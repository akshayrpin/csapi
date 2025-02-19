package csapi.impl.availability;

import csapi.utils.CsReflect;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;


public class AvailabilitySQL {

	public static String getDefault(int avid) {
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT ");
		sb.append(" A.TITLE, ");
		sb.append(" A.ID AS AVAILABILITY_ID, ");
		sb.append(" D.ID AS AVAILABILITY_DEFAULT_ID, ");
		sb.append(" D.DAY_OF_WEEK, ");
		sb.append(" D.TIME_START, ");
		sb.append(" D.TIME_END, ");
		sb.append(" D.SEATS, ");
		sb.append(" D.BUFFER_SEATS, ");
		sb.append(" D.BUFFER_HOURS, ");
		sb.append(" D.TIME_BEGIN, ");
		sb.append(" D.TIME_STOP ");

		sb.append(" FROM ");
		sb.append(" AVAILABILITY AS A ");
		sb.append(" JOIN AVAILABILITY_DEFAULT AS D ON A.ID = D.AVAILABILITY_ID AND D.ACTIVE = 'Y' AND A.ACTIVE = 'Y' ");

		sb.append(" WHERE ");
		sb.append(" A.ID = ").append(avid);

		sb.append(" ORDER BY ");
		sb.append(" D.DAY_OF_WEEK ");
		sb.append(" , ");
		sb.append(" D.TIME_START ");
		sb.append(" , ");
		sb.append(" D.TIME_END ");

		return sb.toString();
	}

	public static String getCustom(Timekeeper start, Timekeeper end, int avid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.TITLE, ");
		sb.append(" A.ID AS AVAILABILITY_ID, ");
		sb.append(" C.ID AS AVAILABILITY_CUSTOM_ID, ");
		sb.append(" C.CUSTOM_DATE, ");
		sb.append(" C.TIME_START, ");
		sb.append(" C.TIME_END, ");
		sb.append(" C.SEATS, ");
		sb.append(" C.BUFFER_SEATS, ");
		sb.append(" C.BUFFER_HOURS, ");
		sb.append(" C.TIME_BEGIN, ");
		sb.append(" C.TIME_STOP ");
		sb.append(" FROM ");
		sb.append(" AVAILABILITY_CUSTOM AS C ");
		sb.append(" JOIN AVAILABILITY AS A ON C.AVAILABILITY_ID = A.ID AND A.ACTIVE = 'Y' AND C.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" A.ID = ").append(avid);
		sb.append(" AND ");
		sb.append(" C.CUSTOM_DATE >= ").append(start.sqlServer());
		sb.append(" AND ");
		sb.append(" C.CUSTOM_DATE <= ").append(end.sqlServer());
		sb.append(" ORDER BY ");
		sb.append(" C.CUSTOM_DATE ");
		sb.append(" , ");
		sb.append(" C.TIME_START ");
		sb.append(" , ");
		sb.append(" C.TIME_END ");
		return sb.toString();
	}


	public static String getTaken(Timekeeper start, Timekeeper end, int avid) {
		StringBuilder sb = new StringBuilder();
	
			sb.append(" WITH AU AS (  ");
			sb.append("   ");
			sb.append("                 SELECT  ");
			sb.append("                                 A.ID AS APPOINTMENT_ID  ");
			sb.append("                 FROM  ");
			sb.append("                                 REVIEW AS R  ");
			sb.append("                                 JOIN REF_COMBOREVIEW_REVIEW AS RCR ON R.ID = RCR.REVIEW_ID AND RCR.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND R.AVAILABILITY_ID =  ").append(avid).append("   ");
			sb.append("                                 JOIN APPOINTMENT AS A ON A.REF_COMBOREVIEW_REVIEW_ID > 0 AND RCR.ID = A.REF_COMBOREVIEW_REVIEW_ID AND A.ACTIVE = 'Y'  ");
			sb.append("   ");
			sb.append("                 UNION  ");
			sb.append("   ");
			sb.append("         SELECT  ");
			sb.append("                         A.ID AS APPOINTMENT_ID  ");
			sb.append("         FROM  ");
			sb.append("                                 REF_PROJECT_APPOINTMENT AS RPA  ");
			sb.append("                                 JOIN PROJECT AS P ON RPA.PROJECT_ID = P.ID AND RPA.ACTIVE = 'Y' AND P.ACTIVE = 'Y'  ");
			sb.append("                                 JOIN LKUP_PROJECT_TYPE AS T ON P.LKUP_PROJECT_TYPE_ID = T.ID AND T.AVAILABILITY_ID =  ").append(avid).append("   ");
			sb.append("                                 JOIN APPOINTMENT AS A ON RPA.APPOINTMENT_ID = P.ID AND P.ACTIVE = 'Y'  ");
			sb.append("   ");
			sb.append("         UNION  ");
			sb.append("   ");
			sb.append("         SELECT  ");
			sb.append("                         A.ID AS APPOINTMENT_ID  ");
			sb.append("         FROM  ");
			sb.append("                                 REF_ACT_APPOINTMENT AS RAA  ");
			sb.append("                                 JOIN ACTIVITY AS ACT ON RAA.ACTIVITY_ID = ACT.ID AND RAA.ACTIVE = 'Y' AND ACT.ACTIVE = 'Y'  ");
			sb.append("                                 JOIN LKUP_ACT_TYPE AS AT ON ACT.LKUP_ACT_TYPE_ID = AT.ID AND AT.AVAILABILITY_ID =  ").append(avid).append("   ");
			sb.append("                                 JOIN APPOINTMENT AS A ON RAA.APPOINTMENT_ID = A.ID AND A.ACTIVE = 'Y' ");
			sb.append("   ");
			sb.append(" ),SS AS (  ");
			sb.append("         SELECT DISTINCT  ");
			sb.append("                                 S.ID AS SCHEDULE_ID,  ");
			sb.append("                                  ").append(avid).append("  AS AVAILABILITY_ID,  ");
			sb.append("                                 CONVERT(date, S.START_DATE) AS DATE_START,  ");
			sb.append("                                 CAST(CONVERT(VARCHAR(5), S.START_DATE,108) AS TIME) AS TIME_START, ");
			sb.append("                                 CONVERT(date, S.END_DATE) AS DATE_END,  ");
			sb.append("                                 CAST(CONVERT(VARCHAR(5), S.END_DATE,108) AS TIME) AS TIME_END, ");
			sb.append("                                 DATEDIFF(day,S.START_DATE,S.END_DATE) AS DIFF,  ");
			sb.append("                                 DATEPART ( weekday , S.START_DATE ) AS START_DAY,  ");
			sb.append("                                 DATEPART ( weekday , S.END_DATE ) AS END_DAY, ");
			sb.append("                                 CASE ");
			sb.append("                                                 WHEN NACS.SCHEDULE_INSPECTION_CANCEL = 'Y' THEN 'Y' ");
			sb.append("                                                 WHEN NACS.SCHEDULE_CANCEL = 'Y' THEN 'Y' ");
			sb.append("                                                 ELSE 'N' ");
			sb.append("                                 END AS CANCELLED ");
			sb.append("         FROM  ");
			sb.append("                                 APPOINTMENT AS A  ");
			sb.append("                                 JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' AND A.ACTIVE = 'Y'  ");
			sb.append("                                 JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULED = 'Y'  ");
			sb.append("                                 JOIN AU ON A.ID = AU.APPOINTMENT_ID  ");
			sb.append("                                 LEFT OUTER JOIN ( ");
			sb.append("                                                 REF_COMBOREVIEW_ACTION AS AC ");
			sb.append("                                                 JOIN REF_COMBOREVIEW_ACTION AS NAC ON NAC.PREVIOUS_ID = AC.ID ");
			sb.append("                                                 JOIN LKUP_REVIEW_STATUS AS NACS ON NAC.LKUP_REVIEW_STATUS_ID = NACS.ID ");
			sb.append("                                 ) ON AC.ID = S.REF_COMBOREVIEW_ACTION_ID ");
			sb.append("                 WHERE ");
			
			sb.append("			S.START_DATE >= ").append(start.sqlDatetime());
			sb.append("			AND ");
			sb.append("			S.START_DATE <= ").append(end.sqlDatetime());
			
			sb.append(" ), S AS (  ");
			sb.append("                 SELECT * FROM SS WHERE CANCELLED = 'N' ");
			sb.append(" ), C AS (  ");
			sb.append("         SELECT DISTINCT  ");
			sb.append("                                 S.SCHEDULE_ID,  ");
			sb.append("                                 S.AVAILABILITY_ID,  ");
			sb.append("                                 S.DATE_START,  ");
			sb.append("                                 S.TIME_START,  ");
			sb.append("                                 S.TIME_END,  ");
			sb.append("                                 S.START_DAY,  ");
			sb.append("                                 S.END_DAY,  ");
			sb.append("                                 C.CUSTOM_DATE,  ");
			sb.append("                                 C.ID AS CUSTOM_ID,  ");
			sb.append("                                 C.TIME_START AS CUSTOM_START,  ");
			sb.append("                                 C.TIME_END AS CUSTOM_END  ");
			sb.append("         FROM  ");
			sb.append("                         S  ");
			sb.append("                                 LEFT OUTER JOIN AVAILABILITY_CUSTOM AS C ON  ");
			sb.append("                                 S.AVAILABILITY_ID = C.AVAILABILITY_ID  ");
			sb.append("                                 AND C.ACTIVE = 'Y'  ");
			sb.append("                                 AND C.CUSTOM_DATE = S.DATE_START  ");
			sb.append("                                 AND S.TIME_END > C.TIME_START  ");
			sb.append("                                 AND S.TIME_START < C.TIME_END  ");
			sb.append("        ) ");
			sb.append(" , NC AS (  ");
			sb.append("                 SELECT  ");
			sb.append("                                 *  ");
			sb.append("                 FROM  ");
			sb.append("                                 C  ");
			sb.append("                 WHERE  ");
			sb.append("                                 CUSTOM_ID IS NULL ");
			sb.append(" ) ");
			sb.append(" , D AS (  ");
			sb.append("         SELECT DISTINCT  ");
			sb.append("                                 NC.SCHEDULE_ID,  ");
			sb.append("                                 NC.AVAILABILITY_ID,  ");
			sb.append("                                 NC.DATE_START,  ");
			sb.append("                                 NC.TIME_START,  ");
			sb.append("                                 NC.TIME_END,  ");
			sb.append("                                 NC.START_DAY,  ");
			sb.append("                                 NC.END_DAY,  ");
			sb.append("                                 D.DAY_OF_WEEK,  ");
			sb.append("                                 D.ID AS DEFAULT_ID,  ");
			sb.append("                                 D.TIME_START AS DEFAULT_START,  ");
			sb.append("                                 D.TIME_END AS DEFAULT_END  ");
			sb.append("         FROM  ");
			sb.append("                                 NC  ");
			sb.append("                                 JOIN AVAILABILITY_DEFAULT AS D ON NC.AVAILABILITY_ID = D.AVAILABILITY_ID ");
			sb.append("                                 AND D.ACTIVE = 'Y' AND D.DAY_OF_WEEK = NC.START_DAY AND NC.TIME_END > D.TIME_START AND NC.TIME_START < D.TIME_END ");
			sb.append("  ) ");
			sb.append(" , U AS (  ");
			sb.append("         SELECT  ");
			sb.append("                                 SCHEDULE_ID,  ");
			sb.append("                                 AVAILABILITY_ID,  ");
			sb.append("                                 DATE_START,  ");
			sb.append("                                 TIME_START,  ");
			sb.append("                                 TIME_END,  ");
			sb.append("                                 START_DAY,  ");
			sb.append("                                 END_DAY,  ");
			sb.append("                                 null AS DAY_OF_WEEK,  ");
			sb.append("                                 null AS DEFAULT_ID,  ");
			sb.append("                                 CUSTOM_DATE,  ");
			sb.append("                                 CUSTOM_ID,  ");
			sb.append("                                 CUSTOM_START AS AVAILABILITY_START,  ");
			sb.append("                                 CUSTOM_END AS AVAILABILITY_END,  ");
			sb.append("                                 'custom' AS TYPE   ");
			sb.append("         FROM  ");
			sb.append("                         C  ");
			sb.append("         WHERE  ");
			sb.append("                         C.CUSTOM_ID IS NOT NULL  ");
			sb.append("         UNION  ");
			sb.append("         SELECT  ");
			sb.append("                         SCHEDULE_ID,  ");
			sb.append("                         AVAILABILITY_ID,  ");
			sb.append("                         DATE_START,  ");
			sb.append("                         TIME_START,  ");
			sb.append("                         TIME_END,  ");
			sb.append("                         START_DAY,  ");
			sb.append("                         END_DAY,  ");
			sb.append("                         DAY_OF_WEEK,  ");
			sb.append("                         DEFAULT_ID,  ");
			sb.append("                         null AS CUSTOM_DATE,  ");
			sb.append("                         null AS CUSTOM_ID,  ");
			sb.append("                         DEFAULT_START AS AVAILABILITY_START,  ");
			sb.append("                         DEFAULT_END AS AVAILABILITY_END,  ");
			sb.append("                         'default' AS TYPE  ");
			sb.append("         FROM  ");
			sb.append("                         D ");
			sb.append(" ) ");
			sb.append("   ");
			sb.append("   ");
			sb.append(" SELECT  ");
			sb.append("                 DATE_START,  ");
			sb.append("                 AVAILABILITY_START AS TIME_START,  ");
			sb.append("                 AVAILABILITY_END AS TIME_END,  ");
			sb.append("                 COUNT(DISTINCT SCHEDULE_ID) AS TAKEN ");
			sb.append(" FROM  ");
			sb.append("                 U ");
			sb.append(" GROUP BY ");
			sb.append("                 DATE_START,AVAILABILITY_START,AVAILABILITY_END ");
			
			return sb.toString();
	}
	/*public static String getTaken2(Timekeeper start, Timekeeper end, int avid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH AU AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		A.ID AS APPOINTMENT_ID ");
		sb.append(" 	FROM ");
		sb.append(" 		REVIEW AS R ");
		sb.append(" 		JOIN REF_COMBOREVIEW_REVIEW AS RCR ON R.ID = RCR.REVIEW_ID AND RCR.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND R.AVAILABILITY_ID = ").append(avid).append(" ");
		sb.append(" 		JOIN APPOINTMENT AS A ON A.REF_COMBOREVIEW_REVIEW_ID > 0 AND RCR.ID = A.REF_COMBOREVIEW_REVIEW_ID AND A.ACTIVE = 'Y' ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		A.ID AS APPOINTMENT_ID ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_PROJECT_APPOINTMENT AS RPA ");
		sb.append(" 		JOIN PROJECT AS P ON RPA.PROJECT_ID = P.ID AND RPA.ACTIVE = 'Y' AND P.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_PROJECT_TYPE AS T ON P.LKUP_PROJECT_TYPE_ID = T.ID AND T.AVAILABILITY_ID = ").append(avid).append(" ");
		sb.append(" 		JOIN APPOINTMENT AS A ON RPA.APPOINTMENT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		A.ID AS APPOINTMENT_ID ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_ACT_APPOINTMENT AS RAA ");
		sb.append(" 		JOIN ACTIVITY AS ACT ON RAA.ACTIVITY_ID = ACT.ID AND RAA.ACTIVE = 'Y' AND ACT.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_ACT_TYPE AS AT ON ACT.LKUP_ACT_TYPE_ID = AT.ID AND AT.AVAILABILITY_ID = ").append(avid).append(" ");
		sb.append(" 		JOIN APPOINTMENT AS A ON RAA.APPOINTMENT_ID = A.ID AND A.ACTIVE = 'Y' ");
		sb.append(" ),S AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		S.ID AS SCHEDULE_ID, ");
		sb.append(" 		").append(avid).append(" AS AVAILABILITY_ID, ");
		sb.append(" 		CONVERT(date, S.START_DATE) AS DATE_START, ");
		
		//sb.append(" 		CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) AS TIME_START, ");
		sb.append(" 		 CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) AS TIME_START,   ");
		sb.append(" 		CONVERT(date, S.END_DATE) AS DATE_END, ");
		//sb.append(" 		 CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) AS TIME_END,   ");
		sb.append(" 		CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) AS TIME_END,   ");
		sb.append(" 		DATEDIFF(day,S.START_DATE,S.END_DATE) AS DIFF, ");
		sb.append(" 		DATEPART ( weekday , S.START_DATE ) AS START_DAY, ");
		sb.append(" 		DATEPART ( weekday , S.END_DATE ) AS END_DAY ");
		sb.append(" 	FROM ");
		sb.append(" 		APPOINTMENT AS A ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' AND A.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULED = 'Y' ");
		sb.append(" 		JOIN AU ON A.ID = AU.APPOINTMENT_ID ");
		sb.append(" 	WHERE  ");
		sb.append("			S.START_DATE >= ").append(start.sqlDatetime());
		sb.append("			AND ");
		sb.append("			S.START_DATE <= ").append(end.sqlDatetime());
		sb.append(" ), C AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		S.SCHEDULE_ID, ");
		sb.append(" 		S.AVAILABILITY_ID, ");
		sb.append(" 		S.DATE_START, ");
		sb.append(" 		S.TIME_START, ");
		sb.append(" 		S.TIME_END, ");
		sb.append(" 		S.START_DAY, ");
		sb.append(" 		S.END_DAY, ");
		sb.append(" 		C.CUSTOM_DATE, ");
		sb.append(" 		C.ID AS CUSTOM_ID, ");
		sb.append(" 		C.TIME_START AS CUSTOM_START, ");
		sb.append(" 		C.TIME_END AS CUSTOM_END ");
		sb.append(" 	FROM ");
		sb.append(" 		S ");
		sb.append(" 		LEFT OUTER JOIN AVAILABILITY_CUSTOM AS C ON ");
		sb.append(" 		S.AVAILABILITY_ID = C.AVAILABILITY_ID ");
		sb.append(" 		AND C.ACTIVE = 'Y' ");
		sb.append(" 		AND C.CUSTOM_DATE = S.DATE_START ");
		sb.append(" 		AND S.TIME_END > C.TIME_START ");
		sb.append(" 		AND S.TIME_START < C.TIME_END ");
		sb.append(" ), NC AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		* ");
		sb.append(" 	FROM ");
		sb.append(" 		C ");
		sb.append(" 	WHERE ");
		sb.append(" 		CUSTOM_ID IS NULL ");
		sb.append(" ), D AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		NC.SCHEDULE_ID, ");
		sb.append(" 		NC.AVAILABILITY_ID, ");
		sb.append(" 		NC.DATE_START, ");
		sb.append(" 		NC.TIME_START, ");
		sb.append(" 		NC.TIME_END, ");
		sb.append(" 		NC.START_DAY, ");
		sb.append(" 		NC.END_DAY, ");
		sb.append(" 		D.DAY_OF_WEEK, ");
		sb.append(" 		D.ID AS DEFAULT_ID, ");
		sb.append(" 		D.TIME_START AS DEFAULT_START, ");
		sb.append(" 		D.TIME_END AS DEFAULT_END ");
		sb.append(" 	FROM ");
		sb.append(" 		NC ");
		sb.append(" 		JOIN AVAILABILITY_DEFAULT AS D ON NC.AVAILABILITY_ID = D.AVAILABILITY_ID AND D.ACTIVE = 'Y' AND D.DAY_OF_WEEK = NC.START_DAY AND NC.TIME_END > D.TIME_START AND NC.TIME_START < D.TIME_END ");
		sb.append(" ), U AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		SCHEDULE_ID, ");
		sb.append(" 		AVAILABILITY_ID, ");
		sb.append(" 		DATE_START, ");
		sb.append(" 		TIME_START, ");
		sb.append(" 		TIME_END, ");
		sb.append(" 		START_DAY, ");
		sb.append(" 		END_DAY, ");
		sb.append(" 		null AS DAY_OF_WEEK, ");
		sb.append(" 		null AS DEFAULT_ID, ");
		sb.append(" 		CUSTOM_DATE, ");
		sb.append(" 		CUSTOM_ID, ");
		sb.append(" 		CUSTOM_START AS AVAILABILITY_START, ");
		sb.append(" 		CUSTOM_END AS AVAILABILITY_END, ");
		sb.append(" 		'custom' AS TYPE  ");
		sb.append(" 	FROM ");
		sb.append(" 		C ");
		sb.append(" 		WHERE ");
		sb.append(" 		C.CUSTOM_ID IS NOT NULL ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		SCHEDULE_ID, ");
		sb.append(" 		AVAILABILITY_ID, ");
		sb.append(" 		DATE_START, ");
		sb.append(" 		TIME_START, ");
		sb.append(" 		TIME_END, ");
		sb.append(" 		START_DAY, ");
		sb.append(" 		END_DAY, ");
		sb.append(" 		DAY_OF_WEEK, ");
		sb.append(" 		DEFAULT_ID, ");
		sb.append(" 		null AS CUSTOM_DATE, ");
		sb.append(" 		null AS CUSTOM_ID, ");
		sb.append(" 		DEFAULT_START AS AVAILABILITY_START, ");
		sb.append(" 		DEFAULT_END AS AVAILABILITY_END, ");
		sb.append(" 		'default' AS TYPE ");
		sb.append(" 	FROM ");
		sb.append(" 		D ");
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" 	DATE_START, ");
		sb.append(" 	AVAILABILITY_START AS TIME_START, ");
		sb.append(" 	AVAILABILITY_END AS TIME_END, ");
		sb.append(" 	COUNT(DISTINCT SCHEDULE_ID) AS TAKEN ");
		sb.append(" FROM ");
		sb.append(" 	U ");
		sb.append(" GROUP BY ");
		sb.append(" DATE_START,AVAILABILITY_START,AVAILABILITY_END  ");

		return sb.toString();
	}*/

	public static String getHoliday1(Timekeeper start, Timekeeper end, int avid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" H.HOLIDAY ");
		sb.append(" , ");
		sb.append(" H.MESSAGE ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" T.CLOSED ");
		sb.append(" , ");
		sb.append(" H.HOLIDAY_DATE ");
		sb.append(" FROM ");
		sb.append(" AVAILABILITY AS A ");
		sb.append(" JOIN LKUP_HOLIDAY_TYPE AS T ON (A.LKUP_HOLIDAY_TYPE_ID = T.ID OR (A.LKUP_HOLIDAY_TYPE_ID IS NULL AND T.GLOBAL_HOLIDAY = 'Y')) AND T.ACTIVE = 'Y' ");
		sb.append(" JOIN HOLIDAY AS H ON T.ID = H.LKUP_HOLIDAY_TYPE_ID ");
		sb.append(" WHERE ");
		sb.append(" H.HOLIDAY_DATE >= ").append(start.sqlServer());
		sb.append(" AND ");
		sb.append(" H.HOLIDAY_DATE <= ").append(end.sqlServer());
		sb.append(" AND ");
		sb.append(" A.ID = ").append(avid);
		return sb.toString();
	}

	public static String getHoliday(Timekeeper start, Timekeeper end, int avid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" H.HOLIDAY ");
		sb.append(" , ");
		sb.append(" H.MESSAGE ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" T.CLOSED ");
		sb.append(" , ");
		sb.append(" H.HOLIDAY_DATE ");
		sb.append(" FROM ");
		sb.append(" HOLIDAY AS H ");
		if (avid > 0) {
			sb.append(" JOIN ( ");
			sb.append("   LKUP_HOLIDAY_TYPE AS T ");
			sb.append("   LEFT OUTER JOIN AVAILABILITY AS A ON A.LKUP_HOLIDAY_TYPE_ID = T.ID AND T.ACTIVE = 'Y' AND A.ID = ").append(avid);
			sb.append(" ) ON T.ID = H.LKUP_HOLIDAY_TYPE_ID AND T.ACTIVE = 'Y' AND H.ACTIVE = 'Y' AND ( T.GLOBAL_HOLIDAY = 'Y' OR A.ID = ").append(avid).append(" ) ");
		}
		else {
			sb.append("  JOIN LKUP_HOLIDAY_TYPE AS T ON T.ID = H.LKUP_HOLIDAY_TYPE_ID AND T.ACTIVE = 'Y' AND H.ACTIVE = 'Y' AND T.GLOBAL_HOLIDAY = 'Y' ");
		}

		sb.append(" WHERE ");
		sb.append(" H.HOLIDAY_DATE >= ").append(start.sqlServer());
		sb.append(" AND ");
		sb.append(" H.HOLIDAY_DATE <= ").append(end.sqlServer());
		return sb.toString();
	}

	public static String getAvailability(String type, int typeid) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String maintable = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.* ");
		sb.append(" FROM ");
		sb.append(maintable).append(" AS M ");
		if (Operator.equalsIgnoreCase(type, "REVIEW")) {
			sb.append(" JOIN AVAILABILITY AS A ON M.AVAILABILITY_ID = A.ID AND A.ACTIVE = 'Y' ");
		}
		else {
			sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON T.ACTIVE = 'Y' AND M.ACTIVE = 'Y' AND M.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
			sb.append(" JOIN AVAILABILITY AS A ON T.AVAILABILITY_ID = A.ID AND A.ACTIVE = 'Y' ");
		}
		sb.append(" WHERE ");
		sb.append(" M.ID = ").append(typeid);
		return sb.toString();
	}

	public static String getAvailability(int avid) {
		if (avid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(" AVAILABILITY AS A ");
		sb.append(" WHERE ");
		sb.append(" A.ID = ").append(avid);
		return sb.toString();
	}

	public static String getAppointmentTypeAvailability(int appttype) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.* ");
		sb.append(" FROM ");
		sb.append(" LKUP_APPOINTMENT_TYPE AS T ");
		sb.append(" JOIN AVAILABILITY AS A ON T.AVAILABILITY_ID = A.ID AND A.ACTIVE = 'Y' AND T.ACTIVE = 'Y' AND T.ID = ").append(appttype);
		return sb.toString();
	}

	public static String getAvailability(String type, int typeid, int appttype, int apptsubtype) {
		if (apptsubtype > 0) {
			return getAvailability("review", apptsubtype);
		}
		else if (appttype > 0) {
			return getAppointmentTypeAvailability(appttype);
		}
		else {
			return getAvailability(type, typeid);
		}
	}

	public static String getDefaultAvailability(int defaultid) {
		if (defaultid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.ID AS AVAILABILITY_ID");
		sb.append(" , ");
		sb.append(" D.ID AS AVAILABILITY_DEFAULT_ID");
		sb.append(" , ");
		sb.append(" D.DAY_OF_WEEK ");
		sb.append(" , ");
		sb.append(" D.TIME_START ");
		sb.append(" , ");
		sb.append(" D.TIME_END ");
		sb.append(" , ");
		sb.append(" D.SEATS ");
		sb.append(" , ");
		sb.append(" D.BUFFER_SEATS ");
		sb.append(" , ");
		sb.append(" D.BUFFER_HOURS ");
		sb.append(" , ");
		sb.append(" D.TIME_BEGIN ");
		sb.append(" , ");
		sb.append(" D.TIME_STOP ");
		sb.append(" FROM ");
		sb.append(" AVAILABILITY_DEFAULT AS D ");
		sb.append(" JOIN AVAILABILITY AS A ON D.AVAILABILITY_ID = A.ID ");
		sb.append(" WHERE ");
		sb.append(" D.ID = ").append(defaultid);
		return sb.toString();
	}


	public static String getDefaultTaken(Timekeeper d, int defaultid, String excludeapptids) {
		
		String ex = "";
		if (Operator.hasValue(excludeapptids)) {
			StringBuilder sc = new StringBuilder();
			sc.append("         AND ");
			sc.append("         A.ID NOT IN ( ");
			String[] a = Operator.split(excludeapptids, "|");
			boolean empty = true;
			for (int i=0; i<a.length; i++) {
				int ai = Operator.toInt(a[i]);
				if (ai > 0) {
					if (!empty) { sc.append(" , "); }
					sc.append(ai);
					empty = false;
				}
			}
			sc.append(" ) ");
			if (!empty) {
				ex = sc.toString();
			}
		}





		StringBuilder sb = new StringBuilder();
		sb.append(" WITH S AS ( ");

		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		S.ID AS SCHEDULE_ID, ");
		sb.append(" 		AV.ID AS AVAILABILITY_ID, ");
		sb.append(" 		AVD.ID AS AVAILABILITY_DEFAULT_ID, ");
		sb.append(" 		AVD.SEATS, ");
		sb.append("         AVD.TIME_BEGIN, ");
		sb.append("         AVD.TIME_STOP, ");
		sb.append("         AVD.BUFFER_SEATS, ");
		sb.append("         AVD.BUFFER_HOURS, ");

		sb.append(" 		CONVERT(date, S.START_DATE) AS DATE_START, ");
		sb.append(" 		CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) AS TIME_START, ");
		sb.append("         AVD.TIME_START AS AVAILABILITY_START, ");

		sb.append(" 		CONVERT(date, S.END_DATE) AS DATE_END, ");
		sb.append(" 		 CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) AS TIME_END,   ");
		sb.append("         AVD.TIME_END AS AVAILABILITY_END, ");

		sb.append(" 		DATEDIFF(day,S.START_DATE,S.END_DATE) AS DIFF, ");

		sb.append(" 		DATEPART ( weekday , S.START_DATE ) AS START_DAY, ");
		sb.append(" 		DATEPART ( weekday , S.END_DATE ) AS END_DAY ");

		sb.append(" 		, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NACS.SCHEDULE_INSPECTION_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 			WHEN NACS.SCHEDULE_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 			ELSE 'N' END AS CANCELLED ");

		sb.append(" 	FROM ");
		sb.append(" 		APPOINTMENT AS A ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' AND A.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULED = 'Y' ");

		sb.append("         JOIN ( ");

		sb.append("           AVAILABILITY AS AV ");
		sb.append("           JOIN AVAILABILITY_DEFAULT AS AVD ON AV.ID = AVD.AVAILABILITY_ID AND AVD.ACTIVE = 'Y' AND AV.ACTIVE = 'Y' ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REVIEW AS REV ");
		sb.append("              JOIN REF_COMBOREVIEW_REVIEW AS CR ON REV.ID = CR.REVIEW_ID ");
		sb.append("           ) ON AV.ID = REV.AVAILABILITY_ID AND REV.ACTIVE = 'Y' ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REF_ACT_APPOINTMENT AS RACA ");
		sb.append("              JOIN ACTIVITY AS ACT ON RACA.ACTIVITY_ID = ACT.ID AND ACT.ACTIVE = 'Y' ");
		sb.append("              JOIN LKUP_ACT_TYPE AS LAT ON ACT.LKUP_ACT_TYPE_ID = LAT.ID AND LAT.ACTIVE = 'Y' ");
		sb.append("           )  ON AV.ID = LAT.AVAILABILITY_ID ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REF_PROJECT_APPOINTMENT AS RPA ");
		sb.append("              JOIN PROJECT AS P ON RPA.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append("              JOIN LKUP_PROJECT_TYPE AS LPT ON P.LKUP_PROJECT_TYPE_ID = LPT.ID AND LPT.ACTIVE = 'Y' ");
		sb.append("           )  ON AV.ID = LPT.AVAILABILITY_ID ");

		sb.append("         ) ON ");
		sb.append("           CR.ID = A.REF_COMBOREVIEW_REVIEW_ID ");
		sb.append("           AND ");
		sb.append("           A.REF_COMBOREVIEW_REVIEW_ID > 0 ");
		sb.append("           AND ");
		sb.append("            CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) > AVD.TIME_START ");
		sb.append("           AND ");
		sb.append("            CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) < AVD.TIME_END ");

		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_COMBOREVIEW_ACTION AS AC ");
        sb.append(" 			JOIN REF_COMBOREVIEW_ACTION AS NAC ON AC.ID = NAC.PREVIOUS_ID ");
        sb.append(" 			JOIN LKUP_REVIEW_STATUS AS NACS ON NAC.LKUP_REVIEW_STATUS_ID = NACS.ID ");
    	sb.append(" 		) ON AC.ID = S.REF_COMBOREVIEW_ACTION_ID ");

		sb.append("		WHERE ");
		sb.append("			CONVERT(date, S.START_DATE) >= ").append(d.sqlServer());
		sb.append("			AND ");
		sb.append("			CONVERT(date, S.END_DATE) <= ").append(d.sqlServer());
		sb.append("         AND ");
		sb.append("         AVD.ID = ").append(defaultid);

		sb.append(ex);

		sb.append(" UNION ");

		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		S.ID AS SCHEDULE_ID, ");
		sb.append(" 		AV.ID AS AVAILABILITY_ID, ");
		sb.append(" 		AVD.ID AS AVAILABILITY_DEFAULT_ID, ");
		sb.append(" 		AVD.SEATS, ");
		sb.append("         AVD.TIME_BEGIN, ");
		sb.append("         AVD.TIME_STOP, ");
		sb.append("         AVD.BUFFER_SEATS, ");
		sb.append("         AVD.BUFFER_HOURS, ");

		sb.append(" 		CONVERT(date, S.START_DATE) AS DATE_START, ");
		sb.append(" 		CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) AS TIME_START, ");
		sb.append("         AVD.TIME_START AS AVAILABILITY_START, ");

		sb.append(" 		CONVERT(date, S.END_DATE) AS DATE_END, ");
		sb.append(" 		 CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) AS TIME_END,   ");
		sb.append("         AVD.TIME_END AS AVAILABILITY_END, ");

		sb.append(" 		DATEDIFF(day,S.START_DATE,S.END_DATE) AS DIFF, ");

		sb.append(" 		DATEPART ( weekday , S.START_DATE ) AS START_DAY, ");
		sb.append(" 		DATEPART ( weekday , S.END_DATE ) AS END_DAY ");

		sb.append(" 		, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NACS.SCHEDULE_INSPECTION_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 			WHEN NACS.SCHEDULE_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 			ELSE 'N' END AS CANCELLED ");

		sb.append(" 	FROM ");
		sb.append(" 		APPOINTMENT AS A ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' AND A.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULED = 'Y' ");

		sb.append("         JOIN ( ");

		sb.append("           AVAILABILITY AS AV ");
		sb.append("           JOIN AVAILABILITY_DEFAULT AS AVD ON AV.ID = AVD.AVAILABILITY_ID AND AVD.ACTIVE = 'Y' AND AV.ACTIVE = 'Y' ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REVIEW AS REV ");
		sb.append("              JOIN REF_COMBOREVIEW_REVIEW AS CR ON REV.ID = CR.REVIEW_ID ");
		sb.append("           ) ON AV.ID = REV.AVAILABILITY_ID AND REV.ACTIVE = 'Y' ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REF_ACT_APPOINTMENT AS RACA ");
		sb.append("              JOIN ACTIVITY AS ACT ON RACA.ACTIVITY_ID = ACT.ID AND ACT.ACTIVE = 'Y' ");
		sb.append("              JOIN LKUP_ACT_TYPE AS LAT ON ACT.LKUP_ACT_TYPE_ID = LAT.ID AND LAT.ACTIVE = 'Y' ");
		sb.append("           )  ON AV.ID = LAT.AVAILABILITY_ID ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REF_PROJECT_APPOINTMENT AS RPA ");
		sb.append("              JOIN PROJECT AS P ON RPA.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append("              JOIN LKUP_PROJECT_TYPE AS LPT ON P.LKUP_PROJECT_TYPE_ID = LPT.ID AND LPT.ACTIVE = 'Y' ");
		sb.append("           )  ON AV.ID = LPT.AVAILABILITY_ID ");

		sb.append("         ) ON ");
		sb.append("           RACA.APPOINTMENT_ID = A.ID ");
		sb.append("           AND ");
		sb.append("           A.REF_COMBOREVIEW_REVIEW_ID < 1 ");
		sb.append("           AND ");
		sb.append("            CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) > AVD.TIME_START ");
		sb.append("           AND ");
		sb.append("            CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) < AVD.TIME_END ");

		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_COMBOREVIEW_ACTION AS AC ");
        sb.append(" 			JOIN REF_COMBOREVIEW_ACTION AS NAC ON AC.ID = NAC.PREVIOUS_ID ");
        sb.append(" 			JOIN LKUP_REVIEW_STATUS AS NACS ON NAC.LKUP_REVIEW_STATUS_ID = NACS.ID ");
    	sb.append(" 		) ON AC.ID = S.REF_COMBOREVIEW_ACTION_ID ");

		sb.append("		WHERE ");
		sb.append("			CONVERT(date, S.START_DATE) >= ").append(d.sqlServer());
		sb.append("			AND ");
		sb.append("			CONVERT(date, S.END_DATE) <= ").append(d.sqlServer());
		sb.append("         AND ");
		sb.append("         AVD.ID = ").append(defaultid);

		sb.append(ex);

		sb.append(" UNION ");

		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		S.ID AS SCHEDULE_ID, ");
		sb.append(" 		AV.ID AS AVAILABILITY_ID, ");
		sb.append(" 		AVD.ID AS AVAILABILITY_DEFAULT_ID, ");
		sb.append(" 		AVD.SEATS, ");
		sb.append("         AVD.TIME_BEGIN, ");
		sb.append("         AVD.TIME_STOP, ");
		sb.append("         AVD.BUFFER_SEATS, ");
		sb.append("         AVD.BUFFER_HOURS, ");

		sb.append(" 		CONVERT(date, S.START_DATE) AS DATE_START, ");
		sb.append(" 		CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) AS TIME_START, ");
		sb.append("         AVD.TIME_START AS AVAILABILITY_START, ");

		sb.append(" 		CONVERT(date, S.END_DATE) AS DATE_END, ");
		sb.append(" 		 CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) AS TIME_END,   ");
		sb.append("         AVD.TIME_END AS AVAILABILITY_END, ");

		sb.append(" 		DATEDIFF(day,S.START_DATE,S.END_DATE) AS DIFF, ");

		sb.append(" 		DATEPART ( weekday , S.START_DATE ) AS START_DAY, ");
		sb.append(" 		DATEPART ( weekday , S.END_DATE ) AS END_DAY ");

		sb.append(" 		, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NACS.SCHEDULE_INSPECTION_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 			WHEN NACS.SCHEDULE_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 			ELSE 'N' END AS CANCELLED ");

		sb.append(" 	FROM ");
		sb.append(" 		APPOINTMENT AS A ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' AND A.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULED = 'Y' ");

		sb.append("         JOIN ( ");

		sb.append("           AVAILABILITY AS AV ");
		sb.append("           JOIN AVAILABILITY_DEFAULT AS AVD ON AV.ID = AVD.AVAILABILITY_ID AND AVD.ACTIVE = 'Y' AND AV.ACTIVE = 'Y' ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REVIEW AS REV ");
		sb.append("              JOIN REF_COMBOREVIEW_REVIEW AS CR ON REV.ID = CR.REVIEW_ID ");
		sb.append("           ) ON AV.ID = REV.AVAILABILITY_ID AND REV.ACTIVE = 'Y' ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REF_ACT_APPOINTMENT AS RACA ");
		sb.append("              JOIN ACTIVITY AS ACT ON RACA.ACTIVITY_ID = ACT.ID AND ACT.ACTIVE = 'Y' ");
		sb.append("              JOIN LKUP_ACT_TYPE AS LAT ON ACT.LKUP_ACT_TYPE_ID = LAT.ID AND LAT.ACTIVE = 'Y' ");
		sb.append("           )  ON AV.ID = LAT.AVAILABILITY_ID ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REF_PROJECT_APPOINTMENT AS RPA ");
		sb.append("              JOIN PROJECT AS P ON RPA.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append("              JOIN LKUP_PROJECT_TYPE AS LPT ON P.LKUP_PROJECT_TYPE_ID = LPT.ID AND LPT.ACTIVE = 'Y' ");
		sb.append("           )  ON AV.ID = LPT.AVAILABILITY_ID ");

		sb.append("         ) ON ");
		sb.append("           RPA.APPOINTMENT_ID = A.ID ");
		sb.append("           AND ");
		sb.append("           A.REF_COMBOREVIEW_REVIEW_ID < 1 ");
		sb.append("           AND ");
		sb.append("            CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) > AVD.TIME_START ");
		sb.append("           AND ");
		sb.append("            CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) < AVD.TIME_END ");

		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_COMBOREVIEW_ACTION AS AC ");
        sb.append(" 			JOIN REF_COMBOREVIEW_ACTION AS NAC ON AC.ID = NAC.PREVIOUS_ID ");
        sb.append(" 			JOIN LKUP_REVIEW_STATUS AS NACS ON NAC.LKUP_REVIEW_STATUS_ID = NACS.ID ");
    	sb.append(" 		) ON AC.ID = S.REF_COMBOREVIEW_ACTION_ID ");

		sb.append("		WHERE ");
		sb.append("			CONVERT(date, S.START_DATE) >= ").append(d.sqlServer());
		sb.append("			AND ");
		sb.append("			CONVERT(date, S.END_DATE) <= ").append(d.sqlServer());
		sb.append("         AND ");
		sb.append("         AVD.ID = ").append(defaultid);

		sb.append(ex);



		sb.append(" ) ");
		sb.append(" , Q AS ( SELECT * FROM S WHERE CANCELLED = 'N' ) ");

		sb.append(" SELECT ");
		sb.append(" DATE_START ");
		sb.append(" , ");
		sb.append(" AVAILABILITY_START AS TIME_START ");
		sb.append(" , ");
		sb.append(" AVAILABILITY_END AS TIME_END ");
		sb.append(" , ");
		sb.append(" SEATS ");
		sb.append(" , ");
		sb.append(" TIME_BEGIN ");
		sb.append(" , ");
		sb.append(" TIME_STOP ");
		sb.append(" , ");
		sb.append(" BUFFER_SEATS ");
		sb.append(" , ");
		sb.append(" BUFFER_HOURS ");
		sb.append(" , ");
		sb.append(" COUNT(DISTINCT SCHEDULE_ID) AS TAKEN ");
		sb.append(" FROM ");
		sb.append(" Q ");
		sb.append(" GROUP BY ");
		sb.append(" DATE_START ");
		sb.append(" , ");
		sb.append(" AVAILABILITY_START ");
		sb.append(" , ");
		sb.append(" AVAILABILITY_END ");
		sb.append(" , ");
		sb.append(" SEATS ");
		sb.append(" , ");
		sb.append(" TIME_BEGIN ");
		sb.append(" , ");
		sb.append(" TIME_STOP ");
		sb.append(" , ");
		sb.append(" BUFFER_SEATS ");
		sb.append(" , ");
		sb.append(" BUFFER_HOURS ");
		return sb.toString();
	}

	public static String getCustomAvailability(int customid) {
		if (customid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.ID AS AVAILABILITY_ID");
		sb.append(" , ");
		sb.append(" C.ID AS AVAILABILITY_CUSTOM_ID");
		sb.append(" , ");
		sb.append(" C.CUSTOM_DATE ");
		sb.append(" , ");
		sb.append(" C.TIME_START ");
		sb.append(" , ");
		sb.append(" C.TIME_END ");
		sb.append(" , ");
		sb.append(" C.SEATS ");
		sb.append(" , ");
		sb.append(" C.BUFFER_SEATS ");
		sb.append(" , ");
		sb.append(" C.BUFFER_HOURS ");
		sb.append(" , ");
		sb.append(" C.TIME_BEGIN ");
		sb.append(" , ");
		sb.append(" C.TIME_STOP ");
		sb.append(" FROM ");
		sb.append(" AVAILABILITY_CUSTOM AS C ");
		sb.append(" JOIN AVAILABILITY AS A ON C.AVAILABILITY_ID = A.ID ");
		sb.append(" WHERE C.ID = ").append(customid);
		return sb.toString();
	}

	public static String getCustomAvailability(Timekeeper d) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.ID AS AVAILABILITY_ID");
		sb.append(" , ");
		sb.append(" C.ID AS AVAILABILITY_CUSTOM_ID");
		sb.append(" , ");
		sb.append(" C.CUSTOM_DATE ");
		sb.append(" , ");
		sb.append(" C.TIME_START ");
		sb.append(" , ");
		sb.append(" C.TIME_END ");
		sb.append(" , ");
		sb.append(" C.SEATS ");
		sb.append(" , ");
		sb.append(" C.BUFFER_SEATS ");
		sb.append(" , ");
		sb.append(" C.BUFFER_HOURS ");
		sb.append(" , ");
		sb.append(" C.TIME_BEGIN ");
		sb.append(" , ");
		sb.append(" C.TIME_STOP ");
		sb.append(" FROM ");
		sb.append(" AVAILABILITY AS A ");
		sb.append(" JOIN AVAILABILITY_CUSTOM AS C ON C.AVAILABILITY_ID = A.ID AND C.ACTIVE = 'Y' AND C.CUSTOM_DATE = ").append(d.sqlServer());
		return sb.toString();
	}

	public static String getCustomAvailability(Timekeeper d, int defaultid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append("  SELECT DISTINCT AVAILABILITY_ID FROM AVAILABILITY_DEFAULT WHERE ID = ").append(defaultid);
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" A.ID AS AVAILABILITY_ID");
		sb.append(" , ");
		sb.append(" C.ID AS AVAILABILITY_CUSTOM_ID");
		sb.append(" , ");
		sb.append(" C.CUSTOM_DATE ");
		sb.append(" , ");
		sb.append(" C.TIME_START ");
		sb.append(" , ");
		sb.append(" C.TIME_END ");
		sb.append(" , ");
		sb.append(" C.SEATS ");
		sb.append(" , ");
		sb.append(" C.BUFFER_SEATS ");
		sb.append(" , ");
		sb.append(" C.BUFFER_HOURS ");
		sb.append(" , ");
		sb.append(" C.TIME_BEGIN ");
		sb.append(" , ");
		sb.append(" C.TIME_STOP ");
		sb.append(" FROM ");
		sb.append(" AVAILABILITY AS A ");
		sb.append(" JOIN AVAILABILITY_CUSTOM AS C ON C.AVAILABILITY_ID = A.ID AND C.ACTIVE = 'Y' AND C.CUSTOM_DATE = ").append(d.sqlServer());
		sb.append(" JOIN Q ON Q.AVAILABILITY_ID = C.AVAILABILITY_ID ");
		return sb.toString();
	}

	public static String getCustomTaken(Timekeeper d, int customid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH S AS ( ");

		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		S.ID AS SCHEDULE_ID, ");
		sb.append(" 		AV.ID AS AVAILABILITY_ID, ");
		sb.append(" 		AVC.ID AS AVAILABILITY_CUSTOM_ID, ");
		sb.append(" 		AVC.SEATS, ");
		sb.append("         AVC.TIME_BEGIN, ");
		sb.append("         AVC.TIME_STOP, ");
		sb.append("         AVC.BUFFER_SEATS, ");
		sb.append("         AVC.BUFFER_HOURS, ");

		sb.append(" 		CONVERT(date, S.START_DATE) AS DATE_START, ");
		sb.append(" 		CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) AS TIME_START, ");
		sb.append("         AVC.TIME_START AS AVAILABILITY_START, ");

		sb.append(" 		CONVERT(date, S.END_DATE) AS DATE_END, ");
		sb.append(" 		 CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) AS TIME_END,   ");
		sb.append("         AVC.TIME_END AS AVAILABILITY_END, ");

		sb.append(" 		DATEDIFF(day,S.START_DATE,S.END_DATE) AS DIFF, ");

		sb.append(" 		DATEPART ( weekday , S.START_DATE ) AS START_DAY, ");
		sb.append(" 		DATEPART ( weekday , S.END_DATE ) AS END_DAY ");

		sb.append(" 		, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NACS.SCHEDULE_INSPECTION_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 			WHEN NACS.SCHEDULE_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 			ELSE 'N' END AS CANCELLED ");

		sb.append(" 	FROM ");
		sb.append(" 		APPOINTMENT AS A ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' AND A.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULED = 'Y' ");

		sb.append("         JOIN ( ");

		sb.append("           AVAILABILITY AS AV ");
		sb.append("           JOIN AVAILABILITY_CUSTOM AS AVC ON AV.ID = AVC.AVAILABILITY_ID AND AVC.ACTIVE = 'Y' ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REVIEW AS REV ");
		sb.append("              JOIN REF_COMBOREVIEW_REVIEW AS CR ON REV.ID = CR.REVIEW_ID ");
		sb.append("           ) ON AV.ID = REV.AVAILABILITY_ID AND REV.ACTIVE = 'Y' ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REF_ACT_APPOINTMENT AS RACA ");
		sb.append("              JOIN ACTIVITY AS ACT ON RACA.ACTIVITY_ID = ACT.ID AND ACT.ACTIVE = 'Y' ");
		sb.append("              JOIN LKUP_ACT_TYPE AS LAT ON ACT.LKUP_ACT_TYPE_ID = LAT.ID AND LAT.ACTIVE = 'Y' ");
		sb.append("           )  ON AV.ID = LAT.AVAILABILITY_ID ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REF_PROJECT_APPOINTMENT AS RPA ");
		sb.append("              JOIN PROJECT AS P ON RPA.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append("              JOIN LKUP_PROJECT_TYPE AS LPT ON P.LKUP_PROJECT_TYPE_ID = LPT.ID AND LPT.ACTIVE = 'Y' ");
		sb.append("           )  ON AV.ID = LPT.AVAILABILITY_ID ");

		sb.append("         ) ON ");
		sb.append("           CR.ID = A.REF_COMBOREVIEW_REVIEW_ID ");
		sb.append("           AND ");
		sb.append("           A.REF_COMBOREVIEW_REVIEW_ID > 0 ");
		sb.append("           AND ");
		sb.append("           CONVERT(date, S.START_DATE) = AVC.CUSTOM_DATE ");
		sb.append("           AND ");
		sb.append("            CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) > AVC.TIME_START ");
		sb.append("           AND ");
		sb.append("            CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) < AVC.TIME_END ");

		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_COMBOREVIEW_ACTION AS AC ");
        sb.append(" 			JOIN REF_COMBOREVIEW_ACTION AS NAC ON AC.ID = NAC.PREVIOUS_ID ");
        sb.append(" 			JOIN LKUP_REVIEW_STATUS AS NACS ON NAC.LKUP_REVIEW_STATUS_ID = NACS.ID ");
    	sb.append(" 		) ON AC.ID = S.REF_COMBOREVIEW_ACTION_ID ");
		
		
		sb.append("		WHERE ");
	//	sb.append("         (RT.ID IS NOT NULL OR SRRT.ID IS NOT NULL) ");
	//	sb.append("         AND ");
		sb.append("			CONVERT(date, S.START_DATE) >= ").append(d.sqlServer());
		sb.append("			AND ");
		sb.append("			CONVERT(date, S.END_DATE) <= ").append(d.sqlServer());
		sb.append("         AND ");
		sb.append("         AVC.ID = ").append(customid);

		sb.append(" UNION ");

		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		S.ID AS SCHEDULE_ID, ");
		sb.append(" 		AV.ID AS AVAILABILITY_ID, ");
		sb.append(" 		AVC.ID AS AVAILABILITY_CUSTOM_ID, ");
		sb.append(" 		AVC.SEATS, ");
		sb.append("         AVC.TIME_BEGIN, ");
		sb.append("         AVC.TIME_STOP, ");
		sb.append("         AVC.BUFFER_SEATS, ");
		sb.append("         AVC.BUFFER_HOURS, ");

		sb.append(" 		CONVERT(date, S.START_DATE) AS DATE_START, ");
		sb.append(" 		CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) AS TIME_START, ");
		sb.append("         AVC.TIME_START AS AVAILABILITY_START, ");

		sb.append(" 		CONVERT(date, S.END_DATE) AS DATE_END, ");
		sb.append(" 		 CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) AS TIME_END,   ");
		sb.append("         AVC.TIME_END AS AVAILABILITY_END, ");

		sb.append(" 		DATEDIFF(day,S.START_DATE,S.END_DATE) AS DIFF, ");

		sb.append(" 		DATEPART ( weekday , S.START_DATE ) AS START_DAY, ");
		sb.append(" 		DATEPART ( weekday , S.END_DATE ) AS END_DAY ");

		sb.append(" 		, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NACS.SCHEDULE_INSPECTION_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 			WHEN NACS.SCHEDULE_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 			ELSE 'N' END AS CANCELLED ");

		sb.append(" 	FROM ");
		sb.append(" 		APPOINTMENT AS A ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' AND A.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULED = 'Y' ");

		sb.append("         JOIN ( ");

		sb.append("           AVAILABILITY AS AV ");
		sb.append("           JOIN AVAILABILITY_CUSTOM AS AVC ON AV.ID = AVC.AVAILABILITY_ID AND AVC.ACTIVE = 'Y' ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REVIEW AS REV ");
		sb.append("              JOIN REF_COMBOREVIEW_REVIEW AS CR ON REV.ID = CR.REVIEW_ID ");
		sb.append("           ) ON AV.ID = REV.AVAILABILITY_ID AND REV.ACTIVE = 'Y' ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REF_ACT_APPOINTMENT AS RACA ");
		sb.append("              JOIN ACTIVITY AS ACT ON RACA.ACTIVITY_ID = ACT.ID AND ACT.ACTIVE = 'Y' ");
		sb.append("              JOIN LKUP_ACT_TYPE AS LAT ON ACT.LKUP_ACT_TYPE_ID = LAT.ID AND LAT.ACTIVE = 'Y' ");
		sb.append("           )  ON AV.ID = LAT.AVAILABILITY_ID ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REF_PROJECT_APPOINTMENT AS RPA ");
		sb.append("              JOIN PROJECT AS P ON RPA.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append("              JOIN LKUP_PROJECT_TYPE AS LPT ON P.LKUP_PROJECT_TYPE_ID = LPT.ID AND LPT.ACTIVE = 'Y' ");
		sb.append("           )  ON AV.ID = LPT.AVAILABILITY_ID ");

		sb.append("         ) ON ");
		sb.append("           RACA.APPOINTMENT_ID = A.ID ");
		sb.append("           AND ");
		sb.append("           A.REF_COMBOREVIEW_REVIEW_ID < 1 ");
		sb.append("           AND ");
		sb.append("           CONVERT(date, S.START_DATE) = AVC.CUSTOM_DATE ");
		sb.append("           AND ");
		sb.append("            CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) > AVC.TIME_START ");
		sb.append("           AND ");
		sb.append("            CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) < AVC.TIME_END ");

		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_COMBOREVIEW_ACTION AS AC ");
        sb.append(" 			JOIN REF_COMBOREVIEW_ACTION AS NAC ON AC.ID = NAC.PREVIOUS_ID ");
        sb.append(" 			JOIN LKUP_REVIEW_STATUS AS NACS ON NAC.LKUP_REVIEW_STATUS_ID = NACS.ID ");
    	sb.append(" 		) ON AC.ID = S.REF_COMBOREVIEW_ACTION_ID ");
		
		sb.append("		WHERE ");
	//	sb.append("         (RT.ID IS NOT NULL OR SRRT.ID IS NOT NULL) ");
	//	sb.append("         AND ");
		sb.append("			CONVERT(date, S.START_DATE) >= ").append(d.sqlServer());
		sb.append("			AND ");
		sb.append("			CONVERT(date, S.END_DATE) <= ").append(d.sqlServer());
		sb.append("         AND ");
		sb.append("         AVC.ID = ").append(customid);

		sb.append(" UNION ");

		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		S.ID AS SCHEDULE_ID, ");
		sb.append(" 		AV.ID AS AVAILABILITY_ID, ");
		sb.append(" 		AVC.ID AS AVAILABILITY_CUSTOM_ID, ");
		sb.append(" 		AVC.SEATS, ");
		sb.append("         AVC.TIME_BEGIN, ");
		sb.append("         AVC.TIME_STOP, ");
		sb.append("         AVC.BUFFER_SEATS, ");
		sb.append("         AVC.BUFFER_HOURS, ");

		sb.append(" 		CONVERT(date, S.START_DATE) AS DATE_START, ");
		sb.append(" 		CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) AS TIME_START, ");
		sb.append("         AVC.TIME_START AS AVAILABILITY_START, ");

		sb.append(" 		CONVERT(date, S.END_DATE) AS DATE_END, ");
		sb.append(" 		CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) AS TIME_END,   ");
		sb.append("         AVC.TIME_END AS AVAILABILITY_END, ");

		sb.append(" 		DATEDIFF(day,S.START_DATE,S.END_DATE) AS DIFF, ");

		sb.append(" 		DATEPART ( weekday , S.START_DATE ) AS START_DAY, ");
		sb.append(" 		DATEPART ( weekday , S.END_DATE ) AS END_DAY ");

		sb.append(" 		, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NACS.SCHEDULE_INSPECTION_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 			WHEN NACS.SCHEDULE_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 			ELSE 'N' END AS CANCELLED ");

		sb.append(" 	FROM ");
		sb.append(" 		APPOINTMENT AS A ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' AND A.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULED = 'Y' ");

		sb.append("         JOIN ( ");

		sb.append("           AVAILABILITY AS AV ");
		sb.append("           JOIN AVAILABILITY_CUSTOM AS AVC ON AV.ID = AVC.AVAILABILITY_ID AND AVC.ACTIVE = 'Y' ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REVIEW AS REV ");
		sb.append("              JOIN REF_COMBOREVIEW_REVIEW AS CR ON REV.ID = CR.REVIEW_ID ");
		sb.append("           ) ON AV.ID = REV.AVAILABILITY_ID AND REV.ACTIVE = 'Y' ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REF_ACT_APPOINTMENT AS RACA ");
		sb.append("              JOIN ACTIVITY AS ACT ON RACA.ACTIVITY_ID = ACT.ID AND ACT.ACTIVE = 'Y' ");
		sb.append("              JOIN LKUP_ACT_TYPE AS LAT ON ACT.LKUP_ACT_TYPE_ID = LAT.ID AND LAT.ACTIVE = 'Y' ");
		sb.append("           )  ON AV.ID = LAT.AVAILABILITY_ID ");

		sb.append("           LEFT OUTER JOIN ( ");
		sb.append("              REF_PROJECT_APPOINTMENT AS RPA ");
		sb.append("              JOIN PROJECT AS P ON RPA.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append("              JOIN LKUP_PROJECT_TYPE AS LPT ON P.LKUP_PROJECT_TYPE_ID = LPT.ID AND LPT.ACTIVE = 'Y' ");
		sb.append("           )  ON AV.ID = LPT.AVAILABILITY_ID ");

		sb.append("         ) ON ");
		sb.append("           RPA.APPOINTMENT_ID = A.ID ");
		sb.append("           AND ");
		sb.append("           A.REF_COMBOREVIEW_REVIEW_ID < 1 ");
		sb.append("           AND ");
		sb.append("           CONVERT(date, S.START_DATE) = AVC.CUSTOM_DATE ");
		sb.append("           AND ");
		sb.append("            CAST(CONVERT(varchar(5), S.END_DATE, 108) as time) > AVC.TIME_START ");
		sb.append("           AND ");
		sb.append("            CAST(CONVERT(varchar(5), S.START_DATE, 108) as time) < AVC.TIME_END ");

		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_COMBOREVIEW_ACTION AS AC ");
        sb.append(" 			JOIN REF_COMBOREVIEW_ACTION AS NAC ON AC.ID = NAC.PREVIOUS_ID ");
        sb.append(" 			JOIN LKUP_REVIEW_STATUS AS NACS ON NAC.LKUP_REVIEW_STATUS_ID = NACS.ID ");
    	sb.append(" 		) ON AC.ID = S.REF_COMBOREVIEW_ACTION_ID ");
		
		sb.append("		WHERE ");
		//sb.append("         (RT.ID IS NOT NULL OR SRRT.ID IS NOT NULL) ");
		//sb.append("         AND ");
		sb.append("			CONVERT(date, S.START_DATE) >= ").append(d.sqlServer());
		sb.append("			AND ");
		sb.append("			CONVERT(date, S.END_DATE) <= ").append(d.sqlServer());
		sb.append("         AND ");
		sb.append("         AVC.ID = ").append(customid);



		sb.append(" ) ");
		sb.append(" , Q AS ( SELECT * FROM S WHERE CANCELLED = 'N' ) ");
		sb.append(" SELECT ");
		sb.append(" DATE_START ");
		sb.append(" , ");
		sb.append(" AVAILABILITY_START AS TIME_START ");
		sb.append(" , ");
		sb.append(" AVAILABILITY_END AS TIME_END ");
		sb.append(" , ");
		sb.append(" SEATS ");
		sb.append(" , ");
		sb.append(" TIME_BEGIN ");
		sb.append(" , ");
		sb.append(" TIME_STOP ");
		sb.append(" , ");
		sb.append(" BUFFER_SEATS ");
		sb.append(" , ");
		sb.append(" BUFFER_HOURS ");
		sb.append(" , ");
		sb.append(" COUNT(DISTINCT SCHEDULE_ID) AS TAKEN ");
		sb.append(" FROM ");
		sb.append(" Q ");
		sb.append(" GROUP BY ");
		sb.append(" DATE_START ");
		sb.append(" , ");
		sb.append(" AVAILABILITY_START ");
		sb.append(" , ");
		sb.append(" AVAILABILITY_END ");
		sb.append(" , ");
		sb.append(" SEATS ");
		
		
		sb.append(" , ");
		sb.append(" TIME_BEGIN ");
		sb.append(" , ");
		sb.append(" TIME_STOP ");
		sb.append(" , ");
		sb.append(" BUFFER_SEATS ");
		sb.append(" , ");
		sb.append(" BUFFER_HOURS ");
		
		return sb.toString();
	}










}















