package csapi.impl.entity;

import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.utils.CsReflect;


public class EntitySQL {

	public static String getEntity(String type, int typeid) {
		StringBuilder sb = new StringBuilder();
		if (type.equalsIgnoreCase("activity") || type.equalsIgnoreCase("project")) {
			sb.append(" SELECT ");
			sb.append("   P.ENTITY, P.ID AS PROJECT_ID, P.PROJECT_NBR ");
			if (type.equalsIgnoreCase("activity")) {
				sb.append(" , A.ACT_NBR ");
			}
			sb.append(" FROM ");
			sb.append("   PROJECT AS P ");
			if (type.equalsIgnoreCase("activity")) {
				sb.append("   JOIN ACTIVITY AS A ON A.PROJECT_ID = P.ID AND A.ID = ").append(typeid);
			}
			else {
				sb.append(" WHERE ");
				sb.append("   ID = ").append(typeid);
			}
		}
		return sb.toString();
	}

	public static String getEntity(String type, String ref) {
		StringBuilder sb = new StringBuilder();
		if (type.equalsIgnoreCase("activity") || type.equalsIgnoreCase("project")) {
			sb.append(" SELECT ");
			sb.append("   P.ENTITY, P.ID AS PROJECT_ID, P.PROJECT_NBR ");
			if (type.equalsIgnoreCase("activity")) {
				sb.append(" , A.ACT_NBR ");
				sb.append(" , A.ID AS TYPEID ");
			}
			else {
				sb.append(" , P.ID AS TYPEID ");
			}
			sb.append(" FROM ");
			sb.append("   PROJECT AS P ");
			if (type.equalsIgnoreCase("activity")) {
				sb.append("   JOIN ACTIVITY AS A ON A.PROJECT_ID = P.ID AND LOWER(A.ACT_NBR) = LOWER('").append(Operator.sqlEscape(ref)).append("') ");
			}
			else {
				sb.append(" WHERE ");
				sb.append("   LOWER(P.PROJECT_NBR) = LOWER('").append(Operator.sqlEscape(ref)).append("') ");
			}
		}
		return sb.toString();
	}

	public static String getEntityId(String entity, String type, int typeid) {
		String tableref = CsReflect.getTableRef(entity);
		String idref = CsReflect.getFieldIdRef(entity);

		StringBuilder sb = new StringBuilder();
		if (type.equalsIgnoreCase("project") || type.equalsIgnoreCase("activity")) {
			sb.append(" SELECT DISTINCT ");
			sb.append(idref).append(" AS ID ");
			sb.append(" FROM ");
			sb.append(" REF_").append(tableref).append("_PROJECT AS RP ");
			sb.append(" WHERE ");
			sb.append(" RP.ACTIVE = 'Y' ");
			sb.append(" AND ");
			sb.append(" RP.PROJECT_ID = ");
			if (type.equalsIgnoreCase("project")) {
				sb.append(typeid);
			}
			else {
				sb.append(" ( SELECT PROJECT_ID FROM ACTIVITY WHERE ID = ").append(typeid).append(" ) ");
			}
		}
		
		return sb.toString();
	}

	public static String entity(String entity, int entityid) {
		if (!Operator.hasValue(entity) || entityid < 1) { return ""; }
		if (Operator.equalsIgnoreCase(entity, "lso")) {
			return getLSO(entityid);
		}
		String table = CsReflect.getMainTableRef(entity);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" M.* ");
		sb.append(" FROM ");
		sb.append(" ").append(table).append(" AS M ");
		sb.append(" WHERE ");
		sb.append(" M.ID = ").append(entityid);
		return sb.toString();

	}

	public static String getEntityParents(String entity, int entityid) {
		if (!Operator.hasValue(entity) || entityid < 1) { return ""; }
		if (Operator.equalsIgnoreCase(entity, "lso")) {
			return getLSOParents(entityid);
		}
		String table = CsReflect.getMainTableRef(entity);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" M.ID ");
		sb.append(" , ");
		sb.append(" M.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" M.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" P.ID AS PARENT_ID ");
		sb.append(" , ");
		sb.append(" P.ISPUBLIC AS PARENT_ISPUBLIC ");
		sb.append(" , ");
		sb.append(" P.PARENT_ID AS GRANDPARENT_ID ");
		sb.append(" , ");
		sb.append(" GP.ISPUBLIC AS GRANDPARENT_ISPUBLIC ");
		sb.append(" , ");
		sb.append(" C.ID AS CHILD_ID ");
		sb.append(" , ");
		sb.append(" GC.ID AS GRANDCHILD_ID ");
		sb.append(" FROM ");
		sb.append(" ").append(table).append(" AS M ");
		sb.append(" LEFT OUTER JOIN ").append(table).append(" AS P ON M.PARENT_ID = P.ID ");
		sb.append(" LEFT OUTER JOIN ").append(table).append(" AS GP ON P.PARENT_ID = GP.ID ");
		sb.append(" LEFT OUTER JOIN ").append(table).append(" AS C ON M.ID = C.PARENT_ID ");
		sb.append(" LEFT OUTER JOIN ").append(table).append(" AS GC ON C.ID = GC.PARENT_ID ");
		sb.append(" WHERE ");
		sb.append(" M.ID = ").append(entityid);
		return sb.toString();

	}

	public static String getLSOParents(int lsoid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" M.ID ");
		sb.append(" , ");
		sb.append("   CAST(M.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(S.PRE_DIR, '') + ' ' + S.STR_NAME + ' ' + S.STR_TYPE + ' ' + COALESCE(M.UNIT, '') AS DESCRIPTION ");
		sb.append(" , ");
		sb.append(" M.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" P.ID AS PARENT_ID ");
		sb.append(" , ");
		sb.append(" P.ISPUBLIC AS PARENT_ISPUBLIC ");
		sb.append(" , ");
		sb.append(" P.PARENT_ID AS GRANDPARENT_ID ");
		sb.append(" , ");
		sb.append(" GP.ISPUBLIC AS GRANDPARENT_ISPUBLIC ");
		sb.append(" , ");
		sb.append(" C.ID AS CHILD_ID ");
		sb.append(" , ");
		sb.append(" GC.ID AS GRANDCHILD_ID ");
		sb.append(" FROM ");
		sb.append(" LSO AS M ");
		sb.append(" JOIN LSO_STREET AS S ON M.LSO_STREET_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN LSO AS P ON M.PARENT_ID = P.ID ");
		sb.append(" LEFT OUTER JOIN LSO AS GP ON P.PARENT_ID = GP.ID ");
		sb.append(" LEFT OUTER JOIN LSO AS C ON M.ID = C.PARENT_ID ");
		sb.append(" LEFT OUTER JOIN LSO AS GC ON C.ID = GC.PARENT_ID ");
		sb.append(" WHERE ");
		sb.append(" M.ID = ").append(lsoid);
		return sb.toString();

	}

	public static String getLSO(int lsoid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(" LSO AS M ");
		sb.append(" WHERE ");
		sb.append(" M.ID = ").append(lsoid);
		return sb.toString();

	}

	public static String getDivisions(String entity, int entityid) {
		if (!Operator.hasValue(entity) || entityid < 1) { return ""; }
		String tableref = CsReflect.getTableRef(entity);
		String idref = CsReflect.getFieldIdRef(entity);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" D.ID ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_DIVISIONS AS RD ");
		sb.append(" JOIN LKUP_DIVISIONS AS D ON RD.LKUP_DIVISIONS_ID = D.ID AND D.ACTIVE = 'Y' AND RD.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_DIVISIONS_TYPE AS T ON D.LKUP_DIVISIONS_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" RD.").append(idref).append(" = ").append(entityid);
		return sb.toString();
	}





}















