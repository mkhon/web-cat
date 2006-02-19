/*==========================================================================*\
 |  $Id: TarArchiveHandler.java,v 1.1 2006/02/19 19:03:11 stedwar2 Exp $
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
import java.io.InputStream;

import net.sf.webcat.archives.AbstractArchiveHandler;
import net.sf.webcat.archives.IArchiveEntry;

//-------------------------------------------------------------------------
/**
 * An archive handler that unpacks uncompressed Unix TAR archives.
 * 
 * @author Tony Allowatt
 */
public class TarArchiveHandler extends AbstractArchiveHandler
{

    // ----------------------------------------------------------
	public boolean acceptsFile( String name )
	{
		return name.toLowerCase().endsWith( ".tar" );
	}


    // ----------------------------------------------------------
	public IArchiveEntry[] getContents( InputStream stream )
        throws IOException
	{
		return TarUtil.getContents( stream );
	}


    // ----------------------------------------------------------
	public void unpack( File destPath, InputStream stream )
	    throws IOException
	{
		TarUtil.unpack( destPath, stream );
	}
}
