/*==========================================================================*\
 |  $Id: TokenDispenser.java,v 1.1 2009/11/17 18:10:36 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2009 Virginia Tech
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

package net.sf.webcat.jobqueue;

import java.util.*;
import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * This class is used as a wait queue for worker threads, and allows
 * controlled dispensing of job tokens to worker threads.  A "token" is
 * nothing more than granting permission to execute.  This class is
 * based off the original GraderQueue class in the Grader
 * subsystem.
 *
 * @author Stephen Edwards
 * @author Last changed by $Author: stedwar2 $
 * @version $Revision: 1.1 $, $Date: 2009/11/17 18:10:36 $
 */
public class TokenDispenser
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Default constructor
     */
    public TokenDispenser()
    {
        tokens = 0;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve the next available token, blocking until one is available.
     */
    public synchronized void getJobToken()
    {
        while (tokens == 0)
        {
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
                log.error("client was interrupted while waiting for a token.");
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Add a token to the dispenser.
     */
    public synchronized void depositToken()
    {
        tokens++;
        notify();
    }


    // ----------------------------------------------------------
    /**
     * Add multiple tokens to the dispenser.
     *
     * @param count The number of tokens to add.
     */
    public synchronized void depositTokens(int count)
    {
        while (count > 0)
        {
            depositToken();
            count--;
        }
    }


    //~ Instance/static variables .............................................

    int tokens;
    static Logger log = Logger.getLogger( TokenDispenser.class );
}
