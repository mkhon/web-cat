package net.sf.webcat.reporter.queryassistants;

import net.sf.webcat.core.User;
import net.sf.webcat.reporter.ReportDataSet;
import net.sf.webcat.reporter.ReportQuery;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

//-------------------------------------------------------------------------
/**
 * TODO real description
 *  
 * @author Tony Allevato
 * @version $Id: ModelOrQueryWrapper.java,v 1.1 2009/05/27 14:31:52 aallowat Exp $
 */
public class ModelOrQueryWrapper implements NSKeyValueCoding
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public ModelOrQueryWrapper(ReportDataSet dataSet)
    {
        this.dataSet = dataSet;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public ReportDataSet dataSet()
    {
        return dataSet;
    }
    

    // ----------------------------------------------------------
    public void switchToAssistantPicker()
    {
        isEditing = false;
        queryAssistant = null;
    }


    // ----------------------------------------------------------
    public void switchToQueryAssistant(QueryAssistantDescriptor qad)
    {
        isEditing = true;
        
        EOQualifier q = null;

        if (savedQuery != null)
        {
            q = savedQuery.qualifier();
            savedQuery = null;
        }
        else if (model != null)
        {
            q = model.qualifierFromValues();
        }
        
        queryAssistant = qad;
        model = qad.createModel();

        if (q != null)
        {
            model.takeValuesFromQualifier(q);
        }
    }


    // ----------------------------------------------------------
    public void switchToSavedQuery(ReportQuery query)
    {
        isEditing = true;

        savedQuery = query;

        queryAssistant = null;
        model = null;
    }
    
    
    // ----------------------------------------------------------
    public boolean isEditing()
    {
        return isEditing;
    }


    // ----------------------------------------------------------
    public QueryAssistantDescriptor queryAssistant()
    {
        return queryAssistant;
    }
    
    
    // ----------------------------------------------------------
    public AbstractQueryAssistantModel model()
    {
        return model;
    }


    // ----------------------------------------------------------
    public String descriptionToSave()
    {
        return descriptionToSave;
    }
    
    
    // ----------------------------------------------------------
    public void setDescriptionToSave(String description)
    {
        descriptionToSave = description;
    }
    
    
    // ----------------------------------------------------------
    public ReportQuery savedQuery()
    {
        return savedQuery;
    }
    
    
    // ----------------------------------------------------------
    public ReportQuery commitAndGetQuery(EOEditingContext ec, User user)
    {
        if (savedQuery != null)
        {
            return savedQuery;
        }
        else if (model != null)
        {
            ReportQuery query = new ReportQuery();
            ec.insertObject(query);

            query.setQueryAssistantId(queryAssistant.id());
            query.setDescription(descriptionToSave);
            query.setUserRelationship(user.localInstance(ec));
            query.setQualifier(model.qualifierFromValues());
            query.setWcEntityName(dataSet.wcEntityName());

            return query;
        }
        else
        {
            return null;
        }
    }


    // ----------------------------------------------------------
    public String displayableSummary()
    {
        if (savedQuery != null)
        {
            return savedQuery.description();
        }
        else if (queryAssistant != null)
        {
            return queryAssistant.description();
        }
        else
        {
            return null;
        }
    }
    

    // ----------------------------------------------------------
    public void takeValueForKey(Object value, String key)
    {
        NSKeyValueCoding.DefaultImplementation.takeValueForKey(
                this, value, key);
    }


    // ----------------------------------------------------------
    public Object valueForKey(String key)
    {
        return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
    }
    

    //~ Static/instance variables .............................................

    private boolean isEditing;
    private ReportDataSet dataSet;
    private QueryAssistantDescriptor queryAssistant;
    private AbstractQueryAssistantModel model;
    private String descriptionToSave;
    private ReportQuery savedQuery;
}
