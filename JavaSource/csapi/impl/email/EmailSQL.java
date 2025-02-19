package csapi.impl.email;

import csapi.utils.CsReflect;

public class EmailSQL {


	

	public static String getPeople(String type, int typeid) {
		return getPeople(type, typeid, -1);
	}

	public static String getPeople(String type, int typeid, int refusersid) {
		String maintableref = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" REF.PRIMARY_CONTACT ");
		sb.append(" , ");
		sb.append(" U.EMAIL ");
		sb.append(" , ");

		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("         ELSE '' ");
		sb.append("       END + ");
		sb.append("       CASE  ");
		sb.append("         WHEN T.TYPE IS NOT NULL AND T.TYPE <> '' THEN ' (' + T.TYPE + ') ' ");
		sb.append("         ELSE '' ");
		sb.append("       END ");
		sb.append("     )) AS TEXT ");

		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_USERS AS REF ");
		sb.append(" JOIN REF_USERS AS R ON REF.REF_USERS_ID = R.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_USERS_TYPE AS T ON R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0");
		sb.append(" LEFT OUTER JOIN USERS_GROUP AS G ON R.USERS_GROUP_ID = G.ID ");
		sb.append(" WHERE ");
		sb.append(" REF.").append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(" REF.ACTIVE = 'Y' ");
		
		if(type.equalsIgnoreCase("activity")){
		sb.append(" UNION ");
		
		sb.append(" SELECT ");
		sb.append(" R.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" REF.PRIMARY_CONTACT ");
		sb.append(" , ");
		sb.append(" U.EMAIL ");
		sb.append(" , ");

		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("         ELSE '' ");
		sb.append("       END + ");
		sb.append("       CASE  ");
		sb.append("         WHEN T.TYPE IS NOT NULL AND T.TYPE <> '' THEN ' (' + T.TYPE + ') ' ");
		sb.append("         ELSE '' ");
		sb.append("       END ");
		sb.append("     )) AS TEXT ");

		sb.append(" FROM ACTIVITY A ");
		sb.append(" JOIN REF_PROJECT_USERS AS REF on A.PROJECT_ID=REF.PROJECT_ID ");
		sb.append(" JOIN REF_USERS AS R ON REF.REF_USERS_ID = R.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_USERS_TYPE AS T ON R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0");
		sb.append(" LEFT OUTER JOIN USERS_GROUP AS G ON R.USERS_GROUP_ID = G.ID ");
		sb.append(" WHERE ");
		sb.append(" A.ID = ").append(typeid);
		sb.append(" AND ");
		sb.append(" REF.ACTIVE = 'Y' ");
		}
		/*if (refusersid > 0) {
			sb.append(" AND ");
			sb.append(" REF.REF_USERS_ID = ").append(refusersid);
		}*/
		/*sb.append(" UNION ");
		sb.append(" SELECT ");
		sb.append(" R.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" 'N' as PRIMARY_CONTACT ");
		sb.append(" , ");
		sb.append(" U.EMAIL ");
		sb.append(" , ");

		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("         ELSE '' ");
		sb.append("       END + ");
		sb.append("       CASE  ");
		sb.append("         WHEN T.TYPE IS NOT NULL AND T.TYPE <> '' THEN ' (' + T.TYPE + ') ' ");
		sb.append("         ELSE '' ");
		sb.append("       END ");
		sb.append("     )) AS TEXT ");

		sb.append(" FROM ");
		sb.append(" ").append(maintableref).append("  AS A ");
		sb.append("  left outer join REF_ACT_TEAM RAU on A.ID=RAU.ACTIVITY_ID  AND RAU.ACTIVE='Y'  ");
		sb.append(" JOIN REF_TEAM AS R ON REF.REF_TEAM_ID = R.ID AND REF.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_USERS_TYPE AS T ON R.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON R.USERS_ID = U.ID AND R.USERS_ID > 0");
		sb.append(" LEFT OUTER JOIN USERS_GROUP AS G ON R.USERS_GROUP_ID = G.ID ");
		sb.append(" WHERE ");
		sb.append(" REF.").append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(" REF.ACTIVE = 'Y' ");*/
		
		return sb.toString();
	}

	
	public static String getPayee(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" select U.EMAIL ");
		sb.append(" , ");

		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("         ELSE '' ");
		sb.append("       END  ");
		
		sb.append("     )) AS TEXT ");
		
		sb.append(" from PAYMENT P JOIN USERS U on P.PAYEE_ID = U.ID where P.ID= ").append(id);
		return sb.toString();
	}

	
	
	
	
}















