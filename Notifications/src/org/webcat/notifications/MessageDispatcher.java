package org.webcat.notifications;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
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

public class MessageDispatcher implements IMessageDispatcher
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
            job.setAttachments(new MutableDictionary(message.attachments()));
        }

        for (User user : message.users())
        {
            job.addToDestinationUsers(User.localInstance(ec, user));
        }

        job.setIsReady(true);
        ec.saveChanges();
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

    private static final Logger log = Logger.getLogger(MessageDispatcher.class);
}
