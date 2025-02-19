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
	
	
	ArrayList<MapSet> divisionfields = new ArrayList<MapSet>();
	divisionfields = AdminAgent.getColumns("SELECT *  FROM LKUP_DIVISIONS_TYPE	WHERE ACTIVE = 'Y'");
	
	ArrayList<MapSet> sitesdfields = new ArrayList<MapSet>();
	sitesdfields = AdminAgent.getColumns("SELECT L.ID AS LKUP_LSO_ID,F.ID, F.NAME  FROM LKUP_LSO_TYPE L JOIN FIELD F on L.SITEDATA_FIELD_GROUP_ID = F.FIELD_GROUPS_ID WHERE  L.ACTIVE = 'Y'");
	
	ArrayList<MapSet> setbackfields = new ArrayList<MapSet>();
	setbackfields = AdminAgent.getColumns("SELECT ID,TYPE  FROM LKUP_LSO_SETBACK_TYPE WHERE ACTIVE = 'Y'");
	
	ArrayList<MapSet> pcfields = new ArrayList<MapSet>();
	pcfields = AdminAgent.getColumns("select DISTINCT FG.* from FIELD_GROUPS FG where FG.MULTI='N' AND FG.ACTIVE='Y'");
	
	ArrayList<MapSet> rfields = new ArrayList<MapSet>();
	rfields = AdminAgent.getColumns("select FG.* from REVIEW_GROUP FG WHERE ACTIVE='Y' ");
	
	ArrayList<MapSet> ufields = new ArrayList<MapSet>();
	ufields = AdminAgent.getColumns("select FG.ID, FG.TYPE from LKUP_USERS_TYPE FG WHERE ACTIVE='Y' ");
	
	ArrayList<MapSet> utfields = new ArrayList<MapSet>();
	utfields = AdminAgent.getColumns("select FG.ID, FG.TYPE from LKUP_TEAM_TYPE FG WHERE ACTIVE='Y' ");
	
	
	ArrayList<MapSet> clfields = new ArrayList<MapSet>();
	clfields = AdminAgent.getColumns("select DISTINCT FG.* from FIELD_GROUPS FG where FG.MULTI='Y' AND FG.ACTIVE='Y'");
	
//	ArrayList<MapSet> arfields = new ArrayList<MapSet>();
	//arfields = AdminAgent.getColumns("select DISTINCT FG.* from REVIEW_GROUP FG JOIN REF_ACT_REVIEW_GROUP RFG on FG.ID= RFG.REVIEW_GROUP_ID ");
	
	String url = Config.fullcontexturl();
	
	ArrayList<MapSet> notetypefields = new ArrayList<MapSet>();
	notetypefields = AdminAgent.getColumns("SELECT L.ID ,L.TYPE  FROM LKUP_NOTES_TYPE L WHERE  L.ACTIVE = 'Y'");
	
	ArrayList<MapSet> fgfields = new ArrayList<MapSet>();
	fgfields = AdminAgent.getColumns("select FG.ID, FG.GROUP_NAME from FEE_GROUP FG ");
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
			        },
			        rememberState: true   
			
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
			
		
			$(".fieldselect").change(function(){
				
				var v = $(this).val();
				 var c = "{"+v+"}";
				    //alert(c);
				    suc(c);
			});
			
			$('#LISTS').change(function(){
				$('.lists').hide(); 
				var val = $(this).val();
				 $('#'+val).show();
			});
			
			$('#LISTS_SUMMARY').change(function(){
				$('.listssummary').hide(); 
				var val = $(this).val();
				 $('#'+val).show();
			});
			
			 
			
			 
			 
			
			 
			 $("#CUSTOM_SINGLE").change(function(){
				 var grpid = $(this).val();
				 $('#CUSTOM_SINGLE_VALUE').empty();
				 if(grpid>0){
				 	var method = 'showselector';
				 	$('#CUSTOM_SINGLE_VALUE').empty();
					 $.ajax({
			   			  type: "POST",
			   			  url: "action.jsp?_action="+method,
			   			  dataType: 'json',		  
			   			  data: { 
			   				   ID : grpid
			   			      //mode : mode
			   			    },
			   			    success: function(output) {
			   			     	var h = $('<option value="">Please Select </option>');
		   			    		$('#CUSTOM_SINGLE_VALUE').append(h);
			   			    	$.each(output, function(k,v) {
			   		            	 var c = $('<option value="custom_'+v.FIELD_GROUPS_ID+'_'+v.ID+'">'+v.NAME+'</option>');
			   			    		$('#CUSTOM_SINGLE_VALUE').append(c);
			   		            });
			   			    	$('#CUSTOM_SINGLE_VALUE').trigger('chosen:updated');		
			   			    },
			   		    error: function(data) {
			   		    	swal("Problem while perfoming the operation ");
			   		    }
		   			});		
				 }
			
			 });
			 
			 
			 
			 $("#CUSTOM_LIST").change(function(){
				 var grpid = $(this).val();
				 var c = "<ul>";
				 $('#CUSTOM_LIST_VALUES').html("");
				 if(grpid>0){
				 	var method = 'showselector';
				 	 $('#CUSTOM_LIST_VALUES').html("");
					 $.ajax({
			   			  type: "POST",
			   			  url: "action.jsp?_action="+method,
			   			  dataType: 'json',		  
			   			  data: { 
			   				   ID : grpid
			   			      //mode : mode
			   			    },
			   			    success: function(output) {
			   			    	$.each(output, function(k,v) {
			   		            	   c += '<li>['+v.ID+'] - '+v.NAME+'</li>';
			   			    		
			   		            });
			   			   
							 c +="<li>rowconfig <b>list_custom FIELD_GROUPS_ID:"+grpid+";</b></li></ul>"
							 $('#CUSTOM_LIST_VALUES').html(c);
			   			    },
			   		    error: function(data) {
			   		    	swal("Problem while perfoming the operation ");
			   		    }
		   			});		
					
				 }
			
			 });
			 
			 
			
			 
			
		});
		
		

		
	function save(){
	 	
		var  v =	$('select').find('.fieldselect :selected').val();
		alert($(".fieldselect").attr("name"));
	 	
	
	//	var h =  $(height).val();
	    alert(v);
	    var c = "{"+v+"}";
	    //alert(c);
	    suc(c)
	 
	}

	function suc(o){
		var CKEDITOR   = window.parent.CKEDITOR;
		var oEditor   = CKEDITOR.instances.editorName;
		var c = o;
		var myEditor = CKEDITOR.instances.editor1;
		myEditor.insertHtml(c);
		CKEDITOR.dialog.getCurrent().hide();
	}	
		



	</script>

</head>
<body alert="">
	<form id="csform" action="fields.jsp" method="post">
		<div id="tabbed-nav">
			<ul>
				<li><a>LSO</a></li>
				<li><a>PROJECT</a></li>
				<li><a>ACTIVITY</a></li>
				<li><a>GENERIC</a></li>
				<li><a>LIST</a></li>
				<li><a>SUMMARY</a></li>
	       </ul>
					  
		<div>

			<!-- LSO -->
			<div>
				<table cellpadding="4" cellspacing="4" border="0">
				 	<tr>
						<td class="csui_label" colnum="2" alert="">LSO</td>
						<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
						<select  name="lso" id="lso" itype="String" val="" _ent="lso" class="fieldselect"  valrequired="false"><option value=""></option>
							<option value="lso_address">lso_address</option>
							<option value="lso_apn">lso_apn</option>
							<option value="lso_city">lso_city</option>
							<option value="lso_state">lso_state</option>
							<option value="lso_zip">lso_zip</option>
							</select>
						</td>
					</tr>
					
					
					<tr>
						<td class="csui_label" colnum="2" alert="">DIVISIONS</td>
						<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
						<select  name="divisions" id="divisions" class="fieldselect" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
						<%for(MapSet r: divisionfields){  %>	<option  value="division_<%=r.getString("ID") %>">division_<%=r.getString("TYPE") %></option>		<%}%>
						<%for(MapSet r: divisionfields){  %>	<option  value="division_<%=r.getString("ID")%>_description">division_<%=r.getString("TYPE") %>_description</option>		<%}%>
						<%for(MapSet r: divisionfields){  %>	<option  value="division_<%=r.getString("ID")%>_info">division_<%=r.getString("TYPE") %>_division_info</option>		<%}%>
							</select>
						</td>
					</tr>
					
					<tr>
						<td class="csui_label" colnum="2" alert="">LAND SITE & SET BACK DATA</td>
						<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
						<select  name="lsd" id="lsd" class="fieldselect" ><option value=""></option>
						<%for(MapSet r: sitesdfields){  if(r.getInt("LKUP_LSO_ID")==1) {%>	<option  value="land_sd_<%=r.getString("ID") %>">land_sd_<%=r.getString("NAME") %></option>		<% } }%>
						<%for(MapSet r: setbackfields){ %>	
							<option  value="land_sd_setback_<%=r.getString("ID") %>_ft">land_sd_setback_<%=r.getString("TYPE") %>_ft</option>
							<option  value="land_sd_setback_<%=r.getString("ID") %>_inches">land_sd_setback_<%=r.getString("TYPE") %>_inches</option>
							<option  value="land_sd_setback_<%=r.getString("ID") %>_comments">land_sd_setback_<%=r.getString("TYPE") %>_comments</option>
							<option  value="land_sd_setback_<%=r.getString("ID") %>_req_ft">land_sd_setback_<%=r.getString("TYPE") %>_req_ft</option>
							<option  value="land_sd_setback_<%=r.getString("ID") %>_req_inches">land_sd_setback_<%=r.getString("TYPE") %>_req_inches</option>
							<option  value="land_sd_setback_<%=r.getString("ID") %>_req_comments">land_sd_setback_<%=r.getString("TYPE") %>_req_comments</option>		
						<%}%>
						</select>
						</td>
					</tr>
					
					<tr>
						<td class="csui_label" colnum="2" alert="">STRUCTURE SITE & SET BACK DATA</td>
						<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
						<select  name="ssd" id="ssd" class="fieldselect" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
						<%for(MapSet r: sitesdfields){  if(r.getInt("LKUP_LSO_ID")==2) {%>	<option  value="structure_sd_<%=r.getString("ID") %>">structure_sd_<%=r.getString("NAME") %></option>		<%} }%>
						
						<%for(MapSet r: setbackfields){ %>	
							<option  value="structure_sd_setback_<%=r.getString("ID") %>_ft">structure_sd_setback_<%=r.getString("TYPE") %>_ft</option>
							<option  value="structure_sd_setback_<%=r.getString("ID") %>_inches">structure_sd_setback_<%=r.getString("TYPE") %>_inches</option>
							<option  value="structure_sd_setback_<%=r.getString("ID") %>_comments">structure_sd_setback_<%=r.getString("TYPE") %>_comments</option>
							<option  value="structure_sd_setback_<%=r.getString("ID") %>_req_ft">structure_sd_setback_<%=r.getString("TYPE") %>_req_ft</option>
							<option  value="structure_sd_setback_<%=r.getString("ID") %>_req_inches">structure_sd_setback_<%=r.getString("TYPE") %>_req_inches</option>
							<option  value="structure_sd_setback_<%=r.getString("ID") %>_req_comments">structure_sd_setback_<%=r.getString("TYPE") %>_req_comments</option>		
						<%}%>
							</select>
						</td>
					</tr>
					
					<tr>
						<td class="csui_label" colnum="2" alert="">OCCUPANCY SITE & SET BACK DATA</td>
						<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
						<select  name="osd" id="osd" class="fieldselect" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
					<%for(MapSet r: sitesdfields){  if(r.getInt("LKUP_LSO_ID")==3) { %>	<option  value="occupancy_sd_<%=r.getString("ID") %>">occupancy_sd_<%=r.getString("NAME") %></option>		<%} }%>
						<%for(MapSet r: setbackfields){ %>	
							<option  value="occupancy_sd_setback_<%=r.getString("ID") %>_ft">occupancy_sd_setback_<%=r.getString("TYPE") %>_ft</option>
							<option  value="occupancy_sd_setback_<%=r.getString("ID") %>_inches">occupancy_sd_setback_<%=r.getString("TYPE") %>_inches</option>
							<option  value="occupancy_sd_setback_<%=r.getString("ID") %>_comments">occupancy_sd_setback_<%=r.getString("TYPE") %>_comments</option>
							<option  value="occupancy_sd_setback_<%=r.getString("ID") %>_req_ft">occupancy_sd_setback_<%=r.getString("TYPE") %>_req_ft</option>
							<option  value="occupancy_sd_setback_<%=r.getString("ID") %>_req_inches">occupancy_sd_setback_<%=r.getString("TYPE") %>_req_inches</option>
							<option  value="occupancy_sd_setback_<%=r.getString("ID") %>_req_comments">occupancy_sd_setback_<%=r.getString("TYPE") %>_req_comments</option>		
						<%}%>
							</select>
						</td>
					</tr>
					
				</table>
			</div>
						
						
			<!-- PROJECT -->
			<div>
				<table cellpadding="4" cellspacing="4" border="0">
				 	<tr>
						<td class="csui_label" colnum="2" alert="">PROJECT</td>
						<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
						<select  name="PROJECT" id="PROJECT" itype="String" val="" _ent="lso" class="fieldselect"  valrequired="false"><option value=""></option>
							<option value="project_nbr">project_nbr</option>
							<option value="project_id">project_id</option>
							<option value="project_name">project_name</option>
							<option value="project_description">project_description</option>
							<option value="project_cip_acctno">project_cip_acctno</option>
							<option value="project_type">project_type</option>
							<option value="project_status">project_status</option>
							<option value="project_start_dt">project_start_dt</option>
							<option value="project_completion_dt">project_completion_dt</option>
							<option value="project_applied_dt">project_applied_dt</option>
							<option value="project_expired_dt">project_expired_dt</option>
							<option value="project_created_by">project_created_by</option>
							<option value="project_updated_by">project_updated_by</option>
							<option value="project_created_date">project_created_date</option>
							<option value="project_updated_date">project_updated_date</option>		
						
						</select></td>
						
					</tr>
				
				
					
			
				
					
				
				
			</table>
			
			</div>
						
						
						
		<!-- ACTIVITY -->
	
			<div>
			<table>
				
				 	<tr>
						<td class="csui_label" colnum="2" alert="">ACTIVITY </td>
						<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
						<select  name="ACTIVITY" id="ACTIVITY" itype="String" val="" _ent="lso" class="fieldselect"  valrequired="false"><option value=""></option>
							<option value="activity_id">activity_id</option>
							<option value="activity_act_nbr">activity_act_nbr</option>
							<option value="activity_type">activity_type</option>
							<option value="activity_status">activity_status</option>
							<option value="activity_description">activity_description</option>
							<option value="activity_department">activity_department</option>
							
							
							
							<option value="activity_valuation_calculated">activity_valuation_calculated</option>
							<option value="activity_valuation_declared">activity_valuation_declared</option>
							<option value="activity_start_date">activity_start_date</option>
							<option value="activity_applied_date">activity_applied_date</option>
							<option value="activity_issued_date">activity_issued_date</option>
							<option value="activity_exp_date">activity_exp_date</option>
							<option value="activity_application_exp_date">activity_application_exp_date</option>
							<option value="activity_final_date">activity_final_date</option>
							
							
							
							<option value="activity_created_by">activity_created_by</option>					
							<option value="activity_updated_by">activity_updated_by</option>	
							<option value="activity_created_date">activity_created_date</option>
							<option value="activity_created_date_time">activity_created_date_time</option>
							<option value="activity_updated_date">activity_updated_date</option>	
							<option value="activity_updated_date_time">activity_updated_date_time</option>					
						
						</select></td>
						
					</tr>
				
					
					
					
					
					
					
					
				</table>
				
			</div>
			
			<!-- GENERIC -->
			<div>
				<table cellpadding="4" cellspacing="4" border="0">
				 	<tr>
						<td class="csui_label" colnum="2" alert="">GENERIC</td>
						<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
						<select  name="GENERIC" id="GENERIC" itype="String" val="" _ent="lso" class="fieldselect"  valrequired="false"><option value=""></option>
							<option value="special_current_date">current_date</option>
							<option value="dot_stickers">dot_stickers- Used to print stickers</option>
							<option value="dot_reprint_stickers">dot_reprint_stickers- Used to re-print stickers</option>
							
						</select></td>
						
					</tr>
					
					<tr>
						<td class="csui_label" colnum="2" alert="">PEOPLE</td>
						<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
						<select  name="pp" id="pp" class="fieldselect" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
						
						<%for(MapSet r: ufields){ %>	
							<option  value="people_<%=r.getString("ID") %>_name">people_<%=r.getString("TYPE") %>_name</option>
							<option  value="people_<%=r.getString("ID") %>_last_name">people_<%=r.getString("TYPE") %>_last_name</option>
							<option  value="people_<%=r.getString("ID") %>_first_name">people_<%=r.getString("TYPE") %>_first_name</option>
							<option  value="people_<%=r.getString("ID") %>_address">people_<%=r.getString("TYPE") %>_address</option>
							<option  value="people_<%=r.getString("ID") %>_email">people_<%=r.getString("TYPE") %>_email</option>
							<option  value="people_<%=r.getString("ID") %>_city">people_<%=r.getString("TYPE") %>_city</option>
							<option  value="people_<%=r.getString("ID") %>_state">people_<%=r.getString("TYPE") %>_state</option>
							<option  value="people_<%=r.getString("ID") %>_zip">people_<%=r.getString("TYPE") %>_zip</option>
							<option  value="people_<%=r.getString("ID") %>_phone_home">people_<%=r.getString("TYPE") %>_phone_home</option>
							<option  value="people_<%=r.getString("ID") %>_phone_work">people_<%=r.getString("TYPE") %>_phone_work</option>
							<option  value="people_<%=r.getString("ID") %>_phone_cell">people_<%=r.getString("TYPE") %>_phone_cell</option>
							<option  value="people_<%=r.getString("ID") %>_lic_num">people_<%=r.getString("TYPE") %>_lic_num</option>
							<option  value="people_<%=r.getString("ID") %>_lic_expiration_date">people_<%=r.getString("TYPE") %>_lic_expiration_date</option>
							
		
							
							
									
						<%}%>
							</select>
						</td>
					</tr>
			
				
					<tr>
						<td class="csui_label" colnum="2" alert="">CUSTOM GROUPS </td>
						<td class="csui" colnum="2" type="String" itype="text" alert="">
						<select  name="CUSTOM_SINGLE" id="CUSTOM_SINGLE" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
							<%for(int i=0;i<pcfields.size();i++){ MapSet r = pcfields.get(i); %>	<option  value="<%=r.getString("ID") %>"><%=r.getString("GROUP_NAME") %></option>		<%}%>
						</select></td>
						<td class="csui_label" colnum="2" alert="">CUSTOM FIELDS </td>
						<td class="csui" colnum="2" type="String" itype="text" alert="">
						<select  name="CUSTOM_SINGLE_VALUE" id="CUSTOM_SINGLE_VALUE"  class="fieldselect" itype="String" val="" _ent="lso" class="fieldselect"  valrequired="false"><option value=""></option>
							
						</select>
						</td>
					</tr>
				
				
			</table>
			
			</div>			
		
		<!-- LIST -->
		
		<div>
				<table cellpadding="4" cellspacing="4" border="0">
				 	<tr>
						<td class="csui_label" colnum="2" alert="">LIST TYPE</td>
						<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
						<select  name="LISTS" id="LISTS" itype="String" val="" _ent="lso" valrequired="false">
							<option value=""></option>
							<option value="list_people">list_people</option>
							<option value="list_notes">list_notes</option>
							<option value="list_library">list_library</option>
							<option value="list_finance">list_finance</option>
							<option value="list_finance_total">list_finance_total</option>
							<option value="list_payment">list_payment</option>
							<option value="list_fee">list_fee</option>
							<option value="list_reviews">list_reviews</option>
							<option value="list_attachments">list_attachments</option>
							<option value="list_holds">list_holds</option>
							<option value="list_team">list_team</option>
							
						</select>
						</td>
					</tr>
					
					<tr>
						<td class="csui_label" colnum="2" alert="">OPTIONS</td>
						<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
						<ul id="list_people" class="lists" style="display:none;">list_people
							<li>[people_name]</li>
							<li>[people_first_name]</li>
							<li>[people_mi]</li>
							<li>[people_last_name]</li>
							<li>[people_username]</li>
							<li>[people_email]</li>
							<li>[people_type]</li>
							<li>[people_address]</li>
							<li>[people_lic_num]</li>
							<li>[people_lic_expiration_date]</li>
							<li>[people_license_type]</li>
							<li>people_city</li>
							<li>[people_state]</li>
							<li>[people_zip]</li>
							<li>[people_zip4]</li>
							<li>[people_phone_home]</li>
							<li>[people_phone_work]</li>
							<li>[people_phone_cell]</li>
							<li>[people_fax]</li>
							<li>[people_comments]</li>
							
							<li>rowconfig <b>list_people</b> with filter <b>list_people LKUP_USERS_TYPE_ID:1,2;</b></li>
							<li>
							
								
							<%for(MapSet r: ufields){ %>
								<%=r.getString("ID") %> -<%=r.getString("TYPE") %> 
								
							<%}%>
							
							</li>
						</ul>
						
						<ul id="list_team" class="lists" style="display:none;">list_team
							<li>[team_name]</li>
							<li>[team_username]</li>
							<li>[team_email]</li>
							<li>[team_type]</li>
							<li>[team_department]</li>
							<li>rowconfig <b>list_team</b> with filter <b>list_team LKUP_TEAM_TYPE_ID:1,2;</b></li>
							<li>
							
								
							<%for(MapSet r: utfields){ %>
								<%=r.getString("ID") %> -<%=r.getString("TYPE") %> 
								
							<%}%>
							
							</li>
						</ul>
						
						
						<ul id="list_finance" class="lists" style="display:none;">list_finance
							<li>[group_name]</li>
							<li>[activity_fee]</li>
							<li>[review_fee]</li>
							<li>[paid]</li>
							<li>[balance]</li>
						
						</ul>
						
						<ul id="list_finance_total" class="lists" style="display:none;">list_finance_total
							<li>[total_activity_fee]</li>
							<li>[total_review_fee]</li>
							<li>[total_paid]</li>
							<li>[total_balance]</li>
						
						</ul>
						
						<ul id="list_fee" class="lists" style="display:none;">list_fee
							<li>[fee_group_name]</li>
							<li>[fee_name]</li>
							<li>[fee_amount]</li>
							<li>[fee_paid]</li>
							<li>[fee_balance]</li>
							<li>[fee_group_id]</li>
							<li>[fee_date]</li>
							<li>[fee_created_date]</li>
							<li>[fee_updated_date]</li>
							<li>[fee_created]</li>
							<li>[fee_updated]</li>
							
							<li>[fee_input1] | [fee_input2] | [fee_input3] | [fee_input4] | [fee_input5]</li>
							
							<li>[fee_input_label1] | [fee_input_label2] | [fee_input_label3] | [fee_input_label4] | [fee_input_label5]</li>
							<li>[fee_input_editable1] | [fee_input_editable2] | [fee_input_editable3] | [fee_input_editable4] | [fee_input_editable5]</li>
							
						<li>rowconfig <b>list_fee</b> with filter <b>list_fee GROUP_ID:1,2; OR INPUT_EDITABLE1:Y</b></li>
							<li>	
								<select  name="FEE_GROUP_ID" id="FEE_GROUP_ID" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
									<%for(int i=0;i<fgfields.size();i++){ MapSet r = fgfields.get(i); %>	<option  value="<%=r.getString("ID") %>"><%=r.getString("GROUP_NAME") %> - <%=r.getString("ID") %></option>		<%}%>
								</select>
							</li>
						</ul>
						
						<ul id="list_notes" class="lists" style="display:none;">list_notes
							<li>[note_id]</li>
							<li>[note_note]</li>
							<li>[note_updated]</li>
							<li>[note_created_date]</li>
							<li>[note_updated_date]</li>
							<li>[note_lkup_notes_type_id]</li>
							<li>[note_type]</li>
							<li>rowconfig <b>list_notes</b> with filter <b>list_notes LKUP_NOTES_TYPE_ID:1,2;</b></li>
							<li>	
								<select  name="LKUP_NOTES_TYPE_ID" id="LKUP_NOTES_TYPE_ID" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
									<%for(int i=0;i<notetypefields.size();i++){ MapSet r = notetypefields.get(i); %>	<option  value="<%=r.getString("ID") %>"><%=r.getString("TYPE") %> - <%=r.getString("ID") %></option>		<%}%>
								</select>
							</li>
						</ul>
						<ul id="list_library" class="lists" style="display:none;">list_library
							<li>[library_id]</li>
							<li>[library_title]</li>
							<li>[library_txt]</li>
							<li>[library_code]</li>
							<li>[library_inspectable]</li>
							<li>[library_warning]</li>
							<li>[library_complete]</li>
							<li>[library_updated]</li>
							<li>[library_updated_date]</li>
							<li>rowconfig <b>list_library</b> with filter <b>list_library INSPECTABLE:Y;</b></li>
							
						</ul>
						
						<ul id="list_payment" class="lists" style="display:none;">list_payment
							<li>[payment_id]</li>
							<li>[payment_method]</li>
							<li>[payment_amount]</li>
							<li>[payment_paid_by]</li>
							<li>[payment_date]</li>
							<li>[payment_transaction_type]</li>
						</ul>
						
						<ul id="list_reviews" class="lists" style="display:none;">
							list_reviews
							<li>[review_group_name]</li>
							<li>[review_sub_title]</li>
							<li>[review_sub_date]</li>
							<li>[review_name]</li>
							<li>[review_description]</li>
							<li>[review_date]</li>
							<li>[review_comment]</li>
							<li>[review_type]</li>
							<li>[review_status]</li>
							<li>[review_team_type]</li>
							<li>[review_user]</li>
							<li>[review_attachment_title]</li>
							<li>[review_attachment_description]</li>
							<li>[review_attachment_path]</li>
							
							<li>[review_due_date]</li>
							<li>[review_team_name]</li>
							<li>[review_team_email]</li>
							<li>[review_team_phone]</li>
							
							<li>rowconfig <b>list_reviews</b> with filter <b>list_reviews REVIEW_GROUP_ID:1,2;</b></li>
							<li>	
								<select  name="REVIEW_GROUP_ID" id="REVIEW_GROUP_ID" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
									<%for(int i=0;i<rfields.size();i++){ MapSet r = rfields.get(i); %>	<option  value="<%=r.getString("ID") %>"><%=r.getString("GROUP_NAME") %> <%=r.getString("DESCRIPTION") %> - <%=r.getString("ID") %></option>		<%}%>
								</select>
							</li>
						</ul>
							
						</td>
					</tr>
					
					<tr>
						<td class="csui_label" colnum="2" alert="">CUSTOM LIST </td>
						<td class="csui" colnum="2" type="String" itype="text" alert="">
						<select  name="CUSTOM_LIST" id="CUSTOM_LIST" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
							<%for(int i=0;i<clfields.size();i++){ MapSet r = clfields.get(i); %>	<option  value="<%=r.getString("ID") %>"><%=r.getString("GROUP_NAME") %></option>		<%}%>
						</select></td>
						<td class="csui_label" colnum="2" alert="">VALUES </td>
						<td class="csui" colnum="2" type="String" itype="text" alert="">
						<div id="CUSTOM_LIST_VALUES" > </div>
						</td>
					</tr>
					
					
				</table>
			</div>
			
			
		<!-- SUMMARY -->
		
		<div>
				<table cellpadding="4" cellspacing="4" border="0">
				 	<tr>
						<td class="csui_label" colnum="2" alert="">LIST SUMMARY</td>
						<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
						<select  name="LISTS_SUMMARY" id="LISTS_SUMMARY" itype="String" val="" _ent="lso" valrequired="false">
							<option value=""></option>
							<option value="list_summary_activities">list_summary_activities</option>
							<option value="list_summary_people">list_summary_people</option>
							<option value="list_summary_notes">list_summary_notes</option>
							<option value="list_summary_reviews">list_summary_reviews</option>
						</select>
						</td>
					</tr>
					
					<tr>
						<td class="csui_label" colnum="2" alert="">OPTIONS</td>
						<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
						<ul id="list_summary_activities" class="listssummary" style="display:none;">list_summary_activities
							<li>[activity_id]</li>
							<li>[activity_act_nbr]</li>
							<li>[activity_type]</li>
							<li>[activity_status]</li>
							<li>[activity_description]</li>
							<li>[activity_valuation_calculated]</li>
							<li>[activity_valuation_declared]</li>
							<li>[activity_start_date]</li>
							<li>[activity_applied_date]</li>
							<li>[activity_issued_date]</li>
							<li>[activity_exp_date]</li>
							<li>[activity_status]</li>
							<li>[activity_created_by]</li>
							<li>[activity_updated_by]</li>
							<li>[total_activity_fee]</li>
							<li>[total_paid]</li>
							<li>[total_balance]</li>
						</ul>
						
			
						
						<ul id="list_summary_people" class="listssummary" style="display:none;">list_summary_people
							<li>[activity_act_nbr]</li>
							<li>[activity_type]</li>
							<li>[activity_status]</li>
							<li>[activity_description]</li>
							<li>[people_name]</li>
							<li>[people_first_name]</li>
							<li>[people_mi]</li>
							<li>[people_last_name]</li>
							<li>[people_username]</li>
							<li>[people_email]</li>
							<li>[people_type]</li>
							<li>[people_address]</li>
							<li>[people_lic_num]</li>
							<li>[people_lic_expiration_date]</li>
							<li>[people_license_type]</li>
							<li>people_city</li>
							<li>[people_state]</li>
							<li>[people_zip]</li>
							<li>[people_zip4]</li>
							<li>[people_phone_home]</li>
							<li>[people_phone_work]</li>
							<li>[people_phone_cell]</li>
							<li>[people_fax]</li>
							<li>[people_comments]</li>
							
							
							<li>rowconfig <b>list_summary_people</b> with filter <b>list_summary_people LKUP_USERS_TYPE_ID:1,2;</b></li>
							<li>
							
								
							<%for(MapSet r: ufields){ %>
								<%=r.getString("ID") %> -<%=r.getString("TYPE") %> 
								
							<%}%>
						</ul>
						
						<ul id="list_summary_notes" class="listssummary" style="display:none;">list_summary_notes
							<li>[note_id]</li>
							<li>[note_note]</li>
							<li>[note_updated]</li>
							<li>[note_created_date]</li>
							<li>[note_updated_date]</li>
							<li>[note_lkup_notes_type_id]</li>
							<li>[note_type]</li>
							<li>rowconfig <b>list_summary_notes</b> with filter <b>list_summary_notes LKUP_NOTES_TYPE_ID:1,2;</b></li>
							<li>	
								<select  name="LKUP_NOTES_TYPE_ID" id="LKUP_NOTES_TYPE_ID" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
									<%for(int i=0;i<notetypefields.size();i++){ MapSet r = notetypefields.get(i); %>	<option  value="<%=r.getString("ID") %>"><%=r.getString("TYPE") %> - <%=r.getString("ID") %></option>		<%}%>
								</select>
							</li>
						</ul>
						
						
						<ul id="list_summary_reviews" class="listssummary" style="display:none;">
							list_summary_reviews 
							<li>[activity_act_nbr]</li>
							<li>[activity_type]</li>
							<li>[activity_status]</li>
							<li>[activity_description]</li>
							<li>[review_group_name]</li>
							<li>[review_sub_title]</li>
							<li>[review_sub_date]</li>
							<li>[review_name]</li>
							<li>[review_description]</li>
							<li>[review_date]</li>
							<li>[review_comment]</li>
							<li>[review_type]</li>
							<li>[review_status]</li>
							<li>[review_team_type]</li>
							<li>[review_user]</li>
							<li>[review_uupdated_date]</li>
							<li>[review_updated_date_time]</li>
							<li>[review_user]</li>
							<li>[review_attachment_title]</li>
							<li>[review_attachment_description]</li>
							<li>[review_attachment_path]</li>
							<li>[review_appointment_start_date]</li>
							<li>[review_appointment_end_date]</li>
							
							
							<li>rowconfig <b>list_summary_reviews</b> with filter <b>list_summary_reviews REVIEW_GROUP_ID:1,2;</b></li>
							<li>	
								<select  name="REVIEW_GROUP_ID" id="REVIEW_GROUP_ID" itype="String" val="" _ent="lso" valrequired="false"><option value=""></option>
									<%for(int i=0;i<rfields.size();i++){ MapSet r = rfields.get(i); %>	<option  value="<%=r.getString("ID") %>"><%=r.getString("GROUP_NAME") %> - <%=r.getString("ID") %></option>		<%}%>
								</select>
							</li>
						</ul>
							
						</td>
					</tr>
					
					
					
					
					
					
					
				</table>
			</div>			
		
		</div>
			
	</form>
				
				


</body>
</html>


