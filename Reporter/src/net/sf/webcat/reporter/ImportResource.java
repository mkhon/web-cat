package net.sf.webcat.reporter;

import net.sf.webcat.core.WCResourceManager;

import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODynamicElement;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSDictionary;

import er.ajax.AjaxUtils;

public class ImportResource extends WODynamicElement
{
	private WOAssociation aType;
	
	private WOAssociation aFramework;
	
	private WOAssociation aFilename;

	public ImportResource(String aName, NSDictionary associations,
			WOElement template)
	{
		super(aName, associations, template);
		
		aType = (WOAssociation)associations.objectForKey("type");
		aFramework = (WOAssociation)associations.objectForKey("framework");
		aFilename = (WOAssociation)associations.objectForKey("filename");
	}
	
	public void appendToResponse(WOResponse response, WOContext context)
	{
		WOComponent component = context.component();
		
		String type = aType != null ?
				(String)aType.valueInComponent(component) : null;
		String framework = aFramework != null ?
				(String)aFramework.valueInComponent(component) : null;
		String filename = aFilename != null ?
				(String)aFilename.valueInComponent(component) : null;

		// If no framework is specified, get the one from the calling
		// component.
		if(framework == null)
		{
			NSBundle bundle = NSBundle.bundleForClass(
					context.component().getClass());
			framework = bundle.name();
		}

		if(type.equalsIgnoreCase("script"))
		{
			AjaxUtils.addScriptResourceInHead(context, response, framework,
					filename);
		}
		else if(type.equalsIgnoreCase("stylesheet"))
		{
			AjaxUtils.addStylesheetResourceInHead(context, response, framework,
					filename);
		}
	}
}
