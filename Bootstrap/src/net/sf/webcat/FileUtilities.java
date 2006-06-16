/*==========================================================================*\
 |  $Id: FileUtilities.java,v 1.1 2006/06/16 14:56:27 stedwar2 Exp $
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

package net.sf.webcat;

import java.io.*;
import java.util.*;
import java.util.zip.*;

// -------------------------------------------------------------------------
/**
 *  TODO Write a one-sentence summary of your class here.
 *  TODO Follow it with additional details about its purpose, what abstraction
 *  it represents, and how to use it.
 *
 *  @author  stedwar2
 *  @version May 18, 2006
 */
public class FileUtilities
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * This constructor is private since this class should not be
     * instantiated.  It simply provides a few static helper methods
     * used by other classes.
     */
    private FileUtilities()
    {
        // No actions to perform, since this class has no state.
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Unzips the zipfile in the output directory.  This method is
     * provided purely for use by the Bootstrap code.  All other code
     * should use the net.sf.webcat.archives.ArchiveManager
     * class instead.
     * 
     * @param zipFile the zip file
     * @param outputDir full path of the output directory
     * @throws java.io.IOException if occurs during unzipping
     */
    public static void unZip( ZipFile zipFile, File outputDir )
        throws java.io.IOException
    {
        Enumeration e = zipFile.entries();
        while ( e.hasMoreElements() )
        {
            ZipEntry entry = (ZipEntry)e.nextElement();
            File entryFile = new File( outputDir, entry.getName() );
            if ( entry.isDirectory() )
            {
                entryFile.mkdirs();
            }
            else
            {
                File parent = entryFile.getParentFile();
                if ( !parent.exists() )
                {
                    parent.mkdirs();
                }
                BufferedInputStream  in = new BufferedInputStream(
                    zipFile.getInputStream( entry ) );
                BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream( entryFile ) );
                copyStream( in, out );
                in.close();
                out.close();
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Copies the contents of an input stream onto an existing output
     * stream.  The output stream is flushed when the operation
     * is complete.
     * 
     * @param in  the input stream to copy
     * @param out the destination
     * @throws IOException if there are IO errors
     */
    public static void copyStream( InputStream in, OutputStream out )
        throws IOException
    {
        // read in increments of BUFFER_SIZE
        byte[] b = new byte[BUFFER_SIZE];
        int count = in.read( b );
        while ( count > -1 )
        {
            out.write( b, 0, count );
            count = in.read( b );
        }
        out.flush();
    }


    //~ Instance/static variables .............................................

    static final private int BUFFER_SIZE = 8192;
}
