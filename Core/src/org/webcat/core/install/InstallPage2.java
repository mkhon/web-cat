/*==========================================================================*\
 |  $Id: InstallPage2.java,v 1.1 2010/05/11 14:51:58 aallowat Exp $
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

package org.webcat.core.install;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.ERXValueUtilities;

import org.webcat.core.Application;

import org.apache.log4j.Logger;
import org.webcat.core.*;

// -------------------------------------------------------------------------
/**
 * Implements the login UI functionality of the system.
 *
 *  @author Stephen Edwards
 *  @version $Id: InstallPage2.java,v 1.1 2010/05/11 14:51:58 aallowat Exp $
 */
public class InstallPage2
    extends InstallPage
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new PreCheckPage object.
     *
     * @param context The context to use
     */
    public InstallPage2( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................



    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public int stepNo()
    {
        return 2;
    }


    // ----------------------------------------------------------
    public String licenseURL()
    {
        return Application.completeURLWithRequestHandlerKey(
            context(),
            "wa",
            "install/license",
            null,
            false,
            0
            );
    }


    // ----------------------------------------------------------
    public void takeValuesFromRequest( WORequest request, WOContext context )
    {
        takeFormValues( request.formValues() );
        super.takeValuesFromRequest( request, context );
    }


    // ----------------------------------------------------------
    public void takeFormValues( NSDictionary formValues )
    {
        if ( !ERXValueUtilities.booleanValue(
            extractFormValue( formValues, "AgreedToLicense" ) ) )
        {
            error( "You must agree to the license terms and "
                + "conditions to proceed." );
        }
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( InstallPage2.class );
}
