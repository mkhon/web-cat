/*==========================================================================*\
 |  $Id: IQueryAssistant.java,v 1.2 2008/03/31 18:27:11 stedwar2 Exp $
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

package net.sf.webcat.reporter.queryassistants;

import com.webobjects.eocontrol.EOQualifier;

//-------------------------------------------------------------------------
/**
 * Component classes that implement a reporter query assistant must implement
 * this interface. A query assistant can use whatever internal representation
 * it wishes to maintain the state of the query being constructed; this
 * interface defines how it transforms that state into an EOQualifier that can
 * be used in a fetch specification.
 *
 * @author aallowat
 * @version $Id: IQueryAssistant.java,v 1.2 2008/03/31 18:27:11 stedwar2 Exp $
 */
public interface IQueryAssistant
{
	// ------------------------------------------------------------------------
	/**
	 * Gets a qualifier that represents the current internal state of this
	 * query assistant.
	 *
	 * @return an EOQualifier object
	 */
	EOQualifier qualifierFromState();


	// ------------------------------------------------------------------------
	/**
	 * Converts the specified qualifier to whatever internal state
	 * representation that this query assistant uses to maintain the query
	 * begin constructed.
	 *
	 * @param q the EOQualifier to obtain the state from
	 */
	void takeStateFromQualifier(EOQualifier q);
}
