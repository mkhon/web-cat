/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

/**
 * @class Ext.form.DateField
 * @extends Ext.form.TextField
Provides a date editor field, and optionally a DatePicker. The DateEditor provides a method to override (showCalendar) if you don't want to use the built in DatePicker control. The reason I chose to use my own DatePicker control rather than the nice YUI Calendar component is my control was very easy to override events to make it work well with the grid. its also only 5k compressed, while the YUI Calendar is 40k compressed. The DatePicker supports left/right keys to move months, up/down keys to move years and the mouse wheel to quickly go through the months. The DateEditor supports the following configuration options:
<ul class="list">
<li><i>format</i> - The date format for the editor. The format is identical to <a href="http://www.php.net/date">PHP date()</a> and text is allowed. Credit for that goes to <a style="font-weight:normal;" href="http://www.xaprb.com/blog/2006/05/14/javascript-date-formatting-benchmarks/">this fantastic date library</a>. This format is for the editor only and doesn't affect the rendering of the cell when not in edit mode. Your rendering function can use any date format it wants.</li>
<li><i>minValue</i> - The minimum allowed date. Can be either a Javascript date object or a string date in the specified format.</li>
<li><i>maxValue</i> - The maximum allowed date. Can be either a Javascript date object or a string date in the specified format.</li>
<li><i>minText</i> - The tooltip to display when the date in the cell is before minValue.</li>
<li><i>maxText</i> - The tooltip to display when the date in the cell is after maxValue.</li>
<li><i>invalidText</i> - The text to display when the date in the field is invalid (for example: 02/31/06)</li>
<li><i>disabledDays</i> - An array of days to disable, 0 based. For example, [0, 6] disables Sunday and Saturday.</li>
<li><i>disabledDaysText</i> - The tooltip to display when the date in the cell (or DatePicker) falls on a disabled day.</li>
<li><i>disabledDates</i> - An array of "dates" to disable, as strings. These strings will be used to build a dynamic regular expression so they are very powerful. For example, ["03/08/2003", "09/16/2003"] would disable those dates, but ["03/08", "09/16"] would disable them for every year. If you are using short years, you will want to use ^ to tell the regular expression to only match the beginning like ["^03/08"]. To disable March of 2006: ["03/../2006"] or every March ["^03"]. In order to support regular expressions, if you are using a date format that has "." in it, you will have to  escape the dot when restricting dates. For example: ["03\\.08\\.03"].</li>
<li><i>disabledDatesText</i> - The tooltip to display when the date in the cell (or DatePicker) falls on a disabled date.</li>
<li><i>allowBlank</i> - True if the cell is allowed to be empty.</li>
<li><i>blankText</i> - The tooltip (error message) to display when the cell is empty and is not allowed to be.</li>
<li><i>validator</i> - Any custom validation function you want called. The function must return true if the data is valid or an error message otherwise.</li>
<li><i>validationDelay</i> - The delay in milliseconds for validation. Each time the user types something the field is validated after a specified delay, setting this value allows you to customize that delay (for example, if your custom validation routine is slow).</li>
</ul>
For more information on using this editor, see <a href="http://www.jackslocum.com/yui/2006/09/10/adding-built-in-editing-support-to-the-yahoo-ui-extensions-grid/">this blog post</a>.
* @constructor
* Create a new DateField
* @param {Object} config
 */
Ext.form.DateField = function(config){
    Ext.form.DateField.superclass.constructor.call(this, config);
    if(typeof this.minValue == "string") this.minValue = this.parseDate(this.minValue);
    if(typeof this.maxValue == "string") this.maxValue = this.parseDate(this.maxValue);
    this.ddMatch = null;
    if(this.disabledDates){
        var dd = this.disabledDates;
        var re = "(?:";
        for(var i = 0; i < dd.length; i++){
            re += dd[i];
            if(i != dd.length-1) re += "|";
        }
        this.ddMatch = new RegExp(re + ")");
    }
};

Ext.extend(Ext.form.DateField, Ext.form.TriggerField,  {
    triggerClass : 'x-form-date-trigger',
    defaultAutoCreate : {tag: "input", type: "text", size: "10", autocomplete: "off"},

    validateValue : function(value){
        value = this.formatDate(value);
        if(!Ext.form.DateField.superclass.validateValue.call(this, value)){
            return false;
        }
        if(value.length < 1){ // if it"s blank and textfield didn"t flag it then it's valid
             return true;
        }
        var svalue = value;
        value = this.parseDate(value);
        if(!value){
            this.markInvalid(String.format(this.invalidText, svalue, this.format));
            return false;
        }
        var time = value.getTime();
        if(this.minValue && time < this.minValue.getTime()){
            this.markInvalid(String.format(this.minText, this.formatDate(this.minValue)));
            return false;
        }
        if(this.maxValue && time > this.maxValue.getTime()){
            this.markInvalid(String.format(this.maxText, this.formatDate(this.maxValue)));
            return false;
        }
        if(this.disabledDays){
            var day = value.getDay();
            for(var i = 0; i < this.disabledDays.length; i++) {
            	if(day === this.disabledDays[i]){
            	    this.markInvalid(this.disabledDaysText);
                    return false;
            	}
            }
        }
        var fvalue = this.formatDate(value);
        if(this.ddMatch && this.ddMatch.test(fvalue)){
            this.markInvalid(String.format(this.disabledDatesText, fvalue));
            return false;
        }
        return true;
    },

    validateBlur : function(){
        return !this.menu || !this.menu.isVisible();
    },

    getValue : function(){
        return this.parseDate(Ext.form.DateField.superclass.getValue.call(this)) || "";
    },

    setValue : function(date){
        Ext.form.DateField.superclass.setValue.call(this, this.formatDate(this.parseDate(date)));
    },

    parseDate : function(value){
        return (!value || value instanceof Date) ?
               value : Date.parseDate(value, this.format);
    },

    formatDate : function(date){
        return (!date || !(date instanceof Date)) ?
               date : date.dateFormat(this.format);
    },

    menuListeners : {
        select: function(m, d){
            this.setValue(d);
        },
        show : function(){ // retain focus styling
            this.el.addClass(this.focusClass);
        },
        hide : function(){
            this.focus();
            var ml = this.menuListeners;
            this.menu.un("select", ml.select,  this);
            this.menu.un("show", ml.show,  this);
            this.menu.un("hide", ml.hide,  this);
        }
    },

    onTriggerClick : function(){
        if(this.disabled){
            return;
        }
        if(this.menu == null){
            this.menu = new Ext.menu.DateMenu();
        }
        Ext.apply(this.menu,  {
            minDate : this.minValue,
            maxDate : this.maxValue,
            disabledDatesRE : this.ddMatch,
            disabledDatesText : this.disabledDatesText,
            disabledDays : this.disabledDays,
            disabledDaysText : this.disabledDaysText,
            format : this.format,
            minText : String.format(this.minText, this.formatDate(this.minValue)),
            maxText : String.format(this.maxText, this.formatDate(this.maxValue))
        });
        this.menu.on(Ext.apply({}, this.menuListeners, {
            scope:this
        }));
        this.menu.picker.setValue(this.getValue() || new Date());
        this.menu.show(this.el, "tl-bl?");
    },

    format : "m/d/y",
    disabledDays : null,
    disabledDaysText : "Disabled",
    disabledDates : null,
    disabledDatesText : "Disabled",
    minValue : null,
    maxValue : null,
    minText : "The date in this field must be after {0}",
    maxText : "The date in this field must be before {0}",
    invalidText : "{0} is not a valid date - it must be in the format {1}"
});