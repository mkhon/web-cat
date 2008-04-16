/*==========================================================================*\
 |  $Id: ExcelRenderingMethod.java,v 1.7 2008/04/16 20:48:23 aallowat Exp $
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
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import java.io.IOException;
import net.sf.webcat.reporter.GeneratedReport;
import net.sf.webcat.reporter.Reporter;
import net.sf.webcat.reporter.ReporterHTMLImageHandler;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.RenderOption;

//-------------------------------------------------------------------------
/**
 * Render method for Excel data files.
 *
 * @author Tony Allevato
 * @version $Id: ExcelRenderingMethod.java,v 1.7 2008/04/16 20:48:23 aallowat Exp $
 */
public class ExcelRenderingMethod
    extends AbstractRenderingMethod
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     * @param engine the reporting engine to use
     */
    public ExcelRenderingMethod(IReportEngine engine)
    {
        super(engine);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public void appendContentToResponse(
        GeneratedReport report, WOResponse response, WOContext context)
    {
        StringBuilder content = new StringBuilder();
        NSMutableDictionary query = new NSMutableDictionary();
        query.setObjectForKey(report.id(), "reportId");

        String filename = "report.xls";

        query.setObjectForKey(filename, "name");
        query.setObjectForKey("application/msexcel", "contentType");
        String actionUrl = context.directActionURLForActionNamed(
            "reportResource/generic", query);

        content.append("<a href=\"");
        content.append(actionUrl);
        content.append("\">");
        content.append("Download <b>");
        content.append(filename);
        content.append("</b>");
        content.append("</a><br/>");

        response.appendContentString(content.toString());
    }


    // ----------------------------------------------------------
    public String displayName()
    {
        return "Microsoft Excel";
    }


    // ----------------------------------------------------------
    public String methodName()
    {
        return "xls";
    }


    // ----------------------------------------------------------
    public Controller prepareToRender(
        GeneratedReport report, NSDictionary options)
    {
        IReportDocument document = Reporter.getInstance().openReportDocument(
            report.generatedReportFile());

        // String actionUrl = (String)options.objectForKey(OPTION_ACTION_URL);

        String filename = "report.xls";

        RenderOption option = new RenderOption();
        option.setOutputFormat("XLS");
        option.setOutputFileName(report.renderedResourcePath(filename));

        IRenderTask task = reportEngine().createRenderTask(document);
        task.setRenderOption(option);

        return new BasicController(task);
    }
}
