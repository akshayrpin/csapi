<%@page import="alain.core.security.Token"%>
<%@page import="csapi.impl.print.BatchRunnable"%>
<%@page import="csapi.impl.log.LogAgent"%>
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
	int totalcount = 200;
	String processId = "dotaccount";
	if(!AdminAgent.secureAdmin(map)){
		map.redirect("../noaccess.jsp");
	}

	Timekeeper d = new Timekeeper();
	String fullcontexturl = CsApiConfig.getString("cs.fullcontexturl");
	ArrayList<MapSet> la = AdminAgent.getIndex(type, typeid, id,false,map);
	MapSet l = new MapSet();
	if(la.size()>0){
		l = la.get(0);
	}
	
	ArrayList<MapSet> a =  new ArrayList<MapSet>();
	
	if(map.equalsIgnoreCase("_action","Print")){
			BatchRunnable.printBatch(processId);
	 }
	
	if(Operator.hasValue(processId)){
		String command = "select * from LOG_THREAD_PROGRESS WHERE PROCESS_ID= '"+processId+"'";
		a = AdminAgent.getList(command);
	}
	
	
	
	

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
			
			$("#pbtn").click(function(){
		        for(var i=0; i<800; i++)
		            $("progress").animate({ value: "+=10" }, 1);
		    })
				 
		});
		
		
	
		
	</script>

</head>
<body>
<div id="loader"></div>

	<div id="csuicontrols">
		<div id="csuicontrol" class="csuicontrol warning">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td align="left" class="csuicontrol"><%= l.getString("NAME") %></td>
					<td align="right">
					&nbsp;
					</td>
				</tr>
			</table>
		</div>

		<div id="csuisubcontrol" class="csuisubcontrol">CONNECTORS</div>
	</div>
	<div id="csuibody">
		<div id="csuimain">
			<div class="csuicontent">
			<br/><br/>
				<div id="csform_message"></div>
				<form id="csform" action="batch.jsp" method="post">
			
				
				<input type="hidden" name="connectors" value="<%=map.getString("view")%>">
				<input type="hidden" name="connectids" value="<%=map.getString("connectids")%>">
				<input type="hidden" name="connecttype" value="<%=map.getString("connecttype")%>">
				<input type="hidden" name="_ent" value="<%=ent%>">
				<input type="hidden" name="_type" value="<%=type%>">
				<input type="hidden" name="_typeid" value="<%=typeid%>">
				<input type="hidden" name="_id" value="<%=id%>">
				<table align="right">
					
					<tr>
						<td class="search"  type="String" itype="String" alert="" align="right">
							<input name="SQ" class="searchbox" id="search" type="text" itype="text" value="<%=map.getString("SQ") %>"  placeholder="&nbsp;Search Batch" maxchar="100" >
							
						</td>
					</tr>
					
				
				</table>
				
				
				<div class="csui_divider"></div>
				
				   <table class="csui_title" alert="warning">
						<tr>
						<td class="csui_title"><%= l.getString("NAME") %></td>
						</tr>
					</table>
			 	 	 	
			 	 	 	<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							<td class="csui">There are <%=totalcount %> in the Queue</td>
							<td class="csui" align="right"><input type="submit" name="_action" id="pbtn" value="Print" class="csui_button">&nbsp;</td>
						</tr>
						
						<tr>
						<td>
						<progress  min="0" max="10000" value="0"></progress>
						
						</td>
						</tr>
						</thead>
						
						 <tbody>
						<%for(int i=0;i<a.size();i++){
							MapSet r = a.get(i);
						%>
							<tr id="tr_<%=r.getString("ID") %>">
								<td class="csui" style="cursor:pointer;" rel="<%=r.getString("ID") %>" ><%=r.getString("PROCESS_ID") %>
								 <input type="hidden" id="refname_<%=r.getString("ID") %>" name="refname_<%=r.getString("ID") %>" value="<%=r.getString("PROCESS_ID") %>">
								</td>
							
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("PERCENT_COMPLETE") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("TITLE") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("DESCRIPTION") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("RESPONSE") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("CREATED_DATE") %></td>
								<td class="csui" style="cursor:pointer;"  rel="<%=r.getString("ID") %>" ><%=r.getString("UPDATED_DATE") %></td>
							</tr>
														
						<%} %>
						</tbody>
						
						</table>
						
						</div>
						
						
					
				</form>		
				
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

