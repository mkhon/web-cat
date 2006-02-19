/*==========================================================================*\
 |  $Id: Main.java,v 1.1 2006/02/19 19:03:11 stedwar2 Exp $
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

package net.sf.webcat.archives.internal;

import java.io.File;
import java.io.IOException;

import net.sf.webcat.archives.ArchiveManager;
import net.sf.webcat.archives.IArchiveEntry;

//-------------------------------------------------------------------------
/**
 * A simple main program that demonstrates how the ArchiveManager works.
 * 
 * @author Tony Allowatt
 */
public class Main
{
    // ----------------------------------------------------------
	public static void main( String[] args )
	{
		String TEST_FILE = "D:/test.zip";
		String TEST_DESTINATION = "D:/ziptest";

		ArchiveManager manager = ArchiveManager.getInstance();
		manager.addHandler( new ZipArchiveHandler()   );
		manager.addHandler( new TarGzArchiveHandler() );
		manager.addHandler( new TarArchiveHandler()   );

		try
		{
			File zipFile = new File( TEST_FILE );

			IArchiveEntry[] entries = manager.getContents( zipFile );
			
			for ( int i = 0; i < entries.length; i++ )
			{
				System.out.print( entries[i].getName() );
				System.out.print( "  Dir  = " + entries[i].isDirectory() );
				System.out.print( "  Size = " + entries[i].length() );
				System.out.print(
                    "  Date = " + entries[i].lastModified().toString() );
				System.out.println();
			}

			File destPath = new File( TEST_DESTINATION );
			destPath.mkdir();
			
			manager.unpack( destPath, zipFile );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}
}
