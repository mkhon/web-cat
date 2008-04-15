/*==========================================================================*\
 |  $Id: ReportQuery.java,v 1.7 2008/04/15 04:09:22 aallowat Exp $
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

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import net.sf.webcat.core.MutableDictionary;

// -------------------------------------------------------------------------
/**
 * A query in the reporter is a qualifier, along with other metadata, that
 * determines the data that will go into a result set.
 *
 * @author Tony Allevato
 * @version $Id: ReportQuery.java,v 1.7 2008/04/15 04:09:22 aallowat Exp $
 */
public class ReportQuery
    extends _ReportQuery
{
    //~ Constructor ...........................................................

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
