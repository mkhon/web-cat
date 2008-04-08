/*==========================================================================*\
 |  $Id: PreviewQueryComparison.java,v 1.1 2008/04/08 18:31:05 aallowat Exp $
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

package net.sf.webcat.oda.designer.preview;

import net.sf.webcat.oda.designer.DesignerActivator;
import net.sf.webcat.oda.designer.contentassist.ContentAssistManager;

public enum PreviewQueryComparison
{
    IS_EQUAL_TO("==", true), IS_NOT_EQUAL_TO("!=", true), IS_LESS_THAN("<",
            true), IS_LESS_THAN_OR_EQUAL_TO("<=", true), IS_GREATER_THAN(">",
            true), IS_GREATER_THAN_OR_EQUAL_TO(">=", true), IS_BETWEEN(
            "is between", false), IS_NOT_BETWEEN("is not between", false), IS_LIKE(
            "is like", true), IS_NOT_LIKE("is not like", true), IS_ONE_OF(
            "is one of", false), IS_NOT_ONE_OF("is not one of", false);

    PreviewQueryComparison(String representation, boolean supportsKeyPaths)
    {
        this.representation = representation;
        this.supportsKeyPaths = supportsKeyPaths;
    }


    public String representation()
    {
        return representation;
    }


    public boolean supportsKeyPaths()
    {
        return supportsKeyPaths;
    }


    public static final PreviewQueryComparison[] BOOLEAN_COMPARISONS = {
            IS_EQUAL_TO, IS_NOT_EQUAL_TO };

    public static final PreviewQueryComparison[] NUMERIC_COMPARISONS = {
            IS_EQUAL_TO, IS_NOT_EQUAL_TO, IS_LESS_THAN,
            IS_LESS_THAN_OR_EQUAL_TO, IS_GREATER_THAN,
            IS_GREATER_THAN_OR_EQUAL_TO, IS_BETWEEN, IS_NOT_BETWEEN, IS_ONE_OF,
            IS_NOT_ONE_OF };

    public static final PreviewQueryComparison[] STRING_COMPARISONS = {
            IS_EQUAL_TO, IS_NOT_EQUAL_TO, IS_BETWEEN, IS_NOT_BETWEEN, IS_LIKE,
            IS_NOT_LIKE, IS_ONE_OF, IS_NOT_ONE_OF };

    public static final PreviewQueryComparison[] TIMESTAMP_COMPARISONS = {
            IS_EQUAL_TO, IS_NOT_EQUAL_TO, IS_LESS_THAN,
            IS_LESS_THAN_OR_EQUAL_TO, IS_GREATER_THAN,
            IS_GREATER_THAN_OR_EQUAL_TO, IS_BETWEEN, IS_NOT_BETWEEN };

    public static final PreviewQueryComparison[] OBJECT_COMPARISONS = {
            IS_EQUAL_TO, IS_NOT_EQUAL_TO, IS_ONE_OF, IS_NOT_ONE_OF };


    public static PreviewQueryComparison[] comparisonsForType(String type)
    {
        if (type.equals("boolean") || type.equals("Boolean"))
        {
            return BOOLEAN_COMPARISONS;
        }
        else if (type.equals("Number") || type.equals("Integer")
                || type.equals("int") || type.equals("Float")
                || type.equals("float") || type.equals("Double")
                || type.equals("double"))
        {
            return NUMERIC_COMPARISONS;
        }
        else if (type.equals("string") || type.equals("String"))
        {
            return STRING_COMPARISONS;
        }
        else if (type.equals("Date") || type.equals("NSTimestamp"))
        {
            return TIMESTAMP_COMPARISONS;
        }
        else
        {
            ContentAssistManager cam = DesignerActivator.getDefault()
                    .getContentAssistManager();

            if (cam.isEntity(type))
            {
                return OBJECT_COMPARISONS;
            }
            else
            {
                return null;
            }
        }
    }


    public static PreviewQueryComparison comparisonWithRepresentation(String rep)
    {
        if ("==".equals(rep))
            return IS_EQUAL_TO;
        else if ("!=".equals(rep))
            return IS_NOT_EQUAL_TO;
        else if ("<".equals(rep))
            return IS_LESS_THAN;
        else if ("<=".equals(rep))
            return IS_LESS_THAN_OR_EQUAL_TO;
        else if (">".equals(rep))
            return IS_GREATER_THAN;
        else if (">=".equals(rep))
            return IS_GREATER_THAN_OR_EQUAL_TO;
        else if ("is between".equals(rep))
            return IS_BETWEEN;
        else if ("is not between".equals(rep))
            return IS_NOT_BETWEEN;
        else if ("is like".equals(rep))
            return IS_LIKE;
        else if ("is not like".equals(rep))
            return IS_NOT_LIKE;
        else if ("is one of".equals(rep))
            return IS_ONE_OF;
        else if ("is not one of".equals(rep))
            return IS_NOT_ONE_OF;
        else
            return null;
    }


    private String representation;

    private boolean supportsKeyPaths;
}
