/*==========================================================================*\
 |  $Id: WCCourseComponent.java,v 1.8 2009/04/27 17:10:53 stedwar2 Exp $
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

package net.sf.webcat.core;

import com.webobjects.appserver.*;
import com.webobjects.foundation.NSMutableArray;

import org.apache.log4j.*;

//-------------------------------------------------------------------------
/**
 * This specialized subclass of WCComponent represents pages that have
 * a notion of a currently-selected course offering and/or course.
 *
 * @author Stephen Edwards
 * @version $Id: WCCourseComponent.java,v 1.8 2009/04/27 17:10:53 stedwar2 Exp $
 */
public class WCCourseComponent
    extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     *
     * @param context The page's context
     */
    public WCCourseComponent( WOContext context )
    {
        super( context );
    }

    //~ Public Methods ........................................................

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
        coreSelections();
        if (log.isDebugEnabled())
        {
            log.debug("awake(): end " + getClass().getName());
        }
    }


    // ----------------------------------------------------------
    /**
     * Access the user's current core selections.
     * @return the core selections manager for this page
     */
    public CoreSelectionsManager coreSelections()
    {
        if (csm == null)
        {
            Object inheritedCsm = transientState().valueForKey( CSM_KEY );
            if (inheritedCsm == null)
            {
                if (user() != null)
                {
                    csm = new CoreSelectionsManager(
                        user().getMyCoreSelections(), ecManager());
                }
                // else: How is it possible to get here !?!?
            }
            else
            {
                csm = (CoreSelectionsManager)
                    ((CoreSelectionsManager)inheritedCsm).clone();
            }
        }
        return csm;
    }


    // ----------------------------------------------------------
    @Override
    public WOComponent pageWithName( String name )
    {
        if (csm != null)
        {
            transientState().takeValueForKey( csm, CSM_KEY );
        }
        WOComponent result = super.pageWithName( name );
        return result;
    }


    // ----------------------------------------------------------
    public NSMutableArray<INavigatorObject> courseOfferings()
    {
        return null;
    }


    // ----------------------------------------------------------
    public INavigatorObject selectedCourseOffering()
    {
        return null;
    }


    // ----------------------------------------------------------
    public void setSelectedCourseOffering(INavigatorObject selected)
    {
    }


    // ----------------------------------------------------------
    public NSMutableArray<INavigatorObject> semesters()
    {
        return null;
    }


    // ----------------------------------------------------------
    public INavigatorObject selectedSemester()
    {
        return null;
    }


    // ----------------------------------------------------------
    public void setSelectedSemester(INavigatorObject selected)
    {
    }


    // ----------------------------------------------------------
    public boolean allowsAllSemesters()
    {
        return allowsAllSemesters;
    }


    // ----------------------------------------------------------
    public void setAllowsAllSemesters(boolean value)
    {
        allowsAllSemesters = value;
    }


    // ----------------------------------------------------------
    public boolean allowsAllOfferingsForCourse()
    {
        return allowsAllOfferingsForCourse;
    }


    // ----------------------------------------------------------
    public void setAllowsAllOfferingsForCourse(boolean value)
    {
        allowsAllOfferingsForCourse = value;
    }


    // ----------------------------------------------------------
    public boolean includeWhatImTeaching()
    {
        // TODO: implement
        return includeWhatImTeaching;
    }


    // ----------------------------------------------------------
    public void setIncludeWhatImTeaching(boolean value)
    {
        // TODO: implement
    }


    // ----------------------------------------------------------
    public boolean includeAdminAccess()
    {
        // TODO: implement
        return includeAdminAccess;
    }


    // ----------------------------------------------------------
    public void setIncludeAdminAccess(boolean value)
    {
        // TODO: implement
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    protected EOManager.ECManager ecManager()
    {
        EOManager.ECManager result = (EOManager.ECManager)
            transientState().valueForKey(ECMANAGER_KEY);
        if (result == null)
        {
            result = new EOManager.ECManager();
            transientState().takeValueForKey(result, ECMANAGER_KEY);
        }
        return result;
    }


    //~ Instance/static variables .............................................

    private CoreSelectionsManager csm;
    private static final String CSM_KEY =
        CoreSelectionsManager.class.getName();
    private static final String ECMANAGER_KEY =
        EOManager.ECManager.class.getName();

    private NSMutableArray<INavigatorObject> courseOfferings;
    private INavigatorObject selectedCourseOffering;

    private NSMutableArray<INavigatorObject> semesters;
    private INavigatorObject selectedSemester;

    private boolean allowsAllSemesters = true;
    private boolean allowsAllOfferingsForCourse = true;

    private Boolean includeWhatImTeaching;
    private Boolean includeAdminAccess;

    private static final String SEMESTER_PREF_KEY = "semester";
    private static final String COURSE_OFFERING_SET_KEY = "courseOfferingSet";

    static Logger log = Logger.getLogger( WCCourseComponent.class );
}
