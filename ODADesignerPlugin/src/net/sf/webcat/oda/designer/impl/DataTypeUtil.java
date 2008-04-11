/*==========================================================================*\
 |  $Id: DataTypeUtil.java,v 1.2 2008/04/11 00:58:37 aallowat Exp $
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
import net.sf.webcat.oda.designer.i18n.Messages;

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
        displayNameDataTypeMapping.put(Messages.DATATYPE_DISPLAYNAME_TIMESTAMP, Integer
                .valueOf(DataTypes.TIMESTAMP));
        displayNameDataTypeMapping.put(Messages.DATATYPE_DISPLAYNAME_DECIMAL, Integer
                .valueOf(DataTypes.DECIMAL));
        displayNameDataTypeMapping.put(Messages.DATATYPE_DISPLAYNAME_FLOAT, Integer
                .valueOf(DataTypes.DOUBLE));
        displayNameDataTypeMapping.put(Messages.DATATYPE_DISPLAYNAME_INTEGER, Integer
                .valueOf(DataTypes.INT));
        displayNameDataTypeMapping.put(Messages.DATATYPE_DISPLAYNAME_STRING, Integer
                .valueOf(DataTypes.STRING));
        displayNameDataTypeMapping.put(Messages.DATATYPE_DISPLAYNAME_BOOLEAN, Integer
                .valueOf(DataTypes.BOOLEAN));

        dataTypeDisplayNameMapping.put(Integer.valueOf(DataTypes.TIMESTAMP),
                Messages.DATATYPE_DISPLAYNAME_TIMESTAMP);
        dataTypeDisplayNameMapping.put(Integer.valueOf(DataTypes.DECIMAL),
                Messages.DATATYPE_DISPLAYNAME_DECIMAL);
        dataTypeDisplayNameMapping.put(Integer.valueOf(DataTypes.DOUBLE),
                Messages.DATATYPE_DISPLAYNAME_FLOAT);
        dataTypeDisplayNameMapping.put(Integer.valueOf(DataTypes.INT),
                Messages.DATATYPE_DISPLAYNAME_INTEGER);
        dataTypeDisplayNameMapping.put(Integer.valueOf(DataTypes.STRING),
                Messages.DATATYPE_DISPLAYNAME_STRING);
        dataTypeDisplayNameMapping.put(Integer.valueOf(DataTypes.BOOLEAN),
                Messages.DATATYPE_DISPLAYNAME_BOOLEAN);
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
            return Messages.DATATYPE_DISPLAYNAME_STRING;
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
