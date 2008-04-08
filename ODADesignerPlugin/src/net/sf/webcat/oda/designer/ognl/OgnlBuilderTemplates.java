/*==========================================================================*\
 |  $Id: OgnlBuilderTemplates.java,v 1.1 2008/04/08 18:31:00 aallowat Exp $
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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.graphics.Point;

public class OgnlBuilderTemplates
{
    public static void insertTemplate(SourceViewer editor, String template)
    {
        Point range = editor.getSelectedRange();

        int selStart = template.indexOf("%%");
        template = template.replaceFirst("%%", "");
        int selLength = template.indexOf("%%") - selStart;
        template = template.replaceFirst("%%", "");

        try
        {
            editor.getDocument().replace(range.x, range.y, template);
            editor.setSelectedRange(range.x + selStart, selLength);
        }
        catch (BadLocationException e1)
        {
            // Ignore exception.
        }
    }


    // The %% markers denote the beginning and end of what should be the
    // selected range of text once the template is inserted.
    public static final String TEMPLATE_MIN = "@min(%%arg1, arg2%%)";
    public static final String TEMPLATE_MAX = "@max(%%arg1, arg2%%)";
    public static final String TEMPLATE_FLOOR = "@floor(%%arg%%)";
    public static final String TEMPLATE_CEIL = "@ceil(%%arg%%)";
    public static final String TEMPLATE_ROUND = "@round(%%arg%%)";
    public static final String TEMPLATE_ABS = "@abs(%%arg%%)";
    public static final String TEMPLATE_SIGNUM = "@signum(%%arg%%)";
    public static final String TEMPLATE_CONDITIONAL = "%%<condition>%% ? <if true> : <if false>";
    public static final String TEMPLATE_INSTANCEOF = " instanceof %%<class>%%";
    public static final String TEMPLATE_LIST = "{ %%item1, item2, ...%% }";
    public static final String TEMPLATE_MAP = "#{ %%key1 : value1, key2 : value2, ...%% }";
    public static final String TEMPLATE_CHAINED = ".( %%<expression>%% )";
    public static final String TEMPLATE_PROJECTION = ".{ %%<expression>%% }";
    public static final String TEMPLATE_SELECT_ALL = ".{? %%<condition>%% }";
    public static final String TEMPLATE_SELECT_FIRST = ".{^ %%<condition>%% }";
    public static final String TEMPLATE_SELECT_LAST = ".{$ %%<condition>%% }";
    public static final String TEMPLATE_LAMBDA = ":[ %%<expression>%% ]";
}
