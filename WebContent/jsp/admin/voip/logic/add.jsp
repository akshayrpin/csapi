


<%@page import="alain.core.utils.FileUtil"%>
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
	String table = Table.LKUPVOIPLOGICTABLE;
	String alert="";
	
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
	ArrayList<MapSet> fields = new ArrayList<MapSet>();
	MapSet v = new MapSet();
	if(editId>0){
		v = AdminAgent.getType(editId,table);
		//fields = AdminAgent.getList(AdminMap.getLibraryFields(editId));
	}
	
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 if(editId>0){
			 result = AdminAgent.updateType(AdminMap.getVoipLogic(map),table);
		 }else {
			 result = AdminAgent.saveType(AdminMap.getVoipLogic(map),table);
		 }
		String path = CsApiConfig.getString("voippath");
		FileUtil.deleteCache(path+"logic.json"); 
		 
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
	<link href='<%=fullcontexturl %>/tools/jquery-ui.css' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/ioscheckboxes/assets/css/mobileCheckbox.iOS.css">
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/ioscheckboxes/assets/js/jquery.mobileCheckbox.js"></script>
	
	<style>
		
	</style>

	<script>
		$(document).ready(function() {
			
						
			<% if(result){%>
				
				window.parent.$("#csform").submit();
		
				parent.$.fancybox.close();
				
				<% }%>
				
			<% if(editId>0){%>
				
				//$('input:radio[name=online]:checked');
				
				
				 $('#refs').sortable({
						update: function saveOrder(){
							var result = $('#refs').sortable('toArray');
							var order ="";
							for (var i = 0; i < result.length; i++) {
								order += result[i] + ",";
							}
							
							if(order != ''){
								order = order.substring(0, order.length-1);
								//alert(order);
								sortrefs(order);
							}
							else {
								return false;
							}
						}
					});	
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
	 				  _typeid : <%=typeid%>,
	 				 _id : <%=id%>,
	 				 LIBRARY_GROUP_ID : <%=editId%>,
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
				   			  url: "<%=Config.fullcontexturl()%>/jsp/admin/library/action.jsp?_action="+method,
				   			  dataType: 'json',		  
				   			  data: { 
				   				  _type : "<%=type%>",
				   				  _typeid : <%=typeid%>,
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
											&nbsp;
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
						<td align="left" id="title">LOGIC</td>
						<td align="right" id="subtitle">&nbsp;</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" action="add.jsp" method="post">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
					<input type="hidden" name="ID" value="<%=editId%>">
					<input type="hidden" name="tabletype" value="single">
					
					<table class="csui_title">
						<tr>
							<td class="csui_title">LOGIC</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
							<tr>
								<td class="csui_label" colnum="2" alert="">LOGIC</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
								<%=v.getString("LOGIC") %> </td>
							</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">ENABLE VOICE</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" colspan="4"><div><input name="ENABLE_VOICE" type="checkbox" itype="boolean" <%if(v.getString("ENABLE_VOICE").equals("Y")){ %>checked <% }%> ></div></td>
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
					</table>

					<div class="csui_divider">
			</div>
			<div class="csui_buttons">
				<input type="submit" name="_action" value="cancel" class="csui_button">&nbsp;
				<input type="submit" name="_action" value="save" class="csui_button">
			</div>
				</form>
				
				
				
				
				
			</div>
		</div>
	</div>






</body>
</html>
