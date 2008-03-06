package net.sf.webcat.reporter;

import java.util.Iterator;

import org.eclipse.birt.report.model.api.CellHandle;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.FreeFormHandle;
import org.eclipse.birt.report.model.api.GridHandle;
import org.eclipse.birt.report.model.api.GroupHandle;
import org.eclipse.birt.report.model.api.ListingHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ReportItemHandle;
import org.eclipse.birt.report.model.api.RowHandle;

public class ReportBodyWalker
{
	public ReportBodyWalker(ReportDesignHandle designHandle)
	{
		this.designHandle = designHandle;
	}
	
	public void visit(IDesignElementVisitor visitor)
	{
		Iterator<DesignElementHandle> it = designHandle.getBody().iterator();
		
		while(it.hasNext())
		{
			DesignElementHandle handle = it.next();
			visit(handle, visitor);
		}
	}
	
	private void visit(DesignElementHandle handle,
			IDesignElementVisitor visitor)
	{
		visitor.accept(handle);
		
		Iterator<DesignElementHandle> it = childrenOfElement(handle);

		if(it != null)
		{
			while(it.hasNext())
			{
				DesignElementHandle child = it.next();
				visit(child, visitor);
			}
		}
	}

	private Iterator<DesignElementHandle> childrenOfElement(
			DesignElementHandle handle)
	{
		if(handle instanceof CellHandle)
		{
			return ((CellHandle)handle).getContent().iterator();
		}
		else if(handle instanceof GroupHandle)
		{
			return new MultiIterator<DesignElementHandle>(new Iterator[] {
					((GroupHandle)handle).getHeader().iterator(),
					((GroupHandle)handle).getFooter().iterator()
			});
		}
		else if(handle instanceof FreeFormHandle)
		{
			return ((FreeFormHandle)handle).getReportItems().iterator();
		}
		else if(handle instanceof GridHandle)
		{
			return new MultiIterator<DesignElementHandle>(new Iterator[] {
					((GridHandle)handle).getColumns().iterator(),
					((GridHandle)handle).getRows().iterator()
			});			
		}
		else if(handle instanceof ListingHandle)
		{
			return new MultiIterator<DesignElementHandle>(new Iterator[] {
					((ListingHandle)handle).getHeader().iterator(),
					((ListingHandle)handle).getGroups().iterator(),
					((ListingHandle)handle).getDetail().iterator(),
					((ListingHandle)handle).getFooter().iterator()
			});						
		}
		else if(handle instanceof RowHandle)
		{
			return ((RowHandle)handle).getCells().iterator();
		}
		else
		{
			return null;
		}
	}

	private ReportDesignHandle designHandle;
}
