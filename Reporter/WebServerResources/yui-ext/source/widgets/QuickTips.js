/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.QuickTips
 * @singleton
 */
Ext.QuickTips = function(){
    var el, tipBody, tipTitle, tm, cfg, close, tagEls = {}, esc, removeCls = null;
    var ce, bd, xy, dd;
    var visible = false, disabled = true, inited = false;
    var showProc = 1, hideProc = 1, dismissProc = 1, locks = [];
    
    var onOver = function(e){
        if(disabled){
            return;
        }
        var t = e.getTarget();
        if(!t || t.nodeType !== 1 || t == document || t == document.body){
            return;
        }
        if(ce && t == ce.el){
            clearTimeout(hideProc);
            return;
        }
        if(t && tagEls[t.id]){
            tagEls[t.id].el = t;
            showProc = show.defer(tm.showDelay, tm, [tagEls[t.id]]);
            return;
        }
        var ttp, et = Ext.fly(t);
        var ns = cfg.namespace;
        if(tm.interceptTitles && t.title){
            ttp = t.title;
            t.qtip = ttp;
            t.removeAttribute("title");
            e.preventDefault();
        }else{
            ttp = t.qtip || et.getAttributeNS(ns, cfg.attribute);
        }
        if(ttp){
            showProc = show.defer(tm.showDelay, tm, [{
                el: t, 
                text: ttp, 
                width: et.getAttributeNS(ns, cfg.width),
                autoHide: et.getAttributeNS(ns, cfg.hide) != "user",
                title: et.getAttributeNS(ns, cfg.title),
           	    cls: et.getAttributeNS(ns, cfg.cls)
            }]);
        }
    };
    
    var onOut = function(e){
        clearTimeout(showProc);
        var t = e.getTarget();
        if(t && ce && ce.el == t && (tm.autoHide && ce.autoHide !== false)){
            hideProc = setTimeout(hide, tm.hideDelay);
        }
    };
    
    var onMove = function(e){
        if(disabled){
            return;
        }
        xy = e.getXY();
        xy[1] += 18;
        if(tm.trackMouse && ce){
            el.setXY(xy);
        }
    };
    
    var onDown = function(e){
        clearTimeout(showProc);
        clearTimeout(hideProc);
        if(!e.within(el)){
            if(tm.hideOnClick && ce && ce.autoHide !== false){
                hide();
                tm.disable();
            }
        }
    };
    
    var onUp = function(e){
        tm.enable();
    };
    
    var show = function(o){
        if(disabled){
            return;
        }
        clearTimeout(dismissProc);
        ce = o;
        if(removeCls){ // in case manually hidden
            el.removeClass(removeCls);
            removeCls = null;
        }
        if(ce.cls){
            el.addClass(ce.cls);
            removeCls = ce.cls;
        }
        if(ce.title){
            tipTitleText.update(ce.title);
            tipTitle.show();
        }else{
            tipTitle.hide();
        }
        tipBody.update(o.text);
        if(!ce.width){
            if(tipBody.dom.style.width){
               tipBody.dom.style.width  = "";
            }
            if(tipBody.dom.offsetWidth > tm.maxWidth){
                tipBody.setWidth(tm.maxWidth);
            }
            if(tipBody.dom.offsetWidth < tm.minWidth){
                tipBody.setWidth(tm.minWidth);
            }
        }else{
            tipBody.setWidth(ce.width);
        }
        if(!ce.autoHide){
            close.setDisplayed(true);
            if(dd){
                dd.unlock();
            }
        }else{
            close.setDisplayed(false);
            if(dd){
                dd.lock();
            }
        }
        if(xy){
            el.setXY(xy);
        }
        if(tm.animate){
            el.setOpacity(.1);
            el.setStyle("visibility", "visible");
            el.fadeIn({callback: afterShow});
        }else{
            afterShow();
        }
    };
    
    var afterShow = function(){
        if(ce){
            el.show();
            esc.enable();
            if(tm.autoDismiss && ce.autoHide !== false){
                dismissProc = setTimeout(hide, tm.autoDismissDelay);
            }
        }
    };
    
    var hide = function(noanim){
        clearTimeout(dismissProc);
        clearTimeout(hideProc);
        ce = null;
        if(el.isVisible()){
            esc.disable();
            if(noanim !== true && tm.animate){
                el.fadeOut({callback: afterHide});
            }else{
                afterHide();
            } 
        }
    };
    
    var afterHide = function(){
        el.hide();
        if(removeCls){
            el.removeClass(removeCls);
            removeCls = null;
        }
    };
    
    return {
       init : function(){
          tm = Ext.QuickTips;
          cfg = tm.tagConfig;
          if(!inited){
              el = new Ext.Layer({cls:"x-tip", shadow:"sides", shim: true, constrain:true});
              el.fxDefaults = {stopFx: true};
              el.update('<div class="x-tip-hd-left"><div class="x-tip-hd-right"><div class="x-tip-hd"></div></div></div>');
              tipTitle = Ext.get(el.dom.firstChild);
              tipTitleText = Ext.get(el.dom.firstChild.firstChild.firstChild);
              tipTitle.enableDisplayMode("block");
              tipBody = el.createChild({tag:"div", cls:"x-tip-bd"});
              close = el.createChild({tag:"div", cls:"x-tip-close"});
              close.on("click", hide);
              d = Ext.get(document);
              d.on("mousedown", onDown);
              d.on("mouseup", onUp);
              d.on("mouseover", onOver);
              d.on("mouseout", onOut);
              d.on("mousemove", onMove);
              esc = d.addKeyListener(27, hide);
              esc.disable();
              if(Ext.dd.DD){
                  dd = el.initDD("default", null, {
                      onDrag : function(){
                          el.sync();  
                      }
                  });
                  dd.setHandleElId(tipTitleText.id);
                  dd.lock();
              }
              inited = true;
          }
          this.enable(); 
       },
       
       tips : function(config){
           var cs = config instanceof Array ? config : arguments;
           for(var i = 0, len = cs.length; i < len; i++) {
               var c = cs[i];
               var target = c.target;
               if(target){
                   if(target instanceof Array){
                       for(var j = 0, jlen = target.length; j < jlen; j++){
                           tagEls[target[j]] = c;
                       }
                   }else{
                       tagEls[target] = c;
                   }               	   
               }
           }
       },
       
       enable : function(){
           if(inited){
               locks.pop();
               if(locks.length < 1){
                   disabled = false;
               }
           }
       },
       
       disable : function(){
          disabled = true;
          clearTimeout(showProc);
          clearTimeout(hideProc);
          clearTimeout(dismissProc);
          if(ce){
              hide(true);
          }
          locks.push(1);
       },

       isEnabled : function(){
            return !disabled;
       },

       tagConfig : {
           namespace : "ext",
           attribute : "qtip",
           width : "width",
           target : "target",
           title : "qtitle",
           hide : "hide",
           cls : "qclass"
       },
       
       minWidth : 75,
       maxWidth : 300,
       interceptTitles : true,
       trackMouse : false,
       hideOnClick : true,
       showDelay : 500,
       hideDelay : 200,
       autoHide : true,
       autoDismiss : true,
       autoDismissDelay : 5000,
       /**
        * True to turn on fade animation. Defaults to false (ClearType/scrollbar flicker issues in IE7).
        * @type Boolean
        */
       animate : false 
   };
}();