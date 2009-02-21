/*==========================================================================*\
 |  $Id: JobQueue.java,v 1.2 2009/02/21 22:32:14 stedwar2 Exp $
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

package net.sf.webcat.jobqueue;

import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import net.sf.webcat.core.*;
import net.sf.webcat.dbupdate.*;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * This subsystem provides the infrastructure support for managing
 * database-backed job queues and worker threads for processing queued
 * jobs.  There is comprehensive support for managing distributed and
 * parallel execution of worker threads, coordinated through the
 * corresponding queues using the central database as the mediator.
 *
 * @author Stephen Edwards
 * @version $Id: JobQueue.java,v 1.2 2009/02/21 22:32:14 stedwar2 Exp $
 */
public class JobQueue
    extends Subsystem
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     */
    public JobQueue()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /* (non-Javadoc)
     * @see net.sf.webcat.core.Subsystem#init()
     */
    public void init()
    {
        super.init();

        try
        {
            java.net.InetAddress localMachine =
                java.net.InetAddress.getLocalHost();
            log.debug("local name = " + localMachine.getHostName());
            log.debug("canonical  = " + localMachine.getCanonicalHostName());
            log.debug("local addr = " + localMachine.getHostAddress());
        }
        catch (java.net.UnknownHostException e)
        {
            log.error("Error looking up local host info: " + e);
        }
        initialized = true;
    }


    // ----------------------------------------------------------
    /**
     * Registers a descriptor in the database, if it has not already been
     * registered.
     * @param context The editing context to use.
     * @param descriptorEntityName The entity name for the descriptor
     *        you want to register.
     * @param searchBindings a set of bindings that uniquely identify
     *        the descriptor you are trying to register.
     * @param initializationBindings a set of additional bindings (that will
     *        be added to the searchBindings) to set the initial field values
     *        when creating the descriptor, if one does not already exist.
     * @return The registered descriptor
     */
    @SuppressWarnings("unchecked")
	public static EOEnterpriseObject registerDescriptor(
        EOEditingContext        context,
        String                  descriptorEntityName,
        NSDictionary<String, ?> searchBindings,
        NSDictionary<String, ?> initializationBindings)
    {
        ensureInitialized();
        if (log.isDebugEnabled())
        {
            log.debug("registerDescriptor(" + descriptorEntityName + ", "
                + searchBindings + ")");
        }
        EOEnterpriseObject result = null;
        try
        {
            context.lock();
            NSArray descriptors = EOUtilities.objectsMatchingValues(
                context, descriptorEntityName, searchBindings);
            if (descriptors == null || descriptors.count() == 0)
            {
                try
                {
                    EOEnterpriseObject descriptor =
                        EOUtilities.createAndInsertInstance(
                            context, descriptorEntityName);
                    descriptor.reapplyChangesFromDictionary(
                    		(NSDictionary<String, Object>)searchBindings);
                    log.debug("descriptor not found: creating a new one");
                    if (initializationBindings != null)
                    {
                        descriptor.reapplyChangesFromDictionary(
                        	(NSDictionary<String, Object>)
                            initializationBindings);
                        if (log.isDebugEnabled())
                        {
                            log.debug(
                                "initialization = " + initializationBindings);
                        }
                    }
                    context.saveChanges();
                }
                catch (EOGeneralAdaptorException e)
                {
                    // Ignore it--most likely an optimistic locking failure
                    // on the id.
                }
            }
            descriptors = EOUtilities.objectsMatchingValues(
                context, descriptorEntityName, searchBindings);
            if (descriptors == null || descriptors.count() == 0)
            {
                log.error("failure registering "
                    + descriptorEntityName + searchBindings);
                Application.emailExceptionToAdmins(
                    null,
                    null,
                    "failure registering "
                    + descriptorEntityName
                    + searchBindings);
            }
            else
            {
            	if (log.isDebugEnabled())
            	{
            		log.debug(descriptors.count() == 1
            				  ? "one descriptor found"
            				  : "multiple descriptors found");
            	}
                while (descriptors.count() > 1)
                {
                    try
                    {
                        log.debug("attempting to delete descriptor: "
                            + descriptors.objectAtIndex(1));
                        context.deleteObject(
                            (EOEnterpriseObject)descriptors.objectAtIndex(1));
                        context.saveChanges();
                    }
                    catch (EOGeneralAdaptorException e)
                    {
                        log.debug("delete attempt stumbled");
                        // Assume delete failed because the object is already
                        // gone.
                        context.revert();
                        context.refaultAllObjects();
                    }
                    descriptors = EOUtilities.objectsMatchingValues(
                        context, descriptorEntityName, searchBindings);
                }
                result = (EOEnterpriseObject)descriptors.objectAtIndex(0);
            }
        }
        finally
        {
            context.unlock();
        }
        if (log.isDebugEnabled())
        {
            log.debug("registerDescriptor: result = " + result);
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Registers a descriptor in the database, if it has not already been
     * registered.
     * @param descriptorEntityName The entity name for the descriptor
     *        you want to register.
     * @param searchBindings a set of bindings that uniquely identify
     *        the descriptor you are trying to register.
     * @param initializationBindings a set of additional bindings (that will
     *        be added to the searchBindings) to set the initial field values
     *        when creating the descriptor, if one does not already exist.
     */
    public static void registerDescriptor(
        String                  descriptorEntityName,
        NSDictionary<String, ?> searchBindings,
        NSDictionary<String, ?> initializationBindings)
    {
        EOEditingContext ec = Application.newPeerEditingContext();
        registerDescriptor(
            ec, descriptorEntityName, searchBindings, initializationBindings);
        Application.releasePeerEditingContext(ec);
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    private static void ensureInitialized()
    {
        if (!initialized)
        {
            log.error(
                "JobQueue subsystem has not yet been initialized "
                + "(check subsystem dependencies)",
                new Throwable("called here"));
        }
    }


    //~ Instance/static variables .............................................

    private static boolean initialized = false;

    static Logger log = Logger.getLogger(JobQueue.class);
}
