package net.sf.webcat.reporter.queryassistants;

import net.sf.webcat.reporter.QualifierUtils;

import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOKeyComparisonQualifier;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EONotQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSSelector;

import er.extensions.ERXBetweenQualifier;

public abstract class AdvancedQueryComparison
{
	public static final AdvancedQueryComparison IS_EQUAL_TO;
	public static final AdvancedQueryComparison IS_NOT_EQUAL_TO;
	public static final AdvancedQueryComparison IS_LESS_THAN;
	public static final AdvancedQueryComparison IS_LESS_THAN_OR_EQUAL_TO;
	public static final AdvancedQueryComparison IS_GREATER_THAN;
	public static final AdvancedQueryComparison IS_GREATER_THAN_OR_EQUAL_TO;
	public static final AdvancedQueryComparison IS_BETWEEN;
	public static final AdvancedQueryComparison IS_NOT_BETWEEN;
	public static final AdvancedQueryComparison IS_LIKE;
	public static final AdvancedQueryComparison IS_NOT_LIKE;
	public static final AdvancedQueryComparison IS_ONE_OF;
	public static final AdvancedQueryComparison IS_NOT_ONE_OF;

	private static final int SIMPLE_EQ = 0;
	private static final int SIMPLE_NE = 1;
	private static final int SIMPLE_LT = 2;
	private static final int SIMPLE_LE = 3;
	private static final int SIMPLE_GT = 4;
	private static final int SIMPLE_GE = 5;

	private static NSMutableDictionary<String, AdvancedQueryComparison>
		nameToComparisonMap;

	private static NSMutableArray<AdvancedQueryComparison> booleanComparisons;
	private static NSMutableArray<AdvancedQueryComparison> numericComparisons;
	private static NSMutableArray<AdvancedQueryComparison> stringComparisons;
	private static NSMutableArray<AdvancedQueryComparison> timestampComparisons;
	private static NSMutableArray<AdvancedQueryComparison> objectComparisons;

	private static NSMutableDictionary<AdvancedQueryComparison, Boolean>
		keyPathSupport;

	protected AdvancedQueryComparison()
	{
        // Nothing to do
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof AdvancedQueryComparison)
		{
			return toString().equals(((AdvancedQueryComparison)obj).toString());
		}
		else
		{
			return false;
		}
	}

	public int hashCode()
	{
		return toString().hashCode();
	}

	static
	{
		IS_EQUAL_TO = new SimpleComparison(SIMPLE_EQ);
		IS_NOT_EQUAL_TO = new SimpleComparison(SIMPLE_NE);
		IS_LESS_THAN = new SimpleComparison(SIMPLE_LT);
		IS_LESS_THAN_OR_EQUAL_TO = new SimpleComparison(SIMPLE_LE);
		IS_GREATER_THAN = new SimpleComparison(SIMPLE_GT);
		IS_GREATER_THAN_OR_EQUAL_TO = new SimpleComparison(SIMPLE_GE);
		IS_BETWEEN = new BetweenComparison(false);
		IS_NOT_BETWEEN = new BetweenComparison(true);
		IS_LIKE = new LikeComparison(false);
		IS_NOT_LIKE = new LikeComparison(true);
		IS_ONE_OF = new InComparison(false);
		IS_NOT_ONE_OF = new InComparison(true);

		booleanComparisons = new NSMutableArray<AdvancedQueryComparison>();
		booleanComparisons.add(IS_EQUAL_TO);
		booleanComparisons.add(IS_NOT_EQUAL_TO);

		numericComparisons = new NSMutableArray<AdvancedQueryComparison>();
		numericComparisons.add(IS_EQUAL_TO);
		numericComparisons.add(IS_NOT_EQUAL_TO);
		numericComparisons.add(IS_LESS_THAN);
		numericComparisons.add(IS_LESS_THAN_OR_EQUAL_TO);
		numericComparisons.add(IS_GREATER_THAN);
		numericComparisons.add(IS_GREATER_THAN_OR_EQUAL_TO);
		numericComparisons.add(IS_BETWEEN);
		numericComparisons.add(IS_NOT_BETWEEN);
		numericComparisons.add(IS_ONE_OF);
		numericComparisons.add(IS_NOT_ONE_OF);

		stringComparisons = new NSMutableArray<AdvancedQueryComparison>();
		stringComparisons.add(IS_EQUAL_TO);
		stringComparisons.add(IS_NOT_EQUAL_TO);
		stringComparisons.add(IS_BETWEEN);
		stringComparisons.add(IS_NOT_BETWEEN);
		stringComparisons.add(IS_LIKE);
		stringComparisons.add(IS_NOT_LIKE);
		stringComparisons.add(IS_ONE_OF);
		stringComparisons.add(IS_NOT_ONE_OF);

		timestampComparisons = new NSMutableArray<AdvancedQueryComparison>();
		timestampComparisons.add(IS_EQUAL_TO);
		timestampComparisons.add(IS_NOT_EQUAL_TO);
		timestampComparisons.add(IS_LESS_THAN);
		timestampComparisons.add(IS_LESS_THAN_OR_EQUAL_TO);
		timestampComparisons.add(IS_GREATER_THAN);
		timestampComparisons.add(IS_GREATER_THAN_OR_EQUAL_TO);
		timestampComparisons.add(IS_BETWEEN);
		timestampComparisons.add(IS_NOT_BETWEEN);

		objectComparisons = new NSMutableArray<AdvancedQueryComparison>();
		objectComparisons.add(IS_EQUAL_TO);
		objectComparisons.add(IS_NOT_EQUAL_TO);
		objectComparisons.add(IS_ONE_OF);
		objectComparisons.add(IS_NOT_ONE_OF);

		keyPathSupport = new NSMutableDictionary<AdvancedQueryComparison, Boolean>();
		keyPathSupport.setObjectForKey(true, IS_EQUAL_TO);
		keyPathSupport.setObjectForKey(true, IS_NOT_EQUAL_TO);
		keyPathSupport.setObjectForKey(true, IS_LESS_THAN);
		keyPathSupport.setObjectForKey(true, IS_LESS_THAN_OR_EQUAL_TO);
		keyPathSupport.setObjectForKey(true, IS_GREATER_THAN);
		keyPathSupport.setObjectForKey(true, IS_GREATER_THAN_OR_EQUAL_TO);
		keyPathSupport.setObjectForKey(false, IS_BETWEEN);
		keyPathSupport.setObjectForKey(false, IS_NOT_BETWEEN);
		keyPathSupport.setObjectForKey(true, IS_LIKE);
		keyPathSupport.setObjectForKey(true, IS_NOT_LIKE);
		keyPathSupport.setObjectForKey(false, IS_ONE_OF);
		keyPathSupport.setObjectForKey(false, IS_NOT_ONE_OF);

		nameToComparisonMap = new NSMutableDictionary<String, AdvancedQueryComparison>();
		nameToComparisonMap.setObjectForKey(IS_EQUAL_TO, "IS_EQUAL_TO");
		nameToComparisonMap.setObjectForKey(IS_NOT_EQUAL_TO, "IS_NOT_EQUAL_TO");
		nameToComparisonMap.setObjectForKey(IS_LESS_THAN, "IS_LESS_THAN");
		nameToComparisonMap.setObjectForKey(IS_LESS_THAN_OR_EQUAL_TO, "IS_LESS_THAN_OR_EQUAL_TO");
		nameToComparisonMap.setObjectForKey(IS_GREATER_THAN, "IS_GREATER_THAN");
		nameToComparisonMap.setObjectForKey(IS_GREATER_THAN_OR_EQUAL_TO, "IS_GREATER_THAN_OR_EQUAL_TO");
		nameToComparisonMap.setObjectForKey(IS_BETWEEN, "IS_BETWEEN");
		nameToComparisonMap.setObjectForKey(IS_NOT_BETWEEN, "IS_NOT_BETWEEN");
		nameToComparisonMap.setObjectForKey(IS_LIKE, "IS_LIKE");
		nameToComparisonMap.setObjectForKey(IS_NOT_LIKE, "IS_NOT_LIKE");
		nameToComparisonMap.setObjectForKey(IS_ONE_OF, "IS_ONE_OF");
		nameToComparisonMap.setObjectForKey(IS_NOT_ONE_OF, "IS_NOT_ONE_OF");
	}

	public static AdvancedQueryComparison comparisonWithName(String name)
	{
		return nameToComparisonMap.objectForKey(name);
	}

	public static NSArray<AdvancedQueryComparison> comparisonsForType(
			Class<?> klass)
	{
		if(Boolean.class.isAssignableFrom(klass) ||
				klass == Boolean.TYPE)
		{
			return booleanComparisons;
		}
		else if(Number.class.isAssignableFrom(klass) ||
				klass == Integer.TYPE || klass == Double.TYPE ||
				klass == Float.TYPE)
		{
			return numericComparisons;
		}
		else if(String.class.isAssignableFrom(klass))
		{
			return stringComparisons;
		}
		else if(java.util.Date.class.isAssignableFrom(klass))
		{
			return timestampComparisons;
		}
		else if(EOEnterpriseObject.class.isAssignableFrom(klass))
		{
			return objectComparisons;
		}
		else
		{
			return null;
		}
	}

    private static AdvancedQueryComparison comparisonFromSelector(
    		NSSelector selector)
    {
    	if(selector.equals(EOQualifier.QualifierOperatorEqual))
    		return IS_EQUAL_TO;
    	else if(selector.equals(EOQualifier.QualifierOperatorNotEqual))
    		return IS_NOT_EQUAL_TO;
    	else if(selector.equals(EOQualifier.QualifierOperatorLessThan))
    		return IS_LESS_THAN;
    	else if(selector.equals(EOQualifier.QualifierOperatorLessThanOrEqualTo))
    		return IS_LESS_THAN_OR_EQUAL_TO;
    	else if(selector.equals(EOQualifier.QualifierOperatorGreaterThan))
    		return IS_GREATER_THAN;
    	else if(selector.equals(EOQualifier.QualifierOperatorGreaterThanOrEqualTo))
    		return IS_GREATER_THAN_OR_EQUAL_TO;
    	else if(selector.equals(EOQualifier.QualifierOperatorCaseInsensitiveLike))
    		return IS_LIKE;
    	else
    		return null;
    }

    /**
	 * Returns a criterion object that represents the given qualifier. This
	 * allows us to access the properties of any qualifier via a common
	 * interface, instead of using large blocks of conditional code based on
	 * the qualifier type.
	 *
	 * @param q the qualifier to convert to a criterion object
	 * @return an AdvancedQueryCriterion containing the qualifier properties
	 */
	public static AdvancedQueryCriterion criterionForQualifier(EOQualifier q)
	{
    	NSDictionary<String, Object> info = QualifierUtils.infoIfInQualifier(q);

    	AdvancedQueryComparison comparison = null;

    	if(info != null)
    	{
    		comparison = IS_ONE_OF;
    	}
    	else if(q instanceof ERXBetweenQualifier)
    	{
    		comparison = IS_BETWEEN;
    	}
    	else if(q instanceof EOKeyValueQualifier)
    	{
    		EOKeyValueQualifier kvq = (EOKeyValueQualifier)q;
    		comparison = comparisonFromSelector(kvq.selector());
    	}
    	else if(q instanceof EOKeyComparisonQualifier)
    	{
    		EOKeyComparisonQualifier kcq = (EOKeyComparisonQualifier)q;
    		comparison = comparisonFromSelector(kcq.selector());
    	}
    	else if(q instanceof EONotQualifier)
    	{
    		EOQualifier nq = ((EONotQualifier)q).qualifier();

        	info = QualifierUtils.infoIfInQualifier(nq);

        	if(info != null)
        	{
        		comparison = IS_NOT_ONE_OF;
        	}
        	else if(nq instanceof ERXBetweenQualifier)
    		{
        		comparison = IS_NOT_BETWEEN;
    		}
        	else if(nq instanceof EOKeyValueQualifier)
    		{
    			EOKeyValueQualifier kvq = (EOKeyValueQualifier)nq;

    			NSSelector selector = kvq.selector();
    			if(selector.equals(
    					EOQualifier.QualifierOperatorCaseInsensitiveLike))
    			{
    				comparison = IS_NOT_LIKE;
    			}
    		}
    		else if(nq instanceof EOKeyComparisonQualifier)
    		{
    			EOKeyComparisonQualifier kcq = (EOKeyComparisonQualifier)nq;

    			NSSelector selector = kcq.selector();
    			if(selector.equals(
    					EOQualifier.QualifierOperatorCaseInsensitiveLike))
    			{
    				comparison = IS_NOT_LIKE;
    			}
    		}
    	}

    	if(comparison != null)
    	{
        	AdvancedQueryCriterion criterion =
        		comparison._criterionForQualifier(q);

        	criterion.setComparison(comparison);

        	return criterion;
    	}
    	else
    	{
    		return null;
    	}
	}

	/**
	 * Returns a qualifier with the properties of the given criterion object.
	 *
	 * @param criterion an AdvancedQueryCriterion containing the qualifier
	 *     properties
	 * @return the qualifier that results from converting the criterion
	 */
	public static EOQualifier qualifierForCriterion(
			AdvancedQueryCriterion criterion)
	{
		if(criterion.keyPath() == null ||
				criterion.comparison() == null)
			return null;

		AdvancedQueryComparison comparison = criterion.comparison();

		return comparison._qualifierForCriterion(criterion);
	}

	/**
	 * Gets a value indicating whether this comparison supports a right-hand
	 * side that is another keypath, rather than a just a literal value.
	 *
	 * @return true if the comparison supports keypaths and literals on the
	 *     right-hand side; false if it only supports literals.
	 */
	public abstract boolean doesSupportKeyPaths();

	/**
	 * Gets a value indicating whether this comparison supports multiple right-
	 * hand sides that are ORed together (either with actual ORs or in an IN
	 * clause).
	 *
	 * @return true if the comparison supports multiple values on the right-
	 *     hand side; false if it only supports singular values.
	 */
	public abstract boolean doesSupportMultipleValues();

	/**
	 * Gets a value indicating whether this comparison has an operand on the
	 * right-hand side.
	 *
	 * @return true if the comparison requires a second operand; otherwise,
	 *     false.
	 */
	public abstract boolean hasSecondOperand();


	protected abstract AdvancedQueryCriterion _criterionForQualifier(
			EOQualifier q);

	protected abstract EOQualifier _qualifierForCriterion(
			AdvancedQueryCriterion criterion);


	// ========================================================================
	/**
	 * Implements comparison logic for the basic relational operators.
	 */
	private static class SimpleComparison extends AdvancedQueryComparison
	{
		public SimpleComparison(int type)
		{
			this.type = type;
		}

		public String toString()
		{
			return descriptions[type];
		}

		private static final String[] descriptions = {
/*			"is equal to", "is not equal to", "is less than",
			"is less than or equal to", "is greater than",
			"is greater than or equal to"*/
			"==", "!=", "<", "<=", ">", ">="
		};

		private static final NSSelector[] selectors = {
			EOQualifier.QualifierOperatorEqual,
			EOQualifier.QualifierOperatorNotEqual,
			EOQualifier.QualifierOperatorLessThan,
			EOQualifier.QualifierOperatorLessThanOrEqualTo,
			EOQualifier.QualifierOperatorGreaterThan,
			EOQualifier.QualifierOperatorGreaterThanOrEqualTo
		};

		@Override
		protected AdvancedQueryCriterion _criterionForQualifier(EOQualifier q)
		{
			AdvancedQueryCriterion criterion = new AdvancedQueryCriterion();

	    	if(q instanceof EOKeyValueQualifier)
	    	{
	    		EOKeyValueQualifier kvq = (EOKeyValueQualifier)q;

	    		criterion.setKeyPath(kvq.key());
	    		criterion.setComparandType(
	    				AdvancedQueryCriterion.COMPARAND_LITERAL);
	    		criterion.setValue(kvq.value());
	    	}
	    	else if(q instanceof EOKeyComparisonQualifier)
	    	{
	    		EOKeyComparisonQualifier kcq = (EOKeyComparisonQualifier)q;

	    		criterion.setKeyPath(kcq.leftKey());
	    		criterion.setComparandType(
	    				AdvancedQueryCriterion.COMPARAND_KEYPATH);
	    		criterion.setValue(kcq.rightKey());
	    	}

	    	return criterion;
		}

		@Override
		protected EOQualifier _qualifierForCriterion(AdvancedQueryCriterion cri)
		{
			EOQualifier q = null;

			NSSelector selector = selectors[type];

			String keypath = cri.keyPath();
			int comparand = cri.comparandType();
			Object value = cri.value();

			if(comparand == AdvancedQueryCriterion.COMPARAND_LITERAL)
			{
				q = new EOKeyValueQualifier(keypath, selector, value);
			}
			else
			{
				q = new EOKeyComparisonQualifier(keypath, selector,
						(String)value);
			}

			return q;
		}

		@Override
		public boolean doesSupportKeyPaths()
		{
			return true;
		}

		@Override
		public boolean doesSupportMultipleValues()
		{
			return false;
		}

		@Override
		public boolean hasSecondOperand()
		{
			return true;
		}

		private int type;
	}

	// ========================================================================
	/**
	 * Implements comparison logic for the basic relational operators.
	 */
	private static class BetweenComparison extends AdvancedQueryComparison
	{
		public BetweenComparison(boolean negate)
		{
			this.negate = negate;
		}

		public String toString()
		{
			if(negate)
				return "is not between";
			else
				return "is between";
		}

		@Override
		protected AdvancedQueryCriterion _criterionForQualifier(EOQualifier q)
		{
			AdvancedQueryCriterion criterion = new AdvancedQueryCriterion();

			ERXBetweenQualifier bq = null;

	    	if(q instanceof ERXBetweenQualifier)
	    	{
	    		bq = (ERXBetweenQualifier)q;
	    	}
	    	else if(q instanceof EONotQualifier)
	    	{
	    		EONotQualifier nq = (EONotQualifier)q;

	    		if(nq.qualifier() instanceof ERXBetweenQualifier)
	    		{
	    			bq = (ERXBetweenQualifier)nq.qualifier();
	    		}
                // TODO: what if the if fails?  How does bq get a value?
	    	}

	    	criterion.setKeyPath(bq.key());
	    	criterion.setComparandType(
                AdvancedQueryCriterion.COMPARAND_LITERAL);

    		if(bq.minimumValue() != null || bq.maximumValue() != null)
    		{
        		NSMutableDictionary<String, Object> values =
        			new NSMutableDictionary<String, Object>();
    			values.setObjectForKey(bq.minimumValue(), "minimumValue");
    			values.setObjectForKey(bq.maximumValue(), "maximumValue");
        		criterion.setValue(values);
    		}
    		else
    		{
    			criterion.setValue(null);
    		}

    		return criterion;
		}

		@Override
		protected EOQualifier _qualifierForCriterion(AdvancedQueryCriterion cri)
		{
			String keypath = cri.keyPath();

			NSDictionary<String, Object> values =
				(NSDictionary<String, Object>)cri.value();

			Object minimum = null, maximum = null;

			if(values != null)
			{
				minimum = values.objectForKey("minimumValue");
				maximum = values.objectForKey("maximumValue");
			}

			ERXBetweenQualifier bq = new ERXBetweenQualifier(keypath, minimum,
					maximum);

			if(negate)
				return new EONotQualifier(bq);
			else
				return bq;
		}

		@Override
		public boolean doesSupportKeyPaths()
		{
			return false;
		}

		@Override
		public boolean doesSupportMultipleValues()
		{
			return false;
		}

		@Override
		public boolean hasSecondOperand()
		{
			return true;
		}

		private boolean negate;
	}

	// ========================================================================
	/**
	 * Implements comparison logic for the basic relational operators.
	 */
	private static class LikeComparison extends AdvancedQueryComparison
	{
		public LikeComparison(boolean negate)
		{
			this.negate = negate;
		}

		public String toString()
		{
			if(negate)
				return "is not like";
			else
				return "is like";
		}

		@Override
		protected AdvancedQueryCriterion _criterionForQualifier(EOQualifier q)
		{
			AdvancedQueryCriterion criterion = new AdvancedQueryCriterion();

	    	if(q instanceof EOKeyValueQualifier)
	    	{
	    		EOKeyValueQualifier kvq = (EOKeyValueQualifier)q;

	    		criterion.setKeyPath(kvq.key());
	    		criterion.setComparandType(
	    				AdvancedQueryCriterion.COMPARAND_LITERAL);
	    		criterion.setValue(kvq.value());
	    	}
	    	else if(q instanceof EOKeyComparisonQualifier)
	    	{
	    		EOKeyComparisonQualifier kcq = (EOKeyComparisonQualifier)q;

	    		criterion.setKeyPath(kcq.leftKey());
	    		criterion.setComparandType(
	    				AdvancedQueryCriterion.COMPARAND_KEYPATH);
	    		criterion.setValue(kcq.rightKey());
	    	}

	    	return criterion;
		}

		@Override
		protected EOQualifier _qualifierForCriterion(AdvancedQueryCriterion cri)
		{
			EOQualifier q = null;

			String keypath = cri.keyPath();
			int comparand = cri.comparandType();
			Object value = cri.value();

			if(comparand == AdvancedQueryCriterion.COMPARAND_LITERAL)
			{
				q = new EOKeyValueQualifier(keypath,
						EOQualifier.QualifierOperatorCaseInsensitiveLike,
						value);
			}
			else
			{
				q = new EOKeyComparisonQualifier(keypath,
						EOQualifier.QualifierOperatorCaseInsensitiveLike,
						(String)value);
			}

			if(negate)
				return new EONotQualifier(q);
			else
				return q;
		}

		@Override
		public boolean doesSupportKeyPaths()
		{
			return true;
		}

		@Override
		public boolean doesSupportMultipleValues()
		{
			return false;
		}

		@Override
		public boolean hasSecondOperand()
		{
			return true;
		}

		private boolean negate;
	}

	// ========================================================================
	/**
	 * Implements comparison logic for the basic relational operators.
	 */
	private static class InComparison extends AdvancedQueryComparison
	{
		public InComparison(boolean negate)
		{
			this.negate = negate;
		}

		public String toString()
		{
			if(negate)
				return "is not one of";
			else
				return "is one of";
		}

		@Override
		protected AdvancedQueryCriterion _criterionForQualifier(EOQualifier q)
		{
			AdvancedQueryCriterion criterion = new AdvancedQueryCriterion();

			if(q instanceof EONotQualifier)
			{
				q = ((EONotQualifier)q).qualifier();
			}

			NSDictionary<String, Object> info =
				QualifierUtils.infoIfInQualifier(q);

        	if(info != null)
        	{
        		criterion.setKeyPath((String)info.objectForKey("key"));
        		criterion.setComparandType(
        				AdvancedQueryCriterion.COMPARAND_LITERAL);
        		criterion.setValue(info.objectForKey("values"));
        	}

	    	return criterion;
		}

		@Override
		protected EOQualifier _qualifierForCriterion(AdvancedQueryCriterion cri)
		{
			EOQualifier q = null;

			String keypath = cri.keyPath();
			NSArray<Object> values = (NSArray<Object>)cri.value();

			if(values != null)
			{
				q = QualifierUtils.qualifierForInCondition(keypath, values);

				if(negate)
					return new EONotQualifier(q);
				else
					return q;
			}
			else
			{
				return null;
			}
		}

		@Override
		public boolean doesSupportKeyPaths()
		{
			return false;
		}

		@Override
		public boolean doesSupportMultipleValues()
		{
			return true;
		}

		@Override
		public boolean hasSecondOperand()
		{
			return true;
		}

		private boolean negate;
	}
}
