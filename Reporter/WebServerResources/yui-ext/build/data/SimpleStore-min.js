/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.data.SimpleStore=function(_1){Ext.data.SimpleStore.superclass.constructor.call(this,{reader:new Ext.data.ArrayReader({id:_1.id},Ext.data.Record.create(_1.fields)),proxy:new Ext.data.MemoryProxy(_1.data)});this.load();};Ext.extend(Ext.data.SimpleStore,Ext.data.Store);
