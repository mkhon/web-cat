/*==========================================================================*\
 |  $Id: ReporterComponent.java,v 1.7 2008/04/02 01:36:38 stedwar2 Exp $
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

import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;
import er.extensions.ERXConstant;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;
import net.sf.webcat.core.Application;
import net.sf.webcat.core.MutableDictionary;
import net.sf.webcat.core.User;
import net.sf.webcat.core.WCComponent;
import net.sf.webcat.grader.FinalReportPage;

import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * A base class for pages in the Reporter subsystem.
 *
 * @author Tony Allevato
 * @version $Id: ReporterComponent.java,v 1.7 2008/04/02 01:36:38 stedwar2 Exp $
 */
public class ReporterComponent
    extends WCComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new ReporterComponent object.
     * @param context The context to use
     */
    public ReporterComponent( WOContext context )
    {
        super( context );
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public void clearLocalReportState()
    {
    	transientState().removeObjectForKey(KEY_REPORT_DESCRIPTION);
    	transientState().removeObjectForKey(KEY_REPORT_UUID);
    	transientState().removeObjectForKey(KEY_ENQUEUED_JOB);
    	transientState().removeObjectForKey(KEY_REPORT_TEMPLATE);
    	transientState().removeObjectForKey(KEY_GENERATED_REPORT);
    	transientState().removeObjectForKey(KEY_RENDERING_METHOD);
    	transientState().removeObjectForKey(KEY_CURRENT_DATASET);
    	transientState().removeObjectForKey(KEY_QUERIES);
    	transientState().removeObjectForKey(KEY_PAGE_CONTROLLER);
    }


    // ----------------------------------------------------------
    public String localReportDescription()
    {
    	return (String)transientState().objectForKey(KEY_REPORT_DESCRIPTION);
    }


    // ----------------------------------------------------------
    public void setLocalReportDescription(String value)
    {
    	transientState().setObjectForKey(value, KEY_REPORT_DESCRIPTION);
    }


    // ----------------------------------------------------------
    public String localReportUuid()
    {
    	return (String)transientState().objectForKey(KEY_REPORT_UUID);
    }


    // ----------------------------------------------------------
    public void setLocalReportUuid(String value)
    {
    	transientState().setObjectForKey(value, KEY_REPORT_UUID);
    }


    // ----------------------------------------------------------
    public EnqueuedReportJob localEnqueuedJob()
    {
    	return (EnqueuedReportJob)transientState().objectForKey(
    	    KEY_ENQUEUED_JOB);
    }


    // ----------------------------------------------------------
    public void setLocalEnqueuedJob(EnqueuedReportJob value)
    {
    	transientState().setObjectForKey(value, KEY_ENQUEUED_JOB);
    }


    // ----------------------------------------------------------
    public ReportTemplate localReportTemplate()
    {
    	return (ReportTemplate)transientState().objectForKey(
    			KEY_REPORT_TEMPLATE);
    }


    // ----------------------------------------------------------
    public void setLocalReportTemplate(ReportTemplate value)
    {
    	transientState().setObjectForKey(value, KEY_REPORT_TEMPLATE);
    }


    // ----------------------------------------------------------
    public int localCurrentReportDataSet()
    {
    	return (Integer)transientState().objectForKey(KEY_CURRENT_DATASET);
    }


    // ----------------------------------------------------------
    public void setLocalCurrentReportDataSet(int value)
    {
    	transientState().setObjectForKey(value, KEY_CURRENT_DATASET);
    }


    // ----------------------------------------------------------
    public GeneratedReport localGeneratedReport()
    {
    	return (GeneratedReport)transientState().objectForKey(
    	    KEY_GENERATED_REPORT);
    }


    // ----------------------------------------------------------
    public void setLocalGeneratedReport(GeneratedReport value)
    {
    	transientState().setObjectForKey(value, KEY_GENERATED_REPORT);
    }


    // ----------------------------------------------------------
    public String localRenderingMethod()
    {
    	return (String)transientState().objectForKey(KEY_RENDERING_METHOD);
    }


    // ----------------------------------------------------------
    public void setLocalRenderingMethod(String value)
    {
    	transientState().setObjectForKey(value, KEY_RENDERING_METHOD);
    }


    // ----------------------------------------------------------
    public void createLocalPageController()
    {
    	QueryPageController controller = new QueryPageController(
            this, localReportTemplate().dataSets());

    	transientState().setObjectForKey(controller, KEY_PAGE_CONTROLLER);
    }


    // ----------------------------------------------------------
    public QueryPageController localPageController()
    {
    	return (QueryPageController)transientState().objectForKey(
    	    KEY_PAGE_CONTROLLER);
    }


    // ----------------------------------------------------------
    public String componentForLocalDataSetUuid(String uuid)
    {
    	NSDictionary<String, NSDictionary<String, Object>> queryMap =
    		(NSDictionary<String, NSDictionary<String, Object>>)
    		transientState().objectForKey(KEY_QUERIES);

    	if (queryMap == null)
        {
    		return null;
        }

    	NSDictionary<String, Object> queryInfo = queryMap.objectForKey(uuid);

    	if (queryInfo == null)
        {
    		return null;
        }

    	return (String)queryInfo.objectForKey("componentName");
    }


    // ----------------------------------------------------------
    public void setComponentForLocalDataSetUuid(String value, String uuid)
    {
    	NSMutableDictionary<String, NSMutableDictionary<String, Object>>
            queryMap =
    		(NSMutableDictionary<String, NSMutableDictionary<String, Object>>)
    		transientState().objectForKey(KEY_QUERIES);

    	if (queryMap == null)
    	{
    		queryMap = new NSMutableDictionary<String,
    			NSMutableDictionary<String, Object>>();
    		transientState().setObjectForKey(queryMap, KEY_QUERIES);
    	}

    	NSMutableDictionary<String, Object> queryInfo =
    		queryMap.objectForKey(uuid);

    	if (queryInfo == null)
    	{
    		queryInfo = new NSMutableDictionary<String, Object>();
    		queryMap.setObjectForKey(queryInfo, uuid);
    	}

   		queryInfo.setObjectForKey(value, "componentName");
    }


    // ----------------------------------------------------------
    public NSArray<String> localDataSetUuids()
    {
    	NSDictionary<String, NSDictionary<String, Object>> queryMap =
    		(NSDictionary<String, NSDictionary<String, Object>>)
    		transientState().objectForKey(KEY_QUERIES);

    	if (queryMap == null)
        {
    		return NSArray.EmptyArray;
        }
    	else
        {
    		return queryMap.allKeys();
        }
    }


    // ----------------------------------------------------------
    public ReportQuery queryForLocalDataSetUuid(String uuid)
    {
    	NSDictionary<String, NSDictionary<String, Object>> queryMap =
    		(NSDictionary<String, NSDictionary<String, Object>>)
    		transientState().objectForKey(KEY_QUERIES);

    	if  (queryMap == null)
        {
    		return null;
        }

    	NSDictionary<String, Object> queryInfo = queryMap.objectForKey(uuid);

    	if (queryInfo == null)
        {
    		return null;
        }

    	return (ReportQuery)queryInfo.objectForKey("query");
    }


    // ----------------------------------------------------------
    public ReportQuery rollbackQueryForDataSet(ReportDataSet dataSet)
    {
    	NSDictionary<String, NSDictionary<String, Object>> queryMap =
    		(NSDictionary<String, NSDictionary<String, Object>>)
    		transientState().objectForKey(KEY_QUERIES);

    	if (queryMap == null)
        {
    		return null;
        }

    	String uuid = dataSet.uuid();
    	NSDictionary<String, Object> queryInfo = queryMap.objectForKey(uuid);

    	if (queryInfo == null)
        {
    		return null;
        }

    	ReportQuery query = (ReportQuery)queryInfo.objectForKey("query");

    	if (query != null)
    	{
    		// Only remove the query from the database if it isn't being used
    		// by any other ReportDataSetQueries.
    		NSArray<ReportDataSetQuery> uses = query.dataSetQueries();
    		if (uses == null || uses.count() == 0)
    		{
    			localContext().deleteObject(query);
    			applyLocalChanges();
    		}
    	}

    	return query;
    }


    // ----------------------------------------------------------
    public void commitNewQueryForDataSet(
        ReportDataSet dataSet, String description, EOQualifier qualifier)
    {
    	ReportQuery query = new ReportQuery();
    	localContext().insertObject(query);
    	query.setDescription(description);
    	query.setQualifier(qualifier);
    	query.setUserRelationship(user());
    	query.setWcEntityName(dataSet.wcEntityName());
    	applyLocalChanges();

    	commitExistingQueryForDataSet(dataSet, query);
    }


    // ----------------------------------------------------------
    public void commitExistingQueryForDataSet(
        ReportDataSet dataSet, ReportQuery query)
    {
    	NSMutableDictionary<String, NSMutableDictionary<String, Object>>
            queryMap =
    		(NSMutableDictionary<String, NSMutableDictionary<String, Object>>)
    		transientState().objectForKey(KEY_QUERIES);

    	if (queryMap == null)
    	{
    		queryMap = new NSMutableDictionary<String,
    			NSMutableDictionary<String, Object>>();
    		transientState().setObjectForKey(queryMap, KEY_QUERIES);
    	}

    	String uuid = dataSet.uuid();
    	NSMutableDictionary<String, Object> queryInfo =
    		queryMap.objectForKey(uuid);

    	if (queryInfo == null)
    	{
    		queryInfo = new NSMutableDictionary<String, Object>();
    		queryMap.setObjectForKey(queryInfo, uuid);
    	}

   		queryInfo.setObjectForKey(query, "query");
    }


    // ----------------------------------------------------------
    public String commitReportGeneration()
    {
		String errorMessage = null;
		log.debug("committing report generation");

		EOEditingContext ec = localContext();

		ReportTemplate reportTemplate = localReportTemplate();

		// Queue it up for the reporter
		NSTimestamp queueTime = new NSTimestamp();
		String actionUrl = context().directActionURLForActionNamed(
		    "reportResource/image", null);

		EnqueuedReportJob job = new EnqueuedReportJob();
		ec.insertObject(job);

		job.setDescription(localReportDescription());
		job.setReportTemplateRelationship(reportTemplate);
		job.setQueueTime(queueTime);
		job.setUuid(localReportUuid());
		job.setRenderedResourceActionUrl(actionUrl);
		job.setRenderingMethod(localRenderingMethod());
		job.setUserRelationship(user());

		ec.saveChanges();

		// Create ReportDataSetQuery objects to map all of the data sets in
		// the transient state to the queries that were created for them.
		NSArray<String> dataSetUuids = localDataSetUuids();
		for (String uuid : dataSetUuids)
		{
			NSArray<ReportDataSet> dataSets = ReportDataSet.objectsForUuid(
					ec, uuid);

			if (dataSets.count() == 1)
			{
				ReportDataSet dataSet = dataSets.objectAtIndex(0);
				ReportQuery query = queryForLocalDataSetUuid(uuid);

				ReportDataSetQuery dataSetQuery =
					job.createDataSetQueriesRelationship();
				dataSetQuery.setDataSetRelationship(dataSet);
				dataSetQuery.setReportQueryRelationship(query);
				applyLocalChanges();
			}
			else
			{
				throw new IllegalStateException("There should only be one " +
						"data set for a particular uuid in the database!");
			}
		}

		ProgressManager progress = ProgressManager.getInstance();
 		progress.beginJobWithToken(localReportUuid());
 		progress.beginTaskForJob(localReportUuid(), new int[] { 95, 5 },
 		    new ReportQueueStatusDescriptionProvider());

		setLocalEnqueuedJob(job);
		Reporter.getInstance().reportQueue().enqueue(null);

		return errorMessage;
	}


    // ----------------------------------------------------------
    public String commitReportRendering()
    {
        String errorMessage = null;
        log.debug("committing report rendering");

        // Queue it up for the reporter
        String uuidString = localReportUuid();
        String actionUrl = context().directActionURLForActionNamed(
            "reportResource/image", null);

        EnqueuedReportJob job = new EnqueuedReportJob();
        localContext().insertObject(job);
        job.setUuid(uuidString);
        job.setRenderedResourceActionUrl(actionUrl);

        String method = localRenderingMethod();
        if (method == null)
        {
            method = "html";
            setLocalRenderingMethod(method);
        }

        job.setRenderingMethod(method);
        job.setUserRelationship(user());
//      job.setParameterSelections(new MutableDictionary());
        applyLocalChanges();

        ProgressManager progress = ProgressManager.getInstance();
        progress.beginJobWithToken(localReportUuid());
        progress.beginTaskForJob(localReportUuid(), 1,
                "Rendering report");

        setLocalEnqueuedJob(job);
        Reporter.getInstance().reportQueue().enqueue(null);

        return errorMessage;
    }


    //~ Private Methods/Classes ...............................................

    // ----------------------------------------------------------
    private static class ReportQueueStatusDescriptionProvider
    	implements IProgressManagerDescriptionProvider
    {
        //~ Constructor .......................................................

        // ----------------------------------------------------------
    	public ReportQueueStatusDescriptionProvider()
    	{
    		oldQueuePos = -1;
    	}


        //~ Public Methods ....................................................

        // ----------------------------------------------------------
		public String description(Object jobToken)
		{
			ReportQueueProcessor rqp =
				Reporter.getInstance().reportQueueProcessor();

			String uuid = (String)jobToken;
			String description;

			if (Reporter.getInstance().isThrottled())
			{
				long time = Reporter.getInstance().throttleTime();

				NSTimestampFormatter fmt = new NSTimestampFormatter(
				    FinalReportPage.formatForSmallTime(time));

				String timeString = fmt.format(new NSTimestamp(time));

				description = "Report generation is currently <b>paused</b> "
                    + "while there are submissions being processed by the "
                    + "grader.  The reporter will resume in approximately "
                    + "<b>" + timeString + "</b>.";
			}
			else if (rqp.isReportWithUuidRunning(uuid))
			{
				description = "Generating the report...";
			}
			else
			{
				description = updateJobData(uuid);
			}

			return description;
		}


        //~ Private Methods ...................................................

        // ----------------------------------------------------------
		private String updateJobData(String uuid)
		{
			if (jobData == null)
            {
				jobData = new JobData();
            }

    		EOEditingContext ec = Application.newPeerEditingContext();

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
                ec.objectsWithFetchSpecification(
                    fetchSpec
                );

            if (oldQueuePos < 0
                || oldQueuePos >= jobData.jobs.count())
            {
                oldQueuePos = jobData.jobs.count() - 1;
            }

            jobData.queuePosition = jobData.jobs.count();
            for ( int i = oldQueuePos; i >= 0; i-- )
            {
                EnqueuedReportJob job =
                    (EnqueuedReportJob)jobData.jobs.objectAtIndex( i );
                if (job.uuid() == uuid )
                {
                    jobData.queuePosition = i;
                    break;
                }
            }
            oldQueuePos = jobData.queuePosition;

//            Grader grader = Grader.getInstance();
//            jobData.mostRecentWait = grader.mostRecentJobWait();
 //           jobData.estimatedWait =
//                grader.estimatedJobTime() * ( jobData.queuePosition + 1 );

            Application.releasePeerEditingContext(ec);

            if (jobData.queuePosition > 1)
            {
            	return "Your report will begin shortly (queue position " +
        			jobData.queuePosition + ").";
            }
            else
            {
            	return "Your report is being generated.";
            }
		}

        // ----------------------------------------------------------
        private static class JobData
        {
            public NSArray jobs;
//          public int queueSize;
            public int queuePosition;
            long mostRecentWait;
            long estimatedWait;
        }


        //~ Instance/static variables .........................................

        private JobData jobData;
		private int oldQueuePos;
    }


    //~ Instance/static variables .............................................

    // Internal constants for key names
    private static final String KEY_REPORT_DESCRIPTION =
        "net.sf.webcat.reporter.reportDescription";
    private static final String KEY_REPORT_UUID =
        "net.sf.webcat.reporter.reportUuid";
    private static final String KEY_ENQUEUED_JOB =
        "net.sf.webcat.reporter.enqueuedJob";
    private static final String KEY_REPORT_TEMPLATE =
        "net.sf.webcat.reporter.reportTemplate";
    private static final String KEY_GENERATED_REPORT =
        "net.sf.webcat.reporter.generatedReport";
    private static final String KEY_RENDERING_METHOD =
        "net.sf.webcat.reporter.renderingMethod";
    private static final String KEY_CURRENT_DATASET =
        "net.sf.webcat.reporter.currentDataSet";
    private static final String KEY_QUERIES =
        "net.sf.webcat.reporter.queries";
    private static final String KEY_PAGE_CONTROLLER =
        "net.sf.webcat.reporter.pageController";

//    private ReporterPrefs prefs;
    static Logger log = Logger.getLogger( ReporterComponent.class );

}
