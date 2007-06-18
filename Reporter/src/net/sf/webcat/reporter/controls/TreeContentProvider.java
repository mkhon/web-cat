package net.sf.webcat.reporter.controls;

import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

public abstract class TreeContentProvider
{
	public abstract String idForElement(Object element);

	public abstract Object elementWithId(String element);

	public abstract Object[] childrenForElement(Object element);
	
	public abstract boolean hasChildren(Object element);
}
