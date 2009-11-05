/*==========================================================================*\
 |  $Id: WCDragHandle.java,v 1.1 2009/11/05 20:25:09 aallowat Exp $
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

package net.sf.webcat.ui;

import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver._private.WOHTMLDynamicElement;
import com.webobjects.foundation.NSDictionary;
import net.sf.webcat.ui._base.DojoElement;

//------------------------------------------------------------------------
/**
 * A drag handle that can be placed on an item in a drag-and-drop source, such
 * as a row in a WCStyledTable.
 *
 * @author Tony Allevato
 * @version $Id: WCDragHandle.java,v 1.1 2009/11/05 20:25:09 aallowat Exp $
 */
public class WCDragHandle extends WOHTMLDynamicElement
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    public WCDragHandle(String aName,
            NSDictionary<String, WOAssociation> someAssociations,
            WOElement template)
    {
        super("div", someAssociations, template);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    protected void _appendCloseTagToResponse(WOResponse response,
            WOContext context)
    {
        // There appear to be some Dojo issues if an element without content
        // uses <div /> instead of <div></div>, so we fix that here.

        response.appendContentString("</");
        response.appendContentString(elementName());
        response.appendContentCharacter('>');
    }


    // ----------------------------------------------------------
    @Override
    public void appendAttributesToResponse(
            WOResponse response, WOContext context)
    {
        super.appendAttributesToResponse(response, context);

        _appendTagAttributeAndValueToResponse(response, "class",
                "dojoDndHandle", false);
    }
}
