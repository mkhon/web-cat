package net.sf.webcat.reporter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;
import java.util.Map;

import net.sf.webcat.core.Application;
import net.sf.webcat.core.Session;
import net.sf.webcat.core.Subsystem;
import net.sf.webcat.core.TabDescriptor;
import net.sf.webcat.grader.EnqueuedJob;
import net.sf.webcat.grader.GraderQueue;
import net.sf.webcat.grader.GraderQueueProcessor;
import net.sf.webcat.reporter.datamodel.IResultSet;
import net.sf.webcat.reporter.internal.datamodel.DataSource;

import org.apache.log4j.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.IExtension;
import org.eclipse.birt.core.framework.IExtensionPoint;
import org.eclipse.birt.core.framework.IExtensionRegistry;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformConfig;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.eclipse.birt.report.engine.api.script.element.IReportDesign;
import org.eclipse.birt.report.model.api.DesignConfig;
import org.eclipse.birt.report.model.api.DesignEngine;
import org.eclipse.birt.report.model.api.IDesignEngine;
import org.eclipse.birt.report.model.api.IDesignEngineFactory;
import org.eclipse.birt.report.model.api.SessionHandle;

import com.ibm.icu.util.ULocale;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

public class Reporter extends Subsystem
{
	public static final String REPORT_ROOT_HTML = "report.html";

    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new Admin subsystem object.
     */
    public Reporter()
    {
        super();
        
        instance = this;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Initialize the subsystem-specific session data in a newly created
     * session object.  This method is called once by the core for
     * each newly created session object.
     * 
     * @param s The new session object
     */
    public void initializeSessionData( Session s )
    {
        s.tabs.mergeClonedChildren( subsystemTabTemplate );
    }

    // ----------------------------------------------------------
    /* (non-Javadoc)
     * @see net.sf.webcat.core.Subsystem#init()
     */
    public void init()
    {
        super.init();
        NSBundle myBundle = NSBundle.bundleForClass( Reporter.class );

        // TODO merge the tab template loading support into the Subsystem
        // base class
        {
            subsystemTabTemplate = TabDescriptor.tabsFromPropertyList(
                new NSData ( myBundle.bytesForResourcePath(
                                 TabDescriptor.TAB_DEFINITIONS ) ) );
        }
        
        initializeBIRT();

        // Create the queue and the queueprocessor
        reportQueue          = new ReportQueue();
        reportQueueProcessor = new ReportQueueProcessor( reportQueue );

        // Kick off the processor thread
        reportQueueProcessor.start();

        if ( Application.configurationProperties().booleanForKey(
                "reporter.resumeSuspendedJobs" ) )
        {
            // Resume any enqueued jobs (if reporter is coming back up
            // after an application restart)
            EOEditingContext ec = Application.newPeerEditingContext();
            try
            {
                ec.lock();
                NSArray jobList = EOUtilities.objectsForEntityNamed(
                                ec, EnqueuedReportJob.ENTITY_NAME );

                for ( int i = 0; i < jobList.count(); i++ )
                {
                    if ( !( (EnqueuedReportJob)jobList.objectAtIndex( i ) ).paused() )
                    {
                        // Only need to trigger the queue processor once,
                        // and it will slurp up all the jobs that are ready.
                        reportQueue.enqueue( null );
                        break;
                    }
                }
            }
            finally
            {
                ec.unlock();
                Application.releasePeerEditingContext( ec );
            }
        }
    }


    @SuppressWarnings( "deprecation" )
    private void initializeBIRT()
    {
        // Initialize the BIRT reporting engine.
        NSBundle myBundle = NSBundle.bundleForClass( Reporter.class );
        String reportEnginePath = myBundle.resourcePath() + "/" +
        	REPORT_ENGINE_SUBDIR;

        log.info("Using reporting engine located at " + reportEnginePath);
        
        EngineConfig config = new EngineConfig();
        config.setEngineHome( reportEnginePath );

        DesignConfig dConfig = new DesignConfig();
        dConfig.setBIRTHome( reportEnginePath );

        // Point the OSGi platform's configuration area to the storage folder
        // chosen by the Web-CAT admin. Otherwise, the default location is in
        // the report engine path specified above, which could be read-only.
    	String configArea = net.sf.webcat.core.Application
			.configurationProperties().getProperty("grader.submissiondir") +
			"/ReporterConfiguration";
    	String instanceArea = net.sf.webcat.core.Application
			.configurationProperties().getProperty("grader.submissiondir") +
			"/ReporterWorkspace";

    	// Copy the initial config area files from the ReportEngine subfolder
    	// into the new config area if they aren't already there.
    	File configAreaDir = new File(configArea);
    	if(!configAreaDir.exists())
    	{
    		configAreaDir.mkdirs();
    		
    		File configSrcDir = new File(reportEnginePath + "/configuration");

    		try
    		{
				net.sf.webcat.archives.FileUtilities
					.copyDirectoryContentsIfNecessary(configSrcDir,
							configAreaDir);
			}
    		catch (IOException e)
    		{
				log.fatal("Could not copy BIRT configuration data into " +
						"Web-CAT storage location", e);
			}
    	}

        Map osgiConfig = new Hashtable();
        osgiConfig.put("osgi.configuration.area", configArea);
        osgiConfig.put("osgi.instance.area", instanceArea);
        config.setOSGiConfig(osgiConfig);

        dConfig.setOSGiConfig(osgiConfig);

        try
        {
			Platform.startup( config );

        	IReportEngineFactory factory = (IReportEngineFactory) Platform
				.createFactoryObject(
						IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY );

			reportEngine = factory.createReportEngine( config );

        	IDesignEngineFactory dFactory = (IDesignEngineFactory) Platform
			.createFactoryObject(
					IDesignEngineFactory.EXTENSION_DESIGN_ENGINE_FACTORY );

        	designEngine = dFactory.createDesignEngine( dConfig );
        }
        catch (Exception e)
        {
        	log.fatal("Error initializing BIRT reporting engine", e);
		}
    }
    
    
    /**
     * Returns the sole instance of the reporter subsystem.
     * 
     * @return the Reporter object that represents the subsystem.
     */
    public static Reporter getInstance()
    {
    	return instance;
    }

    public IReportRunnable openReportTemplate(String path)
    {
    	try
    	{
			return reportEngine.openReportDesign(path);
		}
    	catch(EngineException e)
    	{
    		log.error("Error opening report template: " + path, e);
			return null;
		}
    }
    
    public IReportDocument openReportDocument(String path)
    {
    	try
    	{
			return reportEngine.openReportDocument(path);
		}
    	catch(EngineException e)
    	{
    		log.error("Error opening report template: " + path, e);
			return null;
		}
    }

    public IRunTask setupRunTaskForTemplate(ReportTemplate template,
    		NSDictionary selections, String reportUuid,
    		EOEditingContext editingContext)
    {
    	IReportRunnable runnable = openReportTemplate(template.filePath());
    	IRunTask task = reportEngine.createRunTask(runnable);

    	Map appContext = task.getAppContext();
    	if(appContext == null)
    		appContext = new Hashtable();
    	else
    		appContext = new Hashtable(appContext);
    	
    	NSMutableDictionary initialBindings = new NSMutableDictionary();
    	initialBindings.setObjectForKey(selections, "selected");

        // Initialize the data source.
        // This object will be registered as a scriptable object for
        // JavaScript in the reporting engine.
        DataSource webcatObject = new DataSource(
        		editingContext, initialBindings, reportUuid);

    	appContext.put(DataSource.SCRIPTABLE_OBJECT_NAME, webcatObject);
    	
    	task.setAppContext(appContext);

    	return task;
    }
    
    public IRenderTask setupRenderTaskForReport(GeneratedReport report,
    		String renderedResourceActionUrl)
    {
    	IReportDocument document = openReportDocument(
    			report.generatedReportFile());
    	
    	HTMLRenderOption option = new HTMLRenderOption();
    	option.setEmbeddable(true);
    	option.setImageHandler(new ReporterHTMLImageHandler(report,
    			renderedResourceActionUrl));
    	option.setOutputFileName(GeneratedReport.renderedResourcePath(
    			report.uuid(), REPORT_ROOT_HTML));
    	
    	IRenderTask task = reportEngine.createRenderTask(document);
    	task.setRenderOption(option);
    	
    	return task;
    }
   
    public IGetParameterDefinitionTask createGetParameterDefinitionTask(
    		IReportRunnable runnable)
    {
    	return reportEngine.createGetParameterDefinitionTask(runnable);
    }

    // TODO this is for debugging purposes. Kill it when we're done.
    public IResultSet executeReporterQuery(String queryString, NSDictionary vars)
    {
        DataSource webcatObject = new DataSource(
        		Application.newPeerEditingContext(), vars, null);

        return webcatObject.executeQuery(queryString);
    }

    public ReportQueue reportQueue()
    {
    	return reportQueue;
    }
    
    public ReportQueueProcessor reportQueueProcessor()
    {
    	return reportQueueProcessor;
    }

    public SessionHandle newDesignSession()
    {
    	return designEngine.newSessionHandle(null);
    }

    //~ Instance/static variables .............................................

    // TODO: this should be refactored into the Subsystem parent class,
    // but that means handling Core in an appropriate way.
    private static NSArray subsystemTabTemplate;
    
    /**
     * This is the sole instance of the reporter subsystem, initialized by the
     * constructor.
     */
    private static Reporter instance;
    
    /**
     * This is the sole instance of the report engine, initialized by the init
     * method.
     */
    private IReportEngine reportEngine;
    
    private IDesignEngine designEngine;
    
    /** this is the main single report queue */
    private static ReportQueue reportQueue;

    /** this is the queue processor for processing report jobs */
    private static ReportQueueProcessor reportQueueProcessor;

    static Logger log = Logger.getLogger( Reporter.class );
    
    static final String REPORT_ENGINE_SUBDIR = "ReportEngine";
}
