/*==========================================================================*\
 |  $Id: ManagedWorkerDescriptor.java,v 1.2 2008/10/29 14:13:49 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2008 Virginia Tech
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

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import er.extensions.eof.ERXConstant;
import java.util.Enumeration;
import org.apache.log4j.Logger;
import net.sf.webcat.core.IndependentEOManager;

// -------------------------------------------------------------------------
/**
 * A subclass of IndependentEOManager that holds one {@link WorkerDescriptor}.
 *
 * @author stedwar2
 * @version $Id: ManagedWorkerDescriptor.java,v 1.2 2008/10/29 14:13:49 aallowat Exp $
 */
public  class ManagedWorkerDescriptor
    extends IndependentEOManager
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     * @param descriptor the worker descriptor to wrap
     */
    public ManagedWorkerDescriptor(WorkerDescriptor descriptor)
    {
        super(descriptor);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>idOnHost</code> value.
     * @return the value of the attribute
     */
    public int idOnHost()
    {
        Number result =
            (Number)valueForKey(WorkerDescriptor.ID_ON_HOST_KEY);
        return (result == null)
            ? 0
            : result.intValue();
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>idOnHost</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setIdOnHost(int value)
    {
        takeValueForKey(
            ERXConstant.integerForInt( value ),
            WorkerDescriptor.ID_ON_HOST_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>isAllocated</code> value.
     * @return the value of the attribute
     */
    public boolean isAllocated()
    {
        Number result =
            (Number)valueForKey(WorkerDescriptor.IS_ALLOCATED_KEY);
        return (result == null)
            ? false
            : (result.intValue() > 0);
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>isAllocated</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setIsAllocated(boolean value)
    {
        takeValueForKey(
            ERXConstant.integerForInt(value ? 1 : 0),
            WorkerDescriptor.IS_ALLOCATED_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve this object's <code>isRunning</code> value.
     * @return the value of the attribute
     */
    public boolean isRunning()
    {
        Number result =
            (Number)valueForKey(WorkerDescriptor.IS_RUNNING_KEY);
        return (result == null)
            ? false
            : (result.intValue() > 0);
    }


    // ----------------------------------------------------------
    /**
     * Change the value of this object's <code>isRunning</code>
     * property.
     *
     * @param value The new value for this property
     */
    public void setIsRunning(boolean value)
    {
        takeValueForKey(
            ERXConstant.integerForInt(value ? 1 : 0),
            WorkerDescriptor.IS_RUNNING_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>host</code>
     * relationship.
     * @return the entity in the relationship
     */
    public HostDescriptor host()
    {
        return (HostDescriptor)valueForKey(WorkerDescriptor.HOST_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>host</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setHostRelationship(HostDescriptor value)
    {
        addObjectToBothSidesOfRelationshipWithKey(
            value, WorkerDescriptor.HOST_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the entity pointed to by the <code>queue</code>
     * relationship.
     * @return the entity in the relationship
     */
    public QueueDescriptor queue()
    {
        return (QueueDescriptor)valueForKey(WorkerDescriptor.QUEUE_KEY);
    }


    // ----------------------------------------------------------
    /**
     * Set the entity pointed to by the <code>queue</code>
     * relationship.  This method is a type-safe version of
     * <code>addObjectToBothSidesOfRelationshipWithKey()</code>.
     *
     * @param value The new entity to relate to
     */
    public void setQueueRelationship(QueueDescriptor value)
    {
        addObjectToBothSidesOfRelationshipWithKey(
            value, WorkerDescriptor.QUEUE_KEY);
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( WorkerDescriptor.class );
}
