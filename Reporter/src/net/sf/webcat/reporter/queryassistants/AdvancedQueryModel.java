package net.sf.webcat.reporter.queryassistants;

import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

public class AdvancedQueryModel extends AbstractQueryAssistantModel
{
	private NSMutableArray<AdvancedQueryCriterion> criteria;

	public AdvancedQueryModel()
	{
		// Create a default criterion.

		criteria = new NSMutableArray<AdvancedQueryCriterion>();
		insertNewCriterionAtIndex(0);
	}

	@Override
	public EOQualifier qualifierFromValues()
	{
    	NSMutableArray<EOQualifier> terms = new NSMutableArray<EOQualifier>();
    	
    	for(AdvancedQueryCriterion cri : criteria)
    	{
    		EOQualifier termQual =
    			AdvancedQueryComparison.qualifierForCriterion(cri);
    		
    		if(termQual != null)
    			terms.addObject(termQual);
    	}

    	return new EOAndQualifier(terms);
	}

	@Override
	public void takeValuesFromQualifier(EOQualifier qualifier)
	{
		criteria = new NSMutableArray<AdvancedQueryCriterion>();

		if(qualifier instanceof EOAndQualifier)
		{
			EOAndQualifier qAnd = (EOAndQualifier)qualifier;
			
			for(EOQualifier q : (NSArray<EOQualifier>)qAnd.qualifiers())
			{
				AdvancedQueryCriterion cri =
					AdvancedQueryComparison.criterionForQualifier(q);
				
				if(cri != null)
					criteria.addObject(cri);
			}
		}

		// Insert a blank criterion if the qualifier produces none, so that
		// editing can be performed correctly.
		if(criteria.count() == 0)
			insertNewCriterionAtIndex(0);
	}

	public NSArray<AdvancedQueryCriterion> criteria()
	{
		return criteria;
	}

	public void setCriteria(NSArray<AdvancedQueryCriterion> value)
	{
		criteria = value.mutableClone();
	}

    public void insertNewCriterionAtIndex(int index)
    {
    	AdvancedQueryCriterion newCriterion = new AdvancedQueryCriterion();
    	
    	newCriterion.setCastType(String.class);
    	newCriterion.setComparison(AdvancedQueryComparison.IS_EQUAL_TO);
    	newCriterion.setComparandType(AdvancedQueryCriterion.COMPARAND_LITERAL);

    	criteria.insertObjectAtIndex(newCriterion, index);
    }
    
    public void removeCriterionAtIndex(int index)
    {
    	criteria.removeObjectAtIndex(index);
    	
    	if(criteria.count() == 0)
    		insertNewCriterionAtIndex(0);
    }
}
