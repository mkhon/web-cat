/*==========================================================================*\
 |  $Id: FileUtilities.java,v 1.5 2009/04/27 17:10:53 stedwar2 Exp $
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

package net.sf.webcat.archives;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//-------------------------------------------------------------------------
/**
 * Contains some common file copying operations used by the various archive
 * handlers.
 *
 * @author Tony Allowatt
 */
public class FileUtilities
{
    // ----------------------------------------------------------
	/**
	 * Copies the source file to the destination file.
	 *
     * @param srcFile A File object representing the path of the source file.
	 * @param destFile A File object representing the path (and name) of the
	 * destination file.
	 *
	 * @throws IOException
	 */
	public static void copyFileToFile( File srcFile, File destFile )
		throws IOException
	{
		FileInputStream stream = new FileInputStream( srcFile );
		FileUtilities.copyStreamToFile(
            stream, destFile, srcFile.lastModified() );
		stream.close();
	}


    // ----------------------------------------------------------
    /**
     * Copies a file into a specified directory
     *
     * @param oldFile the file to copy
     * @param outputDir the destination directory
     * @throws IOException if there are IO errors
     */
    public static void copyFileToDir( File oldFile, File outputDir )
        throws IOException
    {
        FileInputStream  in  = new FileInputStream( oldFile );
        File destFile = new File( outputDir, oldFile.getName() );
        FileOutputStream out = new FileOutputStream( destFile );
        copyStream( in, out );
        in.close();
        out.close();
        destFile.setLastModified( oldFile.lastModified() );
    }


    // ----------------------------------------------------------
    /**
     * Copies a file into a specified directory if it does not already
     * exist there, or if the source is newer than the destination.
     *
     * @param oldFile the file to copy
     * @param outputDir the destination directory
     * @throws IOException if there are IO errors
     */
    public static void copyFileToDirIfNecessary( File oldFile, File outputDir )
        throws IOException
    {
        File target = new File( outputDir, oldFile.getName() );
        if ( !target.exists()
             || target.lastModified() < oldFile.lastModified() )
        {
            FileInputStream  in  = new FileInputStream( oldFile );
            FileOutputStream out = new FileOutputStream( target );
            copyStream( in, out );
            in.close();
            out.close();
            target.setLastModified( oldFile.lastModified() );
        }
    }


    // ----------------------------------------------------------
    /**
     * Recursively copies the contents of the source directory into the
     * destination directory.
     *
     * @param source the source directory
     * @param destination the destination directory
     * @throws IOException if there are IO errors
     */
    public static void copyDirectoryContents( File source, File destination )
        throws IOException
    {
        File[] fileList = source.listFiles();
        for ( int i = 0; i < fileList.length; i++ )
        {
            if ( fileList[i].isDirectory() )
            {
                // Copy the whole directory
                File newDir = new File( destination, fileList[i].getName() );
                newDir.mkdir();
                copyDirectoryContents( fileList[i], newDir );
            }
            else if (fileList[i].getName().equals(".DS_Store"))
            {
                // ignore these for Mac OSX
            }
            else
            {
                copyFileToDir( fileList[i], destination );
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Recursively copies the contents of the source directory into the
     * destination directory, only updating files that are missing or
     * outdated.
     *
     * @param source the source directory
     * @param destination the destination directory
     * @throws IOException if there are IO errors
     */
    public static void copyDirectoryContentsIfNecessary(
        File source, File destination )
        throws IOException
    {
        File[] fileList = source.listFiles();
        for ( int i = 0; i < fileList.length; i++ )
        {
            if ( fileList[i].isDirectory() )
            {
                // Copy the whole directory
                File newDir = new File( destination, fileList[i].getName() );
                newDir.mkdir();
                copyDirectoryContentsIfNecessary( fileList[i], newDir );
            }
            else if (fileList[i].getName().equals(".DS_Store"))
            {
                // ignore these for Mac OSX
            }
            else
            {
                copyFileToDirIfNecessary( fileList[i], destination );
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Recursively deletes a directory
     *
     * @param dirName the path of the directory
     */
    public static void deleteDirectory( String dirName )
    {
        deleteDirectory( new File( dirName ) );
    }


    // ----------------------------------------------------------
    /**
     * Recursively deletes a directory
     *
     * @param dir the File object for the directory
     */
    public static void deleteDirectory( File dir )
    {
        File[] files = dir.listFiles();
        for ( int i = 0; i < files.length; i++ )
        {
            if ( files[i].isDirectory() )
            {
                deleteDirectory( files[i] );
            }
            files[i].delete();
        }
        dir.delete();
    }


    // ----------------------------------------------------------
    /**
     * Move the specified file to a new location (path + filename).  First
     * tries to rename the file, and then does a copy/delete if renaming won't
     * work.
     *
     * @param source the file to move
     * @param destination the new file name to rename it to
     * @throws IOException if there are IO errors
     */
    public static void moveFileToFile( File source, File destination )
        throws IOException
    {
        if ( source.renameTo( destination ) )
        {
            // if renaming worked, then we're done
            return;
        }

        copyFileToFile( source, destination );
        source.delete();
    }


    // ----------------------------------------------------------
    /**
     * Move the specified file into the destination directory.  First tries
     * to rename the file, and then does a copy/delete if renaming won't
     * work.
     *
     * @param source the file to move
     * @param destDir the destination directory to move it to
     * @throws IOException if there are IO errors
     */
    public static void moveFileToDir( File source, File destDir )
        throws IOException
    {
        File destFile = new File( destDir, source.getName() );
        moveFileToFile( source, destFile );
    }


    // ----------------------------------------------------------
    /**
     * Recursively copies the contents of the source directory into the
     * destination directory.
     *
     * @param source the source directory
     * @param destination the destination directory
     * @throws IOException if there are IO errors
     */
    public static void moveDirectoryContents( File source, File destination )
        throws IOException
    {
        File[] fileList = source.listFiles();
        for ( int i = 0; i < fileList.length; i++ )
        {
            if ( fileList[i].isDirectory() )
            {
                // Copy the whole directory
                File newDir = new File( destination, fileList[i].getName() );
                newDir.mkdir();
                moveDirectoryContents( fileList[i], newDir );
                fileList[i].delete();
            }
            else
            {
                moveFileToDir( fileList[i], destination );
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
        final int BUFFER_SIZE = 65536;

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


    // ----------------------------------------------------------
    /**
     * Copies data from the specified input stream to a file.
     *
     * @param stream An InputStream containing the data to be copied.
     * @param destFile A File object representing the path (and name) of the
     * destination file.
     *
     * @throws IOException
     */
    public static void copyStreamToFile( InputStream stream, File destFile )
        throws IOException
    {
        OutputStream outStream = new FileOutputStream( destFile );
        copyStream( stream, outStream );
        outStream.flush();
        outStream.close();
    }


    // ----------------------------------------------------------
    /**
     * Copies data from the specified input stream to a file and sets the
     * file's timestamp.
     *
     * @param stream An InputStream containing the data to be copied.
     * @param destFile A File object representing the path (and name) of the
     * destination file.
     * @param fileTime The timestamp to use for the destination file
     *
     * @throws IOException
     */
    public static void copyStreamToFile(
        InputStream stream, File destFile, long fileTime )
        throws IOException
    {
        copyStreamToFile( stream, destFile );
        if ( fileTime != -1 )
        {
            destFile.setLastModified( fileTime );
        }
    }
}
