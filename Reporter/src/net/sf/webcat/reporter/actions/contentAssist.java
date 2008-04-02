/*==========================================================================*\
 |  $Id: contentAssist.java,v 1.4 2008/04/02 01:36:38 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2008 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU Affero General Public License as published
 |  by the Free Software Foundation; either version 3 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU Affero General Public License
 |  along with Web-CAT; if not, see <http://www.gnu.org/licenses/>.
\*==========================================================================*/

package net.sf.webcat.reporter.actions;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import er.extensions.ERXDirectAction;
import net.sf.webcat.core.Application;
import net.sf.webcat.core.Subsystem;
import net.sf.webcat.reporter.queryassistants.KVCAttributeFinder;
import net.sf.webcat.reporter.queryassistants.KVCAttributeInfo;

//-------------------------------------------------------------------------
/**
 * Direct action support for AJAX content assist requests for supported
 * KVC paths.
 *
 * @author aallowat
 * @version $Id: contentAssist.java,v 1.4 2008/04/02 01:36:38 stedwar2 Exp $
 */
public class contentAssist
    extends ERXDirectAction
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     * @param request The incoming request
     */
	public contentAssist(WORequest request)
	{
		super(request);
	}


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
	public WOActionResults entityDescriptionsAction()
	{
		WOResponse response = new WOResponse();

		NSDictionary<String, String> versions = subsystemVersions();

		for (String subsystem : versions.allKeys())
		{
			response.appendContentString("version:" + subsystem + "," +
			    versions.objectForKey(subsystem) + "\n");
		}

		for (EOModel model :
			(NSArray<EOModel>)EOModelGroup.defaultGroup().models())
		{
			for (EOEntity entity : (NSArray<EOEntity>)model.entities())
			{
				String className = entity.className();
				boolean exclude = false;

				for (String toExclude : ENTITIES_TO_EXCLUDE)
				{
					if (toExclude.equals(className))
					{
						exclude = true;
						break;
					}
				}

				if (exclude)
                {
					continue;
                }

				try
				{
					Class<?> klass = Class.forName(className);

					response.appendContentString("entity:" +
					    klass.getSimpleName() + "\n");

					NSArray<KVCAttributeInfo> attributes =
						KVCAttributeFinder.attributesForClass(klass, "");

					for (KVCAttributeInfo attr : attributes)
					{
						response.appendContentString("attribute:" +
								attr.name() + "," + attr.type() + "\n");
					}
				}
				catch (ClassNotFoundException e)
				{
                    // ???
				}
			}
		}

		return response;
	}


    // ----------------------------------------------------------
	public WOActionResults objectDescriptionsAction()
	{
		WOResponse response = new WOResponse();

		EOEditingContext ec = Application.newPeerEditingContext();

		for (String entityName : OBJECTS_TO_DESCRIBE)
		{
	    	EOFetchSpecification fetchSpec = new EOFetchSpecification(
	    		entityName, null, null);
	    	fetchSpec.setFetchLimit(250);

	    	NSArray<EOEnterpriseObject> objects =
	    		ec.objectsWithFetchSpecification(fetchSpec);

	    	response.appendContentString("entity:" + entityName + "\n");

	    	for (EOEnterpriseObject object : objects)
	    	{
	    		Number id = (Number)EOUtilities.primaryKeyForObject(
	                ec, object).objectForKey( "id" );

	    		response.appendContentString("object:" + id.toString() + "," +
	    			object.toString() + "\n");
	    	}
		}

		return response;
	}


    // ----------------------------------------------------------
	public WOActionResults subsystemVersionCheckAction()
	{
		WOResponse response = new WOResponse();

		NSDictionary<String, String> versions = subsystemVersions();

		for (String subsystem : versions.allKeys())
		{
			response.appendContentString("version:" + subsystem + "," +
				versions.objectForKey(subsystem) + "\n");
		}

		return response;
	}


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
	private NSDictionary<String, String> subsystemVersions()
	{
		NSMutableDictionary<String, String> subsystemVersions =
			new NSMutableDictionary<String, String>();

		NSArray<Subsystem> subsystems =
			((Application)Application.application()).
			subsystemManager().subsystems();

		for (Subsystem subsystem : subsystems)
		{
			for (String nameToCheck : SUBSYSTEMS_TO_CHECK)
			{
				if (nameToCheck.equals(subsystem.name()))
				{
					subsystemVersions.setObjectForKey(
						subsystem.descriptor().currentVersion(),
						nameToCheck);
					break;
				}
			}
		}

		return subsystemVersions;
	}


    //~ Instance/static variables .............................................

	private static final String[] ENTITIES_TO_EXCLUDE = {
		"CoreSelections", "ERXGenericRecord", "GraderPrefs", "LoginSession",
		"PasswordChangeRequest"
	};

	private static final String[] OBJECTS_TO_DESCRIBE = {
		"Assignment", "AssignmentOffering", "Course", "CourseOffering"
	};

	private static final String[] SUBSYSTEMS_TO_CHECK = {
		"Core", "Grader", "Reporter"
	};
}
