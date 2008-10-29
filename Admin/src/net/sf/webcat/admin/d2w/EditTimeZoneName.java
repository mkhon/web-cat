/*==========================================================================*\
 |  $Id: EditTimeZoneName.java,v 1.3 2008/10/29 14:16:31 aallowat Exp $
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
 *  @version $Id: EditTimeZoneName.java,v 1.3 2008/10/29 14:16:31 aallowat Exp $
 */
public class EditTimeZoneName
    extends er.directtoweb.components.ERDCustomEditComponent
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
