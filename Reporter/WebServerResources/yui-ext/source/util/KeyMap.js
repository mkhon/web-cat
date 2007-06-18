/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.KeyMap
 * Handles mapping keys to actions for an element. One key map can be used for multiple actions. 
 * A KeyMap can also handle a string representation of keys.<br />
 * Usage:
 <pre><code>
 // map one key by key code
 var map = new Ext.KeyMap("my-element", {
     key: 13,
     fn: myHandler,
     scope: myObject
 });
 
 // map multiple keys to one action by string
 var map = new Ext.KeyMap("my-element", {
     key: "a\r\n\t",
     fn: myHandler,
     scope: myObject
 });
 
 // map multiple keys to multiple actions by strings and array of codes
 var map = new Ext.KeyMap("my-element", [
    {
        key: [10,13],
        fn: function(){ alert("Return was pressed"); }
    }, {
        key: "abc",
        fn: function(){ alert('a, b or c was pressed'); }
    }, {
        key: "\t",
        ctrl:true,
        shift:true,
        fn: function(){ alert('Control + shift + tab was pressed.'); }
    }
]);
 </code></pre>
* <b>Note: A KepMap starts enabled</b>
* @constructor
* @param {String/HTMLElement/Ext.Element} el The element to bind to
* @param {Object} config The config
* @param {String} eventName (optional) The event to bind to (Defaults to "keydown").
 */
Ext.KeyMap = function(el, config, eventName){
    this.el  = Ext.get(el);
    this.eventName = eventName || "keydown";
    this.bindings = [];
    if(config instanceof Array){
	    for(var i = 0, len = config.length; i < len; i++){
	        this.addBinding(config[i]);
	    }
    }else{
        this.addBinding(config);
    }
    this.keyDownDelegate = Ext.EventManager.wrap(this.handleKeyDown, this, true);
    this.enable();
};

Ext.KeyMap.prototype = {
    stopEvent : false,
    /**
     * Add a new binding to this KeyMap
     * @param {Object} config A single KeyMap config
     */
	addBinding : function(config){
        var keyCode = config.key, 
            shift = config.shift, 
            ctrl = config.ctrl, 
            alt = config.alt,
            fn = config.fn,
            scope = config.scope;
        if(typeof keyCode == "string"){
            var ks = [];
            var keyString = keyCode.toUpperCase();
            for(var j = 0, len = keyString.length; j < len; j++){
                ks.push(keyString.charCodeAt(j));
            }
            keyCode = ks;
        }
        var keyArray = keyCode instanceof Array;
        var handler = function(e){
            if((!shift || e.shiftKey) && (!ctrl || e.ctrlKey) &&  (!alt || e.altKey)){
                var k = e.getKey();
                if(keyArray){
                    for(var i = 0, len = keyCode.length; i < len; i++){
                        if(keyCode[i] == k){
                          if(this.stopEvent){
                              e.stopEvent();
                          }
                          fn.call(scope || window, k, e);
                          return;
                        }
                    }
                }else{
                    if(k == keyCode){
                        if(this.stopEvent){
                           e.stopEvent();
                        }
                        fn.call(scope || window, k, e);
                    }
                }
            }
        };
        this.bindings.push(handler);  
	},
	
	handleKeyDown : function(e){
	    if(this.enabled){ //just in case
    	    var b = this.bindings;
    	    for(var i = 0, len = b.length; i < len; i++){
    	        b[i].call(this, e);
    	    }
	    }
	},
	
	/**
	 * Returns true if this KepMap is enabled
	 * @return {Boolean} 
	 */
	isEnabled : function(){
	    return this.enabled;  
	},
	
	/**
	 * Enable this KeyMap
	 */
	enable: function(){
		if(!this.enabled){
		    this.el.on(this.eventName, this.keyDownDelegate);
		    this.enabled = true;
		}
	},

	/**
	 * Disable this KeyMap
	 */
	disable: function(){
		if(this.enabled){
		    this.el.removeListener(this.eventName, this.keyDownDelegate);
		    this.enabled = false;
		}
	}
};