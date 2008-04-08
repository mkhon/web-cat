/*==========================================================================*\
 |  $Id: AuthorsSection.java,v 1.1 2008/04/08 18:31:00 aallowat Exp $
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

import java.util.ArrayList;
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class AuthorsSection extends AbstractSection
{
    // ------------------------------------------------------------------------
    public AuthorsSection(MetadataFormPage formPage, Composite parent,
            FormToolkit toolkit, ModuleHandle model)
    {
        super(
                formPage,
                parent,
                toolkit,
                model,
                "Authors",
                "This section describes the authors who contributed to the content in this report template.");

        authors = new ArrayList<AuthorInfo>();
    }


    // ------------------------------------------------------------------------
    @Override
    protected void createContent(Composite parent)
    {
        GridLayout layout = new GridLayout(4, false);
        parent.setLayout(layout);

        Composite tableContainer = createGridComposite(parent, 2, false);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 4;
        tableContainer.setLayoutData(gd);

        authorTable = new List(tableContainer, SWT.BORDER | SWT.V_SCROLL);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = 48;
        authorTable.setLayoutData(gd);

        authorTable.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                authorSelectionChanged();
            }
        });

        Composite buttonContainer = createGridComposite(tableContainer, 1, true);
        gd = new GridData(SWT.FILL, SWT.FILL, false, true);
        buttonContainer.setLayoutData(gd);

        createButton(buttonContainer, "Add", new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                addAuthor();
            }
        });

        removeButton = createButton(buttonContainer, "Remove",
                new SelectionAdapter()
                {
                    public void widgetSelected(SelectionEvent e)
                    {
                        removeAuthor();
                    }
                });

        Label separator = getToolkit().createSeparator(parent, SWT.HORIZONTAL);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.horizontalSpan = 4;
        separator.setLayoutData(gd);

        createLabel(parent, "Name*:", SWT.CENTER);
        nameField = createText(parent, false, SWT.NONE, SWT.DEFAULT, 3);
        nameField.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                nameFieldModified();
            }
        });

        createLabel(parent, "E-mail:", SWT.CENTER);
        emailField = createText(parent, false, SWT.NONE);
        emailField.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                emailFieldModified();
            }
        });

        createLabel(parent, "URL:", SWT.CENTER);
        urlField = createText(parent, false, SWT.NONE);
        urlField.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                urlFieldModified();
            }
        });

        createLabel(parent, "Affiliation:", SWT.CENTER);
        affiliationField = createText(parent, false, SWT.NONE);
        affiliationField.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                affiliationFieldModified();
            }
        });

        createLabel(parent, "Phone:", SWT.CENTER);
        phoneField = createText(parent, false, SWT.NONE);
        phoneField.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                phoneFieldModified();
            }
        });
    }


    // ------------------------------------------------------------------------
    protected void updateControls()
    {
        ModuleHandle model = getModel();

        authors.clear();
        authorTable.removeAll();

        int count = ReportMetadata.getAuthorsCount(model);

        for (int i = 1; i <= count; i++)
        {
            AuthorInfo author = new AuthorInfo();

            author.name = ReportMetadata.getAuthorName(model, i);
            author.email = ReportMetadata.getAuthorEmail(model, i);
            author.url = ReportMetadata.getAuthorURL(model, i);
            author.affiliation = ReportMetadata.getAuthorAffiliation(model, i);
            author.phone = ReportMetadata.getAuthorPhone(model, i);

            authors.add(author);
            authorTable.add(author.name);
        }

        authorSelectionChanged();
    }


    // ------------------------------------------------------------------------
    public void saveModel()
    {
        ModuleHandle model = getModel();

        ReportMetadata.setAuthorsCount(model, authors.size());

        for (int i = 0; i < authors.size(); i++)
        {
            AuthorInfo author = authors.get(i);

            ReportMetadata.setAuthorName(model, i + 1, author.name);
            ReportMetadata.setAuthorEmail(model, i + 1, author.email);
            ReportMetadata.setAuthorURL(model, i + 1, author.url);
            ReportMetadata.setAuthorAffiliation(model, i + 1,
                    author.affiliation);
            ReportMetadata.setAuthorPhone(model, i + 1, author.phone);
        }
    }


    // ------------------------------------------------------------------------
    private void authorSelectionChanged()
    {
        disableDirtyListener();

        int index = authorTable.getSelectionIndex();

        if (index == -1)
        {
            nameField.setText("");
            emailField.setText("");
            urlField.setText("");
            affiliationField.setText("");
            phoneField.setText("");

            nameField.setEnabled(false);
            emailField.setEnabled(false);
            urlField.setEnabled(false);
            affiliationField.setEnabled(false);
            phoneField.setEnabled(false);

            removeButton.setEnabled(false);
        }
        else
        {
            AuthorInfo author = authors.get(index);

            safeSetText(nameField, author.name);
            safeSetText(emailField, author.email);
            safeSetText(urlField, author.url);
            safeSetText(affiliationField, author.affiliation);
            safeSetText(phoneField, author.phone);

            nameField.setEnabled(true);
            emailField.setEnabled(true);
            urlField.setEnabled(true);
            affiliationField.setEnabled(true);
            phoneField.setEnabled(true);

            removeButton.setEnabled(true);
        }

        enableDirtyListener();
    }


    // ------------------------------------------------------------------------
    private void addAuthor()
    {
        authors.add(new AuthorInfo());
        authorTable.add("<enter name>");
        authorTable.select(authorTable.getItemCount() - 1);

        authorSelectionChanged();

        nameField.setFocus();

        getFormPage().markAsDirty();
    }


    // ------------------------------------------------------------------------
    private void removeAuthor()
    {
        int index = authorTable.getSelectionIndex();

        if (index != -1)
        {
            authors.remove(index);
            authorTable.remove(index);

            authorSelectionChanged();

            getFormPage().markAsDirty();
        }
    }


    // ------------------------------------------------------------------------
    private void nameFieldModified()
    {
        int index = authorTable.getSelectionIndex();

        if (index != -1)
        {
            AuthorInfo author = authors.get(index);
            author.name = nameField.getText();

            authorTable.setItem(index, author.name);
        }
    }


    // ------------------------------------------------------------------------
    private void emailFieldModified()
    {
        int index = authorTable.getSelectionIndex();

        if (index != -1)
        {
            AuthorInfo author = authors.get(index);
            author.email = emailField.getText();
        }
    }


    // ------------------------------------------------------------------------
    private void urlFieldModified()
    {
        int index = authorTable.getSelectionIndex();

        if (index != -1)
        {
            AuthorInfo author = authors.get(index);
            author.url = urlField.getText();
        }
    }


    // ------------------------------------------------------------------------
    private void affiliationFieldModified()
    {
        int index = authorTable.getSelectionIndex();

        if (index != -1)
        {
            AuthorInfo author = authors.get(index);
            author.affiliation = affiliationField.getText();
        }
    }


    // ------------------------------------------------------------------------
    private void phoneFieldModified()
    {
        int index = authorTable.getSelectionIndex();

        if (index != -1)
        {
            AuthorInfo author = authors.get(index);
            author.phone = phoneField.getText();
        }
    }


    // ------------------------------------------------------------------------
    private class AuthorInfo
    {
        public String name;
        public String email;
        public String url;
        public String affiliation;
        public String phone;
    }


    private List authorTable;
    private Button removeButton;
    private Text nameField;
    private Text emailField;
    private Text urlField;
    private Text affiliationField;
    private Text phoneField;

    private java.util.List<AuthorInfo> authors;
}
