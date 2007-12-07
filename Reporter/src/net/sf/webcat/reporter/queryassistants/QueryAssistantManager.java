package net.sf.webcat.reporter.queryassistants;

import org.apache.log4j.Logger;

import net.sf.webcat.core.Application;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSPropertyListSerialization;

public class QueryAssistantManager
{
	public static QueryAssistantManager getInstance()
	{
		if(instance == null)
			instance = new QueryAssistantManager();
		
		return instance;
	}

	private QueryAssistantManager()
	{
		NSBundle myBundle = NSBundle.bundleForClass(getClass());
		
		NSData data = new NSData(myBundle.bytesForResourcePath(
				"QueryAssistants.plist"));
		NSDictionary<String, Object> plist = (NSDictionary<String, Object>)
			NSPropertyListSerialization.propertyListFromData(data, "UTF-8");
		
		assistantMap = new NSMutableDictionary<String, NSArray<QueryAssistantDescriptor>>();

		for(String entity : plist.allKeys())
		{
			NSMutableArray<QueryAssistantDescriptor> assistants =
				new NSMutableArray<QueryAssistantDescriptor>();
			
			NSArray<NSDictionary<String, Object>> assistantsInPlist =
				(NSArray<NSDictionary<String, Object>>)plist.objectForKey(entity);
			
			for(NSDictionary<String, Object> asstInPlist : assistantsInPlist)
			{
				String modelName =
					(String)asstInPlist.objectForKey("modelName");
				String componentName =
					(String)asstInPlist.objectForKey("componentName");
				String description =
					(String)asstInPlist.objectForKey("description");
				
				boolean success = verifyClassInheritance(modelName,
						AbstractQueryAssistantModel.class);
				
				if(success)
				{
					QueryAssistantDescriptor qad =
						new QueryAssistantDescriptor(modelName,
								componentName, description);
			
					assistants.addObject(qad);
				}
			}
			
			assistantMap.setObjectForKey(assistants, entity);
		}
	}

	private boolean verifyClassInheritance(String className,
			Class<?> superclass)
	{
		Class<?> klass = null;

		try
		{
			klass = Class.forName(className);
		}
		catch(Exception e)
		{
			log.error("Could not find a class named " + className +
					". This query assistant will not be available.");
			return false;
		}

		if(klass != null)
		{
			if(!superclass.isAssignableFrom(klass))
			{
				log.error("The class " + className + " does not" +
						"inherit from " + superclass.getCanonicalName() + ". " +
						"This query assistant will not be available.");
			}
		}
		
		return true;
	}

	public NSArray<QueryAssistantDescriptor> assistantsForEntity(String entity)
	{
		NSMutableArray<QueryAssistantDescriptor> array =
			new NSMutableArray<QueryAssistantDescriptor>();
		
		array.addObjectsFromArray(assistantMap.objectForKey(entity));
		array.addObjectsFromArray(assistantMap.objectForKey("*"));

		return array;
	}

	private NSMutableDictionary<String, NSArray<QueryAssistantDescriptor>> assistantMap;

	private static QueryAssistantManager instance;
	
	private static Logger log = Logger.getLogger(QueryAssistantManager.class);
}
