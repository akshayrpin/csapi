package csapi.impl.review;

import alain.core.security.Token;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.general.GeneralSQL;
import csapi.utils.CsReflect;
import csshared.vo.RequestVO;

public class ReviewSQL {

	public static String review(int groupid, int reviewid) {
		if (reviewid < 1) { return review(groupid); }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		if (reviewid > 0) {
			sb.append(" R.ID AS REVIEW_ID ");
			sb.append(" , ");
			sb.append(" R.NAME AS REVIEW ");
			sb.append(" , ");
		}
		sb.append(" G.ID AS GROUP_ID ");
		sb.append(" , ");
		sb.append(" GROUP_NAME AS GROUP_NAME ");
		sb.append(" FROM ");
		sb.append(" REVIEW_GROUP AS G ");
		if (reviewid > 0) {
			sb.append(" LEFT OUTER JOIN REVIEW AS R ON G.ID = R.REVIEW_GROUP_ID AND R.ACTIVE = 'Y' AND R.ID = ").append(reviewid);
		}
		sb.append(" WHERE ");
		sb.append(" G.ID = ").append(groupid);
		return sb.toString();
	}

	public static String getReview(int reviewid) {
		if (reviewid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT R.*, S.ID AS SCHEDULE_INSPECTION_ID ");
		sb.append(" FROM ");
		sb.append(" REVIEW AS R ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON R.LKUP_REVIEW_TYPE_ID = S.LKUP_REVIEW_TYPE_ID AND S.SCHEDULE_INSPECTION = 'Y' AND S.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" R.ID = ").append(reviewid);
		return sb.toString();
	}

	public static String getCurrentInspection(String type, int typeid, int reviewid) {
		if (!Operator.hasValue(type) || typeid < 1 || reviewid < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" TOP 1 ");
		sb.append(" M.ID AS REF_COMBOREVIEW_ID ");
		sb.append(" , ");
		sb.append(" C.ID AS COMBOREVIEW_ID ");
		sb.append(" , ");
		sb.append(" RR.ID AS REF_COMBOREVIEW_REVIEW_ID ");
		sb.append(" , ");
		sb.append(" R.ID AS REVIEW_ID ");
		sb.append(" , ");
		sb.append(" R.REVIEW_GROUP_ID ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_COMBOREVIEW AS M ");
		sb.append(" JOIN COMBOREVIEW AS C ON M.COMBOREVIEW_ID = C.ID AND M.").append(idref).append(" = ").append(typeid).append(" AND C.ACTIVE = 'Y' AND M.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RR ON C.ID = RR.COMBOREVIEW_ID AND RR.ACTIVE = 'Y' AND RR.REVIEW_ID = ").append(reviewid);
		sb.append(" JOIN REVIEW AS R ON RR.REVIEW_ID = R.ID ");
		sb.append(" ORDER BY M.UPDATED_DATE DESC ");
		return sb.toString();
	}

	public static String getReviewTeam(String type, int typeid, int reviewid) {
		if (!Operator.equalsIgnoreCase(type, "activity") && !Operator.equalsIgnoreCase(type, "project")) { return ""; }
		if (typeid < 1) { return ""; }
		if (reviewid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		if (Operator.equalsIgnoreCase(type, "activity")) {
			sb.append(" 	SELECT ");
			sb.append(" 		T.ID AS REF_TEAM_ID, ");
			sb.append(" 		RT.CREATED_DATE ");
			sb.append(" 	FROM ");
			sb.append(" 		ACTIVITY AS A ");
			sb.append(" 		JOIN REF_ACT_TEAM AS RT ON RT.ACTIVITY_ID = A.ID AND RT.ACTIVE = 'Y' AND A.ID = ").append(typeid);
			sb.append(" 		JOIN REF_TEAM AS T ON RT.REF_TEAM_ID = T.ID AND T.ACTIVE = 'Y' ");
			sb.append(" 		JOIN REF_REVIEW_TYPE_TEAM AS RRT ON T.LKUP_TEAM_TYPE_ID = RRT.LKUP_TEAM_TYPE_ID AND RRT.ACTIVE = 'Y' ");
			sb.append(" 		JOIN REVIEW AS R ON R.LKUP_REVIEW_TYPE_ID = RRT.LKUP_REVIEW_TYPE_ID AND R.ACTIVE = 'Y' AND R.ID = ").append(reviewid);
			sb.append(" 	UNION ");
			sb.append(" 	SELECT ");
			sb.append(" 		T.ID AS REF_TEAM_ID, ");
			sb.append(" 		RT.CREATED_DATE ");
			sb.append(" 	FROM ");
			sb.append(" 		ACTIVITY AS A ");
			sb.append(" 		JOIN REF_PROJECT_TEAM AS RT ON RT.PROJECT_ID = A.PROJECT_ID AND RT.ACTIVE = 'Y' AND A.ID = ").append(typeid);
			sb.append(" 		JOIN REF_TEAM AS T ON RT.REF_TEAM_ID = T.ID AND T.ACTIVE = 'Y' ");
			sb.append(" 		JOIN REF_REVIEW_TYPE_TEAM AS RRT ON T.LKUP_TEAM_TYPE_ID = RRT.LKUP_TEAM_TYPE_ID AND RRT.ACTIVE = 'Y' ");
			sb.append(" 		JOIN REVIEW AS R ON R.LKUP_REVIEW_TYPE_ID = RRT.LKUP_REVIEW_TYPE_ID AND R.ACTIVE = 'Y' AND R.ID = ").append(reviewid);
		}
		else {
			sb.append(" 	SELECT ");
			sb.append(" 		T.ID AS REF_TEAM_ID, ");
			sb.append(" 		RT.CREATED_DATE ");
			sb.append(" 	FROM ");
			sb.append(" 		PROJECT AS P ");
			sb.append(" 		JOIN REF_PROJECT_TEAM AS RT ON RT.PROJECT_ID = P.ID AND RT.ACTIVE = 'Y' AND P.ID = ").append(typeid);
			sb.append(" 		JOIN REF_TEAM AS T ON RT.REF_TEAM_ID = T.ID AND T.ACTIVE = 'Y' ");
			sb.append(" 		JOIN REF_REVIEW_TYPE_TEAM AS RRT ON T.LKUP_TEAM_TYPE_ID = RRT.LKUP_TEAM_TYPE_ID AND RRT.ACTIVE = 'Y' ");
			sb.append(" 		JOIN REVIEW AS R ON R.LKUP_REVIEW_TYPE_ID = RRT.LKUP_REVIEW_TYPE_ID AND R.ACTIVE = 'Y' AND R.ID = ").append(reviewid);
		}
		sb.append(" ) ");
		sb.append(" SELECT TOP 1 REF_TEAM_ID FROM Q ORDER BY CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String review(int groupid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID AS GROUP_ID ");
		sb.append(" , ");
		sb.append(" GROUP_NAME AS GROUP_NAME ");
		sb.append(" FROM ");
		sb.append(" REVIEW_GROUP ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(groupid);
		return sb.toString();
	}

	/**
	 * @deprecated No used
	 */
	public static String reviews(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append("    SELECT ");
		sb.append("        R.ID, MAX(A.DATE) AS MAXDATE ");
		sb.append("    FROM ");
		sb.append("        REF_").append(tableref).append("_REVIEW AS REF ");
		sb.append("        JOIN REVIEW_ACTION AS A ON REF.REVIEW_ACTION_ID = A.ID ");
		sb.append("        JOIN REVIEW AS R ON A.REVIEW_ID = R.ID ");
		sb.append("    WHERE ");
		sb.append("        REF.").append(idref).append(" = ").append(typeid);
		sb.append("    GROUP BY ");
		sb.append("        R.ID ");
		sb.append(" ) ");
		sb.append(" , ");
		sb.append(" MA AS ( ");
		sb.append("    SELECT ");
		sb.append("        Q.ID AS REVIEW_ID, MAX(A.ID) AS ID ");
		sb.append("    FROM ");
		sb.append("        REF_").append(tableref).append("_REVIEW AS REF ");
		sb.append("        JOIN REVIEW_ACTION AS A ON REF.REVIEW_ACTION_ID = A.ID ");
		sb.append("        JOIN Q ON A.REVIEW_ID = Q.ID AND A.DATE = Q.MAXDATE ");
		sb.append("    WHERE ");
		sb.append("        REF.").append(idref).append(" = ").append(typeid);
		sb.append("    GROUP BY ");
		sb.append("        Q.ID ");
		sb.append(" ) ");

		sb.append(" , ");
		sb.append(" S AS ( ");
		sb.append("     SELECT ");
		sb.append("         DISTINCT ");
		sb.append("         A.ID AS PARENT_ID, ");
		sb.append("         '").append(maintableref).append("' AS PARENT, ");
		sb.append("         R.ID AS REVIEW_ID, ");
		sb.append("         G.ID AS REVIEW_GROUP_ID, ");
		sb.append("         G.GROUP_NAME, ");
		sb.append("         R.NAME AS REVIEW, ");
		sb.append("         ACTN.ID AS ACTION_ID, ");
		//sb.append("         ACTN.NAME AS ACTION, ");
		sb.append("         R.NAME AS ACTION, ");
		sb.append("         S.DESCRIPTION AS STATUS, ");
		sb.append("         ACTN.DATE, ");
		sb.append("         R.DISPLAY_TYPE, ");
		sb.append("         G.ORDR AS GROUP_ORDER, ");
		sb.append("         R.ORDR AS REVIEW_ORDER ");
		sb.append("     FROM ");
		sb.append("        ").append(maintableref).append(" AS A ");
		sb.append("        JOIN LKUP_").append(tableref).append("_TYPE AS AT ON A.LKUP_").append(tableref).append("_TYPE_ID = AT.ID ");
		sb.append("        JOIN REF_").append(tableref).append("_REVIEW_GROUP AS ARG ON AT.ID = ARG.LKUP_").append(tableref).append("_TYPE_ID ");
		sb.append("        JOIN REVIEW_GROUP AS G ON ARG.REVIEW_GROUP_ID = G.ID ");
		sb.append("        JOIN REVIEW AS R ON G.ID = R.REVIEW_GROUP_ID ");
		sb.append("        LEFT OUTER JOIN ( ");
		sb.append("            MA ");
		sb.append("            JOIN REVIEW_ACTION AS ACTN ON MA.ID = ACTN.ID ");
		sb.append("            JOIN REF_").append(tableref).append("_REVIEW AS AR ON ACTN.ID = AR.REVIEW_ACTION_ID AND AR.").append(idref).append(" = ").append(typeid);
		sb.append("            JOIN LKUP_REVIEW_STATUS AS S ON ACTN.LKUP_REVIEW_STATUS_ID = S.ID ");
		sb.append("        ) ON R.ID = ACTN.REVIEW_ID ");
		sb.append("     WHERE ");
		sb.append("        A.ID = ").append(typeid);
//		sb.append("        AND ");
//		sb.append("        ( ");
//		sb.append("            ACTN.NAME IS NOT NULL ");
//		sb.append("            OR ");
//		sb.append("            R.DISPLAY_TYPE = 'Y' ");
//		sb.append("        ) ");
		sb.append(" ) ");
		sb.append(" , ");
		sb.append(" G AS ( ");
		sb.append("     SELECT DISTINCT REVIEW_GROUP_ID, GROUP_NAME FROM S ");
		sb.append(" ) ");
		sb.append(" , ");
		sb.append(" M AS ( ");
		sb.append("    SELECT ");
		sb.append("         DISTINCT ");
		sb.append("         S.PARENT_ID, ");
		sb.append("         S.PARENT, ");
		sb.append("         S.REVIEW_ID, ");
		sb.append("         G.REVIEW_GROUP_ID, ");
		sb.append("         G.GROUP_NAME, ");
		sb.append("         S.REVIEW, ");
		sb.append("         S.ACTION_ID, ");
		sb.append("         S.ACTION, ");
		sb.append("         S.STATUS, ");
		sb.append("         S.DATE, ");
		sb.append("         S.DISPLAY_TYPE, ");
		sb.append("         S.GROUP_ORDER, ");
		sb.append("         S.REVIEW_ORDER ");
		sb.append("    FROM ");
		sb.append("        G ");
		sb.append("        LEFT OUTER JOIN S ON G.REVIEW_GROUP_ID = S.REVIEW_GROUP_ID AND S.GROUP_NAME = G.GROUP_NAME ");
		//sb.append(" AND (S.ACTION IS NOT NULL OR S.DISPLAY_TYPE = 'Y' ) ");
		sb.append(" AND (S.ACTION_ID IS NOT NULL OR S.DISPLAY_TYPE = 'Y' ) ");
		sb.append(" ) ");
		sb.append(" , ");
		sb.append(" GC AS ( ");
		sb.append("     SELECT COUNT(DISTINCT GROUP_NAME) AS GROUPS FROM M ");
		sb.append(" ) ");

		sb.append(" SELECT ");
		sb.append("     M.*, GC.GROUPS ");
		sb.append(" FROM ");
		sb.append("     M ");
		sb.append("     JOIN GC ON 1=1 ");
		sb.append(" ORDER BY ");
		sb.append("     M.GROUP_ORDER, M.GROUP_NAME, M.REVIEW_ORDER, M.REVIEW ");

		return sb.toString();
	}

	public static String groups(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" G.* ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_COMBOREVIEW AS RC ");
		sb.append(" JOIN COMBOREVIEW AS C ON RC.COMBOREVIEW_ID = C.ID AND RC.").append(idref).append(" = ").append(typeid).append(" AND RC.ACTIVE = 'Y' AND C.ACTIVE = 'Y' ");
		sb.append(" JOIN REVIEW_GROUP AS G ON C.REVIEW_GROUP_ID = G.ID ");
		return sb.toString();
	}

	public static String list(String type, int typeid, int groupid, int reviewid) {
		String tableref = CsReflect.getTableRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append("     SELECT ");
		sb.append("     A.ID ");
		sb.append("     , ");
		sb.append("     G.GROUP_NAME ");
		sb.append("     , ");
		sb.append("     G.DESCRIPTION AS GROUP_DESCRIPTION ");
		sb.append("     , ");
		sb.append("     G.ORDR AS GROUP_ORDER ");
		sb.append("     , ");
		sb.append("     R.NAME AS REVIEW ");
		sb.append("     , ");
		sb.append("     R.DESCRIPTION AS REVIEW_DESCRIPTION ");
		sb.append("     , ");
		sb.append("     R.ORDR AS REVIEW_ORDER ");
		sb.append("     , ");
		sb.append("     A.DATE ");
		sb.append("     , ");
		sb.append("     A.REVIEW_COMMENTS ");
		sb.append("     , ");
		sb.append("     S.STATUS ");
		sb.append("     , ");
		sb.append("     'Y' AS FINAL ");
		sb.append("     FROM ");
		sb.append("         ").append(maintableref).append(" AS M ");
		sb.append("         JOIN REF_").append(tableref).append("_REVIEW_GROUP AS GREF ON GREF.ACTIVE = 'Y' AND M.LKUP_").append(tableref).append("_TYPE_ID = GREF.LKUP_").append(tableref).append("_TYPE_ID AND M.ID = ").append(typeid);
		sb.append("         JOIN REVIEW_GROUP AS G ON GREF.REVIEW_GROUP_ID = G.ID AND G.ACTIVE = 'Y' AND G.ID = ").append(groupid);
		sb.append("         JOIN REVIEW AS R ON R.REVIEW_GROUP_ID = G.ID AND R.ACTIVE = 'Y' ");
		if (reviewid > 0) {
			sb.append(" AND R.ID = ").append(reviewid);
		}

		sb.append("         JOIN ( ");
		sb.append("             REF_").append(tableref).append("_REVIEW AS REF ");
		sb.append("             JOIN REVIEW_ACTION AS A ON REF.REVIEW_ACTION_ID = A.ID AND REF.ACTIVE = 'Y' ");
		sb.append("             JOIN LKUP_REVIEW_STATUS AS S ON A.LKUP_REVIEW_STATUS_ID = S.ID AND S.ACTIVE = 'Y' ");
		sb.append("         ) ON A.REVIEW_ID = R.ID AND REF.").append(idref).append(" = M.ID ");

		sb.append(" ) ");

		sb.append(" SELECT ");
		sb.append("     Q.* ");
		sb.append(" FROM ");
		sb.append("     Q ");
		sb.append(" ORDER BY ");
		sb.append("     Q.GROUP_ORDER, Q.GROUP_NAME, Q.REVIEW_ORDER, Q.REVIEW ");

		return sb.toString();
	}
	

	public static String getType(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT A.ID, T.DESCRIPTION AS TITLE, A.ACT_NBR AS SUBTITLE ");
		sb.append(" FROM ");
		sb.append(ReviewFields.MAIN_TABLE_REF).append(" A ");
		sb.append(" JOIN ").append(Table.ACTTYPETABLE).append(" AS T ON A.LKUP_ACT_TYPE_ID = T.ID ");
		sb.append(" WHERE ");
		sb.append(" A.ID = ").append(id).append(" AND A.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getDetails(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" select A.*,ATT.DESCRIPTION as TITLE, A.ACT_NBR AS SUBTITLE,ATS.LKUP_ACT_STATUS_ID,LAS.STATUS,CU.USERNAME AS CREATED,UP.USERNAME as UPDATED   "); 
		sb.append("  from  ").append(Table.ACTIVITYTABLE).append(" A ");
		sb.append("  JOIN  ").append(Table.ACTTYPETABLE).append(" ATT ON A.LKUP_ACT_TYPE_ID=ATT.ID ");
		//TODO PROPER QUERY OF STATUS SEPARATE
		sb.append(" LEFT OUTER JOIN ").append(Table.ACTSTATUSTABLE).append(" ATS ON A.ID=ATS.ACTIVITY_ID ");
		sb.append(" LEFT OUTER JOIN LKUP_ACT_STATUS LAS on ATS.LKUP_ACT_STATUS_ID=LAS.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY=CU.ID ");
		sb.append("	LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY=UP.ID "); 
		sb.append(" WHERE A.ID= ").append(id).append(" AND A.ACTIVE='Y' ");
		
		
		return sb.toString();
	}

	public static String getCustomGroups(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT");
		sb.append(" G.*, R.ORDR ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID ");
		sb.append(" JOIN REF_ACT_FIELD_GROUPS AS R ON R.LKUP_ACT_TYPE_ID = T.ID ");
		sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID ");
		sb.append(" WHERE ");
		sb.append(" G.ACTIVE = 'Y'  ");
		sb.append(" AND ");
		sb.append(" A.ID = ").append(id);
		sb.append(" ORDER BY R.ORDR ");
		return sb.toString();
		
	
	}

	public static String getCustomGroupsAndFields(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" V.ID ");
		sb.append(" , ");
		sb.append(" F.NAME ");
		sb.append(" , ");
		sb.append(" V.VALUE ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME ");
		sb.append(" ,");
		sb.append(" G.ID as GROUP_ID ");
		sb.append(" ,");
		sb.append(" G.ISPUBLIC as GROUP_PUBLIC ");
		sb.append(" , ");
		sb.append(" R.ORDR AS GROUP_ORDER ");
		sb.append(" , ");
		sb.append(" F.ORDR AS FIELD_ORDER ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS P ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON P.LKUP_ACT_TYPE_ID = T.ID ");
		sb.append(" JOIN REF_ACT_FIELD_GROUPS AS R ON R.LKUP_ACT_TYPE_ID = T.ID ");
		sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID ");
		sb.append(" JOIN FIELD AS F ON F.FIELD_GROUPS_ID = G.ID ");
		sb.append(" LEFT OUTER JOIN ACT_FIELD_VALUE AS V ON F.ID = V.FIELD_ID AND V.ACTIVE = 'Y' AND V.ACTIVITY_ID = P.ID ");
		sb.append(" WHERE ");
		sb.append(" F.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" G.ACTIVE = 'Y'  ");
		sb.append(" AND ");
		sb.append(" P.ID = ").append(id);
		sb.append(" ORDER BY R.ORDR, G.GROUP_NAME, F.ORDR ");
		return sb.toString();
	}

	public static String getReviews(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append("    SELECT ");
		sb.append("        R.ID, MAX(A.DATE) AS MAXDATE ");
		sb.append("    FROM ");
		sb.append("        REF_ACT_REVIEW AS REF ");
		sb.append("        JOIN REVIEW_ACTION AS A ON REF.REVIEW_ACTION_ID = A.ID ");
		sb.append("        JOIN REVIEW AS R ON A.REVIEW_ID = R.ID ");
		sb.append("    WHERE ");
		sb.append("        REF.ACTIVITY_ID = ").append(id);
		sb.append("    GROUP BY ");
		sb.append("        R.ID ");
		sb.append(" ) ");
		sb.append(" , ");
		sb.append(" MA AS ( ");
		sb.append("    SELECT ");
		sb.append("        Q.ID AS REVIEW_ID, MAX(A.ID) AS ID ");
		sb.append("    FROM ");
		sb.append("        REF_ACT_REVIEW AS REF ");
		sb.append("        JOIN REVIEW_ACTION AS A ON REF.REVIEW_ACTION_ID = A.ID ");
		sb.append("        JOIN Q ON A.REVIEW_ID = Q.ID AND A.DATE = Q.MAXDATE ");
		sb.append("    WHERE ");
		sb.append("        REF.ACTIVITY_ID = ").append(id);
		sb.append("    GROUP BY ");
		sb.append("        Q.ID ");
		sb.append(" ) ");

		sb.append(" , ");
		sb.append(" S AS ( ");
		sb.append("     SELECT ");
		sb.append("         DISTINCT ");
		sb.append("         A.ID AS PARENT_ID, ");
		sb.append("         'activity' AS PARENT, ");
		sb.append("         R.ID AS REVIEW_ID, ");
		sb.append("         G.ID AS REVIEW_GROUP_ID, ");
		sb.append("         G.GROUP_NAME, ");
		sb.append("         R.NAME AS REVIEW, ");
		sb.append("         ACTN.ID AS ACTION_ID, ");
		//sb.append("         ACTN.NAME AS ACTION, ");
		sb.append("         R.NAME AS ACTION, ");
		sb.append("         S.DESCRIPTION AS STATUS, ");
		sb.append("         ACTN.DATE, ");
		sb.append("         R.DISPLAY_TYPE, ");
		sb.append("         G.ORDR AS GROUP_ORDER, ");
		sb.append("         R.ORDR AS REVIEW_ORDER ");
		sb.append("     FROM ");
		sb.append("        ACTIVITY AS A ");
		sb.append("        JOIN LKUP_ACT_TYPE AS AT ON A.LKUP_ACT_TYPE_ID = AT.ID ");
		sb.append("        JOIN REF_ACT_REVIEW_GROUP AS ARG ON AT.ID = ARG.LKUP_ACT_TYPE_ID ");
		sb.append("        JOIN REVIEW_GROUP AS G ON ARG.REVIEW_GROUP_ID = G.ID ");
		sb.append("        JOIN REVIEW AS R ON G.ID = R.REVIEW_GROUP_ID ");
		sb.append("        LEFT OUTER JOIN ( ");
		sb.append("            MA ");
		sb.append("            JOIN REVIEW_ACTION AS ACTN ON MA.ID = ACTN.ID ");
		sb.append("            JOIN REF_ACT_REVIEW AS AR ON ACTN.ID = AR.REVIEW_ACTION_ID AND AR.ACTIVITY_ID = ").append(id);
		sb.append("            JOIN LKUP_REVIEW_STATUS AS S ON ACTN.LKUP_REVIEW_STATUS_ID = S.ID ");
		sb.append("        ) ON R.ID = ACTN.REVIEW_ID ");
		sb.append("     WHERE ");
		sb.append("        A.ID = ").append(id);
//		sb.append("        AND ");
//		sb.append("        ( ");
//		sb.append("            ACTN.NAME IS NOT NULL ");
//		sb.append("            OR ");
//		sb.append("            R.DISPLAY_TYPE = 'Y' ");
//		sb.append("        ) ");
		sb.append(" ) ");
		sb.append(" , ");
		sb.append(" G AS ( ");
		sb.append("     SELECT DISTINCT REVIEW_GROUP_ID, GROUP_NAME FROM S ");
		sb.append(" ) ");
		sb.append(" , ");
		sb.append(" M AS ( ");
		sb.append("    SELECT ");
		sb.append("         DISTINCT ");
		sb.append("         S.PARENT_ID, ");
		sb.append("         S.PARENT, ");
		sb.append("         S.REVIEW_ID, ");
		sb.append("         G.REVIEW_GROUP_ID, ");
		sb.append("         G.GROUP_NAME, ");
		sb.append("         S.REVIEW, ");
		sb.append("         S.ACTION_ID, ");
		sb.append("         S.ACTION, ");
		sb.append("         S.STATUS, ");
		sb.append("         S.DATE, ");
		sb.append("         S.DISPLAY_TYPE, ");
		sb.append("         S.GROUP_ORDER, ");
		sb.append("         S.REVIEW_ORDER ");
		sb.append("    FROM ");
		sb.append("        G ");
		sb.append("        LEFT OUTER JOIN S ON G.REVIEW_GROUP_ID = S.REVIEW_GROUP_ID AND S.GROUP_NAME = G.GROUP_NAME ");
		
		//sb.append(" AND (S.ACTION IS NOT NULL OR S.DISPLAY_TYPE = 'Y' ) ");
		sb.append(" AND (S.ACTION_ID IS NOT NULL OR S.DISPLAY_TYPE = 'Y' ) ");
		
		sb.append(" ) ");
		sb.append(" , ");
		sb.append(" GC AS ( ");
		sb.append("     SELECT COUNT(DISTINCT GROUP_NAME) AS GROUPS FROM M ");
		sb.append(" ) ");

		sb.append(" SELECT ");
		sb.append("     M.*, GC.GROUPS ");
		sb.append(" FROM ");
		sb.append("     M ");
		sb.append("     JOIN GC ON 1=1 ");
		sb.append(" ORDER BY ");
		sb.append("     M.GROUP_ORDER, M.GROUP_NAME, M.REVIEW_ORDER, M.REVIEW ");

		return sb.toString();
	}
	
	public static String getRefIdReview(RequestVO r, Token u) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select TOP 1 ID FROM ").append(Table.REVIEWACTIONTABLE).append(" ");
		sb.append(" WHERE ID >0  ");
		sb.append(" AND ");
		/*sb.append(" PATH = '").append(Operator.sqlEscape(obj[2].getValue())).append("' ");
		sb.append(" AND ");*/
		sb.append(" CREATED_BY = ").append(u.getId());
		sb.append(" order by CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String insertRefReview(RequestVO r, Token u, int id) {
		return GeneralSQL.insertRefCommon(r,u,id);
	}
	
	public static String addAction(int reviewid, int statusid, Timekeeper date, String comment, int userid, int scheduleid, int createdby, String ip, Timekeeper updateddate) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REVIEW_ACTION ( ");
		sb.append(" REVIEW_ID ");
		sb.append(" , ");
		sb.append(" LKUP_REVIEW_STATUS_ID ");
		sb.append(" , ");
		sb.append(" DATE ");
		sb.append(" , ");
		sb.append(" REVIEW_COMMENTS ");
		sb.append(" , ");
		sb.append(" USERS_ID ");
		sb.append(" , ");
		sb.append(" APPOINTMENT_SCHEDULE_ID ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" , ");
		sb.append(" CREATED_DATE ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE ");
		sb.append(" ) VALUES ( ");
		sb.append(reviewid);
		sb.append(" , ");
		sb.append(statusid);
		sb.append(" , ");
		sb.append(" ").append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(comment)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(scheduleid);
		sb.append(" , ");
		sb.append(createdby);
		sb.append(" , ");
		sb.append(createdby);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" ").append(updateddate.sqlDatetime());
		sb.append(" , ");
		sb.append(" ").append(updateddate.sqlDatetime());
		sb.append(" ) ");
		return sb.toString();
	}

	
	/**
	 * @deprecated
	 * @param reviewid
	 * @param statusid
	 * @param userid
	 * @param updateddate
	 * @return
	 */
	public static String getAction(int reviewid, int statusid, int userid, Timekeeper updateddate) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.* ");
		sb.append(" FROM ");
		sb.append(" REVIEW_ACTION AS A ");
		sb.append(" WHERE ");
		sb.append(" A.REVIEW_ID = ").append(reviewid);
		sb.append(" AND ");
		sb.append(" A.UPDATED_BY = ").append(userid);
		sb.append(" AND ");
		sb.append(" A.UPDATED_DATE = ").append(updateddate.sqlDatetime());
		return sb.toString();
	}

	public static String addActionRef(String type, int typeid, int actionid, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String fieldidref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_REVIEW ( ");
		sb.append(" ").append(fieldidref).append(" ");
		sb.append(" , ");
		sb.append(" REVIEW_ACTION_ID ");
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
		sb.append(actionid);
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
	
	public static String createCombo(int reviewgroupid, String title, String start, String type, Timekeeper createddate, int userid, String ip) {

		Timekeeper s = new Timekeeper();
		if (Operator.hasValue(start)) {
			s.setDate(start);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO COMBOREVIEW ( ");
		sb.append(" REVIEW_GROUP_ID ");
		sb.append(" , ");
		sb.append(" TITLE ");
		sb.append(" , ");
		sb.append(" START_DATE ");
		sb.append(" , ");
		sb.append(" TYPE ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_DATE ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) Output Inserted.ID VALUES ( ");
		sb.append(reviewgroupid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(title)).append("' ");
		sb.append(" , ");
		sb.append(s.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(type)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(createddate.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(createddate.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getCombo(int rgroupid, String type, int userid, Timekeeper updateddate) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM COMBOREVIEW WHERE ");
		sb.append(" REVIEW_GROUP_ID = ").append(rgroupid);
		sb.append(" AND ");
		sb.append(" TYPE = '").append(Operator.sqlEscape(type)).append("' ");
		sb.append(" AND ");
		sb.append(" UPDATED_DATE = ").append(updateddate.sqlDatetime());
		sb.append(" AND ");
		sb.append(" UPDATED_BY = ").append(userid);
		return sb.toString();
	}

	public static String refCombo(String type, int typeid, int comboid, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_COMBOREVIEW ( ");
		sb.append(idref);
		sb.append(" , ");
		sb.append(" COMBOREVIEW_ID ");
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
		sb.append(comboid);
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

	public static String refAttachment(int actionid, int attachid, int userid, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_COMBOREVIEW_ATTACHMENTS ( ");
		sb.append(" REF_COMBOREVIEW_ACTION_ID, ATTACHMENT_ID, CREATED_BY, CREATED_IP, UPDATED_BY, UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(actionid);
		sb.append(" , ");
		sb.append(attachid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String addComboReview(int comboid, int reviewid, int userid, String ip, Timekeeper createddate) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_COMBOREVIEW_REVIEW ( ");
		sb.append(" COMBOREVIEW_ID ");
		sb.append(" , ");
		sb.append(" REVIEW_ID ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" , ");
		sb.append(" CREATED_DATE ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE ");
		sb.append(" ) VALUES ( ");
		sb.append(comboid);
		sb.append(" , ");
		sb.append(reviewid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(createddate.sqlDatetime());
		sb.append(" , ");
		sb.append(createddate.sqlDatetime());
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getReview(int comboid, int reviewid, int userid, Timekeeper now) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM REF_COMBOREVIEW_REVIEW WHERE ");
		sb.append(" COMBOREVIEW_ID = ").append(comboid);
		sb.append(" AND ");
		sb.append(" REVIEW_ID = ").append(reviewid);
		sb.append(" AND ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" AND ");
		sb.append(" UPDATED_DATE = ").append(now.sqlDatetime());
		return sb.toString();
	}

	public static String getComboreview(int reviewrefid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" C.* ");
		sb.append(" FROM ");
		sb.append(" COMBOREVIEW AS C ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS R ON C.ID = R.COMBOREVIEW_ID ");
		sb.append(" WHERE R.ID = ").append(reviewrefid);
		return sb.toString();
	}

	public static String addComboAction(int reviewrefid, int statusid, String comments, int previousid, int userid, String ip, Timekeeper createddate) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_COMBOREVIEW_ACTION ( ");
		sb.append(" REF_COMBOREVIEW_REVIEW_ID ");
		sb.append(" , ");
		sb.append(" LKUP_REVIEW_STATUS_ID ");
		sb.append(" , ");
		sb.append(" DATE ");
		sb.append(" , ");
		sb.append(" REVIEW_COMMENTS ");
		sb.append(" , ");
		sb.append(" PREVIOUS_ID ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" , ");
		sb.append(" CREATED_DATE ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE ");
		sb.append(" ) OUTPUT Inserted.* VALUES ( ");
		sb.append(reviewrefid);
		sb.append(" , ");
		if (statusid < 1) {
			sb.append(" ( ");
			sb.append(" SELECT ");
			sb.append(" TOP 1 S.ID ");
			sb.append(" FROM ");
			sb.append(" REF_COMBOREVIEW_REVIEW AS CR ");
			sb.append(" JOIN REVIEW AS R ON CR.REVIEW_ID = R.ID AND CR.ID = ").append(reviewrefid);
			sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON R.LKUP_REVIEW_TYPE_ID = S.LKUP_REVIEW_TYPE_ID AND S.ACTIVE = 'Y' ");
			sb.append(" ORDER BY ");
			sb.append(" S.DEFLT DESC, S.STATUS ");
			sb.append(" ) ");
		}
		else {
			sb.append(statusid);
		}
		sb.append(" , ");
		sb.append(createddate.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(comments)).append("' ");
		sb.append(" , ");
		sb.append(previousid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(createddate.sqlDatetime());
		sb.append(" , ");
		sb.append(createddate.sqlDatetime());
		sb.append(" ) ");
		return sb.toString();
	}

	public static String addComboAction(int comboid, int reviewid, int statusid, String comments, int userid, String ip, Timekeeper createddate) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_COMBOREVIEW_ACTION ( ");
		sb.append(" REF_COMBOREVIEW_REVIEW_ID ");
		sb.append(" , ");
		sb.append(" LKUP_REVIEW_STATUS_ID ");
		sb.append(" , ");
		sb.append(" DATE ");
		sb.append(" , ");
		sb.append(" REVIEW_COMMENTS ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" , ");
		sb.append(" CREATED_DATE ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE ");
		sb.append(" ) VALUES ( ");
		sb.append(" ( SELECT TOP 1 ID FROM REF_COMBOREVIEW_REVIEW WHERE COMBOREVIEW_ID = ").append(comboid).append(" AND REVIEW_ID = ").append(reviewid).append(" AND ACTIVE = 'Y' AND UPDATED_DATE = ").append(createddate.sqlDatetime()).append(" ) ");
		sb.append(" , ");
		sb.append(statusid);
		sb.append(" , ");
		sb.append(createddate.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(comments)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(createddate.sqlDatetime());
		sb.append(" , ");
		sb.append(createddate.sqlDatetime());
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getComboAction(int revrefid, int statusid, int userid, Timekeeper createddate) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.* ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_ACTION AS A ");
		sb.append(" WHERE ");
		sb.append(" A.REF_COMBOREVIEW_REVIEW_ID = ").append(revrefid);
		sb.append(" AND ");
		sb.append(" A.LKUP_REVIEW_STATUS_ID = ").append(statusid);
		sb.append(" AND ");
		sb.append(" A.CREATED_BY = ").append(userid);
		sb.append(" AND ");
		sb.append(" A.CREATED_DATE = ").append(createddate.sqlDatetime());
		return sb.toString();
	}

	public static String getComboAction(int comboid, int reviewid, int statusid, int userid, Timekeeper createddate) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.* ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_ACTION AS A ");
		sb.append(" WHERE ");
		sb.append(" A.REF_COMBOREVIEW_REVIEW_ID = ( SELECT TOP 1 ID FROM REF_COMBOREVIEW_REVIEW WHERE COMBOREVIEW_ID = ").append(comboid).append(" AND REVIEW_ID = ").append(reviewid).append(" AND ACTIVE = 'Y' AND UPDATED_DATE = ").append(createddate.sqlDatetime()).append(" ) ");
		sb.append(" AND ");
		sb.append(" A.LKUP_REVIEW_STATUS_ID = ").append(statusid);
		sb.append(" AND ");
		sb.append(" A.CREATED_BY = ").append(userid);
		sb.append(" AND ");
		sb.append(" A.CREATED_DATE = ").append(createddate.sqlDatetime());
		return sb.toString();
	}

	public static String getComboAction(int actionid) {
		if (actionid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.REVIEW_COMMENTS ");
		sb.append(" , ");
		sb.append(" R.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" , ");
		sb.append("  CAST(LSO.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(LS.PRE_DIR, '') + ' ' + LS.STR_NAME + ' ' + LS.STR_TYPE + ' ' + COALESCE(LSO.UNIT, '') AS ADDRESS ");
		sb.append(" , ");
		sb.append(" AC.ACT_NBR ");
		sb.append(" , ");
		sb.append(" CONVERT(VARCHAR(10),APS.START_DATE,101) AS START_DATE ");
		sb.append(" , ");
		sb.append(" CONVERT(VARCHAR(8),APS.START_DATE,114)+' - '+ CONVERT(VARCHAR(8),APS.END_DATE,114) AS INSPECTION_TIME ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_ACTION AS A ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON A.LKUP_REVIEW_STATUS_ID = S.ID ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS CR ON A.REF_COMBOREVIEW_REVIEW_ID = CR.ID AND A.ID = ").append(actionid);
		sb.append(" JOIN REVIEW AS R ON CR.REVIEW_ID = R.ID ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append(" 	REF_ACT_COMBOREVIEW AS RA ");
		sb.append(" 	JOIN ACTIVITY AS AC ON RA.ACTIVITY_ID = AC.ID ");
		sb.append(" 	LEFT OUTER JOIN REF_LSO_PROJECT AS RLP ON AC.PROJECT_ID = RLP.PROJECT_ID AND RLP.ACTIVE='Y' ");
		sb.append(" 	LEFT OUTER JOIN LSO ON RLP.LSO_ID = LSO.ID ");
		sb.append(" 	LEFT OUTER JOIN LSO_STREET AS LS ON LSO.LSO_STREET_ID = LS.ID ");
		sb.append(" ) ON RA.COMBOREVIEW_ID = CR.COMBOREVIEW_ID AND RA.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN APPOINTMENT_SCHEDULE APS on A.ID=APS.REF_COMBOREVIEW_ACTION_ID ");
		return sb.toString();
	}
	
	public static String getComboActionByReviewId(int refreviewid) {
		if (refreviewid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT TOP 1 ");
		sb.append(" A.REVIEW_COMMENTS ");
		sb.append(" , ");
		sb.append(" R.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" , ");
		sb.append(" AC.ACT_NBR ");
		sb.append(" , ");
		sb.append(" CONVERT(VARCHAR(10),APS.START_DATE,101) +' @ '+CONVERT(VARCHAR(8),APS.START_DATE,114) AS START_DATE ");
		sb.append(" , ");
		sb.append(" VA.ADDRESS ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_ACTION AS A ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON A.LKUP_REVIEW_STATUS_ID = S.ID ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS CR ON A.REF_COMBOREVIEW_REVIEW_ID = CR.ID AND A.REF_COMBOREVIEW_REVIEW_ID = ").append(refreviewid);
		sb.append(" JOIN REVIEW AS R ON CR.REVIEW_ID = R.ID ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append(" 	REF_ACT_COMBOREVIEW AS RA ");
		sb.append(" 	JOIN ACTIVITY AS AC ON RA.ACTIVITY_ID = AC.ID ");
		sb.append(" ) ON RA.COMBOREVIEW_ID = CR.COMBOREVIEW_ID AND RA.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN APPOINTMENT_SCHEDULE APS on A.ID=APS.REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" LEFT OUTER JOIN REF_LSO_PROJECT V on AC.PROJECT_ID = V.PROJECT_ID AND V.ACTIVE='Y'  ");
		sb.append(" LEFT OUTER JOIN V_CENTRAL_ADDRESS VA on V.LSO_ID = VA.LSO_ID ORDER BY A.ID DESC ");
		return sb.toString();
	}

	public static String expireComboAction(int refcomboreviewid, int excludecomboid, int userid, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_COMBOREVIEW_ACTION SET ");
		sb.append(" EXPIRED = 'Y' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" REF_COMBOREVIEW_REVIEW_ID = ").append(refcomboreviewid);
		sb.append(" AND ");
		sb.append(" ID <> ").append(excludecomboid);
		return sb.toString();
	}

	public static String addTeam(int comboid, int reviewid, int refactionid, int teamid, int userid, String ip, Timekeeper date) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_COMBOREVIEW_TEAM ( ");
		sb.append(" REF_COMBOREVIEW_REVIEW_ID ");
		sb.append(" , ");
		sb.append(" REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" , ");
		sb.append(" REF_TEAM_ID ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(" ( SELECT TOP 1 ID FROM REF_COMBOREVIEW_REVIEW WHERE COMBOREVIEW_ID = ").append(comboid).append(" AND REVIEW_ID = ").append(reviewid).append(" AND ACTIVE = 'Y' AND UPDATED_DATE = ").append(date.sqlDatetime()).append(" ) ");
		sb.append(" , ");
		sb.append(refactionid);
		sb.append(" , ");
		sb.append(teamid);
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

	public static String addTeam(int revrefid, int refactionid, int teamid, int userid, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_COMBOREVIEW_TEAM ( ");
		sb.append(" REF_COMBOREVIEW_REVIEW_ID ");
		sb.append(" , ");
		sb.append(" REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" , ");
		sb.append(" REF_TEAM_ID ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(revrefid);
		sb.append(" , ");
		sb.append(refactionid);
		sb.append(" , ");
		sb.append(teamid);
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

	public static String deleteTeam(int revrefid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_COMBOREVIEW_TEAM SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" WHERE ");
		sb.append(" REF_COMBOREVIEW_REVIEW_ID = ").append(revrefid);
		return sb.toString();
	}

	public static String addNotify(int refactionid, String recipient, int usersid, String subject, String content, int creator, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_COMBOREVIEW_NOTIFY ( ");
		sb.append(" REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" , ");
		sb.append(" RECIPIENT ");
		sb.append(" , ");
		sb.append(" USERS_ID ");
		sb.append(" , ");
		sb.append(" SUBJECT ");
		sb.append(" , ");
		sb.append(" CONTENT ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(refactionid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(recipient)).append("' ");
		sb.append(" , ");
		sb.append(usersid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(subject)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(content)).append("' ");
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

	public static String getActionStatus(int actionrefid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" S.* ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_ACTION AS A ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON A.LKUP_REVIEW_STATUS_ID = S.ID ");
		sb.append(" WHERE A.ID = ").append(actionrefid);
		return sb.toString();
	}

	public static String getCombo(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append("   C.* ");
		sb.append("   , ");
		sb.append("   G.GROUP_NAME ");
		sb.append(" FROM ");
		sb.append("   COMBOREVIEW AS C ");
		sb.append("   JOIN REVIEW_GROUP AS G ON C.REVIEW_GROUP_ID = G.ID ");
		sb.append(" WHERE ");
		sb.append("   C.ID = ").append(id);
		return sb.toString();
	}

	public static String getRefCombo(int reviewrefid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append("   C.* ");
		sb.append("   , ");
		sb.append("   G.GROUP_NAME ");
		sb.append(" FROM ");
		sb.append("   COMBOREVIEW AS C ");
		sb.append("   JOIN REVIEW_GROUP AS G ON C.REVIEW_GROUP_ID = G.ID ");
		sb.append("   JOIN REF_COMBOREVIEW_REVIEW AS R ON C.ID = R.COMBOREVIEW_ID ");
		sb.append(" WHERE ");
		sb.append("   R.ID = ").append(reviewrefid);
		return sb.toString();
	}

	public static String getComboReviews(int comboid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" CR.* ");
		sb.append(" , ");
		sb.append(" R.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" C.START_DATE ");
		sb.append(" , ");
		sb.append(" R.DAYS_TILL_DUE ");
		sb.append(" , ");
		sb.append(" R.AVAILABILITY_ID ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_REVIEW AS CR ");
		sb.append(" JOIN REVIEW AS R ON CR.REVIEW_ID = R.ID AND CR.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND CR.COMBOREVIEW_ID = ").append(comboid);
		sb.append(" JOIN COMBOREVIEW AS C ON CR.COMBOREVIEW_ID = C.ID ");
		sb.append(" ORDER BY R.ORDR, R.NAME ");
		return sb.toString();
	}

	public static String getRefComboReviews(int revrefid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" CR.* ");
		sb.append(" , ");
		sb.append(" C.START_DATE ");
		sb.append(" , ");
		sb.append(" R.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" R.DAYS_TILL_DUE ");
		sb.append(" , ");
		sb.append(" R.AVAILABILITY_ID ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_REVIEW AS CR ");
		sb.append(" JOIN REVIEW AS R ON CR.REVIEW_ID = R.ID AND CR.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND CR.ID = ").append(revrefid);
		sb.append(" JOIN COMBOREVIEW AS C ON CR.COMBOREVIEW_ID = C.ID ");
		sb.append(" ORDER BY R.ORDR, R.NAME ");
		return sb.toString();
	}

	public static String getStatus(int lkupstatusid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM LKUP_REVIEW_STATUS WHERE ID = ").append(lkupstatusid);
		return sb.toString();
	}

	public static String getInspectionCancelStatus(int reviewid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" S.* ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN S.SCHEDULE_INSPECTION_CANCEL = 'Y' THEN 100 ");
		sb.append("   WHEN S.SCHEDULE_CANCEL = 'Y' THEN 10 ");
		sb.append(" ELSE 0 END AS SCORE ");
		sb.append(" FROM ");
		sb.append(" REVIEW AS R ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON R.LKUP_REVIEW_TYPE_ID = S.LKUP_REVIEW_TYPE_ID AND S.ACTIVE = 'Y' ");
		sb.append(" WHERE R.ID = ").append(reviewid);
		sb.append(" ) ");
		sb.append(" SELECT TOP 1 * FROM Q WHERE SCORE > 0 ORDER BY SCORE DESC ");
		return sb.toString();
	}

	public static String getCancelStatus(int reviewid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" S.* ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN S.SCHEDULE_CANCEL = 'Y' THEN 10 ");
		sb.append(" ELSE 0 END AS SCORE ");
		sb.append(" FROM ");
		sb.append(" REVIEW AS R ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON R.LKUP_REVIEW_TYPE_ID = S.LKUP_REVIEW_ID AND S.ACTIVE = 'Y' ");
		sb.append(" WHERE R.ID = ").append(reviewid);
		sb.append(" ) ");
		sb.append(" SELECT TOP 1 * FROM Q WHERE SCORE > 0 ORDER BY SCORE DESC ");
		return sb.toString();
	}

	public static String getReviewActions(int comboid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	A.ID AS REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" 	, ");
		sb.append(" 	COUNT(DISTINCT N.ID) AS NOTIFICATIONS ");
		sb.append(" FROM ");
		sb.append(" 	REF_COMBOREVIEW_NOTIFY AS N ");
		sb.append(" 	JOIN REF_COMBOREVIEW_ACTION AS A ON N.REF_COMBOREVIEW_ACTION_ID = A.ID AND N.ACTIVE = 'Y' ");
		sb.append(" 	JOIN REF_COMBOREVIEW_REVIEW AS R ON A.REF_COMBOREVIEW_REVIEW_ID = R.ID AND R.ACTIVE = 'Y' AND R.COMBOREVIEW_ID = ").append(comboid);
		sb.append(" GROUP BY ");
		sb.append(" 	A.ID ");
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" RCA.* ");
		sb.append(" , ");
		sb.append(" U.USERNAME ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" , ");
		sb.append(" S.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" S.APPROVED ");
		sb.append(" , ");
		sb.append(" S.UNAPPROVED ");
		sb.append(" , ");
		sb.append(" S.FINAL ");
		sb.append(" , ");
		sb.append(" S.SCHEDULE ");
		sb.append(" , ");
		sb.append(" S.SCHEDULE_CANCEL ");
		sb.append(" , ");
		sb.append(" S.SCHEDULE_INSPECTION ");
		sb.append(" , ");
		sb.append(" S.SCHEDULE_INSPECTION_CANCEL ");
		sb.append(" , ");
		sb.append(" Q.NOTIFICATIONS ");
		sb.append(" , ");
		sb.append(" A.ID AS ATTACHMENT_ID ");
		sb.append(" , ");
		sb.append(" A.TITLE AS ATTACHMENT_TITLE ");
		sb.append(" , ");
		sb.append(" A.PATH AS ATTACHMENT ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION AS ATTACHMENT_DESCRIPTION ");
		sb.append(" , ");
		sb.append(" AT.ID AS ATTACHMENT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" AT.TYPE AS ATTACHMENT_TYPE ");
		sb.append(" , ");
		sb.append(" APPT.ID AS APPOINTMENT_ID ");
		sb.append(" , ");
		sb.append(" APPT.LKUP_APPOINTMENT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" APPT.SUBJECT AS APPTSUBJECT ");
		sb.append(" , ");
		sb.append(" APPT.TYPE AS CSTYPE ");
		sb.append(" , ");
		sb.append(" APPTS.ID AS SCHEDULE_ID ");
		sb.append(" , ");
		sb.append(" APPTS.START_DATE ");
		sb.append(" , ");
		sb.append(" APPTS.END_DATE ");
		sb.append(" , ");
		sb.append(" APPTS.PARENT_ID ");
		sb.append(" , ");
		sb.append(" APPTS.SOURCE AS APPTSOURCE ");
		sb.append(" , ");
		sb.append(" APPTSS.SCHEDULED AS APPTSCHEDULED ");
		sb.append(" , ");
		sb.append(" APPTSS.COMPLETE AS APPTCOMPLETE");
		sb.append(" , ");
		sb.append(" APPTSS.DEFAULT_COMPLETE ");
		sb.append(" , ");
		sb.append(" APPTSS.DEFAULT_BEGIN ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_ACTION AS RCA ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RCR ON RCR.ID = RCA.REF_COMBOREVIEW_REVIEW_ID AND RCR.ACTIVE = 'Y' AND RCA.EXPIRED = 'N' AND RCA.ACTIVE = 'Y' AND RCR.COMBOREVIEW_ID = ").append(comboid);
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON RCA.LKUP_REVIEW_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON RCA.CREATED_BY = U.ID ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("     REF_COMBOREVIEW_ATTACHMENTS AS RATTACH ");
		sb.append("     JOIN ATTACHMENTS AS A ON RATTACH.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND RATTACH.ACTIVE = 'Y' ");
		sb.append("     JOIN LKUP_ATTACHMENTS_TYPE AS AT ON A.LKUP_ATTACHMENTS_TYPE_ID = AT.ID ");
		sb.append(" ) ON RATTACH.REF_COMBOREVIEW_ACTION_ID = RCA.ID ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("     APPOINTMENT AS APPT ");
		sb.append("     JOIN APPOINTMENT_SCHEDULE AS APPTS ON APPT.ID = APPTS.APPOINTMENT_ID AND APPT.ACTIVE = 'Y' AND APPTS.ACTIVE = 'Y' ");
//		sb.append("     LEFT OUTER JOIN LKUP_APPOINTMENT_TYPE AS APPTT ON APPT.LKUP_APPOINTMENT_TYPE_ID = APPTT.ID ");
		sb.append("     JOIN LKUP_APPOINTMENT_STATUS AS APPTSS ON APPTS.LKUP_APPOINTMENT_STATUS_ID = APPTSS.ID AND APPTSS.SCHEDULED = 'Y' ");
		sb.append(" ) ON APPTS.REF_COMBOREVIEW_ACTION_ID = RCA.ID ");
		sb.append(" LEFT OUTER JOIN Q ON RCA.ID = Q.REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" ORDER BY RCA.EXPIRED, RCA.DATE DESC, RCA.CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String getRefReviewActions(int revrefid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	A.ID AS REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" 	, ");
		sb.append(" 	COUNT(DISTINCT N.ID) AS NOTIFICATIONS ");
		sb.append(" FROM ");
		sb.append(" 	REF_COMBOREVIEW_NOTIFY AS N ");
		sb.append(" 	JOIN REF_COMBOREVIEW_ACTION AS A ON N.REF_COMBOREVIEW_ACTION_ID = A.ID AND N.ACTIVE = 'Y' AND A.REF_COMBOREVIEW_REVIEW_ID = ").append(revrefid);
		sb.append(" GROUP BY ");
		sb.append(" 	A.ID ");
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" RCA.* ");
		sb.append(" , ");
		sb.append(" U.USERNAME ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" , ");
		sb.append(" S.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" S.APPROVED ");
		sb.append(" , ");
		sb.append(" S.UNAPPROVED ");
		sb.append(" , ");
		sb.append(" S.SCHEDULE_INSPECTION ");
		sb.append(" , ");
		sb.append(" S.FINAL ");
		sb.append(" , ");
		sb.append(" S.SCHEDULE ");
		sb.append(" , ");
		sb.append(" S.SCHEDULE_CANCEL ");
		sb.append(" , ");
		sb.append(" S.SCHEDULE_INSPECTION ");
		sb.append(" , ");
		sb.append(" S.SCHEDULE_INSPECTION_CANCEL ");
		sb.append(" , ");
		sb.append(" Q.NOTIFICATIONS ");
		sb.append(" , ");
		sb.append(" A.ID AS ATTACHMENT_ID ");
		sb.append(" , ");
		sb.append(" A.TITLE AS ATTACHMENT_TITLE ");
		sb.append(" , ");
		sb.append(" A.PATH AS ATTACHMENT ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION AS ATTACHMENT_DESCRIPTION ");
		sb.append(" , ");
		sb.append(" AT.ID AS ATTACHMENT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" AT.TYPE AS ATTACHMENT_TYPE ");
		sb.append(" , ");
		sb.append(" APPT.ID AS APPOINTMENT_ID ");
		sb.append(" , ");
		sb.append(" APPT.LKUP_APPOINTMENT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" APPT.SUBJECT AS APPTSUBJECT ");
		sb.append(" , ");
		sb.append(" APPT.TYPE AS CSTYPE ");
		sb.append(" , ");
		sb.append(" APPTS.ID AS SCHEDULE_ID ");
		sb.append(" , ");
		sb.append(" APPTS.START_DATE ");
		sb.append(" , ");
		sb.append(" APPTS.END_DATE ");
		sb.append(" , ");
		sb.append(" APPTS.PARENT_ID ");
		sb.append(" , ");
		sb.append(" APPTS.SOURCE AS APPTSOURCE ");
		sb.append(" , ");
		sb.append(" APPTSS.SCHEDULED AS APPTSCHEDULED ");
		sb.append(" , ");
		sb.append(" APPTSS.COMPLETE AS APPTCOMPLETE");
		sb.append(" , ");
		sb.append(" APPTSS.DEFAULT_COMPLETE ");
		sb.append(" , ");
		sb.append(" APPTSS.DEFAULT_BEGIN ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_ACTION AS RCA ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RCR ON RCR.ID = RCA.REF_COMBOREVIEW_REVIEW_ID AND RCR.ACTIVE = 'Y' AND RCA.ACTIVE = 'Y' AND RCR.ID = ").append(revrefid);
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON RCA.LKUP_REVIEW_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON RCA.CREATED_BY = U.ID ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("     REF_COMBOREVIEW_ATTACHMENTS AS RATTACH ");
		sb.append("     JOIN ATTACHMENTS AS A ON RATTACH.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND RATTACH.ACTIVE = 'Y' ");
		sb.append("     LEFT OUTER JOIN LKUP_ATTACHMENTS_TYPE AS AT ON A.LKUP_ATTACHMENTS_TYPE_ID = AT.ID ");
		sb.append(" ) ON RATTACH.REF_COMBOREVIEW_ACTION_ID = RCA.ID ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("     APPOINTMENT AS APPT ");
		sb.append("     JOIN APPOINTMENT_SCHEDULE AS APPTS ON APPT.ID = APPTS.APPOINTMENT_ID AND APPT.ACTIVE = 'Y' AND APPTS.ACTIVE = 'Y' ");
		sb.append("     LEFT OUTER JOIN LKUP_APPOINTMENT_TYPE AS APPTT ON APPT.LKUP_APPOINTMENT_TYPE_ID = APPTT.ID ");
		sb.append("     LEFT OUTER JOIN LKUP_APPOINTMENT_STATUS AS APPTSS ON APPTS.LKUP_APPOINTMENT_STATUS_ID = APPTSS.ID ");
		sb.append(" ) ON APPTS.REF_COMBOREVIEW_ACTION_ID = RCA.ID ");
		sb.append(" LEFT OUTER JOIN Q ON RCA.ID = Q.REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" ORDER BY RCA.EXPIRED, RCA.DATE DESC, RCA.CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String getReviewTeam(int comboid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" RCT.* ");
		sb.append(" , ");
		sb.append(" U.USERNAME ");
		sb.append(" , ");
		sb.append(" U.EMAIL ");
		sb.append(" , ");
		sb.append(" U.FIRST_NAME ");
		sb.append(" , ");
		sb.append(" U.LAST_NAME ");
		sb.append(" , ");
		sb.append(" TT.TYPE ");
		sb.append(" , ");
		sb.append(" TT.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" RCR.COMBOREVIEW_ID ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_TEAM AS RCT ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RCR ON RCR.ID = RCT.REF_COMBOREVIEW_REVIEW_ID AND RCR.ACTIVE = 'Y' AND RCR.COMBOREVIEW_ID = ").append(comboid);
		sb.append(" JOIN REF_TEAM AS T ON RCT.REF_TEAM_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_TEAM_TYPE AS TT ON T.LKUP_TEAM_TYPE_ID = TT.ID ");
		sb.append(" JOIN USERS AS U ON T.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getRefReviewTeam(int revrefid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" RCT.* ");
		sb.append(" , ");
		sb.append(" U.USERNAME ");
		sb.append(" , ");
		sb.append(" U.EMAIL ");
		sb.append(" , ");
		sb.append(" U.FIRST_NAME ");
		sb.append(" , ");
		sb.append(" U.LAST_NAME ");
		sb.append(" , ");
		sb.append(" TT.TYPE ");
		sb.append(" , ");
		sb.append(" TT.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" RCR.COMBOREVIEW_ID ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_TEAM AS RCT ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RCR ON RCR.ID = RCT.REF_COMBOREVIEW_REVIEW_ID AND RCR.ACTIVE = 'Y' AND RCR.ID = ").append(revrefid);
		sb.append(" JOIN REF_TEAM AS T ON RCT.REF_TEAM_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_TEAM_TYPE AS TT ON T.LKUP_TEAM_TYPE_ID = TT.ID ");
		sb.append(" JOIN USERS AS U ON T.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String comboreviewHistory(int comboid, int userid, String ip, Timekeeper date) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO COMBOREVIEW_HISTORY ( ");
		sb.append(" ID, REVIEW_GROUP_ID, TITLE, START_DATE, APPROVED, PARENT_ID, ACTIVE, TYPE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, CREATED_IP, UPDATED_IP ");
		sb.append(" ) ");
		sb.append(" SELECT ID, REVIEW_GROUP_ID, TITLE, START_DATE, APPROVED, PARENT_ID, ACTIVE, TYPE, CREATED_BY, CREATED_DATE, ").append(userid).append(", ").append(date.sqlDatetime()).append(", CREATED_IP, '").append(Operator.sqlEscape(ip)).append("' FROM COMBOREVIEW WHERE ID = ").append(comboid).append(" ");
		return sb.toString();
	}

	public static String updateComboreview(int comboid, String title, String start, int userid, String ip, Timekeeper date) {
		Timekeeper s = new Timekeeper();
		if (Operator.hasValue(start)) {
			s.setDate(start);
		}
		return updateComboreview(comboid, title, s, userid, ip, date);
	}

	public static String updateComboreview(int comboid, String title, Timekeeper start, int userid, String ip, Timekeeper date) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE COMBOREVIEW SET ");
		sb.append(" TITLE = '").append(Operator.sqlEscape(title)).append("' ");
		sb.append(" , ");
		sb.append(" START_DATE = ").append(start.sqlDatetime());
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = ").append(date.sqlDatetime());
		sb.append(" WHERE ");
		sb.append(" ID = ").append(comboid);
		return sb.toString();
	}

	public static String getTypeId(int comboid, String type) {
		String tableref = CsReflect.getTableRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" M.ID ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_COMBOREVIEW AS REF ");
		sb.append(" JOIN ").append(maintableref).append(" AS M ON REF.").append(idref).append(" = M.ID ");
		sb.append(" WHERE REF.COMBOREVIEW_ID = ").append(comboid);
		return sb.toString();
	}

	public static String getComboreviews(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();

		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT DISTINCT ");

		sb.append(" C.ID AS COMBOID ");
		sb.append(" , ");
		sb.append(" CR.ID AS REFREVIEWID ");
		sb.append(" , ");
		sb.append(" CA.ID AS REFACTID ");
		sb.append(" , ");
		sb.append(" CASE WHEN R.DAYS_TILL_DUE > 0 THEN C.START_DATE + R.DAYS_TILL_DUE ELSE CR.DUE_DATE END AS DUE_DATE ");
//		sb.append(" CR.DUE_DATE ");
		sb.append(" , ");
		sb.append(" R.ID AS REVIEWID ");
		sb.append(" , ");
		sb.append(" G.ID AS GROUPID ");
		sb.append(" , ");
		sb.append(" S.ID AS STATUSID ");
		sb.append(" , ");
		sb.append(" APPT.ID AS APPTID ");
		sb.append(" , ");
		sb.append(" SCHED.ID AS SCHEDID ");
		sb.append(" , ");
		sb.append(" C.START_DATE ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME AS REVIEWGROUP ");
		sb.append(" , ");
		sb.append(" R.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" R.DAYS_TILL_DUE ");
		sb.append(" , ");
		sb.append(" C.TITLE AS COMBOTITLE ");
		sb.append(" , ");
		sb.append(" C.EXPEDITED ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" , ");
		sb.append(" S.FINAL ");
		sb.append(" , ");
		sb.append(" S.UNAPPROVED ");
		sb.append(" , ");
		sb.append(" S.APPROVED ");
		sb.append(" , ");
		sb.append(" CA.DATE ");
		sb.append(" , ");
		sb.append(" SCHED.START_DATE AS APPT_START_DATE ");
		sb.append(" , ");
		sb.append(" SCHED.END_DATE AS APPT_END_DATE ");
		sb.append(" , ");
		sb.append(" A.ID AS ATTACHMENT_ID ");
		sb.append(" , ");
		sb.append(" A.TITLE AS ATTACHMENT_TITLE ");
		sb.append(" , ");
		sb.append(" A.PATH AS ATTACHMENT ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION AS ATTACHMENT_DESCRIPTION ");
		sb.append(" , ");
		sb.append(" AT.ID AS ATTACHMENT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" AT.TYPE AS ATTACHMENT_TYPE ");
		sb.append(" , ");
		sb.append(" R.AVAILABILITY_ID ");
		sb.append(" , ");
		sb.append(" R.MAX_ACTIVE_APPOINTMENTS ");
		sb.append(" , ");
		sb.append(" G.ORDR AS GROUPORDER ");
		sb.append(" , ");
		sb.append(" R.ORDR AS REVIEWORDER ");

		sb.append(" FROM ");
		sb.append(" COMBOREVIEW AS C ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS CR ON C.ID = CR.COMBOREVIEW_ID AND CR.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_COMBOREVIEW_ACTION AS CA ON CR.ID = CA.REF_COMBOREVIEW_REVIEW_ID AND CA.ACTIVE = 'Y' AND CA.EXPIRED = 'N' ");
		sb.append(" JOIN REVIEW AS R ON CR.REVIEW_ID = R.ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN REVIEW_GROUP AS G ON R.REVIEW_GROUP_ID = G.ID AND G.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON CA.LKUP_REVIEW_STATUS_ID = S.ID ");
		sb.append(" JOIN REF_").append(tableref).append("_COMBOREVIEW AS RC ON RC.ACTIVE = 'Y' AND C.ACTIVE = 'Y' AND C.ID = RC.COMBOREVIEW_ID AND RC.").append(idref).append(" = ").append(typeid);
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   APPOINTMENT AS APPT ");
		sb.append("   JOIN APPOINTMENT_SCHEDULE AS SCHED ON APPT.ID = SCHED.APPOINTMENT_ID AND SCHED.ACTIVE = 'Y' ");
		sb.append(" ) ON SCHED.REF_COMBOREVIEW_ACTION_ID = CA.ID AND APPT.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("     REF_COMBOREVIEW_ATTACHMENTS AS RATTACH ");
		sb.append("     JOIN ATTACHMENTS AS A ON RATTACH.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND RATTACH.ACTIVE = 'Y' ");
		sb.append("     JOIN LKUP_ATTACHMENTS_TYPE AS AT ON A.LKUP_ATTACHMENTS_TYPE_ID = AT.ID ");
		sb.append(" ) ON RATTACH.REF_COMBOREVIEW_ACTION_ID = CA.ID ");
		sb.append(" ) ");
		sb.append(" SELECT ");

		sb.append(" Q.COMBOID ");
		sb.append(" , ");
		sb.append(" Q.REFREVIEWID ");
		sb.append(" , ");
		sb.append(" Q.REFACTID ");
		sb.append(" , ");
		sb.append(" R.ID AS REVIEWID ");
		sb.append(" , ");
		sb.append(" G.ID AS GROUPID ");
		sb.append(" , ");
		sb.append(" Q.STATUSID ");
		sb.append(" , ");
		sb.append(" Q.APPTID ");
		sb.append(" , ");
		sb.append(" Q.SCHEDID ");
		sb.append(" , ");
		sb.append(" Q.START_DATE ");
		sb.append(" , ");
		sb.append(" Q.DUE_DATE ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME AS REVIEWGROUP ");
		sb.append(" , ");
		sb.append(" R.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" Q.DAYS_TILL_DUE ");
		sb.append(" , ");
		sb.append(" Q.COMBOTITLE ");
		sb.append(" , ");
		sb.append(" Q.EXPEDITED ");
		sb.append(" , ");
		sb.append(" Q.FINAL ");
		sb.append(" , ");
		sb.append(" Q.UNAPPROVED ");
		sb.append(" , ");
		sb.append(" Q.APPROVED ");
		sb.append(" , ");
		sb.append(" Q.STATUS ");
		sb.append(" , ");
		sb.append(" Q.DATE ");
		sb.append(" , ");
		sb.append(" Q.APPT_START_DATE ");
		sb.append(" , ");
		sb.append(" Q.APPT_END_DATE ");
		sb.append(" , ");
		sb.append(" Q.ATTACHMENT_ID ");
		sb.append(" , ");
		sb.append(" Q.ATTACHMENT_TITLE ");
		sb.append(" , ");
		sb.append(" Q.ATTACHMENT ");
		sb.append(" , ");
		sb.append(" Q.ATTACHMENT_DESCRIPTION ");
		sb.append(" , ");
		sb.append(" Q.ATTACHMENT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" Q.ATTACHMENT_TYPE ");
		sb.append(" , ");
		sb.append(" Q.AVAILABILITY_ID ");
		sb.append(" , ");
		sb.append(" Q.MAX_ACTIVE_APPOINTMENTS ");
		sb.append(" , ");
		sb.append(" G.ORDR AS GROUPORDER ");
		sb.append(" , ");
		sb.append(" R.ORDR AS REVIEWORDER ");

		sb.append(" FROM ");
		sb.append("   ").append(maintableref).append(" AS M ");
		sb.append("   JOIN LKUP_").append(tableref).append("_TYPE AS MT ON M.LKUP_").append(tableref).append("_TYPE_ID = MT.ID AND M.ID = ").append(typeid);
		sb.append("   JOIN REF_").append(tableref).append("_REVIEW_GROUP AS REF ON REF.LKUP_").append(tableref).append("_TYPE_ID = MT.ID AND REF.ACTIVE = 'Y' ");
		sb.append("   JOIN REVIEW_GROUP AS G ON REF.REVIEW_GROUP_ID = G.ID AND G.ACTIVE = 'Y' ");
		sb.append("   LEFT OUTER JOIN ( ");
		sb.append("     REVIEW AS R ");
		sb.append("     JOIN Q ON R.REVIEW_GROUP_ID = Q.GROUPID AND R.ID = Q.REVIEWID ");
		sb.append("   ) ON G.ID = R.REVIEW_GROUP_ID AND R.ACTIVE = 'Y' ");

		sb.append(" ORDER BY G.ORDR ASC, G.GROUP_NAME, R.ORDR, R.NAME, Q.DATE DESC ");
		return sb.toString();
	}

	public static String getComboreviewAppointments(String reviewtype, String start, String end, int userid) {
		Timekeeper s = new Timekeeper();
		if (Operator.hasValue(start)) {
			s.setDate(start);
		}
		s.setHour(0);
		s.setMinute(0);
		s.setSecond(0);
		Timekeeper e = s.copy();
		if (Operator.hasValue(end)) {
			e.setDate(end);
		}
		e.setHour(23);
		e.setMinute(59);
		e.setSecond(59);

		StringBuilder sb  = new StringBuilder();

		sb.append(" SELECT DISTINCT ");

		sb.append(" C.ID AS COMBOID ");
		sb.append(" , ");
		sb.append(" C.TYPE AS REF ");
		sb.append(" , ");
		sb.append(" ACT.ACT_NBR ");
		sb.append(" , ");
		sb.append(" PROJ.PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" ACT.ID AS ACT_ID");
		sb.append(" , ");
		sb.append(" PROJ.ID AS PROJ_ID ");
		sb.append(" , ");
		sb.append(" CR.ID AS REFREVIEWID ");
		sb.append(" , ");
		sb.append(" CA.ID AS REFACTID ");
		sb.append(" , ");
		sb.append(" R.ID AS REVIEWID ");
		sb.append(" , ");
		sb.append(" G.ID AS GROUPID ");
		sb.append(" , ");
		sb.append(" S.ID AS STATUSID ");
		sb.append(" , ");
		sb.append(" APPT.ID AS APPTID ");
		sb.append(" , ");
		sb.append(" SCHED.ID AS SCHEDID ");
		sb.append(" , ");
		sb.append(" C.START_DATE ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME AS REVIEWGROUP ");
		sb.append(" , ");
		sb.append(" R.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" C.TITLE AS COMBOTITLE ");
		sb.append(" , ");
		sb.append(" C.EXPEDITED ");
		sb.append(" , ");
		sb.append(" COALESCE(NS.STATUS, S.STATUS) AS STATUS ");
		sb.append(" , ");
		sb.append(" COALESCE(NS.SCHEDULE, S.SCHEDULE) AS SCHEDULE ");
		sb.append(" , ");
		sb.append(" COALESCE(NS.FINAL, S.FINAL) AS FINAL ");
		sb.append(" , ");
		sb.append(" COALESCE(NS.UNAPPROVED, S.UNAPPROVED) AS UNAPPROVED ");
		sb.append(" , ");
		sb.append(" COALESCE(NS.APPROVED, S.APPROVED) AS APPROVED ");
		sb.append(" , ");
		sb.append(" CA.DATE ");
		sb.append(" , ");
		sb.append(" CA.EXPIRED ");
		sb.append(" , ");
		sb.append(" CA.PREVIOUS_ID ");
		sb.append(" , ");
		sb.append(" SCHED.START_DATE AS APPT_START_DATE ");
		sb.append(" , ");
		sb.append(" SCHED.END_DATE AS APPT_END_DATE ");
		sb.append(" , ");
		sb.append(" R.AVAILABILITY_ID ");
		sb.append(" , ");
		sb.append(" R.MAX_ACTIVE_APPOINTMENTS ");
		sb.append(" , ");
		sb.append(" G.ORDR AS GROUPORDER ");
		sb.append(" , ");
		sb.append(" R.ORDR AS REVIEWORDER ");
		sb.append(" , ");

		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_NO IS NOT NULL THEN CAST(LSO.STR_NO AS VARCHAR(10)) ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_NO IS NOT NULL AND ST.PRE_DIR IS NOT NULL AND ST.PRE_DIR <> '' THEN ' ' + ST.PRE_DIR ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_NO IS NOT NULL THEN ' ' + ST.STR_NAME ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_NO IS NOT NULL AND ST.STR_TYPE IS NOT NULL AND ST.STR_TYPE <> '' THEN ' ' + ST.STR_TYPE ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_NO IS NOT NULL AND ST.SUF_DIR IS NOT NULL AND ST.SUF_DIR <> '' THEN ' ' + ST.SUF_DIR ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_NO IS NOT NULL AND LSO.UNIT IS NOT NULL AND LSO.UNIT <> '' THEN ' ' + LSO.UNIT ");
		sb.append("         ELSE ''  ");
		sb.append("       END ");
		sb.append("     )) AS ADDRESS ");
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
		sb.append("       END ");
		sb.append("     )) AS INSPECTOR ");
		sb.append(" , ");
		sb.append(" U.LAST_NAME ");
		sb.append(" , ");
		sb.append(" U.MI ");
		sb.append(" , ");
		sb.append(" U.FIRST_NAME ");
		sb.append(" , ");
		sb.append(" U.ID AS USERID ");
		sb.append(" , ");
		sb.append(" U.USERNAME ");
		sb.append(" , ");
		sb.append(" TM.ID AS TEAMID ");

		sb.append(" FROM ");
		sb.append(" COMBOREVIEW AS C ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS CR ON C.ID = CR.COMBOREVIEW_ID AND CR.ACTIVE = 'Y' ");
		sb.append(" JOIN APPOINTMENT AS APPT ON CR.ID = APPT.REF_COMBOREVIEW_REVIEW_ID AND APPT.ACTIVE = 'Y' ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS SCHED ON APPT.ID = SCHED.APPOINTMENT_ID AND SCHED.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS APPTSTATUS ON SCHED.LKUP_APPOINTMENT_STATUS_ID = APPTSTATUS.ID AND APPTSTATUS.ACTIVE = 'Y'  AND APPTSTATUS.SCHEDULED = 'Y' ");

		sb.append(" JOIN REF_COMBOREVIEW_ACTION AS CA ON CA.ID = SCHED.REF_COMBOREVIEW_ACTION_ID AND CA.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON CA.LKUP_REVIEW_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   REF_COMBOREVIEW_ACTION AS NCA ");
		sb.append("   JOIN LKUP_REVIEW_STATUS AS NS ON NCA.LKUP_REVIEW_STATUS_ID = NS.ID ");
		sb.append(" ) ON CA.ID = NCA.PREVIOUS_ID AND NCA.ACTIVE = 'Y' ");

		sb.append(" JOIN REVIEW AS R ON CR.REVIEW_ID = R.ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_REVIEW_TYPE AS RT ON R.LKUP_REVIEW_TYPE_ID = RT.ID AND RT.ACTIVE = 'Y' ");
		if (Operator.hasValue(reviewtype)) {
			sb.append("   AND LOWER(RT.TYPE) = LOWER('").append(Operator.sqlEscape(reviewtype)).append("') ");
		}
		sb.append(" JOIN REVIEW_GROUP AS G ON R.REVIEW_GROUP_ID = G.ID AND G.ACTIVE = 'Y' ");

		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   ACTIVITY AS ACT ");
		sb.append("   JOIN REF_ACT_COMBOREVIEW AS RACT ON ACT.ID = RACT.ACTIVITY_ID AND RACT.ACTIVE = 'Y' AND ACT.ACTIVE = 'Y' ");
		sb.append("   JOIN LKUP_ACT_TYPE AS ACTT ON ACT.LKUP_ACT_TYPE_ID = ACTT.ID ");
		sb.append(" ) ON C.ID = RACT.COMBOREVIEW_ID ");

		sb.append(" LEFT OUTER JOIN REF_PROJECT_COMBOREVIEW AS RPROJ ON C.ID = RPROJ.COMBOREVIEW_ID AND RPROJ.ACTIVE = 'Y' ");

		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   PROJECT AS PROJ ");
		sb.append("   JOIN REF_LSO_PROJECT AS RLSO ON PROJ.ID = RLSO.PROJECT_ID AND RLSO.ACTIVE = 'Y' ");
		sb.append("   JOIN LSO ON RLSO.LSO_ID = LSO.ID AND LSO.ACTIVE = 'Y' ");
		sb.append("   JOIN LSO_STREET AS ST ON LSO.LSO_STREET_ID = ST.ID AND ST.ACTIVE = 'Y' ");
		sb.append(" ) ON C.ID = RPROJ.COMBOREVIEW_ID OR ACT.PROJECT_ID = PROJ.ID ");

		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   REF_APPOINTMENT_TEAM AS RTM ");
		sb.append("   JOIN REF_TEAM AS TM ON RTM.REF_TEAM_ID = TM.ID AND TM.ACTIVE = 'Y' AND RTM.ACTIVE = 'Y' ");
		sb.append("   JOIN USERS AS U ON TM.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
		sb.append(" ) ON APPT.ID = RTM.APPOINTMENT_ID ");

		sb.append(" WHERE ");
		sb.append(" ( ");
		sb.append("   ( ");
		sb.append("     SCHED.START_DATE >= ").append(s.sqlDatetime());
		sb.append("     AND ");
		sb.append("     SCHED.START_DATE <= ").append(e.sqlDatetime());
		sb.append("   ) ");
		sb.append("   OR ");
		sb.append("   ( ");
		sb.append("     SCHED.START_DATE <= ").append(s.sqlDatetime());
		sb.append("     AND ");
		sb.append("     S.SCHEDULE = 'Y' ");
		sb.append("     AND ");
		sb.append("     APPTSTATUS.COMPLETE = 'N' ");
		sb.append("   ) ");
		sb.append(" ) ");
		if (userid > 0) {
			sb.append(" AND U.ID = ").append(userid);
		}

		return sb.toString();
	}

	public static String getCurrentAction(int revrefid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" TOP 1 ");
		sb.append(" A.* ");
		sb.append(" , ");
		sb.append(" R.REVIEW_ID ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" , ");
		sb.append(" S.SCHEDULE ");
		sb.append(" , ");
		sb.append(" S.SCHEDULE_INSPECTION ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_ACTION AS A ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS R ON A.REF_COMBOREVIEW_REVIEW_ID = R.ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON A.LKUP_REVIEW_STATUS_ID = S.ID ");
		sb.append(" WHERE ");
		sb.append(" A.REF_COMBOREVIEW_REVIEW_ID = ").append(revrefid);
		sb.append(" AND ");
		sb.append(" A.ACTIVE = 'Y' ");
		sb.append(" ORDER BY ");
		sb.append(" A.EXPIRED ASC, A.DATE DESC ");
		return sb.toString();
	}

	public static String getRequired(int reviewgroupid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.* ");
		sb.append(" FROM ");
		sb.append(" REVIEW AS R ");
		sb.append(" WHERE ");
		sb.append(" R.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" R.REQUIRED = 'Y' ");
		sb.append(" AND ");
		sb.append(" R.REVIEW_GROUP_ID = ").append(reviewgroupid);
		return sb.toString();
	}

	public static String updateDue(int refreviewid, String due, int userid, String ip) {
		if (!Operator.hasValue(due)) {
			StringBuilder sb = new StringBuilder();
			sb.append(" UPDATE REF_COMBOREVIEW_REVIEW SET ");
			sb.append(" DUE_DATE = null ");
			sb.append(" , ");
			sb.append(" UPDATED_BY = ").append(userid);
			sb.append(" , ");
			sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
			sb.append(" WHERE ");
			sb.append(" ID = ").append(refreviewid);
			return sb.toString();
		}
		else {
			Timekeeper d = new Timekeeper();
			d.setDate(due);
			return updateDue(refreviewid, d, userid, ip);
		}
	}

	public static String updateDue(int refreviewid, Timekeeper due, int userid, String ip) {
		if (refreviewid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_COMBOREVIEW_REVIEW SET ");
		sb.append(" DUE_DATE = ").append(due.sqlDatetime());
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(refreviewid);
		return sb.toString();
	}

	public static String getRequiredGroups(String type, int typeid) {
		if (!Operator.hasValue(type) || typeid < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String maintable = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.* ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_REVIEW_GROUP AS R ");
		sb.append(" JOIN REVIEW_GROUP AS G ON R.REVIEW_GROUP_ID = G.ID AND G.ACTIVE = 'Y' AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN ").append(maintable).append(" AS M ON R.LKUP_").append(tableref).append("_TYPE_ID = M.LKUP_").append(tableref).append("_TYPE_ID AND M.ID = ").append(typeid);
		sb.append(" WHERE ");
		sb.append(" R.REQUIRED = 'Y' ");
		return sb.toString();
	}

	// Must be a comboreview related to an activity
	public static String getActivity(int comboid) {
		if (comboid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.* ");
		sb.append(" , ");
		sb.append(" S.INHERIT AS STATUS_INHERIT ");
		sb.append(" FROM ");
		sb.append(" REF_ACT_COMBOREVIEW AS R ");
		sb.append(" JOIN ACTIVITY AS A ON R.ACTIVITY_ID = A.ID AND R.COMBOREVIEW_ID = ").append(comboid);
		sb.append(" JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID ");
		return sb.toString();
	}

	public static String getNotifications(int revactid) {
		if (revactid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" N.ID ");
		sb.append(" , ");
		sb.append(" N.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" N.RECIPIENT AS TEXT ");
		sb.append(" , ");
		sb.append(" N.ID AS NOTIFY_ID ");
		sb.append(" , ");
		sb.append(" N.RECIPIENT ");
		sb.append(" , ");
		sb.append(" N.SUBJECT ");
		sb.append(" , ");
		sb.append(" U.USERNAME AS CREATED_BY ");
		sb.append(" , ");
		sb.append(" CONVERT(VARCHAR, N.CREATED_DATE, 107) AS DATE ");
		sb.append(" , ");
		sb.append(" CAST(N.CREATED_DATE AS TIME) AS TIME ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_NOTIFY AS N ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON N.CREATED_BY = U.ID ");
		sb.append(" WHERE ");
		sb.append(" N.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" N.REF_COMBOREVIEW_ACTION_ID = ").append(revactid);
		sb.append(" ORDER BY ");
		sb.append(" N.CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String getNotification(int notifyid) {
		if (notifyid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" N.ID ");
		sb.append(" , ");
		sb.append(" N.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" N.RECIPIENT AS TEXT ");
		sb.append(" , ");
		sb.append(" N.ID AS NOTIFY_ID ");
		sb.append(" , ");
		sb.append(" N.RECIPIENT ");
		sb.append(" , ");
		sb.append(" N.CONTENT ");
		sb.append(" , ");
		sb.append(" N.SUBJECT ");
		sb.append(" , ");
		sb.append(" U.USERNAME AS CREATED_BY ");
		sb.append(" , ");
		sb.append(" N.CREATED_DATE ");
		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_NOTIFY AS N ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON N.CREATED_BY = U.ID ");
		sb.append(" WHERE ");
		sb.append(" N.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" N.ID = ").append(notifyid);
		sb.append(" ORDER BY ");
		sb.append(" N.CREATED_DATE DESC ");
		return sb.toString();
	}
	
	
	public static String getNotifyEmails(int actionId,int reviewId){
		StringBuilder sb = new StringBuilder();
		sb.append(" select U.ID,U.EMAIL,U.USERNAME,U.FIRST_NAME +' '+U.LAST_NAME as NAME,U.NOTIFY from REF_COMBOREVIEW_ACTION RCA  ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE APS on RCA.ID = APS.REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" join REF_COMBOREVIEW_REVIEW RCR on RCA.REF_COMBOREVIEW_REVIEW_ID= RCR.ID ");
		sb.append(" JOIN REF_ACT_COMBOREVIEW RAC on RCR.COMBOREVIEW_ID= RAC.COMBOREVIEW_ID ");
		sb.append(" JOIN REF_ACT_USERS RAU ON RAC.ACTIVITY_ID = RAU.ACTIVITY_ID AND RAU.ACTIVE='Y' ");
		sb.append(" JOIN REF_USERS RU on RAU.REF_USERS_ID = RU.ID ");
		sb.append(" JOIN USERS U on RU.USERS_ID=U.ID ");
		sb.append(" WHERE ( RCA.ID=").append(actionId).append("  OR RCR.ID =").append(reviewId).append(" ) AND U.EMAIL is not null ");
		sb.append(" union  ");
		sb.append(" select U.ID,U.EMAIL,U.USERNAME,U.FIRST_NAME +' '+U.LAST_NAME as NAME,U.NOTIFY from REF_COMBOREVIEW_ACTION RCA  ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE APS on RCA.ID = APS.REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" join REF_COMBOREVIEW_REVIEW RCR on RCA.REF_COMBOREVIEW_REVIEW_ID= RCR.ID ");
		sb.append(" JOIN REF_ACT_COMBOREVIEW RAC on RCR.COMBOREVIEW_ID= RAC.COMBOREVIEW_ID ");
		sb.append(" JOIN ACTIVITY A on RAC.ACTIVITY_ID= A.ID ");
		sb.append(" JOIN REF_PROJECT_USERS RAU ON A.PROJECT_ID = RAU.PROJECT_ID  AND RAU.ACTIVE='Y'  ");
		sb.append(" JOIN REF_USERS RU on RAU.REF_USERS_ID = RU.ID ");
		sb.append(" JOIN USERS U on RU.USERS_ID=U.ID ");
		sb.append(" WHERE ( RCA.ID=").append(actionId).append("  OR RCR.ID =").append(reviewId).append(" )  AND U.EMAIL is not null ");
		
		return sb.toString();
	}
	public static String getCycleDetails(int typeId) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append("   C.ID AS COMBOID,REVIEW_GROUP_ID,GROUP_NAME,TITLE,C.START_DATE,C.TYPE,ACTIVITY_ID ");
		sb.append(" FROM ");
		sb.append("  REF_ACT_COMBOREVIEW RAC JOIN COMBOREVIEW C ON RAC.COMBOREVIEW_ID=C.ID  ");
		sb.append("   JOIN REVIEW_GROUP AS G ON C.REVIEW_GROUP_ID = G.ID ");
		sb.append(" WHERE ");
		sb.append("  RAC.ACTIVE='Y' AND C.ACTIVE='Y' AND RAC.ACTIVITY_ID= ").append(typeId);
		return sb.toString();
	}
}
























