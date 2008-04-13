/*==========================================================================*\
 |  $Id: ContentAssistAttributeInfo.java,v 1.2 2008/04/13 22:04:52 aallowat Exp $
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

// ------------------------------------------------------------------------
/**
 * Attributes about a type that are displayed in a content assist request.
 *
 * @author Tony Allevato (Virginia Tech Computer Science)
 * @version $Id: ContentAssistAttributeInfo.java,v 1.2 2008/04/13 22:04:52 aallowat Exp $
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
    public ContentAssistAttributeInfo(String name, String type)
    {
        this.name = name;
        this.type = type;
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


    //~ Static/instance variables .............................................

    private String name;
    private String type;
}
