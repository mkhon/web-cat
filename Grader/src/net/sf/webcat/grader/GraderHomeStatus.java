/*==========================================================================*\
 |  $Id: GraderHomeStatus.java,v 1.1 2006/02/19 19:15:19 stedwar2 Exp $
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
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.*;
import er.extensions.ERXConstant;

import net.sf.webcat.core.*;

import org.apache.log4j.*;

// -------------------------------------------------------------------------
/**
 *  Generates the grader subsystem's rows in the system status block.
 *
 *  @author  Stephen Edwards
 *  @version $Id: GraderHomeStatus.java,v 1.1 2006/02/19 19:15:19 stedwar2 Exp $
 */
public class GraderHomeStatus
    extends GraderComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new GraderSystemStatusRows object.
     *
     * @param context The page's context
     */
    public GraderHomeStatus( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public WODisplayGroup     enqueuedJobGroup;
    public EnqueuedJob        job;
    public WODisplayGroup     assignmentGroup;
    public WODisplayGroup     upcomingAssignmentsGroup;
    public AssignmentOffering assignment;
    public int                index;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Adds to the response of the page
     * 
     * @param response The response being built
     * @param context  The context of the request
     */
    public void appendToResponse( WOResponse response, WOContext context )
    {
        log.debug( "starting appendToResponse()" );
        enqueuedJobGroup.queryBindings().setObjectForKey(
                wcSession().user(),
                "user"
            );
        enqueuedJobGroup.fetch();

        currentTime = new NSTimestamp();
        NSMutableArray qualifiers = new NSMutableArray();
        qualifiers.addObject( new EOKeyValueQualifier(
                AssignmentOffering.AVAILABLE_FROM_KEY, 
                EOQualifier.QualifierOperatorLessThan, 
                currentTime
            ) );
//        assignmentGroup.setQualifier(
//                        new EOAndQualifier( qualifiers ) );
//        log.debug( "qualifier = " + assignmentGroup.qualifier() );
//        assignmentGroup.fetch();
//        log.debug( "results = " + assignmentGroup.displayedObjects() );
        qualifiers.addObject( new EOKeyValueQualifier( 
                AssignmentOffering.PUBLISH_KEY, 
                EOQualifier.QualifierOperatorEqual,
                ERXConstant.integerForInt( 1 )
            ) );
        qualifiers.addObject( new EOKeyValueQualifier( 
                AssignmentOffering.COURSE_OFFERING_STUDENTS_KEY, 
                EOQualifier.QualifierOperatorContains,
                wcSession().user()
            ) );
//        assignmentGroup.setQualifier(
//                        new EOAndQualifier( qualifiers ) );
//        log.debug( "qualifier = " + assignmentGroup.qualifier() );
//        assignmentGroup.fetch();
//        log.debug( "results = " + assignmentGroup.displayedObjects() );
        qualifiers = new NSMutableArray( new EOAndQualifier( qualifiers ) );
        qualifiers.addObject( new EOKeyValueQualifier( 
                AssignmentOffering.COURSE_OFFERING_INSTRUCTORS_KEY, 
                EOQualifier.QualifierOperatorContains,
                wcSession().user()
            ) );
        qualifiers.addObject( new EOKeyValueQualifier( 
                AssignmentOffering.COURSE_OFFERING_TAS_KEY, 
                EOQualifier.QualifierOperatorContains,
                wcSession().user()
            ) );
//        assignmentGroup.setQualifier(
//                        new EOOrQualifier( qualifiers ) );
//        log.debug( "qualifier = " + assignmentGroup.qualifier() );
//        assignmentGroup.fetch();
//        log.debug( "results = " + assignmentGroup.displayedObjects() );
        qualifiers = new NSMutableArray( new EOOrQualifier( qualifiers ) );
        EOQualifier deadlineQualifier = new EOKeyValueQualifier(
            AssignmentOffering.LATE_DEADLINE_KEY, 
            EOQualifier.QualifierOperatorGreaterThan,
            currentTime
            );
        qualifiers.addObject( deadlineQualifier );
        EOQualifier assignmentQualifier = new EOAndQualifier( qualifiers );
        assignmentGroup.setQualifier( assignmentQualifier );
        assignmentGroup.fetch();
        qualifiers.removeAllObjects();
        qualifiers.addObject( deadlineQualifier );
        qualifiers.addObject( new EONotQualifier( assignmentQualifier ) );
        upcomingAssignmentsGroup.setQualifier(
            new EOAndQualifier( qualifiers ) );
        upcomingAssignmentsGroup.fetch();
        super.appendToResponse( response, context );
    }


    // ----------------------------------------------------------
    /**
     * View results for the most recent submission to the selected assignments.
     * 
     * @return the most recent results page
     */
    public Number mostRecentScore()
    {
        SubmissionResult subResult = assignment.mostRecentSubmissionResultFor(
            wcSession().user() );
        return ( subResult == null )
            ? null
            : new Double( subResult.graphableScore() );
    }


    // ----------------------------------------------------------
    /**
     * Check whether the selected assignment is past the due date.
     * 
     * @return true if any submissions to this assignment will be counted
     *         as late
     */
    public boolean assignmentIsLate()
    {
        return assignment.dueDate().before( currentTime );
    }


    // ----------------------------------------------------------
    /**
     * Check whether the user can edit the selected assignment.
     * 
     * @return true if the user can edit the assignment
     */
    public boolean canEditAssignment()
    {
        return assignment.courseOffering().isInstructor( wcSession().user() );
    }


    // ----------------------------------------------------------
    /**
     * Check whether the user can edit the selected assignment.
     * 
     * @return true if the user can edit the assignment
     */
    public boolean canGradeAssignment()
    {
        boolean result =
            assignment.courseOffering().isInstructor( wcSession().user() )
            || assignment.courseOffering().isTA( wcSession().user() );
        log.debug( "can grade = " + result );
        return result;
    }


    // ----------------------------------------------------------
    /**
     * An action to go to the submission page for a given assignment.
     * 
     * @return the submission page for the selected assignment
     */
    public WOComponent submitAssignment()
    {
        wcSession().setCourseOfferingRelationship(
            assignment.courseOffering() );
        prefs().setAssignmentOfferingRelationship( assignment );
        return pageWithName(
            wcSession().tabs.selectById( "UploadSubmission" ).pageName() );
    }


    // ----------------------------------------------------------
    /**
     * View results for the most recent submission to the selected assignments.
     * 
     * @return the most recent results page
     */
    public WOComponent viewResults()
    {
        wcSession().setCourseOfferingRelationship(
            assignment.courseOffering() );
        prefs().setAssignmentOfferingRelationship( assignment );
        SubmissionResult subResult = assignment.mostRecentSubmissionResultFor(
            wcSession().user() );
        String destinationPageName = null;
        if ( subResult != null )
        {
            prefs().setSubmissionRelationship(  subResult.submission() );
            destinationPageName =
                wcSession().tabs.selectById( "MostRecent" ).pageName();
        }
        else
        {
            destinationPageName =
                wcSession().tabs.selectById( "PickSubmission" ).pageName();
        }
        return pageWithName( destinationPageName );
    }


    // ----------------------------------------------------------
    /**
     * An action to go to the graphing page for a given assignment.
     * 
     * @return the graphing page for the selected assignment
     */
    public WOComponent graphResults()
    {
        wcSession().setCourseOfferingRelationship(
            assignment.courseOffering() );
        prefs().setAssignmentOfferingRelationship( assignment );
        return pageWithName(
            wcSession().tabs.selectById( "GraphResults" ).pageName() );
    }


    // ----------------------------------------------------------
    /**
     * An action to go to edit page for a given assignment.
     * 
     * @return the properties page for the selected assignment
     */
    public WOComponent editAssignment()
    {
        wcSession().setCourseOfferingRelationship(
            assignment.courseOffering() );
        prefs().setAssignmentOfferingRelationship( assignment );
        return pageWithName(
            wcSession().tabs.selectById( "AssignmentProperties" ).pageName() );
    }


    // ----------------------------------------------------------
    /**
     * An action to go to edit page for a given assignment.
     * 
     * @return the properties page for the selected assignment
     */
    public WOComponent gradeAssignment()
    {
        wcSession().setCourseOfferingRelationship(
            assignment.courseOffering() );
        prefs().setAssignmentOfferingRelationship( assignment );
        return pageWithName(
            wcSession().tabs.selectById( "EnterGrades" ).pageName() );
    }


    //~ Instance/static variables .............................................

    private NSTimestamp currentTime;
    static Logger log = Logger.getLogger( GraderHomeStatus.class );
}
