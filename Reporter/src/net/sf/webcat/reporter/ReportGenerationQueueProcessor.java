/*==========================================================================*\
 |  $Id: ReportGenerationQueueProcessor.java,v 1.2 2008/05/28 05:48:11 stedwar2 Exp $
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

import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;
import er.extensions.ERXApplication;
import er.extensions.ERXConstant;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import net.sf.webcat.archives.FileUtilities;
import net.sf.webcat.core.Application;
import net.sf.webcat.core.DirectAction;
import net.sf.webcat.core.MutableArray;
import net.sf.webcat.core.MutableDictionary;
import org.apache.log4j.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IEngineTask;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunTask;

//-------------------------------------------------------------------------
/**
 * This is the main report processor class that performs the
 * generation/rendering/storing cycle on a report job. It is based on the
 * GraderQueueProcessor from the Grader subsystem.
 *
 * @author Tony Allevato
 * @version $Id: ReportGenerationQueueProcessor.java,v 1.2 2008/05/28 05:48:11 stedwar2 Exp $
 */
public class ReportGenerationQueueProcessor extends Thread
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Default constructor
     *
     * @param queue
     *            the queue to operate on
     */
    public ReportGenerationQueueProcessor(ReportGenerationQueue queue)
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
        // Find all jobs that are not paused
        EOFetchSpecification fetchNewJobs = new EOFetchSpecification(
                EnqueuedReportGenerationJob.ENTITY_NAME, null, new NSArray(
                        new Object[] { new EOSortOrdering(
                                EnqueuedReportGenerationJob.QUEUE_TIME_KEY,
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
                NSArray<EnqueuedReportGenerationJob> jobList = null;

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
                        NSTimestamp startProcessing = new NSTimestamp();

                        EnqueuedReportGenerationJob job =
                            jobList.objectAtIndex(i);

                        processReportJobWithProtection(job);

                        NSTimestamp now = new NSTimestamp();
                        if (job.queueTime() != null)
                        {
                            mostRecentJobWait = now.getTime()
                                    - job.queueTime().getTime();
                        }

                        long processingTime = now.getTime()
                                - startProcessing.getTime();
                        totalWaitForJobs += processingTime;
                        jobsCountedWithWaits++;

                        // report template could have changed because
                        // of a fault, so save any changes before
                        // forcing it out of editing context cache
                        editingContext.saveChanges();
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.fatal("Job queue processing halted.\n"
                    + "Exception processing student submission", e);
            Application.emailExceptionToAdmins(e, null,
                    "Job queue processing halted.");
            log.fatal("Aborting: job queue processing halted.");
            ERXApplication.erxApplication().killInstance();
        }
    }


    // ----------------------------------------------------------
    private int countDataSetRefs(ReportTemplate template)
    {
        NSArray<ReportDataSet> dataSets = template.dataSets();
        int dataSetRefs = 0;

        for (ReportDataSet dataSet : dataSets)
        {
            dataSetRefs += dataSet.referenceCount();
        }

        return dataSetRefs;
    }


    // ----------------------------------------------------------
    /**
     * This function processes the job and performs the stages that are
     * necessary. It guards against any exceptions while processing the job.
     *
     * @param job
     *            the job to process
     */
    private void processReportJobWithProtection(EnqueuedReportGenerationJob job)
    {
        try
        {
            processReportJob(job);
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
    private void processReportJob(EnqueuedReportGenerationJob job)
    {
        jobCount++;
        log.info("Processing report " + jobCount + " for: "
                + job.user().userName() + " (template: "
                + job.reportTemplate().name() + ")");

        boolean wasCanceled = false;

        GeneratedReport report = new GeneratedReport();
        editingContext.insertObject(report);

        report.setDescription(job.description());
        report.setUserRelationship(job.user());
        report.setReportTemplateRelationship(job.reportTemplate());

        editingContext.saveChanges();

        int dataSetRefs = countDataSetRefs(job.reportTemplate());

        // Register the GeneratedReport object with the tracker so that the
        // progress page can observe it.
        ReportGenerationTracker.getInstance().startReportForJobId(
                job.id().intValue(), report.id().intValue(), dataSetRefs);

        // Set up the report document directory first
        try
        {
            prepareReportOutputDirectory(report);
        }
        catch (Exception e)
        {
            technicalFault(job, "while preparing the report storage directory",
                    e);
            return;
        }

        MutableArray exceptions = null;

        try
        {
            wasCanceled = generateReportDocument(job, report);
        }
        catch (ReportGenerationException e)
        {
            exceptions = new MutableArray();

            for (Exception ex : e.errors())
            {
                log.error("Error generating report:", ex);
                exceptions.addObject(ex);
            }
        }

        if (wasCanceled)
        {
            return;
        }

        MutableArray errors = ReportExceptionTranslator.translateExceptions(
                exceptions);

        report.setGeneratedTime(new NSTimestamp());
        report.setErrors(errors);
        report.setIsComplete(true);

        editingContext.saveChanges();

        // Associate the data set queries with the generated report now
        // instead of the enqueued job. We have to do this copy weirdness
        // because the NSArray returned by job.dataSetQueries() is really a
        // proxy object that represents the current state of
        // the relationship, which is being modified in the loop.

        NSMutableArray<ReportDataSetQuery> dsqCopy =
            new NSMutableArray<ReportDataSetQuery>();

        NSArray<ReportDataSetQuery> dataSetQueries = job.dataSetQueries();

        for (ReportDataSetQuery dataSetQuery : dataSetQueries)
        {
            dsqCopy.addObject(dataSetQuery);
        }

        for (ReportDataSetQuery dataSetQuery : dsqCopy)
        {
            dataSetQuery.setEnqueuedReportJobRelationship(null);
            dataSetQuery.setGeneratedReportRelationship(report);
        }

        editingContext.deleteObject(job);
        editingContext.saveChanges();
    }


    // ----------------------------------------------------------
    public void cancelJobWithId(EOEditingContext context, Number id)
    {
        int idInt = id.intValue();

        if (currentlyRunningThread != null
                && currentlyRunningThread.jobId() == idInt)
        {
            currentlyRunningThread.interrupt();
        }

        // Delete the GeneratedReport (and accompanying file) if it was already
        // created by this point.

        Integer reportId = ReportGenerationTracker.getInstance()
                .reportIdForJobId(idInt);

        if (reportId != null)
        {
            GeneratedReport report = GeneratedReport.forId(context, idInt);
            context.deleteObject(report);
            context.saveChanges();

            ReportGenerationTracker.getInstance().removeReportIdForJobId(idInt);
        }
    }


    // ----------------------------------------------------------
    private boolean generateReportDocument(EnqueuedReportGenerationJob job,
            GeneratedReport report) throws ReportGenerationException
    {
        GenerateReportThread exeThread = new GenerateReportThread(job.id(),
                report.id());

        try
        {
            currentlyRunningThread = exeThread;
            exeThread.start();
            exeThread.join();
            currentlyRunningThread = null;
        }
        catch (InterruptedException e)
        {
            // Nothing to do
        }

        if (exeThread.generationErrors() != null)
        {
            throw new ReportGenerationException(exeThread.generationErrors());
        }

        return exeThread.wasCanceled;
    }


    // ----------------------------------------------------------
    private class ReportGenerationException extends Exception
    {
        //~ Constructor .......................................................

        // ----------------------------------------------------------
        public ReportGenerationException(List<Exception> errors)
        {
            this.errors = errors;
        }


        //~ Methods ...........................................................

        // ----------------------------------------------------------
        public List<Exception> errors()
        {
            return errors;
        }


        // ----------------------------------------------------------
        public String toString()
        {
            StringBuilder message = new StringBuilder();

            for (int i = 0; i < errors.size(); i++)
            {
                message.append("Error ");
                message.append(i);
                message.append(":\n");
                message.append(((Throwable) errors.get(i)).getCause()
                        .toString());
                message.append("\n\n");
            }

            return message.toString();
        }


        //~ Instance/static variables .........................................

        private List<Exception> errors;
    }


    // ----------------------------------------------------------
    private class GenerateReportThread extends Thread
    {
        //~ Constructor .......................................................

        // ----------------------------------------------------------
        public GenerateReportThread(Number jobId, Number reportId)
        {
            this.jobId = jobId.intValue();
            this.reportId = reportId.intValue();
        }


        //~ Methods ...........................................................

        // ----------------------------------------------------------
        public void run()
        {
            EOEditingContext context = Application.newPeerEditingContext();

            EnqueuedReportGenerationJob job = EnqueuedReportGenerationJob
                    .forId(context, jobId);
            GeneratedReport report = GeneratedReport.forId(context, reportId);

            String reportPath = report.generatedReportFile();

            org.mozilla.javascript.Context.enter();

            try
            {
                runTask = Reporter.getInstance().setupRunTaskForJob(job);
                runTask.setErrorHandlingOption(IEngineTask.CANCEL_ON_ERROR);

                runTask.run(reportPath);

                generationErrors = runTask.getErrors();
                if (generationErrors.isEmpty())
                {
                    generationErrors = null;
                }

                runTask.close();
                runTask = null;
            }
            catch (EngineException e)
            {
                // Error creating process, so record it
                log.error("Exception generating " + reportPath, e);

                if (generationErrors == null)
                {
                    generationErrors = new ArrayList<Exception>();
                }

                generationErrors.add(0, e);
            }

            org.mozilla.javascript.Context.exit();
            Application.releasePeerEditingContext(context);
        }


        // ----------------------------------------------------------
        public void interrupt()
        {
            if (runTask != null)
            {
                runTask.cancel();
                wasCanceled = true;

                log.info("Reporter job with id " + jobId
                        + " canceled during generation stage");
            }

            super.interrupt();
        }


        // ----------------------------------------------------------
        public List<Exception> generationErrors()
        {
            return generationErrors;
        }


        // ----------------------------------------------------------
        public int jobId()
        {
            return jobId;
        }


        //~ Instance/static variables .........................................

        public boolean wasCanceled = false;
        private int jobId;
        private int reportId;
        private IRunTask runTask;
        private List<Exception> generationErrors = null;
    }


    // ----------------------------------------------------------
    /**
     * Creates and cleans the working directory, if necessary, fills it with the
     * student's submission, and creates the reporting directory.
     *
     * @param job
     *            the job to operate on
     * @throws Exception
     *             if it occurs during this stage
     */
    private void prepareReportOutputDirectory(GeneratedReport report)
    {
        // Create the working compilation directory for the user
        File workingDir = new File(report.generatedReportDir());

        if (!workingDir.exists())
        {
            workingDir.mkdirs();
        }
    }


    // ----------------------------------------------------------
    /**
     * Handles a technical fault Suspends grading of other submissions for the
     * same assignment
     *
     * @param job
     *            the job which faulted
     */
    void technicalFault(EnqueuedReportGenerationJob job, String stage,
            Exception e)
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
    /**
     * Find out how many grading jobs have been processed so far.
     *
     * @return the number of jobs process so far
     */
    public int processedJobCount()
    {
        return jobCount;
    }


    // ----------------------------------------------------------
    /**
     * Find out the processing delay for the most recently completed job.
     *
     * @return the time in milliseconds
     */
    public long mostRecentJobWait()
    {
        return mostRecentJobWait;
    }


    // ----------------------------------------------------------
    /**
     * Find out the estimated processing delay for any job.
     *
     * @return the time in milliseconds
     */
    public long estimatedJobTime()
    {
        if (jobsCountedWithWaits > 0)
        {
            return totalWaitForJobs / jobsCountedWithWaits;
        }
        else
        {
            return DEFAULT_JOB_WAIT;
        }
    }


    //~ Instance/static variables .............................................

    /** The queue to receive processing tokens. */
    private ReportGenerationQueue queue;

    /** Number of jobs processed so far, to report administrative status. */
    private int jobCount = 0;
    private int jobsCountedWithWaits = 0;

    /** Time between submission and grading completion for more recent job. */
    private long mostRecentJobWait = 0;
    private long totalWaitForJobs = 0;
    private static final long DEFAULT_JOB_WAIT = 30000;

    static final int defaultTimeout = net.sf.webcat.core.Application
            .configurationProperties().intForKeyWithDefault(
                    "reporter.timeout.default", 300);

    private EOEditingContext editingContext;

    private GenerateReportThread currentlyRunningThread;

    static Logger log = Logger.getLogger(ReportGenerationQueueProcessor.class);
}
