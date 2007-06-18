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
    msgTarget: 'qtip',     msgFx : 'normal',

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
        if(!Ext.isOpera){             this.el.addClass(this.focusClass);
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
        var h = this.el.dom.offsetHeight;     },

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
        if(!this.rendered){             return;
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
        if(!this.rendered){             return;
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
Ext.form.TextField = function(config){
    Ext.form.TextField.superclass.constructor.call(this, config);
};

Ext.extend(Ext.form.TextField, Ext.form.Field,  {
    initEvents : function(){
        Ext.form.TextField.superclass.initEvents.call(this);
        this.el.on(this.validationEvent, this.validate, this, {buffer: this.validationDelay});
        if(this.selectOnFocus || this.emptyText){
            this.on("focus", this.preFocus, this);
            if(this.emptyText){
                this.on('blur', this.postBlur, this);
                if(!this.value && this.getRawValue().length < 1){
                    this.setRawValue(this.emptyText);
                    this.el.addClass(this.emptyClass);
                }
            }
        }
        if(this.maskRe || (this.vtype && this.disableKeyFilter !== true && (this.maskRe = Ext.form.VTypes[this.vtype+'Mask']))){
            this.el.on("keypress", this.filterKeys, this);
        }
    },

    preFocus : function(){
        if(this.emptyText){
            if(this.getRawValue() == this.emptyText){
                this.setRawValue('');
            }
            this.el.removeClass(this.emptyClass);
        }
        if(this.selectOnFocus){
            this.el.dom.select();
        }
    },

    postBlur : function(){
        if(this.emptyText && this.getRawValue().length < 1){
            this.setRawValue(this.emptyText);
            this.el.addClass(this.emptyClass);
        }
    },

    filterKeys : function(e){
        var k = e.getKey();
        if(!Ext.isIE && (e.isNavKeyPress() || k == e.BACKSPACE || k == e.DELETE)){
            return;
        }
        var c = e.getCharCode();
        if(!this.maskRe.test(String.fromCharCode(c) || '')){
            e.stopEvent();
        }
    },

    validateValue : function(value){
        if(value.length < 1){              if(this.allowBlank){
                 this.clearInvalid();
                 return true;
             }else{
                 this.markInvalid(this.blankText);
                 return false;
             }
        }
        if(value.length < this.minLength){
            this.markInvalid(String.format(this.minLengthText, this.minLength));
            return false;
        }
        if(value.length > this.maxLength){
            this.markInvalid(String.format(this.maxLengthText, this.maxLength));
            return false;
        }
        if(this.vtype){
            var vt = Ext.form.VTypes;
            if(!vt[this.vtype](value)){
                this.markInvalid(this.vtypeText || vt[this.vtype +'Text']);
                return false;
            }
        }
        if(typeof this.validator == "function"){
            var msg = this.validator(value);
            if(msg !== true){
                this.markInvalid(msg);
                return false;
            }
        }
        if(this.regex && !this.regex.test(value)){
            this.markInvalid(this.regexText);
            return false;
        }
        return true;
    },

    selectText : function(start, end){
        var v = this.getRawValue();
        if(v.length > 0){
            start = start === undefined ? 0 : start;
            end = end === undefined ? v.length : end;
            var d = this.el.dom;
            if(d.setSelectionRange){
                d.setSelectionRange(start, end);
            }else if(d.createTextRange){
                var range = d.createTextRange();
                range.moveStart("character", start);
                range.moveEnd("character", v.length-end);
                range.select();
            }
        }
    },

    vtype : null,
    maskRe : null,
    disableKeyFilter:false,
    allowBlank : true,
    minLength : 0,
    maxLength : Number.MAX_VALUE,
    minLengthText : "The minimum length for this field is {0}",
    maxLengthText : "The maximum length for this field is {0}",
    selectOnFocus : false,
    blankText : "This field is required",
    validator : null,
    regex : null,
    regexText : "",
    emptyText : null,
    emptyClass : 'x-form-empty-field'
});
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
Ext.form.TextArea = function(config){
    Ext.form.TextArea.superclass.constructor.call(this, config);
    this.addEvents({
        autosize : true
    });
};

Ext.extend(Ext.form.TextArea, Ext.form.TextField,  {
    minHeight : 60,
    maxHeight: 1000,
    preventScrollbars: false,

    initEvents : function(){
        Ext.form.TextArea.superclass.initEvents.call(this);
        if(this.grow){
            this.el.on("keyup", this.onKeyUp,  this, {buffer:50});
            this.el.on("click", this.autoSize,  this);
        }
    },

    onRender : function(ct){
        if(!this.el){
            this.defaultAutoCreate = {
                tag: "textarea",
                style:"width:300px;height:60px;",
                autocomplete: "off"
            };
        }
        Ext.form.TextArea.superclass.onRender.call(this, ct);
        if(this.grow){
            this.textSizeEl = Ext.DomHelper.append(document.body, {
                tag: "pre", cls: "x-form-grow-sizer"
            });
            if(this.preventScrollbars){
                this.el.setStyle("overflow", "hidden");
            }
        }
    },

    onKeyUp : function(e){
        if(!e.isNavKeyPress() || e.getKey() == e.ENTER){
            this.autoSize();
        }
    },

    autoSize : function(){
        if(!this.grow || !this.textSizeEl){
            return;
        }
        var el = this.el;
        var v = el.dom.value;
        var ts = this.textSizeEl;
        Ext.fly(ts).setWidth(this.el.getWidth());
        if(v.length < 1){
            v = "&#160;&#160;";
        }else{
            v += "&#160;\n&#160;";
        }
        if(Ext.isIE){
            v = v.replace(/\n/g, '<br />');
        }
        ts.innerHTML = v;
        var h = Math.min(this.maxHeight, Math.max(ts.offsetHeight, this.minHeight));
        this.el.setHeight(h);
        this.fireEvent("autosize", this, h);
    },

    setValue : function(v){
        Ext.form.TextArea.superclass.setValue.call(this, v);
        this.autoSize();
    }
});
Ext.form.NumberField = function(config){
    Ext.form.NumberField.superclass.constructor.call(this, config);
};

Ext.extend(Ext.form.NumberField, Ext.form.TextField,  {
    fieldClass: "x-form-field x-form-num-field",
    
    initEvents : function(){
        Ext.form.NumberField.superclass.initEvents.call(this);
        var allowed = "0123456789";
        if(this.allowDecimals){
            allowed += this.decimalSeparator;
        }
        if(this.allowNegative){
            allowed += "-";
        }
        var keyPress = function(e){
            var k = e.getKey();
            if(!Ext.isIE && (e.isNavKeyPress() || k == e.BACKSPACE || k == e.DELETE)){
                return;
            }
            var c = e.getCharCode();
            if(allowed.indexOf(String.fromCharCode(c)) === -1){
                e.stopEvent();
            }
        };
        this.el.on("keypress", keyPress, this);
    },

    validateValue : function(value){
        if(!Ext.form.NumberField.superclass.validateValue.call(this, value)){
            return false;
        }
        if(value.length < 1){              return true;
        }
        if(!(/\d+/.test(value))){
            this.markInvalid(String.format(this.nanText, value));
            return false;
        }
        var num = this.parseValue(value);
        if(num < this.minValue){
            this.markInvalid(String.format(this.minText, this.minValue));
            return false;
        }
        if(num > this.maxValue){
            this.markInvalid(String.format(this.maxText, this.maxValue));
            return false;
        }
        return true;
    },


    parseValue : function(value){
        return parseFloat(String(value).replace(this.decimalSeparator, "."));
    },

    fixPrecision : function(value){
       if(!this.allowDecimals || this.decimalPrecision == -1 || isNaN(value) || value == 0 || !value){
           return value;
       }
                                                 var scale = Math.pow(10, this.decimalPrecision+1);
       var fixed = this.decimalPrecisionFcn(value * scale);
       fixed = this.decimalPrecisionFcn(fixed/10);
       return fixed / (scale/10);
    },

    allowDecimals : true,
    decimalSeparator : ".",
    decimalPrecision : 2,
    decimalPrecisionFcn : function(v){
        return Math.floor(v);
    },
    allowNegative : true,
    minValue : Number.NEGATIVE_INFINITY,
    maxValue : Number.MAX_VALUE,
    minText : "The minimum value for this field is {0}",
    maxText : "The maximum value for this field is {0}",
    nanText : "{0} is not a valid number"
});

Ext.form.DateField = function(config){
    Ext.form.DateField.superclass.constructor.call(this, config);
    if(typeof this.minValue == "string") this.minValue = this.parseDate(this.minValue);
    if(typeof this.maxValue == "string") this.maxValue = this.parseDate(this.maxValue);
    this.ddMatch = null;
    if(this.disabledDates){
        var dd = this.disabledDates;
        var re = "(?:";
        for(var i = 0; i < dd.length; i++){
            re += dd[i];
            if(i != dd.length-1) re += "|";
        }
        this.ddMatch = new RegExp(re + ")");
    }
};

Ext.extend(Ext.form.DateField, Ext.form.TriggerField,  {
    triggerClass : 'x-form-date-trigger',
    defaultAutoCreate : {tag: "input", type: "text", size: "10", autocomplete: "off"},

    validateValue : function(value){
        value = this.formatDate(value);
        if(!Ext.form.DateField.superclass.validateValue.call(this, value)){
            return false;
        }
        if(value.length < 1){              return true;
        }
        var svalue = value;
        value = this.parseDate(value);
        if(!value){
            this.markInvalid(String.format(this.invalidText, svalue, this.format));
            return false;
        }
        var time = value.getTime();
        if(this.minValue && time < this.minValue.getTime()){
            this.markInvalid(String.format(this.minText, this.formatDate(this.minValue)));
            return false;
        }
        if(this.maxValue && time > this.maxValue.getTime()){
            this.markInvalid(String.format(this.maxText, this.formatDate(this.maxValue)));
            return false;
        }
        if(this.disabledDays){
            var day = value.getDay();
            for(var i = 0; i < this.disabledDays.length; i++) {
            	if(day === this.disabledDays[i]){
            	    this.markInvalid(this.disabledDaysText);
                    return false;
            	}
            }
        }
        var fvalue = this.formatDate(value);
        if(this.ddMatch && this.ddMatch.test(fvalue)){
            this.markInvalid(String.format(this.disabledDatesText, fvalue));
            return false;
        }
        return true;
    },

    validateBlur : function(){
        return !this.menu || !this.menu.isVisible();
    },

    getValue : function(){
        return this.parseDate(Ext.form.DateField.superclass.getValue.call(this)) || "";
    },

    setValue : function(date){
        Ext.form.DateField.superclass.setValue.call(this, this.formatDate(this.parseDate(date)));
    },

    parseDate : function(value){
        return (!value || value instanceof Date) ?
               value : Date.parseDate(value, this.format);
    },

    formatDate : function(date){
        return (!date || !(date instanceof Date)) ?
               date : date.dateFormat(this.format);
    },

    menuListeners : {
        select: function(m, d){
            this.setValue(d);
        },
        show : function(){             this.el.addClass(this.focusClass);
        },
        hide : function(){
            this.focus();
            var ml = this.menuListeners;
            this.menu.un("select", ml.select,  this);
            this.menu.un("show", ml.show,  this);
            this.menu.un("hide", ml.hide,  this);
        }
    },

    onTriggerClick : function(){
        if(this.disabled){
            return;
        }
        if(this.menu == null){
            this.menu = new Ext.menu.DateMenu();
        }
        Ext.apply(this.menu,  {
            minDate : this.minValue,
            maxDate : this.maxValue,
            disabledDatesRE : this.ddMatch,
            disabledDatesText : this.disabledDatesText,
            disabledDays : this.disabledDays,
            disabledDaysText : this.disabledDaysText,
            format : this.format,
            minText : String.format(this.minText, this.formatDate(this.minValue)),
            maxText : String.format(this.maxText, this.formatDate(this.maxValue))
        });
        this.menu.on(Ext.apply({}, this.menuListeners, {
            scope:this
        }));
        this.menu.picker.setValue(this.getValue() || new Date());
        this.menu.show(this.el, "tl-bl?");
    },

    format : "m/d/y",
    disabledDays : null,
    disabledDaysText : "Disabled",
    disabledDates : null,
    disabledDatesText : "Disabled",
    minValue : null,
    maxValue : null,
    minText : "The date in this field must be after {0}",
    maxText : "The date in this field must be before {0}",
    invalidText : "{0} is not a valid date - it must be in the format {1}"
});

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
Ext.form.ComboBox = function(config){
    Ext.form.ComboBox.superclass.constructor.call(this, config);
    this.addEvents({
        'expand' : true,
        'collapse' : true,
        'beforeselect' : true,
        'select' : true,
        
        'beforequery': true
    });
    if(this.transform){
        var s = Ext.getDom(this.transform);
        if(!this.hiddenName){
            this.hiddenName = s.name;
        }
        if(!this.store){
            this.mode = 'local';
            var d = [], opts = s.options;
            for(var i = 0, len = opts.length;i < len; i++){
                var o = opts[i];
                var value = (Ext.isIE ? o.getAttributeNode('value').specified : o.hasAttribute('value')) ? o.value : o.text;
                if(o.selected) {
                    this.value = value;
                }
                d.push([value, o.text]);
            }
            this.store = new Ext.data.SimpleStore({
                'id': 0,
                fields: ['value', 'text'],
                data : d
            });
            this.valueField = 'value';
            this.displayField = 'text';
        }
        s.name = Ext.id();         if(!this.lazyRender){
            this.el = Ext.DomHelper.insertBefore(s, this.autoCreate || this.defaultAutoCreate);
            s.parentNode.removeChild(s);             this.render(this.el.parentNode);
        }else{
            s.parentNode.removeChild(s);         }

    }
    this.selectedIndex = -1;
    if(this.mode == 'local'){
        if(config.queryDelay === undefined){
            this.queryDelay = 10;
        }
        if(config.minChars === undefined){
            this.minChars = 0;
        }
    }
};

Ext.extend(Ext.form.ComboBox, Ext.form.TriggerField, {
    defaultAutoCreate : {tag: "input", type: "text", size: "24", autocomplete: "off"},
    listWidth: undefined,
    displayField: undefined,
    valueField: undefined,
    hiddenName: undefined,
    listClass: '',
    selectedClass: 'x-combo-selected',
    triggerClass : 'x-form-arrow-trigger',
    shadow:'sides',
    listAlign: 'tl-bl',
    maxHeight: 300,
    triggerAction: 'query',     minChars : 4,
    typeAhead: false,
    queryDelay: 500,
    pageSize: 0,
    selectOnFocus:false,
    queryParam: 'query',
    loadingText: 'Loading...',
    resizable: false,
    handleHeight : 8,
    editable: true,
    allQuery: '',
    mode: 'remote',
    minListWidth : 70,
    forceSelection:false,
    typeAheadDelay : 250,
            valueNotFoundText : undefined,

    onRender : function(ct){
        Ext.form.ComboBox.superclass.onRender.call(this, ct);
        if(this.hiddenName){
            this.hiddenField = this.el.insertSibling({tag:'input', type:'hidden', name: this.hiddenName, id: this.hiddenName},
                    'before', true);
            this.hiddenField.value =
                this.hiddenValue !== undefined ? this.hiddenValue : this.value;
        }
        if(Ext.isGecko){
            this.el.dom.setAttribute('autocomplete', 'off');
        }

        var cls = 'x-combo-list';

        this.list = new Ext.Layer({
            shadow: this.shadow, cls: [cls, this.listClass].join(' '), constrain:false
        });

        this.list.setWidth(this.listWidth || this.wrap.getWidth());
        this.list.swallowEvent('mousewheel');
        this.assetHeight = 0;

        if(this.title){
            this.header = this.list.createChild({cls:cls+'-hd', html: this.title});
            this.assetHeight += this.header.getHeight();
        }

        this.innerList = this.list.createChild({cls:cls+'-inner'});
        this.innerList.on('mouseover', this.onViewOver, this);
        this.innerList.on('mousemove', this.onViewMove, this);

        if(this.pageSize){
            this.footer = this.list.createChild({cls:cls+'-ft'});
            this.pageTb = new Ext.PagingToolbar(this.footer, this.store,
                    {pageSize: this.pageSize});
            this.assetHeight += this.footer.getHeight();
        }

        if(!this.tpl){
            this.tpl = '<div class="'+cls+'-item">{' + this.displayField + '}</div>';
        }

        this.view = new Ext.View(this.innerList, this.tpl, {
            singleSelect:true, store: this.store, selectedClass: this.selectedClass
        });

        this.view.on('click', this.onViewClick, this);

        this.store.on('beforeload', this.onBeforeLoad, this);
        this.store.on('load', this.onLoad, this);
        this.store.on('loadexception', this.collapse, this);

        if(this.resizable){
            this.resizer = new Ext.Resizable(this.list,  {
               pinned:true, handles:'se'
            });
            this.resizer.on('resize', function(r, w, h){
                this.maxHeight = h-this.handleHeight-this.list.getFrameWidth('tb')-this.assetHeight;
                this.listWidth = w;
                this.restrictHeight();
            }, this);
            this[this.pageSize?'footer':'innerList'].setStyle('margin-bottom', this.handleHeight+'px');
        }
        if(!this.editable){
            this.editable = true;
            this.setEditable(false);
        }
    },

    initEvents : function(){
        Ext.form.ComboBox.superclass.initEvents.call(this);

        this.keyNav = new Ext.KeyNav(this.el, {
            "up" : function(e){
                this.inKeyMode = true;
                this.selectPrev();
            },

            "down" : function(e){
                if(!this.isExpanded()){
                    this.onTriggerClick();
                }else{
                    this.inKeyMode = true;
                    this.selectNext();
                }
            },

            "enter" : function(e){
                this.onViewClick();
                return true;
            },

            "esc" : function(e){
                this.collapse();
            },

            "tab" : function(e){
                this.onViewClick(false);
                return true;
            },

            scope : this,

            doRelay : function(foo, bar, hname){
                if(hname == 'down' || this.scope.isExpanded()){
                   return Ext.KeyNav.prototype.doRelay.apply(this, arguments);
                }
                return true;
            }
        });
        this.queryDelay = Math.max(this.queryDelay || 10,
                this.mode == 'local' ? 10 : 250);
        this.dqTask = new Ext.util.DelayedTask(this.initQuery, this);
        if(this.typeAhead){
            this.taTask = new Ext.util.DelayedTask(this.onTypeAhead, this);
        }
        if(this.editable !== false){
            this.el.on("keyup", this.onKeyUp, this);
        }
        if(this.forceSelection){
            this.on('blur', this.doForce, this);
        }
    },

    fireKey : function(e){
        if(e.isNavKeyPress() && !this.list.isVisible()){
            this.fireEvent("specialkey", this, e);
        }
    },

    onResize: function(w, h){
        if(this.list && this.listWidth === undefined){
            this.list.setWidth(Math.max(w, this.minListWidth));
        }
    },
    
    setEditable : function(value){
        if(value == this.editable){
            return;
        }
        if(!value){
            this.el.dom.setAttribute('readOnly', true);
            this.el.on('mousedown', this.onTriggerClick,  this);
            this.el.addClass('x-combo-noedit');
        }else{
            this.el.dom.setAttribute('readOnly', false);
            this.el.un('mousedown', this.onTriggerClick,  this);
            this.el.removeClass('x-combo-noedit');
        }
    },

    onBeforeLoad : function(){
        if(!this.hasFocus){
            return;
        }
        this.innerList.update(this.loadingText ?
               '<div class="loading-indicator">'+this.loadingText+'</div>' : '');
        this.restrictHeight();
        this.selectedIndex = -1;
    },

    onLoad : function(){
        if(!this.hasFocus){
            return;
        }
        if(this.store.getCount() > 0){
            this.expand();
            this.restrictHeight();
            if(this.listAlign.indexOf('?') != -1){                 this.list.alignTo(this.el, this.listAlign);
            }
            if(this.lastQuery == this.allQuery){
                if(this.editable){
                    this.el.dom.select();
                }
                if(!this.selectByValue(this.value, true)){
                    this.select(0, true);
                }
            }else{
                this.selectNext();
                if(this.typeAhead && this.lastKey != Ext.EventObject.BACKSPACE && this.lastKey != Ext.EventObject.DELETE){
                    this.taTask.delay(this.typeAheadDelay);
                }
            }
        }else{
            this.onEmptyResults();
        }
            },

    onTypeAhead : function(){
        if(this.store.getCount() > 0){
            var r = this.store.getAt(0);
            var newValue = r.data[this.displayField];
            var len = newValue.length;
            var selStart = this.getRawValue().length;
            if(selStart != len){
                this.setRawValue(newValue);
                this.selectText(selStart, newValue.length);
            }
        }
    },

    onSelect : function(record, index){
        if(this.fireEvent('beforeselect', this, record, index) !== false){
            this.setValue(record.data[this.valueField || this.displayField]);
            this.collapse();
            this.fireEvent('select', this, record, index);
        }
    },

    getValue : function(){
        if(this.valueField){
            return this.value;
        }else{
            return Ext.form.ComboBox.superclass.getValue.call(this);
        }
    },

    setValue : function(v){
        var text = v;
        if(this.valueField){
            var r = this.findRecord(this.valueField, v);
            if(r){
                text = r.data[this.displayField];
            }else if(this.valueNotFoundText){
                text = this.valueNotFoundText;
            }
        }
        this.lastSelectionText = text;
        if(this.hiddenField){
            this.hiddenField.value = v;
        }
        Ext.form.ComboBox.superclass.setValue.call(this, text);
        this.value = v;
    },

    findRecord : function(prop, value){
        var record;
        if(this.store.getCount() > 0){
            this.store.each(function(r){
                if(r.data[prop] == value){
                    record = r;
                    return false;
                }
            });
        }
        return record;
    },

    onViewMove : function(e, t){
        this.inKeyMode = false;
    },

    onViewOver : function(e, t){
        if(this.inKeyMode){             return;
        }
        var item = this.view.findItemFromChild(t);
        if(item){
            var index = this.view.indexOf(item);
            this.select(index, false);
        }
    },

    onViewClick : function(doFocus){
        var index = this.view.getSelectedIndexes()[0];
        var r = this.store.getAt(index);
        if(r){
            this.onSelect(r, index);
        }
        if(doFocus !== false){
            this.el.focus();
        }
    },

    restrictHeight : function(){
        this.innerList.dom.style.height = '';
        var inner = this.innerList.dom;
        var h = Math.max(inner.clientHeight, inner.offsetHeight, inner.scrollHeight);
        this.innerList.setHeight(h < this.maxHeight ? 'auto' : this.maxHeight);
                    this.list.setHeight(this.innerList.getHeight()+this.list.getFrameWidth('tb')+(this.resizable?this.handleHeight:0)+this.assetHeight);
                this.list.sync();
    },

    onEmptyResults : function(){
        this.collapse();
    },

    isExpanded : function(){
        return this.list.isVisible();
    },

    selectByValue : function(v, scrollIntoView){
        if(this.value !== undefined && this.value !== null){
            var r = this.findRecord(this.valueField || this.displayField, v);
            if(r){
                this.select(this.store.indexOf(r), true);
                return true;
            }
        }
        return false;
    },

    select : function(index, scrollIntoView){
        this.selectedIndex = index;
        this.view.select(index);
        if(scrollIntoView !== false){
            var el = this.view.getNode(index);
            if(el){
                this.innerList.scrollChildIntoView(el);
            }
        }
    },

    selectNext : function(){
        var ct = this.store.getCount();
        if(ct > 0){
            if(this.selectedIndex == -1){
                this.select(0);
            }else if(this.selectedIndex < ct-1){
                this.select(this.selectedIndex+1);
            }
        }
    },

    selectPrev : function(){
        var ct = this.store.getCount();
        if(ct > 0){
            if(this.selectedIndex == -1){
                this.select(0);
            }else if(this.selectedIndex != 0){
                this.select(this.selectedIndex-1);
            }
        }
    },

    onKeyUp : function(e){
        if(!e.isSpecialKey()){
            this.lastKey = e.getKey();
            this.dqTask.delay(this.queryDelay);
        }
    },

    validateBlur : function(){
        return !this.list || !this.list.isVisible();   
    },

    initQuery : function(){
        this.doQuery(this.getRawValue());
    },

    doForce : function(){
        if(this.el.dom.value.length > 0){
            this.el.dom.value =
                this.lastSelectionText === undefined ? '' : this.lastSelectionText;
        }
    },

    doQuery : function(q, forceAll){
        if(q === undefined || q === null){
            q = '';
        }
        var qe = {
            query: q,
            forceAll: forceAll,
            combo: this,
            cancel:false
        };
        if(this.fireEvent('beforequery', qe)===false || qe.cancel){
            return false;
        }
        q = qe.query;
        forceAll = qe.forceAll;
        if(forceAll === true || (q.length >= this.minChars)){
            if(this.lastQuery != q){
                this.lastQuery = q;
                if(this.mode == 'local'){
                    this.selectedIndex = -1;
                    if(forceAll){
                        this.store.clearFilter();
                    }else{
                        this.store.filter(this.displayField, q);
                    }
                    this.onLoad();
                }else{
                    this.store.baseParams[this.queryParam] = q;
                    this.store.load({
                        params: this.getParams(q)
                    });
                    this.expand();
                }
            }else{
                this.onLoad();   
            }
        }
    },

    getParams : function(q){
        var p = {};
                if(this.pageSize){
            p.start = 0;
            p.limit = this.pageSize;
        }
        return p;
    },

    collapse : function(){
        if(!this.isExpanded()){
            return;
        }
        this.list.hide();
        Ext.get(document).un('mousedown', this.collapseIf, this);
        this.fireEvent('collapse', this);
    },

    collapseIf : function(e){
        if(!e.within(this.wrap) && !e.within(this.list)){
            this.collapse();
        }
    },

    expand : function(){
        if(this.isExpanded()){
            return;
        }
        this.list.alignTo(this.el, this.listAlign);
        this.list.show();
        Ext.get(document).on('mousedown', this.collapseIf, this);
        this.fireEvent('expand', this);
    },

    onTriggerClick : function(){
        if(this.disabled){
            return;
        }
        if(this.isExpanded()){
            this.collapse();
            this.el.focus();
        }else{
            this.hasFocus = true;
            this.doQuery(this.triggerAction == 'all' ?
                     this.doQuery(this.allQuery, true) : this.doQuery(this.getRawValue()));
            this.el.focus();
        }
    }
});
Ext.Editor = function(field, config){
    Ext.Editor.superclass.constructor.call(this, config);
    this.field = field;
    this.addEvents({
        "beforestartedit" : true,
        "startedit" : true,
        "beforecomplete" : true,
        "complete" : true,
        "specialkey" : true
    });
};

Ext.extend(Ext.Editor, Ext.Component, {
    value : "",
    alignment: "c-c?",
    shadow : "frame",
    updateEl : false,
    onRender : function(ct){
        this.el = new Ext.Layer({
            shadow: this.shadow,
            cls: "x-editor",
            parentEl : ct,
            shim : this.shim,
            shadowOffset:3,
            id: this.id
        });
        this.el.setStyle("overflow", Ext.isGecko ? "auto" : "hidden");
        this.field.render(this.el);
        if(Ext.isGecko){
            this.field.el.dom.setAttribute('autocomplete', 'off');
        }
        this.field.show();
        this.field.on("blur", this.onBlur, this);
        this.relayEvents(this.field,  ["specialkey"]);
        if(this.field.grow){
            this.field.on("autosize", this.el.sync,  this.el, {delay:1});
        }
    },

    startEdit : function(el, value){
        if(this.editing){
            this.completeEdit();
        }
        this.boundEl = Ext.get(el);
        var v = value !== undefined ? value : this.boundEl.dom.innerHTML;
        if(!this.rendered){
            this.render(this.parentEl || document.body);
        }
        if(this.fireEvent("beforestartedit", this, this.boundEl, v) === false){
            return;
        }
        this.startValue = v;
        this.field.setValue(v);
        if(this.autoSize){
            var sz = this.boundEl.getSize();
            switch(this.autoSize){
                case "width":
                this.setSize(sz.width,  "");
                break;
                case "height":
                this.setSize("",  sz.height);
                break;
                default:
                this.setSize(sz.width,  sz.height);
            }
        }
        this.el.alignTo(this.boundEl, this.alignment);
        this.editing = true;
        if(Ext.QuickTips){
            Ext.QuickTips.disable();
        }
        this.show();
    },

    setSize : function(w, h){
        this.field.setSize(w, h);
        if(this.el){
            this.el.sync();
        }
    },

    realign : function(){
        this.el.alignTo(this.boundEl, this.alignment);
    },

    completeEdit : function(remainVisible){
        if(!this.editing){
            return;
        }
        var v = this.getValue();
        if(this.revertInvalid !== false && !this.field.isValid()){
            v = this.startValue;
            this.cancelEdit(true);
        }
        if(String(v) == String(this.startValue) && this.ignoreNoChange){
            this.editing = false;
            this.hide();
        }
        if(this.fireEvent("beforecomplete", this, v, this.startValue) !== false){
            this.editing = false;
            if(this.updateEl && this.boundEl){
                this.boundEl.update(v);
            }
            if(remainVisible !== true){
                this.hide();
            }
            this.fireEvent("complete", this, v, this.startValue);
        }
    },

    onShow : function(){
        this.el.show();
        if(this.hideEl !== false){
            this.boundEl.hide();
        }
        this.field.show();
        this.field.focus();
        this.fireEvent("startedit", this.boundEl, this.startValue);
    },

    cancelEdit : function(remainVisible){
        if(this.editing){
            this.setValue(this.startValue);
            if(remainVisible !== true){
                this.hide();
            }
        }
    },

    onBlur : function(){
        if(this.allowBlur !== true && this.editing){
            this.completeEdit();
        }
    },

    onHide : function(){
        if(this.editing){
            this.completeEdit();
            return;
        }
        this.field.blur();
        this.el.hide();
        if(this.hideEl !== false){
            this.boundEl.show();
        }
        if(Ext.QuickTips){
            Ext.QuickTips.enable();
        }
    },

    setValue : function(v){
        this.field.setValue(v);
    },

    getValue : function(){
        return this.field.getValue();
    }
});
Ext.form.VTypes = function(){
        var alpha = /^[a-zA-Z_]+$/;
    var alphanum = /^[a-zA-Z0-9_]+$/;
    var email = /^([\w]+)(.[\w]+)*@([\w]+)(.[\w]{2,3}){1,2}$/;
    var url = /(((https?)|(ftp)):\/\/([\-\w]+\.)+\w{2,3}(\/[%\-\w]+(\.\w{2,})?)*(([\w\-\.\?\\\/+@&#;`~=%!]*)(\.\w{2,})?)*\/?)/i;

        return {
        'email' : function(v){
            return email.test(v);
        },
        'emailText' : 'This field should be an e-mail address in the format "user@domain.com"',
        'emailMask' : /[a-z0-9_\.\-@]/i,

        'url' : function(v){
            return url.test(v);
        },
        'urlText' : 'This field should be a URL in the format "http:/'+'/www.domain.com"',
        

        'alpha' : function(v){
            return alpha.test(v);
        },
        'alphaText' : 'This field should only contain letters and _',
        'alphaMask' : /[a-z_]/i,

        'alphanum' : function(v){
            return alphanum.test(v);
        },
        'alphanumText' : 'This field should only contain letters, numbers and _',
        'alphanumMask' : /[a-z0-9_]/i
    };
}();
