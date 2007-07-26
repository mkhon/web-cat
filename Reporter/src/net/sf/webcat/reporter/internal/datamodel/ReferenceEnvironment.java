package net.sf.webcat.reporter.internal.datamodel;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import net.sf.webcat.core.Application;
import net.sf.webcat.reporter.EnqueuedReportJob;
import net.sf.webcat.reporter.ProgressManager;
import net.sf.webcat.reporter.ReportParameter;

import ognl.MethodFailedException;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.webobjects.NSObjectPropertyAccessor;
import ognl.webobjects.WOOgnl;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSValidation;

import er.extensions.ERXFetchSpecificationBatchIterator;

/**
 * The reference environment for a query stores the "symbol table" of object
 * references that can be accessed in the statements of a query and in result
 * set data accesses.
 * 
 * @author aallowat
 */
public class ReferenceEnvironment
{
	/**
	 * The table of bindings used to refer to objects in the Web-CAT object
	 * graph.
	 */
	private NSMutableDictionary bindings;

	/**
	 * The editing context used to access entities in the EO model.
	 */
	private EOEditingContext context;

	private String progressUuid;

	/**
	 * Creates a new reference environment and populates its bindings table
	 * with the specified initial references.
	 * 
	 * @param initialBindings a set of initial bindings to be used when
	 *     initializing the reference environment
	 * @param context the editing context used to access entities in the EO
	 *     model
	 */
	public ReferenceEnvironment(NSDictionary initialBindings,
			String progressUuid)
	{
		this.context = Application.newPeerEditingContext();

		bindings = new NSMutableDictionary();
		bindings.setObjectForKey(context, "context");
		bindings.addEntriesFromDictionary(initialBindings);
		
		this.progressUuid = progressUuid;
	}
	
	public void forceEditingContext(EOEditingContext context)
	{
		Application.releasePeerEditingContext(this.context);
		this.context = context;

		bindings.setObjectForKey(context, "context");
}

	public void recycleEditingContext(ERXFetchSpecificationBatchIterator iter)
	{
		Application.releasePeerEditingContext(context);
		context = Application.newPeerEditingContext();
		
		bindings.setObjectForKey(context, "context");

		if(iter != null)
			iter.setEditingContext(context);
	}
	
	/**
	 * Evaluates the specified key path. The root of this key path can be
	 * either a binding in the referencing environment or the name of an
	 * entity in the EO model.
	 * 
	 * @param keyPath the key path to evaluate
	 * 
	 * @return the result of evaluating the key path
	 */
	public Object evaluate(String expression)
	{
		NSMutableDictionary options = new NSMutableDictionary();

		try
		{
			options.setObjectForKey(ognl.Ognl.parseExpression(expression),
					ReportParameter.OPTION_SOURCE);
		}
		catch (OgnlException e)
		{
			log.error("Malformed OGNL expression", e);
			
			throw new IllegalArgumentException(
					"OGNL expression was malformed", e);
		}

		return evaluate(options);
	}

	private static class StringMethodAccessor implements ognl.MethodAccessor
	{
		private Boolean isLike(Object object, String expr)
		{
			StringBuilder regex = new StringBuilder();
			for(int i = 0; i < expr.length(); i++)
			{
				char ch = expr.charAt(i);
				if(ch == '*')
					regex.append(".*");
				else if(ch == '?')
					regex.append('.');
				else if("+()^$.{}[]|\\".indexOf(ch) != -1)
					regex.append('\\').append(ch);
				else
					regex.append(ch);
			}
			
			return Pattern.matches(regex.toString(), object.toString()) ?
					Boolean.TRUE : Boolean.FALSE;
		}

		private Boolean isLikeNoCase(Object object, String expr)
		{
			return isLike(object.toString().toLowerCase(), expr.toLowerCase());
		}

		public Object callMethod(Map context, Object target,
				String methodName, Object[] args) throws MethodFailedException
		{
			if("isLike".equals(methodName))
			{
				return isLike(target, args[0].toString());
			}
			else if("isLikeNoCase".equals(methodName))
			{
				return isLikeNoCase(target, args[0].toString());
			}
			else
			{
				throw new MethodFailedException(target, methodName, null);
			}
		}

		public Object callStaticMethod(Map context, Class targetClass,
				String methodName, Object[] args) throws MethodFailedException
		{
			throw new MethodFailedException(targetClass, methodName, null);
		}
	}

	static
	{
		OgnlRuntime.setMethodAccessor(String.class, new StringMethodAccessor());
	}

	/**
	 * Evaluates the specified key path and applies the specified "where"
	 * clause to filter the result if it is an array. The root of this
	 * key path can be either a binding in the reference environment or
	 * the name of an entity in the EO model.
	 * 
	 * If the result of evaluating the key path is not an array, the
	 * "where" clause is ignored.
	 * 
	 * @param keyPath the key path to evaluate
	 * @param whereClause the clause used to filter the result if it is
	 *     an array
	 * 
	 * @return the result of evaluating the key path
	 */
	public Object evaluate(NSDictionary options)
	{
		String binding = (String)options.objectForKey("binding");

		if(options.containsKey(ReportParameter.OPTION_ENTITY))
		{
			String entity = (String)options.objectForKey(
					ReportParameter.OPTION_ENTITY);
			
			EOQualifier qualifier = (EOQualifier)options.objectForKey(
					ReportParameter.OPTION_QUALIFIER);
			
			NSArray sortOrderings = (NSArray)options.objectForKey(
					ReportParameter.OPTION_SORT_ORDERINGS);

			if(qualifier != null)
				qualifier = qualifier.qualifierWithBindings(bindings, true);

			EOFetchSpecification spec = new EOFetchSpecification(entity,
					qualifier, sortOrderings);
/*			if(entity.equals("Submission"))
			{
				NSMutableArray keyPaths = new NSMutableArray();
				keyPaths.addObject("user");
				keyPaths.addObject("result");
				spec.setPrefetchingRelationshipKeyPaths(keyPaths);
			}*/
			Boolean useIterator = (Boolean)options.objectForKey("useIterator");
			Object result;
			
			if(useIterator != null && useIterator.booleanValue())
			{
				result = new ERXFetchSpecificationBatchIterator(spec, context);
			}
			else
			{
				NSArray _result = context.objectsWithFetchSpecification(spec);
				
				Object filter = options.objectForKey(ReportParameter.OPTION_FILTER);
				if(filter != null)
				{
					_result = filterArrayWithOgnlExpression(binding, _result, filter);
				}
				
				result = _result;
			}

			return result;
		}
		else
		{
			Object expression = options.objectForKey(
					ReportParameter.OPTION_SOURCE);
			
			Object result = evaluateOgnl(expression, null);
			
			if(result instanceof List)
			{
				List list = (List)result;
				
				Object filter = options.objectForKey(
						ReportParameter.OPTION_FILTER);
				
				if(filter != null)
				{
					result = filterArrayWithOgnlExpression(binding, list,
							filter);
				}
				
				NSArray sortOrderings = (NSArray)options.objectForKey(
						ReportParameter.OPTION_SORT_ORDERINGS);
				if(sortOrderings != null)
				{
					if(!(list instanceof NSArray))
					{
						list = new NSMutableArray(list.toArray());
					}
					else if(!(list instanceof NSMutableArray))
					{
						list = ((NSArray)list).mutableClone();
					}
					
					EOSortOrdering.sortArrayUsingKeyOrderArray(
							(NSMutableArray)list, sortOrderings);
					result = list;
				}
			}

			if(result == NSKeyValueCoding.NullValue)
				result = null;

			return result;
		}
	}

	private Object evaluateOgnl(Object expression, Class resultType)
	{
		Hashtable ctx = WOOgnl.factory().newDefaultContext();
		ctx.putAll(bindings);

		try
		{
			if(resultType != null)
				return ognl.Ognl.getValue(expression, ctx, ctx, resultType);
			else
				return ognl.Ognl.getValue(expression, ctx, ctx);
		}
		catch (OgnlException e)
		{
			if(e.getMessage().startsWith("source is null"))
				return null;

			log.error("Failed to evaluate OGNL expression", e);
			
			throw new IllegalArgumentException(
					"Failed to evaluate OGNL expression", e);
		}
	}

	private NSArray filterArrayWithOgnlExpression(String binding,
			List array, Object filter)
	{
		NSMutableArray filtered = new NSMutableArray();
		
		for(int i = 0; i < array.size(); i++)
		{
			Object obj = array.get(i);
			
			bindings.setObjectForKey(obj, binding);
			Object result = evaluateOgnl(filter, null);
			
			if(result instanceof Boolean)
			{
				if(((Boolean)result).booleanValue())
				{
					filtered.addObject(obj);
				}
			}
			else
			{
				log.error("Filter expression must evaluate to a boolean value" +
						": " + filter.toString());
				
				throw new IllegalArgumentException(
						"Filter expression must evaluate to a boolean value");
			}
		}

		bindings.removeObjectForKey(binding);
		return filtered;
	}

	/**
	 * Adds an object to the reference environment with the specified name.
	 * 
	 * @param binding the name of the object to add
	 * @param obj the object to add
	 */
	public void define(String binding, Object obj)
	{
		if(obj == null)
			obj = NSKeyValueCoding.NullValue;

		bindings.setObjectForKey(obj, binding);
	}
	
	/**
	 * Removes the object with the specified name from the reference
	 * environment.
	 * 
	 * @param binding the name of the object to remove
	 */
	public void undefine(String binding)
	{
		bindings.removeObjectForKey(binding);
	}
	
	public void startProgressTask(int totalWork)
	{
		ProgressManager.getInstance().beginTaskForJob(progressUuid, totalWork);
	}
	
	public void stepProgress(int delta)
	{		
		ProgressManager.getInstance().stepJob(progressUuid, delta);
	}

	public void completeCurrentTask()
	{
		ProgressManager.getInstance().completeCurrentTaskForJob(progressUuid);
	}

	private static Logger log = Logger.getLogger(ReferenceEnvironment.class);
}
