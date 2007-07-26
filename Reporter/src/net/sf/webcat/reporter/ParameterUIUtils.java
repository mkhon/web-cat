package net.sf.webcat.reporter;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

import net.sf.webcat.core.CourseOffering;
import net.sf.webcat.grader.Assignment;
import net.sf.webcat.grader.AssignmentOffering;
import net.sf.webcat.reporter.internal.datamodel.ReferenceEnvironment;

public class ParameterUIUtils
{
	private static NSMutableDictionary simpleTypeMap;
	private static NSMutableDictionary chooseTypeMap;
	
	static
	{
		simpleTypeMap = new NSMutableDictionary();
		simpleTypeMap.setObjectForKey(BooleanParameterPanel.class,
				ReportParameter.TYPE_BOOLEAN);
		
		chooseTypeMap = new NSMutableDictionary();
		chooseTypeMap.setObjectForKey(
				CourseOfferingParameterPanel.class, CourseOffering.class);
		chooseTypeMap.setObjectForKey(
				AssignmentParameterPanel.class, Assignment.class);
		chooseTypeMap.setObjectForKey(
				AssignmentOfferingParameterPanel.class, AssignmentOffering.class);
	}

	public static String updateContainerIdForParameter(ReportParameter param)
	{
		return param.binding() + "_updateContainer";
	}

	public static String observeeIdForParameter(ReportParameter param)
	{
		return param.binding() + "_observee";
	}

	public static String allSelectionControlIdForParameter(ReportParameter param)
	{
		return param.binding() + "_selectAll";
	}

	public static String indexedControlIdForParameter(ReportParameter param, int index)
	{
		return param.binding() + "_" + index;
	}
	
    public static String checkStateChangedInvocationForParameter(
    		ReportParameter parameter)
    {
    	return "checkStateChanged('" + parameter.binding() + "', '" +
    		observeeIdForParameter(parameter) + "');";
    }

    public static String allCheckStateChangedInvocationForParameter(
    		ReportParameter parameter)
    {
    	return "allCheckStateChanged('" + parameter.binding() + "', '" +
    		observeeIdForParameter(parameter) + "');";
    }
    
    public static String busyIndicatorIdForParameter(ReportParameter parameter)
    {
    	return parameter.binding() + "_busyIndicator";
    }
   
    public static String busyIndicatorStartForParameter(
    		ReportParameter parameter)
    {
    	return "function(x) { $('" + busyIndicatorIdForParameter(parameter) +
    		"').style.visibility = 'visible'; }";
    }

    public static String busyIndicatorCompleteForParameter(
    		ReportParameter parameter)
    {
    	return "function(x) { $('" + busyIndicatorIdForParameter(parameter) +
		"').style.visibility = 'hidden'; }";
    }

    public static NSArray evaluateArrayKeypathWithDependents(
    		ReportParameter parameter, EnterReportParametersPage owner,
    		EOEditingContext ec)
    {
    	NSDictionary depSelection =
    		owner.currentSelectionsForDependents(parameter);

    	ReferenceEnvironment env = new ReferenceEnvironment(depSelection,
    			null);
    	env.forceEditingContext(ec);

    	NSMutableDictionary options = parameter.optionsToEvaluate();
    	options.setObjectForKey("self", "binding");

    	return (NSArray)env.evaluate(options);    	
    }

    public static Object evaluateKeypathWithDependents(
    		ReportParameter parameter, EnterReportParametersPage owner,
    		EOEditingContext ec)
    {
    	NSDictionary depSelection =
    		owner.currentSelectionsForDependents(parameter);

    	ReferenceEnvironment env = new ReferenceEnvironment(depSelection,
    			null);
    	env.forceEditingContext(ec);

    	NSMutableDictionary options = parameter.optionsToEvaluate();

    	return env.evaluate(options);    	
    }

    public static String componentNameForParameter(ReportParameter parameter,
    		EnterReportParametersPage owner, EOEditingContext ec)
    {
    	// Some slightly tricky logic here:
    	// Check the type of the parameter. If it is not "CHOOSE", then return
    	// the proper component type based on the parameter type.
    	// If the parameter is "CHOOSE", then we need to evaluate its keypath
    	// (and qualifier, if there is one). This will return an NSArray. If
    	// the NSArray has elements, they should be homogeneous, so use the
    	// type of the first element to determine the component type. On the
    	// other hand, if the array is empty, we really have no way of knowing
    	// what the type of the elements -should- be, so we return the
    	// component type of a generic "No items available" message component.

    	String type = parameter.type();
    	if(simpleTypeMap.containsKey(type))
    	{
    		return ((Class)simpleTypeMap.objectForKey(type)).getCanonicalName();
    	}
    	else if(ReportParameter.TYPE_CHOOSE.equals(type))
    	{
	    	NSArray result = evaluateArrayKeypathWithDependents(parameter,
	    			owner, ec);
	    	
	    	if(result.count() > 0)
	    	{
	    		Class elementClass = result.objectAtIndex(0).getClass();
	    		return ((Class)chooseTypeMap.objectForKey(
	    				elementClass)).getCanonicalName();
	    	}
    	}

		return NoElementsParameterPanel.class.getCanonicalName();
    }
}
