/*==========================================================================*\
 |  $Id: StudentProject.java,v 1.3 2015/05/21 08:15:29 jluke13 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2012 Virginia Tech
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

package org.webcat.deveventtracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.webcat.core.RepositoryProvider;
import org.webcat.core.User;

// -------------------------------------------------------------------------
/**
 * TODO: place a real description here.
 *
 * @author
 * @author  Last changed by: $Author: jluke13 $
 * @version $Revision: 1.3 $, $Date: 2015/05/21 08:15:29 $
 */
public class StudentProject
    extends _StudentProject
    implements RepositoryProvider
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new StudentProject object.
     */
    public StudentProject()
    {
        super();
    }

    //~ Methods ...............................................................

	public void initializeRepositoryContents(File file) throws IOException {
		File readme = new File(file, "/readme.txt");
		readme.createNewFile();
		FileWriter fw = new FileWriter(readme);
		BufferedWriter out = new BufferedWriter(fw);
		out.write("This repository is used for storing student code snapshots as they work. There is one repository per Eclipse project they work on");
		out.flush();
		out.close();
	}

	public boolean userCanAccessRepository(User user) {
		return this.accessibleByUser(user);
	}
	
	public boolean accessibleByUser(User user)
	{
		return this.students().contains(user);
	}
	
    public String apiId()
    {
        return this.uuid();
    }
}
