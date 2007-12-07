package net.sf.webcat.reporter;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableDictionary;

import net.sf.webcat.oda.IWebCATResultSet;
import net.sf.webcat.oda.IWebCATResultSetProvider;

public class OdaResultSetProvider implements IWebCATResultSetProvider
{
	private String jobUuid;

	private NSArray<ReportDataSetQuery> dataSetQueries;

	private NSMutableDictionary<String, ReportQuery> queryMap;

	public OdaResultSetProvider(EnqueuedReportJob job)
	{
		jobUuid = job.uuid();
		dataSetQueries = job.dataSetQueries();
		queryMap = new NSMutableDictionary<String, ReportQuery>();

		for(ReportDataSetQuery dataSetQuery : dataSetQueries)
		{
			String uuid = dataSetQuery.dataSet().uuid();
			ReportQuery query = dataSetQuery.reportQuery();
			queryMap.setObjectForKey(query, uuid);
		}
	}

	public IWebCATResultSet resultSetWithUuid(String uuid)
	{
		ReportQuery query = queryMap.objectForKey(uuid);
		return new OdaResultSet(jobUuid, query);
	}
}
