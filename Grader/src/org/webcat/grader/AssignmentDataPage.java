/*==========================================================================*\
 |  $Id: AssignmentDataPage.java,v 1.1 2010/05/11 14:51:40 aallowat Exp $
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

package org.webcat.grader;

import com.Ostermiller.util.*;
import com.ibm.icu.text.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.io.*;
import org.apache.log4j.Logger;
import org.webcat.core.*;

// -------------------------------------------------------------------------
/**
 * Generates a downloadable CSV file of assignment scheduling and grading
 * data.
 *
 *  @author edwards
 *  @version $Id: AssignmentDataPage.java,v 1.1 2010/05/11 14:51:40 aallowat Exp $
 */
public class AssignmentDataPage
    extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     *
     * @param context The context to use
     */
    public AssignmentDataPage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public int              index;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Get the long response task that computes this page's work.
     * @return The task
     */
    public LongResponseTaskWithProgress longResponse()
    {
        if ( task == null )
        {
            task = new LongResponseTask( wcSession().timeFormatter() );
        }
        return task;
    }


    // ----------------------------------------------------------
    public WOComponent downloadFile()
    {
        DeliverFile csvFile = pageWithName(DeliverFile.class);
        csvFile.setFileData( new NSData( (byte[])longResponse().result() ) );
        csvFile.setFileName( new File( "assignment-data.csv" ) );
        csvFile.setContentType( "application/octet-stream" );
        csvFile.setStartDownload( true );
        return csvFile;
    }


    //~ Long response task ....................................................

    // ----------------------------------------------------------
    /**
     * Encapsulates all the work needed to generate data for this page's
     * response.  The return value produced by performAction() is an
     * array of {@link Pair}s.
     */
    public static class LongResponseTask
        extends InterpolatingLongResponseTask
    {

        // ----------------------------------------------------------
        public LongResponseTask( NSTimestampFormatter formatter )
        {
            // Create a local EC, transfer the result into it, and
            // store both locally
            ec = Application.newPeerEditingContext();
            try
            {
                ec.lock();
                assignments = EOUtilities.objectsForEntityNamed(
                    ec, AssignmentOffering.ENTITY_NAME );
            }
            finally
            {
                ec.unlock();
            }
            this.formatter = formatter;
        }


        // ----------------------------------------------------------
        protected Object setUpTask()
        {
            setUnweightedNumberOfSteps( 1 );
            if ( assignments != null && assignments.count() > 0 )
            {
                setUnweightedNumberOfSteps( assignments.count() );
            }
            outBytes = new ByteArrayOutputStream( 4096 );
            out = new ExcelCSVPrinter( outBytes );
            return null;
        }


        // ----------------------------------------------------------
        protected Object tearDownTask( Object resultSoFar )
        {
            return outBytes == null ? null : outBytes.toByteArray();
        }


        // ----------------------------------------------------------
        protected Object nextStep( int stepNumber, Object resultSoFar )
        {
            if ( stepNumber == 0 )
            {
                // Print the header row first
                out.print( "institution" );
                out.print( "semester" );
                out.print( "course" );
                out.print( "crn" );
                out.print( "assignment" );
                out.print( "moodleId" );
                out.print( "dueDateTimestamp" );
                out.print( "due.date" );
                out.print( "published" );
                out.print( "submissions" );
                out.print( "max.score.ta" );
                out.print( "max.score.tools" );  // pts static analysis
                out.print( "max.score.correctness" );  // pts corr./testing
                out.print( "max.score" );  // pts total
                out.print( "max.submissions" );  // max submissions
                out.print( "max.upload.size" );  // max upload size
                out.print( "start.accepting" );  // start accepting
                out.print( "stop.accepting" );  // stop accepting
                out.print( "bonus.use" );  // use bonus
                out.print( "bonus.limit" );  // bonus limit
                out.print( "bonus.increment" );  // bonus increment
                out.print( "bonus.time.unit" );  // bonus time unit
                out.print( "penalty.use" );  // use penalty
                out.print( "penalty.limit" );  // penalty limit
                out.print( "penalty.increment" );  // penalty increment
                out.print( "penalty.time.unit" );  // penalty time unit
                out.print( "external.submitter" );
                out.println();
            }
            if ( assignments != null && stepNumber < assignments.count() )
            {
                AssignmentOffering ao = (AssignmentOffering)assignments
                    .objectAtIndex( stepNumber );
                if ( ao.courseOffering() != null )
                {
                    out.print(
                        ( ao.courseOffering().course() != null
                          && ao.courseOffering().course().department() != null
                          && ao.courseOffering().course().department()
                             .institution() != null )
                            ? ao.courseOffering().course().department()
                                 .institution().name()
                            : "" );
                    out.print( ao.courseOffering().semester().name() );
                    out.print( ao.courseOffering().course().deptNumber() );
                    out.print( ao.courseOffering().crn() );
                    if ( ao.assignment() != null )
                    {
                        out.print( ao.assignment().name() );
                        {
                            Number moodleId = ao.moodleId();
                            if ( moodleId == null )
                            {
                                out.print( "" );
                            }
                            else
                            {
                                out.print( moodleId.toString() );
                            }
                        }
                        if ( ao.dueDate() == null )
                        {
                            out.print( "" );
                            out.print( "" );
                        }
                        else
                        {
                            // due date as timestamp
                            out.print(
                                Long.toString( ao.dueDate().getTime() ) );
                            // due date as a string
                            out.print( formatter.format( ao.dueDate()));
                        }
                        // published
                        out.print( ao.publish() ? "1" : "0" );
                        // number of submissions
                        out.print(
                            Integer.toString( ao.submissions().count() ) );
                        if ( ao.assignment().submissionProfile() != null )
                        {
                            // pts ta
                            outPrintObject( ao.assignment().submissionProfile()
                                .taPointsRaw() );
                            // pts static analysis
                            outPrintObject( ao.assignment().submissionProfile()
                                .toolPointsRaw() );
                            // pts correctness/testing
                            out.print( Double.toString( ao.assignment()
                                .submissionProfile().correctnessPoints() ) );
                            // pts total
                            outPrintObject( ao.assignment().submissionProfile()
                                .availablePointsRaw() );
                            // max submissions
                            outPrintObject( ao.assignment().submissionProfile()
                                .maxSubmissionsRaw() );
                            // max upload size
                            outPrintObject( ao.assignment().submissionProfile()
                                .maxFileUploadSizeRaw() );
                            // start accepting
                            outPrintObject( ao.assignment().submissionProfile()
                                .availableTimeDeltaRaw() );
                            // stop accepting
                            outPrintObject( ao.assignment().submissionProfile()
                                .deadTimeDeltaRaw() );
                            // use bonus
                            out.print( ao.assignment().submissionProfile()
                                .awardEarlyBonus() ? "1" : "0" );
                            // bonus limit
                            outPrintObject( ao.assignment().submissionProfile()
                                .earlyBonusMaxPtsRaw() );
                            // bonus increment
                            outPrintObject( ao.assignment().submissionProfile()
                                .earlyBonusUnitPtsRaw() );
                            // bonus time unit
                            outPrintObject( ao.assignment().submissionProfile()
                                .earlyBonusUnitTimeRaw() );
                            // use penalty
                            out.print( ao.assignment().submissionProfile()
                                .deductLatePenalty() ? "1" : "0" );
                            // penalty limit
                            outPrintObject( ao.assignment().submissionProfile()
                                .latePenaltyMaxPtsRaw() );
                            // penalty increment
                            outPrintObject( ao.assignment().submissionProfile()
                                .latePenaltyUnitPtsRaw() );
                            // penalty time unit
                            outPrintObject( ao.assignment().submissionProfile()
                                .latePenaltyUnitTimeRaw() );
                            // submitter
                            outPrintObject( ao.assignment().submissionProfile()
                                .submissionMethodRaw() );
                        }
                        else
                        {
                            out.print(
                                "Error: no submission rules for assignment" );
                        }
                    }
                    else
                    {
                        out.print(
                            "Error: no assignment for assignment offering" );
                    }
                }
                else
                {
                    out.print( "Error: no course offering for assignment" );
                }
                out.println();
            }
            return resultSoFar;
        }


        // ----------------------------------------------------------
        private void outPrintObject( Object value )
        {
            out.print( value == null ? "" : value.toString() );
        }


        // ----------------------------------------------------------
        public void resultNoLongerNeeded()
        {
            if ( ec != null )
            {
                Application.releasePeerEditingContext( ec );
            }
            ec = null;
        }


        //~ Instance/static variables .........................................
        private EOEditingContext ec;
        private NSArray          assignments;
        ByteArrayOutputStream    outBytes;
        ExcelCSVPrinter          out;
        NSTimestampFormatter     formatter;
    }


    //~ Instance/static variables .............................................

    private LongResponseTaskWithProgress task;
    static Logger log = Logger.getLogger( AssignmentDataPage.class );
}
