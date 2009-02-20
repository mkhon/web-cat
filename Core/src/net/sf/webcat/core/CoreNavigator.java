/*==========================================================================*\
 |  $Id: CoreNavigator.java,v 1.1 2009/02/20 02:27:21 aallowat Exp $
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

package net.sf.webcat.core;

import net.sf.webcat.core.CoreNavigatorObjects;
import net.sf.webcat.core.Course;
import net.sf.webcat.core.CourseOffering;
import net.sf.webcat.core.EntityUtils;
import net.sf.webcat.core.INavigatorObject;
import net.sf.webcat.core.Semester;
import net.sf.webcat.core.WCComponent;
import net.sf.webcat.ui.util.ComponentIDGenerator;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;
import er.extensions.eof.ERXQ;
import er.extensions.foundation.ERXArrayUtilities;

//--------------------------------------------------------------------------
/**
 * The popup course selector that serves as the basis for the Web-CAT core
 * subsystem navigation scheme.
 * 
 * <h2>Bindings</h2>
 * <dl>
 * <dt>allowsAllSemesters</dt>
 * <dd>A boolean value that adds an option to the semester drop-down that
 * allows the user to select "All" semesters. If false, the user can only
 * select a single semester. Defaults to true.</dd>
 * <dt>allowsAllOfferingsForCourse</dt>
 * <dd>A boolean value that adds an option for each course in the course
 * drop-down that allows the user to select "All" offerings for that course. If
 * false, the user may only select a single offering for a single course.
 * Defaults to true.</dd>
 * <dt>includeAdminAccess</dt>
 * <dd>A boolean value indicating whether the course drop-down should include
 * courses that the user is not teaching or enrolled in but does have admin
 * access for. If the user is an administrator, he can change this in the user
 * interface. Defaults to false.</dd>
 * <dt>includeWhatImTeaching</dt>
 * <dd>A boolean value indicating whether the course drop-down should include
 * courses that the user is teaching. If the user has TA privileges or higher,
 * he can change this in the user interface. Defaults to true.</dd>
 * </dl>
 * 
 * @author Tony Allevato
 * @version $Id: CoreNavigator.java,v 1.1 2009/02/20 02:27:21 aallowat Exp $
 */
public class CoreNavigator extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * TODO: real description
     * 
     * @param context
     */
    public CoreNavigator(WOContext context)
    {
        super(context);
    }
    
    
    //~ KVC attributes (must be public) .......................................
    
    public NSMutableArray<INavigatorObject> courseOfferings;
    public INavigatorObject courseOfferingInRepetition;
    public INavigatorObject selectedCourseOffering;

    public NSMutableArray<INavigatorObject> semesters;
    public INavigatorObject semesterInRepetition;
    public INavigatorObject selectedSemester;
    
    public boolean allowsAllSemesters = true;
    public boolean allowsAllOfferingsForCourse = true;

    public boolean includeWhatImTeaching = true;
    public boolean includeAdminAccess = false;
    
    public ComponentIDGenerator idFor;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    public void appendToResponse(WOResponse response, WOContext context)
    {
        idFor = new ComponentIDGenerator(this);

        // TODO Grab current selections from session and set the values of
        // selectedCourseOffering and selectedSemester to reflect that

        updateSemesters();

        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    /**
     * Updates the list of available semesters, followed by course offerings.
     * 
     * @return the result is ignored
     */
    public WOActionResults updateSemesters()
    {
        semesters = new NSMutableArray<INavigatorObject>();

        if (allowsAllSemesters)
        {
            semesters.addObject(new CoreNavigatorObjects.AllSemesters(
                    localContext()));
        }

        NSArray<Semester> sems = EOUtilities.objectsForEntityNamed(
                localContext(), Semester.ENTITY_NAME);

        for (Semester sem : sems)
        {
            semesters.addObject(new CoreNavigatorObjects.SingleSemester(sem));
        }
        
        if (selectedSemester == null)
        {
            selectedSemester = semesters.objectAtIndex(0);
        }
        
        return updateCourseOfferings();
    }


    // ----------------------------------------------------------
    /**
     * Updates the list of course offerings.
     * 
     * @return the result is ignored
     */
    public WOActionResults updateCourseOfferings()
    {
        courseOfferings = new NSMutableArray<INavigatorObject>();

        NSArray<Semester> sems = (NSArray<Semester>)
            selectedSemester.representedObjects();

        NSArray<CourseOffering> offerings;

        // First, get all the course offerings we're interested in based on
        // the user's access level and selections in the UI. This may include
        // more offerings that we really need.

        if (user().hasAdminPrivileges() && includeAdminAccess)
        {
            offerings = EOUtilities.objectsForEntityNamed(localContext(),
                    CourseOffering.ENTITY_NAME);
        }
        else if (user().hasTAPrivileges() && includeWhatImTeaching)
        {
            NSMutableArray<CourseOffering> temp =
                new NSMutableArray<CourseOffering>();
            
            temp.addObjectsFromArray(user().staffFor());
            temp.addObjectsFromArray(user().enrolledIn());
            
            offerings = temp;
        }
        else
        {
            offerings = user().enrolledIn();
        }

        // Next, filter the course offerings to include only those that occur
        // during the semester(s) that the user has selected.

        offerings = ERXQ.filtered(offerings, ERXQ.in("semester", sems));

        // Now sort the course offerings by course department and number.
        
        offerings = EOSortOrdering.sortedArrayUsingKeyOrderArray(offerings,
                EntityUtils.sortOrderingsForEntityNamed(
                        CourseOffering.ENTITY_NAME));

        // Add each course offering to the list, also adding an "All" option
        // before a set of courses if the user has access to every offering of
        // a course and if that option is selected.

        Course lastCourse = null;

        for (CourseOffering offering : offerings)
        {
            if (lastCourse == null || !lastCourse.equals(offering.course()))
            {
                if (allowsAllOfferingsForCourse &&
                        userHasAccessToAllOfferingsForCourse(offering.course()))
                {
                    INavigatorObject courseWrapper =
                        new CoreNavigatorObjects.OfferingsForSemestersAndCourse(
                                localContext(), sems, offering.course());
                    
                    courseOfferings.addObject(courseWrapper);
                }

                lastCourse = offering.course();
            }

            courseOfferings.addObject(
                    new CoreNavigatorObjects.SingleCourseOffering(
                            offering));
        }
        
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Invoked when the OK button in the dialog is pressed.
     * 
     * @return null to reload the current page
     */
    public WOActionResults okPressed()
    {
        // TODO store the user's current semester, course, and assignment
        // selection in the session or wherever the nav-state is being
        // persisted
        
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Returns a value indicating whether the current user has access to all of
     * the offerings of a particular course.
     * 
     * @param course the course
     * 
     * @return true if the user has access to all of the offerings; otherwise,
     *     false
     */
    private boolean userHasAccessToAllOfferingsForCourse(Course course)
    {
        if (user().hasAdminPrivileges())
        {
            return true;
        }

        NSArray<CourseOffering> offerings = course.offerings();
        
        for (CourseOffering offering : offerings)
        {
            if (!offering.isGrader(user()) && !offering.isInstructor(user()))
            {
                return false;
            }
        }
        
        return true;
    }
}
