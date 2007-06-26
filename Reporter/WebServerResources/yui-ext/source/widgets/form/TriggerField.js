/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.form.TriggerField = function(config){
    Ext.form.TriggerField.superclass.constructor.call(this, config);
    this.mimicing = false;
};

Ext.extend(Ext.form.TriggerField, Ext.form.TextField,  {
    defaultAutoCreate : {tag: "input", type: "text", size: "16", autocomplete: "off"},
    customSize : true,
    hideTrigger:false,
    
    setSize : function(w, h){
        if(!this.wrap){
            this.width = w;
            this.height = h;
            return;
        }
        if(w){
            var wrapWidth = w;
            w = w - this.trigger.getWidth();
            Ext.form.TriggerField.superclass.setSize.call(this, w, h);
            this.wrap.setWidth(wrapWidth);
            if(this.onResize){
                this.onResize(wrapWidth, h);
            }
        }else{
            Ext.form.TriggerField.superclass.setSize.call(this, w, h);
            this.wrap.setWidth(this.el.getWidth()+this.trigger.getWidth());
        }
    },

    onRender : function(ct){
        Ext.form.TriggerField.superclass.onRender.call(this, ct);
        this.wrap = this.el.wrap({cls: "x-form-field-wrap"});
        this.trigger = this.wrap.createChild({
            tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger "+this.triggerClass});
        this.trigger.on("click", this.onTriggerClick, this, {preventDefault:true});
        this.trigger.addClassOnOver('x-form-trigger-over');
        this.trigger.addClassOnClick('x-form-trigger-click');
        if(this.hideTrigger){
            this.trigger.setDisplayed(false);
        }
        this.setSize(this.width||'', this.height||'');
    },

    onFocus : function(){
        Ext.form.TriggerField.superclass.onFocus.call(this);
        if(!this.mimicing){
            this.mimicing = true;
            Ext.get(document).on("mousedown", this.mimicBlur, this);
            this.el.on("keydown", this.checkTab, this);
        }
    },

    checkTab : function(e){
        if(e.getKey() == e.TAB){
            this.triggerBlur();
        }
    },

    onBlur : function(){
        // do nothing
    },

    mimicBlur : function(e, t){
        if(!this.wrap.contains(t) && this.validateBlur()){
            this.triggerBlur();
        }
    },

    triggerBlur : function(){
        this.mimicing = false;
        Ext.get(document).un("mousedown", this.mimicBlur);
        this.el.un("keydown", this.checkTab, this);
        Ext.form.TriggerField.superclass.onBlur.call(this);
    },

    validateBlur : function(e, t){
        return true;
    },

    onTriggerClick : Ext.emptyFn
});