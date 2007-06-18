/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.grid.Grid
 * @extends Ext.util.Observable
 * This class represents the primary interface of a component based grid control.
 * <br><br>Usage:<pre><code>
 var grid = new Ext.grid.Grid("my-container-id", dataSource, columnModel);
 // set any options
 grid.render();
 // or using a config
 var grid = new Ext.grid.Grid("my-container-id", {
     dataModel: myDataModel,
     colModel: myColModel,
     selModel: mySelectionModel,
     autoSizeColumns: true,
     monitorWindowResize: false,
     trackMouseOver: true
 }).render();
 * </code></pre>
 * <b>Common Problems:</b><br/>
 * - Grid does not resize properly when going smaller: Setting overflow hidden on the container 
 * element will correct this<br/>
 * - If you get el.style[camel]= NaNpx or -2px or something related, be certain you have given your container element 
 * dimensions. The grid adapts to your container's size, if your container has no size defined then the results
 * are unpredictable.<br/>
 * - Do not render the grid into an element with display:none. Try using visibility:hidden. Otherwise there is no way for the 
 * grid to calculate dimensions/offsets.<br/>
  * @constructor
 * @param {String/HTMLElement/Ext.Element} container The element into which this grid will be rendered - 
 * The container MUST have some type of size defined for the grid to fill. The container will be 
 * automatically set to position relative if it isn't already.
 * @param {Object} config A config object that sets properties on this grid OR the data model to bind to
 * @param {Object} colModel (optional) The column model with info about this grid's columns
 * @param {Object} selectionModel (optional) The selection model for this grid (defaults to DefaultSelectionModel)
 */
Ext.grid.Grid = function(container, config){
	// initialize the container
	this.container = Ext.get(container);
	this.container.update("");
	this.container.setStyle("overflow", "hidden");
    this.container.addClass('x-grid-container');
    
    this.id = this.container.id;
	
    Ext.apply(this, config);
    // check and correct shorthanded configs
    if(this.ds){
        this.dataSource = this.ds;
        delete this.ds;
    }
    if(this.cm){
        this.colModel = this.cm;
        delete this.cm;
    }
    if(this.sm){
        this.selModel = this.sm;
        delete this.sm;
    }

    if(this.width){
        this.container.setWidth(this.width);
    }

    if(this.height){
        this.container.setHeight(this.height);
    }
    /** @private */
	this.events = {
	    // raw events
	    /**
	     * @event click
	     * The raw click event for the entire grid.
	     * @param {Ext.EventObject} e
	     */
	    "click" : true,
	    /**
	     * @event dblclick
	     * The raw dblclick event for the entire grid.
	     * @param {Ext.EventObject} e
	     */
	    "dblclick" : true,
	    /**
	     * @event contextmenu
	     * The raw contextmenu event for the entire grid.
	     * @param {Ext.EventObject} e
	     */
	    "contextmenu" : true,
	    /**
	     * @event mousedown
	     * The raw mousedown event for the entire grid.
	     * @param {Ext.EventObject} e
	     */
	    "mousedown" : true,
	    /**
	     * @event mouseup
	     * The raw mouseup event for the entire grid.
	     * @param {Ext.EventObject} e
	     */
	    "mouseup" : true,
	    /**
	     * @event mouseover
	     * The raw mouseover event for the entire grid.
	     * @param {Ext.EventObject} e
	     */
	    "mouseover" : true,
	    /**
	     * @event mouseout
	     * The raw mouseout event for the entire grid.
	     * @param {Ext.EventObject} e
	     */
	    "mouseout" : true,
	    /**
	     * @event keypress
	     * The raw keypress event for the entire grid.
	     * @param {Ext.EventObject} e
	     */
	    "keypress" : true,
	    /**
	     * @event keydown
	     * The raw keydown event for the entire grid.
	     * @param {Ext.EventObject} e
	     */
	    "keydown" : true,
	    
	    // custom events
	    
	    /**
	     * @event cellclick
	     * Fires when a cell is clicked
	     * @param {Grid} this
	     * @param {Number} rowIndex
	     * @param {Number} columnIndex
	     * @param {Ext.EventObject} e
	     */
	    "cellclick" : true,
	    /**
	     * @event celldblclick
	     * Fires when a cell is double clicked
	     * @param {Grid} this
	     * @param {Number} rowIndex
	     * @param {Number} columnIndex
	     * @param {Ext.EventObject} e
	     */
	    "celldblclick" : true,
	    /**
	     * @event rowclick
	     * Fires when a row is clicked
	     * @param {Grid} this
	     * @param {Number} rowIndex
	     * @param {Ext.EventObject} e
	     */
	    "rowclick" : true,
	    /**
	     * @event rowdblclick
	     * Fires when a row is double clicked
	     * @param {Grid} this
	     * @param {Number} rowIndex
	     * @param {Ext.EventObject} e
	     */
	    "rowdblclick" : true,
	    /**
	     * @event headerclick
	     * Fires when a header is clicked
	     * @param {Grid} this
	     * @param {Number} columnIndex
	     * @param {Ext.EventObject} e
	     */
	    "headerclick" : true,
	    /**
	     * @event headerdblclick
	     * Fires when a header cell is double clicked
	     * @param {Grid} this
	     * @param {Number} rowIndex
	     * @param {Number} columnIndex
	     * @param {Ext.EventObject} e
	     */
	    "headerdblclick" : true,
	    /**
	     * @event rowcontextmenu
	     * Fires when a row is right clicked
	     * @param {Grid} this
	     * @param {Number} rowIndex
	     * @param {Ext.EventObject} e
	     */
	    "rowcontextmenu" : true,
	    /**
         * @event cellcontextmenu
         * Fires when a cell is right clicked
         * @param {Grid} this
         * @param {Number} rowIndex
         * @param {Number} cellIndex
         * @param {Ext.EventObject} e
         */
         "cellcontextmenu" : true,
	    /**
	     * @event headercontextmenu
	     * Fires when a header is right clicked
	     * @param {Grid} this
	     * @param {Number} columnIndex
	     * @param {Ext.EventObject} e
	     */
	    "headercontextmenu" : true,
	    /**
	     * @event bodyscroll
	     * Fires when the body element is scrolled
	     * @param {Number} scrollLeft
	     * @param {Number} scrollTop
	     */
	    "bodyscroll" : true,
	    /**
	     * @event columnresize
	     * Fires when the user resizes a column
	     * @param {Number} columnIndex
	     * @param {Number} newSize
	     */
	    "columnresize" : true,
	    /**
	     * @event columnmove
	     * Fires when the user moves a column
	     * @param {Number} oldIndex
	     * @param {Number} newIndex
	     */
	    "columnmove" : true,
	    /**
	     * @event startdrag
	     * Fires when row(s) start being dragged 
	     * @param {Grid} this
	     * @param {Ext.GridDD} dd The drag drop object
	     * @param {event} e The raw browser event
	     */
	    "startdrag" : true,
	    /**
	     * @event enddrag
	     * Fires when a drag operation is complete
	     * @param {Grid} this
	     * @param {Ext.GridDD} dd The drag drop object
	     * @param {event} e The raw browser event
	     */
	    "enddrag" : true,
	    /**
	     * @event dragdrop
	     * Fires when dragged row(s) are dropped on a valid DD target 
	     * @param {Grid} this
	     * @param {Ext.GridDD} dd The drag drop object
	     * @param {String} targetId The target drag drop object
	     * @param {event} e The raw browser event
	     */
	    "dragdrop" : true,
	    /**
	     * @event dragover
	     * Fires while row(s) are being dragged. "targetId" is the id of the Yahoo.util.DD object the selected rows are being dragged over.
	     * @param {Grid} this
	     * @param {Ext.GridDD} dd The drag drop object
	     * @param {String} targetId The target drag drop object
	     * @param {event} e The raw browser event
	     */
	    "dragover" : true,
	    /**
	     * @event dragenter
	     *  Fires when the dragged row(s) first cross another DD target while being dragged 
	     * @param {Grid} this
	     * @param {Ext.GridDD} dd The drag drop object
	     * @param {String} targetId The target drag drop object
	     * @param {event} e The raw browser event
	     */
	    "dragenter" : true,
	    /**
	     * @event dragout
	     * Fires when the dragged row(s) leave another DD target while being dragged 
	     * @param {Grid} this
	     * @param {Ext.GridDD} dd The drag drop object
	     * @param {String} targetId The target drag drop object
	     * @param {event} e The raw browser event
	     */
	    "dragout" : true
	};
};
Ext.extend(Ext.grid.Grid, Ext.util.Observable, {
    /** The minimum width a column can be resized to. (Defaults to 25)
	 * @type Number */
	minColumnWidth : 25,
	
	/** True to automatically resize the columns to fit their content <b>on initial render</b>
	 * @type Boolean */
	autoSizeColumns : false,
	
	/** True to measure headers with column data when auto sizing columns
	 * @type Boolean */
	autoSizeHeaders : true,
	
	/**
	 * True to autoSize the grid when the window resizes - defaults to true
	 */
	monitorWindowResize : true,
	
	/** If autoSizeColumns is on, maxRowsToMeasure can be used to limit the number of
	 * rows measured to get a columns size - defaults to 0 (all rows).
	 * @type Number */
	maxRowsToMeasure : 0,
	
	/** True to highlight rows when the mouse is over (default is false)
	 * @type Boolean */
	trackMouseOver : true,
	
	/** True to enable drag and drop of rows
	 * @type Boolean */
	enableDragDrop : false,
	
	/**
	 * True to enable drag and drop reorder of columns
	 * @type Boolean
	 */
	enableColumnMove : true,
	
	/** True to manually sync row heights across locked and not locked rows @type Boolean **/
	enableRowHeightSync : false,
	
	/** True to stripe the rows (default is true)
	 * @type Boolean */
	stripeRows : true,
	/** True to fit the height of the grid container to the height of the data (defaults to false)
	 * @type Boolean */
	autoHeight : false,

    /**
    * The id of a column in this grid that should expand to fill unused space
    * @type {String}
    */
    autoExpandColumn : false,

    /**
	 * The view used by the grid. This can be set before a call to render(). 
	 * Defaults to a Ext.grid.GridView or PagedGridView depending on the data model.
	 * @type Object
	 */
	view : null,
	
	/** A regular expression defining tagNames 
     * allowed to have text selection (Defaults to <code>/INPUT|TEXTAREA|SELECT/i</code>) */
    allowTextSelectionPattern : /INPUT|TEXTAREA|SELECT/i,
    
    /**
     * Called once after all setup has been completed and the grid is ready to be rendered.
     * @return {Ext.grid.Grid} this
     */
    render : function(){
        var c = this.container;
        // try to detect autoHeight/width mode
        if((!c.dom.offsetHeight || c.dom.offsetHeight < 20) || c.getStyle("height") == "auto"){
    	    this.autoHeight = true;   
    	}	       
    	var view = this.getView();
        view.init(this);
        
        c.on("click", this.onClick, this);
        c.on("dblclick", this.onDblClick, this);
        c.on("contextmenu", this.onContextMenu, this);
        c.on("keydown", this.onKeyDown, this);

        this.relayEvents(c, ["mousedown","mouseup","mouseover","mouseout","keypress"]);
        
        this.getSelectionModel().init(this);
        
        view.render();
        
        return this;
    },
    
    reconfigure : function(dataSource, colModel){
        this.view.bind(dataSource, colModel);
        this.dataSource = dataSource;
        this.colModel = colModel;
        this.view.refresh(true);
    },

    onKeyDown : function(e){
        this.fireEvent("keydown", e);
    },

    /**
     * Destroy this grid. 
     * @param {Boolean} removeEl True to remove the element
     */
    destroy : function(removeEl, keepListeners){
        var c = this.container;
        c.removeAllListeners();
        this.view.destroy();
        this.colModel.purgeListeners();
        if(!keepListeners){
            this.purgeListeners();
        }
        c.update("");
        if(removeEl === true){
            c.remove();
        }
    },
    
    // private
    processEvent : function(name, e){
        this.fireEvent(name, e);
        var t = e.getTarget();
        var v = this.view;
        var header = v.findHeaderIndex(t);
        if(header !== false){
            this.fireEvent("header" + name, this, header, e);
        }else{
            var row = v.findRowIndex(t);
            var cell = v.findCellIndex(t);
            if(row !== false){
                this.fireEvent("row" + name, this, row, e);
                if(cell !== false){
                    this.fireEvent("cell" + name, this, row, cell, e);
                }
            }
        }
    },
    
    // private
    onClick : function(e){
        this.processEvent("click", e);
    },

    // private
    onContextMenu : function(e, t){
        this.processEvent("contextmenu", e);
    },

    // private
    onDblClick : function(e){
        this.processEvent("dblclick", e);
    },

    walkCells : function(row, col, step, fn, scope){
        var cm = this.colModel, clen = cm.getColumnCount();
        var ds = this.dataSource, rlen = ds.getCount(), first = true;
        if(step < 0){
            if(col < 0){
                row--;
                first = false;
            }
            while(row >= 0){
                if(!first){
                    col = clen-1;
                }
                first = false;
                while(col >= 0){
                    if(fn.call(scope || this, row, col, cm) === true){
                        return [row, col];
                    }
                    col--;
                }
                row--;
            }
        } else {
            if(col >= clen){
                row++;
                first = false;
            }
            while(row < rlen){
                if(!first){
                    col = 0;
                }
                first = false;
                while(col < clen){
                    if(fn.call(scope || this, row, col, cm) === true){
                        return [row, col];
                    }
                    col++;
                }
                row++;
            }
        }
        return null;
    },

    getSelections : function(){
        return this.selModel.getSelections();  
    },

    /**
     * Causes the grid to manually recalculate its dimensions. Generally this is done automatically,
     * but if manual update is required this method will initiate it.
     */
    autoSize : function(){
        if(this.view){
            this.view.layout();
            if(this.view.adjustForScroll){
                this.view.adjustForScroll();
            }
        }
    },
    
    // private for compatibility, overridden by editor grid
    stopEditing : function(){},
    
    /**
     * Returns the grid's SelectionModel.
     * @return {SelectionModel}
     */
    getSelectionModel : function(){
        if(!this.selModel){
            this.selModel = new Ext.grid.RowSelectionModel();
        }
        return this.selModel;
    },
    
    /**
     * Returns the grid's DataSource.
     * @return {DataSource}
     */
    getDataSource : function(){
        return this.dataSource;
    },
    
    /**
     * Returns the grid's ColumnModel.
     * @return {ColumnModel}
     */
    getColumnModel : function(){
        return this.colModel;
    },
    
    /**
     * Returns the grid's GridView object.
     * @return {GridView}
     */
    getView : function(){
        if(!this.view){
            this.view = new Ext.grid.GridView();
        }
        return this.view;
    },
    /**
     * Called to get grid's drag proxy text, by default returns this.ddText. 
     * @return {String}
     */
    getDragDropText : function(){
        return this.ddText.replace("%0", this.selModel.getCount());
    }
});
/**
 * Configures the text is the drag proxy (defaults to "%0 selected row(s)"). 
 * %0 is replaced with the number of selected rows.
 * @type String
 */
Ext.grid.Grid.prototype.ddText = "%0 selected row(s)";