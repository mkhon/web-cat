/*==========================================================================*\
 |  $Id: QueryPageController.java,v 1.5 2008/10/28 15:52:23 aallowat Exp $
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

import com.webobjects.appserver.WOComponent;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import net.sf.webcat.reporter.queryassistants.AbstractQueryAssistantModel;
import net.sf.webcat.reporter.queryassistants.IQueryAssistant;
import net.sf.webcat.reporter.queryassistants.QueryAssistantDescriptor;

//-------------------------------------------------------------------------
/**
 * Controls the sequencing of pages when specifying queries for reports.
 *
 * @author Tony Allevato
 * @version $Id: QueryPageController.java,v 1.5 2008/10/28 15:52:23 aallowat Exp $
 */
public class QueryPageController
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new controller.
     * @param component The starting page
     * @param dataSets  The datasets involved in the report
     */
    public QueryPageController(ReporterComponent component,
            NSArray<ReportDataSet> dataSets)
    {
        this.currentComponent = component;
        this.dataSets = dataSets;

        currentIndex = -1;
        state = STATE_START;

        selectedQueryAssistants =
            new QueryAssistantDescriptor[dataSets.count()];
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public WOComponent nextPage()
    {
        ReporterComponent page = null;

        boolean isSavedQuery;

        if (currentIndex >= 0 && currentIndex < selectedQueryAssistants.length)
        {
            isSavedQuery = (selectedQueryAssistants[currentIndex] instanceof
                SavedQueryAssistantDescriptor);
        }
        else
        {
            isSavedQuery = false;
        }

        if (state == STATE_CHOOSE_ASSISTANT && !isSavedQuery)
        {
            // Roll back the query for this data set (if there's one already
            // there, meaning we've gone back and forward again), then convert
            // it into a query assistant model and set it on the query
            // assistant page.

            state = STATE_CONSTRUCT_QUERY;
            page = prepareCurrentQueryAssistantHolder();
        }
        else if ((state == STATE_CHOOSE_ASSISTANT && isSavedQuery)
                 || state == STATE_START
                 || state == STATE_CONSTRUCT_QUERY)
        {
            if (isSavedQuery)
            {
                commitSavedQuery();
            }
            else if (state == STATE_CONSTRUCT_QUERY)
            {
                // Before we move away from the query assistant page, commit
                // the user's query into the page's transient state.

                commitCurrentQuery();
            }

            currentIndex++;

            if (currentIndex == dataSets.count())
            {
                state = STATE_SUMMARY;

                page = (ReporterComponent)currentComponent.pageWithName(
                    GenerationSummaryPage.class.getName());
            }
            else
            {
                state = STATE_CHOOSE_ASSISTANT;

                selectedQueryAssistants[currentIndex] = null;

                page = (ReporterComponent)currentComponent.pageWithName(
                    PickQueryAssistantPage.class.getName());

                page.takeValueForKey(currentDataSet(), "dataSet");
            }
        }

        currentComponent = page;
        return page;
    }


    // ----------------------------------------------------------
    public WOComponent previousPage()
    {
        ReporterComponent page = null;

        boolean isSavedQuery;

        if (currentIndex >= 1
            && currentIndex < selectedQueryAssistants.length + 1)
        {
            isSavedQuery = (selectedQueryAssistants[currentIndex - 1]
                instanceof SavedQueryAssistantDescriptor);
        }
        else
        {
            isSavedQuery = false;
        }

        if (!isSavedQuery
            && (state == STATE_CHOOSE_ASSISTANT || state == STATE_SUMMARY))
        {
            currentIndex--;

            if (currentIndex == -1)
            {
                // Return to the starting page where the user selects which
                // template to generate.

                state = STATE_START;
                page = (ReporterComponent)currentComponent.pageWithName(
                    PickTemplateToGeneratePage.class.getName());
            }
            else
            {
                // Roll back the query for this data set (if there's one already
                // there, meaning we've gone back and forward again), then convert
                // it into a query assistant model and set it on the query
                // assistant page.

                state = STATE_CONSTRUCT_QUERY;
                page = prepareCurrentQueryAssistantHolder();
            }
        }
        else if (isSavedQuery || state == STATE_CONSTRUCT_QUERY)
        {
            // Before we move away from the query assistant page, commit
            // the user's query into the page's transient state.

            state = STATE_CHOOSE_ASSISTANT;

            if (isSavedQuery)
            {
                currentIndex--;
                commitSavedQuery();
            }
            else
            {
                commitCurrentQuery();
            }

            selectedQueryAssistants[currentIndex] = null;

            page = (ReporterComponent)currentComponent.pageWithName(
                PickQueryAssistantPage.class.getName());

            page.takeValueForKey(currentDataSet(), "dataSet");
        }

        currentComponent = page;
        return page;
    }


    // ----------------------------------------------------------
    public void selectNextQueryAssistant(QueryAssistantDescriptor qad)
    {
        selectedQueryAssistants[currentIndex] = qad;
    }


    // ----------------------------------------------------------
    public void selectNextSavedQuery(ReportQuery query)
    {
        selectedQueryAssistants[currentIndex] =
            new SavedQueryAssistantDescriptor(query);
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    private ReportDataSet currentDataSet()
    {
        return dataSets.objectAtIndex(currentIndex);
    }


    // ----------------------------------------------------------
    private QueryAssistantDescriptor currentQueryAssistant()
    {
        return selectedQueryAssistants[currentIndex];
    }


    // ----------------------------------------------------------
    private ReporterComponent prepareCurrentQueryAssistantHolder()
    {
        ReportQuery query = currentComponent.rollbackQueryForDataSet(
                currentDataSet());

        EOQualifier qualifier = null;

        if (query != null)
        {
            qualifier = QualifierSerialization.convertGIDsToEOs(
                query.qualifier(),
                currentComponent.localContext());
        }

        ReporterComponent page =
            (ReporterComponent)currentComponent.pageWithName(
                QueryAssistantHolderPage.class.getName());

        QueryAssistantDescriptor qad =
            selectedQueryAssistants[currentIndex];
        AbstractQueryAssistantModel model = qad.createModel();
        model.takeValuesFromQualifier(qualifier);

        page.takeValueForKey(qad.editorComponentName(),
                "queryAssistantComponentName");
        page.takeValueForKey(model, "model");
        page.takeValueForKey(currentDataSet(), "dataSet");

        return page;
    }


    // ----------------------------------------------------------
    private void commitCurrentQuery()
    {
        AbstractQueryAssistantModel model =
            (AbstractQueryAssistantModel)currentComponent.valueForKey(
                "model");
        String description = (String)currentComponent.valueForKey(
                "queryDescription");

        if (description != null && description.trim().length() == 0)
        {
            description = null;
        }

        String queryAsstId = currentQueryAssistant().id();
        EOQualifier qualifier = model.qualifierFromValues();
        EOQualifier gidQualifier =
            QualifierSerialization.convertEOsToGIDs(
                qualifier, currentComponent.localContext());

        currentComponent.commitNewQueryForDataSet(currentDataSet(),
            description, queryAsstId, gidQualifier);
    }


    // ----------------------------------------------------------
    private void commitSavedQuery()
    {
        SavedQueryAssistantDescriptor sqad = (SavedQueryAssistantDescriptor)
            selectedQueryAssistants[currentIndex];

        currentComponent.commitExistingQueryForDataSet(currentDataSet(),
            sqad.query());
    }


    // ----------------------------------------------------------
    public QueryAssistantDescriptor queryAssistantAtIndex(int index)
    {
        return selectedQueryAssistants[index];
    }


    // ----------------------------------------------------------
    private static class SavedQueryAssistantDescriptor
        extends QueryAssistantDescriptor
    {
        //~ Constructor .......................................................

        // ----------------------------------------------------------
        public SavedQueryAssistantDescriptor(ReportQuery query)
        {
            super(null, null, null, null, null, null);

            this.query = query;
        }


        //~ Public Methods ....................................................

        // ----------------------------------------------------------
        public ReportQuery query()
        {
            return query;
        }


        //~ Instance/static variables .........................................

        private ReportQuery query;
    }


    //~ Instance/static variables .............................................

    private static final int STATE_START = 0;
    private static final int STATE_CHOOSE_ASSISTANT = 1;
    private static final int STATE_CONSTRUCT_QUERY = 2;
    private static final int STATE_SUMMARY = 3;

    private ReporterComponent currentComponent;
    private NSArray<ReportDataSet> dataSets;
    private QueryAssistantDescriptor[] selectedQueryAssistants;
    private int currentIndex;
    private int state;
}
