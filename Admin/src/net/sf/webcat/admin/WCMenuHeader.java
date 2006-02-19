/*==========================================================================*\
 |  $Id: WCMenuHeader.java,v 1.1 2006/02/19 19:06:50 stedwar2 Exp $
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

package net.sf.webcat.admin;

import com.webobjects.appserver.*;
import com.webobjects.directtoweb.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.sf.webcat.core.*;

//-------------------------------------------------------------------------
/**
 * The menu header for all of our direct-to-web pages.
 *
 *  @author Stephen Edwards
 *  @version $Id: WCMenuHeader.java,v 1.1 2006/02/19 19:06:50 stedwar2 Exp $
 */
public class WCMenuHeader
    extends WOComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new WCInspectPageTemplate object.
     * 
     * @param aContext The context to use
     */
    public WCMenuHeader( WOContext aContext )
    {
        super( aContext );
    }
    

    //~ KVC Attributes (must be public) .......................................

    public String  entityNameInList;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public String manipulatedEntityName()
    {
        if ( _manipulatedEntityName == null )
        {
            WOComponent currentPage = context().page();
            _manipulatedEntityName =
                D2W.factory().entityNameFromPage( currentPage );
        }
        return _manipulatedEntityName;
    }


    // ----------------------------------------------------------
    public void setManipulatedEntityName( String newValue )
    {
        _manipulatedEntityName = newValue;
    }
    

    // ----------------------------------------------------------
    public NSArray visibleEntityNames()
    {
        return D2W.factory().visibleEntityNames( session() );
    }


    // ----------------------------------------------------------
    public WOComponent findEntityAction()
    {
        QueryPageInterface newQueryPage = D2W.factory()
            .queryPageForEntityNamed( _manipulatedEntityName, session() );
        return (WOComponent)newQueryPage;
    }


    // ----------------------------------------------------------
    public WOComponent newObjectAction()
    {
        WOComponent nextPage = null;
        try {
            EditPageInterface epi = D2W.factory()
                .editPageForNewObjectWithEntityNamed( _manipulatedEntityName,
                                                      session() );
            epi.setNextPage( context().page() );
            nextPage = (WOComponent)epi;
        }
        catch ( IllegalArgumentException e )
        {
            ErrorPageInterface epf = D2W.factory().errorPage( session() );
            epf.setMessage( e.toString() );
            epf.setNextPage( context().page() );
            nextPage = (WOComponent)epf;
        }
        return nextPage;
    }


    // ----------------------------------------------------------
    public WOComponent queryAllAction()
    {
        return D2W.factory().defaultPage( session() );
    }


    // ----------------------------------------------------------
    public WOComponent homeAction()
    {
        return pageWithName(
            ( (Session)session() ).tabs.selectDefault().pageName() );
    }


    //~ Instance/static variables .............................................

    private String _manipulatedEntityName;
}
