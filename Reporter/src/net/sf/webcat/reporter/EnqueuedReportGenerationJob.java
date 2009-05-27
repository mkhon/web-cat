/*==========================================================================*\
 |  $Id: EnqueuedReportGenerationJob.java,v 1.3 2009/05/27 14:31:52 aallowat Exp $
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
 * Represents the generation phase of a report in Web-CAT. The rendering phase
 * is handled by the {@link EnqueuedReportRenderJob} entity.
 *
 * @author Tony Allevato
 * @version $Id: EnqueuedReportGenerationJob.java,v 1.3 2009/05/27 14:31:52 aallowat Exp $
 */
public class EnqueuedReportGenerationJob
    extends _EnqueuedReportGenerationJob
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new EnqueuedReportGenerationJob object.
     */
    public EnqueuedReportGenerationJob()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets the percentage of progress made on this job while it is being
     * generated.
     * 
     * @return the progress percentage, an integer between 0 and 100
     */
    public int progressPercentage()
    {
        float workDone = ReportGenerationTracker.getInstance().
            fractionOfWorkDoneForJobId(id().intValue());

        return (int)Math.floor(workDone * 100 + 0.5);
    }
}
