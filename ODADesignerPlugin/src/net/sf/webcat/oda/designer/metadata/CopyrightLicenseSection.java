/*==========================================================================*\
 |  $Id: CopyrightLicenseSection.java,v 1.1 2008/04/08 18:31:00 aallowat Exp $
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
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class CopyrightLicenseSection extends AbstractSection
{
    // ------------------------------------------------------------------------
    public CopyrightLicenseSection(MetadataFormPage formPage, Composite parent,
            FormToolkit toolkit, ModuleHandle model)
    {
        super(
                formPage,
                parent,
                toolkit,
                model,
                "Copyright and License",
                "This section describes the copyright and license under which this report template is distributed.");
    }


    // ------------------------------------------------------------------------
    @Override
    protected void createContent(Composite parent)
    {
        GridLayout layout = new GridLayout(2, false);
        parent.setLayout(layout);

        createLabel(parent, "Copyright:", SWT.CENTER);
        copyrightField = createText(parent, false, SWT.NONE);

        createLabel(parent, "License:", SWT.CENTER);
        licenseField = createCombo(parent);

        for (String license : LicenseTable.getInstance().getLicenses())
        {
            licenseField.add(license);
        }

        licenseField.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (licenseField.getSelectionIndex() != -1)
                {
                    String license = licenseField.getText();

                    licenseURLField.setText(LicenseTable.getInstance()
                            .getURLForLicense(license));
                }
            }
        });

        createLabel(parent, "License URL:", SWT.CENTER);

        Composite urlComp = createGridComposite(parent, 2, false);

        licenseURLField = createText(urlComp, false, SWT.NONE);

        licenseURLGoButton = createButton(urlComp, "Go...", null);
        licenseURLGoButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                BrowserUtils.openURL(licenseURLField.getText());
            }
        });
    }


    // ------------------------------------------------------------------------
    @Override
    protected void updateControls()
    {
        ModuleHandle module = getModel();

        safeSetText(copyrightField, ReportMetadata.getCopyright(module));
        safeSetText(licenseField, ReportMetadata.getLicense(module));
        safeSetText(licenseURLField, ReportMetadata.getLicenseURL(module));
    }


    // ------------------------------------------------------------------------
    @Override
    public void saveModel()
    {
        ModuleHandle module = getModel();

        ReportMetadata.setCopyright(module, copyrightField.getText());
        ReportMetadata.setLicense(module, licenseField.getText());
        ReportMetadata.setLicenseURL(module, licenseURLField.getText());
    }


    private Text copyrightField;
    private Combo licenseField;
    private Text licenseURLField;
    private Button licenseURLGoButton;
}
