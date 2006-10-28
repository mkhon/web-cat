/*==========================================================================*\
 |  $Id: ConfigureSubsystemPage.java,v 1.1 2006/10/28 20:49:10 stedwar2 Exp $
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

package net.sf.webcat.admin;

import com.webobjects.appserver.*;

import net.sf.webcat.core.*;

// -------------------------------------------------------------------------
/**
 *  Displays the configuration parameters for a given subsystem and
 *  allows the corresponding settings to be edited.
 *
 *  @author  stedwar2
 *  @version $Id: ConfigureSubsystemPage.java,v 1.1 2006/10/28 20:49:10 stedwar2 Exp $
 */
public class ConfigureSubsystemPage
    extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new EditSubsystemConfigurationPage object.
     * 
     * @param context The context to use
     */
    public ConfigureSubsystemPage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public Subsystem subsystem;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /* (non-Javadoc)
     * @see net.sf.webcat.core.WCComponent#applyLocalChanges()
     */
    public boolean applyLocalChanges()
    {
        boolean result = Application.configurationProperties().attemptToSave();
        Application.configurationProperties().updateToSystemProperties();
        ( (Application)application() ).subsystemManager()
            .clearSubsystemPropertyCache();
        if ( !result )
        {
            errorMessage( "Cannot save configuration file, so changes have "
                + "not been made permanent." );
        }
        return super.applyLocalChanges() && result;
    }


    // ----------------------------------------------------------
    public boolean nextEnabled()
    {
        return false;
    }


    // ----------------------------------------------------------
    public boolean backEnabled()
    {
        return false;
    }


    // ----------------------------------------------------------
    public WOComponent cancel()
    {
        cancelLocalChanges();
        return nextPage;
    }


    // ----------------------------------------------------------
    public WOComponent finish()
    {
        if ( applyLocalChanges() )
        {
            return nextPage;
        }
        else
        {
            return null;
        }
    }
    

    // ----------------------------------------------------------
    public WOComponent defaultAction()
    {
        return null;
    }
}
