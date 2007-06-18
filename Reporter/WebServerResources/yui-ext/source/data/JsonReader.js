/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.data.JsonReader = function(meta, recordType){
    Ext.data.JsonReader.superclass.constructor.call(this, meta, recordType);
};
Ext.extend(Ext.data.JsonReader, Ext.data.DataReader, {
    read : function(response){
        var json = response.responseText;
        var o = eval("("+json+")");
        if(!o) {
            throw {message: "JsonReader.read: Json object not found"};
        }
        return this.readRecords(o);
    },
    
    readRecords : function(o){
        this.jsonData = o;
        var s = this.meta;
    	var sid = s.id;
    	var recordType = this.recordType, fields = recordType.prototype.fields;
    	
    	var totalRecords = 0;
    	if(s.totalProperty){
            var v = parseInt(eval("o." + s.totalProperty), 10);
            if(!isNaN(v)){
                totalRecords = v;
            }
        }
        var records = [];
    	var root = s.root ? eval("o." + s.root) : o;
	    for(var i = 0; i < root.length; i++){
		    var n = root[i];
	        var values = {};
	        var id = (n[sid] !== undefined && n[sid] !== "" ? n[sid] : null);
	        for(var j = 0, jlen = fields.length; j < jlen; j++){
	            var f = fields.items[j];
	            var map = f.mapping || f.name;
	            var v = n[map] !== undefined ? n[map] : f.defaultValue;
	            v = f.convert(v);
	            values[f.name] = v;
	        }
	        var record = new recordType(values, id);
	        record.json = n;
	        records[records.length] = record;
	    }
	    return {
	        records : records,
	        totalRecords : totalRecords || records.length
	    };
    }
});