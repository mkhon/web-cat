/*==========================================================================*\
 |  $Id: SubmitResponse.java,v 1.3 2008/03/12 07:32:13 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU General Public License as published by
 |  the Free Software Foundation; either version 2 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU General Public License
 |  along with Web-CAT; if not, write to the Free Software
 |  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 |
 |  Project manager: Stephen Edwards <edwards@cs.vt.edu>
 |  Virginia Tech CS Dept, 660 McBryde Hall (0106), Blacksburg, VA 24061 USA
\*==========================================================================*/

package net.sf.webcat.grader;

import com.webobjects.appserver.*;
import net.sf.webcat.core.*;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 *  This is the response page for BlueJ submission actions.  It is produced
 *  by {@link Grader#handleDirectAction(WORequest,Session,WOContext)}
 *  in response to a BlueJ submitter direct action transaction.
 *
 *  @author Stephen Edwards
 *  @version $Id: SubmitResponse.java,v 1.3 2008/03/12 07:32:13 stedwar2 Exp $
 */
public class SubmitResponse
    extends GraderSubmissionUploadComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new SubmitResponse object.
     * @param context the context for the request
     */
    public SubmitResponse( WOContext context )
    {
        super( context );
        log.debug( "constructor" );
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Check whether there is a non-empty error message.
     * @return true if an error has occurred
     */
    public boolean error()
    {
        log.debug( "error() = " + ( ( message != null )
                                    ? "true" : "false" ) );
        return message != null;
    }


    // ----------------------------------------------------------
    /**
     * Check whether the current submission has been paused by
     * the grading queue because of a submission-specific failure.
     *
     * @return true if this submission has been paused/deferred
     */
    public boolean gradingPaused()
    {
        if ( prefs() == null || prefs().submission() == null )
            return false;
        EnqueuedJob job = prefs().submission().enqueuedJob();
        return job != null  &&  job.paused();
    }


    // ----------------------------------------------------------
    /**
     * Check to see if Web-CAT is accepting submissions for this
     * assignment.
     *
     * @return true if submissions are closed.
     */
    public boolean notAcceptingSubmissions()
    {
        boolean result = assignmentClosed;
        if ( result )
        {
            if ( message == null )
            {
                message = "This assignment is not open for submission.";
            }
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Returns the URL for the direct action to view the results.
     * @return the URL as a string
     */
    public String resultsURL()
    {
        String dest = Application.completeURLWithRequestHandlerKey(
                context(),
                "wa",
                "report",
                "wosid=" + sessionID,
                false,
                0
            );
        log.debug( "link = " + dest );
        return dest;
    }


    //~ Instance/static variables .............................................

    public String  message;
    public String  sessionID;
    public boolean criticalError = false;
    public boolean assignmentClosed = false;

    static Logger log = Logger.getLogger( SubmitResponse.class );
}
