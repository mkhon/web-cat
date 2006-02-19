/*==========================================================================*\
 |  $Id: Log4JConfigurationPage.java,v 1.1 2006/02/19 19:06:50 stedwar2 Exp $
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
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.sf.webcat.core.*;

import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * A component for managing log4J settings.
 *
 *  @author edwards
 *  @version $Id: Log4JConfigurationPage.java,v 1.1 2006/02/19 19:06:50 stedwar2 Exp $
 */
public class Log4JConfigurationPage
    extends er.extensions.ERXLog4JConfiguration
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new Settings object.
     * 
     * @param context The context to use
     */
    public Log4JConfigurationPage( WOContext context )
    {
        super( context );
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse( WOResponse response, WOContext context )
    {
        session().setObjectForKey(
            Boolean.TRUE, "ERXLog4JConfiguration.enabled" );
        super.appendToResponse( response, context );
    }


    // ----------------------------------------------------------
    public WOComponent back()
    {
        return pageWithName( SettingsPage.class.getName() );
    }
}
