/*==========================================================================*\
 |  _SurveyReminderJob.java
 |*-------------------------------------------------------------------------*|
 |  Created by eogenerator
 |  DO NOT EDIT.  Make changes to SurveyReminderJob.java instead.
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

package net.sf.webcat.opinions;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.util.Enumeration;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * An automatically generated EOGenericRecord subclass.  DO NOT EDIT.
 * To change, use EOModeler, or make additions in
 * SurveyReminderJob.java.
 *
 * @author Generated by eogenerator
 * @version version suppressed to control auto-generation
 */
public abstract class _SurveyReminderJob
    extends net.sf.webcat.jobqueue.JobBase
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new _SurveyReminderJob object.
     */
    public _SurveyReminderJob()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * A static factory method for creating a new
     * _SurveyReminderJob object given required
     * attributes and relationships.
     * @param editingContext The context in which the new object will be
     * inserted
     * @param enqueueTime
     * @param isCancelled
     * @param isPaused
     * @param priority
     * @return The newly created object
     */
    public static SurveyReminderJob create(
        EOEditingContext editingContext,
        NSTimestamp enqueueTime,
        boolean isCancelled,
        boolean isPaused,
        int priority
        )
    {
        SurveyReminderJob eoObject = (SurveyReminderJob)
            EOUtilities.createAndInsertInstance(
                editingContext,
                _SurveyReminderJob.ENTITY_NAME);
        eoObject.setEnqueueTime(enqueueTime);
        eoObject.setIsCancelled(isCancelled);
        eoObject.setIsPaused(isPaused);
        eoObject.setPriority(priority);
        return eoObject;
    }


    // ----------------------------------------------------------
    /**
     * Get a local instance of the given object in another editing context.
     * @param editingContext The target editing context
     * @param eo The object to import
     * @return An instance of the given object in the target editing context
     */
    public static SurveyReminderJob localInstance(
        EOEditingContext editingContext, SurveyReminderJob eo)
    {
        return (eo == null)
            ? null
            : (SurveyReminderJob)EOUtilities.localInstanceOfObject(
                editingContext, eo);
    }


    // ----------------------------------------------------------
    /**
     * Look up an object by id number.  Assumes the editing
     * context is appropriately locked.
     * @param ec The editing context to use
     * @param id The id to look up
     * @return The object, or null if no such id exists
     */
    public static SurveyReminderJob forId(
        EOEditingContext ec, int id )
    {
        SurveyReminderJob obj = null;
        if (id > 0)
        {
            NSArray results = EOUtilities.objectsMatchingKeyAndValue( ec,
                ENTITY_NAME, "id", new Integer( id ) );
            if ( results != null && results.count() > 0 )
            {
                obj = (SurveyReminderJob)results.objectAtIndex( 0 );
            }
        }
        return obj;
    }


    // ----------------------------------------------------------
    /**
     * Look up an object by id number.  Assumes the editing
     * context is appropriately locked.
     * @param ec The editing context to use
     * @param id The id to look up
     * @return The object, or null if no such id exists
     */
    public static SurveyReminderJob forId(
        EOEditingContext ec, String id )
    {
        return forId( ec, er.extensions.foundation.ERXValueUtilities.intValue( id ) );
    }


    //~ Constants (for key names) .............................................

    // Attributes ---
    public static final String DUE_TIME_KEY = "dueTime";
    public static final String ENQUEUE_TIME_KEY = "enqueueTime";
    public static final String IS_CANCELLED_KEY = "isCancelled";
    public static final String IS_PAUSED_KEY = "isPaused";
    public static final String PRIORITY_KEY = "priority";
    public static final String SCHEDULED_TIME_KEY = "scheduledTime";
    // To-one relationships ---
    public static final String ASSIGNMENT_OFFERING_KEY = "assignmentOffering";
    public static final String USER_KEY = "user";
    public static final String WORKER_KEY = "worker";
    // To-many relationships ---
    // Fetch specifications ---
    public static final String ENTITY_NAME = "SurveyReminderJob";


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Get a local instance of this object in another editing context.
     * @param editingContext The target editing context
     * @return An instance of this object in the target editing context
     */
    public SurveyReminderJob localInstance(EOEditingContext editingContext)
    {
        return (SurveyReminderJob)EOUtilities.localInstanceOfObject(
            editingContext, this);
    }


    // ----------------------------------------------------------
    /**
     * Get a list of changes between this object's current state and the
     * last committed version.
     * @return a dictionary of the changes that have not yet been committed
     */
    public NSDictionary changedProperties()
    {
        return changesFromSnapshot(
            editingContext().committedSnapshotForObject(this) );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>id</code> value.
     * @return the value of the attribute
     */
    public Number id()
    {
        try
        {
            return (Number)EOUtilities.primaryKeyForObject(
                editingContext() , this ).objectForKey( "id" );
        }
        catch (Exception e)
        {
            return er.extensions.eof.ERXConstant.ZeroInteger;
        }
    }

    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>dueTime</code> value.
     * @return the value of the attribute
     */
    public NSTimestamp dueTime()
    {
        return (NSTimestamp)storedValueForKey( "dueTime" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>dueTime</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setDueTime( NSTimestamp value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setDueTime("
                + value + "): was " + dueTime() );
        }
        takeStoredValueForKey( value, "dueTime" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>enqueueTime</code> value.
     * @return the value of the attribute
     */
    public NSTimestamp enqueueTime()
    {
        return (NSTimestamp)storedValueForKey( "enqueueTime" );
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
        if (log.isDebugEnabled())
        {
            log.debug( "setEnqueueTime("
                + value + "): was " + enqueueTime() );
        }
        takeStoredValueForKey( value, "enqueueTime" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>isCancelled</code> value.
     * @return the value of the attribute
     */
    public boolean isCancelled()
    {
        Integer result =
            (Integer)storedValueForKey( "isCancelled" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
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
        if (log.isDebugEnabled())
        {
            log.debug( "setIsCancelled("
                + value + "): was " + isCancelled() );
        }
        Integer actual =
            er.extensions.eof.ERXConstant.integerForInt( value ? 1 : 0 );
            setIsCancelledRaw( actual );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>isCancelled</code> value.
     * @return the value of the attribute
     */
    public Integer isCancelledRaw()
    {
        return (Integer)storedValueForKey( "isCancelled" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>isCancelled</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setIsCancelledRaw( Integer value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setIsCancelledRaw("
                + value + "): was " + isCancelledRaw() );
        }
        takeStoredValueForKey( value, "isCancelled" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>isPaused</code> value.
     * @return the value of the attribute
     */
    public boolean isPaused()
    {
        Integer result =
            (Integer)storedValueForKey( "isPaused" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
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
        if (log.isDebugEnabled())
        {
            log.debug( "setIsPaused("
                + value + "): was " + isPaused() );
        }
        Integer actual =
            er.extensions.eof.ERXConstant.integerForInt( value ? 1 : 0 );
            setIsPausedRaw( actual );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>isPaused</code> value.
     * @return the value of the attribute
     */
    public Integer isPausedRaw()
    {
        return (Integer)storedValueForKey( "isPaused" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>isPaused</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setIsPausedRaw( Integer value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setIsPausedRaw("
                + value + "): was " + isPausedRaw() );
        }
        takeStoredValueForKey( value, "isPaused" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>priority</code> value.
     * @return the value of the attribute
     */
    public int priority()
    {
        Integer result =
            (Integer)storedValueForKey( "priority" );
        return ( result == null )
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
    public void setPriority( int value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setPriority("
                + value + "): was " + priority() );
        }
        Integer actual =
            er.extensions.eof.ERXConstant.integerForInt( value );
            setPriorityRaw( actual );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>priority</code> value.
     * @return the value of the attribute
     */
    public Integer priorityRaw()
    {
        return (Integer)storedValueForKey( "priority" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>priority</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setPriorityRaw( Integer value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setPriorityRaw("
                + value + "): was " + priorityRaw() );
        }
        takeStoredValueForKey( value, "priority" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>scheduledTime</code> value.
     * @return the value of the attribute
     */
    public NSTimestamp scheduledTime()
    {
        return (NSTimestamp)storedValueForKey( "scheduledTime" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>scheduledTime</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setScheduledTime( NSTimestamp value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setScheduledTime("
                + value + "): was " + scheduledTime() );
        }
        takeStoredValueForKey( value, "scheduledTime" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>assignmentOffering</code>
     * relationship.
     * @return the entity in the relationship
     */
    public net.sf.webcat.grader.AssignmentOffering assignmentOffering()
    {
        return (net.sf.webcat.grader.AssignmentOffering)storedValueForKey( "assignmentOffering" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>assignmentOffering</code>
     * relationship (DO NOT USE--instead, use
     * <code>setAssignmentOfferingRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void setAssignmentOffering( net.sf.webcat.grader.AssignmentOffering value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setAssignmentOffering("
                + value + "): was " + assignmentOffering() );
        }
        takeStoredValueForKey( value, "assignmentOffering" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>assignmentOffering</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setAssignmentOfferingRelationship(
        net.sf.webcat.grader.AssignmentOffering value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setAssignmentOfferingRelationship("
                + value + "): was " + assignmentOffering() );
        }
        if ( value == null )
        {
            net.sf.webcat.grader.AssignmentOffering object = assignmentOffering();
            if ( object != null )
                removeObjectFromBothSidesOfRelationshipWithKey( object, "assignmentOffering" );
        }
        else
        {
            addObjectToBothSidesOfRelationshipWithKey( value, "assignmentOffering" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>user</code>
     * relationship.
     * @return the entity in the relationship
     */
    public net.sf.webcat.core.User user()
    {
        return (net.sf.webcat.core.User)storedValueForKey( "user" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>user</code>
     * relationship (DO NOT USE--instead, use
     * <code>setUserRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void setUser( net.sf.webcat.core.User value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setUser("
                + value + "): was " + user() );
        }
        takeStoredValueForKey( value, "user" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>user</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setUserRelationship(
        net.sf.webcat.core.User value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setUserRelationship("
                + value + "): was " + user() );
        }
        if ( value == null )
        {
            net.sf.webcat.core.User object = user();
            if ( object != null )
                removeObjectFromBothSidesOfRelationshipWithKey( object, "user" );
        }
        else
        {
            addObjectToBothSidesOfRelationshipWithKey( value, "user" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>worker</code>
     * relationship.
     * @return the entity in the relationship
     */
    public net.sf.webcat.jobqueue.WorkerDescriptor worker()
    {
        return (net.sf.webcat.jobqueue.WorkerDescriptor)storedValueForKey( "worker" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>worker</code>
     * relationship (DO NOT USE--instead, use
     * <code>setWorkerRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void setWorker( net.sf.webcat.jobqueue.WorkerDescriptor value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setWorker("
                + value + "): was " + worker() );
        }
        takeStoredValueForKey( value, "worker" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>worker</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setWorkerRelationship(
        net.sf.webcat.jobqueue.WorkerDescriptor value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setWorkerRelationship("
                + value + "): was " + worker() );
        }
        if ( value == null )
        {
            net.sf.webcat.jobqueue.WorkerDescriptor object = worker();
            if ( object != null )
                removeObjectFromBothSidesOfRelationshipWithKey( object, "worker" );
        }
        else
        {
            addObjectToBothSidesOfRelationshipWithKey( value, "worker" );
        }
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( SurveyReminderJob.class );
}