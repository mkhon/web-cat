/*==========================================================================*\
 |  $Id: CoreDatabaseUpdates.java,v 1.9 2007/07/08 14:32:30 stedwar2 Exp $
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

package net.sf.webcat.core;

import net.sf.webcat.dbupdate.UpdateSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * This class captures the SQL database schema for the database tables
 * underlying the Core subsystem and the Core.eomodeld.  Logging output
 * for this class uses its parent class' logger.
 *
 * @author  Stephen Edwards
 * @version $Id: CoreDatabaseUpdates.java,v 1.9 2007/07/08 14:32:30 stedwar2 Exp $
 */
public class CoreDatabaseUpdates
    extends UpdateSet
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * The default constructor uses the name "core" as the unique identifier
     * for this subsystem and EOModel.
     */
    public CoreDatabaseUpdates()
    {
        super( "core" );
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Creates all tables in their baseline configuration, as needed.
     * @throws SQLException on error
     */
    public void updateIncrement0() throws SQLException
    {
        createEoPkTable();
        createAuthenticationDomainTable();
        createCoreSelectionsTable();
        createCourseTable();
        createCourseOfferingTable();
        createCourseStudentTable();
        createCourseTATable();
        createDepartmentTable();
        createInstructorCourseTable();
        createLanguageTable();
        createLoginSessionTable();
        createSemesterTable();
        createUserTable();
    }


    // ----------------------------------------------------------
    /**
     * This performs some simple column value maintenance to repair a
     * bug in an earlier Web-CAT version.  It resets all the
     * updateMutableFields columns to all zeroes.
     * @throws SQLException on error
     */
    public void updateIncrement1() throws SQLException
    {
        database().executeSQL(
            "UPDATE TUSER SET CUPDATEMUTABLEFIELDS = 0" );
    }


    // ----------------------------------------------------------
    /**
     * Changes course CRNs to strings.
     * @throws SQLException on error
     */
    public void updateIncrement2() throws SQLException
    {
        database().executeSQL(
            "alter table TCOURSEOFFERING change CCRN CCRN TINYTEXT" );
    }


    // ----------------------------------------------------------
    /**
     * Changes course CRNs to strings.
     * @throws SQLException on error
     */
    public void updateIncrement3() throws SQLException
    {
        database().executeSQL(
            "alter table TDEPARTMENT add CINSTITUTIONID INTEGER" );
    }


    // ----------------------------------------------------------
    /**
     * Changes login session ids to text.
     * @throws SQLException on error
     */
    public void updateIncrement4() throws SQLException
    {
        database().executeSQL(
            "alter table TLOGINSESSION change CSESSIONID CSESSIONID TINYTEXT" );
    }


    // ----------------------------------------------------------
    /**
     * Adds timeFormat and dateFormat keys to AuthenticationDomain.
     * @throws SQLException on error
     */
    public void updateIncrement5() throws SQLException
    {
        database().executeSQL(
            "alter table TAUTHENTICATIONDOMAIN add CTIMEFORMAT TINYTEXT" );
        database().executeSQL(
            "alter table TAUTHENTICATIONDOMAIN add CDATEFORMAT TINYTEXT" );
    }


    // ----------------------------------------------------------
    /**
     * Adds timeZoneName keys to AuthenticationDomain.
     * @throws SQLException on error
     */
    public void updateIncrement6() throws SQLException
    {
        database().executeSQL(
            "alter table TAUTHENTICATIONDOMAIN "
            + "change CTINYTEXT CDEFAULTURLPATTERN TINYTEXT" );
    }


    // ----------------------------------------------------------
    /**
     * Adds LoggedError and PasswordChangeRequest tables.
     * @throws SQLException on error
     */
    public void updateIncrement7() throws SQLException
    {
        createLoggedErrorTable();
        createPasswordChangeRequestTable();
    }


    // ----------------------------------------------------------
    /**
     * Adds label key to CourseOffering.
     * @throws SQLException on error
     */
    public void updateIncrement8() throws SQLException
    {
        database().executeSQL(
            "alter table TCOURSEOFFERING add CLABEL TINYTEXT" );
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    /**
     * Create the EO_PK_TABLE table, if needed.
     * @throws SQLException on error
     */
    private void createEoPkTable() throws SQLException
    {
        if ( !database().hasTable( "EO_PK_TABLE", "PK", "1" ) )
        {
            log.info( "creating table EO_PK_TABLE" );
            database().executeSQL(
                "CREATE TABLE EO_PK_TABLE "
                + "(NAME CHAR(40) PRIMARY KEY, PK INT)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TAUTHENTICATIONDOMAIN table, if needed.
     * @throws SQLException on error
     */
    private void createAuthenticationDomainTable() throws SQLException
    {
        if ( !database().hasTable( "TAUTHENTICATIONDOMAIN" ) )
        {
            log.info( "creating table TAUTHENTICATIONDOMAIN" );
            database().executeSQL(
                "CREATE TABLE TAUTHENTICATIONDOMAIN "
                + "(CDEFAULTEMAILDOMAIN TINYTEXT , CTINYTEXT TINYTEXT , "
                + "CDISPLAYABLENAME TINYTEXT , OID INTEGER NOT NULL, "
                + "CPROPERTYNAME TINYTEXT , CTIMEZONENAME TINYTEXT )" );
            database().executeSQL(
                "ALTER TABLE TAUTHENTICATIONDOMAIN ADD PRIMARY KEY (OID)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TCORESELECTIONS table, if needed.
     * @throws SQLException on error
     */
    private void createCoreSelectionsTable() throws SQLException
    {
        if ( !database().hasTable( "TCORESELECTIONS" ) )
        {
            log.info( "creating table TCORESELECTIONS" );
            database().executeSQL(
                "CREATE TABLE TCORESELECTIONS "
                + "(CCOURSEID INTEGER , CCOURSEOFFERINGID INTEGER , "
                + "OID INTEGER NOT NULL, CUSERID INTEGER )" );
            database().executeSQL(
                "ALTER TABLE TCORESELECTIONS ADD PRIMARY KEY (OID)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TCOURSE table, if needed.
     * @throws SQLException on error
     */
    private void createCourseTable() throws SQLException
    {
        if ( !database().hasTable( "TCOURSE" ) )
        {
            log.info( "creating table TCOURSE" );
            database().executeSQL(
                "CREATE TABLE TCOURSE "
                + "(CDEPARTMENTID INTEGER , OID INTEGER NOT NULL, "
                + "CNAME TINYTEXT NOT NULL, CNUMBER SMALLINT NOT NULL)" );
            database().executeSQL(
                "ALTER TABLE TCOURSE ADD PRIMARY KEY (OID)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TCOURSEOFFERING table, if needed.
     * @throws SQLException on error
     */
    private void createCourseOfferingTable() throws SQLException
    {
        if ( !database().hasTable( "TCOURSEOFFERING" ) )
        {
            log.info( "creating table TCOURSEOFFERING" );
            database().executeSQL(
                "CREATE TABLE TCOURSEOFFERING "
                + "(CCOURSEID INTEGER , CCRN INTEGER , "
                + "OID INTEGER NOT NULL, CMOODLEGROUPID INTEGER , "
                + "CMOODLEID INTEGER , CSEMESTER INTEGER , URL TINYTEXT )" );
            database().executeSQL(
                "ALTER TABLE TCOURSEOFFERING ADD PRIMARY KEY (OID)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TCOURSESTUDENT table, if needed.
     * @throws SQLException on error
     */
    private void createCourseStudentTable() throws SQLException
    {
        if ( !database().hasTable( "TCOURSESTUDENT", "CID", "1" ) )
        {
            log.info( "creating table TCOURSESTUDENT" );
            database().executeSQL(
                "CREATE TABLE TCOURSESTUDENT "
                + "(CID INT NOT NULL, CID1 INT NOT NULL)" );
            database().executeSQL(
                "ALTER TABLE TCOURSESTUDENT ADD PRIMARY KEY (CID, CID1)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TCOURSETA table, if needed.
     * @throws SQLException on error
     */
    private void createCourseTATable() throws SQLException
    {
        if ( !database().hasTable( "TCOURSETA", "CID", "1" ) )
        {
            log.info( "creating table TCOURSETA" );
            database().executeSQL(
                "CREATE TABLE TCOURSETA "
                + "(CID INT NOT NULL, CID1 INT NOT NULL)" );
            database().executeSQL(
                "ALTER TABLE TCOURSETA ADD PRIMARY KEY (CID, CID1)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TDEPARTMENT table, if needed.
     * @throws SQLException on error
     */
    private void createDepartmentTable() throws SQLException
    {
        if ( !database().hasTable( "TDEPARTMENT" ) )
        {
            log.info( "creating table TDEPARTMENT" );
            database().executeSQL(
                "CREATE TABLE TDEPARTMENT "
                + "(CABBREVIATION TINYTEXT NOT NULL, OID INTEGER NOT NULL, "
                + "CNAME TINYTEXT )" );
            database().executeSQL(
                "ALTER TABLE TDEPARTMENT ADD PRIMARY KEY (OID)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TINSTRUCTORCOURSE table, if needed.
     * @throws SQLException on error
     */
    private void createInstructorCourseTable() throws SQLException
    {
        if ( !database().hasTable( "TINSTRUCTORCOURSE", "CID", "1" ) )
        {
            log.info( "creating table TINSTRUCTORCOURSE" );
            database().executeSQL(
                "CREATE TABLE TINSTRUCTORCOURSE "
                + "(CID INT NOT NULL, CID1 INT NOT NULL)" );
            database().executeSQL(
                "ALTER TABLE TINSTRUCTORCOURSE ADD PRIMARY KEY (CID, CID1)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TLANGUAGE table, if needed.
     * @throws SQLException on error
     */
    private void createLanguageTable() throws SQLException
    {
        if ( !database().hasTable( "TLANGUAGE" ) )
        {
            log.info( "creating table TLANGUAGE" );
            database().executeSQL(
                "CREATE TABLE TLANGUAGE "
                + "(CCOMPILER TINYTEXT , OID INTEGER NOT NULL, "
                + "CNAME TINYTEXT , CVERSION TINYTEXT )" );
            database().executeSQL(
                "ALTER TABLE TLANGUAGE ADD PRIMARY KEY (OID)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TLOGINSESSION table, if needed.
     * @throws SQLException on error
     */
    private void createLoginSessionTable() throws SQLException
    {
        if ( !database().hasTable( "TLOGINSESSION" ) )
        {
            log.info( "creating table TLOGINSESSION" );
            database().executeSQL(
                "CREATE TABLE TLOGINSESSION "
                + "(CEXPIRETIME DATETIME , OID INTEGER NOT NULL, "
                + "CSESSIONID TINYBLOB , CUSERID INTEGER )" );
            database().executeSQL(
                "ALTER TABLE TLOGINSESSION ADD PRIMARY KEY (OID)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TSEMESTER table, if needed.
     * @throws SQLException on error
     */
    private void createSemesterTable() throws SQLException
    {
        if ( !database().hasTable( "TSEMESTER" ) )
        {
            log.info( "creating table TSEMESTER" );
            database().executeSQL(
                "CREATE TABLE TSEMESTER "
                + "(OID INTEGER NOT NULL, CSEASON TINYINT , "
                + "CSEMESTERENDDATE DATE , CSEMESTERSTARTDATE DATE , "
                + "CYEAR SMALLINT NOT NULL)" );
            database().executeSQL(
                "ALTER TABLE TSEMESTER ADD PRIMARY KEY (OID)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the TUSER table, if needed.
     * @throws SQLException on error
     */
    private void createUserTable() throws SQLException
    {
        if ( !database().hasTable( "TUSER" ) )
        {
            log.info( "creating table TUSER" );
            database().executeSQL(
                "CREATE TABLE TUSER "
                + "(CACCESSLEVEL TINYINT NOT NULL, "
                + "CAUTHENTICATIONDOMAINID INTEGER NOT NULL, "
                + "CEMAIL TINYTEXT , CFIRSTNAME TINYTEXT , "
                + "OID INTEGER NOT NULL, CLASTNAME TINYTEXT , "
                + "PASSWORD TINYTEXT , CPREFERENCES BLOB , "
                + "CUNIVERSITYIDNO TINYTEXT , "
                + "CUPDATEMUTABLEFIELDS BIT NOT NULL, URL TINYTEXT , "
                + "CUSERNAME TINYTEXT NOT NULL)" );
            database().executeSQL(
                "ALTER TABLE TUSER ADD PRIMARY KEY (OID)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the LoggedError table, if needed.
     * @throws SQLException on error
     */
    private void createLoggedErrorTable() throws SQLException
    {
        if ( !database().hasTable( "LoggedError" ) )
        {
            log.info( "creating table LoggedError" );
            database().executeSQL(
                "CREATE TABLE LoggedError "
                + "(component TINYTEXT , exceptionName TINYTEXT , "
                + "OID INTEGER NOT NULL, inClass TINYTEXT , "
                + "inMethod TINYTEXT , line INTEGER NOT NULL, "
                + "message TINYTEXT , mostRecent DATETIME , "
                + "occurrences INTEGER NOT NULL, page TINYTEXT , "
                + "stackTrace MEDIUMTEXT)" );
            database().executeSQL(
                "ALTER TABLE LoggedError ADD PRIMARY KEY (OID)" );
        }
    }


    // ----------------------------------------------------------
    /**
     * Create the PasswordChangeRequest table, if needed.
     * @throws SQLException on error
     */
    private void createPasswordChangeRequestTable() throws SQLException
    {
        if ( !database().hasTable( "PasswordChangeRequest" ) )
        {
            log.info( "creating table PasswordChangeRequest" );
            database().executeSQL(
                "CREATE TABLE PasswordChangeRequest "
                + "(code TINYTEXT , expireTime DATETIME , "
                + "OID INTEGER NOT NULL, userId INTEGER NOT NULL)" );
            database().executeSQL(
                "ALTER TABLE PasswordChangeRequest ADD PRIMARY KEY (OID)" );
        }
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( UpdateSet.class );
}
