/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 */

String.prototype.ellipse = function(maxLength){
    if(this.length > maxLength){
        return this.substr(0, maxLength-3) + '...';
    }
    return this;
};

var Viewer = function(){
    // a bunch of private variables accessible by member function
    var layout, statusPanel, south, preview, previewBody, feedPanel;
    var grid, ds, sm;
    var addFeed, currentItem, tpl;
    var suggested, feeds;
    var sfeeds, myfeeds;
    var seed = 0;
    
    // feed clicks bubble up to this universal handler
    var feedClicked = function(e){
        // find the "a" element that was clicked
        var a = e.getTarget('a');
        if(a){
            e.preventDefault();
            Viewer.loadFeed(a.href);
            Viewer.changeActiveFeed(a.id.substr(5));
        }  
    };
    
    return {
        init : function(){
            // initialize state manager, we will use cookies
            Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
            
            // initialize the add feed overlay and buttons
            addFeed = Ext.get('add-feed');
            var addBtn = Ext.get('add-btn');
            addBtn.on('click', this.validateFeed, this);
            var closeBtn = Ext.get('add-feed-close');
            closeBtn.on('click', addFeed.hide, addFeed, true);
            
            // create Elements for the feed and suggested lists
            feeds = Ext.get('feeds'), suggested = Ext.get('suggested');
            
            // delegate clicks on the lists
            feeds.on('click', feedClicked);
            suggested.on('click', feedClicked);
            
            //create feed template
            tpl = new Ext.DomHelper.Template('<a id="feed-{id}" href="{url}"><span class="body">{name}<br><span class="desc">{desc}</span></span></a>');
            
            // collection of feeds added by the user
            myfeeds = {};
            
            // some default feeds
            sfeeds = {
                'jvs':{id:'jvs', name: 'JackSlocum.com', desc: 'Using the Yahoo! UI Library- Beyond the Examples.', url:'http://www.jackslocum.com/yui/feed/'},
                'ajaxian':{id:'ajaxian', name: 'Ajaxian', desc: 'Cleaning up the web with Ajax.', url:'http://feeds.feedburner.com/ajaxian'},
                'yui':{id:'yui', name: 'YUI Blog', desc: 'News and Articles about Designing and Developing with Yahoo! Libraries.', url:'http://feeds.yuiblog.com/YahooUserInterfaceBlog'},
                'sports':{id:'sports', name: 'Yahoo! Sports', desc: 'Latest news and information for the world of sports.', url:'http://sports.yahoo.com/top/rss.xml'}
            };
            
            // go through the suggested feeds and add them to the list
            for(var id in sfeeds) {
            	var f = sfeeds[id];
            	tpl.append(suggested.dom, f);
            }
            
            // create the main layout
            layout = new Ext.BorderLayout(document.body, {
                north: {
                    split:false,
                    initialSize: 25,
                    titlebar: false
                },
                west: {
                    split:true,
                    initialSize: 200,
                    minSize: 175,
                    maxSize: 400,
                    titlebar: true,
                    collapsible: true,
                    animate: true,
                    autoScroll:false,
                    useShim:true,
                    cmargins: {top:0,bottom:2,right:2,left:2}
                },
                east: {
                    split:true,
                    initialSize: 200,
                    minSize: 175,
                    maxSize: 400,
                    titlebar: true,
                    collapsible: true,
                    animate: true,
                    autoScroll:false,
                    useShim:true,
                    collapsed:true,
                    cmargins: {top:0,bottom:2,right:2,left:2}
                },
                south: {
                    split:false,
                    initialSize: 22,
                    titlebar: false,
                    collapsible: false,
                    animate: false
                },
                center: {
                    titlebar: false,
                    autoScroll:false,
                    tabPosition: 'top',
                    closeOnTab: true,
                    alwaysShowTabs: true,
                    resizeTabs: true
                }
            });
            // tell the layout not to perform layouts until we're done adding everything
            layout.beginUpdate();
            layout.add('north', new Ext.ContentPanel('header'));
            
            // initialize the statusbar
            statusPanel = new Ext.ContentPanel('status');
            south = layout.getRegion('south');
            south.add(statusPanel);
            
            // create the add feed toolbar
            var feedtb = new Ext.Toolbar('myfeeds-tb');
            feedtb.addButton({id:'add-feed-btn', text: 'Add Feed', className: 'add-feed', click: this.showAddFeed.createDelegate(this)});
            
            layout.add('west', new Ext.ContentPanel('feeds', {title: 'My Feeds', fitToFrame:true, toolbar: feedtb, resizeEl:'myfeeds-body'}));
            layout.add('east', new Ext.ContentPanel('suggested', {title: 'Suggested Feeds', fitToFrame:true}));
            
            // the inner layout houses the grid panel and the preview panel
            var innerLayout = new Ext.BorderLayout('main', {
                south: {
                    split:true,
                    initialSize: 250,
                    minSize: 100,
                    maxSize: 400,
                    autoScroll:false,
                    collapsible:true,
                    titlebar: true,
                    animate: true,
                    cmargins: {top:2,bottom:0,right:0,left:0}
                },
                center: {
                    autoScroll:false,
                    titlebar:false
                }
            });
            // add the nested layout
            feedPanel = new Ext.NestedLayoutPanel(innerLayout, 'View Feed');
            layout.add('center', feedPanel);
            
            innerLayout.beginUpdate();
            
            var lv = innerLayout.add('center', new Ext.ContentPanel('feed-grid', {title: 'Feed Articles', fitToFrame:true}));
            this.createView(lv.getEl());
            
            // create the preview panel and toolbar
            previewBody = Ext.get('preview-body');
            var tb = new Ext.Toolbar('preview-tb');
            tb.addButton({text: 'View in New Tab', className: 'view-tab', click: this.showInTab.createDelegate(this)});
            tb.addSeparator();
            tb.addButton({text: 'View in New Window', className: 'view-window', click: this.showInWindow.createDelegate(this)});
            
            preview = new Ext.ContentPanel('preview', {title: "Preview", fitToFrame:true, toolbar: tb, resizeEl:'preview-body'});
            innerLayout.add('south', preview);
            
            // restore innerLayout state
            innerLayout.restoreState();
            innerLayout.endUpdate(true);
            
            // restore any state information
            layout.restoreState();
            layout.endUpdate();
            
            // load the default feed - setTimeout for FireFox Mac?
            setTimeout(function(){
                this.loadFeed('http://www.jackslocum.com/yui/feed/');
                this.changeActiveFeed('jvs');
            }.createDelegate(this), 50);
        },
        
        createView : function(el){
            function reformatDate(feedDate){
                var d = new Date(Date.parse(feedDate));
                return d ? d.dateFormat('D M j, Y, g:i a') : '';
            }
            
            var reader = new Ext.data.XmlReader({record: 'item'},
                ['title', {name:'pubDate', type:'date'}, 'link', 'description']
            );

            ds = new Ext.data.Store({
                proxy: new Ext.data.HttpProxy({
                    url: 'feed-proxy.php'
                }),
                reader : reader
            });
            ds.on('load', this.onLoad, this);
            
            var tpl = new Ext.Template(
                  '<div class="feed-item">' +
                  '<div class="item-title">{title}</div>' +
                  '<div class="item-date">{date}</div>' +
                  '{desc}</div>'
            );
            
            var view = new Ext.View(el, tpl, {store: ds, singleSelect:true, selectedClass:'selected-article'});
            /*view.prepareData = function(data){
                return {
                    title: data[0],
                    date: data[1],
                    desc: data[3].replace(/<\/?[^>]+>/gi, '').ellipse(350)
                };
            };*/
            view.on('click', this.showPost, this);
            view.on('dblclick', this.showFullPost, this);
        },
        
        onLoad : function(){
            if(ds.getCount() < 1){
        		preview.setContent('');
        	}
            statusPanel.getEl().addClass('done');
            statusPanel.setContent('Done.');
        },
        
        loadFeed : function(feed){
        	statusPanel.setContent('Loading feed ' + feed + '...');
        	statusPanel.getEl().removeClass('done');
            ds.load({'feed': feed});
        },
        
        showPost : function(view, dataIndex){
            var post = ds.getAt(dataIndex);
            
            var node = dm.getNode(dataIndex);
    		var link = dm.getNamedValue(node, 'link');
    		var title = dm.getValueAt(dataIndex, 0);
    		var desc = dm.getNamedValue(node, 'description', 'No Description Available.');
    		currentItem = {
    		    index: dataIndex, link: link
    		};
    		preview.setTitle(title.ellipse(80));
    		previewBody.update(desc);
        },
        
        showFullPost : function(view, rowIndex){
            var node = dm.getNode(rowIndex);
    		var link = dm.getNamedValue(node, 'link');
    		var title = dm.getValueAt(rowIndex, 0);
    		if(!title){
    		    title = 'View Post';
    		}
    		var iframe = Ext.DomHelper.append(document.body, 
    		            {tag: 'iframe', frameBorder: 0, src: link});
    		var panel = new Ext.ContentPanel(iframe, 
    		            {title: title, fitToFrame:true, closable:true});
    		layout.add('center', panel);     	
        },
        
        showInTab : function(){
            if(currentItem){
                this.showFullPost(grid, currentItem.index);
            }
        },
        
        showInWindow : function(){
            if(currentItem){
                window.open(currentItem.link, 'win');
            }
        },
        
        changeActiveFeed : function(feedId){
            suggested.select('a').removeClass('selected');
            feeds.select('a').removeClass('selected');
            Ext.fly('feed-'+feedId).addClass('selected');
            var feed = sfeeds[feedId] || myfeeds[feedId];
            feedPanel.setTitle('View Feed (' + feed.name.ellipse(16) + ')');
        },
        
        showAddFeed : function(){
            Ext.get('feed-url').dom.value = '';
            Ext.get('add-title').radioClass('active-msg');
            addFeed.alignTo('add-feed-btn', 'tl', [3,3])
            addFeed.show();
        },
        
        validateFeed : function(){
            var url = Ext.get('feed-url').dom.value;
            Ext.get('loading-feed').radioClass('active-msg');
            var success = function(o){
                try{
                    var xml = o.responseXML;
                    var channel = xml.getElementsByTagName('channel')[0];
                    var titleEl = channel.getElementsByTagName('title')[0];
                    var descEl = channel.getElementsByTagName('description')[0];
                    var name = titleEl.firstChild.nodeValue;
                    var desc = (descEl.firstChild ? descEl.firstChild.nodeValue : '');
                    var id = ++seed;
                    myfeeds[id] = {id:id, name:name, desc:desc, url:url};
                    tpl.append('myfeeds-body', myfeeds[id]);
                    this.changeActiveFeed(id);
                    addFeed.hide();
                    dm.loadData(xml);
                }catch(e){
                    Ext.get('invalid-feed').radioClass('active-msg');
                }                     
            }.createDelegate(this);
            var failure = function(o){
                Ext.get('invalid-feed').radioClass('active-msg');
            };
            Ext.lib.Ajax.request('POST', 'feed-proxy.php', {success:success, failure:failure}, 'feed='+encodeURIComponent(url));
        }
    };
}();
Ext.onReady(Viewer.init, Viewer);