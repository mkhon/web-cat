/*==========================================================================*\
 |  $Id: KVCAttributeInfo.java,v 1.2 2008/03/31 18:27:11 stedwar2 Exp $
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

//-------------------------------------------------------------------------
/**
 * Describes one KVC-accessible attribute of a class, including its name
 * and type.
 *
 * @author aallowat
 * @version $Id: KVCAttributeInfo.java,v 1.2 2008/03/31 18:27:11 stedwar2 Exp $
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
