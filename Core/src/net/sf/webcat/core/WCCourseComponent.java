/*==========================================================================*\
 |  $Id: WCCourseComponent.java,v 1.11 2009/05/29 19:17:00 stedwar2 Exp $
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
 * @author  latest changes by: $Author: stedwar2 $
 * @version $Revision: 1.11 $ $Date: 2009/05/29 19:17:00 $
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
    /**
     * This method determines whether any embedded navigator will
     * automatically pop up to force a selection and page reload.
     * The default implementation simply returns false, but is designed
     * to be overridden in subclasses.
     * @return True if the navigator should start out by opening automatically.
     */
    public boolean forceNavigatorSelection()
    {
        if (!allowsAllOfferingsForCourse()
            && coreSelections().courseOffering() == null)
        {
            // Try to guess a course offering from the course
            CourseOffering bestOffering = bestMatchingCourseOffering();
            if (bestOffering != null)
            {
                coreSelections().setCourseOfferingRelationship(
                    bestOffering);
                return false;
            }
            else
            {
                return true;
            }
        }
        return false;
    }


    // ----------------------------------------------------------
    /**
     * This method determines whether any embedded navigator will
     * allow users to select "all" offerings for a course.
     * The default implementation returns true, but is designed
     * to be overridden in subclasses.
     * @return True if the navigator should allow selection of all courses.
     */
    protected CourseOffering bestMatchingCourseOffering()
    {
        CourseOffering bestOffering = coreSelections().courseOffering();
        if (bestOffering == null)
        {
            Course course = coreSelections().course();
            if (course != null)
            {
                for (CourseOffering offering : user().enrolledIn())
                {
                    if (offering.course() == course)
                    {
                        bestOffering = offering;
                        break;
                    }
                }
                if (bestOffering == null)
                {
                    for (CourseOffering offering : user().teaching())
                    {
                        if (offering.course() == course)
                        {
                            bestOffering = offering;
                            break;
                        }
                    }
                }
                if (bestOffering == null)
                {
                    for (CourseOffering offering : user().graderFor())
                    {
                        if (offering.course() == course)
                        {
                            bestOffering = offering;
                            break;
                        }
                    }
                }
            }
        }
        return bestOffering;
    }


    // ----------------------------------------------------------
    /**
     * This method determines whether any embedded navigator will
     * allow users to select "all" offerings for a course.
     * The default implementation returns true, but is designed
     * to be overridden in subclasses.
     * @return True if the navigator should allow selection of all courses.
     */
    public boolean allowsAllOfferingsForCourse()
    {
        return true;
    }


    // ----------------------------------------------------------
    /**
     * This method determines whether any embedded navigator will
     * allow users to select "all" semesters.
     * The default implementation returns true, but is designed
     * to be overridden in subclasses.
     * @return True if the navigator should allow viewing of all semesters.
     */
    public boolean allowsAllSemesters()
    {
        return true;
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

    static Logger log = Logger.getLogger( WCCourseComponent.class );
}
