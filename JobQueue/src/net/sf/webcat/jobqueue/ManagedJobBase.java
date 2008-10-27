/*==========================================================================*\
 |  $Id: ManagedJobBase.java,v 1.1 2008/10/27 01:53:16 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2008 Virginia Tech
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

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.util.Enumeration;
import org.apache.log4j.Logger;
import net.sf.webcat.core.IndependentEOManager;

// -------------------------------------------------------------------------
/**
 * A subclass of IndependentEOManager that holds one {@link JobBase}.
 *
 * @author stedwar2
 * @version $Id: ManagedJobBase.java,v 1.1 2008/10/27 01:53:16 stedwar2 Exp $
 */
public abstract class ManagedJobBase
    extends IndependentEOManager
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     * @param job The job to wrap
     */
    public ManagedJobBase(JobBase job)
    {
        super(job);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>enqueueTime</code> value.
     * @return the value of the attribute
     */
    public NSTimestamp enqueueTime()
    {
        return (NSTimestamp)valueForKey(JobBase.ENQUEUE_TIME_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>enqueueTime</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setEnqueueTime( NSTimestamp value )
    {
        takeValueForKey(value, JobBase.ENQUEUE_TIME_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>isCancelled</code> value.
     * @return the value of the attribute
     */
    public boolean isCancelled()
    {
        Number result =
            (Number)valueForKey(JobBase.IS_CANCELLED_KEY);
        return (result == null)
            ? false
            : (result.intValue() > 0);
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>isCancelled</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setIsCancelled( boolean value )
    {
        takeValueForKey(
            er.extensions.ERXConstant.integerForInt(value ? 1 : 0),
            JobBase.IS_CANCELLED_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>isPaused</code> value.
     * @return the value of the attribute
     */
    public boolean isPaused()
    {
        Number result =
            (Number)valueForKey(JobBase.IS_PAUSED_KEY);
        return (result == null)
            ? false
            : (result.intValue() > 0);
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>isPaused</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setIsPaused( boolean value )
    {
        takeValueForKey(
            er.extensions.ERXConstant.integerForInt(value ? 1 : 0),
            JobBase.IS_PAUSED_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>priority</code> value.
     * @return the value of the attribute
     */
    public int priority()
    {
        Number result =
            (Number)valueForKey(JobBase.PRIORITY_KEY);
        return (result == null)
            ? 0
            : result.intValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>priority</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setPriority(int value)
    {
        takeValueForKey(
            er.extensions.ERXConstant.integerForInt(value),
            JobBase.PRIORITY_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>scheduledTime</code> value.
     * @return the value of the attribute
     */
    public NSTimestamp scheduledTime()
    {
        return (NSTimestamp)valueForKey(JobBase.SCHEDULED_TIME_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>scheduledTime</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setScheduledTime(NSTimestamp value)
    {
        takeValueForKey(value, JobBase.SCHEDULED_TIME_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>user</code>
     * relationship.
     * @return the entity in the relationship
     */
    public net.sf.webcat.core.User user()
    {
        return (net.sf.webcat.core.User)valueForKey(JobBase.USER_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>user</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setUserRelationship(net.sf.webcat.core.User value)
    {
        addObjectToBothSidesOfRelationshipWithKey(value, JobBase.USER_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>worker</code>
     * relationship.
     * @return the entity in the relationship
     */
    public WorkerDescriptor worker()
    {
        return (WorkerDescriptor)valueForKey(JobBase.WORKER_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>worker</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setWorkerRelationship(WorkerDescriptor value)
    {
        addObjectToBothSidesOfRelationshipWithKey(value, JobBase.WORKER_KEY);
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( JobBase.class );
}
