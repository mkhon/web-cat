/*
 * ************************************************************************
 * Copyright (c) 2007 <<Your Company Name here>>
 * 
 * ************************************************************************
 */

package net.sf.webcat.oda.core.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import net.sf.webcat.oda.IWebCATResultSet;
import net.sf.webcat.oda.RelationInformation;
import net.sf.webcat.oda.WebCATDataException;

import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * Implementation class of IResultSet for an ODA runtime driver. <br>
 * For demo purpose, the auto-generated method stubs have hard-coded
 * implementation that returns a pre-defined set of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place.
 * 
 * @author Tony Allevato (Virginia Tech Computer Science)
 */
public class ResultSet implements IResultSet
{
	// === Methods ============================================================

	// ------------------------------------------------------------------------
	/**
	 * Creates a new ResultSet.
	 * 
	 * @param relation
	 *            the RelationInformation object that describes the query that
	 *            created this result set
	 * @param results
	 *            the IWebCATResultSet object that is the source of the data for
	 *            this result set
	 */
	public ResultSet(RelationInformation relation, IWebCATResultSet results)
	{
		this.relation = relation;
		this.results = results;
	}


	// ------------------------------------------------------------------------
	public IResultSetMetaData getMetaData() throws OdaException
	{
		return new ResultSetMetaData(relation);
	}


	// ------------------------------------------------------------------------
	public void setMaxRows(int max) throws OdaException
	{
		maxRows = max;
	}


	// ------------------------------------------------------------------------
	/**
	 * Returns the maximum number of rows that can be fetched from this result
	 * set.
	 * 
	 * @return the maximum number of rows to fetch.
	 */
	protected int getMaxRows()
	{
		return maxRows;
	}


	// ------------------------------------------------------------------------
	public boolean next() throws OdaException
	{
		try
		{
			int maxRows = getMaxRows();
	
			if(maxRows == 0 || results.currentRow() < maxRows)
			{
				return results.moveToNextRow();
			}
			else
			{
				return false;
			}
		}
		catch(WebCATDataException e)
		{
			throw new OdaException(e.getCause());
		}
	}


	// ------------------------------------------------------------------------
	public void close() throws OdaException
	{
		try
		{
			results.close();
		}
		catch(WebCATDataException e)
		{
			throw new OdaException(e.getCause());
		}
	}


	// ------------------------------------------------------------------------
	public int getRow() throws OdaException
	{
		try
		{
			return results.currentRow() + 1;
		}
		catch(WebCATDataException e)
		{
			throw new OdaException(e.getCause());
		}
	}


	// ------------------------------------------------------------------------
	public String getString(int index) throws OdaException
	{
		try
		{
			return results.stringValueAtIndex(index - 1);
		}
		catch(WebCATDataException e)
		{
			throw new OdaException(e.getCause());
		}
	}


	// ------------------------------------------------------------------------
	public String getString(String columnName) throws OdaException
	{
		return getString(findColumn(columnName));
	}


	// ------------------------------------------------------------------------
	public int getInt(int index) throws OdaException
	{
		try
		{
			return results.intValueAtIndex(index - 1);
		}
		catch(WebCATDataException e)
		{
			throw new OdaException(e.getCause());
		}
	}


	// ------------------------------------------------------------------------
	public int getInt(String columnName) throws OdaException
	{
		return getInt(findColumn(columnName));
	}


	// ------------------------------------------------------------------------
	public double getDouble(int index) throws OdaException
	{
		try
		{
			return results.doubleValueAtIndex(index - 1);
		}
		catch(WebCATDataException e)
		{
			throw new OdaException(e.getCause());
		}
	}


	// ------------------------------------------------------------------------
	public double getDouble(String columnName) throws OdaException
	{
		return getDouble(findColumn(columnName));
	}


	// ------------------------------------------------------------------------
	public BigDecimal getBigDecimal(int index) throws OdaException
	{
		try
		{
			return results.decimalValueAtIndex(index - 1);
		}
		catch(WebCATDataException e)
		{
			throw new OdaException(e.getCause());
		}
	}


	// ------------------------------------------------------------------------
	public BigDecimal getBigDecimal(String columnName) throws OdaException
	{
		return getBigDecimal(findColumn(columnName));
	}


	// ------------------------------------------------------------------------
	public Date getDate(int index) throws OdaException
	{
		throw new UnsupportedOperationException();
	}


	// ------------------------------------------------------------------------
	public Date getDate(String columnName) throws OdaException
	{
		return getDate(findColumn(columnName));
	}


	// ------------------------------------------------------------------------
	public Time getTime(int index) throws OdaException
	{
		throw new UnsupportedOperationException();
	}


	// ------------------------------------------------------------------------
	public Time getTime(String columnName) throws OdaException
	{
		return getTime(findColumn(columnName));
	}


	// ------------------------------------------------------------------------
	public Timestamp getTimestamp(int index) throws OdaException
	{
		try
		{
			return results.timestampValueAtIndex(index - 1);
		}
		catch(WebCATDataException e)
		{
			throw new OdaException(e.getCause());
		}
	}


	// ------------------------------------------------------------------------
	public Timestamp getTimestamp(String columnName) throws OdaException
	{
		return getTimestamp(findColumn(columnName));
	}


	// ------------------------------------------------------------------------
	public IBlob getBlob(int index) throws OdaException
	{
		throw new UnsupportedOperationException();
	}


	// ------------------------------------------------------------------------
	public IBlob getBlob(String columnName) throws OdaException
	{
		return getBlob(findColumn(columnName));
	}


	// ------------------------------------------------------------------------
	public IClob getClob(int index) throws OdaException
	{
		throw new UnsupportedOperationException();
	}


	// ------------------------------------------------------------------------
	public IClob getClob(String columnName) throws OdaException
	{
		return getClob(findColumn(columnName));
	}


	// ------------------------------------------------------------------------
	public boolean getBoolean(int index) throws OdaException
	{
		try
		{
			return results.booleanValueAtIndex(index - 1);
		}
		catch(WebCATDataException e)
		{
			throw new OdaException(e.getCause());
		}
	}


	// ------------------------------------------------------------------------
	public boolean getBoolean(String columnName) throws OdaException
	{
		return getBoolean(findColumn(columnName));
	}


	// ------------------------------------------------------------------------
	public boolean wasNull() throws OdaException
	{
		try
		{
			return results.wasValueNull();
		}
		catch(WebCATDataException e)
		{
			throw new OdaException(e.getCause());
		}
	}


	// ------------------------------------------------------------------------
	public int findColumn(String columnName) throws OdaException
	{
		for(int i = 0; i < relation.getColumnCount(); i++)
			if(relation.getColumnName(i).equals(columnName))
				return i + 1;

		// Return a dummy value if the column wasn't found.
		return 1;
	}


	// === Instance Variables =================================================
	
	/**
	 * 
	 */
	private int maxRows;

	/**
	 * 
	 */
	private RelationInformation relation;

	/**
	 * 
	 */
	private IWebCATResultSet results;
}
