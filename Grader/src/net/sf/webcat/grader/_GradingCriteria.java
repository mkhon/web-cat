/*==========================================================================*\
 |  _GradingCriteria.java
 |*-------------------------------------------------------------------------*|
 |  Created by eogenerator
 |  DO NOT EDIT.  Make changes to GradingCriteria.java instead.
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
 * GradingCriteria.java.
 *
 * @author Generated by eogenerator
 * @version version suppressed to control auto-generation
 */
public abstract class _GradingCriteria
    extends er.extensions.ERXGenericRecord
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new _GradingCriteria object.
     */
    public _GradingCriteria()
    {
        super();
    }


    //~ Constants (for key names) .............................................

    // Attributes ---
    public static final String BLANK_LINE_PT_KEY = "blankLinePt";
    public static final String DEAD_TIME_DELTA_KEY = "deadTimeDelta";
    public static final String DIFF_LINE_SYNCING_KEY = "diffLineSyncing";
    public static final String EXTRA_LINE_PT_KEY = "extraLinePt";
    public static final String FLOAT_COMPARISON_STYLE_KEY = "floatComparisonStyle";
    public static final String FLOAT_NEGATIVE_DELTA_KEY = "floatNegativeDelta";
    public static final String FLOAT_POSITIVE_DELTA_KEY = "floatPositiveDelta";
    public static final String IGNORE_BLANK_LINES_KEY = "ignoreBlankLines";
    public static final String IGNORE_CASE_KEY = "ignoreCase";
    public static final String IGNORE_NONPRINTING_KEY = "ignoreNonprinting";
    public static final String IGNORE_PUNCTUATION_KEY = "ignorePunctuation";
    public static final String IGNORE_WHITESPACE_KEY = "ignoreWhitespace";
    public static final String NAME_KEY = "name";
    public static final String NORMALIZE_WHITESPACE_KEY = "normalizeWhitespace";
    public static final String PUNCTUATION_TO_IGNORE_KEY = "punctuationToIgnore";
    public static final String STRING_COMPARSION_STYLE_KEY = "stringComparsionStyle";
    public static final String TOKENIZING_STYLE_KEY = "tokenizingStyle";
    public static final String TRIM_WHITESPACE_KEY = "trimWhitespace";
    // To-one relationships ---
    public static final String AUTHOR_KEY = "author";
    // To-many relationships ---
    public static final String ASSIGNMENT_KEY = "assignment";
    public static final String ENTITY_NAME = "GradingCriteria";


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>blankLinePt</code> value.
     * @return the value of the attribute
     */
    public double blankLinePt()
    {
        Number result =
            (Number)storedValueForKey( "blankLinePt" );
        return ( result == null )
            ? 0.0
            : result.doubleValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>blankLinePt</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setBlankLinePt( double value )
    {
        Number actual =
            new Double( value );
        takeStoredValueForKey( actual, "blankLinePt" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>blankLinePt</code> value.
     * @return the value of the attribute
     */
    public Number blankLinePtRaw()
    {
        return (Number)storedValueForKey( "blankLinePt" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>blankLinePt</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setBlankLinePtRaw( Number value )
    {
        takeStoredValueForKey( value, "blankLinePt" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>deadTimeDelta</code> value.
     * @return the value of the attribute
     */
    public long deadTimeDelta()
    {
        Number result =
            (Number)storedValueForKey( "deadTimeDelta" );
        return ( result == null )
            ? 0L
            : result.intValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>deadTimeDelta</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setDeadTimeDelta( long value )
    {
        Number actual =
            new Long( value );
        takeStoredValueForKey( actual, "deadTimeDelta" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>deadTimeDelta</code> value.
     * @return the value of the attribute
     */
    public Number deadTimeDeltaRaw()
    {
        return (Number)storedValueForKey( "deadTimeDelta" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>deadTimeDelta</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setDeadTimeDeltaRaw( Number value )
    {
        takeStoredValueForKey( value, "deadTimeDelta" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>diffLineSyncing</code> value.
     * @return the value of the attribute
     */
    public boolean diffLineSyncing()
    {
        Number result =
            (Number)storedValueForKey( "diffLineSyncing" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>diffLineSyncing</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setDiffLineSyncing( boolean value )
    {
        Number actual =
            er.extensions.ERXConstant.integerForInt( value ? 1 : 0 );
        takeStoredValueForKey( actual, "diffLineSyncing" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>diffLineSyncing</code> value.
     * @return the value of the attribute
     */
    public Number diffLineSyncingRaw()
    {
        return (Number)storedValueForKey( "diffLineSyncing" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>diffLineSyncing</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setDiffLineSyncingRaw( Number value )
    {
        takeStoredValueForKey( value, "diffLineSyncing" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>extraLinePt</code> value.
     * @return the value of the attribute
     */
    public double extraLinePt()
    {
        Number result =
            (Number)storedValueForKey( "extraLinePt" );
        return ( result == null )
            ? 0.0
            : result.doubleValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>extraLinePt</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setExtraLinePt( double value )
    {
        Number actual =
            new Double( value );
        takeStoredValueForKey( actual, "extraLinePt" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>extraLinePt</code> value.
     * @return the value of the attribute
     */
    public Number extraLinePtRaw()
    {
        return (Number)storedValueForKey( "extraLinePt" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>extraLinePt</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setExtraLinePtRaw( Number value )
    {
        takeStoredValueForKey( value, "extraLinePt" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>floatComparisonStyle</code> value.
     * @return the value of the attribute
     */
    public boolean floatComparisonStyle()
    {
        Number result =
            (Number)storedValueForKey( "floatComparisonStyle" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>floatComparisonStyle</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setFloatComparisonStyle( boolean value )
    {
        Number actual =
            er.extensions.ERXConstant.integerForInt( value ? 1 : 0 );
        takeStoredValueForKey( actual, "floatComparisonStyle" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>floatComparisonStyle</code> value.
     * @return the value of the attribute
     */
    public Number floatComparisonStyleRaw()
    {
        return (Number)storedValueForKey( "floatComparisonStyle" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>floatComparisonStyle</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setFloatComparisonStyleRaw( Number value )
    {
        takeStoredValueForKey( value, "floatComparisonStyle" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>floatNegativeDelta</code> value.
     * @return the value of the attribute
     */
    public double floatNegativeDelta()
    {
        Number result =
            (Number)storedValueForKey( "floatNegativeDelta" );
        return ( result == null )
            ? 0.0
            : result.doubleValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>floatNegativeDelta</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setFloatNegativeDelta( double value )
    {
        Number actual =
            new Double( value );
        takeStoredValueForKey( actual, "floatNegativeDelta" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>floatNegativeDelta</code> value.
     * @return the value of the attribute
     */
    public Number floatNegativeDeltaRaw()
    {
        return (Number)storedValueForKey( "floatNegativeDelta" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>floatNegativeDelta</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setFloatNegativeDeltaRaw( Number value )
    {
        takeStoredValueForKey( value, "floatNegativeDelta" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>floatPositiveDelta</code> value.
     * @return the value of the attribute
     */
    public double floatPositiveDelta()
    {
        Number result =
            (Number)storedValueForKey( "floatPositiveDelta" );
        return ( result == null )
            ? 0.0
            : result.doubleValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>floatPositiveDelta</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setFloatPositiveDelta( double value )
    {
        Number actual =
            new Double( value );
        takeStoredValueForKey( actual, "floatPositiveDelta" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>floatPositiveDelta</code> value.
     * @return the value of the attribute
     */
    public Number floatPositiveDeltaRaw()
    {
        return (Number)storedValueForKey( "floatPositiveDelta" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>floatPositiveDelta</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setFloatPositiveDeltaRaw( Number value )
    {
        takeStoredValueForKey( value, "floatPositiveDelta" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>ignoreBlankLines</code> value.
     * @return the value of the attribute
     */
    public boolean ignoreBlankLines()
    {
        Number result =
            (Number)storedValueForKey( "ignoreBlankLines" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>ignoreBlankLines</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setIgnoreBlankLines( boolean value )
    {
        Number actual =
            er.extensions.ERXConstant.integerForInt( value ? 1 : 0 );
        takeStoredValueForKey( actual, "ignoreBlankLines" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>ignoreBlankLines</code> value.
     * @return the value of the attribute
     */
    public Number ignoreBlankLinesRaw()
    {
        return (Number)storedValueForKey( "ignoreBlankLines" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>ignoreBlankLines</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setIgnoreBlankLinesRaw( Number value )
    {
        takeStoredValueForKey( value, "ignoreBlankLines" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>ignoreCase</code> value.
     * @return the value of the attribute
     */
    public boolean ignoreCase()
    {
        Number result =
            (Number)storedValueForKey( "ignoreCase" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>ignoreCase</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setIgnoreCase( boolean value )
    {
        Number actual =
            er.extensions.ERXConstant.integerForInt( value ? 1 : 0 );
        takeStoredValueForKey( actual, "ignoreCase" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>ignoreCase</code> value.
     * @return the value of the attribute
     */
    public Number ignoreCaseRaw()
    {
        return (Number)storedValueForKey( "ignoreCase" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>ignoreCase</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setIgnoreCaseRaw( Number value )
    {
        takeStoredValueForKey( value, "ignoreCase" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>ignoreNonprinting</code> value.
     * @return the value of the attribute
     */
    public boolean ignoreNonprinting()
    {
        Number result =
            (Number)storedValueForKey( "ignoreNonprinting" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>ignoreNonprinting</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setIgnoreNonprinting( boolean value )
    {
        Number actual =
            er.extensions.ERXConstant.integerForInt( value ? 1 : 0 );
        takeStoredValueForKey( actual, "ignoreNonprinting" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>ignoreNonprinting</code> value.
     * @return the value of the attribute
     */
    public Number ignoreNonprintingRaw()
    {
        return (Number)storedValueForKey( "ignoreNonprinting" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>ignoreNonprinting</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setIgnoreNonprintingRaw( Number value )
    {
        takeStoredValueForKey( value, "ignoreNonprinting" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>ignorePunctuation</code> value.
     * @return the value of the attribute
     */
    public boolean ignorePunctuation()
    {
        Number result =
            (Number)storedValueForKey( "ignorePunctuation" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>ignorePunctuation</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setIgnorePunctuation( boolean value )
    {
        Number actual =
            er.extensions.ERXConstant.integerForInt( value ? 1 : 0 );
        takeStoredValueForKey( actual, "ignorePunctuation" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>ignorePunctuation</code> value.
     * @return the value of the attribute
     */
    public Number ignorePunctuationRaw()
    {
        return (Number)storedValueForKey( "ignorePunctuation" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>ignorePunctuation</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setIgnorePunctuationRaw( Number value )
    {
        takeStoredValueForKey( value, "ignorePunctuation" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>ignoreWhitespace</code> value.
     * @return the value of the attribute
     */
    public boolean ignoreWhitespace()
    {
        Number result =
            (Number)storedValueForKey( "ignoreWhitespace" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>ignoreWhitespace</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setIgnoreWhitespace( boolean value )
    {
        Number actual =
            er.extensions.ERXConstant.integerForInt( value ? 1 : 0 );
        takeStoredValueForKey( actual, "ignoreWhitespace" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>ignoreWhitespace</code> value.
     * @return the value of the attribute
     */
    public Number ignoreWhitespaceRaw()
    {
        return (Number)storedValueForKey( "ignoreWhitespace" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>ignoreWhitespace</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setIgnoreWhitespaceRaw( Number value )
    {
        takeStoredValueForKey( value, "ignoreWhitespace" );
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
     * Retrieve this object's <code>normalizeWhitespace</code> value.
     * @return the value of the attribute
     */
    public boolean normalizeWhitespace()
    {
        Number result =
            (Number)storedValueForKey( "normalizeWhitespace" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>normalizeWhitespace</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setNormalizeWhitespace( boolean value )
    {
        Number actual =
            er.extensions.ERXConstant.integerForInt( value ? 1 : 0 );
        takeStoredValueForKey( actual, "normalizeWhitespace" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>normalizeWhitespace</code> value.
     * @return the value of the attribute
     */
    public Number normalizeWhitespaceRaw()
    {
        return (Number)storedValueForKey( "normalizeWhitespace" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>normalizeWhitespace</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setNormalizeWhitespaceRaw( Number value )
    {
        takeStoredValueForKey( value, "normalizeWhitespace" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>punctuationToIgnore</code> value.
     * @return the value of the attribute
     */
    public String punctuationToIgnore()
    {
        return (String)storedValueForKey( "punctuationToIgnore" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>punctuationToIgnore</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setPunctuationToIgnore( String value )
    {
        takeStoredValueForKey( value, "punctuationToIgnore" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>stringComparsionStyle</code> value.
     * @return the value of the attribute
     */
    public int stringComparsionStyle()
    {
        Number result =
            (Number)storedValueForKey( "stringComparsionStyle" );
        return ( result == null )
            ? 0
            : result.intValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>stringComparsionStyle</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setStringComparsionStyle( int value )
    {
        Number actual =
            er.extensions.ERXConstant.integerForInt( value );
        takeStoredValueForKey( actual, "stringComparsionStyle" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>stringComparsionStyle</code> value.
     * @return the value of the attribute
     */
    public Number stringComparsionStyleRaw()
    {
        return (Number)storedValueForKey( "stringComparsionStyle" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>stringComparsionStyle</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setStringComparsionStyleRaw( Number value )
    {
        takeStoredValueForKey( value, "stringComparsionStyle" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>tokenizingStyle</code> value.
     * @return the value of the attribute
     */
    public boolean tokenizingStyle()
    {
        Number result =
            (Number)storedValueForKey( "tokenizingStyle" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>tokenizingStyle</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setTokenizingStyle( boolean value )
    {
        Number actual =
            er.extensions.ERXConstant.integerForInt( value ? 1 : 0 );
        takeStoredValueForKey( actual, "tokenizingStyle" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>tokenizingStyle</code> value.
     * @return the value of the attribute
     */
    public Number tokenizingStyleRaw()
    {
        return (Number)storedValueForKey( "tokenizingStyle" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>tokenizingStyle</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setTokenizingStyleRaw( Number value )
    {
        takeStoredValueForKey( value, "tokenizingStyle" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>trimWhitespace</code> value.
     * @return the value of the attribute
     */
    public boolean trimWhitespace()
    {
        Number result =
            (Number)storedValueForKey( "trimWhitespace" );
        return ( result == null )
            ? false
            : ( result.intValue() > 0 );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>trimWhitespace</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setTrimWhitespace( boolean value )
    {
        Number actual =
            er.extensions.ERXConstant.integerForInt( value ? 1 : 0 );
        takeStoredValueForKey( actual, "trimWhitespace" );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>trimWhitespace</code> value.
     * @return the value of the attribute
     */
    public Number trimWhitespaceRaw()
    {
        return (Number)storedValueForKey( "trimWhitespace" );
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>trimWhitespace</code>
     * property.
     * 
     * @param value The new value for this property
     */
    public void setTrimWhitespaceRaw( Number value )
    {
        takeStoredValueForKey( value, "trimWhitespace" );
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
     * Set the entity pointed to by the <code>authenticationDomain</code>
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
     * Set the entity pointed to by the <code>authenticationDomain</code>
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
     * Retrieve the entities pointed to by the <code>assignment</code>
     * relationship.
     * @return an NSArray of the entities in the relationship
     */
    public NSArray assignment()
    {
        return (NSArray)storedValueForKey( "assignment" );
    }


    // ----------------------------------------------------------
    /**
     * Replace the list of entities pointed to by the
     * <code>assignment</code> relationship.
     * 
     * @param value The new set of entities to relate to
     */
    public void setAssignment( NSMutableArray value )
    {
        takeStoredValueForKey( value, "assignment" );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>assignment</code>
     * relationship (DO NOT USE--instead, use
     * <code>addToAssignmentRelationship()</code>.
     * This method is provided for WebObjects use.
     * 
     * @param value The new entity to relate to
     */
    public void addToAssignment( net.sf.webcat.grader.Assignment value )
    {
        NSMutableArray array = (NSMutableArray)assignment();
        willChange();
        array.addObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>assignment</code>
     * relationship (DO NOT USE--instead, use
     * <code>removeFromAssignmentRelationship()</code>.
     * This method is provided for WebObjects use.
     * 
     * @param value The entity to remove from the relationship
     */
    public void removeFromAssignment( net.sf.webcat.grader.Assignment value )
    {
        NSMutableArray array = (NSMutableArray)assignment();
        willChange();
        array.removeObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Add a new entity to the <code>assignment</code>
     * relationship.
     * 
     * @param value The new entity to relate to
     */
    public void addToAssignmentRelationship( net.sf.webcat.grader.Assignment value )
    {
        addObjectToBothSidesOfRelationshipWithKey(
            value, "assignment" );
    }


    // ----------------------------------------------------------
    /**
     * Remove a specific entity from the <code>assignment</code>
     * relationship.
     * 
     * @param value The entity to remove from the relationship
     */
    public void removeFromAssignmentRelationship( net.sf.webcat.grader.Assignment value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "assignment" );
    }


    // ----------------------------------------------------------
    /**
     * Create a brand new object that is a member of the
     * <code>assignment</code> relationship.
     * 
     * @return The new entity
     */
    public net.sf.webcat.grader.Assignment createAssignmentRelationship()
    {
        EOClassDescription eoClassDesc = EOClassDescription
            .classDescriptionForEntityName( "Assignment" );
        EOEnterpriseObject eoObject = eoClassDesc
            .createInstanceWithEditingContext( editingContext(), null );
        editingContext().insertObject( eoObject );
        addObjectToBothSidesOfRelationshipWithKey(
            eoObject, "assignment" );
        return (net.sf.webcat.grader.Assignment)eoObject;
    }


    // ----------------------------------------------------------
    /**
     * Remove and then delete a specific entity that is a member of the
     * <code>assignment</code> relationship.
     * 
     * @param value The entity to remove from the relationship and then delete
     */
    public void deleteAssignmentRelationship( net.sf.webcat.grader.Assignment value )
    {
        removeObjectFromBothSidesOfRelationshipWithKey(
            value, "assignment" );
        editingContext().deleteObject( value );
    }


    // ----------------------------------------------------------
    /**
     * Remove (and then delete, if owned) all entities that are members of the
     * <code>assignment</code> relationship.
     */
    public void deleteAllAssignmentRelationships()
    {
        Enumeration objects = assignment().objectEnumerator();
        while ( objects.hasMoreElements() )
            deleteAssignmentRelationship(
                (net.sf.webcat.grader.Assignment)objects.nextElement() );
    }


}
