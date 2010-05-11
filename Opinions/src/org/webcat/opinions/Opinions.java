/*==========================================================================*\
 |  $Id: Opinions.java,v 1.1 2010/05/11 14:51:45 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2008 Virginia Tech
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

package org.webcat.opinions;

import org.apache.log4j.Logger;
import org.webcat.core.Subsystem;
import org.webcat.jobqueue.QueueDescriptor;

//-------------------------------------------------------------------------
/**
 * This subsystem provides feedback/opinion surveys that give students
 * and staff a chance to express their opinions about how engaging and/or
 * frustrating a given assignment is.
 *
 * @author Stephen Edwards
 * @author Last changed by $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2010/05/11 14:51:45 $
 */
public class Opinions
    extends Subsystem
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     */
    public Opinions()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void init()
    {
        super.init();
        QueueDescriptor.registerQueue(SurveyReminderJob.ENTITY_NAME);
        // create the thread and start it
    }


    // ----------------------------------------------------------
//    private void competingRegistration2()
//    {
//        org.webcat.dbupdate.MySQLDatabase db =
//            new org.webcat.dbupdate.MySQLDatabase();
//        db.setConnectionInfoFromProperties(
//            Application.configurationProperties());
//        try
//        {
//            db.executeSQL("insert into TQueueDescriptor (OID, jobEntityName, "
//                + "newestEntryId, requiresExclusiveHostAccess) values("
//                + "'7', 'SurveyReminderJob', '1', '0')");
//        }
//        catch (SQLException e)
//        {
//            log.error("sql error:", e);
//        }
//    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger(Opinions.class);
}
