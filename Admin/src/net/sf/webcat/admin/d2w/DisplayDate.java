/*==========================================================================*\
 |  $Id: DisplayDate.java,v 1.2 2007/07/08 01:51:36 stedwar2 Exp $
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

package net.sf.webcat.admin.d2w;

import com.webobjects.appserver.*;

//-------------------------------------------------------------------------
/**
 * A customized version of {@link er.directtoweb.ERD2WDisplayDateOrNull}
 * that uses the user's specified date formatting and selected time zone.
 *
 *  @author edwards
 *  @version $Id: DisplayDate.java,v 1.2 2007/07/08 01:51:36 stedwar2 Exp $
 */
public class DisplayDate
    extends er.directtoweb.ERD2WDisplayDateOrNull
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     *
     * @param context The context to use
     */
    public DisplayDate( WOContext context )
    {
        super( context );
    }
}
