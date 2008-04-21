/*==========================================================================*\
 |  $Id: AdminLicensePage.java,v 1.1 2008/04/21 02:53:16 stedwar2 Exp $
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

package net.sf.webcat.admin;

import com.webobjects.appserver.*;
import net.sf.webcat.core.*;

// -------------------------------------------------------------------------
/**
 *  A subtab page for displaying the license.  This page is distinct from
 *  {@link net.sf.webcat.core.install.LicensePage}.  This page is wrapped
 *  by tabbed navigation and displays the license in a scrollable div,
 *  while the other page is bare and is designed to deliver the license
 *  alone via a direct action.
 *
 *  @author edwards
 *  @version $Id: AdminLicensePage.java,v 1.1 2008/04/21 02:53:16 stedwar2 Exp $
 */
public class AdminLicensePage
    extends WOComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new PropertyList object.
     *
     * @param context The context to use
     */
    public AdminLicensePage( WOContext context )
    {
        super( context );
    }
}
