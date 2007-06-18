/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

if(typeof YAHOO=="undefined"){var YAHOO={};}YAHOO.namespace=function(){var a=arguments,o=null,i,j,d;for(i=0;i<a.length;++i){d=a[i].split(".");o=YAHOO;for(j=(d[0]=="YAHOO")?1:0;j<d.length;++j){o[d[j]]=o[d[j]]||{};o=o[d[j]];}}return o;};YAHOO.log=function(_6,_7,_8){var l=YAHOO.widget.Logger;if(l&&l.log){return l.log(_6,_7,_8);}else{return false;}};YAHOO.extend=function(_a,_b,_c){var F=function(){};F.prototype=_b.prototype;_a.prototype=new F();_a.prototype.constructor=_a;_a.superclass=_b.prototype;if(_b.prototype.constructor==Object.prototype.constructor){_b.prototype.constructor=_b;}if(_c){for(var i in _c){_a.prototype[i]=_c[i];}}};YAHOO.augment=function(r,s){var rp=r.prototype,sp=s.prototype,a=arguments,i,p;if(a[2]){for(i=2;i<a.length;++i){rp[a[i]]=sp[a[i]];}}else{for(p in sp){if(!rp[p]){rp[p]=sp[p];}}}};YAHOO.namespace("util","widget","example");
