/*==========================================================================*\
 |  $Id: CoreNavigator.java,v 1.7 2009/10/02 01:53:47 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2009 Virginia Tech
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

import org.apache.log4j.Logger;
import net.sf.webcat.core.CoreNavigatorObjects;
import net.sf.webcat.core.Course;
import net.sf.webcat.core.CourseOffering;
import net.sf.webcat.core.EntityUtils;
import net.sf.webcat.core.INavigatorObject;
import net.sf.webcat.core.Semester;
import net.sf.webcat.core.WCComponent;
import net.sf.webcat.ui.util.ComponentIDGenerator;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import er.extensions.eof.ERXQ;

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
 * @author  latest changes by: $Author: stedwar2 $
 * @version $Revision: 1.7 $ $Date: 2009/10/02 01:53:47 $
 */
public class CoreNavigator
    extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create a new object.
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

    public Boolean allowsAllSemesters;
    public Boolean allowsAllOfferingsForCourse;
    public Boolean startOpen;

    public ComponentIDGenerator idFor;

    public static final String COURSE_OFFERING_SET_KEY = "courseOfferingSet";


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    public void appendToResponse(WOResponse response, WOContext context)
    {
        log.debug("entering appendToResponse()");

        idFor = new ComponentIDGenerator(this);

        updateSemesters();

        super.appendToResponse(response, context);
        log.debug("leaving appendToResponse()");
    }


    // ----------------------------------------------------------
    public void awake()
    {
        log.debug("entering awake()");
        if (selectionsParent == null)
        {
            selectionsParent = findNearestAncestor(WCCourseComponent.class);
            if (selectionsParent == null)
            {
                throw new IllegalStateException("CoreNavigator can only be "
                    + "embedded inside a WCCourseComponent page");
            }

            if (allowsAllSemesters == null)
            {
                allowsAllSemesters = Boolean.valueOf(
                    selectionsParent.allowsAllSemesters());
            }
            if (allowsAllOfferingsForCourse == null)
            {
                allowsAllOfferingsForCourse = Boolean.valueOf(
                    selectionsParent.allowsAllOfferingsForCourse());
            }
            if (startOpen == null)
            {
                startOpen = Boolean.valueOf(
                    selectionsParent.forceNavigatorSelection());
            }

            Semester semester = selectionsParent.coreSelections().semester();
            if (semester != null)
            {
                selectedSemester =
                    new CoreNavigatorObjects.SingleSemester(semester);
            }

            // Handle course preference
            Course course = selectionsParent.coreSelections().course();
            if (course != null && allowsAllOfferingsForCourse)
            {
                wantOfferingsForCourse = course;
            }
            else
            {
                CourseOffering co =
                    selectionsParent.coreSelections().courseOffering();
                if (co != null)
                {
                    selectedCourseOffering =
                        new CoreNavigatorObjects.SingleCourseOffering(co);
                    wantOfferingsForCourse = null;
                }
            }
        }
        log.debug("selected semester = " + selectedSemester);
        log.debug("selected course = " + selectedCourseOffering);
        log.debug("parent = " + parent().getClass().getName());
        super.awake();
        log.debug("leaving awake()");
    }


    // ----------------------------------------------------------
    /**
     * Updates the list of available semesters, followed by course offerings
     * and assignments.
     *
     * @return the result is ignored
     */
    public WOActionResults updateSemesters()
    {
        log.debug("updateSemesters()");
        semesters = new NSMutableArray<INavigatorObject>();

        if (allowsAllSemesters)
        {
            semesters.addObject(new CoreNavigatorObjects.AllSemesters(
                    localContext()));
        }

        NSArray<Semester> sems = Semester.objectsForFetchAll(localContext());

        for (Semester sem : sems)
        {
            semesters.addObject(new CoreNavigatorObjects.SingleSemester(sem));
        }

        if (selectedSemester == null)
        {
            selectedSemester = semesters.objectAtIndex(0);
        }

        if (log.isDebugEnabled())
        {
            log.debug("semesters = " + semesters);
            log.debug("selected semester = " + selectedSemester);
        }
        return updateCourseOfferings();
    }


    // ----------------------------------------------------------
    /**
     * Updates the list of course offerings, followed by assignments.
     *
     * @return the result is ignored
     */
    @SuppressWarnings("unchecked")
    public WOActionResults updateCourseOfferings()
    {
        log.debug("updateCourseOfferings()");
        courseOfferings = new NSMutableArray<INavigatorObject>();

        NSArray<Semester> sems = (NSArray<Semester>)
            selectedSemester.representedObjects();

        NSArray<CourseOffering> offerings;

        // First, get all the course offerings we're interested in based on
        // the user's access level and selections in the UI. This may include
        // more offerings that we really need.

        if (user().hasAdminPrivileges() && includeAdminAccess())
        {
            offerings = EOUtilities.objectsForEntityNamed(
                localContext(), CourseOffering.ENTITY_NAME);
        }
        else if (user().hasTAPrivileges() && includeWhatImTeaching())
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

        INavigatorObject oldSelection = selectedCourseOffering;
        selectedCourseOffering = null;

        for (int i = 0; i < offerings.count(); i++)
        {
            CourseOffering offering = offerings.objectAtIndex(i);
            if (lastCourse == null || !lastCourse.equals(offering.course()))
            {
                if (allowsAllOfferingsForCourse)
                {
                    int mismatchIndex = i + 1;
                    for (; mismatchIndex < offerings.count(); mismatchIndex++)
                    {
                        // Find first offering later in list from a
                        // different course
                        if (!offering.course().equals(
                            offerings.objectAtIndex(mismatchIndex).course()))
                        {
                            break;
                        }
                    }

                    // If there was more than one offering for
                    // the initial course
                    if (mismatchIndex > i + 1)
                    {
                        CourseOffering exemplar = null;
                        NSMutableArray<CourseOffering> subset =
                            new NSMutableArray<CourseOffering>(
                                mismatchIndex - i);
                        for (int k = i; k < mismatchIndex; k++)
                        {
                            exemplar = offerings.objectAtIndex(k);
                            subset.add(exemplar);
                        }

                        INavigatorObject courseWrapper =
                            new CoreNavigatorObjects
                            .CourseOfferingSet(subset);
                        courseOfferings.addObject(courseWrapper);
                        if (wantOfferingsForCourse != null
                            && exemplar != null
                            && wantOfferingsForCourse == exemplar.course())
                        {
                            selectedCourseOffering = courseWrapper;
                        }
                    }
                }

                lastCourse = offering.course();
            }

            INavigatorObject newOffering =
                new CoreNavigatorObjects.SingleCourseOffering(offering);
            courseOfferings.addObject(newOffering);
            if ((oldSelection != null && oldSelection.equals(newOffering))
                || (selectedCourseOffering == null
                    && wantOfferingsForCourse != null
                    && wantOfferingsForCourse == offering.course()))
            {
                selectedCourseOffering = newOffering;
            }
        }

        if (selectedCourseOffering == null && courseOfferings.count() > 0)
        {
            selectedCourseOffering = courseOfferings.objectAtIndex(0);
        }

        if (log.isDebugEnabled())
        {
            log.debug("courseOfferings = " + courseOfferings);
            log.debug("selected course offering = " + selectedCourseOffering);
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
        selectionsParent.flushNavigatorDerivedData();

        // Save semester choice
        if (selectedSemester != null)
        {
            NSArray<?> sems = selectedSemester.representedObjects();
            if (sems != null && sems.count() == 1)
            {
                selectionsParent.coreSelections().setSemester(
                    (Semester)sems.objectAtIndex(0));
            }
            else if (allowsAllSemesters)
            {
                selectionsParent.coreSelections().setSemester(null);
            }
        }

        // Save course or course offering choice
        if (selectedCourseOffering != null)
        {
            NSArray<?> offerings = selectedCourseOffering.representedObjects();
            if (offerings != null && offerings.count() > 0)
            {
                CourseOffering co = (CourseOffering)offerings.objectAtIndex(0);
                boolean allOfferings =
                    offerings.count() > 1 && allowsAllOfferingsForCourse;
                if (allOfferings)
                {
                    selectionsParent.coreSelections()
                    .setCourseRelationship(co.course());
                    CourseOffering co2 =
                        selectionsParent.coreSelections().courseOffering();
                    if (co2 != null && co2.course() != co.course())
                    {
                        selectionsParent.coreSelections()
                            .setCourseOfferingRelationship(null);
                    }
                }
                else
                {
                    selectionsParent.coreSelections()
                        .setCourseRelationship(null);
                    selectionsParent.coreSelections()
                        .setCourseOfferingRelationship(co);
                }
            }
        }

        return null;
    }


    // ----------------------------------------------------------
    public void setIncludeWhatImTeaching(boolean includeWhatImTeaching)
    {
        selectionsParent.coreSelections().setIncludeWhatImTeaching(
            includeWhatImTeaching);
    }


    // ----------------------------------------------------------
    public boolean includeWhatImTeaching()
    {
        return selectionsParent.coreSelections().includeWhatImTeaching();
    }


    // ----------------------------------------------------------
    public void setIncludeAdminAccess(boolean includeAdminAccess)
    {
        selectionsParent.coreSelections().setIncludeAdminAccess(
            includeAdminAccess);
    }


    // ----------------------------------------------------------
    public boolean includeAdminAccess()
    {
        return selectionsParent.coreSelections().includeAdminAccess();
    }


    // ----------------------------------------------------------
    /**
     * Searches up the WOComponent hierarchy searching for the nearest
     * enclosing ancestor that is an instance of the specified target
     * class.
     * @param targetClass The type of ancestor to look for
     * @return The identified ancestor, if one is found, or null if
     * none matching the target class is found.
     */
    @SuppressWarnings("unchecked")
    protected <T> T findNearestAncestor(Class<T> targetClass)
    {
        WOComponent ancestor = parent();
        while (ancestor != null
            && !(targetClass.isAssignableFrom(ancestor.getClass())))
        {
            ancestor = ancestor.parent();
        }
        return (T)ancestor;
    }


    //~ Static/instance variables .............................................

    protected WCCourseComponent selectionsParent = null;
    private Course wantOfferingsForCourse;
    static Logger log = Logger.getLogger(CoreNavigator.class);
}
