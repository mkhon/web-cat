/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.menu.Adapter
 * @extends Ext.menu.BaseItem
 * @constructor
 */
Ext.menu.Adapter = function(component, config){
    Ext.menu.Adapter.superclass.constructor.call(this, config);
    this.component = component;
};
Ext.extend(Ext.menu.Adapter, Ext.menu.BaseItem, {
    canActivate : true,

    onRender : function(container){
        this.component.render(container);
        this.el = this.component.getEl();
    },

    activate : function(){
        if(this.disabled){
            return false;
        }
        this.component.focus();
        this.fireEvent("activate", this);
        return true;
    },

    deactivate : function(){
        this.fireEvent("deactivate", this);
    },

    disable : function(){
        this.component.disable();
        Ext.menu.Adapter.superclass.disable.call(this);
    },

    enable : function(){
        this.component.enable();
        Ext.menu.Adapter.superclass.enable.call(this);
    }
});