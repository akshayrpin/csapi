package csapi.impl.activities;

import java.util.ArrayList;

import alain.core.security.Token;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.entity.EntityAgent;
import csapi.impl.finance.FinanceSQL;
import csapi.utils.CsReflect;
import csshared.vo.TypeInfo;

public class ActivitiesSQL {


	
	public static String details(String type, int typeid, int id, String option) {
		if (!Operator.hasValue(type)) { return ""; }
		if (!Operator.equalsIgnoreCase(type, "project")) { return ""; }
		String ids = getActivityIds(typeid, option);
		if (!Operator.hasValue(ids)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(FinanceSQL.calc("activity", ids));
		sb.append(" SELECT ");
		sb.append(" 	M.ID, ");
		sb.append(" 	M.ID AS LINKID,"); 
		sb.append(" 	'activity' AS LINKTYPE, ");
		sb.append(" 	M.ACT_NBR,"); 
		sb.append(" 	M.DESCRIPTION,"); 
		sb.append(" 	TY.DESCRIPTION AS TYPE, "); 
		sb.append(" 	M.VALUATION_DECLARED, "); 
		sb.append(" 	M.VALUATION_CALCULATED, "); 
		sb.append(" 	M.APPLIED_DATE, "); 
		sb.append(" 	M.START_DATE, "); 
		sb.append(" 	M.ISSUED_DATE, "); 
		sb.append(" 	M.EXP_DATE, "); 
		sb.append(" 	M.APPLICATION_EXP_DATE, "); 
		sb.append(" 	M.PLAN_CHK_REQ, "); 
		sb.append(" 	M.SENSITIVE, "); 
		sb.append(" 	M.INHERIT, "); 
		sb.append(" 	S.STATUS, "); 
		sb.append(" 	S.LIVE, "); 
		sb.append(" 	S.ISSUED, "); 
		sb.append(" 	S.EXPIRED, "); 
		sb.append(" 	S.FINAL, "); 
		sb.append(" 	CASE ");
		sb.append(" 		WHEN S.EXPIRED = 'Y' THEN 'N' ");
		sb.append(" 		WHEN S.LIVE = 'N' THEN 'N' ");
		sb.append(" 		WHEN S.FINAL = 'Y' THEN 'N' ");
		sb.append(" 		WHEN COALESCE(CAST(M.EXP_DATE AS DATE), getDate() + 1)  < getDate() THEN 'N' ");
		sb.append(" 		ELSE 'Y' "); 
		sb.append(" 	END AS ACTIVE, ");
		sb.append(" 	CU.USERNAME AS CREATED, "); 
		sb.append(" 	UP.USERNAME as UPDATED, "); 
		sb.append(" 	M.UPDATED_DATE, "); 
		sb.append(" 	COALESCE(T.FEE_AMOUNT, 0) AS FEE_AMOUNT, "); 
		sb.append(" 	COALESCE(T.FEE_PAID, 0) AS FEE_PAID, ");
		sb.append(" 	COALESCE(T.BALANCE_DUE, 0) AS BALANCE_DUE ");
		sb.append(" FROM ");
		sb.append(" TOTAL AS T ");
		sb.append(" JOIN MAIN AS M ON T.REF_ID = M.REF_ID ");
		sb.append(" JOIN LKUP_ACT_STATUS AS S ON M.LKUP_ACT_STATUS_ID = S.ID ");
		sb.append(" JOIN LKUP_ACT_TYPE AS TY ON M.LKUP_ACT_TYPE_ID = TY.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS CU ON M.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS UP ON M.UPDATED_BY = UP.ID ");
		sb.append(" ORDER BY M.UPDATED_DATE DESC ");
		return sb.toString();
	}

	public static String list(String type, int typeid, int id, String option) {
		return details(type, typeid, id, option);
	}

	public static String info(String type, int typeid, int id, String option) {
		return details(type, typeid, id, option);
	}

	public static String summary(String type, int typeid, int id, String option, Token u) {
		if (!Operator.hasValue(type)) { return ""; }
		if (!Operator.equalsIgnoreCase(type, "project")) { return entitySummary(type, typeid, id, option, u); }
		if (!u.isStaff()) { return projectSummary(typeid, option, u); }
		String ids = getActivityIds(typeid, option);
		if (!Operator.hasValue(ids)) { return ""; }
		String roles = u.roles();
		String nproles = Operator.join(u.getNonpublicroles(), ",");
		StringBuilder sb = new StringBuilder();
		sb.append(FinanceSQL.calc("activity", ids));
		sb.append(" , C AS ( SELECT COUNT(DISTINCT ID) AS CONTENT_COUNT FROM CONTENT WHERE ACTIVE = 'Y' AND LOWER(TYPE) = 'activities') ");
		if (!u.isAdmin()) {
			if (u.isStaff() || Operator.hasValue(nproles)) {
				sb.append(" 	,TANP AS ( ");
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
			sb.append(" 	UT AS ( ");
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
			sb.append(" 	) ");
		}
		sb.append(" , Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	M.ID, ");
		sb.append(" 	M.ID AS LINKID,"); 
		sb.append(" 	'activity' AS LINKTYPE, ");
		sb.append(" 	M.ACT_NBR,"); 
		sb.append(" 	M.DESCRIPTION,"); 
		sb.append(" 	TY.DESCRIPTION AS TYPE, "); 
		sb.append(" 	M.VALUATION_DECLARED, "); 
		sb.append(" 	M.VALUATION_CALCULATED, "); 
		sb.append(" 	M.APPLIED_DATE, "); 
		sb.append(" 	M.START_DATE, "); 
		sb.append(" 	M.ISSUED_DATE, "); 
		sb.append(" 	M.FINAL_DATE, "); 
		sb.append(" 	M.EXP_DATE, "); 
		sb.append(" 	M.APPLICATION_EXP_DATE, "); 
		sb.append(" 	M.PLAN_CHK_REQ, "); 
		sb.append(" 	M.SENSITIVE, "); 
		sb.append(" 	M.INHERIT, "); 
		sb.append(" 	S.STATUS, "); 
		sb.append(" 	S.LIVE, "); 
		sb.append(" 	S.ISSUED, "); 
		sb.append(" 	S.EXPIRED, "); 
		sb.append(" 	S.FINAL, "); 
		sb.append(" 	CASE ");
		sb.append(" 		WHEN S.EXPIRED = 'Y' THEN 'N' ");
		sb.append(" 		WHEN S.LIVE = 'N' THEN 'N' ");
		sb.append(" 		WHEN S.FINAL = 'Y' THEN 'N' ");
		sb.append(" 		WHEN COALESCE(CAST(M.EXP_DATE AS DATE), getDate() + 1)  < getDate() THEN 'N' ");
		sb.append(" 		ELSE 'Y' "); 
		sb.append(" 	END AS ACTIVE, ");
		sb.append(" 	CU.USERNAME AS CREATED, "); 
		sb.append(" 	UP.USERNAME as UPDATED, "); 
		sb.append(" 	M.UPDATED_DATE, "); 
		sb.append(" 	COALESCE(T.FEE_AMOUNT, 0) AS FEE_AMOUNT, "); 
		sb.append(" 	COALESCE(T.FEE_PAID, 0) AS FEE_PAID, ");
		sb.append(" 	COALESCE(T.BALANCE_DUE, 0) AS BALANCE_DUE, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN C.CONTENT_COUNT IS NULL THEN '' ");
		sb.append(" 		WHEN C.CONTENT_COUNT > 0 THEN 'activities' ");
		sb.append(" 	ELSE '' END AS CONTENT_TYPE ");
		sb.append(" FROM ");
		sb.append(" TOTAL AS T ");
		sb.append(" JOIN MAIN AS M ON T.REF_ID = M.REF_ID ");
		sb.append(" JOIN LKUP_ACT_STATUS AS S ON M.LKUP_ACT_STATUS_ID = S.ID ");
		sb.append(" JOIN LKUP_ACT_TYPE AS TY ON M.LKUP_ACT_TYPE_ID = TY.ID ");
		if (!u.isAdmin()) {
			sb.append(" 		JOIN UT ON TY.ID = UT.ID AND (S.ISPUBLIC = 'Y' OR COALESCE(UT.REQUIRE_PUBLIC, 'N') = 'N') ");
		}
		sb.append(" LEFT OUTER JOIN USERS AS CU ON M.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS UP ON M.UPDATED_BY = UP.ID ");
		sb.append(" LEFT OUTER JOIN C ON 1 = 1 ");
		sb.append(" ) ");
		sb.append(" , U AS ( ");
		sb.append(" SELECT *, 0 AS ORDR FROM Q ");
		sb.append(" UNION ");
		sb.append(" SELECT ");
		sb.append(" 	1 AS ID, ");
		sb.append(" 	-1 AS LINKID,"); 
		sb.append(" 	'' AS LINKTYPE, ");
		sb.append(" 	'TOTAL' AS ACT_NBR,"); 
		sb.append(" 	'' AS DESCRIPTION,"); 
		sb.append(" 	'' AS TYPE, "); 
		sb.append(" 	-1 AS VALUATION_DECLARED, "); 
		sb.append(" 	-1 AS VALUATION_CALCULATED, "); 
		sb.append(" 	NULL AS APPLIED_DATE, "); 
		sb.append(" 	NULL AS START_DATE, "); 
		sb.append(" 	NULL AS ISSUED_DATE, "); 
		sb.append(" 	NULL AS FINAL_DATE, "); 
		sb.append(" 	NULL AS EXP_DATE, "); 
		sb.append(" 	NULL AS APPLICATION_EXP_DATE, "); 
		sb.append(" 	'' AS PLAN_CHK_REQ, "); 
		sb.append(" 	'' AS SENSITIVE, "); 
		sb.append(" 	'' AS INHERIT, "); 
		sb.append(" 	'' AS STATUS, "); 
		sb.append(" 	'' AS LIVE, "); 
		sb.append(" 	'' AS ISSUED, "); 
		sb.append(" 	'' AS EXPIRED, "); 
		sb.append(" 	'' AS FINAL, "); 
		sb.append(" 	'' AS ACTIVE, ");
		sb.append(" 	'' AS CREATED, "); 
		sb.append(" 	'' AS UPDATED, "); 
		sb.append(" 	NULL AS UPDATED_DATE, "); 
		sb.append(" 	SUM(FEE_AMOUNT) AS FEE_AMOUNT, "); 
		sb.append(" 	SUM(FEE_PAID) AS FEE_PAID, ");
		sb.append(" 	SUM(BALANCE_DUE) AS BALANCE_DUE, ");
		sb.append(" 	'' AS CONTENT_TYPE, ");
		sb.append(" 	10 AS ORDR ");
		sb.append(" FROM ");
		sb.append(" 	Q ");
		sb.append(" ) ");
		sb.append(" SELECT * FROM U ");
		sb.append(" ORDER BY ORDR, UPDATED_DATE DESC ");
		return sb.toString();
	}

	public static String entitySummary(String type, int typeid, int id, String option, Token u) {
		if (!Operator.hasValue(type) || typeid < 1) { return ""; }
		if (Operator.equalsIgnoreCase(type, "activity")) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String roles = u.roles();

		String nproles = Operator.join(u.getNonpublicroles(), ",");
		TypeInfo e = EntityAgent.getEntity(type, typeid);
		ArrayList<Integer> childid = e.getChildid();
		ArrayList<Integer> gchildid = e.getGrandchildid();
		StringBuilder esb = new StringBuilder();
		esb.append(typeid);
		for (int i=0; i<childid.size(); i++) {
			int c = childid.get(i);
			if (c > 0) {
				esb.append(",");
				esb.append(c);
			}
		}
		for (int i=0; i<gchildid.size(); i++) {
			int gc = gchildid.get(i);
			if (gc > 0) {
				esb.append(",");
				esb.append(gc);
			}
		}
		String in = esb.toString();

		StringBuilder sb = new StringBuilder();
		sb.append(" WITH ");
		if (!u.isAdmin()) {
			sb.append(" 	TP AS ( ");
			sb.append(" 		SELECT ");
			sb.append(" 			PT.ID ");
			sb.append(" 		FROM ");
			sb.append(" 			LKUP_PROJECT_TYPE AS PT ");
			sb.append(" 			JOIN REF_PROJECT_TYPE_ROLES AS PR ON PT.ID = PR.LKUP_PROJECT_TYPE_ID AND PR.ACTIVE = 'Y' ");
			sb.append(" 			JOIN LKUP_ROLES AS R ON PR.LKUP_ROLES_ID = R.ID AND R.ACTIVE = 'Y' AND ( R.EVERYONE = 'Y' ");
			if (Operator.hasValue(roles)) {
				sb.append(" 			OR PR.LKUP_ROLES_ID IN (").append(roles).append(") ");
			}
			sb.append(" 			) ");
			if (Operator.hasValue(u.getNonpublicroles())) {
				sb.append(" 		UNION ");
				sb.append(" 		SELECT ");
				sb.append(" 			PT.ID ");
				sb.append(" 		FROM ");
				sb.append(" 			LKUP_PROJECT_TYPE AS PT ");
				sb.append(" 		WHERE ");
				sb.append(" 			PT.ID NOT IN (SELECT LKUP_PROJECT_TYPE_ID AS ID FROM REF_PROJECT_TYPE_ROLES AS PR WHERE PR.ACTIVE = 'Y') ");
			}
			sb.append(" 	), ");
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
			sb.append(" 	TA AS ( ");
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
		sb.append(" Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	A.*, ");
		sb.append(" 	A.ID AS LINKID,"); 
		sb.append(" 	'activity' AS LINKTYPE, ");
		sb.append(" 	TYPE.DESCRIPTION AS TYPE, "); 
		sb.append(" 	P.PROJECT_NBR, "); 
		sb.append(" 	P.NAME AS PROJECT, "); 
		if (Operator.equalsIgnoreCase(type, "lso")) {
			sb.append("   CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(ST.PRE_DIR, '') + ' ' + ST.STR_NAME + ' ' + ST.STR_TYPE + ' ' + COALESCE(L.UNIT, '') AS ADDRESS, ");
			sb.append("   CASE WHEN L.DESCRIPTION IS NOT NULL AND L.DESCRIPTION != '' THEN L.DESCRIPTION ");
			sb.append("   ELSE CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(ST.PRE_DIR, '') + ' ' + ST.STR_NAME + ' ' + ST.STR_TYPE + ' ' + COALESCE(L.UNIT, '') ");
			sb.append("   END AS BUILDING, ");
		}
		sb.append(" 	S.STATUS, "); 
		sb.append(" 	S.LIVE, "); 
		sb.append(" 	S.ISSUED, "); 
		sb.append(" 	S.EXPIRED, "); 
		sb.append(" 	S.FINAL "); 
		sb.append(" FROM ");
		sb.append(" 	ACTIVITY AS A ");
		sb.append(" 	JOIN LKUP_ACT_TYPE TYPE ON A.LKUP_ACT_TYPE_ID = TYPE.ID AND A.ACTIVE = 'Y' ");
		sb.append(" 	JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID ");
		sb.append(" 	JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append(" 	JOIN REF_").append(tableref).append("_PROJECT AS E ON E.ACTIVE = 'Y' AND P.ID = E.PROJECT_ID AND E.").append(idref).append(" IN (").append(in).append(") ");
		if (Operator.equalsIgnoreCase(type, "lso")) {
			sb.append(" JOIN LSO AS L ON E.LSO_ID = L.ID ");
			sb.append(" JOIN LSO_STREET AS ST ON L.LSO_STREET_ID = ST.ID ");
		}
		if (!u.isAdmin()) {
			sb.append(" 		JOIN TA ON TYPE.ID = TA.ID AND (S.ISPUBLIC = 'Y' OR COALESCE(TA.REQUIRE_PUBLIC, 'N') = 'N') ");
			sb.append(" 		JOIN TP ON P.LKUP_PROJECT_TYPE_ID = TP.ID ");
		}
		if (Operator.equalsIgnoreCase(option, "INACTIVE")) {
			sb.append(" WHERE (S.EXPIRED = 'Y' OR COALESCE(CAST(A.EXP_DATE AS DATE), getDate() + 1) < getDate()) ");
		}
		else if (Operator.equalsIgnoreCase(option, "ALL")) {
		}
		else {
			sb.append(" WHERE S.EXPIRED = 'N'  AND COALESCE(CAST(A.EXP_DATE AS DATE), getDate()+1) > getDate() ");
		}
		sb.append(" ) ");
		sb.append(" SELECT * FROM Q ORDER BY ACT_NBR DESC");

		return sb.toString();
	}

	public static String projectSummary(int projectid, String option, Token u) {
		String roles = u.roles();
		String nproles = Operator.join(u.getNonpublicroles(), ",");

		StringBuilder sb = new StringBuilder();
		sb.append(" WITH ");
		if (!u.isAdmin()) {
			sb.append(" 	TP AS ( ");
			sb.append(" 		SELECT ");
			sb.append(" 			PT.ID ");
			sb.append(" 		FROM ");
			sb.append(" 			LKUP_PROJECT_TYPE AS PT ");
			sb.append(" 			JOIN REF_PROJECT_TYPE_ROLES AS PR ON PT.ID = PR.LKUP_PROJECT_TYPE_ID AND PR.ACTIVE = 'Y' ");
			sb.append(" 			JOIN LKUP_ROLES AS R ON PR.LKUP_ROLES_ID = R.ID AND R.ACTIVE = 'Y' AND ( R.EVERYONE = 'Y' ");
			if (Operator.hasValue(roles)) {
				sb.append(" 			OR PR.LKUP_ROLES_ID IN (").append(roles).append(") ");
			}
			sb.append(" 			) ");
			if (Operator.hasValue(u.getNonpublicroles())) {
				sb.append(" 		UNION ");
				sb.append(" 		SELECT ");
				sb.append(" 			PT.ID ");
				sb.append(" 		FROM ");
				sb.append(" 			LKUP_PROJECT_TYPE AS PT ");
				sb.append(" 		WHERE ");
				sb.append(" 			PT.ID NOT IN (SELECT LKUP_PROJECT_TYPE_ID AS ID FROM REF_PROJECT_TYPE_ROLES AS PR WHERE PR.ACTIVE = 'Y') ");
			}
			sb.append(" 	), ");
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
			sb.append(" 	TA AS ( ");
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
		sb.append(" Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	A.*, ");
		sb.append(" 	A.ID AS LINKID,"); 
		sb.append(" 	'activity' AS LINKTYPE, ");
		sb.append(" 	TYPE.DESCRIPTION AS TYPE, "); 
		sb.append(" 	P.PROJECT_NBR, "); 
		sb.append(" 	P.NAME AS PROJECT, "); 
		sb.append("     CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(ST.PRE_DIR, '') + ' ' + ST.STR_NAME + ' ' + ST.STR_TYPE + ' ' + COALESCE(L.UNIT, '') AS ADDRESS, ");
		sb.append("     CASE WHEN L.DESCRIPTION IS NOT NULL AND L.DESCRIPTION != '' THEN L.DESCRIPTION ");
		sb.append("     ELSE CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(ST.PRE_DIR, '') + ' ' + ST.STR_NAME + ' ' + ST.STR_TYPE + ' ' + COALESCE(L.UNIT, '') ");
		sb.append("     END AS BUILDING, ");
		sb.append(" 	S.STATUS, "); 
		sb.append(" 	S.LIVE, "); 
		sb.append(" 	S.ISSUED, "); 
		sb.append(" 	S.EXPIRED, "); 
		sb.append(" 	S.FINAL "); 
		sb.append(" FROM ");
		sb.append(" 	ACTIVITY AS A ");
		sb.append(" 	JOIN LKUP_ACT_TYPE TYPE ON A.LKUP_ACT_TYPE_ID = TYPE.ID AND A.ACTIVE = 'Y' ");
		sb.append(" 	JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID ");
		sb.append(" 	JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' AND P.ID = ").append(projectid);
		sb.append(" 	JOIN REF_LSO_PROJECT AS E ON E.ACTIVE = 'Y' AND P.ID = E.PROJECT_ID ");
		sb.append(" JOIN LSO AS L ON E.LSO_ID = L.ID ");
		sb.append(" JOIN LSO_STREET AS ST ON L.LSO_STREET_ID = ST.ID ");
		if (!u.isAdmin()) {
			sb.append(" 		JOIN TA ON TYPE.ID = TA.ID AND (S.ISPUBLIC = 'Y' OR COALESCE(TA.REQUIRE_PUBLIC, 'N') = 'N') ");
			sb.append(" 		JOIN TP ON P.LKUP_PROJECT_TYPE_ID = TP.ID ");
		}
		if (Operator.equalsIgnoreCase(option, "INACTIVE")) {
			sb.append(" WHERE (S.EXPIRED = 'Y' OR COALESCE(CAST(A.EXP_DATE AS DATE), getDate() + 1) < getDate()) ");
		}
		else if (Operator.equalsIgnoreCase(option, "ALL")) {
		}
		else {
			sb.append(" WHERE S.EXPIRED = 'N'  AND COALESCE(CAST(A.EXP_DATE AS DATE), getDate()+1) > getDate() ");
		}
		sb.append(" ) ");
		sb.append(" SELECT * FROM Q ORDER BY ACT_NBR DESC");

		return sb.toString();
	}

	public static String summary1(String type, int typeid, int id, String option) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH F AS ( ");
		sb.append(" select    ");
		sb.append(" A.ID,'TOTAL' as GROUPNAME,0 as GROUP_ID,  SUM(SD.FEE_AMOUNT) as AMOUNT , SUM(SD.FEE_PAID) as PAID,SUM(SD.BALANCE_DUE) as BALANCE ");
		sb.append(" from ACTIVITY A  ");
		sb.append(" join LKUP_ACT_STATUS AS ST ON A.LKUP_ACT_STATUS_ID = ST.ID ");
		if (Operator.equalsIgnoreCase(option, "INACTIVE")) {
			sb.append(" AND (ST.EXPIRED = 'Y' OR COALESCE(CAST(A.EXP_DATE AS DATE), getDate() + 1) < getDate()) ");
		}
		else if (Operator.equalsIgnoreCase(option, "ALL")) {
		}
		else {
			sb.append(" AND ST.EXPIRED = 'N' AND COALESCE(CAST(A.EXP_DATE AS DATE), getDate()+1) > getDate() ");
		}
		sb.append(" join REF_ACT_STATEMENT RAS on A.ID=RAS.ACTIVITY_ID   ");
		sb.append(" join STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y'   ");
		sb.append(" join STATEMENT_DETAIL SD on  S.ID=SD.STATEMENT_ID  ");
		sb.append(" join FEE_GROUP FG   on SD.GROUP_ID=FG.ID ");
		sb.append(" where ");
		sb.append(" A.PROJECT_ID = ").append(typeid).append(" ");
		sb.append(" group by A.ID ");
		sb.append(" ) ");			
		
		sb.append(" SELECT "); 
		sb.append("   A.ID,"); 
		sb.append("   A.ID AS LINKID,"); 
		sb.append("   'activity' AS LINKTYPE, ");
		sb.append("   A.ACT_NBR,"); 
		sb.append("   ATT.DESCRIPTION AS TYPE, "); 
		sb.append("   LAS.STATUS, "); 
		sb.append("   CU.USERNAME AS CREATED, "); 
		sb.append("   UP.USERNAME as UPDATED, "); 
		sb.append("   F.AMOUNT, "); 
		sb.append("   F.PAID, ");
		sb.append("   F.BALANCE ");
		
		sb.append(" FROM "); 
		sb.append("   ").append(Table.ACTIVITYTABLE).append(" A ");
		sb.append("   JOIN  ").append(Table.ACTTYPETABLE).append(" ATT ON A.LKUP_ACT_TYPE_ID = ATT.ID ");
		sb.append("   JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID = LAS.ID ");
		if (Operator.equalsIgnoreCase(option, "INACTIVE")) {
			sb.append(" AND (LAS.EXPIRED = 'Y' OR COALESCE(CAST(A.EXP_DATE AS DATE), getDate() + 1) < getDate()) ");
		}
		else if (Operator.equalsIgnoreCase(option, "ALL")) {
		}
		else {
			sb.append(" AND LAS.EXPIRED = 'N'  AND COALESCE(CAST(A.EXP_DATE AS DATE), getDate()+1) > getDate() ");
		}
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append("	  LEFT OUTER JOIN F on A.ID = F.ID "); 
		sb.append(" WHERE "); 
		sb.append("   A.PROJECT_ID = ").append(typeid).append(" "); 
		sb.append("   AND "); 
		sb.append("   A.ACTIVE = 'Y' ");
		
		
		sb.append(" union select    ");
		sb.append(" 999999999 as   ID,"); 
		sb.append("   -1 AS LINKID,"); 
		sb.append("   '' AS LINKTYPE, ");
		sb.append("  '' as ACT_NBR,"); 
		sb.append("  ''   AS TYPE, "); 
		sb.append(" 'TOTAL' as   STATUS, "); 
		sb.append("  '' as  CREATED, "); 
		sb.append("  '' as  UPDATED, "); 
		sb.append("  SUM(SD.FEE_AMOUNT) as AMOUNT , SUM(SD.FEE_PAID) as PAID,SUM(SD.BALANCE_DUE) as BALANCE ");
		sb.append(" from ACTIVITY A  ");
		sb.append(" join LKUP_ACT_STATUS AS ST ON A.LKUP_ACT_STATUS_ID = ST.ID ");
		if (Operator.equalsIgnoreCase(option, "INACTIVE")) {
			sb.append(" AND (ST.EXPIRED = 'Y' OR COALESCE(CAST(A.EXP_DATE AS DATE), getDate() + 1) < getDate()) ");
		}
		else if (Operator.equalsIgnoreCase(option, "ALL")) {
		}
		else {
			sb.append(" AND ST.EXPIRED = 'N'  AND COALESCE(CAST(A.EXP_DATE AS DATE), getDate()+1) > getDate() ");
		}
		sb.append(" join REF_ACT_STATEMENT RAS on A.ID=RAS.ACTIVITY_ID   ");
		sb.append(" join STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y'   ");
		sb.append(" join STATEMENT_DETAIL SD on  S.ID=SD.STATEMENT_ID  ");
		sb.append(" join FEE_GROUP FG   on SD.GROUP_ID=FG.ID ");
		sb.append(" where A.PROJECT_ID= ").append(typeid).append("  ");
		return sb.toString();
	}

	public static String getActivityIds(int projectid, String option) {
		if (projectid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.ID ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID AND A.ACTIVE = 'Y' AND A.PROJECT_ID = ").append(projectid);
		if (Operator.equalsIgnoreCase(option, "INACTIVE")) {
			sb.append(" WHERE (S.EXPIRED = 'Y' OR COALESCE(CAST(A.EXP_DATE AS DATE), getDate() + 1) < getDate()) ");
		}
		else if (Operator.equalsIgnoreCase(option, "ALL")) {
		}
		else {
			sb.append(" WHERE S.EXPIRED = 'N'  AND COALESCE(CAST(A.EXP_DATE AS DATE), getDate()+1) > getDate() ");
		}
		return sb.toString();
	}

	public static String getProjectActivities(int projectid, Token u) {
		return summary("project", projectid, -1, "ALL", u);
	}

	public static String updateActivities(String[] actids, String status, String applied, String start, String issued, String appexp, String exp, String finaldate, String valuationcalculated, String valuationdeclared, String plancheck, String inherit, String sensitive, int updater, String ip) {
		if (!Operator.hasValue(actids)) { return ""; }
		if (
				!Operator.hasValue(applied)
				&& !Operator.hasValue(start)
				&& !Operator.hasValue(issued)
				&& !Operator.hasValue(appexp)
				&& !Operator.hasValue(exp)
				&& !Operator.hasValue(finaldate)
				&& !Operator.hasValue(valuationcalculated)
				&& !Operator.hasValue(valuationdeclared)
				&& !Operator.hasValue(plancheck)
				&& !Operator.hasValue(inherit)
				&& !Operator.hasValue(sensitive)
				&& !Operator.hasValue(status)
		) { return ""; }

		boolean empty = true;
		StringBuilder a = new StringBuilder();
		for (int i=0; i<actids.length; i++) {
			int actid = Operator.toInt(actids[i]);
			if (actid > 0) {
				if (!empty) { a.append(","); }
				a.append(actid);
				empty = false;
			}
		}
		String joined = a.toString();
		if (!Operator.hasValue(joined)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE  ACTIVITY SET ");
		sb.append(" 	UPDATED_BY = ").append(updater);
		sb.append(" 	, ");
		sb.append(" 	UPDATED_DATE = getDate() ");
		sb.append(" 	, ");
		sb.append(" 	UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		if (Operator.hasValue(status)) {
			int statusid = Operator.toInt(status);
			if (statusid > 0) {
				sb.append(" , ");
				sb.append(" LKUP_ACT_STATUS_ID = ").append(statusid);
			}
		}
		if (Operator.hasValue(applied)) {
			Timekeeper d = new Timekeeper();
			d.setDate(applied);
			sb.append(" , ");
			sb.append(" APPLIED_DATE = '").append(d.getString("YYYY-MM-DD")).append("' ");
		}
		if (Operator.hasValue(start)) {
			Timekeeper d = new Timekeeper();
			d.setDate(start);
			sb.append(" , ");
			sb.append(" START_DATE = ").append(d.sqlDatetime());
		}
		if (Operator.hasValue(issued)) {
			Timekeeper d = new Timekeeper();
			d.setDate(issued);
			sb.append(" , ");
			sb.append(" ISSUED_DATE = '").append(d.getString("YYYY-MM-DD")).append("' ");
		}
		if (Operator.hasValue(appexp)) {
			Timekeeper d = new Timekeeper();
			d.setDate(appexp);
			sb.append(" , ");
			sb.append(" APPLICATION_EXP_DATE = ").append(d.sqlDatetime());
		}
		if (Operator.hasValue(exp)) {
			Timekeeper d = new Timekeeper();
			d.setDate(exp);
			sb.append(" , ");
			sb.append(" EXP_DATE = '").append(d.getString("YYYY-MM-DD")).append("' ");
		}
		if (Operator.hasValue(finaldate)) {
			Timekeeper d = new Timekeeper();
			d.setDate(finaldate);
			sb.append(" , ");
			sb.append(" FINAL_DATE = '").append(d.getString("YYYY-MM-DD")).append("' ");
		}
		if (Operator.hasValue(valuationcalculated)) {
			sb.append(" , ");
			sb.append(" VALUATION_CALCULATED = ").append(Operator.toDouble(valuationcalculated));
		}
		if (Operator.hasValue(valuationdeclared)) {
			sb.append(" , ");
			sb.append(" VALUATION_DECLARED = ").append(Operator.toDouble(valuationdeclared));
		}
		if (Operator.hasValue(plancheck)) {
			if (Operator.equalsIgnoreCase(plancheck, "Y")) {
				sb.append(" , ");
				sb.append(" PLAN_CHK_REQ = 'Y' ");
			}
			else if (Operator.equalsIgnoreCase(plancheck, "N")) {
				sb.append(" , ");
				sb.append(" PLAN_CHK_REQ = 'N' ");
			}
		}
		if (Operator.hasValue(inherit)) {
			if (Operator.equalsIgnoreCase(inherit, "Y")) {
				sb.append(" , ");
				sb.append(" INHERIT = 'Y' ");
			}
			else if (Operator.equalsIgnoreCase(inherit, "N")) {
				sb.append(" , ");
				sb.append(" INHERIT = 'N' ");
			}
		}
		if (Operator.hasValue(sensitive)) {
			if (Operator.equalsIgnoreCase(sensitive, "Y")) {
				sb.append(" , ");
				sb.append(" SENSITIVE = 'Y' ");
			}
			else if (Operator.equalsIgnoreCase(sensitive, "N")) {
				sb.append(" , ");
				sb.append(" SENSITIVE = 'N' ");
			}
		}
		sb.append(" WHERE ");
		sb.append(" 	ID IN (").append(joined).append(") ");
		return sb.toString();
	}

	public static String status(String[] actids) {
		boolean empty = true;
		StringBuilder a = new StringBuilder();
		for (int i=0; i<actids.length; i++) {
			int actid = Operator.toInt(actids[i]);
			if (actid > 0) {
				if (!empty) { a.append(","); }
				a.append(actid);
				empty = false;
			}
		}
		String joined = a.toString();

		StringBuilder sb = new StringBuilder();
		sb = new StringBuilder();
		sb.append(" WITH T AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		A.LKUP_ACT_TYPE_ID AS ID ");
		sb.append(" 	FROM ");
		sb.append(" 		ACTIVITY AS A ");
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
		sb.append(" 		ST.LKUP_ACT_TYPE_ID IN (SELECT ID FROM T) ");
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

	public static String psearch(String query, int page, int resultsperpage, Token u) {
		if (!Operator.hasValue(query)) { return ""; }
		if (page < 1) { page = 1; }
		int offset = (resultsperpage * page) - resultsperpage;
		String nproles = Operator.join(u.getNonpublicroles(), ",");
		String roles = u.roles();
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH ");
		if (!u.isAdmin()) {
			sb.append(" 	T AS ( ");
			sb.append(" 		SELECT ");
			sb.append(" 			PT.ID ");
			sb.append(" 		FROM ");
			sb.append(" 			LKUP_PROJECT_TYPE AS PT ");
			sb.append(" 			JOIN REF_PROJECT_TYPE_ROLES AS PR ON PT.ID = PR.LKUP_PROJECT_TYPE_ID AND PR.ACTIVE = 'Y' ");
			sb.append(" 			JOIN LKUP_ROLES AS R ON PR.LKUP_ROLES_ID = R.ID AND R.ACTIVE = 'Y' AND ( R.EVERYONE = 'Y' ");
			if (Operator.hasValue(roles)) {
				sb.append(" 			OR PR.LKUP_ROLES_ID IN (").append(roles).append(") ");
			}
			sb.append(" 			) ");
			if (Operator.hasValue(u.getNonpublicroles())) {
				sb.append(" 		UNION ");
				sb.append(" 		SELECT ");
				sb.append(" 			PT.ID ");
				sb.append(" 		FROM ");
				sb.append(" 			LKUP_PROJECT_TYPE AS PT ");
				sb.append(" 		WHERE ");
				sb.append(" 			PT.ID NOT IN (SELECT LKUP_PROJECT_TYPE_ID AS ID FROM REF_PROJECT_TYPE_ROLES AS PR WHERE PR.ACTIVE = 'Y') ");
			}
			sb.append(" 	), ");
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
			sb.append(" 	TA AS ( ");
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

		sb.append(" Q1 AS ( ");
		sb.append(" SELECT DISTINCT ");
		sb.append("   A.ID ");
		sb.append("   , ");
		sb.append("   A.ID AS VALUE ");
		sb.append("   , ");
		sb.append("   A.ACT_NBR ");
		sb.append("   , ");
		sb.append("   A.ACT_NBR AS TEXT ");
		sb.append("   , ");
		sb.append("   A.START_DATE ");
		sb.append("   , ");
		sb.append("   A.APPLIED_DATE ");
		sb.append("   , ");
		sb.append("   A.ISSUED_DATE ");
		sb.append("   , ");
		sb.append("   A.EXP_DATE ");
		sb.append("   , ");
		sb.append("   A.FINAL_DATE ");
		sb.append("   , ");
		sb.append("   A.CREATED_DATE ");
		sb.append("   , ");
		sb.append("   A.UPDATED_DATE ");
		sb.append("   , ");
		sb.append("   TYPE.TYPE ");
		sb.append("   , ");
		sb.append("   S.STATUS ");
		sb.append("   , ");
		sb.append("   P.ID AS PROJECT_ID ");
		sb.append("   , ");
		sb.append("   P.PROJECT_NBR ");
		sb.append("   , ");
		sb.append("   P.NAME AS PROJECT_NAME ");
		sb.append("   , ");
		sb.append("   PT.TYPE AS PROJECT_TYPE ");
		sb.append("   , ");
		sb.append("   L.STR_NO ");
		sb.append("   , ");
		sb.append("   L.STR_MOD ");
		sb.append("   , ");
		sb.append("   STR.PRE_DIR ");
		sb.append("   , ");
		sb.append("   STR.STR_NAME ");
		sb.append("   , ");
		sb.append("   STR.STR_TYPE ");
		sb.append("   , ");
		sb.append("   L.UNIT ");
		sb.append("   , ");
		sb.append("   LTRIM(RTRIM( ");
		sb.append("     CAST(L.STR_NO AS VARCHAR(25)) + ");
		sb.append("     CASE  ");
		sb.append("       WHEN L.STR_MOD IS NOT NULL AND L.STR_MOD <> '' THEN ' ' + L.STR_MOD ");
		sb.append("       ELSE ''  ");
		sb.append("     END +  ");
		sb.append("     CASE  ");
		sb.append("       WHEN STR.PRE_DIR IS NOT NULL AND STR.PRE_DIR <> '' THEN ' ' + STR.PRE_DIR ");
		sb.append("       ELSE ''  ");
		sb.append("     END +  ");
		sb.append("     CASE  ");
		sb.append("       WHEN STR.STR_NAME IS NOT NULL AND STR.STR_NAME <> '' THEN ' ' + STR.STR_NAME ");
		sb.append("       ELSE ''  ");
		sb.append("     END +  ");
		sb.append("     CASE  ");
		sb.append("       WHEN STR.STR_TYPE IS NOT NULL AND STR.STR_TYPE <> '' THEN ' ' + STR.STR_TYPE ");
		sb.append("       ELSE '' ");
		sb.append("     END + ");
		sb.append("     CASE  ");
		sb.append("       WHEN STR.SUF_DIR IS NOT NULL AND STR.SUF_DIR <> '' THEN ' ' + STR.SUF_DIR ");
		sb.append("       ELSE '' ");
		sb.append("     END + ");
		sb.append("     CASE  ");
		sb.append("       WHEN L.UNIT IS NOT NULL AND L.UNIT <> '' THEN ' ' + L.UNIT ");
		sb.append("       ELSE '' ");
		sb.append("     END ");
		sb.append(" )) AS ADDRESS ");
		sb.append(" FROM ");
		sb.append("   ACTIVITY AS A ");
		sb.append("   JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND A.ACTIVE = 'Y' AND P.ACTIVE = 'Y' ");
		sb.append("   JOIN LKUP_ACT_TYPE TYPE ON A.LKUP_ACT_TYPE_ID = TYPE.ID ");
		sb.append("   JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID ");
		sb.append("   JOIN LKUP_PROJECT_TYPE AS PT ON P.LKUP_PROJECT_TYPE_ID = PT.ID ");
		sb.append("   JOIN REF_LSO_PROJECT AS RP ON P.ID = RP.PROJECT_ID AND RP.ACTIVE = 'Y' ");
		sb.append("   JOIN LSO AS L ON RP.LSO_ID = L.ID AND L.ACTIVE = 'Y' ");
		sb.append("   JOIN LSO_STREET AS STR ON L.LSO_STREET_ID = STR.ID ");
		if (!u.isStaff()) {
			sb.append(" AND L.ISPUBLIC = 'Y' ");
		}
		if (!u.isAdmin()) {
			sb.append("     JOIN T ON PT.ID = T.ID ");
			sb.append(" 	JOIN TA ON TYPE.ID = TA.ID AND (S.ISPUBLIC = 'Y' OR COALESCE(TA.REQUIRE_PUBLIC, 'N') = 'N') ");
		}
		sb.append(" WHERE ");
		sb.append("   LOWER(A.ACT_NBR) = '").append(Operator.sqlEscape(query.toLowerCase().trim())).append("' ");
		sb.append(" ), ");
		sb.append(" C AS ( SELECT COUNT(*) AS RESULTS FROM Q1 ) ");

		sb.append(" SELECT C.RESULTS, Q1.* FROM Q1 JOIN C ON 1 = 1 ");
		sb.append(" ORDER BY ACT_NBR, APPLIED_DATE DESC, CREATED_DATE DESC ");
		sb.append(" OFFSET ").append(offset).append(" ROWS FETCH NEXT ").append(resultsperpage).append(" ROWS ONLY ");
		return sb.toString();
	}
	







}



















