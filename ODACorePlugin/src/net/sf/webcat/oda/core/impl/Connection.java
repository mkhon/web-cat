/*
 * ************************************************************************
 * Copyright (c) 2007 <<Your Company Name here>>
 * 
 * ************************************************************************
 */

package net.sf.webcat.oda.core.impl;

import java.util.Map;
import java.util.Properties;

import net.sf.webcat.oda.IWebCATResultSetProvider;
import net.sf.webcat.oda.core.Activator;
import net.sf.webcat.oda.core.Constants;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * Implementation class of IConnection for an ODA runtime driver.
 * 
 * @author Tony Allevato (Virginia Tech Computer Science)
 */
public class Connection implements IConnection
{
	// === Methods ============================================================

	// ------------------------------------------------------------------------
	/**
	 * Creates a new instance of the Connection class.
	 */
	public Connection()
	{
		isOpen = false;
	}


	// ------------------------------------------------------------------------
	public void open(Properties connProperties) throws OdaException
	{
		if(isOpen)
			return;

		if(appContext != null
				&& appContext.get(Constants.APPCONTEXT_RESULT_SET_PROVIDER) != null
				&& appContext.get(Constants.APPCONTEXT_RESULT_SET_PROVIDER) instanceof IWebCATResultSetProvider)
		{
			// Use the data providers passed into the app context.
			resultSets = (IWebCATResultSetProvider)appContext
					.get(Constants.APPCONTEXT_RESULT_SET_PROVIDER);
		}
		else
		{
			try
			{
				resultSets = Activator.getDefault().getResultSetProviderForEmptyAppContext();
			}
			catch (CoreException e)
			{
				Throwable th = e.getStatus().getException();

				if(th instanceof OdaException)
					throw (OdaException)th;
			}
		}

		isOpen = true;
	}


	// ------------------------------------------------------------------------
	public void setAppContext(Object context) throws OdaException
	{
		if(!(context instanceof Map))
		{
			throw new OdaException(
					"App context must be an instance of java.util.Map.");
		}

		appContext = (Map)context;
	}


	// ------------------------------------------------------------------------
	public void close() throws OdaException
	{
		isOpen = false;
	}


	// ------------------------------------------------------------------------
	public boolean isOpen() throws OdaException
	{
		return isOpen;
	}


	// ------------------------------------------------------------------------
	public IDataSetMetaData getMetaData(String dataSetType) throws OdaException
	{
		return new DataSetMetaData(this);
	}


	// ------------------------------------------------------------------------
	public IQuery newQuery(String dataSetType) throws OdaException
	{
		return new Query(resultSets);
	}


	// ------------------------------------------------------------------------
	public int getMaxQueries() throws OdaException
	{
		return 0; // no limit
	}


	// ------------------------------------------------------------------------
	public void commit() throws OdaException
	{
		// do nothing; assumes no transaction support needed
	}


	// ------------------------------------------------------------------------
	public void rollback() throws OdaException
	{
		// do nothing; assumes no transaction support needed
	}

	// === Instance Variables =================================================

	/**
	 * True if the connection is currently open, false if it is closed.
	 */
	private boolean isOpen;

	/**
	 * The app context passed in from the client code that invokes BIRT.
	 */
	private Map appContext;

	/**
	 * A result set provider that was either passed in via the app context, or
	 * created locally if none was passed in (for previewing purposes).
	 */
	private IWebCATResultSetProvider resultSets;
}
