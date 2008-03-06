package net.sf.webcat.reporter;

import java.util.Iterator;

public class MultiIterator<T> implements Iterator<T>
{
	public MultiIterator(Iterator[] iters)
	{
		this.iterators = iters;
		index = 0;
	}

	public boolean hasNext()
	{
		while(index < iterators.length &&
				(iterators[index] == null || !iterators[index].hasNext()))
		{
			index++;
		}
		
		if(index == iterators.length)
		{
			return false;
		}
		else
		{
			return iterators[index].hasNext();
		}
	}

	public T next()
	{
		return (T)iterators[index].next();
	}

	public void remove()
	{
		throw new RuntimeException("remove() is not supported"); 
	}
	
	private Iterator[] iterators;
	
	private int index;
}
