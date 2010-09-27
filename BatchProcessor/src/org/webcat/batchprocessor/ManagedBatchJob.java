/*==========================================================================*\
 |  $Id: ManagedBatchJob.java,v 1.2 2010/09/27 00:15:32 stedwar2 Exp $
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

package org.webcat.batchprocessor;

import org.webcat.jobqueue.ManagedJobBase;
import com.webobjects.foundation.NSArray;

//-------------------------------------------------------------------------
/**
 * TODO real description
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.2 $, $Date: 2010/09/27 00:15:32 $
 */
public class ManagedBatchJob extends ManagedJobBase
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public ManagedBatchJob(BatchJob job)
    {
        super(job);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve the value of the <code>batchState</code> attribute.
     *
     * @return the value of the attribute
     */
    public String currentState()
    {
        return (String) valueForKey(BatchJob.CURRENT_STATE_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Set the value of the <code>batchState</code> attribute.
     *
     * @param value The new value of the attribute
     */
    public void setCurrentState(String value)
    {
        takeValueForKey(value, BatchJob.CURRENT_STATE_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the value of the <code>indexOfNextObject</code> attribute.
     *
     * @return the value of the attribute
     */
    public int indexOfNextObject()
    {
        return (Integer) valueForKey(BatchJob.INDEX_OF_NEXT_OBJECT_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Set the value of the <code>indexOfNextObject</code> attribute.
     *
     * @param value The new value of the attribute
     */
    public void setIndexOfNextObject(int value)
    {
        takeValueForKey(Integer.valueOf(value),
                BatchJob.INDEX_OF_NEXT_OBJECT_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the job is currently in an entity
     * iteration.
     *
     * @return true if the job is currently iterating over entities, otherwise
     *     false
     */
    public boolean isInIteration()
    {
        return (Boolean) valueForKey(BatchJob.IS_IN_ITERATION_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Prepares this job to iterate over a batch of entities.
     *
     * @param stateAfterIteration the state that the job should transition into
     *     after the iteration is complete
     */
    public void prepareForIteration(String stateAfterIteration)
    {
        setIndexOfNextObject(0);
        takeValueForKey(Boolean.TRUE, BatchJob.IS_IN_ITERATION_KEY);
        takeValueForKey(stateAfterIteration,
                BatchJob.STATE_AFTER_ITERATION_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Ends the current iteration and returns the next state for the job.
     *
     * @return the next state for the job
     */
    public String endIteration()
    {
        String nextState =
            (String) valueForKey(BatchJob.STATE_AFTER_ITERATION_KEY);

        takeValueForKey(Boolean.FALSE, BatchJob.IS_IN_ITERATION_KEY);
        takeValueForKey(null, BatchJob.STATE_AFTER_ITERATION_KEY);

        return nextState;
    }


    // ----------------------------------------------------------
    /**
     * Increments the next object index.
     */
    public void incrementIndexOfNextObject()
    {
        int index = indexOfNextObject();
        NSArray<?> ids =
            (NSArray<?>)valueForKey(BatchJob.BATCHED_OBJECT_IDS_KEY);

        if (index < ids.count())
        {
            setIndexOfNextObject(index + 1);
        }
    }
}
