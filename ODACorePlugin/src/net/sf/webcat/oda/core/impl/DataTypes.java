package net.sf.webcat.oda.core.impl;

import java.sql.Types;
import java.util.Locale;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.util.manifest.DataTypeMapping;
import org.eclipse.datatools.connectivity.oda.util.manifest.ExtensionManifest;
import org.eclipse.datatools.connectivity.oda.util.manifest.ManifestExplorer;

/**
 * Manages the mapping between native data type codes and their string
 * representation.
 * 
 * @author Tony Allevato
 */
public class DataTypes
{
	// === Methods ===========================================================

	// -----------------------------------------------------------------------
	/**
	 * Prevent instantiation of the DataTypes class.
	 */
	private DataTypes()
	{
	}


	// -----------------------------------------------------------------------
	/**
	 * Gets the native data type code that corresponds to the specified data
	 * type name.
	 * 
	 * @param typeName
	 *            the name of the data type
	 * 
	 * @return the native data type code
	 * 
	 * @throws OdaException
	 *             if the typename is invalid
	 */
	public static int getType(String typeName) throws OdaException
	{
		if(typeName == null || typeName.trim().length() == 0)
			return STRING;

		String preparedTypeName = typeName.trim().toUpperCase(Locale.ENGLISH);

		DataTypeMapping typeMapping = getManifest().getDataSetType(null)
				.getDataTypeMapping(preparedTypeName);

		if(typeMapping != null)
			return typeMapping.getNativeTypeCode();

		throw new OdaException("Invalid typename " + typeName);
	}


	// -----------------------------------------------------------------------
	/**
	 * Gets the data type name for the specified native data type code.
	 * 
	 * @param type
	 *            the native data type code
	 * 
	 * @return a String containing the data type name
	 * 
	 * @throws OdaException
	 *             if the native data type code is invalid
	 */
	public static String getTypeString(int type) throws OdaException
	{
		DataTypeMapping typeMapping = getManifest().getDataSetType(null)
				.getDataTypeMapping(type);
		if(typeMapping != null)
			return typeMapping.getNativeType();

		throw new OdaException("Invalid type " + type);
	}


	// -----------------------------------------------------------------------
	/**
	 * Returns a value indicating whether the specified type name represents a
	 * valid type.
	 * 
	 * @param typeName
	 *            the name of the data type
	 * 
	 * @return true if the data type is valid; otherwise, false.
	 */
	public static boolean isValidType(String typeName)
	{
		String preparedTypeName = typeName.trim().toUpperCase(Locale.ENGLISH);

		DataTypeMapping typeMapping = null;
		try
		{
			typeMapping = getManifest().getDataSetType(null)
					.getDataTypeMapping(preparedTypeName);
		}
		catch (OdaException e)
		{
		}

		return typeMapping != null;
	}


	// -----------------------------------------------------------------------
	/**
	 * 
	 * @return
	 * @throws OdaException
	 */
	private static ExtensionManifest getManifest() throws OdaException
	{
		return ManifestExplorer.getInstance().getExtensionManifest(
				WEBCAT_DATA_SOURCE_ID);
	}

	// === Static Variables ===================================================

	public static final int INT = Types.INTEGER;

	public static final int DOUBLE = Types.DOUBLE;

	public static final int DECIMAL = Types.DECIMAL;

	public static final int STRING = Types.VARCHAR;

	public static final int TIMESTAMP = Types.TIMESTAMP;

	public static final int BOOLEAN = Types.BOOLEAN;

	private static final String WEBCAT_DATA_SOURCE_ID = "net.sf.webcat.oda";
}
