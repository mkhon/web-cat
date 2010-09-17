/*==========================================================================*\
 |  $Id: WCQueryPageTemplate.java,v 1.1 2010/05/11 14:51:43 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2009 Virginia Tech
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

package org.webcat.admin;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eoaccess.*;
import er.directtoweb.*;

//-------------------------------------------------------------------------
/**
 * A query page generated by the direct-to-web template engine.
 *
 *  @author Stephen Edwards
 *  @version $Id: WCQueryPageTemplate.java,v 1.1 2010/05/11 14:51:43 aallowat Exp $
 */
public class WCQueryPageTemplate
    extends er.directtoweb.pages.ERD2WQueryPage
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new WCQueryPageTemplate object.
     *
     * @param aContext The context to use
     */
    public WCQueryPageTemplate( WOContext aContext )
    {
        super( aContext );
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse( WOResponse arg0, WOContext arg1 )
    {
        rowFlip = true;
        super.appendToResponse( arg0, arg1 );
    }


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
    public String backgroundColorForRow()
    {
        rowFlip = !rowFlip;
        if (rowFlip || !alternateRowColor())
        {
            return backgroundColorForTable();
        } else
        {
            return backgroundColorForTableDark();
        }
    }


    // ----------------------------------------------------------
    public void setBackgroundColorForRow( String value )
    {
        // This isn't a settable attribute, so do nothing
    }


    // ----------------------------------------------------------
    public String cssClassForRow()
    {
        rowFlip = !rowFlip;
        if (rowFlip && alternateRowColor())
        {
            return "e";
        } else
        {
            return "o";
        }
    }


    //~ Instance/static variables .............................................

    private boolean rowFlip = true;
}

