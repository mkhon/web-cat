package net.sf.webcat.reporter.datamodel;

/**
 * The entry point into the Web-CAT data store. This interface is accessible
 * through JavaScript in BIRT scripted data sets as the object named "WebCAT".
 *  
 * @author Tony Allevato
 */
public interface IDataSource
{
	/**
	 * Executes the specified query and returns its result set.
	 *  
	 * @param queryString the query to evaluate
	 * 
	 * @return an IResultSet containing the results of evaluating the query
	 */
	IResultSet executeQuery(String queryString);
}
