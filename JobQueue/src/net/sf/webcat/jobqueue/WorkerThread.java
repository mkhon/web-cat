/*==========================================================================*\
 |  $Id: WorkerThread.java,v 1.8 2009/11/18 20:30:13 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2009-2009 Virginia Tech
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

package net.sf.webcat.jobqueue;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;
import er.extensions.eof.ERXQ;
import er.extensions.eof.ERXS;
import net.sf.webcat.core.Application;

//-------------------------------------------------------------------------
/**
 * Implements a single worker thread on a single host, operating on a
 * shared database-backed queue of jobs represented as {@link JobBase}
 * subclass objects.
 *
 * @param <Job> The subclass of {@link JobBase} that this worker thread
 *     works on.
 *
 * @author Stephen Edwards
 * @author Last changed by $Author: aallowat $
 * @version $Revision: 1.8 $, $Date: 2009/11/18 20:30:13 $
 */
public abstract class WorkerThread<Job extends JobBase>
    extends Thread
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     * @param queueEntity The name of the entity representing the job
     * queue for this worker thread.
     */
    public WorkerThread(String queueEntity)
    {
        descriptor = new ManagedWorkerDescriptor(
            WorkerDescriptor.registerWorker(
                localContext(),
                HostDescriptor.currentHost(localContext()),
                QueueDescriptor.descriptorFor(localContext(), queueEntity),
                this));
    }


    // ----------------------------------------------------------
    /**
     * Access the queue descriptor for this worker's job queue.
     * @return The queue descriptor
     */
    public ManagedQueueDescriptor queueDescriptor()
    {
        if (queueDescriptor == null)
        {
            queueDescriptor = new ManagedQueueDescriptor(descriptor.queue());
        }
        return queueDescriptor;
    }


    // ----------------------------------------------------------
    /**
     * Access the host descriptor for this worker's host.
     * @return The host descriptor
     */
    public ManagedHostDescriptor hostDescriptor()
    {
        if (hostDescriptor == null)
        {
            hostDescriptor = new ManagedHostDescriptor(descriptor.host());
        }
        return hostDescriptor;
    }


    // ----------------------------------------------------------
    /**
     * Access the descriptor for this worker.
     * @return The worker descriptor
     */
    public ManagedWorkerDescriptor descriptor()
    {
        return descriptor;
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * The actual thread of execution, which cannot be overridden.
     */
    @SuppressWarnings("unchecked")
    public final void run()
    {
        logDebug("started");

        try
        {
            while (true)
            {
                ec.lock();

                killCancelledJobs();
                waitForAvailableJob();

                long jobStartTime = System.currentTimeMillis();
                processJob();
                long jobDuration = System.currentTimeMillis() - jobStartTime;

                // Compile the wait statistics.

                long jobsCountedWithWaits =
                    queueDescriptor().jobsCountedWithWaits() + 1;
                long totalWait =
                    queueDescriptor().totalWaitForJobs() + jobDuration;

                boolean wasCancelled = currentJob.isCancelled();
                currentJob.delete();

                try
                {
                    ec.saveChanges();
                    currentJob = null;

                    // Update the wait statistics.

                    boolean statsUpdated = false;

                    while (!wasCancelled && !statsUpdated)
                    {
                        try
                        {
                            queueDescriptor().setJobsCountedWithWaits(
                                    jobsCountedWithWaits);
                            queueDescriptor().setMostRecentJobWait(jobDuration);
                            queueDescriptor().setTotalWaitForJobs(totalWait);

                            queueDescriptor().saveChanges();
                            statsUpdated = true;
                        }
                        catch (Exception e)
                        {
                            statsUpdated = false;
                        }
                    }
                }
                catch (Exception e)
                {
                    Number jobId = currentJob.id();

                    // Refresh the editing context.

                    Application.releasePeerEditingContext(ec);
                    ec = null;
                    localContext();

                    // Get a local instance of the job with the same id.

                    NSArray<Job> results =
                        EOUtilities.objectsMatchingKeyAndValue(
                            ec, queueDescriptor().jobEntityName(),
                            "id", jobId.intValue());

                    if (results != null && results.count() > 0)
                    {
                        currentJob = results.objectAtIndex(0);
                    }
                }

                ec.unlock();
            }
        }
        catch (Exception e)
        {
            // FIXME what should we do here when an exception breaks the loop?
            ec.unlock();
        }
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    /**
     * Subclasses should implement this method to process the
     * {@link #currentJob()}.  All other work involving finding the next
     * job, managing the job queue, and so on is already implemented in
     * this abstract base class.  If this method throws any exceptions,
     * they will force the current job to be suspended (paused) and then
     * the {@link #sendJobSuspensionNotification()} method will be
     * invoked.
     */
    protected abstract void processJob();


    // ----------------------------------------------------------
    /**
     * Called to handle a cancellation request for the job owned by this
     * thread. The default behavior simply sets the isCancelled flag of the
     * thread to true so that it can be polled in the processJob method, but
     * subclasses may override this to provide their own cleanup logic if
     * necessary.
     *
     * Subclasses that override this method should always call the super method
     * first.
     */
    protected void cancelJob()
    {
        synchronized (this)
        {
            isCancelled = true;
        }
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the thread should cancel what it is
     * doing at the earliest opportunity, due to a cancellation request from
     * the user.
     *
     * @return true if the thread should cancel itself at the earliest
     *     opportunity, otherwise false
     */
    protected synchronized boolean isCancelling()
    {
        if (currentJob.isCancelled())
        {
            if (!isCancelled)
            {
                cancelJob();
            }

            return true;
        }
        else
        {
            return false;
        }
    }


    // ----------------------------------------------------------
    /**
     * Access the current job that this thread is working on.
     * @return The current job, or null if there is none
     */
    protected Job currentJob()
    {
        return currentJob;
    }


    // ----------------------------------------------------------
    /**
     * Access this worker's local editing context.
     * @return The editing context
     */
    protected EOEditingContext localContext()
    {
        if (ec == null)
        {
            ec = Application.newPeerEditingContext();
        }
        return ec;
    }


    // ----------------------------------------------------------
    /**
     * Notify the administrator and any other relevant personnel that the
     * current job has been suspended.  The job's "isPaused" flag has
     * already been set before this is called.
     */
    protected void sendJobSuspensionNotification()
    {
        // TODO: implement
    }


    // ----------------------------------------------------------
    /**
     * Waits for a candidate job to become available and tries to take
     * ownership of it. This method will not return until it successfully does
     * this, at which point the currentJob field will be set to that job.
     */
    protected void waitForAvailableJob()
    {
        boolean didGetJob = false;

        if (currentJob != null)
        {
            return;
        }

        do
        {
            // Get a candidate job for this thread to try to take ownership of.
            // A candidate will have a null worker relationship, meaning
            // nobody else successfully owns it yet.

            Job candidate = fetchNextCandidateJob();

            if (candidate == null)
            {
                // If there aren't any jobs currently available, wait
                // until something arrives in the queue
                logDebug("waiting for queue to wake me");
                QueueDescriptor.waitForNextJob(queueDescriptor().id());
                logDebug("woken by the queue");
            }
            else
            {
                // Try to take ownership of the job by setting the worker
                // relationship to our worker descriptor and then saving the
                // changes. If this succeeds, we own the job. If there is an
                // optimistic locking failure, then another thread got it
                // first, so we go back to the top of the loop and try to get
                // another job.

                WorkerDescriptor worker = (WorkerDescriptor)
                    descriptor().localInstanceIn(ec);

                logDebug("volunteering to run job " + candidate.id());
                didGetJob = candidate.volunteerToRun(worker);

                if (didGetJob)
                {
                    logDebug("successfully acquired job " + candidate.id());
                    currentJob = candidate;
                }
            }
        }
        while (!didGetJob);
    }


    // ----------------------------------------------------------
    /**
     * Fetch the next job that this thread will try to take ownership of.
     *
     * @return a job that doesn't currently have any worker threads owning it
     */
    @SuppressWarnings("unchecked")
    protected Job fetchNextCandidateJob()
    {
        String entityName = queueDescriptor().jobEntityName();
        EOFetchSpecification fetchSpec = new EOFetchSpecification(
                entityName,
                ERXQ.and(
                        ERXQ.isNull(JobBase.WORKER_KEY),
                        ERXQ.isFalse(JobBase.IS_CANCELLED_KEY),
                        ERXQ.isTrue(JobBase.IS_READY_KEY)),
                ERXS.sortOrders(JobBase.ENQUEUE_TIME_KEY, ERXS.ASC));
        fetchSpec.setFetchLimit(1);

        NSArray<Job> jobs = ec.objectsWithFetchSpecification(fetchSpec);

        if (jobs.count() == 0)
        {
            return null;
        }
        else
        {
            return jobs.objectAtIndex(0);
        }
    }


    //~ Private methods .......................................................

    // ----------------------------------------------------------
    /**
     * Fetches cancelled jobs from the queue and deletes them.
     */
    private void killCancelledJobs()
    {
        Job cancelledJob = fetchNextCancelledJob();

        while (cancelledJob != null)
        {
            cancelledJob.delete();

            // If there is an optimistic locking failure when we try to save
            // our changes, that's ok because another thread already cancelled
            // the job. Continue blissfully on by getting the next job.

            try
            {
                ec.saveChanges();
            }
            catch (Exception e)
            {
                ec.revert();
            }

            cancelledJob = fetchNextCancelledJob();
        }
    }


    // ----------------------------------------------------------
    /**
     * Retrieves cancelled jobs of the type handled by this worker thread.
     *
     * @return an array of cancelled jobs
     */
    @SuppressWarnings("unchecked")
    private Job fetchNextCancelledJob()
    {
        String entityName = queueDescriptor().jobEntityName();
        EOFetchSpecification fetchSpec = new EOFetchSpecification(
                entityName,
                ERXQ.and(
                        ERXQ.isNull(JobBase.WORKER_KEY),
                        ERXQ.isTrue(JobBase.IS_CANCELLED_KEY)),
                ERXS.sortOrders(JobBase.ENQUEUE_TIME_KEY, ERXS.ASC));
        fetchSpec.setFetchLimit(1);

        NSArray<Job> jobs = ec.objectsWithFetchSpecification(fetchSpec);

        if (jobs.count() == 0)
        {
            return null;
        }
        else
        {
            return jobs.objectAtIndex(0);
        }
    }


    // ----------------------------------------------------------
    private void logDebug(Object obj)
    {
        log.debug(queueDescriptor().jobEntityName()
                + " worker thread " + getId() + ": " + obj);
    }


    //~ Instance/static variables .............................................

    private Job                     currentJob;
    private ManagedQueueDescriptor  queueDescriptor;
    private ManagedHostDescriptor   hostDescriptor;
    private ManagedWorkerDescriptor descriptor;
    private EOEditingContext        ec;
    private boolean                 isCancelled;

    private static final Logger log = Logger.getLogger(WorkerThread.class);
}
