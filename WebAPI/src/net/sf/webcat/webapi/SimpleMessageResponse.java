/*==========================================================================*\
 |  $Id: SimpleMessageResponse.java,v 1.2 2008/10/27 01:48:55 stedwar2 Exp $
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

package net.sf.webcat.webapi;

import com.webobjects.appserver.WOContext;

//-------------------------------------------------------------------------
/**
 * The basic message page for returning single message responses.  The
 * default is an "invalid request" message.
 *
 * @author Stephen Edwards
 * @version $Id: SimpleMessageResponse.java,v 1.2 2008/10/27 01:48:55 stedwar2 Exp $
 */
public class SimpleMessageResponse
extends XmlResponsePage
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new page.
     *
     * @param context The page's context
     */
    public SimpleMessageResponse(WOContext context)
    {
        super(context);
    }


    //~ KVC Properties ........................................................

    public String message = "Invalid request";
    public String elementName = "error";
}
