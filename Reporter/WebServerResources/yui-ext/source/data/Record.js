/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.data.Record = function(data, id){
    this.id = (id || id === 0) ? id : ++Ext.data.Record.AUTO_ID;
    this.data = data;
};

Ext.data.Record.create = function(o){
    var f = function(){
        f.superclass.constructor.apply(this, arguments);
    };
    Ext.extend(f, Ext.data.Record);
    var p = f.prototype;
    p.fields = new Ext.util.MixedCollection(false, function(field){
        return field.name;
    });
    for(var i = 0, len = o.length; i < len; i++){
        p.fields.add(new Ext.data.Field(o[i]));
    }
    f.getField = function(name){
        return p.fields.get(name);  
    };
    return f;
};

Ext.data.Record.AUTO_ID = 1000;
Ext.data.Record.EDIT = 'edit';
Ext.data.Record.REJECT = 'reject';
Ext.data.Record.COMMIT = 'commit';

Ext.data.Record.prototype = {
    dirty : false,
    editing : false,
    error: null,
    modified: null,
    
    join : function(store){
        this.store = store;
    },
    
    set : function(name, value){
        if(this.data[name] == value){
            return;
        }
        this.dirty = true;
        if(!this.modified){
            this.modified = {};
        }
        if(typeof this.modified[name] == 'undefined'){
            this.modified[name] = this.data[name];
        }
        this.data[name] = value;
        if(!this.editing){
            this.store.afterEdit(this);
        }       
    },
    
    get : function(name){
        return this.data[name]; 
    },
    
    beginEdit : function(){
        this.editing = true;
        this.modified = {}; 
    },
    
    cancelEdit : function(){
        this.editing = false;
        delete this.modified;
    },
    
    endEdit : function(){
        this.editing = false;
        if(this.dirty){
            this.store.afterEdit(this);
        }
    },
    
    reject : function(){
        var m = this.modified;
        for(var n in m){
            if(typeof m[n] != "function"){
                this.data[n] = m[n];
            }
        }
        this.dirty = false;
        delete this.modified;
        this.editing = false;
        this.store.afterReject(this);
    },
    
    commit : function(){
        this.dirty = false;
        delete this.modified;
        this.editing = false;
        this.store.afterCommit(this);
    },
    
    hasError : function(){
        return this.error != null;
    },
    
    clearError : function(){
        this.error = null;
    }
};