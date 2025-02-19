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
<%@page import="org.json.JSONArray"%>

<%


	Cartographer map = new Cartographer(request,response);
	Timekeeper d = new Timekeeper();
	String fullcontexturl = CsApiConfig.getString("cs.fullcontexturl");
	String table = Table.FIELDCHOICESTABLE;
	String alert="";
	
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	int editId = map.getInt("ID");
	
	String groupName = map.getString("_groupname","");
	String groupId = map.getString("_groupid","0");
	
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
		 //if(editId>0){
			// result = AdminAgent.updateType(AdminMap.getFieldType(map),table);
		// }else {
			 System.out.println("CAME HERE ");
			 result = AdminAgent.saveChoices(map,AdminMap.getFieldChoice(map),table);
		// }
		 
		 
	 }
	 if(map.equalsIgnoreCase("_action","CANCEL")){
			result = true;
	 }
	
	 JSONArray types = AdminAgent.getLookup("LKUP_FIELD_TYPE");
	 JSONArray itypes = AdminAgent.getLookup("LKUP_FIELD_ITYPE");
	
%>
<html>
<head>

	<title>City Smart- Admin</title>
	<link href='https://fonts.googleapis.com/css?family=Oswald:300,700' rel='stylesheet' type='text/css'>
	<link href='https://fonts.googleapis.com/css?family=Armata' rel='stylesheet' type='text/css'>
	<link href='https://fonts.googleapis.com/css?family=Roboto+Condensed:400,700' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/datetimepicker/jquery.datetimepicker.css"/>
	<link href='<%=fullcontexturl %>/tools/alain/cs.ui.css' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" media="all" href="<%=fullcontexturl %>/tools/fancyapps/source/jquery.fancybox.css"/>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/sweetalert/dist/sweetalert.css">
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/chosen/chosen.css"/>
	
	<style>
		.csui_controls { visibility: hidden }
		.searchbox {  -webkit-border-radius: 50px;-moz-border-radius: 50px;	border-radius: 50px;width:200px;height:25px;align:right;	}
	</style>


	<script type="text/javascript" src="<%=fullcontexturl %>/tools/jquery.min.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/alain/cs.tools.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/alain/cs.form.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/datetimepicker/jquery.datetimepicker.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/autogrow/jquery.autogrowtextarea.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/tinymce/js/tinymce/jquery.tinymce.min.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/tinymce/js/tinymce/tinymce.min.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/inputmask/dist/inputmask/inputmask.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/inputmask/dist/inputmask/jquery.inputmask.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/numeric/jquery.numeric.min.js"></script>
		<script type="text/javascript" src="<%=fullcontexturl %>/tools/chosen/chosen.jquery.js"></script>
	
 	<script type="text/javascript" src="<%=fullcontexturl %>/tools/fancyapps/source/jquery.fancybox.pack.js"></script>
    <script type="text/javascript" src="<%=fullcontexturl %>/tools/fancyapps/source/cms.fancybox.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/sweetalert/dist/sweetalert-dev.js"></script>

  	<script type="text/javascript" src="<%=fullcontexturl %>/tools/jquery-ui.min.js"></script>
	<link href='<%=fullcontexturl %>/tools/jquery-ui.css' rel='stylesheet' type='text/css'>
	<link href='https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.3/css/select2.min.css' rel='stylesheet' type='text/css'>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.3/js/select2.min.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/ioscheckboxes/assets/css/mobileCheckbox.iOS.css">
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/ioscheckboxes/assets/js/jquery.mobileCheckbox.js"></script>
	
	<style>
		
	</style>

	<script>
		$(document).ready(function() {
			
			
			<% if(result){%>
				
				document.location.href="list.jsp?_ent=permit&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=editId%>&_groupid=<%=map.getInt("groupid")%>&_groupname=<%=groupName %>";
				
				<% }%>
				
			<% if(editId>0){%>
				
				$('#LKUP_FIELD_TYPE_ID').val(<%=v.getString("LKUP_FIELD_TYPE_ID")%>);
				$('#LKUP_FIELD_TYPE_ID').trigger('chosen:updated');
				
				$('#LKUP_FIELD_ITYPE_ID').val(<%=v.getString("LKUP_FIELD_ITYPE_ID")%>);
				$('#LKUP_FIELD_ITYPE_ID').trigger('chosen:updated');

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
		
		function addchoice(){
			var v = parseInt($('#choiceslength').val()) +1;
			$('#choiceslength').val(v);
			
			var c = '<tr id="tr_'+v+'">';
				c += '<td class="csui" style="cursor:pointer;" rel="" ><input type="text" class="csui"  id="choicetitle_'+v+'" name="choicetitle_'+v+'" value=""></td>';
				c += '<td class="csui"  style="cursor:pointer;" rel=""><input type="text" class="csui"  id="choicevalue_'+v+'" name="choicevalue_'+v+'" value=""></td>';
				c += '<td class="csui"  style="cursor:pointer;" rel="">&nbsp;</td>';
		 		c += '</tr>'
			
		 	$('#addchoices').append(c);
		 	
		} 
		
		
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
										<a  href="list.jsp?_ent=permit&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=editId%>&_groupid=<%=map.getInt("groupid")%>&_groupname=<%=groupName %>" ><img src="/cs/images/icons/controls/white/back.png" border="0"></a>

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
						<td align="left" id="title">CHOICE </td>
						<td align="right" id="subtitle">&nbsp;</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" class="form" ajax="no" action="add.jsp" method="post">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
					<input type="hidden" name="ID" value="<%=editId%>">
					<input type="hidden" name="_groupname" value="<%=groupName%>">
					<input type="hidden" name="_groupid" value="<%=groupId%>">
					
					<input type="hidden" name="tabletype" value="single">
					<input type="hidden" name="choiceslength" id="choiceslength" value="5">
					
					<table class="csui_title">
						<tr>
							<td class="csui_title">CHOICE OPTIONS</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
							
							<tr>
								<td class="csui_label" colnum="2" alert="">FIELD</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" colspan="4">
								<div>
								<%= groupName%>
								<input name="FIELD_ID" type="hidden" value="<%= editId%>" >
								</div>
								</td>
							
							</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">CHOICE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="TITLE_0" type="text" itype="text" value=""  maxchar="10000"></td>
								<td class="csui_label" colnum="2" alert="">VALUE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" ><input name="TITLE_VALUE_0" type="text" itype="text" value=""  maxchar="10000"></td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">CHOICE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="TITLE_1" type="text" itype="text" value="" maxchar="10000"></td>
								<td class="csui_label" colnum="2" alert="">VALUE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" ><input name="TITLE_VALUE_1" type="text" itype="text" value=""  maxchar="10000"></td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">CHOICE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="TITLE_2" type="text" itype="text" value=""  maxchar="10000"></td>
								<td class="csui_label" colnum="2" alert="">VALUE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" ><input name="TITLE_VALUE_2" type="text" itype="text" value=""   maxchar="10000"></td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">CHOICE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="TITLE_3" type="text" itype="text" value=""  maxchar="10000"></td>
								<td class="csui_label" colnum="2" alert="">VALUE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" ><input name="TITLE_VALUE_3" type="text" itype="text" value=""  maxchar="10000"></td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">CHOICE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="TITLE_4" type="text" itype="text" value="" maxchar="10000"></td>
								<td class="csui_label" colnum="2" alert="">VALUE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" ><input name="TITLE_VALUE_4" type="text" itype="text" value=""   maxchar="10000"></td>
							</tr>
							
							
					</table>
					
					

					<div class="csui_divider">
			</div>
			<div class="csui_buttons">
				<input type="submit" name="_action" value="cancel" class="csui_button">&nbsp;
				<input type="submit" name="_action" value="save" class="csui_button">
			</div>
				<!-- 
				<table class="csui_title" id="choices" >
						<tr>
							<td class="csui_title">CHOICES</td>
							<td class="csui_title">
								<a  href="javascript:void(0);" onclick="addchoice();" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
							</td>
						 </tr>
							
					</table>
					
					<table class="csui"  >
						<thead>
							<tr>
								
								<td class="csui_header">TITLE</td>
								<td class="csui_header">VALUE</td>
								<td class="csui_header">&nbsp;</td>
							</tr>
						</thead>
					
						 <tbody class="refs" id="addchoices" >
							
						</tbody>
						
							
					</table>
			
			 -->
				</form>
			</div>
		</div>
	</div>






</body>
</html>

