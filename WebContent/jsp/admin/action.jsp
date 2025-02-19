<%@page import="alain.core.utils.MapSet"%>
<%@page import="csapi.impl.admin.AdminAgent"%>
<%@page import="csapi.impl.admin.AdminMap"%>
<%@page import="csshared.vo.RequestVO"%>
<%@page import="alain.core.utils.Cartographer"%>
<%

Cartographer map = new Cartographer(request,response);

if (map.equalsIgnoreCase("_action", "deletetype")) {
	MapSet m = AdminMap.getCommon(map);
	m.add("ID",map.getInt("ID"));
	m.add("ACTIVE","N");
	boolean resp = AdminAgent.updateType(m, map.getString("_table"));
	System.out.println("parking response "+resp);
	out.print(resp);
}



%>

