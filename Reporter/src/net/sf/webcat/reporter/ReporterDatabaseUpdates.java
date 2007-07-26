package net.sf.webcat.reporter;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import net.sf.webcat.dbupdate.UpdateSet;

public class ReporterDatabaseUpdates extends UpdateSet
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * The default constructor uses the name "grader" as the unique
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
		createReportParameterTable();
		createReportParameterDependsTable();
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
            	+ "(CAUTHORID INTEGER , CTEMPLATENAME TINYTEXT , "
            	+ "CTEMPLATEDESCRIPTION MEDIUMTEXT , OID INTEGER NOT NULL , "
            	+ "CISPUBLISHED BIT NOT NULL , CUPLOADEDFILENAME TINYTEXT , "
            	+ "CLASTMODIFIEDTIME DATETIME )" );
            database().executeSQL(
                "ALTER TABLE TREPORTTEMPLATE ADD PRIMARY KEY (OID)" );
        }
	}

	
    // ----------------------------------------------------------
    /**
     * Create the TREPORTPARAMETER table, if needed.
     * @throws SQLException on error
     */
	private void createReportParameterTable() throws SQLException
	{
        if ( !database().hasTable( "TREPORTPARAMETER" ) )
        {
            log.info( "creating table TREPORTPARAMETER" );

            database().executeSQL(
            	"CREATE TABLE TREPORTPARAMETER "
            	+ "(CTEMPLATEID INTEGER , CBINDING TINYTEXT , "
            	+ "CPARAMETERTYPE TINYTEXT , "
            	+ "CPARAMETERDESCRIPTION MEDIUMTEXT , OID INTEGER NOT NULL , "
            	+ "CDISPLAYNAME TINYTEXT , "
            	+ "COPTIONS BLOB , CUPDATEMUTABLEFIELDS BIT NOT NULL)" );
            database().executeSQL(
                "ALTER TABLE TREPORTPARAMETER ADD PRIMARY KEY (OID)" );
        }
	}


    // ----------------------------------------------------------
    /**
     * Create the TREPORTPARAMETERDEPENDS table, if needed.
     * @throws SQLException on error
     */
	private void createReportParameterDependsTable() throws SQLException
	{
        if ( !database().hasTable( "TREPORTPARAMETERDEPENDS" ) )
        {
            log.info( "creating table TREPORTPARAMETERDEPENDS" );

            database().executeSQL(
            	"CREATE TABLE TREPORTPARAMETERDEPENDS "
            	+ "(CID INTEGER NOT NULL , CDEPENDSONID INTEGER NOT NULL)");
            database().executeSQL(
                "ALTER TABLE TREPORTPARAMETERDEPENDS ADD PRIMARY KEY "
            	+ "(CID , CDEPENDSONID)");
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
            	+ "CTEMPLATEID INTEGER , CPARAMETERSELECTIONS BLOB , "
            	+ "CUPDATEMUTABLEFIELDS BIT NOT NULL , " 
            	+ "CUSERID INTEGER NOT NULL , CUUID MEDIUMTEXT , "
            	+ "CRENDEREDRESOURCEACTIONURL MEDIUMTEXT , "
            	+ "CREPORTNAME MEDIUMTEXT , CRENDERINGMETHOD MEDIUMTEXT )" );
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
            	+ "(OID INTEGER NOT NULL , CREPORTNAME MEDIUMTEXT , "
            	+ "CTEMPLATEID INTEGER NOT NULL , CGENERATEDTIME DATETIME , "
            	+ "CPARAMETERSELECTIONS BLOB , "
            	+ "CUPDATEMUTABLEFIELDS BIT NOT NULL , " 
            	+ "CUSERID INTEGER NOT NULL , CUUID MEDIUMTEXT )" );
            database().executeSQL(
                "ALTER TABLE TGENERATEDREPORT ADD PRIMARY KEY (OID)" );
        }
	}
	

	//~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( UpdateSet.class );
}
