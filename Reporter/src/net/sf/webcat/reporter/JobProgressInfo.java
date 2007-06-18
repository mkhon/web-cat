package net.sf.webcat.reporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSLog;

import net.sf.webcat.core.MutableArray;
import net.sf.webcat.core.MutableContainer;
import net.sf.webcat.core.MutableDictionary;
import net.sf.webcat.core.MutableContainer.MutableContainerOwner;

public class JobProgressInfo extends MutableDictionary
{
	public static final String KEY_IS_DONE = "isDone";
	public static final String KEY_TASKS = "tasks";
	public static final String KEY_PERCENT_OF_WORK_DONE = "percentOfWorkDone";
	public static final String KEY_TASK_STEPS_COMPLETED = "stepsCompleted";
	public static final String KEY_TASK_TOTAL_STEPS = "totalSteps";

	public static final JobProgressInfo COMPLETED_PROGRESS_INFO;
	
	static
	{
		COMPLETED_PROGRESS_INFO = new JobProgressInfo();
	}


    public NSData archiveData()
    {
    	MutableArray tasks = (MutableArray)objectForKey(KEY_TASKS);

        ByteArrayOutputStream bos = new ByteArrayOutputStream(
            kOverheadAdjustment + tasks.count() * kCountBytesFactor );
        NSData result = null;
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream( bos );
            oos.writeObject( this );
            oos.flush();
            oos.close();
            result = new NSData( bos.toByteArray() );
        }
        catch ( IOException ioe )
        {
            if ( NSLog.debugLoggingAllowedForLevelAndGroups(
                     NSLog.DebugLevelCritical, NSLog.DebugGroupArchiving ) )
            {
                NSLog.debug.appendln( ioe );
            }

            if ( kThrowOnError )
            {
                throw new NSForwardException( ioe );
            }
        }

        return result;
    }

    public static JobProgressInfo objectWithArchiveData( NSData data )
    {
        if ( data == null ) return new JobProgressInfo();
        ByteArrayInputStream bis = new ByteArrayInputStream( data.bytes() );
        JobProgressInfo result = null;
        Throwable exception = null;
        try
        {
            ObjectInputStream ois = new ObjectInputStream( bis );
            Object o = ois.readObject();
            if ( o instanceof JobProgressInfo )
            {
                result = (JobProgressInfo)o;
            }
            else
            {
                exception = new ClassCastException( "objectWithArchiveData(): "
                    + "cannot cast " + o.getClass().getName() + " to "
                    + JobProgressInfo.class.getName() );
                // result is already null
            }
        }
        catch ( IOException ioe )
        {
            exception = ioe;
        }
        catch ( ClassNotFoundException cnfe )
        {
            exception = cnfe;
        }

        if ( exception != null )
        {
            if ( NSLog.debugLoggingAllowedForLevelAndGroups(
                     NSLog.DebugLevelCritical, NSLog.DebugGroupArchiving ) )
            {
                NSLog.debug.appendln( exception );
            }

            if ( kThrowOnError )
            {
                throw new NSForwardException( exception );
            }
        }

        return result;
    }

    private Long makeTaskLong(int current, int total)
    {
    	return Long.valueOf(((long)current) << 32 | total);
    }
    
    private int getTaskStepsDone(Long task)
    {
    	return (int)((task.longValue() >> 32) & 0xFFFFFFFF);
    }

    private int getTaskTotalSteps(Long task)
    {
    	return (int)(task.longValue() & 0xFFFFFFFF);
    }

    public JobProgressInfo()
    {
    	MutableArray tasks = new MutableArray();
    	
    	setObjectForKey(Double.valueOf(0), KEY_PERCENT_OF_WORK_DONE);
    	setObjectForKey(tasks, KEY_TASKS);
    	setObjectForKey(Boolean.FALSE, KEY_IS_DONE);
    }

    public void startTask(int totalWork)
    {
    	MutableArray tasks = (MutableArray)objectForKey(KEY_TASKS);
    	tasks.addObject(makeTaskLong(0, totalWork));
    	log.info("Starting subtask with " + totalWork + " units of work");
    }
    
    public void step()
    {
    	step(1);
    }

    public void step(int delta)
    {
    	MutableArray tasks = (MutableArray)objectForKey(KEY_TASKS);
    	while(delta != 0 && tasks.count() > 0)
    	{
	    	int lastIndex = tasks.count() - 1;
	    	Long subTask = (Long)tasks.objectAtIndex(lastIndex);
	    	
	    	int stepsDone = getTaskStepsDone(subTask);
	    	int totalSteps = getTaskTotalSteps(subTask);
	    	
	    	log.info("Stepping current task from " + stepsDone + " to "
	    			+ (stepsDone + delta) + " out of " + totalSteps +
	    			" units of work");

	    	stepsDone += delta;
	    	if(stepsDone >= totalSteps)
	    	{
	    		tasks.removeObjectAtIndex(lastIndex);
	    		delta = 1;
	    		
	    		log.info("Task complete");
	    	}
	    	else
	    	{
	    		tasks.replaceObjectAtIndex(
	    				makeTaskLong(stepsDone, totalSteps), lastIndex);
	    		delta = 0;
	    	}
    	}
    	
    	updatePercentOfWorkDone();

    	if(tasks.count() == 0)
    	{
    		setObjectForKey(Boolean.TRUE, KEY_IS_DONE);
    	}
    }

    public void updatePercentOfWorkDone()
    {
    	if(isDone())
    	{
    		setObjectForKey(Double.valueOf(1), KEY_PERCENT_OF_WORK_DONE);
    		return;
    	}

    	double workDone = 0, divisor = 1;
    	
    	MutableArray tasks = (MutableArray)objectForKey(KEY_TASKS);
    	Enumeration e = tasks.objectEnumerator();
    	while(e.hasMoreElements())
    	{
    		Long task = (Long)e.nextElement();
    		int stepsDone = getTaskStepsDone(task);
    		int totalSteps = getTaskTotalSteps(task);
    		
    		workDone += ((double)stepsDone / (double)totalSteps) / divisor;
    		divisor *= (double)totalSteps; 
    	}
    	
    	setObjectForKey(Double.valueOf(workDone), KEY_PERCENT_OF_WORK_DONE);
    }
	
	public boolean isDone()
	{
		return ((Boolean)objectForKey(KEY_IS_DONE)).booleanValue();
	}
	
	/**
     * This helps create the ByteArrayOutputStream with a good space estimate.
     */
    private static final int kOverheadAdjustment = 512;

    /**
     * This also helps create the ByteArrayOutputStream with a good space
     * estimate.
     */
    private static final int kCountBytesFactor = 16;

    /**
     * This determines, when an error occurs, if we should throw an
     * NSForwardException or just return null.
     */
    private static final boolean kThrowOnError = true;
    
    private static Logger log = Logger.getLogger(JobProgressInfo.class);
}
