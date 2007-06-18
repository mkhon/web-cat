/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 * 
 * http://www.extjs.com/license.txt
 */

Ext.form.VTypes = function(){
    // closure these in so they are only created once.
    var alpha = /^[a-zA-Z_]+$/;
    var alphanum = /^[a-zA-Z0-9_]+$/;
    var email = /^([\w]+)(.[\w]+)*@([\w]+)(.[\w]{2,3}){1,2}$/;
    var url = /(((https?)|(ftp)):\/\/([\-\w]+\.)+\w{2,3}(\/[%\-\w]+(\.\w{2,})?)*(([\w\-\.\?\\\/+@&#;`~=%!]*)(\.\w{2,})?)*\/?)/i;

    // All these messages and functions are configurable
    return {
        'email' : function(v){
            return email.test(v);
        },
        'emailText' : 'This field should be an e-mail address in the format "user@domain.com"',
        'emailMask' : /[a-z0-9_\.\-@]/i,

        'url' : function(v){
            return url.test(v);
        },
        'urlText' : 'This field should be a URL in the format "http:/'+'/www.domain.com"',
        

        'alpha' : function(v){
            return alpha.test(v);
        },
        'alphaText' : 'This field should only contain letters and _',
        'alphaMask' : /[a-z_]/i,

        'alphanum' : function(v){
            return alphanum.test(v);
        },
        'alphanumText' : 'This field should only contain letters, numbers and _',
        'alphanumMask' : /[a-z0-9_]/i
    };
}();