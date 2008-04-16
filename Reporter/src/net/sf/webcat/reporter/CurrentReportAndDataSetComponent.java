/*==========================================================================*\
 |  $Id: CurrentReportAndDataSetComponent.java,v 1.1 2008/04/16 18:16:18 aallowat Exp $
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

import net.sf.webcat.core.WCComponent;
import com.webobjects.appserver.WOContext;

// ------------------------------------------------------------------------
/**
 * A small info-block that displays the current report template being generated
 * and the current data set being constructed.
 *
 * @author Tony Allevato
 * @version $Id: CurrentReportAndDataSetComponent.java,v 1.1 2008/04/16 18:16:18 aallowat Exp $
 */
public class CurrentReportAndDataSetComponent extends WCComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * This is the default constructor
     * @param context The page's context
     */
    public CurrentReportAndDataSetComponent(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    public ReportTemplate reportTemplate;
    public ReportDataSet dataSet;
}
