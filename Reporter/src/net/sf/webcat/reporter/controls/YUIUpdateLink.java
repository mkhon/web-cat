package net.sf.webcat.reporter.controls;

import net.sf.webcat.core.WCResourceManager;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.ajax.AjaxDynamicElement;
import er.ajax.AjaxOption;
import er.ajax.AjaxOptions;
import er.ajax.AjaxUpdateContainer;
import er.ajax.AjaxUtils;

public class YUIUpdateLink extends AjaxDynamicElement
{
	// MS: If you change this value, make sure to change it in
	// ERXSession.saveSession
	public static final String KEY_AJAX_SUBMIT_BUTTON_NAME = "AJAX_SUBMIT_BUTTON_NAME";

	public YUIUpdateLink(String name, NSDictionary associations,
			WOElement children)
	{
		super(name, associations, children);
	}

	public static boolean isAjaxSubmit(WORequest request) {
		return request.valueForKey(KEY_AJAX_SUBMIT_BUTTON_NAME) != null;
	}

	public boolean disabledInComponent(WOComponent component) {
		return booleanValueForBinding("disabled", false, component);
	}

	public String nameInContext(WOContext context, WOComponent component) {
		return (String) valueForBinding("name", context.elementID(), component);
	}

	public NSDictionary createAjaxOptions(WOComponent component,
			String formReference)
	{
		String name = nameInContext(component.context(), component);

		NSMutableArray ajaxOptionsArray = new NSMutableArray();
		ajaxOptionsArray.addObject(new AjaxOption("onComplete", AjaxOption.SCRIPT));
		ajaxOptionsArray.addObject(new AjaxOption("onSuccess", AjaxOption.SCRIPT));
		ajaxOptionsArray.addObject(new AjaxOption("onFailure", AjaxOption.SCRIPT));
		ajaxOptionsArray.addObject(new AjaxOption("onLoading", AjaxOption.SCRIPT));
		ajaxOptionsArray.addObject(new AjaxOption("evalScripts", AjaxOption.BOOLEAN));
		ajaxOptionsArray.addObject(new AjaxOption("insertion", AjaxOption.SCRIPT));
		NSMutableDictionary options = AjaxOption.createAjaxOptionsDictionary(
				ajaxOptionsArray, component, associations());

		StringBuffer parametersBuffer = new StringBuffer();
		parametersBuffer.append("Form.serializeWithoutSubmits(" + formReference
				+ ")");
		parametersBuffer.append(" + '");
		parametersBuffer.append("&"
				+ AjaxSubmitLink.KEY_AJAX_SUBMIT_BUTTON_NAME + "=" + name);
		String updateContainerID = (String) valueForBinding(
				"updateContainerID", component);
		if (updateContainerID != null) {
//			parametersBuffer.append("&"
//					+ AjaxUpdateContainer.UPDATE_CONTAINER_ID_KEY + "="
//					+ updateContainerID);
		}
		parametersBuffer.append("'");
		options.setObjectForKey(parametersBuffer.toString(), "parameters");
		if (options.objectForKey("evalScripts") == null) {
			options.setObjectForKey("true", "evalScripts");
		}
		return options;
	}

	public void appendToResponse(WOResponse response, WOContext context)
	{
		WOComponent component = context.component();

		String formReference = "Ext.get(this).findParent('form')";

		StringBuffer onClickBuffer = new StringBuffer();

/*		String onClickBefore = (String) valueForBinding("onClickBefore",
				component);
		if (onClickBefore != null) {
			onClickBuffer.append("if (");
			onClickBuffer.append(onClickBefore);
			onClickBuffer.append(") {");
		}*/
		String updateContainerID = (String) valueForBinding(
				"updateContainerID", component);
		
		onClickBuffer.append("Ext.get('" + updateContainerID
				+ "').getUpdateManager().update({ ");

		NSDictionary options = createAjaxOptions(component, formReference);

		onClickBuffer.append("url: ");
		onClickBuffer.append(formReference + ".action");
		onClickBuffer.append(",");
		onClickBuffer.append(" scripts: true,");
		onClickBuffer.append(" parameters: " + options.objectForKey("parameters"));
		onClickBuffer.append("}); alert(" + options.objectForKey("parameters") + ")");

//		AjaxOptions.appendToBuffer(options, onClickBuffer, context);
//		onClickBuffer.append(")");

		String onClick = (String) valueForBinding("onClick", component);
		if (onClick != null) {
			onClickBuffer.append(";");
			onClickBuffer.append(onClick);
		}

		response
				.appendContentString("<a href=\"javascript:void(0)\" ");
		appendTagAttributeToResponse(response, "class", valueForBinding(
				"class", component));
		appendTagAttributeToResponse(response, "style", valueForBinding(
				"style", component));
		appendTagAttributeToResponse(response, "id", valueForBinding("id",
				component));
		response._appendTagAttributeAndValue("onclick", onClickBuffer.toString(), false);
//		appendTagAttributeToResponse(response, "onclick", onClickBuffer
//				.toString());

		response.appendContentString(">");
		if(hasChildrenElements())
			appendChildrenToResponse(response, context);
		response.appendContentString("</a>");

		super.appendToResponse(response, context);
	}

	protected void addRequiredWebResources(WOResponse res, WOContext context)
	{
		addStylesheetResourceInHead(context, res, "Reporter", "yui-ext/resources/css/ext-all.css");
		
		addScriptResourceInHead(context, res, "prototype.js");
		addScriptResourceInHead(context, res, "scriptaculous.js");
		addScriptResourceInHead(context, res, "effects.js");
		addScriptResourceInHead(context, res, "builder.js");
		addScriptResourceInHead(context, res, "controls.js");
		addScriptResourceInHead(context, res, "wonder.js");
		addProperScriptResourceInHead(context, res, "Reporter.framework/WebServerResources/yui-ext/ext-prototype-adapter.js");
		addProperScriptResourceInHead(context, res, "Reporter.framework/WebServerResources/yui-ext/ext-all.js");
	}
	
	protected void addProperScriptResourceInHead(WOContext context, WOResponse response,
			String path)
	{
		String script = "<script type=\"text/javascript\" src=\"" +
			WCResourceManager.frameworkPrefixedResourceURLFor( path,
                context.request() ) + "\"></script>";

//		AjaxUtils.insertInResponseBeforeTag(response,
//				script, AjaxUtils.htmlCloseHead());
	}

	public WOActionResults invokeAction(WORequest worequest, WOContext wocontext) {
		WOActionResults result = null;
		WOComponent wocomponent = wocontext.component();

		String nameInContext = nameInContext(wocontext, wocomponent);
		boolean shouldHandleRequest = (!disabledInComponent(wocomponent) && wocontext
				._wasFormSubmitted())
				&& ((wocontext._isMultipleSubmitForm() && nameInContext
						.equals(worequest
								.formValueForKey(KEY_AJAX_SUBMIT_BUTTON_NAME))) || !wocontext
						._isMultipleSubmitForm());
		if (shouldHandleRequest) {
			wocontext._setActionInvoked(true);
			result = handleRequest(worequest, wocontext);
			AjaxUtils.updateMutableUserInfoWithAjaxInfo(wocontext);
		}
		return result;
	}

	public WOActionResults handleRequest(WORequest worequest,
			WOContext wocontext) {
		WOResponse response = AjaxUtils.createResponse(worequest, wocontext);
		WOComponent wocomponent = wocontext.component();
		Object obj = valueForBinding("action", wocomponent);
/*		String onClickServer = (String) valueForBinding("onClickServer",
				wocomponent);
		if (onClickServer != null) {
			AjaxUtils.appendScriptHeaderIfNecessary(worequest, response);
			response.appendContentString(onClickServer);
			AjaxUtils.appendScriptFooterIfNecessary(worequest, response);
		}*/
		return response;
	}

}
