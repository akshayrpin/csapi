package csapi.impl.team;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.utils.CsReflect;

public class TeamSQL {

	public static String summary(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		String r = "";
		if (Operator.equalsIgnoreCase(type, "activity")) {
			r = actSummary(typeid);
		}
		else if (Operator.equalsIgnoreCase(type, "comboreview")) {
			r = comboreviewSummary(typeid);
		}
		else {
			r = summary (type, typeid, "N");
		}
		return r;
	}

	public static String comboreviewSummary(int reviewrefid) {
		StringBuilder sb = new StringBuilder();
		sb.append("   SELECT ");
		sb.append("     RT.ID AS ID, ");
		sb.append("     U.ID AS USERS_ID, ");

		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("         ELSE '' ");
		sb.append("       END ");
		sb.append("     )) AS NAME, ");
		sb.append("     T.TYPE, ");
		sb.append("     U.EMAIL, ");
		sb.append("     S.TITLE, ");
		sb.append("     D.DEPT AS DEPARTMENT ");
		sb.append(" FROM ");
		sb.append("   REF_COMBOREVIEW_TEAM AS REF");
		sb.append("   JOIN REF_TEAM AS RT ON REF.REF_TEAM_ID = RT.ID AND REF.REF_COMBOREVIEW_REVIEW_ID = ").append(reviewrefid);
		sb.append("   JOIN LKUP_TEAM_TYPE T on RT.LKUP_TEAM_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append("   JOIN USERS AS U ON RT.USERS_ID = U.ID AND RT.USERS_ID > 0 AND U.ACTIVE = 'Y' ");
		sb.append("   LEFT OUTER JOIN ( ");
		sb.append("     STAFF AS S ");
		sb.append("     LEFT OUTER JOIN DEPARTMENT AS D ON S.DEPARTMENT_ID = D.ID ");
		sb.append("   ) ON U.ID = S.USERS_ID AND S.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String summary(String type, int typeid, String disableeditval) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		
		StringBuilder sb = new StringBuilder();
		
		sb = new StringBuilder();
		sb.append("   SELECT ");
		sb.append("     R.ID AS ID, ");
		sb.append("     U.ID AS USERS_ID, ");

		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("         ELSE '' ");
		sb.append("       END ");
		sb.append("     )) AS NAME, ");
		sb.append("     T.TYPE, ");
		sb.append("     U.EMAIL, ");
		sb.append("     S.TITLE, ");
		sb.append("     D.DEPT AS DEPARTMENT ");
		if (Operator.hasValue(disableeditval)) {
			sb.append("     , ");
			sb.append("     '").append(Operator.sqlEscape(disableeditval)).append("' AS DISABLEEDIT ");
		}

		sb.append("   FROM ");
		sb.append("   REF_").append(tableref).append("_TEAM AS REF ");
		sb.append("   JOIN REF_TEAM AS R ON REF.REF_TEAM_ID = R.ID AND R.ACTIVE = 'Y' AND REF.ACTIVE = 'Y' ");
		sb.append("   JOIN LKUP_TEAM_TYPE T on R.LKUP_TEAM_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append("   JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0 AND U.ACTIVE = 'Y' ");
		sb.append("   LEFT OUTER JOIN ( ");
		sb.append("     STAFF AS S ");
		sb.append("     LEFT OUTER JOIN DEPARTMENT AS D ON S.DEPARTMENT_ID = D.ID ");
		sb.append("   ) ON U.ID = S.USERS_ID AND S.ACTIVE = 'Y' ");
		sb.append("   WHERE ");
		sb.append("     REF.").append(idref).append(" = ").append(typeid).append(" ");

		return sb.toString();
	}


	public static String actSummary(int actid) {
		if (actid < 1) { return ""; }
		String r = "";
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT PROJECT_ID FROM ACTIVITY WHERE ID = ").append(actid);
		String command = sb.toString();
		sb = new StringBuilder();
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			int pid = db.getInt("PROJECT_ID");
			if (pid > 0) {
				sb.append(" WITH Q AS ( ");
				sb.append(summary("project", pid, "Y"));
				sb.append(" UNION ");
				sb.append(summary("activity", actid, "N"));
				sb.append(" ) ");
				sb.append(" SELECT Q.*, 'N' AS ADDABLE FROM Q ");
				r = sb.toString();
			}
		}
		db.clear();
		return r;
	}

	public static String details(String type, int typeid, int id) {
		return summary(type, typeid, -1);
	}

	public static String info(String type, int typeid, int id) {
		String r = summary(type, typeid, -1);
		return r;
	}

	public static String list(String type, int typeid, int id) {
		return summary(type, typeid, -1);
	}

	public static String addTeam(String type, int typeid, int refteamid, int createdby) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ");
		sb.append(" REF_").append(tableref).append("_TEAM ");
		sb.append(" (").append(idref).append(", REF_TEAM_ID, CREATED_BY, UPDATED_BY, ACTIVE) VALUES ( ");
		sb.append(typeid);
		sb.append(" , ");
		sb.append(refteamid);
		sb.append(" , ");
		sb.append(createdby);
		sb.append(" , ");
		sb.append(createdby);
		sb.append(" , ");
		sb.append(" 'Y' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getTeam(String[] ids) {
		if (!Operator.hasValue(ids)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" T.ID, ");
		sb.append(" T.ID AS VALUE, ");
		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("         ELSE '' ");
		sb.append("       END ");
		sb.append("     )) AS TEXT, ");
		sb.append(" 'Y' AS SELECTED ");
		sb.append(" FROM ");
		sb.append(" REF_TEAM AS T ");
		sb.append(" JOIN USERS AS U ON T.USERS_ID = U.ID ");
		sb.append(" WHERE ");
		sb.append(" T.ID IN (").append(Operator.join(ids, ",")).append(") ");
		return sb.toString();
	}

	public static String getTeam(String type, int typeid, int refteamid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ID FROM ");
		sb.append(" REF_").append(tableref).append("_TEAM ");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(" REF_TEAM_ID = ").append(refteamid);
		sb.append(" AND ");
		sb.append(" ACTIVE = 'Y' ");
		return sb.toString();
	}


	public static String deleteTeam(String type, int typeid, int userid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" REF_").append(tableref).append("_TEAM ");
		sb.append(" SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		return sb.toString();
	}

	public static String getTeam(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.ID AS VALUE ");
		sb.append(" , ");

		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("         ELSE '' ");
		sb.append("       END + ");
		sb.append("       CASE  ");
		sb.append("         WHEN T.TYPE IS NOT NULL AND T.TYPE <> '' THEN ' (' + T.TYPE + ') ' ");
		sb.append("         ELSE '' ");
		sb.append("       END ");
		sb.append("     )) AS TEXT ");

		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_TEAM AS REF ");
		sb.append(" JOIN REF_TEAM AS R ON REF.REF_TEAM_ID = R.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_TEAM_TYPE AS T ON R.LKUP_TEAM_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		return sb.toString();
	}

	public static String getTeamType(String type) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append("     R.ID AS VALUE ");
		sb.append("     , ");
		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("         ELSE '' ");
		sb.append("       END + ");
		sb.append("       CASE  ");
		sb.append("         WHEN T.TYPE IS NOT NULL AND T.TYPE <> '' THEN ' (' + T.TYPE + ') ' ");
		sb.append("         ELSE '' ");
		sb.append("       END ");
		sb.append("     )) AS TEXT ");
		sb.append(" FROM ");
		sb.append(" USERS AS U ");
		sb.append(" JOIN REF_TEAM AS R ON U.ID = R.USERS_ID AND R.ACTIVE = 'Y' AND U.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_TEAM_TYPE AS T ON R.LKUP_TEAM_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" LOWER(T.TYPE) = LOWER('").append(Operator.sqlEscape(type)).append("') ");
		return sb.toString();
	}

	public static String copy(String type, int typeid, int newtypeid, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_TEAM ( ");
		sb.append(idref);
		sb.append(" , ");
		sb.append(" REF_TEAM_ID ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_DATE ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append("		").append(newtypeid).append(" AS ").append(idref);
		sb.append(" 	, ");
		sb.append(" 	REF_TEAM_ID ");
		sb.append(" 	, ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" 	, ");
		sb.append(" 	getDate() ");
		sb.append(" 	, ");
		sb.append(" 	getDate() ");
		sb.append(" 	, ");
		sb.append(" 	'").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" 	, ");
		sb.append(" 	'").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_TEAM ");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		sb.append(" AND ACTIVE = 'Y' ");
		return sb.toString();
	}


	public static String addTeam(int userid,int teamTypeId, int creator, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_TEAM ( ");
		sb.append(" USERS_ID ");
		sb.append(" , ");
		sb.append(" LKUP_TEAM_TYPE_ID ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_DATE ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) OUTPUT Inserted.* VALUES ( ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(teamTypeId);
		sb.append(" , ");
		sb.append(creator);
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(creator);
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getUserTeamId(int userid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(" REF_TEAM ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" AND USERS_ID = "+userid+"");
		sb.append(" ORDER BY CREATED_DATE DESC ");
		return sb.toString();
	}
}
























