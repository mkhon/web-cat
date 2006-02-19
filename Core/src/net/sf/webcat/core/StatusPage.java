/*==========================================================================*\
 |  $Id: StatusPage.java,v 1.1 2006/02/19 19:03:09 stedwar2 Exp $
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

import com.webobjects.appserver.*;

//-------------------------------------------------------------------------
/**
* Represents a standard Web-CAT page that has not yet been implemented
* (is "to be defined").
*
*  @author Stephen Edwards
*  @version $Id: StatusPage.java,v 1.1 2006/02/19 19:03:09 stedwar2 Exp $
*/
public class StatusPage
    extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new StatusPage object.
     * 
     * @param context The context to use
     */
    public StatusPage( WOContext context )
    {
        super( context );
//        throw new RuntimeException( "Ha!" );
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Jump to the start page for making a new submission.
     * @return the new page
     */
    public WOComponent newSubmission()
    {
        return pageWithName(
            wcSession().tabs.selectById( "NewSubmission" ).pageName() );
    }


    // ----------------------------------------------------------
    /**
     * Jump to the start page for selecting past results.
     * @return the new page
     */
    public WOComponent pastResults()
    {
        return pageWithName(
            wcSession().tabs.selectById( "PastResults" ).pageName() );
    }
}
