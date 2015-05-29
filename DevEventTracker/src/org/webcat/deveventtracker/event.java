/*==========================================================================*\
 |  $Id: event.java,v 1.6 2015/05/29 03:54:08 jluke13 Exp $
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
import org.webcat.core.Course;
import org.webcat.core.User;
import org.webcat.grader.Assignment;
import org.webcat.grader.AssignmentOffering;
import org.webcat.grader.Submission;
import org.webcat.core.git.*;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.appserver.ERXDirectAction;

//-------------------------------------------------------------------------
/**
 * This direct action class handles all response actions for this subsystem.
 * 
 * @author edwards
 * @author Last changed by $Author: jluke13 $
 * @version $Revision: 1.6 $, $Date: 2015/05/29 03:54:08 $
 */
public class event extends ERXDirectAction {

	// ~ Constructor ...........................................................

	// ----------------------------------------------------------
	/**
	 * Creates a new DirectAction object.
	 * 
	 * @param aRequest
	 *            The request to respond to
	 */
	public event(WORequest aRequest) {
		super(aRequest);
	}

	// ~ Methods ...............................................................

	/**
	 * The default action simply returns an invalid request response.
	 * 
	 * @return The session response
	 */
	public WOActionResults defaultAction() {
		return pageWithName(SimpleMessageResponse.class);
	}

	/**
	 * Returns a user's UUID if one exists for the given email. If the user
	 * exists, but does not have a UUID, create one. If no user exists, inform
	 * the requester.
	 * 
	 * @return The page containing the UUID of the user or a message stating
	 *         that no such user exists.
	 */
	public WOActionResults retrieveUserAction() {
		EOEditingContext ec = session().defaultEditingContext();
		WORequest request = request();

		// Get parameters.
		String email = request.stringFormValueForKey("email");

		// If we weren't given an email, we create a UUIDForUser with no
		// associated user,
		// then fill the user in later (via confirmUUID action).
		if (email == null || email.equals("")) {
			UuidForUser noUserUuid = UuidForUser.create(ec, UUID.randomUUID().toString());
			
			RetrieveUserResponse page = pageWithName(RetrieveUserResponse.class);
			page.uuid = noUserUuid.uuid();
			return page;
		}

		// TODO: Fix this (API change the lookupUserByEMail method)
		// Incorrect signature (confirmed by Edwards)
		AuthenticationDomain domain = AuthenticationDomain.forId(ec, 1);
		User user = User.lookupUserByEmail(ec, email, domain);

		// No base User found, so we can't have or make a UuidForUser.
		if (user == null) {
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "No user found for that email.";
			return page;
		}

		UuidForUser uuidForUser = UuidForUser.uniqueObjectMatchingQualifier(ec,
				UuidForUser.user.is(user));

		// This user doesn't have a UUID, so we need to make one.
		if (uuidForUser == null) {
			uuidForUser = UuidForUser.create(ec, UUID.randomUUID().toString());
			uuidForUser.setUserRelationship(user);
			ec.saveChanges();
		}

		// Return the page listing the String representation of this user's
		// UUID.
		RetrieveUserResponse page = pageWithName(RetrieveUserResponse.class);
		page.uuid = uuidForUser.uuid();

		return page;
	}

	/**
	 * Given a projectUri and a userUUID, return the UUID of the StudentProject
	 * associated with these, creating one if necessary.
	 * 
	 * @return The page containing the UUID for the StudentProject, or a message
	 *         indicating no such StudentProject exists.
	 */
	public WOActionResults retrieveStudentProjectAction() {
		EOEditingContext ec = session().defaultEditingContext();
		WORequest request = request();

		// Get parameters.
		String projectUri = request.stringFormValueForKey("projectUri");
		String userUuid = request.stringFormValueForKey("userUuid");

		// Look up UuidForUser from userUUID, then use this to look up User
		UuidForUser uuidForUser = UuidForUser.uniqueObjectMatchingQualifier(ec,
				UuidForUser.uuid.is(userUuid));

		// No base UuidForUser found, so we can't use it to make a new
		// StudentProject.
		if (uuidForUser == null) {
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "No UuidForUser found for that UUID.";
			return page;
		}

		StudentProject studentProject = StudentProject
				.uniqueObjectMatchingQualifier(
						ec,
						StudentProject.uri.is(projectUri).and(
								StudentProject.studentUuids.is(uuidForUser)));

		// No StudentProject that matches both the projectUri and the userUuid
		if (studentProject == null) {
			studentProject = StudentProject.create(ec);
			studentProject.setUri(projectUri);
			studentProject.setUuid(UUID.randomUUID().toString());
			studentProject.addToStudentUuidsRelationship(uuidForUser);

			ec.saveChanges();

			// Create a new repository for this student project.
			GitRepository.repositoryForObject(studentProject);
		}
		RetrieveStudentProjectResponse page = pageWithName(RetrieveStudentProjectResponse.class);
		page.uuid = studentProject.uuid();
		return page;
	}

	private String normalizeName(String name) {
		String normalizedName = "";
		for (char c : name.toCharArray()) {
			if ((Character.isLetter(c) && normalizedName.equals(""))
					|| Character.isDigit(c)) {
				normalizedName += c;
			}
		}
		return normalizedName;
	}

	/**
	 * Stores the given SensorData information, assuming all parameters are
	 * valid.
	 * 
	 * @return The page stating success or failure for storing the SensorData.
	 */
	public WOActionResults postSensorDataAction() {
		EOEditingContext ec = session().defaultEditingContext();
		WORequest request = request();

		// Get parameters.
		String studentProjectUuid = request
				.stringFormValueForKey("studentProjectUuid");
		String userUuid = request.stringFormValueForKey("userUuid");
		String timeString = request.stringFormValueForKey("time");
		String runtimeString = request.stringFormValueForKey("runtime");
		String tool = request.stringFormValueForKey("tool");
		String sensorDataTypeName = request
				.stringFormValueForKey("sensorDataType");
		String uri = request.stringFormValueForKey("uri");
		String commitHash = request.stringFormValueForKey("CommitHash");

		NSTimestamp time = new NSTimestamp(Long.parseLong(timeString));
		NSTimestamp runtime = new NSTimestamp(Long.parseLong(runtimeString));

		// Look up UuidForUser from userUUID, then use this to look up User
		UuidForUser uuidForUser = UuidForUser.uniqueObjectMatchingQualifier(ec,
				UuidForUser.uuid.is(userUuid));

		// No base UuidForUser found, so we can't use it to make a new
		// StudentProject.
		if (uuidForUser == null) {
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "NNo UuidForUser found for that UUID.";
			return page;
		}

		User user = uuidForUser.user();
		if (user == null) {
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "No user found for that UUID.";
			return page;
		}
		
		// Look up StudentProject from the given UUID.
		StudentProject studentProject = StudentProject
				.uniqueObjectMatchingQualifier(ec,
						StudentProject.uuid.is(studentProjectUuid));

		// No StudentProject found for that UUID.
		if (studentProject == null) {
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "No student project found for that UUID";
			return page;
		}

		// Look up dataType based on name from given parameter.
		SensorDataType dataType = SensorDataType.getSensorDataType(ec,
				sensorDataTypeName);

		// No SensorDataType found for that name.
		if (dataType == null) {
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "Invalid SensorDataType";
			return page;
		}

		// create sensordata entry in database and populate from given data
		SensorData sensorData = SensorData.create(ec, time, dataType, user);
		sensorData.setProjectRelationship(studentProject);
		sensorData.setRunTime(runtime);
		sensorData.setTool(tool);
		sensorData.setUri(uri);
		if (commitHash != null) {
			sensorData.setCommitHash(commitHash);
		}
		ec.saveChanges();

		return pageWithName(PostSensorDataResponse.class);
	}

	/**
	 * Given that a submission happened for the given user for the given project,
	 * look up the most recent submission for that user in the db and
	 * create a ProjectForAssignment linking this StudentProject to the AssignmentOffering
	 * (unless there already is a PFA doing this).
	 * @return The page indicating success or failure for creating a new ProjectForAssignment.
	 */
	public WOActionResults submissionHappenedAction() {
		EOEditingContext ec = session().defaultEditingContext();
		WORequest request = request();

		// Get parameters.
		String userUuid = request.stringFormValueForKey("userUuid");
		String studentProjectUuid = request
				.stringFormValueForKey("studentProjectUuid");

		UuidForUser uuidForUser = UuidForUser.uniqueObjectMatchingQualifier(ec,
				UuidForUser.uuid.is(userUuid));
		if (uuidForUser == null)
		{
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "No user found for that UUID.";
			return page;
		}
		User user = uuidForUser.user();

		Submission latestSubmission = Submission.firstObjectMatchingQualifier(
				ec, Submission.user.is(user), Submission.submitTime.descs());
		if (latestSubmission == null)
		{
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "No submissions found for that user.";
			return page;
		}
		AssignmentOffering offering = latestSubmission.assignmentOffering();

		StudentProject project = StudentProject.uniqueObjectMatchingQualifier(
				ec, StudentProject.uuid.is(studentProjectUuid));
		if (project == null)
		{
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "No StudentProject found for that UUID.";
			return page;
		}
		// If there is a PFA associated with this StudentProject that matches the
		// given User and AssignmentOffering, we don't need to create one.
		NSArray<ProjectForAssignment> pfas = project.projectsForAssignment();
		for (ProjectForAssignment p : pfas)
		{
			if(p.students().contains(user) && p.assignmentOffering().equals(offering))
			{
				SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
				page.message = "StudentProject already part of the correct ProjectForAssignment.";
				return page;
			}
		}
		// Otherwise, create a new PFA.
		ProjectForAssignment pfa = ProjectForAssignment.create(ec, offering);
		pfa.addToStudentsRelationship(user);
		pfa.addToStudentProjectsRelationship(project);
		pfa.setStart(offering.availableFrom());
		pfa.setEnd(offering.lateDeadline());

		ec.saveChanges();
		SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
		page.message = "ProjectForAssignment created successfully.";
		return page;
	}

	/**
	 * Given an email and a user UUID, checks to see if the given UUID corresponds
	 * to a null user, and reassociates it with the User from the email if so.
	 * This rematches all StudentProjects associated with the old user UUID with
	 * the new one if necessary.
	 * @return The page indicating success or failure for the confirmation.
	 */
	public WOActionResults confirmUuidAction() {
		EOEditingContext ec = session().defaultEditingContext();
		WORequest request = request();

		// Get parameters.
		String email = request.stringFormValueForKey("email");
		String userUuid = request.stringFormValueForKey("userUuid");

		UuidForUser oldUuidForUser = UuidForUser.uniqueObjectMatchingQualifier(
				ec, UuidForUser.uuid.is(userUuid));
		if (oldUuidForUser == null) {
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "No UuidForUser found for that UUID.";
			return page;
		}
		if (oldUuidForUser.user() == null) {
			User user = User.uniqueObjectMatchingQualifier(ec,
					User.email.is(email));
			if (user == null) {
				SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
				page.message = "No user found for that email.";
				return page;
			}
			UuidForUser uuidForConfirmedUser = UuidForUser
					.uniqueObjectMatchingQualifier(ec,
							UuidForUser.user.is(user));
			// If there is another UUID for this user that has been used, we
			// need to pull all student projects to that UuidForUser, and
			// delete the old UUID (which had no user associated with it).
			if (uuidForConfirmedUser != null
					&& !uuidForConfirmedUser.equals(oldUuidForUser)) {
				NSArray<StudentProject> projects = StudentProject
						.objectsMatchingQualifier(ec,
								StudentProject.studentUuids.is(oldUuidForUser));
				for (StudentProject p : projects)
				{
					p.removeFromStudentUuidsRelationship(oldUuidForUser);
					oldUuidForUser.delete();
					p.addToStudentUuidsRelationship(uuidForConfirmedUser);
				}
				ec.saveChanges();

				RetrieveUserResponse page = pageWithName(RetrieveUserResponse.class);
				page.uuid = uuidForConfirmedUser.uuid();
				return page;
			}
			// If the user with the given email does not have an associated UUID, we 
			// need to create one and return it.
			else if(uuidForConfirmedUser == null)
			{
				UuidForUser newUuidForUser = UuidForUser.create(ec, UUID.randomUUID().toString());
				newUuidForUser.setUserRelationship(user);
				ec.saveChanges();

				RetrieveUserResponse page = pageWithName(RetrieveUserResponse.class);
				page.uuid = newUuidForUser.uuid();
				return page;
			}
			// We should never get here, as it's the same case as the else below.
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "This UUID is already associated with a user.";
			return page;
		} else {
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "This UUID is already associated with a user.";
			return page;
		}
	}

	public WOActionResults projectDownloadAction() {
		EOEditingContext ec = session().defaultEditingContext();
		WORequest request = request();
		
		// Get parameters
		String projectUri = request.stringFormValueForKey("projectUri");
		String userUuid = request.stringFormValueForKey("userUuid");
		String assignmentName = request.stringFormValueForKey("assignmentName");
		String courseName = request.stringFormValueForKey("courseName");
		
		UuidForUser uuidForUser = UuidForUser.uniqueObjectMatchingQualifier(ec, UuidForUser.uuid.is(userUuid));
		if (uuidForUser == null) {
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "No UuidForUser found for that Uuid.";
			return page;
		}
		User user = uuidForUser.user();
		if (user == null) {
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "No user found for that UUID.";
			return page;
		}
		
		Assignment assignment = Assignment.uniqueObjectMatchingQualifier(ec, Assignment.name.is(assignmentName));
		if(assignment == null)
		{
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "No Assignment found for that name.";
			return page;
		}
		AssignmentOffering offering = assignment.offeringForUser(user);
		
		if(offering == null)
		{
			SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
			page.message = "No AssignmentOffering found for that user and assignment name.";
			return page;
		}
		
		StudentProject studentProject = StudentProject.uniqueObjectMatchingQualifier(ec, StudentProject.studentUuids.is(uuidForUser).and(StudentProject.uri.is(projectUri)));
		if(studentProject == null)
		{
			studentProject = StudentProject.create(ec);
			studentProject.setUri(projectUri);
			studentProject.setUuid(UUID.randomUUID().toString());
			studentProject.addToStudentUuidsRelationship(uuidForUser);
		}
		else
		{
			NSArray<ProjectForAssignment> pfas = studentProject.projectsForAssignment();
			for (ProjectForAssignment p : pfas)
			{
				if(p.students().contains(user) && p.assignmentOffering().equals(offering))
				{
					SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
					page.message = "StudentProject already part of the correct ProjectForAssignment.";
					return page;
				}
			}
		}

		// Otherwise, create a new PFA.
		ProjectForAssignment pfa = ProjectForAssignment.create(ec, offering);
		pfa.addToStudentsRelationship(user);
		pfa.addToStudentProjectsRelationship(studentProject);
		pfa.setStart(offering.availableFrom());
		pfa.setEnd(offering.lateDeadline());
		
		ec.saveChanges();
		SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
		page.message = "Starter project stored in database.";
		return page;
	}
	
	public WOActionResults pluginExceptionHappenedAction() {
		EOEditingContext ec = session().defaultEditingContext();
		WORequest request = request();
		
		//Get parameters
		String userUuid = request.stringFormValueForKey("userUuid");
		String exceptionClass = request.stringFormValueForKey("exceptionClass");
		String exceptionMessage = request.stringFormValueForKey("exceptionMessage");
		String className = request.stringFormValueForKey("className");
		String methodName = request.stringFormValueForKey("methodName");
		String fileName = request.stringFormValueForKey("fileName");
		String lineNumber = request.stringFormValueForKey("lineNumber");
		String stackTrace = request.stringFormValueForKey("stackTrace");
		
		UuidForUser uuidForUser = UuidForUser.uniqueObjectMatchingQualifier(ec, UuidForUser.uuid.is(userUuid));
		
		PluginError error = PluginError.create(ec);
		error.setExceptionClass(exceptionClass);
		error.setExceptionMessage(exceptionMessage);
		error.setClassName(className);
		error.setMethodName(methodName);
		error.setFileName(fileName);
		error.setLineNumber(Integer.parseInt(lineNumber));
		error.setStackTrace(stackTrace);
		error.setUuidForUserRelationship(uuidForUser);
		
		ec.saveChanges();
		SimpleMessageResponse page = pageWithName(SimpleMessageResponse.class);
		page.message = "PluginError stored in database.";
		return page;
	}
}
