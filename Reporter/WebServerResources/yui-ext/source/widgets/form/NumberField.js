/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

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
        if(value.length < 1){ // if it"s blank and textfield didn"t flag it then it's valid
             return true;
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
       // this should work but doesn't due to precision error in JS
       // var scale = Math.pow(10, this.decimalPrecision);
       // var fixed = this.decimalPrecisionFcn(value * scale);
       // return fixed / scale;
       //
       // so here's our workaround:
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