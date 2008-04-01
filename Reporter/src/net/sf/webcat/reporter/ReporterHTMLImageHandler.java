/*==========================================================================*\
 |  $Id: ReporterHTMLImageHandler.java,v 1.2 2008/04/01 19:09:46 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2008 Virginia Tech
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

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import net.sf.webcat.reporter.actions.reportResource;
import org.apache.log4j.Logger;
import org.eclipse.birt.report.engine.api.HTMLImageHandler;
import org.eclipse.birt.report.engine.api.IHTMLRenderOption;
import org.eclipse.birt.report.engine.api.IImage;
import org.eclipse.birt.report.engine.api.script.IReportContext;

//-------------------------------------------------------------------------
/**
 * A BIRT image handler for HTML-rendered reports.
 *
 * @author Tony Allevato
 * @version $Id: ReporterHTMLImageHandler.java,v 1.2 2008/04/01 19:09:46 stedwar2 Exp $
 */
public class ReporterHTMLImageHandler
    extends HTMLImageHandler
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     * @param report The report containing the images
     * @param renderedResourceActionUrl The base URL for accessing rendered
     *     images in the final report
     */
	public ReporterHTMLImageHandler(
        GeneratedReport report, String renderedResourceActionUrl)
	{
		this.reportUuid = report.uuid();
		this.renderedResourceActionUrl = renderedResourceActionUrl;
	}


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
	@Override
	public String onCustomImage(IImage image, Object context)
	{
		return handleImage(image, context, "custom", false);
	}


    // ----------------------------------------------------------
	@Override
	public String onDesignImage(IImage image, Object context)
	{
		return handleImage(image, context, "design", true);
	}


    // ----------------------------------------------------------
	@Override
	public String onDocImage(IImage image, Object context)
	{
		return null;
	}


    // ----------------------------------------------------------
	@Override
	public String onURLImage(IImage image, Object context)
	{
		String uri = image.getID();

		if (uri.startsWith("http:") || uri.startsWith("https:"))
		{
			return uri;
		}

		return handleImage(image, context, "uri", true);
	}


    // ----------------------------------------------------------
	@Override
	public String onFileImage(IImage image, Object context)
	{
		return handleImage(image, context, "file", true);
	}


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
	/**
	 * Handles an image report item and returns an image URL.
	 *
	 * @param image
	 *            represents the image design information
	 * @param context
	 *            context information
	 * @param prefix
	 *            image prefix in URL
	 * @param needMap
	 *            whether image map is needed
	 * @return URL for the image
	 */
	protected String handleImage(
        IImage image, Object context, String prefix, boolean needMap)
	{
		String imageKey = null;
		if (needMap)
		{
			imageKey = getImageDictionaryKey(image);
			if (imageDictionary.containsKey(imageKey))
			{
				return (String)imageDictionary.objectForKey(imageKey);
			}
		}

		String imageName = prefix + "-" + UUID.randomUUID().toString();

		String extension = image.getExtension();
		if (extension != null && extension.length() > 0)
		{
			imageName += extension;
		}

		String imagePath = GeneratedReport.renderedResourcePath(
            reportUuid, imageName);

		try
		{
			File file = new File(imagePath);
			image.writeImage(file);
		}
		catch ( IOException e )
		{
			log.error("Could not write image file to " + imagePath, e);
		}

		NSMutableDictionary parameters = new NSMutableDictionary();
		parameters.setObjectForKey(reportUuid, "uuid");
		parameters.setObjectForKey(imageName, "image");

		String imageURL = appendParametersToActionUrl(parameters);

		if (needMap)
		{
			imageDictionary.setObjectForKey(imageURL, imageKey);
		}

		return imageURL;
	}


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
	@SuppressWarnings("deprecation")
	private String appendParametersToActionUrl(NSDictionary parameters)
	{
		if (parameters == null)
        {
			return renderedResourceActionUrl;
        }

		StringBuffer query = new StringBuffer(renderedResourceActionUrl);

		if (!renderedResourceActionUrl.contains("?"))
        {
			query.append('?');
        }
		else
        {
			query.append('&');
        }

		Enumeration e = parameters.keyEnumerator();
		while (e.hasMoreElements())
		{
			String key = e.nextElement().toString();
			query.append( URLEncoder.encode(key));
			query.append( '=' );
			query.append( URLEncoder.encode(
					parameters.objectForKey(key).toString()));

			if (e.hasMoreElements())
	        {
	            query.append( '&' );
	        }
	    }

		return query.toString();
	}


    // ----------------------------------------------------------
	/**
	 * returns the unique identifier for the image
	 *
	 * @param image
	 *            the image object
	 * @return the image id
	 */
	private String getImageDictionaryKey(IImage image)
	{
		if (image.getReportRunnable( ) != null)
		{
			return image.getReportRunnable().hashCode() + image.getID();
		}

		return image.getID();
	}


    //~ Instance/static variables .............................................

    private String reportUuid;
    private String renderedResourceActionUrl;

    private static NSMutableDictionary imageDictionary =
        new NSMutableDictionary();

	private static Logger log =
        Logger.getLogger(ReporterHTMLImageHandler.class);
}
