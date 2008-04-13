/*==========================================================================*\
 |  $Id: IKeyLabelProvider.java,v 1.2 2008/04/13 22:04:53 aallowat Exp $
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

import org.eclipse.swt.graphics.Image;

//------------------------------------------------------------------------
/**
 * A label provider for the KeyBrowser widget.
 *
 * @author Tony Allevato (Virginia Tech Computer Science)
 * @version $Id: IKeyLabelProvider.java,v 1.2 2008/04/13 22:04:53 aallowat Exp $
 */
public interface IKeyLabelProvider
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets a text label that represents the key of the specified class.
     *
     * @param className
     *            the name of the class that contains the key
     * @param key
     *            the key
     *
     * @return the label to display for the key
     */
    String getLabel(String className, String key);


    // ----------------------------------------------------------
    /**
     * Gets an image that represents the key of the specified class.
     *
     * @param className
     *            the name of the class that contains the key
     * @param key
     *            the key
     *
     * @return the image to display for the key
     */
    Image getImage(String className, String key);
}
