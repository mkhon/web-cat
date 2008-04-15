/*==========================================================================*\
 |  $Id: ReporterComponent.java,v 1.8 2008/04/15 04:09:22 aallowat Exp $
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

import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;
import er.extensions.ERXConstant;
import java.io.File;
import java.io.FileOutputStream;
import net.sf.webcat.core.Application;
import net.sf.webcat.core.MutableDictionary;
import net.sf.webcat.core.User;
import net.sf.webcat.core.WCComponent;
import net.sf.webcat.grader.FinalReportPage;

import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * A base class for pages in the Reporter subsystem.
 *
 * @author Tony Allevato
 * @version $Id: ReporterComponent.java,v 1.8 2008/04/15 04:09:22 aallowat Exp $
 */
public class ReporterComponent
    extends WCComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new ReporterComponent object.
     * @param context The context to use
     */
    public ReporterComponent( WOContext context )
    {
        super( context );
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public void clearLocalReportState()
    {
        transientState().removeObjectForKey(KEY_REPORT_DESCRIPTION);
        transientState().removeObjectForKey(KEY_REPORT_GENERATION_JOB);
        transientState().removeObjectForKey(KEY_REPORT_TEMPLATE);
        transientState().removeObjectForKey(KEY_GENERATED_REPORT);
        transientState().removeObjectForKey(KEY_RENDERING_METHOD);
        transientState().removeObjectForKey(KEY_CURRENT_DATASET);
        transientState().removeObjectForKey(KEY_QUERIES);
        transientState().removeObjectForKey(KEY_PAGE_CONTROLLER);
    }


    // ----------------------------------------------------------
    public String localReportDescription()
    {
        return (String)transientState().objectForKey(KEY_REPORT_DESCRIPTION);
    }


    // ----------------------------------------------------------
    public void setLocalReportDescription(String value)
    {
        transientState().setObjectForKey(value, KEY_REPORT_DESCRIPTION);
    }


    // ----------------------------------------------------------
    public EnqueuedReportGenerationJob localReportGenerationJob()
    {
        return (EnqueuedReportGenerationJob)transientState().objectForKey(
            KEY_REPORT_GENERATION_JOB);
    }


    // ----------------------------------------------------------
    public void setLocalReportGenerationJob(EnqueuedReportGenerationJob value)
    {
        transientState().setObjectForKey(value, KEY_REPORT_GENERATION_JOB);
    }


    // ----------------------------------------------------------
    public ReportTemplate localReportTemplate()
    {
        return (ReportTemplate)transientState().objectForKey(
                KEY_REPORT_TEMPLATE);
    }


    // ----------------------------------------------------------
    public void setLocalReportTemplate(ReportTemplate value)
    {
        transientState().setObjectForKey(value, KEY_REPORT_TEMPLATE);
    }


    // ----------------------------------------------------------
    public int localCurrentReportDataSet()
    {
        return (Integer)transientState().objectForKey(KEY_CURRENT_DATASET);
    }


    // ----------------------------------------------------------
    public void setLocalCurrentReportDataSet(int value)
    {
        transientState().setObjectForKey(value, KEY_CURRENT_DATASET);
    }


    // ----------------------------------------------------------
    public GeneratedReport localGeneratedReport()
    {
        return (GeneratedReport)transientState().objectForKey(
            KEY_GENERATED_REPORT);
    }


    // ----------------------------------------------------------
    public void setLocalGeneratedReport(GeneratedReport value)
    {
        transientState().setObjectForKey(value, KEY_GENERATED_REPORT);
    }


    // ----------------------------------------------------------
    public String localRenderingMethod()
    {
        return (String)transientState().objectForKey(KEY_RENDERING_METHOD);
    }


    // ----------------------------------------------------------
    public void setLocalRenderingMethod(String value)
    {
        transientState().setObjectForKey(value, KEY_RENDERING_METHOD);
    }


    // ----------------------------------------------------------
    public void createLocalPageController()
    {
        QueryPageController controller = new QueryPageController(
            this, localReportTemplate().dataSets());

        transientState().setObjectForKey(controller, KEY_PAGE_CONTROLLER);
    }


    // ----------------------------------------------------------
    public QueryPageController localPageController()
    {
        return (QueryPageController)transientState().objectForKey(
            KEY_PAGE_CONTROLLER);
    }


    // ----------------------------------------------------------
    public String componentForLocalDataSetId(Number dataSetId)
    {
        NSDictionary<Number, NSDictionary<String, Object>> queryMap =
            (NSDictionary<Number, NSDictionary<String, Object>>)
            transientState().objectForKey(KEY_QUERIES);

        if (queryMap == null)
        {
            return null;
        }

        NSDictionary<String, Object> queryInfo =
            queryMap.objectForKey(dataSetId);

        if (queryInfo == null)
        {
            return null;
        }

        return (String)queryInfo.objectForKey("componentName");
    }


    // ----------------------------------------------------------
    public void setComponentForLocalDataSetId(String value, Number dataSetId)
    {
        NSMutableDictionary<Number, NSMutableDictionary<String, Object>>
            queryMap =
            (NSMutableDictionary<Number, NSMutableDictionary<String, Object>>)
            transientState().objectForKey(KEY_QUERIES);

        if (queryMap == null)
        {
            queryMap = new NSMutableDictionary<Number,
                NSMutableDictionary<String, Object>>();

            transientState().setObjectForKey(queryMap, KEY_QUERIES);
        }

        NSMutableDictionary<String, Object> queryInfo =
            queryMap.objectForKey(dataSetId);

        if (queryInfo == null)
        {
            queryInfo = new NSMutableDictionary<String, Object>();
            queryMap.setObjectForKey(queryInfo, dataSetId);
        }

           queryInfo.setObjectForKey(value, "componentName");
    }


    // ----------------------------------------------------------
    public NSArray<Number> localDataSetIds()
    {
        NSDictionary<Number, NSDictionary<String, Object>> queryMap =
            (NSDictionary<Number, NSDictionary<String, Object>>)
            transientState().objectForKey(KEY_QUERIES);

        if (queryMap == null)
        {
            return NSArray.EmptyArray;
        }
        else
        {
            return queryMap.allKeys();
        }
    }


    // ----------------------------------------------------------
    public ReportQuery queryForLocalDataSetId(Number dataSetId)
    {
        NSDictionary<Number, NSDictionary<String, Object>> queryMap =
            (NSDictionary<Number, NSDictionary<String, Object>>)
            transientState().objectForKey(KEY_QUERIES);

        if  (queryMap == null)
        {
            return null;
        }

        NSDictionary<String, Object> queryInfo =
            queryMap.objectForKey(dataSetId);

        if (queryInfo == null)
        {
            return null;
        }

        return (ReportQuery)queryInfo.objectForKey("query");
    }


    // ----------------------------------------------------------
    public ReportQuery rollbackQueryForDataSet(ReportDataSet dataSet)
    {
        NSDictionary<Number, NSDictionary<String, Object>> queryMap =
            (NSDictionary<Number, NSDictionary<String, Object>>)
            transientState().objectForKey(KEY_QUERIES);

        if (queryMap == null)
        {
            return null;
        }

        Number dataSetId = dataSet.id();
        NSDictionary<String, Object> queryInfo =
            queryMap.objectForKey(dataSetId);

        if (queryInfo == null)
        {
            return null;
        }

        ReportQuery query = (ReportQuery)queryInfo.objectForKey("query");

        if (query != null)
        {
            // Only remove the query from the database if it isn't being used
            // by any other ReportDataSetQueries.
            NSArray<ReportDataSetQuery> uses = query.dataSetQueries();
            if (uses == null || uses.count() == 0)
            {
                localContext().deleteObject(query);
                applyLocalChanges();
            }
        }

        return query;
    }


    // ----------------------------------------------------------
    public void commitNewQueryForDataSet(
        ReportDataSet dataSet, String description, EOQualifier qualifier)
    {
        ReportQuery query = new ReportQuery();
        localContext().insertObject(query);
        query.setDescription(description);
        query.setQualifier(qualifier);
        query.setUserRelationship(user());
        query.setWcEntityName(dataSet.wcEntityName());
        applyLocalChanges();

        commitExistingQueryForDataSet(dataSet, query);
    }


    // ----------------------------------------------------------
    public void commitExistingQueryForDataSet(
        ReportDataSet dataSet, ReportQuery query)
    {
        NSMutableDictionary<Number, NSMutableDictionary<String, Object>>
            queryMap =
            (NSMutableDictionary<Number, NSMutableDictionary<String, Object>>)
            transientState().objectForKey(KEY_QUERIES);

        if (queryMap == null)
        {
            queryMap = new NSMutableDictionary<Number,
                NSMutableDictionary<String, Object>>();

            transientState().setObjectForKey(queryMap, KEY_QUERIES);
        }

        Number dataSetId = dataSet.id();
        NSMutableDictionary<String, Object> queryInfo =
            queryMap.objectForKey(dataSetId);

        if (queryInfo == null)
        {
            queryInfo = new NSMutableDictionary<String, Object>();
            queryMap.setObjectForKey(queryInfo, dataSetId);
        }

           queryInfo.setObjectForKey(query, "query");
    }


    // ----------------------------------------------------------
    public String commitReportGeneration()
    {
        String errorMessage = null;
        log.debug("committing report generation");

        EOEditingContext ec = localContext();

        ReportTemplate reportTemplate = localReportTemplate();

        // Queue it up for the reporter
        NSTimestamp queueTime = new NSTimestamp();

        EnqueuedReportGenerationJob job = new EnqueuedReportGenerationJob();
        ec.insertObject(job);

        job.setDescription(localReportDescription());
        job.setReportTemplateRelationship(reportTemplate);
        job.setQueueTime(queueTime);
        job.setUserRelationship(user());

        ec.saveChanges();

        // Create ReportDataSetQuery objects to map all of the data sets in
        // the transient state to the queries that were created for them.
        NSArray<Number> dataSetIds = localDataSetIds();
        for (Number dataSetId : dataSetIds)
        {
            ReportDataSet dataSet =
                ReportDataSet.forId(ec, dataSetId.intValue());

            ReportQuery query = queryForLocalDataSetId(dataSetId);

            ReportDataSetQuery dataSetQuery =
                job.createDataSetQueriesRelationship();
            dataSetQuery.setDataSetRelationship(dataSet);
            dataSetQuery.setReportQueryRelationship(query);
            applyLocalChanges();
        }

        setLocalReportGenerationJob(job);
        Reporter.getInstance().reportGenerationQueue().enqueue(null);

        return errorMessage;
    }


    // ----------------------------------------------------------
    public String commitReportRendering(GeneratedReport report)
    {
        String errorMessage = null;
        log.debug("committing report rendering");

        // Queue it up for the reporter
        String actionUrl = context().directActionURLForActionNamed(
            "reportResource/image", null);

        EnqueuedReportRenderJob job = new EnqueuedReportRenderJob();
        localContext().insertObject(job);
        job.setQueueTime(new NSTimestamp());
        job.setGeneratedReportRelationship(report);
        job.setRenderedResourceActionUrl(actionUrl);

        String method = localRenderingMethod();
        if (method == null)
        {
            method = "html";
            setLocalRenderingMethod(method);
        }

        job.setRenderingMethod(method);
        job.setUserRelationship(user());
        applyLocalChanges();

        setLocalGeneratedReport(report);
        Reporter.getInstance().reportRenderQueue().enqueue(null);

        return errorMessage;
    }


    //~ Instance/static variables .............................................

    // Internal constants for key names
    private static final String KEY_REPORT_DESCRIPTION =
        "net.sf.webcat.reporter.reportDescription";
    private static final String KEY_REPORT_GENERATION_JOB =
        "net.sf.webcat.reporter.enqueuedJob";
    private static final String KEY_REPORT_TEMPLATE =
        "net.sf.webcat.reporter.reportTemplate";
    private static final String KEY_GENERATED_REPORT =
        "net.sf.webcat.reporter.generatedReport";
    private static final String KEY_RENDERING_METHOD =
        "net.sf.webcat.reporter.renderingMethod";
    private static final String KEY_CURRENT_DATASET =
        "net.sf.webcat.reporter.currentDataSet";
    private static final String KEY_QUERIES =
        "net.sf.webcat.reporter.queries";
    private static final String KEY_PAGE_CONTROLLER =
        "net.sf.webcat.reporter.pageController";

    static Logger log = Logger.getLogger( ReporterComponent.class );

}
