package csapi.impl.projectinfo;

import alain.core.security.Token;
import alain.core.utils.Operator;
import csapi.utils.CsReflect;

public class ProjectinfoSQL {
	
	public static String info(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}

	public static String summary(String type, int typeid, int id, Token u) {
		if (!Operator.hasValue(type)) { return ""; }
		if(type.equals("activity")) {
			return activitySummary(type, typeid, id, u);
		}
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" P.ID ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_PROJECT_ID ");
		sb.append(" , ");
		sb.append(" CASE WHEN P1.TYPE = 'Other' then P1.TYPE +' - '+ OTHER_PROJECT_TYPE ELSE P1.DESCRIPTION END LKUP_PROJECTINFO_PROJECT_TEXT ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGTYPE_ID");
		sb.append(" , ");
		sb.append(" CASE WHEN P2.TYPE = 'Other' then P2.TYPE +' - '+  OTHER_BUILDING_TYPE ELSE P2.DESCRIPTION END LKUP_PROJECTINFO_BUILDINGTYPE_TEXT ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGUSE_ID");
		sb.append(" , ");
		sb.append(" CASE WHEN (P3.TYPE = 'Other' OR p3.TYPE = 'Mixed Use') then P3.TYPE + ' - ' + OTHER_BUILDING_USE ELSE CASE WHEN (P3.TYPE = 'Change of Use') then 'Existing &nbsp;&nbsp;&nbsp;- ' + P5.DESCRIPTION  + '<br>Proposed - ' + P6.DESCRIPTION ELSE P3.DESCRIPTION END END LKUP_PROJECTINFO_BUILDINGUSE_TEXT ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_USE_ID");
		sb.append(" , ");
		sb.append(" P4.DESCRIPTION LKUP_PROJECTINFO_USE_TEXT");
		sb.append(" , ");
		/*sb.append(" OTHER_PROJECT_TYPE");
		sb.append(" , ");
		sb.append(" OTHER_BUILDING_TYPE");
		sb.append(" , ");
		sb.append(" OTHER_BUILDING_USE");
		sb.append(" , ");
		sb.append(" P5.DESCRIPTION OTHER_USE_EXISTING");
		sb.append(" , ");
		sb.append(" P6.DESCRIPTION OTHER_USE_PROPOSED");
		sb.append(" , ");*/
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" P.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" P.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" P.UPDATED_DATE ");
		sb.append(" , 'project' AS REF");
		sb.append(" , PP.ID AS REF_ID");
		
		sb.append(" FROM ");
		sb.append(" PROJECT AS PP ");
		sb.append(" LEFT OUTER JOIN REF_").append(tableref).append("_PROJECTINFO AS R  ON PP.ID = R.PROJECT_ID ");
		sb.append(" LEFT OUTER JOIN PROJECTINFO AS P ON R.PROJECTINFO_ID = P.ID");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_PROJECT AS P1 ON P.LKUP_PROJECTINFO_PROJECT_ID = P1.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_BUILDINGTYPE AS P2 ON P.LKUP_PROJECTINFO_BUILDINGTYPE_ID = P2.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_BUILDINGUSE AS P3 ON P.LKUP_PROJECTINFO_BUILDINGUSE_ID = P3.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_USE AS P4 ON P.LKUP_PROJECTINFO_USE_ID = P4.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_BUILDINGUSE AS P5 ON OTHER_USE_EXISTING = P5.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_BUILDINGUSE AS P6 ON OTHER_USE_PROPOSED = P6.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS CU ON P.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON P.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" PP.ID = ").append(typeid);
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" P.ID = ").append(id);
		}
		return sb.toString();
	}

	private static String activitySummary(String type, int typeid, int id, Token u) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" P.ID ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_PROJECT_ID ");
		sb.append(" , ");
		sb.append(" CASE WHEN P1.TYPE = 'Other' then P1.TYPE +' - '+ OTHER_PROJECT_TYPE ELSE P1.DESCRIPTION END LKUP_PROJECTINFO_PROJECT_TEXT ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGTYPE_ID");
		sb.append(" , ");
		sb.append(" CASE WHEN P2.TYPE = 'Other' then P2.TYPE +' - '+  OTHER_BUILDING_TYPE ELSE P2.DESCRIPTION END LKUP_PROJECTINFO_BUILDINGTYPE_TEXT ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGUSE_ID");
		sb.append(" , ");
		sb.append(" CASE WHEN (P3.TYPE = 'Other' OR p3.TYPE = 'Mixed Use') then P3.TYPE + ' - ' + OTHER_BUILDING_USE ELSE CASE WHEN (P3.TYPE = 'Change of Use') then 'Existing &nbsp;&nbsp;&nbsp;- ' + P5.DESCRIPTION  + '<br>Proposed - ' + P6.DESCRIPTION ELSE P3.DESCRIPTION END END LKUP_PROJECTINFO_BUILDINGUSE_TEXT ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_USE_ID");
		sb.append(" , ");
		sb.append(" P4.DESCRIPTION LKUP_PROJECTINFO_USE_TEXT");
		sb.append(" , ");
		/*sb.append(" OTHER_PROJECT_TYPE");
		sb.append(" , ");
		sb.append(" OTHER_BUILDING_TYPE");
		sb.append(" , ");
		sb.append(" OTHER_BUILDING_USE");
		sb.append(" , ");
		sb.append(" P5.DESCRIPTION OTHER_USE_EXISTING");
		sb.append(" , ");
		sb.append(" P6.DESCRIPTION OTHER_USE_PROPOSED");
		sb.append(" , ");*/
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" P.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" P.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" P.UPDATED_DATE ");
		sb.append(" , 'project' AS REF");
		sb.append(" , A.PROJECT_ID AS REF_ID");
		
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" LEFT OUTER JOIN REF_PROJECT_PROJECTINFO AS R ON A.PROJECT_ID = R.PROJECT_ID");
		sb.append(" LEFT OUTER JOIN PROJECTINFO AS P ON R.PROJECTINFO_ID = P.ID");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_PROJECT AS P1 ON P.LKUP_PROJECTINFO_PROJECT_ID = P1.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_BUILDINGTYPE AS P2 ON P.LKUP_PROJECTINFO_BUILDINGTYPE_ID = P2.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_BUILDINGUSE AS P3 ON P.LKUP_PROJECTINFO_BUILDINGUSE_ID = P3.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_USE AS P4 ON P.LKUP_PROJECTINFO_USE_ID = P4.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_BUILDINGUSE AS P5 ON OTHER_USE_EXISTING = P5.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_BUILDINGUSE AS P6 ON OTHER_USE_PROPOSED = P6.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS CU ON P.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON P.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" A.ID = ").append(typeid);
		return sb.toString();
	}

	public static String ext(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}

	public static String list(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}

	public static String details(String type, int typeid, int id, Token u) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" P.ID ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_PROJECT_ID ");
		sb.append(" , ");
		sb.append(" P1.DESCRIPTION LKUP_PROJECTINFO_PROJECT_TEXT");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGTYPE_ID");
		sb.append(" , ");
		sb.append(" P2.DESCRIPTION LKUP_PROJECTINFO_BUILDINGTYPE_TEXT");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGUSE_ID");
		sb.append(" , ");
		sb.append(" P3.DESCRIPTION LKUP_PROJECTINFO_BUILDINGUSE_TEXT");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_USE_ID");
		sb.append(" , ");
		sb.append(" P4.DESCRIPTION LKUP_PROJECTINFO_USE_TEXT");
		sb.append(" , ");
		sb.append(" OTHER_PROJECT_TYPE");
		sb.append(" , ");
		sb.append(" OTHER_BUILDING_TYPE");
		sb.append(" , ");
		sb.append(" OTHER_BUILDING_USE");
		sb.append(" , ");
		sb.append(" OTHER_USE_EXISTING");
		sb.append(" , ");
		sb.append(" OTHER_USE_PROPOSED");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" P.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" P.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" P.UPDATED_DATE ");
		
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_PROJECTINFO AS R ");
		sb.append(" JOIN PROJECTINFO AS P ON R.PROJECTINFO_ID = P.ID");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_PROJECT AS P1 ON P.LKUP_PROJECTINFO_PROJECT_ID = P1.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_BUILDINGTYPE AS P2 ON P.LKUP_PROJECTINFO_BUILDINGTYPE_ID = P2.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_BUILDINGUSE AS P3 ON P.LKUP_PROJECTINFO_BUILDINGUSE_ID = P3.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECTINFO_USE AS P4 ON P.LKUP_PROJECTINFO_USE_ID = P4.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS CU ON P.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON P.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" R.ACTIVE = 'Y' AND ");
		sb.append(" R.").append(idref).append(" = ").append(typeid);
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" P.ID = ").append(id);
		}
		return sb.toString();
	}
	
	public static String id(String type, int typeid, int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT P.* FROM ");
		sb.append(" PROJECTINFO P ");
		sb.append(" LEFT OUTER JOIN USERS AS CU ON P.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON P.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" P.ID = ").append(id);
		return sb.toString();
	}
	
	public static String status() {
		return status("", -1, -1);
	}

	public static String status(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" LKUP_PROJECTINFO_PROJECT ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		sb.append(" ORDER BY ID ");
		return sb.toString();
	}

	public static String status(String type, int typeid, int selected) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		if (selected > 0) {
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		else {
			sb.append(" DEFLT AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" LKUP_PROJECTINFO_PROJECT ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY ORDR ");
		return sb.toString();
	}
	
	public static String type() {
		return types("", -1, -1);
	}

	public static String types(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGTYPE ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		sb.append(" ORDER BY ORDR ");
		return sb.toString();
	}

	public static String types(String type, int typeid, int selected) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		if (selected > 0) {
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		else {
			sb.append(" DEFLT AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGTYPE ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY ORDR ");
		return sb.toString();
	}
	
	public static String typedescriptions() {
		return typedescriptions("", -1, -1);
	}

	public static String typedescriptions(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGUSE ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		sb.append(" ORDER BY ORDR ");
		return sb.toString();
	}

	public static String typedescriptions(String type, int typeid, int selected) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		if (selected > 0) {
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		else {
			sb.append(" DEFLT AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGUSE ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY ORDR ");
		return sb.toString();
	}
	
	public static String statusdescriptions() {
		return statusdescriptions("", -1, -1);
	}

	public static String statusdescriptions(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" LKUP_PROJECTINFO_USE ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		sb.append(" ORDER BY TYPE ");
		return sb.toString();
	}

	public static String statusdescriptions(String type, int typeid, int selected) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		if (selected > 0) {
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		else {
			sb.append(" DEFLT AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" LKUP_PROJECTINFO_USE ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY TYPE ");
		return sb.toString();
	}

	public static String addRef(String type, int typeid, int noteid, int userid, String ip) {
		if (!Operator.hasValue(type)) { return ""; }
		if (typeid < 1) { return ""; }
		if (noteid < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_PROJECTINFO ( ");
		sb.append(idref);
		sb.append(" , ");
		sb.append(" PROJECTINFO_ID ");
		sb.append(" , ");
		sb.append(" ACTIVE ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(typeid);
		sb.append(" , ");
		sb.append(noteid);
		sb.append(" , ");
		sb.append(" 'Y' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String add(String ptype, String btype, String buse, String cuse, String pextra, String bextra, String buextra, String cextraex, String cextrapx, int userid, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO PROJECTINFO ( ");
		sb.append(" LKUP_PROJECTINFO_PROJECT_ID ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGTYPE_ID ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGUSE_ID ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_USE_ID ");
		sb.append(" , ");
		sb.append(" OTHER_PROJECT_TYPE ");
		sb.append(" , ");
		sb.append(" OTHER_BUILDING_TYPE ");
		sb.append(" , ");
		sb.append(" OTHER_BUILDING_USE ");
		sb.append(" , ");
		sb.append(" OTHER_USE_EXISTING ");
		sb.append(" , ");
		sb.append(" OTHER_USE_PROPOSED ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) Output Inserted.* VALUES ( ");
		sb.append(" '").append(Operator.sqlEscape(ptype)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(btype)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(buse)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(cuse)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(pextra)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(bextra)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(buextra)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(cextraex)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(cextrapx)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String update(int id, String ptype, String btype, String buse, String cuse, String pextra, String bextra, String buextra, String cextraex, String cextrapr, int userid, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE PROJECTINFO SET ");
		sb.append(" LKUP_PROJECTINFO_PROJECT_ID = ");
		sb.append(" '").append(Operator.sqlEscape(ptype)).append("' ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGTYPE_ID = ");
		sb.append(" '").append(Operator.sqlEscape(btype)).append("' ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_BUILDINGUSE_ID = ");
		sb.append(" '").append(Operator.sqlEscape(buse)).append("' ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECTINFO_USE_ID = ");
		sb.append(" '").append(Operator.sqlEscape(cuse)).append("' ");
		sb.append(" , ");
		sb.append(" OTHER_PROJECT_TYPE = ");
		sb.append(" '").append(Operator.sqlEscape(pextra)).append("' ");
		sb.append(" , ");
		sb.append(" OTHER_BUILDING_TYPE = ");
		sb.append(" '").append(Operator.sqlEscape(bextra)).append("' ");
		sb.append(" , ");
		sb.append(" OTHER_BUILDING_USE = ");
		sb.append(" '").append(Operator.sqlEscape(buextra)).append("' ");
		sb.append(" , ");
		sb.append(" OTHER_USE_EXISTING = ");
		sb.append(" '").append(Operator.sqlEscape(cextraex)).append("' ");
		sb.append(" , ");
		sb.append(" OTHER_USE_PROPOSED = ");
		sb.append(" '").append(Operator.sqlEscape(cextrapr)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = CURRENT_TIMESTAMP ");
		sb.append(" WHERE ");
		sb.append(" ID = ");
		sb.append(id);
		return sb.toString();
	}
}
