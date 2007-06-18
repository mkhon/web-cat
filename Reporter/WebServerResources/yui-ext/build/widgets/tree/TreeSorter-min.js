/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.tree.TreeSorter=function(_1,_2){Ext.apply(this,_2);_1.on("beforechildrenrendered",this.doSort,this);_1.on("append",this.updateSort,this);_1.on("insert",this.updateSort,this);var _3=this.dir&&this.dir.toLowerCase()=="desc";var p=this.property||"text";var _5=this.sortType;var fs=this.folderSort;var cs=this.caseSensitive===true;this.sortFn=function(n1,n2){if(fs){if(n1.leaf&&!n2.leaf){return 1;}if(!n1.leaf&&n2.leaf){return -1;}}var v1=_5?_5(n1):(cs?n1[p]:n1[p].toUpperCase());var v2=_5?_5(n2):(cs?n2[p]:n2[p].toUpperCase());if(v1<v2){return _3?+1:-1;}else{if(v1>v2){return _3?-1:+1;}else{return 0;}}};};Ext.tree.TreeSorter.prototype={doSort:function(_c){_c.sort(this.sortFn);},compareNodes:function(n1,n2){return (n1.text.toUpperCase()>n2.text.toUpperCase()?1:-1);},updateSort:function(_f,_10){if(_10.childrenRendered){this.doSort.defer(1,this,[_10]);}}};
