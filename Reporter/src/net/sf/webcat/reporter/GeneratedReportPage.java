/*==========================================================================*\
 |  $Id: GeneratedReportPage.java,v 1.5 2008/03/31 00:44:58 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU General Public License as published by
 |  the Free Software Foundation; either version 2 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU General Public License
 |  along with Web-CAT; if not, write to the Free Software
 |  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 |
 |  Project manager: Stephen Edwards <edwards@cs.vt.edu>
 |  Virginia Tech CS Dept, 660 McBryde Hall (0106), Blacksburg, VA 24061 USA
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
 * @author  Anthony Allevato
 * @version $Id: GeneratedReportPage.java,v 1.5 2008/03/31 00:44:58 stedwar2 Exp $
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
    public GeneratedReport cachedGeneratedReport;
    public IRenderingMethod renderingMethod;
    public IRenderingMethod selectedRenderingMethod;
    public MutableDictionary error;


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
    	cachedGeneratedReport = null;

    	super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public ReporterComponent self()
    {
    	return this;
    }


    // ----------------------------------------------------------
    public GeneratedReport generatedReport()
    {
    	if(cachedGeneratedReport == null)
    	{
	    	NSArray<GeneratedReport> reports = GeneratedReport.objectsForUuid(
	    			localContext(), localReportUuid());

	    	if(reports.count() > 0)
	    	{
	    		if(reports.count() != 1)
	    		{
	    			log.warn("There is more than one report with uuid " +
	    					localReportUuid() + "!");
	    		}

	    		cachedGeneratedReport = reports.objectAtIndex(0);
	    	}
    	}

    	return cachedGeneratedReport;
    }


    // ----------------------------------------------------------
    public boolean isReportRendered()
    {
    	GeneratedReport report = generatedReport();

    	return (report != null &&
    			report.isRenderedWithMethod(localRenderingMethod()));
    }


    // ----------------------------------------------------------
    public NSArray<MutableDictionary> generatedReportErrors()
    {
    	if(generatedReport() == null)
    	{
    		return null;
    	}
    	else
    	{
    		return generatedReport().errors();
    	}
    }


    // ----------------------------------------------------------
    public String reportUuid()
    {
    	return localReportUuid();
    }


    // ----------------------------------------------------------
    public String errorSeverity()
    {
    	int severity = (Integer)error.objectForKey("severity");

		switch(severity)
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

		switch(severity)
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
    /**
     * Returns null to force a reload of the current page.
     * @return always null, to refresh the current page
     */
    public WOComponent refreshAction()
    {
        return null;
    }


    // ----------------------------------------------------------
    public Object jobToken()
    {
    	return localReportUuid();
    }


    // ----------------------------------------------------------
    public AjaxLongResponseHandler longResponseHandler()
    {
    	return new AjaxLongResponseHandler()
        {
    		public void cancel()
    		{
    			Reporter.getInstance().reportQueueProcessor()
                    .cancelJobWithUuid(localContext(), localReportUuid());
    		}
    	};
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
    	commitReportRendering();
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
     * @param timeDelta the time to format
     * @return the time format to use
     */
    public static String formatForSmallTime( long timeDelta )
    {
        String format = "%j days, %H:%M:%S";
        final int minute = 60 * 1000;
        final int hour   = 60 * minute;
        final int day    = 24 * hour;
        if ( timeDelta < minute )
        {
            format = "%S seconds";
        }
        else if ( timeDelta < hour )
        {
            format = "%M:%S minutes";
        }
        else if ( timeDelta < day )
        {
            format = "%H:%M:%S hours";
        }
        return format;
    }


    // ----------------------------------------------------------
    /**
     * Returns the date format string for the corresponding time value
     * @return the time format for the estimated job wait
     */
    public String estimatedWaitFormat()
    {
        ensureJobDataIsInitialized();
        return formatForSmallTime( jobData.estimatedWait );
    }


    // ----------------------------------------------------------
    /**
     * Returns the date format string for the corresponding time value
     * @return the time format for the most recent job wait
     */
    public String mostRecentJobWaitFormat()
    {
        ensureJobDataIsInitialized();
        return formatForSmallTime( jobData.mostRecentWait );
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
            NSMutableArray qualifiers = new NSMutableArray();
            qualifiers.addObject( new EOKeyValueQualifier(
                            EnqueuedReportJob.DISCARDED_KEY,
                            EOQualifier.QualifierOperatorEqual,
                            ERXConstant.integerForInt( 0 )
            ) );
            qualifiers.addObject( new EOKeyValueQualifier(
                            EnqueuedReportJob.PAUSED_KEY,
                            EOQualifier.QualifierOperatorEqual,
                            ERXConstant.integerForInt( 0 )
            ) );
            EOFetchSpecification fetchSpec =
                new EOFetchSpecification(
                        EnqueuedReportJob.ENTITY_NAME,
                        new EOAndQualifier( qualifiers ),
                        new NSArray( new Object[]{
                                new EOSortOrdering(
                                        EnqueuedReportJob.QUEUE_TIME_KEY,
                                        EOSortOrdering.CompareAscending
                                    )
                            } )
                    );
            jobData.jobs =
                localContext().objectsWithFetchSpecification(
                    fetchSpec
                );
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
                     == localEnqueuedJob() )
                {
                    jobData.queuePosition = i;
                    break;
                }
            }
            oldQueuePos = jobData.queuePosition;
            if ( jobData.queuePosition == jobData.queueSize )
            {
                log.error( "cannot find job in queue for:"
                           + localEnqueuedJob() );
            }

            //Reporter reporter = Reporter.getInstance();
            jobData.mostRecentWait = 0; //reporter.mostRecentJobWait();
            jobData.estimatedWait = 0; //
                //reporter.estimatedJobTime() * ( jobData.queuePosition + 1 );
        }
    }


    //~ Instance/static variables .............................................

    private JobData jobData;
    private int     oldQueuePos = -1;

    static Logger log = Logger.getLogger( GeneratedReportPage.class );
}