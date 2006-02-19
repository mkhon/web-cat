/*==========================================================================*\
 |  $Id: UserAuthenticator.java,v 1.1 2006/02/19 19:03:09 stedwar2 Exp $
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

// -------------------------------------------------------------------------
/**
 *  An interface for all concrete user name/password authentication
 *  techniques.
 *
 *  @author Stephen Edwards
 *  @version $Id: UserAuthenticator.java,v 1.1 2006/02/19 19:03:09 stedwar2 Exp $
 */
public interface UserAuthenticator
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Initialize and configure the authenticator, reading subclass-specific
     * settings from properties.  The authenticator should read any
     * instance-specific settings from properties named
     * "baseName.<property>".  This operation should only be called once,
     * before any authenticate requests.
     * 
     * @param baseName   The base property name for this authenticator object
     * @param properties The property collection from which the object
     *                   should read its configuration settings
     * @return true If configuration was successful and authenticator is
     *              ready for service
     */
    public boolean configure( String       baseName,
                              WCProperties properties
                            );


    // ----------------------------------------------------------
    /**
     * Validate the user `username' with the password `password'.
     * Should not be called until the authenticator has been configured.
     * 
     * @param userName The user id to validate
     * @param password The password to check
     * @param domain   The authentication domain associated with this check
     * @param ec       The editing context to use
     * @return The current user object, or null if invalid login
     */
    public User authenticate( String userName,
                              String password,
                              AuthenticationDomain domain,
                              com.webobjects.eocontrol.EOEditingContext ec
                            );


    // ----------------------------------------------------------
    /**
     * Check whether users validated with this authenticator can
     * change their password.  For authentication mechanisms using
     * external databases or servers where no changes are allowed, the
     * authenticator should return false.
     * 
     * @return True if users associated with this authenticator can
     *         change their password
     */
    public boolean canChangePassword();


    // ----------------------------------------------------------
    /**
     * Change the user's password.  For authentication mechanisms using
     * external databases or servers where no changes are allowed, an
     * authenticator may simply return false for all requests.
     * 
     * @param user        The user
     * @param newPassword The password to change to
     * @return True if the password change was successful
     */
    public boolean changePassword( User   user,
                                   String newPassword );


    // ----------------------------------------------------------
    /**
     * Change the user's password to a new random password, and e-mail's
     * the user their new password.  For authentication mechanisms using
     * external databases or servers where no changes are allowed, an
     * authenticator may simply return false for all requests.
     * 
     * @param user        The user
     * @return True if the password change was successful
     */
    public boolean newRandomPassword( User user );
}
