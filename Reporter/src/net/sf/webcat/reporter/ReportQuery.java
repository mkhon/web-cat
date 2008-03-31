/*==========================================================================*\
 |  ReportQuery.java
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

import net.sf.webcat.core.MutableDictionary;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;

// -------------------------------------------------------------------------
/**
 * TODO: place a real description here.
 *
 * @author
 * @version $Id: ReportQuery.java,v 1.4 2008/03/31 01:50:41 stedwar2 Exp $
 */
public class ReportQuery
    extends _ReportQuery
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new ReportQuery object.
     */
    public ReportQuery()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets the qualifier that applies to this query.
     * @return the qualifier
     */
    public EOQualifier qualifier()
    {
    	MutableDictionary info = queryInfo();

    	if(info == null)
    		return null;
    	else
    		return (EOQualifier)info.objectForKey("qualifier");
    }


    // ----------------------------------------------------------
    /**
     * Sets the qualifier that applies to this query.
     * @param q the qualifier to use
     */
    public void setQualifier(EOQualifier q)
    {
    	if(queryInfo() == null)
    		setQueryInfo(new MutableDictionary());

    	if(q == null)
    	{
    		queryInfo().removeObjectForKey("qualifier");
    	}
    	else
    	{
    		queryInfo().setObjectForKey(q, "qualifier");
    	}
    }
}
