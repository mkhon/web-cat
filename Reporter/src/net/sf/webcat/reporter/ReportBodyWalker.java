/*==========================================================================*\
 |  $Id: ReportBodyWalker.java,v 1.3 2008/04/02 01:36:38 stedwar2 Exp $
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

//-------------------------------------------------------------------------
/**
 * Provides visitor support for report designs.
 *
 * @author Tony Allevato
 * @version $Id: ReportBodyWalker.java,v 1.3 2008/04/02 01:36:38 stedwar2 Exp $
 */
public class ReportBodyWalker
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new walker.
     * @param designHandle the report design to traverse
     */
	public ReportBodyWalker(ReportDesignHandle designHandle)
	{
		this.designHandle = designHandle;
	}


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Traverses the report design with the given visitor, invoking the
     * visitor's {@link IDesignElementVisitor#visit()} method on each element.
     * @param visitor The visitor to use
     */
	public void visit(IDesignElementVisitor visitor)
	{
		Iterator<DesignElementHandle> it = designHandle.getBody().iterator();

		while(it.hasNext())
		{
			DesignElementHandle handle = it.next();
			visit(handle, visitor);
		}
	}


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
	private void visit(
        DesignElementHandle handle, IDesignElementVisitor visitor)
	{
		visitor.accept(handle);

		Iterator<DesignElementHandle> it = childrenOfElement(handle);

		if (it != null)
		{
			while (it.hasNext())
			{
				DesignElementHandle child = it.next();
				visit(child, visitor);
			}
		}
	}


    // ----------------------------------------------------------
	private Iterator<DesignElementHandle> childrenOfElement(
	    DesignElementHandle handle)
	{
		if (handle instanceof CellHandle)
		{
			return ((CellHandle)handle).getContent().iterator();
		}
		else if (handle instanceof GroupHandle)
		{
			return new MultiIterator<DesignElementHandle>(new Iterator[] {
				((GroupHandle)handle).getHeader().iterator(),
				((GroupHandle)handle).getFooter().iterator()
			});
		}
		else if (handle instanceof FreeFormHandle)
		{
			return ((FreeFormHandle)handle).getReportItems().iterator();
		}
		else if (handle instanceof GridHandle)
		{
			return new MultiIterator<DesignElementHandle>(new Iterator[] {
				((GridHandle)handle).getColumns().iterator(),
				((GridHandle)handle).getRows().iterator()
			});
		}
		else if (handle instanceof ListingHandle)
		{
			return new MultiIterator<DesignElementHandle>(new Iterator[] {
				((ListingHandle)handle).getHeader().iterator(),
				((ListingHandle)handle).getGroups().iterator(),
				((ListingHandle)handle).getDetail().iterator(),
				((ListingHandle)handle).getFooter().iterator()
			});
		}
		else if (handle instanceof RowHandle)
		{
			return ((RowHandle)handle).getCells().iterator();
		}
		else
		{
			return null;
		}
	}


    //~ Instance/static variables .............................................

	private ReportDesignHandle designHandle;
}
