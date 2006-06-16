/*==========================================================================*\
 |  $Id: PluginManagerPage.java,v 1.1 2006/06/16 14:51:38 stedwar2 Exp $
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

package net.sf.webcat.grader;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import org.apache.log4j.*;
import net.sf.webcat.*;
import net.sf.webcat.core.*;

// -------------------------------------------------------------------------
/**
 *  The main "control panel" page for plugins in the Plug-ins
 *  tab.
 *
 *  @author  stedwar2
 *  @version $Id: PluginManagerPage.java,v 1.1 2006/06/16 14:51:38 stedwar2 Exp $
 */
public class PluginManagerPage
extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new page object.
     * 
     * @param context The context to use
     */
    public PluginManagerPage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public int            index;
    public ScriptFile     plugin;
    public WODisplayGroup publishedPluginGroup;
    public WODisplayGroup unpublishedPluginGroup;
    public WODisplayGroup personalPluginGroup;
    public NSData         uploadedData;
    public String         uploadedName;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse( WOResponse response, WOContext context )
    {
        if ( wcSession().user().hasAdminPrivileges() )
        {
            publishedPluginGroup.fetch();
            unpublishedPluginGroup.fetch();
        }
//        else
        {
            personalPluginGroup.queryBindings().setObjectForKey(
                wcSession().user(),
                "user"
            );
            personalPluginGroup.fetch();
        }
        super.appendToResponse( response, context );
    }


    // ----------------------------------------------------------
    /**
     * Get the current servlet adaptor, if one is available.
     * @return the servlet adaptor, or null when none is available
     */
    public net.sf.webcat.WCServletAdaptor adaptor()
    {
        return net.sf.webcat.WCServletAdaptor.getInstance();
    }


    // ----------------------------------------------------------
    /**
     * Determine if update download and installation support is active.
     * @return null to refresh the current page
     */
    public boolean canUpdate()
    {
        return adaptor() != null;
    }


    // ----------------------------------------------------------
    /**
     * Download the latest version of the current subsystem for updating
     * on restart.
     * @return null to refresh the current page
     */
    public WOComponent download()
    {
        clearErrors();
//        String msg = subsystem.descriptor().providerVersion().downloadTo(
//            adaptor().updateDownloadLocation() );
//        possibleErrorMessage( msg );
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Download a new subsystem for installation on restart.
     * @return null to refresh the current page
     */
    public WOComponent downloadNew()
    {
        clearErrors();
//        String msg = feature.providerVersion().downloadTo(
//            adaptor().updateDownloadLocation() );
//        possibleErrorMessage( msg );
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Scan the specified provider URL.
     * @return null to refresh the current page
     */
    public WOComponent scanNow()
    {
        clearErrors();
//        if ( providerURL == null || providerURL.equals( "" ) )
//        {
//            errorMessage( "Please specify a provider URL first." );
//        }
//        else
//        {
//            if ( FeatureProvider.getProvider( providerURL ) == null )
//            {
//                errorMessage( "Cannot read feature provider information from "
//                    + " specified URL: '" + providerURL + "'." );
//            }
//        }
//
//        // Erase cache of new subsystems so it will be recalculated now
//        newSubsystems = null;

        // refresh page
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Edit the selected plug-in's configuration settings.
     * @return the subsystem's edit page
     */
    public WOComponent edit()
    {
        // TODO: implement edit action for subsystem configuration
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Browse or edit the selected plug-in's files.  Administrators can
     * edit all plug-ins.  Otherwise, users can only edit plug-ins they
     * have authored, and can only browse others.
     * @return the subsystem's edit page
     */
    public WOComponent editFiles()
    {
        // TODO: implement edit file action for plug-ins
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Toggle the
     * {@link net.sf.webcat.WCServletAdaptor#willUpdateAutomatically()}
     * attribute.
     * @return null to refresh the current page
     */
    public WOComponent toggleAutoUpdates()
    {
//        net.sf.webcat.WCServletAdaptor adaptor = adaptor();
//        adaptor.setWillUpdateAutomatically(
//            !adaptor.willUpdateAutomatically() );
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Upload a new plug-in.
     * @return null to refresh the page
     */
    public WOComponent upload()
    {
        if ( errors == null )
        {
            errors = new NSMutableDictionary();
        }
        if ( uploadedName == null || uploadedData == null )
        {
            errorMessage( "Please select a file to upload." );
            return null;
        }
        ScriptFile.createNewScriptFile(
            wcSession().localContext(),
            wcSession().user(),
            uploadedName,
            uploadedData,
            false,
            true,
            errors
        );
        wcSession().commitLocalChanges();
        uploadedName = null;
        uploadedData = null;
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Publish/unpublish a plug-in by togglinh its isPublished attribute.
     * @return null to refresh the page
     */
    public WOComponent togglePublished()
    {
        plugin.setIsPublished( !plugin.isPublished() );
        wcSession().commitLocalChanges();
        return null;
    }
    
    //~ Instance/static variables .............................................
    static Logger log = Logger.getLogger( PluginManagerPage.class );
}
