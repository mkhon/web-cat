/*==========================================================================*\
 |  $Id: BatchJob.java,v 1.2 2010/09/27 00:15:32 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2009 Virginia Tech
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

import org.webcat.core.Application;
import org.webcat.core.MutableArray;
import org.webcat.core.ObjectQuery;
import org.webcat.core.QualifierUtils;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSRange;
import er.extensions.eof.ERXFetchSpecificationBatchIterator;

// -------------------------------------------------------------------------
/**
 * TODO: place a real description here.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.2 $, $Date: 2010/09/27 00:15:32 $
 */
public class BatchJob
    extends _BatchJob
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new BatchJob object.
     */
    public BatchJob()
    {
        super();
    }


    //~ Constants .............................................................

    /**
     * A state defined by the batch processor denoting that the job is about to
     * begin for the first time.
     */
    public static final String STATE_START = "start";

    /**
     * A state defined by the batch processor denoting that the job is
     * complete.
     */
    public static final String STATE_END = "end";

    /**
     * A state defined by the batch processor denoting a serious error that
     * should suspend the job and issue a notification to the owner.
     */
    public static final String STATE_DIE = "die";


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * A convenience method to create and insert a new BatchJob with default
     * values; the current time as the enqueueTime, not cancelled, and not
     * ready to run. To run the job, the client will need to call
     * {@link #setIsReady(boolean)} on the job and save those changes to the
     * editing context.
     *
     * @param editingContext the editing context
     * @return a new BatchJob
     */
    public static BatchJob create(EOEditingContext editingContext)
    {
        return create(editingContext, new NSTimestamp(),
                false, false, false, false);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the name of the directory where this submission is stored.
     *
     * @return the directory name
     */
    public String workingDirName()
    {
        StringBuffer dir = new StringBuffer(50);
        dir.append(Application.configurationProperties().getProperty(
                "grader.workarea")); // TODO generalize
        dir.append('/');
        dir.append(user().authenticationDomain().subdirName());
        dir.append('/');
        dir.append(user().userName());
        dir.append('/');
        dir.append(id().toString());

        return dir.toString();
    }


    // ----------------------------------------------------------
    @Override
    public void setObjectQueryRelationship(ObjectQuery query)
    {
        super.setObjectQueryRelationship(query);

        if (query != null)
        {
            MutableArray pks = new MutableArray(query.fetchPrimaryKeys());
            setBatchedObjectIds(pks);
        }
        else
        {
            setBatchedObjectIds(new MutableArray());
        }

        setIndexOfNextObject(0);
    }


    // ----------------------------------------------------------
    public ERXFetchSpecificationBatchIterator iteratorForRemainingItems(
            EOEditingContext ec)
    {
        MutableArray pks = batchedObjectIds();

        if (indexOfNextObject() == pks.count())
        {
            return null;
        }

        NSArray<?> remainder = pks.subarrayWithRange(
                new NSRange(indexOfNextObject(),
                        pks.count() - indexOfNextObject()));

        ObjectQuery query = objectQuery();
        EOQualifier[] quals = QualifierUtils.partitionQualifier(
                query.qualifier(), query.objectType());

        EOFetchSpecification fspec = new EOFetchSpecification(
                query.objectType(), quals[0], null);

        return new ERXFetchSpecificationBatchIterator(fspec, remainder, ec,
                    ERXFetchSpecificationBatchIterator.DefaultBatchSize);
    }
}
