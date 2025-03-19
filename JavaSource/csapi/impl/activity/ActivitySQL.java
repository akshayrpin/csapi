package csapi.impl.activity;

import java.util.HashMap;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.divisions.DivisionsAgent;
import csapi.impl.entity.EntityAgent;
import csapi.impl.finance.FinanceSQL;
import csapi.utils.CsReflect;
import csapi.utils.CsTools;
import csshared.vo.DivisionsList;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.TypeInfo;

public class ActivitySQL {

	public static String types(int id) {
		StringBuilder sb = new StringBuilder();
		sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" DESCRIPTION AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		if (id > 0) {
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(id).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" LKUP_ACT_TYPE ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY TYPE ");
		return sb.toString();
	}

	public static String getActivity(String entity, int actid, String reference) {
		if (actid < 1 && !Operator.hasValue(reference)) { return ""; }
		String tableref = CsReflect.getTableRef(entity);
		String idref = CsReflect.getFieldIdRef(entity);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.*, R.").append(idref).append(" AS ENTITY_ID ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN PROJECT AS P ON A.PROJECT_ID = P.ID ");
		sb.append(" JOIN REF_").append(tableref).append("_PROJECT AS R ON R.PROJECT_ID = P.ID AND R.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		if (actid > 0) {
			sb.append(" A.ID = ").append(actid);
		}
		else {
			sb.append(" LOWER(A.ACT_NBR) = LOWER('").append(Operator.sqlEscape(reference)).append("') ");
		}
		return sb.toString();
	}

	public static String getStatus(int actid) {
		if (actid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" S.* ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID AND A.ID = ").append(actid);
		return sb.toString();
	}

	public static String browse(int projectid, String active, String expired, Token u) {
		String nproles = Operator.join(u.getNonpublicroles(), ",");
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH ");
		if (!u.isAdmin()) {
			if (u.isStaff() || Operator.hasValue(nproles)) {
				sb.append(" 	TANP AS ( ");
				if (Operator.hasValue(nproles)) {
					sb.append(" 		SELECT ");
					sb.append(" 			ATY.ID ");
					sb.append(" 			, ");
					sb.append(" 			AR.REQUIRE_PUBLIC ");
					sb.append(" 		FROM ");
					sb.append(" 			LKUP_ACT_TYPE AS ATY ");
					sb.append(" 			JOIN REF_ACT_TYPE_ROLES AS AR ON ATY.ID = AR.LKUP_ACT_TYPE_ID AND AR.ACTIVE = 'Y' ");
					sb.append(" 			JOIN LKUP_ROLES AS R ON AR.LKUP_ROLES_ID = R.ID AND R.ACTIVE = 'Y' AND AR.LKUP_ROLES_ID IN (").append(nproles).append(") ");
					if (u.isStaff()) {
						sb.append(" 		UNION ");
					}
				}
				if (u.isStaff()) {
					sb.append(" 		SELECT ");
					sb.append(" 			ATY.ID ");
					sb.append(" 			, ");
					sb.append(" 			'N' AS REQUIRE_PUBLIC ");
					sb.append(" 		FROM ");
					sb.append(" 			LKUP_ACT_TYPE AS ATY ");
					sb.append(" 		WHERE ");
					sb.append(" 			ATY.ID NOT IN (SELECT ST.LKUP_ACT_TYPE_ID AS ID FROM REF_ACT_TYPE_ROLES AS ST JOIN LKUP_ROLES AS SR ON ST.LKUP_ROLES_ID = SR.ID AND SR.EVERYONE = 'N' AND ST.ACTIVE = 'Y') ");
				}
				sb.append(" 	), ");
			}
			sb.append(" 	T AS ( ");
			if (u.isStaff() || Operator.hasValue(nproles)) {
				sb.append(" 		SELECT * FROM TANP ");
				sb.append(" 		UNION ");
			}
			sb.append(" 		SELECT ");
			sb.append(" 			ATY.ID ");
			sb.append(" 			, ");
			sb.append(" 			AR.REQUIRE_PUBLIC ");
			sb.append(" 		FROM ");
			sb.append(" 			LKUP_ACT_TYPE AS ATY ");
			sb.append(" 			JOIN REF_ACT_TYPE_ROLES AS AR ON ATY.ID = AR.LKUP_ACT_TYPE_ID AND AR.ACTIVE = 'Y' ");
			sb.append(" 			JOIN LKUP_ROLES AS R ON AR.LKUP_ROLES_ID = R.ID AND R.ACTIVE = 'Y' AND R.EVERYONE = 'Y' ");
			if (u.isStaff() || Operator.hasValue(nproles)) {
				sb.append(" 			AND ATY.ID NOT IN (SELECT ID FROM TANP) ");
			}
			sb.append(" 	), ");

		}
		sb.append("   Q AS ( ");
		sb.append("   SELECT ");
		sb.append("     AH.ACTIVITY_ID ");
		sb.append("     , ");
		sb.append("     HT.TYPE AS HOLD ");
		sb.append("     , ");
		sb.append("     COUNT(AH.ID) as NUM_HOLDS  "); 
		sb.append("   FROM ");
		sb.append("     REF_ACT_HOLDS AS AH ");
		sb.append("     JOIN ACTIVITY AS A ON AH.ACTIVITY_ID = A.ID AND A.ACTIVE = 'Y' AND AH.ACTIVE = 'Y' AND AH.NEW_ID < 1 "); 
		sb.append("     JOIN LKUP_HOLDS_TYPE AS HT ON HT.ID = AH.LKUP_HOLDS_TYPE_ID AND HT.ACTIVE = 'Y' AND HT.TYPE IN ('H','S') ");
		sb.append("     JOIN LKUP_HOLDS_STATUS AS HS ON HS.ID = AH.LKUP_HOLDS_STATUS_ID AND HS.ACTIVE = 'Y' AND HS.STATUS = 'A' ");
		sb.append("   WHERE "); 
		sb.append("     A.PROJECT_ID = ").append(projectid).append(" ");
		sb.append(" GROUP BY ");
		sb.append(" AH.ACTIVITY_ID ");
		sb.append(" , ");
		sb.append(" HT.TYPE ");
		sb.append(" ) ");

		sb.append(" , Q1 AS ( ");
		sb.append(" SELECT ");
		sb.append("   A.*, ");
		sb.append("   HH.NUM_HOLDS AS HARD_HOLDS, ");
		sb.append("   HS.NUM_HOLDS AS SOFT_HOLDS, ");
		sb.append("   CASE WHEN S.EXPIRED = 'Y' THEN 'Y' WHEN A.EXP_DATE IS NULL THEN 'N' WHEN CAST(A.EXP_DATE AS DATE) >= CAST(getDate() AS DATE) THEN 'N' ELSE 'Y' END AS ISEXPIRED, ");
		sb.append("   TYPE.DESCRIPTION as TITLE "); 
		sb.append(" FROM ");
		sb.append("   ACTIVITY AS A ");
		sb.append("   JOIN LKUP_ACT_TYPE TYPE ON A.LKUP_ACT_TYPE_ID = TYPE.ID ");
		sb.append("   JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID ");
		if (!u.isAdmin()) {
			sb.append(" 		JOIN T ON TYPE.ID = T.ID AND (S.ISPUBLIC = 'Y' OR COALESCE(T.REQUIRE_PUBLIC, 'N') = 'N') ");
		}
		sb.append("   LEFT OUTER JOIN Q AS HH ON HH.ACTIVITY_ID = A.ID AND HH.HOLD = 'H' ");
		sb.append("   LEFT OUTER JOIN Q AS HS ON HS.ACTIVITY_ID = A.ID AND HS.HOLD = 'S' ");
		sb.append(" WHERE ");
		sb.append("   A.PROJECT_ID = ").append(projectid).append(" ");
		sb.append("   AND ");
		sb.append("   A.ACTIVE = '").append(Operator.sqlEscape(active)).append("' ");
		sb.append(" ) ");

		sb.append(" SELECT DISTINCT * FROM Q1 ");
		if (!Operator.equalsIgnoreCase(expired, "Y")) {
			sb.append(" WHERE ISEXPIRED = 'N' ");
		}
		sb.append(" ORDER BY APPLIED_DATE DESC, CREATED_DATE DESC ");
		return sb.toString();
	}
	
	public static String summary(String type, int typeid, int id) {
		return details(type, typeid, id);
	}

	public static String list(String type, int typeid, int id) {
		return details(type, typeid, id);
	}

	public static String info(String type, int typeid, int id) {
		return details(type, typeid, id);
	}

	public static String details(String type, int typeid, int id) {
		StringBuilder sb = new StringBuilder();
//		sb.append(" WITH Q AS ");
//		sb.append(" ( "); 
//		sb.append("   SELECT "); 
//		sb.append("     TOP 1 "); 
//		sb.append("     ATS.ACTIVITY_ID, "); 
//		sb.append("     ATS.LKUP_ACT_STATUS_ID,"); 
//		sb.append("     LAS.STATUS "); 
//		sb.append("   FROM "); 
//		sb.append("     ").append(Table.ACTSTATUSTABLE).append(" ATS "); 
//		sb.append("     LEFT OUTER JOIN LKUP_ACT_STATUS LAS on ATS.LKUP_ACT_STATUS_ID = LAS.ID ");
//		sb.append("   WHERE "); 
//		sb.append("     ATS.ACTIVITY_ID = ").append(typeid).append(" "); 
//		sb.append("     AND "); 
//		sb.append("     ATS.ACTIVE = 'Y' "); 
//		sb.append("   ORDER BY "); 
//		sb.append("     DATE DESC");
//		sb.append(" ) ");

		sb.append(" WITH Q AS ( SELECT COUNT(DISTINCT ID) AS CONTENT_COUNT FROM CONTENT WHERE ACTIVE = 'Y' AND LOWER(TYPE) = 'activity') ");

		sb.append(" SELECT "); 
		sb.append("   A.*,"); 
		sb.append("   ATT.DESCRIPTION AS TITLE, "); 
		sb.append("   ATT.DESCRIPTION AS LKUP_ACT_TYPE_ID_TEXT, "); 
		sb.append("   CASE ");
		sb.append("     WHEN Q.CONTENT_COUNT IS NULL THEN '' ");
		sb.append("     WHEN Q.CONTENT_COUNT > 0 THEN 'activity' ");
		sb.append("   ELSE '' END AS CONTENT_TYPE, ");
		sb.append("   A.ACT_NBR AS SUBTITLE, "); 
		sb.append("   LAS.STATUS, "); 
		sb.append("   CU.USERNAME AS CREATED, "); 
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" FROM "); 
		sb.append("   ACTIVITY AS A ");
		sb.append("   JOIN  LKUP_ACT_TYPE AS ATT ON A.LKUP_ACT_TYPE_ID = ATT.ID ");
		sb.append("   JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID = LAS.ID ");
//		sb.append("   LEFT OUTER JOIN Q on A.ID = Q.ACTIVITY_ID ");
		sb.append("   LEFT OUTER JOIN USERS AS CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN USERS AS UP on A.UPDATED_BY = UP.ID "); 
		sb.append("   LEFT OUTER JOIN Q ON 1=1 ");

		sb.append(" WHERE "); 
		sb.append("   A.ID = ").append(typeid).append(" "); 
		sb.append("   AND "); 
		sb.append("   A.ACTIVE = 'Y' ");
		
		return sb.toString();
	}

	public static String history(String type, int typeid, int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT "); 
		sb.append("   A.*,"); 
		sb.append("   'Activity Status: ' + A.STATUS AS GROUP_LABEL, ");
		sb.append("   A.LOG_DATE AS GROUP_UPDATED, ");
		
		sb.append("   A.ACT_NBR AS SUBTITLE, "); 
		sb.append("   A.STATUS, "); 
		sb.append("   A.UPDATED "); 
		sb.append(" FROM "); 
		sb.append("   ACTIVITY_HISTORY AS A ");
		sb.append(" WHERE "); 
		sb.append("   A.ID = ").append(typeid).append(" "); 
		sb.append("   AND "); 
		sb.append("   A.ACTIVE = 'Y' ");
		sb.append(" ORDER BY ");
		sb.append("   A.LOG_DATE DESC ");
		return sb.toString();
	}

	public static String type(int typeid){
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ");
		sb.append(" ( "); 
		sb.append("   SELECT "); 
		sb.append("     TOP 1 "); 
		sb.append("     A.ID, "); 
		sb.append("     A.LKUP_ACT_STATUS_ID,"); 
		sb.append("     LAS.STATUS "); 
		sb.append("   FROM "); 
		sb.append("     ACTIVITY AS A "); 
		sb.append("     LEFT OUTER JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID = LAS.ID ");
		sb.append("   WHERE "); 
		sb.append("     A.ID = ").append(typeid).append(" "); 
		sb.append(" ) ");
		
		sb.append(" SELECT ");
		sb.append("   A.ID, ");
		sb.append("   T.DESCRIPTION AS TITLE, ");
		sb.append("   A.ACT_NBR AS SUBTITLE, ");
		sb.append("   Q.STATUS "); 
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN Q on A.ID = Q.ID ");
		sb.append(" WHERE ");
		sb.append("   A.ID = ").append(typeid).append(" ");
		return sb.toString();
	}
	
	public static String my(String username) {
		return my(username, "", "",  0, 250);
	}
	

	public static String my(String username,String search, String option,int start,int noofrecords) {
		if (!Operator.hasValue(username)) { return ""; }
		//String calc = FinanceSQL.calc("activity", myActivities(username));
		String calc = FinanceSQL.calc("activity", ActivityAgent.ids(username, search, option, start, noofrecords));
		StringBuilder sb = new StringBuilder();
		if (Operator.hasValue(calc)) {
			sb.append(calc);
			sb.append(" SELECT  ");
			sb.append(" 	A.ID, ");
			sb.append(" 	A.ACT_NBR, ");
			sb.append(" 	T.TYPE, ");
			sb.append(" 	CAST(A.START_DATE AS DATE) AS START_DATE, ");
			sb.append(" 	A.ISSUED_DATE, ");
			sb.append(" 	A.CREATED_DATE, ");
			sb.append(" 	A.UPDATED_DATE, ");
			sb.append(" 	A.APPLIED_DATE, ");
			sb.append(" 	A.EXP_DATE, ");
			sb.append(" 	A.DESCRIPTION, ");
			sb.append(" 	'' AS SUBTYPE, ");
			
			sb.append(" 	S.STATUS, ");
			sb.append(" 	S.ONLINE_PRINT, ");
			sb.append(" 	CASE WHEN TTL.FINANCE_LOCK='Y' THEN 0 ELSE TTL.BALANCE_DUE END BALANCE ");
			sb.append(" FROM ");
			sb.append(" 	MAIN AS A ");
			sb.append(" 	JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID AND S.LIVE = 'Y' ");
			sb.append(" 	JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID ");
			sb.append(" 	LEFT OUTER JOIN TOTAL AS TTL ON TTL.REF_ID = A.REF_ID ");
			sb.append(" ORDER BY ");
			sb.append(" 	A.UPDATED_DATE DESC,A.START_DATE DESC,  A.EXP_DATE DESC ");
		}
		return sb.toString();
	}

	public static String myActivities(String username) {
		return myActivities(username, "", "", -1, -1);
	}
	public static String myActivities(String username,String search, String option,int start,int noofrecords) {
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.ID,A.UPDATED_DATE ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND T.ISDOT = 'N' ");
		sb.append(" JOIN REF_ACT_USERS AS R ON A.ID = R.ACTIVITY_ID AND R.ACTIVE = 'Y' AND A.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_USERS AS RU ON R.REF_USERS_ID = RU.ID ");
		sb.append(" JOIN USERS AS U ON RU.USERS_ID = U.ID AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");

	
		
		sb.append(" UNION ");

		sb.append(" SELECT ");
		sb.append(" A.ID ,A.UPDATED_DATE");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND T.ISDOT = 'N' ");
		sb.append(" JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND A.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_PROJECT_USERS AS R ON P.ID = R.PROJECT_ID AND R.ACTIVE = 'Y' AND P.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_USERS AS RU ON R.REF_USERS_ID = RU.ID ");
		sb.append(" JOIN USERS AS U ON RU.USERS_ID = U.ID AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" WHERE A.ACTIVE='Y' ");
		if(Operator.hasValue(search)){
			sb.append(" AND A.ACT_NBR = '").append(Operator.sqlEscape(search)).append("' ");
		}
		
		sb.append(" ORDER BY A.UPDATED_DATE DESC ");
		if(start>=0 && noofrecords>=0){
			sb.append(" OFFSET ").append(start).append(" ROWS FETCH NEXT  ").append(noofrecords).append(" ROWS ONLY ");
		}
		
		
		return sb.toString();
	}

	public static String myActive(String username, String acttype) {
		if (!Operator.hasValue(username)) { return ""; }
		String calc = FinanceSQL.calc("activity", myActiveActivities(username, acttype));
		StringBuilder sb = new StringBuilder();
		if (Operator.hasValue(calc)) {
			sb.append(calc);
			sb.append(" SELECT TOP 200 ");
			sb.append(" 	A.ID, ");
			sb.append(" 	A.ACT_NBR, ");
			sb.append(" 	T.TYPE, ");
			sb.append(" 	CAST(A.START_DATE AS DATE) AS START_DATE, ");
			sb.append(" 	A.ISSUED_DATE, ");
			sb.append(" 	A.CREATED_DATE, ");
			sb.append(" 	A.UPDATED_DATE, ");
			sb.append(" 	A.APPLIED_DATE, ");
			sb.append(" 	A.EXP_DATE, ");
			sb.append(" 	A.DESCRIPTION, ");
			sb.append(" 	'' AS SUBTYPE, ");
			
			sb.append(" 	S.STATUS, ");
			sb.append(" 	S.ONLINE_PRINT, ");
			sb.append(" 	CASE WHEN TTL.FINANCE_LOCK='Y' THEN 0 ELSE TTL.BALANCE_DUE END BALANCE ");
			sb.append(" FROM ");
			sb.append(" 	MAIN AS A ");
			sb.append(" 	JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID AND S.LIVE = 'Y' ");
			sb.append(" 	JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID ");
			sb.append(" 	LEFT OUTER JOIN TOTAL AS TTL ON TTL.REF_ID = A.REF_ID ");
			sb.append(" ORDER BY ");
			sb.append(" 	A.UPDATED_DATE DESC,A.START_DATE DESC,  A.EXP_DATE DESC ");
		}
		return sb.toString();
	}

	public static String myActiveActivities(String username, String acttype) {
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.ID ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND COALESCE(A.EXP_DATE, getDate() + 1) > getDate() ");

		sb.append(" AND ");
		if (Operator.hasValue(acttype)) {
			String types[] = Operator.split(acttype, "|");
			String ft = types[0];
			if (Operator.hasValue(ft)) {
				boolean num = Operator.isNumber(ft);
				if (num) {
					sb.append(" T.ID IN ( ");
				}
				else {
					sb.append(" LOWER(T.TYPE) IN ( ");
				}
				boolean empty = true;
				for (int i=0; i<types.length; i++) {
					String t = types[i];
					if (!empty) {
						sb.append(" , ");
					}
					if (num) {
						sb.append(Operator.toInt(t));
					}
					else {
						sb.append(" '").append(Operator.sqlEscape(t.toLowerCase())).append("' ");
						
					}
					empty = false;
				}
				sb.append(" ) ");
			}
		}
		else {
			sb.append(" T.ISDOT = 'N' ");
		}

		sb.append(" JOIN LKUP_ACT_STATUS AS ACS ON A.LKUP_ACT_STATUS_ID = ACS.ID AND ACS.LIVE = 'Y' AND ACS.EXPIRED = 'N' ");
		sb.append(" JOIN REF_ACT_USERS AS R ON A.ID = R.ACTIVITY_ID AND R.ACTIVE = 'Y' AND A.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_USERS AS RU ON R.REF_USERS_ID = RU.ID ");
		sb.append(" JOIN USERS AS U ON RU.USERS_ID = U.ID AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");

		sb.append(" UNION ");

		sb.append(" SELECT ");
		sb.append(" A.ID ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND COALESCE(A.EXP_DATE, getDate() + 1) > getDate() ");

		sb.append(" AND ");
		if (Operator.hasValue(acttype)) {
			if (Operator.isNumber(acttype) && Operator.toInt(acttype) > 0) {
				sb.append(" T.ID = ").append(Operator.toInt(acttype));
			}
			else {
				sb.append(" LOWER(T.TYPE) = LOWER('").append(Operator.sqlEscape(acttype)).append("') ");
			}
		}
		else {
			sb.append(" T.ISDOT = 'N' ");
		}

		sb.append(" JOIN LKUP_ACT_STATUS AS ACS ON A.LKUP_ACT_STATUS_ID = ACS.ID AND ACS.LIVE = 'Y' AND ACS.EXPIRED = 'N' ");
		sb.append(" JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND A.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_PROJECT_USERS AS R ON P.ID = R.PROJECT_ID AND R.ACTIVE = 'Y' AND P.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_USERS AS RU ON R.REF_USERS_ID = RU.ID ");
		sb.append(" JOIN USERS AS U ON RU.USERS_ID = U.ID AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		return sb.toString();
	}

	public static String my1(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append("   SELECT ");
		sb.append("   DISTINCT RU.ID ");
		sb.append("   FROM ");
		sb.append("   USERS AS U ");
		sb.append("   JOIN REF_USERS AS RU ON U.ID = RU.USERS_ID AND U.ACTIVE = 'Y' AND RU.ACTIVE = 'Y' AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" UNION ");
		sb.append("   SELECT ");
		sb.append("   DISTINCT RU.ID ");
		sb.append("   FROM ");
		sb.append("   USERS AS U ");
		sb.append("   JOIN REF_USERS_GROUP AS RUG ON RUG.USERS_ID = U.ID AND U.ACTIVE = 'Y' AND RUG.ACTIVE = 'Y' AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append("   JOIN USERS_GROUP AS G ON RUG.USERS_GROUP_ID = G.ID AND G.ACTIVE = 'Y' ");
		sb.append("   JOIN REF_USERS AS RU ON G.ID = RU.USERS_GROUP_ID AND RU.ACTIVE = 'Y' ");
		sb.append(" ) ");
		sb.append(" , ");
		sb.append(" Q1 AS ( ");
		sb.append("   SELECT ");
		sb.append("     A.ID ");
		sb.append("     , ");
		sb.append("     A.ACT_NBR ");
		sb.append("     , ");
		sb.append("     T.DESCRIPTION TYPE, ");
		sb.append("     A.START_DATE, ");
		sb.append("     A.ISSUED_DATE, ");
		sb.append("     A.APPLIED_DATE, ");
		sb.append("     A.EXP_DATE, ");
		sb.append("     A.DESCRIPTION, ");
		sb.append("     ST.SUBTYPE, ");
		sb.append("     CASE WHEN COALESCE(A.EXP_DATE, getDate()+1) < getDate() THEN 'EXPIRED' ELSE LAS.STATUS END AS STATUS, ");
		sb.append("     LAS.ONLINE_PRINT, ");
		sb.append("     CASE WHEN S.FINANCE_LOCK='Y' THEN 0 ELSE SUM(S.BALANCE_DUE) END BALANCE ");
		sb.append("   FROM ");
		sb.append("   REF_ACT_USERS AS RAU ");
		sb.append("   JOIN Q ON RAU.REF_USERS_ID = Q.ID AND RAU.ACTIVE = 'Y' ");
		sb.append("   JOIN ACTIVITY AS A ON RAU.ACTIVITY_ID = A.ID AND A.ACTIVE = 'Y' ");
		sb.append("   JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND T.ACTIVE = 'Y' AND T.DOT_EXEMPTION = 'N' ");
		sb.append("   JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID = LAS.ID ");
		sb.append("   LEFT OUTER JOIN LKUP_ACT_SUBTYPE AS ST ON A.LKUP_ACT_SUBTYPE_ID = ST.ID AND ST.ACTIVE = 'Y' ");
		sb.append("   LEFT OUTER JOIN REF_ACT_STATEMENT RAS on A.ID = RAS.ACTIVITY_ID AND RAS.ACTIVE = 'Y'  ");
		sb.append("   LEFT OUTER JOIN STATEMENT S on RAS.STATEMENT_ID = S.ID AND S.ACTIVE = 'Y' ");
		sb.append("   GROUP BY A.ID,A.ACT_NBR,T.DESCRIPTION, A.START_DATE, A.ISSUED_DATE, A.APPLIED_DATE, A.EXP_DATE, A.DESCRIPTION, ST.SUBTYPE, S.FINANCE_LOCK, LAS.STATUS, LAS.ONLINE_PRINT ");
		sb.append(" UNION ");
		sb.append("   SELECT ");
		sb.append("     A.ID ");
		sb.append("     , ");
		sb.append("     A.ACT_NBR ");
		sb.append("     , ");
		sb.append("     T.DESCRIPTION TYPE, ");
		sb.append("     A.START_DATE, ");
		sb.append("     A.ISSUED_DATE, ");
		sb.append("     A.APPLIED_DATE, ");
		sb.append("     A.EXP_DATE, ");
		sb.append("     A.DESCRIPTION, ");
		sb.append("     ST.SUBTYPE, ");
		sb.append("     CASE WHEN COALESCE(A.EXP_DATE, getDate()+1) < getDate() THEN 'EXPIRED' ELSE LAS.STATUS END AS STATUS, ");
		sb.append("     LAS.ONLINE_PRINT, ");
		sb.append("     CASE WHEN S.FINANCE_LOCK='Y' THEN 0 ELSE SUM(S.BALANCE_DUE) END BALANCE ");
		sb.append("   FROM ");
		sb.append("   REF_PROJECT_USERS AS RPU ");
		sb.append("   JOIN Q ON RPU.REF_USERS_ID = Q.ID AND RPU.ACTIVE = 'Y' ");
		sb.append("   JOIN ACTIVITY AS A ON RPU.PROJECT_ID = A.PROJECT_ID AND A.ACTIVE = 'Y' ");
		sb.append("   JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND T.ACTIVE = 'Y' AND T.DOT_EXEMPTION = 'N' ");
		sb.append("   JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID = LAS.ID ");
		sb.append("   LEFT OUTER JOIN LKUP_ACT_SUBTYPE AS ST ON A.LKUP_ACT_SUBTYPE_ID = ST.ID AND ST.ACTIVE = 'Y' ");
		sb.append("   LEFT OUTER JOIN REF_ACT_STATEMENT RAS on A.ID = RAS.ACTIVITY_ID AND RAS.ACTIVE = 'Y'  ");
		sb.append("   LEFT OUTER JOIN STATEMENT S on RAS.STATEMENT_ID = S.ID AND S.ACTIVE = 'Y' ");
		sb.append("   GROUP BY A.ID,A.ACT_NBR,T.DESCRIPTION, A.START_DATE, A.ISSUED_DATE, A.APPLIED_DATE, A.EXP_DATE, A.DESCRIPTION, ST.SUBTYPE, S.FINANCE_LOCK, LAS.STATUS, LAS.ONLINE_PRINT ");
	
		sb.append(" ) ");

		sb.append(" SELECT ");
		sb.append(" DISTINCT TOP 100 ");
		sb.append(" Q1.* ");
		/*sb.append(" , ");
		sb.append(" (");
		sb.append(" SELECT ");
		sb.append("   TOP 1 ");
		sb.append("   L.STATUS ");
		sb.append(" FROM ");
		sb.append("   ACT_STATUS AS S ");
		sb.append("   JOIN LKUP_ACT_STATUS AS L ON S.LKUP_ACT_STATUS_ID = L.ID ");
		sb.append(" WHERE ");
		sb.append("   S.ACTIVITY_ID = Q1.ID");
		sb.append(" ORDER BY S.DATE DESC ");
		sb.append(" ) AS STATUS ");*/
		sb.append(" FROM ");
		sb.append(" Q1 ORDER BY EXP_DATE DESC, START_DATE DESC, APPLIED_DATE DESC ");
		
		return sb.toString();
	}

	public static String getActivity(String actnbr) {
		if (!Operator.hasValue(actnbr)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM ACTIVITY WHERE LOWER(ACT_NBR) = LOWER('").append(Operator.sqlEscape(actnbr)).append("') ");
		return sb.toString();
	}

	public static String getActivityId(String actnbr) {
		if (!Operator.hasValue(actnbr)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ID FROM ACTIVITY WHERE LOWER(ACT_NBR) = LOWER('").append(Operator.sqlEscape(actnbr)).append("') ");
		return sb.toString();
	}

	public static String getProject(String actnbr) {
		if (!Operator.hasValue(actnbr)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" P.* ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN PROJECT AS P ON A.PROJECT_ID = P.ID ");
		sb.append(" WHERE ");
		sb.append(" LOWER(A.ACT_NBR) = LOWER('").append(Operator.sqlEscape(actnbr)).append("') ");
		return sb.toString();
	}

	public static String getActivity(int actid) {
		if (actid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.* ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" U.USERNAME AS UPDATED ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" LEFT OUTER JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON A.UPDATED_BY = U.ID ");
		sb.append(" WHERE ");
		sb.append(" A.ID = ").append(actid).append(" ");
		return sb.toString();
	}
	
	public static String getActivityCalc(int actid) {
		if (actid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(FinanceSQL.calc("activity", actid));
		sb.append(" SELECT ");
		sb.append(" MAIN.ID ");
		sb.append(" , ");
		sb.append(" MAIN.ACT_NBR ");
		sb.append(" , ");
		sb.append(" MAIN.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" MAIN.ISSUED_DATE ");
		sb.append(" , ");
		sb.append(" MAIN.EXP_DATE ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" , ");
		sb.append(" TTL.FEE_AMOUNT ");
		sb.append(" , ");
		sb.append(" TTL.FINANCE_LOCK ");
		sb.append(" , ");
		sb.append(" TTL.FEE_PAID ");
		sb.append(" , ");
		sb.append(" TTL.BALANCE_DUE ");
		sb.append(" FROM ");
		sb.append(" MAIN ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON MAIN.LKUP_ACT_TYPE_ID = T.ID ");
		sb.append(" JOIN LKUP_ACT_STATUS AS S ON MAIN.LKUP_ACT_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN TOTAL AS TTL ON MAIN.REF_ID = TTL.REF_ID ");
		return sb.toString();
	}

	public static String getAddress(int actid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" L.ID AS LSO_ID ");
		sb.append(" , ");
		sb.append(" CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(S.PRE_DIR, '') + ' ' + S.STR_NAME + ' ' + S.STR_TYPE + ' ' + COALESCE(L.UNIT, '') AS ADDRESS ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN REF_LSO_PROJECT AS RP ON A.PROJECT_ID = RP.PROJECT_ID AND RP.ACTIVE='Y' AND A.ID = ").append(actid);
		sb.append(" JOIN LSO AS L ON RP.LSO_ID = L.ID ");
		sb.append(" JOIN LSO_STREET AS S ON L.LSO_STREET_ID = S.ID ");
		return sb.toString();
	}

	public static String getAutoIssuedActivities(String actids) {
		if (!Operator.hasValue(actids)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.ID ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND T.AUTO_ISSUE = 'Y' ");
		sb.append(" AND ");
		sb.append(" A.ID IN (").append(actids).append(") ");
		String sub = sb.toString();
		sb = new StringBuilder();
		sb.append(FinanceSQL.calc("activity", sub));
		sb.append(" SELECT ");
		sb.append(" 	M.REF_ID AS ID, ");
		sb.append(" 	M.LKUP_ACT_TYPE_ID ");
		sb.append(" FROM ");
		sb.append(" 	MAIN AS M ");
		sb.append(" 	LEFT OUTER JOIN TOTAL AS T ON M.REF_ID = T.REF_ID ");
		sb.append(" WHERE ");
		sb.append(" 	COALESCE(T.BALANCE_DUE, 0) <= 0 ");
		return sb.toString();
	}

	public static String updateStatus(int actid, int statusid, int userid) {
		if (actid < 1) { return ""; }
		if (statusid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ACTIVITY SET ");
		sb.append(" LKUP_ACT_STATUS_ID = ").append(statusid);
		sb.append(" , ");
		sb.append(" ISSUED_DATE = CAST(getDate() AS DATE) ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(actid);
		sb.append(" AND ");
		sb.append(" LKUP_ACT_STATUS_ID NOT IN ( ");
		sb.append(" SELECT ID FROM LKUP_ACT_STATUS WHERE ACTIVE = 'Y' AND ISSUED = 'Y' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getProject(int actid) {
		if (actid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" P.* ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN PROJECT AS P ON A.PROJECT_ID = P.ID ");
		sb.append(" WHERE ");
		sb.append(" A.ID = ").append(actid);
		return sb.toString();
	}
	
	
	public static String updateActivityNumber(int actid,String number) {
		if (actid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ACTIVITY SET ACT_NBR = '").append(Operator.sqlEscape(number)).append("' WHERE ID = ").append(actid).append(" ");
		return sb.toString();
	}

	/*NOT TO BE USED USING OUTPUT.INSERTED.ID
	 * public static String getActivity(int projectid, String actnbr, int userid, Timekeeper now) {
		if (!Operator.hasValue(actnbr)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY ");
		sb.append(" WHERE ");
		sb.append(" LOWER(ACT_NBR) = LOWER('").append(Operator.sqlEscape(actnbr)).append("') ");
		sb.append(" AND ");
		sb.append(" PROJECT_ID = ").append(projectid);
		sb.append(" AND ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" AND ");
		sb.append(" UPDATED_DATE = ").append(now.sqlDatetime());
		return sb.toString();
	}*/

	public static String add(String actnbr, int projectid, int lkupacttypeid, String description, int lkupactstatusid, double valuationcalculated, double valuationdeclared, String planchkreq, String startdate, String applieddate, String issueddate, String expdate, String applexpdate, String finaldate, String online, String sensitive, String inherit, int userid, String ip, Timekeeper now, String cc) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ACTIVITY ( ");
		sb.append(" ACT_NBR ");
		sb.append(" , ");
		sb.append(" PROJECT_ID ");
		sb.append(" , ");
		sb.append(" LKUP_ACT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		sb.append(" VALUATION_CALCULATED ");
		sb.append(" , ");
		sb.append(" VALUATION_DECLARED ");
		sb.append(" , ");
		sb.append(" PLAN_CHK_REQ ");
		sb.append(" , ");
		sb.append(" START_DATE ");
		sb.append(" , ");
		sb.append(" APPLIED_DATE ");
		sb.append(" , ");
		sb.append(" ISSUED_DATE ");
		sb.append(" , ");
		sb.append(" EXP_DATE ");
		sb.append(" , ");
		sb.append(" APPLICATION_EXP_DATE ");
		sb.append(" , ");
		sb.append(" FINAL_DATE ");
		sb.append(" , ");
		sb.append(" ONLINE ");
		sb.append(" , ");
		sb.append(" SENSITIVE ");
		sb.append(" , ");
		sb.append(" LKUP_ACT_STATUS_ID ");
		sb.append(" , ");
		sb.append(" INHERIT ");
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
		sb.append(" , ");
		sb.append(" CODE_ENFORCEMENT ");

		sb.append(" ) OUTPUT Inserted.ID  VALUES ( ");

		sb.append(" '").append(Operator.sqlEscape(actnbr)).append("' ");
		sb.append(" , ");
		sb.append(projectid);
		sb.append(" , ");
		sb.append(lkupacttypeid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(valuationcalculated);
		sb.append(" , ");
		sb.append(valuationdeclared);
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(planchkreq));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(startdate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(applieddate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(issueddate));
		sb.append(" , ");
		if (Operator.hasValue(expdate)) {
			sb.append(CsTools.dateColumnValue(expdate));
		}
		else {
			sb.append(" ( ");
			sb.append("   SELECT ");
			sb.append(" 		CASE ");
			sb.append(" 			WHEN YEARS_TILL_EXPIRED > 0 AND DAYS_TILL_EXPIRED > 0 THEN DATEADD(yy,YEARS_TILL_EXPIRED,getdate()) + DAYS_TILL_EXPIRED ");
			sb.append(" 			WHEN YEARS_TILL_EXPIRED > 0 THEN DATEADD(yy,YEARS_TILL_EXPIRED,getdate()) ");
			sb.append(" 			WHEN DAYS_TILL_EXPIRED > 0 THEN getDate() + DAYS_TILL_EXPIRED ");
			sb.append(" 		ELSE null END AS EXP_DATE ");
			sb.append("   FROM ");
			sb.append("   LKUP_ACT_TYPE ");
			sb.append("   WHERE ");
			sb.append("   ID = ").append(lkupacttypeid);
			sb.append(" ) ");
		}
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(applexpdate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(finaldate));
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(online));
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(sensitive));
		sb.append(" , ");
		sb.append(lkupactstatusid);
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(inherit));
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(now.sqlDatetime());
		sb.append(" , ");
		sb.append(now.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(cc));
		sb.append(" ) ");
		return sb.toString();
	}

//	public static String addHistory(int actid) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(" INSERT INTO ACTIVITY_HISTORY SELECT * FROM ACTIVITY WHERE ID = ").append(actid).append(" ");
//		return sb.toString();
//	}

	public static String addHistory(int actid, String actnbr, int projectid, String type, int lkupacttypeid, int lkupactsubtypeid, String description, double valuationcalculated, double valuationdeclared, String planchkreq, String startdate, String applieddate, String issueddate, String expdate, String applexpdate, String finaldate, String planchkfeedate, String permitfeedate, String online, String sensitive, String status, int lkupactstatusid, int qty, String printed, String inherit, int createdby, String createddate, String updated, int updatedby, String updateddate, int workflowgoupid, String active, String createdip, String updatedip, String module, int moduleid, String moduleaction) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ACTIVITY_HISTORY ( ");
		sb.append(" ID, ");
		sb.append(" ACT_NBR, ");
		sb.append(" PROJECT_ID, ");
		sb.append(" TYPE, ");
		sb.append(" LKUP_ACT_TYPE_ID, ");
		sb.append(" LKUP_ACT_SUBTYPE_ID, ");
		sb.append(" DESCRIPTION, ");
		sb.append(" VALUATION_CALCULATED, ");
		sb.append(" VALUATION_DECLARED, ");
		sb.append(" PLAN_CHK_REQ, ");
		sb.append(" START_DATE, ");
		sb.append(" APPLIED_DATE, ");
		sb.append(" ISSUED_DATE, ");
		sb.append(" EXP_DATE, ");
		sb.append(" APPLICATION_EXP_DATE, ");
		sb.append(" FINAL_DATE, ");
		sb.append(" PLAN_CHK_FEE_DATE, ");
		sb.append(" PERMIT_FEE_DATE, ");
		sb.append(" ONLINE, ");
		sb.append(" SENSITIVE, ");
		sb.append(" STATUS, ");
		sb.append(" LKUP_ACT_STATUS_ID, ");
		sb.append(" QTY, ");
		sb.append(" PRINTED, ");
		sb.append(" INHERIT, ");
		sb.append(" CREATED_BY, ");
		sb.append(" CREATED_DATE, ");
		sb.append(" UPDATED, ");
		sb.append(" UPDATED_BY, ");
		sb.append(" UPDATED_DATE, ");
		sb.append(" WORKFLOW_GROUP_ID, ");
		sb.append(" ACTIVE, ");
		sb.append(" CREATED_IP, ");
		sb.append(" UPDATED_IP, ");
		sb.append(" MODULE_CHANGED, ");
		sb.append(" MODULE_CHANGED_ID, ");
		sb.append(" MODULE_CHANGED_ACTION ");

		sb.append(" ) VALUES ( ");

		sb.append(actid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(actnbr)).append("' ");
		sb.append(" , ");
		sb.append(projectid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(type)).append("' ");
		sb.append(" , ");
		sb.append(lkupacttypeid);
		sb.append(" , ");
		sb.append(lkupactsubtypeid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(valuationcalculated);
		sb.append(" , ");
		sb.append(valuationdeclared);
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(planchkreq));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(startdate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(applieddate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(issueddate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(expdate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(applexpdate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(finaldate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(planchkfeedate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(permitfeedate));
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(online));
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(sensitive));
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(status)).append("' ");
		sb.append(" , ");
		sb.append(lkupactstatusid);
		sb.append(" , ");
		sb.append(qty);
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(printed));
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(inherit));
		sb.append(" , ");
		sb.append(createdby);
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(createddate));
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(updated)).append("' ");
		sb.append(" , ");
		sb.append(updatedby);
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(updateddate));
		sb.append(" , ");
		sb.append(workflowgoupid);
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(active));
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(createdip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(updatedip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(module)).append("' ");
		sb.append(" , ");
		sb.append(moduleid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(moduleaction)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String update(int actid, String description, int lkupactstatusid, double valuationcalculated, double valuationdeclared, String planchkreq, String startdate, String applieddate, String issueddate, String expdate, String applexpdate, String finaldate, String online, String sensitive, String inherit, int userid, String ip, Timekeeper now, String cc) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ACTIVITY SET ");
		sb.append(" DESCRIPTION = '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(" VALUATION_CALCULATED = ").append(valuationcalculated);
		sb.append(" , ");
		sb.append(" VALUATION_DECLARED = ").append(valuationdeclared);
		sb.append(" , ");
		sb.append(" LKUP_ACT_STATUS_ID = ").append(lkupactstatusid);
		sb.append(" , ");
		sb.append(" PLAN_CHK_REQ = ").append(CsTools.booleanColumnValue(planchkreq));
		sb.append(" , ");
		sb.append(" ONLINE = ").append(CsTools.booleanColumnValue(online));
		sb.append(" , ");
		sb.append(" SENSITIVE = ").append(CsTools.booleanColumnValue(sensitive));
		sb.append(" , ");
		sb.append(" INHERIT = ").append(CsTools.booleanColumnValue(inherit));
		sb.append(" , ");
		sb.append(" CODE_ENFORCEMENT = ").append(CsTools.booleanColumnValue(cc));
		sb.append(" , ");
		sb.append(" START_DATE = ").append(CsTools.dateColumnValue(startdate));
		sb.append(" , ");
		sb.append(" APPLIED_DATE = ").append(CsTools.dateColumnValue(applieddate));
		sb.append(" , ");
		sb.append(" ISSUED_DATE = ").append(CsTools.dateColumnValue(issueddate));
		sb.append(" , ");
		sb.append(" EXP_DATE = ").append(CsTools.dateColumnValue(expdate));
		sb.append(" , ");
		sb.append(" APPLICATION_EXP_DATE = ").append(CsTools.dateColumnValue(applexpdate));
		sb.append(" , ");
		sb.append(" FINAL_DATE = ").append(CsTools.dateColumnValue(finaldate));
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = ").append(now.sqlDatetime());
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" OUTPUT Inserted.* ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(actid);
		return sb.toString();
	}

	/**
	 * @deprecated test - do not use
	 */
	public static String addActivity(String actnbr, int projectid, int lkupacttypeid, int lkupactsubtypeid, String description, double valuationcalculated, double valuationdeclared, String planchkreq, String startdate, String applieddate, String issueddate, String expdate, String finaldate, String planchkfeedate, String permitfeedate, String online, String sensitive, String workflowgroupid, int lkupactstatusid, String qty, String printed, int userid, String ip, Timekeeper now) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ACTIVITY ( ");
		sb.append(" ACT_NBR ");
		sb.append(" , ");
		sb.append(" PROJECT_ID ");
		sb.append(" , ");
		sb.append(" LKUP_ACT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" LKUP_ACT_SUBTYPE_ID ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		sb.append(" VALUATION_CALCULATED ");
		sb.append(" , ");
		sb.append(" VALUATION_DECLARED ");
		sb.append(" , ");
		sb.append(" PLAN_CHK_REQ ");
		sb.append(" , ");
		sb.append(" START_DATE ");
		sb.append(" , ");
		sb.append(" APPLIED_DATE ");
		sb.append(" , ");
		sb.append(" ISSUED_DATE ");
		sb.append(" , ");
		sb.append(" EXP_DATE ");
		sb.append(" , ");
		sb.append(" FINAL_DATE ");
		sb.append(" , ");
		sb.append(" PLAN_CHK_FEE_DATE ");
		sb.append(" , ");
		sb.append(" PERMIT_FEE_DATE ");
		sb.append(" , ");
		sb.append(" ONLINE ");
		sb.append(" , ");
		sb.append(" SENSITIVE ");
		sb.append(" , ");
		sb.append(" WORKFLOW_GROUP_ID ");
		sb.append(" , ");
		sb.append(" LKUP_ACT_STATUS_ID ");
		sb.append(" , ");
		sb.append(" QTY ");
		sb.append(" , ");
		sb.append(" PRINTED ");
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
		sb.append(" ) VALUE ( ");
		sb.append(" '").append(Operator.sqlEscape(actnbr)).append("' ");
		sb.append(" , ");
		sb.append(projectid);
		sb.append(" , ");
		sb.append(lkupacttypeid);
		sb.append(" , ");
		sb.append(lkupactsubtypeid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(valuationcalculated);
		sb.append(" , ");
		sb.append(valuationdeclared);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(planchkreq)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(startdate)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(applieddate)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(issueddate)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(expdate)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(finaldate)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(planchkfeedate)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(permitfeedate)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(online)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(sensitive)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(workflowgroupid)).append("' ");
		sb.append(" , ");
		sb.append(lkupactstatusid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(qty)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(printed)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(now.sqlDatetime());
		sb.append(" , ");
		sb.append(now.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

//	public static String getDetails(int id){
//		StringBuilder sb = new StringBuilder();
//		sb.append(" WITH Q AS ");
//		sb.append(" ( SELECT TOP 1 ATS.ACTIVITY_ID, ATS.LKUP_ACT_STATUS_ID,LAS.STATUS from ").append(Table.ACTSTATUSTABLE).append(" ATS "); 
//		sb.append(" LEFT OUTER JOIN LKUP_ACT_STATUS LAS on ATS.LKUP_ACT_STATUS_ID=LAS.ID ");
//		sb.append(" where ATS.ACTIVITY_ID=").append(id).append(" and ATS.ACTIVE='Y' order by DATE DESC");
//		sb.append(" ) ");
//					
//		sb.append(" select A.*,ATT.DESCRIPTION as TITLE, A.ACT_NBR AS SUBTITLE,Q.LKUP_ACT_STATUS_ID,Q.STATUS,CU.USERNAME AS CREATED,UP.USERNAME as UPDATED   "); 
//		sb.append("  from  ").append(Table.ACTIVITYTABLE).append(" A ");
//		sb.append("  JOIN  ").append(Table.ACTTYPETABLE).append(" ATT ON A.LKUP_ACT_TYPE_ID=ATT.ID ");
//		sb.append(" LEFT OUTER JOIN Q on A.ID=Q.ACTIVITY_ID ");
//		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY=CU.ID ");
//		sb.append("	LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY=UP.ID "); 
//		sb.append(" WHERE A.ID= ").append(id).append(" AND A.ACTIVE='Y' ");
//		
//		return sb.toString();
//	}
//
//	public static String getCustomGroups(int id) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(" SELECT DISTINCT");
//		sb.append(" G.*, R.ORDR ");
//		sb.append(" FROM ");
//		sb.append(" ACTIVITY AS A ");
//		sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID ");
//		sb.append(" JOIN REF_ACT_FIELD_GROUPS AS R ON R.LKUP_ACT_TYPE_ID = T.ID ");
//		sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID ");
//		sb.append(" WHERE ");
//		sb.append(" G.ACTIVE = 'Y'  ");
//		sb.append(" AND ");
//		sb.append(" A.ID = ").append(id);
//		sb.append(" ORDER BY R.ORDR ");
//		return sb.toString();
//		
//	
//	}
//
//	public static String getCustomGroupsAndFields(int id) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(" SELECT ");
//		sb.append(" V.ID ");
//		sb.append(" , ");
//		sb.append(" F.NAME ");
//		sb.append(" , ");
//		sb.append(" V.VALUE ");
//		sb.append(" , ");
//		sb.append(" G.GROUP_NAME ");
//		sb.append(" ,");
//		sb.append(" G.ID as GROUP_ID ");
//		sb.append(" ,");
//		sb.append(" G.ISPUBLIC as GROUP_PUBLIC ");
//		sb.append(" , ");
//		sb.append(" R.ORDR AS GROUP_ORDER ");
//		sb.append(" , ");
//		sb.append(" F.ORDR AS FIELD_ORDER ");
//		sb.append(" ,");
//		sb.append(" LFT.TYPE as FIELD_TYPE ");
//		sb.append(" ,");
//		sb.append(" LFIT.TYPE as FIELD_ITYPE ");
//		sb.append(" FROM ");
//		sb.append(" ACTIVITY AS P ");
//		sb.append(" JOIN LKUP_ACT_TYPE AS T ON P.LKUP_ACT_TYPE_ID = T.ID ");
//		sb.append(" JOIN REF_ACT_FIELD_GROUPS AS R ON R.LKUP_ACT_TYPE_ID = T.ID ");
//		sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID ");
//		sb.append(" JOIN FIELD AS F ON F.FIELD_GROUPS_ID = G.ID ");
//		sb.append(" LEFT OUTER JOIN ACT_FIELD_VALUE AS V ON F.ID = V.FIELD_ID AND V.ACTIVE = 'Y' AND V.ACTIVITY_ID = P.ID ");
//		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDTYPETABLE).append(" LFT ON F.LKUP_FIELD_TYPE_ID = LFT.ID ");
//		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDITYPETABLE).append(" LFIT ON F.LKUP_FIELD_ITYPE_ID = LFIT.ID ");
//		
//		sb.append(" WHERE ");
//		sb.append(" F.ACTIVE = 'Y' ");
//		sb.append(" AND ");
//		sb.append(" G.ACTIVE = 'Y'  ");
//		sb.append(" AND ");
//		sb.append(" P.ID = ").append(id);
//		sb.append(" ORDER BY R.ORDR, G.GROUP_NAME, F.ORDR ");
//		return sb.toString();
//	}
//
//	public static String getReviews(int id) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(" WITH Q AS ( ");
//		sb.append("    SELECT ");
//		sb.append("        R.ID, MAX(A.DATE) AS MAXDATE ");
//		sb.append("    FROM ");
//		sb.append("        REF_ACT_REVIEW AS REF ");
//		sb.append("        JOIN REVIEW_ACTION AS A ON REF.REVIEW_ACTION_ID = A.ID ");
//		sb.append("        JOIN REVIEW AS R ON A.REVIEW_ID = R.ID ");
//		sb.append("    WHERE ");
//		sb.append("        REF.ACTIVITY_ID = ").append(id);
//		sb.append("    GROUP BY ");
//		sb.append("        R.ID ");
//		sb.append(" ) ");
//		sb.append(" , ");
//		sb.append(" MA AS ( ");
//		sb.append("    SELECT ");
//		sb.append("        Q.ID AS REVIEW_ID, MAX(A.ID) AS ID ");
//		sb.append("    FROM ");
//		sb.append("        REF_ACT_REVIEW AS REF ");
//		sb.append("        JOIN REVIEW_ACTION AS A ON REF.REVIEW_ACTION_ID = A.ID ");
//		sb.append("        JOIN Q ON A.REVIEW_ID = Q.ID AND A.DATE = Q.MAXDATE ");
//		sb.append("    WHERE ");
//		sb.append("        REF.ACTIVITY_ID = ").append(id);
//		sb.append("    GROUP BY ");
//		sb.append("        Q.ID ");
//		sb.append(" ) ");
//
//		sb.append(" , ");
//		sb.append(" S AS ( ");
//		sb.append("     SELECT ");
//		sb.append("         DISTINCT ");
//		sb.append("         A.ID AS PARENT_ID, ");
//		sb.append("         'activity' AS PARENT, ");
//		sb.append("         R.ID AS REVIEW_ID, ");
//		sb.append("         G.ID AS REVIEW_GROUP_ID, ");
//		sb.append("         G.GROUP_NAME, ");
//		sb.append("         R.NAME AS REVIEW, ");
//		sb.append("         ACTN.ID AS ACTION_ID, ");
//		sb.append("         ACTN.NAME AS ACTION, ");
//		sb.append("         S.DESCRIPTION AS STATUS, ");
//		sb.append("         ACTN.DATE, ");
//		sb.append("         R.DISPLAY_TYPE, ");
//		sb.append("         G.ORDR AS GROUP_ORDER, ");
//		sb.append("         R.ORDR AS REVIEW_ORDER ");
//		sb.append("     FROM ");
//		sb.append("        ACTIVITY AS A ");
//		sb.append("        JOIN LKUP_ACT_TYPE AS AT ON A.LKUP_ACT_TYPE_ID = AT.ID ");
//		sb.append("        JOIN REF_ACT_REVIEW_GROUP AS ARG ON AT.ID = ARG.LKUP_ACT_TYPE_ID ");
//		sb.append("        JOIN REVIEW_GROUP AS G ON ARG.REVIEW_GROUP_ID = G.ID ");
//		sb.append("        JOIN REVIEW AS R ON G.ID = R.REVIEW_GROUP_ID ");
//		sb.append("        LEFT OUTER JOIN ( ");
//		sb.append("            MA ");
//		sb.append("            JOIN REVIEW_ACTION AS ACTN ON MA.ID = ACTN.ID ");
//		sb.append("            JOIN REF_ACT_REVIEW AS AR ON ACTN.ID = AR.REVIEW_ACTION_ID AND AR.ACTIVITY_ID = ").append(id);
//		sb.append("            JOIN LKUP_REVIEW_STATUS AS S ON ACTN.LKUP_REVIEW_STATUS_ID = S.ID ");
//		sb.append("        ) ON R.ID = ACTN.REVIEW_ID ");
//		sb.append("     WHERE ");
//		sb.append("        A.ID = ").append(id);
////		sb.append("        AND ");
////		sb.append("        ( ");
////		sb.append("            ACTN.NAME IS NOT NULL ");
////		sb.append("            OR ");
////		sb.append("            R.DISPLAY_TYPE = 'Y' ");
////		sb.append("        ) ");
//		sb.append(" ) ");
//		sb.append(" , ");
//		sb.append(" G AS ( ");
//		sb.append("     SELECT DISTINCT REVIEW_GROUP_ID, GROUP_NAME FROM S ");
//		sb.append(" ) ");
//		sb.append(" , ");
//		sb.append(" M AS ( ");
//		sb.append("    SELECT ");
//		sb.append("         DISTINCT ");
//		sb.append("         S.PARENT_ID, ");
//		sb.append("         S.PARENT, ");
//		sb.append("         S.REVIEW_ID, ");
//		sb.append("         G.REVIEW_GROUP_ID, ");
//		sb.append("         G.GROUP_NAME, ");
//		sb.append("         S.REVIEW, ");
//		sb.append("         S.ACTION_ID, ");
//		sb.append("         S.ACTION, ");
//		sb.append("         S.STATUS, ");
//		sb.append("         S.DATE, ");
//		sb.append("         S.DISPLAY_TYPE, ");
//		sb.append("         S.GROUP_ORDER, ");
//		sb.append("         S.REVIEW_ORDER ");
//		sb.append("    FROM ");
//		sb.append("        G ");
//		sb.append("        LEFT OUTER JOIN S ON G.REVIEW_GROUP_ID = S.REVIEW_GROUP_ID AND S.GROUP_NAME = G.GROUP_NAME AND (S.ACTION IS NOT NULL OR S.DISPLAY_TYPE = 'Y' ) ");
//		sb.append(" ) ");
//		sb.append(" , ");
//		sb.append(" GC AS ( ");
//		sb.append("     SELECT COUNT(DISTINCT GROUP_NAME) AS GROUPS FROM M ");
//		sb.append(" ) ");
//
//		sb.append(" SELECT ");
//		sb.append("     M.*, GC.GROUPS ");
//		sb.append(" FROM ");
//		sb.append("     M ");
//		sb.append("     JOIN GC ON 1=1 ");
//		sb.append(" ORDER BY ");
//		sb.append("     M.GROUP_ORDER, M.GROUP_NAME, M.REVIEW_ORDER, M.REVIEW ");
//
//		return sb.toString();
//	}
//	
//	
	
	/*public static String update(ObjVO[] obj,int id,int userId){
		//ObjVO[] obj = new ObjVO[vo.getObj().length];
		StringBuilder sb = new StringBuilder();
		if(id>0){
			sb.append(" UPDATE ACTIVITY SET ");
			int l = obj.length;
			
			for(int i=0;i<l;i++){
				ObjVO v = obj[i];
				
				if(!Operator.equalsIgnoreCase(v.getItype(), "uneditable") && !Operator.equalsIgnoreCase(v.getFieldid(), "STATUS")){
					sb.append(v.getFieldid());
					sb.append("=");
					sb.append("'").append(Operator.sqlEscape(v.getValue())).append("'");
					sb.append(",");
				}
			}
			
			sb.append(" UPDATED_DATE = CURRENT_TIMESTAMP, UPDATED_BY= ").append(userId);
			sb.append(" WHERE ID =").append(id);
		}
		return sb.toString();
	}*/
	
	public static String getRefIdActivity(RequestVO r, Token u) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select TOP 1 ID FROM ").append(Table.ACTIVITYTABLE).append(" ");
		sb.append(" WHERE ID >0  ");
		sb.append(" AND ");
		/*sb.append(" PATH = '").append(Operator.sqlEscape(obj[2].getValue())).append("' ");
		sb.append(" AND ");*/
		sb.append(" CREATED_BY = ").append(u.getId());
		sb.append(" order by CREATED_DATE DESC ");
		return sb.toString();
	}
	
	public static String insertStatus(ObjVO v, int actid, int userId){
		return insertStatus(actid, Operator.toInt(v.getValue()), userId);
	}

	public static String insertStatus(int actid, int statusid, int userid){
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ACT_STATUS ");
		sb.append(" ( ACTIVITY_ID, DATE, LKUP_ACT_STATUS_ID, CREATED_BY, UPDATED_BY ) VALUES ( ");
		sb.append(actid);
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(statusid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" ) ");
		
		return sb.toString();
		
	}
	
	
	public static String getActNbr(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT T.TYPE AS TYPE, 0 AS ID  FROM  ").append(Table.ACTTYPETABLE).append(" AS T WHERE ACTIVE ='Y' AND ID =  ").append(id);
		sb.append(" UNION ");
		sb.append(" SELECT  '' AS TYPE, MAX(ID) +1 AS ID FROM  ").append(Table.ACTIVITYTABLE).append("  ");
		return sb.toString();
	}
	
	public static String getActNbrByTypeHr(int acttypeid, int hr, String start_date,String start_date_hr){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT   count(*) +1 as COUNT, '' as RANDOM  FROM  ACTIVITY where ( CONVERT(VARCHAR(50), START_DATE, 101)= '").append(start_date).append("' or (CONVERT(VARCHAR(50), START_DATE, 101)= '").append(start_date_hr).append("' AND DATEPART(HOUR, START_DATE) < ").append(hr).append(") ) and LKUP_ACT_TYPE_ID= ").append(acttypeid).append(" union  SELECT   TOP 1 0 as count, SUBSTRING(ACT_NBR,1,2) as RANDOM   FROM  ACTIVITY where CONVERT(VARCHAR(50), START_DATE, 101)='").append(start_date).append("' and LKUP_ACT_TYPE_ID= ").append(acttypeid).append(" ");
		return sb.toString();
	}
	
	public static String getActNbrByType(int acttypeid,String start_date){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT    COUNT(*) +1   as COUNT, '' as RANDOM  FROM  ACTIVITY where CONVERT(VARCHAR(50), START_DATE, 101)= '").append(start_date).append("' and LKUP_ACT_TYPE_ID= ").append(acttypeid).append(" union  SELECT   TOP 1 0 as count, SUBSTRING(ACT_NBR,1,2) as RANDOM   FROM  ACTIVITY where CONVERT(VARCHAR(50), START_DATE, 101)='").append(start_date).append("' and LKUP_ACT_TYPE_ID= ").append(acttypeid).append(" ");
		return sb.toString();
	}

	public static String valuation(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT  VALUATION_CALCULATED  FROM  ").append(Table.ACTIVITYTABLE).append(" WHERE ID = ").append(id);
		return sb.toString();
	}
	
	//sunil
	public static String print(String type, int typeid) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 	LABEL = t.c.value('local-name(.)', 'VARCHAR(100)') ");
		sb.append(" 	,FIELDVALUE = t.c.value('.', 'VARCHAR(500)') ");
		sb.append("  ");
		sb.append(" 	FROM ( ");
		sb.append(" 		SELECT ");
		sb.append(" 			 [XML] = ( ");
		sb.append(" 				 ");
		sb.append(" 	select  ");
		sb.append(" 	CONVERT(varchar(100), L.STR_NO)+' '+ ");
		sb.append(" 	LS.STR_NAME+' '+ LS.STR_TYPE+' '+ CASE WHEN PRE_DIR is null THEN '' ELSE PRE_DIR END+''+CASE WHEN L.UNIT is null then '' ELSE L.UNIT END as  ");
		sb.append(" 	lso_address,APN as lso_APN ");
		sb.append("  ");
		sb.append(" 	FOR XML RAW('f'), TYPE ");
		sb.append(" 	) ");
		sb.append(" 	from ACTIVITY A ");
		sb.append(" 	join REF_LSO_PROJECT R on A.PROJECT_ID=R.PROJECT_ID ");
		sb.append(" 	join LSO L on R.LSO_ID=L.ID ");
		sb.append(" 	join LSO_STREET LS on L.LSO_STREET_ID=LS.ID ");
		sb.append(" 	left outer join REF_LSO_APN LA on L.ID =LA.LSO_ID ");
		sb.append(" 	WHERE  A.ID = ").append(typeid).append("  ");
		sb.append("  ");
		sb.append(" 	) p ");
		sb.append(" 	CROSS APPLY p.[XML].nodes('f/@*') t(c) ");
		sb.append("  ");
		sb.append(" 	union  ");
		sb.append("  ");
		sb.append(" 	select  ");
		sb.append(" 	'division_'+''+LDT.TYPE as LABEL ");
		sb.append(" 	,LD.DIVISION as FIELDVALUE ");
		sb.append(" 	from ACTIVITY A ");
		sb.append(" 	join REF_LSO_PROJECT R on A.PROJECT_ID=R.PROJECT_ID ");
		sb.append(" 	left outer join REF_LSO_DIVISIONS RLD on R.LSO_ID= RLD.LSO_ID  ");
		sb.append(" 	left outer join LKUP_DIVISIONS LD on RLD.LKUP_DIVISIONS_ID= LD.ID and LD.ACTIVE='Y' ");
		sb.append(" 	left outer join LKUP_DIVISIONS_TYPE LDT on LD.LKUP_DIVISIONS_TYPE_ID=LDT.ID ");
		sb.append(" 	WHERE  A.ID = ").append(typeid).append("  ");
		sb.append("  ");
		
		
		 sb.append(" 	union  ");
		 sb.append("  ");
		 sb.append(" SELECT ");
		 sb.append(" LABEL = t.c.value('local-name(.)', 'VARCHAR(100)') ");
		 sb.append(" ,FIELDVALUE = t.c.value('.', 'VARCHAR(500)') ");
		 sb.append(" 	 ");
		 sb.append(" FROM ( ");
		 sb.append(" 	SELECT ");
		 sb.append(" 		 [XML] = ( ");
		 sb.append(" 			  ");
		 sb.append(" 	 ");
		 sb.append(" 				SELECT P.ID as ").append("project").append("_ID");
		 sb.append(" 				, ");
		 sb.append(" 				PROJECT_NBR as ").append("project").append("_PROJECT_NBR");
		 sb.append(" 				, ");
		 sb.append(" 				P.NAME as ").append("project").append("_NAME");
		 
		 sb.append(" 				, ");
		 sb.append(" 				P.DESCRIPTION as ").append("project").append("_DESCRIPTION");
		 sb.append(" 				, ");
		 sb.append(" 				P.CIP_ACCTNO as ").append("project").append("_CIP_ACCTNO");
		 sb.append(" 				, ");
		 sb.append(" 				P.ACCOUNT_NUMBER as ").append("project").append("_ACCOUNT_NUMBER");
		 
		 sb.append(" 				, ");
		 sb.append(" 				LAT.TYPE as ").append("project").append("_TYPE");
		 
		 sb.append(" 				, ");
		 sb.append(" 				LAS.STATUS as ").append("project").append("_STATUS");
		
		 sb.append(" 				, ");
		 sb.append(" 				P.START_DT as ").append("project").append("_START_DT");
		 sb.append(" 				, ");
		 sb.append(" 				P.COMPLETION_DT as ").append("project").append("_COMPLETION_DT");
		 sb.append(" 				, ");
		 sb.append(" 				P.APPLIED_DT as ").append("project").append("_APPLIED_DT");
		 sb.append(" 				, ");
		 sb.append(" 				P.EXPIRED_DT as ").append("project").append("_EXPIRED_DT");
		 sb.append(" 				, ");
		 sb.append(" 				CU.USERNAME as ").append("project").append("_CREATED_BY");
		
		 sb.append(" 				, ");
		 sb.append(" 				UP.USERNAME as ").append("project").append("_UPDATED_BY");
		
		 sb.append(" 				, ");
		 sb.append(" 				P.CREATED_DATE as ").append("project").append("_CREATED_DATE");
		
		 sb.append(" 				, ");
		 sb.append(" 				P.UPDATED_DATE as ").append("project").append("_UPDATED_DATE");
		 
		 
		 sb.append(" 				FOR XML RAW('f'), TYPE ");
		 sb.append(" 				 ");
		 sb.append(" 			) ");
		 sb.append(" 	FROM ACTIVITY A   ");
		 sb.append(" 	join PROJECT P  ON A.PROJECT_ID=P.ID ");
		 sb.append(" 	join LKUP_PROJECT_TYPE LAT on P.LKUP_PROJECT_TYPE_ID=LAT.ID  ");
		 sb.append(" 	join LKUP_PROJECT_STATUS LAS on P.LKUP_PROJECT_STATUS_ID=LAS.ID  ");
		 sb.append(" LEFT OUTER JOIN USERS AS CU ON P.CREATED_BY = CU.ID ");
		 sb.append(" LEFT OUTER JOIN USERS UP ON P.UPDATED_BY = UP.ID  ");
		 sb.append(" 	where A.ID = ").append(typeid).append("  ");
		 sb.append(" 	 ");
		 sb.append(" ) p ");
		 sb.append(" CROSS APPLY p.[XML].nodes('f/@*') t(c) ");
		
		
		sb.append(" 	union  ");
		sb.append("  ");
		sb.append(" 	SELECT ");
		sb.append(" 	LABEL = t.c.value('local-name(.)', 'VARCHAR(100)') ");
		sb.append(" 	,FIELDVALUE = t.c.value('.', 'VARCHAR(500)') ");
		sb.append(" 		 ");
		sb.append(" 	FROM ( ");
		sb.append(" 		SELECT ");
		sb.append(" 			 [XML] = ( ");
		sb.append(" 				 ");
		sb.append(" 	 	 ");
		sb.append(" 					SELECT A.ID as activity_ID 				, ");
		sb.append(" 					ACT_NBR as activity_ACT_NBR 				, ");
		sb.append(" 					'http://dev.beverlyhills.org/cs/?entity=lso&type=activity&reference='+ACT_NBR as QR_CODE 				, ");
		sb.append(" 					LAT.DESCRIPTION as activity_TYPE 				, ");
		sb.append(" 					A.DESCRIPTION as activity_DESCRIPTION 				, ");
		sb.append(" 					A.VALUATION_CALCULATED as activity_VALUATION_CALCULATED , ");
		sb.append(" 					A.VALUATION_DECLARED as activity_VALUATION_DECLARED 				, ");
		sb.append(" 					A.START_DATE as activity_START_DATE 				, ");
		sb.append(" 					A.APPLIED_DATE as activity_APPLIED_DATE 				, ");
		sb.append(" 					A.ISSUED_DATE as activity_ISSUED_DATE 				, ");
		sb.append(" 					A.EXP_DATE as activity_EXP_DATE 				, ");
		sb.append(" 					CU.USERNAME as activity_CREATED_BY 				, ");
		sb.append(" 					UP.USERNAME as activity_UPDATED_BY 				 ");
		sb.append(" 					FOR XML RAW('f'), TYPE ");
		sb.append(" 					 ");
		sb.append(" 				) ");
		sb.append(" 		FROM ACTIVITY A ");
		sb.append(" 		join LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID ");
		sb.append(" 	        LEFT OUTER JOIN USERS AS CU ON A.CREATED_BY = CU.ID ");
		sb.append(" 	        LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID ");
		sb.append(" 	 	where A.ID = ").append(typeid).append("  ");
		sb.append(" 	) p ");
		sb.append(" 	CROSS APPLY p.[XML].nodes('f/@*') t(c) ");
		
		return sb.toString();
	}	
	
	
	public static String printAddl(String type, int typeid) {
		StringBuilder sb = new StringBuilder();
		
		//sb.append(" 	WITH Q AS ( ");
		
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 	LABEL = t.c.value('local-name(.)', 'VARCHAR(100)') ");
		sb.append(" 	,FIELDVALUE = t.c.value('.', 'VARCHAR(500)') ");
		sb.append("  ");
		sb.append(" 	FROM ( ");
		sb.append(" 		SELECT ");
		sb.append(" 			 [XML] = ( ");
		sb.append(" 				 ");
		sb.append(" 	select  ");
		sb.append(" 	CONVERT(varchar(100), L.STR_NO)+' '+ ");
		sb.append(" 	LS.STR_NAME+' '+ LS.STR_TYPE+' '+ CASE WHEN PRE_DIR is null THEN '' ELSE PRE_DIR END+''+CASE WHEN L.UNIT is null then '' ELSE L.UNIT END as  ");
		sb.append(" 	lso_address,APN as lso_APN ");
		sb.append("  ");
		sb.append(" 	FOR XML RAW('f'), TYPE ");
		sb.append(" 	) ");
		sb.append(" 	from ACTIVITY A ");
		sb.append(" 	join REF_LSO_PROJECT R on A.PROJECT_ID=R.PROJECT_ID ");
		sb.append(" 	join LSO L on R.LSO_ID=L.ID ");
		sb.append(" 	join LSO_STREET LS on L.LSO_STREET_ID=LS.ID ");
		sb.append(" 	left outer join REF_LSO_APN LA on L.ID =LA.LSO_ID ");
		sb.append(" 	WHERE  A.ID = ").append(typeid).append("  ");
		sb.append("  ");
		sb.append(" 	) p ");
		sb.append(" 	CROSS APPLY p.[XML].nodes('f/@*') t(c) ");
		sb.append("  ");
		sb.append(" 	union  ");
		sb.append("  ");
		sb.append(" 	select  ");
		sb.append(" 	'division_'+''+LDT.TYPE as LABEL ");
		sb.append(" 	,LD.DIVISION as FIELDVALUE ");
		sb.append(" 	from ACTIVITY A ");
		sb.append(" 	join REF_LSO_PROJECT R on A.PROJECT_ID=R.PROJECT_ID ");
		sb.append(" 	left outer join REF_LSO_DIVISIONS RLD on R.LSO_ID= RLD.LSO_ID  ");
		sb.append(" 	left outer join LKUP_DIVISIONS LD on RLD.LKUP_DIVISIONS_ID= LD.ID and LD.ACTIVE='Y' ");
		sb.append(" 	left outer join LKUP_DIVISIONS_TYPE LDT on LD.LKUP_DIVISIONS_TYPE_ID=LDT.ID ");
		sb.append(" 	WHERE  A.ID = ").append(typeid).append("  ");
		/*sb.append("  ) ");
		sb.append(" select ( ");
				sb.append(" select * from Q	 ");
						sb.append(" FOR JSON PATH, INCLUDE_NULL_VALUES  ) as divisions_lso ");*/
		
		
		return sb.toString();
	}	
	
	
	public static String printOld(String type, int typeid) {
		StringBuilder sb = new StringBuilder();
		 sb.append(" WITH Q AS  (     ");
		 sb.append(" 	SELECT      TOP 1      ATS.ACTIVITY_ID,      ATS.LKUP_ACT_STATUS_ID,     LAS.STATUS     ");
		 sb.append(" 	FROM      ACT_STATUS ATS       ");
		 sb.append(" 	LEFT OUTER JOIN LKUP_ACT_STATUS LAS on ATS.LKUP_ACT_STATUS_ID = LAS.ID     ");
		 sb.append(" 	WHERE      ATS.ACTIVITY_ID = ").append(typeid).append("      AND      ATS.ACTIVE = 'Y'     ");
		 sb.append(" 	ORDER BY      DATE DESC  ");
		 sb.append(" 	)   ");
		 
		 sb.append(" , AD AS (     ");
		 sb.append(" 	 select A.ID, CONVERT(varchar(100), L.STR_NO)+' '+  LS.STR_NAME+' '+ LS.STR_TYPE+' '+ CASE WHEN PRE_DIR is null THEN '' ELSE PRE_DIR END+''+CASE WHEN L.UNIT is null then '' ELSE L.UNIT END as ADDRESS,APN  ");
		 sb.append(" 	from ACTIVITY A        ");
		 sb.append(" 	 join REF_LSO_PROJECT R on A.PROJECT_ID=R.PROJECT_ID      ");
		 sb.append(" 	 join LSO L on R.LSO_ID=L.ID      ");
		 sb.append(" 	  join LSO_STREET LS on L.LSO_STREET_ID=LS.ID     ");
		 sb.append(" 	 left outer join REF_LSO_APN LA on L.ID =LA.LSO_ID      ");
		 sb.append(" 	WHERE      A.ID = ").append(typeid).append("        ");
		 sb.append(" 	)   ");
		 
		 
		 
		 sb.append("  ");
		 sb.append(" SELECT ");
		 sb.append(" LABEL = t.c.value('local-name(.)', 'VARCHAR(100)') ");
		 sb.append(" ,FIELDVALUE = t.c.value('.', 'VARCHAR(500)') ");
		 sb.append(" 	 ");
		 sb.append(" FROM ( ");
		 sb.append(" 	SELECT ");
		 sb.append(" 		 [XML] = ( ");
		 sb.append(" 			  ");
		 sb.append(" 	 ");
		 sb.append(" 				SELECT A.ID as ").append(type).append("_ID");
		 sb.append(" 				, ");
		 sb.append(" 				ACT_NBR as ").append(type).append("_ACT_NBR");
		 sb.append(" 				, ");
		 sb.append(" 				'http://dev.beverlyhills.org/cs/?entity=lso&type=activity&reference=").append("'+ACT_NBR as QR_CODE");
		 sb.append(" 				, ");
		 sb.append(" 				Q.STATUS as ").append(type).append("_STATUS");
		 
		 sb.append(" 				, ");
		 sb.append(" 				LAT.DESCRIPTION as ").append(type).append("_TYPE");
		 sb.append(" 				, ");
		 sb.append(" 				A.DESCRIPTION as ").append(type).append("_DESCRIPTION");
		 sb.append(" 				, ");
		 sb.append(" 				A.VALUATION_CALCULATED as ").append(type).append("_VALUATION_CALCULATED");
		 sb.append(" 				, ");
		 sb.append(" 				A.VALUATION_DECLARED as ").append(type).append("_VALUATION_DECLARED");
		 sb.append(" 				, ");
		 sb.append(" 				A.START_DATE as ").append(type).append("_START_DATE");
		 sb.append(" 				, ");
		 sb.append(" 				A.APPLIED_DATE as ").append(type).append("_APPLIED_DATE");
		 sb.append(" 				, ");
		 sb.append(" 				A.ISSUED_DATE as ").append(type).append("_ISSUED_DATE");
		 sb.append(" 				, ");
		 sb.append(" 				A.EXP_DATE as ").append(type).append("_EXP_DATE");
		 sb.append(" 				, ");
		 sb.append(" 				AD.ADDRESS as ").append(type).append("_ADDRESS");
		 sb.append(" 				, ");
		 sb.append(" 				AD.APN as ").append(type).append("_APN");
		 sb.append(" 				, ");
		 sb.append(" 				AD.APN as ").append(type).append("_PARCEL_NUMBER");
		 
		 sb.append(" 				, ");
		 sb.append(" 				CU.USERNAME as ").append(type).append("_CREATED_BY");
		
		 sb.append(" 				, ");
		 sb.append(" 				UP.USERNAME as ").append(type).append("_UPDATED_BY");
		
		 sb.append(" 				, ");
		 sb.append(" 				P.PROJECT_NBR as ").append(type).append("_PROJECT_NBR");
		 sb.append(" 				, ");
		 sb.append(" 				P.NAME as ").append(type).append("_PROJECT_NAME");
		 
		 sb.append(" 				, ");
		 sb.append(" 				P.DESCRIPTION as ").append(type).append("_PROJECT_DESCRIPTION");
		 
		 
		 sb.append(" 				FOR XML RAW('f'), TYPE ");
		 sb.append(" 				 ");
		 sb.append(" 			) ");
		 sb.append(" 	FROM ACTIVITY A   ");
		 sb.append(" 	join PROJECT P on A.PROJECT_ID=P.ID  ");
		 sb.append(" 	LEFT OUTER JOIN Q on A.ID = Q.ACTIVITY_ID  ");
		 sb.append(" 	LEFT OUTER JOIN AD on A.ID = AD.ID ");
		 sb.append(" 	join LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID  ");
		 sb.append(" LEFT OUTER JOIN USERS AS CU ON A.CREATED_BY = CU.ID ");
		 sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID  ");
		 sb.append(" 	where A.ID = ").append(typeid).append("  ");
		 sb.append(" 	 ");
		 sb.append(" ) p ");
		 sb.append(" CROSS APPLY p.[XML].nodes('f/@*') t(c) ");
		
		return sb.toString();
	}

	public static String getDetails(String ids) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ");
		sb.append(" ( "); 
		sb.append("   SELECT "); 
		sb.append("     TOP 1 "); 
		sb.append("     ATS.ACTIVITY_ID, "); 
		sb.append("     ATS.LKUP_ACT_STATUS_ID,"); 
		sb.append("     LTRIM(RTRIM(LAS.STATUS)) STATUS"); 
		sb.append("   FROM "); 
		sb.append("     ").append(Table.ACTSTATUSTABLE).append(" ATS "); 
		sb.append("     LEFT OUTER JOIN LKUP_ACT_STATUS LAS on ATS.LKUP_ACT_STATUS_ID = LAS.ID ");
		sb.append("   WHERE "); 
		sb.append("     ATS.ACTIVITY_ID IN (").append(ids).append(") "); 
		sb.append("     AND "); 
		sb.append("     ATS.ACTIVE = 'Y' "); 
		sb.append("   ORDER BY "); 
		sb.append("     DATE DESC");
		sb.append(" ) ");
					
		sb.append(" SELECT "); 
		sb.append("   A.*,"); 
		sb.append("   ATT.DESCRIPTION AS TYPE, "); 
		//sb.append("   ATT.DESCRIPTION AS LKUP_ACT_TYPE_ID_TEXT, "); 
		
		sb.append("   A.ACT_NBR AS SUBTITLE, "); 
		sb.append("   LTRIM(RTRIM(LAS.STATUS)) STATUS, "); 
		sb.append("   CU.USERNAME AS CREATED, "); 
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" FROM "); 
		sb.append("   ").append(Table.ACTIVITYTABLE).append(" A ");
		sb.append("   JOIN  ").append(Table.ACTTYPETABLE).append(" ATT ON A.LKUP_ACT_TYPE_ID = ATT.ID ");
		sb.append("   JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID = LAS.ID ");
//		sb.append("   LEFT OUTER JOIN Q on A.ID = Q.ACTIVITY_ID ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 

		sb.append(" WHERE "); 
		sb.append("   A.ID in (").append(ids).append(") "); 
		sb.append("   AND "); 
		sb.append("   A.ACTIVE = 'Y' ");
		
		return sb.toString();
	}

	public static String updateVal(HashMap<String, String> map) {
		String acttypeid = map.get("LKUP_ACT_TYPE_ID");
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" T.INHERIT ");
		sb.append(" FROM ");
		sb.append(" LKUP_ACT_TYPE AS T ");
		sb.append(" WHERE ");
		sb.append(" T.ID = ").append(acttypeid);
		return sb.toString();
	}

	public static String getAutoAdd(int projectid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.LKUP_ACT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" T.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" T.TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" T.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.INHERIT ");
		sb.append(" , ");
		sb.append(" T.YEARS_TILL_EXPIRED ");
		sb.append(" , ");
		sb.append(" T.DAYS_TILL_EXPIRED ");
		sb.append(" , ");
		sb.append(" S.LKUP_ACT_STATUS_ID ");
		sb.append(" FROM ");
		sb.append(" REF_PROJECT_ACT_TYPE AS R ");
		sb.append(" JOIN PROJECT AS P ON R.LKUP_PROJECT_TYPE_ID = P.LKUP_PROJECT_TYPE_ID AND R.ACTIVE = 'Y' AND R.AUTO_ADD = 'Y' AND P.ID = ").append(projectid);
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON R.LKUP_ACT_TYPE_ID = T.ID ");
		sb.append(" JOIN REF_ACT_TYPE_STATUS AS S ON S.LKUP_ACT_TYPE_ID IN (-1, R.LKUP_ACT_TYPE_ID) AND S.ACTIVE = 'Y' AND S.DEFLT = 'Y' ");
		return sb.toString();
	}

	public static String getAutoAdd(String[] acttypeids) {
		if (!Operator.hasValue(acttypeids)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" T.ID ");
		sb.append(" , ");
		sb.append(" T.ID AS LKUP_ACT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" T.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" T.TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" T.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.YEARS_TILL_EXPIRED ");
		sb.append(" , ");
		sb.append(" T.DAYS_TILL_EXPIRED ");
		sb.append(" , ");
		sb.append(" (SELECT TOP 1 SS.LKUP_ACT_STATUS_ID FROM REF_ACT_TYPE_STATUS AS SS WHERE SS.LKUP_ACT_TYPE_ID IN (-1, T.ID) ORDER BY SS.DEFLT DESC, SS.LKUP_ACT_TYPE_ID DESC) AS LKUP_ACT_STATUS_ID ");
		sb.append(" FROM ");
		sb.append(" LKUP_ACT_TYPE AS T ");
		sb.append(" WHERE ");
		sb.append(" T.ID IN ( ");
		boolean empty = true;
		for (int i=0; i<acttypeids.length; i++) {
			if (!empty) { sb.append(" , "); }
			int t = Operator.toInt(acttypeids[i]);
			if (t > 0) {
				sb.append(t);
				empty = false;
			}
		}
		sb.append(" ) ");
		if (empty) { return ""; }
		return sb.toString();
	}

	public static String getAutoAddByProjectType(String entity, int entityid, int projecttypeid) {
		String tableref = CsReflect.getTableRef(entity);
		String idref = CsReflect.getFieldIdRef(entity);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.ID AS VALUE ");
		sb.append(" 		, ");
		sb.append(" 		T.TYPE AS TEXT ");
		sb.append(" 		, ");
		sb.append(" 		T.DESCRIPTION ");
		sb.append(" 		, ");
		sb.append(" 		T.INHERIT ");
		sb.append(" 		, ");
		sb.append(" 		PT.ORDR ");
		sb.append(" 		, ");
		sb.append(" 		COUNT(DISTINCT RAD.LKUP_DIVISIONS_ID) AS DIVS ");
		sb.append(" 	FROM ");
		sb.append(" 		LKUP_ACT_TYPE AS T ");
		sb.append(" 		JOIN REF_PROJECT_ACT_TYPE AS PT ON T.ID = PT.LKUP_ACT_TYPE_ID AND PT.ACTIVE = 'Y' AND T.ACTIVE = 'Y' AND PT.AUTO_ADD = 'Y' AND PT.LKUP_PROJECT_TYPE_ID = ").append(projecttypeid);
		sb.append(" 		LEFT OUTER JOIN REF_ACT_DIVISIONS AS RAD ON T.ID = RAD.LKUP_ACT_TYPE_ID AND RAD.ACTIVE = 'Y' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.TYPE ");
		sb.append(" 		, ");
		sb.append(" 		T.DESCRIPTION ");
		sb.append(" 		, ");
		sb.append(" 		T.INHERIT ");
		sb.append(" 		, ");
		sb.append(" 		PT.ORDR ");
		sb.append(" ), ");
		sb.append(" U AS ( ");
		sb.append(" 	SELECT * FROM Q WHERE DIVS = 0 ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		Q.* ");
		sb.append(" 	FROM Q ");
		sb.append(" 		JOIN REF_ACT_DIVISIONS AS RAD ON Q.ID = RAD.LKUP_ACT_TYPE_ID AND Q.DIVS > 0 ");
		sb.append(" 		JOIN REF_").append(tableref).append("_DIVISIONS AS L ON RAD.LKUP_DIVISIONS_ID = L.LKUP_DIVISIONS_ID AND L.ACTIVE = 'Y' AND L.").append(idref).append(" = ").append(entityid).append(" ");
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	ID ");
		sb.append(" 	, ");
		sb.append(" 	VALUE ");
		sb.append(" 	, ");
		sb.append(" 	TEXT ");
		sb.append(" 	, ");
		sb.append(" 	DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	INHERIT ");
		sb.append(" 	, ");
		sb.append(" 	ORDR ");
		sb.append(" FROM U ");
		sb.append(" ORDER BY ORDR, TEXT ");
		return sb.toString();
	}

	public static String status(int acttypeid, int actid) {
		if (acttypeid < 1) { return status(actid); }
		StringBuilder sb = new StringBuilder();
		
		
		sb.append(" WITH Q AS ( ");

		sb.append(" SELECT ");
		sb.append(" S.ID ");
		sb.append(" , ");
		sb.append(" S.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" S.STATUS AS TEXT ");
		sb.append(" , ");
		sb.append(" S.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" S.ISSUED ");
		sb.append(" , ");
		sb.append(" S.FINAL ");
		sb.append(" , ");
		if (actid > 0) {
			sb.append(" CASE WHEN A.ID IS NOT NULL THEN 'Y' ELSE 'N' END AS SELECTED ");
		}
		else {
			sb.append(" R.DEFLT AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" LKUP_ACT_TYPE AS T ");
		sb.append(" JOIN REF_ACT_TYPE_STATUS AS R ON R.LKUP_ACT_TYPE_ID = T.ID AND R.ACTIVE = 'Y' AND T.ACTIVE = 'Y' AND T.ID = ").append(acttypeid);
		sb.append(" JOIN LKUP_ACT_STATUS AS S ON R.LKUP_ACT_STATUS_ID = S.ID AND S.ACTIVE = 'Y' ");
		if (actid > 0) {
			sb.append(" LEFT OUTER JOIN ACTIVITY AS A ON T.ID = A.LKUP_ACT_TYPE_ID AND A.ID = ").append(actid);
		}

		sb.append(" ), C AS ( ");
		sb.append(" SELECT COUNT(DISTINCT ID) AS NUMSELECTED FROM Q WHERE SELECTED = 'Y' ");
		sb.append(" ), U AS ( ");

		sb.append(" SELECT ");
		sb.append(" S.ID ");
		sb.append(" , ");
		sb.append(" S.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" S.STATUS AS TEXT ");
		sb.append(" , ");
		sb.append(" S.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" S.ISSUED ");
		sb.append(" , ");
		sb.append(" S.FINAL ");
		sb.append(" , ");
		if (actid > 0) {
			sb.append(" CASE WHEN A.ID IS NOT NULL THEN 'Y' ELSE 'N' END AS SELECTED ");
		}
		else {
			sb.append(" CASE WHEN C.NUMSELECTED > 0 THEN 'N' ELSE R.DEFLT END AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" REF_ACT_TYPE_STATUS AS R ");
		sb.append(" JOIN LKUP_ACT_STATUS AS S ON R.LKUP_ACT_STATUS_ID = S.ID AND S.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND R.LKUP_ACT_TYPE_ID = -1  AND S.ID NOT IN (SELECT Q.ID FROM Q) ");
		sb.append(" JOIN C ON 1=1 ");
		if (actid > 0) {
			sb.append(" LEFT OUTER JOIN ACTIVITY AS A ON R.LKUP_ACT_TYPE_ID = A.LKUP_ACT_TYPE_ID AND A.ID = ").append(actid);
		}

		if (actid > 0) {
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" S.ID ");
			sb.append(" , ");
			sb.append(" S.ID AS VALUE ");
			sb.append(" , ");
			sb.append(" S.STATUS AS TEXT ");
			sb.append(" , ");
			sb.append(" S.DESCRIPTION ");
			sb.append(" , ");
			sb.append(" S.ISSUED ");
			sb.append(" , ");
			sb.append(" S.FINAL ");
			sb.append(" , ");
			sb.append(" 'Y' AS SELECTED ");
			sb.append(" FROM ");
			sb.append(" ACTIVITY AS A ");
			sb.append(" JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID AND S.ACTIVE = 'Y' AND A.ID = ").append(actid);
		}

		sb.append(" ) ");
		sb.append(" SELECT * FROM Q UNION SELECT * FROM U  ORDER BY TEXT ");
		return sb.toString();
	}

	public static String status(int actid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH ");
		sb.append(" L AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		S.ID ");
		sb.append(" 		, ");
		sb.append(" 		S.ID AS VALUE ");
		sb.append(" 		, ");
		sb.append(" 		S.STATUS AS TEXT ");
		sb.append(" 		, ");
		sb.append(" 		S.DESCRIPTION ");
		sb.append(" 		, ");
		sb.append(" 		S.ISSUED ");
		sb.append(" 		, ");
		sb.append(" 		S.FINAL ");
		sb.append(" 		, ");
		sb.append(" 		R.DEFLT ");
		if (actid > 0) {
			sb.append(" 		, ");
			sb.append(" 		CASE WHEN A.LKUP_ACT_STATUS_ID = S.ID THEN 'Y' ELSE 'N' END AS SELECTED ");
		}
		else {
			sb.append(" 		, ");
			sb.append(" 		'N' AS SELECTED ");
		}
		sb.append(" 	FROM ");
		sb.append(" 		ACTIVITY AS A ");
		sb.append(" 		JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND A.ID = ").append(actid);
		sb.append(" 		JOIN REF_ACT_TYPE_STATUS AS R ON R.LKUP_ACT_TYPE_ID = T.ID AND R.ACTIVE = 'Y' AND T.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_ACT_STATUS AS S ON R.LKUP_ACT_STATUS_ID = S.ID AND S.ACTIVE = 'Y' ");
		sb.append(" ) ");
		sb.append(" , A AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		S.ID ");
		sb.append(" 		, ");
		sb.append(" 		S.ID AS VALUE ");
		sb.append(" 		, ");
		sb.append(" 		S.STATUS AS TEXT ");
		sb.append(" 		, ");
		sb.append(" 		S.DESCRIPTION ");
		sb.append(" 		, ");
		sb.append(" 		S.ISSUED ");
		sb.append(" 		, ");
		sb.append(" 		S.FINAL ");
		sb.append(" 		, ");
		sb.append(" 		R.DEFLT ");
		if (actid > 0) {
			sb.append(" 		, ");
			sb.append(" 		CASE WHEN A.ID IS NOT NULL THEN 'Y' ELSE 'N' END AS SELECTED ");
		}
		else {
			sb.append(" 		, ");
			sb.append(" 		'N' AS SELECTED ");
		}
		sb.append(" 	FROM ");
		sb.append(" 		REF_ACT_TYPE_STATUS AS R ");
		sb.append(" 		JOIN LKUP_ACT_STATUS AS S ON R.LKUP_ACT_STATUS_ID = S.ID AND S.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND R.LKUP_ACT_TYPE_ID = -1 ");
		sb.append(" 		LEFT OUTER JOIN ACTIVITY AS A ON R.LKUP_ACT_TYPE_ID = A.LKUP_ACT_TYPE_ID AND A.ID = ").append(actid);
		sb.append(" ) ");
		sb.append(" , Q AS ( ");
		sb.append(" SELECT DISTINCT * FROM L WHERE ID NOT IN (SELECT ID FROM A WHERE SELECTED = 'Y') ");
		sb.append(" UNION ");
		sb.append(" SELECT DISTINCT * FROM A WHERE ID NOT IN (SELECT ID FROM L) AND SELECTED = 'N' ");
		sb.append(" UNION ");
		sb.append(" SELECT DISTINCT * FROM A WHERE SELECTED = 'Y' ");
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT * FROM Q ORDER BY TEXT ");
		return sb.toString();
	}

	public static String actType(String type, int typeid) {
		return actType(type, typeid, false);
	}

	public static String actType(String type, int typeid, boolean exemption) {
		if (Operator.equalsIgnoreCase(type, "project")) {
			TypeInfo e = EntityAgent.getEntity(type, typeid);
			return projectActType(e.getProjectid(), exemption);
		}
		else if (Operator.equalsIgnoreCase(type, "activity")) {
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT ");
			sb.append(" T.ID ");
			sb.append(" , ");
			sb.append(" T.ID AS VALUE ");
			sb.append(" , ");
			sb.append(" T.TYPE AS TEXT ");
			sb.append(" , ");
			sb.append(" T.DESCRIPTION ");
			sb.append(" , ");
			sb.append(" 'Y' AS SELECTED ");
			sb.append(" FROM ");
			sb.append(" ACTIVITY AS A ");
			sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND A.ID = ").append(typeid);
			return sb.toString();
		}
		else {
			return "";
		}
	}

	public static String projectActType(int projectid) {
		return projectActType(projectid, false);
	}

	/**
	 * @deprecated Use projectActType(int projectid, boolean exemption)
	 * @return
	 */
	public static String projectActType1(String entity, int entityid, int projectid, boolean exemption) {
		String tableref = CsReflect.getTableRef(entity);
		String idref = CsReflect.getFieldIdRef(entity);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.ID AS VALUE ");
		sb.append(" 		, ");
		sb.append(" 		T.TYPE AS TEXT ");
		sb.append(" 		, ");
		sb.append(" 		T.DESCRIPTION ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.YEARS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_APPLICATION_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.MONTH_START ");
		sb.append(" 		, ");
		sb.append(" 		T.DAY_START ");
		sb.append(" 		, ");
		sb.append(" 		PT.ORDR ");
		sb.append(" 		, ");
		sb.append(" 		'ACTIVITY' AS ROLE_TYPE ");
		sb.append(" 		, ");
		sb.append(" 		T.ID AS ROLE_TYPE_ID ");
		sb.append(" 		, ");
		sb.append(" 		COUNT(DISTINCT RAD.LKUP_DIVISIONS_ID) AS DIVS ");
		sb.append(" 	FROM ");
		sb.append(" 		LKUP_ACT_TYPE AS T ");
		sb.append(" 		JOIN REF_PROJECT_ACT_TYPE AS PT ON T.ID = PT.LKUP_ACT_TYPE_ID AND PT.ACTIVE = 'Y' AND T.ACTIVE = 'Y' ");
		if (exemption) {
			sb.append(" AND T.DOT_EXEMPTION = 'Y' ");
		}
		else {
			sb.append(" AND T.DOT_EXEMPTION = 'N' ");
		}
		sb.append(" 		JOIN PROJECT AS P ON PT.LKUP_PROJECT_TYPE_ID = P.LKUP_PROJECT_TYPE_ID AND P.ID = ").append(projectid).append(" ");
		sb.append(" 		LEFT OUTER JOIN REF_ACT_DIVISIONS AS RAD ON T.ID = RAD.LKUP_ACT_TYPE_ID AND RAD.ACTIVE = 'Y' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.TYPE ");
		sb.append(" 		, ");
		sb.append(" 		T.DESCRIPTION ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.YEARS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_APPLICATION_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.MONTH_START ");
		sb.append(" 		, ");
		sb.append(" 		T.DAY_START ");
		sb.append(" 		, ");
		sb.append(" 		PT.ORDR ");
		sb.append(" ), ");
		sb.append(" U AS ( ");
		sb.append(" 	SELECT * FROM Q WHERE DIVS = 0 ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		Q.* ");
		sb.append(" 	FROM Q ");
		sb.append(" 		JOIN REF_ACT_DIVISIONS AS RAD ON Q.ID = RAD.LKUP_ACT_TYPE_ID AND Q.DIVS > 0 ");
		sb.append(" 		JOIN REF_").append(tableref).append("_DIVISIONS AS L ON RAD.LKUP_DIVISIONS_ID = L.LKUP_DIVISIONS_ID AND RAD.ACTIVE = 'Y' AND L.ACTIVE = 'Y' AND L.").append(idref).append(" = ").append(entityid).append(" ");
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	ID ");
		sb.append(" 	, ");
		sb.append(" 	VALUE ");
		sb.append(" 	, ");
		sb.append(" 	TEXT ");
		sb.append(" 	, ");
		sb.append(" 	DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	DAYS_TILL_EXPIRED ");
		sb.append(" 	, ");
		sb.append(" 	YEARS_TILL_EXPIRED ");
		sb.append(" 	, ");
		sb.append(" 	DAYS_TILL_APPLICATION_EXPIRED ");
		sb.append(" 	, ");
		sb.append(" 	MONTH_START ");
		sb.append(" 	, ");
		sb.append(" 	DAY_START ");
		sb.append(" 	, ");
		sb.append(" 	ROLE_TYPE ");
		sb.append(" 	, ");
		sb.append(" 	ROLE_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	ORDR ");
		sb.append(" FROM U ");
		sb.append(" ORDER BY ORDR, TEXT ");
		return sb.toString();
	}

	public static String projectActType(int projectid, boolean exemption) {
		DivisionsList l = DivisionsAgent.getDivisions("project", projectid);
		String divids = l.divisionIds();

		StringBuilder sb = new StringBuilder();
		sb.append(" WITH ");
		sb.append(" Q AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.ID AS VALUE ");
		sb.append(" 		, ");
		sb.append(" 		T.TYPE AS TEXT ");
		sb.append(" 		, ");
		sb.append(" 		T.DESCRIPTION ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.YEARS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_APPLICATION_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.MONTH_START ");
		sb.append(" 		, ");
		sb.append(" 		T.DAY_START ");
		sb.append(" 		, ");
		sb.append(" 		PT.ORDR ");
		sb.append(" 		, ");
		sb.append(" 		'ACTIVITY' AS ROLE_TYPE ");
		sb.append(" 		, ");
		sb.append(" 		T.ID AS ROLE_TYPE_ID ");
		sb.append(" 		, ");
		sb.append(" 		COUNT(DISTINCT RAD.LKUP_DIVISIONS_ID) AS DIVS ");
		sb.append(" 	FROM ");
		sb.append(" 		LKUP_ACT_TYPE AS T ");
		sb.append(" 		JOIN REF_PROJECT_ACT_TYPE AS PT ON T.ID = PT.LKUP_ACT_TYPE_ID AND PT.ACTIVE = 'Y' AND T.ACTIVE = 'Y' ");
		if (exemption) {
			sb.append(" AND T.DOT_EXEMPTION = 'Y' ");
		}
		else {
			sb.append(" AND T.DOT_EXEMPTION = 'N' ");
		}
		sb.append(" 		JOIN PROJECT AS P ON PT.LKUP_PROJECT_TYPE_ID = P.LKUP_PROJECT_TYPE_ID AND P.ID = ").append(projectid).append(" ");
		sb.append(" 		LEFT OUTER JOIN REF_ACT_DIVISIONS AS RAD ON T.ID = RAD.LKUP_ACT_TYPE_ID AND RAD.ACTIVE = 'Y' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.TYPE ");
		sb.append(" 		, ");
		sb.append(" 		T.DESCRIPTION ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.YEARS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_APPLICATION_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.MONTH_START ");
		sb.append(" 		, ");
		sb.append(" 		T.DAY_START ");
		sb.append(" 		, ");
		sb.append(" 		PT.ORDR ");
		sb.append(" ), ");
		sb.append(" U AS ( ");
		sb.append(" 	SELECT * FROM Q WHERE DIVS = 0 ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		Q.* ");
		sb.append(" 	FROM Q ");
		sb.append(" 		JOIN REF_ACT_DIVISIONS AS RAD ON Q.ID = RAD.LKUP_ACT_TYPE_ID AND RAD.ACTIVE = 'Y' AND Q.DIVS > 0 AND RAD.LKUP_DIVISIONS_ID IN (").append(divids).append(") ");
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	ID ");
		sb.append(" 	, ");
		sb.append(" 	VALUE ");
		sb.append(" 	, ");
		sb.append(" 	TEXT ");
		sb.append(" 	, ");
		sb.append(" 	DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	DAYS_TILL_EXPIRED ");
		sb.append(" 	, ");
		sb.append(" 	YEARS_TILL_EXPIRED ");
		sb.append(" 	, ");
		sb.append(" 	DAYS_TILL_APPLICATION_EXPIRED ");
		sb.append(" 	, ");
		sb.append(" 	MONTH_START ");
		sb.append(" 	, ");
		sb.append(" 	DAY_START ");
		sb.append(" 	, ");
		sb.append(" 	ROLE_TYPE ");
		sb.append(" 	, ");
		sb.append(" 	ROLE_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	ORDR ");
		sb.append(" FROM U ");
		sb.append(" ORDER BY ORDR, TEXT ");
		return sb.toString();
	}

	/**
	 * @deprecated Use renewalActType(int projectid)
	 */
	public static String renewalActType1(String entity, int entityid, int projectid) {
		String tableref = CsReflect.getTableRef(entity);
		String idref = CsReflect.getFieldIdRef(entity);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.ID AS VALUE ");
		sb.append(" 		, ");
		sb.append(" 		T.TYPE AS TEXT ");
		sb.append(" 		, ");
		sb.append(" 		T.DESCRIPTION ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.YEARS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_APPLICATION_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.MONTH_START ");
		sb.append(" 		, ");
		sb.append(" 		T.DAY_START ");
		sb.append(" 		, ");
		sb.append(" 		PT.ORDR ");
		sb.append(" 		, ");
		sb.append(" 		'ACTIVITY' AS ROLE_TYPE ");
		sb.append(" 		, ");
		sb.append(" 		T.ID AS ROLE_TYPE_ID ");
		sb.append(" 		, ");
		sb.append(" 		COUNT(DISTINCT RAD.LKUP_DIVISIONS_ID) AS DIVS ");
		sb.append(" 	FROM ");
		sb.append(" 		LKUP_ACT_TYPE AS T ");
		sb.append(" 		JOIN REF_PROJECT_ACT_TYPE AS PT ON T.ID = PT.LKUP_ACT_TYPE_ID AND PT.ACTIVE = 'Y' AND T.ACTIVE = 'Y' AND T.RENEWAL = 'Y' ");
		sb.append(" 		JOIN PROJECT AS P ON PT.LKUP_PROJECT_TYPE_ID = P.LKUP_PROJECT_TYPE_ID AND P.ID = ").append(projectid).append(" ");
		sb.append(" 		LEFT OUTER JOIN REF_ACT_DIVISIONS AS RAD ON T.ID = RAD.LKUP_ACT_TYPE_ID AND RAD.ACTIVE = 'Y' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.TYPE ");
		sb.append(" 		, ");
		sb.append(" 		T.DESCRIPTION ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.YEARS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_APPLICATION_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.MONTH_START ");
		sb.append(" 		, ");
		sb.append(" 		T.DAY_START ");
		sb.append(" 		, ");
		sb.append(" 		PT.ORDR ");
		sb.append(" ), ");
		sb.append(" U AS ( ");
		sb.append(" 	SELECT * FROM Q WHERE DIVS = 0 ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		Q.* ");
		sb.append(" 	FROM ");
		sb.append(" 		V_CENTRAL_ADDRESS AS V ");
		sb.append(" 		JOIN REF_LSO_DIVISIONS AS D ON V.LAND_ID = D.LSO_ID AND D.ACTIVE = 'Y' AND V.LSO_ID = 20765 ");
		sb.append(" 		JOIN REF_ACT_DIVISIONS AS RAD ON RAD.LKUP_DIVISIONS_ID = D.LKUP_DIVISIONS_ID AND RAD.ACTIVE = 'Y' ");
		sb.append(" 		JOIN Q ON Q.ID = RAD.LKUP_ACT_TYPE_ID AND Q.DIVS > 0 ");
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	ID ");
		sb.append(" 	, ");
		sb.append(" 	VALUE ");
		sb.append(" 	, ");
		sb.append(" 	TEXT ");
		sb.append(" 	, ");
		sb.append(" 	DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	DAYS_TILL_EXPIRED ");
		sb.append(" 	, ");
		sb.append(" 	YEARS_TILL_EXPIRED ");
		sb.append(" 	, ");
		sb.append(" 	DAYS_TILL_APPLICATION_EXPIRED ");
		sb.append(" 	, ");
		sb.append(" 	MONTH_START ");
		sb.append(" 	, ");
		sb.append(" 	DAY_START ");
		sb.append(" 	, ");
		sb.append(" 	ROLE_TYPE ");
		sb.append(" 	, ");
		sb.append(" 	ROLE_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	ORDR ");
		sb.append(" FROM U ");
		sb.append(" ORDER BY ORDR, TEXT ");
		return sb.toString();
	}

	public static String renewalActType(int projectid) {
		DivisionsList l = DivisionsAgent.getDivisions("project", projectid);
		String divids = l.divisionIds();
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.ID AS VALUE ");
		sb.append(" 		, ");
		sb.append(" 		T.TYPE AS TEXT ");
		sb.append(" 		, ");
		sb.append(" 		T.DESCRIPTION ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.YEARS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_APPLICATION_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.MONTH_START ");
		sb.append(" 		, ");
		sb.append(" 		T.DAY_START ");
		sb.append(" 		, ");
		sb.append(" 		PT.ORDR ");
		sb.append(" 		, ");
		sb.append(" 		'ACTIVITY' AS ROLE_TYPE ");
		sb.append(" 		, ");
		sb.append(" 		T.ID AS ROLE_TYPE_ID ");
		sb.append(" 		, ");
		sb.append(" 		COUNT(DISTINCT RAD.LKUP_DIVISIONS_ID) AS DIVS ");
		sb.append(" 	FROM ");
		sb.append(" 		LKUP_ACT_TYPE AS T ");
		sb.append(" 		JOIN REF_PROJECT_ACT_TYPE AS PT ON T.ID = PT.LKUP_ACT_TYPE_ID AND PT.ACTIVE = 'Y' AND T.ACTIVE = 'Y' AND T.RENEWAL = 'Y' ");
		sb.append(" 		JOIN PROJECT AS P ON PT.LKUP_PROJECT_TYPE_ID = P.LKUP_PROJECT_TYPE_ID AND P.ID = ").append(projectid).append(" ");
		sb.append(" 		LEFT OUTER JOIN REF_ACT_DIVISIONS AS RAD ON T.ID = RAD.LKUP_ACT_TYPE_ID AND RAD.ACTIVE = 'Y' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.ID ");
		sb.append(" 		, ");
		sb.append(" 		T.TYPE ");
		sb.append(" 		, ");
		sb.append(" 		T.DESCRIPTION ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.YEARS_TILL_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.DAYS_TILL_APPLICATION_EXPIRED ");
		sb.append(" 		, ");
		sb.append(" 		T.MONTH_START ");
		sb.append(" 		, ");
		sb.append(" 		T.DAY_START ");
		sb.append(" 		, ");
		sb.append(" 		PT.ORDR ");
		sb.append(" ), ");
		sb.append(" U AS ( ");
		sb.append(" 	SELECT * FROM Q WHERE DIVS = 0 ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		Q.* ");
		sb.append(" 	FROM Q ");
		sb.append(" 		JOIN REF_ACT_DIVISIONS AS RAD ON Q.ID = RAD.LKUP_ACT_TYPE_ID AND Q.DIVS > 0 AND RAD.LKUP_DIVISIONS_ID IN (").append(divids).append(") ");
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	ID ");
		sb.append(" 	, ");
		sb.append(" 	VALUE ");
		sb.append(" 	, ");
		sb.append(" 	TEXT ");
		sb.append(" 	, ");
		sb.append(" 	DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	DAYS_TILL_EXPIRED ");
		sb.append(" 	, ");
		sb.append(" 	YEARS_TILL_EXPIRED ");
		sb.append(" 	, ");
		sb.append(" 	DAYS_TILL_APPLICATION_EXPIRED ");
		sb.append(" 	, ");
		sb.append(" 	MONTH_START ");
		sb.append(" 	, ");
		sb.append(" 	DAY_START ");
		sb.append(" 	, ");
		sb.append(" 	ROLE_TYPE ");
		sb.append(" 	, ");
		sb.append(" 	ROLE_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	ORDR ");
		sb.append(" FROM U ");
		sb.append(" ORDER BY ORDR, TEXT ");
		return sb.toString();
	}

	public static String inheritExpiration(int projectid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" 	ACTIVITY ");
		sb.append(" SET ");
		sb.append(" 	EXP_DATE = ( ");
		sb.append(" 		SELECT ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN YEARS_TILL_EXPIRED > 0 AND DAYS_TILL_EXPIRED > 0 THEN DATEADD(yy,YEARS_TILL_EXPIRED,getdate()) + DAYS_TILL_EXPIRED ");
		sb.append(" 				WHEN YEARS_TILL_EXPIRED > 0 THEN DATEADD(yy,YEARS_TILL_EXPIRED,getdate()) ");
		sb.append(" 				WHEN DAYS_TILL_EXPIRED > 0 THEN getDate() + DAYS_TILL_EXPIRED ");
		sb.append(" 			ELSE getDate() END AS EXP_DATE ");
		sb.append(" 		FROM ");
		sb.append(" 			LKUP_ACT_TYPE WHERE LKUP_ACT_TYPE.ID = LKUP_ACT_TYPE_ID ");
		sb.append(" 		) ");
		sb.append(" WHERE ");
		sb.append(" 	INHERIT = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	PROJECT_ID = ").append(projectid);
		return sb.toString();
	}

	public static String getExpiration(int actid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN YEARS_TILL_EXPIRED > 0 AND DAYS_TILL_EXPIRED > 0 THEN DATEADD(yy,YEARS_TILL_EXPIRED,getdate()) + DAYS_TILL_EXPIRED ");
		sb.append(" 				WHEN YEARS_TILL_EXPIRED > 0 THEN DATEADD(yy,YEARS_TILL_EXPIRED,getdate()) ");
		sb.append(" 				WHEN DAYS_TILL_EXPIRED > 0 THEN getDate() + DAYS_TILL_EXPIRED ");
		sb.append(" 			ELSE getDate() END AS EXP_DATE ");
		sb.append(" 		FROM ");
		sb.append(" 			ACTIVITY JOIN LKUP_ACT_TYPE ON LKUP_ACT_TYPE.ID = LKUP_ACT_TYPE_ID ");
		sb.append(" WHERE ");
		sb.append(" 	ACTIVITY.INHERIT = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	ACTIVITY.ID = ").append(actid);
		return sb.toString();
	}

	public static String getNullExpiration(int projectid) {
		if (projectid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM ACTIVITY WHERE EXP_DATE IS NULL AND ACTIVE = 'Y' AND PROJECT_ID = ").append(projectid);
		return sb.toString();
	}

	
	
	
	//TODO extend type if needed 
	public static String getDivisions(int projectId,int acttypeid,String divids){
		StringBuilder sb = new StringBuilder();
		/*sb.append(" WITH LAND AS ( ");
		sb.append(" SELECT ");
		sb.append(" DISTINCT  ");
		sb.append(" CASE ");
		sb.append(" WHEN P.PARENT_ID is null AND  P.ID is null THEN M.ID ");
		sb.append(" WHEN P.PARENT_ID =-1 THEN P.ID ");
		sb.append(" WHEN P.PARENT_ID >0 THEN P.PARENT_ID ");
		sb.append(" END AS LAND_ID ");
		sb.append(" FROM ");
		sb.append(" LSO AS M ");
		sb.append(" LEFT OUTER JOIN LSO AS P ON M.PARENT_ID = P.ID ");
		sb.append(" LEFT OUTER JOIN LSO AS C ON M.ID = C.PARENT_ID ");
		sb.append(" LEFT OUTER JOIN LSO AS GC ON C.ID = GC.PARENT_ID ");
		sb.append(" LEFT OUTER JOIN REF_LSO_PROJECT  AS RLP  on RLP.LSO_ID=M.ID ");
		sb.append(" WHERE ");
		sb.append(" RLP.PROJECT_ID=").append(projectId);
		sb.append(" ), CURRENT_SETTINGS AS ( ");
		sb.append(" SELECT  LD.* from LAND L LEFT OUTER JOIN REF_LSO_DIVISIONS RLD on L.LAND_ID=RLD.LSO_ID ");
		sb.append(" left outer join LKUP_DIVISIONS LD on RLD.LKUP_DIVISIONS_ID = LD.ID ");
		
		sb.append(" ) ");
		sb.append(" select CASE WHEN RAD.ID is not null and RAD.LKUP_DIVISIONS_ID = Q.ID THEN 1 ELSE 0 END as CHECKED ");
		sb.append(" FROM REF_ACT_DIVISIONS RAD "); 
		sb.append(" left outer join   CURRENT_SETTINGS AS Q  on RAD.LKUP_DIVISIONS_ID IN (Q.ID) ");
		sb.append(" left outer join LKUP_DIVISIONS LD on RAD.LKUP_DIVISIONS_ID = LD.ID ");
		sb.append(" WHERE LKUP_ACT_TYPE_ID=").append(acttypeid).append("  AND RAD.ACTIVE='Y'  ");*/
		sb.append(" WITH CURRENT_SETTINGS AS ( ");
		sb.append(" SELECT  LD.* from  LKUP_DIVISIONS LD WHERE LD.ID in (").append(divids).append(") ");
		
		sb.append(" ) ");
		sb.append(" select CASE WHEN RAD.ID is not null and RAD.LKUP_DIVISIONS_ID = Q.ID THEN 1 ELSE 0 END as CHECKED ");
		sb.append(" FROM REF_ACT_DIVISIONS RAD "); 
		sb.append(" left outer join   CURRENT_SETTINGS AS Q  on RAD.LKUP_DIVISIONS_ID IN (Q.ID) ");
		sb.append(" left outer join LKUP_DIVISIONS LD on RAD.LKUP_DIVISIONS_ID = LD.ID ");
		sb.append(" WHERE LKUP_ACT_TYPE_ID=").append(acttypeid).append("  AND RAD.ACTIVE='Y'  ");
		
		return sb.toString();
	}
	
	public static void main2(String args[]){
		//System.out.println(getDivisions(218519, 254));
	}
	public static String checkProjectStatus(int projectId){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" EXPIRED  ");
		sb.append(" FROM  ");
		sb.append(" PROJECT P JOIN LKUP_PROJECT_STATUS L on P.LKUP_PROJECT_STATUS_ID= L.ID "); 
		sb.append(" WHERE P.ID =").append(projectId).append("  ");
		return sb.toString();
	}
	
	public static String countActivities(int projectid, int lkupacttypeid, Timekeeper start, Timekeeper end) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" 	COUNT(*) AS COUNT ");
		sb.append(" FROM ");
		sb.append(" 	ACTIVITY AS A ");
		sb.append(" 	JOIN LKUP_ACT_STATUS AS LAS ON A.LKUP_ACT_STATUS_ID = LAS.ID AND A.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" 	LKUP_ACT_TYPE_ID = ").append(lkupacttypeid).append(" ");
		sb.append(" 	AND ");
		sb.append(" 	PROJECT_ID = ").append(projectid);
		sb.append(" 	AND ");
		sb.append(" 	LAS.LIVE='Y' ");
		sb.append(" 	AND ");
		sb.append(" 	CAST(A.START_DATE AS DATE) BETWEEN CAST('").append(start.getString("MM/DD/YYYY")).append("' AS DATE)  AND CAST('").append(end.getString("MM/DD/YYYY")).append("' as date)  ");
		return sb.toString();
	}

	public static String checkMax(RequestVO vo,int projectid, MapSet v){
		StringBuilder sb = new StringBuilder();
		
		
		sb.append(" SELECT ");
		sb.append(" 	COUNT(*) AS COUNT ");
		sb.append(" FROM ");
		sb.append(" 	ACTIVITY AS A ");
		sb.append(" 	JOIN LKUP_ACT_STATUS AS LAS ON A.LKUP_ACT_STATUS_ID = LAS.ID AND A.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" 	LKUP_ACT_TYPE_ID = ").append(v.getInt("ID")).append(" ");
		sb.append(" 	AND ");
		sb.append(" 	PROJECT_ID = ").append(projectid);
		sb.append(" 	AND ");
		sb.append(" 	LAS.EXPIRED='N' ");
		sb.append(" 	AND ");
		sb.append(" 	LAS.LIVE='Y' ");
		
		if(v.getInt("DURATION_MAX")>0){
			Timekeeper st = new Timekeeper();
			Timekeeper ed = new Timekeeper();
			Timekeeper today = new Timekeeper();
			
			Timekeeper renewal = new Timekeeper();
			//Day start && Month start : auto 1 year ignores max days, max months, max years 
			//Day Start && No Month start : auto 1 month ignores max days, max months, max years
			
			//No Day start && No Month start;
			//duration max days subtracts no of days from current date. 
			// duration max months subtracts no of days from current date
			
			
			Logger.info(v.getInt("DAY_START")+"-------0000------"+v.getInt("MONTH_START"));
			
			if(v.getInt("DAY_START")>0 && v.getInt("MONTH_START")>0){ 
				Logger.info("#############CONDITION -1 ##############");
				
				boolean renewdot = false;
					st.setDay(v.getInt("DAY_START"));
					st.setMonth(v.getInt("MONTH_START"));
					ed.setMonth(v.getInt("MONTH_START")-1);
					if(v.getInt("DAY_START")==1){
						ed.setDay(ed.DAYS_IN_MONTH(ed.YEAR(), ed.MONTH()));
					}

					/*if (st.lessThan(today)) {
						ed.addYear(1);
					}
					else {
						st.addYear(-1);
					}*/
					// WHAT IS THIS DOING - Alain
					
					renewal.setDate(vo.getEnddate());
					
					
					if(renewal.YEAR()> st.YEAR()){
						Logger.highlight("Renewal ---> "+ renewal.getString("MM/DD/YYYY"));	
						st.setYear(today.YEAR());
						ed.setYear(st.YEAR()+1);
						renewdot = true;
					}
					
					
					
				
					if(!renewdot){
						if(today.MONTH()<v.getInt("MONTH_START")){
							st.addYear(-1); //DURATION_MAX_YEARS can be added only make sure everything as this setting
						} else {
							ed.addYear(1);
						}
					}
					Logger.info(renewal.YEAR()+"-------1111------"+st.YEAR());
					
					/*if(st.YEAR()==2022){
						st.addYear(-1);
					}*/
					
				
					
				}
			else 
				if(v.getInt("DAY_START")<=0 && v.getInt("MONTH_START")>0){ 
					Logger.info("#############CONDITION -2 ##############");
					//TODO TEST THIS LOOP
					/*st.setMonth(v.getInt("MONTH_START"));
					ed.setMonth(v.getInt("MONTH_START")+1);
					ed.addDay(-1);
					if(v.getInt("DURATION_MAX_YEARS")>0){
						if(st.MONTH()<=v.getInt("MONTH_START") ){
							st.addYear(-v.getInt("DURATION_MAX_YEARS")); 
						}else {
							ed.addYear(v.getInt("DURATION_MAX_YEARS"));
						}
					}*/
				}
			else if(v.getInt("DAY_START")>0 && v.getInt("MONTH_START")<=0 && v.getInt("DURATION_MAX_MONTHS")<=0){ 
				Logger.info("#############CONDITION -3 ##############");
				st.setDay(v.getInt("DAY_START"));	
				ed.setDay(v.getInt("DAY_START"));
				ed.addMonth(1);
				ed.addDay(-1);
				
					
			}
			else {
				if(v.getInt("DAY_START")<=0 && v.getInt("MONTH_START")<=0 && v.getInt("DURATION_MAX_YEARS")>0){
					Logger.info("#############CONDITION -4 ##############");
					st.addYear(-v.getInt("DURATION_MAX_YEARS"));
					
				}
				else if(v.getInt("DURATION_MAX_MONTHS")>0){
					Logger.info("#############CONDITION -5 ##############");
					
					
					if(v.getInt("DAY_START")==1){
						st.setDay(1);
						ed.setDay(ed.DAYS_IN_MONTH(ed.YEAR(), ed.MONTH()));
					}else if(v.getInt("DAY_START")>1){
						st.setDay(v.getInt("DAY_START"));
						ed.setDay(v.getInt("DAY_START")-1);
					}else {
						st.addMonth(-v.getInt("DURATION_MAX_MONTHS"));
					}
					
				}
				
				else if(v.getInt("DAY_START")<=0 && v.getInt("MONTH_START")<=0 && v.getInt("DURATION_MAX_DAYS")>0){
					Logger.info("#############CONDITION -6 ##############");
					//st.addDay(-1);
					if(v.getInt("DURATION_MAX_DAYS")>0){
						st.addDay(-v.getInt("DURATION_MAX_DAYS"));
					}
				}
			}
				
				//sb.append(" AND A.EXP_DATE =  '").append(ed.getString("YYYY-MM-DD")).append("' ");
			String tempstart = st.getString("MM/DD/YYYY");
			Logger.info("#############tempstart ##############"+tempstart);
			
			if(v.getInt("ID")==251 || 	v.getInt("ID")==252 || 	v.getInt("ID")==278 || 	v.getInt("ID")==279 || 	v.getInt("ID")==280){
				if( tempstart.equals("01/01/2022")|| tempstart.equals("10/01/2022") || tempstart.equals("10/01/2021") || tempstart.equals("10/01/2020")){
					tempstart = "10/01/2020";
				}
			}
			sb.append(" AND  CAST(START_DATE as date) BETWEEN CAST('").append(tempstart).append("' as date)  AND CAST('").append(ed.getString("MM/DD/YYYY")).append("' as date)  ");
			
			
		
		}else {
			sb = new StringBuilder();
		}
		
		return sb.toString();
	}
	
	public static String checkMaxDayTimePerYear(int projectid){
		StringBuilder sb = new StringBuilder();
		
		
		sb.append(" SELECT ");
		sb.append(" 	COUNT(*) AS COUNT ");
		sb.append(" FROM ");
		sb.append(" 	ACTIVITY AS A ");
		sb.append(" 	JOIN LKUP_ACT_STATUS AS LAS ON A.LKUP_ACT_STATUS_ID = LAS.ID AND A.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" 	LKUP_ACT_TYPE_ID = ").append(253).append(" ");
		sb.append(" 	AND ");
		sb.append(" 	PROJECT_ID = ").append(projectid);
		sb.append(" 	AND ");
		sb.append(" 	LAS.EXPIRED='N' ");
		sb.append(" 	AND ");
		sb.append(" 	LAS.LIVE='Y' ");
		
		//if(v.getInt("DURATION_MAX")>0){
			Timekeeper st = new Timekeeper();
			Timekeeper ed = new Timekeeper();
			
			if(st.MONTH()<10){
				st.addYear(-1); 
			}else if(st.MONTH()>=10){
				ed.addYear(1);
			}
			
			st.setDay(1);
			st.setMonth(10);
			ed.setMonth(9);
			ed.setDay(ed.DAYS_IN_MONTH(ed.YEAR(), ed.MONTH()));
		
			sb.append(" AND  CAST(START_DATE as date) BETWEEN CAST('").append("10/01/"+st.YEAR()).append("' as date)  AND CAST('").append("09/30/"+ed.YEAR()).append("' as date)  ");
			
			
		
		//}
		
		return sb.toString();
	}
	
	

	public static String getActivityAndProjectType(int actid) {
		if (actid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.LKUP_ACT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" P.LKUP_PROJECT_TYPE_ID ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND A.ID = ").append(actid);
		return sb.toString();
	}

	public static String updateActivity(String actid, String printed, String batchid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" ACTIVITY ");
		sb.append(" SET PRINTED = '").append(printed).append("'");
		sb.append(" , BATCH_PRINT_ID = ").append(batchid);
		sb.append(" , UPDATED_DATE = CURRENT_TIMESTAMP");
		sb.append(" WHERE ID IN (").append(actid).append(") ");
		return sb.toString();
	}
	
	public static String getActivityId(String ids, String type, String typeid, String startdate, String enddate) {
		StringBuilder sb = new StringBuilder();
		/*sb.append("SELECT A.ID FROM ACTIVITY A		JOIN PROJECT P  ON A.PROJECT_ID = P.ID  ");
		sb.append("JOIN LKUP_PROJECT_TYPE ATP ON P.LKUP_PROJECT_TYPE_ID=ATP.ID	AND ATP.ACTIVE='Y'	  ");
		sb.append("JOIN LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID=LAT.ID AND LAT.ACTIVE='Y'	  ");
		sb.append("WHERE ATP.ISDOT ='Y' AND LAT.ISDOT='Y' AND P.ACTIVE='Y' AND A.ACTIVE='Y' AND P.ID IN (");
		sb.append(projectid).append(")");
		if(Operator.toInt(groupid) > 0 && group.equalsIgnoreCase("batch"))
			sb.append("	AND A.PRINTED='Y' ");
		else
			sb.append("	AND A.PRINTED='N' ");
		if(Operator.hasValue(startdate) && Operator.hasValue(enddate)){
			sb.append(" 	and A.START_DATE >= '").append(startdate).append("' and A.EXP_DATE <= '").append(enddate).append("'   ");
		}	*/
		
		sb.append(" select A.ID,A.ACT_NBR,A.PROJECT_ID,A.START_DATE,A.EXP_DATE,LAT.DESCRIPTION as TYPE,SUM(SD.FEE_AMOUNT) as AMOUNT , SUM(SD.FEE_PAID) as PAID,SUM(SD.BALANCE_DUE) as BALANCE ");
		sb.append(" ,  CU.USERNAME AS CREATED, "); 
		sb.append("   UP.USERNAME as UPDATED,CONVERT(VARCHAR(10),A.CREATED_DATE,101) as CREATED_DATE,CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as UPDATED_DATE ");
		
		sb.append(" from activity A      ");
		sb.append(" 	 join LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID  ");
		
		sb.append(" left outer join REF_ACT_STATEMENT RAS on A.ID=RAS.ACTIVITY_ID  ");
		sb.append(" left outer join STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");
		sb.append(" left outer join STATEMENT_DETAIL SD on  S.ID=SD.STATEMENT_ID  ");
		sb.append(" left outer join FEE_GROUP FG   on SD.GROUP_ID=FG.ID ");
	//	sb.append("   LEFT OUTER JOIN Q on A.ID = Q.ACTIVITY_ID ");

		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" 	where LAT.ISDOT='Y' and LAT.DOT_EXEMPTION='N' ");  
		
		if(Operator.equalsIgnoreCase(type, "project")){
			sb.append(" 	and  project_id in  (").append(ids).append(")");
			if(Operator.hasValue(startdate) && Operator.hasValue(enddate)){
				sb.append(" 	and A.START_DATE >= '").append(startdate).append("' and A.EXP_DATE <= '").append(enddate).append("'   ");
			}
		}else {
			sb.append(" 	and  A.ID in  (").append(ids).append(")");
		}
		
		sb.append(" 	and lat.renewal='Y' and lat.id =   ").append(typeid);
		
		sb.append(" 	  group by A.ID,A.ACT_NBR,  LAT.DESCRIPTION,A.PROJECT_ID,A.START_DATE,A.EXP_DATE	,A.CREATED_DATE,A.UPDATED_DATE, A.CREATED_BY, A.UPDATED_BY,CU.USERNAME ,UP.USERNAME  ");
		
		sb.append(" 	 order by A.CREATED_DATE DESC  ");
		return sb.toString();
	}

	public static String statusDefaultIssued(String type, String id, int userid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE A SET LKUP_ACT_STATUS_ID = RATS.LKUP_ACT_STATUS_ID");
		sb.append(" , ISSUED_DATE = CAST(getDate() AS DATE) ");
		sb.append(" , UPDATED_DATE = CURRENT_TIMESTAMP ");
		sb.append(" , UPDATED_BY = ").append(userid);
		sb.append(" FROM ACTIVITY A");
		sb.append(" JOIN LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID AND LAT.ACTIVE='Y'");
		sb.append(" JOIN REF_ACT_TYPE_STATUS RATS ON LAT.ID  = RATS.LKUP_ACT_TYPE_ID AND RATS.ACTIVE ='Y'  AND DEFLT_ISSUED = 'Y' ");
		if(Operator.equalsIgnoreCase(type, "activity")){
			sb.append(" WHERE A.ID IN  (").append(id).append(")");
		}
		return sb.toString();
	}

	public static String getColors(int startyear, int endyear) {
		if (startyear < 2000 && endyear < 2000) {
			Timekeeper s = new Timekeeper();
			startyear = s.YEAR();
			endyear = startyear + 1;
		}
		else if (startyear >= 2000) {
			endyear = startyear + 1;
		}
		else if (endyear >= 2000) {
			startyear = endyear - 1;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" T.DESCRIPTION, ");
		sb.append(" C.LKUP_ACT_TYPE_ID, ");
		sb.append(" C.HEX_COLOR, ");
		sb.append(" C.STYLE, ");
		sb.append(" C.LABEL, ");
		sb.append(" C.EXP_YEAR ");
		sb.append(" FROM ");
		sb.append(" LKUP_ACT_COLOR AS C ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON C.LKUP_ACT_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" C.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" C.EXP_YEAR >= ").append(startyear);
		sb.append(" AND ");
		sb.append(" C.EXP_YEAR <= ").append(endyear);
		return sb.toString();
	}

	public static String getDefaultColors() {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" T.DESCRIPTION, ");
		sb.append(" C.LKUP_ACT_TYPE_ID, ");
		sb.append(" C.HEX_COLOR, ");
		sb.append(" C.STYLE, ");
		sb.append(" C.LABEL, ");
		sb.append(" C.EXP_YEAR ");
		sb.append(" FROM ");
		sb.append(" LKUP_ACT_COLOR AS C ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON C.LKUP_ACT_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" C.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" C.EXP_YEAR = -1 ");
		return sb.toString();
	}

	public static String getActivities(String ids) {
		if (!Operator.hasValue(ids)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM ACTIVITY WHERE ID IN (").append(ids).append(") ");
		return sb.toString();
	}

	public static String getDefaultStatus(int acttypeid) {
		if (acttypeid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" S.ID ");
		sb.append(" , ");
		sb.append(" 0 AS ORDR ");
		sb.append(" FROM ");
		sb.append(" LKUP_ACT_STATUS AS S ");
		sb.append(" JOIN REF_ACT_TYPE_STATUS AS R ON S.ID = R.LKUP_ACT_STATUS_ID AND R.ACTIVE = 'Y' AND S.ACTIVE = 'Y' AND R.DEFLT = 'Y' AND R.LKUP_ACT_TYPE_ID = ").append(acttypeid);
		sb.append(" UNION ");
		sb.append(" SELECT ");
		sb.append(" S.ID ");
		sb.append(" , ");
		sb.append(" 10 AS ORDR ");
		sb.append(" FROM ");
		sb.append(" LKUP_ACT_STATUS AS S ");
		sb.append(" JOIN REF_ACT_TYPE_STATUS AS R ON S.ID = R.LKUP_ACT_STATUS_ID AND R.ACTIVE = 'Y' AND S.ACTIVE = 'Y' AND R.DEFLT = 'Y' AND R.LKUP_ACT_TYPE_ID = -1 ");
		sb.append(" ) ");
		sb.append(" SELECT TOP 1 ID FROM Q ORDER BY ORDR ");
		return sb.toString();
	}

	public static String getDefaultIssued(int acttypeid) {
		if (acttypeid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	S.ID ");
		sb.append(" 	, ");
		sb.append(" 	CASE WHEN R.LKUP_ACT_TYPE_ID = -1 THEN 10 ELSE 0 END AS ORDR ");
		sb.append(" FROM ");
		sb.append(" 	LKUP_ACT_STATUS AS S ");
		sb.append(" 	JOIN REF_ACT_TYPE_STATUS AS R ON S.ID = R.LKUP_ACT_STATUS_ID AND R.ACTIVE = 'Y' AND S.ACTIVE = 'Y' AND R.DEFLT_ISSUED = 'Y' AND R.LKUP_ACT_TYPE_ID IN (-1, ").append(acttypeid).append(") ");
		sb.append(" UNION ");
		sb.append(" SELECT ");
		sb.append(" 	S.ID ");
		sb.append(" 	, ");
		sb.append(" 	CASE WHEN R.LKUP_ACT_TYPE_ID = -1 THEN 30 ELSE 20 END AS ORDR ");
		sb.append(" FROM ");
		sb.append(" 	LKUP_ACT_STATUS AS S ");
		sb.append(" 	JOIN REF_ACT_TYPE_STATUS AS R ON S.ID = R.LKUP_ACT_STATUS_ID AND R.ACTIVE = 'Y' AND S.ACTIVE = 'Y' AND S.ISSUED = 'Y' AND R.LKUP_ACT_TYPE_ID IN (-1, ").append(acttypeid).append(") ");
		sb.append(" ) ");
		sb.append(" SELECT TOP 1 ID FROM Q ORDER BY ORDR ");
		return sb.toString();
	}

	public static String getActivities(int projectid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.ID ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID AND A.ACTIVE = 'Y' AND A.PROJECT_ID = ").append(projectid);
		String sub = sb.toString();
		sb = new StringBuilder();
		sb.append(FinanceSQL.calc("activity", sub));
		sb.append(" SELECT ");
		sb.append(" MAIN.ID ");
		sb.append(" , ");
		sb.append(" MAIN.ACT_NBR ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" , ");
		sb.append(" MAIN.VALUATION_DECLARED ");
		sb.append(" , ");
		sb.append(" MAIN.VALUATION_CALCULATED ");
		sb.append(" , ");
		sb.append(" MAIN.START_DATE ");
		sb.append(" , ");
		sb.append(" MAIN.APPLIED_DATE ");
		sb.append(" , ");
		sb.append(" MAIN.ISSUED_DATE ");
		sb.append(" , ");
		sb.append(" MAIN.EXP_DATE ");
		sb.append(" , ");
		sb.append(" MAIN.APPLICATION_EXP_DATE ");
		sb.append(" , ");
		sb.append(" COALESCE(T.FEE_AMOUNT, 0) AS FEE_AMOUNT ");
		sb.append(" , ");
		sb.append(" COALESCE(T.BALANCE_DUE, 0) AS BALANCE_DUE ");
		sb.append(" FROM ");
		sb.append(" MAIN ");
		sb.append(" JOIN LKUP_ACT_STATUS AS S ON MAIN.LKUP_ACT_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN TOTAL AS T ON MAIN.REF_ID = T.REF_ID ");
		return sb.toString();
	}

	public static String status(String[] acttypeids) {
		if (!Operator.hasValue(acttypeids)) { return ""; }
		boolean empty = true;
		StringBuilder a = new StringBuilder();
		for (int i=0; i<acttypeids.length; i++) {
			int acttypeid = Operator.toInt(acttypeids[i]);
			if (acttypeid > 0) {
				if (!empty) { a.append(","); }
				a.append(acttypeid);
				empty = false;
			}
		}
		String joined = a.toString();
		if (!Operator.hasValue(joined)) { return ""; }

		StringBuilder sb = new StringBuilder();
		sb = new StringBuilder();
		sb.append(" WITH T AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		A.ID ");
		sb.append(" 	FROM ");
		sb.append(" 		LKUP_ACT_TYPE AS A ");
		sb.append(" 	WHERE ");
		sb.append(" 		A.ID IN ( ").append(joined).append(" ) ");
		sb.append(" ) ");
		sb.append(" , S AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		ST.LKUP_ACT_STATUS_ID AS ID, ");
		sb.append(" 		SS.STATUS, ");
		sb.append(" 		SS.LIVE, ");
		sb.append(" 		SS.ISSUED, ");
		sb.append(" 		SS.EXPIRED, ");
		sb.append(" 		SS.FINAL ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_ACT_TYPE_STATUS AS ST ");
		sb.append(" 		JOIN LKUP_ACT_STATUS AS SS ON ST.LKUP_ACT_STATUS_ID = SS.ID AND ST.ACTIVE = 'Y' AND SS.ACTIVE = 'Y' ");
		sb.append(" 	WHERE ");
		sb.append(" 		ST.LKUP_ACT_TYPE_ID IN (").append(joined).append(") ");
		sb.append(" ) ");
		sb.append(" , B AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		T.ID AS TYPE_ID, ");
		sb.append(" 		S.ID AS STATUS_ID ");
		sb.append(" 	FROM ");
		sb.append(" 		T ");
		sb.append(" 		JOIN S ON 1=1 ");
		sb.append(" ) ");
		sb.append(" , TS AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		B.TYPE_ID, ");
		sb.append(" 		B.STATUS_ID, ");
		sb.append(" 		RT.ID AS RTID ");
		sb.append(" 	FROM ");
		sb.append(" 		B ");
		sb.append(" 		LEFT OUTER JOIN REF_ACT_TYPE_STATUS AS RT ON B.TYPE_ID = RT.LKUP_ACT_TYPE_ID AND B.STATUS_ID = RT.LKUP_ACT_STATUS_ID ");
		sb.append(" ) ");
		sb.append(" , Q AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		S.ID, ");
		sb.append(" 		S.ID AS VALUE, ");
		sb.append(" 		S.STATUS AS TEXT, ");
		sb.append(" 		S.STATUS, ");
		sb.append(" 		S.LIVE, ");
		sb.append(" 		S.ISSUED, ");
		sb.append(" 		S.EXPIRED, ");
		sb.append(" 		S.FINAL ");
		sb.append(" 	FROM ");
		sb.append(" 		S ");
		sb.append(" 	WHERE ");
		sb.append(" 		S.ID NOT IN (SELECT STATUS_ID FROM TS WHERE RTID IS NULL) ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		ST.ID, ");
		sb.append(" 		ST.ID AS VALUE, ");
		sb.append(" 		ST.STATUS AS TEXT, ");
		sb.append(" 		ST.STATUS, ");
		sb.append(" 		ST.LIVE, ");
		sb.append(" 		ST.ISSUED, ");
		sb.append(" 		ST.EXPIRED, ");
		sb.append(" 		ST.FINAL ");
		sb.append(" 	FROM ");
		sb.append(" 		LKUP_ACT_STATUS AS ST ");
		sb.append(" 		JOIN REF_ACT_TYPE_STATUS AS RT ON ST.ID = RT.LKUP_ACT_STATUS_ID AND RT.ACTIVE = 'Y' AND ST.ACTIVE = 'Y' AND RT.LKUP_ACT_TYPE_ID = -1 ");
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT * FROM Q ORDER BY TEXT ");
		return sb.toString();
	}

	public static String copy(int actid, int projectid, String actnbr, int acttypeid, int actsubtypeid, int actstatusid, String description, double valcalculated, double valdeclared, String pcreq, String start, String applied, String issued, String exp, String appexp, String fnal, String pcfeedate, String permitfeedate, String online, String sensitive, String inherit, int qty, int workflowgroupid, int userid, String ip) {
		if (actid < 1 || projectid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ACTIVITY ( ");
		sb.append(" 	ACT_NBR ");
		sb.append(" 	, ");
		sb.append(" 	PROJECT_ID ");
		sb.append(" 	, ");
		sb.append(" 	LKUP_ACT_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	LKUP_ACT_SUBTYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	VALUATION_CALCULATED ");
		sb.append(" 	, ");
		sb.append(" 	VALUATION_DECLARED ");
		sb.append(" 	, ");
		sb.append(" 	PLAN_CHK_REQ ");
		sb.append(" 	, ");
		sb.append(" 	START_DATE ");
		sb.append(" 	, ");
		sb.append(" 	APPLIED_DATE ");
		sb.append(" 	, ");
		sb.append(" 	ISSUED_DATE ");
		sb.append(" 	, ");
		sb.append(" 	EXP_DATE ");
		sb.append(" 	, ");
		sb.append(" 	APPLICATION_EXP_DATE ");
		sb.append(" 	, ");
		sb.append(" 	FINAL_DATE ");
		sb.append(" 	, ");
		sb.append(" 	PLAN_CHK_FEE_DATE ");
		sb.append(" 	, ");
		sb.append(" 	PERMIT_FEE_DATE ");
		sb.append(" 	, ");
		sb.append(" 	ONLINE ");
		sb.append(" 	, ");
		sb.append(" 	SENSITIVE ");
		sb.append(" 	, ");
		sb.append(" 	LKUP_ACT_STATUS_ID ");
		sb.append(" 	, ");
		sb.append(" 	QTY ");
		sb.append(" 	, ");
		sb.append(" 	INHERIT ");
		sb.append(" 	, ");
		sb.append(" 	WORKFLOW_GROUP_ID ");
		sb.append(" 	, ");
		sb.append(" 	CREATED_BY ");
		sb.append(" 	, ");
		sb.append(" 	CREATED_DATE ");
		sb.append(" 	, ");
		sb.append(" 	UPDATED_BY ");
		sb.append(" 	, ");
		sb.append(" 	UPDATED_DATE ");
		sb.append(" 	, ");
		sb.append(" 	CREATED_IP ");
		sb.append(" 	, ");
		sb.append(" 	UPDATED_IP ");
		sb.append(" ) OUTPUT Inserted.ID  VALUES ( ");
		sb.append(" '").append(Operator.sqlEscape(actnbr)).append("' ");
		sb.append(" , ");
		sb.append(projectid);
		sb.append(" , ");
		sb.append(acttypeid);
		sb.append(" , ");
		sb.append(actsubtypeid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(valcalculated);
		sb.append(" , ");
		sb.append(valdeclared);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(pcreq)).append("' ");
		sb.append(" , ");
		sb.append(datetime(start));
		sb.append(" , ");
		sb.append(datetime(applied));
		sb.append(" , ");
		sb.append(datetime(issued));
		sb.append(" , ");
		sb.append(datetime(exp));
		sb.append(" , ");
		sb.append(datetime(appexp));
		sb.append(" , ");
		sb.append(datetime(fnal));
		sb.append(" , ");
		sb.append(datetime(pcfeedate));
		sb.append(" , ");
		sb.append(datetime(permitfeedate));
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(online)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(sensitive)).append("' ");
		sb.append(" , ");
		sb.append(actstatusid);
		sb.append(" , ");
		sb.append(qty);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(inherit)).append("' ");
		sb.append(" , ");
		sb.append(workflowgroupid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}


	private static String datetime(String date) {
		if (!Operator.hasValue(date)) { return "NULL"; }
		Timekeeper d = new Timekeeper();
		d.setDate(date);
		return d.sqlDatetime();
	}

	public static String move(int actid, int newprojid, int userid, String ip) {
		if (actid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ACTIVITY SET ");
		sb.append(" PROJECT_ID = ").append(newprojid);
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(actid);
		return sb.toString();
	}


	public static String insertRefLso(int actid, int lsoId, int userid, String ip){
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_LSO_ACT ");
		sb.append(" ( ACTIVITY_ID, LSO_ID, CREATED_BY, UPDATED_BY ");
		sb.append(" 	, ");
		sb.append(" 	CREATED_IP ");
		sb.append(" 	, ");
		sb.append(" 	UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(actid);
		sb.append(" , ");
		sb.append(lsoId);
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




	public static String deactiveHours(){
	StringBuilder sb = new StringBuilder();
	sb.append(" update ACTIVITY SET LKUP_ACT_STATUS_ID = 30, DESCRIPTION=DESCRIPTION+' workflow del ',  UPDATED_DATE = getDate() where ID in ( ");
	sb.append(" select A.ID ");
	sb.append(" from activity A  ");
	sb.append(" JOIN LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID= LAT.ID  ");
	sb.append(" JOIN REF_ACT_STATEMENT RAS on A.ID = RAS.ACTIVITY_ID ");
	sb.append(" JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID = SD.STATEMENT_ID  ");
	sb.append(" WHERE LKUP_ACT_STATUS_ID <> 30  ");
	sb.append(" AND A.START_DATE > '2018-09-30 00:00:00'  ");
	sb.append(" AND A.ACTIVE= 'Y' AND LAT.DEACTIVATE_HRS >0   ");
	sb.append(" AND SD.FEE_PAID <=0 AND A.DESCRIPTION NOT LIKE 'Loaded%' ");
	sb.append(" AND A.CREATED_DATE < DATEADD(hour, -DEACTIVATE_HRS, getDate()) ");
	sb.append(" ) ");

	return sb.toString();
	}
	
	
	public static String permitInfo(String permitNo){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT A.ID, ");
		sb.append(" ACT_NBR, ");
		sb.append(" A.DESCRIPTION AS ACTIVITY_DESCRIPTION, ");
		sb.append(" CONVERT(VARCHAR(10),APPLIED_DATE,101) AS APPLIED_DATE, ");
		sb.append(" CONVERT(VARCHAR(10),ISSUED_DATE,101) AS ISSUED_DATE, ");
		sb.append(" CONVERT(VARCHAR(10),EXP_DATE,101) AS EXP_DATE, ");
		sb.append(" VALUATION_CALCULATED, ");
		sb.append(" P.PROJECT_NBR AS PROJECT_NUMBER, ");
		sb.append(" P.NAME AS PROJECT_NAME, ");
		sb.append(" 'https://cs.beverlyhills.org/cs/?entity=lso&type=activity&_typeid='+CONVERT(VARCHAR(15),A.ID) AS LINK, ");
		sb.append(" LAT.TYPE AS ACTIVITY_TYPE, ");
		sb.append(" RLP.LSO_ID, ");
		sb.append(" VCA.ADDRESS+' '+VCA.CITY +' '+ VCA.STATE +' '+VCA.ZIP AS ADDR, ");
		sb.append(" CASE WHEN VCA.APN IS NULL THEN VCA.LAND_APN ELSE VCA.APN END AS PARCEL_ID, ");
		sb.append(" 'NO' AS HOLDS ");
		//sb.append(" --VCD.DIVISION AS ZONING, ");
		sb.append("  ");
		//sb.append(" --VCD1.DIVISION AS SEISMIC_RETROFIT, ");
		//sb.append(" --VCD2.DIVISION AS ZONING_AREA_DESIGNATION, ");
		//sb.append(" 'ALEXZANDRIA LEXUS KOCSY, (424) 337-8853, ALEXZANDRIA.KOCSY@ANDERSENCORP.COM' AS PRIMARY_CONTACT ");
		sb.append("  ");
		sb.append("  ,  ");
		sb.append(" 		         (       ");
		sb.append(" 		         SELECT DISTINCT  ");
		sb.append(" 		         U.ID AS USER_ID,  ");
		sb.append(" 		         FIRST_NAME + ' ' + LAST_NAME   AS USER_NAME ,  ");
		sb.append(" 		         U.USERNAME   AS USER_USERNAME ,  ");
		sb.append(" 		         U.EMAIL AS USER_EMAIL,  ");
		sb.append(" 		         U.FIRST_NAME AS USER_FNAME,  ");
		sb.append(" 		         U.LAST_NAME AS USER_LNAME, ");
		sb.append(" 		         P.PHONE_WORK +' '+PHONE_CELL +' '+ PHONE_HOME AS PHONE ");
		sb.append(" 		 ");
		sb.append(" 		      FROM ACTIVITY APP  ");
		sb.append(" 		      LEFT OUTER JOIN REF_ACT_USERS RAU ON APP.ID=RAU.ACTIVITY_ID  AND RAU.ACTIVE='Y' AND RAU.PRIMARY_CONTACT='Y'  ");
		sb.append(" 		      LEFT OUTER JOIN   REF_PROJECT_USERS RAUP ON APP.PROJECT_ID=RAUP.PROJECT_ID  AND RAUP.ACTIVE='Y'   AND RAUP.PRIMARY_CONTACT='Y'    ");
		sb.append(" 		      JOIN REF_USERS RU ON (RAU.REF_USERS_ID=RU.ID  OR      RAUP.REF_USERS_ID=RU.ID   )   ");
		sb.append(" 		      LEFT OUTER JOIN LKUP_USERS_TYPE AS LUT ON RU.LKUP_USERS_TYPE_ID=LUT.ID   ");
		sb.append(" 		    ");
		sb.append(" 		      JOIN USERS AS U ON RU.USERS_ID = U.ID   AND U.NOTIFY='Y'      AND U.ACTIVE= 'Y'  ");
		sb.append(" 		       LEFT OUTER JOIN PEOPLE AS P ON U.ID=P.USERS_ID   ");
		sb.append(" 		      WHERE APP.ID = A.ID  ");
		sb.append(" 		 ");
		sb.append(" 		 ");
		sb.append(" 		                        ");
		sb.append(" 		      FOR JSON PATH, INCLUDE_NULL_VALUES ) AS 'LIST_PEOPLE_PRIMARY'  ");
		sb.append(" 		       ");
		sb.append(" 		       ");
		sb.append(" 		       ");
		sb.append(" 		      ,  ");
		sb.append(" 		         (       ");
		sb.append(" 		         SELECT DISTINCT  ");
		sb.append(" 		         TYPE,LKUP_DIVISIONS_TYPE_ID,  ");
		sb.append(" 		         DIVISION  ");
		sb.append(" 		 ");
		sb.append(" 		      FROM ACTIVITY APP  ");
		sb.append(" 		      LEFT OUTER JOIN REF_LSO_PROJECT RAU ON APP.PROJECT_ID=RAU.PROJECT_ID  AND RAU.ACTIVE='Y' ");
		sb.append(" 		      JOIN V_CENTRAL_DIVISION RU ON RAU.LSO_ID=RU.LSO_ID ");
		sb.append(" 		      WHERE APP.ID = A.ID  ");
		sb.append(" 		 ");
		sb.append(" 		 ");
		sb.append(" 		                        ");
		sb.append(" 		      FOR JSON PATH, INCLUDE_NULL_VALUES  )AS 'LIST_DIVISIONS'  ");
		sb.append("  ");
		sb.append(" FROM ACTIVITY A ");
		sb.append(" JOIN LKUP_ACT_STATUS LAS ON A.LKUP_ACT_STATUS_ID = LAS.ID  ");
		sb.append(" JOIN LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID  ");
		sb.append(" LEFT OUTER JOIN PROJECT P ON A.PROJECT_ID = P.ID  ");
		sb.append(" LEFT OUTER JOIN REF_LSO_PROJECT RLP ON A.PROJECT_ID = RLP.PROJECT_ID  ");
		sb.append(" LEFT OUTER JOIN V_CENTRAL_ADDRESS VCA ON RLP.LSO_ID = VCA.LSO_ID  ");
		sb.append(" WHERE LOWER(A.ACT_NBR) ='").append(Operator.sqlEscape(permitNo.toLowerCase())).append("' ");
				return sb.toString();
	}

	public static String getActTypedates(String type, int typeid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND A.ID = ").append(typeid);
		return sb.toString();
	}

	public static String updateActDates(int activityid, String expdate, String appexpdate, int userid, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE  ACTIVITY SET ");
		sb.append(" 	UPDATED_BY = ").append(userid);
		sb.append(" 	, ");
		sb.append(" 	UPDATED_DATE = getDate() ");
		sb.append(" 	, ");
		sb.append(" 	UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		if (Operator.hasValue(expdate)) {
			sb.append(" 	, ");
			sb.append(" 	EXP_DATE = ");
			sb.append(CsTools.dateColumnValue(expdate));
		} else if(expdate == null){
			sb.append(" 	, ");
			sb.append(" 	EXP_DATE = null ");
		}
		if (Operator.hasValue(appexpdate)) {
			sb.append(" 	, ");
			sb.append(" 	APPLICATION_EXP_DATE = ");
			sb.append(CsTools.dateColumnValue(appexpdate));
		} else if(appexpdate == null){
			sb.append(" 	, ");
			sb.append(" 	APPLICATION_EXP_DATE = null ");
		}
		sb.append(" where id = ");
		sb.append(activityid);
		return sb.toString();
	}
}
