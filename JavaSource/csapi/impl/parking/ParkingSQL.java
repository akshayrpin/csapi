package csapi.impl.parking;

import java.util.HashMap;

import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.entity.EntityAgent;
import csshared.vo.TypeInfo;

public class ParkingSQL {



	public static String search(HashMap<String, String> s,String reference){
		StringBuilder sb = new StringBuilder();
		try{
			boolean q = false;
			if(Operator.hasValue(s.get("accountno"))){
				sb.append (" select PROJECT_ID from REF_PROJECT_PARKING where ID = ").append(Operator.sqlEscape(s.get("accountno")));
				q= true;
			}
			else if(Operator.hasValue(s.get("name"))){
				String name = s.get("name");
				sb.append(searchName(name));

//				sb.append(" SELECT ");
//				sb.append(" RAP.PROJECT_ID ");
//				sb.append(" FROM ");
//				sb.append(" REF_USERS U ");
//				sb.append(" JOIN REF_PROJECT_USERS  RU ON U.ID=RU.REF_USERS_ID ");
//				sb.append(" JOIN REF_PROJECT_PARKING  RAP on RU.PROJECT_ID=RAP.PROJECT_ID ");
//				sb.append(" JOIN USERS  USR  on U.USERS_ID=USR.ID ");
//				sb.append(" WHERE ");
//				sb.append(" USR.ID > 0  ");
//				sb.append(" AND ");
//				sb.append(" ( LOWER(USR.FIRST_NAME) like '%").append(Operator.sqlEscape(s.get("name")).toLowerCase()).append("%'");	
//				sb.append(" OR ");
//				sb.append(" LOWER(USR.LAST_NAME) like '%").append(Operator.sqlEscape(s.get("name")).toLowerCase()).append("%'");	
//				sb.append(" OR ");
//				sb.append(" LOWER(USR.USERNAME) like '%").append(Operator.sqlEscape(s.get("name")).toLowerCase()).append("%'");	
//				sb.append(" ) ");	
				q= true;

			}
			else if(Operator.hasValue(s.get("email"))){
				sb.append(" SELECT TOP 100 ");
				sb.append(" RAP.PROJECT_ID ");
				sb.append(" , ");
				sb.append(" USR.EMAIL AS HIT ");
				sb.append(" FROM ");
				sb.append(" REF_USERS U ");
				sb.append(" JOIN REF_PROJECT_USERS  RU ON U.ID=RU.REF_USERS_ID ");
				sb.append(" JOIN REF_PROJECT_PARKING  RAP ON RU.PROJECT_ID=RAP.PROJECT_ID ");
				sb.append(" JOIN USERS USR ON U.USERS_ID=USR.ID ");
				sb.append(" WHERE ");
				sb.append(" USR.ID > 0  ");
				sb.append(" AND ");
				sb.append(" LOWER(USR.EMAIL) like '%").append(Operator.sqlEscape(s.get("email")).toLowerCase()).append("%' ");
			}
			else if(Operator.hasValue(s.get("permit"))){
				sb.append(" SELECT TOP 100 ");
				sb.append(" A.PROJECT_ID ");
				sb.append(" , ");
				sb.append(" A.ACT_NBR AS HIT ");
				sb.append(" FROM ");
				sb.append(" ACTIVITY AS A ");
				sb.append(" WHERE ");
				sb.append(" LOWER(A.ACT_NBR) like '%").append(Operator.sqlEscape(s.get("permit")).toLowerCase()).append("%' ");
			}
			else if(Operator.hasValue(s.get("licno"))){
				sb.append(" SELECT TOP 100 RA.PROJECT_ID, V.LICENSE_PLATE AS HIT ");
				sb.append(" FROM ");
				sb.append(" REF_PROJECT_PARKING AS RA ");
				sb.append(" JOIN VEHICLE V ON V.REF_PROJECT_PARKING_ID = RA.ID ");
				sb.append(" WHERE ");
				sb.append(" V.ID > 0 ");
				sb.append(" AND ");
				sb.append(" LOWER(V.LICENSE_PLATE) like '%").append(Operator.sqlEscape(s.get("licno")).toLowerCase()).append("%' ");	
				q= true;
			
			}
			else {
				sb.append(" select ");
				sb.append(" 	DISTINCT RLP.PROJECT_ID ");
				sb.append(" FROM ");
				sb.append(" 	LSO L ");
				sb.append(" 	JOIN REF_LSO_PROJECT RLP on L.ID = RLP.LSO_ID AND RLP.ACTIVE='Y' ");
				sb.append(" 	JOIN PROJECT P on RLP.PROJECT_ID = P.ID ");
				sb.append(" 	JOIN LKUP_PROJECT_STATUS LPS ON P.LKUP_PROJECT_STATUS_ID = LPS.ID ");

				if(Operator.hasValue(s.get("strno")) || Operator.hasValue(s.get("strname")) || Operator.hasValue(s.get("fraction")) || Operator.hasValue(s.get("unit"))){
					sb.append (" AND ");
					sb.append(" LPS.EXPIRED <>'Y' ");
					sb.append(" AND ");
					sb.append(" COALESCE(P.EXPIRED_DT, getDate() + 10) >= getDate() ");
					//sb.append("  COALESCE(DATEADD(dd, 1, P.EXPIRED_DT), getDate() + 10)>= getDate() ");
					sb.append(" AND ");
					sb.append(" LPS.LIVE = 'Y' ");	
					q= true;
				}

				sb.append(" 	JOIN LKUP_PROJECT_TYPE LPT on P.LKUP_PROJECT_TYPE_ID = LPT.ID ");
				sb.append(" 	JOIN REF_PROJECT_PARKING RAP on RLP.PROJECT_ID=RAP.PROJECT_ID ");

				/*sb.append("  left outer join PROJECT PL on RAP.PROJECT_ID=PL.ID ");
				sb.append("  left outer join LKUP_PROJECT_TYPE LPTL on PL.LKUP_PROJECT_TYPE_ID=LPTL.ID and  (LPT.ISDOT ='Y' OR LPTL.ISDOT ='Y')");*/
				sb.append(" where  L.ID>0  ");
				
				
				if(Operator.hasValue(s.get("strno"))){
					sb.append (" and STR_NO = ").append(Operator.sqlEscape(s.get("strno")));	
					q= true;
				}
				if(Operator.hasValue(s.get("strname"))){
					sb.append (" and LSO_STREET_ID = ").append(Operator.sqlEscape(s.get("strname")));	
					q= true;
				}
				
				if(Operator.hasValue(s.get("unit"))){
					sb.append (" and UNIT = '").append(Operator.sqlEscape(s.get("unit"))).append("'");	
					q= true;
				}
				
				if(Operator.hasValue(s.get("fraction"))){
					sb.append (" and STR_MOD = '").append(Operator.sqlEscape(s.get("fraction"))).append("' ");	
					q= true;
				}
				
				if(Operator.hasValue(s.get("accountno"))){
					sb.append (" and RAP.ID = ").append(Operator.sqlEscape(s.get("accountno")));
					q= true;
				}
				
				if(Operator.hasValue(reference)){
					sb.append (" and RAP.ID = ").append(Operator.sqlEscape(reference));
					q= true;
				}
				
				if(!q){
					sb.append( " and L.ACTIVE='R' ");
				}
			
			}
			//TEMPORARY TO REMOVE oNCE migrations is done
			/*if(Operator.hasValue(s.get("projectno"))){
				sb.append (" and ( RAP.PROJECT_ID = ").append(Operator.sqlEscape(s.get("projectno"))).append(" OR RLP.PROJECT_ID =").append(Operator.sqlEscape(s.get("projectno"))).append(" )");
			}*/
		
		
		}catch(Exception e){
			Logger.error(e.getMessage());
		}

		return sb.toString();
	}

	public static String searchName(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" RAP.PROJECT_ID ");
		sb.append(" , ");
		sb.append(" COALESCE(USR.FIRST_NAME, '') + ' ' + COALESCE(USR.LAST_NAME, '') AS HIT ");
		sb.append(" FROM ");
		sb.append(" REF_USERS U ");
		sb.append(" JOIN REF_PROJECT_USERS AS RU ON U.ID=RU.REF_USERS_ID ");
		sb.append(" JOIN REF_PROJECT_PARKING AS RAP ON RU.PROJECT_ID=RAP.PROJECT_ID ");
		sb.append(" JOIN PROJECT AS P ON RAP.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_PROJECT_STATUS AS S ON P.LKUP_PROJECT_STATUS_ID = S.ID AND S.EXPIRED = 'N' ");
		sb.append(" JOIN USERS USR ON U.USERS_ID=USR.ID ");
		sb.append(" WHERE ");
		sb.append(" USR.ID > 0  ");
		sb.append(" AND ");
		sb.append(" LOWER(USR.FIRST_NAME) like '%").append(Operator.sqlEscape(name).toLowerCase()).append("%' ");
		sb.append(" UNION ");
		sb.append(" SELECT ");
		sb.append(" RAP.PROJECT_ID ");
		sb.append(" , ");
		sb.append(" COALESCE(USR.FIRST_NAME, '') + ' ' + COALESCE(USR.LAST_NAME, '') AS HIT ");
		sb.append(" FROM ");
		sb.append(" REF_USERS U ");
		sb.append(" JOIN REF_PROJECT_USERS AS RU ON U.ID=RU.REF_USERS_ID AND RU.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_PROJECT_PARKING AS RAP ON RU.PROJECT_ID=RAP.PROJECT_ID AND RAP.ACTIVE = 'Y' ");
		sb.append(" JOIN PROJECT AS P ON RAP.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_PROJECT_STATUS AS S ON P.LKUP_PROJECT_STATUS_ID = S.ID AND S.EXPIRED = 'N' ");
		sb.append(" JOIN USERS USR ON U.USERS_ID=USR.ID ");
		sb.append(" WHERE ");
		sb.append(" USR.ID > 0  ");
		sb.append(" AND ");
		sb.append(" LOWER(USR.LAST_NAME) like '%").append(Operator.sqlEscape(name).toLowerCase()).append("%' ");

		String[] narray = Operator.split(name, " ");
		int l = narray.length;
		if (l > 1) {
			StringBuilder insb = new StringBuilder();
			for (int i=0; i<l; i++) {
				if (i > 0) { insb.append(", "); }
				insb.append(" '").append(Operator.sqlEscape(narray[i])).append("' ");
			}
			String in = insb.toString();

			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" RAP.PROJECT_ID ");
			sb.append(" , ");
			sb.append(" COALESCE(USR.FIRST_NAME, '') + ' ' + COALESCE(USR.LAST_NAME, '') AS HIT ");
			sb.append(" FROM ");
			sb.append(" REF_USERS U ");
			sb.append(" JOIN REF_PROJECT_USERS AS RU ON U.ID=RU.REF_USERS_ID AND RU.ACTIVE = 'Y' ");
			sb.append(" JOIN REF_PROJECT_PARKING AS RAP ON RU.PROJECT_ID=RAP.PROJECT_ID AND RAP.ACTIVE = 'Y' ");
			sb.append(" JOIN PROJECT AS P ON RAP.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
			sb.append(" JOIN LKUP_PROJECT_STATUS AS S ON P.LKUP_PROJECT_STATUS_ID = S.ID AND S.EXPIRED = 'N' ");
			sb.append(" JOIN USERS USR ON U.USERS_ID=USR.ID ");
			sb.append(" WHERE ");
			sb.append(" USR.ID > 0  ");
			sb.append(" AND ");
			sb.append(" LOWER(USR.FIRST_NAME) IN (").append(in).append(" ) ");
			sb.append(" AND ");
			sb.append(" LOWER(USR.LAST_NAME) IN (").append(in).append(" ) ");
		}

		return sb.toString();
	}


	public static String search(int userId){
		StringBuilder sb = new StringBuilder();
		try{
		
			sb.append(" select distinct RAP.PROJECT_ID,U.ID from REF_USERS U ");
			sb.append(" join REF_PROJECT_USERS  RU on U.ID=RU.REF_USERS_ID AND RU.ACTIVE='Y'");
			sb.append(" join REF_PROJECT_PARKING  RAP on RU.PROJECT_ID=RAP.PROJECT_ID and RAP.ACTIVE = 'Y'  ");
			sb.append(" where  U.ID>0  ");
			
			sb.append (" and U.USERS_ID = ").append(userId);	
			
		}catch(Exception e){
			Logger.error(e.getMessage());
		}

		return sb.toString();
	}
	
	
	public static String getParkingInfo(String ids){
		StringBuilder sb = new StringBuilder();
		
		sb.append(" WITH PI AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		P.ID, ");
		sb.append(" 		CONVERT(varchar(100), L.STR_NO)+' '+COALESCE(L.STR_MOD, '')+' '+  LS.STR_NAME+' '+ LS.STR_TYPE+' '+ CASE WHEN PRE_DIR is null THEN '' ELSE PRE_DIR END+''+CASE WHEN L.UNIT is null then '' ELSE L.UNIT END as ADDRESS  ");
		sb.append(" 	FROM ");
		sb.append(" 		PROJECT P ");
		sb.append(" 		JOIN REF_LSO_PROJECT R on P.ID=R.PROJECT_ID AND R.ACTIVE='Y'");
		sb.append(" 		JOIN LSO L on R.LSO_ID=L.ID ");
		sb.append(" 	 	JOIN LSO_STREET LS on L.LSO_STREET_ID=LS.ID ");
		sb.append(" 	WHERE ");
		sb.append(" 		P.ID in (").append(ids).append(") ");
		sb.append(" 	)   ");
		
		sb.append(" SELECT DISTINCT TOP 1 ");
		sb.append(" 		LTRIM(RTRIM( ");
		sb.append(" 		CASE WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ELSE '' END + ");
		sb.append(" 		CASE WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ELSE '' END + ");       
		sb.append(" 		CASE WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ELSE '' END )) AS NAME, ");   
		sb.append(" 		RAP.ID AS ACCOUNT_NO, ");
		sb.append(" 		U.EMAIL,PPL.PHONE_WORK, ");
		sb.append(" 		PPL.PHONE_CELL, ");
		sb.append(" 		PPL.PHONE_HOME, ");
		sb.append(" 		P.PROJECT_NBR, ");
		sb.append(" 		PI.ADDRESS, ");
		sb.append(" 		RAP.NO_SPACES, ");
		sb.append(" 		RAP.NO_CARS, ");
		sb.append(" 		RAP.APPROVED_SPACE, ");
		sb.append(" 		RAP.SECRET, ");
		sb.append(" 		T.DESCRIPTION as TYPE, ");
		sb.append(" 		P.ID as PROJECT_ID, ");
		sb.append(" 		LSP.STATUS, ");
		sb.append(" 		LSP.EXPIRED,RLP.LSO_ID ");
		sb.append(" FROM ");
		sb.append(" 	PROJECT P  ");
		sb.append(" 	JOIN REF_LSO_PROJECT RLP on P.ID = RLP.PROJECT_ID 	AND RLP.ACTIVE='Y' ");
		sb.append(" 	JOIN LKUP_PROJECT_STATUS  LSP on P.LKUP_PROJECT_STATUS_ID = LSP.ID ");
		sb.append(" 	LEFT OUTER JOIN REF_PROJECT_PARKING RAP on P.ID = RAP.PROJECT_ID ");
		sb.append(" 	LEFT OUTER JOIN REF_PROJECT_USERS  REF on P.ID = REF.PROJECT_ID ");
		sb.append(" 	LEFT OUTER JOIN REF_USERS AS R ON REF.REF_USERS_ID = R.ID AND R.ACTIVE = 'Y' AND REF.ACTIVE = 'Y' ");   
		sb.append(" 	LEFT OUTER JOIN LKUP_USERS_TYPE T on R.LKUP_USERS_TYPE_ID = T.ID AND T.ACTIVE = 'Y' AND T.ID=11 "); 
		sb.append(" 	LEFT OUTER JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0 AND U.ACTIVE = 'Y' ");
		sb.append(" 	LEFT OUTER JOIN PEOPLE AS PPL ON U.ID = PPL.USERS_ID AND PPL.ACTIVE = 'Y' ");
		sb.append(" 	LEFT OUTER JOIN PI on P.ID = PI.ID ");
		sb.append(" WHERE P.ID IN (").append(ids).append(")");
		return sb.toString();

	}
	
	public static String getParkingPeople(String ids){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" 	LTRIM(RTRIM( ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append(" 		ELSE '' ");
		sb.append(" 	END + ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append(" 		ELSE '' ");
		sb.append(" 	END +   ");       
		sb.append(" 	CASE ");
		sb.append(" 		WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append(" 		ELSE '' ");
		sb.append(" 	END ");
		sb.append("      )) AS NAME,   "); 
		sb.append(" 	R.ID AS REF_USERS_ID, ");
		sb.append(" 	U.EMAIL, ");
		sb.append(" 	U.USERNAME, ");
		sb.append(" 	PPL.PHONE_WORK, ");
		sb.append(" 	PPL.PHONE_CELL, ");
		sb.append(" 	PPL.PHONE_HOME, ");
		sb.append(" 	T.DESCRIPTION as TYPE ");
		sb.append(" FROM ");
		sb.append(" 	PROJECT P  ");
		sb.append(" 	JOIN REF_PROJECT_USERS  REF on P.ID = REF.PROJECT_ID AND REF.ACTIVE = 'Y' ");
		sb.append(" 	JOIN REF_USERS AS R ON REF.REF_USERS_ID = R.ID ");   
		sb.append(" 	JOIN LKUP_USERS_TYPE T on R.LKUP_USERS_TYPE_ID = T.ID "); 
		sb.append(" 	JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0 AND U.ACTIVE = 'Y' ");
		sb.append(" 	LEFT OUTER JOIN PEOPLE AS PPL ON U.ID = PPL.USERS_ID AND PPL.ACTIVE = 'Y' ");
		sb.append(" WHERE P.ID IN (").append(ids).append(")");
		return sb.toString();

	}
	
	public static String getParkingZones(String ids) {
		int id = Operator.toInt(ids);
		if (id < 1) { return ""; }
		TypeInfo entity = EntityAgent.getEntity("project", id);
		int landid = entity.getLowestEntity();
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		T.ID, ");
		sb.append(" 		TYPE.DESCRIPTION AS FIELD, ");
		sb.append(" 		T.DIVISION AS VALUE, ");
		sb.append(" 		T.INFO, ");
		sb.append(" 		TYPE.ID AS TYPE_ID, ");
		sb.append(" 		TYPE.REQUIRED   ");
		sb.append(" 	 FROM ");
		sb.append(" 		LKUP_DIVISIONS_TYPE AS TYPE ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			LKUP_DIVISIONS AS T ");   
		sb.append(" 	  		JOIN REF_LSO_DIVISIONS AS R ON T.ID = R.LKUP_DIVISIONS_ID  AND R.ACTIVE = 'Y' AND T.ACTIVE = 'Y' AND R.LSO_ID = ").append(landid);
		sb.append("  		) ON T.LKUP_DIVISIONS_TYPE_ID = TYPE.ID  ");
		sb.append(" 	WHERE ");
		sb.append(" 		TYPE.ACTIVE = 'Y' ");
		sb.append(" 		and ");
		sb.append(" 		TYPE.ISDOT = 'Y' ");

		sb.append(" 	UNION ");

		sb.append(" 	SELECT ");
		sb.append(" 		T.ID, ");
		sb.append(" 		TYPE.DESCRIPTION AS FIELD, ");
		sb.append(" 		T.DIVISION AS VALUE, ");
		sb.append(" 		T.INFO, ");
		sb.append(" 		TYPE.ID AS TYPE_ID, ");
		sb.append(" 		TYPE.REQUIRED   ");
		sb.append(" 	 FROM ");
		sb.append(" 		LKUP_DIVISIONS_TYPE AS TYPE ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			LKUP_DIVISIONS AS T ");   
		sb.append(" 	  		JOIN REF_LSO_DIVISIONS AS R ON T.ID = R.LKUP_DIVISIONS_ID  AND R.ACTIVE = 'Y' AND T.ACTIVE = 'Y' AND R.LSO_ID = ").append(landid);
		sb.append("  		) ON T.LKUP_DIVISIONS_TYPE_ID = TYPE.ID  ");
		sb.append(" 	WHERE ");
		sb.append(" 		TYPE.ACTIVE = 'Y' ");
		sb.append(" 		and ");
		sb.append(" 		TYPE.DEFLT = 'Y' ");
		sb.append(" )");
		sb.append(" SELECT DISTINCT * FROM Q ");
		return sb.toString();

	}
	
	
	public static String getParkingProjects(String ids, String userId){
		StringBuilder sb = new StringBuilder();
		
		sb.append(" WITH AD AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		DISTINCT P.ID, CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(LS.PRE_DIR, '') + ' ' + COALESCE(L.STR_MOD, '') + ' ' + LS.STR_NAME + ' ' + LS.STR_TYPE + ' ' + COALESCE(L.UNIT, '') AS ADDRESS ");
		sb.append(" 	FROM ");
		sb.append(" 		PROJECT P ");
		sb.append(" 		JOIN REF_LSO_PROJECT R on P.ID=R.PROJECT_ID   AND R.ACTIVE='Y' 		");
		sb.append(" 		JOIN LSO L on R.LSO_ID=L.ID ");
		sb.append(" 		JOIN LSO_STREET LS on L.LSO_STREET_ID=LS.ID ");
		sb.append(" 	WHERE ");
		sb.append(" 		P.ID in (").append(ids).append(") ");
		sb.append(" 	)   ");

		sb.append(" SELECT DISTINCT ");
		sb.append(" 	P.*, ");
		sb.append(" 	RAP.ID as ACCOUNT_NO, ");
		sb.append(" 	ATT.STATUS as LKUP_PROJECT_STATUS_TEXT, ");
		sb.append(" 	ATT.EXPIRED, ");
		sb.append(" 	CASE WHEN ATT.EXPIRED = 'Y' THEN 10 ELSE 0 END AS ORDR, ");
		sb.append(" 	ATP.TYPE as LKUP_PROJECT_TYPE_TEXT, ");
		sb.append(" 	AD.ADDRESS as ADDRESS, ");
		sb.append(" 	RAP.NO_SPACES, ");
		sb.append(" 	RAP.NO_CARS, ");
		sb.append(" 	RAP.APPROVED_SPACE, ");
		sb.append(" 	CU.USERNAME AS CREATED, "); 
		sb.append(" 	UP.USERNAME as UPDATED, ");
		sb.append(" 	CONVERT(VARCHAR(10),P.CREATED_DATE,101) as P_CREATED_DATE, ");
		sb.append(" 	CONVERT(VARCHAR(10),P.UPDATED_DATE,101) as P_UPDATED_DATE "); 
		if(Operator.hasValue(userId)) {
			sb.append(" , PU.PRIMARY_CONTACT "); 
		}
		sb.append(" FROM ");
		sb.append(" 	PROJECT P ");
		sb.append(" 	LEFT OUTER JOIN REF_PROJECT_PARKING RAP on P.ID= RAP.PROJECT_ID ");
		sb.append(" 	JOIN LKUP_PROJECT_STATUS ATT ON P.LKUP_PROJECT_STATUS_ID=ATT.ID ");
		sb.append(" 	JOIN LKUP_PROJECT_TYPE ATP ON P.LKUP_PROJECT_TYPE_ID=ATP.ID ");
		sb.append(" 	LEFT OUTER JOIN AD on P.ID = AD.ID ");
		sb.append(" 	LEFT OUTER JOIN USERS CU on P.CREATED_BY = CU.ID ");
		sb.append("		LEFT OUTER JOIN USERS UP on P.UPDATED_BY = UP.ID "); 
		if(Operator.hasValue(userId)){
			sb.append("   JOIN REF_PROJECT_USERS PU on P.ID = PU.PROJECT_ID");
			sb.append("   JOIN REF_USERS U on PU.REF_USERS_ID = U.ID AND U.USERS_ID = ").append(userId);
		}
		sb.append(" WHERE ");
		sb.append(" 	P.ID in (").append(ids).append(") ");
		sb.append(" 	and ");
		sb.append(" 	ATP.ISDOT ='Y' ");
		sb.append(" 	and ");
		sb.append(" 	P.ACTIVE='Y'");
		sb.append(" ORDER BY ORDR, ADDRESS ASC ");
		return sb.toString();

	}
	
	public static String getParkingExemptions(String ids){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT TOP 50 ");
		sb.append(" 	ACT_NBR, ");
		sb.append(" 	TYPE, ");
		sb.append(" 	CONVERT(VARCHAR(10), START_DATE,101) START_DATE, ");
		sb.append(" 	CONVERT(VARCHAR(10),EXP_DATE,101) EXP_DATE, ");
		sb.append(" 	A.ID, ");
		sb.append(" 	PROJECT_ID, ");
		sb.append(" 	QTY, ");
		sb.append(" 	LAT.DESCRIPTION as TYPE, ");
		sb.append(" 	CU.USERNAME AS CREATED, "); 
		sb.append(" 	UP.USERNAME as UPDATED,CONVERT(VARCHAR(10),A.CREATED_DATE,101) as A_CREATED_DATE,CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as A_UPDATED_DATE ");
		sb.append(" FROM ");
		sb.append(" 	ACTIVITY A ");
		sb.append(" 	JOIN LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID  ");
		sb.append(" 	JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID AND S.LIVE = 'Y' ");
		sb.append(" 	LEFT OUTER JOIN USERS CU on A.CREATED_BY = CU.ID ");
		sb.append("		LEFT OUTER JOIN USERS UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" WHERE ");
		sb.append(" 	A.PROJECT_ID in  (").append(ids).append(")");
		sb.append(" 	AND ");
		sb.append(" 	LAT.ISDOT='Y' ");
		sb.append(" 	AND ");
		sb.append(" 	LAT.DOT_EXEMPTION='Y'    ");   
		sb.append(" ORDER BY A.CREATED_DATE DESC  ");
		return sb.toString();

	}

	public static String getParkingPermits(String ids,String startdate, String enddate){
		StringBuilder sb = new StringBuilder();
//	COMMENTED BY ALAIN - NOT SURE WHY THE WITH CLAUSE CAN NOT BE JOINED IN THE MAIN SELECT QUERY
//		sb.append(" WITH Q AS ");
//		sb.append(" ( "); 
//		sb.append("   SELECT "); 
//		sb.append("     TOP 1 "); 
//		sb.append("     ATS.ACTIVITY_ID, "); 
//		sb.append("     ATS.LKUP_ACT_STATUS_ID,"); 
//		sb.append("     LAS.STATUS "); 
//		sb.append("   FROM "); 
//		sb.append("     ACT_STATUS ATS "); 
//		sb.append("     LEFT OUTER JOIN ACTIVITY A on ATS.ACTIVITY_ID = A.ID ");
//		sb.append("     LEFT OUTER JOIN LKUP_ACT_STATUS LAS on ATS.LKUP_ACT_STATUS_ID = LAS.ID ");
//		sb.append("   WHERE "); 
//		sb.append("     A.PROJECT_ID in (").append(ids).append(") "); 
//		sb.append("     AND "); 
//		sb.append("     ATS.ACTIVE = 'Y' "); 
//		sb.append("   ORDER BY "); 
//		sb.append("     DATE DESC ");
//		sb.append(" ) ");
		
		sb.append(" SELECT ");
		sb.append(" 	A.ID, ");
		sb.append(" 	A.ACT_NBR, ");
		sb.append(" 	A.PROJECT_ID, ");
		sb.append(" 	CONVERT(VARCHAR(10),A.START_DATE,101) START_DATE, ");
		sb.append(" 	CONVERT(VARCHAR(10),A.EXP_DATE,101) EXP_DATE, ");
		sb.append(" 	LAT.DESCRIPTION as TYPE, ");
		sb.append(" 	SUM(SD.FEE_AMOUNT) as AMOUNT, ");
		sb.append(" 	SUM(SD.FEE_PAID) as PAID, ");
		sb.append(" 	SUM(SD.BALANCE_DUE) as BALANCE, ");
		sb.append(" 	A.PRINTED, "); 
		sb.append(" 	LAT.DOT_STICKERS, "); 
		sb.append(" 	ST.STATUS, "); 
		sb.append(" 	ST.LIVE, "); 
		sb.append(" 	CU.USERNAME AS CREATED, "); 
		sb.append(" 	UP.USERNAME as UPDATED, ");
		sb.append(" 	CONVERT(VARCHAR(10),A.CREATED_DATE,101) as CREATED_DATE, ");
		sb.append(" 	CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as UPDATED_DATE ");
		
		sb.append(" FROM ");
		sb.append(" 	ACTIVITY A ");
		sb.append(" 	JOIN LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID = LAT.ID AND A.ACTIVE='Y' ");
		sb.append(" 	JOIN LKUP_ACT_STATUS AS ST ON A.LKUP_ACT_STATUS_ID = ST.ID AND ST.LIVE = 'Y' AND ST.EXPIRED <> 'Y' ");
		sb.append(" 	LEFT OUTER JOIN REF_ACT_STATEMENT RAS on A.ID=RAS.ACTIVITY_ID  ");
		sb.append(" 	LEFT OUTER JOIN STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y'   ");
		sb.append(" 	LEFT OUTER JOIN STATEMENT_DETAIL SD on  S.ID=SD.STATEMENT_ID  ");
		sb.append(" 	LEFT OUTER JOIN FEE_GROUP FG   on SD.GROUP_ID=FG.ID ");

//		sb.append(" 	LEFT OUTER JOIN Q on A.ID = Q.ACTIVITY_ID ");

		sb.append(" 	LEFT OUTER JOIN USERS CU on A.CREATED_BY = CU.ID ");
		sb.append("		LEFT OUTER JOIN USERS UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" 	WHERE ");
		sb.append("			A.PROJECT_ID in  (").append(ids).append(") ");
		sb.append(" 		AND ");
		sb.append("			LAT.ISDOT='Y' ");
		sb.append("			AND ");
		sb.append("			LAT.DOT_EXEMPTION='N' "); 
		
		if(Operator.hasValue(startdate) && Operator.hasValue(enddate)){
			sb.append(" 	AND ");
			sb.append("		A.START_DATE >= '").append(startdate).append("' ");
			sb.append(" 	AND ");
			sb.append("		ST.EXPIRED = 'N' ");
//			sb.append("		and ");
//			sb.append("		A.EXP_DATE <= '").append(enddate).append("'   ");
		}
		
		sb.append(" 	GROUP BY ");
		sb.append("			A.ID, ");
		sb.append("			A.ACT_NBR, ");
		sb.append("			LAT.DESCRIPTION, ");
		sb.append("			A.PROJECT_ID, ");
		sb.append("			A.START_DATE, ");
		sb.append("			A.EXP_DATE, ");
		sb.append("			A.CREATED_DATE, ");
		sb.append("			A.UPDATED_DATE, ");
		sb.append("			A.CREATED_BY, ");
		sb.append("			A.UPDATED_BY, ");
		sb.append("			A.PRINTED, ");
		sb.append(" 		LAT.DOT_STICKERS, "); 
		sb.append("			ST.STATUS, ");
		sb.append("			ST.LIVE, ");
		sb.append("			CU.USERNAME, ");
		sb.append("			UP.USERNAME  ");
		sb.append(" 	ORDER BY ");
		sb.append("			A.CREATED_DATE DESC, ");
		sb.append("			LAT.DESCRIPTION  ");
		return sb.toString();

	}

	/**
	 * Reformatted by Alain. Check commented code below.
	 * @deprecated Use getCount(String projectids,String startdate,String enddate,boolean current)
	 */
	public static String getParkingPermitsCount(String ids,String startdate,String enddate,boolean current){
		StringBuilder sb = new StringBuilder();
		
		if(current){
			sb.append(" select ");
			sb.append(" 	COUNT(A.ID) as COUNT, ");
			sb.append(" 	LAT.ID, ");
			sb.append(" 	LAT.DESCRIPTION as TYPE, ");
			sb.append(" 	LAT.DURATION_MAX, ");
			sb.append(" 	LAT.ISDOT, ");
			sb.append(" 	RAT.APPROVED_SPACE, ");
			sb.append(" 	RAT.NO_CARS, ");
			sb.append(" 	RAT.NO_SPACES, ");
			sb.append(" 	LAT.DOT_EXEMPTION ");
			sb.append(" from ");
			sb.append(" 	activity A ");
			sb.append(" 	join LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID ");
			sb.append(" 	join LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID=LAS.ID ");
			sb.append(" 	join REF_PROJECT_PARKING RAT on A.PROJECT_ID=RAT.PROJECT_ID ");
			sb.append(" 	where ");
			//sb.append(" 	(A.project_id in (").append(ids).append(") or RAT.ID in (").append(ids).append(") ) ");
			sb.append(" 	A.project_id in (").append(ids).append(") ");
			sb.append(" 	and ");
			sb.append(" 	LAT.ISDOT='Y'     ");   
			sb.append(" 	and ");
			sb.append(" 	A.START_DATE >= '").append(startdate).append("' and A.EXP_DATE <= '").append(enddate).append("'   ");   
			sb.append(" 	and ");
			sb.append(" 	LAS.LIVE='Y' AND LAS.EXPIRED='N'  ");   
			sb.append(" 	group by ");
			sb.append(" 	LAT.DESCRIPTION, ");
			sb.append(" 	LAT.ID, ");
			sb.append(" 	LAT.DURATION_MAX, ");
			sb.append(" 	LAT.ISDOT, ");
			sb.append(" 	RAT.APPROVED_SPACE, ");
			sb.append(" 	RAT.NO_CARS, ");
			sb.append(" 	RAT.NO_SPACES, ");
			sb.append(" 	LAT.DOT_EXEMPTION ");
		} else {
			sb.append(" WITH Q AS ( ");   
			sb.append(" 	select ");
			sb.append(" 		COUNT(A.ID) as COUNT, ");
			sb.append(" 		LAT.ID, ");
			sb.append(" 		LAT.DESCRIPTION as TYPE, ");
			sb.append(" 		LAT.DURATION_MAX, ");
			/*sb.append(" 		LAT.CONFIGURATION_GROUP_ID, ");
			sb.append(" 		c.c_value, ");
			sb.append(" 		c.LKUP_ACT_TYPE_ID, ");*/
			sb.append(" 	RAT.APPROVED_SPACE, ");
			sb.append(" 	RAT.NO_CARS, ");
			sb.append(" 	RAT.NO_SPACES ");
			//sb.append(" 		LAT1.DURATION_MAX CONF_DURATION_MAX ");
			sb.append(" 	from ");
			sb.append(" 		activity A ");
			sb.append(" 	join ");
			sb.append(" 		LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID ");
			sb.append(" 		join REF_PROJECT_PARKING RAT on A.PROJECT_ID=RAT.PROJECT_ID ");
			sb.append("         JOIN LKUP_ACT_STATUS AS LAS ON A.LKUP_ACT_STATUS_ID = LAS.ID ");
			/*sb.append(" 		left outer join CONFIGURATION_GROUP cg on LAT.CONFIGURATION_GROUP_ID = cg.id ");
			sb.append(" 		left outer join CONFIGURATION c on LAT.CONFIGURATION_GROUP_ID = c.CONFIGURATION_GROUP_ID	");
			sb.append(" 		left outer join LKUP_CONFIGURATION lc on c.LKUP_CONFIGURATION_ID = lc.ID ");
			sb.append(" 		left outer join LKUP_ACT_TYPE LAT1 on c.LKUP_ACT_TYPE_ID=LAT1.ID   ");*/
			sb.append(" 	where ");
//			sb.append(" 		(A.project_id in  (").append(ids).append(") or RAT.ID in (").append(ids).append(") )");
			sb.append(" 		A.project_id in (").append(ids).append(") ");
			sb.append(" 	 	and LAT.ISDOT='Y'     ");
			sb.append(" 	and ");
			sb.append(" 	LAS.LIVE='Y' AND LAS.EXPIRED='N'  ");   
			sb.append(" 	and ");
			sb.append(" 	    convert(varchar(10), start_date, 111) >= CASE WHEN LAT.DURATION_MAX_DAYS = 1 THEN convert(varchar(10),getdate(),111) ELSE convert(varchar(10),'").append(startdate).append("', 111)  END  ");
			sb.append("     and ");
			sb.append("         convert(varchar(10), exp_date, 111) <= CASE WHEN LAT.DURATION_MAX_DAYS = 1 THEN convert(varchar(10),getdate(),111)  ELSE convert(varchar(10),'").append(enddate).append("', 111)   END  ");   
			sb.append(" 	group by ");
			sb.append(" 		LAT.DESCRIPTION, ");
			sb.append(" 		LAT.ID, ");
			sb.append(" 		LAT.DURATION_MAX, ");
			/*sb.append(" 		LAT.CONFIGURATION_GROUP_ID, ");
			sb.append(" 		c.c_value, ");
			sb.append(" 		c.LKUP_ACT_TYPE_ID, ");*/
			sb.append(" 	RAT.APPROVED_SPACE, ");
			sb.append(" 	RAT.NO_CARS, ");
			sb.append(" 	RAT.NO_SPACES ");
			//sb.append(" 		LAT1.DURATION_MAX ");
			sb.append(" )   ");   
			sb.append(" select ");
			sb.append(" 	CASE WHEN Q.ID is null THEN L.ID ELSE Q.ID END AS ID,   ");   
			sb.append(" 	CASE WHEN Q.ID is null THEN 0 ELSE Q.COUNT END AS COUNT,   ");   
			sb.append(" 	L.DESCRIPTION as TYPE, ");
//			sb.append(" 	CASE WHEN LOWER(L.DESCRIPTION) = 'overnight parking' THEN CASE WHEN Q.APPROVED_SPACE IS NOT NULL THEN Q.APPROVED_SPACE WHEN COALESCE(Q.NO_CARS, 0) = 0 THEN 0 WHEN COALESCE(Q.NO_SPACES, 0) = 0 THEN 0 ELSE Q.NO_CARS - Q.NO_SPACES END ");
//			sb.append("     ELSE L.DURATION_MAX END AS DURATION_MAX, ");
			sb.append(" 	L.DURATION_MAX, ");
			sb.append(" 	L.ISDOT, ");
			sb.append(" 	L.DOT_EXEMPTION, ");
			sb.append(" 	L.CONFIGURATION_GROUP_ID, ");
			sb.append(" 	c.c_value, ");
			sb.append(" 	c.LKUP_ACT_TYPE_ID, ");
			sb.append(" 	LAT1.DURATION_MAX CONF_DURATION_MAX ");   
			sb.append(" FROM ");
			sb.append(" 	LKUP_ACT_TYPE L ");   
			sb.append(" 	left outer join Q on L.ID = Q.ID   ");   
			sb.append(" 	left outer join CONFIGURATION_GROUP cg on L.CONFIGURATION_GROUP_ID = cg.id ");
			sb.append(" 	left outer join CONFIGURATION c on L.CONFIGURATION_GROUP_ID = c.CONFIGURATION_GROUP_ID	");
			sb.append(" 	left outer join LKUP_CONFIGURATION lc on c.LKUP_CONFIGURATION_ID = lc.ID ");
			sb.append(" 	left outer join LKUP_ACT_TYPE LAT1 on c.LKUP_ACT_TYPE_ID=LAT1.ID   ");
			sb.append(" WHERE ");
			sb.append(" 	L.ACTIVE='Y' ");
			sb.append(" 	and ");
			sb.append(" 	L.ISDOT='Y' ");
			sb.append(" 	and ");
			sb.append(" 	L.ONLINE='Y' ");   

		}
		
		return sb.toString();

	}
	
	public static String getCount(String projectids, String startdate, String enddate, boolean current){
		StringBuilder sb = new StringBuilder();
		
		if(current){
			sb.append(" select ");
			sb.append(" 	COUNT(A.ID) as COUNT, ");
			sb.append(" 	LAT.ID, ");
			sb.append(" 	LAT.DESCRIPTION as TYPE, ");
			sb.append(" 	LAT.DURATION_MAX, ");
			sb.append(" 	LAT.ISDOT, ");
			sb.append(" 	RAT.APPROVED_SPACE, ");
			sb.append(" 	RAT.NO_CARS, ");
			sb.append(" 	RAT.NO_SPACES, ");
			sb.append(" 	LAT.DOT_EXEMPTION ");
			sb.append(" from ");
			sb.append(" 	activity A ");
			sb.append(" 	join LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID AND A.ACTIVE = 'Y' ");
			sb.append(" 	join LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID=LAS.ID ");
			sb.append(" 	join REF_PROJECT_PARKING RAT on A.PROJECT_ID=RAT.PROJECT_ID ");
			sb.append(" 	where ");
			sb.append(" 	A.project_id in (").append(projectids).append(") ");
			sb.append(" 	and ");
			sb.append(" 	LAT.ISDOT='Y'     ");   
			sb.append(" 	and ");
			sb.append(" 	A.START_DATE >= '").append(startdate).append("' and A.EXP_DATE <= '").append(enddate).append("'   ");   
			sb.append(" 	and ");
			sb.append(" 	LAS.LIVE='Y' AND LAS.EXPIRED='N'  ");   
			sb.append(" 	group by ");
			sb.append(" 	LAT.DESCRIPTION, ");
			sb.append(" 	LAT.ID, ");
			sb.append(" 	LAT.DURATION_MAX, ");
			sb.append(" 	LAT.ISDOT, ");
			sb.append(" 	RAT.APPROVED_SPACE, ");
			sb.append(" 	RAT.NO_CARS, ");
			sb.append(" 	RAT.NO_SPACES, ");
			sb.append(" 	LAT.DOT_EXEMPTION ");
		} else {
			sb.append(" WITH ");
			sb.append(" F AS ( ");
			sb.append(" 	SELECT ");
			sb.append(" 		A.ID AS ACTIVITY_ID, ");
		
			
			 //2/7/2022
			//sb.append(" 		LAT.ID, ");
			//sb.append(" 		LAT.DESCRIPTION AS TYPE, ");
			//sb.append(" 		LAT.DURATION_MAX, ");
			
			sb.append(" CASE WHEN LAT.ID = 279 THEN 251 WHEN LAT.ID = 280 THEN 252 ELSE LAT.ID END AS ID, ");
			sb.append(" CASE WHEN LAT.ID = 279 THEN 'Overnight Parking (TR)' WHEN LAT.ID = 280 THEN 'Preferential Parking (TR)' ELSE LAT.DESCRIPTION END AS TYPE,"); 
			sb.append(" CASE WHEN LAT.ID = 279 THEN 3 WHEN LAT.ID = 280 THEN 3 ELSE LAT.DURATION_MAX END AS DURATION_MAX, ");
			
			
			
			sb.append(" 		RAT.APPROVED_SPACE, ");
			sb.append(" 		RAT.NO_CARS, ");
			sb.append(" 		RAT.NO_SPACES, ");
			sb.append(" 		LAT.DURATION_MAX_DAYS AS MDAYS, ");
			sb.append(" 		LAT.DURATION_MAX_MONTHS AS MMONTHS, ");
			sb.append(" 		LAT.DURATION_MAX_YEARS AS MYEARS, ");
			sb.append(" 		CASE ");
			sb.append(" 			WHEN LAT.DURATION_MAX_DAYS > 0 THEN ");
			sb.append(" 				CASE ");
			sb.append(" 					WHEN CAST(A.START_DATE AS DATE) > CAST(GETDATE() - LAT.DURATION_MAX_DAYS AS DATE) AND CAST(A.START_DATE AS DATE) <= CAST(GETDATE() AS DATE) THEN 'Y' ");
			sb.append(" 				ELSE 'N' END ");
		
			//sb.append(" 			WHEN LAT.DURATION_MAX_MONTHS = 12 THEN ");
			
			sb.append(" 			WHEN LAT.DURATION_MAX_MONTHS = 12  OR LAT.ID in (279,280) THEN ");
			
			sb.append(" 				CASE ");
			sb.append(" 					WHEN CAST(A.START_DATE AS DATE) >= CAST('").append(startdate).append("' AS DATE) AND CAST(A.START_DATE AS DATE) <= CAST('").append(enddate).append("' AS DATE) THEN 'Y' ");
			sb.append(" 				ELSE 'N' END ");
			sb.append(" 			WHEN LAT.DURATION_MAX_MONTHS > 0 THEN ");
			sb.append(" 				CASE ");
			sb.append(" 					WHEN DATEPART(YEAR, A.START_DATE) = DATEPART(YEAR, GETDATE()) THEN ");
			sb.append(" 						CASE ");
			sb.append(" 							WHEN DATEPART(MONTH, A.START_DATE) > DATEPART(MONTH, GETDATE()) - LAT.DURATION_MAX_MONTHS AND DATEPART(MONTH, A.START_DATE) <= DATEPART(MONTH, GETDATE()) THEN 'Y' ");
			sb.append(" 						ELSE 'N' END ");
			sb.append(" 				ELSE 'N' END ");
			sb.append(" 			WHEN LAT.DURATION_MAX_YEARS > 0 THEN ");
			sb.append(" 				CASE ");
			sb.append(" 					WHEN DATEPART(YEAR, A.START_DATE) >= DATEPART(YEAR, GETDATE()) - LAT.DURATION_MAX_YEARS AND DATEPART(YEAR, A.START_DATE) <= DATEPART(YEAR, GETDATE()) THEN 'Y' ");
			sb.append(" 				ELSE 'N' END ");
			sb.append(" 			ELSE 'N' END ");
			sb.append(" 		AS DOCOUNT ");
			sb.append(" 					 ");
			sb.append(" 					 ");
			sb.append(" 			 ");
			sb.append(" 	FROM ");
			sb.append(" 		ACTIVITY A ");
			sb.append(" 	JOIN ");
			sb.append(" 		LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID=LAT.ID AND A.ACTIVE = 'Y' ");
			sb.append(" 		JOIN REF_PROJECT_PARKING RAT ON A.PROJECT_ID=RAT.PROJECT_ID ");
			sb.append(" 		JOIN LKUP_ACT_STATUS AS LAS ON A.LKUP_ACT_STATUS_ID = LAS.ID ");
			sb.append(" 	WHERE ");
			sb.append(" 		A.PROJECT_ID IN (").append(projectids).append(") ");
			sb.append(" 	 	AND ");
			sb.append(" 	 	LAT.ISDOT='Y' ");
			sb.append(" 		AND ");
			sb.append(" 		LAS.LIVE='Y' ");
			sb.append(" 		AND ");
			sb.append(" 		LAS.EXPIRED='N' ");
			sb.append(" ) ");
			
			
			sb.append(", P AS( ");
			sb.append("        SELECT ");
			sb.append("			PK.ID AS ACCOUNT_NUMBER, ");
			sb.append("			PK.PROJECT_ID, ");
			sb.append("			COALESCE(APPROVED_SPACE, 0) AS APPROVED_SPACE, ");
			sb.append("			COALESCE(NO_CARS, 0) AS NO_CARS, ");
			sb.append("			COALESCE(NO_SPACES, 0) AS NO_SPACES ");
			sb.append("			FROM ");
			sb.append("			REF_PROJECT_PARKING AS PK ");
			sb.append(" 		WHERE ");
			sb.append("			PK.PROJECT_ID IN (").append(projectids).append(") ");
			sb.append(") ");

			
			
			sb.append(" , Q AS ( ");
			sb.append(" 	SELECT ");
			sb.append(" 		COUNT(DISTINCT ACTIVITY_ID) AS COUNT, ");
			sb.append(" 		ID, ");
			sb.append(" 		TYPE, ");
			sb.append(" 		DURATION_MAX, ");
			sb.append(" 		APPROVED_SPACE, ");
			sb.append(" 		NO_CARS, ");
			sb.append(" 		NO_SPACES ");
			sb.append(" 	FROM ");
			sb.append(" 		F ");
			sb.append(" 	WHERE ");
			sb.append(" 		DOCOUNT = 'Y' ");
			sb.append(" 	GROUP BY ");
			sb.append(" 		ID, ");
			sb.append(" 		TYPE, ");
			sb.append(" 		DURATION_MAX, ");
			sb.append(" 		APPROVED_SPACE, ");
			sb.append(" 		NO_CARS, ");
			sb.append(" 		NO_SPACES ");
			sb.append("  ");
			sb.append(" ) ");
			sb.append("  ");
			sb.append(" SELECT ");
			sb.append(" 	CASE WHEN Q.ID IS NULL THEN L.ID ELSE Q.ID END AS ID, ");
			sb.append(" 	CASE WHEN Q.ID IS NULL THEN 0 ELSE Q.COUNT END AS COUNT, ");
			sb.append(" 	L.DESCRIPTION AS TYPE, ");
		
			sb.append(" 	 CASE ");
			sb.append(" 	                     WHEN L.ID = 251 THEN P.APPROVED_SPACE ");
			sb.append(" 	                     ELSE L.DURATION_MAX ");
			sb.append(" 	     END AS DURATION_MAX, ");
			sb.append(" 	  ");

			//sb.append(" 	L.DURATION_MAX, ");
			sb.append(" 	L.ISDOT, ");
			sb.append(" 	L.DOT_EXEMPTION, ");
			sb.append(" 	L.CONFIGURATION_GROUP_ID, ");
			sb.append(" 	C.C_VALUE, ");
			sb.append(" 	C.LKUP_ACT_TYPE_ID, ");
			sb.append(" 	LAT1.DURATION_MAX CONF_DURATION_MAX ");
			sb.append(" FROM ");
			sb.append(" 	LKUP_ACT_TYPE L ");
			sb.append(" 	LEFT OUTER JOIN Q ON L.ID = Q.ID ");
			sb.append(" 	LEFT OUTER JOIN CONFIGURATION_GROUP CG ON L.CONFIGURATION_GROUP_ID = CG.ID ");
			sb.append(" 	LEFT OUTER JOIN CONFIGURATION C ON L.CONFIGURATION_GROUP_ID = C.CONFIGURATION_GROUP_ID ");
			sb.append(" 	LEFT OUTER JOIN LKUP_CONFIGURATION LC ON C.LKUP_CONFIGURATION_ID = LC.ID ");
			sb.append(" 	LEFT OUTER JOIN LKUP_ACT_TYPE LAT1 ON C.LKUP_ACT_TYPE_ID=LAT1.ID ");
			sb.append("     JOIN P ON 1=1 ");
			sb.append(" WHERE ");
			sb.append(" 	L.ACTIVE='Y' ");
			sb.append(" 	AND ");
			sb.append(" 	L.ISDOT='Y' ");
			sb.append(" 	AND ");
			sb.append(" 	L.ONLINE='Y' ");

		}
		
		return sb.toString();

	}
	
	public static String count(String projectids){
		Timekeeper st = new Timekeeper();
		if (st.MONTH() < 10) {
			st.addYear(-1);
		}
		st.setMonth(10);
		st.setDay(1);
		Timekeeper en = st.copy();
		en.addYear(1);
		en.addDay(-1);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH ");
		sb.append(" F AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		A.ID AS ACTIVITY_ID, ");
		sb.append(" 		LAT.ID, ");
		sb.append(" 		LAT.DESCRIPTION AS TYPE, ");
		sb.append(" 		LAT.DURATION_MAX, ");
		sb.append(" 		RAT.APPROVED_SPACE, ");
		sb.append(" 		RAT.NO_CARS, ");
		sb.append(" 		RAT.NO_SPACES, ");
		sb.append(" 		LAT.DURATION_MAX_DAYS AS MDAYS, ");
		sb.append(" 		LAT.DURATION_MAX_MONTHS AS MMONTHS, ");
		sb.append(" 		LAT.DURATION_MAX_YEARS AS MYEARS, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN LAT.DURATION_MAX_DAYS > 0 THEN ");
		sb.append(" 				CASE ");
		sb.append(" 					WHEN CAST(A.START_DATE AS DATE) > CAST(GETDATE() - LAT.DURATION_MAX_DAYS AS DATE) AND CAST(A.START_DATE AS DATE) <= CAST(GETDATE() AS DATE) THEN 'Y' ");
		sb.append(" 				ELSE 'N' END ");
		sb.append(" 			WHEN LAT.DURATION_MAX_MONTHS = 12 THEN ");
		sb.append(" 				CASE ");
		sb.append(" 					WHEN CAST(A.START_DATE AS DATE) >= CAST('").append(st.getString("YYYY/MM/DD")).append("' AS DATE) AND CAST(A.START_DATE AS DATE) <= CAST('").append(en.getString("YYYY/MM/DD")).append("' AS DATE) THEN 'Y' ");
		sb.append(" 				ELSE 'N' END ");
		sb.append(" 			WHEN LAT.DURATION_MAX_MONTHS > 0 THEN ");
		sb.append(" 				CASE ");
		sb.append(" 					WHEN DATEPART(YEAR, A.START_DATE) = DATEPART(YEAR, GETDATE()) THEN ");
		sb.append(" 						CASE ");
		sb.append(" 							WHEN DATEPART(MONTH, A.START_DATE) > DATEPART(MONTH, GETDATE()) - LAT.DURATION_MAX_MONTHS AND DATEPART(MONTH, A.START_DATE) <= DATEPART(MONTH, GETDATE()) THEN 'Y' ");
		sb.append(" 						ELSE 'N' END ");
		sb.append(" 				ELSE 'N' END ");
		sb.append(" 			WHEN LAT.DURATION_MAX_YEARS > 0 THEN ");
		sb.append(" 				CASE ");
		sb.append(" 					WHEN DATEPART(YEAR, A.START_DATE) >= DATEPART(YEAR, GETDATE()) - LAT.DURATION_MAX_YEARS AND DATEPART(YEAR, A.START_DATE) <= DATEPART(YEAR, GETDATE()) THEN 'Y' ");
		sb.append(" 				ELSE 'N' END ");
		sb.append(" 			ELSE 'N' END ");
		sb.append(" 		AS DOCOUNT ");
		sb.append(" 					 ");
		sb.append(" 					 ");
		sb.append(" 			 ");
		sb.append(" 	FROM ");
		sb.append(" 		ACTIVITY A ");
		sb.append(" 	JOIN ");
		sb.append(" 		LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID=LAT.ID ");
		sb.append(" 		JOIN REF_PROJECT_PARKING RAT ON A.PROJECT_ID=RAT.PROJECT_ID ");
		sb.append(" 		JOIN LKUP_ACT_STATUS AS LAS ON A.LKUP_ACT_STATUS_ID = LAS.ID ");
		sb.append(" 	WHERE ");
		sb.append(" 		A.PROJECT_ID IN (").append(projectids).append(") ");
		sb.append(" 	 	AND ");
		sb.append(" 	 	LAT.ISDOT='Y' ");
		sb.append(" 		AND ");
		sb.append(" 		LAS.LIVE='Y' ");
		sb.append(" 		AND ");
		sb.append(" 		LAS.EXPIRED='N' ");
		sb.append(" ) ");
		sb.append(" , Q AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		COUNT(DISTINCT ACTIVITY_ID) AS COUNT, ");
		sb.append(" 		ID, ");
		sb.append(" 		TYPE, ");
		sb.append(" 		DURATION_MAX, ");
		sb.append(" 		APPROVED_SPACE, ");
		sb.append(" 		NO_CARS, ");
		sb.append(" 		NO_SPACES ");
		sb.append(" 	FROM ");
		sb.append(" 		F ");
		sb.append(" 	WHERE ");
		sb.append(" 		DOCOUNT = 'Y' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		ID, ");
		sb.append(" 		TYPE, ");
		sb.append(" 		DURATION_MAX, ");
		sb.append(" 		APPROVED_SPACE, ");
		sb.append(" 		NO_CARS, ");
		sb.append(" 		NO_SPACES ");
		sb.append("  ");
		sb.append(" ) ");
		sb.append("  ");
		sb.append(" SELECT ");
		sb.append(" 	CASE WHEN Q.ID IS NULL THEN L.ID ELSE Q.ID END AS ID, ");
		sb.append(" 	CASE WHEN Q.ID IS NULL THEN 0 ELSE Q.COUNT END AS COUNT, ");
		sb.append(" 	L.DESCRIPTION AS TYPE, ");
		sb.append(" 	L.DURATION_MAX, ");
		sb.append(" 	L.ISDOT, ");
		sb.append(" 	L.DOT_EXEMPTION, ");
		sb.append(" 	L.CONFIGURATION_GROUP_ID, ");
		sb.append(" 	C.C_VALUE, ");
		sb.append(" 	C.LKUP_ACT_TYPE_ID, ");
		sb.append(" 	LAT1.DURATION_MAX CONF_DURATION_MAX ");
		sb.append(" FROM ");
		sb.append(" 	LKUP_ACT_TYPE L ");
		sb.append(" 	LEFT OUTER JOIN Q ON L.ID = Q.ID ");
		sb.append(" 	LEFT OUTER JOIN CONFIGURATION_GROUP CG ON L.CONFIGURATION_GROUP_ID = CG.ID ");
		sb.append(" 	LEFT OUTER JOIN CONFIGURATION C ON L.CONFIGURATION_GROUP_ID = C.CONFIGURATION_GROUP_ID ");
		sb.append(" 	LEFT OUTER JOIN LKUP_CONFIGURATION LC ON C.LKUP_CONFIGURATION_ID = LC.ID ");
		sb.append(" 	LEFT OUTER JOIN LKUP_ACT_TYPE LAT1 ON C.LKUP_ACT_TYPE_ID=LAT1.ID ");
		sb.append("  ");
		sb.append(" WHERE ");
		sb.append(" 	L.ACTIVE='Y' ");
		sb.append(" 	AND ");
		sb.append(" 	L.ISDOT='Y' ");
		sb.append(" 	AND ");
		sb.append(" 	L.ONLINE='Y' ");
		return sb.toString();

	}
	
	/*public static String getParkingPermitsCount(String ids,String startdate,String enddate,boolean current){
		StringBuilder sb = new StringBuilder();
		
		if(current){
			sb.append(" select  COUNT(A.ID) as COUNT,LAT.ID,LAT.DESCRIPTION as TYPE ,LAT.MAX_ALLOWED,LAT.ISDOT,LAT.DOT_EXEMPTION from activity A      ");
			sb.append(" 	 join LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID  join REF_PROJECT_PARKING RAT on A.PROJECT_ID=RAT.PROJECT_ID  	 ");
			sb.append(" 	where (A.project_id in  (").append(ids).append(") or RAT.ID in (").append(ids).append(") ) ");
			sb.append(" 	and LAT.ISDOT='Y'     ");   
			sb.append(" 	and A.START_DATE >= '").append(startdate).append("' and A.EXP_DATE <= '").append(enddate).append("'   ");   
			sb.append(" 	 group by LAT.DESCRIPTION ,LAT.ID,LAT.MAX_ALLOWED,LAT.ISDOT,LAT.DOT_EXEMPTION ");
		}else {
			sb.append(" WITH Q AS (   ");   
			sb.append(" select  COUNT(A.ID) as COUNT,LAT.ID,LAT.DESCRIPTION as TYPE ,LAT.MAX_ALLOWED from activity A      ");
			sb.append(" 	 join LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID  join REF_PROJECT_PARKING RAT on A.PROJECT_ID=RAT.PROJECT_ID  	 ");
			sb.append(" 	where (A.project_id in  (").append(ids).append(") or RAT.ID in (").append(ids).append(") ) ");
			sb.append(" 	and LAT.ISDOT='Y'     ");   
			sb.append(" 	and A.START_DATE >= '").append(startdate).append("' and A.EXP_DATE <= '").append(enddate).append("'   ");   
			sb.append(" 	 group by LAT.DESCRIPTION ,LAT.ID,LAT.MAX_ALLOWED ");
			sb.append(" )   ");   
			sb.append(" select CASE WHEN Q.ID is null THEN L.ID ELSE Q.ID END AS ID,   ");   
			sb.append(" CASE WHEN Q.ID is null THEN 0 ELSE Q.COUNT END AS COUNT,   ");   
			sb.append(" L.DESCRIPTION as TYPE,L.MAX_ALLOWED,L.ISDOT,L.DOT_EXEMPTION    ");   
			sb.append(" FROM LKUP_ACT_TYPE L   ");   
			sb.append(" left outer join Q on L.ID = Q.ID   ");   
			sb.append(" WHERE ACTIVE='Y' and ISDOT='Y'    ");   

		}
		
		return sb.toString();

	}*/

	public static String getActType(int id) {
		if (id < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" T.* ");
		sb.append(" FROM ");
		sb.append(" LKUP_ACT_TYPE AS T ");
		sb.append(" WHERE ");
		sb.append(" T.ID = ").append(id);
		return sb.toString();
	}

	public static String insertApplicant(int id){
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT into REF_USERS (");
		sb.append("LKUP_USERS_TYPE_ID, USERS_ID");
		sb.append(",CREATED_BY,UPDATED_BY,CREATED_DATE,UPDATED_DATE,ACTIVE  ) VALUES (");
		sb.append(3).append(",").append(id).append(",").append(id).append(",").append(id).append(",").append(" CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Y')");
		return sb.toString();
	}

	/*public static String insertAccountUser(int refUser, int userid, int projectid) {
		StringBuilder sb = new StringBuilder();
		sb.append(";MERGE INTO REF_PROJECT_USERS T USING (SELECT PROJECT_ID, REF_USERS_ID) S ON (T.PROJECT_ID = S.PROJECT_ID and T.REF_USERS_ID = S.REF_USERS_ID)");
		sb.append(" WHEN MATCHED THEN");
		sb.append(" UPDATE SET T.PROJECT_ID =").append(projectid).append(", T.REF_USERS_ID = ").append(refUser);
		sb.append(", T.CREATED_BY =").append(userid).append(", T.UPDATED_BY=").append(userid);
		sb.append(", T.CREATED_DATE = CURRENT_TIMESTAMP, T.UPDATED_DATE=CURRENT_TIMESTAMP, T.ACTIVE='Y' ");
		sb.append(" WHEN NOT MATCHED THEN ");
		sb.append(" INSERT (");
		sb.append(" PROJECT_ID, REF_USERS_ID");
		sb.append(",CREATED_BY,UPDATED_BY,CREATED_DATE,UPDATED_DATE,ACTIVE  ) VALUES (");
		sb.append(projectid).append(",").append(refUser).append(",").append(userid).append(",").append(userid).append(",").append(" CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Y');");
		
		sb.append("IF EXISTS (SELECT 1 FROM REF_PROJECT_USERS WHERE PROJECT_ID=").append(projectid).append(" and REF_USERS_ID = ").append(refUser);
		sb.append(") UPDATE REF_PROJECT_USERS SET PROJECT_ID =").append(projectid).append(", REF_USERS_ID = ").append(refUser);
		sb.append(", CREATED_BY =").append(userid).append(", UPDATED_BY=").append(userid);
		sb.append(", CREATED_DATE = CURRENT_TIMESTAMP, UPDATED_DATE=CURRENT_TIMESTAMP, ACTIVE='Y' WHERE PROJECT_ID=").append(projectid).append(" and REF_USERS_ID = ").append(refUser);
		sb.append(" ELSE ");
		sb.append(" INSERT INTO REF_PROJECT_USERS (");
		sb.append(" PROJECT_ID, REF_USERS_ID");
		sb.append(",CREATED_BY,UPDATED_BY,CREATED_DATE,UPDATED_DATE,ACTIVE  ) VALUES (");
		sb.append(projectid).append(",").append(refUser).append(",").append(userid).append(",").append(userid).append(",").append(" CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Y');");
		return sb.toString();
	}*/
	
	/*public static String insertAccount(String account, String projectid){
		StringBuilder sb = new StringBuilder();
		sb.append(";MERGE INTO REF_PROJECT_PARKING T USING (SELECT ID, PROJECT_ID) S ON (T.PROJECT_ID = S.PROJECT_ID and T.ID = S.ID)");
		sb.append(" WHEN MATCHED THEN");
		sb.append(" UPDATE SET T.PROJECT_ID =").append(projectid).append(", T.ID = ").append(account);
		sb.append(" WHEN NOT MATCHED THEN");
		sb.append(" INSERT (ID, PROJECT_ID, ACTIVE)VALUES (");
		sb.append(account).append(",").append(projectid).append(", 'Y');");
		
		sb.append("IF EXISTS (SELECT 1 FROM REF_PROJECT_PARKING WHERE PROJECT_ID=").append(projectid).append(" and ID = ").append(account);
		sb.append(") UPDATE REF_PROJECT_PARKING SET PROJECT_ID =").append(projectid).append(", ACTIVE = 'Y' WHERE PROJECT_ID=").append(projectid).append(" and ID = ").append(account);
		sb.append(" ELSE ");
		sb.append(" INSERT INTO REF_PROJECT_PARKING (PROJECT_ID, ACTIVE)VALUES (");
		sb.append(projectid).append(", 'Y');");
		return sb.toString();
	}*/
	
	
	
	public static String acttype(String type, int typeid,boolean permit, boolean exemption) {
		if (Operator.equalsIgnoreCase(type, "project")) {
			TypeInfo e = EntityAgent.getEntity(type, typeid);
			if(exemption){
				return ActivitySQL.projectActType(e.getProjectid(),true);
			}
			return ActivitySQL.projectActType(e.getProjectid());
		}
		else {
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT ");
			sb.append(" T.ID ");
			sb.append(" , ");
			sb.append(" T.ID AS VALUE ");
			sb.append(" , ");
			sb.append(" T.TYPE AS TEXT ");
			sb.append(" , ");
			sb.append(" T.DESCRIPTION ");
			sb.append(" , ");
			sb.append(" 'N' AS SELECTED ");
			sb.append(" FROM ");
			//sb.append(" ACTIVITY AS A ");
			//sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND A.ID = ").append(typeid);
			sb.append("  LKUP_ACT_TYPE AS T  ");

			sb.append(" WHERE ISDOT='Y' ");
			if(permit){
				sb.append(" AND DOT_EXEMPTION ='N' ");
			}
			
			if(exemption){
				sb.append(" AND DOT_EXEMPTION ='Y' ");
			}
			
			return sb.toString();
		}
	}
	
	
	public static String onlinePrints(String group, String groupid, String startdate, String enddate, String selected) {
		StringBuilder sb = new StringBuilder();
		String sel[] = Operator.split(selected,"_");
		
		sb.append(" select distinct ");
		sb.append(" 	P.ID, ");
		sb.append(" 	CONVERT(varchar(100), ");
		sb.append(" 	L.STR_NO)+' '+COALESCE(L.STR_MOD, '')+' '+  LS.STR_NAME+' '+ LS.STR_TYPE+' '+ CASE WHEN PRE_DIR is null THEN '' ELSE PRE_DIR END+''+CASE WHEN L.UNIT is null then '' ELSE L.UNIT END as ADDRESS,  ");
		sb.append(" 	RAP.ID as ACCOUNT_NO, ");
		sb.append(" 	L.ID AS LSO_ID, ");
		sb.append(" 	A.PROJECT_ID, ");
		sb.append(" 	ATT.STATUS as LKUP_PROJECT_STATUS_TEXT, ");
		sb.append(" 	ATT.EXPIRED, ");
		sb.append(" 	ATP.TYPE as LKUP_PROJECT_TYPE_TEXT,  ");
		sb.append(" 	CU.USERNAME AS CREATED, "); 
		sb.append(" 	UP.USERNAME as UPDATED, ");
		sb.append(" 	CONVERT(VARCHAR(10),P.CREATED_DATE,101) as P_CREATED_DATE, ");
		sb.append(" 	CONVERT(VARCHAR(10),P.UPDATED_DATE,101) as P_UPDATED_DATE, ");
		sb.append(" 	BATCH_PRINT_ID "); 
		sb.append(" FROM ");
		sb.append(" 	PROJECT P        ");
		sb.append(" 	JOIN ACTIVITY A on P.ID = A.PROJECT_ID ");
		sb.append(" 	JOIN REF_LSO_PROJECT R on P.ID=R.PROJECT_ID ");
		sb.append(" 	JOIN LSO L on R.LSO_ID=L.ID ");
		sb.append(" 	JOIN LSO_STREET LS on L.LSO_STREET_ID=LS.ID ");
		sb.append(" 	JOIN REF_PROJECT_PARKING RAP on P.ID= RAP.PROJECT_ID ");
		sb.append(" 	JOIN LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID AND LAT.ACTIVE='Y' "); 
		sb.append(" JOIN LKUP_ACT_STATUS LAS ON A.LKUP_ACT_STATUS_ID = LAS.ID AND LAS.ACTIVE='Y'  AND LAS.ISSUED='Y' ");

		sb.append(" 	JOIN LKUP_PROJECT_STATUS ATT ON P.LKUP_PROJECT_STATUS_ID=ATT.ID ");
		sb.append(" 	JOIN LKUP_PROJECT_TYPE ATP ON P.LKUP_PROJECT_TYPE_ID=ATP.ID ");
		sb.append(" 	LEFT OUTER JOIN USERS CU on P.CREATED_BY = CU.ID ");
		sb.append(" 	LEFT OUTER JOIN USERS UP on P.UPDATED_BY = UP.ID "); 
		sb.append(" 	LEFT OUTER JOIN REF_ACT_STATEMENT RAS on A.ID=RAS.ACTIVITY_ID ");
		sb.append(" 	LEFT OUTER JOIN STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");
		sb.append(" 	LEFT OUTER JOIN STATEMENT_DETAIL SD on  S.ID=SD.STATEMENT_ID  ");
		sb.append(" 	LEFT OUTER JOIN FEE_GROUP FG   on SD.GROUP_ID=FG.ID ");
		
		if(group.equalsIgnoreCase("renewal")){
			if(sel.length > 1){
				sb.append("JOIN REF_PROJECT_DIVISIONS RPD ON ATP.ID = RPD.LKUP_PROJECT_TYPE_ID AND RPD.LKUP_DIVISIONS_ID = ");
				sb.append(sel[1]);
			}
		}
		
		sb.append("  WHERE "); 
		sb.append(" 	ATP.ISDOT ='Y' "); 
		sb.append(" 	AND "); 
		sb.append(" 	P.ACTIVE='Y' "); 
		sb.append(" 	AND "); 
		sb.append(" 	LAT.BATCH='Y' ");
		sb.append(" 	AND "); 
		sb.append(" 	LAS.ISSUED='Y' ");
		
		if (group.equalsIgnoreCase("renewal")){
			sb.append("	AND RENEWAL = 'Y'");
			sb.append(" AND LAT.ID IN (").append(sel[0]).append(") ");
		}
		else {
			if(Operator.toInt(groupid) > 0 && group.equalsIgnoreCase("batch")) {
				sb.append("	AND BATCH_PRINT_ID = ").append(groupid);
			}
			else {
				sb.append("	AND A.PRINTED='N' AND BATCH_PRINT_ID iS NULL ");
			}
			//sb.append(" AND S.FEE_AMOUNT > 0 AND S.BALANCE_DUE <= 0 ");
			sb.append("  AND (S.BALANCE_DUE <= 0  OR 	S.BALANCE_DUE is null) ");
		}
		
		if(Operator.hasValue(startdate) && Operator.hasValue(enddate)){
			sb.append(" 	AND ");
			sb.append(" 	A.START_DATE >= '").append(startdate).append("' ");
//			sb.append(" 	AND ");
//			sb.append(" 	A.EXP_DATE <= '").append(enddate).append("' ");
		}
		
		sb.append(" ORDER BY P.ID DESC ");
		
		return sb.toString();
		
	}
	
	
	public static String insertOnlineUser(int userid, String account, String lsoid, String association, String reside, String space, String size, int projectid, String status, String existing) {
		StringBuilder sb = new StringBuilder();		
		sb.append(" INSERT INTO REF_ACCOUNT_APPLICATION (");
		sb.append(" USERS_ID, ACCOUNT_NO, LSO_ID, LKUP_USERS_TYPE_ID, RESIDE, SPACE_AVAIL, NO_CARS");
		sb.append(", CREATED_BY, UPDATED_BY, CREATED_DATE, UPDATED_DATE, APPROVAL, PROJECT_ID, LKUP_ACCOUNT_APPLICATION_STATUS_ID, EXISTING_ACT  ) OUTPUT Inserted.ID VALUES (");
		sb.append(userid).append(",'");
		sb.append(account).append("','");
		sb.append(lsoid).append("','");
		sb.append(association).append("','");
		sb.append(reside).append("','");
		sb.append(space).append("','");
		sb.append(size).append("',");
		sb.append(userid).append(",");
		sb.append(userid).append(",");
		sb.append(" CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Y',");
		sb.append(projectid).append(",");
		sb.append(status).append(",'");
		sb.append(existing).append("')");
		return sb.toString();
	}
	
	public static String updateOnlineUser(int id, int userid, String association, String reside, String space, String size, String status, String existing) {
		StringBuilder sb = new StringBuilder();		
		sb.append(" UPDATE REF_ACCOUNT_APPLICATION set LKUP_USERS_TYPE_ID = '").append(association);
		sb.append("', LKUP_ACCOUNT_APPLICATION_STATUS_ID = '").append(status);
		sb.append("', RESIDE = '").append(reside);
		sb.append("', SPACE_AVAIL = ").append(space);
		sb.append(", NO_CARS = ").append(size);
		sb.append(", UPDATED_BY = ").append(userid);
		sb.append(", UPDATED_DATE = CURRENT_TIMESTAMP, APPROVAL = 'Y' ");
		sb.append(",  EXISTING_ACT = '").append(existing).append("' ");
		sb.append(" WHERE ID = ").append(id);
		return sb.toString();
	}
	
	
	public static String updateOnlineUserAdditionalAddress(int id,  String strno, String strName, String unit, String strmod) {
		StringBuilder sb = new StringBuilder();		
		sb.append(" UPDATE REF_ACCOUNT_APPLICATION set STR_NO = '").append(Operator.sqlEscape(strno));
		sb.append("', STREET_ID = '").append(Operator.sqlEscape(strName));
		sb.append("', UNIT = '").append(Operator.sqlEscape(unit));
		sb.append("', STR_MOD = '").append(Operator.sqlEscape(strmod));
		sb.append("'");
		sb.append(" WHERE ID = ").append(id);
		return sb.toString();
	}
	
	public static String insertAttachment(int id, String file, String type) {
		StringBuilder sb = new StringBuilder();		
		sb.append(" INSERT INTO REF_ACCOUNT_APPLICATION_ATTACHMENTS (");
		sb.append(" REF_ACCOUNT_APPLICATION_ID, ATTACHMENT, LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" ) VALUES (");
		sb.append(id).append(",'").append(file).append("','").append(type).append("')");
		return sb.toString();
	}
	
	public static String getProjectByLSO(int lsoid, String typeid, String statusid, String usertype){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT P.ID from PROJECT P ");
		sb.append(" JOIN REF_LSO_PROJECT RLP on RLP.PROJECT_ID = P.ID ");
		sb.append(" JOIN REF_USERS_TYPE_PROJECT_TYPE RUPT on RUPT.LKUP_PROJECT_TYPE_ID = P.LKUP_PROJECT_TYPE_ID ");
		sb.append(" WHERE lso_id = ").append(lsoid);
		sb.append(" AND P.LKUP_PROJECT_TYPE_ID IN (").append(typeid).append(")");
		sb.append(" AND (LKUP_PROJECT_STATUS_ID IN (").append(statusid).append(")");
		//sb.append(" AND (DateDiff(d, current_timestamp, EXPIRED_DT)) >= 0) ");
		sb.append(" ) ");
		sb.append(" AND RUPT.LKUP_USERS_TYPE_ID IN ( ").append(usertype).append(")");
		sb.append(" order by P.CREATED_DATE desc");
		return sb.toString();
	}
	

	
	public static String deleteAttachment(int typeid){
		StringBuilder sb = new StringBuilder();
		sb.append(" Delete from REF_ACCOUNT_APPLICATION_ATTACHMENTS where id = ").append(typeid);
		return sb.toString();
	}
	
	public static String updateOnlineUser(int id, int userid, String active, String comments, String status) {
		StringBuilder sb = new StringBuilder();		
		sb.append(" UPDATE REF_ACCOUNT_APPLICATION set ");
		if(Operator.equalsIgnoreCase(status, "3")){
			sb.append(" APPROVAL = '").append("N");
		}else{
			sb.append(" APPROVAL = '").append(active);
		}
		sb.append("', LKUP_ACCOUNT_APPLICATION_STATUS_ID = '").append(status);
		sb.append("', COMMENT = '").append(comments);
		sb.append("', UPDATED_BY = ").append(userid);
		sb.append(", UPDATED_DATE = CURRENT_TIMESTAMP ");
		sb.append(" WHERE ID = ").append(id);
		return sb.toString();
	}

	public static String summary(String type, int typeid, int id) {
		if (!type.equalsIgnoreCase("project")) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" P.ID AS ACCOUNT, ");
		sb.append(" P.* ");
		sb.append(" FROM ");
		sb.append(" REF_PROJECT_PARKING AS P ");
		sb.append(" WHERE ");
		sb.append(" P.PROJECT_ID = ").append(typeid);
		sb.append(" AND ");
		sb.append(" P.ACTIVE = 'Y' ");
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" ID = ").append(id);
		}
		return sb.toString();
	}

	public static String details(String type, int typeid, int id) {
		return summary(type, typeid, id);
	}

	public static String update(String type, int typeid, int spaces, int cars, int approved, String secret, int userid, String ip) {
		if (!type.equalsIgnoreCase("project")) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_PROJECT_PARKING SET ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		
		sb.append(",");
		sb.append("UPDATED_DATE = CURRENT_TIMESTAMP ");
		
		if (Operator.hasValue(secret)) {
			sb.append(" , ");
			sb.append(" SECRET = '").append(Operator.sqlEscape(secret)).append("' ");
		}
		if (spaces > -1) {
			sb.append(" ,  ");
			sb.append(" NO_SPACES = ").append(spaces);
		}
		if (cars > -1) {
			sb.append(" ,  ");
			sb.append(" NO_CARS = ").append(cars);
		}
		if (approved > -1) {
			sb.append(" , ");
			sb.append(" APPROVED_SPACE = ").append(approved);
		}
		sb.append(" WHERE ");
		sb.append(" PROJECT_ID = ").append(typeid);
		return sb.toString();
	}

	public static String getParking(String type, int typeid) {
		if (!type.equalsIgnoreCase("project")) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM REF_PROJECT_PARKING WHERE PROJECT_ID = ").append(typeid);
		return sb.toString();
	}

	public static String addHistory(int id, int projectid, int nospaces, int nocars, String active, int createdby, String createddate, int updatedby, String updateddate, String createdip, String updatedip) {
		Timekeeper c = new Timekeeper();
		c.setDate(createddate);
		Timekeeper u = new Timekeeper();
		u.setDate(updateddate);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_PROJECT_PARKING_HISTORY ( ");
		sb.append(" ACCOUNT ");
		sb.append(" , ");
		sb.append(" PROJECT_ID ");
		sb.append(" , ");
		sb.append(" NO_SPACES ");
		sb.append(" , ");
		sb.append(" NO_CARS ");
		sb.append(" , ");
		sb.append(" ACTIVE ");
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
		sb.append(" ) Output Inserted.* VALUES ( ");
		sb.append(id);
		sb.append(" , ");
		sb.append(projectid);
		sb.append(" , ");
		sb.append(nospaces);
		sb.append(" , ");
		sb.append(nocars);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(active)).append("' ");
		sb.append(" , ");
		sb.append(createdby);
		sb.append(" , ");
		sb.append(c.sqlDatetime());
		sb.append(" , ");
		sb.append(updatedby);
		sb.append(" , ");
		sb.append(u.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(createdip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(updatedip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String id(String type, int typeid, int histid) {
		if (histid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(" REF_PROJECT_PARKING_HISTORY ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(histid);
		return sb.toString();
	}

	public static String getRenewalPermitsCount(String id, int renewalyear) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH QC AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		T.ID, ");
		sb.append(" 		T.TYPE, ");
		sb.append(" 		T.RENEWAL_TYPE_ID, ");
		sb.append(" 		RT.TYPE AS RENEWAL_TYPE, ");
		sb.append(" 		T.DURATION_MAX, ");
		sb.append(" 		COUNT(DISTINCT A.ID) AS RENEWED, ");
		sb.append(" 		COUNT(DISTINCT CA.ID) AS CURR ");
		sb.append(" 	FROM ");
		sb.append(" 		LKUP_ACT_TYPE AS T ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			ACTIVITY AS A ");
		sb.append(" 			JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID AND S.LIVE = 'Y' AND YEAR(A.EXP_DATE) = ").append(renewalyear);
		sb.append(" 			JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND P.ID = ").append(id);
		sb.append(" 			JOIN REF_PROJECT_PARKING AS PP ON P.ID = PP.PROJECT_ID ");
		sb.append(" 		) ON T.ID = A.LKUP_ACT_TYPE_ID AND A.ACTIVE = 'Y' ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			ACTIVITY AS CA ");
		sb.append(" 			JOIN LKUP_ACT_STATUS AS CS ON CA.LKUP_ACT_STATUS_ID = CS.ID AND CS.LIVE = 'Y' AND YEAR(CA.EXP_DATE) = ").append(renewalyear - 1);
		sb.append(" 			JOIN PROJECT AS CP ON CA.PROJECT_ID = CP.ID AND CP.ID = ").append(id);
		sb.append(" 			JOIN REF_PROJECT_PARKING AS CPP ON CP.ID = CPP.PROJECT_ID ");
		sb.append(" 		) ON T.ID = CA.LKUP_ACT_TYPE_ID AND CA.ACTIVE = 'Y' ");
		sb.append(" 		LEFT OUTER JOIN LKUP_ACT_TYPE AS RT ON T.RENEWAL_TYPE_ID = RT.ID ");
		sb.append("  ");
		sb.append(" 	WHERE ");
		sb.append(" 		T.ACTIVE = 'Y' ");
		sb.append(" 		AND ");
		sb.append(" 		T.ISDOT = 'Y' ");
		sb.append(" 		AND ");
		sb.append(" 		T.RENEWAL = 'Y' ");
		sb.append("  ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		T.ID, ");
		sb.append(" 		T.TYPE, ");
		sb.append(" 		T.RENEWAL_TYPE_ID, ");
		sb.append(" 		RT.TYPE, ");
		sb.append(" 		T.DURATION_MAX ");
		sb.append(" ) ");
		sb.append(" , QO AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		CASE WHEN RENEWAL_TYPE_ID > 0 AND RENEWAL_TYPE IS NOT NULL THEN RENEWAL_TYPE_ID ELSE ID END AS ID ");
		sb.append(" 		, ");
		sb.append(" 		CASE WHEN RENEWAL_TYPE_ID > 0 AND RENEWAL_TYPE IS NOT NULL THEN RENEWAL_TYPE ELSE TYPE END AS TYPE ");
		sb.append(" 		, ");
		sb.append(" 		CASE WHEN RENEWAL_TYPE_ID > 0 AND RENEWAL_TYPE IS NOT NULL THEN 0 ELSE DURATION_MAX END AS DURATION_MAX ");
		sb.append(" 		, ");
		sb.append(" 		RENEWED ");
		sb.append(" 		, ");
		sb.append(" 		CURR ");
		sb.append(" 	FROM QC ");
		sb.append(" ) ");
		sb.append(" , Q AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		ID ");
		sb.append(" 		, ");
		sb.append(" 		TYPE ");
		sb.append(" 		, ");
		sb.append(" 		SUM(DURATION_MAX) AS DURATION_MAX ");
		sb.append(" 		, ");
		sb.append(" 		SUM(RENEWED) AS RENEWED ");
		sb.append(" 		, ");
		sb.append(" 		SUM(CURR) AS CURR ");
		sb.append(" 	FROM ");
		sb.append(" 		QO ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		ID ");
		sb.append(" 		, ");
		sb.append(" 		TYPE ");
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" 	ID, ");
		sb.append(" 	TYPE, ");
		sb.append(" 	RENEWED, ");
		sb.append(" 	CURR, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN LOWER(TYPE) LIKE LOWER('Overnight Parking%') THEN CURR - RENEWED ");
		sb.append(" 		WHEN LOWER(TYPE) LIKE LOWER('Preferential Parking%') AND CURR < 1 THEN 0 ");
		sb.append(" 		WHEN LOWER(TYPE) LIKE LOWER('Preferential Parking%') THEN DURATION_MAX - RENEWED ");
		sb.append(" 		ELSE 0 ");
		sb.append(" 	END AS RENEWABLE, ");
		sb.append(" 	RENEWED AS COUNT, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN LOWER(TYPE) LIKE LOWER('Overnight Parking%') THEN CURR ");
		sb.append(" 		WHEN LOWER(TYPE) LIKE LOWER('Preferential Parking%') AND CURR > 0 THEN DURATION_MAX ");
		sb.append(" 		ELSE 0 ");
		sb.append(" 	END AS MAX_ALLOWED ");
		sb.append(" FROM ");
		sb.append(" 	Q ");
		return sb.toString();
	}

	public static String getRenewalPermitsCount(String id,String startdate,String enddate,String nextstdate,String nexteddate){
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS( ");
		sb.append(" 	SELECT ");
		sb.append(" 	COUNT(A.ID) AS MAX_ALLOWED, ");
		sb.append(" 	LAT.ID, ");
		sb.append(" 	LAT.DESCRIPTION AS TYPE ");
		sb.append(" FROM ");
		sb.append(" 	ACTIVITY AS A ");
		sb.append(" 	JOIN LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID AND A.ACTIVE = 'Y' ");
		sb.append(" 	JOIN LKUP_ACT_STATUS LAS ON A.LKUP_ACT_STATUS_ID = LAS.ID AND LAS.LIVE = 'Y' ");
		sb.append(" 	JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append(" 	JOIN REF_PROJECT_PARKING RAT ON A.PROJECT_ID = RAT.PROJECT_ID ");
		sb.append(" WHERE ");
//		sb.append(" 	( ");
		sb.append(" 		A.PROJECT_ID IN  (").append(id).append(") ");
//		sb.append(" 		OR ");
//		sb.append(" 		RAT.ID in (").append(id).append(" ) ");
//		sb.append(" 	) ");
		sb.append(" 	AND ");
		sb.append(" 	LAT.ISDOT='Y' ");
		sb.append(" 	AND ");
		sb.append(" 	LAT.RENEWAL ='Y' ");
		sb.append(" 	AND ");
		sb.append(" 	A.START_DATE >= '").append(startdate).append("' ");
		sb.append(" 	AND ");
		sb.append(" 	A.EXP_DATE <= '").append(enddate).append("' ");
		sb.append(" GROUP BY ");
		sb.append(" 	LAT.DESCRIPTION,");
		sb.append(" 	LAT.ID");
		sb.append(" ) ");
		sb.append(" , P AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		COUNT(A.ID) AS COUNT, ");
		sb.append(" 		LAT.ID ");
		sb.append(" 	FROM ");
		sb.append(" 		ACTIVITY AS A ");
		sb.append(" 		JOIN LKUP_ACT_TYPE AS LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID AND A.ACTIVE = 'Y' ");   
		sb.append(" 		JOIN LKUP_ACT_STATUS LAS ON A.LKUP_ACT_STATUS_ID = LAS.ID AND LAS.LIVE = 'Y' ");
		sb.append(" 		JOIN REF_PROJECT_PARKING AS RAT ON A.PROJECT_ID = RAT.PROJECT_ID ");
		sb.append(" 	WHERE ");
//		sb.append(" 		( ");
		sb.append(" 			A.project_id in  (	").append(id).append(" ) ");
//		sb.append(" 			OR ");
//		sb.append(" 			RAT.ID IN (").append(id).append(") ");
//		sb.append(" 		)  ");
		sb.append(" 		AND ");
		sb.append(" 		LAT.ISDOT = 'Y' ");
		sb.append(" 		AND ");
		sb.append(" 		LAT.RENEWAL ='Y' ");
		sb.append(" 		AND ");
		sb.append(" 		A.START_DATE >= '").append(nextstdate).append("' ");
		sb.append(" 		AND ");
		sb.append(" 		A.EXP_DATE <= '").append(nexteddate).append("' ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		LAT.ID");
		sb.append(" )    ");   
		sb.append(" SELECT ");
		sb.append(" 	MAX_ALLOWED, ");
		sb.append(" 	Q.ID, ");
		sb.append(" 	TYPE, ");
		sb.append(" 	CASE WHEN COUNT IS NULL THEN  0 ELSE COUNT END COUNT, ");
		sb.append(" 	P.ID ");
		sb.append(" FROM ");
		sb.append(" 	Q ");
		sb.append(" 	LEFT OUTER JOIN P ON Q.ID = P.ID  ");
		return sb.toString();

	}
	
	public static String deleteAllAttachment(int typeid){
		StringBuilder sb = new StringBuilder();
		sb.append(" Delete from REF_ACCOUNT_APPLICATION_ATTACHMENTS where REF_ACCOUNT_APPLICATION_ID = ").append(typeid);
		return sb.toString();
	}
	
	public static String deleteDetails(int typeid){
		StringBuilder sb = new StringBuilder();
		sb.append(" Delete from REF_ACCOUNT_APPLICATION where id = ").append(typeid);
		return sb.toString();
	}


	public static String getStatus(String status) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ID FROM  LKUP_PROJECT_STATUS ");
		sb.append("WHERE UPPER(STATUS) IN (");
		if(!Operator.hasValue(status))
			sb.append("'APPROVED','PENDING'");
		else
			sb.append("'").append(status.toUpperCase()).append("'");
		
		sb.append(") ");
		return sb.toString();
	}


	public static String updateProject(String prjid, String prjstatus) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE PROJECT SET LKUP_PROJECT_STATUS_ID =");
		sb.append(prjstatus);
		sb.append(",");
		sb.append("UPDATED_DATE = CURRENT_TIMESTAMP ");
		sb.append(" WHERE ID IN (").append(prjid);
		sb.append(")");
		return sb.toString();
	}


	public static String searchLSO(HashMap<String, String> s) {
		
		StringBuilder sb = new StringBuilder();
		boolean q = false;
		
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" FROM ");
		sb.append(" LSO L ");
		sb.append(" WHERE ");
		sb.append(" L.ID > 0 ");
		
		if(Operator.hasValue(s.get("strno"))){
			sb.append (" and STR_NO = ").append(Operator.sqlEscape(s.get("strno")));	
			q= true;
		}
		if(Operator.hasValue(s.get("strname"))){
			sb.append (" and LSO_STREET_ID = ").append(Operator.sqlEscape(s.get("strname")));	
			q= true;
		}
		
		if(Operator.hasValue(s.get("unit"))){
			sb.append (" and UNIT = '").append(Operator.sqlEscape(s.get("unit"))).append("'");	
			q= true;
		}

		if(Operator.hasValue(s.get("fraction"))){
			sb.append (" and STR_MOD = '").append(Operator.sqlEscape(s.get("fraction"))).append("' ");	
			q= true;
		}
		
		if(!q){
			sb.append( " and L.ACTIVE='R' ");
		}
		
		return sb.toString();
	}

	public static String getParkingApprovalCount() {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	RUO.ID, ");
		sb.append(" 	RUO.USERS_ID, ");
		sb.append(" 	U.FIRST_NAME, ");
		sb.append(" 	U.MI, ");
		sb.append(" 	U.LAST_NAME, ");
		sb.append(" 	U.USERNAME, ");
		sb.append(" 	U.EMAIL, ");
		sb.append(" 	RAP.ID ACCOUNT_NO, ");
		sb.append(" 	RUO.ACCOUNT_NO, ");
		sb.append(" 	RUO.APPROVAL, ");
		sb.append(" 	RUO.PROJECT_ID, ");
		sb.append(" 	RUO.LKUP_USERS_TYPE_ID, ");
		sb.append(" 	RUO.RESIDE, ");
		sb.append(" 	RUO.SPACE_AVAIL, ");
		sb.append(" 	RUO.NO_CARS, ");
		sb.append(" 	RUO.COMMENT, ");
		sb.append(" 	PS.EXPIRED, ");
//		sb.append(" 	VA.LSO_STREET_ID, ");
//		sb.append(" 	VA.STR_NO, ");
//		sb.append(" 	VA.PRE_DIR, ");
//		sb.append(" 	VA.STR_NAME, ");
//		sb.append(" 	VA.STR_TYPE, ");
//		sb.append(" 	VA.SUF_DIR, ");
//		sb.append(" 	VA.UNIT, ");
//		sb.append(" 	VA.LSO_ID, ");
//		sb.append(" 	VA.LSO_STREET_ID, ");
		sb.append(" 	LUT.TYPE, ");
		sb.append(" 	RUO.LKUP_ACCOUNT_APPLICATION_STATUS_ID, ");
		sb.append(" 	LAA.STATUS ");
		sb.append(" FROM ");
		sb.append(" 	REF_ACCOUNT_APPLICATION RUO ");
		sb.append(" 	JOIN USERS U ON RUO.USERS_ID = U.ID ");
//		sb.append(" 	JOIN V_CENTRAL_ADDRESS VA ON RUO.LSO_ID = VA.LSO_ID ");
		sb.append(" 	JOIN LKUP_USERS_TYPE LUT ON RUO.LKUP_USERS_TYPE_ID = LUT.ID ");
		sb.append(" 	LEFT OUTER JOIN REF_PROJECT_PARKING RAP ON RUO.PROJECT_ID = RAP.PROJECT_ID ");
		sb.append(" 	LEFT OUTER JOIN PROJECT P ON RAP.PROJECT_ID = P.ID ");
		sb.append(" 	LEFT OUTER JOIN LKUP_PROJECT_STATUS PS ON P.LKUP_PROJECT_STATUS_ID = PS.ID ");
		sb.append(" 	JOIN LKUP_ACCOUNT_APPLICATION_STATUS LAA ON RUO.LKUP_ACCOUNT_APPLICATION_STATUS_ID = LAA.ID ");
		sb.append(" WHERE ");
		sb.append(" 	LAA.DEFLT = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	APPROVAL = 'Y' ");
		return sb.toString();
	}

	public static String getLSO(int strno, int strid, String fraction, String unit, String level) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	LSO_ID ");
		sb.append(" FROM ");
		sb.append(" 	V_CENTRAL_ADDRESS ");
		sb.append(" WHERE ");
		sb.append(" 	LSO_STREET_ID = ").append(strid);
		sb.append(" 	AND ");
		sb.append(" 	STR_NO = ").append(strno);
		if (Operator.hasValue(fraction)) {
			sb.append(" AND ");
			sb.append(" LOWER(STR_MOD) = LOWER('").append(Operator.sqlEscape(fraction)).append("') ");
		}
		else {
			sb.append(" AND ");
			sb.append(" (STR_MOD IS NULL OR STR_MOD = '') ");
		}
		if (Operator.hasValue(unit)) {
			if (!Operator.equalsIgnoreCase(level, "L")) {
				sb.append(" AND ");
				sb.append(" LOWER(UNIT) = '").append(Operator.sqlEscape(unit.toLowerCase())).append("' ");
			}
		}
		if (Operator.equalsIgnoreCase(level, "L")) {
			sb.append(" AND ");
			sb.append(" LSO_TYPE = 'L' ");
		}
		else if (Operator.equalsIgnoreCase(level, "S")) {
			sb.append(" AND ");
			sb.append(" LSO_TYPE = 'S' ");
		}
		else {
			sb.append(" AND ");
			sb.append(" LSO_TYPE = 'O' ");
		}
		return sb.toString();
	}

	public static String getOnlinePermits(int projectid, String divisionids, boolean isexemption, boolean istemp) {
		String temp = "N";
		String exemption = "N";
		if (istemp) { temp = "Y"; } else { temp = "N"; }
		if (isexemption) { exemption = "Y"; } else { exemption = "N"; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	LAT.ID, ");
		sb.append(" 	LAT.ID VALUE, ");
		sb.append(" 	LAT.DESCRIPTION AS TEXT ");
		sb.append(" FROM ");
		sb.append(" 	PROJECT P ");
		sb.append(" 	JOIN REF_PROJECT_ACT_TYPE AS PAT ON P.LKUP_PROJECT_TYPE_ID = PAT.LKUP_PROJECT_TYPE_ID ");
		sb.append(" 	JOIN LKUP_ACT_TYPE AS LAT ON PAT.LKUP_ACT_TYPE_ID = LAT.ID ");
		sb.append(" 	JOIN REF_ACT_DIVISIONS AS RAD ON LAT.ID = RAD.LKUP_ACT_TYPE_ID ");
		sb.append(" 	AND RAD.ACTIVE = 'Y' ");
		sb.append(" 	AND RAD.LKUP_DIVISIONS_ID IN (").append(divisionids).append(") ");
		sb.append(" WHERE ");
		sb.append(" 	ISDOT = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	ONLINE = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	ISTEMP = '").append(temp).append("' ");
		sb.append(" 	AND ");
		sb.append(" 	DOT_EXEMPTION = '").append(exemption).append("' ");
		sb.append(" 	AND ");
		sb.append(" 	P.ID = ").append(projectid);
		return sb.toString();
	}

	public static String myActive(String username, String acttype) {
		StringBuilder sb = new StringBuilder();
		if (Operator.hasValue(username)) {
			sb.append(" SELECT ");
			sb.append(" A.ACT_NBR ");
			sb.append(" , ");
			sb.append(" CAST(L.STR_NO AS VARCHAR(10)) + ' ' + COALESCE(ST.PRE_DIR, '') + ' ' + ST.STR_NAME + ' ' + ST.STR_TYPE + ' ' + COALESCE(L.UNIT, '') AS ADDRESS ");
			sb.append(" , ");
			sb.append(" CAST(A.START_DATE AS DATE) AS START_DATE ");
			sb.append(" , ");
			sb.append(" A.EXP_DATE ");
			sb.append(" FROM ");
			sb.append(" ACTIVITY AS A ");
			sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
			sb.append(" AND ");
			if (Operator.hasValue(acttype)) {
				String types[] = Operator.split(acttype, "|");
				String ft = types[0];
				if (Operator.hasValue(ft)) {
					boolean num = Operator.isNumber(ft);
					if (num) {
						sb.append(" T.ID IN ( ");
					}
					else {
						sb.append(" LOWER(T.TYPE) IN ( ");
					}
					boolean empty = true;
					for (int i=0; i<types.length; i++) {
						String t = types[i];
						if (!empty) {
							sb.append(" , ");
						}
						if (num) {
							sb.append(Operator.toInt(t));
						}
						else {
							sb.append(" '").append(Operator.sqlEscape(t.toLowerCase())).append("' ");
							
						}
						empty = false;
					}
					sb.append(" ) ");
				}
			}
			else {
				sb.append(" T.ISDOT = 'Y' AND T.DOT_EXEMPTION = 'N' ");
			}
			sb.append(" JOIN LKUP_ACT_STATUS AS S ON A.LKUP_ACT_STATUS_ID = S.ID AND S.ACTIVE = 'Y' AND S.LIVE = 'Y' AND S.EXPIRED = 'N' AND COALESCE(A.EXP_DATE, getDate() + 1) > getDate() ");
			sb.append(" JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND A.ACTIVE = 'Y' AND P.ACTIVE = 'Y' ");
			sb.append(" JOIN REF_LSO_PROJECT AS RLP ON P.ID = RLP.PROJECT_ID AND RLP.ACTIVE = 'Y' ");
			sb.append(" JOIN LSO AS L ON RLP.LSO_ID = L.ID ");
			sb.append(" JOIN LSO_STREET AS ST ON L.LSO_STREET_ID = ST.ID ");
			sb.append(" JOIN REF_PROJECT_PARKING AS PP ON P.ID = PP.PROJECT_ID AND PP.ACTIVE = 'Y' ");
			sb.append(" JOIN REF_PROJECT_USERS AS PU ON P.ID = PU.PROJECT_ID AND PU.ACTIVE = 'Y' ");
			sb.append(" JOIN REF_USERS AS RU ON PU.REF_USERS_ID = RU.ID AND RU.ACTIVE = 'Y' ");
			sb.append(" JOIN USERS AS U ON RU.USERS_ID = U.ID AND U.ACTIVE = 'Y' AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
			sb.append(" ORDER BY A.EXP_DATE DESC ");
		}
		return sb.toString();
	}


}


















