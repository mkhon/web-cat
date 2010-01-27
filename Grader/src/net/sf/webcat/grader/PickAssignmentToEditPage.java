/*==========================================================================*\
 |  $Id: PickAssignmentToEditPage.java,v 1.10 2010/01/27 01:01:58 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2010 Virginia Tech
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

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import er.extensions.foundation.ERXValueUtilities;
import net.sf.webcat.core.*;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 *  This page presents a list of assignments available for the user
 *  to choose from.
 *
 *  @author  Stephen Edwards
 * @author  latest changes by: $Author: stedwar2 $
 * @version $Revision: 1.10 $, $Date: 2010/01/27 01:01:58 $
 */
public class PickAssignmentToEditPage
    extends GraderCourseComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new PickAssignmentPage object.
     *
     * @param context The context to use
     */
    public PickAssignmentToEditPage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public AssignmentOffering anAssignmentOffering;
    public WODisplayGroup     assignmentDisplayGroup;
    public int                index         = -1;
    public int                selectedIndex = -1;
    public boolean            createNew;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    protected void beforeAppendToResponse(
        WOResponse response, WOContext context)
    {
        log.debug( "starting appendToResponse()" );
        createNew = false;
        selectedIndex = -1;
        assignmentDisplayGroup.setQualifier(
                new EOKeyValueQualifier( AssignmentOffering.COURSE_OFFERING_KEY,
                                         EOQualifier.QualifierOperatorEqual,
                                         coreSelections().courseOffering() )
            );
        assignmentDisplayGroup.fetch();
        if ( assignmentDisplayGroup.displayedObjects().count() == 0 )
        {
            log.debug( "attempting second group fetch" );
            assignmentDisplayGroup.fetch();
        }
        AssignmentOffering selectedAssignment = prefs().assignmentOffering();
        if ( selectedAssignment != null )
        {
            selectedIndex =
                assignmentDisplayGroup.displayedObjects().
                    indexOfIdenticalObject( selectedAssignment );
            if ( selectedIndex == NSArray.NotFound )
            {
                prefs().setAssignmentOfferingRelationship( null );
                selectedAssignment = null;
            }
        }
        if ( selectedAssignment == null  &&
             assignmentDisplayGroup.displayedObjects().count() > 0 )
        {
            selectedIndex = 0;
            prefs().setAssignmentOfferingRelationship(
                    (AssignmentOffering)assignmentDisplayGroup
                        .displayedObjects().objectAtIndex( selectedIndex )
                );
        }
        createNew = selectedIndex == -1 && canCreate();

        super.beforeAppendToResponse( response, context );
    }


    // ----------------------------------------------------------
    public boolean nextEnabled()
    {
        return (  assignmentDisplayGroup.displayedObjects().count() > 0
               || canCreate() )
            &&  super.nextEnabled();
    }


    // ----------------------------------------------------------
    public WOComponent next()
    {
        if ( selectedIndex == -1 && !createNew )
        {
            error( "You must choose an assignment to proceed." );
            return null;
        }
        else if ( createNew )
        {
            WCComponent createPage = (WCComponent)pageWithName(
                CreateAssignmentPage.class.getName() );
            createPage.nextPage = this;
            return createPage;
        }
        else // if ( selectedIndex > -1 )
        {
            log.debug( "existing assignment selected ("
                       + selectedIndex
                       + ")" );
            prefs().setAssignmentOfferingRelationship(
                (AssignmentOffering)assignmentDisplayGroup
                    .displayedObjects().objectAtIndex( selectedIndex ) );
            return super.next();
        }
    }


    // ----------------------------------------------------------
    public void cancelLocalChanges()
    {
        NSDictionary<String, Object> config =
            wcSession().tabs.selectedDescendant().config();
        if ( config != null
             && config.objectForKey( "resetPrimeUser" ) != null )
        {
            setLocalUser( wcSession().primeUser() );
        }
        super.cancelLocalChanges();
    }


    // ----------------------------------------------------------
    public boolean showTable()
    {
        return assignmentDisplayGroup.displayedObjects().count() > 0
            || canCreate();
    }


    // ----------------------------------------------------------
    public boolean canCreate()
    {
        NSDictionary<String, Object> config =
            wcSession().tabs.selectedDescendant().config();
        return config != null &&
            ERXValueUtilities.booleanValueWithDefault(
                config.objectForKey( "allowCreate" ), false );
    }


    // ----------------------------------------------------------
    /**
     * Determine if the current assignment has suspended submissions (that
     * this user can see).
     *
     * @return true if the user can see this assignment's status and this
     * assignment has suspended submissions
     */
    public boolean assignmentHasSuspendedSubs()
    {
        return ( user().hasAdminPrivileges()
                 || anAssignmentOffering.courseOffering().instructors()
                     .containsObject( user() ) )
               && anAssignmentOffering.getSuspendedSubs().count() > 0;
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( PickAssignmentToEditPage.class );
}
