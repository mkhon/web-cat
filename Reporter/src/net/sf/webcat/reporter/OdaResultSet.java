/*==========================================================================*\
 |  $Id: OdaResultSet.java,v 1.7 2008/10/28 15:52:23 aallowat Exp $
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

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSTimestamp;
import er.extensions.ERXFetchSpecificationBatchIterator;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Map;
import net.sf.webcat.core.Application;
import net.sf.webcat.oda.commons.IWebCATResultSet;
import net.sf.webcat.oda.commons.WebCATDataException;
import ognl.Node;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.enhance.ExpressionAccessor;
import ognl.webobjects.WOOgnl;

//-------------------------------------------------------------------------
/**
 * A result set for a report.
 *
 * @author  Tony Allevato
 * @version $Id: OdaResultSet.java,v 1.7 2008/10/28 15:52:23 aallowat Exp $
 */
public class OdaResultSet
    implements IWebCATResultSet
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a result set.
     *
     * @param jobId
     *      The ID of the EnqueuedReportGenerationJob that is generated the
     *      report that will contain this data
     * @param query
     *      The query defining this result set
     */
    public OdaResultSet(Number jobId, ReportQuery query)
    {
        this.jobId = jobId.intValue();
        this.query = query;

        currentRow = 0;
        rawCurrentRow = 0;
        lastThrottleCheck = 0;
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public void close()
    {
        Application.releasePeerEditingContext(editingContext);

        ReportGenerationTracker.getInstance().completeDataSetForJobId(jobId);
    }


    // ----------------------------------------------------------
    public int currentRow()
    {
        return currentRow;
    }


    // ----------------------------------------------------------
    public void execute()
    {
        recycleEditingContext();

        EOQualifier qualifier = QualifierSerialization.convertGIDsToEOs(
            query.qualifier(), editingContext);

        EOQualifier[] quals = QualifierUtils.partitionQualifier(
            qualifier, query.wcEntityName());
        fetchQualifier = quals[0];
        inMemoryQualifier = quals[1];

        EOFetchSpecification fetch = new EOFetchSpecification(
            query.wcEntityName(), fetchQualifier, null);
        iterator =
            new ERXFetchSpecificationBatchIterator(fetch, editingContext);

        ReportGenerationTracker.getInstance().startNextDataSetForJobId(jobId,
                iterator.count());
    }


    // ----------------------------------------------------------
    public void prepare(String entityType, String[] myExpressions)
        throws WebCATDataException
    {
        this.expressions = myExpressions;
        defaultContext = new OgnlContext();
        accessors = new ExpressionAccessor[myExpressions.length];

        int i = 0;
        for (String expression : myExpressions)
        {
            try
            {
                accessors[i] = Ognl.compileExpression(
                    defaultContext, null, expression).getAccessor();
            }
            catch (Exception e)
            {
                throw new WebCATDataException(e);
            }
            i++;
        }
    }


    // ----------------------------------------------------------
    public boolean moveToNextRow()
    {
        throttleIfNecessary();
        boolean hasNext = true;
        rawCurrentRow++;
        if (rawCurrentRow % PROGRESS_STEP_SIZE == 0)
        {
            ReportGenerationTracker.getInstance().doWorkForJobId(jobId,
                    PROGRESS_STEP_SIZE);
        }

        if (currentBatchEnum == null || !currentBatchEnum.hasMoreElements())
        {
            hasNext = getNextBatch();
        }

        if (hasNext)
        {
            currentObject = currentBatchEnum.nextElement();
            currentRow++;
        }

        return hasNext;
    }


    // ----------------------------------------------------------
    public boolean booleanValueAtIndex(int column)
        throws WebCATDataException
    {
        Boolean value = evaluate(column, Boolean.class);
        return (value == null)? false : value;
    }


    // ----------------------------------------------------------
    public BigDecimal decimalValueAtIndex(int column)
        throws WebCATDataException
    {
        return evaluate(column, BigDecimal.class);
    }


    // ----------------------------------------------------------
    public double doubleValueAtIndex(int column)
        throws WebCATDataException
    {
        Double value = evaluate(column, Double.class);
        if (value == null)
        {
            return 0.0;
        }
        else
        {
            return value;
        }
    }


    // ----------------------------------------------------------
    public int intValueAtIndex(int column)
        throws WebCATDataException
    {
        Integer value = evaluate(column, Integer.class);
        return (value == null)? 0 : value;
    }


    // ----------------------------------------------------------
    public String stringValueAtIndex(int column)
        throws WebCATDataException
    {
        return evaluate(column, String.class);
    }


    // ----------------------------------------------------------
    public Timestamp timestampValueAtIndex(int column)
    {
        ExpressionAccessor accessor = accessors[column];
        Object result = accessor.get(defaultContext, currentObject);

        if (result instanceof NSTimestamp)
        {
            return (NSTimestamp)result;
        }
        else
        {
            // TODO Probably do some more conversions here.
            return null;
        }
    }


    // ----------------------------------------------------------
    public boolean wasValueNull()
    {
        return wasNull;
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    private void recycleEditingContext()
    {
        if (editingContext != null)
        {
            Application.releasePeerEditingContext(editingContext);
        }

        editingContext = Application.newPeerEditingContext();

        if (iterator != null)
        {
            iterator.setEditingContext(editingContext);
        }
    }


    // ----------------------------------------------------------
    private boolean getNextBatch()
    {
        if (iterator.hasNextBatch())
        {
            boolean getBatch = true;

            while (getBatch)
            {
                recycleEditingContext();
                currentBatch = iterator.nextBatch();

                if (inMemoryQualifier != null)
                {
                    currentBatch = EOQualifier.filteredArrayWithQualifier(
                        currentBatch, inMemoryQualifier);
                }
                if (currentBatch.isEmpty())
                {
                    getBatch = iterator.hasNextBatch();
                }
                else
                {
                    currentBatchEnum = currentBatch.objectEnumerator();
                    return true;
                }
            }
        }
        return false;
    }


    // ----------------------------------------------------------
    private void throttleIfNecessary()
    {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastThrottleCheck > MILLIS_BETWEEN_THROTTLE_CHECK)
        {
            while (Reporter.getInstance().refreshThrottleStatus())
            {
                try
                {
                    Thread.sleep(MILLIS_TO_THROTTLE);
                }
                catch (InterruptedException e)
                {
                    // Nothing to do
                }
            }
            lastThrottleCheck = System.currentTimeMillis();
        }
    }


    // ----------------------------------------------------------
    private <T> T evaluate(int column, Class<T> destType)
        throws WebCATDataException
    {
        ExpressionAccessor accessor = accessors[column];
        Object result = null;

        try
        {
            result = accessor.get(defaultContext, currentObject);
        }
        catch (NSKeyValueCoding.UnknownKeyException e)
        {
            // Translate the expression into something a little easier for the
            // user to read.
            String msg = String.format(
                "In the expression (%s), the key \"%s\" is not recognized " +
                "by the source object (which is of type \"%s\")",
                expressions[column],
                e.key(),
                e.object().getClass().getName());

            throw new WebCATDataException(new IllegalArgumentException(msg));
        }
        catch (Exception e)
        {
            if (e instanceof OgnlException)
            {
                wasNull = true;
                return null;
            }
            throw new WebCATDataException(e);
        }

        if (result == null)
        {
            wasNull = true;
            return null;
        }
        else
        {
            wasNull = false;

            if (destType.isInstance(result))
            {
                return (T)result;
            }
            else
            {
                return (T)defaultContext.getTypeConverter().convertValue(
                        null, null, null, null, result, destType);
            }
        }
    }


    //~ Instance/static variables .............................................

    private int jobId;
    private ReportQuery query;
    private EOEditingContext editingContext;
    private EOQualifier fetchQualifier;
    private EOQualifier inMemoryQualifier;
    private ERXFetchSpecificationBatchIterator iterator;
    private int currentRow;
    private int rawCurrentRow;
    private String[] expressions;
    private ExpressionAccessor[] accessors;
    private OgnlContext defaultContext;
    private NSArray<Object> currentBatch;
    private Enumeration<Object> currentBatchEnum;
    private Object currentObject;
    private boolean wasNull;
    private long lastThrottleCheck;

    private static final long MILLIS_BETWEEN_THROTTLE_CHECK = 3000;
    private static final long MILLIS_TO_THROTTLE = 5000;
    private static final int PROGRESS_STEP_SIZE = 10;
}
