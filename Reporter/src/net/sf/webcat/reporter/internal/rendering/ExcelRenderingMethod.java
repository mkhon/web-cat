package net.sf.webcat.reporter.internal.rendering;

import java.io.IOException;

import net.sf.webcat.reporter.GeneratedReport;
import net.sf.webcat.reporter.Reporter;
import net.sf.webcat.reporter.ReporterHTMLImageHandler;

import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.RenderOption;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

public class ExcelRenderingMethod extends AbstractRenderingMethod {

	public ExcelRenderingMethod(IReportEngine engine)
	{
		super(engine);
	}

	public void appendContentToResponse(GeneratedReport report,
			WOResponse response, WOContext context) throws IOException
	{
		StringBuilder content = new StringBuilder();
		NSMutableDictionary query = new NSMutableDictionary();
		query.setObjectForKey(report.uuid(), "uuid");

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

	public String displayName()
	{
		return "Microsoft Excel";
	}

	public String methodName()
	{
		return "xls";
	}

	public Controller prepareToRender(GeneratedReport report,
			NSDictionary options)
	{
    	IReportDocument document = Reporter.getInstance().openReportDocument(
    			report.generatedReportFile());
   
    	String actionUrl = (String)options.objectForKey(OPTION_ACTION_URL);

    	String filename = "report.xls";
  
    	RenderOption option = new RenderOption();
    	option.setOutputFormat("XLS");
    	option.setOutputFileName(GeneratedReport.renderedResourcePath(
    			report.uuid(), filename));
    	
    	IRenderTask task = reportEngine().createRenderTask(document);
    	task.setRenderOption(option);
    	
    	return new ExcelController(task);
	}
	
	private class ExcelController implements Controller
	{
		private IRenderTask task;
		
		public ExcelController(IRenderTask task)
		{
			this.task = task;
		}

		public void render() throws Exception
		{
    		org.mozilla.javascript.Context.enter();
            task.render();
            org.mozilla.javascript.Context.exit();

            task.close();
		}
		
		public void cancel()
		{
			task.cancel();
		}
	}
}
