<%@page import="alain.core.security.Token"%>
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
	
	String ent = map.getString("_ent");
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	
	
	if(!AdminAgent.secureAdmin(map)){
		map.redirect("../noaccess.jsp");
	}
	//TODO PAGINATION WHEN DATA SIZE GETS BIGGER 
	Timekeeper d = new Timekeeper();
	String fullcontexturl = CsApiConfig.getString("cs.fullcontexturl");
	
	String table = "";
	String SEARCH_FIELDS = "";
	String SORT_FIELD = "";
	String CONNECT_TYPE = "";
	String COLUMNS = "";
	String label = "";
	String reftable ="";
	String additionalColumns ="";
	String additionaljoins ="";
	String additionaland =""; 
	
	if(type.equalsIgnoreCase("LKUP_PROJECT_TYPE")){
		table = type;
		COLUMNS = "TYPE,DESCRIPTION,UPDATED,C_UPDATED_DATE";
		SORT_FIELD = "TYPE";
		SEARCH_FIELDS = "TYPE,DESCRIPTION";
		label = "PROJECT LOOKUP";
		reftable ="REF_PROJECT_TYPE_ROLES";
		additionaljoins = " LEFT OUTER JOIN  REF_PROJECT_TYPE_ROLES RPTR on A.ID = RPTR.LKUP_PROJECT_TYPE_ID  AND RPTR.ACTIVE='Y' AND RPTR.LKUP_ROLES_ID= "+id;
		additionaland =" AND RPTR.ID is null "; 
	}
	
	if(type.equalsIgnoreCase("LKUP_ACT_TYPE")){
		table = type;
		COLUMNS = "TYPE,DESCRIPTION,UPDATED,C_UPDATED_DATE";
		SORT_FIELD = "TYPE";
		SEARCH_FIELDS = "TYPE,DESCRIPTION";
		label = "ACTIVITY LOOKUP";
		reftable ="REF_ACT_TYPE_ROLES";
		additionaljoins = " LEFT OUTER JOIN  REF_ACT_TYPE_ROLES RPTR on A.ID = RPTR.LKUP_ACT_TYPE_ID  AND RPTR.ACTIVE='Y' AND RPTR.LKUP_ROLES_ID= "+id;
		additionaland =" AND RPTR.ID is null "; 
	}
	
	if(type.equalsIgnoreCase("LKUP_MODULE")){
		table = type;
		COLUMNS = "MODULE,DESCRIPTION,UPDATED,C_UPDATED_DATE";
		SORT_FIELD = "MODULE";
		SEARCH_FIELDS = "MODULE,DESCRIPTION";
		label = "MODULE LOOKUP";
		reftable ="REF_MODULE_ROLES";
		additionaljoins = " LEFT OUTER JOIN  REF_MODULE_ROLES RPTR on A.ID = RPTR.LKUP_MODULE_ID  AND RPTR.ACTIVE='Y'  AND RPTR.LKUP_ROLES_ID= "+id;
		additionaland =" AND RPTR.ID is null "; 
	}
	
	if(type.equalsIgnoreCase("REVIEW_GROUP")){
		table = type;
		COLUMNS = "GROUP_NAME,DESCRIPTION,UPDATED,C_UPDATED_DATE";
		SORT_FIELD = "GROUP_NAME";
		SEARCH_FIELDS = "GROUP_NAME,DESCRIPTION";
		label = "REVIEW GROUP";
		reftable ="REF_REVIEW_GROUP_ROLES";
		additionaljoins = " LEFT OUTER JOIN  REF_REVIEW_GROUP_ROLES RPTR on A.ID = RPTR.REVIEW_GROUP_ID  AND RPTR.ACTIVE='Y'  AND RPTR.LKUP_ROLES_ID= "+id;
		additionaland =" AND RPTR.ID is null "; 
	}
	
	if(type.equalsIgnoreCase("FIELD_GROUPS")){
		table = type;
		COLUMNS = "GROUP_NAME,UPDATED,C_UPDATED_DATE";
		SORT_FIELD = "GROUP_NAME";
		SEARCH_FIELDS = "GROUP_NAME";
		label = "CUSTOM GROUP";
		reftable ="REF_FIELD_GROUPS_ROLES";
		additionaljoins = " LEFT OUTER JOIN  REF_FIELD_GROUPS_ROLES RPTR on A.ID = RPTR.FIELD_GROUPS_ID  AND RPTR.ACTIVE='Y'  AND RPTR.LKUP_ROLES_ID= "+id;
		additionaland =" AND RPTR.ID is null "; 
	}
	

	if(type.equalsIgnoreCase("LKUP_TAB")){
		table = type;
		COLUMNS = "TAB,UPDATED,C_UPDATED_DATE";
		SORT_FIELD = "TAB";
		SEARCH_FIELDS = "TAB";
		label = "TAB LOOKUP";     
		reftable ="REF_TAB_ROLES";
		additionaljoins = " LEFT OUTER JOIN  REF_TAB_ROLES RTR on A.ID = RTR.LKUP_TAB_ID  AND RTR.ACTIVE='Y' AND RTR.LKUP_ROLES_ID= "+id;
		additionaland =" AND RTR.ID is null "; 
	}
	
	if(type.equalsIgnoreCase("LKUP_ADMIN_MODULE")){
		table = type;
		COLUMNS = "NAME,UPDATED,C_UPDATED_DATE";
		SORT_FIELD = "NAME";
		SEARCH_FIELDS = "NAME";
		label = "ADMIN LOOKUP";     
		reftable ="REF_ADMIN_ROLES";
		additionaljoins = " LEFT OUTER JOIN  REF_ADMIN_ROLES RTR on A.ID = RTR.LKUP_ADMIN_MODULE_ID  AND RTR.ACTIVE='Y' AND RTR.LKUP_ROLES_ID= "+id;
		additionaland =" AND RTR.ID is null "; 
	}
	
	
	String alert="";
	
	
	
	
	//Pagination
	int maxrows = map.getInt("MAX", 2000);
	int pg = map.getInt("PAGE",1);
	String sortfield= map.getString("SORT_FIELD",SORT_FIELD);
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
	ArrayList<MapSet> a =  new ArrayList<MapSet>();
	ArrayList<MapSet> la = AdminAgent.getIndex(type, typeid, id,false,map);
	MapSet l = new MapSet();
	if(la.size()>0){
		l = la.get(0);
	}
	StringBuffer url = new StringBuffer();
	StringBuffer urlsort = new StringBuffer();
		
	ArrayList<MapSet> sla = AdminAgent.getSubIndex(type, typeid, id);
	
	 if(map.equalsIgnoreCase("SEARCH","SEARCH")){
		a =  AdminAgent.getListWithAdditional(table,start,end,sortfield,order,map.getString("SQ"),map.getString("SEARCH_FIELDS"),additionalColumns,additionaljoins,additionaland);
		//records =5;
		
	//	url.append("/admin/formmgr/formEntries.jsp?SEARCH=SEARCH&FORM_ID=").append(formId).append("&CATEGORY_ID=").append(categoryId).append("&MAX=").append(maxrows).append("&Q=").append(q).append("&searchfields=").append(searchfields).append("&displayfields=").append(displayfields).append("&SORT_FIELD=").append(sortfield).append("&ORDER=").append(order);
	//	urlsort.append("/admin/formmgr/formEntries.jsp?SEARCH=SEARCH&FORM_ID=").append(formId).append("&CATEGORY_ID=").append(categoryId).append("&MAX=").append(maxrows).append("&Q=").append(q).append("&searchfields=").append(searchfields).append("&displayfields=").append(displayfields);
	}else {
		a =  AdminAgent.getListWithAdditional(table,start,end,sortfield,order,"","",additionalColumns,additionaljoins,additionaland);
		//records =11;
		//url.append("/admin/formmgr/formEntries.jsp?FORM_ID=").append(formId).append("&CATEGORY_ID=").append(categoryId).append("&MAX=").append(maxrows).append("&SORT_FIELD=").append(sortfield).append("&ORDER=").append(order);
		urlsort.append("/lookupprojecttype.jsp?_type=").append(type).append("&_typeid=").append(typeid).append("&MAX=").append(maxrows);
	} 
	 int totalpages = Operator.getTotalPages(records,maxrows);


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
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/tablesorter/css/theme.dropbox.css">
	<style>
		.csui_controls { visibility: hidden }
		.searchbox {  -webkit-border-radius: 50px;-moz-border-radius: 50px;	border-radius: 50px;width:200px;height:25px;align:right;	}
		 .pageindex, .pageindex a, .pageindex a:link, .pageindex a:active, .pageindex a:visited, a.pageindex:link, a.pageindex:active, a.pageindex:visited {
		 font-family: Arial, Helvetica, Sans-Serif;
		 font-size: 12px;
		 color: #ffffff;
		 text-decoration: none;
		 padding: 5px;
		 background-color: transparent;

	}
	 .pageindex-current, .pageindex-current a, .pageindex-current a:link, .pageindex-current a:active, .pageindex-current a:visited, a.pageindex-current:link, a.pageindex-current:active, a.pageindex-current:visited {
		 font-family: Arial, Helvetica, Sans-Serif;
		 font-size: 15px;
		 font-weight: bold;
		 color: #000000;
		 text-decoration: none;
		 padding: 5px;
		 background-color: #ffffff;
		 border: 1px solid #000000;
	}
	.csui_header{
		cursor:pointer;
	}
		
	</style>
	

	<script type="text/javascript" src="<%=fullcontexturl %>/tools/jquery.min.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/alain/cs.tools.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/alain/cs.form.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/datetimepicker/jquery.datetimepicker.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/chosen/chosen.jquery.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/toggleswitch/tinytools.toggleswitch.js"></script>
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
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/tablesorter/jquery.tablesorter.min.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/sticky/jquery.sticky.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/ioscheckboxes/assets/css/mobileCheckbox.iOS.css">
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/ioscheckboxes/assets/js/jquery.mobileCheckbox.js"></script>
	
	<style>
		
	</style>

	<script>
		$(document).ready(function() {
			
				 $(".tablesorter").tablesorter(); 
				 
				 $("#selectorall").click(function(){
					   $('input:checkbox').not(this).prop('checked', this.checked);
				});
				 
				 
				 
				 $('#associate').click(function () {
						
						var method = "associatetypes";
						var v = $('input:checkbox:checked').map(function() { if(this.value>0)  return this.value; }).get();
						if(v==""){
							swal("Select Types in order to proceed");
							return false;
						}
					
						
						var c = "N";
						var r = "N";
						var u = "N";
						var d = "N";
						
						if($('#c').is(':checked')==true){ c = "Y"; 	}
						if($('#r').is(':checked')==true){ r = "Y"; 	}
						if($('#u').is(':checked')==true){ u = "Y"; 	}
						if($('#d').is(':checked')==true){ d = "Y"; 	}
					
						$.ajax({
				   			  type: "POST",
				   			  url: "action.jsp?_action="+method,
				   			  data: { 
				   			      id : "<%=id%>",
				   			   	  _ent : "<%=ent%>",
				   			   	  _type : "<%=type%>",
				   				  MULTI_IDS : v,
				   				 _typeid : "<%=typeid%>",
				   				 reftable : "<%=reftable%>",
				   				 c : c,
				   				 r : r,
				   				 u : u,
				   				 d : d
				   		
				   			    },
				   			    success: function(output) {
				   			    	swal({   title: "Request",   text: "Associated!",   timer: 2000,   showConfirmButton: false });
				   			    	window.parent.$("#csform").submit();
				   					parent.$.fancybox.close();	
				   			    },
				   		    error: function(data) {
				   		        swal('Your request was not processed. Please check your input data.');
				   		    }
			   			});
						
						
			       	
				});
			
				 
		});
		
	
		
		
	</script>

</head>
<body alert="<%= alert %>">
<div id="loader"></div>

	<div id="csuicontrols">
		<div id="csuicontrol" class="csuicontrol warning">
				<%= label %>
		</div>

		<div id="csuisubcontrol" class="csuisubcontrol <%= alert %>">CONNECTORS</div>
	</div>
	<div id="csuibody">
		<div id="csuimain">
			<div class="csuicontent">
			<br/><br/>
				<div id="csform_message"></div>
				<form id="csform" action="rlist.jsp" method="post">
				<input type="hidden" name="_ent" value="<%=ent%>">
				<input type="hidden" name="_type" value="<%=type%>">
				<input type="hidden" name="_typeid" value="<%=typeid%>">
				<input type="hidden" name="_id" value="<%=id%>">
				<input type="hidden" name="SEARCH" value="SEARCH">
				<input type="hidden" name="SEARCH_FIELDS" value="<%=SEARCH_FIELDS%>">
				
				<table align="right">
					
					<tr>
						<td class="search"  type="String" itype="String" alert="" align="right">
							<input name="SQ" class="searchbox" id="search" type="text" itype="text" value="<%=map.getString("SQ") %>"  placeholder="&nbsp;Search" maxchar="100" >
							
						</td>
					</tr>
					
					<%if(records>maxrows){ %>
					<tr>
						<td class="csui"  type="String" itype="String" alert="" align="right">
							Showing (<%=maxrows %> of <%=records %>)
						</td>
					</tr>
					<%} %>
				</table>
				
				
				</form>
				
				<div class="csui_divider"></div>
			
				
				
				
				   <table class="csui_title" alert="warning">
						<tr>
						<td class="csui_title"><%= label %></td>
						</tr>
					</table>
					
					<table class="csui tablesorter" type="horizontal">
					 <thead>
						<tr>
							<td class="csui_header"><input type="checkbox" name="selectorall" id="selectorall" ></td>
							<%
							String headers[] = COLUMNS.split(",");
							for(String h :headers ){	
							%>
								<td class="csui_header"><%=h %></td>
							
							<%} %>
							
							
						</tr>
						</thead>
						 <tbody>
						<%for(int i=0;i<a.size();i++){
							MapSet r = a.get(i);
							
						%>
							<tr id="tr_<%=r.getString("ID") %>">
								<td class="csui"><input type="checkbox" name="selector" id="selector" class="selector" value="<%=r.getString("ID") %>"> </td>
								<%
									for(String h :headers ){	
								%>
								<td class="csui"><%=r.getString(h) %></td>
								<%} %>
							</tr>
						<%} %>
						</tbody>
					</table>
	
				   <table cellpadding="20" cellspacing="0" border="0" width="100%" class="csui_title">
						<tr>
							<td align="center" class="csui_title"><%=Operator.pageIndex(url.toString(),totalpages,pg) %></td>
						</tr>
					</table>
				   
				   
				
			</div>
				
			</div>
		
		<div id="csuisub">
				<div class="csuisub_divider"></div>
				<div class="csuisubcontent">
				
				<table class="csuisub_title sticky " alert="warning"  cellpadding="20" cellspacing="0" border="0" type="horizontal" >
						<tr>
							<td class="csui">
								Create
							</td>
							<td class="csui">
								<input type="checkbox" id="c" name="c">
							</td>
						</tr>
						<tr>
							<td class="csui">
								Read
							</td>
							<td class="csui">
								<input type="checkbox" id="r" name="r">
							</td>
						</tr>
						<tr>
							<td class="csui">
								Update
							</td>
							<td class="csui">
								<input type="checkbox" id="u" name="u">
							</td>
						</tr>
						<tr>
							<td class="csui">
								Delete
							</td>
							<td class="csui">
								<input type="checkbox" id="d" name="d">
							</td>
						</tr>
						<tr>
							<td class="csuisub_title" colspan="2">
							<input type="submit" id="associate" name="associate" value="ASSOCIATE" class="connectors" >
							</td>
						</tr>
						
				</table>
				
				</div>
				<div class="csuisub_divider"></div>
				<div class="csui_divider"></div>
				
				
				
		</div>
	</div>




</body>
</html>

