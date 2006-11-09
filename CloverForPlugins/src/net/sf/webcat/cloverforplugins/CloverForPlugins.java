/*==========================================================================*\
 |  $Id: CloverForPlugins.java,v 1.1 2006/11/09 18:59:21 stedwar2 Exp $
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
 *  @version $Id: CloverForPlugins.java,v 1.1 2006/11/09 18:59:21 stedwar2 Exp $
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
           CLOVER_JAR_KEY,
           SUBSYSTEM_PREFIX + CLOVER_JAR_KEY,
           "clover/lib/clover.jar" );
   }


   //~ Instance/static variables .............................................

   private static final String SUBSYSTEM_PREFIX = "CloverForPlugins.";
   private static final String CLOVER_JAR_KEY   = "clover.jar";
}
