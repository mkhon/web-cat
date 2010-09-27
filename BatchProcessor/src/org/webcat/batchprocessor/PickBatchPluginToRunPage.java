/*==========================================================================*\
 |  $Id: PickBatchPluginToRunPage.java,v 1.2 2010/09/27 00:15:32 stedwar2 Exp $
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

package org.webcat.batchprocessor;

import org.webcat.core.WCComponent;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.foundation.NSArray;

//-------------------------------------------------------------------------
/**
 * This page allows the user to select the batch plug-in that they want to run.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.2 $, $Date: 2010/09/27 00:15:32 $
 */
public class PickBatchPluginToRunPage extends WCComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new page.
     * @param context The page's context
     */
    public PickBatchPluginToRunPage(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    public WODisplayGroup pluginsDisplayGroup;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public WODisplayGroup pluginsDisplayGroup()
    {
        if (pluginsDisplayGroup == null)
        {
            NSArray<BatchPlugin> batchPlugins =
                BatchPlugin.pluginsAccessibleByUser(localContext(), user());

            pluginsDisplayGroup = new WODisplayGroup();
            pluginsDisplayGroup.setObjectArray(batchPlugins);
        }

        return pluginsDisplayGroup;
    }


    // ----------------------------------------------------------
    public WOComponent pluginChosen()
    {
        BatchPlugin batchPlugin =
            (BatchPlugin) pluginsDisplayGroup.selectedObject();

        DescribeBatchInputsPage page =
            pageWithName(DescribeBatchInputsPage.class);

        page.batchPlugin = batchPlugin;

        return page;
    }
}
