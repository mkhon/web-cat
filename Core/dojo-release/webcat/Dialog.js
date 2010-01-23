/*==========================================================================*\
 |  $Id: Dialog.js,v 1.2 2010/01/23 02:32:41 aallowat Exp $
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

dojo.provide("webcat.Dialog");

dojo.require("dijit.Dialog");
dojo.require("webcat.ContentPane");

//------------------------------------------------------------------------
/**
 * A dialog that inherits from webcat.ContentPane instead of
 * dijit.layout.ContentPane.
 *
 * @author Tony Allevato
 * @version $Id: Dialog.js,v 1.2 2010/01/23 02:32:41 aallowat Exp $
 */
dojo.declare(
    "webcat.Dialog",
    [webcat.ContentPane, dijit._DialogBase],
    {}
);
