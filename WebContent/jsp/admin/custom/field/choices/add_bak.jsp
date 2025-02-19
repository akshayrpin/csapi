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
	String table = Table.FIELDCHOICESTABLE;
	String alert="";
	
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String fieldname = map.getString("_fieldname");
	
	String id = map.getString("_id","0");
	int editId = map.getInt("ID");
	int ID =0;
	if(editId>0)
	{
		ID=map.getInt("ID");;
	}//else{
	   // ID = map.getInt("ID");
	//}
	
	int fieldId =map.getInt("_fieldId");
	
	//map.add("FIELD_ID",editId);
	
	ArrayList<HashMap<String,String>> a =  new ArrayList<HashMap<String,String>>();
	ArrayList<MapSet> la = AdminAgent.getIndex(type, typeid, id,false,map);

	MapSet l = new MapSet();
	if(la.size()>0){
		l = la.get(0);
	}
	ArrayList<MapSet> fieldChoices = new ArrayList<MapSet>();
	MapSet v = new MapSet();
	if(editId>0){
		v = AdminAgent.getType(editId,table);
	}
		fieldChoices = AdminAgent.getList(AdminMap.getCustomFieldChoices(fieldId));
	
	
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 if(editId>0){
			 result = AdminAgent.updateType(AdminMap.getFieldChoice(map),table);
			 fieldChoices = AdminAgent.getList(AdminMap.getCustomFieldChoices(fieldId));
			// ID=0;
		 }else {
			 result = AdminAgent.saveMultiFieldChoices(AdminMap.getFieldChoice(map),table);
			 fieldChoices = AdminAgent.getList(AdminMap.getCustomFieldChoices(fieldId));
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
	<link href='<%=fullcontexturl %>/tools/jquery-ui.css' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/ioscheckboxes/assets/css/mobileCheckbox.iOS.css">
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/ioscheckboxes/assets/js/jquery.mobileCheckbox.js"></script>
	
	<style>
		
	</style>

	<script>
		$(document).ready(function() {
			
			
			
			<% if(result){%>
				
				//window.parent.$("#csform").submit();
				document.forms[0].TITLE.value="";
				document.forms[0].TITLE_VALUE.value="";
		
				//parent.$.fancybox.close();
				
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
			var method = "deletefieldchoices";
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
				   			  url: "<%=Config.fullcontexturl()%>/jsp/admin/custom/field/choices/action.jsp?_action="+method,
				   			  dataType: 'json',		  
				   			  data: { 
				   				  _type : "<%=type%>",
				   				  _typeid : <%=typeid%>,
				   				  _id : <%=id%>,
				   				  FIELD_ID:<%=fieldId%>,
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
		
		
		
		function addRow(tableID) {

			var table = document.getElementById(tableID);

			var rowCount = table.rows.length;
			var row = table.insertRow(rowCount);

			var cell1 = row.insertCell(0);
			var element1 = document.createElement("input");
			element1.type = "checkbox";
			element1.name="chkbox[]";
			
			//var x = document.createElement("IMG");
            // x.setAttribute("src", "/cs/images/icons/controls/delete.png");
            // x.onclick = "alert('blah')";
            // x.setAttribute("width", "25");
            // x.setAttribute("onclick", "deleteRow('dataTable')");
             //x.setAttribute("height", "25");
            // x.setAttribute("alt", "Delete row");
            // x.name="chkbox[]";
          
           
            //document.body.appendChild(x);
			cell1.appendChild(element1);

			var cell2 = row.insertCell(1);
			//var element2 =document.createElement("LABEL");
			//element1.type = "checkbox";
			//element1.name="chkbox[]";
			//element2.setAttribute("style", " padding: 6px; font-family: Roboto Condensed, Arial, Helvetica, sans-serif; font-size: 10px; background-color: #eeeeee; text-transform: uppercase")
			// var t = document.createTextNode("CHOICE TITLE");
			//element2.appendChild(t);
			//cell2.appendChild(element2);
			cell2.innerHTML="<td class=\"csui_label\" colnum=\"2\" alert=\"\">CHOICE TITLE</td>";
			cell2.style="padding: 6px; font-family: Roboto Condensed, Arial, Helvetica, sans-serif; font-size: 10px; background-color: #eeeeee; text-transform: uppercase";
			cell2.setAttribute("colspan","2");
			cell2.setAttribute("colnum","2");
			
			
			var cell3 = row.insertCell(2);
			cell3.innerHTML = "<input class=\"csui\"  name=\"TITLE\" type=\"text\" itype=\"text\"  valrequired=\"true\" maxchar=\"10000\">";
		    cell3.style="padding: 6px; font-family: Armata, Arial, Helvetica, sans-serif; font-size: 12px;background-color: #ffffff; ";
		    cell3.setAttribute("colspan","4");
		    
			var cell4 = row.insertCell(3);

			cell4.innerHTML="<td class=\"csui_label\" colnum=\"2\">CHOICE VALUE</td>";
			cell4.style="padding: 6px; font-family: Roboto Condensed, Arial, Helvetica, sans-serif; font-size: 10px; background-color: #eeeeee; text-transform: uppercase";
			cell4.setAttribute("colspan","2");
			cell4.setAttribute("colnum","2");
			
			var cell5 = row.insertCell(4);
			cell5.innerHTML = "<input class=\"csui\"  name=\"TITLE_VALUE\" type=\"text\" itype=\"text\" valrequired=\"true\" maxchar=\"10000\">";
		    cell5.style="padding: 6px; font-family: Armata, Arial, Helvetica, sans-serif; font-size: 12px;background-color: #ffffff; ";
		    cell5.setAttribute("colspan","4");
		    
			var cell6 = row.insertCell(5);
			cell6.innerHTML = "<td class=\"csui_title\"><img src=\"/cs/images/icons/controls/black/delete.png\" border=\"0\" style=\"cursor:pointer\" title=\"Delete\" onclick=\"deleteRow(1);\" ></td>";
		    cell6.style="padding: 6px; font-family: Armata, Arial, Helvetica, sans-serif; font-size: 12px; background-color: #ffffff;";
		    cell6.setAttribute("colspan","2");
			
			//var cell2 = row.insertCell(1);
			

		}

		function deleteRow(tableID) {
			
			try {
			var table = document.getElementById("dataTable");
			var rowCount = table.rows.length;
			
			for(var i=0; i<rowCount; i++) {
				var row = table.rows[i];
				var chkbox = row.cells[0].childNodes[0];
				
				//alert();
				if(null != chkbox && true == chkbox.checked) {
					table.deleteRow(i);
					rowCount--;
					i--;
				}


			}
			}catch(e) {
				alert(e);
			}
		}
		
		
	</script>

</head>
<body alert="<%=alert %>">
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
						<td align="left" id="title">FIELD CHOICES</td>
						<td align="right" id="subtitle">&nbsp;</td>
	
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" action="add.jsp?_fieldname=<%=fieldname%>&_fieldId=<%=fieldId%>" method="post">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
			
					<input type="hidden" name="ID" value="<%=ID%>">
					<input type="hidden" name="tabletype" value="single">
					<input type="hidden" name="FIELD_ID" value="<%=fieldId%>">
					
					
					<table class="csui_title">
						<tr>
							<td class="csui_title">FIELD CHOICES</td>
							<td class="csui_title">
							<a href="#" onclick="addRow('dataTable')" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
						</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
					<%
						if(editId>0){
							
					%>
						<table class="csui" colnum="2" type="default">
							<tr>
							   <td class="csui_label" colnum="2" alert="">CHOICE TITLE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4"><input name="TITLE" type="text" itype="text" value="<%=v.getString("TITLE") %>" valrequired="true" maxchar="10000"></td>
							    <td class="csui_label" colnum="2" alert="">CHOICE VALUE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4"><input name="TITLE_VALUE" type="text" itype="text" value="<%=v.getString("TITLE_VALUE") %>" valrequired="true" maxchar="10000"></td>
							</tr>
					</table>
					<%} else{%>
					
					<table colnum="2" style="border-spacing: 1px; border-collapse: separate;" type="default" id="dataTable">
					</table>
					<%} %>
					<div class="csui_divider">
			</div>
			<div class="csui_buttons">
				<input type="submit" name="_action" value="cancel" class="csui_button">&nbsp;
				<input type="submit" name="_action" value="save" class="csui_button">
			</div>
				</form>
				<%
				//if(editId>0){
               %>
				
				<table class="csui_title">
						<tr>
							<td class="csui_title">FIELDS</td>
 						 </tr>
							
							
					
				</table>
				<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							<td class="csui_header">CHOICE TITLE</td>
							<td class="csui_header">TITLE VALUE</td>
							<td class="csui_header" widtd="1%">&nbsp;</td>
							<td class="csui_header" widtd="1%">&nbsp;</td>
							
						
							
						</tr>
						</thead>
						
						 <tbody id="refs">
						<%for(int i=0;i<fieldChoices.size();i++){
							MapSet r = fieldChoices.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
							
							
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("TITLE") %>
								 <input type="hidden" id="refname_<%=r.getString("ID") %>" name="refname_<%=r.getString("ID") %>" value="<%=r.getString("TITLE") %>">
								</td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("TITLE_VALUE") %></td>


								<td class="csui" width="1%">
									<a href="add.jsp?_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=r.getString("ID") %>&editId=<%=r.getString("ID") %>&_fieldname=<%=fieldname%>&_fieldId=<%=fieldId%>&_groupname=<%=v.getString("GROUP_NAME") %>&process=edit" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>
								</td>
								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete" onclick="deletefield(<%=r.getString("ID") %>);" >
								</td>
								
							</tr>
							
							
						<%} %>
						</tbody>
						
						</table>
					<%
					//}
					%>
				
				
			</div>
		</div>
	</div>






</body>
</html>


