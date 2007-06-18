package net.sf.webcat.reporter.controls;

import java.util.Enumeration;

import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODynamicElement;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;

import er.ajax.AjaxUtils;

/**
 * AjaxUpdateTrigger is useful if you have multiple containers on a page
 * that are controlled by a central parent component.  AjaxUpdateTrigger
 * allows you to pass in an array of containers that need to be updated.  An
 * example of this is if you have multiple editable areas on a page and only
 * one should be in edit mode at a time. If you put an AjaxUpdateTrigger 
 * inside the edit view, you can set the other components to not be in
 * edit mode and trigger all of the other update containers to update, 
 * reflecting their new non-editable status.
 * 
 * @binding updateContainerIDs an array of update container IDs to update
 * @binding resetAfterUpdate if true, the array of IDs will be cleared after appendToResponse
 * 
 * @author mschrag
 */
public class AjaxMultipleUpdater extends WODynamicElement {
	private NSDictionary _associations;
	private WOAssociation _updateContainerIDs;
	private WOAssociation _name;
	private WOAssociation _action;
	private WOAssociation _formReference;

	public AjaxMultipleUpdater(String name, NSDictionary associations, WOElement template) {
		super(name, associations, template);
		_associations = associations;
		_updateContainerIDs = (WOAssociation) associations.objectForKey("updateContainerIDs");
		_name = (WOAssociation) associations.objectForKey("name");
		_action = (WOAssociation) associations.objectForKey("action");
		_formReference = (WOAssociation) associations.objectForKey("formReference");
	}

	public void appendToResponse(WOResponse response, WOContext context)
	{
		super.appendToResponse(response, context);
		WOComponent component = context.component();
		String updateContainerIDString = (String) _updateContainerIDs.valueInComponent(component);

		if(updateContainerIDString != null)
		{
			String formReference = "this.up('form')";
			if(_formReference != null)
			{
				formReference = "document." +
					(String)_formReference.valueInComponent(component);
			}

			if(_name != null)
			{
				String name = (String)_name.valueInComponent(component);

				AjaxUtils.appendScriptHeader(response);
				response.appendContentString("function " + name + "() {\n");
			}
			else
			{
				response.appendContentString("function() {\n");
			}

			String body = generateUpdaterBody(updateContainerIDString, formReference, null);
			response.appendContentString(body);

			response.appendContentString("}");

			if(_name != null)
			{
				AjaxUtils.appendScriptFooter(response);
			}
		}
	}
	
	public static String generateUpdaterBody(String updateContainerIDString, String formReference,
			String parameterString)
	{
		String[] updateContainerIDs = updateContainerIDString.split(",");
		
		StringBuffer buffer = new StringBuffer();

//		buffer.append("new Ajax.Request(" + formReference + ".action, {\n");
		
		StringBuffer parametersBuffer = new StringBuffer();
		parametersBuffer.append("Form.serializeWithoutSubmits(" + formReference + ")");
	    
		if(parameterString != null)
		{
			parametersBuffer.append(" + '&' + ");
			parametersBuffer.append(parameterString);
		}

//	    buffer.append("  parameters: " + parametersBuffer.toString() + ",\n");

//		buffer.append("  onSuccess: function() {\n");
		
		for(int i = 0; i < updateContainerIDs.length; i++)
		{
			String updateContainerID = (String) updateContainerIDs[i];

			buffer.append("    if($('" + updateContainerID + "')) {\n");
			buffer.append("      new Ajax.Updater('" + updateContainerID + "', ");
			buffer.append("$('" + updateContainerID + "').getAttribute('updateUrl'), ");
			buffer.append("{ parameters: " + parametersBuffer.toString() + ", evalScripts: true, insertion: Element.update });\n");
			buffer.append("    }\n");
		}

//		buffer.append("  }\n");
//		buffer.append("});\n");
		
		return buffer.toString();
	}
}
