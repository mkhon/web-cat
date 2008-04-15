/*==========================================================================*\
 |  $Id: DataSetListWithSelection.java,v 1.4 2008/04/15 04:09:22 aallowat Exp $
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

package net.sf.webcat.reporter;

import com.webobjects.appserver.*;
import com.webobjects.foundation.NSArray;
import net.sf.webcat.core.WCComponent;

//-------------------------------------------------------------------------
/**
 * This component is used to display a list of data sets that are defined in
 * a report during the query construction phase of report generation. It also
 * allows one of the data sets to act as the current "selection" -- this data
 * set will be highlighted and have its description elaborated in the list
 * for emphasis.
 *
 * @binding dataSets the data sets to be displayed in the list
 * @binding selection a data set equal to one of the elements in dataSets,
 *          this entry will be highlighted when the list is displayed
 *
 * @author Tony Allevato
 * @version $Id: DataSetListWithSelection.java,v 1.4 2008/04/15 04:09:22 aallowat Exp $
 */
public class DataSetListWithSelection
    extends WCComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * This is the default constructor
     * @param context The page's context
     */
    public DataSetListWithSelection(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    /**
     * The data sets to iterate over in the list.
     */
    public NSArray<ReportDataSet> dataSets;

    /**
     * The current data set that should be highlighted and have its description
     * displayed in the list.
     */
    public ReportDataSet selection;


    // --- Internal state ---------------------------------

    /**
     * The current data set in the iteration.
     */
    public ReportDataSet dataSet;
}