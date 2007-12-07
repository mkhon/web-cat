package net.sf.webcat.reporter.controls;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver._private.WODynamicGroup;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;

import er.ajax.AjaxUtils;

public class HeadInserter extends WODynamicGroup
{
	public HeadInserter(String arg0, NSDictionary arg1, WOElement arg2)
	{
		super(arg0, arg1, arg2);
	}

	public void appendToResponse(WOResponse response, WOContext context)
	{
		// I have no idea if this is a good idea whatsoever.  But it looks like
		// it works.
		WOResponse childrenResponse = new WOResponse();
		super.appendToResponse(childrenResponse, context);

//		AjaxUtils.insertInResponseBeforeTag(response,
//				childrenResponse.contentString(), AjaxUtils.htmlCloseHead());
	}
}
