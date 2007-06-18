/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.form.Field = function(config){
    Ext.form.Field.superclass.constructor.call(this, config);
    this.addEvents({
        focus : true,
        blur : true,
        specialkey: true,
        change:true,
        invalid : true,
        valid : true
    });
};

Ext.extend(Ext.form.Field, Ext.Component,  {
    invalidClass : "x-form-invalid",
    invalidText : "The value in this field is invalid",
    focusClass : "x-form-focus",
    validationEvent : "keyup",
    validationDelay : 250,
    defaultAutoCreate : {tag: "input", type: "text", size: "20", autocomplete: "off"},
    fieldClass: "x-form-field",
    hasFocus : false,
    msgTarget: 'qtip', // qtip, title, under, or element id
    msgFx : 'normal',

    applyTo : function(target){
        this.target = target;
        this.el = Ext.get(target);
        this.render(this.el.dom.parentNode);
        return this;
    },

    onRender : function(ct){
        if(this.el){
            this.el = Ext.get(this.el);
            if(!this.target){
                ct.dom.appendChild(this.el.dom);
            }
        }else {
            var cfg = typeof this.autoCreate == "object" ?
                      this.autoCreate : this.defaultAutoCreate;
            if(this.id & !cfg.id){
                cfg.id = this.id;
            }
            if(!cfg.name){
                cfg.name = this.name || this.id;
            }
            this.el = ct.createChild(cfg);
        }
        var type = this.el.dom.type;
        if(type){
            if(type == 'password'){
                type = 'text';
            }
            this.el.addClass('x-form-'+type);
        }
        if(!this.customSize && (this.width || this.height)){
            this.setSize(this.width || "", this.height || "");
        }
        if(this.style){
            this.el.applyStyles(this.style);
            delete this.style;
        }
        if(this.readOnly){
            this.el.dom.readOnly = true;
        }

        this.el.addClass([this.fieldClass, this.cls]);
        this.initValue();
    },

    initValue : function(){
        if(this.value !== undefined){
            this.setValue(this.value);
        }else if(this.el.dom.value.length > 0){
            this.setValue(this.el.dom.value);
        }
    },

    afterRender : function(){
        this.initEvents();
    },

    fireKey : function(e){
        if(e.isNavKeyPress()){
            this.fireEvent("specialkey", this, e);
        }
    },

    initEvents : function(){
        this.el.on(Ext.isIE ? "keydown" : "keypress", this.fireKey,  this);
        this.el.on("focus", this.onFocus,  this);
        this.el.on("blur", this.onBlur,  this);
    },

    onFocus : function(){
        if(!Ext.isOpera){ // don't touch in Opera
            this.el.addClass(this.focusClass);
        }
        this.hasFocus = true;
        this.startValue = this.getValue();
        this.fireEvent("focus", this);
    },

    onBlur : function(){
        this.el.removeClass(this.focusClass);
        this.hasFocus = false;
        if(this.validationEvent != "blur"){
            this.validate();
        }
        var v = this.getValue();
        if(v != this.startValue){
            this.fireEvent('change', this, v, this.startValue);
        }
        this.fireEvent("blur", this);
    },

    setSize : function(w, h){
        if(!this.rendered){
            this.width = w;
            this.height = h;
            return;
        }
        if(w){
            this.el.setWidth(w);
        }
        if(h){
            this.el.setHeight(h);
        }
        var h = this.el.dom.offsetHeight; // force browser recalc
    },

    isValid : function(){
        return this.validateValue(this.getValue());
    },

    validate : function(){
        if(this.validateValue(this.getRawValue())){
            this.clearInvalid();
        }
    },

    validateValue : function(value){
        return true;
    },
    
    markInvalid : function(msg){
        if(!this.rendered){ // not rendered
            return;
        }
        this.el.addClass(this.invalidClass);
        msg = msg || this.invalidText;
        switch(this.msgTarget){
            case 'qtip':
                this.el.dom.qtip = msg;
                break;
            case 'title':
                this.el.dom.title = msg;
                break;
            case 'under':
                if(!this.errorEl){
                    var elp = this.el.findParent('.x-form-element', 5, true);
                    this.errorEl = elp.createChild({cls:'x-form-invalid-msg'});
                    this.errorEl.setWidth(elp.getWidth()-20);
                }
                this.errorEl.update(msg);
                Ext.form.Field.msgFx[this.msgFx].show(this.errorEl, this);
                break;
            default:
                var t = Ext.getDom(this.msgTarget);
                t.innerHTML = msg;
                t.style.display = this.msgDisplay;
                break;
        }
        this.fireEvent('invalid', this, msg);
    },

    clearInvalid : function(){
        if(!this.rendered){ // not rendered
            return;
        }
        this.el.removeClass(this.invalidClass);
        switch(this.msgTarget){
            case 'qtip':
                this.el.dom.qtip = '';
                break;
            case 'title':
                this.el.dom.title = '';
                break;
            case 'under':
                if(this.errorEl){
                    var p = this.el.findParent('.x-form-item', 5, true);
                    if(p){
                        p.removeClass('x-form-item-msg');
                        Ext.form.Field.msgFx[this.msgFx].hide(this.errorEl, this);
                    }
                }
                break;
            default:
                var t = Ext.getDom(this.msgTarget);
                t.innerHTML = '';
                t.style.display = 'none';
                break;
        }
        this.fireEvent('valid', this);
    },

    getRawValue : function(){
        return this.el.getValue();
    },

    getValue : function(){
        return this.el.getValue();
    },

    setRawValue : function(v){
        return this.el.dom.value = v;
    },

    setValue : function(v){
        this.value = v;
        if(this.rendered){
            this.el.dom.value = v;
            this.validate();
        }
    }
});


// anything other than normal should be considered experimental
Ext.form.Field.msgFx = {
    normal : {
        show: function(msgEl, f){
            msgEl.setDisplayed('block');
        },

        hide : function(msgEl, f){
            msgEl.setDisplayed(false).update('');
        }
    },

    slide : {
        show: function(msgEl, f){
            msgEl.slideIn('t', {stopFx:true});
        },

        hide : function(msgEl, f){
            msgEl.slideOut('t', {stopFx:true,useDisplay:true});
        }
    },

    slideRight : {
        show: function(msgEl, f){
            msgEl.fixDisplay();
            msgEl.alignTo(f.el, 'tl-tr');
            msgEl.slideIn('l', {stopFx:true});
        },

        hide : function(msgEl, f){
            msgEl.slideOut('l', {stopFx:true,useDisplay:true});
        }
    }
};