/*==========================================================================*\
 |  $Id: CourseAndAssignmentResultOutcomesAssistant.java,v 1.2 2009/06/04 16:45:03 aallowat Exp $
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

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import er.ajax.AjaxTreeModel;
import er.extensions.eof.ERXEOControlUtilities;
import net.sf.webcat.core.Course;
import net.sf.webcat.core.CourseOffering;
import net.sf.webcat.grader.Assignment;
import net.sf.webcat.grader.AssignmentOffering;
import net.sf.webcat.reporter.QualifierUtils;
import net.sf.webcat.reporter.ReportDataSet;
import net.sf.webcat.reporter.ReporterComponent;

//-------------------------------------------------------------------------
/**
 * A simplified query assistant that allows the user to select all the
 * result outcomes from submissions from one or more assignment offerings that
 * are common across a specified set of course offerings.
 * 
 * Right now the code for this component is no different than the code for the
 * CourseAndAssignmentSubmissionsAssistant -- it is the models that differ, and
 * those places where the component UI is different act upon the models
 * directly instead of going through methods or properties on this class.  For
 * this reason, currently, this class merely subclasses
 * CourseAndAssignmentSubmissionsAssistant and adds nothing to it.
 *
 * @author aallowat
 * @version $Id: CourseAndAssignmentResultOutcomesAssistant.java,v 1.2 2009/06/04 16:45:03 aallowat Exp $
 */
public class CourseAndAssignmentResultOutcomesAssistant
    extends CourseAndAssignmentSubmissionsAssistant
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new object.
     * @param context the page's context
     */
    public CourseAndAssignmentResultOutcomesAssistant(WOContext context)
    {
        super(context);
    }
}
