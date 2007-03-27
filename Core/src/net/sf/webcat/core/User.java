/*==========================================================================*\
 |  $Id: User.java,v 1.3 2007/03/27 20:36:24 stedwar2 Exp $
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

package net.sf.webcat.core;

import com.webobjects.foundation.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;

import er.extensions.*;

import java.util.*;
import org.apache.log4j.*;

// -------------------------------------------------------------------------
/**
 * A user of the system.
 * <p>
 * This class also defines constant values for the access levels supported
 * by Web-CAT.  Each higher access level subsumes all of the rights and
 * privileges of all lower levels.  The levels, in order from lowest to
 * highest, are:
 * </p><ul>
 * <li> STUDENT_PRIVILEGES
 * <li> GRADER_PRIVILEGES
 * <li> GTA_PRIVILEGES
 * <li> INSTRUCTOR_PRIVILEGES
 * <li> WEBCAT_READ_PRIVILEGES
 * <li> WEBCAT_RW_PRIVILEGES
 * </ul>
 *
 * @author Stephen Edwards
 * @version $Id: User.java,v 1.3 2007/03/27 20:36:24 stedwar2 Exp $
 */
public class User
    extends _User
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new User object.
     */
    public User()
    {
        super();
    }


    //~ Public Constants ......................................................

    public static final byte STUDENT_PRIVILEGES     = 0;
    public static final byte GRADER_PRIVILEGES      = 30;
    public static final byte GTA_PRIVILEGES         = 40;
    public static final byte INSTRUCTOR_PRIVILEGES  = 50;
    public static final byte WEBCAT_READ_PRIVILEGES = 80;
    public static final byte WEBCAT_RW_PRIVILEGES   = 90;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Creates a new user.
     *
     * @param pid         The new username
     * @param password    The user's password
     * @param domain      The domain the user comes from
     * @param accessLevel The user's access privilege level
     * @param ec          The editing context in which to create the user
     * @return            The new user object
     */
    public static User createUser( String               pid,
                                   String               password,
                                   AuthenticationDomain domain,
                                   byte                 accessLevel,
                                   EOEditingContext     ec )
    {
        User u = new User();
        ec.insertObject( u );
        u.setPreferences( new MutableDictionary() );
        u.setUserName( pid );
        u.setPassword( password );
        u.setAccessLevel( accessLevel );
        u.setAuthenticationDomainRelationship( domain );
        ec.saveChanges();
        return u;
    }


    // ----------------------------------------------------------
    /**
     * Return the user's full name as a string, in the format "First Last".
     * @return the name
     */
    public String name()
    {
        String last  = lastName();
        String first = firstName();
        boolean lastIsEmpty = ( last == null || last.equals( "" ) );
        boolean firstIsEmpty = ( first == null || first.equals( "" ) );

        if ( lastIsEmpty && firstIsEmpty )
            return userName();
        else if ( lastIsEmpty )
            return first;
        else if ( firstIsEmpty )
            return last;
        else
            return first + " " + last;
    }


    // ----------------------------------------------------------
    /**
     * Return the user's full name as a string, in the format "Last, First"
     * (the meaning of the _LF suffix).
     * @return the name
     */
    public String name_LF()
    {
        if ( name_LF_cache == null )
        {
            String last  = lastName();
            String first = firstName();
            boolean lastIsEmpty = ( last == null || last.equals( "" ) );
            boolean firstIsEmpty = ( first == null || first.equals( "" ) );
    
            if ( lastIsEmpty && firstIsEmpty )
                name_LF_cache = userName();
            else if ( lastIsEmpty )
                name_LF_cache = first;
            else if ( firstIsEmpty )
                name_LF_cache = last;
            else
                name_LF_cache = last + ", " + first;
        }
        return name_LF_cache;
    }


    // ----------------------------------------------------------
    /* (non-Javadoc)
     * @see net.sf.webcat.core._User#setFirstName(java.lang.String)
     */
    public void setFirstName( String value )
    {
        super.setFirstName( value );
        name_LF_cache = null;
    }


    // ----------------------------------------------------------
    /* (non-Javadoc)
     * @see net.sf.webcat.core._User#setLastName(java.lang.String)
     */
    public void setLastName( String value )
    {
        super.setLastName( value );
        name_LF_cache = null;
    }


    // ----------------------------------------------------------
    /* (non-Javadoc)
     * @see net.sf.webcat.core._User#setUserName(java.lang.String)
     */
    public void setUserName( String value )
    {
        super.setUserName( value );
        name_LF_cache = null;
    }


    // ----------------------------------------------------------
    /**
     * Return the user's full name as a string, in the format "First Last".
     * @return the name
     */
    public String nameAndUid()
    {
        String name  = name();
        if ( name == null || name.equals( "" ) )
            return userName();
        else
            return name + " (" + userName() + ")";
    }


    // ----------------------------------------------------------
    /**
     * Return the user's full name as a string, in the format "Last, First"
     * (the meaning of the _LF suffix).
     * @return the name
     */
    public String shortName()
    {
        String last  = lastName();

        if ( last == null || last.equals( "" ) )
            return userName();
        else
            return last;
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this user's e-mail address.  This comes
     * from the <code>email</code> attribute value, if set.  If
     * <code>email</code> is not set, the user's pid is combined
     * with "@" and the authentication domain's default e-mail domain.
     *
     * @return the e-mail address
     */
    public String email()
    {
        String result = super.email();
        if ( result == null || result == "" )
        {
            result = userName();
            AuthenticationDomain authDomain = authenticationDomain();
            if ( authDomain != null )
            {
                String domain = authDomain.defaultEmailDomain();
                if ( domain != null && domain != "" )
                {
                    result = result + "@" + domain;
                }
            }
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Returns an href for emailing a user.
     * @return a mailto: string containing the e-mail address
     */
    public String emailHref()
    {
        return "mailto:" + email();
    }


    // ----------------------------------------------------------
    /**
     * Validate the user with the given password.
     * Internally, it uses the <code>CurrentUserAuthenticator</code>
     * class to perform authentication.
     * 
     * @param userName The user id to validate
     * @param password The password to check
     * @param domain   The domain to which this user belongs
     * @param ec       The editing context in which to create the user object
     * @return True if the username/password combination is valid
     */
    public static User validate(
            String userName,
            String password,
            AuthenticationDomain domain,
            com.webobjects.eocontrol.EOEditingContext ec
        )
    {
        UserAuthenticator authenticator = domain.authenticator();
        if ( authenticator != null )
        {
            return authenticator.authenticate(
                    userName, password, domain, ec );
        }
        else
        {
            log.error( "no registered authenticator called "
                       + domain.propertyName() );
            return null;
        }
    }


    // ----------------------------------------------------------
    /**
     * Check whether this user can change his/her password.
     * @return True if users associated with this authenticator can
     *         change their password
     */
    public boolean canChangePassword()
    {
        AuthenticationDomain ad = authenticationDomain();
        if ( ad == null )
            return false;
        else
        {
            UserAuthenticator authenticator = ad.authenticator();
            return authenticator != null
                && authenticator.canChangePassword();
        }
    }


    // ----------------------------------------------------------
    /**
     * Change the user's password, if possible.
     * @param newPassword The password to change to
     * @return True if the password change was successful
     */
    public boolean changePassword( String newPassword )
    {
        AuthenticationDomain ad = authenticationDomain();
        if ( ad == null )
            return false;
        else
        {
            UserAuthenticator authenticator = ad.authenticator();
            return authenticator != null
                && authenticator.changePassword( this, newPassword );
        }        
    }


    // ----------------------------------------------------------
    /**
     * Reset the user's password to a new random password and e-mail the
     * user a message, if possible.
     * @return True if the password change was successful
     */
    public boolean newRandomPassword()
    {
        AuthenticationDomain ad = authenticationDomain();
        if ( ad == null )
            return false;
        else
        {
            UserAuthenticator authenticator = ad.authenticator();
            return authenticator != null
                && authenticator.newRandomPassword( this );
        }        
    }


    // ----------------------------------------------------------
    /**
     * Find out if this user can switch between student and staff views.
     * @return true if this user can switch
     */
    public boolean canChangeViews()
    {
        return accessLevel() > STUDENT_PRIVILEGES;
    }


    // ----------------------------------------------------------
    /**
     * Toggle the student view setting for this user.  This only affect's
     * the state within the user object, and does not affect any session
     * navigation or other UI features.  It is intended to be called from
     * {@link Session#toggleStudentView()}, which handles updating the
     * corresponding session navigation data.
     * @return Returns null, to force reloading of the calling page
     * (if desired)
     */
    public WOComponent toggleStudentView()
    {
        if ( canChangeViews() )
        {
            studentView = !studentView;
            taFor_cache = null;
            teaching_cache = null;
            taForButNotStudent_cache = null;
            instructorForButNotTAOrStudent_cache = null;
            staffFor_cache = null;
            adminForButNotStaff_cache = null;
            adminForButNoOtherRelationships_cache = null;
        }
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Get the string label for the "toggle view" button for this user.
     * @return The button label text, indicating what view the user will
     * toggle to next
     */
    public String toggleViewLabel()
    {
        String result = "Student View";
        if ( studentView )
        {
            if ( accessLevel() > INSTRUCTOR_PRIVILEGES )
            {
                result = "Admin View";
            }
            else if ( accessLevel() > GTA_PRIVILEGES )
            {
                result = "Instructor View";
            }
            else
            {
                result = "Grader View";
            }
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Determine if this user's view should be restricted to student-only
     * features.
     * @return true if this user's view is restricted
     */
    public boolean restrictToStudentView()
    {
        return studentView;
    }


    // ----------------------------------------------------------
    public NSArray TAFor()
    {
        return studentView ? emptyArray : super.TAFor();
    }


    // ----------------------------------------------------------
    public NSArray teaching()
    {
        return studentView ? emptyArray : super.teaching();
    }


    // ----------------------------------------------------------
    /**
     * Returns true if the user has TA privileges for Web-CAT.
     * @return true if user has at least TA access level
     */
    public boolean hasTAPrivileges()
    {
        return !studentView && accessLevel() >= GTA_PRIVILEGES;
    }


    // ----------------------------------------------------------
    /**
     * Returns true if the user has faculty privileges for Web-CAT.
     * @return true if user has at least faculty access level
     */
    public boolean hasFacultyPrivileges()
    {
        return !studentView && accessLevel() >= INSTRUCTOR_PRIVILEGES;
    }


    // ----------------------------------------------------------
    /**
     * Returns true if the user has faculty privileges for Web-CAT.
     * @return true if user has at least faculty access level
     */
    public boolean hasAdminPrivileges()
    {
        return !studentView && accessLevel() >= WEBCAT_RW_PRIVILEGES;
    }


    // ----------------------------------------------------------
    /**
     * Adds property definitions to the given properties object containing
     * this user's basic information.  All properties added begin with
     * "user.".
     * @param properties The object to which the definitions will be added
     */
    public void addPropertiesTo( Properties properties )
    {
        String value = email();
        if ( value != null )
            properties.setProperty( PREFIX + EMAIL_KEY, value );
        value = firstName();
        if ( value != null )
            properties.setProperty( PREFIX + FIRST_NAME_KEY, value );
        value = lastName();
        if ( value != null )
            properties.setProperty( PREFIX + LAST_NAME_KEY, value );
        value = password();
        if ( value != null )
            properties.setProperty( PREFIX + PASSWORD_KEY, value );
        value = universityIDNo();
        if ( value != null )
            properties.setProperty( PREFIX + UNIVERSITY_IDNO_KEY, value );
        value = url();
        if ( value != null )
            properties.setProperty( PREFIX + URL_KEY, value );
        value = userName();
        if ( value != null )
            properties.setProperty( PREFIX + USER_NAME_KEY, value );
    }


    // ----------------------------------------------------------
    /**
     * Returns a sorted list of course offerings that this user is a TA for,
     * without including any courses where this user is also a student.
     * @return a sorted array of the matching course offerings.
     */
    public NSArray TAForButNotStudent()
    {
        if ( taFor_cache != TAFor() )
        {
            taFor_cache = TAFor();
            taForButNotStudent_cache = null;
        }
        if ( taFor_cache == null || taFor_cache.count() == 0 )
        {
            taForButNotStudent_cache = emptyArray;
        }
        else
        {
            if ( enrolledIn_cache != enrolledIn() )
            {
                enrolledIn_cache = enrolledIn();
                taForButNotStudent_cache = null;
            }
            if ( taForButNotStudent_cache == null )
            {
                taForButNotStudent_cache =
                    ERXArrayUtilities.filteredArrayWithEntityFetchSpecification(
                        taFor_cache,
                        CourseOffering.ENTITY_NAME,
                        "withoutStudent",
                        userFilteringDictionary()
                        );
            }
        }
        return taForButNotStudent_cache;
    }


    // ----------------------------------------------------------
    /**
     * Returns a sorted list of course offerings that this user is an
     * instructor for, without including any courses where this user is also
     * a student or a TA.
     * @return a sorted array of the matching course offerings.
     */
    public NSArray instructorForButNotTAOrStudent()
    {
        if ( teaching_cache != teaching() )
        {
            teaching_cache = teaching();
            instructorForButNotTAOrStudent_cache = null;
        }
        if ( teaching_cache == null || teaching_cache.count() == 0 )
        {
            instructorForButNotTAOrStudent_cache = emptyArray;
        }
        else
        {
            if ( enrolledIn_cache != enrolledIn() )
            {
                enrolledIn_cache = enrolledIn();
                instructorForButNotTAOrStudent_cache = null;
            }
            if ( taFor_cache != TAFor() )
            {
                taFor_cache = TAFor();
                instructorForButNotTAOrStudent_cache = null;
            }
            if ( instructorForButNotTAOrStudent_cache == null )
            {
                instructorForButNotTAOrStudent_cache =
                    ERXArrayUtilities.filteredArrayWithEntityFetchSpecification(
                        teaching_cache,
                        CourseOffering.ENTITY_NAME,
                        "withoutStudentOrTA",
                        userFilteringDictionary()
                        );
            }
        }
        return instructorForButNotTAOrStudent_cache;
    }


    // ----------------------------------------------------------
    /**
     * Returns a sorted list of course offerings that this user is either
     * an instructor or TA for.
     * @return a sorted array of the matching course offerings.
     */
    public NSArray staffFor()
    {
        if ( taFor_cache != TAFor() )
        {
            taFor_cache = TAFor();
            staffFor_cache = null;
        }
        if ( teaching_cache != teaching() )
        {
            teaching_cache = teaching();
            staffFor_cache = null;
        }
        if ( staffFor_cache == null )
        {
            staffFor_cache = EOSortOrdering.sortedArrayUsingKeyOrderArray(
                teaching_cache.arrayByAddingObjectsFromArray( taFor_cache ),
                courseSortOrderings );
        }
        return staffFor_cache;
    }


    // ----------------------------------------------------------
    /**
     * Returns a sorted list of course offerings that this user has
     * administrative access to, but is not an instructor or TA
     * for.
     * @return a sorted array of the matching course offerings.
     */
    public NSArray adminForButNotStaff()
    {
        if ( !hasAdminPrivileges() ) return emptyArray;
        if ( taFor_cache != TAFor() )
        {
            taFor_cache = TAFor();
            adminForButNotStaff_cache = null;
        }
        if ( teaching_cache != teaching() )
        {
            teaching_cache = teaching();
            adminForButNotStaff_cache = null;
        }
        if ( adminForButNotStaff_cache == null )
        {
            // For some reason, using the fetch directly does not seem
            // to work, at least not when the result is empty.
//            adminForButNoOtherRelationships_cache =
//                CourseOffering.objectsForWithoutAnyRelationshipToUser(
//                    editingContext(), this );
            adminForButNotStaff_cache =
                ERXArrayUtilities.filteredArrayWithEntityFetchSpecification(
                    EOUtilities.objectsForEntityNamed( editingContext(),
                        CourseOffering.ENTITY_NAME ),
                    CourseOffering.ENTITY_NAME,
                    "withoutUserAsStaff",
                    userFilteringDictionary()
                    );
        }
        return adminForButNotStaff_cache;
    }


    // ----------------------------------------------------------
    /**
     * Returns a sorted list of course offerings that this user has
     * administrative access to, but is not an instructor, TA, or student
     * for.
     * @return a sorted array of the matching course offerings.
     */
    public NSArray adminForButNoOtherRelationships()
    {
        if ( !hasAdminPrivileges() ) return emptyArray;
        if ( enrolledIn_cache != enrolledIn() )
        {
            enrolledIn_cache = enrolledIn();
            adminForButNoOtherRelationships_cache = null;
        }
        if ( taFor_cache != TAFor() )
        {
            taFor_cache = TAFor();
            adminForButNoOtherRelationships_cache = null;
        }
        if ( teaching_cache != teaching() )
        {
            teaching_cache = teaching();
            adminForButNoOtherRelationships_cache = null;
        }
        if ( adminForButNoOtherRelationships_cache == null )
        {
            // For some reason, using the fetch directly does not seem
            // to work, at least not when the result is empty.
//            adminForButNoOtherRelationships_cache =
//                CourseOffering.objectsForWithoutAnyRelationshipToUser(
//                    editingContext(), this );
            adminForButNoOtherRelationships_cache =
                ERXArrayUtilities.filteredArrayWithEntityFetchSpecification(
                    EOUtilities.objectsForEntityNamed( editingContext(),
                        CourseOffering.ENTITY_NAME ),
                    CourseOffering.ENTITY_NAME,
                    "withoutAnyRelationshipToUser",
                    userFilteringDictionary()
                    );
        }
        return adminForButNoOtherRelationships_cache;
    }


//    // ----------------------------------------------------------
//    public void willUpdate()
//    {
//        dict = null;
//        super.willUpdate();
//    }

//  If you add instance variables to store property values you
//  should add empty implementions of the Serialization methods
//  to avoid unnecessary overhead (the properties will be
//  serialized for you in the superclass).

//     // ----------------------------------------------------------
//     /**
//      * Serialize this object (an empty implementation, since the
//      * superclass handles this responsibility).
//      * @param out the stream to write to
//      */
//     private void writeObject( java.io.ObjectOutputStream out )
//         throws java.io.IOException
//     {
//     }
 //
 //
//     // ----------------------------------------------------------
//     /**
//      * Read in a serialized object (an empty implementation, since the
//      * superclass handles this responsibility).
//      * @param in the stream to read from
//      */
//     private void readObject( java.io.ObjectInputStream in )
//         throws java.io.IOException, java.lang.ClassNotFoundException
//     {
//     }


    //~ Private Methods .......................................................
    // ----------------------------------------------------------
    private NSDictionary userFilteringDictionary()
    {
        if ( userIsMe == null )
        {
            userIsMe = new NSDictionary( this, "user" );
        }
        return userIsMe;
    }


    //~ Instance/static variables .............................................

    private NSArray enrolledIn_cache;
    private NSArray taFor_cache;
    private NSArray teaching_cache;
    private NSArray taForButNotStudent_cache;
    private NSArray instructorForButNotTAOrStudent_cache;
    private NSArray staffFor_cache;
    private NSArray adminForButNotStaff_cache;
    private NSArray adminForButNoOtherRelationships_cache;
    private String  name_LF_cache;
    
    private NSDictionary userIsMe;

    private boolean studentView = false;

    private static final String PREFIX = "user.";
    private static final NSArray emptyArray = new NSArray();
    private static final NSArray courseSortOrderings = new NSArray(
        new Object[] {
            EOSortOrdering.sortOrderingWithKey(
                CourseOffering.COURSE_NUMBER_KEY,
                EOSortOrdering.CompareAscending ),
            EOSortOrdering.sortOrderingWithKey(
                CourseOffering.CRN_KEY,
                EOSortOrdering.CompareAscending )
            });

    static Logger log = Logger.getLogger( User.class );
}
