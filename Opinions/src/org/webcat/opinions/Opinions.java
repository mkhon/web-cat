/*==========================================================================*\
 |  $Id: Opinions.java,v 1.2 2010/10/19 23:34:58 stedwar2 Exp $
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
import org.webcat.core.Application;
import org.webcat.core.CourseOffering;
import org.webcat.core.Semester;
import org.webcat.core.Subsystem;
import org.webcat.grader.Assignment;
import org.webcat.grader.AssignmentOffering;
import org.webcat.jobqueue.QueueDescriptor;
import org.webcat.opinions.messaging.SurveyReminderMessage;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;
import er.extensions.eof.ERXConstant;

//-------------------------------------------------------------------------
/**
 * This subsystem provides feedback/opinion surveys that give students
 * and staff a chance to express their opinions about how engaging and/or
 * frustrating a given assignment is.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.2 $, $Date: 2010/10/19 23:34:58 $
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

        // Register the job queue
        QueueDescriptor.registerQueue(SurveyReminderJob.ENTITY_NAME);

        // Register for notifications from assignment offerings that they
        // need a job created
//        NSNotificationCenter.defaultCenter().addObserver(
//            this,
//            new NSSelector<Void>(
//                "sendSurveyRemindersFor",
//                new Class<?>[] { NSNotification.class }),
//            AssignmentOffering.ENQUEUE_SURVEY_JOB,
//            null);

        // Register notification message
        SurveyReminderMessage.register();
    }


    // ----------------------------------------------------------
    public void start()
    {
        new SurveyReminderWorker().start();

        new SurveyReminderWatcher().start();
    }


    // ----------------------------------------------------------
//    public void sendSurveyRemindersFor(Number assignmentOfferingId)
//    {
//        EOEditingContext ec = Application.newPeerEditingContext();
//        try
//        {
//            ec.lock();
//            AssignmentOffering offering = AssignmentOffering.forId(
//                ec, assignmentOfferingId.intValue());
//            if (offering != null)
//            {
//                sendSurveyRemindersFor(offering);
//            }
//            else
//            {
//                log.error("sendSurveyRemindersFor(): cannot find assignment "
//                    + "offering for id " + assignmentOfferingId);
//            }
//        }
//        finally
//        {
//            ec.unlock();
//            Application.releasePeerEditingContext(ec);
//        }
//    }


    // ----------------------------------------------------------
//    public void sendSurveyRemindersFor(NSNotification notification)
//    {
//        sendSurveyRemindersFor((Number)notification.object());
//    }


    // ----------------------------------------------------------
    private static class SurveyReminderWatcher
        extends Thread
    {
        // ----------------------------------------------------------
        @Override
        public void run()
        {
            // repeat forever
            while (true)
            {
                EOEditingContext ec = Application.newPeerEditingContext();
                try
                {
                    ec.lock();

                    // Find all assignments that need notifications sent

                    // First, find current semester
                    Semester current = Semester.forDate(ec, new NSTimestamp());
                    if (current == null)
                    {
                        NSArray<Semester> allSemesters =
                            Semester.allObjectsOrderedByStartDate(ec);
                        if (allSemesters.count() > 0)
                        {
                            current = allSemesters.get(0);
                        }
                    }

                    // Then, find all assignment offerings for current semester
                    // that want surveys
                    NSArray<AssignmentOffering> offerings =
                        AssignmentOffering.objectsMatchingQualifier(ec,
                            AssignmentOffering.courseOffering
                                .dot(CourseOffering.semester).eq(current).and(
                                    AssignmentOffering.assignment
                                        .dot(Assignment.trackOpinions)
                                        .isTrue()));

                    if (log.isDebugEnabled())
                    {
                        log.debug("SurveyReminderWatcher: found "
                            + offerings.count() + " offerings to check");
                        for (AssignmentOffering offering : offerings)
                        {
                            log.debug("\t" + offering);
                        }
                    }

                    // The call sendSurveyRemindersFor() on each one
                    for (AssignmentOffering offering : offerings)
                    {
                        sendSurveyRemindersFor(offering);
                    }
                }
                catch (Exception e)
                {
                    log.error("SurveyReminderWatcher: Exception occurred", e);
                }
                finally
                {
                    ec.unlock();
                    Application.releasePeerEditingContext(ec);
                }

                try
                {
                    sleep(//1000
                        // Should be 24 hours:
                        1000 * 60 * 60 * 24
                    );
                }
                catch (InterruptedException e)
                {
                    // ignore
                }
            }
        }


        // ----------------------------------------------------------
        private void sendSurveyRemindersFor(AssignmentOffering offering)
        {
            EOEditingContext ec = offering.editingContext();
            SurveyNotificationMarker marker = SurveyNotificationMarker
                .firstObjectMatchingQualifier(
                    ec,
                    SurveyNotificationMarker.assignmentOffering.eq(offering),
                    null);

            if (marker == null)
            {
                // if no marker, then enqueue a reminder job
                SurveyReminderJob job = SurveyReminderJob.create(
                    ec,
                    new NSTimestamp(),
                    false,
                    true);
                job.setAssignmentOfferingRelationship(offering);
                if (log.isDebugEnabled())
                {
                    log.debug("SurveyReminderWatcher: creating job for "
                        + offering);
                }
                ec.saveChanges();
            }
            else if (log.isDebugEnabled())
            {
                log.debug("SurveyReminderWatcher: marker already exists for "
                    + offering);
            }
        }
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger(Opinions.class);
}
