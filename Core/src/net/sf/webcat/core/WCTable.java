/*==========================================================================*\
 |  $Id: WCTable.java,v 1.2 2008/04/02 00:50:26 stedwar2 Exp $
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

package net.sf.webcat.core;

import com.webobjects.appserver.*;

// -------------------------------------------------------------------------
/**
 * A WOGenericContainer that represents a table, formatted and styled
 * the standard Web-CAT way.
 *
 * @author Stephen Edwards
 * @version $Id: WCTable.java,v 1.2 2008/04/02 00:50:26 stedwar2 Exp $
 */

public class WCTable
extends WOComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new WCTable object.
     *
     * @param context The page's context
     */
    public WCTable( WOContext context )
    {
        super( context );
    }
}
