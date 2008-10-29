/*==========================================================================*\
 |  $Id: StudentsForAssignmentPage.java,v 1.12 2008/10/29 14:15:21 aallowat Exp $
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

package net.sf.webcat.grader;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;
import er.extensions.foundation.ERXArrayUtilities;
import java.util.HashMap;
import java.util.Map;
import net.sf.webcat.core.*;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * Show an overview of class grades for an assignment, and allow the user
 * to download them in spreadsheet form or edit them one at a time.
 *
 * @author Stephen Edwards
 * @version $Id: StudentsForAssignmentPage.java,v 1.12 2008/10/29 14:15:21 aallowat Exp $
 */
public class StudentsForAssignmentPage
    extends GraderAssignmentComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Default constructor.
     * @param context The page's context
     */
    public StudentsForAssignmentPage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public WODisplayGroup submissionDisplayGroup;
    /** Submission in the worepetition */
    public Submission  aSubmission;
    public Submission  partnerSubmission;
    /** index in the worepetition */
    public int         index;

    /** Value of the corresponding checkbox on the page. */
    public boolean omitStaff           = true;
    public boolean useBlackboardFormat = true;
    public double  highScore           = 0.0;
    public double  lowScore            = 0.0;
    public double  avgScore            = 0.0;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse( WOResponse response, WOContext context )
    {
        if ( maxSubmission == null )
        {
            maxSubmission = new HashMap();
        }
        else
        {
            maxSubmission.clear();
        }
        NSMutableArray students =
            coreSelections().courseOffering().students().mutableClone();
        ERXArrayUtilities.addObjectsFromArrayWithoutDuplicates(
            students,
            coreSelections().courseOffering().instructors() );
        ERXArrayUtilities.addObjectsFromArrayWithoutDuplicates(
            students,
            coreSelections().courseOffering().graders() );
        NSMutableArray submissions = new NSMutableArray();
        highScore = 0.0;
        lowScore  = 0.0;
        avgScore  = 0.0;
        if ( students != null )
        {
            for ( int i = 0; i < students.count(); i++ )
            {
                User student = (User)students.objectAtIndex( i );
                log.debug( "checking " + student.userName() );

                Submission thisSubmission = null;
                Submission mostRecentSubmission = null;
                Submission gradedSubmission = null;
                // Find the submission
                NSArray thisSubmissionSet = EOUtilities.objectsMatchingValues(
                        localContext(),
                        Submission.ENTITY_NAME,
                        new NSDictionary(
                            new Object[] {
                                student,
                                prefs().assignmentOffering()
                            },
                            new Object[] {
                                Submission.USER_KEY,
                                Submission.ASSIGNMENT_OFFERING_KEY
                            }
                        )
                    );
                log.debug( "searching for submissions" );
                for ( int j = 0; j < thisSubmissionSet.count(); j++ )
                {
                    Submission sub =
                        (Submission)thisSubmissionSet.objectAtIndex( j );
                    if ( mostRecentSubmission == null
                         || sub.submitNumber() >
                             mostRecentSubmission.submitNumber() )
                    {
                        mostRecentSubmission = sub;
                    }
                    log.debug( "\tsub #" + sub.submitNumber() );
                    if ( sub.result() != null && !sub.partnerLink() )
                    {
                        if ( thisSubmission == null )
                        {
                            thisSubmission = sub;
                        }
                        else if ( sub.submitNumberRaw() != null )
                        {
                            int num = sub.submitNumber();
                            if ( num > thisSubmission.submitNumber() )
                            {
                                thisSubmission = sub;
                            }
                        }
                        if ( sub.result().status() != Status.TO_DO )
                        {
                            if ( gradedSubmission == null )
                            {
                                gradedSubmission = sub;
                            }
                            else if ( sub.submitNumberRaw() != null )
                            {
                                int num = sub.submitNumber();
                                if ( num > gradedSubmission.submitNumber() )
                                {
                                    gradedSubmission = sub;
                                }
                            }
                        }
                    }
                }
                if ( mostRecentSubmission != null )
                {
                    maxSubmission.put( mostRecentSubmission.user(),
                                       mostRecentSubmission );
                }
                if ( gradedSubmission != null )
                {
                    thisSubmission = gradedSubmission;
                }
                if ( thisSubmission != null )
                {
                    log.debug( "submission found = "
                                    + thisSubmission.submitNumber() );
                    double score = thisSubmission.result().finalScore();
                    if ( submissions.count() == 0 )
                    {
                        highScore = score;
                        lowScore  = score;
                    }
                    else
                    {
                        if ( score > highScore )
                        {
                            highScore = score;
                        }
                        if ( score < lowScore )
                        {
                            lowScore = score;
                        }
                    }
                    avgScore += score;
                    submissions.addObject( thisSubmission );
                }
                else
                {
                    log.debug( "no submission found" );
                }
            }
        }
        if ( submissions.count() > 0 )
        {
            avgScore /= submissions.count();
        }
        submissionDisplayGroup.setObjectArray( submissions );
        super.appendToResponse( response, context );
    }


    // ----------------------------------------------------------
    public WOComponent editSubmissionScore()
    {
        WOComponent destination = null;
        if ( !hasMessages() )
        {
            if ( aSubmission == null )
            {
                log.error( "editSubmissionScore(): null submission!" );
            }
            else if ( aSubmission.result() == null )
            {
                log.error( "editSubmissionScore(): null submission result!" );
                log.error( "student = " + aSubmission.user().userName() );
            }
            prefs().setSubmissionRelationship( aSubmission );
            destination = super.next();
//            destination = (WCComponent)pageWithName(
//                            GradeStudentSubmissionPage.class.getName() );
//            destination.nextPage = this;
        }
        return destination;
    }


    // ----------------------------------------------------------
    /**
     * Marks all the submissions shown that have been partially graded as
     * being completed, sending e-mail notifications as necessary.
     * @return null to force this page to reload
     */
    public WOComponent markAsCompleteActionOk()
    {
        NSArray submissions = submissionDisplayGroup.allObjects();
        for ( int i = 0; i < submissions.count(); i++ )
        {
            Submission sub = (Submission)submissions.objectAtIndex( i );
            if ( sub.result().status() == Status.UNFINISHED )
            {
                sub.result().setStatus( Status.CHECK );
                if (applyLocalChanges())
                {
                    sub.emailNotificationToStudent(
                        "has been updated by the course staff" );
                }
            }
        }
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Marks all the submissions shown that have been partially graded as
     * being completed, sending e-mail notifications as necessary.
     * @return null to force this page to reload
     */
    public WOComponent markAsComplete()
    {
        ConfirmPage confirmPage =
            (ConfirmPage)pageWithName( ConfirmPage.class.getName() );
        confirmPage.nextPage       = this;
        confirmPage.message        =
            "You are about to mark all <b>partially graded</b> submissions "
            + "as now complete.  Submissions that have no remarks or manual "
            + "scoring information will not be affected.  All students who "
            + "are affected will be receive an e-mail notification.";
        confirmPage.actionReceiver = this;
        confirmPage.actionOk       = "markAsCompleteActionOk";
        confirmPage.setTitle( "Confirm Grading Is Complete" );
        return confirmPage;
    }


    // ----------------------------------------------------------
    public boolean hasTAScore()
    {
        return aSubmission.result().taScoreRaw() != null;
    }


    // ----------------------------------------------------------
    public boolean hasPartners()
    {
        return aSubmission.result().submissions().count() > 1;
    }


    // ----------------------------------------------------------
    public boolean hasMultiplePartners()
    {
        return aSubmission.result().submissions().count() > 2;
    }


    // ----------------------------------------------------------
    public boolean isAPartner()
    {
        return partnerSubmission.user() != aSubmission.user();
    }


    // ----------------------------------------------------------
    public boolean morePartners()
    {
        NSArray submissions = aSubmission.result().submissions();
        Submission lastSubmission = (Submission)submissions
            .objectAtIndex( submissions.count() - 1 );
        if ( lastSubmission == aSubmission )
        {
            lastSubmission = (Submission)submissions
            .objectAtIndex( submissions.count() - 2 );
        }
        return partnerSubmission != lastSubmission;
    }


    // ----------------------------------------------------------
    public boolean isMostRecentSubmission()
    {
        return aSubmission ==
            (Submission)maxSubmission.get( aSubmission.user() );
    }


    // ----------------------------------------------------------
    public int mostRecentSubmissionNo()
    {
        return ( (Submission)maxSubmission.get( aSubmission.user() ) )
            .submitNumber();
    }


    //~ Instance/static variables .............................................

    private Map maxSubmission;
    static Logger log = Logger.getLogger( StudentsForAssignmentPage.class );
}
