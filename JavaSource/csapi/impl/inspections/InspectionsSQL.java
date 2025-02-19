package csapi.impl.inspections;

import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.utils.CsReflect;

public class InspectionsSQL {

	public static String browse() {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");

		sb.append(" SELECT ");
		sb.append("     U.ID ");
		sb.append("     , ");
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
		sb.append("       END ");
		sb.append("     )) AS TITLE ");
		sb.append("     , ");
		sb.append("     U.USERNAME AS DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" REF_TEAM AS RT ");
		sb.append(" JOIN LKUP_TEAM_TYPE AS LTT ON RT.LKUP_TEAM_TYPE_ID = LTT.ID AND LOWER(LTT.TYPE) = 'inspector' AND RT.ACTIVE = 'Y' AND LTT.ACTIVE = 'Y' ");
		sb.append(" JOIN USERS AS U ON RT.USERS_ID = U.ID AND RT.USERS_ID > 0 AND U.ACTIVE = 'Y' ");

		sb.append(" ) ");
		sb.append(" SELECT DISTINCT * FROM Q ORDER BY TITLE ");
		return sb.toString();
	}

	public static String my(String username) {
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(myWith(username));
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	* ");
		sb.append(" FROM ");
		sb.append(" 	Q ");
		sb.append(" WHERE ");
		sb.append(" 	ACTIVE = 'Y' ");
		sb.append(" ORDER BY ");
		sb.append("         START_DATE DESC ");
		return sb.toString();
	}

	public static String myActive(String username) {
		if (!Operator.hasValue(username)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(myWith(username));
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	* ");
		sb.append(" FROM ");
		sb.append(" 	Q ");
		sb.append(" WHERE ");
		sb.append(" 	ACTIVE = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	(COMPLETE = 'N' OR START_DATE >= DATEADD(day, 7, CAST(getDate() AS DATE))) ");
		sb.append(" ORDER BY ");
		sb.append("         START_DATE DESC ");
		return sb.toString();
	}

	public static String myWith(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		RCR.ID AS REF_COMBOREVIEW_REVIEW_ID, ");
		sb.append(" 		RCA.ID AS REF_COMBOREVIEW_ACTION_ID, ");
		sb.append(" 		P.ID AS PROJECT_ID, ");
		sb.append(" 		P.PROJECT_NBR, ");
		sb.append(" 		P.NAME AS PROJECT_NAME, ");
		sb.append(" 		A.ID AS ACTIVITY_ID, ");
		sb.append(" 		A.ACT_NBR, ");
		sb.append(" 		AT.TYPE AS ACTIVITY_TYPE, ");
		sb.append(" 		'activity' AS REFERENCE_TYPE, ");
		sb.append(" 		A.ID AS REFERENCE_ID, ");
		sb.append(" 		A.ACT_NBR AS REFERENCE, ");
		sb.append(" 		R.NAME AS TYPE, ");
		sb.append(" 		APPT.SUBJECT, ");
		sb.append(" 		CAST(S.START_DATE AS DATE) as START_DATE, ");
		sb.append(" 		CAST(S.END_DATE AS DATE) as END_DATE, ");
		sb.append(" 		CONVERT(CHAR(5), S.START_DATE, 108) + ' - ' + CONVERT(CHAR(5), S.END_DATE, 108) AS TIME, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NOT NULL THEN 'N' ");
		sb.append(" 			WHEN S.START_DATE < getDate()+1 THEN 'N' ");
		sb.append(" 			ELSE 'Y' ");
		sb.append(" 		END AS EDITABLE, ");
		sb.append(" 		N.REVIEW_COMMENTS, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NOT NULL THEN NS.STATUS ");
		sb.append(" 			WHEN APPTS.SCHEDULED = 'N' THEN APPTS.STATUS ");
		sb.append(" 			ELSE ST.STATUS ");
		sb.append(" 		END AS STATUS, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NOT NULL THEN ");
		sb.append(" 				CASE ");
		sb.append(" 					WHEN NS.APPROVED = 'Y' THEN 'Y' ");
		sb.append(" 					WHEN NS.UNAPPROVED = 'Y' THEN 'Y' ");
		sb.append(" 					WHEN NS.FINAL = 'Y' THEN 'Y' ");
		sb.append(" 				ELSE 'N' END ");
		sb.append(" 			WHEN APPTS.COMPLETE = 'Y' THEN 'Y' ");
		sb.append(" 			ELSE 'N' ");
		sb.append(" 		END AS COMPLETE, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NULL AND RCA.EXPIRED = 'Y' THEN 'N' ");
		sb.append(" 		ELSE 'Y' END AS ACTIVE ");
		sb.append(" 	FROM ");
		sb.append(" 		ACTIVITY AS A ");
		sb.append(" 		JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND A.ACTIVE = 'Y' AND P.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_ACT_TYPE AS AT ON A.LKUP_ACT_TYPE_ID = AT.ID ");
		sb.append(" 		JOIN REF_ACT_COMBOREVIEW AS RAC ON A.ID = RAC.ACTIVITY_ID AND RAC.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_COMBOREVIEW_REVIEW AS RCR ON RAC.COMBOREVIEW_ID = RCR.COMBOREVIEW_ID AND RCR.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_COMBOREVIEW_ACTION AS RCA ON RCR.ID = RCA.REF_COMBOREVIEW_REVIEW_ID AND RCA.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REVIEW AS R ON RCR.REVIEW_ID = R.ID ");
		sb.append(" 		JOIN LKUP_REVIEW_STATUS AS ST ON RCA.LKUP_REVIEW_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULE_INSPECTION = 'Y' ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON RCA.ID = S.REF_COMBOREVIEW_ACTION_ID AND S.ACTIVE = 'Y' AND S.START_DATE >= getDate() - 365 ");
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS APPTS ON S.LKUP_APPOINTMENT_STATUS_ID = APPTS.ID ");
		sb.append(" 		JOIN APPOINTMENT AS APPT ON S.APPOINTMENT_ID = APPT.ID AND APPT.ACTIVE = 'Y' ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_ACT_USERS AS RAU ");
		sb.append(" 			JOIN REF_USERS AS RU ON RAU.REF_USERS_ID = RU.ID ");
		sb.append(" 			JOIN USERS AS U ON RU.USERS_ID = U.ID AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" 		) ON A.ID = RAU.ACTIVITY_ID AND RAU.ACTIVE = 'Y' ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_PROJECT_USERS AS RPU ");
		sb.append(" 			JOIN REF_USERS AS PRU ON RPU.REF_USERS_ID = PRU.ID ");
		sb.append(" 			JOIN USERS AS PU ON PRU.USERS_ID = PU.ID AND LOWER(PU.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" 		) ON A.PROJECT_ID = RPU.PROJECT_ID AND RPU.ACTIVE = 'Y' ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_COMBOREVIEW_ACTION AS N ");
		sb.append(" 			JOIN LKUP_REVIEW_STATUS AS NS ON N.LKUP_REVIEW_STATUS_ID = NS.ID ");
		sb.append(" 		) ON RCA.ID = N.PREVIOUS_ID AND N.ACTIVE = 'Y' ");
		sb.append(" 	WHERE ");
		sb.append(" 		LOWER('").append(Operator.sqlEscape(username)).append("') IN (LOWER(U.USERNAME),LOWER(PU.USERNAME)) ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		RCR.ID AS REF_COMBOREVIEW_REVIEW_ID, ");
		sb.append(" 		RCA.ID AS REF_COMBOREVIEW_ACTION_ID, ");
		sb.append(" 		P.ID AS PROJECT_ID, ");
		sb.append(" 		P.PROJECT_NBR, ");
		sb.append(" 		P.NAME AS PROJECT_NAME, ");
		sb.append(" 		A.ID AS ACTIVITY_ID, ");
		sb.append(" 		A.ACT_NBR, ");
		sb.append(" 		AT.TYPE AS ACTIVITY_TYPE, ");
		sb.append(" 		'activity' AS REFERENCE_TYPE, ");
		sb.append(" 		A.ID AS REFERENCE_ID, ");
		sb.append(" 		A.ACT_NBR AS REFERENCE, ");
		sb.append(" 		R.NAME AS TYPE, ");
		sb.append(" 		APPT.SUBJECT, ");
		sb.append(" 		CAST(S.START_DATE AS DATE) as START_DATE, ");
		sb.append(" 		CAST(S.END_DATE AS DATE) as END_DATE, ");
		sb.append(" 		CONVERT(CHAR(5), S.START_DATE, 108) + ' - ' + CONVERT(CHAR(5), S.END_DATE, 108) AS TIME, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NOT NULL THEN 'N' ");
		sb.append(" 			WHEN S.START_DATE < getDate()+1 THEN 'N' ");
		sb.append(" 			ELSE 'Y' ");
		sb.append(" 		END AS EDITABLE, ");
		sb.append(" 		N.REVIEW_COMMENTS, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NOT NULL THEN NS.STATUS ");
		sb.append(" 			WHEN APPTS.SCHEDULED = 'N' THEN APPTS.STATUS ");
		sb.append(" 			ELSE ST.STATUS ");
		sb.append(" 		END AS STATUS, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NOT NULL THEN ");
		sb.append(" 				CASE ");
		sb.append(" 					WHEN NS.APPROVED = 'Y' THEN 'Y' ");
		sb.append(" 					WHEN NS.UNAPPROVED = 'Y' THEN 'Y' ");
		sb.append(" 					WHEN NS.FINAL = 'Y' THEN 'Y' ");
		sb.append(" 				ELSE 'N' END ");
		sb.append(" 			WHEN APPTS.COMPLETE = 'Y' THEN 'Y' ");
		sb.append(" 			ELSE 'N' ");
		sb.append(" 		END AS COMPLETE, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NULL AND RCA.EXPIRED = 'Y' THEN 'N' ");
		sb.append(" 		ELSE 'Y' END AS ACTIVE ");
		sb.append(" 	FROM ");
		sb.append(" 		ACTIVITY AS A ");
		sb.append(" 		JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND A.ACTIVE = 'Y' AND P.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_ACT_TYPE AS AT ON A.LKUP_ACT_TYPE_ID = AT.ID ");
		sb.append(" 		JOIN REF_ACT_COMBOREVIEW AS RAC ON A.ID = RAC.ACTIVITY_ID AND RAC.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_COMBOREVIEW_REVIEW AS RCR ON RAC.COMBOREVIEW_ID = RCR.COMBOREVIEW_ID AND RCR.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_COMBOREVIEW_ACTION AS RCA ON RCR.ID = RCA.REF_COMBOREVIEW_REVIEW_ID AND RCA.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REVIEW AS R ON RCR.REVIEW_ID = R.ID ");
		sb.append(" 		JOIN LKUP_REVIEW_STATUS AS ST ON RCA.LKUP_REVIEW_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULE_INSPECTION = 'Y' ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON RCA.ID = S.REF_COMBOREVIEW_ACTION_ID AND S.ACTIVE = 'Y' AND S.START_DATE >= getDate() - 365 ");
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS APPTS ON S.LKUP_APPOINTMENT_STATUS_ID = APPTS.ID ");
		sb.append(" 		JOIN APPOINTMENT AS APPT ON S.APPOINTMENT_ID = APPT.ID AND APPT.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_APPOINTMENT_USERS AS RU ON S.APPOINTMENT_ID = RU.APPOINTMENT_ID AND RU.ACTIVE = 'Y' ");
		sb.append(" 		JOIN USERS AS U ON RU.USERS_ID = U.ID AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_COMBOREVIEW_ACTION AS N ");
		sb.append(" 			JOIN LKUP_REVIEW_STATUS AS NS ON N.LKUP_REVIEW_STATUS_ID = NS.ID ");
		sb.append(" 		) ON RCA.ID = N.PREVIOUS_ID AND N.ACTIVE = 'Y' ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		RCR.ID AS REF_COMBOREVIEW_REVIEW_ID, ");
		sb.append(" 		RCA.ID AS REF_COMBOREVIEW_ACTION_ID, ");
		sb.append(" 		P.ID AS PROJECT_ID, ");
		sb.append(" 		P.PROJECT_NBR, ");
		sb.append(" 		P.NAME AS PROJECT_NAME, ");
		sb.append(" 		-1 AS ACTIVITY_ID, ");
		sb.append(" 		'' AS ACT_NBR, ");
		sb.append(" 		'' AS ACTIVITY_TYPE, ");
		sb.append(" 		'project' AS REFERENCE_TYPE, ");
		sb.append(" 		P.ID AS REFERENCE_ID, ");
		sb.append(" 		P.PROJECT_NBR AS REFERENCE, ");
		sb.append(" 		R.NAME AS TYPE, ");
		sb.append(" 		APPT.SUBJECT, ");
		sb.append(" 		CAST(S.START_DATE AS DATE) as START_DATE, ");
		sb.append(" 		CAST(S.END_DATE AS DATE) as END_DATE, ");
		sb.append(" 		CONVERT(CHAR(5), S.START_DATE, 108) + ' - ' + CONVERT(CHAR(5), S.END_DATE, 108) AS TIME, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NOT NULL THEN 'N' ");
		sb.append(" 			WHEN S.START_DATE < getDate()+1 THEN 'N' ");
		sb.append(" 			ELSE 'Y' ");
		sb.append(" 		END AS EDITABLE, ");
		sb.append(" 		N.REVIEW_COMMENTS, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NOT NULL THEN NS.STATUS ");
		sb.append(" 			WHEN APPTS.SCHEDULED = 'N' THEN APPTS.STATUS ");
		sb.append(" 			ELSE ST.STATUS ");
		sb.append(" 		END AS STATUS, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NOT NULL THEN ");
		sb.append(" 				CASE ");
		sb.append(" 					WHEN NS.APPROVED = 'Y' THEN 'Y' ");
		sb.append(" 					WHEN NS.UNAPPROVED = 'Y' THEN 'Y' ");
		sb.append(" 					WHEN NS.FINAL = 'Y' THEN 'Y' ");
		sb.append(" 				ELSE 'N' END ");
		sb.append(" 			WHEN APPTS.COMPLETE = 'Y' THEN 'Y' ");
		sb.append(" 			ELSE 'N' ");
		sb.append(" 		END AS COMPLETE, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NULL AND RCA.EXPIRED = 'Y' THEN 'N' ");
		sb.append(" 		ELSE 'Y' END AS ACTIVE ");
		sb.append(" 	FROM ");
		sb.append(" 		PROJECT AS P ");
		sb.append(" 		JOIN REF_PROJECT_COMBOREVIEW AS RPC ON P.ID = RPC.PROJECT_ID AND RPC.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_COMBOREVIEW_REVIEW AS RCR ON RPC.COMBOREVIEW_ID = RCR.COMBOREVIEW_ID AND RCR.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_COMBOREVIEW_ACTION AS RCA ON RCR.ID = RCA.REF_COMBOREVIEW_REVIEW_ID AND RCA.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REVIEW AS R ON RCR.REVIEW_ID = R.ID ");
		sb.append(" 		JOIN LKUP_REVIEW_STATUS AS ST ON RCA.LKUP_REVIEW_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULE_INSPECTION = 'Y' ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON RCA.ID = S.REF_COMBOREVIEW_ACTION_ID AND S.ACTIVE = 'Y' AND S.START_DATE >= getDate() - 365 ");
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS APPTS ON S.LKUP_APPOINTMENT_STATUS_ID = APPTS.ID ");
		sb.append(" 		JOIN APPOINTMENT AS APPT ON S.APPOINTMENT_ID = APPT.ID AND APPT.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_PROJECT_USERS AS RPU ON P.ID = RPU.PROJECT_ID AND RPU.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_USERS AS PRU ON RPU.REF_USERS_ID = PRU.ID ");
		sb.append(" 		JOIN USERS AS PU ON PRU.USERS_ID = PU.ID AND LOWER(PU.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_COMBOREVIEW_ACTION AS N ");
		sb.append(" 			JOIN LKUP_REVIEW_STATUS AS NS ON N.LKUP_REVIEW_STATUS_ID = NS.ID ");
		sb.append(" 		) ON RCA.ID = N.PREVIOUS_ID AND N.ACTIVE = 'Y' ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		RCR.ID AS REF_COMBOREVIEW_REVIEW_ID, ");
		sb.append(" 		RCA.ID AS REF_COMBOREVIEW_ACTION_ID, ");
		sb.append(" 		P.ID AS PROJECT_ID, ");
		sb.append(" 		P.PROJECT_NBR, ");
		sb.append(" 		P.NAME AS PROJECT_NAME, ");
		sb.append(" 		-1 AS ACTIVITY_ID, ");
		sb.append(" 		'' AS ACT_NBR, ");
		sb.append(" 		'' AS ACTIVITY_TYPE, ");
		sb.append(" 		'project' AS REFERENCE_TYPE, ");
		sb.append(" 		P.ID AS REFERENCE_ID, ");
		sb.append(" 		P.PROJECT_NBR AS REFERENCE, ");
		sb.append(" 		R.NAME AS TYPE, ");
		sb.append(" 		APPT.SUBJECT, ");
		sb.append(" 		CAST(S.START_DATE AS DATE) as START_DATE, ");
		sb.append(" 		CAST(S.END_DATE AS DATE) as END_DATE, ");
		sb.append(" 		CONVERT(CHAR(5), S.START_DATE, 108) + ' - ' + CONVERT(CHAR(5), S.END_DATE, 108) AS TIME, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NOT NULL THEN 'N' ");
		sb.append(" 			WHEN S.START_DATE < getDate()+1 THEN 'N' ");
		sb.append(" 			ELSE 'Y' ");
		sb.append(" 		END AS EDITABLE, ");
		sb.append(" 		N.REVIEW_COMMENTS, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NOT NULL THEN NS.STATUS ");
		sb.append(" 			WHEN APPTS.SCHEDULED = 'N' THEN APPTS.STATUS ");
		sb.append(" 			ELSE ST.STATUS ");
		sb.append(" 		END AS STATUS, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NOT NULL THEN ");
		sb.append(" 				CASE ");
		sb.append(" 					WHEN NS.APPROVED = 'Y' THEN 'Y' ");
		sb.append(" 					WHEN NS.UNAPPROVED = 'Y' THEN 'Y' ");
		sb.append(" 					WHEN NS.FINAL = 'Y' THEN 'Y' ");
		sb.append(" 				ELSE 'N' END ");
		sb.append(" 			WHEN APPTS.COMPLETE = 'Y' THEN 'Y' ");
		sb.append(" 			ELSE 'N' ");
		sb.append(" 		END AS COMPLETE, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NS.ID IS NULL AND RCA.EXPIRED = 'Y' THEN 'N' ");
		sb.append(" 		ELSE 'Y' END AS ACTIVE ");
		sb.append(" 	FROM ");
		sb.append(" 		PROJECT AS P ");
		sb.append(" 		JOIN REF_PROJECT_COMBOREVIEW AS RPC ON P.ID = RPC.PROJECT_ID AND RPC.ACTIVE = 'Y' AND P.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_COMBOREVIEW_REVIEW AS RCR ON RPC.COMBOREVIEW_ID = RCR.COMBOREVIEW_ID AND RCR.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_COMBOREVIEW_ACTION AS RCA ON RCR.ID = RCA.REF_COMBOREVIEW_REVIEW_ID AND RCA.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REVIEW AS R ON RCR.REVIEW_ID = R.ID ");
		sb.append(" 		JOIN LKUP_REVIEW_STATUS AS ST ON RCA.LKUP_REVIEW_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULE_INSPECTION = 'Y' ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON RCA.ID = S.REF_COMBOREVIEW_ACTION_ID AND S.ACTIVE = 'Y' AND S.START_DATE >= getDate() - 365 ");
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS APPTS ON S.LKUP_APPOINTMENT_STATUS_ID = APPTS.ID ");

		sb.append(" 		JOIN APPOINTMENT AS APPT ON S.APPOINTMENT_ID = APPT.ID AND APPT.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_APPOINTMENT_USERS AS RU ON S.APPOINTMENT_ID = RU.APPOINTMENT_ID AND RU.ACTIVE = 'Y' ");
		sb.append(" 		JOIN USERS AS U ON RU.USERS_ID = U.ID AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_COMBOREVIEW_ACTION AS N ");
		sb.append(" 			JOIN LKUP_REVIEW_STATUS AS NS ON N.LKUP_REVIEW_STATUS_ID = NS.ID ");
		sb.append(" 		) ON RCA.ID = N.PREVIOUS_ID AND N.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String my3(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" 	ACR.ID AS REF_COMBOREVIEW_REVIEW_ID, ");
		sb.append(" 	ACA.ID AS REF_COMBOREVIEW_ACTION_ID, ");

		sb.append(" 	P.ID AS PROJECT_ID, ");
		sb.append(" 	P.PROJECT_NBR, ");
		sb.append(" 	P.NAME AS PROJECT_NAME, ");
		sb.append(" 	PT.TYPE AS PROJECT_TYPE, ");

		sb.append(" 	ACT.ID AS ACTIVITY_ID, ");
		sb.append(" 	ACT.ACT_NBR, ");
		sb.append(" 	AT.TYPE AS ACTIVITY_TYPE, ");

		sb.append(" 	'activity' AS REFERENCE_TYPE, ");
		sb.append(" 	ACT.ID AS REFERENCE_ID, ");
		sb.append(" 	ACT.ACT_NBR AS REFERENCE, ");

		sb.append(" 	R.NAME AS TYPE, ");
		sb.append(" 	A.SUBJECT SUBJECT, ");
		sb.append(" 	CONVERT(VARCHAR(10),S.START_DATE,101) as START_DATE, ");
		sb.append(" 	CONVERT(VARCHAR(10),S.END_DATE,101) as END_DATE, ");
		sb.append(" 	CONVERT(CHAR(5), S.START_DATE, 108) + ' - ' + CONVERT(CHAR(5), S.END_DATE, 108) AS TIME, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN NS.ID IS NOT NULL THEN 'N' ");
		sb.append(" 		WHEN S.START_DATE < getDate()+1 THEN 'N' ");
		sb.append(" 		ELSE 'Y' ");
		sb.append(" 	END AS EDITABLE, ");
		sb.append(" 	N.REVIEW_COMMENTS, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN NS.ID IS NOT NULL THEN NS.STATUS ");
		sb.append(" 		ELSE ST.STATUS ");
		sb.append(" 	END AS STATUS ");

		sb.append(" FROM ");

		sb.append(" 	USERS AS U ");
		sb.append(" 	JOIN REF_APPOINTMENT_USERS AS AU ON U.ID = AU.USERS_ID AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" 	JOIN APPOINTMENT AS A ON AU.APPOINTMENT_ID = A.ID AND A.ACTIVE = 'Y' ");
		sb.append(" 	JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");

		sb.append(" 	JOIN REF_COMBOREVIEW_ACTION AS ACA ON ACA.ID = S.REF_COMBOREVIEW_ACTION_ID AND ACA.ACTIVE = 'Y' ");
		sb.append(" 	JOIN LKUP_REVIEW_STATUS AS ST ON ACA.LKUP_REVIEW_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULE_INSPECTION = 'Y' ");
		sb.append(" 	JOIN REF_COMBOREVIEW_REVIEW AS ACR ON ACA.REF_COMBOREVIEW_REVIEW_ID = ACR.ID AND ACR.ACTIVE = 'Y' ");
		sb.append(" 	JOIN COMBOREVIEW AS CR ON ACR.COMBOREVIEW_ID = CR.ID AND CR.ACTIVE = 'Y' ");
		sb.append(" 	JOIN REVIEW AS R ON ACR.REVIEW_ID = R.ID ");

		sb.append(" 	LEFT OUTER JOIN ( ");
		sb.append(" 		REF_COMBOREVIEW_ACTION AS N ");
		sb.append(" 		JOIN LKUP_REVIEW_STATUS AS NS ON N.LKUP_REVIEW_STATUS_ID = NS.ID ");
		sb.append(" 	) ON ACA.ID = N.PREVIOUS_ID AND N.ACTIVE = 'Y' ");

		sb.append(" 	JOIN REF_ACT_COMBOREVIEW AS AC ON CR.ID = AC.COMBOREVIEW_ID AND AC.ACTIVE = 'Y' ");
		sb.append(" 	JOIN ACTIVITY AS ACT ON AC.ACTIVITY_ID = ACT.ID ");
		sb.append(" 	JOIN LKUP_ACT_TYPE AS AT ON ACT.LKUP_ACT_TYPE_ID = AT.ID ");
		sb.append(" 	JOIN PROJECT AS P ON ACT.PROJECT_ID = P.ID ");
		sb.append(" 	JOIN LKUP_PROJECT_TYPE AS PT ON P.LKUP_PROJECT_TYPE_ID = PT.ID ");

		sb.append(" UNION ");

		sb.append(" SELECT ");
		sb.append(" 	ACR.ID AS REF_COMBOREVIEW_REVIEW_ID, ");
		sb.append(" 	ACA.ID AS REF_COMBOREVIEW_ACTION_ID, ");

		sb.append(" 	P.ID AS PROJECT_ID, ");
		sb.append(" 	P.PROJECT_NBR, ");
		sb.append(" 	P.NAME AS PROJECT_NAME, ");
		sb.append(" 	PT.TYPE AS PROJECT_TYPE, ");

		sb.append(" 	null AS ACTIVITY_ID, ");
		sb.append(" 	null AS ACT_NBR, ");
		sb.append(" 	null AS ACTIVITY_TYPE, ");

		sb.append(" 	'project' AS REFERENCE_TYPE, ");
		sb.append(" 	P.ID AS REFERENCE_ID, ");
		sb.append(" 	P.PROJECT_NBR AS REFERENCE, ");

		sb.append(" 	R.NAME AS TYPE, ");
		sb.append(" 	A.SUBJECT SUBJECT, ");
		sb.append(" 	CONVERT(VARCHAR(10),S.START_DATE,101) as START_DATE, ");
		sb.append(" 	CONVERT(VARCHAR(10),S.END_DATE,101) as END_DATE, ");
		sb.append(" 	CONVERT(CHAR(5), S.START_DATE, 108) + ' - ' + CONVERT(CHAR(5), S.END_DATE, 108) AS TIME, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN NS.ID IS NOT NULL THEN 'N' ");
		sb.append(" 		WHEN S.START_DATE < getDate()+1 THEN 'N' ");
		sb.append(" 		ELSE 'Y' ");
		sb.append(" 	END AS EDITABLE, ");
		sb.append(" 	N.REVIEW_COMMENTS, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN NS.ID IS NOT NULL THEN NS.STATUS ");
		sb.append(" 		ELSE ST.STATUS ");
		sb.append(" 	END AS STATUS ");

		sb.append(" FROM ");

		sb.append(" 	USERS AS U ");
		sb.append(" 	JOIN REF_APPOINTMENT_USERS AS AU ON U.ID = AU.USERS_ID AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		sb.append(" 	JOIN APPOINTMENT AS A ON AU.APPOINTMENT_ID = A.ID AND A.ACTIVE = 'Y' ");
		sb.append(" 	JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");

		sb.append(" 	JOIN REF_COMBOREVIEW_ACTION AS ACA ON ACA.ID = S.REF_COMBOREVIEW_ACTION_ID AND ACA.ACTIVE = 'Y' ");
		sb.append(" 	JOIN LKUP_REVIEW_STATUS AS ST ON ACA.LKUP_REVIEW_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULE_INSPECTION = 'Y' ");
		sb.append(" 	JOIN REF_COMBOREVIEW_REVIEW AS ACR ON ACA.REF_COMBOREVIEW_REVIEW_ID = ACR.ID AND ACR.ACTIVE = 'Y' ");
		sb.append(" 	JOIN COMBOREVIEW AS CR ON ACR.COMBOREVIEW_ID = CR.ID AND CR.ACTIVE = 'Y' ");
		sb.append(" 	JOIN REVIEW AS R ON ACR.REVIEW_ID = R.ID ");

		sb.append(" 	LEFT OUTER JOIN ( ");
		sb.append(" 		REF_COMBOREVIEW_ACTION AS N ");
		sb.append(" 		JOIN LKUP_REVIEW_STATUS AS NS ON N.LKUP_REVIEW_STATUS_ID = NS.ID ");
		sb.append(" 	) ON ACA.ID = N.PREVIOUS_ID AND N.ACTIVE = 'Y' ");

		sb.append(" 	JOIN REF_PROJECT_COMBOREVIEW AS PC ON CR.ID = PC.COMBOREVIEW_ID AND PC.ACTIVE = 'Y' ");
		sb.append(" 	JOIN PROJECT AS P ON PC.PROJECT_ID = P.ID ");
		sb.append(" 	JOIN LKUP_PROJECT_TYPE AS PT ON P.LKUP_PROJECT_TYPE_ID = PT.ID ");

		sb.append(" ) ");
		sb.append(" SELECT * FROM Q ORDER BY START_DATE DESC ");
		return sb.toString();
	}

	public static String my2(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append("   SELECT ");
		/*sb.append("     C.ID AS COMBOREVIEW_ID ");
		sb.append("     , ");
		sb.append("     CRR.ID AS REF_COMBOREVIEW_REVIEW_ID ");
		sb.append("     , ");
		sb.append("     A.ID AS REF_COMBOREVIEW_ACTION_ID ");
		sb.append("     , ");
		sb.append("     APPT.ID AS APPOINTMENT_ID ");
		sb.append("     , ");
		sb.append("     SCHED.ID AS APPOINTMENT_SCHEDULE_ID ");
		sb.append("     , ");
		sb.append("     R.ID AS REVIEW_ID ");
		sb.append("     , ");
		sb.append("     APPTS.ID AS LKUP_APPOINTMENT_STATUS_ID ");
		sb.append("     , ");
		sb.append("     NXTST.ID AS NEXT_REVIEW_STATUS_ID ");*/
		sb.append(" 	CRR.ID AS REF_COMBOREVIEW_REVIEW_ID, M.ACT_NBR, LAT.TYPE TYPE, APPT.SUBJECT SUBJECT,  SCHED.START_DATE START_DATE, SCHED.END_DATE END_DATE,APPTS.STATUS STATUS, r.DESCRIPTION DESCRIPTION");
		sb.append("   FROM ");
		sb.append("     REF_COMBOREVIEW_ACTION AS A ");
		sb.append("     JOIN LKUP_REVIEW_STATUS AS S ON A.LKUP_REVIEW_STATUS_ID = S.ID AND A.ACTIVE = 'Y' AND S.ACTIVE = 'Y' AND S.SCHEDULE_INSPECTION = 'Y' ");
		sb.append("     JOIN REF_COMBOREVIEW_REVIEW AS CRR ON A.REF_COMBOREVIEW_REVIEW_ID = CRR.ID AND CRR.ACTIVE = 'Y' ");
		sb.append("     JOIN COMBOREVIEW AS C ON CRR.COMBOREVIEW_ID = C.ID AND C.ACTIVE = 'Y' ");
		sb.append("     JOIN REVIEW AS R ON CRR.REVIEW_ID = R.ID AND R.ACTIVE = 'Y' ");
		sb.append("     JOIN APPOINTMENT AS APPT ON CRR.ID = APPT.REF_COMBOREVIEW_REVIEW_ID AND APPT.ACTIVE = 'Y' ");
		sb.append("     JOIN APPOINTMENT_SCHEDULE AS SCHED ON APPT.ID = SCHED.APPOINTMENT_ID AND A.ID = SCHED.REF_COMBOREVIEW_ACTION_ID AND SCHED.PARENT_ID = -1 ");
		sb.append(" 	JOIN LKUP_APPOINTMENT_TYPE AS LAT ON APPT.LKUP_APPOINTMENT_TYPE_ID = LAT.ID AND LAT.ACTIVE = 'Y' ");
		sb.append("     JOIN LKUP_APPOINTMENT_STATUS AS APPTS ON SCHED.LKUP_APPOINTMENT_STATUS_ID = APPTS.ID AND APPTS.ACTIVE = 'Y' AND APPTS.SCHEDULED = 'Y' ");
		sb.append("		JOIN REF_ACT_APPOINTMENT RAA on APPT.ID = RAA.APPOINTMENT_ID ");
		sb.append("		JOIN ACTIVITY M on RAA.ACTIVITY_ID = M.ID ");
		sb.append("     JOIN REF_APPOINTMENT_USERS AS AU ON APPT.ID = AU.APPOINTMENT_ID AND AU.ACTIVE = 'Y' ");
		sb.append("     JOIN USERS AS U ON AU.USERS_ID = U.ID ");
		sb.append("       AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') "); 
		sb.append("     LEFT OUTER JOIN ( ");
		sb.append("       REF_COMBOREVIEW_ACTION AS NXT ");
		sb.append("       JOIN LKUP_REVIEW_STATUS AS NXTST ON NXT.LKUP_REVIEW_STATUS_ID = NXTST.ID AND NXTST.ACTIVE = 'Y' ");
		sb.append("     )  ON A.ID = NXT.PREVIOUS_ID AND NXT.ACTIVE = 'Y' ");
		sb.append(" ) SELECT * FROM Q ");
		return sb.toString();
	}

	public static String my1(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append("   SELECT ");
		sb.append("     S.ID ");
		sb.append("     , ");
		sb.append("     S.ID AS SCHEDULE_ID");
		sb.append("     , ");
		sb.append("     A.ID AS APPOINTMENT_ID ");
		sb.append("     , ");
		sb.append("     (RG.ID * -1) AS TYPE ");
		sb.append("     , ");
		sb.append("     RG.GROUP_NAME AS TYPE_TEXT ");
		sb.append("     , ");
		sb.append("     RV.ID AS SUBJECT ");
		sb.append("     , ");
		sb.append("     RV.NAME AS SUBJECT_TEXT ");
		sb.append("     , ");
		sb.append("     S.START_DATE ");
		sb.append("     , ");
		sb.append("     S.END_DATE ");
		sb.append("     , ");
		sb.append("     ST.STATUS ");

		sb.append("   FROM ");
		sb.append("     APPOINTMENT AS A ");
		sb.append("     JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");
		sb.append("     JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULED = 'Y' ");


		sb.append("     JOIN REVIEW AS RV ON A.REVIEW_ID = RV.ID ");
		sb.append("     JOIN REVIEW_GROUP AS RG ON RV.REVIEW_GROUP_ID = RG.ID AND RG.ACTIVE = 'Y' AND RG.INSPECTION = 'Y' ");
		sb.append("     JOIN ( ");
		sb.append("       USERS AS U ");
		sb.append("       LEFT OUTER JOIN REF_APPOINTMENT_USERS AS RAU ON U.ID = RAU.USERS_ID ");
		sb.append("     ) ON (A.CREATED_BY = U.ID OR S.CREATED_BY = U.ID OR RAU.APPOINTMENT_ID = A.ID) AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");

		sb.append("   WHERE ");
		sb.append("     (S.START_DATE > getDate() OR S.END_DATE > getDate()) ");
		sb.append(" ) ");

		sb.append(" SELECT DISTINCT ");
		sb.append("   Q.ID ");
		sb.append("   , ");
		sb.append("   Q.ID AS SCHEDULE_ID");
		sb.append("   , ");
		sb.append("   Q.ID AS APPOINTMENT_ID ");
		sb.append("   , ");
		sb.append("   A.ID AS ACTIVITY_ID ");
		sb.append("   , ");
		sb.append("   A.ACT_NBR ");
		sb.append("   , ");
		sb.append("   T.TYPE AS ACT_TYPE ");
		sb.append("   , ");
		sb.append("   P.ID AS PROJECT_ID ");
		sb.append("   , ");
		sb.append("   LSO.STR_NO ");
		sb.append("   , ");
		sb.append("   LSO.STR_MOD ");
		sb.append("   , ");
		sb.append("   STR.PRE_DIR ");
		sb.append("   , ");
		sb.append("   STR.STR_NAME ");
		sb.append("   , ");
		sb.append("   STR.STR_TYPE ");
		sb.append("   , ");
		sb.append("   LSO.UNIT ");
		sb.append("   , ");

		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_NO IS NOT NULL THEN CONVERT(varchar(10), LSO.STR_NO) ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_MOD IS NOT NULL AND LSO.STR_MOD <> '' THEN ' ' + LSO.STR_MOD ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN STR.PRE_DIR IS NOT NULL AND STR.PRE_DIR <> '' THEN ' ' + STR.PRE_DIR ");
		sb.append("         ELSE '' ");
		sb.append("       END + ");
		sb.append("       CASE  ");
		sb.append("         WHEN STR.STR_NAME IS NOT NULL AND STR.STR_NAME <> '' THEN ' ' + STR.STR_NAME ");
		sb.append("         ELSE '' ");
		sb.append("       END + ");
		sb.append("       CASE  ");
		sb.append("         WHEN STR.STR_TYPE IS NOT NULL AND STR.STR_TYPE <> '' THEN ' ' + STR.STR_TYPE ");
		sb.append("         ELSE '' ");
		sb.append("       END + ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.UNIT IS NOT NULL AND LSO.UNIT <> '' THEN ' ' + LSO.UNIT ");
		sb.append("         ELSE '' ");
		sb.append("       END ");
		sb.append("     )) AS ADDRESS ");
		sb.append("   , ");


		sb.append("   Q.TYPE ");
		sb.append("   , ");
		sb.append("   Q.TYPE_TEXT ");
		sb.append("   , ");
		sb.append("   Q.SUBJECT ");
		sb.append("   , ");
		sb.append("   Q.SUBJECT_TEXT ");
		sb.append("   , ");
		sb.append("   Q.START_DATE ");
		sb.append("   , ");
		sb.append("   Q.END_DATE ");
		sb.append(" FROM ");
		sb.append("   Q ");
		sb.append("   LEFT OUTER JOIN ( ");
		sb.append("     REF_ACT_APPOINTMENT ");
		sb.append("     JOIN ACTIVITY AS A ON REF_ACT_APPOINTMENT.ACTIVITY_ID = A.ID AND A.ACTIVE = 'Y' ");
		sb.append("     JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append("     JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append("     LEFT OUTER JOIN ( ");
		sb.append("       REF_LSO_PROJECT AS RLSO ");
		sb.append("       JOIN LSO ON RLSO.LSO_ID = LSO.ID AND LSO.ACTIVE = 'Y' ");
		sb.append("       JOIN LSO_STREET AS STR ON LSO.LSO_STREET_ID = STR.ID AND STR.ACTIVE = 'Y' ");
		sb.append("     ) ON P.ID = RLSO.PROJECT_ID AND RLSO.ACTIVE = 'Y' ");
		sb.append("   )  ON Q.ID = REF_ACT_APPOINTMENT.APPOINTMENT_ID ");
		sb.append(" ORDER BY Q.START_DATE DESC, Q.END_DATE DESC ");

//		sb.append(" LEFT OUTER JOIN ( ");
//		sb.append("     REF_PROJECT_APPOINTMENT ON Q.ID = REF_PROJECT_APPOINTMENT.APPOINTMENT_ID ");
//		sb.append("     JOIN PROJECT AS P ON REF_PROJECT_APPOINTMENT.PROJECT_ID = P.ID ");
//		sb.append(" ) ");


		return sb.toString();
	}

	public static String full1(int userid, Timekeeper start, Timekeeper end) {
		start.setHour(0);
		start.setMinute(0);
		start.setSecond(0);

		Timekeeper s = new Timekeeper(start.DATECODE());
		s.setHour(23);
		s.setMinute(59);
		s.setSecond(59);

		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" ) ");
		return sb.toString();

	}



	public static String full(String userid, Timekeeper start, Timekeeper end) {
		start.setHour(0);
		start.setMinute(0);
		start.setSecond(0);

		Timekeeper s = new Timekeeper(start.DATECODE());
		s.setHour(23);
		s.setMinute(59);
		s.setSecond(59);

		int id = Operator.toInt(userid);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" R.AVAILABILITY_ID ");
		sb.append(" , ");
		sb.append(" S.ID ");
		sb.append(" , ");
		sb.append(" S.ID AS SCHEDULE_ID ");
		sb.append(" , ");
		sb.append(" CRR.ID AS REF_COMBOREVIEW_REVIEW_ID ");
		sb.append(" , ");
		sb.append(" CRA.ID AS REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME ");
		sb.append(" , ");
		sb.append(" R.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" S.START_DATE ");
		sb.append(" , ");
		sb.append(" S.END_DATE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN PRS.ID IS NOT NULL THEN PRS.STATUS ");
		sb.append("   ELSE CRS.STATUS END AS STATUS ");
		sb.append(" , ");
		sb.append(" ACT.ACT_NBR AS ACTIVITY ");
		sb.append(" , ");
		sb.append(" P.PROJECT_NBR AS PROJECT ");
		sb.append(" , ");
		sb.append(" U.ID AS USERS_ID ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN ACT.ID IS NULL THEN PT.TYPE ");
		sb.append("   ELSE AT.DESCRIPTION END AS TYPE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN PRS.ID IS NOT NULL THEN PRS.APPROVED ");
		sb.append("   ELSE CRS.APPROVED END AS APPROVED ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN PRS.ID IS NOT NULL THEN PRS.UNAPPROVED ");
		sb.append("   ELSE CRS.UNAPPROVED END AS UNAPPROVED ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN PRS.ID IS NOT NULL THEN PRS.SCHEDULE ");
		sb.append("   ELSE CRS.SCHEDULE END AS SCHEDULE ");
		sb.append(" , ");

		sb.append("     LTRIM(RTRIM( ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_NO IS NOT NULL THEN CAST(LSO.STR_NO AS VARCHAR(10)) ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_NO IS NOT NULL AND ST.PRE_DIR IS NOT NULL AND ST.PRE_DIR <> '' THEN ' ' + ST.PRE_DIR ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_NO IS NOT NULL THEN ' ' + ST.STR_NAME ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_NO IS NOT NULL AND ST.STR_TYPE IS NOT NULL AND ST.STR_TYPE <> '' THEN ' ' + ST.STR_TYPE ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_NO IS NOT NULL AND ST.SUF_DIR IS NOT NULL AND ST.SUF_DIR <> '' THEN ' ' + ST.SUF_DIR ");
		sb.append("         ELSE ''  ");
		sb.append("       END +  ");
		sb.append("       CASE  ");
		sb.append("         WHEN LSO.STR_NO IS NOT NULL AND LSO.UNIT IS NOT NULL AND LSO.UNIT <> '' THEN ' ' + LSO.UNIT ");
		sb.append("         ELSE ''  ");
		sb.append("       END ");
		sb.append("     )) AS ADDRESS ");

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
		sb.append("       END ");
		sb.append("     )) AS INSPECTOR ");

		sb.append(" FROM ");
		sb.append(" APPOINTMENT AS A ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID ");
		sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS APS ON S.LKUP_APPOINTMENT_STATUS_ID = APS.ID AND APS.SCHEDULED = 'Y' ");

		sb.append(" JOIN REF_COMBOREVIEW_ACTION AS CRA ON CRA.ID = S.REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS CRR ON CRA.REF_COMBOREVIEW_REVIEW_ID = CRR.ID ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS CRS ON CRA.LKUP_REVIEW_STATUS_ID = CRS.ID ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   REF_COMBOREVIEW_ACTION AS PRA ");
		sb.append("   JOIN LKUP_REVIEW_STATUS AS PRS ON PRA.LKUP_REVIEW_STATUS_ID = PRS.ID ");
		sb.append(" ) ON PRA.PREVIOUS_ID = CRA.ID AND PRA.ACTIVE = 'Y' ");

		sb.append(" JOIN REVIEW AS R ON CRR.REVIEW_ID = R.ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN REVIEW_GROUP AS G ON R.REVIEW_GROUP_ID = G.ID AND G.ACTIVE = 'Y' ");

		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   REF_COMBOREVIEW_TEAM AS RAT ");
		sb.append("   JOIN REF_TEAM AS RT ON RAT.REF_TEAM_ID = RT.ID AND RT.ACTIVE = 'Y' AND RAT.ACTIVE = 'Y' ");
		sb.append("   JOIN LKUP_TEAM_TYPE AS UT ON RT.LKUP_TEAM_TYPE_ID = UT.ID AND UT.ACTIVE = 'Y' AND LOWER(UT.TYPE) = 'inspector' ");
		sb.append("   JOIN USERS AS U ON RT.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
		sb.append(" ) ON CRR.ID = RAT.REF_COMBOREVIEW_REVIEW_ID ");

		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   REF_ACT_COMBOREVIEW AS RA ");
		sb.append("   JOIN ACTIVITY AS ACT ON RA.ACTIVITY_ID = ACT.ID AND ACT.ACTIVE = 'Y' ");
		sb.append("   JOIN LKUP_ACT_TYPE AS AT ON ACT.LKUP_ACT_TYPE_ID = AT.ID ");
		sb.append(" ) ON RA.COMBOREVIEW_ID = CRR.COMBOREVIEW_ID ");

		sb.append(" LEFT OUTER JOIN REF_PROJECT_COMBOREVIEW AS RP ON RP.COMBOREVIEW_ID = CRR.COMBOREVIEW_ID AND RP.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN PROJECT AS P ON P.ACTIVE = 'Y' AND (RP.PROJECT_ID = P.ID OR ACT.PROJECT_ID = P.ID) ");
		sb.append(" LEFT OUTER JOIN LKUP_PROJECT_TYPE AS PT ON P.LKUP_PROJECT_TYPE_ID = PT.ID ");

		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   REF_LSO_PROJECT AS RLSO ");
		sb.append("   JOIN LSO ON RLSO.LSO_ID = LSO.ID ");
		sb.append("   JOIN LSO_STREET AS ST ON LSO.LSO_STREET_ID = ST.ID ");
		sb.append(" ) ON RLSO.PROJECT_ID = P.ID AND RLSO.ACTIVE = 'Y' ");

		sb.append(" WHERE ");

		sb.append(" ( ");
		sb.append("   ( ");
		sb.append("     S.START_DATE >= ").append(start.sqlDatetime());
		sb.append("     AND ");
		sb.append("     S.START_DATE <= ").append(s.sqlDatetime());
		sb.append("   ) ");
		sb.append("   OR ");
		sb.append("   ( ");
		sb.append("     S.START_DATE <= ").append(s.sqlDatetime());
		sb.append("     AND ");
		sb.append("     APS.COMPLETE = 'N' ");
		sb.append("   ) ");
		sb.append(" ) ");

		if (id > 0) {
			sb.append(" AND ");
			sb.append(" (U.ID = ").append(id).append(" ) ");
		}

		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(" Q ");
		sb.append(" ORDER BY INSPECTOR, START_DATE ASC ");


		return sb.toString();
	}


	public static String getInspectionTypes(String type, int typeid) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String maintable = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" R.ID ");
		sb.append(" , ");
		sb.append(" CAST(R.ID AS VARCHAR(10)) AS VALUE ");
		sb.append(" , ");
		sb.append(" R.NAME AS TEXT ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME + ': ' + R.NAME AS DESCRIPTION ");
		sb.append(" , ");
		sb.append(" S.REQUIRE_ISSUED ");
		sb.append(" , ");
		sb.append(" MS.ISSUED ");
		sb.append(" , ");
		sb.append(" CASE WHEN REQUIRE_ISSUED = 'N' THEN 'Y' WHEN MS.ISSUED = 'Y' THEN 'Y' WHEN REQUIRE_ISSUED = 'N' THEN 'Y' ELSE 'N' END AS VALID_ISSUED ");
		sb.append(" , ");
		sb.append(" V.ID AS LKUP_VOIP_MENU_ID ");
		sb.append(" , ");
		sb.append(" V.PRESS ");
		sb.append(" , ");
		sb.append(" V.DESCRIPTION AS VOIP_DESCRIPTION ");
		sb.append(" , ");
		sb.append(" V.LOGIC ");
		sb.append(" , ");
		sb.append(" V.HINT ");
		sb.append(" , ");
		sb.append(" V.SAY_DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(maintable).append(" AS M ");
		sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON M.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
		sb.append(" JOIN LKUP_").append(tableref).append("_STATUS AS MS ON M.LKUP_").append(tableref).append("_STATUS_ID = MS.ID ");
		sb.append(" JOIN REF_").append(tableref).append("_REVIEW_GROUP AS REF ON T.ID = REF.LKUP_").append(tableref).append("_TYPE_ID AND REF.ACTIVE = 'Y' ");
		sb.append(" JOIN REVIEW_GROUP AS G ON REF.REVIEW_GROUP_ID = G.ID AND G.ACTIVE = 'Y' ");
		sb.append(" JOIN REVIEW AS R ON G.ID = R.REVIEW_GROUP_ID AND R.AVAILABILITY_ID > 0 AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON R.LKUP_REVIEW_TYPE_ID = S.LKUP_REVIEW_TYPE_ID AND S.SCHEDULE_INSPECTION = 'Y' AND S.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   REF_VOIP_REVIEW AS RV ");
		sb.append("   JOIN LKUP_VOIP_MENU AS V ON RV.LKUP_VOIP_MENU_ID = V.ID AND V.ACTIVE = 'Y' AND RV.ACTIVE = 'Y' ");
		sb.append(" ) ON R.ID = RV.REVIEW_ID ");
		sb.append(" WHERE ");
		sb.append(" G.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" R.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" M.ID = ").append(typeid);
		sb.append(" ) ");
		sb.append(" SELECT * FROM Q WHERE VALID_ISSUED = 'Y' ");
		return sb.toString();
	}


	public static String getInspectionTypes(String actnbr) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.ID ");
		sb.append(" , ");
		sb.append(" CAST(R.ID AS VARCHAR(10)) AS VALUE ");
		sb.append(" , ");
		sb.append(" R.NAME AS TEXT ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME + ': ' + R.NAME AS DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" ACTIVITY AS A ");
		sb.append(" JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID ");
		sb.append(" JOIN REF_ACT_REVIEW_GROUP AS REF ON T.ID = REF.LKUP_ACT_TYPE_ID ");
		sb.append(" JOIN REVIEW_GROUP AS G ON REF.REVIEW_GROUP_ID = G.ID ");
		sb.append(" JOIN REVIEW AS R ON G.ID = R.REVIEW_GROUP_ID ");
		sb.append(" JOIN LKUP_REVIEW_TYPE AS RT ON R.LKUP_REVIEW_TYPE_ID = RT.ID ");
		sb.append(" WHERE ");
		sb.append(" LOWER(RT.DESCRIPTION) = 'inspection' ");
		sb.append(" AND ");
		sb.append(" G.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" R.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" A.ACT_NBR = '").append(Operator.sqlEscape(actnbr)).append("' ");
		return sb.toString();
	}

	public static String team(String reviewids) {
		String[] arr = Operator.split(reviewids, "|");
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" T.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" LTRIM(RTRIM( ");
		sb.append("     CASE  ");
		sb.append("       WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("       ELSE ''  ");
		sb.append("     END +  ");
		sb.append("     CASE  ");
		sb.append("       WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("       ELSE ''  ");
		sb.append("     END +  ");
		sb.append("     CASE  ");
		sb.append("       WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("       ELSE '' ");
//		sb.append("     END + ");
//		sb.append("     CASE  ");
//		sb.append("       WHEN LT.TYPE IS NOT NULL AND LT.TYPE <> '' THEN ' (' + LT.TYPE + ') ' ");
//		sb.append("       ELSE '' ");
		sb.append("     END ");
		sb.append(" )) AS TEXT ");
		sb.append(" , ");
		sb.append(" LT.TYPE AS DESCRIPTION");
		sb.append(" FROM ");
		sb.append(" REVIEW AS R ");
		sb.append(" JOIN REF_REVIEW_TEAM AS RT ON R.ID = RT.REVIEW_ID AND RT.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_TEAM_TYPE AS LT ON RT.LKUP_TEAM_TYPE_ID = LT.ID AND LT.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_TEAM AS T ON LT.ID = T.LKUP_TEAM_TYPE_ID AND T.ACTIVE = 'Y' ");
		sb.append(" JOIN USERS AS U ON T.USERS_ID = U.ID AND U.ACTIVE='Y' ");
		sb.append(" WHERE ");
		sb.append(" R.ID IN ( ");
		boolean empty = true;
		for (int i=0; i<arr.length; i++) {
			int id = Operator.toInt(arr[i]);
			if (id > 0) {
				if (!empty) { sb.append(","); }
				sb.append(id);
				empty = false;
			}
		}
		sb.append(" ) ");
		return sb.toString();
	}

	// Returns inspectable activities
	public static String getActivity(int actid) {
		return getActivities(-1, "", "", "", actid, "");
	}

	public static String getActivity(String actnbr) {
		return getActivities(-1, "", "", "", -1, actnbr);
	}

	public static String getActivities(int userid) {
		return getActivities(userid, "", "", "", -1, "");
	}

	public static String getActivity(int userid, int actid) {
		return getActivities(userid, "", "", "", actid, "");
	}

	public static String getActivity(int userid, String actnbr) {
		return getActivities(userid, "", "", "", -1, actnbr);
	}


	public static String getActivities(String username) {
		return getActivities(-1, username, "", "", -1, "");
	}

	public static String getActivity(String username, int actid) {
		return getActivities(-1, username, "", "", actid, "");
	}

	public static String getActivity(String username, String actnbr) {
		return getActivities(-1, username, "", "", -1, actnbr);
	}


	public static String getActivitiesByEmail(String email) {
		return getActivities(-1, "", email, "", -1, "");
	}

	public static String getActivitiesByPhone(String phone) {
		return getActivities(-1, "", "", phone, -1, "");
	}



	public static String getActivities(int userid, String username, String email, String phonecell, int actid, String actnbr) {
		if (userid < 1 && !Operator.hasValue(username) && !Operator.hasValue(email) && !Operator.hasValue(phonecell) && actid < 1 && !Operator.hasValue(actnbr)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append("   SELECT DISTINCT ");
		sb.append("     A.ID ");
		sb.append("     , ");
		sb.append("     A.ID AS ACTIVITY_ID ");
		sb.append("     , ");
		sb.append("     A.ID AS VALUE ");
		sb.append("     , ");
		sb.append("     A.ACT_NBR ");
		sb.append("     , ");
		sb.append("     A.ACT_NBR AS TEXT ");
		sb.append("     , ");
		sb.append("     A.ISSUED_DATE ");
		sb.append("     , ");
		sb.append("     AST.ISSUED ");
		sb.append("     , ");
		sb.append("     CASE ");
		sb.append("       WHEN S.REQUIRE_ISSUED = 'N' THEN 'Y' ");
		sb.append("       WHEN AST.ISSUED = 'Y' THEN 'Y' ");
		sb.append("       ELSE 'N' END AS VALID_ISSUED ");
		sb.append("     , ");
		sb.append("     S.REQUIRE_PAID ");
		sb.append("     , ");
		sb.append("     SUM(STMT.BALANCE_DUE) AS BALANCE_DUE ");
		sb.append("   FROM ");
		sb.append("     ACTIVITY AS A ");
		sb.append("     JOIN LKUP_ACT_STATUS AS AST ON A.LKUP_ACT_STATUS_ID = AST.ID AND AST.ACTIVE = 'Y' AND AST.LIVE = 'Y' AND A.ACTIVE = 'Y' ");
		if (actid > 0) {
			sb.append(" AND A.ID = ").append(actid);
		}
		else if (Operator.hasValue(actnbr)) {
			sb.append(" AND LOWER(A.ACT_NBR) = LOWER('").append(Operator.sqlEscape(actnbr)).append("') ");
		}

		sb.append("     JOIN REF_ACT_REVIEW_GROUP AS RRG ON A.LKUP_ACT_TYPE_ID = RRG.LKUP_ACT_TYPE_ID AND RRG.ACTIVE = 'Y' ");
		sb.append("     JOIN REVIEW_GROUP AS G ON RRG.REVIEW_GROUP_ID = G.ID AND G.ACTIVE = 'Y' ");
		sb.append("     JOIN REVIEW AS R ON G.ID = R.REVIEW_GROUP_ID AND R.ACTIVE = 'Y' ");
		sb.append("     JOIN LKUP_REVIEW_STATUS AS S ON R.LKUP_REVIEW_TYPE_ID = S.LKUP_REVIEW_TYPE_ID AND S.SCHEDULE_INSPECTION = 'Y' ");
		sb.append("     JOIN REF_ACT_USERS AS RAU ON A.ID = RAU.ACTIVITY_ID AND RAU.ACTIVE = 'Y' ");
		sb.append("     JOIN REF_USERS AS RU ON RAU.REF_USERS_ID = RU.ID AND RU.ACTIVE = 'Y' ");
		sb.append("     JOIN USERS AS U ON RU.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");

		if (userid > 0) {
			sb.append("     AND U.ID = ").append(userid);
		}
		else if (Operator.hasValue(username)) {
			sb.append("     AND LOWER(U.USERNAME) = LOWER('").append(Operator.sqlEscape(username)).append("') ");
		}
		else if (Operator.hasValue(email)) {
			sb.append("     AND LOWER(U.EMAIL) = LOWER('").append(Operator.sqlEscape(email)).append("') ");
		}
		else if (Operator.hasValue(phonecell)) {
			sb.append("     JOIN PEOPLE AS P ON U.ID = P.USERS_ID AND P.ACTIVE = 'Y' AND LOWER(P.PHONE_CELL) = LOWER('").append(Operator.sqlEscape(phonecell)).append("') ");
		}

		sb.append("     LEFT OUTER JOIN ( ");
		sb.append("         REF_ACT_STATEMENT AS RSTMT ");
		sb.append("         JOIN STATEMENT AS STMT ON RSTMT.STATEMENT_ID = STMT.ID AND RSTMT.ACTIVE = 'Y' AND STMT.ACTIVE = 'Y' ");
		sb.append("     ) ON RSTMT.ACTIVITY_ID = A.ID ");
		sb.append("     GROUP BY ");
		sb.append("     A.ID ");
		sb.append("     , ");
		sb.append("     A.ACT_NBR ");
		sb.append("     , ");
		sb.append("     A.ISSUED_DATE ");
		sb.append("     , ");
		sb.append("     AST.ISSUED ");
		sb.append("     , ");
		sb.append("     S.REQUIRE_ISSUED ");
		sb.append("     , ");
		sb.append("     S.REQUIRE_PAID ");
		sb.append(" ) ");

		sb.append(" , Q1 AS ( ");
		sb.append("   SELECT DISTINCT ");
		sb.append("     Q.ID ");
		sb.append("     , ");
		sb.append("     Q.VALUE ");
		sb.append("     , ");
		sb.append("     Q.TEXT ");
		sb.append("     , ");
		sb.append("     Q.ISSUED_DATE ");
		sb.append("   FROM Q WHERE VALID_ISSUED = 'Y' AND REQUIRE_PAID = 'N' ");

		sb.append("   UNION ");

		sb.append("   SELECT DISTINCT ");
		sb.append("     Q.ID ");
		sb.append("     , ");
		sb.append("     Q.VALUE ");
		sb.append("     , ");
		sb.append("     Q.TEXT ");
		sb.append("     , ");
		sb.append("     Q.ISSUED_DATE ");
		sb.append("   FROM Q WHERE VALID_ISSUED = 'Y' AND REQUIRE_PAID = 'Y' AND BALANCE_DUE <= 0 ");
		sb.append(" ) ");

		sb.append(" SELECT ");
		sb.append("   ID ");
		sb.append("   , ");
		sb.append("   VALUE ");
		sb.append("   , ");
		sb.append("   TEXT ");
		sb.append(" FROM ");
		sb.append(" Q1 ");
		sb.append(" ORDER BY Q1.ISSUED_DATE DESC ");
		return sb.toString();
	}



	public static String statisticsDay(String additional){
		StringBuilder sb = new StringBuilder();
		if(Operator.hasValue(additional)){
		sb.append(" WITH Q AS ( ");
		sb.append("  ");
		sb.append(" SELECT DISTINCT  ");
		sb.append("  ");
		sb.append("                                 CR.ID AS COMBOREVIEW_ID ");
		sb.append("                                 , ");
		sb.append("                                 CR.TYPE AS REF ");
		sb.append("                                 , ");
		sb.append("                                 A.ACT_NBR ");
		sb.append("                                 , ");
		sb.append("                                 LAT.TYPE AS ACTIVITYTYPE ");
		sb.append("                                  ");
		sb.append("                                 , ");
		sb.append("                                 P.PROJECT_NBR ");
		sb.append("                                 , ");
		sb.append("                                 A.ID AS ACTIVITY_ID ");
		sb.append("                                 , ");
		sb.append("                                 P.ID AS PROJECT_ID ");
		sb.append("                                 , ");
		sb.append("                                 L.ID AS LSO_ID ");
		sb.append("                                 , ");
		sb.append("                                 CRR.ID AS REF_COMBOREVIEW_REVIEW_ID ");
		sb.append("                                 , ");
		sb.append("                                 CRA.ID AS REF_COMBOREVIEW_ACTION_ID ");
		sb.append("                                 , ");
		sb.append("                                 NCRA.ID AS NEXT_ID ");
		sb.append("                                 , ");
		sb.append("                                 CRA.PREVIOUS_ID ");
		sb.append("                                 , ");
		sb.append("                                 R.ID AS REVIEW_ID ");
		sb.append("                                 , ");
		sb.append("                                 G.ID AS REVIEW_GROUP_ID ");
		sb.append("                                 , ");
		sb.append("  ");
		sb.append("  ");
		sb.append("  ");
		sb.append("                                 CASE WHEN NS.ID IS NOT NULL THEN NS.ID ELSE S.ID END AS LKUP_REVIEW_STATUS_ID ");
		sb.append("                                 , ");
		sb.append("                                 CASE WHEN NS.ID IS NOT NULL THEN NS.STATUS ELSE S.STATUS END AS STATUS ");
		sb.append("                                 , ");
		sb.append("                                 CASE WHEN NS.ID IS NOT NULL THEN NS.APPROVED ELSE S.APPROVED END AS APPROVED ");
		sb.append("                                 , ");
		sb.append("                                 CASE WHEN NS.ID IS NOT NULL THEN NS.UNAPPROVED ELSE S.UNAPPROVED END AS UNAPPROVED ");
		sb.append("                                 , ");
		sb.append("                                 CASE WHEN NS.ID IS NOT NULL THEN NS.FINAL ELSE S.FINAL END AS FINAL ");
		sb.append("                                 , ");
		sb.append("                                 CASE WHEN NS.ID IS NOT NULL THEN 'Y' ELSE 'N' END AS COMPLETE ");
		sb.append("  ");
		sb.append("  ");
		sb.append("  ");
		sb.append("  ");
		sb.append("                                 , ");
		sb.append("                                 APPT.ID AS APPOINTMENT_TID ");
		sb.append("                                 , ");
		sb.append("                                 SCHED.ID AS APPOINTMENT_SCHEDULE_ID ");
		sb.append("                                 , ");
		sb.append("                                 CR.START_DATE AS REVIEW_START_DATE ");
		sb.append("                                 , ");
		sb.append("                                 G.GROUP_NAME AS REVIEW_GROUP_NAME ");
		sb.append("                                 , ");
		sb.append("                                 R.NAME AS REVIEW ");
		sb.append("                                 , ");
		sb.append("                                 CR.TITLE AS COMBOREVIEW_TITLE ");
		sb.append("                                 , ");
		sb.append("                                 CR.EXPEDITED ");
		sb.append("                                 , ");
		sb.append("                                 R.AVAILABILITY_ID ");
		sb.append("                                 , ");
		sb.append("                                 SCHED.START_DATE ");
		sb.append("                                 , ");
		sb.append("                                 SCHED.END_DATE ");
		sb.append("                                 , ");
		sb.append("                                 CONVERT(date, SCHED.START_DATE) AS DATE_START ");
		sb.append("                                 , ");
		sb.append("                                 CAST(SCHED.START_DATE as time) AS TIME_START ");
		sb.append("                                 , ");
		sb.append("                                 CONVERT(date, SCHED.END_DATE) AS DATE_END ");
		sb.append("                                 , ");
		sb.append("                                 CAST(SCHED.END_DATE as time) AS TIME_END ");
		sb.append("                                 , ");
		sb.append("                                 DATEPART ( weekday , SCHED.START_DATE ) AS START_DAY ");
		sb.append("                                 , ");
		sb.append("                                 DATEPART ( weekday , SCHED.END_DATE ) AS END_DAY ");
		sb.append("                                , ");
		sb.append("                                 U.FIRST_NAME +' '+U.LAST_NAME AS INSPECTOR ");
		sb.append("                                 , ");
		sb.append("                                 U.MI AS ASSIGNED_MI ");
		sb.append("                                 , ");
		sb.append("                                 U.FIRST_NAME AS ASSIGNED_FIRST_NAME ");
		sb.append("                                 , ");
		sb.append("                                 U.ID AS ASSIGNED_USER_ID ");
		sb.append("                                 , ");
		sb.append("                                 U.USERNAME AS ASSIGNED_USERNAME ");
		sb.append("                                 , ");
		sb.append("                                 TM.ID AS TEAM_ID ");
		sb.append("                                 , ");
		sb.append("                                 CASE WHEN U.USERNAME IS NULL THEN 'UNASSIGNED' ELSE  U.USERNAME END AS ASSIGNED ");
		sb.append("                                 , ");
		sb.append("                                 LTT.TYPE AS TEAM_TYPE ");
		sb.append("                                 , ");
		sb.append("                                 LG.LATITUDE ");
		sb.append("                                 , ");
		sb.append("                                 LG.LONGITUDE ");
		sb.append("                                 , ");
		sb.append("                                 RCRR.ORDR ");
		sb.append("                                 , ");
		sb.append("                                 RTM.REF_TEAM_ID ");
		sb.append("  ");
		sb.append("  ");
		sb.append("                 FROM ");
		sb.append("  ");
		sb.append("                                 COMBOREVIEW AS CR ");
		sb.append("  ");
		sb.append("                                 JOIN REF_COMBOREVIEW_REVIEW AS CRR ON CR.ID = CRR.COMBOREVIEW_ID AND CR.ACTIVE = 'Y' AND CRR.ACTIVE = 'Y' ");
		sb.append("                                 JOIN REVIEW AS R ON CRR.REVIEW_ID = R.ID AND R.ACTIVE = 'Y' ");
		sb.append("                                 JOIN REVIEW_GROUP AS G ON R.REVIEW_GROUP_ID = G.ID AND G.ACTIVE = 'Y' ");
		sb.append("  ");
		sb.append("                                 JOIN REF_COMBOREVIEW_ACTION AS CRA ON CRA.REF_COMBOREVIEW_REVIEW_ID = CRR.ID ");
		sb.append("                                 JOIN LKUP_REVIEW_STATUS AS S ON CRA.LKUP_REVIEW_STATUS_ID = S.ID AND S.SCHEDULE_INSPECTION = 'Y' ");
		sb.append("                                  ");
		sb.append("  ");
		sb.append("                                 JOIN APPOINTMENT_SCHEDULE AS SCHED ON CRA.ID = SCHED.REF_COMBOREVIEW_ACTION_ID AND SCHED.ACTIVE = 'Y' ");
		sb.append("                                 JOIN APPOINTMENT AS APPT ON SCHED.APPOINTMENT_ID = APPT.ID AND APPT.ACTIVE = 'Y' ");
		sb.append("     JOIN LKUP_APPOINTMENT_STATUS AS APPTS ON SCHED.LKUP_APPOINTMENT_STATUS_ID = APPTS.ID AND APPTS.ACTIVE = 'Y' AND APPTS.SCHEDULED = 'Y' ");

		sb.append("  ");
		sb.append("                                 JOIN REF_ACT_COMBOREVIEW AS RACR ON CR.ID = RACR.COMBOREVIEW_ID AND RACR.ACTIVE = 'Y' ");
		sb.append("                                 JOIN ACTIVITY AS A ON RACR.ACTIVITY_ID = A.ID AND A.ACTIVE = 'Y' ");
		sb.append("                                  JOIN LKUP_ACT_TYPE AS LAT ON A.LKUP_ACT_TYPE_ID = LAT.ID  ");
		sb.append("                                 JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append("  ");
		sb.append("                                 JOIN REF_LSO_PROJECT AS LPROJ ON P.ID = LPROJ.PROJECT_ID AND LPROJ.ACTIVE = 'Y' ");
		sb.append("                                  LEFT OUTER  JOIN  LSO AS L ON LPROJ.LSO_ID = L.ID AND L.ACTIVE = 'Y' ");
		sb.append("                                  LEFT OUTER  JOIN  LSO_STREET AS ST ON L.LSO_STREET_ID = ST.ID AND ST.ACTIVE = 'Y' ");
		sb.append("                                 LEFT OUTER JOIN LSO_GEO LG on L.ID = LG.ID ");
		sb.append("  ");
		sb.append("                                 LEFT OUTER JOIN ( ");
		sb.append("                                                 REF_COMBOREVIEW_ACTION AS NCRA ");
		sb.append("                                                 JOIN LKUP_REVIEW_STATUS AS NS ON NCRA.LKUP_REVIEW_STATUS_ID = NS.ID ");
		sb.append("                                 ) ON CRA.ID = NCRA.PREVIOUS_ID AND NCRA.ACTIVE = 'Y' ");
		sb.append("  ");
		sb.append(" 				LEFT OUTER JOIN REF_COMBOREVIEW_ROUTE RCRR on CRA.ID = RCRR.REF_COMBOREVIEW_ACTION_ID ");
		sb.append("  ");
		sb.append("                                 LEFT OUTER JOIN ( ");
		sb.append("                                                 REF_APPOINTMENT_TEAM AS RTM ");
		sb.append("                                                 JOIN REF_TEAM AS TM ON RTM.REF_TEAM_ID = TM.ID AND TM.ACTIVE = 'Y' AND RTM.ACTIVE = 'Y' ");
		sb.append("                                                 JOIN LKUP_TEAM_TYPE AS LTT ON TM.LKUP_TEAM_TYPE_ID = LTT.ID AND LTT.ACTIVE='Y' ");
		sb.append("  ");
		sb.append("                                                 JOIN USERS AS U ON TM.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
		sb.append("                                 ) ON APPT.ID = RTM.APPOINTMENT_ID  ");
		sb.append(" ) ");
		
		
		/*sb.append(" select count(*) as COUNT, DATE_START,AVAILABILITY_ID from Q   ");
		sb.append(" WHERE DATE_START BETWEEN '2018-02-05' AND '2018-08-15'  ");
		sb.append(" GROUP BY DATE_START,Q.AVAILABILITY_ID ORDER BY DATE_START ");*/
		
			sb.append(additional);
		}
		
		return sb.toString();
	}

	public static String statistics(Timekeeper startdate, Timekeeper enddate) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH COMPLETE AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		S.ID AS SCHEDULE_ID, ");
		sb.append(" 		ACT.ID AS ACTIVITY_ID, ");
		sb.append(" 		ACT.ACT_NBR, ");
		sb.append(" 		AV.ID AS AVAILABILITY_ID, ");
		sb.append(" 		AV.TITLE AS AVAILABILITY, ");
		sb.append(" 		R.NAME AS REVIEW, ");
		sb.append(" 		S.SOURCE, ");
		sb.append(" 		CAST(S.START_DATE AS DATE) AS START_DATE, ");
		sb.append(" 		CASE ");
		sb.append(" 		       WHEN NCA.ID IS NOT NULL THEN NCAS.STATUS ");
		sb.append(" 		       ELSE CAS.STATUS ");
		sb.append(" 		END AS STATUS, ");
		sb.append(" 		CASE ");
		sb.append(" 		       WHEN NCA.ID IS NOT NULL AND NCAS.SCHEDULE_INSPECTION_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 		       WHEN NCA.ID IS NOT NULL AND NCAS.SCHEDULE_CANCEL = 'Y' THEN 'Y' ");
		sb.append(" 		       WHEN SS.SCHEDULED = 'N' THEN 'Y' ");
		sb.append(" 		ELSE 'N' END AS CANCELLED, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN NCA.ID IS NOT NULL THEN 'Y' ");
		sb.append(" 		ELSE 'N' END AS COMPLETED ");
		sb.append("  ");
		sb.append(" 	FROM ");
		sb.append(" 		APPOINTMENT AS A ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND A.ACTIVE = 'Y' AND S.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS SS ON S.LKUP_APPOINTMENT_STATUS_ID = SS.ID AND SS.SCHEDULED = 'Y' ");
		sb.append(" 		JOIN ( ");
		sb.append(" 		       REF_COMBOREVIEW_ACTION AS CA ");
		sb.append(" 		       JOIN LKUP_REVIEW_STATUS AS CAS ON CA.LKUP_REVIEW_STATUS_ID = CAS.ID ");
		sb.append(" 		       LEFT OUTER JOIN ( ");
		sb.append(" 			       REF_COMBOREVIEW_ACTION AS NCA ");
		sb.append(" 			       JOIN LKUP_REVIEW_STATUS AS NCAS ON NCA.LKUP_REVIEW_STATUS_ID = NCAS.ID ");
		sb.append(" 		       ) ON CA.ID = NCA.PREVIOUS_ID AND NCA.ACTIVE = 'Y' ");
		sb.append(" 		) ON S.REF_COMBOREVIEW_ACTION_ID = CA.ID AND CA.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_COMBOREVIEW_REVIEW AS CR ON CA.REF_COMBOREVIEW_REVIEW_ID = CR.ID AND CR.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REF_ACT_COMBOREVIEW AS RAC ON RAC.COMBOREVIEW_ID = CR.COMBOREVIEW_ID AND RAC.ACTIVE = 'Y' ");
		sb.append(" 		JOIN REVIEW AS R ON CR.REVIEW_ID = R.ID AND R.ACTIVE = 'Y' ");
		sb.append(" 		JOIN ACTIVITY AS ACT ON RAC.ACTIVITY_ID = ACT.ID AND ACT.ACTIVE = 'Y' ");
		sb.append(" 		LEFT OUTER JOIN AVAILABILITY AS AV ON R.AVAILABILITY_ID = AV.ID ");
		sb.append(" 	WHERE ");
		sb.append(" 		CAST(S.START_DATE AS DATE) >= '").append(startdate.getString("MM/DD/YYYY")).append("' ");
		sb.append(" 		AND ");
		sb.append(" 		CAST(S.START_DATE AS DATE) <= '").append(enddate.getString("MM/DD/YYYY")).append("' ");
		sb.append(" ) ");
		sb.append(" , MAIN AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		SCHEDULE_ID, ");
		sb.append(" 		AVAILABILITY, ");
		sb.append(" 		AVAILABILITY_ID, ");
		sb.append(" 		START_DATE, ");
		sb.append(" 		SOURCE, ");
		sb.append(" 		DATEPART ( weekday , START_DATE ) AS START_DAY, ");
		sb.append(" 		REVIEW, ");
		sb.append(" 		STATUS, ");
		sb.append(" 		COMPLETED ");
		sb.append(" 	FROM ");
		sb.append(" 		COMPLETE ");
		sb.append(" 	WHERE CANCELLED = 'N' ");
		sb.append(" ) ");
		sb.append(" , AVAILABILITY_VALUES AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		A.ID AS AVAILABILITY_ID, ");
		sb.append(" 		M.START_DATE, ");
		sb.append(" 		D.ID AS AVAILABILITY_DEFAULT_ID, ");
		sb.append(" 		D.SEATS AS DEFAULT_SEATS, ");
		sb.append(" 		D.BUFFER_SEATS AS DEFAULT_BUFFER, ");
		sb.append(" 		C.ID AS AVAILABILITY_CUSTOM_ID, ");
		sb.append(" 		C.SEATS AS CUSTOM_SEATS, ");
		sb.append(" 		C.BUFFER_SEATS AS CUSTOM_BUFFER ");
		sb.append(" 	FROM ");
		sb.append(" 		AVAILABILITY AS A ");
		sb.append(" 		JOIN MAIN AS M ON A.ID = M.AVAILABILITY_ID ");
		sb.append(" 		LEFT OUTER JOIN AVAILABILITY_DEFAULT AS D ON A.ID = D.AVAILABILITY_ID AND D.DAY_OF_WEEK = M.START_DAY AND D.ACTIVE = 'Y' ");
		sb.append(" 		LEFT OUTER JOIN AVAILABILITY_CUSTOM AS C ON A.ID = C.AVAILABILITY_ID AND C.ACTIVE = 'Y' AND M.START_DATE = C.CUSTOM_DATE ");
		sb.append(" ) ");
		sb.append(" , AVAILABILITY_DEFAULT_VALUES AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		V.AVAILABILITY_ID, ");
		sb.append(" 		V.START_DATE, ");
		sb.append(" 		V.DEFAULT_SEATS AS SEATS, ");
		sb.append(" 		V.DEFAULT_BUFFER AS BUFFER ");
		sb.append(" 	FROM ");
		sb.append(" 		AVAILABILITY_VALUES AS V ");
		sb.append(" 	WHERE ");
		sb.append(" 		V.AVAILABILITY_CUSTOM_ID IS NULL ");
		sb.append(" ) ");
		sb.append(" , AVAILABILITY_CUSTOM_VALUES AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		V.AVAILABILITY_ID, ");
		sb.append(" 		V.START_DATE, ");
		sb.append(" 		V.CUSTOM_SEATS AS SEATS, ");
		sb.append(" 		V.CUSTOM_BUFFER AS BUFFER ");
		sb.append(" 	FROM ");
		sb.append(" 		AVAILABILITY_VALUES AS V ");
		sb.append(" 	WHERE ");
		sb.append(" 		V.AVAILABILITY_CUSTOM_ID IS NOT NULL ");
		sb.append(" ) ");
		sb.append(" , AVAILABILITY_CALC AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		V.AVAILABILITY_ID, ");
		sb.append(" 		V.START_DATE, ");
		sb.append(" 		'CUSTOM' AS TYPE, ");
		sb.append(" 		SUM(V.SEATS) AS SEATS, ");
		sb.append(" 		SUM(V.BUFFER) AS BUFFER ");
		sb.append(" 	FROM ");
		sb.append(" 		AVAILABILITY_CUSTOM_VALUES AS V ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		V.AVAILABILITY_ID, ");
		sb.append(" 		V.START_DATE ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		V.AVAILABILITY_ID, ");
		sb.append(" 		V.START_DATE, ");
		sb.append(" 		'DEFAULT' AS TYPE, ");
		sb.append(" 		SUM(V.SEATS) AS SEATS, ");
		sb.append(" 		SUM(V.BUFFER) AS BUFFER ");
		sb.append(" 	FROM ");
		sb.append(" 		AVAILABILITY_DEFAULT_VALUES AS V ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		V.AVAILABILITY_ID, ");
		sb.append(" 		V.START_DATE ");
		sb.append(" ) ");
		sb.append(" , CALC AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		M.AVAILABILITY, ");
		sb.append(" 		M.AVAILABILITY_ID, ");
		sb.append(" 		M.SOURCE, ");
		sb.append(" 		M.START_DATE, ");
		sb.append(" 		C.TYPE, ");
		sb.append(" 		C.SEATS, ");
		sb.append(" 		C.BUFFER, ");
		sb.append(" 		COUNT(DISTINCT M.SCHEDULE_ID) AS REQUESTS ");
		sb.append(" 	FROM ");
		sb.append(" 		MAIN AS M ");
		sb.append(" 		LEFT OUTER JOIN AVAILABILITY_CALC AS C ON M.AVAILABILITY_ID = C.AVAILABILITY_ID AND C.START_DATE = M.START_DATE ");
		sb.append(" 	GROUP BY ");
		sb.append(" 		M.AVAILABILITY, ");
		sb.append(" 		M.AVAILABILITY_ID, ");
		sb.append(" 		M.SOURCE, ");
		sb.append(" 		M.START_DATE, ");
		sb.append(" 		C.TYPE, ");
		sb.append(" 		C.SEATS, ");
		sb.append(" 		C.BUFFER ");
		sb.append(" ) ");
		sb.append(" SELECT * FROM CALC ORDER BY START_DATE, AVAILABILITY ");
		return sb.toString();
	}

	public static String getAvailabilityTotals(String availabilityids) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH AV_SEATS AS ( ");
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		D.AVAILABILITY_ID, ");
		sb.append(" 		A.TITLE AS AVAILABILITY, ");
		sb.append(" 		D.DAY_OF_WEEK, ");
		sb.append(" 		SUM(D.SEATS) AS SEATS, ");
		sb.append(" 		SUM(D.BUFFER_SEATS) AS BUFER_SEATS ");
		sb.append(" 	FROM ");
		sb.append(" 		AVAILABILITY_DEFAULT AS D ");
		sb.append(" 		JOIN AVAILABILITY AS A ON D.AVAILABILITY_ID = A.ID ");
		sb.append(" 	WHERE ");
		sb.append(" 		D.ACTIVE = 'Y' ");
		if (Operator.hasValue(availabilityids)) {
			sb.append(" 		AND ");
			sb.append(" 		A.ID IN (").append(availabilityids).append(") ");
		}
		sb.append(" 	GROUP BY ");
		sb.append(" 		D.AVAILABILITY_ID, ");
		sb.append(" 		A.TITLE, ");
		sb.append(" 		D.DAY_OF_WEEK ");
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" 	AVAILABILITY, ");
		sb.append(" 	AVAILABILITY_ID, ");
		sb.append(" 	DAY_OF_WEEK, ");
		sb.append(" 	SEATS + BUFER_SEATS AS SEATS ");
		sb.append(" FROM ");
		sb.append(" 	AV_SEATS ");
		return sb.toString();
	}

	public static String getActivityInspections(int actid) {
		if (actid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" 	A.ID AS ACTIVITY_ID ");
		sb.append(" 	, ");
		sb.append(" 	A.ACT_NBR ");
		sb.append(" 	, ");
		sb.append(" 	R.NAME AS REVIEW ");
		sb.append(" 	, ");
		sb.append(" 	APS.START_DATE ");
		sb.append(" 	, ");
		sb.append(" 	N.REVIEW_COMMENTS AS INSPECTION_COMMENTS ");
		sb.append(" 	, ");
		sb.append(" 	CRA.REVIEW_COMMENTS AS SCHEDULE_COMMENTS ");
		sb.append(" 	, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN N.ID IS NOT NULL THEN N.REVIEW_COMMENTS ");
		sb.append(" 	ELSE CRA.REVIEW_COMMENTS END AS REVIEW_COMMENTS ");
		sb.append(" 	, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN N.ID IS NOT NULL THEN NRS.STATUS ");
		sb.append(" 	ELSE RS.STATUS END AS STATUS ");
		sb.append(" FROM ");
		sb.append(" 	ACTIVITY AS A ");
		sb.append(" 	JOIN REF_ACT_COMBOREVIEW AS RAC ON A.ID = RAC.ACTIVITY_ID AND RAC.ACTIVE = 'Y' AND RAC.ACTIVITY_ID = ").append(actid);
		sb.append(" 	JOIN COMBOREVIEW AS CR ON RAC.COMBOREVIEW_ID = CR.ID AND CR.ACTIVE = 'Y' ");
		sb.append(" 	JOIN REF_COMBOREVIEW_REVIEW AS CRR ON CRR.COMBOREVIEW_ID = CR.ID AND CRR.ACTIVE = 'Y' ");
		sb.append(" 	JOIN REF_COMBOREVIEW_ACTION AS CRA ON CRR.ID = CRA.REF_COMBOREVIEW_REVIEW_ID AND CRA.ACTIVE = 'Y' ");
		sb.append(" 	JOIN LKUP_REVIEW_STATUS AS RS ON CRA.LKUP_REVIEW_STATUS_ID = RS.ID AND RS.SCHEDULE_INSPECTION = 'Y' ");
		sb.append(" 	JOIN REVIEW AS R ON CRR.REVIEW_ID = R.ID ");
		sb.append(" 	JOIN APPOINTMENT_SCHEDULE AS APS ON CRA.ID = APS.REF_COMBOREVIEW_ACTION_ID AND APS.ACTIVE = 'Y' ");
		sb.append(" 	LEFT OUTER JOIN ( ");
		sb.append(" 		REF_COMBOREVIEW_ACTION AS N ");
		sb.append(" 		JOIN LKUP_REVIEW_STATUS AS NRS ON NRS.ID = N.LKUP_REVIEW_STATUS_ID ");
		sb.append(" 	) ON CRA.ID = N.PREVIOUS_ID AND N.ACTIVE = 'Y' ");
		sb.append(" ORDER BY APS.START_DATE DESC ");
		return sb.toString();
	}
	
	public static String inspectionAvailable(String type,int typeid){
		StringBuilder sb = new StringBuilder();
		sb.append(" select MAX(AVAILABILITY_ID) AS AVAILABILITY_ID from ACTIVITY A  ");
		sb.append("	LEFT OUTER JOIN REF_ACT_REVIEW_GROUP RRG on A.LKUP_ACT_TYPE_ID = RRG.LKUP_ACT_TYPE_ID AND RRG.ACTIVE='Y' ");
		sb.append(" LEFT OUTER JOIN REVIEW R on RRG.REVIEW_GROUP_ID=  R.REVIEW_GROUP_ID AND R.ACTIVE='Y' ");
		sb.append("		WHERE A.ID= ").append(typeid);
		return sb.toString();
	}



}















