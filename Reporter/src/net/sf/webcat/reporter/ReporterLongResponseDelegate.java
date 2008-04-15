/*==========================================================================*\
 |  $Id: ReporterLongResponseDelegate.java,v 1.1 2008/04/15 04:09:22 aallowat Exp $
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

//-------------------------------------------------------------------------
/**
 * Defines a delegate that will handle information requests from a
 * {@link ReporterLongResponse} component.
 *
 * @author Tony Allevato
 * @version $Id: ReporterLongResponseDelegate.java,v 1.1 2008/04/15 04:09:22 aallowat Exp $
 */
public abstract class ReporterLongResponseDelegate
{
    // ----------------------------------------------------------
    /**
     * This method is called each time the progress bar component is awakened,
     * before any of the delegate methods {@link fractionOfWorkDone},
     * {@link isDone}, {@link workDescription}, or {@link canCancel} are
     * called. Subclasses can override this method in order to calculate or
     * cache information that will be used in those methods during a single
     * refresh cycle.
     */
    public void longResponseAwakened()
    {
        // Default implementation does nothing.
    }


    // ----------------------------------------------------------
    /**
     * Gets the amount of work done in the long response as a floating point
     * value between 0 and 1.
     *
     * This method is called periodically, once for each time the long response
     * update container refreshes.
     *
     * @return the amount of work done, where 0 is no work and 1 is complete
     */
    public abstract float fractionOfWorkDone();


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the job is completed and the long
     * response should be dismissed to reveal the component content.
     *
     * This is provided as a separate method instead of inferring this value
     * from (fractionOfWorkDone() == 1) so that faulty progress calculations
     * that send the progress to 1 or above do not prematurely reveal the
     * component content. There is no requirement that the test done in this
     * method have any relation to the work calculation done in
     * fractionOfWorkDone.
     *
     * This method is called periodically, once for each time the long response
     * update container refreshes.
     *
     * @return true if the job is done, otherwise false
     */
    public abstract boolean isDone();


    // ----------------------------------------------------------
    /**
     * A human-readable description of the work currently in progress. This
     * string can be changed as work continues in order to show more detailed
     * descriptions of the various phases to the user.
     *
     * This string is not escaped before it is displayed by the progress bar,
     * so you may include HTML content if you wish.
     *
     * This method is called periodically, once for each time the long response
     * update container refreshes.
     *
     * @return the human-readable description of the work currently in progress
     */
    public abstract String workDescription();


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the job can be canceled. If this returns
     * false, the "cancel" button on the progress bar will be disabled.
     *
     * This method is called periodically, once for each time the long response
     * update container refreshes.
     *
     * @return true if the job can be canceled, otherwise false
     */
    public boolean canCancel()
    {
        // Default implementation does not permit cancellation.

        return false;
    }


    // ----------------------------------------------------------
    /**
     * Take action when the long response's "cancel" button is pressed.
     *
     * This method is not called periodically; it is only invoked when the user
     * presses the "cancel" button on the progress bar.
     */
    public void cancel()
    {
        // Default implementation does nothing.
    }
}
