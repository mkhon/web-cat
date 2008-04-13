/*==========================================================================*\
 |  $Id: OgnlDocumentProvider.java,v 1.2 2008/04/13 22:04:52 aallowat Exp $
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

package net.sf.webcat.oda.designer.ognl;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;

//------------------------------------------------------------------------
/**
 * TODO: real description
 *
 * @author Tony Allevato (Virginia Tech Computer Science)
 * @version $Id: OgnlDocumentProvider.java,v 1.2 2008/04/13 22:04:52 aallowat Exp $
 */
public class OgnlDocumentProvider extends StorageDocumentProvider
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    public OgnlDocumentProvider()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    protected IDocument createDocument(Object element) throws CoreException
    {
        IDocument document = super.createDocument(element);
        if (document != null)
        {
            IDocumentPartitioner partitioner = new FastPartitioner(
                    new OgnlPartitionScanner(), colorTokens);
            partitioner.connect(document);
            document.setDocumentPartitioner(partitioner);
        }
        return document;
    }


    //~ Static/instance variables .............................................

    /** Array of token types. */
    private static String[] colorTokens = { OgnlPartitionScanner.OGNL_STRING,
            OgnlPartitionScanner.OGNL_KEYWORD,
            OgnlPartitionScanner.OGNL_STATIC_METHOD,
            OgnlPartitionScanner.OGNL_VARIABLE };
}
