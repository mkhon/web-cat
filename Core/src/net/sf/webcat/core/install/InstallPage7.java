/*==========================================================================*\
 |  $Id: InstallPage7.java,v 1.2 2006/02/25 07:58:07 stedwar2 Exp $
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

package net.sf.webcat.core.install;

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import er.extensions.ERXValueUtilities;
import java.util.Calendar;
import net.sf.webcat.core.*;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * Implements the login UI functionality of the system.
 *
 *  @author Stephen Edwards
 *  @version $Id: InstallPage7.java,v 1.2 2006/02/25 07:58:07 stedwar2 Exp $
 */
public class InstallPage7
    extends InstallPage
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new PreCheckPage object.
     * 
     * @param context The context to use
     */
    public InstallPage7( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public NSArray periods = new NSArray( Semester.integers );
    public Integer period;
    public Integer selectedPeriod;
    public String  empty;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public int stepNo()
    {
        return 7;
    }


    // ----------------------------------------------------------
    public void setDefaultConfigValues( WCConfigurationFile configuration )
    {
        java.util.Calendar now = new java.util.GregorianCalendar();
        int thisYear = now.get( java.util.Calendar.YEAR );
        int thisMonth = now.get( java.util.Calendar.MONTH ) + 1;
        int semester = Semester.defaultSemesterFor( now );
        selectedPeriod = Semester.integers[semester];
        int startMonth = Semester.defaultStartingMonth( semester );
        int startYear = thisYear;
        if ( startMonth > thisMonth ) startYear--;
        int endMonth = Semester.defaultEndingMonth( semester );
        int endYear = thisYear;
        if ( endMonth < thisMonth ) endYear++;
        int startDay = 1;
        int endDay = 30;
        switch ( endMonth )
        {
            case 2: endDay = 28; break;
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12: endDay = 31; break;
        }

        setConfigDefault( configuration, "StartDate",
            ( startMonth < 10 ? "0" : "" ) + startMonth
            + ( startDay < 10 ? "/0" : "/" )  + startDay
            + "/" + startYear );
        setConfigDefault( configuration, "EndDate",
            ( endMonth < 10 ? "0" : "" )  + endMonth
            + ( endDay < 10 ? "/0" : "/" ) + endDay
            + "/" + endYear );
    }


    // ----------------------------------------------------------
    public void takeFormValues( NSDictionary formValues )
    {
        EOEditingContext ec = Application.newPeerEditingContext();
        try
        {
            // SemesterCreate = ("1");
            if ( ERXValueUtilities.booleanValue(
                 extractFormValue( formValues, "SemesterCreate" ) ) )
            {
                Semester semester = new Semester();
                // SemesterPeriod = ("0");
                semester.setSeason(ERXValueUtilities.intValue(
                    extractFormValue( formValues, "SemesterPeriod" ) ) );
                // StartDate = ("1/1/2006");
                java.text.DateFormat df = java.text.DateFormat
                    .getDateInstance( java.text.DateFormat.SHORT );
                try
                {
                    String str = extractFormValue( formValues, "StartDate" );
                    log.debug( "start raw value = " + str );
                    NSTimestamp t = new NSTimestamp(
                        df.parse( str )
                        );
                    log.debug( "start timestamp = " + t );
                    semester.setSemesterStartDate( t );
                    Calendar cal = Calendar.getInstance();
                    cal.setTime( t );
                    semester.setYear( cal.get( Calendar.YEAR ) );
                }
                catch ( java.text.ParseException e )
                {
                    errorMessage( "Invalid format for semester start date." );
                }
                // EndDate = ("5/31/2006");
                try
                {
                    String str = extractFormValue( formValues, "EndDate" );
                    log.debug( "end raw value = " + str );
                    NSTimestamp t = new NSTimestamp(
                        df.parse( str )
                        );
                    log.debug( "end timestamp = " + t );
                    semester.setSemesterEndDate( t );
                }
                catch ( java.text.ParseException e )
                {
                    errorMessage( "Invalid format for semester end date." );
                }
                if ( !hasErrors() )
                {
                    ec.insertObject( semester );
                }
            }

            // SandboxCreate = ("1");
            Department dept = null;
            if ( ERXValueUtilities.booleanValue(
                     extractFormValue( formValues, "SandboxCreate" ) ) )
            {
                dept = new Department();
                ec.insertObject( dept );
                dept.setName( "Sandbox Department" );
                dept.setAbbreviation( "Sandbox" );
                dept.setInstitutionRelationship(
                    AuthenticationDomain.authDomainByName(
                        Application.configurationProperties()
                        .getProperty( "authenticator.default" ) )
                );
                Course course = new Course();
                ec.insertObject( course );
                course.setDepartmentRelationship( dept );
                course.setName( "Sandbox Course" );
                course.setNumber( 1 );
            }

            // DeptName = ("");
            // DeptAbbrev = ("");
            String deptName   = extractFormValue( formValues, "DeptName" );
            String deptAbbrev = extractFormValue( formValues, "DeptAbbrev" );
            if ( deptName != null && !deptName.equals( "" ) )
            {
                if ( deptAbbrev == null || deptAbbrev.equals( "" ) )
                {
                    errorMessage( "Please provide a department abbreviation." );
                }
                else
                {
                    dept = new Department();
                    ec.insertObject( dept );
                    dept.setName( deptName );
                    dept.setAbbreviation( deptAbbrev );
                    dept.setInstitutionRelationship(
                        AuthenticationDomain.authDomainByName(
                            Application.configurationProperties()
                            .getProperty( "authenticator.default" ) )
                    );
                }
            }

            // CourseNo = ("", "", "", "", "");
            // CourseName = ("", "", "", "", "");
            NSArray courseNos = (NSArray)formValues.objectForKey( "CourseNo" );
            NSArray courseNames =
                (NSArray)formValues.objectForKey( "CourseName" );
            if ( dept != null )
            {
                for ( int i = 0; i < courseNos.count(); i++ )
                {
                    String num  = (String)courseNos.objectAtIndex( i );
                    String name = (String)courseNames.objectAtIndex( i );
                    if ( num != null && !num.equals( "" )
                         && name != null && !name.equals( "" ) )
                    {
                        try
                        {
                            int number = Integer.parseInt( num );
                            Course course = new Course();
                            ec.insertObject( course );
                            course.setName( name );
                            course.setNumber( number );
                            course.setDepartmentRelationship( dept );
                        }
                        catch ( NumberFormatException e )
                        {
                            errorMessage( "Course no. \"" + num + "\" cannot "
                                + "be parsed as an integer." );
                        }
                    }
                    else if ( num != null && !num.equals( "" )
                              && ( name == null || name.equals( "" ) ) )
                    {
                        errorMessage(
                            "Course \"" + num + "\" needs a name."  );
                    }
                    else if ( name != null && !name.equals( "" )
                              && ( num == null || num.equals( "" ) ) )
                    {
                        errorMessage(
                            "Course \"" + name + "\" needs a number."  );
                    }
                }
            }
            else
            {
                for ( int i = 0; i < courseNos.count(); i++ )
                {
                    String str = (String)courseNos.objectAtIndex( i );
                    if ( str != null && !str.equals( "" ) )
                    {
                        errorMessage(
                            "Cannot create courses without a department!" );
                        break;
                    }
                    str = (String)courseNames.objectAtIndex( i );
                    if ( str != null && !str.equals( "" ) )
                    {
                        errorMessage(
                            "Cannot create courses without a department!" );
                        break;
                    }
                }
            }

            // commit changes to the database
            ec.saveChanges();
        }
        finally
        {
            Application.releasePeerEditingContext( ec );
        }
        Application.configurationProperties().remove( "StartDate" );
        Application.configurationProperties().remove( "EndDate" );
    }


    // ----------------------------------------------------------
    public String periodName()
    {
        return (String)Semester.names.objectAtIndex( period.intValue() );
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( InstallPage7.class );
}
