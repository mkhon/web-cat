/*==========================================================================*\
 |  $Id: webapi.java,v 1.2 2008/06/09 18:19:52 stedwar2 Exp $
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

package net.sf.webcat.webapi;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOSession;
import er.extensions.ERXDirectAction;
import org.apache.log4j.Logger;

import net.sf.webcat.core.AuthenticationDomain;
import net.sf.webcat.core.Session;
import net.sf.webcat.core.User;

//-------------------------------------------------------------------------
/**
 * This direct action class handles all response actions for WebAPI
 * queries from external tools.
 *
 * @author Stephen Edwards
 * @version $Id: webapi.java,v 1.2 2008/06/09 18:19:52 stedwar2 Exp $
 */
public class webapi
    extends ERXDirectAction
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new DirectAction object.
     *
     * @param aRequest The request to respond to
     */
    public webapi(WORequest aRequest)
    {
        super(aRequest);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * The default action simply returns an invalid request response.
     * @return The session response
     */
    public WOActionResults defaultAction()
    {
        return pageWithName(SimpleMessageResponse.class);
    }


    // ----------------------------------------------------------
    public WOActionResults performActionNamed(String actionName)
    {
        if (!"default".equals(actionName)
            && !"startSession".equals(actionName))
        {
            // require an active session for all other actions
            if (existingSession() == null)
            {
                log.debug("no active session: action "
                    + actionName + " redirected to default");
                actionName = "default";
            }
        }
        log.debug("performActionNamed(" + actionName + ")");
        return super.performActionNamed(actionName);
    }

    // ----------------------------------------------------------
    /**
     * Begin a new WebAPI session for external requests.
     *
     * @return The session response
     */
    public WOActionResults startSessionAction()
    {
        log.debug("startSessionAction()");

        if (existingSession() != null)
        {
            return defaultAction();
        }

        Session session = (Session)session();
        WORequest request = request();

        // Try to validate the user's credentials
        session.setUseLoginSession(false);

        String userName = request.stringFormValueForKey( "UserName" );
        if ( userName == null )
            userName = request.stringFormValueForKey( "u" );

        String password = request.stringFormValueForKey( "UserPassword" );
        if ( password == null )
            password = request.stringFormValueForKey( "p" );

        AuthenticationDomain domain = null;
        String institution = request.stringFormValueForKey("institution");
        if (institution != null)
        {
            domain = AuthenticationDomain.authDomainByName(institution);
        }
        else
        {
            domain = AuthenticationDomain.defaultDomain();
        }

        if (userName != null && password != null && domain != null)
        {
            User user = User.validate(userName, password, domain,
                session.sessionContext());
            if ( user != null )
            {
                session.setUser(user);
            }
        }

        if (session.user() != null)
        {
            return pageWithName(StartSession.class);
        }
        else
        {
            session.terminate();
            return defaultAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * Terminate the current session.
     *
     * @return The results in an XML response
     */
    public WOActionResults endSessionAction()
    {
        ((Session)session()).terminate();
        SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
        page.message = "session terminated";
        return page;
    }


    // ----------------------------------------------------------
    /**
     * Generate a list of all courses and assignments that the user can
     * access.
     *
     * @return The results in an XML response
     */
    public WOActionResults coursesAndAssignmentsAction()
    {
        log.debug("coursesAndAssignmentsAction()");
        session();
        return pageWithName(CoursesAndAssignments.class);
    }


    // ----------------------------------------------------------
    // Just provides some debug logging
    @Override
    public WOSession existingSession()
    {
        WOSession result = super.existingSession();
        if (log.isDebugEnabled())
        {
            log.debug("existingSession() = "
                + ((result == null)
                    ? "null"
                    : result.sessionID()));
        }
        return result;
    }


    // ----------------------------------------------------------
    // Just provides some debug logging
    @Override
    public WOSession session()
    {
        WOSession result = super.session();
        if (log.isDebugEnabled())
        {
            log.debug("session() = "
                + ((result == null)
                    ? "null"
                    : result.sessionID()));
        }
        return result;
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger(webapi.class);
}
