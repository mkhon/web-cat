/*==========================================================================*\
 |  $Id: PickEnrolledStudentPage.java,v 1.7 2008/04/11 23:06:05 stedwar2 Exp $
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

package net.sf.webcat.grader;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;
import net.sf.webcat.core.*;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * Allow the user to select an enrolled student from the current course.
 *
 * @author Stephen Edwards
 * @version $Id: PickEnrolledStudentPage.java,v 1.7 2008/04/11 23:06:05 stedwar2 Exp $
 */
public class PickEnrolledStudentPage
    extends GraderAssignmentComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Default constructor.
     * @param context The page's context
     */
    public PickEnrolledStudentPage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public WODisplayGroup      studentDisplayGroup;
    public User                student;
    public int                 studentIndex;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse( WOResponse response, WOContext context )
    {
        studentDisplayGroup.setMasterObject(
            coreSelections().courseOffering() );
        super.appendToResponse( response, context );
    }


    // ----------------------------------------------------------
    public User localUser()
    {
        return user();
    }


    // ----------------------------------------------------------
    public void cancelLocalChanges()
    {
        resetPrimeUser();
        super.cancelLocalChanges();
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( PickEnrolledStudentPage.class );
}