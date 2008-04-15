/*==========================================================================*\
 |  $Id: PickTemplateToGeneratePage.java,v 1.7 2008/04/15 04:09:22 aallowat Exp $
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
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import net.sf.webcat.core.WCComponent;
import net.sf.webcat.grader.EditPluginGlobalsPage;

//-------------------------------------------------------------------------
/**
 * This page allows the user to select the template to use for a new report.
 *
 * @author Tony Allevato
 * @version $Id: PickTemplateToGeneratePage.java,v 1.7 2008/04/15 04:09:22 aallowat Exp $
 */
public class PickTemplateToGeneratePage
    extends ReporterComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new page.
     * @param context The page's context
     */
    public PickTemplateToGeneratePage(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    public NSArray<ReportTemplate> reportTemplates;
    public ReportTemplate reportTemplate;
    public int index;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public NSArray<ReportTemplate> reportTemplates()
    {
        if (reportTemplates == null)
        {
            reportTemplates =
                ReportTemplate.objectsForAllTemplates(localContext());
        }
        return reportTemplates;
    }


    // ----------------------------------------------------------
    public WOComponent templateChosen()
    {
        clearLocalReportState();

        setLocalReportTemplate(reportTemplate);
        setLocalCurrentReportDataSet(0);
        createLocalPageController();

        return localPageController().nextPage();
    }
}