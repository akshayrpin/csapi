<%@page import="csapi.search.GlobalSearch"%>
<%@page import="csapi.impl.admin.AdminMap"%>
<%@page import="alain.core.utils.MapSet"%>
<%@page import="csapi.common.Table"%>
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
<%@page import="org.json.JSONArray"%>
<%


	Cartographer map = new Cartographer(request,response);
	Timekeeper d = new Timekeeper();
	String fullcontexturl = CsApiConfig.getString("cs.fullcontexturl");
	String table = Table.LSOTABLE;
	String alert="";
	
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	int editId = map.getInt("ID");
	
	int parentId = map.getInt("PARENT_ID",-1);
	int primaryId = map.getInt("PRIMARY_ID",-1);
	
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
		//fields = AdminAgent.getList(AdminMap.getCustomFields(editId));
		parentId = v.getInt("PARENT_ID");
		primaryId = v.getInt("PRIMARY_ID");
	}
	
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 if(editId>0){
			 result = AdminAgent.updateType(AdminMap.getLSOMap(map),table); 
		 }else {
			 result = AdminAgent.saveType(AdminMap.getLSOMap(map),table);
		 }
		 GlobalSearch.index(GlobalSearch.ADDRESS_LSO_DELTA);
		 
	 }
	 if(map.equalsIgnoreCase("_action","CANCEL")){
			result = true;
	 }
	
	 JSONArray lsoTypes = AdminAgent.getLookup("LKUP_LSO_TYPE");
	 JSONArray streetList = AdminAgent.getLookup("LSO_STREET");

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
				
				
				$('#LSO_STREET_ID').val(<%=v.getString("LSO_STREET_ID")%>);
				$('#LSO_STREET_ID').trigger('chosen:updated');
				
				
			<% }%>
			
		});
		
		
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
				   			  url: "<%=Config.fullcontexturl()%>/jsp/admin/custom/action.jsp?_action="+method,
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
						<td align="left" id="title">LSO</td>
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
					<input type="hidden" name="SOURCE" value="CS">
					<input type="hidden" name="PARENT_ID" value="<%=parentId%>">
					<input type="hidden" name="PRIMARY_ID" value="<%=primaryId%>">
					
					
					
					<table class="csui_title">
						<tr>
							<td class="csui_title">LSO</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
						<%if(editId<=0){ %>
							<tr>
								<td class="csui_label" colnum="2" alert="">LSO TYPE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" >
								<%if(Operator.equalsIgnoreCase(map.getString("process"), "add")){ %> 
									<input type="hidden" name="LKUP_LSO_TYPE_ID" value="<%=map.getInt("LKUP_LSO_TYPE_ID")%>">
									
								<%if(map.getInt("LKUP_LSO_TYPE_ID")==1){ %> 
									LAND
								<%}else if(map.getInt("LKUP_LSO_TYPE_ID")==2){ %> 
									STRUCTURE
								<%}else if(map.getInt("LKUP_LSO_TYPE_ID")==3){ %> 
									OCCUPANCY
								<%} %> 
								
								<%} %> 
								
								</td>
								<td class="csui_label" colnum="2" alert="">ISPUBLIC</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input name="ISPUBLIC" type="checkbox"  <%if(v.getString("ISPUBLIC").equals("Y")){ %>checked <% }%>itype="boolean" ></div></td>
							</tr>
						<%} else {%>
							<tr>
								<td class="csui_label" colnum="2" alert="">LSO TYPE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" >
								<input type="hidden" name="LKUP_LSO_TYPE_ID" value="<%=v.getInt("LKUP_LSO_TYPE_ID")%>">
								<%if(v.getInt("LKUP_LSO_TYPE_ID")==1){ %> 
									LAND
								<%}else if(v.getInt("LKUP_LSO_TYPE_ID")==2){ %> 
									STRUCTURE
								<%}else if(v.getInt("LKUP_LSO_TYPE_ID")==3){ %> 
									OCCUPANCY
								<%} %> 
								</td>
								<td class="csui_label" colnum="2" alert="">ISPUBLIC</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input name="ISPUBLIC" type="checkbox"  <%if(v.getString("ISPUBLIC").equals("Y")){ %>checked <% }%>itype="boolean" ></div></td>
							</tr>
						<%}%>
							<tr>
								<td class="csui_label" colnum="2" alert="">STREET NUMBER</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<input name="STR_NO" type="text" itype="text" value="<%=v.getString("STR_NO") %>" valrequired="true" maxchar="10000">
								</td>
								<td class="csui_label" colnum="2" alert="">STREET LIST</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<select  name="LSO_STREET_ID" id="LSO_STREET_ID" itype="String" val="" _ent="lso" valrequired="false" ><option value=""></option>
									<%for(int i=0;i<streetList.length();i++){ %>	<option value="<%=streetList.getJSONObject(i).getInt("ID")%>"><%=streetList.getJSONObject(i).getString("TEXT")%></option><%}%>
								</select>
								</td>
							</tr>
							
							<tr>
								
								<td class="csui_label" colnum="2" alert="">DESCRIPTION</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<input name="DESCRIPTION" type="text" itype="text" value="<%=v.getString("DESCRIPTION") %>" valrequired="true" maxchar="10000">
								</td>
								<td class="csui_label" colnum="2" alert="">STREET MOD</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<input name="STR_MOD" type="text" itype="text" value="<%=v.getString("STR_MOD") %>" valrequired="false" maxchar="10000">
								</td>
							</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">UNIT</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<input name="UNIT" type="text" itype="text" value="<%=v.getString("UNIT") %>" valrequired="false" maxchar="10000">
								</td>
								<td class="csui_label" colnum="2" alert="">CITY</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<input name="CITY" type="text" itype="text" value="<%=v.getString("CITY") %>" valrequired="false" maxchar="10000">
								</td>
							</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">STATE</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<input name="STATE" type="text" itype="text" value="<%=v.getString("STATE") %>" valrequired="false" maxchar="10000" maxlength="2">
								</td>
								<td class="csui_label" colnum="2" alert="">SOURCE</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<div><%=v.getString("SOURCE") %></div>
								</td>
							</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">ZIP</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<input name="ZIP" type="text" itype="text" value="<%=v.getString("ZIP") %>" valrequired="true" maxchar="10000">
								</td>
								<td class="csui_label" colnum="2" alert="">ZIP4</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<input name="ZIP4" type="text" itype="text" value="<%=v.getString("ZIP4") %>" valrequired="false" maxchar="10000">
								</td>
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


