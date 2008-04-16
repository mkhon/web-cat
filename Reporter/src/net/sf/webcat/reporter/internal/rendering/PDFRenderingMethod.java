/*==========================================================================*\
 |  $Id: PDFRenderingMethod.java,v 1.1 2008/04/16 20:48:23 aallowat Exp $
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import net.sf.webcat.reporter.GeneratedReport;
import net.sf.webcat.reporter.Reporter;
import net.sf.webcat.reporter.ReporterHTMLImageHandler;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

//-------------------------------------------------------------------------
/**
 * Render method for PDF reports.
 *
 * @author Tony Allevato
 * @version $Id: PDFRenderingMethod.java,v 1.1 2008/04/16 20:48:23 aallowat Exp $
 */
public class PDFRenderingMethod extends AbstractRenderingMethod
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     * @param engine the reporting engine to use
     */
    public PDFRenderingMethod(IReportEngine engine)
    {
        super(engine);
    }


    //~ Public methods ........................................................

    // ----------------------------------------------------------
    public String methodName()
    {
        return "pdf";
    }


    // ----------------------------------------------------------
    public String displayName()
    {
        return "PDF";
    }


    // ----------------------------------------------------------
    public void appendContentToResponse(GeneratedReport report,
            WOResponse response, WOContext context) throws IOException
    {
        NSMutableDictionary query = new NSMutableDictionary();
        query.setObjectForKey(report.id(), "reportId");
        query.setObjectForKey(REPORT_PDF, "name");
        query.setObjectForKey(PDF_CONTENT_TYPE, "contentType");
        query.setObjectForKey(Boolean.FALSE.toString(), "inline");
        query.setObjectForKey(report.description() + ".pdf", "deliveredName");

        String downloadUrl = context.directActionURLForActionNamed(
            "reportResource/generic", query);

        StringBuffer buffer = new StringBuffer(256);

        buffer.append("<p>");
        buffer.append("<a href=\"");
        buffer.append(downloadUrl);
        buffer.append("\">");
        buffer.append("Click here to download the report shown below.");
        buffer.append("</a></p>");

        query = new NSMutableDictionary();
        query.setObjectForKey(report.id(), "reportId");
        query.setObjectForKey(REPORT_PDF, "name");
        query.setObjectForKey(PDF_CONTENT_TYPE, "contentType");
        query.setObjectForKey(Boolean.TRUE.toString(), "inline");

        String embedUrl = context.directActionURLForActionNamed(
            "reportResource/generic", query);

        buffer.append("<embed src=\"");
        buffer.append(embedUrl);
        buffer.append("\" width=\"100%\" style=\"height: 5in\">");
        buffer.append("</embed>");

        response.appendContentString(buffer.toString());
    }


    // ----------------------------------------------------------
    public Controller prepareToRender(GeneratedReport report,
            NSDictionary options)
    {
        IReportDocument document = Reporter.getInstance().openReportDocument(
                report.generatedReportFile());

//        String actionUrl = (String)options.objectForKey(OPTION_ACTION_URL);

        PDFRenderOption option = new PDFRenderOption();
        option.setOutputFormat("PDF");
        option.setOutputFileName(report.renderedResourcePath(REPORT_PDF));

        IRenderTask task = reportEngine().createRenderTask(document);
        task.setRenderOption(option);

        return new BasicController(task);
    }


    //~ Instance/static variables .............................................

    private static final String REPORT_PDF = "report.pdf";

    private static final String PDF_CONTENT_TYPE = "application/pdf";
}
