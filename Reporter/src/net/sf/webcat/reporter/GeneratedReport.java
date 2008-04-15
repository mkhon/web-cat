/*==========================================================================*\
 |  $Id: GeneratedReport.java,v 1.6 2008/04/15 04:09:22 aallowat Exp $
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

package net.sf.webcat.reporter;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import net.sf.webcat.core.MutableArray;
import net.sf.webcat.core.MutableDictionary;
import net.sf.webcat.core.User;

// -------------------------------------------------------------------------
/**
 * Represents a report that is being generated or has been generated from a
 * report template.
 *
 * @author Tony Allevato
 * @version $Id: GeneratedReport.java,v 1.6 2008/04/15 04:09:22 aallowat Exp $
 */
public class GeneratedReport
    extends _GeneratedReport
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new GeneratedReport object.
     */
    public GeneratedReport()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    private static String generatedReportDirForUser(User user)
    {

        StringBuffer dir = new StringBuffer( 50 );
        dir.append( net.sf.webcat.core.Application
            .configurationProperties().getProperty( "grader.submissiondir" ) );
        dir.append( '/' );
        dir.append( user.authenticationDomain().subdirName() );
        dir.append( '/' );
        dir.append( GENERATED_REPORTS_SUBDIR_NAME );
        dir.append( '/' );
        dir.append( user.userName() );
        return dir.toString();
    }


    // ----------------------------------------------------------
    private static String generatedReportFilePathForUser(
        User user, Number id)
    {
        return generatedReportDirForUser(user) + "/" + id.toString() +
            REPORT_EXTENSION;
    }


    // ----------------------------------------------------------
    public String renderedResourcesDir()
    {
        StringBuffer dir = new StringBuffer( 50 );
        dir.append( net.sf.webcat.core.Application
            .configurationProperties().getProperty( "grader.workarea" ) );
        dir.append( "/" );
        dir.append( RENDERED_REPORTS_SUBDIR_NAME );
        dir.append( "/" );
        dir.append( id().toString() );

        return dir.toString();
    }


    // ----------------------------------------------------------
    public String renderedResourcePath(String filename)
    {
        return renderedResourcesDir() + "/" + filename;
    }


    // ----------------------------------------------------------
    public boolean hasRenderingErrors()
    {
        File renderDir = new File(renderedResourcesDir());
        if (renderDir.exists())
        {
            File renderErrorFile = new File(renderDir, RENDER_ERRORS_FILE);
            if (renderErrorFile.exists())
            {
                return true;
            }
        }

        return false;
    }


    // ----------------------------------------------------------
    public MutableArray renderingErrors()
    {
        MutableArray errors = null;

        File renderDir = new File(renderedResourcesDir());
        if (renderDir.exists())
        {
            File renderErrorFile = new File(renderDir, RENDER_ERRORS_FILE);
            if (renderErrorFile.exists())
            {
                try
                {
                    FileInputStream stream = new FileInputStream(renderErrorFile);
                    NSData data = new NSData(stream, 1024);
                    errors = MutableArray.objectWithArchiveData(data);
                    stream.close();
                }
                catch (IOException e)
                {
                    log.error("Error reading rendering error info: ", e);
                }
            }
        }

        return errors;
    }


    // ----------------------------------------------------------
    public void setRenderingErrors(MutableArray errors)
    {
        File renderDir = new File(renderedResourcesDir());
        if (renderDir.exists())
        {
            try
            {
                File renderErrorFile = new File(renderDir, RENDER_ERRORS_FILE);
                FileOutputStream stream = new FileOutputStream(renderErrorFile);
                errors.archiveData().writeToStream(stream);
                stream.close();
            }
            catch (IOException e)
            {
                log.error("Error writing rendering error info: ", e);
            }
        }
    }


    // ----------------------------------------------------------
    public void markAsRenderedWithMethod(String method)
    {
        File renderDir = new File(renderedResourcesDir());
        if (renderDir.exists())
        {
            try
            {
                String tokenName = RENDER_TOKEN_PREFIX + method;
                File renderToken = new File(renderDir, tokenName);
                FileOutputStream stream = new FileOutputStream(renderToken);
                stream.write(0);
                stream.close();
            }
            catch (IOException e)
            {
                log.error("Could not create render-complete token: ", e);
            }
        }
    }


    // ----------------------------------------------------------
    public boolean isRenderedWithMethod(String method)
    {
        File renderDir = new File(renderedResourcesDir());
        if (renderDir.exists())
        {
            String tokenName = RENDER_TOKEN_PREFIX + method;
            File renderToken = new File(renderDir, tokenName);
            if (renderToken.exists())
            {
                return true;
            }
        }
        return false;
    }


    // ----------------------------------------------------------
    public String generatedReportDir()
    {
        return generatedReportDirForUser(user());
    }


    // ----------------------------------------------------------
    public String generatedReportFile()
    {
        return generatedReportFilePathForUser(user(), id());
    }


    //~ Constructors ..........................................................

    public static final String REPORT_EXTENSION = ".rptdocument";

    private static final String RENDER_TOKEN_PREFIX = ".rendered.";

    private static final String RENDER_ERRORS_FILE = ".renderingErrors";

    private static final String GENERATED_REPORTS_SUBDIR_NAME =
        "GeneratedReports";

    private static final String RENDERED_REPORTS_SUBDIR_NAME =
        "RenderedReports";
}
