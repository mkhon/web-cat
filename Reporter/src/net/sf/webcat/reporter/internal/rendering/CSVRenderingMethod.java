/*==========================================================================*\
 |  $Id: CSVRenderingMethod.java,v 1.4 2008/04/15 04:09:22 aallowat Exp $
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.birt.data.engine.core.DataException;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IDataExtractionTask;
import org.eclipse.birt.report.engine.api.IDataIterator;
import org.eclipse.birt.report.engine.api.IExtractionResults;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IResultMetaData;
import org.eclipse.birt.report.engine.api.IResultSetItem;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

import net.sf.webcat.reporter.GeneratedReport;
import net.sf.webcat.reporter.IRenderingMethod;
import net.sf.webcat.reporter.Reporter;
import net.sf.webcat.reporter.ReporterHTMLImageHandler;

//-------------------------------------------------------------------------
/**
 * Render method for CSV data files (comma-separated values).
 *
 * @author Tony Allevato
 * @version $Id: CSVRenderingMethod.java,v 1.4 2008/04/15 04:09:22 aallowat Exp $
 */
public class CSVRenderingMethod
    extends AbstractRenderingMethod
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     * @param engine the reporting engine to use
     */
    public CSVRenderingMethod(IReportEngine engine)
    {
        super(engine);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public String methodName()
    {
        return "csv";
    }


    // ----------------------------------------------------------
    public String displayName()
    {
        return "Comma-Separated Values";
    }


    // ----------------------------------------------------------
    public Controller prepareToRender(
        GeneratedReport report, NSDictionary options)
    {
        IReportDocument document = Reporter.getInstance().openReportDocument(
            report.generatedReportFile());

        IDataExtractionTask task =
            reportEngine().createDataExtractionTask(document);

        return new CSVController(task, report);
    }


    // ----------------------------------------------------------
    public void appendContentToResponse(
        GeneratedReport report, WOResponse response, WOContext context)
        throws IOException
    {
        // The CSV renderer only renders hyperlinks to the CSV files as its
        // content. These hyperlinks are direct actions that request the
        // appropriate CSV file.

        StringBuilder content = new StringBuilder();
        NSMutableDictionary query = new NSMutableDictionary();
        query.setObjectForKey(report.id(), "reportId");

        String indexPath = report.renderedResourcePath(INDEX_FILE);
        BufferedReader reader = new BufferedReader(new FileReader(indexPath));
        String resultSetName;

        while ((resultSetName = reader.readLine()) != null)
        {
            query.setObjectForKey(resultSetName, "name");
            String actionUrl = context.directActionURLForActionNamed(
                "reportResource/csv", query);

            content.append("<a href=\"");
            content.append(actionUrl);
            content.append("\">");
            content.append("Download <b>");
            content.append(resultSetName);
            content.append(".csv");
            content.append("</b>");
            content.append("</a><br/>");
        }

        response.appendContentString(content.toString());

        reader.close();
    }


    //~ Private Methods/Classes ...............................................

    // ----------------------------------------------------------
    private static class CSVController
        implements Controller
    {
        //~ Constructor .......................................................

        // ----------------------------------------------------------
        public CSVController(IDataExtractionTask task, GeneratedReport report)
        {
            this.task = task;
            this.report = report;
        }


        //~ Public Methods ....................................................

        // ----------------------------------------------------------
        public void render()
            throws Exception
        {
            String indexPath = report.renderedResourcePath(INDEX_FILE);
            PrintWriter indexWriter = new PrintWriter(indexPath);

            List resultSets = task.getResultSetList();
            for (int i = 0; i < resultSets.size(); i++)
            {
                renderResultSet(indexWriter, (IResultSetItem)resultSets.get(i));
            }

            indexWriter.close();
            task.close();
        }


        // ----------------------------------------------------------
        public void cancel()
        {
            task.cancel();
        }


        //~ Private Methods ...................................................

        // ----------------------------------------------------------
        private void renderResultSet(
            PrintWriter indexWriter, IResultSetItem resultSet)
            throws Exception
        {
            String name = resultSet.getResultSetName();
            task.selectResultSet(name);
            IExtractionResults extraction = task.extract();

            if (extraction != null)
            {
                indexWriter.println(name);

                // Create the CSV file.
                String csvPath = report.renderedResourcePath(name + ".csv");
                File csvFile = new File(csvPath);
                PrintWriter writer = new PrintWriter(csvFile);

                // Write the column names to the first line of the file.
                IResultMetaData metadata = extraction.getResultMetaData();
                int columnCount = metadata.getColumnCount();
                writer.print('"');
                writer.print(metadata.getColumnLabel(0));
                writer.print('"');
                for (int i = 1; i < columnCount; i++)
                {
                    writer.print(",\"");
                    writer.print(metadata.getColumnLabel(i));
                    writer.print('"');
                }
                writer.println();

                // Write the values for each row into the CSV file.
                IDataIterator it = extraction.nextResultIterator();
                if (it != null)
                {
                    while (it.next())
                    {
                        writer.print(getColumnValue(it, 0));

                        for (int i = 1; i < columnCount; i++)
                        {
                            writer.print(',');
                            writer.print(getColumnValue(it, i));
                        }

                        writer.println();
                    }

                    it.close();
                }

                writer.close();
                extraction.close();
            }
        }


        // ----------------------------------------------------------
        private String getColumnValue(IDataIterator it, int index)
        {
            String value;

            try
            {
                value = it.getValue(index).toString();
                value = "\"" + value.replaceAll("\\\"", "\\\"\\\"") + "\"";
            }
            catch (Exception e)
            {
                value = "";
            }

            return value;
        }


        //~ Instance/static variables .........................................

        private IDataExtractionTask task;
        private GeneratedReport report;
    }


    //~ Instance/static variables .............................................

    private static final String INDEX_FILE = ".resultSetIndex";
}
