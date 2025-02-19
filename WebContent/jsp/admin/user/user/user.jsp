<%@page import="csapi.impl.users.UsersAgent"%>
<%@page import="csshared.vo.finance.DepositCreditVO"%>
<%@page import="csapi.impl.deposit.DepositAgent"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.json.JSONArray"%>
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
	String table = Table.FIELDGROUPSTABLE;
	String alert="";
	
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	int editId = map.getInt("ID",0);
	
	String error = "";
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 result = UsersAgent.saveUser(map);
		 if(!result){
			 error = "</br> Problem while saving the user.</br> Username/Email already exists";
		 }
		 map.remove("_action");
		 if(result && editId==0){
			 editId = UsersAgent.getUser(map);
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
	ArrayList<MapSet> userattachments = new ArrayList<MapSet>();
	ArrayList<MapSet> userteams = new ArrayList<MapSet>();

	ArrayList<MapSet> userroles = new ArrayList<MapSet>();
	ArrayList<MapSet> peopleattachments = new ArrayList<MapSet>();
	ArrayList<MapSet> peoplelicense = new ArrayList<MapSet>();
	ArrayList<MapSet> usergroups = new ArrayList<MapSet>();
	ArrayList<MapSet> usertypes = new ArrayList<MapSet>();
	ArrayList<MapSet> userdeposit = new ArrayList<MapSet>();

	MapSet v = new MapSet();
	if(editId>0){
		MapSet m = AdminMap.getUser(editId);
	
		v = AdminAgent.getType(m.getString("_getuserquery"));
		System.out.println(v.getString("PEOPLE_ID")+"################");
		usertypes = AdminAgent.getList(m.getString("_getusertypesquery"));
		usergroups = AdminAgent.getList(m.getString("_getusergroupsquery"));
		userattachments = AdminAgent.getList(m.getString("_getuserattachmentsquery"));
		userteams = AdminAgent.getList(m.getString("_getuserteamsquery"));
		userroles = AdminAgent.getList(m.getString("_getuserrolesquery"));
		userdeposit = AdminAgent.getList(m.getString("_getdepositsquery"));
		
		if(v.getBoolean("ENABLE_PEOPLE")){
			peopleattachments = AdminAgent.getList(m.getString("_getpeopleattachmentsquery"));
			peoplelicense = AdminAgent.getList(m.getString("_getpeoplelicensequery"));
			
		}
	}
	
	 JSONArray depts = AdminAgent.getLookup("DEPARTMENT");
	 JSONArray types =  new JSONArray();
	 if(editId>0){
		 types = AdminAgent.getLookup("LKUP_USERS_TYPE",editId);
	 }else {
		 types =AdminAgent.getLookup("LKUP_USERS_TYPE");
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
	<script language="JavaScript" src="<%=Config.fullcontexturl()%>/tools/jq/json2.js"></script>
	
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
			        }
			
			    })
			    
			// $('#tabbed-nav').data('TYPE').select(1);
			
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
				$('#DEPARTMENT_ID').val(<%=v.getString("DEPARTMENT_ID")%>);
				$('#DEPARTMENT_ID').trigger('chosen:updated');
				
				<% if(v.getString("ENABLE_PEOPLE").equals("N")){%>
					$('.editpeoplecl').hide();
				<% } else { %>
					$('.editpeoplecl').show();
				<% } %>
				<% if(v.getString("ENABLE_STAFF").equals("N")){%>
					$('.editstaffcl').hide();
				<% } else { %>
					$('.editstaffcl').show();
				<% }  %>
				
			<% }%>
			
		
			
			$('#LKUP_USER_TYPE_ID').change(function(){
				var method = "addtype";
				var typeid= $(this).val();
				$.ajax({
		 			  type: "POST",
		 			  url: "../action.jsp?_action="+method,
		 			  dataType: 'json',		  
		 			  data: { 
		 				  _type : "<%=type%>",
		 				  _typeid : "<%=typeid%>",
		 				 _id : <%=id%>,
		 				 LKUP_USERS_TYPE_ID : typeid,
		 				 USERS_ID : <%=editId%>
		 				  //mode : mode
		 			    },
		 			    success: function(output) {
		 			    	//swal("Sorted successfully ");
		 			    	//alert(JSON.stringify(output));
		 			    	appendtypes(output);
		 			    	
		 			    	
		 			    	
		 			    		
		 			    },
		 		    error: function(data) {
		 		    	swal("Problem while processing the request");  
		 		    }
				 });		
				
				
			});
			
			
			$('#ENABLE_STAFF').change(function(){
				
				  //$('input:checkbox').not(this).prop('checked', this.checked);
				if($(this).prop('checked')==true){
					$('.editstaffcl').show();
					//$('.noeditstaffcl').hide();
				}else {
					$('.editstaffcl').hide();
					//$('.noeditstaffcl').show();
				}
				//if($(this).val()=="")
				
			});
			
			
			$('#ENABLE_PEOPLE').change(function(){
				
				  //$('input:checkbox').not(this).prop('checked', this.checked);
				if($(this).prop('checked')==true){
					$('.editpeoplecl').show();
					//$('.noeditpeoplecl').hide();
				}else {
					$('.editpeoplecl').hide();
					//$('.noeditpeoplecl').show();
				}
				//if($(this).val()=="")
				
			});
			
			
			
		});
		
		
		function showchildgroup(id){
			$("#show_"+id).toggle();
			showPayment(id);
		}
		
		function showchild(id){
			$("#show_"+id).toggle();
			showPayment(id);
		}
		
		
		
		
		function showPayment(id){
			//alert(id);
			var method = "showdepositledger";
			var ty ="{}";
			$.ajax({
				  type: "POST",
				  url: "../action.jsp?_action="+method,
				  dataType: 'json',		  
				  data: { 
					 cartjson : ty,
				  	  _ent : "lso",
					 _type:"finance",
					 _grptype:"deposit",
				      P_ID : id
				      //mode : mode
				    },
				    success: function(output) {
				    	displayledger(output,id);
				    	
				    },
			    error: function(data) {
			        alert('Your request was not processed. Please check your input data.');
			    }
			});
		}
		
		
		

		function displayledger(output,id){
			output = JSON.stringify(output);
			output = JSON.parse(output);
			
			var c='';
			c += '<td colspan="8" >';
			c += '	<table class="csui" width="100%" >';
			c += '		<tr>';
			c += '			<td class="csui_header" colspan="2" align="right">&nbsp;</td>';
			c += '			<td class="csui_header" colspan="2" align="center">DATE</td>';
			c += '			<td class="csui_header">AMOUNT</td>';
			c += '			<td class="csui_header">CURRENT AMOUNT</td>';
			c += '			<td class="csui_header">PARENT ID</td>';
			c += '			<td class="csui_header">TRANSACTION ID</td>';
			c += '			<td class="csui_header" width="1%">&nbsp;</td>';
			c += '			<td class="csui_header" width="1%">&nbsp;</td>';
			c += '		</tr>';
			c += '';
			
			
			$.each(output['depositcredits'], function(k,v) {
		
				c += '		<tr>';
				c += '			<td class="csui" colspan="2" align="right">&nbsp;</td>';
				c += '			<td class="csui_header" colspan="2" align="center">'+v.createddate+'</td>';
				c += '			<td class="csui">$'+v.amount+'</td>';
				c += '			<td class="csui">$'+v.currentamount+'</td>';
				c += '			<td class="csui">'+v.parentid+'</td>';
				c += '			<td class="csui">'+v.paymentid+'</td>';
				c += '			<td class="csui" width="1%" >&nbsp;</td>';
				c += '			<td class="csui_header" width="1%">&nbsp;</td>';
				c += '		</tr>';
			
	 		});
			
		
			c += '';
			c += '	</table>';
			c += '</td>';
			
			$("#show_"+id).html(c);
			
		}
		
		
		function deletetype(id,opt){
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
				   			  url: "<%=Config.fullcontexturl()%>/jsp/admin/user/action.jsp?_action="+method,
				   			  dataType: 'json',		  
				   			  data: { 
				   				  _type : "<%=type%>",
				   				  _typeid : "<%=typeid%>",
				   				  _id : <%=id%>,
				   				   ID : id,
				   				   OPT : opt
				   			      //mode : mode
				   			    },
				   			    success: function(output) {
				   			    		$('#tr_'+id).remove();
				   			    		swal({   title: "Deleted",   text: "The record has been deleted.!",   timer: 1000,   showConfirmButton: false });
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
		

		
function appendtypes(output){
	var c ='';
	
	c +=' <tr id="tr_'+output.ID+'">' ;
	c +=' <td class="csui">'+output.TYPE+'</td>' ;
	c +=' <td class="csui">'+output.DESCRIPTION+'</td>' ;
	c +=' <td class="csui">'+output.CREATED+'</td>' ;
	c +=' <td class="csui">'+output.C_CREATED_DATE+'</td>' ;
	c +=' <td class="csui">'+output.UPDATED+'</td>' ;
	c +=' <td class="csui">'+output.C_UPDATED_DATE+'</td>' ;
	c +=' <td class="csui" width="1%">' ;
	c +=' <a target="lightbox-iframe" href="type.jsp?_id='+output.ID+'&process=edit" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>' ;
	c +=' </td>' ;
	c +=' <td class="csui" width="1%">' ;
	c +=' <a href="javascript:void(0);" target="" onclick="deletetype('+output.ID+',"refusers");" ><img src="/cs/images/icons/controls/black/delete.png" border="0"></a>' ;
	c +=' </td>' ;
	c +=' </tr>' ;
	
	//alert(c);
	$('#typeshtml').append(c);
	var o = output.LKUP_USER_TYPE_ID;
	//alert(o);
	$("#LKUP_USER_TYPE_ID option[value="+o+"]").remove(); 
	$('#LKUP_USER_TYPE_ID').trigger('chosen:updated');
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
						<td align="left" id="title">USER <%=v.getString("USERNAME") %><font color="#762525"><%=error %></font></td>
						<td align="right" id="subtitle"></td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" class="form" ajax="no" action="user.jsp" method="post">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
					<input type="hidden" name="ID" value="<%=editId%>">
					<input type="hidden" name="tabletype" value="single">
					
					<input type="hidden" name="STAFF_ID" value="<%=v.getString("STAFF_ID") %>">
					<input type="hidden" name="PEOPLE_ID" value="<%=v.getString("PEOPLE_ID") %>">
					
					
					<div id="tabbed-nav">
					  <ul>
					    <li><a>USER</a></li>
					    <%if(editId>0) {%>
							<li><a>PEOPLE</a></li>
							<li><a>TYPE</a></li>
							<li><a>GROUPS</a></li>
							<li><a>STAFF</a></li>
							<li><a>DEPOSIT</a></li>
							
							<li><a>TEAM</a></li>
							<li><a>ROLES</a></li>
						<%}%>
					  </ul>
					  <div>
						
						
						<!-- USER -->	
						<div>
					
						
						
						
						<table class="csui editusercl " colnum="2" type="default">
						
						 	<tr>
								<td class="csui_label" colnum="2" alert="">USERNAME</td>
								<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="USERNAME" type="text" itype="string" value="<%=v.getString("USERNAME") %>" valrequired="true" maxchar="10000"></td>
								<td class="csui_label" colnum="2" alert="">EMAIL</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" ><input  name="EMAIL" type="text" itype="email" value="<%=v.getString("EMAIL") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							
							
							
							
							<tr>
								<td class="csui_label" colnum="2" alert="">NAME</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
									<table class="csui" colnum="2" type="default">
										<tr>
											<td class="csui" colnum="2" alert="">FIRST	<input name="FIRST_NAME" type="text" itype="text" value="<%=v.getString("FIRST_NAME") %>" valrequired="true" maxchar="10000"></td>
											<td class="csui" colnum="2" alert="">MIDDLE	 <input name="MI" type="text" itype="text" value="<%=v.getString("MI") %>" valrequired="false" maxchar="10000"></td>
											<td class="csui" colnum="2" alert="">LAST	 <input name="LAST_NAME" type="text" itype="text" value="<%=v.getString("LAST_NAME") %>" valrequired="true" maxchar="10000"></td>
										</tr>
										
									</table>
									
									
								</td>
							
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">LAST LOGIN</td>
								<td class="csui" colnum="2" type="DATETIME" itype="String" alert="" colspan="4"><%=v.getString("C_CREATED_DATE") %></td>					
							</tr>
							<tr>
								
								<td class="csui_label" colnum="2" alert="">E-NOTIFY</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" colspan="4"><div><input name="NOTIFY" id="NOTIFY" type="checkbox"  itype="boolean" <%if(v.getString("NOTIFY").equals("Y")){ %>checked <% }%> ></div></td>
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
						
						
					</div>
					
					 <%if(editId>0) {%>
					
					<!-- PEOPLE FORM -->	
						<div>
						
						<table class="csui" colnum="2" type="default" >
							<tr>
								
								<td class="csui_label" colnum="2" alert="">ENABLE PEOPLE</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" colspan="4"><div><input name="ENABLE_PEOPLE" id="ENABLE_PEOPLE" type="checkbox"  itype="boolean" <%if(v.getString("ENABLE_PEOPLE").equals("Y")){ %>checked <% }%> ></div></td>
							</tr>
						</table>
					
						<table class="csui  editpeoplecl" colnum="2" type="default" >
						
						
							<tr>
								<td class="csui_label" colnum="2" alert="">ADDRESS</td>
								<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="ADDRESS" type="text" itype="text" value="<%=v.getString("ADDRESS") %>" valrequired="true" maxchar="10000"></td>
								<td class="csui_label" colnum="2" alert="">CITY</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" ><input  name="CITY" type="text" itype="text" value="<%=v.getString("CITY") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">STATE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="STATE" type="text" itype="text" value="<%=v.getString("STATE") %>" valrequired="true" maxchar="10000"></td>
								<td class="csui_label" colnum="2" alert="">ZIP</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" ><input  name="ZIP" type="text" itype="text" value="<%=v.getString("ZIP") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							
							
							<tr>
								<td class="csui_label" colnum="2" alert="">PHONE_WORK</td>
								<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="P_PHONE_WORK" type="text" itype="phone" value="<%=v.getString("P_PHONE_WORK") %>" valrequired="true" maxchar="10000"></td>							
								
								<td class="csui_label" colnum="2" alert="">PHONE_CELL</td>
								<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="P_PHONE_CELL" type="text" itype="phone" value="<%=v.getString("P_PHONE_CELL") %>" valrequired="true" maxchar="10000"></td>			
								
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">COMMENTS</td>
								<td class="csui" colnum="2" type="DATETIME" itype="String" alert="" colspan="4"><textarea name="COMMENTS"><%=v.getString("COMMENTS") %></textarea></td>					
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">LICENSE</td>
								<td class="csui" colnum="2" type="DATETIME" itype="String" alert="" colspan="4"><input name="LICENCE" type="text" itype="text" value="87956" valrequired="true" maxchar="10000"></td>					
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
						<div class="csui_divider"></div>
							
							<%if(v.getInt("PEOPLE_ID")>0){ %>
							<!-- List LICENSE-->
						 	<table class="csui_title editpeoplecl" alert="warning">
								<tr>
								<td class="csui_title">LICENSE</td>
								<td class="csui_tools" align="right">
									<a title="Contractor" target="lightbox-iframe" href="https://www2.cslb.ca.gov/OnlineServices/CheckLicenseII/checklicense.aspx " ><img src="/cs/images/icons/controls/black/lic.png" border="0"></a>
								</td>
								<td class="csui_tools" align="right">
									<a title="Engineer" target="lightbox-iframe" href="http://www2.dca.ca.gov/pls/wllpub/wllqryna$lcev2.startup?p_qte_code=ENG&p_qte_pgm_code=7500 " ><img src="/cs/images/icons/controls/black/lic.png" border="0"></a>
								</td>
								<td class="csui_tools" align="right">
									<a title="Architect " target="lightbox-iframe" href="http://www2.dca.ca.gov/pls/wllpub/wllqryna$lcev2.startup?p_qte_code=GEN&p_qte_pgm_code=0600" ><img src="/cs/images/icons/controls/black/lic.png" border="0"></a>
								</td>
								<td class="csui_tools" align="right">
									<a target="lightbox-iframe" href="lic.jsp?_ent=admin&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&PEOPLE_ID=<%=v.getInt("PEOPLE_ID")%>" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
								</td>
								</tr>
							</table>
				
							
							<table class="csui editpeoplecl" type="horizontal">
								
								
								 <thead>
									<tr>
										
										<td class="csui_header">TYPE</td>
										<td class="csui_header">LIC NUM</td>
										<td class="csui_header">EXPIRATION DATE</td>
										<td class="csui_header">CREATED BY</td>
										<td class="csui_header">CREATED</td>
										<td class="csui_header">UPDATED BY</td>
										<td class="csui_header">UPDATED</td>
										<td class="csui_header" width="1%">&nbsp;</td>
										<td class="csui_header" width="1%">&nbsp;</td>
										<td class="csui_header" width="1%">&nbsp;</td>
									</tr>
								</thead>
								 <tbody>
									<%for(int i=0;i<peoplelicense.size();i++){
										MapSet ty = peoplelicense.get(i);
										
										boolean licexpired = Operator.s2b(ty.getString("LIC_EXPIRED"));
										String style = "background-color: #8f6662;";
										if(!licexpired){
											 style = "";
										}
									%>
									<tr id="tr_<%=ty.getString("ID") %>" >
										<td class="csui" ><%=ty.getString("TYPE") %></td>
										<td class="csui" style="<%=style %>" ><%=ty.getString("LIC_NUM") %></td>
										<td class="csui"  style="<%=style %>" ><%=ty.getString("LIC_EXPIRATION_DATE") %></td>
										<td class="csui" ><%=ty.getString("CREATED") %></td>
										<td class="csui" ><%=ty.getString("C_CREATED_DATE") %></td>
										<td class="csui" ><%=ty.getString("UPDATED") %></td>
										<td class="csui" ><%=ty.getString("C_UPDATED_DATE") %></td>
										<td class="csui"  >
										<a target="lightbox-iframe" href="https://www2.cslb.ca.gov/onlineservices/CheckLicenseII/LicenseDetail.aspx?LicNum=<%=ty.getString("LIC_NUM") %>" ><img src="/cs/images/icons/controls/black/lic.png" border="0"></a>
										</td>
										<td class="csui" width="1%" alert="hold_h" >
											<a target="lightbox-iframe" href="lic.jsp?_ent=admin&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&PEOPLE_ID=<%=ty.getString("PEOPLE_ID") %>&ID=<%=ty.getString("ID") %>&process=edit" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>
										</td>
										<td class="csui" width="1%" alert="hold_h" >
										<a href="javascript:void(0);" target="" onclick="deletetype(<%=ty.getString("ID")%>,'license');" ><img src="/cs/images/icons/controls/black/delete.png" border="0"></a>
										</td>
									</tr>
								<%} %>
								</tbody>
							</table>
							
							
							<div class="csui_divider"></div>
							<table class="csui_title" alert="warning">
								<tr>
								<td class="csui_title">ATTACHMENTS</td>
								<td class="csui_tools" align="right">
									<a target="lightbox-iframe" href="lic.jsp?_ent=admin&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&PEOPLE_ID=<%=v.getInt("PEOPLE_ID")%>" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
								</td>
								</tr>
							</table>
					
							<table class="csui" type="horizontal">
								
								
								 <thead>
									<tr>
										<td class="csui_header">TITLE</td>
										<td class="csui_header">DESCRIPTION</td>
										<td class="csui_header">CREATED BY</td>
										<td class="csui_header">CREATED</td>
										<td class="csui_header">UPDATED BY</td>
										<td class="csui_header">UPDATED</td>
										<td class="csui_header" width="1%">&nbsp;</td>
										
									</tr>
								</thead>
								 <tbody>
									<%for(int i=0;i<peopleattachments.size();i++){
										MapSet ty = peopleattachments.get(i);
									%>
									<tr id="tr_<%=ty.getString("ID") %>">
										<td class="csui"><%=ty.getString("TITLE") %></td>
										<td class="csui"><%=ty.getString("DESCRIPTION") %></td>
										<td class="csui"><%=ty.getString("CREATED") %></td>
										<td class="csui"><%=ty.getString("C_CREATED_DATE") %></td>
										<td class="csui"><%=ty.getString("UPDATED") %></td>
										<td class="csui"><%=ty.getString("C_UPDATED_DATE") %></td>
										<td class="csui" width="1%">
										<a href="javascript:void(0);" target="" onclick="deletetype(<%=ty.getString("ID") %>);" ><img src="/cs/images/icons/controls/black/delete.png" border="0"></a>
										</td>
									</tr>
								<%} %>
								</tbody>
							</table>
							
							
						<%} %>
						</div>
						
						<!-- TYPE-->
						<div style=" display: flex; flex-direction: column; min-height: 50vh;">
						<div>
							<table class="csui" colnum="2" type="default" >
								<tr>
									<td class="csui_label" colnum="2" alert="">ADD TYPE</td>
								
								
									<td class="csui" align="left">
									<select   name="LKUP_USER_TYPE_ID" id="LKUP_USER_TYPE_ID" itype="String" val="" _ent="lso" valrequired="true">
									<option value=""></option>
										<%for(int i=0;i<types.length();i++){ %>	<option value="<%=types.getJSONObject(i).getInt("ID")%>"><%=types.getJSONObject(i).getString("TEXT")%></option>		<%}%>
									</select>
									</td>
								</tr>
								
							</table>
						
							<table class="csui" type="horizontal">
							
							
							 <thead>
								<tr>
									
									<td class="csui_header">TYPE</td>
									<td class="csui_header">DESCRIPTION</td>
									<td class="csui_header">CREATED BY</td>
									<td class="csui_header">CREATED</td>
									<td class="csui_header">UPDATED BY</td>
									<td class="csui_header">UPDATED</td>
									<td class="csui_header" width="1%">&nbsp;</td>
									<td class="csui_header" width="1%">&nbsp;</td>
									
								</tr>
							</thead>
							 <tbody id="typeshtml">
								<%for(int i=0;i<usertypes.size();i++){
									MapSet ty = usertypes.get(i);
								%>
								<tr id="tr_<%=ty.getString("ID") %>">
									<td class="csui"><%=ty.getString("TYPE") %></td>
									<td class="csui"><%=ty.getString("DESCRIPTION") %></td>
									<td class="csui"><%=ty.getString("CREATED") %></td>
									<td class="csui"><%=ty.getString("C_CREATED_DATE") %></td>
									<td class="csui"><%=ty.getString("UPDATED") %></td>
									<td class="csui"><%=ty.getString("C_UPDATED_DATE") %></td>
									<td class="csui" width="1%">
									<a target="lightbox-iframe" href="add.jsp?_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=ty.getString("ID") %>&process=edit" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>
									</td>
									<td class="csui" width="1%">
									<a href="javascript:void(0);" target="" onclick="deletetype(<%=ty.getString("ID") %>,'refusers');" ><img src="/cs/images/icons/controls/black/delete.png" border="0"></a>
									</td>
								</tr>
							<%} %>
							</tbody>
						</table>
						</div>
						</div>
						<!-- groups -->
						<div>
						
							 <table class="csui_title" alert="warning">
								<tr>
									<td class="csui_title">GROUPS</td>
									<td class="csui_tools">
										<a target="lightbox-iframe" href="../groups/clist.jsp?_ent=admin&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=editId%>" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
									</td>
								</tr>
							</table>
					
							<table class="csui" type="horizontal">
								 <thead>
									<tr>
										
										<td class="csui_header">TYPE</td>
										<td class="csui_header">CREATED BY</td>
										<td class="csui_header">CREATED</td>
										<td class="csui_header">UPDATED BY</td>
										<td class="csui_header">UPDATED</td>
										<td class="csui_header" width="1%">&nbsp;</td>
										
									</tr>
								</thead>
								 <tbody>
									<%for(int i=0;i<usergroups.size();i++){
										MapSet ty = usergroups.get(i);
									%>
									<tr id="trug_<%=ty.getString("ID") %>">
										<td class="csui"><%=ty.getString("GROUP_NAME") %></td>
										<td class="csui"><%=ty.getString("CREATED") %></td>
										<td class="csui"><%=ty.getString("C_CREATED_DATE") %></td>
										<td class="csui"><%=ty.getString("UPDATED") %></td>
										<td class="csui"><%=ty.getString("C_UPDATED_DATE") %></td>
										<td class="csui" width="1%">
										<a href="javascript:void(0);" target="" onclick="deletetype(<%=ty.getString("ID") %>);" ><img src="/cs/images/icons/controls/black/delete.png" border="0"></a>
										</td>
									</tr>
								<%} %>
								</tbody>
							</table>

						</div>  
						
						<!-- STAFF-->
						<div>
						
							<table class="csui" colnum="2" type="default" >
								<tr>
									
									<td class="csui_label" colnum="2" alert="">ENABLE STAFF</td>
									<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" colspan="4"><div><input name="ENABLE_STAFF" id="ENABLE_STAFF" type="checkbox"  itype="boolean" <%if(v.getString("ENABLE_STAFF").equals("Y")){ %>checked <% }%> ></div></td>
								</tr>
							</table>
							
							<table class="csui editstaffcl" colnum="2" type="default">
							
							
								<tr>
									<td class="csui_label" colnum="2" alert="">EMPL NUM</td>
									<td class="csui" colnum="2" type="String" itype="text" alert="">
										<input name="EMPL_NUM" type="text" itype="text" value="<%=v.getString("EMPL_NUM") %>" valrequired="false" maxchar="10000">
									</td>
									<td class="csui_label" colnum="2" alert="">DEPARTMENT</td>
									<td class="csui" colnum="2" type="String" itype="text" alert="">
									<select  name="DEPARTMENT_ID" id="DEPARTMENT_ID" itype="String" val="" _ent="lso" valrequired="true"><option value=""></option>
										<%for(int i=0;i<depts.length();i++){ %>	<option value="<%=depts.getJSONObject(i).getInt("ID")%>"><%=depts.getJSONObject(i).getString("TEXT")%></option>		<%}%>
									</select>
									
									</td>
								</tr>
								
								
								
								<tr>
									<td class="csui_label" colnum="2" alert="">TITLE</td>
									<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="TITLE" type="text" itype="text" value="<%=v.getString("TITLE") %>" valrequired="true" maxchar="10000"></td>							
									
									<td class="csui_label" colnum="2" alert="">DIVISION</td>
									<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="DIVISION" type="text" itype="text" value="<%=v.getString("DIVISION") %>" valrequired="false" maxchar="10000"></td>			
									
								</tr>
								
								<tr>
									<td class="csui_label" colnum="2" alert="">PHONE_WORK</td>
									<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="PHONE_WORK" type="text" itype="phone" value="<%=v.getString("PHONE_WORK") %>" valrequired="false" maxchar="10000"></td>							
									
									<td class="csui_label" colnum="2" alert="">PHONE_CELL</td>
									<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="PHONE_CELL" type="text" itype="phone" value="<%=v.getString("PHONE_CELL") %>" valrequired="false" maxchar="10000"></td>			
									
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
							
					
						</div>     
						
						
					<!-- DEPOSIT-->
					<div>
						 <table class="csui_title" alert="warning">
								<tr>
									<td class="csui_title">DEPOSIT</td>
									<td class="csui_tools">
										<a target="lightbox-iframe-refresh" href="<%=fullcontexturl %>/depositpayment.jsp?_ent=lso&_id=<%=id%>&_type=users&_typeid=<%=editId%>&ID=<%=editId%>&_grptype=deposit&_act=depositpayment" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
									</td>
								</tr>
							</table>
						
							<table class="csui" type="horizontal" >
							<tr>
								<td class="csui_header">NO</td>
								<td class="csui_header">DATE</td>
								<td class="csui_header">TYPE</td>
								<td class="csui_header">AMOUNT</td>
								<td class="csui_header">CURRENT AMOUNT</td>
								<td class="csui_header">TRANSACTION ID</td>
								<td class="csui_header">&nbsp;</td>
							</tr>
						
							
						
							<%
							DecimalFormat fm = new DecimalFormat("#,###.00"); 
							
							for(int i=0;i<userdeposit.size();i++){ 
								MapSet ty =  userdeposit.get(i);
								
							%>
						
						
							<tr>
								<td class="csui"  id="<%=ty.getInt("ID") %>_checkbox" >
								<%=ty.getInt("ID") %>
								</td>
								<td class="csui"><%=ty.getInt("ID") %></td>
								<td class="csui">Deposit</td>
								<td class="csui">$<%=fm.format(ty.getDouble("AMOUNT")) %></td>
								<td class="csui">$<%=fm.format(ty.getDouble("CURRENT_AMOUNT")) %></td>
	
								<td class="csui"><%=ty.getInt("PAYMENT_ID") %></td>
								<td class="csui" width="1%"><a href="javascript:void(0);" onclick="showchildgroup(<%=ty.getInt("ID")%>);"> <img src="<%=fullcontexturl %>/images/icons/controls/black/downarrow.png" height="16" width="16" ></a></td>
							</tr>
						
						
								<tr id="show_<%=ty.getInt("ID") %>" style="display:none;">
									
								</tr>	
							
							
								
						
						<%} %>
						
						
						</table>	
					</div>  
						
						
						
						
						
						<!-- team -->
						<div>
						
							 <table class="csui_title" alert="warning">
								<tr>
									<td class="csui_title">TEAM</td>
									<td class="csui_tools">
										<a target="lightbox-iframe" href="../team/clist.jsp?_ent=admin&_id=35&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=editId%>" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
									</td>
								</tr>
							</table>
							
							<table class="csui" type="horizontal" >
							<tr>
								<td class="csui_header">TYPE</td>
								<td class="csui_header">CREATED BY</td>
								<td class="csui_header">CREATED</td>
								<td class="csui_header">UPDATED BY</td>
								<td class="csui_header">UPDATED</td>
								<td class="csui_header" width="1%">&nbsp;</td>
							</tr>
						
							
						
							<%for(int i=0;i<userteams.size();i++){
											MapSet ty = userteams.get(i);
								%>
						
						
							<tr id="tr_<%=ty.getString("ID") %>">
								<td class="csui"><%=ty.getString("TYPE") %></td>
								<td class="csui"><%=ty.getString("CREATED") %></td>
								<td class="csui"><%=ty.getString("C_CREATED_DATE") %></td>
								<td class="csui"><%=ty.getString("UPDATED") %></td>
								<td class="csui"><%=ty.getString("C_UPDATED_DATE") %></td>
								<td class="csui" width="1%">
								<a href="javascript:void(0);" target="" onclick="deletetype(<%=ty.getString("ID") %>,'refteam');" ><img src="/cs/images/icons/controls/black/delete.png" border="0"></a>
								</td>
							</tr>
							<%} %>
						
						
						</table>	

					</div>  
						
						
						
						<!-- roles -->
						<div>
						
							 <table class="csui_title" alert="warning">
								<tr>
									<td class="csui_title">ROLE</td>
									<td class="csui_tools">
										<a target="lightbox-iframe" href="../roles/clist.jsp?_ent=admin&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=editId%>" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
									</td>
								</tr>
							</table>
							
							<table class="csui" type="horizontal" >
							<tr>
								<td class="csui_header">TYPE</td>
								<td class="csui_header">CREATED BY</td>
								<td class="csui_header">CREATED</td>
								<td class="csui_header">UPDATED BY</td>
								<td class="csui_header">UPDATED</td>
								<td class="csui_header" width="1%">&nbsp;</td>
							</tr>
						
							
						
							<%for(int i=0;i<userroles.size();i++){
											MapSet ty = userroles.get(i);
								%>
						
						
							<tr id="tr_<%=ty.getString("ID") %>">
								<td class="csui"><%=ty.getString("ROLE") %></td>
								<td class="csui"><%=ty.getString("CREATED") %></td>
								<td class="csui"><%=ty.getString("C_CREATED_DATE") %></td>
								<td class="csui"><%=ty.getString("UPDATED") %></td>
								<td class="csui"><%=ty.getString("C_UPDATED_DATE") %></td>
								<td class="csui" width="1%">
								<a href="javascript:void(0);" target="" onclick="deletetype(<%=ty.getString("ID") %>,'refroles');" ><img src="/cs/images/icons/controls/black/delete.png" border="0"></a>
								</td>
							</tr>
							<%} %>
						
						
						</table>	

					</div>  
						
						
						
						
			<%}%>			
						
						
						
						
						
						
						
						    
					  </div>
					</div>
				

					<div class="csui_divider">
			</div>
			<div class="csui_buttons">
				<input type="submit" name="_action" value="cancel" class="csui_button">&nbsp;
				<input type="submit" name="_action" value="save" class="csui_button">
			</div>
				</form>
				
				
				
				
					
					
					<div class="csui_divider"></div>
						<!-- List ATTACHMENTS-->
					 <table class="csui_title" alert="warning">
							<tr>
								<td class="csui_title">ATTACHMENTS</td>
								<td class="csui_tools">
									<a target="lightbox-iframe-refresh" href="<%=fullcontexturl %>/attachments.jsp?_ent=lso&_id=<%=0%>&_type=users&_typeid=<%=editId%>&ID=<%=editId%>&_grpid=attachments&_grp=attachments&_grptype=attachments&_act=add" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
								</td>
							</tr>
						</table>
					
					<table class="csui" type="horizontal">
							
							
							 <thead>
								<tr>
									<td class="csui_header">TITLE</td>
									<td class="csui_header">DESCRIPTION</td>
									<td class="csui_header">PATH</td>
									<td class="csui_header">CREATED BY</td>
									<td class="csui_header">CREATED</td>
									<td class="csui_header">UPDATED BY</td>
									<td class="csui_header">UPDATED</td>
									<td class="csui_header" width="1%">&nbsp;</td>
									
								</tr>
							</thead>
							 <tbody>
								<%for(int i=0;i<userattachments.size();i++){
									MapSet ty = userattachments.get(i);
								%>
								<tr id="tr_<%=ty.getString("ID") %>">
									<td class="csui"><%=ty.getString("TITLE") %></td>
									<td class="csui"><%=ty.getString("DESCRIPTION") %></td>
									<td class="csui"><a href="<%=ty.getString("PATH") %>" target="_blank"> <%=ty.getString("PATH") %></a></td>
									<td class="csui"><%=ty.getString("CREATED") %></td>
									<td class="csui"><%=ty.getString("C_CREATED_DATE") %></td>
									<td class="csui"><%=ty.getString("UPDATED") %></td>
									<td class="csui"><%=ty.getString("C_UPDATED_DATE") %></td>
									<td class="csui" width="1%">
									<a href="javascript:void(0);" target="" onclick="deletetype(<%=ty.getString("ID") %>);" ><img src="/cs/images/icons/controls/black/delete.png" border="0"></a>
									</td>
								</tr>
							<%} %>
							</tbody>
						</table>
				
				
				
			</div>
		</div>
	</div>






</body>
</html>


