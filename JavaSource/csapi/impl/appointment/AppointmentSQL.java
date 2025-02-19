package csapi.impl.appointment;

import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.utils.CsReflect;
import csapi.utils.CsTools;


public class AppointmentSQL {

	public static String list(String type, int typeid, int id) {
		return summary(type, typeid, id, "All");
	}

	public static String summary(String type, int typeid, int id, String option) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();

		sb.append(" WITH ");
		sb.append(" C AS ( SELECT COUNT(DISTINCT ID) AS CONTENT_COUNT FROM CONTENT WHERE ACTIVE = 'Y' AND LOWER(TYPE) = 'appointment') ");
		sb.append(" , Q AS ( ");
		sb.append(" SELECT ");

		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" A.ID AS APPOINTMENT_ID ");
		sb.append(" , ");
		sb.append("   CASE ");
		sb.append("     WHEN C.CONTENT_COUNT IS NULL THEN '' ");
		sb.append("     WHEN C.CONTENT_COUNT > 0 THEN 'appointment' ");
		sb.append("   ELSE '' END AS CONTENT_TYPE ");
		sb.append(" , ");
		sb.append(" CASE WHEN RMG.ID IS NOT NULL THEN RMG.ID ELSE T.ID END AS TYPE ");
		sb.append(" , ");
		sb.append(" CASE WHEN RMG.GROUP_NAME IS NOT NULL THEN RMG.GROUP_NAME ELSE T.TYPE END AS TYPE_TEXT ");
		sb.append(" , ");
		sb.append(" CASE WHEN RM.NAME IS NOT NULL THEN RM.NAME ELSE A.SUBJECT END AS SUBJECT ");
		sb.append(" , ");
		sb.append(" CASE WHEN RM.NAME IS NOT NULL THEN RM.NAME ELSE A.SUBJECT END AS SUBJECT_TEXT ");
		sb.append(" , ");
		sb.append(" S.START_DATE ");
		sb.append(" , ");
		sb.append(" S.END_DATE ");
		sb.append(" , ");
		sb.append(" CASE WHEN NXTS.STATUS IS NOT NULL THEN NXTS.STATUS WHEN CRS.STATUS IS NOT NULL THEN CRS.STATUS WHEN S.END_DATE < getDate() THEN 'EXPIRED' ELSE ST.STATUS END AS STATUS ");
		sb.append(" , ");
		sb.append(" CASE WHEN NXTS.STATUS IS NOT NULL THEN 'Y' WHEN ST.COMPLETE = 'Y' THEN 'Y' ELSE 'N' END AS FINAL ");
		sb.append(" , ");
		sb.append(" CASE WHEN S.REF_COMBOREVIEW_ACTION_ID > 0 AND NXTS.STATUS IS NULL THEN 'Y' WHEN ST.COMPLETE = 'N' THEN 'Y' ELSE 'N' END AS SCHEDULED ");
		sb.append(" , ");
		sb.append(" CASE WHEN S.REF_COMBOREVIEW_ACTION_ID > 0 AND NXTS.STATUS IS NULL THEN 'Y' WHEN ST.COMPLETE = 'Y' THEN 'N' WHEN S.END_DATE < getDate() THEN 'N' ELSE 'Y' END AS ISACTIVE ");
		sb.append(" , ");
		sb.append(" S.ID AS SCHEDULE_ID ");
		sb.append(" , ");
		sb.append(" RMR.ID AS REF_COMBOREVIEW_REVIEW_ID ");
		sb.append(" , ");
		sb.append(" CRA.ID AS REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" , ");
		sb.append(" RMR.COMBOREVIEW_ID ");
		sb.append(" , ");
		sb.append(" RM.ID AS REVIEW_ID ");
		sb.append(" , ");
		sb.append(" RM.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" RMG.ID AS REVIEW_GROUP_ID ");
		sb.append(" , ");
		sb.append(" RMG.GROUP_NAME AS REVIEW_GROUP ");
		sb.append(" , ");
		sb.append(" S.CREATED_DATE ");

		sb.append(" , ");
		sb.append(" COUNT(DISTINCT U.ID) AS COLLABORATORS ");

		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_APPOINTMENT AS R ");
		sb.append(" JOIN APPOINTMENT AS A ON R.APPOINTMENT_ID = A.ID ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID ");

		sb.append(" LEFT OUTER JOIN LKUP_APPOINTMENT_TYPE AS T ON A.LKUP_APPOINTMENT_TYPE_ID > 0 AND A.LKUP_APPOINTMENT_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN C ON 1 = 1 ");

		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   REF_COMBOREVIEW_REVIEW AS RMR ");
		sb.append("   JOIN REVIEW AS RM ON RMR.REVIEW_ID = RM.ID AND RM.ACTIVE = 'Y' ");
		sb.append("   JOIN REVIEW_GROUP AS RMG ON RM.REVIEW_GROUP_ID = RMG.ID AND RMG.ACTIVE = 'Y' ");
		sb.append(" ) ON RMR.ID = A.REF_COMBOREVIEW_REVIEW_ID AND RMR.ACTIVE = 'Y' ");

		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   REF_COMBOREVIEW_ACTION AS CRA ");
		sb.append("   JOIN LKUP_REVIEW_STATUS AS CRS ON CRA.LKUP_REVIEW_STATUS_ID = CRS.ID ");
		sb.append("   LEFT OUTER JOIN ( ");
		sb.append("     REF_COMBOREVIEW_ACTION AS NXTA ");
		sb.append("     JOIN LKUP_REVIEW_STATUS AS NXTS ON NXTA.LKUP_REVIEW_STATUS_ID = NXTS.ID AND NXTS.ACTIVE = 'Y' AND NXTA.ACTIVE = 'Y' ");
		sb.append("   ) ON NXTA.PREVIOUS_ID = CRA.ID ");
		sb.append(" ) ON S.REF_COMBOREVIEW_ACTION_ID = CRA.ID ");

		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   REF_APPOINTMENT_USERS AS RAU ");
		sb.append("   JOIN USERS AS U ON RAU.USERS_ID = U.ID AND U.ACTIVE = 'Y' AND RAU.ACTIVE = 'Y' ");
		sb.append(" )  ON A.ID = RAU.APPOINTMENT_ID ");

		sb.append(" WHERE ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" AND ");
		sb.append(" ST.SCHEDULED = 'Y' ");
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" A.ID = ").append(id);
		}
		else {
			sb.append(" AND ");
			sb.append(" A.ACTIVE = 'Y' ");
			sb.append(" AND ");
			sb.append(" R.ACTIVE = 'Y' ");
		}

		sb.append(" GROUP BY ");
		sb.append(" S.ID ");
		sb.append(" , ");
		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" RMG.ID ");
		sb.append(" , ");
		sb.append(" T.ID ");
		sb.append(" , ");
		sb.append(" RMG.GROUP_NAME ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" C.CONTENT_COUNT ");
		sb.append(" , ");
		sb.append(" RM.NAME ");
		sb.append(" , ");
		sb.append(" A.SUBJECT ");
		sb.append(" , ");
		sb.append(" S.START_DATE ");
		sb.append(" , ");
		sb.append(" S.END_DATE ");
		sb.append(" , ");
		sb.append(" NXTS.STATUS ");
		sb.append(" , ");
		sb.append(" CRS.STATUS ");
		sb.append(" , ");
		sb.append(" ST.STATUS ");
		sb.append(" , ");
		sb.append(" ST.COMPLETE ");
		sb.append(" , ");
		sb.append(" S.REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" , ");
		sb.append(" RMR.ID ");
		sb.append(" , ");
		sb.append(" CRA.ID ");
		sb.append(" , ");
		sb.append(" RMR.COMBOREVIEW_ID ");
		sb.append(" , ");
		sb.append(" RM.ID ");
		sb.append(" , ");
		sb.append(" S.CREATED_DATE ");



		sb.append(" ) ");
		if (option.equalsIgnoreCase("COUNT")) {
			sb.append(" SELECT COUNT(DISTINCT ID) AS NUMRESULTS FROM Q ");
		}
		else {
			sb.append(" SELECT * FROM Q ");
			if (Operator.equalsIgnoreCase(option, "INACTIVE")) {
				sb.append(" WHERE Q.ISACTIVE = 'N' ");
			}
			else if (Operator.equalsIgnoreCase(option, "ALL")) {
			}
			else {
				sb.append(" WHERE Q.ISACTIVE = 'Y' ");
			}
			sb.append(" ORDER BY SCHEDULED DESC, START_DATE DESC, END_DATE DESC, CREATED_DATE ASC ");
		}
		return sb.toString();
	}

	public static String id(String type, int typeid, int id) {
		StringBuilder sb = new StringBuilder();


		sb.append(" SELECT ");
		sb.append(" APPOINTMENT_ID AS ID, ");
		sb.append(" APPOINTMENT_ID, ");
		sb.append(" APPOINTMENT_SCHEDULE_ID, ");
		sb.append(" LKUP_APPOINTMENT_TYPE_ID, ");
		sb.append(" REF_COMBOREVIEW_REVIEW_ID, ");
		sb.append(" REF_COMBOREVIEW_ACTION_ID, ");
		sb.append(" SUBJECT, ");
		sb.append(" TYPE, ");
		sb.append(" APPOINTMENT_DATE, ");
		sb.append(" NOTES_ID, ");
		sb.append(" NOTE, ");
		sb.append(" START_DATE, ");
		sb.append(" END_DATE, ");
		sb.append(" LKUP_APPOINTMENT_STATUS_ID, ");
		sb.append(" STATUS, ");
		sb.append(" PARENT_ID, ");
		sb.append(" COLLABORATORS, ");
		sb.append(" TEAM, ");
		sb.append(" SOURCE, ");
		sb.append(" ACTIVE, ");
		sb.append(" CREATED_BY, ");
		sb.append(" CREATED_USERNAME, ");
		sb.append(" CREATED_DATE, ");
		sb.append(" UPDATED_BY, ");
		sb.append(" UPDATED_USERNAME, ");
		sb.append(" UPDATED_DATE, ");
		sb.append(" CREATED_IP, ");
		sb.append(" UPDATED_IP ");
		sb.append(" FROM ");
		sb.append(" APPOINTMENT_HISTORY ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		return sb.toString();
	}

	public static String getHistory(int histid) {
		if (histid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(" APPOINTMENT_HISTORY ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(histid);
		return sb.toString();
	}

	public static String getFullAppointment(int apptid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");

		sb.append(" S.APPOINTMENT_ID, ");
		sb.append(" S.ID AS APPOINTMENT_SCHEDULE_ID, ");
		sb.append(" A.LKUP_APPOINTMENT_TYPE_ID, ");
		sb.append(" A.REF_COMBOREVIEW_REVIEW_ID, ");
		sb.append(" S.REF_COMBOREVIEW_ACTION_ID, ");
		sb.append(" CASE ");
		sb.append("   WHEN RCA.ID IS NOT NULL THEN R.NAME ");
		sb.append("   ELSE A.SUBJECT END AS SUBJECT, ");
		sb.append(" CASE ");
		sb.append("   WHEN RCA.ID IS NOT NULL THEN RG.GROUP_NAME ");
		sb.append("   ELSE TY.TYPE END AS TYPE, ");
		sb.append(" A.APPOINTMENT_DATE, ");
		sb.append(" S.START_DATE, ");
		sb.append(" S.END_DATE, ");
		sb.append(" S.LKUP_APPOINTMENT_STATUS_ID, ");
		sb.append(" T.STATUS, ");
		sb.append(" S.PARENT_ID, ");
		sb.append(" S.SOURCE, ");
		sb.append(" S.ACTIVE, ");
		sb.append(" S.CREATED_BY, ");
		sb.append(" C.USERNAME AS CREATED_USERNAME, ");
		sb.append(" S.CREATED_DATE, ");
		sb.append(" S.UPDATED_BY, ");
		sb.append(" U.USERNAME AS UPDATED_USERNAME, ");
		sb.append(" S.UPDATED_DATE, ");
		sb.append(" S.CREATED_IP, ");
		sb.append(" S.UPDATED_IP ");

		sb.append(" FROM ");
		sb.append(" APPOINTMENT AS A ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND A.ID = ").append(apptid);
		sb.append(" LEFT OUTER JOIN LKUP_APPOINTMENT_STATUS AS T ON S.LKUP_APPOINTMENT_STATUS_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_APPOINTMENT_TYPE AS TY ON A.LKUP_APPOINTMENT_TYPE_ID = TY.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS C ON S.CREATED_BY = C.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON S.UPDATED_BY = U.ID ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("    REF_COMBOREVIEW_ACTION AS RCA ");
		sb.append("    JOIN LKUP_REVIEW_STATUS AS RS ON RCA.LKUP_REVIEW_STATUS_ID = RS.ID ");
		sb.append("    JOIN REF_COMBOREVIEW_REVIEW AS RCR ON RCA.REF_COMBOREVIEW_REVIEW_ID = RCR.ID ");
		sb.append("    JOIN REVIEW AS R ON RCR.REVIEW_ID = R.ID ");
		sb.append("    JOIN REVIEW_GROUP AS RG ON R.REVIEW_GROUP_ID = RG.ID ");
		sb.append(" ) ON S.REF_COMBOREVIEW_ACTION_ID = RCA.ID ");
		return sb.toString();
	}

	public static String addHistory(int appointmentid, int appointmentscheduleid, int lkupappointmenttypeid, int refcomboreviewreviewid, int refcomboreviewactionid, String subject, String type, int noteid, String note, String appointmentdate, String startdate, String enddate, int lkupappointmentstatusid, String status, int parentid, String collaborators, String team, String source, String active, int createdby, String createdusername, String createddate, int updatedby, String updatedusername, String updateddate, String createdip, String updatedip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO APPOINTMENT_HISTORY ( ");
		sb.append(" APPOINTMENT_ID, ");
		sb.append(" APPOINTMENT_SCHEDULE_ID, ");
		sb.append(" LKUP_APPOINTMENT_TYPE_ID, ");
		sb.append(" REF_COMBOREVIEW_REVIEW_ID, ");
		sb.append(" REF_COMBOREVIEW_ACTION_ID, ");
		sb.append(" SUBJECT, ");
		sb.append(" TYPE, ");
		sb.append(" NOTES_ID, ");
		sb.append(" NOTE, ");
		sb.append(" APPOINTMENT_DATE, ");
		sb.append(" START_DATE, ");
		sb.append(" END_DATE, ");
		sb.append(" LKUP_APPOINTMENT_STATUS_ID, ");
		sb.append(" STATUS, ");
		sb.append(" PARENT_ID, ");
		sb.append(" COLLABORATORS, ");
		sb.append(" TEAM, ");
		sb.append(" SOURCE, ");
		sb.append(" ACTIVE, ");
		sb.append(" CREATED_BY, ");
		sb.append(" CREATED_USERNAME, ");
		sb.append(" CREATED_DATE, ");
		sb.append(" UPDATED_BY, ");
		sb.append(" UPDATED_USERNAME, ");
		sb.append(" UPDATED_DATE, ");
		sb.append(" CREATED_IP, ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) Output Inserted.ID VALUES ( ");
		sb.append(appointmentid);
		sb.append(" , ");
		sb.append(appointmentscheduleid);
		sb.append(" , ");
		sb.append(lkupappointmenttypeid);
		sb.append(" , ");
		sb.append(refcomboreviewreviewid);
		sb.append(" , ");
		sb.append(refcomboreviewactionid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(subject)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(type)).append("' ");
		sb.append(" , ");
		sb.append(noteid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(note)).append("' ");
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(appointmentdate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(startdate));
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(enddate));
		sb.append(" , ");
		sb.append(lkupappointmentstatusid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(status)).append("' ");
		sb.append(" , ");
		sb.append(parentid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(collaborators)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(team)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(source)).append("' ");
		sb.append(" , ");
		sb.append(CsTools.booleanColumnValue(active));
		sb.append(" , ");
		sb.append(createdby);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(createdusername)).append("' ");
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(createddate));
		sb.append(" , ");
		sb.append(updatedby);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(updatedusername)).append("' ");
		sb.append(" , ");
		sb.append(CsTools.dateColumnValue(updateddate));
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(createdip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(updatedip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String summary1(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" S.ID ");
		sb.append(" , ");
		sb.append(" CASE WHEN RG.ID IS NOT NULL THEN RG.ID ELSE T.ID END AS TYPE ");
		sb.append(" , ");
		sb.append(" CASE WHEN RG.GROUP_NAME IS NOT NULL THEN RG.GROUP_NAME ELSE T.TYPE END AS TYPE_TEXT ");
		sb.append(" , ");
		sb.append(" CASE WHEN RV.NAME IS NOT NULL THEN RV.NAME ELSE A.SUBJECT END AS SUBJECT ");
		sb.append(" , ");
		sb.append(" CASE WHEN RV.NAME IS NOT NULL THEN RV.NAME ELSE A.SUBJECT END AS SUBJECT_TEXT ");
		sb.append(" , ");
		sb.append(" S.START_DATE ");
		sb.append(" , ");
		sb.append(" S.END_DATE ");
		sb.append(" , ");
		sb.append(" CASE WHEN NXTS.STATUS IS NOT NULL THEN NXTS.STATUS WHEN RST.STATUS IS NOT NULL THEN RST.STATUS ELSE ST.STATUS END AS STATUS ");
		sb.append(" , ");
		sb.append(" CASE WHEN RST.SCHEDULE IS NOT NULL AND RST.SCHEDULE = 'N' THEN 'Y' WHEN ST.COMPLETE = 'Y' THEN 'Y' ELSE 'N' END AS FINAL ");
		sb.append(" , ");
		sb.append(" CASE WHEN RST.SCHEDULE IS NOT NULL AND RST.SCHEDULE = 'Y' AND RST.FINAL = 'N' AND RST.APPROVED = 'N' AND RST.UNAPPROVED = 'N' THEN 'Y' WHEN RST.SCHEDULE IS NULL AND ST.COMPLETE = 'N' THEN 'Y' ELSE 'N' END AS SCHEDULED ");
		sb.append(" , ");
		sb.append(" RST.APPROVED ");
		sb.append(" , ");
		sb.append(" RST.UNAPPROVED ");
		sb.append(" , ");
		sb.append(" RST.FINAL AS REVIEWFINAL ");
		sb.append(" , ");
		sb.append(" RST.SCHEDULE AS REVIEWSCHEDULE ");
		sb.append(" , ");
		sb.append(" S.ID AS SCHEDULE_ID ");
		sb.append(" , ");
		sb.append(" CRR.ID AS REF_COMBOREVIEW_REVIEW_ID ");
		sb.append(" , ");
		sb.append(" CRA.ID AS REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" , ");
		sb.append(" CRR.COMBOREVIEW_ID ");
		sb.append(" , ");
		sb.append(" RV.ID AS REVIEW_ID ");
		sb.append(" , ");
		sb.append(" RV.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" RST.STATUS AS REVIEW_STATUS ");
		sb.append(" , ");
		sb.append(" RG.ID AS REVIEW_GROUP_ID ");
		sb.append(" , ");
		sb.append(" RG.GROUP_NAME AS REVIEW_GROUP ");
	
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_APPOINTMENT AS R ");
		sb.append(" JOIN APPOINTMENT AS A ON R.APPOINTMENT_ID = A.ID ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");

		sb.append(" LEFT OUTER JOIN LKUP_APPOINTMENT_TYPE AS T ON A.LKUP_APPOINTMENT_TYPE_ID > 0 AND A.LKUP_APPOINTMENT_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID ");

		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   REF_COMBOREVIEW_REVIEW AS CRR ");
		sb.append("   JOIN REF_COMBOREVIEW_ACTION AS CRA ON CRA.REF_COMBOREVIEW_REVIEW_ID = CRR.ID AND CRA.ACTIVE = 'Y' AND CRA.EXPIRED = 'N' ");
		sb.append("   JOIN LKUP_REVIEW_STATUS AS RST ON CRA.LKUP_REVIEW_STATUS_ID = RST.ID ");
		sb.append("   JOIN REVIEW AS RV ON CRR.REVIEW_ID = RV.ID ");
		sb.append("   JOIN REVIEW_GROUP AS RG ON RV.REVIEW_GROUP_ID = RG.ID ");
		sb.append("   JOIN REF_").append(tableref).append("_COMBOREVIEW AS RCR ON CRR.COMBOREVIEW_ID = RCR.COMBOREVIEW_ID AND RCR.").append(idref).append(" = ").append(typeid);
		sb.append(" ) ON CRR.ID = A.REF_COMBOREVIEW_REVIEW_ID ");

		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   REF_COMBOREVIEW_ACTION AS NXT ");
		sb.append("   JOIN LKUP_REVIEW_STATUS AS NXTS ON NXT.LKUP_REVIEW_STATUS_ID = NXTS.ID ");
		sb.append(" ) ON S.REF_COMBOREVIEW_ACTION_ID = NXT.PREVIOUS_ID AND NXT.ACTIVE = 'Y' ");

		sb.append(" WHERE ");
		sb.append(" ( ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" OR ");
		sb.append(" RCR.").append(idref).append(" = ").append(typeid);
		sb.append(" ) ");
		sb.append(" AND ");
		sb.append(" ST.SCHEDULED = 'Y' ");
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" A.ID = ").append(id);
		}
		else {
			sb.append(" AND ");
			sb.append(" A.ACTIVE = 'Y' ");
			sb.append(" AND ");
			sb.append(" R.ACTIVE = 'Y' ");
		}
		sb.append(" AND ");
		sb.append(" ( ");
		sb.append(" CONVERT(date, S.START_DATE) >= CONVERT(date, getDate()) ");
		sb.append(" OR ");
		sb.append(" CONVERT(date, S.END_DATE) > CONVERT(date, getDate()) ");
		sb.append(" ) ");
		sb.append(" ORDER BY S.START_DATE ASC, S.END_DATE ASC ");
		return sb.toString();
	}

	public static String getTypeId(String type, int apptid) {
		if (apptid < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.").append(idref).append(" AS ID ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_APPOINTMENT AS R ");
		sb.append(" WHERE ");
		sb.append(" R.APPOINTMENT_ID = ").append(apptid);
		sb.append(" AND ");
		sb.append(" R.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String details(String type, int typeid, int apptid) {
		if (!Operator.hasValue(type)) { return ""; }
		StringBuilder sb = new StringBuilder();

		sb.append(" WITH S AS ( ");

		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 		CASE WHEN R.ID IS NOT NULL THEN R.AVAILABILITY_ID ELSE T.AVAILABILITY_ID END AS AVAILABILITY_ID ");
		sb.append(" 		, ");
		sb.append(" 		S.ID ");
		sb.append(" 		, ");
		sb.append(" 		S.ID AS SCHEDULE_ID ");
		sb.append(" 		, ");
		sb.append(" 		CASE WHEN A.LKUP_APPOINTMENT_TYPE_ID < 0 THEN G.ID * -1 ELSE A.LKUP_APPOINTMENT_TYPE_ID END AS TYPE ");
		sb.append(" 		, ");
		sb.append(" 		CASE WHEN A.LKUP_APPOINTMENT_TYPE_ID < 0 THEN G.GROUP_NAME ELSE T.TYPE END AS TYPE_TEXT ");
		sb.append(" 		, ");
		sb.append(" 		CASE WHEN A.LKUP_APPOINTMENT_TYPE_ID < 0 THEN R.NAME ELSE A.SUBJECT END AS SUBJECT ");
		sb.append(" 		, ");
		sb.append(" 		R.ID AS REVIEW_ID ");
		sb.append(" 		, ");
		sb.append(" 		R.NAME AS REVIEW ");
//		sb.append(" 		, ");
//		sb.append(" 		A.SUBJECT ");
		sb.append(" 		, ");
		sb.append(" 		S.START_DATE ");
		sb.append(" 		, ");
		sb.append(" 		S.END_DATE ");
		sb.append(" 		, ");
		sb.append(" 		CASE WHEN R.ID IS NOT NULL AND R.ID > 0 THEN CRS.ID * -1 ELSE ST.ID END AS STATUS_ID ");
		sb.append(" 		, ");
		sb.append(" 		CASE WHEN R.ID IS NOT NULL AND R.ID > 0 THEN CRS.STATUS ELSE ST.STATUS END AS STATUS ");
		sb.append(" 		, ");
		sb.append(" 		CASE WHEN R.ID IS NOT NULL AND R.ID > 0 THEN 'Y' WHEN ST.COMPLETE = 'Y' THEN 'Y' ELSE 'N' END AS FINAL ");
		sb.append(" 		, ");
		sb.append(" 		CONVERT(date, S.START_DATE) AS DATE_START ");
		sb.append(" 		, ");
		sb.append(" 		CAST(S.START_DATE as time) AS TIME_START ");
		sb.append(" 		, ");
		sb.append(" 		CONVERT(date, S.END_DATE) AS DATE_END ");
		sb.append(" 		, ");
		sb.append(" 		CAST(S.END_DATE as time) AS TIME_END ");
		sb.append(" 		, ");
		sb.append(" 		DATEPART ( weekday , S.START_DATE ) AS START_DAY ");
		sb.append(" 		, ");
		sb.append(" 		DATEPART ( weekday , S.END_DATE ) AS END_DAY ");

		sb.append(" 	FROM ");
		sb.append(" 		APPOINTMENT AS A ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID ");
		sb.append(" 		LEFT OUTER JOIN LKUP_APPOINTMENT_TYPE AS T ON A.LKUP_APPOINTMENT_TYPE_ID = T.ID ");
		sb.append(" 		LEFT OUTER JOIN ( ");
		sb.append(" 			REF_COMBOREVIEW_ACTION AS CRA ");
		sb.append(" 			JOIN LKUP_REVIEW_STATUS AS CRS ON CRA.LKUP_REVIEW_STATUS_ID = CRS.ID ");
		sb.append(" 			JOIN REF_COMBOREVIEW_REVIEW AS CRR ON CRA.REF_COMBOREVIEW_REVIEW_ID = CRR.ID ");
		sb.append(" 			JOIN REVIEW AS R ON CRR.REVIEW_ID = R.ID ");
		sb.append(" 			JOIN REVIEW_GROUP AS G ON R.REVIEW_GROUP_ID = G.ID ");
		sb.append(" 		) ON S.REF_COMBOREVIEW_ACTION_ID = CRA.ID ");


		if (apptid > 0) {
			sb.append(" 	WHERE ");
			sb.append(" 		A.ID = ").append(apptid);
		}

		sb.append(" ), Q AS ( ");

		sb.append(" 	SELECT ");
		sb.append(" 		S.* ");
		sb.append(" 		, ");
		sb.append(" 		CASE WHEN C.ID IS NULL THEN 0 ELSE C.ID END AS CUSTOM_ID ");
		sb.append(" 		, ");
		sb.append(" 		CASE WHEN D.ID IS NULL THEN 0 ELSE D.ID END AS DEFAULT_ID ");
		sb.append(" 		, ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN ");
		sb.append(" 				C.ID IS NOT NULL AND S.TIME_START = C.TIME_START AND S.TIME_END = C.TIME_END THEN 100 ");
		sb.append(" 			WHEN ");
		sb.append(" 				C.ID IS NOT NULL AND S.TIME_START = C.TIME_START THEN 50 ");
		sb.append(" 			WHEN ");
		sb.append(" 				C.ID IS NULL AND S.TIME_START = D.TIME_START AND S.TIME_END = D.TIME_END THEN 100 ");
		sb.append(" 			WHEN ");
		sb.append(" 				C.ID IS NULL AND S.TIME_START = D.TIME_START THEN 50 ");
		sb.append(" 			WHEN ");
		sb.append(" 				C.ID IS NOT NULL THEN 25 ");
		sb.append(" 			ELSE ");
		sb.append(" 				0 END AS POINTS ");
		sb.append(" 	FROM ");
		sb.append(" 		S ");
		sb.append(" 		LEFT OUTER JOIN AVAILABILITY_CUSTOM AS C ON S.AVAILABILITY_ID = C.AVAILABILITY_ID AND C.ACTIVE = 'Y' AND C.CUSTOM_DATE = S.DATE_START AND S.TIME_END > C.TIME_START AND S.TIME_START < C.TIME_END ");
		sb.append(" 		LEFT OUTER JOIN AVAILABILITY_DEFAULT AS D ON S.AVAILABILITY_ID = D.AVAILABILITY_ID AND D.ACTIVE = 'Y' AND D.DAY_OF_WEEK = S.START_DAY AND S.TIME_END > D.TIME_START AND S.TIME_START < D.TIME_END ");
		sb.append(" ) ");

		sb.append(" SELECT TOP 1 ");
		sb.append(" 	Q.* ");
		sb.append(" 	, ");
		sb.append(" 	Q.DATE_START AS DATE ");
		sb.append(" 	, ");
		sb.append(" 	CAST(Q.DEFAULT_ID AS VARCHAR(10)) + '|' + CAST(Q.CUSTOM_ID AS VARCHAR(10)) AS TIME ");
		sb.append(" 	, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN TIME_START = '00:00:00' AND TIME_END = '23:59:59' THEN 'ALL DAY' ");
		sb.append(" 		ELSE CAST(TIME_START AS VARCHAR(10)) + ' ' + CAST(TIME_END AS VARCHAR(10)) END AS TIME_TEXT ");
		sb.append(" 	FROM Q ");
		sb.append(" 	ORDER BY ");
		sb.append(" 		POINTS DESC, TIME_START ASC ");

		return sb.toString();
	}





	public static String details1(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" S.ID ");
		sb.append(" , ");
		sb.append(" CASE WHEN A.LKUP_APPOINTMENT_TYPE_ID < 0 THEN A.REVIEW_GROUP_ID * -1 ELSE A.LKUP_APPOINTMENT_TYPE_ID END AS LKUP_APPOINTMENT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" CASE WHEN A.LKUP_APPOINTMENT_TYPE_ID < 0 THEN RG.GROUP_NAME ELSE T.TYPE END AS LKUP_APPOINTMENT_TYPE ");
		sb.append(" , ");
		sb.append(" A.REVIEW_ID ");
		sb.append(" , ");
		sb.append(" RV.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" A.SUBJECT ");
		sb.append(" , ");
		sb.append(" S.START_DATE ");
		sb.append(" , ");
		sb.append(" S.END_DATE ");
		sb.append(" , ");
		sb.append(" ST.STATUS ");

		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_APPOINTMENT AS R ");
		sb.append(" JOIN APPOINTMENT AS A ON R.APPOINTMENT_ID = A.ID ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN LKUP_APPOINTMENT_TYPE AS T ON A.LKUP_APPOINTMENT_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REVIEW_GROUP AS RG ON A.REVIEW_GROUP_ID = RG.ID AND RG.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REVIEW AS RV ON A.REVIEW_ID = RV.ID AND RV.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" S.ID = ").append(id);
		}
		else {
			sb.append(" AND ");
			sb.append(" A.ACTIVE = 'Y' ");
			sb.append(" AND ");
			sb.append(" R.ACTIVE = 'Y' ");
		}
		sb.append(" ORDER BY A.UPDATED_DATE DESC ");
		return sb.toString();
	}

	public static String addAppointment(String type, int appttypeid, int refcomboreviewid, String subject, int user, String ip, Timekeeper date) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO APPOINTMENT (LKUP_APPOINTMENT_TYPE_ID, REF_COMBOREVIEW_REVIEW_ID, SUBJECT, TYPE, CREATED_BY, CREATED_IP, CREATED_DATE, UPDATED_BY, UPDATED_IP, UPDATED_DATE) VALUES ( ");
		sb.append(appttypeid);
		sb.append(" , ");
		sb.append(refcomboreviewid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(subject)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(type)).append("' ");
		sb.append(" , ");
		sb.append(user);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(user);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" ) ");
		return sb.toString();
	}

	public static String updateAppointment(int id, String subject, int user, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE APPOINTMENT SET ");
		sb.append(" SUBJECT = '").append(Operator.sqlEscape(subject)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(user);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		return sb.toString();
	}

	public static String updateAppointment(int id, int user, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE APPOINTMENT SET ");
		sb.append(" UPDATED_BY = ").append(user);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		return sb.toString();
	}

	public static String deleteAppointment(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE APPOINTMENT SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		return sb.toString();
	}

	public static String getAppointment(int scheduleid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.* ");
		sb.append(" ,  ");
		sb.append(" R.REVIEW_ID ");
		sb.append(" FROM ");
		sb.append(" APPOINTMENT AS A ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID ");
		sb.append(" LEFT OUTER JOIN REF_COMBOREVIEW_REVIEW AS R ON A.REF_COMBOREVIEW_REVIEW_ID = R.ID AND A.LKUP_APPOINTMENT_TYPE_ID < 0 ");
		sb.append(" WHERE ");
		sb.append(" S.ID = ").append(scheduleid);
		return sb.toString();
	}

	public static String getApptAppointment(int apptid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.* ");
		sb.append(" ,  ");
		sb.append(" R.REVIEW_ID ");
		sb.append(" FROM ");
		sb.append(" APPOINTMENT AS A ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID ");
		sb.append(" LEFT OUTER JOIN REF_COMBOREVIEW_REVIEW AS R ON A.REF_COMBOREVIEW_REVIEW_ID = R.ID AND A.LKUP_APPOINTMENT_TYPE_ID < 0 ");
		sb.append(" WHERE ");
		sb.append(" A.ID = ").append(apptid);
		return sb.toString();
	}

	public static String getReviewActionAppointment(int actid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.* ");
		sb.append(" , ");
		sb.append(" S.ID AS SCHEDULE_ID ");
		sb.append(" FROM ");
		sb.append(" APPOINTMENT AS A ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID ");
		sb.append(" WHERE ");
		sb.append(" S.REF_COMBOREVIEW_ACTION_ID = ").append(actid);
		return sb.toString();
	}

	public static String getReviewAppointment(int revrefid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.* ");
		sb.append(" , ");
		sb.append(" S.ID AS SCHEDULE_ID ");
		sb.append(" FROM ");
		sb.append(" APPOINTMENT AS A ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID ");
		sb.append(" WHERE ");
		sb.append(" A.REF_COMBOREVIEW_REVIEW_ID = ").append(revrefid);
		return sb.toString();
	}

	public static String getAppointment(String subject, int user, Timekeeper date) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM APPOINTMENT WHERE ");
		sb.append(" SUBJECT = '").append(Operator.sqlEscape(subject)).append("' ");
		sb.append(" AND ");
		sb.append(" CREATED_BY = ").append(user);
		sb.append(" AND ");
		sb.append(" CREATED_DATE = ").append(date.sqlDatetime());
		return sb.toString();
	}

	public static String updateReviewApointment(int revrefid, int user, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE APPOINTMENT SET ");
		sb.append(" UPDATED_BY = ").append(user);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" REF_COMBOREVIEW_REVIEW_ID = ").append(revrefid);
		return sb.toString();
	}

	public static String updateSchedule(int apptid, String status, int user, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" APPOINTMENT_SCHEDULE ");
		sb.append(" SET ");
		sb.append(" LKUP_APPOINTMENT_STATUS_ID = (SELECT TOP 1 ID FROM LKUP_APPOINTMENT_STATUS WHERE LOWER(STATUS) = LOWER('").append(status).append("')) ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(user);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = GETDATE() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(" APPOINTMENT_ID = ").append(apptid);
		return sb.toString();
	}

	public static String completeSchedule(int id, int user, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" APPOINTMENT_SCHEDULE ");
		sb.append(" SET ");
		sb.append(" LKUP_APPOINTMENT_STATUS_ID = (SELECT TOP 1 ID FROM LKUP_APPOINTMENT_STATUS WHERE DEFAULT_COMPLETE = 'Y') ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(user);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = GETDATE() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		return sb.toString();
	}

	public static String completeSchedule(int revrefid, int actid, int user, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" APPOINTMENT_SCHEDULE ");
		sb.append(" SET ");
		sb.append(" LKUP_APPOINTMENT_STATUS_ID = (SELECT TOP 1 ID FROM LKUP_APPOINTMENT_STATUS WHERE DEFAULT_COMPLETE = 'Y') ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(user);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = GETDATE() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(" APPOINTMENT_ID IN ( SELECT ID FROM APPOINTMENT WHERE REF_COMBOREVIEW_REVIEW_ID = ").append(revrefid).append(" ) ");
		sb.append(" AND ");
		sb.append(" ID <> ").append(actid);
		sb.append(" AND ");
		sb.append(" LKUP_APPOINTMENT_STATUS_ID NOT IN ( SELECT ID FROM LKUP_APPOINTMENT_STATUS WHERE SCHEDULED = 'N' ) ");
		return sb.toString();
	}

	public static String cancelSchedule(int revrefid, int actid, int user, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" APPOINTMENT_SCHEDULE ");
		sb.append(" SET ");
		sb.append(" LKUP_APPOINTMENT_STATUS_ID = (SELECT TOP 1 ID FROM LKUP_APPOINTMENT_STATUS WHERE LOWER(STATUS) = 'cancelled') ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(user);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = GETDATE() ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(" APPOINTMENT_ID IN ( SELECT ID FROM APPOINTMENT WHERE REF_COMBOREVIEW_REVIEW_ID = ").append(revrefid).append(" ) ");
		sb.append(" AND ");
		sb.append(" ID <> ").append(actid);
		sb.append(" AND ");
		sb.append(" LKUP_APPOINTMENT_STATUS_ID NOT IN ( SELECT ID FROM LKUP_APPOINTMENT_STATUS WHERE SCHEDULED = 'N' ) ");
		return sb.toString();
	}

	public static String getSchedule(int scheduleid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" S.* ");
		sb.append(" , ");
		sb.append(" A.REVIEW_ID ");
		sb.append(" , ");
		sb.append(" ST.STATUS ");
		sb.append(" , ");
		sb.append(" ST.COMPLETE ");
		sb.append(" FROM ");
		sb.append(" APPOINTMENT_SCHEDULE AS S ");
		sb.append(" JOIN APPOINTMENT AS A ON S.APPOINTMENT_ID = A.ID ");
		sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID ");
		sb.append(" WHERE ");
		sb.append(" S.ID = ").append(scheduleid);
		return sb.toString();
	}

	public static String getChildSchedule(int parentid, int userid, Timekeeper d) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" S.* ");
		sb.append(" , ");
		sb.append(" A.REVIEW_ID ");
		sb.append(" , ");
		sb.append(" ST.STATUS ");
		sb.append(" , ");
		sb.append(" ST.COMPLETE ");
		sb.append(" FROM ");
		sb.append(" APPOINTMENT_SCHEDULE AS S ");
		sb.append(" JOIN APPOINTMENT AS A ON S.APPOINTMENT_ID = A.ID ");
		sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID ");
		sb.append(" WHERE ");
		sb.append(" S.PARENT_ID = ").append(parentid);
		sb.append(" AND ");
		sb.append(" S.UPDATED_BY = ").append(userid);
		sb.append(" AND ");
		sb.append(" S.UPDATED_DATE = ").append(d.sqlDatetime());
		return sb.toString();
	}

	public static String copySchedule(int scheduleid, String apptstatus, int reviewstatusid, int userid, String ip, Timekeeper d) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ");
		sb.append(" APPOINTMENT_SCHEDULE ");
		sb.append(" ( APPOINTMENT_ID, START_DATE, END_DATE, LKUP_APPOINTMENT_STATUS_ID, LKUP_REVIEW_STATUS_ID, PARENT_ID, CREATED_BY, UPDATED_BY, CREATED_IP, UPDATED_IP, CREATED_DATE, UPDATED_DATE ) ");
		sb.append(" SELECT ");
		sb.append(" APPOINTMENT_ID ");
		sb.append(" , ");
		sb.append(" START_DATE ");
		sb.append(" , ");
		sb.append(" END_DATE ");
		sb.append(" , ");
		sb.append(" (SELECT TOP 1 ID FROM LKUP_APPOINTMENT_STATUS WHERE LOWER(STATUS) = LOWER('").append(Operator.sqlEscape(apptstatus)).append("') AND ACTIVE='Y') ");
		sb.append(" , ");
		sb.append(reviewstatusid);
		sb.append(" , ");
		sb.append(scheduleid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" ").append(d.sqlDatetime()).append(" ");
		sb.append(" , ");
		sb.append(" ").append(d.sqlDatetime()).append(" ");
		sb.append(" FROM ");
		sb.append(" APPOINTMENT_SCHEDULE ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(scheduleid);
		return sb.toString();
	}

	public static String createReschedule(int scheduleid, Timekeeper start, Timekeeper end, int userid, String ip, Timekeeper d) {
		if (scheduleid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ");
		sb.append(" APPOINTMENT_SCHEDULE ");
		sb.append(" ( APPOINTMENT_ID, START_DATE, END_DATE, LKUP_APPOINTMENT_STATUS_ID, REF_COMBOREVIEW_ACTION_ID, PARENT_ID, CREATED_BY, UPDATED_BY, CREATED_IP, UPDATED_IP, CREATED_DATE, UPDATED_DATE ) ");
		sb.append(" SELECT ");
		sb.append(" APPOINTMENT_ID ");
		sb.append(" , ");
		sb.append(start.sqlDatetime());
		sb.append(" , ");
		sb.append(end.sqlDatetime());
		sb.append(" , ");
		sb.append(" LKUP_APPOINTMENT_STATUS_ID ");
		sb.append(" , ");
		sb.append(" REF_COMBOREVIEW_ACTION_ID ");
		sb.append(" , ");
		sb.append(scheduleid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" ").append(d.sqlDatetime()).append(" ");
		sb.append(" , ");
		sb.append(" ").append(d.sqlDatetime()).append(" ");
		sb.append(" FROM ");
		sb.append(" APPOINTMENT_SCHEDULE ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(scheduleid);
		return sb.toString();
	}

	public static String updateStatus(int scheduleid, String status, int userid, String ip) {
		if (scheduleid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE APPOINTMENT_SCHEDULE SET ");
		sb.append(" LKUP_APPOINTMENT_STATUS_ID = ( SELECT ID FROM LKUP_APPOINTMENT_STATUS WHERE LOWER(STATUS) = LOWER('").append(Operator.sqlEscape(status)).append("') ) ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(scheduleid);
		return sb.toString();
	}

	public static String getSchedules(String scheduleid) {
		String[] sarr = Operator.split(scheduleid, "|");
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" S.* ");
		sb.append(" , ");
		sb.append(" ST.STATUS, ST.COMPLETE ");
		sb.append(" FROM ");
		sb.append(" APPOINTMENT_SCHEDULE AS S ");
		sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID ");
		sb.append(" WHERE ");
		sb.append(" S.ID IN (" );
		boolean empty = true;
		for (int i=0; i<sarr.length; i++) {
			int sint = Operator.toInt(sarr[i]);
			if (sint > 0) {
				if (!empty) { sb.append(" , "); }
				sb.append(sint);
				empty = false;
			}
		}
		sb.append(" ) ");
		if (empty) { return ""; }
		return sb.toString();
	}

	public static String getSchedule(int apptid, Timekeeper s, Timekeeper e, int refcomboactionid, int user, Timekeeper date) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM APPOINTMENT_SCHEDULE WHERE ");
		sb.append(" APPOINTMENT_ID = ").append(apptid);
		sb.append(" AND ");
		sb.append(" START_DATE = ").append(s.sqlDatetime());
		sb.append(" AND ");
		sb.append(" END_DATE = ").append(e.sqlDatetime());
		sb.append(" AND ");
		sb.append(" REF_COMBOREVIEW_ACTION_ID = ").append(refcomboactionid);
		sb.append(" AND ");
		sb.append(" UPDATED_BY = ").append(user);
		sb.append(" AND ");
		sb.append(" UPDATED_DATE = ").append(date.sqlDatetime());
		return sb.toString();
	}

	public static String addSchedule(int apptid, String startdate, String enddate, int statusid, int refcomboactionid, int parentid, int user, String ip, Timekeeper date) {
		if (!Operator.hasValue(startdate)) { return ""; }
		Timekeeper s = new Timekeeper();
		s.setTimestamp(startdate);
		Timekeeper e = new Timekeeper();
		if (Operator.hasValue(enddate)) {
			e.setTimestamp(enddate);
		}
		else {
			e = s.copy();
		}
		return addSchedule(apptid, s, e, statusid, refcomboactionid, parentid, user, ip, date);
	}

	public static String addSchedule(int apptid, Timekeeper startdate, Timekeeper enddate, int statusid, int refcomboactionid, int user, String ip, Timekeeper date) {
		return addSchedule(apptid, startdate, enddate, statusid, refcomboactionid, -1, user, ip, date);
	}

	public static String addSchedule(int apptid, String startdate, String enddate, int statusid, int refcomboactionid, int user, String ip, Timekeeper date) {
		return addSchedule(apptid, startdate, enddate, statusid, refcomboactionid, -1, user, ip, date);
	}

	public static String addSchedule(int apptid, Timekeeper s, Timekeeper e, int statusid, int refcomboactionid, int parentid, int user, String ip, Timekeeper date) {
		if (parentid < 1) { parentid = -1; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO APPOINTMENT_SCHEDULE (APPOINTMENT_ID, START_DATE, END_DATE, LKUP_APPOINTMENT_STATUS_ID, REF_COMBOREVIEW_ACTION_ID, PARENT_ID, CREATED_BY, CREATED_IP, CREATED_DATE, UPDATED_BY, UPDATED_IP, UPDATED_DATE) Output Inserted.ID VALUES ( ");
		sb.append(apptid);
		sb.append(" , ");
		sb.append(s.sqlDatetime());
		sb.append(" , ");
		sb.append(e.sqlDatetime());
		if (statusid < 0) {
			sb.append(" , ");
			sb.append(" (SELECT TOP 1 ID FROM LKUP_APPOINTMENT_STATUS WHERE ACTIVE = 'Y' AND DEFAULT_COMPLETE = 'Y') ");
		}
		else if (statusid > 0) {
			sb.append(" , ");
			sb.append(statusid);
		}
		else {
			sb.append(" , ");
			sb.append(" (SELECT TOP 1 ID FROM LKUP_APPOINTMENT_STATUS WHERE LOWER(STATUS) = 'scheduled' AND ACTIVE='Y') ");
		}
		sb.append(" , ");
		sb.append(refcomboactionid);
		sb.append(" , ");
		sb.append(parentid);
		sb.append(" , ");
		sb.append(user);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(user);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" ) ");
		return sb.toString();
	}

	public static String addSchedule1(int apptid, Timekeeper s, Timekeeper e, int user, String ip, Timekeeper date, int parentid) {
		if (parentid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO APPOINTMENT_SCHEDULE (APPOINTMENT_ID, START_DATE, END_DATE, LKUP_APPOINTMENT_STATUS_ID, LKUP_REVIEW_STATUS_ID, CREATED_BY, CREATED_IP, CREATED_DATE, UPDATED_BY, UPDATED_IP, UPDATED_DATE, ACTIVE, PARENT_ID) VALUES ( ");
		sb.append(apptid);
		sb.append(" , ");
		sb.append(s.sqlDatetime());
		sb.append(" , ");
		sb.append(e.sqlDatetime());

		sb.append(" , ");
		sb.append(" (SELECT LKUP_APPOINTMENT_STATUS_ID FROM APPOINTMENT_SCHEDULE WHERE ID = ").append(parentid).append(") ");
		sb.append(" , ");
		sb.append(" (SELECT LKUP_REVIEW_STATUS_ID FROM APPOINTMENT_SCHEDULE WHERE ID = ").append(parentid).append(") ");

		sb.append(" , ");
		sb.append(user);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(user);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(" 'Y' ");
		sb.append(" , ");
		sb.append(parentid);
		sb.append(" ) ");
		return sb.toString();
	}

	public static String addNote(int apptid, String note, int user, String ip, Timekeeper date) {
		StringBuilder sb = new StringBuilder();
		return sb.toString();
	}

	public static String getAppointmentTypes(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String maintable = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" A.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" A.TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" A.AVAILABILITY_ID ");
		sb.append(" FROM ");
		sb.append("   LKUP_APPOINTMENT_TYPE AS A ");
		sb.append(" WHERE ");
		sb.append("   A.ACTIVE = 'Y' ");
		if (Operator.hasValue(type) && typeid > 0) {
			sb.append(" UNION ");
			sb.append(" SELECT DISTINCT ");
			sb.append(" G.ID * -1 AS ID ");
			sb.append(" , ");
			sb.append(" G.ID * -1 AS VALUE ");
			sb.append(" , ");
			sb.append(" G.GROUP_NAME AS TEXT ");
			sb.append(" , ");
			sb.append(" G.DESCRIPTION ");
			sb.append(" , ");
			sb.append(" R.AVAILABILITY_ID ");
			sb.append(" FROM ");
			sb.append(maintable).append(" AS M ");
			sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON M.LKUP_").append(tableref).append("_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
			sb.append(" JOIN REF_").append(tableref).append("_REVIEW_GROUP AS REF ON T.ID = REF.LKUP_").append(tableref).append("_TYPE_ID AND REF.ACTIVE = 'Y' ");
			sb.append(" JOIN REVIEW_GROUP AS G ON REF.REVIEW_GROUP_ID = G.ID AND G.ACTIVE = 'Y' ");
			sb.append(" JOIN REVIEW AS R ON G.ID = R.REVIEW_GROUP_ID AND R.ACTIVE = 'Y' AND R.AVAILABILITY_ID > 0 ");
			sb.append(" JOIN LKUP_REVIEW_TYPE AS RT ON R.LKUP_REVIEW_TYPE_ID = RT.ID AND RT.ACTIVE = 'Y' ");
			sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON RT.ID = S.LKUP_REVIEW_TYPE_ID AND S.ACTIVE = 'Y' AND S.SCHEDULE_INSPECTION = 'Y' ");
			sb.append(" WHERE ");
			sb.append(" G.ACTIVE = 'Y' ");
			sb.append(" AND ");
			sb.append(" M.ID = ").append(typeid);
		}
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT * FROM Q ORDER BY Q.TEXT ");
		return sb.toString();
	}

	public static String getAppointmentScheduleStatus(int apptid, int reviewid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		if (reviewid <= 0) {
			sb.append(" SELECT ");
			sb.append(" S.ID ");
			sb.append(" , ");
			sb.append(" S.ID AS VALUE ");
			sb.append(" , ");
			sb.append(" S.STATUS AS TEXT ");
			sb.append(" , ");
			sb.append(" S.DESCRIPTION ");
			sb.append(" , ");
			sb.append(" S.DEFAULT_BEGIN AS SELECTED ");
			sb.append(" , ");
			sb.append(" CASE WHEN S.DEFAULT_BEGIN = 'Y' THEN 1000 ELSE 100 END AS POINTS ");
			sb.append(" FROM ");
			sb.append("   LKUP_APPOINTMENT_STATUS AS S ");
			sb.append(" WHERE ");
			sb.append("   S.ACTIVE = 'Y' ");
			sb.append("   AND ");
			sb.append("   S.SCHEDULED = 'Y' ");
			if (apptid < 1) {
				sb.append(" AND ");
				sb.append("   S.COMPLETE = 'N' ");
			}
		}
		else {
//			sb.append("UNION ");
			sb.append(" SELECT ");
			sb.append(" S.ID * -1 AS ID ");
			sb.append(" , ");
			sb.append(" S.ID * -1 AS VALUE ");
			sb.append(" , ");
			sb.append(" S.STATUS AS TEXT ");
			sb.append(" , ");
			sb.append(" S.DESCRIPTION ");
			sb.append(" , ");
			sb.append(" 'N' AS SELECTED ");
			sb.append(" , ");
			sb.append(" 10 AS POINTS ");
			sb.append(" FROM ");
			sb.append(" REVIEW AS R ");
			sb.append(" JOIN LKUP_REVIEW_TYPE AS T ON R.LKUP_REVIEW_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
			sb.append(" JOIN LKUP_REVIEW_STATUS AS S ON T.ID = S.LKUP_REVIEW_TYPE_ID AND T.ACTIVE = 'Y' ");
			sb.append(" WHERE ");
			sb.append(" S.SCHEDULE_INSPECTION = 'Y' ");
			sb.append(" AND ");
			sb.append(" T.ACTIVE = 'Y' ");
			sb.append(" AND ");
			sb.append(" R.ID = ").append(reviewid);
		}
		sb.append(" ) ");
		sb.append(" SELECT * FROM Q ORDER BY Q.POINTS DESC, Q.TEXT ");
		return sb.toString();
	}

	public static String my(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" CASE WHEN T.TYPE IS NOT NULL THEN T.ID ELSE (RG.ID*-1) END AS TYPE ");
		sb.append(" , ");
		sb.append(" CASE WHEN T.TYPE IS NOT NULL THEN T.TYPE ELSE RG.GROUP_NAME END AS TYPE_TEXT ");
		sb.append(" , ");
		sb.append(" CASE WHEN RV.NAME IS NOT NULL THEN CAST(RV.ID AS VARCHAR(10)) ELSE A.SUBJECT END AS SUBJECT ");
		sb.append(" , ");
		sb.append(" CASE WHEN RV.NAME IS NOT NULL THEN RV.NAME ELSE A.SUBJECT END AS SUBJECT_TEXT ");
		sb.append(" , ");
		sb.append(" S.START_DATE ");
		sb.append(" , ");
		sb.append(" S.END_DATE ");
		sb.append(" , ");
		sb.append(" ST.STATUS ");

		sb.append(" FROM ");
		sb.append(" APPOINTMENT AS A ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_APPOINTMENT_USERS AS RAU ON A.ID = RAU.APPOINTMENT_ID ");
		sb.append(" JOIN REF_USERS AS RU ON RAU.REF_USERS_ID = RU.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_APPOINTMENT_TYPE AS T ON A.LKUP_APPOINTMENT_TYPE_ID > 0 AND A.LKUP_APPOINTMENT_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REVIEW_GROUP AS RG ON A.LKUP_APPOINTMENT_TYPE_ID < 1 AND A.REVIEW_GROUP_ID = RG.ID AND RG.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REVIEW AS RV ON A.REVIEW_ID = RV.ID ");

		sb.append(" LEFT OUTER JOIN USERS AS U ON RU.USERS_ID = U.ID AND RU.USERS_ID > 0 AND LOWER(U.USERNAME) = LOWER('").append(username).append("') ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("   USERS_GROUP AS G ");
		sb.append("   JOIN REF_USERS_GROUP AS RUG ON G.ID = RUG.USERS_GROUP_ID ");
		sb.append("   JOIN USERS AS GU ON RUG.USERS_ID = GU.ID AND LOWER(GU.USERNAME) = LOWER('").append(username).append("') ");
		sb.append(" ) ON RU.USERS_GROUP_ID = G.ID AND RU.USERS_GROUP_ID > 0 AND RU.USERS_ID < 1 ");

		sb.append(" WHERE ");
		sb.append(" (U.ID IS NOT NULL OR GU.ID IS NOT NULL) ");
		sb.append(" AND ");
		sb.append(" (S.START_DATE > getDate() OR S.END_DATE > getDate()) ");
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" Q.ID ");
		sb.append(" , ");
		sb.append(" A.ID AS ACTIVITY_ID ");
		sb.append(" , ");
		sb.append(" A.ACT_NBR ");
		sb.append(" , ");
		sb.append(" Q.TYPE ");
		sb.append(" , ");
		sb.append(" Q.SUBJECT ");
		sb.append(" , ");
		sb.append(" Q.START_DATE ");
		sb.append(" , ");
		sb.append(" Q.END_DATE ");
		sb.append(" FROM ");
		sb.append(" Q ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append("     REF_ACT_APPOINTMENT ");
		sb.append("     JOIN ACTIVITY AS A ON REF_ACT_APPOINTMENT.ACTIVITY_ID = A.ID ");
		sb.append(" )  ON Q.ID = REF_ACT_APPOINTMENT.APPOINTMENT_ID ");

//		sb.append(" LEFT OUTER JOIN ( ");
//		sb.append("     REF_PROJECT_APPOINTMENT ON Q.ID = REF_PROJECT_APPOINTMENT.APPOINTMENT_ID ");
//		sb.append("     JOIN PROJECT AS P ON REF_PROJECT_APPOINTMENT.PROJECT_ID = P.ID ");
//		sb.append(" ) ");


		return sb.toString();
	}

	public static String getScheduleCollaborators(int schedid) {
		if (schedid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	U.ID AS USERS_ID, ");
		sb.append(" 	U.USERNAME, ");
		sb.append(" 	U.EMAIL, U.NOTIFY,");
		sb.append(" 	A.ACT_NBR, ");
		sb.append(" 	R.NAME AS REVIEW, ");
		sb.append(" 	S.REF_COMBOREVIEW_ACTION_ID, ");
		sb.append(" 	RS.STATUS, ");
		sb.append("     LTRIM(RTRIM( ");
		sb.append("         CASE ");
		sb.append("           WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("           ELSE '' ");
		sb.append("         END + ");
		sb.append("         CASE ");
		sb.append("           WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("           ELSE '' ");
		sb.append("         END + ");
		sb.append("         CASE ");
		sb.append("           WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("           ELSE '' ");
		sb.append("         END ");
		sb.append("     )) AS NAME ");
		sb.append(" FROM ");
		sb.append(" APPOINTMENT_SCHEDULE AS S ");
		sb.append(" JOIN REF_APPOINTMENT_USERS AS RU ON S.APPOINTMENT_ID = RU.APPOINTMENT_ID AND RU.ACTIVE = 'Y' AND S.ID = ").append(schedid);
		//sb.append(" JOIN USERS AS U ON RU.USERS_ID = U.ID ");
		sb.append(" LEFT OUTER JOIN ( ");
		sb.append(" 	REF_COMBOREVIEW_ACTION AS RCA ");
		sb.append(" 	JOIN LKUP_REVIEW_STATUS AS RS ON RCA.LKUP_REVIEW_STATUS_ID = RS.ID ");
		sb.append(" 	JOIN REF_COMBOREVIEW_REVIEW AS RCR ON RCA.REF_COMBOREVIEW_REVIEW_ID = RCR.ID ");
		sb.append(" 	JOIN REVIEW AS R ON RCR.REVIEW_ID = R.ID ");
		sb.append(" 	LEFT OUTER JOIN ( ");
		sb.append(" 		REF_ACT_COMBOREVIEW AS RAC ");
		sb.append(" 		JOIN ACTIVITY AS A ON RAC.ACTIVITY_ID = A.ID ");
		sb.append(" 	) ON RCR.COMBOREVIEW_ID = RAC.COMBOREVIEW_ID AND RAC.ACTIVE = 'Y' ");
		sb.append(" ) ON RCA.ID = S.REF_COMBOREVIEW_ACTION_ID ");
		
		//TODO verify with Alain before pushing
		sb.append(" LEFT OUTER JOIN ACTIVITY AC on RAC.ACTIVITY_ID= AC.ID ");
		sb.append(" LEFT OUTER join REF_ACT_USERS RAU on AC.ID=RAU.ACTIVITY_ID  AND RAU.ACTIVE='Y' ");
		sb.append(" LEFT OUTER JOIN   REF_PROJECT_USERS RAUP on AC.PROJECT_ID=RAUP.PROJECT_ID  AND RAUP.ACTIVE='Y'    ");
		sb.append(" LEFT OUTER JOIN REF_USERS RUU on (RAU.REF_USERS_ID=RUU.ID  OR      RAUP.REF_USERS_ID=RUU.ID   )  ");
		sb.append(" JOIN USERS AS U ON (RU.USERS_ID = U.ID  OR      RUU.USERS_ID = U.ID   ) ");
		
		return sb.toString();
	}

	public static String getCollaborators(String type, int typeid, int apptid) {
		if (!Operator.hasValue(type) && apptid < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);

		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		if (apptid > 0) {
			sb.append(" SELECT DISTINCT ");
			sb.append(" R.USERS_ID ");
			sb.append(" FROM ");
			sb.append(" REF_APPOINTMENT_USERS AS R ");
			sb.append(" WHERE ");
			sb.append(" R.APPOINTMENT_ID = ").append(apptid);
			sb.append(" AND ");
			sb.append(" R.ACTIVE = 'Y' ");
		}
		if (Operator.hasValue(type)) {

			if (apptid > 0) {
				sb.append(" UNION ");
			}

			if (Operator.equalsIgnoreCase(type, "activity")) {
				sb.append(" SELECT DISTINCT ");
				sb.append(" RU.USERS_ID ");
				sb.append(" FROM ");
				sb.append(" REF_ACT_USERS AS R ");
				sb.append(" JOIN REF_USERS AS RU ON R.REF_USERS_ID = RU.ID AND RU.ACTIVE = 'Y' ");
				sb.append(" WHERE ");
				sb.append(" R.ACTIVITY_ID = ").append(typeid);
				sb.append(" AND R.ACTIVE = 'Y' ");

				sb.append(" UNION ");

				sb.append(" SELECT DISTINCT ");
				sb.append(" RU.USERS_ID ");
				sb.append(" FROM ");
				sb.append(" ACTIVITY AS A ");
				sb.append(" JOIN REF_PROJECT_USERS AS R ON R.PROJECT_ID = A.PROJECT_ID AND R.ACTIVE = 'Y' ");
				sb.append(" JOIN REF_USERS AS RU ON R.REF_USERS_ID = RU.ID AND RU.ACTIVE = 'Y' ");
				sb.append(" WHERE ");
				sb.append(" A.ID = ").append(typeid);
			}
			else {
				sb.append(" SELECT DISTINCT ");
				sb.append(" RU.USERS_ID ");
				sb.append(" FROM ");
				sb.append(" REF_").append(tableref).append("_USERS AS R ");
				sb.append(" JOIN REF_USERS AS RU ON R.REF_USERS_ID = RU.ID AND RU.ACTIVE = 'Y' ");
				sb.append(" WHERE ");
				sb.append(" R.").append(idref).append(" = ").append(typeid);
				sb.append(" AND R.ACTIVE = 'Y' ");
			}
		}
		sb.append(" ) ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" U.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN RA.ID IS NOT NULL THEN 'Y' ");
		sb.append("   ELSE 'N' END AS SELECTED ");
		sb.append(" , ");
		sb.append("     LTRIM(RTRIM( ");
//		sb.append("     CASE  ");
//		sb.append("       WHEN U.ID IS NULL THEN G.USERGROUP + ' (' + T.TYPE + ') ' ");
//		sb.append("       ELSE ");
		sb.append("         CASE  ");
		sb.append("           WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("           ELSE ''  ");
		sb.append("         END +  ");
		sb.append("         CASE  ");
		sb.append("           WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("           ELSE ''  ");
		sb.append("         END +  ");
		sb.append("         CASE  ");
		sb.append("           WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("           ELSE '' ");
		sb.append("         END ");
//		sb.append("       END ");
		sb.append("     )) AS TEXT ");
//		sb.append(" , ");
//		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" U.EMAIL AS DESCRIPTION ");

		sb.append(" FROM ");
		sb.append(" Q ");
		sb.append(" JOIN USERS AS U ON Q.USERS_ID = U.ID ");
//		sb.append(" LEFT OUTER JOIN USERS_GROUP AS G ON U.ID = G.USERS_ID AND G.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REF_APPOINTMENT_USERS AS RA ON RA.USERS_ID = Q.USERS_ID AND RA.ACTIVE = 'Y' AND RA.APPOINTMENT_ID = ").append(apptid);
//		sb.append(" ORDER BY TYPE, TEXT ");

		return sb.toString();
	}

	public static String getReviewCollaborators(String type, int typeid, int reviewrefid) {
		if (!Operator.hasValue(type) && reviewrefid < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);

		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		if (reviewrefid > 0) {
			sb.append(" SELECT DISTINCT ");
			sb.append(" R.USERS_ID ");
			sb.append(" FROM ");
			sb.append(" REF_APPOINTMENT_USERS AS R ");
			sb.append(" JOIN APPOINTMENT AS A ON R.APPOINTMENT_ID = A.ID AND R.ACTIVE = 'Y' AND A.ACTIVE = 'Y' AND A.REF_COMBOREVIEW_REVIEW_ID = ").append(reviewrefid);
			sb.append(" JOIN USERS AS U ON R.USERS_ID = U.ID ");
		}
		if (Operator.hasValue(type)) {

			if (reviewrefid > 0) {
				sb.append(" UNION ");
			}

			if (Operator.equalsIgnoreCase(type, "activity")) {
				sb.append(" SELECT DISTINCT ");
				sb.append(" RU.USERS_ID ");
				sb.append(" FROM ");
				sb.append(" REF_ACT_USERS AS R ");
				sb.append(" JOIN REF_USERS AS RU ON R.REF_USERS_ID = RU.ID AND R.ACTIVE = 'Y' AND R.ACTIVITY_ID = ").append(typeid);

				sb.append(" UNION ");

				sb.append(" SELECT DISTINCT ");
				sb.append(" RU.USERS_ID ");
				sb.append(" FROM ");
				sb.append(" ACTIVITY AS A ");
				sb.append(" JOIN REF_PROJECT_USERS AS R ON R.PROJECT_ID = A.PROJECT_ID AND R.ACTIVE = 'Y' ");
				sb.append(" JOIN REF_USERS AS RU ON R.REF_USERS_ID = RU.ID ");
				sb.append(" WHERE ");
				sb.append(" A.ID = ").append(typeid);
			}
			else {
				sb.append(" SELECT DISTINCT ");
				sb.append(" RU.USERS_ID ");
				sb.append(" FROM ");
				sb.append(" REF_").append(tableref).append("_USERS AS R ");
				sb.append(" JOIN REF_USERS AS RU ON R.REF_USERS_ID = RU.ID AND R.ACTIVE = 'Y' AND R.").append(idref).append(" = ").append(typeid);
			}
		}
		sb.append(" ) ");
		sb.append(" , A AS ( ");
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	U.ID AS VALUE ");
		sb.append(" 	, ");
		sb.append(" 	LTRIM(RTRIM( ");
		sb.append(" 		CASE  ");
		sb.append(" 			WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append(" 			ELSE ''  ");
		sb.append(" 		END +  ");
		sb.append(" 		CASE  ");
		sb.append(" 			WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append(" 			ELSE ''  ");
		sb.append(" 		END +  ");
		sb.append(" 		CASE  ");
		sb.append(" 			WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append(" 			ELSE '' ");
		sb.append(" 		END ");
		sb.append(" 	)) AS TEXT ");
		sb.append(" 	, ");
		sb.append(" 	U.EMAIL AS DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	U.ACTIVE ");

		sb.append(" FROM ");
		sb.append(" 	Q ");
		sb.append(" 	JOIN USERS AS U ON Q.USERS_ID = U.ID ");
		sb.append(" ) ");
		sb.append(" , D AS ( ");
		sb.append(" 	SELECT DISTINCT * FROM A WHERE ACTIVE = 'Y' ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT DISTINCT * FROM A WHERE ACTIVE = 'N' AND A.DESCRIPTION NOT IN (SELECT DISTINCT DESCRIPTION FROM A WHERE ACTIVE = 'Y' ) ");
		sb.append(" ) ");

		sb.append(" SELECT ");
		sb.append(" 	MAX(A.VALUE) AS VALUE ");
		sb.append(" 	, ");
		sb.append(" 	TEXT ");
		sb.append(" 	, ");
		sb.append(" 	DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" 	A ");
		sb.append(" GROUP BY ");
		sb.append(" 	TEXT ");
		sb.append(" 	, ");
		sb.append(" 	DESCRIPTION ");
		sb.append(" ORDER BY TEXT ");

		return sb.toString();
	}


	public static String getTeam(String type, int typeid, int apptid, int appttypeid, int appttypesubid) {
		if (appttypeid < 1 && appttypesubid < 1) { return ""; }
		int reviewid = appttypesubid;
		if (!Operator.hasValue(type) && appttypesubid > 0) {
			type = "review";
			typeid = appttypesubid;
		}
		if (!Operator.hasValue(type) && apptid < 1) { return ""; }

		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		if (apptid > 0) {
			sb.append(" SELECT DISTINCT ");
			sb.append(" R.REF_TEAM_ID ");
			sb.append(" FROM ");
			sb.append(" REF_APPOINTMENT_TEAM AS R ");
			sb.append(" WHERE ");
			sb.append(" R.APPOINTMENT_ID = ").append(apptid);
			sb.append(" AND ");
			sb.append(" R.ACTIVE = 'Y' ");
		}

		if (Operator.hasValue(type)) {


			if (Operator.equalsIgnoreCase(type, "activity")) {

				if (apptid > 0) {
					sb.append(" UNION ");
				}
				sb.append(" SELECT DISTINCT ");
				sb.append(" R.REF_TEAM_ID ");
				sb.append(" FROM ");
				sb.append(" REF_ACT_TEAM AS R ");
				sb.append(" JOIN REF_TEAM AS T ON R.REF_TEAM_ID = T.ID AND T.ACTIVE = 'Y' ");
				sb.append(" JOIN USERS AS U ON T.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
				sb.append(" WHERE ");
				sb.append(" R.ACTIVITY_ID = ").append(typeid);
				sb.append(" AND R.ACTIVE = 'Y' ");

				sb.append(" UNION ");

				sb.append(" SELECT DISTINCT ");
				sb.append(" R.REF_TEAM_ID ");
				sb.append(" FROM ");
				sb.append(" ACTIVITY AS A ");
				sb.append(" JOIN REF_PROJECT_TEAM AS R ON R.PROJECT_ID = A.PROJECT_ID AND R.ACTIVE = 'Y' ");
				sb.append(" JOIN REF_TEAM AS T ON R.REF_TEAM_ID = T.ID AND T.ACTIVE = 'Y' ");
				sb.append(" JOIN USERS AS U ON T.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
				sb.append(" WHERE ");
				sb.append(" A.ID = ").append(typeid);

			}
			else if (Operator.equalsIgnoreCase(type, "project")) {
				if (apptid > 0) {
					sb.append(" UNION ");
				}
				sb.append(" SELECT DISTINCT ");
				sb.append(" R.REF_TEAM_ID ");
				sb.append(" FROM ");
				sb.append(" REF_PROJECT_TEAM AS R ");
				sb.append(" JOIN REF_TEAM AS T ON R.REF_TEAM_ID = T.ID AND T.ACTIVE = 'Y' AND R.ACTIVE = 'Y' ");
				sb.append(" JOIN USERS AS U ON T.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
				sb.append(" WHERE ");
				sb.append(" R.ID = ").append(typeid);
			}
			else {
//				sb.append(" SELECT DISTINCT ");
//				sb.append(" T.ID AS REF_TEAM_ID ");
//				sb.append(" FROM ");
//				sb.append(" REF_REVIEW_TEAM AS R ");
//				sb.append(" JOIN REF_TEAM AS T ON R.LKUP_TEAM_TYPE_ID = T.LKUP_TEAM_TYPE_ID AND T.ACTIVE = 'Y' ");
//				sb.append(" JOIN USERS AS U ON T.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
//				sb.append(" WHERE ");
//				sb.append(" R.REVIEW_ID = ").append(reviewid);
//				sb.append(" AND R.ACTIVE = 'Y' ");
			}
		}
		sb.append(" ) ");



		sb.append(" SELECT DISTINCT ");
		sb.append(" RT.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN RA.ID IS NOT NULL THEN 'Y' ");
		sb.append("   ELSE 'N' END AS SELECTED ");
		sb.append(" , ");
		sb.append("     LTRIM(RTRIM( ");
		sb.append("         CASE  ");
		sb.append("           WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("           ELSE ''  ");
		sb.append("         END +  ");
		sb.append("         CASE  ");
		sb.append("           WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("           ELSE ''  ");
		sb.append("         END +  ");
		sb.append("         CASE  ");
		sb.append("           WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("           ELSE '' ");
		sb.append("         END ");
		sb.append("     )) AS TEXT ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" T.TYPE AS DESCRIPTION");

		sb.append(" FROM ");
		sb.append(" Q ");
		sb.append(" JOIN REF_TEAM AS RT ON Q.REF_TEAM_ID = RT.ID AND RT.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_TEAM_TYPE AS T ON RT.LKUP_TEAM_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON RT.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REF_APPOINTMENT_TEAM AS RA ON RA.REF_TEAM_ID = Q.REF_TEAM_ID AND RA.ACTIVE = 'Y' AND RA.APPOINTMENT_ID = ").append(apptid);
		sb.append(" ORDER BY TYPE, TEXT ");

		return sb.toString();
	}

	public static String getAssignTeam(String type, int typeid, int reviewid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" RT.ID ");
		sb.append(" , ");
		sb.append(" LTT.TYPE ");
		sb.append(" , ");
		sb.append("     LTRIM(RTRIM( ");
		sb.append("         CASE  ");
		sb.append("           WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
		sb.append("           ELSE ''  ");
		sb.append("         END +  ");
		sb.append("         CASE  ");
		sb.append("           WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
		sb.append("           ELSE ''  ");
		sb.append("         END +  ");
		sb.append("         CASE  ");
		sb.append("           WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
		sb.append("           ELSE '' ");
		sb.append("         END + ");
		sb.append("         CASE  ");
		sb.append("           WHEN LTT.TYPE IS NOT NULL AND LTT.TYPE <> '' THEN ' (' + LTT.TYPE + ') ' ");
		sb.append("           ELSE '' ");
		sb.append("         END ");
		sb.append("     )) AS TEXT ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_TEAM AS R ");
		sb.append(" JOIN REF_TEAM AS RT ON R.REF_TEAM_ID = RT.ID AND RT.ACTIVE = 'Y' AND R.").append(idref).append(" = ").append(typeid);
		if (reviewid > 0) {
			sb.append(" JOIN REF_REVIEW_TEAM AS RRT ON RT.LKUP_TEAM_TYPE_ID = RRT.LKUP_TEAM_TYPE_ID AND RRT.ACTIVE = 'Y' AND RRT.REVIEW_ID = ").append(reviewid);
		}
		sb.append(" JOIN LKUP_TEAM_TYPE AS LTT ON RT.LKUP_TEAM_TYPE_ID = LTT.ID AND LTT.ACTIVE = 'Y' ");
		sb.append(" JOIN USERS AS U ON RT.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getDuplicateAppointment(String apptids, String type, int typeid, int reviewid, Timekeeper start, Timekeeper end) {
		String typeref = CsReflect.getTableRef(type);
		String typeidref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.LKUP_APPOINTMENT_TYPE_ID ");
		sb.append(" , ");
		sb.append(" A.SUBJECT ");
		sb.append(" , ");
		if (reviewid > 0) { 
			sb.append(" CRR.REVIEW_ID ");
			sb.append(" , ");
		}
		sb.append(" S.START_DATE ");
		sb.append(" , ");
		sb.append(" S.END_DATE ");
		sb.append(" , ");
		sb.append(" ST.STATUS ");
		sb.append(" FROM ");
		sb.append(" REF_").append(typeref).append("_APPOINTMENT AS RA ");
		sb.append(" JOIN APPOINTMENT AS A ON RA.APPOINTMENT_ID = A.ID AND RA.").append(typeidref).append(" = ").append(typeid).append(" AND RA.ACTIVE = 'Y'  AND A.ACTIVE = 'Y' ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' AND S.START_DATE = ").append(start.sqlDatetime()).append(" AND S.END_DATE = ").append(end.sqlDatetime());
		sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULED = 'Y' AND ST.COMPLETE = 'N' ");
		if (reviewid > 0) {
			sb.append(" JOIN ( ");
			sb.append("   REF_COMBOREVIEW_REVIEW AS CRR ");
			sb.append("   JOIN REF_COMBOREVIEW_ACTION AS CRA ON CRR.ID = CRA.REF_COMBOREVIEW_REVIEW_ID AND CRA.ACTIVE = 'Y' AND CRA.EXPIRED = 'N' ");
			sb.append(" ) ON CRR.ID = A.REF_COMBOREVIEW_REVIEW_ID AND CRR.ACTIVE = 'Y' AND CRR.REVIEW_ID = ").append(reviewid);
		}
		if (Operator.hasValue(apptids)) {
			sb.append(" WHERE ");
			String[] a = Operator.split(apptids, "|");
			sb.append(" A.ID NOT IN ( ");
			boolean empty = true;
			for (int i=0; i<a.length; i++) {
				int x = Operator.toInt(a[i], -1);
				if (x > 0) {
					if (!empty) { sb.append(" , "); }
					sb.append(x);
					empty = false;
				}
			}
			sb.append(" ) ");
		}
		return sb.toString();
	}

	public static String getMax(String type, int typeid, String apptid) {
		if (Operator.equalsIgnoreCase(type, "review")) {
			return getReviewMax(typeid, apptid);
		}
		String maintable = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		String typeidref = CsReflect.getFieldIdRef(type);

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" M.ID ");
		sb.append(" , ");
		sb.append(" T.ID AS TYPE_ID ");
		sb.append(" , ");
		sb.append(" T.MAX_ACTIVE_APPOINTMENTS ");
		sb.append(" , ");
		sb.append(" COUNT (DISTINCT S.ID) AS ACTIVE_APPOINTMENTS ");
		sb.append(" FROM ");
		sb.append(maintable).append(" AS M ");
		sb.append(" JOIN REF_").append(tableref).append("_APPOINTMENT AS RA ON M.ID = RA.").append(typeidref).append(" AND RA.ACTIVE = 'Y' AND M.ID = ").append(typeid);
		sb.append(" JOIN APPOINTMENT AS A ON RA.APPOINTMENT_ID = A.ID AND A.ACTIVE = 'Y' ");

		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_COMBOREVIEW_ACTION RCA on S.REF_COMBOREVIEW_ACTION_ID = RCA.ID AND RCA.EXPIRED='N'  ");
		if (Operator.hasValue(apptid)) {
			StringBuilder sc = new StringBuilder();
			sc.append(" AND ");
			String[] a = Operator.split(apptid, "|");
			sc.append(" A.ID NOT IN ( ");
			boolean empty = true;
			for (int i=0; i<a.length; i++) {
				int x = Operator.toInt(a[i], -1);
				if (x > 0) {
					if (!empty) { sc.append(" , "); }
					sc.append(x);
					empty = false;
				}
			}
			sc.append(" ) ");
			if (!empty) {
				sb.append(sc.toString());
			}
		}

		sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULED = 'Y' AND ST.COMPLETE = 'N' ");
		sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON M.LKUP_").append(tableref).append("_TYPE_ID = T.ID ");
		sb.append(" GROUP BY ");
		sb.append(" M.ID ");
		sb.append(" , ");
		sb.append(" T.ID ");
		sb.append(" , ");
		sb.append(" T.MAX_ACTIVE_APPOINTMENTS ");
		return sb.toString();
	}

	public static String getReviewMax(int reviewid, String apptids) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" R.ID ");
		sb.append(" , ");
		sb.append(" T.ID AS TYPE_ID ");
		sb.append(" , ");
		sb.append(" R.MAX_ACTIVE_APPOINTMENTS ");
		sb.append(" , ");
		sb.append(" COUNT (DISTINCT S.ID) AS ACTIVE_APPOINTMENTS ");
		sb.append(" FROM ");
		sb.append(" APPOINTMENT AS A ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS CR ON A.REF_COMBOREVIEW_REVIEW_ID = CR.ID AND CR.ACTIVE = 'Y' ");
		sb.append(" JOIN REVIEW AS R ON CR.REVIEW_ID = R.ID AND R.ACTIVE = 'Y' AND R.ID = ").append(reviewid);
		sb.append(" JOIN LKUP_REVIEW_TYPE AS T ON R.LKUP_REVIEW_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");

		if (Operator.hasValue(apptids)) {
			StringBuilder sc = new StringBuilder();
			sc.append(" AND ");
			String[] a = Operator.split(apptids, "|");
			sc.append(" A.ID NOT IN ( ");
			boolean empty = true;
			for (int i=0; i<a.length; i++) {
				int x = Operator.toInt(a[i], -1);
				if (x > 0) {
					if (!empty) { sc.append(" , "); }
					sc.append(x);
					empty = false;
				}
			}
			sc.append(" ) ");
			if (!empty) {
				sb.append(sc.toString());
			}
		}
		sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID AND ST.ACTIVE = 'Y' AND ST.SCHEDULED = 'Y' AND ST.COMPLETE = 'N' ");

		sb.append(" GROUP BY ");
		sb.append(" R.ID ");
		sb.append(" , ");
		sb.append(" T.ID ");
		sb.append(" , ");
		sb.append(" R.MAX_ACTIVE_APPOINTMENTS ");

		return sb.toString();
	}

	public static String getMaxChildDay(int projid, Timekeeper start, Timekeeper end, String apptid) {
		// Make sure current schedule is not counted
		StringBuilder exc = new StringBuilder();
		if (Operator.hasValue(apptid)) {
			StringBuilder sc = new StringBuilder();
			sc.append(" AND ");
			String[] a = Operator.split(apptid, "|");
			sc.append(" A.ID NOT IN ( ");
			boolean empty = true;
			for (int i=0; i<a.length; i++) {
				int x = Operator.toInt(a[i], -1);
				if (x > 0) {
					if (!empty) { sc.append(" , "); }
					sc.append(x);
					empty = false;
				}
			}
			sc.append(" ) ");
			if (!empty) {
				exc.append(sc.toString());
			}
		}
		String exclude = exc.toString();

		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 		").append(projid).append(" AS PROJECT_ID, ");
		sb.append(" 		A.ID AS APPOINTMENT_ID, ");
		sb.append(" 		S.ID AS SCHEDULE_ID, ");
		sb.append(" 		CONVERT(date, S.START_DATE) AS START_DATE, ");
		sb.append(" 		CONVERT(date, S.END_DATE) AS END_DATE ");
		sb.append(" 	FROM ");
		sb.append(" 		REVIEW AS R ");
		sb.append(" 		JOIN REF_COMBOREVIEW_REVIEW AS RCR ON R.ID = RCR.REVIEW_ID AND RCR.ACTIVE = 'Y' AND R.ACTIVE = 'Y' ");
		sb.append(" 		JOIN APPOINTMENT AS A ON A.REF_COMBOREVIEW_REVIEW_ID > 0 AND RCR.ID = A.REF_COMBOREVIEW_REVIEW_ID AND A.ACTIVE = 'Y' ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");
		sb.append(exclude);
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS APPTS ON S.LKUP_APPOINTMENT_STATUS_ID = APPTS.ID AND APPTS.SCHEDULED = 'Y' AND APPTS.COMPLETE = 'N' ");
		sb.append(" 		JOIN REF_ACT_COMBOREVIEW AS RACT ON RACT.COMBOREVIEW_ID = RCR.COMBOREVIEW_ID AND RACT.ACTIVE = 'Y' ");
		sb.append(" 		JOIN ACTIVITY AS ACT ON RACT.ACTIVITY_ID = ACT.ID AND ACT.ACTIVE = 'Y' AND ACT.PROJECT_ID = ").append(projid).append(" ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		").append(projid).append(" AS PROJECT_ID, ");
		sb.append(" 		A.ID AS APPOINTMENT_ID, ");
		sb.append(" 		S.ID AS SCHEDULE_ID, ");
		sb.append(" 		CONVERT(date, S.START_DATE) AS START_DATE, ");
		sb.append(" 		CONVERT(date, S.END_DATE) AS END_DATE ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_ACT_APPOINTMENT AS RAA ");
		sb.append(" 		JOIN APPOINTMENT AS A ON RAA.APPOINTMENT_ID = A.ID AND A.ACTIVE = 'Y' ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");
		sb.append(exclude);
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS APPTS ON S.LKUP_APPOINTMENT_STATUS_ID = APPTS.ID AND APPTS.SCHEDULED = 'Y' AND APPTS.COMPLETE = 'N' ");
		sb.append(" 		JOIN ACTIVITY AS ACT ON RAA.ACTIVITY_ID = ACT.ID AND RAA.ACTIVE = 'Y' AND ACT.ACTIVE = 'Y' AND ACT.PROJECT_ID = ").append(projid).append(" ");
		sb.append(" 	UNION ");
		sb.append(" 	SELECT ");
		sb.append(" 		").append(projid).append(" AS PROJECT_ID, ");
		sb.append(" 		A.ID AS APPOINTMENT_ID, ");
		sb.append(" 		S.ID AS SCHEDULE_ID, ");
		sb.append(" 		CONVERT(date, S.START_DATE) AS START_DATE, ");
		sb.append(" 		CONVERT(date, S.END_DATE) AS END_DATE ");
		sb.append(" 	FROM ");
		sb.append(" 		REF_PROJECT_APPOINTMENT AS RPA ");
		sb.append(" 		JOIN APPOINTMENT AS A ON RPA.APPOINTMENT_ID = A.ID AND A.ACTIVE = 'Y' AND RPA.PROJECT_ID = ").append(projid).append(" ");
		sb.append(" 		JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");
		sb.append(exclude);
		sb.append(" 		JOIN LKUP_APPOINTMENT_STATUS AS APPTS ON S.LKUP_APPOINTMENT_STATUS_ID = APPTS.ID AND APPTS.SCHEDULED = 'Y' AND APPTS.COMPLETE = 'N' ");
		sb.append(" ) ");
		sb.append(" , C AS ( ");
		sb.append(" SELECT PROJECT_ID, COUNT(DISTINCT APPOINTMENT_ID) AS TOTAL_ACTIVE FROM Q GROUP BY PROJECT_ID ");
		sb.append(" ) ");
		sb.append(" , AU AS ( ");
		sb.append(" SELECT DISTINCT * FROM Q WHERE (CONVERT(date, Q.START_DATE) >= ").append(start.mssql()).append(" AND CONVERT(date, Q.START_DATE) <= ").append(end.mssql()).append(") ");
		sb.append(" ) ");

		sb.append(" SELECT ");
		sb.append(" 	T.MAX_CHILD_APPOINTMENTS ");
		sb.append(" 	, ");
		sb.append(" 	T.MAX_CHILD_APPOINTMENTS_DAY ");
		sb.append(" 	, ");
		sb.append(" 	C.TOTAL_ACTIVE ");
		sb.append(" 	, ");
		sb.append(" 	AU.START_DATE AS DATE ");
		sb.append(" 	, ");
		sb.append(" 	COUNT(DISTINCT AU.APPOINTMENT_ID) AS APPOINTMENTS ");
		sb.append(" FROM ");
		sb.append(" 	PROJECT AS P ");
		sb.append(" 	JOIN LKUP_PROJECT_TYPE AS T ON P.LKUP_PROJECT_TYPE_ID = T.ID ");
		sb.append(" 	LEFT OUTER JOIN AU ON P.ID = AU.PROJECT_ID ");
		sb.append(" 	LEFT OUTER JOIN C ON P.ID = C.PROJECT_ID ");

		sb.append(" WHERE ");
		sb.append(" 	P.ID = ").append(projid).append(" ");
		sb.append(" GROUP BY ");
		sb.append(" 	T.MAX_CHILD_APPOINTMENTS, T.MAX_CHILD_APPOINTMENTS_DAY, C.TOTAL_ACTIVE, AU.START_DATE ");
		return sb.toString();
	}

	public static String getMaxChildDay(String type, int typeid, Timekeeper start, Timekeeper end, String scheduleid) {
		if (!Operator.equalsIgnoreCase(type, "activity")) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append("   SELECT PROJECT_ID FROM ACTIVITY WHERE ID = ").append(typeid);
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" T.MAX_CHILD_APPOINTMENTS_DAY ");
		sb.append(" , ");
		sb.append(" CONVERT(date, S.START_DATE) AS DATE ");
		sb.append(" , ");
		sb.append(" COUNT(DISTINCT S.ID) AS APPOINTMENTS ");
		sb.append(" FROM ");
		sb.append(" PROJECT AS P ");
		sb.append(" JOIN Q ON P.ID = Q.PROJECT_ID ");
		sb.append(" JOIN LKUP_PROJECT_TYPE AS T ON P.LKUP_PROJECT_TYPE_ID = T.ID ");
		sb.append(" JOIN ACTIVITY AS A ON P.ID = A.PROJECT_ID AND A.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_ACT_TYPE AS AT ON A.LKUP_ACT_TYPE_ID = AT.ID ");
		sb.append(" JOIN REF_ACT_APPOINTMENT AS RAPPT ON A.ID = RAPPT.ACTIVITY_ID AND RAPPT.ACTIVE = 'Y' ");
		sb.append(" JOIN APPOINTMENT AS APPT ON RAPPT.APPOINTMENT_ID = APPT.ID AND APPT.ACTIVE = 'Y' ");

		// Make sure that only appointments with availability are counted
		sb.append(" JOIN ( ");
		sb.append("   AVAILABILITY AS AV ");
		sb.append("   LEFT OUTER JOIN REVIEW AS RV ON AV.ID = RV.AVAILABILITY_ID ");
		sb.append(" ) ON (APPT.REVIEW_ID < 1 AND AV.ID = AT.AVAILABILITY_ID AND RV.ID IS NULL) OR (APPT.REVIEW_ID > 0 AND RV.ID IS NOT NULL AND RV.ID = APPT.REVIEW_ID) ");

		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON APPT.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" ( ");
		sb.append(" (CONVERT(date, S.START_DATE) >= ").append(start.sqlServer()).append(" AND CONVERT(date, S.START_DATE) <= ").append(end.sqlServer()).append(") ");
		sb.append(" ) ");

		// Make sure current schedule is not counted
		if (Operator.hasValue(scheduleid)) {
			if (Operator.hasValue(scheduleid)) {
				StringBuilder sc = new StringBuilder();
				sc.append(" AND ");
				String[] a = Operator.split(scheduleid, "|");
				sc.append(" S.ID NOT IN ( ");
				boolean empty = true;
				for (int i=0; i<a.length; i++) {
					int x = Operator.toInt(a[i], -1);
					if (x > 0) {
						if (!empty) { sc.append(" , "); }
						sc.append(x);
						empty = false;
					}
				}
				sc.append(" ) ");
				if (!empty) {
					sb.append(sc.toString());
				}
			}
		}

		sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS APPTS ON S.LKUP_APPOINTMENT_STATUS_ID = APPTS.ID AND APPTS.ACTIVE = 'Y' AND APPTS.SCHEDULED = 'Y' ");

		sb.append(" GROUP BY ");
		sb.append(" T.MAX_CHILD_APPOINTMENTS_DAY ");
		sb.append(" , ");
		sb.append(" CONVERT(date, S.START_DATE) ");
		return sb.toString();
	}

	public static String deactivateTeam(String[] apptids, int userid, String ip) {
		if (!Operator.hasValue(apptids)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_APPOINTMENT_TEAM SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" APPOINTMENT_ID IN ( ");
		sb.append(Operator.join(apptids, ","));
		sb.append(" ) ");
		return sb.toString();
	}

	public static String addTeam(int apptid, int refteamid, int userid, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_APPOINTMENT_TEAM ( ");
		sb.append("   APPOINTMENT_ID, REF_TEAM_ID, CREATED_BY, UPDATED_BY, CREATED_IP, UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(apptid);
		sb.append(" , ");
		sb.append(refteamid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getCollaborators(int appointmentid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" U.* ");
		sb.append(" FROM ");
		sb.append(" REF_APPOINTMENT_USERS AS R ");
		sb.append(" JOIN USERS AS U ON R.USERS_ID = U.ID AND R.ACTIVE = 'Y' AND U.ACTIVE = 'Y' AND R.APPOINTMENT_ID = ").append(appointmentid);
		return sb.toString();
	}



}















