package net.sf.webcat.reporter.controls;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver._private.WOInput;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

import er.ajax.AjaxComponent;
import er.ajax.AjaxDynamicElement;
import er.ajax.AjaxUtils;

public class TreePanel extends AjaxComponent
{
	public static final String ACTION_REQUEST_PARAMETER =
		"net.sf.webcat.reporter.controls.TreePanel.action";
	
//	private NSDictionary associations;

//	public TreePanel(String name, NSDictionary associations, WOElement template)
	public TreePanel(WOContext context)
	{
//		super(name, associations, template);

//		this.associations = associations;
		super(context);
	}

    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }

	private void appendTagAttributeToResponse(WOResponse response,
			String tag, Object value)
	{
		if(value != null)
			response._appendTagAttributeAndValue(tag, value.toString(), true);
	}

	public void appendToResponse(WOResponse response, WOContext context)
	{
		String id = safeElementID();

		String preRender = (String)valueForBinding("beforeRender", null);
		String postRender = (String)valueForBinding("afterRender", null);

		// Create <div> container for the tree.
		response.appendContentString("<div");
	    appendTagAttributeToResponse(response, "id", id);
	    appendTagAttributeToResponse(response, "class", valueForBinding("class", null));
	    appendTagAttributeToResponse(response, "style", valueForBinding("style", null));
	    response.appendContentString("></div>");
	    
	    // Create the script that initializes the tree.
	    String actionUrl = AjaxUtils.ajaxComponentActionUrl(context);

	    AjaxUtils.appendScriptHeader(response);
	    response.appendContentString("Ext.onReady(function() {\n");
		response.appendContentString("  var tree = new Ext.tree.TreePanel($('" + id + "'), {\n");
		response.appendContentString("rootVisible: false,");
		response.appendContentString("animate: true,");
		response.appendContentString("loader: new Ext.tree.TreeLoader({ baseParams: { '");
		response.appendContentString(ACTION_REQUEST_PARAMETER + "': 'loadData', ");
		response.appendContentString(" },");
		response.appendContentString("dataUrl: '" + actionUrl + "' }),");
		response.appendContentString("containerScroll: true });\n");
		response.appendContentString("  var root = new Ext.tree.AsyncTreeNode({ draggable: false });\n");
		response.appendContentString("  tree.setRootNode(root);\n");

		response.appendContentString("  WebCAT.registerControl('" + id + "', tree);\n");

		String updateOnSelectionChange = (String)valueForBinding("updateOnSelectionChange", null);
		if(updateOnSelectionChange != null)
		{
			response.appendContentString("  tree.getSelectionModel().on('selectionchange', function(node) {");
			
			String body = AjaxMultipleUpdater.generateUpdaterBody(
					updateOnSelectionChange, "$('" + id + "').up('form')",
					"'" + ACTION_REQUEST_PARAMETER + "=updateSelection&" +
					id + "=' + this.getSelectedNode().id");
			response.appendContentString(body);

			response.appendContentString("});");
		}

		if(preRender != null)
			response.appendContentString(preRender);
			
		response.appendContentString("  tree.render();\n");
		response.appendContentString("  root.expand();\n");
		
		if(postRender != null)
			response.appendContentString(postRender);

		response.appendContentString("});");

	    AjaxUtils.appendScriptFooter(response);

		super.appendToResponse(response, context);
	}

	@Override
	public void takeValuesFromRequest(WORequest request, WOContext context)
	{
		TreeContentProvider contentProvider =
			(TreeContentProvider)valueForBinding("contentProvider");

		BrokerRegistry brokers = (BrokerRegistry)valueForBinding("brokers");
	
		String id = safeElementID();
		if(brokers.brokerForComponent(id) == null)
		{
			TreeBroker broker = new TreeBroker(contentProvider,
					(TreeDisplayProvider)valueForBinding("displayProvider"),
					id);
			brokers.setBrokerForComponent(broker, id);
		}

		String action = request.stringFormValueForKey(ACTION_REQUEST_PARAMETER);
		if("updateSelection".equals(action))
		{
			String encodedSelection = request.stringFormValueForKey(id);

			Object element = contentProvider.elementWithId(encodedSelection);
			//NSArray.componentsSeparatedByString(encodedSelection, ",");
			setValueForBinding(element, "selection");
		}
	}

	@Override
	protected void addRequiredWebResources(WOResponse res)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public WOActionResults handleRequest(WORequest request, WOContext context)
	{
		BrokerRegistry brokers = (BrokerRegistry)valueForBinding("brokers");
		Broker broker = brokers.brokerForComponent(safeElementID());

		if(broker != null)
		{
			WOResponse response = broker.handleRequest(request, context);
			
			if(response != null)
			{
				return response;
			}
		}

		return null;
	}
}
