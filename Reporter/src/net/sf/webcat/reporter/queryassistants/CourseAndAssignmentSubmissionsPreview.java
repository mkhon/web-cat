/*==========================================================================*\
 |  $Id: CourseAndAssignmentSubmissionsPreview.java,v 1.1 2008/10/28 15:52:30 aallowat Exp $
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

package net.sf.webcat.reporter.queryassistants;

import net.sf.webcat.core.CourseOffering;
import net.sf.webcat.reporter.ReportDataSet;
import net.sf.webcat.reporter.ReporterComponent;
import com.webobjects.appserver.WOContext;

//-------------------------------------------------------------------------
/**
 * The previewer associated with CourseAndAssignmentSubmissionAssistant.
 *
 * @author aallowat
 * @version $Id: CourseAndAssignmentSubmissionsPreview.java,v 1.1 2008/10/28 15:52:30 aallowat Exp $
 */
public class CourseAndAssignmentSubmissionsPreview extends ReporterComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new object.
     * @param context the page's context
     */
    public CourseAndAssignmentSubmissionsPreview(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

    public ReportDataSet dataSet;
    public CourseAndAssignmentSubmissionsModel model;

    // Repetition variables
    public CourseOffering courseOffering;
    public int index;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the current index in the repetition is
     * not the last course offering in the model (for delimiting the list by
     * commas when displayed).
     *
     * @return true if the current index is not the last course offering;
     *     false if it is.
     */
    public boolean indexIsNotLastCourseOffering()
    {
        return (index < model.courseOfferings().count() - 1);
    }
}
