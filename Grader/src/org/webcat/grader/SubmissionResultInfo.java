/*==========================================================================*\
 |  $Id: SubmissionResultInfo.java,v 1.2 2010/09/14 18:24:24 aallowat Exp $
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

package org.webcat.grader;

import org.webcat.core.Course;
import org.webcat.core.CourseOffering;
import org.webcat.core.User;
import org.webcat.ui.generators.JavascriptGenerator;
import org.webcat.ui.util.ComponentIDGenerator;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import er.extensions.eof.ERXQ;
import er.extensions.foundation.ERXArrayUtilities;

// -------------------------------------------------------------------------
/**
 *  Renders a descriptive table containing a submission result's basic
 *  identifying information.  An optional submission file stats object,
 *  if present, will be used to present file-specific data.
 *
 *  @author  Stephen Edwards
 *  @author  latest changes by: $Author: aallowat $
 *  @version $Revision: 1.2 $, $Date: 2010/09/14 18:24:24 $
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
    public User                aPartner;
    public int                 rowNumber;
    public NSArray<User>       originalPartners;
    public NSArray<User>       partnersForEditing;

    public ComponentIDGenerator idFor;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    protected void beforeAppendToResponse(
        WOResponse response, WOContext context)
    {
        idFor = new ComponentIDGenerator(this);

        rowNumber = 0;

        if (submission != null)
        {
            NSMutableArray<User> partners = new NSMutableArray<User>();

            for (Submission partneredSubmission :
                submission.partneredSubmissions())
            {
                partners.addObject(partneredSubmission.user());
            }

            originalPartners = partners;
            partnersForEditing = partners.mutableClone();
        }

        super.beforeAppendToResponse( response, context );
    }


    // ----------------------------------------------------------
    public String showEditPartnersDialogScript()
    {
        return "dijit.byId('" + idFor.get("editPartnersDialog") + "').show();";
    }


    // ----------------------------------------------------------
    public JavascriptGenerator partnersChanged()
    {
        NSArray<User> partnersToRemove = ERXArrayUtilities.arrayMinusArray(
                originalPartners, partnersForEditing);
        submission.unpartnerFromUsers(partnersToRemove, localContext());

        NSArray<User> partnersToAdd = ERXArrayUtilities.arrayMinusArray(
                partnersForEditing, originalPartners);
        submission.partnerWithUsers(partnersToAdd, localContext());

        applyLocalChanges();

        originalPartners = partnersForEditing.mutableClone();

        JavascriptGenerator script = new JavascriptGenerator();
        script.refresh(idFor.get("partnersPane")).
               dijit(idFor.get("editPartnersDialog")).call("hide");

        return script;
    }


    // ----------------------------------------------------------
    public EOQualifier qualifierForStudentsInCourse()
    {
        Course course =
            submission.assignmentOffering().courseOffering().course();
        NSArray<CourseOffering> offerings = course.offerings();

        EOQualifier[] enrollmentQuals = new EOQualifier[offerings.count()];
        int i = 0;
        for (CourseOffering offering : offerings)
        {
            enrollmentQuals[i++] = User.enrolledIn.is(offering);
        }

        return ERXQ.or(enrollmentQuals);
    }
}
