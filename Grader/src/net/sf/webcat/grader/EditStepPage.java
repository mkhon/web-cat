/*==========================================================================*\
 |  $Id: EditStepPage.java,v 1.6 2008/10/29 14:15:21 aallowat Exp $
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

package net.sf.webcat.grader;

import com.webobjects.foundation.*;
import com.webobjects.appserver.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipFile;
import net.sf.webcat.core.*;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * This class presents the list of scripts (grading steps) that
 * are available for selection.
 *
 * @author Stephen Edwards
 * @version $Id: EditStepPage.java,v 1.6 2008/10/29 14:15:21 aallowat Exp $
 */
public class EditStepPage
    extends GraderComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * This is the default constructor
     *
     * @param context The page's context
     */
    public EditStepPage( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public NSArray           stepConfigList;
    public StepConfig        stepConfig;
    public Step              step;
    public int               index;
    public java.io.File      baseDir;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse( WOResponse response, WOContext context )
    {
        log.debug( "appendToResponse()" );
        step = prefs().step();
        stepConfigList = StepConfig.configsForUserAndCourseScriptIncludingMine(
            localContext(),
            user(),
            step.script(),
            prefs().assignmentOffering().courseOffering().course(),
            step.config() );
        if ( baseDir == null )
        {
            baseDir = new java.io.File ( ScriptFile.userScriptDirName(
                user(), true ).toString() );
        }
        if ( log.isDebugEnabled() )
        {
            log.debug( "assignment option values ("
                + (step.configSettings() == null
                       ? "null"
                       : step.configSettings().hashCode())
                + ") =\n" + step.configSettings() );
            if ( step.config() == null )
            {
                log.debug( "shared option values = null\n" );
            }
            else
            {
                log.debug( "shared option values ("
                    + (step.config().configSettings() == null
                           ? "null"
                           : step.config().configSettings().hashCode())
                    + ") =\n" + step.config().configSettings() );
            }
        }
        super.appendToResponse( response, context );
    }


    // ----------------------------------------------------------
    public WOComponent next()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "new assignment option values ("
                + (step.configSettings() == null
                       ? "null"
                       : step.configSettings().hashCode())
                + ") =\n" + step.configSettings() );
            if ( step.config() == null )
            {
                log.debug( "new shared option values = null\n" );
            }
            else
            {
                log.debug( "new shared option values ("
                    + (step.config().configSettings() == null
                           ? "null"
                           : step.config().configSettings().hashCode())
                    + ") =\n" + step.config().configSettings() );
            }
        }
        return super.next();
    }


    // ----------------------------------------------------------
    public WOComponent editReusableConfig()
    {
        EditReusableScriptParametersPage newPage =
            (EditReusableScriptParametersPage)
            pageWithName( EditReusableScriptParametersPage.class.getName() );
        newPage.nextPage = this;
        return newPage;
    }


    // ----------------------------------------------------------
    public WOComponent newReusableConfig()
    {
        step.setConfigRelationship( null );
        return editReusableConfig();
    }


    // ----------------------------------------------------------
    public WOComponent defaultAction()
    {
        return null;
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( EditStepPage.class );
}
