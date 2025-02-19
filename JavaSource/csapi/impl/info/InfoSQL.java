package csapi.impl.info;

import alain.core.utils.Operator;


public class InfoSQL {

	public static String getContent(String ctype, boolean staff) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" TOP 1 * ");
		sb.append(" FROM ");
		sb.append(" CONTENT AS C ");
		sb.append(" WHERE ");
		sb.append(" LOWER(C.TYPE) = LOWER('").append(Operator.sqlEscape(ctype)).append("') ");
		sb.append(" AND ");
		sb.append(" C.ACTIVE = 'Y' ");
		if (!staff) {
			sb.append(" AND ");
			sb.append(" C.STAFF = 'N' ");
		}
		sb.append(" ORDER BY STAFF DESC, UPDATED_DATE DESC ");
		return sb.toString();
	}

}















