/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.util.CSS
 * Class for manipulating CSS Rules
 * @singleton
 */
Ext.util.CSS = function(){
	var rules = null;
   	var doc = document;
   	
   	var toCamel = function(property) {
      var convert = function(prop) {
         var test = /(-[a-z])/i.exec(prop);
         return prop.replace(RegExp.$1, RegExp.$1.substr(1).toUpperCase());
      };
      while(property.indexOf("-") > -1) {
         property = convert(property);
      }
      return property;
   };
   
   return {
   /**
    * Very simple dynamic creation of stylesheets from a text blob of rules.
    * @param {String} cssText The text containing the css rules
    * @return {StyleSheet} 
    */
   createStyleSheet : function(cssText){
       var ss;
       if(Ext.isIE){
           ss = doc.createStyleSheet();
           ss.cssText = cssText;
       }else{
           var head = doc.getElementsByTagName("head")[0];
           var rules = doc.createElement("style");
           rules.setAttribute("type", "text/css");
           try{
                rules.appendChild(doc.createTextNode(cssText));
           }catch(e){
               rules.cssText = cssText; 
           }
           head.appendChild(rules);
           ss = rules.styleSheet ? rules.styleSheet : (rules.sheet || doc.styleSheets[doc.styleSheets.length-1]);
       }
       this.cacheStyleSheet(ss);
       return ss;
   },
   
   removeStyleSheet : function(id){
       var existing = doc.getElementById(id);
       if(existing){
           existing.parentNode.removeChild(existing);
       }
   },
   
   swapStyleSheet : function(id, url){
       this.removeStyleSheet(id);
       var ss = doc.createElement("link");
       ss.setAttribute("rel", "stylesheet");
       ss.setAttribute("type", "text/css");
       ss.setAttribute("id", id);
       ss.setAttribute("href", url);
       doc.getElementsByTagName("head")[0].appendChild(ss);
   },
   
   /**
    * Refresh the rule cache if you have dynamically added stylesheets
    * @return {Object} An object (hash) of rules indexed by selector
    */
   refreshCache : function(){
       return this.getRules(true);
   },
   
   cacheStyleSheet : function(ss){
       if(!rules){
           rules = {};
       }
       try{// try catch for cross domain access issue
           var ssRules = ss.cssRules || ss.rules;
           for(var j = ssRules.length-1; j >= 0; --j){
               rules[ssRules[j].selectorText] = ssRules[j];
           }
       }catch(e){}
   },
   
   /**
    * Gets all css rules for the document
    * @param {Boolean} refreshCache true to refresh the internal cache
    * @return {Object} An object (hash) of rules indexed by selector
    */
   getRules : function(refreshCache){
   		if(rules == null || refreshCache){
   			rules = {};
   			var ds = doc.styleSheets;
   			for(var i =0, len = ds.length; i < len; i++){
   			    try{
    		        this.cacheStyleSheet(ds[i]);
    		    }catch(e){} 
	        }
   		}
   		return rules;
   	},
   	
   	/**
    * Gets an an individual CSS rule by selector(s)
    * @param {String/Array} selector The CSS selector or an array of selectors to try. The first selector that is found is returned.
    * @param {Boolean} refreshCache true to refresh the internal cache
    * @return {CSSRule} The CSS rule or null if one is not found
    */
   getRule : function(selector, refreshCache){
   		var rs = this.getRules(refreshCache);
   		if(!(selector instanceof Array)){
   		    return rs[selector];
   		}
   		for(var i = 0; i < selector.length; i++){
			if(rs[selector[i]]){
				return rs[selector[i]];
			}
		}
		return null;
   	},
   	
   	
   	/**
    * Updates a rule property
    * @param {String/Array} selector If it's an array it tries each selector until it finds one. Stops immediately once one is found.
    * @param {String} property The css property
    * @param {String} value The new value for the property
    * @return {Boolean} true if a rule was found and updated 
    */
   updateRule : function(selector, property, value){
   		if(!(selector instanceof Array)){
   			var rule = this.getRule(selector);
   			if(rule){
   				rule.style[toCamel(property)] = value;
   				return true;
   			}
   		}else{
   			for(var i = 0; i < selector.length; i++){
   				if(this.updateRule(selector[i], property, value)){
   					return true;
   				}
   			}
   		}
   		return false;
   	},
   	
   	/**
    * Applies a rule to an element without adding the class
    * @param {HTMLElement} el The element
    * @param {String/Array} selector If it's an array it tries each selector until it finds one. Stops immediately once one is found.
    * @return {Boolean} true if a rule was found and applied 
    */
   apply : function(el, selector){
   		if(!(selector instanceof Array)){
   			var rule = this.getRule(selector);
   			if(rule){
   			    var s = rule.style;
   				for(var key in s){
   				    if(typeof s[key] != "function"){
       					if(s[key] && String(s[key]).indexOf(":") < 0 && s[key] != "false"){
       						try{el.style[key] = s[key];}catch(e){}
       					}
   				    }
   				}
   				return true;
   			}
   		}else{
   			for(var i = 0; i < selector.length; i++){
   				if(this.apply(el, selector[i])){
   					return true;
   				}
   			}
   		}
   		return false;
   	},
   	
   	applyFirst : function(el, id, selector){
   		var selectors = [
   			"#" + id + " " + selector,
   			selector
   		];
   		return this.apply(el, selectors);
   	},
   	
   	revert : function(el, selector){
   		if(!(selector instanceof Array)){
   			var rule = this.getRule(selector);
   			if(rule){
   				for(key in rule.style){
   					if(rule.style[key] && String(rule.style[key]).indexOf(":") < 0 && rule.style[key] != "false"){
   						try{el.style[key] = "";}catch(e){}
   					}
   				}
   				return true;
   			}
   		}else{
   			for(var i = 0; i < selector.length; i++){
   				if(this.revert(el, selector[i])){
   					return true;
   				}
   			}
   		}
   		return false;
   	},
   	
   	revertFirst : function(el, id, selector){
   		var selectors = [
   			"#" + id + " " + selector,
   			selector
   		];
   		return this.revert(el, selectors);
   	}
   };	
}();