/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 */

if(Cookies.get('extlib') == 'jquery'){
    document.write([
       '<script type="text/javascript" src="../../jquery.js"></scri','pt>',
       '<script type="text/javascript" src="../jquery-plugins.js"></scr','ipt>',
       '<script type="text/javascript" src="../../ext-jquery-adapter.js"></scr','ipt>'].join(''));
}else{
    document.write([
       '<script type="text/javascript" src="../../yui-utilities.js"></scr','ipt>',
       '<script type="text/javascript" src="../../ext-yui-adapter.js"></scr','ipt>'].join(''));
}
var xtheme = Cookies.get('exttheme');
if(xtheme && xtheme != 'default'){
    document.write('<link rel="stylesheet" type="text/css" href="../../resources/css/ytheme-'+xtheme+'.css" />');
}