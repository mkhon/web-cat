/*==========================================================================*\
 |  $Id: InstallPageWrapper.java,v 1.2 2006/02/25 07:58:07 stedwar2 Exp $
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
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.sf.webcat.core.*;

import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * Implements the login UI functionality of the system.
 *
 *  @author Stephen Edwards
 *  @version $Id: InstallPageWrapper.java,v 1.2 2006/02/25 07:58:07 stedwar2 Exp $
 */
public class InstallPageWrapper
    extends WOComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new PreCheckPage object.
     * 
     * @param context The context to use
     */
    public InstallPageWrapper( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public String       title;
    public int          stepNo = -1;
    public int          index;
    public String       step;
    public String[]     stepList = install.steps;
    public NSDictionary errors;
    public String       stylesheet;
    public String       externalJavascript;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public String imageFileName()
    {
        String name = "images/install-cat" + ( index + 1 );
        if ( stepNo != index + 1 )
        {
            name += "-off";
        }
        return  name + ".gif";
    }


    // ----------------------------------------------------------
    public String stepClass()
    {
        return ( stepNo == index + 1) ? "step-on" : "step-off";
    }


    // ----------------------------------------------------------
    public String nextButtonLabel()
    {
        return ( stepNo == install.steps.length )
            ? " Login > " // " Site Administration > "
            : " Next > ";
    }


    // ----------------------------------------------------------
    public boolean backEnabled()
    {
        return 1 < stepNo  &&  stepNo < install.steps.length;
    }


    // ----------------------------------------------------------
    public void takeValuesFromRequest( WORequest arg0, WOContext arg1 )
    {
        log.debug( "takeValuesFromRequest: " + arg0.formValues() );
        super.takeValuesFromRequest( arg0, arg1 );
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( InstallPageWrapper.class );
}
