/*==========================================================================*\
 |  $Id: ReporterComponent.java,v 1.12 2009/12/09 05:03:40 aallowat Exp $
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

import net.sf.webcat.core.WCComponent;
import net.sf.webcat.reporter.queryassistants.ModelOrQueryWrapper;
import org.apache.log4j.Logger;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

//-------------------------------------------------------------------------
/**
 * A base class for pages and subcomponents in the Reporter subsystem.
 *
 * @author Tony Allevato
 * @version $Id: ReporterComponent.java,v 1.12 2009/12/09 05:03:40 aallowat Exp $
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
        transientState().removeObjectForKey(KEY_REPORT_TEMPLATE);
        transientState().removeObjectForKey(KEY_GENERATED_REPORT);
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
    public String commitReportGeneration(ModelOrQueryWrapper[] modelWrappers)
    {
        String errorMessage = null;
        log.debug("committing report generation");

        EOEditingContext ec = localContext();

        ReportTemplate reportTemplate = localReportTemplate();

        // Create the generated report object and the job object.

        GeneratedReport report = GeneratedReport.create(ec, false, false);
        report.setDescription(localReportDescription());
        report.setReportTemplateRelationship(reportTemplate);
        report.setUserRelationship(user());

        ReportGenerationJob job = ReportGenerationJob.create(ec);
        job.setGeneratedReportRelationship(report);
        job.setUserRelationship(user());

        applyLocalChanges();

        // Create ReportDataSetQuery objects to map all of the data sets in
        // the transient state to the queries that were created for them.

        for (ModelOrQueryWrapper modelWrapper : modelWrappers)
        {
            ReportDataSet dataSet = modelWrapper.dataSet();
            ReportQuery query = modelWrapper.commitAndGetQuery(ec, user());

            ReportDataSetQuery dataSetQuery =
                report.createDataSetQueriesRelationship();

            dataSetQuery.setDataSetRelationship(dataSet);
            dataSetQuery.setReportQueryRelationship(query);

            applyLocalChanges();
        }

        setLocalGeneratedReport(report);

        // Set the job to be ready so that the queue will start processing it.

        job.setIsReady(true);
        applyLocalChanges();

        return errorMessage;
    }


    //~ Instance/static variables .............................................

    // Internal constants for key names
    private static final String KEY_REPORT_DESCRIPTION =
        "net.sf.webcat.reporter.reportDescription";
    private static final String KEY_REPORT_TEMPLATE =
        "net.sf.webcat.reporter.reportTemplate";
    private static final String KEY_GENERATED_REPORT =
        "net.sf.webcat.reporter.generatedReport";

    static Logger log = Logger.getLogger( ReporterComponent.class );

}
