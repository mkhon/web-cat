/*==========================================================================*\
 |  $Id: PartialInlineReport.java,v 1.3 2010/09/27 04:23:20 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2009 Virginia Tech
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

package org.webcat.grader;

import com.webobjects.appserver.*;
import er.extensions.foundation.ERXFileUtilities;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * A page for inlining an HTML fragment stored in a file.  The
 * file property should be bound to a File object referring to the
 * fragment to include.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.3 $, $Date: 2010/09/27 04:23:20 $
 */
public class PartialInlineReport
    extends WOComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * This is the default constructor
     *
     * @param context The page's context
     */
    public PartialInlineReport( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public String title;
    public boolean open;
    public boolean substituteOldCollapsingRegions = true;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Adds to the response of the page
     *
     * @param response The response being built
     * @param context  The context of the request
     */
    public void appendToResponse( WOResponse response, WOContext context )
    {
        useModule = title != null;
        if ( file != null )
        {
            // Only read the file if it is really there, of course
            if ( file.exists() )
            {
                try
                {
                    content = ERXFileUtilities.stringFromFile(file, "UTF-8");

                    if (substituteOldCollapsingRegions)
                    {
                    boolean nestedFound = false;

                    // Try to substitute out old collapsing regions
                    // First, tackle nested regions
                    {
                        Matcher m = NESTED_MODULE.matcher(content);
                        StringBuffer translated =
                            new StringBuffer(content.length());
                        int pos = 0;
                        while (m.find())
                        {
                            int start = m.start();
                            if (start > pos)
                            {
                                translated.append(
                                    content.substring(pos, start));
                            }
                            boolean sectionOpen = "expanded".equals(m.group(1));
                            String sectionTitle = m.group(2);
                            String body = m.group(4);
                            translated.append("<nesteddiv "
                                + "dojoType=\"webcat.TitlePane\" title=\"");
                            translated.append(
                                sectionTitle.replace("&", "&amp;")
                                .replace("<", "&lt;")
                                .replace(">", "&gt;")
                                .replace("\"", "&quot;"));
                            translated.append("\" open=\"");
                            translated.append(sectionOpen);
                            translated.append("\">\n");
                            translated.append(body);
                            translated.append("</nesteddiv>");
                            pos = m.end();
                        }
                        if (pos > 0)
                        {
                            translated.append(content.substring(pos));
                            content = translated.toString();
                            nestedFound = true;
                            useModule = false;
                        }
                    }

                    // Now handle outer regions
                    {
                        Matcher m = MODULE.matcher(content);
                        StringBuffer translated =
                            new StringBuffer(content.length());
                        int pos = 0;
                        while (m.find())
                        {
                            int start = m.start();
                            if (start > pos)
                            {
                                translated.append(
                                    content.substring(pos, start));
                            }
                            boolean sectionOpen = "expanded".equals(m.group(1));
                            String sectionTitle = m.group(2);
                            String body = m.group(4);
                            translated.append("<div class=\"module\"><div "
                                + "dojoType=\"webcat.TitlePane\" title=\"");
                            translated.append(
                                sectionTitle.replace("&", "&amp;")
                                .replace("<", "&lt;")
                                .replace(">", "&gt;")
                                .replace("\"", "&quot;"));
                            translated.append("\" open=\"");
                            translated.append(sectionOpen);
                            translated.append("\">\n");
                            translated.append(body);
                            translated.append("</div></div>");
                            pos = m.end();
                        }
                        if (pos > 0)
                        {
                            translated.append(content.substring(pos));
                            content = translated.toString();
                            useModule = false;
                        }
                    }

                    if (nestedFound)
                    {
                        content =
                            content.replaceAll("<(/?)nesteddiv", "<$1div");
                    }
                    }
                }
                catch ( Exception e )
                {
                    log.error( "Exception including inlined report:", e );
                }
            }
        }
        else
        {
            log.warn( "file property is null" );
        }
        super.appendToResponse( response, context );
    }


    // ----------------------------------------------------------
    public boolean useModule()
    {
        return useModule;
    }


    // ----------------------------------------------------------
    public boolean hasData()
    {
        return content != null;
    }


    // ----------------------------------------------------------
    public String content()
    {
        return content;
    }


    // ----------------------------------------------------------
    /**
     * Access the file property value.
     *
     * @return the file property's current value
     */
    public File file()
    {
        return file;
    }


    // ----------------------------------------------------------
    /**
     * Set the file property.
     *
     * @param value the new value for the property
     */
    public void setFile( File value )
    {
        file = value;
    }


    //~ Instance/static variables .............................................

    private File   file;
    private String content;
    private boolean useModule = false;

    static final Pattern MODULE = Pattern.compile(
        "<h2 class=\"collapsible\"><a[^>]*><img[^>]*(collapsed|expanded)[^>]*>"
        + "((.(?!</a>))*.)</a>\\s*</h2>\\s*"
        + "<div[^>]*\\sclass=\"expboxcontent\"[^>]*>((.(?!</div>))*.)</div>"
        ,
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    static final Pattern NESTED_MODULE = Pattern.compile(
        "<h3 class=\"collapsible\"><a[^>]*><img[^>]*(collapsed|expanded)[^>]*>"
        + "((.(?!</a>))*.)</a>\\s*</h3>\\s*"
        + "<div[^>]*\\sclass=\"expboxcontent\"[^>]*>((.(?!</div>))*.)</div>"
        ,
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    static Logger log = Logger.getLogger( PartialInlineReport.class );
}
