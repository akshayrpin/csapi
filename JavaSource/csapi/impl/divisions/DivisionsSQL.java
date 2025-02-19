package csapi.impl.divisions;

import java.util.HashMap;

import csapi.impl.entity.EntityAgent;
import csapi.utils.CsReflect;
import csshared.vo.TypeInfo;
import alain.core.utils.Operator;

public class DivisionsSQL {

	public static String details(String type, int typeid) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		if (entity.getEntityid() < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT  ");
		sb.append("   T.ID, ");
		sb.append("   TYPE.DESCRIPTION AS FIELD, ");
		sb.append("   T.DIVISION AS VALUE, ");
		sb.append("   TYPE.ID AS TYPE_ID, ");
		sb.append("   TYPE.REQUIRED ");
		sb.append(" FROM ");
		sb.append("   LSO AS L ");
		sb.append("   JOIN LKUP_DIVISIONS_TYPE AS TYPE ON L.LKUP_LSO_TYPE_ID = TYPE.LKUP_LSO_TYPE_ID AND L.ID = ").append(entity.getEntityid());
		sb.append("   LEFT OUTER JOIN ( ");
		sb.append("     LKUP_DIVISIONS AS T ");
		sb.append("     JOIN REF_LSO_DIVISIONS AS R ON T.ID = R.LKUP_DIVISIONS_ID AND R.LSO_ID = ").append(entity.getEntityid()).append(" AND R.ACTIVE = 'Y' AND T.ACTIVE = 'Y' ");
		sb.append("   ) ON T.LKUP_DIVISIONS_TYPE_ID = TYPE.ID ");
		sb.append(" WHERE ");
		sb.append("   TYPE.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String summary(String type, int typeid) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		if (entity.getEntityid() < 1) { return ""; }
		StringBuilder e = new StringBuilder();
		e.append(entity.getEntityid());
		if (entity.getParentid() > 0) {
			e.append(",").append(entity.getParentid());
		}
		if (entity.getGrandparentid() > 0) {
			e.append(",").append(entity.getGrandparentid());
		}
		String in = e.toString();
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH V AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		D.ID, ");
		sb.append(" 		T.ID AS TYPE_ID, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN R.LSO_ID <> ").append(typeid).append(" THEN T.DESCRIPTION + ' (' + LT.DESCRIPTION + ')' ");
		sb.append(" 		ELSE T.DESCRIPTION END AS FIELD, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN R.LSO_ID = ").append(typeid).append(" THEN 'LEVEL' ");
		sb.append(" 		ELSE 'EXTERNAL' END AS DATATYPE, ");
		sb.append(" 		D.DIVISION AS VALUE, ");
		sb.append(" 		LT.DESCRIPTION AS LSO_TYPE, ");
		sb.append(" 		R.LSO_ID ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_LSO_DIVISIONS AS R ");
		sb.append(" 		JOIN LKUP_DIVISIONS AS D ON R.LKUP_DIVISIONS_ID = D.ID AND R.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND R.LSO_ID IN (").append(in).append(") ");
		sb.append(" 		JOIN LKUP_DIVISIONS_TYPE AS T ON D.LKUP_DIVISIONS_TYPE_ID = T.ID ");
		sb.append(" 		JOIN LKUP_LSO_TYPE AS LT ON T.LKUP_LSO_TYPE_ID = LT.ID ");
		sb.append(" ) ");
		sb.append(" , E AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		-1 AS ID, ");
		sb.append(" 		T.ID AS TYPE_ID, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN L.ID <> ").append(typeid).append(" THEN T.DESCRIPTION + ' (' + LT.DESCRIPTION + ')' ");
		sb.append(" 		ELSE T.DESCRIPTION END AS FIELD, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN L.ID = ").append(typeid).append(" THEN 'LEVEL' ");
		sb.append(" 		ELSE 'EXTERNAL' END AS DATATYPE, ");
		sb.append(" 		'' AS VALUE, ");
		sb.append(" 		LT.DESCRIPTION AS LSO_TYPE, ");
		sb.append(" 		L.ID AS LSO_ID ");
		sb.append(" 	FROM ");
		sb.append(" 		LKUP_DIVISIONS_TYPE AS T ");
		sb.append(" 		JOIN LSO AS L ON L.LKUP_LSO_TYPE_ID = T.LKUP_LSO_TYPE_ID AND T.ACTIVE = 'Y' AND T.ID NOT IN (SELECT TYPE_ID FROM V) AND L.ID IN (").append(in).append(") ");
		sb.append(" 		JOIN LKUP_LSO_TYPE AS LT ON T.LKUP_LSO_TYPE_ID = LT.ID ");
		sb.append(" ) ");
		sb.append(" , U AS ( ");
		sb.append(" 	SELECT * FROM V ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT * FROM E ");
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	ID, ");
		sb.append(" 	TYPE_ID, ");
		sb.append(" 	FIELD, ");
		sb.append(" 	VALUE, ");
		sb.append(" 	DATATYPE, ");
		sb.append(" 	LSO_TYPE, ");
		sb.append(" 	LSO_ID ");
		sb.append(" FROM ");
		sb.append(" 	U ");
		sb.append(" ORDER BY ");
		sb.append(" 	DATATYPE DESC, FIELD ");
		return sb.toString();
	}

	/**
	 * @deprecated Use inActiveCustom(String type, int typeid)
	 * @return
	 */
	public static String inActiveCustom(int typeid) {
		return inActiveCustom("lso", typeid);
	}

	public static String inActiveCustom(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		if(typeid>0) {
			sb.append(" UPDATE  REF_").append(tableref).append("_DIVISIONS SET ACTIVE ='N' WHERE ").append(idref).append(" = ").append(typeid);
		}
		return sb.toString();
	}

	/**
	 * @deprecated Use backUpCustom(String type, int typeid)
	 * @return
	 */
	public static String backUpCustom(int typeid) {
		return backUpCustom("lso", typeid);
	}

	public static String backUpCustom(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		if(typeid>0) {
			sb.append(" INSERT INTO REF_").append(tableref).append("_DIVISIONS_HISTORY SELECT * FROM  REF_").append(tableref).append("_DIVISIONS WHERE ACTIVE='N' AND ").append(idref).append(" = ").append(typeid);
		}
		return sb.toString();
	}
	
	/**
	 * @deprecated Use deleteCustom(String type, int typeid)
	 * @return
	 */
	public static String deleteCustom(int typeid) {
		return deleteCustom("lso", typeid);
	}

	public static String deleteCustom(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		if(typeid>0){
			sb.append(" DELETE FROM REF_").append(tableref).append("_DIVISIONS WHERE ACTIVE='N' AND ").append(idref).append(" = ").append(typeid);
		}
		return sb.toString();
	}
	
	
	public static String checkTerritory(int typeId) {
		StringBuilder sb = new StringBuilder();
		sb.append(" delete from REF_LSO_DIVISIONS WHERE  ACTIVE='N' AND LSO_ID = ").append(typeId);
		return sb.toString();
	}

	public static String choices(int divtypeid, int selected){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID, ");
		sb.append(" ID as VALUE, ");
		sb.append(" DIVISION AS TEXT, ");
		sb.append(" DESCRIPTION ");
		if (selected > 0) {
			sb.append(" , CASE WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" LKUP_DIVISIONS ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE='Y' ");
		sb.append(" AND ");
		sb.append(" LKUP_DIVISIONS_TYPE_ID = ").append(divtypeid);
		sb.append(" ORDER BY DIVISION ");
		return sb.toString();
	}

	public static String choices(int divtypeid, int selected, HashMap<String, String> addl){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		for (String key : addl.keySet()) {
			String field = key;
			String value = addl.get(key);
			if (Operator.hasValue(field)) {
				if (!Operator.equalsIgnoreCase(field, "ID") && !Operator.equalsIgnoreCase(field, "TEXT") && !Operator.equalsIgnoreCase(field, "VALUE") && !Operator.equalsIgnoreCase(field, "DESCRIPTION")) {
					sb.append(" '").append(Operator.sqlEscape(value)).append("' AS ").append(field);
					sb.append(" , ");
				}
			}
		}
		sb.append(" ID, ");
		sb.append(" ID as VALUE, ");
		sb.append(" DIVISION AS TEXT, ");
		sb.append(" DESCRIPTION ");
		if (selected > 0) {
			sb.append(" , CASE WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" LKUP_DIVISIONS ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE='Y' ");
		sb.append(" AND ");
		sb.append(" LKUP_DIVISIONS_TYPE_ID = ").append(divtypeid);
		sb.append(" ORDER BY DIVISION ");
		return sb.toString();
	}

	public static String choices(){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID, ");
		sb.append(" ID as VALUE, ");
		sb.append(" DIVISION AS TEXT, ");
		sb.append(" DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" LKUP_DIVISIONS ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE='Y' ");
		sb.append(" ORDER BY DIVISION ");
		return sb.toString();
	}

	public static String getDivision(String type, int typeid) {
		if (!Operator.hasValue(type)) { return ""; }
		if (typeid < 1) { return ""; }
		TypeInfo t = EntityAgent.getEntity(type, typeid);
		int lsoid = t.getEntityid();
		int parentid = t.getParentid();
		int grandparentid = t.getGrandparentid();
		return getDivision(lsoid, parentid, grandparentid);
	}

	public static String getDivision(int lsoid, int parentid, int grandparentid) {
		if (lsoid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH S AS ( ");
		sb.append(" 	SELECT * FROM LKUP_DIVISIONS_TYPE WHERE ACTIVE = 'Y' ");
		sb.append(" ) ");
		sb.append(" , DQ AS ( ");
		sb.append(getDivision(lsoid));
		if (parentid > 0) {
			sb.append(" UNION ");
			sb.append(getDivision(parentid));
		}
		if (grandparentid > 0) {
			sb.append(" UNION ");
			sb.append(getDivision(grandparentid));
		}
		sb.append(" ) ");
		sb.append(" , Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	CASE WHEN DQ.ID IS NULL THEN S.ID * -1 ELSE DQ.ID END AS ID ");
		sb.append(" 	, ");
		sb.append(" 	DQ.LKUP_DIVISIONS_GROUP_ID ");
		sb.append(" 	, ");
		sb.append(" 	S.ID AS LKUP_DIVISIONS_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	DQ.LKUP_DIVISIONS_ID ");
		sb.append(" 	, ");
		sb.append(" 	DQ.LSO_ID ");
		sb.append(" 	, ");
		sb.append(" 	COALESCE(DQ.GROUP_NAME, 'Default') AS GROUP_NAME ");
		sb.append(" 	, ");
		sb.append(" 	S.TYPE ");
		sb.append(" 	, ");
		sb.append(" 	DQ.DIVISION ");
		sb.append(" 	, ");
		sb.append(" 	DQ.DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	DQ.INFO ");
		sb.append(" 	, ");
		sb.append(" 	DQ.URL ");
		sb.append(" 	, ");
		sb.append(" 	S.REQUIRED ");
		sb.append(" 	, ");
		sb.append(" 	S.ISDOT ");
		sb.append(" 	, ");
		sb.append(" 	S.DEFLT ");
		sb.append(" 	, ");
		sb.append(" 	DQ.ISPUBLIC ");
		sb.append(" 	, ");
		sb.append(" 	DQ.CREATED_BY ");
		sb.append(" 	, ");
		sb.append(" 	DQ.CREATED_DATE ");
		sb.append(" 	, ");
		sb.append(" 	DQ.UPDATED_BY ");
		sb.append(" 	, ");
		sb.append(" 	DQ.UPDATED_DATE ");
		sb.append(" 	, ");
		sb.append(" 	S.LKUP_LSO_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	DQ.SOURCE ");
		sb.append(" 	, ");
		sb.append(" 	DQ.ORDR ");
		sb.append(" FROM ");
		sb.append(" S ");
		sb.append(" LEFT OUTER JOIN DQ ON DQ.LKUP_DIVISIONS_TYPE_ID = S.ID ");
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT * FROM Q ORDER BY TYPE, ORDR ");
		return sb.toString();
	}

	public static String getDivision(int lsoid) {
		if (lsoid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" 	RD.ID ");
		sb.append(" 	, ");
		sb.append(" 	T.LKUP_DIVISIONS_GROUP_ID ");
		sb.append(" 	, ");
		sb.append(" 	D.LKUP_DIVISIONS_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	RD.LKUP_DIVISIONS_ID ");
		sb.append(" 	, ");
		sb.append(" 	RD.LSO_ID ");
		sb.append(" 	, ");
		sb.append(" 	G.GROUP_NAME ");
		sb.append(" 	, ");
		sb.append(" 	T.TYPE ");
		sb.append(" 	, ");
		sb.append(" 	D.DIVISION ");
		sb.append(" 	, ");
		sb.append(" 	D.DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	D.INFO ");
		sb.append(" 	, ");
		sb.append(" 	D.URL ");
		sb.append(" 	, ");
		sb.append(" 	T.REQUIRED ");
		sb.append(" 	, ");
		sb.append(" 	T.ISDOT ");
		sb.append(" 	, ");
		sb.append(" 	T.DEFLT ");
		sb.append(" 	, ");
		sb.append(" 	T.ISPUBLIC ");
		sb.append(" 	, ");
		sb.append(" 	D.CREATED_BY ");
		sb.append(" 	, ");
		sb.append(" 	D.CREATED_DATE ");
		sb.append(" 	, ");
		sb.append(" 	D.UPDATED_BY ");
		sb.append(" 	, ");
		sb.append(" 	D.UPDATED_DATE ");
		sb.append(" 	, ");
		sb.append(" 	LT.TYPE AS LKUP_LSO_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	LT.TYPE AS SOURCE ");
		sb.append(" 	, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN LOWER(LT.TYPE) = LOWER('O') THEN 1000 ");
		sb.append(" 		WHEN LOWER(LT.TYPE) = LOWER('S') THEN 100 ");
		sb.append(" 	ELSE 10 END AS ORDR ");
		sb.append(" FROM ");
		sb.append(" 	REF_LSO_DIVISIONS AS RD ");
		sb.append(" 	JOIN LKUP_DIVISIONS AS D ON RD.LKUP_DIVISIONS_ID = D.ID AND RD.ACTIVE = 'Y' AND RD.LSO_ID = ").append(lsoid);
		sb.append(" 	JOIN LKUP_DIVISIONS_TYPE AS T ON D.LKUP_DIVISIONS_TYPE_ID = T.ID ");
		sb.append(" 	JOIN LSO AS L ON RD.LSO_ID = L.ID ");
		sb.append(" 	JOIN LKUP_LSO_TYPE AS LT ON L.LKUP_LSO_TYPE_ID = LT.ID ");
		sb.append(" 	LEFT OUTER JOIN LKUP_DIVISIONS_GROUP AS G ON T.LKUP_DIVISIONS_GROUP_ID = G.ID ");
		return sb.toString();
	}

	public static String add(int lsoid, int lkupdivid, int userid, String ip) {
		if (lsoid < 1) { return ""; }
		if (lkupdivid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_LSO_DIVISIONS ( LSO_ID, LKUP_DIVISIONS_ID, CREATED_BY, UPDATED_BY, CREATED_DATE, UPDATED_DATE ) VALUES ( ");
		sb.append(lsoid);
		sb.append(" , ");
		sb.append(lkupdivid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String deactivate(int lsoid, int divtypeid, int userid, String ip) {
		if (lsoid < 1) { return ""; }
		if (divtypeid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_LSO_DIVISIONS SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" LSO_ID = ").append(lsoid);
		sb.append(" AND ");
		sb.append(" LKUP_DIVISIONS_ID IN ( ");
		sb.append(" SELECT ID FROM LKUP_DIVISIONS WHERE LKUP_DIVISIONS_TYPE_ID = ").append(divtypeid);
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getDivision(int lsoid, int parentid, int grandparentid, int divtypeid) {
		if (lsoid < 1 || divtypeid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	LTRIM(RTRIM(D.DIVISION)) AS DIVISION ");
		sb.append(" 	, ");
		sb.append(" 	10 AS ORDR ");
		sb.append(" FROM ");
		sb.append(" 	REF_LSO_DIVISIONS AS R ");
		sb.append(" 	JOIN LKUP_DIVISIONS AS D ON R.LKUP_DIVISIONS_ID = D.ID AND R.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND R.LSO_ID = ").append(lsoid).append(" AND D.LKUP_DIVISIONS_TYPE_ID = ").append(divtypeid);
		if (parentid > 0) {
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" 	LTRIM(RTRIM(D.DIVISION)) AS DIVISION ");
			sb.append(" 	, ");
			sb.append(" 	50 AS ORDR ");
			sb.append(" FROM ");
			sb.append(" 	REF_LSO_DIVISIONS AS R ");
			sb.append(" 	JOIN LKUP_DIVISIONS AS D ON R.LKUP_DIVISIONS_ID = D.ID AND R.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND R.LSO_ID = ").append(parentid).append(" AND D.LKUP_DIVISIONS_TYPE_ID = ").append(divtypeid);
		}
		if (grandparentid > 0) {
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" 	LTRIM(RTRIM(D.DIVISION)) AS DIVISION ");
			sb.append(" 	, ");
			sb.append(" 	100 AS ORDR ");
			sb.append(" FROM ");
			sb.append(" 	REF_LSO_DIVISIONS AS R ");
			sb.append(" 	JOIN LKUP_DIVISIONS AS D ON R.LKUP_DIVISIONS_ID = D.ID AND R.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND R.LSO_ID = ").append(grandparentid).append(" AND D.LKUP_DIVISIONS_TYPE_ID = ").append(divtypeid);
		}
		sb.append(" ) ");
		sb.append(" SELECT TOP 1 * FROM Q ORDER BY ORDR");
		return sb.toString();
	}








}















