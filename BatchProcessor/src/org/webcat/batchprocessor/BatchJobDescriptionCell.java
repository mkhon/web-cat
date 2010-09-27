/*==========================================================================*\
 |  $Id: BatchJobDescriptionCell.java,v 1.2 2010/09/27 00:15:32 stedwar2 Exp $
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

package org.webcat.batchprocessor;

import org.webcat.ui.WCTableCell;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

//-------------------------------------------------------------------------
/**
 * A WCTable cell that provides a clickable link for a batch job, as well as
 * an indicator of whether it is suspended.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.2 $, $Date: 2010/09/27 00:15:32 $
 */
public class BatchJobDescriptionCell extends WCTableCell
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public BatchJobDescriptionCell(WOContext context)
    {
        super(context);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Performs the action named in the "action" property on the component that
     * contains the ObjectTable.
     *
     * @return the results of the action
     */
    public WOActionResults performAction()
    {
        String action = (String) properties.objectForKey("action");
        return parent().performParentAction(action);
    }
}
