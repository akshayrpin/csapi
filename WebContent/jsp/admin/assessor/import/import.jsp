<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@page import="csapi.impl.admin.AdminMap"%>
<%@page import="csapi.impl.assessor.AssessorImport"%>
<%@page import="alain.core.utils.MapSet"%>
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

 
File file ;
 
   ServletContext context = pageContext.getServletContext();
   String filePath = Config.getString("files.assessor_path");
   System.out.println(" filePath ********  " + filePath);    

   // Verify the content type
   String contentType = (request.getContentType()!=null)?request.getContentType():"";  
   String fieldName = "";
   String fileName ="";

   String action= "";
     
   if ((contentType.indexOf("multipart/form-data") >= 0)) {

      DiskFileItemFactory factory = new DiskFileItemFactory();
      //factory.setSizeThreshold(maxMemSize);
      factory.setRepository(new File(filePath)); 
      ServletFileUpload upload = new ServletFileUpload(factory);
      //upload.setSizeMax( maxFileSize );
      try{ 
         List<FileItem> fileItems =(List<FileItem>) upload.parseRequest(request);
         System.out.println("fileItems size " + fileItems.size());
         Iterator<FileItem> i = fileItems.iterator();
         
         while ( i.hasNext () ) 
         {
        	
            FileItem fi = (FileItem)i.next();
            System.out.println("fileItems  " + fi.getSize() + " "+ fi.getName());   
            if ( fi.getName()!=null)  {
                fieldName = fi.getFieldName();
                fileName = fi.getName();
                boolean isInMemory = fi.isInMemory();
                long sizeInBytes = fi.getSize();
                file = new File( filePath + fi.getName()) ;
                fi.write( file ) ;
                
                //out.println("Uploaded Filename: " + filePath + fileName + "<br>");
               // fileName=filePath + fileName;
                out.write("");         
            }
           
         }
         
         action="fileUpload";
      }catch(Exception ex) {
         System.out.println(ex);
      }
      
   } 
   
	Cartographer map = new Cartographer(request,response);
	Timekeeper d = new Timekeeper();
	String fullcontexturl = CsApiConfig.getString("cs.fullcontexturl");
	String table = Table.LKUPMODULETABLE;
	String alert="";
		
	String type = map.getString("_type");
	if(type.equalsIgnoreCase(""))
	{
		type= request.getParameter("_type");
	}
	
	String typeid = map.getString("_typeid");
	if(typeid.equalsIgnoreCase(""))
	{
		typeid= request.getParameter("_typeid");
	}
	
	String id = map.getString("_id","0");
	if(id.equalsIgnoreCase(""))
	{
		id= request.getParameter("_id");
	}   
	
	int editId = map.getInt("ID");
	
	ArrayList<MapSet> la = AdminAgent.getIndex(type, typeid, id,false,map);

	MapSet l = new MapSet();
	if(la.size()>0){
		l = la.get(0);
	}
	ArrayList<MapSet> fields = new ArrayList<MapSet>();
	MapSet v = new MapSet();
	if(editId>0){
		v = AdminAgent.getType(editId,table);
		//fields = AdminAgent.getList(AdminMap.getCustomFields(editId));
	}
	
	boolean result = false;
	 if(map.equalsIgnoreCase("_action","SAVE")){
		 if(editId>0){
			 result = AdminAgent.updateType(AdminMap.getModuleType(map),table);
		 }else {
			 result = AdminAgent.saveType(AdminMap.getModuleType(map),table);
		 }
		 
		 
	 }
	 if(map.equalsIgnoreCase("_action","CANCEL")){
			result = true;
	 }
	
	 ArrayList<MapSet> a =  new ArrayList<MapSet>();
	 a = AssessorImport.getAssessorLogs();
	 

%>
<html>
<head>

	<title>City Smart- Admin</title>
	<link href='https://fonts.googleapis.com/css?family=Oswald:300,700' rel='stylesheet' type='text/css'>
	<link href='https://fonts.googleapis.com/css?family=Armata' rel='stylesheet' type='text/css'>
	<link href='https://fonts.googleapis.com/css?family=Roboto+Condensed:400,700' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/datetimepicker/jquery.datetimepicker.css"/>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/chosen/chosen.css"/>
	<link href='<%=fullcontexturl %>/tools/alain/cs.ui.css' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" media="all" href="<%=fullcontexturl %>/tools/fancyapps/source/jquery.fancybox.css"/>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/sweetalert/dist/sweetalert.css">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
	<style>
		.csui_controls { visibility: hidden }
		.searchbox {  -webkit-border-radius: 50px;-moz-border-radius: 50px;	border-radius: 50px;width:200px;height:25px;align:right;	}
	</style>


	<script type="text/javascript" src="<%=fullcontexturl %>/tools/jquery.min.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/alain/cs.tools.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/alain/cs.form.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/datetimepicker/jquery.datetimepicker.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/chosen/chosen.jquery.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/autogrow/jquery.autogrowtextarea.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/tinymce/js/tinymce/jquery.tinymce.min.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/tinymce/js/tinymce/tinymce.min.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/inputmask/dist/inputmask/inputmask.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/inputmask/dist/inputmask/jquery.inputmask.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/numeric/jquery.numeric.min.js"></script>
	
 	<script type="text/javascript" src="<%=fullcontexturl %>/tools/fancyapps/source/jquery.fancybox.pack.js"></script>
    <script type="text/javascript" src="<%=fullcontexturl %>/tools/fancyapps/source/cms.fancybox.js"></script>
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/sweetalert/dist/sweetalert-dev.js"></script>

  	<script type="text/javascript" src="<%=fullcontexturl %>/tools/jquery-ui.min.js"></script>
	<link href='<%=fullcontexturl %>/tools/jquery-ui.css' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" href="<%=fullcontexturl %>/tools/ioscheckboxes/assets/css/mobileCheckbox.iOS.css">
	<script type="text/javascript" src="<%=fullcontexturl %>/tools/ioscheckboxes/assets/js/jquery.mobileCheckbox.js"></script>
	
	<style>
	
		input[type=button], a.button, span.button {
		background-color: #eeeeee;
		border: 1px solid #cccccc;
		font-family: Oswald, Arial, Helvetica;
		text-transform: uppercase;
		padding: 10px;
		padding-left: 20px;
		padding-right: 20px;
		margin: 10px;
		font-size: 16px;
		font-weight: bold;
		border-radius: 5px;
		color: #000000;
		cursor: pointer;
		text-decoration: none;
	}
	
		
	</style>

	<script>
			
		function deleteLogs(id)
		{
			var method = "clearLogs";	
			
			$.ajax({
	   			  type: "POST",
	   			  url: "action.jsp?_action="+method,
	   			  dataType: 'text',
	   			  data: { 
	   				  _type : "<%=type%>",
	   				  _typeid : <%=typeid%>,
	   				  _id : <%=id%>,
	   				   ID : id
	   			     },
	   			    success: function(output) {
	   			    		swal({title: "Clear Logs!", text: "The import logs cleared.", type: "success"},
	   			    			   function(){ 
	   			    			       location.reload();
	   			    			   }
	   			    			);
	   			    },
	   		    error: function(data) {
	   		    	swal("Problem while clearing import logs");  
	   		    }
 			});		
		

		}

		function exceptionExport(id)
		{
			var method = "exceptionExport";	
			
			$.ajax({
	   			  type: "POST",
	   			  url: "action.jsp?_action="+method,
	   			  dataType: 'text',
	   			  data: { 
	   				  _type : "<%=type%>",
	   				  _typeid : <%=typeid%>,
	   				  _id : <%=id%>,
	   				   ID : id
	   			     },
	   			    success: function(output) {
	   			
	   			    		swal("Export exception!", "The export logs exported..", "success");   
	   			    },
	   		    error: function(data) {
	   		    	swal("Problem while exporting exception");  
	   		    }
 			});		
		

		}
		
		
		
		function uploadFile(id){  
			
			var method = "fileUpload";
			
						$.ajax({
				   			  type: "POST",
				   			  url: "import.jsp?_ent=permit&_type=<%=type%>&_typeid=<%=typeid%>&_id=<%=id%>&_action="+method,
				   			  dataType: 'text',
				   		
				   			  data: { 
				   				  _type : "<%=type%>",
				   				  _typeid : <%=typeid%>,
				   				  _id : <%=id%>,
				   				   ID : id
				   				
				   			    },
				   			    success: function(output) {
				   			    	$('#tr_'+id).remove();				   			    	
				   			    	//swal("Uploaded!", "The file uploaded successfully.", "success");  
				   			    		//importdata(id,0);
				   			    },
				   		    error: function(data) {
				   		    	swal("Problem while perfoming import looks like the server is busy");  
				   		    }
			   			});		
				
		}
		
		function importdata(id,processCount){
			var method = "importdata"; 
						$.ajax({
				   			  type: "POST",
				   			  url: "action.jsp?_ent=permit&_type=<%=type%>&_typeid=<%=typeid%>&_id=<%=id%>&_action="+method,
				   			  dataType: 'json',
				   		
				   			  data: { 
				   				  _type : "<%=type%>",
				   				  _typeid : <%=typeid%>,
				   				  _id : <%=id%>,
				   				   ID : id,
				   	     processCount :processCount,
				   	          fileName:"<%=fileName%>"
				   			    },
				   			    success: function(output) {
				   			    	
				   			    	var processCount=parseInt(output.processCount);
				   			    	
				   			    	var recordCount=parseInt(output.recordCount);
				   			    	//alert(processCount + "   " + recordCount );
				   			    	var perBar = (100*processCount)/recordCount;
				   			    	perBar = Math.round(perBar);
				   			    	//alert( "  Percentage bar  " + perBar);
				   			    	
				   					$("#progressBar").width(perBar+"%");
				   					$("#progressBar").last().html( perBar+"%" );
				   					
				   					if(processCount>=recordCount && processCount >0)
				   						{
				   						swal("Imported!", "The record has been Imported.", "success");  
				   						processCount=0;
				   						recordCount=0;
				   						}else{
				   							//$('#tr_'+id).remove();
				   						//alert(recordCount + " ******Record count and Process count *****"+ processCount);
						   					importdata(id,processCount);
				   			    		
				   						}
				   			    		
				   			    		
				   			    },
				   		    error: function(data) {
				   		    	swal("Problem while perfoming import looks like the server is busy");  
				   		    }
			   			});		
				
		}

		
		function exportexception(id){
			var method = "csv"; 
			window.open('fopen.jsp','_blank');
		}

	var action='<%=action%>';
	
	if(action=='fileUpload')
	{
	importdata(<%=id%>,0); 
	}
	</script>
	<%
	//}
	%>

</head>
<body alert="<%= alert %>">
<div id="csuibody">
		<div id="csuimain">
			<div class="csuicontrol ">
				<div id="csuicontrol" class="csuicontrol">
					
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td align="left">
								<table class="csui_tools">
									<tr>
										<td class="csui_tools">
											&nbsp;
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					
					
				</div>

			</div>
			<div class="csuicontent">
				<table cellpadding="10" cellspacing="0" border="0" width="100%">
					<tr>
						<td align="left" id="title">MODULE</td>
						<td align="right" id="subtitle">&nbsp;</td>
					</tr>
				</table>
				
				<div id="csform_message"></div>
				<form action="import.jsp" method="post" enctype="multipart/form-data">  
					<input type="hidden" name="_type" value="<%=type%>">
					<input type="hidden" name="_typeid" value="<%=typeid%>">
					<input type="hidden" name="_id" value="<%=id%>">
					<input type="hidden" name="ID" value="<%=editId%>">
					<input type="hidden" name="tabletype" value="single">
					
					<table class="csui_title">
						<tr>
							<td class="csui_title">MODULE</td>
							<td class="csui_controls">&nbsp;</td>
						</tr>
					</table>
						
						<table class="csui" colnum="2" type="default">
							<tr>
								<td class="csui_label" colnum="2" alert="">MODULE</td>
								<td class="csui" colnum="2" type="String" itype="text" alert="" colspan="4"><%=v.getString("MODULE") %>
								<input type="file" name="file" value="choose file" />
								
								</td>
								
								
							</tr>

					</table>

					<div class="csui_divider">
			</div>
			<div class="csui_buttons">
			    <input type="submit" name="_action" value="IMPORT" class="csui_button" onclick="uploadFile(<%=id%>);">  
				<input type="submit" name="_action" value="cancel" class="csui_button">&nbsp;
				<!-- <input type="button" name="_action" value="Import" class="csui_button" onclick="importdata(<%--id--%>,0);"> -->
				
				
			</div>
			
			
			<div class="container">
			   <div class="progress">
			    <div class="progress-bar" role="progressbar" id="progressBar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width:0%">
			     
			    </div>
			  </div>
			  </div>
			  
			  
				<div class="csui_divider"></div>
				
				   <table class="csui_title" alert="warning">
						<tr>
						<td class="csui_title">ASSESSOR LOGS</td>
						
						
						<td class="csui_buttons">
						
						<input type="button" name="_action" value="Export Exception" class="csui_button" onclick="exportexception(<%=id%>);">
						
						</td>
						<td class="csui_buttons">
						
						<input type="button" name="_action" value="CLEAR LOGS" class="csui_button" onclick="deleteLogs(<%=id%>);"> 
						</td>
						</tr>
					</table>
			 	 	 	
			 	 	 	<table class="csui tablesorter" type="horizontal">
					 	<thead>
						<tr>
			
							<td class="csui_header">PROCESS TYPE</td>
							<td class="csui_header">PROCESS DESCRIPTION</td>
							<td class="csui_header">STATUS</td>
							<td class="csui_header">CREATED BY</td>
							<td class="csui_header">CREATED DATE</td>
							
						</tr>
						</thead>
						
						 <tbody>
						<%for(int i=0;i<a.size();i++){
							MapSet r = a.get(i);
						%>
							<tr>
								<td class="csui"  style="cursor:pointer;"><%=r.getString("PROCESS_TYPE") %></td>
								<td class="csui" style="cursor:pointer;"><%=r.getString("PROCESS_DESCRIPTION") %></td>
								<td class="csui" style="cursor:pointer;"><%=r.getString("STATUS") %></td>
								<td class="csui" style="cursor:pointer;"><%=r.getString("CREATED_NAME") %></td>
								<td class="csui" style="cursor:pointer;"><%=r.getString("CREATED_DATE") %></td>
								
							</tr>
							 <tr style="display:none;" id="show_selector_<%=r.getString("ID") %>">
								 <td  align="right" colspan="11">
								 <table class="csui" type="horizontal" id="show_selector_table_<%=r.getString("ID") %>" width="100%">
								 
								 </table>
								 </td>
							</tr>
							
						<%} %>
						</tbody>
						
						</table>
			  
				</form>
				
			</div>
		</div>
	</div>






</body>
</html>


