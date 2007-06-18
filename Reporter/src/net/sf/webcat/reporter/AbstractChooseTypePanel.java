package net.sf.webcat.reporter;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

import net.sf.webcat.core.WCComponent;
import net.sf.webcat.reporter.internal.datamodel.ReferenceEnvironment;

public abstract class AbstractChooseTypePanel extends AbstractParameterPanel
{
	public int index;
	public String observeeValue;

	public AbstractChooseTypePanel(WOContext context)
	{
		super(context);
	}
	
    public String observeeId()
    {
    	return ParameterUIUtils.observeeIdForParameter(parameter);
    }

	public String indexedFormControlId()
    {
    	return ParameterUIUtils.indexedControlIdForParameter(parameter, index);
    }
    
    public String allSelectionFormControlId()
    {
    	return ParameterUIUtils.allSelectionControlIdForParameter(parameter);
    }

    public String checkStateChangedMethodInvocation()
    {
    	return ParameterUIUtils.checkStateChangedInvocationForParameter(
    			parameter);
    }

    public String allCheckStateChangedMethodInvocation()
    {
    	return
    		ParameterUIUtils.allCheckStateChangedInvocationForParameter(
    			parameter);
    }
    
    public NSArray getChoicesBasedOnDependents()
    {
    	return ParameterUIUtils.evaluateArrayKeypathWithDependents(parameter,
    			owner, wcSession().localContext());
    }
}
