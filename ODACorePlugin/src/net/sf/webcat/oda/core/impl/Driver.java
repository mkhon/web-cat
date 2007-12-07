/*
 * ************************************************************************
 * Copyright (c) 2007 <<Your Company Name here>>
 * 
 * ************************************************************************
 */

package net.sf.webcat.oda.core.impl;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDriver;
import org.eclipse.datatools.connectivity.oda.LogConfiguration;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.util.manifest.DataTypeMapping;
import org.eclipse.datatools.connectivity.oda.util.manifest.ExtensionManifest;
import org.eclipse.datatools.connectivity.oda.util.manifest.ManifestExplorer;

/**
 * Implementation class of IDriver for an ODA runtime driver.
 * 
 * @author Tony Allevato (Virginia Tech Computer Science)
 */
public class Driver implements IDriver
{
	// === Methods ============================================================
	
	// ------------------------------------------------------------------------
	public IConnection getConnection(String dataSourceType) throws OdaException
	{
		return new Connection();
	}


	// ------------------------------------------------------------------------
	public void setLogConfiguration(LogConfiguration logConfig)
			throws OdaException
	{
		// do nothing; assumes simple driver has no logging
	}


	// ------------------------------------------------------------------------
	public int getMaxConnections() throws OdaException
	{
		return 0; // no limit
	}


	// ------------------------------------------------------------------------
	public void setAppContext(Object context) throws OdaException
	{
		// do nothing; assumes no support for pass-through context
	}


	// ------------------------------------------------------------------------
	/**
	 * Returns the object that represents this extension's manifest.
	 * 
	 * @throws OdaException
	 */
	private static ExtensionManifest getManifest() throws OdaException
	{
		return ManifestExplorer.getInstance().getExtensionManifest(
				ODA_DATA_SOURCE_ID);
	}


	// ------------------------------------------------------------------------
	/**
	 * Returns the native data type name of the specified code, as defined in
	 * this data source extension's manifest.
	 * 
	 * @param nativeTypeCode
	 *            the native data type code
	 * @return corresponding native data type name
	 * @throws OdaException
	 *             if lookup fails
	 */
	public static String getNativeDataTypeName(int nativeDataTypeCode)
			throws OdaException
	{
		DataTypeMapping typeMapping = getManifest().getDataSetType(null)
				.getDataTypeMapping(nativeDataTypeCode);
		if(typeMapping != null)
			return typeMapping.getNativeType();
		return "Non-defined";
	}


	// === Static Variables ===================================================
	
	/**
	 * The unique identifier of the Web-CAT ODA data source.
	 */
	private static final String ODA_DATA_SOURCE_ID = "net.sf.webcat.oda"; //$NON-NLS-1$
}
