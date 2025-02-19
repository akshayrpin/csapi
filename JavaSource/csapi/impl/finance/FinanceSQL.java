package csapi.impl.finance;

import org.json.JSONObject;

import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.activity.ActivitySQL;
import csapi.utils.CsReflect;
import csshared.vo.RequestVO;
import csshared.vo.finance.FeeVO;
import csshared.vo.finance.PaymentVO;

public class FinanceSQL {

	public static String summary(String type, int typeid, int id) {
		return summary(type, typeid, id,false);
	}
	
	public static String summary(String type, int typeid, int id,boolean empty) {
		return getTable(type, typeid);
	}

	public static String getTable(String type, int typeid) {
		if (!Operator.hasValue(type) || typeid < 1) { return ""; }
		String calc = calc(type, typeid);
		StringBuilder sb = new StringBuilder();
		if (Operator.hasValue(calc)) {
			sb.append(calc);
			sb.append(", Q AS ( ");
			sb.append(" SELECT ");
			sb.append(" * ");
			sb.append(" , ");
			sb.append(" PERMIT_FEE AS ACTIVITY_FEE ");
			sb.append(" FROM ");
			sb.append(" CALC ");
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" * ");
			sb.append(" , ");
			sb.append(" PERMIT_FEE AS ACTIVITY_FEE ");
			sb.append(" FROM ");
			sb.append(" TOTAL ");
			sb.append(" ) ");
			sb.append(" , CT AS ( SELECT COUNT(DISTINCT ID) AS CONTENT_COUNT FROM CONTENT WHERE ACTIVE = 'Y' AND LOWER(TYPE) = 'finance') ");
			sb.append(" SELECT ");
			sb.append(" * ");
			sb.append(" , ");
			sb.append(" CASE ");
			sb.append("   WHEN CT.CONTENT_COUNT IS NULL THEN '' ");
			sb.append("   WHEN CT.CONTENT_COUNT > 0 THEN 'finance' ");
			sb.append(" ELSE '' END AS CONTENT_TYPE ");
			sb.append(" FROM ");
			sb.append(" Q ");
			sb.append(" LEFT OUTER JOIN CT ON 1 = 1 ");
			sb.append(" ORDER BY ORDR ");
		}
		return sb.toString();
	}

	public static String summary2(String type, int typeid, int id,boolean empty) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append("         SELECT ");
		sb.append("         FG.ID AS GROUP_ID, ");
		sb.append("         FG.GROUP_NAME, ");
		sb.append("         COALESCE(RFG.REVIEW_FEE, 'N') AS REVIEW_FEE, ");
		sb.append("         COALESCE(D.FEE_AMOUNT, 0) AS FEE_AMOUNT, ");
		sb.append("         COALESCE(D.FEE_PAID, 0) AS FEE_PAID, ");
		sb.append("         COALESCE(D.BALANCE_DUE, 0) AS BALANCE_DUE ");
		sb.append(" FROM ");
		sb.append(" 	REF_").append(tableref).append("_STATEMENT AS REF ");
		sb.append(" 	JOIN STATEMENT AS S ON REF.STATEMENT_ID = S.ID AND REF.").append(idref).append(" = ").append(typeid).append(" AND REF.ACTIVE = 'Y' ");
		sb.append(" 	JOIN STATEMENT_DETAIL AS D ON S.ID = D.STATEMENT_ID AND D.ACTIVE = 'Y' ");
		sb.append(" 	JOIN FEE_GROUP FG ON D.GROUP_ID = FG.ID ");
		sb.append(" 	LEFT OUTER JOIN REF_FEE_GROUP RFG ON FG.ID = RFG.FEE_GROUP_ID AND D.FEE_ID = RFG.FEE_ID  ");
		sb.append(" ), ");
		sb.append(" C AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	GROUP_ID, ");
		sb.append(" 	GROUP_NAME, ");
		sb.append(" 	REVIEW_FEE, ");
		sb.append(" 	SUM(FEE_AMOUNT) AS FEE_AMOUNT, ");
		sb.append(" 	SUM(FEE_PAID) AS FEE_PAID, ");
		sb.append(" 	SUM(BALANCE_DUE) AS BALANCE_DUE ");
		sb.append(" FROM ");
		sb.append(" 	Q ");
		sb.append(" GROUP BY ");
		sb.append(" 	GROUP_ID, ");
		sb.append(" 	GROUP_NAME, ");
		sb.append(" 	REVIEW_FEE ");
		sb.append(" ), ");
		sb.append(" RA AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		Q.GROUP_ID, ");
		sb.append(" 		COALESCE(A.FEE_AMOUNT, 0) AS PERMIT_AMOUNT, ");
		sb.append(" 		COALESCE(R.FEE_AMOUNT, 0) AS REVIEW_AMOUNT ");
		sb.append(" 	FROM Q ");
		sb.append(" 		LEFT OUTER JOIN C AS A ON Q.GROUP_ID = A.GROUP_ID AND A.REVIEW_FEE = 'N' ");
		sb.append(" 		LEFT OUTER JOIN C AS R ON Q.GROUP_ID = R.GROUP_ID AND R.REVIEW_FEE = 'Y' ");
		sb.append(" ), ");
		sb.append(" M AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		C.GROUP_ID, ");
		sb.append(" 		C.GROUP_NAME, ");
		sb.append(" 		RA.PERMIT_AMOUNT AS ACTIVITY_FEE, ");
		sb.append(" 		RA.REVIEW_AMOUNT AS REVIEW_FEE, ");
		sb.append(" 		SUM(C.FEE_PAID) AS PAID, ");
		sb.append(" 		SUM(C.BALANCE_DUE) AS BALANCE ");
		sb.append("  ");
		sb.append(" 	FROM ");
		sb.append(" 	C ");
		sb.append(" 	LEFT OUTER JOIN RA ON C.GROUP_ID = RA.GROUP_ID ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		C.GROUP_ID, ");
		sb.append(" 		C.GROUP_NAME, ");
		sb.append(" 		RA.PERMIT_AMOUNT, ");
		sb.append(" 		RA.REVIEW_AMOUNT ");
		sb.append(" ), ");
		sb.append(" R AS ( ");
		sb.append(" 	SELECT *, 0 AS ORDR FROM M ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		0, ");
		sb.append(" 		'TOTAL' AS GROUP_NAME, ");
		sb.append(" 		SUM(M.ACTIVITY_FEE) AS ACTIVITY_FEE, ");
		sb.append(" 		SUM(M.REVIEW_FEE) AS REVIEW_FEE, ");
		sb.append(" 		SUM(M.PAID) AS PAID, ");
		sb.append(" 		SUM(M.BALANCE) AS BALANCE, ");
		sb.append(" 		1 AS ORDR ");
		sb.append(" 	FROM ");
		sb.append(" 		M ");
		sb.append(" ) ");
		sb.append(" SELECT * FROM R ORDER BY ORDR, GROUP_NAME ");
		return sb.toString();
	}

	public static String calc(String type, int typeid) {
		return calc(type, Operator.toString(typeid),false);
	}
	
	public static String calc(String type, int typeid,boolean cartnegativebalancehide) {
		return calc(type, Operator.toString(typeid),cartnegativebalancehide);
	}
	
	public static String calc(String type, String typeids) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH ");
		sb.append(calcSubqueries(type, typeids, false));
		return sb.toString();
	}

	public static String calc(String type, String typeids, boolean cartnegativebalancehide) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH ");
		sb.append(calcSubqueries(type, typeids, cartnegativebalancehide));
		return sb.toString();
	}

	public static String calcSubqueries(String type, String typeids) {
		return calcSubqueries(type, typeids, false);
	}
	public static String calcSubqueries(String type, String typeids, boolean cartnegativebalancehide) {
		if (!Operator.hasValue(type) || !Operator.hasValue(typeids)) { return ""; }
		String maintableref = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String referenceref = CsReflect.getReferenceRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" MAIN AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		MAINWITH.*, ");
		sb.append(" 		MAINWITH.LKUP_").append(tableref).append("_TYPE_ID AS MAIN_TYPE, ");
		sb.append(" 		'").append(type).append("' AS REF_TYPE, ");
		sb.append(" 		MAINWITH.ID AS REF_ID, ");
		sb.append(" 		MAINWITH.").append(referenceref).append(" AS REF ");
		sb.append(" 	FROM ");
		sb.append(" 		").append(maintableref).append(" AS MAINWITH ");
		sb.append(" 	WHERE ");
		sb.append(" 		MAINWITH.ID IN (").append(typeids).append(") ");
		sb.append(" 		AND ");
		sb.append(" 		MAINWITH.ACTIVE = 'Y' ");
		sb.append(" ) ");
		sb.append(" , FLOCK AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		MAIN.ID AS REF_ID, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN (COUNT(S.ID) > 0) THEN 'Y' ");
		sb.append(" 		ELSE 'N' END AS FINANCE_LOCK ");
		sb.append(" 	FROM ");
		sb.append(" 		STATEMENT AS S ");
		sb.append(" 		JOIN REF_").append(tableref).append("_STATEMENT AS REF ON S.ID = REF.STATEMENT_ID AND S.ACTIVE = 'Y' AND S.FINANCE_LOCK = 'Y' ");
		sb.append(" 		JOIN MAIN ON REF.").append(idref).append(" = MAIN.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		MAIN.ID ");
		sb.append(" ) ");
		sb.append(" , ALLGROUPS AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		MAIN.REF_TYPE, ");
		sb.append(" 		MAIN.REF_ID, ");
		sb.append(" 		MAIN.REF, ");
		sb.append(" 		FG.ID AS GROUP_ID, ");
		sb.append(" 		FG.GROUP_NAME, ");
		sb.append(" 		RFG.ORDR ");
		sb.append(" 	FROM ");
		sb.append(" 		MAIN ");
		sb.append(" 		JOIN REF_").append(tableref).append("_FEE_GROUP AS RFG ON MAIN.MAIN_TYPE = RFG.LKUP_").append(tableref).append("_TYPE_ID AND RFG.ACTIVE = 'Y' ");
		sb.append(" 		JOIN FEE_GROUP AS FG ON RFG.FEE_GROUP_ID = FG.ID AND FG.ACTIVE = 'Y' ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		MAIN.REF_TYPE, ");
		sb.append(" 		MAIN.REF_ID, ");
		sb.append(" 		MAIN.REF, ");
		sb.append(" 		FG.ID AS GROUP_ID, ");
		sb.append(" 		FG.GROUP_NAME, ");
		sb.append(" 		100000 AS ORDR ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_").append(tableref).append("_STATEMENT AS REF ");
		sb.append(" 		JOIN MAIN ON MAIN.ID = REF.").append(idref).append(" ");
		sb.append(" 		JOIN STATEMENT AS S ON REF.STATEMENT_ID = S.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" 		JOIN STATEMENT_DETAIL AS D ON S.ID = D.STATEMENT_ID AND D.ACTIVE = 'Y' ");
		if(cartnegativebalancehide){
			sb.append(" 		AND D.BALANCE_DUE>0 ");
		}
		sb.append(" 		JOIN FEE_GROUP FG ON D.GROUP_ID = FG.ID ");
		sb.append(" ) ");
		sb.append(" , GROUPS AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		REF_TYPE, ");
		sb.append(" 		REF_ID, ");
		sb.append(" 		REF, ");
		sb.append(" 		GROUP_ID, ");
		sb.append(" 		GROUP_NAME, ");
		sb.append(" 		MIN(ORDR) AS ORDR ");
		sb.append(" 	FROM ");
		sb.append(" 		ALLGROUPS ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		REF_TYPE, ");
		sb.append(" 		REF_ID, ");
		sb.append(" 		REF, ");
		sb.append(" 		GROUP_ID, ");
		sb.append(" 		GROUP_NAME ");
		sb.append(" ) ");
		sb.append(" , ALLFEE AS ( ");
		sb.append("  ");
		sb.append(" 	SELECT ");
		sb.append(" 		GROUPS.REF_TYPE, ");
		sb.append(" 		GROUPS.REF_ID, ");
		sb.append(" 		GROUPS.REF, ");
		sb.append(" 		GROUPS.GROUP_ID, ");
		sb.append(" 		GROUPS.GROUP_NAME, ");
		sb.append(" 		GROUPS.ORDR, ");
		sb.append(" 		COALESCE(RFG.REVIEW_FEE, 'N') AS REVIEW_FEE, ");
		sb.append(" 		COALESCE(D.FEE_AMOUNT, 0) AS FEE_AMOUNT, ");
		sb.append(" 		COALESCE(D.FEE_PAID, 0) AS FEE_PAID, ");
		sb.append(" 		COALESCE(D.BALANCE_DUE, 0) AS BALANCE_DUE ");
		sb.append(" 	FROM ");
		sb.append(" 		GROUPS ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_").append(tableref).append("_STATEMENT AS REF ");
		sb.append(" 			JOIN STATEMENT AS S ON REF.STATEMENT_ID = S.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" 			JOIN STATEMENT_DETAIL AS D ON S.ID = D.STATEMENT_ID AND D.ACTIVE = 'Y' ");
		if(cartnegativebalancehide){
			sb.append(" 		AND D.BALANCE_DUE>0 ");
		}
		sb.append(" 			LEFT OUTER JOIN REF_FEE_GROUP RFG ON D.GROUP_ID = RFG.FEE_GROUP_ID AND D.FEE_ID = RFG.FEE_ID ");
		sb.append(" 		) ON D.GROUP_ID = GROUPS.GROUP_ID AND REF.").append(idref).append(" = GROUPS.REF_ID ");
		sb.append("  ");
		sb.append(" ) ");
		sb.append(" , CALCPERMIT AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		ALLFEE.REF_ID, ");
		sb.append(" 		ALLFEE.GROUP_ID, ");
		sb.append(" 		SUM(ALLFEE.FEE_AMOUNT) AS PERMIT_FEE, ");
		sb.append(" 		SUM(ALLFEE.FEE_PAID) AS FEE_PAID, ");
		sb.append(" 		SUM(ALLFEE.BALANCE_DUE) AS BALANCE_DUE ");
		sb.append(" 	FROM ");
		sb.append(" 		ALLFEE ");
		sb.append(" 	WHERE ");
		sb.append(" 		ALLFEE.REVIEW_FEE = 'N' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		ALLFEE.REF_ID, ");
		sb.append(" 		ALLFEE.GROUP_ID ");
		sb.append(" ) ");
		sb.append(" , CALCREVIEW AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		ALLFEE.REF_ID, ");
		sb.append(" 		ALLFEE.GROUP_ID, ");
		sb.append(" 		SUM(ALLFEE.FEE_AMOUNT) AS REVIEW_FEE, ");
		sb.append(" 		SUM(ALLFEE.FEE_PAID) AS FEE_PAID, ");
		sb.append(" 		SUM(ALLFEE.BALANCE_DUE) AS BALANCE_DUE ");
		sb.append(" 	FROM ");
		sb.append(" 		ALLFEE ");
		sb.append(" 	WHERE ");
		sb.append(" 		ALLFEE.REVIEW_FEE = 'Y' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		ALLFEE.REF_ID, ");
		sb.append(" 		ALLFEE.GROUP_ID ");
		sb.append(" ) ");
		sb.append(" , GFEES AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		GROUPS.REF_TYPE, ");
		sb.append(" 		GROUPS.REF_ID, ");
		sb.append(" 		GROUPS.REF, ");
		sb.append(" 		GROUPS.GROUP_ID, ");
		sb.append(" 		GROUPS.GROUP_NAME, ");
		sb.append(" 		GROUPS.ORDR, ");
		sb.append(" 		COALESCE(CALCPERMIT.PERMIT_FEE, 0) AS PERMIT_FEE, ");
		sb.append(" 		COALESCE(CALCPERMIT.FEE_PAID, 0) AS PERMIT_PAID, ");
		sb.append(" 		COALESCE(CALCPERMIT.BALANCE_DUE, 0) AS PERMIT_BALANCE, ");
		sb.append(" 		COALESCE(CALCREVIEW.REVIEW_FEE, 0) AS REVIEW_FEE, ");
		sb.append(" 		COALESCE(CALCREVIEW.FEE_PAID, 0) AS REVIEW_PAID, ");
		sb.append(" 		COALESCE(CALCREVIEW.BALANCE_DUE, 0) AS REVIEW_BALANCE ");
		sb.append(" 	FROM ");
		sb.append(" 		GROUPS ");
		sb.append(" 		LEFT OUTER JOIN CALCPERMIT ON GROUPS.GROUP_ID = CALCPERMIT.GROUP_ID AND CALCPERMIT.REF_ID = GROUPS.REF_ID ");
		sb.append(" 		LEFT OUTER JOIN CALCREVIEW ON GROUPS.GROUP_ID = CALCREVIEW.GROUP_ID AND CALCREVIEW.REF_ID = GROUPS.REF_ID ");
		sb.append(" ) ");
		sb.append(" , CALC AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		MAIN.REF_TYPE, ");
		sb.append(" 		MAIN.REF_ID, ");
		sb.append(" 		MAIN.REF, ");
		sb.append(" 		GFEES.GROUP_ID, ");
		sb.append(" 		GFEES.GROUP_NAME, ");
		sb.append(" 		COALESCE(FLOCK.FINANCE_LOCK, 'N') AS FINANCE_LOCK, ");
		sb.append(" 		GFEES.ORDR, ");
		sb.append(" 		COALESCE(GFEES.PERMIT_FEE, 0) AS PERMIT_FEE, ");
		sb.append(" 		COALESCE(GFEES.REVIEW_FEE, 0) AS REVIEW_FEE, ");
		sb.append(" 		COALESCE(GFEES.PERMIT_FEE + GFEES.REVIEW_FEE, 0) AS FEE_AMOUNT, ");
		sb.append(" 		COALESCE(GFEES.PERMIT_PAID + GFEES.REVIEW_PAID, 0) AS FEE_PAID, ");
		sb.append(" 		COALESCE(GFEES.PERMIT_BALANCE + GFEES.REVIEW_BALANCE, 0) AS BALANCE_DUE ");
		sb.append(" 	FROM ");
		sb.append(" 		MAIN ");
		sb.append(" 		LEFT OUTER JOIN GFEES ON MAIN.REF_TYPE = GFEES.REF_TYPE AND MAIN.REF_ID = GFEES.REF_ID ");
		sb.append(" 		LEFT OUTER JOIN FLOCK ON MAIN.REF_ID = FLOCK.REF_ID ");
		sb.append(" ) ");
		sb.append(" , TOTAL AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		CALC.REF_TYPE, ");
		sb.append(" 		CALC.REF_ID, ");
		sb.append(" 		CALC.REF, ");
		sb.append(" 		0 AS GROUP_ID, ");
		sb.append(" 		'TOTAL' AS GROUP_NAME, ");
		sb.append(" 		CALC.FINANCE_LOCK, ");
		sb.append(" 		1000000000 AS ORDR, ");
		sb.append(" 		SUM(CALC.PERMIT_FEE) AS PERMIT_FEE, ");
		sb.append(" 		SUM(CALC.REVIEW_FEE) AS REVIEW_FEE, ");
		sb.append(" 		SUM(CALC.FEE_AMOUNT) AS FEE_AMOUNT, ");
		sb.append(" 		SUM(CALC.FEE_PAID) AS FEE_PAID, ");
		sb.append(" 		SUM(CALC.BALANCE_DUE) AS BALANCE_DUE ");
		sb.append(" 	FROM ");
		sb.append(" 		CALC ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		CALC.REF_TYPE, ");
		sb.append(" 		CALC.REF_ID, ");
		sb.append(" 		CALC.REF, ");
		sb.append(" 		CALC.FINANCE_LOCK ");
		sb.append(" ) ");
		return sb.toString();
	}

	// THIS ONLY DOES ONE AT A TIME. CREATED NEW ON TO DO MULTIPLE
	public static String calc1(String type, int typeid) {
		if (!Operator.hasValue(type) || typeid < 1) { return ""; }
		String maintableref = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String referenceref = CsReflect.getReferenceRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH ");
		sb.append(" FLOCK AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		REF.").append(idref).append(" AS REF_ID, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN (COUNT(S.ID) > 0) THEN 'Y' ");
		sb.append(" 		ELSE 'N' END AS FINANCE_LOCK ");
		sb.append(" 	FROM ");
		sb.append(" 		STATEMENT AS S ");
		sb.append(" 		JOIN REF_").append(tableref).append("_STATEMENT AS REF ON S.ID = REF.STATEMENT_ID AND REF.").append(idref).append(" = ").append(typeid).append(" AND REF.ACTIVE = 'Y' AND S.ACTIVE = 'Y' AND S.FINANCE_LOCK = 'Y' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		REF.").append(idref);
		sb.append(" ) ");
		sb.append(", ALLGROUPS AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		'").append(Operator.sqlEscape(type)).append("' AS REF_TYPE, ");
		sb.append(" 		A.ID AS REF_ID, ");
		if (Operator.hasValue(referenceref)) {
			sb.append(" A.").append(referenceref).append(" AS REF, ");
		}
		else {
			sb.append(" '' AS REF, ");
		}
		sb.append(" 		FG.ID AS GROUP_ID, ");
		sb.append(" 		FG.GROUP_NAME, ");
		sb.append(" 		RFG.ORDR ");
		sb.append(" 	FROM ");
		sb.append(" 		").append(maintableref).append(" AS A ");
		sb.append(" 		JOIN LKUP_").append(tableref).append("_TYPE AS T ON A.LKUP_").append(tableref).append("_TYPE_ID = T.ID AND A.ID = ").append(typeid);
		sb.append(" 		JOIN REF_").append(tableref).append("_FEE_GROUP AS RFG ON T.ID = RFG.LKUP_").append(tableref).append("_TYPE_ID AND RFG.ACTIVE = 'Y' ");
		sb.append(" 		JOIN FEE_GROUP AS FG ON RFG.FEE_GROUP_ID = FG.ID AND FG.ACTIVE = 'Y' ");

		sb.append(" 	UNION ");

		sb.append(" 	SELECT ");
		sb.append(" 		'").append(Operator.sqlEscape(type)).append("' AS REF_TYPE, ");
		sb.append(" 		A.ID AS REF_ID, ");
		if (Operator.hasValue(referenceref)) {
			sb.append(" A.").append(referenceref).append(" AS REF, ");
		}
		else {
			sb.append(" '' AS REF, ");
		}
		sb.append(" 		FG.ID AS GROUP_ID, ");
		sb.append(" 		FG.GROUP_NAME, ");
		sb.append(" 		100000 AS ORDR ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_").append(tableref).append("_STATEMENT AS REF ");
		sb.append(" 		JOIN ").append(maintableref).append(" AS A ON A.ID = REF.").append(idref).append(" AND A.ID = ").append(typeid);
		sb.append(" 		JOIN STATEMENT AS S ON REF.STATEMENT_ID = S.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" 		JOIN STATEMENT_DETAIL AS D ON S.ID = D.STATEMENT_ID AND D.ACTIVE = 'Y' ");
		sb.append(" 		JOIN FEE_GROUP FG ON D.GROUP_ID = FG.ID ");
		sb.append(" ) ");

		sb.append(" , GROUPS AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		REF_TYPE, ");
		sb.append(" 		REF_ID, ");
		sb.append(" 		REF, ");
		sb.append(" 		GROUP_ID, ");
		sb.append(" 		GROUP_NAME, ");
		sb.append(" 		MIN(ORDR) AS ORDR ");
		sb.append(" 	FROM ");
		sb.append(" 		ALLGROUPS ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		REF_TYPE, ");
		sb.append(" 		REF_ID, ");
		sb.append(" 		REF, ");
		sb.append(" 		GROUP_ID, ");
		sb.append(" 		GROUP_NAME ");
		sb.append(" ) ");

		sb.append(" , ALLFEE AS ( ");
		sb.append("  ");
		sb.append(" 	SELECT ");
		sb.append(" 		GROUPS.REF_TYPE, ");
		sb.append(" 		GROUPS.REF_ID, ");
		sb.append(" 		GROUPS.REF, ");
		sb.append(" 		GROUPS.GROUP_ID, ");
		sb.append(" 		GROUPS.GROUP_NAME, ");
		sb.append(" 		GROUPS.ORDR, ");
		sb.append(" 		COALESCE(RFG.REVIEW_FEE, 'N') AS REVIEW_FEE, ");
		sb.append(" 		COALESCE(D.FEE_AMOUNT, 0) AS FEE_AMOUNT, ");
		sb.append(" 		COALESCE(D.FEE_PAID, 0) AS FEE_PAID, ");
		sb.append(" 		COALESCE(D.BALANCE_DUE, 0) AS BALANCE_DUE ");
		sb.append(" 	FROM ");
		sb.append(" 		GROUPS ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_").append(tableref).append("_STATEMENT AS REF ");
		sb.append(" 			JOIN STATEMENT AS S ON REF.STATEMENT_ID = S.ID AND REF.").append(idref).append(" = ").append(typeid).append(" AND REF.ACTIVE = 'Y' ");
		sb.append(" 			JOIN STATEMENT_DETAIL AS D ON S.ID = D.STATEMENT_ID AND D.ACTIVE = 'Y' ");
		sb.append(" 			LEFT OUTER JOIN REF_FEE_GROUP RFG ON D.GROUP_ID = RFG.FEE_GROUP_ID AND D.FEE_ID = RFG.FEE_ID ");
		sb.append(" 		) ON D.GROUP_ID = GROUPS.GROUP_ID ");
		sb.append("  ");
		sb.append(" ) ");
		sb.append(" , PERMIT AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		ALLFEE.GROUP_ID, ");
		sb.append(" 		SUM(ALLFEE.FEE_AMOUNT) AS PERMIT_FEE, ");
		sb.append(" 		SUM(ALLFEE.FEE_PAID) AS FEE_PAID, ");
		sb.append(" 		SUM(ALLFEE.BALANCE_DUE) AS BALANCE_DUE ");
		sb.append(" 	FROM ");
		sb.append(" 		ALLFEE ");
		sb.append(" 	WHERE ");
		sb.append(" 		ALLFEE.REVIEW_FEE = 'N' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		ALLFEE.GROUP_ID ");
		sb.append(" ) ");
		sb.append(" , REVIEW AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		ALLFEE.GROUP_ID, ");
		sb.append(" 		SUM(ALLFEE.FEE_AMOUNT) AS REVIEW_FEE, ");
		sb.append(" 		SUM(ALLFEE.FEE_PAID) AS FEE_PAID, ");
		sb.append(" 		SUM(ALLFEE.BALANCE_DUE) AS BALANCE_DUE ");
		sb.append(" 	FROM ");
		sb.append(" 		ALLFEE ");
		sb.append(" 	WHERE ");
		sb.append(" 		ALLFEE.REVIEW_FEE = 'Y' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		ALLFEE.GROUP_ID ");
		sb.append(" ) ");
		sb.append(" , GFEES AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		GROUPS.REF_TYPE, ");
		sb.append(" 		GROUPS.REF_ID, ");
		sb.append(" 		GROUPS.REF, ");
		sb.append(" 		GROUPS.GROUP_ID, ");
		sb.append(" 		GROUPS.GROUP_NAME, ");
		sb.append(" 		GROUPS.ORDR, ");
		sb.append(" 		COALESCE(PERMIT.PERMIT_FEE, 0) AS PERMIT_FEE, ");
		sb.append(" 		COALESCE(PERMIT.FEE_PAID, 0) AS PERMIT_PAID, ");
		sb.append(" 		COALESCE(PERMIT.BALANCE_DUE, 0) AS PERMIT_BALANCE, ");
		sb.append(" 		COALESCE(REVIEW.REVIEW_FEE, 0) AS REVIEW_FEE, ");
		sb.append(" 		COALESCE(REVIEW.FEE_PAID, 0) AS REVIEW_PAID, ");
		sb.append(" 		COALESCE(REVIEW.BALANCE_DUE, 0) AS REVIEW_BALANCE ");
		sb.append(" 	FROM ");
		sb.append(" 		GROUPS ");
		sb.append(" 		LEFT OUTER JOIN PERMIT ON GROUPS.GROUP_ID = PERMIT.GROUP_ID ");
		sb.append(" 		LEFT OUTER JOIN REVIEW ON GROUPS.GROUP_ID = REVIEW.GROUP_ID ");
		sb.append(" ) ");
		sb.append(" , CALC AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		GFEES.REF_TYPE, ");
		sb.append(" 		GFEES.REF_ID, ");
		sb.append(" 		GFEES.REF, ");
		sb.append(" 		GFEES.GROUP_ID, ");
		sb.append(" 		GFEES.GROUP_NAME, ");
		sb.append(" 		COALESCE(FLOCK.FINANCE_LOCK, 'N') AS FINANCE_LOCK, ");
		sb.append(" 		GFEES.ORDR, ");
		sb.append(" 		GFEES.PERMIT_FEE, ");
		sb.append(" 		GFEES.REVIEW_FEE, ");
		sb.append(" 		GFEES.REVIEW_FEE + GFEES.REVIEW_FEE AS FEE_AMOUNT, ");
		sb.append(" 		GFEES.PERMIT_PAID + GFEES.REVIEW_PAID AS FEE_PAID, ");
		sb.append(" 		GFEES.PERMIT_BALANCE + GFEES.REVIEW_BALANCE AS BALANCE_DUE ");
		sb.append(" 	FROM ");
		sb.append(" 		GFEES ");
		sb.append(" 		LEFT OUTER JOIN FLOCK ON GFEES.REF_ID = FLOCK.REF_ID ");
		sb.append(" ) ");
		sb.append(" , TOTAL AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		CALC.REF_TYPE, ");
		sb.append(" 		CALC.REF_ID, ");
		sb.append(" 		CALC.REF, ");
		sb.append(" 		0 AS GROUP_ID, ");
		sb.append(" 		'TOTAL' AS GROUP_NAME, ");
		sb.append(" 		CALC.FINANCE_LOCK, ");
		sb.append(" 		1000000000 AS ORDR, ");
		sb.append(" 		SUM(CALC.PERMIT_FEE) AS PERMIT_FEE, ");
		sb.append(" 		SUM(CALC.REVIEW_FEE) AS REVIEW_FEE, ");
		sb.append(" 		SUM(CALC.FEE_AMOUNT) AS FEE_AMOUNT, ");
		sb.append(" 		SUM(CALC.FEE_PAID) AS FEE_PAID, ");
		sb.append(" 		SUM(CALC.BALANCE_DUE) AS BALANCE_DUE ");
		sb.append(" 	FROM ");
		sb.append(" 		CALC ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		CALC.REF_TYPE, ");
		sb.append(" 		CALC.REF_ID, ");
		sb.append(" 		CALC.REF, ");
		sb.append(" 		CALC.FINANCE_LOCK ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String summary1(String type, int typeid, int id,boolean empty) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" WITH RY AS ( ");
		sb.append(" select  DISTINCT   FG.GROUP_NAME  , FG.ID AS GROUP_ID,  CASE WHEN (SD.FEE_AMOUNT) IS NULL THEN 0 ELSE (SD.FEE_AMOUNT) END as REVIEW_FEE ,");
		sb.append(" 0.00 as ACTIVITY_FEE, CASE WHEN (SD.FEE_PAID) IS NULL THEN 0 ELSE  (SD.FEE_PAID) END as PAID, ");
		sb.append(" CASE WHEN (SD.BALANCE_DUE) IS NULL THEN 0 ELSE   (SD.BALANCE_DUE) END as BALANCE ");  
		sb.append(" from ").append(maintableref).append("  A    ");
		sb.append(" join REF_").append(tableref).append("_STATEMENT RAS on A.ID=RAS.").append(idref).append(" ");   
		
		sb.append(" join STATEMENT_DETAIL SD on  RAS.STATEMENT_ID=SD.STATEMENT_ID  and SD.ACTIVE ='Y' "); 
		sb.append(" join FEE_GROUP FG   on SD.GROUP_ID=FG.ID  ");
		sb.append(" left outer join REF_FEE_GROUP RFG on FG.ID =RFG.FEE_GROUP_ID AND SD.FEE_ID= RFG.FEE_ID AND REVIEW_FEE='Y' ");
		sb.append(" where A.ID= ").append(typeid).append("   AND REVIEW_FEE IS NOT NULL ");
	//	sb.append(" group by FG.ID,FG.GROUP_NAME  ");
		sb.append(" ), RN AS ( ");
		sb.append(" ");
		sb.append(" select     FG.GROUP_NAME  , FG.ID AS GROUP_ID, 0.00 as REVIEW_FEE,CASE WHEN (SD.FEE_AMOUNT) IS NULL THEN 0 ELSE (SD.FEE_AMOUNT) END as ACTIVITY_FEE , CASE WHEN (SD.FEE_PAID) IS NULL THEN 0 ELSE  (SD.FEE_PAID) END  as PAID, ");
		sb.append(" CASE WHEN (SD.BALANCE_DUE) IS NULL THEN 0 ELSE   (SD.BALANCE_DUE) END as BALANCE   ");
		sb.append(" from ").append(maintableref).append("  A    ");
		sb.append(" join REF_").append(tableref).append("_STATEMENT RAS on A.ID=RAS.").append(idref).append(" ");   
		//sb.append(" join STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");   
		sb.append(" join STATEMENT_DETAIL SD on  RAS.STATEMENT_ID=SD.STATEMENT_ID  and SD.ACTIVE ='Y' "); 
		sb.append(" join FEE_GROUP FG   on SD.GROUP_ID=FG.ID "); 
		sb.append(" left outer join REF_FEE_GROUP RFG on FG.ID =RFG.FEE_GROUP_ID AND SD.FEE_ID= RFG.FEE_ID AND REVIEW_FEE='N' ");
		sb.append(" where A.ID= ").append(typeid).append("   AND REVIEW_FEE IS NOT NULL ");
		//sb.append(" group by FG.ID,FG.GROUP_NAME  ");
		sb.append(" ), RYN AS ( ");
	
		sb.append(" select RN.GROUP_ID, RN.GROUP_NAME, ");
		sb.append(" SUM(RN.ACTIVITY_FEE) as activity_fee, "); 
		sb.append(" SUM(RN.REVIEW_FEE) as   review_fee,  ");
		sb.append(" SUM(RN.PAID) as   paid,    ");
		sb.append(" SUM(RN.BALANCE)as  balance  ");
		sb.append(" from RN  group by RN.GROUP_ID,RN.GROUP_NAME");
		sb.append(" ");
		sb.append(" union ");
		sb.append(" select RY.GROUP_ID, RY.GROUP_NAME, ");
		sb.append(" SUM(RY.ACTIVITY_FEE) as activity_fee, "); 
		sb.append(" SUM(RY.REVIEW_FEE) as   review_fee,  ");
		sb.append(" SUM(RY.PAID) as   paid,    ");
		sb.append(" SUM(RY.BALANCE)as  balance  ");
		sb.append(" from RY group by RY.GROUP_ID,RY.GROUP_NAME ");
		sb.append(") ");
		
		sb.append(" select * from RYN ");
		sb.append(" union "); 
		sb.append(" ");
		sb.append(" select 0 as GROUP_ID,'TOTAL' as GROUPNAME, ");
		sb.append(" SUM(RYN.ACTIVITY_FEE) as ACTIVITY_FEE, "); 
		sb.append(" SUM(RYN.REVIEW_FEE) as REVIEW_FEE,  ");
		sb.append(" SUM(RYN.PAID) as PAID,  ");
		sb.append(" SUM(RYN.BALANCE) as BALANCE ");
		sb.append(" from RYN  ORDER BY  group_id desc ");


		return sb.toString();
	}
	
	public static String getActivityFee(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" select * ");
		sb.append(" from REF_ACT_WORKSHEET RW  ");
		sb.append(" join FEE_WORKSHEET FW on RW.ID=FW.REF_ID  ");
		sb.append(" join REF_FEE_GROUP RFG on FW.FEE_ID=RFG.FEE_ID  ");
		sb.append(" join FEE_GROUP FG on RFG.FEE_GROUP_ID=FG.ID  ");
		sb.append(" where ACT_ID=  ").append(id);
		
		return sb.toString();
	}

	public static String getFeeGroups(String type, int typeid){
		return getTable(type, typeid);
	}

	public static String getFeeGroups1(String type, int typeid){
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(" WITH RY AS ( ");

		sb.append(" SELECT DISTINCT ");
		sb.append(" 	SD.ID, ");
		sb.append(" 	FG.GROUP_NAME , ");
		sb.append(" 	FG.ID AS GROUP_ID , ");
		sb.append(" 	CASE WHEN (SD.FEE_AMOUNT) IS NULL THEN 0 ELSE (SD.FEE_AMOUNT) END AS REVIEW_FEE, ");
		sb.append(" 	0 as ACTIVITY_FEE, ");
		sb.append(" 	CASE WHEN (SD.FEE_PAID) IS NULL THEN 0 ELSE (SD.FEE_PAID) END AS PAID, ");
		sb.append(" 	CASE WHEN (SD.BALANCE_DUE) IS NULL THEN 0 ELSE (SD.BALANCE_DUE) END AS BALANCE ");
		sb.append(" FROM ");
		sb.append(" 	FEE_GROUP AS FG ");
		sb.append(" 	JOIN REF_").append(tableref).append("_FEE_GROUP AS RAT ON FG.ID=RAT.FEE_GROUP_ID ");
		sb.append(" 	JOIN ").append(maintableref).append(" AS A on RAT.LKUP_ACT_TYPE_ID=A.LKUP_ACT_TYPE_ID ");
		sb.append(" 	JOIN REF_FEE_GROUP RFG on RFG.FEE_GROUP_ID=FG.ID  AND RFG.REVIEW_FEE= 'Y' ");
		sb.append(" 	JOIN FEE F on RFG.FEE_ID=F.ID  ");
		sb.append(" 	LEFT OUTER JOIN REF_").append(tableref).append("_STATEMENT AS RAS ON A.ID=RAS.").append(idref).append(" ");
		sb.append(" 	LEFT OUTER JOIN STATEMENT S ON RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");
		sb.append(" 	LEFT OUTER JOIN STATEMENT_DETAIL AS SD ON FG.ID=SD.GROUP_ID AND F.ID=SD.FEE_ID AND S.ID=SD.STATEMENT_ID AND SD.ACTIVE='Y'  ");
		//sb.append(" left outer join REF_FEE_FORMULA RFF on SD.FEE_ID=RFF.FEE_ID  and (RFF.START_DATE>= SD.CREATED_DATE AND  RFF.EXPIRATION_DATE <= SD.CREATED_DATE OR RFF.EXPIRATION_DATE is null)   ");
		//sb.append(" left outer join LKUP_FORMULA LF on RFF.LKUP_FORMULA_ID=LF.ID  ");
		sb.append(" WHERE ");
		sb.append(" 	A.ID = ").append(typeid);
		//sb.append(" group by FG.ID,FG.GROUP_NAME  ");

		sb.append("  ), ");
		sb.append(" RN AS ( ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	SD.ID, ");
		sb.append(" 	FG.GROUP_NAME, ");
		sb.append(" 	FG.ID AS GROUP_ID, ");
		sb.append(" 	CASE WHEN (SD.FEE_AMOUNT) IS NULL THEN 0 ELSE (SD.FEE_AMOUNT) END AS ACTIVITY_FEE, ");
		sb.append(" 	0 as REVIEW_FEE, ");
		sb.append(" 	CASE WHEN (SD.FEE_PAID) IS NULL THEN 0 ELSE (SD.FEE_PAID) END AS PAID, ");
		sb.append(" 	CASE WHEN (SD.BALANCE_DUE) IS NULL THEN 0 ELSE (SD.BALANCE_DUE) END AS BALANCE ");
		sb.append(" FROM ");
		sb.append(" FEE_GROUP FG    ");
		sb.append(" JOIN REF_").append(tableref).append("_FEE_GROUP AS RAT ON FG.ID=RAT.FEE_GROUP_ID     ");
		sb.append(" JOIN ").append(maintableref).append(" AS A ON RAT.LKUP_ACT_TYPE_ID=A.LKUP_ACT_TYPE_ID    ");
		sb.append(" JOIN REF_FEE_GROUP RFG ON RFG.FEE_GROUP_ID=FG.ID AND RFG.REVIEW_FEE= 'N' ");
		sb.append(" JOIN FEE F ON RFG.FEE_ID=F.ID  ");
		sb.append(" LEFT OUTER JOIN REF_").append(tableref).append("_STATEMENT RAS ON A.ID=RAS.").append(idref).append(" ");
		sb.append(" LEFT OUTER JOIN STATEMENT S ON RAS.STATEMENT_ID =S.ID AND S.ACTIVE='Y' ");
		sb.append(" LEFT OUTER JOIN STATEMENT_DETAIL SD ON FG.ID=SD.GROUP_ID AND F.ID=SD.FEE_ID AND S.ID=SD.STATEMENT_ID AND SD.ACTIVE='Y' ");
		//sb.append(" left outer join REF_FEE_FORMULA RFF on SD.FEE_ID=RFF.FEE_ID  and (RFF.START_DATE>= SD.CREATED_DATE AND  RFF.EXPIRATION_DATE <= SD.CREATED_DATE OR RFF.EXPIRATION_DATE is null)   ");
		//sb.append(" left outer join LKUP_FORMULA LF on RFF.LKUP_FORMULA_ID=LF.ID  ");
		sb.append(" WHERE ");
		sb.append(" 	A.ID = ").append(typeid);
		//sb.append(" group by FG.ID,FG.GROUP_NAME  ");

		sb.append(" ), ");
		sb.append(" RYN AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	RN.GROUP_ID, ");
		sb.append(" 	RN.GROUP_NAME, ");
		sb.append(" 	SUM(RN.ACTIVITY_FEE) AS ACTIVITY_FEE, "); 
		sb.append(" 	SUM(RN.REVIEW_FEE) AS REVIEW_FEE,  ");
		sb.append(" 	SUM(RN.PAID) AS PAID, ");
		sb.append(" 	SUM(RN.BALANCE) AS BALANCE ");
		sb.append(" FROM ");
		sb.append(" 	RN ");
		sb.append(" GROUP BY ");
		sb.append(" 	RN.GROUP_ID,RN.GROUP_NAME");

		sb.append(" UNION ");

		sb.append(" SELECT ");
		sb.append(" 	RY.GROUP_ID, ");
		sb.append(" 	RY.GROUP_NAME, ");
		sb.append(" 	SUM(RY.ACTIVITY_FEE) AS ACTIVITY_FEE, "); 
		sb.append(" 	SUM(RY.REVIEW_FEE) AS REVIEW_FEE,  ");
		sb.append(" 	SUM(RY.PAID) AS PAID, ");
		sb.append(" 	SUM(RY.BALANCE) AS BALANCE ");
		sb.append(" FROM ");
		sb.append(" 	RY ");
		sb.append(" GROUP BY ");
		sb.append(" 	RY.GROUP_ID, ");
		sb.append(" 	RY.GROUP_NAME ");
		sb.append(" ) ");
		
		sb.append(" SELECT ");
		sb.append(" RYN.GROUP_ID, ");
		sb.append(" RYN.GROUP_NAME, ");
		sb.append(" SUM(RYN.ACTIVITY_FEE) AS ACTIVITY_FEE, "); 
		sb.append(" SUM(RYN.REVIEW_FEE) AS REVIEW_FEE,  ");
		sb.append(" SUM(RYN.PAID) AS PAID, ");
		sb.append(" SUM(RYN.BALANCE) AS BALANCE ");
		sb.append(" FROM ");
		sb.append(" RYN ");
		sb.append(" GROUP BY ");
		sb.append(" RYN.GROUP_ID, ");
		sb.append(" RYN.GROUP_NAME ");
		sb.append(" UNION "); 
		sb.append(" SELECT ");
		sb.append(" 0 as GROUP_ID, ");
		sb.append(" 'TOTAL' as GROUPNAME, ");
		sb.append(" SUM(RYN.ACTIVITY_FEE) AS ACTIVITY_FEE, "); 
		sb.append(" SUM(RYN.REVIEW_FEE) AS REVIEW_FEE,  ");
		sb.append(" SUM(RYN.PAID) AS PAID, ");
		sb.append(" SUM(RYN.BALANCE) AS BALANCE ");
		sb.append(" FROM ");
		sb.append(" RYN  ");
		sb.append(" ORDER BY ");
		sb.append(" GROUP_ID DESC ");
		return sb.toString();
	}
	
	public static String getNewActFeeGroups(RequestVO r){
		return getNewActFeeGroups(r.getType(), r.getTypeid());
	}
	
	public static String getNewActFeeGroups(String type, int typeid){
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(" WITH RY AS ( ");

		sb.append(" SELECT DISTINCT ");
		sb.append(" 	SD.ID, ");
		sb.append(" 	FG.GROUP_NAME , ");
		sb.append(" 	FG.ID AS GROUP_ID , ");
		sb.append(" 	CASE WHEN (SD.FEE_AMOUNT) IS NULL THEN 0 ELSE (SD.FEE_AMOUNT) END AS REVIEW_FEE, ");
		sb.append(" 	0 as ACTIVITY_FEE, ");
		sb.append(" 	CASE WHEN (SD.FEE_PAID) IS NULL THEN 0 ELSE (SD.FEE_PAID) END AS PAID, ");
		sb.append(" 	CASE WHEN (SD.BALANCE_DUE) IS NULL THEN 0 ELSE (SD.BALANCE_DUE) END AS BALANCE ");
		sb.append(" FROM ");
		sb.append(" 	FEE_GROUP AS FG ");
		sb.append(" 	JOIN REF_").append(tableref).append("_FEE_GROUP AS RAT ON FG.ID=RAT.FEE_GROUP_ID AND RAT.ACTIVE = 'Y' AND FG.ACTIVE = 'Y' ");
		sb.append(" 	JOIN ").append(maintableref).append(" AS A on RAT.LKUP_ACT_TYPE_ID=A.LKUP_ACT_TYPE_ID ");
		sb.append(" 	JOIN REF_FEE_GROUP RFG on RFG.FEE_GROUP_ID=FG.ID  AND RFG.REVIEW_FEE= 'Y' AND RFG.ACTIVE = 'Y' ");
		sb.append(" 	JOIN FEE F on RFG.FEE_ID=F.ID AND F.ACTIVE = 'Y' ");
		sb.append(" 	LEFT OUTER JOIN REF_").append(tableref).append("_STATEMENT AS RAS ON A.ID=RAS.").append(idref).append(" ");
		sb.append(" 	LEFT OUTER JOIN STATEMENT S ON RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");
		sb.append(" 	LEFT OUTER JOIN STATEMENT_DETAIL AS SD ON FG.ID=SD.GROUP_ID AND F.ID=SD.FEE_ID AND S.ID=SD.STATEMENT_ID AND SD.ACTIVE='Y'  ");
		//sb.append(" left outer join REF_FEE_FORMULA RFF on SD.FEE_ID=RFF.FEE_ID  and (RFF.START_DATE>= SD.CREATED_DATE AND  RFF.EXPIRATION_DATE <= SD.CREATED_DATE OR RFF.EXPIRATION_DATE is null)   ");
		//sb.append(" left outer join LKUP_FORMULA LF on RFF.LKUP_FORMULA_ID=LF.ID  ");
		sb.append(" WHERE ");
		sb.append(" 	A.ID = ").append(typeid);
		//sb.append(" group by FG.ID,FG.GROUP_NAME  ");

		sb.append("  ), ");
		sb.append(" RN AS ( ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	SD.ID, ");
		sb.append(" 	FG.GROUP_NAME, ");
		sb.append(" 	FG.ID AS GROUP_ID, ");
		sb.append(" 	CASE WHEN (SD.FEE_AMOUNT) IS NULL THEN 0 ELSE (SD.FEE_AMOUNT) END AS ACTIVITY_FEE, ");
		sb.append(" 	0 as REVIEW_FEE, ");
		sb.append(" 	CASE WHEN (SD.FEE_PAID) IS NULL THEN 0 ELSE (SD.FEE_PAID) END AS PAID, ");
		sb.append(" 	CASE WHEN (SD.BALANCE_DUE) IS NULL THEN 0 ELSE (SD.BALANCE_DUE) END AS BALANCE ");
		sb.append(" FROM ");
		sb.append(" FEE_GROUP AS FG ");
		sb.append(" JOIN REF_").append(tableref).append("_FEE_GROUP AS RAT ON FG.ID=RAT.FEE_GROUP_ID  AND RAT.ACTIVE = 'Y' ");
		sb.append(" JOIN ").append(maintableref).append(" AS A ON RAT.LKUP_ACT_TYPE_ID=A.LKUP_ACT_TYPE_ID    ");
		sb.append(" JOIN REF_FEE_GROUP RFG ON RFG.FEE_GROUP_ID=FG.ID AND RFG.REVIEW_FEE= 'N' AND RFG.ACTIVE = 'Y' ");
		sb.append(" JOIN FEE F ON RFG.FEE_ID=F.ID AND F.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REF_").append(tableref).append("_STATEMENT RAS ON A.ID=RAS.").append(idref).append(" ");
		sb.append(" LEFT OUTER JOIN STATEMENT S ON RAS.STATEMENT_ID =S.ID AND S.ACTIVE='Y' ");
		sb.append(" LEFT OUTER JOIN STATEMENT_DETAIL SD ON FG.ID=SD.GROUP_ID AND F.ID=SD.FEE_ID AND S.ID=SD.STATEMENT_ID AND SD.ACTIVE='Y' ");
		//sb.append(" left outer join REF_FEE_FORMULA RFF on SD.FEE_ID=RFF.FEE_ID  and (RFF.START_DATE>= SD.CREATED_DATE AND  RFF.EXPIRATION_DATE <= SD.CREATED_DATE OR RFF.EXPIRATION_DATE is null)   ");
		//sb.append(" left outer join LKUP_FORMULA LF on RFF.LKUP_FORMULA_ID=LF.ID  ");
		sb.append(" WHERE ");
		sb.append(" 	A.ID = ").append(typeid);
		//sb.append(" group by FG.ID,FG.GROUP_NAME  ");

		sb.append(" ), ");
		sb.append(" RYN AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	RN.GROUP_ID, ");
		sb.append(" 	RN.GROUP_NAME, ");
		sb.append(" 	SUM(RN.ACTIVITY_FEE) AS ACTIVITY_FEE, "); 
		sb.append(" 	SUM(RN.REVIEW_FEE) AS REVIEW_FEE,  ");
		sb.append(" 	SUM(RN.PAID) AS PAID, ");
		sb.append(" 	SUM(RN.BALANCE) AS BALANCE ");
		sb.append(" FROM ");
		sb.append(" 	RN ");
		sb.append(" GROUP BY ");
		sb.append(" 	RN.GROUP_ID,RN.GROUP_NAME");

		sb.append(" UNION ");

		sb.append(" SELECT ");
		sb.append(" 	RY.GROUP_ID, ");
		sb.append(" 	RY.GROUP_NAME, ");
		sb.append(" 	SUM(RY.ACTIVITY_FEE) AS ACTIVITY_FEE, "); 
		sb.append(" 	SUM(RY.REVIEW_FEE) AS REVIEW_FEE,  ");
		sb.append(" 	SUM(RY.PAID) AS PAID, ");
		sb.append(" 	SUM(RY.BALANCE) AS BALANCE ");
		sb.append(" FROM ");
		sb.append(" 	RY ");
		sb.append(" GROUP BY ");
		sb.append(" 	RY.GROUP_ID, ");
		sb.append(" 	RY.GROUP_NAME ");
		sb.append(" ) ");
		
		sb.append(" SELECT ");
		sb.append(" RYN.GROUP_ID, ");
		sb.append(" RYN.GROUP_NAME, ");
		sb.append(" SUM(RYN.ACTIVITY_FEE) AS ACTIVITY_FEE, "); 
		sb.append(" SUM(RYN.REVIEW_FEE) AS REVIEW_FEE,  ");
		sb.append(" SUM(RYN.PAID) AS PAID, ");
		sb.append(" SUM(RYN.BALANCE) AS BALANCE ");
		sb.append(" FROM ");
		sb.append(" RYN ");
		sb.append(" GROUP BY ");
		sb.append(" RYN.GROUP_ID, ");
		sb.append(" RYN.GROUP_NAME ");
		sb.append(" UNION "); 
		sb.append(" SELECT ");
		sb.append(" 0 as GROUP_ID, ");
		sb.append(" 'TOTAL' as GROUPNAME, ");
		sb.append(" SUM(RYN.ACTIVITY_FEE) AS ACTIVITY_FEE, "); 
		sb.append(" SUM(RYN.REVIEW_FEE) AS REVIEW_FEE,  ");
		sb.append(" SUM(RYN.PAID) AS PAID, ");
		sb.append(" SUM(RYN.BALANCE) AS BALANCE ");
		sb.append(" FROM ");
		sb.append(" RYN  ");
		sb.append(" ORDER BY ");
		sb.append(" GROUP_ID DESC ");
		Logger.highlight("################################################################################################");
		return sb.toString();
	}
	
	public static String getFeeGroups(RequestVO r){
		return getFeeGroups(r.getType(), r.getTypeid());
	}
	
	public static String getFeeGroupsFees(RequestVO r,int groupId){
		if (!Operator.hasValue(r.getType())) { return ""; }
		String tableref = CsReflect.getTableRef(r.getType());
		String idref = CsReflect.getFieldIdRef(r.getType());
		String maintableref = CsReflect.getMainTableRef(r.getType());
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select  ");
		sb.append(" DISTINCT FG.GROUP_NAME , FG.ID AS GROUP_ID ,");
		sb.append(" SD.FEE_ID");
		sb.append(",");
		sb.append(" SD.ID ");
		sb.append(",");
		sb.append(" F.NAME ");
		sb.append(",");
		sb.append(" F.DESCRIPTION ");
		sb.append(",");
		sb.append(" SD.INPUT1 ");
		sb.append(",");
		sb.append(" SD.INPUT2 ");
		sb.append(",");
		sb.append(" SD.INPUT3 ");
		sb.append(",");
		sb.append(" SD.INPUT4 ");
		sb.append(",");
		sb.append(" SD.INPUT5 ");
		sb.append(",");
		sb.append(" SD.FINANCE_MAP_ID ");
		sb.append(",");
		sb.append(" SD.ACCOUNT_NUMBER ");
		sb.append(",");
		sb.append("  CONVERT(VARCHAR(10),SD.FEE_DATE,101)  as FEE_DATE ");
		
		sb.append(",");
		sb.append(" SD.FEE_AMOUNT ");
		sb.append(",");
		sb.append(" SD.FEE_PAID ");
		sb.append(",");
		sb.append(" SD.BALANCE_DUE ");
		
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE1 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE2 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE3 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE4 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE5 ");
		
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL1 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL2 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL3 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL4 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL5 ");
		
		
		sb.append(",");
		sb.append(" RFF.LKUP_FORMULA_ID ");
		
		sb.append(",");
		sb.append("  CONVERT(VARCHAR(10),RFF.START_DATE,101)  as START_DATE ");
		sb.append(",");
		sb.append("  CONVERT(VARCHAR(10),RFF.EXPIRATION_DATE,101) as EXPIRATION_DATE ");
		
		
		/*sb.append(",");
		sb.append(" LF.DEFINITION ");*/
		sb.append(",");
		sb.append(" SD.ACTIVE ");
		
		sb.append(",");
		sb.append(" RFG.REVIEW_FEE ");
		/*sb.append(",");
		sb.append(" S.FINANCE_LOCK ");
		sb.append(",");
		sb.append(" UP.USERNAME ");*/
		
		sb.append("    , ");
		sb.append("  CASE WHEN     ( RFF.INPUT_EDITABLE1 ='Y' OR  ");
		sb.append(" RFF.INPUT_EDITABLE2 ='Y' OR  ");
		sb.append(" RFF.INPUT_EDITABLE3 ='Y' OR  ");
		sb.append("  RFF.INPUT_EDITABLE4 ='Y' OR  ");
		sb.append("  RFF.INPUT_EDITABLE5 ='Y' )  THEN 'Y' else 'N' END AS EDIT ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE1 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE2 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE3 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE4 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE5 ");
		
		sb.append(" from FEE_GROUP FG  ");
		sb.append(" join REF_").append(tableref).append("_FEE_GROUP RAT on FG.ID=RAT.FEE_GROUP_ID ");
		sb.append(" join ").append(maintableref).append("  A on RAT.LKUP_").append(tableref).append("_TYPE_ID=A.LKUP_").append(tableref).append("_TYPE_ID  ");
		sb.append(" join REF_FEE_GROUP RFG on RFG.FEE_GROUP_ID=FG.ID ");
		sb.append(" join FEE F on RFG.FEE_ID=F.ID ");
		sb.append(" left outer join REF_").append(tableref).append("_STATEMENT RAS on A.ID=RAS.").append(idref).append(" ");
		
		sb.append(" left outer join STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");
		//sb.append("  LEFT OUTER JOIN USERS UP on S.UPDATED_BY = UP.ID 	  ");
		
		sb.append(" left outer join STATEMENT_DETAIL SD on FG.ID=SD.GROUP_ID and F.ID=SD.FEE_ID and S.ID=SD.STATEMENT_ID ");
		

		//sb.append(" left outer join REF_FEE_FORMULA RFF on SD.FEE_ID=RFF.FEE_ID and (RFF.START_DATE>= SD.CREATED_DATE AND  RFF.EXPIRATION_DATE <= SD.CREATED_DATE OR RFF.EXPIRATION_DATE is null) ");
		sb.append(" left outer join REF_FEE_FORMULA RFF on SD.REF_FEE_FORMULA_ID=RFF.ID  ");
		//sb.append(" left outer join LKUP_FORMULA LF on RFF.LKUP_FORMULA_ID=LF.ID ");
		sb.append(" where A.ID= ").append(r.getTypeid()).append(" and FG.ID=").append(groupId).append(" and SD.ID is not null ");
		if(!r.getAction().equalsIgnoreCase("showall")){
			sb.append(" and SD.ACTIVE ='Y' ");
		}
		
		sb.append(" order by SD.ID,FG.ID ");
		
		return sb.toString();
	}
	
	public static String getGroups(RequestVO r){
		StringBuilder sb = new StringBuilder();
		sb.append("select top 10 * from FEE_GROUP where ACTIVE='Y'");
		return sb.toString();
	}
	
	public static String getFeeList(String type, int typeid, String entity,int groupid,String date,String divisionfilters){
		return getFeeList(type,typeid,entity,groupid, date, false, false,divisionfilters);
	}
	
	public static String getFeeList(String type, int typeid, String entity,int groupid,String date, boolean autoadd, boolean plancheck,String divisionfilters){
		StringBuilder sb = new StringBuilder();
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		
		
		
		sb.append(" select  ");
		sb.append(" F.ID ");
		sb.append(",");
		sb.append(" F.NAME ");
		sb.append(",");
		sb.append(" F.DESCRIPTION ");
		sb.append(",");
		sb.append("  CONVERT(VARCHAR(10),RFF.START_DATE,101)  as START_DATE ");
		//sb.append(" RFF.START_DATE ");
		sb.append(",");
		//sb.append(" RFF.EXPIRATION_DATE ");
		sb.append("  CONVERT(VARCHAR(10),RFF.EXPIRATION_DATE,101) as EXPIRATION_DATE ");
		sb.append(",");
		sb.append(" RFF.ACCOUNT ");
		sb.append(",");
		sb.append(" RFF.INPUT1 ");
		sb.append(",");
		sb.append(" RFF.INPUT2 ");
		sb.append(",");
		sb.append(" RFF.INPUT3 ");
		sb.append(",");
		sb.append(" RFF.INPUT4 ");
		sb.append(",");
		sb.append(" RFF.INPUT5 ");
		sb.append(",");
		sb.append(" RFF.FACTOR ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE1 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE2 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE3 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE4 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE5 ");
		
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE1 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE2 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE3 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE4 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE5 ");
		
		
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL1 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL2 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL3 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL4 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL5 ");
		
		
		sb.append(",");
		sb.append(" RFF.MANUAL_ACCOUNT ");
		
		sb.append(",");
		sb.append(" RFF.LKUP_FORMULA_ID ");
		sb.append(",");
		sb.append(" LF.DEFINITION ");
		sb.append(",");
		sb.append(" RFG.REQ ");
		sb.append(",");
		sb.append(" FG.GROUP_NAME ");
		sb.append(",");
		sb.append(" FG.ID AS GROUP_ID ");
		sb.append(",");
		sb.append(" RFF.ID as RFF_ID ");
		sb.append(" from REF_FEE_FORMULA RFF ");
		sb.append(" left outer JOIN REF_FEE_GROUP RFG on RFF.FEE_ID=RFG.FEE_ID AND RFG.FEE_GROUP_ID=").append(groupid).append(" AND RFG.ACTIVE='Y' ");
		sb.append(" left outer join LKUP_FORMULA LF on RFF.LKUP_FORMULA_ID=LF.ID ");
		sb.append(" JOIN FEE F on RFF.FEE_ID=F.ID ");
		sb.append(" join FEE_GROUP FG on RFG.FEE_GROUP_ID=FG.ID ");
		//sb.append(" where RFG.FEE_GROUP_ID=").append(groupid).append("  ");
		//sb.append(" and EXPIRATION_DATE is null ");
		//sb.append( " and ((RFF.START_DATE >= '").append(date).append("' OR RFF.EXPIRATION_DATE is null) OR (RFF.EXPIRATION_DATE >= '").append(date).append("' or RFF.EXPIRATION_DATE is null) )");
		sb.append( " WHERE  ((CONVERT(VARCHAR(10),RFF.START_DATE,101) <= '").append(date).append("' and RFF.EXPIRATION_DATE >= '").append(date).append("'  )  OR (CONVERT(VARCHAR(10),RFF.START_DATE,101) <= '").append(date).append("' AND RFF.EXPIRATION_DATE is null ) )"); 

		if(autoadd && !plancheck){
			sb.append(" AND RFG.AUTO_ADD='Y' ");
			if(Operator.hasValue(divisionfilters)){
				sb.append(" AND RFG.FEE_ID not in (").append(divisionfilters).append(") ");
			}
		}
		
		if(plancheck && !autoadd){
			sb.append(" AND RFG.PLAN_CHK_REQ='Y' ");
			if(Operator.hasValue(divisionfilters)){
				sb.append(" AND RFG.FEE_ID not in (").append(divisionfilters).append(") ");
			}
		}
		
		if(autoadd && plancheck){
			sb.append(" AND (RFG.AUTO_ADD='Y'  OR  RFG.PLAN_CHK_REQ='Y' ) ");
			if(Operator.hasValue(divisionfilters)){
				sb.append(" AND RFG.FEE_ID not in (").append(divisionfilters).append(") ");
			}
		}
		if(!autoadd && !plancheck){
			if(Operator.hasValue(divisionfilters)){
				sb.append(" AND RFG.FEE_ID not in (").append(divisionfilters).append(") ");
			}
		}
		
		sb.append(" order by RFG.ORDR ");
		
		
		
		return sb.toString();
	}
	
	
	public static String getFeeFilterList(String divids,int groupid){
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" select DISTINCT LTTT.ID,LTTT.TYPE, CASE WHEN LT.ID is null then 0 else LT.ID END as LKUP_DIVISIONS_ID  from  ");
		sb.append(" LKUP_DIVISIONS_TYPE LTTT  ");
		//sb.append(" left outer join  V_CENTRAL_DIVISION LT on LTTT.ID= LT.LKUP_DIVISIONS_TYPE_ID  AND LSO_ID = ").append(lsoId);
		sb.append("   LEFT OUTER JOIN LKUP_DIVISIONS AS LT ON LTTT.ID = LT.LKUP_DIVISIONS_TYPE_ID AND LT.ID IN ("+divids+") ");
		sb.append(" ), ");
	
		sb.append(" FOUT AS( ");
		sb.append(" select FOUT.FEE_ID,LTTT.ID as TYPE_ID,LTTT.DESCRIPTION as TYPE,LTT.ID,'NOTEQUAL' as E from REF_FEE_OUT_DIVISIONS FOUT  ");
		sb.append(" left outer join LKUP_DIVISIONS LTT on FOUT.LKUP_DIVISIONS_ID =LTT.ID ");
		sb.append(" left outer join LKUP_DIVISIONS_TYPE LTTT on LTT.LKUP_DIVISIONS_TYPE_ID = LTTT.ID ");
		sb.append(" where FEE_GROUP_ID=").append(groupid).append(" and FOUT.ACTIVE='Y'");
		sb.append("  ");
		sb.append(" UNION ");
		sb.append("  ");
		sb.append(" select FINN.FEE_ID,LTTT.ID as TYPE_ID,LTTT.DESCRIPTION as TYPE,LTT.ID,'EQUAL' as E  from REF_FEE_IN_DIVISIONS FINN  ");
		sb.append(" left outer join LKUP_DIVISIONS LTT on FINN.LKUP_DIVISIONS_ID =LTT.ID ");
		sb.append(" left outer join LKUP_DIVISIONS_TYPE LTTT on LTT.LKUP_DIVISIONS_TYPE_ID = LTTT.ID ");
		sb.append(" where FEE_GROUP_ID=").append(groupid).append(" and FINN.ACTIVE='Y' ");
		sb.append("  ");
		sb.append("  ");
		sb.append(" ) ");
		sb.append(" select  ROW_NUMBER() OVER(ORDER BY LKUP_DIVISIONS_ID ASC) AS ROW_ID,Q.ID,Q.TYPE ,Q.LKUP_DIVISIONS_ID ");
		sb.append(" ,FOUT.FEE_ID,FOUT.TYPE_ID,FOUT.TYPE,FOUT.ID as FOUT_LKUP_DIVISIONS_ID ,FOUT.E AS OPT ");
		sb.append(" from Q ");
		sb.append(" left outer join FOUT  on Q.ID = FOUT.TYPE_ID  ");
		sb.append("  ");
		sb.append(" where FOUT.ID is not null order by FOUT.E ");
		
		return sb.toString();
	}
	
	/*public static String getFeeFilterList(String type, int typeid, String entity,int groupid){
		StringBuilder sb = new StringBuilder();
		if (!Operator.hasValue(type)) { return ""; }
		String maintableref = CsReflect.getMainTableRef(type);
	
		sb.append(" WITH FOUT AS( ");
		sb.append(" select FOUT.FEE_ID,LTTT.ID as TYPE_ID,LTTT.DESCRIPTION as TYPE,LTT.ID,'NOTEQUAL' as E from REF_FEE_OUT_DIVISIONS FOUT  ");
		sb.append(" left outer join LKUP_DIVISIONS LTT on FOUT.LKUP_DIVISIONS_ID =LTT.ID ");
		sb.append(" left outer join LKUP_DIVISIONS_TYPE LTTT on LTT.LKUP_DIVISIONS_TYPE_ID = LTTT.ID ");
		sb.append(" where FEE_GROUP_ID=").append(groupid).append(" and FOUT.ACTIVE='Y'");
		sb.append("  ");
		sb.append(" UNION ");
		sb.append("  ");
		sb.append(" select FINN.FEE_ID,LTTT.ID as TYPE_ID,LTTT.DESCRIPTION as TYPE,LTT.ID,'EQUAL' as E  from REF_FEE_IN_DIVISIONS FINN  ");
		sb.append(" left outer join LKUP_DIVISIONS LTT on FINN.LKUP_DIVISIONS_ID =LTT.ID ");
		sb.append(" left outer join LKUP_DIVISIONS_TYPE LTTT on LTT.LKUP_DIVISIONS_TYPE_ID = LTTT.ID ");
		sb.append(" where FEE_GROUP_ID=").append(groupid).append(" and FINN.ACTIVE='Y' ");
		sb.append("  ");
		sb.append("  ");
		sb.append(" ) ");
		sb.append(" select  ROW_NUMBER() OVER(ORDER BY LKUP_DIVISIONS_ID ASC) AS ROW_ID,LTTT.ID,LTTT.DESCRIPTION,LT.LKUP_DIVISIONS_ID ");
		sb.append(" ,FOUT.FEE_ID,FOUT.TYPE_ID,FOUT.TYPE,FOUT.ID as FOUT_LKUP_DIVISIONS_ID ,FOUT.E AS OPT ");
		sb.append(" from ").append(maintableref).append(" A ");
		
		if(maintableref.equalsIgnoreCase("activity")){
			sb.append(" join REF_LSO_PROJECT RL on A.PROJECT_ID= RL.PROJECT_ID  ");
		}else {
			sb.append(" join REF_LSO_PROJECT RL on A.ID= RL.PROJECT_ID  ");
		}
		
		sb.append(" left outer join V_CENTRAL_DIVISION LT on RL.LSO_ID=LT.LSO_ID  ");
		sb.append(" left outer join LKUP_DIVISIONS LTT on LT.LKUP_DIVISIONS_ID = LTT.ID ");
		sb.append(" left outer join LKUP_DIVISIONS_TYPE LTTT on LTT.LKUP_DIVISIONS_TYPE_ID = LTTT.ID ");
		sb.append(" left outer join FOUT  on LTTT.ID = FOUT.TYPE_ID  ");
		sb.append("  ");
		sb.append(" where A.ID=").append(typeid).append(" and FOUT.ID is not null order by FOUT.E ");
		
		return sb.toString();
	}*/
	
	public static String insertStatement(String number,double total,String comment,Timekeeper expdate,String online,int userId){
		StringBuilder sb = new StringBuilder();
		sb.append( "insert into STATEMENT (STATEMENT_NUMBER,FEE_AMOUNT,PAID_AMOUNT,BALANCE_DUE,STATUS,COMMENT,EXPIRATION_DATE,ONLINE,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)");
		sb.append(" VALUES (");
		sb.append("'").append(Operator.sqlEscape(number)).append("'");
		sb.append(",");
		sb.append(total);
		sb.append(",");
		sb.append(0);
		sb.append(",");
		sb.append(total);
		sb.append(",");
		sb.append("'U'");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(comment)).append("'");
		sb.append(",");
		sb.append(expdate.sqlDatetime());
		sb.append(",");
		sb.append("'N'");
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(" ) "); 
		
		return sb.toString();
	}
	
	
	public static String getStatement(String number,int userId){
		StringBuilder sb = new StringBuilder();
		sb.append( "select * from STATEMENT WHERE ID>0 ");
		sb.append(" AND ");
		sb.append(" STATEMENT_NUMBER= '").append(Operator.sqlEscape(number)).append("'");
		sb.append(" AND ");
		sb.append(" CREATED_BY= ").append(userId);
		return sb.toString();
	}
	
	public static String insertRefStatement(String type,int typeId,int statementId,int userId){
		String ref = CsReflect.getTableRef(type);
		String fieldId = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		
		sb.append( "insert into REF_").append(ref).append("_STATEMENT (").append(fieldId).append(",STATEMENT_ID,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)");
		sb.append(" VALUES (");
		sb.append(typeId);
		sb.append(",");
		sb.append(statementId);
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(" ) "); 
		return sb.toString();
	}
	
	public static String searchCashier(String q){
		StringBuilder sb = new StringBuilder();
		sb.append( " SELECT P.ID as PROJECT_ID, 0 as ACTIVITY_ID, 0 as STATEMENT_ID, 'CP' as TYPE from PROJECT P where project_nbr='").append(Operator.sqlEscape(q)).append("' ");
		sb.append( " UNION ");
		sb.append( " SELECT P.ID as PROJECT_ID, A.ID as ACTIVITY_ID, 0 as STATEMENT_ID, 'CA' as TYPE from PROJECT P left outer join activity A on P.ID=A.PROJECT_ID where act_nbr='").append(Operator.sqlEscape(q)).append("' ");
		sb.append( " UNION  ");
		sb.append( " SELECT P.ID as PROJECT_ID, A.ID as ACTIVITY_ID, S.ID as STATEMENT_ID, 'CS' as TYPE from PROJECT P left outer join activity A on P.ID=A.PROJECT_ID "); 
		sb.append( " left outer join ref_act_statement R on A.ID=R.ACTIVITY_ID  ");
		sb.append( " left outer join statement S on R.STATEMENT_ID=S.ID ");
		sb.append( " where STATEMENT_NUMBER='").append(Operator.sqlEscape(q)).append("' ");
		return sb.toString();
	}
	
	public static String searchCashierPayment(String q){
		StringBuilder sb = new StringBuilder();
		sb.append( " select 0 as PROJECT_ID, 0 as ACTIVITY_ID, 0 as STATEMENT_ID,ID as PAYMENT_ID, 'CT' as TYPE from PAYMENT P where ID='").append(Operator.toInt(Operator.sqlEscape(q))).append("' ");
		return sb.toString();
	}
	
	public static String searchCashierUser(String q){
		StringBuilder sb = new StringBuilder();
		sb.append( " select DISTINCT 0 as PROJECT_ID, 0 as ACTIVITY_ID, 0 as STATEMENT_ID,U.ID as USER_ID,CASE WHEN U.USERNAME is NULL THEN U.EMAIL  ELSE U.USERNAME END as USERNAME, 'CU' as TYPE from USERS U JOIN REF_USERS_DEPOSIT RUD on U.ID = RUD.USERS_ID ");
		sb.append(" where ( U.USERNAME like '%").append(Operator.sqlEscape(q)).append("%' ");
		sb.append(" OR  U.ID = ").append(Operator.toInt(q)).append(" ");
		sb.append(" OR  U.EMAIL like '%").append(Operator.sqlEscape(q)).append("%' OR  U.FIRST_NAME like '%").append(Operator.sqlEscape(q)).append("%' )");
		return sb.toString();
	}
	
	public static String searchCashier(String type,int id){
		StringBuilder sb = new StringBuilder();
		if(type.equalsIgnoreCase("CP")){
			sb.append(" select P.ID as PROJECT_ID,P.NAME as PROJECT_NAME , count(A.ID) as CHILDREN from PROJECT P left outer join ACTIVITY A on P.ID = A.PROJECT_ID where P.ID = ").append(id).append(" group by P.ID,P.NAME");
		}
		if(type.equalsIgnoreCase("CA")){
			sb.append(" select P.ID as PROJECT_ID,P.NAME as PROJECT_NAME,A.ID,A.ACT_NBR from PROJECT P left outer join ACTIVITY A on P.ID = A.PROJECT_ID where P.ID = ").append(id);
		}
		
		if(type.equalsIgnoreCase("CS")){
			sb.append("  select P.ID as PROJECT_ID,P.NAME as PROJECT_NAME,A.ID,A.ACT_NBR, S.ID as STATEMENT_ID, S.STATEMENT_NUMBER from PROJECT P left outer join ACTIVITY A on P.ID = A.PROJECT_ID left outer join REF_ACT_STATEMENT R on A.ID = R.ACTIVITY_ID left outer join STATEMENT S on R.STATEMENT_ID = S.ID where A.ID = ").append(id);
		}	
		return sb.toString();
	}
	
	public static String searchCashierActivity(String type,int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" select P.ID as PROJECT_ID,P.NAME as PROJECT_NAME,A.ID,A.ACT_NBR from PROJECT P left outer join ACTIVITY A on P.ID = A.PROJECT_ID where A.ID = ").append(id);
		return sb.toString();
	}
	
	public static String searchCashierBrowse(String type,String id){
		if (Operator.toInt(id) < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		if(type.equalsIgnoreCase("A")){
			sb.append(" select S.ID,STATEMENT_NUMBER as ACT_NBR from STATEMENT S JOIN REF_ACT_STATEMENT R on S.ID=R.STATEMENT_ID where  R.ACTIVITY_ID = ").append(id);
		}else {
			sb.append(" select A.ID,A.ACT_NBR, COUNT(R.STATEMENT_ID) as CHILDREN from ACTIVITY A LEFT OUTER JOIN REF_ACT_STATEMENT R on A.ID = R.ACTIVITY_ID where PROJECT_ID=").append(id).append(" group by A.ID,A.ACT_NBR ");
		}
		
		return sb.toString();
	}
	
	public static String getProjectStatement(int projectid){
		StringBuilder sb = new StringBuilder();
		sb.append(" select  ");
		sb.append(" DISTINCT FG.GROUP_NAME , FG.ID AS GROUP_ID , sum(SD.FEE_AMOUNT) as FEE_AMOUNT,sum(SD.FEE_PAID) as FEE_PAID,sum(SD.BALANCE_DUE) as BALANCE_DUE ");
		sb.append(" from STATEMENT_DETAIL SD  ");
		sb.append(" left outer join FEE_GROUP FG on SD.GROUP_ID=FG.ID ");
		sb.append(" left outer join STATEMENT S on  SD.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");
		sb.append(" left outer join REF_PROJECT_STATEMENT R on  S.ID =R.STATEMENT_ID  ");
		sb.append(" join FEE F on SD.FEE_ID=F.ID ");
		sb.append(" where  SD.ID is not null ");
		sb.append(" AND R.PROJECT_ID=").append(projectid);
		sb.append(" and SD.ACTIVE ='Y' and S.FINANCE_LOCK ='N' group by SD.GROUP_ID,FG.ID,FG.GROUP_NAME ");
		sb.append(" order by FG.ID ");
		return sb.toString();
	}
	
	public static String getActivityStatement(int activityId){
		StringBuilder sb = new StringBuilder();
		sb.append(" select  ");
		sb.append(" DISTINCT FG.GROUP_NAME , FG.ID AS GROUP_ID , sum(SD.FEE_AMOUNT) as FEE_AMOUNT,sum(SD.FEE_PAID) as FEE_PAID,sum(SD.BALANCE_DUE) as BALANCE_DUE ");
		sb.append(" from STATEMENT_DETAIL SD  ");
		sb.append(" left outer join FEE_GROUP FG on SD.GROUP_ID=FG.ID ");
		sb.append(" left outer join STATEMENT S on  SD.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");
		sb.append(" left outer join REF_ACT_STATEMENT R on  S.ID =R.STATEMENT_ID  ");
		sb.append(" join FEE F on SD.FEE_ID=F.ID ");
		sb.append(" where  SD.ID is not null ");
		sb.append(" AND R.ACTIVITY_ID=").append(activityId);
		sb.append(" and SD.ACTIVE ='Y' and S.FINANCE_LOCK ='N' group by SD.GROUP_ID,FG.ID,FG.GROUP_NAME ");
		sb.append(" order by FG.ID ");
		return sb.toString();
	}
	
	public static String getStatement(int statementId){
		StringBuilder sb = new StringBuilder();
		sb.append(" select  ");
		sb.append(" DISTINCT FG.GROUP_NAME , FG.ID AS GROUP_ID , sum(SD.FEE_AMOUNT) as FEE_AMOUNT,sum(SD.FEE_PAID) as FEE_PAID,sum(SD.BALANCE_DUE) as BALANCE_DUE ");
		sb.append(" from STATEMENT_DETAIL SD  ");
		sb.append(" left outer join FEE_GROUP FG on SD.GROUP_ID=FG.ID ");
		sb.append(" left outer join STATEMENT S on  SD.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");
		sb.append(" join FEE F on SD.FEE_ID=F.ID ");
		sb.append(" where  SD.ID is not null ");
		sb.append(" AND S.ID=").append(statementId);
		sb.append(" and SD.ACTIVE ='Y' and S.FINANCE_LOCK ='N' group by SD.GROUP_ID,FG.ID,FG.GROUP_NAME ");
		sb.append(" order by FG.ID ");
		return sb.toString();
	}
	
	public static String getStatementDetails(int activityId,int groupId,String type){
		StringBuilder sb = new StringBuilder();
		//CHanged to get Map details 01/25/2017
		/*sb.append(" select  ");
		//sb.append(" DISTINCT FG.NAME AS GROUP_NAME , FG.ID AS GROUP_ID ,");
		sb.append(" SD.FEE_ID");
		sb.append(",");
		sb.append(" SD.ID ");
		sb.append(",");
		sb.append(" F.NAME ");
		sb.append(",");
		sb.append(" F.DESCRIPTION ");
		sb.append(",");
		sb.append(" SD.FEE_DATE ");
		sb.append(",");
		sb.append(" SD.FEE_AMOUNT ");
		sb.append(",");
		sb.append(" SD.FEE_PAID ");
		sb.append(",");
		sb.append(" SD.BALANCE_DUE ");
		
		sb.append(" from STATEMENT_DETAIL SD  ");
		sb.append(" join FEE F on SD.FEE_ID=F.ID ");
		if(type.equalsIgnoreCase("P")){
			sb.append(" left outer join REF_PROJECT_STATEMENT R on   SD.STATEMENT_ID  =R.STATEMENT_ID  ");
			sb.append(" where SD.ID is not null and SD.GROUP_ID=").append(groupId).append(" AND R.PROJECT_ID=").append(activityId);
		}else {
			sb.append(" left outer join REF_ACT_STATEMENT R on   SD.STATEMENT_ID  =R.STATEMENT_ID  ");
			sb.append(" where SD.ID is not null and SD.GROUP_ID=").append(groupId).append(" AND R.ACTIVITY_ID=").append(activityId);
		}
		
		sb.append(" order by SD.ID ");*/
		
		String table ="REF_ACT_STATEMENT";
		String column ="ACTIVITY_ID";
		if(type.equalsIgnoreCase("P")){
			table ="REF_PROJECT_STATEMENT";
			column ="PROJECT_ID";
		}
		
		sb.append(" WITH Q AS (  ");
		sb.append(" select  DISTINCT M.*, SD.FEE_ID   ");
		sb.append(" from STATEMENT_DETAIL SD   join FEE F on SD.FEE_ID=F.ID    ");
		sb.append(" left outer join ").append(table).append(" R on   SD.STATEMENT_ID  =R.STATEMENT_ID    ");
		sb.append(" left outer join REF_FEE_FORMULA RFF on SD.FEE_ID=RFF.FEE_ID   ");
		sb.append(" left outer join FINANCE_MAP M on RFF.FINANCE_MAP_ID =M.ID  ");
		sb.append(" where SD.ID is not null and RFF.EXPIRATION_DATE IS NULL and SD.GROUP_ID=").append(groupId).append("  AND SD.ACTIVE='Y' AND R.").append(column).append(" =").append(activityId);
		sb.append(" )  ");
		
		
		sb.append(" , IND AS ( ");
		sb.append(" 		select  DISTINCT M.*, SD.FEE_ID  ");
		sb.append(" from STATEMENT_DETAIL SD   join FEE F on SD.FEE_ID=F.ID   ");
		sb.append(" left outer join ").append(table).append("  R on   SD.STATEMENT_ID  =R.STATEMENT_ID   ");
		sb.append(" left outer join FINANCE_MAP M on SD.FINANCE_MAP_ID =M.ID ");
		sb.append(" where SD.ID is not null and M.ID is not null and SD.GROUP_ID=").append(groupId).append("  AND SD.ACTIVE='Y' AND R.").append(column).append(" =").append(activityId);
		sb.append(" ) ");
		sb.append(" select  ");
		sb.append(" CASE WHEN IND.ID >0 THEN IND.ID else Q.ID  END AS FINANCE_MAP_ID, ");
		sb.append(" CASE WHEN IND.ID >0 THEN IND.KEY_CODE else Q.KEY_CODE  END AS KEY_CODE, ");
		sb.append(" CASE WHEN IND.ID >0 THEN IND.ACCOUNT_NUMBER else Q.ACCOUNT_NUMBER  END AS ACCOUNT_NUMBER, ");
		sb.append(" CASE WHEN IND.ID >0 THEN IND.BUDGET_UNIT else Q.BUDGET_UNIT  END AS BUDGET_UNIT, ");
		sb.append(" CASE WHEN IND.ID >0 THEN IND.FUND else Q.FUND  END AS FUND ");
		
		
		sb.append(" , SD.FEE_ID, SD.ID , F.NAME , F.DESCRIPTION , SD.FEE_DATE , SD.FEE_AMOUNT , SD.FEE_PAID , SD.BALANCE_DUE  ");  
		sb.append(" from STATEMENT_DETAIL SD   join FEE F on SD.FEE_ID=F.ID    ");
		sb.append(" left outer join ").append(table).append("  R on   SD.STATEMENT_ID  =R.STATEMENT_ID  ");  
		sb.append(" left outer join Q  on SD.FEE_ID=Q.FEE_ID  ");
		sb.append(" left outer join IND  on SD.FEE_ID=IND.FEE_ID ");
		sb.append(" where SD.ID is not null and SD.GROUP_ID=").append(groupId).append("  AND SD.ACTIVE='Y'  AND SD.BALANCE_DUE>0 AND R.").append(column).append("=").append(activityId);
		sb.append(" order by SD.ID   ");
		
		
		
		return sb.toString();
	}
	
	public static String getStatementDetails(int statementId,int groupId){
		StringBuilder sb = new StringBuilder();
		/*sb.append(" select  ");
		//sb.append(" DISTINCT FG.NAME AS GROUP_NAME , FG.ID AS GROUP_ID ,");
		sb.append(" SD.FEE_ID");
		sb.append(",");
		sb.append(" SD.ID ");
		sb.append(",");
		sb.append(" F.NAME ");
		sb.append(",");
		sb.append(" F.DESCRIPTION ");
		sb.append(",");
		sb.append(" SD.FEE_DATE ");
		sb.append(",");
		sb.append(" SD.FEE_AMOUNT ");
		sb.append(",");
		sb.append(" SD.FEE_PAID ");
		sb.append(",");
		sb.append(" SD.BALANCE_DUE ");
		
		sb.append(" from STATEMENT_DETAIL SD  ");
		sb.append(" join FEE F on SD.FEE_ID=F.ID ");
		//sb.append(" join FEE_GROUP FG on FG.FEE_ID=F.ID ");
		sb.append(" where SD.ID is not null and SD.GROUP_ID=").append(groupId).append(" AND SD.STATEMENT_ID=").append(statementId);
		
		sb.append(" order by SD.ID ");
		*/
		
		sb.append(" WITH Q AS (  ");
		sb.append(" select  DISTINCT M.*, SD.FEE_ID   ");
		sb.append(" from STATEMENT_DETAIL SD   join FEE F on SD.FEE_ID=F.ID    ");
//		sb.append(" left outer join ").append(table).append(" R on   SD.STATEMENT_ID  =R.STATEMENT_ID    ");
		sb.append(" left outer join REF_FEE_FORMULA RFF on SD.FEE_ID=RFF.FEE_ID   ");
		sb.append(" left outer join FINANCE_MAP M on RFF.FINANCE_MAP_ID =M.ID  ");
		sb.append(" where SD.ID is not null and RFF.EXPIRATION_DATE IS NULL and SD.GROUP_ID=").append(groupId).append(" AND SD.ACTIVE='Y' AND SD.STATEMENT_ID=").append(statementId);
		sb.append(" )  ");
		
		
		sb.append(" , IND AS ( ");
		sb.append(" 		select  DISTINCT M.*, SD.FEE_ID  ");
		sb.append(" from STATEMENT_DETAIL SD   join FEE F on SD.FEE_ID=F.ID   ");
		//sb.append(" left outer join ").append(table).append("  R on   SD.STATEMENT_ID  =R.STATEMENT_ID   ");
		sb.append(" left outer join FINANCE_MAP M on SD.FINANCE_MAP_ID =M.ID ");
		sb.append(" where SD.ID is not null and SD.GROUP_ID=").append(groupId).append(" AND  SD.ACTIVE='Y'  AND SD.STATEMENT_ID=").append(statementId);
		sb.append(" ) ");
		sb.append(" select  ");
		sb.append(" CASE WHEN IND.ID >0 THEN IND.ID else Q.ID  END AS FINANCE_MAP_ID, ");
		sb.append(" CASE WHEN IND.ID >0 THEN IND.KEY_CODE else Q.KEY_CODE  END AS KEY_CODE, ");
		sb.append(" CASE WHEN IND.ID >0 THEN IND.ACCOUNT_NUMBER else Q.ACCOUNT_NUMBER  END AS ACCOUNT_NUMBER, ");
		sb.append(" CASE WHEN IND.ID >0 THEN IND.BUDGET_UNIT else Q.BUDGET_UNIT  END AS BUDGET_UNIT, ");
		sb.append(" CASE WHEN IND.ID >0 THEN IND.FUND else Q.FUND  END AS FUND ");
		
		
		sb.append(" , SD.FEE_ID, SD.ID , F.NAME , F.DESCRIPTION , SD.FEE_DATE , SD.FEE_AMOUNT , SD.FEE_PAID , SD.BALANCE_DUE  ");  
		sb.append(" from STATEMENT_DETAIL SD   join FEE F on SD.FEE_ID=F.ID    ");
		//sb.append(" left outer join ").append(table).append("  R on   SD.STATEMENT_ID  =R.STATEMENT_ID  ");  
		sb.append(" left outer join Q  on SD.FEE_ID=Q.FEE_ID  ");
		sb.append(" left outer join IND  on SD.FEE_ID=IND.FEE_ID ");
		sb.append(" where SD.ID is not null and SD.GROUP_ID=").append(groupId).append(" AND  SD.ACTIVE='Y'  AND SD.STATEMENT_ID=").append(statementId);
		sb.append(" order by SD.ID   ");
		
		
		return sb.toString();
	}

	
	
	public static String insertPayment(PaymentVO p,int userId,String ip){
		StringBuilder sb = new StringBuilder();
		sb.append(" insert into PAYMENT (" );
		/*sb.append("PAYMENT_NU ");
		sb.append(",");
		*/
		sb.append( " LKUP_PAYMENT_METHOD_ID ");
		sb.append(",");
		sb.append("LKUP_PAYMENT_TRANSACTION_TYPE_ID ");
		sb.append(",");
		sb.append( "LKUP_PAYMENT_COUNTER_ID ");
		sb.append(",");
		sb.append("PAYEE_ID ");
		sb.append(",");
		sb.append("PAYEE_DETAILS ");
		sb.append(",");
		sb.append("PAYMENT_AMOUNT ");
		sb.append(",");
		sb.append("ACCOUNT_NO ");
		sb.append(",");
		sb.append("COMMENT ");
		sb.append(",");
		sb.append("ONLINE ");
		sb.append(",");
		sb.append("PAYMENT_DATE ");
		
		sb.append(",");
		sb.append("ONLINE_TRANS_ID ");
		sb.append(",");
		sb.append("REV_PAYMENT_ID ");
		sb.append(",");
		sb.append("REV_AMOUNT ");
		sb.append(",");
		sb.append("CREATED_BY ");
		sb.append(",");
		sb.append("UPDATED_BY ");
		sb.append(",");
		sb.append("CREATED_IP ");
		sb.append(",");
		sb.append("LOCKBOX_TRANSACTION_NO ");
		
		
		sb.append(",");
		sb.append("AUTO ");
		
		sb.append(") VALUES (");
		sb.append(p.getMethod());
		sb.append(",");
		sb.append(p.getTransactiontype());
		sb.append(",");
		sb.append(p.getCounter());
		sb.append(",");
		sb.append(p.getPayeeid());
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(p.getOtherpayeename())).append("'");
		sb.append(",");
		sb.append(p.getAmount());
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(p.getNumber())).append("'");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(p.getComment())).append("'");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(p.getOnline())).append("'");
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(p.getOnlinetranasactionnumber())).append("'");
		sb.append(",");
		sb.append(p.getRevpaymentid());
		sb.append(",");
		sb.append(p.getRevamount());
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(ip)).append("'");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(p.getCountername())).append("'");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(p.getAuto())).append("'");
		sb.append(" ) "); 
		
		return sb.toString();
	}
	
	
	public static String insertPaymentDetail(JSONObject f,String ip,int userId){
		StringBuilder sb = new StringBuilder();
		try{
		sb.append(" insert into PAYMENT_DETAIL (" );
		sb.append("PAYMENT_ID ");
		sb.append(",");
		sb.append("STATEMENT_DETAIL_ID ");
		sb.append(",");
		sb.append("AMOUNT ");
		/*sb.append(",");
		sb.append("FEE_ID ");*/
		if(f.has("revpaymentid")){
			sb.append(",");
			sb.append("REV_PAYMENT_ID ");
		}
		if(f.has("revtransid")){
			sb.append(",");
			sb.append("REV_TRANSACTION_ID ");
		}
		sb.append(",");
		sb.append("REV_AMOUNT ");
		sb.append(",");
		sb.append("CREATED_BY ");
		sb.append(",");
		sb.append("UPDATED_BY ");
		sb.append(",");
		sb.append("CREATED_IP ");
		
		if(f.has("financemapid")){
			sb.append(",");
			sb.append("FINANCE_MAP_ID ");
			sb.append(",");
			sb.append("KEY_CODE ");
			sb.append(",");
			sb.append("BUDGET_UNIT ");
			sb.append(",");
			sb.append("ACCOUNT_NUMBER ");
			sb.append(",");
			sb.append("FUND ");	
		}
		
		sb.append(") VALUES (");
		sb.append(f.getInt("paymentid"));
		sb.append(",");
		sb.append(f.getInt("statementdetailid"));
		sb.append(",");
		sb.append(f.getDouble("amount"));
		/*sb.append(",");
		sb.append(f.getInt("feeid"));*/
		
		if(f.has("revpaymentid")){
			sb.append(",");
			sb.append(f.getInt("revpaymentid"));
		}
		if(f.has("revtransid")){
			sb.append(",");
			sb.append(f.getInt("revtransid"));
		}
		sb.append(",");
		sb.append(f.getDouble("amount"));
		
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(ip)).append("'");
		
		
		if(f.has("financemapid")){
			sb.append(",");
			sb.append(f.getInt("financemapid"));
			sb.append(",");
			sb.append("'").append(Operator.sqlEscape(f.getString("keycode"))).append("'");
			sb.append(",");
			sb.append("'").append(Operator.sqlEscape(f.getString("budgetunit"))).append("'");
			sb.append(",");
			sb.append("'").append(Operator.sqlEscape(f.getString("accountnumber"))).append("'");
			sb.append(",");
			sb.append("'").append(Operator.sqlEscape(f.getString("fund"))).append("'");
		}
		
		
		
		sb.append(" ) "); 
		} catch(Exception e){
			e.printStackTrace();
			sb = new StringBuilder();
		}
		return sb.toString();
	}
	
	public static String getPaymentId(String ip,int userId){
		StringBuilder sb = new StringBuilder();
		sb.append( "select TOP 1 * from PAYMENT WHERE ID>0 ");
		sb.append(" AND ");
		sb.append(" CREATED_IP= '").append(Operator.sqlEscape(ip)).append("'");
		sb.append(" AND ");
		sb.append(" CREATED_BY= ").append(userId);
		sb.append(" ORDER BY ID DESC ");
		return sb.toString();
	}
	
	
	public static String insertRefPayment(String type,int typeId,int paymentId,int userId){
		String ref = CsReflect.getTableRef(type);
		String fieldId = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		
		sb.append( "insert into REF_").append(ref).append("_PAYMENT (").append(fieldId).append(",PAYMENT_ID,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)");
		sb.append(" VALUES (");
		sb.append(typeId);
		sb.append(",");
		sb.append(paymentId);
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(" ) "); 
		return sb.toString();
	}
	
	
	public static String getActivityPaymentStatement(int activityId,int paymentId){
		StringBuilder sb = new StringBuilder();
		sb.append(" select  ");
		sb.append(" DISTINCT FG.GROUP_NAME , FG.ID AS GROUP_ID , sum(SD.FEE_AMOUNT) as FEE_AMOUNT,sum(SD.FEE_PAID) as FEE_PAID,sum(SD.BALANCE_DUE) as BALANCE_DUE ");
		sb.append(" from STATEMENT_DETAIL SD  ");
		sb.append(" left outer join FEE_GROUP FG on SD.GROUP_ID=FG.ID ");
		sb.append(" left outer join STATEMENT S on  SD.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");
		sb.append(" left outer join REF_ACT_STATEMENT R on  S.ID =R.STATEMENT_ID  ");
		sb.append(" join REF_ACT_PAYMENT RAP on R.ACTIVITY_ID=RAP.ACTIVITY_ID ");
		sb.append(" join FEE F on SD.FEE_ID=F.ID ");
		sb.append(" where  SD.ID is not null and SD.ACTIVE ='Y'  ");
		sb.append(" AND R.ACTIVITY_ID=").append(activityId);
		sb.append(" AND RAP.PAYMENT_ID=").append(paymentId);
		sb.append(" group by SD.GROUP_ID,FG.ID,FG.GROUP_NAME ");
		sb.append(" order by FG.ID ");
		return sb.toString();
	}
	
	public static String getActivityPaymentStatementsDetails(int paymentId,int groupId){
		StringBuilder sb = new StringBuilder();
		sb.append(" select  ");
		//sb.append(" DISTINCT FG.NAME AS GROUP_NAME , FG.ID AS GROUP_ID ,");
		sb.append(" SD.FEE_ID");
		sb.append(",");
		sb.append(" SD.ID ");
		sb.append(",");
		sb.append(" PD.ID as PAYMENT_DETAIL_ID");
		sb.append(",");
		sb.append(" F.NAME ");
		sb.append(",");
		sb.append(" F.DESCRIPTION ");
		sb.append(",");
		sb.append(" SD.FEE_DATE ");
		sb.append(",");
		sb.append(" PD.AMOUNT ");
		/*sb.append(",");
		sb.append(" SD.FEE_PAID ");
		sb.append(",");
		sb.append(" SD.BALANCE_DUE ");*/
		
		sb.append(" from STATEMENT_DETAIL SD  ");
		sb.append(" join FEE F on SD.FEE_ID=F.ID ");
		sb.append(" left outer join REF_ACT_STATEMENT R on   SD.STATEMENT_ID  =R.STATEMENT_ID  ");
		sb.append(" join PAYMENT_DETAIL PD on   SD.ID  =PD.STATEMENT_DETAIL_ID  ");
		sb.append(" where SD.ID is not null and SD.GROUP_ID=").append(groupId).append(" AND PD.PAYMENT_ID=").append(paymentId);
		
		sb.append(" order by SD.ID ");
		
		return sb.toString();
	}
	
	
	public static String getFeeId(RequestVO r,int feeId){
		if (!Operator.hasValue(r.getType())) { return ""; }
		String tableref = CsReflect.getTableRef(r.getType());
		String idref = CsReflect.getFieldIdRef(r.getType());
		String maintableref = CsReflect.getMainTableRef(r.getType());
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select SUM(FEE_AMOUNT) as FEE_AMOUNT FROM STATEMENT_DETAIL SD ");
		sb.append(" JOIN STATEMENT S on SD.STATEMENT_ID = S.ID ");
		sb.append(" left outer join REF_").append(tableref).append("_STATEMENT RAS on S.ID = RAS.STATEMENT_ID ");
		sb.append(" join FEE F  on SD.FEE_ID = F.ID ");
		sb.append(" where RAS.").append(idref).append("= ").append(r.getTypeid()).append(" AND F.ID= ").append(feeId).append("");
		
		
		return sb.toString();
	}
	
	public static String getGroupName(RequestVO r,String groupname){
		if (!Operator.hasValue(r.getType())) { return ""; }
		String tableref = CsReflect.getTableRef(r.getType());
		String idref = CsReflect.getFieldIdRef(r.getType());
		String maintableref = CsReflect.getMainTableRef(r.getType());
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select SUM(SD.FEE_AMOUNT) as FEE_AMOUNT FROM STATEMENT_DETAIL SD ");
		sb.append(" JOIN STATEMENT S on SD.STATEMENT_ID = S.ID ");
		sb.append(" left outer join REF_").append(tableref).append("_STATEMENT RAS on S.ID = RAS.STATEMENT_ID ");
		sb.append(" join FEE_GROUP G  on SD.GROUP_ID = G.ID ");
		sb.append(" where  SD.ACTIVE ='Y' and RAS.").append(idref).append("= ").append(r.getTypeid()).append(" AND G.NAME= '").append(Operator.sqlEscape(groupname)).append("'");
		
		return sb.toString();
	}
	
	
	public static String getFeeName(RequestVO r,String feename){
		if (!Operator.hasValue(r.getType())) { return ""; }
		String tableref = CsReflect.getTableRef(r.getType());
		String idref = CsReflect.getFieldIdRef(r.getType());
		String maintableref = CsReflect.getMainTableRef(r.getType());
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select CASE WHEN SUM(SD.FEE_AMOUNT) is null then 0 else SUM(SD.FEE_AMOUNT) END as FEE_AMOUNT FROM STATEMENT_DETAIL SD ");
		sb.append(" JOIN STATEMENT S on SD.STATEMENT_ID = S.ID ");
		sb.append(" left outer join REF_").append(tableref).append("_STATEMENT RAS on S.ID = RAS.STATEMENT_ID ");
		sb.append(" join FEE F  on SD.FEE_ID = F.ID ");
		sb.append(" where RAS.").append(idref).append("= ").append(r.getTypeid()).append(" AND SD.ACTIVE='Y' AND F.NAME= '").append(Operator.sqlEscape(feename)).append("'");
		
		
		return sb.toString();
	}
	
	public static String getFeeNameAll(RequestVO r,String feename){
		if (!Operator.hasValue(r.getType())) { return ""; }
		String tableref = CsReflect.getTableRef(r.getType());
		String idref = CsReflect.getFieldIdRef(r.getType());
		String maintableref = CsReflect.getMainTableRef(r.getType());
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select CASE WHEN SUM(SD.FEE_AMOUNT) is null then 0 else SUM(SD.FEE_AMOUNT) END as FEE_AMOUNT,F.NAME FROM STATEMENT_DETAIL SD ");
		sb.append(" JOIN STATEMENT S on SD.STATEMENT_ID = S.ID ");
		sb.append(" left outer join REF_").append(tableref).append("_STATEMENT RAS on S.ID = RAS.STATEMENT_ID ");
		sb.append(" join FEE F  on SD.FEE_ID = F.ID ");
		sb.append(" where RAS.").append(idref).append("= ").append(r.getTypeid()).append(" AND SD.ACTIVE='Y' AND F.NAME IN  (").append(feename).append(") group by F.NAME ");
		
		
		return sb.toString();
	}
	
	public static String getPaymentsByUser(int userId){
		StringBuilder sb = new StringBuilder();
		sb.append(" select  P.*, M.METHOD_TYPE, ");
		
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
		
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10), P.PAYMENT_DATE, 101) + ' ' + RIGHT(CONVERT(CHAR(20), PAYMENT_DATE, 22), 11) as PAYMENTDATE ");
		sb.append(",");
		sb.append(" T.TYPE AS TRANSACTIONTYPE ");
		
		sb.append(" from payment p ");
		sb.append(" join LKUP_PAYMENT_METHOD M on P.LKUP_PAYMENT_METHOD_ID= M.ID  ");
		sb.append("   left outer join LKUP_PAYMENT_TRANSACTION_TYPE T on P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID  ");
		//sb.append(" left outer join REF_USERS R ON P.PAYEE_ID = R.ID ");
		sb.append(" left outer join USERS AU ON P.PAYEE_ID = AU.ID ");
		sb.append(" where P.CREATED_BY=").append(userId).append(" AND CAST(PAYMENT_DATE AS DATE) = CAST(GETDATE() AS DATE) order by P.ID desc ");
		return sb.toString();
	}
	
	public static String getPayment(int paymentId){
		StringBuilder sb = new StringBuilder();
		
		sb.append(" WITH Q AS ( ");
		sb.append(" select ").append(paymentId).append(" as QPID,SUM(AMOUNT) AS REV_AMOUNT from PAYMENT_DETAIL  where (PAYMENT_ID=").append(paymentId).append(" OR REV_PAYMENT_ID=").append(paymentId).append(") AND STATEMENT_DETAIL_ID>0 ");
		sb.append(" ) ");
		sb.append(" select Q.REV_AMOUNT , P.*, M.METHOD_TYPE, ");
	
		
		
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
		
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10), P.PAYMENT_DATE, 101) + ' ' + RIGHT(CONVERT(CHAR(20), PAYMENT_DATE, 22), 11) as PAYMENTDATE ");
		sb.append(",");
		sb.append(" T.TYPE AS TRANSACTIONTYPE ");
		
		sb.append(" from payment p ");
		sb.append(" left outer join LKUP_PAYMENT_METHOD M on P.LKUP_PAYMENT_METHOD_ID= M.ID  ");
		sb.append("   left outer join LKUP_PAYMENT_TRANSACTION_TYPE T on P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID  ");
		sb.append("  left outer join Q on P.ID = Q.QPID ");
		sb.append(" left outer join USERS AU ON P.PAYEE_ID = AU.ID ");
		sb.append(" where P.ID=").append(paymentId).append(" ");
		return sb.toString();
	}
	
	
	public static String getDepositBalance(int userId){
		StringBuilder sb = new StringBuilder();
		sb.append(" select  AU.ID as USERS_ID, ");
		
		
		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN AU.FIRST_NAME IS NOT NULL AND AU.FIRST_NAME <> '' THEN AU.FIRST_NAME ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
	/*	sb.append("       CASE  ");
		sb.append("         WHEN AU.MI IS NOT NULL AND AU.MI <> '' THEN ' ' + AU.MI ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");*/
		sb.append("       CASE  ");
		sb.append("         WHEN AU.LAST_NAME IS NOT NULL AND AU.LAST_NAME <> '' THEN ' ' + AU.LAST_NAME ");
		sb.append("         ELSE '' ");
		sb.append("       END + ");
		sb.append("       CASE  ");
		sb.append("         WHEN AU.EMAIL IS NOT NULL AND AU.EMAIL <> '' THEN ' (' + AU.EMAIL + ') ' ");
		sb.append("         ELSE '' ");
		sb.append("       END ");
		sb.append("     )) AS TEXT ");
		
		sb.append(",");
		sb.append(" SUM(D.CURRENT_AMOUNT) as AMOUNT ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10), getdate(), 101) + ' ' + RIGHT(CONVERT(CHAR(20), getdate(), 22), 11) as PAYMENTDATE ");
	/*	sb.append(",");
		sb.append(" T.TYPE AS TRANSACTIONTYPE ");*/
		
		sb.append(" from USERS AU ");
		sb.append(" join REF_USERS_DEPOSIT RUD on AU.ID= RUD.USERS_ID  ");
		sb.append(" join DEPOSIT D on RUD.DEPOSIT_ID= D.ID  ");
		sb.append(" where AU.ID=").append(userId).append(" AND PARENT_ID=0 GROUP BY AU.ID,AU.FIRST_NAME,AU.LAST_NAME,AU.EMAIL ");
		return sb.toString();
	}
	
	
	
	public static String getPayments(RequestVO r){
		if (!Operator.hasValue(r.getType())) { return ""; }
		String tableref = CsReflect.getTableRef(r.getType());
		String idref = CsReflect.getFieldIdRef(r.getType());
		String maintableref = CsReflect.getMainTableRef(r.getType());
		
		StringBuilder sb = new StringBuilder();
		
		
		sb.append(" select  P.*, M.METHOD_TYPE, ");
		
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
		
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10), P.PAYMENT_DATE, 101) + ' ' + RIGHT(CONVERT(CHAR(20), PAYMENT_DATE, 22), 11) as PAYMENTDATE ");
		sb.append(",");
		sb.append(" T.TYPE AS TRANSACTIONTYPE ");
		
		sb.append(" from payment p ");
		sb.append(" join LKUP_PAYMENT_METHOD M on P.LKUP_PAYMENT_METHOD_ID= M.ID  ");
		sb.append("   left outer join LKUP_PAYMENT_TRANSACTION_TYPE T on P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID  ");
	//	sb.append(" left outer join REF_USERS R ON P.PAYEE_ID = R.ID ");
		sb.append(" left outer join USERS AU ON P.PAYEE_ID = AU.ID ");
		
		sb.append(" left outer join REF_").append(tableref).append("_PAYMENT RAP ON P.ID = RAP.PAYMENT_ID ");
		
		
		sb.append(" where RAP.").append(idref).append("=").append(r.getTypeid()).append(" ");
		sb.append(" order by P.PAYMENT_DATE");
		return sb.toString();
	}
	
	
	public static String getPaymentStatementDetail(int sdid){
		StringBuilder sb = new StringBuilder();
		
		/*sb.append(" WITH Q AS ( ");
		sb.append(" select SUM(PD.AMOUNT) AS REV_AMOUNT,  PD.STATEMENT_DETAIL_ID from PAYMENT_DETAIL PD   WHERE PD.STATEMENT_DETAIL_ID=").append(sdid).append("  group by STATEMENT_DETAIL_ID ");
		sb.append(" ) ");
		
		sb.append(" select PD.ID,PD.AMOUNT,CASE WHEN PD.REV_PAYMENT_ID>0 THEN 0 ELSE Q.REV_AMOUNT END AS REV_AMOUNT,M.METHOD_TYPE as METHOD,P.ID as PAYMENT_ID,P.PAYEE_ID,   ");
		
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
		
		sb.append(",");
		sb.append("  CONVERT(VARCHAR(10), P.PAYMENT_DATE, 101) + ' ' + RIGHT(CONVERT(CHAR(20), PAYMENT_DATE, 22), 11) as PAYMENTDATE  ");
		sb.append(",");
		sb.append(" T.TYPE AS TRANSACTIONTYPE ");
		
		sb.append(" from PAYMENT_DETAIL PD ");
		sb.append(" join PAYMENT P on PD.PAYMENT_ID=P.ID   ");
		sb.append("    left outer join Q on PD.STATEMENT_DETAIL_ID = Q.STATEMENT_DETAIL_ID   ");
		sb.append("   left outer join LKUP_PAYMENT_METHOD M on P.LKUP_PAYMENT_METHOD_ID= M.ID  ");
		sb.append("   left outer join LKUP_PAYMENT_TRANSACTION_TYPE T on P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID  ");
		//sb.append("  left outer join REF_USERS R ON P.PAYEE_ID = R.ID ");
		sb.append("  left outer join USERS AU ON P.PAYEE_ID = AU.ID ");
		sb.append(" where PD.STATEMENT_DETAIL_ID=").append(sdid).append(" order by P.PAYMENT_DATE  ");*/
		
		
		 sb.append(" WITH Q AS (  ");
		 sb.append(" SELECT ");
		 sb.append("	STATEMENT_DETAIL_ID, ");
		 sb.append("	CASE ");
		 sb.append("		WHEN COALESCE(REV_PAYMENT_ID, 0) > 0 THEN REV_PAYMENT_ID ");
		 sb.append("		ELSE PAYMENT_ID ");
		 sb.append("	END AS PAYMENT_GROUP_ID, ");
		 sb.append("	AMOUNT ");
		 sb.append("FROM ");
		 sb.append("	PAYMENT_DETAIL AS D ");
		 sb.append(") ");
		 sb.append(" , ");
		 sb.append(" PDM AS ( ");
		 sb.append("SELECT ");
		 sb.append("	STATEMENT_DETAIL_ID, ");
		 sb.append("	PAYMENT_GROUP_ID, ");
		 sb.append("	SUM(AMOUNT) as REV_AMOUNT ");
		 sb.append("FROM ");
		 sb.append("	Q ");
		 sb.append(" ");
		 sb.append(" WHERE ");
		 sb.append("	STATEMENT_DETAIL_ID = ").append(sdid).append(" ");
		 sb.append("GROUP BY ");
		 sb.append("	STATEMENT_DETAIL_ID, ");
		 sb.append("	PAYMENT_GROUP_ID ");
		 sb.append("  ");
		 sb.append("  )   ");
		 sb.append("select  ");
		 sb.append("PD.ID, ");
		 sb.append("PD.AMOUNT, ");
		 sb.append("CASE WHEN PD.REV_PAYMENT_ID>0 THEN 0 ELSE PDM.REV_AMOUNT END AS REV_AMOUNT, ");
		 sb.append("M.METHOD_TYPE as METHOD, ");
		 sb.append("P.ID as PAYMENT_ID, ");
		 sb.append("P.PAYEE_ID,        ");
		 sb.append("LTRIM(RTRIM(        CASE           WHEN AU.FIRST_NAME IS NOT NULL AND AU.FIRST_NAME <> '' THEN AU.FIRST_NAME          ELSE ''         END +         CASE           WHEN AU.MI IS NOT NULL AND AU.MI <> '' THEN ' ' + AU.MI          ELSE ''         END +         CASE           WHEN AU.LAST_NAME IS NOT NULL AND AU.LAST_NAME <> '' THEN ' ' + AU.LAST_NAME          ELSE ''        END +        CASE           WHEN AU.EMAIL IS NOT NULL AND AU.EMAIL <> '' THEN ' (' + AU.EMAIL + ') '          ELSE ''        END      )) AS TEXT ,   ");
		 sb.append("CONVERT(VARCHAR(10), P.PAYMENT_DATE, 101) + ' ' + RIGHT(CONVERT(CHAR(20), PAYMENT_DATE, 22), 11) as PAYMENTDATE  ,  ");
		 sb.append("T.TYPE AS TRANSACTIONTYPE   ");
		 sb.append("from PAYMENT_DETAIL PD   ");
		 sb.append("join PAYMENT P on PD.PAYMENT_ID=P.ID       ");
		 sb.append("left outer join PDM on PD.STATEMENT_DETAIL_ID = PDM.STATEMENT_DETAIL_ID AND PD.PAYMENT_ID=PDM.PAYMENT_GROUP_ID      ");
		 sb.append("left outer join LKUP_PAYMENT_METHOD M on P.LKUP_PAYMENT_METHOD_ID= M.ID     ");
		 sb.append("left outer join LKUP_PAYMENT_TRANSACTION_TYPE T on P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID     ");
		 sb.append("left outer join USERS AU ON P.PAYEE_ID = AU.ID  ");
		 sb.append("where PD.STATEMENT_DETAIL_ID=").append(sdid).append("  ");
		 sb.append("order by P.PAYMENT_DATE ");
		
		
		return sb.toString();
	}
	
	
	
	
	public static String getMethodDetail(int methodid){
		StringBuilder sb = new StringBuilder();
		sb.append(" select * from LKUP_PAYMENT_METHOD WHERE ACTIVE='Y' and ID= ").append(methodid);
		return sb.toString();
	}
	
	
	public static String getInitSub(RequestVO r,int groupId){
		if (!Operator.hasValue(r.getType())) { return ""; }
		String tableref = CsReflect.getTableRef(r.getType());
		String idref = CsReflect.getFieldIdRef(r.getType());
		String maintableref = CsReflect.getMainTableRef(r.getType());
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select SD.FEE_AMOUNT FROM STATEMENT_DETAIL SD ");
		sb.append(" JOIN STATEMENT S on SD.STATEMENT_ID = S.ID ");
		sb.append(" left outer join REF_").append(tableref).append("_STATEMENT RAS on S.ID = RAS.STATEMENT_ID ");
		sb.append(" where RAS.").append(idref).append("= ").append(r.getTypeid()).append(" AND SD.GROUP_ID= ").append(groupId);
		sb.append(" ORDER BY SD.ID ");
		return sb.toString();
	}
	
	
	public static String getPaymentDetails(int paymentId){
		StringBuilder sb = new StringBuilder();
		sb.append(" select DISTINCT ");
		//sb.append(" DISTINCT FG.NAME AS GROUP_NAME , FG.ID AS GROUP_ID ,");
		sb.append(" SD.FEE_ID");
		sb.append(",");
		sb.append(" SD.ID ");
		sb.append(",");
		sb.append(" PD.ID as PAYMENT_DETAIL_ID");
		sb.append(",");
		sb.append(" F.NAME ");
		sb.append(",");
		sb.append(" F.DESCRIPTION ");
		sb.append(",");
		sb.append(" SD.FEE_DATE ");
		sb.append(",");
		sb.append(" PD.AMOUNT ");
		sb.append(",");
		sb.append(" PD.ACCOUNT_NUMBER ");
		/*sb.append(",");
		sb.append(" SD.FEE_PAID ");
		sb.append(",");
		sb.append(" SD.BALANCE_DUE ");*/
		sb.append(",");
		sb.append(" A.ACT_NBR ");
		sb.append(",");
		sb.append(" LAT.TYPE ");
		sb.append(",");
		sb.append(" FG.GROUP_NAME ");
		
		sb.append(" from PAYMENT_DETAIL PD   ");
		
		
		sb.append(" left outer  join STATEMENT_DETAIL SD on   PD.STATEMENT_DETAIL_ID=SD.ID  ");
		sb.append(" left outer join FEE F on SD.FEE_ID=F.ID ");
		sb.append(" left outer join FEE_GROUP FG on SD.GROUP_ID=FG.ID ");
		sb.append(" left outer join REF_ACT_STATEMENT RAS on SD.STATEMENT_ID=RAS.STATEMENT_ID ");
		sb.append(" left outer join ACTIVITY A on RAS.ACTIVITY_ID=A.ID ");
		sb.append(" left outer join LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID ");
		sb.append(" where PD.PAYMENT_ID=").append(paymentId);
		sb.append(" AND SD.ID>0  ");
		sb.append(" union  ");
		sb.append("select DISTINCT   ");
		sb.append("0, 0 , PD.ID as PAYMENT_DETAIL_ID, 'DEPOSIT' , 'DEPOSIT FEE' , null , PD.AMOUNT , PD.ACCOUNT_NUMBER , '' , '' , ''   ");
		sb.append("from PAYMENT_DETAIL PD     ");
		sb.append(" where PD.PAYMENT_ID=").append(paymentId).append(" AND COALESCE(PD.STATEMENT_DETAIL_ID,0)<=0  ");
		
		
		sb.append(" order by SD.ID ");
		
		return sb.toString();
	}
	
	
	public static String insertCart(String cart,int userId, double amount){
		StringBuilder sb = new StringBuilder();
		sb.append( "insert into PAYMENT_ONLINE_PREVIEW (CART,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,PAYMENT_AMOUNT)");
		sb.append(" VALUES (");
		sb.append("'").append(Operator.sqlEscape(cart)).append("'");
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(",");
		sb.append(amount);
		sb.append(" ) "); 
		return sb.toString();
	}	
	
	public static String getCart(int cartId,int userId){
		StringBuilder sb = new StringBuilder();
		if(cartId>0){
			sb.append( "select * from  PAYMENT_ONLINE_PREVIEW WHERE  ");
			sb.append(" ACTIVE='Y' AND PAID='N' AND ID = ").append(cartId);
		}else {
			sb.append( "select TOP 1 * from  PAYMENT_ONLINE_PREVIEW WHERE  ");
			sb.append(" ACTIVE='Y' AND PAID='N' AND   CREATED_BY = ").append(userId).append(" ORDER BY ID DESC ");
		}
		return sb.toString();
	}
	
	public static String deleteCart(int cartId){
		StringBuilder sb = new StringBuilder();
		if(cartId>0){
			sb.append( "UPDATE PAYMENT_ONLINE_PREVIEW SET ACTIVE='N' WHERE  ");
			sb.append(" ID = ").append(cartId);
		}/*else {
			sb.append( "select TOP 1 * from  PAYMENT_ONLINE_PREVIEW WHERE  ");
			sb.append(" ACTIVE='Y' AND PAID='N' AND   CREATED_BY = ").append(userId).append(" ORDER BY ID DESC ");
		}*/
		return sb.toString();
	}
	
	public static String updateCart(int userId,String active,String paid){
		StringBuilder sb = new StringBuilder();
		sb.append( "UPDATE PAYMENT_ONLINE_PREVIEW SET UPDATED_DATE = CURRENT_TIMESTAMP   ");
		sb.append( " ,ACTIVE = '").append(Operator.sqlEscape(active)).append("'");
		sb.append( " ,PAID = '").append(Operator.sqlEscape(paid)).append("'");	
		sb.append(" WHERE CREATED_BY = ").append(userId);
		return sb.toString();
	}
	
	public static String updateCart(int cartId,String active,String paid,String cart,double amount){
		StringBuilder sb = new StringBuilder();
		sb.append( "UPDATE PAYMENT_ONLINE_PREVIEW SET UPDATED_DATE = CURRENT_TIMESTAMP   ");
		sb.append( " ,ACTIVE = '").append(Operator.sqlEscape(active)).append("', CART = ");
		sb.append("'").append(Operator.sqlEscape(cart)).append("' ");
		sb.append( " ,PAID = '").append(Operator.sqlEscape(paid)).append("'");	
		sb.append( " ,PAYMENT_AMOUNT = '").append(amount).append("'");	
		sb.append(" WHERE ID = ").append(cartId);
		return sb.toString();
	}
	
	public static String deletefee(int stId, int userId){
		StringBuilder sb = new StringBuilder();
		sb.append( "UPDATE STATEMENT_DETAIL SET ACTIVE='N', UPDATED_DATE= CURRENT_TIMESTAMP, UPDATED_BY = ").append(userId).append(" WHERE  ");
		sb.append(" ID = ").append(stId);
		return sb.toString();
	}
	
	public static String updateCartByCartId(int cartId,int userid, String active,String paid, String message){
		StringBuilder sb = new StringBuilder();
		sb.append( "UPDATE PAYMENT_ONLINE_PREVIEW SET UPDATED_DATE = CURRENT_TIMESTAMP   ");
		sb.append( " ,ACTIVE = '").append(Operator.sqlEscape(active)).append("'");
		sb.append( " ,PAID = '").append(Operator.sqlEscape(paid)).append("'");
		sb.append( " ,UPDATED_BY = ").append(userid);
		sb.append( " WHERE ID = ").append(cartId);
		return sb.toString();
	}
	
	public static String insertPaymentResult(int cartId, String transid, int userid, String message){
		StringBuilder sb = new StringBuilder();
		sb.append( "INSERT INTO REF_PAYMENT_ONLINE_PREVIEW_RESULT (PAYMENT_ONLINE_PREVIEW_ID, TRANSACTION_ID, RESULT, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE) VALUES (");
		sb.append(cartId).append(", ");
		sb.append(transid).append(", '");
		sb.append(Operator.sqlEscape(message)).append("',");
		sb.append(userid).append(",");
		sb.append(" CURRENT_TIMESTAMP ").append(",");
		sb.append(userid) .append(",");
		sb.append(" CURRENT_TIMESTAMP )");
		return sb.toString();
	}
	
	public static String getInvoiceFinanceLock(String type, int typeid){
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select S.FINANCE_LOCK,UP.USERNAME FROM ");
		sb.append("").append(maintableref).append(" A  ");
		sb.append(" left outer join REF_").append(tableref).append("_STATEMENT RAS on A.ID=RAS.").append(idref).append(" ");
		sb.append(" left outer join STATEMENT S on  RAS.STATEMENT_ID =S.ID  ");
		sb.append("  LEFT OUTER JOIN USERS UP on S.UPDATED_BY = UP.ID 	  ");
		sb.append(" where A.ID = ").append(typeid).append("  and S.ACTIVE='Y' AND S.FINANCE_LOCK ='Y' ");
		
		
		return sb.toString();
	}
	
	public static String getTypeFinanceLock(String type, int typeid){
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select RAS.FINANCE_LOCK FROM ");
		sb.append("").append(maintableref).append(" A  ");
		sb.append(" left outer join LKUP_").append(tableref).append("_TYPE RAS on A.LKUP_").append(tableref).append("_TYPE_ID =RAS.ID").append("").append(" ");
		sb.append(" where A.ID = ").append(typeid).append("  ");
		
		
		return sb.toString();
	}
	
	
	public static String updateInvoiceFinanceLock(String type, int typeid,int userId,boolean lock){
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		
		StringBuilder sb = new StringBuilder();
		sb.append( "UPDATE STATEMENT SET UPDATED_DATE = CURRENT_TIMESTAMP   ");
		if(lock){
			sb.append(" ,FINANCE_LOCK ='Y' ");
		}else {
			sb.append(" ,FINANCE_LOCK ='N' ");
		}
		sb.append( " where ID in (select STATEMENT_ID FROM REF_").append(tableref).append("_STATEMENT WHERE ").append(idref).append(" = ").append(typeid).append(") ");
	
		return sb.toString();
	}
	
	public static String print(String type, int typeid){
		return summary(type, typeid, 0);
	}
	
	
	public static String printListFee(String type, int typeid){
		StringBuilder sb = new StringBuilder();
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		
		sb.append(" select FG.GROUP_NAME,F.NAME as FEE_NAME, SD.* from ").append(maintableref).append(" A  ");
		sb.append(" JOIN REF_").append(tableref).append("_STATEMENT RAS on A.ID= RAS.").append(idref).append("  ");
		sb.append(" JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y'  ");
		sb.append(" JOIN FEE_GROUP FG on SD.GROUP_ID=FG.ID  ");
		sb.append(" JOIN FEE F on SD.FEE_ID= F.ID  ");
		sb.append(" WHERE A.ID = ").append(typeid).append(" ");
		return sb.toString();
	}
	
	
	public static String getManualAccounts(String type, int typeid){
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select F.NAME,FM.* FROM ");
		sb.append("").append(maintableref).append(" A  ");
		sb.append(" left outer join REF_").append(tableref).append("_STATEMENT RAS on A.ID=RAS.").append(idref).append(" ");
		sb.append(" left outer join STATEMENT_DETAIL S on  RAS.STATEMENT_ID =S.STATEMENT_ID  ");
		sb.append(" left outer join REF_FEE_FORMULA RFF on  S.REF_FEE_FORMULA_ID =RFF.ID AND RFF.MANUAL_ACCOUNT='N' ");
		sb.append(" left outer join FINANCE_MAP FM on  RFF.FINANCE_MAP_ID =FM.ID ");
		sb.append(" left outer join FEE F on  S.FEE_ID =F.ID ");
		sb.append(" where A.ID = ").append(typeid).append(" AND FM.ID is not null  and S.ACTIVE='Y'  ");
		
		
		return sb.toString();
	}

	public static String projectCart(int projectid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append("   P.ID, ");
		sb.append("   P.NAME, ");
		sb.append("   P.ID as PROJECT_ID, ");
		sb.append("   A.ID as ACTIVITY_ID, ");
		sb.append("   ACT_NBR, ");
		sb.append("   'A' as TYPE, ");
		sb.append("   SUM(SD.FEE_AMOUNT) as FEE_AMOUNT, ");
		sb.append("   SUM(SD.FEE_PAID) as FEE_PAID, ");
		sb.append("   SUM(SD.BALANCE_DUE) as BALANCE_DUE ");
		sb.append(" FROM ");
		sb.append("   REF_ACT_STATEMENT R ");  
		sb.append("   JOIN ACTIVITY A on R.ACTIVITY_ID = A.ID ");
		sb.append("   JOIN PROJECT P on A.PROJECT_ID = P.ID ");
		sb.append("   JOIN STATEMENT S on R.STATEMENT_ID = S.ID ");
		sb.append(" join STATEMENT_DETAIL  SD on R.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y'  AND SD.BALANCE_DUE>0 ");
		sb.append(" WHERE ");
		sb.append("   S.ID is not null ");
		sb.append("   AND ");
		sb.append("   P.ID = ").append(projectid);
		sb.append(" GROUP BY ");
		sb.append("   P.ID, ");
		sb.append("   P.NAME, ");
		sb.append("   A.ID, ");
		sb.append("   ACT_NBR");
		sb.append(" UNION ");
		sb.append(" SELECT ");
		sb.append("   P.ID, ");
		sb.append("   P.NAME, ");
		sb.append("   P.ID as PROJECT_ID, ");
		sb.append("   P.ID as ACTIVITY_ID, ");
		sb.append("   P.PROJECT_NBR as ACT_NBR, ");
		sb.append("   'P' as TYPE, ");
		sb.append("   SUM(SD.FEE_AMOUNT) as FEE_AMOUNT, ");
		sb.append("   SUM(SD.FEE_PAID) as FEE_PAID, ");
		sb.append("   SUM(SD.BALANCE_DUE) as BALANCE_DUE  ");
		sb.append(" FROM ");
		sb.append("   REF_PROJECT_STATEMENT R ");  
		sb.append("   JOIN PROJECT P on R.PROJECT_ID = P.ID ");
		sb.append("   JOIN STATEMENT S on R.STATEMENT_ID = S.ID ");
		sb.append(" join STATEMENT_DETAIL  SD on R.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y' AND SD.BALANCE_DUE>0 ");
		sb.append(" WHERE ");
		sb.append("   S.ID is not null ");
		sb.append("   AND ");
		sb.append("   S.FINANCE_LOCK='N' ");
		sb.append("   AND ");
		sb.append("   P.ID = ").append(projectid);
		sb.append(" GROUP BY P.ID,P.NAME,PROJECT_NBR");
		return sb.toString();
	}

	public static String projectCart(String projectnbr) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append("   P.ID, ");
		sb.append("   P.NAME, ");
		sb.append("   P.ID as PROJECT_ID, ");
		sb.append("   A.ID as ACTIVITY_ID, ");
		sb.append("   ACT_NBR, ");
		sb.append("   'A' as TYPE, ");
		sb.append("   SUM(SD.FEE_AMOUNT) as FEE_AMOUNT, ");
		sb.append("   SUM(SD.FEE_PAID) as FEE_PAID, ");
		sb.append("   SUM(SD.BALANCE_DUE) as BALANCE_DUE ");
		sb.append(" FROM ");
		sb.append("   REF_ACT_STATEMENT R ");  
		sb.append("   JOIN ACTIVITY A on R.ACTIVITY_ID = A.ID ");
		sb.append("   JOIN PROJECT P on A.PROJECT_ID = P.ID ");
		sb.append("   JOIN STATEMENT S on R.STATEMENT_ID = S.ID ");
		sb.append(" join STATEMENT_DETAIL  SD on R.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y'  AND SD.BALANCE_DUE>0 ");
		sb.append(" WHERE ");
		sb.append("   S.ID is not null ");
		sb.append("   AND ");
		sb.append("   LOWER(P.PROJECT_NBR) = LOWER('").append(Operator.sqlEscape(projectnbr)).append("') ");
		sb.append(" GROUP BY ");
		sb.append("   P.ID, ");
		sb.append("   P.NAME, ");
		sb.append("   A.ID, ");
		sb.append("   ACT_NBR");
		sb.append(" UNION ");
		sb.append(" SELECT ");
		sb.append("   P.ID, ");
		sb.append("   P.NAME, ");
		sb.append("   P.ID as PROJECT_ID, ");
		sb.append("   P.ID as ACTIVITY_ID, ");
		sb.append("   P.PROJECT_NBR as ACT_NBR, ");
		sb.append("   'P' as TYPE, ");
		sb.append("   SUM(SD.FEE_AMOUNT) as FEE_AMOUNT, ");
		sb.append("   SUM(SD.FEE_PAID) as FEE_PAID, ");
		sb.append("   SUM(SD.BALANCE_DUE) as BALANCE_DUE  ");
		sb.append(" FROM ");
		sb.append("   REF_PROJECT_STATEMENT R ");  
		sb.append("   JOIN PROJECT P on R.PROJECT_ID = P.ID ");
		sb.append("   JOIN STATEMENT S on R.STATEMENT_ID = S.ID ");
		sb.append(" join STATEMENT_DETAIL  SD on R.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y'  AND SD.BALANCE_DUE>0 ");
		sb.append(" WHERE ");
		sb.append("   S.ID is not null ");
		sb.append("   AND ");
		sb.append("   S.FINANCE_LOCK='N' ");
		sb.append("   AND ");
		sb.append("   LOWER(P.PROJECT_NBR) = LOWER('").append(Operator.sqlEscape(projectnbr)).append("') ");
		sb.append(" GROUP BY P.ID,P.NAME,PROJECT_NBR");
		return sb.toString();
	}

	public static String activityCart(int actid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" 	P.ID, ");
		sb.append(" 	P.NAME, ");
		sb.append(" 	A.ID AS ACTIVITY_ID, ");
		sb.append(" 	ACT_NBR, ");
		sb.append(" 	'A' AS TYPE, ");
		sb.append(" 	LAT.TYPE AS ACT_TYPE, ");
		sb.append(" 	SUM(SD.FEE_AMOUNT) AS FEE_AMOUNT, ");
		sb.append(" 	SUM(SD.FEE_PAID) AS FEE_PAID, ");
		sb.append(" 	SUM(SD.BALANCE_DUE) AS BALANCE_DUE  ");
		sb.append(" FROM ");
		sb.append(" 	REF_ACT_STATEMENT R ");  
		sb.append(" 	JOIN ACTIVITY AS A ON R.ACTIVITY_ID = A.ID ");
		sb.append(" 	JOIN LKUP_ACT_TYPE AS LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID ");
		sb.append(" 	JOIN PROJECT AS P ON A.PROJECT_ID = P.ID ");
		sb.append(" 	JOIN STATEMENT AS S ON R.STATEMENT_ID = S.ID ");
		sb.append(" 	JOIN STATEMENT_DETAIL AS SD ON R.STATEMENT_ID = SD.STATEMENT_ID AND SD.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" 	S.ID IS NOT NULL ");
		sb.append(" 	AND ");
		sb.append(" 	S.FINANCE_LOCK = 'N' ");
		sb.append(" 	AND ");
		sb.append(" 	S.ACTIVE='Y' ");
		sb.append(" 	AND ");
		sb.append(" 	R.ACTIVITY_ID = ").append(actid);
		sb.append(" GROUP BY ");
		sb.append(" 	P.ID, ");
		sb.append(" 	P.NAME, ");
		sb.append(" 	A.ID, ");
		sb.append(" 	ACT_NBR, ");
		sb.append(" 	LAT.TYPE ");
		return sb.toString();
	}

	// WRITTEN BY ALAIN 07/04/2018
	// WANT TO REPLACE OLD activityCart method above
	public static String activityCart1(int actid) {
		if (actid < 1) { return ""; }
		String calc = calc("activity", actid,true);
		StringBuilder sb = new StringBuilder();
		if (Operator.hasValue(calc)) {
			sb.append(calc);
			sb.append(", Q AS ( ");
			sb.append(" 	SELECT ");
			sb.append(" 		P.ID, ");
			sb.append(" 		P.NAME, ");
			sb.append(" 		A.ID AS ACTIVITY_ID, ");
			sb.append(" 		A.ACT_NBR, ");
			sb.append(" 		'A' AS TYPE, ");
			sb.append(" 		LAT.TYPE AS ACT_TYPE, ");
			sb.append(" 		TOTAL.REF_TYPE, ");
			sb.append(" 		TOTAL.REF_ID, ");
			sb.append(" 		TOTAL.REF, ");
			sb.append(" 		TOTAL.GROUP_ID, ");
			sb.append(" 		TOTAL.GROUP_NAME, ");
			sb.append(" 		TOTAL.ORDR, ");
			sb.append(" 		TOTAL.FINANCE_LOCK, ");
			sb.append(" 		TOTAL.PERMIT_FEE, ");
			sb.append(" 		TOTAL.REVIEW_FEE, ");
			sb.append(" 		TOTAL.FEE_AMOUNT, ");
			sb.append(" 		TOTAL.FEE_PAID, ");
			sb.append(" 		TOTAL.BALANCE_DUE, ");
			sb.append(" 		TOTAL.PERMIT_FEE AS ACTIVITY_FEE ");
			sb.append(" 	FROM ");
			sb.append(" 		TOTAL ");
			sb.append(" 		JOIN ACTIVITY AS A ON TOTAL.REF_ID = A.ID ");
			sb.append(" 		JOIN LKUP_ACT_TYPE AS LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID ");
			sb.append(" 		JOIN PROJECT AS P ON A.PROJECT_ID = P.ID ");
			sb.append(" 	WHERE ");
			sb.append(" 		TOTAL.FINANCE_LOCK = 'N' ");
			sb.append(" ) ");
			sb.append(" SELECT * FROM Q ORDER BY ORDR ");
		}
		return sb.toString();
	}

	public static String activityCart1(String reference) {
		if (!Operator.hasValue(reference)) { return ""; }
		String calc = calc("activity", ActivitySQL.getActivityId(reference),true);
		StringBuilder sb = new StringBuilder();
		if (Operator.hasValue(calc)) {
			sb.append(calc);
			sb.append(", Q AS ( ");
			sb.append(" 	SELECT ");
			sb.append(" 		P.ID, ");
			sb.append(" 		P.NAME, ");
			sb.append(" 		A.ID AS ACTIVITY_ID, ");
			sb.append(" 		A.ACT_NBR, ");
			sb.append(" 		'A' AS TYPE, ");
			sb.append(" 		LAT.TYPE AS ACT_TYPE, ");
			sb.append(" 		TOTAL.REF_TYPE, ");
			sb.append(" 		TOTAL.REF_ID, ");
			sb.append(" 		TOTAL.REF, ");
			sb.append(" 		TOTAL.GROUP_ID, ");
			sb.append(" 		TOTAL.GROUP_NAME, ");
			sb.append(" 		TOTAL.ORDR, ");
			sb.append(" 		TOTAL.FINANCE_LOCK, ");
			sb.append(" 		TOTAL.PERMIT_FEE, ");
			sb.append(" 		TOTAL.REVIEW_FEE, ");
			sb.append(" 		TOTAL.FEE_AMOUNT, ");
			sb.append(" 		TOTAL.FEE_PAID, ");
			sb.append(" 		TOTAL.BALANCE_DUE, ");
			sb.append(" 		TOTAL.PERMIT_FEE AS ACTIVITY_FEE ");
			sb.append(" 	FROM ");
			sb.append(" 		TOTAL ");
			sb.append(" 		JOIN ACTIVITY AS A ON TOTAL.REF_ID = A.ID ");
			sb.append(" 		JOIN LKUP_ACT_TYPE AS LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID ");
			sb.append(" 		JOIN PROJECT AS P ON A.PROJECT_ID = P.ID ");
			sb.append(" 	WHERE ");
			sb.append(" 		TOTAL.FINANCE_LOCK = 'N' ");
			sb.append(" ) ");
			sb.append(" SELECT * FROM Q ORDER BY ORDR ");
		}
		return sb.toString();
	}

	public static String activityCart(String reference) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append("   P.ID, ");
		sb.append("   P.NAME, ");
		sb.append("   A.ID as ACTIVITY_ID, ");
		sb.append("   ACT_NBR, 'A' as TYPE, ");
		sb.append("   LAT.TYPE as ACT_TYPE, ");
		sb.append("   SUM(SD.FEE_AMOUNT) as FEE_AMOUNT, ");
		sb.append("   SUM(SD.FEE_PAID) as FEE_PAID, ");
		sb.append("   SUM(SD.BALANCE_DUE) as BALANCE_DUE  ");
		sb.append(" FROM  ");
		sb.append("   REF_ACT_STATEMENT R ");  
		sb.append("   JOIN ACTIVITY A on R.ACTIVITY_ID = A.ID ");
		sb.append("   JOIN LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID ");
		sb.append("   JOIN PROJECT P on A.PROJECT_ID = P.ID ");
		sb.append("   JOIN STATEMENT S on R.STATEMENT_ID = S.ID  AND S.ACTIVE='Y' ");
		sb.append(" join STATEMENT_DETAIL  SD on R.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y' ");
		sb.append(" WHERE  ");
		sb.append("   S.ID is not null  ");
		sb.append("   AND  ");
		sb.append("   S.FINANCE_LOCK = 'N' ");
		sb.append("   AND ");
		sb.append("   LOWER(A.ACT_NBR) = LOWER('").append(Operator.sqlEscape(reference)).append("') ");
		sb.append(" GROUP BY  ");
		sb.append("   P.ID, ");
		sb.append("   P.NAME, ");
		sb.append("   A.ID, ");
		sb.append("   ACT_NBR,");
		sb.append("   LAT.TYPE ");
		return sb.toString();
	}

	public static String statementCart(int stid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select P.ID,P.NAME,A.ID as  ACTIVITY_ID,ACT_NBR, 'S' as TYPE");
		sb.append(" ,S.ID as STATEMENT_ID, S.STATEMENT_NUMBER, SUM(SD.FEE_AMOUNT) as FEE_AMOUNT,SUM(SD.FEE_PAID) as FEE_PAID,SUM(SD.BALANCE_DUE) as BALANCE_DUE  ");
		sb.append(" from REF_ACT_STATEMENT R ");  
		sb.append(" join ACTIVITY A on R.ACTIVITY_ID= A.ID ");
		sb.append(" join PROJECT P on A.PROJECT_ID=P.ID ");
		sb.append(" join STATEMENT S on R.STATEMENT_ID=S.ID ");
		sb.append(" join STATEMENT_DETAIL  SD on R.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y' ");
		sb.append(" where S.ID is not null and S.FINANCE_LOCK='N' ");
		sb.append(" AND ");
		sb.append(" S.ID = ").append(stid);
		sb.append(" GROUP BY  ");
		sb.append("   P.ID, ");
		sb.append("   P.NAME, ");
		sb.append("   A.ID, ");
		sb.append("   ACT_NBR,");
		sb.append("   S.ID, ");
		sb.append("  S.STATEMENT_NUMBER ");
		return sb.toString();
	}


	public static String extractFinanceRecords(int departmentId,Timekeeper date){
		StringBuilder sb = new StringBuilder();
		
		/*sb.append("  ");
		sb.append(" select PD.budget_unit,PD.ACCOUNT_NUMBER, 0 as cash,sum(PD.AMOUNT) as REVENUE  from PAYMENT P ");
		sb.append(" LEFT OUTER join LKUP_PAYMENT_METHOD M on P.LKUP_PAYMENT_METHOD_ID= M.ID  AND M.RECORD_FINANCIALS='Y' ");
		sb.append(" LEFT outer join LKUP_PAYMENT_TRANSACTION_TYPE T on P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID AND T.RECORD_FINANCIALS='Y' ");
		sb.append(" LEFT OUTER JOIN PAYMENT_DETAIL PD on P.ID = PD.PAYMENT_ID ");
		sb.append(" LEFT OUTER JOIN STATEMENT_DETAIL SD on PD.STATEMENT_DETAIL_ID = SD.ID ");
		sb.append(" LEFT OUTER JOIN REF_ACT_STATEMENT RAS on SD.STATEMENT_ID= RAS.STATEMENT_ID ");
		sb.append(" LEFT OUTER JOIN ACTIVITY A on RAS.ACTIVITY_ID = A.ID  ");
		sb.append(" LEFT OUTER JOIN LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID = LAT.ID ");
		sb.append(" WHERE CONVERT(VARCHAR(10),PAYMENT_DATE,101)   = '").append(date.getString("MM/DD/YYYY")).append("' ");
		if(departmentId>0){
			sb.append(" AND LAT.DEPARTMENT_ID = ").append(departmentId);
		}
		sb.append(" AND M.CASH_FLAG= 'N' group by  PD.budget_unit,PD.ACCOUNT_NUMBER ");
		sb.append(" UNION  ");
		sb.append("  ");
		sb.append(" select PD.budget_unit,PD.ACCOUNT_NUMBER,sum(PD.AMOUNT) as cash, 0 as revenue  from PAYMENT P ");
		sb.append(" LEFT OUTER join LKUP_PAYMENT_METHOD M on P.LKUP_PAYMENT_METHOD_ID= M.ID  AND M.RECORD_FINANCIALS='Y' ");
		sb.append(" LEFT outer join LKUP_PAYMENT_TRANSACTION_TYPE T on P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID AND T.RECORD_FINANCIALS='Y' ");
		sb.append(" LEFT OUTER JOIN PAYMENT_DETAIL PD on P.ID = PD.PAYMENT_ID ");
		sb.append(" LEFT OUTER JOIN STATEMENT_DETAIL SD on PD.STATEMENT_DETAIL_ID = SD.ID ");
		sb.append(" LEFT OUTER JOIN REF_ACT_STATEMENT RAS on SD.STATEMENT_ID= RAS.STATEMENT_ID ");
		sb.append(" LEFT OUTER JOIN ACTIVITY A on RAS.ACTIVITY_ID = A.ID  ");
		sb.append(" LEFT OUTER JOIN LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID = LAT.ID ");
		sb.append(" WHERE CONVERT(VARCHAR(10),PAYMENT_DATE,101)   = '").append(date.getString("MM/DD/YYYY")).append("' ");
		if(departmentId>0){
			sb.append(" AND LAT.DEPARTMENT_ID = ").append(departmentId);
		}
		sb.append(" AND M.CASH_FLAG= 'Y' group by  PD.budget_unit,PD.ACCOUNT_NUMBER ");
		sb.append(" UNION  ");
		sb.append(" select PD.FUND as BUDGET_UNIT,'10000' as account_number, 0 as cash,sum(PD.AMOUNT) as REVENUE  from PAYMENT P ");
		sb.append(" LEFT OUTER join LKUP_PAYMENT_METHOD M on P.LKUP_PAYMENT_METHOD_ID= M.ID  AND M.RECORD_FINANCIALS='Y' ");
		sb.append(" LEFT outer join LKUP_PAYMENT_TRANSACTION_TYPE T on P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID AND T.RECORD_FINANCIALS='Y' ");
		sb.append(" LEFT OUTER JOIN PAYMENT_DETAIL PD on P.ID = PD.PAYMENT_ID ");
		sb.append(" LEFT OUTER JOIN STATEMENT_DETAIL SD on PD.STATEMENT_DETAIL_ID = SD.ID ");
		sb.append(" LEFT OUTER JOIN REF_ACT_STATEMENT RAS on SD.STATEMENT_ID= RAS.STATEMENT_ID ");
		sb.append(" LEFT OUTER JOIN ACTIVITY A on RAS.ACTIVITY_ID = A.ID  ");
		sb.append(" LEFT OUTER JOIN LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID = LAT.ID ");
		sb.append(" WHERE CONVERT(VARCHAR(10),PAYMENT_DATE,101)   = '").append(date.getString("MM/DD/YYYY")).append("'  ");
		if(departmentId>0){
			sb.append(" AND LAT.DEPARTMENT_ID = ").append(departmentId);
		}
		sb.append(" AND M.CASH_FLAG= 'N' group by  PD.FUND ");
		sb.append(" UNION  ");
		sb.append("  ");
		sb.append(" select PD.FUND as BUDGET_UNIT,'10000' as account_number,sum(PD.AMOUNT) as cash, 0 as revenue  from PAYMENT P ");
		sb.append(" LEFT OUTER join LKUP_PAYMENT_METHOD M on P.LKUP_PAYMENT_METHOD_ID= M.ID  AND M.RECORD_FINANCIALS='Y' ");
		sb.append(" LEFT outer join LKUP_PAYMENT_TRANSACTION_TYPE T on P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID AND T.RECORD_FINANCIALS='Y' ");
		sb.append(" LEFT OUTER JOIN PAYMENT_DETAIL PD on P.ID = PD.PAYMENT_ID ");
		sb.append(" LEFT OUTER JOIN STATEMENT_DETAIL SD on PD.STATEMENT_DETAIL_ID = SD.ID ");
		sb.append(" LEFT OUTER JOIN REF_ACT_STATEMENT RAS on SD.STATEMENT_ID= RAS.STATEMENT_ID ");
		sb.append(" LEFT OUTER JOIN ACTIVITY A on RAS.ACTIVITY_ID = A.ID  ");
		sb.append(" LEFT OUTER JOIN LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID = LAT.ID ");
		sb.append(" WHERE CONVERT(VARCHAR(10),PAYMENT_DATE,101)   = '").append(date.getString("MM/DD/YYYY")).append("'  ");
		if(departmentId>0){
			sb.append(" AND LAT.DEPARTMENT_ID = ").append(departmentId);
		}
		sb.append(" AND M.CASH_FLAG= 'Y'group by  PD.FUND ");
		sb.append("  ");*/
		
		
		sb.append(" WITH R AS (");
		sb.append(" SELECT PD.BUDGET_UNIT,PD.ACCOUNT_NUMBER,");
		//sb.append(" 0 AS DEBIT,SUM(PD.AMOUNT) AS CREDIT  "); //changed after auto valuation 1/10/2018
		sb.append(" CASE WHEN SUM(PD.AMOUNT) <0  THEN SUM(PD.AMOUNT) ELSE 0 END AS DEBIT,");
		sb.append(" CASE WHEN SUM(PD.AMOUNT) >0  THEN SUM(PD.AMOUNT) ELSE 0 END AS CREDIT ");
		
		sb.append(" FROM PAYMENT P  ");
		sb.append("  JOIN LKUP_PAYMENT_METHOD M ON P.LKUP_PAYMENT_METHOD_ID= M.ID  AND M.RECORD_FINANCIALS='Y'  ");
		sb.append("  JOIN LKUP_PAYMENT_TRANSACTION_TYPE T ON P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID ") ;// --AND T.RECORD_FINANCIALS='Y'  ");
		sb.append("  JOIN PAYMENT_DETAIL PD ON P.ID = PD.PAYMENT_ID  ");
		sb.append("  LEFT OUTER JOIN STATEMENT_DETAIL SD ON PD.STATEMENT_DETAIL_ID = SD.ID  ");
		sb.append("  LEFT OUTER JOIN REF_ACT_STATEMENT RAS ON SD.STATEMENT_ID= RAS.STATEMENT_ID  ");
		sb.append("  LEFT OUTER JOIN ACTIVITY A ON RAS.ACTIVITY_ID = A.ID   ");
		sb.append(" LEFT OUTER JOIN LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID  ");
		sb.append(" WHERE CONVERT(VARCHAR(10),PAYMENT_DATE,101)   = '").append(date.getString("MM/DD/YYYY")).append("'    AND T.ID IN (1,2)");
		
		sb.append(" AND P.AUTO ='N' ");
		
		if(departmentId>0){
			sb.append(" AND LAT.DEPARTMENT_ID = ").append(departmentId);
		}
		sb.append(" GROUP BY  PD.BUDGET_UNIT,PD.ACCOUNT_NUMBER ");
		sb.append(" )");
		sb.append(" ");
		sb.append(" , C AS (");
		sb.append(" SELECT PD.BUDGET_UNIT,PD.ACCOUNT_NUMBER,");
		
	//5/13	sb.append(" CASE WHEN PD.AMOUNT<0 THEN SUM(PD.AMOUNT) ELSE 0 END  AS DEBIT, CASE WHEN PD.AMOUNT>0 THEN SUM(PD.AMOUNT) ELSE 0 END AS CREDIT  ");
		sb.append(" CASE WHEN SUM(PD.AMOUNT)<0 THEN SUM(PD.AMOUNT) ELSE 0 END  AS DEBIT, CASE WHEN SUM(PD.AMOUNT)>0 THEN SUM(PD.AMOUNT) ELSE 0 END AS CREDIT  ");
		
		sb.append(" FROM PAYMENT P  ");
		sb.append("   JOIN LKUP_PAYMENT_METHOD M ON P.LKUP_PAYMENT_METHOD_ID= M.ID  AND M.RECORD_FINANCIALS='Y'  ");
		sb.append("   JOIN LKUP_PAYMENT_TRANSACTION_TYPE T ON P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID AND T.RECORD_FINANCIALS='Y'  ");
		sb.append("   JOIN PAYMENT_DETAIL PD ON P.ID = PD.PAYMENT_ID  ");
		sb.append("   LEFT OUTER JOIN STATEMENT_DETAIL SD ON PD.STATEMENT_DETAIL_ID = SD.ID  ");
		sb.append("   LEFT OUTER JOIN REF_ACT_STATEMENT RAS ON SD.STATEMENT_ID= RAS.STATEMENT_ID  ");
		sb.append("   LEFT OUTER JOIN ACTIVITY A ON RAS.ACTIVITY_ID = A.ID   ");
		sb.append("  LEFT OUTER JOIN LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID ");
		sb.append("  WHERE CONVERT(VARCHAR(10),PAYMENT_DATE,101)   = '").append(date.getString("MM/DD/YYYY")).append("'    AND T.ID IN (3,5)");
		sb.append(" AND P.AUTO ='N' ");
		if(departmentId>0){
			sb.append(" AND LAT.DEPARTMENT_ID = ").append(departmentId);
		}
		sb.append("  GROUP BY  PD.BUDGET_UNIT,PD.ACCOUNT_NUMBER ");
		//5/13 sb.append(" ,PD.AMOUNT  ");
		
		sb.append("  ");
		sb.append("  ),");
		sb.append("  FR AS (");
		sb.append("  SELECT PD.FUND,'100000' AS ACCOUNT_NUMBER,   ");
		
		//sb.append(" SUM(PD.AMOUNT) AS DEBIT,0 AS CREDIT "); //changed after auto valuation 1/10/2018
		sb.append(" CASE WHEN SUM(PD.AMOUNT) >0  THEN SUM(PD.AMOUNT) ELSE 0 END AS DEBIT,");
		sb.append(" CASE WHEN SUM(PD.AMOUNT) <0  THEN  SUM(PD.AMOUNT)  ELSE 0 END AS CREDIT ");
		
		sb.append(" FROM PAYMENT P  ");
		sb.append("  JOIN LKUP_PAYMENT_METHOD M ON P.LKUP_PAYMENT_METHOD_ID= M.ID  AND M.RECORD_FINANCIALS='Y'  ");
		sb.append("  JOIN LKUP_PAYMENT_TRANSACTION_TYPE T ON P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID ") ;//--AND T.RECORD_FINANCIALS='Y'  ");
		sb.append("  JOIN PAYMENT_DETAIL PD ON P.ID = PD.PAYMENT_ID  ");
		sb.append("  LEFT OUTER JOIN STATEMENT_DETAIL SD ON PD.STATEMENT_DETAIL_ID = SD.ID  ");
		sb.append("  LEFT OUTER JOIN REF_ACT_STATEMENT RAS ON SD.STATEMENT_ID= RAS.STATEMENT_ID  ");
		sb.append("  LEFT OUTER JOIN ACTIVITY A ON RAS.ACTIVITY_ID = A.ID   ");
		sb.append(" LEFT OUTER JOIN LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID  ");
		sb.append(" WHERE CONVERT(VARCHAR(10),PAYMENT_DATE,101)   = '").append(date.getString("MM/DD/YYYY")).append("'    AND T.ID IN (1,2)");
		sb.append(" AND P.AUTO ='N' ");
		if(departmentId>0){
			sb.append(" AND LAT.DEPARTMENT_ID = ").append(departmentId);
		}
		sb.append(" GROUP BY  PD.FUND");
		sb.append("  )");
		sb.append(" ,");
		sb.append("  FC AS (");
		sb.append("  SELECT PD.FUND,'100000' AS ACCOUNT_NUMBER, ");
		// 5/13 sb.append("  CASE WHEN PD.AMOUNT>0 THEN SUM(PD.AMOUNT) ELSE 0 END  AS DEBIT, CASE WHEN PD.AMOUNT<0 THEN SUM(PD.AMOUNT) ELSE 0 END AS CREDIT  ");
		sb.append("  CASE WHEN SUM(PD.AMOUNT)>0 THEN SUM(PD.AMOUNT) ELSE 0 END  AS DEBIT, CASE WHEN SUM(PD.AMOUNT)<0 THEN SUM(PD.AMOUNT) ELSE 0 END AS CREDIT  ");
		sb.append(" ");
		sb.append(" FROM PAYMENT P  ");
		sb.append("  JOIN LKUP_PAYMENT_METHOD M ON P.LKUP_PAYMENT_METHOD_ID= M.ID  AND M.RECORD_FINANCIALS='Y'  ");
		sb.append("  JOIN LKUP_PAYMENT_TRANSACTION_TYPE T ON P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID AND T.RECORD_FINANCIALS='Y'  ");
		sb.append("  JOIN PAYMENT_DETAIL PD ON P.ID = PD.PAYMENT_ID  ");
		sb.append("  LEFT OUTER JOIN STATEMENT_DETAIL SD ON PD.STATEMENT_DETAIL_ID = SD.ID  ");
		sb.append("  LEFT OUTER JOIN REF_ACT_STATEMENT RAS ON SD.STATEMENT_ID= RAS.STATEMENT_ID  ");
		sb.append("  LEFT OUTER JOIN ACTIVITY A ON RAS.ACTIVITY_ID = A.ID   ");
		sb.append(" LEFT OUTER JOIN LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID  ");
		sb.append(" WHERE CONVERT(VARCHAR(10),PAYMENT_DATE,101)   = '").append(date.getString("MM/DD/YYYY")).append("'     AND T.ID IN (3,5) ");
		sb.append(" AND P.AUTO ='N' ");
		if(departmentId>0){
			sb.append(" AND LAT.DEPARTMENT_ID = ").append(departmentId);
		}
		sb.append(" GROUP BY  PD.FUND  ");
		//5/13 sb.append(" ,PD.AMOUNT  ");
			
		sb.append(" ");
		sb.append("  )");
		sb.append("  SELECT *,10 as ORDR  FROM R");
		sb.append("  UNION ");
		sb.append("  SELECT *,10 as ORDR  FROM C");
		sb.append("  UNION ");
		sb.append("  SELECT *,20 as ORDR  FROM FR");
		sb.append("  UNION ");
		sb.append("  SELECT *,20 as ORDR  FROM FC");
		sb.append(" ORDER BY ORDR ");
		
		return sb.toString();
	}




	public static void main(String args[]){
		Timekeeper k = new Timekeeper();
		String start = "2018-10-01";
		k.setDate(start);
		
		//k.setMonth(6);
		int j = 0;
		if(k.MONTH()>6){
			j = k.MONTH()-6;
		}else {
			j = k.MONTH()+6;
		}
		
		String s = "BS0410445";
		
		System.out.println(j);
	}



	public static String getStatementDetail(int statementdetailId){
		
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select  ");
		sb.append(" ");
		sb.append(" SD.FEE_ID");
		sb.append(",");
		sb.append(" SD.ID ");
		sb.append(",");
		sb.append(" F.NAME ");
		sb.append(",");
		sb.append(" F.DESCRIPTION ");
		sb.append(",");
		sb.append(" SD.INPUT1 ");
		sb.append(",");
		sb.append(" SD.INPUT2 ");
		sb.append(",");
		sb.append(" SD.INPUT3 ");
		sb.append(",");
		sb.append(" SD.INPUT4 ");
		sb.append(",");
		sb.append(" SD.INPUT5 ");
		sb.append(",");
		sb.append(" SD.FINANCE_MAP_ID ");
		sb.append(",");
		sb.append(" SD.ACCOUNT_NUMBER ");
		sb.append(",");
		sb.append("  CONVERT(VARCHAR(10),SD.FEE_DATE,101)  as FEE_DATE ");
		
		sb.append(",");
		sb.append(" SD.FEE_AMOUNT ");
		sb.append(",");
		sb.append(" SD.FEE_PAID ");
		sb.append(",");
		sb.append(" SD.BALANCE_DUE ");
		
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE1 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE2 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE3 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE4 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE5 ");
		
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL1 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL2 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL3 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL4 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL5 ");
		
		
		sb.append(",");
		sb.append(" RFF.LKUP_FORMULA_ID ");
		
		sb.append(",");
		sb.append("  CONVERT(VARCHAR(10),RFF.START_DATE,101)  as START_DATE ");
		sb.append(",");
		sb.append("  CONVERT(VARCHAR(10),RFF.EXPIRATION_DATE,101) as EXPIRATION_DATE ");
		
		
		/*sb.append(",");
		sb.append(" LF.DEFINITION ");*/
		sb.append(",");
		sb.append(" SD.ACTIVE ");
		
		
		/*sb.append(",");
		sb.append(" S.FINANCE_LOCK ");
		sb.append(",");
		sb.append(" UP.USERNAME ");*/
		
		sb.append("    , ");
		sb.append("  CASE WHEN     ( RFF.INPUT_EDITABLE1 ='Y' OR  ");
		sb.append(" RFF.INPUT_EDITABLE2 ='Y' OR  ");
		sb.append(" RFF.INPUT_EDITABLE3 ='Y' OR  ");
		sb.append("  RFF.INPUT_EDITABLE4 ='Y' OR  ");
		sb.append("  RFF.INPUT_EDITABLE5 ='Y' )  THEN 'Y' else 'N' END AS EDIT ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE1 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE2 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE3 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE4 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE5 ");
		
		sb.append(" from STATEMENT_DETAIL SD  ");
		sb.append(" join FEE F on SD.FEE_ID=F.ID ");
		sb.append(" join REF_FEE_FORMULA RFF on SD.REF_FEE_FORMULA_ID=RFF.ID  ");
		sb.append(" where SD.ID= ").append(statementdetailId).append("  ");
		

		
		return sb.toString();
	}




	public static String getCopyFeeList(String type, int typeid, String entity,int groupid,String date,String divisionfilters){
		StringBuilder sb = new StringBuilder();
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		
		
		
		sb.append(" select  ");
		sb.append(" F.ID ");
		sb.append(",");
		sb.append(" F.NAME ");
		sb.append(",");
		sb.append(" F.DESCRIPTION ");
		sb.append(",");
		sb.append("  CONVERT(VARCHAR(10),RFF.START_DATE,101)  as START_DATE ");
		//sb.append(" RFF.START_DATE ");
		sb.append(",");
		//sb.append(" RFF.EXPIRATION_DATE ");
		sb.append("  CONVERT(VARCHAR(10),RFF.EXPIRATION_DATE,101) as EXPIRATION_DATE ");
		sb.append(",");
		sb.append(" RFF.ACCOUNT ");
		sb.append(",");
		sb.append(" RFF.INPUT1 ");
		sb.append(",");
		sb.append(" RFF.INPUT2 ");
		sb.append(",");
		sb.append(" RFF.INPUT3 ");
		sb.append(",");
		sb.append(" RFF.INPUT4 ");
		sb.append(",");
		sb.append(" RFF.INPUT5 ");
		sb.append(",");
		sb.append(" RFF.FACTOR ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE1 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE2 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE3 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE4 ");
		sb.append(",");
		sb.append(" RFF.INPUT_TYPE5 ");
		
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE1 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE2 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE3 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE4 ");
		sb.append(",");
		sb.append(" RFF.INPUT_EDITABLE5 ");
		
		
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL1 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL2 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL3 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL4 ");
		sb.append(",");
		sb.append(" RFF.INPUT_LABEL5 ");
		
		
		sb.append(",");
		sb.append(" RFF.MANUAL_ACCOUNT ");
		
		sb.append(",");
		sb.append(" RFF.LKUP_FORMULA_ID ");
		sb.append(",");
		sb.append(" LF.DEFINITION ");
		sb.append(",");
		sb.append(" RFG.REQ ");
		sb.append(",");
		sb.append(" FG.GROUP_NAME ");
		sb.append(",");
		sb.append(" FG.ID AS GROUP_ID ");
		sb.append(",");
		sb.append(" RFF.ID as RFF_ID  ");
		sb.append(" from REF_").append(tableref).append("_STATEMENT RAS  JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE ='Y' JOIN REF_FEE_FORMULA RFF ON SD.FEE_ID = RFF.FEE_ID ");
		sb.append(" left outer JOIN REF_FEE_GROUP RFG on SD.FEE_ID=RFG.FEE_ID AND SD.GROUP_ID= RFG.FEE_GROUP_ID");
		sb.append(" left outer join LKUP_FORMULA LF on RFF.LKUP_FORMULA_ID=LF.ID ");
		sb.append(" JOIN FEE F on RFF.FEE_ID=F.ID ");
		sb.append(" join FEE_GROUP FG on RFG.FEE_GROUP_ID=FG.ID ");
		sb.append(" where RFG.FEE_GROUP_ID=").append(groupid).append("  ");
		//sb.append(" and EXPIRATION_DATE is null ");
		//sb.append( " and ((RFF.START_DATE >= '").append(date).append("' OR RFF.EXPIRATION_DATE is null) OR (RFF.EXPIRATION_DATE >= '").append(date).append("' or RFF.EXPIRATION_DATE is null) )");
		sb.append( " and ((CONVERT(VARCHAR(10),RFF.START_DATE,101) <= '").append(date).append("' and RFF.EXPIRATION_DATE >= '").append(date).append("'  )  OR (CONVERT(VARCHAR(10),RFF.START_DATE,101) <= '").append(date).append("' AND RFF.EXPIRATION_DATE is null ) )"); 
		//sb.append( " and ( (RFF.START_DATE <= '").append(date).append("' AND RFF.EXPIRATION_DATE is null ) )");
		/*if(autoadd){
			sb.append(" AND RFG.AUTO_ADD='Y' ");
		}
		
		if(plancheck){
			sb.append(" AND RFG.PLAN_CHK_REQ='Y' ");
		}*/
		
		if(Operator.hasValue(divisionfilters)){
			sb.append(" AND RFG.FEE_ID not in (").append(divisionfilters).append(") ");
		}
		
		sb.append (" AND RAS.").append(idref).append(" =  ").append(typeid);
		sb.append(" order by RFG.ORDR ");
		
		
		
		return sb.toString();
	}





}
