/*==========================================================================*\
 |  $Id: PickReportToViewPage.java,v 1.10 2009/06/09 17:43:10 aallowat Exp $
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
 * @version $Id: PickReportToViewPage.java,v 1.10 2009/06/09 17:43:10 aallowat Exp $
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
    public WODisplayGroup enqueuedReportsDisplayGroup;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        NSMutableDictionary bindings;
        
        bindings = generatedReportsDisplayGroup.queryBindings();
        bindings.setObjectForKey(user(), "user");
        generatedReportsDisplayGroup.fetch();

        bindings = enqueuedReportsDisplayGroup.queryBindings();
        bindings.setObjectForKey(user(), "user");
        enqueuedReportsDisplayGroup.fetch();

        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public WOComponent viewReport()
    {
        GeneratedReport report = (GeneratedReport)
            generatedReportsDisplayGroup.selectedObject();

        if (report != null)
        {
            setLocalGeneratedReport(report);
            return pageWithName(GeneratedReportPage.class.getName());
        }
        else
        {
            return null;
        }
    }


    // ----------------------------------------------------------
    public WOActionResults deleteSelectedReports()
    {
        NSArray<GeneratedReport> reports =
            generatedReportsDisplayGroup.selectedObjects();

        for (GeneratedReport report : reports)
        {
            localContext().deleteObject(report);
        }

        localContext().saveChanges();

        generatedReportsDisplayGroup.fetch();

        return null;
    }

    
    // ----------------------------------------------------------
    public WOComponent viewReportProgress()
    {
        EnqueuedReportGenerationJob job = (EnqueuedReportGenerationJob)
            enqueuedReportsDisplayGroup.selectedObject();

        if (job != null)
        {
            setLocalReportGenerationJob(job);
            return pageWithName(GeneratedReportPage.class.getName());
        }
        else
        {
            return null;
        }
    }
}
