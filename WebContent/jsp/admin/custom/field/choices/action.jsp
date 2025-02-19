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
	System.out.println(action+"###############"+map.getInt("FIELD_ID"));
	
	if(Operator.equalsIgnoreCase(action, "showselector")){
		
		int fieldId = map.getInt("FIELD_ID");
		
		if(fieldId <=0)
		{
			
			fieldId=map.getInt("_fieldId");
		}
		
		if(fieldId>0){
			
			JSONArray o = AdminAgent.getJsonList(AdminMap.getCustomFieldChoices(fieldId));
			//o.put(1,);
			System.out.println(o.toString()+"###############");
			out.write(o.toString());
		}
	}

	
	
	if(Operator.equalsIgnoreCase(action, "deletefieldchoices")){
		
	
			MapSet m = AdminMap.getFieldChoice(map);
		
			
			boolean r = AdminAgent.deleteRef(m);
			out.write(r+"");

	}
	if(Operator.equalsIgnoreCase(action, "sortfields")){
		int FIELD_GROUPS_ID = map.getInt("FIELD_ID");
		if(FIELD_GROUPS_ID>0){
			MapSet m = AdminMap.getFieldChoice(map);
			boolean r = AdminAgent.sortRef(m);
			out.write(r+"");
		}
	}
	
	if (map.equalsIgnoreCase("_action", "deletefield")) {
		MapSet m = AdminMap.getCommon(map);
		String table = Table.FIELDCHOICESTABLE;
		
		m.add("ID",map.getInt("ID"));
		m.add("ACTIVE","N");
		boolean resp = AdminAgent.updateType(m, table);
		out.print(resp);
	}
	
	if (map.equalsIgnoreCase("_action", "updateflags")) {
		MapSet m = AdminMap.getCommon(map);
		m.add("ID",map.getInt("ID"));
		m.add(map.getString("type"),map.getString("val"));
		String t = Table.FIELDCHOICESTABLE;
		boolean resp = AdminAgent.updateType(m, t);
		System.out.println(map.getString("type")+"parking response "+resp);
		out.print(resp);
	}

	
	

%>
