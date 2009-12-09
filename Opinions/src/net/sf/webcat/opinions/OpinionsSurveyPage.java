/*==========================================================================*\
 |  $Id: OpinionsSurveyPage.java,v 1.3 2009/12/09 03:15:16 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2009 Virginia Tech
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

package net.sf.webcat.opinions;

import com.webobjects.appserver.*;
import com.webobjects.foundation.NSTimestamp;
import net.sf.webcat.core.*;
import net.sf.webcat.grader.AssignmentOffering;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * This page presents the user with the feedback/opinions survey for
 * the given assignment.
 *
 * @author Stephen Edwards
 * @author Last changed by $Author: stedwar2 $
 * @version $Revision: 1.3 $, $Date: 2009/12/09 03:15:16 $
 */
public class OpinionsSurveyPage
    extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     *
     * @param context The context to use
     */
    public OpinionsSurveyPage(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    public AssignmentOffering assignmentOffering;
    public SurveyResponse response;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse pageResponse, WOContext context)
    {
        if (response == null)
        {
            response = SurveyResponse.create(localContext());
            response.setAssignmentOfferingRelationship(assignmentOffering);
            response.setUserRelationship(user());
        }
        super.appendToResponse(pageResponse, context);
    }


    // ----------------------------------------------------------
    public WOComponent next()
    {
        response.setSubmitTime(new NSTimestamp());
        // Since we're in a SaveCancelPage, the call to super.next()
        // will save all changes.
        return super.next();
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger(OpinionsSurveyPage.class);
}
