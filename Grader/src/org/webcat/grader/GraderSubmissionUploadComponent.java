/*==========================================================================*\
 |  $Id: GraderSubmissionUploadComponent.java,v 1.1 2010/05/11 14:51:40 aallowat Exp $
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

package org.webcat.grader;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;
import java.io.*;
import org.apache.log4j.Logger;
import org.webcat.core.*;
import org.webcat.core.messaging.UnexpectedExceptionMessage;

//-------------------------------------------------------------------------
/**
 *  A {@link GraderAssignmentComponent} that adds support for a
 *  {@link SubmissionInProcess} state object.
 *
 *  @author  Stephen Edwards
 *  @version $Id: GraderSubmissionUploadComponent.java,v 1.1 2010/05/11 14:51:40 aallowat Exp $
 */
public class GraderSubmissionUploadComponent
    extends GraderAssignmentComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     *
     * @param context The context to use
     */
    public GraderSubmissionUploadComponent( WOContext context )
    {
        super( context );
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Access the user's current submission in progress.
     * @return the submission in progress for this page
     */
    public SubmissionInProcess submissionInProcess()
    {
        if (sip == null)
        {
            sip = (SubmissionInProcess)transientState().valueForKey(KEY);
            if (sip == null)
            {
                sip = new SubmissionInProcess();
                transientState().takeValueForKey(sip, KEY);
            }
        }
        return sip;
    }


    // ----------------------------------------------------------
    /**
     * Creates a fresh (but not saved or committed) submission
     * object and binds it to the submission key.
     * @param submitNumber the number of the new submission
     * @param user the person making the submission
     */
    public void startSubmission( int submitNumber, User user )
    {
        Submission submission = new Submission();
        localContext().insertObject( submission );
        submission.setSubmitNumber( submitNumber );
        submission.setUserRelationship( user );
        log.debug( "startSubmission( " + submitNumber + ", " + user + " )" );
        submissionInProcess().setSubmission( submission );
    }


    // ----------------------------------------------------------
    /**
     * Enters the current submission into the editing context,
     * establishes all the necessary relationships, and saves the
     * uploaded file to disk.
     *
     * @param  context    the context of the request
     * @param  submitTime the time to record for this submission
     * @return a string error message, or null if there were no errors
     */
    public String commitSubmission( WOContext context,
                                    NSTimestamp submitTime )
    {
        String errorMessage = null;
        log.debug( "committing submission" );
        Submission submission = submissionInProcess().submission();
        String uploadedFileName = submissionInProcess().uploadedFileName();
        submission.setSubmitTime( submitTime );
        submission.setFileName( uploadedFileName );
        // wcSession().localContext().insertObject( submission );
        //      ec.saveChanges();
        submission.setAssignmentOfferingRelationship(
            prefs().assignmentOffering() );
        prefs().assignmentOffering().addToSubmissionsRelationship( submission );
        log.debug( "Uploaded file name: " + uploadedFileName );

        // First, make the necessary directory
        try
        {
            File dirFile = new File( submission.dirName() );
            dirFile.mkdirs();
        }
        catch ( Exception e )
        {
            // Security exception
            new UnexpectedExceptionMessage(e, context, null,
                    "Exception creating submission directory").send();
            localContext().deleteObject( submission );
            prefs().setSubmissionRelationship( null );
            submissionInProcess().setSubmission( null );
            applyLocalChanges();
            return "A file error occurred while saving your "
                   + "submission.  The error has been reported "
                   + "to the administrator.  Please try your "
                   + "submission again later once the problem "
                   + "has been corrected.";
        }

        // Next, write out the file
        try
        {
            File outFile = submission.file();
            log.debug( "Local file name: " + outFile.getPath() );
            FileOutputStream out = new FileOutputStream( outFile );
            submissionInProcess().uploadedFile().writeToStream( out );
            out.close();
        }
        catch ( Exception e )
        {
            // Do something with the exception
            new UnexpectedExceptionMessage(e, context, null,
                    "Exception uploading submission file").send();
            localContext().deleteObject( submission );
            prefs().setSubmissionRelationship( null );
            submissionInProcess().setSubmission( null );
            applyLocalChanges();
            return "A file error occurred while saving your "
                   + "submission.  The error has been reported "
                   + "to the administrator.  Please try your "
                   + "submission again later once the problem "
                   + "has been corrected.";
        }

        // Clear out older jobs
        try
        {
            NSArray oldJobs = EOUtilities.objectsMatchingValues(
                    localContext(),
                    EnqueuedJob.ENTITY_NAME,
                    new NSDictionary(
                        new Object[] {  user(),
                                        submission.assignmentOffering()    },
                        new Object[] { EnqueuedJob.USER_KEY,
                                       EnqueuedJob.ASSIGNMENT_OFFERING_KEY }
                ) );
            for ( int i = 0; i < oldJobs.count(); i++ )
            {
                EnqueuedJob job = (EnqueuedJob)oldJobs.objectAtIndex( i );
                job.setDiscarded( true );
            }
        }
        catch ( Exception e )
        {
            // ignore it
        }

        // Queue it up for the grader
        EnqueuedJob job = new EnqueuedJob();
//      job.setSubmission( submission );
        localContext().insertObject( job );
        job.setSubmissionRelationship( submission );
        job.setQueueTime( new NSTimestamp() );
        applyLocalChanges();
        prefs().setSubmissionRelationship(submission);

        Grader.getInstance().graderQueue().enqueue( null );

        submissionInProcess().clearUpload();
        submissionInProcess().setSubmission(null);

        return errorMessage;
    }


    // ----------------------------------------------------------
    /**
     * Erases the submission in progress and nulls out the corresponding
     * data members.
     */
    public void clearSubmission()
    {
        if ( submissionInProcess().submissionInProcess() )
        {
            Submission submission = submissionInProcess().submission();
            if ( submission != null && submission.result() == null )
            {
                localContext().deleteObject( submission );
                prefs().setSubmissionRelationship( null );
            }
            submissionInProcess().setSubmission(null);
        }
    }


    // ----------------------------------------------------------
    /**
     * Cancels any editing in progress.  Typically called when pressing
     * a cancel button or using a tab to transfer to a different page.
     */
    public void cancelLocalChanges()
    {
        clearSubmission();
        super.cancelLocalChanges();
    }


    //~ Instance/static variables .............................................

    private SubmissionInProcess sip;
    private static final String KEY = SubmissionInProcess.class.getName();

    static Logger log = Logger.getLogger(GraderSubmissionUploadComponent.class);
}
