/*==========================================================================*\
 |  $Id: WCBatchNavigator.java,v 1.5 2007/02/27 14:41:20 stedwar2 Exp $
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
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import er.extensions.ERXConstant;
import java.util.*;
import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 *  A custom version of a batch navigator that has a different look than
 *  the one in WOExtensions.
 *
 *  @author Stephen Edwards
 *  @version $Id: WCBatchNavigator.java,v 1.5 2007/02/27 14:41:20 stedwar2 Exp $
 */
public class WCBatchNavigator
    extends WCComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new WOBatchNavigationBar object.
     * 
     * @param aContext the context
     */
    public WCBatchNavigator( WOContext aContext )
    {
        super( aContext );
    }


    //~ Methods ...............................................................


    // ----------------------------------------------------------
    /* (non-Javadoc)
     * @see net.sf.webcat.core.WCComponentWithErrorMessages#appendToResponse(com.webobjects.appserver.WOResponse, com.webobjects.appserver.WOContext)
     */
    public void appendToResponse( WOResponse response, WOContext context )
    {
        if ( hasBinding( "persistentKey" )
             && hasSession() )
        {
            String key = (String)valueForBinding( "persistentKey" );
            Object o = ( (Session)session() ).user().preferences()
                .valueForKey( key );
            if ( o != null && o instanceof Integer )
            {
                log.debug( "appendToResponse(): key " + key + " = " + o );
                setNumberOfObjectsPerBatchIfNecessary(
                    ( (Integer)o ).intValue() );
            }
        }
        super.appendToResponse( response, context );
    }


    // ----------------------------------------------------------
    /**
     * Set the page number (batch number) to display.
     * 
     * @param index The page number
     */
    public void setBatchIndex( Integer index )
    {
        log.debug( "setBatchIndex(" + index + ")" );
        int batchIndex;

        //Treat a null index as a 0 index. Negative numbers are handled
        //by the display group.
        batchIndex = ( index != null )
                          ? index.intValue()
                          : 0;
        ( (WODisplayGroup)valueForBinding( "displayGroup" ) ).
            setCurrentBatchIndex( batchIndex );
    }


    // ----------------------------------------------------------
    private void setNumberOfObjectsPerBatchIfNecessary( int number )
    {
        WODisplayGroup group =
            ( (WODisplayGroup)valueForBinding( "displayGroup" ) );
        int curSize = group.numberOfObjectsPerBatch();
        log.debug( "setNumberOfObjectsPerBatchIfNecessary(" + number
            + "), was " + curSize );
        if ( curSize != number )
        {
            // index is the one-based index of the first object shown in
            // the current batch
            int index = ( group.currentBatchIndex() - 1 ) * curSize + 1;
            if ( number <= 0 )
            {
                group.setNumberOfObjectsPerBatch( number );
            }
            else
            {
                // newPage is the one-based batch number that will show
                // the object at the given index
                int newBatch = index / number + 1;
                group.setNumberOfObjectsPerBatch( number );
                group.setCurrentBatchIndex( newBatch );
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Set the number of objects shown on each page/batch.
     * 
     * @param number The number of objects to show
     */
    public void setNumberOfObjectsPerBatch( Integer number )
    {
        log.debug( "setNumberOfObjectsPerBatch(" + number + ")" );
        int num;

        //If a negative number is provided we default the number
        //of objects per batch to 0.
        num = ( ( number != null ) && ( number.intValue() > 0 ) )
                      ? number.intValue()
                      : 0;
        setNumberOfObjectsPerBatchIfNecessary( num );
        if ( hasBinding( "persistentKey" )
             && hasSession() )
        {
            String key = (String)valueForBinding( "persistentKey" );
            User user = ( (Session)session() ).user();
            EOEditingContext ec = Application.newPeerEditingContext();
            log.debug( "setNumberOfObjectsPerBatch(): key " + key + " <- "
                + num + "(" + number + ")" );
            ec.lock();
            try
            {
                // Use a separate EC to store the changed preferences
                User me = (User)EOUtilities.localInstanceOfObject( ec, user );
                me.preferences().takeValueForKey(
                    ( number == null )
                    ? ERXConstant.integerForInt( num )
                    : number, key );
                ec.saveChanges();
                // Now refresh the session's user object so that it loads
                // this saved preferences value
                user.editingContext().refreshObject( user );
            }
            finally
            {
                ec.unlock();
            }
        }
    }


    // ----------------------------------------------------------
    public boolean isStateless()
    {
        return true;
    }


    // ----------------------------------------------------------
    /**
     * Access the bound display group's current batch position.
     * 
     * @return The current batch index
     */
    public int batchIndex()
    {
        return ( (WODisplayGroup)valueForBinding( "displayGroup" ) ).
            currentBatchIndex();
    }


    // ----------------------------------------------------------
    /**
     * The current number of objects displayed for each page/batch.
     * 
     * @return The number of objects per batch
     */
    public int numberOfObjectsPerBatch()
    {
        return ( (WODisplayGroup)valueForBinding( "displayGroup" ) ).
            numberOfObjectsPerBatch();
    }


    // ----------------------------------------------------------
    /**
     * Action for the "go" button in the batch navigator, which simply
     * returns true to reload the current page.  It also takes care of
     * storing the batch navigation settings if an appropriate key is
     * defined.
     * @return null, to reload the current page
     */
    public WOComponent go()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "go(): batch = " + batchIndex() + ", size = "
                + numberOfObjectsPerBatch() );
        }
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Action for the "fewer" button in the batch navigator, which simply
     * returns true to reload the current page.  It also takes care of
     * setting the batch size to less than the total number of objects
     * displayed, to force paging.
     * @return null, to reload the current page
     */
    public WOComponent fewer()
    {
        int num = ( (WODisplayGroup)valueForBinding( "displayGroup" ) ).
            allObjects().count() / 2;
        if ( num == 0 )
        {
            num++;
        }
        setNumberOfObjectsPerBatch( ERXConstant.integerForInt( num ) );
        if ( log.isDebugEnabled() )
        {
            log.debug( "fewer(): now, batch = " + batchIndex() + ", size = "
                + numberOfObjectsPerBatch() );
        }
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Determine if the batch navigator should be able to show a button
     * for reducing the batch size.
     * @return true if the associated display group can show more than
     * 1 object
     */
    public boolean canShowFewer()
    {
        return ( (WODisplayGroup)valueForBinding( "displayGroup" ) ).
            allObjects().count() > 1;
    }


    //~ Instance/static variables .............................................

    static Logger log = Logger.getLogger( WCBatchNavigator.class );
}
