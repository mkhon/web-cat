/*==========================================================================*\
 |  $Id: SensorDataType.java,v 1.2 2015/05/29 03:54:08 jluke13 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2012 Virginia Tech
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

package org.webcat.deveventtracker;

import com.webobjects.eocontrol.EOEditingContext;

// -------------------------------------------------------------------------
/**
 * TODO: place a real description here.
 *
 * @author
 * @author  Last changed by: $Author: jluke13 $
 * @version $Revision: 1.2 $, $Date: 2015/05/29 03:54:08 $
 */
public class SensorDataType
    extends _SensorDataType
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new SensorDataType object.
     */
    public SensorDataType()
    {
        super();
    }


    //~ Methods ...............................................................
    /**
     * Returns the SensorDataType associated with the given string (one of the public static Strings declared in this class),
     * or creates it and returns it if necessary.
     * @param ec The EOEditingContext to use.
     * @param type The name of the SensorDataType to look up/create (one of the public static Strings in this class).
     * @return The SensorDataType corresponding to the given name.
     */
    public static SensorDataType getSensorDataType(EOEditingContext ec, String type)
    {
    	SensorDataType storedType = SensorDataType.uniqueObjectMatchingQualifier(ec, SensorDataType.name.is(type));
    	if(storedType != null)
    	{
    		return storedType;
    	}
    	storedType = SensorDataType.create(ec, type);
    	ec.saveChanges();
    	return storedType;
    }
}
