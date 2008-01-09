/*==========================================================================*\
 |  _CourseOffering.java
 |*-------------------------------------------------------------------------*|
 |  Created by eogenerator
 |  DO NOT EDIT.  Make changes to CourseOffering.java instead.
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

package net.sf.webcat.core;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.util.Enumeration;

// -------------------------------------------------------------------------
/**
 * An automatically generated EOGenericRecord subclass.  DO NOT EDIT.
 * To change, use EOModeler, or make additions in
 * CourseOffering.java.
 *
 * @author Generated by eogenerator
 * @version version suppressed to control auto-generation
 */
public abstract class _CourseOffering
    extends er.extensions.ERXGenericRecord
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new _CourseOffering object.
     */
    public _CourseOffering()
    {
        super();
    }


    //~ Constants (for key names) .............................................

    // Attributes ---
    public static final String CRN_KEY = "crn";
    public static final String LABEL_KEY = "label";
    public static final String MOODLE_GROUP_ID_KEY = "moodleGroupId";
    public static final String MOODLE_ID_KEY = "moodleId";
    public static final String URL_KEY = "url";
    // To-one relationships ---
    public static final String COURSE_KEY = "course";
    public static final String SEMESTER_KEY = "semester";
    // To-many relationships ---
    public static final String TAS_KEY = "TAs";
    public static final String INSTRUCTORS_KEY = "instructors";
    public static final String STUDENTS_KEY = "students";
    // Fetch specifications ---
    public static final String FOR_SEMESTER_FSPEC = "forSemester";
    public static final String WITHOUT_ANY_RELATIONSHIP_TO_USER_FSPEC = "withoutAnyRelationshipToUser";
    public static final String WITHOUT_STUDENT_FSPEC = "withoutStudent";
    public static final String WITHOUT_STUDENT_OR_TA_FSPEC = "withoutStudentOrTA";
    public static final String WITHOUT_USER_AS_STAFF_FSPEC = "withoutUserAsStaff";
    public static final String ENTITY_NAME = "CourseOffering";


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>crn</code> value.
     * @return the value of the attribute
     */
    public String crn()
    {
        return (String)storedValueForKey( "crn" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>crn</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setCrn( String value )
    {
        takeStoredValueForKey( value, "crn" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>label</code> value.
     * @return the value of the attribute
     */
    public String label()
    {
        return (String)storedValueForKey( "label" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>label</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setLabel( String value )
    {
        takeStoredValueForKey( value, "label" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>moodleGroupId</code> value.
     * @return the value of the attribute
     */
    public Number moodleGroupId()
    {
        return (Number)storedValueForKey( "moodleGroupId" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>moodleGroupId</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setMoodleGroupId( Number value )
    {
        takeStoredValueForKey( value, "moodleGroupId" );
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
     * Retrieve object according to the <code>ForSemester</code>
     * fetch specification.
     *
     * @param context The editing context to use
     * @param semesterBinding fetch spec parameter
     * @return an NSArray of the entities retrieved
     */
    public static NSArray objectsForForSemester(
            EOEditingContext context,
            net.sf.webcat.core.Semester semesterBinding
        )
    {
        EOFetchSpecification spec = EOFetchSpecification
            .fetchSpecificationNamed( "forSemester", "CourseOffering" );

        NSMutableDictionary bindings = new NSMutableDictionary();

        if ( semesterBinding != null )
            bindings.setObjectForKey( semesterBinding,
                                      "semester" );
        spec = spec.fetchSpecificationWithQualifierBindings( bindings );

        return context.objectsWithFetchSpecification( spec );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve object according to the <code>WithoutAnyRelationshipToUser</code>
     * fetch specification.
     *
     * @param context The editing context to use
     * @param userBinding fetch spec parameter
     * @return an NSArray of the entities retrieved
     */
    public static NSArray objectsForWithoutAnyRelationshipToUser(
            EOEditingContext context,
            net.sf.webcat.core.User userBinding
        )
    {
        EOFetchSpecification spec = EOFetchSpecification
            .fetchSpecificationNamed( "withoutAnyRelationshipToUser", "CourseOffering" );

        NSMutableDictionary bindings = new NSMutableDictionary();

        if ( userBinding != null )
            bindings.setObjectForKey( userBinding,
                                      "user" );
        spec = spec.fetchSpecificationWithQualifierBindings( bindings );

        return context.objectsWithFetchSpecification( spec );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve object according to the <code>WithoutStudent</code>
     * fetch specification.
     *
     * @param context The editing context to use
     * @param userBinding fetch spec parameter
     * @return an NSArray of the entities retrieved
     */
    public static NSArray objectsForWithoutStudent(
            EOEditingContext context,
            net.sf.webcat.core.User userBinding
        )
    {
        EOFetchSpecification spec = EOFetchSpecification
            .fetchSpecificationNamed( "withoutStudent", "CourseOffering" );

        NSMutableDictionary bindings = new NSMutableDictionary();

        if ( userBinding != null )
            bindings.setObjectForKey( userBinding,
                                      "user" );
        spec = spec.fetchSpecificationWithQualifierBindings( bindings );

        return context.objectsWithFetchSpecification( spec );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve object according to the <code>WithoutStudentOrTA</code>
     * fetch specification.
     *
     * @param context The editing context to use
     * @param userBinding fetch spec parameter
     * @return an NSArray of the entities retrieved
     */
    public static NSArray objectsForWithoutStudentOrTA(
            EOEditingContext context,
            net.sf.webcat.core.User userBinding
        )
    {
        EOFetchSpecification spec = EOFetchSpecification
            .fetchSpecificationNamed( "withoutStudentOrTA", "CourseOffering" );

        NSMutableDictionary bindings = new NSMutableDictionary();

        if ( userBinding != null )
            bindings.setObjectForKey( userBinding,
                                      "user" );
        spec = spec.fetchSpecificationWithQualifierBindings( bindings );

        return context.objectsWithFetchSpecification( spec );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve object according to the <code>WithoutUserAsStaff</code>
     * fetch specification.
     *
     * @param context The editing context to use
     * @param userBinding fetch spec parameter
     * @return an NSArray of the entities retrieved
     */
    public static NSArray objectsForWithoutUserAsStaff(
            EOEditingContext context,
            net.sf.webcat.core.User userBinding
        )
    {
        EOFetchSpecification spec = EOFetchSpecification
            .fetchSpecificationNamed( "withoutUserAsStaff", "CourseOffering" );

        NSMutableDictionary bindings = new NSMutableDictionary();

        if ( userBinding != null )
            bindings.setObjectForKey( userBinding,
                                      "user" );
        spec = spec.fetchSpecificationWithQualifierBindings( bindings );

        return context.objectsWithFetchSpecification( spec );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>course</code>
     * relationship.
     * @return the entity in the relationship
     */
    public net.sf.webcat.core.Course course()
    {
        return (net.sf.webcat.core.Course)storedValueForKey( "course" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>authenticationDomain</code>
     * relationship (DO NOT USE--instead, use
     * <code>setCourseRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void setCourse( net.sf.webcat.core.Course value )
    {
        takeStoredValueForKey( value, "course" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>authenticationDomain</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setCourseRelationship(
        net.sf.webcat.core.Course value )
    {
        if ( value == null )
        {
            net.sf.webcat.core.Course object = course();
            if ( object != null )
                removeObjectFromBothSidesOfRelationshipWithKey( object, "course" );
        }
        else
        {
            addObjectToBothSidesOfRelationshipWithKey( value, "course" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>semester</code>
     * relationship.
     * @return the entity in the relationship
     */
    public net.sf.webcat.core.Semester semester()
    {
        return (net.sf.webcat.core.Semester)storedValueForKey( "semester" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>authenticationDomain</code>
     * relationship (DO NOT USE--instead, use
     * <code>setSemesterRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void setSemester( net.sf.webcat.core.Semester value )
    {
        takeStoredValueForKey( value, "semester" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>authenticationDomain</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setSemesterRelationship(
        net.sf.webcat.core.Semester value )
    {
        if ( value == null )
        {
            net.sf.webcat.core.Semester object = semester();
            if ( object != null )
                removeObjectFromBothSidesOfRelationshipWithKey( object, "semester" );
        }
        else
        {
            addObjectToBothSidesOfRelationshipWithKey( value, "semester" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entities pointed to by the <code>TAs</code>
     * relationship.
     * @return an NSArray of the entities in the relationship
     */
    public NSArray TAs()
    {
        return (NSArray)storedValueForKey( "TAs" );
    }


    // ----------------------------------------------------------
    /**
     * Replace the list of entities pointed to by the
     * <code>TAs</code> relationship.
     *
     * @param value The new set of entities to relate to
     */
    public void setTAs( NSMutableArray value )
    {
        takeStoredValueForKey( value, "TAs" );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>TAs</code>
     * relationship (DO NOT USE--instead, use
     * <code>addToTAsRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void addToTAs( net.sf.webcat.core.User value )
    {
        NSMutableArray array = (NSMutableArray)TAs();
        willChange();
        array.addObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>TAs</code>
     * relationship (DO NOT USE--instead, use
     * <code>removeFromTAsRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromTAs( net.sf.webcat.core.User value )
    {
        NSMutableArray array = (NSMutableArray)TAs();
        willChange();
        array.removeObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>TAs</code>
     * relationship.
     *
     * @param value The new entity to relate to
     */
    public void addToTAsRelationship( net.sf.webcat.core.User value )
    {
        addObjectToBothSidesOfRelationshipWithKey(
            value, "TAs" );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>TAs</code>
     * relationship.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromTAsRelationship( net.sf.webcat.core.User value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "TAs" );
    }


    // ----------------------------------------------------------
    /**
     * Create a brand new object that is a member of the
     * <code>TAs</code> relationship.
     *
     * @return The new entity
     */
    public net.sf.webcat.core.User createTAsRelationship()
    {
        EOClassDescription eoClassDesc = EOClassDescription
            .classDescriptionForEntityName( "User" );
        EOEnterpriseObject eoObject = eoClassDesc
            .createInstanceWithEditingContext( editingContext(), null );
        editingContext().insertObject( eoObject );
        addObjectToBothSidesOfRelationshipWithKey(
            eoObject, "TAs" );
        return (net.sf.webcat.core.User)eoObject;
    }


    // ----------------------------------------------------------
    /**
     * Remove and then delete a specific entity that is a member of the
     * <code>TAs</code> relationship.
     *
     * @param value The entity to remove from the relationship and then delete
     */
    public void deleteTAsRelationship( net.sf.webcat.core.User value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "TAs" );
        editingContext().deleteObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove (and then delete, if owned) all entities that are members of the
     * <code>TAs</code> relationship.
     */
    public void deleteAllTAsRelationships()
    {
        Enumeration objects = TAs().objectEnumerator();
        while ( objects.hasMoreElements() )
            deleteTAsRelationship(
                (net.sf.webcat.core.User)objects.nextElement() );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entities pointed to by the <code>instructors</code>
     * relationship.
     * @return an NSArray of the entities in the relationship
     */
    public NSArray instructors()
    {
        return (NSArray)storedValueForKey( "instructors" );
    }


    // ----------------------------------------------------------
    /**
     * Replace the list of entities pointed to by the
     * <code>instructors</code> relationship.
     *
     * @param value The new set of entities to relate to
     */
    public void setInstructors( NSMutableArray value )
    {
        takeStoredValueForKey( value, "instructors" );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>instructors</code>
     * relationship (DO NOT USE--instead, use
     * <code>addToInstructorsRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void addToInstructors( net.sf.webcat.core.User value )
    {
        NSMutableArray array = (NSMutableArray)instructors();
        willChange();
        array.addObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>instructors</code>
     * relationship (DO NOT USE--instead, use
     * <code>removeFromInstructorsRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromInstructors( net.sf.webcat.core.User value )
    {
        NSMutableArray array = (NSMutableArray)instructors();
        willChange();
        array.removeObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>instructors</code>
     * relationship.
     *
     * @param value The new entity to relate to
     */
    public void addToInstructorsRelationship( net.sf.webcat.core.User value )
    {
        addObjectToBothSidesOfRelationshipWithKey(
            value, "instructors" );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>instructors</code>
     * relationship.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromInstructorsRelationship( net.sf.webcat.core.User value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "instructors" );
    }


    // ----------------------------------------------------------
    /**
     * Create a brand new object that is a member of the
     * <code>instructors</code> relationship.
     *
     * @return The new entity
     */
    public net.sf.webcat.core.User createInstructorsRelationship()
    {
        EOClassDescription eoClassDesc = EOClassDescription
            .classDescriptionForEntityName( "User" );
        EOEnterpriseObject eoObject = eoClassDesc
            .createInstanceWithEditingContext( editingContext(), null );
        editingContext().insertObject( eoObject );
        addObjectToBothSidesOfRelationshipWithKey(
            eoObject, "instructors" );
        return (net.sf.webcat.core.User)eoObject;
    }


    // ----------------------------------------------------------
    /**
     * Remove and then delete a specific entity that is a member of the
     * <code>instructors</code> relationship.
     *
     * @param value The entity to remove from the relationship and then delete
     */
    public void deleteInstructorsRelationship( net.sf.webcat.core.User value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "instructors" );
        editingContext().deleteObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove (and then delete, if owned) all entities that are members of the
     * <code>instructors</code> relationship.
     */
    public void deleteAllInstructorsRelationships()
    {
        Enumeration objects = instructors().objectEnumerator();
        while ( objects.hasMoreElements() )
            deleteInstructorsRelationship(
                (net.sf.webcat.core.User)objects.nextElement() );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entities pointed to by the <code>students</code>
     * relationship.
     * @return an NSArray of the entities in the relationship
     */
    public NSArray students()
    {
        return (NSArray)storedValueForKey( "students" );
    }


    // ----------------------------------------------------------
    /**
     * Replace the list of entities pointed to by the
     * <code>students</code> relationship.
     *
     * @param value The new set of entities to relate to
     */
    public void setStudents( NSMutableArray value )
    {
        takeStoredValueForKey( value, "students" );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>students</code>
     * relationship (DO NOT USE--instead, use
     * <code>addToStudentsRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void addToStudents( net.sf.webcat.core.User value )
    {
        NSMutableArray array = (NSMutableArray)students();
        willChange();
        array.addObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>students</code>
     * relationship (DO NOT USE--instead, use
     * <code>removeFromStudentsRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromStudents( net.sf.webcat.core.User value )
    {
        NSMutableArray array = (NSMutableArray)students();
        willChange();
        array.removeObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>students</code>
     * relationship.
     *
     * @param value The new entity to relate to
     */
    public void addToStudentsRelationship( net.sf.webcat.core.User value )
    {
        addObjectToBothSidesOfRelationshipWithKey(
            value, "students" );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>students</code>
     * relationship.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromStudentsRelationship( net.sf.webcat.core.User value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "students" );
    }


    // ----------------------------------------------------------
    /**
     * Create a brand new object that is a member of the
     * <code>students</code> relationship.
     *
     * @return The new entity
     */
    public net.sf.webcat.core.User createStudentsRelationship()
    {
        EOClassDescription eoClassDesc = EOClassDescription
            .classDescriptionForEntityName( "User" );
        EOEnterpriseObject eoObject = eoClassDesc
            .createInstanceWithEditingContext( editingContext(), null );
        editingContext().insertObject( eoObject );
        addObjectToBothSidesOfRelationshipWithKey(
            eoObject, "students" );
        return (net.sf.webcat.core.User)eoObject;
    }


    // ----------------------------------------------------------
    /**
     * Remove and then delete a specific entity that is a member of the
     * <code>students</code> relationship.
     *
     * @param value The entity to remove from the relationship and then delete
     */
    public void deleteStudentsRelationship( net.sf.webcat.core.User value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "students" );
        editingContext().deleteObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove (and then delete, if owned) all entities that are members of the
     * <code>students</code> relationship.
     */
    public void deleteAllStudentsRelationships()
    {
        Enumeration objects = students().objectEnumerator();
        while ( objects.hasMoreElements() )
            deleteStudentsRelationship(
                (net.sf.webcat.core.User)objects.nextElement() );
    }


}
