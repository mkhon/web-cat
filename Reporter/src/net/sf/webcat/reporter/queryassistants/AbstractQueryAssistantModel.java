package net.sf.webcat.reporter.queryassistants;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSKeyValueCoding;

/**
 * This interface defines the two methods that any query assistant model must
 * implement, in order to translate the model's internal state to and from
 * EOModel qualifiers that are stored in the query database.
 * 
 * @author aallowat
 */
public abstract class AbstractQueryAssistantModel implements NSKeyValueCoding
{
	/**
	 * This method converts the specified qualifier into whatever internal
	 * state model the query assistant model requires. If the conversion is not
	 * possible (i.e., the qualifier came from a different query assistant and
	 * this one does not know how to handle it), then the model should either
	 * try to interpret as much of the qualifier as it can, or simply
	 * initialize itself to defaults.
	 * 
	 * The model should NOT try to preserve qualifiers that it does not
	 * understand and reconstitute them in the qualifierFromValues method. This
	 * would cause user confusion as the user interface for a particular
	 * query assistant would have no way of displaying or modifying these
	 * "hidden" qualifiers.
	 *  
	 * @param qualifier the qualifier to convert to the model
	 */
	public abstract void takeValuesFromQualifier(EOQualifier qualifier);
	
	
	/**
	 * This method converts the internal state of the query assistant model
	 * into a qualifier that can be stored in the database.
	 * 
	 * @return a qualifier that represents the internal state of this query
	 *     assistant
	 */
	public abstract EOQualifier qualifierFromValues();
	
	
	/**
	 * Key-value coding support.
	 */
	public void takeValueForKey(Object value, String key)
	{
		NSKeyValueCoding.DefaultImplementation.takeValueForKey(this,
				value, key);
	}


	public Object valueForKey(String key)
	{
		return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
	}
}
