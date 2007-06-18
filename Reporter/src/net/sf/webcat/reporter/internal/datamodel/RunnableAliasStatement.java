package net.sf.webcat.reporter.internal.datamodel;

import com.webobjects.foundation.NSArray;

public class RunnableAliasStatement extends RunnableStatement
{
	/**
	 * Creates a new runnable statement that is associated with a statement
	 * in the query specification.
	 * 
	 * @param source the statement with which this runnable is associated
	 * @param refEnv the reference environment to use when executing the query
	 * @param parent the parent of this runnable statement
	 */
	public RunnableAliasStatement(AliasStatement source,
			ReferenceEnvironment refEnv, RunnableStatement parent)
	{
		super(source, refEnv, parent);
	}

	public AliasStatement aliasStatement()
	{
		return (AliasStatement)statement();
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
	public IDataWalker dataWalker()
	{
		return new DataWalker();
	}

	/**
	 * Implements the IDataWalker interface so that the result set generated
	 * by a query can walk the "virtual tree" induced by the query structure
	 * and the objects in the Web-CAT object graph.
	 * 
	 * @author aallowat
	 */
	private class DataWalker implements IDataWalker
	{
		/**
		 * The current state of the finite state machine used to properly
		 * yield and re-enter the traversal.
		 */
		private int state;
				
		/**
		 * The walker associated with the child statement of the level that
		 * we are currently traversing. If this level is not the "leaf" of the
		 * query, then we use this walker to yield everything deeper in the
		 * "tree" before we continue along the current level. 
		 */
		private IDataWalker descendantWalker;
		
		/**
		 * The states used to maintain the traversal.
		 */
		private static final int STATE_DONE = -1;
		private static final int STATE_INITIALIZE = 0;
		private static final int STATE_YIELD_CHILDREN = 1;
		
		/**
		 * Create a new data walker associated with this runnable statement.
		 * The walker will be set to the initial state and is ready to
		 * traverse the data in the data store when the first call to
		 * advance() is made.
		 */
		public DataWalker()
		{
			state = STATE_INITIALIZE;
		}

		/**
		 * Advances the walker to the next element in the traversal.
		 * 
		 * @return true if advancing was successful; false if there is no more
		 *     data available.
		 */
		public boolean advance()
		{
	        if (state == STATE_DONE)
	            return false;

	        // *** Entry point when state == STATE_INITIALIZE:
	        
	        // If this is the first time the walker is being used, we need to
	        // initialize it by evaluating the key path and storing the list
	        // that is the result.
	        if (state == STATE_INITIALIZE)
	        {
	            Object result = refEnv().evaluate(
	            		aliasStatement().options());
	
	            refEnv().define(aliasStatement().binding(), result);

	            if(child() != null)
	            	descendantWalker = child().dataWalker();
            	
            	state = STATE_YIELD_CHILDREN;
	        }

	        if(descendantWalker != null)
	        {
	        	return descendantWalker.advance();
            }

	        state = STATE_DONE;
	        return true;
	    }
	}
}
