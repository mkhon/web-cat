/*==========================================================================*\
 |  $Id: ColumnMappingTableViewer.java,v 1.1 2008/04/08 18:31:09 aallowat Exp $
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.sf.webcat.oda.commons.DataSetDescription;
import net.sf.webcat.oda.core.impl.DataTypes;
import net.sf.webcat.oda.designer.i18n.Messages;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 *
 * @author Tony Allevato
 */
public class ColumnMappingTableViewer
{
    // -----------------------------------------------------------------------
    /**
     *
     * @param parent
     */
    public ColumnMappingTableViewer(Composite parent)
    {
        GridData gd;

        // Main container
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        container.setLayout(layout);

        // Table viewer
        viewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        viewer.getTable().setHeaderVisible(true);
        viewer.getTable().setLinesVisible(true);

        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        viewer.getControl().setLayoutData(gd);

        // Up/down/remove button container
        Composite buttonContainer = new Composite(container, SWT.NONE);
        buttonContainer.setLayout(new FillLayout(SWT.VERTICAL));

        gd = new GridData(SWT.CENTER, SWT.BEGINNING, false, false);
        buttonContainer.setLayoutData(gd);

        // Up button
        upButton = new Button(buttonContainer, SWT.PUSH);
        upButton.setText(Messages.DATASET_COLUMN_MOVE_UP);

        // Down button
        downButton = new Button(buttonContainer, SWT.PUSH);
        downButton.setText(Messages.DATASET_COLUMN_MOVE_DOWN);

        // Remove button
        removeButton = new Button(buttonContainer, SWT.PUSH);
        removeButton.setText(Messages.DATASET_COLUMN_REMOVE);
    }


    // -----------------------------------------------------------------------
    /**
     *
     * @return
     */
    public TableViewer getViewer()
    {
        return viewer;
    }


    // -----------------------------------------------------------------------
    /**
     *
     * @return
     */
    public Composite getControl()
    {
        return container;
    }


    // -----------------------------------------------------------------------
    /**
     *
     * @return
     */
    public Button getUpButton()
    {
        return upButton;
    }


    // -----------------------------------------------------------------------
    /**
     *
     * @return
     */
    public Button getDownButton()
    {
        return downButton;
    }


    // -----------------------------------------------------------------------
    /**
     *
     * @return
     */
    public Button getRemoveButton()
    {
        return removeButton;
    }


    // -----------------------------------------------------------------------
    /**
     *
     * @param info
     * @param columnMapping
     * @return
     */
    public List<ColumnMappingElement> refresh(DataSetDescription info,
            Map<String, ColumnMappingElement> columnMapping)
    {
        ArrayList<ColumnMappingElement> columnsList = new ArrayList<ColumnMappingElement>();

        if (info == null)
            return columnsList;

        for (int i = 0; i < info.getColumnCount(); i++)
        {
            ColumnMappingElement element = new ColumnMappingElement();
            element.setColumnName(info.getColumnName(i));
            element.setExpression(info.getColumnExpression(i));

            try
            {
                element.setType(DataTypeUtil.getDataTypeDisplayName(DataTypes
                        .getType(info.getColumnType(i))));
            }
            catch (OdaException e)
            {
                // Ignore exception.
            }

            columnMapping.put(info.getColumnName(i), element);
            columnsList.add(element);
        }

        return columnsList;
    }


    // =======================================================================
    private Composite container;

    private TableViewer viewer;

    private Button removeButton;

    private Button upButton;

    private Button downButton;
}
