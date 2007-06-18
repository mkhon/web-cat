package net.sf.webcat.reporter.internal.datamodel;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSPropertyListSerialization;

import net.sf.webcat.reporter.datamodel.IResultSet;
import net.sf.webcat.reporter.internal.parser.QueryParser;

public class Query
{
	/**
	 * The set of initial bindings that wil be passed along to the new
	 * reference environment when this query is executed.
	 */
	private NSDictionary initialBindings;

	/**
	 * The sequence of statements that define the query.
	 */
	private NSMutableArray statements;

	/**
	 * The editing context used to access entities in the EO model.
	 */
	private EOEditingContext context;
	
	private String uuid;
	
	/**
	 * Creates a new query.
	 * 
	 * @param initialBindings the set of initial bindings to use when
	 *     initializing the reference environment for this query.
	 * @param context the editing context used to access entities in the EO
	 *     model.
	 */
	public Query(NSDictionary initialBindings, EOEditingContext context,
			String uuid)
	{
		this.initialBindings = initialBindings;
		this.statements = new NSMutableArray();
		this.context = context;
		this.uuid = uuid;
	}

	public Query(String queryString, NSDictionary initialBindings,
			EOEditingContext context, String uuid)
	{
		this(initialBindings, context, uuid);
		
		QueryParser parser = new QueryParser(queryString, this);
		parser.processQueryString();
	}

	public void addStatement(QueryStatement statement)
	{
		statements.addObject(statement);
	}

	public IResultSet execute()
	{
		ReferenceEnvironment refEnv =
			new ReferenceEnvironment(initialBindings, context, uuid);

		return new ResultSet(refEnv, statements);
	}
}
