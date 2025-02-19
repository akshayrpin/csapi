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
	
	if(Operator.equalsIgnoreCase(action, "showselector")){
		
		int groupid = map.getInt("LIBRARY_GROUP_ID");
		
		if(groupid>0){
			
			JSONArray o = AdminAgent.getJsonList(AdminMap.getLibraryFields(groupid));
			//o.put(1,);
			System.out.println(o.toString()+"###############");
			out.write(o.toString());
		}
	}

	if(Operator.equalsIgnoreCase(action, "addref")){
		String connecttype = map.getString("connecttype","project");
		String connectids = map.getString("connectids","0");
		int groupid = map.getInt("LIBRARY_GROUP_ID");
		
		if(groupid>0 && Operator.hasValue(connecttype)){
			MapSet m = AdminMap.getLibraryRef(map);
			boolean r = AdminAgent.insertRef(m);
			out.write(r+"");
		}
	}
	
	if(Operator.equalsIgnoreCase(action, "deleteref")){
		String connecttype = map.getString("connecttype");
		String connectids = map.getString("connectids","0");
		//int refid = map.getInt("REF_ID");
		
		if(Operator.hasValue(connecttype)){
			MapSet m = AdminMap.getLibraryRef(map);
		
			
			boolean r = AdminAgent.deleteRef(m);
			out.write(r+"");
		}
	}
	
	
	if(Operator.equalsIgnoreCase(action, "sortref")){
		String connecttype = map.getString("connecttype");
		if(Operator.hasValue(connecttype)){
			MapSet m = AdminMap.getLibraryRef(map);
			boolean r = AdminAgent.sortRef(m);
			out.write(r+"");
		}
	}
	if(Operator.equalsIgnoreCase(action, "sortfields")){
		int LIBRARY_GROUP_ID = map.getInt("LIBRARY_GROUP_ID");
		if(LIBRARY_GROUP_ID>0){
			MapSet m = AdminMap.getLibraryType(map);
			boolean r = AdminAgent.sortRef(m);
			out.write(r+"");
		}
	}

	
	if (map.equalsIgnoreCase("_action", "deletefield")) {
		MapSet m = AdminMap.getCommon(map);
		String table = Table.LIBRARYTABLE;
		
		m.add("ID",map.getInt("ID"));
		m.add("ACTIVE","N");
		boolean resp = AdminAgent.updateType(m, table);
		out.print(resp);
	}

	
	if (map.equalsIgnoreCase("_action", "deletetype")) {
		MapSet m = AdminMap.getCommon(map);
		m.add("ID",map.getInt("ID"));
		m.add("ACTIVE","N");
		boolean resp = AdminAgent.updateType(m, map.getString("_table"));
		//System.out.println("parking response "+resp);
		out.print(resp);
	}
	
	if(Operator.equalsIgnoreCase(action, "showselector")){
		
		int fid = map.getInt("ID");
		
		if(fid>0){
			
			JSONArray o = AdminAgent.getJsonList(AdminMap.getFormulaFields(fid));
			//o.put(1,);
			System.out.println(o.toString()+"###############");
			out.write(o.toString());
		}
	}
%>
