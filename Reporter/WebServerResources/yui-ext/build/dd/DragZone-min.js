/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.dd.DragZone=function(el,_2){Ext.dd.DragZone.superclass.constructor.call(this,el,_2);if(this.containerScroll){Ext.dd.ScrollManager.register(this.el);}};Ext.extend(Ext.dd.DragZone,Ext.dd.DragSource,{getDragData:function(e){return Ext.dd.Registry.getHandleFromEvent(e);},onInitDrag:function(e){this.proxy.update(this.dragData.ddel.cloneNode(true));this.onStartDrag(e);return true;},afterRepair:function(){if(Ext.enableFx){Ext.Element.fly(this.dragData.ddel).highlight(this.hlColor||"c3daf9");}this.dragging=false;},getRepairXY:function(e){return Ext.Element.fly(this.dragData.ddel).getXY();}});
