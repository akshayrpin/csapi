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
	String table = Table.LKUPVOIPMENUTABLE;
	String alert="";
	
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	int editId = map.getInt("ID");
	String ent = map.getString("_ent");
	ArrayList<HashMap<String,String>> a =  new ArrayList<HashMap<String,String>>();
	ArrayList<MapSet> la = AdminAgent.getIndex(type, typeid, id,false,map);

	MapSet l = new MapSet();
	if(la.size()>0){
		l = la.get(0);
	}
	
	MapSet v = new MapSet();
	if(editId>0){
		v = AdminAgent.getType(editId,table);
		
	}
	
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 if(editId>0){
			 result = AdminAgent.updateType(AdminMap.getVoipMenu(map),table);
		 }else {
			 result = AdminAgent.saveType(AdminMap.getVoipMenu(map),table);
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
	<link href='<%=fullcontexturl %>/tools/alain/cs.ui.css' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" media="all" href="<%=fullcontexturl %>/tools/fancyapps/source/jquery.fancybox.css"/>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/sweetalert/dist/sweetalert.css">
		<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/tablesorter/css/theme.dropbox.css">
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
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/tablesorter/jquery.tablesorter.min.js"></script>
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
			
			 $('#multiedit').click(function () {
				 var loc=$(this).attr("loc");
				 var name ="Multi-Edit";
				 var v = $('input:checkbox:checked').map(function() {   return this.value; }).get();
					if(v==""){
						swal("Select Types in order to proceed");
						return false;
					}
					//v = v.replace("on","0");
					//alert(v);
					var u = loc+"&IDS="+v;
					u = u.replace("on,","");
					//alert(u);
					$(this).attr("href",u);
		       	
					});
			 
			 $('#merge').click(function () {
				 var loc=$(this).attr("loc");
				 var name ="merge";
				 var v = $('input:checkbox:checked').map(function() {   return this.value; }).get();
					if(v==""){
						swal("Select Types in order to proceed");
						return false;
					}
					//v = v.replace("on","0");
					//alert(v);
					var u = loc+"&IDS="+v;
					u = u.replace("on,","");
					//alert(u);
					$(this).attr("href",u);
		       	
					});
			 
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
				
			 $(".tablesorter").tablesorter(); 
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
		
		//function 
	
	function deletetype(id,table){
		var method = "deletetype";

		swal({  
				title: "Are you sure?",   
				text: "You want to delete1 this record!",   
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
			   			  url: "<%=Config.fullcontexturl()%>/jsp/admin/action.jsp?_action="+method,
			   			  dataType: 'json',		  
			   			  data: { 
			   				// feesjson : type,
			   			      _grptype : "finance",
			   			   	  _ent : "<%=ent%>",
			   			   	  _type : "<%=type%>",
			   				  _typeid : <%=typeid%>,
			   				  _id : <%=id%>,
			   				 _table : table,
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
						<td align="left" id="title">VOIP MENU</td>
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
							<td class="csui_title">VOIP MENU</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
							<tr>
								<td class="csui_label" colnum="2" alert="">PRESS</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" >
									<input name="PRESS"  id="PRESS" type="text" itype="integer" value="<%=v.getString("PRESS") %>"  maxchar="10000" valrequired="true" >
								</td>
								
								<td class="csui_label" colnum="2" alert="">LOGIC</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="">
								<select  name="LOGIC" id="LOGIC" itype="String" val="" _ent="lso" valrequired="true" >
								    <option value="">Please Select</option>
									<option value="schedule" <%if(v.getString("LOGIC").equalsIgnoreCase("schedule")) {%>selected="true"<%} %>>Schedule</option>
									<option value="transfer" <%if(v.getString("LOGIC").equalsIgnoreCase("transfer")) {%>selected="true"<%} %>>Transfer</option>
								
								</select>
								
								<%
								String logic =v.getString("LOGIC");
								%>
								
								
		                 	</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">DESCRIPTION</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
								<textarea name="DESCRIPTION"><%=v.getString("DESCRIPTION") %></textarea></td>
							</tr>
							
							<tr>
								
								<td class="csui_label" colnum="2" alert="">SAY DESCRIPTION</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="">
									<textarea name="SAY_DESCRIPTION"><%=v.getString("SAY_DESCRIPTION") %></textarea></td>
									
								<td class="csui_label" colnum="2" alert="">SAY HINT</td>
							  	<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="HINT" type="text" itype="text" value="<%=v.getString("HINT") %>"  maxchar="10000"></td>
						
								
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
				
				
								<%if(editId>0 && (v.getString("LOGIC")).equalsIgnoreCase("Transfer")){ 
									
								%>
				
				<table class="csui_title">
						<tr>
							<td class="csui_title">VOIP TRANSFER</td>

							<td class="csui_tools">
							<a target="lightbox-iframe" href="refvoiptransfer/add.jsp?_ent=<%=ent%>&_voipmenuid=<%=editId%>&_voipmenuname=<%=v.getString("DESCRIPTION") %>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
							</td>
							
 						 </tr>
					
				</table>
				<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
		                    <td class="csui_header" style="cursor:pointer;">PRESS</td>
							<td class="csui_header" style="cursor:pointer;">DESCRIPTION</td>
							<td class="csui_header" style="cursor:pointer;">PHONE</td>
							<td class="csui_header" style="cursor:pointer;">HINT</td>
							<td class="csui_header">CREATED BY</td>
							<td class="csui_header">CREATED</td>
							<td class="csui_header">UPDATED BY</td>
							<td class="csui_header">UPDATED</td>
							
							<td class="csui_header" widtd="1%">&nbsp;</td>
							<td class="csui_header" widtd="1%">&nbsp;</td>
						</tr>
						</thead>
						
						 <tbody id="refs">
						<%
						
						if(logic.equals("transfer"))
						{
						ArrayList<MapSet> fields = new ArrayList<MapSet>();
						table = Table.REFVOIPTRANSFERTABLE;
						
						//Pagination
						int maxrows = map.getInt("MAX", 1000);
						int pg = map.getInt("PAGE",1);
						String sortfield= map.getString("SORT_FIELD","PRESS");
						String order= map.getString("ORDER","ASC");
						String ord= order;
						if(order.equalsIgnoreCase("DESC")){
							ord = "ASC";
						}else {
							ord="DESC";
						}
						int records = 0;

						
						int start = (pg-1)*maxrows;
						int end = start+maxrows;
						if(editId>0){
							v = AdminAgent.getType(editId,table);
							fields = AdminAgent.getList(AdminMap.getRefVoipTransfer(editId,table , sortfield,order));
							//fields =AdminAgent.getList(table,start,end,sortfield,order,map.getString("SQ"),map.getString("SEARCH_FIELDS"));
						}
						
						
						for(int i=0;i<fields.size();i++){
							MapSet r = fields.get(i);
						
						
						%>
							<tr id="tr_<%=r.getString("ID") %>">
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>"><%=r.getString("PRESS") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>"><%=r.getString("DESCRIPTION") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>"><%=r.getString("PHONE") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>"><%=r.getString("HINT") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>"><%=r.getString("CREATED") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>"><%=r.getString("C_CREATED_DATE") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>"><%=r.getString("UPDATED") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>"><%=r.getString("C_UPDATED_DATE") %></td>
								
								<td class="csui" width="1%">
								<a target="lightbox-iframe" href="refvoiptransfer/add.jsp?_ent=<%=ent%>&_press=<%=v.getString("PRESS")%>&_voipmenuid=<%=editId%>&_voipmenuname=<%=v.getString("DESCRIPTION") %>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=r.getString("ID") %>&process=edit" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>
								</td>
								<td class="csui" width="1%">
								<a href="javascript:void(0);" target="" onclick="deletetype(<%=r.getString("ID") %>,'<%=table%>');" ><img src="/cs/images/icons/controls/black/delete.png" border="0"></a>
								</td>
							</tr>
						<%} 
						  }%>
				</tbody>
						</table>
					<%}%>
			</div>
		</div>
	</div>






</body>
</html>

