/*==========================================================================*\
 |  $Id: GeneratedReportPage.java,v 1.8 2008/04/15 04:09:22 aallowat Exp $
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

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;
import er.extensions.ERXConstant;
import java.io.File;
import net.sf.webcat.core.MutableDictionary;
import net.sf.webcat.grader.FinalReportPage;
import org.apache.log4j.Logger;
import org.eclipse.birt.core.exception.BirtException;

//-------------------------------------------------------------------------
/**
 * This page displayed a generated report.
 *
 * @author  Tony Allevato
 * @version $Id: GeneratedReportPage.java,v 1.8 2008/04/15 04:09:22 aallowat Exp $
 */
public class GeneratedReportPage
    extends ReporterComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create a new page.
     * @param context The page's context
     */
    public GeneratedReportPage(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    /** The associated refresh interval for this page */
    public int refreshTimeout = 15;
    public GeneratedReport generatedReport;
    public Number reportGenerationJobId;
    public ReporterLongResponseDelegate longResponseDelegate;
    public IRenderingMethod renderingMethod;
    public IRenderingMethod selectedRenderingMethod;
    public MutableDictionary error;


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        EnqueuedReportGenerationJob genJob = localReportGenerationJob();

        if(genJob != null)
        {
            reportGenerationJobId = genJob.id();
        }
        else
        {
            reportGenerationJobId = null;
        }

        // Get the generated report the first time we load the page, if it
        // exists. If it doesn't, then we'll continue trying to update it in
        // the long response delegate.

        generatedReport = localGeneratedReport();

        longResponseDelegate = new Delegate();

        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public ReporterComponent self()
    {
        return this;
    }


    // ----------------------------------------------------------
    public boolean isReportRendered()
    {
        return (generatedReport != null
                && generatedReport.isRenderedWithMethod(localRenderingMethod()));
    }


    // ----------------------------------------------------------
    public boolean reportHasRenderingErrors()
    {
        return (generatedReport != null
                && generatedReport.hasRenderingErrors());
    }


    // ----------------------------------------------------------
    public NSArray<MutableDictionary> generatedReportErrors()
    {
        if (generatedReport == null)
        {
            return null;
        }
        else
        {
            return generatedReport.errors();
        }
    }


    // ----------------------------------------------------------
    public NSArray<MutableDictionary> renderingErrors()
    {
        if (generatedReport == null)
        {
            return null;
        }
        else
        {
            return generatedReport.renderingErrors();
        }
    }


    // ----------------------------------------------------------
    public String errorSeverity()
    {
        int severity = (Integer)error.objectForKey("severity");

        switch (severity)
        {
            case BirtException.OK:
                return "OK";

            case BirtException.INFO:
                return "INFO";

            case BirtException.WARNING:
                return "WARNING";

            case BirtException.ERROR:
                return "ERROR";

            case BirtException.CANCEL:
                return "CANCEL";
        }

        return "ERROR";
    }


    // ----------------------------------------------------------
    public String errorCssClass()
    {
        int severity = (Integer)error.objectForKey("severity");

        switch (severity)
        {
            case BirtException.OK:
            case BirtException.INFO:
            case BirtException.CANCEL:
                return "infoBox";

            case BirtException.WARNING:
                return "warningBox";

            case BirtException.ERROR:
                return "errorBox";
        }
        return "errorBox";
    }


    // ----------------------------------------------------------
    public String errorMessage()
    {
        return (String)error.objectForKey("message");
    }


    // ----------------------------------------------------------
    public String errorCause()
    {
        return (String)error.objectForKey("cause");
    }


    // ----------------------------------------------------------
    public NSArray<IRenderingMethod> renderingMethods()
    {
        return Reporter.getInstance().allRenderingMethods();
    }


    // ----------------------------------------------------------
    public WOComponent rerenderReport()
    {
        setLocalRenderingMethod(selectedRenderingMethod.methodName());
        commitReportRendering(generatedReport);
        return pageWithName(GeneratedReportPage.class.getName());
    }


    // ----------------------------------------------------------
    public int queuedJobCount()
    {
        ensureJobDataIsInitialized();
        return jobData.queueSize;
    }


    // ----------------------------------------------------------
    public int queuePosition()
    {
        ensureJobDataIsInitialized();
        return jobData.queuePosition + 1;
    }


    // ----------------------------------------------------------
    /**
     * Returns the estimated time needed to complete processing this job.
     * @return the most recent job wait
     */
    public NSTimestamp estimatedWait()
    {
        ensureJobDataIsInitialized();
        return new NSTimestamp( jobData.estimatedWait );
    }


    // ----------------------------------------------------------
    /**
     * Returns the time taken to process the most recent job.
     * @return the most recent job wait
     */
    public NSTimestamp mostRecentJobWait()
    {
        ensureJobDataIsInitialized();
        return new NSTimestamp( jobData.mostRecentWait );
    }


    // ----------------------------------------------------------
    /**
     * Returns the date format string for the corresponding time value
     * @return the time format for the estimated job wait
     */
    public String estimatedWaitFormat()
    {
        ensureJobDataIsInitialized();
        return FinalReportPage.formatForSmallTime( jobData.estimatedWait );
    }


    // ----------------------------------------------------------
    /**
     * Returns the date format string for the corresponding time value
     * @return the time format for the most recent job wait
     */
    public String mostRecentJobWaitFormat()
    {
        ensureJobDataIsInitialized();
        return FinalReportPage.formatForSmallTime( jobData.mostRecentWait );
    }


    // ----------------------------------------------------------
    private class Delegate extends ReporterLongResponseDelegate
    {
        // ----------------------------------------------------------
        /**
         * Cache the
         */
        public void longResponseAwakened()
        {
            if (generatedReport == null)
            {
                if (reportGenerationJobId != null)
                {
                    Integer reportId =
                        ReportGenerationTracker.getInstance().reportIdForJobId(
                            reportGenerationJobId.intValue());

                    if(reportId != null)
                    {
                        // The GeneratedReport was created while we have been
                        // observing the progress, so store it for future
                        // updates.

                        generatedReport = GeneratedReport.forId(
                                localContext(), reportId);

                        // The new state will be picked up in the
                        // (generatedReport != null) block below.
                    }
                    else
                    {
                        // There is a generation job, but no report ID has been
                        // registered for it yet in the tracker, so it is still
                        // in the queue.

                        state = STATE_ENQUEUED;
                    }
                }

                // No else clause; if reportGenerationJobId == null, then
                // generatedReport will not be null
            }

            if (generatedReport != null)
            {
                boolean ready =
                    isReportRendered() || reportHasRenderingErrors();

                if (ready)
                {
                    // The report is rendered, so the state is "ready".

                    state = STATE_READY;
                }
                else if (generatedReport.isComplete())
                {
                    NSArray<EnqueuedReportRenderJob> jobs =
                        EnqueuedReportRenderJob.objectsForGeneratedReport(
                                localContext(), generatedReport);

                    if (jobs.count() == 0)
                    {
                        // If the report is complete, it is not "ready", and
                        // there are no rendering jobs currently in the queue
                        // for it, then we commit one now.

                        commitReportRendering(generatedReport);
                    }

                    state = STATE_RENDERING;
                }
                else
                {
                    // The GeneratedReport has been created but it is not yet
                    // complete.

                    state = STATE_GENERATING;
                }
            }
        }


        // ----------------------------------------------------------
        public float fractionOfWorkDone()
        {
            switch (state)
            {
            case STATE_ENQUEUED:
                return 0.00f;

            case STATE_GENERATING:
                ReportGenerationTracker tracker =
                    ReportGenerationTracker.getInstance();

                return tracker.fractionOfWorkDoneForJobId(
                        reportGenerationJobId.intValue()) * GENERATING_FRACTION;

            case STATE_RENDERING:
                return GENERATING_FRACTION;

            case STATE_READY:
                return 1.00f;

            default:
                return 0.00f;
            }
        }


        // ----------------------------------------------------------
        public boolean isDone()
        {
            return (state == STATE_READY);
        }


        // ----------------------------------------------------------
        public String workDescription()
        {
            switch (state)
            {
            case STATE_ENQUEUED:
                return String.format("Your report is currently in the queue "
                        + "and will be processed shortly (queue position %d).",
                        queuePosition());

            case STATE_GENERATING:
                return "Your report is currently being generated.";

            case STATE_RENDERING:
                return "Your report has been generated and is now being rendered.";

            default:
                return "";
            }
        }


        // ----------------------------------------------------------
        public boolean canCancel()
        {
            // Right now we don't permit canceling in the render phase, only in
            // the generation phase. This may change in the future.

            return (state == STATE_ENQUEUED || state == STATE_GENERATING);
        }


        // ----------------------------------------------------------
        public void cancel()
        {
            Reporter.getInstance().reportGenerationQueueProcessor()
                .cancelJobWithId(localContext(), reportGenerationJobId);
        }


        private static final int STATE_ENQUEUED = 0;
        private static final int STATE_GENERATING = 1;
        private static final int STATE_RENDERING = 2;
        private static final int STATE_READY = 3;

        private static final float GENERATING_FRACTION = 0.95f;

        private int state;
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    static private class JobData
    {
        public NSArray jobs;
        public int queueSize;
        public int queuePosition;
        long mostRecentWait;
        long estimatedWait;
    }


    // ----------------------------------------------------------
    private void ensureJobDataIsInitialized()
    {
        if ( jobData == null )
        {
            jobData = new JobData();
            EOFetchSpecification fetchSpec =
                new EOFetchSpecification(
                    EnqueuedReportGenerationJob.ENTITY_NAME,
                    null,
                    new NSArray( new Object[]{
                        new EOSortOrdering(
                            EnqueuedReportGenerationJob.QUEUE_TIME_KEY,
                            EOSortOrdering.CompareAscending
                        )
                    } )
                );
            jobData.jobs =
                localContext().objectsWithFetchSpecification(fetchSpec);
            jobData.queueSize = jobData.jobs.count();
            if ( oldQueuePos < 0
                 || oldQueuePos >= jobData.queueSize )
            {
                oldQueuePos = jobData.queueSize - 1;
            }
            jobData.queuePosition = jobData.queueSize;
            for ( int i = oldQueuePos; i >= 0; i-- )
            {
                if ( jobData.jobs.objectAtIndex( i )
                     == localReportGenerationJob() )
                {
                    jobData.queuePosition = i;
                    break;
                }
            }
            oldQueuePos = jobData.queuePosition;
            if ( jobData.queuePosition == jobData.queueSize )
            {
                log.error("cannot find job in queue for:"
                        + localReportGenerationJob());
            }

            // Reporter reporter = Reporter.getInstance();
            jobData.mostRecentWait = 0; // reporter.mostRecentJobWait();
            jobData.estimatedWait = 0;
            // reporter.estimatedJobTime() * ( jobData.queuePosition + 1 );
        }
    }


    //~ Instance/static variables .............................................

    private JobData jobData;
    private int     oldQueuePos = -1;

    static Logger log = Logger.getLogger( GeneratedReportPage.class );
}