/*==========================================================================*\
 |  $Id: reportResource.java,v 1.5 2008/04/15 04:09:23 aallowat Exp $
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

package net.sf.webcat.reporter.actions;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSData;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.sf.webcat.core.Application;
import net.sf.webcat.core.DirectAction;
import net.sf.webcat.reporter.GeneratedReport;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * Return resources (like images) to which generated HTML reports refer. Image
 * references in a rendered report use this direct action as their source URL
 * since this rendered content is not actually stored in a web-accessible
 * location.
 *
 * @author Tony Allevato
 * @version $Id: reportResource.java,v 1.5 2008/04/15 04:09:23 aallowat Exp $
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

        int reportId = Integer.parseInt(
                request().stringFormValueForKey("reportId"));
        String image = request().stringFormValueForKey("image");

        EOEditingContext ec = Application.newPeerEditingContext();
        GeneratedReport report = GeneratedReport.forId(ec, reportId);

        File file = new File(report.renderedResourcePath(image));

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

        Application.releasePeerEditingContext(ec);

        return response;
    }


    // ----------------------------------------------------------
    public WOActionResults csvAction()
    {
        WOResponse response = new WOResponse();

        int reportId = Integer.parseInt(
                request().stringFormValueForKey("reportId"));
        String name = request().stringFormValueForKey("name");

        String filename = name + ".csv";

        response.setHeader("text/csv", "Content-Type");
        response.setHeader("attachment; filename=\"" + filename + "\"",
            "Content-Disposition");

        EOEditingContext ec = Application.newPeerEditingContext();
        GeneratedReport report = GeneratedReport.forId(ec, reportId);

        File file = new File(report.renderedResourcePath(filename));

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

        Application.releasePeerEditingContext(ec);

        return response;
    }


    // ----------------------------------------------------------
    public WOActionResults genericAction()
    {
        WOResponse response = new WOResponse();

        int reportId = Integer.parseInt(
                request().stringFormValueForKey("reportId"));
        String name = request().stringFormValueForKey("name");
        String type = request().stringFormValueForKey("contentType");
        String filename = name;

        response.setHeader(type, "Content-Type");
        response.setHeader("attachment; filename=\"" + filename + "\"",
            "Content-Disposition");

        EOEditingContext ec = Application.newPeerEditingContext();
        GeneratedReport report = GeneratedReport.forId(ec, reportId);

        File file = new File(report.renderedResourcePath(filename));

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

        Application.releasePeerEditingContext(ec);

        return response;
    }


    //~ Instance/static variables .............................................

    private static Logger log = Logger.getLogger(reportResource.class);
}
