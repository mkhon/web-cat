/*==========================================================================*\
 |  $Id: EditCoursePage.java,v 1.6 2008/02/25 06:23:27 stedwar2 Exp $
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
import com.webobjects.foundation.*;

import net.sf.webcat.core.*;

import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
* Represents a standard Web-CAT page that has not yet been implemented
* (is "to be defined").
*
*  @author Stephen Edwards
*  @version $Id: EditCoursePage.java,v 1.6 2008/02/25 06:23:27 stedwar2 Exp $
*/
public class EditCoursePage
    extends GraderCourseEditComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new TBDPage object.
     *
     * @param context The context to use
     */
    public EditCoursePage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public WODisplayGroup      courseDisplayGroup;
    public WODisplayGroup      instructorDisplayGroup;
    public WODisplayGroup      TADisplayGroup;
    public Course              course;
    public User                user;
    public int                 index;
    public NSArray             semesters;
    public Semester            aSemester;
    public NSTimestamp         earliest;
    public NSTimestamp         latest;
    public boolean             earliestAndLatestComputed;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse( WOResponse arg0, WOContext arg1 )
    {
        if ( semesters == null )
        {
            semesters =
                Semester.objectsForFetchAll( localContext() );
        }
        instructorDisplayGroup.setMasterObject( courseOffering() );
        TADisplayGroup.setMasterObject( courseOffering() );
        super.appendToResponse( arg0, arg1 );
    }


    // ----------------------------------------------------------
    public WOComponent defaultAction()
    {
        // apply();
        // return super.defaultAction();

        // When semester list changes, make sure not to take the
        // default action, which is to click "next".
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Used to filter out the current user from some functions.
     * @return true if the user we are iterating over is the same as
     *     the currently logged in user
     */
    public boolean matchesUser()
    {
        return user == user();
    }


    // ----------------------------------------------------------
    /**
     * Remove the selected instructor.
     * @return always null
     */
    public WOComponent removeInstructor()
    {
        courseOffering().removeFromInstructorsRelationship( user );
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Remove the selected TA.
     * @return always null
     */
    public WOComponent removeTA()
    {
        courseOffering().removeFromTAsRelationship( user );
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Add a new instructor.
     * @return the add instructor page
     */
    public WOComponent addInstructor()
    {
        EditStaffPage addPage = (EditStaffPage)pageWithName(
            EditStaffPage.class.getName() );
        addPage.editInstructors = true;
        addPage.nextPage = this;
        return addPage;
    }


    // ----------------------------------------------------------
    /**
     * Add a new TA.
     * @return the add TA page
     */
    public WOComponent addTA()
    {
        EditStaffPage addPage = (EditStaffPage)pageWithName(
            EditStaffPage.class.getName() );
        addPage.editInstructors = false;
        addPage.nextPage = this;
        return addPage;
    }


    // ----------------------------------------------------------
    public WOComponent deleteActionOk()
    {
        if (!applyLocalChanges()) return null;
        CourseOffering thisOffering = courseOffering();
        setCourseOffering(null);
        coreSelections().setCourseOfferingRelationship( null );
        localContext().deleteObject(thisOffering);
        return finish();
    }


    // ----------------------------------------------------------
    public WOComponent delete()
    {
        ConfirmPage confirmPage = null;
        confirmPage =
            (ConfirmPage)pageWithName( ConfirmPage.class.getName() );
        confirmPage.nextPage       = this;
        confirmPage.message        =
            "This action will <b>delete the course offering</b>. "
            + "This action cannot be undone.</p>";
        confirmPage.actionReceiver = this;
        confirmPage.actionOk       = "deleteActionOk";
        confirmPage.setTitle( "Confirm Delete Request" );
        return confirmPage;
    }


    // ----------------------------------------------------------
    /**
     * Find the dates of the earliest and latest submissions for
     * any assignment associated with this course.
     * @return null, to force a page refresh
     */
    public WOComponent computeSubmissionDateRange()
    {
        NSArray subs = Submission.objectsForEarliestForCourseOffering(
            localContext(), courseOffering());
        if (subs.count() > 0)
        {
            earliest = ((Submission)subs.objectAtIndex(0)).submitTime();
            subs = Submission.objectsForLatestForCourseOffering(
                localContext(), courseOffering());
            latest = ((Submission)subs.objectAtIndex(0)).submitTime();
        }
        earliestAndLatestComputed = true;
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Extracts course offering identification from the given startup
     * parameters.  Note that this is copied directly from
     * {@link GraderCourseComponent}, but can't be inherited because of
     * MI restrictions.
     * @param params A dictionary of form values to decode
     * @return True if successful, false if the parameter is missing
     */
    public boolean startWith( NSDictionary params )
    {
        boolean result = false;
        String crn = stringValueForKey( params, CourseOffering.CRN_KEY );
        if ( crn != null )
        {
            result = startWith( CourseOffering
                .offeringForCrn( localContext(), crn ) );
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Sets the relevant course and course offering properties for this
     * session.  Note that this is copied directly from
     * {@link GraderCourseComponent}, but can't be inherited because of
     * MI restrictions.
     * @param offering the course offering to use for generating settings
     * @return True if successful, false if the course offering is not valid
     */
    protected boolean startWith( CourseOffering offering )
    {
        boolean result = false;
        User sessionUser = user();
        if ( offering != null
             && ( sessionUser.enrolledIn().contains(  offering )
                  || offering.isInstructor( sessionUser )
                  || offering.isTA( sessionUser ) ) )
        {
            result = true;
            coreSelections().setCourseRelationship( offering.course() );
            coreSelections().setCourseOfferingRelationship( offering );
        }
        return result;
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( EditCoursePage.class );
}
