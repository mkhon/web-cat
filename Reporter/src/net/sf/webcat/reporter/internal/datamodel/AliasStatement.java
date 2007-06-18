package net.sf.webcat.reporter.internal.datamodel;

import com.webobjects.foundation.NSDictionary;

public class AliasStatement extends QueryStatement
{
	private NSDictionary options;
	
	public AliasStatement(NSDictionary options)
	{
		this.options = options;
	}
	
	public String binding()
	{
		return (String)options.objectForKey("binding");
	}
	
	public String entity()
	{
		return (String)options.objectForKey("entity");
	}

	public String sourceExpression()
	{
		return (String)options.objectForKey("source");
	}
	
	public NSDictionary options()
	{
		return options;
	}
}
