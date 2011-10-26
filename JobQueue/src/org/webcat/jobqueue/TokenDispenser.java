/*==========================================================================*\
 |  $Id: TokenDispenser.java,v 1.3 2011/10/26 15:24:30 stedwar2 Exp $
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

package org.webcat.jobqueue;

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
 * @author  Stephen Edwards
 * @author  Last changed by $Author: stedwar2 $
 * @version $Revision: 1.3 $, $Date: 2011/10/26 15:24:30 $
 */
public class TokenDispenser
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Default constructor
     */
    public TokenDispenser(int initialCount)
    {
        tokens = initialCount;
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
        tokens--;
        log.debug("releasing token to worker thread from " + this +
                " (" + tokens + " tokens remain)");
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
    public synchronized void depositTokens(long count)
    {
        while (count > 0)
        {
            depositToken();
            count--;
        }
    }


    // ----------------------------------------------------------
    /**
     * Add multiple tokens to the dispenser to ensure at least N are
     * available.
     *
     * @param N The minimum number of tokens to guarantee are present.
     *          If fewer are in the dispenser, the difference will be added.
     */
    public synchronized void ensureAtLeastNTokens(int n)
    {
        int amount = n - tokens;
        log.debug("depositing " + amount + " tokens in " + this
            + " (already holding " + tokens + " tokens)");
        depositTokens(amount);
    }


    //~ Instance/static variables .............................................

    int tokens;
    static Logger log = Logger.getLogger( TokenDispenser.class );
}
