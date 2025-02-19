<%@page import="csapi.impl.admin.AdminMap"%>
<%@page import="alain.core.utils.MapSet"%>
<%@page import="csapi.common.Table"%>
<%@page import="javax.swing.SortOrder"%>
<%@page import="csapi.impl.admin.AdminAgent"%>
<%@page import="csapi.utils.CsApiConfig"%>
<%@page import="csshared.utils.ObjMapper"%>
<%@page import="alain.core.utils.Operator"%>
<%@page import="csshared.vo.ResponseVO"%>
<%@page import="org.json.JSONObject"%>
<%@page import="csshared.utils.CsConfig"%>
<%@page import="csshared.vo.SubObjVO"%>
<%@page import="alain.core.utils.Timekeeper"%>
<%@page import="alain.core.utils.Config"%>
<%@page import="csshared.vo.ToolsVO"%>
<%@page import="alain.core.utils.Logger"%>
<%@page import="csshared.vo.ObjGroupVO"%>
<%@page import="csapi.impl.admin.AdminAgent"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="csshared.vo.TypeVO"%>
<%@page import="csshared.vo.RequestVO"%>
<%@page import="alain.core.utils.Cartographer"%>
<%


	Cartographer map = new Cartographer(request,response);
	Timekeeper d = new Timekeeper();
	String fullcontexturl = CsApiConfig.getString("cs.fullcontexturl");
	String table = Table.TEMPLATETABLE;
	String alert="";
	
	String ent = map.getString("_ent");
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	int editId = map.getInt("ID");
	
	ArrayList<HashMap<String,String>> a =  new ArrayList<HashMap<String,String>>();
	ArrayList<MapSet> la = AdminAgent.getIndex(type, typeid, id,false,map);

	MapSet l = new MapSet();
	if(la.size()>0){
		l = la.get(0);
	}
	ArrayList<MapSet> history = new ArrayList<MapSet>();
	MapSet v = new MapSet();
	if(editId>0){
		v = AdminAgent.getType(editId,table);
		history = AdminAgent.getList(AdminMap.getTemplateHistory(editId));
	}
	
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 if(editId>0){
			 v.add("TEMPLATE_ID", editId);
			 result = AdminAgent.saveType(v,"TEMPLATE_HISTORY");
			 result = AdminAgent.updateType(AdminMap.getTemplateType(map),table);
		 }else {
			 result = AdminAgent.saveType(AdminMap.getTemplateType(map),table);
		 }
		 
		 if(result){
			 StringBuilder rr = new StringBuilder();
			 rr.append("list.jsp?_ent=").append(ent).append("&_id=").append(id).append("&_type=").append(type).append("&_typeid=").append(typeid).append("");
			 map.redirect(rr.toString());
		 }
		 
	 }
	 if(map.equalsIgnoreCase("_action","CANCEL")){
			result = true;
	 }
	


%>
<html>
<head>

	<title>City Smart- Admin</title>
	<link href='https://fonts.googleapis.com/css?family=Oswald:300,700' rel='stylesheet' type='text/css'>
	<link href='https://fonts.googleapis.com/css?family=Armata' rel='stylesheet' type='text/css'>
	<link href='https://fonts.googleapis.com/css?family=Roboto+Condensed:400,700' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/datetimepicker/jquery.datetimepicker.css"/>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/chosen/chosen.css"/>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/toggleswitch/css/tinytools.toggleswitch.css"/>
	<link href='<%=fullcontexturl %>/tools/alain/cs.ui.css' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" media="all" href="<%=fullcontexturl %>/tools/fancyapps/source/jquery.fancybox.css"/>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/sweetalert/dist/sweetalert.css">
	
	<style>
		.csui_controls { visibility: hidden }
		.searchbox {  -webkit-border-radius: 50px;-moz-border-radius: 50px;	border-radius: 50px;width:200px;height:25px;align:right;	}
	</style>


	<script type="text/javascript" src="<%=fullcontexturl %>/tools/jquery.min.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/alain/cs.tools.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/alain/cs.form.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/datetimepicker/jquery.datetimepicker.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/chosen/chosen.jquery.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/toggleswitch/tinytools.toggleswitch.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/autogrow/jquery.autogrowtextarea.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/tinymce/js/tinymce/jquery.tinymce.min.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/tinymce/js/tinymce/tinymce.min.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/inputmask/dist/inputmask/inputmask.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/inputmask/dist/inputmask/jquery.inputmask.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/numeric/jquery.numeric.min.js"></script>
	
 	<script type="text/javascript" src="<%=fullcontexturl %>/tools/fancyapps/source/jquery.fancybox.pack.js"></script>
    <script type="text/javascript" src="<%=fullcontexturl %>/tools/fancyapps/source/cms.fancybox.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/sweetalert/dist/sweetalert-dev.js"></script>

  	<script type="text/javascript" src="<%=fullcontexturl %>/tools/jquery-ui.min.js"></script>
  	<script language="javascript" type="text/javascript" src="../../../tools/ckeditor/ckeditor.js"></script>
	<link href='<%=fullcontexturl %>/tools/jquery-ui.css' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/ioscheckboxes/assets/css/mobileCheckbox.iOS.css">
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/ioscheckboxes/assets/js/jquery.mobileCheckbox.js"></script>
	
	<style>
		
	</style>

	<script>
		$(document).ready(function() {
		
			
			$('textarea[itype!=richtext]').autoGrow();
			tinymce.init({
            	selector: "textarea[itype=richtext]"
	        });
			$('input[itype=phone]').inputmask({
				"mask":"(999) 999-9999"
			});
			
			
			
			
			
			<% if(result){%>
				
				window.parent.$("#csform").submit();
		
				parent.$.fancybox.close();
				
				<% }%>
				
			<% if(editId>0){%>
				
			//	$('input:radio[name=online]:checked');
				
				
				 
			<% }%>
			
		});
		
		function sortrefs(order){
			var method = "sortfields";
			var find = 'tr_';
			order = order.replace(new RegExp(find, 'g'), "");
			$.ajax({
	 			  type: "POST",
	 			  url: "action.jsp?_action="+method,
	 			  dataType: 'json',		  
	 			  data: { 
	 				  _type : "<%=type%>",
	 				  _typeid : "<%=typeid%>",
	 				 _id : <%=id%>,
	 				 TEMPLATE_ID : <%=editId%>,
	 				  sortOrder : order
	 			      //mode : mode
	 			    },
	 			    success: function(output) {
	 			    	//swal("Sorted successfully ");
	 			    		
	 			    },
	 		    error: function(data) {
	 		    	swal("Problem while processing the request");  
	 		    }
			 });		
			
			
			
			
		}
	
		function deletefield(id){
			var method = "deletefield";
			swal({  
					title: "Are you sure?",   
					text: "You want to delete this record!",   
					type: "warning",   
					showCancelButton: true,   
					confirmButtonColor: "#DD6B55",   
					confirmButtonText: "Yes, delete it!",   
					cancelButtonText: "No, cancel plx!",   
					closeOnConfirm: false,   
					closeOnCancel: false 
				}, 
				function(isConfirm){   
					if (isConfirm) {     
						$.ajax({
				   			  type: "POST",
				   			  url: "<%=Config.fullcontexturl()%>/jsp/admin/template/action.jsp?_action="+method,
				   			  dataType: 'json',		  
				   			  data: { 
				   				  _type : "<%=type%>",
				   				  _typeid : "<%=typeid%>",
				   				  _id : <%=id%>,
				   				   ID : id
				   			      //mode : mode
				   			    },
				   			    success: function(output) {
				   			    		$('#tr_'+id).remove();
				   			    		swal("Deleted!", "The record has been deleted.", "success");   
				   			    		
				   			    },
				   		    error: function(data) {
				   		    	swal("Problem while perfoming delete looks like the server is busy");  
				   		    }
			   			});		
					
					} 
					else {    
						swal("Cancelled", "The record is safe :)", "error");  
					} 
				});
		}
		
		
	</script>
	
		

</head>
<body alert="<%= alert %>">
<div id="csuibody">
		<div id="csuimain">
			<div class="csuicontrol ">
				<div id="csuicontrol" class="csuicontrol">
					
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td align="left">
								<table class="csui_tools">
									<tr>
										<td class="csui_tools">
										<a target="_self" href="list.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>" ><img src="/cs/images/icons/controls/white/back.png" border="0"></a>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					
					
				</div>

			</div>
			<div class="csuicontent">
				<table cellpadding="10" cellspacing="0" border="0" width="100%">
					<tr>
						<td align="left" id="title">TEMPLATE</td>
						<td align="right" id="subtitle">&nbsp;</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" ajax="no" class="form" action="add.jsp" method="post" enctype="multipart/form-data">
					<input type="hidden" name="_ent" value="<%=ent%>">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
					<input type="hidden" name="ID" value="<%=editId%>">
					<input type="hidden" name="tabletype" value="single">
					
					<table class="csui_title">
						<tr>
							<td class="csui_title">TEMPLATE</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
							<tr>
								<td class="csui_label" colnum="2" alert="">NAME</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4"><input name="NAME" type="text" itype="text" value="<%=v.getString("NAME") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">DESCRIPTION</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4"><input name="DESCRIPTION" type="text" itype="text" value="<%=v.getString("DESCRIPTION") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">LANDSCAPE </td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" ><div><input name="LANDSCAPE" type="checkbox"  <%if(v.getString("LANDSCAPE").equals("Y")){ %>checked <% }%>itype="boolean" ></div></td>
								
								<td class="csui_label" colnum="2" alert="">INCLUDE PAGE NUMBERS</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" ><div><input name="PAGE_NUMBERS" type="checkbox"  <%if(v.getString("PAGE_NUMBERS").equals("Y")){ %>checked <% }%>itype="boolean" ></div></td>
								
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">PAGE WIDTH (inch)</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" ><div><input name="PAGE_WIDTH" type="text" itype="double" value="<%=v.getString("PAGE_WIDTH") %>"  valrequired="false" maxchar="10000"></td>
								
								<td class="csui_label" colnum="2" alert="">PAGE HEIGHT (inch)</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" ><div><input name="PAGE_HEIGHT" type="text" itype="double" value="<%=v.getString("PAGE_HEIGHT") %>"  valrequired="false" maxchar="10000"></td>
								
							</tr>
							
							
							<tr>
								<td class="csui_label" colnum="2" alert="">MARGIN LEFT</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" ><div><input name="MARGIN_LEFT" type="text" itype="double" value="<%=v.getString("MARGIN_LEFT") %>"  valrequired="false" maxchar="10000"></td>
								
								<td class="csui_label" colnum="2" alert="">MARGIN RIGHT</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" ><div><input name="MARGIN_RIGHT" type="text" itype="double" value="<%=v.getString("MARGIN_RIGHT") %>"  valrequired="false" maxchar="10000"></td>
								
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">MARGIN TOP</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" ><div><input name="MARGIN_TOP" type="text" itype="double" value="<%=v.getString("MARGIN_TOP") %>"  valrequired="false" maxchar="10000"></td>
								
								<td class="csui_label" colnum="2" alert="">MARGIN BOTTOM</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" ><div><input name="MARGIN_BOTTOM" type="text" itype="double" value="<%=v.getString("MARGIN_BOTTOM") %>"  valrequired="false" maxchar="10000"></td>
								
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">CREATED</td>
								<td class="csui" colnum="2" type="String" itype="String" alert=""><%=v.getString("CREATED") %></td>
								<td class="csui_label" colnum="2" alert="">CREATED DATE</td>
								<td class="csui" colnum="2" type="DATETIME" itype="String" alert=""><%=v.getString("C_CREATED_DATE") %></td>
							</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">UPDATED</td>
								<td class="csui" colnum="2" type="String" itype="String" alert=""><%=v.getString("UPDATED") %></td>
								<td class="csui_label" colnum="2" alert="">UPDATED DATE</td>
								<td class="csui" colnum="2" type="DATETIME" itype="String" alert=""><%=v.getString("C_UPDATED_DATE") %></td>
								
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">FILE DESIGN</td>
								<td class="csui" colnum="2" type="String" itype="String" alert="">
									<input name="FILE_DESIGN" type="file" itype="file" "  valrequired="false">
								<%if(Operator.hasValue(v.getString("FILE_DESIGN"))){ %>
								 </br>
								 	<a href="<%=Config.fullcontexturl() %>/viewfile.jsp?PATH=<%=v.getString("FILE_DESIGN") %>" target="_blank"><%=v.getString("FILE_DESIGN") %> </a> 
								 <%} %>
								</td>
								
								<td class="csui" colnum="2" type="String" itype="String" alert="" colspan="2">* Note using this feature design is not required as it uses Pre Designed File. </br>However kindly add all the fields in the editor for the fields that are used in the pre-designed file.</td>
								
							</tr>
						
							<tr>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" colspan="4">
								<div><textarea name="TEMPLATE" id="editor1" ><%=v.getString("TEMPLATE")%></textarea>
								</div></td>
								
							</tr>
							
								<script>
								
								CKEDITOR.timestamp = null;     
								CKEDITOR.config.extraAllowedContent = 'tr(*)';
								CKEDITOR.config.font_defaultLabel = 'Arial';
								CKEDITOR.config.fontSize_defaultLabel = '12px';
								CKEDITOR.config.toolbar_MA=[
								                           
								                        	//{ name: 'document', items : [ 'Save' ] },
								                        	
								                        	{ name: 'styles', items : [ 'FontSize','Font','Styles' ] },
								                        	{ name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript' ] },
								                        	{ name: 'colors', items : [ 'TextColor','BGColor' ] },
								                        	{ name: 'paragraph', items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ] },
								                        	{ name: 'editing', items : [ 'Find','Replace','-','SelectAll' ] },
														                        	
								                        	'/',
								                        	{ name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
								                        	{ name: 'links', items : [ 'Link','Unlink','Anchor' ] },
								                        	{ name: 'insert', items : [ 'Image','Table','HorizontalRule','SpecialChar','PageBreak','Iframe' ] },
								                        	{ name: 'tools', items : [ 'Source','-','Maximize', 'ShowBlocks','RemoveFormat','-','SpellChecker', 'Scayt'] },
								                        	
								                        	'/',
								                        	{ name: 'citysmart', items : [ 'csqrcode','csfields','cstablerows' ] },
								                        	//'/',
								                        	//{ name: 'bh', items : [ 'bhtext','bhtextarea','bhhidden','bhoption','bhfile','bhbutton','bhcalc','bhdate','bhcaptcha','bhpercent' ] },
								                        	
								                        	//'/',
								                        	//{ name: 'bht', items : [ 'bhdoc','bhlink','bhimage','bhnav' ] },
								                        ];
								
								CKEDITOR.replace( 'editor1', {
									fullPage: true,
									toolbar:'MA',
									extraPlugins: 'wysiwygarea',
									extraPlugins: 'csqrcode,csfields,cstablerows',
									removePlugins : 'forms',
									enterMode : CKEDITOR.ENTER_BR,
									shiftEnterMode : CKEDITOR.ENTER_BR,
									 allowedContent : true
									//uiColor:'#666666',
									//,contentsCss : '../../../tools/ckeditor/content.css'
								
								
									
								});
							
								

							</script>	
							
							
					</table>

					<div class="csui_divider">
			</div>
			<div class="csui_buttons">
				<input type="submit" name="_action" value="cancel" class="csui_button">&nbsp;
				<input type="submit" name="_action" value="save" class="csui_button">
			</div>
			
			
			
				</form>
				
				
				
				<%if(editId>0){ %>
				
				<table class="csui_title">
						<tr>
							<td class="csui_title">HISTORY</td>
							<td class="csui_title">
								<a target="lightbox-iframe" href="field/add.jsp?_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&_groupid=<%=editId%>&_groupname=<%=v.getString("GROUP_NAME") %>" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
							</td>
						

 						 </tr>
							
							
					
				</table>
				<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							
							<td class="csui_header">NAME</td>
							<td class="csui_header">DESCRIPTION</td>
							
							<td class="csui_header">UPDATED BY</td>
							<td class="csui_header">UPDATED</td>
						
							
						</tr>
						</thead>
						
						 <tbody >
						<%for(int i=0;i<history.size();i++){
							MapSet r = history.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
							
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("NAME") %>
								 <input type="hidden" id="refname_<%=r.getString("ID") %>" name="refname_<%=r.getString("ID") %>" value="<%=r.getString("NAME") %>">
								 
								</td>
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("DESCRIPTION") %></td>
								
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("UPDATED") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("C_UPDATED_DATE") %></td>
								
				
								
							</tr>
							
							
						<%} %>
						</tbody>
						
						</table>
					<%}%>
				
				
			</div>
		</div>
	</div>






</body>
</html>

