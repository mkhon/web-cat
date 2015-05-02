/*==========================================================================*\
 |  $Id: event.java,v 1.2 2015/05/02 01:16:54 jluke13 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2014 Virginia Tech
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


package org.webcat.deveventtracker;

import java.util.UUID;

import org.webcat.core.AuthenticationDomain;
import org.webcat.core.User;
import org.webcat.grader.AssignmentOffering;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.appserver.ERXDirectAction;

//-------------------------------------------------------------------------
/**
 * This direct action class handles all response actions for this
 * subsystem.
 *
 * @author  edwards
 * @author  Last changed by $Author: jluke13 $
 * @version $Revision: 1.2 $, $Date: 2015/05/02 01:16:54 $
 */
public class event
    extends ERXDirectAction
{
	
	private static NSTimestamp lastCheckedForSubmissions;
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new DirectAction object.
     *
     * @param aRequest The request to respond to
     */
    public event(WORequest aRequest)
    {
        super(aRequest);
    }


    //~ Methods ...............................................................
    
    
    /**
     * The default action simply returns an invalid request response.
     * @return The session response
     */
    public WOActionResults defaultAction()
    {
        return pageWithName(SimpleMessageResponse.class);
    }
    
    /**
     * Returns a user's UUID if one exists for the given email.
     * If the user exists, but does not have a UUID, create one.
     * If no user exists, inform the requester.
     * @return The page containing the UUID of the user or a message stating that no such user exists.
     */
    public WOActionResults retrieveUserAction()
    {
    	EOEditingContext ec = session().defaultEditingContext();
    	WORequest request = request();
    	
    	//Get parameters.
    	String email = request.stringFormValueForKey("email");
    	
    	//TODO: Fix this (API change the lookupUserByEMail method)
    	AuthenticationDomain domain = AuthenticationDomain.forId(ec, 1);
    	User user = User.lookupUserByEmail(ec, email, domain);
    	
    	//No base User found, so we can't have or make a UuidForUser.
    	if (user == null)
    	{
    		SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
    		page.message = "No user found for that email.";
    		return page;
    	}
    	
    	UuidForUser uuidForUser = UuidForUser.uniqueObjectMatchingQualifier(ec, UuidForUser.user.is(user));
    	
    	//This user doesn't have a UUID, so we need to make one.
    	if (uuidForUser == null)
    	{
    		uuidForUser = UuidForUser.create(ec, UUID.randomUUID().toString(), user);
    		ec.saveChanges();
    	}
    	
    	//Return the page listing the String representation of this user's UUID.
    	RetrieveUserResponse page = pageWithName(RetrieveUserResponse.class);
    	page.uuid = uuidForUser.uuid();
    	
    	return page;
    }
    
    /**
     * Given a projectUri and a userUUID, return the UUID of the StudentProject associated with these, creating one if necessary.
     * @return The page containing the UUID for the StudentProject, or a message indicating no such StudentProject exists.
     */
    public WOActionResults retrieveStudentProjectAction()
    {
    	EOEditingContext ec = session().defaultEditingContext();
    	WORequest request = request();
    	
    	//Get parameters.
    	String projectUri = request.stringFormValueForKey("projectUri");
    	String userUuid = request.stringFormValueForKey("userUuid");
    	
    	//Look up UuidForUser from userUUID, then use this to look up User
    	UuidForUser uuidForUser = UuidForUser.uniqueObjectMatchingQualifier(ec, UuidForUser.uuid.is(userUuid));
    	
    	//No base UuidForUser found, so we can't use it to make a new StudentProject.
    	if (uuidForUser == null)
    	{
    		SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
    		page.message = "No user found for that UUID.";
    		return page;
    	}
    	
    	User user = uuidForUser.user();
    	
    	StudentProject studentProject  = StudentProject.uniqueObjectMatchingQualifier(ec, StudentProject.uri.is(projectUri).and(StudentProject.students.is(user)));    	 
    	
    	//No StudentProject that matches both the projectUri and the userUuid
    	if(studentProject == null)
    	{
    		studentProject = StudentProject.create(ec);
    		studentProject.setUri(projectUri);
    		studentProject.setUuid(UUID.randomUUID().toString());
    		NSMutableArray<User> newStudents = new NSMutableArray<User>();
    		newStudents.add(user);
    		studentProject.setStudents(newStudents);
    		
        	//TODO: Look and see if there is a ProjectForAssignment that matches this StudentProject and create
    		//the relationship appropriately if there is.
    		//How to match URIs?
    		
    		//is there a PFA that has the same user and this uri matches the assignment #/name
    		
    		//All ProjectsForAssignments associated with this user
    		NSArray<ProjectForAssignment> potentialProjects = ProjectForAssignment.objectsMatchingQualifier(ec, ProjectForAssignment.students.is(user));
    		
    		for(ProjectForAssignment p : potentialProjects)
    		{
    			if(normalizeName(p.assignmentOffering().assignment().name()).equals(normalizeName(projectUri)))
    			{
    				p.addToStudentProjectsRelationship(studentProject);
    				break;
    			}
    		}
    		

    		ec.saveChanges();
    	}
    	RetrieveStudentProjectResponse page = pageWithName(RetrieveStudentProjectResponse.class);
    	page.uuid = studentProject.uuid();
    	return page;
    }

    private String normalizeName(String name)
    {
    	String normalizedName = "";
    	for(char c : name.toCharArray())
    	{
    		if((Character.isLetter(c) && normalizedName.equals("")) || Character.isDigit(c))
    		{
    			normalizedName += c;
    		}
    	}
    	return normalizedName;
    }
    
    /**
     * Stores the given SensorData information, assuming all parameters are valid.
     * @return The page stating success or failure for storing the SensorData.
     */
    public WOActionResults postSensorDataAction()
    {
    	EOEditingContext ec = session().defaultEditingContext();
    	WORequest request = request();
    	
    	//Get parameters.
    	String studentProjectUuid = request.stringFormValueForKey("studentProjectUuid");
    	String userUuid = request.stringFormValueForKey("userUuid");
    	String timeString = request.stringFormValueForKey("time");
    	String runtimeString = request.stringFormValueForKey("runtime");
    	String tool = request.stringFormValueForKey("tool");
    	String sensorDataTypeName = request.stringFormValueForKey("sensorDataType");
    	String uri = request.stringFormValueForKey("uri");
    	
    	NSTimestamp time = new NSTimestamp(Long.parseLong(timeString));
    	NSTimestamp runtime = new NSTimestamp(Long.parseLong(runtimeString));
    	
    	//Look up UuidForUser from userUUID, then use this to look up User
    	UuidForUser uuidForUser = UuidForUser.uniqueObjectMatchingQualifier(ec, UuidForUser.uuid.is(userUuid));
    	
    	//No base UuidForUser found, so we can't use it to make a new StudentProject.
    	if (uuidForUser == null)
    	{
    		SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
    		page.message = "No user found for that UUID.";
    		return page;
    	}
    	
    	User user = uuidForUser.user();
    	
    	//Look up StudentProject from the given UUID.
    	StudentProject studentProject = StudentProject.uniqueObjectMatchingQualifier(ec, StudentProject.uuid.is(studentProjectUuid));
    	
    	//No StudentProject found for that UUID.
    	if (studentProject == null)
    	{
    		SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
    		page.message = "No student project found for that UUID";
    		return page;
    	}
    	
    	//Look up dataType based on name from given parameter.
    	SensorDataType dataType = SensorDataType.uniqueObjectMatchingQualifier(ec, SensorDataType.name.is(sensorDataTypeName));
    	
    	//No SensorDataType found for that name.
    	if (dataType == null)
    	{
    		SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
    		page.message = "Invalid SensorDataType";
    		return page;
    	}
    	
    	//create sensordata entry in database and populate from given data
    	SensorData sensorData = SensorData.create(ec, time, dataType, user);
    	sensorData.setProjectRelationship(studentProject);
    	sensorData.setRunTime(runtime);
    	sensorData.setTool(tool);
    	sensorData.setUri(uri);
    	ec.saveChanges();
    	
    	return pageWithName(PostSensorDataResponse.class);
    }
    
    /**
     * Creates a new ProjectForAssignment.
     * @param ec The editingcontext to use
     * @param start The start time of the ProjectForAssignment
     * @param end The end time of the ProjectForAssignment
     * @param assignmentOffering The assignmentOffering to link to
     */
    private void createProjectForAssignmentAction(EOEditingContext ec, NSTimestamp start, NSTimestamp end, AssignmentOffering assignmentOffering)
    {
    	
    	//NSTimestamp start = new NSTimestamp(Long.parseLong(startString));
    	//NSTimestamp end = new NSTimestamp(Long.parseLong(endString));
    	
    	ProjectForAssignment projectForAssignment = ProjectForAssignment.create(ec, assignmentOffering);
    	projectForAssignment.setStart(start);
    	projectForAssignment.setEnd(end);
    	
    }
    
    public WOActionResults submissionHappenedAction()
    {
    	//Check all submissions since lastCheckedForSubmissions.
    	//Match to existing ProjectsForAssignment, create new where necessary.
    	
    	return null;
    }
}
