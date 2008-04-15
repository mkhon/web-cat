/*==========================================================================*\
 |  $Id: OdaResultSetProvider.java,v 1.4 2008/04/15 04:09:22 aallowat Exp $
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

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableDictionary;
import net.sf.webcat.oda.commons.IWebCATResultSet;
import net.sf.webcat.oda.commons.IWebCATResultSetProvider;

//-------------------------------------------------------------------------
/**
 * Generates result sets for a report generation job based on the data set
 * queries that it has specified.
 *
 * @author Tony Allevato
 * @version $Id: OdaResultSetProvider.java,v 1.4 2008/04/15 04:09:22 aallowat Exp $
 */
public class OdaResultSetProvider implements IWebCATResultSetProvider
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new result set provider for a report job.
     *
     * @param job
     *            the report job
     * @param report
     *            the generated report that this data will go into
     */
    public OdaResultSetProvider(EnqueuedReportGenerationJob job)
    {
        jobId = job.id();

        dataSetQueries = job.dataSetQueries();
        queryMap = new NSMutableDictionary<Integer, ReportQuery>();

        // Construct the mapping from data set IDs to queries that define the
        // data to be retrieved.

        for (ReportDataSetQuery dataSetQuery : dataSetQueries)
        {
            Number dataSetId = dataSetQuery.dataSet().id();
            ReportQuery query = dataSetQuery.reportQuery();

            queryMap.setObjectForKey(query,
                    Integer.valueOf(dataSetId.intValue()));
        }
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public IWebCATResultSet resultSetWithId(String id)
    {
        Integer dataSetId = Integer.parseInt(id);
        ReportQuery query = queryMap.objectForKey(dataSetId);

        return new OdaResultSet(jobId, query);
    }


    //~ Instance/static variables .............................................

    private Number jobId;
    private NSArray<ReportDataSetQuery> dataSetQueries;
    private NSMutableDictionary<Integer, ReportQuery> queryMap;
}
