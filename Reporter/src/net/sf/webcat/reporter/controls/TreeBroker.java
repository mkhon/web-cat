package net.sf.webcat.reporter.controls;

import org.json.JSONArray;
import org.json.JSONObject;

import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;

/**
 * A broker object to be used with TreePanel components. The TreeBroker allows
 * the state and contents of the tree to be modified on the server and then to
 * have those changes reflected in the user interface on the client.
 * 
 * @author aallowat
 */
public class TreeBroker extends Broker
{
	private TreeContentProvider contentProvider;
	private TreeDisplayProvider displayProvider;
	private String treePanelId;

	public TreeBroker(TreeContentProvider contentProvider,
			TreeDisplayProvider displayProvider, String treePanelId)
	{
		this.contentProvider = contentProvider;
		this.displayProvider = displayProvider;
		this.treePanelId = treePanelId;
	}

	public void addElement(Object parent, Object child)
	{
		addElements(parent, new Object[] { child });
	}

	public void addElements(Object parent, Object[] children)
	{
		beginOperation();

		String parentId = safeIdForElement(parent);
		
		scriptGetRegisteredTree();
		scriptGetNodeById(parentId);

		for(int i = 0; i < children.length; i++)
		{
			Object child = children[i];
			
			transact("node.appendChild(new Ext.tree.AsyncTreeNode({");
			transact(" id: \"{0}\", text: \"{1}\", cls: \"{2}\", leaf: {3} }));",
					contentProvider.idForElement(child),
					displayProvider.textForElement(child),
					displayProvider.classForElement(child),
					!contentProvider.hasChildren(child));
		}

		endOperation();
	}

	public void removeElement(Object element)
	{
		beginOperation();
		
		String elementId = safeIdForElement(element);
		
		scriptGetRegisteredTree();
		scriptGetNodeById(elementId);
		transact("node.parentNode.removeChild(node);");
		
		endOperation();
	}

	public void selectElement(Object element)
	{
		beginOperation();
		
		String elementId = safeIdForElement(element);
		
		scriptGetRegisteredTree();
		scriptGetNodeById(elementId);
		
		transact("node.select();");
		
		endOperation();
	}

	private String safeIdForElement(Object element)
	{
		return (element == null) ? null : contentProvider.idForElement(element);
	}

	private void scriptGetRegisteredTree()
	{
		transact("var tree = WebCAT.getRegisteredControl(\"{0}\");",
				treePanelId);
	}
	
	private void scriptGetNodeById(String id)
	{
		if(id == null)
			transact("var node = tree.getRootNode();");
		else
			transact("var node = tree.getNodeById(\"{0}\");", id);
	}

	private String getJSONForChildren(String nodeId)
	{
		Object element = contentProvider.elementWithId(nodeId);
		Object[] children = contentProvider.childrenForElement(element);
		
		JSONArray array = new JSONArray();
		
		for(int i = 0; i < children.length; i++)
		{
			Object child = children[i];
			
			JSONObject obj = new JSONObject();
			obj.put("text", displayProvider.textForElement(child));
			obj.put("id", contentProvider.idForElement(child));
			obj.put("cls", displayProvider.classForElement(child));
			obj.put("leaf", !contentProvider.hasChildren(child));

			array.put(obj);
		}
		
		return array.toString();
	}
	
	public WOResponse handleRequest(WORequest request, WOContext context)
	{
		String action = request.stringFormValueForKey(
				TreePanel.ACTION_REQUEST_PARAMETER);

		if("loadData".equals(action))
		{
			String nodeId = request.stringFormValueForKey("node");
			
			WOResponse response = new WOResponse();
			response.appendContentString(getJSONForChildren(nodeId));
			return response;
		}
		else
		{
			return null;
		}
	}
}
