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

	if(Operator.equalsIgnoreCase(action, "addref")){
		String connecttype = map.getString("connecttype","project");
		String connectids = map.getString("connectids","0");
		int groupid = map.getInt("FIELD_GROUPS_ID");
		
		if(groupid>0 && Operator.hasValue(connecttype)){
			MapSet m = AdminMap.getDivisionsRef(map);
			boolean r = AdminAgent.insertRef(m);
			out.write(r+"");
		}
	}
	
	if(Operator.equalsIgnoreCase(action, "deleteref")){
		String connecttype = map.getString("connecttype");
		String connectids = map.getString("connectids","0");
		//int refid = map.getInt("REF_ID");
		
		if(Operator.hasValue(connecttype)){
			MapSet m = AdminMap.getDivisionsRef(map);
		
			
			boolean r = AdminAgent.deleteRef(m);
			out.write(r+"");
		}
	}
	
	
	if(Operator.equalsIgnoreCase(action, "sortref")){
		String connecttype = map.getString("connecttype");
		if(Operator.hasValue(connecttype)){
			MapSet m = AdminMap.getDivisionsRef(map);
			boolean r = AdminAgent.sortRef(m);
			out.write(r+"");
		}
	}
	if(Operator.equalsIgnoreCase(action, "sortfields")){
		int LKUP_DIVISIONS_TYPE_ID = map.getInt("LKUP_DIVISIONS_TYPE_ID");
		if(LKUP_DIVISIONS_TYPE_ID>0){
			MapSet m = AdminMap.getDivisionsFieldType(map);
			boolean r = AdminAgent.sortRef(m);
			out.write(r+"");
		}
	}

%>
