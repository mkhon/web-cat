/*==========================================================================*\
 |  $Id: QueueDescriptor.java,v 1.7 2011/12/07 02:06:54 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2008-2009 Virginia Tech
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

package org.webcat.jobqueue;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.webcat.core.Application;
import org.webcat.woextensions.WCFetchSpecification;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import er.extensions.eof.ERXDefaultEditingContextDelegate;
import er.extensions.eof.ERXQ;
import er.extensions.eof.ERXS;

// -------------------------------------------------------------------------
/**
 * Represents and identifies a single database-backed queue of jobs that
 * descends from {@link JobBase}.  All queues that support parallel processing
 * over a cluster of servers should have a QueueDescriptor and be stored
 * in the database, which is the sole arbiter for concurrency control
 * among clustered servers.  All workers across an arbitrary number of
 * servers then operate on the same shared queue of jobs stored in the
 * database.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.7 $, $Date: 2011/12/07 02:06:54 $
 */
public class QueueDescriptor
    extends _QueueDescriptor
{
    /**
     * Determines the rate at which past data points in the (exponential)
     * moving average for job processing times "decay".  A value of 20
     * means the "half life" of the most recent job processing time is
     * approximately 20 jobs.  See the "S_t,alternate" and "EMA_today"
     * formulae under exponential moving averages on
     * http://en.wikipedia.org/wiki/Rolling_average.  The decay factor
     * is 1/alpha.
     */
    public static final double MOVING_AVERAGE_DECAY_FACTOR = 20.0;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new QueueDescriptor object.
     */
    public QueueDescriptor()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * Registers a queue in the database, if it has not already been
     * registered, and returns the associated descriptor.
     * @param context The editing context to use.
     * @param theJobEntityName The name of the {@link JobBase} subclass used
     *        to hold the queue's contents in the database.
     * @return The registered descriptor.
     */
    public static QueueDescriptor descriptorFor(
        EOEditingContext context, String theJobEntityName)
    {
        QueueDescriptor result = (QueueDescriptor)JobQueue.registerDescriptor(
            context,
            QueueDescriptor.ENTITY_NAME,
            new NSDictionary<String, String>(
                theJobEntityName,
                QueueDescriptor.JOB_ENTITY_NAME_KEY),
            new NSDictionary<String, Long>(
                new Long(0),
                QueueDescriptor.NEWEST_ENTRY_ID_KEY));
        synchronized (dispensers)
        {
            if (dispensers.get(result.id()) == null)
            {
                int initialTokenCount = 0;
                EOEditingContext ec = Application.newPeerEditingContext();
                try
                {
                    ec.lock();
                    initialTokenCount =
                        result.localInstance(ec).pendingJobCount(ec);
                }
                finally
                {
                    ec.unlock();
                }
                Application.releasePeerEditingContext(ec);
                dispensers.put(result.id(),
                    new TokenDispenser(initialTokenCount));
            }
        }
        return result;
    }

    // ----------------------------------------------------------
    /**
     * Retrieve a managed descriptor for a given job queue, registering
     * the queue if necessary.
     * @param theJobEntityName The name of the {@link JobBase} subclass used
     *        to hold the queue's contents in the database.
     * @return The managed descriptor.
     */
    public static ManagedQueueDescriptor managedDescriptorFor(
        EOEditingContext context, String theJobEntityName)
    {
        return new ManagedQueueDescriptor(
            descriptorFor(context, theJobEntityName));
    }


    // ----------------------------------------------------------
    /**
     * Registers a queue in the database, if it has not already been
     * registered.
     * @param theJobEntityName The name of the {@link JobBase} subclass used
     *        to hold the queue's contents in the database.
     */
    public static void registerQueue(String theJobEntityName)
    {
        EOEditingContext ec = queueContext();
        synchronized (ec)
        {
            ec.lock();
            try
            {
                descriptorFor(ec, theJobEntityName);
            }
            finally
            {
                ec.unlock();
            }
        }
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public int pendingJobCount(EOEditingContext ec)
    {
        EOFetchSpecification fetchSpec = new WCFetchSpecification<JobBase>(
            jobEntityName(),
            ERXQ.and(
                ERXQ.isFalse(JobBase.IS_CANCELLED_KEY),
                ERXQ.isTrue(JobBase.IS_READY_KEY)),
            null);
        NSArray<?> jobs = ec.objectsWithFetchSpecification(fetchSpec);
        return jobs.count();
    }


    // ----------------------------------------------------------
    /* package */ static void waitForNextJob(QueueDescriptor descriptor)
    {
        Number id = descriptor.id();
        assert id != null;
        TokenDispenser dispenser = null;
        synchronized (dispensers)
        {
            dispenser = dispensers.get(id);
            if (dispenser == null)
            {
                int initialTokenCount = 0;
                EOEditingContext ec = Application.newPeerEditingContext();
                try
                {
                    ec.lock();
                    initialTokenCount =
                        descriptor.localInstance(ec).pendingJobCount(ec);
                }
                finally
                {
                    ec.unlock();
                }
                Application.releasePeerEditingContext(ec);
                dispenser = new TokenDispenser(initialTokenCount);
                dispensers.put(id, dispenser);
            }
        }
        try
        {
            descriptor.editingContext().unlock();
            dispenser.getJobToken();
        }
        finally
        {
            descriptor.editingContext().lock();
        }
    }


    // ----------------------------------------------------------
    /* package */ static void newJobIsReadyOn(QueueDescriptor descriptor)
    {
        Number id = descriptor.id();
        assert id != null;
        TokenDispenser dispenser = null;
        synchronized (dispensers)
        {
            dispenser = dispensers.get(id);
            if (dispenser == null)
            {
                int initialTokenCount = 0;
                EOEditingContext ec = Application.newPeerEditingContext();
                try
                {
                    ec.lock();
                    initialTokenCount =
                        descriptor.localInstance(ec).pendingJobCount(ec);
                }
                finally
                {
                    ec.unlock();
                }
                Application.releasePeerEditingContext(ec);
                dispenser = new TokenDispenser(initialTokenCount);
                dispensers.put(id, dispenser);
            }
            dispenser.depositToken();
        }
    }


    // ----------------------------------------------------------
    // Used by QueueDelegate
    private static EOEditingContext queueContext()
    {
        return _ec;
    }


    // ----------------------------------------------------------
    /**
     * This class needs to be public as an implementation side-effect so that
     * WebObjects' NSSelector can access the delegate methods.
     */
    public static class QueueDelegate
        extends ERXDefaultEditingContextDelegate
    {
        // ----------------------------------------------------------
        public QueueDelegate()
        {
            // nothing to do
        }

        // ----------------------------------------------------------
        public void editingContextDidMergeChanges(EOEditingContext context)
        {
            if (jobContext == null)
            {
                jobContext = Application.newPeerEditingContext();
            }
            synchronized (dispensers)
            {
                try
                {
                    jobContext.lock();
                    for (Number id : dispensers.keySet())
                    {
                        QueueDescriptor descriptor =
                            forId(jobContext, id.intValue());
                        dispensers.get(id).ensureAtLeastNTokens(
                            descriptor.pendingJobCount(jobContext));
                    }
                }
                finally
                {
                    jobContext.unlock();
                }
            }
        }

        private EOEditingContext jobContext;
    }


    //~ Instance/static variables .............................................

    private static EOEditingContext _ec;
    static {
        _ec = Application.newPeerEditingContext();
        _ec.setDelegate(new QueueDelegate());
    }

    // Accessed by inner QueueDelegate
    /* package */ static Map<Number, TokenDispenser> dispensers =
        new HashMap<Number, TokenDispenser>();

    static Logger log = Logger.getLogger(QueueDescriptor.class);
}
