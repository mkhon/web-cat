/*==========================================================================*\
 |  $Id: GeneralInfoSection.java,v 1.1 2008/04/08 18:31:00 aallowat Exp $
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

import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.command.UserPropertyException;
import org.eclipse.birt.report.model.api.core.UserPropertyDefn;
import org.eclipse.birt.report.model.api.metadata.IPropertyType;
import org.eclipse.birt.report.model.metadata.PropertyType;
import org.eclipse.birt.report.model.metadata.StringPropertyType;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class GeneralInfoSection extends AbstractSection
{
    // ------------------------------------------------------------------------
    public GeneralInfoSection(MetadataFormPage formPage, Composite parent,
            FormToolkit toolkit, ModuleHandle model)
    {
        super(formPage, parent, toolkit, model, "General Information",
                "This section describes general information about the report template.");
    }


    // ------------------------------------------------------------------------
    @Override
    protected void createContent(Composite parent)
    {
        GridLayout layout = new GridLayout(2, false);
        parent.setLayout(layout);

        createLabel(parent, "Title*:", SWT.CENTER);
        titleField = createText(parent, false, SWT.NONE);
        titleField.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                if (titleField.getText().length() == 0)
                {
                    addMessage(EMPTY_TITLE_KEY, EMPTY_TITLE_MESSAGE, null,
                            IMessageProvider.ERROR, titleField);
                }
                else
                {
                    removeMessage(EMPTY_TITLE_KEY, titleField);
                }
            }
        });

        createLabel(parent, "Description*:", SWT.LEAD);
        descriptionField = createText(parent, true, SWT.NONE, 56);
        descriptionField.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                if (descriptionField.getText().length() == 0)
                {
                    addMessage(EMPTY_DESCRIPTION_KEY,
                            EMPTY_DESCRIPTION_MESSAGE, null,
                            IMessageProvider.ERROR, descriptionField);
                }
                else
                {
                    removeMessage(EMPTY_DESCRIPTION_KEY, descriptionField);
                }
            }
        });

        createLabel(parent, "Keywords:", SWT.LEAD);
        keywordsField = createText(parent, true, SWT.NONE, 32);
    }


    // ------------------------------------------------------------------------
    protected void updateControls()
    {
        ModuleHandle module = getModel();

        safeSetText(titleField, ReportMetadata.getTitle(module));
        safeSetText(descriptionField, ReportMetadata.getDescription(module));
        safeSetText(keywordsField, ReportMetadata.getKeywords(module));
    }


    // ------------------------------------------------------------------------
    public void saveModel()
    {
        ModuleHandle module = getModel();

        ReportMetadata.setTitle(module, titleField.getText());
        ReportMetadata.setDescription(module, descriptionField.getText());
        ReportMetadata.setKeywords(module, keywordsField.getText());
    }


    private static final String EMPTY_TITLE_KEY = "generalInfo.emptyTitle";
    private static final String EMPTY_TITLE_MESSAGE = "The title of the report must not be empty.";

    private static final String EMPTY_DESCRIPTION_KEY = "generalInfo.emptyDescription";
    private static final String EMPTY_DESCRIPTION_MESSAGE = "The description of the report must not be empty.";

    private Text titleField;
    private Text descriptionField;
    private Text keywordsField;
}
