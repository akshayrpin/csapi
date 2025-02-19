package csapi.impl.profile;

import alain.core.security.Token;
import alain.core.utils.Operator;


public class ProfileSQL {
	
	public static String details(String type, int typeid, int id, Token u) {
		String username = u.getUsername();
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	U.USERNAME ");
		sb.append(" 	, ");
		sb.append(" 	U.FIRST_NAME ");
		sb.append(" 	, ");
		sb.append(" 	U.MI ");
		sb.append(" 	, ");
		sb.append(" 	U.LAST_NAME ");
		sb.append(" 	, ");
		sb.append(" 	U.EMAIL ");
		sb.append(" 	, ");
		sb.append(" 	P.ADDRESS ");
		sb.append(" 	, ");
		sb.append(" 	P.CITY ");
		sb.append(" 	, ");
		sb.append(" 	P.STATE ");
		sb.append(" 	, ");
		sb.append(" 	P.ZIP ");
		sb.append(" 	, ");
		sb.append(" 	P.PHONE_WORK ");
		sb.append(" 	, ");
		sb.append(" 	P.PHONE_CELL ");
		sb.append(" 	, ");
		sb.append(" 	P.PHONE_HOME ");
		sb.append(" FROM ");
		sb.append(" 	USERS AS U ");
		sb.append(" 	JOIN PEOPLE AS P ON U.ID = P.USERS_ID AND U.ACTIVE = 'Y' AND P.ACTIVE = 'Y' AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		return sb.toString();
	}

	public static String parkingAddl(int id, Token u) {
		if (id < 1) { return parkingAddl(u); }
		if (!Operator.hasValue(u.getUsername())) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" 	SELECT ");
		sb.append(" 		RAA.ID ");
		sb.append(" 		, ");
		sb.append(" 		L.ID AS LSO_ID ");
		sb.append(" 		, ");
		sb.append(" 		S.ID AS LSO_STREET_ID ");
		sb.append(" 		, ");
		sb.append(" 		CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(L.STR_MOD, '') + ' ' + COALESCE(S.PRE_DIR, '') + ' ' + S.STR_NAME + ' ' + S.STR_TYPE + ' ' + COALESCE(L.UNIT, '') AS ADDRESS ");
		sb.append(" 		, ");
		sb.append(" 		COALESCE(S.PRE_DIR, '') + ' ' + S.STR_NAME + ' ' + S.STR_TYPE AS STREET ");
		sb.append(" 		, ");
		sb.append(" 		L.STR_NO ");
		sb.append(" 		, ");
		sb.append(" 		L.STR_MOD ");
		sb.append(" 		, ");
		sb.append(" 		S.PRE_DIR ");
		sb.append(" 		, ");
		sb.append(" 		S.STR_NAME ");
		sb.append(" 		, ");
		sb.append(" 		S.STR_TYPE ");
		sb.append(" 		, ");
		sb.append(" 		L.UNIT ");
		sb.append(" 		, ");
		sb.append(" 		L.CITY ");
		sb.append(" 		, ");
		sb.append(" 		L.STATE ");
		sb.append(" 		, ");
		sb.append(" 		L.ZIP ");
		sb.append(" 		, ");
		sb.append(" 		RAA.ACCOUNT_NO AS ACCOUNT ");
		sb.append(" 		, ");
		sb.append(" 		CASE WHEN ST.STATUS IS NULL THEN 'ACTIVE' ELSE ST.STATUS END AS STATUS ");
		sb.append(" 		, ");
		sb.append(" 		RAA.COMMENT ");
		sb.append(" 		, ");
		sb.append(" 		'Y' AS EDITABLE ");
		sb.append(" 		, ");
		sb.append(" 		10 AS ORDR ");
		sb.append(" 	FROM ");
		sb.append(" 		USERS AS U ");
		sb.append(" 		JOIN REF_ACCOUNT_APPLICATION AS RAA ON U.ID = RAA.USERS_ID AND U.ACTIVE = 'Y' AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(u.getUsername())).append("') AND RAA.ID = ").append(id);
		sb.append(" 		JOIN LKUP_ACCOUNT_APPLICATION_STATUS AS ST ON RAA.LKUP_ACCOUNT_APPLICATION_STATUS_ID = ST.ID ");
		sb.append(" 		JOIN LSO AS L ON RAA.LSO_ID = L.ID AND L.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LSO_STREET AS S ON L.LSO_STREET_ID = S.ID AND S.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String parkingAddl(Token u) {
		if (!Operator.hasValue(u.getUsername())) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q1 AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		PP.ID ");
		sb.append(" 		, ");
		sb.append(" 		L.ID AS LSO_ID ");
		sb.append(" 		, ");
		sb.append(" 		CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(L.STR_MOD, '') + ' ' + COALESCE(S.PRE_DIR, '') + ' ' + S.STR_NAME + ' ' + S.STR_TYPE + ' ' + COALESCE(L.UNIT, '') AS ADDRESS ");
		sb.append(" 		, ");
		sb.append(" 		L.STR_NO ");
		sb.append(" 		, ");
		sb.append(" 		L.STR_MOD ");
		sb.append(" 		, ");
		sb.append(" 		S.PRE_DIR ");
		sb.append(" 		, ");
		sb.append(" 		S.STR_NAME ");
		sb.append(" 		, ");
		sb.append(" 		S.STR_TYPE ");
		sb.append(" 		, ");
		sb.append(" 		L.UNIT ");
		sb.append(" 		, ");
		sb.append(" 		L.CITY ");
		sb.append(" 		, ");
		sb.append(" 		L.STATE ");
		sb.append(" 		, ");
		sb.append(" 		L.ZIP ");
		sb.append(" 		, ");
		sb.append(" 		PP.ID AS ACCOUNT ");
		sb.append(" 		, ");
		sb.append(" 		CASE WHEN ST.STATUS IS NULL THEN 'ACTIVE' ELSE ST.STATUS END AS STATUS ");
		sb.append(" 		, ");
		sb.append(" 		RAA.COMMENT ");
		sb.append(" 		, ");
		sb.append(" 		'N' AS EDITABLE ");
		sb.append(" 		, ");
		sb.append(" 		0 AS ORDR ");
		sb.append(" 	FROM ");
		sb.append(" 		USERS AS U ");
		sb.append(" 		JOIN REF_USERS AS R ON U.ID = R.USERS_ID AND U.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(u.getUsername())).append("') ");
		sb.append(" 		JOIN REF_PROJECT_USERS AS PU ON R.ID = PU.REF_USERS_ID AND PU.ACTIVE = 'Y' ");
		sb.append(" 		JOIN PROJECT AS P ON PU.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_PROJECT_PARKING AS PP ON P.ID = PP.PROJECT_ID AND PP.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_LSO_PROJECT AS LP ON P.ID = LP.PROJECT_ID AND LP.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LSO AS L ON LP.LSO_ID = L.ID AND L.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LSO_STREET AS S ON L.LSO_STREET_ID = S.ID AND S.ACTIVE = 'Y' ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_ACCOUNT_APPLICATION AS RAA ");
		sb.append(" 			JOIN LKUP_ACCOUNT_APPLICATION_STATUS AS ST ON RAA.LKUP_ACCOUNT_APPLICATION_STATUS_ID = ST.ID ");
		sb.append(" 		) ON U.ID = RAA.USERS_ID AND PP.ID = RAA.ACCOUNT_NO ");
		sb.append(" ) ");
		sb.append(" , Q2 AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		RAA.ID ");
		sb.append(" 		, ");
		sb.append(" 		L.ID AS LSO_ID ");
		sb.append(" 		, ");
		sb.append(" 		CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(L.STR_MOD, '') + ' ' + COALESCE(S.PRE_DIR, '') + ' ' + S.STR_NAME + ' ' + S.STR_TYPE + ' ' + COALESCE(L.UNIT, '') AS ADDRESS ");
		sb.append(" 		, ");
		sb.append(" 		L.STR_NO ");
		sb.append(" 		, ");
		sb.append(" 		L.STR_MOD ");
		sb.append(" 		, ");
		sb.append(" 		S.PRE_DIR ");
		sb.append(" 		, ");
		sb.append(" 		S.STR_NAME ");
		sb.append(" 		, ");
		sb.append(" 		S.STR_TYPE ");
		sb.append(" 		, ");
		sb.append(" 		L.UNIT ");
		sb.append(" 		, ");
		sb.append(" 		L.CITY ");
		sb.append(" 		, ");
		sb.append(" 		L.STATE ");
		sb.append(" 		, ");
		sb.append(" 		L.ZIP ");
		sb.append(" 		, ");
		sb.append(" 		RAA.ACCOUNT_NO AS ACCOUNT ");
		sb.append(" 		, ");
		sb.append(" 		CASE WHEN ST.STATUS IS NULL THEN 'ACTIVE' ELSE ST.STATUS END AS STATUS ");
		sb.append(" 		, ");
		sb.append(" 		RAA.COMMENT ");
		sb.append(" 		, ");
		sb.append(" 		'Y' AS EDITABLE ");
		sb.append(" 		, ");
		sb.append(" 		10 AS ORDR ");
		sb.append(" 	FROM ");
		sb.append(" 		USERS AS U ");
		sb.append(" 		JOIN REF_ACCOUNT_APPLICATION AS RAA ON U.ID = RAA.USERS_ID AND U.ACTIVE = 'Y' AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(u.getUsername())).append("') ");
		sb.append(" 		JOIN LKUP_ACCOUNT_APPLICATION_STATUS AS ST ON RAA.LKUP_ACCOUNT_APPLICATION_STATUS_ID = ST.ID ");
		sb.append(" 		JOIN LSO AS L ON RAA.LSO_ID = L.ID AND L.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LSO_STREET AS S ON L.LSO_STREET_ID = S.ID AND S.ACTIVE = 'Y' ");
		sb.append(" ) ");
		sb.append(" , Q AS ( ");
		sb.append(" 	SELECT * FROM Q1 ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		Q2.* ");
		sb.append(" 	FROM ");
		sb.append(" 		Q2 ");
		sb.append(" 		LEFT OUTER JOIN Q1 ON Q1.ID = Q2.ACCOUNT OR Q1.LSO_ID = Q2.LSO_ID ");
		sb.append(" 	WHERE ");
		sb.append(" 		Q1.ID IS NULL ");
		sb.append(" ) ");
		sb.append(" SELECT * FROM Q ORDER BY ORDR ");
		return sb.toString();
	}

	public static String updateUser(String username, String fname, String mname, String lname, String email) {
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE USERS SET ");
		sb.append(" FIRST_NAME = '").append(fname).append("' ");
		sb.append(" , ");
		sb.append(" MI = '").append(mname).append("' ");
		sb.append(" , ");
		sb.append(" LAST_NAME = '").append(lname).append("' ");
		sb.append(" , ");
		sb.append(" EMAIL = '").append(email).append("' ");
		sb.append(" WHERE ");
		sb.append(" LOWER(USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" AND ");
		sb.append(" ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String updatePeople(String username, String address, String city, String state, String zip, String wphone, String hphone, String cphone, String fax) {
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE PEOPLE SET ");
		sb.append(" ADDRESS = '").append(Operator.sqlEscape(address)).append("' ");
		sb.append(" , ");
		sb.append(" CITY = '").append(Operator.sqlEscape(city)).append("' ");
		sb.append(" , ");
		sb.append(" STATE = '").append(Operator.sqlEscape(state)).append("' ");
		sb.append(" , ");
		sb.append(" ZIP = '").append(Operator.sqlEscape(zip)).append("' ");
		sb.append(" , ");
		sb.append(" PHONE_WORK = '").append(Operator.sqlEscape(wphone)).append("' ");
		sb.append(" , ");
		sb.append(" PHONE_HOME = '").append(Operator.sqlEscape(hphone)).append("' ");
		sb.append(" , ");
		sb.append(" PHONE_CELL = '").append(Operator.sqlEscape(cphone)).append("' ");
		sb.append(" , ");
		sb.append(" FAX = '").append(Operator.sqlEscape(fax)).append("' ");
		sb.append(" WHERE ");
		sb.append(" USERS_ID IN (SELECT ID FROM USERS WHERE LOWER(USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') AND ACTIVE = 'Y') ");
		sb.append(" AND ");
		sb.append(" ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String updateParkingaddl(int id, int lsoid, int account) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_ACCOUNT_APPLICATION SET ");
		sb.append(" LSO_ID = ").append(lsoid);
		sb.append(" , ");
		sb.append(" ACCOUNT_NO = ").append(account);
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		return sb.toString();
	}
















}















