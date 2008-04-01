/*==========================================================================*\
 |  $Id: EnqueuedReportJob.java,v 1.3 2008/04/01 02:26:27 stedwar2 Exp $
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

package net.sf.webcat.reporter;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import net.sf.webcat.core.Application;

// -------------------------------------------------------------------------
/**
 * TODO: place a real description here.
 *
 * @author
 * @version $Id: EnqueuedReportJob.java,v 1.3 2008/04/01 02:26:27 stedwar2 Exp $
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
