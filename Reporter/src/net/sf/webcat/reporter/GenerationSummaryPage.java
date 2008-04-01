/*==========================================================================*\
 |  $Id: GenerationSummaryPage.java,v 1.4 2008/04/01 02:48:25 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU General Public License as published by
 |  the Free Software Foundation; either version 2 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU General Public License
 |  along with Web-CAT; if not, write to the Free Software
 |  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 |
 |  Project manager: Stephen Edwards <edwards@cs.vt.edu>
 |  Virginia Tech CS Dept, 660 McBryde Hall (0106), Blacksburg, VA 24061 USA
\*==========================================================================*/

package net.sf.webcat.reporter;

import com.webobjects.appserver.*;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;

//-------------------------------------------------------------------------
/**
 * This page summarizes the report that will be generated.
 *
 * @author  Anthony Allevato
 * @version $Id: GenerationSummaryPage.java,v 1.4 2008/04/01 02:48:25 stedwar2 Exp $
 */
public class GenerationSummaryPage
    extends ReporterComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new page.
     * @param context The page's context
     */
    public GenerationSummaryPage(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

	public ReportTemplate         reportTemplate;
	public NSArray<ReportDataSet> dataSets;
	public ReportDataSet          dataSet;
	public int                    index;
	public String                 reportDescription;
	public IRenderingMethod       renderingMethod;
	public IRenderingMethod       selectedRenderingMethod;


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
    	reportTemplate = localReportTemplate();
    	dataSets = reportTemplate.dataSets();

    	super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public String defaultDescription()
    {
    	return reportTemplate.name();
    }


    // ----------------------------------------------------------
    public String querySummaryForDataSet()
    {
    	ReportQuery query = queryForLocalDataSetUuid(dataSet.uuid());
    	return query.qualifier().toString();
    }


    // ----------------------------------------------------------
    public NSArray<IRenderingMethod> renderingMethods()
    {
    	return Reporter.getInstance().allRenderingMethods();
    }


    // ----------------------------------------------------------
    public WOComponent goNext()
    {
    	String desc = reportDescription;
    	if (desc == null)
    	{
    		desc = defaultDescription();
    	}

    	setLocalReportDescription(desc);
    	setLocalRenderingMethod(selectedRenderingMethod.methodName());

    	commitReportGeneration();
    	return pageWithName(GeneratedReportPage.class.getName());
    }
}