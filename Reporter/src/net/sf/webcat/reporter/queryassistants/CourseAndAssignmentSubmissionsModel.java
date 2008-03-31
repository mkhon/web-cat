/*==========================================================================*\
 |  $Id: CourseAndAssignmentSubmissionsModel.java,v 1.2 2008/03/31 03:39:24 stedwar2 Exp $
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
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import net.sf.webcat.core.CourseOffering;
import net.sf.webcat.grader.Assignment;
import net.sf.webcat.reporter.QualifierUtils;

//-------------------------------------------------------------------------
/**
 * The model of the query built by a
 * {@link CourseAndAssignmentSubmissionsAssistant}.
 *
 * @author aallowat
 * @version $Id: CourseAndAssignmentSubmissionsModel.java,v 1.2 2008/03/31 03:39:24 stedwar2 Exp $
 */
public class CourseAndAssignmentSubmissionsModel
    extends AbstractQueryAssistantModel
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
	public CourseAndAssignmentSubmissionsModel()
	{
		courseOfferings = new NSMutableArray<CourseOffering>();
		assignment = null;
	}


    // ----------------------------------------------------------
	@Override
	public EOQualifier qualifierFromValues()
	{
    	if (courseOfferings.isEmpty() || assignment == null)
    	{
    		return null;
    	}
    	else
    	{
    		NSMutableArray<EOQualifier> terms =
    			new NSMutableArray<EOQualifier>();

    		terms.addObject(QualifierUtils.qualifierForInCondition(
    			"assignmentOffering.courseOffering", courseOfferings));

    		terms.addObject(new EOKeyValueQualifier(
    			"assignmentOffering.assignment",
    			EOQualifier.QualifierOperatorEqual,
                assignment));

    		return new EOAndQualifier(terms);
    	}
	}


    // ----------------------------------------------------------
	@Override
	public void takeValuesFromQualifier(EOQualifier qualifier)
	{
		courseOfferings = new NSMutableArray<CourseOffering>();
		assignment = null;

    	if (qualifier instanceof EOAndQualifier)
    	{
    		EOAndQualifier aq = (EOAndQualifier)qualifier;
    		NSArray<EOQualifier> terms = aq.qualifiers();

    		for (EOQualifier q : terms)
    		{
    			NSDictionary<String, Object> info =
    				QualifierUtils.infoIfInQualifier(q);

    			if (info != null)
    			{
    				String key = (String)info.objectForKey("key");
    				NSArray<?> values = (NSArray<?>)info.objectForKey("values");

    				if ("assignmentOffering.courseOffering".equals(key))
    				{
    					courseOfferings =
    						new NSMutableArray<CourseOffering>(
    							(NSArray<CourseOffering>)values);
    				}
    			}
    			else if (q instanceof EOKeyValueQualifier)
    			{
    				EOKeyValueQualifier kvq = (EOKeyValueQualifier)q;
    				if ("assignmentOffering.assignment".equals(kvq.key())
                        && EOQualifier.QualifierOperatorEqual.equals(
    						   kvq.selector()))
    				{
    					assignment = (Assignment)kvq.value();
    				}
    			}
    		}
    	}
	}


    // ----------------------------------------------------------
	public NSArray<CourseOffering> courseOfferings()
	{
		return courseOfferings;
	}


    // ----------------------------------------------------------
	public void setCourseOfferings(NSArray<CourseOffering> co)
	{
		courseOfferings = new NSMutableArray<CourseOffering>(co);
	}


    // ----------------------------------------------------------
	public void addCourseOffering(CourseOffering co)
	{
		courseOfferings.addObject(co);
	}


    // ----------------------------------------------------------
	public void removeCourseOffering(CourseOffering co)
	{
		courseOfferings.removeObject(co);
	}


    // ----------------------------------------------------------
	public boolean containsCourseOffering(CourseOffering co)
	{
		return courseOfferings.contains(co);
	}


    // ----------------------------------------------------------
	public Assignment assignment()
	{
		return assignment;
	}


    // ----------------------------------------------------------
	public void setAssignment(Assignment a)
	{
		assignment = a;
	}


    //~ Instance/static variables .............................................

    private NSMutableArray<CourseOffering> courseOfferings;
    private Assignment assignment;
}
