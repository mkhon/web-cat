/*==========================================================================*\
 |  $Id: Reporter.java,v 1.18 2009/12/09 05:03:40 aallowat Exp $
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

import java.util.Hashtable;
import java.util.Map;
import net.sf.webcat.birtruntime.BIRTRuntime;
import net.sf.webcat.core.Application;
import net.sf.webcat.core.Subsystem;
import net.sf.webcat.grader.EnqueuedJob;
import net.sf.webcat.grader.Grader;
import net.sf.webcat.jobqueue.HostDescriptor;
import net.sf.webcat.jobqueue.QueueDescriptor;
import net.sf.webcat.reporter.messaging.ReportCompleteMessage;
import org.apache.log4j.Logger;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IDataExtractionTask;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.eclipse.birt.report.model.api.IDesignEngine;
import org.eclipse.birt.report.model.api.SessionHandle;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;
import er.extensions.eof.ERXConstant;
import er.extensions.eof.ERXEOControlUtilities;
import er.extensions.eof.ERXQ;

//-------------------------------------------------------------------------
/**
 * The primary class of the Reporter subsystem.
 *
 * @author Tony Allevato
 * @version $Id: Reporter.java,v 1.18 2009/12/09 05:03:40 aallowat Exp $
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

        // Register the reporter subsystem's messages.

        ReportCompleteMessage.register();

        // Register the report generation job queue and create worker threads.

        QueueDescriptor.registerQueue(ReportGenerationJob.ENTITY_NAME);
        new ReportGenerationWorkerThread().start();

        // Create the global report design session used to gather information
        // about uploaded report templates.

        log.info("Creating global report design session");

        IDesignEngine designEngine =
            BIRTRuntime.getInstance().getDesignEngine();
        designSession = designEngine.newSessionHandle(null);
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
    public IRunTask setupRunTaskForJob(ReportGenerationJob job)
    {
        IReportEngine reportEngine =
            BIRTRuntime.getInstance().getReportEngine();

        ReportTemplate template = job.generatedReport().reportTemplate();

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

        ManagedReportGenerationJob managedJob =
            new ManagedReportGenerationJob(job);
        OdaResultSetProvider resultProvider =
            new OdaResultSetProvider(managedJob);

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
    public SessionHandle designSession()
    {
        return designSession;
    }


    // ----------------------------------------------------------
    public boolean refreshThrottleStatus()
    {
        EOEditingContext ec = Application.newPeerEditingContext();

        EOQualifier qualifier = ERXQ.and(
                ERXQ.is(EnqueuedJob.DISCARDED_KEY, 0),
                ERXQ.is(EnqueuedJob.PAUSED_KEY, 0));

        jobCountAtLastThrottleCheck =
            ERXEOControlUtilities.objectCountWithQualifier(ec,
                EnqueuedJob.ENTITY_NAME, qualifier);

        Application.releasePeerEditingContext(ec);

        /*if (log.isDebugEnabled())
        {
            log.debug("refreshThrottleStatus(): " + isThrottled());
        }*/

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

    private SessionHandle designSession;
    private int           jobCountAtLastThrottleCheck;

    /**
     * This is the sole instance of the reporter subsystem, initialized by the
     * constructor.
     */
    private static Reporter instance;

    private static Logger log = Logger.getLogger( Reporter.class );
}
