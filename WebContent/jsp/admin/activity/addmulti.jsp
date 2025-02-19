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
	String table = Table.LKUPACTTYPETABLE;
	String alert="";
	
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	int editId = map.getInt("ID");
	
	String ids = map.getString("IDS");
	
	ArrayList<HashMap<String,String>> a =  new ArrayList<HashMap<String,String>>();
	ArrayList<MapSet> la = AdminAgent.getIndex(type, typeid, id,false,map);

	MapSet l = new MapSet();
	if(la.size()>0){
		l = la.get(0);
	}
	
	MapSet v = new MapSet();
	
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 MapSet ms = AdminMap.getActivityTypeMulti(map);
		 result = AdminAgent.updateMultipleType(ms,table);
	 }
	 
	 if(map.equalsIgnoreCase("_action","CANCEL")){
			result = true;
	 }
	 JSONArray depts = AdminAgent.getLookup("DEPARTMENT");
	 JSONArray atypes = AdminAgent.getLookup("AVAILABILITY");
	 JSONArray patterns = AdminAgent.getLookup("LKUP_PATTERN");
	 JSONArray configrations = AdminAgent.getLookup("LKUP_CONFIGURATION");


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
				
			//	$('input:radio[name=online]:checked');
				
			//	$('#DEPARTMENT_ID').val(<%=v.getString("DEPARTMENT_ID")%>);
			//	$('#DEPARTMENT_ID').trigger('chosen:updated');
				
				
			//	$('#AVAILABILITY_ID').val(<%=v.getString("AVAILABILITY_ID")%>);
			//	$('#AVAILABILITY_ID').trigger('chosen:updated');
			<% }%>
			
		});
		
		//function 
	
		
		
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
						<td align="left" id="title">ACTIVITY TYPE</td>
						<td align="right" id="subtitle">&nbsp;</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" action="addmulti.jsp" method="post">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
					<input type="hidden" name="IDS" value="<%=ids%>">
					<input type="hidden" name="tabletype" value="single">
					
					<table class="csui_title">
						<tr>
							<td class="csui_title">ACTIVITY TYPE</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
							
							<tr>
								<td class="csui_label" colnum="2" alert="">PATTERN</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="">
								<select  name="LKUP_PATTERN_ID" id="LKUP_PATTERN_ID" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
									<%for(int i=0;i<patterns.length();i++){ %>	<option value="<%=patterns.getJSONObject(i).getInt("ID")%>"><%=patterns.getJSONObject(i).getString("TEXT")%></option>		<%}%>
								</select></td>
								<td class="csui_label" colnum="2" alert="">ONLINE</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="">
									<select  name="ONLINE" id="ONLINE" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
									</select>
								</td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">DEPARTMENT</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
								
								<select  name="DEPARTMENT_ID" id=""DEPARTMENT_ID"" itype="String" val="" _ent="lso" valrequired="true"><option value=""></option>
									<%for(int i=0;i<depts.length();i++){ %>	<option value="<%=depts.getJSONObject(i).getInt("ID")%>"><%=depts.getJSONObject(i).getString("TEXT")%></option>		<%}%>
								</select>
								
								</td>
								
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">AVAILABILITY</td>
								<td class="csui" colnum="2" type="String" itype="String" alert="" >
								<select  name="AVAILABILITY_ID" id="AVAILABILITY_ID" itype="String" val="" _ent="lso" valrequired="true" ><option value=""></option>
									<%for(int i=0;i<atypes.length();i++){ %>	<option value="<%=atypes.getJSONObject(i).getInt("ID")%>"><%=atypes.getJSONObject(i).getString("TEXT")%></option>		<%}%>
								</select>
								</td><td class="csui_label" colnum="2" alert="">MAX ACTIVE APPOINTMENTS</td>
								<td class="csui" colnum="2" type="DATE" itype="DATE" alert=""><input name="MAX_ACTIVE_APPOINTMENTS" type="text" itype="String" value="<%=v.getString("MAX_ACTIVE_APPOINTMENTS") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">CONFIGURATION GROUP</td>
								<td class="csui" colnum="2" type="String" itype="String" alert="" >
								<select  name="CONFIGURATION_GROUP_ID" id="CONFIGURATION_GROUP_ID" itype="String" val="" _ent="lso" valrequired="true"><option value=""></option>
									<%for(int i=0;i<configrations.length();i++){ %>	<option value="<%=configrations.getJSONObject(i).getInt("ID")%>"><%=configrations.getJSONObject(i).getString("TEXT")%></option>		<%}%>
								</select>
								</td>
								<td class="csui_label" colnum="2" alert="">MAX ALLOWED</td>
								<td class="csui" colnum="2" type="String" itype="Integer" alert=""><input name="MAX_ALLOWED" type="text" itype="String" value="<%=v.getString("MAX_ALLOWED") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							
							
							<tr>
								<td class="csui_label" colnum="2" alert="">MONTH START</td>
								<td class="csui" colnum="2" type="String" itype="Integer" alert=""><input name="MONTH_START" type="text" itype="String" value="<%=v.getString("MONTH_START") %>" valrequired="true" maxchar="10000"></td>
								<td class="csui_label" colnum="2" alert="">DAY START</td>
								<td class="csui" colnum="2" type="String" itype="Integer" alert=""><input name="DAY_START" type="text" itype="String" value="<%=v.getString("DAY_START") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">YEARS TILL EXPIRED</td>
								<td class="csui" colnum="2" type="String" itype="Integer" alert=""><input name="YEARS_TILL_EXPIRED" type="text" itype="String" value="<%=v.getString("YEARS_TILL_EXPIRED") %>" valrequired="true" maxchar="10000"></td>
								<td class="csui_label" colnum="2" alert="">DAYS TILL EXPIRED</td>
								<td class="csui" colnum="2" type="String" itype="Integer" alert=""><input name="DAYS_TILL_EXPIRED" type="text" itype="String" value="<%=v.getString("DAYS_TILL_EXPIRED") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							
							<tr>
								
								<td class="csui_label" colnum="2" alert="">DAYS TILL APPLICATION EXPIRED</td>
								<td class="csui" colnum="2" type="String" itype="Integer" alert="" ><input name="DAYS_TILL_APPLICATION_EXPIRED" type="text" itype="String" value="<%=v.getString("DAYS_TILL_APPLICATION_EXPIRED") %>" valrequired="true" maxchar="10000"></td>
								
								<td class="csui_label" colnum="2" alert="">AUTO ISSUE</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="">
									<select  name="AUTO_ISSUE" id="AUTO_ISSUE" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
									</select>
								</td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">ISDOT</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="">
									<select  name="ISDOT" id="ISDOT" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
									</select>
								</td>

								<td class="csui_label" colnum="2" alert="">DOT EXEMPTION</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="">
									<select  name="DOT_EXEMPTION" id="DOT_EXEMPTION" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
									</select>
								</td>
							</tr>
							
							
							<tr>
								<td class="csui_label" colnum="2" alert="">FINANCE LOCK</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="">
									<select  name="FINANCE_LOCK" id="FINANCE_LOCK" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
									</select>
								</td>
								<td class="csui_label" colnum="2" alert="">ISTEMP</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="">
									<select  name="ISTEMP" id="ISTEMP" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
									</select>
								</td>
							</tr>
							
								<tr>
								<td class="csui_label" colnum="2" alert="">DURATION MAX</td>
								<td class="csui" colnum="2" type="String" itype="Integer" alert=""><input name="DURATION_MAX" type="text" itype="String" value="<%=v.getString("DURATION_MAX") %>" valrequired="true" maxchar="10000"></td>

								<td class="csui_label" colnum="2" alert="">DURATION MAX DAYS</td>
								<td class="csui" colnum="2" type="String" itype="Integer" alert=""><input name="DURATION_MAX_DAYS" type="text" itype="String" value="<%=v.getString("DURATION_MAX_DAYS") %>" valrequired="true" maxchar="10000"></td>
								
								
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">DURATION MAX MONTHS</td>
								<td class="csui" colnum="2" type="String" itype="Integer" alert=""><input name="DURATION_MAX_MONTHS" type="text" itype="String" value="<%=v.getString("DURATION_MAX_MONTHS") %>" valrequired="true" maxchar="10000"></td>
								
								<td class="csui_label" colnum="2" alert="">DURATION MAX YEARS</td>
								<td class="csui" colnum="2" type="String" itype="Integer" alert=""><input name="DURATION_MAX_YEARS" type="text" itype="String" value="<%=v.getString("DURATION_MAX_YEARS") %>" valrequired="true" maxchar="10000"></td>
								
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">TEMPLATE</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="">
									<select  name="TEMPLATE" id="TEMPLATE" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
									</select>
								</td>
								<td class="csui_label" colnum="2" alert="">RENEWAL</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="">
									<select  name="RENEWAL" id="RENEWAL" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
									</select>
								</td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">ENABLE TIME</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="">
									<select  name="ENABLE_TIME" id="ENABLE_TIME" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
									</select>
								</td>
								<td class="csui_label" colnum="2" alert="">INHERIT</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="">
									<select  name="INHERIT" id="INHERIT" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
									</select>
								</td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">ISPUBLIC</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="">
									<select  name="ISPUBLIC" id="ISPUBLIC" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
									</select>
								</td>
			
								<td class="csui_label" colnum="2" alert="">DOT STICKERS</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="">
									<select  name="DOT_STICKERS" id="DOT_STICKERS" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
									</select>
								</td>
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

