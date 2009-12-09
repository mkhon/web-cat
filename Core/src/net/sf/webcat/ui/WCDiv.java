/*==========================================================================*\
 |  $Id: WCDiv.java,v 1.3 2009/12/09 04:55:11 aallowat Exp $
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

import net.sf.webcat.ui._base.DojoElement;
import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver._private.WODynamicElementCreationException;
import com.webobjects.foundation.NSDictionary;

//--------------------------------------------------------------------------
/**
 * Essentially a regular div, but with two minor enhancements: a proper end-tag
 * will be generated if the element contains no content, and since this is a
 * WebObjects element instead of a straight HTML tag, inline bindings can be
 * used directly in attributes (attr="$value") instead of being required to use
 * WOString elements to evaluate them.
 *
 * @author Tony Allevato
 * @version $Id: WCDiv.java,v 1.3 2009/12/09 04:55:11 aallowat Exp $
 */
public class WCDiv extends DojoElement
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public WCDiv(String name,
            NSDictionary<String, WOAssociation> someAssociations,
            WOElement template)
    {
        super("div", someAssociations, template);

        if (_dojoType == null)
        {
            throw new WODynamicElementCreationException(
                    "<" + getClass().getName() + "> 'dojoType' binding must "
                    + "be specified.");
        }
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    public String dojoType()
    {
        return null;
    }
}
