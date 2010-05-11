/*==========================================================================*\
 |  $Id: SystemMessagingConfigPage.java,v 1.1 2010/05/11 14:51:35 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2009 Virginia Tech
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

package org.webcat.notifications;

import java.util.HashMap;
import org.webcat.core.MutableDictionary;
import org.webcat.core.WCComponent;
import org.webcat.core.messaging.Message;
import org.webcat.core.messaging.MessageDescriptor;
import org.webcat.notifications.protocols.Protocol;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOCustomObject;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import er.extensions.eof.ERXS;
import er.extensions.eof.ERXSortOrdering.ERXSortOrderings;

//-------------------------------------------------------------------------
/**
 * TODO real description
 *
 * @author Tony Allevato
 * @version $Id: SystemMessagingConfigPage.java,v 1.1 2010/05/11 14:51:35 aallowat Exp $
 */
public class SystemMessagingConfigPage extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public SystemMessagingConfigPage(WOContext context)
    {
        super(context);
    }


    //~ KVC attributes (must be public) .......................................

    public NSArray<Protocol> protocols;
    public Protocol protocol;
    public int indexOfProtocol;

    public ProtocolSettings protocolSettings;
    public MutableDictionary optionValues;


    //~ Methods ..........................................................

    // ----------------------------------------------------------
    @Override
    public void appendToResponse(WOResponse response, WOContext context)
    {
        protocols = MessageDispatcher.sharedDispatcher().registeredProtocols();

        protocolSettings = ProtocolSettings.systemSettings(localContext());

        if (protocolSettings.settings() != null)
        {
            optionValues = new MutableDictionary(protocolSettings.settings());
        }
        else
        {
            optionValues = new MutableDictionary();
        }

        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public String editGlobalOptionsDialogShowCall()
    {
        return "dijit.byId('smc_editGlobalOptions_" + indexOfProtocol + "').show();";
    }


    // ----------------------------------------------------------
    public NSArray<NSDictionary<String, Object>> protocolOptions()
    {
        return protocol.globalOptions();
    }


    // ----------------------------------------------------------
    public WOActionResults optionsEdited()
    {
        if (optionValues.isEmpty())
        {
            protocolSettings.setSettings(null);
        }
        else
        {
            protocolSettings.setSettings(optionValues);
        }

        applyLocalChanges();

        return null;
    }
}
