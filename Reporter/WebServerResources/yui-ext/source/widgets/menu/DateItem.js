/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.menu.DateItem
 * @extends Ext.menu.Adapter
 * @constructor
 */
Ext.menu.DateItem = function(config){
    Ext.menu.DateItem.superclass.constructor.call(this, new Ext.DatePicker(config), config);
    this.picker = this.component;
    this.addEvents({select: true});
    
    this.picker.on("render", function(picker){
        picker.getEl().swallowEvent("click");
        picker.container.addClass("x-menu-date-item");
    });

    this.picker.on("select", this.onSelect, this);
};

Ext.extend(Ext.menu.DateItem, Ext.menu.Adapter, {
    onSelect : function(picker, date){
        this.fireEvent("select", this, date, picker);
        Ext.menu.DateItem.superclass.handleClick.call(this);
    }
});