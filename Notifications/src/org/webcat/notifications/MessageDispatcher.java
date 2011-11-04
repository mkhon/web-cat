/*==========================================================================*\
 |  $Id: MessageDispatcher.java,v 1.3 2011/11/04 13:19:10 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2009 Virginia Tech
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
import java.util.Map;
import org.webcat.core.MutableDictionary;
import org.webcat.core.User;
import org.webcat.core.messaging.IMessageDispatcher;
import org.webcat.core.messaging.IMessageSettings;
import org.webcat.core.messaging.Message;
import org.webcat.notifications.protocols.EmailProtocol;
import org.webcat.notifications.protocols.Protocol;
import org.webcat.notifications.protocols.SMSProtocol;
import org.webcat.notifications.protocols.TwitterProtocol;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

//-------------------------------------------------------------------------
/**
 * TODO: place a real description here.
 *
 * @author  Tony Allevato
 * @author  Latest changes by: $Author: aallowat $
 * @version $Revision: 1.3 $, $Date: 2011/11/04 13:19:10 $
 */
public class MessageDispatcher
    implements IMessageDispatcher
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    private MessageDispatcher()
    {
        broadcastProtocols = new HashMap<Class<? extends Protocol>, Protocol>();
        directProtocols = new HashMap<Class<? extends Protocol>, Protocol>();

        // Register messaging protocols.

        registerProtocol(new EmailProtocol());
        registerProtocol(new TwitterProtocol());
        registerProtocol(new SMSProtocol());
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public static final MessageDispatcher sharedDispatcher()
    {
        if (instance == null)
        {
            instance = new MessageDispatcher();
        }

        return instance;
    }


    // ----------------------------------------------------------
    public void sendMessage(Message message)
    {
        EOEditingContext ec = message.editingContext();

        try
        {
            ec.lock();

            SendMessageJob job = SendMessageJob.create(ec, message.isSevere());

            IMessageSettings settings = message.broadcastSettings();
            if (settings instanceof ProtocolSettings)
            {
                job.setBroadcastProtocolSettingsRelationship(
                        ProtocolSettings.localInstance(ec,
                                (ProtocolSettings) settings));
            }
            else if (settings != null)
            {
                job.setBroadcastProtocolSettingsSnapshot(
                        settings.settingsSnapshot());
            }
            else
            {
                job.setBroadcastProtocolSettingsRelationship(
                        ProtocolSettings.systemSettings(ec));
            }

            job.setMessageType(message.messageType());
            job.setTitle(message.title());
            job.setShortBody(message.shortBody());
            job.setFullBody(message.fullBody());

            if (message.links() != null)
            {
                job.setLinks(new MutableDictionary(message.links()));
            }

            if (message.attachments() != null)
            {
                job.setAttachments(new MutableDictionary(
                        message.attachments()));
            }

            for (User user : message.users())
            {
                job.addToDestinationUsers(User.localInstance(ec, user));
            }

            job.setIsReady(true);
            ec.saveChanges();
        }
        finally
        {
            ec.unlock();
        }
    }


    // ----------------------------------------------------------
    /**
     * Registers a message-sending protocol with the messaging system.
     *
     * @param protocol the protocol instance to register
     */
    private void registerProtocol(Protocol protocol)
    {
        if (protocol.isBroadcast())
        {
            broadcastProtocols.put(protocol.getClass(), protocol);
        }
        else
        {
            directProtocols.put(protocol.getClass(), protocol);
        }
    }


    // ----------------------------------------------------------
    /**
     * Gets the currently registered protocols -- both direct and broadcast
     * protocols.
     *
     * @return an array of protocols
     */
    public NSArray<Protocol> registeredProtocols()
    {
        NSMutableArray<Protocol> protocols = new NSMutableArray<Protocol>();

        for (Protocol protocol : broadcastProtocols.values())
        {
            protocols.addObject(protocol);
        }

        for (Protocol protocol : directProtocols.values())
        {
            protocols.addObject(protocol);
        }

        return protocols;
    }


    // ----------------------------------------------------------
    /**
     * Gets the currently registered protocols.
     *
     * @param isBroadcast true to get broadcast protocols, false to get direct
     *     message protocols
     * @return an array of protocols
     */
    public NSArray<Protocol> registeredProtocols(boolean isBroadcast)
    {
        NSMutableArray<Protocol> protocols = new NSMutableArray<Protocol>();

        for (Protocol protocol : isBroadcast ?
                broadcastProtocols.values() : directProtocols.values())
        {
            protocols.addObject(protocol);
        }

        return protocols;
    }


    // ----------------------------------------------------------
    /**
     * Gets the registered Protocol with the specified class name.
     *
     * @param className the fully-qualified class name of the protocol
     * @param isBroadcast true if it is a broadcast protocol; false if it is a
     *     direct-to-user protocol
     * @return the Protocol object with the specified class name, or null if
     *     none was found
     */
    public Protocol protocolWithName(String className, boolean isBroadcast)
    {
        try
        {
            Class<?> klass = Class.forName(className);
            Class<? extends Protocol> protocolClass = klass.asSubclass(
                    Protocol.class);

            if (isBroadcast)
            {
                return broadcastProtocols.get(protocolClass);
            }
            else
            {
                return directProtocols.get(protocolClass);
            }
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }
    }


    //~ Static/instance variables .............................................

    private static MessageDispatcher instance;

    private Map<Class<? extends Protocol>, Protocol> broadcastProtocols;
    private Map<Class<? extends Protocol>, Protocol> directProtocols;
}
