/*==========================================================================*\
 |  _ReportParameter.java
 |*-------------------------------------------------------------------------*|
 |  Created by eogenerator
 |  DO NOT EDIT.  Make changes to ReportParameter.java instead.
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

package net.sf.webcat.reporter;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.util.Enumeration;

// -------------------------------------------------------------------------
/**
 * An automatically generated EOGenericRecord subclass.  DO NOT EDIT.
 * To change, use EOModeler, or make additions in
 * ReportParameter.java.
 *
 * @author Generated by eogenerator
 * @version version suppressed to control auto-generation
 */
public abstract class _ReportParameter
    extends er.extensions.ERXGenericRecord
    implements net.sf.webcat.core.MutableContainer.MutableContainerOwner
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new _ReportParameter object.
     */
    public _ReportParameter()
    {
        super();
    }


    //~ Constants (for key names) .............................................

    // Attributes ---
    public static final String BINDING_KEY = "binding";
    public static final String DESCRIPTION_KEY = "description";
    public static final String DISPLAY_NAME_KEY = "displayName";
    public static final String OPTIONS_KEY = "options";
    public static final String TYPE_KEY = "type";
    public static final String UPDATE_MUTABLE_FIELDS_KEY = "updateMutableFields";
    // To-one relationships ---
    public static final String REPORT_TEMPLATE_KEY = "reportTemplate";
    // To-many relationships ---
    public static final String DEPENDS_ON_KEY = "dependsOn";
    public static final String NEEDED_BY_KEY = "neededBy";
    public static final String ENTITY_NAME = "ReportParameter";


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>binding</code> value.
     * @return the value of the attribute
     */
    public String binding()
    {
        return (String)storedValueForKey( "binding" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>binding</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setBinding( String value )
    {
        takeStoredValueForKey( value, "binding" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>description</code> value.
     * @return the value of the attribute
     */
    public String description()
    {
        return (String)storedValueForKey( "description" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>description</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setDescription( String value )
    {
        takeStoredValueForKey( value, "description" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>displayName</code> value.
     * @return the value of the attribute
     */
    public String displayName()
    {
        return (String)storedValueForKey( "displayName" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>displayName</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setDisplayName( String value )
    {
        takeStoredValueForKey( value, "displayName" );
    }


    //-- Local mutable cache --
    private net.sf.webcat.core.MutableDictionary optionsCache;
    private NSData optionsRawCache;

    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>options</code> value.
     * @return the value of the attribute
     */
    public net.sf.webcat.core.MutableDictionary options()
    {
    	NSData dbValue = 
            (NSData)storedValueForKey( "options" );
        if ( optionsRawCache != dbValue )
        {
            if ( dbValue != null && dbValue.equals( optionsRawCache ) )
            {
                // They are still equal, so just update the raw cache
                optionsRawCache = dbValue;
            }
            else
            {
                // Underlying attribute may have changed because
                // of a concurrent update through another editing
                // context, so throw away current values.
                optionsRawCache = dbValue;
                net.sf.webcat.core.MutableDictionary newValue =
                    net.sf.webcat.core.MutableDictionary
                    .objectWithArchiveData( dbValue );
                if ( optionsCache != null )
                {
                    optionsCache.copyFrom( newValue );
                }
                else
                {
                    optionsCache = newValue;
                }
                optionsCache.setOwner( this );
                setUpdateMutableFields( true );
            }
        }
        else if ( dbValue == null && optionsCache == null )
        { 
            optionsCache = 
                net.sf.webcat.core.MutableDictionary
                .objectWithArchiveData( dbValue );
             optionsCache.setOwner( this );
             setUpdateMutableFields( true );
        }
        return optionsCache;
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>options</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setOptions( net.sf.webcat.core.MutableDictionary value )
    {
        if ( optionsCache == null )
        {
            optionsCache = value;
            value.setHasChanged( false );
            optionsRawCache = value.archiveData();
            takeStoredValueForKey( optionsRawCache, "options" );
        }
        else if ( optionsCache != value )  // ( optionsCache != null )
        {
            optionsCache.copyFrom( value );
            setUpdateMutableFields( true );
        }
        else  // ( optionsCache == non-null value )
        {
            // no nothing
        }
    }


    // ----------------------------------------------------------
    /**
     * Clear the value of this object's <code>options</code>
     * property.
     */
    public void clearOptions()
    {
        takeStoredValueForKey( null, "options" );
        optionsRawCache = null;
        optionsCache = null;
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>type</code> value.
     * @return the value of the attribute
     */
    public String type()
    {
        return (String)storedValueForKey( "type" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>type</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setType( String value )
    {
        takeStoredValueForKey( value, "type" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>updateMutableFields</code> value.
     * @return the value of the attribute
     */
    public boolean updateMutableFields()
    {
        Number result =
            (Number)storedValueForKey( "updateMutableFields" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>updateMutableFields</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setUpdateMutableFields( boolean value )
    {
        Number actual =
            er.extensions.ERXConstant.integerForInt( value ? 1 : 0 );
        takeStoredValueForKey( actual, "updateMutableFields" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>updateMutableFields</code> value.
     * @return the value of the attribute
     */
    public Number updateMutableFieldsRaw()
    {
        return (Number)storedValueForKey( "updateMutableFields" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>updateMutableFields</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setUpdateMutableFieldsRaw( Number value )
    {
        takeStoredValueForKey( value, "updateMutableFields" );
    }


    // ----------------------------------------------------------
    /**
     * Called just before this object is saved to the database.
     */
    public void saveMutables()
    {
        if ( optionsCache != null
            && optionsCache.hasChanged() )
        {
            optionsRawCache = optionsCache.archiveData();
            takeStoredValueForKey( optionsRawCache, "options" );
            optionsCache.setHasChanged( false );
        }

        setUpdateMutableFields( false );
    }


    // ----------------------------------------------------------
    /**
     * Called just before this object is saved to the database.
     */
    public void willUpdate()
    {
        saveMutables();
        super.willUpdate();
    }


    // ----------------------------------------------------------
    /**
     * Called just before this object is inserted into the database.
     */
    public void willInsert()
    {
        saveMutables();
        super.willInsert();
    }


    // ----------------------------------------------------------
    /**
     * Called when the object is invalidated.
     */
    public void flushCaches()
    {
        optionsCache = null;
        optionsRawCache  = null;
        setUpdateMutableFields( false );
        super.flushCaches();
    }


    // ----------------------------------------------------------
    /**
     * Called when an owned mutable container object is changed.
     */
    public void mutableContainerHasChanged()
    {
        setUpdateMutableFields( true );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>reportTemplate</code>
     * relationship.
     * @return the entity in the relationship
     */
    public net.sf.webcat.reporter.ReportTemplate reportTemplate()
    {
        return (net.sf.webcat.reporter.ReportTemplate)storedValueForKey( "reportTemplate" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>authenticationDomain</code>
     * relationship (DO NOT USE--instead, use
     * <code>setReportTemplateRelationship()</code>.
     * This method is provided for WebObjects use.
     * 
     * @param value The new entity to relate to
     */
    public void setReportTemplate( net.sf.webcat.reporter.ReportTemplate value )
    {
        takeStoredValueForKey( value, "reportTemplate" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>authenticationDomain</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     * 
     * @param value The new entity to relate to
     */
    public void setReportTemplateRelationship(
        net.sf.webcat.reporter.ReportTemplate value )
    {
        if ( value == null )
        {
            net.sf.webcat.reporter.ReportTemplate object = reportTemplate();
            if ( object != null )
                removeObjectFromBothSidesOfRelationshipWithKey( object, "reportTemplate" );
        }
        else
        {
            addObjectToBothSidesOfRelationshipWithKey( value, "reportTemplate" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entities pointed to by the <code>dependsOn</code>
     * relationship.
     * @return an NSArray of the entities in the relationship
     */
    public NSArray dependsOn()
    {
        return (NSArray)storedValueForKey( "dependsOn" );
    }


    // ----------------------------------------------------------
    /**
     * Replace the list of entities pointed to by the
     * <code>dependsOn</code> relationship.
     * 
     * @param value The new set of entities to relate to
     */
    public void setDependsOn( NSMutableArray value )
    {
        takeStoredValueForKey( value, "dependsOn" );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>dependsOn</code>
     * relationship (DO NOT USE--instead, use
     * <code>addToDependsOnRelationship()</code>.
     * This method is provided for WebObjects use.
     * 
     * @param value The new entity to relate to
     */
    public void addToDependsOn( net.sf.webcat.reporter.ReportParameter value )
    {
        NSMutableArray array = (NSMutableArray)dependsOn();
        willChange();
        array.addObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>dependsOn</code>
     * relationship (DO NOT USE--instead, use
     * <code>removeFromDependsOnRelationship()</code>.
     * This method is provided for WebObjects use.
     * 
     * @param value The entity to remove from the relationship
     */
    public void removeFromDependsOn( net.sf.webcat.reporter.ReportParameter value )
    {
        NSMutableArray array = (NSMutableArray)dependsOn();
        willChange();
        array.removeObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>dependsOn</code>
     * relationship.
     * 
     * @param value The new entity to relate to
     */
    public void addToDependsOnRelationship( net.sf.webcat.reporter.ReportParameter value )
    {
        addObjectToBothSidesOfRelationshipWithKey(
            value, "dependsOn" );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>dependsOn</code>
     * relationship.
     * 
     * @param value The entity to remove from the relationship
     */
    public void removeFromDependsOnRelationship( net.sf.webcat.reporter.ReportParameter value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "dependsOn" );
    }


    // ----------------------------------------------------------
    /**
     * Create a brand new object that is a member of the
     * <code>dependsOn</code> relationship.
     * 
     * @return The new entity
     */
    public net.sf.webcat.reporter.ReportParameter createDependsOnRelationship()
    {
        EOClassDescription eoClassDesc = EOClassDescription
            .classDescriptionForEntityName( "ReportParameter" );
        EOEnterpriseObject eoObject = eoClassDesc
            .createInstanceWithEditingContext( editingContext(), null );
        editingContext().insertObject( eoObject );
        addObjectToBothSidesOfRelationshipWithKey(
            eoObject, "dependsOn" );
        return (net.sf.webcat.reporter.ReportParameter)eoObject;
    }


    // ----------------------------------------------------------
    /**
     * Remove and then delete a specific entity that is a member of the
     * <code>dependsOn</code> relationship.
     * 
     * @param value The entity to remove from the relationship and then delete
     */
    public void deleteDependsOnRelationship( net.sf.webcat.reporter.ReportParameter value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "dependsOn" );
        editingContext().deleteObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove (and then delete, if owned) all entities that are members of the
     * <code>dependsOn</code> relationship.
     */
    public void deleteAllDependsOnRelationships()
    {
        Enumeration objects = dependsOn().objectEnumerator();
        while ( objects.hasMoreElements() )
            deleteDependsOnRelationship(
                (net.sf.webcat.reporter.ReportParameter)objects.nextElement() );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entities pointed to by the <code>neededBy</code>
     * relationship.
     * @return an NSArray of the entities in the relationship
     */
    public NSArray neededBy()
    {
        return (NSArray)storedValueForKey( "neededBy" );
    }


    // ----------------------------------------------------------
    /**
     * Replace the list of entities pointed to by the
     * <code>neededBy</code> relationship.
     * 
     * @param value The new set of entities to relate to
     */
    public void setNeededBy( NSMutableArray value )
    {
        takeStoredValueForKey( value, "neededBy" );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>neededBy</code>
     * relationship (DO NOT USE--instead, use
     * <code>addToNeededByRelationship()</code>.
     * This method is provided for WebObjects use.
     * 
     * @param value The new entity to relate to
     */
    public void addToNeededBy( net.sf.webcat.reporter.ReportParameter value )
    {
        NSMutableArray array = (NSMutableArray)neededBy();
        willChange();
        array.addObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>neededBy</code>
     * relationship (DO NOT USE--instead, use
     * <code>removeFromNeededByRelationship()</code>.
     * This method is provided for WebObjects use.
     * 
     * @param value The entity to remove from the relationship
     */
    public void removeFromNeededBy( net.sf.webcat.reporter.ReportParameter value )
    {
        NSMutableArray array = (NSMutableArray)neededBy();
        willChange();
        array.removeObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>neededBy</code>
     * relationship.
     * 
     * @param value The new entity to relate to
     */
    public void addToNeededByRelationship( net.sf.webcat.reporter.ReportParameter value )
    {
        addObjectToBothSidesOfRelationshipWithKey(
            value, "neededBy" );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>neededBy</code>
     * relationship.
     * 
     * @param value The entity to remove from the relationship
     */
    public void removeFromNeededByRelationship( net.sf.webcat.reporter.ReportParameter value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "neededBy" );
    }


    // ----------------------------------------------------------
    /**
     * Create a brand new object that is a member of the
     * <code>neededBy</code> relationship.
     * 
     * @return The new entity
     */
    public net.sf.webcat.reporter.ReportParameter createNeededByRelationship()
    {
        EOClassDescription eoClassDesc = EOClassDescription
            .classDescriptionForEntityName( "ReportParameter" );
        EOEnterpriseObject eoObject = eoClassDesc
            .createInstanceWithEditingContext( editingContext(), null );
        editingContext().insertObject( eoObject );
        addObjectToBothSidesOfRelationshipWithKey(
            eoObject, "neededBy" );
        return (net.sf.webcat.reporter.ReportParameter)eoObject;
    }


    // ----------------------------------------------------------
    /**
     * Remove and then delete a specific entity that is a member of the
     * <code>neededBy</code> relationship.
     * 
     * @param value The entity to remove from the relationship and then delete
     */
    public void deleteNeededByRelationship( net.sf.webcat.reporter.ReportParameter value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "neededBy" );
        editingContext().deleteObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove (and then delete, if owned) all entities that are members of the
     * <code>neededBy</code> relationship.
     */
    public void deleteAllNeededByRelationships()
    {
        Enumeration objects = neededBy().objectEnumerator();
        while ( objects.hasMoreElements() )
            deleteNeededByRelationship(
                (net.sf.webcat.reporter.ReportParameter)objects.nextElement() );
    }


}
