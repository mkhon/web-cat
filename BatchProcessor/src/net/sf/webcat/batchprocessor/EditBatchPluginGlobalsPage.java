/*==========================================================================*\
 |  $Id: EditBatchPluginGlobalsPage.java,v 1.1 2010/04/19 15:24:14 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2010 Virginia Tech
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

package net.sf.webcat.batchprocessor;

import org.apache.log4j.Logger;
import net.sf.webcat.core.WCComponent;
import com.webobjects.appserver.*;

//-------------------------------------------------------------------------
/**
 *  This class allows one to edit the global settings for a plug-in.
 *  The creator of this page must set the plugin attribute before
 *  rendering the page.
 *
 *  @author Stephen Edwards
 * @author  latest changes by: $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2010/04/19 15:24:14 $
 */
public class EditBatchPluginGlobalsPage extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * This is the default constructor
     *
     * @param context The page's context
     */
    public EditBatchPluginGlobalsPage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public BatchPlugin   plugin;
    public java.io.File baseDir;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        log.debug( "appendToResponse()" );
        if ( baseDir == null )
        {
            baseDir = new java.io.File ( BatchPlugin.userPluginDirName(
                user(), true ).toString() );
        }
        if ( log.isDebugEnabled() )
        {
            log.debug( "plug-in global settings =\n"
                + plugin.globalConfigSettings() );
        }
        super.appendToResponse( response, context );
    }


    // ----------------------------------------------------------
    public WOComponent defaultAction()
    {
        return null;
    }


    //~ Static/instance variables .............................................

    private static final Logger log = Logger.getLogger(
            EditBatchPluginGlobalsPage.class);
}
