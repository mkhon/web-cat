/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */


Ext.UpdateManager.defaults.indicatorText = '<div class="loading-indicator">Loading...</div>';

Date.monthNames =
   ["January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"];

Date.dayNames =
   ["Sunday",
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday"];

Ext.apply(Ext.DatePicker.prototype, {
    todayText : "Today",
    todayTip : "{0} (Spacebar)",
    minText : "This date is before the minimum date",
    maxText : "This date is after the maximum date",
    format : "m/d/y",
    disabledDaysText : "",
    disabledDatesText : "",
    monthNames : Date.monthNames,
    dayNames : Date.dayNames,
    nextText: 'Next Month (Control+Right)',
    prevText: 'Previous Month (Control+Left)',
    monthYearText: 'Choose a month (Control+Up/Down to move years)'
});

Ext.MessageBox.buttonText = {
    ok : "OK",
    cancel : "Cancel",
    yes : "Yes",
    no : "No"
};

Ext.apply(Ext.PagingToolbar.prototype, {
    beforePageText : "Page",
    afterPageText : "of {0}",
    firstText : "First Page",
    prevText : "Previous Page",
    nextText : "Next Page",
    lastText : "Last Page",
    refreshText : "Refresh"
});

Ext.TabPanelItem.prototype.closeText = "Close this tab";