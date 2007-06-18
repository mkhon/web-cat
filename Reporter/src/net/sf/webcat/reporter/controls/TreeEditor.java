package net.sf.webcat.reporter.controls;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODynamicElement;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSDictionary;

import er.ajax.AjaxComponent;
import er.ajax.AjaxUtils;

public class TreeEditor extends AjaxComponent
{
	public static final String ACTION_REQUEST_PARAMETER =
		"net.sf.webcat.reporter.controls.TreeEditor.action";

	public TreeEditor(WOContext context)
	{
		super(context);
	}

    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }
   
	private boolean booleanValueForBinding(String binding, boolean defaultValue)
	{
		Object result = valueForBinding(binding);
		if(result == null)
			return defaultValue;
		else
			return ((Boolean)result).booleanValue();
	}

	public void appendToResponse(WOResponse response, WOContext context)
	{
		String id = safeElementID();
		String treeId = (String)valueForBinding("treeId");
		String blankText = (String)valueForBinding("blankText");
		boolean allowBlank = booleanValueForBinding("allowBlank", false);
		boolean selectOnFocus = booleanValueForBinding("selectOnFocus", true);
		
		response.appendContentString("<script type='text/javascript'>");
	    response.appendContentString("Ext.onReady(function() {\n");
		response.appendContentString("var editor = new Ext.tree.TreeEditor(WebCAT.getRegisteredControl('"
				+ treeId + "'), {");
		response.appendContentString("allowBlank: " + Boolean.toString(allowBlank) + ", ");
		
		if(blankText != null)
			response.appendContentString("blankText: '" + blankText + "', ");

		response.appendContentString("selectOnFocus: " + Boolean.toString(selectOnFocus));
		response.appendContentString("});");

		response.appendContentString("WebCAT.registerControl('" + id + "', editor);");

		String actionUrl = AjaxUtils.ajaxComponentActionUrl(context());
		response.appendContentString("editor.on('complete', function(ed, value) {");
		response.appendContentString("new Ajax.Request('" + actionUrl + "', { parameters: { '" +
				ACTION_REQUEST_PARAMETER + "': 'editComplete', newValue: value }, onComplete: function() { ");

		String updateOnEditComplete = (String)valueForBinding("updateOnEditComplete", context.component());
		if(updateOnEditComplete != null)
		{
			String body = AjaxMultipleUpdater.generateUpdaterBody(
					updateOnEditComplete, "$('" + id + "').up('form')", null);
			response.appendContentString(body);
		}

		response.appendContentString("} });\n");
		response.appendContentString("});");

		response.appendContentString("});");
		response.appendContentString("</script>");
		
		super.appendToResponse(response, context);
	}

	@Override
	public void takeValuesFromRequest(WORequest request, WOContext context)
	{
		String action = request.stringFormValueForKey(ACTION_REQUEST_PARAMETER);
		if("editComplete".equals(action))
		{
			String newValue = request.stringFormValueForKey("newValue");
			setValueForBinding(newValue, "value");
		}
	}

	@Override
	protected void addRequiredWebResources(WOResponse res) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WOActionResults handleRequest(WORequest request, WOContext context) {
		// TODO Auto-generated method stub
		return null;
	}
}
