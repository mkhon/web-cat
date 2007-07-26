package net.sf.webcat.reporter.internal.datamodel;

import org.apache.log4j.Logger;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

import net.sf.webcat.reporter.datamodel.IDataSource;
import net.sf.webcat.reporter.datamodel.IResultSet;

/**
 * This class acts as the entry point of the bridge connecting the Web-CAT
 * data source and the report templates used by BIRT. A single instance of
 * this object is created for the lifetime of the reporter subsystem, and
 * it is registered with BIRT as a scriptable object that can be accessed
 * in the JavaScript code provided for a scripted data set.
 * 
 * @author aallowat
 */
public class DataSource implements IDataSource
{
	/**
	 * The name that will be used to refer to this object in JavaScript.
	 */
	public static final String SCRIPTABLE_OBJECT_NAME = "WebCAT";

	/**
	 * Creates an instance of the reporter data bridge using the specified
	 * editing context.
	 * 
	 * @param context the EOEditingContext to use
	 */
	public DataSource(NSDictionary initialBindings, String uuid)
	{
//		this.context = context;
		this.initialBindings = initialBindings;
		this.uuid = uuid;
	}

	public IResultSet executeQuery(String queryString)
	{
		Query newQuery = new Query(queryString, initialBindings, uuid);
		return newQuery.execute();
	}

	public void log(Object message)
	{
		log.debug("Message from report script: " + message.toString());
	}

	/**
	 * The editing context used to access EO entities in query clauses.
	 * The reporter data bridge will use the same single context that the
	 * report processing queue creates, rather than generating a new one
	 * for each operation.
	 */
//	private EOEditingContext context;
	
	private NSDictionary initialBindings;
	
	private String uuid;
	
	private static Logger log = Logger.getLogger(DataSource.class);
}
