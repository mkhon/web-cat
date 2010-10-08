/*==========================================================================*\
 |  $Id: ConfirmSubmissionPage.java,v 1.5 2010/10/08 14:48:02 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2010 Virginia Tech
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
import com.webobjects.foundation.*;
import org.apache.log4j.Logger;
import org.webcat.archives.ArchiveManager;
import org.webcat.archives.IArchiveEntry;
import org.webcat.core.*;
import org.webcat.core.messaging.UnexpectedExceptionMessage;

// -------------------------------------------------------------------------
/**
 * This class summarizes the student's submission and asks for
 * confirmation before making it "official".
 *
 * @author  Amit Kulkarni
 * @author  Latest changes by: $Author: stedwar2 $
 * @version $Revision: 1.5 $, $Date: 2010/10/08 14:48:02 $
 */
public class ConfirmSubmissionPage
    extends GraderSubmissionUploadComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * This is the default constructor
     *
     * @param context The page's context
     */
    public ConfirmSubmissionPage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    /** File object for the uploaded file */
    public IArchiveEntry file;
    /** User object for a partner in the submission */
    public User partnerInRepetition;
    /** index of the file in the repetition table */
    public int index;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * This function is used to provide a notion that the wizard remembers
     * the previous selection
     *
     * @param response The response being built
     * @param context  The context of the request
     */
    protected void beforeAppendToResponse(
        WOResponse response, WOContext context)
    {
        if ( !submissionInProcess().submissionInProcess()
            || !submissionInProcess().hasValidFileUpload() )
        {
            WOComponent prevPage = back();
            if ( prevPage != null )
            {
                log.debug( "skipping to previous page" );
                response.setContent(
                    prevPage.generateResponse().content() );
                // skip calling super.appendToResponse
                return;
            }
        }
        log.debug( "The submission number is "
            + submissionInProcess().submitNumber() );
        if ( submissionInProcess().uploadedFileList() == null )
        {
            // Initialize the list of files contained in this submission,
            // which is cached in the wizard state as a non-persistent
            // NSArray field.  This array is an array of ZipEntry objects.
            try
            {
                submissionInProcess().setUploadedFileList(
                    new NSArray<IArchiveEntry>(
                        ArchiveManager.getInstance().getContents(
                            submissionInProcess().uploadedFileName(),
                            submissionInProcess().uploadedFile().stream(),
                            submissionInProcess().uploadedFile().length()
                        )));
            }
            catch ( Exception e )
            {
                submissionInProcess().clearUpload();
                error(
                    "An error occurred while unpacking "
                    + "your submission.  The error has been "
                    + "reported to the administrator.  If you "
                    + "have uploaded the wrong file by accident, "
                    + "use the Back button to try again." );
                new UnexpectedExceptionMessage(e, context(), null,
                    "Exception unzipping submission file").send();
            }
        }
        else
        {
            log.debug( "file list has already been initialized" );
        }
        // preProcessSubmission();
        super.beforeAppendToResponse(response, context);
    }


    // ----------------------------------------------------------
    /**
     * Returns a string version of the file size for the currrent file.
     *
     * @return the file size as a string
     */
    public String fileSize()
    {
        return FileUtilities.fileSizeAsString( file.length() );
    }


    // ----------------------------------------------------------
    public boolean singleFile()
    {
        NSArray<IArchiveEntry> list = submissionInProcess().uploadedFileList();
        return list == null || list.count() <= 1;
    }


    // ----------------------------------------------------------
    public WOComponent next()
    {
        NSTimestamp submitTime = new NSTimestamp();
        NSTimestamp deadline = new NSTimestamp(
            prefs().assignmentOffering().dueDate().getTime()
            + prefs().assignmentOffering().assignment()
                  .submissionProfile().deadTimeDelta() );
        CourseOffering course = prefs().assignmentOffering().courseOffering();
        User primeUser = wcSession().primeUser().localInstance(localContext());
        if ( deadline.before( submitTime )
             && !course.isInstructor( primeUser )
             && !course.isGrader( primeUser ) )
        {
            error(
                "Unfortunately, the final deadline for this assignment "
                + "has passed.  No more submissions are being accepted." );
        }
        else
        {
            String msg = commitSubmission( context(), submitTime );
            if ( msg != null )
            {
                log.debug( "Submission error = " + msg );
                error( msg );
            }
        }

        if ( hasMessages() )
        {
            return null;
        }
        else
        {
            resetPrimeUser();
            return super.next();
        }
    }


    // ----------------------------------------------------------
    public void cancelLocalChanges()
    {
        clearSubmission();
        resetPrimeUser();
        super.cancelLocalChanges();
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( ConfirmSubmissionPage.class );
}
