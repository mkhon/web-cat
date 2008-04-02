/*==========================================================================*\
 |  $Id: ReportDataSet.java,v 1.5 2008/04/02 01:36:38 stedwar2 Exp $
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

package net.sf.webcat.reporter;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

// -------------------------------------------------------------------------
/**
 * TODO: place a real description here.
 *
 * @author
 * @version $Id: ReportDataSet.java,v 1.5 2008/04/02 01:36:38 stedwar2 Exp $
 */
public class ReportDataSet
    extends _ReportDataSet
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new ReportDataSet object.
     */
    public ReportDataSet()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public static ReportDataSet createNewReportDataSet(
    		EOEditingContext ec,
    		ReportTemplate reportTemplate,
    		String uuid,
            String entityName,
    		String name,
            String description,
            int    referenceCount)
    {
        ReportDataSet dataSet = new ReportDataSet();
        ec.insertObject( dataSet );

        dataSet.setReportTemplateRelationship(reportTemplate);
        dataSet.setUuid(uuid);
        dataSet.setWcEntityName(entityName);
        dataSet.setName(name);
        dataSet.setDescription(description);
        dataSet.setReferenceCount(referenceCount);

        return dataSet;
    }
}
