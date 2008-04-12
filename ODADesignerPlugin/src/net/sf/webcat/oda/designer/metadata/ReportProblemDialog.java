/*==========================================================================*\
 |  $Id: ReportProblemDialog.java,v 1.1 2008/04/12 20:56:05 aallowat Exp $
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
import net.sf.webcat.oda.designer.i18n.Messages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

// ------------------------------------------------------------------------
/**
 * A dialog that is displayed if any problems are found in the report template
 * when it is saved.
 *
 * @author Tony Allevato (Virginia Tech Computer Science)
 * @version $Id: ReportProblemDialog.java,v 1.1 2008/04/12 20:56:05 aallowat Exp $
 */
public class ReportProblemDialog extends TitleAreaDialog
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new report problem dialog with the specified problems to
     * display.
     *
     * @param parentShell
     *            the shell that will be the parent of this dialog
     * @param problems
     *            the problems to be displayed in the dialog
     */
    public ReportProblemDialog(Shell parentShell, ReportProblem[] problems)
    {
        super(parentShell);

        int style = getShellStyle() | SWT.RESIZE;
        setShellStyle(style);

        this.problems = problems;
    }


    // ----------------------------------------------------------
    @Override
    protected Control createContents(Composite parent)
    {
        Control control = super.createContents(parent);

        getShell().setText(Messages.REPORT_PROBLEM_DIALOG_TITLE);
        setTitle(Messages.REPORT_PROBLEM_DIALOG_TITLE);
        setMessage(Messages.REPORT_PROBLEM_DIALOG_MESSAGE);

        return control;
    }


    // ----------------------------------------------------------
    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite composite = (Composite) super.createDialogArea(parent);

        Composite panel = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout(1, true);
        panel.setLayout(layout);

        problemTable = new TableViewer(panel, SWT.BORDER | SWT.FULL_SELECTION);

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 500;
        gd.heightHint = 150;
        problemTable.getControl().setLayoutData(gd);

        ProblemProvider provider = new ProblemProvider();
        problemTable.setContentProvider(provider);
        problemTable.setLabelProvider(provider);
        problemTable.setInput(problems);

        return composite;
    }


    // ----------------------------------------------------------
    protected void createButtonsForButtonBar(Composite parent)
    {
        createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.CLOSE_LABEL, true);
    }


    //~ Inner classes .........................................................

    // ----------------------------------------------------------
    /**
     * A JFace content and label provider that uses the problem list as its data
     * source.
     */
    private class ProblemProvider implements IStructuredContentProvider,
            ITableLabelProvider
    {
        // ----------------------------------------------------------
        public Object[] getElements(Object inputElement)
        {
            return problems;
        }


        // ----------------------------------------------------------
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
            // Do nothing.
        }


        // ----------------------------------------------------------
        public void dispose()
        {
            // Do nothing.
        }


        // ----------------------------------------------------------
        public Image getColumnImage(Object element, int columnIndex)
        {
            int severity = ((ReportProblem) element).getSeverity();

            switch (severity)
            {
            case ReportProblem.SEVERITY_OK:
                return JFaceResources.getImage(DLG_IMG_MESSAGE_INFO);

            case ReportProblem.SEVERITY_WARNING:
                return JFaceResources.getImage(DLG_IMG_MESSAGE_WARNING);

            case ReportProblem.SEVERITY_ERROR:
                return JFaceResources.getImage(DLG_IMG_MESSAGE_ERROR);

            default:
                return null;
            }
        }


        // ----------------------------------------------------------
        public String getColumnText(Object element, int columnIndex)
        {
            return ((ReportProblem) element).getDescription();
        }


        // ----------------------------------------------------------
        public boolean isLabelProperty(Object element, String property)
        {
            return false;
        }


        // ----------------------------------------------------------
        public void addListener(ILabelProviderListener listener)
        {
            // Do nothing.
        }


        // ----------------------------------------------------------
        public void removeListener(ILabelProviderListener listener)
        {
            // Do nothing.
        }
    }


    //~ Static/instance variables .............................................

    private TableViewer problemTable;
    private ReportProblem[] problems;
}
