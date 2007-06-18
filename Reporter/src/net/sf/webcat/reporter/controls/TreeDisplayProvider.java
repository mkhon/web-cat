package net.sf.webcat.reporter.controls;

public abstract class TreeDisplayProvider
{
	public String textForElement(Object element)
	{
		return element.toString();
	}
	
	public String classForElement(Object element)
	{
		return "file";
	}
	
	public boolean isElementDisabled(Object element)
	{
		return false;
	}
	
	public String tipForElement(Object element)
	{
		return null;
	}
}
