/*==========================================================================*\
 |  $Id: GraderKilledMessage.java,v 1.1 2010/04/19 15:23:04 aallowat Exp $
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

package net.sf.webcat.grader.messaging;

import java.io.File;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSMutableDictionary;
import net.sf.webcat.core.User;
import net.sf.webcat.core.messaging.Message;
import net.sf.webcat.core.messaging.UnexpectedExceptionMessage;
import net.sf.webcat.grader.Submission;

//-------------------------------------------------------------------------
/**
 * A message that is sent to the system administrator when a severe error
 * causes the entire grader to be killed.
 *
 * This is a separate subclass of UnexpectedExceptionMessage so that it can be
 * configured independently from other exception notifications if desired.
 *
 * @author Tony Allevato
 * @author  latest changes by: $Author: aallowat $
 * @version $Revision: 1.1 $ $Date: 2010/04/19 15:23:04 $
 */
public class GraderKilledMessage extends UnexpectedExceptionMessage
{
    //~ Constructors ..........................................................

    public GraderKilledMessage(Throwable e)
    {
        super(e, null, null, "Grader job queue processing halted.");
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Called by the subsystem init() to register the message.
     */
    public static void register()
    {
        Message.registerMessage(
                GraderKilledMessage.class,
                "Grader",
                "Grader Killed",
                false,
                User.WEBCAT_RW_PRIVILEGES);
    }
}
