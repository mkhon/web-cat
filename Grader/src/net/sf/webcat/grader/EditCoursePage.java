/*==========================================================================*\
 |  $Id: EditCoursePage.java,v 1.4 2008/01/13 00:12:36 stedwar2 Exp $
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
*  @version $Id: EditCoursePage.java,v 1.4 2008/01/13 00:12:36 stedwar2 Exp $
*/
public class EditCoursePage
    extends GraderCourseComponent
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
                Semester.objectsForFetchAll( wcSession().localContext() );
        }
        instructorDisplayGroup.setMasterObject( wcSession().courseOffering() );
        TADisplayGroup.setMasterObject( wcSession().courseOffering() );
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
        return user == wcSession().user();
    }


    // ----------------------------------------------------------
    /**
     * Remove the selected instructor.
     * @return always null
     */
    public WOComponent removeInstructor()
    {
        wcSession().courseOffering().removeFromInstructorsRelationship( user );
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Remove the selected TA.
     * @return always null
     */
    public WOComponent removeTA()
    {
        wcSession().courseOffering().removeFromTAsRelationship( user );
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
    /**
     * Find the dates of the earliest and latest submissions for
     * any assignment associated with this course.
     * @return null, to force a page refresh
     */
    public WOComponent computeSubmissionDateRange()
    {
        NSArray subs = Submission.objectsForEarliestForCourseOffering(
            wcSession().localContext(),
            wcSession().courseOffering());
        if (subs.count() > 0)
        {
            earliest = ((Submission)subs.objectAtIndex(0)).submitTime();
            subs = Submission.objectsForLatestForCourseOffering(
                wcSession().localContext(),
                wcSession().courseOffering());
            latest = ((Submission)subs.objectAtIndex(0)).submitTime();
        }
        earliestAndLatestComputed = true;
        return null;
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( EditCoursePage.class );
}
