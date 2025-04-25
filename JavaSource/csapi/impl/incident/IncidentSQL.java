package csapi.impl.incident;

import alain.core.security.Token;
import alain.core.utils.Operator;
import csapi.utils.CsReflect;

public class IncidentSQL {
	
	public static String info(String type, int typeid, int id, Token u) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( SELECT COUNT(DISTINCT ID) AS CONTENT_COUNT FROM CONTENT WHERE ACTIVE = 'Y' AND LOWER(TYPE) = 'incident') ");
		sb.append(" SELECT ");
		

		sb.append(" I.ID ");
		sb.append(" , ");
		sb.append(" LKUP_INCIDENT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" I1.DESCRIPTION LKUP_INCIDENT_TYPE_TEXT");
		sb.append(" , ");
		sb.append(" LKUP_INCIDENT_STATUS_ID");
		sb.append(" , ");
		sb.append(" I2.DESCRIPTION LKUP_INCIDENT_STATUS_TEXT");
		sb.append(" , ");
		sb.append(" I.DESCRIPTION");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" I.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" I.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" I.UPDATED_DATE, ");
		sb.append("   CASE ");
		sb.append("     WHEN Q.CONTENT_COUNT IS NULL THEN '' ");
		sb.append("     WHEN Q.CONTENT_COUNT > 0 THEN 'incident' ");
		sb.append("   ELSE '' END AS CONTENT_TYPE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_INCIDENT AS R ");
		sb.append(" JOIN INCIDENT AS I ON R.INCIDENT_ID = I.ID");
		sb.append(" LEFT OUTER JOIN LKUP_INCIDENT_TYPE AS I1 ON I.LKUP_INCIDENT_TYPE_ID = I1.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_INCIDENT_STATUS AS I2 ON I.LKUP_INCIDENT_STATUS_ID = I2.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS CU ON I.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON I.UPDATED_BY = UP.ID  ");
		sb.append(" AND ");
		sb.append(" R.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN Q ON 1=1 ");
		sb.append(" WHERE ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" AND ");
		sb.append(" R.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" I.ACTIVE = 'Y' ");
//		sb.append(" AND ");
//		sb.append(" R.LKUP_HOLDS_STATUS_ID <> 3 ");
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" R.ID = ").append(id);
		}
		return sb.toString();
	}

	public static String summary(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
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
		sb.append(" I.ID ");
		sb.append(" , ");
		sb.append(" LKUP_INCIDENT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" I1.DESCRIPTION LKUP_INCIDENT_TYPE_TEXT");
		sb.append(" , ");
		sb.append(" LKUP_INCIDENT_STATUS_ID");
		sb.append(" , ");
		sb.append(" I2.DESCRIPTION LKUP_INCIDENT_STATUS_TEXT");
		sb.append(" , ");
		sb.append(" I.DESCRIPTION");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" I.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" I.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" I.UPDATED_DATE ");
		
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_INCIDENT AS R ");
		sb.append(" JOIN INCIDENT AS I ON R.INCIDENT_ID = I.ID");
		sb.append(" LEFT OUTER JOIN LKUP_INCIDENT_TYPE AS I1 ON I.LKUP_INCIDENT_TYPE_ID = I1.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_INCIDENT_STATUS AS I2 ON I.LKUP_INCIDENT_STATUS_ID = I2.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS CU ON I.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON I.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" R.ACTIVE = 'Y' AND ");
		sb.append(" R.").append(idref).append(" = ").append(typeid);
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" I.ID = ").append(id);
		}
		return sb.toString();
	}
	
	public static String id(String type, int typeid, int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT I.* FROM ");
		sb.append(" INCIDENT I ");
		sb.append(" LEFT OUTER JOIN USERS AS CU ON P.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON P.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" I.ID = ").append(id);
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
		sb.append(" DESCRIPTION AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" LKUP_INCIDENT_STATUS ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		sb.append(" ORDER BY STATUS ");
		return sb.toString();
	}

	public static String status(String type, int typeid, int selected) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" DESCRIPTION AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		if (selected > 0) {
			sb.append(" , ");
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" LKUP_INCIDENT_STATUS ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY STATUS ");
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
		sb.append(" DESCRIPTION AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" LKUP_INCIDENT_TYPE ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		sb.append(" ORDER BY TYPE ");
		return sb.toString();
	}

	public static String types(String type, int typeid, int selected) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" DESCRIPTION AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		if (selected > 0) {
			sb.append(" , ");
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" LKUP_INCIDENT_TYPE ");
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
		sb.append(" INSERT INTO REF_").append(tableref).append("_INCIDENT ( ");
		sb.append(idref);
		sb.append(" , ");
		sb.append(" INCIDENT_ID ");
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

	public static String add(String inctype, String status, String description, int userid, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO INCIDENT ( ");
		sb.append(" LKUP_INCIDENT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" LKUP_INCIDENT_STATUS_ID ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
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
		sb.append(" ) Output Inserted.* VALUES ( ");
		sb.append(" '").append(Operator.sqlEscape(inctype)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(status)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
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

	public static String update(int id, String inctype, String status, String description, int userid, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE INCIDENT SET ");
		sb.append(" LKUP_INCIDENT_TYPE_ID = ");
		sb.append(" '").append(Operator.sqlEscape(inctype)).append("' ");
		sb.append(" , ");
		sb.append(" LKUP_INCIDENT_STATUS_ID = ");
		sb.append(" '").append(Operator.sqlEscape(status)).append("' ");
		sb.append(" , ");
		sb.append(" DESCRIPTION = ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(" ACTIVE = 'Y' ");
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
