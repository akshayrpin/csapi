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
	
	
	/* if(Operator.equalsIgnoreCase(action, "showselector")){
		
		int groupid = map.getInt("TEMPLATE_ID");
		
		if(groupid>0){
			
			JSONArray o = AdminAgent.getJsonList(AdminMap.getCustomFields(groupid));
			//o.put(1,);
			System.out.println(o.toString()+"###############");
			out.write(o.toString());
		}
	} */

	if(Operator.equalsIgnoreCase(action, "addref")){
		String connecttype = map.getString("connecttype","project");
		String connectids = map.getString("connectids","0");
		int groupid = map.getInt("LKUP_ACT_STATUS_ID");
		
		if(groupid>0 && Operator.hasValue(connecttype)){
			MapSet m = AdminMap.getActivityStatusRef(map);
			System.out.println(groupid+"######INSIDE HERE######"+m.getString("LKUP_ACT_TYPE_ID"));
			if(m.getString("DEFLT").equals("Y")){
				  AdminAgent.saveType("UPDATE REF_ACT_TYPE_STATUS SET DEFLT ='N' WHERE LKUP_ACT_TYPE_ID IN ("+m.getString("connectIds")+")");
			 }
			if(m.getString("DEFLT_ISSUED").equals("Y")){
				  AdminAgent.saveType("UPDATE REF_ACT_TYPE_STATUS SET DEFLT_ISSUED ='N' WHERE LKUP_ACT_TYPE_ID IN ("+m.getString("connectIds")+")");
			 }
			
			System.out.println(groupid+"######INSIDE HERE######"+m.getString("DEFLT"));
			boolean r = AdminAgent.insertRef(m);
			out.write(r+"");
		}
	}
	
	if(Operator.equalsIgnoreCase(action, "deleteref")){
		String connecttype = map.getString("connecttype");
		String connectids = map.getString("connectids","0");
		//int refid = map.getInt("REF_ID");
		
		if(Operator.hasValue(connecttype)){
			MapSet m = AdminMap.getActivityStatusRef(map);
		
			
			boolean r = AdminAgent.deleteRef(m);
			out.write(r+"");
		}
	}
	
	if (map.equalsIgnoreCase("_action", "updateflags")) {
		MapSet m = AdminMap.getCommon(map);
		m.add("ID",map.getInt("ID"));
		m.add(map.getString("type"),map.getString("val"));
		String connecttype = map.getString("connecttype");
		if(connecttype.equalsIgnoreCase("project")){
			m.add("table","REF_PROJECT_TYPE_STATUS");
			//m.add("column","LKUP_PROJECT_TYPE_ID");
			
			if(m.getString("DEFLT").equals("Y")){
				  AdminAgent.saveType("UPDATE REF_PROJECT_TYPE_STATUS SET DEFLT ='N' WHERE LKUP_ACT_TYPE_ID IN ("+map.getString("connectIds")+")");
			 }
			if(m.getString("DEFLT_ISSUED").equals("Y")){
				  AdminAgent.saveType("UPDATE REF_PROJECT_TYPE_STATUS SET DEFLT_ISSUED ='N' WHERE LKUP_ACT_TYPE_ID IN ("+map.getString("connectIds")+")");
			 }
		}
		if(connecttype.equalsIgnoreCase("activity")){
			m.add("table","REF_ACT_TYPE_STATUS");
			//m.add("column","LKUP_ACT_TYPE_ID");
		
			if(m.getString("DEFLT").equals("Y")){
				  AdminAgent.saveType("UPDATE REF_ACT_TYPE_STATUS SET DEFLT ='N' WHERE LKUP_ACT_TYPE_ID IN ("+map.getString("connectIds")+")");
			 }
			if(m.getString("DEFLT_ISSUED").equals("Y")){
				  AdminAgent.saveType("UPDATE REF_ACT_TYPE_STATUS SET DEFLT_ISSUED ='N' WHERE LKUP_ACT_TYPE_ID IN ("+map.getString("connectIds")+")");
			 }
		}
		
		String tbl = m.getString("table");
		boolean resp = AdminAgent.updateType(m, tbl);
		System.out.println(map.getString("type")+"parking response "+resp);
		out.print(resp);
	}
	
	if(Operator.equalsIgnoreCase(action, "sortref")){
		String connecttype = map.getString("connecttype");
		if(Operator.hasValue(connecttype)){
			MapSet m = AdminMap.getActivityStatusRef(map);
			boolean r = AdminAgent.sortRef(m);
			out.write(r+"");
		}
	}
	if(Operator.equalsIgnoreCase(action, "sortfields")){
		int TEMPLATE_ID = map.getInt("TEMPLATE_ID");
		if(TEMPLATE_ID>0){
			MapSet m = AdminMap.getActivityStatusRef(map);
			boolean r = AdminAgent.sortRef(m);
			out.write(r+"");
		}
	}

	
/* 	if (map.equalsIgnoreCase("_action", "deletefield")) {
		MapSet m = AdminMap.getCommon(map);
		String table = Table.FIELDTABLE;
		
		m.add("ID",map.getInt("ID"));
		m.add("ACTIVE","N");
		boolean resp = AdminAgent.updateType(m, table);
		out.print(resp);
	}
 */
	
	if (map.equalsIgnoreCase("_action", "deletetype")) {
		MapSet m = AdminMap.getCommon(map);
		m.add("ID",map.getInt("ID"));
		m.add("ACTIVE","N");
		boolean resp = AdminAgent.updateType(m, map.getString("_table"));
		System.out.println("parking response "+resp);
		out.print(resp);
	}
%>
