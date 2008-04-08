/*==========================================================================*\
 |  $Id: DataTypeUtil.java,v 1.1 2008/04/08 18:31:11 aallowat Exp $
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

import java.util.HashMap;
import net.sf.webcat.oda.core.impl.DataTypes;

/**
 *
 *
 * @author Tony Allevato
 */
public class DataTypeUtil
{
    private static HashMap<String, Integer> displayNameDataTypeMapping = new HashMap<String, Integer>();

    private static HashMap<Integer, String> dataTypeDisplayNameMapping = new HashMap<Integer, String>();

    // -----------------------------------------------------------------------
    /**
     *
     */
    static
    {
        displayNameDataTypeMapping.put("Timestamp", Integer
                .valueOf(DataTypes.TIMESTAMP));
        displayNameDataTypeMapping.put("Decimal", Integer
                .valueOf(DataTypes.DECIMAL));
        displayNameDataTypeMapping.put("Float", Integer
                .valueOf(DataTypes.DOUBLE));
        displayNameDataTypeMapping.put("Integer", Integer
                .valueOf(DataTypes.INT));
        displayNameDataTypeMapping.put("String", Integer
                .valueOf(DataTypes.STRING));
        displayNameDataTypeMapping.put("Boolean", Integer
                .valueOf(DataTypes.BOOLEAN));

        dataTypeDisplayNameMapping.put(Integer.valueOf(DataTypes.TIMESTAMP),
                "Timestamp");
        dataTypeDisplayNameMapping.put(Integer.valueOf(DataTypes.DECIMAL),
                "Decimal");
        dataTypeDisplayNameMapping.put(Integer.valueOf(DataTypes.DOUBLE),
                "Float");
        dataTypeDisplayNameMapping.put(Integer.valueOf(DataTypes.INT),
                "Integer");
        dataTypeDisplayNameMapping.put(Integer.valueOf(DataTypes.STRING),
                "String");
        dataTypeDisplayNameMapping.put(Integer.valueOf(DataTypes.BOOLEAN),
                "Boolean");
    }


    // -----------------------------------------------------------------------
    /**
     *
     * @param type
     * @return
     */
    public static String getDataTypeDisplayName(int type)
    {
        String s = dataTypeDisplayNameMapping.get(Integer.valueOf(type));
        if (s != null)
            return s;
        else
            return "String";
    }


    // -----------------------------------------------------------------------
    /**
     *
     * @param displayName
     * @return
     */
    public static Integer getDataType(String displayName)
    {
        Integer i = displayNameDataTypeMapping.get(displayName);
        if (i != null)
            return i;
        else
            return Integer.valueOf(DataTypes.STRING);
    }
}
