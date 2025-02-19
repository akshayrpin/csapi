<%@page import="csapi.search.GlobalSearch"%>
<%@page import="alain.core.utils.Config"%>
<%@page import="csapi.common.ApiHandler"%>
<%@page import="csshared.vo.finance.PaymentVO"%>
<%@page import="csapi.common.Table"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@page import="alain.core.utils.MapSet"%>
<%@page import="csapi.impl.admin.AdminAgent"%>
<%@page import="csapi.impl.admin.AdminMap"%>
<%@page import="csshared.vo.RequestVO"%>
<%@page import="alain.core.utils.Cartographer"%>
<%

Cartographer map = new Cartographer(request,response);

if (map.equalsIgnoreCase("_action", "addtype")) {
	MapSet m = AdminMap.getRefUsers(map);
	boolean resp = AdminAgent.saveType(m, m.getString("_table"));
	System.out.println("users response "+resp);
	GlobalSearch.index(GlobalSearch.TEAM_DELTA);
	GlobalSearch.index(GlobalSearch.PEOPLE_DELTA);
	if(resp){
		JSONArray a = AdminAgent.getJsonList(m.getString("initialquery"));
		if(a.length()>0){
			JSONObject o = a.getJSONObject(0);
			out.print(o.toString());
		}
	}else{
		out.print(resp);
	}
}

if (map.equalsIgnoreCase("_action", "deletefield")) {
	MapSet m = AdminMap.getCommon(map);
	String opt = map.getString("OPT");
	
	String table = "";//Table.LIBRARYTABLE;
	if(opt.equalsIgnoreCase("refusers")){
		table = Table.REFUSERSTABLE;
	}
	
	if(opt.equalsIgnoreCase("refteam")){
		table = Table.REFTEAMTABLE;
	}
	
	if(opt.equalsIgnoreCase("refgroups")){
		table = Table.REFUSERSGROUPTABLE;
	}
	
	if(opt.equalsIgnoreCase("refroles")){
		table = Table.REFUSERROLESTABLE;
	}
	
	
	
	m.add("ID",map.getInt("ID"));
	m.add("ACTIVE","N");
	boolean resp = AdminAgent.updateType(m, table);
	out.print(resp);
}

//TODO combine jsp
if (map.equalsIgnoreCase("_action", "associate")) {
	if (map.equalsIgnoreCase("type", "refusergroup")) {
		MapSet m = AdminMap.insertUserGroupUserRef(map);
		String table = Table.REFUSERSGROUPTABLE;
		boolean resp = AdminAgent.saveTypeMultiValues(m, table);
		System.out.println("copy response "+resp);
		out.print(resp);
	}
}

if (map.equalsIgnoreCase("_action", "showdepositledger")) {
	RequestVO vo = new RequestVO();
	vo.setRequest("showdepositledger");
	vo.setUrl(Config.fullcontexturl()+"/rest/deposit/showdepositledger");
	PaymentVO p = new PaymentVO();
	p.setPaymentid(map.getInt("P_ID"));
	vo.setPayment(p);
	String resp = ApiHandler.post(vo);
	System.out.println(resp);
	out.print(resp);
	
}	



%>

