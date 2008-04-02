/*==========================================================================*\
 |  $Id: ConfigureSubsystemPage.java,v 1.3 2008/04/02 00:56:28 stedwar2 Exp $
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

import com.webobjects.appserver.*;
import net.sf.webcat.core.*;

// -------------------------------------------------------------------------
/**
 *  Displays the configuration parameters for a given subsystem and
 *  allows the corresponding settings to be edited.
 *
 *  @author  stedwar2
 *  @version $Id: ConfigureSubsystemPage.java,v 1.3 2008/04/02 00:56:28 stedwar2 Exp $
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
        boolean superResult = super.applyLocalChanges();
        if ( !result )
        {
            warning( "Cannot write to configuration file, so changes have "
                + "not been made permanent." );
        }
        return  superResult && result;
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
