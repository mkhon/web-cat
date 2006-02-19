/*==========================================================================*\
 |  _Department.java
 |*-------------------------------------------------------------------------*|
 |  Created by eogenerator
 |  DO NOT EDIT.  Make changes to Department.java instead.
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
 * Department.java.
 *
 * @author Generated by eogenerator
 * @version version suppressed to control auto-generation
 */
public abstract class _Department
    extends er.extensions.ERXGenericRecord
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new _Department object.
     */
    public _Department()
    {
        super();
    }


    //~ Constants (for key names) .............................................

    // Attributes ---
    public static final String ABBREVIATION_KEY = "abbreviation";
    public static final String NAME_KEY = "name";
    // To-one relationships ---
    public static final String INSTITUTION_KEY = "institution";
    // To-many relationships ---
    public static final String ENTITY_NAME = "Department";


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>abbreviation</code> value.
     * @return the value of the attribute
     */
    public String abbreviation()
    {
        return (String)storedValueForKey( "abbreviation" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>abbreviation</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setAbbreviation( String value )
    {
        takeStoredValueForKey( value, "abbreviation" );
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
     * Retrieve the entity pointed to by the <code>institution</code>
     * relationship.
     * @return the entity in the relationship
     */
    public net.sf.webcat.core.AuthenticationDomain institution()
    {
        return (net.sf.webcat.core.AuthenticationDomain)storedValueForKey( "institution" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>authenticationDomain</code>
     * relationship (DO NOT USE--instead, use
     * <code>setInstitutionRelationship()</code>.
     * This method is provided for WebObjects use.
     * 
     * @param value The new entity to relate to
     */
    public void setInstitution( net.sf.webcat.core.AuthenticationDomain value )
    {
        takeStoredValueForKey( value, "institution" );
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>authenticationDomain</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     * 
     * @param value The new entity to relate to
     */
    public void setInstitutionRelationship(
        net.sf.webcat.core.AuthenticationDomain value )
    {
        if ( value == null )
        {
            net.sf.webcat.core.AuthenticationDomain object = institution();
            if ( object != null )
                removeObjectFromBothSidesOfRelationshipWithKey( object, "institution" );
        }
        else
        {
            addObjectToBothSidesOfRelationshipWithKey( value, "institution" );
        }
    }


}
