package net.sf.webcat.reporter.queryassistants;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

public class KVCAttributeFinder
{
	private static final NSMutableArray<Class<?>> acceptableTypes;
	private static final NSMutableDictionary<Class<?>, NSArray<String>> cache;

	static
	{
		cache = new NSMutableDictionary<Class<?>, NSArray<String>>();
		
		acceptableTypes = new NSMutableArray<Class<?>>();
/*		acceptableTypes.addObject(Number.class);
		acceptableTypes.addObject(Integer.class);
		acceptableTypes.addObject(Integer.TYPE);
		acceptableTypes.addObject(Double.class);
		acceptableTypes.addObject(Double.TYPE);
		acceptableTypes.addObject(Float.class);
		acceptableTypes.addObject(Float.TYPE);
		acceptableTypes.addObject(BigDecimal.class);
		acceptableTypes.addObject(String.class);
		acceptableTypes.addObject();*/
	}

	public static NSArray<String> attributesForClass(Class<?> klass,
			String prefix)
	{
		NSArray<String> unfiltered;

		if(cache.containsKey(klass))
		{
			unfiltered = cache.objectForKey(klass);
		}
		else
		{
			NSMutableArray<String> attrs = new NSMutableArray<String>();
	
			if(NSKeyValueCoding.class.isAssignableFrom(klass))
			{
				addMethods(klass.getMethods(), attrs);
				addFields(klass.getFields(), attrs);
			}
			
			try
			{
				attrs.sortUsingComparator(
						NSComparator.AscendingCaseInsensitiveStringComparator);
			}
			catch(Exception e)
			{
			}

			cache.setObjectForKey(attrs, klass);
			unfiltered = attrs;
		}
		
		NSMutableArray<String> filtered = new NSMutableArray<String>();

		for(String key : unfiltered)
		{
			if(key.toLowerCase().startsWith(prefix.toLowerCase()))
				filtered.addObject(key);
		}
		
		return filtered;
	}
	
	private static void addMethods(Method[] methods,
			NSMutableArray<String> attrs)
	{
		for(Method method : methods)
		{
			String name = method.getName();
			int modifiers = method.getModifiers();

			if((modifiers & Modifier.PUBLIC) != 0 &&
					(modifiers & Modifier.STATIC) == 0 &&
					method.getParameterTypes().length == 0 &&
					typeIsAcceptable(method.getReturnType()))
			{
				if(name.startsWith("_get"))
				{
					name = initialLower(name.substring("_get".length()));
				}
				else if(name.startsWith("get"))
				{
					name = initialLower(name.substring("get".length()));
				}
				else if(name.startsWith("_is"))
				{
					name = initialLower(name.substring("_is".length()));
				}
				else if(name.startsWith("is"))
				{
					name = initialLower(name.substring("is".length()));
				}
				else if(name.startsWith("_"))
				{
					name = name.substring("_".length());
				}
				
				if(!attrs.containsObject(name))
					attrs.addObject(name);
			}
		}
	}

	private static void addFields(Field[] fields, NSMutableArray<String> attrs)
	{
		for(Field field : fields)
		{
			String name = field.getName();
			int modifiers = field.getModifiers();

			if((modifiers & Modifier.PUBLIC) != 0 &&
					(modifiers & Modifier.STATIC) == 0)
			{
				if(name.startsWith("_is"))
				{
					name = initialLower(name.substring("_is".length()));
				}
				else if(name.startsWith("is"))
				{
					name = initialLower(name.substring("is".length()));
				}
				else if(name.startsWith("_"))
				{
					name = name.substring("_".length());
				}

				if(!attrs.containsObject(name))
					attrs.addObject(name);
			}
		}
	}
	
	private static boolean typeIsAcceptable(Class<?> klass)
	{
		//return acceptableTypes.containsObject(klass);
		return klass != Void.class && klass != Void.TYPE;
	}
	
	private static String initialLower(String str)
	{
		if(str != null && str.length() > 0)
		{
			return "" + Character.toUpperCase(str.charAt(0)) +
				str.substring(1);
		}
		else
		{
			return str;
		}
	}
}
