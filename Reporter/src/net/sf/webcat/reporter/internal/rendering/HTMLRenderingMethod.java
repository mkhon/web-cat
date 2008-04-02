/*==========================================================================*\
 |  $Id: HTMLRenderingMethod.java,v 1.3 2008/04/02 01:36:38 stedwar2 Exp $
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

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import net.sf.webcat.reporter.GeneratedReport;
import net.sf.webcat.reporter.IRenderingMethod;
import net.sf.webcat.reporter.Reporter;
import net.sf.webcat.reporter.ReporterHTMLImageHandler;
import org.apache.log4j.Logger;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;

//-------------------------------------------------------------------------
/**
 * Render method for HTML-viewable reports.
 *
 * @author aallowat
 * @version $Id: HTMLRenderingMethod.java,v 1.3 2008/04/02 01:36:38 stedwar2 Exp $
 */
public class HTMLRenderingMethod
    extends AbstractRenderingMethod
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     * @param engine the reporting engine to use
     */
    public HTMLRenderingMethod(IReportEngine engine)
    {
        super(engine);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public String methodName()
    {
        return "html";
    }

    // ----------------------------------------------------------
    public String displayName()
    {
        return "HTML";
    }

    // ----------------------------------------------------------
    public Controller prepareToRender(
        GeneratedReport report, NSDictionary options)
    {
        IReportDocument document = Reporter.getInstance().openReportDocument(
            report.generatedReportFile());

        String actionUrl = (String)options.objectForKey(OPTION_ACTION_URL);

        HTMLRenderOption option = new HTMLRenderOption();
        option.setEmbeddable(true);
        option.setImageHandler(new ReporterHTMLImageHandler(report,
            actionUrl));
        option.setOutputFileName(GeneratedReport.renderedResourcePath(
            report.uuid(), REPORT_ROOT_HTML));

        IRenderTask task = reportEngine().createRenderTask(document);
        task.setRenderOption(option);

        return new HTMLController(task);
    }


    // ----------------------------------------------------------
    public void appendContentToResponse(
        GeneratedReport report, WOResponse response, WOContext context)
        throws IOException
    {
        String htmlPath = GeneratedReport.renderedResourcePath(
            report.uuid(), REPORT_ROOT_HTML);
        File htmlFile = new File(htmlPath);

        NSData htmlData = new NSData(
            new FileInputStream(htmlFile), (int)htmlFile.length());
        response.appendContentData(htmlData);
    }


    //~ Private Methods/Classes ...............................................

    // ----------------------------------------------------------
    private static class HTMLController
        implements Controller
    {
        //~ Constructor .......................................................

        // ----------------------------------------------------------
        public HTMLController(IRenderTask task)
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

    private static final String REPORT_ROOT_HTML = "report.html";
}
