/*==========================================================================*\
 |  $Id: IKeyProvider.java,v 1.1 2008/04/08 18:31:11 aallowat Exp $
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

package net.sf.webcat.oda.designer.widgets;

/**
 *
 * @author Tony Allevato
 */
public interface IKeyProvider
{
    // -----------------------------------------------------------------------
    /**
     * Gets a value indicating whether or not the specified class has any keys.
     *
     * @param className
     *            The name of the class to check for keys.
     * @return true if the class has keys; false if it is primitive or
     *         undefined.
     */
    boolean hasKeys(String className);


    // -----------------------------------------------------------------------
    /**
     * Gets an array of key names that belong to the specified class.
     *
     * @param className
     *            The name of the class whose keys should be retrieved.
     * @return an array of Strings denoting the key names, or null if this type
     *         is a primitive or invalid type
     */
    String[] getKeys(String className);


    // -----------------------------------------------------------------------
    /**
     * Gets the string representation of the type of a key on the specified
     * class.
     *
     * @param className
     *            The name of the class to which the key belongs.
     * @param key
     *            The key whose type should be retrieved.
     * @return a String denoting the type of the key
     */
    String getKeyType(String className, String key);
}
