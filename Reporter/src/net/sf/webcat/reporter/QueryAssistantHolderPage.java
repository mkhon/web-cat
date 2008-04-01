/*==========================================================================*\
 |  $Id: QueryAssistantHolderPage.java,v 1.2 2008/04/01 18:10:04 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2008 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU General Public License as published by
 |  the Free Software Foundation; either version 2 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU General Public License
 |  along with Web-CAT; if not, write to the Free Software
 |  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 |
 |  Project manager: Stephen Edwards <edwards@cs.vt.edu>
 |  Virginia Tech CS Dept, 660 McBryde Hall (0106), Blacksburg, VA 24061 USA
\*==========================================================================*/

package net.sf.webcat.reporter;

import com.webobjects.appserver.*;
import com.webobjects.foundation.NSArray;
import net.sf.webcat.core.WCComponent;
import net.sf.webcat.reporter.queryassistants.AbstractQueryAssistantModel;
import net.sf.webcat.reporter.queryassistants.QueryAssistantManager;

//-------------------------------------------------------------------------
/**
 * A page ...
 *
 * @author Tony Allevato
 * @version $Id: QueryAssistantHolderPage.java,v 1.2 2008/04/01 18:10:04 stedwar2 Exp $
 */
public class QueryAssistantHolderPage
    extends ReporterComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new page.
     * @param context The page's context
     */
    public QueryAssistantHolderPage(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    /**
     * The component name of the query assistant that should be rendered on
     * this page.
     */
    public String queryAssistantComponentName;

    /**
     * The current data set for which a query is being constructed.
     */
    public ReportDataSet dataSet;

    /**
     * The query assistant model (whose specific type corresponds to that of
     * the selected query assistant) that contains information about the query.
     */
    public AbstractQueryAssistantModel model;

    /**
     * If this is not null, then the user who created this query will be able
     * to recall it when generating future reports, for convenience.
     */
    public String queryDescription;
}