/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.data.JsonReader=function(_1,_2){Ext.data.JsonReader.superclass.constructor.call(this,_1,_2);};Ext.extend(Ext.data.JsonReader,Ext.data.DataReader,{read:function(_3){var _4=_3.responseText;var o=eval("("+_4+")");if(!o){throw {message:"JsonReader.read: Json object not found"};}return this.readRecords(o);},readRecords:function(o){this.jsonData=o;var s=this.meta;var _8=s.id;var _9=this.recordType,_a=_9.prototype.fields;var _b=0;if(s.totalProperty){var v=parseInt(eval("o."+s.totalProperty),10);if(!isNaN(v)){_b=v;}}var _d=[];var _e=s.root?eval("o."+s.root):o;for(var i=0;i<_e.length;i++){var n=_e[i];var _11={};var id=(n[_8]!==undefined&&n[_8]!==""?n[_8]:null);for(var j=0,_14=_a.length;j<_14;j++){var f=_a.items[j];var map=f.mapping||f.name;var v=n[map]!==undefined?n[map]:f.defaultValue;v=f.convert(v);_11[f.name]=v;}var _17=new _9(_11,id);_17.json=n;_d[_d.length]=_17;}return {records:_d,totalRecords:_b||_d.length};}});
