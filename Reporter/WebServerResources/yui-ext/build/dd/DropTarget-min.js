/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.dd.DropTarget=function(el,_2){this.el=Ext.get(el);Ext.apply(this,_2);if(this.containerScroll){Ext.dd.ScrollManager.register(this.el);}Ext.dd.DropTarget.superclass.constructor.call(this,this.el.dom,this.ddGroup||this.group,{isTarget:true});};Ext.extend(Ext.dd.DropTarget,Ext.dd.DDTarget,{isTarget:true,isNotifyTarget:true,dropAllowed:"x-dd-drop-ok",dropNotAllowed:"x-dd-drop-nodrop",notifyEnter:function(dd,e,_5){if(this.overClass){this.el.addClass(this.overClass);}return this.dropAllowed;},notifyOver:function(dd,e,_8){return this.dropAllowed;},notifyOut:function(dd,e,_b){if(this.overClass){this.el.removeClass(this.overClass);}},notifyDrop:function(dd,e,_e){return false;}});
