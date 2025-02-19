<%@page import="csapi.tasks.Review"%>
<%@page import="csapi.tasks.PDoxReleasePlans"%>
<%@page import="csapi.tasks.PDoxCreator"%>
<%@page import="csapi.tasks.Payment"%>
<%@page import="alain.core.utils.Timekeeper"%>
<%@page import="csapi.impl.admin.AdminMap"%>
<%@page import="csapi.tasks.process.Task"%>
<%@page import="org.json.JSONArray"%>
<%@page import="csapi.tasks.Notify"%>
<%@page import="alain.core.utils.Operator"%>
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
	int lkup_id = map.getInt("ID");
	int editId = map.getInt("TASK_ID");
	int ref_id = map.getInt("REF_ID");
	
	System.out.println(ref_id+"sssssssssssssssssss");
	
	MapSet v = new MapSet();
	MapSet v2 = new MapSet();
	String table = "TASKS_REVIEW";
	String reftable = "REF_ACT_TYPE_TASKS";
	if(editId>0){
		v = AdminAgent.getType(editId,table);
		v2 = AdminAgent.getType(ref_id,reftable);
	}
	
	
	
	
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 MapSet payment =Review.getOptions(map);
		
		 if(editId>0){
			 payment.remove("ID");
			 payment.add("ID", editId);
			 result = AdminAgent.updateType(payment,table);
			 
			 MapSet m = AdminMap.getCommon(map);
			 m.add("ID", ref_id);
			 m.add("DESCRIPTION", map.getString("DESCRIPTION"));
			 
			 result = AdminAgent.updateType(m,reftable);
			 
		 }else {
			 
			
				 result = AdminAgent.saveType(payment,table); 
				 MapSet cr = AdminAgent.getType(payment.getString("lastinsert"));
				 if(cr.getInt("ID")>0){
					 map.setString("TASK_ID", cr.getString("ID"));
					 MapSet task =AdminMap.getTaskOptions(map,reftable);
					 result = AdminAgent.saveType(task,reftable);
				 }
			 
		 }
		 
		 
	 }
	 if(map.equalsIgnoreCase("_action","CANCEL")){
			result = true;
	 }
	 
	 JSONArray templates = AdminAgent.getJsonList("select DISTINCT T.ID,T.NAME as TEXT from REF_ACT_TEMPLATE RAT JOIN TEMPLATE T on RAT.TEMPLATE_ID=T.ID where RAT.ACTIVE='Y' AND RAT.LKUP_ACT_TYPE_ID = "+lkup_id );
	 JSONArray  types =AdminAgent.getLookup("LKUP_USERS_TYPE");
	 
	 JSONArray  teamtypes =AdminAgent.getLookup("LKUP_TEAM_TYPE");
	 
	 JSONArray  status =AdminAgent.getJsonList("select DISTINCT T.ID,T.STATUS as TEXT from REF_ACT_TYPE_STATUS RAT JOIN LKUP_ACT_STATUS T on RAT.LKUP_ACT_STATUS_ID=T.ID where RAT.ACTIVE='Y' AND RAT.LKUP_ACT_TYPE_ID = "+lkup_id);
	 
	 JSONArray  reviewgroups =AdminAgent.getJsonList("select DISTINCT T.ID,T.GROUP_NAME as TEXT from REF_ACT_REVIEW_GROUP RAT JOIN REVIEW_GROUP T on RAT.REVIEW_GROUP_ID=T.ID where RAT.ACTIVE='Y' AND RAT.LKUP_ACT_TYPE_ID = "+lkup_id);

	 
	 JSONArray  reviewstatus =AdminAgent.getJsonList("select DISTINCT RS.* from REVIEW_GROUP RG JOIN REVIEW R on RG.ID = R.REVIEW_GROUP_ID AND R.ACTIVE='Y' JOIN LKUP_REVIEW_STATUS RS on R.LKUP_REVIEW_TYPE_ID = RS.LKUP_REVIEW_TYPE_ID AND RS.ACTIVE='Y' 	WHERE RG.ID =  "+v.getString("REVIEW_GROUP_ID"));

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
		<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/chosen/chosen.css"/>
	
	<style>
		.csui_controls { visibility: hidden }
		.searchbox {  -webkit-border-radius: 50px;-moz-border-radius: 50px;	border-radius: 50px;width:200px;height:25px;align:right;	}
	</style>


	<script type="text/javascript" src="<%=fullcontexturl %>/tools/jquery.min.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/alain/cs.tools.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/alain/cs.form.js"></script>
		<script type="text/javascript" src="<%=fullcontexturl %>/tools/chosen/chosen.jquery.js"></script>
	
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
			
			$('#TEMPLATE_ID').val(<%=v.getString("TEMPLATE_ID")%>);
			$('#TEMPLATE_ID').trigger('chosen:updated');
			
			
			$('#EMAIL_PEOPLE_TYPE').val("<%=v.getString("EMAIL_PEOPLE_TYPE")%>");
			$('#EMAIL_PEOPLE_TYPE').trigger('chosen:updated');
			
			$('#DATE_SELECT').val("<%=v.getString("DATE_SELECT")%>");
			$('#DATE_SELECT').trigger('chosen:updated');
			
			
			
			
			<% if(Operator.hasValue(v.getString("EMAIL_PEOPLE_TYPE"))){
			   String sa[] = Operator.split(v.getString("EMAIL_PEOPLE_TYPE"), "|");
			  	  
			
			%>
			var my_val = "<%=v.getString("EMAIL_PEOPLE_TYPE")%>";
			var str_array = my_val.split('|');
			$('#EMAIL_PEOPLE_TYPE').val(str_array).trigger("chosen:updated");
			
			<%  }%>
			
			
			<% if(Operator.hasValue(v.getString("EMAIL_TEAM_TYPE"))){
				   String sa[] = Operator.split(v.getString("EMAIL_TEAM_TYPE"), "|");
				  	  
				
				%>
				var my_val = "<%=v.getString("EMAIL_TEAM_TYPE")%>";
				var str_array = my_val.split('|');
				$('#EMAIL_TEAM_TYPE').val(str_array).trigger("chosen:updated");
				
				<%  }%>
				
				<% if(Operator.hasValue(v.getString("REVIEW_GROUP_ID"))){
					   String sa[] = Operator.split(v.getString("REVIEW_GROUP_ID"), "|");
					  	  
					
					%>
					var my_val = "<%=v.getString("REVIEW_GROUP_ID")%>";
					var str_array = my_val.split('|');
					$('#REVIEW_GROUP_ID').val(str_array).trigger("chosen:updated");
					
					<%  }%>	
					
					
					
					<% if(Operator.hasValue(v.getString("LKUP_REVIEW_STATUS_ID"))){
						   String sa[] = Operator.split(v.getString("LKUP_REVIEW_STATUS_ID"), "|");
						  	  
						
						%>
						var my_val = "<%=v.getString("LKUP_REVIEW_STATUS_ID")%>";
						var str_array = my_val.split('|');
						$('#LKUP_REVIEW_STATUS_ID').val(str_array).trigger("chosen:updated");
						
						<%  }%>	
					
					
						
						<% if(Operator.hasValue(v.getString("STATUS_CHANGE_ID"))){
							  
							  	  
							
							%>
							var my_val = "<%=v.getString("STATUS_CHANGE_ID")%>";
							var str_array = my_val;
							$('#STATUS_CHANGE_ID').val(str_array).trigger("chosen:updated");
							
							<%  }%>	
							
							
							
							<% if(Operator.hasValue(v.getString("NOT_IN_STATUS_ID"))){
								   String sa[] = Operator.split(v.getString("NOT_IN_STATUS_ID"), "|");
								  	  
								
								%>
								var my_val = "<%=v.getString("NOT_IN_STATUS_ID")%>";
								var str_array = my_val.split('|');
								$('#NOT_IN_STATUS_ID').val(str_array).trigger("chosen:updated");
								
								<%  }%>	
				
					
			
			
		<% }%>
			
		 $('.dtsel').change(function(){
				var v = $(this).val();
				var div = $(this).attr("div");
				var today = new Date();
				var dt = v
			    var newdate = new Date();
				newdate.setDate(today.getDate() +parseInt(dt));
				var dd = newdate.getDate();
				var mm = newdate.getMonth()+1; 
				var yyyy = newdate.getFullYear();
				if(dd<10) 
				{
				    dd='0'+dd;
				} 
				if(mm<10) 
				{
				    mm='0'+mm;
				} 
				var  t = mm+'/'+dd+'/'+yyyy;
				$('#'+div).html(t);
		 });	
		
		 
		 $('#selection').change(function(){
			 
			
			 if($(this).prop("checked") == true){
	               
	                var r = $('#STATUS_CHANGE_ID').val();
	               
	                var select_ids = [];
	                $('#NOT_IN_STATUS_ID option').each(function(index, element) {
	                   //select_ids.push($(this).val());
	                  if($(this).val() !=r){
	                	//select_ids += $(this).val()+"|";
	                	  select_ids.push($(this).val());
	                   }
	                	
	                    
	                });
	               // var str_array = select_ids;
	               $('#NOT_IN_STATUS_ID').val(select_ids).trigger("chosen:updated");
	            }
	            else if($(this).prop("checked") == false){
	               
	                $('#NOT_IN_STATUS_ID').val('').trigger("chosen:updated");
	            }
			 
			
			 
		 });	
		 
		 $('#REVIEW_GROUP_ID').change(function(){
				
				var method = "reviewstatus";
				
				var typeid= $(this).find(':selected').attr('typeid');
				//alert(typeid);
				$.ajax({
		 			  type: "POST",
		 			  url: "action.jsp?_action="+method,
		 			  dataType: 'json',		  
		 			  data: { 
		 				 REVIEW_GROUP_ID : typeid
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
		 
		
		
		});
		
		
		
		
		
		function appendtypes(output){
			$.each(output, function(k,v) {
			
				
				$('<option/>')
				  .val(v.ID)
				  .text(v.STATUS)
				  .appendTo('#LKUP_REVIEW_STATUS_ID')
			 });		
			$('#LKUP_REVIEW_STATUS_ID').trigger('chosen:updated');
			
			
		}		
		//function 
	
		
		
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
						<td align="left" id="title">REVIEW</td>
						<td align="right" id="subtitle">&nbsp;</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" class="form" ajax="no" action="review.jsp" method="post">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="LKUP_ACT_TYPE_ID" value="<%=lkup_id%>">
					<input type="hidden" name="TASK" value="Review">
					<input type="hidden" name="ID" value="<%=lkup_id%>">
					<input type="hidden" name="TASK_ID" value="<%=editId%>">
					<input type="hidden" name="REF_ID" value="<%=ref_id%>">
					
					<table class="csui_title">
						<tr>
							<td class="csui_title">REVIEW</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
						
							<tr>
								<td class="csui_label" colnum="2" alert="">TASK DESCRIPTION<font color="red">*</font></td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="3">
									<textarea name="DESCRIPTION" valrequired="true"><%=v2.getString("DESCRIPTION") %></textarea>
								</td>
								
							</tr>
							
							
							
							<%if(ref_id<=0){ %>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">REVIEW GROUPS<font color="red">*</font></td>
								<td class="csui" colnum="2" type="date" itype="date" alert="" colspan="3">
									<select   name="REVIEW_GROUP_ID" id="REVIEW_GROUP_ID" itype="String" val="" _ent="lso" valrequired="false" >
									<option value=""></option>
										<%for(int i=0;i<reviewgroups.length();i++){ %>	<option typeid="<%=reviewgroups.getJSONObject(i).getInt("ID")%>"  value="<%=reviewgroups.getJSONObject(i).getInt("ID")%>"><%=reviewgroups.getJSONObject(i).getString("TEXT")%></option>		<%}%>
									</select>
								</td>
							</tr>
							
							
							<tr>
								<td class="csui_label" colnum="2" alert="">REVIEW STATUS<font color="red">*</font></td>
								<td class="csui" colnum="2" type="date" itype="date" alert="" colspan="3">
									<select   name="LKUP_REVIEW_STATUS_ID" id="LKUP_REVIEW_STATUS_ID" itype="String" val="" _ent="lso" valrequired="false"  multiple="multiple">
									<option value=""></option>
								
									</select>
								</td>
							</tr>
							<%}else { %>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">REVIEW GROUPS<font color="red">*</font></td>
								<td class="csui" colnum="2" type="date" itype="date" alert="" colspan="3">
								<input type="hidden" name="REVIEW_GROUP_ID" value="<%=v.getString("REVIEW_GROUP_ID")%>">
									<select   name="REVIEW_GROUP_ID" id="REVIEW_GROUP_ID" itype="String" val="" _ent="lso" valrequired="false" disabled="disabled">
									<option value=""></option>
										<%for(int i=0;i<reviewgroups.length();i++){ %>	<option typeid="<%=reviewgroups.getJSONObject(i).getInt("ID")%>"  value="<%=reviewgroups.getJSONObject(i).getInt("ID")%>"><%=reviewgroups.getJSONObject(i).getString("TEXT")%></option>		<%}%>
									</select>
								</td>
							</tr>
							
							
							
							<tr>
								<td class="csui_label" colnum="2" alert="">REVIEW STATUS<font color="red">*</font></td>
								<td class="csui" colnum="2" type="date" itype="date" alert="" colspan="3">
									<select   name="LKUP_REVIEW_STATUS_ID" id="LKUP_REVIEW_STATUS_ID" itype="String" val="" _ent="lso" valrequired="false" multiple="multiple">
									<option value=""></option>
										<%for(int i=0;i<reviewstatus.length();i++){ %>	<option value="<%=reviewstatus.getJSONObject(i).getInt("ID")%>"><%=reviewstatus.getJSONObject(i).getString("STATUS")%></option>		<%}%>
									</select>
								</td>
							</tr>
							
							
							<%} %>
							<tr>
								<td class="csui_label" colnum="2" alert="">STATUS CHANGED TO<font color="red">*</font></td>
								<td class="csui" colnum="2" type="date" itype="date" alert="" colspan="3">
									<select   name="STATUS_CHANGE_ID" id="STATUS_CHANGE_ID" itype="String" val="" _ent="lso" valrequired="false" >
									<option value=""></option>
									<option value="-9">Any Status</option>
										<%for(int i=0;i<status.length();i++){ %>	<option value="<%=status.getJSONObject(i).getInt("ID")%>"><%=status.getJSONObject(i).getString("TEXT")%></option>		<%}%>
									</select>
								</td>
							</tr>
							
							
							
							
							<tr>
								<td class="csui_label" colnum="2" alert="">NOT IN STATUS <input type="checkbox" name="selection" id="selection" ></td>
								<td class="csui" colnum="2" type="date" itype="date" alert="" colspan="3">
								
								
  								
									<select   name="NOT_IN_STATUS_ID" id="NOT_IN_STATUS_ID" itype="String" val="" _ent="lso" valrequired="false" multiple >
									<option value=""></option>
									
										<%for(int i=0;i<status.length();i++){ %>	<option value="<%=status.getJSONObject(i).getInt("ID")%>"><%=status.getJSONObject(i).getString("TEXT")%></option>		<%}%>
									</select>
								</td>
							</tr>
							
							
							
							<tr>
								<td class="csui_label" colnum="2" alert="">TEMPLATE<font color="red">*</font></td>
								<td class="csui" colnum="2" type="date" itype="date" alert="">
									<select   name="TEMPLATE_ID" id="TEMPLATE_ID" itype="String" val="" _ent="lso" valrequired="true">
									<option value="">Please Select</option>
										<%for(int i=0;i<templates.length();i++){ %>	<option value="<%=templates.getJSONObject(i).getInt("ID")%>"><%=templates.getJSONObject(i).getString("TEXT")%></option>		<%}%>
									</select>
								</td>
								
								
								<td class="csui_label" colnum="2" alert="">TEMPLATE AS ATTACHED PDF<font color="red">*</font></td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" ><div>
								<input name="EMAIL_PEOPLE_MANAGER" id="TEMPLATE_ATTACHMENT" type="checkbox"  itype="boolean" <%if(v.getString("TEMPLATE_ATTACHMENT").equals("Y")){ %>checked <% }%> ></div></td>
								
								
								
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">EMAIL ADDRESS </td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="3"><input name="EMAIL_ADDRESS" type="text" itype="text" value="<%=v.getString("EMAIL_ADDRESS") %>" valrequired="false" maxchar="10000">
								Multiple email split by comma
								</td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">EMAIL SUBJECT</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="3"><input width="100%" name="EMAIL_SUBJECT" type="text" itype="text" value="<%=v.getString("EMAIL_SUBJECT") %>" valrequired="true" maxchar="10000"></td>

							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">EMAIL BODY</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="3">
									use : Hello {user_name} for personalized msg</br>
									<textarea name="EMAIL_BODY" id="EMAIL_BODY"  rows="10" cols="50"><%=v.getString("EMAIL_BODY") %></textarea>
								</td>

							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">EMAIL PEOPLE MANAGER<font color="red">*</font></td>
								
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" ><div><input name="EMAIL_PEOPLE_MANAGER" id="EMAIL_PEOPLE_MANAGER" type="checkbox"  itype="boolean" <%if(v.getString("EMAIL_PEOPLE_MANAGER").equals("Y")){ %>checked <% }%> ></div></td>
								
								<td class="csui_label" colnum="2" alert="">EMAIL TEAM MANAGER</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" ><div><input name="EMAIL_TEAM_MANAGER" id="EMAIL_TEAM_MANAGER" type="checkbox"  itype="boolean" <%if(v.getString("EMAIL_TEAM_MANAGER").equals("Y")){ %>checked <% }%> ></div></td>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">EMAIL PEOPLE TYPES</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" >
								<select   name="EMAIL_PEOPLE_TYPE" id="EMAIL_PEOPLE_TYPE" itype="String" val="" _ent="lso" valrequired="false" multiple>
									<option value="0"></option>
										<%for(int i=0;i<types.length();i++){ %>	<option value="<%=types.getJSONObject(i).getInt("ID")%>"><%=types.getJSONObject(i).getString("TEXT")%></option>		<%}%>
									</select>
								
								</td>
								
								
								<td class="csui_label" colnum="2" alert="">EMAIL TEAM TYPES</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" >
								<select   name="EMAIL_TEAM_TYPE" id="EMAIL_TEAM_TYPE" itype="String" val="" _ent="lso" valrequired="false" multiple>
									<option value="0"></option>
										<%for(int i=0;i<teamtypes.length();i++){ %>	<option value="<%=teamtypes.getJSONObject(i).getInt("ID")%>"><%=teamtypes.getJSONObject(i).getString("TEXT")%></option>		<%}%>
									</select>
								
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

