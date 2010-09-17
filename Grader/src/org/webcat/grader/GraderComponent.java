/*==========================================================================*\
 |  $Id: GraderComponent.java,v 1.1 2010/05/11 14:51:40 aallowat Exp $
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

package org.webcat.grader;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import java.io.File;
import java.io.FileOutputStream;
import org.apache.log4j.Logger;
import org.webcat.core.*;
import org.webcat.core.messaging.UnexpectedExceptionMessage;

// -------------------------------------------------------------------------
/**
 *  A specialized version of {@link WCCourseComponent} that adds some extras
 *  for use by components in the Grader subsystem.
 *
 *  @author  Stephen Edwards
 *  @version $Id: GraderComponent.java,v 1.1 2010/05/11 14:51:40 aallowat Exp $
 */
public class GraderComponent
    extends WCCourseComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new GraderComponent object.
     *
     * @param context The context to use
     */
    public GraderComponent( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public static final String GRADER_PREFS_KEY = "graderPrefs";


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Grab user's current selections when waking, if necessary.
     */
    @Override
    public void awake()
    {
        if (log.isDebugEnabled())
        {
            log.debug("awake(): begin " + getClass().getName());
        }
        super.awake();
        prefs();
        if (log.isDebugEnabled())
        {
            log.debug("awake(): end " + getClass().getName());
        }
    }


    // ----------------------------------------------------------
    /**
     * Determine whether "Finish" can be pressed for this task.
     * @return true if a submission is stored
     */
    public boolean canFinish()
    {
        return prefs().submission() != null;
    }


    // ----------------------------------------------------------
    /**
     * Access the user's current grader preferences.
     * @return the grader preferences manager for this page
     */
    public GraderPrefsManager prefs()
    {
        if (prefs == null)
        {
            Object inheritedPrefs = transientState().valueForKey( GP_KEY );
            if (inheritedPrefs == null)
            {
                reloadGraderPrefs();
            }
            else
            {
                prefs = (GraderPrefsManager)
                    ((GraderPrefsManager)inheritedPrefs).clone();
            }
        }
        return prefs;
    }


    // ----------------------------------------------------------
    @Override
    public WOComponent pageWithName( String name )
    {
        if (prefs != null)
        {
            transientState().takeValueForKey( prefs, GP_KEY );
        }
        WOComponent result = super.pageWithName( name );
        return result;
    }


    // ----------------------------------------------------------
    @Override
    public void changeWorkflow()
    {
        super.changeWorkflow();
        resetPrimeUser();
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    /**
     * Forces current prefs values to be reloaded from the database.
     */
    protected void reloadGraderPrefs()
    {
        prefs = new GraderPrefsManager(
            getGraderPrefs(), ecManager());
    }


    // ----------------------------------------------------------
    /**
     * If the current page should reset the prime user when you leave it,
     * this method will do the job.
     */
    public void resetPrimeUser()
    {
        NSDictionary config = currentTab().config();
        if ( config != null
             && config.objectForKey( "resetPrimeUser" ) != null )
        {
            setLocalUser( wcSession().primeUser() );
        }
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    /**
     * Returns the currently selected assignment offering.
     * @return The assignment offering
     */
    private GraderPrefs getGraderPrefs()
    {
        NSArray results = null;
        try
        {
            results = GraderPrefs.objectsForUser(localContext(), user());
        }
        catch ( java.lang.IllegalStateException e )
        {
            // Just try again, in case this is a failure due to the
            // use of shared contexts under Win2K
            results = GraderPrefs.objectsForUser(localContext(), user());
        }
        if ( results.count() > 0 )
        {
            return (GraderPrefs)results.objectAtIndex(0);
        }
        else
        {
            EOEditingContext ec = Application.newPeerEditingContext();
            GraderPrefs newPrefs = null;
            try
            {
                ec.lock();
                newPrefs = GraderPrefs.create(ec);
                newPrefs.setUserRelationship(user().localInstance(ec));
                ec.saveChanges();
                newPrefs = newPrefs.localInstance(localContext());
            }
            catch ( Exception e)
            {
                new UnexpectedExceptionMessage(e, context(), null,
                        "failure initializing prefs!").send();
            }
            finally
            {
                ec.unlock();
                Application.releasePeerEditingContext( ec );
            }
            /*  Appears to be unnecessary ...
            if ( newPrefs == null )
            {
                log.error( "null prefs!", new Exception( "here" ) );
                log.error( Application.extraInfoForContext( context() ) );
            }
            */
            return newPrefs;
        }
    }


    //~ Instance/static variables .............................................

    private GraderPrefsManager prefs;
    private static final String GP_KEY =
        GraderPrefsManager.class.getName();
    static Logger log = Logger.getLogger( GraderComponent.class );
}