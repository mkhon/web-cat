/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.BasicDialog
 * @extends Ext.util.Observable
 * Lightweight Dialog Class.
 * 
 * The code below lists all configuration options along with the default value.
 * If the default value is what you want you can leave it out:
 * <pre><code>
  var dlg = new Ext.BasicDialog("element-id", {
       autoCreate: false, (true to auto create from scratch, or DomHelper Object)
       title: null, (title to set at config time)
       width: (css),
       height: (css),
       x: 200, //(defaults to center screen if blank)
       y: 500, //(defaults to center screen if blank)
       animateTarget: null,// (no animation) This is the id or element to animate from
       resizable: true,
       resizeHandles: "all",
       minHeight: 80,
       minWidth: 200,
       modal: false,
       autoScroll: true,
       closable: true,
       constraintoviewport: true,
       draggable: true,
       autoTabs: false, (if true searches child nodes for elements with class x-dlg-tab and converts them to tabs)
       tabTag: "div", // the tag name of tab elements
       proxyDrag: false, (drag a proxy element rather than the dialog itself)
       fixedcenter: false,
       shadow: false,
       buttonAlign: "right",
       minButtonWidth: 75,
       shim: false // true to create an iframe shim to 
                   // keep selects from showing through
  });
  </code></pre>
 * @constructor
 * Create a new BasicDialog.
 * @param {String/HTMLElement/Ext.Element} el The id of or container element
 * @param {Object} config configuration options
 */
Ext.BasicDialog = function(el, config){
    config = config || {};
    var dh = Ext.DomHelper;
    var lc = {
        shadow : config.shadow,
        shim : config.shim != false,
        constrain : config.constraintoviewport !== false
    };
    var existing = false;
    if(config.autoCreate){
        var o = typeof config.autoCreate == "object" ?
            config.autoCreate : {tag: "div"};
        o.id = (config.autoCreate.id || el) || Ext.id();
        lc.dh = o; 
    }else{
        existing = el;
    }
    var el = new Ext.layer(lc, existing);
    this.el = el;
    this.id = el.id;
    el.addClass("x-dlg");
        
    Ext.apply(this, config);
    
    this.proxy = el.createProxy("x-dlg-proxy");
    this.proxy.hide = this.hideAction;
    this.proxy.setOpacity(.5);
    this.proxy.hide();
    
    if(config.width){
        el.setWidth(config.width);
    }
    if(config.height){
        el.setHeight(config.height);
    }
    this.size = el.getSize();
    if(typeof config.x != "undefined" && typeof config.y != "undefined"){
        this.xy = [config.x,config.y];
    }else{
        this.xy = el.getCenterXY(true);
    }
    
    /** The header element @type Ext.Element */
    this.header = el.child("/.x-dlg-hd");
    /** The body element @type Ext.Element */
    this.body = el.child("/.x-dlg-bd");
    /** The footer element @type Ext.Element */
    this.footer = el.child("/.x-dlg-ft");
    
    if(!this.header){
        this.header = el.createChild({tag: "div", cls:"x-dlg-hd"}, this.body ? this.body.dom : null);
    }
    if(!this.body){
        this.body = el.createChild({tag: "div", cls:"x-dlg-bd"});
    }
    
    if(this.title){
        this.header.update(this.title);
    }
    // this element allows the dialog to be focused for keyboard event
    this.focusEl = el.createChild({tag: "a", href:"#", cls:"x-dlg-focus", tabIndex:"-1"});
    this.focusEl.swallowEvent("click", true);
    
    this.header.wrap({cls:"x-dlg-hd-right"}).wrap({cls:"x-dlg-hd-left"}, true);
    
    // wrap the body and footer for special rendering
    this.bwrap = this.body.wrap({tag: "div", cls:"x-dlg-dlg-body"});
    if(this.footer){
        this.bwrap.dom.appendChild(this.footer.dom);
    }
    
    this.bg = this.el.createChild({
        tag: "div", cls:"x-dlg-bg",
        html: '<div class="x-dlg-bg-left"><div class="x-dlg-bg-right"><div class="x-dlg-bg-center">&#160;</div></div></div>'
    });
    this.centerBg = this.bg.child("div.x-dlg-bg-center");Ext.get(this.bg.dom.firstChild.firstChild.firstChild);
    
    
    if(this.autoScroll !== false && !this.autoTabs){
        this.body.setStyle("overflow", "auto");
    }
    if(this.closable !== false){
        el.addClass("x-dlg-closable");
        this.close = el.createChild({tag: "div", cls:"x-dlg-close"});
        this.close.on("click", this.closeClick, this);
        this.close.addClassOnOver("x-dlg-close-over");
    }
    if(this.resizable !== false){
        el.addClass("x-dlg-resizable");
        this.resizer = new Ext.Resizable(el, {
            minWidth: this.minWidth || 80, 
            minHeight:this.minHeight || 80, 
            handles: this.resizeHandles || "all",
            pinned: true
        });
        this.resizer.on("beforeresize", this.beforeResize, this);
        this.resizer.on("resize", this.onResize, this);
    }
    if(this.draggable !== false){
        el.addClass("x-dlg-draggable");
        if (!this.proxyDrag) {
            var dd = new Ext.dd.DD(el.dom.id, "WindowDrag");
        }
        else {
            var dd = new Ext.dd.DDProxy(el.dom.id, "WindowDrag", {dragElId: this.proxy.id});
        }
        dd.setHandleElId(this.header.id);
        dd.endDrag = this.endMove.createDelegate(this);
        dd.startDrag = this.startMove.createDelegate(this);
        dd.onDrag = this.onDrag.createDelegate(this);
        this.dd = dd;
    }
    if(this.modal){
        el.addClass("x-dlg-modal");
        this.mask = dh.append(document.body, {tag: "div", cls:"x-dlg-mask"}, true);
        this.mask.enableDisplayMode("block");
        this.mask.hide();
    }
    if(this.autoTabs){
        this.initTabs();
    }
    this.syncBodyHeight();
    this.events = {
        /**
         * @event keydown
         * Fires when a key is pressed
         * @param {Ext.BasicDialog} this
         * @param {Ext.EventObject} e
         */
        "keydown" : true,
        /**
         * @event move
         * Fires when this dialog is moved by the user.
         * @param {Ext.BasicDialog} this
         * @param {Number} x The new page X
         * @param {Number} y The new page Y
         */
        "move" : true,
        /**
         * @event resize
         * Fires when this dialog is resized by the user.
         * @param {Ext.BasicDialog} this
         * @param {Number} width The new width
         * @param {Number} height The new height
         */
        "resize" : true,
        /**
         * @event beforehide
         * Fires before this dialog is hidden.
         * @param {Ext.BasicDialog} this
         */
        "beforehide" : true,
        /**
         * @event hide
         * Fires when this dialog is hidden.
         * @param {Ext.BasicDialog} this
         */
        "hide" : true,
        /**
         * @event beforeshow
         * Fires before this dialog is shown.
         * @param {Ext.BasicDialog} this
         */
        "beforeshow" : true,
        /**
         * @event show
         * Fires when this dialog is shown.
         * @param {Ext.BasicDialog} this
         */
        "show" : true
    };
    el.on("keydown", this.onKeyDown, this);
    el.on("mousedown", this.toFront, this);
    Ext.EventManager.onWindowResize(this.adjustViewport, this, true);
    this.el.hide();
    Ext.DialogManager.register(this);
};

Ext.extend(Ext.BasicDialog, Ext.util.Observable, {
    shadowOffset: 3,
    minHeight: 80,
    minWidth: 200,
    minButtonWidth: 75,
    defaultButton: null,
    buttonAlign: "right",
    /**
     * Sets the dialog title.
     * @param {String} text
     * @return {Ext.BasicDialog} this
     */
    setTitle : function(text){
        this.header.update(text);
        return this; 
    },
    
    closeClick : function(){
        this.hide();  
    },
    
    /**
     * Reinitializes the tabs component, clearing out old tabs and finding new ones.
     * @return {Ext.TabPanel} tabs The tabs component
     */
    initTabs : function(){
        var tabs = this.getTabs();
        while(tabs.getTab(0)){
            tabs.removeTab(0);
        }
        var tabEls = YAHOO.util.Dom.getElementsByClassName("x-dlg-tab", this.tabTag || "div", this.el.dom);
        if(tabEls.length > 0){
            for(var i = 0, len = tabEls.length; i < len; i++) {
            	var tabEl = tabEls[i];
            	tabs.addTab(Ext.id(tabEl), tabEl.title);
            	tabEl.title = "";
            }
            tabs.activate(0);
        }
        return tabs;
    },
    
    beforeResize : function(){
        this.resizer.minHeight = Math.max(this.minHeight, this.getHeaderFooterHeight(true)+40);
    },
    
    onResize : function(){
        this.refreshSize();
        this.syncBodyHeight();
        this.adjustAssets();
        this.fireEvent("resize", this, this.size.width, this.size.height);
    },
    
    onKeyDown : function(e){
        if(this.isVisible()){
            this.fireEvent("keydown", this, e);
        }
    },
    
    /**
     * Resizes the dialog.
     * @param {Number} width
     * @param {Number} height
     * @return {Ext.BasicDialog} this
     */
    resizeTo : function(width, height){
        this.el.setSize(width, height);
        this.size = {width: width, height: height};
        this.syncBodyHeight();
        if(this.fixedcenter){
            this.center();
        }
        if(this.isVisible()){
            this.constrainXY();
            this.adjustAssets();
        }
        this.fireEvent("resize", this, width, height);
        return this;
    },
    
    
    /**
     * Resizes the dialog to fit the specified content size.
     * @param {Number} width
     * @param {Number} height
     * @return {Ext.BasicDialog} this
     */
    setContentSize : function(w, h){
        h += this.getHeaderFooterHeight() + this.body.getMargins("tb");
        w += this.body.getMargins("lr") + this.bwrap.getMargins("lr") + this.centerBg.getPadding("lr");
        //if(!this.el.isBorderBox()){
            h +=  this.body.getPadding("tb") + this.bwrap.getBorderWidth("tb") + this.body.getBorderWidth("tb") + this.el.getBorderWidth("tb");
            w += this.body.getPadding("lr") + this.bwrap.getBorderWidth("lr") + this.body.getBorderWidth("lr") + this.bwrap.getPadding("lr") + this.el.getBorderWidth("lr");
        //}
        if(this.tabs){
            h += this.tabs.stripWrap.getHeight() + this.tabs.bodyEl.getMargins("tb") + this.tabs.bodyEl.getPadding("tb");
            w += this.tabs.bodyEl.getMargins("lr") + this.tabs.bodyEl.getPadding("lr");
        }
        this.resizeTo(w, h);
        return this;
    },
    
    /**
     * Adds a key listener for when this dialog is displayed
     * @param {Number/Array/Object} key Either the numeric key code, array of key codes or an object with the following options: 
     *                                  {key: (number or array), shift: (true/false), ctrl: (true/false), alt: (true/false)}
     * @param {Function} fn The function to call
     * @param {Object} scope (optional) The scope of the function
     * @return {Ext.BasicDialog} this
     */
    addKeyListener : function(key, fn, scope){
        var keyCode, shift, ctrl, alt;
        if(typeof key == "object" && !(key instanceof Array)){
            keyCode = key["key"];
            shift = key["shift"];
            ctrl = key["ctrl"];
            alt = key["alt"];
        }else{
            keyCode = key;
        }
        var handler = function(dlg, e){
            if((!shift || e.shiftKey) && (!ctrl || e.ctrlKey) &&  (!alt || e.altKey)){
                var k = e.getKey();
                if(keyCode instanceof Array){
                    for(var i = 0, len = keyCode.length; i < len; i++){
                        if(keyCode[i] == k){
                          fn.call(scope || window, dlg, k, e);
                          return;
                        }
                    }
                }else{
                    if(k == keyCode){
                        fn.call(scope || window, dlg, k, e);
                    }
                }
            }
        };
        this.on("keydown", handler);
        return this; 
    },
    
    /**
     * Returns the TabPanel component (if autoTabs)
     * @return {Ext.TabPanel}
     */
    getTabs : function(){
        if(!this.tabs){
            this.el.addClass("x-dlg-auto-tabs");
            this.body.addClass(this.tabPosition == "bottom" ? "x-tabs-bottom" : "x-tabs-top");
            this.tabs = new Ext.TabPanel(this.body.dom, this.tabPosition == "bottom");
        }
        return this.tabs;    
    },
    
    /**
     * Adds a button.
     * @param {String/Object} config A string becomes the button text, an object is expected to be a valid Ext.DomHelper element config
     * @param {Function} handler The function called when the button is clicked
     * @param {Object} scope (optional) The scope of the handler function
     * @return {Ext.Button}
     */
    addButton : function(config, handler, scope){
        var dh = Ext.DomHelper;
        if(!this.footer){
            this.footer = dh.append(this.bwrap.dom, {tag: "div", cls:"x-dlg-ft"}, true);
        }
        if(!this.btnContainer){
            var tb = this.footer.createChild({
                tag:"div",
                cls:"x-dlg-btns x-dlg-btns-"+this.buttonAlign,
                html:'<table cellspacing="0"><tbody><tr></tr></tbody></table>'
            });
            this.btnContainer = tb.dom.firstChild.firstChild.firstChild;
        }
        var bconfig = {
            handler: handler,
            scope: scope,
            minWidth: this.minButtonWidth
        };
        if(typeof config == "string"){
            bconfig.text = config;
        }else{
            bconfig.dhconfig = config;
        }
        var btn = new Ext.Button(
            this.btnContainer.appendChild(document.createElement("td")),
            bconfig
        );
        this.syncBodyHeight();
        if(!this.buttons){
            this.buttons = [];
        }
        this.buttons.push(btn);
        return btn;
    },
    
    /**
     * Sets the default button to be focused when the dialog is displayed
     * @param {Ext.BasicDialog.Button} btn The button object returned by addButton
     * @return {Ext.BasicDialog} this
     */
    setDefaultButton : function(btn){
        this.defaultButton = btn;  
        return this;
    },
    
    getHeaderFooterHeight : function(safe){
        var height = 0;
        if(this.header){
           height += this.header.getHeight();
        }
        if(this.footer){
           var fm = this.footer.getMargins();
            height += (this.footer.getHeight()+fm.top+fm.bottom);
        }
        height += this.bwrap.getPadding("tb")+this.bwrap.getBorderWidth("tb");
        height += this.centerBg.getPadding("tb");
        return height;
    },
    
    syncBodyHeight : function(){
        var height = this.size.height - this.getHeaderFooterHeight(false);
        this.body.setHeight(height-this.body.getMargins("tb"));
        if(this.tabs){
            this.tabs.syncHeight();
        }
        var hh = this.header.getHeight();
        var h = this.size.height-hh;
        this.centerBg.setHeight(h);
        this.bwrap.setLeftTop(this.centerBg.getPadding("l"), hh+this.centerBg.getPadding("t"));
        this.bwrap.setHeight(h-this.centerBg.getPadding("tb"));
        this.bwrap.setWidth(this.el.getWidth(true)-this.centerBg.getPadding("lr"));
        this.body.setWidth(this.bwrap.getWidth(true));
    },
    
    /**
     * Restores the previous state of the dialog if Ext.state is configured
     * @return {Ext.BasicDialog} this
     */
    restoreState : function(){
        var box = Ext.state.Manager.get(this.stateId || (this.el.id + "-state"));
        if(box && box.width){
            this.xy = [box.x, box.y];
            this.resizeTo(box.width, box.height);
        }
        return this; 
    },
    
    beforeShow : function(){
        if(this.fixedcenter) {
            this.xy = this.el.getCenterXY(true);
        }
        if(this.modal){
            YAHOO.util.Dom.addClass(document.body, "x-masked");
            this.mask.setSize(Ext.lib.Dom.getViewWidth(true), Ext.lib.Dom.getViewHeight(true));
            this.mask.show();
        }
        this.constrainXY();
    },
    
    animShow : function(){
        var b = Ext.get(this.animateTarget, true).getBox();
        this.proxy.setSize(b.width, b.height);
        this.proxy.setLocation(b.x, b.y);
        this.proxy.show();
        this.proxy.setBounds(this.xy[0], this.xy[1], this.size.width, this.size.height, 
                    true, .35, this.showEl.createDelegate(this));
    },
    
    /**
     * Shows the dialog.
     * @param {String/HTMLElement/Ext.Element} animateTarget (optional) Reset the animation target
     * @return {Ext.BasicDialog} this
     */
    show : function(animateTarget){
        if (this.fireEvent("beforeshow", this) === false){
            return;
        }
        if(this.syncHeightBeforeShow){
            this.syncBodyHeight();
        }
        this.animateTarget = animateTarget || this.animateTarget;
        if(!this.el.isVisible()){
            this.beforeShow();
            if(this.animateTarget){
                this.animShow();
            }else{
                this.showEl();
            }
        }
        return this; 
    },
    
    showEl : function(){
        this.proxy.hide();
        this.el.setXY(this.xy);
        this.el.show();
        this.adjustAssets(true);
        this.toFront();
        this.focus();
        this.fireEvent("show", this);
    },
    
    focus : function(){
        if(this.defaultButton){
            this.defaultButton.focus();
        }else{
            this.focusEl.focus();
        }  
    },
    
    constrainXY : function(){
        if(this.constraintoviewport !== false){
            if(!this.viewSize){
                if(this.container){
                    var s = this.container.getSize();
                    this.viewSize = [s.width, s.height];
                }else{
                    this.viewSize = [Ext.lib.Dom.getViewWidth(),Ext.lib.Dom.getViewHeight()];
                }
            }
            var x = this.xy[0], y = this.xy[1];
            var w = this.size.width, h = this.size.height;
            var vw = this.viewSize[0], vh = this.viewSize[1];
            // only move it if it needs it
            var moved = false;
            // first validate right/bottom
            if(x + w > vw){
                x = vw - w;
                moved = true;
            }
            if(y + h > vh){
                y = vh - h;
                moved = true;
            }
            // then make sure top/left isn't negative
            if(x < 0){
                x = 0;
                moved = true;
            }
            if(y < 0){
                y = 0;
                moved = true;
            }
            if(moved){
                // cache xy
                this.xy = [x, y];
                if(this.isVisible()){
                    this.el.setLocation(x, y);
                    this.adjustAssets();
                }
            }
        }
    },
    
    onDrag : function(){
        if(!this.proxyDrag){
            this.xy = this.el.getXY();
            this.adjustAssets();
        }   
    },
    
    adjustAssets : function(doShow){
        var x = this.xy[0], y = this.xy[1];
        var w = this.size.width, h = this.size.height;
        if(doShow === true){
            if(this.shadow){
                this.shadow.show();
            }
            if(this.shim){
                this.shim.show();
            }
        }
        if(this.shadow && this.shadow.isVisible()){
            this.shadow.setBounds(x + this.shadowOffset, y + this.shadowOffset, w, h);
        }
        if(this.shim && this.shim.isVisible()){
            this.shim.setBounds(x, y, w, h);
        }
    },
    
    
    adjustViewport : function(w, h){
        if(!w || !h){
            w = Ext.lib.Dom.getViewWidth();
            h = Ext.lib.Dom.getViewHeight();
        }
        // cache the size
        this.viewSize = [w, h];
        if(this.modal && this.mask.isVisible()){
            this.mask.setSize(w, h); // first make sure the mask isn't causing overflow
            this.mask.setSize(Ext.lib.Dom.getViewWidth(true), Ext.lib.Dom.getViewHeight(true));
        }
        if(this.isVisible()){
            this.constrainXY();
        }
    },
    
    /**
     * Destroys this dialog
     * @param {Boolean} removeEl (optional) true to remove the element from the DOM
     */
    destroy : function(removeEl){
        Ext.EventManager.removeResizeListener(this.adjustViewport, this);
        if(this.tabs){
            this.tabs.destroy(removeEl);
        }
        if(this.shim){
            this.shim.remove();
        }
        if(this.shadow){
            this.shadow.remove();
        }
        if(this.proxy){
            this.proxy.remove();
        }
        if(this.resizer){
            this.resizer.destroy();
        }
        if(this.close){
            this.close.removeAllListeners();
            this.close.remove();
        }
        if(this.mask){
            this.mask.remove();
        }
        if(this.dd){
            this.dd.unreg();
        }
        if(this.buttons){
           for(var i = 0, len = this.buttons.length; i < len; i++){
               this.buttons[i].destroy();
           }
        }
        this.el.removeAllListeners();
        if(removeEl === true){
            this.el.update("");
            this.el.remove();
        }
        Ext.DialogManager.unregister(this);
    },
    
    startMove : function(){
        if(this.proxyDrag){
            this.proxy.show();
        }
        if(this.constraintoviewport !== false){
            this.dd.constrainTo(document.body, {right: this.shadowOffset, bottom: this.shadowOffset});
        }
    },
    
    endMove : function(){
        if(!this.proxyDrag){
            Ext.dd.DD.prototype.endDrag.apply(this.dd, arguments);
        }else{
            Ext.dd.DDProxy.prototype.endDrag.apply(this.dd, arguments);
            this.proxy.hide();
        }
        this.refreshSize();
        this.adjustAssets();
        this.fireEvent("move", this, this.xy[0], this.xy[1])
    },
    
    /**
     * Brings this dialog to the front of any other visible dialogs
     * @return {Ext.BasicDialog} this
     */
    toFront : function(){
        Ext.DialogManager.bringToFront(this);  
        return this; 
    },
    
    /**
     * Sends this dialog to the back (under) of any other visible dialogs
     * @return {Ext.BasicDialog} this
     */
    toBack : function(){
        Ext.DialogManager.sendToBack(this);  
        return this; 
    },
    
    /**
     * Centers this dialog 
     * @return {Ext.BasicDialog} this
     */
    center : function(){
        var xy = this.el.getCenterXY(true);
        this.moveTo(xy[0], xy[1]);
        return this; 
    },
    
    /**
     * Moves the dialog to the specified point
     * @param {Number} x
     * @param {Number} y
     * @return {Ext.BasicDialog} this
     */
    moveTo : function(x, y){
        this.xy = [x,y];
        if(this.isVisible()){
            this.el.setXY(this.xy);
            this.adjustAssets();
        }
        return this; 
    },
    
    /**
     * Returns true if the dialog is visible
     * @return {Boolean}
     */
    isVisible : function(){
        return this.el.isVisible();    
    },
    
    animHide : function(callback){
        var b = Ext.get(this.animateTarget, true).getBox();
        this.proxy.show();
        this.proxy.setBounds(this.xy[0], this.xy[1], this.size.width, this.size.height);
        this.el.hide();
        this.proxy.setBounds(b.x, b.y, b.width, b.height, true, .35, 
                    this.hideEl.createDelegate(this, [callback]));
    },
    
    /**
     * Hides the dialog.
     * @param {Function} callback (optional) Function to call when the dialog is hidden
     * @return {Ext.BasicDialog} this
     */
    hide : function(callback){
        if (this.fireEvent("beforehide", this) === false)
            return;
        
        if(this.shadow){
            this.shadow.hide();
        }
        if(this.shim) {
          this.shim.hide();
        }
        if(this.animateTarget){
           this.animHide(callback);
        }else{
            this.el.hide();
            this.hideEl(callback);
        }
        return this; 
    },
    
    hideEl : function(callback){
        this.proxy.hide();
        if(this.modal){
            this.mask.hide();
            YAHOO.util.Dom.removeClass(document.body, "x-masked");
        }
        this.fireEvent("hide", this);
        if(typeof callback == "function"){
            callback();
        }
    },
    
    hideAction : function(){
        this.setLeft("-10000px");
        this.setTop("-10000px");
        this.setStyle("visibility", "hidden");
    },
    
    refreshSize : function(){
        this.size = this.el.getSize();
        this.xy = this.el.getXY();
        Ext.state.Manager.set(this.stateId || this.el.id + "-state", this.el.getBox());
    },
    
    setZIndex : function(index){
        if(this.modal){
            this.mask.setStyle("z-index", index);
        }
        if(this.shim){
            this.shim.setStyle("z-index", ++index);
        }
        if(this.shadow){
            this.shadow.setStyle("z-index", ++index);
        }
        this.el.setStyle("z-index", ++index);
        if(this.proxy){
            this.proxy.setStyle("z-index", ++index);
        }
        if(this.resizer){
            this.resizer.proxy.setStyle("z-index", ++index);
        }
        
        this.lastZIndex = index;
    },
    
    /**
     * Returns the element for this dialog
     * @return {Ext.Element}
     */
    getEl : function(){
        return this.el;
    }
});

/**
 * @class Ext.DialogManager
 * Provides global access to BasicDialogs that have been created and 
 * support for z-indexing (layering) multiple open dialogs.
 */
Ext.DialogManager = function(){
    var list = {};
    var accessList = [];
    var front = null;
    
    var sortDialogs = function(d1, d2){
        return (!d1._lastAccess || d1._lastAccess < d2._lastAccess) ? -1 : 1;
    };
    
    var orderDialogs = function(){
        accessList.sort(sortDialogs);
        var seed = Ext.DialogManager.zseed;
        for(var i = 0, len = accessList.length; i < len; i++){
            if(accessList[i]){
                accessList[i].setZIndex(seed + (i*10));
            }  
        }
    };
    
    return {
        /**
         * The starting z-index for BasicDialogs - defaults to 10000
         * @type Number
         */
        zseed : 10000,
        
        
        register : function(dlg){
            list[dlg.id] = dlg;
            accessList.push(dlg);
        },
        
        unregister : function(dlg){
            delete list[dlg.id];
            if(!accessList.indexOf){
                for(var i = 0, len = accessList.length; i < len; i++){
                    accessList.splice(i, 1);
                    return;
                }
            }else{
                var i = accessList.indexOf(dlg);
                if(i != -1){
                    accessList.splice(i, 1);
                }
            }
        },
        
        /**
         * Gets a registered dialog by id
         * @param {String/Object} id The id of the dialog or a dialog
         * @return {Ext.BasicDialog}
         */
        get : function(id){
            return typeof id == "object" ? id : list[id];
        },
        
        /**
         * Brings the specified dialog to the front
         * @param {String/Object} dlg The id of the dialog or a dialog
         * @return {Ext.BasicDialog}
         */
        bringToFront : function(dlg){
            dlg = this.get(dlg);
            if(dlg != front){
                front = dlg;
                dlg._lastAccess = new Date().getTime();
                orderDialogs();
            }
            return dlg;
        },
        
        /**
         * Sends the specified dialog to the back
         * @param {String/Object} dlg The id of the dialog or a dialog
         * @return {Ext.BasicDialog}
         */
        sendToBack : function(dlg){
            dlg = this.get(dlg);
            dlg._lastAccess = -(new Date().getTime());
            orderDialogs();
            return dlg;
        }
    };
}();

/**
 * @class Ext.LayoutDialog
 * @extends Ext.BasicDialog
 * Dialog which provides adjustments for working with a layout in a Dialog. 
 * Add your neccessary layout config options to the dialogs config.<br>
 * Example Usage (including a nested layout):
 * <pre><code>    if(!dialog){
    dialog = new Ext.LayoutDialog("download-dlg", { 
            modal: true,
            width:600,
            height:450,
            shadow:true,
            minWidth:500,
            minHeight:350,
            autoTabs:true,
            proxyDrag:true,
            // layout config merges with the dialog config
            center:{
                tabPosition: "top",
                alwaysShowTabs: true
            }
    });
    dialog.addKeyListener(27, dialog.hide, dialog);
    dialog.setDefaultButton(dialog.addButton("Close", dialog.hide, dialog));
    dialog.addButton("Build It!", this.getDownload, this);
    
    // we can even add nested layouts
    var innerLayout = new Ext.BorderLayout("dl-inner", {
        east: {
            initialSize: 200,
            autoScroll:true,
            split:true
        },
        center: {
            autoScroll:true
        }
    });
    innerLayout.beginUpdate();
    innerLayout.add("east", new Ext.ContentPanel("dl-details"));
    innerLayout.add("center", new Ext.ContentPanel("selection-panel"));
    innerLayout.endUpdate(true);
    
    var layout = dialog.getLayout();
    layout.beginUpdate();
    layout.add("center", new Ext.ContentPanel("standard-panel",
                        {title: "Download the Source", fitToFrame:true}));
    layout.add("center", new Ext.NestedLayoutPanel(innerLayout,
               {title: "Build your own yui-ext.js"}));
    layout.getRegion("center").showPanel(sp);
    layout.endUpdate();</code></pre>
    * @constructor
    * @param {String/HTMLElement/Ext.Element} el The id of or container element
    * @param {Object} config configuration options
  */
Ext.LayoutDialog = function(el, config){
    config.autoTabs = false;
    Ext.LayoutDialog.superclass.constructor.call(this, el, config);
    this.body.setStyle({overflow:"hidden", position:"relative"});
    this.layout = new Ext.BorderLayout(this.body.dom, config);
    this.layout.monitorWindowResize = false;
    this.el.addClass("x-dlg-auto-layout");
    // fix case when center region overwrites center function
    this.center = Ext.BasicDialog.prototype.center;
    this.on("show", this.layout.layout, this.layout, true);
};
Ext.extend(Ext.LayoutDialog, Ext.BasicDialog, {
    /**
     * Ends update of the layout <strike>and resets display to none</strike>. Use standard beginUpdate/endUpdate on the layout.
     * @deprecated
     */
    endUpdate : function(){
        this.layout.endUpdate();
    },
    /**
     * Begins an update of the layout <strike>and sets display to block and visibility to hidden</strike>. Use standard beginUpdate/endUpdate on the layout.
     *  @deprecated
     */
    beginUpdate : function(){
        this.layout.beginUpdate();
    },
    /**
     * Get the BorderLayout for this dialog
     * @return {Ext.BorderLayout} 
     */
    getLayout : function(){
        return this.layout;
    },
    syncBodyHeight : function(){
        Ext.LayoutDialog.superclass.syncBodyHeight.call(this);
        if(this.layout)this.layout.layout();
    }
});