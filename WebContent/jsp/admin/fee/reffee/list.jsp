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
	

String _userid = map.getString("_userid");
_userid = Operator.replace(_userid, "?", "&");

	String ent = map.getString("_ent");
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	if(!AdminAgent.secureAdmin(map)){
		map.redirect("../noaccess.jsp");
	}
	Timekeeper k = new Timekeeper();
	Timekeeper d = new Timekeeper();
	String fullcontexturl = CsApiConfig.getString("cs.fullcontexturl");
	String table = Table.FEETABLE;
	
	String alert="";
	
	//extras
	
	boolean connector = Operator.equalsIgnoreCase(map.getString("view"),"connector");
	String connectids = map.getString("connectids","");
	String connecttype = map.getString("connecttype");
	
	System.out.println(connecttype+"%%%%%"+connectids+"#########"+connector+"4444"+map.getString("view"));
	
	
	
	//Pagination
	int maxrows = map.getInt("MAX", 500);
	int pg = map.getInt("PAGE",1);
	String sortfield= map.getString("SORT_FIELD","NAME");
	String order= map.getString("ORDER","ASC");
	String ord= order;
	if(order.equalsIgnoreCase("DESC")){
		ord = "ASC";
	}else {
		ord="DESC";
	}
	int records = 1;

	
	
	int start = (pg-1)*maxrows;
	int end = start+maxrows;
	ArrayList<MapSet> a =  new ArrayList<MapSet>();
	ArrayList<MapSet> c =  new ArrayList<MapSet>();
	ArrayList<MapSet> la = AdminAgent.getIndex(type, typeid, id,false,map);
	MapSet l = new MapSet();
	if(la.size()>0){
		l = la.get(0);
	}
	StringBuffer url = new StringBuffer();
	StringBuffer urlsort = new StringBuffer();
	url.append("list.jsp?_token="+map.get("_token").toString()+"&_userid="+map.get("_userid").toString()+"&_ent=permit&_type=admin&_typeid="+map.get("_typeid").toString()+"&_id="+map.get("_id").toString()+"");	
	
	if(connector){
		MapSet m = AdminMap.getFeeGroupRef(map);
		c =  AdminAgent.getSubList(m);
	}
	
	
	 if(map.equalsIgnoreCase("SEARCH","SEARCH")){
		a =  AdminAgent.getFeeList(table,start,end,sortfield,order,map.getString("feesearch"),map.getString("SEARCH_FIELDS"),map.getString("start_date_st"),map.getString("exp_date_ed"));
		records= AdminAgent.getFeeListCount(table,start,end,sortfield,order,map.getString("feesearch"),map.getString("SEARCH_FIELDS"),map.getString("start_date_st"),map.getString("exp_date_ed"));
		//records =5;
		//System.out.println("SEARCHG"+map.getString("SEARCH_FIELDS"));
	//	url.append("/admin/formmgr/formEntries.jsp?SEARCH=SEARCH&FORM_ID=").append(formId).append("&CATEGORY_ID=").append(categoryId).append("&MAX=").append(maxrows).append("&Q=").append(q).append("&searchfields=").append(searchfields).append("&displayfields=").append(displayfields).append("&SORT_FIELD=").append(sortfield).append("&ORDER=").append(order);
	//	urlsort.append("/admin/formmgr/formEntries.jsp?SEARCH=SEARCH&FORM_ID=").append(formId).append("&CATEGORY_ID=").append(categoryId).append("&MAX=").append(maxrows).append("&Q=").append(q).append("&searchfields=").append(searchfields).append("&displayfields=").append(displayfields);
	} else {	
		a =  AdminAgent.getFeeList(table,start,end,sortfield,order,"","","","");
		records= AdminAgent.getFeeListCount(table,start,end,sortfield,order,"","","","");
		//records =11;
		//url.append("/admin/formmgr/formEntries.jsp?FORM_ID=").append(formId).append("&CATEGORY_ID=").append(categoryId).append("&MAX=").append(maxrows).append("&SORT_FIELD=").append(sortfield).append("&ORDER=").append(order);
		//urlsort.append("/lookupprojecttype.jsp?_type=").append(type).append("&_typeid=").append(typeid).append("&MAX=").append(maxrows);
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
		input[type=button], a.button, span.button {
		background-color: #eeeeee;
		border: 1px solid #cccccc;
		font-family: Oswald, Arial, Helvetica;
		text-transform: uppercase;
		padding: 10px;
		padding-left: 20px;
		padding-right: 20px;
		margin: 10px;
		font-size: 16px;
		font-weight: bold;
		border-radius: 5px;
		color: #000000;
		cursor: pointer;
		text-decoration: none;
	}
	
	input[type=button]:hover, a.button:hover, span.button:hover {
		background-color: #336699;
		color: #ffffff;
	}
	
	table {
	  text-align: left;
	  position: relative;
	  border-collapse: collapse; 
	}
	th {
	  background: white;
	  position: sticky;
	  top: 0; /* Don't forget this, required for the stickiness */
	  box-shadow: 0 2px 2px -1px rgba(0, 0, 0, 0.4);
	}
	
	th, td {
  padding: 0.25rem;
}


th.csui_header, th.csui_label { padding: 6px; font-family: Roboto Condensed, Arial, Helvetica, sans-serif; font-size: 10px; background-color: #eeeeee; text-transform: uppercase }
table[colnum] th.csui_header { width: 10%; white-space: nowrap }
	</style>

	<script>
		$(document).ready(function() {
			

				 $('#custom').click(function () {
			      	 $('<a title="Custom" href="<%=Config.fullcontexturl()%>/jsp/admin/custom/list.jsp?" >Friendly description</a>').fancybox({
		       		'width'				: '75%',
						'height'			: '75%',
						'autoScale'			: false,
						'transitionIn'		: 'none',
						'transitionOut'		: 'none',
						'type'				: 'iframe'
		          }).click();
		       	
					});
			
				 $(".tablesorter").tablesorter(); 
				 
				 $("#selectorall").click(function(){
					   $('input:checkbox').not(this).prop('checked', this.checked);
				});
				 
				 
				 $('#reset').click(function() {
						
						  var href = $(this).attr("href");
						    window.location.href = href;
					});
				 
				 
				 $('.scrollselector').click(function() {
						var stid = $(this).attr('stid');
						var id = $(this).attr('id');
						if ($(this).hasClass('stactive')) {
							$(this).removeClass('stactive');
							$('[rel='+stid+']').removeClass('stactive');
						}
						else {
							$(this).addClass('stactive');
							$('[rel='+stid+']').addClass('stactive');
						}
						showselector(stid,id);
					});
					

				<%if(Operator.hasValue(connectids)){ %>
				
				 <%for(int i=0;i<c.size();i++){
					 	MapSet r = c.get(i);
					 	
					%>
					var id = "<%= r.getString("FIELD_GROUPS_ID")%>";
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
				 $("#searchButton").hide();
				 $('input[itype=date]').datetimepicker({
						timepicker:false,
						format:'m/d/Y'
				 }); 
		});
		
		function showselector(stid,id){
			if($('#show_bottom_'+stid).val()=="1"){
				$("#show_selector_"+stid).hide();
				$('#show_bottom_'+stid).val("0");
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
	   			   	  FEE_ID : id,
	   				 _typeid : "<%=typeid%>"
	   			      //mode : mode
	   			    },
	   			    success: function(output) {
	   			    		showselectorui(stid,output);
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
			c += '<td class="csui_header pymnt"  nowrap>GROUP</td>';
			c += '<td class="csui_header pymnt"  nowrap>START DATE</td>';
			c += '<td class="csui_header pymnt"  nowrap>EXP DATE</td>';
			c += '<td class="csui_header pymnt"  nowrap>UPDATED BY</td>';
			c += '<td class="csui_header pymnt" nowrap>UPDATED</td>';
			//c += '<td class="csui_header pymnt">PAYEE</td>';
			//c += '<td class="csui_header pymnt" width="1%" nowrap>PAID</td>';
			//c += '<td class="csui_header pymnt" width="1%" nowrap>REVERSE AMOUNT</td>';
			//c += '<td class="csui_header pymnt" width="1%" nowrap>REVERSE METHOD</td>';
		/* 	c += '<td class="csui_header pymnt" width="1%">ACCOUNT</td>';
			c += '<td class="csui_header pymnt" width="1%">COPY</td>';
			c += '<td class="csui_header pymnt" width="1%">ASSOCIATE</td>';
			c += '<td class="csui_header pymnt" width="1%" >Division IN </td>';
			c += '<td class="csui_header pymnt" width="1%" >Division NOT-IN </td>'; */
			c += '</tr>';
			//c += '<table class="sortable">';
	 		var t = 1;	
	 		$.each(output, function(k,v) {
	 				c+= '<tr class="csui"  style="cursor:pointer;" >';
	 				c += '<td class="csui pymnt" type="String"  itype="String"  nowrap>'+t+++'</td>';
			 		c += '<td class="csui pymnt" type="String"  itype="String"  nowrap>'+v.GROUP_NAME+'</td>';
			 		var st =  v.C_START_DATE;
			 		var find = ' 00:00:00.0';
			 		st = st.replace(new RegExp(find, 'g'), "");
			 		c += '<td class="csui pymnt" type="String" itype="String"  nowrap>'+st+'</td>';
			 		c += '<td class="csui pymnt" type="String" itype="String"  nowrap>'+v.C_EXPIRATION_DATE+'</td>';
			 		/* c += '<td class="csui pymnt" type="String" itype="String">'+v.FORMULA_NAME+'</td>';
			 		c += '<td class="csui pymnt" type="String" itype="String" width="1%" nowrap>'+v.REQ+'</td>'; */
			 		c += '<td class="csui pymnt" type="String" itype="String" nowrap>'+v.UPDATED+'</td>';
			 		c += '<td class="csui pymnt" type="String" itype="String" nowrap>'+v.C_UPDATED_DATE+'</td>';
			 		
					c += '</tr>';
			 	//	console.log(c);
			 		
					
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
			   			  url: "<%=Config.fullcontexturl()%>/jsp/admin/fee/action.jsp?_action="+method,
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

	
	function appendrefs(id){
		var name = $('#refname_'+id).val();
		var method = "addref";
		var c = '';
		c += '<table class="csui" type="horizontal" id="cr_'+id+'" style="cursor:pointer;" title="Drag to sort">';
		c += '<tr id="'+id+'" class="ref_sortable" >';
		c += '	<td class="csui" width="1%">';
		c += '<img src="/cs/images/icons/controls/black/delete.png" border="0" onclick="deleteref('+id+');" >';
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
 				  FEE_GROUP_ID : id
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
			   				   //REF_ID : id,
			   				   FEE_GROUP_ID : fieldgroupsid
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
	
 	
	
	function deletefield(id){
		var method = "deletefeeonly";
		
		swal({  
				title: "Are you sure? You want to delete this record!",   
				text: "You need to enter the expiration date MM/DD/YYYY or current date will become the expiration date",   
				type: "input",   
				showCancelButton: true,   
				confirmButtonColor: "#DD6B55",   
				
				
				animation: "slide-from-top",
				closeOnConfirm: true,   
				
				inputValue: "<%=k.getString("MM/DD/YYYY")%>"
			}, 
			function(inputValue){
				var expdate=""
				if(inputValue==""){
					expdate="<%=k.getString("MM/DD/YYYY")%>";
				}else {
					expdate = inputValue;
				}
				
				$.ajax({
		   			  type: "POST",
		   			  url: "<%=Config.fullcontexturl()%>/jsp/admin/fee/action.jsp?_action="+method,
		   			  dataType: 'json',		  
		   			  data: { 
		   				  _type : "<%=type%>",
		   				  _typeid : <%=typeid%>,
		   				  _id : <%=id%>,
		   				   ID : id,
		   				   EXPIRATION_DATE: expdate
		   			      //mode : mode
		   			    },
		   			    success: function(output) {
		   			    		//$('#tr_'+id).remove();
		   			    		swal("Deleted!", "The record has been expired.", "success");   
		   			    		
		   			    },
		   		    error: function(data) {
		   		    	swal("Problem while perfoming delete looks like the server is busy");  
		   		    }
	   			});		
				
			
			});
	}
	
		
	</script>

</head>
<body  alert="<%= alert %>">
<div id="loader"></div>

	<div id="csuicontrols">
		<div id="csuicontrol" class="csuicontrol warning">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td align="left" class="csuicontrol">
					
						<a target="_self" href="../../?_ent=<%=ent%>&_userid=<%=_userid%>&_id=5&_type=admin&_typeid=5" ><img src="/cs/images/icons/controls/white/back.png" border="0"></a>
						&nbsp;
					<%= l.getString("NAME") %></td>
					<td align="right">
					<table class="csui_tools">
 					 <tr>
 					 
 					 	
						<td class="csui_tools">
						<a target="_blank" href="export.jsp"  title="Export"><img src="/cs/images/icons/controls/black/csv.png" border="0" ></a>
						</td>
						
						<td class="csui_tools">
						<a target="lightbox-iframe" href="add.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
						</td>
						

 						 </tr>
					</table>
					</td>
				</tr>
			</table>
		</div>

		<%-- <div id="csuisubcontrol" class="csuisubcontrol <%= alert %>">CONNECTORS</div> --%>
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
				<input type="hidden" name="SEARCH_FIELDS" value="NAME,KEY_CODE,BUDGET_UNIT,ACCOUNT_NUMBER,FUND,FORMULA_NAME">
				<!-- <input type="hidden" name="SEARCH_START_DATE" value="SEARCH_START_DATE"> -->
				<input type="hidden" name="connectors" value="<%=map.getString("view")%>">
				<input type="hidden" name="connectids" value="<%=map.getString("connectids")%>">
				<input type="hidden" name="connecttype" value="<%=map.getString("connecttype")%>">
				
				<table align="right">
					
					<tr>
						<!-- <td width="50%" class="cssearch_date">
						
						<input type="submit" name="action" value="Search" id="searchButton"  class="csui_button" >
						 </td>
						 <td width="50%" class="cssearch_date">
						 </td> -->
						<td class="csui"  align="right">
							<%-- <input name="START_DATE" id="START_DATE"  type="text" itype="startdate" value="<%=map.getString("START_DATE") %>" maxchar="100" placeholder="&nbsp;Start Date"> --%>						    
							<%-- <input name="EXPIRATION_DATE" id="EXPIRATION_DATE"  type="text" itype="expdate" value="<%=map.getString("EXPIRATION_DATE") %>" maxchar="100" placeholder="&nbsp;Exp Date"> --%>
							<%-- <input name="SQ" class="searchbox" id="search" type="text" value="<%=map.getString("SQ") %>"  placeholder="&nbsp;Search" /> --%>
							<input type="text" itype="date" class="searchbox" id="start_date_st" name="start_date_st" value="<%=map.getString("start_date_st") %>" placeholder="start date" ad="T00:00:00Z" >
							<input type="text" itype="date" class="searchbox" id="exp_date_ed" name="exp_date_ed" value="<%=map.getString("exp_date_ed") %>" placeholder="exp date" ad="T00:00:00Z" >
							<input name="feesearch" class="searchbox" id="feesearch" type="text" value="<%=map.getString("feesearch") %>"  placeholder="&nbsp;Search" />
							
							
							
							
							<input type="submit" name="action" value="Search" id="searchButton"  class="csui_button" >
						</td>
						
						
					</tr>
					<tr>
					<td class="csui"  align="right">
					
							<input name="Reset" class="csui_button" id="reset" type="button" value="Reset" href="list.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>" />
							<input name="Go" class="button" id="go" type="submit" value="Go"   />
							</td>
					</tr>					
					<%if(records>maxrows){ %>
					<tr>
						<td class="csui"  type="String" itype="String" alert="" align="right">
							Showing (<%=maxrows %> of <%=records %>)
						</td>
					</tr>
					<%} else {%>
					<tr>
						<td class="csui"  type="String" itype="String" alert="" align="right">
							Total Records (<%=records %>)
						</td>
					</tr>
					
					<%} %>
				</table>
				
				
				<div class="csui_divider"></div>
				
				   <table class="csui_title" alert="warning">
						<tr>
						<td class="csui_title"><%= l.getString("NAME") %></td>
						</tr>
					</table>
			 	 	 	
			 	 	 	<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr >
							<!-- to do pagination sorting adjuster if needed Sunil
								<td class="csui_header" style="background-color: #eeeeee"  nowrap><a class="csui" title="sort" href="&SORT_FIELD=TYPE&ORDER=">TYPE &nbsp; &nbsp; </a>
							-->
							<th class="csui_header">NAME</td>
							<th class="csui_header">START DATE</td>
							<th class="csui_header">EXP DATE</td>
							<th class="csui_header">FORMULA</td>
							<th class="csui_header">KEY CODE</td>
							<th class="csui_header">ORG CODE</td>
							<th class="csui_header">OBJ CODE</td>
							<th class="csui_header">UPDATED BY</td>
							<th class="csui_header">UPDATED</td>
							
							<th class="csui_header">INPUT1</td>
							<th class="csui_header">INPUT2</td>
							<th class="csui_header">INPUT3</td>
							<th class="csui_header">INPUT4</td>
							<th class="csui_header">INPUT5</td>
							<th class="csui_header">UPDATABLE</td>
							<th class="csui_header" width="1%">&nbsp;</td>
							<th class="csui_header" width="1%">&nbsp;</td>
							<th class="csui_header"  width="1%">&nbsp;</td>
							<!-- <td class="csui_header"  width="1%">&nbsp;</td> -->
						
							
						</tr>
						</thead>
						
						 <tbody>
						<%for(int i=0;i<a.size();i++){
							MapSet r = a.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
								
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("NAME") %>
								 <input type="hidden" id="refname_<%=r.getString("ID") %>" name="refname_<%=r.getString("ID") %>" value="<%=r.getString("NAME") %>">
								</td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("C_START_DATE") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("C_EXPIRATION_DATE") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("FORMULA_NAME") %></td>
								
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("KEY_CODE") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("BUDGET_UNIT") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("ACCOUNT_NUMBER") %></td>
								
								
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("UPDATED") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("C_UPDATED_DATE") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("INPUT1") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("INPUT2") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("INPUT3") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("INPUT4") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("INPUT5") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("UPDATABLE") %></td>
							 	<td class="csui scrollselector" id="<%=r.getString("ID") %>" stid="<%=r.getString("REF_FEE_FORMULA_ID") %>" title="Show Fields">&nbsp;
								 <input type="hidden" id="show_bottom_<%=r.getString("REF_FEE_FORMULA_ID") %>" name="show_bottom_<%=r.getString("REF_FEE_FORMULA_ID")%>" value="0">
								</td>
								<td class="csui" width="1%">
								<a target="lightbox-iframe" href="add.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&_groupid=<%=r.getString("FEE_GROUP_ID")%>&FEE_ID=<%=r.getString("FEE_ID") %>&ID=<%=r.getString("REF_FEE_FORMULA_ID") %>&process=edit" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>
								</td>
								<td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete" onclick="deletetype(<%=r.getString("ID") %>);" >
								</td>
								<%-- <td class="csui" width="1%">
									<img src="/cs/images/icons/controls/black/delete.png" border="0" style="cursor:pointer" title="Delete" onclick="deletetype(<%=r.getString("ID") %>);" >
								</td> --%>
								
								
								
								
								
							</tr>
							 <tr style="display:none;" id="show_selector_<%=r.getString("REF_FEE_FORMULA_ID") %>">
								 <td  align="right" colspan="15">
								 <table class="csui" type="horizontal" id="show_selector_table_<%=r.getString("REF_FEE_FORMULA_ID") %>" width="100%">
								 
								 </table>
								 </td>
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
						
						
					
				</form>		
				
			</div>
		

		
	</div>




</body>
</html>

