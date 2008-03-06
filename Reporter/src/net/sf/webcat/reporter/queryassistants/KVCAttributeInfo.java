package net.sf.webcat.reporter.queryassistants;

public class KVCAttributeInfo
{
	public KVCAttributeInfo(String name, String type)
	{
		this.name = name;
		this.type = type;
	}

	public String name()
	{
		return name;
	}
	
	public String type()
	{
		return type;
	}

	private String name;
	
	private String type;
}
