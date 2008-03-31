package net.sf.webcat.reporter.queryassistants;

import java.util.TimeZone;

import org.apache.log4j.Logger;

import net.sf.webcat.grader.Submission;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

public class AdvancedQueryUtils
{
	public static final int TYPE_STRING = 0;
	public static final int TYPE_INTEGER = 1;
	public static final int TYPE_DOUBLE = 2;
	public static final int TYPE_BOOLEAN = 3;
	public static final int TYPE_TIMESTAMP = 4;
	public static final int TYPE_ENTITY = 5;

	public static int typeOfKeyPath(String entityType, String keypath)
	{
		KeyPathParser parser = new KeyPathParser(entityType, keypath);
		Class<?> klass = parser.theClass();

		return typeOfClass(klass);
	}

	public static int typeOfClass(Class<?> klass)
	{
		if(String.class.isAssignableFrom(klass))
		{
			return TYPE_STRING;
		}
		else if(Integer.class.isAssignableFrom(klass) ||
				klass == Integer.TYPE)
		{
			return TYPE_INTEGER;
		}
		else if(Double.class.isAssignableFrom(klass) ||
				klass == Double.TYPE)
		{
			return TYPE_DOUBLE;
		}
		else if(Boolean.class.isAssignableFrom(klass) ||
				klass == Boolean.TYPE)
		{
			return TYPE_BOOLEAN;
		}
		else if(java.util.Date.class.isAssignableFrom(klass))
		{
			return TYPE_TIMESTAMP;
		}
		else if(EOEnterpriseObject.class.isAssignableFrom(klass))
		{
			return TYPE_ENTITY;
		}
		else
		{
			return TYPE_STRING;
		}
	}

	public static Object valueRangeForPreviewRepresentation(int type,
			String rep, EOEditingContext ec)
	{
		NSMutableDictionary<String, Object> dict =
			new NSMutableDictionary<String, Object>();

		String[] parts = rep.split(",");
		dict.setObjectForKey(
				singleValueForPreviewRepresentation(type, parts[0], ec),
				"minimumValue");
		dict.setObjectForKey(
				singleValueForPreviewRepresentation(type, parts[1], ec),
				"maximumValue");

		return dict;
	}

	public static Object singleValueForPreviewRepresentation(int type,
			String rep, EOEditingContext ec)
	{
		switch(type)
		{
			case TYPE_STRING:
				return rep;

			case TYPE_INTEGER:
	    		try
	    		{
	    			return Integer.parseInt(rep.trim());
	    		}
	    		catch(NumberFormatException e)
	    		{
	    			return null;
	    		}

			case TYPE_DOUBLE:
	    		try
	    		{
	    			return Double.parseDouble(rep.trim());
	    		}
	    		catch(NumberFormatException e)
	    		{
	    			return null;
	    		}

			case TYPE_BOOLEAN:
				return Boolean.parseBoolean(rep);

			case TYPE_TIMESTAMP:
				return timestampFromRepresentation(rep);

			case TYPE_ENTITY:
				try
	    		{
					String[] parts = rep.split(":");
					String entity = parts[0];
					int id = Integer.parseInt(parts[1]);

	    			return objectWithId(id, entity, ec);
	    		}
	    		catch(Exception e)
	    		{
	    			log.warn("Exception trying to find object " +
	    					rep, e);

	    			return null;
	    		}

			default:
				return null;
		}
	}

	public static Object multipleValuesForPreviewRepresentation(
        int type, String rep, EOEditingContext ec)
	{
		switch (type)
		{
			case TYPE_STRING:
			{
		    	String[] values = rep.split(",");

		    	NSMutableArray<String> array = new NSMutableArray<String>();

		    	for (String item : values)
		    	{
		    		array.addObject(item.trim());
		    	}

		    	return array;
			}

			case TYPE_INTEGER:
			{
		    	String[] values = rep.split(",");

		    	NSMutableArray<Integer> array = new NSMutableArray<Integer>();

		    	for (String item : values)
		    	{
		    		try
		    		{
		    			array.addObject(Integer.parseInt(item.trim()));
		    		}
		    		catch(NumberFormatException e)
		    		{
                        // Ignore the erroneous value
		    		}
		    	}

		    	return array;
			}

			case TYPE_DOUBLE:
			{
		    	String[] values = rep.split(",");

		    	NSMutableArray<Double> array = new NSMutableArray<Double>();

		    	for (String item : values)
		    	{
		    		try
		    		{
		    			array.addObject(Double.parseDouble(item.trim()));
		    		}
		    		catch (NumberFormatException e)
		    		{
                        // Ignore the erroneous value
		    		}
		    	}

		    	return array;
			}

			case TYPE_ENTITY:
			{
		    	String[] values = rep.split(",");

		    	NSMutableArray<EOEnterpriseObject> array =
		    		new NSMutableArray<EOEnterpriseObject>();

		    	for (String itemRep : values)
		    	{
					try
		    		{
						String[] parts = itemRep.split(":");
						String entity = parts[0];
						int id = Integer.parseInt(parts[1]);

						EOEnterpriseObject object = objectWithId(
						    id, entity, ec);

						if (object != null)
						{
							array.addObject(object);
						}
						else
						{
							log.warn("No object found for entity " +
							    entity + " with id " + id + "!");
						}
		    		}
		    		catch (Exception e)
		    		{
		    			log.warn("Exception trying to find object " +
		    			    itemRep, e);
		    		}
		    	}

		    	return array;
			}

			default:
				return null;
		}
	}

	private static EOEnterpriseObject objectWithId(
        int id, String entity, EOEditingContext ec)
	{
        if (id > 0)
        {
            NSArray<EOEnterpriseObject> results =
            	EOUtilities.objectsMatchingKeyAndValue( ec,
                entity, "id", new Integer( id ) );

            if ( results != null && results.count() > 0 )
            {
                return results.objectAtIndex( 0 );
            }
        }

        return null;
    }

	private static NSTimestamp timestampFromRepresentation(String rep)
	{
		try
		{
			String[] parts = rep.split(" ");
			return new NSTimestamp(
					Integer.parseInt(parts[0]),
					Integer.parseInt(parts[1]) + 1,
					Integer.parseInt(parts[2]),
					Integer.parseInt(parts[3]),
					Integer.parseInt(parts[4]),
					0, TimeZone.getDefault());
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	private static final Logger log =
		Logger.getLogger(AdvancedQueryUtils.class);
}
