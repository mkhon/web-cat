/*==========================================================================*\
 |  $Id: KVCAttributeInfo.java,v 1.3 2008/04/02 01:36:38 stedwar2 Exp $
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

package net.sf.webcat.reporter.queryassistants;

//-------------------------------------------------------------------------
/**
 * Describes one KVC-accessible attribute of a class, including its name
 * and type.
 *
 * @author aallowat
 * @version $Id: KVCAttributeInfo.java,v 1.3 2008/04/02 01:36:38 stedwar2 Exp $
 */
public class KVCAttributeInfo
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create an object.
     * @param name The name of this attribute
     * @param type The type of this attribute
     */
	public KVCAttributeInfo(String name, String type)
	{
		this.name = name;
		this.type = type;
	}


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
	public String name()
	{
		return name;
	}


    // ----------------------------------------------------------
	public String type()
	{
		return type;
	}


    //~ Instance/static variables .............................................

	private String name;
	private String type;
}
