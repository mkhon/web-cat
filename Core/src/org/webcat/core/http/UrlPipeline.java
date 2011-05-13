/*==========================================================================*\
 |  $Id: UrlPipeline.java,v 1.1 2011/05/13 19:46:57 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2011 Virginia Tech
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

package org.webcat.core.http;

import java.io.IOException;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WORequestHandler;
import com.webobjects.appserver.WOResponse;

//-------------------------------------------------------------------------
/**
 * A URL pipeline for handling requests.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2011/05/13 19:46:57 $
 */
public abstract class UrlPipeline
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public UrlPipeline(RequestFilter[] filters,
                       RequestHandlerWithResponse requestHandler)
    {
        this.filters = filters;
        this.requestHandler = requestHandler;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public abstract boolean matches(WORequest request);


    // ----------------------------------------------------------
    public void handleRequest(WORequest request, WOResponse response)
        throws Exception
    {
        if (filters.length > 0)
        {
            new Chain(filters, requestHandler).filterRequest(request, response);
        }
        else
        {
            requestHandler.handleRequest(request, response);
        }
    }


    //~ Inner classes .........................................................

    // ----------------------------------------------------------
    private static class Chain implements RequestFilterChain
    {
        // ----------------------------------------------------------
        public Chain(RequestFilter[] filters,
                     RequestHandlerWithResponse requestHandler)
        {
            this.filters = filters;
            this.requestHandler = requestHandler;
        }


        // ----------------------------------------------------------
        public void filterRequest(WORequest request, WOResponse response)
            throws Exception
        {
            if (filterIndex < filters.length)
            {
                filters[filterIndex++].filterRequest(request, response, this);
            }
            else
            {
                requestHandler.handleRequest(request, response);
            }
        }


        //~ Static/instance variables .........................................

        private RequestFilter[] filters;
        private RequestHandlerWithResponse requestHandler;
        private int filterIndex;
    }

    //~ Static/instance variables .............................................

    private RequestFilter[] filters;
    private RequestHandlerWithResponse requestHandler;
}
