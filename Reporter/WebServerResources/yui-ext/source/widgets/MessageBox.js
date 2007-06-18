/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.MessageBox = function(){
    var dlg, opt, mask;
    var bodyEl, msgEl, textboxEl, textareaEl, progressEl, pp;
    var buttons, activeTextEl, bwidth;
    
    var handleButton = function(button){
        dlg.hide();
        Ext.callback(opt.fn, opt.scope||window, [button, activeTextEl.dom.value], 1);
    };
    
    var handleHide = function(){
        if(opt && opt.cls){
            dlg.el.removeClass(cls);
        }    
    };
    
    var updateButtons = function(b){
        var width = 0;
        if(!b){
            buttons["ok"].hide();
            buttons["cancel"].hide();
            buttons["yes"].hide();
            buttons["no"].hide();
            return width;
        }
        for(var k in buttons){
            if(typeof buttons[k] != "function"){
                if(b[k]){
                    buttons[k].show();
                    buttons[k].setText(typeof b[k] == "string" ? b[k] : Ext.MessageBox.buttonText[k]);
                    width += buttons[k].el.getWidth()+15;
                }else{
                    buttons[k].hide();
                }
            }
        }
        return width;
    };
    
    return {
        getDialog : function(){
           if(!dlg){
                dlg = new Ext.BasicDialog("x-msg-box", {
                    autoCreate : true,
                    shadow: true,
                    draggable: true,
                    resizable:false,
                    constraintoviewport:false,
                    fixedcenter:true,
                    collapsible : false,
                    shim:true,
                    modal: true,
                    width:400, height:100,
                    buttonAlign:"center",
                    closeClick : function(){
                        if(opt && opt.buttons && opt.buttons.no && !opt.buttons.cancel){
                            handleButton("no");
                        }else{
                            handleButton("cancel");
                        }
                    }
                });
                dlg.on("hide", handleHide);
                mask = dlg.mask;
                dlg.addKeyListener(27, dlg.hide, dlg);
                buttons = {};
                var bt = this.buttonText;
                buttons["ok"] = dlg.addButton(bt["ok"], handleButton.createCallback("ok"));
                buttons["yes"] = dlg.addButton(bt["yes"], handleButton.createCallback("yes"));
                buttons["no"] = dlg.addButton(bt["no"], handleButton.createCallback("no"));
                buttons["cancel"] = dlg.addButton(bt["cancel"], handleButton.createCallback("cancel"));
                bodyEl = dlg.body.createChild({
                    tag:"div",
                    html:'<span class="ext-mb-text"></span><br /><input type="text" class="ext-mb-input"><textarea class="ext-mb-textarea"></textarea><div class="ext-mb-progress-wrap"><div class="ext-mb-progress"><div class="ext-mb-progress-bar">&#160;</div></div></div>'
                });
                msgEl = bodyEl.dom.firstChild;
                textboxEl = Ext.get(bodyEl.dom.childNodes[2]);
                textboxEl.enableDisplayMode();
                textboxEl.addKeyListener([10,13], function(){
                    if(dlg.isVisible() && opt && opt.buttons){
                        if(opt.buttons.ok){
                            handleButton("ok");
                        }else if(opt.buttons.yes){
                            handleButton("yes");
                        }
                    }
                });
                textareaEl = Ext.get(bodyEl.dom.childNodes[3]);
                textareaEl.enableDisplayMode();
                progressEl = Ext.get(bodyEl.dom.childNodes[4]);
                progressEl.enableDisplayMode();
                var pf = progressEl.dom.firstChild;
                pp = Ext.get(pf.firstChild);
                pp.setHeight(pf.offsetHeight);
            }
            return dlg;
        },
        
        updateText : function(text){
            if(!dlg.isVisible() && !opt.width){
                dlg.resizeTo(this.maxWidth, 100); // resize first so content is never clipped from previous shows
            }
            msgEl.innerHTML = text || '&#160;';
            var w = Math.max(Math.min(opt.width || msgEl.offsetWidth, this.maxWidth), 
                        Math.max(opt.minWidth || this.minWidth, bwidth));
            if(opt.prompt){
                activeTextEl.setWidth(w);
            }
            if(dlg.isVisible()){
                dlg.fixedcenter = false;
            }
            dlg.setContentSize(w, bodyEl.getHeight());
            if(dlg.isVisible()){
                dlg.fixedcenter = true;
            }
            return this;
        },        
        
        updateProgress : function(value, text){
            if(text){
                this.updateText(text);
            }
            pp.setWidth(Math.floor(value*progressEl.dom.firstChild.offsetWidth));
            return this;
        },        
        
        isVisible : function(){
            return dlg && dlg.isVisible();  
        },
        
        hide : function(){
            if(this.isVisible()){
                dlg.hide();
            }  
        },
        
        show : function(options){
            var d = this.getDialog();
            opt = options;
            d.setTitle(opt.title || "&#160;");
            d.close.setDisplayed(opt.closable !== false);
            activeTextEl = textboxEl;
            opt.prompt = opt.prompt || (opt.multiline ? true : false);
            if(opt.prompt){
                if(opt.multiline){
                    textboxEl.hide();
                    textareaEl.show();
                    textareaEl.setHeight(typeof opt.multiline == "number" ?
                        opt.multiline : this.defaultTextHeight);
                    activeTextEl = textareaEl;
                }else{
                    textboxEl.show();
                    textareaEl.hide();
                }
            }else{
                textboxEl.hide();
                textareaEl.hide();
            }
            progressEl.setDisplayed(opt.progress === true);
            this.updateProgress(0);
            activeTextEl.dom.value = opt.value || "";
            if(opt.prompt){
                dlg.setDefaultButton(activeTextEl);
            }else{
                var bs = opt.buttons;
                var db = null;
                if(bs && bs.ok){
                    db = buttons["ok"];
                }else if(bs && bs.yes){
                    db = buttons["yes"];
                }
                dlg.setDefaultButton(db);
            }
            bwidth = updateButtons(opt.buttons);
            this.updateText(opt.msg);
            if(opt.cls){
                d.el.addClass(opt.cls);
            }
            d.proxyDrag = opt.proxyDrag === true;
            d.modal = opt.modal !== false;
            d.mask = opt.modal !== false ? mask : false;
            if(!d.isVisible()){
                d.animateTarget = null;
                d.show(options.animEl);
            }
            return this;
        },
        
        progress : function(title, msg){
            this.show({
                title : title,
                msg : msg,
                buttons: false,
                progress:true,
                closable:false,
                minWidth: this.minProgressWidth
            });
            return this;
        },
        
        alert : function(title, msg, fn, scope){
            this.show({
                title : title,
                msg : msg,
                buttons: this.OK,
                fn: fn,
                scope : scope
            });
            return this;
        },
        
        confirm : function(title, msg, fn, scope){
            this.show({
                title : title,
                msg : msg,
                buttons: this.YESNO,
                fn: fn,
                scope : scope
            });
            return this;
        },
        
        prompt : function(title, msg, fn, scope, multiline){
            this.show({
                title : title,
                msg : msg,
                buttons: this.OKCANCEL,
                fn: fn,
                minWidth:250,
                scope : scope,
                prompt:true,
                multiline: multiline
            });
            return this;
        },
        
        OK : {ok:true},
        YESNO : {yes:true, no:true},
        OKCANCEL : {ok:true, cancel:true},
        YESNOCANCEL : {yes:true, no:true, cancel:true},
        
        defaultTextHeight:75,
        maxWidth : 600,
        minWidth : 100,
        minProgressWidth : 250,
        buttonText : {
            ok : "OK",
            cancel : "Cancel",
            yes : "Yes",
            no : "No"
        }
    };
}();

Ext.Msg = Ext.MessageBox;