<%@page import="alain.core.utils.Timekeeper"%>
<%@page import="csapi.impl.admin.AdminAgent"%>
<%@page import="java.io.FileNotFoundException"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.OutputStreamWriter"%>
<%@page import="java.io.Writer"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.PrintStream"%>
<%@page import="java.nio.charset.Charset"%>
<%@page import="alain.core.utils.Operator"%>
<%@page import="alain.core.utils.Config"%>
<%@page import="csshared.vo.ToolsVO"%>

<%@page import="alain.core.utils.Logger"%>


<%@page import="csshared.vo.ObjGroupVO"%>

<%@page import="alain.core.utils.Cartographer"%>
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@page trimDirectiveWhitespaces="true" %> 
<!--sunil  -->
<%
try{
	Cartographer map = new Cartographer(request,response);
	String date = map.getString("START_DATE");
	Timekeeper d = new Timekeeper();
	d.setDate(date);
	String mm = d.mm();
	String yyyy = d.yyyy();
	String filename = "export"+mm+""+yyyy+".txt";
	response.setContentType("text/plain");
	response.setHeader("Content-Disposition", "attachment; filename="+filename);
	
	
	String content = AdminAgent.assessorReport(date);
	
	
	java.io.PrintWriter op = response.getWriter();
	op.write(content);
	op.close();

		
	
	
} catch(Exception e){}
%>
