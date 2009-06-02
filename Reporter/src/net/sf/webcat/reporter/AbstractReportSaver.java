package net.sf.webcat.reporter;

import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import net.sf.webcat.birtruntime.BIRTRuntime;
import net.sf.webcat.core.DeliverFile;

public abstract class AbstractReportSaver
{
    //~ Constructors ..........................................................
    
    // ----------------------------------------------------------
    public AbstractReportSaver(GeneratedReport report)
    {
        this.report = report;
        document = report.openReportDocument();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void close()
    {
        document.close();
    }
    

    // ----------------------------------------------------------
    protected IReportEngine reportEngine()
    {
        return BIRTRuntime.getInstance().getReportEngine();
    }


    // ----------------------------------------------------------
    protected GeneratedReport generatedReport()
    {
        return report;
    }
    
    
    // ----------------------------------------------------------
    protected IReportDocument reportDocument()
    {
        return document;
    }


    // ----------------------------------------------------------
    public abstract Throwable deliverTo(DeliverFile file);


    //~ Static/instance variables .............................................

    private GeneratedReport report;
    private IReportDocument document;
}
