/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.data.Field=function(_1){if(typeof _1=="string"){_1={name:_1};}Ext.apply(this,_1);if(!this.type){this.type="auto";}var st=Ext.data.SortTypes;if(typeof this.sortType=="string"){this.sortType=st[this.sortType];}if(!this.sortType){switch(this.type){case "string":this.sortType=st.asUCString;break;case "date":this.sortType=st.asDate;break;default:this.sortType=st.none;}}if(!this.convert){var cv,_4=this.dateFormat;switch(this.type){case "":case "auto":case undefined:cv=function(v){return v;};break;case "string":cv=function(v){return String(v);};break;case "int":cv=function(v){return parseInt(String(v).replace(/[\$,%]/g,""),10);};break;case "float":cv=function(v){return parseFloat(String(v).replace(/[\$,%]/g,""));};break;case "bool":case "boolean":cv=function(v){return v===true||v==="true"||v==1;};break;case "date":cv=function(v){if(!v){return null;}if(v instanceof Date){return v;}if(_4){if(_4=="timestamp"){return new Date(v*1000);}return Date.parseDate(v,_4);}var _b=Date.parse(v);return _b?new Date(_b):null;};break;}this.convert=cv;}};Ext.data.Field.prototype={dateFormat:null,defaultValue:"",mapping:null,sortType:null,sortDir:"ASC"};
