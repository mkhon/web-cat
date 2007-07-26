package net.sf.webcat.reporter;

import java.util.Enumeration;
import java.util.UUID;

import net.sf.webcat.core.MutableArray;

import org.apache.log4j.Logger;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

public class ProgressManager
{
	public static ProgressManager getInstance()
	{
		if(instance == null)
			instance = new ProgressManager();
		
		return instance;
	}

	public ProgressManager()
	{
		jobs = new NSMutableDictionary();
	}

	public synchronized void beginJobWithToken(Object token)
	{
		log.debug("Starting progress monitored job with token: " + token);
		jobs.setObjectForKey(new ProgressManagerJob(), token);
	}

	public Object beginJobWithAutomaticToken()
	{
		String token = UUID.randomUUID().toString();
		beginJobWithToken(token);
		
		return token;
	}
	
	public synchronized void forceJobComplete(Object token)
	{
		if(jobExists(token))
			jobForToken(token).setIsDone(true);
	}

	public void beginTaskForJob(Object token, int totalWork)
	{
		beginTaskForJob(token, totalWork, null);
	}

	public synchronized void beginTaskForJob(Object token, int totalWork,
			String description)
	{
		if(jobExists(token))
			jobForToken(token).beginTask(totalWork, description);
	}

	public void stepJob(Object token)
	{
		stepJob(token, 1);
	}
	
	public synchronized void stepJob(Object token, int delta)
	{
		if(jobExists(token))
			jobForToken(token).step(delta);
	}

	public synchronized void completeCurrentTaskForJob(Object token)
	{
		if(jobExists(token))
			jobForToken(token).completeCurrentTask();
	}

	public String descriptionOfCurrentTaskForJob(Object token)
	{
		if(jobExists(token))
			return jobForToken(token).descriptionOfCurrentTask();
		else
			return null;
	}

    public double percentDoneOfJob(Object token)
    {
		if(jobExists(token))
			return jobForToken(token).percentDone();
		else
			return 0;
    }
    
    public boolean isJobDone(Object token)
    {
		if(jobExists(token))
			return jobForToken(token).isDone();
		else
			return false;
    }
    
    public boolean jobExists(Object token)
    {
    	return jobForToken(token) != null;
    }

	private ProgressManagerJob jobForToken(Object token)
	{
		return (ProgressManagerJob)jobs.objectForKey(token);
	}

	private class ProgressManagerJob
	{
		public ProgressManagerJob()
		{
			tasks = new NSMutableArray();
			isDone = false;
		}

		public void beginTask(int totalWork, String description)
		{
	    	tasks.addObject(new Task(totalWork, description));
	    	
	    	if(description == null)
		    	logTaskStack("New task with " + totalWork + " units");
	    	else
		    	logTaskStack("New task (\"" + description + "\") with " + totalWork + " units");
		}
		
		public void step(int delta)
		{
	    	int lastIndex = tasks.count() - 1;
	    	Task task = (Task)tasks.objectAtIndex(lastIndex);
	    	task.step(delta);
		}
		
		public void completeCurrentTask()
		{			
	    	int lastIndex = tasks.count() - 1;
	    	tasks.removeObjectAtIndex(lastIndex);

	    	if(tasks.count() == 0)
	    	{
	    		setIsDone(true);
	    	}			
	    	else
	    	{
	    		step(1);
	    	}

	    	logTaskStack("Task " + lastIndex + " complete");
		}
		
		public String descriptionOfCurrentTask()
		{
			int lastIndex = tasks.count() - 1;
			
			for(int i = lastIndex; i >= 0; i--)
			{
				Task task = (Task)tasks.objectAtIndex(i);
				
				if(task.description != null)
					return task.description;
			}
			
			return null;
		}

		public double percentDone()
		{
	    	if(isDone)
	    	{
	    		return 1;
	    	}

	    	double workDone = 0, divisor = 1;
	    	
	    	Enumeration e = tasks.objectEnumerator();
	    	while(e.hasMoreElements())
	    	{
	    		Task task = (Task)e.nextElement();
	    		workDone += task.percentDone() / divisor;
	    		divisor *= task.totalSteps; 
	    	}
	    	
	    	return workDone;
		}

		public boolean isDone()
		{
			return isDone;
		}

		public void setIsDone(boolean done)
		{
			isDone = done;
		}
		
		private void logTaskStack(String prefix)
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append(prefix);
			buffer.append(": ");
			
	    	Enumeration e = tasks.objectEnumerator();
	    	while(e.hasMoreElements())
	    	{
	    		Task task = (Task)e.nextElement();
	    		buffer.append("(");
	    		buffer.append(task.stepsDoneSoFar);
	    		buffer.append(" of ");
	    		buffer.append(task.totalSteps);
	    		buffer.append(") ");
	    	}
	    	
	    	log.debug(buffer.toString());
		}

		private class Task
		{
			public int stepsDoneSoFar;
			public int totalSteps;
			public String description;

			public Task(int total, String desc)
			{
				stepsDoneSoFar = 0;
				totalSteps = total;
				description = desc;
			}

			public void step(int delta)
			{
				stepsDoneSoFar += delta;
				if(stepsDoneSoFar > totalSteps)
					stepsDoneSoFar = totalSteps;
			}
			
			public double percentDone()
			{
				return (double)stepsDoneSoFar / (double)totalSteps;
			}
		}

		private boolean isDone;

		private NSMutableArray tasks;
	}

	private NSMutableDictionary jobs;

	private static ProgressManager instance;
	
	private static Logger log = Logger.getLogger(ProgressManager.class);
}
