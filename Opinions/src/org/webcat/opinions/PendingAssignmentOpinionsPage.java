/*==========================================================================*\
 |  $Id: PendingAssignmentOpinionsPage.java,v 1.1 2010/05/11 14:51:45 aallowat Exp $
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

package org.webcat.opinions;

import org.apache.log4j.Logger;
import org.webcat.core.Application;
import org.webcat.core.CourseOffering;
import org.webcat.core.WCComponent;
import org.webcat.grader.Assignment;
import org.webcat.grader.AssignmentOffering;
import org.webcat.grader.Submission;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.eof.ERXQ;
import er.extensions.eof.ERXS;

//-------------------------------------------------------------------------
/**
 * This page shows the user all those assignments that allow surveys
 * of student engagement and frustration.  Only assignments that (a)
 * track student opinions, (b) the student has submitted to, and
 * (c) the student has not yet filled out a survey for, will be shown.
 *
 * @author Stephen Edwards
 * @author Last changed by $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2010/05/11 14:51:45 $
 */
public class PendingAssignmentOpinionsPage
    extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     *
     * @param context The context to use
     */
    public PendingAssignmentOpinionsPage(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    public AssignmentOffering assignmentOffering;
    public ERXDisplayGroup<Assignment> assignmentOfferingDisplayGroup;
    public int index;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        log.debug("starting appendToResponse()");
        if (pendingOpinions == null)
        {
            if (log.isDebugEnabled())
            {
                Application.enableSQLLogging();
            }

            // First, get all assignments that this user has submitted to
            // that accept opinion surveys
            pendingOpinions = AssignmentOffering.objectsMatchingQualifier(
                localContext(),
                AssignmentOffering.assignment
                    .dot(Assignment.trackOpinions).isTrue().and(
                    AssignmentOffering.submissions.dot(Submission.user)
                        .is(user()))).mutableClone();
            if (log.isDebugEnabled())
            {
                log.debug("assignments pending feedback: " + pendingOpinions);
            }

            if (user().teaching().count() > 0)
            {
                // Remove all assignments for which this user is the instructor
                NSArray<AssignmentOffering> instructorFor =
                    AssignmentOffering.objectsMatchingQualifier(
                        localContext(),
                        ERXQ.and(
                            AssignmentOffering.assignment
                                .dot(Assignment.trackOpinions).isTrue(),
                            AssignmentOffering.courseOffering
                                .dot(CourseOffering.instructors).is(user()),
                            AssignmentOffering.submissions
                                .dot(Submission.user).is(user())
                        ));
                pendingOpinions.removeObjectsInArray(instructorFor);
                if (log.isDebugEnabled())
                {
                    log.debug("user is instructor for: " + instructorFor);
                }
            }

            if (user().graderFor().count() > 0)
            {
                // Remove all assignments for which this user is the grader
                NSArray<AssignmentOffering> graderFor =
                    AssignmentOffering.objectsMatchingQualifier(
                        localContext(),
                        ERXQ.and(
                            AssignmentOffering.assignment
                                .dot(Assignment.trackOpinions).isTrue(),
                            AssignmentOffering.courseOffering
                                .dot(CourseOffering.graders).is(user()),
                            AssignmentOffering.submissions
                                .dot(Submission.user).is(user())
                        ));
                pendingOpinions.removeObjectsInArray(graderFor);
                if (log.isDebugEnabled())
                {
                    log.debug("user is grader for: " + graderFor);
                }
            }

            // We need to filter out those that the user has already
            // responded to.  But, since there is no relationship from
            // assignments to survey responses, since the surveys are in
            // a different data model, we have to filter out those that
            // have been responded to manually.

            // First, get all responses this user has completed.
            NSArray<SurveyResponse> userResponses =
                SurveyResponse.responsesForUser(localContext(), user());
            if (log.isDebugEnabled())
            {
                log.debug("responses from user: " + userResponses);
            }
            if (userResponses != null && userResponses.size() > 0)
            {
                // If there are any, convert the responses into an
                // array of the corresponding assignments
                NSMutableArray<AssignmentOffering> alreadyResponded =
                    new NSMutableArray<AssignmentOffering>(
                        userResponses.size());
                for (int i = 0; i < userResponses.size(); i++)
                {
                    alreadyResponded.add(
                        i, userResponses.get(i).assignmentOffering());
                }

                // Now, remove all assignments in the alreadyResponded array
                // from those in the pendingOpinions array
                pendingOpinions.removeObjectsInArray(alreadyResponded);
            }
            if (log.isDebugEnabled())
            {
                log.debug("final list: " + pendingOpinions);
            }
            assignmentOfferingDisplayGroup.setObjectArray(pendingOpinions);
            Application.disableSQLLogging();
        }
        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public Submission highestSubmission()
    {
        if (highest == null
            || highest.assignmentOffering() != assignmentOffering)
        {
            EOFetchSpecification fspec = new EOFetchSpecification(
                Submission.ENTITY_NAME,
                ERXQ.and(
                    Submission.user.is(user()),
                    Submission.assignmentOffering.is(assignmentOffering)
                    ),
                ERXS.descs(Submission.SUBMIT_NUMBER_KEY));
            fspec.setUsesDistinct(true);
            fspec.setFetchLimit(1);
            NSArray<Submission> result = Submission
                .objectsWithFetchSpecification(localContext(), fspec);
            if (result != null && result.count() > 0)
            {
                highest = result.objectAtIndex(0);
            }
        }
        return highest;
    }


    // ----------------------------------------------------------
    public WOComponent surveyForAssignment()
    {
        OpinionsSurveyPage page = pageWithName(OpinionsSurveyPage.class);
        page.nextPage = this;
        page.assignmentOffering = assignmentOffering;
        pendingOpinions = null;
        // page.submission = mostRecentSubmission();
        return page;
    }


    // ----------------------------------------------------------
    public String permalink()
    {
        return Application.configurationProperties().getProperty( "base.url" )
            + "?page=opinions";
    }


    //~ Instance/static variables .............................................

    private NSMutableArray<AssignmentOffering> pendingOpinions;
    private Submission highest;
    static Logger log = Logger.getLogger(PendingAssignmentOpinionsPage.class);
}
