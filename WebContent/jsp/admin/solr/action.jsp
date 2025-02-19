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
	
	String indexUrl = map.getString("indexUrl");
	
	
	
	String action = map.getString("_action");
	
	if(Operator.equalsIgnoreCase(action, "indexSolr")){
		
		boolean r = true;
		System.out.println(indexUrl);
		int jobId=AdminAgent.getJobId(editId);
		
		while (r) {
			
			
			if(jobId <=0)
			{
			AdminAgent.scheduleJob(editId); 
			
			String s = AdminAgent.indexSolrIndex(indexUrl);
			//System.out.println(s);
			if (!Operator.equalsIgnoreCase(s, "busy") && !Operator.hasValue(s)) {
				r = false;
			}else {
				r=true;
				AdminAgent.updateJobSchedule(editId);
			}
			 
			}else {
				r=true;
			}
			
			
			if (r==true) { 
				break;
			} 
		
		
		}
		
		out.write(r+"");		
	}
		

	
%>
