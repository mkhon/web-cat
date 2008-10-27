/*==========================================================================*\
 |  $Id: WorkerThread.java,v 1.1 2008/10/27 01:53:16 stedwar2 Exp $
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

package net.sf.webcat.jobqueue;

import com.webobjects.eocontrol.EOEditingContext;
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
 * @version $Id: WorkerThread.java,v 1.1 2008/10/27 01:53:16 stedwar2 Exp $
 */
public abstract class WorkerThread<Job extends JobBase>
    extends Thread
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     * @param workerDescriptor the descriptor for this worker thread
     */
    public WorkerThread(WorkerDescriptor workerDescriptor)
    {
        descriptor = new ManagedWorkerDescriptor(
            workerDescriptor.localInstance(localContext()));
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
    public final void run()
    {
        // ...
        //repeat forever
            // lock the ec
            // while there are cancelled jobs
                // attempt kill each one in turn
                // protect against optimistic locking failures

            // get first available job
            // if none are available, wait ...

            // process job

            // compile wait statistics
            // remove job
            // update wait statistics
            // unlock the ec
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


    //~ Instance/static variables .............................................

    private Job                     currentJob;
    private ManagedQueueDescriptor  queueDescriptor;
    private ManagedHostDescriptor   hostDescriptor;
    private ManagedWorkerDescriptor descriptor;
    private EOEditingContext        ec;
}
