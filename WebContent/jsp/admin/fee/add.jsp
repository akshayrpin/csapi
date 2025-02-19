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
	String table = Table.FEEGROUPTABLE;
	String alert="";
	
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	int editId = map.getInt("ID");
	
	
	
	
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 if(editId>0){
			 result = AdminAgent.updateType(AdminMap.getFeeGroupType(map),table);
		 }else {
			 result = AdminAgent.saveType(AdminMap.getFeeGroupType(map),table);
		 }
		 
		 
	 }
	 if(map.equalsIgnoreCase("_action","CANCEL")){
			result = true;
	 }
	
	
	
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
		fields = AdminAgent.getList(AdminMap.getFeeGroupFields(editId));
	}
	
	
	 
	 Timekeeper k = new Timekeeper();
	 String exp = "";
	 if(Operator.hasValue(v.getString("EXPIRATION_DATE"))){
		 exp = Operator.subString(v.getString("EXPIRATION_DATE"), 0, 10);
	 }
	 //System.out.println("###########SUNILLLLLLLLLLLLL##############"+v.getString("EXPIRATION_DATE"));

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
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/tablesorter/jquery.tablesorter.min.js"></script>

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
				
				$('input:radio[name=online]:checked');
				
				
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
				 
				 
				 $('.ajautoseq').blur(function(){
						var id = $(this).attr("rel");
						var type = $(this).attr("id");
						var v = $(this).val();
						 
					   var method = "updateseq";
					   
						 $.ajax({
				 			  type: "POST",
				 			  url: "action.jsp?_action="+method,
				 			  dataType: 'json',		  
				 			  data: { 
				 				  type : type,
				 				  val : v,
				 				  ID : id,
				 				 
				 				  //mode : mode
				 			    },
				 			    success: function(output) {
				 			    	swal({
				 			    	    title: "Success!",
				 			    	    text: "Updated ",
				 			    	    timer: 10,
				 			    	    showConfirmButton: false
				 			    	  });
				 			    		
				 			    },
				 		    error: function(data) {
				 		    	swal("Problem while processing the request");  
				 		    }
						 });	
						

				 });	
				 
				 $('.ajauto').change(function(){
						var id = $(this).attr("rel");
						var type = $(this).attr("id");
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
				 				 
				 				  //mode : mode
				 			    },
				 			    success: function(output) {
				 			    	//swal("Sorted successfully ");
				 			    	swal({
				 			    	    title: "Success!",
				 			    	    text: "Updated ",
				 			    	    timer: 100,
				 			    	    showConfirmButton: false
				 			    	  });
				 			    		
				 			    },
				 		    error: function(data) {
				 		    	swal("Problem while processing the request");  
				 		    }
						 });	
						

				 });	
			<% }%>
			
			  $.tablesorter.addParser({
		            id:'input',
		            is:function (s) {
		                return false;
		            },
		            format:function(s, table, cell) {
		            	  return $('input', cell).val();
		            },
		            type:'text'
		        });
			
			 $(".tablesorter").tablesorter({
				 headers:{
		                7:{
		                    sorter:'input'
		                }
		            }
				 
			 }); 
			
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
	 				 FIELD_GROUPS_ID : <%=editId%>,
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
			var method = "deletefeeonly";
			
			swal({  
					title: "Are you sure? You want to delete this record!",   
					text: "You need to enter the expiration date MM/DD/YYYY or current date will become the expiration date",   
					type: "input",   
					showCancelButton: true,   
					confirmButtonColor: "#DD6B55",   
					
					cancelButtonText: "No, cancel plx!",   
					animation: "slide-from-top",
					closeOnConfirm: true,   
					inputValue: "<%=k.getString("MM/DD/YYYY")%>"
				}, 
				function(inputValue){
					var expdate=""
					if(inputValue==""){
						expdate="<%=k.getString("MM/DD/YYYY")%>";
					}else {
						expdate = inputValue;
					}
					
					$.ajax({
			   			  type: "POST",
			   			  url: "<%=Config.fullcontexturl()%>/jsp/admin/fee/action.jsp?_action="+method,
			   			  dataType: 'json',		  
			   			  data: { 
			   				  _type : "<%=type%>",
			   				  _typeid : <%=typeid%>,
			   				  _id : <%=id%>,
			   				   ID : id,
			   				   EXPIRATION_DATE: expdate
			   			      //mode : mode
			   			    },
			   			    success: function(output) {
			   			    		//$('#tr_'+id).remove();
			   			    		swal("Deleted!", "The record has been expired.", "success");   
			   			    		
			   			    },
			   		    error: function(data) {
			   		    	swal("Problem while perfoming delete looks like the server is busy");  
			   		    }
		   			});		
					
				
				});
		}
		
		
		function deletetype(id,hideid){
			var method = "deleteassociation";
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
				   			  url: "<%=Config.fullcontexturl()%>/jsp/admin/fee/action.jsp?_action="+method,
				   			  dataType: 'json',		  
				   			  data: { 
				   				// feesjson : type,
				   			    
				   			
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
										<a  href="list.jsp?_ent=permit&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>" ><img src="/cs/images/icons/controls/white/back.png" border="0"></a>

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
						<td align="left" id="title">FEE GROUP</td>
						<td align="right" id="subtitle">&nbsp;</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" class="form" ajax="no" action="add.jsp" method="post">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
					<input type="hidden" name="ID" value="<%=editId%>">
					<input type="hidden" name="tabletype" value="single">
					
					<table class="csui_title">
						<tr>
							<td class="csui_title">FEE GROUP</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
							<tr>
								<td class="csui_label" colnum="2" alert="">GROUP NAME</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4"><input name="GROUP_NAME" type="text" itype="text" value="<%=v.getString("GROUP_NAME") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">START DATE</td>
								<td class="csui" colnum="2" type="String" itype="String" alert="">
								<%if(editId>0){ %>
									<%=v.getString("START_DATE") %>
									<input type="hidden" name="START_DATE" value="<%=v.getString("START_DATE") %>">
								<%}else { %>
									<input name="START_DATE" type="text" itype="date" value="<%=k.getString("YYYY/MM/DD") %>" valrequired="true" maxchar="10000">
								<%}%>
								</td>
								<td class="csui_label" colnum="2" alert="">EXPIRATION DATE</td>
								<td class="csui" colnum="2" type="DATETIME" itype="String" alert=""><input name="EXPIRATION_DATE" type="text" itype="date" value="<%=exp %>" valrequired="false" maxchar="10000"></td>
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
				
				
				
				<%if(editId>0){ 
				
				%>
				
				<table class="csui_title">
						<tr>
							<td class="csui_title">FEE</td>
							<td class="csui_title">
								<a target="lightbox-iframe" href="field/add.jsp?_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&_groupid=<%=editId%>&_groupname=<%=v.getString("GROUP_NAME") %>" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
							</td>
						

 						 </tr>
							
							
					
				</table>
				<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							
							<td class="csui_header">NAME</td>
							<td class="csui_header">FORMULA</td>
							<td class="csui_header">ACCOUNT</td>
							<td class="csui_header">REQ</td>
							<td class="csui_header">AUTO ADD</td>	
							<td class="csui_header">PLAN CHK AUTO</td>
							<td class="csui_header">REVIEW FEE</td>
							<td class="csui_header">SEQUENCE</td>
							<td class="csui_header">START DATE</td>
							<td class="csui_header">EXP DATE</td>
							<td class="csui_header">UPDATED BY</td>
							<td class="csui_header">UPDATED</td>
							<td class="csui_header" widtd="1%">&nbsp;</td>
							<td class="csui_header" widtd="1%">&nbsp;</td>
							<td class="csui_header" widtd="1%">&nbsp;</td>
						    <td class="csui_header" widtd="1%">&nbsp;</td>
							
						</tr>
						</thead>
						
						 <tbody id="refs">
						<%for(int i=0;i<fields.size();i++){
							MapSet r = fields.get(i);
						%>
							<tr id="tr_<%=r.getString("REF_FEE_GROUP_ID") %>">
							
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("NAME") %>
								 <input type="hidden" id="refname_<%=r.getString("ID") %>" name="refname_<%=r.getString("ID") %>" value="<%=r.getString("GROUP_NAME") %>">
								 
								</td>
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("FORMULA_NAME") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("KEY_CODE") %></td>
								
								
								
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="REQ" name="REQ"  rel="<%=r.getString("REF_FEE_GROUP_ID") %>" class="ajauto" type="checkbox"  <%if(r.getString("REQ").trim().equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" ><div><input id="AUTO_ADD" name="AUTO_ADD"  rel="<%=r.getString("REF_FEE_GROUP_ID") %>" class="ajauto" type="checkbox"  <%if(r.getString("AUTO_ADD").trim().equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="PLAN_CHK_REQ" name="PLAN_CHK_REQ"  rel="<%=r.getString("REF_FEE_GROUP_ID") %>" class="ajauto" type="checkbox"<%if(r.getString("PLAN_CHK_REQ").trim().equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="REVIEW_FEE" name="REVIEW_FEE"  rel="<%=r.getString("REF_FEE_GROUP_ID") %>" class="ajauto" type="checkbox"<%if(r.getString("REVIEW_FEE").trim().equals("Y")){ %>checked <% }%> ></div></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="SEQUENCE" name="SEQUENCE"  rel="<%=r.getString("REF_FEE_GROUP_ID") %>" class="ajautoseq" itype="integer"  type="text" value="<%=r.getString("SEQUENCE") %>" width="1%" ></div></td>
								
							
								
							
								
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("C_START_DATE") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("C_EXPIRATION_DATE") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("UPDATED") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("C_UPDATED_DATE") %></td>
								<td class="csui scrollselector" stid="<%=r.getString("ID") %>" title="Show Fields">&nbsp;
								 <input type="hidden" id="show_bottom_<%=r.getString("ID") %>" name="show_bottom_<%=r.getString("ID") %>" value="0">
								</td>
									
								
								<td class="csui" width="1%">
									<a target="lightbox-iframe" href="field/add.jsp?_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=r.getString("ID") %>&_groupid=<%=editId%>&_groupname=<%=v.getString("GROUP_NAME") %>&process=edit" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>
								</td>
								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete Fee All groups" onclick="deletefield(<%=r.getString("ID") %>);" >
								</td>
								
								<td class="csui" width="1%">
									<%if(r.getInt("MULTI")>1) {%>
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete Reference from this group" onclick="deletetype(<%=r.getString("REF_FEE_GROUP_ID") %>,<%=r.getString("ID") %>);" >
									<%} else { %>
										&nbsp;
									<%} %>
								</td>
								
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

