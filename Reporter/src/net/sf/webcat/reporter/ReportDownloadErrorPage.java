package net.sf.webcat.reporter;

import java.io.PrintWriter;
import java.io.StringWriter;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

public class ReportDownloadErrorPage extends ReporterComponent
{
    public ReportDownloadErrorPage(WOContext context)
    {
        super(context);
    }
    
    
    public Throwable throwable;
    public GeneratedReport generatedReport;
    
    
    public String mainBlockTitle()
    {
        return "Error Downloading Report: " + generatedReport.description();
    }
    
    
    public String throwableStackTrace()
    {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }


    public WOActionResults goBack()
    {
        setLocalGeneratedReport(generatedReport);
        GeneratedReportPage page = (GeneratedReportPage) pageWithName(
                GeneratedReportPage.class.getName());
        return page;
    }
}
