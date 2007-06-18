/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/*
 * This field needs some work. It is only here for backword compatiblity with checkbox grid editor
 */
Ext.form.Checkbox = function(config){
    Ext.form.Checkbox.superclass.constructor.call(this, config);
    this.addEvents({
        check : true
    });
};

Ext.extend(Ext.form.Checkbox, Ext.form.Field,  {
    focusClass : "x-form-check-focus",
    fieldClass: "x-form-field",
    checked: false,
    setSize : function(w, h){
        this.wrap.setSize(w, h);
        this.el.alignTo(this.wrap, 'c-c');
    },
    onRender : function(ct){
        if(!this.el){
            this.defaultAutoCreate = {
                tag: "input", type: 'checkbox',
                autocomplete: "off"
            };
        }
        Ext.form.Checkbox.superclass.onRender.call(this, ct);
        this.wrap = this.el.wrap({cls: "x-form-check-wrap"});
    },

    initValue : Ext.emptyFn,
    
    getValue : function(){
        if(this.rendered){
            return this.el.dom.checked;
        }
        return false;
    },

    setValue : function(v){
        this.checked = (v === true || v === 'true' || v == '1');
        if(this.rendered){
            this.el.dom.checked = this.checked;
        }
    }
});