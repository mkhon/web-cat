/*==========================================================================*\
 |  $Id: ANTForPlugins.java,v 1.1 2006/11/09 18:12:02 stedwar2 Exp $
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

package net.sf.webcat.antforplugins;

import com.webobjects.foundation.*;
import java.io.File;
import org.apache.log4j.Logger;
import net.sf.webcat.core.Application;
import net.sf.webcat.core.Subsystem;

//-------------------------------------------------------------------------
/**
 *  This subsystem provides ANT, Checkstyle, and PMD for grading plug-ins.
 *
 *  @author  stedwar2
 *  @version $Id: ANTForPlugins.java,v 1.1 2006/11/09 18:12:02 stedwar2 Exp $
 */
public class ANTForPlugins
   extends Subsystem
{
   //~ Constructors ..........................................................

   // ----------------------------------------------------------
   /**
    * Creates a new PerlForPlugins subsystem object.
    */
   public ANTForPlugins()
   {
       super();
   }


   //~ Methods ...............................................................

   // ----------------------------------------------------------
   /**
    * Add any subsystem-specific command-line environment variable bindings
    * to the given dictionary.
    * @param env the dictionary to add environment variable bindings to;
    * the full set of currently available bindings are passed in.
    */
   public void addEnvironmentBindings( NSMutableDictionary env )
   {
       // JAVA_HOME
       String userSetting = Application.configurationProperties()
           .getProperty( SUBSYSTEM_PREFIX + JAVA_HOME_KEY );
       if ( userSetting != null )
       {
           env.takeValueForKey( userSetting,  JAVA_HOME_KEY );
       }

       // ANT_HOME
       addFileBinding(
           env,
           ANT_HOME_KEY,
           SUBSYSTEM_PREFIX + ANT_HOME_KEY,
           "ant" );
       
       // Add ANT_HOME/bin to path
       Object antHomeObj = env.valueForKey( ANT_HOME_KEY );
       if ( antHomeObj != null )
       {
           String path = antHomeObj.toString()
               + System.getProperty( "file.separator" ) + "bin";
           File antBinDir = new File( path );
           if ( antBinDir.exists() )
           {
               // Handle the fact that Windows variants often use "Path"
               // instead of "PATH"
               String pathKey = PATH_KEY2;
               Object valueObj = env.valueForKey( pathKey );
               if ( valueObj == null )
               {
                   pathKey = PATH_KEY1;
                   valueObj = env.valueForKey( pathKey );
               }
               if ( valueObj != null )
               {
                   path = path + System.getProperty( "path.separator" )
                       + valueObj.toString();
               }
               env.takeValueForKey( path, pathKey );
           }
           else
           {
               log.error( "Cannot locate ant/bin in Resources directory" );
           }
       }
   }


   //~ Instance/static variables .............................................

   private static final String SUBSYSTEM_PREFIX   = "ANTForPlugins.";
   private static final String JAVA_HOME_KEY      = "JAVA_HOME";
   private static final String ANT_HOME_KEY       = "ANT_HOME";
   private static final String PATH_KEY1          = "PATH";
   private static final String PATH_KEY2          = "Path";
   static Logger log = Logger.getLogger( ANTForPlugins.class );
}
