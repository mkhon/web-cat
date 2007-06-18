package net.sf.webcat.reporter.internal.datamodel;

import com.webobjects.foundation.NSArray;

/**
 * A RunnableStatement is a "runnable" version of a query statement.
 * Essentially, the QueryStatement class represents only the informational
 * quality of the statment -- its binding name, key path, and "where"
 * clause -- but the RunnableStatement allows the traversal of the data
 * in the object graph based on that information. This allows us to separate
 * the specification from the execution so that a single Query object (with
 * a single set of Statements) can have multiple ResultSets executed from it
 * existing simultaneously.
 * 
 * @author aallowat
 */
public abstract class RunnableStatement
{
	/**
	 * The query statement with which this runnable is associated.
	 */
	private QueryStatement source;

	/**
	 * The immediate child of this runnable statement.
	 */
	private RunnableStatement runnableChild;
	
	/**
	 * The reference environment used to access and store the objects in the
	 * query.
	 */
	private ReferenceEnvironment refEnv;

	public static RunnableStatement createRunnableStatement(
			QueryStatement source, ReferenceEnvironment refEnv,
			RunnableStatement parent)
	{
		if(source instanceof IterationStatement)
		{
			return new RunnableIterationStatement((IterationStatement)source,
					refEnv, parent);
		}
		else if(source instanceof AliasStatement)
		{
			return new RunnableAliasStatement((AliasStatement)source,
					refEnv, parent);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Creates a new runnable statement that is associated with a statement
	 * in the query specification.
	 * 
	 * @param source the statement with which this runnable is associated
	 * @param refEnv the reference environment to use when executing the query
	 * @param parent the parent of this runnable statement
	 */
	protected RunnableStatement(QueryStatement source, ReferenceEnvironment refEnv,
			RunnableStatement parent)
	{
		this.source = source;
		this.refEnv = refEnv;

		if(parent != null)
			parent.runnableChild = this;
	}

	public QueryStatement statement()
	{
		return source;
	}
	
	public RunnableStatement child()
	{
		return runnableChild;
	}
	
	public ReferenceEnvironment refEnv()
	{
		return refEnv;
	}

	/**
	 * Creates and returns an object used to traverse the data beginning at
	 * this level in the query.
	 * 
	 * @return a DataWalker object used to walk the leaves of the data tree
	 * 
	 * @throws IllegalArgumentException if the key path associated with this
	 *     quantifier does not evaluate to a list.
	 */
	public abstract IDataWalker dataWalker();
}
