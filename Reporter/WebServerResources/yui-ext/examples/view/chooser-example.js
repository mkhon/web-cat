/*
 * Ext - JS Library 1.0 Alpha 3 - Rev 4
 * Copyright(c) 2006-2007, Jack Slocum.
 */

Ext.onReady(function(){
    var chooser, btn;
    
    function insertImage(data){
    	Ext.DomHelper.append('images', {
    		tag: 'img', src: data.url, style:'margin:10px;visibility:hidden;'
    	}, true).show(true);
    	btn.getEl().focus();
    };
    
    function choose(btn){
    	if(!chooser){
    		chooser = new ImageChooser({
    			url:'get-images.php',
    			width:515, 
    			height:400
    		});
    	}
    	chooser.show(btn.getEl(), insertImage);
    };
    
    btn = new Ext.Button('buttons', {
	    text: "Insert Image",
		handler: choose
	});
});
