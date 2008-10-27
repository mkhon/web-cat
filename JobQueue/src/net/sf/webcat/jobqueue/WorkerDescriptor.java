/*==========================================================================*\
 |  $Id: WorkerDescriptor.java,v 1.1 2008/10/27 01:53:16 stedwar2 Exp $
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
 * Represents and identifies a single {@link WorkerThread} on a single
 * Web-CAT host.  All worker threads that are going to run clustered
 * against a database-backed job queue should have a corresponding
 * WorkerDescriptor.
 *
 * @author
 * @version $Id: WorkerDescriptor.java,v 1.1 2008/10/27 01:53:16 stedwar2 Exp $
 */
public class WorkerDescriptor
    extends _WorkerDescriptor
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new WorkerDescriptor object.
     */
    public WorkerDescriptor()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Determine whether this worker is currently blocked, because
     * another worker on the same host is currently processing a job
     * that requires exclusive host access.
     * @return True if this worker is currently blocked
     */
    public boolean isBlocked()
    {
        // TODO: implement this!
        return false;
    }
}
