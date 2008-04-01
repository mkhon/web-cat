/*==========================================================================*\
 |  $Id: IDesignElementVisitor.java,v 1.2 2008/04/01 02:44:18 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU General Public License as published by
 |  the Free Software Foundation; either version 2 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU General Public License
 |  along with Web-CAT; if not, write to the Free Software
 |  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 |
 |  Project manager: Stephen Edwards <edwards@cs.vt.edu>
 |  Virginia Tech CS Dept, 660 McBryde Hall (0106), Blacksburg, VA 24061 USA
\*==========================================================================*/

package net.sf.webcat.reporter;

import org.eclipse.birt.report.model.api.DesignElementHandle;

//-------------------------------------------------------------------------
/**
 * An interface defining the kinds of objects that can be used in the
 * visitor pattern over design elements in a report template.
 *
 * @author  Anthony Allevato
 * @version $Id: IDesignElementVisitor.java,v 1.2 2008/04/01 02:44:18 stedwar2 Exp $
 */
public interface IDesignElementVisitor
{
    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Visit one design element and process it.
     * @param handle The current design element
     */
	void accept(DesignElementHandle handle);
}
