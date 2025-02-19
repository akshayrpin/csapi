<%@page import="alain.core.security.Token"%>
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
	
	String ent = map.getString("_ent");
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	
	if(!AdminAgent.secureAdmin(map)){
		map.redirect("../noaccess.jsp");
	}

	Timekeeper d = new Timekeeper();
	String fullcontexturl = CsApiConfig.getString("cs.fullcontexturl");
	String table = Table.LKUPACTTYPETABLE;
	String alert="";
	
	//String connecttype = "ACTIVITY";
	
	boolean connector = Operator.equalsIgnoreCase(map.getString("view"),"connector");
	String connectids = map.getString("connectids","");
	String connecttype = map.getString("connecttype");
	
	System.out.println(connecttype+"%%%%%"+connectids+"#########"+connector+"4444"+map.getString("view")+"%%%"+table);
	
	
	
	
	//Pagination
	int maxrows = map.getInt("MAX", 1000);
	int pg = map.getInt("PAGE",1);
	String sortfield= map.getString("SORT_FIELD","TYPE");
	String order= map.getString("ORDER","ASC");
	String ord= order;
	if(order.equalsIgnoreCase("DESC")){
		ord = "ASC";
	}else {
		ord="DESC";
	}
	int records = 0;
	ArrayList<MapSet> c =  new ArrayList<MapSet>();
	if(connector){
		MapSet m = AdminMap.getProjectActivityRef(map);
		c =  AdminAgent.getSubList(m);
	}
	
	
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
		a =  AdminAgent.getList(table,start,end,sortfield,order,map.getString("SQ"),map.getString("SEARCH_FIELDS"));
		//records =5;
		//System.out.println("SEARCHG"+map.getString("SEARCH_FIELDS"));
	//	url.append("/admin/formmgr/formEntries.jsp?SEARCH=SEARCH&FORM_ID=").append(formId).append("&CATEGORY_ID=").append(categoryId).append("&MAX=").append(maxrows).append("&Q=").append(q).append("&searchfields=").append(searchfields).append("&displayfields=").append(displayfields).append("&SORT_FIELD=").append(sortfield).append("&ORDER=").append(order);
	//	urlsort.append("/admin/formmgr/formEntries.jsp?SEARCH=SEARCH&FORM_ID=").append(formId).append("&CATEGORY_ID=").append(categoryId).append("&MAX=").append(maxrows).append("&Q=").append(q).append("&searchfields=").append(searchfields).append("&displayfields=").append(displayfields);
	}else {
		a =  AdminAgent.getList(table,start,end,sortfield,order,"","");
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
			
			
			 $('#multiedit').click(function () {
				 var loc=$(this).attr("loc");
				 var name ="Multi-Edit";
				 var v = $('input:checkbox:checked').map(function() {   return this.value; }).get();
					if(v==""){
						swal("Select Types in order to proceed");
						return false;
					}
					//v = v.replace("on","0");
					//alert(v);
					var u = loc+"&IDS="+v;
					u = u.replace("on,","");
					//alert(u);
					$(this).attr("href",u);
		       	
					});
			
			
				
			
				 $(".tablesorter").tablesorter(); 
				 
				  
				 
				 
				 <%if(Operator.hasValue(connectids)){ %>
					
				 <%for(int i=0;i<c.size();i++){
					 	MapSet r = c.get(i);
					 	
					%>
					var id = "<%= r.getString("ID")%>";
					$('#tr_'+id).hide();
					
					<%}%>
				 
				 $('.csui').click(function() {
						var id = $(this).attr('rel');
						//alert(id);
						if(id>0){
							appendrefs(id);
						}
						
					});
				 <%} %>
				
				<%if(c.size()>0){ %>
				 
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

				 <%} %>
				
					
				 
		});
		
		function deleteref(fieldgroupsid){
			var method = "deleteref";
			var name = $('#refname_'+fieldgroupsid).val();
			swal({  
					title: "Are you sure?",   
					text: "You want to delete "+name+" record!",   
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
				   			  url: "action.jsp?_action="+method,
				   			  dataType: 'json',		  
				   			  data: { 
				   			
				   			   	  _ent : "<%=ent%>",
				   			   	  _type : "<%=type%>",
				   				  _typeid : <%=typeid%>,
				   				  _id : <%=id%>,
				   				   connecttype : "<%=connecttype%>",
				   				   connectids : "<%=connectids%>",
				   				  // REF_ID : fieldgroupsid
				   				  LKUP_ACT_TYPE_ID : fieldgroupsid
				   			      //mode : mode
				   			    },
				   			    success: function(output) {
				   			    		$('#tr_'+fieldgroupsid).show();
				   			    		$('#cr_'+fieldgroupsid).remove();
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
	
	
	function appendrefs(id){
		var name = $('#refname_'+id).val();
		var method = "addref";
		var autoadd= "N";
		if($('#AUTO_ADD_'+id).is(':checked')){
			autoadd ="Y";
		}
		
		var c = '';
		c += '<table class="csui" type="horizontal" id="cr_'+id+'" style="cursor:pointer;" title="Drag to sort">';
		c += '<tr id="'+id+'" class="ref_sortable" >';
		c += '	<td class="csui" width="1%">';
		c += '<img src="/cs/images/icons/controls/black/delete.png" border="0" onclick="deleteref('+id+');" >';
		c += '</td>';
		
		c += '<td class="csui">';
		c += '	'+autoadd+'';
		c += '</td>';
		c += '<td class="csui">';
		c += '	'+name+'';
		c += '</td>';
		c += '</tr>';
		c += '</table>';
		$.ajax({
 			  type: "POST",
 			  url: "action.jsp?_action="+method,
 			  dataType: 'json',		  
 			  data: { 
 				  _ent : "<%=ent%>",
 			   	  _type : "<%=type%>",
 				  _typeid : <%=typeid%>,
 				  _id : <%=id%>,
 				  connecttype : "<%=connecttype%>",
 				  connectids : "<%=connectids%>",
 				  LKUP_ACT_TYPE_ID : id,
 				  AUTO_ADD : autoadd
 			      //mode : mode
 			    },
 			    success: function(output) {
 			    
 			    	$('#refs').append(c);
 					$('#tr_'+id).hide();
 			    		
 			    },
 		    error: function(data) {
 		    	swal("Problem while processing the request");  
 		    }
		 });		
		
		
		
		
	}

	function sortrefs(order){
		var method = "sortref";
		var find = 'cr_';
		order = order.replace(new RegExp(find, 'g'), "");
		$.ajax({
 			  type: "POST",
 			  url: "action.jsp?_action="+method,
 			  dataType: 'json',		  
 			  data: { 
 				  _ent : "<%=ent%>",
 			   	  _type : "<%=type%>",
 				  _typeid : <%=typeid%>,
 				 _id : <%=id%>,
 				  connecttype : "<%=connecttype%>",
 				  connectids : "<%=connectids%>",
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
<div id="loader"></div>

	<div id="csuicontrols">
		<div id="csuicontrol" class="csuicontrol warning">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td align="left" class="csuicontrol"><%= l.getString("NAME") %></td>
					<td align="right"><table class="csui_tools">
  <tr>
	<td class="csui_tools">
	<a target="lightbox-iframe" href="add.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
	</td>
	

  </tr>
</table>
</td>
				</tr>
			</table>
		</div>

		<div id="csuisubcontrol" class="csuisubcontrol <%= alert %>">CONNECTORS</div>
	</div>
	<div id="csuibody">
		<div id="csuimain">
			<div class="csuicontent">
			<br/><br/>
				<div id="csform_message"></div>
				<form id="csform" action="list.jsp" method="post">
				<input type="hidden" name="_ent" value="<%=ent%>">
				<input type="hidden" name="_type" value="<%=type%>">
				<input type="hidden" name="_typeid" value="<%=typeid%>">
				<input type="hidden" name="_id" value="<%=id%>">
				<input type="hidden" name="SEARCH" value="SEARCH">
				<input type="hidden" name="SEARCH_FIELDS" value="TYPE,DESCRIPTION">
				
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
						<td class="csui_title"><%= l.getString("NAME") %></td>
						</tr>
					</table>
					
					<table class="csui tablesorter" type="horizontal">
					 <thead>
						<tr>
							<td class="csui_header">TYPE</td>
							<td class="csui_header">DESCRIPTION</td>
							<td class="csui_header">ISPUBLIC</td>
							<td class="csui_header">ONLINE</td>
							<td class="csui_header">PATTERN</td>
							<td class="csui_header">ISDOT</td>
							<td class="csui_header">DOT_EXEMPTION</td>
							<td class="csui_header">MAX ALLOWED</td>
							<td class="csui_header">MAX APPOINTMENTS</td>
							<td class="csui_header">MONTH ST</td>
							<td class="csui_header">DAY ST</td>
							<td class="csui_header">YEARS TILL</td>
							<td class="csui_header">DAYS TILL</td>
							<td class="csui_header">AUTO ADD</td>
							
						
							
							
						</tr>
						</thead>
						 <tbody>
						<%for(int i=0;i<a.size();i++){
							MapSet r = a.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("TYPE") %></td>
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("DESCRIPTION") %>
									 <input type="hidden" id="refname_<%=r.getString("ID") %>" name="refname_<%=r.getString("ID") %>" value="<%=r.getString("DESCRIPTION") %>">
								
								</td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("ISPUBLIC") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("ONLINE") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("PATTERN") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("ISDOT") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("DOT_EXEMPTION") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("MAX_ALLOWED") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("MAX_ACTIVE_APPOINTMENTS") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("MONTH_START") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("DAY_START") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("YEARS_TILL_EXPIRED") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("DAYS_TILL_EXPIRED") %></td>
								
						
								
							<td class="csui" colnum="2" type="boolean" itype="boolean" alert=""><div>
							<input name="AUTO_ADD_<%=r.getString("ID") %>" id="AUTO_ADD_<%=r.getString("ID") %>" type="checkbox" itype="boolean">
							</div></td>

								
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
					<%if(c.size()>0 || Operator.hasValue(connectids)){ %>
				<table class="csuisub_title sticky" alert="warning"  cellpadding="20" cellspacing="0" border="0" type="horizontal" >
						
						<tr>
							<td class="csui">
						
								<table class="csui_title" type="horizontal">
									<tr>
										<td class="csui_title" colspan="2" align="center">
											SELECTED
										</td>
									</tr>
									
								</table>
							
								<div id="refs" style=" position:relative; padding: 5px;overflow:auto ">
						
							
							
								<%for(int i=0;i<c.size();i++){
								 	MapSet r = c.get(i);
								%>
								
									<table class="csui" type="horizontal" id="cr_<%=r.getString("ID")%>" style="cursor:pointer;" title="Drag to sort">
										<tr>
											<td class="csui" width="1%">
												<img src="/cs/images/icons/controls/black/delete.png" border="0" onclick="deleteref(<%=r.getString("ID") %>);" >
											</td>
											<td class="csui">
												
												<%=r.getString("AUTO_ADD")%>
												
											</td>
											<td class="csui">
												<%=r.getString("DESCRIPTION")%>
											</td>
											
											
										</tr>
									</table>
								<%} %>
					
					
								</div>
							</td>
						</tr>
				</table>
				<%} %>
				</div>
				<div class="csuisub_divider"></div>
				<div class="csui_divider"></div>
				
		</div>
	</div>




</body>
</html>

