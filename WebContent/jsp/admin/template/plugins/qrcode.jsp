<%@page import="alain.core.utils.Config"%>
<%@page import="alain.core.utils.Cartographer"%>
<%@page import="alain.core.utils.Operator"%>

<%
Cartographer map = new Cartographer(request,response);
int formId = map.getInt("FORM_ID");
int fieldId = map.getInt("FIELD_ID",0);

String url = Config.fullcontexturl();


%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>Captcha</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
	<link rel="stylesheet" href="/tools/jquery/ui-smoothness/jquery-ui.css" />
   	<script src="/tools/jquery/ui-smoothness/jquery-ui.js"></script>
   	<link rel="stylesheet" href="/tools/jquery/multiselect/multi-select.css" />
   	<script src="/tools/jquery/multiselect/jquery.multi-select.js"></script>
   	<script src="val.js"></script>
	<link rel="stylesheet" href="field.css" />
<script type="text/javascript" >
$(function() {
	$(document).ready(function() {
		
		
		
		
	});  
    
});

function save(){
	//var $form = $('#editform');
  //  var $inputs = $form.find("input, select, button, textarea");
  //  var fieldname = $("#editform input[name=FIELD_NAME]").val();
   
   // if(fieldname == ""){
    //	alert("Field Name is required");
    //	return false;
   // }
    
   
    //var serializedData = $form.serialize();
    
    var h =  $(height).val();
   
    var c = '<img src="<%=url%>/jsp/admin/template/plugins/qrcode.png" id="qrcode" name="code" height="'+h+'" > ';
   // alert(c);
    suc(c)
 
}

function suc(o){
	var CKEDITOR   = window.parent.CKEDITOR;
	var oEditor   = CKEDITOR.instances.editorName;
	var c = o;
	var myEditor = CKEDITOR.instances.editor1;
	myEditor.insertHtml(c);
	CKEDITOR.dialog.getCurrent().hide();
}

</script>


</head>
<body>
<form action="#" id="editform" name="editform" >
	<div id="tabs">
	 
		<div id="tabs-1">
			<table cellpadding="4" cellspacing="8" border="0" width="100%">
					<tr>
						<td>Height</td> <td><input name="height" id="height" value="100"> </input></td>
						
					</tr>
					
					<tr>
						<td >Click Save to add a QRCODE</td>
						
					</tr>
					
					
					
					<tr>
						<td>
							
						</td>
					</tr>
				
					
			</table>
			
		</div>
		
		
		
		
		
	</div>


	<div style="display:none;">
		<input  type="hidden" id="formid" name="FORM_ID"  value="<%= formId%>"/>
		<input  type="hidden" id="createdby" name="CREATED_BY"  value="<%= map.username()%>"/>
		<input  type="hidden" id="updatedby" name="UPDATED_BY" value="<%= map.username()%>"/>
		<input  type="hidden" name="FIELD_TYPE"  value="captcha"/>
		<input  type="hidden" name="FIELD_VALIDATE"  value="captcha"/>
		<input  type="hidden" name="FIELD_NAME"  value="captcha"/>
	<!-- 	<input  type="hidden" name="FIELD_STYLE_ETC"  value="border: 1px solid #aaaaaa; background-color: #cccccc; height: 57px; width: 300px"/>  -->
		<input  type="hidden" name="PRINT_UI"  value="N"/>
		<input  type="hidden" id="ui" name="ui" value="0"/>
		<input  type="hidden" name="EXECUTE"  value="ADD_FIELD"/>
		
	</div>

	
</form>
</body>
</html>
