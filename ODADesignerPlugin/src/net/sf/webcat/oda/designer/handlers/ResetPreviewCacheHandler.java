/*==========================================================================*\
 |  $Id: ResetPreviewCacheHandler.java,v 1.2 2008/04/13 22:04:53 aallowat Exp $
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

package net.sf.webcat.oda.designer.handlers;

import net.sf.webcat.oda.designer.DesignerActivator;
import net.sf.webcat.oda.designer.i18n.Messages;
import net.sf.webcat.oda.designer.preview.PreviewingResultCache;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

//------------------------------------------------------------------------
/**
 * An Eclipse command handler that clears the data that has been cached for
 * previewing operations.
 *
 * @author Tony Allevato (Virginia Tech Computer Science)
 * @version $Id: ResetPreviewCacheHandler.java,v 1.2 2008/04/13 22:04:53 aallowat Exp $
 */
public class ResetPreviewCacheHandler extends AbstractHandler
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IWorkbenchWindow window = HandlerUtil
                .getActiveWorkbenchWindowChecked(event);

        PreviewingResultCache cache = DesignerActivator.getDefault()
                .getPreviewCache();

        if (cache != null)
        {
            MessageBox box = new MessageBox(window.getShell(),
                    SWT.ICON_QUESTION | SWT.YES | SWT.NO);

            box.setMessage(Messages.RESETPREVIEWCACHE_CONFIRMATION);
            box.setText(Messages.RESETPREVIEWCACHE_MSGBOX_NAME);

            if (box.open() == SWT.YES)
            {
                cache.reset();
            }
        }

        return null;
    }
}
