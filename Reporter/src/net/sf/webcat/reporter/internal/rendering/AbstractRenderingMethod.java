/*==========================================================================*\
 |  $Id: AbstractRenderingMethod.java,v 1.2 2008/03/31 01:31:49 stedwar2 Exp $
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

package net.sf.webcat.reporter.internal.rendering;

import net.sf.webcat.reporter.IRenderingMethod;
import org.eclipse.birt.report.engine.api.IReportEngine;

//-------------------------------------------------------------------------
/**
 * Abstract base class for rendering methods.
 *
 * @author aallowat
 * @version $Id: AbstractRenderingMethod.java,v 1.2 2008/03/31 01:31:49 stedwar2 Exp $
 */
public abstract class AbstractRenderingMethod
    implements IRenderingMethod
{
    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public AbstractRenderingMethod(IReportEngine engine)
    {
        reportEngine = engine;
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    protected IReportEngine reportEngine()
    {
        return reportEngine;
    }


    //~ Instance/static variables .............................................

    private IReportEngine reportEngine;
}
