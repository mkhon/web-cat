/*==========================================================================*\
 |  $Id: FeatureDescriptor.java,v 1.7 2010/09/16 18:49:16 aallowat Exp $
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

package net.sf.webcat;

import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;

// -------------------------------------------------------------------------
/**
 *  This class represents the key properties of an updatable Web-CAT feature,
 *  such as a subsystem or a plug-in.  The key properties include
 *  its version, its provider, and where updates can be obtained on the
 *  web.
 *
 *  @author  stedwar2
 *  @version $Id: FeatureDescriptor.java,v 1.7 2010/09/16 18:49:16 aallowat Exp $
 */
public class FeatureDescriptor
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new feature descriptor.
     * @param name the name of this feature (e.g., "Core")
     * @param properties the properties object that defines the
     *        characteristics of this subsystem
     * @param isPlugin false if this is a subsystem, true if it is a plug-in
     */
    public FeatureDescriptor(
        String name, Properties properties, boolean isPlugin )
    {
        this.name = name;
        this.properties = properties;
        this.isPlugin = isPlugin;
    }


    // ----------------------------------------------------------
    /**
     * For subclass use.
     */
    protected FeatureDescriptor()
    {
        // Subclass is responsible for initializing data fields
    }


    //~ Public Constants ......................................................

    public static final String PLUGIN_NAME_PREFIX    = "plugin.";
    public static final String SUBSYSTEM_NAME_PREFIX = "subsystem.";
    public static final String RENAME_SUFFIX = ".renameTo";


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Get this subsystem's symbolic name, e.g. "grader" or "core".
     * @return the symbolic name of this subsystem
     */
    public String name()
    {
        return name;
    }


    // ----------------------------------------------------------
    /**
     * Determine if this is a real subsystem, or just a proxy for a
     * non-registered framework on the classpath.
     * @return True if this is a registered subsystem
     */
    public boolean isRegistered()
    {
        return name != null;
    }


    // ----------------------------------------------------------
    /**
     * Determine if this is a subsystem or a plug-in descriptor.
     * @return True if this is a plug-in descriptor
     */
    public boolean isPlugin()
    {
        return isPlugin;
    }


    // ----------------------------------------------------------
    /**
     * Get the version number for the currently installed version of this
     * subsystem.
     * @return the current subsystem version as a string, like "1.2.3"
     */
    public String currentVersion()
    {
        if ( currentVersion == null )
        {
            currentVersion = "" + versionMajor() + "." + versionMinor()
                + "." + versionRevision();
        }
        return currentVersion;
    }


    // ----------------------------------------------------------
    /**
     * Get the major version number for this subsystem.
     * @return the subsystem's major version number
     */
    public int versionMajor()
    {
        if ( versionMajor < 0 )
        {
            versionMajor = intProperty( "version.major" );
        }
        return versionMajor;
    }


    // ----------------------------------------------------------
    /**
     * Get the minor version number for this subsystem.
     * @return the subsystem's minor version number
     */
    public int versionMinor()
    {
        if ( versionMinor < 0 )
        {
            versionMinor = intProperty( "version.minor" );
        }
        return versionMinor;
    }


    // ----------------------------------------------------------
    /**
     * Get the revision number for this subsystem.
     * @return the subsystem's revision number
     */
    public int versionRevision()
    {
        if ( versionRevision < 0 )
        {
            versionRevision = intProperty( "version.revision" );
        }
        return versionRevision;
    }


    // ----------------------------------------------------------
    /**
     * Get the build date for this subsystem.
     * @return the subsystem's build date
     */
    public String versionDate()
    {
        return getProperty( "version.date" );
    }


    // ----------------------------------------------------------
    /**
     * Get the build date for this subsystem.
     * @return the subsystem's build date
     */
    public String description()
    {
        return getProperty( "description" );
    }


    // ----------------------------------------------------------
    /**
     * Get a proxy for this subsystem's provider, which can be used to
     * dynamically download a new version of this subsystem.
     * @return the subsystem's provider
     */
    public FeatureProvider provider()
    {
        return FeatureProvider.getProvider( getProperty( "provider.url" ) );
    }


    // ----------------------------------------------------------
    /**
     * Determine if a newer version of this subsystem is available from
     * its provider.
     * @return True if a newer version is available
     */
    public FeatureDescriptor providerVersion()
    {
        FeatureDescriptor latest = null;
        FeatureProvider provider = provider();
        if ( provider != null )
        {
            latest = isPlugin()
                ? provider.pluginDescriptor( name )
                : provider.subsystemDescriptor( name );
        }
        return latest;
    }


    // ----------------------------------------------------------
    /**
     * Determine whether one descriptor's version is less than annother's.
     * @param other the desscriptor to compare against
     * @return true if the current version for this subsystem is higher
     * than the current version for the other descriptor
     */
    public boolean isNewerThan( FeatureDescriptor other )
    {
        return ( other.versionMajor() < this.versionMajor() )

            || ( other.versionMajor() == this.versionMajor()
                 && other.versionMinor() < this.versionMinor() )

            || ( other.versionMajor() == this.versionMajor()
                 && other.versionMinor() == this.versionMinor()
                 && other.versionRevision() < this.versionRevision() );
    }


    // ----------------------------------------------------------
    /**
     * Determine if a newer version of this subsystem is available from
     * its provider.
     * @return True if a newer version is available
     */
    public boolean updateIsAvailable()
    {
        boolean result = false;
        FeatureDescriptor latest = providerVersion();
        if ( latest != null )
        {
            result = latest.isNewerThan( this );
        }
        return result;
    }


    // ----------------------------------------------------------
    /**
     * Retrieve a subsystem-specific property's value.
     * @param propName the name of the property to retrieve
     * @return the value of the <i>name.propName</i> property, where
     *     <i>name</i> is the name of this subsystem
     */
    public String getProperty( String propName )
    {
        return properties.getProperty( name + "." + propName );
    }


    // ----------------------------------------------------------
    /**
     * Retrieve a subsystem-specific property's value.
     * @param propName the name of the property to retrieve
     * @param defaultValue the value to use if the property is not found
     * @return the value of the <i>name.propName</i> property, where
     *     <i>name</i> is the name of this subsystem, or the defaultValue
     *     if no such property is found
     */
    public String getProperty( String propName, String defaultValue )
    {
        return properties.getProperty( name + "." + propName, defaultValue );
    }


    // ----------------------------------------------------------
    /**
     * Get the raw subsystem properties object to inspect lower-level
     * information about this subsystem.
     * @return the subsystem's properties
     */
    public Properties properties()
    {
        return properties;
    }


    // ----------------------------------------------------------
    /**
     * Download this feature version from its provider.
     * @param location the place to put the downloaded file
     * @return null on success, or an error message on failure
     */
    public String downloadTo( File location )
    {
        FeatureProvider provider = provider();
        if ( provider != null )
        {
            String fileName = name + "_" + currentVersion() + ".jar";
            String update = ( isPlugin() ? "plugins/" : "subsystems/" )
                + fileName;
            try
            {
                URL fileUrl = new URL( provider.url(), update );
                InputStream in = fileUrl.openStream();
                File outFile = new File( location, fileName );
                FileOutputStream out = new FileOutputStream( outFile );
                logInfo( "Downloading " + fileUrl + " to "
                    + outFile.getAbsolutePath() );
                FileUtilities.copyStream( in, out );
                out.close();
                in.close();
            }
            catch ( IOException e )
            {
                logError( "unable to download " + update + ":", e );
            }
        }
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Download an updated version of this feature from its provider, if
     * available.
     * @param location the place to put the downloaded file
     * @return null on success, or an error message on failure
     */
    public String downloadUpdateIfNecessary( File location )
    {
        String updateAutomatically = getProperty( "updateAutomatically" );
        if (   updateAutomatically != null
            && (   updateAutomatically.equals( "0" )
                || updateAutomatically.equals( "false" )
                || updateAutomatically.equals( "no" ) ) )
        {
            // If we shouldn't update this subsystem, skip it
            return null;
        }

        FeatureDescriptor latest = providerVersion();
        if ( latest != null && latest.isNewerThan( this ) )
        {
            logInfo( "Updating " + name );
            return latest.downloadTo( location );
        }
        else if ( name != null )
        {
            logInfo( "Feature " + name + " is up to date." );
        }
        return null;
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    /**
     * Access a given property and convert it to a numeric value (with a
     * default of zero).
     * @param propName the name of the property to look up
     * @return the property's value as an int
     */
    protected int intProperty( String propName )
    {
        int val = 0;
        String str = getProperty( propName, "0" );
        try {
            if ( str.length() > 0 )
            {
                val = Integer.parseInt( str );
            }
        }
        catch ( NumberFormatException e )
        {
            logError( "Non-numeric property " + propName
                + " for feature " + name );
        }
        return val;
    }


    // ----------------------------------------------------------
    /**
     * Log an informational message.  This implementation sends output
     * to {@link System#out}, but provides a hook so that subclasses
     * can use Log4J (we don't use that here, so that the Log4J library
     * can be dynamically updatable through subsystems).
     * @param msg the message to log
     */
    protected void logInfo( String msg )
    {
        System.out.println( msg );
    }


    // ----------------------------------------------------------
    /**
     * Log an error message.  This implementation sends output
     * to {@link System#out}, but provides a hook so that subclasses
     * can use Log4J (we don't use that here, so that the Log4J library
     * can be dynamically updatable through subsystems).
     * @param msg the message to log
     */
    protected void logError( String msg )
    {
        String className = getClass().getName();
        int pos = className.lastIndexOf( '.' );
        if ( pos >= 0 )
        {
            className = className.substring( pos + 1 );
        }
        System.out.println( className + ": ERROR: " + msg );
    }


    // ----------------------------------------------------------
    /**
     * Log an error message.  This implementation sends output
     * to {@link System#out}, but provides a hook so that subclasses
     * can use Log4J (we don't use that here, so that the Log4J library
     * can be dynamically updatable through subsystems).
     * @param msg the message to log
     * @param exception an optional exception that goes with the message
     */
    protected void logError( String msg, Throwable exception )
    {
        logError( msg );
        System.out.println( exception );
    }


    //~ Instance/static variables .............................................

    protected Properties properties;
    protected String name;
    protected boolean isPlugin = false;

    private String  currentVersion;
    private int     versionMajor    = -1;
    private int     versionMinor    = -1;
    private int     versionRevision = -1;
}
