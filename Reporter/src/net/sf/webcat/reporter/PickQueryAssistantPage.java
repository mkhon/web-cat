/*==========================================================================*\
 |  $Id: PickQueryAssistantPage.java,v 1.3 2008/04/01 17:31:43 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006 Virginia Tech
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
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import net.sf.webcat.reporter.queryassistants.AbstractQueryAssistantModel;
import net.sf.webcat.reporter.queryassistants.QueryAssistantDescriptor;
import net.sf.webcat.reporter.queryassistants.QueryAssistantManager;

//-------------------------------------------------------------------------
/**
 * This page allows the user to pick which query assistant they want to use.
 *
 * @author aallowat
 * @version $Id: PickQueryAssistantPage.java,v 1.3 2008/04/01 17:31:43 stedwar2 Exp $
 */
public class PickQueryAssistantPage
    extends ReporterComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new page.
     * @param context The page's context
     */
    public PickQueryAssistantPage(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    public ReportDataSet dataSet;
    public NSArray<ReportDataSet> dataSets;
    public ReportDataSet iterDataSet;
    public NSArray<QueryAssistantDescriptor> queryAssistants;
    public QueryAssistantDescriptor queryAssistant;
    public NSArray<ReportQuery> savedQueries;
    public ReportQuery savedQuery;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
    	queryAssistants = QueryAssistantManager.getInstance().
    		assistantsForEntity(dataSet.wcEntityName());

    	savedQueries = ReportQuery.objectsForUserAndEntitySavedQueries(
    		localContext(), user(), dataSet.wcEntityName());

    	super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public String linkPartOfQueryAssistantDescription()
    {
    	String desc = queryAssistant.description();
    	int pipe = desc.indexOf('|');

    	if (pipe >= 0)
        {
    		return desc.substring(0, pipe);
        }
    	else
        {
    		return desc;
        }
    }


    // ----------------------------------------------------------
    public String nonLinkPartOfQueryAssistantDescription()
    {
    	String desc = queryAssistant.description();
    	int pipe = desc.indexOf('|');

    	if (pipe >= 0)
        {
    		return desc.substring(pipe + 1);
        }
    	else
        {
    		return desc;
        }
    }


    // ----------------------------------------------------------
    public WOComponent chooseQueryAssistant()
    {
    	localPageController().selectNextQueryAssistant(queryAssistant);
    	return localPageController().nextPage();
    }


    // ----------------------------------------------------------
    public WOComponent useSavedQuery()
    {
    	localPageController().selectNextSavedQuery(savedQuery);
    	return localPageController().nextPage();
    }
}