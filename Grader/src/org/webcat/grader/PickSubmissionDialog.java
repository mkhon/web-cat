/*==========================================================================*\
 |  $Id: PickSubmissionDialog.java,v 1.3 2011/01/20 18:44:16 stedwar2 Exp $
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

import org.apache.log4j.Logger;
import org.webcat.core.WCComponent;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import er.extensions.appserver.ERXDisplayGroup;

//-------------------------------------------------------------------------
/**
 * Allows a grader to choose a different submission to grade than the one that
 * is displayed on the StudentsForAssignment page.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.3 $, $Date: 2011/01/20 18:44:16 $
 */
public class PickSubmissionDialog extends GraderComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public PickSubmissionDialog(WOContext context)
    {
        super(context);
    }


    //~ KVC attributes (must be public) .......................................

    public WCComponent                 nextPageForResultsPage;
    public UserSubmissionPair          rootUserSubmission;
    public NSArray<UserSubmissionPair> allUserSubmissionsForNavigation;
    public ERXDisplayGroup<Submission> submissionDisplayGroup;
    public Submission                  aSubmission;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Sets the "root submission" for which this dialog is being invoked, which
     * is the submission that will be used to access all of the other
     * submissions made on this assignment by the user.
     *
     * @param pair the UserSubmissionPair for which the dialog is being invoked
     */
    public void setRootUserSubmission(UserSubmissionPair pair)
    {
        rootUserSubmission = pair;
        collectSubmissions();
    }


    // ----------------------------------------------------------
    private void collectSubmissions()
    {
        if (rootUserSubmission != null
                && rootUserSubmission != lastRootUserSubmission)
        {
            lastRootUserSubmission = rootUserSubmission;

            NSArray<Submission> submissions =
                rootUserSubmission.submission().allSubmissions();
            submissionDisplayGroup.setObjectArray(submissions);

            for (int i = 0; i < submissions.count(); i++)
            {
                if (rootUserSubmission.submission() ==
                        submissions.objectAtIndex(i))
                {
                    submissionDisplayGroup.selectObject(
                            submissions.objectAtIndex(i));
                    break;
                }
            }
        }
    }


    // ----------------------------------------------------------
    public String submitTimeSpanClass()
    {
        if (aSubmission.isLate())
        {
            return "warn";
        }
        else
        {
            return null;
        }
    }


    // ----------------------------------------------------------
    public String submissionStatus()
    {
        String result = "suspended";
        EnqueuedJob job = aSubmission.enqueuedJob();

        if (job == null)
        {
            result = "cancelled";
        }
        else if (!job.paused())
        {
            result = "queued for grading";
        }

        return result;
    }


    // ----------------------------------------------------------
    public WOComponent viewSubmission()
    {
        Submission selectedSub = submissionDisplayGroup.selectedObject();

        if (selectedSub == null)
        {
            selectedSub = rootUserSubmission.submission();
        }

        GradeStudentSubmissionPage page =
            pageWithName(GradeStudentSubmissionPage.class);

        prefs().setSubmissionRelationship(selectedSub);

        if (allUserSubmissionsForNavigation == null)
        {
            page.availableSubmissions = null;
            page.thisSubmissionIndex = 0;
        }
        else
        {
            page.availableSubmissions =
                allUserSubmissionsForNavigation.immutableClone();
            page.thisSubmissionIndex =
                page.availableSubmissions.indexOf(rootUserSubmission);
        }
        page.nextPage = nextPageForResultsPage;

        page.reloadGraderPrefs();

        return page;
    }


    //~ Static/instance variables .............................................

    private UserSubmissionPair lastRootUserSubmission;

    static final Logger log = Logger.getLogger(PickSubmissionDialog.class);
}
