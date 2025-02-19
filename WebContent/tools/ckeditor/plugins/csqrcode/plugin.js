/**
 * Copyright (c) 2017, CKSource - Sunil. All rights reserved.
 * For contacting, http://www.edgesoftinc.com or sunvoyage
 */

/**
 * @fileOverview The "csqrcode" plugin. It wraps the selected block level elements with a 'csqrcode' element with specified styles and attributes.
 *
 */

(function () {
	var field ='';
	
	var id = getUrlVars()['FORM_ID'];
	
	CKEDITOR.plugins.add('csqrcode',
    {
        //requires: ['csqrcode.jsp'],
    
    	    init: function (editor) {
        	
            var me = this;
         
            CKEDITOR.dialog.add('csqrcodeDialog', function (targetEditor) {
            	return {
                    title: 'QR CODE',
                    minWidth: 695,
                    minHeight: 500,
                    contents:
                       [
                       {
                           id: 'iframeGloss',
                           label: 'csqrcode',
                           expand: true,
                           elements:
                        [
                            {
                                type: 'html',
                                id: 'Glossary',
                                label: 'Glossary',
                                style: 'width : 100%;',
                                html: '<iframe src="/csapi/jsp/admin/template/plugins/qrcode.jsp?FORM_ID='+id+'&FIELD_ID='+field+'" frameborder="0" name="custom_QrCode" id="custom_QrCode" allowtransparency="1" style="width:100%;height:490px;margin:0;padding:0;"></iframe>'
                            }
                        ]
                       }
                       ] 
                      , buttons:[{
                    	   type:'button',
                    	   id:'okBtn',
                    	   label: 'Save',
                    	   style: 'background-color:#666666; padding:6px;',
                    	   onClick: function(){
                    		   window.frames['custom_QrCode'].save();
                    	   }
                    	  }, CKEDITOR.dialog.cancelButton],
                    onOk: function () {
                    	
                    },
                    onShow:function () {
                    	var link = document.getElementById('custom_QrCode');
                    	link.src =  "/csapi/jsp/admin/template/plugins/qrcode.jsp?FORM_ID="+id+"&FIELD_ID="+field;
                    	field ="";
                    }
                   
                
                    
                };


            });

            editor.ui.addButton('csqrcode',
            {
                label: 'QrCode',
                command: 'csqrcode',
                icon: this.path + 'icon-captcha.png'
            });
            
            
            var iconPath = this.path + 'images/icon.png';
           
            editor.addCommand('csqrcodeDialog', new CKEDITOR.dialogCommand('csqrcodeDialog'));

            editor.ui.addButton('csqrcode',
            {
                label: 'QrCode',
                command: 'csqrcodeDialog',
                icon: this.path + 'icon-captcha.png'
            });
            
             
            if (editor.contextMenu)
            {
            	// Code creating context menu items goes here.
            	editor.contextMenu.addListener( function( element )
            	{
            		
            		if ( element )
            			
            			if (element.getAttribute('special') == 'QrCode1'){
           				 var t = element.getAttribute( 'name' );
           				 var src = element.getAttribute( 'id' );
							 field = src;
           		         var text1 = 'Edit QrCode - '+t;
           		         editor.addMenuItem( 'QrCodeItem',
           		             	{
           		             		label : text1,
           		             		icon : iconPath,
           		             		command : 'csqrcodeDialog',
           		             		group : 'myGroup'
           		             	});
            				
            			}else {
            				 editor.removeMenuItem( 'QrCodeItem');
            			}
            		
            		if ( element && !element.isReadOnly() && !element.data( 'cke-realelement' ) )
             			return { QrCodeItem : CKEDITOR.TRISTATE_OFF };
            		return null;
            	});
            }
            
            
          
            
        }
   
    
    });
})();

function getUrlVars() {
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}



