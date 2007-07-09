/*==========================================================================*\
 |  $Id: Assignment.java,v 1.4 2007/07/09 15:49:00 stedwar2 Exp $
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

import com.webobjects.foundation.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import er.extensions.ERXArrayUtilities;
import java.io.File;
import java.util.*;
import net.sf.webcat.core.*;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * An assignment that can be given in one or more classes.
 *
 * @author Stephen Edwards
 * @version $Id: Assignment.java,v 1.4 2007/07/09 15:49:00 stedwar2 Exp $
 */
public class Assignment
    extends _Assignment
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new Assignment object.
     */
    public Assignment()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * Look up an Assignment by id number.  Assumes the editing
     * context is appropriately locked.
     * @param ec The editing context to use
     * @param id The id to look up
     * @return The assignment, or null if no such id exists
     */
    public static Assignment assignmentForId( EOEditingContext ec, int id )
    {
        Assignment assignment = null;
        NSArray results = EOUtilities.objectsMatchingKeyAndValue( ec,
            ENTITY_NAME, "id", new Integer( id ) );
        if ( results != null && results.count() > 0 )
        {
            assignment = (Assignment)results.objectAtIndex( 0 );
        }
        return assignment;
    }


    // ----------------------------------------------------------
    /**
     * Look up an Assignment by id number.  Assumes the editing
     * context is appropriately locked.  Converts the string parameter
     * to an integer, then performs the search.
     * @param ec The editing context to use
     * @param id The id to look up
     * @return The assignment, or null if no such id exists
     */
    public static Assignment assignmentForId( EOEditingContext ec, String id )
    {
        Assignment result = null;
        int idNumber = er.extensions.ERXValueUtilities.intValue( id );
        if ( idNumber > 0 )
        {
            result = assignmentForId( ec, idNumber );
        }
        return result;
    }


    //~ Constants (for key names) .............................................

    public static final String COURSE_OFFERINGS_KEY =
        OFFERINGS_KEY
        + "." + AssignmentOffering.COURSE_OFFERING_KEY;
    public static final String COURSES_KEY =
        COURSE_OFFERINGS_KEY
        + "." + CourseOffering.COURSE_KEY;
    public static final String ID_FORM_KEY = "aid";


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public String subdirName()
    {
        if ( cachedSubdirName == null )
        {
            String name = name();
            cachedSubdirName = subdirNameOf( name );
            log.debug( "trimmed name '" + name + "' to '"
                       + cachedSubdirName + "'" );
        }
        return cachedSubdirName;
    }


    // ----------------------------------------------------------
    /**
     * Get a short (no longer than 60 characters) description of this
     * assignment.
     * @return the description
     */
    public String userPresentableDescription()
    {
        String result = name();
        if ( offerings().count() > 0 )
        {
            result += " (" +
                ( (AssignmentOffering)offerings().objectAtIndex( 0 ) )
                    .courseOffering().course().deptNumber()
                + ")";
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Get a human-readable representation of this assignment, which is
     * the same as {@link #userPresentableDescription()}.
     * @return this user's name
     */
    public String toString()
    {
        return userPresentableDescription();
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>id</code> value.
     * @return the value of the attribute
     */
    public Number id()
    {
        return (Number)EOUtilities.primaryKeyForObject(
            editingContext() , this ).objectForKey( "id" );
    }


    // ----------------------------------------------------------
    public void setName( String value )
    {
        // TODO[low]: Need to rename the corresponding directory as well
        if ( dirNeedingRenaming == null && name() != null )
        {
            dirNeedingRenaming = subdirName();
        }
        cachedSubdirName = null;
        super.setName( value.trim() );
    }


    // ----------------------------------------------------------
    public Object validateName( Object value )
    {
        if ( value == null )
        {
            log.debug( "conflict exists, throwing exception" );
            throw new ValidationException(
                "Please provide an assignment name." );
        }
        String result = value.toString().trim();
        String newSubdirName = subdirNameOf( result );
        log.debug( "validateName(" + result + ")" );
        log.debug( "subdir = " + newSubdirName );
        if ( !newSubdirName.equals( subdirName() )
             && conflictingSubdirNameExists( newSubdirName ) )
        {
            log.debug( "conflict exists, throwing exception" );
            throw new ValidationException(
                "The name '" + result + "' conflicts with an existing "
                + "assignment.  Please choose another name." );
        }
        log.debug( "no conflict found" );
        return result;
    }


    // ----------------------------------------------------------
    /* (non-Javadoc)
     * @see er.extensions.ERXGenericRecord#didUpdate()
     */
    public void didUpdate()
    {
        super.didUpdate();
        if ( dirNeedingRenaming != null )
        {
            renameSubdirs( dirNeedingRenaming, subdirName() );
            dirNeedingRenaming = null;
        }
    }


    // ----------------------------------------------------------
    public String titleString()
    {
        String result = name();
        if ( shortDescription() != null )
        {
            result += ": " + shortDescription();
        }
        return result;
    }


    // ----------------------------------------------------------
    public Step addNewStep( ScriptFile script )
    {
        int position = steps().count() + 1;
        Step step = createStepsRelationship();
        step.setOrder( position );
        step.setScriptRelationship( script );
        return step;
    }


    // ----------------------------------------------------------
    public Step copyStep( Step step, boolean keepOrdering )
    {
        Step newStep = addNewStep( step.script() );
        newStep.setTimeout( step.timeout() );
        newStep.setConfigRelationship( step.config() );
        MutableDictionary dict = step.configSettings();
        if ( dict != null )
        {
            newStep.setConfigSettings( new MutableDictionary( dict ) );
        }
        if ( keepOrdering )
        {
            newStep.setOrder( step.order() );
        }
        return newStep;
    }


    // ----------------------------------------------------------
    public boolean usesTestingScore()
    {
        SubmissionProfile profile = submissionProfile();
        return profile != null
            && ( profile.correctnessPoints() > 0.0 );
    }


    // ----------------------------------------------------------
    public boolean usesToolCheckScore()
    {
        SubmissionProfile profile = submissionProfile();
        return profile != null
            && ( profile.toolPointsRaw() != null
                 || profile.toolPoints() != 0 );
    }


    // ----------------------------------------------------------
    public boolean usesBonusesOrPenalties()
    {
        SubmissionProfile profile = submissionProfile();
        return profile != null
            && ( profile.awardEarlyBonus() || profile.deductLatePenalty() );
    }


    // ----------------------------------------------------------
    public NSTimestamp commonOfferingsDueDate()
    {
        NSTimestamp common = null;
        NSArray offerings = offerings();
        if ( offerings.count() > 1 )
        {
            for ( int i = 0; i < offerings.count(); i++ )
            {
                AssignmentOffering ao =
                    (AssignmentOffering)offerings.objectAtIndex( i );
                if ( common == null )
                {
                    common = ao.dueDate();
                }
                else if ( common.compare( ao.dueDate() ) !=
                          NSComparator.OrderedSame )
                {
                    common = null;
                    break;
                }
            }
        }
        return common;
    }


    // ----------------------------------------------------------
    public AssignmentOffering offeringForUser( User user )
    {
        AssignmentOffering offering = null;
        NSDictionary userBinding = new NSDictionary( user, "user" );

        // First, check to see if the user is a student in any of the
        // course offerings associated with the available assignment offerings
        NSArray results = ERXArrayUtilities
            .filteredArrayWithEntityFetchSpecification( offerings(),
                AssignmentOffering.ENTITY_NAME,
                AssignmentOffering.STUDENT_FSPEC,
                userBinding );
        if ( results == null || results.count() == 0 )
        {
            // if the user is not found as a student, check for staff instead
            results = ERXArrayUtilities
                .filteredArrayWithEntityFetchSpecification( offerings(),
                    AssignmentOffering.ENTITY_NAME,
                    AssignmentOffering.STAFF_FSPEC,
                    userBinding );
        }
        if ( results != null && results.count() > 0 )
        {
            offering = (AssignmentOffering)results.objectAtIndex( 0 );
        }
        return offering;
    }


    // ----------------------------------------------------------
    public static boolean namesAreSimilar( String name1, String name2 )
    {
        boolean result = false;
        int limit = Math.min( name1.length(), name2.length() );
        for ( int i = 0; i < limit; i++ )
        {
            if ( Character.isLetter( name1.charAt( i ) ) )
            {
                result = ( name1.charAt( i ) == name2.charAt( i ) );
                if ( !result ) break;
            }
            else
            {
                break;
            }
        }
        return result;
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    private String subdirNameOf( String name )
    {
        String result = null;
        if ( name != null )
        {
            char[] chars = new char[ name.length() ];
            int  pos   = 0;
            for ( int i = 0; i < name.length(); i++ )
            {
                char c = name.charAt( i );
                if ( Character.isLetterOrDigit( c ) ||
                     c == '_'                       ||
                     c == '-' )
                {
                    chars[ pos ] = c;
                    pos++;
                }
            }
            result = new String( chars, 0, pos );
        }
        return result;
    }


    // ----------------------------------------------------------
    private boolean conflictingSubdirNameExists( String subdir )
    {
        NSArray domains = AuthenticationDomain.authDomains();
        for ( int i = 0; i < domains.count(); i++ )
        {
            AuthenticationDomain domain =
                (AuthenticationDomain)domains.objectAtIndex( i );
            NSArray offerings = offerings();
            StringBuffer dir =
                AssignmentOffering.submissionBaseDirName( domain );
            int baseDirLen = dir.length();
            for ( int j = 0; j < offerings.count(); j++ )
            {
                // clear out old suffix
                dir.delete( baseDirLen, dir.length() );
                AssignmentOffering offering =
                    (AssignmentOffering)offerings.objectAtIndex( j );
                offering.addSubdirNameForAssignment( dir, subdir );
                File f = new File( dir.toString() );
                if ( f.exists() )
                {
                    return true;
                }
            }
        }
        return false;
    }


    // ----------------------------------------------------------
    private void renameSubdirs( String oldSubdir, String newSubdir )
    {
        NSArray domains = AuthenticationDomain.authDomains();
        for ( int i = 0; i < domains.count(); i++ )
        {
            AuthenticationDomain domain =
                (AuthenticationDomain)domains.objectAtIndex( i );
            NSArray offerings = offerings();
            StringBuffer dir =
                AssignmentOffering.submissionBaseDirName( domain );
            int baseDirLen = dir.length();
            for ( int j = 0; j < offerings.count(); j++ )
            {
                // clear out old suffix
                dir.delete( baseDirLen, dir.length() );
                AssignmentOffering offering =
                    (AssignmentOffering)offerings.objectAtIndex( j );
                offering.addSubdirNameForAssignment( dir, oldSubdir );
                File oldDir = new File( dir.toString() );
                dir.delete( baseDirLen, dir.length() );
                offering.addSubdirNameForAssignment( dir, newSubdir );
                File newDir = new File( dir.toString() );
                if ( oldDir.exists() )
                {
                    oldDir.renameTo( newDir );
                }
            }
        }
    }


    //~ Instance/static variables .............................................

    private String cachedSubdirName   = null;
    private String dirNeedingRenaming = null;
    static Logger log = Logger.getLogger( Assignment.class );
}
