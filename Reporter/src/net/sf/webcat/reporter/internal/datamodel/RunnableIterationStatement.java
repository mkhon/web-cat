package net.sf.webcat.reporter.internal.datamodel;

import java.util.List;

import org.apache.log4j.Logger;

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
public class RunnableIterationStatement extends RunnableStatement
{
	/**
	 * Creates a new runnable statement that is associated with a statement
	 * in the query specification.
	 * 
	 * @param source the statement with which this runnable is associated
	 * @param refEnv the reference environment to use when executing the query
	 * @param parent the parent of this runnable statement
	 */
	public RunnableIterationStatement(IterationStatement source,
			ReferenceEnvironment refEnv, RunnableStatement parent)
	{
		super(source, refEnv, parent);
	}

	public IterationStatement iterationStatement()
	{
		return (IterationStatement)statement();
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
		 * The list of elements being traversed at this level of the query.
		 */
		private List elements;
		
		/**
		 * The current element being traversed at this level of the query.
		 */
		private int index;
		
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
		private static final int STATE_YIELDED_THIS_LEVEL = 1;
		private static final int STATE_YIELDED_DESCENDANT = 2;
		
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
	            		iterationStatement().options());

	            if (!(result instanceof List))
	            {
	            	// If the key path does not evaluate to an List, this
	            	// is an error since we can't iterate over it. Throw an
	            	// exception that will bubble all the way up to the report
	            	// generator. The error will be displayed as an exception
	            	// trace in the rendered report.
	            	
	            	String msg;
	            	if(iterationStatement().entity() != null)
	            	{
	            		msg = iterationStatement().entity();
	            		if(iterationStatement().sourceExpression() != null)
	            			msg += ":\"" +
	            				iterationStatement().sourceExpression() + "\"";
	            			
	            	}
	            	else
	            	{
            			msg = "\"" +
        					iterationStatement().sourceExpression() + "\"";
	            	}

	                state = STATE_DONE;
	            	throw new IllegalArgumentException(
	            			"Expression " + msg +
	            			" does not evaluate to an object of type " +
	            			"List.");
	            }

	            elements = (List)result;
	            index = 0;
	            
	            refEnv().startProgressTask(elements.size());
	        }

	        // This flag is required for proper looping when the state is one
	        // of the YIELD values. If it is, we need to "jump down" to the
	        // appropriate block inside the loop, but then when the loop
	        // continues after that, we want to execute everything from the
	        // top.
	        boolean firstRun = true;

	        while (index < elements.size())
	        {
	            if (!firstRun || (firstRun && state == STATE_INITIALIZE))
	            {
	                Object childRef = elements.get(index);
	                
	                // Add the current reference to the reference environment
	                // so that it can be used as the root of key paths in
	                // descendent statements and value accesses.
	                refEnv().define(iterationStatement().binding(), childRef);

	                // If we're at the deepest level of the query, start
	                // yielding the rows that we find. Otherwise, initialize
	                // a walker for the next level down.
	                if (child() == null)
	                {
	                    state = STATE_YIELDED_THIS_LEVEL;
	                    return true;
	                }
	                else
	                {
	                	descendantWalker = child().dataWalker();
	                }
	            }

	            // *** Entry point when state == STATE_YIELDED_DESCENDANT:
	            
	            if (!firstRun || (firstRun && (state == STATE_INITIALIZE ||
	            		state == STATE_YIELDED_DESCENDANT)))
	            {
	            	// We have a child walker that may still have rows to
	            	// yield before continuing along this level. If so,
	            	// yield them all here.
	                if (descendantWalker.advance())
	                {
	                    state = STATE_YIELDED_DESCENDANT;
	                    return true;
	                }
	            }

	            // *** Entry point when state == STATE_YIELDED_THIS_LEVEL:

	            // We're done with the current row, so we can undefine it in
	            // the reference environment.
	            refEnv().undefine(iterationStatement().binding());
	
	            firstRun = false;
	            index++;
	            
	            refEnv().stepProgress(1);
	        }

	        // This block is reached when we've finished iterating over a
	        // level in the "tree." At this point this DataWalker object
	        // has served its purpose and will no longer be referenced by
	        // its parent.
	        refEnv().completeCurrentTask();
	        state = STATE_DONE;
	        return false;
	    }
	}
	
	private static Logger log = Logger.getLogger(RunnableIterationStatement.class);
}
