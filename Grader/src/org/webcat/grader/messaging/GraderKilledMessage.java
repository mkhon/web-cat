/*==========================================================================*\
 |  $Id: GraderKilledMessage.java,v 1.1 2010/05/11 14:51:40 aallowat Exp $
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

package org.webcat.grader.messaging;

import java.io.File;
import org.webcat.core.User;
import org.webcat.core.messaging.Message;
import org.webcat.core.messaging.UnexpectedExceptionMessage;
import org.webcat.grader.Submission;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSMutableDictionary;

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
 * @version $Revision: 1.1 $ $Date: 2010/05/11 14:51:40 $
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
