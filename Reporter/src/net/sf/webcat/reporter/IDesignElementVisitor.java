/*==========================================================================*\
 |  $Id: IDesignElementVisitor.java,v 1.3 2008/04/02 01:36:38 stedwar2 Exp $
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

import org.eclipse.birt.report.model.api.DesignElementHandle;

//-------------------------------------------------------------------------
/**
 * An interface defining the kinds of objects that can be used in the
 * visitor pattern over design elements in a report template.
 *
 * @author  Anthony Allevato
 * @version $Id: IDesignElementVisitor.java,v 1.3 2008/04/02 01:36:38 stedwar2 Exp $
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
