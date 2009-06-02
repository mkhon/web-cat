/*==========================================================================*\
 |  $Id: GeneratedReportPage.java,v 1.11 2009/06/02 19:59:12 aallowat Exp $
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
import er.extensions.eof.ERXConstant;
import java.io.File;
import java.util.List;
import java.util.Properties;
import net.sf.webcat.core.DeliverFile;
import net.sf.webcat.core.MutableArray;
import net.sf.webcat.core.MutableDictionary;
import net.sf.webcat.grader.FinalReportPage;
import org.apache.log4j.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IDataExtractionTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IResultSetItem;
import org.json.JSONException;
import org.json.JSONObject;

//-------------------------------------------------------------------------
/**
 * This page displayed a generated report.
 *
 * @author  Tony Allevato
 * @version $Id: GeneratedReportPage.java,v 1.11 2009/06/02 19:59:12 aallowat Exp $
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
    public int currentPageNumber = 0;
    public NSArray<String> resultSetsToExtract;
    public String resultSet;
    public int resultSetIndex;

    
    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        EnqueuedReportGenerationJob genJob = localReportGenerationJob();

        if (genJob != null)
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

        if (generatedReport != null)
        {
            // If the report has already been generated, start with page 1
            // instead of 0 (which is a placeholder that indicates nothing is
            // ready yet).
            currentPageNumber = 1;
        }

        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public boolean isReportComplete()
    {
        if (generatedReport == null)
        {
            return false;
        }
        else
        {
            Properties props = generatedReport.renderingProperties();
            return Boolean.valueOf(props.getProperty("isComplete", "false"));
        }
    }

    
    // ----------------------------------------------------------
    public String mainBlockTitle()
    {
        String prefix = "Your Report";

        if (generatedReport == null)
        {
            if (reportGenerationJobId != null)
            {
                EnqueuedReportGenerationJob job =
                    EnqueuedReportGenerationJob.forId(localContext(),
                        reportGenerationJobId.intValue());
                
                if (job != null)
                {
                    return prefix + ": " + job.description();
                }
            }

            return prefix;
        }
        else
        {
            return prefix + ": " + generatedReport.description();
        }
    }


    // ----------------------------------------------------------
    public void setCurrentPageNumber(int pageNum)
    {
        currentPageNumber = pageNum;
    }
    

    // ----------------------------------------------------------
    public int highestPageSoFar()
    {
        if (generatedReport == null)
        {
            return 0;
        }
        else
        {
            Properties props = generatedReport.renderingProperties();
            return Integer.valueOf(props.getProperty(
                    "highestRenderedPageNumber", "0"));
        }
    }

    
    // ----------------------------------------------------------
    public synchronized JSONObject pollReportStatus()
    {
        JSONObject result = new JSONObject();
        
        try
        {
            if (generatedReport == null && reportGenerationJobId != null)
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
                    result.put("isStarted", false);
                    result.put("queuePosition", queuePosition());
                }
            }

            if (generatedReport != null)
            {
                Properties props = generatedReport.renderingProperties();

                int highestPage = Integer.valueOf(props.getProperty(
                        "highestRenderedPageNumber", "0"));

                if (currentPageNumber == 0 && highestPage > 0)
                {
                    // Kick off the first page once it's ready.
                    currentPageNumber = 1;
                }

                result.put("isStarted", true);
                result.put("highestRenderedPageNumber", highestPage);

                result.put("isComplete",
                        Boolean.valueOf(props.getProperty(
                                "isComplete", "false")));

                if (reportGenerationJobId == null)
                {
                    result.put("progress", 100);
                }
                else
                {
                    ReportGenerationTracker tracker =
                        ReportGenerationTracker.getInstance();

                    int progress = (int) (tracker.fractionOfWorkDoneForJobId(
                            reportGenerationJobId.intValue()) * 100 + 0.5);
                    result.put("progress", progress);
                }
            }
        }
        catch (JSONException e)
        {
            // Do nothing.
        }

        return result;
    }


    // ----------------------------------------------------------
    public String initialProgress()
    {
        if (reportGenerationJobId == null)
        {
            return "0%";
        }
        else
        {
            ReportGenerationTracker tracker =
                ReportGenerationTracker.getInstance();
            int progress = (int) (tracker.fractionOfWorkDoneForJobId(
                    reportGenerationJobId.intValue()) * 100 + 0.5);
            
            return "" + progress + "%";
        }
    }
    
    
    // ----------------------------------------------------------
    public NSArray<?> resultSetsToExtract()
    {
        if (resultSetsToExtract == null)
        {
            NSMutableArray<String> resultSets = new NSMutableArray<String>();

            IReportDocument document = generatedReport.openReportDocument();
            IDataExtractionTask task =
                Reporter.getInstance().createDataExtractionTask(document);

            try
            {
                List<IResultSetItem> list = task.getResultSetList();

                for (IResultSetItem item : list)
                {
                    resultSets.addObject(item.getResultSetName());
                }
            }
            catch (EngineException e)
            {
                log.error("There was an error reading the result sets to be "
                        + "extracted from the report:", e);
            }

            document.close();

            resultSetsToExtract = resultSets;
        }
        
        return resultSetsToExtract;
    }


    // ----------------------------------------------------------
    public void cancelReport()
    {
        if (reportGenerationJobId != null)
        {
            Reporter.getInstance().reportGenerationQueueProcessor()
                .cancelJobWithId(localContext(), reportGenerationJobId);
        }
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
    private WOActionResults saveWithSaver(AbstractReportSaver saver)
    {
        DeliverFile file = (DeliverFile) pageWithName(
                DeliverFile.class.getName());

        Throwable error = saver.deliverTo(file);
        
        if (error != null)
        {
            ReportDownloadErrorPage page =
                (ReportDownloadErrorPage) pageWithName(
                    ReportDownloadErrorPage.class.getName());
            page.throwable = error;
            page.generatedReport = generatedReport;
            return page;
        }
        else
        {
            return file;
        }
    }
    
    
    // ----------------------------------------------------------
    public WOActionResults savePDF()
    {
        PDFReportSaver saver = new PDFReportSaver(generatedReport);
        return saveWithSaver(saver);
    }
    
    
    // ----------------------------------------------------------
    public WOActionResults saveExcel()
    {
        ExcelReportSaver saver = new ExcelReportSaver(generatedReport);
        return saveWithSaver(saver);
    }
    
    
    // ----------------------------------------------------------
    public WOActionResults saveZippedHTML()
    {
        HTMLReportSaver saver = new HTMLReportSaver(generatedReport);
        return saveWithSaver(saver);
    }
    
    
    // ----------------------------------------------------------
    public WOActionResults saveZippedCSV()
    {
        CSVReportSaver saver = new CSVReportSaver(generatedReport, null);
        return saveWithSaver(saver);
    }
    
    
    // ----------------------------------------------------------
    public WOActionResults saveOneCSV()
    {
        CSVReportSaver saver = new CSVReportSaver(generatedReport, resultSet);
        return saveWithSaver(saver);
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