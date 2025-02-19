<%@page import="alain.core.security.Token"%>
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
	
	String ent = map.getString("_ent");
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	if(!AdminAgent.secureAdmin(map)){
		map.redirect("../noaccess.jsp");
	}

	Token t = Token.retrieve(map.getString("_token"), map.getRemoteIp());
	Timekeeper d = new Timekeeper();
	String fullcontexturl = CsApiConfig.getString("cs.fullcontexturl");
	String table = Table.LSOTABLE;
	
	String alert="";
	
	//extras
	
	boolean connector = Operator.equalsIgnoreCase(map.getString("view"),"connector");
	String connectids = map.getString("connectids","");
	String connecttype = map.getString("connecttype");
	
	//Pagination
	int maxrows = map.getInt("MAX", 1000);
	int pg = map.getInt("PAGE",1);
	String sortfield= map.getString("SORT_FIELD","DESCRIPTION");
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
	ArrayList<MapSet> c =  new ArrayList<MapSet>();
	ArrayList<MapSet> la = AdminAgent.getIndex(type, typeid, id,false, t);
	MapSet l = new MapSet();
	if(la.size()>0){
		l = la.get(0);
	}
	StringBuffer url = new StringBuffer();
	StringBuffer urlsort = new StringBuffer();
		
	
	if(connector){
		MapSet m = AdminMap.getModuleRef(map);
		//c =  AdminAgent.getSubList(m);
	}
	JSONObject lso = new JSONObject(); 
	ArrayList<MapSet> lsoList = new ArrayList<MapSet>();
	 if(map.equalsIgnoreCase("SEARCH","SEARCH")){
			lso = AdminAgent.searchLso(map.getString("STR_NO"), map.getString("STR_MOD"), map.getString("LSO_STREET_ID"), map.getString("UNIT"), map.getString("LSO_ID"));
	} else {
		//lsoList = AdminAgent.getList(m.getString("_getlsoquery"));
		//a =  AdminAgent.getList(table,start,end,sortfield,order,"","");
		//records =11;
		//url.append("/admin/formmgr/formEntries.jsp?FORM_ID=").append(formId).append("&CATEGORY_ID=").append(categoryId).append("&MAX=").append(maxrows).append("&SORT_FIELD=").append(sortfield).append("&ORDER=").append(order);
		//urlsort.append("/lookupprojecttype.jsp?_type=").append(type).append("&_typeid=").append(typeid).append("&MAX=").append(maxrows);
	} 
	 int totalpages = Operator.getTotalPages(records,maxrows);

	 JSONArray mtypes = AdminAgent.getLookup("STREET_MOD");
	 JSONArray stypes = AdminAgent.getLookup("LSO_STREET");
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
	
	
	
		td[stid] {
			cursor: pointer;
			background: url(<%=fullcontexturl %>/images/arrow-down-black.png) center no-repeat #fff;
			width: 15px;
		}
		td[stid].stactive {
			background: url(<%=fullcontexturl%>/images/arrow-up-white.png) center no-repeat #336699 !important;
			width: 15px !important;
		}
		.csui_header.pymnt {
			color: #ffffff !important;
			background-color: #336699 !important;
		}
		.csui.pymnt {
			background-color: #eeeeee !important;
		}
		.csui.stactive {
			background-color: #d7e1eb !important;
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
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/jquery-ui.min.js"></script>
	
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/ioscheckboxes/assets/css/mobileCheckbox.iOS.css">
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/ioscheckboxes/assets/js/jquery.mobileCheckbox.js"></script>
	
	<style>
		
	</style>

	<script>
		$(document).ready(function() {
			
		
			$('#LSO_STREET_ID').val(<%=map.getString("LSO_STREET_ID")%>);
			$('#LSO_STREET_ID').trigger('chosen:updated');
					 
			 $('.scrollselector').click(function() {
					var stid = $(this).attr('stid');
					if ($(this).hasClass('stactive')) {
						$(this).removeClass('stactive');
						$('[rel='+stid+']').removeClass('stactive');
					}
					else {
						$(this).addClass('stactive');
						$('[rel='+stid+']').addClass('stactive');
					}
					showselector(stid);
				});
					

				
				 
		});
		
		function showselector(id){
			
			if($('#show_bottom_'+id).val()=="1"){
				$("#show_selector_"+id).hide();
				$('#show_bottom_'+id).val("0");
				return false;
			}
			
			var method = "showselector"
			var type ={};
			$.ajax({
	   			  type: "POST",
	   			  url: "action.jsp?_action="+method,
	   			 // dataType: 'json',		  
	   			  data: { 
	   				// feesjson : type,
	   			      id : "<%=id%>",
	   			   	  _ent : "<%=ent%>",
	   			   	  _type : "<%=type%>",
	   			   	  lso_id : id,
	   				 _typeid : "<%=typeid%>"
	   			      //mode : mode
	   			    },
	   			 complete: function(){
	   				 $('.scrollselector').click(function() {
							var stid = $(this).attr('stid');
							if ($(this).hasClass('stactive')) {
								$(this).removeClass('stactive');
								$('[rel='+stid+']').removeClass('stactive');
							}
							else {
								$(this).addClass('stactive');
								$('[rel='+stid+']').addClass('stactive');
							}
							showselector(stid);
						});
	   			 },
	   			    success: function(output) {
	   			    		showselectorui(id,output);
	   			    		//output = JSON.parse(output);
	   			    		//alert(output);
	   			    		
	   			    },
	   		    error: function(data) {
	   		        swal('Your request was not processed. Please check your input data.');
	   		    }
   			});
		}
		
	function showselectorui(id,output){
			//output = JSON.stringify(output);
	 		//alert(output);
	 		//console.log(output);
	 		output = JSON.parse(output);
	 		//alert(output[''].length);
	 		var c = '';
	 		c += '<tr id="show_selector_"'+id+' >';
	 		c += '<td class="csui_header pymnt">&nbsp;</td>';
	 		c += '<td class="csui_header pymnt" width="1%" nowrap>NAME</td>';
			c += '<td class="csui_header pymnt" width="1%" nowrap>NAME</td>';
			c += '<td class="csui_header pymnt" width="1%" nowrap>TYPE</td>';
			c += '<td class="csui_header pymnt" width="1%" nowrap>ITYPE</td>';
			//c += '<td class="csui_header pymnt">PAYEE</td>';
			//c += '<td class="csui_header pymnt" width="1%" nowrap>PAID</td>';
			//c += '<td class="csui_header pymnt" width="1%" nowrap>REVERSE AMOUNT</td>';
			//c += '<td class="csui_header pymnt" width="1%" nowrap>REVERSE METHOD</td>';
			c += '<td class="csui_header pymnt">&nbsp;</td>';
			c += '</tr>';
			//c += '<table class="sortable">';
			c ='';
	 		var t = 1;	
	 		$.each(output, function(k,v) {
	 				c+= '<tr class="csui"  style="cursor:pointer;" id="tr_'+v.LSO_ID+'" >';
	 				c += '<td class="csui" type="String"  itype="String" colspan="2" nowrap>&nbsp;</td>';
	 				
	 				c += '<td class="csui" type="String"  itype="String" >'+v.LSO_TYPE+'</td>';
	 				c += '<td class="csui" type="String"  itype="String" >'+v.LSO_ID+'</td>';
			 		c += '<td class="csui" type="String"  itype="String" >'+v.ADDRESS+'</td>';
			 		c += '<td class="csui" type="String" itype="String" >'+v.DESCRIPTION+'</td>';
			 		c += '<td class="csui"><a title="Edit" target="lightbox-iframe" href="addAPN.jsp?_ent=permit&_id=38&_type=admin&_typeid=17&ID='+v.LSO_ID+'&LKUP_LSO_TYPE_ID='+lkupId+'&process=edit" class="csui"  >'+v.APN+'</a></td>';
			 		
			 		c += '<td class="csui scrollselector" type="String" stid='+v.LSO_ID+' itype="String">&nbsp;';
			 		
			 		c += ' <input type="hidden" id='+v.LSO_ID+' name='+v.LSO_ID+' value="0"></td>';
			 		
			 	
			 		
					
			 		var lkupId= 2;
					if(v.LSO_TYPE=='O'){
						lkupId = 3;
					}
			 		
			 		
			 		c += '<td class="csui" width="1%">';
					c += '<a  title="Edit" target="lightbox-iframe" href="add.jsp?_ent=permit&_id=38&_type=admin&_typeid=17&ID='+v.LSO_ID+'&LKUP_LSO_TYPE_ID='+lkupId+'&process=edit" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>';
					
					c += '</td>';
					
					c += '<td class="csui" width="1%">';
					c += '<a  title="Manage Alias"  target="lightbox-iframe" href="alias.jsp?_ent=permit&_id=38&_type=admin&_typeid=17&ID='+v.LSO_ID+'&process=alias" ><img src="/cs/images/icons/controls/black/exclude.png" border="0"></a>';
	
					c += '</td>';
					
					c += '<td class="csui" width="1%">';
					c += '<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete" onclick="deletetype('+v.LSO_ID+');" >';
					c += '</td>';
			 		
			 		
			 		
					c += '</tr>';
			 	//	console.log(c);
			 	
					c += ' <tr style="display:none;" id="show_selector_'+v.LSO_ID+'">';
					c += ' <td  align="right" colspan="11">';
					c += ' <table class="csui" type="horizontal" id="show_selector_table_'+v.LSO_ID+'" width="100%">';
					c += ' </table>';
					c += ' </td>';
					c += '</tr>';

			 		
					
	 		});
	 		//c += '</table>';
	 		
	 		//alert(t);
	 		
	 		
	 		
	 		$("#show_selector_table_"+id).html(c);
	 		$("#show_selector_"+id).show();
	 		$('#show_bottom_'+id).val("1");
		}
		
	function deletetype(id){
		var method = "deletetype";
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
			   			  url: "<%=Config.fullcontexturl()%>/jsp/admin/custom/action.jsp?_action="+method,
			   			  dataType: 'json',		  
			   			  data: { 
			   				// feesjson : type,
			   			      _grptype : "finance",
			   			   	  _ent : "<%=ent%>",
			   			   	  _type : "<%=type%>",
			   				  _typeid : <%=typeid%>,
			   				  _id : <%=id%>,
			   				 _table : "<%=table%>",
			   				   ID : id
			   			      //mode : mode
			   			    },
			   			    success: function(output) {
			   			    		$('#tr_'+id).remove();
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
				<a target="lightbox-iframe" href="add.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&LKUP_LSO_TYPE_ID=1&process=add" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
			</td>
	
	  </tr>
</table>
</td>
</tr>
			</table>
		</div>

		
	</div>
	<div id="csuibody">
		<div id="csuimain">
			<div class="csuicontent">
			<br/><br/>
				<div id="csform_message"></div>
				
				<table class="csui_title">
					<tr>
						<td class="csui_title" nowrap>Search Address</td>
					</tr>
				</table>
				<form id="csform" action="list.jsp" ajax="no" class="form" method="post">
					<input type="hidden" name="_ent" value="<%=ent%>">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
					<input type="hidden" name="SEARCH" value="SEARCH">
					<input type="hidden" name="SEARCH_FIELDS" value="DESCRIPTION">
					<input type="hidden" name="SORT_FIELD" value="DESCRIPTION">
					<input type="hidden" name="ORDER" value="ASC">
					<input type="hidden" name="connectors" value="<%=map.getString("view")%>">
					<input type="hidden" name="connectids" value="<%=map.getString("connectids")%>">
					<input type="hidden" name="connecttype" value="<%=map.getString("connecttype")%>">
					<table class="csui" style="width: 100%">
						<tr>
							<td style="padding: 10px; font-family: Roboto Condensed, Arial, Helvetica, sans-serif; font-size: 10px; background-color: #eeeeee; text-transform: uppercase; width: 150px" id ="label_STR_NO">LSO ID</td>
							<td style="padding: 10px; font-family: Roboto Condensed, Arial, Helvetica, sans-serif; font-size: 10px; background-color: #eeeeee; text-transform: uppercase; width: 50px" id ="label_LSO_ID">&nbsp;</td>
							<td style="padding: 10px; font-family: Roboto Condensed, Arial, Helvetica, sans-serif; font-size: 10px; background-color: #eeeeee; text-transform: uppercase; width: 150px" id ="label_STR_NO">Street Number</td>
							<td style="padding: 10px; font-family: Roboto Condensed, Arial, Helvetica, sans-serif; font-size: 10px; background-color: #eeeeee; text-transform: uppercase; width: 150px">Fraction</td>
							<td style="padding: 10px; font-family: Roboto Condensed, Arial, Helvetica, sans-serif; font-size: 10px; background-color: #eeeeee; text-transform: uppercase" id ="label_STREET">Street Name</td>
							<td style="padding: 10px; font-family: Roboto Condensed, Arial, Helvetica, sans-serif; font-size: 10px; background-color: #eeeeee; text-transform: uppercase; width: 150px" id="label_UNIT">Unit</td>
						</tr>
						<tr>
							<td class="csui" style="width: 150px"><input type="text" itype="integer" name="LSO_ID" id="LSO_ID" placeholder="LSO ID" value="<%=map.getString("LSO_ID") %>" style="width: 150px; padding: 6px; border: 1px solid #cccccc" valrequired="false"/></td>
							<td class="csui" style="padding: 10px; font-family: Roboto Condensed, Arial, Helvetica, sans-serif; font-size: 10px; background-color: #eeeeee; text-transform: uppercase; width: 50px; text-align: center">OR</td>
							<td class="csui" style="width: 150px"><input type="text" itype="integer" name="STR_NO" id="STR_NO" placeholder="Street Number" value="<%=map.getString("STR_NO") %>" style="width: 150px; padding: 6px; border: 1px solid #cccccc" valrequired="false"/></td>
							<td class="csui" style="width: 150px">
									<select  id="STR_MOD" name="STR_MOD" itype="String" val="" _ent="lso" valrequired="false" ><option value="">Choose Fraction</option>
									<%for(int i=0;i<mtypes.length();i++){ %>	<option value="<%=mtypes.getJSONObject(i).getString("TEXT")%>"><%=mtypes.getJSONObject(i).getString("TEXT")%></option>		<%}%>
								</select>
							</td>
							<td class="csui">
								<select  id="LSO_STREET_ID" name="LSO_STREET_ID" itype="String" val="" _ent="lso" valrequired="false" ><option value="">Choose Street</option>
									<%for(int i=0;i<stypes.length();i++){ %>	<option value="<%=stypes.getJSONObject(i).getInt("ID")%>"><%=stypes.getJSONObject(i).getString("TEXT")%></option>		<%}%>
								</select>
							</td>
							<td class="csui" style="width: 150px"><input type="text" itype="String" name="UNIT" placeholder="Unit" style="width: 150px; padding: 6px; border: 1px solid #cccccc"/></td>
						</tr>
					</table>
					<div class="csui_divider"></div>
					<div class="csui_buttons"><input type="submit" value="search" class="search"/></div>
				</form>
				
				
				<table class="csui" type="horizontal">
				
					<thead>
						<tr>
							<td class="csui_header">ADD</td>
							<td class="csui_header">TYPE</td>
							<td class="csui_header">LSO-ID</td>
							<td class="csui_header">ADDRESS</td>
							<td class="csui_header">DESCRIPTION</td>
							<td class="csui_header">APN</td>
							<td class="csui_header" width="1%">SHOW</td>
							<td class="csui_header" width="1%">EDIT</td>
							<td class="csui_header" width="1%">ALIAS</td>
							<td class="csui_header" width="1%">DELETE</td>
						
							
						</tr>
						</thead>
				 <tbody>
						<%
							if(lso.has("ID")){
								
								JSONObject land = lso.getJSONObject("L");
								
								
							
						%>
							<tr id="tr_<%=land.getInt("LSO_ID") %>">
								<td class="csui" style="cursor:pointer;" >
								<a  title="Add" target="lightbox-iframe" href="add.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&PARENT_ID=<%=land.getInt("LSO_ID") %>&LKUP_LSO_TYPE_ID=2&process=add" >	<img src="/cs/images/icons/controls/black/add.png" border="0" style="cursor:pointer" title="Add Structure"  ></a>
								</td>
								<td class="csui" style="cursor:pointer;"   ><%=land.getString("LSO_TYPE") %></td>
								
								<td class="csui" style="cursor:pointer;" rel="<%=land.getInt("LSO_ID") %>" ><%=land.getInt("LSO_ID") %>
								</td>
							
								<td class="csui" style="cursor:pointer;"  ><%=land.getString("ADDRESS") %></td>
								<td class="csui" style="cursor:pointer;"  ><%=land.getString("DESCRIPTION") %></td>
								
								<td class="csui" style="cursor:pointer;"  ><a title="APN Edit" target="lightbox-iframe" href="addAPN.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=land.getInt("LSO_ID") %>&LSO_TYPE=<%=land.getString("LSO_TYPE") %>&process=edit" class="csui"  ><%=land.getString("APN") %></a></td>
								
								
								<td class="csui scrollselector" stid="<%=land.getInt("LSO_ID") %>" title="Show Fields">&nbsp;
								 <input type="hidden" id="show_bottom_<%=land.getInt("LSO_ID") %>" name="show_bottom_<%=land.getInt("LSO_ID") %>" value="0">
								</td>
									
								
								<td class="csui" width="1%">
								<a  title="Edit" target="lightbox-iframe" href="add.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=land.getInt("LSO_ID") %>&process=edit" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>

								</td>
								
								<td class="csui" width="1%">
									<a  title="Manage Alias"  target="lightbox-iframe" href="alias.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=land.getInt("LSO_ID") %>&process=alias" ><img src="/cs/images/icons/controls/black/exclude.png" border="0"></a>
								</td>

								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete" onclick="deletetype(<%=land.getInt("LSO_ID") %>);" >
								</td>
								
							</tr>
							 <tr style="display:none;" id="show_selector_<%=land.getInt("LSO_ID") %>">
								 <td  align="right" colspan="11">
								 <table class="csui" type="horizontal" id="show_selector_table_<%=land.getInt("LSO_ID") %>" width="100%">
								 
								 </table>
								 </td>
							</tr>
							
							
							<%
							if(lso.has("S")){
								
								JSONObject structure = lso.getJSONObject("S");
								
							
						%>
							<tr id="tr_<%=structure.getInt("LSO_ID") %>">
							
							<td class="csui" style="cursor:pointer;" align="right">
								<a  title="Add" target="lightbox-iframe" href="add.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&PARENT_ID=<%=structure.getInt("LSO_ID") %>&LKUP_LSO_TYPE_ID=3&process=add" >	<img src="/cs/images/icons/controls/black/add.png" border="0" style="cursor:pointer" title="Add Occupancy"  ></a>
							</td>
							<td class="csui" style="cursor:pointer;"   ><%=structure.getString("LSO_TYPE") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=structure.getInt("LSO_ID") %>" ><%=structure.getInt("LSO_ID") %>
								</td>
							
								<td class="csui" style="cursor:pointer;"  ><%=structure.getString("ADDRESS") %></td>
								<td class="csui" style="cursor:pointer;"  ><%=structure.getString("DESCRIPTION") %></td>
								
								<td class="csui" style="cursor:pointer;"   ><%=structure.getString("APN") %></td>
								
								
								
								<td class="csui scrollselector" stid="<%=structure.getInt("LSO_ID") %>" title="Show Fields">&nbsp;
								 <input type="hidden" id="show_bottom_<%=structure.getInt("LSO_ID") %>" name="show_bottom_<%=structure.getInt("LSO_ID") %>" value="0">
								</td>
									
								
								<td class="csui" width="1%">
								<a  title="Edit" target="lightbox-iframe" href="add.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=structure.getInt("LSO_ID") %>&process=edit" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>

								</td>
								
								<td class="csui" width="1%">
									<a  title="Manage Alias" target="lightbox-iframe" href="alias.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=structure.getInt("LSO_ID") %>&process=alias" ><img src="/cs/images/icons/controls/black/exclude.png" border="0"></a>
								</td>
								
								

								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete" onclick="deletetype(<%=structure.getInt("LSO_ID") %>);" >
								</td>
								
							</tr>
							 <tr style="display:none;" id="show_selector_<%=structure.getInt("LSO_ID") %>">
								 <td  align="right" colspan="11">
								 <table class="csui" type="horizontal" id="show_selector_table_<%=structure.getInt("LSO_ID") %>" width="100%">
								 
								 </table>
								 </td>
							</tr>
							
						<% 						
							
						}%>
						
						
						<%
							if(lso.has("O")){
								
								JSONObject occupancy = lso.getJSONObject("O");
								
							
						%>
							<tr id="tr_<%=occupancy.getInt("LSO_ID") %>">
							<td class="csui" style="cursor:pointer;" align="right"  >&nbsp;</td>
							<td class="csui" style="cursor:pointer;"   ><%=occupancy.getString("LSO_TYPE") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=occupancy.getInt("LSO_ID") %>" ><%=occupancy.getInt("LSO_ID") %>
								</td>
							
								<td class="csui" style="cursor:pointer;"  ><%=occupancy.getString("ADDRESS") %></td>
								<td class="csui" style="cursor:pointer;"  ><%=occupancy.getString("DESCRIPTION") %></td>
								<td class="csui" style="cursor:pointer;"   ><%=occupancy.getString("APN") %></td>
								<td class="csui scrollselector" stid="<%=occupancy.getInt("LSO_ID") %>" title="Show Fields">&nbsp;
								 <input type="hidden" id="show_bottom_<%=occupancy.getInt("LSO_ID") %>" name="show_bottom_<%=occupancy.getInt("LSO_ID") %>" value="0">
								</td>
									
								
								<td class="csui" width="1%">
								<a  title="Edit" target="lightbox-iframe" href="add.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=occupancy.getInt("LSO_ID") %>&process=edit" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>

								</td>
								
								<td class="csui" width="1%">
									<a  title="Manage Alias" target="lightbox-iframe" href="alias.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=occupancy.getInt("LSO_ID") %>&process=alias" ><img src="/cs/images/icons/controls/black/exclude.png" border="0"></a>
								</td>

								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete" onclick="deletetype(<%=occupancy.getInt("LSO_ID") %>);" >
								</td>
								
							</tr>
							 <tr style="display:none;" id="show_selector_<%=occupancy.getInt("LSO_ID") %>">
								 <td  align="right" colspan="11">
								 <table class="csui" type="horizontal" id="show_selector_table_<%=occupancy.getInt("LSO_ID") %>" width="100%">
								 
								 </table>
								 </td>
							</tr>
							
						<% 						
							
						}%>
							
							
							
						<% 						
							
						}%>
				</tbody>
				
				</table>
				
				
			</div>
		<div id="csuisub">
				<div class="csuisub_divider"></div>
				<div class="csuisubcontent">
					&nbsp;
				</div>
				<div class="csuisub_divider"></div>
				<div class="csui_divider"></div>
				
		</div>
	</div>




</body>
</html>

