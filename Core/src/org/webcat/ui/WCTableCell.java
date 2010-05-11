/*==========================================================================*\
 |  $Id: WCTableCell.java,v 1.1 2010/05/11 14:51:58 aallowat Exp $
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

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSDictionary;

//------------------------------------------------------------------------
/**
 * An abstract class that all ObjectTable cell classes must extend.
 * 
 * @author Tony Allevato
 * @version $Id: WCTableCell.java,v 1.1 2010/05/11 14:51:58 aallowat Exp $
 */
public abstract class WCTableCell extends WOComponent
{
    //~ Constructors ..........................................................
    
    // ----------------------------------------------------------
    public WCTableCell(WOContext context)
    {
        super(context);
    }


    //~ KVC attributes (must be public) .......................................

    public Object value;
    public NSDictionary<String, Object> properties;

    public String jsWrapperID;
    public int columnIndex;
    public int indexInBatch;
    
    
    // ----------------------------------------------------------
    /**
     * Sets the dictionary of cell-specific properties for this cell.
     * Subclasses can override this method to intercept the dictionary and do
     * any other initialization that is necessary (for example, the StringCell
     * converts numberFormat and dateFormat strings into a true Format object).
     * 
     * If overridden, subclasses must call the superclass method.
     * 
     * @param props a dictionary containing cell-specific properties
     */
    public void setProperties(NSDictionary<String, Object> props)
    {
        properties = props;
    }


    // ----------------------------------------------------------
    @Override
    public void pushValuesToParent()
    {
        // Do nothing.
    }
}
