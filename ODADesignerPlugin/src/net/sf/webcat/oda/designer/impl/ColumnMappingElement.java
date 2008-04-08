/*==========================================================================*\
 |  $Id: ColumnMappingElement.java,v 1.1 2008/04/08 18:31:11 aallowat Exp $
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

package net.sf.webcat.oda.designer.impl;

public class ColumnMappingElement
{
    private String columnName;
    private String expression;
    private String type;


    public String getColumnName()
    {
        return columnName;
    }


    public void setColumnName(String value)
    {
        columnName = value;
    }


    public String getExpression()
    {
        return expression;
    }


    public void setExpression(String value)
    {
        expression = value;
    }


    public String getType()
    {
        return type;
    }


    public void setType(String value)
    {
        type = value;
    }

}
