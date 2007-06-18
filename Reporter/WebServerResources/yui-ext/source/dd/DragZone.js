/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.dd.DragZone
 * @extends Ext.dd.Source
 * This class provides a container DD instance that proxies for multiple child node sources.<br />
 * By default, this class requires that draggable child nodes are registered with
 * {@link Ext.dd.Registry}.
 * @cfg {Boolean} containerScroll True to register this container with the Scrollmanager 
 * for auto scrolling during drag operations.
 * @constructor
 * @param {String/HTMLElement/Element} el The container element
 * @param {Object} config
 */
Ext.dd.DragZone = function(el, config){
    Ext.dd.DragZone.superclass.constructor.call(this, el, config);
    if(this.containerScroll){
        Ext.dd.ScrollManager.register(this.el);
    }
};

Ext.extend(Ext.dd.DragZone, Ext.dd.DragSource, {
    /**
     * Called when a mousedown occurs in this container. Looks in {@link Ext.dd.Registry}
     * for a valid target to drag based on the mouse down. Override this method
     * to provide your own lookup logic (e.g. finding a child by class name). Make sure your returned
     * object has a "ddel" attribute (with an HTML Element) for other functions to work.
     * @param {EventObject} e The mouse down event
     * @return {Object} The dragData
     */
    getDragData : function(e){
        return Ext.dd.Registry.getHandleFromEvent(e);
    },
    
    /**
     * Called once drag threshold has been reached to initialize the proxy element. By default, it clones the
     * this.dragData.ddel
     * @param {EventObject} e The current event
     * @return {Boolean} true to continue the drag, false to cancel
     */
    onInitDrag : function(e){
        this.proxy.update(this.dragData.ddel.cloneNode(true));
        this.onStartDrag(e);
        return true;
    },
    
    /**
     * Called after a repair of an invalid drop. By default, highlights this.dragData.ddel 
     */
    afterRepair : function(){
        if(Ext.enableFx){
            Ext.Element.fly(this.dragData.ddel).highlight(this.hlColor || "c3daf9");
        }
        this.dragging = false;
    },

    /**
     * Called before a repair of an invalid drop to get the XY to animate to. By default returns
     * the XY of this.dragData.ddel
     * @param {EventObject} e The mouse up event
     * @return {Array} The xy location (e.g. [100, 200])
     */
    getRepairXY : function(e){
        return Ext.Element.fly(this.dragData.ddel).getXY();  
    }
});