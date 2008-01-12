/*==========================================================================*\
 |  _Assignment.java
 |*-------------------------------------------------------------------------*|
 |  Created by eogenerator
 |  DO NOT EDIT.  Make changes to Assignment.java instead.
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
import java.util.Enumeration;

// -------------------------------------------------------------------------
/**
 * An automatically generated EOGenericRecord subclass.  DO NOT EDIT.
 * To change, use EOModeler, or make additions in
 * Assignment.java.
 *
 * @author Generated by eogenerator
 * @version version suppressed to control auto-generation
 */
public abstract class _Assignment
    extends er.extensions.ERXGenericRecord
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new _Assignment object.
     */
    public _Assignment()
    {
        super();
    }


    //~ Constants (for key names) .............................................

    // Attributes ---
    public static final String FILE_UPLOAD_MESSAGE_KEY = "fileUploadMessage";
    public static final String MOODLE_ID_KEY = "moodleId";
    public static final String NAME_KEY = "name";
    public static final String SHORT_DESCRIPTION_KEY = "shortDescription";
    public static final String URL_KEY = "url";
    // To-one relationships ---
    public static final String AUTHOR_KEY = "author";
    public static final String GRADING_CRITERIA_KEY = "gradingCriteria";
    public static final String SUBMISSION_PROFILE_KEY = "submissionProfile";
    // To-many relationships ---
    public static final String OFFERINGS_KEY = "offerings";
    public static final String STEPS_KEY = "steps";
    // Fetch specifications ---
    public static final String NEIGHBOR_ASSIGNMENTS_FSPEC = "neighborAssignments";
    public static final String REUSE_IN_COURSE_FSPEC = "reuseInCourse";
    public static final String ENTITY_NAME = "Assignment";


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>fileUploadMessage</code> value.
     * @return the value of the attribute
     */
    public String fileUploadMessage()
    {
        return (String)storedValueForKey( "fileUploadMessage" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>fileUploadMessage</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setFileUploadMessage( String value )
    {
        takeStoredValueForKey( value, "fileUploadMessage" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>moodleId</code> value.
     * @return the value of the attribute
     */
    public Number moodleId()
    {
        return (Number)storedValueForKey( "moodleId" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>moodleId</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setMoodleId( Number value )
    {
        takeStoredValueForKey( value, "moodleId" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>name</code> value.
     * @return the value of the attribute
     */
    public String name()
    {
        return (String)storedValueForKey( "name" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>name</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setName( String value )
    {
        takeStoredValueForKey( value, "name" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>shortDescription</code> value.
     * @return the value of the attribute
     */
    public String shortDescription()
    {
        return (String)storedValueForKey( "shortDescription" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>shortDescription</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setShortDescription( String value )
    {
        takeStoredValueForKey( value, "shortDescription" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>url</code> value.
     * @return the value of the attribute
     */
    public String url()
    {
        return (String)storedValueForKey( "url" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>url</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setUrl( String value )
    {
        takeStoredValueForKey( value, "url" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve object according to the <code>NeighborAssignments</code>
     * fetch specification.
     *
     * @param context The editing context to use
     * @param courseOfferingBinding fetch spec parameter
     * @return an NSArray of the entities retrieved
     */
    public static NSArray objectsForNeighborAssignments(
            EOEditingContext context,
            net.sf.webcat.core.CourseOffering courseOfferingBinding
        )
    {
        EOFetchSpecification spec = EOFetchSpecification
            .fetchSpecificationNamed( "neighborAssignments", "Assignment" );

        NSMutableDictionary bindings = new NSMutableDictionary();

        if ( courseOfferingBinding != null )
            bindings.setObjectForKey( courseOfferingBinding,
                                      "courseOffering" );
        spec = spec.fetchSpecificationWithQualifierBindings( bindings );

        return context.objectsWithFetchSpecification( spec );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve object according to the <code>ReuseInCourse</code>
     * fetch specification.
     *
     * @param context The editing context to use
     * @param courseBinding fetch spec parameter
     * @param courseOfferingBinding fetch spec parameter
     * @return an NSArray of the entities retrieved
     */
    public static NSArray objectsForReuseInCourse(
            EOEditingContext context,
            net.sf.webcat.core.Course courseBinding,
            net.sf.webcat.core.CourseOffering courseOfferingBinding
        )
    {
        EOFetchSpecification spec = EOFetchSpecification
            .fetchSpecificationNamed( "reuseInCourse", "Assignment" );

        NSMutableDictionary bindings = new NSMutableDictionary();

        if ( courseBinding != null )
            bindings.setObjectForKey( courseBinding,
                                      "course" );
        if ( courseOfferingBinding != null )
            bindings.setObjectForKey( courseOfferingBinding,
                                      "courseOffering" );
        spec = spec.fetchSpecificationWithQualifierBindings( bindings );

        return context.objectsWithFetchSpecification( spec );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>author</code>
     * relationship.
     * @return the entity in the relationship
     */
    public net.sf.webcat.core.User author()
    {
        return (net.sf.webcat.core.User)storedValueForKey( "author" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>author</code>
     * relationship (DO NOT USE--instead, use
     * <code>setAuthorRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void setAuthor( net.sf.webcat.core.User value )
    {
        takeStoredValueForKey( value, "author" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>author</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setAuthorRelationship(
        net.sf.webcat.core.User value )
    {
        if ( value == null )
        {
            net.sf.webcat.core.User object = author();
            if ( object != null )
                removeObjectFromBothSidesOfRelationshipWithKey( object, "author" );
        }
        else
        {
            addObjectToBothSidesOfRelationshipWithKey( value, "author" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>gradingCriteria</code>
     * relationship.
     * @return the entity in the relationship
     */
    public net.sf.webcat.grader.GradingCriteria gradingCriteria()
    {
        return (net.sf.webcat.grader.GradingCriteria)storedValueForKey( "gradingCriteria" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>gradingCriteria</code>
     * relationship (DO NOT USE--instead, use
     * <code>setGradingCriteriaRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void setGradingCriteria( net.sf.webcat.grader.GradingCriteria value )
    {
        takeStoredValueForKey( value, "gradingCriteria" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>gradingCriteria</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setGradingCriteriaRelationship(
        net.sf.webcat.grader.GradingCriteria value )
    {
        if ( value == null )
        {
            net.sf.webcat.grader.GradingCriteria object = gradingCriteria();
            if ( object != null )
                removeObjectFromBothSidesOfRelationshipWithKey( object, "gradingCriteria" );
        }
        else
        {
            addObjectToBothSidesOfRelationshipWithKey( value, "gradingCriteria" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>submissionProfile</code>
     * relationship.
     * @return the entity in the relationship
     */
    public net.sf.webcat.grader.SubmissionProfile submissionProfile()
    {
        return (net.sf.webcat.grader.SubmissionProfile)storedValueForKey( "submissionProfile" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>submissionProfile</code>
     * relationship (DO NOT USE--instead, use
     * <code>setSubmissionProfileRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void setSubmissionProfile( net.sf.webcat.grader.SubmissionProfile value )
    {
        takeStoredValueForKey( value, "submissionProfile" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>submissionProfile</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setSubmissionProfileRelationship(
        net.sf.webcat.grader.SubmissionProfile value )
    {
        if ( value == null )
        {
            net.sf.webcat.grader.SubmissionProfile object = submissionProfile();
            if ( object != null )
                removeObjectFromBothSidesOfRelationshipWithKey( object, "submissionProfile" );
        }
        else
        {
            addObjectToBothSidesOfRelationshipWithKey( value, "submissionProfile" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entities pointed to by the <code>offerings</code>
     * relationship.
     * @return an NSArray of the entities in the relationship
     */
    public NSArray offerings()
    {
        return (NSArray)storedValueForKey( "offerings" );
    }


    // ----------------------------------------------------------
    /**
     * Replace the list of entities pointed to by the
     * <code>offerings</code> relationship.
     *
     * @param value The new set of entities to relate to
     */
    public void setOfferings( NSMutableArray value )
    {
        takeStoredValueForKey( value, "offerings" );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>offerings</code>
     * relationship (DO NOT USE--instead, use
     * <code>addToOfferingsRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void addToOfferings( net.sf.webcat.grader.AssignmentOffering value )
    {
        NSMutableArray array = (NSMutableArray)offerings();
        willChange();
        array.addObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>offerings</code>
     * relationship (DO NOT USE--instead, use
     * <code>removeFromOfferingsRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromOfferings( net.sf.webcat.grader.AssignmentOffering value )
    {
        NSMutableArray array = (NSMutableArray)offerings();
        willChange();
        array.removeObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>offerings</code>
     * relationship.
     *
     * @param value The new entity to relate to
     */
    public void addToOfferingsRelationship( net.sf.webcat.grader.AssignmentOffering value )
    {
        addObjectToBothSidesOfRelationshipWithKey(
            value, "offerings" );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>offerings</code>
     * relationship.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromOfferingsRelationship( net.sf.webcat.grader.AssignmentOffering value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "offerings" );
    }


    // ----------------------------------------------------------
    /**
     * Create a brand new object that is a member of the
     * <code>offerings</code> relationship.
     *
     * @return The new entity
     */
    public net.sf.webcat.grader.AssignmentOffering createOfferingsRelationship()
    {
        EOClassDescription eoClassDesc = EOClassDescription
            .classDescriptionForEntityName( "AssignmentOffering" );
        EOEnterpriseObject eoObject = eoClassDesc
            .createInstanceWithEditingContext( editingContext(), null );
        editingContext().insertObject( eoObject );
        addObjectToBothSidesOfRelationshipWithKey(
            eoObject, "offerings" );
        return (net.sf.webcat.grader.AssignmentOffering)eoObject;
    }


    // ----------------------------------------------------------
    /**
     * Remove and then delete a specific entity that is a member of the
     * <code>offerings</code> relationship.
     *
     * @param value The entity to remove from the relationship and then delete
     */
    public void deleteOfferingsRelationship( net.sf.webcat.grader.AssignmentOffering value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "offerings" );
        editingContext().deleteObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove (and then delete, if owned) all entities that are members of the
     * <code>offerings</code> relationship.
     */
    public void deleteAllOfferingsRelationships()
    {
        Enumeration objects = offerings().objectEnumerator();
        while ( objects.hasMoreElements() )
            deleteOfferingsRelationship(
                (net.sf.webcat.grader.AssignmentOffering)objects.nextElement() );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entities pointed to by the <code>steps</code>
     * relationship.
     * @return an NSArray of the entities in the relationship
     */
    public NSArray steps()
    {
        return (NSArray)storedValueForKey( "steps" );
    }


    // ----------------------------------------------------------
    /**
     * Replace the list of entities pointed to by the
     * <code>steps</code> relationship.
     *
     * @param value The new set of entities to relate to
     */
    public void setSteps( NSMutableArray value )
    {
        takeStoredValueForKey( value, "steps" );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>steps</code>
     * relationship (DO NOT USE--instead, use
     * <code>addToStepsRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void addToSteps( net.sf.webcat.grader.Step value )
    {
        NSMutableArray array = (NSMutableArray)steps();
        willChange();
        array.addObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>steps</code>
     * relationship (DO NOT USE--instead, use
     * <code>removeFromStepsRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromSteps( net.sf.webcat.grader.Step value )
    {
        NSMutableArray array = (NSMutableArray)steps();
        willChange();
        array.removeObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>steps</code>
     * relationship.
     *
     * @param value The new entity to relate to
     */
    public void addToStepsRelationship( net.sf.webcat.grader.Step value )
    {
        addObjectToBothSidesOfRelationshipWithKey(
            value, "steps" );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>steps</code>
     * relationship.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromStepsRelationship( net.sf.webcat.grader.Step value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "steps" );
    }


    // ----------------------------------------------------------
    /**
     * Create a brand new object that is a member of the
     * <code>steps</code> relationship.
     *
     * @return The new entity
     */
    public net.sf.webcat.grader.Step createStepsRelationship()
    {
        EOClassDescription eoClassDesc = EOClassDescription
            .classDescriptionForEntityName( "Step" );
        EOEnterpriseObject eoObject = eoClassDesc
            .createInstanceWithEditingContext( editingContext(), null );
        editingContext().insertObject( eoObject );
        addObjectToBothSidesOfRelationshipWithKey(
            eoObject, "steps" );
        return (net.sf.webcat.grader.Step)eoObject;
    }


    // ----------------------------------------------------------
    /**
     * Remove and then delete a specific entity that is a member of the
     * <code>steps</code> relationship.
     *
     * @param value The entity to remove from the relationship and then delete
     */
    public void deleteStepsRelationship( net.sf.webcat.grader.Step value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "steps" );
        editingContext().deleteObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove (and then delete, if owned) all entities that are members of the
     * <code>steps</code> relationship.
     */
    public void deleteAllStepsRelationships()
    {
        Enumeration objects = steps().objectEnumerator();
        while ( objects.hasMoreElements() )
            deleteStepsRelationship(
                (net.sf.webcat.grader.Step)objects.nextElement() );
    }


}
