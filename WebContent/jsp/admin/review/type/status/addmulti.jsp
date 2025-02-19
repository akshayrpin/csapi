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
	String table = Table.LKUPREVIEWSTATUSTABLE;
	String alert="";
	
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	int editId = map.getInt("ID");
	
	
	
	String reviewTypeName = map.getString("_reviewtypename","");
	String reviewTypeId = map.getString("_reviewtypeid","0");
	
	ArrayList<HashMap<String,String>> a =  new ArrayList<HashMap<String,String>>();
	ArrayList<MapSet> la = AdminAgent.getIndex(type, typeid, id,false,map);

	String ids = map.getString("IDS");
	
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
		     MapSet ms = AdminMap.getReviewStatusMulti(map);
		     result = AdminAgent.updateMultipleType(ms,table);
	 }
	 if(map.equalsIgnoreCase("_action","CANCEL")){
			result = true;
	 }
	
	 JSONArray types = AdminAgent.getLookup("LKUP_REVIEW_TYPE");
	 JSONArray libs = AdminAgent.getLookup("LIBRARY_GROUP");
	 

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
				
				//$('input:radio[name=online]:checked');
				
				$('#LKUP_REVIEW_TYPE_ID').val(<%=v.getString("LKUP_REVIEW_TYPE_ID")%>);
				$('#LKUP_REVIEW_TYPE_ID').trigger('chosen:updated');
				
				$('#LIBRARY_GROUP_ID').val(<%=v.getString("LIBRARY_GROUP_ID")%>);
				$('#LIBRARY_GROUP_ID').trigger('chosen:updated');
				
				
				
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
						<td align="left" id="title">REVIEW STATUS</td>
						<td align="right" id="subtitle">&nbsp;</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" action="addmulti.jsp" method="post">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
					<input type="hidden" name="ID" value="<%=editId%>">
					<input type="hidden" name="IDS" value="<%=ids%>">
					<input type="hidden" name="tabletype" value="single">
					<input type="hidden" name="_reviewtypename" value="<%=reviewTypeName%>">
					<input type="hidden" name="_reviewTypeId" value="<%=reviewTypeId%>">
					
					<table class="csui_title">
						<tr>
							<td class="csui_title">REVIEW STATUS</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
							<tr>
								<td class="csui_label" colnum="2" alert="">LIBRARY GROUP</td>
								<td class="csui" colnum="2" type="String" itype="String" alert="" colspan="4">
								<select  name="LIBRARY_GROUP_ID" id="LIBRARY_GROUP_ID" itype="String" val="" _ent="lso" valrequired="false" ><option value=""></option>
									<%for(int i=0;i<libs.length();i++){ %>	<option value="<%=libs.getJSONObject(i).getInt("ID")%>"><%=libs.getJSONObject(i).getString("TEXT")%></option>		<%}%>
								</select>
								</td>
								
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">APPROVED</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<select  name="APPROVED" id="APPROVED" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
								</select>
								</td>
								
								<td class="csui_label" colnum="2" alert="">UNAPPROVED</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<select  name="UNAPPROVED" id="UNAPPROVED" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
								</select>
								</td>
							</tr>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">ASSIGN</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<select  name="ASSIGN" id="ASSIGN" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
								</select>
								</td>
								
								<td class="csui_label" colnum="2" alert="">FINAL</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<select  name="FINAL" id="FINAL" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
								</select>
								</td>
							</tr>
							
							
							<tr>
								<td class="csui_label" colnum="2" alert="">SCHEDULE</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<select  name="SCHEDULE" id="SCHEDULE" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
								</select>
								</td>
								
								<td class="csui_label" colnum="2" alert="">SCHEDULE CANCEL</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<select  name="SCHEDULE_CANCEL" id="SCHEDULE_CANCEL" itype="String" val="" _ent="lso" valrequired="true" >
										<option value="">Please Select</option>
										<option value="Y">Yes</option>
										<option value="N">No</option>
								</select>
								</td>
							</tr>
							
							<tr>
							
							<td class="csui_label" colnum="2" alert="">DAYS TILL DUE</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<input  name="DAYS_TILL_DUE" id="DAYS_TILL_DUE" itype="String" val="" _ent="lso"  >
										
								</td>
								
								<td class="csui_label" colnum="2" alert="">ATTACHMENT</td>
								<td class="csui" colnum="2" type="boolean" itype="boolean" alert="" >
								<select  name="ATTACHMENT" id="ATTACHMENT" itype="String" val="" _ent="lso" valrequired="true" >
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

