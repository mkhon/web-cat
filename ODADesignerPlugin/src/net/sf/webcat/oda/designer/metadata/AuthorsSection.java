/*==========================================================================*\
 |  $Id: AuthorsSection.java,v 1.3 2008/04/12 20:56:05 aallowat Exp $
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

import java.util.List;
import java.util.ArrayList;
import net.sf.webcat.oda.designer.i18n.Messages;
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class AuthorsSection extends AbstractSection
{
    // ------------------------------------------------------------------------
    public AuthorsSection(OverviewFormPage formPage, Composite parent,
            FormToolkit toolkit, ModuleHandle model)
    {
        super(formPage, parent, toolkit, model, Messages.AUTHORS_SECTION_TITLE,
                Messages.AUTHORS_SECTION_DESCRIPTION);

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

        authorTable = new TableViewer(tableContainer, SWT.BORDER
                | SWT.FULL_SELECTION);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = 36;
        authorTable.getControl().setLayoutData(gd);

        authorTable
                .addPostSelectionChangedListener(new ISelectionChangedListener()
                {
                    public void selectionChanged(SelectionChangedEvent event)
                    {
                        authorSelectionChanged();
                    }
                });

        AuthorProvider provider = new AuthorProvider();
        authorTable.setContentProvider(provider);
        authorTable.setLabelProvider(provider);
        authorTable.setInput(authors);

        Composite buttonContainer = createGridComposite(tableContainer, 1, true);
        gd = new GridData(SWT.FILL, SWT.FILL, false, true);
        buttonContainer.setLayoutData(gd);

        createButton(buttonContainer, Messages.AUTHORS_ADD,
                new SelectionAdapter()
                {
                    public void widgetSelected(SelectionEvent e)
                    {
                        addAuthor();
                    }
                });

        removeButton = createButton(buttonContainer, Messages.AUTHORS_REMOVE,
                new SelectionAdapter()
                {
                    public void widgetSelected(SelectionEvent e)
                    {
                        removeAuthor();
                    }
                });

        createLabel(parent, Messages.AUTHORS_NAME, SWT.CENTER);
        nameField = createText(parent, false, SWT.NONE, SWT.DEFAULT, 3,
                new TrackingFocusListener(getFormPage())
        {
            @Override
            protected void textDidChange()
            {
                nameFieldModified();
            }
        });

        createLabel(parent, Messages.AUTHORS_EMAIL, SWT.CENTER);
        emailField = createText(parent, false, SWT.NONE,
                new TrackingFocusListener(getFormPage())
        {
            @Override
            protected void textDidChange()
            {
                emailFieldModified();
            }
        });

        createLabel(parent, Messages.AUTHORS_URL, SWT.CENTER);
        urlField = createText(parent, false, SWT.NONE,
                new TrackingFocusListener(getFormPage())
        {
            @Override
            protected void textDidChange()
            {
                urlFieldModified();
            }
        });

        createLabel(parent, Messages.AUTHORS_AFFILIATION, SWT.CENTER);
        affiliationField = createText(parent, false, SWT.NONE,
                new TrackingFocusListener(getFormPage())
        {
            @Override
            protected void textDidChange()
            {
                affiliationFieldModified();
            }
        });

        createLabel(parent, Messages.AUTHORS_PHONE, SWT.CENTER);
        phoneField = createText(parent, false, SWT.NONE,
                new TrackingFocusListener(getFormPage())
        {
            @Override
            protected void textDidChange()
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
        }

        authorTable.setInput(authors);
        authorTable.refresh();
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


    // ----------------------------------------------------------
    private AuthorInfo getSelectedAuthor()
    {
        IStructuredSelection selection =
            (IStructuredSelection) authorTable.getSelection();

        if(selection.isEmpty())
        {
            return null;
        }
        else
        {
            return (AuthorInfo) selection.getFirstElement();
        }
    }


    // ------------------------------------------------------------------------
    private void authorSelectionChanged()
    {
        AuthorInfo author = getSelectedAuthor();

        if(author == null)
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
    }


    // ------------------------------------------------------------------------
    private void addAuthor()
    {
        authors.add(new AuthorInfo());
        authorTable.refresh();
        authorTable.getTable().setSelection(authors.size() - 1);

        authorSelectionChanged();

        nameField.setFocus();

        getFormPage().markAsDirty();
    }


    // ------------------------------------------------------------------------
    private void removeAuthor()
    {
        AuthorInfo author = getSelectedAuthor();

        if (author != null)
        {
            authors.remove(author);
            authorTable.refresh();

            authorSelectionChanged();

            getFormPage().markAsDirty();
        }
    }


    // ------------------------------------------------------------------------
    private void nameFieldModified()
    {
        AuthorInfo author = getSelectedAuthor();

        if (author != null)
        {
            author.name = nameField.getText();
            authorTable.update(author, null);
        }
    }


    // ------------------------------------------------------------------------
    private void emailFieldModified()
    {
        AuthorInfo author = getSelectedAuthor();

        if (author != null)
        {
            author.email = emailField.getText();
            authorTable.update(author, null);
        }
    }


    // ------------------------------------------------------------------------
    private void urlFieldModified()
    {
        AuthorInfo author = getSelectedAuthor();

        if (author != null)
        {
            author.url = urlField.getText();
        }
    }


    // ------------------------------------------------------------------------
    private void affiliationFieldModified()
    {
        AuthorInfo author = getSelectedAuthor();

        if (author != null)
        {
            author.affiliation = affiliationField.getText();
        }
    }


    // ------------------------------------------------------------------------
    private void phoneFieldModified()
    {
        AuthorInfo author = getSelectedAuthor();

        if (author != null)
        {
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


    // ----------------------------------------------------------
    private class AuthorProvider
    implements IStructuredContentProvider, ITableLabelProvider
    {
        // ----------------------------------------------------------
        public Object[] getElements(Object inputElement)
        {
            return authors.toArray();
        }


        // ----------------------------------------------------------
        public void dispose()
        {
            // Do nothing.
        }


        // ----------------------------------------------------------
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
            // Do nothing.
        }


        // ----------------------------------------------------------
        public Image getColumnImage(Object element, int columnIndex)
        {
            return null;
        }


        // ----------------------------------------------------------
        public String getColumnText(Object element, int columnIndex)
        {
            AuthorInfo author = (AuthorInfo) element;

            String name = "";

            if(author.name == null)
            {
                name = "<no name provided>";
            }
            else
            {
                name = author.name;
            }

            if(author.email != null)
            {
                name += " (" + author.email + ")";
            }

            return name;
        }


        // ----------------------------------------------------------
        public void addListener(ILabelProviderListener listener)
        {
            // Do nothing.
        }


        // ----------------------------------------------------------
        public boolean isLabelProperty(Object element, String property)
        {
            return false;
        }


        // ----------------------------------------------------------
        public void removeListener(ILabelProviderListener listener)
        {
            // Do nothing.
        }
    }


    //~ Static/instance variables .............................................

    private TableViewer authorTable;
    private Button removeButton;
    private Text nameField;
    private Text emailField;
    private Text urlField;
    private Text affiliationField;
    private Text phoneField;
    private List<AuthorInfo> authors;
}
