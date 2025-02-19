package csapi.impl.users;

import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import alain.core.utils.jencrypt;
import csapi.common.Table;
import csapi.utils.CsApiConfig;
import csapi.utils.CsReflect;

public class UsersSQL {
	
	public static String type(int typeid){
		StringBuilder sb = new StringBuilder();
		sb.append("   SELECT ");
		sb.append("     AU.ID AS ID, ");
		sb.append("     AU.ID AS SUBTITLE, ");
		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN AU.FIRST_NAME IS NOT NULL AND AU.FIRST_NAME <> '' THEN AU.FIRST_NAME ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN AU.MI IS NOT NULL AND AU.MI <> '' THEN ' ' + AU.MI ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN AU.LAST_NAME IS NOT NULL AND AU.LAST_NAME <> '' THEN ' ' + AU.LAST_NAME ");
		sb.append("         ELSE '' ");
		sb.append("       END + ");
		sb.append("       CASE  ");
		sb.append("         WHEN AU.EMAIL IS NOT NULL AND AU.EMAIL <> '' THEN ' (' + AU.EMAIL + ') ' ");
		sb.append("         ELSE '' ");
		sb.append("       END ");
		sb.append("     )) AS TITLE ");

		sb.append("   FROM ");
		sb.append("   USERS AS AU ");
		//sb.append("   LEFT OUTER JOIN USERS_GROUP AS AG ON ARU.USERS_GROUP_ID = AG.ID AND ARU.USERS_ID < 1 AND AG.ACTIVE = 'Y' ");
		sb.append("   WHERE ");
		sb.append("     AU.ID IN ( ").append(typeid).append(") ");
		
		
		return sb.toString();
	}

	// DELETE FROM REF_USERS TABLE
	public static String deleteUser(String type, int typeid, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" REF_").append(tableref).append("_USERS ");
		sb.append(" SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		return sb.toString();
	}

	// ADD TO REF_USERS TABLE
	public static String addUser(String type, int typeid, int userid, int createdby, String ip) {
		Timekeeper d = new Timekeeper();
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ");
		sb.append(" REF_").append(tableref).append("_USERS ");
		sb.append(" (").append(idref).append(", USERS_ID, CREATED_BY, UPDATED_BY, CREATED_DATE, UPDATED_DATE, CREATED_IP, UPDATED_IP, ACTIVE) VALUES ( ");
		sb.append(typeid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(createdby);
		sb.append(" , ");
		sb.append(createdby);
		sb.append(" , ");
		sb.append(d.sqlDatetime());
		sb.append(" , ");
		sb.append(d.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" 'Y' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getUser(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM USERS WHERE LOWER(USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') AND ACTIVE = 'Y' ");
		return sb.toString();
	}

	// UPDATE USERS TABLE
	public static String updateUsers(int userid, String username, String fname, String mname, String lname, String email, int updater, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE USERS SET ");
		sb.append(" USERNAME = '").append(Operator.sqlEscape(username)).append("' ");
		sb.append(" , ");
		sb.append(" FIRST_NAME = '").append(Operator.sqlEscape(fname)).append("' ");
		sb.append(" , ");
		sb.append(" MI = '").append(Operator.sqlEscape(mname)).append("' ");
		sb.append(" , ");
		sb.append(" LAST_NAME = '").append(Operator.sqlEscape(lname)).append("' ");
		sb.append(" , ");
		sb.append(" EMAIL = '").append(Operator.sqlEscape(email)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(updater);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ID = ").append(userid);
		return sb.toString();
	}

	public static String updateUsers(int userid, String fname, String mname, String lname, String email, int updater, String ip,String notify) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE USERS SET ");
		sb.append(" FIRST_NAME = '").append(Operator.sqlEscape(fname)).append("' ");
		sb.append(" , ");
		sb.append(" MI = '").append(Operator.sqlEscape(mname)).append("' ");
		sb.append(" , ");
		sb.append(" LAST_NAME = '").append(Operator.sqlEscape(lname)).append("' ");
		sb.append(" , ");
		sb.append(" EMAIL = '").append(Operator.sqlEscape(email)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(updater);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" NOTIFY = '").append(Operator.sqlEscape(notify)).append("' ");
		sb.append(" WHERE ID = ").append(userid);
		return sb.toString();
	}

	public static String addUsers(String username, String fname, String mname, String lname, String email, int userid, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO USERS ( ");
		sb.append(" USERNAME ");
		sb.append(" , ");
		sb.append(" FIRST_NAME ");
		sb.append(" , ");
		sb.append(" MI ");
		sb.append(" , ");
		sb.append(" LAST_NAME ");
		sb.append(" , ");
		sb.append(" EMAIL ");
		sb.append(" , ");
		sb.append(" LAST_LOGIN ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_DATE ");
		sb.append(" , ");
		sb.append(" CREATED_IP  ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" UPDATED_IP  ");
		sb.append(" ) OUTPUT Inserted.*  VALUES ( ");
		sb.append(" '").append(Operator.sqlEscape(username)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(fname)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(mname)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(lname)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(email)).append("' ");
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getUsername(String username) {
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(" USERS ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" AND LOWER(USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" ORDER BY CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String getUserEmail(String email) {
		if (!Operator.hasValue(email)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(" USERS ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" AND LOWER(EMAIL) = LOWER('").append(Operator.sqlEscape(email)).append("') ");
		sb.append(" ORDER BY CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String getUsernameCount(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	U.ID ");
		sb.append(" 	, ");
		sb.append(" 	U.USERNAME ");
		sb.append(" 	, ");
		sb.append(" 	U.CREATED_DATE ");
		sb.append(" 	, ");
		sb.append(" 	COUNT(P.ID) AS PROJECTS ");
		sb.append(" 	, ");
		sb.append(" 	COUNT(A.ID) AS ACTIVITIES ");
		sb.append(" 	, ");
		sb.append(" 	COUNT(D.ID) AS DEPOSITS ");
		sb.append(" FROM ");
		sb.append(" 	USERS AS U ");
		sb.append(" 	LEFT OUTER JOIN REF_USERS AS R ON U.ID = R.USERS_ID AND R.ACTIVE = 'Y' ");
		sb.append(" 	LEFT OUTER JOIN REF_PROJECT_USERS AS P ON R.ID = P.REF_USERS_ID AND P.ACTIVE = 'Y' ");
		sb.append(" 	LEFT OUTER JOIN REF_ACT_USERS AS A ON R.ID = A.REF_USERS_ID AND A.ACTIVE = 'Y' ");
		sb.append(" 	LEFT OUTER JOIN REF_USERS_DEPOSIT AS D ON U.ID = D.USERS_ID AND D.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" 	U.ACTIVE = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" GROUP BY ");
		sb.append(" 	U.ID ");
		sb.append(" 	, ");
		sb.append(" 	U.USERNAME ");
		sb.append(" 	, ");
		sb.append(" 	U.CREATED_DATE ");
		sb.append(" ) ");
		sb.append(" SELECT * FROM Q ");
		sb.append(" ORDER BY PROJECTS DESC, ACTIVITIES DESC, DEPOSITS DESC, CREATED_DATE DESC, ID DESC ");
		return sb.toString();
	}

	public static String getLastUserid(String username) {
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" 	U.* ");
		sb.append(" FROM ");
		sb.append(" 	USERS AS U ");
		sb.append(" WHERE ");
		sb.append(" 	U.ACTIVE = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" ORDER BY CREATED_DATE DESC, UPDATED_DATE DESC ");
		return sb.toString();
	}

	public static String getUsers(int id) {
		StringBuilder sb = new StringBuilder();
		if (id > 0) {
			sb.append(" SELECT U.*, ");
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
			sb.append("     )) AS NAME ");
			sb.append(" FROM USERS AS U WHERE U.ID = ").append(id);
		}
		return sb.toString();
	}

	public static String updateUsername(int usersid) {
		if (usersid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE USERS SET ");
		sb.append(" USERNAME = (SELECT EMAIL FROM USERS WHERE ID = ").append(usersid).append(") ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(usersid);
		return sb.toString();
	}

	public static String updateUsername(int usersid, String username) {
		if (!Operator.hasValue(username)) { return ""; }
		if (usersid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE USERS SET ");
		sb.append(" USERNAME = '").append(Operator.sqlEscape(username)).append("' ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(usersid);
		return sb.toString();
	}

	public static String updateUsernameEmail(int usersid, String username) {
		if (!Operator.hasValue(username)) { return ""; }
		if (usersid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE USERS SET ");
		sb.append(" USERNAME = '").append(Operator.sqlEscape(username)).append("' ");
		sb.append(" , ");
		sb.append(" EMAIL = '").append(Operator.sqlEscape(username)).append("' ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(usersid);
		return sb.toString();
	}

	public static String mergeRefUser(int activeuserid, String username) {
		if (!Operator.hasValue(username)) { return ""; }
		if (activeuserid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_USERS SET ");
		sb.append(" USERS_ID = ").append(activeuserid);
		sb.append(" , ");
		sb.append(" UPDATED_BY = -1 ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" USERS_ID IN (SELECT ID FROM USERS WHERE LOWER(USERNAME)=LOWER('").append(Operator.sqlEscape(username)).append("') ) ");
		return sb.toString();
	}

	public static String mergeRefUserDeposit(int activeuserid, String username) {
		if (!Operator.hasValue(username)) { return ""; }
		if (activeuserid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_USERS_DEPOSIT SET ");
		sb.append(" USERS_ID = ").append(activeuserid);
		sb.append(" , ");
		sb.append(" UPDATED_BY = -1 ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" USERS_ID IN (SELECT ID FROM USERS WHERE LOWER(USERNAME)=LOWER('").append(Operator.sqlEscape(username)).append("') ) ");
		return sb.toString();
	}

	public static String mergePayee(int activeuserid, String username) {
		if (!Operator.hasValue(username)) { return ""; }
		if (activeuserid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE PAYMENT SET ");
		sb.append(" PAYEE_ID = ").append(activeuserid);
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" PAYEE_ID IN (SELECT ID FROM USERS WHERE LOWER(USERNAME)=LOWER('").append(Operator.sqlEscape(username)).append("') ) ");
		return sb.toString();
	}

	public static String mergeUserGroup(int activeuserid, String username) {
		if (!Operator.hasValue(username)) { return ""; }
		if (activeuserid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_USERS_GROUP SET ");
		sb.append(" USERS_ID = ").append(activeuserid);
		sb.append(" , ");
		sb.append(" UPDATED_BY = -1 ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" USERS_ID IN (SELECT ID FROM USERS WHERE LOWER(USERNAME)=LOWER('").append(Operator.sqlEscape(username)).append("') ) ");
		return sb.toString();
	}

	public static String mergeAppointmentUsers(int activeuserid, String username) {
		if (!Operator.hasValue(username)) { return ""; }
		if (activeuserid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_APPOINTMENT_USERS SET ");
		sb.append(" USERS_ID = ").append(activeuserid);
		sb.append(" , ");
		sb.append(" UPDATED_BY = -1 ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
//		sb.append(" ACTIVE = 'Y' ");
//		sb.append(" AND ");
		sb.append(" USERS_ID IN (SELECT ID FROM USERS WHERE LOWER(USERNAME)=LOWER('").append(Operator.sqlEscape(username)).append("') ) ");
		return sb.toString();
	}

	public static String getRefUserCount(int activeuserid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH C AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	R.LKUP_USERS_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	R.USERS_ID ");
		sb.append(" 	, ");
		sb.append(" 	COUNT(R.ID) AS RECS ");
		sb.append(" FROM ");
		sb.append(" 	REF_USERS AS R ");
		sb.append(" WHERE ");
		sb.append(" 	R.ACTIVE = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	R.USERS_ID = ").append(activeuserid);
		sb.append(" GROUP BY ");
		sb.append(" 	R.LKUP_USERS_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	R.USERS_ID ");
		sb.append(" ), ");
		sb.append(" M AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 	MAX(R.ID) AS MAX_ID ");
		sb.append(" 	, ");
		sb.append(" 	R.LKUP_USERS_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	R.USERS_ID ");
		sb.append(" FROM ");
		sb.append(" 	REF_USERS AS R ");
		sb.append(" WHERE ");
		sb.append(" 	R.ACTIVE = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	R.USERS_ID = ").append(activeuserid);
		sb.append(" GROUP BY ");
		sb.append(" 	R.LKUP_USERS_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	R.USERS_ID ");
		sb.append(" ), ");
		sb.append(" L AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 	MAX(R.ID) AS MAX_LICENSE_ID ");
		sb.append(" 	, ");
		sb.append(" 	R.LKUP_USERS_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	R.USERS_ID ");
		sb.append(" FROM ");
		sb.append(" 	REF_USERS AS R ");
		sb.append(" WHERE ");
		sb.append(" 	R.ACTIVE = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	COALESCE(R.LIC_NO, R.BUS_LIC_NO) IS NOT NULL ");
		sb.append(" 	AND ");
		sb.append(" 	R.USERS_ID = ").append(activeuserid);
		sb.append(" GROUP BY ");
		sb.append(" 	R.LKUP_USERS_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	R.USERS_ID ");
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" C.LKUP_USERS_TYPE_ID ");
		sb.append(" , ");
		sb.append(" C.USERS_ID ");
		sb.append(" , ");
		sb.append(" C.RECS ");
		sb.append(" , ");
		sb.append(" M.MAX_ID ");
		sb.append(" , ");
		sb.append(" L.MAX_LICENSE_ID ");
		sb.append(" FROM ");
		sb.append(" C ");
		sb.append(" LEFT OUTER JOIN M ON C.LKUP_USERS_TYPE_ID = M.LKUP_USERS_TYPE_ID AND C.USERS_ID = M.USERS_ID ");
		sb.append(" LEFT OUTER JOIN L ON C.LKUP_USERS_TYPE_ID = L.LKUP_USERS_TYPE_ID AND C.USERS_ID = L.USERS_ID ");
		return sb.toString();
	}

	public static String disableDuplicateRefUsers(int activerefusersid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_USERS SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = -1 ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" ID <> ").append(activerefusersid);
		sb.append(" AND ");
		sb.append(" USERS_ID = (SELECT USERS_ID FROM REF_USERS WHERE ID = ").append(activerefusersid).append(") ");
		sb.append(" AND ");
		sb.append(" LKUP_USERS_TYPE_ID = (SELECT LKUP_USERS_TYPE_ID FROM REF_USERS WHERE ID = ").append(activerefusersid).append(") ");
		return sb.toString();
	}

	public static String disableDuplicateUsers(int activeuserid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE USERS SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = -1 ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" ID <> ").append(activeuserid);
		sb.append(" AND ");
		sb.append(" LOWER(USERNAME) = (SELECT LOWER(USERNAME) FROM USERS WHERE ID = ").append(activeuserid).append(" AND USERNAME IS NOT NULL AND USERNAME <> '') ");
		return sb.toString();
	}

	public static String getUser(MapSet m){
		StringBuilder sb = new StringBuilder();
		String table = Table.USERSTABLE;
		sb.append(" select DISTINCT U.* ");
		sb.append(" from " ).append(table).append(" U ");
		sb.append("	WHERE U.FIRST_NAME='").append(Operator.sqlEscape(m.getString("FIRST_NAME"))).append("' and U.ACTIVE='Y'  ");
		sb.append("	AND U.EMAIL='").append(Operator.sqlEscape(m.getString("EMAIL"))).append("'");
		sb.append("	AND U.USERNAME='").append(Operator.sqlEscape(m.getString("USERNAME"))).append("'");
		sb.append("	AND U.CREATED_BY='").append(Operator.sqlEscape(m.getString("CREATED_BY"))).append("'");
		return sb.toString();
	}
	
	
	public static String checkUser(MapSet m){
		StringBuilder sb = new StringBuilder();
		String table = Table.USERSTABLE;
		sb.append(" select DISTINCT U.* ");
		sb.append(" from " ).append(table).append(" U ");
		sb.append("	WHERE U.ACTIVE='Y'  ");
	//	sb.append("	AND ( LOWER(U.EMAIL)='").append(Operator.sqlEscape(m.getString("EMAIL").toLowerCase())).append("'");
		//sb.append("	OR LOWER(U.USERNAME)='").append(Operator.sqlEscape(m.getString("USERNAME").toLowerCase())).append("' )");
		sb.append("	AND LOWER(U.USERNAME)='").append(Operator.sqlEscape(m.getString("USERNAME").toLowerCase())).append("' ");
		
		return sb.toString();
	}
	
	public static MapSet getUser(int id){
		MapSet m = new MapSet();
		StringBuilder sb = new StringBuilder();
		String table = Table.USERSTABLE;
		sb.append(" select DISTINCT U.*,");
		sb.append(" CASE WHEN S.ID >0 THEN 'Y' else 'N' END as ENABLE_STAFF,S.ID as STAFF_ID,S.DEPARTMENT_ID,S.EMPL_NUM,S.TITLE,S.DIVISION,S.PHONE_WORK,S.PHONE_CELL   ");
		sb.append(",");	
		sb.append(" CASE WHEN P.ID >0 THEN 'Y' else 'N' END as ENABLE_PEOPLE,P.ID as PEOPLE_ID,P.ADDRESS,P.CITY,P.STATE,P.ZIP,P.ZIP4,P.PHONE_WORK as P_PHONE_WORK,P.PHONE_CELL as P_PHONE_CELL,P.FAX,P.COMMENTS   ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),U.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),U.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from " ).append(table).append(" U ");
		sb.append(" Left outer join STAFF S on U.ID= S.USERS_ID and S.ACTIVE='Y'  ");
		sb.append(" Left outer join PEOPLE P on U.ID= P.USERS_ID and P.ACTIVE='Y'  ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on U.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on U.UPDATED_BY = UP.ID "); 
		sb.append("	WHERE U.ID=").append(id).append(" and U.ACTIVE='Y'  ");
		
		m.add("_getuserquery",sb.toString());
		
		sb = new StringBuilder();
		sb.append("select U.*,L.TYPE,L.DESCRIPTION, ");
		
		sb.append(" CONVERT(VARCHAR(10),U.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),U.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append("  from ").append(Table.REFUSERSTABLE).append(" U JOIN ").append(Table.LKUPUSERTYPETABLE).append(" L on U.LKUP_USERS_TYPE_ID = L.ID  "); 
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on U.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on U.UPDATED_BY = UP.ID "); 
		sb.append(" where U.ACTIVE='Y'  AND U.USERS_ID=").append(id);
		
		m.add("_getusertypesquery",sb.toString());
		
		
		sb = new StringBuilder();
		sb.append("select U.*,L.GROUP_NAME, ");
		
		sb.append(" CONVERT(VARCHAR(10),U.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),U.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append("  from ").append(Table.REFUSERSGROUPTABLE).append(" U JOIN ").append(Table.USERSGROUPTABLE).append(" L on U.USERS_GROUP_ID = L.ID  "); 
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on U.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on U.UPDATED_BY = UP.ID "); 
		sb.append(" where U.ACTIVE='Y'  AND U.USERS_ID=").append(id);
		
		m.add("_getusergroupsquery",sb.toString());
		
		
		sb = new StringBuilder();
		sb.append("select U.*,L.TYPE,L.DESCRIPTION, ");
		
		sb.append(" CONVERT(VARCHAR(10),U.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),U.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append("  from ").append(Table.REFTEAMTABLE).append(" U JOIN ").append(Table.LKUPTEAMTYPETABLE).append(" L on U.LKUP_TEAM_TYPE_ID = L.ID  "); 
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on U.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on U.UPDATED_BY = UP.ID "); 
		sb.append(" where U.ACTIVE='Y'  AND U.USERS_ID=").append(id);
		
		m.add("_getuserteamsquery",sb.toString());
		
		
		sb = new StringBuilder();
		sb.append("select U.*,L.ROLE,L.DESCRIPTION, ");
		
		sb.append(" CONVERT(VARCHAR(10),U.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),U.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append("  from ").append(Table.REFUSERROLESTABLE).append(" U JOIN ").append(Table.LKUPROLESTABLE).append(" L on U.LKUP_ROLES_ID = L.ID  "); 
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on U.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on U.UPDATED_BY = UP.ID "); 
		sb.append(" where U.ACTIVE='Y'  AND U.USERS_ID=").append(id);
		
		m.add("_getuserrolesquery",sb.toString());
		
		sb = new StringBuilder();
		sb.append("select U.*,L.TITLE,L.DESCRIPTION,L.PATH, ");
		
		sb.append(" CONVERT(VARCHAR(10),U.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),U.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append("  from ").append(Table.REFUSERATTACHMENTSTABLE).append(" U JOIN ").append(Table.ATTACHMENTSTABLE).append(" L on U.ATTACHMENT_ID = L.ID  "); 
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on U.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on U.UPDATED_BY = UP.ID "); 
		sb.append(" where U.ACTIVE='Y'  AND U.USERS_ID=").append(id);
		
		m.add("_getuserattachmentsquery",sb.toString());
		
		
		
		/*sb = new StringBuilder();
		
		sb.append(" select CASE WHEN PL.LIC_EXPIRATION_DATE < CURRENT_TIMESTAMP THEN 'Y' ELSE 'N' END AS LIC_EXPIRED,CONVERT(VARCHAR(10),PL.LIC_EXPIRATION_DATE,101) as LIC_EXPIRATION_DATE , PL.*,L.TYPE, ");
		sb.append(" CONVERT(VARCHAR(10),PL.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),PL.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from " ).append(table).append(" U ");
		sb.append(" join REF_USERS PL on U.ID= P.USERS_ID and P.ACTIVE='Y'  ");
		sb.append(" join PEOPLE_LICENSE PL on P.ID= PL.PEOPLE_ID and PL.ACTIVE='Y'  ");
		sb.append(" join LKUP_PEOPLE_LICENSE_TYPE L on PL.LKUP_PEOPLE_LICENSE_TYPE_ID= L.ID and L.ACTIVE='Y'  ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on PL.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on PL.UPDATED_BY = UP.ID "); 
		sb.append("	WHERE U.ID=").append(id).append(" and U.ACTIVE='Y'  ");
		
		m.add("_getpeoplelicensequery",sb.toString());*/
		
		
		sb = new StringBuilder();
		
		sb.append(" select  L.*, ");
		sb.append(" CONVERT(VARCHAR(10),L.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),L.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from " ).append(table).append(" U ");
		sb.append(" join PEOPLE P on U.ID= P.USERS_ID and P.ACTIVE='Y'  ");
		sb.append(" join REF_PEOPLE_ATTACHMENTS PL on P.ID= PL.PEOPLE_ID and PL.ACTIVE='Y'  ");
		sb.append(" join ATTACHMENTS L on PL.ATTACHMENT_ID= L.ID and L.ACTIVE='Y'  ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on L.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on L.UPDATED_BY = UP.ID "); 
		sb.append("	WHERE U.ID=").append(id).append(" and U.ACTIVE='Y'  ");
		
		m.add("_getpeopleattachmentsquery",sb.toString());
		
		sb = new StringBuilder();
		
		sb.append(" select  L.*, ");
		sb.append(" CONVERT(VARCHAR(10),L.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),L.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from " ).append(table).append(" U ");
	
		sb.append(" join REF_USERS_DEPOSIT PL on U.ID= PL.USERS_ID and PL.ACTIVE='Y'  ");
		sb.append(" join DEPOSIT L on PL.DEPOSIT_ID= L.ID and L.ACTIVE='Y' and L.PARENT_ID=0 ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on L.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on L.UPDATED_BY = UP.ID "); 
		sb.append("	WHERE U.ID=").append(id).append(" and LKUP_DEPOSIT_TYPE_ID in (1,2) and U.ACTIVE='Y'  ");
		
		m.add("_getdepositsquery",sb.toString());
		
		return m;
	}

	public static String getUsers(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" U.ID ");
		sb.append(" , ");
		sb.append(" U.FIRST_NAME ");
		sb.append(" , ");
		sb.append(" U.MI ");
		sb.append(" , ");
		sb.append(" U.LAST_NAME ");
		sb.append(" , ");
		sb.append(" U.EMAIL ");
		sb.append(" , ");
		sb.append(" P.ID AS PEOPLE_ID ");
		sb.append(" , ");
		sb.append(" P.ADDRESS ");
		sb.append(" , ");
		sb.append(" P.CITY ");
		sb.append(" , ");
		sb.append(" P.STATE ");
		sb.append(" , ");
		sb.append(" P.ZIP ");
		sb.append(" , ");
		sb.append(" P.ZIP4 ");
		sb.append(" , ");
		sb.append(" P.PHONE_WORK ");
		sb.append(" , ");
		sb.append(" P.PHONE_CELL ");
		sb.append(" , ");
		sb.append(" P.FAX ");
		sb.append(" , ");
		sb.append(" L.ID AS LICENSE_ID ");
		sb.append(" , ");
		sb.append(" LT.ID AS LICENSE_TYPE_ID ");
		sb.append(" , ");
		sb.append(" LT.TYPE AS LICENSE_TYPE ");
		sb.append(" , ");
		sb.append(" L.LIC_NUM ");
		sb.append(" , ");
		sb.append(" L.LIC_EXPIRATION_DATE ");
		sb.append(" , ");
		sb.append(" S.ID AS STAFF_ID ");
		sb.append(" , ");
		sb.append(" S.EMPL_NUM ");
		sb.append(" , ");
		sb.append(" S.TITLE ");
		sb.append(" , ");
		sb.append(" D.DEPARTMENT ");
		sb.append(" , ");
		sb.append(" S.DIVISION ");
		sb.append(" , ");
		sb.append(" S.PHONE_WORK AS STAFF_PHONE ");
		sb.append(" , ");
		sb.append(" S.PHONE_CELL AS STAFF_CELL ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_USERS AS RU ");
		sb.append(" JOIN USERS AS U ON RU.USERS_ID = U.ID AND RU.ACTIVE = 'Y' AND U.ACTIVE = 'Y' AND ").append(idref).append(" = ").append(typeid);
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   PEOPLE AS P ");
		sb.append("   JOIN PEOPLE_LICENSE AS L ON P.ID = L.PEOPLE_ID ");
		sb.append("   JOIN LKUP_PEOPLE_LICENSE AS LT ON L.LKUP_PEOPLE_LICENSE_ID = LT.ID ");
		sb.append(" ) ON U.ID = P.USERS_ID AND P.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   STAFF AS S ");
		sb.append("   JOIN DEPARTMENT AS D ON S.DEPARTMENT_ID = D.ID ");
		sb.append(" )  ON U.ID = S.USERS_ID AND S.ACTIVE = 'Y' ");
		return sb.toString();
	}


	public static String getEmail(String email) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" U.* ");
		sb.append(" , ");
		sb.append(" R.ID AS REF_USERS_ID ");
		sb.append(" , ");
		sb.append(" R.LKUP_USERS_TYPE_ID ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" P.ID AS PEOPLE_ID ");
		sb.append(" , ");
		sb.append(" P.ADDRESS ");
		sb.append(" , ");
		sb.append(" P.CITY ");
		sb.append(" , ");
		sb.append(" P.STATE ");
		sb.append(" , ");
		sb.append(" P.ZIP ");
		sb.append(" , ");
		sb.append(" P.PHONE_WORK ");
		sb.append(" , ");
		sb.append(" P.PHONE_CELL ");
		sb.append(" , ");
		sb.append(" P.PHONE_HOME ");
		sb.append(" , ");
		sb.append(" P.FAX ");
		sb.append(" FROM ");
		sb.append(" REF_USERS AS R ");
		sb.append(" JOIN USERS AS U ON R.USERS_ID = U.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_USERS_TYPE AS T ON R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN PEOPLE AS P ON R.USERS_ID = P.USERS_ID ");
		sb.append(" WHERE ");
		sb.append(" LOWER(U.EMAIL) = LOWER('").append(Operator.sqlEscape(email)).append("') ");
		return sb.toString();
	}

	public static String getUser(String username, int usertype) {
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" U.* ");
		sb.append(" , ");
		sb.append(" P.ID AS PEOPLE_ID ");
		sb.append(" , ");
		sb.append(" P.ADDRESS ");
		sb.append(" , ");
		sb.append(" P.CITY ");
		sb.append(" , ");
		sb.append(" P.STATE ");
		sb.append(" , ");
		sb.append(" P.ZIP ");
		sb.append(" , ");
		sb.append(" P.PHONE_WORK ");
		sb.append(" , ");
		sb.append(" P.PHONE_CELL ");
		sb.append(" , ");
		sb.append(" P.PHONE_HOME ");
		sb.append(" , ");
		sb.append(" P.FAX ");
		if (usertype > 0) {
			sb.append(" , ");
			sb.append(" LIC_NO ");
			sb.append(" , ");
			sb.append(" LIC_EXP_DT ");
			sb.append(" , ");
			sb.append(" BUS_LIC_NO ");
			sb.append(" , ");
			sb.append(" BUS_LIC_EXP_DT ");
			sb.append(" , ");
			sb.append(" GEN_LIABILITY_DT ");
			sb.append(" , ");
			sb.append(" AUTO_LIABILITY_DT ");
			sb.append(" , ");
			sb.append(" WORK_COMP_EXP_DT ");
		}
		sb.append(" FROM ");
		sb.append(" USERS AS U ");
		sb.append(" LEFT OUTER JOIN PEOPLE AS P ON U.ID = P.USERS_ID ");
		if (usertype > 0) {
			sb.append(" LEFT OUTER JOIN REF_USERS AS R ON U.ID = R.USERS_ID AND R.ACTIVE = 'Y' AND R.LKUP_USERS_TYPE_ID = ").append(usertype);
		}
		sb.append(" WHERE ");
		sb.append(" LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		return sb.toString();
	}

	public static String user(String username) {
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" U.* ");
		sb.append(" , ");
		sb.append(" P.ID AS PEOPLE_ID ");
		sb.append(" , ");
		sb.append(" P.ADDRESS ");
		sb.append(" , ");
		sb.append(" P.CITY ");
		sb.append(" , ");
		sb.append(" P.STATE ");
		sb.append(" , ");
		sb.append(" P.ZIP ");
		sb.append(" , ");
		sb.append(" P.PHONE_WORK ");
		sb.append(" , ");
		sb.append(" P.PHONE_CELL ");
		sb.append(" , ");
		sb.append(" P.PHONE_HOME ");
		sb.append(" , ");
		sb.append(" P.FAX ");
		sb.append(" FROM ");
		sb.append(" USERS AS U ");
		sb.append(" LEFT OUTER JOIN PEOPLE AS P ON U.ID = P.USERS_ID ");
		sb.append(" WHERE ");
		sb.append(" LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		return sb.toString();
	}

	public static String email(String email) {
		if (!Operator.hasValue(email)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" U.* ");
		sb.append(" , ");
		sb.append(" P.ID AS PEOPLE_ID ");
		sb.append(" , ");
		sb.append(" P.ADDRESS ");
		sb.append(" , ");
		sb.append(" P.CITY ");
		sb.append(" , ");
		sb.append(" P.STATE ");
		sb.append(" , ");
		sb.append(" P.ZIP ");
		sb.append(" , ");
		sb.append(" P.PHONE_WORK ");
		sb.append(" , ");
		sb.append(" P.PHONE_CELL ");
		sb.append(" , ");
		sb.append(" P.PHONE_HOME ");
		sb.append(" , ");
		sb.append(" P.FAX ");
		sb.append(" FROM ");
		sb.append(" USERS AS U ");
		sb.append(" LEFT OUTER JOIN PEOPLE AS P ON U.ID = P.USERS_ID ");
		sb.append(" WHERE ");
		sb.append(" LOWER(U.EMAIL) = LOWER('").append(Operator.sqlEscape(email)).append("') ");
		return sb.toString();
	}

	public static String getLicense(String username, int usertype) {
		if (!Operator.hasValue(username) || usertype < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" U.* ");
		sb.append(" , ");
		sb.append(" LIC_NO ");
		sb.append(" , ");
		sb.append(" LIC_EXP_DT ");
		sb.append(" , ");
		sb.append(" BUS_LIC_NO ");
		sb.append(" , ");
		sb.append(" BUS_LIC_EXP_DT ");
		sb.append(" , ");
		sb.append(" GEN_LIABILITY_DT ");
		sb.append(" , ");
		sb.append(" AUTO_LIABILITY_DT ");
		sb.append(" , ");
		sb.append(" WORK_COMP_EXP_DT ");
		sb.append(" FROM ");
		sb.append(" USERS AS U ");
		sb.append(" JOIN REF_USERS AS R ON U.ID = R.USERS_ID AND R.ACTIVE = 'Y' AND R.LKUP_USERS_TYPE_ID = ").append(usertype);
		sb.append(" WHERE ");
		sb.append(" LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		return sb.toString();
	}

	public static String getRefUser(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.ID ");
		sb.append(" , ");
		sb.append(" U.ID AS USERS_ID ");
		sb.append(" , ");
		sb.append(" U.FIRST_NAME ");
		sb.append(" , ");
		sb.append(" U.MI ");
		sb.append(" , ");
		sb.append(" U.LAST_NAME ");
		sb.append(" , ");
		sb.append(" U.USERNAME ");
		sb.append(" , ");
		sb.append(" U.EMAIL ");
		sb.append(" , ");
		sb.append(" U.LAST_LOGIN ");
		sb.append(" , ");
		sb.append(" U.ACTIVE ");
		sb.append(" , ");
		sb.append(" U.CREATED_BY ");
		sb.append(" , ");
		sb.append(" U.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" U.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" U.UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" U.CREATED_IP ");
		sb.append(" , ");
		sb.append(" U.UPDATED_IP ");
		sb.append(" , ");
		sb.append(" R.ID AS REF_USERS_ID ");
		sb.append(" , ");
		sb.append(" R.LKUP_USERS_TYPE_ID ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" P.ID AS PEOPLE_ID ");
		sb.append(" , ");
		sb.append(" P.ADDRESS ");
		sb.append(" , ");
		sb.append(" P.CITY ");
		sb.append(" , ");
		sb.append(" P.STATE ");
		sb.append(" , ");
		sb.append(" P.ZIP ");
		sb.append(" , ");
		sb.append(" P.PHONE_WORK ");
		sb.append(" , ");
		sb.append(" P.PHONE_CELL ");
		sb.append(" , ");
		sb.append(" P.PHONE_HOME ");
		sb.append(" , ");
		sb.append(" P.FAX ");
		sb.append(" , ");
		sb.append(" R.LIC_NO ");
		sb.append(" , ");
		sb.append(" R.LIC_EXP_DT ");
		sb.append(" , ");
		sb.append(" R.GEN_LIABILITY_DT ");
		sb.append(" , ");
		sb.append(" R.AUTO_LIABILITY_DT ");
		sb.append(" , ");
		sb.append(" R.WORK_COMP_EXP_DT ");
		sb.append(" , ");
		sb.append(" T.REQUIRED_LICENSE ");
		sb.append(" FROM ");
		sb.append(" REF_USERS AS R ");
		sb.append(" JOIN USERS AS U ON R.USERS_ID = U.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_USERS_TYPE AS T ON R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN PEOPLE AS P ON R.USERS_ID = P.USERS_ID ");
		sb.append(" WHERE ");
		sb.append(" R.ID = ").append(id);
		return sb.toString();
	}

	public static String getRefUser(int userid, int usertypeid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" U.* ");
		sb.append(" , ");
		sb.append(" R.ID AS REF_USERS_ID ");
		sb.append(" , ");
		sb.append(" R.LKUP_USERS_TYPE_ID ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" P.ADDRESS ");
		sb.append(" , ");
		sb.append(" P.CITY ");
		sb.append(" , ");
		sb.append(" P.STATE ");
		sb.append(" , ");
		sb.append(" P.ZIP ");
		sb.append(" , ");
		sb.append(" P.PHONE_WORK ");
		sb.append(" , ");
		sb.append(" P.PHONE_CELL ");
		sb.append(" , ");
		sb.append(" P.PHONE_HOME ");
		sb.append(" , ");
		sb.append(" P.FAX ");
		sb.append(" , ");
		sb.append(" R.LIC_NO ");
		sb.append(" , ");
		sb.append(" R.LIC_EXP_DT ");
		sb.append(" , ");
		sb.append(" R.BUS_LIC_NO ");
		sb.append(" , ");
		sb.append(" R.BUS_LIC_EXP_DT ");
		sb.append(" , ");
		sb.append(" R.GEN_LIABILITY_DT ");
		sb.append(" , ");
		sb.append(" R.AUTO_LIABILITY_DT ");
		sb.append(" , ");
		sb.append(" R.WORK_COMP_EXP_DT ");
		sb.append(" FROM ");
		sb.append(" REF_USERS AS R ");
		sb.append(" JOIN USERS AS U ON R.USERS_ID = U.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_USERS_TYPE AS T ON R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN PEOPLE AS P ON R.USERS_ID = P.USERS_ID ");
		sb.append(" WHERE ");
		sb.append(" R.USERS_ID = ").append(userid);
		sb.append(" AND ");
		sb.append(" R.LKUP_USERS_TYPE_ID = ").append(usertypeid);
		sb.append(" ORDER BY R.UPDATED_DATE DESC, U.CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String addRefUser(int userid, int usertypeid, String licno, String licexp, String genliability, String autoliability, String workcomp, int creator, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_USERS ( ");
		sb.append(" USERS_ID, LKUP_USERS_TYPE_ID, LIC_NO, LIC_EXP_DT, CREATED_BY, CREATED_DATE, CREATED_IP, UPDATED_BY, UPDATED_DATE, UPDATED_IP ");
		sb.append(" ) OUTPUT Inserted.*  VALUES ( ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(usertypeid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(licno)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(licexp)).append("' ");
		sb.append(" , ");
		sb.append(creator);
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(creator);
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String updateRefUser(int refuserid, String licno, String licexp, String genliability, String autoliability, String workcomp, int creator, String ip) {
		if (refuserid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_USERS SET ");
		sb.append(" LIC_NO = '").append(Operator.sqlEscape(licno)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(creator);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");

		sb.append(" , ");
		sb.append(" LIC_EXP_DT = ");
		if (Operator.hasValue(licexp)) {
			Timekeeper d = new Timekeeper();
			d.setDate(licexp);
			sb.append(d.sqlDatetime());
		}
		else { sb.append(" null "); }

		sb.append(" , ");
		sb.append(" GEN_LIABILITY_DT = ");
		if (Operator.hasValue(genliability)) {
			Timekeeper d = new Timekeeper();
			d.setDate(genliability);
			sb.append(d.sqlDatetime());
		}
		else { sb.append(" null "); }

		sb.append(" , ");
		sb.append(" AUTO_LIABILITY_DT = ");
		if (Operator.hasValue(autoliability)) {
			Timekeeper d = new Timekeeper();
			d.setDate(autoliability);
			sb.append(d.sqlDatetime());
		}
		else { sb.append(" null "); }

		sb.append(" , ");
		sb.append(" WORK_COMP_EXP_DT = ");
		if (Operator.hasValue(workcomp)) {
			Timekeeper d = new Timekeeper();
			d.setDate(workcomp);
			sb.append(d.sqlDatetime());
		}
		else { sb.append(" null "); }

		sb.append(" WHERE ID = ").append(refuserid);
		return sb.toString();
	}

	public static String archiveRefUser(int refuserid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_USERS_HISTORY ");
		sb.append(" SELECT * FROM REF_USERS ");
		sb.append(" WHERE ID = ").append(refuserid);
		return sb.toString();
	}

	public static String deleteRefUser(int userid, int usertypeid, int updater, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_USERS SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(updater);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE USERS_ID = ").append(userid).append(" AND LKUP_USERS_TYPE_ID = ").append(usertypeid);
		return sb.toString();
	}

	public static String getUserType(int usertypeid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM LKUP_USERS_TYPE WHERE ID = ").append(usertypeid);
		return sb.toString();
	}

	public static String getUserRoles(String username, int staffid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID AS ROLE ");
		sb.append(" , ");
		sb.append(" ROLE AS TEXT ");
		sb.append(" , ");
		sb.append(" ADMIN ");
		sb.append(" , ");
		sb.append(" EVERYONE ");
		sb.append(" FROM ");
		sb.append(" LKUP_ROLES ");
		sb.append(" WHERE ");
		sb.append(" EVERYONE = 'Y' ");
		sb.append(" AND ");
		sb.append(" ACTIVE = 'Y' ");

		if (staffid > 0) {
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" ID AS ROLE ");
			sb.append(" , ");
			sb.append(" ROLE AS TEXT ");
			sb.append(" , ");
			sb.append(" ADMIN ");
			sb.append(" , ");
			sb.append(" EVERYONE ");
			sb.append(" FROM ");
			sb.append(" LKUP_ROLES ");
			sb.append(" WHERE ");
			sb.append(" STAFF = 'Y' ");
			sb.append(" AND ");
			sb.append(" ACTIVE = 'Y' ");
		}

		sb.append(" UNION ");
		sb.append(" SELECT ");
		sb.append(" R.ID AS ROLE ");
		sb.append(" , ");
		sb.append(" R.ROLE AS TEXT ");
		sb.append(" , ");
		sb.append(" R.ADMIN ");
		sb.append(" , ");
		sb.append(" R.EVERYONE ");
		sb.append(" FROM ");
		sb.append(" LKUP_ROLES AS R ");
		sb.append(" JOIN REF_USERS_ROLES AS RU ON R.ID = RU.LKUP_ROLES_ID AND RU.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND R.EVERYONE = 'N' AND R.STAFF = 'N' ");
		sb.append(" JOIN USERS AS U ON RU.USERS_ID = U.ID AND U.ACTIVE = 'Y' AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		return sb.toString();
	}

	public static String getUsers(String[] ids) {
		if (!Operator.hasValue(ids)) { return ""; }
		String in = Operator.join(ids, ",");
		if (!Operator.hasValue(in)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM USERS WHERE ID IN (").append(in).append(") ");
		return sb.toString();
	}
	
	public static String getOnlineUsers(String username, int id, int lkupuser) {
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" U.ID ");
		sb.append(" , ");
		sb.append(" RU.ID REF_USER_ID ");
		sb.append(" , ");
		sb.append(" U.FIRST_NAME ");
		sb.append(" , ");
		sb.append(" U.MI ");
		sb.append(" , ");
		sb.append(" U.LAST_NAME ");
		sb.append(" , ");
		sb.append(" U.EMAIL ");
		sb.append(" , ");
		sb.append(" U.NOTIFY ");
		sb.append(" , ");
		sb.append(" P.ID AS PEOPLE_ID ");
		sb.append(" , ");
		sb.append(" P.ADDRESS ");
		sb.append(" , ");
		sb.append(" P.CITY ");
		sb.append(" , ");
		sb.append(" P.STATE ");
		sb.append(" , ");
		sb.append(" P.ZIP ");
		sb.append(" , ");
		sb.append(" P.ZIP4 ");
		sb.append(" , ");
		sb.append(" P.PHONE_WORK ");
		sb.append(" , ");
		sb.append(" P.PHONE_CELL ");
		sb.append(" , ");
		sb.append(" P.PHONE_HOME ");
		sb.append(" , ");
		sb.append(" P.FAX ");
		sb.append(" , ");
		sb.append(" RU.LKUP_USERS_TYPE_ID ");
		sb.append(" FROM ");
		sb.append(" USERS AS U ");
		sb.append(" LEFT OUTER JOIN REF_USERS AS RU ON RU.USERS_ID = U.ID AND RU.ACTIVE = 'Y' AND U.ACTIVE = 'Y' ");
		if(lkupuser > 0) {
			sb.append(" and RU.LKUP_USERS_TYPE_ID=").append(lkupuser);
		}
		sb.append(" LEFT OUTER JOIN PEOPLE AS P ON U.ID = P.USERS_ID AND P.ACTIVE = 'Y' ");
		sb.append(" WHERE U.ACTIVE = 'Y' AND LOWER(U.username) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		if(id > 0) {
			sb.append(" and U.id=").append(id);
		}
		
		return sb.toString();
	}

	public static String getProfile(String username) {
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" U.ID ");
		sb.append(" , ");
		sb.append(" U.FIRST_NAME ");
		sb.append(" , ");
		sb.append(" U.MI ");
		sb.append(" , ");
		sb.append(" U.LAST_NAME ");
		sb.append(" , ");
		sb.append(" U.EMAIL ");
		sb.append(" , ");
		sb.append(" P.ID AS PEOPLE_ID ");
		sb.append(" , ");
		sb.append(" P.ADDRESS ");
		sb.append(" , ");
		sb.append(" P.CITY ");
		sb.append(" , ");
		sb.append(" P.STATE ");
		sb.append(" , ");
		sb.append(" P.ZIP ");
		sb.append(" , ");
		sb.append(" P.ZIP4 ");
		sb.append(" , ");
		sb.append(" P.PHONE_WORK ");
		sb.append(" , ");
		sb.append(" P.PHONE_CELL ");
		sb.append(" , ");
		sb.append(" P.PHONE_HOME ");
		sb.append(" , ");
		sb.append(" P.FAX ");
		sb.append(" FROM ");
		sb.append(" USERS AS U ");
		sb.append(" LEFT OUTER JOIN PEOPLE AS P ON U.ID = P.USERS_ID AND P.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" U.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" LOWER(U.username) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" ORDER BY U.CREATED_DATE DESC, U.UPDATED_DATE DESC ");
		return sb.toString();
	}

	public static String getRepresentative(String rep, String password) {
		if (!Operator.hasValue(rep) || !Operator.hasValue(password)) { return ""; }
		String enc = jencrypt.aesencrypt(password, CsApiConfig.getRepSalt());
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM REPRESENTATIVE WHERE ");
		sb.append(" LOWER(REP) = LOWER('").append(Operator.sqlEscape(rep)).append("') ");
		sb.append(" AND ");
		sb.append(" PASSWORD = '").append(Operator.sqlEscape(enc)).append("' ");
		sb.append(" AND ");
		sb.append(" ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getPeopleType() {
		StringBuilder sb = new StringBuilder();
		sb.append(" select DISTINCT ID,REQUIRED_LICENSE as VALUE, DESCRIPTION as TEXT from LKUP_USERS_TYPE WHERE active = 'Y' AND ISONLINE='Y'");
		return sb.toString();
	}

	public static String getStaff(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" S.* ");
		sb.append(" FROM ");
		sb.append(" STAFF AS S ");
		sb.append(" JOIN USERS AS U ON S.USERS_ID = U.ID AND S.ACTIVE = 'Y' AND U.ACTIVE = 'Y' AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		return sb.toString();
	}

	public static String updateHistory(int usersid, int createdby, String createdip) {
		if (usersid < 1) { return ""; }
		String stack = Logger.getStack(3, 15);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO USERS_HISTORY ");
		sb.append(" SELECT ");
		sb.append(" 	U.ID AS USERS_ID, ");
		sb.append(" 	P.ID AS PEOPLE_ID, ");
		sb.append(" 	U.FIRST_NAME, ");
		sb.append(" 	U.MI, ");
		sb.append(" 	U.LAST_NAME, ");
		sb.append(" 	U.USERNAME, ");
		sb.append(" 	U.EMAIL, ");
		sb.append(" 	P.ADDRESS, ");
		sb.append(" 	P.CITY, ");
		sb.append(" 	P.STATE, ");
		sb.append(" 	P.ZIP, ");
		sb.append(" 	P.ZIP4, ");
		sb.append(" 	P.PHONE_WORK, ");
		sb.append(" 	P.PHONE_CELL, ");
		sb.append(" 	P.PHONE_HOME, ");
		sb.append(" 	P.FAX, ");
		sb.append(" 	U.UPDATED_BY AS USERS_UPDATED_BY, ");
		sb.append(" 	U.UPDATED_DATE AS USERS_UPDATED_DATE, ");
		sb.append(" 	P.UPDATED_BY AS PEOPLE_UPDATED_BY, ");
		sb.append(" 	P.UPDATED_DATE AS PEOPLE_UPDATED_DATE, ");
		sb.append(" 	'").append(Operator.sqlEscape(stack)).append("' AS STACK, ");
		sb.append(" 	").append(createdby).append(" AS CREATED_BY, ");
		sb.append(" 	getDate() AS CREATED_DATE, ");
		sb.append(" 	'").append(Operator.sqlEscape(createdip)).append("' AS CREATED_IP ");
		sb.append(" FROM ");
		sb.append(" 	USERS AS U ");
		sb.append(" 	LEFT OUTER JOIN PEOPLE AS P ON P.USERS_ID = U.ID AND P.ACTIVE = 'Y' ");
		sb.append(" 	WHERE ");
		sb.append(" 	U.ID = ").append(usersid);
		sb.append(" 	AND ");
		sb.append(" 	U.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String updateHistory(String username, int createdby, String createdip) {
		if (!Operator.hasValue(username)) { return ""; }
		String stack = Logger.getStack(3, 15);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO USERS_HISTORY ");
		sb.append(" SELECT ");
		sb.append(" 	U.ID AS USERS_ID, ");
		sb.append(" 	P.ID AS PEOPLE_ID, ");
		sb.append(" 	U.FIRST_NAME, ");
		sb.append(" 	U.MI, ");
		sb.append(" 	U.LAST_NAME, ");
		sb.append(" 	U.USERNAME, ");
		sb.append(" 	U.EMAIL, ");
		sb.append(" 	P.ADDRESS, ");
		sb.append(" 	P.CITY, ");
		sb.append(" 	P.STATE, ");
		sb.append(" 	P.ZIP, ");
		sb.append(" 	P.ZIP4, ");
		sb.append(" 	P.PHONE_WORK, ");
		sb.append(" 	P.PHONE_CELL, ");
		sb.append(" 	P.PHONE_HOME, ");
		sb.append(" 	P.FAX, ");
		sb.append(" 	U.UPDATED_BY AS USERS_UPDATED_BY, ");
		sb.append(" 	U.UPDATED_DATE AS USERS_UPDATED_DATE, ");
		sb.append(" 	P.UPDATED_BY AS PEOPLE_UPDATED_BY, ");
		sb.append(" 	P.UPDATED_DATE AS PEOPLE_UPDATED_DATE, ");
		sb.append(" 	'").append(Operator.sqlEscape(stack)).append("' AS STACK, ");
		sb.append(" 	").append(createdby).append(" AS CREATED_BY, ");
		sb.append(" 	getDate() AS CREATED_DATE, ");
		sb.append(" 	'").append(Operator.sqlEscape(createdip)).append("' AS CREATED_IP ");
		sb.append(" FROM ");
		sb.append(" 	USERS AS U ");
		sb.append(" 	LEFT OUTER JOIN PEOPLE AS P ON P.USERS_ID = U.ID AND P.ACTIVE = 'Y' ");
		sb.append(" 	WHERE ");
		sb.append(" 	LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" 	AND ");
		sb.append(" 	U.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String updateAllHistory(int usersid, int createdby, String createdip) {
		if (usersid < 1) { return ""; }
		String stack = Logger.getStack(3, 15);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO USERS_HISTORY ");
		sb.append(" SELECT ");
		sb.append(" 	U.ID AS USERS_ID, ");
		sb.append(" 	P.ID AS PEOPLE_ID, ");
		sb.append(" 	U.FIRST_NAME, ");
		sb.append(" 	U.MI, ");
		sb.append(" 	U.LAST_NAME, ");
		sb.append(" 	U.USERNAME, ");
		sb.append(" 	U.EMAIL, ");
		sb.append(" 	P.ADDRESS, ");
		sb.append(" 	P.CITY, ");
		sb.append(" 	P.STATE, ");
		sb.append(" 	P.ZIP, ");
		sb.append(" 	P.ZIP4, ");
		sb.append(" 	P.PHONE_WORK, ");
		sb.append(" 	P.PHONE_CELL, ");
		sb.append(" 	P.PHONE_HOME, ");
		sb.append(" 	P.FAX, ");
		sb.append(" 	U.UPDATED_BY AS USERS_UPDATED_BY, ");
		sb.append(" 	U.UPDATED_DATE AS USERS_UPDATED_DATE, ");
		sb.append(" 	P.UPDATED_BY AS PEOPLE_UPDATED_BY, ");
		sb.append(" 	P.UPDATED_DATE AS PEOPLE_UPDATED_DATE, ");
		sb.append(" 	'").append(Operator.sqlEscape(stack)).append("' AS STACK, ");
		sb.append(" 	").append(createdby).append(" AS CREATED_BY, ");
		sb.append(" 	getDate() AS CREATED_DATE, ");
		sb.append(" 	'").append(Operator.sqlEscape(createdip)).append("' AS CREATED_IP ");
		sb.append(" FROM ");
		sb.append(" 	USERS AS U ");
		sb.append(" 	LEFT OUTER JOIN PEOPLE AS P ON P.USERS_ID = U.ID AND P.ACTIVE = 'Y' ");
		sb.append(" 	WHERE ");
		sb.append(" 	LOWER(U.USERNAME) = (SELECT LOWER(USERNAME) FROM USERS WHERE ID = ").append(usersid).append(" AND USERNAME IS NOT NULL AND USERNAME <> '') ");
		sb.append(" 	AND ");
		sb.append(" 	U.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String updatePeopleHistory(int peopleid, int createdby, String createdip) {
		if (peopleid < 1) { return ""; }
		String stack = Logger.getStack(3, 15);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO USERS_HISTORY ");
		sb.append(" SELECT ");
		sb.append(" 	U.ID AS USERS_ID, ");
		sb.append(" 	P.ID AS PEOPLE_ID, ");
		sb.append(" 	U.FIRST_NAME, ");
		sb.append(" 	U.MI, ");
		sb.append(" 	U.LAST_NAME, ");
		sb.append(" 	U.USERNAME, ");
		sb.append(" 	U.EMAIL, ");
		sb.append(" 	P.ADDRESS, ");
		sb.append(" 	P.CITY, ");
		sb.append(" 	P.STATE, ");
		sb.append(" 	P.ZIP, ");
		sb.append(" 	P.ZIP4, ");
		sb.append(" 	P.PHONE_WORK, ");
		sb.append(" 	P.PHONE_CELL, ");
		sb.append(" 	P.PHONE_HOME, ");
		sb.append(" 	P.FAX, ");
		sb.append(" 	U.UPDATED_BY AS USERS_UPDATED_BY, ");
		sb.append(" 	U.UPDATED_DATE AS USERS_UPDATED_DATE, ");
		sb.append(" 	P.UPDATED_BY AS PEOPLE_UPDATED_BY, ");
		sb.append(" 	P.UPDATED_DATE AS PEOPLE_UPDATED_DATE, ");
		sb.append(" 	'").append(Operator.sqlEscape(stack)).append("' AS STACK, ");
		sb.append(" 	").append(createdby).append(" AS CREATED_BY, ");
		sb.append(" 	getDate() AS CREATED_DATE, ");
		sb.append(" 	'").append(Operator.sqlEscape(createdip)).append("' AS CREATED_IP ");
		sb.append(" FROM ");
		sb.append(" 	USERS AS U ");
		sb.append(" 	JOIN PEOPLE AS P ON P.USERS_ID = U.ID ");
		sb.append(" 	WHERE ");
		sb.append(" 	P.ID = ").append(peopleid);
		sb.append(" 	AND ");
		sb.append(" 	U.ACTIVE = 'Y' ");
		return sb.toString();
	}





}















