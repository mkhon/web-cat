package net.sf.webcat.reporter.actions;

import net.sf.webcat.core.Application;
import net.sf.webcat.core.Subsystem;
import net.sf.webcat.reporter.queryassistants.KVCAttributeFinder;
import net.sf.webcat.reporter.queryassistants.KVCAttributeInfo;

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

public class contentAssist extends ERXDirectAction
{
	public contentAssist(WORequest request)
	{
		super(request);
	}

	public WOActionResults entityDescriptionsAction()
	{
		WOResponse response = new WOResponse();

		NSDictionary<String, String> versions = subsystemVersions();

		for(String subsystem : versions.allKeys())
		{
			response.appendContentString("version:" + subsystem + "," +
					versions.objectForKey(subsystem) + "\n");
		}

		for(EOModel model :
			(NSArray<EOModel>)EOModelGroup.defaultGroup().models())
		{
			for(EOEntity entity : (NSArray<EOEntity>)model.entities())
			{
				String className = entity.className();
				boolean exclude = false;

				for(String toExclude : ENTITIES_TO_EXCLUDE)
				{
					if(toExclude.equals(className))
					{
						exclude = true;
						break;
					}
				}

				if(exclude)
					continue;

				try
				{
					Class<?> klass = Class.forName(className);

					response.appendContentString("entity:" +
							klass.getSimpleName() + "\n");

					NSArray<KVCAttributeInfo> attributes =
						KVCAttributeFinder.attributesForClass(klass, "");

					for(KVCAttributeInfo attr : attributes)
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

	public WOActionResults objectDescriptionsAction()
	{
		WOResponse response = new WOResponse();

		EOEditingContext ec = Application.newPeerEditingContext();

		for(String entityName : OBJECTS_TO_DESCRIBE)
		{
	    	EOFetchSpecification fetchSpec = new EOFetchSpecification(
	    			entityName, null, null);
	    	fetchSpec.setFetchLimit(250);

	    	NSArray<EOEnterpriseObject> objects =
	    		ec.objectsWithFetchSpecification(fetchSpec);

	    	response.appendContentString("entity:" + entityName + "\n");

	    	for(EOEnterpriseObject object : objects)
	    	{
	    		Number id = (Number)EOUtilities.primaryKeyForObject(
	                    ec, object).objectForKey( "id" );

	    		response.appendContentString("object:" + id.toString() + "," +
	    				object.toString() + "\n");
	    	}
		}

		return response;
	}

	public WOActionResults subsystemVersionCheckAction()
	{
		WOResponse response = new WOResponse();

		NSDictionary<String, String> versions = subsystemVersions();

		for(String subsystem : versions.allKeys())
		{
			response.appendContentString("version:" + subsystem + "," +
					versions.objectForKey(subsystem) + "\n");
		}

		return response;
	}

	private NSDictionary<String, String> subsystemVersions()
	{
		NSMutableDictionary<String, String> subsystemVersions =
			new NSMutableDictionary<String, String>();

		NSArray<Subsystem> subsystems =
			((Application)Application.application()).
			subsystemManager().subsystems();

		for(Subsystem subsystem : subsystems)
		{
			for(String nameToCheck : SUBSYSTEMS_TO_CHECK)
			{
				if(nameToCheck.equals(subsystem.name()))
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
