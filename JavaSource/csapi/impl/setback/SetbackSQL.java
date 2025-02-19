package csapi.impl.setback;



public class SetbackSQL {

	public static String summary(String type, int typeid, int id) {
		return info(type, typeid, id);
	}

	public static String info(String type, int typeid, int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" 	S.ID, ");
		sb.append(" 	T.ID AS TYPE_ID, ");
		sb.append(" 	T.TYPE, ");
		sb.append(" 	S.FT, ");
		sb.append(" 	S.INCHES, ");
		sb.append(" 	S.COMMENTS, ");
		sb.append(" 	S.REQ_FT, ");
		sb.append(" 	S.REQ_INCHES, ");
		sb.append(" 	S.REQ_COMMENT, ");
		sb.append(" 	S.START_DATE, ");
		sb.append(" 	T.ORDR ");
		sb.append(" FROM ");
		sb.append(" 	LKUP_LSO_SETBACK_TYPE AS T ");
		sb.append(" 	LEFT OUTER JOIN ( ");
		sb.append(" 		LSO_SITEDATA AS D ");
		sb.append(" 		JOIN LSO_SETBACK AS S ON D.ID = S.LSO_SITEDATA_ID AND S.ACTIVE = 'Y' AND D.ID = ").append(id);
		sb.append(" 	) ON T.ID = S.LKUP_LSO_SETBACK_TYPE_ID ");
		sb.append(" WHERE ");
		sb.append(" 	T.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String details(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" 	S.ID, ");
		sb.append(" 	T.ID AS TYPE_ID, ");
		sb.append(" 	T.TYPE, ");
		sb.append(" 	S.FT, ");
		sb.append(" 	S.INCHES, ");
		sb.append(" 	S.COMMENTS, ");
		sb.append(" 	S.REQ_FT, ");
		sb.append(" 	S.REQ_INCHES, ");
		sb.append(" 	S.REQ_COMMENT, ");
		sb.append(" 	S.START_DATE, ");
		sb.append(" 	T.ORDR ");
		sb.append(" FROM ");
		sb.append(" 	LKUP_LSO_SETBACK_TYPE AS T ");
		sb.append(" 	LEFT OUTER JOIN ( ");
		sb.append(" 		LSO_SITEDATA AS D ");
		sb.append(" 		JOIN LSO_SETBACK AS S ON D.ID = S.LSO_SITEDATA_ID AND S.ACTIVE = 'Y' AND D.ID = ").append(id);
		sb.append(" 	) ON T.ID = S.LKUP_LSO_SETBACK_TYPE_ID ");
		sb.append(" WHERE ");
		sb.append(" 	T.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String details1(String type, int typeid, int id) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(" WITH Q AS ( ");
		sb.append("   SELECT TOP 1 ");
		sb.append("     SB.START_DATE ");
		sb.append("   FROM  ");
		sb.append("     LSO ");
		sb.append("     JOIN LSO_SITEDATA AS SD ON SD.LSO_ID = LSO.ID ");
		sb.append("     JOIN LSO_SETBACK SB ON SD.ID = SB.LSO_SITEDATA_ID  ");
		sb.append("   WHERE ");
		sb.append("     LSO.ID = ").append(typeid).append(" ");
		sb.append("     AND ");
		sb.append("     SD.ACTIVE = 'Y' ");
		sb.append("     AND ");
		sb.append("     SB.ACTIVE = 'Y' ");
		sb.append("   GROUP BY ");
		sb.append("     SB.START_DATE ");
		sb.append("   ORDER BY ");
		sb.append("     SB.START_DATE DESC ");
		sb.append(" ) ");
		sb.append(" SELECT  ");
		sb.append("   SB.ID, ");
		sb.append("   T.ID AS TYPE_ID, ");
		sb.append("   T.TYPE, ");
		sb.append("   SB.FT, ");
		sb.append("   SB.INCHES, ");
		sb.append("   SB.COMMENTS, ");
		sb.append("   SB.REQ_FT, ");
		sb.append("   SB.REQ_INCHES, ");
		sb.append("   SB.REQ_COMMENT, ");
		sb.append("   SB.START_DATE, ");
		sb.append("   T.ORDR ");
		sb.append(" FROM  ");
		sb.append("   LKUP_LSO_SETBACK_TYPE AS T ");
		sb.append("   LEFT OUTER JOIN ( ");
		sb.append("     LSO ");
		sb.append("     JOIN LSO_SITEDATA AS SD ON SD.LSO_ID = LSO.ID ");
		sb.append("     JOIN LSO_SETBACK SB ON SD.ID = SB.LSO_SITEDATA_ID  ");
		sb.append("    LEFT OUTER JOIN Q ON SB.START_DATE = Q.START_DATE ");
		sb.append("   ) ON SB.LKUP_LSO_SETBACK_TYPE_ID = T.ID AND SD.ACTIVE = 'Y' AND SB.ACTIVE = 'Y' AND LSO.ID = ").append(typeid);
		sb.append(" WHERE ");
		sb.append("   T.ACTIVE = 'Y' ");
		if(id>0){
			sb.append(" AND SB.LSO_SITEDATA_ID=").append(id);
		}
		sb.append(" ORDER BY ");
		sb.append("  T.ORDR ,   ");
		sb.append("   SB.START_DATE DESC ");

		return sb.toString();

	}


}















