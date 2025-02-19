


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
	String table = Table.LKUPFORMULATABLE;
	String alert="";
	String ent = map.getString("_ent");
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	int editId = map.getInt("ID");
	
	String process = map.getString("process","");
	System.out.println(process);
	
	ArrayList<HashMap<String,String>> a =  new ArrayList<HashMap<String,String>>();
	ArrayList<MapSet> la = AdminAgent.getIndex(type, typeid, id,false,map);

	MapSet l = new MapSet();
	if(la.size()>0){
		l = la.get(0);
	}
	
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 if(editId>0){
			MapSet u =  AdminMap.getFeeFormula(map);
			 if(map.getInt("present")>0){
				u.add("ORIGINAL_ID",u.getInt("ID"));
				result = AdminAgent.saveType(u,table);
				if(result){
					int newFormulaId = AdminAgent.getRecordInserted("select MAX(ID) as ID  from "+table+" WHERE ORIGINAL_ID = "+u.getInt("ORIGINAL_ID"));
					System.out.println(u.getString("EXPIRATION_DATE")+ "NEW ID FORMULA FOR RETAIN ="+newFormulaId);
					if(newFormulaId>0){
						
						String forumlaExpire = AdminMap.forumlaExpire(u.getString("EXPIRATION_DATE"), u.getInt("ID"), newFormulaId);
						result = AdminAgent.saveType(forumlaExpire);		
						
						String createsetquery = AdminMap.forumlaCreateSet(newFormulaId,u);
						result = AdminAgent.saveType(createsetquery);		
						
						String formulaUpdateForStart = AdminMap.formulaUpdateForStart(u.getInt("ID"), newFormulaId, u.getInt("UPDATED_BY"));
						result = AdminAgent.saveType(formulaUpdateForStart);		
					
						//u.remove("ORIGINAL_ID");
						//u.add("ORIGINAL_ID",newFormulaId);
						u.remove("EXPIRATION_DATE");
						
						//result = AdminAgent.updateType(u,table);
						result = AdminAgent.saveType("UPDATE "+table+"  SET ORIGINAL_ID=  "+newFormulaId+" WHERE ID  ="+u.getInt("ID"));
						result = AdminAgent.saveType("UPDATE "+table+"  SET ORIGINAL_ID=  "+newFormulaId+" WHERE ID in (SELECT ID FROM "+table+" WHERE ORIGINAL_ID  ="+u.getInt("ID")+" ) ");
						result = AdminAgent.saveType("UPDATE "+table+"  SET ORIGINAL_ID=  0 WHERE ID  ="+newFormulaId);
					}
					
				}
				 
			 }else {
			 	result = AdminAgent.updateType(u,table);
			 
			 }
			 
			 
		 }else {
			 result = AdminAgent.saveType(AdminMap.getFeeFormula(map),table);
		 }
		 
		 
	 }
	 if(map.equalsIgnoreCase("_action","CANCEL")){
			result = true;
	 }
	 
	ArrayList<MapSet> fields = new ArrayList<MapSet>();
	MapSet v = new MapSet();
	int present = 0;
	String maxstartdate = "";
	if(editId>0){
		v = AdminAgent.getType(editId,table);
		fields = AdminAgent.getList(AdminMap.getFormulaHistory(editId));
		MapSet vc = AdminAgent.getType("select  COUNT(*) as PRESENT from REF_FEE_FORMULA R LEFT OUTER JOIN STATEMENT_DETAIL SD ON R.ID=SD.REF_FEE_FORMULA_ID WHERE  LKUP_FORMULA_ID ="+editId+" AND SD.ID IS NOT NULL");
		present = vc.getInt("PRESENT");		
		
		MapSet vcm = AdminAgent.getType("select  MAX(START_DATE) as START_DATE  from REF_FEE_FORMULA R LEFT OUTER JOIN STATEMENT_DETAIL SD ON R.ID=SD.REF_FEE_FORMULA_ID WHERE  LKUP_FORMULA_ID ="+editId+" AND SD.ID IS  NULL AND START_DATE > getDate()");
		maxstartdate = vcm.getString("START_DATE");			
	}
	
	//present = 1;
	


%>
<html>
<head>

	<title>City Smart- Admin</title>
	
	<link rel="stylesheet" type="text/css" href="<%=Config.fullcontexturl() %>/tools/codemirror/codemirror.css">
	
	<script type="text/javascript" src="<%=Config.fullcontexturl() %>/tools/codemirror/codemirror.js"></script>
			<script type="text/javascript" src="<%=Config.fullcontexturl() %>/tools/codemirror/clike.js"></script>
			
	<style>.CodeMirror {border: 2px inset #dee;}</style>
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
			
			 var javaEditor = CodeMirror.fromTextArea(document.getElementById("java-code"), {
			        lineNumbers: true,
			        matchBrackets: true,
			        mode: "text/x-java",
			        lineWrapping: true 
			       
			      });
			 
			 javaEditor.setSize(800, 600);
			
			<% if(result){%>
				
				window.parent.$("#csform").submit();
		
				parent.$.fancybox.close();
				
				<% }%>
				
			<% if(editId>0){%>
				 
				 var d = new Date();  
					var tomo = new Date();
					tomo.setDate(d.getDate()+1);

					$('input[itype=newdate]').datetimepicker({
						timepicker:false,
						format:'Y/m/d',
						minDate: tomo
					});
				 
				 
			<% }%>
			
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
		
		function sortrefs(order){
			var method = "sortfields";
			var find = 'tr_';
			order = order.replace(new RegExp(find, 'g'), "");
			$.ajax({
	 			  type: "POST",
	 			  url: "action.jsp?_action="+method,
	 			  dataType: 'json',		  
	 			  data: { 
	 				  _type : "<%=type%>",
	 				  _typeid : <%=typeid%>,
	 				 _id : <%=id%>,
	 				 LIBRARY_GROUP_ID : <%=editId%>,
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
			var method = "deletefield";
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
				   			  url: "<%=Config.fullcontexturl()%>/jsp/admin/library/action.jsp?_action="+method,
				   			  dataType: 'json',		  
				   			  data: { 
				   				  _type : "<%=type%>",
				   				  _typeid : <%=typeid%>,
				   				  _id : <%=id%>,
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
	   			   	   ID : id,
	   				 _typeid : "<%=typeid%>"
	   			      //mode : mode
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
	 		c += '<td class="csui_header pymnt" width="1%">GROUP NAME</td>';
			c += '<td class="csui_header pymnt" width="1%" nowrap>FEE NAME</td>';
			c += '<td class="csui_header pymnt" width="1%" nowrap>START DATE</td>';
			c += '<td class="csui_header pymnt" width="1%" nowrap>EXP DATE</td>';
			c += '<td class="csui_header pymnt" width="1%" nowrap>FORMULA</td>';
			c += '<td class="csui_header pymnt" width="1%" nowrap>REQ</td>';
			c += '<td class="csui_header pymnt" width="1%" nowrap>UPDATED BY</td>';
			c += '<td class="csui_header pymnt" width="1%" nowrap>UPDATED</td>';
			//c += '<td class="csui_header pymnt">PAYEE</td>';
			//c += '<td class="csui_header pymnt" width="1%" nowrap>PAID</td>';
			//c += '<td class="csui_header pymnt" width="1%" nowrap>REVERSE AMOUNT</td>';
			//c += '<td class="csui_header pymnt" width="1%" nowrap>REVERSE METHOD</td>';
		//	c += '<td class="csui_header pymnt" width="1%">ASSOCIATE</td>';
			//c += '<td class="csui_header pymnt" width="1%" >Terriotry IN </td>';
		//	c += '<td class="csui_header pymnt" width="1%" >Terriotry NOT-IN </td>';
			c += '</tr>';
			//c += '<table class="sortable">';
	 		var t = 1;	
	 		$.each(output, function(k,v) {
	 				c+= '<tr class="csui"  style="cursor:pointer;" >';
	 				c += '<td class="csui pymnt" type="String"  itype="String" width="1%" nowrap>'+t+++'</td>';
	 				c += '<td class="csui pymnt" type="String"  itype="String" width="1%" nowrap>'+v.GROUP_NAME+'</td>';
			 		c += '<td class="csui pymnt" type="String"  itype="String" width="1%" nowrap>'+v.NAME+'</td>';
			 		var st =  v.START_DATE;
			 		var find = ' 00:00:00.0';
			 		st = st.replace(new RegExp(find, 'g'), "");
			 		c += '<td class="csui pymnt" type="String" itype="String" width="1%" nowrap>'+st+'</td>';
			 		c += '<td class="csui pymnt" type="String" itype="String" width="1%" nowrap>'+v.C_EXPIRATION_DATE+'</td>';
			 		c += '<td class="csui pymnt" type="String" itype="String">'+v.FORMULA_NAME+'</td>';
			 		c += '<td class="csui pymnt" type="String" itype="String" width="1%" nowrap>'+v.REQ+'</td>';
			 		c += '<td class="csui pymnt" type="String" itype="String" width="1%" nowrap>'+v.UPDATED+'</td>';
			 		c += '<td class="csui pymnt" type="String" itype="String" width="1%" nowrap>'+v.C_UPDATED_DATE+'</td>';
			 		
			 	
			 		
			 		
			 		//c += '<td class="csui pymnt" type="String" itype="String" align="center" width="1%" nowrap><a title="Associate" target="lightbox-iframe" href="'+u+'" ><img src="/cs/images/icons/controls/'+associatecolor+'/copy.png" border="0"></a></td>';
			 		//c += '<td class="csui pymnt" type="String" itype="String" align="center" width="1%" nowrap><a title="Divisions -in" target="lightbox-iframe" href="'+tinn+'" ><img src="/cs/images/icons/controls/'+inncolor+'/divisionsinclude.png" border="0"></a></td>';
			 		//c += '<td class="csui pymnt" type="String" itype="String" align="center" width="1%" nowrap><a title="Divisions -other" target="lightbox-iframe" href="'+tout+'" ><img src="/cs/images/icons/controls/'+outcolor+'/divisionsexclude.png" border="0"></a></td>';
					c += '</tr>';
			 	//	console.log(c);
			 		
					
	 		});
	 		//c += '</table>';
	 		
	 		//alert(t);
	 		
	 		
	 		
	 		$("#show_selector_table_"+id).html(c);
	 		$("#show_selector_"+id).show();
	 		$('#show_bottom_'+id).val("1");
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
											<a target="lightbox-iframe" href="helper.jsp" title="help"><img src="/cs/images/icons/controls/black/help.png" border="0"></a>
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
						<td align="left" id="title">FORMULA</td>
						<td align="right" id="subtitle">&nbsp;</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form id="csform" class="form" ajax="no" action="add.jsp" method="post">
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
					<input type="hidden" name="ID" value="<%=editId%>">
					<input type="hidden" name="tabletype" value="single">
					<input type="hidden" name="present" value="<%=present%>">
					<table class="csui_title">
						<tr>
							<td class="csui_title">FORMULA</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
							<tr>
								<td class="csui_label" colnum="2" alert="">NAME</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4"><input name="NAME" type="text" itype="text" value="<%=v.getString("NAME") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							<tr>
								<td class="csui_label" colnum="2" alert="">DESCRIPTION</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4"><input name="DESCRIPTION" type="text" itype="text" value="<%=v.getString("DESCRIPTION") %>" valrequired="true" maxchar="10000"></td>
							</tr>
							
							
								<tr>
								<td class="csui_label" colnum="2" alert="">DEFINITION</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4">
								<textarea id="java-code" name="DEFINITION" ><%=v.getString("DEFINITION") %></textarea></td>
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
							
							<%if(present>0 && !Operator.equalsIgnoreCase(process, "view")){ %>
							
							<tr>
								<td class="csui_label" colnum="2" alert="">NEW START DATE</td>
								<td class="csui" colnum="2" type="String" itype="String" alert="">
								<input name="NEW_START_DATE" id="NEW_START_DATE"  type="text" itype="newdate" value="" valrequired="true" maxchar="10000"><div id="new_start_dt"></div>
								
								
								</td>
								<td class="csui_label" colnum="2" alert="" colspan="2"><div> 
								<font color="red">* Note Editing the formula will recreate the formulas and associate the fees connected in all the groups</font>
								<%if(Operator.hasValue(maxstartdate)){ %></br><font color="red">* Also there are fee which starts after <%=maxstartdate %> which will be directly updated  by the edited formula If the new start date is is less than the actual start date</font><%} %>
								</div></td>
								
								
							</tr>
							<%} %>
					</table>

					<div class="csui_divider">
					
				
						
						
						
					
			</div>
			
			<div class="csui_buttons">
			<input type="submit" name="_action" value="cancel" class="csui_button">&nbsp;
				<%if(!Operator.equalsIgnoreCase(process, "view")){ %>
					<input type="submit" name="_action" value="save" class="csui_button">
				<%} %>
			</div>
			
				</form>
				
				
				
				<%if(editId>0 && fields.size()>0){ %>
				
				<table class="csui_title">
						<tr>
							<td class="csui_title">HISTORY</td>
							<td class="csui_title">
								<a target="lightbox-iframe" href="field/add.jsp?_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&_groupid=<%=editId%>&_groupname=<%=v.getString("GROUP_NAME") %>" ><img src="/cs/images/icons/controls/white/add.png" border="0"></a>
							</td>
						

 						 </tr>
							
							
					
				</table>
				<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							
							
							
							<!-- to do pagination sorting adjuster if needed Sunil
								<td class="csui_header" style="background-color: #eeeeee"  nowrap><a class="csui" title="sort" href="&SORT_FIELD=TYPE&ORDER=">TYPE &nbsp; &nbsp; </a>
							-->
							<td class="csui_header">ID</td>
							<td class="csui_header">NAME</td>
							<td class="csui_header">DESCRIPTION</td>
							<td class="csui_header">ORIGINAL ID</td>
							<td class="csui_header">UPDATED BY</td>
							<td class="csui_header">UPDATED</td>
							<td class="csui_header" width="1%">&nbsp;</td>
							<td class="csui_header" width="1%">&nbsp;</td>
						
							
						</tr>
						</thead>
						
						 <tbody>
						<%for(int i=0;i<fields.size();i++){
							MapSet r = fields.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("ID") %></td>
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("NAME") %>
								 <input type="hidden" id="refname_<%=r.getString("ID") %>" name="refname_<%=r.getString("ID") %>" value="<%=r.getString("NAME") %>">
								 
								</td>
								<td class="csui"  style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("DESCRIPTION") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("ORIGINAL_ID") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("UPDATED") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("C_UPDATED_DATE") %></td>
								<td class="csui scrollselector" stid="<%=r.getString("ID") %>" title="Show Fields">&nbsp;
								 <input type="hidden" id="show_bottom_<%=r.getString("ID") %>" name="show_bottom_<%=r.getString("ID") %>" value="0">
								</td>
								<td class="csui" width="1%">
									<a target="lightbox-iframe" href="add.jsp?_ent=permits&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&ID=<%=r.getString("ID") %>&process=view" ><img src="/cs/images/icons/controls/black/edit.png" border="0"></a>
								</td>
								
								
							</tr>
							 <tr style="display:none;" id="show_selector_<%=r.getString("ID") %>">
								 <td  align="right" colspan="11">
								 <table class="csui" type="horizontal" id="show_selector_table_<%=r.getString("ID") %>" width="100%">
								 
								 </table>
								 </td>
							</tr>
							
						<%} %>
						</tbody>
						
						</table>
					<%}%>
				
				
			</div>
		</div>
	</div>






</body>
</html>
