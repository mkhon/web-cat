/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.BorderLayout
 * @extends Ext.LayoutManager
 * This class represents a common layout manager used in desktop applications. For screenshots and more details,
 * please see: <br><br>
 * <a href="http://www.jackslocum.com/yui/2006/10/19/cross-browser-web-20-layouts-with-yahoo-ui/">Cross Browser Layouts - Part 1</a><br>
 * <a href="http://www.jackslocum.com/yui/2006/10/28/cross-browser-web-20-layouts-part-2-ajax-feed-viewer-20/">Cross Browser Layouts - Part 2</a><br><br>
 * Example:
 <pre><code>
 var layout = new Ext.BorderLayout(document.body, {
    north: {
        initialSize: 25,
        titlebar: false
    },
    west: {
        split:true,
        initialSize: 200,
        minSize: 175,
        maxSize: 400,
        titlebar: true,
        collapsible: true
    },
    east: {
        split:true,
        initialSize: 202,
        minSize: 175,
        maxSize: 400,
        titlebar: true,
        collapsible: true
    },
    south: {
        split:true,
        initialSize: 100,
        minSize: 100,
        maxSize: 200,
        titlebar: true,
        collapsible: true
    },
    center: {
        titlebar: true,
        autoScroll:true,
        resizeTabs: true,
        minTabWidth: 50,
        preferredTabWidth: 150
    }
});

// shorthand
var CP = Ext.ContentPanel;

layout.beginUpdate();
layout.add("north", new CP("north", "North"));
layout.add("south", new CP("south", {title: "South", closable: true}));
layout.add("west", new CP("west", {title: "West"}));
layout.add("east", new CP("autoTabs", {title: "Auto Tabs", closable: true}));
layout.add("center", new CP("center1", {title: "Close Me", closable: true}));
layout.add("center", new CP("center2", {title: "Center Panel", closable: false}));
layout.getRegion("center").showPanel("center1");
layout.endUpdate();
</code></pre>

<b>The container the layout is rendered into can be either the body element or any other element.
If it is not the body element, the element needs to either be an absolute positioned element,
or you will need to add "position:relative" to the css of the element.</b>

* @constructor
* Create a new BorderLayout
* @param {String/HTMLElement/Element} container The container this layout is bound to
* @param {Object} config Configuration options
 */
Ext.BorderLayout = function(container, config){
    config = config || {};
    Ext.BorderLayout.superclass.constructor.call(this, container);
    this.factory = config.factory || Ext.BorderLayout.RegionFactory;
    for(var i = 0, len = this.factory.validRegions.length; i < len; i++) {
    	var target = this.factory.validRegions[i];
    	if(config[target]){
    	    this.addRegion(target, config[target]);
    	}
    }
};

Ext.extend(Ext.BorderLayout, Ext.LayoutManager, {
    /**
     * Creates and adds a new region if it doesn't already exist.
     * @param {String} target The target region key (north, south, east, west or center).
     * @param {Object} config The regions config object
     * @return {BorderLayoutRegion} The new region
     */
    addRegion : function(target, config){
        if(!this.regions[target]){
            var r = this.factory.create(target, this, config);
    	    this.regions[target] = r;
    	    r.on("visibilitychange", this.layout, this);
            r.on("paneladded", this.layout, this);
            r.on("panelremoved", this.layout, this);
            r.on("invalidated", this.layout, this);
            r.on("resized", this.onRegionResized, this);
            r.on("collapsed", this.onRegionCollapsed, this);
            r.on("expanded", this.onRegionExpanded, this);
        }
        return this.regions[target];
    },
    
    /**
     * Performs a layout update.
     */
    layout : function(){
        if(this.updating) return;
        var size = this.getViewSize();
        var w = size.width, h = size.height;
        var centerW = w, centerH = h, centerY = 0, centerX = 0;
        //var x = 0, y = 0;
        
        var rs = this.regions;
        var n = rs["north"], s = rs["south"], west = rs["west"], e = rs["east"], c = rs["center"];
        if(this.hideOnLayout){
            c.el.setStyle("display", "none");
        }
        if(n && n.isVisible()){
            var b = n.getBox();
            var m = n.getMargins();
            b.width = w - (m.left+m.right);
            b.x = m.left;
            b.y = m.top;
            centerY = b.height + b.y + m.bottom;
            centerH -= centerY;
            n.updateBox(this.safeBox(b));
        }
        if(s && s.isVisible()){
            var b = s.getBox();
            var m = s.getMargins();
            b.width = w - (m.left+m.right);
            b.x = m.left;
            var totalHeight = (b.height + m.top + m.bottom);
            b.y = h - totalHeight + m.top;
            centerH -= totalHeight;
            s.updateBox(this.safeBox(b));
        }
        if(west && west.isVisible()){
            var b = west.getBox();
            var m = west.getMargins();
            b.height = centerH - (m.top+m.bottom);
            b.x = m.left;
            b.y = centerY + m.top;
            var totalWidth = (b.width + m.left + m.right);
            centerX += totalWidth;
            centerW -= totalWidth;
            west.updateBox(this.safeBox(b));
        }
        if(e && e.isVisible()){
            var b = e.getBox();
            var m = e.getMargins();
            b.height = centerH - (m.top+m.bottom);
            var totalWidth = (b.width + m.left + m.right);
            b.x = w - totalWidth + m.left;
            b.y = centerY + m.top;
            centerW -= totalWidth;
            e.updateBox(this.safeBox(b));
        }
        if(c){
            var m = c.getMargins();
            var centerBox = {
                x: centerX + m.left,
                y: centerY + m.top,
                width: centerW - (m.left+m.right),
                height: centerH - (m.top+m.bottom)
            };
            if(this.hideOnLayout){
                c.el.setStyle("display", "block");
            }
            c.updateBox(this.safeBox(centerBox));
        }
        this.el.repaint();
        this.fireEvent("layout", this);
    },
    
    safeBox : function(box){
        box.width = Math.max(0, box.width);
        box.height = Math.max(0, box.height);
        return box;
    },
    
    /**
     * Adds a ContentPanel (or subclass) to this layout.
     * @param {String} target The target region key (north, south, east, west or center).
     * @param {Ext.ContentPanel} panel The panel to add
     * @return {Ext.ContentPanel} The added panel
     */
    add : function(target, panel){
        target = target.toLowerCase();
        return this.regions[target].add(panel);
    },
    
    /**
     * Remove a ContentPanel (or subclass) to this layout.
     * @param {String} target The target region key (north, south, east, west or center).
     * @param {Number/String/Ext.ContentPanel} panel The index, id or panel to remove
     * @return {Ext.ContentPanel} The removed panel
     */
    remove : function(target, panel){
        target = target.toLowerCase();
        return this.regions[target].remove(panel);
    },
    
    /**
     * Searches all regions for a panel with the specified id
     * @param {String} panelId
     * @return {Ext.ContentPanel} The panel or null if it wasn't found
     */
    findPanel : function(panelId){
        var rs = this.regions;
        for(var target in rs){
            if(typeof rs[target] != "function"){
                var p = rs[target].getPanel(panelId);
                if(p){
                    return p;
                }
            }
        }
        return null;
    },
    
    /**
     * Searches all regions for a panel with the specified id and activates (shows) it.
     * @param {String/ContentPanel} panelId The panels id or the panel itself
     * @return {Ext.ContentPanel} The shown panel or null
     */
    showPanel : function(panelId) {
      var rs = this.regions;
      for(var target in rs){
         var r = rs[target];
         if(typeof r != "function"){
            if(r.hasPanel(panelId)){
               return r.showPanel(panelId);
            }
         }
      }
      return null;
   },
   
   /**
     * Restores this layouts state using Ext.state.Manager or the state provided by the passed provider.
     * @param {Ext.state.Provider} provider (optional) An alternate state provider
     */
    restoreState : function(provider){
        if(!provider){
            provider = Ext.state.Manager;
        }
        var sm = new Ext.LayoutStateManager();
        sm.init(this, provider);
    }
});

Ext.BorderLayout.create = function(config, targetEl){
    var layout = new Ext.BorderLayout(targetEl || document.body, config);
    layout.beginUpdate();
    var regions = Ext.BorderLayout.RegionFactory.validRegions;
    for(var j = 0, jlen = regions.length; j < jlen; j++){
        var lr = regions[j];
        if(layout.regions[lr] && config[lr].panels){
            var r = layout.regions[lr];
            var ps = config[lr].panels;
            for(var i =0, len = ps.length; i < len; i++){
                var p = ps[i];
                if(typeof p == "string"){
                    r.add(new Ext.ContentPanel(p)); // raw dom id
                }else if(!p.events){ // raw config object?
                    var el = p.el;
                    delete p.el; // prevent conflict
                    r.add(new Ext.ContentPanel(el, p));
                }else{
                    r.add(p); // panel
                }
            }
        }
    }
    layout.endUpdate();
    return layout;
};

Ext.BorderLayout.RegionFactory = {
    validRegions : ["north","south","east","west","center"],
    
    create : function(target, mgr, config){
        target = target.toLowerCase();
        if(config.lightweight || config.basic){
            return new Ext.BasicLayoutRegion(mgr, config, target);
        }
        switch(target){
            case "north":
                return new Ext.NorthLayoutRegion(mgr, config);
            case "south":
                return new Ext.SouthLayoutRegion(mgr, config);
            case "east":
                return new Ext.EastLayoutRegion(mgr, config);
            case "west":
                return new Ext.WestLayoutRegion(mgr, config);
            case "center":
                return new Ext.CenterLayoutRegion(mgr, config);
        }
        throw 'Layout region "'+target+'" not supported.';
    }
};