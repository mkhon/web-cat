package net.sf.webcat.reporter;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import net.sf.webcat.dbupdate.UpdateSet;

public class ReporterDatabaseUpdates extends UpdateSet
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * The default constructor uses the name "reporter" as the unique
     * identifier for this subsystem and EOModel.
     */
    public ReporterDatabaseUpdates()
    {
        super("reporter");
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Creates all tables in their baseline configuration, as needed.
     * @throws SQLException on error
     */
	@Override
	public void updateIncrement0() throws SQLException
	{
		createReportTemplateTable();
		createReportDataSetTable();
		createReportQueryTable();
		createReportDataSetQueryTable();
		createEnqueuedReportJobTable();
		createGeneratedReportTable();
	}


	//~ Private Methods .......................................................

    // ----------------------------------------------------------
    /**
     * Create the TREPORTTEMPLATE table, if needed.
     * @throws SQLException on error
     */
	private void createReportTemplateTable() throws SQLException
	{
        if ( !database().hasTable( "TREPORTTEMPLATE" ) )
        {
            log.info( "creating table TREPORTTEMPLATE" );

            database().executeSQL(
            	"CREATE TABLE TREPORTTEMPLATE "
            	+ "(OID INTEGER NOT NULL , CAUTHORID INTEGER , "
            	+ "CNAME TINYTEXT NOT NULL , CDESCRIPTION MEDIUMTEXT , "
            	+ "CISPUBLISHED BIT NOT NULL , CUPLOADEDFILENAME TINYTEXT , "
            	+ "CLASTMODIFIEDTIME DATETIME , CVERSION INTEGER )" );
            database().executeSQL(
                "ALTER TABLE TREPORTTEMPLATE ADD PRIMARY KEY (OID)" );
        }
	}

	
    // ----------------------------------------------------------
    /**
     * Create the TREPORTDATASET table, if needed.
     * @throws SQLException on error
     */
	private void createReportDataSetTable() throws SQLException
	{
        if ( !database().hasTable( "TREPORTDATASET" ) )
        {
            log.info( "creating table TREPORTDATASET" );

            database().executeSQL(
            	"CREATE TABLE TREPORTDATASET "
            	+ "(OID INTEGER NOT NULL , CREPORTTEMPLATEID INTEGER NOT NULL , "
            	+ "CENTITYNAME TINYTEXT NOT NULL , CUUID TINYTEXT NOT NULL , "
            	+ "CDESCRIPTION MEDIUMTEXT , CREFERENCECOUNT INTEGER )" );
            database().executeSQL(
                "ALTER TABLE TREPORTDATASET ADD PRIMARY KEY (OID)" );
        }
	}


    // ----------------------------------------------------------
    /**
     * Create the TREPORTQUERY table, if needed.
     * @throws SQLException on error
     */
	private void createReportQueryTable() throws SQLException
	{
        if ( !database().hasTable( "TREPORTQUERY" ) )
        {
            log.info( "creating table TREPORTQUERY" );

            database().executeSQL(
            	"CREATE TABLE TREPORTQUERY "
            	+ "(OID INTEGER NOT NULL , CUSERID INTEGER NOT NULL ,"
            	+ "CENTITYNAME TINYTEXT NOT NULL , CDESCRIPTION MEDIUMTEXT , "
            	+ "CQUALIFIERS BLOB , CUPDATEMUTABLEFIELDS BIT NOT NULL)");
            database().executeSQL(
                "ALTER TABLE TREPORTQUERY ADD PRIMARY KEY (OID)");
        }
	}
	
	
    // ----------------------------------------------------------
    /**
     * Create the TREPORTDATASETQUERY table, if needed.
     * @throws SQLException on error
     */
	private void createReportDataSetQueryTable() throws SQLException
	{
        if ( !database().hasTable( "TREPORTDATASETQUERY" ) )
        {
            log.info( "creating table TREPORTDATASETQUERY" );

            database().executeSQL(
            	"CREATE TABLE TREPORTDATASETQUERY "
            	+ "(OID INTEGER NOT NULL , CENQUEUEDREPORTJOBID INTEGER ,"
            	+ "CGENERATEDREPORTID INTEGER , CDATASETID INTEGER NOT NULL , "
            	+ "CREPORTQUERYID INTEGER NOT NULL)");
            database().executeSQL(
                "ALTER TABLE TREPORTDATASETQUERY ADD PRIMARY KEY (OID)");
        }
	}
	
	// ----------------------------------------------------------
    /**
     * Create the TENQUEUEDREPORTJOB table, if needed.
     * @throws SQLException on error
     */
	private void createEnqueuedReportJobTable() throws SQLException
	{
        if ( !database().hasTable( "TENQUEUEDREPORTJOB" ) )
        {
            log.info( "creating table TENQUEUEDREPORTJOB" );

            database().executeSQL(
            	"CREATE TABLE TENQUEUEDREPORTJOB "
            	+ "(OID INTEGER NOT NULL , CPAUSED BIT NOT NULL , "
            	+ "CDISCARDED BIT NOT NULL , CQUEUETIME DATETIME , "
            	+ "CREPORTTEMPLATEID INTEGER , CDESCRIPTION MEDIUMTEXT , "
            	+ "CUSERID INTEGER NOT NULL , CUUID TINYTEXT , "
            	+ "CRENDEREDRESOURCEACTIONURL MEDIUMTEXT , "
            	+ "CRENDERINGMETHOD TINYTEXT )" );
            database().executeSQL(
                "ALTER TABLE TENQUEUEDREPORTJOB ADD PRIMARY KEY (OID)" );
        }
	}
	

    // ----------------------------------------------------------
    /**
     * Create the TGENERATEDREPORT table, if needed.
     * @throws SQLException on error
     */
	private void createGeneratedReportTable() throws SQLException
	{
        if ( !database().hasTable( "TGENERATEDREPORT" ) )
        {
            log.info( "creating table TGENERATEDREPORT" );

            database().executeSQL(
            	"CREATE TABLE TGENERATEDREPORT "
            	+ "(OID INTEGER NOT NULL , CDESCRIPTION MEDIUMTEXT , "
            	+ "CREPORTTEMPLATEID INTEGER NOT NULL , CGENERATEDTIME DATETIME , "
            	+ "CUSERID INTEGER NOT NULL , CUUID MEDIUMTEXT )" );
            database().executeSQL(
                "ALTER TABLE TGENERATEDREPORT ADD PRIMARY KEY (OID)" );
        }
	}
	

	//~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( UpdateSet.class );
}
