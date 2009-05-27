/*==========================================================================*\
 |  $Id: CourseAndAssignmentResultOutcomesModel.java,v 1.1 2009/05/27 14:31:52 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2008 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU Affero General Public License as published
 |  by the Free Software Foundation; either version 3 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU Affero General Public License
 |  along with Web-CAT; if not, see <http://www.gnu.org/licenses/>.
\*==========================================================================*/

package net.sf.webcat.reporter.queryassistants;

import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOKeyComparisonQualifier;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EONotQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import er.extensions.eof.ERXQ;
import net.sf.webcat.core.CourseOffering;
import net.sf.webcat.core.QualifierInSubquery;
import net.sf.webcat.grader.Assignment;
import net.sf.webcat.reporter.QualifierUtils;

//-------------------------------------------------------------------------
/**
 * The model of the query built by a
 * {@link CourseAndAssignmentResultOutcomesAssistant}.
 *
 * @author aallowat
 * @version $Id: CourseAndAssignmentResultOutcomesModel.java,v 1.1 2009/05/27 14:31:52 aallowat Exp $
 */
public class CourseAndAssignmentResultOutcomesModel
    extends AbstractQueryAssistantModel
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    public CourseAndAssignmentResultOutcomesModel()
    {
        courseOfferings = new NSMutableArray<CourseOffering>();
        tags = null;
        assignment = null;
        includeCourseStaff = false;
        includeOnlySubmissionsForGrading = true;
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
                    "submission.assignmentOffering.courseOffering",
                    courseOfferings));

            terms.addObject(ERXQ.equals(
                    "submission.assignmentOffering.assignment",
                    assignment));

            if (includeOnlySubmissionsForGrading)
            {
                terms.addObject(ERXQ.isTrue("submission.submissionForGrading"));
            }

            if (!includeCourseStaff)
            {
                EOQualifier q1 =
                    QualifierUtils.qualifierForKeyPathInRelationship(
                        "submission.user",
                        "submission.assignmentOffering.courseOffering.instructors");
                
                EOQualifier q2 =
                    QualifierUtils.qualifierForKeyPathInRelationship(
                        "submission.user",
                        "submission.assignmentOffering.courseOffering.graders");

                terms.addObject(ERXQ.not(q1));
                terms.addObject(ERXQ.not(q2));
            }

            if (!tags.isEmpty())
            {
                terms.addObject(ERXQ.in("tag", tags));
            }

            return new EOAndQualifier(terms);
        }
    }


    // ----------------------------------------------------------
    @Override
    public void takeValuesFromQualifier(EOQualifier qualifier)
    {
        courseOfferings = new NSMutableArray<CourseOffering>();
        assignment = null;
        includeCourseStaff = false;
        includeOnlySubmissionsForGrading = true;

        boolean foundSubsForGradingQualifier = false;
        boolean excludeGraders = false;
        boolean excludeInstructors = false;

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

                    if ("submission.assignmentOffering.courseOffering".equals(key))
                    {
                        courseOfferings =
                            new NSMutableArray<CourseOffering>(
                                (NSArray<CourseOffering>)values);
                    }
                    else if ("tags".equals(key))
                    {
                        tags = new NSMutableArray<String>(
                                (NSArray<String>)values);
                    }
                }
                else if (q instanceof EOKeyValueQualifier)
                {
                    EOKeyValueQualifier kvq = (EOKeyValueQualifier)q;
                    if ("submission.assignmentOffering.assignment".equals(kvq.key())
                        && EOQualifier.QualifierOperatorEqual.equals(
                               kvq.selector()))
                    {
                        assignment = (Assignment)kvq.value();
                    }
                    else if ("submission.submissionForGrading".equals(kvq.key())
                            && EOQualifier.QualifierOperatorEqual.equals(kvq.selector())
                            && Boolean.TRUE.equals(kvq.value()))
                    {
                        foundSubsForGradingQualifier = true;
                    }
                }
                else if (q instanceof EONotQualifier)
                {
                    EOQualifier nq = ((EONotQualifier) q).qualifier();

                    if (nq instanceof QualifierInSubquery)
                    {
                        QualifierInSubquery qis = (QualifierInSubquery) nq;
                        
                        if (qis.qualifier() instanceof EOKeyComparisonQualifier)
                        {
                            EOKeyComparisonQualifier kcq =
                                (EOKeyComparisonQualifier) qis.qualifier();
    
                            if ("submission.user.id".equals(kcq.leftKey())
                                    && EOQualifier.QualifierOperatorEqual.equals(kcq.selector()))
                            {
                                if ("submission.assignmentOffering.courseOffering.instructors.id".equals(kcq.rightKey()))
                                {
                                    excludeInstructors = true;
                                }
                                else if ("submission.assignmentOffering.courseOffering.graders.id".equals(kcq.rightKey()))
                                {
                                    excludeGraders = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!foundSubsForGradingQualifier)
        {
            includeOnlySubmissionsForGrading = false;
        }

        if (!excludeInstructors && !excludeGraders && qualifier != null)
        {
            includeCourseStaff = true;
        }
    }


    // ----------------------------------------------------------
    public String tags()
    {
        StringBuffer buffer = new StringBuffer();
        
        if (tags != null && tags.count() > 0)
        {
            buffer.append(tags.objectAtIndex(0));

            for (int i = 1; i < tags.count(); i++)
            {
                buffer.append(", ");
                buffer.append(tags.objectAtIndex(i));
            }
        }

        return buffer.toString();
    }


    // ----------------------------------------------------------
    public void setTags(String tagString)
    {
        tags = new NSMutableArray<String>();

        if (tagString != null)
        {
            String[] tagArray = tagString.split(",");
            
            for (String tag : tagArray)
            {
                tags.addObject(tag.trim());
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


    // ----------------------------------------------------------
    public boolean includeOnlySubmissionsForGrading()
    {
        return includeOnlySubmissionsForGrading;
    }


    // ----------------------------------------------------------
    public void setIncludeOnlySubmissionsForGrading(boolean v)
    {
        includeOnlySubmissionsForGrading = v;
    }


    // ----------------------------------------------------------
    public boolean includeCourseStaff()
    {
        return includeCourseStaff;
    }


    // ----------------------------------------------------------
    public void setIncludeCourseStaff(boolean v)
    {
        includeCourseStaff = v;
    }


    //~ Instance/static variables .............................................

    private NSMutableArray<CourseOffering> courseOfferings;
    private Assignment assignment;
    private NSMutableArray<String> tags;
    private boolean includeOnlySubmissionsForGrading;
    private boolean includeCourseStaff;
}
