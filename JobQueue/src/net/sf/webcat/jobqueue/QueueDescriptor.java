/*==========================================================================*\
 |  $Id: QueueDescriptor.java,v 1.6 2009/11/17 18:21:19 stedwar2 Exp $
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

package net.sf.webcat.jobqueue;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import er.extensions.eof.ERXDefaultEditingContextDelegate;
import net.sf.webcat.core.Application;

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
 * @author Stephen Edwards
 * @author Last changed by $Author: stedwar2 $
 * @version $Revision: 1.6 $, $Date: 2009/11/17 18:21:19 $
 */
public class QueueDescriptor
    extends _QueueDescriptor
{
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
     * @param jobEntityName The name of the {@link JobBase} subclass used
     *        to hold the queue's contents in the database.
     * @return The registered descriptor.
     */
    public static QueueDescriptor descriptorFor(
        EOEditingContext context, String jobEntityName)
    {
        QueueDescriptor result = (QueueDescriptor)JobQueue.registerDescriptor(
            context,
            QueueDescriptor.ENTITY_NAME,
            new NSDictionary<String, String>(
                jobEntityName,
                QueueDescriptor.JOB_ENTITY_NAME_KEY),
            new NSDictionary<String, Long>(
                new Long(0),
                QueueDescriptor.NEWEST_ENTRY_ID_KEY));
        if (context != queueContext())
        {
            result.localInstance(queueContext());
        }
        synchronized (dispensers)
        {
            if (dispensers.get(result.id()) == null)
            {
                dispensers.put(result.id(), new TokenDispenser());
            }
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Retrieve a managed descriptor for a given job queue, registering
     * the queue if necessary.
     * @param jobEntityName The name of the {@link JobBase} subclass used
     *        to hold the queue's contents in the database.
     * @return The managed descriptor.
     */
    public static ManagedQueueDescriptor managedDescriptorFor(
        EOEditingContext context, String jobEntityName)
    {
        return new ManagedQueueDescriptor(
            descriptorFor(context, jobEntityName));
    }


    // ----------------------------------------------------------
    /**
     * Registers a queue in the database, if it has not already been
     * registered.
     * @param jobEntityName The name of the {@link JobBase} subclass used
     *        to hold the queue's contents in the database.
     */
    public static void registerQueue(String jobEntityName)
    {
        EOEditingContext ec = queueContext();
        ec.lock();
        try
        {
            descriptorFor(ec, jobEntityName);
        }
        finally
        {
            ec.unlock();
        }
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /* package */ static void waitForNextJob(QueueDescriptor descriptor)
    {
        assert descriptor.editingContext() != null;
        if (descriptor.editingContext() != queueContext())
        {
            descriptor.localInstance(queueContext());
        }
        waitForNextJob(descriptor.id());
    }


    // ----------------------------------------------------------
    /* package */ static void waitForNextJob(Number descriptorId)
    {
        assert descriptorId != null;
        TokenDispenser dispenser = null;
        synchronized (dispensers)
        {
            dispenser = dispensers.get(descriptorId);
            if (dispenser == null)
            {
                dispenser = new TokenDispenser();
                dispensers.put(descriptorId, dispenser);
            }
        }
        dispenser.getJobToken();
    }


    // ----------------------------------------------------------
    /* package */ static void newJobIsReadyOn(QueueDescriptor descriptor)
    {
        Number id = descriptor.id();
        assert id != null;
        if (descriptor.editingContext() != queueContext())
        {
            descriptor.localInstance(queueContext());
        }
        TokenDispenser dispenser = null;
        synchronized (dispensers)
        {
            dispenser = dispensers.get(id);
            if (dispenser == null)
            {
                dispenser = new TokenDispenser();
                dispensers.put(id, dispenser);
            }
        }
        dispenser.depositToken();
    }


    // ----------------------------------------------------------
    private static EOEditingContext queueContext()
    {
        if (_ec == null)
        {
            _ec = Application.newPeerEditingContext();
            _ec.setDelegate(new QueueDelegate());
        }
        return _ec;
    }


    // ----------------------------------------------------------
    private static class QueueDelegate
        extends ERXDefaultEditingContextDelegate
    {
        // ----------------------------------------------------------
        public QueueDelegate()
        {
            // TBD
        }

    }


    //~ Instance/static variables .............................................

    private static EOEditingContext _ec;
    private static Map<Number, TokenDispenser> dispensers =
        new HashMap<Number, TokenDispenser>();
    static Logger log = Logger.getLogger(QueueDescriptor.class);
}
