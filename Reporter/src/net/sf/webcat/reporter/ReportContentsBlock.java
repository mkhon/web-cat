/*==========================================================================*\
 |  $Id: ReportContentsBlock.java,v 1.5 2008/04/02 01:36:38 stedwar2 Exp $
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
import com.webobjects.foundation.NSData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * A component that displays the contents of a generated report.
 *
 * @author Tony Allevato
 * @version $Id: ReportContentsBlock.java,v 1.5 2008/04/02 01:36:38 stedwar2 Exp $
 */
public class ReportContentsBlock
    extends ReporterComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new page.
     * @param context The page's context
     */
    public ReportContentsBlock(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    public GeneratedReport generatedReport;
	public ReporterComponent owner;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
    	try
    	{
        	IRenderingMethod method =
        		Reporter.getInstance().renderingMethodWithName(
        			localRenderingMethod());
        	method.appendContentToResponse(generatedReport, response, context);
		}
    	catch (IOException e)
		{
    		log.error("An error occurred while reading the rendered report", e);

    		response.appendContentString("<p>There was an error reading the " +
    				"rendered report contents.</p>");
		}

    	super.appendToResponse(response, context);
    }


    //~ Instance/static variables .............................................

    private static Logger log = Logger.getLogger(ReportContentsBlock.class);
}