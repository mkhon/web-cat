/*==========================================================================*\
 |  $Id: PickQueryAssistantPage.java,v 1.5 2008/04/04 21:00:53 aallowat Exp $
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
 * @version $Id: PickQueryAssistantPage.java,v 1.5 2008/04/04 21:00:53 aallowat Exp $
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
    public ReportQuery selectedSavedQuery;


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
    	localPageController().selectNextSavedQuery(selectedSavedQuery);
    	return localPageController().nextPage();
    }
}