package net.sf.webcat.reporter.internal.datamodel;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

/**
 * An iteration statement is a specification of a looping construct used to
 * traverse the items in a collection residing in the Web-CAT object graph.
 *
 * Each statement has three parts:
 *
 *   1) binding: the name that will be used to refer to the current object
 *      of iteration in nested statements and key path evaluations
 *   2) key path: the key path that, when evaluated, returns the elements
 *      to be iterated over
 *   3) "where" clause: an optional clause used to filter the elements
 *      returned by evaluating the key path
 * 
 * @author aallowat
 */
public class IterationStatement extends QueryStatement
{
	private NSDictionary options;
	
	/**
	 * Creates a new query statement with the specified parameters.
	 * 
	 * @param binding the name that will be used to refer to the objects
	 *     at this level in lower levels of the query
	 * @param keyPath the key path that evaluates to the list of objects at
	 *     this level of the query
	 * @param whereClause the "where" clause used to filter the results
	 *     returned by evaluating the key path
	 */
	public IterationStatement(NSDictionary options)
	{
		this.options = options;
	}

	/**
	 * Gets the name of the binding used to represent elements iterated over
	 * in this query statement.
	 * 
	 * @return the name of the binding
	 */
	public String binding()
	{
		return (String)options.objectForKey("binding");
	}
	
	/**
	 * Gets the key path that should be evaluated to access the elements to
	 * be iterated over in this query statement.
	 * 
	 * @return the key path associated with this query statement
	 */
	public String entity()
	{
		return (String)options.objectForKey("entity");
	}

	/**
	 * Gets the key path that should be evaluated to access the elements to
	 * be iterated over in this query statement.
	 * 
	 * @return the key path associated with this query statement
	 */
	public String sourceExpression()
	{
		return (String)options.objectForKey("source");
	}
	
	/**
	 * Gets the "where" clause used to filter the elements iterated over by
	 * this query statement.
	 * 
	 * @return the claused used to filter in this query statement
	 */
	public String filterExpression()
	{
		return (String)options.objectForKey("filter");
	}
	
	public NSArray sortOrderings()
	{
		return (NSArray)options.objectForKey("sortOrderings");
	}
	
	public NSDictionary options()
	{
		return options;
	}
}
