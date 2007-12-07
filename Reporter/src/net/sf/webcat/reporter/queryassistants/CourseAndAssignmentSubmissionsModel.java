package net.sf.webcat.reporter.queryassistants;

import net.sf.webcat.core.CourseOffering;
import net.sf.webcat.grader.Assignment;
import net.sf.webcat.reporter.QualifierUtils;

import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;

public class CourseAndAssignmentSubmissionsModel extends
		AbstractQueryAssistantModel
{
	private NSMutableArray<CourseOffering> courseOfferings;
	
	private Assignment assignment;

	public CourseAndAssignmentSubmissionsModel()
	{
		courseOfferings = new NSMutableArray<CourseOffering>();
		assignment = null;
	}

	@Override
	public EOQualifier qualifierFromValues()
	{
    	if(courseOfferings.isEmpty() || assignment == null)
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
    				EOQualifier.QualifierOperatorEqual, assignment));
    		
    		return new EOAndQualifier(terms);
    	}
	}

	@Override
	public void takeValuesFromQualifier(EOQualifier qualifier)
	{
		courseOfferings = new NSMutableArray<CourseOffering>();
		assignment = null;

    	if(qualifier instanceof EOAndQualifier)
    	{
    		EOAndQualifier aq = (EOAndQualifier)qualifier;
    		
    		NSArray<EOQualifier> terms = aq.qualifiers();
    		
    		for(EOQualifier q : terms)
    		{
    			NSDictionary<String, Object> info =
    				QualifierUtils.infoIfInQualifier(q);
    			
    			if(info != null)
    			{
    				String key = (String)info.objectForKey("key");
    				NSArray<?> values = (NSArray<?>)info.objectForKey("values");
    				
    				if("assignmentOffering.courseOffering".equals(key))
    				{
    					courseOfferings =
    						new NSMutableArray<CourseOffering>(
    								(NSArray<CourseOffering>)values);
    				}
    			}
    			else if(q instanceof EOKeyValueQualifier)
    			{
    				EOKeyValueQualifier kvq = (EOKeyValueQualifier)q;
    				if("assignmentOffering.assignment".equals(kvq.key()) &&
    						EOQualifier.QualifierOperatorEqual.equals(
    								kvq.selector()))
    				{
    					assignment = (Assignment)kvq.value();
    				}
    			}
    		}
    	}
	}
	
	public NSArray<CourseOffering> courseOfferings()
	{
		return courseOfferings;
	}
	
	public void setCourseOfferings(NSArray<CourseOffering> co)
	{
		courseOfferings = new NSMutableArray<CourseOffering>(co);
	}

	public void addCourseOffering(CourseOffering co)
	{
		courseOfferings.addObject(co);
	}
	
	public void removeCourseOffering(CourseOffering co)
	{
		courseOfferings.removeObject(co);
	}

	public boolean containsCourseOffering(CourseOffering co)
	{
		return courseOfferings.contains(co);
	}

	public Assignment assignment()
	{
		return assignment;
	}
	
	public void setAssignment(Assignment a)
	{
		assignment = a;
	}
}
