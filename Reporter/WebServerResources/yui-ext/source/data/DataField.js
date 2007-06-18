/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.data.Field = function(config){
    if(typeof config == "string"){
        config = {name: config};
    }
    Ext.apply(this, config);
    
    if(!this.type){
        this.type = "auto";
    }
    
    var st = Ext.data.SortTypes;
    // named sortTypes are supported, here we look them up
    if(typeof this.sortType == "string"){
        this.sortType = st[this.sortType];
    }
    
    // set default sortType for strings and dates
    if(!this.sortType){
        switch(this.type){
            case "string":
                this.sortType = st.asUCString;
                break;
            case "date":
                this.sortType = st.asDate;
                break;
            default:
                this.sortType = st.none;
        }
    }
    
    // prebuilt conversion function for this field, instead of 
    // switching every time we're reading a value
    if(!this.convert){
        var cv, dateFormat = this.dateFormat;
        switch(this.type){
            case "":
            case "auto":
            case undefined:
                cv = function(v){ return v; };
                break;
            case "string":
                cv = function(v){ return String(v); };
                break;
            case "int":
                cv = function(v){ return parseInt(String(v).replace(/[\$,%]/g, ""), 10); };
                break;
            case "float":
                cv = function(v){ return parseFloat(String(v).replace(/[\$,%]/g, "")); };
                break;
            case "bool":
            case "boolean":
                cv = function(v){ return v === true || v === "true" || v == 1; };
                break;
            case "date":
                cv = function(v){
                    if(!v){
                        return null;
                    }
                    if(v instanceof Date){
                        return v;
                    }
                    if(dateFormat){
                        if(dateFormat == "timestamp"){
                            return new Date(v*1000);
                        }
                        return Date.parseDate(v, dateFormat);
                    }
                    var parsed = Date.parse(v);
                    return parsed ? new Date(parsed) : null;
                };
             break;
            
        }
        this.convert = cv;
    }
};

Ext.data.Field.prototype = {
    dateFormat: null,
    defaultValue: "",
    mapping: null,
    sortType : null,
    sortDir : "ASC"
};