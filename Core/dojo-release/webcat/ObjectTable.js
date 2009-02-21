/*==========================================================================*\
 |  $Id: ObjectTable.js,v 1.1 2009/02/20 02:27:21 aallowat Exp $
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

dojo.provide("webcat.ObjectTable");

// ------------------------------------------------------------------------
/**
 * This is not a true Dojo widget class. It is a wrapper class that wraps an
 * instance of the ObjectTable component based on its AjaxProxy and JavaScript
 * id, and provides functions that communicate back to the server through the
 * proxy.
 */
dojo.declare("webcat.ObjectTable", null,
{
	//~ Constructor ...........................................................

	// ----------------------------------------------------------
	constructor: function(proxy, idPrefix, multiSelect)
	{
		this.proxy = proxy;
		this.idPrefix = idPrefix;
		this.multiSelect = multiSelect;
		
		this.selectionCheckboxName = idPrefix + "_selectionState";
		this.allSelectionCheckboxID = idPrefix + "_allSelectionState";
		this.updateContainerID = idPrefix + "_updateContainer";
	},
	

	//~ Methods ...............................................................

	// ----------------------------------------------------------
	selectAllObjectsInBatch: function(state)
	{
		var checkboxes = document.getElementsByName(
			this.selectionCheckboxName);

		for (var i = 0; i < checkboxes.length; i++)
			checkboxes[i].checked = state;
	
		this.proxy.selectAllObjectsInBatch(
			function() { }, state);
	},
	

	// ----------------------------------------------------------
	selectObjectAtIndexInBatch: function(index, state)
	{
		if (this.multiSelect)
		{
			var allChecked = true;
			var checkboxes = document.getElementsByName(
				this.selectionCheckboxName);
				
			for (var i = 0; i < checkboxes.length; i++)
			{
				if (!checkboxes[i].checked)
				{
					allChecked = false;
					break;
				}
			}
		
			var checkbox = dojo.byId(this.allSelectionCheckboxID);
			checkbox.checked = allChecked;
		}

		this.proxy.selectObjectAtIndexInBatch(
			function() { }, index, state);
	},
	

	// ----------------------------------------------------------
	selectOnlyObjectAtIndexInBatch: function(index, options)
	{
		options = typeof(options) != 'undefined' ? options : {};

		if (this.multiSelect)
		{
			var checkboxes = document.getElementsByName(
				this.selectionCheckboxName);
	
			for (var i = 0; i < checkboxes.length; i++)
				checkboxes[i].checked = (i == index);

			var checkbox = dojo.byId(this.allSelectionCheckboxID);
			checkbox.checked = false;
		}

		if (options.synchronous)
		{
			this.proxy.selectOnlyObjectAtIndexInBatch(index);
		}
		else
		{
			this.proxy.selectOnlyObjectAtIndexInBatch(function() { }, index);
		}
	},


	// ----------------------------------------------------------
	performActionOnObjectAtIndexInBatch: function(index, columnIndex)
	{
		if (this.multiSelect)
		{
			var checkboxes = document.getElementsByName(
				this.selectionCheckboxName);
	
			for (var i = 0; i < checkboxes.length; i++)
				checkboxes[i].checked = (i == index);
		}

		var checkbox = dojo.byId(this.allSelectionCheckboxID);
		checkbox.checked = false;
	
		this.proxy.performActionOnObjectAtIndexInBatch(
			this._updateTableGenerator(), index, columnIndex);
	},
	

	// ----------------------------------------------------------
	changeSortOrdering: function(index)
	{
		this.proxy.changeSortOrdering(
			this._updateTableGenerator(), index);
	},
	

	// ----------------------------------------------------------
	changeBatchSize: function(size)
	{
		this.proxy.changeBatchSize(
			this._updateTableGenerator(), size);
	},
	

	// ----------------------------------------------------------
	_updateTableGenerator: function()
	{
		var ucid = this.updateContainerID;
		return function() {
			eval("dijit.byId('" + ucid + "').refresh();");
		};
	}
});