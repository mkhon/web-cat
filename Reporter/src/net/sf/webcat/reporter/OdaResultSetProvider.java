/*==========================================================================*\
 |  $Id: OdaResultSetProvider.java,v 1.3 2008/04/02 01:36:38 stedwar2 Exp $
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
import net.sf.webcat.oda.IWebCATResultSet;
import net.sf.webcat.oda.IWebCATResultSetProvider;

//-------------------------------------------------------------------------
/**
 * Generates result sets.
 *
 * @author aallowat
 * @version $Id: OdaResultSetProvider.java,v 1.3 2008/04/02 01:36:38 stedwar2 Exp $
 */
public class OdaResultSetProvider
    implements IWebCATResultSetProvider
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new provider for a report job.
     * @param job the report job
     */
	public OdaResultSetProvider(EnqueuedReportJob job)
	{
		jobUuid = job.uuid();
		dataSetQueries = job.dataSetQueries();
		queryMap = new NSMutableDictionary<String, ReportQuery>();

		for (ReportDataSetQuery dataSetQuery : dataSetQueries)
		{
			String uuid = dataSetQuery.dataSet().uuid();
			ReportQuery query = dataSetQuery.reportQuery();
			queryMap.setObjectForKey(query, uuid);
		}
	}


    //~ Methods ...............................................................

    // ----------------------------------------------------------
	public IWebCATResultSet resultSetWithUuid(String uuid)
	{
		ReportQuery query = queryMap.objectForKey(uuid);
		return new OdaResultSet(jobUuid, query);
	}


    //~ Instance/static variables .............................................

    private String jobUuid;
    private NSArray<ReportDataSetQuery> dataSetQueries;
    private NSMutableDictionary<String, ReportQuery> queryMap;
}
