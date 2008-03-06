package net.sf.webcat.reporter;

import org.eclipse.birt.report.model.api.DesignElementHandle;

public interface IDesignElementVisitor
{
	void accept(DesignElementHandle handle);
}
