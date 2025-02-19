package csapi.impl.project;

import java.util.ArrayList;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.divisions.DivisionsAgent;
import csapi.impl.entity.EntityAgent;
import csapi.impl.general.GeneralSQL;
import csapi.utils.CsReflect;
import csapi.utils.CsTools;
import csshared.vo.DivisionsList;
import csshared.vo.RequestVO;
import csshared.vo.TypeInfo;

public class ProjectSQL {

	public static String browse(String type, int typeid, String active, String expired, String sublevel, Token u) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		if (!entity.isIspublic()) {
			if (!u.isAdmin() && !u.isStaff()) { return ""; }
		}
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String roles = u.roles();
		String nproles = Operator.join(u.getNonpublicroles(), ",");
		boolean exp = Operator.equalsIgnoreCase(expired, "Y");
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
		sb.append(" 	P AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		PR.ID, ");
		sb.append(" 		PR.NAME, ");
		sb.append(" 		PR.PROJECT_NBR, ");
		sb.append(" 		PR.DESCRIPTION, ");
		sb.append(" 		PR.APPLIED_DT, ");
		sb.append(" 		PR.EXPIRED_DT, ");
		sb.append(" 		CASE WHEN S.EXPIRED = 'Y' THEN 'Y' WHEN PR.EXPIRED_DT IS NULL THEN 'N' WHEN CAST(PR.EXPIRED_DT AS DATE) >= CAST(getDate() AS DATE) THEN 'N' ELSE 'Y' END AS ISEXPIRED, ");
		sb.append(" 		PR.CREATED_DATE, ");
		sb.append(" 		S.EXPIRED, ");
		sb.append(" 		PR.LKUP_PROJECT_TYPE_ID ");
		sb.append(" 	FROM ");
		sb.append(" 		PROJECT AS PR ");
		sb.append(" 		JOIN LKUP_PROJECT_STATUS AS S ON PR.LKUP_PROJECT_STATUS_ID = S.ID AND PR.ACTIVE = 'Y' AND S.LIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_PROJECT_TYPE AS PT ON PR.LKUP_PROJECT_TYPE_ID = PT.ID ");
		if (!u.isAdmin()) {
			sb.append(" 		JOIN T ON PT.ID = T.ID ");
		}

		sb.append(" 		JOIN REF_").append(tableref).append("_PROJECT AS L ON PR.ID = L.PROJECT_ID AND L.ACTIVE = 'Y' AND L.").append(idref).append(" IN ( ");

		sb.append(typeid);
		if (Operator.equalsIgnoreCase(sublevel, "Y")) {
			ArrayList<Integer> ca = entity.getChildid();
			for (int i=0; i<ca.size(); i++) {
				int c = ca.get(i);
				if (c > 0) {
					sb.append(",").append(c);
				}
			}
			ArrayList<Integer> gca = entity.getGrandchildid();
			for (int i=0; i<gca.size(); i++) {
				int g = gca.get(i);
				if (g > 0) {
					sb.append(",").append(g);
				}
			}
		}
		sb.append(" 		) ");


		sb.append(" ), ");
		sb.append(" H AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		P.ID, T.TYPE, COUNT(H.ID) as NUM_HOLDS ");
		sb.append(" 	FROM ");
		sb.append(" 		P ");
		sb.append(" 		JOIN REF_PROJECT_HOLDS AS H ON P.ID = H.PROJECT_ID AND H.ACTIVE = 'Y' AND H.NEW_ID < 1 ");
		sb.append(" 		JOIN LKUP_HOLDS_TYPE AS T ON H.LKUP_HOLDS_TYPE_ID = T.ID AND T.TYPE IN ('H','S') ");
		sb.append(" 		JOIN LKUP_HOLDS_STATUS AS S ON H.LKUP_HOLDS_STATUS_ID = S.ID AND S.STATUS = 'A' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		P.ID, T.TYPE ");
		sb.append(" ), ");
		sb.append(" C AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		P.ID, ");
		sb.append(" 		COUNT(A.ID) AS CHILDREN ");
		sb.append(" 	FROM ");
		sb.append(" 		P ");
		sb.append(" 		JOIN ACTIVITY AS A ON P.ID = A.PROJECT_ID AND A.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID AND S.LIVE= 'Y' ");
		if (!exp) {
			sb.append(" AND CAST(COALESCE(A.EXP_DATE, getDate() + 1) AS DATE) >= CAST(getDate() AS DATE) AND S.EXPIRED = 'N' ");
		}
		if (!u.isAdmin()) {
			sb.append(" 		JOIN TA ON A.LKUP_ACT_TYPE_ID = TA.ID AND (S.ISPUBLIC = 'Y' OR COALESCE(TA.REQUIRE_PUBLIC, 'N') = 'N') ");
		}
		sb.append(" 	GROUP BY ");
		sb.append(" 		P.ID ");
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT  ");
		sb.append(" 	P.ID, ");
		sb.append(" 	P.NAME, ");
		sb.append(" 	P.PROJECT_NBR, ");
		sb.append(" 	P.DESCRIPTION, ");
		sb.append(" 	P.APPLIED_DT, ");
		sb.append(" 	P.EXPIRED_DT, ");
		sb.append(" 	P.ISEXPIRED, ");
		sb.append(" 	P.CREATED_DATE, ");
		sb.append(" 	P.EXPIRED, ");
		sb.append(" 	P.LKUP_PROJECT_TYPE_ID, ");
		sb.append(" 	COALESCE(C.CHILDREN, 0) AS CHILDREN, ");
		sb.append(" 	COALESCE(HH.NUM_HOLDS, 0) AS HARD_HOLDS, ");
		sb.append(" 	COALESCE(SH.NUM_HOLDS, 0) AS SOFT_HOLDS ");
		sb.append(" FROM ");
		sb.append(" 	P ");
		sb.append(" 	LEFT OUTER JOIN C ON P.ID = C.ID ");
		sb.append(" 	LEFT OUTER JOIN H AS HH ON P.ID = HH.ID AND HH.TYPE = 'H' ");
		sb.append(" 	LEFT OUTER JOIN H AS SH ON P.ID = SH.ID AND SH.TYPE = 'S' ");
		boolean w = false;
		if (!u.isStaff()) {
			if (!w) { sb.append(" WHERE "); }
			sb.append(" COALESCE(C.CHILDREN, 0) > 0 ");
			w = true;
		}
		if (!exp) {
			if (!w) { sb.append(" WHERE "); }
			else { sb.append(" AND "); }
			sb.append(" P.ISEXPIRED = 'N' ");
			w = true;
		}
		sb.append(" ORDER BY CREATED_DATE DESC,APPLIED_DT DESC,  NAME  ");
		return sb.toString();
	}

	public static String browse1(String type, int typeid, String active, String expired, Token u) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		boolean exp = Operator.equalsIgnoreCase(expired, "Y");
//		String roles = u.roles();

		StringBuilder sb = new StringBuilder();

		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append("   RH.PROJECT_ID ");
		sb.append("   , ");
		sb.append("   HT.TYPE AS HOLD ");
		sb.append("   , ");
		sb.append("   COUNT(RH.ID) as NUM_HOLDS  "); 
		sb.append(" FROM ");
		sb.append("    REF_PROJECT_HOLDS AS RH ");
		sb.append("    JOIN REF_").append(tableref).append("_PROJECT AS R ON RH.PROJECT_ID = R.PROJECT_ID AND R.ACTIVE = 'Y' AND RH.ACTIVE = 'Y' AND RH.NEW_ID < 1 "); 
		sb.append("    JOIN LKUP_HOLDS_TYPE AS HT ON HT.ID = RH.LKUP_HOLDS_TYPE_ID AND HT.ACTIVE = 'Y' AND HT.TYPE IN ('H','S') ");
		sb.append("    JOIN LKUP_HOLDS_STATUS AS HS ON HS.ID = RH.LKUP_HOLDS_STATUS_ID AND HS.ACTIVE = 'Y' AND HS.STATUS = 'A' ");
		sb.append(" WHERE "); 
		sb.append("   R.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" GROUP BY ");
		sb.append("   RH.PROJECT_ID ");
		sb.append("   , ");
		sb.append("   HT.TYPE ");
		sb.append(" ) ");

		sb.append(" , A AS ( ");
		sb.append(" SELECT ");
		sb.append(" AC.ID, AC.PROJECT_ID, AC.LKUP_ACT_TYPE_ID ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_PROJECT AS R "); 
		sb.append(" JOIN ACTIVITY AS AC ON R.PROJECT_ID = AC.PROJECT_ID AND AC.ACTIVE = 'Y' AND R.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" JOIN LKUP_ACT_STATUS AS ACTST ON AC.LKUP_ACT_STATUS_ID = ACTST.ID ");
		if (!exp) {
			sb.append(" AND CAST(COALESCE(AC.EXP_DATE, getDate() + 1) AS DATE) >= CAST(getDate() AS DATE) AND ACTST.EXPIRED = 'N' ");
		}
		sb.append(" ) ");

// COMMENTED BY ALAIN - REMOVE ACCESS ROLE CHECK
//		if (!u.isAdmin()) {
//			sb.append(", CRT AS ( ");
//			sb.append("   SELECT R.LKUP_ACT_TYPE_ID, COUNT(DISTINCT R.ID) AS NUM_ROLES ");
//			sb.append(" FROM ");
//			sb.append("   REF_ACT_TYPE_ROLES AS R ");
//			sb.append("   JOIN A ON R.LKUP_ACT_TYPE_ID = A.LKUP_ACT_TYPE_ID AND R.ACTIVE = 'Y' ");
//			sb.append(" GROUP BY R.LKUP_ACT_TYPE_ID ");
//			sb.append(" ) ");
//
//			if (Operator.hasValue(roles)) {
//				sb.append(" , ");
//				sb.append(" CRU AS ( ");
//				sb.append("   SELECT R.LKUP_ACT_TYPE_ID, COUNT(DISTINCT R.ID) AS U_ROLES ");
//				sb.append(" FROM ");
//				sb.append("   REF_ACT_TYPE_ROLES AS R ");
//				sb.append("   JOIN A ON R.LKUP_ACT_TYPE_ID = A.LKUP_ACT_TYPE_ID AND R.ACTIVE = 'Y' AND R.LKUP_ROLES_ID IN (").append(roles).append(") ");
//				sb.append(" GROUP BY R.LKUP_ACT_TYPE_ID ");
//				sb.append(" ) ");
//			}
//		}


		sb.append(", Q1 AS ( ");
		sb.append(" SELECT "); 
		sb.append("   P.ID, "); 
		sb.append("   P.PROJECT_NBR, "); 
		sb.append("   P.NAME, "); 
		sb.append("   P.DESCRIPTION, "); 
		sb.append("   P.APPLIED_DT,"); 
		sb.append("   P.EXPIRED_DT,");
		sb.append("   CASE WHEN LPS.EXPIRED = 'Y' THEN 'Y' WHEN P.EXPIRED_DT IS NULL THEN 'N' WHEN CAST(P.EXPIRED_DT AS DATE) >= CAST(getDate() AS DATE) THEN 'N' ELSE 'Y' END AS ISEXPIRED, ");
		sb.append("   P.CREATED_DATE,"); 
		sb.append("   LPS.EXPIRED,"); 
		sb.append("   P.LKUP_PROJECT_TYPE_ID, ");
		sb.append("   HH.NUM_HOLDS AS HARD_HOLDS, ");
		sb.append("   HS.NUM_HOLDS AS SOFT_HOLDS, ");
		sb.append("   COUNT(A.ID) as CHILDREN  "); 

		sb.append(" FROM REF_").append(tableref).append("_PROJECT AS R "); 
		sb.append("   JOIN PROJECT AS P ON R.PROJECT_ID = P.ID ");
		sb.append("   JOIN LKUP_PROJECT_STATUS AS LPS ON P.LKUP_PROJECT_STATUS_ID = LPS.ID ");

// COMMENTED BY ALAIN - REMOVE ACCESS ROLE CHECK
//		if (u.isAdmin()) {
			sb.append("   LEFT OUTER JOIN A ON A.PROJECT_ID = P.ID ");
//		}
//		else {
//			sb.append("   LEFT OUTER JOIN ( ");
//			sb.append("     A ");
//			sb.append("     LEFT OUTER JOIN CRT ON A.LKUP_ACT_TYPE_ID = CRT.LKUP_ACT_TYPE_ID ");
//			if (Operator.hasValue(roles)) {
//				sb.append(" LEFT OUTER JOIN CRU ON A.LKUP_ACT_TYPE_ID = CRU.LKUP_ACT_TYPE_ID ");
//			}
//			sb.append("   ) ON A.PROJECT_ID = P.ID ");
//			sb.append("   AND ( ");
//			sb.append("   COALESCE(CRT.NUM_ROLES, 0) = 0 ");
//			if (Operator.hasValue(roles)) {
//				sb.append(" OR ");
//				sb.append(" COALESCE(CRU.U_ROLES, 0) > 0 ");
//			}
//			sb.append(" ) ");
//		}

		sb.append("   LEFT OUTER JOIN Q AS HH ON HH.PROJECT_ID = P.ID AND HH.HOLD = 'H' ");
		sb.append("   LEFT OUTER JOIN Q AS HS ON HS.PROJECT_ID = P.ID AND HS.HOLD = 'S' ");
		sb.append(" WHERE "); 
		sb.append("   R.").append(idref).append(" = ").append(typeid).append(" AND R.ACTIVE='").append(Operator.sqlEscape(active)).append("' AND P.ACTIVE='").append(Operator.sqlEscape(active)).append("' ");
		sb.append(" GROUP BY P.ID, P.PROJECT_NBR, P.NAME, P.DESCRIPTION, P.APPLIED_DT, P.EXPIRED_DT, P.CREATED_DATE, LPS.EXPIRED, P.LKUP_PROJECT_TYPE_ID, HH.NUM_HOLDS, HS.NUM_HOLDS ");
		sb.append(" ) ");

// COMMENTED BY ALAIN - REMOVE ACCESS ROLE CHECK
//		if (!u.isAdmin()) {
//			sb.append(", RT AS ( ");
//			sb.append("   SELECT R.LKUP_PROJECT_TYPE_ID, COUNT(DISTINCT R.ID) AS NUM_ROLES ");
//			sb.append(" FROM ");
//			sb.append("   REF_PROJECT_TYPE_ROLES AS R ");
//			sb.append("   JOIN Q1 ON R.LKUP_PROJECT_TYPE_ID = Q1.LKUP_PROJECT_TYPE_ID AND R.ACTIVE = 'Y' ");
//			sb.append(" GROUP BY R.LKUP_PROJECT_TYPE_ID ");
//			sb.append(" ) ");
//
//			if (Operator.hasValue(roles)) {
//				sb.append(" , ");
//				sb.append(" RU AS ( ");
//				sb.append("   SELECT R.LKUP_PROJECT_TYPE_ID, COUNT(DISTINCT R.ID) AS U_ROLES ");
//				sb.append(" FROM ");
//				sb.append("   REF_PROJECT_TYPE_ROLES AS R ");
//				sb.append("   JOIN Q1 ON R.LKUP_PROJECT_TYPE_ID = Q1.LKUP_PROJECT_TYPE_ID AND R.ACTIVE = 'Y' AND R.LKUP_ROLES_ID IN (").append(roles).append(") ");
//				sb.append(" GROUP BY R.LKUP_PROJECT_TYPE_ID ");
//				sb.append(" ) ");
//			}
//		}

		sb.append(" SELECT ");
		sb.append(" Q1.* ");
		sb.append(" FROM ");
		sb.append(" Q1 ");
//		if (u.isAdmin()) {
//			if (!exp) {
//				sb.append(" WHERE ISEXPIRED = 'N' ");
//			}
//		}
//		else {
//			sb.append(" LEFT OUTER JOIN RT ON Q1.LKUP_PROJECT_TYPE_ID = RT.ID ");
// COMMENTED BY ALAIN - REMOVE ACCESS ROLE CHECK
//			if (Operator.hasValue(roles)) {
//				sb.append(" LEFT OUTER JOIN RU ON Q1.LKUP_PROJECT_TYPE_ID = RU.LKUP_PROJECT_TYPE_ID ");
//			}
//			sb.append(" WHERE ");
//			sb.append(" ( ");
//			sb.append(" COALESCE(RT.NUM_ROLES, 0) = 0 ");
//			if (Operator.hasValue(roles)) {
//				sb.append(" OR ");
//				sb.append(" COALESCE(RU.U_ROLES, 0) > 0 ");
//			}
//			sb.append(" ) ");

			if (!exp) {
				sb.append(" WHERE ISEXPIRED = 'N' ");
			}
//		}

		sb.append(" ORDER BY APPLIED_DT DESC, CREATED_DATE DESC, NAME  ");
		return sb.toString();
	}
	
	public static String type(int typeid){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT "); 
		sb.append(" P.ID, "); 
		sb.append(" P.NAME AS TITLE, "); 
		sb.append(" P.PROJECT_NBR AS SUBTITLE ");
		sb.append(" FROM PROJECT P ");
		sb.append(" JOIN LKUP_PROJECT_STATUS S ON P.LKUP_PROJECT_STATUS_ID = S.ID ");
		sb.append(" JOIN LKUP_PROJECT_TYPE ATP ON P.LKUP_PROJECT_TYPE_ID=ATP.ID ");
		
//		sb.append(" LEFT OUTER JOIN  REF_PROJECT_PARKING R ON P.ID=R.PROJECT_ID ");
		sb.append(" WHERE P.ID= ").append(typeid).append(" AND P.ACTIVE = 'Y' ");
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

	public static String details(String type, int typeid, int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( SELECT COUNT(DISTINCT ID) AS CONTENT_COUNT FROM CONTENT WHERE ACTIVE = 'Y' AND LOWER(TYPE) = 'project') ");
		sb.append(" SELECT "); 
		sb.append("   P.NAME AS TITLE, "); 
		sb.append("   P.PROJECT_NBR AS SUBTITLE, ");
		//sb.append("   P.PROJECT_NBR AS SUBTITLE, "); 
		sb.append("   ATT.STATUS as LKUP_PROJECT_STATUS_TEXT, "); 
		sb.append("   ATP.TYPE as LKUP_PROJECT_TYPE_TEXT, "); 
		sb.append("   CU.USERNAME AS CREATED, "); 
		sb.append("   UP.USERNAME as UPDATED, "); 
		sb.append("   CASE ");
		sb.append("     WHEN Q.CONTENT_COUNT IS NULL THEN '' ");
		sb.append("     WHEN Q.CONTENT_COUNT > 0 THEN 'project' ");
		sb.append("   ELSE '' END AS CONTENT_TYPE, ");
//		sb.append("   RAP.ID AS ACCOUNT_NUMBER, "); 
		sb.append("   P.* "); 
		sb.append(" FROM "); 
		sb.append("   ").append(Table.PROJECTTABLE).append(" P ");
		sb.append("   JOIN ").append(Table.LKUPPROJECTSTATUSTABLE).append(" ATT ON P.LKUP_PROJECT_STATUS_ID=ATT.ID ");
		sb.append("   JOIN ").append(Table.LKUPPROJECTTYPETABLE).append(" ATP ON P.LKUP_PROJECT_TYPE_ID=ATP.ID ");
//		sb.append("   LEFT OUTER JOIN ").append("REF_PROJECT_PARKING").append(" RAP on P.ID=RAP.PROJECT_ID ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on P.CREATED_BY=CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on P.UPDATED_BY=UP.ID "); 
		sb.append("   LEFT OUTER JOIN Q ON 1 = 1 ");
		sb.append(" WHERE "); 
		sb.append("   P.ID= ").append(typeid).append(" "); 
		sb.append("   AND "); 
		sb.append("   P.ACTIVE='Y' ");
		return sb.toString();
	}

	public static String addHistory(int id, String type, int lkupprojecttypeid, String projectnbr, String name, String description, String status, int lkupprojectstatusid, String cipacctno, String startdt, String completiondt, String applieddt, String expireddt, int lsouseid, int deptid, String microfilm, String active, int createdby, String createddate, String updated, int updatedby, String updateddate, String createdip, String updatedip, String accountnumber, String entity, String modulechanged, int modulechangedid, String moduleaction) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO PROJECT_HISTORY ( ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" TYPE ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" NAME ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		sb.append(" STATUS ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECT_STATUS_ID ");
		sb.append(" , ");
		sb.append(" CIP_ACCTNO ");
		sb.append(" , ");
		sb.append(" START_DT ");
		sb.append(" , ");
		sb.append(" COMPLETION_DT ");
		sb.append(" , ");
		sb.append(" APPLIED_DT ");
		sb.append(" , ");
		sb.append(" EXPIRED_DT ");
		sb.append(" , ");
		sb.append(" LSO_USE_ID ");
		sb.append(" , ");
		sb.append(" DEPT_ID ");
		sb.append(" , ");
		sb.append(" MICROFILM ");
		sb.append(" , ");
		sb.append(" ACTIVE ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_DATE ");
		sb.append(" , ");
		sb.append(" UPDATED ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" , ");
		sb.append(" ACCOUNT_NUMBER ");
		sb.append(" , ");
		sb.append(" ENTITY ");
		sb.append(" , ");
		sb.append(" MODULE_CHANGED ");
		sb.append(" , ");
		sb.append(" MODULE_CHANGED_ID ");
		sb.append(" , ");
		sb.append(" MODULE_CHANGED_ACTION ");
		sb.append(" ) VALUES ( ");
		sb.append(id);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(type)).append("' ");
		sb.append(" , ");
		sb.append(lkupprojecttypeid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(projectnbr)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(name)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(status)).append("' ");
		sb.append(" , ");
		sb.append(lkupprojectstatusid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(cipacctno)).append("' ");
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(startdt));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(completiondt));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(applieddt));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(expireddt));
		sb.append(" , ");
		sb.append(lsouseid);
		sb.append(" , ");
		sb.append(deptid);
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(microfilm));
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(active));
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
		sb.append(" '").append(Operator.sqlEscape(createdip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(updatedip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(accountnumber)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(entity)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(modulechanged)).append("' ");
		sb.append(" , ");
		sb.append(modulechangedid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(moduleaction)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String history(String type, int typeid, int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT "); 
	
		sb.append("   P.NAME AS TITLE, "); 
		sb.append("   P.PROJECT_NBR AS SUBTITLE, ");
		sb.append("   P.STATUS as LKUP_PROJECT_STATUS_TEXT, "); 
		sb.append("   P.UPDATED, "); 
		sb.append("   P.LOG_DATE AS GROUP_UPDATED, ");
		sb.append("   P.* "); 
		sb.append(" FROM "); 
		sb.append("   PROJECT_HISTORY AS P ");
		sb.append(" WHERE "); 
		sb.append("   P.ID = ").append(typeid).append(" "); 
		sb.append("   AND "); 
		sb.append("   P.ACTIVE='Y' ");
		sb.append(" ORDER BY ");
		sb.append("   P.LOG_DATE DESC ");
		return sb.toString();
	}

	public static String getType(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT P.ID, P.NAME AS TITLE, P.PROJECT_NBR AS SUBTITLE, S.STATUS "); 
		sb.append(" FROM ").append(Table.PROJECTTABLE).append(" P ");
		sb.append(" JOIN ").append(Table.LKUPPROJECTSTATUSTABLE).append(" S ON P.LKUP_PROJECT_STATUS_ID = S.ID ");
		sb.append(" WHERE P.ID= ").append(id).append(" AND P.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getDetails(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT P.*, P.NAME AS TITLE, P.PROJECT_NBR AS SUBTITLE, ATT.STATUS as STATUS, CU.USERNAME AS CREATED, UP.USERNAME as UPDATED "); 
		sb.append(" FROM ").append(Table.PROJECTTABLE).append(" P ");
		sb.append(" JOIN ").append(Table.LKUPPROJECTSTATUSTABLE).append(" ATT ON P.LKUP_PROJECT_STATUS_ID=ATT.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on P.CREATED_BY=CU.ID ");
		sb.append("	LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on P.UPDATED_BY=UP.ID "); 
		sb.append(" WHERE P.ID= ").append(id).append(" AND P.ACTIVE='Y' ");
		return sb.toString();
	}

	public static String getCustomGroups(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT");
		sb.append(" G.* ");
		sb.append(" FROM ");
		sb.append(" PROJECT AS P ");
		sb.append(" JOIN PROJECT_TYPE AS T ON P.PROJECT_TYPE_ID = T.ID ");
		sb.append(" JOIN REF_PROJECT_TYPE_FIELD_GROUPS AS R ON R.PROJECT_ID = T.ID ");
		sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID ");
		sb.append(" WHERE ");
		sb.append(" G.ACTIVE = 'Y'  ");
		sb.append(" AND ");
		sb.append(" P.ID = ").append(id);
		sb.append(" ORDER BY G.ORDR ");
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
		sb.append(" ,");
		sb.append(" LFT.TYPE as FIELD_TYPE ");
		sb.append(" ,");
		sb.append(" LFIT.TYPE as FIELD_ITYPE ");
		sb.append(" FROM ");
		sb.append(" PROJECT AS P ");
		sb.append(" JOIN PROJECT_TYPE AS T ON P.PROJECT_TYPE_ID = T.ID ");
		sb.append(" JOIN REF_PROJECT_TYPE_FIELD_GROUPS AS R ON R.PROJECT_ID = T.ID ");
		sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID ");
		sb.append(" JOIN FIELD AS F ON F.FIELD_GROUPS_ID = G.ID ");
		sb.append(" LEFT OUTER JOIN PROJECT_FIELD_VALUE AS V ON F.ID = V.FIELD_ID AND V.ACTIVE = 'Y' AND V.PROJECT_ID = P.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDTYPETABLE).append(" LFT ON F.LKUP_FIELD_TYPE_ID = LFT.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDITYPETABLE).append(" LFIT ON F.LKUP_FIELD_ITYPE_ID = LFIT.ID ");
		
		sb.append(" WHERE ");
		sb.append(" F.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" G.ACTIVE = 'Y'  ");
		sb.append(" AND ");
		sb.append(" P.ID = ").append(id);
		sb.append(" ORDER BY G.ORDR, F.ORDR ");
		return sb.toString();
	}

	public static String getProject(String entity, int projectid, String reference) {
		if (projectid < 1 && !Operator.hasValue(reference)) { return ""; }
		String tableref = CsReflect.getTableRef(entity);
		String idref = CsReflect.getFieldIdRef(entity);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" P.*, R.").append(idref).append(" AS ENTITY_ID ");
		sb.append(" FROM ");
		sb.append(" PROJECT AS P ");
		sb.append(" JOIN REF_").append(tableref).append("_PROJECT AS R ON R.PROJECT_ID = P.ID AND R.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		if (projectid > 0) {
			sb.append(" P.ID = ").append(projectid);
		}
		else {
			sb.append(" LOWER(P.PROJECT_NBR) = LOWER('").append(Operator.sqlEscape(reference)).append("') ");
		}
		return sb.toString();
	}

	public static String getProject(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" P.*, ");
		sb.append(" S.STATUS, ");
		sb.append(" T.TYPE, ");
		sb.append(" U.USERNAME AS UPDATED ");
		sb.append(" FROM  ");
		sb.append(" PROJECT AS  P ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECT_STATUS AS S ON P.LKUP_PROJECT_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON P.UPDATED_BY = U.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECT_TYPE AS T ON P.LKUP_PROJECT_TYPE_ID = T.ID ");
		sb.append(" WHERE ");
		sb.append(" P.ID = ").append(id).append(" AND P.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getProject(String projectnumber){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT P.* "); 
		sb.append(" FROM  PROJECT AS P ");
		sb.append(" WHERE LOWER(P.PROJECT_NBR) = LOWER('").append(Operator.sqlEscape(projectnumber)).append("') ");
		return sb.toString();
	}

	public static String getSubs(int id,String active){
		StringBuilder sb = new StringBuilder();
		sb.append("select P.ID,P.PROJECT_NBR,P.NAME,P.DESCRIPTION,P.CREATED_DATE ,count(A.ID) as CHILDRENS  "); 
	/*	sb.append("  , stuff((select DISTINCT  ','+convert(varchar(100),A.ID)+' '+A.ACT_NBR "); 
		sb.append("  from  ").append(Table.ACTIVITYTABLE).append(" A  "); 
		sb.append("  where P.ID=A.PROJECT_ID AND A.ACTIVE='Y' for xml path('')),1,1,'') as CHILDRENS "); */
		sb.append("  from  ").append(Table.REFLSOPROJECTTABLE).append(" R "); 
		sb.append("  JOIN  ").append(Table.PROJECTTABLE).append(" P ON R.PROJECT_ID= P.ID ");
		sb.append("  LEFT OUTER JOIN  ").append(Table.ACTIVITYTABLE).append(" A ON P.ID=A.PROJECT_ID ");
		sb.append(" where LSO_ID= ").append(id).append(" AND P.ACTIVE='").append(Operator.sqlEscape(active)).append("' ");
		sb.append("  group by P.ID,P.PROJECT_NBR,P.NAME,P.DESCRIPTION,P.CREATED_DATE ");
		sb.append("  order by P.CREATED_DATE DESC ,P.NAME  ");

		return sb.toString();
	}
	
	
	public static String getChildrens(int id,String active){
		StringBuilder sb = new StringBuilder();
		sb.append("select A.*,ATT.DESCRIPTION as TITLE  "); 
		sb.append("  from  ").append(Table.REFLSOPROJECTTABLE).append(" R "); 
		sb.append("  JOIN  ").append(Table.PROJECTTABLE).append(" P ON R.PROJECT_ID= P.ID ");
		sb.append("  JOIN  ").append(Table.ACTIVITYTABLE).append(" A ON P.ID=A.PROJECT_ID ");
		sb.append("  JOIN  ").append(Table.ACTTYPETABLE).append(" ATT ON A.LKUP_ACT_TYPE_ID=ATT.ID ");
		sb.append(" where A.PROJECT_ID= ").append(id).append(" AND A.ACTIVE='").append(Operator.sqlEscape(active)).append("' ");
		sb.append("  order by A.CREATED_DATE DESC ");
		return sb.toString();
	}
	
	public static String getRefIdProject(RequestVO r, Token u) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select TOP 1 ID FROM ").append(Table.PROJECTTABLE).append(" ");
		sb.append(" WHERE ID >0  ");
		sb.append(" AND ");
		/*sb.append(" PATH = '").append(Operator.sqlEscape(obj[2].getValue())).append("' ");
		sb.append(" AND ");*/
		sb.append(" CREATED_BY = ").append(u.getId());
		sb.append(" order by CREATED_DATE DESC ");
		return sb.toString();
	}
	
	public static String insertRefProject(RequestVO r, Token u, int id) {
		return GeneralSQL.insertRefCommon(r, u, id);
	}


	public static String print(String type, int typeid) {
		StringBuilder sb = new StringBuilder();
		 sb.append(" WITH AD AS (     ");
		 sb.append(" 	 select P.ID, CONVERT(varchar(100), L.STR_NO)+' '+  LS.STR_NAME+' '+ LS.STR_TYPE+' '+ CASE WHEN PRE_DIR is null THEN '' ELSE PRE_DIR END+''+CASE WHEN L.UNIT is null then '' ELSE L.UNIT END as ADDRESS,APN  ");
		 sb.append(" 	from PROJECT P        ");
		 sb.append(" 	 join REF_LSO_PROJECT R on P.ID=R.PROJECT_ID      ");
		 sb.append(" 	 join LSO L on R.LSO_ID=L.ID      ");
		 sb.append(" 	  join LSO_STREET LS on L.LSO_STREET_ID=LS.ID     ");
		 sb.append(" 	 left outer join REF_LSO_APN LA on L.ID =LA.LSO_ID      ");
		 sb.append(" 	WHERE      P.ID = ").append(typeid).append("        ");
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
		 sb.append(" 				SELECT P.ID as ").append(type).append("_ID");
		 sb.append(" 				, ");
		 sb.append(" 				PROJECT_NBR as ").append(type).append("_PROJECT_NBR");
		 sb.append(" 				, ");
		 sb.append(" 				P.NAME as ").append(type).append("_NAME");
		 
		 sb.append(" 				, ");
		 sb.append(" 				P.DESCRIPTION as ").append(type).append("_DESCRIPTION");
		 sb.append(" 				, ");
		 sb.append(" 				P.CIP_ACCTNO as ").append(type).append("_CIP_ACCTNO");
		 sb.append(" 				, ");
		 sb.append(" 				P.ACCOUNT_NUMBER as ").append(type).append("_ACCOUNT_NUMBER");
		 
		 sb.append(" 				, ");
		 sb.append(" 				LAT.TYPE as ").append(type).append("_TYPE");
		 
		 sb.append(" 				, ");
		 sb.append(" 				LAS.STATUS as ").append(type).append("_STATUS");
		
		 sb.append(" 				, ");
		 sb.append(" 				P.START_DT as ").append(type).append("_START_DT");
		 sb.append(" 				, ");
		 sb.append(" 				P.COMPLETION_DT as ").append(type).append("_COMPLETION_DT");
		 sb.append(" 				, ");
		 sb.append(" 				P.APPLIED_DT as ").append(type).append("_APPLIED_DT");
		 sb.append(" 				, ");
		 sb.append(" 				P.EXPIRED_DT as ").append(type).append("_EXPIRED_DT");
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
		 sb.append(" 				P.CREATED_DATE as ").append(type).append("_CREATED_DATE");
		
		 sb.append(" 				, ");
		 sb.append(" 				P.UPDATED_DATE as ").append(type).append("_UPDATED_DATE");
		 
		 
		 sb.append(" 				FOR XML RAW('f'), TYPE ");
		 sb.append(" 				 ");
		 sb.append(" 			) ");
		 sb.append(" 	FROM PROJECT P   ");
		 sb.append(" 	LEFT OUTER JOIN AD on P.ID = AD.ID ");
		 sb.append(" 	join LKUP_PROJECT_TYPE LAT on P.LKUP_PROJECT_TYPE_ID=LAT.ID  ");
		 sb.append(" 	join LKUP_PROJECT_STATUS LAS on P.LKUP_PROJECT_STATUS_ID=LAS.ID  ");
		 sb.append(" LEFT OUTER JOIN USERS AS CU ON P.CREATED_BY = CU.ID ");
		 sb.append(" LEFT OUTER JOIN USERS UP ON P.UPDATED_BY = UP.ID  ");
		 sb.append(" 	where P.ID = ").append(typeid).append("  ");
		 sb.append(" 	 ");
		 sb.append(" ) p ");
		 sb.append(" CROSS APPLY p.[XML].nodes('f/@*') t(c) ");
		
		return sb.toString();
	}
	
	
	public static String printStickers(String type, int typeid) {
		StringBuilder sb = new StringBuilder();
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
		
		 
		 
		 sb.append(" 				FOR XML RAW('f'), TYPE ");
		 sb.append(" 				 ");
		 sb.append(" 			) ");
		 sb.append(" 	FROM ACTIVITY A   ");
		 
		 
		 sb.append(" 	join LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID  ");
		 
		 Timekeeper k = new Timekeeper();
		 
		 boolean kt = false;
		 if(k.MONTH()<9){
			 k.addYear(-1);
			 kt = true;
		 }
		 sb.append(" 	where A.PROJECT_ID = ").append(typeid).append(" ");
		 sb.append(" AND A.START_DATE > '09-30-").append(k.yyyy()).append("' ");
		
		 if(kt){
			 k.addYear(1);
		 }
		 
		 sb.append(" AND A.EXP_DATE <= '09-30-").append(k.yyyy()).append("' ");
		 sb.append(" 	 ");
		 sb.append(" ) p ");
		 sb.append(" CROSS APPLY p.[XML].nodes('f/@*') t(c) ");
		
		return sb.toString();
	}
	
	
	public static String getLkupProjectType(int projecttypeid){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * "); 
		sb.append(" FROM ").append(Table.LKUPPROJECTTYPETABLE).append(" WHERE ID = ").append(projecttypeid).append(" AND ACTIVE = 'Y' ");
		return sb.toString();
	}
	
	public static String insertRefAccount(int projectId, int user) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_PROJECT_PARKING ( ");
		sb.append(" PROJECT_ID ");
//		sb.append(" , ");
//		sb.append(" CREATED_BY ");
//		sb.append(" , ");
//		sb.append(" UPDATED_BY ");
		sb.append(" ) VALUES ( ");
		sb.append(projectId);
//		sb.append(" , ");
//		sb.append(user);
//		sb.append(" , ");
//		sb.append(user);
		sb.append(" ) ");
		
		return sb.toString();
	}


	public static String zone(String type, int typeid){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT     T.ID,    TYPE.DESCRIPTION AS FIELD,    T.DIVISION AS VALUE,    TYPE.ID AS TYPE_ID,    TYPE.REQUIRED");  
		sb.append(" FROM    LKUP_DIVISIONS_TYPE AS TYPE    ");
		sb.append(" LEFT OUTER JOIN (      LKUP_DIVISIONS AS T   "); 
		sb.append(" JOIN REF_LSO_DIVISIONS AS R ON T.ID = R.LKUP_DIVISIONS_ID ");
		sb.append(" join ref_lso_project AS  RP on RP.LSO_ID=R.LSO_ID");
		sb.append(" AND rp.project_id="+typeid+" AND R.ACTIVE = 'Y' AND T.ACTIVE = 'Y')");
		sb.append(" ON T.LKUP_DIVISIONS_TYPE_ID = TYPE.ID  WHERE    TYPE.ACTIVE = 'Y'");
		sb.append(" and TYPE.DESCRIPTION='ZONE'");
		
		return sb.toString();
	}
	
	public static String getParkingPermits(String ids,String type,String startdate, String enddate,boolean checkprinted){
		return getParkingPermits(ids, type, startdate,  enddate, checkprinted, 0);
	}
	
	public static String getParkingPermits(String ids,String type,String startdate, String enddate,boolean checkprinted, int batch_id){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT "); 
		sb.append(" 	A.ID, "); 
		sb.append(" 	A.ACT_NBR, "); 
		sb.append(" 	A.PROJECT_ID, "); 
		sb.append(" 	A.START_DATE, "); 
		sb.append(" 	CONVERT(VARCHAR(10),A.EXP_DATE,101) as EXP_DATE, "); 
		sb.append(" 	LAT.DESCRIPTION as TYPE, "); 
		sb.append(" 	SUM(SD.FEE_AMOUNT) as AMOUNT, "); 
		sb.append(" 	SUM(SD.FEE_PAID) as PAID, "); 
		sb.append(" 	SUM(SD.BALANCE_DUE) as BALANCE, ");
		sb.append(" 	CU.USERNAME AS CREATED, "); 
		sb.append(" 	UP.USERNAME as UPDATED, "); 
		sb.append(" 	CONVERT(VARCHAR(10), A.CREATED_DATE,101) as CREATED_DATE, "); 
		sb.append(" 	CONVERT(VARCHAR(10), A.UPDATED_DATE,101) as UPDATED_DATE ");
		
		sb.append(" FROM ");
		sb.append(" 	ACTIVITY AS A ");
		sb.append(" 	JOIN LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID  ");
		sb.append("   	JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID=LAS.ID   ");
		sb.append(" 	LEFT OUTER JOIN REF_ACT_STATEMENT RAS on A.ID=RAS.ACTIVITY_ID  ");
		sb.append(" 	LEFT OUTER JOIN STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");
		sb.append(" 	LEFT OUTER JOIN STATEMENT_DETAIL SD on  S.ID=SD.STATEMENT_ID  ");
		sb.append(" 	LEFT OUTER JOIN FEE_GROUP FG   on SD.GROUP_ID=FG.ID ");
	//	sb.append("   LEFT OUTER JOIN Q on A.ID = Q.ACTIVITY_ID ");

		sb.append(" 	LEFT OUTER JOIN USERS AS CU on A.CREATED_BY = CU.ID ");
		sb.append("		LEFT OUTER JOIN USERS AS UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" WHERE ");
		sb.append("		LAT.ISDOT='Y' ");
		sb.append("		and ");
		sb.append("		LAT.DOT_EXEMPTION='N' ");
		sb.append("		AND LAS.ISSUED='Y'	 ");  
		Logger.info("checkprinted checkprinted checkprinted checkprinted" + checkprinted);
		if(Operator.equalsIgnoreCase(type, "project")){
			sb.append(" 	AND  PROJECT_ID IN  (").append(ids).append(") ");
			if(Operator.hasValue(startdate) && Operator.hasValue(enddate)){
				sb.append(" 	AND ");
				sb.append("		A.START_DATE >= '").append(startdate).append("' ");
			}
		}
		else {
			sb.append(" 	AND ");
			sb.append("		A.ID in  (").append(ids).append(")");
		}

		if (batch_id > 0) {
			sb.append(" 	AND ");
			sb.append("		BATCH_PRINT_ID =").append(batch_id);
		}
		else if(checkprinted) {
			sb.append(" 	AND ");
			sb.append("		(A.PRINTED= 'Y' or A.PRINTED IS NULL or A.PRINTED= 'N') ");
		} else 	if(!checkprinted){
			sb.append(" 	AND ");
			sb.append("		(A.PRINTED= 'N' or A.PRINTED IS NULL) ");
		}

		sb.append(" GROUP BY ");
		sb.append("		A.ID, ");
		sb.append("		A.ACT_NBR, ");
		sb.append("		LAT.DESCRIPTION, ");
		sb.append("		A.PROJECT_ID, ");
		sb.append("		A.START_DATE, ");
		sb.append("		A.EXP_DATE, ");
		sb.append("		A.CREATED_DATE, ");
		sb.append("		A.UPDATED_DATE, ");
		sb.append("		A.CREATED_BY, ");
		sb.append("		A.UPDATED_BY, ");
		sb.append("		CU.USERNAME, ");
		sb.append("		UP.USERNAME  ");
		sb.append(" ORDER BY ");
		sb.append("		A.CREATED_DATE DESC  ");
		return sb.toString();

	}

	public static String getProjectId(String type, int typeid) {
		StringBuilder sb = new StringBuilder();
		if (type.equalsIgnoreCase("activity")) {
			sb.append(" SELECT ");
			sb.append("   A.PROJECT_ID ");
			sb.append(" FROM ");
			sb.append("   ACTIVITY AS A ");
			sb.append(" WHERE ");
			sb.append("   ID = ").append(typeid);
		}
		return sb.toString();
	}

	public static String getProject(String projectnbr, String name, int userid, Timekeeper now) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM PROJECT WHERE ");
		sb.append(" LOWER(PROJECT_NBR) = LOWER('").append(Operator.sqlEscape(projectnbr)).append("') ");
		sb.append(" AND ");
		sb.append(" LOWER(name) = LOWER('").append(Operator.sqlEscape(name)).append("') ");
		sb.append(" AND ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" AND ");
		sb.append(" UPDATED_DATE = ").append(now.sqlDatetime());
		return sb.toString();
	}
	
	
	public static String updateProjectNumber(int id,String number) {
		if (id < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE PROJECT SET PROJECT_NBR = '").append(Operator.sqlEscape(number)).append("' WHERE ID = ").append(id).append(" ");
		return sb.toString();
	}

	public static String add(int projecttypeid, String projectnbr, String name, String description, int statusid, String cip, String startdate, String completiondate, String applieddate, String expireddate, String accountnumber, int userid, String ip, String entity, Timekeeper now) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(" INSERT INTO PROJECT ( ");

		sb.append(" LKUP_PROJECT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" NAME ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECT_STATUS_ID ");
		sb.append(" , ");
		sb.append(" CIP_ACCTNO ");
		sb.append(" , ");
		sb.append(" START_DT ");
		sb.append(" , ");
		sb.append(" COMPLETION_DT ");
		sb.append(" , ");
		sb.append(" APPLIED_DT ");
		sb.append(" , ");
		sb.append(" EXPIRED_DT ");
		sb.append(" , ");
		sb.append(" ACCOUNT_NUMBER ");
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
		sb.append(" , ");
		sb.append(" ENTITY ");

		sb.append(" ) OUTPUT Inserted.ID  VALUES (  ");

		sb.append(projecttypeid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(projectnbr)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(name)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(statusid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(cip)).append("' ");
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(startdate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(completiondate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(applieddate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(expireddate));
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(accountnumber)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(now.sqlDatetime());
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(now.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(entity)).append("' ");

		sb.append(" ) ");

		return sb.toString();
	}

	public static String update(int projectid, String name, String description, int statusid, String cip, String startdate, String completiondate, String applieddate, String expireddate, String accountnumber, int userid, String ip, Timekeeper now) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE PROJECT SET ");
		sb.append(" NAME = '").append(Operator.sqlEscape(name)).append("' ");
		sb.append(" , ");
		sb.append(" DESCRIPTION = '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(" LKUP_PROJECT_STATUS_ID = ").append(statusid);
		sb.append(" , ");
		sb.append(" CIP_ACCTNO = '").append(Operator.sqlEscape(cip)).append("' ");
		sb.append(" , ");
		sb.append(" START_DT = ").append(CsTools.dateColumnValue(startdate));
		sb.append(" , ");
		sb.append(" COMPLETION_DT = ").append(CsTools.dateColumnValue(completiondate));
		sb.append(" , ");
		sb.append(" APPLIED_DT = ").append(CsTools.dateColumnValue(applieddate));
		sb.append(" , ");
		sb.append(" EXPIRED_DT = ").append(CsTools.dateColumnValue(expireddate));
		sb.append(" , ");
		sb.append(" ACCOUNT_NUMBER = '").append(Operator.sqlEscape(accountnumber)).append("' ");
		sb.append(" , ");
		sb.append(" CREATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" CREATED_DATE = ").append(now.sqlDatetime());
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = ").append(now.sqlDatetime());
		sb.append(" , ");
		sb.append(" CREATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(projectid);
		return sb.toString();
	}

	public static String addRef(String entity, int entityid, int projectid, int userid, String ip, Timekeeper now) {
		String tableref = CsReflect.getTableRef(entity);
		String idref = CsReflect.getFieldIdRef(entity);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_PROJECT ( ");
		sb.append(idref);
		sb.append(" , ");
		sb.append(" PROJECT_ID ");
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
		sb.append(entityid);
		sb.append(" , ");
		sb.append(projectid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(now.sqlDatetime());
		sb.append(" , ");
		sb.append(now.sqlDatetime());
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getProjectTypes(String type, int typeid) {
		Logger.info(type+"%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		if (Operator.equalsIgnoreCase(type, "project")) { return getProjectTypes(typeid); }
		DivisionsList l = DivisionsAgent.getDivisions("lso", typeid);
		String divids = l.divisionIds();
		Logger.info(divids+"%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH C AS ( ");
		sb.append("   SELECT ");
		sb.append("     T.ID ");
		sb.append("     , ");
		sb.append("     T.ID AS VALUE ");
		sb.append("     , ");
		sb.append("     T.TYPE AS TEXT ");
		sb.append("     , ");
		sb.append("     T.DESCRIPTION ");
		sb.append("     , ");
		sb.append("     COUNT(DISTINCT RP.ID) AS DIVISIONS ");
		sb.append("   FROM ");
		sb.append("     LKUP_PROJECT_TYPE AS T ");
		sb.append("     LEFT OUTER JOIN REF_PROJECT_DIVISIONS AS RP ON T.ID = RP.LKUP_PROJECT_TYPE_ID AND T.ACTIVE = 'Y' AND RP.ACTIVE = 'Y' ");
		sb.append("   GROUP BY ");
		sb.append("     T.ID ");
		sb.append("     , ");
		sb.append("     T.ID ");
		sb.append("     , ");
		sb.append("     T.TYPE  ");
		sb.append("     , ");
		sb.append("     T.DESCRIPTION ");
		sb.append(" ) ");
		sb.append(" , D AS ( ");
		sb.append("   SELECT DISTINCT ");
		sb.append("     T.ID ");
		sb.append("     , ");
		sb.append("     T.ID AS VALUE ");
		sb.append("     , ");
		sb.append("     T.TYPE AS TEXT ");
		sb.append("     , ");
		sb.append("     T.DESCRIPTION ");
		sb.append("   FROM ");
		sb.append("     REF_").append(tableref).append("_DIVISIONS AS RD ");
		sb.append("     JOIN REF_PROJECT_DIVISIONS AS RP ON RD.LKUP_DIVISIONS_ID = RP.LKUP_DIVISIONS_ID AND RD.ACTIVE = 'Y' AND RP.ACTIVE = 'Y' ");//AND RD.").append(idref).append(" = ").append(typeid);
		sb.append("     AND RD.LKUP_DIVISIONS_ID IN (").append(divids).append(")");
		sb.append("     JOIN LKUP_PROJECT_TYPE AS T ON RP.LKUP_PROJECT_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append("   UNION ");
		sb.append("   SELECT ");
		sb.append("     ID ");
		sb.append("     , ");
		sb.append("     VALUE ");
		sb.append("     , ");
		sb.append("     TEXT ");
		sb.append("     , ");
		sb.append("     DESCRIPTION ");
		sb.append("   FROM ");
		sb.append("     C ");
		sb.append("   WHERE ");
		sb.append("     DIVISIONS < 1 ");
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT * FROM D ");
		sb.append(" ORDER BY TEXT ");
		return sb.toString();
	}

	public static String getProjectTypes1(String type, int typeid) {
		if (Operator.equalsIgnoreCase(type, "project")) { return getProjectTypes(typeid); }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" T.ID ");
		sb.append(" , ");
		sb.append(" T.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" T.TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" T.DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_DIVISIONS AS RD ");
		sb.append(" JOIN REF_PROJECT_DIVISIONS AS RP ON RD.LKUP_DIVISIONS_ID = RP.LKUP_DIVISIONS_ID AND RD.ACTIVE = 'Y' AND RP.ACTIVE = 'Y' AND RD.").append(idref).append(" = ").append(typeid);
		sb.append(" JOIN LKUP_PROJECT_TYPE AS T ON RP.LKUP_PROJECT_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" ORDER BY T.TYPE ");
		return sb.toString();
	}

	public static String getProjectTypes(int projectid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" T.ID ");
		sb.append(" , ");
		sb.append(" T.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" T.TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" T.DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" LKUP_PROJECT_TYPE AS T ");
		sb.append(" JOIN PROJECT AS P ON T.ID = P.LKUP_PROJECT_TYPE_ID AND P.ID = ").append(projectid);
		sb.append(" ORDER BY T.TYPE ");
		return sb.toString();
	}

	public static String getProjectTypes() {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" T.ID ");
		sb.append(" , ");
		sb.append(" T.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" T.TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" T.DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" LKUP_PROJECT_TYPE AS T ");
		sb.append(" WHERE ");
		sb.append(" T.ACTIVE = 'Y' ");
		sb.append(" ORDER BY T.TYPE ");
		return sb.toString();
	}

	public static String getProjectStatuses() {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" S.ID ");
		sb.append(" , ");
		sb.append(" S.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" S.STATUS AS TEXT ");
		sb.append(" , ");
		sb.append(" S.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" S.EXPIRED ");
		sb.append(" FROM ");
		sb.append(" LKUP_PROJECT_STATUS AS S ");
		sb.append(" WHERE ");
		sb.append(" S.ACTIVE = 'Y' ");
		sb.append(" ORDER BY S.STATUS ");
		return sb.toString();
	}

	public static String updateExpiration(int projectid) {
		if (projectid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE PROJECT SET ");
		sb.append(" EXPIRED_DT = ( SELECT MAX(EXP_DATE) FROM ACTIVITY WHERE PROJECT_ID = ").append(projectid).append(" ) ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(projectid);
		return sb.toString();
	}

	public static String removeExpiration(int projectid) {
		if (projectid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE PROJECT SET ");
		sb.append(" EXPIRED_DT = null ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(projectid);
		return sb.toString();
	}

	public static String getProjectType(int projectid) {
		if (projectid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" T.* ");
		sb.append(" FROM ");
		sb.append(" PROJECT AS P ");
		sb.append(" JOIN LKUP_PROJECT_TYPE AS T ON P.LKUP_PROJECT_TYPE_ID = T.ID AND P.ID = ").append(projectid);
		return sb.toString();
	}


	public static String getRenewalPermits(String ids,String type,String startdate, String enddate, String selected){
		String sel[] = Operator.split(selected,"_");
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT COUNT(A.ID) COUNT, LAT.DESCRIPTION FROM ACTIVITY A ");
		sb.append(" JOIN PROJECT P ON A.PROJECT_ID = P.ID ");
		sb.append(" JOIN LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID ");
		sb.append(" JOIN LKUP_PROJECT_TYPE LPT ON P.LKUP_PROJECT_TYPE_ID = LPT.ID ");
		if(sel.length > 1){
			sb.append("JOIN REF_PROJECT_DIVISIONS RPD ON LPT.ID = RPD.LKUP_PROJECT_TYPE_ID AND RPD.LKUP_DIVISIONS_ID = ");
			sb.append(sel[1]);
		}
		sb.append(" WHERE ");
		if(Operator.equalsIgnoreCase(type, "project")){
			sb.append(" 	A.PROJECT_ID IN  (").append(ids).append(")");
			if(Operator.hasValue(startdate) && Operator.hasValue(enddate)){
				sb.append(" AND A.START_DATE >= '").append(startdate).append("' AND A.EXP_DATE <= '").append(enddate).append("'   ");
			}
		}else {
			sb.append(" 	A.ID IN  (").append(ids).append(")");
		}
		sb.append(" AND LAT.ID IN (").append(sel[0]).append(")");
		
		
		
		sb.append(" GROUP BY LAT.DESCRIPTION ");
		return sb.toString();
		
	}

	public static String getPaymentDetails(int id, String trans) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * from PAYMENT_ONLINE_PREVIEW P ");
		sb.append(" JOIN REF_PAYMENT_ONLINE_PREVIEW_RESULT RES on P.ID = RES.PAYMENT_ONLINE_PREVIEW_ID ");
		sb.append(" WHERE ID =  ").append(id);
		sb.append(" AND TRANSACTION_ID = ").append(trans);
		return sb.toString();
	}

	public static String deactivateRef(int projectid, int userid, String ip) {
		if (projectid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_LSO_PROJECT SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(" PROJECT_ID = ").append(projectid);
		sb.append(" AND ");
		sb.append(" ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String addRef(int projectid, int newlsoid, int userid, String ip) {
		if (projectid < 1) { return ""; }
		if (newlsoid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_LSO_PROJECT ( ");
		sb.append(" PROJECT_ID, ");
		sb.append(" LSO_ID, ");
		sb.append(" CREATED_BY, ");
		sb.append(" UPDATED_BY, ");
		sb.append(" CREATED_IP, ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(projectid);
		sb.append(" , ");
		sb.append(newlsoid);
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

	public static String search(int curractid, String projnum) {
		if (!Operator.hasValue(projnum) && curractid < 1) {
			return "";
		}
		TypeInfo t = new TypeInfo();
		if (Operator.hasValue(projnum)) {
			t = EntityAgent.getEntity("project", projnum);
		}
		else {
			t = EntityAgent.getEntity("activity", curractid);
		}
		String entity = t.getEntity();
		int entityid = t.getEntityid();
		Logger.highlight(entity+":"+entityid);
		if (!Operator.hasValue(projnum) && !Operator.equalsIgnoreCase(entity, "lso")) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" P.ID, ");
		sb.append(" P.ID AS VALUE, ");
		sb.append(" P.PROJECT_NBR, ");
		sb.append(" P.PROJECT_NBR AS TEXT, ");
		sb.append(" P.NAME, ");
		sb.append(" P.DESCRIPTION, ");
		sb.append(" T.TYPE ");
		if (Operator.equalsIgnoreCase(entity, "lso")) {
			sb.append("     , ");
			sb.append("     CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(L.STR_MOD, '') + ' ' + COALESCE(S.PRE_DIR, '') + ' ' + S.STR_NAME + ' ' + S.STR_TYPE + ' ' + COALESCE(L.UNIT, '') AS ADDRESS ");
		}
		sb.append(" FROM ");
		sb.append(" PROJECT AS P ");
		sb.append(" JOIN LKUP_PROJECT_TYPE AS T ON P.LKUP_PROJECT_TYPE_ID = T.ID ");
		if (Operator.equalsIgnoreCase(entity, "lso")) {
			sb.append(" JOIN REF_LSO_PROJECT AS R ON P.ID = R.PROJECT_ID AND R.ACTIVE = 'Y' ");
			if (!Operator.hasValue(projnum)) {
				sb.append(" AND P.ACTIVE = 'Y' ");
			}
			sb.append(" JOIN LSO AS L ON R.LSO_ID = L.ID ");
			sb.append(" JOIN LSO_STREET AS S ON L.LSO_STREET_ID = S.ID ");
		}
		sb.append(" WHERE ");
		if (Operator.hasValue(projnum)) {
			sb.append(" LOWER(P.PROJECT_NBR) = LOWER('").append(Operator.sqlEscape(projnum)).append("') ");
		}
		else {
			sb.append(" L.ID = ").append(entityid);
			sb.append(" ORDER BY P.DESCRIPTION ");
		}
		return sb.toString();
	}

















}




















