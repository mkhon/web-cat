/*==========================================================================*\
 |  ReportParameter.java
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

package net.sf.webcat.reporter;

import ognl.OgnlException;
import net.sf.webcat.core.MutableDictionary;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;

// -------------------------------------------------------------------------
/**
 * TODO: place a real description here.
 *
 * @author 
 * @version $Id: ReportParameter.java,v 1.1 2007/06/18 20:48:07 aallowat Exp $
 */
public class ReportParameter
    extends _ReportParameter
{
	/**
	 * These string constants represent the possible parameter types used by
	 * the Reporter subsystem.
	 */
	public static final String TYPE_INTEGER = "INTEGER";
	public static final String TYPE_BOOLEAN = "BOOLEAN";
	public static final String TYPE_FLOAT = "FLOAT";
	public static final String TYPE_STRING = "STRING";
	public static final String TYPE_TEXT = "TEXT";
	public static final String TYPE_DATETIME = "DATETIME";
	public static final String TYPE_CHOOSE = "CHOOSE";

	/*
	 * These strings correspond to keys in the parameter option dictionary.
	 * Convenience getter methods for each are given below.
	 */
	
	/**
	 * If the parameter type is TYPE_CHOOSE, then the value is the OGNL
	 * expression to be evaluated to obtain the list of objects to be displayed.
	 * For any other type, the value is the OGNL expression to be evaluated
	 * to obtain the parameter's default value. This expression should evaluate
	 * to an object of a type appropriate for the given parameter type:
	 * 
	 * Parameter type		Java type
	 * --------------		---------
	 * TYPE_INTEGER			Integer
	 * TYPE_BOOLEAN			Boolean
	 * TYPE_FLOAT			Double
	 * TYPE_STRING			String
	 * TYPE_TEXT			String
	 * TYPE_DATETIME		java.util.Date
	 * 
	 * This option and the "entity"/"qualifier" pair of options are mutually
	 * exclusive.
	 */
	public static final String OPTION_SOURCE = "source";
	
	/**
	 * Used only with TYPE_CHOOSE. Specifies the EO model entity whose objects
	 * should be returned as values for this parameter.
	 */
	public static final String OPTION_ENTITY = "entity";

	/**
	 * Used only with TYPE_CHOOSE. If the "entity" option is specified, this
	 * option is an OGNL expression that will be transformed into an
	 * EOQualifier to fast-filter the requested entities.
	 */
	public static final String OPTION_QUALIFIER = "qualifier";
	
	/**
	 * Used only with TYPE_CHOOSE. This option is an OGNL expression that will
	 * be executed to filter the elements returned by evaluating "source", or
	 * to post-filter (slow-filter) those returned by evaluating the
	 * "entity"/"qualifier" pair.
	 */
	public static final String OPTION_FILTER = "filter";

	/**
	 * Used only with TYPE_CHOOSE. The value of this option is Boolean.TRUE if
	 * multiple items can be selected from the list; Boolean.FALSE is only a
	 * single item can be selected.
	 */
	public static final String OPTION_MULTIPLE_SELECTION = "multipleSelection";
	
	/**
	 * Used only with TYPE_CHOOSE. The value of this option is an NSArray of
	 * EOSortOrdering objects that determine how the items in the list should
	 * be sorted.
	 */
	public static final String OPTION_SORT_ORDERINGS = "sortOrderings";

    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new ReportParameter object.
     */
    public ReportParameter()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Creates a new report parameter associated with the specified template
     * and with the other specified attributes.
     * 
     * @param ec the editing context in which to insert the object
     * @param template the report template to which the parameter belongs
     * @param binding the symbolic name associated with the parameter
     * @param type the Reporter subsystem type of the parameter
     * @param displayName a short human-readable name of the parameter
     * @param description a human-readable description of the parameter
     * @param options a dictionary of type-specific options for the parameter.
     *     Valid keys in this dictionary are the OPTION_* constants provided
     *     by this class.
     * 
     * @returns the ReportParameter object that was created
     */
    public static ReportParameter createNewReportParameter(
			EOEditingContext ec,
			ReportTemplate template,
			String binding,
			String type,
			String displayName,
			String description,
			NSDictionary options)
	{
		ReportParameter parameter = new ReportParameter();
		ec.insertObject(parameter);
		
		parameter.setReportTemplateRelationship(template);
		parameter.setBinding(binding);
		parameter.setType(type);
		parameter.setDisplayName(displayName);
		parameter.setDescription(description);
		parameter.setOptions(new MutableDictionary(options));
	
		return parameter;
	}
	
	
    // ----------------------------------------------------------
    /**
     * Returns true if this parameter depends on the specified parameter.
     * 
     * @param param the other parameter
     * 
     * @return true if this parameter depends on the other parameter.
     */
	public boolean isDependentOn(ReportParameter param)
	{
		return dependsOn().containsObject(param);
	}
	
	
    // ----------------------------------------------------------
	/**
	 * Returns true if the specified parameter depends on this parameter.
	 * 
	 * @param param the other parameter
	 * 
	 * @return true if the other parameter depends on this parameter
	 */
	public boolean isNeededBy(ReportParameter param)
	{
		return neededBy().containsObject(param);
	}


    // ----------------------------------------------------------
	/**
	 * Returns true if the parameter has a value for the specified option.
	 * 
	 * @param optionName the name of the option to check
	 * 
	 * @return true if the parameter has the option
	 */
	public boolean hasOption(String optionName)
	{
		return options().containsKey(optionName);
	}


    // ----------------------------------------------------------
	/**
	 * Returns the OGNL AST value of the OPTION_SOURCE option.
	 * 
	 * @return the OGNLAST (Object) value of OPTION_SOURCE
	 */
	public Object sourceOption()
	{
		return optionsToEvaluate().objectForKey(OPTION_SOURCE);
	}


    // ----------------------------------------------------------
	/**
	 * Returns the OGNL AST value of the OPTION_FILTER option.
	 * 
	 * @return the OGNLAST (Object) value of OPTION_FILTER
	 */
	public Object filterOption()
	{
		return optionsToEvaluate().objectForKey(OPTION_FILTER);
	}
	
	
	// ----------------------------------------------------------
	/**
	 * Returns the String value of the OPTION_ENTITY option.
	 * 
	 * @return the String value of OPTION_ENTITY
	 */
	public String entityOption()
	{
		return options().stringObjectForKey(OPTION_ENTITY);
	}


    // ----------------------------------------------------------
	/**
	 * Returns the boolean value of the OPTION_MULTIPLE_SELECTION option.
	 * 
	 * @return the boolean value of OPTION_MULTIPLE_SELECTION
	 */
	public boolean multipleSelectionOption()
	{
		return options().booleanObjectForKey(
				OPTION_MULTIPLE_SELECTION).booleanValue();
	}


    // ----------------------------------------------------------
	/**
	 * Returns the EOQualifier object that is the value of the
	 * OPTION_QUALIFIER option.
	 * 
	 * @return the EOQualifier object
	 */
	public EOQualifier qualifierOption()
	{
		return (EOQualifier)options().objectForKey(OPTION_QUALIFIER);
	}
//	
//
//    // ----------------------------------------------------------
//	/**
//	 * Returns the NSArray of EOSortOrderings that is the value of the
//	 * OPTION_SORT_ORDERINGS option.
//	 * 
//	 * @return the NSArray of EOSortOrderings
//	 */
//	public NSArray sortOrderingsOption()
//	{
//		return (NSArray)options().objectForKey(OPTION_SORT_ORDERINGS);
//	}

	// ----------------------------------------------------------
	/**
	 * Returns the parameter options in a format appropriate for being
	 * evaluated by the referencing environment. Specifically, the source
	 * and filter OGNL expressions are parsed here. (The qualifier
	 * expression is already an EOQualifier object; it is transformed at
	 * report upload time.)
	 * 
	 * The parsed expression trees are cached for the lifetime of this
	 * parameter object, since presumably the parameter will be re-evaluated
	 * many times in a report with large interdependencies among parameters.
	 */
	public NSMutableDictionary optionsToEvaluate()
	{
		if(cachedOptions == null)
		{
			cachedOptions = new NSMutableDictionary(options());
		
			String source = (String)cachedOptions.objectForKey(OPTION_SOURCE);
			if(source != null)
			{
				try
				{
					cachedOptions.setObjectForKey(
							ognl.Ognl.parseExpression(source), OPTION_SOURCE);
				}
				catch (OgnlException e)
				{
					// Throw out the exception; it should never be thrown
					// anyway because the expression is parsed also when the
					// template is uploaded.
				}
			}

			String filter = (String)cachedOptions.objectForKey(OPTION_FILTER);
			if(filter != null)
			{
				try
				{
					cachedOptions.setObjectForKey(
							ognl.Ognl.parseExpression(filter), OPTION_FILTER);
				}
				catch (OgnlException e)
				{
					// Throw out the exception; it should never be thrown
					// anyway because the expression is parsed also when the
					// template is uploaded.
				}
			}
		}
		
		return cachedOptions;
	}
	
// If you add instance variables to store property values you
// should add empty implementions of the Serialization methods
// to avoid unnecessary overhead (the properties will be
// serialized for you in the superclass).

//    // ----------------------------------------------------------
//    /**
//     * Serialize this object (an empty implementation, since the
//     * superclass handles this responsibility).
//     * @param out the stream to write to
//     */
//    private void writeObject( java.io.ObjectOutputStream out )
//        throws java.io.IOException
//    {
//    }
//
//
//    // ----------------------------------------------------------
//    /**
//     * Read in a serialized object (an empty implementation, since the
//     * superclass handles this responsibility).
//     * @param in the stream to read from
//     */
//    private void readObject( java.io.ObjectInputStream in )
//        throws java.io.IOException, java.lang.ClassNotFoundException
//    {
//    }
	
	private NSMutableDictionary cachedOptions = null;
}
