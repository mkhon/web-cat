/*==========================================================================*\
 |  $Id: ReportRenderQueueProcessor.java,v 1.3 2009/05/27 14:31:52 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2008 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU Affero General Public License as published
 |  by the Free Software Foundation; either version 3 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU Affero General Public License
 |  along with Web-CAT; if not, see <http://www.gnu.org/licenses/>.
\*==========================================================================*/

package net.sf.webcat.reporter;

import java.io.File;
import java.util.Vector;
import org.apache.log4j.Logger;
import net.sf.webcat.archives.FileUtilities;
import net.sf.webcat.core.Application;
import net.sf.webcat.core.MutableArray;
import net.sf.webcat.core.MutableDictionary;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;
import er.extensions.appserver.ERXApplication;

// -------------------------------------------------------------------------
/**
 * This is the main report processor class that performs the
 * generation/rendering/storing cycle on a report job. It is based on the
 * GraderQueueProcessor from the Grader subsystem.
 *
 * @author Tony Allevato
 * @version $Id: ReportRenderQueueProcessor.java,v 1.3 2009/05/27 14:31:52 aallowat Exp $
 */
public class ReportRenderQueueProcessor extends Thread
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Default constructor
     *
     * @param queue
     *            the queue to operate on
     */
    public ReportRenderQueueProcessor(ReportRenderQueue queue)
    {
        this.queue = queue;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * The actual thread of execution
     */
    public void run()
    {
        // Find all rendering jobs
        EOFetchSpecification fetchNewJobs = new EOFetchSpecification(
                EnqueuedReportRenderJob.ENTITY_NAME, null, new NSArray(
                        new Object[] { new EOSortOrdering(
                                EnqueuedReportRenderJob.QUEUE_TIME_KEY,
                                EOSortOrdering.CompareAscending) }));

        try
        {
            while (true)
            {
                if (editingContext != null)
                {
                    editingContext.unlock();
                    Application.releasePeerEditingContext(editingContext);
                }

                editingContext = Application.newPeerEditingContext();
                editingContext.lock();

                // Clear discarded jobs
                NSArray<EnqueuedReportRenderJob> jobList = null;

                // Get a job
                log.debug("waiting for a token");
                // We don't need the return value, since it is just null:
                queue.getJobToken();
                log.debug("token received.");

                try
                {
                    jobList = editingContext
                            .objectsWithFetchSpecification(fetchNewJobs);
                }
                catch (Exception e)
                {
                    log.info("error fetching jobs: ", e);
                    jobList = editingContext
                            .objectsWithFetchSpecification(fetchNewJobs);
                }

                if (log.isDebugEnabled())
                {
                    log.debug(""
                            + (jobList == null ? "<null>" : ""
                                    + jobList.count())
                            + " fresh jobs retrieved");
                }

                // This test is just to make sure the compiler knows it
                // isn't null, even though the try/catch above ensures it
                if (jobList != null)
                {
                    for (int i = 0; i < jobList.count(); i++)
                    {
                        EnqueuedReportRenderJob job = jobList.objectAtIndex(i);

                        GeneratedReport generatedReport = job.generatedReport();

                        if (generatedReport == null)
                        {
                            log.error("null generated report in "
                                    + "enqueued render job: deleting");

                            editingContext.deleteObject(job);
                            editingContext.saveChanges();
                        }
                        else
                        {
                            {
                                processRenderJobWithProtection(job);

                                // ProgressManager.getInstance().forceJobComplete(
                                // job.progressManagerToken());
                            }

                            // report template could have changed because
                            // of a fault, so save any changes before
                            // forcing it out of editing context cache
                            editingContext.saveChanges();
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.fatal("Job queue processing halted.\n"
                    + "Exception rendering generated report ", e);
            Application.emailExceptionToAdmins(e, null,
                    "Job queue processing halted.");
            log.fatal("Aborting: job queue processing halted.");
            ERXApplication.erxApplication().killInstance();
        }
    }


    // ----------------------------------------------------------
    /**
     * This function processes the job and performs the stages that are
     * necessary. It guards against any exceptions while processing the job.
     *
     * @param job
     *            the job to process
     */
    private void processRenderJobWithProtection(EnqueuedReportRenderJob job)
    {
        try
        {
            processRenderJob(job);
        }
        catch (Exception e)
        {
            technicalFault(job, "while processing job", e);
        }
    }


    // ----------------------------------------------------------
    /**
     * This function processes the job and performs the stages that are
     * necessary.
     *
     * @param job
     *            the job to process
     */
    private void processRenderJob(EnqueuedReportRenderJob job)
    {
        jobCount++;

        log.info("Rendering report " + jobCount + " for: "
                + job.user().userName());

        boolean wasCanceled = false;

        GeneratedReport report = job.generatedReport();

        if (!report.isRenderedWithMethod(job.renderingMethod()))
        {
            // Set up the rendered resources directory first
            try
            {
                prepareRenderOutputDirectory(report);
            }
            catch (Exception e)
            {
                technicalFault(
                        job,
                        "while preparing the report rendered resources directory",
                        e);

                return;
            }

            // Render the report.
            try
            {
                wasCanceled = renderReportDocument(job, report);
            }
            catch (Exception e)
            {
                writeErrorToRenderDirectory(report, e);
            }

            if (wasCanceled)
            {
                return;
            }
        }

        editingContext.deleteObject(job);
        editingContext.saveChanges();
    }


    // ----------------------------------------------------------
    private void writeErrorToRenderDirectory(GeneratedReport report, Exception e)
    {
        MutableDictionary errorInfo =
            ReportExceptionTranslator.translateException(e);

        MutableArray errors = new MutableArray();
        errors.addObject(errorInfo);

        report.setRenderingErrors(errors);
    }


    // ----------------------------------------------------------
    private void prepareRenderOutputDirectory(GeneratedReport report)
    {
        File workingDir = new File(report.renderedResourcesDir());

        if (workingDir.exists())
        {
            // If the directory already exists, delete it and its contents.

            FileUtilities.deleteDirectory(workingDir);
        }

        workingDir.mkdirs();
    }


    // ----------------------------------------------------------
    private boolean renderReportDocument(EnqueuedReportRenderJob job,
            GeneratedReport report)
        throws Exception
    {
        RenderReportThread renderThread = new RenderReportThread(
                job.id(), report.id());

        try
        {
            renderThread.start();
            renderThread.join();
        }
        catch ( InterruptedException e )
        {
            // Nothing to do
        }

        if ( renderThread.exception != null )
        {
            throw renderThread.exception;
        }

        return renderThread.wasCanceled;
    }


    // ----------------------------------------------------------
    /**
     * Handles a technical fault.
     *
     * @param job
     *            the job which faulted
     */
    void technicalFault(EnqueuedReportRenderJob job, String stage, Exception e)
    {
        String errorMsg = "An " + ((e == null) ? "error" : "exception")
                + " occurred " + stage;
        if (e != null)
        {
            errorMsg += ":\n" + e;
        }

        errorMsg += "\n\nGeneration of reports from this template has "
                + "been suspended.\n";
        String subject = "[Reporter] Report generation error: "
                + job.user().userName();
        log.info("technicalFault(): " + subject);
        log.info(errorMsg, e);
        NSMutableArray users = new NSMutableArray();
        users.addObject(job.user());
        Application.sendAdminEmail(null, users, true, subject, errorMsg, null);
    }


    // ----------------------------------------------------------
    public class RenderReportThread extends Thread
    {
        //~ Constructor .......................................................

        // ----------------------------------------------------------
        public RenderReportThread(Number jobId, Number reportId)
        {
            this.jobId = jobId.intValue();
            this.reportId = reportId.intValue();
        }


        //~ Methods ...........................................................

        // ----------------------------------------------------------
        public void run()
        {
            EOEditingContext context = Application.newPeerEditingContext();

            EnqueuedReportRenderJob job =
                EnqueuedReportRenderJob.forId(context, jobId);
            GeneratedReport report =
                GeneratedReport.forId(context, reportId);

            IRenderingMethod method =
                Reporter.getInstance().renderingMethodWithName(
                    job.renderingMethod());

            if (method != null)
            {
                try
                {
                    NSMutableDictionary options = new NSMutableDictionary();
                    options.setObjectForKey(job.renderedResourceActionUrl(),
                        IRenderingMethod.OPTION_ACTION_URL);
                    controller = method.prepareToRender(report, options);

                    controller.render();
                    controller = null;
                    report.markAsRenderedWithMethod(job.renderingMethod());
                }
                catch (Exception e)
                {
                    log.error("Exception rendering report: "
                        + report.description()
                        + "(id: " + report.id() + ")", e);
                    exception = e;
                }
            }

            Application.releasePeerEditingContext(context);
        }


        // ----------------------------------------------------------
        public void interrupt()
        {
            if (controller != null)
            {
                controller.cancel();
                wasCanceled = true;

                log.info("Reporter job with id " + jobId
                        + " (for report id " + reportId + " )"
                        + " canceled during rendering stage");
            }

            super.interrupt();
        }


        //~ Instance/static variables .........................................

        public boolean wasCanceled = false;
        private int jobId;
        private int reportId;
        private IRenderingMethod.Controller controller;
        public Exception exception = null;
    }


    //~ Instance/static variables .............................................

    /** The queue to receive processing tokens. */
    private ReportRenderQueue queue;

    private EOEditingContext editingContext;

    private int jobCount = 0;

    static Logger log = Logger.getLogger(ReportRenderQueueProcessor.class);
}
