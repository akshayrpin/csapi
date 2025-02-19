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
	String table = Table.REFFEEFORMULATABLE;
	String alert="";
	
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	int editId = map.getInt("ID");
	MapSet gv = new MapSet();
	gv=AdminAgent.getFeeGroupDetails(map.getString("FEE_ID","0"));
	String groupName = map.getString("_groupname","");
	String groupId = map.getString("_groupid","0");
	if(!Operator.hasValue(groupName)){
	groupName= gv.getString("GROUP_NAME");
	}
	if(!Operator.hasValue(groupId) || groupId=="0"){
	groupId= gv.getString("FEE_GROUP_ID");
	}
	if(!Operator.hasValue(groupName) && (!Operator.hasValue(groupId) || groupId=="0")){
		gv=AdminAgent.getFeeGroupDefaultDetails();
		groupName= gv.getString("GROUP_NAME");
		groupId= gv.getString("FEE_GROUP_ID");
	}
	ArrayList<HashMap<String,String>> a =  new ArrayList<HashMap<String,String>>();
	ArrayList<MapSet> la = AdminAgent.getIndex(type, typeid, id,false,map);

	MapSet l = new MapSet();
	if(la.size()>0){
		l = la.get(0);
	}
	String reffeegrouptable = Table.REFFEEGROUPTABLE;
	MapSet v = new MapSet();
	if(editId>0){
		v = AdminAgent.getType(AdminMap.getRefFeeId(editId));
		
	}
	
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 MapSet e = AdminMap.getRefFeeFormula(map);	
		 MapSet fg = AdminMap.getRefFeeGroup(map);	
		 if(editId>0){
			
			// System.out.println(e.getString("NEW_START_DATE"));
			
			 if(e.hasString("NEW_START_DATE")){
				 MapSet j = AdminMap.getCommon(map);
				 j.add("EXPIRATION_DATE", e.get("EXPIRATION_DATE"));
				 j.add("ID", map.getInt("ID"));
				 result = AdminAgent.updateType(j,table);
			//	 System.out.println(result+"initial update");
				 if(result){
					 e.remove("EXPIRATION_DATE");
					// e.add("EXPIRATION_DATE", "");
					 result = AdminAgent.saveType(e,table);
				 }
				 
			 }else{
			 	result = AdminAgent.updateType(e,table);
			 	result = AdminAgent.updateType(fg,table);
			 }
		 }else {
			 
			 result = AdminAgent.saveType(AdminMap.getRefFeeFormula(map),table);
			 if(result){
					 result = AdminAgent.saveType(fg,reffeegrouptable);
				 }
		 }
		 
		 if(result){
			// result = AdminAgent.saveType(AdminMap.getRefFeeFormula(map),Table.REFFEEGROUPTABLE);
		 }
		 
		 
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
	
	<style>
		
	</style>

	<script>
		$(document).ready(function() {
			
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
				
				$('#INPUT_TYPE1').val(<%=v.getString("INPUT_TYPE1")%>);
				$('#INPUT_TYPE1').trigger('chosen:updated');
				
				$('#INPUT_TYPE2').val(<%=v.getString("INPUT_TYPE2")%>);
				$('#INPUT_TYPE2').trigger('chosen:updated');
				
				$('#INPUT_TYPE3').val(<%=v.getString("INPUT_TYPE3")%>);
				$('#INPUT_TYPE3').trigger('chosen:updated');
				
				$('#INPUT_TYPE4').val(<%=v.getString("INPUT_TYPE4")%>);
				$('#INPUT_TYPE4').trigger('chosen:updated');
				
				$('#INPUT_TYPE5').val(<%=v.getString("INPUT_TYPE5")%>);
				$('#INPUT_TYPE5').trigger('chosen:updated');

				

				var d = new Date("<%=v.getString("START_DATE")%>");  
				var tomo = new Date();
				tomo.setDate(d.getDate()+1);

				$('input[itype=newdate]').datetimepicker({
					timepicker:false,
					format:'Y/m/d',
					minDate: tomo
				});
				
				
			<% }%>
			//$('#newstartdate').hide();
			
		});
		
		//function 
	
		function validatereq(){
			
			
			
			if($('#FEE_ID').val()==''){
				$("#feename").html("Fee Name is required");
				$("#feename").css('background','#FFFF00');
				return false;
			}
			
			if($('#LKUP_FORMULA_ID').val()==''){
				$("#feeformula").html("Formula is required");
				$("#feeformula").css('background','#FFFF00');
				return false;
			}
			
			if($('#START_DATE').val()==''){
				$("#start_dt").html("Start Date is required");
				$("#start_dt").css('background','#FFFF00');
				return false;
			}
			
			
			
			
			if($('#FINANCE_MAP_ID').val()==''){
				if($('#MANUAL_ACCOUNT').is(':checked')!=true){
					$("#feeaccount").html("Account is required");
					$("#feeaccount").css('background','#FFFF00');
				
					return false;
				}
			}
			
			
			
			<% if(editId>0){%>
				if($('#OLD_FINANCE_MAP_ID').val() != $('#FINANCE_MAP_ID').val() ||$('#OLD_LKUP_FORMULA_ID').val() != $('#LKUP_FORMULA_ID').val() || $('#OLD_INPUT1').val() != $('#INPUT1').val() || $('#OLD_INPUT2').val() != $('#INPUT2').val() || $('#OLD_INPUT3').val() != $('#INPUT3').val() || $('#OLD_INPUT4').val() != $('#INPUT4').val()	||	$('#OLD_INPUT5').val() != $('#INPUT5').val()){
					if($('#NEW_START_DATE').val()==''){
						//alert("New Start date is required.");
						$("#new_start_dt").html("New Start date is required.");
						$("#new_start_dt").css('background','#FFFF00');
						$('#NEW_START_DATE').focus();
						return false;
					}
				}
			
			<% }%>
			
			
			
			return true;
			
		}
		
		function showDt(){
			<% if(editId>0){%>
			$('#newstartdate').show();
			
			if($('#EXPIRATION_DATE').val()!=''){
				//var dateString = $('#EXPIRATION_DATE').val();

				var d = new Date($('#EXPIRATION_DATE').val());
				d.setDate(d.getDate() + 1);
				var c = d.toISOString().slice(0,10);
				c = c.replace("-","/");
				c = c.replace("-","/");
				$('#NEW_START_DATE').val(c);
			}
			<% }%>
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
						<td align="left" id="title">FEE </td>
						<td align="right" id="subtitle">	<%=Operator.subString(v.getString("START_DATE"), 0, 10) %>
						<%if(Operator.hasValue(v.getString("EXPIRATION_DATE"))){ %>
							to	<%=Operator.subString(v.getString("EXPIRATION_DATE"), 0, 10) %>
						<%}%>
						
						</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" action="add.jsp" method="post">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
					<input type="hidden" name="ID" value="<%=editId%>">
					<input type="hidden" name="_groupname" value="<%=groupName%>">
					<input type="hidden" name="_groupid" value="<%=groupId%>">
					
					<%if(editId>0){ %>
						<input name="OLD_INPUT1"  id="OLD_INPUT1" type="hidden" itype="decimal" value="<%=v.getString("INPUT1") %>" >
						<input name="OLD_INPUT2"  id="OLD_INPUT2"  type="hidden" itype="decimal" value="<%=v.getString("INPUT2") %>" >
						<input name="OLD_INPUT3"  id="OLD_INPUT3"  type="hidden" itype="decimal" value="<%=v.getString("INPUT3") %>" >
						<input name="OLD_INPUT4"  id="OLD_INPUT4"  type="hidden" itype="decimal" value="<%=v.getString("INPUT4") %>" >
						<input name="OLD_INPUT5"  id="OLD_INPUT5"  type="hidden" itype="decimal" value="<%=v.getString("INPUT5") %>" >
						
					<%}%>
					
					<input type="hidden" name="tabletype" value="single">
					
					<table class="csui_title">
						<tr>
							<td class="csui_title">FEE</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
							
							<tr>
								<td class="csui_label" colnum="2" alert="">GROUP </td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" colspan="4">
								<div>
								<%= groupName%>
								<input name="FEE_GROUP_ID" type="hidden" value="<%= groupId%>" >
								</div>
								</td>
							
							</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">NAME
								<%if(editId<=0){ %>
								<a target="lightbox-iframe" href="../names/list.jsp?_ent=permit&_id=7&view=connector" ><img src="/cs/images/icons/controls/black/add.png" border="0"></a>
								<%} %>
								</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
									<input id="FEE_ID" name="FEE_ID" type="hidden"  value="<%=v.getString("FEE_ID") %>" >
									<%if(editId>0){ %>
									<div ><%=v.getString("FEE_NAME") %></div>
									<%}else { %>
									<div id="feename">Select</div>
									<%} %>
								</td>
								
							</tr>
							
							
							<tr>
								<td class="csui_label" colnum="2" alert="">INPUTS</td>
								<td class="csui" colnum="2" type="String" itype="String" alert="" colspan="4">
									<table class="csui"  type="horizontal">
										<tr>
											<td class="csui_header" width="1%"> &nbsp;</td>
											<td class="csui_header"> EDITABLE</td>
											<td class="csui_header"> LABEL</td>
											<td class="csui_header"> DESCRIPTION</td>
											<td class="csui_header"> TYPE</td>
											<td class="csui_header"> VALUE</td>
										</tr>
										
										<tr>
											<td class="csui" width="1%" > input1</td>
											<td class="csui" width="1%">
												<input name="INPUT_EDITABLE1" type="checkbox"   <%if(v.getString("INPUT_EDITABLE1").equals("Y")){ %> checked <%} %>" valrequired="false">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
												<input name="INPUT_LABEL1" type="text" itype="int" value="<%=v.getString("INPUT_LABEL1") %>" valrequired="false"  maxlength="400">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
												<input name="INPUT_DESCRIPTION1" type="String" itype="text" value="<%=v.getString("INPUT_DESCRIPTION1") %>" valrequired="false" maxlength="150">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
											<select  name="INPUT_TYPE1" id="INPUT_TYPE1" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
											<%for(int i=0;i<types.length();i++){ %>	<option value="<%=types.getJSONObject(i).getInt("ID")%>"><%=types.getJSONObject(i).getString("TEXT")%></option>		<%}%>
											</select>
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert=""><input id="INPUT1" name="INPUT1" type="text" itype="decimal" value="<%=v.getString("INPUT1") %>" valrequired="false" maxchar="10000"></td>
											
										</tr>
										<tr>
											<td class="csui" width="1%" > input2</td>
											<td class="csui" width="1%">
												<input name="INPUT_EDITABLE2" type="checkbox"   <%if(v.getString("INPUT_EDITABLE2").equals("Y")){ %> checked <%} %>" valrequired="false">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
												<input name="INPUT_LABEL2" type="text" itype="int" value="<%=v.getString("INPUT_LABEL2") %>" valrequired="false"  maxlength="400">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
												<input name="INPUT_DESCRIPTION2" type="String" itype="text" value="<%=v.getString("INPUT_DESCRIPTION2") %>" valrequired="false" maxlength="150">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
											<select  name="INPUT_TYPE2" id="INPUT_TYPE2" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
											<%for(int i=0;i<types.length();i++){ %>	<option value="<%=types.getJSONObject(i).getInt("ID")%>"><%=types.getJSONObject(i).getString("TEXT")%></option>		<%}%>
											</select>
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert=""><input id="INPUT2" name="INPUT2" type="text" itype="decimal" value="<%=v.getString("INPUT2") %>" valrequired="false" maxchar="10000"></td>
											
										</tr>
										
										<tr>
											<td class="csui" width="1%" > input3</td>
											<td class="csui" width="1%">
												<input name="INPUT_EDITABLE3" type="checkbox"   <%if(v.getString("INPUT_EDITABLE3").equals("Y")){ %> checked <%} %>" valrequired="false">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
												<input name="INPUT_LABEL3" type="text" itype="int" value="<%=v.getString("INPUT_LABEL3") %>" valrequired="false"  maxlength="400">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
												<input name="INPUT_DESCRIPTION3" type="String" itype="text" value="<%=v.getString("INPUT_DESCRIPTION3") %>" valrequired="false" maxlength="150">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
											<select  name="INPUT_TYPE3" id="INPUT_TYPE3" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
											<%for(int i=0;i<types.length();i++){ %>	<option value="<%=types.getJSONObject(i).getInt("ID")%>"><%=types.getJSONObject(i).getString("TEXT")%></option>		<%}%>
											</select>
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert=""><input id="INPUT3" name="INPUT3" type="text" itype="decimal" value="<%=v.getString("INPUT3") %>" valrequired="false" maxchar="10000"></td>
											
										</tr>
										
										<tr>
											<td class="csui" width="1%" > input4</td>
											<td class="csui" width="1%">
												<input name="INPUT_EDITABLE4" type="checkbox"   <%if(v.getString("INPUT_EDITABLE4").equals("Y")){ %> checked <%} %>" valrequired="false">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
												<input name="INPUT_LABEL4" type="text" itype="int" value="<%=v.getString("INPUT_LABEL4") %>" valrequired="false"  maxlength="400">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
												<input name="INPUT_DESCRIPTION4" type="String" itype="text" value="<%=v.getString("INPUT_DESCRIPTION4") %>" valrequired="false" maxlength="150">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
											<select  name="INPUT_TYPE4" id="INPUT_TYPE4" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
											<%for(int i=0;i<types.length();i++){ %>	<option value="<%=types.getJSONObject(i).getInt("ID")%>"><%=types.getJSONObject(i).getString("TEXT")%></option>		<%}%>
											</select>
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert=""><input id="INPUT4" name="INPUT4" type="text" itype="decimal" value="<%=v.getString("INPUT4") %>" valrequired="false" maxchar="10000"></td>
											
										</tr>
										
										<tr>
											<td class="csui" width="1%" > input5</td>
											<td class="csui" width="1%">
												<input name="INPUT_EDITABLE5" type="checkbox"   <%if(v.getString("INPUT_EDITABLE5").equals("Y")){ %> checked <%} %>" valrequired="false">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
												<input name="INPUT_LABEL5" type="text" itype="int" value="<%=v.getString("INPUT_LABEL5") %>" valrequired="false"  maxlength="400">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
												<input name="INPUT_DESCRIPTION5" type="String" itype="text" value="<%=v.getString("INPUT_DESCRIPTION5") %>" valrequired="false" maxlength="150">
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert="">
											<select  name="INPUT_TYPE5" id="INPUT_TYPE5" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
											<%for(int i=0;i<types.length();i++){ %>	<option value="<%=types.getJSONObject(i).getInt("ID")%>"><%=types.getJSONObject(i).getString("TEXT")%></option>		<%}%>
											</select>
											</td>
											<td class="csui" colnum="2" type="String" itype="text" alert=""><input id="INPUT5" name="INPUT5" type="text" itype="decimal" value="<%=v.getString("INPUT5") %>" valrequired="false" maxchar="10000"></td>
										</tr>
									</table>
								</td>
								
							</tr>
							
							
							
							<tr>
								
								<td class="csui_label" colnum="2" alert="">FORMULA
								
								<a target="lightbox-iframe" href="../formula/list.jsp?_ent=permit&_id=8&_type=<%=type%>&_typeid=<%=typeid%>&view=connector" ><img src="/cs/images/icons/controls/black/add.png" border="0"></a>
								
								</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="">
									<input id="LKUP_FORMULA_ID" name="LKUP_FORMULA_ID" type="hidden"  value="<%=v.getString("LKUP_FORMULA_ID") %>" onchange="showDt();" >
									<input id="OLD_LKUP_FORMULA_ID" name="OLD_LKUP_FORMULA_ID" type="hidden"  value="<%=v.getString("LKUP_FORMULA_ID") %>" >
									
									<%if(editId>0){ %>
									<div id="feeformula"><%=v.getString("FORMULA_NAME") %></div>
									<a target="lightbox-iframe" href="../formula/add.jsp?_ent=permit&_id=8&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=v.getString("LKUP_FORMULA_ID")%>&process=edit" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>
									<%}else { %>
									<div id="feeformula">Select</div>
									<%} %>
								</td>
								
								<%if(editId>0){ %>
								
								<td class="csui_label" colnum="2" alert="">NEW START DATE</td>
								<td class="csui" colnum="2" type="date" itype="date" alert="" >
									<input name="NEW_START_DATE" id="NEW_START_DATE"  type="text" itype="newdate" value="" valrequired="true" maxchar="10000"><div id="new_start_dt"></div>
									<input name="START_DATE" id="START_DATE"  type="hidden" itype="date" value="<%=v.getString("START_DATE")%>" valrequired="true" maxchar="10000">
									
								<font bgcolor="#FFFF00">* NOTE changing formula input values / formula/ account  the new start date is required</font>
								</td>
								<%}else { %>
								<td class="csui_label" colnum="2" alert="">START DATE</td>
								<td class="csui" colnum="2" type="date" itype="date" alert="" > 
									<input name="START_DATE" id="START_DATE"  type="text" itype="date" value="" valrequired="true" maxchar="10000">
									<div id="start_dt"></div>
								</td>
								<%} %>
								
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">ACCOUNT
								
									<a target="lightbox-iframe" href="../map/list.jsp?_ent=permit&_id=27&_type=<%=type%>&_typeid=<%=typeid%>&view=connector" ><img src="/cs/images/icons/controls/black/add.png" border="0"></a>
								
								</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="">
									<input id="FINANCE_MAP_ID" name="FINANCE_MAP_ID" type="hidden"  value="<%=v.getString("FINANCE_MAP_ID") %>" >
									<input id="OLD_FINANCE_MAP_ID" name="OLD_FINANCE_MAP_ID" type="hidden"  value="<%=v.getString("FINANCE_MAP_ID") %>" >
									<%if(editId>0){ %>
									<div id="feeaccount">KEY CODE- <%=v.getString("KEY_CODE") %> </br> ORG CODE- <%=v.getString("BUDGET_UNIT") %> </br> OBJ CODE - <%=v.getString("ACCOUNT_NUMBER") %></div>
									<%}else { %>
									<div id="feeaccount">Select</div>
									<%} %>
								</td>
								<td class="csui_label" colnum="2" alert="">MANUAL ACCOUNT</td>
								<td class="csui" width="1%">
								
								<%if(editId>0 && Operator.hasValue(v.getString("ACCOUNT_NUMBER"))){ %>
									<input name="MANUAL_ACCOUNT" id="MANUAL_ACCOUNT" hidden="hidden"  value="N" valrequired="false">
								
								<%}else { %>
								<input name="MANUAL_ACCOUNT" id="MANUAL_ACCOUNT" type="checkbox"   <%if(v.getString("MANUAL_ACCOUNT").equals("Y")){ %> checked <%} %>" valrequired="false">
							
								<%} %>
								</td>
								
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">UPDATABLE</td>
								<td class="csui" width="1%">
							  	 <input name="UPDATABLE" id="UPDATABLE" type="checkbox"   <%if(v.getString("UPDATABLE").equals("Y")){ %> checked <%} %>" valrequired="false">
								</td>	
								<td class="csui_label" colnum="2" alert="">&nbsp;</td>
								<td class="csui" colnum="2" type="DATETIME" itype="String" alert="">&nbsp;</td>
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
			
			<%if(!Operator.hasValue(v.getString("EXPIRATION_DATE"))){ %>
			<div class="csui_buttons">
				<input type="submit" name="_action" value="cancel" class="csui_button">&nbsp;
				<input type="submit" name="_action" value="save" class="csui_button" onclick="return validatereq();">
			</div>
			<%} %>
				</form>
			</div>
		</div>
	</div>






</body>
</html>

