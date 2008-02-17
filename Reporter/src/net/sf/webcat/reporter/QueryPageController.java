package net.sf.webcat.reporter;

import net.sf.webcat.reporter.queryassistants.AbstractQueryAssistantModel;
import net.sf.webcat.reporter.queryassistants.IQueryAssistant;
import net.sf.webcat.reporter.queryassistants.QueryAssistantDescriptor;

import com.webobjects.appserver.WOComponent;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

public class QueryPageController
{
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
	
	private ReportDataSet currentDataSet()
	{
		return dataSets.objectAtIndex(currentIndex);
	}
	
	private ReporterComponent prepareCurrentQueryAssistantHolder()
	{
    	ReportQuery query = currentComponent.rollbackQueryForDataSet(
    			currentDataSet());

    	EOQualifier qualifier = null;

    	if(query != null)
    		qualifier = QualifierUtils.qualifierWithEOsForGIDs(
    				query.qualifier(),
    				currentComponent.wcSession().localContext());

		ReporterComponent page =
			(ReporterComponent)currentComponent.pageWithName(
				QueryAssistantHolderPage.class.getName());

		QueryAssistantDescriptor qad =
			selectedQueryAssistants[currentIndex];
		AbstractQueryAssistantModel model = qad.createModel();
		model.takeValuesFromQualifier(qualifier);

		page.takeValueForKey(qad.componentName(),
				"queryAssistantComponentName");
		page.takeValueForKey(model, "model");
		page.takeValueForKey(currentDataSet(), "dataSet");
		
		return page;
	}
	
	private void commitCurrentQuery()
	{
		AbstractQueryAssistantModel model =
			(AbstractQueryAssistantModel)currentComponent.valueForKey(
					"model");
		String description = (String)currentComponent.valueForKey(
				"queryDescription");
		
		if(description != null && description.trim().length() == 0)
			description = null;

    	EOQualifier qualifier = model.qualifierFromValues();
    	EOQualifier gidQualifier =
    		QualifierUtils.qualifierWithGIDsForEOs(
    			qualifier, currentComponent.wcSession().localContext());

    	currentComponent.commitNewQueryForDataSet(currentDataSet(),
   				description, gidQualifier);
    }

	private void commitSavedQuery()
	{
		SavedQueryAssistantDescriptor sqad = (SavedQueryAssistantDescriptor)
			selectedQueryAssistants[currentIndex];

		currentComponent.commitExistingQueryForDataSet(currentDataSet(),
				sqad.query());
	}

	public WOComponent nextPage()
	{
		ReporterComponent page = null;

		boolean isSavedQuery;
		
		if(currentIndex >= 0 && currentIndex < selectedQueryAssistants.length)
			isSavedQuery = (selectedQueryAssistants[currentIndex] instanceof
					SavedQueryAssistantDescriptor);
		else
			isSavedQuery = false;

		if(state == STATE_CHOOSE_ASSISTANT && !isSavedQuery)
		{
			// Roll back the query for this data set (if there's one already
			// there, meaning we've gone back and forward again), then convert
			// it into a query assistant model and set it on the query
			// assistant page.

			state = STATE_CONSTRUCT_QUERY;
			page = prepareCurrentQueryAssistantHolder();
		}
		else if((state == STATE_CHOOSE_ASSISTANT && isSavedQuery) ||
				state == STATE_START || state == STATE_CONSTRUCT_QUERY)
		{
			if(isSavedQuery)
			{
				commitSavedQuery();
			}
			else if(state == STATE_CONSTRUCT_QUERY)
			{
				// Before we move away from the query assistant page, commit
				// the user's query into the session.

				commitCurrentQuery();
			}

			currentIndex++;

			if(currentIndex == dataSets.count())
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
	
	public WOComponent previousPage()
	{
		ReporterComponent page = null;

		boolean isSavedQuery;
		
		if(currentIndex >= 1 && currentIndex < selectedQueryAssistants.length + 1)
			isSavedQuery = (selectedQueryAssistants[currentIndex - 1] instanceof
					SavedQueryAssistantDescriptor);
		else
			isSavedQuery = false;

		if(!isSavedQuery && (state == STATE_CHOOSE_ASSISTANT || state == STATE_SUMMARY))
		{
			currentIndex--;
			
			if(currentIndex == -1)
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
		else if(isSavedQuery || state == STATE_CONSTRUCT_QUERY)
		{
			// Before we move away from the query assistant page, commit
			// the user's query into the session.

			state = STATE_CHOOSE_ASSISTANT;

			if(isSavedQuery)
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

	public void selectNextQueryAssistant(QueryAssistantDescriptor qad)
	{
		selectedQueryAssistants[currentIndex] = qad;
	}

	public void selectNextSavedQuery(ReportQuery query)
	{
		selectedQueryAssistants[currentIndex] =
			new SavedQueryAssistantDescriptor(query);
	}

	private static class SavedQueryAssistantDescriptor
		extends QueryAssistantDescriptor
	{

		public SavedQueryAssistantDescriptor(ReportQuery query)
		{
			super(null, null, null);
			
			this.query = query;
		}

		public ReportQuery query()
		{
			return query;
		}

		private ReportQuery query;
	}

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
