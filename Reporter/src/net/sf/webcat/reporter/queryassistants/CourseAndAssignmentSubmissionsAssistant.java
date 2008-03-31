/*==========================================================================*\
 |  $Id: CourseAndAssignmentSubmissionsAssistant.java,v 1.5 2008/03/31 03:42:45 stedwar2 Exp $
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
import net.sf.webcat.core.Course;
import net.sf.webcat.core.CourseOffering;
import net.sf.webcat.grader.Assignment;
import net.sf.webcat.grader.AssignmentOffering;
import net.sf.webcat.reporter.QualifierUtils;
import net.sf.webcat.reporter.ReportDataSet;
import net.sf.webcat.reporter.ReporterComponent;

//-------------------------------------------------------------------------
/**
 * A simplified query assistant that allows the user to select all the
 * submissions from one or more assignment offerings that are common across
 * a specified set of course offerings.
 *
 * @author aallowat
 * @version $Id: CourseAndAssignmentSubmissionsAssistant.java,v 1.5 2008/03/31 03:42:45 stedwar2 Exp $
 */
public class CourseAndAssignmentSubmissionsAssistant
    extends ReporterComponent
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new object.
     * @param context the page's context
     */
    public CourseAndAssignmentSubmissionsAssistant(WOContext context)
    {
        super(context);
    }


    //~ KVC Attributes (must be public) .......................................

	public ReportDataSet                       dataSet;
	public CourseAndAssignmentSubmissionsModel model;
	public CourseTreeNode                      currentNode;
	public Assignment                          assignment;
	public CourseTreeNode                      courseRoot;
	public CourseTreeDelegate                  courseDelegate;
	public int                                 index;


    //~ Public Nested Classes .................................................

    // ----------------------------------------------------------
    /**
     * Represents a single node in the AJAX-based tree view provided by
     * this component.
     */
    public class CourseTreeNode
	{
        //~ Constructor .......................................................

        // ----------------------------------------------------------
        public CourseTreeNode(CourseTreeNode parent, Object data)
        {
            id = getUniqueId();
            this.data = data;
            this.parent = parent;
        }


        //~ KVC Attributes (must be public) ...................................

        public String id;
		public Object data;
		public CourseTreeNode parent;
		public NSArray<CourseTreeNode> children;


        //~ Public Methods ....................................................

        // ----------------------------------------------------------
        public boolean isChecked()
		{
			if (children == null || children.isEmpty())
			{
				if (data instanceof CourseOffering)
                {
					return model.containsCourseOffering((CourseOffering)data);
                }
				else
                {
					return false;
                }
			}
			else
			{
				boolean allChecked = true;
				for (CourseTreeNode child : children)
				{
					if (!child.isChecked())
					{
						allChecked = false;
						break;
					}
				}
				return allChecked;
			}
		}


        // ----------------------------------------------------------
		/**
		 * This method is a no-op. The problem with this setter is that the
		 * checked state of EVERY node in the tree gets submitted as part of
		 * the AJAX update, so if the node that is toggled by the user is
		 * a parent of another node, then the child node would get set after
		 * the parent was, which could in turn modify the parent's logical
		 * state. Thus, the checking is handled by the toggle() action below,
		 * which will only be invoked on the node that was directly touched by
		 * the user.
         * @param value is ignored
		 */
		public void setChecked(boolean value)
		{
            // no-op
		}


        // ----------------------------------------------------------
		public WOActionResults toggle()
		{
			checkStateChanged = true;
			setCheckedRecursively(!isChecked());
			return null;
		}


        //~ Private Methods ...................................................

        // ----------------------------------------------------------
        private void setCheckedRecursively(boolean value)
        {
            if (children == null || children.isEmpty())
            {
                if (data instanceof CourseOffering)
                {
                    CourseOffering offering = (CourseOffering)data;

                    if (value)
                    {
                        model.addCourseOffering(offering);
                    }
                    else
                    {
                        model.removeCourseOffering(offering);
                    }
                }
            }
            else
            {
                for (CourseTreeNode child : children)
                {
                    child.setCheckedRecursively(value);
                }
            }
        }
	}


    // ----------------------------------------------------------
    /**
     * Handles AJAX manipulations of the tree view.
     */
	public class CourseTreeDelegate
        implements AjaxTreeModel.Delegate
	{
        //~ Public Methods ....................................................

        // ----------------------------------------------------------
		public NSArray<?> childrenTreeNodes(Object arg0)
		{
			CourseTreeNode node = (CourseTreeNode)arg0;
			return node.children;
		}


        // ----------------------------------------------------------
		public boolean isLeaf(Object arg0)
		{
			CourseTreeNode node = (CourseTreeNode)arg0;
			return (node.data instanceof CourseOffering);
		}


        // ----------------------------------------------------------
		public Object parentTreeNode(Object arg0)
		{
			CourseTreeNode node = (CourseTreeNode)arg0;

			if (node == null)
            {
				return null;
            }
			else
            {
				return node.parent;
            }
		}

	}


    //~ Public Methods ........................................................


    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
    	courseDelegate = new CourseTreeDelegate();
    	courseRoot = new CourseTreeNode(null, null);

    	EOFetchSpecification fetch =
            new EOFetchSpecification("Course", null, null);

    	NSArray<Course> courses = localContext().
    		objectsWithFetchSpecification(fetch);

    	NSMutableArray<EOSortOrdering> sortOrdering =
    		new NSMutableArray<EOSortOrdering>();
    	sortOrdering.addObject(new EOSortOrdering("deptNumberAndName",
    	    EOSortOrdering.CompareAscending));

    	courses = EOSortOrdering.sortedArrayUsingKeyOrderArray(courses,
    	    sortOrdering);

    	NSMutableArray<CourseTreeNode> courseNodes =
    		new NSMutableArray<CourseTreeNode>();

    	for (Course c : courses)
    	{
    		CourseTreeNode ctn = new CourseTreeNode(courseRoot, c);
    		courseNodes.addObject(ctn);

    		// Why did this disappear?!
//    		NSArray<CourseOffering> offerings = c.offerings();
    		EOFetchSpecification fs = new EOFetchSpecification("CourseOffering",
    		    EOQualifier.qualifierWithQualifierFormat("course = %@",
    		        new NSArray<Object>(new Object[] { c })), null);
    		NSArray<CourseOffering> offerings =
    			localContext().objectsWithFetchSpecification(fs);

    		NSMutableArray<CourseTreeNode> courseOfferingNodes =
    			new NSMutableArray<CourseTreeNode>();
    		for (CourseOffering co : offerings)
    		{
    			courseOfferingNodes.addObject(new CourseTreeNode(ctn, co));
    		}

    		ctn.children = courseOfferingNodes;
    	}

    	courseRoot.children = courseNodes;

    	super.appendToResponse(response, context);
    }


    // ----------------------------------------------------------
    public boolean isCurrentNodeCourse()
    {
    	return currentNode.data instanceof Course;
    }


    // ----------------------------------------------------------
    public boolean isCurrentNodeCourseOffering()
    {
    	return currentNode.data instanceof CourseOffering;
    }


    // ----------------------------------------------------------
    public NSArray<String> containersToUpdate()
    {
    	NSMutableArray<String> array = new NSMutableArray<String>();

    	if (checkStateChanged)
    	{
    		array.addObject("assignmentContainer");
    		checkStateChanged = false;
    	}

    	return array;
    }


    // ----------------------------------------------------------
    public NSArray<Assignment> assignments()
    {
    	EOFetchSpecification fetchSpec =
            new EOFetchSpecification("Assignment", null, null);
    	NSArray<Assignment> allAssignments =
            localContext().objectsWithFetchSpecification(fetchSpec);

    	NSMutableArray<Assignment> assignments =
    		new NSMutableArray<Assignment>();

    	for (Assignment assn : allAssignments)
    	{
    		boolean matches = true;

    		if (model.courseOfferings().isEmpty())
            {
    			matches = false;
            }

    		for (CourseOffering sco : model.courseOfferings())
    		{
    			boolean found = false;

       			for (AssignmentOffering ao :
       				(NSArray<AssignmentOffering>)assn.offerings())
       			{
       				CourseOffering co = ao.courseOffering();

       				if (co.equals(sco))
       				{
       					found = true;
       					break;
       				}
        		}

       			if (found == false)
       			{
       				matches = false;
       				break;
       			}
    		}

    		if (matches)
            {
    			assignments.addObject(assn);
            }
    	}

    	NSMutableArray<EOSortOrdering> sortOrderings =
    		new NSMutableArray<EOSortOrdering>();
    	sortOrderings.addObject(
            new EOSortOrdering("titleString", EOSortOrdering.CompareAscending));
    	EOSortOrdering.sortArrayUsingKeyOrderArray(assignments, sortOrderings);

    	return assignments;
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    private static String getUniqueId()
    {
        return "courseTreeNode_" + Integer.toString(nextIdNumber++);
    }


    //~ Instance/static variables .............................................

    private boolean checkStateChanged = false;
    private static int nextIdNumber = 0;
}
