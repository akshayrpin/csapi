package csapi.impl.lkup;

import alain.core.utils.Operator;
import csapi.utils.CsReflect;


public class LkupSQL {


	public static String types(String grouptype, int selected) {
		String table = CsReflect.getTypeTable(grouptype);
		StringBuilder sb = new StringBuilder();
		if (!Operator.hasValue(table)) {
			String tr = CsReflect.getTableRef(grouptype);
			sb = new StringBuilder();
			if (Operator.hasValue(tr)) {
				sb.append("LKUP_").append(tr).append("_TYPE");
				table = sb.toString();
			}
		}
		sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		if (selected > 0) {
			sb.append(" , ");
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" ").append(table).append(" ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY TYPE ");
		return sb.toString();
	}

	public static String typeDescriptions(String grouptype, int selected) {
		String table = CsReflect.getTypeTable(grouptype);
		StringBuilder sb = new StringBuilder();
		if (!Operator.hasValue(table)) {
			String tr = CsReflect.getTableRef(grouptype);
			sb = new StringBuilder();
			if (Operator.hasValue(tr)) {
				sb.append("LKUP_").append(tr).append("_TYPE");
				table = sb.toString();
			}
		}
		sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" DESCRIPTION AS TEXT ");
		sb.append(" , ");
		sb.append(" TYPE AS DESCRIPTION ");
		if (selected > 0) {
			sb.append(" , ");
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" ").append(table).append(" ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY TYPE ");
		return sb.toString();
	}


	public static String status(String grouptype, int selected) {
		String table = CsReflect.getStatusTable(grouptype);
		StringBuilder sb = new StringBuilder();
		if (!Operator.hasValue(table)) {
			String tr = CsReflect.getTableRef(grouptype);
			sb = new StringBuilder();
			if (Operator.hasValue(tr)) {
				sb.append("LKUP_").append(tr).append("_STATUS");
				table = sb.toString();
			}
		}
		sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" STATUS AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		if (selected > 0) {
			sb.append(" , ");
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" ").append(table).append(" ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY STATUS ");
		return sb.toString();
	}

	public static String statusDescriptions(String grouptype, int selected) {
		String table = CsReflect.getStatusTable(grouptype);
		StringBuilder sb = new StringBuilder();
		if (!Operator.hasValue(table)) {
			String tr = CsReflect.getTableRef(grouptype);
			sb = new StringBuilder();
			if (Operator.hasValue(tr)) {
				sb.append("LKUP_").append(tr).append("_STATUS");
				table = sb.toString();
			}
		}
		sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" DESCRIPTION AS TEXT ");
		sb.append(" , ");
		sb.append(" STATUS AS DESCRIPTION ");
		if (selected > 0) {
			sb.append(" , ");
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" ").append(table).append(" ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY STATUS ");
		return sb.toString();
	}

	public static String groups(String grouptype, String type, int typeid, int selected) {
		String grptable = CsReflect.getTypeTable(grouptype);
		String tableref = CsReflect.getTableRef(type);
		String table = CsReflect.getMainTableRef(type);
		String tabletype = CsReflect.getTypeTableRef(type);


		StringBuilder sb = new StringBuilder();

		if (!Operator.hasValue(grptable)) {
			String grptr = CsReflect.getTableRef(grouptype);
			sb = new StringBuilder();
			if (Operator.hasValue(grptr)) {
				sb.append(grptr).append("_GROUP");
				grptable = sb.toString();
			}
		}

		if (!Operator.hasValue(tabletype)) {
			String typtr = CsReflect.getTableRef(type);
			sb = new StringBuilder();
			if (Operator.hasValue(typtr)) {
				sb.append("LKUP_").append(typtr).append("_TYPE");
				tabletype = sb.toString();
			}
		}

		sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" L.ID ");
		sb.append(" , ");
		sb.append(" L.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" L.GROUP_NAME AS TEXT ");
		sb.append(" , ");
		sb.append(" L.DESCRIPTION ");
		if (selected > 0) {
			sb.append(" , ");
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" ").append(grptable).append(" AS L ");
		sb.append(" JOIN REF_").append(tableref).append("_").append(grptable).append(" AS R ON L.ID = R.").append(grptable).append("_ID AND L.ACTIVE = 'Y' AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN ").append(table).append(" AS M ON R.").append(tabletype).append("_ID = M.").append(tabletype).append("_ID AND M.ACTIVE = 'Y' AND M.ID = ").append(typeid);
		sb.append(" ORDER BY GROUP_NAME ");
		return sb.toString();
	}







}















