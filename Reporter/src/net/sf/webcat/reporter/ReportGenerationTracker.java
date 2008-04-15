/*==========================================================================*\
 |  $Id: ReportGenerationTracker.java,v 1.1 2008/04/15 04:09:22 aallowat Exp $
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

import com.webobjects.foundation.NSMutableDictionary;

// ------------------------------------------------------------------------
/**
 * This class allows for us to track the generation of a report as it passes
 * through the various job queues and generation/rendering phases. The problem
 * is as follows. A periodically updating AjaxUpdateContainer is used to display
 * the progress of the report, but this progress is represented by a sequence of
 * distinct objects:
 *
 * 1) An EnqueuedReportGenerationJob is created and the report is put into the
 * queue.
 *
 * 2) When the job is taken from the queue, a corresponding GeneratedReport
 * object is created.
 *
 * 3) When the report generation (but not rendering) is complete, the
 * EnqueuedReportGenerationJob is deleted.
 *
 * The following steps then occur only if the user is sitting on the report
 * progress page when the generation is complete:
 *
 * 4) An EnqueuedReportRenderJob is created, which includes the GeneratedReport
 * to be rendered, and is placed in the render queue.
 *
 * 5) The job is taken from the render queue, processed, and when the report is
 * done rendering, the job is deleted.
 *
 * These steps occur in the background as the AjaxUpdateContainer updates, so it
 * is possible that if a step completed at a faster rate than the update
 * frequency, it would not be observed by the component. This causes a problem
 * in steps 2 and 3 because it gives us no way to track the GeneratedReport that
 * corresponds to the EnqueuedReportGenerationJob -- even if we create a
 * relationship between the objects, if the job is deleted before the update
 * container observes the setting of this relationship, then we have no way to
 * access the report.
 *
 * This class solves the problem by ensuring that a mapping between the job and
 * the report exists even after the job is destroyed. It maintains this mapping
 * as a function from job IDs to report IDs, and the report generation queue
 * processor sets this mapping when it first creates the GeneratedReport object.
 * Then, the update container that monitors the progress of the report can
 * remember the job ID even after the job is destroyed and use it to access the
 * report.
 *
 * The ReportGenerationTracker class now also manages the progress of a report
 * that will be used by the progress bar on the GeneratedReportPage component,
 * previously the responsibility of the old ProgressManager class.
 *
 * @author Tony Allevato
 * @version $Id: ReportGenerationTracker.java,v 1.1 2008/04/15 04:09:22 aallowat Exp $
 */
public class ReportGenerationTracker
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initializes the report generation tracker.
     */
    private ReportGenerationTracker()
    {
        jobToReportMap = new NSMutableDictionary<Integer, Integer>();
        jobToProgressMap = new NSMutableDictionary<Integer, ReportProgress>();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets the single instance of the report generation tracker.
     *
     * @return the single instance of the report generation tracker
     */
    public static synchronized ReportGenerationTracker getInstance()
    {
        if (instance == null)
        {
            instance = new ReportGenerationTracker();
        }

        return instance;
    }


    // ----------------------------------------------------------
    /**
     * Gets the ID of the report that was generated as a result of processing
     * the job with the given ID.
     *
     * @param jobId
     *            the ID of the job
     * @return the ID of the report that was generated, or null if none has yet
     *         been generated
     */
    public synchronized Integer reportIdForJobId(int jobId)
    {
        return jobToReportMap.objectForKey(jobId);
    }


    // ----------------------------------------------------------
    /**
     * Sets the ID of the report that was generated as a result of processing
     * the job with the given ID.
     *
     * @param jobId
     *            the ID of the job
     * @param reportId
     *            the ID of the report
     * @param numDataSetRefs
     *            the number of data set references in the report
     */
    public synchronized void startReportForJobId(int jobId, int reportId,
            int numDataSetRefs)
    {
        jobToReportMap.setObjectForKey(reportId, jobId);

        ReportProgress progress = new ReportProgress(numDataSetRefs);
        jobToProgressMap.setObjectForKey(progress, jobId);
    }


    // ----------------------------------------------------------
    /**
     * Called when a report that is being generated begins computing the results
     * of a new data set.
     *
     * @param jobId
     *            the ID of the job generating the report
     * @param totalWork
     *            the number of rows in the result set
     */
    public synchronized void startNextDataSetForJobId(int jobId, int totalWork)
    {
        ReportProgress progress = jobToProgressMap.objectForKey(jobId);
        progress.startNextDataSet(totalWork);
    }


    // ----------------------------------------------------------
    /**
     * Called after a batch of rows has been generated in a report to update its
     * progress.
     *
     * @param jobId
     *            the ID of the job genrerating the report
     * @param units
     *            the number of rows that were generated
     */
    public synchronized void doWorkForJobId(int jobId, int units)
    {
        ReportProgress progress = jobToProgressMap.objectForKey(jobId);
        progress.doWork(units);
    }


    // ----------------------------------------------------------
    /**
     * Forces a data set in a report that is being generated to be marked as
     * complete.
     *
     * @param jobId
     *            the ID of the job generating the report
     */
    public synchronized void completeDataSetForJobId(int jobId)
    {
        ReportProgress progress = jobToProgressMap.objectForKey(jobId);
        progress.completeDataSet();
    }


    // ----------------------------------------------------------
    /**
     * Gets the fraction of work done for a report generation job, based on the
     * amount of data that has so far been generated for it.
     *
     * @param jobId
     *            the ID of the job generating the report
     *
     * @return a value between 0 and 1 indicating the amount of work done
     */
    public synchronized float fractionOfWorkDoneForJobId(int jobId)
    {
        ReportProgress progress = jobToProgressMap.objectForKey(jobId);
        return progress.fractionOfWorkDone();
    }


    // ----------------------------------------------------------
    /**
     * Removes the ID of the report that was generated as a result of processing
     * the job with the given ID.
     *
     * This method should be called when a report is actually rendered in order
     * to remove stale values; once the report is rendered, this mapping is no
     * longer needed because viewing starts with a known GeneratedReport and
     * steps 1-3 (as described in the class summary) are skipped.
     *
     * @param jobId
     *            the ID of the job
     */
    public synchronized void removeReportIdForJobId(int jobId)
    {
        jobToReportMap.removeObjectForKey(jobId);
        jobToProgressMap.removeObjectForKey(jobId);
    }


    // ----------------------------------------------------------
    /**
     * Wraps an array of DataSetProgress objects to maintain the progress of a
     * report being generated.
     */
    private class ReportProgress
    {
        //~ Constructor .......................................................

        // ----------------------------------------------------------
        public ReportProgress(int numDataSetRefs)
        {
            dataSets = new DataSetProgress[numDataSetRefs];
            currentDataSet = -1;
        }


        //~ Methods ...........................................................

        // ----------------------------------------------------------
        public void startNextDataSet(int totalWork)
        {
            currentDataSet++;

            dataSets[currentDataSet] = new DataSetProgress(totalWork);
        }


        // ----------------------------------------------------------
        public void doWork(int units)
        {
            dataSets[currentDataSet].doWork(units);
        }


        // ----------------------------------------------------------
        public void completeDataSet()
        {
            dataSets[currentDataSet].complete();
        }


        // ----------------------------------------------------------
        public float fractionOfWorkDone()
        {
            if (currentDataSet == dataSets.length)
            {
                return 1.0f;
            }

            float progress = 0.0f;

            for (int i = 0; i <= currentDataSet; i++)
            {
                progress += dataSets[i].fractionOfWorkDone() / dataSets.length;
            }

            return progress;
        }


        //~ Static/instance variables .........................................

        private int currentDataSet;
        private DataSetProgress[] dataSets;
    }


    // ----------------------------------------------------------
    /**
     * A simple class that encapsulates the amount of work done and total work
     * done for a single data set in a report.
     */
    private class DataSetProgress
    {
        //~ Constructor .......................................................

        // ----------------------------------------------------------
        /**
         * Initializes this data set progress item with the specified amount of
         * work.
         *
         * @param totalWork
         *            the amount of work to be done
         */
        public DataSetProgress(int totalWork)
        {
            this.workDone = 0;
            this.totalWork = totalWork;
        }


        //~ Methods ...........................................................

        // ----------------------------------------------------------
        /**
         * Gets the fraction of work done for this data set, as a value between
         * 0 and 1.
         *
         * @return the fraction of work done
         */
        public float fractionOfWorkDone()
        {
            return (float) workDone / totalWork;
        }


        // ----------------------------------------------------------
        /**
         * Add the specified number of units of work to this data set progress
         * item.
         *
         * @param units
         *            the amount of work that was done
         */
        public void doWork(int units)
        {
            workDone += units;

            if (workDone > totalWork)
                workDone = totalWork;
        }


        // ----------------------------------------------------------
        /**
         * Forces the data set to be completely done.
         */
        public void complete()
        {
            workDone = totalWork;
        }


        //~ Instance/static variables .........................................

        private int workDone;
        private int totalWork;
    }


    //~ Instance/static variables .............................................

    private static ReportGenerationTracker instance;

    private NSMutableDictionary<Integer, Integer> jobToReportMap;

    private NSMutableDictionary<Integer, ReportProgress> jobToProgressMap;
}
