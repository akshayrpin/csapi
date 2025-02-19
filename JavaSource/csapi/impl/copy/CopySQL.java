package csapi.impl.copy;

import alain.core.utils.Operator;

public class CopySQL {

	public static String details(String type, int typeid, int id) {
		if (!Operator.hasValue(type) || typeid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.PROJECT_ID ");
		sb.append(" , ");
		sb.append(" P.PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" A.ID AS ACTIVITY_ID ");
		sb.append(" , ");
		sb.append(" A.ACT_NBR ");
		sb.append(" , ");
		sb.append(" A.START_DATE ");
		sb.append(" , ");
		sb.append(" A.APPLIED_DATE ");
		sb.append(" , ");
		sb.append(" A.ISSUED_DATE ");
		sb.append(" , ");
		sb.append(" A.EXP_DATE ");
		sb.append(" , ");
		sb.append(" A.APPLICATION_EXP_DATE ");
		sb.append(" , ");
		sb.append(" A.FINAL_DATE ");
		sb.append(" , ");
		sb.append(" T.DAYS_TILL_EXPIRED ");
		sb.append(" , ");
		sb.append(" T.DAYS_TILL_APPLICATION_EXPIRED ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND A.ID = ").append(typeid);
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID ");
		return sb.toString();
	}
	

















}















