/**
 * Copyright (c) 2017, CKSource - Sunil. All rights reserved.
 * For contacting, http://www.edgesoftinc.com or sunvoyage
 */

/**
 * @fileOverview The "csfields" plugin. It wraps the selected block level elements with a 'csfields' element with specified styles and attributes.
 *
 */

(function () {
	var field ='';
	
	var id = getUrlVars()['FORM_ID'];
	
	CKEDITOR.plugins.add('csfields',
    {
        //requires: ['csfields.jsp'],
    
    	    init: function (editor) {
        	
            var me = this;
         
            CKEDITOR.dialog.add('csfieldsDialog', function (targetEditor) {
            	return {
                    title: 'Fields',
                    minWidth: 695,
                    minHeight: 500,
                    contents:
                       [
                       {
                           id: 'iframeGloss',
                           label: 'csfields',
                           expand: true,
                           elements:
                        [
                            {
                                type: 'html',
                                id: 'Glossary',
                                label: 'Glossary',
                                style: 'width : 100%;',
                                html: '<iframe src="/csapi/jsp/admin/template/plugins/fields.jsp?FORM_ID='+id+'&FIELD_ID='+field+'" frameborder="0" name="custom_text" id="custom_text" allowtransparency="1" style="width:100%;height:490px;margin:0;padding:0;"></iframe>'
                            }
                        ]
                       }
                       ] 
                      , buttons:[CKEDITOR.dialog.cancelButton],
                    onOk: function () {
                    	
                    },
                    onShow:function () {
                    	var link = document.getElementById('custom_text');
                    	link.src =  "/csapi/jsp/admin/template/plugins/fields.jsp?FORM_ID="+id+"&FIELD_ID="+field;
                    	field ="";
                    }
                   
                
                    
                };


            });

            editor.ui.addButton('csfields',
            {
                label: 'Fields',
                command: 'csfields',
                icon: this.path + 'icon-text.png'
            });
            
            
            var iconPath = this.path + 'images/icon.png';
           
            editor.addCommand('csfieldsDialog', new CKEDITOR.dialogCommand('csfieldsDialog'));

            editor.ui.addButton('csfields',
            {
                label: 'Fields',
                command: 'csfieldsDialog',
                icon: this.path + 'icon-text.png'
            });
            
             
            if (editor.contextMenu)
            {
            	// Code creating context menu items goes here.
            	editor.contextMenu.addListener( function( element )
            	{
            		
            		if ( element )
            			
            			
            			if (element.getAttribute('special') == 'text'){
            				 var t = element.getAttribute( 'name' );
            				 var src = element.getAttribute( 'id' );
							 field = src;
            		         var text1 = 'Edit Text - '+t;
            		         editor.addMenuItem( 'abbrItem',
            		             	{
            		             		label : text1,
            		             		icon : iconPath,
            		             		command : 'csfieldsDialog',
            		             		group : 'myGroup'
            		             	});
            				
            			}else {
            				 editor.removeMenuItem( 'abbrItem');
            			}
            		
            		if ( element && !element.isReadOnly() && !element.data( 'cke-realelement' ) )
             			return { abbrItem : CKEDITOR.TRISTATE_OFF };
            		return null;
            	});
            }
            
            
            if ( editor.contextMenu ){
            	editor.addMenuGroup( 'myGroup' );
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



