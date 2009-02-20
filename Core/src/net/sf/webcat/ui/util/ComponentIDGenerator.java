/*==========================================================================*\
 |  $Id: ComponentIDGenerator.java,v 1.1 2009/02/20 02:27:21 aallowat Exp $
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

package net.sf.webcat.ui.util;

import com.webobjects.appserver.WOComponent;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

//--------------------------------------------------------------------------
/**
 * A simple class that lets a component easily generate CSS identifiers for its
 * sub-elements without being required to create a special method for each one.
 * Usage is as follows:
 * <p>
 * In your component class, declare an field that is an instance of this class:
 * <pre>
 *     ComponentIDGenerator idFor;
 * </pre>
 * <p>
 * In your component's <tt>appendToResponse</tt> method, initialize the field
 * by passing it the current component:
 * <pre>
 *     public void appendToResponse(WOResponse response, WOContext context)
 *     {
 *         idFor = new ComponentIDGenerator(this);
 *         
 *         // other processing...
 *         
 *         super.appendToResponse(response, context);
 *     }
 * </pre>
 * <p>
 * In your component's WOD file, use keys (or keypaths) that branch off this
 * "idFor" object:
 * <pre>
 *     SomeElement: WOSomeElement {
 *         id = idFor.aUniqueIdentifierString;
 *     }
 * </pre>
 * <p>
 * If the WebObjects element ID of SomeElement was "0.5.3", then the identifier
 * generated above would be "_0_5_3_aUniqueIdentifierString".
 * 
 * @author Tony Allevato
 * @version $Id: ComponentIDGenerator.java,v 1.1 2009/02/20 02:27:21 aallowat Exp $
 */
public class ComponentIDGenerator implements NSKeyValueCodingAdditions
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public ComponentIDGenerator(WOComponent component)
    {
        idBase = "_" + component.context().elementID().replace('.', '_');
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void takeValueForKey(Object object, String key)
    {
        // Do nothing.
    }

    
    // ----------------------------------------------------------
    public Object valueForKey(String key)
    {
        return idBase + "_" + key;
    }
    

    // ----------------------------------------------------------
    public void takeValueForKeyPath(Object object, String keypath)
    {
        // Do nothing.
    }


    // ----------------------------------------------------------
    public Object valueForKeyPath(String keypath)
    {
        String suffix = keypath.replace('.', '_');
        return idBase + "_" + suffix;
    } 


    //~ Static/instance variables .............................................

    private String idBase;
}
