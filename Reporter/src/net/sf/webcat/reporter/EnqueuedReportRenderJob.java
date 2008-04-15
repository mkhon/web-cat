/*==========================================================================*\
 |  $Id: EnqueuedReportRenderJob.java,v 1.1 2008/04/15 04:09:22 aallowat Exp $
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

package net.sf.webcat.reporter;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

// -------------------------------------------------------------------------
/**
 * Represents the rendering of a report. The generation of a report is handled
 * by the {@link EnqueuedReportGenerationJob} class.
 *
 * @author Tony Allevato
 * @version $Id: EnqueuedReportRenderJob.java,v 1.1 2008/04/15 04:09:22 aallowat Exp $
 */
public class EnqueuedReportRenderJob
    extends _EnqueuedReportRenderJob
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new EnqueuedReportRenderJob object.
     */
    public EnqueuedReportRenderJob()
    {
        super();
    }


    //~ Methods ...............................................................

}
