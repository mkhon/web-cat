package net.sf.webcat.reporter.queryassistants;
// Generated by the WOLips Templateengine Plug-in at Sep 21, 2007 6:25:56 PM

import net.sf.webcat.core.Course;
import net.sf.webcat.core.CourseOffering;
import net.sf.webcat.grader.Assignment;
import net.sf.webcat.grader.AssignmentOffering;
import net.sf.webcat.reporter.QualifierUtils;
import net.sf.webcat.reporter.ReportDataSet;
import net.sf.webcat.reporter.ReporterComponent;

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.ajax.AjaxTreeModel;
import er.extensions.ERXEOControlUtilities;

public class CourseAndAssignmentSubmissionsAssistant extends ReporterComponent
{
	public ReportDataSet dataSet;
	
	public CourseAndAssignmentSubmissionsModel model;

	public CourseTreeNode currentNode;

	public Assignment assignment;
	
	public CourseTreeNode courseRoot;

	public CourseTreeDelegate courseDelegate;

	public int index;
	
	private boolean checkStateChanged = false;

	private static int nextIdNumber = 0;
	
	private static String getUniqueId()
	{
		return "courseTreeNode_" + Integer.toString(nextIdNumber++);
	}

	public class CourseTreeNode
	{
		public String id;

		public Object data;
		
		public CourseTreeNode parent;
		
		public NSArray<CourseTreeNode> children;

		public CourseTreeNode(CourseTreeNode parent, Object data)
		{
			id = getUniqueId();
			this.data = data;
			this.parent = parent;
		}
		
		public boolean isChecked()
		{
			if(children == null || children.isEmpty())
			{
				if(data instanceof CourseOffering)
					return model.containsCourseOffering((CourseOffering)data);
				else
					return false;
			}
			else
			{
				boolean allChecked = true;
				for(CourseTreeNode child : children)
				{
					if(!child.isChecked())
					{
						allChecked = false;
						break;
					}
				}
				
				return allChecked;
			}
		}

		/**
		 * This method is a no-op. The problem with this setter is that the
		 * checked state of EVERY node in the tree gets submitted as part of
		 * the AJAX update, so if the node that is toggled by the user is
		 * a parent of another node, then the child node would get set after
		 * the parent was, which could in turn modify the parent's logical
		 * state. Thus, the checking is handled by the toggle() action below,
		 * which will only be invoked on the node that was directly touched by
		 * the user.
		 */
		public void setChecked(boolean value)
		{
		}
		
		private void setCheckedRecursively(boolean value)
		{
			if(children == null || children.isEmpty())
			{
				if(data instanceof CourseOffering)
				{
					CourseOffering offering = (CourseOffering)data;

					if(value)
						model.addCourseOffering(offering);
					else
						model.removeCourseOffering(offering);
				}
			}
			else
			{
				for(CourseTreeNode child : children)
				{
					child.setCheckedRecursively(value);
				}
			}
		}

		public WOActionResults toggle()
		{
			checkStateChanged = true;
			setCheckedRecursively(!isChecked());
			return null;
		}
	}

	public class CourseTreeDelegate implements AjaxTreeModel.Delegate
	{
		public NSArray<?> childrenTreeNodes(Object arg0)
		{
			CourseTreeNode node = (CourseTreeNode)arg0;
			return node.children;
		}

		public boolean isLeaf(Object arg0)
		{
			CourseTreeNode node = (CourseTreeNode)arg0;
			return (node.data instanceof CourseOffering);
		}

		public Object parentTreeNode(Object arg0)
		{
			CourseTreeNode node = (CourseTreeNode)arg0;

			if(node == null)
				return null;
			else
				return node.parent;
		}
		
	}

    public CourseAndAssignmentSubmissionsAssistant(WOContext context)
    {
        super(context);
    }

    public void appendToResponse(WOResponse response, WOContext context)
    {
    	courseDelegate = new CourseTreeDelegate();
    	courseRoot = new CourseTreeNode(null, null);

    	EOFetchSpecification fetch = new EOFetchSpecification("Course",
    			null, null);
    	
    	NSArray<Course> courses = wcSession().localContext().
    		objectsWithFetchSpecification(fetch);

    	NSMutableArray<EOSortOrdering> sortOrdering =
    		new NSMutableArray<EOSortOrdering>();
    	sortOrdering.addObject(new EOSortOrdering("deptNumberAndName",
    			EOSortOrdering.CompareAscending));

    	courses = EOSortOrdering.sortedArrayUsingKeyOrderArray(courses,
    			sortOrdering);

    	NSMutableArray<CourseTreeNode> courseNodes =
    		new NSMutableArray<CourseTreeNode>();

    	for(Course c : courses)
    	{
    		CourseTreeNode ctn = new CourseTreeNode(courseRoot, c);
    		courseNodes.addObject(ctn);
    		
    		NSArray<CourseOffering> offerings = c.offerings();
    		
    		NSMutableArray<CourseTreeNode> courseOfferingNodes =
    			new NSMutableArray<CourseTreeNode>();
    		for(CourseOffering co : offerings)
    		{
    			courseOfferingNodes.addObject(new CourseTreeNode(ctn, co));
    		}
    		
    		ctn.children = courseOfferingNodes;
    	}

    	courseRoot.children = courseNodes;

    	super.appendToResponse(response, context);
    }

    public boolean isCurrentNodeCourse()
    {
    	return currentNode.data instanceof Course;
    }

    public boolean isCurrentNodeCourseOffering()
    {
    	return currentNode.data instanceof CourseOffering;
    }

    public NSArray<String> containersToUpdate()
    {
    	NSMutableArray<String> array = new NSMutableArray<String>();
    	
    	if(checkStateChanged)
    	{
    		array.addObject("assignmentContainer");
    		checkStateChanged = false;
    	}
    	
    	return array;
    }

    public NSArray<Assignment> assignments()
    {
    	EOFetchSpecification fetchSpec = new EOFetchSpecification("Assignment",
    			null, null);
    	NSArray<Assignment> allAssignments = wcSession().localContext()
    		.objectsWithFetchSpecification(fetchSpec);
    	
    	NSMutableArray<Assignment> assignments =
    		new NSMutableArray<Assignment>();

    	for(Assignment assn : allAssignments)
    	{
    		boolean matches = true;

    		if(model.courseOfferings().isEmpty())
    			matches = false;

    		for(CourseOffering sco : model.courseOfferings())
    		{
    			boolean found = false;
    			
       			for(AssignmentOffering ao :
       				(NSArray<AssignmentOffering>)assn.offerings())
       			{
       				CourseOffering co = ao.courseOffering();

       				if(co.equals(sco))
       				{
       					found = true;
       					break;
       				}
        		}
       			
       			if(found == false)
       			{
       				matches = false;
       				break;
       			}
    		}

    		if(matches)
    			assignments.addObject(assn);
    	}

    	NSMutableArray<EOSortOrdering> sortOrderings =
    		new NSMutableArray<EOSortOrdering>();
    	sortOrderings.addObject(new EOSortOrdering("titleString",
    			EOSortOrdering.CompareAscending));
    	EOSortOrdering.sortArrayUsingKeyOrderArray(assignments, sortOrderings);

    	return assignments;
    }
}
