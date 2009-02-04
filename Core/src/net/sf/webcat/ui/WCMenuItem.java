/*==========================================================================*\
 |  $Id: WCMenuItem.java,v 1.1 2009/02/04 18:54:01 aallowat Exp $
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

package net.sf.webcat.ui;

import net.sf.webcat.ui._base.DojoActionFormElement;
import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOElement;
import com.webobjects.foundation.NSDictionary;

//--------------------------------------------------------------------------
/**
 * A menu item that can execute an action, either as a page-load or an Ajax
 * request.
 * 
 * TODO describe bindings
 * 
 * @author Tony Allevato
 * @version $Id: WCMenuItem.java,v 1.1 2009/02/04 18:54:01 aallowat Exp $
 */
public class WCMenuItem extends DojoActionFormElement
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public WCMenuItem(String name,
            NSDictionary<String, WOAssociation> someAssociations,
            WOElement template)
    {
        super("div", someAssociations, template);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    public String dojoType()
    {
        return "dijit.MenuItem";
    }
    
    
    // ----------------------------------------------------------
    protected boolean needsShadowButton()
    {
        return true;
    }
}
