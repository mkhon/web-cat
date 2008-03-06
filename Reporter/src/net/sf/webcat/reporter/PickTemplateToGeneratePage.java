package net.sf.webcat.reporter;
// Generated by the WOLips Templateengine Plug-in at Jan 30, 2007 2:42:54 PM

import java.util.UUID;

import net.sf.webcat.core.WCComponent;
import net.sf.webcat.grader.EditPluginGlobalsPage;

import com.webobjects.appserver.*;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

public class PickTemplateToGeneratePage extends ReporterComponent {

	public NSArray<ReportTemplate> reportTemplates;
	public ReportTemplate reportTemplate;
	public int index;

    public PickTemplateToGeneratePage(WOContext context)
    {
        super(context);
    }

    public NSArray<ReportTemplate> reportTemplates()
    {
    	if(reportTemplates == null)
    	{
    		reportTemplates =
    			ReportTemplate.objectsForAllTemplates(localContext());
    	}
    	
    	return reportTemplates;
    }

    public WOComponent templateChosen()
    {
    	clearLocalReportState();

    	setLocalReportUuid(UUID.randomUUID().toString());
        setLocalReportTemplate(reportTemplate);
        setLocalCurrentReportDataSet(0);
        createLocalPageController();

        return localPageController().nextPage();
    }
}