/*==========================================================================*\
 |  $Id: QueueDescriptor.java,v 1.3 2009/11/13 17:12:05 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006 Virginia Tech
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

import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
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
 * @author
 * @version $Id: QueueDescriptor.java,v 1.3 2009/11/13 17:12:05 stedwar2 Exp $
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
        return (QueueDescriptor)JobQueue.registerDescriptor(
            context,
            QueueDescriptor.ENTITY_NAME,
            new NSDictionary<String, String>(
                jobEntityName,
                QueueDescriptor.JOB_ENTITY_NAME_KEY),
            new NSDictionary<String, Long>(
                new Long(0),
                QueueDescriptor.NEWEST_ENTRY_ID_KEY));
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
        String jobEntityName)
    {
        EOEditingContext ec = Application.newPeerEditingContext();
        ManagedQueueDescriptor result = new ManagedQueueDescriptor(
            descriptorFor(ec, jobEntityName));
        Application.releasePeerEditingContext(ec);
        return result;
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
        EOEditingContext ec = Application.newPeerEditingContext();
        descriptorFor(ec, jobEntityName);
        Application.releasePeerEditingContext(ec);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public String toString()
    {
        return userPresentableDescription();
    }
}
