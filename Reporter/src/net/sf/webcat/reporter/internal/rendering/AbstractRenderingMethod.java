/*==========================================================================*\
 |  $Id: AbstractRenderingMethod.java,v 1.5 2008/04/16 20:48:23 aallowat Exp $
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

package net.sf.webcat.reporter.internal.rendering;

import net.sf.webcat.reporter.IRenderingMethod;
import net.sf.webcat.reporter.IRenderingMethod.Controller;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportEngine;

//-------------------------------------------------------------------------
/**
 * Abstract base class for rendering methods.
 *
 * @author Tony Allevato
 * @version $Id: AbstractRenderingMethod.java,v 1.5 2008/04/16 20:48:23 aallowat Exp $
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


    //~ Protected Methods/Classes .............................................

    // ----------------------------------------------------------
    protected IReportEngine reportEngine()
    {
        return reportEngine;
    }


    // ----------------------------------------------------------
    /**
     * A basic rendering controller that simply delegates operations to the
     * BIRT {@link IRenderTask}.
     */
    protected static class BasicController
        implements Controller
    {
        //~ Constructor .......................................................

        // ----------------------------------------------------------
        public BasicController(IRenderTask task)
        {
            this.task = task;
        }


        //~ Public Methods ....................................................

        // ----------------------------------------------------------
        public void render() throws Exception
        {
            org.mozilla.javascript.Context.enter();
            task.render();
            org.mozilla.javascript.Context.exit();

            task.close();
        }


        // ----------------------------------------------------------
        public void cancel()
        {
            task.cancel();
        }


        //~ Instance/static variables .........................................

        private IRenderTask task;
    }


    //~ Instance/static variables .............................................

    private IReportEngine reportEngine;
}
