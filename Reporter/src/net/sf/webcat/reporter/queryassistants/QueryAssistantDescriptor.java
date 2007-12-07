package net.sf.webcat.reporter.queryassistants;

import org.apache.log4j.Logger;

import com.webobjects.foundation.NSKeyValueCoding;

public class QueryAssistantDescriptor implements NSKeyValueCoding
{
	private String modelName;

	private String componentName;
	
	private String description;

	public QueryAssistantDescriptor(String modName, String compName,
			String desc)
	{
		modelName = modName;
		componentName = compName;
		description = desc;
	}

	public String modelName()
	{
		return modelName;
	}

	public String componentName()
	{
		return componentName;
	}
	
	public String description()
	{
		return description;
	}

	public AbstractQueryAssistantModel createModel()
	{
		try
		{
			Class<?> klass = Class.forName(modelName);
			return (AbstractQueryAssistantModel)klass.newInstance();
		}
		catch(Exception e)
		{
			// This shouldn't happen because we check at load time if the class
			// exists.
			log.error("Could not create query assistant model of type " +
					modelName);

			return null;
		}
	}

	public void takeValueForKey(Object value, String key)
	{
		NSKeyValueCoding.DefaultImplementation.takeValueForKey(this, value, key);
	}

	public Object valueForKey(String key)
	{
		return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
	}
	
	private static final Logger log =
		Logger.getLogger(QueryAssistantDescriptor.class);
}
