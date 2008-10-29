/*==========================================================================*\
 |  $Id: ReporterLongResponse.java,v 1.5 2008/10/29 14:14:59 aallowat Exp $
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
import er.extensions.eof.ERXConstant;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * A long response component that displays a progress bar until the
 * real content is ready, and then updates the content using AJAX.
 *
 * @author Tony Allevato
 * @version $Id: ReporterLongResponse.java,v 1.5 2008/10/29 14:14:59 aallowat Exp $
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
    }


    //~ KVC Attributes (must be public) .......................................

    public ReporterLongResponseDelegate delegate;
    public String cancellationMessage;
    public String workingMessage;

    // These two keys ensure that the task is only checked once, so that
    // all conditionals in the template always return consistent results,
    // even if the task finishes part-way through generation of this page
    public boolean isDone     = false;
    public boolean isCanceled = false;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public double fractionOfWorkDone()
    {
        return delegate.fractionOfWorkDone();
    }


    // ----------------------------------------------------------
    public String workDescription()
    {
        return delegate.workDescription();
    }


    // ----------------------------------------------------------
    public void setFractionOfWorkDone(double value)
    {
        // Keep KVC happy.
    }


    // ----------------------------------------------------------
    public void setWorkDescription(String value)
    {
        // Keep KVC happy.
    }


    // ----------------------------------------------------------
    public boolean cannotCancel()
    {
        return !delegate.canCancel();
    }


    // ----------------------------------------------------------
    public WOActionResults cancelJob()
    {
        delegate.cancel();
        isCanceled = true;
        return null;
    }


    // ----------------------------------------------------------
    public void awake()
    {
        super.awake();

        updateStateFromDelegate();
    }


    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        // The first time the component is awakened, delegate appears to be
        // null (bindings not yet resolved?), so we also check the isDone
        // status here in order to bypass the progress bar entirely if the
        // operation is already complete.

        updateStateFromDelegate();

        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    private void updateStateFromDelegate()
    {
        if (delegate != null)
        {
            delegate.longResponseAwakened();
            isDone = delegate.isDone();
        }
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( ReporterLongResponse.class );
}
