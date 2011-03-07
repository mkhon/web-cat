/*==========================================================================*\
 |  $Id: WOEC.java,v 1.2 2011/03/07 18:44:37 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2011 Virginia Tech
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

package org.webcat.core;

import org.apache.log4j.Logger;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOObjectStore;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

// -------------------------------------------------------------------------
/**
 *  This is a specialized editing context subclass that is used to track
 *  down an obscure WO bug.
 *
 *  @author  Stephen Edwards
 *  @author  Last changed by $Author: stedwar2 $
 *  @version $Revision: 1.2 $, $Date: 2011/03/07 18:44:37 $
 */
public class WOEC
    extends er.extensions.eof.ERXEC
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new WOEC object.
     */
    public WOEC()
    {
        super();
        if (log.isDebugEnabled())
        {
            log.debug("creating new ec: " + hashCode());
        }
    }


    // ----------------------------------------------------------
    /**
     * Creates a new WOEC object.
     * @param os the parent object store
     */
    public WOEC( EOObjectStore os )
    {
        super( os );
        if (log.isDebugEnabled())
        {
            log.debug("creating new ec: " + hashCode());
        }
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void dispose()
    {
        if (log.isDebugEnabled())
        {
            log.debug("dispose(): " + hashCode());
        }
        super.dispose();
    }


    // ----------------------------------------------------------
    public static class WOECFactory
        extends er.extensions.eof.ERXEC.DefaultFactory
    {
        protected EOEditingContext _createEditingContext( EOObjectStore parent )
        {
            return new WOEC( parent == null
                             ? EOEditingContext.defaultParentObjectStore()
                             : parent );
        }
    }


    // ----------------------------------------------------------
    public static void installWOECFactory()
    {
        er.extensions.eof.ERXEC.setFactory( new WOECFactory() );
    }


    // ----------------------------------------------------------
    public static class PeerManager
    {
        // ----------------------------------------------------------
        public PeerManager(PeerManagerPool pool)
        {
            owner = pool;
            if (log.isDebugEnabled())
            {
                log.debug("creating manager: " + this);
            }
        }


        // ----------------------------------------------------------
        public EOEditingContext editingContext()
        {
            if (ec == null)
            {
                ec = Application.newPeerEditingContext();
                if (log.isDebugEnabled())
                {
                    log.debug("creating ec: " + ec.hashCode()
                        + " for manager: " + this);
                }
            }
            return ec;
        }


        // ----------------------------------------------------------
        public void dispose()
        {
            if (ec != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("disposing ec: " + ec.hashCode()
                        + " for manager: " + this);
                }
                Application.releasePeerEditingContext(ec);
                ec = null;
            }
            else
            {
                log.debug("dispose() called with null ec for manager: " + this);
            }
            if (transientState != null)
            {
                for (Object value : transientState.allValues())
                {
                    if (value instanceof EOManager.ECManager)
                    {
                        ((EOManager.ECManager)value).dispose();
                    }
                    else if (value instanceof EOEditingContext)
                    {
                        ((EOEditingContext)value).dispose();
                    }
                    else if (value instanceof PeerManager)
                    {
                        ((PeerManager)value).dispose();
                    }
                    else if (value instanceof PeerManagerPool)
                    {
                        ((PeerManagerPool)value).dispose();
                    }
                }
                transientState = null;
            }
        }


        // ----------------------------------------------------------
        public void sleep()
        {
            if (ec != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("sleep(): " + this);
                }
                if (cachePermanently)
                {
                    owner.cachePermanently( this );
                }
                else
                {
                    owner.cache( this );
                }
            }
        }


        // ----------------------------------------------------------
        public boolean cachePermanently()
        {
            return cachePermanently;
        }


        // ----------------------------------------------------------
        public void setCachePermanently(boolean value)
        {
            if (log.isDebugEnabled())
            {
                log.debug("setCachePermanently(" + value
                    + ") for manager: " + this);
            }
            cachePermanently = value;
        }


        // ----------------------------------------------------------
        /**
         * Retrieve an NSMutableDictionary used to hold transient settings for
         * this editing context (data that is not database-backed).
         * @return A map of transient settings
         */
        public NSMutableDictionary<String, Object> transientState()
        {
            if (transientState == null)
            {
                transientState = new NSMutableDictionary<String, Object>();
            }
            return transientState;
        }


        //~ Instance/static variables .........................................
        private EOEditingContext                    ec;
        private PeerManagerPool                     owner;
        private boolean                             cachePermanently;
        private NSMutableDictionary<String, Object> transientState;
        static Logger log = Logger.getLogger(
            PeerManager.class.getName().replace('$', '.'));
    }


    // ----------------------------------------------------------
    public static class PeerManagerPool
    {
        // ----------------------------------------------------------
        public PeerManagerPool()
        {

            managerCache = new NSMutableArray<PeerManager>();
            permanentManagerCache = new NSMutableArray<PeerManager>();
            if (log.isDebugEnabled())
            {
                log.debug("creating manager pool: " + this);
            }
        }


        // ----------------------------------------------------------
        public void cache(PeerManager manager)
        {
            if (log.isDebugEnabled())
            {
                log.debug("cache(" + manager + ")");
            }
            cache(manager, managerCache);
        }


        // ----------------------------------------------------------
        public void cachePermanently(PeerManager manager)
        {
            if (log.isDebugEnabled())
            {
                log.debug("cachePermanently(" + manager + ")");
            }
            managerCache.removeObject(manager);
            cache(manager, permanentManagerCache);
        }


        // ----------------------------------------------------------
        public void dispose()
        {
            log.debug("dispose()");
            dispose(managerCache);
            dispose(permanentManagerCache);
        }


        // ----------------------------------------------------------
        private void cache(
            PeerManager manager, NSMutableArray<PeerManager> cache)
        {
            int pos = cache.indexOfObject(manager);
            if (pos == NSArray.NotFound)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("caching: manager " + manager + " not in cache");
                }
                if (cache.count()
                    > Application.application().pageCacheSize())
                {
                    log.debug("caching: pool full, flushing oldest");
                    cache.objectAtIndex(0).dispose();
                    cache.removeObjectAtIndex(0);
                }
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("caching: manager " + manager
                        + " found at pos " + pos);
                }
                cache.remove(pos);
            }
            if (log.isDebugEnabled())
            {
                log.debug("caching: manager " + manager
                    + " placed at pos " + cache.count());
            }
            cache.add(manager);
        }


        // ----------------------------------------------------------
        private void dispose(NSMutableArray<PeerManager> cache)
        {
            for (PeerManager manager : cache)
            {
                manager.dispose();
            }
            cache.clear();
        }


        //~ Instance/static variables .........................................
        private NSMutableArray<PeerManager> managerCache;
        private NSMutableArray<PeerManager> permanentManagerCache;
        static Logger log = Logger.getLogger(
            PeerManagerPool.class.getName().replace('$', '.'));
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( WOEC.class );
}
