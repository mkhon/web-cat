/*==========================================================================*\
 |  $Id: SubsystemManager.java,v 1.6 2008/04/02 00:50:30 stedwar2 Exp $
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

package net.sf.webcat.core;

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import net.sf.webcat.*;

import org.apache.log4j.Logger;

// -------------------------------------------------------------------------
/**
 * Manages the Subsystem's stored on disk. A subsystem is either a WebObjects
 * framework or a separate jar file that contains a framework.
 *
 *  @author Stephen Edwards
 *  @version $Id: SubsystemManager.java,v 1.6 2008/04/02 00:50:30 stedwar2 Exp $
 */
public class SubsystemManager
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initialize the <code>SubsystemManager</code>.  The
     * <code>SubsystemManager</code> will look for two properties in
     * the application's property settings.  First, it will look for
     * the property "subsystem.jar.dir".  If found, it will load all
     * jar'ed subsystems from this directory.  Second, it will look
     * for "subsystem.unjarred.classes", which is a "|"-separated list
     * of subsystem classes to register--subsystems that are already on
     * the classpath instead of in subsystem jars.  If this property is
     * defined, the corresponding classes will be registered as
     * subsystems.  Either property may be undefined, in which case it
     * will be ignored.
     *
     * @param properties The application's property settings
     */
    public SubsystemManager( WCProperties properties )
    {
        log.debug( "creating subsystem manager" );
        if ( properties != null )
        {
//            String jarDir = properties.getProperty( "subsystem.jar.dir" );
//            if ( jarDir != null )
//            {
//                addSubsystemJarsFromDirectory( jarDir );
//            }
//
//            NSArray subs =
//                properties.arrayForKey( "subsystem.unjarred.classes" );
//            if ( subs != null )
//            {
//                for ( int i = 0; i < subs.count(); i++ )
//                {
//                    addSubsystemFromClassName(
//                        (String)subs.objectAtIndex( i ) );
//                }
//            }
            ArrayList subsystemNames = new ArrayList();
            // Have to look in the system properties, because that is where
            // all subsystem info will go, not in the config file
            for ( Enumeration e = System.getProperties().keys();
                  e.hasMoreElements(); )
            {
                String key = (String)e.nextElement();
                if (  key.startsWith( SUBSYSTEM_KEY_PREFIX )
                   && key.indexOf( '.', SUBSYSTEM_KEY_PREFIX.length() ) == -1 )
                {
                    String name =
                        key.substring( SUBSYSTEM_KEY_PREFIX.length() );
                    subsystemNames.add( name );
                }
            }
            addSubsystemsInOrder(
                subsystemNames, null, new HashMap(), properties );
            initAllSubsystems();
        }
        envp();
        pluginProperties();
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Search a given directory for jars, and load the subsystem
     * contained in each one.
     *
     * @param directory  The root directory for loading the jar'ed subsystems
     */
//    public void addSubsystemJarsFromDirectory( String directory )
//    {
//        File root = new File( directory );
//
//        // Get list of all .jar files in the directory
//        File[] jars = root.listFiles( new FilenameFilter() {
//            public boolean accept( File dir, String name )
//            {
//                return name.endsWith( ".jar" );
//            }
//        } );
//
//        if ( jars != null )
//        {
//            // Add the jar files
//            for ( int i = 0; i < jars.length; ++i )
//            {
//                try
//                {
//                    addSubsystemFromJarFile( jars[i] );
//                }
//                catch ( Exception e )
//                {
//                    log.error( "Exception adding subsystem: "
//                               + jars[i].toString() + ":", e );
//                }
//            }
//        }
//
//    }


    // ----------------------------------------------------------
    /**
     * Get the requested subsystem.
     *
     * @param name  The name of the subsystem to get
     * @return      The corresponding JarSubsystem
     */
    public Subsystem subsystem( String name )
    {
        return (Subsystem)subsystems.get( name );
    }


    // ----------------------------------------------------------
    /**
     * Get an iterator for all loaded subsystems by name.
     *
     * @return An iterator for the names of all loaded subsystems
     */
    public NSArray subsystems()
    {
        return subsystemArray;
    }


    // ----------------------------------------------------------
    /**
     * Add a JarSubsystem to the manager, given a file name.
     *
     * @param name  The file name of the JAR file that contains the subsystem
     * @throws IOException
     */
//    public void addSubsystemFromJarFile( String name )
//        throws IOException
//    {
//        addSubsystemFromJarFile( new File( name ) );
//    }


    // ----------------------------------------------------------
    /**
     * Add a JarSubsystem to the manager, given a file object.
     *
     * @param file  The file of the JAR file that contains the subsystem
     * @throws IOException
     */
//    public void addSubsystemFromJarFile( File file )
//         throws IOException
//    {
//        JarSubsystem s = JarSubsystem.initializeSubsystemFromJar( file );
//        if ( s != null )
//        {
//            addSubsystem( s );
//        }
//    }


    // ----------------------------------------------------------
    /**
     * Add a Subsystem to the manager, given a class name.
     *
     * @param name the symbolic name to use for this subsystem
     * @param className the class name to load
     */
    public void addSubsystemFromClassName( String name, String className )
    {
        log.debug( "attempting to load subsystem " + name + " using class '"
            + className + "'");
        try
        {
            addSubsystem( name,
                (Subsystem)DelegatingUrlClassLoader.getClassLoader()
                    .loadClass( className ).newInstance() );
        }
        catch ( Exception e )
        {
            log.error( "Exception loading subsystem:", e );
        }
    }


    // ----------------------------------------------------------
    /**
     * Add a Subsystem to the manager.
     *
     * @param name  The subsystem's name
     * @param s     The subsystem object to add
     */
    public void addSubsystem( String name, Subsystem s )
    {
        if ( name == null ) name = s.name();
        if ( !subsystems.containsKey( name ) )
        {
            log.info( "Registering subsystem " + s.name() + " as " + name );
            s.setName( name );
            subsystems.put( name, s );
            subsystemArray.addObject( s );
            clearSubsystemPropertyCache();
        }
        else
        {
            log.error( "Subsystem already registered: " + name );
        }
    }


    // ----------------------------------------------------------
    /**
     * Calls {@link Subsystem#initializeSessionData(Session)} for
     * all registered subsystems.
     * @param s the session to initialize
     */
    public void initializeSessionData( Session s )
    {
        NSArray subs = subsystems();
        for ( int i = 0; i < subs.count(); i++ )
        {
            ( (Subsystem)subs.objectAtIndex( i ) )
                .initializeSessionData( s );
        }
    }


    // ----------------------------------------------------------
    /**
     * Generate the component definitions and bindings for a given
     * pre-defined information fragment, so that the result can be
     * plugged into other pages defined elsewhere in the system.
     * @param fragmentKey the identifier for the fragment to generate
     *        (see the keys defined in {@link SubsystemFragmentCollector}
     * @param htmlBuffer add the html template for the subsystem's fragment
     *        to this buffer
     * @param wodBuffer add the binding definitions (the .wod file contents)
     *        for the subsystem's fragment to this buffer
     */
    public void collectSubsystemFragments(
        String fragmentKey, StringBuffer htmlBuffer, StringBuffer wodBuffer )
    {
        NSArray subs = subsystems();
        for ( int i = 0; i < subs.count(); i++ )
        {
            ( (Subsystem)subs.objectAtIndex( i ) )
                .collectSubsystemFragments(
                    fragmentKey, htmlBuffer, wodBuffer );
        }
    }


    // ----------------------------------------------------------
    /**
     * Take a list of subsystem names (typically, requirements needed to
     * support some feature) and determine if they are present.
     * @param names a list of subsystem names to look for
     * @return true if all of the named subsystems are installed
     */
    public boolean subsystemsAreInstalled( NSArray names )
    {
        for ( int i = 0; i < names.count(); i++ )
        {
            if ( subsystems.get( names.objectAtIndex( i ) ) == null )
            {
                return false;
            }
        }
        return true;
    }


    // ----------------------------------------------------------
    /**
     * Get the command line environment variables used for executing
     * external commands.
     * @return a dictionary of ENV bindings
     */
    public NSDictionary environment()
    {
        if ( envCache == null )
        {
            NSMutableDictionary env = inheritedEnvironment();
            NSArray subs = subsystems();
            for ( int i = 0; i < subs.count(); i++ )
            {
                ( (Subsystem)subs.objectAtIndex( i ) )
                    .addEnvironmentBindings( env );
            }
            envCache = env;
            if ( log.isDebugEnabled() )
            {
                log.debug( "plug-in ENV = " + env );
            }
        }
        return envCache;
    }


    // ----------------------------------------------------------
    /**
     * Get the command line environment variables used for executing
     * external commands in a form suitable for passing to
     * {@link Runtime#exec(String,String[])}.
     * @return a string array of ENV bindings, each in the form:
     * <i>NAME=value</i>.
     */
    public String[] envp()
    {
        if ( envpCache == null )
        {
            NSDictionary env = environment();
            ArrayList envpList = new ArrayList();
            for ( Enumeration e = env.keyEnumerator(); e.hasMoreElements(); )
            {
                String key = (String)e.nextElement();
                String val = env.objectForKey( key ).toString();
                envpList.add( key + "=" + val );
            }
            String[] envp =
                (String[])envpList.toArray( new String[envpList.size()] );
            envpCache = envp;
            if ( log.isDebugEnabled() )
            {
                log.debug( "envp = " + arrayToString( envp ) );
            }
        }
        return envpCache;
    }


    // ----------------------------------------------------------
    /**
     * Get the plug-in property definitions to be passed to plug-ins.
     * @return a dictionary of plug-in properties
     */
    public NSDictionary pluginProperties()
    {
        if ( pluginPropertiesCache == null )
        {
            NSMutableDictionary properties = new NSMutableDictionary();
            NSArray subs = subsystems();
            for ( int i = 0; i < subs.count(); i++ )
            {
                ( (Subsystem)subs.objectAtIndex( i ) )
                    .addPluginPropertyBindings( properties );
            }
            pluginPropertiesCache = properties;
            if ( log.isDebugEnabled() )
            {
                log.debug( "plug-in properties = " + properties );
            }
        }
        return pluginPropertiesCache;
    }


    // ----------------------------------------------------------
    /**
     * Clear the cache of any subsystem-provided environment definitions
     * or plug-in property definitions.
     */
    public void clearSubsystemPropertyCache()
    {
        envCache = null;
        envpCache = null;
        pluginPropertiesCache = null;
    }


    // ----------------------------------------------------------
    /**
     * Refreshes cached information about subsystems and their providers.
     */
    public void refreshSubsystemDescriptorsAndProviders()
    {
        for ( Iterator i = FeatureProvider.providers().iterator();
            i.hasNext(); )
        {
            ( (FeatureProvider)i.next() ).refresh();
        }
        if ( subsystemArray != null )
        {
            for ( int i = 0; i < subsystemArray.count(); i++ )
            {
                ( (Subsystem)subsystemArray.objectAtIndex( i ) )
                    .refreshDescriptor();
            }
        }
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    /**
     * Calls {@link Subsystem#init()} for all registered subsystems.
     */
    private void initAllSubsystems()
    {
        NSArray subs = subsystems();
        for ( int i = 0; i < subs.count(); i++ )
        {
            ( (Subsystem)subs.objectAtIndex( i ) ).init();
        }
    }


    // ----------------------------------------------------------
    /**
     * Convert a (possibly null) comma-separated string into an array of
     * strings.
     * @param rawList the comma-separated string to split (can be null)
     * @return an array of the items in rawList, after separating on commas;
     * any whitespace surrounding commas are ignored; if rawList is null or
     * empty, then null is returned.
     */
    private String[] featureList( String rawList )
    {
        String[] result = null;
        if ( rawList != null )
        {
            rawList = rawList.trim();
            if ( !rawList.equals( "" ) )
            {
                result = rawList.split( "\\s*,\\s*" );
            }
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Check to see if any of the strings in the featureList are stored
     * in theSet.  If theSet is null, returns true.  If featureList is
     * null, returns false.
     * @param featureList an array of strings to test
     * @param theSet a set to check in
     * @return true if any string in featureList is found in theSet, or if
     * theSet is null
     */
    private boolean foundIn( String[] featureList, Map theSet )
    {
        if ( featureList == null ) return false;
        if ( theSet == null ) return true;
        for ( int i = 0; i < featureList.length; i++ )
        {
            if ( theSet.containsKey( featureList[i] ) )
            {
                return true;
            }
        }
        return false;
    }


    // ----------------------------------------------------------
    /**
     * Check to see if all of the strings in the featureList are stored
     * in theSet.  If featureList is null, returns false.  Otherwise, if
     * theSet is null, returns true.
     * @param featureList an array of strings to test
     * @param theSet a set to check in
     * @return false if any string in featureList is missing in theSet
     */
    private boolean missingFrom( String[] featureList, Map theSet )
    {
        if ( featureList == null ) return false;
        if ( theSet == null ) return true;
        for ( int i = 0; i < featureList.length; i++ )
        {
            if ( !theSet.containsKey( featureList[i] ) )
            {
                return true;
            }
        }
        return false;
    }


    // ----------------------------------------------------------
    /**
     * Add all of the strings in featureList to theSet.
     * @param featureList an array of strings to add
     * @param theSet the map to add them to
     */
    private void addTo( String[] featureList, Map theSet )
    {
        if ( featureList == null ) return;
        for ( int i = 0; i < featureList.length; i++ )
        {
            theSet.put( featureList[i], featureList[i] );
        }
    }


    // ----------------------------------------------------------
    /**
     * Generate a string from an array of strings.
     * @param array the array to convert
     */
    private String arrayToString( String[] array )
    {
        if ( array == null ) return null;
        StringBuffer buffer = new StringBuffer();
        buffer.append( "[ " );
        for ( int i = 0; i < array.length; i++ )
        {
            if ( i > 0 )
            {
                buffer.append( ", " );
            }
            buffer.append( array[i] );
        }
        buffer.append( " ]" );
        return buffer.toString();
    }


    // ----------------------------------------------------------
    /**
     * Install/load a list of subsystems, searching for dependencies and
     * using them to install subsystems in the correct order.  Since
     * dependencies are fairly rare, a dumb O(n^2) algorithm is used for
     * the topological sort.
     * @param names a list of subsystem names that need to be
     *     loaded/installed
     * @param pendingFeatures a map that contains the names of defined
     *     features that have not yet been loaded, for dependency tracking
     * @param addedFeatures a map that contains the names of defined features
     *     that have been installed (or at least partially installed)
     * @param properties The application's property settings
     */
    private void addSubsystemsInOrder(
        ArrayList    names,
        Map          pendingFeatures,
        Map          addedFeatures,
        WCProperties properties )
    {
        if ( names.size() == 0 ) return;
        int oldSize = names.size();
        Map incompleteFeatures = new HashMap();
        log.debug( "starting subsystem list traversal: " + names );
        for ( int i = 0; i < names.size(); i++ )
        {
            String name = (String)names.get( i );
            String[] depends = featureList(
                properties.getProperty( name + DEPENDS_SUFFIX ) );
            String[] requires = featureList(
                properties.getProperty( name + REQUIRES_SUFFIX ) );
            if ( foundIn( depends, pendingFeatures )
                 || foundIn( requires, pendingFeatures ) )
            {
                if ( log.isDebugEnabled() )
                {
                    log.debug( "skipping " + name + ": depends = "
                        + arrayToString( depends ) + "; requires = "
                        + arrayToString( requires ) );
                }
                incompleteFeatures.put( name, name );
                addTo( featureList(
                    properties.getProperty( name + PROVIDES_SUFFIX ) ),
                    incompleteFeatures );
            }
            else
            {
                if ( log.isDebugEnabled() )
                {
                    log.debug( "loading " + name + ": depends = "
                        + arrayToString( depends ) + "; requires = "
                        + arrayToString( requires ) );
                }
                names.remove( i );
                i--;
                if ( missingFrom( requires, addedFeatures ) )
                {
                    log.error( "unable to load subsystem '" + name
                        + "': one or more required subsystems are missing: "
                        + arrayToString( requires ) );
                }
                else
                {
                    addedFeatures.put( name, name );
                    addTo( featureList(
                        properties.getProperty( name + PROVIDES_SUFFIX ) ),
                        addedFeatures );
                    String className =
                        properties.getProperty( SUBSYSTEM_KEY_PREFIX + name );

                    // Use a default class if no class name is specified
                    // in the property
                    if ( className == null || className.equals( "" ) )
                    {
                        className = Subsystem.class.getName();
                    }

                    addSubsystemFromClassName( name, className );
                }
            }
        }
        if ( oldSize == names.size() )
        {
            log.error(
                "cyclic or missing dependencies among subsystems detected: "
                + names );
        }
        else
        {
            addSubsystemsInOrder(
                names, incompleteFeatures, addedFeatures, properties );
        }
    }


    // ----------------------------------------------------------
    /**
     * Load this application's current ENV bindings into a dictionary.
     * @return the current ENV bindings
     */
    private NSMutableDictionary inheritedEnvironment()
    {
        if ( inheritedEnvCache == null )
        {
            NSMutableDictionary env = new NSMutableDictionary();
            // Fill it up

            // First, try Unix command
            try
            {
                Process process = Runtime.getRuntime().exec(
                    Application.isRunningOnWindows()
                        ? "cmd /c set" : "printenv" );
                BufferedReader in = new BufferedReader(
                    new InputStreamReader( process.getInputStream() ) );
                String line = in.readLine();
                while ( line != null )
                {
                    int pos = line.indexOf( '=' );
                    if ( pos > 0 )
                    {
                        String key = line.substring( 0, pos );
                        String val = line.substring( pos + 1 );
                        env.takeValueForKey( val, key );
                    }
                    line = in.readLine();
                }
            }
            catch ( java.io.IOException e )
            {
                log.error(
                    "Error attempting to parse default ENV settings:", e );
            }

            inheritedEnvCache = env;
            if ( log.isDebugEnabled() )
            {
                log.debug( "inherited ENV = " + env );
            }
        }
        return inheritedEnvCache.mutableClone();
    }


    //~ Instance/static variables .............................................

    /** Map&lt;String, JarSubsystem&gt;: holds the loaded subsystems. */
    private Map subsystems = new HashMap();
    private NSMutableArray subsystemArray = new NSMutableArray();
    private NSDictionary inheritedEnvCache = null;
    private NSDictionary envCache = null;
    private NSDictionary pluginPropertiesCache = null;
    private String[] envpCache = null;

    private static final String SUBSYSTEM_KEY_PREFIX = "subsystem.";
    private static final String DEPENDS_SUFFIX       = ".depends";
    private static final String REQUIRES_SUFFIX      = ".requires";
    private static final String PROVIDES_SUFFIX      = ".provides";

    static Logger log = Logger.getLogger( SubsystemManager.class );
}
