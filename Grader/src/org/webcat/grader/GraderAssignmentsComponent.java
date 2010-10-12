/*==========================================================================*\
 |  $Id: GraderAssignmentsComponent.java,v 1.1 2010/10/12 02:40:32 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2010 Virginia Tech
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

import org.webcat.core.Course;
import org.webcat.core.CourseOffering;
import org.webcat.core.Semester;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

//-------------------------------------------------------------------------
/**
 * A subclass of {@link GraderAssignmentComponent} that allows for
 * multi-offering course/assignment selections.
 *
 * @author Stephen Edwards
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.1 $, $Date: 2010/10/12 02:40:32 $
 */
public class GraderAssignmentsComponent
    extends GraderAssignmentComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new GraderAssignmentsComponent object.
     *
     * @param context The context to use
     */
    public GraderAssignmentsComponent(WOContext context)
    {
        super(context);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * This method determines whether the current page requires the
     * user to have a selected AssignmentOffering.
     * The default implementation returns true, but is designed
     * to be overridden in subclasses.
     * @return True if the page requires a selected assignment offering.
     */
    @Override
    public boolean requiresAssignmentOffering()
    {
        return false;
    }


    // ----------------------------------------------------------
    /*
     * Get the selected course offering(s) for this page.
     * @return The list of course offerings (empty if none is selected).
     */
    public NSArray<CourseOffering> courseOfferings()
    {
        NSMutableArray<CourseOffering> courseOfferings =
            new NSMutableArray<CourseOffering>(10);
        if (!forceNavigatorSelection())
        {
            Course course = coreSelections().course();
            if (course == null)
            {
                // Just one offering selected
                CourseOffering co = coreSelections().courseOffering();
                if (co != null && co.isStaff(user()))
                {
                    courseOfferings.add(co);
                }
            }
            else
            {
                Semester semester = coreSelections().semester();
                // Find all offerings that this user can see
                NSArray<CourseOffering> candidates = (semester == null)
                    ? course.offerings()
                    : CourseOffering.offeringsForSemesterAndCourse(
                        localContext(), course, semester);

                for (CourseOffering co : candidates)
                {
                    if (co.isStaff(user()))
                    {
                        courseOfferings.add(co);
                    }
                }
            }
        }
        return courseOfferings;
    }


    // ----------------------------------------------------------
    /*
     * Get the selected assignment offering(s) for this page.
     * @return The list of assignment offerings (empty if none is selected).
     */
    public NSArray<AssignmentOffering> assignmentOfferings(
        NSArray<CourseOffering> courseOfferings)
    {
        NSMutableArray<AssignmentOffering> assignmentOfferings =
            new NSMutableArray<AssignmentOffering>(courseOfferings.size());
        Assignment assignment = prefs().assignment();
        if (assignment != null)
        {
            for (CourseOffering co : courseOfferings)
            {
                assignmentOfferings.addAll(
                    AssignmentOffering.objectsMatchingQualifier(
                        localContext(),
                        AssignmentOffering.assignment.eq(assignment).and(
                            AssignmentOffering.courseOffering.eq(co))));
            }
        }
        return assignmentOfferings;
    }


    //~ Instance/static variables ...............NSMutableArray................
}
