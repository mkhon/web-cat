/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 @class Ext.grid.AbstractSelectionModel
 @extends Ext.util.Observable
 @constructor
 */
Ext.grid.AbstractSelectionModel = function(){
    this.locked = false;
};

Ext.extend(Ext.grid.AbstractSelectionModel, Ext.util.Observable,  {
    /** @ignore Called by the grid automatically. Do not call directly. */
    init : function(grid){
        this.grid = grid;
        this.initEvents();
    },
    
    /**
     * Lock the selections
     */
    lock : function(){
        this.locked = true;
    },
    
    /**
     * Unlock the selections
     */
    unlock : function(){
        this.locked = false;  
    },
    
    /**
     * Returns true if the selections are locked
     * @return {Boolean}
     */
    isLocked : function(){
        return this.locked;    
    }
});