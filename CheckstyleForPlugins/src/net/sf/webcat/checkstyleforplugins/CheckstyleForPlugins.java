/*==========================================================================*\
 |  $Id: CheckstyleForPlugins.java,v 1.2 2008/04/02 01:02:40 stedwar2 Exp $
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

package net.sf.webcat.checkstyleforplugins;

import com.webobjects.foundation.*;
import net.sf.webcat.core.Application;
import net.sf.webcat.core.Subsystem;

//-------------------------------------------------------------------------
/**
 *  This subsystem provides <a href="http://checkstyle.sf.net/">Checkstyle</a>
 *  for grading plug-ins.
 *
 *  @author  stedwar2
 *  @version $Id: CheckstyleForPlugins.java,v 1.2 2008/04/02 01:02:40 stedwar2 Exp $
 */
public class CheckstyleForPlugins
    extends Subsystem
{
   //~ Constructors ..........................................................

   // ----------------------------------------------------------
   /**
    * Creates a new CheckstyleForPlugins subsystem object.
    */
   public CheckstyleForPlugins()
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
       // checkstyle.jar
       addFileBinding(
           properties,
           CHECKSTYLE_JAR_KEY,
           SUBSYSTEM_PREFIX + CHECKSTYLE_JAR_KEY,
           "checkstyle-all.jar" );
   }


   //~ Instance/static variables .............................................

   private static final String SUBSYSTEM_PREFIX   = "CheckstyleForPlugins.";
   private static final String CHECKSTYLE_JAR_KEY = "checkstyle.jar";
}
