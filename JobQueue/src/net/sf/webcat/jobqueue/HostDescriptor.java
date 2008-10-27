/*==========================================================================*\
 |  $Id: HostDescriptor.java,v 1.1 2008/10/27 01:53:16 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006 Virginia Tech
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

package net.sf.webcat.jobqueue;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

// -------------------------------------------------------------------------
/**
 * Represents and identifies a Web-CAT host within the cluster of
 * servers operating on a single shared database.
 *
 * @author
 * @version $Id: HostDescriptor.java,v 1.1 2008/10/27 01:53:16 stedwar2 Exp $
 */
public class HostDescriptor
    extends _HostDescriptor
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new HostDescriptor object.
     */
    public HostDescriptor()
    {
        super();
    }


    //~ Methods ...............................................................

}
