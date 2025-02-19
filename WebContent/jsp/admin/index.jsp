<%@page import="alain.core.utils.Logger"%>
<%@page import="csapi.security.AuthorizeToken"%>
<%@page import="alain.core.security.Token"%>
<%@page import="alain.core.utils.Operator"%>
<%@page import="alain.core.utils.Config"%>
<%@page import="alain.core.utils.MapSet"%>
<%@page import="csapi.impl.admin.AdminAgent"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="csapi.utils.CsApiConfig"%>
<%@page import="alain.core.utils.Cartographer"%>
<% 

	Cartographer map = new Cartographer(request,response);
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id");
	String fullcontexturl = CsApiConfig.getString("cs.fullcontexturl");
	
	if(!AdminAgent.secureAdmin(map)){
		map.redirect("noaccess.jsp");
	}
	String _userid = map.getString("_userid");
	_userid = Operator.replace(_userid, "?", "&");
	System.out.println("INDEX PAGE ******************"+_userid);

	Token t = Token.retrieve(map.getString("_token"), map.getRemoteIp());
	System.out.println(t.isAdmin());
	ArrayList<MapSet> a = AdminAgent.getIndex(type, typeid, id, true, t);
	
%>

<html>
<head>
<title> ADMIN </title>

<link href='https://fonts.googleapis.com/css?family=Oswald:300,700' rel='stylesheet' type='text/css'>
<link href='https://fonts.googleapis.com/css?family=Armata' rel='stylesheet' type='text/css'>
<link href='https://fonts.googleapis.com/css?family=Roboto+Condensed:400,700' rel='stylesheet' type='text/css'>
<link href='<%=fullcontexturl %>/tools/alain/cs.ui.css' rel='stylesheet' type='text/css'>
	<style>
		.csui_controls { visibility: hidden }
		.searchbox {  -webkit-border-radius: 50px;-moz-border-radius: 50px;	border-radius: 50px;width:200px;height:25px;align:right;	}
	</style>

</head>

<body>

<body >
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
						<td align="left" id="title">ADMIN PANEL</td>
						<td align="right" id="subtitle">&nbsp;</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				
					<table class="csui_title">
						<tr>
							<td class="csui_title">MANAGE</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
						
						<%for(int i=0;i<a.size();i++){ 
								MapSet m = a.get(i);
								String title = m.getString("NAME");
								String desc = m.getString("DESCRIPTION");
								StringBuilder url =  new StringBuilder();
								url.append(Config.fullcontexturl()).append(m.getString("LOCATION"));
								if(m.getString("LOCATION").indexOf("?")>0){
									url.append("&");
								}else {
									url.append("?");
									url.append("_token=").append(map.getString("_token"));
									url.append("&");
									url.append("_userid=").append(_userid);
									url.append("&");
								}
								url.append("_type=").append(type);
								url.append("&");
								url.append("_typeid=").append(typeid);
								url.append("&");
								url.append("_id=").append(m.getString("ID"));
							
								String align="left";
								if(m.getInt("ORDR")==0){
									align="center";
								}
							%>
							<tr>
								<td class="csui" ><a href="<%=url.toString() %> " class="csui"  ><%=title %></a></td>
								<td class="csui" ><a href="<%=url.toString() %> " class="csui"  ><%=desc %></a></td>
							</tr>
							
						
							
							<%} %>
							
					</table>

					<div class="csui_divider">
				</div>
		
			</div>
		</div>
	</div>






</body>


</body>

</html>


