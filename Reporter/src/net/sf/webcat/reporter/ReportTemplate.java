/*==========================================================================*\
 |  ReportTemplate.java
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.script.element.IReportDesign;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ParameterHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ReportElementHandle;
import org.eclipse.birt.report.model.api.ReportItemHandle;
import org.eclipse.birt.report.model.api.ScalarParameterHandle;
import org.eclipse.birt.report.model.api.SessionHandle;
import org.eclipse.birt.report.model.api.SlotHandle;

import net.sf.webcat.core.MutableDictionary;
import net.sf.webcat.core.User;
import net.sf.webcat.oda.RelationInformation;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;

// -------------------------------------------------------------------------
/**
 * TODO: place a real description here.
 *
 * @author 
 * @version $Id: ReportTemplate.java,v 1.4 2008/03/06 20:13:41 aallowat Exp $
 */
public class ReportTemplate
    extends _ReportTemplate
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new ReportTemplate object.
     */
    public ReportTemplate()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Retrieve the name of the directory where this script is stored.
     * @return the directory name
     */
    public String dirName()
    {
        StringBuffer dir = userTemplateDirName( author() );
        return dir.toString();
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the path name for this report template.
     * @return the path to the template
     */
    public String filePath()
    {
        return dirName() + "/" + uploadedFileName();
    }

    
    // ----------------------------------------------------------
    public String toString()
    {
        return filePath();
    }
    
    
    // ----------------------------------------------------------
    /**
     * Retrieve the name of the directory where a user's report templates are
     * stored.
     * @param author the user
     * @return the directory name
     */
    public static StringBuffer userTemplateDirName( User author )
    {
        StringBuffer dir = new StringBuffer( 50 );
        dir.append( templateRoot() );
        dir.append( '/' );
        dir.append( author.authenticationDomain().subdirName() );
        dir.append( '/' );
        dir.append( author.userName() );
        return dir;
    }


    // ----------------------------------------------------------
    /**
     * Retrieve the name of the directory where all user report templates are
     * stored.
     * @return the directory name
     */
    public static String templateRoot()
    {
        if ( templateRoot == null )
        {
        	templateRoot = net.sf.webcat.core.Application
        		.configurationProperties()
        		.getProperty("grader.reporttemplatesroot" );
        	
            if ( templateRoot == null )
            {
            	templateRoot = net.sf.webcat.core.Application
                    .configurationProperties()
                        .getProperty( "grader.submissiondir" )
                    + "/UserReportTemplates";
            }
        }
        return templateRoot;
    }


    public String displayableVersion()
    {
    	return displayableVersion(version());    	
    }

    
    public static String displayableVersion(int version)
    {
    	int major = (version >> 16) & 0xFF;
    	int minor = (version >> 8) & 0xFF;
    	int revision = version & 0xFF;
    	
    	return String.format("%d.%d.%d", major, minor, revision);
    }

    
    public int versionByIncrementingMajor()
    {
    	return versionByIncrementingMajor(version());
    }


    public static int versionByIncrementingMajor(int version)
    {
    	int[] components = componentsFromVersion(version);
    	return versionFromComponents(
    			components[0] + 1, 0, 0);
    }


    public int versionByIncrementingMinor()
    {
    	return versionByIncrementingMinor(version());
    }


    public static int versionByIncrementingMinor(int version)
    {
    	int[] components = componentsFromVersion(version);
    	return versionFromComponents(
    			components[0], components[1] + 1, 0);
    }

    
    public int versionByIncrementingRevision()
    {
    	return versionByIncrementingRevision(version());
    }


    public static int versionByIncrementingRevision(int version)
    {
    	int[] components = componentsFromVersion(version);
    	return versionFromComponents(
    			components[0], components[1], components[2] + 1);
    }


    public int versionMajor()
    {
    	return componentsFromVersion(version())[0];
    }

    
    public int versionMinor()
    {
    	return componentsFromVersion(version())[1];
    }

    
    public int versionRevision()
    {
    	return componentsFromVersion(version())[2];
    }

    
    public void setVersion(int major, int minor, int revision)
    {
    	setVersion(versionFromComponents(major, minor, revision));
    }


    public static int[] componentsFromVersion(int version)
    {
    	int[] components = new int[3];
    	components[0] = (version >> 16) & 0xFF;
    	components[1] = (version >> 8) & 0xFF;
    	components[2] = version & 0xFF;
    	return components;
    }


    public static int versionFromComponents(int major, int minor, int revision)
    {
    	if(revision > 255)
    	{
    		revision = 0;
    		minor++;
    	}
    	
    	if(minor > 255)
    	{
    		minor = 0;
    		major++;
    	}
    	
    	if(major > 255)
    	{
    		// This will hopefully never happen!
    		throw new IllegalArgumentException("No version numbers remaining!");
    	}

    	return (major << 16) | (minor << 8) | revision;
    }


    // ----------------------------------------------------------
    /**
     * Gets the parameter with the specified binding in this report template.
     * 
     * @param binding the binding of the parameter to find
     * 
     * @return the ReportParameter if it exists, or null if no parameter with
     *     the specified binding is in this report template
     */
/*    public ReportParameter parameterWithBinding(String binding)
    {
    	Enumeration e = parameters().objectEnumerator();
    	while(e.hasMoreElements())
    	{
    		ReportParameter param = (ReportParameter)e.nextElement();
    		
    		if(param.binding().equals(binding))
    			return param;
    	}
    	
    	return null;
    }*/
    

    // ----------------------------------------------------------
    /**
     * Gets the array of parameters defined in this report template, but sorted
     * such that if a parameter Y depends on parameter X, then X occurs in the
     * array before Y (that is, they are topologically sorted based on their
     * dependencies).
     * 
     * @return the array of parameters sorted in dependency order
     */
/*    public NSArray sortedParameters()
    {
    	ReverseTopologicalSort rts = new ReverseTopologicalSort(parameters());
    	return rts.sortedParameters();
    }*/
    
    // ----------------------------------------------------------
    /**
     * Create a new report template object from uploaded file data.
     * @param ec           the editing context in which to add the new object
     * @param author       the user uploading the template
     * @param uploadedName the template's file name
     * @param uploadedData the file's data
     * @param errors       a dictionary in which to store any error messages
     *                     for display to the user
     * @return the new report template, if successful, or null if unsuccessful
     */
    public static ReportTemplate createNewReportTemplate(
            EOEditingContext    ec,
            User                author,
            String              uploadedName,
            NSData              uploadedData,
            int                 version,
            NSMutableDictionary errors)
    {
        String userTemplateDir = userTemplateDirName( author ).toString();
        uploadedName = ( new File( uploadedName ) ).getName();
        if(uploadedName.endsWith(".rptdesign"))
        {
        	uploadedName = uploadedName.substring(0,
        			uploadedName.length() - ".rptdesign".length());
        	uploadedName += "_" + displayableVersion(version) + ".rptdesign";
        }

        File toLookFor = new File( userTemplateDir + "/" + uploadedName );

        if ( toLookFor.exists() )
        {
            String msg = "You already have an uploaded report template with "
            	         + "this name.  Please use a different file name "
                         + "for this new template.";
            errors.setObjectForKey( msg, msg );
            return null;
        }

        ReportTemplate template = new ReportTemplate();
        ec.insertObject( template );
        template.setName("");
        ec.saveChanges();

        template.setVersion(version);
        template.setUploadedFileName( uploadedName );
        template.setLastModified( new NSTimestamp() );
        template.setAuthorRelationship( author );

        // Save the file to disk
        log.debug( "Saving report template to disk: " + template.filePath() );
        File scriptPath = new File( template.filePath() );
        try
        {
            scriptPath.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream( scriptPath );
            uploadedData.writeToStream( out );
            out.close();
        }
        catch ( java.io.IOException e )
        {
            String msg = e.getMessage();
            errors.setObjectForKey( msg, msg );
            ec.deleteObject( template );
            scriptPath.delete();
            return null;
        }

        SessionHandle designSession = Reporter.getInstance().newDesignSession();

        try
        {
            ReportDesignHandle reportHandle = designSession.openDesign(
            		template.filePath());

	        String msg = template.initializeAttributes(reportHandle);
	        if (msg != null )
	        {
	            errors.setObjectForKey( msg, msg );
	            ec.deleteObject( template );
	            scriptPath.delete();
	            return null;
	        }
	        
	        msg = template.processDataSets(ec, reportHandle);
	        if ( msg != null )
	        {
	            errors.setObjectForKey( msg, msg );
	            ec.deleteObject( template );
	            scriptPath.delete();
	            return null;
	        }
	
	        return template;
		}
		catch(Exception e)
		{
			String msg = "There was an internal error opening the report template: "
				+ e.toString();
	        errors.setObjectForKey( msg, msg );
	       	ec.deleteObject( template );
	       	scriptPath.delete();
			return null;
		}
		finally
		{
			try { designSession.closeAll(false); }
			catch(Exception e) { }
		}
	}


    public static ReportTemplate createNewReportTemplate(
            EOEditingContext    ec,
            User                author,
            String              fullPath,
            NSMutableDictionary errors)
    {
    	File scriptPath = new File(fullPath);
    	  
        ReportTemplate template = new ReportTemplate();
        ec.insertObject( template );
        template.setName("");
        ec.saveChanges();

        template.setUploadedFileName( scriptPath.getName() );
        template.setLastModified( new NSTimestamp() );
        template.setAuthorRelationship( author );

        SessionHandle designSession = Reporter.getInstance().newDesignSession();

    	try
    	{
	        ReportDesignHandle reportHandle = designSession.openDesign(
	        		template.filePath());

	        String msg = template.initializeAttributes(reportHandle);
	        if (msg != null )
	        {
	            errors.setObjectForKey( msg, msg );
	            ec.deleteObject( template );
	            scriptPath.delete();
	            return null;
	        }
	
	        msg = template.processDataSets(ec, reportHandle);
	        if ( msg != null )
	        {
	            errors.setObjectForKey( msg, msg );
	            ec.deleteObject( template );
	            scriptPath.delete();
	            return null;
	        }

	        return template;
    	}
    	catch(Exception e)
    	{
    		String msg = "There was an internal error opening the report template: "
    			+ e.toString();
            errors.setObjectForKey( msg, msg );
           	ec.deleteObject( template );
           	scriptPath.delete();
    		return null;
    	}
    	finally
    	{
    		try { designSession.closeAll(false); }
    		catch(Exception e) { }
    	}
    }

    	
    public void deleteTemplate(EOEditingContext ec)
    {
        File scriptPath = new File( filePath() );
        ec.deleteObject( this );
        scriptPath.delete();
    }

    
    // ----------------------------------------------------------
    /**
     * Initializes various report template attributes in the EO model.
     */
    private String initializeAttributes(ReportDesignHandle reportHandle)
    {
    	String title = reportHandle.getStringProperty(
    			ReportDesignHandle.TITLE_PROP);
    	
    	if(title == null || title.trim().length() == 0)
    	{
    		String msg = "The report template you tried to upload does not have a title. " +
    			"Please enter one in the <b>Title</b> field of the General Properties " +
    			"section of the report designer and then upload it again.";
    		
    		return msg;
    	}

    	String description = reportHandle.getDescription();

    	setName(title);
    	setDescription(description);
    	
    	return null;
    }
    
    
    private class DataSetCollector implements IDesignElementVisitor
    {
    	public DataSetCollector()
    	{
    		dataSetRefCounts =
    			new NSMutableDictionary<DataSetHandle, Integer>();
    	}

		public void accept(DesignElementHandle handle)
		{
			if(handle instanceof ReportItemHandle)
			{
				ReportItemHandle repItem = (ReportItemHandle)handle;
				DataSetHandle dataSet = repItem.getDataSet();

				if(dataSet != null)
				{
					String extensionID =
						dataSet.getStringProperty("extensionID");
					
		    		if("net.sf.webcat.oda.dataSet".equals(extensionID))
		    		{
		    			if(dataSetRefCounts.containsKey(dataSet))
		    			{
		    				int count = dataSetRefCounts.objectForKey(dataSet);
		    				count++;
		    				dataSetRefCounts.setObjectForKey(count, dataSet);
		    			}
		    			else
		    			{
		    				dataSetRefCounts.setObjectForKey(Integer.valueOf(1),
		    						dataSet);
		    			}
		    		}
				}
			}
		}
		
		public NSDictionary<DataSetHandle, Integer> dataSetsAndRefCounts()
		{
			return dataSetRefCounts;
		}

		private NSMutableDictionary<DataSetHandle, Integer> dataSetRefCounts;
    }
    

    // ----------------------------------------------------------
    private String processDataSets(EOEditingContext ec, ReportDesignHandle reportHandle)
    {
    	ReportBodyWalker walker = new ReportBodyWalker(reportHandle);
    	DataSetCollector collector = new DataSetCollector();
    	walker.visit(collector);
    	
    	NSDictionary<DataSetHandle, Integer> sets =
    		collector.dataSetsAndRefCounts();

    	for(DataSetHandle dataSetHandle : sets.allKeys())
    	{
    		int refCount = sets.objectForKey(dataSetHandle);

			String name = dataSetHandle.getName();
			String description = dataSetHandle.getComments();
			String queryText = dataSetHandle.getStringProperty("queryText");
			RelationInformation relation = new RelationInformation(queryText);

			ReportDataSet.createNewReportDataSet(ec, this,
					relation.getDataSetUuid(),
					relation.getEntityType(),
					name, description, refCount);    		
    	}
    	
    	return null;
    }
    
    
    // ----------------------------------------------------------
    /**
     * Adds the parameters of the report template to the EO model and sets up
     * the appropriate associations and dependencies.
     */
/*    private String processParameters(EOEditingContext ec,
    		ReportDesignHandle reportHandle)
    {
    	Iterator it = reportHandle.getAllParameters().iterator();
    	while(it.hasNext())
    	{
    		ParameterHandle paramHandleBase = (ParameterHandle)it.next();
    		if(!(paramHandleBase instanceof ScalarParameterHandle))
    			continue;
    		
    		ScalarParameterHandle paramHandle =
    			(ScalarParameterHandle)paramHandleBase;
    		
    		String binding = paramHandle.getName();
    		String displayName = paramHandle.getPromptText();
    		String description = paramHandle.getHelpText();
    		String optionString = paramHandle.getDefaultValue();

    		try
    		{
	    		ParameterParser parser = new ParameterParser(optionString);
	    		parser.parse();
	    		
	    		String type = parser.getType();
	    		NSDictionary options = parser.getOptions();
	    		
	    		ReportParameter.createNewReportParameter(ec, this,
	    				binding, type, displayName, description, options);
    		}
    		catch(Exception e)
    		{
    			return "Error processing parameter '" + binding + "': " +
    				e.getMessage();
    		}
    	}

		String msg = computeAllParameterDependencies();
		if(msg != null)
			return msg;

    	return null;
    }*/

    
    // ----------------------------------------------------------
    /**
     * Computes the dependencies among all the parameters in the report
     * template.
     */
/*    private String computeAllParameterDependencies()
    {
    	Enumeration e = parameters().objectEnumerator();
    	while(e.hasMoreElements())
    	{
    		ReportParameter param = (ReportParameter)e.nextElement();
    		
    		String msg = computeDependenciesForParameter(param);
    		if(msg != null)
    			return msg;
    	}

    	return null;
    }*/
    
    
    // ----------------------------------------------------------
    /**
     * Computes the dependencies for the specified parameter in the report
     * template.
     */
/*    private String computeDependenciesForParameter(ReportParameter param)
    {
    	NSMutableArray dependentBindings = new NSMutableArray();

    	if(param.hasOption(ReportParameter.OPTION_SOURCE))
    	{
    		ognl.Node ast = (ognl.Node)param.sourceOption();

    		String msg = ognl.OgnlQualifierUtils.computeDependenciesFromOgnlAST(
    				ast, dependentBindings);
    		
    		if(msg != null)
    			return msg;
    	}
    	
    	if(param.hasOption(ReportParameter.OPTION_FILTER))
    	{
    		ognl.Node ast = (ognl.Node)param.filterOption();

    		String msg = ognl.OgnlQualifierUtils.computeDependenciesFromOgnlAST(
    				ast, dependentBindings);
    		
    		if(msg != null)
    			return msg;
    	}

    	if(param.hasOption(ReportParameter.OPTION_QUALIFIER))
    	{
    		EOQualifier qualifier = param.qualifierOption();
    		Enumeration e = qualifier.bindingKeys().objectEnumerator();
    		while(e.hasMoreElements())
    		{
    			String binding = (String)e.nextElement();

    			if(binding.startsWith("selected."))
    			{
    				binding = binding.substring("selected.".length());
    				dependentBindings.addObject(binding);
    			}
    		}
    	}
    	
    	String dependString = "";

    	Enumeration e = dependentBindings.objectEnumerator();
    	while(e.hasMoreElements())
    	{
    		String binding = (String)e.nextElement();
    		dependString += binding + " ";
    		
    		ReportParameter dependent = parameterWithBinding(binding);
    		
    		if(dependent == null)
    		{
    			return "Parameter '" + param.binding() + "' depends on " +
    				"non-existant parameter '" + binding + "'.";
    		}
    		else if(dependent.isDependentOn(param))
    		{
    			return "Circular dependency between parameters '" +
    				binding + "' and '" + param.binding() + "'.";
    		}
    		else
    		{
    			param.addToDependsOnRelationship(dependent);
    		}
    	}
    	
    	return null;
    }*/

    
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

    
    //  ----------------------------------------------------------
    /**
     * Implements a standard reverse topological sort algorithm in order to
     * sort the parameters of a report in dependency order (parameters occur
     * before those that depend on them).
     */
/*    private static class ReverseTopologicalSort
    {
    	private NSArray parameters;

    	private int sortedIndex;
    	private boolean[] visited;
    	private int[] reordering;
    	
    	/**
    	 * Creates a new sorter and stores the result of sorting the
    	 * specified parameters.
    	 * 
    	 * @param parameters an NSArray of ReportParameter objects to sort
    	 */
/*    	public ReverseTopologicalSort(NSArray parameters)
    	{
    		this.parameters = parameters;
    		int paramCount = parameters.count();
    		
    		sortedIndex = 0;
    		visited = new boolean[paramCount];
    		reordering = new int[paramCount];
    		
    		for(int i = 0; i < paramCount; i++)
    			reordering[i] = -1;
    		
    		for(int i = 0; i < paramCount; i++)
    			if(!visited[i])
    				sortRecursive(i);
    	}

    	private void sortRecursive(int index)
    	{
    		visited[index] = true;
    		
    		ReportParameter parameter =
    			(ReportParameter)parameters.objectAtIndex(index);
    		
    		Enumeration dependsOn = parameter.dependsOn().objectEnumerator();
    		
    		while(dependsOn.hasMoreElements())
    		{
    			ReportParameter dependent =
    				(ReportParameter)dependsOn.nextElement();
    			
    			int dependentIndex =
    				parameters.indexOfIdenticalObject(dependent);
    			
    			if(!visited[dependentIndex])
    				sortRecursive(dependentIndex);
    		}
    		
    		reordering[sortedIndex++] = index;
    	}
    	
    	/**
    	 * 
    	 * @return
    	 */
/*    	public NSArray sortedParameters()
    	{
    		NSMutableArray sorted = new NSMutableArray();

    		for(int i = 0; i < parameters.count(); i++)
    			sorted.addObject(parameters.objectAtIndex(reordering[i]));
    		
    		return sorted;
    	}
    }*/

    
    static private String templateRoot = null;
    static Logger log = Logger.getLogger( ReportTemplate.class );
}
