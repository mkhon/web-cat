/*==========================================================================*\
 |  $Id: ProgressManager.java,v 1.7 2008/04/02 01:36:38 stedwar2 Exp $
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

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import java.util.Enumeration;
import java.util.UUID;
import net.sf.webcat.core.MutableArray;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * Monitors the progress and status of jobs to support progress bar
 * status updates.  This is a singleton class.
 *
 * @author aallowat
 * @version $Id: ProgressManager.java,v 1.7 2008/04/02 01:36:38 stedwar2 Exp $
 */
public class ProgressManager
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Access the singleton instance of this class.
     * @return The shared instance
     */
	public static ProgressManager getInstance()
	{
		if (instance == null)
        {
			instance = new ProgressManager();
        }
		return instance;
	}


    // ----------------------------------------------------------
    /**
     * The constructor is private because this is a singleton class.
     */
	private ProgressManager()
	{
		jobs = new NSMutableDictionary<Object, Job>();
	}


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
	public synchronized void beginJobWithToken(Object token)
	{
		log.debug("Starting progress monitored job with token: " + token);
		jobs.setObjectForKey(new Job(token), token);
	}


    // ----------------------------------------------------------
	public Object beginJobWithAutomaticToken()
	{
		String token = UUID.randomUUID().toString();
		beginJobWithToken(token);
		return token;
	}


    // ----------------------------------------------------------
	public synchronized void forceJobComplete(Object token)
	{
		if (jobExists(token))
        {
			jobForToken(token).setIsDone(true);
        }
	}


    // ----------------------------------------------------------
	public void beginTaskForJob(Object token, int totalWork)
	{
		beginTaskForJob(token, totalWork, (String)null);
	}


    // ----------------------------------------------------------
	public void beginTaskForJob(Object token, int[] weights)
	{
		beginTaskForJob(token, weights, (String)null);
	}


    // ----------------------------------------------------------
	public synchronized void beginTaskForJob(
        Object token, int[] weights, String description)
	{
		if (jobExists(token))
        {
			jobForToken(token).beginTask(weights, description);
        }
	}


    // ----------------------------------------------------------
	public synchronized void beginTaskForJob(
        Object token, int totalWork, String description)
	{
		if (jobExists(token))
        {
			jobForToken(token).beginTask(totalWork, description);
        }
	}


    // ----------------------------------------------------------
	public synchronized void beginTaskForJob(
        Object token,
        int[] weights,
        IProgressManagerDescriptionProvider descriptionProvider)
	{
		if (jobExists(token))
        {
			jobForToken(token).beginTask(weights, descriptionProvider);
        }
	}


    // ----------------------------------------------------------
	public synchronized void beginTaskForJob(
        Object token,
        int totalWork,
        IProgressManagerDescriptionProvider descriptionProvider)
	{
		if (jobExists(token))
        {
			jobForToken(token).beginTask(totalWork, descriptionProvider);
        }
	}


    // ----------------------------------------------------------
	public void stepJob(Object token)
	{
		stepJob(token, 1);
	}


    // ----------------------------------------------------------
	public synchronized void stepJob(Object token, int delta)
	{
		if (jobExists(token))
        {
			jobForToken(token).step(delta);
        }
	}


    // ----------------------------------------------------------
	public synchronized void completeCurrentTaskForJob(Object token)
	{
		if (jobExists(token))
        {
			jobForToken(token).completeCurrentTask();
        }
	}


    // ----------------------------------------------------------
	public String descriptionOfCurrentTaskForJob(Object token)
	{
		if (jobExists(token))
        {
			return jobForToken(token).descriptionOfCurrentTask();
        }
		else
        {
			return null;
        }
	}


    // ----------------------------------------------------------
    public double percentDoneOfJob(Object token)
    {
		if (jobExists(token))
        {
			return jobForToken(token).percentDone();
        }
		else
        {
			return 0;
        }
    }


    // ----------------------------------------------------------
    public boolean isJobDone(Object token)
    {
		if (jobExists(token))
        {
			return jobForToken(token).isDone();
        }
		else
        {
			return false;
        }
    }


    // ----------------------------------------------------------
    public boolean jobExists(Object token)
    {
    	return jobForToken(token) != null;
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
	private Job jobForToken(Object token)
	{
		return jobs.objectForKey(token);
	}


    // ----------------------------------------------------------
	private static class Job
	{
        //~ Constructor .......................................................

        // ----------------------------------------------------------
		public Job(Object token)
		{
			jobToken = token;
			tasks = new NSMutableArray<Task>();
			isDone = false;
		}


        //~ Public Methods ....................................................

        // ----------------------------------------------------------
		public void beginTask(int[] weights, String description)
		{
			tasks.addObject(new Task(weights, weights.length, description));

	    	if (description == null)
            {
		    	logTaskStack("New task with " + weights.length + " units");
            }
	    	else
            {
		    	logTaskStack("New task (\"" + description + "\") with "
		    	             + weights.length + " units");
            }
		}


        // ----------------------------------------------------------
		public void beginTask(int totalWork, String description)
		{
	    	tasks.addObject(new Task(null, totalWork, description));

	    	if (description == null)
            {
		    	logTaskStack("New task with " + totalWork + " units");
            }
	    	else
            {
		    	logTaskStack("New task (\"" + description + "\") with "
                             + totalWork + " units");
            }
		}


        // ----------------------------------------------------------
		public void beginTask(
            int[] weights,
            IProgressManagerDescriptionProvider descriptionProvider)
		{
			tasks.addObject(
                new Task(weights, weights.length, descriptionProvider));
	    	logTaskStack("New task with " + weights.length + " units");
		}


        // ----------------------------------------------------------
		public void beginTask(
            int totalWork,
            IProgressManagerDescriptionProvider descriptionProvider)
		{
	    	tasks.addObject(new Task(null, totalWork, descriptionProvider));
	    	logTaskStack("New task with " + totalWork + " units");
		}


        // ----------------------------------------------------------
		public void step(int delta)
		{
	    	int lastIndex = tasks.count() - 1;
	    	Task task = tasks.objectAtIndex(lastIndex);
	    	task.step(delta);
		}


        // ----------------------------------------------------------
		public void completeCurrentTask()
		{
	    	int lastIndex = tasks.count() - 1;
	    	tasks.removeObjectAtIndex(lastIndex);

	    	if (tasks.count() == 0)
	    	{
	    		setIsDone(true);
	    	}
	    	else
	    	{
	    		step(1);
	    	}
	    	logTaskStack("Task " + lastIndex + " complete");
		}


        // ----------------------------------------------------------
		public String descriptionOfCurrentTask()
		{
			int lastIndex = tasks.count() - 1;

			for (int i = lastIndex; i >= 0; i--)
			{
				Task task = tasks.objectAtIndex(i);

				if (task.description() != null)
                {
					return task.description();
                }
			}

			return null;
		}


        // ----------------------------------------------------------
		public double percentDone()
		{
	    	if (isDone)
	    	{
	    		return 1;
	    	}

	    	double workDone = 0, divisor = 1;

	    	Enumeration<Task> e = tasks.objectEnumerator();
	    	while (e.hasMoreElements())
	    	{
	    		Task task = e.nextElement();
	    		workDone += task.percentDone() * divisor;
	    		divisor *= task.nextWeightPercent();
	    	}

	    	return workDone;
		}


        // ----------------------------------------------------------
		public boolean isDone()
		{
			return isDone;
		}


        // ----------------------------------------------------------
		public void setIsDone(boolean done)
		{
			isDone = done;
		}


        //~ Private Methods/Classes ...........................................

        // ----------------------------------------------------------
		private void logTaskStack(String prefix)
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append(prefix);
			buffer.append(": ");

	    	Enumeration<Task> e = tasks.objectEnumerator();
	    	while (e.hasMoreElements())
	    	{
	    		Task task = e.nextElement();
	    		buffer.append("(");
	    		buffer.append(task.stepsDoneSoFar);
	    		buffer.append(" of ");
	    		buffer.append(task.totalSteps);
	    		buffer.append(") ");
	    	}

	    	log.debug(buffer.toString());
		}


        // ----------------------------------------------------------
		private class Task
		{
            //~ Public Fields .................................................

            public int weightSoFar;
            public int stepsDoneSoFar;
            public int[] weights;
            public int totalWeight;
            public int totalSteps;


            //~ Constructors ..................................................

            // ----------------------------------------------------------
			public Task(int[] weights, int total, String desc)
			{
				initialize(weights, total);
				description = desc;
			}


            // ----------------------------------------------------------
			public Task(
                int[] weights,
                int total,
                IProgressManagerDescriptionProvider descProvider)
			{
				initialize(weights, total);
				descriptionProvider = descProvider;
			}


            // ----------------------------------------------------------
			private void initialize(int[] myWeights, int total)
			{
				weights = myWeights;
				totalWeight = 0;

				if (myWeights == null)
				{
					totalWeight = total;
				}
				else
				{
					for (int weight : myWeights)
                    {
						totalWeight += weight;
                    }
				}

				weightSoFar = 0;
				stepsDoneSoFar = 0;
				totalSteps = total;
			}


            //~ Public Methods ................................................

            // ----------------------------------------------------------
			public double nextWeightPercent()
			{
				if (weights == null)
				{
					return 1.0 / totalWeight;
				}
				else
				{
					return (double)weights[stepsDoneSoFar] / totalWeight;
				}
			}


            // ----------------------------------------------------------
			public void step(int delta)
			{
				int stop = Math.min(totalSteps, stepsDoneSoFar + delta);

				if (weights == null)
				{
					weightSoFar += delta;
				}
				else
				{
					for (int i = stepsDoneSoFar; i < stop; i++)
                    {
						weightSoFar += weights[i];
                    }
				}

				stepsDoneSoFar += delta;

				if (stepsDoneSoFar > totalSteps)
				{
					stepsDoneSoFar = totalSteps;
					weightSoFar = totalWeight;
				}
			}


            // ----------------------------------------------------------
			public double percentDone()
			{
				return (double)weightSoFar / (double)totalWeight;
			}


            // ----------------------------------------------------------
			public String description()
			{
				if (description != null)
                {
					return description;
                }
				else if (descriptionProvider != null)
                {
					return descriptionProvider.description(jobToken);
                }
				else
                {
					return null;
                }
			}


            //~ Instance/static variables (for Job.Task) ......................

            private String description;
            private IProgressManagerDescriptionProvider descriptionProvider;
		}


        //~ Instance/static variables (for Job) ...............................

		private Object jobToken;
		private boolean isDone;
		private NSMutableArray<Task> tasks;
	}


    //~ Instance/static variables (for ProgressManager) .......................

	private NSMutableDictionary<Object, Job> jobs;
	private static ProgressManager instance;
	private static Logger log = Logger.getLogger(ProgressManager.class);
}
