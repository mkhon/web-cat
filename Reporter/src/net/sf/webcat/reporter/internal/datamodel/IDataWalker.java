package net.sf.webcat.reporter.internal.datamodel;

/**
 * This interface is implemented by objects that traverse the object graph in
 * Web-CAT. It only contains an advance() method to move to the next item,
 * since there is no notion of a "current object." Rather, data is accessed
 * via key paths branching off the objects stored by name in the reference
 * environment.
 * 
 * @author aallowat
 */
public interface IDataWalker
{
	/**
	 * Advances the walker to the next element in the traversal.
	 * 
	 * @return true if advancing was successful; false if there is no more
	 *     data available.
	 */
	boolean advance();
}
