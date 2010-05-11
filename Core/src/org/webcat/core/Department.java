/*==========================================================================*\
 |  $Id: Department.java,v 1.1 2010/05/11 14:51:55 aallowat Exp $
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

package org.webcat.core;

import org.webcat.core._Department;
import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;

import sun.security.krb5.internal.*;

// -------------------------------------------------------------------------
/**
 * Represents one department within an institution.
 *
 * @author Stephen Edwards
 * @version $Id: Department.java,v 1.1 2010/05/11 14:51:55 aallowat Exp $
 */
public class Department
    extends _Department
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new Department object.
     */
    public Department()
    {
        super();
    }


    //~ Methods ...............................................................


    // ----------------------------------------------------------
    /**
     * Get a short (no longer than 60 characters) description of this
     * department.
     * @return the description
     */
    public String userPresentableDescription()
    {
        return abbreviation() + " (" + institution() + ")";
    }
}
