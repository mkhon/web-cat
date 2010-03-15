/*==========================================================================*\
 |  $Id: Selector.js,v 1.1 2010/03/15 16:48:50 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2009 Virginia Tech
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

dojo.provide("webcat.Selector");

dojo.require("dojo.dnd.Selector");

// ------------------------------------------------------------------------
/**
 * @author Tony Allevato
 * @version $Id: Selector.js,v 1.1 2010/03/15 16:48:50 aallowat Exp $
 */
dojo.declare("webcat.Selector", dojo.dnd.Selector,
{
    onMouseDown: function(/*Event*/ e)
    {
        var oldSelection = dojo.clone(this.selection);
        this.inherited(arguments);

        var changed = false;
        var empty = dojo.dnd._empty;
        for (var i in this.selection)
        {
            if (i in empty)
            {
                continue;
            }

            if (!this.selection[i] == oldSelection[i])
            {
                changed = true;
                break;
            }
        }

        if (changed)
        {
            this.onSelectionChanged();
        }
    },

    onSelectionChanged: function()
    {
    }
});
