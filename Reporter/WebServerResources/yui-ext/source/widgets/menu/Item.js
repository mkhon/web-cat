/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.menu.Item
 * @extends Ext.menu.BaseItem
 * @constructor
 */
Ext.menu.Item = function(config){
    Ext.menu.Item.superclass.constructor.call(this, config);
    if(this.menu){
        this.menu = Ext.menu.MenuMgr.get(this.menu);
    }
};
Ext.extend(Ext.menu.Item, Ext.menu.BaseItem, {
    itemCls : "x-menu-item",
    canActivate : true,
    ctype: "Ext.menu.Item",

    onRender : function(container){
        var el = document.createElement("a");
        el.hideFocus = true;
        el.unselectable = "on";
        el.href = this.href || "#";
        if(this.hrefTarget){
            el.target = this.hrefTarget;
        }
        el.className = this.itemCls + (this.menu ?  " x-menu-item-arrow" : "") + (this.cls ?  " " + this.cls : "");
        el.innerHTML = String.format(
                '<img src="{0}" class="x-menu-item-icon">{1}',
                this.icon || Ext.BLANK_IMAGE_URL, this.text);
        this.el = el;
        Ext.menu.Item.superclass.onRender.call(this, container);
    },

    setText : function(text){
        this.text = text;
        if(this.rendered){
            this.el.update(String.format(
                '<img src="{0}" class="x-menu-item-icon">{1}',
                this.icon || Ext.BLANK_IMAGE_URL, this.text));
            this.parentMenu.autoWidth();
        }
    },

    handleClick : function(e){
        if(!this.href){ // if no link defined, stop the event automatically
            e.stopEvent();
        }
        Ext.menu.Item.superclass.handleClick.apply(this, arguments);
    },

    activate : function(autoExpand){
        if(Ext.menu.Item.superclass.activate.apply(this, arguments)){
            this.focus();
            if(autoExpand){
                this.expandMenu();
            }
        }
        return true;
    },

    shouldDeactivate : function(e){
        if(Ext.menu.Item.superclass.shouldDeactivate.call(this, e)){
            if(this.menu && this.menu.isVisible()){
                return !this.menu.getEl().getRegion().contains(e.getPoint());
            }
            return true;
        }
        return false;
    },

    deactivate : function(){
        Ext.menu.Item.superclass.deactivate.apply(this, arguments);
        this.hideMenu();
    },

    expandMenu : function(autoActivate){
        if(!this.disabled && this.menu){
            if(!this.menu.isVisible()){
                this.menu.show(this.container, this.parentMenu.subMenuAlign || "tl-tr?", this.parentMenu);
            }
            if(autoActivate){
                this.menu.tryActivate(0, 1);
            }
        }
    },

    hideMenu : function(){
        if(this.menu && this.menu.isVisible()){
            this.menu.hide();
        }
    }
});