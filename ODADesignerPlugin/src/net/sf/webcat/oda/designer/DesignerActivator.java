/*==========================================================================*\
 |  $Id: DesignerActivator.java,v 1.1 2008/04/08 18:31:13 aallowat Exp $
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

package net.sf.webcat.oda.designer;

import net.sf.webcat.oda.designer.contentassist.ContentAssistManager;
import net.sf.webcat.oda.designer.i18n.Messages;
import net.sf.webcat.oda.designer.preferences.IPreferencesConstants;
import net.sf.webcat.oda.designer.preview.PreviewQueryManager;
import net.sf.webcat.oda.designer.preview.PreviewingResultCache;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

// ------------------------------------------------------------------------
/**
 * The activator class controls the plug-in life cycle
 *
 * @author Tony Allevato (Virginia Tech Computer Science)
 * @version $Id: DesignerActivator.java,v 1.1 2008/04/08 18:31:13 aallowat Exp $
 */
public class DesignerActivator extends AbstractUIPlugin implements IStartup
{
    // The plug-in ID
    public static final String PLUGIN_ID = "net.sf.webcat.oda.ui"; //$NON-NLS-1$

    // The shared instance
    private static DesignerActivator plugin;


    // ----------------------------------------------------------
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
    }


    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop(context);
    }


    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static DesignerActivator getDefault()
    {
        return plugin;
    }


    public void earlyStartup()
    {
        // Force loading of this information in the background when Eclipse
        // starts.
        getContentAssistManager();
    }


    public PreviewingResultCache getPreviewCache()
    {
        if (previewCache == null)
        {
            Preferences prefs = DesignerActivator.getDefault().getPluginPreferences();

            // Try to construct a previewing result set provider based on the
            // current preferences settings.
            String url = prefs.getString(IPreferencesConstants.SERVER_URL_KEY);
            String username = prefs
                    .getString(IPreferencesConstants.USERNAME_KEY);
            String password = prefs
                    .getString(IPreferencesConstants.PASSWORD_KEY);
            int maxRecords = 0;
            int timeout = 0;

            try
            {
                maxRecords = Integer.parseInt(prefs
                        .getString(IPreferencesConstants.MAX_RECORDS_KEY));

                timeout = Integer
                        .parseInt(prefs
                                .getString(IPreferencesConstants.CONNECTION_TIMEOUT_KEY));
            }
            catch (NumberFormatException e)
            {
                // Do nothing; keep default value of 0.
            }

            if (url == null || url.trim().length() == 0)
            {
                MessageBox box = new MessageBox(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(), SWT.OK
                        | SWT.ICON_WARNING);

                box.setMessage(Messages.ACTIVATOR_SERVER_URL_NOT_SET);
                box.open();
            }
            else
            {
                previewCache = new PreviewingResultCache();
                previewCache.setServerCredentials(url, username, password);
                previewCache.setMaxRecords(maxRecords);
                previewCache.setTimeout(timeout);
            }
        }

        return previewCache;
    }


    public ContentAssistManager getContentAssistManager()
    {
        if (contentAssistManager == null)
        {
            Preferences prefs = DesignerActivator.getDefault().getPluginPreferences();
            String url = prefs.getString(IPreferencesConstants.SERVER_URL_KEY);
            String username = prefs
                    .getString(IPreferencesConstants.USERNAME_KEY);
            String password = prefs
                    .getString(IPreferencesConstants.PASSWORD_KEY);

            contentAssistManager = new ContentAssistManager();
            contentAssistManager.setServerCredentials(url, username, password);
        }

        return contentAssistManager;
    }


    public PreviewQueryManager getPreviewQueryManager()
    {
        if (previewQueryManager == null)
        {
            previewQueryManager = new PreviewQueryManager();
        }

        return previewQueryManager;
    }


    private PreviewingResultCache previewCache;

    private ContentAssistManager contentAssistManager;

    private PreviewQueryManager previewQueryManager;
}
