package net.sf.webcat.reporter.queryassistants;

import com.webobjects.eocontrol.EOQualifier;

/**
 * Component classes that implement a reporter query assistant must implement
 * this interface. A query assistant can use whatever internal representation
 * it wishes to maintain the state of the query being constructed; this
 * interface defines how it transforms that state into an EOQualifier that can
 * be used in a fetch specification.
 * 
 * @author aallowat
 */
public interface IQueryAssistant
{
	// ------------------------------------------------------------------------
	/**
	 * Gets a qualifier that represents the current internal state of this
	 * query assistant.
	 * 
	 * @return an EOQualifier object
	 */
	EOQualifier qualifierFromState();
	

	// ------------------------------------------------------------------------
	/**
	 * Converts the specified qualifier to whatever internal state
	 * representation that this query assistant uses to maintain the query
	 * begin constructed.
	 * 
	 * @param q the EOQualifier to obtain the state from
	 */
	void takeStateFromQualifier(EOQualifier q);
}
