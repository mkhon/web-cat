/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.data.DataReader = function(meta, recordType){
    this.meta = meta;
    this.recordType = recordType instanceof Array ? 
        Ext.data.Record.create(recordType) : recordType;
};

Ext.data.DataReader.prototype = {
    
};