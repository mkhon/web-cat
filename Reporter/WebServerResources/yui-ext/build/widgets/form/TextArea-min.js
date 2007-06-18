/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.form.TextArea=function(_1){Ext.form.TextArea.superclass.constructor.call(this,_1);this.addEvents({autosize:true});};Ext.extend(Ext.form.TextArea,Ext.form.TextField,{minHeight:60,maxHeight:1000,preventScrollbars:false,initEvents:function(){Ext.form.TextArea.superclass.initEvents.call(this);if(this.grow){this.el.on("keyup",this.onKeyUp,this,{buffer:50});this.el.on("click",this.autoSize,this);}},onRender:function(ct){if(!this.el){this.defaultAutoCreate={tag:"textarea",style:"width:300px;height:60px;",autocomplete:"off"};}Ext.form.TextArea.superclass.onRender.call(this,ct);if(this.grow){this.textSizeEl=Ext.DomHelper.append(document.body,{tag:"pre",cls:"x-form-grow-sizer"});if(this.preventScrollbars){this.el.setStyle("overflow","hidden");}}},onKeyUp:function(e){if(!e.isNavKeyPress()||e.getKey()==e.ENTER){this.autoSize();}},autoSize:function(){if(!this.grow||!this.textSizeEl){return;}var el=this.el;var v=el.dom.value;var ts=this.textSizeEl;Ext.fly(ts).setWidth(this.el.getWidth());if(v.length<1){v="&#160;&#160;";}else{v+="&#160;\n&#160;";}if(Ext.isIE){v=v.replace(/\n/g,"<br />");}ts.innerHTML=v;var h=Math.min(this.maxHeight,Math.max(ts.offsetHeight,this.minHeight));this.el.setHeight(h);this.fireEvent("autosize",this,h);},setValue:function(v){Ext.form.TextArea.superclass.setValue.call(this,v);this.autoSize();}});
