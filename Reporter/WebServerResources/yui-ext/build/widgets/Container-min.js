/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.Container=function(_1){Ext.Container.superclass.constructor.call(this,_1);this.items=new Ext.util.MixedCollection(false,this.getComponentId);};Ext.extend(Ext.Container,Ext.Component,{getComponentId:function(_2){return _2.id;},add:Ext.emptyFn,remove:Ext.emptyFn,insert:Ext.emptyFn});
