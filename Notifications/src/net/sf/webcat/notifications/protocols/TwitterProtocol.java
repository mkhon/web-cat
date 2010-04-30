/*==========================================================================*\
 |  $Id: TwitterProtocol.java,v 1.1 2010/04/30 17:17:19 aallowat Exp $
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

package net.sf.webcat.notifications.protocols;

import net.sf.webcat.core.User;
import net.sf.webcat.core.messaging.IMessageSettings;
import net.sf.webcat.notifications.ProtocolSettings;
import net.sf.webcat.notifications.SendMessageJob;
import org.apache.log4j.Logger;
import twitter4j.AsyncTwitter;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;

//-------------------------------------------------------------------------
/**
 * A notification protocol that delivers messages as Twitter feed updates.
 *
 * @author Tony Allevato
 * @version $Id: TwitterProtocol.java,v 1.1 2010/04/30 17:17:19 aallowat Exp $
 */
public class TwitterProtocol extends Protocol
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    public void sendMessage(SendMessageJob message,
                            User user,
                            IMessageSettings settings)
    throws Exception
    {
        final String username =
            settings.stringSettingForKey(USERNAME_SETTING, null);
        String password = settings.stringSettingForKey(PASSWORD_SETTING, null);

        if (username == null || password == null)
        {
            return;
        }

        String content = message.shortBody();

        if (content.length() > 140)
        {
            content = content.substring(0, 137) + "...";
        }

        AsyncTwitter twitter = new AsyncTwitter(username, password);

        twitter.updateStatusAsync(content, new TwitterAdapter() {
            @Override public void onException(TwitterException e, int method)
            {
                if (method == AsyncTwitter.UPDATE_STATUS)
                {
                    log.warn("An error occurred when updating the Twitter "
                            + "feed \"" + username + "\"", e);
                }
            }
        });
    }


    // ----------------------------------------------------------
    @Override
    public boolean isBroadcast()
    {
        return true;
    }


    // ----------------------------------------------------------
    @Override
    public String name()
    {
        return "Twitter";
    }


    //~ Static/instance variables .............................................

    private static final String USERNAME_SETTING =
        "net.sf.webcat.notifications.protocols.TwitterProtocol.username";
    private static final String PASSWORD_SETTING =
        "net.sf.webcat.notifications.protocols.TwitterProtocol.password";

    private static final Logger log = Logger.getLogger(TwitterProtocol.class);
}
