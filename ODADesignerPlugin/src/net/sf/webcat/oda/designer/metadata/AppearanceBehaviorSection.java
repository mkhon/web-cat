/*==========================================================================*\
 |  $Id: AppearanceBehaviorSection.java,v 1.2 2008/04/11 00:58:36 aallowat Exp $
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

import java.util.Locale;
import net.sf.webcat.oda.designer.i18n.Messages;
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import com.ibm.icu.util.ULocale;

public class AppearanceBehaviorSection extends AbstractSection
{
    // ------------------------------------------------------------------------
    public AppearanceBehaviorSection(MetadataFormPage formPage,
            Composite parent, FormToolkit toolkit, ModuleHandle model)
    {
        super(
                formPage,
                parent,
                toolkit,
                model,
                Messages.APP_BEHAV_SECTION_TITLE,
                Messages.APP_BEHAV_SECTION_DESCRIPTION);
    }


    // ------------------------------------------------------------------------
    @Override
    protected void createContent(Composite parent)
    {
        GridLayout layout = new GridLayout(2, false);
        parent.setLayout(layout);

        Label label;

        label = createLabel(parent, Messages.APP_BEHAV_LANGUAGE, SWT.CENTER);
        label
                .setToolTipText(Messages.APP_BEHAV_LANGUAGE_TOOLTIP);
        languageField = createCombo(parent);

        for (String language : LanguageTable.getInstance().getDisplayNames())
        {
            languageField.add(language);
        }

        label = createLabel(parent, Messages.APP_BEHAV_PREFERRED_RENDERER, SWT.CENTER);
        label
                .setToolTipText(Messages.APP_BEHAV_PREFERRED_RENDERER_TOOLTIP);
        rendererField = createCombo(parent);

        rendererField.add("html");
        rendererField.add("pdf");
        rendererField.add("csv");
    }


    // ------------------------------------------------------------------------
    protected void updateControls()
    {
        ModuleHandle module = getModel();

        String language = ReportMetadata.getLanguage(module);

        if (language == null)
            language = "en";

        language = LanguageTable.getInstance().getDisplayNameForName(language);

        safeSetText(languageField, language);
        safeSetText(rendererField, ReportMetadata.getPreferredRenderer(module));
    }


    // ------------------------------------------------------------------------
    public void saveModel()
    {
        ModuleHandle module = getModel();

        String language = languageField.getText();
        String name = LanguageTable.getInstance().getNameForDisplayName(
                language);

        if (name == null)
        {
            name = language;
        }

        ReportMetadata.setLanguage(module, name);
        ReportMetadata.setPreferredRenderer(module, rendererField.getText());
    }


    private Combo languageField;

    private Combo rendererField;
}
