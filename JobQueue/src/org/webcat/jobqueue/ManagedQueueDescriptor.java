/*==========================================================================*\
 |  $Id: ManagedQueueDescriptor.java,v 1.2 2010/09/27 00:30:22 stedwar2 Exp $
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

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import er.extensions.eof.ERXConstant;

import java.util.Enumeration;
import org.apache.log4j.Logger;
import org.webcat.core.IndependentEOManager;

// -------------------------------------------------------------------------
/**
 * A subclass of IndependentEOManager that holds one {@link QueueDescriptor}.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.2 $, $Date: 2010/09/27 00:30:22 $
 */
public class ManagedQueueDescriptor
    extends IndependentEOManager
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     * @param descriptor the queue descriptor to wrap
     */
    public ManagedQueueDescriptor(QueueDescriptor descriptor)
    {
        super(descriptor);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>defaultJobWait</code> value.
     * @return the value of the attribute
     */
    public long defaultJobWait()
    {
        Number result =
            (Number)valueForKey(QueueDescriptor.DEFAULT_JOB_WAIT_KEY);
        return (result == null)
            ? 0L
            : result.longValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>defaultJobWait</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setDefaultJobWait(long value)
    {
        takeValueForKey(new Long(value), QueueDescriptor.DEFAULT_JOB_WAIT_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>jobCount</code> value.
     * @return the value of the attribute
     */
    public long jobCount()
    {
        Number result =
            (Number)valueForKey(QueueDescriptor.JOB_COUNT_KEY);
        return (result == null)
            ? 0L
            : result.longValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>jobCount</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setJobCount(long value)
    {
        takeValueForKey(new Long(value), QueueDescriptor.JOB_COUNT_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>jobEntityName</code> value.
     * @return the value of the attribute
     */
    public String jobEntityName()
    {
        return (String)valueForKey(QueueDescriptor.JOB_ENTITY_NAME_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>jobEntityName</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setJobEntityName(String value)
    {
        takeValueForKey(value, QueueDescriptor.JOB_ENTITY_NAME_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>jobsCountedWithWaits</code> value.
     * @return the value of the attribute
     */
    public long jobsCountedWithWaits()
    {
        Number result =
            (Number)valueForKey(QueueDescriptor.JOBS_COUNTED_WITH_WAITS_KEY);
        return (result == null)
            ? 0L
            : result.longValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>jobsCountedWithWaits</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setJobsCountedWithWaits(long value)
    {
        takeValueForKey(
            new Long(value), QueueDescriptor.JOBS_COUNTED_WITH_WAITS_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>mostRecentJobWait</code> value.
     * @return the value of the attribute
     */
    public long mostRecentJobWait()
    {
        Number result =
            (Number)valueForKey(QueueDescriptor.MOST_RECENT_JOB_WAIT_KEY);
        return (result == null)
            ? 0L
            : result.longValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>mostRecentJobWait</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setMostRecentJobWait(long value)
    {
        takeValueForKey(
            new Long(value), QueueDescriptor.MOST_RECENT_JOB_WAIT_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>newestEntryId</code> value.
     * @return the value of the attribute
     */
    public long newestEntryId()
    {
        Number result =
            (Number)valueForKey(QueueDescriptor.NEWEST_ENTRY_ID_KEY);
        return (result == null)
            ? 0L
            : result.longValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>newestEntryId</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setNewestEntryId(long value)
    {
        takeValueForKey(
            new Long(value), QueueDescriptor.NEWEST_ENTRY_ID_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>requiresExclusiveHostAccess</code> value.
     * @return the value of the attribute
     */
    public boolean requiresExclusiveHostAccess()
    {
        Number result = (Number)valueForKey(
            QueueDescriptor.REQUIRES_EXCLUSIVE_HOST_ACCESS_KEY);
        return (result == null)
            ? false
            : (result.intValue() > 0);
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>requiresExclusiveHostAccess</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setRequiresExclusiveHostAccess(boolean value)
    {
        takeValueForKey(
            ERXConstant.integerForInt(value ? 1 : 0),
            QueueDescriptor.REQUIRES_EXCLUSIVE_HOST_ACCESS_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>totalWaitForJobs</code> value.
     * @return the value of the attribute
     */
    public long totalWaitForJobs()
    {
        Number result =
            (Number)valueForKey(QueueDescriptor.TOTAL_WAIT_FOR_JOBS_KEY);
        return (result == null)
            ? 0L
            : result.longValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>totalWaitForJobs</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setTotalWaitForJobs(long value)
    {
        takeValueForKey(
            new Long(value), QueueDescriptor.TOTAL_WAIT_FOR_JOBS_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entities pointed to by the <code>workers</code>
     * relationship.
     * @return an NSArray of the entities in the relationship
     */
    public NSArray workers()
    {
        return (NSArray)valueForKey(QueueDescriptor.WORKERS_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>workers</code>
     * relationship.
     *
     * @param value The new entity to relate to
     */
    public void addToWorkersRelationship(WorkerDescriptor value)
    {
        addObjectToBothSidesOfRelationshipWithKey(
            value, QueueDescriptor.WORKERS_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>workers</code>
     * relationship.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromWorkersRelationship(WorkerDescriptor value)
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, QueueDescriptor.WORKERS_KEY);
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( QueueDescriptor.class );
}
