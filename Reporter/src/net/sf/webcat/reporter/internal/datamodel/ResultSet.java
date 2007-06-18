package net.sf.webcat.reporter.internal.datamodel;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.util.Date;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestampFormatter;

import net.sf.webcat.reporter.datamodel.IResultSet;

//-------------------------------------------------------------------------
/**
 * Implements the IResultSet interface.
 * 
 * @see net.sf.webcat.reporter.datamodel.IResultSet
 * 
 * @author Tony Allevato
 */
public class ResultSet implements IResultSet
{
	//~ Constructors ..........................................................

    // ----------------------------------------------------------
	/**
	 * Creates a new result set to be evaluated under the specified reference
	 * environment, executing the specified list of statements.
	 * 
	 * @param refEnv the ReferenceEnvironment used to evaluate the result set
	 * @param statements the list of statements to be executed by the result set
	 */
	public ResultSet(ReferenceEnvironment refEnv, NSArray statements)
	{
		this.referenceEnvironment = refEnv;
		
		createRunnableStatements(statements);
		
		moveToFirstRow();
	}

	
    //~ Methods ...............................................................

    // ----------------------------------------------------------
	/**
	 * Transforms the given set of query statement descriptors into a list of
	 * runnable statement objects.
	 * 
	 * @param statements the statement descriptors to be made runnable
	 */
	private void createRunnableStatements(NSArray statements)
	{
		runnableStatements = new NSMutableArray();

		for(int i = 0; i < statements.size(); i++)
		{
			QueryStatement statement =
				(QueryStatement)statements.objectAtIndex(i);
			
			RunnableStatement parent = null;
			if(i > 0)
				parent = (RunnableStatement)
					runnableStatements.objectAtIndex(i - 1);
			
			RunnableStatement runnable =
				RunnableStatement.createRunnableStatement(
					statement, referenceEnvironment, parent);

			runnableStatements.addObject(runnable);
		}
	}

	
    // ----------------------------------------------------------
	/**
	 * Moves the current position of the result set to the first row.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.moveToFirstRow()
	 */
	public boolean moveToFirstRow()
	{
		RunnableStatement root =
			(RunnableStatement)runnableStatements.objectAtIndex(0);
		
		dataWalker = root.dataWalker();
		currentRow = -1;

		return moveToNextRow();
	}


	// ----------------------------------------------------------
	/**
	 * Moves the current position to the next row in the result set, if the
	 * next row exists.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.moveToNextRow()
	 */
	public boolean moveToNextRow()
	{
		wasNull = false;
		hasRow = dataWalker.advance();
		
		if(hasRow)
			currentRow++;

		return hasRow;
	}


	// ----------------------------------------------------------
	/**
	 * Moves the current position to a specific row in the result set.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.moveToRow(int)
	 */
	public boolean moveToRow(int row)
	{
		moveToFirstRow();
		
		boolean result = true;
		
		for(int i = 0; i < row && result; i++)
			result = moveToNextRow();
		
		return result;
	}
	
	
	// ----------------------------------------------------------
	/**
	 * Returns the index of the current row the cursor is on in the result set.
	 *
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.currentRow()
	 */
	public int currentRow()
	{
		return currentRow;
	}


	// ----------------------------------------------------------
	/**
	 * Returns a value indicating whether the current position in the result
	 * set is a valid row.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.hasRow()
	 */
	public boolean hasRow()
	{
		return hasRow;
	}


	// ----------------------------------------------------------
	/**
	 * Returns a value indicating whether the last
	 * <code><i>xxx</i>ValueForKeyPath</code> call was null or invalid.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.wasNull()
	 */
	public boolean wasNull()
	{
		return wasNull;
	}


	// ----------------------------------------------------------
	/**
	 * Retrieves the value of the specified key path in the result set as a
	 * generic object, without performing any data conversion.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.value(String)
	 */
	public Object value(String expression)
	{
		Object value = referenceEnvironment.evaluate(expression);
		
		wasNull = (value == null);
		return value;
	}
	
	
	// ----------------------------------------------------------
	/**
	 * Retrieves the string value of the specified expression in the result set.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.stringValue(String)
	 */
	public String stringValue(String expression)
	{
		Object value = value(expression);

		if(value == null)
		{
			return null;
		}
		else
		{
			return value.toString();
		}
	}

	
	// ----------------------------------------------------------
	/**
	 * Retrieves the boolean value of the specified expression in the result
	 * set.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.booleanValue(String)
	 */
	public boolean booleanValue(String expression)
	{
		Object value = value(expression);
		
		if(value instanceof Boolean)
		{
			return ((Boolean)value).booleanValue();
		}
		else
		{
			try
			{
				return Boolean.parseBoolean(value.toString());
			}
			catch(NumberFormatException e)
			{
				wasNull = true;
			}
		}
		
		return false;
	}

	
	// ----------------------------------------------------------
	/**
	 * Retrieves the character value of the specified expression in the result
	 * set.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.charValue(String)
	 */
	public char charValue(String expression)
	{
		Object value = value(expression);
		
		if(value instanceof Boolean)
		{
			return ((Character)value).charValue();
		}
		else
		{
			String stringValue = value.toString();
			if(stringValue.length() == 1)
			{
				return stringValue.charAt(0);
			}
			else
			{
				return 0;
			}
		}
	}

	
	// ----------------------------------------------------------
	/**
	 * Retrieves the byte value of the specified expression in the result set.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.byteValue(String)
	 */
	public byte byteValue(String expression)
	{
		Object value = value(expression);
		
		if(value instanceof Byte)
		{
			return ((Byte)value).byteValue();
		}
		else
		{
			try
			{
				return Byte.parseByte(value.toString());
			}
			catch(NumberFormatException e)
			{
				wasNull = true;
			}
		}
		
		return 0;
	}
	
	
	// ----------------------------------------------------------
	/**
	 * Retrieves the short value of the specified expression in the result set.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.shortValue(String)
	 */
	public short shortValue(String expression)
	{
		Object value = value(expression);
		
		if(value instanceof Short)
		{
			return ((Short)value).shortValue();
		}
		else
		{
			try
			{
				return Short.parseShort(value.toString());
			}
			catch(NumberFormatException e)
			{
				wasNull = true;
			}
		}
		
		return 0;
	}
	
	
	// ----------------------------------------------------------
	/**
	 * Retrieves the integer value of the specified expression in the result
	 * set.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.intValue(String)
	 */
	public int intValue(String expression)
	{
		Object value = value(expression);
		
		if(value instanceof Integer)
		{
			return ((Integer)value).intValue();
		}
		else
		{
			try
			{
				return Integer.parseInt(value.toString());
			}
			catch(NumberFormatException e)
			{
				wasNull = true;
			}
		}
		
		return 0;
	}

	
	// ----------------------------------------------------------
	/**
	 * Retrieves the long value of the specified expression in the result set.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.longValue(String)
	 */
	public long longValue(String expression)
	{
		Object value = value(expression);
		
		if(value instanceof Long)
		{
			return ((Long)value).longValue();
		}
		else
		{
			try
			{
				return Long.parseLong(value.toString());
			}
			catch(NumberFormatException e)
			{
				wasNull = true;
			}
		}
		
		return 0;
	}
	
	
	// ----------------------------------------------------------
	/**
	 * Retrieves the float value of the specified expression in the result set.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.floatValue(String)
	 */
	public float floatValue(String expression)
	{
		Object value = value(expression);
		
		if(value instanceof Float)
		{
			return ((Float)value).floatValue();
		}
		else
		{
			try
			{
				return Float.parseFloat(value.toString());
			}
			catch(NumberFormatException e)
			{
				wasNull = true;
			}
		}
		
		return Float.NaN;
	}

	
	// ----------------------------------------------------------
	/**
	 * Retrieves the double value of the specified expression in the result set.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.doubleValue(String)
	 */
	public double doubleValue(String expression)
	{
		Object value = value(expression);
		
		if(value instanceof Double)
		{
			return ((Double)value).doubleValue();
		}
		else
		{
			try
			{
				return Double.parseDouble(value.toString());
			}
			catch(NumberFormatException e)
			{
				wasNull = true;
			}
		}
		
		return Double.NaN;
	}

	
	// ----------------------------------------------------------
	/**
	 * Retrieves the date value of the specified expression in the result set.
	 * 
	 * @see net.sf.webcat.reporter.datamodel.IResultSet.dateValue(String)
	 */
	public Date dateValue(String expression)
	{
		Object value = value(expression);
		
		if(value == null)
		{
			return null;
		}
		else if(value instanceof Date)
		{
			return (Date)value;
		}
		else
		{
			NSTimestampFormatter formatter = new NSTimestampFormatter();

			return (Date)formatter.parseObjectInUTC(value.toString(),
					new ParsePosition(0));
		}
	}


    //~ Instance/static variables .............................................

	/**
	 * The referencing environment upon which this result set is being
	 * evaluated.
	 */
	private ReferenceEnvironment referenceEnvironment;

	/**
	 * A list of runnable query statements that are executed to determine the
	 * rows in this result set.
	 */
	private NSMutableArray runnableStatements;

	/**
	 * The data walker that yields the rows of this result set.
	 */
	private IDataWalker dataWalker;
	
	/**
	 * The current row in the result set.
	 */
	private int currentRow;

	/**
	 * A value indicating whether the result set has a row following the
	 * current one.
	 */
	private boolean hasRow;

	/**
	 * A value indicating whether the last call to a xxxValue method had a null
	 * or invalid result.
	 */
	private boolean wasNull;
}
