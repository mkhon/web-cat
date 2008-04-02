/*==========================================================================*\
 |  $Id: PickReportToViewPage.java,v 1.5 2008/04/02 01:36:38 stedwar2 Exp $
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
 * @author aallowat
 * @version $Id: PickReportToViewPage.java,v 1.5 2008/04/02 01:36:38 stedwar2 Exp $
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
    public EnqueuedReportJob enqueuedReport;
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
    	setLocalReportUuid(generatedReport.uuid());
    	commitReportRendering();
    	return pageWithName(GeneratedReportPage.class.getName());
    }


    // ----------------------------------------------------------
    public WOComponent viewReportProgress()
    {
    	setLocalReportUuid(enqueuedReport.uuid());
    	return pageWithName(GeneratedReportPage.class.getName());
    }


    // ----------------------------------------------------------
    public String enqueuedReportProgress()
    {
    	ProgressManager manager = ProgressManager.getInstance();
    	double done = manager.percentDoneOfJob(enqueuedReport.uuid());
    	int percent = (int)Math.floor(done * 100 + 0.5);
    	return "" + percent + "%";
    }
}