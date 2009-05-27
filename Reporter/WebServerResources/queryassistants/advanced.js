dojo.provide("webcat.reporter.AdvancedQueryAssistant");

(function() {

    var deadKeys = {};
    deadKeys[dojo.keys.TAB] = true;
    deadKeys[dojo.keys.ENTER] = true;
    deadKeys[dojo.keys.ESCAPE] = true;
    deadKeys[dojo.keys.LEFT_ARROW] = true;
    deadKeys[dojo.keys.RIGHT_ARROW] = true;
    deadKeys[dojo.keys.UP_ARROW] = true;
    deadKeys[dojo.keys.DOWN_ARROW] = true;
    deadKeys[dojo.keys.HOME] = true;
    deadKeys[dojo.keys.END] = true;
    deadKeys[dojo.keys.PAGE_UP] = true;
    deadKeys[dojo.keys.PAGE_DOWN] = true;

    webcat.reporter.isEventKeyDead = function(event)
    {
        return (event.keyCode == null || (deadKeys[event.keyCode] || false));
    };

})();

dojo.declare("webcat.reporter.AdvancedQueryAssistant", null,
{
    //~ Properties ...........................................................

    _updateRowTimeoutIds: [],


    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    constructor: function(proxy, idPrefix)
    {
        this.proxy = proxy;
        this.idPrefix = idPrefix;
    },
    
    
    // ----------------------------------------------------------
    _completeId: function(suffix)
    {
        return this.idPrefix + '_' + suffix;
    },


    // ----------------------------------------------------------
    _dojoById: function(suffix)
    {
        return dojo.byId(this._completeId(suffix));
    },


    // ----------------------------------------------------------
    _dijitById: function(suffix)
    {
        return dijit.byId(this._completeId(suffix));
    },


    // ----------------------------------------------------------
    startBusyForRow: function(index)
    {
        this._dojoById('busy_' + index).style.visibility = 'visible';
    },


    // ----------------------------------------------------------
	stopBusyForRow: function(index)
	{
	    this._dojoById('busy_' + index).style.visibility = 'hidden';
	},


    // ----------------------------------------------------------
	enqueueRowUpdate: function(widget, event, index)
	{
	    if (webcat.reporter.isEventKeyDead(event))
	    {
	        return;
	    }

        this.startBusyForRow(index);

        if (this._updateRowTimeoutIds[index])
        {
	       clearTimeout(this._updateRowTimeoutIds[index]);
	    }

	    this._updateRowTimeoutIds[index] = setTimeout(
	        dojo.hitch(this, function() {
	           this.proxy.immediatelySetKeyPathAtIndex(
	               dojo.hitch(this, function() {
		               var fn = this._completeId("updateRowAfterKeyPath_" + index); 
		               eval(fn + "(dijit.byId('" + this._completeId("keyPath_" + index) + "'));");
		               delete this._updateRowTimeoutIds[index];
	               }),
	               widget.getValue(), index);
	        }),
	        500);
	},


    // ----------------------------------------------------------
    updateCastType: function(widget, event, index)
    {
        this.startBusyForRow(index);

        this.proxy.immediatelySetCastTypeAtIndex(
            dojo.hitch(this, function() {
                var fn = this._completeId("updateRowAfterCastType_" + index); 
                eval(fn + "(dijit.byId('" + this._completeId("castType_" + index) + "'));");
            }),
            widget.getValue(), index);
    },


    // ----------------------------------------------------------
    updateComparison: function(widget, event, index)
    {
        this.startBusyForRow(index);

        var castTypeWidget = dijit.byId(this._completeId("castType_" + index));
        var castType = castTypeWidget ? castTypeWidget.getValue() : 0;

        this.proxy.immediatelySetComparisonAtIndex(
            dojo.hitch(this, function() {
                var fn = this._completeId("updateRowAfterComparison_" + index); 
                eval(fn + "(dijit.byId('" + this._completeId("comparison_" + index) + "'));");
            }),
            widget.getValue(), castType, index);
    },


    // ----------------------------------------------------------
    updateComparandType: function(widget, event, index)
    {
        this.startBusyForRow(index);

        this.proxy.immediatelySetComparandTypeAtIndex(
            dojo.hitch(this, function() {
                var fn = this._completeId("updateRowAfterComparandType_" + index); 
                eval(fn + "(dijit.byId('" + this._completeId("comparandType_" + index) + "'));");
            }),
            widget.getValue(), index);
    }
});
