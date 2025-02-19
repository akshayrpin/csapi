<%@page import="alain.core.utils.Operator"%>
<%@page import="csapi.impl.admin.AdminAgent"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="csapi.utils.CsApiConfig"%>
<%@page import="alain.core.utils.Cartographer"%>
<% 

	Cartographer map = new Cartographer(request,response);
	
	int userId = Operator.toInt(map.getString("_userId"));
	
	
	
%>

<html>
<head>

</head>

<body>
Welcome to Admin Panel 
<table>
<tr>
	<td>
		
		
			<table>
				
				<tr>
					<td>No Access : Contact your Administrator </td>
				</tr>
			
			</table>
			
	
	
		
	</td>
</tr>
</table>


</body>

</html>


