/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.menu.MenuMgr = function(){
   var menus, active, groups = {};

   // private - called when first menu is created
   function init(){
       menus = {}, active = new Ext.util.MixedCollection();
       Ext.get(document).addKeyListener(27, function(){
           if(active.length > 0){
               hideAll();
           }
       });
   }

   function hideAll(){
       if(active.length > 0){
           var c = active.clone();
           c.each(function(m){
               m.hide();
           });
       }
   }

   function onHide(m){
       active.remove(m);
       if(active.length < 1){
           Ext.get(document).un("mousedown", onMouseDown);
       }
   }

   function onShow(m){
       var last = active.last();
       active.add(m);
       if(active.length == 1){
           Ext.get(document).on("mousedown", onMouseDown);
       }
       if(m.parentMenu){
          m.getEl().setZIndex(parseInt(m.parentMenu.getEl().getStyle("z-index"), 10) + 3);
          m.parentMenu.activeChild = m;
       }else if(last && last.isVisible()){
          m.getEl().setZIndex(parseInt(last.getEl().getStyle("z-index"), 10) + 3);
       }
   }

   function onBeforeHide(m){
       if(m.activeChild){
           m.activeChild.hide();
       }
       if(m.autoHideTimer){
           clearTimeout(m.autoHideTimer);
           delete m.autoHideTimer;
       }
   }

   function onBeforeShow(m){
       var pm = m.parentMenu;
       if(!pm && !m.allowOtherMenus){
           hideAll();
       }else if(pm && pm.activeChild){
           pm.activeChild.hide();
       }
   }

   function onMouseDown(e){
       if(active.length > 0 && !e.getTarget(".x-menu")){
           hideAll();
       }
   }

   function onBeforeCheck(mi, state){
       if(state){
           var g = groups[mi.group];
           for(var i = 0, l = g.length; i < l; i++){
               if(g[i] != mi){
                   g[i].setChecked(false);
               }
           }
       }
   }

   return {
       register : function(menu){
           if(!menus){
               init();
           }
           menus[menu.id] = menu;
           menu.on("beforehide", onBeforeHide);
           menu.on("hide", onHide);
           menu.on("beforeshow", onBeforeShow);
           menu.on("show", onShow);
           var g = menu.group;
           if(g && menu.events["checkchange"]){
               if(!groups[g]){
                   groups[g] = [];
               }
               groups[g].push(menu);
               menu.on("checkchange", onCheck);
           }
       },

       get : function(menu){
           if(typeof menu == "string"){ // menu id
               return menus[menu];
           }else if(menu.events){  // menu instance
               return menu;
           }else{ // otherwise, must be a config
               return new Ext.menu.Menu(menu);
           }
       },

       unregister : function(menu){
           delete menus[menu.id];
           menu.un("beforehide", onBeforeHide);
           menu.un("hide", onHide);
           menu.un("beforeshow", onBeforeShow);
           menu.un("show", onShow);
           var g = menu.group;
           if(g && menu.events["checkchange"]){
               groups[g].remove(menu);
               menu.un("checkchange", onCheck);
           }
       },

       registerCheckable : function(menuItem){
           var g = menuItem.group;
           if(g){
               if(!groups[g]){
                   groups[g] = [];
               }
               groups[g].push(menuItem);
               menuItem.on("beforecheckchange", onBeforeCheck);
           }
       },

       unregisterCheckable : function(menuItem){
           var g = menuItem.group;
           if(g){
               groups[g].remove(menuItem);
               menuItem.un("beforecheckchange", onBeforeCheck);
           }
       }
   };
}();
