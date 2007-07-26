package net.sf.webcat.reporter.actions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;

import net.sf.webcat.core.Application;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;

import er.extensions.ERXDirectAction;

public class dataPreview extends ERXDirectAction
{
	public dataPreview(WORequest request)
	{
		super(request);
	}

	public WOActionResults defaultAction()
	{
		WOResponse response = new WOResponse();

		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
	
			String entityName = request().formValueForKey("entity").toString();
			String columnNames = request().formValueForKey("columns").toString();
			String[] columns = columnNames.split("|");
			
			EOEditingContext ec = Application.newPeerEditingContext();
			EOFetchSpecification fetch = new EOFetchSpecification(entityName,
					null, null);
			fetch.setFetchLimit(100);
			NSArray objects = ec.objectsWithFetchSpecification(fetch);
			
			Enumeration e = objects.objectEnumerator();
			while(e.hasMoreElements())
			{
				EOGenericRecord eo = (EOGenericRecord)e.nextElement();
				
				for(int i = 0; i < columns.length; i++)
				{
					Object value = eo.valueForKeyPath(columns[i]);
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
