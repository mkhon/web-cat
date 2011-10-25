/*==========================================================================*\
 |  _ProtocolSettings.java
 |*-------------------------------------------------------------------------*|
 |  Created by eogenerator
 |  DO NOT EDIT.  Make changes to ProtocolSettings.java instead.
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2010 Virginia Tech
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

package org.webcat.notifications;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import er.extensions.eof.ERXEOControlUtilities;
import er.extensions.eof.ERXKey;
import org.apache.log4j.Logger;
import org.webcat.core.EOBasedKeyGenerator;
import org.webcat.woextensions.WCFetchSpecification;

// -------------------------------------------------------------------------
/**
 * An automatically generated EOGenericRecord subclass.  DO NOT EDIT.
 * To change, use EOModeler, or make additions in
 * ProtocolSettings.java.
 *
 * @author Generated by eogenerator
 * @version version suppressed to control auto-generation
 */
public abstract class _ProtocolSettings
    extends er.extensions.eof.ERXGenericRecord
    implements org.webcat.core.MutableContainer.MutableContainerOwner
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new _ProtocolSettings object.
     */
    public _ProtocolSettings()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * A static factory method for creating a new
     * ProtocolSettings object given required
     * attributes and relationships.
     * @param editingContext The context in which the new object will be
     * inserted
     * @param updateMutableFieldsValue
     * @return The newly created object
     */
    public static ProtocolSettings create(
        EOEditingContext editingContext,
        boolean updateMutableFieldsValue
        )
    {
        ProtocolSettings eoObject = (ProtocolSettings)
            EOUtilities.createAndInsertInstance(
                editingContext,
                _ProtocolSettings.ENTITY_NAME);
        eoObject.setUpdateMutableFields(updateMutableFieldsValue);
        return eoObject;
    }


    // ----------------------------------------------------------
    /**
     * Get a local instance of the given object in another editing context.
     * @param editingContext The target editing context
     * @param eo The object to import
     * @return An instance of the given object in the target editing context
     */
    public static ProtocolSettings localInstance(
        EOEditingContext editingContext, ProtocolSettings eo)
    {
        return (eo == null)
            ? null
            : (ProtocolSettings)EOUtilities.localInstanceOfObject(
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
    public static ProtocolSettings forId(
        EOEditingContext ec, int id)
    {
        ProtocolSettings obj = null;
        if (id > 0)
        {
            NSArray<ProtocolSettings> objects =
                objectsMatchingValues(ec, "id", new Integer(id));
            if (objects != null && objects.count() > 0)
            {
                obj = objects.objectAtIndex(0);
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
    public static ProtocolSettings forId(
        EOEditingContext ec, String id)
    {
        return forId(ec, er.extensions.foundation.ERXValueUtilities.intValue(id));
    }


    //~ Constants (for key names) .............................................

    // Attributes ---
    public static final String SETTINGS_KEY = "settings";
    public static final ERXKey<NSData> settings =
        new ERXKey<NSData>(SETTINGS_KEY);
    public static final String UPDATE_MUTABLE_FIELDS_KEY = "updateMutableFields";
    public static final ERXKey<Integer> updateMutableFields =
        new ERXKey<Integer>(UPDATE_MUTABLE_FIELDS_KEY);
    // To-one relationships ---
    public static final String PARENT_KEY = "parent";
    public static final ERXKey<org.webcat.notifications.ProtocolSettings> parent =
        new ERXKey<org.webcat.notifications.ProtocolSettings>(PARENT_KEY);
    public static final String USER_KEY = "user";
    public static final ERXKey<org.webcat.core.User> user =
        new ERXKey<org.webcat.core.User>(USER_KEY);
    // To-many relationships ---
    public static final String CHILDREN_KEY = "children";
    public static final ERXKey<org.webcat.notifications.ProtocolSettings> children =
        new ERXKey<org.webcat.notifications.ProtocolSettings>(CHILDREN_KEY);
    // Fetch specifications ---
    public static final String ENTITY_NAME = "ProtocolSettings";

    public transient final EOBasedKeyGenerator generateKey =
        new EOBasedKeyGenerator(this);


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Get a local instance of this object in another editing context.
     * @param editingContext The target editing context
     * @return An instance of this object in the target editing context
     */
    public ProtocolSettings localInstance(EOEditingContext editingContext)
    {
        return (ProtocolSettings)EOUtilities.localInstanceOfObject(
            editingContext, this);
    }


    // ----------------------------------------------------------
    /**
     * Get a list of changes between this object's current state and the
     * last committed version.
     * @return a dictionary of the changes that have not yet been committed
     */
    @SuppressWarnings("unchecked")
    public NSDictionary<String, Object> changedProperties()
    {
        return changesFromSnapshot(
            editingContext().committedSnapshotForObject(this));
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
                editingContext() , this).objectForKey("id");
        }
        catch (Exception e)
        {
            return er.extensions.eof.ERXConstant.ZeroInteger;
        }
    }

    //-- Local mutable cache --
    private org.webcat.core.MutableDictionary settingsCache;
    private NSData settingsRawCache;

    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>settings</code> value.
     * @return the value of the attribute
     */
    public org.webcat.core.MutableDictionary settings()
    {
        NSData dbValue =
            (NSData)storedValueForKey("settings");
        if (settingsRawCache != dbValue)
        {
            if (dbValue != null && dbValue.equals( settingsRawCache))
            {
                // They are still equal, so just update the raw cache
                settingsRawCache = dbValue;
            }
            else
            {
                // Underlying attribute may have changed because
                // of a concurrent update through another editing
                // context, so throw away current values.
                settingsRawCache = dbValue;
                org.webcat.core.MutableDictionary newValue =
                    org.webcat.core.MutableDictionary
                    .objectWithArchiveData( dbValue );
                if ( settingsCache != null )
                {
                    settingsCache.copyFrom( newValue );
                }
                else
                {
                    settingsCache = newValue;
                }
                settingsCache.setOwner( this );
                setUpdateMutableFields( true );
            }
        }
        else if ( dbValue == null && settingsCache == null )
        {
            settingsCache =
                org.webcat.core.MutableDictionary
                .objectWithArchiveData( dbValue );
             settingsCache.setOwner( this );
             setUpdateMutableFields( true );
        }
        return settingsCache;
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>settings</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setSettings( org.webcat.core.MutableDictionary value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setSettings("
                + value + ")" );
        }
        if ( settingsCache == null )
        {
            settingsCache = value;
            value.setHasChanged( false );
            settingsRawCache = value.archiveData();
            takeStoredValueForKey( settingsRawCache, "settings" );
        }
        else if ( settingsCache != value )  // ( settingsCache != null )
        {
            settingsCache.copyFrom( value );
            setUpdateMutableFields( true );
        }
        else  // ( settingsCache == non-null value )
        {
            // no nothing
        }
    }


    // ----------------------------------------------------------
    /**
     * Clear the value of this object's <code>settings</code>
     * property.
     */
    public void clearSettings()
    {
        if (log.isDebugEnabled())
        {
            log.debug( "clearSettings()" );
        }
        takeStoredValueForKey( null, "settings" );
        settingsRawCache = null;
        settingsCache = null;
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>updateMutableFields</code> value.
     * @return the value of the attribute
     */
    public boolean updateMutableFields()
    {
        Integer returnValue =
            (Integer)storedValueForKey( "updateMutableFields" );
        return ( returnValue == null )
            ? false
            : ( returnValue.intValue() > 0 );
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
        if (log.isDebugEnabled())
        {
            log.debug( "setUpdateMutableFields("
                + value + "): was " + updateMutableFields() );
        }
        Integer actual =
            er.extensions.eof.ERXConstant.integerForInt( value ? 1 : 0 );
            setUpdateMutableFieldsRaw( actual );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>updateMutableFields</code> value.
     * @return the value of the attribute
     */
    public Integer updateMutableFieldsRaw()
    {
        return (Integer)storedValueForKey( "updateMutableFields" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>updateMutableFields</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setUpdateMutableFieldsRaw( Integer value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setUpdateMutableFieldsRaw("
                + value + "): was " + updateMutableFieldsRaw() );
        }
        takeStoredValueForKey( value, "updateMutableFields" );
    }


    // ----------------------------------------------------------
    /**
     * Called just before this object is saved to the database.
     */
    public void saveMutables()
    {
        log.debug("saveMutables()");
        if ( settingsCache != null
            && settingsCache.hasChanged() )
        {
            settingsRawCache = settingsCache.archiveData();
            takeStoredValueForKey( settingsRawCache, "settings" );
            settingsCache.setHasChanged( false );
        }

        setUpdateMutableFields( false );
    }


    // ----------------------------------------------------------
    /**
     * Called just before this object is saved to the database.
     */
    public void willUpdate()
    {
        log.debug("willUpdate()");
        saveMutables();
        super.willUpdate();
    }


    // ----------------------------------------------------------
    /**
     * Called just before this object is inserted into the database.
     */
    public void willInsert()
    {
        log.debug("willInsert()");
        saveMutables();
        super.willInsert();
    }


    // ----------------------------------------------------------
    /**
     * Called when the object is invalidated.
     */
    public void flushCaches()
    {
        log.debug("flushCaches()");
        settingsCache = null;
        settingsRawCache  = null;
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
     * Retrieve the entity pointed to by the <code>parent</code>
     * relationship.
     * @return the entity in the relationship
     */
    public org.webcat.notifications.ProtocolSettings parent()
    {
        return (org.webcat.notifications.ProtocolSettings)storedValueForKey( "parent" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>parent</code>
     * relationship (DO NOT USE--instead, use
     * <code>setParentRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void setParent( org.webcat.notifications.ProtocolSettings value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setParent("
                + value + "): was " + parent() );
        }
        takeStoredValueForKey( value, "parent" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>parent</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setParentRelationship(
        org.webcat.notifications.ProtocolSettings value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setParentRelationship("
                + value + "): was " + parent() );
        }
        if ( value == null )
        {
            org.webcat.notifications.ProtocolSettings object = parent();
            if ( object != null )
                removeObjectFromBothSidesOfRelationshipWithKey( object, "parent" );
        }
        else
        {
            addObjectToBothSidesOfRelationshipWithKey( value, "parent" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>user</code>
     * relationship.
     * @return the entity in the relationship
     */
    public org.webcat.core.User user()
    {
        return (org.webcat.core.User)storedValueForKey( "user" );
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
    public void setUser( org.webcat.core.User value )
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
        org.webcat.core.User value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setUserRelationship("
                + value + "): was " + user() );
        }
        if ( value == null )
        {
            org.webcat.core.User object = user();
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
     * Retrieve the entities pointed to by the <code>children</code>
     * relationship.
     * @return an NSArray of the entities in the relationship
     */
    @SuppressWarnings("unchecked")
    public NSArray<org.webcat.notifications.ProtocolSettings> children()
    {
        return (NSArray)storedValueForKey( "children" );
    }


    // ----------------------------------------------------------
    /**
     * Replace the list of entities pointed to by the
     * <code>children</code> relationship.
     *
     * @param value The new set of entities to relate to
     */
    public void setChildren( NSMutableArray<org.webcat.notifications.ProtocolSettings>  value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "setChildren("
                + value + "): was " + children() );
        }
        takeStoredValueForKey( value, "children" );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>children</code>
     * relationship (DO NOT USE--instead, use
     * <code>addToChildrenRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The new entity to relate to
     */
    public void addToChildren( org.webcat.notifications.ProtocolSettings value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "addToChildren("
                + value + "): was " + children() );
        }
        NSMutableArray<org.webcat.notifications.ProtocolSettings> array =
            (NSMutableArray<org.webcat.notifications.ProtocolSettings>)children();
        willChange();
        array.addObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>children</code>
     * relationship (DO NOT USE--instead, use
     * <code>removeFromChildrenRelationship()</code>.
     * This method is provided for WebObjects use.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromChildren( org.webcat.notifications.ProtocolSettings value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "RemoveFromChildren("
                + value + "): was " + children() );
        }
        NSMutableArray<org.webcat.notifications.ProtocolSettings> array =
            (NSMutableArray<org.webcat.notifications.ProtocolSettings>)children();
        willChange();
        array.removeObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>children</code>
     * relationship.
     *
     * @param value The new entity to relate to
     */
    public void addToChildrenRelationship( org.webcat.notifications.ProtocolSettings value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "addToChildrenRelationship("
                + value + "): was " + children() );
        }
        addObjectToBothSidesOfRelationshipWithKey(
            value, "children" );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>children</code>
     * relationship.
     *
     * @param value The entity to remove from the relationship
     */
    public void removeFromChildrenRelationship( org.webcat.notifications.ProtocolSettings value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "removeFromChildrenRelationship("
                + value + "): was " + children() );
        }
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "children" );
    }


    // ----------------------------------------------------------
    /**
     * Create a brand new object that is a member of the
     * <code>children</code> relationship.
     *
     * @return The new entity
     */
    public org.webcat.notifications.ProtocolSettings createChildrenRelationship()
    {
        if (log.isDebugEnabled())
        {
            log.debug( "createChildrenRelationship()" );
        }
        EOClassDescription eoClassDesc = EOClassDescription
            .classDescriptionForEntityName( "ProtocolSettings" );
        EOEnterpriseObject eoObject = eoClassDesc
            .createInstanceWithEditingContext( editingContext(), null );
        editingContext().insertObject( eoObject );
        addObjectToBothSidesOfRelationshipWithKey(
            eoObject, "children" );
        return (org.webcat.notifications.ProtocolSettings)eoObject;
    }


    // ----------------------------------------------------------
    /**
     * Remove and then delete a specific entity that is a member of the
     * <code>children</code> relationship.
     *
     * @param value The entity to remove from the relationship and then delete
     */
    public void deleteChildrenRelationship( org.webcat.notifications.ProtocolSettings value )
    {
        if (log.isDebugEnabled())
        {
            log.debug( "deleteChildrenRelationship("
                + value + "): was " + children() );
        }
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "children" );
        editingContext().deleteObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove (and then delete, if owned) all entities that are members of the
     * <code>children</code> relationship.
     */
    public void deleteAllChildrenRelationships()
    {
        if (log.isDebugEnabled())
        {
            log.debug( "deleteAllChildrenRelationships(): was "
                + children() );
        }
        for (org.webcat.notifications.ProtocolSettings object : children())
        {
            deleteChildrenRelationship(object);
        }
    }


    // ----------------------------------------------------------
    /**
     * Retrieve objects using a fetch specification.
     *
     * @param context The editing context to use
     * @param fspec The fetch specification to use
     *
     * @return an NSArray of the entities retrieved
     */
    @SuppressWarnings("unchecked")
    public static NSArray<ProtocolSettings> objectsWithFetchSpecification(
        EOEditingContext context,
        EOFetchSpecification fspec)
    {
        return context.objectsWithFetchSpecification(fspec);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve all objects of this type.
     *
     * @param context The editing context to use
     *
     * @return an NSArray of the entities retrieved
     */
    public static NSArray<ProtocolSettings> allObjects(
        EOEditingContext context)
    {
        return objectsMatchingQualifier(context, null, null);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve objects using a qualifier.
     *
     * @param context The editing context to use
     * @param qualifier The qualifier to use
     *
     * @return an NSArray of the entities retrieved
     */
    public static NSArray<ProtocolSettings> objectsMatchingQualifier(
        EOEditingContext context,
        EOQualifier qualifier)
    {
        return objectsMatchingQualifier(context, qualifier, null);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve objects using a qualifier and sort orderings.
     *
     * @param context The editing context to use
     * @param qualifier The qualifier to use
     * @param sortOrderings The sort orderings to use
     *
     * @return an NSArray of the entities retrieved
     */
    public static NSArray<ProtocolSettings> objectsMatchingQualifier(
        EOEditingContext context,
        EOQualifier qualifier,
        NSArray<EOSortOrdering> sortOrderings)
    {
        @SuppressWarnings("unchecked")
        EOFetchSpecification fspec = new WCFetchSpecification(
                ENTITY_NAME, qualifier, sortOrderings);
        fspec.setUsesDistinct(true);
        return objectsWithFetchSpecification(context, fspec);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the first object that matches a qualifier, when
     * sorted with the specified sort orderings.
     *
     * @param context The editing context to use
     * @param qualifier The qualifier to use
     * @param sortOrderings the sort orderings
     *
     * @return the first entity that was retrieved, or null if there was none
     */
    public static ProtocolSettings firstObjectMatchingQualifier(
        EOEditingContext context,
        EOQualifier qualifier,
        NSArray<EOSortOrdering> sortOrderings)
    {
        NSArray<ProtocolSettings> objects =
            objectsMatchingQualifier(context, qualifier, sortOrderings);
        return (objects.size() > 0)
            ? objects.get(0)
            : null;
    }


    // ----------------------------------------------------------
    /**
     * Retrieve a single object using a list of keys and values to match.
     *
     * @param context The editing context to use
     * @param qualifier The qualifier to use
     *
     * @return the single entity that was retrieved
     *
     * @throws EOUtilities.MoreThanOneException
     *     if there is more than one matching object
     */
    public static ProtocolSettings uniqueObjectMatchingQualifier(
        EOEditingContext context,
        EOQualifier qualifier) throws EOUtilities.MoreThanOneException
    {
        NSArray<ProtocolSettings> objects =
            objectsMatchingQualifier(context, qualifier);
        if (objects.size() > 1)
        {
            throw new EOUtilities.MoreThanOneException(null);
        }
        return (objects.size() > 0)
            ? objects.get(0)
            : null;
    }


    // ----------------------------------------------------------
    /**
     * Retrieve objects using a list of keys and values to match.
     *
     * @param context The editing context to use
     * @param keysAndValues a list of keys and values to match, alternating
     *     "key", "value", "key", "value"...
     *
     * @return an NSArray of the entities retrieved
     */
    public static NSArray<ProtocolSettings> objectsMatchingValues(
        EOEditingContext context,
        Object... keysAndValues)
    {
        if (keysAndValues.length % 2 != 0)
        {
            throw new IllegalArgumentException("There should a value " +
                "corresponding to every key that was passed.");
        }

        NSMutableDictionary<String, Object> valueDictionary =
            new NSMutableDictionary<String, Object>();

        for (int i = 0; i < keysAndValues.length; i += 2)
        {
            Object key = keysAndValues[i];
            Object value = keysAndValues[i + 1];

            if (!(key instanceof String))
            {
                throw new IllegalArgumentException("Keys should be strings.");
            }

            valueDictionary.setObjectForKey(value, (String)key);
        }

        return objectsMatchingValues(context, valueDictionary);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve objects using a dictionary of keys and values to match.
     *
     * @param context The editing context to use
     * @param keysAndValues a dictionary of keys and values to match
     *
     * @return an NSArray of the entities retrieved
     */
    @SuppressWarnings("unchecked")
    public static NSArray<ProtocolSettings> objectsMatchingValues(
        EOEditingContext context,
        NSDictionary<String, Object> keysAndValues)
    {
        return EOUtilities.objectsMatchingValues(context, ENTITY_NAME,
            keysAndValues);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the first object that matches a set of keys and values, when
     * sorted with the specified sort orderings.
     *
     * @param context The editing context to use
     * @param sortOrderings the sort orderings
     * @param keysAndValues a list of keys and values to match, alternating
     *     "key", "value", "key", "value"...
     *
     * @return the first entity that was retrieved, or null if there was none
     */
    public static ProtocolSettings firstObjectMatchingValues(
        EOEditingContext context,
        NSArray<EOSortOrdering> sortOrderings,
        Object... keysAndValues)
    {
        if (keysAndValues.length % 2 != 0)
        {
            throw new IllegalArgumentException("There should a value " +
                "corresponding to every key that was passed.");
        }

        NSMutableDictionary<String, Object> valueDictionary =
            new NSMutableDictionary<String, Object>();

        for (int i = 0; i < keysAndValues.length; i += 2)
        {
            Object key = keysAndValues[i];
            Object value = keysAndValues[i + 1];

            if (!(key instanceof String))
            {
                throw new IllegalArgumentException("Keys should be strings.");
            }

            valueDictionary.setObjectForKey(value, (String)key);
        }

        return firstObjectMatchingValues(
            context, sortOrderings, valueDictionary);
    }


    // ----------------------------------------------------------
    /**
     * Retrieves the first object that matches a set of keys and values, when
     * sorted with the specified sort orderings.
     *
     * @param context The editing context to use
     * @param sortOrderings the sort orderings
     * @param keysAndValues a dictionary of keys and values to match
     *
     * @return the first entity that was retrieved, or null if there was none
     */
    public static ProtocolSettings firstObjectMatchingValues(
        EOEditingContext context,
        NSArray<EOSortOrdering> sortOrderings,
        NSDictionary<String, Object> keysAndValues)
    {
        @SuppressWarnings("unchecked")
        EOFetchSpecification fspec = new WCFetchSpecification(
                ENTITY_NAME,
                EOQualifier.qualifierToMatchAllValues(keysAndValues),
                sortOrderings);
        fspec.setFetchLimit(1);

        NSArray<ProtocolSettings> objects =
            objectsWithFetchSpecification( context, fspec );

        if ( objects.count() == 0 )
        {
            return null;
        }
        else
        {
            return objects.objectAtIndex(0);
        }
    }


    // ----------------------------------------------------------
    /**
     * Retrieve a single object using a list of keys and values to match.
     *
     * @param context The editing context to use
     * @param keysAndValues a list of keys and values to match, alternating
     *     "key", "value", "key", "value"...
     *
     * @return the single entity that was retrieved, or null if there was none
     *
     * @throws EOUtilities.MoreThanOneException
     *     if there is more than one matching object
     */
    public static ProtocolSettings uniqueObjectMatchingValues(
        EOEditingContext context,
        Object... keysAndValues) throws EOUtilities.MoreThanOneException
    {
        if (keysAndValues.length % 2 != 0)
        {
            throw new IllegalArgumentException("There should a value " +
                "corresponding to every key that was passed.");
        }

        NSMutableDictionary<String, Object> valueDictionary =
            new NSMutableDictionary<String, Object>();

        for (int i = 0; i < keysAndValues.length; i += 2)
        {
            Object key = keysAndValues[i];
            Object value = keysAndValues[i + 1];

            if (!(key instanceof String))
            {
                throw new IllegalArgumentException("Keys should be strings.");
            }

            valueDictionary.setObjectForKey(value, (String)key);
        }

        return uniqueObjectMatchingValues(context, valueDictionary);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve an object using a dictionary of keys and values to match.
     *
     * @param context The editing context to use
     * @param keysAndValues a dictionary of keys and values to match
     *
     * @return the single entity that was retrieved, or null if there was none
     *
     * @throws EOUtilities.MoreThanOneException
     *     if there is more than one matching object
     */
    public static ProtocolSettings uniqueObjectMatchingValues(
        EOEditingContext context,
        NSDictionary<String, Object> keysAndValues)
        throws EOUtilities.MoreThanOneException
    {
        try
        {
            return (ProtocolSettings)EOUtilities.objectMatchingValues(
                context, ENTITY_NAME, keysAndValues);
        }
        catch (EOObjectNotAvailableException e)
        {
            return null;
        }
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the count of all objects of this type.
     *
     * @param context The editing context to use
     *
     * @return the count of all objects
     */
    public static int countOfAllObjects(EOEditingContext context)
    {
        return countOfObjectsMatchingQualifier(context, null);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the count of objects that match a qualifier.
     *
     * @param context The editing context to use
     * @param qualifier The qualifier to use
     *
     * @return the count of objects matching the qualifier
     */
    public static int countOfObjectsMatchingQualifier(
        EOEditingContext context, EOQualifier qualifier)
    {
        return ERXEOControlUtilities.objectCountWithQualifier(
                context, ENTITY_NAME, qualifier);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the count of objects using a list of keys and values to match.
     *
     * @param context The editing context to use
     * @param keysAndValues a list of keys and values to match, alternating
     *     "key", "value", "key", "value"...
     *
     * @return the count of objects that match the specified values
     */
    public static int countOfObjectsMatchingValues(
        EOEditingContext context,
        Object... keysAndValues)
    {
        if (keysAndValues.length % 2 != 0)
        {
            throw new IllegalArgumentException("There should a value " +
                "corresponding to every key that was passed.");
        }

        NSMutableDictionary<String, Object> valueDictionary =
            new NSMutableDictionary<String, Object>();

        for (int i = 0; i < keysAndValues.length; i += 2)
        {
            Object key = keysAndValues[i];
            Object value = keysAndValues[i + 1];

            if (!(key instanceof String))
            {
                throw new IllegalArgumentException("Keys should be strings.");
            }

            valueDictionary.setObjectForKey(value, (String)key);
        }

        return countOfObjectsMatchingValues(context, valueDictionary);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the count of objects using a dictionary of keys and values to
     * match.
     *
     * @param context The editing context to use
     * @param keysAndValues a dictionary of keys and values to match
     *
     * @return the count of objects that matched the specified values
     */
    public static int countOfObjectsMatchingValues(
        EOEditingContext context,
        NSDictionary<String, Object> keysAndValues)
    {
        return countOfObjectsMatchingQualifier(context,
                EOQualifier.qualifierToMatchAllValues(keysAndValues));
    }


    // ----------------------------------------------------------
    /**
     * Produce a string representation of this object.  This implementation
     * calls UserPresentableDescription(), which uses WebObjects' internal
     * mechanism to print out the visible fields of this object.  Normally,
     * subclasses would override userPresentableDescription() to change
     * the way the object is printed.
     *
     * @return A string representation of the object's value
     */
    public String toString()
    {
        return userPresentableDescription();
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger(ProtocolSettings.class);
}
