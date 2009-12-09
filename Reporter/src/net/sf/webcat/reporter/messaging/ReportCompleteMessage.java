/*==========================================================================*\
 |  $Id: ReportCompleteMessage.java,v 1.1 2009/12/09 05:03:40 aallowat Exp $
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

package net.sf.webcat.reporter.messaging;

import com.webobjects.foundation.NSArray;
import net.sf.webcat.core.User;
import net.sf.webcat.core.messaging.Message;
import net.sf.webcat.reporter.GeneratedReport;

//-------------------------------------------------------------------------
/**
 * TODO add description
 *
 * @author Tony Allevato
 * @version $Id: ReportCompleteMessage.java,v 1.1 2009/12/09 05:03:40 aallowat Exp $
 */
public class ReportCompleteMessage extends Message
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new instance of the ReportCompleteMessage class.
     *
     * @param report the report that was completed
     */
    public ReportCompleteMessage(GeneratedReport report)
    {
        this.report = report;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public static void register()
    {
        Message.registerMessage(
                ReportCompleteMessage.class,
                "Reports",
                "Report Completed",
                false,
                User.GRADER_PRIVILEGES);
    }


    // ----------------------------------------------------------
    @Override
    public String fullBody()
    {
        // TODO make this better
        return "The report named \"" + report.description() +
            "\" was generated.";
    }


    // ----------------------------------------------------------
    @Override
    public String shortBody()
    {
        return "The report named \"" + report.description() +
            "\" was generated.";
    }


    // ----------------------------------------------------------
    @Override
    public String title()
    {
        return "Report completed: " + report.description();
    }


    // ----------------------------------------------------------
    @Override
    public NSArray<User> users()
    {
        return new NSArray<User>(report.user());
    }


    //~ Static/instance variables .............................................

    private GeneratedReport report;
}
