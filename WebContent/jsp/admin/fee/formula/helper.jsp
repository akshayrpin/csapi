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
String fullcontexturl = CsApiConfig.getString("cs.fullcontexturl");
	


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
		
		
		
		
	
	
	
		
	</script>

</head>
<body alert="">
<div id="loader"></div>

	<div id="csuicontrols">
		<div id="csuicontrol" class="csuicontrol warning">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td align="left" class="csuicontrol">HELPER</td>
					<td align="right">
					&nbsp;
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
				
				<table align="right">
				
				
				
				
				<div class="csui_divider"></div>
				
				   <table class="csui_title" alert="warning">
						<tr>
						<td class="csui_title">Formula syntax</td>
						</tr>
					</table>
			 	 	 	
			 	 	 	<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
							
							
							<!-- to do pagination sorting adjuster if needed Sunil
								<td class="csui_header" style="background-color: #eeeeee"  nowrap><a class="csui" title="sort" href="&SORT_FIELD=TYPE&ORDER=">TYPE &nbsp; &nbsp; </a>
							-->
							<td class="csui_header">PARAMETERS</td>
							<td class="csui_header">DESCRIPTION</td>
							
						
							
						</tr>
						</thead>
						
						 <tbody>
					
							<tr>
								<td class="csui" >o </td>
								<td class="csui" >End result variable Ex: o = 1; results with output 1</td>
							</tr>
							
							<tr>
								<td class="csui" >input 1-5</td>
								<td class="csui" >You can use input1,input2,input3,input4,input5
								which are available . Based on the set values in configuations for this params. You can create a formula Ex: o = input1 + input2 . You can use any aritmetic and logic symbols in between</td>
							</tr>
							
							<tr>
								<td class="csui" >valuation</td>
								<td class="csui" >Ex: o = input1 * valuation or o = valuation . </td>
							</tr>
							
							<tr>
								<td class="csui" >feename</td>
								<td class="csui" >used for adding different fee names.  Ex: o = ( feename[Electrical Permit] +  feename[MEP Trade Plan Review] )* input1 . </td>
							</tr>
							
							<tr>
								<td class="csui" >subtotal</td>
								<td class="csui" >Calculataes the sum of fee group total and you can create a formula based on it. Ex: o = input1 + subtotal[FEE_GROUP_NAME] . </td>
							</tr>
							
							<tr>
								<td class="csui" >total</td>
								<td class="csui" >Calculataes the sum of all fee group totals and you can create a formula based on it. Ex: o = input1 + total . </td>
							</tr>
							
							<tr>
								<td class="csui" >NAMEVALUE</td>
								<td class="csui" >configure the name value once configured.  Ex: o = input1 + NAMEVALUE[cpi] . </td>
							</tr>
							
						</tbody>
						
						</table>
						
						
						</div>
						
						
					
				</form>		
				
			</div>
		
		
	</div>




</body>
</html>

