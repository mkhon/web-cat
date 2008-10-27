/*==========================================================================*\
 |  $Id: JobQueueDatabaseUpdates.java,v 1.1 2008/10/27 01:53:16 stedwar2 Exp $
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

package net.sf.webcat.jobqueue;

import net.sf.webcat.dbupdate.Database;
import net.sf.webcat.dbupdate.UpdateSet;
import java.sql.SQLException;
import net.sf.webcat.core.*;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * This class captures the SQL database schema for the database tables
 * underlying the Grader subsystem and the Grader.eomodeld.  Logging output
 * for this class uses its parent class' logger.
 *
 * @author  Stephen Edwards
 * @version $Id: JobQueueDatabaseUpdates.java,v 1.1 2008/10/27 01:53:16 stedwar2 Exp $
 */
public class JobQueueDatabaseUpdates
    extends UpdateSet
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * The default constructor uses the name "grader" as the unique
     * identifier for this subsystem and EOModel.
     */
    public JobQueueDatabaseUpdates()
    {
        super("jobqueue");
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Creates all tables in their baseline configuration, as needed.
     * @throws SQLException on error
     */
    public void updateIncrement0() throws SQLException
    {
        createHostDescriptorTable();
        createQueueDescriptorTable();
        createWorkerDescriptorTable();
    }


    // ----------------------------------------------------------
    /**
     * Create the common columns inherited by all job tables, if needed.
     * This method is intended to be used in other database updaters that
     * own tables containing subclasses of {@link JobBase}.  With
     * horizontal EO inheritance, each such subclass is contained within
     * its own table, and has columns to hold all of its inherited attributes.
     * This method can be used to create those inherited columns in the
     * tables used by subclasses.
     * @param database The database of the updater calling this method,
     *     which typically comes from the caller's {@link #database()} method.
     * @param tableName The name of the table used by the subclass entity.
     * @throws SQLException on error
     */
    public static void createJobBaseTable(Database database, String tableName)
        throws SQLException
    {
        if ( !database.hasTable(tableName) )
        {
            log.info( "creating table " + tableName );
            database.executeSQL(
                "CREATE TABLE " + tableName
                + "(enqueueTime DATETIME NOT NULL, "
                + "OID INTEGER NOT NULL, "
                + "isCancelled BIT NOT NULL, "
                + "isPaused BIT NOT NULL, "
                + "priority INTEGER NOT NULL, "
                + "scheduledTime DATETIME, "
                + "userId INTEGER, "
                + "workerId INTEGER )");
            database.executeSQL(
                "ALTER TABLE " + tableName + " ADD PRIMARY KEY (OID)" );
        }
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    /**
     * Create the THostDescriptor table, if needed.
     * @throws SQLException on error
     */
    private void createHostDescriptorTable() throws SQLException
    {
        if ( !database().hasTable( "THostDescriptor" ) )
        {
            log.info( "creating table THostDescriptor" );
            database().executeSQL(
                "CREATE TABLE THostDescriptor "
                + "(hostName TINYTEXT NOT NULL, "
                + "OID INTEGER NOT NULL)");
            database().executeSQL(
                "ALTER TABLE THostDescriptor ADD PRIMARY KEY (OID)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TQueueDescriptor table, if needed.
     * @throws SQLException on error
     */
    private void createQueueDescriptorTable() throws SQLException
    {
        if ( !database().hasTable( "TQueueDescriptor" ) )
        {
            log.info( "creating table TQueueDescriptor" );
            database().executeSQL(
                "CREATE TABLE TQueueDescriptor "
                + "(defaultJobWait BIGINT, "
                + "OID INTEGER NOT NULL, "
                + "jobCount BIGINT, "
                + "jobEntityName TINYTEXT NOT NULL,"
                + "jobsCountedWithWaits BIGINT, "
                + "mostRecentJobWait BIGINT, "
                + "newestEntryId BIGINT NOT NULL, "
                + "requiresExclusiveHostAccess BIT, "
                + "totalWaitForJobs BIGINT)");
            database().executeSQL(
                "ALTER TABLE TQueueDescriptor ADD PRIMARY KEY (OID)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TWorkerDescriptor table, if needed.
     * @throws SQLException on error
     */
    private void createWorkerDescriptorTable() throws SQLException
    {
        if ( !database().hasTable( "TWorkerDescriptor" ) )
        {
            log.info( "creating table TWorkerDescriptor" );
            database().executeSQL(
                "CREATE TABLE TWorkerDescriptor "
                + "(hostId INTEGER , "
                + "OID INTEGER NOT NULL, "
                + "idOnHost INTEGER, "
                + "isAllocated BIT NOT NULL, "
                + "isRunning BIT NOT NULL, "
                + "queueId INTEGER )" );
            database().executeSQL(
                "ALTER TABLE TWorkerDescriptor ADD PRIMARY KEY (OID)" );
        }
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger(JobQueueDatabaseUpdates.class);
}
