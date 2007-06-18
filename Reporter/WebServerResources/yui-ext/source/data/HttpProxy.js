/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.data.HttpProxy = function(conn){
    Ext.data.HttpProxy.superclass.constructor.call(this);
    // is conn a conn config or a real conn?
    this.conn = conn.events ? conn : new Ext.data.Connection(conn);
};

Ext.extend(Ext.data.HttpProxy, Ext.data.DataProxy, {
    getConnection : function(){
        return this.conn;
    },
    
    load : function(params, reader, callback, scope, arg){
        if(this.fireEvent("beforeload", this, params) !== false){
            this.conn.request({
                params : params || {}, 
                request: {
                    callback : callback,
                    scope : scope,
                    arg : arg
                },
                reader: reader,
                callback : this.loadResponse,
                scope: this
            });
        }else{
            callback.call(scope||this, null, arg, false);
        }
    },
    
    loadResponse : function(o, success, response){
        if(!success){
            this.fireEvent("loadexception", this, o, response);
            o.request.callback.call(o.request.scope, null, o.request.arg, false);
            return;
        }
        var result;
        try {
            result = o.reader.read(response);
        }catch(e){
            this.fireEvent("loadexception", this, o, response, e);
            o.request.callback.call(o.request.scope, null, o.request.arg, false);
            return;
        }
        o.request.callback.call(o.request.scope, result, o.request.arg, true);
    },
    
    update : function(dataSet){
        
    },
    
    updateResponse : function(dataSet){
        
    }
});