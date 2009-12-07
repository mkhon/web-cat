/*==========================================================================*\
 |  $Id: PendingAssignmentOpinionsPage.java,v 1.1 2009/12/07 20:04:04 stedwar2 Exp $
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

package net.sf.webcat.opinions;

import org.apache.log4j.Logger;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import er.extensions.eof.ERXQ;
import net.sf.webcat.core.*;
import net.sf.webcat.grader.Assignment;
import net.sf.webcat.grader.AssignmentOffering;
import net.sf.webcat.grader.Submission;
import er.extensions.appserver.ERXDisplayGroup;

//-------------------------------------------------------------------------
/**
 * This page shows the user all those assignments that allow surveys
 * of student engagement and frustration.  Only assignments that (a)
 * track student opinions, (b) the student has submitted to, and
 * (c) the student has not yet filled out a survey for, will be shown.
 *
 * @author Stephen Edwards
 * @author Last changed by $Author: stedwar2 $
 * @version $Revision: 1.1 $, $Date: 2009/12/07 20:04:04 $
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

    public Assignment assignment;
    public ERXDisplayGroup<Assignment> assignmentDisplayGroup;
    public int index;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        log.debug("starting appendToResponse()");
        if (pendingOpinions == null)
        {
            EOFetchSpecification fspec = new EOFetchSpecification(
                Assignment.ENTITY_NAME,
                ERXQ.and(
                    ERXQ.equals(
                        Assignment.TRACK_OPINIONS_KEY,
                        true),
                    ERXQ.equals(
                        Assignment.OFFERINGS_KEY + "."
                        + AssignmentOffering.SUBMISSIONS_KEY + "."
                        + Submission.USER_KEY,
                        user())
                    ),
                    null);
            fspec.setUsesDistinct(true);
            Application.enableSQLLogging();

            // First, get all assignments that this user has submitted to
            // that accept opinion surveys
            pendingOpinions = Assignment.objectsWithFetchSpecification(
                localContext(), fspec);
            if (log.isDebugEnabled())
            {
                log.debug("assignments with tracking: "
                    + Assignment.objectsMatchingQualifier(localContext(),
                        ERXQ.equals(Assignment.TRACK_OPINIONS_KEY, true)));
                log.debug("assignments with user submissions: "
                    + Assignment.objectsMatchingQualifier(localContext(),
                        ERXQ.equals(Assignment.OFFERINGS_KEY + "."
                            + AssignmentOffering.SUBMISSIONS_KEY + "."
                            + Submission.USER_KEY, user())));
                log.debug("assignments pending feedback: " + pendingOpinions);
            }

            // We need to filter out those that the user has already
            // responded to.  But, since there is no relationship from
            // assignments to survey responses, since the surveys are in
            // a different data model, we have to filter out those that
            // have been responded to manually.

            // First, get all responses this user has completed.
            NSArray<SurveyResponse> userResponses =
                SurveyResponse.objectsForUser(localContext(), user());
            if (log.isDebugEnabled())
            {
                log.debug("responses from user: " + userResponses);
            }
            if (userResponses != null && userResponses.size() > 0)
            {
                // If there are any, convert the responses into an
                // array of the corresponding assignments
                NSMutableArray<Assignment> alreadyResponded =
                    new NSMutableArray<Assignment>(userResponses.size());
                for (int i = 0; i < userResponses.size(); i++)
                {
                    alreadyResponded.set(i, userResponses.get(i).assignment());
                }

                // Now, remove all assignments in the alreadyResponded array
                // from those in the pendingOpinions array
                NSMutableArray<Assignment> needingResponses =
                    pendingOpinions.mutableClone();
                needingResponses.removeObjectsInArray(alreadyResponded);
                pendingOpinions = needingResponses;
            }
            if (log.isDebugEnabled())
            {
                log.debug("final list: " + pendingOpinions);
            }
            assignmentDisplayGroup.setObjectArray(pendingOpinions);
            Application.disableSQLLogging();
        }
        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public Submission highestSubmission()
    {
        // TODO: fix me
        return null;
    }


    // ----------------------------------------------------------
    public Submission mostRecentSubmission()
    {
        // TODO: fix me
        return null;
    }


    // ----------------------------------------------------------
    public AssignmentOffering offeringWithDistribution()
    {
//        if (withDistribution == null || withDistribution.assignment() != assignment)
        // TODO: fix me
        return null;
    }


    // ----------------------------------------------------------
    public WOComponent surveyForAssignment()
    {
        OpinionsSurveyPage page = pageWithName(OpinionsSurveyPage.class);
        page.nextPage = this;
        page.assignment = assignment;
        return page;
    }


    //~ Instance/static variables .............................................

    private NSArray<Assignment> pendingOpinions;
    private Submission highest;
    private Submission mostRecent;
    private AssignmentOffering withDistribution;
    static Logger log = Logger.getLogger(PendingAssignmentOpinionsPage.class);
}
