/*==========================================================================*\
 |  $Id: ReportRenderQueue.java,v 1.1 2008/04/15 04:09:22 aallowat Exp $
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

import java.util.Vector;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * This is the synchronous queue class for report rendering jobs.  It currently
 * mirrors the GraderQueue class in the Grader subsystem.
 *
 * @author Tony Allevato
 * @version $Id: ReportRenderQueue.java,v 1.1 2008/04/15 04:09:22 aallowat Exp $
 */
public class ReportRenderQueue
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Default constructor
     */
    public ReportRenderQueue()
    {
        queue = new Vector();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * suspends all processors till there is a job to process then wakes up a
     * processor and gets a job
     *
     * @return the job
     */
    public synchronized Object getJobToken()
    {
        try
        {
            while ( queue.size() == 0 )
            {
                wait();
            }
            // Here, after being woken up by notify(), we are guaranteed
            // that the queue size is not zero and the current thread
            // owns the monitor for this queue object
            return dequeue();
        }
        catch ( InterruptedException e )
        {
            log.error( "ReportRenderQueue client was interrupted while " +
               "waiting for a job." );
            return null;
        }
    }


    // ----------------------------------------------------------
    /**
     * Puts a job in the queue.
     *
     * @param o the job to enqueue
     */
    public synchronized void enqueue( Object o )
    {
        queue.add( o );
        notify();
    }


    // ----------------------------------------------------------
    /**
     * Dequeues a job from the queue.
     *
     * @return a dequeued job
     */
    private synchronized Object dequeue()
    {
        Object o = queue.elementAt( 0 );
        queue.removeElementAt( 0 );
        return o;
    }


    //~ Instance/static variables .............................................

    /** The queue is maintained as a vector. */
    private Vector queue;

    static Logger log = Logger.getLogger( ReportRenderQueue.class );
}
