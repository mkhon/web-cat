/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.data.XmlReader=function(_1,_2){Ext.data.XmlReader.superclass.constructor.call(this,_1,_2);};Ext.extend(Ext.data.XmlReader,Ext.data.DataReader,{read:function(_3){var _4=_3.responseXML;if(!_4){throw {message:"XmlReader.read: XML Document not available"};}return this.readRecords(_4);},readRecords:function(_5){this.xmlData=_5;var _6=_5.documentElement||_5;var q=Ext.DomQuery;var _8=this.recordType,_9=_8.prototype.fields;var _a=this.meta.id;var _b=0;if(this.meta.totalRecords){_b=q.selectNumber(this.meta.totalRecords,_6,0);}var _c=[];var ns=q.select(this.meta.record,_6);for(var i=0,_f=ns.length;i<_f;i++){var n=ns[i];var _11={};var id=_a?q.selectValue(_a,n):undefined;for(var j=0,_14=_9.length;j<_14;j++){var f=_9.items[j];var v=q.selectValue(f.mapping||f.name,n,f.defaultValue);v=f.convert(v);_11[f.name]=v;}var _17=new _8(_11,id);_17.node=n;_c[_c.length]=_17;}return {records:_c,totalRecords:_b||_c.length};}});
