/*==========================================================================*\
 |  $Id: WCTableHeadings.java,v 1.1 2010/10/28 00:37:30 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2009 Virginia Tech
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

package org.webcat.ui;

import org.webcat.ui._base.WCTableSubcomponent;
import org.webcat.ui.generators.JavascriptGenerator;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver._private.WOComponentReference;
import com.webobjects.appserver._private.WODynamicGroup;
import com.webobjects.appserver._private.WOHTMLBareString;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

//-------------------------------------------------------------------------
/**
 * A component that contains {@link WCTableHeading} instances. This component
 * also provides the paging controls for the table.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2010/10/28 00:37:30 $
 */
public class WCTableHeadings extends WCTableSubcomponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public WCTableHeadings(WOContext context)
    {
        super(context);
    }


    //~ Internal KVC attributes (must be public) ..............................

    public String searchString;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    public void appendToResponse(WOResponse response, WOContext context)
    {
        if (table() == null)
        {
            throw new IllegalStateException("WCTableHeadings must be an "
                    + "immediate child of WCTable.");
        }

        super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    /**
     * Returns the number of columns in the table, computed by counting the
     * number of {@link WCTableHeading} elements contained as immediate children
     * of this element. This is used to determine the "colspan" attribute of
     * the header cell that contains the toolbar controls.
     *
     * @return the number of columns in the table
     */
    public Integer numberOfColumns()
    {
        if (cachedNumberOfColumns == null)
        {
            int count = 0;

            WOElement childTemplate = _childTemplate();
            if (childTemplate instanceof WODynamicGroup)
            {
                WODynamicGroup childGroup = (WODynamicGroup) childTemplate;

                for (WOElement child : childGroup.childrenElements())
                {
                    if (child instanceof WOComponentReference)
                    {
                        WOComponentReference compRef =
                            (WOComponentReference) child;

                        if (compRef._name.equals(
                                WCTableHeading.class.getCanonicalName()))
                        {
                            count++;
                        }
                    }
                    else if (child instanceof WOHTMLBareString)
                    {
                        WOHTMLBareString bareString = (WOHTMLBareString) child;

                        // What a hack...
                        WOResponse response = new WOResponse();
                        bareString.appendToResponse(response, context());
                        String str = response.contentString().toLowerCase();

                        // Count the number of occurrences of "<th" in the
                        // string.

                        int index = -1;
                        while ((index = str.indexOf("<th", index + 1)) != -1)
                        {
                            count++;
                        }
                    }
                }

                if (table().canSelectRows)
                {
                    count++;
                }

                cachedNumberOfColumns = count;
                table().numberOfColumns = count;
            }
        }

        return cachedNumberOfColumns;
    }


    // ----------------------------------------------------------
    public boolean shouldDisplayToolbar()
    {
        return shouldDisplayPaginationControls() || shouldDisplaySearchField();
    }


    // ----------------------------------------------------------
    public boolean shouldDisplayPaginationControls()
    {
        return displayGroup().numberOfObjectsPerBatch() != 0;
    }


    // ----------------------------------------------------------
    public boolean shouldDisplaySearchField()
    {
        return table().searchOnKeyPaths != null;
    }


    // ----------------------------------------------------------
    public String allSelectionCheckBoxName()
    {
        return table().idFor.get("allSelectionCheckBox");
    }


    // ----------------------------------------------------------
    public boolean areAllRowsSelected()
    {
        NSArray<Integer> selIndices = displayGroup().selectionIndexes();

        for (int i = displayGroup().indexOfFirstDisplayedObject();
             i <= displayGroup().indexOfLastDisplayedObject(); i++)
        {
            if (!selIndices.containsObject(i))
            {
                return false;
            }
        }

        return true;
    }


    // ----------------------------------------------------------
    public void setAreAllRowsSelected(boolean value)
    {
        // Do nothing; exists to satisfy KVC. Checking or unchecking this box
        // causes the appropriate thing to happen on the Javascript side, and
        // those checkboxes will cause the selection to be synchronized with
        // the server.
    }


    // ----------------------------------------------------------
    public WOActionResults search()
    {
        return table().filterUsingSearchString(searchString);
    }


    //~ Static/instance variables .............................................

    private Integer cachedNumberOfColumns;
}
