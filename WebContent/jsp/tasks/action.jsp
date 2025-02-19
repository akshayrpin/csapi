<%@page import="csapi.impl.admin.AdminMap"%>
<%@page import="java.util.Arrays"%>
<%@page import="alain.core.utils.MapSet"%>
<%@page import="org.json.JSONArray"%>
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
	
	
	String alert="";
	
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	int editId = map.getInt("ID");
	
	String action = map.getString("_action");
	System.out.println(action+"###############");
	
	
	
	if (map.equalsIgnoreCase("_action", "updateflags")) {
		MapSet m = AdminMap.getCommon(map);
		m.add("ID",map.getInt("ID"));
		m.add(map.getString("type"),map.getString("val"));
		
		boolean resp = AdminAgent.updateType(m, map.getString("table"));
		System.out.println(map.getString("type")+"flags response "+resp);
		out.print(resp);
	}
	
	
	if (map.equalsIgnoreCase("_action", "sortfields")) {
		
		MapSet m = AdminMap.getTaskOptions(map, "REF_ACT_TYPE_TASKS");
		//System.out.println(m.getString("sortselection"));
		boolean r = AdminAgent.sortRef(m);
		out.write(r+"");
	}
	
	
	if (map.equalsIgnoreCase("_action", "reviewstatus")) {
		System.out.println("CAME HERE ");
		System.out.println("*************************action"+map.getString("REVIEW_GROUP_ID"));
		//LKUP_REVIEW_STATUS LKUP_REVIEW_TYPE_ID
		String command = "select DISTINCT RS.* from REVIEW_GROUP RG JOIN REVIEW R on RG.ID = R.REVIEW_GROUP_ID AND R.ACTIVE='Y' JOIN LKUP_REVIEW_STATUS RS on R.LKUP_REVIEW_TYPE_ID = RS.LKUP_REVIEW_TYPE_ID AND RS.ACTIVE='Y' 	WHERE RG.ID = "+map.getString("REVIEW_GROUP_ID");
		JSONArray a = AdminAgent.getJsonList(command);
		System.out.println(a.toString());
		if(a.length()>0){
			//JSONObject o = a.getJSONObject(0);
			out.print(a.toString());
			
		}
	
	}
	
	
%>
