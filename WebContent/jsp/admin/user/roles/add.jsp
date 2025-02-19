


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
<%@page import="csapi.utils.*"%>
<%


	Cartographer map = new Cartographer(request,response);
	Timekeeper d = new Timekeeper();
	String fullcontexturl = CsApiConfig.getString("cs.fullcontexturl");
	String table = Table.LKUPROLESTABLE;
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
	
	ArrayList<MapSet> project = new ArrayList<MapSet>();
	ArrayList<MapSet> activity = new ArrayList<MapSet>();
	ArrayList<MapSet> modules = new ArrayList<MapSet>();
	ArrayList<MapSet> reviews = new ArrayList<MapSet>();
	ArrayList<MapSet> custom = new ArrayList<MapSet>();
	ArrayList<MapSet> tabs = new ArrayList<MapSet>();
	ArrayList<MapSet> admins = new ArrayList<MapSet>();
	
	
	
	MapSet v = new MapSet();
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 if(editId>0){
			 result = AdminAgent.updateType(AdminMap.getRole(map),table);
			 CsDeleteCache.deleteRolesCache();
		 }else {
			 result = AdminAgent.saveType(AdminMap.getRole(map),table);
			 CsDeleteCache.deleteRolesCache();
		 }
		 
		 
	 }
	 if(map.equalsIgnoreCase("_action","CANCEL")){
			result = true;
	 }
	
	
	if(editId>0){
		v = AdminAgent.getType(editId,table);
		//fields = AdminAgent.getList(AdminMap.getLibraryFields(editId));
		project = AdminAgent.getList("select RPR.*,LPT.TYPE,LPT.DESCRIPTION from REF_PROJECT_TYPE_ROLES RPR JOIN LKUP_PROJECT_TYPE LPT on RPR.LKUP_PROJECT_TYPE_ID = LPT.ID WHERE RPR.ACTIVE ='Y' AND LKUP_ROLES_ID="+editId+" ORDER BY TYPE");
		activity = AdminAgent.getList("select RPR.*,LPT.TYPE,LPT.DESCRIPTION from REF_ACT_TYPE_ROLES RPR JOIN LKUP_ACT_TYPE LPT on RPR.LKUP_ACT_TYPE_ID = LPT.ID WHERE RPR.ACTIVE ='Y' AND LKUP_ROLES_ID="+editId+" ORDER BY TYPE");
		modules = AdminAgent.getList("select RPR.*,LPT.MODULE from REF_MODULE_ROLES RPR JOIN LKUP_MODULE LPT on RPR.LKUP_MODULE_ID = LPT.ID WHERE RPR.ACTIVE ='Y' AND LKUP_ROLES_ID="+editId+" ORDER BY MODULE ");
		reviews = AdminAgent.getList("select RPR.*,LPT.GROUP_NAME,LPT.DESCRIPTION from REF_REVIEW_GROUP_ROLES RPR JOIN REVIEW_GROUP LPT on RPR.REVIEW_GROUP_ID = LPT.ID WHERE RPR.ACTIVE ='Y' AND LKUP_ROLES_ID="+editId+" ORDER BY GROUP_NAME  ");
		custom = AdminAgent.getList("select RPR.*,LPT.GROUP_NAME from REF_FIELD_GROUPS_ROLES RPR JOIN FIELD_GROUPS LPT on RPR.FIELD_GROUPS_ID = LPT.ID WHERE RPR.ACTIVE ='Y' AND LKUP_ROLES_ID="+editId+"  ORDER BY GROUP_NAME ");
		tabs = AdminAgent.getList("select RTR.*,LT.TAB from REF_TAB_ROLES RTR JOIN LKUP_TAB LT on RTR.LKUP_TAB_ID = LT.ID WHERE RTR.ACTIVE ='Y' AND RTR.LKUP_ROLES_ID="+editId+"  ORDER BY TAB ");
		admins = AdminAgent.getList("select RAR.*,LT.NAME,LT.DESCRIPTION from REF_ADMIN_ROLES RAR JOIN LKUP_ADMIN_MODULE LT on RAR.LKUP_ADMIN_MODULE_ID = LT.ID AND LT.ACTIVE='Y' WHERE RAR.ACTIVE ='Y' AND RAR.LKUP_ROLES_ID="+editId+"  ORDER BY LT.NAME ");

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
		<link href="<%=fullcontexturl %>/tools/zozotabs_6.5/css/zozo.tabs.min.css" rel="stylesheet">
	
		<script type="text/javascript" src="<%=fullcontexturl %>/tools/zozotabs_6.5/js/zozo.tabs.min.js"></script>
	
	<style>
		
	</style>

	<script>
		$(document).ready(function() {
			
			
			 $("#tabbed-nav").zozoTabs({
			        theme: "silver",      
			        animation: {
			            duration: 800,
			            effects: "slideH"
			        },
			        rememberState: true   
			
			    })
			    
			    
			$('input[itype=datetime]').datetimepicker({
				formatTime:'g:i A',
				step: 1
			});
			$('input[itype=availability]').datetimepicker({
				timepicker:false,
				format:'Y/m/d'
			});
			$('input[itype=date]').datetimepicker({
				timepicker:false,
				format:'Y/m/d'
			});
			$('select:not([itype=boolean]):not([valrequired=true])').chosen({
				width:'100%',
				disable_search_threshold: 10,
				allow_single_deselect: true
			});
			$('select:not([itype=boolean])[valrequired=true]').chosen({
				width:'100%',
				disable_search_threshold: 10
			});
			
			
			
			
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
				 
				 
				 
				 
				 
				 $('.ajauto').change(function(){
						var id = $(this).attr("rel");
						var type = $(this).attr("id");
						var tb = $(this).attr("tb");
						var v = "N";
						 if($(this).is(":checked")) {
							 v = "Y";
						 }
					   var method = "updateflags";
					   
						 $.ajax({
				 			  type: "POST",
				 			  url: "action.jsp?_action="+method,
				 			  dataType: 'json',		  
				 			  data: { 
				 				  type : type,
				 				  val : v,
				 				  ID : id,
				 				  tb : tb
				 				  //mode : mode
				 			    },
				 			    success: function(output) {
				 			    	//swal("Sorted successfully ");
				 			    		
				 			    },
				 		    error: function(data) {
				 		    	swal("Problem while processing the request");  
				 		    }
						 });	
						

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
					text: "You want to delete this record?",   
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
				   			    		swal({
				   			    			title: "Deleted!",
				   			    			text: "The records has been deleted.",
				   			    			timer: 500,
				   			    			showConfirmButton: false
				   			    		});
				   			    		
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
		
		
		function deletetype(id,ref){
			var method = "deleterefroles";
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
				   			  url: "action.jsp?_action="+method,
				   			  dataType: 'json',		  
				   			  data: { 
				   				   ref : ref,
				   				   ID : id
				   			    },
				   			    success: function(output) {
				   			    		$('#tr_'+id).remove();
				   			    		swal({
				   			    			title: "Deleted!",
				   			    			text: "The records has been deleted.",
				   			    			timer: 200,
				   			    			showConfirmButton: false
				   			    		});
				   			    		
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
										<a  href="list.jsp?_ent=permit&_id=<%=id %>&_type=<%=type %>&_typeid=<%=typeid %>" ><img src="/cs/images/icons/controls/white/back.png" border="0"></a>

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
						<td align="left" id="title">ROLE</td>
						<td align="right" id="subtitle">&nbsp;</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" ajax="no" class="form" action="add.jsp" method="post">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
					<input type="hidden" name="ID" value="<%=editId%>">
					<input type="hidden" name="tabletype" value="single">
					
					<table class="csui_title">
						<tr>
							<td class="csui_title">ROLE</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
							<tr>
								<td class="csui_label" colnum="2" alert="">ROLE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4"><input name="ROLE" type="text" itype="text" value="<%=v.getString("ROLE") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">DESCRIPTION</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4"><input name="DESCRIPTION" type="text" itype="text" value="<%=v.getString("DESCRIPTION") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">ADMIN</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input name="ADMIN" type="checkbox"  <%if(v.getString("ADMIN").equals("Y")){ %>checked <% }%>itype="boolean" ></div></td>		
								
								<td class="csui_label" colnum="2" alert="">EVERYONE</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input name="EVERYONE" type="checkbox"  <%if(v.getString("EVERYONE").equals("Y")){ %>checked <% }%> itype="boolean" ></div></td>		
							</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">STAFF</td>
								<td class="csui" type="boolean" itype="boolean" alert="" colnum="2" ><div><input name="STAFF" type="checkbox"  <%if(v.getString("STAFF").equals("Y")){ %>checked <% }%> itype="boolean" ></div></td>
								
								<td class="csui_label" colnum="2" alert="">PEOPLE</td>
								<td class="csui" type="boolean" itype="boolean" alert="" colnum="2" ><div><input name="PEOPLE" type="checkbox"  <%if(v.getString("PEOPLE").equals("Y")){ %>checked <% }%> itype="boolean" ></div></td>		
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
				
				
				
			<div id="tabbed-nav">
				<ul>
					<li><a>PROJECT</a></li>
					<li><a>ACTIVITY</a></li>
					<li><a>MODULES</a></li>
					<li><a>REVIEWS</a></li>
					<li><a>CUSTOM</a></li>
					<li><a>TAB</a></li>
					<li><a>ADMIN</a></li>
		       </ul>
						  
			<div>
	
				<!-- PROJECT -->
				<div>
				
					<table class="csui_title">
						<tr>
							<td class="csui_title">PROJECT</td>
							<td class="csui_title">
								<a target="lightbox-iframe" href="rlist.jsp?_id=<%=editId%>&_type=LKUP_PROJECT_TYPE&_typeid=LKUP_PROJECT_TYPE_ID" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
							</td>
						 </tr>
					</table>
					
					
					<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							
							<td class="csui_header">TYPE</td>
							<td class="csui_header">DESCRIPTION</td>
							<td class="csui_header">CREATE</td>
							<td class="csui_header">READ</td>
							<td class="csui_header">UPDATE</td>
							<td class="csui_header">DELETE</td>
							<td class="csui_header">&nbsp;</td>
						</tr>
						</thead>
						
						 <tbody>
						<%for(int i=0;i<project.size();i++){
							MapSet r = project.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("TYPE") %></td>
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("DESCRIPTION") %></td>
								
							
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="C" name="C"  rel="<%=r.getString("ID") %>" tb="REF_PROJECT_TYPE_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("C").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="R" name="R"  rel="<%=r.getString("ID") %>" tb="REF_PROJECT_TYPE_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("R").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="U" name="U"  rel="<%=r.getString("ID") %>" tb="REF_PROJECT_TYPE_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("U").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="D" name="D"  rel="<%=r.getString("ID") %>" tb="REF_PROJECT_TYPE_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("D").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete <%=r.getString("TYPE") %>" onclick="deletetype(<%=r.getString("ID") %>,'REF_PROJECT_TYPE_ROLES');" >
								</td>
								
							</tr>
							
						<%} %>
						</tbody>
						
						</table>
					</div>
					
				<!-- ACTIVITY -->
				<div>
				
					<table class="csui_title">
						<tr>
							<td class="csui_title">ACTIVITY</td>
							<td class="csui_title">
								<a target="lightbox-iframe" href="rlist.jsp?_id=<%=editId%>&_type=LKUP_ACT_TYPE&_typeid=LKUP_ACT_TYPE_ID" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
							</td>
						 </tr>
					</table>
					
					
					<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							
							<td class="csui_header">TYPE</td>
							<td class="csui_header">DESCRIPTION</td>
							<td class="csui_header">CREATE</td>
							<td class="csui_header">READ</td>
							<td class="csui_header">UPDATE</td>
							<td class="csui_header">DELETE</td>
							<td class="csui_header">REQUIRE PUBLIC STATUS</td>
							<td class="csui_header">&nbsp;</td>
						</tr>
						</thead>
						
						 <tbody>
						<%for(int i=0;i<activity.size();i++){
							MapSet r = activity.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("TYPE") %></td>
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("DESCRIPTION") %></td>
								
							
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="C" name="C"  rel="<%=r.getString("ID") %>" tb="REF_ACT_TYPE_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("C").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="R" name="R"  rel="<%=r.getString("ID") %>" tb="REF_ACT_TYPE_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("R").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="U" name="U"  rel="<%=r.getString("ID") %>" tb="REF_ACT_TYPE_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("U").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="D" name="D"  rel="<%=r.getString("ID") %>" tb="REF_ACT_TYPE_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("D").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="REQUIRE_PUBLIC" name="REQUIRE_PUBLIC"  rel="<%=r.getString("ID") %>" tb="REF_ACT_TYPE_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("REQUIRE_PUBLIC").equals("Y")){ %>checked <% }%> ></div></td>
								
								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete <%=r.getString("TYPE") %>" onclick="deletetype(<%=r.getString("ID") %>,'REF_ACT_TYPE_ROLES');" >
								</td>
								
							</tr>
							
						<%} %>
						</tbody>
						
						</table>
					</div>
										
				<!-- MODULE -->
				<div>
				
					<table class="csui_title">
						<tr>
							<td class="csui_title">MODULE</td>
							<td class="csui_title">
								<a target="lightbox-iframe" href="rlist.jsp?_id=<%=editId%>&_type=LKUP_MODULE&_typeid=LKUP_MODULE_ID" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
							</td>
						 </tr>
					</table>
					
					
					<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							
							<td class="csui_header">MODULE</td>
							<td class="csui_header">DESCRIPTION</td>
							<td class="csui_header">CREATE</td>
							<td class="csui_header">READ</td>
							<td class="csui_header">UPDATE</td>
							<td class="csui_header">DELETE</td>
							<td class="csui_header">&nbsp;</td>
						</tr>
						</thead>
						
						 <tbody>
						<%for(int i=0;i<modules.size();i++){
							MapSet r = modules.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("MODULE") %></td>
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("DESCRIPTION") %></td>
								
							
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="C" name="C"  rel="<%=r.getString("ID") %>" tb="REF_MODULE_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("C").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="R" name="R"  rel="<%=r.getString("ID") %>" tb="REF_MODULE_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("R").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="U" name="U"  rel="<%=r.getString("ID") %>" tb="REF_MODULE_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("U").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="D" name="D"  rel="<%=r.getString("ID") %>" tb="REF_MODULE_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("D").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete <%=r.getString("TYPE") %>" onclick="deletetype(<%=r.getString("ID") %>,'REF_MODULE_ROLES');" >
								</td>
								
							</tr>
							
						<%} %>
						</tbody>
						
						</table>
					</div>					

				<!-- REVIEWS -->
				<div>
				
					<table class="csui_title">
						<tr>
							<td class="csui_title">REVIEWS</td>
							<td class="csui_title">
								<a target="lightbox-iframe" href="rlist.jsp?_id=<%=editId%>&_type=REVIEW_GROUP&_typeid=REVIEW_GROUP_ID" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
							</td>
						 </tr>
					</table>
					
					
					<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							
							<td class="csui_header">GROUP NAME</td>
							<td class="csui_header">DESCRIPTION</td>
							<td class="csui_header">CREATE</td>
							<td class="csui_header">READ</td>
							<td class="csui_header">UPDATE</td>
							<td class="csui_header">DELETE</td>
							<td class="csui_header">&nbsp;</td>
						</tr>
						</thead>
						
						 <tbody>
						<%for(int i=0;i<reviews.size();i++){
							MapSet r = reviews.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("GROUP_NAME") %></td>
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("DESCRIPTION") %></td>
								
							
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="C" name="C"  rel="<%=r.getString("ID") %>" tb="REF_REVIEW_GROUP_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("C").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="R" name="R"  rel="<%=r.getString("ID") %>" tb="REF_REVIEW_GROUP_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("R").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="U" name="U"  rel="<%=r.getString("ID") %>" tb="REF_REVIEW_GROUP_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("U").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="D" name="D"  rel="<%=r.getString("ID") %>" tb="REF_REVIEW_GROUP_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("D").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete <%=r.getString("GROUP_NAME") %>" onclick="deletetype(<%=r.getString("ID") %>,'REF_REVIEW_GROUP_ROLES');" >
								</td>
								
							</tr>
							
						<%} %>
						</tbody>
						
						</table>
					</div>
					
					
				<!-- CUSTOM -->
				<div>
				
					<table class="csui_title">
						<tr>
							<td class="csui_title">CUSTOM</td>
							<td class="csui_title">
								<a target="lightbox-iframe" href="rlist.jsp?_id=<%=editId%>&_type=FIELD_GROUPS&_typeid=FIELD_GROUPS_ID" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
							</td>
						 </tr>
					</table>
					
					
					<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							
							<td class="csui_header">GROUP_NAME</td>
							<td class="csui_header">DESCRIPTION</td>
							<td class="csui_header">CREATE</td>
							<td class="csui_header">READ</td>
							<td class="csui_header">UPDATE</td>
							<td class="csui_header">DELETE</td>
							<td class="csui_header">&nbsp;</td>
						</tr>
						</thead>
						
						 <tbody>
						<%for(int i=0;i<custom.size();i++){
							MapSet r = custom.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("GROUP_NAME") %></td>
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("DESCRIPTION") %></td>
								
							
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="C" name="C"  rel="<%=r.getString("ID") %>" tb="REF_FIELD_GROUPS_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("C").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="R" name="R"  rel="<%=r.getString("ID") %>" tb="REF_FIELD_GROUPS_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("R").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="U" name="U"  rel="<%=r.getString("ID") %>" tb="REF_FIELD_GROUPS_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("U").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="D" name="D"  rel="<%=r.getString("ID") %>" tb="REF_FIELD_GROUPS_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("D").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete <%=r.getString("GROUP_NAME") %>" onclick="deletetype(<%=r.getString("ID") %>,'REF_FIELD_GROUPS_ROLES');" >
								</td>
								
							</tr>
							
						<%} %>
						</tbody>
						
						</table>
					</div>	
					
					
					<!-- TAB -->
				<div>
				
					<table class="csui_title">
						<tr>
							<td class="csui_title">TAB</td>
							<td class="csui_title">
								<a target="lightbox-iframe" href="rlist.jsp?_id=<%=editId%>&_type=LKUP_TAB&_typeid=LKUP_TAB_ID" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
							</td>
						 </tr>
					</table>
					
					
					<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							
							<td class="csui_header">TAB NAME</td>
							<td class="csui_header">CREATE</td>
							<td class="csui_header">READ</td>
							<td class="csui_header">UPDATE</td>
							<td class="csui_header">DELETE</td>
							<td class="csui_header">&nbsp;</td>
						</tr>
						</thead>
						
						 <tbody>
						<%for(int i=0;i<tabs.size();i++){
							MapSet r = tabs.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("TAB") %></td>
														
							
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="C" name="C"  rel="<%=r.getString("ID") %>" tb="REF_TAB_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("C").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="R" name="R"  rel="<%=r.getString("ID") %>" tb="REF_TAB_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("R").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="U" name="U"  rel="<%=r.getString("ID") %>" tb="REF_TAB_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("U").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="D" name="D"  rel="<%=r.getString("ID") %>" tb="REF_TAB_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("D").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete <%=r.getString("TAB") %>" onclick="deletetype(<%=r.getString("ID") %>,'REF_TAB_ROLES');" >
								</td>
								
							</tr>
							
						<%} %>
						</tbody>
						
						</table>
					</div>										
					
					<!-- ADMIN  -->
				<div>
				
					<table class="csui_title">
						<tr>
							<td class="csui_title">ADMIN</td>
							<td class="csui_title">
								<a target="lightbox-iframe" href="rlist.jsp?_id=<%=editId%>&_type=LKUP_ADMIN_MODULE&_typeid=LKUP_ADMIN_MODULE_ID" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
							</td>
						 </tr>
					</table>
					
					
					<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							
							<td class="csui_header">ADMIN</td>
							<td class="csui_header">DESCRIPTION</td>
							<td class="csui_header">CREATE</td>
							<td class="csui_header">READ</td>
							<td class="csui_header">UPDATE</td>
							<td class="csui_header">DELETE</td>
							<td class="csui_header">&nbsp;</td>
						</tr>
						</thead>
						
						 <tbody>
						<%for(int i=0;i<admins.size();i++){
							MapSet r = admins.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("NAME") %></td>
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("DESCRIPTION") %></td>
								
							
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="C" name="C"  rel="<%=r.getString("ID") %>" tb="REF_ADMIN_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("C").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="R" name="R"  rel="<%=r.getString("ID") %>" tb="REF_ADMIN_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("R").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="U" name="U"  rel="<%=r.getString("ID") %>" tb="REF_ADMIN_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("U").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="D" name="D"  rel="<%=r.getString("ID") %>" tb="REF_ADMIN_ROLES" class="ajauto" type="checkbox"  <%if(r.getString("D").equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete <%=r.getString("NAME") %>" onclick="deletetype(<%=r.getString("ID") %>,'REF_ADMIN_ROLES');" >
								</td>
								
							</tr>
							
						<%} %>
						</tbody>
						
						</table>
					</div>			
					
					<!-- End -->
					
				</div>
			
			</div>		
				
				
		</div>
			
			
			
			
			
	</div>
		
		
		
		
		
		
		
	</div>


	


</body>
</html>
