/*==========================================================================*\
 |  $Id: MetadataFormPage.java,v 1.3 2008/04/11 01:58:56 aallowat Exp $
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

import java.lang.reflect.InvocationTargetException;
import net.sf.webcat.oda.designer.i18n.Messages;
import org.eclipse.birt.report.designer.internal.ui.command.WrapperCommandStack;
import org.eclipse.birt.report.designer.internal.ui.editors.parts.event.ModelEventManager;
import org.eclipse.birt.report.designer.internal.ui.views.data.DataViewPage;
import org.eclipse.birt.report.designer.internal.ui.views.data.DataViewTreeViewerPage;
import org.eclipse.birt.report.designer.internal.ui.views.outline.DesignerOutlinePage;
import org.eclipse.birt.report.designer.ui.editors.IPageStaleType;
import org.eclipse.birt.report.designer.ui.editors.IReportEditorPage;
import org.eclipse.birt.report.designer.ui.editors.IReportProvider;
import org.eclipse.birt.report.designer.ui.editors.pages.ReportFormPage;
import org.eclipse.birt.report.designer.ui.views.attributes.AttributeViewPage;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class MetadataFormPage extends ReportFormPage
{
    // ------------------------------------------------------------------------
    @Override
    public void doSave(IProgressMonitor monitor)
    {
        saveModelWithProgress(monitor);

        IReportProvider provider = getProvider();

        if (provider != null)
        {
            provider.saveReport(getModel(), getEditorInput(), monitor);
            firePropertyChange(PROP_DIRTY);
        }

        isDirty = false;
        markPageStale(IPageStaleType.NONE);
        getEditor().editorDirtyStateChanged();
    }


    // ------------------------------------------------------------------------
    @Override
    public void doSaveAs()
    {
        final IReportProvider provider = getProvider();

        if (provider != null)
        {
            IPath path = provider.getSaveAsPath(getEditorInput());

            if (path == null)
            {
                return;
            }

            final IEditorInput input = provider.createNewEditorInput(path);

            setInput(input);

            IRunnableWithProgress op = new IRunnableWithProgress()
            {
                public synchronized final void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException
                {
                    final InvocationTargetException[] iteHolder = new InvocationTargetException[1];

                    try
                    {
                        IWorkspaceRunnable workspaceRunnable = new IWorkspaceRunnable()
                        {
                            public void run(IProgressMonitor pm)
                                    throws CoreException
                            {
                                execute(pm);
                            }
                        };

                        ResourcesPlugin.getWorkspace().run(workspaceRunnable,
                                ResourcesPlugin.getWorkspace().getRoot(),
                                IResource.NONE, monitor);
                    }
                    catch (CoreException e)
                    {
                        throw new InvocationTargetException(e);
                    }
                    catch (OperationCanceledException e)
                    {
                        throw new InterruptedException(e.getMessage());
                    }

                    // Re-throw the InvocationTargetException, if any occurred
                    if (iteHolder[0] != null)
                    {
                        throw iteHolder[0];
                    }
                }


                public void execute(final IProgressMonitor monitor)
                {
                    try
                    {
                        doSave(monitor);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            };

            try
            {
                new ProgressMonitorDialog(getSite().getWorkbenchWindow()
                        .getShell()).run(false, true, op);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    // ------------------------------------------------------------------------
    private void saveModel(boolean showProgress)
    {
        if (showProgress)
        {
            try
            {
                new ProgressMonitorDialog(getSite().getWorkbenchWindow()
                        .getShell()).run(false, true,
                        new IRunnableWithProgress()
                        {
                            public void run(IProgressMonitor monitor)
                                    throws InvocationTargetException,
                                    InterruptedException
                            {
                                saveModelWithProgress(monitor);
                            }
                        });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            saveModelWithProgress(null);
        }
    }


    // ------------------------------------------------------------------------
    private void saveModelWithProgress(IProgressMonitor monitor)
    {
        int count = managedForm.getParts().length;

        if (monitor != null)
            monitor.beginTask(Messages.OVERVIEW_PAGE_STORING_PROGRESS, count);

        for (IFormPart part : managedForm.getParts())
        {
            if (part instanceof AbstractSection)
            {
                // Create a progress dialog here
                AbstractSection section = (AbstractSection) part;
                section.saveModel();
            }

            if (monitor != null)
                monitor.worked(1);
        }

        if (monitor != null)
            monitor.done();
    }


    protected void hookModelEventManager(Object model)
    {
        getModelEventManager( ).hookRoot( model);

        getModelEventManager( ).hookCommandStack( new WrapperCommandStack( ) );
    }

    protected void unhookModelEventManager(Object model)
    {
        getModelEventManager( ).unhookRoot( model);
    }

    // ------------------------------------------------------------------------
    @Override
    public boolean isDirty()
    {
        return isDirty;
    }


    // ------------------------------------------------------------------------
    @Override
    public void createPartControl(Composite parent)
    {
        try
        {
            managedForm = new ManagedForm(parent);

            TableWrapLayout layout = new TableWrapLayout();
            layout.numColumns = 2;
            layout.makeColumnsEqualWidth = false;
            managedForm.getForm().getBody().setLayout(layout);

            FormToolkit toolkit = managedForm.getToolkit();
            toolkit.decorateFormHeading(managedForm.getForm().getForm());
            managedForm.getForm().setText(Messages.OVERVIEW_PAGE_TITLE);

            createBodyContent(managedForm.getForm().getBody());

            hookModelEventManager(getModel());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        updateContentValues();
    }


    // ------------------------------------------------------------------------
    private void createBodyContent(final Composite parent)
    {
        SectionPart part;
        TableWrapData twd;

        FormToolkit toolkit = managedForm.getToolkit();

        final Composite leftPane = managedForm.getToolkit().createComposite(
                parent);
        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = 1;
        layout.makeColumnsEqualWidth = true;
        leftPane.setLayout(layout);

        twd = new TableWrapData(TableWrapData.FILL_GRAB);
        leftPane.setLayoutData(twd);

        final Composite rightPane = managedForm.getToolkit().createComposite(
                parent);
        layout = new TableWrapLayout();
        layout.numColumns = 1;
        layout.makeColumnsEqualWidth = true;
        rightPane.setLayout(layout);

        twd = new TableWrapData(TableWrapData.FILL_GRAB);
        rightPane.setLayoutData(twd);

        part = new GeneralInfoSection(this, leftPane, toolkit, getModel());
        twd = new TableWrapData(TableWrapData.FILL_GRAB);
        part.getSection().setLayoutData(twd);
        managedForm.addPart(part);

        part = new CopyrightLicenseSection(this, leftPane, toolkit, getModel());
        twd = new TableWrapData(TableWrapData.FILL_GRAB);
        part.getSection().setLayoutData(twd);
        managedForm.addPart(part);

        part = new AppearanceBehaviorSection(this, leftPane, toolkit,
                getModel());
        twd = new TableWrapData(TableWrapData.FILL_GRAB);
        part.getSection().setLayoutData(twd);
        managedForm.addPart(part);

        part = new AuthorsSection(this, rightPane, toolkit, getModel());
        twd = new TableWrapData(TableWrapData.FILL_GRAB);
        part.getSection().setLayoutData(twd);
        managedForm.addPart(part);

        part = new RepositorySection(this, rightPane, toolkit, getModel());
        twd = new TableWrapData(TableWrapData.FILL_GRAB);
        part.getSection().setLayoutData(twd);
        managedForm.addPart(part);
    }


    // ------------------------------------------------------------------------
    public int getStaleType()
    {
        return staleType;
    }


    // ------------------------------------------------------------------------
    public void markPageStale(int type)
    {
        staleType = type;
    }


    // ------------------------------------------------------------------------
    public void markAsDirty()
    {
        isDirty = true;
        markPageStale(IPageStaleType.MODEL_CHANGED);
        firePropertyChange(PROP_DIRTY);
        getEditor().editorDirtyStateChanged();
    }


    // ------------------------------------------------------------------------
    public boolean onBroughtToTop(IReportEditorPage prevPage)
    {
        updateContentValues();

        return true;
    }


    // ------------------------------------------------------------------------
    private void updateContentValues()
    {
        for (IFormPart part : managedForm.getParts())
        {
            if (part instanceof AbstractSection)
            {
                AbstractSection section = (AbstractSection) part;
                section.updateContentValues();
            }
        }
    }


    // ------------------------------------------------------------------------
    public String getId()
    {
        return ID;
    }


    // ------------------------------------------------------------------------
    public Control getPartControl()
    {
        if (managedForm != null)
        {
            return managedForm.getForm();
        }
        else
        {
            return null;
        }
    }


    // ------------------------------------------------------------------------
    public IManagedForm getManagedForm()
    {
        return managedForm;
    }


    // ------------------------------------------------------------------------
    public boolean canLeaveThePage()
    {
        if (isDirty())
        {
            saveModel(true);
        }

        return super.canLeaveThePage();
    }


    // ------------------------------------------------------------------------
    private IReportProvider getProvider()
    {
        return (IReportProvider) getEditor().getAdapter(IReportProvider.class);
    }


    // ------------------------------------------------------------------------
    protected ModelEventManager getModelEventManager()
    {
        if (manager == null)
        {
            manager = new ModelEventManager();
        }

        return manager;
    }


    // ------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter)
    {
        if (adapter == IContentOutlinePage.class)
        {
            DesignerOutlinePage outlinePage = new DesignerOutlinePage(
                    getModel());
            getModelEventManager().addModelEventProcessor(
                    outlinePage.getModelProcessor());
            return outlinePage;
        }
        else if (adapter == DataViewPage.class)
        {
            DataViewTreeViewerPage page = new DataViewTreeViewerPage(getModel());
            getModelEventManager().addModelEventProcessor(
                    page.getModelProcessor());
            return page;
        }
        else if (adapter == AttributeViewPage.class)
        {
            return new AttributeViewPage();
        }

        return super.getAdapter(adapter);
    }


    private static final String ID = "net.sf.webcat.oda.ui.editors.metadata"; //$NON-NLS-1$

    private boolean isDirty = false;
    private int staleType;
    private ManagedForm managedForm;
    private ModelEventManager manager;
}
