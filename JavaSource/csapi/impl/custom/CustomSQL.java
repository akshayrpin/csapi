package csapi.impl.custom;

import alain.core.utils.Operator;
import csapi.common.Table;
import csapi.utils.CsReflect;

public class CustomSQL {

	public static String details(String type, int typeid, int groupId) {
		if (!Operator.hasValue(type)) { return ""; }
		String table = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" F.ID ");
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
		sb.append(" , ");
		sb.append(" R.ORDR AS GROUP_ORDER ");
		sb.append(" , ");
		sb.append(" F.ORDR AS FIELD_ORDER ");
		sb.append(" ,");
		sb.append(" LFT.TYPE as FIELD_TYPE ");
		sb.append(" ,");
		sb.append(" LFIT.TYPE as FIELD_ITYPE ");
		sb.append(" , ");
		sb.append(" COUNT (DISTINCT C.ID) AS CHOICES ");
		sb.append(" FROM ");
		sb.append(" ").append(table).append(" AS P ");
		sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON P.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
		sb.append(" JOIN REF_").append(tableref).append("_FIELD_GROUPS AS R ON R.LKUP_").append(tableref).append("_TYPE_ID = T.ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID AND G.ACTIVE = 'Y' ");
		sb.append(" JOIN FIELD AS F ON F.FIELD_GROUPS_ID = G.ID AND G.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN ").append(tableref).append("_FIELD_VALUE AS V ON F.ID = V.FIELD_ID AND V.ACTIVE = 'Y' AND V.").append(idref).append(" = P.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDTYPETABLE).append(" LFT ON F.LKUP_FIELD_TYPE_ID = LFT.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDITYPETABLE).append(" LFIT ON F.LKUP_FIELD_ITYPE_ID = LFIT.ID ");
		sb.append(" LEFT OUTER JOIN FIELD_CHOICES AS C ON F.ID = C.FIELD_ID AND C.ACTIVE = 'Y' ");
		
		sb.append(" WHERE ");
		sb.append(" P.ID = ").append(typeid);
		if (groupId > 0) {
			sb.append(" AND ");
			sb.append(" G.ID = ").append(groupId);
		}

		sb.append(" GROUP BY ");
		sb.append(" F.ID ");
		sb.append(" , ");
		sb.append(" F.NAME ");
		sb.append(" , ");
		sb.append(" V.VALUE ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME ");
		sb.append(" ,");
		sb.append(" G.ID ");
		sb.append(" ,");
		sb.append(" G.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" R.ORDR ");
		sb.append(" , ");
		sb.append(" F.ORDR ");
		sb.append(" ,");
		sb.append(" LFT.TYPE ");
		sb.append(" ,");
		sb.append(" LFIT.TYPE ");

		sb.append(" ORDER BY R.ORDR, G.GROUP_NAME, F.ORDR ");
		

		return sb.toString();
	}
	
	public static String details(String type, int typeid, int groupId, int setId) {
		if (!Operator.hasValue(type)) { return ""; }
		String table = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		if(groupId > 0 && setId>0){
			sb.append(" SELECT ");
			sb.append(" F.ID ");
			sb.append(" , ");
			sb.append(" F.NAME ");
			sb.append(" , ");
			sb.append(" V.VALUE ");
			sb.append(" , ");
			sb.append(" G.GROUP_NAME ");
			sb.append(" ,");
			sb.append(" G.ID as GROUP_ID ");
			sb.append(" , ");
			sb.append(" G.ISPUBLIC as GROUP_PUBLIC ");
			sb.append(" , ");
			sb.append(" R.ORDR AS GROUP_ORDER ");
			sb.append(" , ");
			sb.append(" F.ORDR AS FIELD_ORDER ");
			sb.append(" , ");
			sb.append(" LFT.TYPE as FIELD_TYPE ");
			sb.append(" , ");
			sb.append(" LFIT.TYPE as FIELD_ITYPE ");
			sb.append(" , ");
			sb.append(" COUNT (DISTINCT C.ID) AS CHOICES ");
			sb.append(" FROM ");
			sb.append(" ").append(table).append(" AS P ");
			sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON P.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
			sb.append(" JOIN REF_").append(tableref).append("_FIELD_GROUPS AS R ON R.LKUP_").append(tableref).append("_TYPE_ID = T.ID AND R.ACTIVE = 'Y' ");
			sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID AND G.ACTIVE = 'Y' ");
			sb.append(" JOIN FIELD AS F ON F.FIELD_GROUPS_ID = G.ID AND F.ACTIVE = 'Y' ");
			sb.append(" LEFT OUTER JOIN ").append(tableref).append("_FIELD_VALUE AS V ON F.ID = V.FIELD_ID AND V.ACTIVE = 'Y' AND V.").append(idref).append(" = P.ID ");
			sb.append(" LEFT OUTER JOIN LKUP_FIELD_TYPE LFT ON F.LKUP_FIELD_TYPE_ID = LFT.ID ");
			sb.append(" LEFT OUTER JOIN LKUP_FIELD_ITYPE LFIT ON F.LKUP_FIELD_ITYPE_ID = LFIT.ID ");
			sb.append(" LEFT OUTER JOIN FIELD_CHOICES AS C ON F.ID = C.FIELD_ID AND C.ACTIVE = 'Y' ");

			sb.append(" WHERE ");
			sb.append(" P.ID = ").append(typeid);
			sb.append(" AND ");
			sb.append(" G.ID = ").append(groupId);
			sb.append(" AND ");
			sb.append(" V.SET_ID = ").append(setId).append(" ");
		
			sb.append(" GROUP BY ");
			sb.append(" F.ID ");
			sb.append(" , ");
			sb.append(" F.NAME ");
			sb.append(" , ");
			sb.append(" V.VALUE ");
			sb.append(" , ");
			sb.append(" G.GROUP_NAME ");
			sb.append(" ,");
			sb.append(" G.ID ");
			sb.append(" ,");
			sb.append(" G.ISPUBLIC ");
			sb.append(" , ");
			sb.append(" R.ORDR ");
			sb.append(" , ");
			sb.append(" F.ORDR ");
			sb.append(" ,");
			sb.append(" LFT.TYPE ");
			sb.append(" ,");
			sb.append(" LFIT.TYPE ");
		}
		else if (groupId > 0  && setId==0) {
			sb.append(" SELECT ");
			sb.append(" F.ID ");
			sb.append(" , ");
			sb.append(" F.NAME ");
			sb.append(" , ");
			sb.append(" '' AS VALUE ");
			sb.append(" , ");
			sb.append(" G.GROUP_NAME ");
			sb.append(" , ");
			sb.append(" G.ID as GROUP_ID ");
			sb.append(" , ");
			sb.append(" G.ISPUBLIC as GROUP_PUBLIC ");
			sb.append(" , ");
			sb.append(" R.ORDR AS GROUP_ORDER ");
			sb.append(" , ");
			sb.append(" F.ORDR AS FIELD_ORDER ");
			sb.append(" ,");
			sb.append(" LFT.TYPE as FIELD_TYPE ");
			sb.append(" , ");
			sb.append(" LFIT.TYPE as FIELD_ITYPE ");
			sb.append(" , ");
			sb.append(" COUNT (DISTINCT C.ID) AS CHOICES ");
			sb.append(" FROM ");
			sb.append(" ").append(table).append(" AS P ");
			sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON P.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
			sb.append(" JOIN REF_").append(tableref).append("_FIELD_GROUPS AS R ON R.LKUP_").append(tableref).append("_TYPE_ID = T.ID AND R.ACTIVE = 'Y' ");
			sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID AND G.ACTIVE = 'Y' ");
			sb.append(" JOIN FIELD AS F ON F.FIELD_GROUPS_ID = G.ID AND F.ACTIVE = 'Y' ");
			//sb.append(" LEFT OUTER JOIN ").append(tableref).append("_FIELD_VALUE AS V ON F.ID = V.FIELD_ID AND V.ACTIVE = 'Y' AND V.").append(idref).append(" = P.ID ");
			sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDTYPETABLE).append(" LFT ON F.LKUP_FIELD_TYPE_ID = LFT.ID ");
			sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDITYPETABLE).append(" LFIT ON F.LKUP_FIELD_ITYPE_ID = LFIT.ID ");
			sb.append(" LEFT OUTER JOIN FIELD_CHOICES AS C ON F.ID = C.FIELD_ID AND C.ACTIVE = 'Y' ");
			
			sb.append(" WHERE ");
			sb.append(" P.ID = ").append(typeid);
			sb.append(" AND ");
			sb.append(" G.ID = ").append(groupId);

			sb.append(" GROUP BY ");
			sb.append(" F.ID ");
			sb.append(" , ");
			sb.append(" F.NAME ");
			sb.append(" , ");
			sb.append(" G.GROUP_NAME ");
			sb.append(" ,");
			sb.append(" G.ID ");
			sb.append(" ,");
			sb.append(" G.ISPUBLIC ");
			sb.append(" , ");
			sb.append(" R.ORDR ");
			sb.append(" , ");
			sb.append(" F.ORDR ");
			sb.append(" ,");
			sb.append(" LFT.TYPE ");
			sb.append(" ,");
			sb.append(" LFIT.TYPE ");

		}
		sb.append(" ORDER BY R.ORDR, G.GROUP_NAME, F.ORDR ");
		return sb.toString();
	}

	public static String getChoices(int fieldid) {
		if (fieldid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" TITLE_VALUE ");
		sb.append(" , ");
		sb.append(" TITLE AS TEXT ");
		sb.append(" , ");
		sb.append(" DEFAULT_VALUE ");
		sb.append(" , ");
		sb.append(" DEFAULT_VALUE AS SELECTED ");
		
		sb.append(" FROM ");
		sb.append(" FIELD_CHOICES ");
		sb.append(" WHERE ");
		sb.append(" FIELD_ID = ").append(fieldid).append(" ");
		sb.append(" AND ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY ORDR ");
		return sb.toString();
	}

	public static String fields(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" F.ID ");
		sb.append(" , ");
		sb.append(" F.NAME ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME ");
		sb.append(" ,");
		sb.append(" G.ID as GROUP_ID ");
		sb.append(" ,");
		sb.append(" G.ISPUBLIC as GROUP_PUBLIC ");
		sb.append(" ,");
		sb.append(" F.ORDR as FIELD_ORDER ");
		sb.append(" ,");
		sb.append(" LFT.TYPE as FIELD_TYPE ");
		sb.append(" ,");
		sb.append(" LFIT.TYPE as FIELD_ITYPE ");
		sb.append(" FROM ");
		sb.append(" ").append(Table.FIELDGROUPSTABLE).append(" AS G ");
		sb.append(" JOIN ").append(Table.FIELDTABLE).append(" F ON F.FIELD_GROUPS_ID = G.ID ");
		
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDTYPETABLE).append(" LFT ON F.LKUP_FIELD_TYPE_ID = LFT.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDITYPETABLE).append(" LFIT ON F.LKUP_FIELD_ITYPE_ID = LFIT.ID ");
		
		sb.append(" WHERE ");
		sb.append(" F.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" G.ACTIVE = 'Y'  ");
		sb.append(" AND ");
		sb.append(" G.ID = ").append(id);
		sb.append(" ORDER BY F.SET_ID DESC, F.ORDR, F.NAME ");
		return sb.toString();
	}

	public static String groups(String type, int typeid) {
		String table = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT");
		sb.append("   G.*, R.ORDR ");
		sb.append(" FROM ");
		sb.append("   ").append(table).append(" AS A ");
		sb.append("   JOIN LKUP_").append(tableref).append("_TYPE AS T ON A.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
		sb.append("   JOIN REF_").append(tableref).append("_FIELD_GROUPS AS R ON R.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
		sb.append("   JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID ");
		sb.append("   JOIN FIELD AS F ON G.ID = F.FIELD_GROUPS_ID ");
		sb.append(" WHERE ");
		sb.append("   G.ACTIVE = 'Y'  ");
		sb.append("   AND ");
		sb.append("   R.ACTIVE = 'Y'  ");
		sb.append("   AND ");
		sb.append("   A.ID = ").append(typeid);
		sb.append(" ORDER BY R.ORDR ");
		return sb.toString();
	}

	public static String summary(String type, int typeid) {
		if (!Operator.hasValue(type)) { return ""; }
		String table = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" CASE WHEN V.SET_ID >0 THEN V.SET_ID ELSE 0 END AS SET_ID ");
		sb.append(" , ");
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
		sb.append(" G.MULTI ");
		sb.append(" , ");
		sb.append(" R.ORDR AS GROUP_ORDER ");
		sb.append(" , ");
		sb.append(" F.ORDR AS FIELD_ORDER ");
		sb.append(" , ");
		sb.append(" F.IDX ");
		sb.append(" , ");
		sb.append(" F.ISPUBLIC ");
		sb.append(" ,");
		sb.append(" LFT.TYPE as FIELD_TYPE ");
		sb.append(" ,");
		sb.append(" LFIT.TYPE as FIELD_ITYPE ");
		//sb.append(" ,");
		//sb.append(" V.SET_ID ");
		sb.append(" FROM ");
		sb.append(" ").append(table).append(" AS P ");
		sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON P.LKUP_").append(tableref).append("_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_").append(tableref).append("_FIELD_GROUPS AS R ON R.LKUP_").append(tableref).append("_TYPE_ID = T.ID AND R.ACTIVE = 'Y'  ");
		sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID AND G.ACTIVE = 'Y'  ");
		sb.append(" LEFT OUTER JOIN FIELD AS F ON F.FIELD_GROUPS_ID = G.ID AND F.IDX = 'Y' AND F.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN ").append(tableref).append("_FIELD_VALUE AS V ON F.ID = V.FIELD_ID AND V.ACTIVE = 'Y' AND V.").append(idref).append(" = P.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDTYPETABLE).append(" LFT ON F.LKUP_FIELD_TYPE_ID = LFT.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDITYPETABLE).append(" LFIT ON F.LKUP_FIELD_ITYPE_ID = LFIT.ID ");
		
		sb.append(" WHERE ");
		sb.append(" G.ACTIVE = 'Y'  ");
		sb.append(" AND ");
		sb.append(" P.ID = ").append(typeid);
		sb.append(" ) ");
		sb.append(" SELECT * FROM Q ");
		sb.append(" ORDER BY GROUP_ORDER, GROUP_NAME, SET_ID DESC, FIELD_ORDER, NAME ");
		return sb.toString();
	}

	public static String roles(String type, int typeid) {
		if (!Operator.hasValue(type)) { return ""; }
		String table = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" F.FIELD_GROUPS_ID, R.* ");
		sb.append(" FROM ");
		sb.append(" ").append(table).append(" AS M ");
		sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON M.LKUP_").append(tableref).append("_TYPE_ID = T.ID AND M.ID = ").append(typeid);
		sb.append(" JOIN REF_").append(tableref).append("_FIELD_GROUPS AS F ON T.ID = F.LKUP_").append(tableref).append("_TYPE_ID ");
		sb.append(" JOIN REF_FIELD_GROUPS_ROLES AS R ON R.FIELD_GROUPS_ID  = F.FIELD_GROUPS_ID AND R.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String values(String type, int typeid) {
		return values(type, typeid, -1);
	}

	public static String values(String type, int typeid, int groupid) {
		if (!Operator.hasValue(type)) { return ""; }
		String table = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
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
		sb.append(" G.MULTI ");
		sb.append(" , ");
		sb.append(" R.ORDR AS GROUP_ORDER ");
		sb.append(" , ");
		sb.append(" F.ORDR AS FIELD_ORDER ");
		sb.append(" , ");
		sb.append(" F.IDX ");
		sb.append(" ,");
		sb.append(" LFT.TYPE as FIELD_TYPE ");
		sb.append(" ,");
		sb.append(" LFIT.TYPE as FIELD_ITYPE ");
		sb.append(" ,");
		sb.append(" V.SET_ID ");
		sb.append(" FROM ");
		sb.append(" ").append(table).append(" AS P ");
		sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON P.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
		sb.append(" JOIN REF_").append(tableref).append("_FIELD_GROUPS AS R ON R.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
		sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID ");
		sb.append(" JOIN FIELD AS F ON F.FIELD_GROUPS_ID = G.ID ");
		sb.append(" LEFT OUTER JOIN ").append(tableref).append("_FIELD_VALUE AS V ON F.ID = V.FIELD_ID AND V.ACTIVE = 'Y' AND V.").append(idref).append(" = P.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDTYPETABLE).append(" LFT ON F.LKUP_FIELD_TYPE_ID = LFT.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDITYPETABLE).append(" LFIT ON F.LKUP_FIELD_ITYPE_ID = LFIT.ID ");
		
		sb.append(" WHERE ");
		sb.append(" F.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" G.ACTIVE = 'Y'  ");
		sb.append(" AND ");
		sb.append(" P.ID = ").append(typeid);
		if (groupid > 0) {
			sb.append(" AND ");
			sb.append(" G.ID = ").append(groupid);
		}
		sb.append(" ORDER BY R.ORDR, G.GROUP_NAME, V.SET_ID DESC, F.ORDR, F.NAME ");
		return sb.toString();
	}


	public static String inActiveCustom(String type, int typeId, int setId,String fields) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		if(typeId>0){
			sb.append(" UPDATE  ").append(tableref).append("_FIELD_VALUE SET ACTIVE ='N' WHERE ").append(idref).append("= ").append(typeId);
			if(setId>0){
				sb.append(" AND SET_ID = ").append(setId);
			}
			sb.append(" AND FIELD_ID in (").append(fields).append(")");
		}
		return sb.toString();
	}
	
	
	
	public static String backUpCustom(String type, int typeId,int setId,String fields) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		if(typeId>0){
			sb.append(" insert into  ").append(tableref).append("_FIELD_VALUE_HISTORY select * from  ").append(tableref).append("_FIELD_VALUE WHERE  ACTIVE='N' AND ").append(idref).append("= ").append(typeId);
			if(setId>0){
				sb.append(" AND SET_ID = ").append(setId);
			}
			sb.append(" AND FIELD_ID in (").append(fields).append(")");
		}
		return sb.toString();
	}
	
	public static String deleteCustom(String type, int typeId,int setId,String fields) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		if(typeId>0){
			sb.append(" delete from   ").append(tableref).append("_FIELD_VALUE WHERE  ACTIVE='N' AND ").append(idref).append("= ").append(typeId);
			if(setId>0){
				sb.append(" AND SET_ID = ").append(setId);
			}
			sb.append(" AND FIELD_ID in (").append(fields).append(")");
		}
		return sb.toString();
	}
	
	public static String getGroup(int groupId) {
		StringBuilder sb = new StringBuilder();
		if(groupId>0){
			sb.append(" select ID,MULTI from FIELD_GROUPS where ID= ").append(groupId);
		}
		return sb.toString();
	}

	
	public static String getSetId(String type, int typeid, int groupId) {
		if (!Operator.hasValue(type)) { return ""; }
		String table = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" MAX(SET_ID) + 1 as ID ");
		sb.append(" FROM ");
		sb.append(" ").append(table).append(" AS P ");
		sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON P.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
		sb.append(" JOIN REF_").append(tableref).append("_FIELD_GROUPS AS R ON R.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
		sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID ");
		sb.append(" JOIN FIELD AS F ON F.FIELD_GROUPS_ID = G.ID ");
		sb.append(" LEFT OUTER JOIN ").append(tableref).append("_FIELD_VALUE AS V ON F.ID = V.FIELD_ID AND V.ACTIVE = 'Y' AND V.").append(idref).append(" = P.ID ");
		
		sb.append(" WHERE ");
		sb.append(" F.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" G.ACTIVE = 'Y'  ");
		sb.append(" AND ");
		sb.append(" P.ID = ").append(typeid);
		if (groupId > 0) {
			sb.append(" AND ");
			sb.append(" G.ID = ").append(groupId);
		}
		
		
		return sb.toString();
	}
	
	
	
	public static String print(String type, int typeid){
		if (!Operator.hasValue(type)) { return ""; }
		String table = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		
		//sb.append("  WITH Q AS (   ");
		sb.append("  SELECT   ");
		sb.append(" 'custom_'+CAST(G.ID as varchar)+'_'+CAST(F.ID  as varchar)as LABEL ,  V.VALUE   ");
		sb.append("  FROM  ").append(table).append(" AS P   ");
		sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON P.LKUP_").append(tableref).append("_TYPE_ID = T.ID  JOIN REF_").append(tableref).append("_FIELD_GROUPS AS R ON R.LKUP_").append(tableref).append("_TYPE_ID = T.ID   ");
		sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID  ");
		sb.append(" LEFT OUTER JOIN FIELD AS F ON F.FIELD_GROUPS_ID = G.ID AND F.IDX = 'Y' AND F.ACTIVE = 'Y'   ");
		sb.append(" LEFT OUTER JOIN ").append(tableref).append("_FIELD_VALUE AS V ON F.ID = V.FIELD_ID AND V.ACTIVE = 'Y' AND V.").append(idref).append(" = P.ID   ");
		sb.append(" LEFT OUTER JOIN LKUP_FIELD_TYPE LFT ON F.LKUP_FIELD_TYPE_ID = LFT.ID  ");
		sb.append(" LEFT OUTER JOIN LKUP_FIELD_ITYPE LFIT ON F.LKUP_FIELD_ITYPE_ID = LFIT.ID   ");
		sb.append("  WHERE  G.ACTIVE = 'Y'   AND  P.ID = ").append(typeid).append(" AND G.MULTI='N' ");
		/*sb.append("  ) ");
		sb.append(" select ( ");
				sb.append(" select * from Q	 ");
						sb.append(" FOR JSON PATH , INCLUDE_NULL_VALUES ) as custom ");*/
		return sb.toString();
	}
	
	public static String printList(String type, int typeid,String fieldids,String listtype){
		if (!Operator.hasValue(type)) { return ""; }
		String table = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		
		String cols[] = Operator.split(fieldids, ",");
		StringBuilder cb = new StringBuilder();
		for(String c :cols){
			cb.append("[").append(c).append("]");
			cb.append(",");
		}
		cb.append("[0]");
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select ( ");
		sb.append(" select * from ( ");
		sb.append("   SELECT ACTIVITY_ID,VALUE,FIELD_ID, ");
		sb.append("    ROW_NUMBER() OVER(PARTITION BY AFV.FIELD_ID order by AFV.UPDATED_DATE DESC,AFV.SET_ID) as row_no ");
		sb.append("    FROM ").append(tableref).append("_FIELD_VALUE AFV  ");
		//sb.append("    JOIN FIELD F on AFV.FIELD_ID=F.ID   ");
		//sb.append("   JOIN ACTIVITY A on AFV.ACTIVITY_ID=A.ID  ");
		sb.append("   where  FIELD_ID in (").append(fieldids).append(") ");
		sb.append("  AND ").append(idref).append(" = ").append(typeid).append(" AND AFV.ACTIVE='Y' ");
		
		sb.append(" ) ");
		sb.append(" d ");
		sb.append(" pivot ");
		sb.append(" ( ");
		sb.append("   MAX(VALUE) ");
		sb.append("   for FIELD_ID  in (").append(cb.toString()).append(") ");
		sb.append(" ) ");
		sb.append(" piv ");
		sb.append("                       ");
		sb.append(" FOR JSON PATH, INCLUDE_NULL_VALUES   )as list_custom ");		      
		return sb.toString();
		}	 
	
	
	
	public static String getFields(int groupId){
		StringBuilder sb = new StringBuilder();
		sb.append(" select stuff((select  ','+convert(varchar(100),id) from FIELD where FIELD_GROUPS_ID=").append(groupId).append("  for xml path('')),1,1,'') as COMBINED  ");		      
	
		return sb.toString();
	}

	public static String copy(String moduleid, String type, int typeid, int newtypeid, int userid, String ip) {
		if (Operator.toInt(moduleid) < 1 || !Operator.hasValue(type) || typeid < 1 || newtypeid < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ").append(tableref).append("_FIELD_VALUE ( ");
		sb.append(idref);
		sb.append(" , ");
		sb.append(" FIELD_ID ");
		sb.append(" , ");
		sb.append(" VALUE ");
		sb.append(" , ");
		sb.append(" VALUE_CHAR ");
		sb.append(" , ");
		sb.append(" VALUE_INT ");
		sb.append(" , ");
		sb.append(" VALUE_DATE ");
		sb.append(" , ");
		sb.append(" VALUE_DECIMAL ");
		sb.append(" , ");
		sb.append(" SET_ID ");
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
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append("		").append(newtypeid).append(" AS ").append(idref);
		sb.append(" 	, ");
		sb.append(" 	V.FIELD_ID ");
		sb.append(" 	, ");
		sb.append(" 	V.VALUE ");
		sb.append(" 	, ");
		sb.append(" 	V.VALUE_CHAR ");
		sb.append(" 	, ");
		sb.append(" 	V.VALUE_INT ");
		sb.append(" 	, ");
		sb.append(" 	V.VALUE_DATE ");
		sb.append(" 	, ");
		sb.append(" 	V.VALUE_DECIMAL ");
		sb.append(" 	, ");
		sb.append(" 	V.SET_ID ");
		sb.append(" 	, ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" 	, ");
		sb.append(" 	getDate() ");
		sb.append(" 	, ");
		sb.append(" 	getDate() ");
		sb.append(" 	, ");
		sb.append(" 	'").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" 	, ");
		sb.append(" 	'").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" FROM ");
		sb.append(" 	").append(tableref).append("_FIELD_VALUE AS V ");
		sb.append(" 	JOIN FIELD AS F ON V.FIELD_ID = F.ID AND F.FIELD_GROUPS_ID = ").append(Operator.toInt(moduleid));
		sb.append(" WHERE ");
		sb.append(" 	V.").append(idref).append(" = ").append(typeid);
		sb.append(" 	AND ");
		sb.append(" 	V.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String delete(String type, int typeid, int fieldgroupid, int setid, int updatedby, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ").append(tableref).append("_FIELD_VALUE SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(updatedby);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(" SET_ID = ").append(setid);
		sb.append(" AND ");
		sb.append(" FIELD_ID IN ( SELECT ID FROM FIELD WHERE FIELD_GROUPS_ID = ").append(fieldgroupid).append(" ) ");
		return sb.toString();
	}


}















