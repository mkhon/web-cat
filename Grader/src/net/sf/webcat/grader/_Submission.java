/*==========================================================================*\
 |  _Submission.java
 |*-------------------------------------------------------------------------*|
 |  Created by eogenerator
 |  DO NOT EDIT.  Make changes to Submission.java instead.
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006 Virginia Tech
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

package net.sf.webcat.grader;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eoaccess.*;
import java.util.Enumeration;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * An automatically generated EOGenericRecord subclass.  DO NOT EDIT.
 * To change, use EOModeler, or make additions in
 * Submission.java.
 *
 * @author Generated by eogenerator
 * @version version suppressed to control auto-generation
 */
public abstract class _Submission
    extends er.extensions.ERXGenericRecord
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new _Submission object.
     */
    public _Submission()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * A static factory method for creating a new
     * _Submission object given required
     * attributes and relationships.
     * @param editingContext The context in which the new object will be
     * inserted
     * @param partnerLink
     * @return The newly created object
     */
    public static Submission createSubmission(
        EOEditingContext editingContext,
        boolean partnerLink
        )
    {
        Submission eoObject = (Submission)
            EOUtilities.createAndInsertInstance(
                editingContext,
                _Submission.ENTITY_NAME);
        eoObject.setPartnerLink(partnerLink);
        return eoObject;
    }


    // ----------------------------------------------------------
    /**
     * Get a local instance of the given object in another editing context.
     * @param editingContext The target editing context
     * @param eo The object to import
     * @return An instance of the given object in the target editing context
     */
    public static Submission localInstance(
        EOEditingContext editingContext, Submission eo)
    {
        return (eo == null)
            ? null
            : (Submission)EOUtilities.localInstanceOfObject(
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
    public static Submission forId(
        EOEditingContext ec, int id )
    {
        Submission obj = null;
        if (id > 0)
        {
            NSArray results = EOUtilities.objectsMatchingKeyAndValue( ec,
                ENTITY_NAME, "id", new Integer( id ) );
            if ( results != null && results.count() > 0 )
            {
                obj = (Submission)results.objectAtIndex( 0 );
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
    public static Submission forId(
        EOEditingContext ec, String id )
    {
        return forId( ec, er.extensions.ERXValueUtilities.intValue( id ) );
    }


    //~ Constants (for key names) .............................................

    // Attributes ---
    public static final String FILE_NAME_KEY = "fileName";
    public static final String PARTNER_LINK_KEY = "partnerLink";
    public static final String SUBMIT_NUMBER_KEY = "submitNumber";
    public static final String SUBMIT_TIME_KEY = "submitTime";
    // To-one relationships ---
    public static final String ASSIGNMENT_OFFERING_KEY = "assignmentOffering";
    public static final String RESULT_KEY = "result";
    public static final String USER_KEY = "user";
    // To-many relationships ---
    public static final String ENQUEUED_JOBS_KEY = "enqueuedJobs";
    // Fetch specifications ---
    public static final String EARLIEST_FOR_COURSE_OFFERING_FSPEC = "earliestForCourseOffering";
    public static final String LATEST_FOR_COURSE_OFFERING_FSPEC = "latestForCourseOffering";
    public static final String ENTITY_NAME = "Submission";


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Get a local instance of this object in another editing context.
     * @param editingContext The target editing context
     * @return An instance of this object in the target editing context
     */
    public Submission localInstance(EOEditingContext editingContext)
    {
        return (Submission)EOUtilities.localInstanceOfObject(
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
            return er.extensions.ERXConstant.ZeroInteger;
        }
    }

    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>fileName</code> value.
     * @return the value of the attribute
     */
    public String fileName()
    {
        return (String)storedValueForKey( "fileName" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>fileName</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setFileName( String value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setFileName("
                + value + "): was " + fileName() );
        }
        takeStoredValueForKey( value, "fileName" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>partnerLink</code> value.
     * @return the value of the attribute
     */
    public boolean partnerLink()
    {
        Number result =
            (Number)storedValueForKey( "partnerLink" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>partnerLink</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setPartnerLink( boolean value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setPartnerLink("
                + value + "): was " + partnerLink() );
        }
        Number actual =
            er.extensions.ERXConstant.integerForInt( value ? 1 : 0 );
        setPartnerLinkRaw( actual );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>partnerLink</code> value.
     * @return the value of the attribute
     */
    public Number partnerLinkRaw()
    {
        return (Number)storedValueForKey( "partnerLink" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>partnerLink</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setPartnerLinkRaw( Number value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setPartnerLinkRaw("
                + value + "): was " + partnerLinkRaw() );
        }
        takeStoredValueForKey( value, "partnerLink" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>submitNumber</code> value.
     * @return the value of the attribute
     */
    public int submitNumber()
    {
        Number result =
            (Number)storedValueForKey( "submitNumber" );
        return ( result == null )
            ? 0
            : result.intValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>submitNumber</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setSubmitNumber( int value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setSubmitNumber("
                + value + "): was " + submitNumber() );
        }
        Number actual =
            er.extensions.ERXConstant.integerForInt( value );
        setSubmitNumberRaw( actual );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>submitNumber</code> value.
     * @return the value of the attribute
     */
    public Number submitNumberRaw()
    {
        return (Number)storedValueForKey( "submitNumber" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>submitNumber</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setSubmitNumberRaw( Number value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setSubmitNumberRaw("
                + value + "): was " + submitNumberRaw() );
        }
        takeStoredValueForKey( value, "submitNumber" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>submitTime</code> value.
     * @return the value of the attribute
     */
    public NSTimestamp submitTime()
    {
        return (NSTimestamp)storedValueForKey( "submitTime" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>submitTime</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setSubmitTime( NSTimestamp value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setSubmitTime("
                + value + "): was " + submitTime() );
        }
        takeStoredValueForKey( value, "submitTime" );
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
     * Retrieve the entity pointed to by the <code>result</code>
     * relationship.
     * @return the entity in the relationship
     */
    public net.sf.webcat.grader.SubmissionResult result()
    {
        return (net.sf.webcat.grader.SubmissionResult)storedValueForKey( "result" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>result</code>
     * relationship (DO NOT USE--instead, use
     * <code>setResultRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void setResult( net.sf.webcat.grader.SubmissionResult value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setResult("
                + value + "): was " + result() );
        }
        takeStoredValueForKey( value, "result" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>result</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setResultRelationship(
        net.sf.webcat.grader.SubmissionResult value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setResultRelationship("
                + value + "): was " + result() );
        }
        if ( value == null )
        {
            net.sf.webcat.grader.SubmissionResult object = result();
            if ( object != null )
                removeObjectFromBothSidesOfRelationshipWithKey( object, "result" );
        }
        else
        {
            addObjectToBothSidesOfRelationshipWithKey( value, "result" );
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
     * Retrieve the entities pointed to by the <code>enqueuedJobs</code>
     * relationship.
     * @return an NSArray of the entities in the relationship
     */
    public NSArray enqueuedJobs()
    {
        return (NSArray)storedValueForKey( "enqueuedJobs" );
    }


    // ----------------------------------------------------------
    /**
     * Replace the list of entities pointed to by the
     * <code>enqueuedJobs</code> relationship.
     *
     * @param value The new set of entities to relate to
     */
    public void setEnqueuedJobs( NSMutableArray value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setEnqueuedJobs("
                + value + "): was " + enqueuedJobs() );
        }
        takeStoredValueForKey( value, "enqueuedJobs" );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>enqueuedJobs</code>
     * relationship (DO NOT USE--instead, use
     * <code>addToEnqueuedJobsRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void addToEnqueuedJobs( net.sf.webcat.grader.EnqueuedJob value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "addToEnqueuedJobs("
                + value + "): was " + enqueuedJobs() );
        }
        NSMutableArray array = (NSMutableArray)enqueuedJobs();
        willChange();
        array.addObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>enqueuedJobs</code>
     * relationship (DO NOT USE--instead, use
     * <code>removeFromEnqueuedJobsRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromEnqueuedJobs( net.sf.webcat.grader.EnqueuedJob value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "RemoveFromEnqueuedJobs("
                + value + "): was " + enqueuedJobs() );
        }
        NSMutableArray array = (NSMutableArray)enqueuedJobs();
        willChange();
        array.removeObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>enqueuedJobs</code>
     * relationship.
     *
     * @param value The new entity to relate to
     */
    public void addToEnqueuedJobsRelationship( net.sf.webcat.grader.EnqueuedJob value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "addToEnqueuedJobsRelationship("
                + value + "): was " + enqueuedJobs() );
        }
        addObjectToBothSidesOfRelationshipWithKey(
            value, "enqueuedJobs" );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>enqueuedJobs</code>
     * relationship.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromEnqueuedJobsRelationship( net.sf.webcat.grader.EnqueuedJob value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "removeFromEnqueuedJobsRelationship("
                + value + "): was " + enqueuedJobs() );
        }
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "enqueuedJobs" );
    }


    // ----------------------------------------------------------
    /**
     * Create a brand new object that is a member of the
     * <code>enqueuedJobs</code> relationship.
     *
     * @return The new entity
     */
    public net.sf.webcat.grader.EnqueuedJob createEnqueuedJobsRelationship()
    {
        if (log.isDebugEnabled())
        {
            log.debug( "createEnqueuedJobsRelationship()" );
        }
        EOClassDescription eoClassDesc = EOClassDescription
            .classDescriptionForEntityName( "EnqueuedJob" );
        EOEnterpriseObject eoObject = eoClassDesc
            .createInstanceWithEditingContext( editingContext(), null );
        editingContext().insertObject( eoObject );
        addObjectToBothSidesOfRelationshipWithKey(
            eoObject, "enqueuedJobs" );
        return (net.sf.webcat.grader.EnqueuedJob)eoObject;
    }


    // ----------------------------------------------------------
    /**
     * Remove and then delete a specific entity that is a member of the
     * <code>enqueuedJobs</code> relationship.
     *
     * @param value The entity to remove from the relationship and then delete
     */
    public void deleteEnqueuedJobsRelationship( net.sf.webcat.grader.EnqueuedJob value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "deleteEnqueuedJobsRelationship("
                + value + "): was " + enqueuedJobs() );
        }
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "enqueuedJobs" );
        editingContext().deleteObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove (and then delete, if owned) all entities that are members of the
     * <code>enqueuedJobs</code> relationship.
     */
    public void deleteAllEnqueuedJobsRelationships()
    {
        if (log.isDebugEnabled())
        {
            log.debug( "deleteAllEnqueuedJobsRelationships(): was "
                + enqueuedJobs() );
        }
        Enumeration objects = enqueuedJobs().objectEnumerator();
        while ( objects.hasMoreElements() )
            deleteEnqueuedJobsRelationship(
                (net.sf.webcat.grader.EnqueuedJob)objects.nextElement() );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve object according to the <code>EarliestForCourseOffering</code>
     * fetch specification.
     *
     * @param context The editing context to use
     * @param courseOfferingBinding fetch spec parameter
     * @return an NSArray of the entities retrieved
     */
    public static NSArray objectsForEarliestForCourseOffering(
            EOEditingContext context,
            net.sf.webcat.core.CourseOffering courseOfferingBinding
        )
    {
        EOFetchSpecification spec = EOFetchSpecification
            .fetchSpecificationNamed( "earliestForCourseOffering", "Submission" );

        NSMutableDictionary bindings = new NSMutableDictionary();

        if ( courseOfferingBinding != null )
            bindings.setObjectForKey( courseOfferingBinding,
                                      "courseOffering" );
        spec = spec.fetchSpecificationWithQualifierBindings( bindings );

        NSArray result = context.objectsWithFetchSpecification( spec );
        if (log.isDebugEnabled())
        {
            log.debug( "objectsForEarliestForCourseOffering(ec"
            
                + ", " + courseOfferingBinding
                + "): " + result );
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Retrieve object according to the <code>LatestForCourseOffering</code>
     * fetch specification.
     *
     * @param context The editing context to use
     * @param courseOfferingBinding fetch spec parameter
     * @return an NSArray of the entities retrieved
     */
    public static NSArray objectsForLatestForCourseOffering(
            EOEditingContext context,
            net.sf.webcat.core.CourseOffering courseOfferingBinding
        )
    {
        EOFetchSpecification spec = EOFetchSpecification
            .fetchSpecificationNamed( "latestForCourseOffering", "Submission" );

        NSMutableDictionary bindings = new NSMutableDictionary();

        if ( courseOfferingBinding != null )
            bindings.setObjectForKey( courseOfferingBinding,
                                      "courseOffering" );
        spec = spec.fetchSpecificationWithQualifierBindings( bindings );

        NSArray result = context.objectsWithFetchSpecification( spec );
        if (log.isDebugEnabled())
        {
            log.debug( "objectsForLatestForCourseOffering(ec"
            
                + ", " + courseOfferingBinding
                + "): " + result );
        }
        return result;
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( Submission.class );
}
