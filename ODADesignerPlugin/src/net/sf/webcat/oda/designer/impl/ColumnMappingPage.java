/*==========================================================================*\
 |  $Id: ColumnMappingPage.java,v 1.5 2008/10/02 17:10:26 aallowat Exp $
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

package net.sf.webcat.oda.designer.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.webcat.oda.commons.DataSetDescription;
import net.sf.webcat.oda.designer.DesignerActivator;
import net.sf.webcat.oda.designer.contentassist.ContentAssistManager;
import net.sf.webcat.oda.designer.i18n.Messages;
import net.sf.webcat.oda.designer.ognl.OgnlExpressionCellEditor;
import net.sf.webcat.oda.designer.preview.PreviewQueryBuilder;
import net.sf.webcat.oda.designer.preview.PreviewQueryClause;
import net.sf.webcat.oda.designer.preview.PreviewingResultCache;
import ognl.Ognl;
import ognl.OgnlException;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.Property;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

//------------------------------------------------------------------------
/**
 * This class implements the page in the Web-CAT data set wizard that displays
 * the entity and column mappings for the data set.
 *
 * @author Tony Allevato (Virginia Tech Computer Science)
 * @version $Id: ColumnMappingPage.java,v 1.5 2008/10/02 17:10:26 aallowat Exp $
 */
public class ColumnMappingPage extends DataSetWizardPage
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public ColumnMappingPage(String pageName)
    {
        super(pageName);
        setTitle(pageName);
        setMessage(DEFAULT_MESSAGE);
        setPageComplete(false);
    }


    // ----------------------------------------------------------
    public ColumnMappingPage(String pageName, String title,
            ImageDescriptor titleImage)
    {
        super(pageName, title, titleImage);
        setMessage(DEFAULT_MESSAGE);
        setPageComplete(false);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void createPageCustomControl(Composite parent)
    {
        setControl(createPageControl(parent));

        if (WebCATInformationHolder.hasDestroyed())
            WebCATInformationHolder.start(this.getInitializationDesign());

        initializeControl();
    }


    // ----------------------------------------------------------
    private Control createPageControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));

        GridData gd;

        // Entity description label
        Label descLabel = new Label(composite, SWT.WRAP);
        descLabel.setText(Messages.DATASET_ENTITY_LABEL);
        gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        gd.horizontalSpan = 2;
        gd.widthHint = 350;
        descLabel.setLayoutData(gd);

        // Entity type field label
        Label fieldLabel = new Label(composite, SWT.NONE);
        fieldLabel.setText(Messages.DATASET_ENTITY_COMBO_LABEL);

        // Entity type field drop-down
        entityTypeField = new Combo(composite, SWT.BORDER);
        ContentAssistManager cam = DesignerActivator.getDefault()
                .getContentAssistManager();
        for (String entity : cam.getEntities())
        {
            entityTypeField.add(entity);
        }

        entityTypeField.setText(entityTypeName);

        gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        entityTypeField.setLayoutData(gd);

        entityTypeField.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                if (ognlCellEditor != null)
                    ognlCellEditor.setRootClassName(entityTypeField.getText());

                entityTypeName = entityTypeField.getText();
                updateRelationInformation();
                enablePageControls();
            }
        });

        // Horizontal separator
        Label lineLabel = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.horizontalSpan = 2;
        gd.verticalIndent = 12;
        lineLabel.setLayoutData(gd);

        // Column mapping table description label
        Label tableLabel = new Label(composite, SWT.WRAP);
        tableLabel.setText(Messages.DATASET_COLUMN_MAPPING_LABEL);
        gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        gd.horizontalSpan = 2;
        gd.widthHint = 350;
        tableLabel.setLayoutData(gd);

        // Column mapping table
        columnMappingTable = new ColumnMappingTableViewer(composite);
        columnMappingTable.getViewer().getTable().setHeaderVisible(true);
        columnMappingTable.getViewer().getTable().setLinesVisible(true);

        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        gd.grabExcessVerticalSpace = true;
        gd.heightHint = 150;
        columnMappingTable.getControl().setLayoutData(gd);

        TableColumn column;

        column = new TableColumn(columnMappingTable.getViewer().getTable(),
                SWT.LEFT);
        column.setText(COLUMN_NAME);
        column.setWidth(200);

        column = new TableColumn(columnMappingTable.getViewer().getTable(),
                SWT.LEFT);
        column.setText(EXPRESSION_NAME);
        column.setWidth(200);

        column = new TableColumn(columnMappingTable.getViewer().getTable(),
                SWT.LEFT);
        column.setText(TYPE_NAME);
        column.setWidth(120);

        columnMappingTable.getViewer().setContentProvider(
                new ColumnMappingTableContentProvider());

        columnMappingTable.getViewer().setLabelProvider(
                new ColumnMappingTableLabelProvider());

        columnMappingTable.getViewer().setInput(columnMappingList);
        refreshColumnMappingViewer();

        // Horizontal separator
        lineLabel = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.horizontalSpan = 2;
        gd.verticalIndent = 12;
        lineLabel.setLayoutData(gd);

        // Preview query panel
        Composite panel = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        panel.setLayout(layout);
        gd = new GridData(SWT.FILL, SWT.TOP, true, false);
        gd.horizontalSpan = 2;
        gd.widthHint = 350;
        panel.setLayoutData(gd);

        Label previewLabel = new Label(panel, SWT.WRAP);
        previewLabel.setText(Messages.DATASET_DEFINE_QUERY_LABEL);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        previewLabel.setLayoutData(gd);

        Button previewButton = new Button(panel, SWT.PUSH);
        previewButton.setText(Messages.DATASET_DEFINE_QUERY_BUTTON);
        gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
        previewButton.setLayoutData(gd);

        previewButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                openPreviewQueryBuilder();
            }
        });

        setupListeners();
        setupEditors();

        return composite;
    }


    // ----------------------------------------------------------
    /**
     * Opens the preview query builder dialog.
     */
    private void openPreviewQueryBuilder()
    {
        PreviewQueryClause[] clauses = DesignerActivator.getDefault()
                .getPreviewQueryManager().getQuery(dataSetId);

        PreviewQueryBuilder builder = new PreviewQueryBuilder(getShell(),
                entityTypeName, clauses);

        if (builder.open() == Window.OK)
        {
            DesignerActivator.getDefault().getPreviewQueryManager().addQuery(
                    dataSetId, builder.getClauses());
            DesignerActivator.getDefault().getPreviewQueryManager()
                    .saveToState();

            DesignerActivator.getDefault().getPreviewCache().reset(dataSetId);
        }
    }


    // ----------------------------------------------------------
    private void setupListeners()
    {
        columnMappingTable.getViewer().getTable().addSelectionListener(
                new SelectionAdapter()
                {
                    public void widgetSelected(SelectionEvent e)
                    {
                        enablePageControls();
                    }
                });

        columnMappingTable.getViewer().getTable().addKeyListener(
                new KeyAdapter()
                {
                    @Override
                    public void keyReleased(KeyEvent e)
                    {
                        if (e.keyCode == SWT.DEL)
                        {
                            removeSelectedItem();
                            enablePageControls();
                        }
                    }
                });

        columnMappingTable.getRemoveButton().addSelectionListener(
                new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        removeSelectedItem();
                        enablePageControls();
                    }
                });

        columnMappingTable.getUpButton().addSelectionListener(
                new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        moveUpSelectedItem();
                    }
                });

        columnMappingTable.getDownButton().addSelectionListener(
                new SelectionAdapter()
                {
                    public void widgetSelected(SelectionEvent e)
                    {
                        moveDownSelectedItem();
                    }
                });
    }


    // ----------------------------------------------------------
    private void setupEditors()
    {
        CellEditor[] editors = new CellEditor[3];

        editors[0] = new TextCellEditor(columnMappingTable.getViewer()
                .getTable(), SWT.NONE);

        ognlCellEditor = new OgnlExpressionCellEditor(columnMappingTable
                .getViewer().getTable(), SWT.NONE);
        ognlCellEditor.setRootClassName(entityTypeField.getText());
        editors[1] = ognlCellEditor;

        editors[2] = new ComboBoxCellEditor(columnMappingTable.getViewer()
                .getTable(), dataTypeDisplayNames, SWT.READ_ONLY);

        columnMappingTable.getViewer().setCellEditors(editors);
        columnMappingTable.getViewer().setColumnProperties(
                new String[] { COLUMN_NAME, EXPRESSION_NAME, TYPE_NAME });

        columnMappingTable.getViewer().setCellModifier(
                new ColumnMappingTableCellModifier());
    }


    // ----------------------------------------------------------
    private void updateRelationInformation()
    {
        if (WebCATInformationHolder.hasDestroyed())
            return;

        String queryText = getQueryText();
        WebCATInformationHolder.setPropertyValue(
                Constants.PROP_RELATION_INFORMATION, queryText);

        DataSetDescription relation = new DataSetDescription(queryText);
        WebCATInformationHolder.setPropertyValue(Constants.PROP_ENTITY_TYPE,
                relation.getEntityType());
        WebCATInformationHolder.setPropertyValue(Constants.PROP_DATA_SET_ID,
                relation.getUniqueId());
    }


    // ----------------------------------------------------------
    private void removeSelectedItem()
    {
        int index = columnMappingTable.getViewer().getTable()
                .getSelectionIndex();
        int count = columnMappingTable.getViewer().getTable().getItemCount();

        if (index > -1 && index < count)
        {
            TableItem item = columnMappingTable.getViewer().getTable().getItem(
                    index);
            Object element = item.getData();
            String elementName = "";

            if (element instanceof ColumnMappingElement && element != newColumn)
            {
                ColumnMappingElement entry = (ColumnMappingElement) element;
                elementName = entry.getColumnName();

                columnMappingTable.getViewer().getTable().select(index);

                columnMap.remove(elementName);
                columnMappingList.remove(index);

                updateRelationInformation();
            }
        }

        refreshColumnMappingViewer();
    }


    // ----------------------------------------------------------
    private void moveUpSelectedItem()
    {
        int index = columnMappingTable.getViewer().getTable()
                .getSelectionIndex();
        int count = columnMappingTable.getViewer().getTable().getItemCount();

        if (index > 0 && index < count)
        {
            ColumnMappingElement element = columnMappingList.get(index);
            columnMappingList.set(index, columnMappingList.get(index - 1));
            columnMappingList.set(index - 1, element);
            refreshColumnMappingViewer();

            updateRelationInformation();
        }
    }


    // ----------------------------------------------------------
    private void moveDownSelectedItem()
    {
        int index = columnMappingTable.getViewer().getTable()
                .getSelectionIndex();
        int count = columnMappingTable.getViewer().getTable().getItemCount();

        if (index > -1 && index < count - 2)
        {
            ColumnMappingElement element = columnMappingList.get(index);
            columnMappingList.set(index, columnMappingList.get(index + 1));
            columnMappingList.set(index + 1, element);
            refreshColumnMappingViewer();

            updateRelationInformation();
        }
    }


    // ----------------------------------------------------------
    private void enablePageControls()
    {
        boolean columnMappingExist = false;
        boolean entityTypeExist = true;

        String entityType = entityTypeField.getText();
        if (entityType == null || entityType.trim().length() == 0)
            entityTypeExist = false;

        columnMappingExist = (columnMappingList != null && columnMappingList
                .size() > 0);

        int selectionIndex = columnMappingTable.getViewer().getTable()
                .getSelectionIndex();
        int count = columnMappingTable.getViewer().getTable().getItemCount();

        columnMappingTable.getDownButton().setEnabled(
                selectionIndex < count - 2);
        columnMappingTable.getUpButton().setEnabled(selectionIndex > 0);

        columnMappingTable.getRemoveButton().setEnabled(
                selectionIndex != -1 && selectionIndex != count - 1);

        setPageComplete(columnMappingExist && entityTypeExist);
    }


    // ----------------------------------------------------------
    @SuppressWarnings("unused")
    private boolean isValidKeyPath(String keypath)
    {
        if (keypath == null || keypath.length() == 0)
            return true;

        try
        {
            Ognl.parseExpression(keypath);
            return true;
        }
        catch (OgnlException e)
        {
            return false;
        }
    }


    // ----------------------------------------------------------
    private String getQueryText()
    {
        // Convert the data set info (entity type and columns) into a text
        // "query" for the ODA engine

        DataSetDescription info = new DataSetDescription();

        info.setUniqueId(dataSetId);
        info.setEntityType(entityTypeName);

        for (int i = 0; i < columnMappingList.size(); i++)
        {
            ColumnMappingElement element = columnMappingList.get(i);

            info.addColumn(element.getColumnName(), element.getExpression(),
                    element.getType());
        }

        return info.getQueryText();
    }


    // ----------------------------------------------------------
    private void refreshColumnMappingViewer()
    {
        columnMappingTable.getViewer().setInput(columnMappingList);
        for (int i = 0; i < columnMappingTable.getViewer().getTable()
                .getItemCount() - 1; i++)
        {
            TableItem ti = columnMappingTable.getViewer().getTable().getItem(i);
            Object element = ti.getData();

            String c1 = "", c2 = "", c3 = "";

            if (element instanceof ColumnMappingElement)
            {
                ColumnMappingElement cme = (ColumnMappingElement) element;
                c1 = cme.getColumnName() == null ? "" : cme.getColumnName();
                c2 = cme.getExpression() == null ? "" : cme.getExpression();
                c3 = cme.getType() == null ? "" : cme.getType();
            }

            ti.setText(0, c1);
            ti.setText(1, c2);
            ti.setText(2, c3);
        }

        newColumn = null;
        columnMappingTable.getViewer().refresh();
    }


    // ----------------------------------------------------------
    /**
     * Initializes the page control with the last edited data set design.
     */
    private void initializeControl()
    {
        String queryText = WebCATInformationHolder
                .getPropertyValue(Constants.PROP_RELATION_INFORMATION);
        if (queryText != null && queryText.trim().length() > 0)
        {
            // initialize controls
            DataSetDescription info = new DataSetDescription(queryText);
            entityTypeName = info.getEntityType();
            dataSetId = info.getUniqueId();

            columnMap = new HashMap<String, ColumnMappingElement>();
            columnMappingList = columnMappingTable.refresh(info, columnMap);

            entityTypeField.setText(entityTypeName);
            refreshColumnMappingViewer();
        }

        enablePageControls();
    }


    // ----------------------------------------------------------
    protected void refresh(DataSetDesign dataSetDesign)
    {
        if (WebCATInformationHolder.hasDestroyed())
            WebCATInformationHolder.start(dataSetDesign);

        this.setMessage(DEFAULT_MESSAGE);

        String queryText = WebCATInformationHolder
                .getPropertyValue(Constants.PROP_RELATION_INFORMATION);
        if (queryText != null && queryText.trim().length() > 0)
        {
            /*
             * RelationInformation info = new RelationInformation(queryText);
             * entityTypeName = info.getEntityType();
             *
             * entityTypeField.setText(entityTypeName);
             * refreshColumnMappingViewer();
             */
        }
    }


    // ----------------------------------------------------------
    protected DataSetDesign collectDataSetDesign(DataSetDesign design)
    {
        if (!hasValidData())
            return design;

        savePage(design);
        return design;
    }


    // ----------------------------------------------------------
    protected boolean canLeave()
    {
        return isPageComplete();
    }


    // ----------------------------------------------------------
    private boolean isUniqueName(String columnName, ColumnMappingElement element)
    {
        boolean success = true;

        if (columnMap != null)
        {
            if (columnMap.get(columnName) != element
                    && columnMap.get(columnName) != null)
            {
                String msg = MessageFormat.format(
                        Messages.DATASET_COLUMN_NAME_IN_USE, columnName);
                setMessage(msg, IMessageProvider.ERROR);
                success = false;
            }
            else
            {
                setMessage(DEFAULT_MESSAGE, IMessageProvider.NONE);
            }
        }
        else
        {
            setMessage(DEFAULT_MESSAGE, IMessageProvider.NONE);
            columnMap = new HashMap<String, ColumnMappingElement>();
            columnMappingList = new ArrayList<ColumnMappingElement>();
        }

        return success;
    }


    // ----------------------------------------------------------
    /**
     * Indicates whether the custom page has valid data to proceed with defining
     * a data set.
     */
    private boolean hasValidData()
    {
        String queryText = getQueryText();

        if (queryText == null || queryText.trim().length() == 0)
            return false;
        else
            return true;
    }


    // ----------------------------------------------------------
    /**
     * Saves the user-defined value in this page, and updates the specified
     * dataSetDesign with the latest design definition.
     */
    private void savePage(DataSetDesign dataSetDesign)
    {
        if (WebCATInformationHolder.hasDestroyed())
            return;

        if (dataSetDesign != null)
        {
            if (dataSetDesign.getQueryText() == null)
            {
                dataSetDesign.setQueryText(WebCATInformationHolder
                        .getPropertyValue(Constants.PROP_RELATION_INFORMATION));
            }

            if (dataSetDesign.getQueryText() != null
                    && !dataSetDesign
                            .getQueryText()
                            .equals(
                                    WebCATInformationHolder
                                            .getPropertyValue(Constants.PROP_RELATION_INFORMATION)))
            {
                dataSetDesign.setQueryText(WebCATInformationHolder
                        .getPropertyValue(Constants.PROP_RELATION_INFORMATION));

                updatePrivateProperties(dataSetDesign);

                DataSetDesignPopulator.populateResultSet(dataSetDesign);

                PreviewingResultCache cache = DesignerActivator.getDefault()
                        .getPreviewCache();
                if (cache != null)
                {
                    DataSetDescription ri = new DataSetDescription(
                            WebCATInformationHolder
                                    .getPropertyValue(Constants.PROP_RELATION_INFORMATION));

                    int count = ri.getColumnCount();
                    String[] expressions = new String[count];

                    for (int i = 0; i < count; i++)
                    {
                        expressions[i] = ri.getColumnExpression(i);
                    }

                    cache.reset(ri.getUniqueId());
                    cache.ensureResultsAreCached(ri.getUniqueId(), ri
                            .getEntityType(), expressions, false);
                }
            }
        }
    }


    // ----------------------------------------------------------
    private void updatePrivateProperties(DataSetDesign dataSetDesign)
    {
        if (dataSetDesign.getPrivateProperties() == null)
        {
            try
            {
                java.util.Properties utilProps = new java.util.Properties();
                utilProps.setProperty(Constants.PROP_ENTITY_TYPE, "");
                utilProps.setProperty(Constants.PROP_DATA_SET_ID, "");

                dataSetDesign.setPrivateProperties(DesignSessionUtil
                        .createDataSetNonPublicProperties(dataSetDesign
                                .getOdaExtensionDataSourceId(), dataSetDesign
                                .getOdaExtensionDataSetId(), utilProps));
            }
            catch (OdaException e)
            {
                // Ignore exception.
            }
        }

        if (dataSetDesign.getPrivateProperties() != null)
        {
            Property property;

            property = dataSetDesign.getPrivateProperties().findProperty(
                    Constants.PROP_ENTITY_TYPE);

            if (property != null)
            {
                property.setNameValue(Constants.PROP_ENTITY_TYPE,
                        WebCATInformationHolder
                                .getPropertyValue(Constants.PROP_ENTITY_TYPE));
            }

            property = dataSetDesign.getPrivateProperties().findProperty(
                    Constants.PROP_DATA_SET_ID);

            if (property != null)
            {
                property.setNameValue(
                                Constants.PROP_DATA_SET_ID,
                                WebCATInformationHolder
                                        .getPropertyValue(Constants.PROP_DATA_SET_ID));
            }
        }
    }


    // ----------------------------------------------------------
    protected void cleanup()
    {
        WebCATInformationHolder.destroy();
    }


    //~ Nested classes ........................................................

    // ----------------------------------------------------------
    private class ColumnMappingTableContentProvider implements
            IStructuredContentProvider
    {
        // ----------------------------------------------------------
        public Object[] getElements(Object inputElement)
        {
            if (inputElement instanceof ArrayList)
            {
                ArrayList<ColumnMappingElement> inputList = new ArrayList<ColumnMappingElement>(
                        10);

                inputList.addAll(columnMappingList);

                if (newColumn == null)
                    newColumn = new ColumnMappingElement();

                inputList.add(newColumn);
                return inputList.toArray();
            }

            return new Object[0];
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
    }


    // ----------------------------------------------------------
    private class ColumnMappingTableLabelProvider implements
            ITableLabelProvider
    {
        // ----------------------------------------------------------
        public Image getColumnImage(Object element, int columnIndex)
        {
            return null;
        }


        // ----------------------------------------------------------
        public String getColumnText(Object element, int columnIndex)
        {
            String value = "";
            ColumnMappingElement cme = (ColumnMappingElement) element;

            if (element != newColumn)
            {
                if (columnIndex == 0)
                {
                    value = cme.getColumnName();
                }
                else if (columnIndex == 1)
                {
                    value = cme.getExpression();
                }
                else
                {
                    value = cme.getType();
                }
            }
            else if (columnIndex == 0)
            {
                value = Messages.DATASET_NEW_COLUMN_PROMPT;
            }

            return value;
        }


        // ----------------------------------------------------------
        public void addListener(ILabelProviderListener listener)
        {
            // Do nothing.
        }


        // ----------------------------------------------------------
        public void dispose()
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


    // ----------------------------------------------------------
    private class ColumnMappingTableCellModifier implements ICellModifier
    {
        // ----------------------------------------------------------
        public boolean canModify(Object element, String property)
        {
            if (element == newColumn && !property.equals(COLUMN_NAME))
                return false;
            else
                return true;
        }


        // ----------------------------------------------------------
        public Object getValue(Object element, String property)
        {
            ColumnMappingElement cme = (ColumnMappingElement) element;

            Object value = "";
            if (property.equals(COLUMN_NAME))
            {
                value = cme.getColumnName();
                if (value == null)
                    value = "";
            }
            else if (property.equals(EXPRESSION_NAME))
            {
                value = cme.getExpression();
            }
            else if (property.equals(TYPE_NAME))
            {
                String temp = cme.getType();

                if (temp == null)
                {
                    value = new Integer(0);
                }
                else
                {
                    for (int i = 0; i < dataTypeDisplayNames.length; i++)
                    {
                        if (temp.equals(dataTypeDisplayNames[i]))
                        {
                            value = new Integer(i);
                            break;
                        }
                    }
                }
            }

            return value;
        }


        // ----------------------------------------------------------
        public void modify(Object element, String property, Object value)
        {
            Object actualElement = ((TableItem) element).getData();
            ColumnMappingElement cme = null;
            if (actualElement instanceof ColumnMappingElement)
                cme = (ColumnMappingElement) actualElement;

            if (value != null)
            {
                if (property.equals(COLUMN_NAME))
                {
                    if (cme != null && isUniqueName((String) value, cme))
                    {
                        if (columnMap.get(cme.getColumnName()) != null)
                        {
                            columnMap.remove(cme.getColumnName());
                            cme.setColumnName((String) value);
                            columnMap.put((String) value, cme);
                        }
                        else
                        {
                            cme.setColumnName((String) value);
                        }

                        if (cme == newColumn)
                        {
                            cme.setType("String"); //$NON-NLS-1$
                        }

                        updateRelationInformation();
                    }
                    else
                    {
                        return;
                    }
                }
                else if (property.equals(EXPRESSION_NAME))
                {
                    /*
                     * if(!isValidKeyPath((String) value)) {
                     * setMessage(Messages.DATASET_EXPRESSION_INVALID,
                     * IMessageProvider.ERROR); } else {
                     * setMessage(DEFAULT_MESSAGE, IMessageProvider.NONE); }
                     */
                    if (cme != null)
                    {
                        cme.setExpression((String) value);
                    }

                    updateRelationInformation();
                }
                else if (property.equals(TYPE_NAME))
                {
                    int selType = ((Integer) value).intValue();

                    if (cme != null)
                    {
                        cme.setType(dataTypeDisplayNames[selType]);
                    }

                    updateRelationInformation();
                }

                columnMappingTable.getViewer().update(
                        ((TableItem) element).getData(), null);

                if (cme != null)
                {
                    if (newColumn != null && newColumn.getColumnName() != null
                            && newColumn.getColumnName().trim().length() > 0)
                    {
                        columnMap.put(newColumn.getColumnName(), newColumn);
                        columnMappingList.add(newColumn);
                        newColumn = null;
                        updateRelationInformation();
                        columnMappingTable.getViewer().refresh();
                        enablePageControls();
                    }
                }
            }
        }
    }


    //~ Static/instance variables .............................................

    private static final String DEFAULT_MESSAGE = Messages.DATASET_DEFAULT_TITLE;
    private static final String COLUMN_NAME = Messages.DATASET_COLUMN_NAME_HEADER;
    private static final String EXPRESSION_NAME = Messages.DATASET_EXPRESSION_HEADER;
    private static final String TYPE_NAME = Messages.DATASET_TYPE_HEADER;

    private Combo entityTypeField;
    private ColumnMappingTableViewer columnMappingTable;
    private OgnlExpressionCellEditor ognlCellEditor;
    private String dataSetId;
    private String entityTypeName = "Submission"; //$NON-NLS-1$
    private List<ColumnMappingElement> columnMappingList = new ArrayList<ColumnMappingElement>();
    private Map<String, ColumnMappingElement> columnMap = new HashMap<String, ColumnMappingElement>();
    private ColumnMappingElement newColumn;

    private static String[] dataTypeDisplayNames = new String[] {
            Messages.DATATYPE_DISPLAYNAME_INTEGER,
            Messages.DATATYPE_DISPLAYNAME_FLOAT,
            Messages.DATATYPE_DISPLAYNAME_DECIMAL,
            Messages.DATATYPE_DISPLAYNAME_STRING,
            Messages.DATATYPE_DISPLAYNAME_TIMESTAMP,
            Messages.DATATYPE_DISPLAYNAME_BOOLEAN };
}
