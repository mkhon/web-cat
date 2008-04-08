/*==========================================================================*\
 |  $Id: OgnlSyntaxContext.java,v 1.1 2008/04/08 18:31:04 aallowat Exp $
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

import net.sf.webcat.oda.designer.widgets.IKeyLabelProvider;
import net.sf.webcat.oda.designer.widgets.IKeyProvider;

public class OgnlSyntaxContext
{
    private String rootClassName;

    private IKeyProvider keyProvider;

    private IKeyLabelProvider keyLabelProvider;


    public OgnlSyntaxContext(String rootClass, IKeyProvider keyProv,
            IKeyLabelProvider keyLabelProv)
    {
        rootClassName = rootClass;
        keyProvider = keyProv;
        keyLabelProvider = keyLabelProv;
    }


    public String getRootClassName()
    {
        return rootClassName;
    }


    public IKeyProvider getKeyProvider()
    {
        return keyProvider;
    }


    public IKeyLabelProvider getKeyLabelProvider()
    {
        return keyLabelProvider;
    }
}