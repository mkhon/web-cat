/*==========================================================================*\
 |  $Id: ReportQueueProcessor.java,v 1.7 2008/04/01 19:35:27 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2008 Virginia Tech
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
 * generation/rendering/storing cycle on a report job.  It is based
 * on the GraderQueueProcessor from the Grader subsystem.
 *
 * @author aallowat
 * @version $Id: ReportQueueProcessor.java,v 1.7 2008/04/01 19:35:27 stedwar2 Exp $
 */
public class ReportQueueProcessor
    extends Thread
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Default constructor
     * @param queue the queue to operate on
     */
    public ReportQueueProcessor( ReportQueue queue )
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
        NSMutableArray newJobQualifiers = new NSMutableArray();
        newJobQualifiers.addObject( new EOKeyValueQualifier(
            EnqueuedReportJob.PAUSED_KEY,
            EOQualifier.QualifierOperatorEqual,
            ERXConstant.integerForInt( 0 ) ) );
        EOFetchSpecification fetchNewJobs =
            new EOFetchSpecification(
                EnqueuedReportJob.ENTITY_NAME,
                new EOAndQualifier( newJobQualifiers ),
                new NSArray( new Object[]{
                    new EOSortOrdering(
                        EnqueuedReportJob.QUEUE_TIME_KEY,
                        EOSortOrdering.CompareAscending
                    )
                } )
            );
        EOFetchSpecification fetchDiscardedJobs =
            new EOFetchSpecification(
                EnqueuedReportJob.ENTITY_NAME,
                new EOKeyValueQualifier(
                    EnqueuedReportJob.DISCARDED_KEY,
                    EOQualifier.QualifierOperatorEqual,
                    ERXConstant.integerForInt( 1 ) ),
                    null
            );
        try
        {
            while ( true )
            {
                if ( editingContext != null )
                {
                    editingContext.unlock();
                    Application.releasePeerEditingContext( editingContext );
                }
                editingContext = Application.newPeerEditingContext();
                editingContext.lock();

                // Clear discarded jobs
                NSArray jobList = null;
                try
                {
                    jobList = editingContext.objectsWithFetchSpecification(
                                fetchDiscardedJobs
                        );
                }
                catch ( Exception e )
                {
                    log.info( "error fetching jobs: ", e );
                    jobList = editingContext.objectsWithFetchSpecification(
                        fetchDiscardedJobs);
                }

                if ( jobList != null )
                {
                    // delete all the discarded jobs
                    for ( int i = 0; i < jobList.count(); i++ )
                    {
                        EnqueuedReportJob job =
                            (EnqueuedReportJob)jobList.objectAtIndex( i );
                        editingContext.deleteObject( job );
                    }
                    editingContext.saveChanges();
                    log.debug( "" + jobList.count()
                        + " discarded jobs retrieved" );
                }

                // Get a job
                log.debug( "waiting for a token" );
                // We don't need the return value, since it is just null:
                queue.getJobToken();
                log.debug( "token received." );

                jobList = null;
                try
                {
                    jobList = editingContext.objectsWithFetchSpecification(
                        fetchNewJobs);
                }
                catch ( Exception e )
                {
                    log.info( "error fetching jobs: ", e );
                    jobList = editingContext.objectsWithFetchSpecification(
                        fetchNewJobs);
                }

                if ( log.isDebugEnabled() )
                {
                    log.debug( ""
                               + ( jobList == null
                                   ? "<null>"
                                   : "" + jobList.count() )
                               + " fresh jobs retrieved" );
                }

                // This test is just to make sure the compiler knows it
                // isn't null, even though the try/catch above ensures it
                if ( jobList != null )
                {
                    for ( int i = 0; i < jobList.count(); i++ )
                    {
                        NSTimestamp startProcessing = new NSTimestamp();
                        EnqueuedReportJob job =
                            (EnqueuedReportJob)jobList.objectAtIndex( i );
                        if ( job.discarded() )
                        {
                            editingContext.deleteObject( job );
                            editingContext.saveChanges();
                            continue;
                        }

                        ReportTemplate reportTemplate = job.reportTemplate();
                        String uuid = job.uuid();
                        if ( reportTemplate == null && uuid == null )
                        {
                            log.error(
                                "null report template or report uuid in "
                                + "enqueued job: deleting" );
                            editingContext.deleteObject( job );
                            editingContext.saveChanges();
                        }
                        else
                        {
                        	/*
                        	 * Should add a way here to suspend all reports for
                        	 * a report template, if the template is determined
                        	 * to contain an error.
                        	 */
/*                            if ( submission.assignmentOffering()
                                     .gradingSuspended() )
                            {
                                log.warn( "Suspending job "
                                          + submission.dirName() );
                                job.setPaused( true );
                            }
                            else*/
                            {
								if(reportTemplate != null)
								{
									log.info(
                                        "generating and rendering report with "
                                        + "template "
                                        + reportTemplate.name() );

							 		int dataSetRefs =
                                        countDataSetRefs(reportTemplate);

							 		ProgressManager.getInstance()
							 		    .beginTaskForJob(
							 				job.uuid(), dataSetRefs);
								}
								else
								{
									log.info(
                                        "re-rendering already generated "
                                        + "report with uuid " + uuid);

									ProgressManager.getInstance()
                                        .beginTaskForJob(
											job.uuid(), 1, "Rendering report");
								}

                                processReportJobWithProtection( job );

                                ProgressManager.getInstance().forceJobComplete(
                                    job.uuid());

                                NSTimestamp now = new NSTimestamp();
                                if ( job.queueTime() != null )
                                {
                                    mostRecentJobWait = now.getTime()
                                        - job.queueTime().getTime();
                                }
/*                                else
                                {
                                    mostRecentJobWait = now.getTime()
                                    - submission.submitTime().getTime();
                                }
*/                                {
                                    long processingTime = now.getTime() -
                                       startProcessing.getTime();
                                    totalWaitForJobs += processingTime;
                                    jobsCountedWithWaits++;
                                }
                            }
                            // report template could have changed because
                            // of a fault, so save any changes before
                            // forcing it out of editing context cache
                            editingContext.saveChanges();

                            if(reportTemplate != null)
                            {
	                            editingContext.refaultObject(
	                                reportTemplate,
	                                editingContext.globalIDForObject(
	                                    reportTemplate ),
	                                editingContext );
                            }
                        }
                        // Only process one regrading job before looking for
                        // more regular submissions.
/*                        if ( job.regrading() )
                        {
                            queue.enqueue( null );
                            break;
                        }*/
                    }
                }
            }
        }
        catch ( Exception e )
        {
            log.fatal( "Job queue processing halted.\n"
                       + "Exception processing student submission",
                       e );
            Application.emailExceptionToAdmins(
                    e,
                    null,
                    "Job queue processing halted."
                );
            log.fatal( "Aborting: job queue processing halted." );
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
     * This function processes the job and performs the stages that
     * are necessary.  It guards against any exceptions while
     * processing the job.
     *
     * @param job the job to process
     */
    private void processReportJobWithProtection( EnqueuedReportJob job )
    {
        try
        {
            processReportJob( job );
        }
        catch ( Exception e )
        {
            technicalFault( job,
                            "while processing job",
                            e,
                            null );
        }
    }


    // ----------------------------------------------------------
    /**
     * This function processes the job and performs the stages that
     * are necessary.
     *
     * @param job the job to process
     */
    private void processReportJob( EnqueuedReportJob job )
    {
        jobCount++;
        log.info( "Processing report " + jobCount + " for: "
                  + job.user().userName() );

        boolean wasCanceled = false;

        GeneratedReport report = tryToGetGeneratedReport(job);
        if (report == null)
        {
	        // Set up the report document directory first
	        try
	        {
	            prepareReportOutputDirectory( job );
	        }
	        catch ( Exception e )
	        {
	            technicalFault( job,
	                            "while preparing the report storage directory",
	                            e,
	                            null );
	            return;
	        }

	        MutableArray exceptions = null;

	        try
	        {
	        	wasCanceled = generateReportDocument( job );
	        }
	        catch ( ReportGenerationException e )
	        {
	        	exceptions = new MutableArray();

	        	for (Exception ex : e.errors())
                {
	        		exceptions.addObject(ex);
                }
	        }

	        if ( wasCanceled )
	        {
	            return;
	        }

	        MutableArray errors = translateExceptions(exceptions);

	        report = new GeneratedReport();
	        editingContext.insertObject(report);

	        report.setGeneratedTime(job.queueTime());
	        report.setUuid(job.uuid());
	        report.setDescription(job.description());
	        report.setUserRelationship(job.user());
	        report.setReportTemplateRelationship(job.reportTemplate());
	        report.setErrors(errors);

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

	        editingContext.saveChanges();
        }

        if (!report.isRenderedWithMethod(job.renderingMethod()))
        {
	        // Set up the rendered resources directory first
	        try
	        {
	            prepareRenderOutputDirectory( job );
	        }
	        catch ( Exception e )
	        {
	            technicalFault(
                    job,
                    "while preparing the report rendered resources directory",
                    e,
                    null );
	            return;
	        }

	        // Render the report.
	        try
	        {
	        	wasCanceled = renderReportDocument(job, report);
	        }
	        catch ( Exception e )
	        {
//	        	technicalFault( job,
//	        			"while generating report", e, null );
//	        	return;
	        }

	        if ( wasCanceled )
	        {
	            return;
	        }
        }

        editingContext.deleteObject( job );
        editingContext.saveChanges();
    }


    // ----------------------------------------------------------
    private MutableArray translateExceptions(NSArray<Exception> exceptions)
    {
    	if (exceptions == null)
        {
    		return new MutableArray();
        }

    	MutableArray array = new MutableArray();

    	for (Exception e : exceptions)
    	{
    		MutableDictionary errorInfo = new MutableDictionary();

    		if (e instanceof BirtException)
    		{
    			errorInfo.setObjectForKey(
    			    ERXConstant.integerForInt(((BirtException)e).getSeverity()),
    			    "severity");
    		}
    		else
    		{
    			errorInfo.setObjectForKey(
                    ERXConstant.integerForInt(BirtException.ERROR),
    			    "severity");
    		}

    		if (e.getCause() != null)
    		{
    			if (e.getCause().getMessage() != null)
    			{
    				errorInfo.setObjectForKey(
                        e.getCause().getMessage(), "cause");
    			}
    			else
    			{
    				errorInfo.setObjectForKey(
                        e.getCause().toString(), "cause");
    			}
    		}

    		errorInfo.setObjectForKey(e.getMessage(), "message");
    		array.addObject(errorInfo);
    	}

    	return array;
    }


    // ----------------------------------------------------------
    public void cancelJobWithUuid(EOEditingContext context, String uuid)
    {
    	NSArray jobs = EnqueuedReportJob.objectsForUuid(context, uuid);
    	if (jobs.count() > 1)
    	{
    		log.warn("cancelJobWithUuid: more than one job with same uuid!");
    	}

    	if (jobs.count() > 0)
    	{
    		EnqueuedReportJob job = (EnqueuedReportJob)jobs.objectAtIndex(0);
    		job.setDiscarded(true);
    		context.saveChanges();

    		if (currentlyRunningThread != null
                && currentlyRunningThread.jobUuid().equals(uuid))
    		{
    			currentlyRunningThread.interrupt();
    		}

    		// If a report document got generated, delete it.
    		NSArray reports = GeneratedReport.objectsForUuid(context, uuid);
    		if (reports.count() > 0)
    		{
    			GeneratedReport report =
                    (GeneratedReport)reports.objectAtIndex(0);
    			context.deleteObject(report);
    			context.saveChanges();
    		}
    	}
    }


    // ----------------------------------------------------------
    private GeneratedReport tryToGetGeneratedReport(EnqueuedReportJob job)
    {
    	NSArray reports = GeneratedReport.objectsForUuid(
    			editingContext, job.uuid());

    	if (reports.count() > 0)
        {
    		return (GeneratedReport)reports.objectAtIndex(0);
        }
    	else
        {
    		return null;
        }
    }


    // ----------------------------------------------------------
	private boolean generateReportDocument( EnqueuedReportJob job )
	    throws ReportGenerationException
	{
		EOGlobalID jobId = editingContext.globalIDForObject(job);

		GenerateReportThread exeThread = new GenerateReportThread(
		    job.uuid(), jobId );

		try
		{
			currentlyRunningThread = exeThread;
			exeThread.start();
			exeThread.join();
			currentlyRunningThread = null;
		}
		catch ( InterruptedException e )
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
	private class ReportGenerationException
        extends Exception
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
				message.append(
                    ((Throwable)errors.get(i)).getCause().toString());
				message.append("\n\n");
			}

			return message.toString();
		}


        //~ Instance/static variables .........................................

        private List<Exception> errors;
	}

    // ----------------------------------------------------------
	private boolean renderReportDocument(
        EnqueuedReportJob job, GeneratedReport report)
        throws Exception
	{
		EOGlobalID jobId = editingContext.globalIDForObject(job);
		EOGlobalID reportId = editingContext.globalIDForObject(report);

		RenderReportThread exeThread = new RenderReportThread(
        		job.uuid(), jobId, reportId );

		try
		{
			currentlyRunningThread = exeThread;
			exeThread.start();
			exeThread.join();
			currentlyRunningThread = null;
		}
		catch ( InterruptedException e )
		{
		    // Nothing to do
		}

		if ( exeThread.exception != null )
		{
			throw exeThread.exception;
		}

		return exeThread.wasCanceled;
	}


    // ----------------------------------------------------------
	private class ReportQueueProcessorThread
        extends Thread
	{
        //~ Constructor .......................................................

        // ----------------------------------------------------------
		public ReportQueueProcessorThread(String uuid)
		{
			jobUuid = uuid;
		}


        //~ Methods ...........................................................

        // ----------------------------------------------------------
		public String jobUuid()
		{
			return jobUuid;
		}


        //~ Instance/static variables .........................................

        private String jobUuid;
	}


    // ----------------------------------------------------------
	private class GenerateReportThread
        extends ReportQueueProcessorThread
	{
        //~ Constructor .......................................................

        // ----------------------------------------------------------
	    public GenerateReportThread( String uuid, EOGlobalID id )
	    {
	        super(uuid);
	        jobId = id;
	    }


        //~ Methods ...........................................................

        // ----------------------------------------------------------
	    public void run()
	    {
        	EOEditingContext context = Application.newPeerEditingContext();

        	EnqueuedReportJob job =
        		(EnqueuedReportJob)context.faultForGlobalID(jobId, context);

        	String reportPath = GeneratedReport.generatedReportFilePathForUser(
        			job.user(), job.uuid());

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

	            // The current task in the progress stack for this job is the
	            // task containing units of work for each data set. Complete it
	            // to move us back up to the generate/render task.
                ProgressManager.getInstance().completeCurrentTaskForJob(
                    jobUuid());
	        }
	        catch ( EngineException e )
	        {
	            // Error creating process, so record it
	            log.error( "Exception generating " + reportPath, e );

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

	    		log.info("Reporter job with uuid " + jobUuid()
                    + " canceled during generation stage");
	    	}
	    	super.interrupt();
	    }


        // ----------------------------------------------------------
		public List<Exception> generationErrors()
		{
			return generationErrors;
		}


        //~ Instance/static variables .........................................

        public boolean wasCanceled = false;
	    private EOGlobalID	jobId;
	    private IRunTask runTask;
	    private List<Exception> generationErrors = null;
	}


    // ----------------------------------------------------------
    public class RenderReportThread
        extends ReportQueueProcessorThread
    {
        //~ Constructor .......................................................

        // ----------------------------------------------------------
        public RenderReportThread(String uuid, EOGlobalID id, EOGlobalID rid)
        {
            super(uuid);

            jobId = id;
            reportId = rid;
        }


        //~ Methods ...........................................................

        // ----------------------------------------------------------
        public void run()
        {
        	EOEditingContext context = Application.newPeerEditingContext();

        	EnqueuedReportJob job =
        		(EnqueuedReportJob)context.faultForGlobalID(jobId, context);
        	GeneratedReport report =
        		(GeneratedReport)context.faultForGlobalID(reportId, context);

        	IRenderingMethod method =
        		Reporter.getInstance().renderingMethodWithName(
        			job.renderingMethod());

        	if (method != null)
        	{
            	NSMutableDictionary options = new NSMutableDictionary();
            	options.setObjectForKey(job.renderedResourceActionUrl(),
            	    IRenderingMethod.OPTION_ACTION_URL);
            	controller = method.prepareToRender(report, options);

            	try
            	{
            		controller.render();
            		controller = null;
            		report.markAsRenderedWithMethod(job.renderingMethod());
            	}
            	catch (Exception e)
            	{
                    log.error("Exception rendering report: "
                        + report.description()
                        + "(uuid: " + report.uuid() + ")", e);
                    exception = e;
            	}
        	}

    		ProgressManager.getInstance().stepJob(jobUuid());

            Application.releasePeerEditingContext(context);
        }


        // ----------------------------------------------------------
	    public void interrupt()
	    {
	    	if (controller != null)
	    	{
	    		controller.cancel();
	    		wasCanceled = true;

	    		log.info("Reporter job with uuid " + jobUuid()
                    + " canceled during rendering stage");
	    	}

	    	super.interrupt();
	    }


        //~ Instance/static variables .........................................

        public boolean wasCanceled = false;
        private EOGlobalID	jobId;
        private EOGlobalID	reportId;
        private IRenderingMethod.Controller controller;
        public  Exception exception = null;
    }


	// ----------------------------------------------------------
    /**
     * Creates and cleans the working directory, if necessary, fills
     * it with the student's submission, and creates the reporting
     * directory.
     *
     * @param job the job to operate on
     * @throws Exception if it occurs during this stage
     */
    private void prepareReportOutputDirectory( EnqueuedReportJob job )
    {
        // Create the working compilation directory for the user
        File workingDir = new File( job.generatedReportDir() );
        if ( !workingDir.exists() )
        {
            workingDir.mkdirs();
        }
    }


    // ----------------------------------------------------------
    private void prepareRenderOutputDirectory( EnqueuedReportJob job )
	{
	    // Create the working compilation directory for the user
	    File workingDir = new File(
	    		GeneratedReport.renderedResourcesDir(job.uuid()));

	    if (workingDir.exists())
	    {
	    	FileUtilities.deleteDirectory(workingDir);
	    }

	    workingDir.mkdirs();
	}

    // ----------------------------------------------------------
    /**
     * Handles a technical fault Suspends grading of other submissions for the
     * same assignment
     *
     * @param job the job which faulted
     */
    void technicalFault( EnqueuedReportJob job,
                         String      stage,
                         Exception   e,
                         File        attachmentsDir )
    {
        job.setPaused( true );

        // Suspend grading for the assignment
//        ReportTemplate reportTemplate = job.reportTemplate();
        // assignment.setSuspended( true );
//        assignment.setHasSuspendedSubs( true );

        Vector attachments = null;
        if ( attachmentsDir != null  &&  attachmentsDir.exists() )
        {
            attachments = new Vector();
            File[] fileList = attachmentsDir.listFiles();
            for ( int i = 0; i < fileList.length; i++ )
            {
                if ( !fileList[i].isDirectory() )
                {
                    attachments.addElement( fileList[i].getPath() );
                }
            }
        }

        String errorMsg = "An " + ( ( e == null ) ? "error": "exception" )
                          + " occurred " + stage;
        if ( e != null )
        {
            errorMsg += ":\n" + e;
        }

        errorMsg += "\n\nGeneration of reports from this template has "
            + "been suspended.\n";
        String subject = "[Reporter] Report generation error: "
            + job.user().userName();
        log.info( "technicalFault(): " + subject );
        log.info( errorMsg, e );
        NSMutableArray users = new NSMutableArray();
        users.addObject(job.user());
        Application.sendAdminEmail( null,
                                    users,
                                    true,
                                    subject,
                                    errorMsg,
                                    attachments );
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
        if ( jobsCountedWithWaits > 0 )
        {
            return totalWaitForJobs / jobsCountedWithWaits;
        }
        else
        {
            return DEFAULT_JOB_WAIT;
        }
    }


    // ----------------------------------------------------------
    public boolean isReportWithUuidRunning(String uuid)
    {
    	if (currentlyRunningThread != null)
    	{
    		if (currentlyRunningThread.jobUuid() == uuid)
    		{
    			return true;
    		}
    	}
    	return false;
    }


    //~ Instance/static variables .............................................

    /**
     * The grace period is added to the timeout limits for the various
     * scripts.  The value comes from the application property file.
     */
    static final int gracePeriod =
        Application.configurationProperties().intForKey( "reporter.gracePeriod" );

    /**
     * The grace period is added to the timeout limits for the various
     * scripts.  The value comes from the application property file.
     */
/*    static final int emailWaitMinutes =
        Application.configurationProperties().intForKeyWithDefault(
            "grader.mailResultNotificationAfterMinutes", 15 );
*/
    /** The queue to receive processing tokens. */
    private ReportQueue queue;

    /** Number of jobs processed so far, to report administrative status. */
    private int  jobCount = 0;
    private int  jobsCountedWithWaits = 0;

    /** Time between submission and grading completion for more recent job. */
    private long mostRecentJobWait = 0;
    private long totalWaitForJobs = 0;
    private static final long DEFAULT_JOB_WAIT = 30000;

    static final int defaultTimeout = net.sf.webcat.core.Application
		.configurationProperties()
		.intForKeyWithDefault( "reporter.timeout.default", 300 );

    // State for the current step being executed
//    private boolean timeoutOccurredInStep;

    private EOEditingContext editingContext;

    private ReportQueueProcessorThread currentlyRunningThread;

    static Logger log = Logger.getLogger( ReportQueueProcessor.class );
}
