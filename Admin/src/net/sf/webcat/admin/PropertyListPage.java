/*==========================================================================*\
 |  $Id: PropertyListPage.java,v 1.1 2006/02/19 19:06:50 stedwar2 Exp $
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

package net.sf.webcat.admin;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.sf.webcat.core.*;

import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * A property listing page.
 *
 *  @author edwards
 *  @version $Id: PropertyListPage.java,v 1.1 2006/02/19 19:06:50 stedwar2 Exp $
 */
public class PropertyListPage
    extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new PropertyList object.
     * 
     * @param context The context to use
     */
    public PropertyListPage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public java.util.Map.Entry property;
    public WODisplayGroup      propertyDisplayGroup;
    public int                 propertyIndex;
    public String              newPropertyName;
    public String              newPropertyValue;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public String title()
    {
        return "Application Properties";
    }


    // ----------------------------------------------------------
    /**
     * Access the key associated with the current property.
     * @return The property key as a string
     */
    public String propertyKey()
    {
        return (String)property.getKey();
    }


    // ----------------------------------------------------------
    /**
     * Access the value associated with the current property.
     * @return The property value as a string
     */
    public String propertyValue()
    {
        String result = "";
        String value = (String)property.getValue();
        int pos = 0;
        while ( pos < value.length() )
        {
            int remaining = value.length() - pos;
            if ( remaining > 60 )
            {
                result += value.substring( pos, pos + 60 ) + "<br/>";
                pos += 60;
            }
            else
            {
                result += value.substring( pos, value.length() );
                pos += remaining;
            }
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Setup this page before rendering.
     */
    public void awake()
    {
        super.awake();
        propertyDisplayGroup.setObjectArray( new NSMutableArray(
            ( (Session)session() ).properties().inheritedEntrySet().toArray()
        ));
    }


    // ----------------------------------------------------------
    public WOComponent back()
    {
        clearErrors();
        return pageWithName( SettingsPage.class.getName() );
    }


    // ----------------------------------------------------------
    public WOComponent setNewProperty()
    {
        clearErrors();
        if ( newPropertyName == null || newPropertyName.equals( "" ) )
        {
            errorMessage( "Please specify a property name to set." );
        }
        else
        {
            if ( newPropertyValue == null )
            {
                newPropertyValue = "";
            }
            System.setProperty( newPropertyName, newPropertyValue );
            er.extensions.ERXLogger.configureLoggingWithSystemProperties();
            errorMessage( "System property \"" + newPropertyName
                + "\" set to \"" + newPropertyValue + "\"." );
        }
        return null;
    }


    //~ Instance/static variables .............................................
    static Logger log = Logger.getLogger( PropertyListPage.class );
}
