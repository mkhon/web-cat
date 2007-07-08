/*==========================================================================*\
 |  $Id: EditTimeZoneName.java,v 1.1 2007/07/08 01:52:22 stedwar2 Exp $
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

package net.sf.webcat.admin.d2w;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import net.sf.webcat.core.*;

//-------------------------------------------------------------------------
/**
 * A customized edit component for selecting time zone names.
 *
 *  @author edwards
 *  @version $Id: EditTimeZoneName.java,v 1.1 2007/07/08 01:52:22 stedwar2 Exp $
 */
public class EditTimeZoneName
    extends er.directtoweb.ERDCustomEditComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new object.
     *
     * @param context The context to use
     */
    public EditTimeZoneName( WOContext context )
    {
        super( context );
    }


    //~ KVC Attributes (must be public) .......................................

    public AuthenticationDomain.TimeZoneDescriptor zone;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public AuthenticationDomain.TimeZoneDescriptor selectedZone()
    {
        if ( selectedZone == null )
        {
            NSArray zones = AuthenticationDomain.availableTimeZones();
            String name = (String)objectPropertyValue();
            for ( int i = 0; i < zones.count(); i++ )
            {
                AuthenticationDomain.TimeZoneDescriptor descriptor =
                    (AuthenticationDomain.TimeZoneDescriptor)zones
                        .objectAtIndex( i );
                System.out.println( "checking " + name + " against "
                    + descriptor.id );
                if ( descriptor.id.equals( name ) )
                {
                    selectedZone = descriptor;
                    break;
                }
            }
        }
        return selectedZone;
    }


    // ----------------------------------------------------------
    public void setSelectedZone( AuthenticationDomain.TimeZoneDescriptor aZone )
    {
        selectedZone = aZone;
        setObjectPropertyValue( aZone.id );
    }


    //~ Instance/static variables .............................................

    private AuthenticationDomain.TimeZoneDescriptor selectedZone;
}
