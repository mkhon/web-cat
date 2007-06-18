/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.Layer
 * @extends Ext.Element
 * An extended Element object that supports a shadow and shim, constrain to viewport and
 * automatic maintaining of shadow/shim positions.
 * @cfg {Boolean} shim False to disable the iframe shim in browsers which need one (defaults to true)
 * @cfg {String/Boolean} shadow True to create a shadow element with default class "x-layer-shadow" or
 * you can pass a string with a css class name. False turns off the shadow.
 * @cfg {Object} dh DomHelper object config to create element with (defaults to {tag: "div", cls: "x-layer"}).
 * @cfg {Boolean} constrain False to disable constrain to viewport (defaults to true)
 * @cfg {String} cls CSS class to add to the element
 * @cfg {Number} zindex Starting z-index (defaults to 11000!)
 * @cfg {Number} shadowOffset Offset for the shadow (defaults to 3)
 * @constructor
 * @param {Object} config
 * @param {String/HTMLElement} existingEl (optional) Uses an existing dom element. If the element is not found it creates it.
 */
(function(){ 
Ext.Layer = function(config, existingEl){
    config = config || {};
    var dh = Ext.DomHelper;
    var cp = config.parentEl, pel = cp ? Ext.getDom(cp) : document.body;
    if(existingEl){
        this.dom = Ext.getDom(existingEl);
    }
    if(!this.dom){
        var o = config.dh || {tag: "div", cls: "x-layer"};
        this.dom = dh.append(pel, o);
    }
    if(config.cls){
        this.addClass(config.cls);
    }
    this.constrain = config.constrain !== false;
    this.visibilityMode = Ext.Element.VISIBILITY;
    if(config.id){
        this.id = this.dom.id = config.id;
    }else{
        this.id = Ext.id(this.dom);
    }
    var zindex = (config.zindex || parseInt(this.getStyle("z-index"), 10)) || 11000;
    this.position("absolute", zindex);
    if(config.shadow){
        this.shadowOffset = config.shadowOffset || 4;
        this.shadow = new Ext.Shadow({
            offset : this.shadowOffset,
            mode : config.shadow
        });
    }else{
        this.shadowOffset = 0;
    }
    if(config.shim !== false && Ext.useShims){
        this.shim = this.createShim();
        this.shim.setOpacity(0);
        this.shim.position("absolute", zindex-2);
    }
    this.useDisplay = config.useDisplay;
    this.hide();
};

var supr = Ext.Element.prototype;

Ext.extend(Ext.Layer, Ext.Element, {
    // this code can execute repeatedly in milliseconds (i.e. during a drag) so
    // code size was sacrificed for effeciency (e.g. no getBox/setBox, no XY calls)
    sync : function(doShow){
        var sw = this.shadow, sh = this.shim;
        if(this.isVisible() && (sw || sh)){
            var w = this.getWidth(),
                h = this.getHeight();

            var l = this.getLeft(true),
                t = this.getTop(true);

            if(sw){
                if(doShow && !sw.isVisible()){
                    sw.show(this);
                }else{
                    sw.realign(l, t, w, h);
                }
                if(sh){
                    if(doShow){
                       sh.show();
                    }
                    // fit the shim behind the shadow, so it is shimmed too
                    var a = sw.adjusts, s = sh.dom.style;
                    s.left = (l+a.l)+"px";
                    s.top = (t+a.t)+"px";
                    s.width = (w+a.w)+"px";
                    s.height = (h+a.h)+"px";
                }
            }else if(sh){
                if(doShow){
                   sh.show();
                }
                sh.setSize(w, h);
                sh.setLeftTop(l, t);
            }
            
        }
    },
    
    hideUnders : function(negOffset){
        if(this.shadow){
            this.shadow.hide();
        }
        if(this.shim){
           this.shim.hide();
           if(negOffset){
               this.shim.setLeftTop(-10000,-10000);
           }
        }
    },
    
    constrainXY : function(){
        if(this.constrain){
            var vw = Ext.lib.Dom.getViewWidth(),
                vh = Ext.lib.Dom.getViewHeight();
            var s = Ext.get(document).getScroll();

            xy = this.getXY();
            var x = xy[0], y = xy[1];   
            var w = this.dom.offsetWidth+this.shadowOffset, h = this.dom.offsetHeight+this.shadowOffset;
            // only move it if it needs it
            var moved = false;
            // first validate right/bottom
            if((x + w) > vw+s.left){
                x = vw - w - this.shadowOffset;
                moved = true;
            }
            if((y + h) > vh+s.top){
                y = vh - h - this.shadowOffset;
                moved = true;
            }
            // then make sure top/left isn't negative
            if(x < s.left){
                x = s.left;
                moved = true;
            }
            if(y < s.top){
                y = s.top;
                moved = true;
            }
            if(moved){
                xy = [x, y];
                this.lastXY = xy;
                supr.setXY.call(this, xy);
                this.sync();
            }
        }
    },

    showAction : function(){
        if(this.useDisplay === true){
            this.setDisplayed("");
        }else if(this.lastXY){
            supr.setXY.call(this, this.lastXY);
        }
    },

    hideAction : function(){
        if(this.useDisplay === true){
            this.setDisplayed(false);
        }else{
            this.setLeftTop(-10000,-10000);
        }
    },

    setVisible : function(v, a, d, c, e){
        this.showAction();
        if(a && v){
            var cb = function(){
                this.sync(true);
                if(c){
                    c();
                }
            }.createDelegate(this);
            supr.setVisible.call(this, true, true, d, cb, e);
        }else{
            if(!v){
                this.hideUnders(true);
            }
            var cb = c;
            if(a){
                cb = function(){
                    this.hideAction();
                    if(c){
                        c();
                    }
                }.createDelegate(this);
            }
            supr.setVisible.call(this, v, a, d, cb, e);
            if(v){
                this.sync(true);
            }else if(!a){
                this.hideAction();
            }
        }
    },

    beforeFx : function(){
        this.beforeAction();
        return Ext.Layer.superclass.beforeFx.apply(this, arguments);
    },

    afterFx : function(){
        Ext.Layer.superclass.afterFx.apply(this, arguments);
        this.sync(this.isVisible());
    },

    beforeAction : function(){
        if(this.shadow){
            this.shadow.hide();
        }
    },
    
    setXY : function(xy, a, d, c, e){
        this.fixDisplay();
        this.beforeAction();
        this.lastXY = xy;
        var cb = this.createCB(c);
        supr.setXY.call(this, xy, a, d, cb, e);
        if(!a){
            cb();
        }
    },
    
    createCB : function(c){
        var el = this;
        return function(){
            el.constrainXY();
            el.sync(true);
            if(c){
                c();
            }
        };
    },
    
    setX : function(x, a, d, c, e){
        this.setXY([x, this.getY()], a, d, c, e);
    },
    
    setY : function(y, a, d, c, e){
        this.setXY([this.getX(), y], a, d, c, e);
    },
    
    setSize : function(w, h, a, d, c, e){
        this.beforeAction();
        var cb = this.createCB(c);
        supr.setSize.call(this, w, h, a, d, cb, e);
        if(!a){
            cb();
        }
    },
    
    setWidth : function(w, a, d, c, e){
        this.beforeAction();
        var cb = this.createCB(c);
        supr.setWidth.call(this, w, a, d, cb, e);
        if(!a){
            cb();
        }
    },
    
    setHeight : function(h, a, d, c, e){
        this.beforeAction();
        var cb = this.createCB(c);
        supr.setHeight.call(this, h, a, d, cb, e);
        if(!a){
            cb();
        }
    },
    
    setBounds : function(x, y, w, h, a, d, c, e){
        this.beforeAction();
        var cb = this.createCB(c);
        if(!a){
            supr.setXY.call(this, [x, y]);
            supr.setSize.call(this, w, h, a, d, cb, e);
            cb();
        }else{
            supr.setBounds.call(this, x, y, w, h, a, d, cb, e);
        }
        return this;
    },
    
    /**
     * Set the z-index of this layer and adjusts shadow and shims z-index. The result zindex is zindex + 2.
     * @param {Number} zindex
     * @return {this}
     */
    setZIndex : function(zindex){
        this.setStyle("z-index", zindex + 2);
        if(this.shadow){
            this.shadow.setZIndex(zindex + 1);
        }
        if(this.shim){
            this.shim.setStyle("z-index", zindex);
        }
    }
});
})();