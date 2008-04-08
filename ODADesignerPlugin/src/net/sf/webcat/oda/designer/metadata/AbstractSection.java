/*==========================================================================*\
 |  $Id: AbstractSection.java,v 1.1 2008/04/08 18:30:59 aallowat Exp $
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;

public abstract class AbstractSection extends SectionPart
{
    // ------------------------------------------------------------------------
    public AbstractSection(MetadataFormPage formPage, Composite parent,
            FormToolkit toolkit, ModuleHandle model, String title,
            String description)
    {
        super(parent, toolkit, Section.TITLE_BAR | Section.DESCRIPTION);

        this.formPage = formPage;
        this.model = model;
        this.toolkit = toolkit;

        Section section = getSection();

        section.setText(title);
        section.setDescription(description);

        Composite client = getToolkit().createComposite(section);
        createContent(client);
        section.setClient(client);
    }


    // ------------------------------------------------------------------------
    protected abstract void createContent(Composite parent);


    // ------------------------------------------------------------------------
    public void updateContentValues()
    {
        disableDirtyListener();
        updateControls();
        enableDirtyListener();
    }


    // ------------------------------------------------------------------------
    protected void updateControls()
    {
        // Implement in subclasses if necessary.
    }


    // ------------------------------------------------------------------------
    public void saveModel()
    {
        // Implement in subclasses if necessary.
    }


    // ------------------------------------------------------------------------
    protected Label createLabel(Composite parent, String text, int valign)
    {
        Label label = getToolkit().createLabel(parent, text);
        label.setForeground(getToolkit().getColors()
                .getColor(IFormColors.TITLE));

        GridData gd = new GridData(SWT.LEAD, valign, false, false);
        label.setLayoutData(gd);

        return label;
    }


    // ------------------------------------------------------------------------
    protected FormText createFormText(Composite parent, boolean vFill)
    {
        FormText formText = new FormText(parent, SWT.NONE);

        int valign = vFill ? SWT.FILL : SWT.CENTER;

        GridData gd = new GridData(SWT.FILL, valign, true, vFill);
        formText.setLayoutData(gd);

        return formText;
    }


    // ------------------------------------------------------------------------
    protected Text createText(Composite parent, boolean multiline, int style)
    {
        return createText(parent, multiline, style, SWT.DEFAULT);
    }


    // ------------------------------------------------------------------------
    protected Text createText(Composite parent, boolean multiline, int style,
            int height)
    {
        return createText(parent, multiline, style, height, 1);
    }


    // ------------------------------------------------------------------------
    protected Text createText(Composite parent, boolean multiline, int style,
            int height, int colSpan)
    {
        style |= SWT.BORDER;

        if (multiline)
        {
            style |= SWT.MULTI | SWT.WRAP | SWT.V_SCROLL;
        }

        int valign = multiline ? SWT.FILL : SWT.CENTER;

        Text text = getToolkit().createText(parent, "", style);
        GridData gd = new GridData(SWT.FILL, valign, true, false);
        gd.widthHint = 30;
        gd.heightHint = height;
        gd.horizontalSpan = colSpan;
        text.setLayoutData(gd);

        text.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                if (!listenersDisabled)
                {
                    formPage.markAsDirty();
                }
            }
        });

        return text;
    }


    // ------------------------------------------------------------------------
    protected Combo createCombo(Composite parent)
    {
        Combo combo = new Combo(parent, SWT.NONE);
        GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        combo.setLayoutData(gd);

        combo.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                if (!listenersDisabled)
                {
                    formPage.markAsDirty();
                }
            }
        });

        return combo;
    }


    // ------------------------------------------------------------------------
    protected Button createButton(Composite parent, String text,
            SelectionListener listener)
    {
        Button button = toolkit.createButton(parent, text, SWT.PUSH);
        GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
        button.setLayoutData(gd);

        if (listener != null)
        {
            button.addSelectionListener(listener);
        }

        return button;
    }


    // ------------------------------------------------------------------------
    protected Composite createGridComposite(Composite parent, int numColumns,
            boolean sameWidth)
    {
        Composite comp = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(numColumns, sameWidth);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        comp.setLayout(layout);

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        comp.setLayoutData(gd);

        return comp;
    }


    // ------------------------------------------------------------------------
    protected void safeSetText(Text control, String value)
    {
        control.setText(value != null ? value : "");
    }


    // ------------------------------------------------------------------------
    protected void safeSetText(Combo control, String value)
    {
        control.setText(value != null ? value : "");
    }


    // ------------------------------------------------------------------------
    protected void addMessage(Object key, String messageText, Object data,
            int type, Control control)
    {
        getManagedForm().getMessageManager().addMessage(key, messageText, data,
                type, control);
    }


    // ------------------------------------------------------------------------
    protected void removeMessage(Object key, Control control)
    {
        getManagedForm().getMessageManager().removeMessage(key, control);
    }


    // ------------------------------------------------------------------------
    protected MetadataFormPage getFormPage()
    {
        return formPage;
    }


    // ------------------------------------------------------------------------
    protected FormToolkit getToolkit()
    {
        return toolkit;
    }


    // ------------------------------------------------------------------------
    protected ModuleHandle getModel()
    {
        return model;
    }


    // ------------------------------------------------------------------------
    protected void disableDirtyListener()
    {
        listenersDisabled = true;
    }


    // ------------------------------------------------------------------------
    protected void enableDirtyListener()
    {
        listenersDisabled = false;
    }


    private boolean listenersDisabled = false;
    private MetadataFormPage formPage;
    private FormToolkit toolkit;
    private ModuleHandle model;
}
