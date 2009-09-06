/*==========================================================================*\
 |  $Id: SamplePage.java,v 1.1 2009/09/06 02:39:17 stedwar2 Exp $
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

package net.sf.webcat.admin;

import com.webobjects.appserver.*;

// -------------------------------------------------------------------------
/**
 * A property listing page.
 *
 *  @author edwards
 *  @author Last changed by $Author: stedwar2 $
 *  @version $Revision: 1.1 $, $Date: 2009/09/06 02:39:17 $
 */
public class SamplePage
    extends WOComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new PropertyList object.
     *
     * @param context The context to use
     */
    public SamplePage(WOContext context)
    {
        super(context);
    }
}
