package csapi.impl.review;

import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.team.TeamSQL;
import csapi.utils.CsReflect;


public class ReviewOptSQL {

	public static String getReviews(int groupid, int selection) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" NAME AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN ID = ").append(selection).append(" THEN 'Y' ");
		sb.append("   ELSE 'N' END AS SELECTED ");
		sb.append(" , ");
		sb.append(" ORDR ");
		sb.append(" , ");
		sb.append(" REQUIRED ");
		sb.append(" FROM REVIEW ");
		sb.append(" WHERE ");
		sb.append(" REVIEW_GROUP_ID = ").append(groupid);
		sb.append(" ORDER BY ORDR ");
		return sb.toString();
	}

	public static String getStatus(int reviewid, boolean pricontact) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" S.ID ");
		sb.append(" , ");
		sb.append(" S.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" S.STATUS AS TEXT ");
		sb.append(" , ");
		sb.append(" S.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" S.APPROVED ");
		sb.append(" , ");
		sb.append(" S.UNAPPROVED ");
		sb.append(" , ");
		sb.append(" S.SCHEDULE_INSPECTION AS SCHEDULE ");
		sb.append(" , ");
		sb.append(" S.ATTACHMENT ");
		sb.append(" , ");
		sb.append(" S.LIBRARY_GROUP_ID ");
		sb.append(" , ");
		sb.append(" S.ASSIGN ");
		sb.append(" , ");
		sb.append(" S.DAYS_TILL_DUE ");
		sb.append(" , ");
		sb.append(" S.PDOX ");
		sb.append(" , ");
		sb.append(" S.FINAL ");
		sb.append(" , ");
		sb.append(" S.ORDR ");
		sb.append(" FROM ");
		sb.append(" REVIEW AS R ");
		sb.append(" JOIN LKUP_REVIEW_TYPE AS T ON R.LKUP_REVIEW_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON S.LKUP_REVIEW_TYPE_ID = T.ID AND S.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" R.ID = ").append(reviewid);
		if (!pricontact) {
			sb.append(" AND S.PDOX <> 'Y' ");
		}
		sb.append(" ORDER BY S.ORDR, S.STATUS ");
		return sb.toString();
	}

	public static String getRevRefStatus(int reviewrefid, boolean pricontact) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" S.ID ");
		sb.append(" , ");
		sb.append(" S.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" S.STATUS AS TEXT ");
		sb.append(" , ");
		sb.append(" S.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" S.APPROVED ");
		sb.append(" , ");
		sb.append(" S.UNAPPROVED ");
		sb.append(" , ");
		sb.append(" S.SCHEDULE_INSPECTION AS SCHEDULE ");
		sb.append(" , ");
		sb.append(" S.ATTACHMENT ");
		sb.append(" , ");
		sb.append(" S.LIBRARY_GROUP_ID ");
		sb.append(" , ");
		sb.append(" S.ASSIGN ");
		sb.append(" , ");
		sb.append(" S.DAYS_TILL_DUE ");
		sb.append(" , ");
		sb.append(" S.PDOX ");
		sb.append(" , ");
		sb.append(" S.FINAL ");
		sb.append(" , ");
		sb.append(" S.ORDR ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_REVIEW AS CR ");
		sb.append(" JOIN REVIEW AS R ON CR.REVIEW_ID = R.ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_REVIEW_TYPE AS T ON R.LKUP_REVIEW_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON S.LKUP_REVIEW_TYPE_ID = T.ID AND S.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" CR.ID = ").append(reviewrefid);
		if (!pricontact) {
			sb.append(" AND ");
			sb.append(" S.PDOX <> 'Y' ");
		}
		sb.append(" ORDER BY S.ORDR, S.STATUS ");
		return sb.toString();
	}

	public static String getUserGroups(int reviewId) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select U.ID , U.ID as VALUE, U.FIRST_NAME +' '+ U.LAST_NAME as TEXT, U.USERNAME AS DESCRIPTION from REF_REVIEW_TEAM RRT ");
		sb.append("  join REF_USERS_GROUP RUG on RRT.USERS_GROUP_ID = RUG.USERS_GROUP_ID ");
		sb.append(" join USERS U on RUG.USERS_ID = U.ID ");
		sb.append(" WHERE REVIEW_ID=").append(reviewId);
		return sb.toString();
	}

	public static String getReviews(String type, int typeid, String reviewgoup) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String maintable = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" DISTINCT ");
		sb.append(" R.ID ");
		sb.append(" , ");
		sb.append(" R.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" R.NAME AS TEXT ");
		sb.append(" , ");
		sb.append(" R.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" R.DAYS_TILL_DUE ");
		sb.append(" , ");
		sb.append(" R.AVAILABILITY_ID ");
		sb.append(" , ");
		sb.append(" R.ORDR ");
		sb.append(" , ");
		sb.append(" R.NAME ");
		sb.append(" FROM ");
		sb.append(maintable).append(" AS M ");
		sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON M.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
		sb.append(" JOIN REF_").append(tableref).append("_REVIEW_GROUP AS REF ON T.ID = REF.LKUP_").append(tableref).append("_TYPE_ID ");
		sb.append(" JOIN REVIEW_GROUP AS G ON REF.REVIEW_GROUP_ID = G.ID ");
		sb.append(" JOIN REVIEW AS R ON G.ID = R.REVIEW_GROUP_ID ");
		sb.append(" WHERE ");
		if (Operator.isNumber(reviewgoup)) {
			int rg = Operator.toInt(reviewgoup);
			if (rg < 0) {
				rg = rg * -1;
			}
			sb.append(" G.ID = ").append(rg);
		}
		else {
			sb.append(" LOWER(G.GROUP_NAME) = LOWER('").append(Operator.sqlEscape(reviewgoup)).append("') ");
		}
		sb.append(" AND ");
		sb.append(" G.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" R.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" M.ID = ").append(typeid);
		sb.append(" ORDER BY R.ORDR, R.NAME ");
		return sb.toString();
	}

	public static String getApptReviews(String type, int typeid, String reviewgoup) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String maintable = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" R.ID ");
		sb.append(" , ");
		sb.append(" R.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" R.NAME AS TEXT ");
		sb.append(" , ");
		sb.append(" R.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" R.AVAILABILITY_ID ");
		sb.append(" FROM ");
		sb.append(maintable).append(" AS M ");
		sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON M.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
		sb.append(" JOIN REF_").append(tableref).append("_REVIEW_GROUP AS REF ON T.ID = REF.LKUP_").append(tableref).append("_TYPE_ID ");
		sb.append(" JOIN REVIEW_GROUP AS G ON REF.REVIEW_GROUP_ID = G.ID ");
		sb.append(" JOIN REVIEW AS R ON G.ID = R.REVIEW_GROUP_ID AND R.AVAILABILITY_ID > 0 ");
		sb.append(" JOIN LKUP_REVIEW_TYPE AS TYPE ON R.LKUP_REVIEW_TYPE_ID = TYPE.ID AND TYPE.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON TYPE.ID = S.LKUP_REVIEW_TYPE_ID AND S.ACTIVE = 'Y' AND S.SCHEDULE_INSPECTION = 'Y' ");
		sb.append(" WHERE ");
		if (Operator.isNumber(reviewgoup)) {
			int rg = Operator.toInt(reviewgoup);
			if (rg < 0) {
				rg = rg * -1;
			}
			sb.append(" G.ID = ").append(rg);
		}
		else {
			sb.append(" LOWER(G.GROUP_NAME) = LOWER('").append(Operator.sqlEscape(reviewgoup)).append("') ");
		}
		sb.append(" AND ");
		sb.append(" G.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" R.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" M.ID = ").append(typeid);
		return sb.toString();
	}

	// Added after review process change
	public static String getTeam(String type, int typeid, int reviewid) {
		return getTeam(type, typeid, reviewid, -1, "");
	}

	public static String getTeam(String type, int typeid, int reviewid, int reviewrefid, String teamtype) {
		StringBuilder sb = new StringBuilder();

		String c = getComboTeam(reviewrefid, teamtype);
		String a = getActivityTeam(type, typeid, teamtype);
		String p = getProjectTeam(type, typeid, teamtype);
		String r = getReviewTeam(reviewid, teamtype);

		boolean hasC = Operator.hasValue(c);
		boolean hasA = Operator.hasValue(a);
		boolean hasP = Operator.hasValue(p);
		boolean hasR = Operator.hasValue(r);

		boolean empty = true;
		sb.append(" WITH ");
		if (hasC) {
			sb.append(" C AS ( ").append(c).append(" ) ");
			empty = false;
		}
		if (hasP) {
			if (!empty) { sb.append(" , "); }
			sb.append(" P AS ( ").append(p).append(" ) ");
			empty = false;
		}
		if (hasA) {
			if (!empty) { sb.append(" , "); }
			sb.append(" A AS ( ").append(a).append(" ) ");
			empty = false;
		}
		if (hasR) {
			if (!empty) { sb.append(" , "); }
			sb.append(" R AS ( ").append(r).append(" ) ");
			empty = false;
		}

		empty = true;
		sb.append(", U AS ( ");
		if (hasC) {
			sb.append(" SELECT C.* FROM C ");
			empty = false;
		}
		if (hasP) {
			if (!empty) { sb.append(" UNION "); }
			sb.append(" SELECT P.* FROM P ");
			if (hasC) {
				sb.append(" LEFT OUTER JOIN C ON P.ID = C.ID ");
				sb.append(" WHERE C.ID IS NULL ");
			}
			empty = false;
		}
		if (hasA) {
			if (!empty) { sb.append(" UNION "); }
			sb.append(" SELECT A.* FROM A ");
			if (hasC) {
				sb.append(" LEFT OUTER JOIN C ON A.ID = C.ID ");
				sb.append(" WHERE C.ID IS NULL ");
			}
			empty = false;
		}
		if (hasR) {
			if (!empty) { sb.append(" UNION "); }
			sb.append(" SELECT R.* FROM R ");
			if (hasC) {
				sb.append(" LEFT OUTER JOIN C ON R.ID = C.ID ");
			}
			if (hasP) {
				sb.append(" LEFT OUTER JOIN P ON R.ID = P.ID ");
			}
			if (hasC || hasP) {
				sb.append(" WHERE ");
				if (hasC) {
					sb.append(" C.ID IS NULL ");
					if (hasP) {
						sb.append(" AND ");
					}
				}
				if (hasP) {
					sb.append(" P.ID IS NULL ");
				}
			}
			empty = false;
		}
		sb.append(" ) ");

		sb.append(" SELECT DISTINCT * FROM U ");
		sb.append(" ORDER BY TEXT ");
		return sb.toString();
	}

	public static String getProjectTeam(String type, int typeid) {
		return getProjectTeam(type, typeid, "");
	}

	public static String getProjectTeam(String type, int typeid, String teamtype) {
		if (!Operator.hasValue(type)) { return ""; }
		if (!Operator.equalsIgnoreCase(type, "project") && !Operator.equalsIgnoreCase(type, "activity")) { return ""; }
		if (typeid < 1) { return ""; }
		
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" RT.ID ");
		sb.append(" , ");
		sb.append(" RT.ID AS VALUE ");
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
		sb.append(" , ");
		sb.append(" REF.PROJECT_ID ");
		sb.append(" , ");
		sb.append(" 'project' AS RECTYPE ");
		sb.append(" , ");
		sb.append(" 'N' AS SELECTED ");

		sb.append(" FROM ");
		sb.append(" REF_PROJECT_TEAM AS REF ");
		if (Operator.equalsIgnoreCase(type, "activity")) {
			sb.append(" JOIN ACTIVITY AS A ON REF.PROJECT_ID = A.PROJECT_ID ");
		}

		sb.append(" JOIN REF_TEAM AS RT ON REF.REF_TEAM_ID = RT.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_TEAM_TYPE AS T ON RT.LKUP_TEAM_TYPE_ID = T.ID ");
		if (Operator.hasValue(teamtype)) {
			sb.append(" AND LOWER(T.TYPE) LIKE LOWER('%").append(teamtype).append("%') ");
		}

		sb.append(" LEFT OUTER JOIN USERS AS U ON RT.USERS_ID = U.ID AND RT.USERS_ID > 0");

		sb.append(" WHERE ");
		if (Operator.equalsIgnoreCase(type, "activity")) {
			sb.append(" A.ID = ").append(typeid);
		}
		else {
			sb.append(" REF.PROJECT_ID = ").append(typeid);
		}
		return sb.toString();
	}

	public static String getActivityTeam(String type, int typeid, String teamtype) {
		if (!Operator.hasValue(type)) { return ""; }
		if (!Operator.equalsIgnoreCase(type, "activity")) { return ""; }
		if (typeid < 1) { return ""; }
		
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" RT.ID ");
		sb.append(" , ");
		sb.append(" RT.ID AS VALUE ");
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
		sb.append(" , ");
		sb.append(" A.PROJECT_ID ");
		sb.append(" , ");
		sb.append(" 'activity' AS RECTYPE ");
		sb.append(" , ");
		sb.append(" 'N' AS SELECTED ");

		sb.append(" FROM ");
		sb.append(" REF_ACT_TEAM AS REF ");
		sb.append(" JOIN ACTIVITY AS A ON REF.ACTIVITY_ID = A.ID ");
		sb.append(" JOIN REF_TEAM AS RT ON REF.REF_TEAM_ID = RT.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_TEAM_TYPE AS T ON RT.LKUP_TEAM_TYPE_ID = T.ID ");
		if (Operator.hasValue(teamtype)) {
			sb.append(" AND LOWER(T.TYPE) LIKE LOWER('%").append(teamtype).append("%') ");
		}
		sb.append(" LEFT OUTER JOIN USERS AS U ON RT.USERS_ID = U.ID AND RT.USERS_ID > 0");

		sb.append(" WHERE ");
		sb.append(" A.ID = ").append(typeid);
		return sb.toString();
	}

	public static String getReviewTeam(int reviewid, String teamtype) {
		if (reviewid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" RT.ID ");
		sb.append(" , ");
		sb.append(" RT.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" LTRIM(RTRIM( ");
		sb.append("     CASE  ");
		sb.append("       WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("       ELSE ''  ");
		sb.append("     END +  ");
		sb.append("     CASE  ");
		sb.append("       WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("       ELSE ''  ");
		sb.append("     END +  ");
		sb.append("     CASE  ");
		sb.append("       WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("       ELSE '' ");
		sb.append("     END + ");
		sb.append("       CASE  ");
		sb.append("         WHEN T.TYPE IS NOT NULL AND T.TYPE <> '' THEN ' (' + T.TYPE + ') ' ");
		sb.append("         ELSE '' ");
		sb.append("     END ");
		sb.append(" )) AS TEXT ");
		sb.append(" , ");
		sb.append(" -1 AS PROJECT_ID ");
		sb.append(" , ");
		sb.append(" 'review' AS RECTYPE ");
		sb.append(" , ");
		sb.append(" 'N' AS SELECTED ");

		sb.append(" FROM ");
		sb.append(" REF_REVIEW_TEAM AS REF ");
		sb.append(" JOIN REF_TEAM AS RT ON REF.LKUP_TEAM_TYPE_ID = RT.LKUP_TEAM_TYPE_ID AND RT.ACTIVE = 'Y' AND REF.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_TEAM_TYPE AS T ON RT.LKUP_TEAM_TYPE_ID = T.ID ");
		if (Operator.hasValue(teamtype)) {
			sb.append(" AND LOWER(T.TYPE) LIKE LOWER('%").append(teamtype).append("%') ");
		}
		sb.append(" JOIN USERS AS U ON RT.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" REF.REVIEW_ID = ").append(reviewid);
		return sb.toString();
	}

	public static String getComboTeam(int reviewrefid, String teamtype) {
		if (reviewrefid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" RT.ID ");
		sb.append(" , ");
		sb.append(" RT.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" LTRIM(RTRIM( ");
		sb.append("     CASE  ");
		sb.append("       WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("       ELSE ''  ");
		sb.append("     END +  ");
		sb.append("     CASE  ");
		sb.append("       WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("       ELSE ''  ");
		sb.append("     END +  ");
		sb.append("     CASE  ");
		sb.append("       WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("       ELSE '' ");
		sb.append("     END + ");
		sb.append("       CASE  ");
		sb.append("         WHEN T.TYPE IS NOT NULL AND T.TYPE <> '' THEN ' (' + T.TYPE + ') ' ");
		sb.append("         ELSE '' ");
		sb.append("     END ");
		sb.append(" )) AS TEXT ");
		sb.append(" , ");
		sb.append(" -1 AS PROJECT_ID ");
		sb.append(" , ");
		sb.append(" 'comboreview' AS RECTYPE ");
		sb.append(" , ");
		sb.append(" 'Y' AS SELECTED ");

		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_TEAM AS REF ");
		sb.append(" JOIN REF_TEAM AS RT ON REF.REF_TEAM_ID = RT.ID AND RT.ACTIVE = 'Y' AND REF.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_TEAM_TYPE AS T ON RT.LKUP_TEAM_TYPE_ID = T.ID ");
		if (Operator.hasValue(teamtype)) {
			sb.append(" AND LOWER(T.TYPE) LIKE LOWER('%").append(teamtype).append("%') ");
		}
		sb.append(" JOIN USERS AS U ON RT.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" REF.REF_COMBOREVIEW_REVIEW_ID = ").append(reviewrefid);
		return sb.toString();
	}






}



















