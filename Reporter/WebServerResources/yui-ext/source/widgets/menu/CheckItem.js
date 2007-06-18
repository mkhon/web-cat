/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.menu.CheckItem
 * @extends Ext.menu.Item
 * @constructor
 */
Ext.menu.CheckItem = function(config){
    Ext.menu.CheckItem.superclass.constructor.call(this, config);
    this.addEvents({
        "beforecheckchange" : true,
        "checkchange" : true
    });
    if(this.checkHandler){
        this.on('checkchange', this.checkHandler, this.scope);
    }
};
Ext.extend(Ext.menu.CheckItem, Ext.menu.Item, {
    itemCls : "x-menu-item x-menu-check-item",
    groupClass : "x-menu-group-item",
    checked: false,
    ctype: "Ext.menu.CheckItem",
    
    onRender : function(c){
        Ext.menu.CheckItem.superclass.onRender.apply(this, arguments);
        if(this.group){
            this.el.addClass(this.groupClass);
        }
        Ext.menu.MenuMgr.registerCheckable(this);
        if(this.checked){
            this.checked = false;
            this.setChecked(true, true);
        }
    },

    destroy : function(){
        if(this.rendered){
            Ext.menu.MenuMgr.unregisterCheckable(this);
        }
        Ext.menu.CheckItem.superclass.destroy.apply(this, arguments);
    },

    /**
     * Set the checked state of this item
     */
    setChecked : function(state, suppressEvent){
        if(this.checked != state && this.fireEvent("beforecheckchange", this, state) !== false){
            if(this.container){
                this.container[state ? "addClass" : "removeClass"]("x-menu-item-checked");
            }
            this.checked = state;
            if(suppressEvent !== true){
                this.fireEvent("checkchange", this, state);
            }
        }
    },

    handleClick : function(e){
       if(!this.disabled && !(this.checked && this.group)){// disable unselect on radio item
           this.setChecked(!this.checked);
       }
       Ext.menu.CheckItem.superclass.handleClick.apply(this, arguments);
    }
});