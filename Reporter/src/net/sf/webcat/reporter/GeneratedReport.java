/*==========================================================================*\
 |  GeneratedReport.java
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU General Public License as published by
 |  the Free Software Foundation; either version 2 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU General Public License
 |  along with Web-CAT; if not, write to the Free Software
 |  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 |
 |  Project manager: Stephen Edwards <edwards@cs.vt.edu>
 |  Virginia Tech CS Dept, 660 McBryde Hall (0106), Blacksburg, VA 24061 USA
 \*==========================================================================*/

package net.sf.webcat.reporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sf.webcat.core.User;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;

// -------------------------------------------------------------------------
/**
 * TODO: place a real description here.
 *
 * @author 
 * @version $Id: GeneratedReport.java,v 1.2 2007/07/26 15:22:50 aallowat Exp $
 */
public class GeneratedReport
    extends _GeneratedReport
{
	private static final String RENDER_TOKEN_PREFIX = ".rendered.";

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

    public static String generatedReportDirForUser(User user)
    {

        StringBuffer dir = new StringBuffer( 50 );
        dir.append( net.sf.webcat.core.Application
            .configurationProperties().getProperty( "grader.submissiondir" ) );
        dir.append( '/' );
        dir.append( user.authenticationDomain().subdirName() );
        dir.append( '/' );
        dir.append( "GeneratedReports/" );
        dir.append( user.userName() );
        return dir.toString();
    }

    
    public static String generatedReportFilePathForUser(User user,
    		String uuid)
    {
    	return generatedReportDirForUser(user) + "/" + uuid +
    		".rptdocument";
    }
    
    
    public static String renderedResourcesDir(String uuid)
    {
        StringBuffer dir = new StringBuffer( 50 );
        dir.append( net.sf.webcat.core.Application
            .configurationProperties().getProperty( "grader.workarea" ) );
        dir.append( "/RenderedReports/" );
        dir.append( uuid );

        return dir.toString();    	
    }

    public static String renderedResourcePath(String uuid, String filename)
    {
    	return renderedResourcesDir(uuid) + "/" + filename;
    }

    public void markAsRenderedWithMethod(String method)
    {
    	File renderDir = new File(renderedResourcesDir(uuid()));
    	if(renderDir.exists())
    	{
    		try
    		{
    			String tokenName = RENDER_TOKEN_PREFIX + method;
        		File renderToken = new File(renderDir, tokenName);
				FileOutputStream stream = new FileOutputStream(renderToken);
				stream.write(0);
				stream.close();
			}
    		catch(IOException e)
    		{
				log.error("Could not create render-complete token: ", e);
			}
    	}
    }
    
    public boolean isRenderedWithMethod(String method)
    {
    	File renderDir = new File(renderedResourcesDir(uuid()));
    	if(renderDir.exists())
    	{
    		String tokenName = RENDER_TOKEN_PREFIX + method;
    		File renderToken = new File(renderDir, tokenName);
    		if(renderToken.exists())
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }

    public String generatedReportDir()
    {
    	return generatedReportDirForUser(user());
    }


    public String generatedReportFile()
    {
    	return generatedReportFilePathForUser(user(), uuid());
    }


// If you add instance variables to store property values you
// should add empty implementions of the Serialization methods
// to avoid unnecessary overhead (the properties will be
// serialized for you in the superclass).

//    // ----------------------------------------------------------
//    /**
//     * Serialize this object (an empty implementation, since the
//     * superclass handles this responsibility).
//     * @param out the stream to write to
//     */
//    private void writeObject( java.io.ObjectOutputStream out )
//        throws java.io.IOException
//    {
//    }
//
//
//    // ----------------------------------------------------------
//    /**
//     * Read in a serialized object (an empty implementation, since the
//     * superclass handles this responsibility).
//     * @param in the stream to read from
//     */
//    private void readObject( java.io.ObjectInputStream in )
//        throws java.io.IOException, java.lang.ClassNotFoundException
//    {
//    }
}
