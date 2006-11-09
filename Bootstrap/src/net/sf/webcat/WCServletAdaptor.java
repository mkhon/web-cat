/*==========================================================================*\
 |  $Id: WCServletAdaptor.java,v 1.2 2006/11/09 16:43:50 stedwar2 Exp $
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

package net.sf.webcat;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

// -------------------------------------------------------------------------
/**
 *  This is a custom subclass of com.webobjects.jspservlet.WOServletAdaptor.
 *  It adds transparent capabilities for self-updating Java subsystems
 *  within Web-CAT, before the application starts up.
 *
 *  @author  stedwar2
 *  @version $Id: WCServletAdaptor.java,v 1.2 2006/11/09 16:43:50 stedwar2 Exp $
 */
public class WCServletAdaptor
    extends com.webobjects.jspservlet.WOServletAdaptor
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new adaptor object.
     * @throws ServletException
     */
    public WCServletAdaptor()
        throws ServletException
    {
        super();
        instance = this;
    }


    // ----------------------------------------------------------
    /**
     * Get access to the current adaptor object.
     * @return a reference to the most recently created adaptor, or null
     *     if there isn't one
     */
    public static WCServletAdaptor getInstance()
    {
        return instance;
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Start up the servlet--this method contains the heavy-weight one-time
     * actions at application startup, including updating any necessary
     * subsystems before the main application class is loaded or the
     * classpath is completely set up.
     * @throws ServletException
     */
    public void init()
        throws ServletException
    {
        String webInfRoot = super.getServletContext().getRealPath( "WEB-INF" );
        File webInfDir = new File( webInfRoot );
        propertiesFile = new File( webInfDir, "update.properties" );
        updateDir = new File( webInfDir, UPDATE_SUBDIR );
        loadProperties();
        applyNecessaryUpdates( webInfDir );
        try
        {
            super.init();
        }
        catch ( javax.servlet.UnavailableException e )
        {
            // License key must be missing or invalid
            needsLicense = true;
        }
    }


    // ----------------------------------------------------------
    /**
     * Access the servlet context object for this servlet.  This is a wrapper
     * around the superclass default implementation that returns a
     * {@link WCServletContext} object wrapped around the real servlet
     * context, to provide access to a customized classpath via
     * {@link WCServletContext#getInitParameter(String)}.
     * @return the context object
     * @see javax.servlet.GenericServlet#getServletContext()
     */
    public javax.servlet.ServletContext getServletContext()
    {
        javax.servlet.ServletContext result = super.getServletContext();
        if ( result != null )
        {
            if ( result != innerContext )
            {
                wrappedContext = new WCServletContext( result, woClasspath );
            }
            innerContext = result;
            result = wrappedContext;
        }
        return result;
    }


    // ----------------------------------------------------------
    public void doGet( HttpServletRequest arg0, HttpServletResponse arg1 )
        throws IOException,
            ServletException
    {
        if ( needsLicense )
        {
            sendLicenseForm( arg1, null );
        }
        else
        {
            super.doGet( arg0, arg1 );
        }
    }


    // ----------------------------------------------------------
    public void doPost( HttpServletRequest  request,
                        HttpServletResponse response )
        throws IOException,
            ServletException
    {
        if ( needsLicense )
        {
            String key = request.getParameter( "key" );
            if ( key == null || key.equals( "" ) )
            {
                sendLicenseForm( response,
                    "You must enter a deployment license key." );
            }
            else
            {
                // Try to install license
                File license = licenseKeyFile();
                if ( license.exists() )
                {
                    license.delete();
                }
                try
                {
                    PrintWriter out = new PrintWriter(
                        new FileWriter( license ) );
                    out.println( key );
                    out.close();
                    // Try to re-initialize with new license
                    super.init();
                    needsLicense = false;
                }
                catch ( javax.servlet.UnavailableException e )
                {
                    sendLicenseForm( response, null );
                }
                catch ( IOException e )
                {
                    sendLicenseForm( response,
                        "Error writing license key file: " + e.getMessage() );
                }
                if ( !needsLicense )
                {
                    sendLicenseAcknowledgement( response );
                }
            }
        }
        else
        {
            super.doPost( request, response );
        }
    }


    // ----------------------------------------------------------
    /**
     * Determine if this adaptor will attempt to automatically update all
     * subsystems on start-up.
     * @return true if subsystems will be automatically updated
     */
    public boolean willUpdateAutomatically()
    {
        String val = properties.getProperty( "updateAutomatically", "1" );
        return val.equals( "1" )
            || val.equals( "true" )
            || val.equals( "yes" );
    }


    // ----------------------------------------------------------
    /**
     * Set whether or not this adaptor will attempt to automatically update
     * all subsystems on start-up.
     * @param value true if this adaptor will auto-update subsystems
     */
    public void setWillUpdateAutomatically( boolean value )
    {
        properties.setProperty( "updateAutomatically", value ? "1" : "0" );
        commitProperties();
    }


    // ----------------------------------------------------------
    /**
     * Access the collection of subsystems in this application.
     * @return a collection of {@link SubsystemUpdater} objects representing
     *     the available subsystems
     */
    public Collection subsystems()
    {
        return subsystems.values();
    }


    // ----------------------------------------------------------
    /**
     * Get a file representing the directory where downloaded updates should
     * be placed.
     * @return a collection of {@link SubsystemUpdater} objects representing
     *     the available subsystems
     */
    public File updateDownloadLocation()
    {
        return updateDir;
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    /**
     * Build a license entry form as the designated http response.
     * @param response the http response being generated
     * @param msg an optional error message to display
     * @throws IOException if one arises while writing the response
     */
    private void sendLicenseForm( HttpServletResponse response, String msg )
        throws IOException
    {
        response.setContentType( "text/html" );
        PrintWriter out = new PrintWriter( response.getOutputStream() );
        out.println( "<html><head>" );
        out.println( "<title>WebObjects License Required</title>" );
        out.println( "</head><body>" );
        out.println( "<h1>WebObjects License Required</h1>" );
        if ( msg == null )
        {
            File license = licenseKeyFile();
            if ( license.exists() )
            {
                msg = "Invalid license: '";
                try
                {
                    BufferedReader in = new BufferedReader(
                        new FileReader( license ) );
                    msg += in.readLine();
                    in.close();
                }
                catch ( Exception e )
                {
                    System.out.println( "Exception reading from "
                        + license.getAbsolutePath() + ": " + e.getMessage() );
                    msg = "Error reading license key file: '" + e.getMessage();
                }
                msg += "'";
            }
            else
            {
                msg = "No license installed.";
            }
        }
        out.print( "<p><b style=\"color:red\">" );
        out.print( msg );
        out.print( "</b></p>" );
        out.println( "<p>This application requires a valid <b>WebObjects " );
        out.println( "Deployment License</b>.  Please enter your deployment " );
        out.println( "license key.</p>" );
        out.println( "<form method=\"post\" action=\"\">" );
        out.println( "License key: <input type=\"text\" name=\"key\" " );
        out.println( "size=\"40\"/> <input type=\"submit\" " );
        out.println( "value=\"Install License\"/>" );
        out.println( "</form></body></html>" );
        out.flush();
        out.close();
    }


    // ----------------------------------------------------------
    /**
     * Build a license acknowledgement message as the designated http response.
     * @param response the http response being generated
     * @throws IOException if one arises while writing the response
     */
    private void sendLicenseAcknowledgement( HttpServletResponse response )
        throws IOException
    {
        response.setContentType( "text/html" );
        PrintWriter out = new PrintWriter( response.getOutputStream() );
        out.println( "<html><head>" );
        out.println( "<title>WebObjects License Installed</title>" );
        out.println( "</head><body>" );
        out.println( "<h1>WebObjects License Installed</h1>" );
        out.println( "<p>Your WebObjects Deployment License has been " );
        out.println( "installed.  Now <a href=\"\">install Web-CAT</a>.</p>" );
        out.println( "</body></html>" );
        out.flush();
        out.close();
    }


    // ----------------------------------------------------------
    /**
     * Get the file storing the license key.
     */
    private File licenseKeyFile()
    {
        return new File( frameworkDir,
            "JavaWebObjects.framework/Resources/License.key" );
    }


    // ----------------------------------------------------------
    /**
     * Apply any necessary updates and compute the woClasspath value.
     * @param webInfDir the WEB-INF directory as a file object
     */
    private void applyNecessaryUpdates( File webInfDir )
    {
        File mainBundle   = null;
        if ( webInfDir.isDirectory() )
        {
            File[] bundleSearchDirs = webInfDir.listFiles();
            for ( int i = 0; i < bundleSearchDirs.length; i++ )
            {
                if ( bundleSearchDirs[i].isDirectory()
                     && bundleSearchDirs[i].getName().endsWith( ".woa" ) )
                {
                    mainBundle = new File( bundleSearchDirs[i], "Contents" );
                    frameworkDir = new File(
                        bundleSearchDirs[i].getAbsolutePath()
                        + FRAMEWORK_SUBDIR );
                    break;
                }
            }
            File appDir = webInfDir.getParentFile();
            downloadNewUpdates( frameworkDir, mainBundle );
            applyPendingUpdates( frameworkDir, appDir );
        }
        if ( frameworkDir != null && frameworkDir.isDirectory() )
        {
            File[] subdirs = frameworkDir.listFiles();
            java.util.Arrays.sort( subdirs, new FrameworkComparator() );
            woClasspath = classPathFrom( subdirs,  mainBundle );
            System.out.println( "Dynamically computed classpath:" );
            System.out.print( woClasspath );
        }
    }


    // ----------------------------------------------------------
    /**
     * Scan the update directory for any downloaded update files, apply
     * them, and then delete them.
     * @param frameworkDir The place to unpack updates
     * @param appDir The application directory, which is where any top-level
     *     updates (e.g., "webcat_*" files) are unpacked
     */
    private void applyPendingUpdates( File frameworkDir, File appDir )
    {
        if ( updateDir.exists() && updateDir.isDirectory() )
        {
            File[] jars = updateDir.listFiles();
            String[] extensions = SubsystemUpdater.JAVA_ARCHIVE_EXTENSIONS;
            for ( int i = 0; i < jars.length; i++ )
            {
                for ( int j = 0; j < extensions.length; j++ )
                if ( jars[i].getName().endsWith( extensions[j] ) )
                {
                    File unpackDir = frameworkDir;
                    if ( jars[i].getName().startsWith( APP_JAR_PREFIX ) )
                    {
                        unpackDir = appDir;
                    }
                    try
                    {
                        System.out.println( "Applying update from "
                            + jars[i].getName() );
                        ZipFile zipFile = new ZipFile( jars[i] );
                        FileUtilities.unZip( zipFile, unpackDir );
                        zipFile.close();
                        if ( !jars[i].delete() )
                        {
                            System.out.println( "unable to delete "
                                + jars[i].getAbsolutePath() );
                        }
                    }
                    catch ( java.io.IOException e )
                    {
                        System.out.println( "IO error trying to unpack "
                            + "update bundle: " + e.getMessage() );
                        System.out.println( "on file: "
                            + jars[i].getAbsolutePath() );
                    }
                }
            }
            
            refreshSubsystemUpdaters();
        }
    }


    // ----------------------------------------------------------
    /**
     * If automatic updates are turned on, scan all current subsystems and
     * download any new versions of update files that are available.
     * @param frameworkDir The directory where all subsystems are located
     * @param mainBundle The main bundle location
     */
    private void downloadNewUpdates( File frameworkDir, File mainBundle )
    {
        // Simply return if no updates should be automatically downloaded
        if ( !willUpdateAutomatically() ) return;

        File[] subdirs = frameworkDir.listFiles();
        for ( int i = 0; i < subdirs.length; i++ )
        {
            downloadUpdateIfNecessary( getUpdaterFor( subdirs[i], true ) );
        }
        
        // Now handle the application update, if available
        downloadUpdateIfNecessary( getUpdaterFor( mainBundle, false ) );
    }


    // ----------------------------------------------------------
    /**
     * Check for any updates for the given subsystem, and download them.
     * @param updater The {@link SubsystemUpdater} to download for
     */
    private void downloadUpdateIfNecessary( SubsystemUpdater updater )
    {
        if ( !updateDir.exists() )
        {
            updateDir.mkdirs();
        }
        String msg = updater.downloadUpdateIfNecessary( updateDir );
        if ( msg != null )
        {
            System.out.println( "Error downloading update for "
                + updater.name + ":" );
            System.out.println( msg );
        }
    }


    // ----------------------------------------------------------
    /**
     * @param subdirs
     * @param mainBundle
     */
    private String classPathFrom( File[] subdirs, File mainBundle )
    {
        StringBuffer buffer = new StringBuffer( 20 * subdirs.length );

        // First, handle all the subsystems
        for ( int i = 0; i < subdirs.length; i++ )
        {
            getUpdaterFor( subdirs[i], true ).addToClasspath( buffer );
        }

        // Now handle the main bundle itself
        getUpdaterFor( mainBundle, true ).addToClasspath( buffer );

        return buffer.toString();
    }
    

    // ----------------------------------------------------------
    /**
     * Attempt to load the properties settings for this adaptor.
     */
    private void loadProperties()
    {
        properties = new Properties();
        if ( propertiesFile.exists() )
        {
            try
            {
                InputStream is = new FileInputStream( propertiesFile );
                properties.load( is );
                is.close();
            }
            catch ( IOException e )
            {
                // We're not using log4j, since that may be within a
                // subsystem that needs updating
                System.out.println( "Error loading properties from "
                           + propertiesFile.getAbsolutePath()
                           + ":"
                           + e.getMessage() );
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Save the properties settings for this adaptor.
     */
    private void commitProperties()
    {
        try
        {
            OutputStream out = new FileOutputStream( propertiesFile );
            properties.store( out, "WCServletAdaptor properties" );
            out.close();
        }
        catch ( IOException e )
        {
            System.out.println( "Error saving WCServletAdaptor properties to "
                       + propertiesFile.getAbsolutePath()
                       + ":"
                       + e.getMessage() );
        }
    }


    // ----------------------------------------------------------
    /**
     * Get the {@link SubsystemUpdater} for the specified subsystem location.
     * Creates a new updater on demand, if necessary.
     * @param dir the subsystem location to look up
     * @param deleteBeforeUpdate passed to the SubsystemUpdater if one must
     *     be constructed
     * @return the corresponding updater
     */
    private SubsystemUpdater getUpdaterFor( File    dir,
                                            boolean deleteBeforeUpdate )
    {
        SubsystemUpdater updater = null;
        if ( dir != null )
        {
            updater = (SubsystemUpdater)subsystems.get( dir );
            if ( updater == null )
            {
                updater = new SubsystemUpdater( dir /*, deleteBeforeUpdate*/ );
                subsystems.put( dir, updater );
            }
        }
        return updater;
    }


    // ----------------------------------------------------------
    /**
     * Refresh the subsystems collection so that it reflects the new
     * updates (intended to be called after downloading/applying pending
     * updates).
     */
    private void refreshSubsystemUpdaters()
    {
        Map oldSubsystems = subsystems;
        subsystems = new HashMap();
        for ( Iterator i = oldSubsystems.keySet().iterator(); i.hasNext(); )
        {
            File dir = (File)i.next();
            // TODO: read delete param from current updater 
            getUpdaterFor( dir, true );
        }
        oldSubsystems.clear();
    }


    // ----------------------------------------------------------
    /**
     * This comparator is used by {@link #applyNecessaryUpdates(File)} to
     * sort framework directories.  It ensures that the high-priority
     * frameworks are first on the resulting classpath.
     */
    private static class FrameworkComparator
        implements java.util.Comparator
    {
        // ----------------------------------------------------------
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare( Object o1, Object o2 )
        {
            String left  = ( (File)o1 ).getName();
            String right = ( (File)o2 ).getName();
            if ( left.equals( right ) )
            {
                return 0;
            }
            else
            {
                for ( int i = 0; i < PRIORITY_FRAMEWORKS.length; i++ )
                {
                    if ( PRIORITY_FRAMEWORKS[i].equals( left ) )
                    {
                        return -1;
                    }
                    else if ( PRIORITY_FRAMEWORKS[i].equals( right ) )
                    {
                        return 1;
                    }
                }
                return left.compareTo( right );
            }
        }
    }


    //~ Instance/static variables .............................................

    private javax.servlet.ServletContext innerContext   = null;
    private javax.servlet.ServletContext wrappedContext = null;
    private String                       woClasspath    = null;
    private Properties                   properties;
    private File                         propertiesFile;
    private File                         updateDir;
    private File                         frameworkDir;
    private Map                          subsystems     = new HashMap();
    private boolean                      needsLicense   = false;

    private static WCServletAdaptor instance;

    private static final String FRAMEWORK_SUBDIR =
        "/Contents/Frameworks/Library/Frameworks";
    private static final String[] PRIORITY_FRAMEWORKS = {
        "EOJDBCPrototypes.framework",
        "ERJars.framework",
        "ERExtensions.framework"
    };
    private static final String UPDATE_SUBDIR  = "pending-updates";
    private static final String APP_JAR_PREFIX = "webcat_";
}
