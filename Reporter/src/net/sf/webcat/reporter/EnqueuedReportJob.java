/*==========================================================================*\
 |  $Id: EnqueuedReportJob.java,v 1.4 2008/04/02 01:36:38 stedwar2 Exp $
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

package net.sf.webcat.reporter;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import net.sf.webcat.core.Application;

// -------------------------------------------------------------------------
/**
 * TODO: place a real description here.
 *
 * @author
 * @version $Id: EnqueuedReportJob.java,v 1.4 2008/04/02 01:36:38 stedwar2 Exp $
 */
public class EnqueuedReportJob
    extends _EnqueuedReportJob
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new EnqueuedReportJob object.
     */
    public EnqueuedReportJob()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve the name of the directory where this submission is stored.
     * @return the directory name
     */
    public String generatedReportDir()
    {
    	return GeneratedReport.generatedReportDirForUser(user());
    }


    // ----------------------------------------------------------
    public String generatedReportFile()
    {
    	return GeneratedReport.generatedReportFilePathForUser(user(), uuid());
    }
}
