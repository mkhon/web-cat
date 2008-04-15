/*==========================================================================*\
 |  $Id: MultiIterator.java,v 1.4 2008/04/15 04:09:22 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2008 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU Affero General Public License as published
 |  by the Free Software Foundation; either version 3 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU Affero General Public License
 |  along with Web-CAT; if not, see <http://www.gnu.org/licenses/>.
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
 * @author  Tony Allevato
 * @version $Id: MultiIterator.java,v 1.4 2008/04/15 04:09:22 aallowat Exp $
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
