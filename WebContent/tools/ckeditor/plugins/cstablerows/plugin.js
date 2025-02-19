/**
 * Copyright (c) 2017, custom - Sunil. All rights reserved.
 * For contacting, http://www.edgesoftinc.com or sunvoyage
 */
  

(function () {
	var field ='';
	
	var id = 58;
	
	CKEDITOR.plugins.add('cstablerows',
		    {
				init: function (editor) {
				var iconPath = this.path + 'images/icon.png';
				editor.addCommand('cstablerowsDialog', new CKEDITOR.dialogCommand('cstablerowsDialog'));
			     editor.ui.addButton('cstablerows',
			     {
			         label: 'List Row Config',
			         command: 'cstablerowsDialog',
			         icon: this.path + 'icon-text.png'
			     });
	
		 	  
          
        CKEDITOR.dialog.add('cstablerowsDialog', function (editor) {
        	
        	return {
                title: 'EDIT ROW LIST',
                minWidth: 695,
                minHeight: 500,
                contents:
                   [
	                   {
	                       id: 'info',
	                       label: 'cstablerowsDialog',
	                       expand: true,
	                       elements: [{
		                            type: 'text',
		                            id: 'rowtype',
		                            label: 'Enter List Style',
		                            style: 'width : 100%;',
		                           "default": "",
		                            onShow:function () {
		                           	  var selection = editor.getSelection();
		                           	  var element = selection.getStartElement();
		                           	  var p = element.getParents();
		                           	
		                           	  for ( var i = 0; i < p.length; ++i ) {
		                           		  var e = p[i];
			                           	  if(e.getName()=="tr"){
			                           		  var lty = e.getAttribute( 'rowtype');
					                          if(lty!=null || lty!=undefined){
					                        	  this.setValue(lty);
					                           }
			                           	    }
			                           	}
		                          }
		                        }
		                    ]
	                   }
                   ],
                 onOk: function(){
                	 var str = this.getContentElement('info', 'rowtype').getValue();
                	 var selection = editor.getSelection();
                     var element = selection.getStartElement();
                     var p = element.getParents();
                    	for ( var i = 0; i < p.length; ++i ) {
                    	    var e = p[i];
                    	    if(e.getName()=="tr"){
                    	    	e.setAttribute( 'rowtype',str);
                    	    }
                    		
                    	}
                   
                    // if(element.getName()=="td"){
                   	//  element.getParent().setAttribute( 'rowtype',str);
                    // }
                 }
                
            };


        });
      
            
            
            
           
		  }   });     //end bgin 
             
            
            
            
       
            
         
            
            
         
    
   
	 
      
       
   
})();




