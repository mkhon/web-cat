/*==========================================================================*\
 |  $Id: LoggedError.java,v 1.1 2007/07/08 01:40:13 stedwar2 Exp $
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

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;

// -------------------------------------------------------------------------
/**
 * Represents data about a specific logged exception trace, the source of
 * the error, and the number of times this specific error has occurred.
 *
 * @author
 * @version $Id: LoggedError.java,v 1.1 2007/07/08 01:40:13 stedwar2 Exp $
 */
public class LoggedError
    extends _LoggedError
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new LoggedError object.
     */
    public LoggedError()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve the object associated with a given Throwable, creating one
     * if none exists.  Assumes the given editing context has been locked.
     *
     * @param context The editing context to use
     * @param throwable The error to look up
     * @return the corresponding LoggedError object
     */
    public static LoggedError objectForException(
            EOEditingContext context,
            Throwable        throwable
        )
    {
        if ( throwable == null ) return null;

        // Drill down to the root cause first
        while ( throwable.getCause() != null )
        {
            throwable = throwable.getCause();
        }

        StackTraceElement[] trace = throwable.getStackTrace();
        StackTraceElement top = ( trace != null && trace.length > 0 )
            ? trace[0]
            : new StackTraceElement( "unknown", "unknown", "unknown", 0 );

        LoggedError result = null;
        NSArray results = objectsForExceptionLocation(
            context,
            top.getLineNumber(),
            top.getMethodName(),
            top.getClassName(),
            throwable.getClass().getName() );
        if ( results != null && results.count() > 0 )
        {
            result = (LoggedError)results.objectAtIndex( 0 );
        }
        else
        {
            result = (LoggedError)er.extensions.ERXEOControlUtilities
                .createAndInsertObject( context, ENTITY_NAME );
            result.setLine( top.getLineNumber() );
            result.setExceptionName( throwable.getClass().getName() );
            result.setInClass( top.getClassName() );
            result.setInMethod( top.getMethodName() );
        }
        return result;
    }
}
