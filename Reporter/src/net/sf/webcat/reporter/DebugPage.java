/*==========================================================================*\
 |  $Id: DebugPage.java,v 1.6 2008/04/02 01:36:38 stedwar2 Exp $
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
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import er.extensions.ERXEOControlUtilities;
import java.io.PrintWriter;
import java.io.StringWriter;
import net.sf.webcat.core.WCComponent;
import net.sf.webcat.grader.Submission;
import ognl.webobjects.WOOgnl;

//-------------------------------------------------------------------------
/**
 * Provides debugging output.
 *
 * @author aallowat
 * @version $Id: DebugPage.java,v 1.6 2008/04/02 01:36:38 stedwar2 Exp $
 */
public class DebugPage
    extends ReporterComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * This is the default constructor
     * @param context The page's context
     */
    public DebugPage(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

	public NSArray<Submission> results;
	public Submission result;
	public NSMutableArray<String> columns;
	public String column;
	public int index;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
    	columns = new NSMutableArray<String>();
    	columns.addObject("user.userName");
    	columns.addObject("result.finalScore");
    	columns.addObject("assignmentOffering.courseOffering.compactName");
    	columns.addObject("assignmentOffering.assignment.titleString");

    	String uuid = localDataSetUuids().objectAtIndex(0);
    	System.out.println("Getting qualifier for dataset " + uuid);

    	super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public String currentRowColumnValue()
    {
    	try
    	{
    		return result.valueForKeyPath(column).toString();
    	}
    	catch (Exception e)
    	{
    		return "<error>";
    	}
    }
}