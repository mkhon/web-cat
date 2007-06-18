/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */


Ext.StyleAnim = function(css){
    var o = css;
    if(typeof css == "string"){
         o = {};
        var re = Ext.StyleAnim.styleRE;
        var m;
        while ((m = re.exec(css)) != null){
            o[m[1]] = m[2];
        }
    }
    this.styles = o;
    if(!Ext.StyleAnim.measureEl){
        var el = document.createElement("div");
        el.style.position = "absolute";
        el.style.top = "-500px";
        el.style.left = "-500px";
        el.style.width = "0px";
        el.style.visibility = "hidden";
        document.body.appendChild(el);
        Ext.StyleAnim.measureEl = Ext.get(el);
    }
};
Ext.StyleAnim.styleRE = /\s?([a-z\-]*)\:([^;]*);?/gi;

Ext.StyleAnim.measurables = [
       "border",
       "border-width",
       "border-left",
       "border-right",
       "border-top",
       "border-bottom",
       "border-left-width",
       "border-right-width",
       "border-top-width",
       "border-bottom-width",
       "padding-left",
       "padding-right",
       "padding-top",
       "padding-bottom"
];
Ext.StyleAnim.copyStyles = [
       "border-left-width",
       "border-right-width",
       "border-top-width",
       "border-bottom-width",
       "padding-left",
       "padding-right",
       "padding-top",
       "padding-bottom"
];
Ext.StyleAnim.prototype = {
    apply : function(el, callback){
        var o = this.styles;
        var el = Ext.get(el);
        var adjAttr = ["width","height"];
        if(el.autoBoxAdjust && !el.isBorderBox()){
            var up = Ext.Element.unitPattern;
            var adj;
            for(var i = 0, len = adjAttr.length; i < len; i++) {
            	var attr = adjAttr[i];
            	if(o[attr] !== undefined){
                     var m = String(o[attr]).match(up);
                     if(!m || m[1] == "px"){
                         if(!adj){
                             adj = this.getAdjustments(el, o);
                         }
                         var v = Math.max(0, (parseInt(o[attr], 10) || 0) - adj[0]);
                         o[attr] = v;
                     }
                }
            }
        }else{
            for(var i = 0, len = adjAttr.length; i < len; i++) {
            	var attr = adjAttr[i];
            	if(o[attr] !== undefined){
                    o[attr] = Math.max(0, parseInt(o[attr], 10) || 0);
                }
            }
        }
        var attr = {};
        for(var k in o){
            if(typeof o[k] != "function"){
                attr[k] = {to: o[k]};
            }
        }
        alert(Ext.util.JSON.encode(attr))
        new YAHOO.util.Anim(el.dom, attr, .5).animate();
        return this;
    },

    getAdjustments : function(el, o){
        var mel = Ext.StyleAnim.measureEl;
        var ms = Ext.StyleAnim.measurables;
        mel.copyStyles(el, Ext.StyleAnim.copyStyles);
        // apply measurable styles
        for(var i = 0, len = ms.length; i < len; i++) {
        	var s = ms[i];
        	if(o[s]){
        	    mel.setStyle(s, o[s]);
        	}
        }
        return [mel.getBorderWidth("lr")+mel.getPadding("lr"),
                mel.getBorderWidth("tb")+mel.getPadding("tb")];
    }
};

Ext.ClassAnim = function(selector){
     var rule = Ext.util.CSS.getRule(selector);
     var s = rule.style;
     var attrs = {};
	 for(var key in s){
	    if(s[key] && typeof s[key] != "function" && String(s[key]).indexOf(":") < 0 && s[key] != "false"){
			attrs[key] = s[key];
	    }
	 }
	 Ext.ClassAnim.superclass.constructor.call(this, attrs);
};

Ext.extend(Ext.ClassAnim, Ext.StyleAnim);