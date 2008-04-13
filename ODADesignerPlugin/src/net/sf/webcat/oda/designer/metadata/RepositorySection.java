/*==========================================================================*\
 |  $Id: RepositorySection.java,v 1.4 2008/04/13 22:04:52 aallowat Exp $
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

import net.sf.webcat.oda.commons.ReportMetadata;
import net.sf.webcat.oda.designer.i18n.Messages;
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

//------------------------------------------------------------------------
/**
 * A section on the Overview page that displays information about a report
 * template that was stored when it was uploaded to a Web-CAT template
 * repository.
 *
 * @author Tony Allevato (Virginia Tech Computer Science)
 * @version $Id: RepositorySection.java,v 1.4 2008/04/13 22:04:52 aallowat Exp $
 */
public class RepositorySection extends AbstractSection
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    public RepositorySection(OverviewFormPage formPage, Composite parent,
            FormToolkit toolkit, ModuleHandle model)
    {
        super(formPage, parent, toolkit, model,
                Messages.REPOSITORY_SECTION_TITLE,
                Messages.REPOSITORY_SECTION_DESCRIPTION);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    protected void createContent(Composite parent)
    {
        GridLayout layout = new GridLayout(2, false);
        parent.setLayout(layout);

        createLabel(parent, Messages.REPOSITORY_ID, SWT.CENTER);
        idField = createFormText(parent, false);
        idField.setColor("disabled", Display.getCurrent().getSystemColor( //$NON-NLS-1$
                SWT.COLOR_GRAY));

        createLabel(parent, Messages.REPOSITORY_VERSION, SWT.CENTER);
        versionField = createFormText(parent, false);
        versionField.setColor("disabled", Display.getCurrent().getSystemColor( //$NON-NLS-1$
                SWT.COLOR_GRAY));

        createLabel(parent, Messages.REPOSITORY_UPLOAD_DATE, SWT.CENTER);
        uploadDateField = createFormText(parent, false);
        uploadDateField.setColor("disabled", Display.getCurrent() //$NON-NLS-1$
                .getSystemColor(SWT.COLOR_GRAY));

        createLabel(parent, Messages.REPOSITORY_ROOT_ID, SWT.CENTER);
        rootIdField = createFormText(parent, false);
        rootIdField.setColor("disabled", Display.getCurrent().getSystemColor( //$NON-NLS-1$
                SWT.COLOR_GRAY));

        createLabel(parent, Messages.REPOSITORY_CHANGE_HISTORY, SWT.LEAD);
        changeHistoryField = createFormText(parent, true);
        changeHistoryField.setColor("disabled", Display.getCurrent() //$NON-NLS-1$
                .getSystemColor(SWT.COLOR_GRAY));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.minimumHeight = 56;
        changeHistoryField.setLayoutData(gd);
    }


    // ----------------------------------------------------------
    public void updateControls()
    {
        String text;

        ModuleHandle module = getModel();

        String id = ReportMetadata.getRepositoryId(module);

        if (id == null)
            text = NOT_YET_UPLOADED;
        else
            text = "<form>" + id + "</form>"; //$NON-NLS-1$ //$NON-NLS-2$

        idField.setText(text, true, true);

        String rootId = ReportMetadata.getRepositoryRootId(module);

        if (rootId == null)
            text = NOT_YET_UPLOADED;
        else
            text = "<form>" + rootId + "</form>"; //$NON-NLS-1$ //$NON-NLS-2$

        rootIdField.setText(text, true, true);

        String version = ReportMetadata.getRepositoryVersion(module);

        if (version == null)
            text = NOT_YET_UPLOADED;
        else
            text = "<form>" + version + "</form>"; //$NON-NLS-1$ //$NON-NLS-2$

        versionField.setText(text, true, true);

        String uploadDate = ReportMetadata.getRepositoryUploadDate(module);

        if (uploadDate == null)
            text = NOT_YET_UPLOADED;
        else
            text = "<form>" + uploadDate + "</form>"; //$NON-NLS-1$ //$NON-NLS-2$

        uploadDateField.setText(text, true, true);

        String changeHistory = ReportMetadata
                .getRepositoryChangeHistory(module);

        if (changeHistory == null)
            text = NOT_YET_UPLOADED;
        else
            text = "<form>" + changeHistory + "</form>"; //$NON-NLS-1$ //$NON-NLS-2$

        changeHistoryField.setText(text, true, true);
    }


    //~ Static/instance variables .............................................

    private static final String NOT_YET_UPLOADED = "<form><p><span color=\"disabled\">"
            + Messages.REPOSITORY_SECTION_NOT_YET_UPLOADED
            + "</span></p></form>"; //$NON-NLS-1$ //$NON-NLS-2$

    private FormText idField;
    private FormText versionField;
    private FormText uploadDateField;
    private FormText rootIdField;
    private FormText changeHistoryField;
}
