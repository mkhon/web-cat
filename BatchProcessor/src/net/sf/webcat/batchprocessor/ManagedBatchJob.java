/*==========================================================================*\
 |  $Id: ManagedBatchJob.java,v 1.1 2010/04/19 15:24:14 aallowat Exp $
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

package net.sf.webcat.batchprocessor;

import net.sf.webcat.jobqueue.ManagedJobBase;
import com.webobjects.foundation.NSArray;

//-------------------------------------------------------------------------
/**
 * TODO real description
 *
 * @author  Tony Allevato
 * @version $Id: ManagedBatchJob.java,v 1.1 2010/04/19 15:24:14 aallowat Exp $
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
    public String batchState()
    {
        return (String) valueForKey(BatchJob.BATCH_STATE_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Set the value of the <code>batchState</code> attribute.
     *
     * @param value The new value of the attribute
     */
    public void setBatchState(String value)
    {
        takeValueForKey(value, BatchJob.BATCH_STATE_KEY);
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
    public boolean isInIteration()
    {
        return (Boolean) valueForKey(BatchJob.IS_IN_ITERATION_KEY);
    }


    // ----------------------------------------------------------
    public void prepareForIteration()
    {
        setIndexOfNextObject(0);
        takeValueForKey(Boolean.TRUE, BatchJob.IS_IN_ITERATION_KEY);
    }


    // ----------------------------------------------------------
    public void endIteration()
    {
        takeValueForKey(Boolean.FALSE, BatchJob.IS_IN_ITERATION_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Increments the next object index.
     */
    public void incrementIndexOfNextObject()
    {
        int index = indexOfNextObject();
        NSArray ids = (NSArray) valueForKey(BatchJob.BATCHED_OBJECT_IDS_KEY);

        if (index < ids.count())
        {
            setIndexOfNextObject(index + 1);
        }
    }
}
