/*==========================================================================*\
 |  $Id: SubmissionResultInfo.java,v 1.2 2007/03/02 02:43:25 stedwar2 Exp $
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
 *  @version $Id: SubmissionResultInfo.java,v 1.2 2007/03/02 02:43:25 stedwar2 Exp $
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
    public void appendToResponse( WOResponse response, WOContext context )
    {
        rowNumber = 0;
        if ( submission != null )
        {
            partnerDisplayGroup.setMasterObject( submission.result() );
        }
        super.appendToResponse( response, context );
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
        WCComponent reportPage =
            (WCComponent)pageWithName( EditPartnersPage.class.getName() );
        reportPage.nextPage = (WCComponent)context().page();
        return reportPage;
    }
}
