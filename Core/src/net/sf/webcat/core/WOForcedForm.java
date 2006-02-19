/*==========================================================================*\
 |  $Id: WOForcedForm.java,v 1.1 2006/02/19 19:03:09 stedwar2 Exp $
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

package net.sf.webcat.core;

import com.webobjects.foundation.*;
import com.webobjects.appserver.*;
import er.extensions.*;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 *  This experimental class is just an attempt at creating a form
 *  component that always emits its tag, since form detection appears
 *  to fail in some circumstances; use with extreme caution.
 *
 *  @author  stedwar2
 *  @version $Id: WOForcedForm.java,v 1.1 2006/02/19 19:03:09 stedwar2 Exp $
 */
public class WOForcedForm
    extends ERXWOForm
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public WOForcedForm( String name,
                         NSDictionary associations,
                         WOElement template )
    {
        super( name, associations, template );
    }
    

    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse( WOResponse response, WOContext context )
    {
        log.debug( "inForm = " + context.isInForm() );
        log.debug( "elementName = " + elementName() );
        context.setInForm( false );
        super.appendToResponse( response, context );
    }    


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( WOForcedForm.class );
}
