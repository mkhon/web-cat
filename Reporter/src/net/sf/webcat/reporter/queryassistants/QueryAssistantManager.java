/*==========================================================================*\
 |  $Id: QueryAssistantManager.java,v 1.2 2008/03/31 18:27:11 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU General Public License as published by
 |  the Free Software Foundation; either version 2 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU General Public License
 |  along with Web-CAT; if not, write to the Free Software
 |  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 |
 |  Project manager: Stephen Edwards <edwards@cs.vt.edu>
 |  Virginia Tech CS Dept, 660 McBryde Hall (0106), Blacksburg, VA 24061 USA
\*==========================================================================*/

package net.sf.webcat.reporter.queryassistants;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSPropertyListSerialization;
import net.sf.webcat.core.Application;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * A singleton that manages the set of query assistants that are available,
 * and looks up those that are applicable based on the kind of entity a query
 * is for.  The available query assistants are read from a plist file
 * stored in the subsystem's resources.
 *
 * @author aallowat
 * @version $Id: QueryAssistantManager.java,v 1.2 2008/03/31 18:27:11 stedwar2 Exp $
 */
public class QueryAssistantManager
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Get the singleton instance of this class.
     * @return The single shared instance of this class
     */
	public static QueryAssistantManager getInstance()
	{
		if (instance == null)
        {
			instance = new QueryAssistantManager();
        }
		return instance;
	}


    // ----------------------------------------------------------
    /**
     * The constructor is private, since this is a singleton class.
     */
	private QueryAssistantManager()
	{
		NSBundle myBundle = NSBundle.bundleForClass(getClass());

		NSData data = new NSData(myBundle.bytesForResourcePath(
			"QueryAssistants.plist"));
		NSDictionary<String, Object> plist = (NSDictionary<String, Object>)
			NSPropertyListSerialization.propertyListFromData(data, "UTF-8");

		assistantMap = new
            NSMutableDictionary<String, NSArray<QueryAssistantDescriptor>>();

		for (String entity : plist.allKeys())
		{
			NSMutableArray<QueryAssistantDescriptor> assistants =
				new NSMutableArray<QueryAssistantDescriptor>();

			NSArray<NSDictionary<String, Object>> assistantsInPlist =
				(NSArray<NSDictionary<String, Object>>)plist
                    .objectForKey(entity);

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

				if (success)
				{
					QueryAssistantDescriptor qad =
						new QueryAssistantDescriptor(
                            modelName, componentName, description);

					assistants.addObject(qad);
				}
			}

			assistantMap.setObjectForKey(assistants, entity);
		}
	}


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    public NSArray<QueryAssistantDescriptor> assistantsForEntity(String entity)
    {
        NSMutableArray<QueryAssistantDescriptor> array =
            new NSMutableArray<QueryAssistantDescriptor>();

        array.addObjectsFromArray(assistantMap.objectForKey(entity));
        array.addObjectsFromArray(assistantMap.objectForKey("*"));

        return array;
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
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


    //~ Instance/static variables .............................................

	private NSMutableDictionary<String, NSArray<QueryAssistantDescriptor>>
        assistantMap;
	private static QueryAssistantManager instance;
	private static Logger log = Logger.getLogger(QueryAssistantManager.class);
}
