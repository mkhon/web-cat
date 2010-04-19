/*==========================================================================*\
 |  $Id: BatchResultPage.java,v 1.1 2010/04/19 15:24:14 aallowat Exp $
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

import java.io.File;
import net.sf.webcat.core.DeliverFile;
import net.sf.webcat.core.EntityResourceRequestHandler;
import net.sf.webcat.core.WCComponent;
import net.sf.webcat.ui.util.ComponentIDGenerator;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;

//-------------------------------------------------------------------------
/**
 * Displays the results of a batch job.
 *
 * @author  Tony Allevato
 * @version $Id: BatchResultPage.java,v 1.1 2010/04/19 15:24:14 aallowat Exp $
 */
public class BatchResultPage extends WCComponent
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
    public ComponentIDGenerator idFor;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        idFor = new ComponentIDGenerator(this);

        super.appendToResponse(response, context);
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
    public WOActionResults cancelJob()
    {
        BatchJob job = result.batchJob();

        if (job != null)
        {
            job.setIsCancelled(true);
            job.editingContext().saveChanges();

            return pageWithName(PickBatchResultToViewPage.class);
        }
        else
        {
            return null;
        }
    }


    // ----------------------------------------------------------
    public WOActionResults restartJob()
    {
        BatchJob job = result.batchJob();

        if (job != null)
        {
            job.setIsReady(true);
            job.editingContext().saveChanges();
        }

        return null;
    }


    // ----------------------------------------------------------
    public WOActionResults downloadStderrFile()
    {
        File resultDir = new File(result.resultDirName());
        File stderrFile = new File(resultDir, "stderr.txt");

        DeliverFile deliver = pageWithName(DeliverFile.class);
        deliver.setFileName(stderrFile);
        deliver.setContentType("text/plain");

        return deliver;
    }


    //~ Static/instance variables .............................................

    private NSArray<BatchFeedbackSection> cachedFeedbackSections;
}
