/*==========================================================================*\
 |  $Id: ProtocolSettings.java,v 1.1 2010/04/30 17:17:19 aallowat Exp $
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

package net.sf.webcat.notifications;

import com.webobjects.eocontrol.EOEditingContext;
import net.sf.webcat.core.MutableDictionary;
import net.sf.webcat.core.User;
import net.sf.webcat.core.messaging.IMessageSettings;

// -------------------------------------------------------------------------
/**
 * TODO: place a real description here.
 *
 * @author
 * @author  latest changes by: $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2010/04/30 17:17:19 $
 */
public class ProtocolSettings
    extends _ProtocolSettings
    implements IMessageSettings
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new ProtocolSettings object.
     */
    public ProtocolSettings()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets the system-wide broadcast protocol settings as defined by the
     * system administrator.
     *
     * This object will always have id 1; it is assumed that the Application
     * class has made sure that this object exists during its initialization.
     *
     * @param ec the editing context
     * @return the system-wide broadcast protocol settings, or null if it does
     *     not exist
     */
    public static ProtocolSettings systemSettings(EOEditingContext ec)
    {
        return ProtocolSettings.forId(ec, 1);
    }


    // ----------------------------------------------------------
    /**
     * Retrieves the protocol settings for the given user. If no settings
     * object yet exists for this user, an empty one is created as associated
     * with them.
     *
     * @param user the user
     * @return the protocol settings for the user
     */
    public static ProtocolSettings protocolSettingsForUser(
            User user)
    {
        ProtocolSettings settings =
            ProtocolSettings.uniqueObjectMatchingQualifier(
                user.editingContext(),
                ProtocolSettings.user.is(user));

        if (settings == null)
        {
            settings = ProtocolSettings.create(user.editingContext(), false);
            settings.setUserRelationship(user);
            user.editingContext().saveChanges();
        }

        return settings;
    }


    // ----------------------------------------------------------
    public Object settingForKey(String key)
    {
        Object value = settings().objectForKey(key);

        if (value == null && parent() != null)
        {
            value = parent().settingForKey(key);
        }

        return value;
    }


    // ----------------------------------------------------------
    public String stringSettingForKey(String key, String defaultValue)
    {
        String value = (String) settingForKey(key);
        return value == null ? defaultValue : value;
    }


    // ----------------------------------------------------------
    public boolean booleanSettingForKey(String key, boolean defaultValue)
    {
        Boolean value = (Boolean) settingForKey(key);
        return value == null ? defaultValue : value;
    }


    // ----------------------------------------------------------
    public int intSettingForKey(String key, int defaultValue)
    {
        Number value = (Number) settingForKey(key);
        return value == null ? defaultValue : value.intValue();
    }


    // ----------------------------------------------------------
    public double doubleSettingForKey(String key, double defaultValue)
    {
        Number value = (Number) settingForKey(key);
        return value == null ? defaultValue : value.doubleValue();
    }


    // ----------------------------------------------------------
    public MutableDictionary settingsSnapshot()
    {
        MutableDictionary snapshot = new MutableDictionary();
        gatherSettingsForSnapshot(snapshot);
        return snapshot;
    }


    // ----------------------------------------------------------
    private void gatherSettingsForSnapshot(MutableDictionary snapshot)
    {
        // First, get the parent's settings.

        if (parent() != null)
        {
            parent().gatherSettingsForSnapshot(snapshot);
        }

        // Then, replace settings that the child has overridden.

        snapshot.addEntriesFromDictionary(settings());
    }
}
