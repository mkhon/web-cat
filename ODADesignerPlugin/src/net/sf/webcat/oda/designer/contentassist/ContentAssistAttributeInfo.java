/*==========================================================================*\
 |  $Id: ContentAssistAttributeInfo.java,v 1.3 2008/11/11 15:26:19 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2008 Virginia Tech
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

package net.sf.webcat.oda.designer.contentassist;

import org.json.JSONException;
import org.json.JSONObject;

// ------------------------------------------------------------------------
/**
 * Attributes about a type that are displayed in a content assist request.
 *
 * @author Tony Allevato (Virginia Tech Computer Science)
 * @version $Id: ContentAssistAttributeInfo.java,v 1.3 2008/11/11 15:26:19 aallowat Exp $
 */
public class ContentAssistAttributeInfo
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new content assist attribute info object.
     *
     * @param name the name of the property
     * @param type the type of the property
     */
    public ContentAssistAttributeInfo(String name, String type,
            JSONObject properties)
    {
        this.name = name;
        this.type = type;
        this.properties = properties;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets the name of the property.
     *
     * @return the name of the property
     */
    public String name()
    {
        return name;
    }

    // ----------------------------------------------------------
    /**
     * Gets the type of the property.
     *
     * @return the type of the property
     */
    public String type()
    {
        return type;
    }
    
    
    // ----------------------------------------------------------
    /**
     * 
     *
     * @return
     */
    public Object valueForProperty(String property)
    {
        try
        {
            return properties.get(property);
        }
        catch (JSONException e)
        {
            return null;
        }
    }
    
    
    // ----------------------------------------------------------
    /**
     * 
     *
     * @return
     */
    public JSONObject allPropertyValues()
    {
        return properties;
    }


    //~ Static/instance variables .............................................

    private String name;
    private String type;
    private JSONObject properties;
}
