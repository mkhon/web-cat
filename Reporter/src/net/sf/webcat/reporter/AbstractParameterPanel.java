package net.sf.webcat.reporter;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import net.sf.webcat.core.WCComponent;

public abstract class AbstractParameterPanel extends WCComponent
{
	public EnterReportParametersPage owner;
	
	public ReportParameter parameter;
	
	/**
	 * A special token returned by the evaluateWithErrorHandling method
	 * to indicate that an exception was thrown when evaluating the OGNL
	 * expression associated with the parameter. We use this instead of null
	 * because null could, in some case, be a valid error-free result to the
	 * expression evaluation.
	 */
	protected static final Object EVALUATION_ERROR = new Object();

	public AbstractParameterPanel(WOContext context)
	{
		super(context);
	}
	
    public void appendToResponse(WOResponse response, WOContext context)
    {
    	owner.registerChildPanel(parameter, this);

    	super.appendToResponse(response, context);
    }

    public String busyIndicatorIdForParameter()
    {
    	return ParameterUIUtils.busyIndicatorIdForParameter(parameter);
    }

    protected Object evaluateWithErrorHandling()
    {
    	// TODO Similar handling for CHOOSE parameters.
    	clearAllMessages();

    	try
    	{
	    	return ParameterUIUtils.evaluateKeypathWithDependents(parameter,
	    			owner, wcSession().defaultEditingContext());
    	}
    	catch(NullPointerException e)
    	{
    		error("<p>A NullPointerException occurred while evaluating the OGNL " +
    				"expression associated with the parameter named <b>" +
    				parameter.binding() + "</b>. This error is most likely " +
    				"the result of referencing a property or variable that " +
    				"is not defined.</p>" +
    				"<p>The faulty expression is: <code>" +
    				parameter.options().valueForKey(
    						ReportParameter.OPTION_SOURCE) + "</code>");
    		
    		return EVALUATION_ERROR;
    	}
    	catch(Exception e)
    	{
    		error("<p>An exception was thrown evaluating the OGNL expression " +
    				"associated with the parameter named <b>" +
    				parameter.binding() + "</b>: " + e.toString() + "</p>" +
    				"<p>The faulty expression is: <code>" +
    				parameter.options().valueForKey(
    						ReportParameter.OPTION_SOURCE) + "</code>");
    		
    		return EVALUATION_ERROR;
    	}
    }

    public abstract String observeeId();
    
    public abstract Object currentSelection();
}
