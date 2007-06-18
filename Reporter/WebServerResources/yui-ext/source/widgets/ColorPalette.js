/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.ColorPalette = function(config){
    Ext.ColorPalette.superclass.constructor.call(this, config);
    this.addEvents({
        select: true
    });

    if(this.handler){
        this.on("select", this.handler, this.scope, true);
    }
};
Ext.extend(Ext.ColorPalette, Ext.Component, {
    itemCls : "x-color-palette",
    value : null,
    ctype: "Ext.ColorPalette",

    colors : [
        "000000", "993300", "333300", "003300", "003366", "000080", "333399", "333333",
        "800000", "FF6600", "808000", "008000", "008080", "0000FF", "666699", "808080",
        "FF0000", "FF9900", "99CC00", "339966", "33CCCC", "3366FF", "800080", "969696",
        "FF00FF", "FFCC00", "FFFF00", "00FF00", "00FFFF", "00CCFF", "993366", "C0C0C0",
        "FF99CC", "FFCC99", "FFFF99", "CCFFCC", "CCFFFF", "99CCFF", "CC99FF", "FFFFFF"
    ],

    onRender : function(container){
        var t = new Ext.MasterTemplate(
            '<tpl><a href="#" class="color-{0}" hidefocus="on"><em><span style="background:#{0}">&#160;</span></em></a></tpl>'
        );
        var c = this.colors;
        for(var i = 0, len = c.length; i < len; i++){
            t.add([c[i]]);
        }
        var el = document.createElement("div");
        el.className = this.itemCls;
        t.overwrite(el);
        container.dom.appendChild(el);
        this.el = Ext.get(el);
        this.el.on("click", this.handleClick,  this, {delegate: "a"});
    },

    afterRender : function(){
        if(this.value){
            var s = this.value;
            this.value = null;
            this.select(s);
        }
    },

    handleClick : function(e, t){
        e.preventDefault();
        if(!this.disabled){
            var c = t.className.match(/(?:^|\s)color-(.{6})(?:\s|$)/)[1];
            this.select(c.toUpperCase());
        }
    },

    select : function(color){
        color = color.replace("#", "");
        if(color != this.value){
            var el = this.el;
            if(this.value){
                el.child("a.color-"+this.value).removeClass("x-color-palette-sel");
            }
            el.child("a.color-"+color).addClass("x-color-palette-sel");
            this.value = color;
            this.fireEvent("select", this, color);
        }
    }
});