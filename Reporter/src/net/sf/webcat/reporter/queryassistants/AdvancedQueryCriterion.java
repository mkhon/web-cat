package net.sf.webcat.reporter.queryassistants;

import com.webobjects.foundation.NSKeyValueCoding;

public class AdvancedQueryCriterion
{
	public static final int COMPARAND_LITERAL = 0;
	
	public static final int COMPARAND_KEYPATH = 1;

	public String keyPath()
	{
		return keyPath;
	}
	
	public void setKeyPath(String kp)
	{
		keyPath = kp;
	}
	
	public Class<?> castType()
	{
		return castType;
	}
	
	public void setCastType(Class<?> ct)
	{
		castType = ct;
	}
	
	public AdvancedQueryComparison comparison()
	{
		return comparison;
	}
	
	public void setComparison(AdvancedQueryComparison c)
	{
		comparison = c;
	}
	
	public int comparandType()
	{
		return comparandType;
	}
	
	public void setComparandType(int ct)
	{
		comparandType = ct;	
	}
	
	public Object value()
	{
		if(value == NSKeyValueCoding.NullValue)
			return null;
		else
			return value;
	}
	
	public void setValue(Object v)
	{
		value = v;
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("AdvancedQueryCriterion = { ");
		buffer.append("keyPath = " + keyPath);
		buffer.append(", comparison = " + comparison);
		buffer.append(", comparandType = ");
		
		if(comparandType == COMPARAND_LITERAL)
			buffer.append("LITERAL");
		else if(comparandType == COMPARAND_KEYPATH)
			buffer.append("KEYPATH");
		
		buffer.append(", value = " + value);
		buffer.append(" }");

		return buffer.toString();
	}

	private String keyPath;
	
	private Class<?> castType;

	private AdvancedQueryComparison comparison;

	/*
	 * The type of the right hand side of the comparison (keypath or literal).
	 * The term "comparand" is derived from the gerundive of the Latin
	 * "comparare" in the sense of "that which is to be compared"; this is
	 * analogous to such mathematical terms as "addend", "minuend", and
	 * "subtrahend". 
	 */
	private int comparandType;
	
	private Object value;
}
