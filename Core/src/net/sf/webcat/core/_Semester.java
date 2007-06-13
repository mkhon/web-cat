/*==========================================================================*\
 |  _Semester.java
 |*-------------------------------------------------------------------------*|
 |  Created by eogenerator
 |  DO NOT EDIT.  Make changes to Semester.java instead.
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
 * Semester.java.
 *
 * @author Generated by eogenerator
 * @version version suppressed to control auto-generation
 */
public abstract class _Semester
    extends er.extensions.ERXGenericRecord
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new _Semester object.
     */
    public _Semester()
    {
        super();
    }


    //~ Constants (for key names) .............................................

    // Attributes ---
    public static final String SEASON_KEY = "season";
    public static final String SEMESTER_END_DATE_KEY = "semesterEndDate";
    public static final String SEMESTER_START_DATE_KEY = "semesterStartDate";
    public static final String YEAR_KEY = "year";
    // To-one relationships ---
    // To-many relationships ---
    // Fetch specifications ---
    public static final String ENTITY_NAME = "Semester";


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>season</code> value.
     * @return the value of the attribute
     */
    public Number season()
    {
        return (Number)storedValueForKey( "season" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>season</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setSeason( Number value )
    {
        takeStoredValueForKey( value, "season" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>semesterEndDate</code> value.
     * @return the value of the attribute
     */
    public NSTimestamp semesterEndDate()
    {
        return (NSTimestamp)storedValueForKey( "semesterEndDate" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>semesterEndDate</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setSemesterEndDate( NSTimestamp value )
    {
        takeStoredValueForKey( value, "semesterEndDate" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>semesterStartDate</code> value.
     * @return the value of the attribute
     */
    public NSTimestamp semesterStartDate()
    {
        return (NSTimestamp)storedValueForKey( "semesterStartDate" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>semesterStartDate</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setSemesterStartDate( NSTimestamp value )
    {
        takeStoredValueForKey( value, "semesterStartDate" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>year</code> value.
     * @return the value of the attribute
     */
    public int year()
    {
        Number result =
            (Number)storedValueForKey( "year" );
        return ( result == null )
            ? 0
            : result.intValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>year</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setYear( int value )
    {
        Number actual =
            er.extensions.ERXConstant.integerForInt( value );
        takeStoredValueForKey( actual, "year" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>year</code> value.
     * @return the value of the attribute
     */
    public Number yearRaw()
    {
        return (Number)storedValueForKey( "year" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>year</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setYearRaw( Number value )
    {
        takeStoredValueForKey( value, "year" );
    }


}
