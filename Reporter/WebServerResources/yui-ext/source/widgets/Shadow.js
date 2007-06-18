/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.Shadow = function(config){
    Ext.apply(this, config);
    if(typeof this.mode != "string"){
        this.mode = this.defaultMode;
    }
    var o = this.offset, a = {h: 0};
    switch(this.mode.toLowerCase()){
        case "drop":
            a.w = 0;
            a.l = a.t = o;
        break;
        case "sides":
            a.w = (o*2);
            a.l = -o;
            a.t = o;
        break;
        case "frame":
            a.w = a.h = (o*2);
            a.l = a.t = -o;
        break;
    };
    this.adjusts = a;
};

Ext.Shadow.prototype = {
    defaultMode: "drop",
    offset: 4,
    show : function(target){
        target = Ext.get(target);
        if(!this.el){
            this.el = Ext.Shadow.Pool.pull();
            this.el.insertBefore(target);
        }
        this.el.setStyle("z-index", this.zIndex || parseInt(target.getStyle("z-index"), 10)-1);
        this.realign(
                target.getLeft(true),
                target.getTop(true),
                target.getWidth(),
                target.getHeight()
        );
        this.el.dom.style.display = "block";
    },

    isVisible : function(){
        return this.el ? true : false;  
    },
    /**
     * Direct alignment when values are already available. Show must be called at least once before
     * calling this method to ensure it is initialized.
     */
    realign : function(l, t, w, h){
        var a = this.adjusts, d = this.el.dom, s = d.style;
        s.left = (l+a.l)+"px";
        s.top = (t+a.t)+"px";
        var sw = (w+a.w), sh = (h+a.h), sws = sw +"px", shs = sh + "px";
        if(s.width != sws || s.height != shs){
            s.width = sws;
            s.height = shs;
            var cn = d.childNodes;
            var sww = Math.max(0, (sw-12))+"px";
            cn[0].childNodes[1].style.width = sww;
            cn[1].childNodes[1].style.width = sww;
            cn[2].childNodes[1].style.width = sww;
            cn[1].style.height = Math.max(0, (sh-12))+"px";
        }
    },

    hide : function(){
        if(this.el){
            this.el.dom.style.display = "none";
            Ext.Shadow.Pool.push(this.el);
            delete this.el;
        }
    },

    setZIndex : function(z){
        this.zIndex = z;
        if(this.el){
            this.el.setStyle("z-index", z);
        }
    }
};

Ext.Shadow.Pool = function(){
    var p = [];
    var markup = '<div class="x-shadow"><div class="xst"><div class="xstl"></div><div class="xstc"></div><div class="xstr"></div></div><div class="xsc"><div class="xsml"></div><div class="xsmc"></div><div class="xsmr"></div></div><div class="xsb"><div class="xsbl"></div><div class="xsbc"></div><div class="xsbr"></div></div></div>';
    return {
        pull : function(){
            var sh = p.shift();
            if(!sh){
                sh = Ext.get(Ext.DomHelper.insertHtml("beforeBegin", document.body.firstChild, markup));
                if(Ext.isIE && !Ext.isIE7){ //ie6 broken png
                    sh.setOpacity(.3);
                }
            }
            return sh;
        },

        push : function(sh){
            p.push(sh);
        }
    };
}();