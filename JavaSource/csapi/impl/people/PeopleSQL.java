package csapi.impl.people;

import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.users.UsersSQL;
import csapi.utils.CsReflect;
import csshared.vo.finance.StatementVO;

public class PeopleSQL {


	public static String peopleType() {
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
		sb.append(" REQUIRED_LICENSE ");
		sb.append(" , ");
		sb.append(" VALIDATE_LICENSE_URL ");
		sb.append(" FROM ");
		sb.append(" LKUP_USERS_TYPE ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
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
		sb.append(" TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		sb.append(" REQUIRED_LICENSE ");
		sb.append(" , ");
		sb.append(" VALIDATE_LICENSE_URL ");
		sb.append(" FROM ");
		sb.append(" LKUP_USERS_TYPE ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY TYPE ");
		return sb.toString();
	}

	public static String search(String query) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" FROM ");
		sb.append(" WHERE ");
		return sb.toString();
	}

	public static String getPeople(String type, int typeid) {
		return getPeople(type, typeid, -1);
	}

	public static String getPeople(String type, int typeid, int refusersid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" REF.PRIMARY_CONTACT ");
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
		sb.append(" REF_").append(tableref).append("_USERS AS REF ");
		sb.append(" JOIN REF_USERS AS R ON REF.REF_USERS_ID = R.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_USERS_TYPE AS T ON R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0");
		sb.append(" LEFT OUTER JOIN USERS_GROUP AS G ON R.USERS_GROUP_ID = G.ID ");
		sb.append(" WHERE ");
		sb.append(" REF.").append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(" REF.ACTIVE = 'Y' ");
		if (refusersid > 0) {
			sb.append(" AND ");
			sb.append(" REF.REF_USERS_ID = ").append(refusersid);
		}
		return sb.toString();
	}

	public static String deletePeople(String type, int typeid, int id, int updater, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" REF_").append(tableref).append("_USERS ");
		sb.append(" SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(updater);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(" REF_USERS_ID = ").append(id);
		return sb.toString();
	}

	public static String deletePeople(String type, int typeid, int updater, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" REF_").append(tableref).append("_USERS ");
		sb.append(" SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(updater);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		return sb.toString();
	}

	public static String addPeople(String type, int typeid, int refuserid, int primary, int createdby, String ip) {
		boolean pri = false;
		if (refuserid == primary) { pri = true; }
		return addPeople(type, typeid, refuserid, pri, createdby, ip);
	}

	public static String addPeople(String type, int typeid, int refuserid, boolean primary, int createdby, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String pri = "N";
		if (primary) { pri = "Y"; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ");
		sb.append(" REF_").append(tableref).append("_USERS ");
		sb.append(" (").append(idref).append(", REF_USERS_ID, PRIMARY_CONTACT, CREATED_BY, UPDATED_BY, CREATED_IP, UPDATED_IP, ACTIVE) VALUES ( ");
		sb.append(typeid);
		sb.append(" , ");
		sb.append(refuserid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(pri)).append("' ");
		sb.append(" , ");
		sb.append(createdby);
		sb.append(" , ");
		sb.append(createdby);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" 'Y' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String findPerson(String type, int typeid, int refuserid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" REF.ID ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_USERS AS REF ");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(" REF_USERS_ID = ").append(refuserid);
		sb.append(" AND ");
		sb.append(" ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getActiveUsername(int refusersid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		U.USERNAME ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_USERS AS R ");
		sb.append(" 		JOIN USERS AS U ON R.USERS_ID = U.ID AND R.ID = ").append(refusersid).append(" AND U.USERNAME IS NOT NULL AND U.USERNAME <> '' ");
		sb.append(" ) ");
		sb.append(" , A AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		U.* ");
		sb.append(" 		, 0 AS ORDR ");
		sb.append(" 	FROM ");
		sb.append(" 		USERS AS U ");
		sb.append(" 		JOIN Q ON LOWER(U.USERNAME) = LOWER(Q.USERNAME) AND U.USERNAME <> '' AND U.USERNAME IS NOT NULL AND U.ACTIVE = 'Y' ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		U.* ");
		sb.append(" 		, 10 AS ORDR ");
		sb.append(" 	FROM ");
		sb.append(" 		USERS AS U ");
		sb.append(" 		JOIN REF_USERS AS R ON U.ID = USERS_ID ");
		sb.append(" ) ");
		sb.append(" SELECT TOP 1 * FROM A ");
		sb.append(" ORDER BY ORDR, UPDATED_DATE DESC ");
		return sb.toString();
	}

	public static String findApplicant(int usersid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.ID AS REF_USERS_ID, ");
		sb.append(" R.USERS_ID, ");
		sb.append(" T.ID AS LKUP_USERS_TYPE_ID, ");
		sb.append(" T.TYPE, ");
		sb.append(" R.CREATED_DATE ");
		sb.append(" FROM ");
		sb.append(" REF_USERS AS R ");
		sb.append(" JOIN LKUP_USERS_TYPE AS T ON R.LKUP_USERS_TYPE_ID = T.ID AND R.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" T.APPLICANT = 'Y' ");
		sb.append(" AND ");
		sb.append(" USERS_ID = ").append(usersid);
		sb.append(" ORDER BY ");
		sb.append(" R.CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String removePrimary(String type, int typeid, int updater, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" REF_").append(tableref).append("_USERS ");
		sb.append(" SET ");
		sb.append(" PRIMARY_CONTACT = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(updater);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		return sb.toString();
	}

//	public static String removeRefPrimary(int refid, String type, int updater, String ip) {
//		String tableref = CsReflect.getTableRef(type);
//		String idref = CsReflect.getFieldIdRef(type);
//		StringBuilder sb = new StringBuilder();
//		sb.append(" UPDATE ");
//		sb.append(" REF_").append(tableref).append("_USERS ");
//		sb.append(" SET ");
//		sb.append(" PRIMARY_CONTACT = 'N' ");
//		sb.append(" , ");
//		sb.append(" UPDATED_BY = ").append(updater);
//		sb.append(" , ");
//		sb.append(" UPDATED_DATE = getDate() ");
//		sb.append(" , ");
//		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
//		sb.append(" WHERE ");
//		sb.append(idref).append(" IN ( SELECT ").append(idref).append(" FROM ").append(tableref).append(" WHERE ID = ").append(refid).append(" ) ");
//		return sb.toString();
//	}

	public static String setPrimary(int refusersid, String type, int typeid, int updater, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" REF_").append(tableref).append("_USERS ");
		sb.append(" SET ");
		sb.append(" PRIMARY_CONTACT = 'Y' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(updater);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(" REF_USERS_ID = ").append(refusersid);
		sb.append(" AND ");
		sb.append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(" ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String removePrimary(int refusersid, String type, int typeid, int updater, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" REF_").append(tableref).append("_USERS ");
		sb.append(" SET ");
		sb.append(" PRIMARY_CONTACT = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(updater);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
//		sb.append(" REF_USERS_ID = ").append(refusersid);
//		sb.append(" AND ");
		sb.append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(" ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getActivityPeople(StatementVO[] st) {
		int l = st.length;
		Logger.highlight(l);
		StringBuilder sb = new StringBuilder();
		if (l > 0) {
			for (int i=0; i<l; i++) {
				if (i > 0) { sb.append(" , "); }
				sb.append(st[i].getActivityid());
			}
			String in = sb.toString();

			sb = new StringBuilder();
			sb.append(" WITH Q AS ( ");
			sb.append("   SELECT ");
			sb.append("     AU.ID AS ID, ");
			sb.append("     AU.ID AS VALUE, ");
			sb.append("     AG.GROUP_NAME AS USERGROUP, ");
			sb.append("     AU.ID AS USERS_ID, ");

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
			sb.append("     )) AS TEXT ");

			sb.append("   FROM ");
			sb.append("   ACTIVITY AS A ");
			sb.append("   JOIN REF_ACT_USERS AS AR ON AR.ACTIVITY_ID = A.ID ");
			sb.append("   JOIN REF_USERS AS ARU ON AR.REF_USERS_ID = ARU.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS AU ON ARU.USERS_ID = AU.ID AND ARU.USERS_ID > 0 AND AU.ACTIVE = 'Y' ");
			sb.append("   LEFT OUTER JOIN USERS_GROUP AS AG ON ARU.USERS_GROUP_ID = AG.ID AND ARU.USERS_ID < 1 AND AG.ACTIVE = 'Y' ");
			sb.append("   WHERE ");
			sb.append("     A.ID IN ( ").append(in).append("     ) ");

			sb.append("   UNION ");

			sb.append("   SELECT ");
			sb.append("     PU.ID AS ID, ");
			sb.append("     PU.ID AS VALUE, ");
			sb.append("     PG.GROUP_NAME AS USERGROUP, ");
			sb.append("     PU.ID AS USERS_ID, ");

			sb.append("     LTRIM(RTRIM( ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.FIRST_NAME IS NOT NULL AND PU.FIRST_NAME <> '' THEN PU.FIRST_NAME ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.MI IS NOT NULL AND PU.MI <> '' THEN ' ' + PU.MI ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.LAST_NAME IS NOT NULL AND PU.LAST_NAME <> '' THEN ' ' + PU.LAST_NAME ");
			sb.append("         ELSE '' ");
			sb.append("       END + ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.EMAIL IS NOT NULL AND PU.EMAIL <> '' THEN ' (' + PU.EMAIL + ') ' ");
			sb.append("         ELSE '' ");
			sb.append("       END ");
			sb.append("     )) AS TEXT ");
			sb.append("   FROM ");
			sb.append("   ACTIVITY AS A ");
			sb.append("   JOIN REF_PROJECT_USERS AS PR ON A.PROJECT_ID = PR.PROJECT_ID ");
			sb.append("   JOIN REF_USERS AS PRU ON PR.REF_USERS_ID = PRU.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS PU ON PRU.USERS_ID = PU.ID AND PRU.USERS_ID > 0 AND PU.ACTIVE = 'Y' ");
			sb.append("   LEFT OUTER JOIN USERS_GROUP AS PG ON PRU.USERS_GROUP_ID = PG.ID AND PRU.USERS_ID < 1 AND PG.ACTIVE = 'Y' ");
			sb.append("   WHERE ");
			sb.append("     A.ID IN ( ").append(in).append("     ) ");

			sb.append(" ) ");
			sb.append(" SELECT ");
			sb.append(" Q.ID ");
			sb.append(" , ");
			sb.append(" Q.VALUE ");
			sb.append(" , ");
			sb.append(" CASE ");
			sb.append("   WHEN Q.USERS_ID IS NULL THEN Q.USERGROUP ");
			sb.append("   ELSE Q.TEXT END AS TEXT ");
			sb.append(" FROM Q ");

		}
		return sb.toString();
	}
	
	public static String getActivityPeople(int actin) {
		
		StringBuilder sb = new StringBuilder();
		
			sb = new StringBuilder();
			sb.append(" WITH Q AS ( ");
			sb.append("   SELECT ");
			sb.append("     AU.ID AS ID, ");
			sb.append("     AU.ID AS VALUE, ");
			sb.append("     AG.GROUP_NAME AS USERGROUP, ");
			sb.append("     AU.ID AS USERS_ID, ");

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
			sb.append("     )) AS TEXT ");

			sb.append("   FROM ");
			sb.append("   ACTIVITY AS A ");
			sb.append("   JOIN REF_ACT_USERS AS AR ON AR.ACTIVITY_ID = A.ID ");
			sb.append("   JOIN REF_USERS AS ARU ON AR.REF_USERS_ID = ARU.ID ");
			sb.append("   JOIN USERS AS AU ON ARU.USERS_ID = AU.ID AND ARU.USERS_ID > 0 AND AU.ACTIVE = 'Y' ");
			sb.append("   LEFT OUTER JOIN USERS_GROUP AS AG ON ARU.USERS_GROUP_ID = AG.ID AND ARU.USERS_ID < 1 AND AG.ACTIVE = 'Y' ");
			sb.append("   WHERE ");
			sb.append("     A.ID IN ( ").append(actin).append("     ) ");

			sb.append("   UNION ");

			sb.append("   SELECT ");
			sb.append("     PU.ID AS ID, ");
			sb.append("     PU.ID AS VALUE, ");
			sb.append("     PG.GROUP_NAME AS USERGROUP, ");
			sb.append("     PU.ID AS USERS_ID, ");

			sb.append("     LTRIM(RTRIM( ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.FIRST_NAME IS NOT NULL AND PU.FIRST_NAME <> '' THEN PU.FIRST_NAME ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.MI IS NOT NULL AND PU.MI <> '' THEN ' ' + PU.MI ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.LAST_NAME IS NOT NULL AND PU.LAST_NAME <> '' THEN ' ' + PU.LAST_NAME ");
			sb.append("         ELSE '' ");
			sb.append("       END + ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.EMAIL IS NOT NULL AND PU.EMAIL <> '' THEN ' (' + PU.EMAIL + ') ' ");
			sb.append("         ELSE '' ");
			sb.append("       END ");
			sb.append("     )) AS TEXT ");
			sb.append("   FROM ");
			sb.append("   ACTIVITY AS A ");
			sb.append("   JOIN REF_PROJECT_USERS AS PR ON A.PROJECT_ID = PR.PROJECT_ID ");
			sb.append("   JOIN REF_USERS AS PRU ON PR.REF_USERS_ID = PRU.ID ");
			sb.append("   JOIN USERS AS PU ON PRU.USERS_ID = PU.ID AND PRU.USERS_ID > 0 AND PU.ACTIVE = 'Y' ");
			sb.append("   LEFT OUTER JOIN USERS_GROUP AS PG ON PRU.USERS_GROUP_ID = PG.ID AND PRU.USERS_ID < 1 AND PG.ACTIVE = 'Y' ");
			sb.append("   WHERE ");
			sb.append("     A.ID IN ( ").append(actin).append("     ) ");

			sb.append(" ) ");
			sb.append(" SELECT ");
			sb.append(" Q.ID ");
			sb.append(" , ");
			sb.append(" Q.VALUE ");
			sb.append(" , ");
			sb.append(" CASE ");
			sb.append("   WHEN Q.USERS_ID IS NULL THEN Q.USERGROUP ");
			sb.append("   ELSE Q.TEXT END AS TEXT ");
			sb.append(" FROM Q ");

		
		return sb.toString();
	}
	
	
	public static String getUserPeople(int in) {
		
		StringBuilder sb = new StringBuilder();
		
			sb = new StringBuilder();
			
			sb.append("   SELECT ");
			sb.append("     AU.ID AS ID, ");
			sb.append("     AU.ID AS VALUE, ");
		

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
			sb.append("     )) AS TEXT ");

			sb.append("   FROM ");
			sb.append("   USERS AS AU ");
			//sb.append("   LEFT OUTER JOIN USERS_GROUP AS AG ON ARU.USERS_GROUP_ID = AG.ID AND ARU.USERS_ID < 1 AND AG.ACTIVE = 'Y' ");
			sb.append("   WHERE ");
			sb.append("     AU.ID IN ( ").append(in).append(") ");

			
		

		
		return sb.toString();
	}

	public static String id(String type, int typeid, int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append("   SELECT ");
		sb.append("     R.ID AS ID, ");
		sb.append("     G.GROUP_NAME as USERGROUP, ");
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
		sb.append("     T.DESCRIPTION as TYPE, ");
		sb.append("     U.EMAIL, ");
		sb.append("     U.USERNAME, ");
		sb.append("     P.PHONE_WORK, ");
		sb.append("     P.ADDRESS,");
		sb.append("     R.LIC_NO ");
		sb.append("     , ");
		sb.append("     R.LIC_EXP_DT ");
		sb.append("     , ");
		sb.append("    CASE WHEN R.LIC_EXP_DT < CURRENT_TIMESTAMP THEN 'Y' ELSE 'N' END AS LIC_EXPIRED ");
		sb.append("     , ");
		sb.append("     UPD.USERNAME AS UPDATED ");

		sb.append("   FROM ");
		sb.append("   REF_USERS AS R ");
		sb.append("   JOIN LKUP_USERS_TYPE T on R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append("   LEFT OUTER JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0 ");
		sb.append("   LEFT OUTER JOIN USERS_GROUP AS G ON R.USERS_GROUP_ID = G.ID AND R.USERS_ID < 1 ");
		sb.append("   LEFT OUTER JOIN PEOPLE P ON U.ID = P.USERS_ID AND P.ACTIVE = 'Y' ");
		sb.append("   LEFT OUTER JOIN USERS AS UPD ON R.UPDATED_BY = UPD.ID ");
		sb.append("   WHERE ");
		sb.append("     R.ID = ").append(id);
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" Q.ID ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN Q.USERS_ID IS NULL THEN Q.USERGROUP ");
		sb.append("   ELSE Q.NAME END AS NAME, ");
		sb.append(" Q.TYPE, ");
		sb.append(" Q.EMAIL, ");
		sb.append(" Q.USERNAME, ");
		sb.append(" Q.PHONE_WORK, ");
		sb.append(" Q.ADDRESS,");
		sb.append(" Q.LIC_NO ");
		sb.append(" , ");
		sb.append(" Q.LIC_EXP_DT ");
		sb.append(" , ");
		sb.append(" Q.LIC_EXP_DT AS EXPIRES ");
		
		sb.append(",");
		sb.append(" Q.LIC_EXPIRED ");
		sb.append(" FROM Q ");
		return sb.toString();
	}

	public static String ext(String type, int typeid, int id) {
		return summary(type, typeid, id);
	}

	public static String summary(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		if (Operator.equalsIgnoreCase(type, "activity")) {
			sb.append(getActivitySummary(typeid));
		}
		else {
			sb.append(getSummary(type, typeid));
		}
		sb.append(" ) ");
		sb.append(" , C AS ( SELECT COUNT(DISTINCT ID) AS CONTENT_COUNT FROM CONTENT WHERE ACTIVE = 'Y' AND LOWER(TYPE) = 'people') ");
		sb.append(" SELECT ");
		sb.append(" Q.ID ");
		sb.append(" , ");
		sb.append(" Q.EMAIL AS REF ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN Q.USERS_ID IS NULL THEN Q.USERGROUP ");
		sb.append("   ELSE Q.NAME END AS NAME, ");
		sb.append(" Q.TYPE, ");
		sb.append(" Q.ISPUBLIC, ");
		sb.append(" Q.EMAIL, ");
		sb.append(" Q.USERNAME, ");
		sb.append(" Q.PHONE_WORK, ");
		sb.append(" Q.ADDRESS,");
		sb.append("   CASE ");
		sb.append("     WHEN C.CONTENT_COUNT IS NULL THEN '' ");
		sb.append("     WHEN C.CONTENT_COUNT > 0 THEN 'people' ");
		sb.append("   ELSE '' END AS CONTENT_TYPE, ");
		sb.append(" CASE WHEN Q.EMAIL = '' THEN 'X' WHEN Q.EMAIL IS NULL THEN 'X' WHEN Q.PRIMARY_CONTACT = 'Y' THEN 'Y' WHEN Q.DEFAULT_CONTACT = 'Y' THEN 'D' WHEN Q.ONLEVEL = 'N' THEN 'X' ELSE 'N' END AS PRIMARY_CONTACT,");
		sb.append(" Q.DEFAULT_CONTACT,");
		sb.append(" Q.REF,");
		sb.append(" Q.REF_ID,");
		sb.append(" Q.LIC_NO ");
		sb.append(" , ");
		sb.append(" Q.LIC_EXP_DT ");
		//sb.append(" , ");
		//sb.append(" CASE WHEN Q.HOLDS_STATUS = 'A' THEN 'Y' WHEN Q.LIC_EXP_DT IS NOT NULL THEN 'Y' ELSE NULL END AS EXPIRES ");
		
		sb.append(",");
		sb.append(" Q.LIC_EXPIRED ");
		
	//	sb.append(",");
	//	sb.append("Q.HOLDS_STATUS");
		
//		sb.append(",");
//		sb.append("'").append(Config.rooturl()).append("/").append(Config.contextroot()).append("/jsp/admin/user/user/user.jsp?_id=30&_typeid=30&_ent=permit&type=admin&ID=").append("'+CAST(Q.USERS_ID as VARCHAR(50)) as ADMINLINK ");
		sb.append(" FROM Q ");
		sb.append(" LEFT OUTER JOIN C ON 1 = 1");
		if (id > 0) {
			sb.append(" WHERE ");
			sb.append(" Q.ID = ").append(id);
		}
		sb.append(" ORDER BY NAME ");
		return sb.toString();
	}

	public static String getSummary(String type, int typeid) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		
		StringBuilder sb = new StringBuilder();
		
		sb = new StringBuilder();
		sb.append("   SELECT  DISTINCT  ");
		sb.append("     R.ID AS ID, ");
		sb.append("     G.GROUP_NAME as USERGROUP, ");
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
		sb.append("     T.DESCRIPTION as TYPE, ");
		sb.append("     T.ISPUBLIC, ");
		sb.append("     T.REQUIRED_LICENSE, ");
		sb.append("     U.EMAIL, ");
		sb.append("     U.USERNAME, ");
		sb.append("     P.PHONE_WORK, ");
		sb.append("     P.ADDRESS,");
		sb.append("     '").append(Operator.sqlEscape(type)).append("' AS REF,");
		sb.append("     'Y' AS ONLEVEL,");
		sb.append("     REF.").append(idref).append(" AS REF_ID, ");
		sb.append("     REF.PRIMARY_CONTACT,");
		sb.append("     REF.PRIMARY_CONTACT AS DEFAULT_CONTACT,");
		sb.append("     R.LIC_NO ");
		sb.append("     , ");
		sb.append("     CASE WHEN T.REQUIRED_LICENSE = 'N' THEN NULL ELSE R.LIC_EXP_DT END AS LIC_EXP_DT ");
		sb.append("     , ");
		sb.append("    CASE WHEN T.REQUIRED_LICENSE = 'N' THEN 'N' WHEN R.LIC_EXP_DT < CURRENT_TIMESTAMP THEN 'Y' ELSE 'N' END AS LIC_EXPIRED ");

		sb.append("   FROM ");
		sb.append("   REF_").append(tableref).append("_USERS AS REF ");
		sb.append("   JOIN REF_USERS AS R ON REF.REF_USERS_ID = R.ID AND REF.ACTIVE = 'Y' ");
		sb.append("   JOIN LKUP_USERS_TYPE T on R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append("   LEFT OUTER JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0 ");
		sb.append("   LEFT OUTER JOIN USERS_GROUP AS G ON R.USERS_GROUP_ID = G.ID AND R.USERS_ID < 1 AND G.ACTIVE = 'Y' ");
		sb.append("   LEFT OUTER JOIN PEOPLE P ON U.ID = P.USERS_ID AND P.ACTIVE = 'Y' ");
		sb.append("   WHERE ");
		sb.append("     REF.").append(idref).append(" = ").append(typeid).append(" ");

		return sb.toString();
	}

	public static String getActivitySummary(int actid) {
		StringBuilder sb = new StringBuilder();

		sb.append("   SELECT ");
		sb.append("     R.ID AS ID, ");
		sb.append("     G.GROUP_NAME as USERGROUP, ");
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
		sb.append("     T.ISPUBLIC, ");
		sb.append("     T.REQUIRED_LICENSE, ");
		sb.append("     U.USERNAME, ");
		sb.append("     U.EMAIL, ");
		sb.append("     P.PHONE_WORK, ");
		sb.append("     P.ADDRESS,");
		sb.append("     'activity' AS REF,");
		sb.append("     'Y' AS ONLEVEL,");
		sb.append("     REF.ACTIVITY_ID AS REF_ID, ");
		sb.append("     REF.PRIMARY_CONTACT, ");
		sb.append("     'N' AS DEFAULT_CONTACT, ");
		sb.append("     R.LIC_NO ");
		sb.append("     , ");
		sb.append("     CASE WHEN T.REQUIRED_LICENSE = 'N' THEN NULL ELSE R.LIC_EXP_DT END AS LIC_EXP_DT ");
		sb.append("     , ");
		sb.append("     CASE WHEN T.REQUIRED_LICENSE = 'N' THEN 'N' WHEN R.LIC_EXP_DT < CURRENT_TIMESTAMP THEN 'Y' ELSE 'N' END AS LIC_EXPIRED,HS.STATUS AS HOLDS_STATUS ");
		sb.append(" FROM ");
		sb.append(" REF_ACT_USERS AS REF ");
		sb.append(" JOIN REF_USERS AS R ON REF.REF_USERS_ID = R.ID AND REF.ACTIVE = 'Y' AND REF.ACTIVITY_ID = ").append(actid);
		sb.append(" JOIN LKUP_USERS_TYPE T on R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0 ");
		sb.append(" LEFT OUTER JOIN USERS_GROUP AS G ON R.USERS_GROUP_ID = G.ID AND R.USERS_ID < 1 ");
		sb.append(" LEFT OUTER JOIN PEOPLE P ON U.ID = P.USERS_ID AND P.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REF_USERS_HOLDS H ON U.ID = H.USERS_ID AND H.ACTIVE = 'Y' AND  H.NEW_ID < 0  ");
		sb.append(" LEFT OUTER JOIN LKUP_HOLDS_STATUS HS ON H.LKUP_HOLDS_STATUS_ID = HS.ID AND HS.ACTIVE = 'Y' AND HS.RELEASED='N' ");

		sb.append(" UNION ");

		sb.append(" SELECT ");
		sb.append("     R.ID AS ID, ");
		sb.append("     G.GROUP_NAME as USERGROUP, ");
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
		sb.append("     T.DESCRIPTION as TYPE, ");
		sb.append("     T.ISPUBLIC, ");
		sb.append("     T.REQUIRED_LICENSE, ");
		sb.append("     U.USERNAME, ");
		sb.append("     U.EMAIL, ");
		sb.append("     P.PHONE_WORK, ");
		sb.append("     P.ADDRESS,");
		sb.append("     'project' AS REF,");
		sb.append("     'N' AS ONLEVEL,");
		sb.append("     A.PROJECT_ID AS REF_ID, ");
		sb.append("     CASE WHEN PRI.ID IS NOT NULL THEN 'Y' ELSE 'N' END AS PRIMARY_CONTACT, ");
		sb.append("     REF.PRIMARY_CONTACT AS DEFAULT_CONTACT, ");
		sb.append("     R.LIC_NO ");
		sb.append("     , ");
		sb.append("     CASE WHEN T.REQUIRED_LICENSE = 'N' THEN NULL ELSE R.LIC_EXP_DT END AS LIC_EXP_DT ");
		sb.append("     , ");
		sb.append("    CASE WHEN T.REQUIRED_LICENSE = 'N' THEN 'N' WHEN R.LIC_EXP_DT < CURRENT_TIMESTAMP THEN 'Y' ELSE 'N' END AS LIC_EXPIRED ,'' AS HOLDS_STATUS");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN REF_PROJECT_USERS AS REF ON A.PROJECT_ID = REF.PROJECT_ID AND A.ID =  ").append(actid);
		sb.append(" JOIN REF_USERS AS R ON REF.REF_USERS_ID = R.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_USERS_TYPE T on R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0 ");
		sb.append(" LEFT OUTER JOIN USERS_GROUP AS G ON R.USERS_GROUP_ID = G.ID AND R.USERS_ID < 1 ");
		sb.append(" LEFT OUTER JOIN PEOPLE P ON U.ID = P.USERS_ID AND P.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REF_ACT_USERS AS PRI ON PRI.ACTIVITY_ID = A.ID AND PRI.ACTIVE = 'Y' AND PRI.REF_USERS_ID = R.ID ");
		return sb.toString();
	}

	public static String getProjectSummary(int projid) {
		StringBuilder sb = new StringBuilder();

		sb.append("   SELECT ");
		sb.append("     REF.ID AS ID, ");
		sb.append("     G.GROUP_NAME as USERGROUP, ");
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
		sb.append("     T.DESCRIPTION as TYPE, ");
		sb.append("     T.REQUIRED_LICENSE, ");
		sb.append("     U.EMAIL, ");
		sb.append("     P.PHONE_WORK, ");
		sb.append("     P.ADDRESS,");
		sb.append("     'activity' AS REF,");
		sb.append("     REF.PRIMARY_CONTACT, ");
		sb.append("     'N' AS DEFAULT_CONTACT, ");
		sb.append("     R.LIC_NO ");

		sb.append(" FROM ");
		sb.append(" REF_ACT_USERS AS REF ");
		sb.append(" JOIN ACTIVITY AS A ON REF.ACTIVITY_ID = A.ID AND A.PROJECT_ID = ").append(projid);
		sb.append(" JOIN REF_USERS AS R ON REF.REF_USERS_ID = R.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_USERS_TYPE T on R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0 ");
		sb.append(" LEFT OUTER JOIN USERS_GROUP AS G ON R.USERS_GROUP_ID = G.ID AND R.USERS_ID < 1 ");
		sb.append(" LEFT OUTER JOIN PEOPLE P ON U.ID = P.USERS_ID AND P.ACTIVE = 'Y' ");

		sb.append(" UNION ");

		sb.append(" SELECT ");
		sb.append("     REF.ID AS ID, ");
		sb.append("     G.GROUP_NAME as USERGROUP, ");
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
		sb.append("     T.DESCRIPTION as TYPE, ");
		sb.append("     T.REQUIRED_LICENSE, ");
		sb.append("     U.EMAIL, ");
		sb.append("     P.PHONE_WORK, ");
		sb.append("     P.ADDRESS,");
		sb.append("     'project' AS REF,");
		sb.append("     CASE WHEN PRI.ID IS NOT NULL THEN 'Y' ELSE 'N' END AS PRIMARY_CONTACT, ");
		sb.append("     REF.PRIMARY_CONTACT AS DEFAULT_CONTACT, ");
		sb.append("     R.LIC_NO ");
		sb.append(" FROM ");
		sb.append(" REF_PROJECT_USERS AS REF ");
		sb.append(" JOIN REF_USERS AS R ON REF.REF_USERS_ID = R.ID AND REF.ACTIVE = 'Y' AND REF.PROJECT_ID = ").append(projid);
		sb.append(" JOIN LKUP_USERS_TYPE T on R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0' ");
		sb.append(" LEFT OUTER JOIN USERS_GROUP AS G ON R.USERS_GROUP_ID = G.ID AND R.USERS_ID < 1 ");
		sb.append(" LEFT OUTER JOIN PEOPLE P ON U.ID = P.USERS_ID AND P.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REF_ACT_USERS AS PRI ON PRI.ACTIVITY_ID = A.ID AND PRI.ACTIVE = 'Y' ");
		return sb.toString();
	}


	public static String details(String type, int typeid, int id) {
		return UsersSQL.getRefUser(id);
	}

	public static String info(String type, int typeid, int id) {
		return summary(type, typeid, -1);
	}

	public static String list(String type, int typeid, int id) {
		return summary(type, typeid, -1);
	}

	public static String getUser(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append("     U.ID, ");
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
		sb.append("     )) AS TITLE, ");
		sb.append("     U.EMAIL AS SUBTITLE, ");
		sb.append("     U.EMAIL, ");
		sb.append("     U.FIRST_NAME, ");
		sb.append("     U.LAST_NAME, ");
		sb.append("     U.MI, ");
		sb.append("     U.USERNAME, ");
		sb.append("     P.ADDRESS, ");
		sb.append("     P.CITY, ");
		sb.append("     P.STATE, ");
		sb.append("     P.ZIP, ");
		sb.append("     P.PHONE_WORK, ");
		sb.append("     P.PHONE_CELL, ");
		sb.append("     P.FAX ");
		sb.append(" FROM ");
		sb.append(" USERS AS U ");
		sb.append(" LEFT OUTER JOIN PEOPLE AS P ON U.ID = P.USERS_ID AND P.ACTIVE='Y' ");
		sb.append(" WHERE  U.ACTIVE='Y' AND  ");
		sb.append(" LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("')");
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(" , ");
		sb.append(" Q.TITLE AS NAME ");
		sb.append(" FROM ");
		sb.append(" Q ");
		return sb.toString();
	}

	public static String getOffsitePeople(String username, String account){
		StringBuilder sb = new StringBuilder();
		sb.append("With Q as ( ");
		sb.append("select u.id, u.username,'Y' applicant  from Users u "); 
		sb.append("join REF_USERS ru on u.id = ru.users_id ");
		sb.append("join LKUP_USERS_TYPE lut on ru.lkup_users_type_id = lut.id ");
		sb.append("where lut.offsite='Y'  and u.active ='Y' and u.username = ").append(Operator.checkString(username));
		sb.append("), p as ( ");
		sb.append("select u.id, u.username, 'Y' absentee from Project p "); 
		sb.append("join REF_PROJECT_USERS rpu on p.id = rpu.project_id and rpu.active = 'Y' ");
		sb.append("join ref_users ru on rpu.ref_users_id = ru.id ");   
		sb.append("join users u on ru.users_id = u.id ");
		sb.append("where p.DESCRIPTION like 'Absentee Owner%' and u.active ='Y' and p.account_number = ").append(Operator.checkString(account));
		sb.append(" and u.username = ").append(Operator.checkString(username)).append(") ");
		sb.append("select * from Q ");
		sb.append("full outer join p on q.ID = p.id");
		
		return sb.toString();

	}

	public static String delete(String type, int typeid, int refusersid, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_").append(tableref).append("_USERS ").append(" SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ").append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(" REF_USERS_ID = ").append(refusersid);
		return sb.toString();
	}

	public static String getPrimary(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" U.* ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_USERS AS R ");
		sb.append(" JOIN REF_USERS AS U ON R.REF_USERS_ID = U.ID AND R.PRIMARY_CONTACT = 'Y' AND U.ACTIVE = 'Y' AND R.").append(idref).append(" = ").append(typeid);
		return sb.toString();
	}

	public static String updatePeople(int usersid, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, String comments, int updater, String ip) {
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
		sb.append(" PHONE_CELL = '").append(Operator.sqlEscape(cphone)).append("' ");
		sb.append(" , ");
		sb.append(" PHONE_HOME = '").append(Operator.sqlEscape(hphone)).append("' ");
		sb.append(" , ");
		sb.append(" FAX = '").append(Operator.sqlEscape(fax)).append("' ");
		sb.append(" , ");
		sb.append(" COMMENTS = '").append(Operator.sqlEscape(comments)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(updater);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(" USERS_ID = ").append(usersid);
		return sb.toString();
	}

	public static String update(int peopleid, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, String comments, int updater, String ip) {
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
		sb.append(" PHONE_CELL = '").append(Operator.sqlEscape(cphone)).append("' ");
		sb.append(" , ");
		sb.append(" PHONE_HOME = '").append(Operator.sqlEscape(hphone)).append("' ");
		sb.append(" , ");
		sb.append(" FAX = '").append(Operator.sqlEscape(fax)).append("' ");
		sb.append(" , ");
		sb.append(" COMMENTS = '").append(Operator.sqlEscape(comments)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(updater);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" OUTPUT Inserted.* ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(peopleid);
		return sb.toString();
	}

	public static String addPeople(int usersid, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, int creator, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO PEOPLE ( ");
		sb.append("   USERS_ID, ADDRESS, CITY, STATE, ZIP, PHONE_WORK, PHONE_CELL, PHONE_HOME, FAX, CREATED_BY, CREATED_DATE, CREATED_IP, UPDATED_BY, UPDATED_DATE, UPDATED_IP ");
		sb.append(" ) OUTPUT Inserted.*  VALUES ( ");
		sb.append(usersid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(address)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(city)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(state)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(zip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(wphone)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(cphone)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(hphone)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(fax)).append("' ");
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

	public static String getPeople(int usersid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM PEOPLE WHERE ACTIVE = 'Y' AND USERS_ID = ").append(usersid);
		return sb.toString();
	}

	public static String addApplicant(int userid, int creator, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_USERS ( ");
		sb.append(" USERS_ID ");
		sb.append(" , ");
		sb.append(" LKUP_USERS_TYPE_ID ");
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
		sb.append(" ( SELECT TOP 1 ID FROM LKUP_USERS_TYPE WHERE ACTIVE = 'Y' AND APPLICANT = 'Y' ORDER BY CREATED_DATE ) ");
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

	public static String getPeopleUsers(String type, int typeid) {
		if (Operator.equalsIgnoreCase(type, "activity")) { return getActivityPeopleUsers(typeid); }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" 	U.ID ");
		sb.append(" 	, ");
		sb.append(" 	U.ID AS VALUE ");
		sb.append(" 	, ");
		sb.append(" 	CASE WHEN U.EMAIL IS NULL OR U.EMAIL = '' THEN U.USERNAME ELSE U.EMAIL END AS DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	REF.PRIMARY_CONTACT ");
		sb.append(" 	, ");
		sb.append(" 	T.TYPE ");
		sb.append(" 	, ");
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
		sb.append("     )) AS TEXT ");

		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_USERS AS REF ");
		sb.append(" JOIN REF_USERS AS R ON REF.REF_USERS_ID = R.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_USERS_TYPE AS T ON R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0");
		sb.append(" WHERE ");
		sb.append(" REF.").append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(" REF.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getActivityPeopleUsers(int actid) {
		if (actid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH A AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 	R.REF_USERS_ID ");
		sb.append(" 	, ");
		sb.append(" 	R.PRIMARY_CONTACT ");
		sb.append(" FROM ");
		sb.append(" 	REF_ACT_USERS AS R ");
		sb.append(" WHERE ");
		sb.append(" 	R.ACTIVE = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	R.ACTIVITY_ID = ").append(actid);
		sb.append(" ) ");
		sb.append(" , P AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 	R.REF_USERS_ID ");
		sb.append(" 	, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN R.PRIMARY_CONTACT = 'N' THEN 'N' ");
		sb.append(" 		WHEN R.PRIMARY_CONTACT = 'Y' AND (SELECT COUNT(REF_USERS_ID) AS NUM FROM A WHERE PRIMARY_CONTACT = 'Y') < 1 THEN 'Y' ");
		sb.append(" 	ELSE 'N' END AS PRIMARY_CONTACT ");
		sb.append(" FROM ");
		sb.append(" 	REF_PROJECT_USERS AS R ");
		sb.append(" 	JOIN ACTIVITY AS A ON R.PROJECT_ID = A.PROJECT_ID AND R.ACTIVE = 'Y' AND R.REF_USERS_ID NOT IN (SELECT REF_USERS_ID FROM A) AND A.ID = ").append(actid);
		sb.append(" ) ");
		sb.append(" , UN AS ( ");
		sb.append(" SELECT * FROM A UNION SELECT * FROM P ");
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" 	U.ID ");
		sb.append(" 	, ");
		sb.append(" 	U.ID AS VALUE ");
		sb.append(" 	, ");
		sb.append(" 	CASE WHEN U.EMAIL IS NULL OR U.EMAIL = '' THEN U.USERNAME ELSE U.EMAIL END AS DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	CASE WHEN U.EMAIL IS NULL OR U.EMAIL = '' THEN U.USERNAME ELSE U.EMAIL END AS EMAIL ");
		sb.append(" 	, ");
		sb.append(" 	UN.PRIMARY_CONTACT ");
		sb.append(" 	, ");
		sb.append(" 	T.ID AS LKUP_USERS_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	T.TYPE ");
		sb.append(" 	, ");
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
		sb.append("     )) AS TEXT ");

		sb.append(" FROM ");
		sb.append(" UN ");
		sb.append(" JOIN REF_USERS AS R ON UN.REF_USERS_ID = R.ID ");
		sb.append(" JOIN LKUP_USERS_TYPE AS T ON R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0");
		sb.append(" ORDER BY PRIMARY_CONTACT DESC ");
		return sb.toString();
	}

	public static String getActivityRecipients(int actid, String types) {
		if (actid < 1) { return ""; }
		boolean primary = false;
		String[] t = Operator.split(types, "|");
		boolean empty = true;
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<t.length; i++) {
			String ts = t[i];
			int ti = Operator.toInt(ts);
			if (Operator.equalsIgnoreCase(ts, "primary")) {
				primary = true;
			}
			else if (ti > 0) {
				if (!empty) { sb.append(","); }
				sb.append(ti);
				empty = false;
			}
		}
		String ty = sb.toString();
		sb = new StringBuilder();
		sb.append(" WITH A AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 	R.REF_USERS_ID ");
		sb.append(" 	, ");
		sb.append(" 	R.PRIMARY_CONTACT ");
		sb.append(" FROM ");
		sb.append(" 	REF_ACT_USERS AS R ");
		sb.append(" WHERE ");
		sb.append(" 	R.ACTIVE = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	R.ACTIVITY_ID = ").append(actid);
		sb.append(" ) ");
		sb.append(" , P AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 	R.REF_USERS_ID ");
		sb.append(" 	, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN R.PRIMARY_CONTACT = 'N' THEN 'N' ");
		sb.append(" 		WHEN R.PRIMARY_CONTACT = 'Y' AND (SELECT COUNT(REF_USERS_ID) AS NUM FROM A WHERE PRIMARY_CONTACT = 'Y') < 1 THEN 'Y' ");
		sb.append(" 	ELSE 'N' END AS PRIMARY_CONTACT ");
		sb.append(" FROM ");
		sb.append(" 	REF_PROJECT_USERS AS R ");
		sb.append(" 	JOIN ACTIVITY AS A ON R.PROJECT_ID = A.PROJECT_ID AND R.ACTIVE = 'Y' AND R.REF_USERS_ID NOT IN (SELECT REF_USERS_ID FROM A) AND A.ID = ").append(actid);
		sb.append(" ) ");
		sb.append(" , UN AS ( ");
		sb.append(" SELECT * FROM A UNION SELECT * FROM P ");
		sb.append(" ) ");
		sb.append(" , Q AS ( ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	U.ID ");
		sb.append(" 	, ");
		sb.append(" 	CASE WHEN U.EMAIL IS NULL OR U.EMAIL = '' THEN U.USERNAME ELSE U.EMAIL END AS EMAIL ");
		sb.append(" 	, ");
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

		sb.append(" FROM ");
		sb.append(" UN ");
		sb.append(" JOIN REF_USERS AS R ON UN.REF_USERS_ID = R.ID ");
		sb.append(" JOIN LKUP_USERS_TYPE AS T ON R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0");
		if (Operator.hasValue(ty)) {
			sb.append(" WHERE ");
			sb.append(" T.ID IN ( ").append(ty).append(" ) ");
			if (primary) {
				sb.append(" OR UN.PRIMARY_CONTACT = 'Y' ");
			}
		}
		else if (primary) {
			sb.append(" WHERE UN.PRIMARY_CONTACT = 'Y' ");
		}
		else {
			return "";
		}
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" 	MAX(ID) AS ID, ");
		sb.append(" 	EMAIL, ");
		sb.append(" 	NAME ");
		sb.append(" FROM ");
		sb.append(" 	Q ");
		sb.append(" GROUP BY ");
		sb.append(" 	EMAIL, ");
		sb.append(" 	NAME ");
		return sb.toString();
	}

	public static String copy(String type, int typeid, int newtypeid, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_USERS ( ");
		sb.append(idref);
		sb.append(" , ");
		sb.append(" REF_USERS_ID ");
		sb.append(" , ");
		sb.append(" PRIMARY_CONTACT ");
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
		sb.append(" 	REF_USERS_ID ");
		sb.append(" 	, ");
		sb.append(" 	PRIMARY_CONTACT ");
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
		sb.append(" REF_").append(tableref).append("_USERS ");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		sb.append(" AND ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String isPeople(String type, int typeid, int userid) {
		if (!Operator.hasValue(type)) { return ""; }
		if (typeid < 1) { return ""; }
		if (userid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		if (Operator.equalsIgnoreCase(type, "activity")) { sb.append(isActivityPeople(typeid, userid)); }
		if (Operator.equalsIgnoreCase(type, "project")) { sb.append(isProjectPeople(typeid, userid)); }
		return sb.toString();
	}

	public static String isActivityPeople(int actid, int userid) {
		if (actid < 1) { return ""; }
		if (userid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" 	SELECT ");
		sb.append(" 		A.ACTIVITY_ID AS ID ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_ACT_USERS AS A ");
		sb.append(" 		JOIN REF_USERS AS R ON A.REF_USERS_ID = R.ID AND R.ACTIVE = 'Y' AND A.ACTIVITY_ID = ").append(actid).append(" R.USERS_ID = ").append(userid);
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		A.ID ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_PROJECT_USERS AS P ");
		sb.append(" 		JOIN ACTIVITY AS A ON P.PROJECT_ID = A.PROJECT_ID AND A.ID = ").append(actid);
		sb.append(" 		JOIN REF_USERS AS R ON P.REF_USERS_ID = R.ID AND R.ACTIVE = 'Y' AND R.USERS_ID = ").append(userid);
		return sb.toString();
	}

	public static String getActivities(int projid, int userid) {
		if (projid < 1) { return ""; }
		if (userid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" 	SELECT ");
		sb.append(" 		A.ID ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_ACT_USERS AS AU ");
		sb.append(" 		JOIN ACTIVITY AS A ON AU.ACTIVITY_ID = A.ID AND A.ACTIVE = 'Y' AND A.PROJECT_ID = ").append(projid);
		sb.append(" 		JOIN REF_USERS AS R ON AU.REF_USERS_ID = R.ID AND R.ACTIVE = 'Y' AND R.USERS_ID = ").append(userid);
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		A.ID ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_PROJECT_USERS AS P ");
		sb.append(" 		JOIN ACTIVITY AS A ON P.PROJECT_ID = A.PROJECT_ID AND A.ACTIVE = 'Y' AND A.PROJECT_ID = ").append(projid);
		sb.append(" 		JOIN REF_USERS AS R ON P.REF_USERS_ID = R.ID AND R.ACTIVE = 'Y' AND R.USERS_ID = ").append(userid);
		return sb.toString();
	}

	public static String isProjectPeople(int projid, int userid) {
		if (projid < 1) { return ""; }
		if (userid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" 	SELECT ");
		sb.append(" 		A.PROJECT_ID ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_ACT_USERS AS AU ");
		sb.append(" 		JOIN ACTIVITY AS A ON AU.ACTIVITY_ID = A.ID AND A.ACTIVE = 'Y' AND A.PROJECT_ID = ").append(projid);
		sb.append(" 		JOIN REF_USERS AS R ON AU.REF_USERS_ID = R.ID AND R.ACTIVE = 'Y' AND R.USERS_ID = ").append(userid);
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		P.PROJECT_ID ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_PROJECT_USERS AS P ");
		sb.append(" 		JOIN REF_USERS AS R ON P.REF_USERS_ID = R.ID AND R.ACTIVE = 'Y' AND P.PROJECT_ID = ").append(projid).append(" AND R.USERS_ID = ").append(userid);
		return sb.toString();
	}

	public static String mergeUserPeople(int activeuserid, String username) {
		if (!Operator.hasValue(username)) { return ""; }
		if (activeuserid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE PEOPLE SET ");
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

	public static String disableDuplicatePeople(int activepeopleid, int activeuserid) {
		if (activeuserid < 1) { return ""; }
		if (activepeopleid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE PEOPLE SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" USERS_ID = ").append(activeuserid);
		sb.append(" AND ");
		sb.append(" ID <> ").append(activepeopleid);
		return sb.toString();
	}

	public static String getLastPeople(String username) {
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" P.* ");
		sb.append(" FROM ");
		sb.append(" PEOPLE AS P ");
		sb.append(" JOIN USERS AS U ON P.USERS_ID = U.ID AND P.ACTIVE = 'Y' AND U.ACTIVE = 'Y' AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" ORDER BY ");
		sb.append(" P.UPDATED_DATE DESC, P.CREATED_DATE DESC ");
		return sb.toString();
	}


}















