//
// ERXWOForm.java
// Project armehaut
//
// Created by ak on Mon Apr 01 2002
//

package er.extensions;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

/** 
 * Transparent replacement for WOForm. You don't really need to do anything to use it, because it
 * will get used instead of WOForm elements automagically. In addition, it has a few new features:
 * <ul>
 * <li> it adds the FORM's name to the ERXWOContext's mutableUserInfo as as "formName" key,
 * which makes writing JavaScript elements a bit easier.
 * <li> it warns you when you have one FORM embedded inside another and ommits the tags for the nested FORM.
 * <li> it pushes the <code>enctype</code> into the userInfo, so that {@link ERXWOFileUpload}
 * can check if it is set correctly. ERXFileUpload will throw an exception if the enctype is not set.
 * <li> it has a "fragmentIdentifier" binding, which, when set spews out some javascript that appends 
 * "#" + the value of the binding to the action. The obvious case comes when you have a form at the bottom of the page
 * and want to jump to the error messages if there are any.  That javascript is used is an implementation 
 * detail, though and shouldn't be relied on.
 * </ul>
 * This subclass is installed when the frameworks loads. 
 * @author ak
 */  
public class ERXWOForm extends com.webobjects.appserver._private.WOForm {
    static final ERXLogger log = ERXLogger.getERXLogger(ERXWOForm.class);
    WOAssociation _formName;
    WOAssociation _enctype;
    WOAssociation _fragmentIdentifier;
    
    public ERXWOForm(String name, NSDictionary associations,
                     WOElement template) {
        super(name, associations, template);
        _formName = (WOAssociation) _associations.removeObjectForKey("name");
        _enctype = (WOAssociation) _associations.removeObjectForKey("enctype");
        _fragmentIdentifier = (WOAssociation) _associations.removeObjectForKey("fragmentIdentifier");
    }

    public void appendAttributesToResponse(WOResponse response, WOContext context) {
        if(context != null && context instanceof ERXMutableUserInfoHolderInterface) {
            NSMutableDictionary ui = ((ERXMutableUserInfoHolderInterface)context).mutableUserInfo();
            if(_formName != null) {
                String formName = (String)_formName.valueInComponent(context.component());
                if(formName != null) {
                    ui.setObjectForKey(formName, "formName");
                    response._appendTagAttributeAndValue("name", formName, false);
                }
            }
            if(_enctype != null) {
                String enctype = (String)_enctype.valueInComponent(context.component());
                if(enctype != null) {
                    ui.setObjectForKey(enctype.toLowerCase(), "enctype");
                    response._appendTagAttributeAndValue("enctype", enctype, false);
                }
            }
        }
        super.appendAttributesToResponse(response, context);
    }

    public void appendToResponse(WOResponse response, WOContext context) {
        boolean shouldAppendFormTags = !context.isInForm();
        
        if ( context != null && response != null )
        {
            String elementName = elementName();
            shouldAppendFormTags = shouldAppendFormTags 
                && elementName != null;

            if ( shouldAppendFormTags )
            {
                _appendOpenTagToResponse( response, context );
                context.setInForm( true );
            }
            else
            {
                log.warn(
                  "This FORM is embedded inside another FORM. Omitting Tags.");
            }

            this.appendChildrenToResponse( response, context );

            if ( shouldAppendFormTags )
            {
                _appendCloseTagToResponse( response, context );
                context.setInForm( false );
            }
        }
        if ( shouldAppendFormTags )
        {
            if( _fragmentIdentifier != null )
            {
                Object value = _fragmentIdentifier.valueInComponent(
                    context.component() );
                if ( value != null )
                {
                    response.appendContentString(
                        "<script language=\"javascript\">document.forms[document.forms.length-1].action+=\"#"
                        + value + "\";</script>" );
                }
            }
            if ( context instanceof ERXMutableUserInfoHolderInterface )
            {
                NSMutableDictionary ui =
                    ( (ERXMutableUserInfoHolderInterface)context )
                    .mutableUserInfo();
                ui.removeObjectForKey( "formName" );
                ui.removeObjectForKey( "enctype" );
            }
        }
    }
    
    /**
     * Retrieves the current FORM's name in the supplied context. If none is set 
     * (either the FORM is not a ERXWOForm or the context is not ERXMutableUserInfo) the supplied default
     * value is used. 
     * @param context current context
     * @param defaultName default name to use
     * @return form name in context or default value
     */
    public static String formName(WOContext context, String defaultName) {
    	String formName = defaultName;
    	if(context instanceof ERXMutableUserInfoHolderInterface) {
        	formName = (String) ((ERXMutableUserInfoHolderInterface)context).mutableUserInfo().objectForKey("formName");
        }
    	return formName;
    }
}
