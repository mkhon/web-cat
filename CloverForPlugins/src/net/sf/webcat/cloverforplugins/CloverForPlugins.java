/*==========================================================================*\
 |  $Id: CloverForPlugins.java,v 1.3 2008/04/02 01:03:59 stedwar2 Exp $
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

package net.sf.webcat.cloverforplugins;

import com.webobjects.foundation.*;
import java.io.*;
import org.apache.log4j.Logger;
import net.sf.webcat.core.Application;
import net.sf.webcat.core.Subsystem;

// -------------------------------------------------------------------------
/**
 *  This subsystem provides <a href="http://www.cenqua.com/clover/">Clover</a>
 *  for grading plug-ins.
 *
 *  @author  stedwar2
 *  @version $Id: CloverForPlugins.java,v 1.3 2008/04/02 01:03:59 stedwar2 Exp $
 */
public class CloverForPlugins
    extends Subsystem
{
   //~ Constructors ..........................................................

   // ----------------------------------------------------------
   /**
    * Creates a new CloverForPlugins subsystem object.
    */
   public CloverForPlugins()
   {
       super();
   }


   //~ Methods ...............................................................

   // ----------------------------------------------------------
   /**
    * Add any subsystem-specific plug-in property bindings
    * to the given dictionary.
    * @param properties the dictionary to add new properties to;
    * individual plug-in information may override these later.
    */
   public void addPluginPropertyBindings( NSMutableDictionary properties )
   {
       // clover.jar
       addFileBinding(
           properties,
           CLOVER_DIR_KEY,
           SUBSYSTEM_PREFIX + CLOVER_DIR_KEY,
           "clover" );
   }


   //~ Instance/static variables .............................................

   private static final String SUBSYSTEM_PREFIX = "CloverForPlugins.";
   private static final String CLOVER_DIR_KEY   = "clover.dir";
}
