/*==========================================================================*\
 |  $Id: DevEventTrackerDatabaseUpdates.java,v 1.3 2015/05/29 03:54:08 jluke13 Exp $
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

import java.sql.SQLException;
import org.webcat.dbupdate.UpdateSet;

//-------------------------------------------------------------------------
/**
 * This class captures the SQL database schema for the database tables
 * underlying the DevEventTracker subsystem and the DevEventTracker.eomodeld.
 * 
 * @author Stephen Edwards
 * @author Last changed by $Author: jluke13 $
 * @version $Revision: 1.3 $, $Date: 2015/05/29 03:54:08 $
 */
public class DevEventTrackerDatabaseUpdates extends UpdateSet {
	// ~ Constructors ..........................................................

	// ----------------------------------------------------------
	/**
	 * The default constructor uses the name "deveventtracker" as the unique
	 * identifier for this subsystem and EOModel.
	 */
	public DevEventTrackerDatabaseUpdates() {
		super("deveventtracker");
	}

	// ~ Public Methods ........................................................

	// ----------------------------------------------------------
	/**
	 * Creates all tables in their baseline configuration, as needed.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	public void updateIncrement0() throws SQLException {
		createProjectForAssignmentTable();
		createProjectForAssignmentStudentTable();
		createSensorDataTable();
		createSensorDataTypeTable();
		createStudentProjectTable();
		createStudentProjectForAssignmentTable();
		createStudentProjectStudentTable();
		createUuidForUserTable();
	}

	/**
	 * Adds column for commit hash in SensorData table.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	public void updateIncrement1() throws SQLException {
		database().executeSQL(
				"alter table SensorData add commitHash MEDIUMTEXT");
	}

	/**
	 * Allows the foreign id userId in table UUIDForUser to be null.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	public void updateIncrement2() throws SQLException {
		database().executeSQL(
				"alter table UuidForUser modify column userId INTEGER NULL");
	}

	/**
	 * Changes the relationship between StudentProjects and Users to be between
	 * StudentProjects and UuidForUsers instead.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	public void updateIncrement3() throws SQLException {
		database().executeSQL("drop table StudentProjectStudent");
		createStudentProjectUuidForUserTable();
	}

	/**
	 * Creates the table to store errors thrown by the plugin, logged, and sent
	 * to the server.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	public void updateIncrement4() throws SQLException {
		createPluginErrorTable();
	}

	// ~ Private Methods .......................................................

	// ----------------------------------------------------------
	/**
	 * Create the ProjectForAssignment table, if needed.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	private void createProjectForAssignmentTable() throws SQLException {
		if (!database().hasTable("ProjectForAssignment")) {
			log.info("creating table ProjectForAssignment");
			database().executeSQL(
					"create table ProjectForAssignment ("
							+ "OID INTEGER NOT NULL, "
							+ "assignmentOfferingId INTEGER, "
							+ "start DATETIME, " + "end DATETIME" + ")");
			database().executeSQL(
					"ALTER TABLE ProjectForAssignment ADD PRIMARY KEY (OID)");
			createIndexFor("ProjectForAssignment", "assignmentOfferingId");
		}
	}

	// ----------------------------------------------------------
	/**
	 * Create the ProjectForAssignmentStudent table, if needed.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	private void createProjectForAssignmentStudentTable() throws SQLException {
		if (!database().hasTable("ProjectForAssignmentStudent")) {
			log.info("creating table ProjectForAssignmentStudent");
			database().executeSQL(
					"create table ProjectForAssignmentStudent ("
							+ "projectId INTEGER NOT NULL, "
							+ "studentId INTEGER NOT NULL" + ")");
			database().executeSQL(
					"ALTER TABLE ProjectForAssignmentStudent ADD PRIMARY KEY "
							+ "(projectId, studentId)");
			createIndexFor("ProjectForAssignmentStudent", "projectId");
			createIndexFor("ProjectForAssignmentStudent", "studentId");
		}
	}

	// ----------------------------------------------------------
	/**
	 * Create the SensorData table, if needed.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	private void createSensorDataTable() throws SQLException {
		if (!database().hasTable("SensorData")) {
			log.info("creating table SensorData");
			database().executeSQL(
					"create table SensorData (" + "OID INTEGER NOT NULL, "
							+ "projectId INTEGER, " + "runTime DATETIME, "
							+ "time DATETIME, " + "tool TINYTEXT, "
							+ "typeId INTEGER, " + "uri MEDIUMTEXT, "
							+ "userId INTEGER" + ")");
			database().executeSQL(
					"ALTER TABLE SensorData ADD PRIMARY KEY (OID)");
			createIndexFor("SensorData", "projectId");
			createIndexFor("SensorData", "typeId");
			createIndexFor("SensorData", "userId");
		}
	}

	// ----------------------------------------------------------
	/**
	 * Create the SensorDataType table, if needed.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	private void createSensorDataTypeTable() throws SQLException {
		if (!database().hasTable("SensorDataType")) {
			log.info("creating table SensorDataType");
			database().executeSQL(
					"create table SensorDataType (" + "OID INTEGER NOT NULL, "
							+ "name TINYTEXT" + ")");
			database().executeSQL(
					"ALTER TABLE SensorDataType ADD PRIMARY KEY (OID)");
		}
	}

	// ----------------------------------------------------------
	/**
	 * Create the StudentProject table, if needed.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	private void createStudentProjectTable() throws SQLException {
		if (!database().hasTable("StudentProject")) {
			log.info("creating table StudentProject");
			database().executeSQL(
					"create table StudentProject (" + "OID INTEGER NOT NULL, "
							+ "uri MEDIUMTEXT, " + "uuid TINYTEXT" + ")");
			database().executeSQL(
					"ALTER TABLE StudentProject ADD PRIMARY KEY (OID)");
			createIndexFor("StudentProject", "uuid");
		}
	}

	// ----------------------------------------------------------
	/**
	 * Create the StudentProjectForAssignment table, if needed.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	private void createStudentProjectForAssignmentTable() throws SQLException {
		if (!database().hasTable("StudentProjectForAssignment")) {
			log.info("creating table StudentProjectForAssignment");
			database().executeSQL(
					"create table StudentProjectForAssignment ("
							+ "studentProjectId INTEGER NOT NULL, "
							+ "projectForAssignmentId INTEGER NOT NULL" + ")");
			database().executeSQL(
					"ALTER TABLE StudentProjectForAssignment ADD PRIMARY KEY "
							+ "(studentProjectId, projectForAssignmentId)");
			createIndexFor("StudentProjectForAssignment", "studentProjectId");
			createIndexFor("StudentProjectForAssignment",
					"projectForAssignmentId");
		}
	}

	// ----------------------------------------------------------
	/**
	 * Create the StudentProjectStudent table, if needed.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	private void createStudentProjectStudentTable() throws SQLException {
		if (!database().hasTable("StudentProjectStudent")) {
			log.info("creating table StudentProjectStudent");
			database().executeSQL(
					"create table StudentProjectStudent ("
							+ "projectId INTEGER NOT NULL, "
							+ "studentId INTEGER NOT NULL" + ")");
			database().executeSQL(
					"ALTER TABLE StudentProjectStudent ADD PRIMARY KEY "
							+ "(projectId, studentId)");
			createIndexFor("StudentProjectStudent", "projectId");
			createIndexFor("StudentProjectStudent", "studentId");
		}
	}

	// ----------------------------------------------------------
	/**
	 * Create the UuidForUser table, if needed.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	private void createUuidForUserTable() throws SQLException {
		if (!database().hasTable("UuidForUser")) {
			log.info("creating table UuidForUser");
			database().executeSQL(
					"create table UuidForUser (" + "OID INTEGER NOT NULL, "
							+ "userId INTEGER NOT NULL, "
							+ "uuid TINYTEXT NOT NULL" + ")");
			database().executeSQL(
					"ALTER TABLE UuidForUser ADD PRIMARY KEY (OID)");
			createIndexFor("UuidForUser", "userId");
			createIndexFor("UuidForUser", "uuid");
		}
	}

	// ----------------------------------------------------------
	/**
	 * Create the StudentProjectUuidForUser table, if needed.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	private void createStudentProjectUuidForUserTable() throws SQLException {
		if (!database().hasTable("StudentProjectUuidForUser")) {
			log.info("creating table StudentProjectUuidForUser");
			database().executeSQL(
					"create table StudentProjectUuidForUser ("
							+ "studentProjectId INTEGER NOT NULL, "
							+ "uuidForUserId INTEGER NOT NULL" + ")");
			database().executeSQL(
					"ALTER TABLE StudentProjectUuidForUser ADD PRIMARY KEY "
							+ "(studentProjectId, uuidForUserId)");
			createIndexFor("StudentProjectUuidForUser", "studentProjectId");
			createIndexFor("StudentProjectUuidForUser", "uuidForUserId");
		}
	}

	// ----------------------------------------------------------
	/**
	 * Create the PluginError table, if needed.
	 * 
	 * @throws SQLException
	 *             on error
	 */
	private void createPluginErrorTable() throws SQLException {
		if(!database().hasTable("PluginError"))
		{
			log.info("creating table PluginError");
			database().executeSQL("create table PluginError ("
					+ "className MEDIUMTEXT, "
					+ "exceptionClass MEDIUMTEXT, "
					+ "exceptionMessage LONGTEXT, "
					+ "fileName MEDIUMTEXT, "
					+ "OID INTEGER NOT NULL, "
					+ "lineNumber INTEGER, "
					+ "methodName MEDIUMTEXT, "
					+ "stackTrace LONGTEXT, "
					+ "studentProjectID INTEGER, "
					+ "uuidForUserID INTEGER" + ")");
			database().executeSQL(
					"ALTER TABLE PluginError ADD PRIMARY KEY (OID)");
			createIndexFor("PluginError", "studentProjectID");
			createIndexFor("PluginError", "uuidForUserID");
		}

	}
}
