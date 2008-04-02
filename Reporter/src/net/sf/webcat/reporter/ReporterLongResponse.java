/*==========================================================================*\
 |  $Id: ReporterLongResponse.java,v 1.3 2008/04/02 01:36:38 stedwar2 Exp $
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

package net.sf.webcat.reporter;

import com.webobjects.appserver.*;
import er.extensions.ERXConstant;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * A long response component that displays a progress bar until the
 * real content is ready, and then updates the content using AJAX.
 *
 * @author Tony Allevato
 * @version $Id: ReporterLongResponse.java,v 1.3 2008/04/02 01:36:38 stedwar2 Exp $
 */
public class ReporterLongResponse
    extends WOComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Default constructor.
     * @param aContext The page's context
     */
    public ReporterLongResponse(WOContext aContext)
    {
        super(aContext);

        doneAndRefreshed = false;
        refreshInterval = ERXConstant.ZeroInteger;
        performingAction = false;
    }


    //~ KVC Attributes (must be public) .......................................

    public Object jobToken;
    public AjaxLongResponseHandler handler;
    public String cancellationMessage;
    public String workingMessage;

    // These two keys ensure that the task is only checked once, so that
    // all conditionals in the template always return consistent results,
    // even if the task finishes part-way through generation of this page
    public boolean isDone      = false;
    public boolean isCancelled = false;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public int refreshInterval()
    {
    	if (ERXConstant.ZeroInteger.equals(refreshInterval))
        {
    		Number n = (Number)valueForBinding("refreshInterval");
    		if (n != null)
            {
    			refreshInterval = n;
    		}
    	}
    	return refreshInterval.intValue();
    }


    // ----------------------------------------------------------
    public void setRefreshInterval(int value)
    {
    	refreshInterval = new Integer(value);
    }


    // ----------------------------------------------------------
    public double percentOfWorkDone()
    {
    	ProgressManager progress = ProgressManager.getInstance();

    	if (jobToken == null || !progress.jobExists(jobToken))
    	{
    		return 0;
    	}
    	else
    	{
    		return progress.percentDoneOfJob(jobToken);
    	}
    }


    // ----------------------------------------------------------
    public String taskDescription()
    {
    	ProgressManager progress = ProgressManager.getInstance();

    	if (jobToken != null && progress.jobExists(jobToken))
    	{
    		return progress.descriptionOfCurrentTaskForJob(jobToken);
    	}
    	else
    	{
    		return null;
    	}
    }


    // ----------------------------------------------------------
    public void setPercentOfWorkDone(double value)
    {
    	// Keep KVC happy.
    }


    // ----------------------------------------------------------
    public void setTaskDescription(String value)
    {
    	// Keep KVC happy.
    }


    // ----------------------------------------------------------
    public WOActionResults cancelJob()
    {
    	handler.cancel();
    	isCancelled = true;
    	return null;
    }


    // ----------------------------------------------------------
    public void awake()
    {
    	ProgressManager progress = ProgressManager.getInstance();

        if (jobToken != null && progress.jobExists(jobToken))
        {
            isDone = progress.isJobDone(jobToken);

//            isCancelled =
//                ( (LongResponseTaskWithProgress)task() ).isCancelled();
        }
    }


    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
    	super.appendToResponse(response, context);
    }


    //~ Instance/static variables .............................................

    protected Number refreshInterval;
    protected boolean performingAction;
    protected boolean doneAndRefreshed;

    static Logger log = Logger.getLogger( ReporterLongResponse.class );
}