/*==========================================================================*\
 |  $Id: PickCourseEnrolledPage.java,v 1.6 2008/02/08 20:01:38 stedwar2 Exp $
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
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.*;

import net.sf.webcat.core.*;

import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 *  This page presents a list of courses for a student to choose from.
 *
 *  @author  Stephen Edwards
 *  @version $Id: PickCourseEnrolledPage.java,v 1.6 2008/02/08 20:01:38 stedwar2 Exp $
 */
public class PickCourseEnrolledPage
    extends GraderComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new PickCourseTaughtPage object.
     *
     * @param context The context to use
     */
    public PickCourseEnrolledPage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public WODisplayGroup      courseDisplayGroup;
    public NSArray             coursesTAed;
    public NSArray             coursesTaught;
    public NSArray             coursesAdmined;
    public CourseOffering      courseOffering;
    public int                 index;
    public int                 selectedCourseIndex;
    public int                 selectedTAIndex;
    public int                 selectedInstructorIndex;
    public int                 selectedAdminIndex;
    public NSArray             semesters;
    public Semester            semester;
    public Semester            aSemester;

    public static final String SEMESTER_PREF_KEY = "semester";


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void awake()
    {
        super.awake();
        log.debug( "awake()" );
        selectedCourseIndex     = -1;
        selectedTAIndex         = -1;
        selectedInstructorIndex = -1;
        selectedAdminIndex      = -1;
    }


    // ----------------------------------------------------------
    public void appendToResponse( WOResponse response, WOContext context )
    {
        User user = wcSession().user();
        if ( semesters == null )
        {
            semesters =
                Semester.objectsForFetchAll( wcSession().localContext() );
            Object semesterPref =
                user.preferences().valueForKey( SEMESTER_PREF_KEY );
            if (semesterPref == null && semesters.count() > 0)
            {
                // Default to most recent semester, if no preference is set
                semester = (Semester)semesters.objectAtIndex(0);
            }
            else
            {
                semester = Semester.forId( wcSession().localContext(),
                    ERXValueUtilities.intValue( semesterPref ) );
            }
        }
        // Save selected semester
        user.preferences().takeValueForKey(
            semester == null ? ERXConstant.ZeroInteger : semester.id(),
            SEMESTER_PREF_KEY );
        user.savePreferences();

        courseDisplayGroup.setMasterObject( user );
        if (semester == null)
        {
            courseDisplayGroup.setQualifier( null );
            courseDisplayGroup.updateDisplayedObjects();
        }
        else
        {
            courseDisplayGroup.setQualifier( new EOKeyValueQualifier(
                CourseOffering.SEMESTER_KEY,
                EOQualifier.QualifierOperatorEqual,
                semester ));
            courseDisplayGroup.updateDisplayedObjects();
        }
        coursesTAed = user.TAForButNotStudent(semester);
        coursesTaught = user.instructorForButNotTAOrStudent(semester);
        coursesAdmined = user.adminForButNoOtherRelationships(semester);
        if ( log.isDebugEnabled() )
        {
            log.debug( "TA list = " + coursesTAed );
            log.debug( "instructor list = " + coursesTaught );
            log.debug( "admin list = " + coursesAdmined );
        }
        if ( courseDisplayGroup.displayedObjects().count() == 0
            && coursesTAed.count() == 0
            && coursesTaught.count() == 0
            && coursesAdmined.count() == 0 )
        {
            // There are no enrolled courses
            error( "Web-CAT has no record of your course enrollments "
                          + "for this semester." );
        }
        CourseOffering selectedCourse = coreSelections().courseOffering();
        if ( selectedCourse != null )
        {
            selectedCourseIndex =
                courseDisplayGroup.displayedObjects().indexOfIdenticalObject(
                        selectedCourse );
            if ( selectedCourseIndex == NSArray.NotFound )
            {
                selectedTAIndex = coursesTAed
                    .indexOfIdenticalObject( selectedCourse );
                if ( selectedTAIndex != NSArray.NotFound )
                {
                    selectedTAIndex +=
                        courseDisplayGroup.displayedObjects().count();
                }
            }
            if ( selectedCourseIndex == NSArray.NotFound
                 && selectedTAIndex == NSArray.NotFound )
            {
                selectedInstructorIndex = coursesTaught.indexOfIdenticalObject(
                        selectedCourse );
                if ( selectedInstructorIndex != NSArray.NotFound )
                {
                    selectedInstructorIndex +=
                        courseDisplayGroup.displayedObjects().count()
                        + coursesTAed.count();
                }
            }
            if ( selectedCourseIndex == NSArray.NotFound
                 && selectedTAIndex == NSArray.NotFound
                 && selectedInstructorIndex == NSArray.NotFound )
            {
                selectedAdminIndex = coursesAdmined.indexOfIdenticalObject(
                    selectedCourse );
                if ( selectedAdminIndex != NSArray.NotFound )
                {
                    selectedAdminIndex +=
                        courseDisplayGroup.displayedObjects().count()
                        + coursesTAed.count()
                        + coursesTaught.count();
                }
            }
            if ( selectedCourseIndex == NSArray.NotFound
                 && selectedTAIndex == NSArray.NotFound
                 && selectedInstructorIndex == NSArray.NotFound
                 && selectedAdminIndex == NSArray.NotFound )
            {
                coreSelections().setCourseOffering( null );
                selectedCourse = null;
            }
        }
        if ( selectedCourse == null )
        {
            if ( courseDisplayGroup.displayedObjects().count() > 0 )
            {
                selectedCourseIndex = 0;
                coreSelections().setCourseOffering(
                     (CourseOffering)courseDisplayGroup.displayedObjects().
                         objectAtIndex( selectedCourseIndex )
                );
            }
            else if ( coursesTAed.count() > 0 )
            {
                selectedTAIndex = courseDisplayGroup.displayedObjects().count();
                coreSelections().setCourseOffering(
                     (CourseOffering)coursesTAed.
                         objectAtIndex( 0 ) );
            }
            else if ( coursesTaught.count() > 0 )
            {
                selectedInstructorIndex =
                    courseDisplayGroup.displayedObjects().count()
                    + coursesTAed.count();
                coreSelections().setCourseOffering(
                     (CourseOffering)coursesTaught.objectAtIndex( 0 ) );
            }
            else if ( coursesAdmined.count() > 0 )
            {
                selectedInstructorIndex =
                    courseDisplayGroup.displayedObjects().count()
                    + coursesTAed.count()
                    + coursesTaught.count();
                coreSelections().setCourseOffering(
                     (CourseOffering)coursesAdmined.objectAtIndex( 0 ) );
            }
        }

        if ( log.isDebugEnabled() )
        {
            log.debug( "appendToResponse():" );
            log.debug(" selected 1 = " + selectedCourseIndex );
            log.debug(" selected 2 = " + selectedTAIndex );
            log.debug(" selected 3 = " + selectedInstructorIndex );
            log.debug(" selected 4 = " + selectedAdminIndex );
        }
        super.appendToResponse( response, context );
    }


    // ----------------------------------------------------------
    public WOComponent next()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "next():" );
            log.debug(" selected 1 = " + selectedCourseIndex );
            log.debug(" selected 2 = " + selectedTAIndex );
            log.debug(" selected 3 = " + selectedInstructorIndex );
            log.debug(" selected 4 = " + selectedAdminIndex );
        }
        if ( selectedCourseIndex >= 0 )
        {
            log.debug( "choosing enrolled course " + selectedCourseIndex );
            coreSelections().setCourseOffering(
                (CourseOffering)courseDisplayGroup.displayedObjects()
                    .objectAtIndex( selectedCourseIndex ) );
            return super.next();
        }
        else if ( selectedTAIndex >= 0 )
        {
            selectedTAIndex -= courseDisplayGroup.displayedObjects().count();
            log.debug( "choosing TAed course " + selectedTAIndex );
            coreSelections().setCourseOffering(
                (CourseOffering)coursesTAed
                    .objectAtIndex( selectedTAIndex ) );
            return super.next();
        }
        else if ( selectedInstructorIndex >= 0 )
        {
            selectedInstructorIndex -=
                courseDisplayGroup.displayedObjects().count();
            selectedInstructorIndex -= coursesTAed.count();
            log.debug( "choosing taught course " + selectedInstructorIndex );
            coreSelections().setCourseOffering(
                (CourseOffering)coursesTaught
                    .objectAtIndex( selectedInstructorIndex ) );
            return super.next();
        }
        else if ( selectedAdminIndex >= 0 )
        {
            selectedAdminIndex -=
                courseDisplayGroup.displayedObjects().count();
            selectedAdminIndex -= coursesTAed.count();
            selectedAdminIndex -= coursesTaught.count();
            log.debug( "choosing admin'ed course " + selectedInstructorIndex );
            coreSelections().setCourseOffering(
                (CourseOffering)coursesAdmined
                    .objectAtIndex( selectedAdminIndex ) );
            return super.next();
        }
        else
        {
            error( "You must choose a course to proceed." );
            return null;
        }
    }


    // ----------------------------------------------------------
    public boolean nextEnabled()
    {
        return
            ( courseDisplayGroup.displayedObjects().count() > 0
              || coursesTAed.count() > 0
              || coursesTaught.count() > 0
              || coursesAdmined.count() > 0 )
            &&  super.nextEnabled();
    }


    // ----------------------------------------------------------
    public WOComponent defaultAction()
    {
        // When semester list changes, make sure not to take the
        // default action, which is to click "next".
        return null;
    }


    // ----------------------------------------------------------
    public int index1()
    {
        return index;
    }


    // ----------------------------------------------------------
    public int index2()
    {
        return index1() + courseDisplayGroup.displayedObjects().count();
    }


    // ----------------------------------------------------------
    public int index3()
    {
        return index2() + coursesTAed.count();
    }


    // ----------------------------------------------------------
    public int index4()
    {
        return index3() + coursesTaught.count();
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( PickCourseTaughtPage.class );
}
