/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.menu.TextItem
 * @extends Ext.menu.BaseItem
 * @constructor
 * @param {String} text
 */
Ext.menu.TextItem = function(text){
    this.text = text;
    Ext.menu.TextItem.superclass.constructor.call(this);
};
Ext.extend(Ext.menu.TextItem, Ext.menu.BaseItem, {
    hideOnClick : false,
    itemCls : "x-menu-text",
    onRender : function(){
        var s = document.createElement("span");
        s.className = this.itemCls;
        s.innerHTML = this.text;
        this.el = s;
        Ext.menu.TextItem.superclass.onRender.apply(this, arguments);
    }
});