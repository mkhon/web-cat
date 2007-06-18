/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.Container = function(config){
    Ext.Container.superclass.constructor.call(this, config);
    this.items = new Ext.util.MixedCollection(false, this.getComponentId);
};

Ext.extend(Ext.Container, Ext.Component, {
    getComponentId : function(comp){
        return comp.id;
    },

    add : Ext.emptyFn,

    remove : Ext.emptyFn,

    insert : Ext.emptyFn
});