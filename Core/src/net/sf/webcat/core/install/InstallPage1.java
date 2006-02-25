/*==========================================================================*\
 |  $Id: InstallPage1.java,v 1.2 2006/02/25 07:58:07 stedwar2 Exp $
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

package net.sf.webcat.core.install;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import net.sf.webcat.core.*;

import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * Implements the login UI functionality of the system.
 *
 *  @author Stephen Edwards
 *  @version $Id: InstallPage1.java,v 1.2 2006/02/25 07:58:07 stedwar2 Exp $
 */
public class InstallPage1
    extends InstallPage
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new PreCheckPage object.
     * 
     * @param context The context to use
     */
    public InstallPage1( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................



    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public int stepNo()
    {
        return 1;
    }


    // ----------------------------------------------------------
    public void setDefaultConfigValues( WCConfigurationFile configuration )
    {
        if ( configuration.getProperty( "installComplete" ) != null )
        {
            configuration.remove( "installComplete" );
        }
        String osname = configuration.getProperty( "os.name" );
        if ( osname.toLowerCase().startsWith( "windows" ) )
        {
            configuration.setProperty( "coreCmdShell", "cmd /c" );
        }
        else
        {
            configuration.setProperty( "coreCmdShell", "" );
        }
        super.setDefaultConfigValues( configuration );
    }


    // ----------------------------------------------------------
    public boolean configExists()
    {
        return Application.configurationProperties().exists();
    }


    // ----------------------------------------------------------
    public String configLocation()
    {
        try
        {
            return Application.configurationProperties().file()
                .getCanonicalPath();
        }
        catch ( java.io.IOException e )
        {
            log.error( "exception looking up configuration file location:", e );
            return e.getMessage();
        }
    }


    // ----------------------------------------------------------
    public boolean configIsWriteable()
    {
        return Application.configurationProperties().isWriteable();
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( InstallPage1.class );
}
