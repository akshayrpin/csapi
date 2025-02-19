package csapi.impl.sitedata;

import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.entity.EntityAgent;
import csapi.utils.CsReflect;
import csshared.vo.TypeInfo;

public class SitedataSQL {

	public static String info(String type, int typeid, int id) {
		return summary(type, typeid, -1);
	}

	public static String summary(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		TypeInfo t = EntityAgent.getEntity(type, typeid);
		StringBuilder sb = new StringBuilder();
		if (Operator.hasValue(t.getEntity()) && Operator.equalsIgnoreCase(t.getEntity(), "lso")) {
			int landid = t.getLSOLandId();
			int structureid = t.getLSOStructureId();
			int occupancyid = t.getLSOOccupancyId();
			sb.append(" WITH Q AS ( ");
			sb.append(" SELECT TOP 1 ");
			sb.append(" LA.ID, ");
			sb.append(" 'LAND' AS ENTITY, ");
			sb.append(" L.DESCRIPTION, ");
			sb.append(" LA.CREATED_DATE AS DATE, ");
			sb.append(" 3 AS ORDR ");
			sb.append(" FROM ");
			sb.append(" LSO_SITEDATA AS LA ");
			sb.append(" JOIN LSO L ON LA.LSO_ID = L.ID ");
			sb.append(" WHERE ");
			sb.append(" LA.ACTIVE='Y' ");
			sb.append(" AND ");
			sb.append(" LA.LSO_ID = ").append(landid);
			sb.append(" ORDER BY LA.CREATED_DATE DESC, ID DESC ");
			if (structureid > 0) {
				sb.append(" UNION ");
				sb.append(" SELECT TOP 1 ");
				sb.append(" LA.ID, ");
				sb.append(" 'STRUCTURE' AS ENTITY, ");
				sb.append(" L.DESCRIPTION, ");
				sb.append(" LA.CREATED_DATE AS DATE, ");
				sb.append(" 2 AS ORDR ");
				sb.append(" FROM ");
				sb.append(" LSO_SITEDATA AS LA ");
				sb.append(" JOIN LSO L ON LA.LSO_ID = L.ID ");
				sb.append(" WHERE ");
				sb.append(" LA.ACTIVE='Y' ");
				sb.append(" AND ");
				sb.append(" LA.LSO_ID = ").append(structureid);
				sb.append(" ORDER BY LA.CREATED_DATE DESC, ID DESC ");
				if (occupancyid > 0) {
					sb.append(" UNION ");
					sb.append(" SELECT TOP 1 ");
					sb.append(" LA.ID, ");
					sb.append(" 'OCCUPANCY' AS ENTITY, ");
					sb.append(" L.DESCRIPTION, ");
					sb.append(" LA.CREATED_DATE AS DATE, ");
					sb.append(" 1 AS ORDR ");
					sb.append(" FROM ");
					sb.append(" LSO_SITEDATA AS LA ");
					sb.append(" JOIN LSO L ON LA.LSO_ID = L.ID ");
					sb.append(" WHERE ");
					sb.append(" LA.ACTIVE='Y' ");
					sb.append(" AND ");
					sb.append(" LA.LSO_ID = ").append(occupancyid);
					sb.append(" ORDER BY LA.CREATED_DATE DESC, ID DESC ");
				}
			}
			sb.append(" ) ");
			sb.append(" SELECT * FROM Q ORDER BY ORDR, DATE DESC ");
		}
		return sb.toString();
	}

	public static String list(String type, int typeid, int id) {
		return details(type, typeid, -1);
	}
	

	/*public static String details(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" TOP 1 LA.ID,F.NAME AS FIELDNAME,LA.UPDATED_DATE as DATE");
		sb.append(" from LSO_SITEDATA LA ");
		sb.append(" left outer join LSO_SITEDATA_FIELD_VALUE LSF on LA.ID= LSF.LSO_SITEDATA_ID ");
		sb.append(" left outer join FIELD F on LSF.FIELD_ID= F.ID ");
	
		
		//sb.append(" where  LA.ACTIVE='Y' and F.ACTIVE='Y'  and F.NAME='LANDNAME' ");
		sb.append(" where  LA.ACTIVE='Y' and F.ACTIVE='Y' ");
		
		sb.append(" AND ");
		sb.append(" LSO_ID = ").append(typeid);
		if(id>0){
			sb.append(" AND ");
			sb.append(" LA.ID = ").append(id);
		}
		//sb.append(" ORDER BY N.UPDATED_DATE DESC ");
		return sb.toString();
	}*/
	public static String details(String type, int typeid, int id) {
		if (id < 1) { return ""; }
		if (!Operator.hasValue(type)) { return ""; }
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		StringBuilder sb = new StringBuilder();
		if (Operator.equalsIgnoreCase(entity.getEntity(), "lso")) {
			sb.append(" SELECT ");
			sb.append(" 	F.ID, ");
			sb.append(" 	SD.DESCRIPTION, ");
			sb.append(" 	L.ID AS LSO_ID, ");
			sb.append(" 	SD.ID AS LSO_SITEDATA_ID, ");
			sb.append(" 	F.NAME, ");
			sb.append(" 	V.VALUE, ");
			sb.append(" 	FG.GROUP_NAME, ");
			sb.append(" 	FG.ISPUBLIC AS GROUP_PUBLIC, ");
			sb.append(" 	F.ORDR AS FIELD_ORDER, ");
			sb.append(" 	FT.TYPE AS FIELD_TYPE, ");
			sb.append(" 	FIT.TYPE AS FIELD_ITYPE ");
			sb.append(" FROM ");
			sb.append(" 	LSO_SITEDATA AS SD ");
			sb.append(" 	JOIN LSO AS L ON SD.LSO_ID = L.ID AND SD.ACTIVE = 'Y' AND SD.ID = ").append(id);
			sb.append(" 	JOIN LKUP_LSO_TYPE AS T ON L.LKUP_LSO_TYPE_ID = T.ID ");
			sb.append(" 	JOIN FIELD_GROUPS AS FG ON T.SITEDATA_FIELD_GROUP_ID = FG.ID AND FG.ACTIVE = 'Y' ");
			sb.append(" 	JOIN FIELD AS F ON F.FIELD_GROUPS_ID = FG.ID AND F.ACTIVE = 'Y' ");
			sb.append(" 	LEFT OUTER JOIN LKUP_FIELD_TYPE AS FT ON F.LKUP_FIELD_TYPE_ID = FT.ID ");
			sb.append(" 	LEFT OUTER JOIN LKUP_FIELD_ITYPE AS FIT ON F.LKUP_FIELD_ITYPE_ID = FIT.ID ");
			sb.append(" 	LEFT OUTER JOIN LSO_SITEDATA_FIELD_VALUE AS V ON SD.ID = V.LSO_SITEDATA_ID AND F.ID = V.FIELD_ID AND V.ACTIVE = 'Y' ");
			sb.append(" ORDER BY ");
			sb.append(" 	FG.GROUP_NAME, F.ORDR ");
		}
		return sb.toString();
	}

	public static String fields(String type, int typeid) {
		if (!Operator.hasValue(type)) { return ""; }
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		StringBuilder sb = new StringBuilder();
		if (Operator.equalsIgnoreCase(entity.getEntity(), "lso")) {
			sb.append(" SELECT ");
			sb.append(" 	F.ID, ");
			sb.append(" 	L.ID AS LSO_ID, ");
			sb.append(" 	F.NAME, ");
			sb.append(" 	FG.GROUP_NAME, ");
			sb.append(" 	FG.ISPUBLIC AS GROUP_PUBLIC, ");
			sb.append(" 	F.ORDR AS FIELD_ORDER, ");
			sb.append(" 	FT.TYPE AS FIELD_TYPE, ");
			sb.append(" 	FIT.TYPE AS FIELD_ITYPE ");
			sb.append(" FROM ");
			sb.append(" 	LSO AS L ");
			sb.append(" 	JOIN LKUP_LSO_TYPE AS T ON L.LKUP_LSO_TYPE_ID = T.ID AND L.ID = ").append(entity.getEntityid());
			sb.append(" 	JOIN FIELD_GROUPS AS FG ON T.SITEDATA_FIELD_GROUP_ID = FG.ID AND FG.ACTIVE = 'Y' ");
			sb.append(" 	JOIN FIELD AS F ON F.FIELD_GROUPS_ID = FG.ID AND F.ACTIVE = 'Y' ");
			sb.append(" 	LEFT OUTER JOIN LKUP_FIELD_TYPE AS FT ON F.LKUP_FIELD_TYPE_ID = FT.ID ");
			sb.append(" 	LEFT OUTER JOIN LKUP_FIELD_ITYPE AS FIT ON F.LKUP_FIELD_ITYPE_ID = FIT.ID ");
			sb.append(" ORDER BY ");
			sb.append(" 	FG.GROUP_NAME, F.ORDR ");
		}
		return sb.toString();
	}

	
	


	public static String sdetails1(String type, int typeid, int id) {
		if (id < 1) { return ""; }
		if (!Operator.hasValue(type)) { return ""; }
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		StringBuilder sb = new StringBuilder();
		if (Operator.equalsIgnoreCase(entity.getEntity(), "lso")) {
			sb.append(" SELECT ");
			sb.append(" 	F.ID, ");
			sb.append(" 	SD.DESCRIPTION, ");
			sb.append(" 	SD.ID AS LSO_SITEDATA_ID, ");
			sb.append(" 	F.NAME, ");
			sb.append(" 	V.VALUE, ");
			sb.append(" 	FG.GROUP_NAME, ");
			sb.append(" 	FG.ISPUBLIC AS GROUP_PUBLIC, ");
			sb.append(" 	F.ORDR AS FIELD_ORDER, ");
			sb.append(" 	FT.TYPE, ");
			sb.append(" 	FIT.TYPE AS ITYPE ");
			sb.append(" FROM ");
			sb.append(" 	LSO_SITEDATA AS SD ");
			sb.append(" 	JOIN LSO AS L ON SD.LSO_ID = L.ID AND SD.ACTIVE = 'Y' AND SD.ID = 4199 ");
			sb.append(" 	JOIN LKUP_LSO_TYPE AS T ON L.LKUP_LSO_TYPE_ID = T.ID ");
			sb.append(" 	JOIN FIELD_GROUPS AS FG ON T.SITEDATA_FIELD_GROUP_ID = FG.ID AND FG.ACTIVE = 'Y' ");
			sb.append(" 	JOIN FIELD AS F ON F.FIELD_GROUPS_ID = FG.ID AND F.ACTIVE = 'Y' ");
			sb.append(" 	LEFT OUTER JOIN LKUP_FIELD_TYPE AS FT ON F.LKUP_FIELD_TYPE_ID = FT.ID ");
			sb.append(" 	LEFT OUTER JOIN LKUP_FIELD_ITYPE AS FIT ON F.LKUP_FIELD_ITYPE_ID = FIT.ID ");
			sb.append(" 	LEFT OUTER JOIN LSO_SITEDATA_FIELD_VALUE AS V ON SD.ID = V.LSO_SITEDATA_ID AND F.ID = V.FIELD_ID AND V.ACTIVE = 'Y' ");
			sb.append(" ORDER BY ");
			sb.append(" 	FG.GROUP_NAME, F.ORDR ");

//			OLD QUERY - REPLACED BY ALAIN 06/23/2018
//			sb.append(" SELECT ");
//			sb.append(" F.ID ");
//			sb.append(" , ");
//			sb.append(" F.NAME ");
//			sb.append(" , ");
//			sb.append(" V.VALUE ");
//			sb.append(" , ");
//			sb.append(" G.GROUP_NAME ");
//			sb.append(" ,");
//			sb.append(" G.ID as GROUP_ID ");
//			sb.append(" ,");
//			sb.append(" G.ISPUBLIC as GROUP_PUBLIC ");
//			sb.append(" , ");
//			sb.append(" F.ORDR AS FIELD_ORDER ");
//			sb.append(" ,");
//			sb.append(" LFT.TYPE as FIELD_TYPE ");
//			sb.append(" ,");
//			sb.append(" LFIT.TYPE as FIELD_ITYPE ");
//			sb.append(" FROM ");
//			sb.append(" LSO P ");
//			sb.append(" JOIN LKUP_LSO_TYPE AS T ON P.LKUP_LSO_TYPE_ID = T.ID ");
//			sb.append(" JOIN FIELD_GROUPS AS G ON T.SITEDATA_FIELD_GROUP_ID = G.ID   ");
//			sb.append(" JOIN FIELD AS F ON F.FIELD_GROUPS_ID = G.ID ");
//			sb.append(" LEFT OUTER JOIN LSO_SITEDATA LSD on P.ID= LSD.LSO_ID  ");
//			sb.append(" LEFT OUTER JOIN LSO_SITEDATA_FIELD_VALUE AS V ON F.ID = V.FIELD_ID AND V.ACTIVE = 'Y'  AND LSD.ID = V.LSO_SITEDATA_ID ");
//			sb.append(" LEFT OUTER JOIN LKUP_FIELD_TYPE LFT ON F.LKUP_FIELD_TYPE_ID = LFT.ID ");
//			sb.append(" LEFT OUTER JOIN LKUP_FIELD_ITYPE LFIT ON F.LKUP_FIELD_ITYPE_ID = LFIT.ID ");
//			sb.append(" WHERE ");
//			sb.append(" F.ACTIVE = 'Y' ");
//			sb.append(" AND ");
//			sb.append(" G.ACTIVE = 'Y'  ");
//			sb.append(" AND ");
//			sb.append(" LSD.ID = ").append(id);
//			sb.append(" ORDER BY  G.GROUP_NAME, F.ORDR ");
		}
		return sb.toString();
	}




	public static String inActiveCustom(int sitedataid, int setId) {
		StringBuilder sb = new StringBuilder();
		if(sitedataid>0){
			sb.append(" UPDATE LSO_SITEDATA_FIELD_VALUE SET ACTIVE ='N' WHERE LSO_SITEDATA_ID = ").append(sitedataid);
			if(setId>0){
				sb.append(" AND SET_ID = ").append(setId);
			}
		}
		return sb.toString();
	}


	public static String backUpCustom(int sitedataid, int setId) {
		StringBuilder sb = new StringBuilder();
		if(sitedataid>0){
			sb.append(" INSERT INTO LSO_SITEDATA_FIELD_VALUE_HISTORY SELECT * FROM LSO_SITEDATA_FIELD_VALUE WHERE ACTIVE='N' AND LSO_SITEDATA_ID = ").append(sitedataid);
			if(setId>0){
				sb.append(" AND SET_ID = ").append(setId);
			}
		}
		return sb.toString();
	}

	public static String deleteCustom(int sitedataid, int setId) {
		StringBuilder sb = new StringBuilder();
		if(sitedataid>0){
			sb.append(" DELETE FROM LSO_SITEDATA_FIELD_VALUE WHERE  ACTIVE='N' AND LSO_SITEDATA_ID = ").append(sitedataid);
			if(setId>0){
				sb.append(" AND SET_ID = ").append(setId);
			}
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
		sb.append(" LSO_SITEDATA AS P ");
		sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON P.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
		//sb.append(" JOIN REF_").append(tableref).append("_FIELD_GROUPS AS R ON R.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
		sb.append(" JOIN FIELD_GROUPS AS G ON R.FIELD_GROUPS_ID = G.ID ");
		sb.append(" JOIN FIELD AS F ON F.FIELD_GROUPS_ID = G.ID ");
		sb.append(" LEFT OUTER JOIN LSO_SITEDATA_FIELD_VALUE AS V ON F.ID = V.FIELD_ID AND V.ACTIVE = 'Y' AND V.LSO_SITEDATA_ID = P.ID ");
		
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
	
	
	
	public static String print(String type,int typeid){
		String table = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT M.ID,P.ID AS PARENT_ID,P.PARENT_ID AS GRANDPARENT_ID ,M.LKUP_LSO_TYPE_ID FROM ");
		sb.append(" LSO AS M ");
		sb.append(" JOIN REF_LSO_PROJECT RLP on M.ID=RLP.LSO_ID ");
		sb.append(" LEFT OUTER JOIN LSO AS P ON M.PARENT_ID = P.ID ");
		
		sb.append(" JOIN ACTIVITY A on RLP.PROJECT_ID=A.PROJECT_ID ");
		sb.append(" WHERE A.ID=").append(typeid);
		
		sb.append("  ");
		sb.append(" ), ");
		sb.append(" R AS ( ");
		sb.append("  ");
		sb.append(" select MAX(LSD.ID) as SITE_DATA_ID,Q.ID AS LSO_ID ");
		sb.append(" ,CASE WHEN LKUP_LSO_TYPE_ID =3 THEN 'occupancy_sd_' WHEN LKUP_LSO_TYPE_ID =2 THEN 'structure_sd_' ELSE 'land_sd_' END AS TYPE  ");
		sb.append(" FROM LSO_SITEDATA LSD JOIN Q on LSD.LSO_ID = Q.ID WHERE LSD.ACTIVE='Y' group by Q.ID,LKUP_LSO_TYPE_ID ");
		sb.append(" union  ");
		sb.append(" select MAX(LSD.ID) as SITE_DATA_ID, Q.PARENT_ID AS LSO_ID   ");
		sb.append(" ,CASE WHEN LKUP_LSO_TYPE_ID =3 THEN 'structure_sd_' WHEN LKUP_LSO_TYPE_ID =2 THEN 'land_sd_' ELSE '' END AS TYPE  ");
		sb.append(" FROM LSO_SITEDATA LSD JOIN Q on LSD.LSO_ID = Q.PARENT_ID WHERE LSD.ACTIVE='Y' group by Q.PARENT_ID,LKUP_LSO_TYPE_ID ");
		sb.append(" union ");
		sb.append(" select MAX(LSD.ID) as SITE_DATA_ID,Q.GRANDPARENT_ID AS LSO_ID  ");
		sb.append(" ,CASE WHEN LKUP_LSO_TYPE_ID =3 THEN 'land_sd_'  ELSE '' END AS TYPE  ");
		sb.append(" FROM LSO_SITEDATA LSD JOIN Q on LSD.LSO_ID = Q.GRANDPARENT_ID WHERE LSD.ACTIVE='Y' group by Q.GRANDPARENT_ID,LKUP_LSO_TYPE_ID ");
		sb.append("  ");
		sb.append(" )  ");
		sb.append("  ");
		sb.append("  ");
		sb.append(" select R.TYPE+CAST(F.ID AS VARCHAR) AS LABEL, RD.VALUE  from  ");
		sb.append(" LSO_SITEDATA_FIELD_VALUE RD  ");
		sb.append(" JOIN  R on RD.LSO_SITEDATA_ID= R.SITE_DATA_ID ");
		sb.append(" LEFT OUTER JOIN FIELD AS F ON RD.FIELD_ID = F.ID ");
		sb.append(" union  ");
		sb.append(" select R.TYPE+'setback_'+SBT.TYPE+'_ft' AS LABEL, CAST(SB.FT AS VARCHAR) as VALUE  from  ");
		sb.append(" LSO_SITEDATA_FIELD_VALUE RD  ");
		sb.append(" JOIN  R on RD.LSO_SITEDATA_ID= R.SITE_DATA_ID ");
		sb.append(" LEFT OUTER JOIN LSO_SETBACK SB on RD.LSO_SITEDATA_ID = SB.LSO_SITEDATA_ID ");
		sb.append(" LEFT OUTER JOIN LKUP_LSO_SETBACK_TYPE SBT on SB.LKUP_LSO_SETBACK_TYPE_ID = SBT.ID ");
		sb.append(" union  ");
		sb.append(" select R.TYPE+'setback_'+SBT.TYPE+'_inches' AS LABEL, CAST(SB.INCHES AS VARCHAR) as VALUE  from  ");
		sb.append(" LSO_SITEDATA_FIELD_VALUE RD  ");
		sb.append(" JOIN  R on RD.LSO_SITEDATA_ID= R.SITE_DATA_ID ");
		sb.append(" LEFT OUTER JOIN LSO_SETBACK SB on RD.LSO_SITEDATA_ID = SB.LSO_SITEDATA_ID ");
		sb.append(" LEFT OUTER JOIN LKUP_LSO_SETBACK_TYPE SBT on SB.LKUP_LSO_SETBACK_TYPE_ID = SBT.ID ");
		sb.append(" union  ");
		sb.append(" select R.TYPE+'setback_'+SBT.TYPE+'_comments' AS LABEL, SB.COMMENTS as VALUE  from  ");
		sb.append(" LSO_SITEDATA_FIELD_VALUE RD  ");
		sb.append(" JOIN  R on RD.LSO_SITEDATA_ID= R.SITE_DATA_ID ");
		sb.append(" LEFT OUTER JOIN LSO_SETBACK SB on RD.LSO_SITEDATA_ID = SB.LSO_SITEDATA_ID ");
		sb.append(" LEFT OUTER JOIN LKUP_LSO_SETBACK_TYPE SBT on SB.LKUP_LSO_SETBACK_TYPE_ID = SBT.ID ");
		sb.append("  ");
		sb.append("  ");
			
		return sb.toString();
	}
	
	
	


}















