package csapi.impl.peoplesummary;

import alain.core.utils.Config;
import alain.core.utils.Operator;

public class PeoplesummarySQL {

	public static String details(String type, int typeid, int id) {
		return summary(type, typeid, id);
	}

	public static String summary(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		if (!Operator.equalsIgnoreCase(type, "project")) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(getProjectSummary(typeid));
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" Q.ID ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN Q.USERS_ID IS NULL THEN Q.USERGROUP ");
		sb.append("   ELSE Q.NAME END AS NAME, ");
		sb.append(" Q.ACTIVITY_ID, ");
		sb.append(" Q.ACT_NBR, ");
		sb.append(" Q.TYPE, ");
		sb.append(" Q.EMAIL, ");
		sb.append(" Q.PHONE_WORK, ");
		sb.append(" Q.ADDRESS,");
		sb.append(" CASE WHEN Q.PRIMARY_CONTACT = 'Y' THEN 'Y' WHEN Q.DEFAULT_CONTACT = 'Y' THEN 'D' ELSE 'N' END AS PRIMARY_CONTACT,");
		sb.append(" Q.DEFAULT_CONTACT,");
		sb.append(" Q.REF,");
		sb.append(" Q.REF_ID,");
		sb.append(" Q.LIC_NUM, ");
		sb.append("'").append(Config.rooturl()).append("/").append(Config.contextroot()).append("/jsp/admin/user/user/user.jsp?_id=30&_typeid=30&_ent=permit&type=admin&ID=").append("'+CAST(Q.USERS_ID as VARCHAR(50)) as ADMINLINK ");
		sb.append(" FROM Q ");
		if (id > 0) {
			sb.append(" WHERE ");
			sb.append(" Q.ID = ").append(id);
		}
		sb.append(" ORDER BY NAME, ACT_NBR ");
		return sb.toString();
	}

	public static String getProjectSummary(int projid) {
		StringBuilder sb = new StringBuilder();

		sb.append("   SELECT ");
		sb.append("     REF.ID AS ID, ");
		sb.append("     A.ID AS ACTIVITY_ID, ");
		sb.append("     A.ACT_NBR, ");
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
		sb.append("     P.PHONE_WORK, ");
		sb.append("     P.ADDRESS,");
		sb.append("     'activity' AS REF,");
		sb.append("     A.ID AS REF_ID, ");
		sb.append("     CASE WHEN PRI.ID IS NOT NULL THEN 'Y' ELSE 'N' END AS PRIMARY_CONTACT, ");
		sb.append("     'N' AS DEFAULT_CONTACT, ");
		sb.append("     LI.LIC_NUM ");
		sb.append(" FROM ");
		sb.append(" REF_ACT_USERS AS REF ");
		sb.append(" JOIN ACTIVITY AS A ON REF.ACTIVITY_ID = A.ID AND REF.ACTIVE = 'Y' AND A.PROJECT_ID = ").append(projid);
		sb.append(" JOIN REF_USERS AS R ON REF.REF_USERS_ID = R.ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_USERS_TYPE T on R.LKUP_USERS_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0 AND U.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS_GROUP AS G ON R.USERS_GROUP_ID = G.ID AND R.USERS_ID < 1 AND G.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN PEOPLE P ON U.ID = P.USERS_ID AND P.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN PEOPLE_LICENSE LI on P.ID = LI.PEOPLE_ID AND LI.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REF_ACT_USERS AS PRI ON PRI.ACTIVITY_ID = REF.ACTIVITY_ID AND PRI.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String activities(String type, int typeid) {
		if (!Operator.equalsIgnoreCase(type, "project")) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.ID, ");
		sb.append(" A.ID AS VALUE, ");
		sb.append(" A.ACT_NBR AS TEXT, ");
		sb.append(" T.DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID ");
		sb.append(" WHERE ");
		sb.append(" A.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" A.PROJECT_ID = ").append(typeid);
		return sb.toString();
	}

	public static String save(int actid, int refusersid, int creator, String ip) {
		if (actid < 1 || refusersid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_ACT_USERS (ACTIVITY_ID, REF_USERS_ID, CREATED_BY, UPDATED_BY, CREATED_IP, UPDATED_IP) VALUES ( ");
		sb.append(actid);
		sb.append(" , ");
		sb.append(refusersid);
		sb.append(" , ");
		sb.append(creator);
		sb.append(" , ");
		sb.append(creator);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String find(int actid, int refusersid) {
		if (actid < 1 || refusersid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM REF_ACT_USERS WHERE ACTIVITY_ID = ").append(actid).append(" AND REF_USERS_ID = ").append(refusersid).append(" AND ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String delete(int refid, int userid, String ip) {
		if (refid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_ACT_USERS SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(refid);
		return sb.toString();
	}




}















