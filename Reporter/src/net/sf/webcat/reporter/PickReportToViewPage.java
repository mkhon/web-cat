/*==========================================================================*\
 |  $Id: PickReportToViewPage.java,v 1.6 2008/04/15 04:09:22 aallowat Exp $
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

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.EODatabaseDataSource;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import net.sf.webcat.core.MutableDictionary;

//-------------------------------------------------------------------------
/**
 * This page allows the user to select among already-generated reports.
 *
 * @author Tony Allevato
 * @version $Id: PickReportToViewPage.java,v 1.6 2008/04/15 04:09:22 aallowat Exp $
 */
public class PickReportToViewPage
    extends ReporterComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new page.
     * @param context The page's context
     */
    public PickReportToViewPage(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    public WODisplayGroup generatedReportsDisplayGroup;
    public GeneratedReport generatedReport;
    public int index;
    public WODisplayGroup enqueuedReportsDisplayGroup;
    public EnqueuedReportGenerationJob enqueuedReport;
    public int enqueuedIndex;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        EODatabaseDataSource source = (EODatabaseDataSource)
            generatedReportsDisplayGroup.dataSource();

        NSMutableDictionary bindings = new NSMutableDictionary();
        bindings.setObjectForKey(user(), "user");
        source.setQualifierBindings(bindings);

        source = (EODatabaseDataSource)
            enqueuedReportsDisplayGroup.dataSource();

        bindings = new NSMutableDictionary();
        bindings.setObjectForKey(user(), "user");
        source.setQualifierBindings(bindings);

        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public WOComponent viewReport()
    {
        commitReportRendering(generatedReport);
        return pageWithName(GeneratedReportPage.class.getName());
    }


    // ----------------------------------------------------------
    public WOComponent viewReportProgress()
    {
        setLocalReportGenerationJob(enqueuedReport);
        return pageWithName(GeneratedReportPage.class.getName());
    }


    // ----------------------------------------------------------
    public String enqueuedReportProgress()
    {
        int jobId = enqueuedReport.id().intValue();

        float workDone = ReportGenerationTracker.getInstance().
            fractionOfWorkDoneForJobId(jobId);

        int percent = (int)Math.floor(workDone * 100 + 0.5);
        return "" + percent + "%";
    }
}