/*==========================================================================*\
 |  $Id: WCServletAdaptor.java,v 1.5 2007/01/30 02:20:48 stedwar2 Exp $
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
 *  @version $Id: WCServletAdaptor.java,v 1.5 2007/01/30 02:20:48 stedwar2 Exp $
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
            String kind = request.getParameter( "kind" );
            if ( kind == null )
            {
                sendLicenseForm( response,
                    "You must choose a deployment license method." );
            }
            else if ( kind.equals( "key" ) )
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
                        properties.remove( INSTALLED_WOROOT );
                        commitProperties();

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
                            "Error writing license key file: "
                            + e.getMessage() );
                    }
                    if ( !needsLicense )
                    {
                        sendLicenseAcknowledgement( response );
                    }
                }
            }
            else
            {
                String woroot = request.getParameter( "woroot" );
                if ( woroot == null )
                {
                    sendLicenseForm( response,
                        "You must enter a directory for the local "
                        + "WebObjects installation." );
                }
                else
                {
                    File woRoot = new File( woroot );
                    if ( !woRoot.exists() )
                    {
                        sendLicenseForm( response,
                            "The local WebObjects root you specified "
                            + "could not be found." );
                    }
                    else if ( !woRoot.isDirectory() )
                    {
                        sendLicenseForm( response,
                            "The local WebObjects root you specified "
                            + "is not a directory." );
                    }
                    else
                    {
                        // Save woroot in the properties file
                        properties.setProperty( INSTALLED_WOROOT, woroot );
                        commitProperties();

                        // Force classpath to be reloaded
                        String webInfRoot = super.getServletContext()
                            .getRealPath( "WEB-INF" );
                        File webInfDir = new File( webInfRoot );
                        applyNecessaryUpdates( webInfDir );
                        if ( wrappedContext != null )
                        {
                            ( (WCServletContext)wrappedContext )
                                .setWOClasspath( woClasspath );
                        }
                        try
                        {
                            // Try to clear installed wo application wrapper
                            // via reflection (need to access a private
                            // field!)
                            Class parent = com.webobjects.jspservlet
                                .WOServletAdaptor.class;
                            java.lang.reflect.Field field = parent
                                .getDeclaredField( "woApplicationWrapper" );
                            field.setAccessible( true );
                            field.set( this, null );
                            field = parent
                                .getDeclaredField( "classLoader" );
                            field.setAccessible( true );
                            field.set( this, null );

                            // Now, try to restart WO
                            super.init();
                            needsLicense = false;
                        }
                        catch ( Exception e )
                        {
                            e.printStackTrace();
                            response.setContentType( "text/html" );
                            PrintWriter out = new PrintWriter(
                                response.getOutputStream() );
                            out.println( "<html><head>" );
                            out.println(
                                "<title>WebObjects License Set</title>" );
                            out.println( "</head><body>" );
                            out.println( "<h1>WebObjects License Has Been " );
                            out.println( "Set</h1>" );
                            out.println( "<p>Web-CAT has been configured to " );
                            out.println( "use your locally installed copy " );
                            out.println( "of WebObjects.</p><p>Please " );
                            out.println( "restart Web-CAT (or its servlet " );
                            out.println( "container) to continue.</p>" );
                            out.println( "</body></html>" );
                            out.flush();
                            out.close();
                        }
                    }
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

        // Check for possible local WO install
        String defaultDir = null;
        if ( System.getProperty( "os.name" ).indexOf( "Windows" ) >= 0 )
        {
            defaultDir = "C:/Apple";
            File f = new File( defaultDir ); 
            if ( !f.exists() || !f.isDirectory() )
            {
                // Not found
                defaultDir = null;
            }
        }
        else if ( System.getProperty( "os.name" ).indexOf( "Mac" ) >= 0 )
        {
            defaultDir = "/System";
            File f = new File( defaultDir ); 
            if ( !f.exists() || !f.isDirectory() )
            {
                // Not found
                defaultDir = null;
            }
        }
        else
        {
            defaultDir = "/opt/Apple";
            File f = new File( defaultDir ); 
            if ( !f.exists() || !f.isDirectory() )
            {
                // Not found, so try /usr/local
                defaultDir = "/usr/local/Apple";
                f = new File( defaultDir ); 
                if ( !f.exists() || !f.isDirectory() )
                {
                    // Not found
                    defaultDir = null;
                }
            }
        }

        out.println( "<p>This application requires a valid <b>WebObjects " );
        out.println( "Deployment License</b>.  Please enter your deployment " );
        out.println( "license key.</p>" );
        out.println( "<form method=\"post\" action=\"\">" );
        out.println( "<input type=\"radio\" name=\"kind\" value=\"key\"" );
        if ( defaultDir == null )
        {
            out.println( " checked" );
        }
        out.println( "> " );
        out.println( "Install license key: <input type=\"text\" name=\"key\"");
        out.println( " size=\"40\"/><blockquote><p>\n" );
        out.println( "Enter a WebObjects 5.2.x deployment license key to\n" );
        out.println( "use the built-in WebObjects run-time contained in the\n");
        out.println( "WAR.</p></blockquote>\n" );
        out.println( "<input type=\"radio\" name=\"kind\" value=\"local\"" );
        if ( defaultDir != null )
        {
            out.println( " checked" );
        }
        out.println( "> " );
        out.println( " Use existing WO installation: <input type=\"text\" " );
        out.println( "name=\"woroot\" size=\"60\"" );
        if ( defaultDir != null )
        {
            out.println( " value=\"" + defaultDir + "\"" );
        }
        out.println( "/><blockquote><p>\n" );
        out.println( "Enter the local path to an existing WebObjects\n" );
        out.println( "installation (your local WOROOT) to use your existing\n");
        out.println( "license if you have one.</p></blockquote>\n" );
        out.println( " <input type=\"submit\" " );
        out.println( "value=\"Set License\"/>" );
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
     * @param aFrameworkDir The place to unpack updates
     * @param appDir The application directory, which is where any top-level
     *     updates (e.g., "webcat_*" files) are unpacked
     */
    private void applyPendingUpdates( File aFrameworkDir, File appDir )
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
                    File unpackDir = aFrameworkDir;
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
     * @param aFrameworkDir The directory where all subsystems are located
     * @param mainBundle The main bundle location
     */
    private void downloadNewUpdates( File aFrameworkDir, File mainBundle )
    {
        // Simply return if no updates should be automatically downloaded
        if ( !willUpdateAutomatically() ) return;

        File[] subdirs = aFrameworkDir.listFiles();
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
        String woroot = properties.getProperty( INSTALLED_WOROOT );
        File installedWOFrameworkDir = null;
        if ( woroot != null )
        {
            installedWOFrameworkDir = new File( woroot, "Library/Frameworks" );
            if ( !installedWOFrameworkDir.exists() )
            {
                System.out.println( "Cannot locate installed WO framework "
                    + "directory at: " + installedWOFrameworkDir );
                installedWOFrameworkDir = null;
            }
            else if ( !installedWOFrameworkDir.isDirectory() )
            {
                System.out.println( "Installed WO framework location is not a "
                    + "directory: " + installedWOFrameworkDir );
                installedWOFrameworkDir = null;
            }
            System.out.println( "using WO root = " + installedWOFrameworkDir );
        }

        // First, handle all the subsystems
        for ( int i = 0; i < subdirs.length; i++ )
        {
            File subdir = subdirs[i];
            // Be sure to use the *local* version of JavaWOExtensions (from
            // project WONDER) rather than the default system version.
            if ( installedWOFrameworkDir != null
                 && !"JavaWOExtensions.framework".equals( subdir.getName() ) )
            {
                File localSubdir =
                    new File( installedWOFrameworkDir, subdir.getName() );
                if ( localSubdir.exists() )
                {
                    // use the externally installed version instead
                    subdir = localSubdir;
                }
            }
            getUpdaterFor( subdir, true ).addToClasspath( buffer );
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
    private static final String UPDATE_SUBDIR    = "pending-updates";
    private static final String APP_JAR_PREFIX   = "webcat_";
    private static final String INSTALLED_WOROOT = "installed.woroot";
}
