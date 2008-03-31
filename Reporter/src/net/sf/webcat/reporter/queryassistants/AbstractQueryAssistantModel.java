/*==========================================================================*\
 |  $Id: AbstractQueryAssistantModel.java,v 1.2 2008/03/31 02:28:38 stedwar2 Exp $
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

package net.sf.webcat.reporter.queryassistants;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSKeyValueCoding;

//-------------------------------------------------------------------------
/**
 * This interface defines the two methods that any query assistant model must
 * implement, in order to translate the model's internal state to and from
 * EOModel qualifiers that are stored in the query database.
 *
 * @author aallowat
 * @version $Id: AbstractQueryAssistantModel.java,v 1.2 2008/03/31 02:28:38 stedwar2 Exp $
 */
public abstract class AbstractQueryAssistantModel
    implements NSKeyValueCoding
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
	/**
	 * This method converts the specified qualifier into whatever internal
	 * state model the query assistant model requires. If the conversion is not
	 * possible (i.e., the qualifier came from a different query assistant and
	 * this one does not know how to handle it), then the model should either
	 * try to interpret as much of the qualifier as it can, or simply
	 * initialize itself to defaults.
	 *
	 * The model should NOT try to preserve qualifiers that it does not
	 * understand and reconstitute them in the qualifierFromValues method. This
	 * would cause user confusion as the user interface for a particular
	 * query assistant would have no way of displaying or modifying these
	 * "hidden" qualifiers.
	 *
	 * @param qualifier the qualifier to convert to the model
	 */
	public abstract void takeValuesFromQualifier(EOQualifier qualifier);


    // ----------------------------------------------------------
	/**
	 * This method converts the internal state of the query assistant model
	 * into a qualifier that can be stored in the database.
	 *
	 * @return a qualifier that represents the internal state of this query
	 *     assistant
	 */
	public abstract EOQualifier qualifierFromValues();


    // ----------------------------------------------------------
	public void takeValueForKey(Object value, String key)
	{
		NSKeyValueCoding.DefaultImplementation.takeValueForKey(
            this, value, key);
	}


    // ----------------------------------------------------------
	public Object valueForKey(String key)
	{
		return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
	}
}
