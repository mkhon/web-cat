package net.sf.webcat.reporter;

import java.util.Vector;

import org.apache.log4j.Logger;

public class ReportQueue
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Default constructor
     */
    public ReportQueue()
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
            log.error( "ReportQueue client was interrupted while " +
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

    static Logger log = Logger.getLogger( ReportQueue.class );
}
