<%@page import="csapi.impl.admin.AdminSQL"%>
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
		
		int feeid = map.getInt("FEE_ID");
		
		if(feeid>0){
			
			JSONArray o = AdminAgent.getJsonList(AdminSQL.getFeeGroupFields(feeid));
			//o.put(1,);
			System.out.println(o.toString()+" test ###############");
			out.write(o.toString());
		}
	}

	if(Operator.equalsIgnoreCase(action, "addref")){
		String connecttype = map.getString("connecttype","project");
		String connectids = map.getString("connectids","0");
		int groupid = map.getInt("FEE_GROUP_ID");
		
		if(groupid>0 && Operator.hasValue(connecttype)){
			MapSet m = AdminMap.getFeeGroupRef(map);
			boolean r = AdminAgent.insertRef(m);
			out.write(r+"");
		}
	}
	
	if(Operator.equalsIgnoreCase(action, "deleteref")){
		String connecttype = map.getString("connecttype");
		String connectids = map.getString("connectids","0");
		//int refid = map.getInt("REF_ID");
		
		if(Operator.hasValue(connecttype)){
			MapSet m = AdminMap.getFeeGroupRef(map);
		
			
			boolean r = AdminAgent.deleteRef(m);
			out.write(r+"");
		}
	}
	
	
	if(Operator.equalsIgnoreCase(action, "sortref")){
		String connecttype = map.getString("connecttype");
		System.out.println(connecttype);
		if(Operator.hasValue(connecttype)){
			MapSet m = AdminMap.getFeeGroupRef(map);
			boolean r = AdminAgent.sortRef(m);
			out.write(r+"");
		}
	}
	if(Operator.equalsIgnoreCase(action, "sortfields")){
		int FIELD_GROUPS_ID = map.getInt("FIELD_GROUPS_ID");
		if(FIELD_GROUPS_ID>0){
			MapSet m = AdminMap.getFeeGroupType(map);
			boolean r = AdminAgent.sortRef(m);
			out.write(r+"");
		}
	}

	
	if (map.equalsIgnoreCase("_action", "deletefield")) {
		MapSet m = AdminMap.getCommon(map);
		String table = Table.FIELDTABLE;
		
		m.add("ID",map.getInt("ID"));
		m.add("ACTIVE","N");
		boolean resp = AdminAgent.updateType(m, table);
		out.print(resp);
	}
	if (map.equalsIgnoreCase("_action", "deletefeeonly")) {
		MapSet m = AdminMap.getCommon(map);
		String ed = map.getString("EXPIRATION_DATE");
		Timekeeper k = new Timekeeper();
		k.setDay(Operator.subString(ed, 3, 5));
		k.setMonth(Operator.subString(ed, 0, 2));
		k.setYear(Operator.subString(ed, 6, 10));
		String table = Table.REFFEEFORMULATABLE;
		m.add("EXPIRATION_DATE",k.getString("YYYY/MM/DD")+" 00:00:00");
		m.add("ID",map.getInt("ID"));
		//m.add("ACTIVE","N");
		
		System.out.println(m.getString("EXPIRATION_DATE"));
		
		boolean resp = AdminAgent.updateType(m, table);
		out.print(resp);
	}
	
	if (map.equalsIgnoreCase("_action", "deleteassociation")) {
		MapSet m = AdminMap.getCommon(map);
		String table = Table.REFFEEGROUPTABLE;
		
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
		System.out.println("parking response "+resp);
		out.print(resp);
	}
	
	if (map.equalsIgnoreCase("_action", "associate")) {
		MapSet m = AdminMap.insertFeeGroupFeeRef(map);
		String table = Table.REFFEEGROUPTABLE;
		boolean resp = AdminAgent.saveTypeMultiValues(m, table);
		System.out.println("associate response "+resp);
		out.print(resp);
	}
	
	if (map.equalsIgnoreCase("_action", "copy")) {
		
		 
		boolean resp = AdminAgent.copyFee(map);
		System.out.println(map.getString("START_DATE")+"copy response "+resp+ map.getString("MANUAL_ACCOUNT"));
		out.print(resp);
	}
	
	if (map.equalsIgnoreCase("_action", "updateflags")) {
		MapSet m = AdminMap.getCommon(map);
		m.add("ID",map.getInt("ID"));
		m.add(map.getString("type"),map.getString("val"));
		String reffeegrouptable = Table.REFFEEGROUPTABLE;
		boolean resp = AdminAgent.updateType(m, reffeegrouptable);
		System.out.println(map.getString("type")+"parking response "+resp);
		out.print(resp);
	}
	
	if (map.equalsIgnoreCase("_action", "updateseq")) {
		MapSet m = AdminMap.getCommon(map);
		m.add("ID",map.getInt("ID"));
		m.add(map.getString("type"),map.getInt("val",0));
		String reffeegrouptable = Table.REFFEEGROUPTABLE;
		boolean resp = AdminAgent.updateType(m, reffeegrouptable);
		System.out.println(map.getString("type")+"parking response "+resp);
		out.print(resp);
	}
%>
