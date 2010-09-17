/*==========================================================================*\
 |  $Id: Log4JConfigurationPage.java,v 1.1 2010/05/11 14:51:43 aallowat Exp $
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

package org.webcat.admin;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import org.apache.log4j.Logger;
import org.webcat.core.*;

// -------------------------------------------------------------------------
/**
 * A component for managing log4J settings.
 *
 *  @author edwards
 *  @version $Id: Log4JConfigurationPage.java,v 1.1 2010/05/11 14:51:43 aallowat Exp $
 */
public class Log4JConfigurationPage
    extends er.extensions.logging.ERXLog4JConfiguration
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