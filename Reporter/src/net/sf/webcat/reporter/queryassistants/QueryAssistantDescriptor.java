/*==========================================================================*\
 |  $Id: QueryAssistantDescriptor.java,v 1.2 2008/03/31 18:27:11 stedwar2 Exp $
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

import com.webobjects.foundation.NSKeyValueCoding;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * Query assistant support class.
 *
 * @author aallowat
 * @version $Id: QueryAssistantDescriptor.java,v 1.2 2008/03/31 18:27:11 stedwar2 Exp $
 */
public class QueryAssistantDescriptor
    implements NSKeyValueCoding
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * This is the default constructor
     * @param modName  The name of the model used by this query assistant
     * @param compName The name of the component implementing this query
     *                 assistant
     * @param desc     A description
     */
	public QueryAssistantDescriptor(
        String modName, String compName, String desc)
	{
		modelName = modName;
		componentName = compName;
		description = desc;
	}


    //~ Methods ...............................................................

    // ----------------------------------------------------------
	public String modelName()
	{
		return modelName;
	}


    // ----------------------------------------------------------
	public String componentName()
	{
		return componentName;
	}


    // ----------------------------------------------------------
	public String description()
	{
		return description;
	}


    // ----------------------------------------------------------
	public AbstractQueryAssistantModel createModel()
	{
		try
		{
			Class<?> klass = Class.forName(modelName);
			return (AbstractQueryAssistantModel)klass.newInstance();
		}
		catch (Exception e)
		{
			// This shouldn't happen because we check at load time if the
            // class exists.
			log.error(
                "Could not create query assistant model of type " + modelName);
			return null;
		}
	}


    // ----------------------------------------------------------
	public void takeValueForKey(Object value, String key)
	{
		NSKeyValueCoding.DefaultImplementation.takeValueForKey(
            this, value, key);
	}


    // ----------------------------------------------------------
	public Object valueForKey(String key)
	{
		return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
	}


    //~ Instance/static variables .............................................

    private String modelName;
    private String componentName;
    private String description;

    private static final Logger log =
		Logger.getLogger(QueryAssistantDescriptor.class);
}
