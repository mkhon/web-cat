/*==========================================================================*\
 |  $Id: ConfirmingActionTestPage.java,v 1.3 2010/10/24 18:49:06 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2010 Virginia Tech
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

package org.webcat.core;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

//-------------------------------------------------------------------------
/**
 * A test page to show how to use confirming actions--buttons with
 * confirmation pop-up dialogs.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.3 $, $Date: 2010/10/24 18:49:06 $
 */
public class ConfirmingActionTestPage
    extends WCComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    public ConfirmingActionTestPage(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    public boolean checkBoxChecked;
    public String textBoxValue;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public WOActionResults dummyAction()
    {
        System.out.println("Executing dummy action");
        return null;
    }


    // ----------------------------------------------------------
    public WOActionResults processThings()
    {
        return new ConfirmingAction(this)
        {
            // ----------------------------------------------------------
            @Override
            protected String confirmationMessage()
            {
                return "Confirming the values <b>" + checkBoxChecked
                    + "</b> and <b>" + textBoxValue + "</b>?";
            }

            // ----------------------------------------------------------
            @Override
            protected WOActionResults performStandardAction()
            {
                return null;
            }
        };
    }
}
