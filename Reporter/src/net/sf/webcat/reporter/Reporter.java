/*==========================================================================*\
 |  $Id: Reporter.java,v 1.11 2008/04/15 04:09:22 aallowat Exp $
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

import com.ibm.icu.util.ULocale;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import er.extensions.ERXConstant;
import er.extensions.ERXEOControlUtilities;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;
import java.util.Map;
import net.sf.webcat.birtruntime.BIRTRuntime;
import net.sf.webcat.core.Application;
import net.sf.webcat.core.Session;
import net.sf.webcat.core.Subsystem;
import net.sf.webcat.core.TabDescriptor;
import net.sf.webcat.dbupdate.UpdateEngine;
import net.sf.webcat.grader.EnqueuedJob;
import net.sf.webcat.grader.Grader;
import net.sf.webcat.grader.GraderQueue;
import net.sf.webcat.grader.GraderQueueProcessor;
import net.sf.webcat.reporter.internal.rendering.CSVRenderingMethod;
import net.sf.webcat.reporter.internal.rendering.ExcelRenderingMethod;
import net.sf.webcat.reporter.internal.rendering.HTMLRenderingMethod;
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
import org.eclipse.birt.report.engine.api.IDataExtractionTask;
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

//-------------------------------------------------------------------------
/**
 * The primary class of the Reporter subsystem.
 *
 * @author Tony Allevato
 * @version $Id: Reporter.java,v 1.11 2008/04/15 04:09:22 aallowat Exp $
 */
public class Reporter
    extends Subsystem
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new Reporter subsystem object.
     */
    public Reporter()
    {
        super();

        instance = this;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /* (non-Javadoc)
     * @see net.sf.webcat.core.Subsystem#init()
     */
    public void init()
    {
        super.init();

        // Initialize the rendering methods that are available for reporting.
        initializeRenderingMethods();

        // Create the queue and the queueprocessor
        reportGenerationQueue          = new ReportGenerationQueue();
        reportGenerationQueueProcessor = new ReportGenerationQueueProcessor( reportGenerationQueue );

        reportRenderQueue = new ReportRenderQueue();
        reportRenderQueueProcessor = new ReportRenderQueueProcessor( reportRenderQueue );

        log.info("Creating global report design session");

        IDesignEngine designEngine =
            BIRTRuntime.getInstance().getDesignEngine();
        designSession = designEngine.newSessionHandle(null);

        // Kick off the processor thread
        reportGenerationQueueProcessor.start();
        reportRenderQueueProcessor.start();

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
                                ec, EnqueuedReportGenerationJob.ENTITY_NAME );

                for ( int i = 0; i < jobList.count(); i++ )
                {
                    // Only need to trigger the queue processor once,
                    // and it will slurp up all the jobs that are ready.
                    reportGenerationQueue.enqueue( null );
                    break;
                }
            }
            finally
            {
                ec.unlock();
                Application.releasePeerEditingContext( ec );
            }
        }
    }


    // ----------------------------------------------------------
    private void initializeRenderingMethods()
    {
        IReportEngine reportEngine =
            BIRTRuntime.getInstance().getReportEngine();

        // Initialize the available report rendering methods.

        NSMutableArray methods = new NSMutableArray();
        methods.addObject(new HTMLRenderingMethod(reportEngine));
        methods.addObject(new CSVRenderingMethod(reportEngine));
        methods.addObject(new ExcelRenderingMethod(reportEngine));
        renderingMethods = methods;
    }


    // ----------------------------------------------------------
    /**
     * Returns the sole instance of the reporter subsystem.
     *
     * @return the Reporter object that represents the subsystem.
     */
    public static Reporter getInstance()
    {
        return instance;
    }


    // ----------------------------------------------------------
    public IReportRunnable openReportTemplate(String path)
    {
        IReportEngine reportEngine =
            BIRTRuntime.getInstance().getReportEngine();

        try
        {
            return reportEngine.openReportDesign(path);
        }
        catch (EngineException e)
        {
            log.error("Error opening report template: " + path, e);
            return null;
        }
    }


    // ----------------------------------------------------------
    public IReportDocument openReportDocument(String path)
    {
        IReportEngine reportEngine =
            BIRTRuntime.getInstance().getReportEngine();

        try
        {
            return reportEngine.openReportDocument(path);
        }
        catch (EngineException e)
        {
            log.error("Error opening report template: " + path, e);
            return null;
        }
    }


    // ----------------------------------------------------------
    public IRunTask setupRunTaskForJob(EnqueuedReportGenerationJob job)
    {
        IReportEngine reportEngine =
            BIRTRuntime.getInstance().getReportEngine();

        ReportTemplate template = job.reportTemplate();

        IReportRunnable runnable = openReportTemplate(template.filePath());
        IRunTask task = reportEngine.createRunTask(runnable);

        Map appContext = task.getAppContext();
        if (appContext == null)
        {
            appContext = new Hashtable();
        }
        else
        {
            appContext = new Hashtable(appContext);
        }

        OdaResultSetProvider resultProvider = new OdaResultSetProvider(job);

        appContext.put("net.sf.webcat.oda.resultSetProvider", resultProvider);

        task.setAppContext(appContext);

        return task;
    }


    // ----------------------------------------------------------
    public IGetParameterDefinitionTask createGetParameterDefinitionTask(
        IReportRunnable runnable)
    {
        IReportEngine reportEngine =
            BIRTRuntime.getInstance().getReportEngine();

        return reportEngine.createGetParameterDefinitionTask(runnable);
    }


    // ----------------------------------------------------------
    public IDataExtractionTask createDataExtractionTask(
        IReportDocument document)
    {
        IReportEngine reportEngine =
            BIRTRuntime.getInstance().getReportEngine();

        return reportEngine.createDataExtractionTask(document);
    }


    // ----------------------------------------------------------
    public NSArray allRenderingMethods()
    {
        return renderingMethods;
    }


    // ----------------------------------------------------------
    public IRenderingMethod renderingMethodWithName(String name)
    {
        for (int i = 0; i < renderingMethods.count(); i++)
        {
            IRenderingMethod method =
                (IRenderingMethod)renderingMethods.objectAtIndex(i);

            if (method.methodName().equals(name))
            {
                return method;
            }
        }

        log.error("Could not find a report rendering method with name "
            + name + "!");
        return null;
    }


    // ----------------------------------------------------------
    public ReportGenerationQueue reportGenerationQueue()
    {
        return reportGenerationQueue;
    }


    // ----------------------------------------------------------
    public ReportGenerationQueueProcessor reportGenerationQueueProcessor()
    {
        return reportGenerationQueueProcessor;
    }


    // ----------------------------------------------------------
    public ReportRenderQueue reportRenderQueue()
    {
        return reportRenderQueue;
    }


    // ----------------------------------------------------------
    public ReportRenderQueueProcessor reportRenderQueueProcessor()
    {
        return reportRenderQueueProcessor;
    }


    // ----------------------------------------------------------
    public SessionHandle designSession()
    {
        return designSession;
    }


    // ----------------------------------------------------------
    public boolean refreshThrottleStatus()
    {
        EOEditingContext ec = Application.newPeerEditingContext();

        NSMutableArray qualifiers = new NSMutableArray();
        qualifiers.addObject( new EOKeyValueQualifier(
            EnqueuedJob.DISCARDED_KEY,
            EOQualifier.QualifierOperatorEqual,
            ERXConstant.integerForInt( 0 )
        ) );
        qualifiers.addObject( new EOKeyValueQualifier(
            EnqueuedJob.PAUSED_KEY,
            EOQualifier.QualifierOperatorEqual,
            ERXConstant.integerForInt( 0 )
        ) );

        jobCountAtLastThrottleCheck =
            ERXEOControlUtilities.objectCountWithQualifier(ec,
                EnqueuedJob.ENTITY_NAME, new EOAndQualifier(qualifiers));

        Application.releasePeerEditingContext(ec);

        return isThrottled();
    }


    // ----------------------------------------------------------
    public boolean isThrottled()
    {
        return jobCountAtLastThrottleCheck > 0;
    }


    // ----------------------------------------------------------
    public long throttleTime()
    {
        return Grader.getInstance().estimatedJobTime() *
            jobCountAtLastThrottleCheck;
    }


    //~ Instance/static variables .............................................

    /**
     * This is the sole instance of the reporter subsystem, initialized by the
     * constructor.
     */
    private static Reporter instance;

    private NSArray renderingMethods;

    /** this is the main single report queue */
    private static ReportGenerationQueue reportGenerationQueue;

    /** this is the queue processor for processing report jobs */
    private static ReportGenerationQueueProcessor reportGenerationQueueProcessor;

    private static ReportRenderQueue reportRenderQueue;

    private static ReportRenderQueueProcessor reportRenderQueueProcessor;

    private SessionHandle designSession;

    private int jobCountAtLastThrottleCheck;

    static Logger log = Logger.getLogger( Reporter.class );
}
