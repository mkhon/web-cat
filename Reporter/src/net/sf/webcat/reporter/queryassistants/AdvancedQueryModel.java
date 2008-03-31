/*==========================================================================*\
 |  $Id: AdvancedQueryModel.java,v 1.3 2008/03/31 03:07:21 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU General Public License as published by
 |  the Free Software Foundation; either version 2 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU General Public License
 |  along with Web-CAT; if not, write to the Free Software
 |  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 |
 |  Project manager: Stephen Edwards <edwards@cs.vt.edu>
 |  Virginia Tech CS Dept, 660 McBryde Hall (0106), Blacksburg, VA 24061 USA
\*==========================================================================*/

package net.sf.webcat.reporter.queryassistants;

import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

//-------------------------------------------------------------------------
/**
 * A query model implementation.
 *
 * @author aallowat
 * @version $Id: AdvancedQueryModel.java,v 1.3 2008/03/31 03:07:21 stedwar2 Exp $
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

			for (EOQualifier q : (NSArray<EOQualifier>)qAnd.qualifiers())
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
