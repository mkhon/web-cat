/*==========================================================================*\
 |  $Id: ReportQuery.java,v 1.9 2009/06/09 17:43:10 aallowat Exp $
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
 * @version $Id: ReportQuery.java,v 1.9 2009/06/09 17:43:10 aallowat Exp $
 */
public class ReportQuery extends _ReportQuery
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
     *
     * @return the qualifier
     */
    public EOQualifier qualifier()
    {
        MutableDictionary info = queryInfo();

        if (info == null)
        {
            return null;
        }
        else
        {
            EOQualifier q = (EOQualifier) info.objectForKey(KEY_QUALIFIER);
            return q;
        }
    }


    // ----------------------------------------------------------
    /**
     * Sets the qualifier that applies to this query.
     *
     * @param q
     *            the qualifier to use
     */
    public void setQualifier(EOQualifier q)
    {
        if (queryInfo() == null)
        {
            setQueryInfo(new MutableDictionary());
        }

        if (q == null)
        {
            queryInfo().removeObjectForKey(KEY_QUALIFIER);
        }
        else
        {
            queryInfo().setObjectForKey(q, KEY_QUALIFIER);
        }
    }


    // ----------------------------------------------------------
    /**
     * Gets the identifier of the query assistant that was used to construct
     * this query.
     *
     * @return the id of the query assistant
     */
    public String queryAssistantId()
    {
        MutableDictionary info = queryInfo();

        // Since older queries did not store the query assistant that was used
        // to construct them, we put in a special case to fallback to the
        // advanced query builder, which should handle any of the queries
        // created up to the time that this was done.

        if (info == null)
        {
            return FALLBACK_QUERY_ASSISTANT;
        }
        else
        {
            String id = (String) info.objectForKey(KEY_QUERY_ASSISTANT_ID);

            return (id != null) ? id : FALLBACK_QUERY_ASSISTANT;
        }
    }


    // ----------------------------------------------------------
    /**
     * Sets the identifier of the query assistant that was used to construct
     * this query.
     *
     * @param id
     *            the id of the query assistant
     */
    public void setQueryAssistantId(String id)
    {
        if (queryInfo() == null)
        {
            setQueryInfo(new MutableDictionary());
        }

        if (id == null)
        {
            queryInfo().removeObjectForKey(KEY_QUERY_ASSISTANT_ID);
        }
        else
        {
            queryInfo().setObjectForKey(id, KEY_QUERY_ASSISTANT_ID);
        }
    }


    //~ Static/instance variables .............................................

    /*
     * A safe fallback for older queries that did not keep track of the query
     * assistant used to construct them.
     */
    private static final String FALLBACK_QUERY_ASSISTANT =
        "net.sf.webcat.reporter.queryassistants.advancedQuery";

    private static final String KEY_QUALIFIER = "qualifier";
    private static final String KEY_QUERY_ASSISTANT_ID = "queryAssistantId";
}
