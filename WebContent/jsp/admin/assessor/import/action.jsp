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
<%@page import="csapi.impl.assessor.AssessorImport"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="csshared.vo.TypeVO"%>
<%@page import="csshared.vo.RequestVO"%>
<%@page import="alain.core.utils.Cartographer"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.FileWriter"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.util.*"%>
<%@ page import = "javax.servlet.http.*" %>
<%@ page import = "org.apache.commons.fileupload.*" %>
<%@ page import = "org.apache.commons.fileupload.disk.*" %>
<%@ page import = "org.apache.commons.fileupload.servlet.*" %>
<%@ page import = "org.apache.commons.io.output.*" %>
<%@ page import = "java.io.*,java.util.*, javax.servlet.*" %>


<%
String method=request.getParameter("_action");
String action =request.getParameter("_action");
String alert="";
boolean r=true;  
File file ;
   ServletContext context = pageContext.getServletContext();
   String filePath = Config.getString("files.assessor_path");

   // Verify the content type
   String contentType = request.getContentType();  
   String fieldName = "";
   //String fileName ="";

   Cartographer map = new Cartographer(request,response); 

   String fileName = map.getString("fileName");
   action = map.getString("_action");
  
  
   if(action.equalsIgnoreCase("clearLogs"))
   {
	r=AssessorImport.clearLogs();
   	out.write(r+"");
   }  
   
   if(action.equalsIgnoreCase("importdata"))
   {   
	fileName=filePath +fileName;
	System.out.println("File name with full path " +fileName);
	int recordCount=0;
	
	 recordCount= AssessorImport.countLines(fileName);
	  
	System.out.println("Line count in file is " +recordCount);
    System.out.println("**********3 "+fileName);
	String type = map.getString("_type");
	String typeid = map.getString("_typeid");
	String id = map.getString("_id","0");
	int editId = map.getInt("ID");
	
	
	String table = Table.ASSESSORIMPORTLOG;
 
	// insert data to assessor table.
	 int processCount = map.getInt("processCount");
	
	 
	int oldProcessCount= processCount;
	if(processCount==0)
	{
	AssessorImport.initialAssessorUpdate();
	}
	
	if(processCount < recordCount)
		  
	{
		processCount=AssessorImport.assessorDataImport(((fileName!=null)?fileName:""), processCount,recordCount);
				
	}
	 
	if((processCount == recordCount) && (processCount >0) )
		  
	{
	AssessorImport.exportException();  
	}
	r=true;
	//processCount=AssessorImport.processRecordsCount();
	System.out.println(" ******processCount******* " + processCount);  
	
	JSONObject sampleObject = new JSONObject();
    sampleObject.put("r", r);
    sampleObject.put("recordCount", recordCount);
    sampleObject.put("processCount", processCount);
    String jsonObj=sampleObject.toString();
    System.out.println("**********Json object "+jsonObj);   
   // if(oldProcessCount<processCount)
   // {
    	out.write(sampleObject+""); 
    //}
	 
	   
}    
   
%>
