/*==========================================================================*\
 |  $Id: WCSearchField.java,v 1.1 2010/10/28 00:37:30 aallowat Exp $
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

package org.webcat.ui;

import org.webcat.ui.util.ComponentIDGenerator;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

//-------------------------------------------------------------------------
/**
 * Displays a search field, implemented by a text box that automatically calls
 * a remote action on keyUp events, as well as a spinner that shows that the
 * operation is in progress.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2010/10/28 00:37:30 $
 */
public class WCSearchField extends WOComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initializes a new WCSearchField.
     *
     * @param context
     */
    public WCSearchField(WOContext context)
    {
        super(context);
    }


    //~ KVC attributes (must be public) .......................................

    public ComponentIDGenerator idFor;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    public void appendToResponse(WOResponse response, WOContext context)
    {
        idFor = new ComponentIDGenerator(this);

        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }


    // ----------------------------------------------------------
    public String id()
    {
        return (String) valueForBinding("id");
    }


    // ----------------------------------------------------------
    public WOActionResults action()
    {
        return (WOActionResults) valueForBinding("action");
    }


    // ----------------------------------------------------------
    public String searchText()
    {
        return (String) valueForBinding("searchText");
    }


    // ----------------------------------------------------------
    public String style()
    {
        return (String) valueForBinding("style");
    }


    // ----------------------------------------------------------
    public void setSearchText(String value)
    {
        setValueForBinding(value, "searchText");
    }


    // ----------------------------------------------------------
    public String onClearClickScript()
    {
        return "dijit.byId('" + id() + "').attr('value', ''); "
            + "dijit.byId('" + idFor.get("spinner") + "').start(); "
            + "webcat.searchField.forceChange(this, "
            + idFor.get("searchAction") + ");";
    }
}
