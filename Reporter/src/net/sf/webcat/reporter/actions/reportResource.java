/*==========================================================================*\
 |  $Id: reportResource.java,v 1.3 2008/03/31 02:09:29 stedwar2 Exp $
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

package net.sf.webcat.reporter.actions;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSData;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import net.sf.webcat.core.DirectAction;
import net.sf.webcat.reporter.GeneratedReport;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * Return resources (like images) to which generated HTML reports refer.
 *
 * @author aallowat
 * @version $Id: reportResource.java,v 1.3 2008/03/31 02:09:29 stedwar2 Exp $
 */
public class reportResource
    extends DirectAction
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     * @param request The incoming request
     */
	public reportResource(WORequest request)
	{
		super(request);
	}


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
	public WOActionResults imageAction()
	{
		WOResponse response = new WOResponse();

		String uuid = request().stringFormValueForKey("uuid");
		String image = request().stringFormValueForKey("image");

		File file = new File(GeneratedReport.renderedResourcePath(uuid, image));

		try
		{
			NSData data = new NSData(new FileInputStream(file),
				(int)file.length());

			response.appendContentData(data);
		}
		catch (IOException e)
		{
			log.error(e);
		}

		return response;
	}


    // ----------------------------------------------------------
	public WOActionResults csvAction()
	{
		WOResponse response = new WOResponse();

		String uuid = request().stringFormValueForKey("uuid");
		String name = request().stringFormValueForKey("name");

		String filename = name + ".csv";

		response.setHeader("text/csv", "Content-Type");
		response.setHeader("attachment; filename=\"" + filename + "\"",
			"Content-Disposition");

		File file = new File(GeneratedReport.renderedResourcePath(uuid,
			filename));

		try
		{
			NSData data = new NSData(new FileInputStream(file),
				(int)file.length());

			response.appendContentData(data);
		}
		catch (IOException e)
		{
			log.error(e);
		}

		return response;
	}


    // ----------------------------------------------------------
	public WOActionResults genericAction()
	{
		WOResponse response = new WOResponse();

		String uuid = request().stringFormValueForKey("uuid");
		String name = request().stringFormValueForKey("name");
		String type = request().stringFormValueForKey("contentType");
		String filename = name;

		response.setHeader(type, "Content-Type");
		response.setHeader("attachment; filename=\"" + filename + "\"",
			"Content-Disposition");

		File file = new File(GeneratedReport.renderedResourcePath(uuid,
			filename));

		try
		{
			NSData data = new NSData(new FileInputStream(file),
				(int)file.length());

			response.appendContentData(data);
		}
		catch (IOException e)
		{
			log.error(e);
		}

		return response;
	}


    //~ Instance/static variables .............................................

	private static Logger log = Logger.getLogger(reportResource.class);
}
