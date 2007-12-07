package net.sf.webcat.oda.core;

/**
 * Constants used throughout the Web-CAT ODA and ODA user interface plug-ins.
 * 
 * @author Tony Allevato (Virginia Tech Computer Science)
 */
public interface Constants
{
	/**
	 * The major version number of the ODA plug-in.
	 */
	static final int DATA_SOURCE_MAJOR_VERSION = 1;

	/**
	 * The minor version number of the ODA plug-in.
	 */
	static final int DATA_SOURCE_MINOR_VERSION = 0;

	/**
	 * The app context key whose value is the result set provider that should
	 * be queried for data from Web-CAT.
	 */
	static final String APPCONTEXT_RESULT_SET_PROVIDER =
		"net.sf.webcat.oda.resultSetProvider";
}
