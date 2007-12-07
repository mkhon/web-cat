/*
 *************************************************************************
 * Copyright (c) 2007 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package net.sf.webcat.oda.core.impl;

import net.sf.webcat.oda.RelationInformation;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * Implementation class of IResultSetMetaData for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that returns a pre-defined set
 * of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place.
 * 
 * @author Tony Allevato (Virginia Tech Computer Science)
 */
public class ResultSetMetaData implements IResultSetMetaData
{
    // === Methods ============================================================
	
	// ------------------------------------------------------------------------
	public ResultSetMetaData(RelationInformation relation)
	{
		this.relation = relation;
	}


	// ------------------------------------------------------------------------
	public int getColumnCount() throws OdaException
	{
        return relation.getColumnCount();
	}


	// ------------------------------------------------------------------------
	public String getColumnName(int index) throws OdaException
	{
		return relation.getColumnName(index - 1);
	}


	// ------------------------------------------------------------------------
	public String getColumnLabel(int index) throws OdaException
	{
		return getColumnName(index);
	}


	// ------------------------------------------------------------------------
	public int getColumnType(int index) throws OdaException
	{
		return DataTypes.getType(relation.getColumnType(index - 1));
	}


	// ------------------------------------------------------------------------
	public String getColumnTypeName(int index) throws OdaException
	{
        int nativeTypeCode = getColumnType(index);
        return Driver.getNativeDataTypeName(nativeTypeCode);
	}


	// ------------------------------------------------------------------------
	public int getColumnDisplayLength(int index) throws OdaException
	{
		return -1;
	}


	// ------------------------------------------------------------------------
	public int getPrecision(int index) throws OdaException
	{
		return -1;
	}


	// ------------------------------------------------------------------------
	public int getScale(int index) throws OdaException
	{
		return -1;
	}


	// ------------------------------------------------------------------------
	public int isNullable(int index) throws OdaException
	{
		return IResultSetMetaData.columnNullableUnknown;
	}

	
	// === Instance Variables =================================================

	/**
	 * Relational information about the query that owns this result set metadata
	 * object.
	 */
	private RelationInformation relation;
}
