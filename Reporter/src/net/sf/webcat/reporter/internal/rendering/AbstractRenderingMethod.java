package net.sf.webcat.reporter.internal.rendering;

import org.eclipse.birt.report.engine.api.IReportEngine;

import net.sf.webcat.reporter.IRenderingMethod;

public abstract class AbstractRenderingMethod implements IRenderingMethod
{
	private IReportEngine reportEngine;
	
	public AbstractRenderingMethod(IReportEngine engine)
	{
		reportEngine = engine;
	}
	
	protected IReportEngine reportEngine()
	{
		return reportEngine;
	}
}
