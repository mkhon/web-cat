/*==========================================================================*\
 |  $Id: BrowserUtils.java,v 1.1 2008/04/08 18:31:00 aallowat Exp $
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

package net.sf.webcat.oda.designer.metadata;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class BrowserUtils
{
    public static void openURL(String url)
    {
        try
        {
            PlatformUI.getWorkbench().getBrowserSupport().createBrowser(
                    IWorkbenchBrowserSupport.AS_EXTERNAL,
                    "WebCATDesignerBrowser", "url", "url")
                    .openURL(new URL(url));
        }
        catch (MalformedURLException e1)
        {
            e1.printStackTrace();
        }
        catch (PartInitException e1)
        {
            e1.printStackTrace();
        }
    }
}
