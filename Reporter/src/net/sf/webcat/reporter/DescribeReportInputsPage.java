package net.sf.webcat.reporter;

import net.sf.webcat.reporter.queryassistants.AbstractQueryAssistantModel;
import net.sf.webcat.reporter.queryassistants.ModelOrQueryWrapper;
import net.sf.webcat.reporter.queryassistants.QueryAssistantDescriptor;
import net.sf.webcat.reporter.queryassistants.QueryAssistantManager;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;

/**
 * 
 * @author Tony Allevato
 * @version $Id: DescribeReportInputsPage.java,v 1.1 2009/05/27 14:31:52 aallowat Exp $
 */
public class DescribeReportInputsPage extends ReporterComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public DescribeReportInputsPage(WOContext context)
    {
        super(context);
    }
    
    
    //~ KVC attributes (must be public) .......................................

    public NSArray<String> stylesheetsToImport;
    public String stylesheetToImport;
    
    public NSArray<String> javascriptsToImport;
    public String javascriptToImport;

    public NSArray<ReportDataSet> dataSets;
    public ReportDataSet dataSet;
    public int dataSetIndex;

    public QueryAssistantDescriptor queryAssistant;
    public ReportQuery savedQuery;

    public NSDictionary<String, Object> parameter;

    public NSArray<IRenderingMethod> renderingMethods;
    public IRenderingMethod renderingMethod;
    public IRenderingMethod selectedRenderingMethod;
    
    public String reportDescription;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        dataSets = localReportTemplate().dataSets();

        if (dataSets != null)
        {
            modelWrappers = new ModelOrQueryWrapper[dataSets.count()];

            for (int i = 0; i < dataSets.count(); i++)
            {
                modelWrappers[i] = new ModelOrQueryWrapper(
                        dataSets.objectAtIndex(i));
            }
        }
        
        renderingMethods = Reporter.getInstance().allRenderingMethods();

        for (IRenderingMethod method : renderingMethods)
        {
            if (method.methodName().equals(
                    localReportTemplate().preferredRenderer()))
            {
                selectedRenderingMethod = method;
                break;
            }
        }

        if (selectedRenderingMethod == null)
        {
            // Default to HTML if one isn't specified.

            selectedRenderingMethod =
                Reporter.getInstance().renderingMethodWithName("html");
        }

        NSArray<QueryAssistantDescriptor> allAssistants =
            QueryAssistantManager.getInstance().allAssistants();
        
        NSMutableSet<String> stylesheets = new NSMutableSet<String>();
        NSMutableSet<String> javascripts = new NSMutableSet<String>();
        
        for (QueryAssistantDescriptor qad : allAssistants)
        {
            stylesheets.addObjectsFromArray(qad.stylesheets());
            javascripts.addObjectsFromArray(qad.javascripts());
        }

        stylesheetsToImport = stylesheets.allObjects();
        javascriptsToImport = javascripts.allObjects();

        parameterValues = new NSMutableDictionary<String, Object>();

        super.appendToResponse(response, context);
    }
    
    
    // ----------------------------------------------------------
    public NSArray<QueryAssistantDescriptor> queryAssistantsForDataSet()
    {
        String entityName = dataSet.wcEntityName();

        return QueryAssistantManager.getInstance().assistantsForEntity(
                entityName);
    }
    
    
    // ----------------------------------------------------------
    public ModelOrQueryWrapper selectedModelOrQueryForDataSet()
    {
        return modelWrappers[dataSetIndex];
    }
    

    // ----------------------------------------------------------
    public NSArray<ReportQuery> savedQueriesApplicableToDataSet()
    {
        return ReportQuery.objectsForUserAndEntitySavedQueries(localContext(),
                user(), dataSet.wcEntityName());
    }
    
    
    // ----------------------------------------------------------
    public ReportQuery selectedSavedQueryForDataSet()
    {
        return modelWrappers[dataSetIndex].savedQuery();
    }
    
    
    // ----------------------------------------------------------
    public void setSelectedSavedQueryForDataSet(ReportQuery query)
    {
        modelWrappers[dataSetIndex].switchToSavedQuery(query);
    }
    
    
    // ----------------------------------------------------------
    public WOActionResults useSavedQuery()
    {
        return null;
    }

    
    // ----------------------------------------------------------
    public String titleForDataSetPageModule()
    {
        return "Data set: " + dataSet.name() + " (" +
            (dataSetIndex + 1) + " of " + dataSets.count() + ")";
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
    public String idForQueryAssistantPickerForDataSet()
    {
        return "queryAssistantPicker_" + dataSetIndex;
    }
    
    
    // ----------------------------------------------------------
    public String idForQueryAssistantContainer()
    {
        return "queryAssistantContainer_" + dataSetIndex;
    }
    

    // ----------------------------------------------------------
    public String idForSavedQueryPickerForDataSet()
    {
        return "savedQueryPicker_" + dataSetIndex;
    }
    
    
    // ----------------------------------------------------------
    public WOActionResults chooseQueryAssistantForDataSet()
    {
        modelWrappers[dataSetIndex].switchToQueryAssistant(queryAssistant);
        return null;
    }

    
    // ----------------------------------------------------------
    public WOActionResults revertEditingForDataSet()
    {
        modelWrappers[dataSetIndex].switchToAssistantPicker();
        return null;
    }


    // ----------------------------------------------------------
    public Object valueForParameter()
    {
        return parameterValues.objectForKey(parameter.objectForKey("name"));
    }


    // ----------------------------------------------------------
    public void setValueForParameter(Object value)
    {
        parameterValues.setObjectForKey(value, parameter.objectForKey("name"));
    }

    
    // ----------------------------------------------------------
    public WOActionResults changeReportTemplate()
    {
        clearLocalReportState();

        return pageWithName(PickTemplateToGeneratePage.class.getName());
    }
    
    
    // ----------------------------------------------------------
    public WOActionResults generateReport()
    {
        String desc = reportDescription;
        if (desc == null)
        {
            desc = defaultDescription();
        }

        setLocalReportDescription(desc);
        setLocalRenderingMethod(selectedRenderingMethod.methodName());

        commitReportGeneration(modelWrappers);
        return pageWithName(GeneratedReportPage.class.getName());
    }
    

    // ----------------------------------------------------------
    public String defaultDescription()
    {
        return localReportTemplate().name();
    }
    

    //~ Static/instance variables .............................................

    private ModelOrQueryWrapper[] modelWrappers;
    private NSMutableDictionary<String, Object> parameterValues;
}
