/*==========================================================================*\
 |  $Id: EditAssignmentPage.java,v 1.7 2010/11/01 17:04:08 aallowat Exp $
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

import com.ibm.icu.text.MessageFormat;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import er.extensions.appserver.ERXDisplayGroup;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.webcat.core.*;
import org.webcat.ui.generators.JavascriptGenerator;

// -------------------------------------------------------------------------
/**
 *  This class presents an assignment's properties so they can be edited.
 *
 *  @author  Stephen Edwards
 *  @author  Last changed by $Author: aallowat $
 *  @version $Revision: 1.7 $, $Date: 2010/11/01 17:04:08 $
 */
public class EditAssignmentPage
    extends GraderAssignmentsComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * This is the default constructor
     *
     * @param context The page's context
     */
    public EditAssignmentPage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public ERXDisplayGroup<Step>               scriptDisplayGroup;
    public int                                 index;
    public Step                                thisStep;
    public ERXDisplayGroup<SubmissionProfile>  submissionProfileDisplayGroup;
    public SubmissionProfile                   submissionProfile;
    public AssignmentOffering                  upcomingOffering;
    public AssignmentOffering                  thisOffering;
    public Assignment                          assignment;
    public AssignmentOffering                  selectedOffering;
    public ERXDisplayGroup<AssignmentOffering> offeringGroup;

    public NSArray<GradingPlugin> gradingPluginsToAdd;
    public GradingPlugin gradingPluginToAdd;
    public GradingPlugin selectedGradingPluginToAdd;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    protected void beforeAppendToResponse(
        WOResponse response, WOContext context)
    {
        timeStart = System.currentTimeMillis();

        log.debug("starting appendToResponse()");
        currentTime = new NSTimestamp();

        // Get all the available grading plugins.
        gradingPluginsToAdd = GradingPlugin.pluginsAvailableToUser(
                localContext(), user());

        offeringGroup.setObjectArray(assignmentOfferings(courseOfferings()));
        if (selectedOffering == null)
        {
            if (offeringGroup.displayedObjects().count() > 0)
            {
                selectedOffering = offeringGroup.displayedObjects()
                    .objectAtIndex(0);
            }
            else
            {
                selectedOffering = prefs().assignmentOffering();
            }
        }
        if (assignment == null)
        {
            assignment = prefs().assignment();
            if (assignment == null && selectedOffering != null)
            {
                assignment = selectedOffering.assignment();
            }
        }
        scriptDisplayGroup.setMasterObject(assignment);
        // TODO: Fix NPEs on this page when no selectedOffering
        if (selectedOffering != null)
        {
            submissionProfileDisplayGroup.setObjectArray(
                SubmissionProfile.profilesForCourseIncludingMine(
                    localContext(),
                    user(),
                    selectedOffering.courseOffering().course(),
                    assignment.submissionProfile() )
                 );
        }
        log.debug("starting super.appendToResponse()");
        super.beforeAppendToResponse(response, context);
    }


    // ----------------------------------------------------------
    protected void afterAppendToResponse(WOResponse response, WOContext context)
    {
        super.afterAppendToResponse(response, context);
        log.debug( "finishing super.appendToResponse()" );
        log.debug( "finishing appendToResponse()" );

        long timeTaken = System.currentTimeMillis() - timeStart;
        log.debug("Time in appendToResponse(): " + timeTaken + " ms");
    }


    // ----------------------------------------------------------
    public boolean validateURL( String assignUrl )
    {
        boolean result = ( assignUrl == null );
        if ( assignUrl != null )
        {
            try
            {
                // Try to create an instance of URL, which will
                // throw an exception if the name isn't valid
                result = ( new URL( assignUrl ) != null );
            }
            catch ( MalformedURLException e )
            {
                error( "The specified URL is not valid." );
                log.error( "Error in validateURL()", e );
            }
        }
        log.debug( "url validation = " + result );
        return result;
    }


    // ----------------------------------------------------------
    public boolean allowsAllOfferingsForCourse()
    {
        return true;
    }


    // ----------------------------------------------------------
    public boolean requiresAssignmentOffering()
    {
        // Want to show all offerings for this assignment.
        return false;
    }


    // ----------------------------------------------------------
    public void flushNavigatorDerivedData()
    {
        selectedOffering = null;
        assignment = null;
    }


    // ----------------------------------------------------------
    /* Checks for errors, then records the current selections.
     *
     * @returns false if there is an error message to display.
     */
    protected boolean saveAndCanProceed()
    {
        return saveAndCanProceed( true );
    }


    // ----------------------------------------------------------
    /* Checks for errors, then records the current selections.
     *
     * @param requireProfile if true, a missing submission profile will
     *        trigger a false result
     * @returns false if there is an error message to display.
     */
    protected boolean saveAndCanProceed(boolean requireProfile)
    {
/*        if (thisOffering != null)
        {
            boolean offeringIsSuspended =
                thisOffering.gradingSuspended();
            if ( offeringIsSuspended != isSuspended )
            {
                if ( ! offeringIsSuspended )
                {
                    log.debug( "suspending grading on this assignment" );
                    thisOffering.setGradingSuspended( true );
                    isSuspended = true;
                }
                else
                {
                    log.debug( "resuming grading on this assignment" );
                    thisOffering.setGradingSuspended( false );
                    // Have to save this change first!
//                    if (!applyLocalChanges()) return false;
                    releaseSuspendedSubs();
                }
            }
        }
*/      if (requireProfile
            && assignment.submissionProfile() == null)
        {
            error(
                "please select submission rules for this assignment." );
        }
        return validateURL(assignment.url()) && !hasMessages();
    }


    // ----------------------------------------------------------
    public WOComponent next()
    {
        if (saveAndCanProceed())
        {
            return super.next();
        }
        else
        {
            return null;
        }
    }


    // ----------------------------------------------------------
    public boolean applyEnabled()
    {
        return hasSubmissionProfile();
    }


    // ----------------------------------------------------------
    public boolean finishEnabled()
    {
        return applyEnabled();
    }


    // ----------------------------------------------------------
    public boolean applyLocalChanges()
    {
        return saveAndCanProceed() && super.applyLocalChanges();
    }


    // ----------------------------------------------------------
    public boolean hasSubmissionProfile()
    {
        return null != assignment.submissionProfile();
    }


    // ----------------------------------------------------------
    public WOComponent editSubmissionProfile()
    {
        WCComponent result = null;
        if ( saveAndCanProceed() )
        {
//            result = (WCComponent)pageWithName(
//                SelectSubmissionProfile.class.getName());
            result = pageWithName(EditSubmissionProfilePage.class);
            result.nextPage = this;
        }
        return result;
    }


    // ----------------------------------------------------------
    public WOComponent newSubmissionProfile()
    {
        WCComponent result = null;
        if (saveAndCanProceed(false))
        {
            SubmissionProfile newProfile = new SubmissionProfile();
            localContext().insertObject(newProfile);
            assignment.setSubmissionProfileRelationship(newProfile);
            newProfile.setAuthor(user());
            result = pageWithName(EditSubmissionProfilePage.class);
            result.nextPage = this;
        }
        return result;
    }


    // ----------------------------------------------------------
    public String searchStringForGradingPluginToAdd()
    {
        String name = gradingPluginToAdd.name();
        return name;
    }


    // ----------------------------------------------------------
    public String displayStringForGradingPluginToAdd()
    {
        String name = gradingPluginToAdd.name();
        String version = gradingPluginToAdd.descriptor().currentVersion();
        NSTimestamp lastModified = gradingPluginToAdd.lastModified();
        String formattedTime =
            wcSession().timeFormatter().format(lastModified);

        return MessageFormat.format(
                "<p class=\"pluginListTitle\">{0}</p>" +
                "<p class=\"pluginListSubtitle\">version {1} ({2})</p>",
                new Object[] { name, version, formattedTime });
    }


    // ----------------------------------------------------------
    public boolean hasSuspendedSubs()
    {
        log.debug(
            "script count = " + scriptDisplayGroup.displayedObjects().count());
        suspendedSubmissionCount =
            thisOffering.getSuspendedSubs().count();
        return suspendedSubmissionCount > 0;
    }


    // ----------------------------------------------------------
    public int numSuspendedSubs()
    {
        return suspendedSubmissionCount;
    }


    // ----------------------------------------------------------
    public WOComponent releaseSuspendedSubs()
    {
        log.info("releasing all paused assignments: "
              + thisOffering.titleString());
        EOEditingContext ec = Application.newPeerEditingContext();
        try
        {
            ec.lock();
            AssignmentOffering localAO = thisOffering.localInstance(ec);
            NSArray<EnqueuedJob> jobList = localAO.getSuspendedSubs();
            for (EnqueuedJob job : jobList)
            {
                job.setPaused(false);
                job.setQueueTime(new NSTimestamp());
            }
            log.info("released " + jobList.count() + " jobs");
            ec.saveChanges();
        }
        finally
        {
            ec.unlock();
            Application.releasePeerEditingContext(ec);
        }
        // trigger the grading queue to read the released jobs
        Grader.getInstance().graderQueue().enqueue(null);
        return null;
    }


    // ----------------------------------------------------------
    public WOComponent cancelSuspendedSubs()
    {
        EOEditingContext ec = Application.newPeerEditingContext();
        try
        {
            ec.lock();
            AssignmentOffering localAO = thisOffering.localInstance(ec);
            log.info(
                "cancelling all paused assignments: "
                + coreSelections().courseOffering().course().deptNumber()
                + " "
                + localAO.assignment().name());
            NSArray<EnqueuedJob> jobList = localAO.getSuspendedSubs();
            for (EnqueuedJob job : jobList)
            {
                ec.deleteObject(job);
            }
            log.info("cancelled " + jobList.count() + " jobs");
            ec.saveChanges();
        }
        finally
        {
            ec.unlock();
            Application.releasePeerEditingContext(ec);
        }
        return null;
    }


    // ----------------------------------------------------------
    public WOComponent regradeSubsActionOk()
    {
        if (!applyLocalChanges()) return null;
        offeringForAction.regradeMostRecentSubsForAll(localContext());
        applyLocalChanges();
        return null;
    }


    // ----------------------------------------------------------
    public WOActionResults regradeSubs()
    {
        if (!saveAndCanProceed())
        {
            return displayMessages();
        }

        offeringForAction = thisOffering;
        return new ConfirmingAction(this, false)
        {
            @Override
            protected String confirmationTitle()
            {
                return "Regrade Everyone's Submission?";
            }

            @Override
            protected String confirmationMessage()
            {
                return "<p>This action will <b>regrade the most recent "
                    + "submission for every student</b> who has submitted to "
                    + "this assignment.</p><p>This will also <b>delete all "
                    + "prior results</b> for the submissions to be regraded "
                    + "and <b>delete all TA comments and scoring</b> that "
                    + "have been recorded for the submissions to be regraded."
                    + "</p><p>Each student\'s most recent submission will be "
                    + "re-queued for grading, and each student will receive "
                    + "an e-mail message when their new results are "
                    + "available.</p><p class=\"center\">Regrade everyone's "
                    + "most recent submission?</p>";
            }

            @Override
            protected WOActionResults actionWasConfirmed()
            {
                return regradeSubsActionOk();
            }
        };
    }


    // ----------------------------------------------------------
    public WOComponent suspendGrading()
    {
        if (saveAndCanProceed())
        {
            thisOffering.setGradingSuspended(true);
            applyLocalChanges();
        }
        return null;
    }


    // ----------------------------------------------------------
    public WOComponent resumeGrading()
    {
        if (saveAndCanProceed())
        {
            thisOffering.setGradingSuspended(false);
            if (applyLocalChanges())
            {
                releaseSuspendedSubs();
            }
        }
        return null;
    }


    // ----------------------------------------------------------
    public JavascriptGenerator addStep()
    {
        if (selectedGradingPluginToAdd != null)
        {
            if (saveAndCanProceed())
            {
                assignment.addNewStep(selectedGradingPluginToAdd);
                applyLocalChanges();

                scriptDisplayGroup.fetch();
            }
        }

        return new JavascriptGenerator()
            .refresh("gradingSteps")
            .unblock("gradingStepsTable");
    }


    // ----------------------------------------------------------
    public JavascriptGenerator removeStep()
    {
        int pos = thisStep.order();
        for (int i = pos;
             i < scriptDisplayGroup.displayedObjects().count();
             i++)
        {
            scriptDisplayGroup.displayedObjects().objectAtIndex(i).setOrder(i);
        }
        if (   thisStep.config() != null
            && thisStep.config().steps().count() == 1)
        {
            StepConfig thisConfig = thisStep.config();
            thisStep.setConfigRelationship(null);
            localContext().deleteObject(thisConfig);
        }
        localContext().deleteObject(thisStep);
        localContext().saveChanges();

        scriptDisplayGroup.fetch();

        return new JavascriptGenerator()
            .refresh("gradingSteps")
            .unblock("gradingStepsTable");
    }


    // ----------------------------------------------------------
    public boolean stepAllowsTimeout()
    {
        return thisStep.gradingPlugin().timeoutMultiplier() != 0;
    }


    // ----------------------------------------------------------
    public WOComponent editStep()
    {
        WCComponent result = null;
        if ( saveAndCanProceed() )
        {
            log.debug( "step = " + thisStep );
            prefs().setAssignmentOfferingRelationship(selectedOffering);
            prefs().setStepRelationship( thisStep );
            result = pageWithName(EditStepPage.class);
            result.nextPage = this;
        }
        return result;
    }


    // ----------------------------------------------------------
    public void gradingStepsWereDropped(
            String sourceId, int[] dragIndices,
            String targetId, int[] dropIndices,
            boolean isCopy)
    {
        NSMutableArray<Step> steps =
            scriptDisplayGroup.displayedObjects().mutableClone();
        NSMutableArray<Step> stepsRemoved = new NSMutableArray<Step>();
        TreeMap<Integer, Step> finalStepPositions =
            new TreeMap<Integer, Step>();

        for (int i = 0; i < dragIndices.length; i++)
        {
            Step step = steps.objectAtIndex(dragIndices[i]);
            finalStepPositions.put(dropIndices[i], step);
            stepsRemoved.addObject(step);
        }

        steps.removeObjectsInArray(stepsRemoved);

        for (Map.Entry<Integer, Step> movement : finalStepPositions.entrySet())
        {
            int dropIndex = movement.getKey();
            Step step = movement.getValue();

            steps.insertObjectAtIndex(step, dropIndex);
        }

        int order = 1;
        for (Step step : steps)
        {
            step.setOrder(order++);
        }

        scriptDisplayGroup.fetch();
    }


    // ----------------------------------------------------------
    public Integer stepTimeout()
    {
        log.debug( "step = " + thisStep );
        log.debug( "num steps = "
                   + scriptDisplayGroup.displayedObjects().count() );
        return thisStep.timeoutRaw();
    }


    // ----------------------------------------------------------
    public void setStepTimeout( Integer value )
    {
        if ( value != null && !Step.timeoutIsWithinLimits( value ) )
        {
            // set error message if timeout is out of range
            error(
                "The maximum timeout allowed is "
                + Step.maxTimeout()
                + ".  Contact the administrator for higher limits." );
        }
        // This will automatically restrict to the max value anyway
        thisStep.setTimeoutRaw( value );
    }


    // ----------------------------------------------------------
    public JavascriptGenerator clearGraph()
    {
        thisOffering.clearGraphSummary();
        applyLocalChanges();

        return new JavascriptGenerator()
            .refresh("scoreHistogram" + thisOffering.id());
    }


    // ----------------------------------------------------------
    public void takeValuesFromRequest(WORequest arg0, WOContext arg1)
    {
        log.debug("takeValuesFromRequest()");
        long timeStartedHere = System.currentTimeMillis();

        super.takeValuesFromRequest(arg0, arg1);

        if (assignment != null)
        {
            log.debug("looking for similar submission profile by name");
            String name = assignment.name();
            if ( assignment.submissionProfile() == null
                 && name != null )
            {
                NSArray<AssignmentOffering> similar = AssignmentOffering
                    .offeringsWithSimilarNames(
                        localContext(),
                        name,
                        selectedOffering.courseOffering(),
                        1);
                if (similar.count() > 0)
                {
                    AssignmentOffering ao = similar.objectAtIndex(0);
                    assignment.setSubmissionProfile(
                        ao.assignment().submissionProfile());
                }
            }
        }

        long timeTaken = System.currentTimeMillis() - timeStartedHere;
        log.debug("Time in takeValuesFromRequest(): " + timeTaken + " ms");
    }


    // ----------------------------------------------------------
    private WOComponent flush(WOComponent page)
    {
        flushNavigatorDerivedData();
        return page;
    }


    // ----------------------------------------------------------
    private WOComponent deleteActionOk()
    {
        prefs().setAssignmentOfferingRelationship(null);
        for (AssignmentOffering ao : offeringGroup.displayedObjects())
        {
            if (ao != offeringForAction)
            {
                prefs().setAssignmentOfferingRelationship(ao);
                break;
            }
        }
        if (!applyLocalChanges()) return flush(null);
        localContext().deleteObject(offeringForAction);
        if (!applyLocalChanges()) return flush(null);
        if (assignment.offerings().count() == 0)
        {
            prefs().setAssignmentOfferingRelationship(null);
            prefs().setAssignmentRelationship(null);
            localContext().deleteObject(assignment);
            applyLocalChanges();
        }
        return flush(null);
    }


    // ----------------------------------------------------------
    public WOActionResults delete()
    {
        if (!applyLocalChanges())
        {
            return displayMessages();
        }

        offeringForAction = thisOffering;
        return new ConfirmingAction(this, false) {
            @Override
            protected String confirmationTitle()
            {
                return "Delete This Assignment Offering?";
            }

            @Override
            protected String confirmationMessage()
            {
                String message =
                    "<p>This action will <b>delete the assignment offering \""
                    + offeringForAction
                    + "\"</b>, "
                    + "together with any staff submissions that have been "
                    + "made to it.</p>";
                if (offeringForAction.assignment().offerings().count() == 1)
                {
                    message +=
                        "<p>Since this is the only offering of the selected "
                        + "assignment, this action will also <b>delete the "
                        + "assignment altogether</b>.  This action cannot be "
                        + "undone.</p>";
                }
                return message + "<p class=\"center\">Delete this "
                    + "assignment offering?</p>";
            }

            @Override
            protected WOActionResults actionWasConfirmed()
            {
                return deleteActionOk();
            }
        };
    }


    // ----------------------------------------------------------
    /**
     * Check whether the selected assignment is past the due date.
     *
     * @return true if any submissions to this assignment will be counted
     *         as late
     */
    public boolean upcomingOfferingIsLate()
    {
        NSTimestamp dueDate = upcomingOffering.dueDate();

        if (dueDate != null)
        {
            return dueDate.before(currentTime);
        }
        else
        {
            // FIXME is this the best answer?
            return true;
        }
    }


    // ----------------------------------------------------------
    public NSArray<AssignmentOffering> upcomingOfferings()
    {
        if (upcomingOfferings == null)
        {
            upcomingOfferings = AssignmentOffering.allOfferingsOrderedByDueDate(
                localContext()).mutableClone();
            upcomingOfferings.removeObject(selectedOffering);
        }
        return upcomingOfferings;
    }


    // ----------------------------------------------------------
    public TimeZone timeZone()
    {
        return TimeZone.getTimeZone(user().timeZoneName());
    }


    // ----------------------------------------------------------
    public Boolean surveysSupported()
    {
        if (surveysSupported == null)
        {
            surveysSupported = Boolean.valueOf(
                wcApplication().subsystemManager().subsystem("Opinions")
                != null);
        }
        return surveysSupported.booleanValue();
    }


    //~ Instance/static variables .............................................

    private int            suspendedSubmissionCount = 0;
    private NSMutableArray<AssignmentOffering> upcomingOfferings;
    private NSTimestamp    currentTime;
    private AssignmentOffering offeringForAction;

    private long timeStart;

    private static Boolean surveysSupported;

    static Logger log = Logger.getLogger( EditAssignmentPage.class );
}
