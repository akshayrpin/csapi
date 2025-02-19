<%@page import="alain.core.utils.Operator"%><%@page import="alain.core.utils.jencrypt"%><%@page import="csapi.utils.CsApiConfig"%><%@page import="alain.core.utils.Cartographer"%><%

Cartographer map = new Cartographer(request, response);
String password = map.getString("reppass");
String enc = "";
if (Operator.hasValue(password)) {
	enc = jencrypt.aesencrypt(password, CsApiConfig.getRepSalt());
}

%>
<html>

<body>

<form method="get" action="encrypt.jsp">
<input type="text" name="reppass"/>
<input type="submit" value="encrypt"/>
</form>
<br/><br/>
<%= enc %>

</body>
</html>