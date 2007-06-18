package net.sf.webcat.reporter.controls;

import net.sf.webcat.core.WCResourceManager;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver._private.WODynamicElementCreationException;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.ajax.AjaxDynamicElement;
import er.ajax.AjaxOption;
import er.ajax.AjaxOptions;
import er.ajax.AjaxUpdateContainer;
import er.ajax.AjaxUtils;
import er.extensions.ERXWOForm;

/**
 * AjaxSubmitLink behaves just like a WOSubmitButton except that it submits in
 * the background with an Ajax.Request.
 * 
 * @binding name the HTML name of this submit button (optional)
 * @binding value the HTML value of this submit button (optional)
 * @binding action the action to execute when this button is pressed
 * @binding id the HTML ID of this submit button
 * @binding class the HTML class of this submit button
 * @binding style the HTML style of this submit button
 * @binding onClick arbitrary Javascript to execute when the client clicks the
 *          button
 * @binding onClickBefore if the given function returns true, the onClick is
 *          executed. This is to support confirm(..) dialogs.
 * @binding onServerClick if the action defined in the action binding returns
 *          null, the value of this binding will be returned as javascript from
 *          the server
 * @binding onComplete JavaScript function to evaluate when the request has
 *          finished.
 * @binding onSuccess javascript to execute in response to the Ajax onSuccess
 *          event
 * @binding onFailure javascript to execute in response to the Ajax onFailure
 *          event
 * @binding onLoading javascript to execute when loading
 * @binding insertion JavaScript function to evaluate when the update takes
 *          place.
 * @binding evalScripts evaluate scripts on the result
 * @binding button if false, it will display a link
 * @binding formName if button is false, you must specify the name of the form
 *          to submit
 * @binding functionName if set, the link becomes a javascript function instead
 * @binding updateContainerID the id of the AjaxUpdateContainer to update after
 *          performing this action
 * @binding showUI if functionName is set, the UI defaults to hidden; showUI
 *          re-enables it
 * 
 * @author anjo
 */
public class ScriptActionButton extends AjaxDynamicElement
{
	// MS: If you change this value, make sure to change it in
	// ERXSession.saveSession
	public static final String KEY_AJAX_SUBMIT_BUTTON_NAME = "AJAX_SUBMIT_BUTTON_NAME";

	public ScriptActionButton(String name, NSDictionary associations,
			WOElement children) {
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
			String formReference) {
		String name = nameInContext(component.context(), component);
		NSMutableArray ajaxOptionsArray = new NSMutableArray();
		ajaxOptionsArray.addObject(new AjaxOption("onComplete",
				AjaxOption.SCRIPT));
		ajaxOptionsArray.addObject(new AjaxOption("onSuccess",
				AjaxOption.SCRIPT));
		ajaxOptionsArray.addObject(new AjaxOption("onFailure",
				AjaxOption.SCRIPT));
		ajaxOptionsArray.addObject(new AjaxOption("onLoading",
				AjaxOption.SCRIPT));
		NSMutableDictionary options = AjaxOption.createAjaxOptionsDictionary(
				ajaxOptionsArray, component, associations());

		StringBuffer parametersBuffer = new StringBuffer();
		parametersBuffer.append("Form.serializeWithoutSubmits(" + formReference
				+ ")");
		parametersBuffer.append(" + '");
		parametersBuffer.append("&"
				+ AjaxSubmitLink.KEY_AJAX_SUBMIT_BUTTON_NAME + "=" + name);
		parametersBuffer.append("'");
		options.setObjectForKey(parametersBuffer.toString(), "parameters");
//		options.setObjectForKey("function(transport) { eval(transport.responseText); }", "onComplete");
		return options;
	}

	public void appendToResponse(WOResponse response, WOContext context)
	{
		WOComponent component = context.component();

		String formReference = "this.up('form')";

		StringBuffer onClickBuffer = new StringBuffer();

//		String onClickBefore = (String) valueForBinding("onClickBefore",
//				component);
//		if (onClickBefore != null) {
//			onClickBuffer.append("if (");
//			onClickBuffer.append(onClickBefore);
//			onClickBuffer.append(") {");
//		}

		onClickBuffer.append("new Ajax.Request(");
		onClickBuffer.append(formReference + ".action");
		onClickBuffer.append(",");

		NSDictionary options = createAjaxOptions(component, formReference);
		AjaxOptions.appendToBuffer(options, onClickBuffer, context);
		onClickBuffer.append(")");

		String onClick = (String) valueForBinding("onClick", component);
		if(onClick != null)
		{
			onClickBuffer.append(";");
			onClickBuffer.append(onClick);
		}
//		if (onClickBefore != null) {
//			onClickBuffer.append("}");
//		}

		response.appendContentString("<input");
		appendTagAttributeToResponse(response, "type", "button");
		String name = nameInContext(context, component);
		appendTagAttributeToResponse(response, "name", name);
		appendTagAttributeToResponse(response, "value", valueForBinding("value", component));
		appendTagAttributeToResponse(response, "class", valueForBinding(
				"class", component));
		appendTagAttributeToResponse(response, "style", valueForBinding(
				"style", component));
		appendTagAttributeToResponse(response, "id", valueForBinding("id",
				component));

		if (disabledInComponent(component)) {
			appendTagAttributeToResponse(response, "disabled", "disabled");
		}

		appendTagAttributeToResponse(response, "onclick", onClickBuffer
				.toString());

		response.appendContentString(" />");

		super.appendToResponse(response, context);
	}

	protected void addRequiredWebResources(WOResponse res, WOContext context) {
/*		addStylesheetResourceInHead(context, res, "Reporter",
				"yui-ext/resources/css/ext-all.css");
*/
		addScriptResourceInHead(context, res, "prototype.js");
/*		addScriptResourceInHead(context, res, "scriptaculous.js");
		addScriptResourceInHead(context, res, "effects.js");
		addScriptResourceInHead(context, res, "builder.js");
		addScriptResourceInHead(context, res, "controls.js");
		addScriptResourceInHead(context, res, "wonder.js");
		addProperScriptResourceInHead(context, res,
				"Reporter.framework/WebServerResources/yui-ext/ext-prototype-adapter.js");
		addProperScriptResourceInHead(context, res,
				"Reporter.framework/WebServerResources/yui-ext/ext-all.js");
*/
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

	public WOActionResults handleRequest(WORequest worequest, WOContext wocontext)
	{
		WOComponent wocomponent = wocontext.component();
		return (WOActionResults)valueForBinding("action", wocomponent);
	}

}
