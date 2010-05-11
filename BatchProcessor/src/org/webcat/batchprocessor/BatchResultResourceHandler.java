/*==========================================================================*\
 |  $Id: BatchResultResourceHandler.java,v 1.1 2010/05/11 14:51:46 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2010 Virginia Tech
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

package org.webcat.batchprocessor;

import java.io.File;
import org.webcat.core.IEntityResourceHandler;

//-------------------------------------------------------------------------
/**
 * The Web-CAT entity resource handler for accessing resources associated with
 * BatchResult entities through direct URLs.
 *
 * @author  Tony Allevato
 * @version $Id: BatchResultResourceHandler.java,v 1.1 2010/05/11 14:51:46 aallowat Exp $
 */
public class BatchResultResourceHandler
implements IEntityResourceHandler<BatchResult>
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public File pathForResource(BatchResult object, String relativePath)
    {
        return new File(object.resultDir(), relativePath);
    }
}
