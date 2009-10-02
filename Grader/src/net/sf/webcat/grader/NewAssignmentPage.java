/*==========================================================================*\
 |  $Id: NewAssignmentPage.java,v 1.1 2009/10/02 01:56:52 stedwar2 Exp $
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

package net.sf.webcat.grader;

import java.util.GregorianCalendar;
import net.sf.webcat.core.Course;
import net.sf.webcat.core.CourseOffering;
import net.sf.webcat.core.Semester;
import org.apache.log4j.Logger;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

//-------------------------------------------------------------------------
/**
 * Allows the user to create a new assignment + offering.
 *
 * @author Stephen Edwards
 * @author Last changed by $Author: stedwar2 $
 * @version $Revision: 1.1 $, $Date: 2009/10/02 01:56:52 $
 */
public class NewAssignmentPage
    extends GraderAssignmentComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     *
     * @param context The context to use
     */
    public NewAssignmentPage(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    public String aName;
    public String title;
    public String targetCourse;
    public boolean reuseOpen = false;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Adds to the response of the page
     *
     * @param response The response being built
     * @param context  The context of the request
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        if (coreSelections().course() == null
            && coreSelections().courseOffering() == null)
        {
            if (prefs().assignmentOffering() != null)
            {
                coreSelections().setCourseRelationship(
                    prefs().assignmentOffering().courseOffering().course());
            }
            else if (prefs().assignment() != null
                && prefs().assignment().offerings().count() > 0)
            {
                coreSelections().setCourseRelationship(
                    prefs().assignment().offerings().objectAtIndex(0)
                        .courseOffering().course());
            }
        }
        if (coreSelections().course() == null
            && coreSelections().courseOffering() == null)
        {
            targetCourse = "Please select a course ->";
        }
        else if (coreSelections().course() != null)
        {
            targetCourse = coreSelections().course().toString()
                + " (all offerings)";
            if (forAllSections == null)
            {
                forAllSections = Boolean.TRUE;
            }
        }
        else
        {
            targetCourse = coreSelections().courseOffering().compactName();
            if (forAllSections == null)
            {
                forAllSections = Boolean.FALSE;
            }
        }
        Semester semester = coreSelections().semester();
        if (semester == null)
        {
            targetCourse += ", all semesters";
        }
        else
        {
            targetCourse += ", " + semester;
        }
        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public boolean allowsAllOfferingsForCourse()
    {
        return true;
    }


    // ----------------------------------------------------------
    public boolean requiresAssignmentOffering()
    {
        // Want to show all offerings for this assignment.
        return false;
    }


    // ----------------------------------------------------------
    /**
     * Create a new assignment object as well as its associated assignment
     * offering objects, and then move on to an edit page.
     * @see net.sf.webcat.core.WCComponent#next()
     */
    public WOComponent next()
    {
        if (aName == null || aName.length() == 0)
        {
            error("Please enter a name for your assignment.");
            return null;
        }

        Semester semester = coreSelections().semester();
        Course course = coreSelections().course();
        NSArray<CourseOffering> offerings = null;
        if (course != null)
        {
            offerings = course.offerings();
            if (semester != null)
            {
                NSMutableArray<CourseOffering> fullOfferings =
                    offerings.mutableClone();
                for (int i = 0; i < fullOfferings.count(); i++)
                {
                    if (fullOfferings.objectAtIndex(i).semester() != semester)
                    {
                        fullOfferings.remove(i);
                        i--;
                    }
                }
                offerings = fullOfferings;
            }
        }
        else
        {
            offerings = new NSArray<CourseOffering>(
                coreSelections().courseOffering());
        }

        if (offerings == null || offerings.count() == 0)
        {
            error("Please select a course in which to create the assignment.");
            return null;
        }

        log.debug("creating new assignment");
        Assignment newAssignment = new Assignment();
        localContext().insertObject(newAssignment);
        newAssignment.setShortDescription(title);
        newAssignment.setAuthorRelationship(user());

        NSArray<AssignmentOffering> similar = AssignmentOffering
            .offeringsWithSimilarNames(
                localContext(),
                aName,
                course,
                1);
        if (similar.count() > 0)
        {
            newAssignment.setSubmissionProfile(
                similar.objectAtIndex(0).assignment().submissionProfile());
        }


        NSTimestamp common = newAssignment.commonOfferingsDueDate();
        AssignmentOffering firstOffering = null;

        for (CourseOffering offering: offerings)
        {
            log.debug("creating new assignment offering for " + offering);
            AssignmentOffering newOffering = new AssignmentOffering();
            if (firstOffering == null)
            {
                firstOffering = newOffering;
            }
            localContext().insertObject(newOffering);
            newOffering.setAssignmentRelationship(newAssignment);
            prefs().setAssignmentOfferingRelationship(newOffering);
            newOffering.setCourseOfferingRelationship(offering);
            configureNewAssignmentOffering(newOffering, common);
        }

        newAssignment.setName(aName);
        applyLocalChanges();
        prefs().setAssignmentRelationship(newAssignment);
        prefs().setAssignmentOfferingRelationship(firstOffering);
        applyLocalChanges();

        return super.next();
    }


    // ----------------------------------------------------------
    public void configureNewAssignmentOffering(
        AssignmentOffering newOffering, NSTimestamp commonDueDate)
    {
        NSTimestamp ts = new NSTimestamp();

        // first, look for any other assignments, and use their due date
        // as a default
        {
            AssignmentOffering other = null;
            NSArray<AssignmentOffering> others =
                prefs().assignmentOffering().assignment().offerings();
            for (AssignmentOffering ao : others)
            {
                if (ao != prefs().assignmentOffering())
                {
                    other = ao;
                    break;
                }
            }
            if (other == null)
            {
                if (commonDueDate != null)
                {
                    ts = commonDueDate;
                }
                else
                {
                    GregorianCalendar dueDateTime = new GregorianCalendar();
                    dueDateTime.setTime(ts
                        .timestampByAddingGregorianUnits(0, 0, 15, 18, 55, 00));
                    dueDateTime.set(GregorianCalendar.AM_PM,
                                    GregorianCalendar.PM );
                    dueDateTime.set(GregorianCalendar.HOUR   , 11);
                    dueDateTime.set(GregorianCalendar.MINUTE , 55);
                    dueDateTime.set(GregorianCalendar.SECOND , 0);

                    ts = new NSTimestamp(dueDateTime.getTime());
                }
            }
            else
            {
                ts = other.dueDate();
            }
        }

        // Next, look for assignments for this course with similar names,
        // and try to spot a trend
        String name1 = newOffering.assignment().name();
        if (name1 != null)
        {
            NSMutableArray<AssignmentOffering> others =
                AssignmentOffering.offeringsWithSimilarNames(
                    localContext(), name1,
                    coreSelections().courseOffering(), 2);
            if (others.count() > 1)
            {
                AssignmentOffering ao1 = others.objectAtIndex(0);
                GregorianCalendar ao1DateTime = new GregorianCalendar();
                ao1DateTime.setTime(ao1.dueDate());
                AssignmentOffering ao2 = others.objectAtIndex(1);
                GregorianCalendar ao2DateTime = new GregorianCalendar();
                ao2DateTime.setTime(ao2.dueDate());

                if (ao1DateTime.get(GregorianCalendar.HOUR_OF_DAY)
                    == ao2DateTime.get(GregorianCalendar.HOUR_OF_DAY)
                    && ao1DateTime.get(GregorianCalendar.MINUTE)
                    == ao2DateTime.get(GregorianCalendar.MINUTE)
                    )
                {
                    int days = ao1DateTime.get(GregorianCalendar.DAY_OF_YEAR)
                        - ao2DateTime.get(GregorianCalendar.DAY_OF_YEAR);
                    if (days < 0)
                    {
                        GregorianCalendar yearLen = new GregorianCalendar(
                            ao1DateTime.get(GregorianCalendar.YEAR),
                            0, 1);
                        yearLen.add(GregorianCalendar.DAY_OF_YEAR, -1);
                        days += yearLen.get(GregorianCalendar.DAY_OF_YEAR);
                    }

                    log.debug("day gap: " + days);
                    log.debug("old time: " + ao1DateTime);
                    ao1DateTime.add(GregorianCalendar.DAY_OF_YEAR, days);
                    GregorianCalendar today = new GregorianCalendar();
                    while (today.after(ao1DateTime))
                    {
                        ao1DateTime.add(GregorianCalendar.DAY_OF_YEAR, 7);
                    }
                    log.debug("new time: " + ao1DateTime);
                    ts = new NSTimestamp(ao1DateTime.getTime());
                }
                else
                {
                    ts = new NSTimestamp(
                        adjustTimeLike(ts, ao1DateTime).getTime());
                }
            }
            else if (others.count() > 0)
            {
                AssignmentOffering ao = others.objectAtIndex(0);
                GregorianCalendar aoDateTime = new GregorianCalendar();
                aoDateTime.setTime(ao.dueDate());
                ts = new NSTimestamp(
                    adjustTimeLike(ts, aoDateTime).getTime());
            }
        }

        newOffering.setDueDate(ts);
    }


    // ----------------------------------------------------------
    public boolean forAllSections()
    {
        return forAllSections != null && forAllSections.booleanValue();
    }


    // ----------------------------------------------------------
    public void setForAllSections(boolean value)
    {
        forAllSections = Boolean.valueOf(value);
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    private GregorianCalendar adjustTimeLike(
        NSTimestamp starting, GregorianCalendar similarTo)
    {
        GregorianCalendar result = new GregorianCalendar();
        result.setTime(starting);

        // First, copy the time and day of the week from the old
        // assignment
        result.set(GregorianCalendar.HOUR_OF_DAY,
                   similarTo.get( GregorianCalendar.HOUR_OF_DAY));
        result.set(GregorianCalendar.MINUTE,
                   similarTo.get( GregorianCalendar.MINUTE));
        result.set(GregorianCalendar.SECOND,
                   similarTo.get( GregorianCalendar.SECOND));
        result.set(GregorianCalendar.DAY_OF_WEEK,
                   similarTo.get( GregorianCalendar.DAY_OF_WEEK));

        // jump ahead by weeks until we're in the future
        GregorianCalendar today = new GregorianCalendar();
        while (today.after(result))
        {
            result.add(GregorianCalendar.DAY_OF_YEAR, 7);
        }

        return result;
    }


    //~ Instance/static variables .............................................

    private Boolean forAllSections = null;
    static Logger log = Logger.getLogger(NewAssignmentPage.class);
}
