/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.ComponentMgr = function(){
    var all = new Ext.util.MixedCollection();

    return {
        register : function(c){
            all.add(c);
        },

        unregister : function(c){
            all.remove(c);
        },

        get : function(id){
            return all.get(id);
        },

        onAvailable : function(id, fn, scope){
            all.on("add", function(index, o){
                if(o.id == id){
                    fn.call(scope || o, o);
                    all.un("add", fn, scope);
                }
            });
        }
    };
}();

Ext.Component = function(config){
    config = config || {};
    if(config.tagName || config.dom || typeof config == "string"){ // element object
        config = {el: config, id: config.id || config};
    }
    Ext.apply(this, config);
    this.addEvents({
        disable : true,
        enable : true,
        beforeshow : true,
        show : true,
        beforehide : true,
        hide : true,
        beforerender : true,
        render : true,
        beforedestroy : true,
        destroy : true
    });
    if(!this.id){
        this.id = "ext-comp-" + (++Ext.Component.AUTO_ID);
    }
    Ext.ComponentMgr.register(this);
};

Ext.Component.AUTO_ID = 1000;

Ext.extend(Ext.Component, Ext.util.Observable, {
    /**
     * true if this component is hidden. Read-only.
     */
    hidden : false,
    /**
     * true if this component is disabled. Read-only.
     */
    disabled : false,
    /**
     * CSS class added to the component when it is disabled.
     */
    disabledClass : "x-item-disabled",
    /**
     * true if this component has been rendered. Read-only.
     */
    rendered : false,

    ctype : "Ext.Component",

    actionMode : "el",

    getActionEl : function(){
        return this[this.actionMode];
    },

    render : function(container){
        if(!this.rendered && this.fireEvent("beforerender", this) !== false){
            this.container = Ext.get(container);
            this.rendered = true;
            this.onRender(this.container);
            if(this.cls){
                this.el.addClass(this.cls);
                delete this.cls;
            }
            this.fireEvent("render", this);
            if(this.hidden){
                this.hide();
            }
            if(this.disabled){
                this.disable();
            }
            this.afterRender(this.container);
        }
    },

    // default function is not really useful
    onRender : function(ct){
        this.el = Ext.get(this.el);
        ct.dom.appendChild(this.el.dom);
    },

    afterRender : Ext.emptyFn,

    destroy : function(){
        if(this.fireEvent("beforedestroy", this) !== false){
            this.purgeListeners();
            if(this.rendered){
                this.el.removeAllListeners();
                this.el.remove();
                if(this.actionMode == "container"){
                    this.container.remove();
                }
            }
            Ext.ComponentMgr.unregister(this);
            this.fireEvent("destroy", this);
        }
    },

    getEl : function(){
        return this.el;
    },

/**
 * Try to focus this component
 */
    focus : function(selectText){
        if(this.rendered){
            this.el.focus();
            if(selectText === true){
                this.el.dom.select();
            }
        }
    },

    blur : function(){
        if(this.rendered){
            this.el.blur();
        }
    },
/**
 * Disable this component
 */
    disable : function(){
        if(this.rendered){
            this.getActionEl().addClass(this.disabledClass);
            this.el.dom.disabled = true;
        }
        this.disabled = true;
        this.fireEvent("disable", this);
    },

/**
 * Enable this component
 */
    enable : function(){
        if(this.rendered){
            this.getActionEl().removeClass(this.disabledClass);
            this.el.dom.disabled = false;
        }
        this.disabled = false;
        this.fireEvent("enable", this);
    },

    setDisabled : function(disabled){
        this[disabled ? "disable" : "enable"]();
    },

/**
 * Show this component
 */
    show: function(){
        if(this.fireEvent("beforeshow", this) !== false){
            this.hidden = false;
            if(this.rendered){
                this.onShow();
            }
            this.fireEvent("show", this);
        }
    },

    onShow : function(){
        var st = this.getActionEl().dom.style;
        st.display = "";
        st.visibility = "visible";
    },

/**
 * Hide this component
 */
    hide: function(){
        if(this.fireEvent("beforehide", this) !== false){
            this.hidden = true;
            if(this.rendered){
                this.onHide();
            }
            this.fireEvent("hide", this);
        }
    },

    onHide : function(){
        this.getActionEl().dom.style.display = "none";
    },

    setVisible: function(visible){
        if(visible) {
            this.show();
        }else{
            this.hide();
        }
    }
});