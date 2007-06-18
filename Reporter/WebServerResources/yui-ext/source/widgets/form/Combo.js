/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.form.ComboBox = function(config){
    Ext.form.ComboBox.superclass.constructor.call(this, config);
    this.addEvents({
        'expand' : true,
        'collapse' : true,
        'beforeselect' : true,
        'select' : true,
        /**
         * @event beforequery
         * Fires before all queries are processed. Return false to cancel the query or set cancel to true.
         * The event object passed has these properties:
         * <ul style="padding:5px;padding-left:16px;">
	     * <li>combo - This combo box</li>
	     * <li>query - The query</li>
	     * <li>forceAll - true to force "all" query</li>
	     * <li>cancel - set to true to cancel the query.</li>
	     * </ul>
	     * @param {Object} e The query event object
	     */
        'beforequery': true
    });
    if(this.transform){
        var s = Ext.getDom(this.transform);
        if(!this.hiddenName){
            this.hiddenName = s.name;
        }
        if(!this.store){
            this.mode = 'local';
            var d = [], opts = s.options;
            for(var i = 0, len = opts.length;i < len; i++){
                var o = opts[i];
                var value = (Ext.isIE ? o.getAttributeNode('value').specified : o.hasAttribute('value')) ? o.value : o.text;
                if(o.selected) {
                    this.value = value;
                }
                d.push([value, o.text]);
            }
            this.store = new Ext.data.SimpleStore({
                'id': 0,
                fields: ['value', 'text'],
                data : d
            });
            this.valueField = 'value';
            this.displayField = 'text';
        }
        s.name = Ext.id(); // wipe out the name in case somewhere else they have a reference
        if(!this.lazyRender){
            this.el = Ext.DomHelper.insertBefore(s, this.autoCreate || this.defaultAutoCreate);
            s.parentNode.removeChild(s); // remove it
            this.render(this.el.parentNode);
        }else{
            s.parentNode.removeChild(s); // remove it
        }

    }
    this.selectedIndex = -1;
    if(this.mode == 'local'){
        if(config.queryDelay === undefined){
            this.queryDelay = 10;
        }
        if(config.minChars === undefined){
            this.minChars = 0;
        }
    }
};

Ext.extend(Ext.form.ComboBox, Ext.form.TriggerField, {
    defaultAutoCreate : {tag: "input", type: "text", size: "24", autocomplete: "off"},
    listWidth: undefined,
    displayField: undefined,
    valueField: undefined,
    hiddenName: undefined,
    listClass: '',
    selectedClass: 'x-combo-selected',
    triggerClass : 'x-form-arrow-trigger',
    shadow:'sides',
    listAlign: 'tl-bl',
    maxHeight: 300,
    triggerAction: 'query', // can also be 'all'
    minChars : 4,
    typeAhead: false,
    queryDelay: 500,
    pageSize: 0,
    selectOnFocus:false,
    queryParam: 'query',
    loadingText: 'Loading...',
    resizable: false,
    handleHeight : 8,
    editable: true,
    allQuery: '',
    mode: 'remote',
    minListWidth : 70,
    forceSelection:false,
    typeAheadDelay : 250,
    // when using a name/value combo, if the value passed to setValue is not found in the store,
    // valueNotFoundText will be displayed
    valueNotFoundText : undefined,

    onRender : function(ct){
        Ext.form.ComboBox.superclass.onRender.call(this, ct);
        if(this.hiddenName){
            this.hiddenField = this.el.insertSibling({tag:'input', type:'hidden', name: this.hiddenName, id: this.hiddenName},
                    'before', true);
            this.hiddenField.value =
                this.hiddenValue !== undefined ? this.hiddenValue : this.value;
        }
        if(Ext.isGecko){
            this.el.dom.setAttribute('autocomplete', 'off');
        }

        var cls = 'x-combo-list';

        this.list = new Ext.Layer({
            shadow: this.shadow, cls: [cls, this.listClass].join(' '), constrain:false
        });

        this.list.setWidth(this.listWidth || this.wrap.getWidth());
        this.list.swallowEvent('mousewheel');
        this.assetHeight = 0;

        if(this.title){
            this.header = this.list.createChild({cls:cls+'-hd', html: this.title});
            this.assetHeight += this.header.getHeight();
        }

        this.innerList = this.list.createChild({cls:cls+'-inner'});
        this.innerList.on('mouseover', this.onViewOver, this);
        this.innerList.on('mousemove', this.onViewMove, this);

        if(this.pageSize){
            this.footer = this.list.createChild({cls:cls+'-ft'});
            this.pageTb = new Ext.PagingToolbar(this.footer, this.store,
                    {pageSize: this.pageSize});
            this.assetHeight += this.footer.getHeight();
        }

        if(!this.tpl){
            this.tpl = '<div class="'+cls+'-item">{' + this.displayField + '}</div>';
        }

        this.view = new Ext.View(this.innerList, this.tpl, {
            singleSelect:true, store: this.store, selectedClass: this.selectedClass
        });

        this.view.on('click', this.onViewClick, this);

        this.store.on('beforeload', this.onBeforeLoad, this);
        this.store.on('load', this.onLoad, this);
        this.store.on('loadexception', this.collapse, this);

        if(this.resizable){
            this.resizer = new Ext.Resizable(this.list,  {
               pinned:true, handles:'se'
            });
            this.resizer.on('resize', function(r, w, h){
                this.maxHeight = h-this.handleHeight-this.list.getFrameWidth('tb')-this.assetHeight;
                this.listWidth = w;
                this.restrictHeight();
            }, this);
            this[this.pageSize?'footer':'innerList'].setStyle('margin-bottom', this.handleHeight+'px');
        }
        if(!this.editable){
            this.editable = true;
            this.setEditable(false);
        }
    },

    initEvents : function(){
        Ext.form.ComboBox.superclass.initEvents.call(this);

        this.keyNav = new Ext.KeyNav(this.el, {
            "up" : function(e){
                this.inKeyMode = true;
                this.selectPrev();
            },

            "down" : function(e){
                if(!this.isExpanded()){
                    this.onTriggerClick();
                }else{
                    this.inKeyMode = true;
                    this.selectNext();
                }
            },

            "enter" : function(e){
                this.onViewClick();
                return true;
            },

            "esc" : function(e){
                this.collapse();
            },

            "tab" : function(e){
                this.onViewClick(false);
                return true;
            },

            scope : this,

            doRelay : function(foo, bar, hname){
                if(hname == 'down' || this.scope.isExpanded()){
                   return Ext.KeyNav.prototype.doRelay.apply(this, arguments);
                }
                return true;
            }
        });
        this.queryDelay = Math.max(this.queryDelay || 10,
                this.mode == 'local' ? 10 : 250);
        this.dqTask = new Ext.util.DelayedTask(this.initQuery, this);
        if(this.typeAhead){
            this.taTask = new Ext.util.DelayedTask(this.onTypeAhead, this);
        }
        if(this.editable !== false){
            this.el.on("keyup", this.onKeyUp, this);
        }
        if(this.forceSelection){
            this.on('blur', this.doForce, this);
        }
    },

    fireKey : function(e){
        if(e.isNavKeyPress() && !this.list.isVisible()){
            this.fireEvent("specialkey", this, e);
        }
    },

    onResize: function(w, h){
        if(this.list && this.listWidth === undefined){
            this.list.setWidth(Math.max(w, this.minListWidth));
        }
    },
    
    setEditable : function(value){
        if(value == this.editable){
            return;
        }
        if(!value){
            this.el.dom.setAttribute('readOnly', true);
            this.el.on('mousedown', this.onTriggerClick,  this);
            this.el.addClass('x-combo-noedit');
        }else{
            this.el.dom.setAttribute('readOnly', false);
            this.el.un('mousedown', this.onTriggerClick,  this);
            this.el.removeClass('x-combo-noedit');
        }
    },

    onBeforeLoad : function(){
        if(!this.hasFocus){
            return;
        }
        this.innerList.update(this.loadingText ?
               '<div class="loading-indicator">'+this.loadingText+'</div>' : '');
        this.restrictHeight();
        this.selectedIndex = -1;
    },

    onLoad : function(){
        if(!this.hasFocus){
            return;
        }
        if(this.store.getCount() > 0){
            this.expand();
            this.restrictHeight();
            if(this.listAlign.indexOf('?') != -1){ // constraining alignment
                this.list.alignTo(this.el, this.listAlign);
            }
            if(this.lastQuery == this.allQuery){
                if(this.editable){
                    this.el.dom.select();
                }
                if(!this.selectByValue(this.value, true)){
                    this.select(0, true);
                }
            }else{
                this.selectNext();
                if(this.typeAhead && this.lastKey != Ext.EventObject.BACKSPACE && this.lastKey != Ext.EventObject.DELETE){
                    this.taTask.delay(this.typeAheadDelay);
                }
            }
        }else{
            this.onEmptyResults();
        }
        //this.el.focus();
    },

    onTypeAhead : function(){
        if(this.store.getCount() > 0){
            var r = this.store.getAt(0);
            var newValue = r.data[this.displayField];
            var len = newValue.length;
            var selStart = this.getRawValue().length;
            if(selStart != len){
                this.setRawValue(newValue);
                this.selectText(selStart, newValue.length);
            }
        }
    },

    onSelect : function(record, index){
        if(this.fireEvent('beforeselect', this, record, index) !== false){
            this.setValue(record.data[this.valueField || this.displayField]);
            this.collapse();
            this.fireEvent('select', this, record, index);
        }
    },

    getValue : function(){
        if(this.valueField){
            return this.value;
        }else{
            return Ext.form.ComboBox.superclass.getValue.call(this);
        }
    },

    setValue : function(v){
        var text = v;
        if(this.valueField){
            var r = this.findRecord(this.valueField, v);
            if(r){
                text = r.data[this.displayField];
            }else if(this.valueNotFoundText){
                text = this.valueNotFoundText;
            }
        }
        this.lastSelectionText = text;
        if(this.hiddenField){
            this.hiddenField.value = v;
        }
        Ext.form.ComboBox.superclass.setValue.call(this, text);
        this.value = v;
    },

    findRecord : function(prop, value){
        var record;
        if(this.store.getCount() > 0){
            this.store.each(function(r){
                if(r.data[prop] == value){
                    record = r;
                    return false;
                }
            });
        }
        return record;
    },

    onViewMove : function(e, t){
        this.inKeyMode = false;
    },

    onViewOver : function(e, t){
        if(this.inKeyMode){ // prevent key nav and mouse over conflicts
            return;
        }
        var item = this.view.findItemFromChild(t);
        if(item){
            var index = this.view.indexOf(item);
            this.select(index, false);
        }
    },

    onViewClick : function(doFocus){
        var index = this.view.getSelectedIndexes()[0];
        var r = this.store.getAt(index);
        if(r){
            this.onSelect(r, index);
        }
        if(doFocus !== false){
            this.el.focus();
        }
    },

    restrictHeight : function(){
        this.innerList.dom.style.height = '';
        var inner = this.innerList.dom;
        var h = Math.max(inner.clientHeight, inner.offsetHeight, inner.scrollHeight);
        this.innerList.setHeight(h < this.maxHeight ? 'auto' : this.maxHeight);
        //if(Ext.isIE){
            this.list.setHeight(this.innerList.getHeight()+this.list.getFrameWidth('tb')+(this.resizable?this.handleHeight:0)+this.assetHeight);
        //}
        this.list.sync();
    },

    onEmptyResults : function(){
        this.collapse();
    },

    isExpanded : function(){
        return this.list.isVisible();
    },

    selectByValue : function(v, scrollIntoView){
        if(this.value !== undefined && this.value !== null){
            var r = this.findRecord(this.valueField || this.displayField, v);
            if(r){
                this.select(this.store.indexOf(r), true);
                return true;
            }
        }
        return false;
    },

    select : function(index, scrollIntoView){
        this.selectedIndex = index;
        this.view.select(index);
        if(scrollIntoView !== false){
            var el = this.view.getNode(index);
            if(el){
                this.innerList.scrollChildIntoView(el);
            }
        }
    },

    selectNext : function(){
        var ct = this.store.getCount();
        if(ct > 0){
            if(this.selectedIndex == -1){
                this.select(0);
            }else if(this.selectedIndex < ct-1){
                this.select(this.selectedIndex+1);
            }
        }
    },

    selectPrev : function(){
        var ct = this.store.getCount();
        if(ct > 0){
            if(this.selectedIndex == -1){
                this.select(0);
            }else if(this.selectedIndex != 0){
                this.select(this.selectedIndex-1);
            }
        }
    },

    onKeyUp : function(e){
        if(!e.isSpecialKey()){
            this.lastKey = e.getKey();
            this.dqTask.delay(this.queryDelay);
        }
    },

    validateBlur : function(){
        return !this.list || !this.list.isVisible();   
    },

    initQuery : function(){
        this.doQuery(this.getRawValue());
    },

    doForce : function(){
        if(this.el.dom.value.length > 0){
            this.el.dom.value =
                this.lastSelectionText === undefined ? '' : this.lastSelectionText;
        }
    },

    doQuery : function(q, forceAll){
        if(q === undefined || q === null){
            q = '';
        }
        var qe = {
            query: q,
            forceAll: forceAll,
            combo: this,
            cancel:false
        };
        if(this.fireEvent('beforequery', qe)===false || qe.cancel){
            return false;
        }
        q = qe.query;
        forceAll = qe.forceAll;
        if(forceAll === true || (q.length >= this.minChars)){
            if(this.lastQuery != q){
                this.lastQuery = q;
                if(this.mode == 'local'){
                    this.selectedIndex = -1;
                    if(forceAll){
                        this.store.clearFilter();
                    }else{
                        this.store.filter(this.displayField, q);
                    }
                    this.onLoad();
                }else{
                    this.store.baseParams[this.queryParam] = q;
                    this.store.load({
                        params: this.getParams(q)
                    });
                    this.expand();
                }
            }else{
                this.onLoad();   
            }
        }
    },

    getParams : function(q){
        var p = {};
        //p[this.queryParam] = q;
        if(this.pageSize){
            p.start = 0;
            p.limit = this.pageSize;
        }
        return p;
    },

    collapse : function(){
        if(!this.isExpanded()){
            return;
        }
        this.list.hide();
        Ext.get(document).un('mousedown', this.collapseIf, this);
        this.fireEvent('collapse', this);
    },

    collapseIf : function(e){
        if(!e.within(this.wrap) && !e.within(this.list)){
            this.collapse();
        }
    },

    expand : function(){
        if(this.isExpanded()){
            return;
        }
        this.list.alignTo(this.el, this.listAlign);
        this.list.show();
        Ext.get(document).on('mousedown', this.collapseIf, this);
        this.fireEvent('expand', this);
    },

    onTriggerClick : function(){
        if(this.disabled){
            return;
        }
        if(this.isExpanded()){
            this.collapse();
            this.el.focus();
        }else{
            this.hasFocus = true;
            this.doQuery(this.triggerAction == 'all' ?
                     this.doQuery(this.allQuery, true) : this.doQuery(this.getRawValue()));
            this.el.focus();
        }
    }
});