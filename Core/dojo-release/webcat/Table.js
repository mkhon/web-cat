/*==========================================================================*\
 |  $Id: Table.js,v 1.7 2010/11/08 20:16:42 aallowat Exp $
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

dojo.provide("webcat.Table");
dojo.require("webcat.global");

//------------------------------------------------------------------------
/**
 * This is not a true Dojo widget class. It is a utility class for WCTable, in
 * order to simplify the Javascript that must be embedded directly inside the
 * component.
 */
dojo.declare("webcat.Table", null,
{
    //~ Properties ............................................................

    // The content pane that wraps the table.
    contentPane: "",

    // The prefix used for all of the widget IDs in the table.
    idPrefix: "",


    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    constructor: function(/* Object */ args)
    {
        dojo.mixin(this, args);
    },


    // ----------------------------------------------------------
    startup: function()
    {
        this.contentPane = dijit.byId(this.contentPane);
    },


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    _getCheckboxes: function()
    {
        var paneNode = this.contentPane.domNode;
        var selector =
            "input[name='" + this.idPrefix + "_selectionControlGroup']";
        return dojo.query(selector, paneNode);
    },


    // ----------------------------------------------------------
    selectAllRows: function(/*Boolean*/ selected)
    {
        var allSelectionCheckbox = dijit.byId(
                this.idPrefix + '_allSelectionCheckBox');
        this._getCheckboxes().forEach(function(checkbox) {
            dijit.byId(checkbox.id).attr('checked', selected);
        });
    },


    // ----------------------------------------------------------
    rowSelectionChanged: function(/*Integer*/ row, /*Boolean*/ selected)
    {
        var allSelectionCheckbox = dijit.byId(
                this.idPrefix + '_allSelectionCheckBox');
        var allChecked = this._getCheckboxes().every(function(checkbox) {
            return dijit.byId(checkbox.id).attr('checked');
        });
        allSelectionCheckbox.attr('checked', allChecked);
    }
});
