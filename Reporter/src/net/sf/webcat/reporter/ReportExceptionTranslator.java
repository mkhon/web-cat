/*==========================================================================*\
 |  $Id: ReportExceptionTranslator.java,v 1.3 2009/05/27 14:31:52 aallowat Exp $
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

import java.io.PrintWriter;
import java.io.StringWriter;
import net.sf.webcat.core.MutableArray;
import net.sf.webcat.core.MutableDictionary;
import org.eclipse.birt.core.exception.BirtException;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableSet;
import er.extensions.eof.ERXConstant;

// ------------------------------------------------------------------------
/**
 * Utility methods to translate exceptions that occur during report generation
 * and rendering into an array of dictionaries with "severity", "cause", and
 * "message" keys.
 *
 * @author Tony Allevato
 * @version $Id: ReportExceptionTranslator.java,v 1.3 2009/05/27 14:31:52 aallowat Exp $
 */
public class ReportExceptionTranslator
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Prevent instantiation.
     */
    private ReportExceptionTranslator()
    {
        // Static class; prevent instantiation.
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Translates a single exception into a {@link MutableDictionary}
     * containing information about the exception.
     *
     * @param e the exception to translate
     * @return a dictionary containing information about the exception
     */
    public static MutableDictionary translateException(Exception e)
    {
        MutableDictionary errorInfo = new MutableDictionary();

        if (e instanceof BirtException)
        {
            errorInfo.setObjectForKey(ERXConstant
                    .integerForInt(((BirtException) e).getSeverity()),
                    "severity");
        }
        else
        {
            errorInfo.setObjectForKey(ERXConstant
                    .integerForInt(BirtException.ERROR), "severity");
        }

        MutableArray chain = new MutableArray();
        errorInfo.setObjectForKey(chain, "chain");

        Throwable t = e;

        if (t != null)
        {
            boolean done = false;
            String lastMessage = null;
            NSMutableSet<Throwable> seenExceptions =
                new NSMutableSet<Throwable>();
    
            do
            {
                MutableDictionary exInfo = new MutableDictionary();
                exInfo.setObjectForKey(t.getClass().getName(), "type");
                
                String msg = t.getMessage();
                if (msg != null && !msg.equals(lastMessage))
                {
                    lastMessage = msg;
                    exInfo.setObjectForKey(msg, "message");
                }
                
                StringWriter writer = new StringWriter();
                t.printStackTrace(new PrintWriter(writer));
                writer.flush();
                exInfo.setObjectForKey(writer.toString(), "stackTrace");

                chain.addObject(exInfo);
                seenExceptions.addObject(t);

                Throwable lastT = t;
                t = t.getCause();
    
                done = (t == null || t == lastT
                        || seenExceptions.containsObject(t));
            }
            while (!done);
        }

/*        if (e.getCause() != null)
        {
            if (e.getCause().getMessage() != null)
            {
                errorInfo.setObjectForKey(e.getCause().getMessage(),
                        "cause");
            }
            else
            {
                errorInfo.setObjectForKey(e.getCause().toString(), "cause");
            }
        }

        if (e.getMessage() != null)
        {
            errorInfo.setObjectForKey(e.getMessage(), "message");
        }
*/
        return errorInfo;
    }


    // ----------------------------------------------------------
    /**
     * Translates an array of exceptions into a {@link MutableArray} that
     * contains a {@link MutableDictionary} with information about each
     * exception.
     *
     * @param exceptions the array of exceptions to translate
     * @return the array of dictionaries containing exception information
     */
    public static MutableArray translateExceptions(NSArray<Exception> exceptions)
    {
        if (exceptions == null)
        {
            return new MutableArray();
        }

        MutableArray array = new MutableArray();

        for (Exception e : exceptions)
        {
            MutableDictionary errorInfo = translateException(e);
            array.addObject(errorInfo);
        }

        return array;
    }
}
