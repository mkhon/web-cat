/*
 * ************************************************************************
 * Copyright (c) 2007 <<Your Company Name here>>
 * 
 * ************************************************************************
 */

package net.sf.webcat.oda.core.impl;

import net.sf.webcat.oda.core.Constants;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * Implementation class of IDataSetMetaData for an ODA runtime driver. <br>
 * For demo purpose, the auto-generated method stubs have hard-coded
 * implementation that assume this custom ODA data set is capable of handling a
 * query that returns a single result set and accepts scalar input parameters by
 * index. A custom ODA driver is expected to implement own data set specific
 * behavior in its place.
 */
public class DataSetMetaData implements IDataSetMetaData
{
	// === Methods ============================================================

	// ------------------------------------------------------------------------
	/**
	 * Creates a new instance of the DataSetMetaData object.
	 */
	public DataSetMetaData(IConnection connection)
	{
		this.connection = connection;
	}


	// ------------------------------------------------------------------------
	public IConnection getConnection() throws OdaException
	{
		return connection;
	}


	// ------------------------------------------------------------------------
	public IResultSet getDataSourceObjects(String catalog, String schema,
			String object, String version) throws OdaException
	{
		throw new UnsupportedOperationException();
	}


	// ------------------------------------------------------------------------
	public int getDataSourceMajorVersion() throws OdaException
	{
		return Constants.DATA_SOURCE_MAJOR_VERSION;
	}


	// ------------------------------------------------------------------------
	public int getDataSourceMinorVersion() throws OdaException
	{
		return Constants.DATA_SOURCE_MINOR_VERSION;
	}


	// ------------------------------------------------------------------------
	public String getDataSourceProductName() throws OdaException
	{
		return "Web-CAT Data Source";
	}


	// ------------------------------------------------------------------------
	public String getDataSourceProductVersion() throws OdaException
	{
		return Integer.toString(getDataSourceMajorVersion()) + "." + //$NON-NLS-1$
				Integer.toString(getDataSourceMinorVersion());
	}


	// ------------------------------------------------------------------------
	public int getSQLStateType() throws OdaException
	{
		return IDataSetMetaData.sqlStateSQL99;
	}


	// ------------------------------------------------------------------------
	public boolean supportsMultipleResultSets() throws OdaException
	{
		return false;
	}


	// ------------------------------------------------------------------------
	public boolean supportsMultipleOpenResults() throws OdaException
	{
		return false;
	}


	// ------------------------------------------------------------------------
	public boolean supportsNamedResultSets() throws OdaException
	{
		return false;
	}


	// ------------------------------------------------------------------------
	public boolean supportsNamedParameters() throws OdaException
	{
		return false;
	}


	// ------------------------------------------------------------------------
	public boolean supportsInParameters() throws OdaException
	{
		return true;
	}


	// ------------------------------------------------------------------------
	public boolean supportsOutParameters() throws OdaException
	{
		return false;
	}


	// ------------------------------------------------------------------------
	public int getSortMode()
	{
		return IDataSetMetaData.sortModeNone;
	}


	// === Instance Variables =================================================

	/**
	 * The connection that created this data set metadata object.
	 */
	private IConnection connection;
}
