/*==========================================================================*\
 |  $Id: PopUpInfo.java,v 1.4 2009/05/25 16:51:20 aallowat Exp $
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

package net.sf.webcat.core;

import com.webobjects.appserver.*;
import net.sf.webcat.ui.util.ComponentIDGenerator;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * A component that renders as an info icon and an overlib-based pop-up
 * info/help message.
 *
 * @author Stephen Edwards
 * @version $Id: PopUpInfo.java,v 1.4 2009/05/25 16:51:20 aallowat Exp $
 */
public class PopUpInfo
    extends WOComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     *
     * @param context The page's context
     */
    public PopUpInfo( WOContext context )
    {
        super( context );
    }

    //~ KVC attributes (must be public) .......................................

    public ComponentIDGenerator idFor;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        idFor = new ComponentIDGenerator(this);
        
        super.appendToResponse(response, context);
    }
    
    
    // ----------------------------------------------------------
    public boolean isStateless()
    {
        return true;
    }


    // ----------------------------------------------------------
    public String message()
    {
        return (String)valueForBinding( "message" );
    }


    // ----------------------------------------------------------
    public String title()
    {
        String title = "Information";
        if ( hasBinding( "title" ) )
        {
            title = (String)valueForBinding( "title" );
        }
        return title;
    }
}
