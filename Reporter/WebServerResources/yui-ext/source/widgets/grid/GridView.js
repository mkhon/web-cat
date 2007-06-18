/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.grid.GridView = function(config){
    Ext.grid.GridView.superclass.constructor.call(this);
    this.el = null;

    Ext.apply(this, config);
};

Ext.extend(Ext.grid.GridView, Ext.grid.AbstractGridView, {
    rowClass : "x-grid-row",
    
    cellClass : "x-grid-col",

    tdClass : "x-grid-td",

    hdClass : "x-grid-hd",
    
    splitClass : "x-grid-split",
    
    sortClasses : ["sort-asc", "sort-desc"],
    
    enableMoveAnim : false,

    hlColor: "C3DAF9",
    
    dh : Ext.DomHelper,
    
    fly : Ext.Element.fly,
    
    css : Ext.util.CSS,
    
    borderWidth: 1,
    
    splitOffset: 3,
    
    scrollIncrement : 22,
    
    cellRE: /(?:.*?)x-grid-(?:hd|cell|csplit)-(?:[\d]+)-([\d]+)(?:.*?)/,
    
    findRE: /\s?(?:x-grid-hd|x-grid-col|x-grid-csplit)\s/,
    
    init: function(grid){
		Ext.grid.GridView.superclass.init.call(this, grid);
		
		this.bind(grid.dataSource, grid.colModel);
		
	    grid.on("headerclick", this.handleHeaderClick, this);

        if(grid.trackMouseOver){
            grid.on("mouseover", this.onRowOver, this);
	        grid.on("mouseout", this.onRowOut, this);
	    }
	    grid.cancelTextSelection = function(){};
		this.gridId = grid.id;
		
		var tpls = this.templates || {};
		
		if(!tpls.master){
		    tpls.master = new Ext.Template(
		       '<div class="x-grid" hidefocus="true">',
		          '<div class="x-grid-topbar"></div>',
		          '<div class="x-grid-scroller"><div></div></div>',
		          '<div class="x-grid-locked">',
		              '<div class="x-grid-header">{lockedHeader}</div>',
		              '<div class="x-grid-body">{lockedBody}</div>',
		          "</div>",
		          '<div class="x-grid-viewport">',
		              '<div class="x-grid-header">{header}</div>',
		              '<div class="x-grid-body">{body}</div>',
		          "</div>",
		          '<div class="x-grid-bottombar"></div>',
		          '<a href="#" class="x-grid-focus" tabIndex="-1"></a>',
		          '<div class="x-grid-resize-proxy">&#160;</div>',
		       "</div>"
		    );
		    tpls.master.disableformats = true;
		}
		
		if(!tpls.header){
		    tpls.header = new Ext.Template(
		       '<table border="0" cellspacing="0" cellpadding="0">',
		       '<tbody><tr class="x-grid-hd-row">{cells}</tr></tbody>',
		       "</table>{splits}"
		    );
		    tpls.header.disableformats = true;
		}
		tpls.header.compile();
		
		if(!tpls.hcell){
		    tpls.hcell = new Ext.Template(
		        '<td class="x-grid-hd x-grid-td-{id} {cellId}"><div title="{title}" class="x-grid-hd-inner x-grid-hd-{id}">',
		        '<div class="x-grid-hd-text" unselectable="on">{value}<img class="x-grid-sort-icon" src="', Ext.BLANK_IMAGE_URL, '" /></div>',
		        "</div></td>"
		     );
		     tpls.hcell.disableFormats = true;
		}
		tpls.hcell.compile();
		
		if(!tpls.hsplit){
		    tpls.hsplit = new Ext.Template('<div class="x-grid-split {splitId} x-grid-split-{id}" unselectable="on">&#160;</div>');
		    tpls.hsplit.disableFormats = true;
		}
		tpls.hsplit.compile();
		
		if(!tpls.body){
		    tpls.body = new Ext.Template(
		       '<table border="0" cellspacing="0" cellpadding="0">',
		       "<tbody>{rows}</tbody>",
		       "</table>"
		    );
		    tpls.body.disableFormats = true;
		}
		tpls.body.compile();
		
		if(!tpls.row){
		    tpls.row = new Ext.Template('<tr class="x-grid-row {alt}">{cells}</tr>');
		    tpls.row.disableFormats = true;
		}
		tpls.row.compile();
		
		if(!tpls.cell){
		    tpls.cell = new Ext.Template(
		        '<td class="x-grid-col x-grid-td-{id} {cellId} {css}" tabIndex="0">',
		        '<div class="x-grid-col-{id} x-grid-cell-inner"><div class="x-grid-cell-text" unselectable="on" {attr}>{value}</div></div>',
		        "</td>"
		    );
            tpls.cell.disableFormats = true;
        }
		if(!tpls.rowMarker){
		    tpls.rowMarker = new Ext.Template(
		        '<td class="x-grid-row-marker">',
		        '<div class="x-grid-col-{id} x-grid-cell-inner"><div class="x-grid-cell-text" unselectable="on" {attr}>{value}</div></div>',
		        "</td>"
		    );
            tpls.cell.disableFormats = true;
        }
		tpls.cell.compile();
		
		this.templates = tpls;
	},
	
	bind : function(ds, cm){
        if(this.ds){
            this.ds.un("load", this.scrollToTop, this);
            this.ds.un("datachanged", this.onDataChange);
            this.ds.un("add", this.onAdd);
            this.ds.un("remove", this.onRemove);
            this.ds.un("update", this.onUpdate);
            this.ds.un("clear", this.onClear);
        }
        if(ds){
            ds.on("load", this.scrollToTop, this);
            ds.on("datachanged", this.onDataChange, this);
            ds.on("add", this.onAdd, this);
            ds.on("remove", this.onRemove, this);
            ds.on("update", this.onUpdate, this);
            ds.on("clear", this.onClear, this);
        }
        this.ds = ds;
        
        if(this.cm){
            this.cm.un("widthchange", this.updateColumns, this);
            this.cm.un("headerchange", this.updateHeaders, this);
            this.cm.un("hiddenchange", this.handleHiddenChange, this);
            this.cm.un("columnmoved", this.handleColumnMove, this);
            this.cm.un("columnlockchange", this.handleLockChange, this);
        }
        if(cm){
            this.generateRules(cm);
            cm.on("widthchange", this.updateColumns, this);
            cm.on("headerchange", this.updateHeaders, this);
            cm.on("hiddenchange", this.handleHiddenChange, this);
            cm.on("columnmoved", this.handleColumnMove, this);
            cm.on("columnlockchange", this.handleLockChange, this);
        }
        this.cm = cm;
    },
    
    onDataChange : function(){
        this.refresh();
        this.updateHeaderSortState();
    },

	onClear : function(){
        this.refresh();
    },

	onUpdate : function(ds, record){
        this.refreshRow(record);
    },

    refreshRow : function(record){
        var ds = this.ds, index;
        if(typeof record == 'number'){
            index = record;
            record = ds.getAttribute(index);
        }else{
            index = ds.indexOf(record);
        }
        var rows = this.getRowComposite(index);
        var cls = [];
        this.insertRows(ds, index, index, true);
        this.onRemove(ds, record, index+1, true);
        this.syncRowHeights(index, index);
        this.layout();
        this.fireEvent("rowupdated", this, index, record);
    },

    onAdd : function(ds, records, index){
        this.insertRows(ds, index, index + (records.length-1));
    },

    onRemove : function(ds, record, index, isUpdate){
        if(isUpdate !== true){
            this.fireEvent("beforerowremoved", this, index, record);
        }
        var bt = this.getBodyTable(), lt = this.getLockedTable();
        if(bt.rows[index]){
            bt.firstChild.removeChild(bt.rows[index]);
        }
        if(lt.rows[index]){
            lt.firstChild.removeChild(lt.rows[index]);
        }
        if(isUpdate !== true){
            this.stripeRows(index);
            this.syncRowHeights(index, index);
            this.layout();
            this.fireEvent("rowremoved", this, index, record);
        }
    },

    scrollToTop : function(){
        if(this.scroller){
            this.scroller.dom.scrollTop = 0;
            this.syncScroll();
        }
    },

    getHeaderPanel : function(doShow){
        if(doShow){
            this.headerPanel.show();
        }
        return this.headerPanel;
	},
	
	getFooterPanel : function(doShow){
        if(doShow){
            this.footerPanel.show();
        }
        return this.footerPanel;
	},
	
	initElements : function(){
	    var E = Ext.Element;
	    var el = this.grid.container.dom.firstChild;
	    var cs = el.childNodes;
	    
	    this.el = new E(el);
	    this.headerPanel = new E(el.firstChild);
	    this.headerPanel.enableDisplayMode("block");

        this.scroller = new E(cs[1]);
	    this.scrollSizer = new E(this.scroller.dom.firstChild);
	    
	    this.lockedWrap = new E(cs[2]);
	    this.lockedHd = new E(this.lockedWrap.dom.firstChild);
	    this.lockedBody = new E(this.lockedWrap.dom.childNodes[1]);
	    
	    this.mainWrap = new E(cs[3]);
	    this.mainHd = new E(this.mainWrap.dom.firstChild);
	    this.mainBody = new E(this.mainWrap.dom.childNodes[1]);
	    
	    this.footerPanel = new E(cs[4]);
	    this.footerPanel.enableDisplayMode("block");

        this.focusEl = new E(cs[5]);
        this.focusEl.swallowEvent("click", true);
        this.resizeProxy = new E(cs[6]);
	    
	    this.headerSelector = String.format(
	       '#{0} td.x-grid-hd, #{1} td.x-grid-hd',
	       this.lockedHd.id, this.mainHd.id
	    );
	    
	    this.splitterSelector = String.format(
	       '#{0} div.x-grid-split, #{1} div.x-grid-split',
	       this.lockedHd.id, this.mainHd.id
	    );
    },
	
	getHeaderCell : function(index){
	    return Ext.DomQuery.select(this.headerSelector)[index];
	},
	
	getHeaderCellMeasure : function(index){
	    return this.getHeaderCell(index).firstChild;
	},
	
	getHeaderCellText : function(index){
	    return this.getHeaderCell(index).firstChild.firstChild;
	},
	
	getLockedTable : function(){
	    return this.lockedBody.dom.firstChild;
	},
	
	getBodyTable : function(){
	    return this.mainBody.dom.firstChild;
	},
	
	getLockedRow : function(index){
	    return this.getLockedTable().rows[index];
	},
	
	getRow : function(index){
	    return this.getBodyTable().rows[index];
	},
	
	getRowComposite : function(index){
	    if(!this.rowEl){
	        this.rowEl = new Ext.CompositeElementLite();
	    }
        var els = [], lrow, mrow;
        if(lrow = this.getLockedRow(index)){
            els.push(lrow);
        }
        if(mrow = this.getRow(index)){
            els.push(mrow);
        }
        this.rowEl.elements = els;
	    return this.rowEl;
	},
	
	getCell : function(rowIndex, colIndex){
	    var locked = this.cm.getLockedCount();
	    var source;
	    if(colIndex < locked){
	        source = this.lockedBody.dom.firstChild;
	    }else{
	        source = this.mainBody.dom.firstChild;
	        colIndex -= locked;
	    }
        return source.rows[rowIndex].childNodes[colIndex];
	},
	
	getCellText : function(rowIndex, colIndex){
	    return this.getCell(rowIndex, colIndex).firstChild.firstChild;
	},
	
	getCellBox : function(cell){
	    var b = this.fly(cell).getBox();
        if(Ext.isOpera){ // opera fails to report the Y
            b.y = cell.offsetTop + this.mainBody.getY();
        }
        return b;
    },
    
    getCellIndex : function(cell){
        var id = String(cell.className).match(this.cellRE);
        if(id){
            return parseInt(id[1], 10);
        }
        return 0;
    },
    
    findHeaderIndex : function(n){
        var r = Ext.fly(n).findParent("td." + this.hdClass, 6);
        return r ? this.getCellIndex(r) : false;
    },
    
    findHeaderCell : function(n){
        var r = Ext.fly(n).findParent("td." + this.hdClass, 6);
        return r ? r : false;
    },
    
    findRowIndex : function(n){
        var r = Ext.fly(n).findParent("tr." + this.rowClass, 6);
        return r ? r.rowIndex : false;
    },
    
    findCellIndex : function(node){
        var stop = this.el.dom;
        while(node && node != stop){
            if(this.findRE.test(node.className)){
                return this.getCellIndex(node);
            }
            node = node.parentNode;
        }
        return false;
    },
    
    getColumnId : function(index){
	    return this.cm.getColumnId(index);  
	},
	
	getSplitters : function(){
	    if(this.splitterSelector){
	       return Ext.DomQuery.select(this.splitterSelector);
	    }else{
	        return null;
	    }
	},
	
	getSplitter : function(index){
	    return this.getSplitters()[index];
	},

    onRowOver : function(e, t){
        var row;
        if((row = this.findRowIndex(t)) !== false){
            this.getRowComposite(row).addClass("x-grid-row-over");
        }
    },

    onRowOut : function(e, t){
        var row;
        if((row = this.findRowIndex(t)) !== false && row !== this.findRowIndex(e.getRelatedTarget())){
            this.getRowComposite(row).removeClass("x-grid-row-over");
        }
    },

    renderHeaders : function(){
	    var cm = this.cm;
        var ct = this.templates.hcell, ht = this.templates.header, st = this.templates.hsplit;
        var cb = [], lb = [], sb = [], lsb = [], p = {};
        for(var i = 0, len = cm.getColumnCount(); i < len; i++){
            p.cellId = "x-grid-hd-0-" + i;
            p.splitId = "x-grid-csplit-0-" + i;
            p.id = cm.getColumnId(i);
            p.title = cm.getColumnTooltip(i) || "";
            p.value = cm.getColumnHeader(i) || "";
            if(!cm.isLocked(i)){
                cb[cb.length] = ct.apply(p);
                sb[sb.length] = st.apply(p);
            }else{
                lb[lb.length] = ct.apply(p); 
                lsb[lsb.length] = st.apply(p);
            }
        }
        return [ht.apply({cells: lb.join(""), splits:lsb.join("")}),
                ht.apply({cells: cb.join(""), splits:sb.join("")})];
	},
	
	updateHeaders : function(){
        var html = this.renderHeaders();
        this.lockedHd.update(html[0]);
        this.mainHd.update(html[1]);
    },
    
    /**
     * Focuses the specified row..
     * @param {Number} row The row index
     */
    focusRow : function(row){
        var x = this.scroller.dom.scrollLeft;
        this.focusCell(row, 0, false);
        this.scroller.dom.scrollLeft = x;
    },

    /**
     * Focuses the specified cell.
     * @param {Number} row The row index
     * @param {Number} col The column index
     */
    focusCell : function(row, col, hscroll){
        var el = this.ensureVisible(row, col, hscroll);
        this.focusEl.alignTo(el, "tl-tl");
        this.focusEl.focus.defer(1, this.focusEl);
    },

    /** @ignore */
    ensureVisible : function(row, col, hscroll){
        if(typeof row != "number"){
            row = row.rowIndex;
        }
        if(row < 0 && row >= this.ds.getCount()){
            return;
        }
        col = (col !== undefined ? col : 0);
        var cm = this.grid.colModel;
        while(cm.isHidden(col)){
            col++;
        }

        var el = this.getCell(row, col);
        if(!el){
            return;
        }
        var c = this.scroller.dom;
        
        var ctop = parseInt(el.offsetTop, 10);
        var cleft = parseInt(el.offsetLeft, 10);
        var cbot = ctop + el.offsetHeight;
        var cright = cleft + el.offsetWidth;

        var ch = c.clientHeight - this.mainHd.dom.offsetHeight;
        var stop = parseInt(c.scrollTop, 10);
        var sleft = parseInt(c.scrollLeft, 10);
        var sbot = stop + ch;
        var sright = sleft + c.clientWidth;

        if(ctop < stop){
        	c.scrollTop = ctop;
        }else if(cbot > sbot){
            c.scrollTop = cbot-ch;
        }

        if(hscroll !== false){
            if(cleft < sleft){
                c.scrollLeft = cleft;
            }else if(cright > sright){
                c.scrollLeft = cright-c.clientWidth;
            }
        }
        return el;
    },
    
    updateColumns : function(){
        this.grid.stopEditing();
        var cm = this.grid.colModel, colIds = this.getColumnIds();
        //var totalWidth = cm.getTotalWidth();
        var pos = 0;
        for(var i = 0, len = cm.getColumnCount(); i < len; i++){
            //if(cm.isHidden(i)) continue;
            var w = cm.getColumnWidth(i);
            this.css.updateRule(this.colSelector+colIds[i], "width", (w - this.borderWidth) + "px");
            this.css.updateRule(this.hdSelector+colIds[i], "width", (w - this.borderWidth) + "px");
        }
        this.updateSplitters();
    },
    
    updateSplitters : function(){
        var cm = this.cm, s = this.getSplitters();
        if(s){ // splitters not created yet
            var pos = 0, locked = true;
            for(var i = 0, len = cm.getColumnCount(); i < len; i++){
                if(cm.isHidden(i)) continue;
                var w = cm.getColumnWidth(i);
                if(!cm.isLocked(i) && locked){
                    pos = 0;
                    locked = false;
                }
                pos += w;
                s[i].style.left = (pos-this.splitOffset) + "px";
            }
        }
    },
    
    handleHiddenChange : function(colModel, colIndex, hidden){
        if(hidden){
            this.hideColumn(colIndex);
        }else{
            this.unhideColumn(colIndex);
        }
    },
    
    hideColumn : function(colIndex){
        var cid = this.getColumnId(colIndex);
        this.css.updateRule(this.tdSelector+cid, "display", "none");
        this.css.updateRule(this.splitSelector+cid, "display", "none");
        if(Ext.isSafari){
            this.updateHeaders();
        }
        this.updateSplitters();
        this.layout();
    },
    
    unhideColumn : function(colIndex){
        var cid = this.getColumnId(colIndex);
        this.css.updateRule(this.tdSelector+cid, "display", "");
        this.css.updateRule(this.splitSelector+cid, "display", "");
        
        if(Ext.isSafari){
            this.updateHeaders();
        }
        this.updateSplitters();
        this.layout();
    },
    
    insertRows : function(dm, firstRow, lastRow, isUpdate){
        if(firstRow == 0 && lastRow == dm.getCount()-1){
            this.refresh();
        }else{
            if(!isUpdate){
                this.fireEvent("beforerowsinserted", this, firstRow, lastRow);
            }
            var s = this.getScrollState();
            var markup = this.renderRows(firstRow, lastRow);
            this.bufferRows(markup[0], this.getLockedTable(), firstRow);
            this.bufferRows(markup[1], this.getBodyTable(), firstRow);
            this.restoreScroll(s);
            if(!isUpdate){
                this.fireEvent("rowsinserted", this, firstRow, lastRow);
                this.syncRowHeights(firstRow, lastRow);
                this.stripeRows(firstRow);
                this.layout();
            }
        }
    },
    
    bufferRows : function(markup, target, index){
        var before = null, trows = target.rows, tbody = target.tBodies[0];
        if(index < trows.length){
            before = trows[index];
        }
        var b = document.createElement("div");
        b.innerHTML = "<table><tbody>"+markup+"</tbody></table>";
        var rows = b.firstChild.rows;
        for(var i = 0, len = rows.length; i < len; i++){
            if(before){
                tbody.insertBefore(rows[i], before);
            }else{
                tbody.appendChild(rows[i]);
            }
        }                
        b.innerHTML = "";
        b = null;
    },
    
    deleteRows : function(dm, firstRow, lastRow){
        if(dm.getRowCount()<1){
            this.fireEvent("beforerefresh", this);
            this.mainBody.update("");
            this.lockedBody.update("");
            this.fireEvent("refresh", this);
        }else{
            this.fireEvent("beforerowsdeleted", this, firstRow, lastRow);
            var bt = this.getBodyTable();
            var tbody = bt.firstChild;
            var rows = bt.rows;
            for(var rowIndex = firstRow; rowIndex <= lastRow; rowIndex++){
                tbody.removeChild(rows[firstRow]);
            }
            this.stripeRows(firstRow);
            this.fireEvent("rowsdeleted", this, firstRow, lastRow);
        }
    },
    
    updateRows : function(dataSource, firstRow, lastRow){
        var s = this.getScrollState();
        this.refresh();
        this.restoreScroll(s);
    },
    
    handleSort : function(dataSource, sortColumnIndex, sortDir, noRefresh){
        if(!noRefresh){
           this.refresh();
        }
        this.updateHeaderSortState();
    },
    
    getScrollState : function(){
        var sb = this.scroller.dom;
        return {left: sb.scrollLeft, top: sb.scrollTop};
    },
    
    stripeRows : function(startRow){
        if(!this.grid.stripeRows || this.ds.getCount() < 1){
            return;
        }
        startRow = startRow || 0;
        var rows = this.getBodyTable().rows;
        var lrows = this.getLockedTable().rows;
        var cls = ' x-grid-row-alt ';
        for(var i = startRow, len = rows.length; i < len; i++){
            var row = rows[i], lrow = lrows[i];
            var isAlt = ((i+1) % 2 == 0);
            var hasAlt = (' '+row.className + ' ').indexOf(cls) != -1;
            if(isAlt == hasAlt){
                continue;
            }
            if(isAlt){
                row.className += " x-grid-row-alt";
            }else{
                row.className = row.className.replace("x-grid-row-alt", "");
            }
            if(lrow){
                lrow.className = row.className;
            }
        }
    },
    
    restoreScroll : function(state){
        var sb = this.scroller.dom;
        sb.scrollLeft = state.left;
        sb.scrollTop = state.top;
        this.syncScroll();
    },
    
    syncScroll : function(){
        var sb = this.scroller.dom;
        var sh = this.mainHd.dom;
        var bs = this.mainBody.dom;
        var lv = this.lockedBody.dom;
        sh.scrollLeft = bs.scrollLeft = sb.scrollLeft;
        lv.scrollTop = bs.scrollTop = sb.scrollTop;
    },
    
    handleScroll : function(e){
        this.syncScroll();
        var sb = this.scroller.dom;
        this.grid.fireEvent("bodyscroll", sb.scrollLeft, sb.scrollTop);
        e.stopEvent();
    },
    
    handleWheel : function(e){
        var d = e.getWheelDelta();
        this.scroller.dom.scrollTop -= d*22;
        // set this here to prevent jumpy scrolling on large tables
        this.lockedBody.dom.scrollTop = this.mainBody.dom.scrollTop = this.scroller.dom.scrollTop;
        e.stopEvent();
    },
    
    renderRows : function(startRow, endRow){
        // pull in all the crap needed to render rows
        var g = this.grid, cm = g.colModel, ds = g.dataSource, stripe = g.stripeRows;
        var colCount = cm.getColumnCount();
        
        if(ds.getCount() < 1){
            return ["", ""];
        }
        
        // build a map for all the columns
        var cs = [];
        for(var i = 0; i < colCount; i++){
            var name = cm.getDataIndex(i);
            cs[i] = {
                name : typeof name == 'undefined' ? ds.fields.get(i).name : name,
                renderer : cm.getRenderer(i),
                id : cm.getColumnId(i),
                locked : cm.isLocked(i)
            };
        }
                
        startRow = startRow || 0;
        endRow = typeof endRow == "undefined"? ds.getCount()-1 : endRow;
        
        // records to render
        var rs = ds.getRange(startRow, endRow);

        return this.doRender(cs, rs, ds, startRow, colCount, stripe);
    },

    // As much as I hate to duplicate code, this was branched because FireFox really hates
    // [].join("") on strings. The performance difference was substantial enough to
    // branch this function
    doRender : Ext.isGecko ?
            function(cs, rs, ds, startRow, colCount, stripe){
                var ts = this.templates, ct = ts.cell, rt = ts.row;
                // buffers
                var buf = "", lbuf = "", cb, lcb, c, p = {}, rp = {}, r;
                for(var j = 0, len = rs.length; j < len; j++){
                    r = rs[j], cb = "", lcb = "", rowIndex = (j+startRow);
                    for(var i = 0; i < colCount; i++){
                        c = cs[i];
                        p.cellId = "x-grid-cell-" + rowIndex + "-" + i;
                        p.id = c.id;
                        p.css = p.attr = "";
                        p.value = c.renderer(r.data[c.name], p, r, rowIndex, i, ds);
                        if(p.value == undefined || p.value === "") p.value = "&#160;";
                        if(r.dirty && typeof r.modified[c.name] !== 'undefined'){
                            p.css += p.css ? ' x-grid-dirty-cell' : 'x-grid-dirty-cell';
                        }
                        var markup = ct.apply(p);
                        if(!c.locked){
                            cb+= markup;
                        }else{
                            lcb+= markup;
                        }
                    }
                    var alt = [];
                    if(stripe && ((rowIndex+1) % 2 == 0)){
                        alt[0] = "x-grid-row-alt";
                    }
                    if(r.dirty){
                        alt[1] = " x-grid-dirty-row";
                    }
                    rp.cells = lcb;
                    if(this.getRowClass){
                        alt[2] = this.getRowClass(r, rowIndex);
                    }
                    rp.alt = alt.join(" ");
                    lbuf+= rt.apply(rp);
                    rp.cells = cb;
                    buf+=  rt.apply(rp);
                }
                return [lbuf, buf];
            } :
            function(cs, rs, ds, startRow, colCount, stripe){
                var ts = this.templates, ct = ts.cell, rt = ts.row;
                // buffers
                var buf = [], lbuf = [], cb, lcb, c, p = {}, rp = {}, r;
                for(var j = 0, len = rs.length; j < len; j++){
                    r = rs[j], cb = [], lcb = [], rowIndex = (j+startRow);
                    for(var i = 0; i < colCount; i++){
                        c = cs[i];
                        p.cellId = "x-grid-cell-" + rowIndex + "-" + i;
                        p.id = c.id;
                        p.css = p.attr = "";
                        p.value = c.renderer(r.data[c.name], p, r, rowIndex, i, ds);
                        if(p.value == undefined || p.value === "") p.value = "&#160;";
                        if(r.dirty && typeof r.modified[c.name] !== 'undefined'){
                            p.css += p.css ? ' x-grid-dirty-cell' : 'x-grid-dirty-cell';
                        }
                        var markup = ct.apply(p);
                        if(!c.locked){
                            cb[cb.length] = markup;
                        }else{
                            lcb[lcb.length] = markup;
                        }
                    }
                    var alt = [];
                    if(stripe && ((rowIndex+1) % 2 == 0)){
                        alt[0] = "x-grid-row-alt";
                    }
                    if(r.dirty){
                        alt[1] = " x-grid-dirty-row";
                    }
                    rp.cells = lcb;
                    if(this.getRowClass){
                        alt[2] = this.getRowClass(r, rowIndex);
                    }
                    rp.alt = alt.join(" ");
                    rp.cells = lcb.join("");
                    lbuf[lbuf.length] = rt.apply(rp);
                    rp.cells = cb.join("");
                    buf[buf.length] =  rt.apply(rp);
                }
                return [lbuf.join(""), buf.join("")];
            },

    renderBody : function(){
        var markup = this.renderRows();
        var bt = this.templates.body;
        return [bt.apply({rows: markup[0]}), bt.apply({rows: markup[1]})];
    },
    
    refresh : function(headersToo){
        this.fireEvent("beforerefresh", this);
        this.grid.stopEditing();
        var result = this.renderBody();
        this.lockedBody.update(result[0]);
        this.mainBody.update(result[1]);
        if(headersToo === true){
            this.updateHeaders();
            this.updateColumns();
            this.updateSplitters();
            this.updateHeaderSortState();
        }
        this.syncRowHeights();
        this.layout();
        this.fireEvent("refresh", this);
    },
    
    handleColumnMove : function(cm, oldIndex, newIndex){
        this.indexMap = null;
        var s = this.getScrollState();
        this.refresh(true);
        this.restoreScroll(s);
        this.afterMove(newIndex);
    },
    
    afterMove : function(colIndex){
        if(this.enableMoveAnim && Ext.enableFx){
            this.fly(this.getHeaderCell(colIndex).firstChild).highlight(this.hlColor);
        }
    },
    
    updateCell : function(dm, rowIndex, dataIndex){
        var colIndex = this.getColumnIndexByDataIndex(dataIndex);
        if(typeof colIndex == "undefined"){ // not present in grid
            return;
        }
        var cm = this.grid.colModel;
        var cell = this.getCell(rowIndex, colIndex);
        var cellText = this.getCellText(rowIndex, colIndex);
        
        var p = {
            cellId : "x-grid-cell-" + rowIndex + "-" + colIndex,
            id : cm.getColumnId(colIndex),
            css: colIndex == cm.getColumnCount()-1 ? "x-grid-col-last" : ""
        };
        var renderer = cm.getRenderer(colIndex);
        var val = renderer(dm.getValueAt(rowIndex, dataIndex), p, rowIndex, colIndex, dm);
        if(typeof val == "undefined" || val === "") val = "&#160;";
        cellText.innerHTML = val;
        cell.className = this.cellClass + " " + p.cellId + " " + p.css;
        this.syncRowHeights(rowIndex, rowIndex);
    },
    
    calcColumnWidth : function(colIndex, maxRowsToMeasure){
        var maxWidth = 0;
        if(this.grid.autoSizeHeaders){
            var h = this.getHeaderCellMeasure(colIndex);
            maxWidth = Math.max(maxWidth, h.scrollWidth);
        }
        var tb, index;
        if(this.cm.isLocked(colIndex)){
            tb = this.getLockedTable();
            index = colIndex;
        }else{
            tb = this.getBodyTable();
            index = colIndex - this.cm.getLockedCount();
        }
        if(tb && tb.rows){
            var rows = tb.rows;
            var stopIndex = Math.min(maxRowsToMeasure || rows.length, rows.length);
            for(var i = 0; i < stopIndex; i++){
                var cell = rows[i].childNodes[index].firstChild;
                maxWidth = Math.max(maxWidth, cell.scrollWidth);
            }
        }
        return maxWidth + /*margin for error in IE*/ 5;
    },
    /**
     * Autofit a column to its content.
     * @param {Number} colIndex
     * @param {Boolean} forceMinSize true to force the column to go smaller if possible
     */
     autoSizeColumn : function(colIndex, forceMinSize, suppressEvent){
         if(this.cm.isHidden(colIndex)){
             return; // can't calc a hidden column
         }
        if(forceMinSize){
            var cid = this.cm.getColumnId(colIndex);
            this.css.updateRule(this.colSelector + cid, "width", this.grid.minColumnWidth + "px");
           if(this.grid.autoSizeHeaders){
               this.css.updateRule(this.hdSelector + cid, "width", this.grid.minColumnWidth + "px");
           }
        }
        var newWidth = this.calcColumnWidth(colIndex);
        this.cm.setColumnWidth(colIndex,
            Math.max(this.grid.minColumnWidth, newWidth), suppressEvent);
        if(!suppressEvent){
            this.grid.fireEvent("columnresize", colIndex, newWidth);
        }
    },
    
    /**
     * Autofits all columns to their content and then expands to fit any extra space in the grid
     */
     autoSizeColumns : function(){
        var cm = this.grid.colModel;
        var colCount = cm.getColumnCount();
        for(var i = 0; i < colCount; i++){
            this.autoSizeColumn(i, true, true);
        }
        if(cm.getTotalWidth() < this.scroller.dom.clientWidth){
            this.fitColumns();
        }else{
            this.updateColumns();
            this.layout();
        }
    },
    
    /**
     * Autofits all columns to the grid's width proportionate with their current size
     * @param {Boolean} reserveScrollSpace Reserve space for a scrollbar
     */
    fitColumns : function(reserveScrollSpace){
        var cm = this.grid.colModel;
        var colCount = cm.getColumnCount();
        var cols = [];
        var width = 0;
        var i, w;
        for (i = 0; i < colCount; i++){
            if(!cm.isHidden(i) && !cm.isFixed(i)){
                w = cm.getColumnWidth(i);
                cols.push(i);
                cols.push(w);
                width += w;
            }
        }
        var avail = Math.min(this.scroller.dom.clientWidth, this.el.getWidth());
        if(reserveScrollSpace){
            avail -= 17;
        }
        var frac = (avail - cm.getTotalWidth())/width;
        while (cols.length){
            w = cols.pop();
            i = cols.pop();
            cm.setColumnWidth(i, Math.floor(w + w*frac), true);
        }
        this.updateColumns();
        this.layout();
    }, 
    
    onRowSelect : function(rowIndex){
        var row = this.getRowComposite(rowIndex);
        row.addClass("x-grid-row-selected");
    },

    onRowDeselect : function(rowIndex){
        var row = this.getRowComposite(rowIndex);
        row.removeClass("x-grid-row-selected");
    },

    onCellSelect : function(row, col){
        var cell = this.getCell(row, col);
        if(cell){
            Ext.fly(cell).addClass("x-grid-cell-selected");
        }
    },

    onCellDeselect : function(row, col){
        var cell = this.getCell(row, col);
        if(cell){
            Ext.fly(cell).removeClass("x-grid-cell-selected");
        }
    },

    updateHeaderSortState : function(){
        var state = this.ds.getSortState();
        if(!state){
            return;
        }
        this.sortState = state;
        var sortColumn = this.cm.findColumnIndex(state.field);
        if(sortColumn != -1){
            var sortDir = state.direction;
            var sc = this.sortClasses;
            var hds = this.el.select(this.headerSelector).removeClass(sc);
            hds.item(sortColumn).addClass(sc[sortDir == "DESC" ? 1 : 0]);
        }
    },
    
    handleHeaderClick : function(g, index){
        if(this.headersDisabled){
            return;
        }
        var dm = g.dataSource, cm = g.colModel;
	    if(!cm.isSortable(index)){
            return;
        }
	    g.stopEditing();
        dm.sort(cm.getDataIndex(index));
    },
    
    
    destroy : function(){
        this.bind(null, null);
    },
    
    handleLockChange : function(){
        this.refresh(true);  
    },

    onDenyColumnLock : function(){

    },

    onDenyColumnHide : function(){

    },

    handleHdMenuClick : function(item){
        var index = this.hdCtxIndex;
        var cm = this.cm, ds = this.ds;
        switch(item.id){
            case "asc":
                ds.sort(cm.getDataIndex(index), "ASC");
                break;
            case "desc":
                ds.sort(cm.getDataIndex(index), "DESC");
                break;
            case "lock":
                var lc = cm.getLockedCount();
                if(cm.getColumnCount(true) <= lc+1){
                    this.onDenyColumnLock();
                    return;
                }
                if(lc != index){
                    cm.setLocked(index, true, true);
                    cm.moveColumn(index, lc);
                    this.grid.fireEvent("columnmove", index, lc);
                }else{
                    cm.setLocked(index, true);
                }
            break;
            case "unlock":
                var lc = cm.getLockedCount();
                if((lc-1) != index){
                    cm.setLocked(index, false, true);
                    cm.moveColumn(index, lc-1);
                    this.grid.fireEvent("columnmove", index, lc-1);
                }else{
                    cm.setLocked(index, false);
                }
            break;
            default:
                index = cm.getIndexById(item.id.substr(4));
                if(index != -1){
                    if(item.checked && cm.getColumnCount(true) <= 1){
                        this.onDenyColumnHide();
                        return false;
                    }
                    cm.setHidden(index, item.checked);
                }
        }
        return true;
    },

    beforeColMenuShow : function(){
        var cm = this.cm,  colCount = cm.getColumnCount();
        this.colMenu.removeAll();
        for(var i = 0; i < colCount; i++){
            this.colMenu.add(new Ext.menu.CheckItem({
                id: "col-"+cm.getColumnId(i),
                text: cm.getColumnHeader(i),
                checked: !cm.isHidden(i),
                hideOnClick:false
            }));
        }
    },

    handleHdCtx : function(g, index, e){
        e.stopEvent();
        var hd = this.getHeaderCell(index);
        this.hdCtxIndex = index;
        var ms = this.hmenu.items, cm = this.cm;
        ms.get("asc").setDisabled(!cm.isSortable(index));
        ms.get("desc").setDisabled(!cm.isSortable(index));
        if(this.grid.enableColLock !== false){
            ms.get("lock").setDisabled(cm.isLocked(index));
            ms.get("unlock").setDisabled(!cm.isLocked(index));
        }
        this.hmenu.show(hd, "tl-bl");
    },

    handleHdOver : function(e){
        var hd = this.findHeaderCell(e.getTarget());
        if(hd && !this.headersDisabled){
            if(this.grid.colModel.isSortable(this.getCellIndex(hd))){
               this.fly(hd).addClass("x-grid-hd-over");
            }
        }
    },
    
    handleHdOut : function(e){
        var hd = this.findHeaderCell(e.getTarget());
        if(hd){
            this.fly(hd).removeClass("x-grid-hd-over");
        }
    },

    handleSplitDblClick : function(e, t){
        var i = this.getCellIndex(t);
        this.autoSizeColumn(i, true);
        this.layout();
    },

    render : function(){
        
        var cm = this.cm;
        var colCount = cm.getColumnCount();
        
        if(this.grid.monitorWindowResize === true){
            Ext.EventManager.onWindowResize(this.onWindowResize, this, true);
        }
        var header = this.renderHeaders();
        var body = this.templates.body.apply({rows:""});
        var html = this.templates.master.apply({
            lockedBody: body,
            body: body,
            lockedHeader: header[0],
            header: header[1]
        });
        
        this.updateColumns();

        this.grid.container.dom.innerHTML = html;

        this.initElements();

        this.scroller.on("scroll", this.handleScroll, this);
        this.lockedBody.on("mousewheel", this.handleWheel, this);
        this.mainBody.on("mousewheel", this.handleWheel, this);
        
        this.mainHd.on("mouseover", this.handleHdOver, this);
        this.mainHd.on("mouseout", this.handleHdOut, this);
        this.mainHd.on("dblclick", this.handleSplitDblClick, this,
                {delegate: "."+this.splitClass});

        this.lockedHd.on("mouseover", this.handleHdOver, this);
        this.lockedHd.on("mouseout", this.handleHdOut, this);
        this.lockedHd.on("dblclick", this.handleSplitDblClick, this,
                {delegate: "."+this.splitClass});

        if(this.grid.enableColumnResize !== false){
            new Ext.grid.SplitDragZone(this.grid, this.lockedHd.dom, this.mainHd.dom);
        }

        this.updateSplitters();

        if(this.grid.enableColumnMove){
            new Ext.grid.HeaderDragZone(this.grid, this.lockedHd.dom, this.mainHd.dom);
            new Ext.grid.HeaderDropZone(this.grid, this.lockedHd.dom, this.mainHd.dom);
        }

        if(this.grid.enableCtxMenu !== false){
            this.colMenu = new Ext.menu.Menu({id:this.grid.id + "-hcols-menu"});
            this.colMenu.on("beforeshow", this.beforeColMenuShow, this);
            this.colMenu.on("itemclick", this.handleHdMenuClick, this);

            this.hmenu = new Ext.menu.Menu({id: this.grid.id + "-hctx"});
            this.hmenu.add(
                {id:"asc", text: this.sortAscText, cls: "xg-hmenu-sort-asc"},
                {id:"desc", text: this.sortDescText, cls: "xg-hmenu-sort-desc"},
                "separator"
            );
            if(this.grid.enableColLock !== false){
                this.hmenu.add(
                    {id:"lock", text: this.lockText, cls: "xg-hmenu-lock"},
                    {id:"unlock", text: this.unlockText, cls: "xg-hmenu-unlock"},
                    "separator"
                );
            }
            this.hmenu.add(
                {id:"columns", text: this.columnsText, menu: this.colMenu}
            );
            this.hmenu.on("itemclick", this.handleHdMenuClick, this);

            this.grid.on("headercontextmenu", this.handleHdCtx, this);
        }

        for(var i = 0; i < colCount; i++){
            if(cm.isHidden(i)){
                this.hideColumn(i);
            }
            if(cm.config[i].align){
                this.css.updateRule(this.colSelector + i, "textAlign", cm.config[i].align);
                this.css.updateRule(this.hdSelector + i, "textAlign", cm.config[i].align);
            }
        }
        
        this.updateHeaderSortState();
        
        this.beforeInitialResize();
        this.layout(true);

        // two part rendering gives faster view to the user
        this.renderPhase2.defer(1, this);
    },

    renderPhase2 : function(){
        // render the rows now
        this.refresh();
        if(this.grid.autoSizeColumns){
            this.autoSizeColumns();
        }
    },

    beforeInitialResize : function(){
        
    },
    
    onColumnSplitterMoved : function(i, w){
        this.userResized = true;
        var cm = this.grid.colModel;
        cm.setColumnWidth(i, w, true);
        var cid = cm.getColumnId(i);
        this.css.updateRule(this.colSelector + cid, "width", (w-this.borderWidth) + "px");
        this.css.updateRule(this.hdSelector + cid, "width", (w-this.borderWidth) + "px");
        this.updateSplitters();
        this.layout();
        this.grid.fireEvent("columnresize", i, w);
    },
    
    syncRowHeights : function(startIndex, endIndex){
        if(this.grid.enableRowHeightSync === true && this.cm.getLockedCount() > 0){
            startIndex = startIndex || 0;
            var mrows = this.getBodyTable().rows;
            var lrows = this.getLockedTable().rows;
            var len = mrows.length-1;
            endIndex = Math.min(endIndex || len, len);
            for(var i = startIndex; i <= endIndex; i++){
                var m = mrows[i], l = lrows[i];
                var h = Math.max(m.offsetHeight, l.offsetHeight);
                m.style.height = l.style.height = h + "px";
            }
        }
    },
    
    layout : function(initialRender){
        var auto = this.grid.autoHeight;
        var scrollOffset = 16;
        var c = this.grid.container, cm = this.cm,
                expandCol = this.grid.autoExpandColumn,
                gv = this;
        //c.beginMeasure();

        var hasLock = this.cm.isLocked(0);

        var tbh = this.headerPanel.getHeight();
        var bbh = this.footerPanel.getHeight();

        if(auto){
            var ch = this.getBodyTable().offsetHeight + tbh + bbh + this.mainHd.getHeight();
            c.setHeight(ch+c.getBorderWidth("tb"));
        }

        if(this.grid.autoWidth){
            c.setWidth(cm.getTotalWidth()+c.getBorderWidth('lr'));
        }

        var s = this.scroller;

        var csize = c.getSize(true);

        this.el.setSize(csize.width, csize.height);

        this.headerPanel.setWidth(csize.width);
        this.footerPanel.setWidth(csize.width);

        var hdHeight = this.mainHd.getHeight();
        var vw = csize.width;
        var vh = csize.height - (tbh + bbh);

        s.setSize(vw, vh);

        var bt = this.getBodyTable();
        var ltWidth = hasLock ?
                      Math.max(this.getLockedTable().offsetWidth, this.lockedHd.dom.firstChild.offsetWidth) : 0;

        var scrollHeight = bt.offsetHeight;
        var scrollWidth = ltWidth + bt.offsetWidth;
        var vscroll = false, hscroll = false;

        this.scrollSizer.setSize(scrollWidth, scrollHeight+hdHeight);

        var lw = this.lockedWrap, mw = this.mainWrap;
        var lb = this.lockedBody, mb = this.mainBody;

        setTimeout(function(){
            var t = s.dom.offsetTop;
            var w = s.dom.clientWidth, 
                h = s.dom.clientHeight;
            
            lw.setTop(t);
            lw.setSize(ltWidth, h);
            
            mw.setLeftTop(ltWidth, t);
            mw.setSize(w-ltWidth, h);
            
            lb.setHeight(h-hdHeight);
            mb.setHeight(h-hdHeight);

            var tw;
            if(!gv.userResized && expandCol && gv.ds.getCount() > 0 && w > (tw = cm.getTotalWidth(false))){
                // high speed resize without full column calculation
                var ci = cm.getIndexById(expandCol);
                var cw = ((w-tw)+cm.getColumnWidth(ci)-2)-/*scrollbar*/(w <= s.dom.offsetWidth ? 0 : 18);
                cm.setColumnWidth(ci, cw, true);
                gv.css.updateRule(gv.colSelector+expandCol, "width", (cw - gv.borderWidth) + "px");
                gv.css.updateRule(gv.hdSelector+expandCol, "width", (cw - gv.borderWidth) + "px");
                gv.updateSplitters();
                gv.layout();
            }

            if(initialRender){
                lw.show();
                mw.show();
            }
            //c.endMeasure();
        }, 10);
    },
    
    onWindowResize : function(){
        if(!this.grid.monitorWindowResize || this.grid.autoHeight){
            return;
        }
        this.layout();
    },
    
    appendFooter : function(parentEl){
        return null;  
    },

    sortAscText : "Sort Ascending",
    sortDescText : "Sort Descending",
    lockText : "Lock Column",
    unlockText : "Unlock Column",
    columnsText : "Columns"
});
if(Ext.dd.DragZone){
Ext.grid.HeaderDragZone = function(grid, hd, hd2){
    this.grid = grid;
    this.view = grid.getView();
    this.ddGroup = "gridHeader" + this.grid.container.id;
    Ext.grid.HeaderDragZone.superclass.constructor.call(this, hd);
    this.setHandleElId(Ext.id(hd));
    this.setOuterHandleElId(Ext.id(hd2));
    this.scroll = false;
};
Ext.extend(Ext.grid.HeaderDragZone, Ext.dd.DragZone, {
    maxDragWidth: 120,
    getDragData : function(e){
        var t = Ext.lib.Event.getTarget(e);
        var h = this.view.findHeaderCell(t);
        if(h){
            return {ddel: h.firstChild, header:h};
        }
        return false;
    },
    
    onInitDrag : function(e){
        var clone = this.dragData.ddel.cloneNode(true);
        clone.style.width = Math.min(this.dragData.header.offsetWidth,this.maxDragWidth) + "px";
        this.proxy.update(clone);
        return true;
    }
});

Ext.grid.SplitDragZone = function(grid, hd, hd2){
    this.grid = grid;
    this.view = grid.getView();
    this.proxy = this.view.resizeProxy;
    Ext.grid.SplitDragZone.superclass.constructor.call(this, hd, 
        "gridSplitters" + this.grid.container.id, {
        dragElId : Ext.id(this.proxy.dom), resizeFrame:false
    });
    this.setHandleElId(Ext.id(hd));
    this.setOuterHandleElId(Ext.id(hd2));
    this.scroll = false;
};
Ext.extend(Ext.grid.SplitDragZone, Ext.dd.DDProxy, {
    fly: Ext.Element.fly,
    
    b4StartDrag : function(x, y){
        this.view.headersDisabled = true;
        this.proxy.setHeight(this.view.mainWrap.getHeight());
        var w = this.cm.getColumnWidth(this.cellIndex);
        var minw = Math.max(w-this.grid.minColumnWidth, 0);
        this.resetConstraints();
        this.setXConstraint(minw, 1000);
        this.setYConstraint(0, 0);
        this.minX = x - minw;
        this.maxX = x + 1000;
        this.startPos = x;
        Ext.dd.DDProxy.prototype.b4StartDrag.call(this, x, y);
    },
    
    
    handleMouseDown : function(e){
        ev = Ext.EventObject.setEvent(e);
        var t = this.fly(ev.getTarget());
        if(t.hasClass("x-grid-split")){
            this.cellIndex = this.view.getCellIndex(t.dom);
            this.split = t.dom;
            this.cm = this.grid.colModel;
            if(this.cm.isResizable(this.cellIndex) && !this.cm.isFixed(this.cellIndex)){
                Ext.grid.SplitDragZone.superclass.handleMouseDown.apply(this, arguments);
            }
        }
    },
    
    endDrag : function(e){
        this.view.headersDisabled = false;
        var endX = Math.max(this.minX, Ext.lib.Event.getPageX(e));
        var diff = endX - this.startPos;
        this.view.onColumnSplitterMoved(this.cellIndex, this.cm.getColumnWidth(this.cellIndex)+diff);
    },
    
    autoOffset : function(){
        this.setDelta(0,0);
    }
});
Ext.grid.HeaderDropZone = function(grid, hd, hd2){
    this.grid = grid;
    this.view = grid.getView();
    // split the proxies so they don't interfere with mouse events
    this.proxyTop = Ext.DomHelper.append(document.body, {
        tag:"div", cls:"col-move-top", html:"&#160;"
    }, true);
    this.proxyBottom = Ext.DomHelper.append(document.body, {
        tag:"div", cls:"col-move-bottom", html:"&#160;"
    }, true);
    this.proxyTop.hide = this.proxyBottom.hide = function(){
        this.setLeftTop(-100,-100);
        this.setStyle("visibility", "hidden");
    };
    this.ddGroup = "gridHeader" + this.grid.container.id;
    // temporarily disabled
    //Ext.dd.ScrollManager.register(this.view.scroller.dom);
    Ext.grid.HeaderDropZone.superclass.constructor.call(this, grid.container.dom);
};
Ext.extend(Ext.grid.HeaderDropZone, Ext.dd.DropZone, {
    proxyOffsets : [-4, -9],
    fly: Ext.Element.fly,
    
    getTargetFromEvent : function(e){
        var t = Ext.lib.Event.getTarget(e);
        var cindex = this.view.findCellIndex(t);
        if(cindex !== false){
            return this.view.getHeaderCell(cindex);
        }
        /*else{
            return this.grid.getHeaderFromChild(t);
        }*/
    },
    
    nextVisible : function(h){
        var v = this.view, cm = this.grid.colModel;
        h = h.nextSibling;
        while(h){
            if(!cm.isHidden(v.getCellIndex(h))){
                return h;
            }
            h = h.nextSibling;
        }
        return null;
    },
    
    prevVisible : function(h){
        var v = this.view, cm = this.grid.colModel;
        h = h.prevSibling;
        while(h){
            if(!cm.isHidden(v.getCellIndex(h))){
                return h;
            }
            h = h.prevSibling;
        }
        return null;
    },
    
    positionIndicator : function(h, n, e){
        var x = Ext.lib.Event.getPageX(e);
        var r = Ext.lib.Dom.getRegion(n.firstChild);
        var px, pt, py = r.top + this.proxyOffsets[1];
        if((r.right - x) <= (r.right-r.left)/2){
            px = r.right+this.view.borderWidth;
            pt = "after";
        }else{
            px = r.left;
            pt = "before";
        }
        var oldIndex = this.view.getCellIndex(h);
        var newIndex = this.view.getCellIndex(n);
        var locked = this.grid.colModel.isLocked(newIndex);
        if(pt == "after"){
            newIndex++;
        }
        if(oldIndex < newIndex){
            newIndex--;
        }
        if(oldIndex == newIndex && (locked == this.grid.colModel.isLocked(oldIndex))){
            return false;
        }
        px +=  this.proxyOffsets[0];
        this.proxyTop.setLeftTop(px, py);
        this.proxyTop.show();
        if(!this.bottomOffset){
            this.bottomOffset = this.view.mainHd.getHeight();
        }
        this.proxyBottom.setLeftTop(px, py+this.proxyTop.dom.offsetHeight+this.bottomOffset);
        this.proxyBottom.show();
        return pt;
    },
    
    onNodeEnter : function(n, dd, e, data){
        if(data.header != n){
            this.positionIndicator(data.header, n, e);
        }
    },
    
    onNodeOver : function(n, dd, e, data){
        var result = false;
        if(data.header != n){
            result = this.positionIndicator(data.header, n, e);
        }
        if(!result){
            this.proxyTop.hide();
            this.proxyBottom.hide();
        }
        return result ? this.dropAllowed : this.dropNotAllowed;
    },
    
    onNodeOut : function(n, dd, e, data){
        this.proxyTop.hide();
        this.proxyBottom.hide();
    },
    
    onNodeDrop : function(n, dd, e, data){
        var h = data.header;
        if(h != n){
            var cm = this.grid.colModel;
            var x = Ext.lib.Event.getPageX(e);
            var r = Ext.lib.Dom.getRegion(n.firstChild);
            var pt = (r.right - x) <= ((r.right-r.left)/2) ? "after" : "before";
            var oldIndex = this.view.getCellIndex(h);
            var newIndex = this.view.getCellIndex(n);
            var locked = cm.isLocked(newIndex);
            if(pt == "after"){
                newIndex++;
            }
            if(oldIndex < newIndex){
                newIndex--;
            }
            if(oldIndex == newIndex && (locked == cm.isLocked(oldIndex))){
                return false;
            }
            cm.setLocked(oldIndex, locked, true);
            cm.moveColumn(oldIndex, newIndex);
            this.grid.fireEvent("columnmove", oldIndex, newIndex);
            return true;
        }
        return false;
    }
});
}
