/*==========================================================================*\
 |  $Id: Admin.java,v 1.4 2008/04/02 00:56:33 stedwar2 Exp $
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

package net.sf.webcat.admin;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import net.sf.webcat.core.*;

// -------------------------------------------------------------------------
/**
 *  The subsystem defining Web-CAT administrative tasks.
 *
 *  @author Stephen Edwards
 *  @version $Id: Admin.java,v 1.4 2008/04/02 00:56:33 stedwar2 Exp $
 */
public class Admin
    extends Subsystem
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new Admin subsystem object.
     */
    public Admin()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Initialize the subsystem-specific session data in a newly created
     * session object.  This method is called once by the core for
     * each newly created session object.
     *
     * @param s The new session object
     */
    public void initializeSessionData( Session s )
    {
        s.tabs.mergeClonedChildren( subsystemTabTemplate );
    }

    // ----------------------------------------------------------
    /* (non-Javadoc)
     * @see net.sf.webcat.core.Subsystem#init()
     */
    public void init()
    {
        super.init();
        // TODO merge the tab template loading support into the Subsystem
        // base class
        {
            NSBundle myBundle = NSBundle.bundleForClass( Admin.class );
            subsystemTabTemplate = TabDescriptor.tabsFromPropertyList(
                new NSData ( myBundle.bytesForResourcePath(
                                 TabDescriptor.TAB_DEFINITIONS ) ) );
        }
    }

    //~ Instance/static variables .............................................

    // TODO: this should be refactored into the Subsystem parent class,
    // but that means handling Core in an appropriate way.
    private static NSArray subsystemTabTemplate;
}
