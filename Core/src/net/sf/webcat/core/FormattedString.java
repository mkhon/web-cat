/*==========================================================================*\
 |  $Id: FormattedString.java,v 1.1 2006/02/19 19:03:09 stedwar2 Exp $
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
 * Display a (possibly formatted) text string.
 *
 * @author Stephen Edwards
 * @version $Id: FormattedString.java,v 1.1 2006/02/19 19:03:09 stedwar2 Exp $
 */
public class FormattedString
    extends WOComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new Footer object.
     *
     * @param context The page's context
     */
    public FormattedString( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public byte   format;
    public String value;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public boolean preformatted()
    {
        return format == 1;
    }
}
