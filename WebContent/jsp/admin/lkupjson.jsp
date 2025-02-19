<%@page import="csapi.impl.admin.AdminAgent"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@page import="alain.core.utils.Cartographer"%>
<% 

	Cartographer map = new Cartographer(request,response);
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id");
	String _lookup = map.getString("_lookup");
	JSONArray j= AdminAgent.getLookup(_lookup);
	
%>
<%=j.toString() %>
