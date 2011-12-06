/*==========================================================================*\
 |  $Id: SurveyReminderMessage.java,v 1.2 2011/12/06 18:40:43 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2010 Virginia Tech
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

package org.webcat.opinions.messaging;

import org.webcat.core.User;
import org.webcat.core.WCProperties;
import org.webcat.core.messaging.Message;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

//-------------------------------------------------------------------------
/**
 * A message that is sent to the student when an engagement/frustration
 * survey is available for an assignment.
 *
 * @author  Stephen Edwards
 * @author  Latest changes by: $Author: stedwar2 $
 * @version $Revision: 1.2 $ $Date: 2011/12/06 18:40:43 $
 */
public class SurveyReminderMessage
    extends Message
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    public SurveyReminderMessage(User user, WCProperties properties)
    {
        EOEditingContext ec = editingContext();
        try
        {
            ec.lock();
            this.user = user.localInstance(ec);
        }
        finally
        {
            ec.unlock();
        }
        this.properties = properties;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Called by the subsystem init() to register the message.
     */
    public static void register()
    {
        Message.registerMessage(
                SurveyReminderMessage.class,
                "Opinions",
                "Survey Announcement",
                false,
                User.STUDENT_PRIVILEGES);
    }


    // ----------------------------------------------------------
    @Override
    public String fullBody()
    {
        return properties.stringForKeyWithDefault(
            "survey.email.body",
            "To help improve assignments in your course, you can provide "
            + "your opinions\nabout what was interesting or frustrating to "
            + "you.  This short survey should\nonly take a few minutes.  "
            + "Login to Web-CAT to complete the survey on your "
            + "assignment: \"${assignment.title}\".\n");
    }


    // ----------------------------------------------------------
    @Override
    public String shortBody()
    {
        return properties.stringForKeyWithDefault(
                "survey.email.short.body",
                "Login to Web-CAT to complete a short survey on your "
                + "assignment: \"${assignment.title}\".\n\n");
    }


    // ----------------------------------------------------------
    @Override
    public NSDictionary<String, String> links()
    {
        NSMutableDictionary<String, String> links =
            new NSMutableDictionary<String, String>();

        links.setObjectForKey(
                properties.stringForKey("survey.link"),
                "Complete your survey");

        return links;
    }


    // ----------------------------------------------------------
    @Override
    public String title()
    {
        return properties.stringForKeyWithDefault("survey.email.title",
                "[Opinions] Tell us about: \""
                + "${assignment.title}\"");
    }


    // ----------------------------------------------------------
    @Override
    public NSArray<User> users()
    {
        return new NSArray<User>(user);
    }


    //~ Static/instance variables .............................................

    private User user;
    private WCProperties properties;
}
