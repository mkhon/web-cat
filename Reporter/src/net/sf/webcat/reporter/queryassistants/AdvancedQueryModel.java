/*==========================================================================*\
 |  $Id: AdvancedQueryModel.java,v 1.5 2008/10/28 15:52:30 aallowat Exp $
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

package net.sf.webcat.reporter.queryassistants;

import net.sf.webcat.core.Application;
import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOEntityClassDescription;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.EOSQLExpressionFactory;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.jdbcadaptor.JDBCExpression;
import er.extensions.ERXSQLHelper;

//-------------------------------------------------------------------------
/**
 * A query model implementation.
 *
 * @author aallowat
 * @version $Id: AdvancedQueryModel.java,v 1.5 2008/10/28 15:52:30 aallowat Exp $
 */
public class AdvancedQueryModel
    extends AbstractQueryAssistantModel
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    public AdvancedQueryModel()
    {
        // Create a default criterion.

        criteria = new NSMutableArray<AdvancedQueryCriterion>();
        insertNewCriterionAtIndex(0);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    @Override
    public EOQualifier qualifierFromValues()
    {
        NSMutableArray<EOQualifier> terms = new NSMutableArray<EOQualifier>();

        for (AdvancedQueryCriterion cri : criteria)
        {
            EOQualifier termQual =
                AdvancedQueryComparison.qualifierForCriterion(cri);

            if (termQual != null)
            {
                System.out.println(termQual.toString());
                terms.addObject(termQual);
            }
        }

        return new EOAndQualifier(terms);
    }


    // ----------------------------------------------------------
    @Override
    public void takeValuesFromQualifier(EOQualifier qualifier)
    {
        criteria = new NSMutableArray<AdvancedQueryCriterion>();

        if (qualifier instanceof EOAndQualifier)
        {
            EOAndQualifier qAnd = (EOAndQualifier)qualifier;

            for (EOQualifier q : qAnd.qualifiers())
            {
                AdvancedQueryCriterion cri =
                    AdvancedQueryComparison.criterionForQualifier(q);

                if (cri != null)
                {
                    criteria.addObject(cri);
                }
            }
        }

        // Insert a blank criterion if the qualifier produces none, so that
        // editing can be performed correctly.
        if (criteria.count() == 0)
        {
            insertNewCriterionAtIndex(0);
        }
    }


    // ----------------------------------------------------------
    public NSArray<AdvancedQueryCriterion> criteria()
    {
        return criteria;
    }


    // ----------------------------------------------------------
    public void setCriteria(NSArray<AdvancedQueryCriterion> value)
    {
        criteria = value.mutableClone();
    }


    // ----------------------------------------------------------
    public void insertNewCriterionAtIndex(int index)
    {
        AdvancedQueryCriterion newCriterion = new AdvancedQueryCriterion();

        newCriterion.setCastType(String.class);
        newCriterion.setComparison(AdvancedQueryComparison.IS_EQUAL_TO);
        newCriterion.setComparandType(AdvancedQueryCriterion.COMPARAND_LITERAL);

        criteria.insertObjectAtIndex(newCriterion, index);
    }


    // ----------------------------------------------------------
    public void removeCriterionAtIndex(int index)
    {
        criteria.removeObjectAtIndex(index);

        if (criteria.count() == 0)
        {
            insertNewCriterionAtIndex(0);
        }
    }


    //~ Instance/static variables .............................................

    private NSMutableArray<AdvancedQueryCriterion> criteria;
}
