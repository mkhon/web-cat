/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

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
        if(value.length < 1){ // if it's blank
             if(this.allowBlank){
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