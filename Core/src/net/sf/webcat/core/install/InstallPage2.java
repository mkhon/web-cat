/*==========================================================================*\
 |  $Id: InstallPage2.java,v 1.1 2006/02/19 19:03:08 stedwar2 Exp $
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

import er.extensions.*;

import net.sf.webcat.core.*;

import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * Implements the login UI functionality of the system.
 *
 *  @author Stephen Edwards
 *  @version $Id: InstallPage2.java,v 1.1 2006/02/19 19:03:08 stedwar2 Exp $
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
            errorMessage( "You must agree to the license terms and "
                + "conditions to proceed." );
        }
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( InstallPage2.class );
}
