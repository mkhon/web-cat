package net.sf.webcat.reporter.actions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;

import net.sf.webcat.core.Application;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver.WOSession;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.ERXDirectAction;
import er.extensions.ERXFetchSpecificationBatchIterator;

public class dataPreview extends ERXDirectAction
{
	private static final String PARAM_ENTITY_TYPE = "entityType";
	
	private static final String PARAM_EXPRESSIONS = "expressions";
	
	private static final String SESSION_ITERATOR = "net.sf.webcat.reporter.actions.dataPreview.iterator";

	private static final String SESSION_EXPRESSIONS = "net.sf.webcat.reporter.actions.dataPreview.expressions";

	public dataPreview(WORequest request)
	{
		super(request);
	}

	public WOActionResults startAction()
	{
		WOResponse response = new WOResponse();
		WOSession session = session();
		
		String entityType = request().formValueForKey(PARAM_ENTITY_TYPE).toString();
		String expressionString = request().formValueForKey(PARAM_EXPRESSIONS).toString();

		String[] expressions = expressionString.split("%===%");

		EOEditingContext context = Application.newPeerEditingContext();
		EOFetchSpecification spec = new EOFetchSpecification(entityType, null, null);
		
		EOEntity rootEntity = EOUtilities.entityNamed(context, entityType);
		NSMutableArray prefetch = new NSMutableArray();
		for(String expression : expressions)
		{
			EOEntity entity = rootEntity;

			String[] parts = expression.split("\\.");
			String partsSoFar = "";
			for(String part : parts)
			{
				partsSoFar += part;

				EORelationship relationship = entity.relationshipNamed(part);
				if(relationship != null)
				{
					entity = relationship.destinationEntity();
					prefetch.addObject(partsSoFar);
				}
				else
				{
					break;
				}
				
				partsSoFar += ".";
			}
		}
		spec.setPrefetchingRelationshipKeyPaths(prefetch);

		ERXFetchSpecificationBatchIterator iterator =
			new ERXFetchSpecificationBatchIterator(spec, context);

		session.setObjectForKey(iterator, SESSION_ITERATOR);
		session.setObjectForKey(expressions, SESSION_EXPRESSIONS);

		response.appendContentString(session.sessionID());
		response.appendContentCharacter('\n');
		response.appendContentString(Integer.toString(iterator.count()));
		response.appendContentCharacter('\n');

		return response;
	}

	public WOActionResults cleanupAction()
	{
		WOResponse response = new WOResponse();
		WOSession session = session();
		
		session.removeObjectForKey(SESSION_ITERATOR);
		session.removeObjectForKey(SESSION_EXPRESSIONS);

		return response;
	}

	public WOActionResults nextBatchAction()
	{
		WOResponse response = new WOResponse();
		WOSession session = session();

		String[] expressions = (String[])session.objectForKey(SESSION_EXPRESSIONS);

		ERXFetchSpecificationBatchIterator iterator =
			(ERXFetchSpecificationBatchIterator)session.objectForKey(SESSION_ITERATOR);

		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			if(iterator.hasNextBatch())
			{
				NSArray batch = iterator.nextBatch();
				oos.writeInt(batch.count());

				Enumeration<?> e = batch.objectEnumerator();
				while(e.hasMoreElements())
				{
					EOGenericRecord eo = (EOGenericRecord)e.nextElement();
					
					for(int i = 0; i < expressions.length; i++)
					{
						Object value = eo.valueForKeyPath(expressions[i]);

						if(value instanceof NSTimestamp)
						{
							NSTimestamp timestamp = (NSTimestamp)value;
							long time = timestamp.getTime();
							java.sql.Timestamp sqlTime = new java.sql.Timestamp(time);
							value = sqlTime;
						}

						oos.writeObject(value);
					}
				}
			}
			else
			{
				oos.writeInt(0);
			}

			oos.close();
			baos.close();
			
			NSData data = new NSData(baos.toByteArray());
			response.appendContentData(data);
		}
		catch(Exception e)
		{
			response.setStatus(WOResponse.HTTP_STATUS_INTERNAL_ERROR);
			response.setContent(e.toString());
		}

		// Recycle the editing context after we've processed this batch.
		if(iterator != null)
		{
			Application.releasePeerEditingContext(iterator.editingContext());
			iterator.setEditingContext(Application.newPeerEditingContext());
		}
		
		return response;
	}

	public WOActionResults defaultAction()
	{
		WOResponse response = new WOResponse();

		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
	
			String entityType = request().formValueForKey("entityType").toString();
			String columnNames = request().formValueForKey("expressions").toString();
			int maxRecords = Integer.parseInt(request().formValueForKey("maxRecords").toString());
			String[] columns = columnNames.split("%===%");
			
			EOEditingContext ec = Application.newPeerEditingContext();
			EOFetchSpecification fetch = new EOFetchSpecification(entityType,
					null, null);
			fetch.setFetchLimit(maxRecords);
			NSArray objects = ec.objectsWithFetchSpecification(fetch);
			
			Enumeration e = objects.objectEnumerator();
			while(e.hasMoreElements())
			{
				EOGenericRecord eo = (EOGenericRecord)e.nextElement();
				
				for(int i = 0; i < columns.length; i++)
				{
					Object value = eo.valueForKeyPath(columns[i]);
					
					if(value instanceof NSTimestamp)
					{
						NSTimestamp timestamp = (NSTimestamp)value;
						long time = timestamp.getTime();
						java.sql.Timestamp sqlTime = new java.sql.Timestamp(time);
//						sqlTime.setNanos(timestamp.getNanos());
						value = sqlTime;
					}

					oos.writeObject(value);
				}
			}

			Application.releasePeerEditingContext(ec);
	
			oos.close();
			baos.close();
			
			NSData data = new NSData(baos.toByteArray());
			response.appendContentData(data);
		}
		catch(IOException e)
		{
			response.setStatus(WOResponse.HTTP_STATUS_INTERNAL_ERROR);
			response.setContent(e.toString());
		}

		return response;
	}
}
