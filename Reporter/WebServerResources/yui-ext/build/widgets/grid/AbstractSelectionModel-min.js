/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.grid.AbstractSelectionModel=function(){this.locked=false;};Ext.extend(Ext.grid.AbstractSelectionModel,Ext.util.Observable,{init:function(_1){this.grid=_1;this.initEvents();},lock:function(){this.locked=true;},unlock:function(){this.locked=false;},isLocked:function(){return this.locked;}});
