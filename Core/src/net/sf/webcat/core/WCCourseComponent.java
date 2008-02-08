/*==========================================================================*\
 |  $Id: WCCourseComponent.java,v 1.1 2008/02/08 19:36:03 stedwar2 Exp $
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

import com.webobjects.appserver.*;

import org.apache.log4j.*;

//-------------------------------------------------------------------------
/**
 * This specialized subclass of WCComponent represents pages that have
 * a notion of a currently-selected course offering and/or course.
 *
 * @author Stephen Edwards
 * @version $Id: WCCourseComponent.java,v 1.1 2008/02/08 19:36:03 stedwar2 Exp $
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
        super.awake();
        log.debug("awake(): begin");
        if (csm == null)
        {
            csm = new CoreSelectionsManager(
                wcSession().localUser().getCoreSelections(),
                ecManager());
        }
        log.debug("awake(): end");
    }


    // ----------------------------------------------------------
    /**
     * Access the user's current core selections.
     * @return the core selections manager for this page
     */
    public CoreSelectionsManager coreSelections()
    {
        return csm;
    }


    // ----------------------------------------------------------
    @Override
    public WOComponent pageWithName( String name )
    {
        WOComponent result = super.pageWithName( name );
        if (result instanceof WCCourseComponent && csm != null)
        {
            ((WCCourseComponent)result).csm =
                (CoreSelectionsManager)csm.clone();
        }
        return result;
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    private IndependentEOManager.ECManager ecManager()
    {
        IndependentEOManager.ECManager result = (IndependentEOManager.ECManager)
            wcSession().transientState().valueForKey(KEY);
        if (result == null)
        {
            result = new IndependentEOManager.ECManager();
            wcSession().transientState().takeValueForKey(result, KEY);
        }
        return result;
    }


    //~ Instance/static variables .............................................

    private CoreSelectionsManager csm;
    private static final String KEY = CoreSelectionsManager.class.getName();

    static Logger log = Logger.getLogger( WCCourseComponent.class );
}
