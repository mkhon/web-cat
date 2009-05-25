/*==========================================================================*\
 |  $Id: ContentPane.js,v 1.3 2009/05/25 16:57:26 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2009 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU Affero General Public License as published
 |  by the Free Software Foundation; either version 3 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU Affero General Public License
 |  along with Web-CAT; if not, see <http://www.gnu.org/licenses/>.
\*==========================================================================*/

dojo.provide("webcat.ContentPane");

dojo.require("dijit.layout.ContentPane");

// ------------------------------------------------------------------------
/**
 * The webcat.ContentPane is a minor hack to dijit.layout.ContentPane that
 * fixes an issue that helps it fit better into the WebObjects model. The
 * intention is that the content pane will be filled with component content
 * that is to be refreshed when the refresh() method is called. The refresh()
 * method requires that the "href" attribute be set on the pane (which is
 * obtained from the Wonder Ajax request handler), but doing so causes Dojo to
 * replace the pane's content with the content at the href when the page is
 * loaded (so essentially, the content is loaded twice). This subclass patches
 * this behavior so that the pane's content is NOT loaded from the remote URL
 * when it is initialized.
 *
 * @author Tony Allevato
 * @version $Id: ContentPane.js,v 1.3 2009/05/25 16:57:26 aallowat Exp $
 */
dojo.declare("webcat.ContentPane", dijit.layout.ContentPane,
{
	//~ Properties ............................................................
	
	/* Set to true when the pane is initially starting up, otherwise false. */
	initialStartup: true,
	
	/* Web-CAT's content pane by default does NOT show a loading message when
	   it is being updated, to behave more like the old-style Prototype
	   AjaxUpdateContainers (that is, modifications are made in place and
	   appear instant). Set this attribute to true if you want to show a
	   loading message when this pane is refreshed (for example, to indicate
	   a long-running operation). */
	showsLoadingMessageOnRefresh: false,


	//~ Methods ...............................................................

	// ----------------------------------------------------------
	startup: function()
	{
		this.initialStartup = true;
		this.inherited(arguments);
		this.initialStartup = false;
	},
	
	
	// ----------------------------------------------------------
	_loadCheck: function(/* Boolean */ forceLoad)
	{
		if (!this.initialStartup)
		{
			this.inherited(arguments);
		}
	},


    // ----------------------------------------------------------
    _setContent: function(cont, isFakeContent)
    {
// BEGIN WEBCAT CHANGES
        if (isFakeContent && !this.showsLoadingMessageOnRefresh)
            return;
// END WEBCAT CHANGES

        // first get rid of child widgets
        this.destroyDescendants();

        // Delete any state information we have about current contents
        delete this._singleChild;

        // dojo.html.set will take care of the rest of the details
        // we provide an overide for the error handling to ensure the widget gets the errors 
        // configure the setter instance with only the relevant widget instance properties
        // NOTE: unless we hook into attr, or provide property setters for each property, 
        // we need to re-configure the ContentSetter with each use
        var setter = this._contentSetter; 
        if(! (setter && setter instanceof dojo.html._ContentSetter)) {
            setter = this._contentSetter = new dojo.html._ContentSetter({
                node: this.containerNode,
                _onError: dojo.hitch(this, this._onError),
// BEGIN WEBCAT CHANGES
                onEnd: function() {
                    // Run scripts that aren't of type "dojo/..." before the
                    // widgets are parsed.

                    dojo.query("script", this.node).forEach(function(n) {
                        if (!/dojo\//.test(n.type))
                        {
                            dojo.eval(dojox.data.dom.textContent(n));
                        }
                    });

                    if(this.parseContent){
                        // populates this.parseResults if you need those..
                        this._parse();
                    }

                    return this.node; /* DomNode */
                },
// END WEBCAT CHANGES
                onContentError: dojo.hitch(this, function(e){
                    // fires if a domfault occurs when we are appending this.errorMessage
                    // like for instance if domNode is a UL and we try append a DIV
                    var errMess = this.onContentError(e);
                    try{
                        this.containerNode.innerHTML = errMess;
                    }catch(e){
                        console.error('Fatal '+this.id+' could not change content due to '+e.message, e);
                    }
                })/*,
                _onError */
            });
        };

        var setterParams = dojo.mixin({
            cleanContent: this.cleanContent, 
            extractContent: this.extractContent, 
            parseContent: this.parseOnLoad 
        }, this._contentSetterParams || {});
        
        dojo.mixin(setter, setterParams); 

        setter.set( (dojo.isObject(cont) && cont.domNode) ? cont.domNode : cont );

        // setter params must be pulled afresh from the ContentPane each time
        delete this._contentSetterParams;

        if(!isFakeContent){
            dojo.forEach(this.getChildren(), function(child){
                child.startup();
            });

            if(this.doLayout){
                this._checkIfSingleChild();
            }

            // Call resize() on each of my child layout widgets,
            // or resize() on my single child layout widget...
            // either now (if I'm currently visible)
            // or when I become visible
            this._scheduleLayout();
            
            this._onLoadHandler(cont);
        }
    }
});


// ------------------------------------------------------------------------
/**
 * An inline content pane.
 *
 * @author Tony Allevato
 */
dojo.declare("webcat.ContentSpan", webcat.ContentPane,
{
    baseClass: ""
});
