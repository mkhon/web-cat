/*==========================================================================*\
 |  $Id: RequestHandlerBinder.java,v 1.1 2011/05/13 19:46:57 aallowat Exp $
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

import com.webobjects.appserver.WORequestHandler;

//-------------------------------------------------------------------------
/**
 * A binder that can be used to configure the filters and the request handler
 * that service a particular request.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2011/05/13 19:46:57 $
 */
public interface RequestHandlerBinder
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Attaches a filter to the filter chain that will be processed before the
     * final request handler.
     *
     * @param filter the request filter
     * @return this binder, used to chain calls
     */
    public RequestHandlerBinder through(RequestFilter filter);


    // ----------------------------------------------------------
    /**
     * Attaches the request handler that will be used to process the request if
     * all of the filters before it (if any) are satisfied.
     *
     * @param handler the request handler
     */
    public void with(RequestHandlerWithResponse handler);
}
