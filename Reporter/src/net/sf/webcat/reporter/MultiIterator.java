/*==========================================================================*\
 |  $Id: MultiIterator.java,v 1.2 2008/04/01 03:08:38 stedwar2 Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU General Public License as published by
 |  the Free Software Foundation; either version 2 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU General Public License
 |  along with Web-CAT; if not, write to the Free Software
 |  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 |
 |  Project manager: Stephen Edwards <edwards@cs.vt.edu>
 |  Virginia Tech CS Dept, 660 McBryde Hall (0106), Blacksburg, VA 24061 USA
\*==========================================================================*/

package net.sf.webcat.reporter;

import java.util.Iterator;

//-------------------------------------------------------------------------
/**
 * Supports iterating over a sequence of iterators by cycling through
 * each one in turn until that one is exhausted, then moving on to the
 * next.
 *
 * @param <T> The type of object returned by all the iterators contained
 *            within this multi-iterator.
 *
 * @author  Anthony Allevato
 * @version $Id: MultiIterator.java,v 1.2 2008/04/01 03:08:38 stedwar2 Exp $
 */
public class MultiIterator<T>
    implements Iterator<T>
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Construct a new object.
     * @param iters The sequence of iterators to traverse
     */
	public MultiIterator(Iterator[] iters)
	{
		this.iterators = iters;
		index = 0;
	}


    //~ Methods ...............................................................

    // ----------------------------------------------------------
	public boolean hasNext()
	{
		while (index < iterators.length
               && (iterators[index] == null || !iterators[index].hasNext()))
		{
			index++;
		}
        return index < iterators.length && iterators[index].hasNext();
	}


    // ----------------------------------------------------------
	public T next()
	{
		return (T)iterators[index].next();
	}


    // ----------------------------------------------------------
	public void remove()
	{
		throw new RuntimeException("remove() is not supported");
	}


    //~ Instance/static variables .............................................

	private Iterator[] iterators;
	private int index;
}
