import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;

import junit.framework.TestCase;


public class BirtTest extends TestCase {

	private IReportEngine reportEngine;

	public BirtTest()
	{
        // Initialize the BIRT reporting engine here.
        String reportEnginePath = "/Users/aallowat/eclipse/Web-CAT/Reporter/build/Reporter.framework/Resources/ReportEngine";

        EngineConfig config = new EngineConfig();
        config.setEngineHome( reportEnginePath );
        
        // Point the OSGi platform's configuration area to the storage folder
        // chosen by the Web-CAT admin. Otherwise, the default location is in
        // the report engine path specified above, which could be read-only.
    	String configArea = "/usr/local/webcat-storage-f05/ReporterConfiguration";
    	String instanceArea = "/usr/local/webcat-storage-f05/ReporterWorkspace";

    	File configAreaDir = new File(configArea);
    	if(!configAreaDir.exists())
    	{
    		configAreaDir.mkdirs();
    		
    		File configSrcDir = new File(reportEnginePath + "/configuration");

    		try
    		{
				net.sf.webcat.archives.FileUtilities
					.copyDirectoryContentsIfNecessary(configSrcDir,
							configAreaDir);
			}
    		catch (IOException e)
    		{
    			e.printStackTrace();
			}
    	}

        Map osgiConfig = new Hashtable();
        osgiConfig.put("osgi.configuration.area", configArea);
        osgiConfig.put("osgi.instance.area", instanceArea);
        config.setOSGiConfig(osgiConfig);
        
        try
        {
			Platform.startup( config );

        	IReportEngineFactory factory = (IReportEngineFactory) Platform
				.createFactoryObject(
						IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY );

			reportEngine = factory.createReportEngine( config );
		}
        catch (Exception e)
        {
        	e.printStackTrace();
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testOpenTemplate()
	{
    	try
    	{
    		String path = "/Users/aallowat/report-templates/param_test.rptdesign";
			IReportRunnable runnable = reportEngine.openReportDesign(path);
			
			IGetParameterDefinitionTask task =
				reportEngine.createGetParameterDefinitionTask(runnable);
			
			Iterator it = task.getParameterDefns(false).iterator();
			while(it.hasNext())
			{
				IParameterDefn param = (IParameterDefn)it.next();
				System.out.println(param.getName());
			}
		}
    	catch(EngineException e)
    	{
    		e.printStackTrace();
		}
	}
}
