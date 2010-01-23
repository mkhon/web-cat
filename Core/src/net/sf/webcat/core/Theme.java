/*==========================================================================*\
 |  $Id: Theme.java,v 1.5 2010/01/23 02:36:09 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2008-2009 Virginia Tech
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

import java.io.File;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import er.extensions.foundation.ERXValueUtilities;

// -------------------------------------------------------------------------
/**
 * Represents a theme (stored in the Core framework).
 *
 * @author
 *  @author Last changed by $Author: aallowat $
 *  @version $Revision: 1.5 $, $Date: 2010/01/23 02:36:09 $
 */
public class Theme
    extends _Theme
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new Theme object.
     */
    public Theme()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * Look up and return a theme object by its directory name (short
     * symbolic name, not its human-readable name).
     *
     * @param dirName the subdirectory name of the theme
     * @return The matching theme object
     */
    public static Theme themeFromName(String dirName)
    {
        ensureThemesLoaded();
        return themeForDirName(
            EOSharedEditingContext.defaultSharedEditingContext(), dirName);
    }


    // ----------------------------------------------------------
    /**
     * Return the default theme object to use when users have not
     * chosen one of their own.
     *
     * @return The default theme
     */
    public static Theme defaultTheme()
    {
        return themeFromName("dream-way");
    }


    // ----------------------------------------------------------
    /**
     * Get a list of shared hteme objects that have already been loaded
     * into the shared editing context.
     * @return an array of all theme objects
     */
    public static NSArray<Theme> themes()
    {
        ensureThemesLoaded();
        return themes;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Get a human-readable representation of this theme, which is
     * the same as {@link #name()}.
     * @return this theme's name
     */
    public String userPresentableDescription()
    {
        return name();
    }


    // ----------------------------------------------------------
    public Theme parent()
    {
        if (baseIsNotSet)
        {
            String baseName = (String)properties().valueForKey("extends");
            if (baseName != null)
            {
                base = themeFromName(baseName);
                baseIsNotSet = false;
            }
        }
        return base;
    }


    // ----------------------------------------------------------
    /**
     * Provided for OGNL compatibility of the pseudo-key .inherit.
     * WO will correctly use valueForKey()/valueForKeyPath(), but
     * OGNL won't, so we need this stub for evaluating OGNL expressions
     * using the .inherit key.
     */
    public Object inherit()
    {
        return valueForKey(INHERIT_KEY);
    }


    // ----------------------------------------------------------
    public Object valueForKey(String key)
    {
        if (INHERIT_KEY.equals(key))
        {
            if (inheriter == null)
            {
                inheriter = new PropertyInheriter();
            }
            return inheriter;
        }
        else if (key.startsWith(INHERIT_PREFIX))
        {
            key = key.substring(INHERIT_PREFIX_LEN);
            if (inheriter == null)
            {
                inheriter = new PropertyInheriter();
            }
            return inheriter.valueForKey(key);
        }
        else
        {
            return super.valueForKey(key);
        }
    }


    // ----------------------------------------------------------
    public void takeValueForKey(Object value, String key)
    {
        if (key.equals(INHERIT_KEY))
        {
            throw new IllegalArgumentException("cannot set the .inherit key");
        }
        else if (key.startsWith(INHERIT_PREFIX))
        {
            key = key.substring(INHERIT_PREFIX_LEN);
        }
        super.takeValueForKey(value, key);
    }


    // ----------------------------------------------------------
    public Object valueForKeyPath(String keyPath)
    {
        if (keyPath.startsWith(INHERIT_PREFIX))
        {
            keyPath = keyPath.substring(INHERIT_PREFIX_LEN);
            if (inheriter == null)
            {
                inheriter = new PropertyInheriter();
            }
            return inheriter.valueForKeyPath(keyPath);
        }
        else
        {
            return super.valueForKeyPath(keyPath);
        }
    }


    // ----------------------------------------------------------
    public void takeValueForKeyPath(Object value, String keyPath)
    {
        if (keyPath.startsWith(INHERIT_PREFIX))
        {
            keyPath = keyPath.substring(INHERIT_PREFIX_LEN);
        }
        super.takeValueForKey(value, keyPath);
    }


    // ----------------------------------------------------------
    public String dojoTheme()
    {
        Object result = valueForKeyPath("properties.dojoTheme");
        return (result == null)
            ? "nihilo"
            : result.toString();
    }


    // ----------------------------------------------------------
    public String linkTags()
    {
        if (linkTags == null)
        {
            linkTags = (parent() == null)
                ? ""
                : parent().linkTags();
            try
            {
                Object cssFileList = properties().valueForKey("cssOrder");
                if (cssFileList != null && cssFileList instanceof NSArray)
                {
                    NSArray cssFiles = (NSArray)cssFileList;
                    String baseLocation =
                        "Core.framework/WebServerResources/theme/"
                        + dirName() + "/";
                    for (int i = 0; i < cssFiles.count(); i++)
                    {
                        NSDictionary css =
                            (NSDictionary)cssFiles.objectAtIndex(i);
                        linkTags += "<link rel=\"stylesheet\" type=\"text/css\""
                            + "href=\""
                            + WCResourceManager.resourceURLFor(
                                baseLocation
                                + css.valueForKey("file"),
                                null)
                            + "\"";
                        String media = (String)css.valueForKey("media");
                        if (media != null)
                        {
                            linkTags += " media=\"" + media + "\"";
                        }
                        linkTags += " />";
                    }
                }
            }
            catch (Exception e)
            {
                Application.emailExceptionToAdmins(
                    e,
                    null,
                    "Unexpected exception trying to decode theme properties "
                    + "for theme: " + dirName() + "(" + id() + ").");
            }
        }
        return linkTags;
    }


    // ----------------------------------------------------------
    public void refresh()
    {
        File plist = new File(themeBaseDir(), dirName());
        if (plist.exists())
        {
            refreshFrom(plist);
        }
        else
        {
            log.error("Unable to refresh theme " + this + ": file "
                + "not found: " + plist);
        }
    }


    // ----------------------------------------------------------
    public static void refreshThemes()
    {
        log.debug("refreshThemes()");
        if (!themeBaseDir().exists()) return;

        EOEditingContext ec = Application.newPeerEditingContext();
        try
        {
            ec.lock();
            ec.setSharedEditingContext(null);

            for (File subdir : themeBaseDir().listFiles())
            {
                if (subdir.isDirectory())
                {
                    File plist = new File(subdir, "theme.plist");
                    if (plist.exists())
                    {
                        Theme themeToUpdate =
                            themeForDirName(ec, subdir.getName());
                        if (themeToUpdate != null)
                        {
                            // Theme already exists, so check to see if
                            // it needs to be updated
                            NSTimestamp modTime = new NSTimestamp(
                                plist.lastModified());
                            if (themeToUpdate.lastUpdate() != null
                                && themeToUpdate.lastUpdate().after(modTime))
                            {
                                // No update needed
                                log.debug("theme " + themeToUpdate.dirName()
                                    + " is up to date");
                                themeToUpdate = null;
                            }
                        }
                        else
                        {
                            // Create it
                            log.info("Registering new theme: "
                                + subdir.getName());
                            themeToUpdate =
                                create(ec, subdir.getName(), false, false);
                        }
                        if (themeToUpdate != null)
                        {
                            themeToUpdate.refreshFrom(plist);
                            ec.saveChanges();
                        }
                    }
                }
            }

        }
        finally
        {
            ec.unlock();
            Application.releasePeerEditingContext( ec );
        }

        log.debug( "refreshing shared theme objects" );
        themes = allObjectsOrderedByName(
            EOSharedEditingContext.defaultSharedEditingContext());
        if (log.isDebugEnabled())
        {
            log.debug("Registered themes = " + themes);
        }
    }


    // ----------------------------------------------------------
    public void setUpdateMutableFields(boolean value)
    {
        // Silently swallow this operation, since Themes are held in
        // the shared editing context and should not be modified, except
        // under very controlled conditions.
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    private void refreshFrom(File plist)
    {
        NSTimestamp now = new NSTimestamp();
        try
        {
            log.info("reloading theme settings from: "
                + plist.getCanonicalPath());
            MutableDictionary dict =
                MutableDictionary.fromPropertyList(plist);
            setProperties(dict);
            String name = (String)dict.objectForKey("name");
            setName(name);
            setLastUpdate(now);
            setIsForThemeDevelopers(ERXValueUtilities.booleanValue(
                dict.valueForKey("isForThemeDevelopers")));
        }
        catch (Exception e)
        {
            log.error("Unable to refresh theme from " + plist, e);
            Application.emailExceptionToAdmins(
                e, null, "Error refreshing theme.");
        }
    }


    // ----------------------------------------------------------
    private static void ensureThemesLoaded()
    {
        if (themes == null)
        {
            refreshThemes();
        }
    }


    // ----------------------------------------------------------
    private static File themeBaseDir()
    {
        if (themeBaseDir == null)
        {
            themeBaseDir = new File(
                ((Application)Application.application()).subsystemManager()
                    .subsystem("Core").myResourcesDir(),
                "../WebServerResources/theme");

        }
        return themeBaseDir;
    }


    // ----------------------------------------------------------
    private class PropertyInheriter
        implements NSKeyValueCodingAdditions
    {
        // ----------------------------------------------------------
        public void takeValueForKeyPath(Object value, String keyPath)
        {
            Theme.this.takeValueForKeyPath(value, keyPath);
        }


        // ----------------------------------------------------------
        public Object valueForKeyPath(String keyPath)
        {
            Object result = Theme.this.valueForKeyPath(keyPath);
            if (result == null && parent() != null)
            {
                result = parent().valueForKeyPath(INHERIT_PREFIX + keyPath);
            }
            return result;
        }


        // ----------------------------------------------------------
        public void takeValueForKey(Object value, String key)
        {
            Theme.this.takeStoredValueForKey(value, key);
        }


        // ----------------------------------------------------------
        public Object valueForKey(String key)
        {
            Object result = Theme.this.valueForKey(key);
            if (result == null && parent() != null)
            {
                result = parent().valueForKey(INHERIT_PREFIX + key);
            }
            return result;
        }

    }


    //~ Instance/static variables .............................................

    private String linkTags;
    private Theme base;
    private boolean baseIsNotSet = true;
    private PropertyInheriter inheriter;

    private static NSArray<Theme> themes;
    private static File themeBaseDir;
    private static final String INHERIT_KEY    = "inherit";
    private static final String INHERIT_PREFIX = INHERIT_KEY + ".";
    private static final int INHERIT_PREFIX_LEN = INHERIT_PREFIX.length();
}
