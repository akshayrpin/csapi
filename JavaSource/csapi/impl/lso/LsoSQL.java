package csapi.impl.lso;

import alain.core.utils.Operator;
import csapi.common.Table;
import csapi.utils.CsTools;

public class LsoSQL {

	public static String getLso(int lsoid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM LSO WHERE ID = ").append(lsoid);
		return sb.toString();
	}

	public static String info(String type, int typeid, int groupid) {
		return details(type, typeid, groupid);
	}

	public static String summary(String type, int typeid, int groupid) {
		return details(type, typeid, groupid);
	}

	public static String details(String type, int typeid, int groupid){
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( SELECT COUNT(DISTINCT ID) AS CONTENT_COUNT FROM CONTENT WHERE ACTIVE = 'Y' AND LOWER(TYPE) = 'lso') ");
		sb.append(" SELECT ");
		sb.append("   L.ID, ");
		sb.append("   L.LKUP_LSO_TYPE_ID, ");
		sb.append("   L.UNIT, ");
		sb.append("   L.DESCRIPTION, ");
		sb.append("   CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(S.PRE_DIR, '') + ' ' + S.STR_NAME + ' ' + S.STR_TYPE + ' ' + COALESCE(L.UNIT, '') AS ADDRESS, ");
		sb.append("   L.CITY, ");
		sb.append("   L.STATE, ");
		sb.append("   L.ZIP, ");
		sb.append("   L.CREATED_DATE, ");
		sb.append("   L.UPDATED_DATE, ");
		sb.append("   CASE ");
		sb.append("     WHEN Q.CONTENT_COUNT IS NULL THEN '' ");
		sb.append("     WHEN Q.CONTENT_COUNT > 0 THEN 'lso' ");
		sb.append("   ELSE '' END AS CONTENT_TYPE, ");
		sb.append("   CASE ");
		sb.append("     WHEN ");
		sb.append("       L.PRIMARY_ID >0 AND PARENT_ID>0 THEN 'N' ");
		sb.append("     ELSE 'Y' END AS PRMARY, ");
		sb.append("   L.ACTIVE ");
		sb.append(" FROM ");
		sb.append("   LSO AS L ");
		sb.append("   JOIN LKUP_LSO_TYPE AS LT ON L.LKUP_LSO_TYPE_ID = LT.ID ");
		sb.append("   JOIN LSO_STREET AS S ON L.LSO_STREET_ID = S.ID ");
		sb.append("   LEFT OUTER JOIN Q ON 1 = 1 ");
		sb.append(" WHERE L.ID = ").append(typeid).append(" ");

		return sb.toString();
	}

	public static String type(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append("   L.ID, ");
		sb.append("   CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(S.PRE_DIR, '') + ' ' + S.STR_NAME + ' ' + S.STR_TYPE AS TITLE  ");
		sb.append(" FROM ");
		sb.append("   LSO AS L ");
		sb.append("   JOIN LSO_STREET S ON L.LSO_STREET_ID = S.ID ");
		sb.append(" WHERE ");
		sb.append("   L.ID = ").append(id);
		return sb.toString();
	}

	public static String browse(String id, String active) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH H AS ( ");
		sb.append("   SELECT ");
		sb.append("     H.LSO_ID ");
		sb.append("     , ");
		sb.append("     HT.TYPE AS HOLD ");
		sb.append("     , ");
		sb.append("     COUNT(H.ID) as NUM_HOLDS  "); 
		sb.append("   FROM ");
		sb.append("     LSO AS L ");
		sb.append("     JOIN REF_LSO_HOLDS AS H ON L.ID = H.LSO_ID ");
		sb.append("     JOIN LKUP_HOLDS_TYPE AS HT ON HT.ID = H.LKUP_HOLDS_TYPE_ID AND HT.ACTIVE = 'Y' AND HT.TYPE IN ('H','S') ");
		sb.append("     JOIN LKUP_HOLDS_STATUS AS HS ON HS.ID = H.LKUP_HOLDS_STATUS_ID AND HS.ACTIVE = 'Y' AND HS.STATUS = 'A' ");
		sb.append("   WHERE ");
		sb.append("     L.PARENT_ID = ").append(id);
		sb.append("   GROUP BY ");
		sb.append("     H.LSO_ID ");
		sb.append("     , ");
		sb.append("     HT.TYPE ");
		sb.append(" ), ");
		sb.append(" Q AS ( ");
		sb.append("   SELECT ");
		sb.append("     L1.ID ");
		sb.append("     , ");
		sb.append("     L1.PARENT_ID ");
		sb.append("     , ");
		sb.append("     CASE ");
		sb.append("       WHEN L2.PARENT_ID IS NOT NULL AND L2.PARENT_ID > 0 THEN L2.PARENT_ID ");
		sb.append("       WHEN L1.PARENT_ID IS NOT NULL AND L1.PARENT_ID > 0 THEN L1.PARENT_ID ");
		sb.append("       ELSE L1.ID END AS ROOT ");
		sb.append("   FROM ");
		sb.append("     LSO AS L1 ");
		sb.append("     LEFT OUTER JOIN LSO AS L2 ON L1.PARENT_ID = L2.ID ");
		sb.append("   WHERE ");
		sb.append("     L1.ID = ").append(id);
		sb.append(" ) ");
		sb.append("   SELECT ");
		sb.append("     L.ID ");
		sb.append("     , ");
		sb.append("     L.STR_NO ");
		sb.append("     , ");
		sb.append("     L.STR_MOD ");
		sb.append("     , ");
		sb.append("     S.PRE_DIR ");
		sb.append("     , ");
		sb.append("     S.STR_NAME ");
		sb.append("     , ");
		sb.append("     S.STR_TYPE ");
		sb.append("     , ");
		sb.append("     L.UNIT ");
		sb.append("     , ");
		sb.append("     L.DESCRIPTION ");
		sb.append("     , ");
		sb.append("     HH.NUM_HOLDS AS HARD_HOLDS ");
		sb.append("     , ");
		sb.append("     HS.NUM_HOLDS AS SOFT_HOLDS ");
		sb.append("     , ");
		sb.append("     CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(L.STR_MOD, '') + ' ' + COALESCE(S.PRE_DIR, '') + ' ' + S.STR_NAME + ' ' + S.STR_TYPE + ' ' + COALESCE(L.UNIT, '') AS ADDRESS ");
		sb.append("     , ");
		sb.append("     COUNT(C.ID) AS CHILDREN ");
		sb.append("     , ");
		
		sb.append( " CASE WHEN Q.ID=L.PARENT_ID THEN Q.ID ELSE Q.PARENT_ID END AS PARENT_ID  ");
		//sb.append("     Q.PARENT_ID AS PARENT_ID ");
		sb.append("     , ");
		sb.append("     CASE ");
		sb.append("       WHEN Q.ROOT = L.ID THEN -1 ");
		sb.append("       WHEN Q.ROOT = Q.PARENT_ID THEN -1 ");
		sb.append("       ELSE Q.ROOT END AS GRANDPARENT_ID ");
		sb.append("     , ");
		sb.append("     CASE ");
		sb.append("       WHEN Q.ROOT = L.ID THEN 'ROOT' ");
		sb.append("       WHEN L.PARENT_ID = ").append(id).append(" THEN 'ITEM' ");
		sb.append("       ELSE 'CURRENT' END AS NODE ");
		sb.append("     , ");
		sb.append("     CASE ");
		sb.append("       WHEN L.ID = ").append(id).append(" THEN 'Y' ");
		sb.append("       ELSE 'N' END AS CURR ");
		sb.append("   FROM ");
		sb.append("     LSO AS L ");
		sb.append("     JOIN LSO_STREET AS S ON L.LSO_STREET_ID = S.ID AND L.PRIMARY_ID < 0");
		sb.append("     LEFT OUTER JOIN LSO AS C ON L.ID = C.PARENT_ID AND C.PRIMARY_ID < 0 AND C.ACTIVE='").append(active).append("'    ");
		sb.append("     LEFT OUTER JOIN Q ON L.ID = Q.ROOT OR L.ID = Q.ID ");
		sb.append("     LEFT OUTER JOIN H AS HH ON L.ID = HH.LSO_ID AND HH.HOLD = 'H' ");
		sb.append("     LEFT OUTER JOIN H AS HS ON L.ID = HS.LSO_ID AND HS.HOLD = 'S' ");
		sb.append("   WHERE ");
		sb.append("     L.ACTIVE = '").append(active).append("' ");
		sb.append("     AND ");
		sb.append("     ( ");
		sb.append("       L.PARENT_ID = ").append(id);
		sb.append("       OR ");
		sb.append("       L.ID = ").append(id);
		sb.append("       OR ");
		sb.append("       Q.ROOT = L.ID ");
		sb.append("     ) ");
		sb.append(" GROUP BY ");
		sb.append("     L.ID ");
		sb.append("     , ");
		sb.append("     L.STR_NO ");
		sb.append("     , ");
		sb.append("     L.STR_MOD ");
		sb.append("     , ");
		sb.append("     S.PRE_DIR ");
		sb.append("     , ");
		sb.append("     S.STR_NAME ");
		sb.append("     , ");
		sb.append("     S.STR_TYPE ");
		sb.append("     , ");
		sb.append("     L.UNIT ");
		sb.append("     , ");
		sb.append("     L.DESCRIPTION ");
		sb.append("     , ");
		sb.append("     HH.NUM_HOLDS ");
		sb.append("     , ");
		sb.append("     HS.NUM_HOLDS ");
		sb.append("     , ");
		sb.append("     L.PARENT_ID ");
		sb.append("     , ");
		sb.append("     Q.PARENT_ID ");
		sb.append("     , ");
		sb.append("     Q.ROOT ");
		sb.append("     , ");
		sb.append("     Q.ID ");
		sb.append(" ORDER BY ");
		sb.append("    S.STR_NAME, S.STR_TYPE, S.PRE_DIR, L.STR_NO, L.STR_MOD, L.UNIT ");
		return sb.toString();
	}

	public static String getChildren(int id,String active){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT CAST(L.STR_NO as VARCHAR(25)) +CASE WHEN S.PRE_DIR IS NOT NULL THEN ' '+S.PRE_DIR ELSE '' END+' '+S.STR_NAME+CASE WHEN S.STR_TYPE IS NOT NULL THEN ' '+S.STR_TYPE ELSE '' END+' '+CASE WHEN L.UNIT IS NOT NULL THEN ' '+L.UNIT ELSE '' END as TITLE,L.* ");
		//sb.append(" where ID = ").append(id).append(" OR (PARENT_ID=").append(id).append(" and LKUP_LSO_TYPE_ID=").append(type).append(")");
		
		sb.append(" , STUFF((select DISTINCT  ','+CONVERT(VARCHAR(100),SP.STR_NO)+' '+LS.STR_NAME "); 
		sb.append(" FROM ").append(Table.LSOTABLE).append(" SP join ").append(Table.LSOSTREETTABLE).append(" LS on SP.LSO_STREET_ID = LS.ID  ");
		sb.append(" WHERE SP.PRIMARY_ID < 1 AND SP.STR_NO=L.STR_NO and L.LSO_STREET_ID=SP.LSO_STREET_ID  and L.ID= SP.PARENT_ID and LS.ACTIVE='Y' for xml path('')),1,1,'') as CHILDRENS ");
		
		sb.append(" FROM ").append(Table.LSOTABLE).append(" L ");
		sb.append(" JOIN ").append(Table.LSOSTREETTABLE).append(" S on L.LSO_STREET_ID = S.ID");
		sb.append(" WHERE ( L.ID = ").append(id).append(" OR PARENT_ID = ").append(id).append(" )");
		sb.append(" AND L.ACTIVE = '").append(Operator.sqlEscape(active)).append("'");
		sb.append(" AND L.PRIMARY_ID < 1 ");
		
		sb.append("  ORDER BY L.UNIT, L.STR_NO ");

		return sb.toString();
	}
	
	
	public static String getDetails(int id,String active){
		StringBuilder sb = new StringBuilder();
		sb.append(" select L.ID,L.LKUP_LSO_TYPE_ID,L.UNIT,L.DESCRIPTION, ");
		sb.append(" CAST(L.STR_NO as VARCHAR(25)) +CASE WHEN S.PRE_DIR IS NOT NULL THEN ' '+S.PRE_DIR ELSE '' END+' '+S.STR_NAME+CASE WHEN S.STR_TYPE IS NOT NULL THEN ' '+S.STR_TYPE ELSE '' END as ADDRESS ");
		sb.append(" ,L.CITY,L.STATE,L.ZIP,L.CREATED_DATE,L.UPDATED_DATE,CASE WHEN L.PRIMARY_ID >0 AND PARENT_ID>0 THEN 'N' ELSE 'Y' END as PRIMAR,L.ACTIVE ");
		sb.append(" from ").append(Table.LSOTABLE).append(" L  ");
		sb.append(" join ").append(Table.LKUPLSOTYPETABLE).append(" LT on L.LKUP_LSO_TYPE_ID=LT.ID ");
		sb.append(" join  ").append(Table.LSOSTREETTABLE).append(" S on L.LSO_STREET_ID = S.ID ");
		sb.append(" where L.ID=").append(id).append(" and L.ACTIVE='").append(Operator.sqlEscape(active)).append("' ");

		return sb.toString();
	}
	
	
	public static String getDivisionsDetails(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" select LTT.ID,LTT.DESCRIPTION as FIELD,LT.DIVISION as FIELD_VALUE ");
		sb.append(" from REF_LSO_DIVISIONS R  ");
		sb.append(" JOIN  LKUP_DIVISIONS  LT on R.LKUP_DIVISION_ID = LT.ID  ");
		sb.append(" JOIN  LKUP_DIVISIONS_TYPE  LTT on LT.LKUP_DIVISION_TYPE_ID = LTT.ID  ");
		sb.append(" where LSO_ID=").append(id).append(" and LTT.ACTIVE='Y' and  R.ACTIVE='Y' and LT.ACTIVE='Y' ");
	
		return sb.toString();
	}
	
	
	
	/*public static String getSetBack(int id,int typeId){
		StringBuilder sb = new StringBuilder();
		
		sb.append(" select TOP 2 ");
		sb.append(" LSB.START_DATE ");
		sb.append(" from  ");
		sb.append(" ").append(Table.LSOSITEDATATABLE).append("  LSD  ");
		sb.append(" join ").append(Table.LSOTABLE).append("  L on LSD.LSO_ID=L.ID  ");
		sb.append(" join ").append(Table.LSOSETBACKTABLE).append(" LSB on LSD.ID=LSB.LSO_SITEDATA_ID  ");
		sb.append(" where LSO_ID=").append(id).append(" AND LSD.ACTIVE='Y' and LSB.ACTIVE='Y' and L.LKUP_LSO_TYPE_ID=").append(typeId).append(" ");
		sb.append(" group by LSB.START_DATE ");
		sb.append(" order by LSB.START_DATE DESC ");
		
		return sb.toString();
	}*/
	
	
	public static String getSetBack(int id,int typeId){
		StringBuilder sb = new StringBuilder();
		
		sb.append(" WITH Q AS ( ");
		sb.append(" select TOP 1 ");
		sb.append(" LSB.START_DATE ");
		sb.append(" from  ");
		sb.append(" ").append(Table.LSOSITEDATATABLE).append("  LSD  ");
		sb.append(" join ").append(Table.LSOTABLE).append("  L on LSD.LSO_ID=L.ID  ");
		sb.append(" join ").append(Table.LSOSETBACKTABLE).append(" LSB on LSD.ID=LSB.LSO_SITEDATA_ID  ");
		sb.append(" where LSO_ID=").append(id).append(" AND LSD.ACTIVE='Y' and LSB.ACTIVE='Y' and L.LKUP_LSO_TYPE_ID=").append(typeId).append(" ");
		sb.append(" group by LSB.START_DATE ");
		sb.append(" order by LSB.START_DATE DESC ");
		sb.append("  ");
		sb.append(" 	) ");
		sb.append(" 	select  ");
		sb.append(" LSB.ID,LLST.ID,LLST.TYPE, LSB.FT,LSB.INCHES,LSB.COMMENTS,LSB.REQ_FT,LSB.REQ_INCHES,LSB.REQ_COMMENT,LSB.START_DATE ");
		sb.append(" from  ");
		sb.append(" ").append(Table.LSOSITEDATATABLE).append("  LSD "); 
		sb.append(" join ").append(Table.LSOTABLE).append("  L on LSD.LSO_ID=L.ID "); 
		sb.append(" join ").append(Table.LSOSETBACKTABLE).append("  LSB on LSD.ID=LSB.LSO_SITEDATA_ID "); 
		sb.append(" join ").append(Table.LKUPLSOSETBACKTYPETABLE).append("  LLST on LSB.LKUP_LSO_SETBACK_ID=LLST.ID ");
		sb.append(" join Q on Q.START_DATE=LSB.START_DATE ");
		sb.append(" where LSO_ID=").append(id).append(" AND LSD.ACTIVE='Y' and LSB.ACTIVE='Y' and L.LKUP_LSO_TYPE_ID=").append(typeId).append(" and LLST.ACTIVE='Y' ");
		sb.append("  ");
		sb.append(" group by LLST.TYPE, LSB.FT,LSB.INCHES,LSB.COMMENTS,LSB.REQ_FT,LSB.REQ_INCHES,LSB.REQ_COMMENT,LSB.START_DATE,LSB.ID,LLST.ID ");
		sb.append(" order by LSB.START_DATE DESC,LLST.ID ");

		return sb.toString();

	}
	
	
	/*public static String getSetBack(int id,int typeId,String date){
		StringBuilder sb = new StringBuilder();
		sb.append(" 	select  ");
		sb.append(" LSB.ID,LLST.ID,LLST.TYPE, LSB.FT,LSB.INCHES,LSB.COMMENTS,LSB.REQ_FT,LSB.REQ_INCHES,LSB.REQ_COMMENT,LSB.START_DATE ");
		sb.append(" from  ");
		sb.append(" ").append(Table.LSOSITEDATATABLE).append("  LSD "); 
		sb.append(" join ").append(Table.LSOTABLE).append("  L on LSD.LSO_ID=L.ID "); 
		sb.append(" join ").append(Table.LSOSETBACKTABLE).append("  LSB on LSD.ID=LSB.LSO_SITEDATA_ID "); 
		sb.append(" join ").append(Table.LKUPLSOSETBACKTYPETABLE).append("  LLST on LSB.LKUP_LSO_SETBACK_ID=LLST.ID ");
		sb.append(" where LSO_ID=").append(id).append(" AND LSD.ACTIVE='Y' and LSB.ACTIVE='Y' and L.LKUP_LSO_TYPE_ID=").append(typeId).append(" and LLST.ACTIVE='Y' ");
		sb.append(" AND LSB.START_DATE='").append(date).append("' ");
	
		return sb.toString();

	}*/
	
	public static String getMod(int selected) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" STR_MOD AS ID, ");
		sb.append(" STR_MOD AS VALUE, ");
		if (selected > 0) {
			sb.append(" , ");
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		sb.append(" STR_MOD AS TEXT ");
		sb.append(" FROM ");
		sb.append(" LSO ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY STR_MOD ");
		return sb.toString();
	}


	public static String getStreets(int selected) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" ID, ");
		sb.append(" ID AS VALUE, ");
		if (selected > 0) {
			sb.append(" , ");
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		sb.append(" CASE ");
		sb.append(" 	WHEN PRE_DIR IS NOT NULL THEN PRE_DIR ");
		sb.append(" 	ELSE '' END ");
		sb.append(" + ' ' + STR_NAME + ");
		sb.append(" CASE ");
		sb.append(" 	WHEN STR_TYPE IS NOT NULL THEN ' ' + STR_TYPE ");
		sb.append(" 	ELSE '' ");
		sb.append(" END AS TEXT, ");
		sb.append(" STR_NAME, ");
		sb.append(" PRE_DIR, ");
		sb.append(" STR_TYPE ");
		sb.append(" FROM ");
		sb.append(" LSO_STREET ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY STR_NAME, PRE_DIR, STR_TYPE ");
		return sb.toString();
	}

	public static String search(int lsoid, int strno, String mod, int strid, String unit) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	V.PRIMARY_ID AS ID, ");
		sb.append(" 	V.PRIMARY_ID AS VALUE, ");
		sb.append(" 	CAST(V.STR_NO AS VARCHAR(10)) + ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN V.STR_MOD IS NOT NULL THEN ' ' + V.STR_MOD ");
		sb.append(" 			ELSE '' END + ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN V.PRE_DIR IS NOT NULL THEN ' ' + V.PRE_DIR ");
		sb.append(" 			ELSE '' END + ");
		sb.append(" 		' ' + V.STR_NAME + ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN V.STR_TYPE IS NOT NULL THEN ' ' + V.STR_TYPE ");
		sb.append(" 			ELSE '' END + ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN V.UNIT IS NOT NULL AND V.UNIT <> '' THEN ' ' + V.UNIT ");
		sb.append(" 			ELSE '' ");
		sb.append(" 	END AS TEXT, ");
		sb.append(" 	L.DESCRIPTION, ");
		sb.append(" 	V.APN, ");
		sb.append(" 	V.LSO_TYPE, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN V.LSO_TYPE = 'L' THEN 0 ");
		sb.append(" 		WHEN V.LSO_TYPE = 'S' THEN 10 ");
		sb.append(" 	ELSE 20 END AS ORDR, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN V.LSO_TYPE = 'L' THEN 'Land' ");
		sb.append(" 		WHEN V.LSO_TYPE = 'S' THEN 'Structure' ");
		sb.append(" 		WHEN V.LSO_TYPE = 'O' THEN 'Occupancy' ");
		sb.append(" 	ELSE '' END AS TYPE, ");
		sb.append(" 	V.STR_NO, ");
		sb.append(" 	V.STR_NAME, ");
		sb.append(" 	V.PRE_DIR, ");
		sb.append(" 	V.STR_TYPE, ");
		sb.append(" 	V.UNIT, ");
		sb.append(" 	V.CITY ");
		sb.append(" FROM ");
		sb.append(" 	V_CENTRAL_ADDRESS AS V ");
		sb.append(" 	JOIN LSO AS L ON V.PRIMARY_ID = L.ID ");
		sb.append(" WHERE ");
		if (lsoid > 0) {
			sb.append(" V.LSO_ID = ").append(lsoid);
		}
		else {
			sb.append(" 	V.LSO_STREET_ID = ").append(strid).append(" ");
			sb.append(" 	AND ");
			sb.append(" 	V.STR_NO = ").append(strno);
			if (Operator.hasValue(mod)) {
				sb.append(" AND LOWER(V.STR_MOD) = LOWER('").append(Operator.sqlEscape(mod)).append("') ");
			}
			if (Operator.hasValue(unit)) {
				sb.append(" AND LOWER(V.UNIT) = LOWER('").append(Operator.sqlEscape(unit)).append("') ");
			}
		}
		sb.append(" ORDER BY ORDR, V.STR_NAME, V.PRE_DIR, V.STR_TYPE, V.STR_NO, V.UNIT ");
		return sb.toString();
	}

	public static String psearch1(String query, int page, int resultsperpage) {
		if (!Operator.hasValue(query)) { return ""; }
		if (page < 1) { page = 1; }
		int offset = (resultsperpage * page) - resultsperpage;
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" A.LSO_ID AS ID ");
		sb.append(" , ");
		sb.append(" A.LSO_ID AS VALUE ");
		sb.append(" , ");
		sb.append(" A.ADDRESS AS TEXT ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" A.DERIVED_APN AS APN ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN A.LSO_TYPE = 'L' THEN 'LAND' ");
		sb.append("   WHEN A.LSO_TYPE = 'S' THEN 'STRUCTURE' ");
		sb.append("   WHEN A.LSO_TYPE = 'O' THEN 'OCCUPANCY' ");
		sb.append(" ELSE '' END AS LSO_TYPE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN A.LSO_TYPE = 'L' THEN 10 ");
		sb.append("   WHEN A.LSO_TYPE = 'S' THEN 20 ");
		sb.append("   WHEN A.LSO_TYPE = 'O' THEN 30 ");
		sb.append(" ELSE 50 END AS ORDR ");
		sb.append(" FROM ");
		sb.append(" V_CENTRAL_ADDRESS AS A ");
		sb.append(" WHERE ");
		sb.append(" A.ACTIVE = 'Y' ");
		String[] arr = Operator.split(query, " ");
		for (int i=0; i<arr.length; i++) {
			String s = arr[i];
			if (Operator.isNumber(s)) {
				sb.append(" AND ");
				sb.append(" '").append(s).append("' IN ( A.DERIVED_APN, A.ZIP, CAST(A.STR_NO AS VARCHAR(10))) ");
			}
			else {
				String abb = CsTools.abbreviateAddress(s);
				sb.append(" AND ");
				sb.append(" LOWER(A.ADDRESS) LIKE '%").append(abb.toLowerCase()).append("%' ");
			}
		}
		sb.append(" ), ");
		sb.append(" C AS ( SELECT COUNT(*) AS RESULTS FROM Q ) ");
		sb.append(" SELECT ");
		sb.append(" C.RESULTS, ");
		sb.append(" Q.* ");
		sb.append(" FROM ");
		sb.append(" Q ");
		sb.append(" JOIN C ON 1 = 1 ");
		sb.append(" ORDER BY ORDR ");
		sb.append(" OFFSET ").append(offset).append(" ROWS FETCH NEXT ").append(resultsperpage).append(" ROWS ONLY ");
		return sb.toString();
	}

	public static String psearch(String query, int page, int resultsperpage) {
		if (!Operator.hasValue(query)) { return ""; }
		if (page < 1) { page = 1; }
		int offset = (resultsperpage * page) - resultsperpage;
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH SRCH AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		L.ID ");
		sb.append(" 		, ");
		sb.append(" 		LTRIM(RTRIM( ");
		sb.append(" 			CAST(L.STR_NO AS VARCHAR(25)) + ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN L.STR_MOD IS NOT NULL AND L.STR_MOD <> '' THEN ' ' + L.STR_MOD ");
		sb.append(" 				ELSE '' ");
		sb.append(" 			END + ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN S.PRE_DIR IS NOT NULL AND S.PRE_DIR <> '' THEN ' ' + S.PRE_DIR ");
		sb.append(" 				ELSE '' ");
		sb.append(" 			END + ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN S.STR_NAME IS NOT NULL AND S.STR_NAME <> '' THEN ' ' + S.STR_NAME ");
		sb.append(" 				ELSE '' ");
		sb.append(" 			END + ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN S.STR_TYPE IS NOT NULL AND S.STR_TYPE <> '' THEN ' ' + S.STR_TYPE ");
		sb.append(" 				ELSE '' ");
		sb.append(" 			END + ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN S.SUF_DIR IS NOT NULL AND S.SUF_DIR <> '' THEN ' ' + S.SUF_DIR ");
		sb.append(" 				ELSE '' ");
		sb.append(" 			END + ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN L.UNIT IS NOT NULL AND L.UNIT <> '' THEN ' ' + L.UNIT ");
		sb.append(" 				ELSE '' ");
		sb.append(" 			END ");
		sb.append(" 		)) AS TEXT ");
		sb.append(" 		, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN L.PRIMARY_ID < 1 THEN L.ID ");
		sb.append(" 		ELSE L.PRIMARY_ID END AS PRIMARY_ID ");
		sb.append(" 	FROM ");
		sb.append(" 		LSO AS L ");
		sb.append(" 		JOIN LSO_STREET AS S ON L.LSO_STREET_ID = S.ID AND L.ACTIVE = 'Y' ");
		sb.append(" 		LEFT OUTER JOIN LKUP_LSO_TYPE AS T ON L.LKUP_LSO_TYPE_ID = T.ID ");
		sb.append(" 		LEFT OUTER JOIN REF_LSO_APN AS A ON L.ID = A.LSO_ID AND A.ACTIVE = 'Y' ");

		sb.append(" 		LEFT OUTER JOIN LSO AS P ON L.PARENT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append(" 		LEFT OUTER JOIN REF_LSO_APN AS PA ON P.ID = PA.LSO_ID AND PA.ACTIVE = 'Y' ");

		sb.append(" 		LEFT OUTER JOIN LSO AS GP ON P.PARENT_ID = GP.ID AND GP.ACTIVE = 'Y' ");
		sb.append(" 		LEFT OUTER JOIN REF_LSO_APN AS GPA ON GP.ID = GPA.LSO_ID AND GPA.ACTIVE = 'Y' ");

		sb.append(" 	WHERE ");
		sb.append(" 		L.ACTIVE = 'Y' ");



		boolean empty = true;
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		String[] arr = Operator.split(query, " ");
		for (int i=0; i<arr.length; i++) {
			String s = arr[i];
			String abb = CsTools.abbreviateAddress(s);

			if (!empty) {
				sb1.append(" 		AND ");
				sb2.append(" 		AND ");
			}
			if (Operator.equalsIgnoreCase(abb, "N") || Operator.equalsIgnoreCase(abb, "W") || Operator.equalsIgnoreCase(abb, "E") || Operator.equalsIgnoreCase(abb, "S")) {
				sb1.append(" 		COALESCE(S.PRE_DIR, '").append(Operator.sqlEscape(abb.toLowerCase())).append("') = '").append(Operator.escape(abb.toLowerCase())).append("' ");
				sb2.append(" 		COALESCE(S.PRE_DIR, '").append(Operator.sqlEscape(abb.toLowerCase())).append("') = '").append(Operator.escape(abb.toLowerCase())).append("' ");
			}
			else {
				sb1.append(" 		'").append(Operator.escape(abb.toLowerCase())).append("' IN (CAST(L.STR_NO AS VARCHAR(10)), LOWER(L.STR_MOD), LOWER(S.PRE_DIR), LOWER(S.STR_NAME), LOWER(S.STR_TYPE), LOWER(S.SUF_DIR), LOWER(A.APN), LOWER(PA.APN), LOWER(GPA.APN))  ");
				sb2.append("		CAST(L.STR_NO AS VARCHAR(25)) + ' ' + COALESCE(LOWER(L.STR_MOD), '') + ' ' + COALESCE(LOWER(PRE_DIR), '') + ' ' + LOWER(S.STR_NAME)  + ' ' + COALESCE(LOWER(S.STR_TYPE), '') LIKE LOWER('%").append(Operator.escape(abb.toLowerCase())).append("%') ");
			}

			empty = false;
		}

		sb.append(" 	AND ");
		sb.append(" 	( ");
		sb.append(" 		( ");
		sb.append(sb1.toString());
		sb.append(" 		) ");
		sb.append(" 		OR ");
		sb.append(" 		( ");
		sb.append(sb2.toString());
		sb.append(" 		) ");
		sb.append(" 	) ");

		sb.append(" ), ");
		sb.append(" Q AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		L.ID ");
		sb.append(" 		, ");
		sb.append(" 		L.ID AS VALUE ");
		sb.append(" 		, ");
		sb.append(" 		LTRIM(RTRIM( ");
		sb.append(" 			CAST(L.STR_NO AS VARCHAR(25)) + ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN L.STR_MOD IS NOT NULL AND L.STR_MOD <> '' THEN ' ' + L.STR_MOD ");
		sb.append(" 				ELSE '' ");
		sb.append(" 			END + ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN S.PRE_DIR IS NOT NULL AND S.PRE_DIR <> '' THEN ' ' + S.PRE_DIR ");
		sb.append(" 				ELSE '' ");
		sb.append(" 			END + ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN S.STR_NAME IS NOT NULL AND S.STR_NAME <> '' THEN ' ' + S.STR_NAME ");
		sb.append(" 				ELSE '' ");
		sb.append(" 			END + ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN S.STR_TYPE IS NOT NULL AND S.STR_TYPE <> '' THEN ' ' + S.STR_TYPE ");
		sb.append(" 				ELSE '' ");
		sb.append(" 			END + ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN S.SUF_DIR IS NOT NULL AND S.SUF_DIR <> '' THEN ' ' + S.SUF_DIR ");
		sb.append(" 				ELSE '' ");
		sb.append(" 			END + ");
		sb.append(" 			CASE ");
		sb.append(" 				WHEN L.UNIT IS NOT NULL AND L.UNIT <> '' THEN ' ' + L.UNIT ");
		sb.append(" 				ELSE '' ");
		sb.append(" 			END ");
		sb.append(" 		)) AS TEXT ");
		sb.append(" 		, ");
		sb.append(" 		L.DESCRIPTION ");
		sb.append(" 		, ");
		sb.append(" 		L.STR_NO ");
		sb.append(" 		, ");
		sb.append(" 		S.PRE_DIR ");
		sb.append(" 		, ");
		sb.append(" 		S.STR_NAME ");
		sb.append(" 		, ");
		sb.append(" 		S.STR_TYPE ");
		sb.append(" 		, ");
		sb.append(" 		L.UNIT ");
		sb.append(" 		, ");
		sb.append(" 		SRCH.TEXT AS ALIAS ");
		sb.append(" 		, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN T.TYPE = 'L' THEN 'LAND' ");
		sb.append(" 			WHEN T.TYPE = 'S' THEN 'STRUCTURE' ");
		sb.append(" 			WHEN T.TYPE = 'O' THEN 'OCCUPANCY' ");
		sb.append(" 		ELSE '' END AS LSO_TYPE ");
		sb.append(" 		, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN T.TYPE = 'L' THEN 10 ");
		sb.append(" 			WHEN T.TYPE = 'S' THEN 20 ");
		sb.append(" 			WHEN T.TYPE = 'O' THEN 30 ");
		sb.append(" 		ELSE 50 END AS ORDR ");
		sb.append(" 		, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN A.APN IS NOT NULL AND A.APN <> '' THEN A.APN ");
		sb.append(" 			WHEN PA.APN IS NOT NULL AND PA.APN <> '' THEN PA.APN ");
		sb.append(" 			WHEN GPA.APN IS NOT NULL AND GPA.APN <> '' THEN GPA.APN ");
		sb.append(" 		ELSE '' END AS APN ");
		sb.append(" 	FROM ");
		sb.append(" 		SRCH ");
		sb.append(" 		JOIN ( ");
		sb.append(" 			LSO AS L ");
		sb.append(" 			JOIN LSO_STREET AS S ON L.LSO_STREET_ID = S.ID AND L.ACTIVE = 'Y' ");
		sb.append(" 			LEFT OUTER JOIN LKUP_LSO_TYPE AS T ON L.LKUP_LSO_TYPE_ID = T.ID ");
		sb.append(" 			LEFT OUTER JOIN REF_LSO_APN AS A ON L.ID = A.LSO_ID AND A.ACTIVE = 'Y' ");

		sb.append(" 			LEFT OUTER JOIN LSO AS P ON L.PARENT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append(" 			LEFT OUTER JOIN REF_LSO_APN AS PA ON P.ID = PA.LSO_ID AND PA.ACTIVE = 'Y' ");

		sb.append(" 			LEFT OUTER JOIN LSO AS GP ON P.PARENT_ID = GP.ID AND GP.ACTIVE = 'Y' ");
		sb.append(" 			LEFT OUTER JOIN REF_LSO_APN AS GPA ON GP.ID = GPA.LSO_ID AND GPA.ACTIVE = 'Y' ");

		sb.append(" 		) ON SRCH.PRIMARY_ID = L.ID ");

		sb.append(" ), ");
		sb.append(" C AS ( SELECT COUNT(*) AS RESULTS FROM Q ) ");
		sb.append(" SELECT ");
		sb.append(" C.RESULTS, ");
		sb.append(" Q.* ");
		sb.append(" FROM ");
		sb.append(" Q ");
		sb.append(" JOIN C ON 1 = 1 ");
		sb.append(" ORDER BY STR_NAME, STR_TYPE, PRE_DIR, STR_NO, UNIT, ORDR ");
		sb.append(" OFFSET ").append(offset).append(" ROWS FETCH NEXT ").append(resultsperpage).append(" ROWS ONLY ");
		return sb.toString();
	}

	public static String blocked(String type,int typeId){
		StringBuilder sb = new StringBuilder();
		if(type.equalsIgnoreCase("lso")){
			sb.append("select TOP 1 ISPUBLIC from LSO L  WHERE ID =  ").append(typeId);
		}else {
			sb.append("select TOP 1 ISPUBLIC from LSO L ");
			sb.append(" LEFT OUTER JOIN REF_LSO_PROJECT RLA on L.ID=RLA.LSO_ID ");
			sb.append(" LEFT OUTER JOIN ACTIVITY A on RLA.PROJECT_ID = A.PROJECT_ID WHERE ");
			if(type.equalsIgnoreCase("project")){
				sb.append("A.PROJECT_ID =  ").append(typeId);
			}else if(type.equalsIgnoreCase("activity")){
				sb.append("A.ID =  ").append(typeId);
	
			}
		}
		
		
		
		return sb.toString();
	}


}
















