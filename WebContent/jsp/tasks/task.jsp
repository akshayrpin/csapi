<%@page import="alain.core.utils.Config"%>
<%@page import="csapi.impl.admin.AdminMap"%>
<%@page import="csapi.tasks.process.Task"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.json.JSONArray"%>
<%@page import="csapi.impl.admin.AdminAgent"%>
<%@page import="alain.core.utils.MapSet"%>
<%@page import="csapi.utils.CsApiConfig"%>
<%@page import="alain.core.utils.Cartographer"%>
<%


	Cartographer map = new Cartographer(request,response);
	String fullcontexturl = CsApiConfig.getString("cs.fullcontexturl");
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	int editId = map.getInt("ID");
	MapSet v = new MapSet();
	ArrayList<MapSet> tasks = new ArrayList<MapSet>();
	String table = "REF_ACT_TYPE_TASKS";
	if(editId>0){
		tasks = AdminAgent.getList(AdminMap.getTaskFields(table, editId));
	}
	ArrayList<MapSet> t = Task.getTasks();
	
	

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
	  	<script type="text/javascript" src="<%=fullcontexturl %>/tools/jquery-ui.min.js"></script>
	
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/fancyapps/source/jquery.fancybox.pack.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/fancyapps/source/cms.fancybox.js"></script>
	
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/chosen/chosen.jquery.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/sweetalert/dist/sweetalert-dev.js"></script>
	
	<style>
		
	</style>

	<script>
		$(document).ready(function() {
			
			$('select:not([itype=boolean]):not([valrequired=true])').chosen({
				width:'100%',
				disable_search_threshold: 10,
				allow_single_deselect: true
			});
			$('select:not([itype=boolean])[valrequired=true]').chosen({
				width:'100%',
				disable_search_threshold: 10
			});

			
			
			$('#LKUP_TASK_ID').change(function () {
				
				
				var element = $(this).find('option:selected'); 
		        var path = element.attr("path"); 
		        var name=element.attr("text");
		        var id=element.attr("value");
				if(id==''){
					return false;
				}
				var u =path+"?REF=ACT_TYPE&ID=<%=editId%>";
				
				$('<a title="'+id+'" href="'+u+'">Friendly description</a>').fancybox({
		       			'width'				: '75%',
						'height'			: '75%',
						'autoScale'			: false,
						'transitionIn'		: 'none',
						'transitionOut'		: 'none',
						'type'				: 'iframe',
						
		          }).click();
		       	
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
			 				 table : "<%=table%>"
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
	 				ID : <%=editId%>,
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
	
		function deletetype(id){
			var method = "deletetype";
			swal({  
					title: "Are you sure?",   
					text: "You want to delete this record!",   
					type: "warning",   
					showCancelButton: true,   
					confirmButtonColor: "#DD6B55",   
					confirmButtonText: "Yes, delete it!",   
					cancelButtonText: "No, cancel plx!",   
					closeOnConfirm: true,   
					closeOnCancel: false 
				}, 
				function(isConfirm){   
					if (isConfirm) {     
						$.ajax({
				   			  type: "POST",
				   			  url: "<%=Config.fullcontexturl()%>/jsp/admin/action.jsp?_action="+method,
				   			  dataType: 'json',		  
				   			  data: { 
				   				// feesjson : type,
				   			      _grptype : "finance",
				   			   	  _ent : "",
				   			   	  _type : "<%=type%>",
				   				  _typeid : <%=typeid%>,
				   				  _id : <%=id%>,
				   				 _table : "<%=table%>",
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
<body>
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
						<td align="left" id="title">TASK MANAGER</td>
						<td align="right" id="subtitle">&nbsp;</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" action="task.jsp" method="post">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="ID" value="<%=editId%>">
					<table class="csui_title">
						<tr>
							<td class="csui_title">TASKS</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
					<table class="csui" colnum="2" type="default">
							<tr>
								<td class="csui_label" colnum="2" alert="">ADD</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="">
								<select  name="LKUP_TASK_ID" id="LKUP_TASK_ID" itype="String" val="" _ent="lso" ><option value=""></option>
									<%for(MapSet m: t){ %>	<option value="<%=m.getString("_TASK")%>" path="<%=m.getString("_PATH")%>" ><%=m.getString("_TASK")%></option>		<%}%>
								</select>
								</td>
								
							</tr>
							
							
					</table>

					<div class="csui_divider">
				</div>
				
				
				
				<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							
							<td class="csui_header">TASK</td>
							<td class="csui_header">DESCRIPTION</td>
							
							<td class="csui_header">REPEAT</td>
							
							<td class="csui_header">UPDATED BY</td>
							<td class="csui_header">UPDATED</td>
							
							<td class="csui_header" width="1%">&nbsp;</td>
							<td class="csui_header" width="1%">&nbsp;</td>
						
							
						</tr>
						</thead>
						
						 <tbody id="refs">
						<%for(int i=0;i<tasks.size();i++){
							MapSet r = tasks.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
							
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("TASK") %>
								 <input type="hidden" id="refname_<%=r.getString("ID") %>" name="refname_<%=r.getString("ID") %>" value="<%=r.getString("TASK") %>">
								 
								</td>
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("DESCRIPTION") %></td>
								
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input id="REPEAT" name="REPEAT"  rel="<%=r.getString("ID") %>" class="ajauto" type="checkbox"<%if(r.getString("REPEAT").trim().equals("Y")){ %>checked <% }%> ></div></td>
								
							
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("UPDATED") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("C_UPDATED_DATE") %></td>
								
								<td class="csui" width="1%">
									<a target="lightbox-iframe" href="<%=r.getString("TASK").toLowerCase() %>.jsp?_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&REF_ID=<%=r.getString("ID") %>&ID=<%=editId %>&TASK_ID=<%=r.getString("TASK_ID") %>&_groupid=<%=editId%>&_groupname=<%=v.getString("GROUP_NAME") %>&process=edit" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>
								</td>
								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete" onclick="deletetype(<%=r.getString("ID") %>);" >
								</td>
								
							</tr>
							
							
						<%} %>
						</tbody>
						
						</table>
				
				
			
				</form>
			</div>
		</div>
	</div>






</body>
</html>

