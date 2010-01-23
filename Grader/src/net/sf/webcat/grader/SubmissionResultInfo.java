/*==========================================================================*\
 |  $Id: SubmissionResultInfo.java,v 1.5 2010/01/23 03:47:28 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2010 Virginia Tech
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
import net.sf.webcat.core.*;

// -------------------------------------------------------------------------
/**
 *  Renders a descriptive table containing a submission result's basic
 *  identifying information.  An optional submission file stats object,
 *  if present, will be used to present file-specific data.
 *
 *  @author  Stephen Edwards
 *  @author  latest changes by: $Author: stedwar2 $
 *  @version $Revision: 1.5 $, $Date: 2010/01/23 03:47:28 $
 */
public class SubmissionResultInfo
    extends GraderComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Default constructor.
     * @param context The page's context
     */
    public SubmissionResultInfo( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public Submission          submission;
    public SubmissionFileStats submissionFileStats;
    public boolean             showFileInfo     = false;
    public boolean             allowPartnerEdit = false;
    public boolean             includeSeparator = true;
    public WODisplayGroup      partnerDisplayGroup;
    public Submission          partnerSubmission;
    public int                 rowNumber;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void _appendToResponse( WOResponse response, WOContext context )
    {
        rowNumber = 0;
        if ( submission != null )
        {
            partnerDisplayGroup.setMasterObject( submission.result() );
        }
        super._appendToResponse( response, context );
    }


    // ----------------------------------------------------------
    public boolean isAPartner()
    {
        return partnerSubmission.user() != submission.user();
    }


    // ----------------------------------------------------------
    public WOComponent editPartners()
    {
        WOComponent parent = parent();
        if ( parent instanceof GradeStudentSubmissionPage )
        {
            ( (GradeStudentSubmissionPage)parent ).saveGrading();
        }
        EditPartnersPage reportPage = pageWithName(EditPartnersPage.class);
        reportPage.nextPage = (WCComponent)context().page();
        reportPage.result = submission.result();
        return reportPage;
    }
}
