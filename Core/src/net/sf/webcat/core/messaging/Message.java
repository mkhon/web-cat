/*==========================================================================*\
 |  $Id: Message.java,v 1.1 2009/12/09 04:58:37 aallowat Exp $
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

package net.sf.webcat.core.messaging;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sf.webcat.core.Application;
import net.sf.webcat.core.BroadcastMessageSubscription;
import net.sf.webcat.core.MutableDictionary;
import net.sf.webcat.core.ProtocolSettings;
import net.sf.webcat.core.SentMessage;
import net.sf.webcat.core.Subsystem;
import net.sf.webcat.core.User;
import net.sf.webcat.core.UserMessageSubscription;
import org.apache.log4j.Logger;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

//-------------------------------------------------------------------------
/**
 * The abstract base class for all notification messages in Web-CAT. Subclasses
 * should localize their registration by providing a static register() method
 * that calls the {@link #registerMessage(Class, String, boolean, int)} with
 * the appropriate parameters, and then the subsystem can all these register()
 * methods in its {@link Subsystem#init()}.
 *
 * @author Tony Allevato
 * @version $Id: Message.java,v 1.1 2009/12/09 04:58:37 aallowat Exp $
 */
public abstract class Message
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Registers the specified message type in the messaging system.
     *
     * @param messageClass the class of message being registered
     * @param category a human-readable name of a category for this message,
     *     used for grouping
     * @param description a human-readable name of the message type, suitable
     *     for display in the Web-CAT user interface
     * @param isBroadcast true if messages of this type should be broadcast
     *     to any system-wide notification protocols (such as Twitter feeds),
     *     in addition to being sent to the message's list of users
     * @param accessLevel the lowest access level of users who can receive
     *     this message type (used to control whether the user can view it in
     *     the message/protocol enablement matrix)
     */
    public static void registerMessage(Class<? extends Message> messageClass,
            String category, String description, boolean isBroadcast,
            int accessLevel)
    {
        MessageDescriptor descriptor = new MessageDescriptor(
                messageClass.getCanonicalName(),
                category, description, isBroadcast, accessLevel);

        descriptors.put(messageClass, descriptor);
    }


    // ----------------------------------------------------------
    /**
     * Gets the descriptors for the currently registered direct-to-user
     * messages. This method only returns those messages that can be sent to
     * the specified user, based on access level.
     *
     * @param user the user
     * @return an array of message descriptors
     */
    public static NSArray<MessageDescriptor> registeredUserMessages(User user)
    {
        NSMutableArray<MessageDescriptor> descs =
            new NSMutableArray<MessageDescriptor>();

        for (MessageDescriptor descriptor : descriptors.values())
        {
            if (!descriptor.isBroadcast() &&
                    user.accessLevel() >= descriptor.accessLevel())
            {
                descs.addObject(descriptor);
            }
        }

        return descs;
    }


    // ----------------------------------------------------------
    /**
     * Gets the descriptors for the currently registered broadcast messages.
     *
     * @return an array of message descriptors
     */
    public static NSArray<MessageDescriptor> registeredBroadcastMessages()
    {
        NSMutableArray<MessageDescriptor> descs =
            new NSMutableArray<MessageDescriptor>();

        for (MessageDescriptor descriptor : descriptors.values())
        {
            if (descriptor.isBroadcast())
            {
                descs.addObject(descriptor);
            }
        }

        return descs;
    }


    // ----------------------------------------------------------
    /**
     * Registers a message-sending protocol with the messaging system.
     *
     * @param protocol the protocol instance to register
     */
    public static void registerProtocol(Protocol protocol)
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
     * Gets the currently registered protocols.
     *
     * @param isBroadcast true to get broadcast protocols, false to get direct
     *     message protocols
     * @return an array of protocols
     */
    public static NSArray<Protocol> registeredProtocols(boolean isBroadcast)
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
     * A shorthand method for getting the message type name, which is the
     * canonical name of the message class.
     *
     * @return the canonical class name of the message
     */
    public final String messageType()
    {
        return getClass().getCanonicalName();
    }


    // ----------------------------------------------------------
    /**
     * Gets the list of users who should receive this message. If the message
     * is meant to be broadcast system-wide only, then this method may return
     * null.
     *
     * @return an array of Users who should receive the message, or null if it
     *     should only be broadcast system-wide
     */
    public abstract NSArray<User> users();


    // ----------------------------------------------------------
    /**
     * Gets the title of the message. The message title is used in situations
     * where it is useful to differentiate a very brief description of the
     * message from its longer-form content (such as the subject line of an
     * e-mail). Titles are typically very brief, even shorter than the
     * {@link #shortBody()} of the message.
     *
     * @return the title of the message
     */
    public abstract String title();


    // ----------------------------------------------------------
    /**
     * Gets the short-form body content of the message. The short form is used
     * in the pop-up notification list, and is available for messaging
     * protocols that have constraints on the length of the content that they
     * can send (such as Twitter and SMS).
     *
     * Subclasses should attempt to keep their short form content under 140
     * characters as a rule of thumb, but no guarantees are made to ensure
     * this, so specific protocols may need to further elide this content if
     * necessary.
     *
     * @return the short form of the message body
     */
    public abstract String shortBody();


    // ----------------------------------------------------------
    /**
     * Gets the full-form body content of the message. The full form can be an
     * arbitrary length and should be used by protocols where there are no
     * tight constraints on message content length, such as the body of an
     * e-mail or description of an item in an RSS feed.
     *
     * @return the full form of the message body
     */
    public abstract String fullBody();


    // ----------------------------------------------------------
    /**
     * Gets a set of attachments that should be sent along with the message, if
     * the protocol (such as e-mail) supports attachments. Since many messaging
     * protocols do not support attachments, there should not be any critical
     * information included here. This method may return null if there are no
     * attachments.
     *
     * @return a dictionary of attachments, keyed by the name of the attachment
     */
    public NSDictionary<String, NSData> attachments()
    {
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Gets a set of URLs that link to relevant content for the message. This
     * method may return null if there are no links for a particular message.
     *
     * @return a dictionary containing links to relevant content, keyed by
     *     plain-text labels that describe each link
     */
    public NSDictionary<String, URL> links()
    {
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Gets the protocol settings that should be used when broadcasting this
     * message system-wide. The default behavior returns the global system
     * settings; subclasses can override this to change the destination of the
     * message, such as to a course-specific Twitter feed when the message is
     * relevant to a particular course or assignment.
     *
     * @return the ProtocolSettings object to use when broadcasting the message
     */
    public ProtocolSettings broadcastProtocolSettings()
    {
        EOEditingContext ec = editingContext();

        try
        {
            ec.lock();
            return ProtocolSettings.systemSettings(ec);
        }
        finally
        {
            ec.unlock();
        }
    }


    // ----------------------------------------------------------
    /**
     * Gets the protocol settings that should be used when sending this message
     * to the specified user. The default behavior returns the user's protocol
     * settings; subclasses should usually not need to override this method
     * since the default behavior is usually appropriate.
     *
     * @param user the User to whom the message is being sent
     * @return the ProtocolSettings object to use when sending the message
     */
    public ProtocolSettings protocolSettingsForUser(User user)
    {
        return user.protocolSettings();
    }


    // ----------------------------------------------------------
    /**
     * Sends the message.
     */
    public final void send()
    {
        EOEditingContext ec = editingContext();

        try
        {
            ec.lock();
            sendWithLockedEditingContext(ec);
        }
        finally
        {
            ec.unlock();
        }
    }


    // ----------------------------------------------------------
    /**
     * Sends the message, after the editing context has been locked.
     *
     * @param ec the editing context to use for fetching protocol settings
     */
    private void sendWithLockedEditingContext(EOEditingContext ec)
    {
        MessageDescriptor descriptor = descriptors.get(getClass());

        // Broadcast the message to any system-wide messaging protocols that
        // apply.

        if (descriptor.isBroadcast())
        {
            ProtocolSettings protocolSettings = broadcastProtocolSettings();
            Set<Protocol> protocols = broadcastProtocolsToSend(ec);

            for (Protocol protocol : protocols)
            {
                try
                {
                    protocol.sendMessage(this, null, protocolSettings);
                }
                catch (Exception e)
                {
                    log.error("The following error occurred when sending " +
                            "the broadcast message \"" + title() + "\"", e);
                }
            }
        }

        // Send the message directly to any users to whom the message applies,
        // if they have notifications for a particular protocol enabled.

        NSArray<User> users = users();
        if (users != null)
        {
            for (User user : users)
            {
                // Sanity check to ensure that messages don't get sent to users
                // who shouldn't receive them based on their access level.

                if (user.accessLevel() >= descriptor.accessLevel())
                {
                    ProtocolSettings protocolSettings =
                        protocolSettingsForUser(user);
                    Set<Protocol> protocols = userProtocolsToSend(user);

                    for (Protocol protocol : protocols)
                    {
                        try
                        {
                            protocol.sendMessage(this, user, protocolSettings);
                        }
                        catch (Exception e)
                        {
                            log.error("The following error occurred when " +
                                    "sending the direct message \"" + title() +
                                    "\" to user " + user.name(), e);
                        }
                    }
                }
            }
        }

        // Store the message in the database.

        storeMessage(ec, descriptor);
    }


    // ----------------------------------------------------------
    /**
     * Stores the contents of this message in the database.
     *
     * @param descriptor the message descriptor for this message
     * @param ec the editing context
     */
    private void storeMessage(EOEditingContext ec, MessageDescriptor descriptor)
    {
        // Create a representation of the message in the database so that it
        // can be viewed by users later.

        SentMessage message = SentMessage.create(ec, false);

        message.setSentTime(new NSTimestamp());
        message.setMessageType(messageType());
        message.setTitle(title());
        message.setShortBody(shortBody());

        if (links() != null)
        {
            message.setLinks(new MutableDictionary(links()));
        }

        for (User user : users())
        {
            // Sanity check to ensure that messages don't get sent to users
            // who shouldn't receive them based on their access level.

            if (user.accessLevel() >= descriptor.accessLevel())
            {
                message.addToUsersRelationship(user.localInstance(ec));
            }
        }

        ec.saveChanges();
    }


    // ----------------------------------------------------------
    /**
     * Gets the set of protocols that the system administrator has enabled for
     * the specified message type.
     *
     * @param ec the editing context
     * @return the set of Protocols that the system administrator has enabled
     *     for broadcast messages
     */
    private Set<Protocol> broadcastProtocolsToSend(EOEditingContext ec)
    {
        String messageType = messageType();

        Set<Protocol> protocolsToSend = new HashSet<Protocol>();

        NSArray<BroadcastMessageSubscription> subscriptions =
            BroadcastMessageSubscription.enabledSubscriptionsForMessageType(
                    ec, messageType);

        for (Protocol protocol : broadcastProtocols.values())
        {
            if (protocol.isEnabledByDefault())
            {
                protocolsToSend.add(protocol);
            }
        }

        for (BroadcastMessageSubscription subscription : subscriptions)
        {
            String type = subscription.protocolType();
            Protocol protocol = protocolWithName(type, true);

            if (protocol != null)
            {
                if (subscription.isEnabled())
                {
                    protocolsToSend.add(protocol);
                }
                else
                {
                    protocolsToSend.remove(protocol);
                }
            }
            else
            {
                log.warn("Attempted to send broadcast message via " +
                        "unregistered protocol: " + type);
            }
        }

        return protocolsToSend;
    }


    // ----------------------------------------------------------
    /**
     * Gets the set of protocols that the specified user has enabled for a
     * particular message type.
     *
     * @param user the User who will be receiving a message
     * @param ec the editing context
     * @return the set of Protocols that this user has enabled
     */
    private Set<Protocol> userProtocolsToSend(User user)
    {
        String messageType = messageType();
        EOEditingContext ec = user.editingContext();

        Set<Protocol> protocolsToSend = new HashSet<Protocol>();

        NSArray<UserMessageSubscription> subscriptions =
            UserMessageSubscription.enabledSubscriptionsForMessageTypeAndUser(
                    ec, messageType, user);

        for (Protocol protocol : directProtocols.values())
        {
            if (protocol.isEnabledByDefault())
            {
                protocolsToSend.add(protocol);
            }
        }

        for (UserMessageSubscription subscription : subscriptions)
        {
            String type = subscription.protocolType();
            Protocol protocol = protocolWithName(type, false);

            if (protocol != null)
            {
                if (subscription.isEnabled())
                {
                    protocolsToSend.add(protocol);
                }
                else
                {
                    protocolsToSend.remove(protocol);
                }
            }
            else
            {
                log.warn("Attempted to send direct message via " +
                        "unregistered protocol: " + type);
            }
        }

        return protocolsToSend;
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
    private Protocol protocolWithName(String typename, boolean isBroadcast)
    {
        try
        {
            Class<?> klass = Class.forName(typename);
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


    // ----------------------------------------------------------
    /**
     * Gets an editing context that will be used by the messaging system to
     * fetch broadcast and user message preferences, creating one if it does
     * not yet exist. To prevent bloat, the editing context is recycled
     * whenever more than one minute has passed between uses.
     *
     * @return an editing context
     */
    private EOEditingContext editingContext()
    {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastECCreationTime >= TIME_TO_RECYCLE_EC)
        {
            if (editingContext != null)
            {
                Application.releasePeerEditingContext(editingContext);
            }

            editingContext = null;
        }

        if (editingContext == null)
        {
            editingContext = Application.newPeerEditingContext();
            lastECCreationTime = System.currentTimeMillis();
        }

        return editingContext;
    }


    //~ Static/instance variables .............................................

    private EOEditingContext editingContext;
    private long lastECCreationTime;

    private static final long TIME_TO_RECYCLE_EC = 60 * 1000; // 1 minute

    private static Map<Class<? extends Message>, MessageDescriptor> descriptors =
        new HashMap<Class<? extends Message>, MessageDescriptor>();

    private static Map<Class<? extends Protocol>, Protocol> broadcastProtocols =
        new HashMap<Class<? extends Protocol>, Protocol>();
    private static Map<Class<? extends Protocol>, Protocol> directProtocols =
        new HashMap<Class<? extends Protocol>, Protocol>();

    private static final Logger log = Logger.getLogger(Message.class);
}
