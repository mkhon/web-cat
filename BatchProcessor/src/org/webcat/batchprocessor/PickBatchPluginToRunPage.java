/*==========================================================================*\
 |  $Id: PickBatchPluginToRunPage.java,v 1.3 2010/10/28 00:39:20 aallowat Exp $
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
import er.extensions.appserver.ERXDisplayGroup;

//-------------------------------------------------------------------------
/**
 * This page allows the user to select the batch plug-in that they want to run.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: aallowat $
 * @version $Revision: 1.3 $, $Date: 2010/10/28 00:39:20 $
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

    public ERXDisplayGroup<BatchPlugin> pluginsDisplayGroup;
    public BatchPlugin batchPlugin;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public WODisplayGroup pluginsDisplayGroup()
    {
        if (pluginsDisplayGroup == null)
        {
            NSArray<BatchPlugin> batchPlugins =
                BatchPlugin.pluginsAccessibleByUser(localContext(), user());

            pluginsDisplayGroup = new ERXDisplayGroup<BatchPlugin>();
            pluginsDisplayGroup.setObjectArray(batchPlugins);
        }

        return pluginsDisplayGroup;
    }


    // ----------------------------------------------------------
    public WOComponent pluginChosen()
    {
        DescribeBatchInputsPage page =
            pageWithName(DescribeBatchInputsPage.class);

        page.batchPlugin = batchPlugin;

        return page;
    }
}
