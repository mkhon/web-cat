/*==========================================================================*\
 |  $Id: BatchResultPage.java,v 1.1 2010/05/11 14:51:46 aallowat Exp $
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

package org.webcat.batchprocessor;

import java.io.File;
import org.webcat.core.DeliverFile;
import org.webcat.core.EntityResourceRequestHandler;
import org.webcat.core.WCComponent;
import org.webcat.jobqueue.WCPageWithJobMonitoring;
import org.webcat.ui.util.ComponentIDGenerator;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;

//-------------------------------------------------------------------------
/**
 * Displays the results of a batch job.
 *
 * @author  Tony Allevato
 * @version $Id: BatchResultPage.java,v 1.1 2010/05/11 14:51:46 aallowat Exp $
 */
public class BatchResultPage extends WCComponent
    implements WCPageWithJobMonitoring.Delegate
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public BatchResultPage(WOContext context)
    {
        super(context);
    }


    //~ KVC attributes (must be public) .......................................

    public BatchResult result;
    public BatchFeedbackSection feedbackSection;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public BatchResultPage self()
    {
        return this;
    }


    // ----------------------------------------------------------
    public NSArray<BatchFeedbackSection> feedbackSections()
    {
        if (result.isComplete() && cachedFeedbackSections == null)
        {
            cachedFeedbackSections =
                result.sortedVisibleFeedbackSections(user());
        }

        return cachedFeedbackSections;
    }


    // ----------------------------------------------------------
    public String feedbackSectionResourceURL()
    {
        String filename = feedbackSection.fileName();

        return EntityResourceRequestHandler.urlForEntityResource(
                context(), result, filename);
    }


    // ----------------------------------------------------------
    public WOActionResults jobWasCancelled()
    {
        return pageWithName(PickBatchResultToViewPage.class);
    }


    //~ Static/instance variables .............................................

    private NSArray<BatchFeedbackSection> cachedFeedbackSections;
}
