/*==========================================================================*\
 |  $Id: LongResponse.java,v 1.1 2007/04/04 02:06:23 stedwar2 Exp $
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

package net.sf.webcat.core;

import com.webobjects.appserver.*;

import er.extensions.*;

//-------------------------------------------------------------------------
/**
 * A version of the {@link ERXLongResponse} component that conditionally
 * displays either a progress bar (or just a spinning icon) while a
 * {@link ERXLongResponseTask} or {@link LongResponseTaskWithProgress} is
 * working, or its nested component content if the long response task has
 * completed.
 *
 * @author Stephen Edwards
 * @version $Id: LongResponse.java,v 1.1 2007/04/04 02:06:23 stedwar2 Exp $
 */
public class LongResponse
    extends ERXLongResponse
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Default constructor.
     * @param context The page's context
     */
    public LongResponse( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public String message      = "Working";
    
    // These two keys ensure that the task is only checked once, so that
    // all conditionals in the template always return consistent results,
    // even if the task finishes part-way through generation of this page
    public boolean isDone      = false;
    public boolean isCancelled = false;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse( WOResponse response, WOContext context )
    {
        if ( task() != null )
        {
            isDone = task().isDone();
            isCancelled =
                ( (LongResponseTaskWithProgress)task() ).isCancelled();
        }
        super.appendToResponse( response, context );
    }
}
