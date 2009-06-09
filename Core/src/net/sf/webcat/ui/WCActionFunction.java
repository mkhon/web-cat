/*==========================================================================*\
 |  $Id: WCActionFunction.java,v 1.1 2009/06/09 17:25:40 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2008 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU Affero General Public License as published
 |  by the Free Software Foundation; either version 3 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU Affero General Public License
 |  along with Web-CAT; if not, see <http://www.gnu.org/licenses/>.
\*==========================================================================*/

package net.sf.webcat.ui;

import org.apache.log4j.Logger;
import net.sf.webcat.core.Application;
import net.sf.webcat.ui._base.DojoActionFormElement;
import net.sf.webcat.ui.util.DojoOptions;
import net.sf.webcat.ui.util.DojoRemoteHelper;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver._private.WOHTMLDynamicElement;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import er.ajax.AjaxUtils;
import er.extensions.appserver.ERXResponseRewriter;
import er.extensions.appserver.ERXWOContext;
import er.extensions.appserver.ajax.ERXAjaxApplication;
import er.extensions.components.ERXComponentUtilities;

//--------------------------------------------------------------------------
/**
 * Generates a JavaScript function that can be called to execute an action,
 * either as a synchronous (page-load) request or a remote Ajax request.
 * 
 * As with other Dojo action elements, the "remoteness" of the request is
 * determined by the presence of any of the "remote.*" bindings. If they are
 * omitted, the request is synchronous.
 * 
 * TODO does not currently support form submits for Ajax requests (uses
 * webcat.invokeRemoteAction instead of webcat.partialSubmit). This means that
 * form contents will not be serialized and the server-side component bindings
 * will not be updated.
 * 
 * @author Tony ALlevato
 * @version $Id: WCActionFunction.java,v 1.1 2009/06/09 17:25:40 aallowat Exp $
 */
public class WCActionFunction extends DojoActionFormElement
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public WCActionFunction(String name,
            NSDictionary<String, WOAssociation> someAssociations,
            WOElement template)
    {
        super(name, someAssociations, template);

        _jsId = _associations.removeObjectForKey("jsId");
    }


    //~ Methods ...............................................................
    
    // ----------------------------------------------------------
    @Override
    public String dojoType()
    {
        return null;
    }


    // ----------------------------------------------------------
    @Override
    protected boolean needsShadowButton()
    {
        return true;
    }
    
    
    // ----------------------------------------------------------
    protected String jsIdInContext(WOContext context)
    {
        if (_jsId != null)
        {
            return _jsId.valueInComponent(context.component()).toString();
        }
        else
        {
            return null;
        }
    }

    
    // ----------------------------------------------------------
    @Override
    public void appendToResponse(WOResponse response, WOContext context)
    {
        super.appendToResponse(response, context);
        
        String id = jsIdInContext(context);

        WOComponent component = context.component();

        String actionUrl = null;
        
        if (_directActionName != null)
        {
            actionUrl = context.directActionURLForActionNamed(
                    (String) _directActionName.valueInComponent(component),
                    ERXComponentUtilities.queryParametersInComponent(
                            _associations, component)).replaceAll("&amp;", "&");
        }
        else
        {
            actionUrl = AjaxUtils.ajaxComponentActionUrl(context);
        }

        StringBuffer script = new StringBuffer();
        script.append(id);
        script.append(" = function(widget) {\n");

        if (_remoteHelper.isRemoteInContext(context))
        {
            script.append(_remoteHelper.invokeRemoteActionCall(
                    "widget", actionUrl, null, context));
        }
        else
        {
            script.append("dojo.byId('"
                    + shadowButtonIdInContext(context)
                    + "').click();");
        }

        script.append("}");

        if (Application.isAjaxRequest(context.request()))
        {
            response.appendContentString("<script type=\"text/javascript\">");
            response.appendContentString(script.toString());
            response.appendContentString("</script>");
        }
        else
        {
            ERXResponseRewriter.addScriptCodeInHead(response, context,
                    script.toString());
        }
    }


    // ----------------------------------------------------------
    @Override
    protected void appendOnClickScriptToResponse(WOResponse response,
            WOContext context)
    {
        // Do nothing.
    }


    // ----------------------------------------------------------
    @Override
    protected void _appendOpenTagToResponse(WOResponse response,
            WOContext context)
    {
        // Do nothing.
    }

    
    // ----------------------------------------------------------
    @Override
    protected void _appendCloseTagToResponse(WOResponse response,
            WOContext context)
    {
        // Do nothing.
    }


    //~ Static/instance variables .............................................
    
    protected WOAssociation _jsId;

    private static final Logger log = Logger.getLogger(WCActionFunction.class);
}
