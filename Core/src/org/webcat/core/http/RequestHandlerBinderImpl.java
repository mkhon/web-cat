/*==========================================================================*\
 |  $Id: RequestHandlerBinderImpl.java,v 1.1 2011/05/13 19:46:57 aallowat Exp $
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

import java.util.ArrayList;
import com.webobjects.appserver.WOMessage;
import com.webobjects.appserver.WORequestHandler;

//-------------------------------------------------------------------------
/**
 * A concrete implementation of the request handler binder.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2011/05/13 19:46:57 $
 */
public abstract class RequestHandlerBinderImpl implements RequestHandlerBinder
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public RequestHandlerBinderImpl()
    {
        filters = new ArrayList<RequestFilter>();
    }

    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public RequestHandlerBinder through(RequestFilter filter)
    {
        filters.add(filter);
        return this;
    }


    // ----------------------------------------------------------
    public void with(RequestHandlerWithResponse handler)
    {
        if (requestHandler != null)
        {
            throw new IllegalStateException(
                    "Request handler was already bound");
        }

        requestHandler = handler;
    }


    // ----------------------------------------------------------
    public abstract UrlPipeline create();


    // ----------------------------------------------------------
    protected RequestFilter[] filters()
    {
        return filters.toArray(new RequestFilter[filters.size()]);
    }


    // ----------------------------------------------------------
    protected RequestHandlerWithResponse requestHandler()
    {
        if (requestHandler != null)
        {
            return requestHandler;
        }
        else
        {
            return new ErrorRequestHandler(WOMessage.HTTP_STATUS_NOT_FOUND);
        }
    }


    //~ Static/instance variables .............................................

    private ArrayList<RequestFilter> filters;
    private RequestHandlerWithResponse requestHandler;
}
