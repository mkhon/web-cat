/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.data.MemoryProxy = function(data){
    Ext.data.MemoryProxy.superclass.constructor.call(this);
    this.data = data;
};

Ext.extend(Ext.data.MemoryProxy, Ext.data.DataProxy, {
    load : function(params, reader, callback, scope, arg){
        params = params || {};
        var result;
        try {
            result = reader.readRecords(this.data);
        }catch(e){
            this.fireEvent("loadexception", this, arg, null, e);
            callback.call(scope, null, arg, false);
            return;
        }
        callback.call(scope, result, arg, true);
    },
    
    update : function(params, records){
        
    }
});