package net.sf.webcat.reporter;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

public class EOGlobalIDUtils
{
	public static NSArray enterpriseObjectsForIdArray(List array,
			EOEditingContext ec)
	{
		NSMutableArray newArray = new NSMutableArray();
		
		if(array instanceof NSArray)
		{
			Enumeration e = ((NSArray)array).objectEnumerator();
			while(e.hasMoreElements())
			{
				Object value = e.nextElement();
				
				Object newValue = tryEnterpriseObjectForId(value, ec);
				newArray.addObject(newValue);
			}
		}
		else
		{
			Iterator it = array.iterator();
			while(it.hasNext())
			{
				Object value = it.next();
				
				Object newValue = tryEnterpriseObjectForId(value, ec);
				newArray.addObject(newValue);
			}
		}
		
		return newArray;
	}
	
	public static NSDictionary enterpriseObjectsForIdDictionary(
			Map dictionary, EOEditingContext ec)
	{
		NSMutableDictionary newDictionary = new NSMutableDictionary();

		if(dictionary instanceof NSDictionary)
		{
			Enumeration e = ((NSDictionary)dictionary).keyEnumerator();
			while(e.hasMoreElements())
			{
				Object key = e.nextElement();
				Object value = ((NSDictionary)dictionary).objectForKey(key);
				
				Object newValue = tryEnterpriseObjectForId(value, ec);
				newDictionary.setObjectForKey(newValue, key);
			}
		}
		else
		{
			Iterator it = dictionary.keySet().iterator();
			while(it.hasNext())
			{
				Object key = it.next();
				Object value = dictionary.get(key);
				
				Object newValue = tryEnterpriseObjectForId(value, ec);
				newDictionary.setObjectForKey(newValue, key);
			}
		}
		
		return newDictionary;
	}
	
	public static Object tryEnterpriseObjectForId(Object value,
			EOEditingContext ec)
	{
		if(value instanceof List)
		{
			return enterpriseObjectsForIdArray((List)value, ec);
		}
		else if(value instanceof Map)
		{
			return enterpriseObjectsForIdDictionary((Map)value, ec);
		}
		else if(value instanceof EOGlobalID)
		{
			return ec.faultForGlobalID((EOGlobalID)value, ec);
		}
		else
		{
			return value;
		}
	}

	public static NSArray idsForEnterpriseObjectArray(List array,
			EOEditingContext ec)
	{
		NSMutableArray newArray = new NSMutableArray();
		
		if(array instanceof NSArray)
		{
			Enumeration e = ((NSArray)array).objectEnumerator();
			while(e.hasMoreElements())
			{
				Object value = e.nextElement();
				
				Object newValue = tryIdForEnterpriseObject(value, ec);
				newArray.addObject(newValue);
			}
		}
		else
		{
			Iterator it = array.iterator();
			while(it.hasNext())
			{
				Object value = it.next();
				
				Object newValue = tryIdForEnterpriseObject(value, ec);
				newArray.addObject(newValue);
			}
		}
		
		return newArray;
	}
	
	public static NSDictionary idsForEnterpriseObjectDictionary(
			Map dictionary, EOEditingContext ec)
	{
		NSMutableDictionary newDictionary = new NSMutableDictionary();

		if(dictionary instanceof NSDictionary)
		{
			Enumeration e = ((NSDictionary)dictionary).keyEnumerator();
			while(e.hasMoreElements())
			{
				Object key = e.nextElement();
				Object value = ((NSDictionary)dictionary).objectForKey(key);
				
				Object newValue = tryIdForEnterpriseObject(value, ec);
				newDictionary.setObjectForKey(newValue, key);
			}
		}
		else
		{
			Iterator it = dictionary.keySet().iterator();
			while(it.hasNext())
			{
				Object key = it.next();
				Object value = dictionary.get(key);
				
				Object newValue = tryIdForEnterpriseObject(value, ec);
				newDictionary.setObjectForKey(newValue, key);
			}
		}
		
		return newDictionary;
	}
	
	public static Object tryIdForEnterpriseObject(Object value, EOEditingContext ec)
	{
		if(value instanceof List)
		{
			return idsForEnterpriseObjectArray((List)value, ec);
		}
		else if(value instanceof Map)
		{
			return idsForEnterpriseObjectDictionary((Map)value, ec);
		}
		else if(value instanceof EOEnterpriseObject)
		{
			return ec.globalIDForObject((EOEnterpriseObject)value);
		}
		else
		{
			return value;
		}
	}
}
