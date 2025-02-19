Formula Migration :: <%@page import="alain.core.utils.Cartographer"%>
<%@page import="csapi.utils.FormulaMigration"%>
<%@page import="csapi.impl.activity.ActivitySQL"%>
<%@page import="csapi.utils.CsReflect"%>
<%@page import="csshared.vo.ObjGroupVO"%>
<%@page import="csshared.vo.ObjVO"%>
<%@page import="csapi.impl.general.GeneralAgent"%>
<%@page import="csshared.vo.TypeVO"%>
<%@page import="csapi.common.Test1"%>
<%@page import="alain.core.db.Sage"%>
<% 
 
Cartographer map = new Cartographer(request,response);
String db = map.getString("db","");
boolean result = FormulaMigration.formulaMigrate(db);


%>

<%=result%>