/*==========================================================================*\
 |  $Id: CoursesAndAssignments.java,v 1.2 2008/06/19 00:20:17 stedwar2 Exp $
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

package net.sf.webcat.webapi;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.ERXArrayUtilities;

import net.sf.webcat.core.CourseOffering;
import net.sf.webcat.core.Semester;
import net.sf.webcat.grader.AssignmentOffering;

//-------------------------------------------------------------------------
/**
 * XML Response page for webapi/coursesAndAssignments requests.
 *
 * @author Stephen Edwards
 * @version $Id: CoursesAndAssignments.java,v 1.2 2008/06/19 00:20:17 stedwar2 Exp $
 */
public class CoursesAndAssignments
    extends XmlResponsePage
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new page.
     *
     * @param context The page's context
     */
    public CoursesAndAssignments(WOContext context)
    {
        super(context);
    }


    //~ KVC Properties ........................................................

    public NSArray<Semester>           semesters;
    public Semester                    aSemester;

    public NSArray<CourseOffering>     courseOfferings;
    public CourseOffering              aCourseOffering;

    public AssignmentOffering          anAssignmentOffering;


    //~ KVC Properties ........................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        // Look up the semesters
        semesters = Semester.objectsForFetchAll(session().sessionContext());

        // Calculate the courses this user can work with
        courseOfferings =
            ERXArrayUtilities.arrayByAddingObjectsFromArrayWithoutDuplicates(
                session().user().teaching(),
                session().user().TAFor());

        // Finally, generate the response
        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve all the assignment offerings associated with the current
     * course offering stored in aCourseOffering.
     * @return the list of assignment offerings for this course offering
     */
    public NSArray<AssignmentOffering> assignmentOfferings()
    {
        return AssignmentOffering.objectsForCourseOffering(
            session().sessionContext(), aCourseOffering);
    }
}
