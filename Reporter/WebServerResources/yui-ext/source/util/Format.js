/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.util.Format
 * Reusable format functions usable in yui-ext components.
 * @singleton
 */
Ext.util.Format = function(){
    var trimRe = /^\s*(.*)\s*$/;
    return {
        ellipsis : function(value, len){
            if(value && value.length > len){
                return value.substr(0, len-3)+"...";
            }
            return value;
        },
        
        undef : function(value){
            return typeof value != "undefined" ? value : "";
        },
        
        htmlEncode : function(value){
            return !value ? value : String(value).replace(/&/g, "&amp;").replace(/>/g, "&gt;").replace(/</g, "&lt;").replace(/"/g, "&quot;");
        },
        
        trim : function(value){
            return String(value).replace(trimRe, "$1");
        },
        
        substr : function(value, start, length){
            return String(value).substr(start, length);
        },
        
        lowercase : function(value){
            return String(value).toLowerCase();
        },
        
        uppercase : function(value){
            return String(value).toUpperCase();
        },
        
        capitalize : function(value){
            return !value ? value : value.charAt(0).toUpperCase() + value.substr(1).toLowerCase();
        },
        
        call : function(value, fn){
            if(arguments.length > 2){
                var args = Array.prototype.slice.call(arguments, 2);
                args.unshift(value);
                return eval(fn).apply(window, args);
            }else{
                return eval(fn).call(window, value);
            }
        },
        
        usMoney : function(v){
            v = (Math.round((v-0)*100))/100;
            v = (v == Math.floor(v)) ? v + ".00" : ((v*10 == Math.floor(v*10)) ? v + "0" : v);
            return "$" + v ;
        },
        
        date : function(v, format){
            if(!v){
                return "";
            }
            if(!(v instanceof Date)){
                v = new Date(Date.parse(v));
            }
            return v.dateFormat(format || "m/d/Y");
        },

        dateRenderer : function(format){
            return function(v){
                return Ext.util.Format.date(v, format);  
            };
        },

        stripTagsRE : /<\/?[^>]+>/gi,
        
        /**
         * Strips all HTML tags
         * @param {Mixed} s The text
         * @return {String} The stripped text
         */
        stripTags : function(v){
            return !v ? v : String(v).replace(this.stripTagsRE, "");
        }
    };
}();