package net.sf.webcat.reporter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.apache.log4j.Logger;

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

import er.extensions.ERXConstant;

import net.sf.webcat.core.Application;
import net.sf.webcat.core.MutableDictionary;
import net.sf.webcat.core.User;
import net.sf.webcat.core.WCComponent;

public class ReporterComponent extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new ReporterComponent object.
     * 
     * @param context The context to use
     */
    public ReporterComponent( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public static final String SESSION_REPORT_DESCRIPTION =
    	"net.sf.webcat.reporter.reportDescription";
    public static final String SESSION_REPORT_UUID =
    	"net.sf.webcat.reporter.reportUuid";
    public static final String SESSION_ENQUEUED_JOB =
    	"net.sf.webcat.reporter.enqueuedJob";
    public static final String SESSION_REPORT_TEMPLATE =
    	"net.sf.webcat.reporter.reportTemplate";
    public static final String SESSION_GENERATED_REPORT =
    	"net.sf.webcat.reporter.generatedReport";
    public static final String SESSION_RENDERING_METHOD =
    	"net.sf.webcat.reporter.renderingMethod";
    public static final String SESSION_CURRENT_DATASET =
    	"net.sf.webcat.reporter.currentDataSet";
    public static final String SESSION_QUERIES =
    	"net.sf.webcat.reporter.queries";
    public static final String SESSION_PAGE_CONTROLLER =
    	"net.sf.webcat.reporter.pageController";
    
    //~ Methods ...............................................................

    // ----------------------------------------------------------
   
    public void clearSessionData()
    {
    	wcSession().removeObjectForKey(SESSION_REPORT_DESCRIPTION);
    	wcSession().removeObjectForKey(SESSION_REPORT_UUID);
    	wcSession().removeObjectForKey(SESSION_ENQUEUED_JOB);
    	wcSession().removeObjectForKey(SESSION_REPORT_TEMPLATE);
    	wcSession().removeObjectForKey(SESSION_GENERATED_REPORT);
    	wcSession().removeObjectForKey(SESSION_RENDERING_METHOD);
    	wcSession().removeObjectForKey(SESSION_CURRENT_DATASET);
    	wcSession().removeObjectForKey(SESSION_QUERIES);
    	wcSession().removeObjectForKey(SESSION_PAGE_CONTROLLER);
    }
    

    public String reportDescriptionInSession()
    {
    	return (String)wcSession().objectForKey(SESSION_REPORT_DESCRIPTION);
    }
    
    
    public void setReportDescriptionInSession(String value)
    {
    	wcSession().setObjectForKey(value, SESSION_REPORT_DESCRIPTION);
    }

 
    public String reportUuidInSession()
    {
    	return (String)wcSession().objectForKey(SESSION_REPORT_UUID);
    }
    
    
    public void setReportUuidInSession(String value)
    {
    	wcSession().setObjectForKey(value, SESSION_REPORT_UUID);
    }


    public EnqueuedReportJob enqueuedJobInSession()
    {
    	return (EnqueuedReportJob)wcSession().objectForKey(
    			SESSION_ENQUEUED_JOB);
    }
    
    
    public void setEnqueuedJobInSession(EnqueuedReportJob value)
    {
    	wcSession().setObjectForKey(value, SESSION_ENQUEUED_JOB);
    }


    public ReportTemplate reportTemplateInSession()
    {
    	return (ReportTemplate)wcSession().objectForKey(
    			SESSION_REPORT_TEMPLATE);
    }
    
    
    public void setReportTemplateInSession(ReportTemplate value)
    {
    	wcSession().setObjectForKey(value, SESSION_REPORT_TEMPLATE);
    }

    
    public int currentReportDataSetInSession()
    {
    	return (Integer)wcSession().objectForKey(SESSION_CURRENT_DATASET);
    }
    
    
    public void setCurrentReportDataSetInSession(int value)
    {
    	wcSession().setObjectForKey(value, SESSION_CURRENT_DATASET);
    }
    
   
    public GeneratedReport generatedReportInSession()
    {
    	return (GeneratedReport)wcSession().objectForKey(
    			SESSION_GENERATED_REPORT);
    }
    
    
    public void setGeneratedReportInSession(GeneratedReport value)
    {
    	wcSession().setObjectForKey(value, SESSION_GENERATED_REPORT);
    }


    public String renderingMethodInSession()
    {
    	return (String)wcSession().objectForKey(SESSION_RENDERING_METHOD);
    }
    

    public void setRenderingMethodInSession(String value)
    {
    	wcSession().setObjectForKey(value, SESSION_RENDERING_METHOD);
    }

    
    public void createPageControllerInSession()
    {
    	QueryPageController controller = new QueryPageController(this,
    			reportTemplateInSession().dataSets());
    	
    	wcSession().setObjectForKey(controller, SESSION_PAGE_CONTROLLER);
    }


    public QueryPageController pageControllerInSession()
    {
    	return (QueryPageController)wcSession().objectForKey(
    			SESSION_PAGE_CONTROLLER);
    }


    public String componentForDataSetUuidInSession(String uuid)
    {
    	NSDictionary<String, NSDictionary<String, Object>> queryMap =
    		(NSDictionary<String, NSDictionary<String, Object>>)
    		wcSession().objectForKey(SESSION_QUERIES);
    	
    	if(queryMap == null)
    		return null;

    	NSDictionary<String, Object> queryInfo = queryMap.objectForKey(uuid);
    	
    	if(queryInfo == null)
    		return null;
    	
    	return (String)queryInfo.objectForKey("componentName");
    }
    
    
    public void setComponentForDataSetUuidInSession(String value, String uuid)
    {
    	NSMutableDictionary<String, NSMutableDictionary<String, Object>> queryMap =
    		(NSMutableDictionary<String, NSMutableDictionary<String, Object>>)
    		wcSession().objectForKey(SESSION_QUERIES);
    	
    	if(queryMap == null)
    	{
    		queryMap = new NSMutableDictionary<String,
    			NSMutableDictionary<String, Object>>();
    		wcSession().setObjectForKey(queryMap, SESSION_QUERIES);
    	}

    	NSMutableDictionary<String, Object> queryInfo =
    		queryMap.objectForKey(uuid);
    	
    	if(queryInfo == null)
    	{
    		queryInfo = new NSMutableDictionary<String, Object>();
    		queryMap.setObjectForKey(queryInfo, uuid);
    	}
    	
   		queryInfo.setObjectForKey(value, "componentName");
    }


    public NSArray<String> dataSetUuidsInSession()
    {
    	NSDictionary<String, NSDictionary<String, Object>> queryMap =
    		(NSDictionary<String, NSDictionary<String, Object>>)
    		wcSession().objectForKey(SESSION_QUERIES);
    	
    	if(queryMap == null)
    		return NSArray.EmptyArray;
    	else
    		return queryMap.allKeys();
    }


    public ReportQuery queryForDataSetUuid(String uuid)
    {
    	NSDictionary<String, NSDictionary<String, Object>> queryMap =
    		(NSDictionary<String, NSDictionary<String, Object>>)
    		wcSession().objectForKey(SESSION_QUERIES);
    	
    	if(queryMap == null)
    		return null;

    	NSDictionary<String, Object> queryInfo = queryMap.objectForKey(uuid);
    	
    	if(queryInfo == null)
    		return null;
    	
    	return (ReportQuery)queryInfo.objectForKey("query");
    }
    

    public ReportQuery rollbackQueryForDataSet(ReportDataSet dataSet)
    {
    	NSDictionary<String, NSDictionary<String, Object>> queryMap =
    		(NSDictionary<String, NSDictionary<String, Object>>)
    		wcSession().objectForKey(SESSION_QUERIES);
    	
    	if(queryMap == null)
    		return null;

    	String uuid = dataSet.uuid();
    	NSDictionary<String, Object> queryInfo = queryMap.objectForKey(uuid);
    	
    	if(queryInfo == null)
    		return null;
    	
    	ReportQuery query = (ReportQuery)queryInfo.objectForKey("query");

    	if(query != null)
    	{
    		// Only remove the query from the database if it isn't being used
    		// by any other ReportDataSetQueries.
    		NSArray<ReportDataSetQuery> uses = query.dataSetQueries();
    		if(uses == null || uses.count() == 0)
    		{
    			wcSession().localContext().deleteObject(query);
    			wcSession().commitLocalChanges();
    		}
    	}

    	return query;
    }


    public void commitNewQueryForDataSet(ReportDataSet dataSet,
    		String description, EOQualifier qualifier)
    {
    	ReportQuery query = new ReportQuery();
    	wcSession().localContext().insertObject(query);
    	query.setDescription(description);
    	query.setQualifier(qualifier);
    	query.setUserRelationship(wcSession().user());
    	query.setWcEntityName(dataSet.wcEntityName());
    	wcSession().commitLocalChanges();

    	commitExistingQueryForDataSet(dataSet, query);
    }

    public void commitExistingQueryForDataSet(ReportDataSet dataSet,
    		ReportQuery query)
    {
    	NSMutableDictionary<String, NSMutableDictionary<String, Object>> queryMap =
    		(NSMutableDictionary<String, NSMutableDictionary<String, Object>>)
    		wcSession().objectForKey(SESSION_QUERIES);
    	
    	if(queryMap == null)
    	{
    		queryMap = new NSMutableDictionary<String,
    			NSMutableDictionary<String, Object>>();
    		wcSession().setObjectForKey(queryMap, SESSION_QUERIES);
    	}

    	String uuid = dataSet.uuid();
    	NSMutableDictionary<String, Object> queryInfo =
    		queryMap.objectForKey(uuid);
    	
    	if(queryInfo == null)
    	{
    		queryInfo = new NSMutableDictionary<String, Object>();
    		queryMap.setObjectForKey(queryInfo, uuid);
    	}

   		queryInfo.setObjectForKey(query, "query");
    }

    public String commitReportGeneration()
    {
		String errorMessage = null;
		log.debug("committing report generation");

		EOEditingContext ec = wcSession().localContext();

		ReportTemplate reportTemplate = reportTemplateInSession();

		// Queue it up for the reporter
		NSTimestamp queueTime = new NSTimestamp();
		String actionUrl = context().directActionURLForActionNamed(
				"reportResource/image", null);

		EnqueuedReportJob job = new EnqueuedReportJob();
		ec.insertObject(job);

		job.setDescription(reportDescriptionInSession());
		job.setReportTemplateRelationship(reportTemplate);
		job.setQueueTime(queueTime);
		job.setUuid(reportUuidInSession());
		job.setRenderedResourceActionUrl(actionUrl);
		job.setRenderingMethod(renderingMethodInSession());
		job.setUserRelationship(wcSession().user());

		ec.saveChanges();

		// Create ReportDataSetQuery objects to map all of the data sets in
		// the session to the queries that were created for them.
		NSArray<String> dataSetUuids = dataSetUuidsInSession();
		for(String uuid : dataSetUuids)
		{
			NSArray<ReportDataSet> dataSets = ReportDataSet.objectsForUuid(
					ec, uuid);
			
			if(dataSets.count() == 1)
			{
				ReportDataSet dataSet = dataSets.objectAtIndex(0);
				ReportQuery query = queryForDataSetUuid(uuid);
				
				ReportDataSetQuery dataSetQuery = job.createDataSetQueriesRelationship();
				dataSetQuery.setDataSetRelationship(dataSet);
				dataSetQuery.setReportQueryRelationship(query);
				wcSession().commitLocalChanges();
			}
			else
			{
				throw new IllegalStateException("There should only be one " +
						"data set for a particular uuid in the database!");
			}
		}

		ProgressManager progress = ProgressManager.getInstance();
 		progress.beginJobWithToken(reportUuidInSession());
 		progress.beginTaskForJob(reportUuidInSession(), new int[] { 95, 5 },
 				new ReportQueueStatusDescriptionProvider()); //"Generating report");

		setEnqueuedJobInSession(job);
		Reporter.getInstance().reportQueue().enqueue(null);

		return errorMessage;
	}


    private static class ReportQueueStatusDescriptionProvider
    	implements IProgressManagerDescriptionProvider
    {
    	public ReportQueueStatusDescriptionProvider()
    	{
    		oldQueuePos = -1;
    	}

		public String description(Object jobToken)
		{
			ReportQueueProcessor rqp =
				Reporter.getInstance().reportQueueProcessor();

			String uuid = (String)jobToken;
			String description;

			if(rqp.isReportWithUuidRunning(uuid))
			{
				description = "Generating the report...";
			}
			else
			{
				description = updateJobData(uuid);
			}

			return description;
		}
		
		private String updateJobData(String uuid)
		{
			if(jobData == null)
				jobData = new JobData();
			
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

            if ( oldQueuePos < 0
                    || oldQueuePos >= jobData.jobs.count() )
            {
                oldQueuePos = jobData.jobs.count() - 1;
            }
            
            jobData.queuePosition = jobData.jobs.count();
            for ( int i = oldQueuePos; i >= 0; i-- )
            {
                EnqueuedReportJob job = (EnqueuedReportJob)jobData.jobs.objectAtIndex( i );
                if(job.uuid() == uuid )
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
            
            return "Your report will begin shortly (queue position " +
            	jobData.queuePosition + ").";
		}
		
		private JobData jobData;

		private int oldQueuePos;

		private static class JobData
		{
	        public NSArray jobs;
//	        public int queueSize;
	        public int queuePosition;
	        long mostRecentWait;
	        long estimatedWait;			
		}
    }

    public String commitReportRendering()
    {
		String errorMessage = null;
		log.debug("committing report rendering");

		// Queue it up for the reporter
		String uuidString = reportUuidInSession();
		String actionUrl = context().directActionURLForActionNamed(
				"reportResource/image", null);

		EnqueuedReportJob job = new EnqueuedReportJob();
		wcSession().localContext().insertObject(job);
		job.setUuid(uuidString);
		job.setRenderedResourceActionUrl(actionUrl);
		
		String method = renderingMethodInSession();
		if(method == null)
		{
			method = "html";
			setRenderingMethodInSession(method);
		}
		
		job.setRenderingMethod(method);
		job.setUserRelationship(wcSession().user());
//		job.setParameterSelections(new MutableDictionary());
		wcSession().commitLocalChanges();

		ProgressManager progress = ProgressManager.getInstance();
 		progress.beginJobWithToken(reportUuidInSession());
 		progress.beginTaskForJob(reportUuidInSession(), 1,
 				"Rendering report");

		setEnqueuedJobInSession(job);
		Reporter.getInstance().reportQueue().enqueue(null);

		return errorMessage;
	}

    
    //~ Instance/static variables .............................................

//    private ReporterPrefs prefs;
    static Logger log = Logger.getLogger( ReporterComponent.class );

}
