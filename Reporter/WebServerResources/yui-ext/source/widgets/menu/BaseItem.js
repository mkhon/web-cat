/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.menu.BaseItem
 * @constructor
 * @param {Object} config
 */
Ext.menu.BaseItem = function(config){
    Ext.menu.BaseItem.superclass.constructor.call(this, config);

    this.addEvents({
        click: true,
        activate : true,
        deactivate : true
    });

    if(this.handler){
        this.on("click", this.handler, this.scope, true);
    }
};

Ext.extend(Ext.menu.BaseItem, Ext.Component, {
    canActivate : false,
    activeClass : "x-menu-item-active",
    hideOnClick : true,
    hideDelay : 100,
    ctype: "Ext.menu.BaseItem",
    actionMode : "container",

    render : function(container, parentMenu){
        this.parentMenu = parentMenu;
        Ext.menu.BaseItem.superclass.render.call(this, container);
        this.container.menuItemId = this.id;
    },

    onRender : function(container){
        this.el = Ext.get(this.el);
        container.dom.appendChild(this.el.dom);
    },

    onClick : function(e){
        if(!this.disabled && this.fireEvent("click", this, e) !== false
                && this.parentMenu.fireEvent("itemclick", this, e) !== false){
            this.handleClick(e);
        }else{
            e.stopEvent();
        }
    },

    activate : function(){
        if(this.disabled){
            return false;
        }
        var li = this.container;
        li.addClass(this.activeClass);
        this.region = li.getRegion().adjust(2, 2, -2, -2);
        this.fireEvent("activate", this);
        return true;
    },

    deactivate : function(){
        this.container.removeClass(this.activeClass);
        this.fireEvent("deactivate", this);
    },

    shouldDeactivate : function(e){
        return !this.region || !this.region.contains(e.getPoint());
    },

    handleClick : function(e){
        if(this.hideOnClick){
            this.parentMenu.hide.defer(this.hideDelay, this.parentMenu, [true]);
        }
    },

    expandMenu : function(autoActivate){
        // do nothing
    },

    hideMenu : function(){
        // do nothing
    }
});