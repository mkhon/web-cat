/*==========================================================================*\
 |  $Id: ReportProblemFinder.java,v 1.1 2008/04/12 20:56:05 aallowat Exp $
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

package net.sf.webcat.oda.designer.metadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ModuleHandle;

// ------------------------------------------------------------------------
/**
 * Detects problems in a report template that will cause Web-CAT to complain so
 * that the user has an opportunity to fix them before uploading the template.
 *
 * @author Tony Allevato (Virginia Tech Computer Science)
 * @version $Id: ReportProblemFinder.java,v 1.1 2008/04/12 20:56:05 aallowat Exp $
 */
public class ReportProblemFinder
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new report problem finder and gathers the problems that exist
     * in the report.
     *
     * @param module
     *            the report template handle to search for problems
     */
    public ReportProblemFinder(ModuleHandle module)
    {
        this.module = module;

        problems = new ArrayList<ReportProblem>();

        findMetadataProblems();
        findDataSetProblems();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether any problems were found in the report
     * template.
     *
     * @return true if there were problems, otherwise false.
     */
    public boolean hasProblems()
    {
        return problems.size() > 0;
    }


    // ----------------------------------------------------------
    /**
     * Gets the highest severity among all of the problems that were found.
     *
     * @return the highest severity among all of the problems
     */
    public int getSeverityOfWorstProblem()
    {
        int maxSeverity = ReportProblem.SEVERITY_OK;

        for(ReportProblem problem : problems)
        {
            if(problem.getSeverity() > maxSeverity)
            {
                maxSeverity = problem.getSeverity();
            }
        }

        return maxSeverity;
    }


    // ----------------------------------------------------------
    /**
     * Gets the problems that were found in the report template.
     *
     * @return an array of ReportProblem objects describing the problems.
     */
    public ReportProblem[] getProblems()
    {
        ReportProblem[] problemArray = new ReportProblem[problems.size()];
        problems.toArray(problemArray);
        return problemArray;
    }


    // ----------------------------------------------------------
    /**
     * Finds problems related to Web-CAT metadata, such as the lack of a title
     * or description.
     */
    private void findMetadataProblems()
    {
        // Check for empty title.
        if (ReportMetadata.getTitle(module) == null)
        {
            addProblem(ReportProblem.SEVERITY_ERROR, MSG_NO_TITLE);
        }

        // Check for empty description.
        if (ReportMetadata.getDescription(module) == null)
        {
            addProblem(ReportProblem.SEVERITY_ERROR, MSG_NO_DESCRIPTION);
        }

        // Check that at least one author has been provided.
        int authorCount = ReportMetadata.getAuthorsCount(module);

        if (authorCount == 0)
        {
            addProblem(ReportProblem.SEVERITY_ERROR, MSG_NO_AUTHORS);
        }

        // Check that every author has a name.
        for (int i = 1; i <= authorCount; i++)
        {
            if (ReportMetadata.getAuthorName(module, i) == null)
            {
                addProblem(ReportProblem.SEVERITY_ERROR, MSG_AUTHOR_NO_NAME, i);
            }
        }

        // Check that a copyright statement has been provided.
        if (ReportMetadata.getCopyright(module) == null)
        {
            addProblem(ReportProblem.SEVERITY_WARNING, MSG_NO_COPYRIGHT);
        }

        // Check that a license has been provided.
        if (ReportMetadata.getLicense(module) == null)
        {
            addProblem(ReportProblem.SEVERITY_WARNING, MSG_NO_LICENSE);
        }
    }


    // ----------------------------------------------------------
    /**
     * Finds problems related to data sets, such as missing descriptions.
     */
    private void findDataSetProblems()
    {
        Iterator<?> it = module.getDataSets().iterator();

        // Check that every Web-CAT data set has a description in its comments
        // section.
        while (it.hasNext())
        {
            DataSetHandle dataSet = (DataSetHandle) it.next();

            String extensionID = dataSet.getStringProperty("extensionID");

            if ("net.sf.webcat.oda.dataSet".equals(extensionID))
            {
                String description = dataSet.getComments();

                if (description == null || description.trim().length() == 0)
                {
                    addProblem(ReportProblem.SEVERITY_WARNING,
                            MSG_DATASET_NO_DESCRIPTION, dataSet.getName());
                }
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Utility method to add a problem with a formatted description to the
     * problem list.
     *
     * @param severity
     *            the severity of the problem
     * @param format
     *            the format string for the description
     * @param params
     *            arguments to the format string
     */
    private void addProblem(int severity, String format, Object... params)
    {
        ReportProblem problem = new ReportProblem(severity, String.format(
                format, params));

        problems.add(problem);
    }


    //~ Static/instance variables .............................................

    private ModuleHandle module;

    private List<ReportProblem> problems;

    /*
     * The following messages are not internationalized because this source is
     * current shared between both the Eclipse designer plug-ins and Web-CAT.
     */
    private static final String MSG_NO_TITLE = "The report template does not "
            + "have a title.";

    private static final String MSG_NO_DESCRIPTION = "The report template "
            + "does not have a description.";

    private static final String MSG_NO_AUTHORS = "The report template does "
            + "not list any authors.";

    private static final String MSG_AUTHOR_NO_NAME = "Author #%d does not "
            + "have a name.";

    private static final String MSG_NO_COPYRIGHT = "You have not provided a "
            + "copyright statement. A default copyright will be made from the "
            + "current year and institution of the first author.";

    private static final String MSG_NO_LICENSE = "You have not provided a "
            + "license. A default license of \"All rights reserved by the "
            + "copyright holder\" will be used.";

    private static final String MSG_DATASET_NO_DESCRIPTION = "The Web-CAT "
            + "data set \"%s\" does not have a description.";
}
