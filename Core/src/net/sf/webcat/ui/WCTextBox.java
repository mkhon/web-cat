/*==========================================================================*\
 |  $Id: WCTextBox.java,v 1.1 2009/04/28 15:25:06 aallowat Exp $
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

import net.sf.webcat.ui._base.DojoFormElement;
import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOElement;
import com.webobjects.foundation.NSDictionary;

//------------------------------------------------------------------------
/**
 * A basic text box that automatically takes on Dojo styling.
 * 
 * <h2>Bindings</h2>
 * <dl>
 * <dt>value</dt>
 * <dd>The text entered into the text box.</dd>
 * </dl>
 * 
 * @author Tony Allevato
 * @version $Id: WCTextBox.java,v 1.1 2009/04/28 15:25:06 aallowat Exp $
 */
public class WCTextBox extends DojoFormElement
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public WCTextBox(String name,
            NSDictionary<String, WOAssociation> someAssociations,
            WOElement template)
    {
        super("input", someAssociations, template);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    public String dojoType()
    {
        return "dijit.form.TextBox";
    }
}
