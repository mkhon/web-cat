/*==========================================================================*\
 |  $Id: NoCacheRequestFilter.java,v 1.2 2012/03/28 13:48:08 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2011-2012 Virginia Tech
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

package org.webcat.core.git.http;

import org.webcat.core.http.RequestFilter;
import org.webcat.core.http.RequestFilterChain;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;

//-------------------------------------------------------------------------
/**
 * A filter that appends headers to the response to prevent the client from
 * caching it.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.2 $, $Date: 2012/03/28 13:48:08 $
 */
public class NoCacheRequestFilter
    implements RequestFilter
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Appends headers to the response to prevent the client from caching it.
     *
     * @param request the request
     * @param response the response
     * @param filterChain the filter chain
     * @throws Exception if an error occurs
     */
    public void filterRequest(WORequest request, WOResponse response,
            RequestFilterChain filterChain) throws Exception
    {
        response.disableClientCaching();
        filterChain.filterRequest(request, response);
    }
}
