package net.sf.webcat.reporter.internal.rendering;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;

import net.sf.webcat.reporter.GeneratedReport;
import net.sf.webcat.reporter.IRenderingMethod;
import net.sf.webcat.reporter.Reporter;
import net.sf.webcat.reporter.ReporterHTMLImageHandler;

public class HTMLRenderingMethod extends AbstractRenderingMethod
{
	private static final String REPORT_ROOT_HTML = "report.html";

	public HTMLRenderingMethod(IReportEngine engine)
	{
		super(engine);
	}

	public String methodName()
	{
		return "html";
	}

	public String displayName()
	{
		return "HTML";
	}

	public Controller prepareToRender(GeneratedReport report,
			NSDictionary options)
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

	public void appendContentToResponse(GeneratedReport report,
			WOResponse response, WOContext context) throws IOException
	{
	   	String htmlPath = GeneratedReport.renderedResourcePath(
	   			report.uuid(), REPORT_ROOT_HTML);
		File htmlFile = new File(htmlPath);

		NSData htmlData = new NSData(new FileInputStream(htmlFile),
			(int)htmlFile.length());
		response.appendContentData(htmlData);
	}
	
	private class HTMLController implements Controller
	{
		private IRenderTask task;
		
		public HTMLController(IRenderTask task)
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
