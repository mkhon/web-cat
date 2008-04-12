/*==========================================================================*\
 |  $Id: ReportProblem.java,v 1.1 2008/04/12 20:56:05 aallowat Exp $
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

// ------------------------------------------------------------------------
/**
 * Represents a problem discovered within the report template.
 *
 * @author Tony Allevato (Virginia Tech Computer Science)
 * @version $Id: ReportProblem.java,v 1.1 2008/04/12 20:56:05 aallowat Exp $
 */
public class ReportProblem
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new report problem object.
     *
     * @param severity
     *            the severity of the problem, defined by one of the SEVERITY_*
     *            constants
     * @param description
     *            a text description of the problem
     */
    public ReportProblem(int severity, String description)
    {
        this.severity = severity;
        this.description = description;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets the severity of the problem.
     *
     * @return the problem severity
     */
    public int getSeverity()
    {
        return severity;
    }


    // ----------------------------------------------------------
    /**
     * Gets the description of the problem.
     *
     * @return the problem description
     */
    public String getDescription()
    {
        return description;
    }


    //~ Static/instance variables .............................................

    /**
     * The problem is a simple informative message that does not require user
     * intervention.
     */
    public static final int SEVERITY_OK = 0;

    /**
     * The problem is one which will not prevent the report template from being
     * used, but will cause the full functionality of Web-CAT to be reduced in
     * some way or cause the user interface to be less clear or informative for
     * the user.
     */
    public static final int SEVERITY_WARNING = 1;

    /**
     * The problem is serious enough that Web-CAT will not accept the report
     * template until it is fixed.
     */
    public static final int SEVERITY_ERROR = 2;

    /** The severity of the problem. */
    private int severity;

    /** The description of the problem. */
    private String description;
}
