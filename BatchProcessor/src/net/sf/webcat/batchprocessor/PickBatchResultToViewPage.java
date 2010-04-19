/*==========================================================================*\
 |  $Id: PickBatchResultToViewPage.java,v 1.1 2010/04/19 15:24:14 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2010 Virginia Tech
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

import net.sf.webcat.core.WCComponent;
import net.sf.webcat.ui.generators.JavascriptGenerator;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableDictionary;

//-------------------------------------------------------------------------
/**
 * A page that lets the user select the batch results that he wants to view,
 * or view the progress of any jobs currently in the queue.
 *
 * @author  Tony Allevato
 * @version $Id: PickBatchResultToViewPage.java,v 1.1 2010/04/19 15:24:14 aallowat Exp $
 */
public class PickBatchResultToViewPage extends WCComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new page.
     * @param context The page's context
     */
    public PickBatchResultToViewPage(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    public WODisplayGroup batchResultDisplayGroup;
    public WODisplayGroup batchJobDisplayGroup;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        NSMutableDictionary bindings;

        bindings = batchResultDisplayGroup.queryBindings();
        bindings.setObjectForKey(user(), "user");
        batchResultDisplayGroup.fetch();

        bindings = batchJobDisplayGroup.queryBindings();
        bindings.setObjectForKey(user(), "user");
        batchJobDisplayGroup.fetch();

        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public WOComponent viewBatchResult()
    {
        BatchResult result = (BatchResult)
            batchResultDisplayGroup.selectedObject();

        if (result != null)
        {
            BatchResultPage page = pageWithName(BatchResultPage.class);
            page.result = result;
            return page;
        }
        else
        {
            return null;
        }
    }


    // ----------------------------------------------------------
    public WOActionResults deleteSelectedResults()
    {
        NSArray<BatchResult> results =
            batchResultDisplayGroup.selectedObjects();

        for (BatchResult result : results)
        {
            localContext().deleteObject(result);
        }

        localContext().saveChanges();

        batchResultDisplayGroup.clearSelection();
        batchResultDisplayGroup.fetch();

        return new JavascriptGenerator().refresh("batchResultContainer");
    }


    // ----------------------------------------------------------
    public WOComponent viewBatchProgress()
    {
        BatchJob job = (BatchJob) batchJobDisplayGroup.selectedObject();
        BatchResult result = job.batchResult();

        if (result != null)
        {
            BatchResultPage page = pageWithName(BatchResultPage.class);
            page.result = result;
            return page;
        }
        else
        {
            return null;
        }
    }
}
