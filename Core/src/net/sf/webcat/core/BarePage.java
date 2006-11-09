/*==========================================================================*\
 |  $Id: BarePage.java,v 1.2 2006/11/09 16:55:11 stedwar2 Exp $
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

package net.sf.webcat.core;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * Places a titlebar and a grey window-looking rectangle around and behind its
 * contents.
 *
 * @author Stephen Edwards
 * @version $Id: BarePage.java,v 1.2 2006/11/09 16:55:11 stedwar2 Exp $
 */
public class BarePage
    extends WOComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new BarePage object.
     *
     * @param context The page's context
     */
    public BarePage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public String  title;
    public String  stylesheet;
    public String  externalJavascript;
    public String  inlineHeaderContents;
    public String  onLoad;
    public String  bodyClass;
    public Boolean omitStdStyleSheet;
    public boolean includePageWrapping = true;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse( WOResponse response, WOContext context )
    {
        log.debug( "nowrap = "
                   + context.request().stringFormValueForKey( "nowrap" ) );
        includePageWrapping =
            ( context.request().stringFormValueForKey( "nowrap" ) == null );
        super.appendToResponse( response, context );
    }


    // ----------------------------------------------------------
    /**
     * Returns the HTML page's title string.  This is the title
     * string that will show as the "page title" in the browser.
     * This generic implementation returns "Web-CAT", which is the
     * title that will be used for pages that do not provide one.
     * Ideally, subsystems will override this default.
     *
     * @return The page title
     */
    public String pageTitle()
    {
        return ( title == null )
                ? "Web-CAT"
                : ( "Web-CAT: " + title );
    }


    // ----------------------------------------------------------
    /**
     * Returns the text of a css "link" tag for use in the html header
     * if the "stylesheet" key has been bound, or null otherwise.
     * @return the text to use for the link tag
     */
    public String styleSheetLink()
    {
        String result = null;
        if ( stylesheet != null )
        {
            StringBuffer buffer = new StringBuffer();
            if ( stylesheet.indexOf( ',' ) >= 0 )
            {
                // Handle a list of javascript urls
                StringTokenizer st =
                    new StringTokenizer( stylesheet, "," );
                while ( st.hasMoreTokens() )
                {
                    oneStylesheetLink( buffer, st.nextToken() );
                }
            }
            else
            {
                oneStylesheetLink( buffer, stylesheet );
            }
            result = buffer.toString();
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Returns the text of a javascript "script" tag for use in the html header
     * if the "externalJavascript" key has been bound, or null otherwise.
     * @return the text to use for the script tag
     */
    public String javaScriptLink()
    {
        String result = null;
        if ( externalJavascript != null )
        {
            StringBuffer buffer = new StringBuffer();
            if ( externalJavascript.indexOf( ',' ) >= 0 )
            {
                // Handle a list of javascript urls
                StringTokenizer st =
                    new StringTokenizer( externalJavascript, "," );
                while ( st.hasMoreTokens() )
                {
                    oneJavaScriptLink( buffer, st.nextToken() );
                }
            }
            else
            {
                oneJavaScriptLink( buffer, externalJavascript );
            }
            result = buffer.toString();
        }
        return result;
    }


    // ----------------------------------------------------------
    private void oneJavaScriptLink( StringBuffer buffer, String url )
    {
        buffer.append( "<script type=\"text/javascript\" src=\"" );
        buffer.append( 
            WCResourceManager.frameworkPrefixedResourceURLFor( url ) );
        buffer.append( "\"></script>" );
    }


    // ----------------------------------------------------------
    private void oneStylesheetLink( StringBuffer buffer, String url )
    {
        buffer.append( "<link rel=\"stylesheet\" type=\"text/css\" href=\"" );
        buffer.append(
            WCResourceManager.frameworkPrefixedResourceURLFor( url ) );
        buffer.append( "\"/>" );
    }


    // ----------------------------------------------------------
    public String wcStylesheet()
    {
        return WCResourceManager.frameworkPrefixedResourceURLFor(
            "Core.framework/WebServerResources/wc.css" );
    }


    // ----------------------------------------------------------
    public String wcIE5Stylesheet()
    {
        return WCResourceManager.frameworkPrefixedResourceURLFor(
            "Core.framework/WebServerResources/wc-ie5.css" );
    }


    // ----------------------------------------------------------
    public String wcIE6Stylesheet()
    {
        return WCResourceManager.frameworkPrefixedResourceURLFor(
            "Core.framework/WebServerResources/wc-ie6.css" );
    }


    // ----------------------------------------------------------
    public String overlibLink()
    {
        if ( overlibLink == null )
        {
            overlibLink = "<script type=\"text/javascript\" src=\""
                + WCResourceManager.frameworkPrefixedResourceURLFor(
            "Core.framework/WebServerResources/overlib/Mini/overlib_mini.js" )
                + "\"></script>";
        }
        return overlibLink;
    }


    //~ Instance/static variables .............................................

    private static String overlibLink = null;

    static Logger log = Logger.getLogger( BarePage.class );
}
