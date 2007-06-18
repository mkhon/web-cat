package net.sf.webcat.reporter.controls;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

public class BrokerRegistry
{
	private NSMutableDictionary brokers;
	
	public BrokerRegistry()
	{
		brokers = new NSMutableDictionary();
	}
	
	public Broker brokerForComponent(String id)
	{
		return (Broker)brokers.objectForKey(id);
	}
	
	public void setBrokerForComponent(Broker broker, String id)
	{
		brokers.setObjectForKey(broker, id);
	}
}
