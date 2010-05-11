/*==========================================================================*\
 |  $Id: BatchProcessor.java,v 1.1 2010/05/11 14:51:46 aallowat Exp $
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

package org.webcat.batchprocessor;

import java.util.Hashtable;
import java.util.Map;
import org.apache.log4j.Logger;
import org.webcat.core.Application;
import org.webcat.core.EntityResourceRequestHandler;
import org.webcat.core.Subsystem;
import org.webcat.grader.EnqueuedJob;
import org.webcat.grader.Grader;
import org.webcat.jobqueue.QueueDescriptor;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import er.extensions.eof.ERXEOControlUtilities;
import er.extensions.eof.ERXQ;

//-------------------------------------------------------------------------
/**
 * The primary class of the BatchProcessor subsystem.
 *
 * @author Tony Allevato
 * @version $Id: BatchProcessor.java,v 1.1 2010/05/11 14:51:46 aallowat Exp $
 */
public class BatchProcessor extends Subsystem
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new BatchProcessor subsystem object.
     */
    public BatchProcessor()
    {
        super();

        instance = this;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void init()
    {
        super.init();

        // Register the batch processor subsystem's messages.

        // TODO add messages

        // Register the BatchResult resource handler.

        EntityResourceRequestHandler.registerHandler(BatchResult.class,
                new BatchResultResourceHandler());

        // Register the batch job queue and create worker threads.

        QueueDescriptor.registerQueue(BatchJob.ENTITY_NAME);
    }


    // ----------------------------------------------------------
    public void start()
    {
        log.info("Starting BatchJob worker thread");

        new BatchWorkerThread().start();
    }


    // ----------------------------------------------------------
    /**
     * Returns the sole instance of the reporter subsystem.
     *
     * @return the Reporter object that represents the subsystem.
     */
    public static BatchProcessor getInstance()
    {
        return instance;
    }


    //~ Instance/static variables .............................................

    /**
     * This is the sole instance of the batch processor subsystem, initialized
     * by the constructor.
     */
    private static BatchProcessor instance;

    private static Logger log = Logger.getLogger( BatchProcessor.class );
}
