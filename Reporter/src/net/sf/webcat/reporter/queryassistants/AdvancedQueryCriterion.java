/*==========================================================================*\
 |  $Id: AdvancedQueryCriterion.java,v 1.2 2008/03/31 03:07:21 stedwar2 Exp $
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

import com.webobjects.foundation.NSKeyValueCoding;

//-------------------------------------------------------------------------
/**
 * Represents the criterion used in an {@link AdvancedQueryComparison}, where
 * a KVC path can be compared against a literal value, or another KVC
 * value.
 *
 * @author aallowat
 * @version $Id: AdvancedQueryCriterion.java,v 1.2 2008/03/31 03:07:21 stedwar2 Exp $
 */
public class AdvancedQueryCriterion
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    public AdvancedQueryComparison comparison()
    {
        return comparison;
    }


    //~ Public Constants ......................................................

	public static final int COMPARAND_LITERAL = 0;
	public static final int COMPARAND_KEYPATH = 1;


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
	public String keyPath()
	{
		return keyPath;
	}


    // ----------------------------------------------------------
	public void setKeyPath(String kp)
	{
		keyPath = kp;
	}


    // ----------------------------------------------------------
	public Class<?> castType()
	{
		return castType;
	}


    // ----------------------------------------------------------
	public void setCastType(Class<?> ct)
	{
		castType = ct;
	}


    // ----------------------------------------------------------
	public void setComparison(AdvancedQueryComparison c)
	{
		comparison = c;
	}


    // ----------------------------------------------------------
	public int comparandType()
	{
		return comparandType;
	}


    // ----------------------------------------------------------
	public void setComparandType(int ct)
	{
		comparandType = ct;
	}


    // ----------------------------------------------------------
	public Object value()
	{
		if (value == NSKeyValueCoding.NullValue)
        {
			return null;
        }
		else
        {
			return value;
        }
	}


    // ----------------------------------------------------------
	public void setValue(Object v)
	{
		value = v;
	}


    // ----------------------------------------------------------
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("AdvancedQueryCriterion = { ");
		buffer.append("keyPath = " + keyPath);
		buffer.append(", comparison = " + comparison);
		buffer.append(", comparandType = ");

		if (comparandType == COMPARAND_LITERAL)
        {
			buffer.append("LITERAL");
        }
		else if(comparandType == COMPARAND_KEYPATH)
        {
			buffer.append("KEYPATH");
        }

		buffer.append(", value = " + value);
		buffer.append(" }");

		return buffer.toString();
	}


    //~ Instance/static variables .............................................

    /*
	 * The type of the right hand side of the comparison (keypath or literal).
	 * The term "comparand" is derived from the gerundive of the Latin
	 * "comparare" in the sense of "that which is to be compared"; this is
	 * analogous to such mathematical terms as "addend", "minuend", and
	 * "subtrahend".
	 */
	private int comparandType;

    private String keyPath;
    private Class<?> castType;
    private AdvancedQueryComparison comparison;
    private Object value;
}
