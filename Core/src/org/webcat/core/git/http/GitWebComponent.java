/*==========================================================================*\
 |  $Id: GitWebComponent.java,v 1.1 2011/05/13 19:46:57 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2011 Virginia Tech
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

package org.webcat.core.git.http;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.webcat.core.WCComponent;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

//-------------------------------------------------------------------------
/**
 * A base page component from which all Git repository viewing pages can
 * inherit.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2011/05/13 19:46:57 $
 */
public abstract class GitWebComponent extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public GitWebComponent(WOContext context)
    {
        super(context);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public GitWebContext gitContext()
    {
        return gitContext;
    }


    // ----------------------------------------------------------
    public void setGitContext(GitWebContext gitContext)
    {
        this.gitContext = gitContext;
    }


    //~ Static/instance variables .............................................

    private GitWebContext gitContext;
}
