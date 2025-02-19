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
	String table = Table.REVIEWTABLE;
	String reftable = Table.REFVOIPREVIEW;
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
	int r = 0;
	if(editId>0){
		v = AdminAgent.getType(editId,table);
		r = AdminAgent.getRType(editId,reftable);
		
	}
	
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 MapSet mr = AdminMap.getReviews(map);
		 MapSet mvoip = AdminMap.getRefVoipReview(map);
		 mvoip.add("table", reftable);
		
		 mr.add("table", table);
		 
		 if(editId>0){
			 result = AdminAgent.updateType(mr,table);
		 }else {
			 result = AdminAgent.saveType(mr,table);
			
		 }
		// System.out.println(result+"initiial");
		 result = AdminAgent.saveRefVoipReview(mr,mvoip);
		 
	 }
	 if(map.equalsIgnoreCase("_action","CANCEL")){
			result = true;
	 }
	
	 JSONArray types = AdminAgent.getLookup("LKUP_REVIEW_TYPE");
	 JSONArray atypes = AdminAgent.getLookup("AVAILABILITY");
	 
	 JSONArray voipMenu = AdminAgent.getLookup("LKUP_VOIP_MENU");
	
	
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
				
				$('#LKUP_REVIEW_TYPE_ID').val(<%=v.getString("LKUP_REVIEW_TYPE_ID")%>);
				$('#LKUP_REVIEW_TYPE_ID').trigger('chosen:updated');
				
				$('#AVAILABILITY_ID').val(<%=v.getString("AVAILABILITY_ID")%>);
				$('#AVAILABILITY_ID').trigger('chosen:updated');

				
				$('#LKUP_VOIP_MENU_ID').val(<%=r%>);
				$('#LKUP_VOIP_MENU_ID').trigger('chosen:updated');
				//$('input:radio[name=online]:checked');
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
						<td align="left" id="title">REVIEW </td>
						<td align="right" id="subtitle">&nbsp;</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" class="form" action="add.jsp" ajax="no" method="post">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
					<input type="hidden" name="ID" value="<%=editId%>">
					<input type="hidden" name="_groupname" value="<%=groupName%>">
					<input type="hidden" name="_groupid" value="<%=groupId%>">
					
					<input type="hidden" name="tabletype" value="single">
					
					<table class="csui_title">
						<tr>
							<td class="csui_title">REVIEW</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
							
							<tr>
								<td class="csui_label" colnum="2" alert="">GROUP </td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" colspan="4">
								<div>
								<%= groupName%>
								<input name="REVIEW_GROUP_ID" type="hidden" value="<%= groupId%>" >
								</div>
								</td>
							
							</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">NAME</td>
								<td class="csui" colnum="2" type="String" itype="text" alert=""><input name="NAME" type="text" itype="text" value="<%=v.getString("NAME") %>" valrequired="true" maxchar="10000"></td>
								<td class="csui_label" colnum="2" alert="">DESCRIPTION</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" ><input name="DESCRIPTION" type="text" itype="text" value="<%=v.getString("DESCRIPTION") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">REVIEW TYPE</td>
								<td class="csui" colnum="2" type="String" itype="String" alert="" colspan="4">
								<select  name="LKUP_REVIEW_TYPE_ID" id="LKUP_REVIEW_TYPE_ID" itype="String" val="" _ent="lso" valrequired="true" ><option value=""></option>
									<%for(int i=0;i<types.length();i++){ %>	<option value="<%=types.getJSONObject(i).getInt("ID")%>"><%=types.getJSONObject(i).getString("TEXT")%></option>		<%}%>
								</select>
								</td>
								
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">DISPLAY TYPE</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input name="DISPLAY_TYPE" type="checkbox"  <%if(v.getString("DISPLAY_TYPE").equals("Y") || v.getString("DISPLAY_TYPE").equals("AUTO")){ %>checked <% }%>itype="boolean" ></div></td>
								<td class="csui_label" colnum="2" alert="">REQUIRED</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div><input name="REQUIRED" type="checkbox"  <%if(v.getString("REQUIRED").equals("Y")){ %>checked <% }%>itype="boolean" ></div></td>
							</tr>
							
							<tr>
								
								<td class="csui_label" colnum="2" alert="">ONLINE</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" colspan="4"><div><input name="ONLINE" type="checkbox"  <%if(v.getString("ONLINE").equals("Y")){ %>checked <% }%>itype="boolean" ></div></td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">DAYS TILL DUE</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" ><div><input name="DAYS_TILL_DUE" type="text" itype="text" value="<%=v.getString("DAYS_TILL_DUE") %>" valrequired="false" maxchar="10000"></div></td>
								
								<td class="csui_label" colnum="2" alert="">VOIP MENU</td>
								<td class="csui" colnum="2" type="String" itype="String" alert="" >
								<select  name="LKUP_VOIP_MENU_ID" id="LKUP_VOIP_MENU_ID" itype="String" val="" _ent="lso" valrequired="false" ><option value="-1"></option>
									<%for(int i=0;i<voipMenu.length();i++){ %>	<option value="<%=voipMenu.getJSONObject(i).getInt("ID")%>"><%=Operator.subString(voipMenu.getJSONObject(i).getString("TEXT"), 0, 20)%>...</option>		<%}%>
								</select>
							
							</tr>
							
							
							
							<tr>
								<td class="csui_label" colnum="2" alert="">AVAILABILITY</td>
								<td class="csui" colnum="2" type="String" itype="String" alert="" >
								<select  name="AVAILABILITY_ID" id="AVAILABILITY_ID" itype="String" val="" _ent="lso" valrequired="false" ><option value=""></option>
									<%for(int i=0;i<atypes.length();i++){ %>	<option value="<%=atypes.getJSONObject(i).getInt("ID")%>"><%=atypes.getJSONObject(i).getString("TEXT")%></option>		<%}%>
								</select>
								</td>
									<td class="csui_label" colnum="2" alert="">MAX ACTIVE APPOINTMENTS</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" ><input name="MAX_ACTIVE_APPOINTMENTS" type="text" itype="text" value="<%=v.getString("MAX_ACTIVE_APPOINTMENTS") %>" valrequired="false" maxchar="10000"></td>
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

