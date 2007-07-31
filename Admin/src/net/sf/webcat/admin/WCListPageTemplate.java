/*==========================================================================*\
 |  $Id: WCListPageTemplate.java,v 1.3 2007/07/31 19:22:23 stedwar2 Exp $
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
import com.webobjects.foundation.*;
import com.webobjects.directtoweb.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eoaccess.*;
import er.directtoweb.*;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * The template for D2W list pages in Web-CAT.
 *
 * @author edwards
 * @version $Id: WCListPageTemplate.java,v 1.3 2007/07/31 19:22:23 stedwar2 Exp $
 */
public class WCListPageTemplate
    extends er.directtoweb.ERD2WListPage
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new WCListPageTemplate object.
     *
     * @param context The page's context
     */
    public WCListPageTemplate( WOContext context )
    {
        super( context );
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Returns the alternate row color for tables on this page.
     * Because of the way the alternating row info is computed,
     * this is really the color of the "first" row.  The d2w.d2wmodel
     * rule file sets the table background to "#eeeeee" for list
     * tasks to create the alternating effect.
     *
     * @return The color as a string
     */
    public String backgroundColorForTableDark()
    {
        return "white";
    }


    // ----------------------------------------------------------
    public void setLocalContext( D2WContext arg0 )
    {
        super.setLocalContext( arg0 );
        if ( setUpSortOrdering )
        {
          // override default sort ordering with a new one from the
          // d2w properties
          NSArray sortOrderings = sortOrderings();
            if ( sortOrderings != null )
            {
                displayGroup().setSortOrderings( sortOrderings );
            }
            setUpSortOrdering = false;
        }
    }


    // ----------------------------------------------------------
    public NSArray sortOrderings()
    {
        NSArray sortOrderings = null;
        if ( userPreferencesCanSpecifySorting() )
        {
            sortOrderings = (NSArray)
                userPreferencesValueForPageConfigurationKey("sortOrdering");
            if ( log.isDebugEnabled() )
                log.debug(
                    "Found sort Orderings in user prefs " + sortOrderings);
        }
        if (sortOrderings == null)
        {
            sortOrderings = (NSArray)d2wContext().valueForKey(
                "defaultSortOrdering" );
            if (log.isDebugEnabled())
                log.debug("Found sort Orderings in rules " + sortOrderings);
        }
        return sortOrderings;
    }


    //~ Instance/static variables .............................................
    private boolean setUpSortOrdering = true;
}
