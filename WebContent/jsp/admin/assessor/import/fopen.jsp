<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" %>
<%@ page import="java.io.BufferedInputStream,java.io.FileInputStream,java.io.FileNotFoundException,java.io.IOException,java.io.File,java.io.InputStream,java.io.OutputStream"%>
<%@ page import="java.util.ResourceBundle"%>
<%@page import="csapi.utils.CsApiConfig"%>
<%@page import="csshared.utils.CsConfig"%> 
<%@page import="alain.core.utils.Config"%>

<html>
<body>  

<% 

String fname ="";
String filePath = Config.getString("files.storage_path");
fname = filePath + "exception/ASSESSOR_EXCEPTION_EXPORT.csv";
String fileName="ASSESSOR_EXCEPTION_EXPORT.csv";

System.out.println("fname... "+fname);
response.setHeader("Content-Disposition", "attachement; filename=\""+fileName+"\"");
String extension = "";
int i = fileName.lastIndexOf('.');
if (i > 0) {
    extension = fileName.substring(i+1);
}

if(extension.equalsIgnoreCase("csv"))
{
  response.setHeader("Content-Type", "text/csv");
}


long fileSize = new File(fname).length();
response.setHeader("Content-Length",fileSize+"");

InputStream inStream=null;
/*Getting output Stream from response*/
OutputStream outStream = response.getOutputStream();
		
		
try
{
inStream = new BufferedInputStream(new FileInputStream(fname));
/*Setting buffer size*/
byte[] buf = new byte[8 * 1024];
int bytesRead = 0;

/*Reading file in buffer stream*/

while ((bytesRead = inStream.read(buf)) != -1) {
outStream.write(buf, 0, buf.length);
}
out.clear(); // where out is a JspWriter
out = pageContext.pushBody();
}
catch (IOException e)
{
//e.printStackTrace();
}

inStream.close();

%> 

</body>
</html>