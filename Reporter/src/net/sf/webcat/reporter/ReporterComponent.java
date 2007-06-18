package net.sf.webcat.reporter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

import net.sf.webcat.core.Application;
import net.sf.webcat.core.MutableDictionary;
import net.sf.webcat.core.User;
import net.sf.webcat.core.WCComponent;
import net.sf.webcat.grader.EnqueuedJob;
import net.sf.webcat.grader.Grader;
import net.sf.webcat.grader.Submission;

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

    public static final String REPORTER_PREFS_KEY = "reporterPrefs";

    public static final String SESSION_PARAMETER_SELECTIONS =
    	"reporter.parameterSelections";
    public static final String SESSION_REPORT_NAME =
    	"reporter.reportName";
    public static final String SESSION_REPORT_UUID =
    	"reporter.reportUuid";
    public static final String SESSION_ENQUEUED_JOB =
    	"reporter.enqueuedJob";
    public static final String SESSION_REPORT_TEMPLATE =
    	"reporter.reportTemplate";
    public static final String SESSION_GENERATED_REPORT =
    	"reporter.generatedReport";

    
    //~ Methods ...............................................................

    // ----------------------------------------------------------
   
    public NSMutableDictionary parameterSelectionsInSession()
    {
    	return (NSMutableDictionary)wcSession().objectForKey(
    			SESSION_PARAMETER_SELECTIONS);
    }
    
    
    public void setParameterSelectionsInSession(NSMutableDictionary value)
    {
    	wcSession().setObjectForKey(value, SESSION_PARAMETER_SELECTIONS);
    }

    
    public String reportNameInSession()
    {
    	return (String)wcSession().objectForKey(SESSION_REPORT_NAME);
    }
    
    
    public void setReportNameInSession(String value)
    {
    	wcSession().setObjectForKey(value, SESSION_REPORT_NAME);
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

    
    public GeneratedReport generatedReportInSession()
    {
    	return (GeneratedReport)wcSession().objectForKey(
    			SESSION_GENERATED_REPORT);
    }
    
    
    public void setGeneratedReportInSession(GeneratedReport value)
    {
    	wcSession().setObjectForKey(value, SESSION_GENERATED_REPORT);
    }

    

    public String commitReportGeneration()
    {
		String errorMessage = null;
		log.debug("committing report generation");

		ReportTemplate reportTemplate = reportTemplateInSession();

		// Convert any EOModel objects to global IDs before adding the job to
		// the database.
		MutableDictionary selections = new MutableDictionary(
				EOGlobalIDUtils.idsForEnterpriseObjectDictionary(
						parameterSelectionsInSession(),
						wcSession().localContext()));

		// Queue it up for the reporter
		NSTimestamp queueTime = new NSTimestamp();
		String actionUrl = context().directActionURLForActionNamed(
				"reportResource/image", null);

		EnqueuedReportJob job = new EnqueuedReportJob();
		wcSession().localContext().insertObject(job);
		job.setReportName(reportNameInSession());
		job.setReportTemplateRelationship(reportTemplate);
		job.setParameterSelections(selections);
		job.setQueueTime(queueTime);
		job.setUuid(reportUuidInSession());
		job.setRenderedResourceActionUrl(actionUrl);
		job.setUserRelationship(wcSession().user());
		wcSession().commitLocalChanges();

		ProgressManager progress = ProgressManager.getInstance();
 		progress.beginJobWithToken(reportUuidInSession());
 		progress.beginTaskForJob(reportUuidInSession(), 2,
 				"Generating report");

		setEnqueuedJobInSession(job);
		Reporter.getInstance().reportQueue().enqueue(null);

		return errorMessage;
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
		job.setUserRelationship(wcSession().user());
		job.setParameterSelections(new MutableDictionary());
		wcSession().commitLocalChanges();

		setEnqueuedJobInSession(job);
		Reporter.getInstance().reportQueue().enqueue(null);

//		prefs().clearUpload();
//		prefs().setSubmissionInProcess(false);

		return errorMessage;
	}

    
    //~ Instance/static variables .............................................

//    private ReporterPrefs prefs;
    static Logger log = Logger.getLogger( ReporterComponent.class );

}
