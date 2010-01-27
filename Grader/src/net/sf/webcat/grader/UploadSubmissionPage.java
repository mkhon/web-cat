/*==========================================================================*\
 |  $Id: UploadSubmissionPage.java,v 1.16 2010/01/27 01:01:58 stedwar2 Exp $
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
import net.sf.webcat.core.*;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * This wizard page summarizes past submissions and allows a student
 * to upload a program file for the current (new) submission.
 *
 * @author Stephen Edwards
 * @author Last changed by $Author: stedwar2 $
 * @version $Revision: 1.16 $, $Date: 2010/01/27 01:01:58 $
 */
public class UploadSubmissionPage
    extends GraderSubmissionUploadComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * This is the default constructor
     *
     * @param context The page's context
     */
    public UploadSubmissionPage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public WODisplayGroup submissionDisplayGroup;
    /** submission item in the worepetition */
    public Submission aSubmission;
    /** index in worepetition */
    public int index;
    /** true if there are previous submissions */
    public boolean hasPreviousSubmissions;
    public Boolean showStudentSelector;

    public WODisplayGroup      studentDisplayGroup;
    public User                student;
    public User                submitAsStudent;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Adds to the response of the page
     *
     * @param response The response being built
     * @param context  The context of the request
     */
    protected void beforeAppendToResponse(
        WOResponse response, WOContext context)
    {
        log.debug( "primeUser = " + wcSession().primeUser()
                   + ", localUser = " + user() );
        if (showStudentSelector == null)
        {
            NSDictionary<?, ?> config =
                wcSession().tabs.selectedDescendant().config();
            showStudentSelector = Boolean.valueOf(
                config != null
                && config.containsKey("showStudentSelector"));
        }
        if (showStudentSelector)
        {
            studentDisplayGroup.setMasterObject(
                prefs().assignmentOffering().courseOffering());
            if (submitAsStudent == null
                && studentDisplayGroup.displayedObjects().count() > 0)
            {
                submitAsStudent = (User)
                    studentDisplayGroup.displayedObjects().objectAtIndex(0);
            }
        }
        int currentSubNo = fillDisplayGroup(user());
        hasPreviousSubmissions = submissionDisplayGroup.displayedObjects()
            .count() > 0;

        Number maxSubmissions = prefs().assignmentOffering()
            .assignment().submissionProfile().maxSubmissionsRaw();
        okayToSubmit = ( maxSubmissions == null )
            || ( currentSubNo <= maxSubmissions.intValue() );

        if ( okayToSubmit )
        {
            startSubmission(currentSubNo, user());
        }

        if (prefs().assignmentOffering().dueDate() != null)
        {
            log.debug( "due = "
                       + prefs().assignmentOffering().dueDate().getTime() );
            log.debug( "grace = " +
                       prefs().assignmentOffering().assignment()
                           .submissionProfile().deadTimeDelta() );

            NSTimestamp deadline = new NSTimestamp(
                    prefs().assignmentOffering().dueDate().getTime()
                    + prefs().assignmentOffering().assignment()
                       .submissionProfile().deadTimeDelta() );
            log.debug( "time = " + deadline );
        }

        super.beforeAppendToResponse( response, context );
    }


    // ----------------------------------------------------------
    protected void afterAppendToResponse(WOResponse response, WOContext context)
    {
        super.afterAppendToResponse(response, context);
        oldBatchSize  = submissionDisplayGroup.numberOfObjectsPerBatch();
        oldBatchIndex = submissionDisplayGroup.currentBatchIndex();
        cachedUploadedFile     = submissionInProcess().uploadedFile();
        cachedUploadedFileName = submissionInProcess().uploadedFileName();
        cachedUploadedFileList = submissionInProcess().uploadedFileList();
    }


    // ----------------------------------------------------------
    /**
     * This method determines whether any embedded navigator will
     * automatically pop up to force a selection and page reload.
     * @return True if the navigator should start out by opening automatically.
     */
    public boolean forceNavigatorSelection()
    {
        boolean result = super.forceNavigatorSelection();

        if (!result)
        {
            // If the assignment is closed and the user is not allowed to
            // submit to it (i.e., not a grader or instructor), then force the
            // assignment offering selection to be null and pop open the
            // navigator.

            AssignmentOffering assnOff = prefs().assignmentOffering();

            if (!user().hasAdminPrivileges() && !assnOff.userCanSubmit(user()))
            {
                prefs().setAssignmentOfferingRelationship(null);
                result = true;
            }
        }

        return result;
    }


    // ----------------------------------------------------------
    /**
     * A predicate that indicates whether the user can proceed.
     * As a side-effect, it sets the error message if the user cannot
     * proceed.
     *
     * @return true if the user can proceed
     */
    public boolean okayToSubmit()
    {
        if ( !okayToSubmit )
        {
            error( "You have already made the maximum allowed "
                          + "number of submissions for this assignment." );
        }
        return okayToSubmit;
    }


    // ----------------------------------------------------------
    public boolean nextEnabled()
    {
        return okayToSubmit();
    }


    // ----------------------------------------------------------
    public WOComponent next()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "next():" );
            log.debug(" request = " + context().request() );
            log.debug(" form values = " + context().request().formValues() );
            log.debug(" multipart = "
                + context().request().isMultipartFormData() );
        }
        if (showStudentSelector && submitAsStudent == null)
        {
            error("Please select a student for this submission.");
            return null;
        }
        if ( okayToSubmit )
        {
            NSTimestamp deadline = new NSTimestamp(
                    prefs().assignmentOffering().dueDate().getTime()
                    + prefs().assignmentOffering().assignment()
                      .submissionProfile().deadTimeDelta() );
            log.debug( "deadline = " + deadline );
            log.debug( "now = " + ( new NSTimestamp() ) );
            CourseOffering course = prefs().assignmentOffering().courseOffering();
            User primeUser =
                wcSession().primeUser().localInstance(localContext());
            if ( deadline.before( new NSTimestamp() )
                 && !course.isInstructor( primeUser )
                 && !course.isGrader( primeUser ) )
            {
                error(
                    "Unfortunately, the final deadline for this assignment "
                    + "has passed.  No more submissions are being accepted." );
                return null;
            }
            boolean clearFileList = true;
            if ( !submissionInProcess().hasValidFileUpload() )
            {
                submissionInProcess().setUploadedFile( cachedUploadedFile );
                submissionInProcess().setUploadedFileName( cachedUploadedFileName );
                submissionInProcess().setUploadedFileList( cachedUploadedFileList );
                clearFileList = false;
            }
            if ( !submissionInProcess().hasValidFileUpload() )
            {
                error( "Please select a file to upload." );
                return null;
            }
            if ( clearFileList )
            {
                submissionInProcess().setUploadedFileList( null );
            }
            if ( submissionInProcess().uploadedFile().length() >
                 prefs().assignmentOffering().assignment()
                     .submissionProfile().effectiveMaxFileUploadSize() )
            {
                error(
                    "You file exceeds the file size limit for this assignment ("
                    + prefs().assignmentOffering().assignment()
                          .submissionProfile().effectiveMaxFileUploadSize()
                    + ").  Please choose a smaller file." );
                return null;
            }
/*
        if ( gstate.selectedAssignmentOffering().assignment()
                 .testDrivenAssignment().intValue() == 0 )
        {
            state.navigator().setCurrentPage(
                    state.navigator().currentPage() + 1 );
        }
*/
            if (showStudentSelector)
            {
                setLocalUser(submitAsStudent);
                int currentSubNo = fillDisplayGroup(user());
                submissionInProcess().submission().setSubmitNumber(
                    currentSubNo);
                submissionInProcess().submission().setUserRelationship(user());
            }
            return super.next();
        }
        else
        {
            // If we get here, an error message has already been set
            // in okayToSubmit(), so just refresh the page to show it.
            return null;
        }
    }


    // ----------------------------------------------------------
    /**
     * A boolean predicate that indicates that this assignment
     * has instructor-provided instructions to show to the student.
     *
     * @return true if there are instructions to show
     */
    public boolean hasInstructions()
    {
        String instructions = prefs().assignmentOffering()
            .assignment().fileUploadMessage();
        return instructions != null && !instructions.equals( "" );
    }


    // ----------------------------------------------------------
    /**
     * Returns a string version of the file size for the currently
     * uploaded file.
     *
     * @return the file size as a string
     */
    public String uploadedFileSize()
    {
        long size = 0L;
        NSData file = submissionInProcess().uploadedFile();
        if ( file != null )
        {
            size = file.length();
        }
        return Submission.fileSizeAsString( size );
    }


    // ----------------------------------------------------------
    public WOComponent defaultAction()
    {
        log.debug( "defaultAction()" );
        if ( oldBatchSize != submissionDisplayGroup.numberOfObjectsPerBatch()
             || oldBatchIndex != submissionDisplayGroup.currentBatchIndex() )
        {
            return null;
        }
        else
        {
            return super.defaultAction();
        }
    }


    // ----------------------------------------------------------
    public void cancelLocalChanges()
    {
        clearSubmission();
        resetPrimeUser();
        super.cancelLocalChanges();
    }


    // ----------------------------------------------------------
    public boolean allowsAllOfferingsForCourse()
    {
        return false;
    }


    // ----------------------------------------------------------
    public void takeValuesFromRequest( WORequest arg0, WOContext arg1 )
    {
        try
        {
            super.takeValuesFromRequest( arg0, arg1 );
        }
        catch ( Exception e )
        {
            // Ignore it
//            error( e.getMessage() );
//            Application.emailExceptionToAdmins( e, arg1,
//                "In UploadSubmissionPage:takeValuesFromRequest(), a post "
//                + "request without an attached file submission was received\n."
//                + "Browser info = \n"
//                + context().request().headerForKey("user-agent")
//                );
        }
    }


    // ----------------------------------------------------------
    public String permalink()
    {
        if (prefs().assignmentOffering() != null)
        {
            return prefs().assignmentOffering().permalink();
        }
        else
        {
            return null;
        }
    }


    // ----------------------------------------------------------
    public void setPermalink(String value)
    {
        // Do nothing.
    }


    // ----------------------------------------------------------
    private int fillDisplayGroup(User user)
    {
        NSArray<?> submissions = EOUtilities.objectsMatchingValues(
            localContext(),
            Submission.ENTITY_NAME,
            new NSDictionary<String, Object>(
                    new Object[] {  user,
                                    prefs().assignmentOffering()
                                 },
                    new String[] {  Submission.USER_KEY,
                                    Submission.ASSIGNMENT_OFFERING_KEY }
            )
        );
        submissionDisplayGroup.setObjectArray(submissions);
        int currentSubNo = submissions.count() + 1;
        for (int i = 0; i < submissions.count(); i++)
        {
            int sno = ( (Submission)submissions.objectAtIndex(i) )
                .submitNumber();
            if (sno >= currentSubNo)
            {
                currentSubNo = sno + 1;
            }
        }
        return currentSubNo;
    }


    //~ Instance/static variables .............................................

    /** Saves the state of the batch navigator to detect setting changes */
    protected int oldBatchSize;
    /** Saves the state of the batch navigator to detect setting changes */
    protected int oldBatchIndex;

    protected NSData  cachedUploadedFile;
    protected String  cachedUploadedFileName;
    protected NSArray cachedUploadedFileList;

    /** True if the user has not reached the maximum allowable submissions
     *  for this assignment and its okay to submit another time.
     */
    protected boolean okayToSubmit;

    static Logger log = Logger.getLogger( UploadSubmissionPage.class );
}
