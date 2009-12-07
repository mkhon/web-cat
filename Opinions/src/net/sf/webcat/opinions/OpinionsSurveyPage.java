/*==========================================================================*\
 |  $Id: OpinionsSurveyPage.java,v 1.1 2009/12/07 20:04:04 stedwar2 Exp $
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
import net.sf.webcat.core.*;
import net.sf.webcat.grader.Assignment;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * This page presents the user with the feedback/opinions survey for
 * the given assignment.
 *
 * @author Stephen Edwards
 * @author Last changed by $Author: stedwar2 $
 * @version $Revision: 1.1 $, $Date: 2009/12/07 20:04:04 $
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

    public Assignment assignment;


    //~ Methods ...............................................................



    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger(OpinionsSurveyPage.class);
}
