package csapi.impl.communications;

import csapi.utils.CsReflect;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

public class CommunicationsSQL {

	public static String summary(String type, int typeid, int id, String option) {
		if (!Operator.hasValue(type)) { return ""; }
		if (typeid < 1 && id < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		if (Operator.equalsIgnoreCase(type, "review")) {
			tableref = "COMBOREVIEW";
			idref = "REF_COMBOREVIEW_ACTION_ID";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH ");
		sb.append(" C AS ( SELECT COUNT(DISTINCT ID) AS CONTENT_COUNT FROM CONTENT WHERE ACTIVE = 'Y' AND LOWER(TYPE) = 'communications') ");
		sb.append(" , Q AS ( ");
		sb.append(" 	SELECT ");
		sb.append(" 	N.ID ");
		sb.append(" 	, ");
		sb.append(" 	N.ID AS VALUE ");
		sb.append(" 	, ");
		sb.append(" 	N.ID AS VIEW_CONTENT ");
		sb.append(" 	, ");
		sb.append(" 	N.RECIPIENT AS TEXT ");
		sb.append(" 	, ");
		sb.append(" 	N.ID AS NOTIFY_ID ");
		sb.append(" 	, ");
		sb.append(" 	N.RECIPIENT ");
		sb.append(" 	, ");
		sb.append(" 	N.SUBJECT ");
		sb.append(" 	, ");
		sb.append(" 	N.CONTENT ");
		sb.append(" 	, ");
		sb.append(" 	'").append(Operator.sqlEscape(type)).append("' AS REF ");
		sb.append(" 	, ");
		sb.append(" 	U.USERNAME AS CREATED_BY ");
		sb.append(" 	, ");
		sb.append(" 	U.USERNAME AS SENT_BY ");
		sb.append(" 	, ");
		sb.append(" 	CAST(N.CREATED_DATE AS DATE) AS DATE ");
		sb.append(" 	, ");
		sb.append(" 	CAST(N.CREATED_DATE AS TIME) AS TIME ");
		sb.append(" 	, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN C.CONTENT_COUNT IS NULL THEN '' ");
		sb.append(" 		WHEN C.CONTENT_COUNT > 0 THEN 'communications' ");
		sb.append(" 	ELSE '' END AS CONTENT_TYPE ");
		sb.append(" FROM ");
		sb.append(" 	REF_").append(tableref).append("_NOTIFY AS RN ");
		sb.append(" 	JOIN NOTIFY AS N ON RN.NOTIFY_ID = N.ID AND RN.ACTIVE = 'Y' ");
		sb.append(" 	LEFT OUTER JOIN USERS AS U ON N.CREATED_BY = U.ID ");
		sb.append(" 	LEFT OUTER JOIN C ON 1 = 1 ");
		sb.append(" WHERE ");
		sb.append(" 	N.ACTIVE = 'Y' ");
		sb.append(" 	AND ");
		sb.append(" 	RN.").append(idref).append(" = ").append(typeid);

		sb.append(" UNION ");

		sb.append(" SELECT ");
		sb.append(" 	N.ID ");
		sb.append(" 	, ");
		sb.append(" 	N.ID AS VALUE ");
		sb.append(" 	, ");
		sb.append(" 	N.ID AS VIEW_CONTENT ");
		sb.append(" 	, ");
		sb.append(" 	N.RECIPIENT AS TEXT ");
		sb.append(" 	, ");
		sb.append(" 	N.ID AS NOTIFY_ID ");
		sb.append(" 	, ");
		sb.append(" 	N.RECIPIENT ");
		sb.append(" 	, ");
		sb.append(" 	N.SUBJECT ");
		sb.append(" 	, ");
		sb.append(" 	N.CONTENT ");
		sb.append(" 	, ");
		sb.append(" 	'notes' AS REF ");
		sb.append(" 	, ");
		sb.append(" 	U.USERNAME AS CREATED_BY ");
		sb.append(" 	, ");
		sb.append(" 	U.USERNAME AS SENT_BY ");
		sb.append(" 	, ");
		sb.append(" 	CAST(N.CREATED_DATE AS DATE) AS DATE ");
		sb.append(" 	, ");
		sb.append(" 	CAST(N.CREATED_DATE AS TIME) AS TIME ");
		sb.append(" 	, ");
		sb.append(" 	CASE ");
		sb.append(" 		WHEN C.CONTENT_COUNT IS NULL THEN '' ");
		sb.append(" 		WHEN C.CONTENT_COUNT > 0 THEN 'communications' ");
		sb.append(" 	ELSE '' END AS CONTENT_TYPE ");
		sb.append(" FROM ");
		sb.append(" 	REF_NOTES_NOTIFY AS R ");
		sb.append(" 	JOIN REF_").append(tableref).append("_NOTES AS MR ON MR.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND R.NOTES_ID = MR.NOTES_ID AND MR.").append(idref).append(" = ").append(typeid);
		sb.append(" 	JOIN NOTIFY AS N ON R.NOTIFY_ID = N.ID AND R.ACTIVE = 'Y' ");
		sb.append(" 	LEFT OUTER JOIN USERS AS U ON N.CREATED_BY = U.ID ");
		sb.append(" 	LEFT OUTER JOIN C ON 1 = 1 ");

		if (Operator.equalsIgnoreCase(type, "activity") || Operator.equalsIgnoreCase(type, "project")) {
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" 	N.ID ");
			sb.append(" 	, ");
			sb.append(" 	N.ID AS VALUE ");
			sb.append(" 	, ");
			sb.append(" 	N.ID AS VIEW_CONTENT ");
			sb.append(" 	, ");
			sb.append(" 	N.RECIPIENT AS TEXT ");
			sb.append(" 	, ");
			sb.append(" 	N.ID AS NOTIFY_ID ");
			sb.append(" 	, ");
			sb.append(" 	N.RECIPIENT ");
			sb.append(" 	, ");
			sb.append(" 	N.SUBJECT ");
			sb.append(" 	, ");
			sb.append(" 	N.CONTENT ");
			sb.append(" 	, ");
			sb.append(" 	'review' AS REF ");
			sb.append(" 	, ");
			sb.append(" 	U.USERNAME AS CREATED_BY ");
			sb.append(" 	, ");
			sb.append(" 	U.USERNAME AS SENT_BY ");
			sb.append(" 	, ");
			sb.append(" 	CAST(N.CREATED_DATE AS DATE) AS DATE ");
			sb.append(" 	, ");
			sb.append(" 	CAST(N.CREATED_DATE AS TIME) AS TIME ");
			sb.append(" 	, ");
			sb.append(" 	CASE ");
			sb.append(" 		WHEN C.CONTENT_COUNT IS NULL THEN '' ");
			sb.append(" 		WHEN C.CONTENT_COUNT > 0 THEN 'communications' ");
			sb.append(" 	ELSE '' END AS CONTENT_TYPE ");
			sb.append(" FROM ");
			sb.append(" 	REF_COMBOREVIEW_NOTIFY AS R ");
			sb.append(" 	JOIN REF_COMBOREVIEW_ACTION AS RCA ON R.REF_COMBOREVIEW_ACTION_ID = RCA.ID AND R.ACTIVE = 'Y' ");
			sb.append(" 	JOIN REF_COMBOREVIEW_REVIEW AS RCR ON RCA.REF_COMBOREVIEW_REVIEW_ID = RCR.ID ");
			sb.append(" 	JOIN REF_").append(tableref).append("_COMBOREVIEW AS MR ON MR.ACTIVE = 'Y' AND RCR.COMBOREVIEW_ID = MR.COMBOREVIEW_ID AND MR.").append(idref).append(" = ").append(typeid);
			sb.append(" 	JOIN NOTIFY AS N ON R.NOTIFY_ID = N.ID AND R.ACTIVE = 'Y' ");
			sb.append(" 	LEFT OUTER JOIN USERS AS U ON N.CREATED_BY = U.ID ");
			sb.append(" 	LEFT OUTER JOIN C ON 1 = 1 ");
		}

		sb.append(" ) ");

		sb.append(" SELECT ");
		if (Operator.equalsIgnoreCase(option, "50")) {
			sb.append(" TOP 50 ");
		}
		else if (Operator.equalsIgnoreCase(option, "All")) {
		}
		else {
			sb.append(" TOP 10 ");
		}
		sb.append(" * FROM Q ");
		if (Operator.equalsIgnoreCase(option, "notes")) {
			sb.append(" WHERE ");
			sb.append(" Q.REF = 'notes' ");
		}
		if (Operator.equalsIgnoreCase(option, "review")) {
			sb.append(" WHERE ");
			sb.append(" Q.REF = 'review' ");
		}
		sb.append(" ORDER BY ");
		sb.append(" DATE DESC, TIME DESC ");
		return sb.toString();
	}

	public static String addRef(String type, int typeid, int notifyid, int creator, String ip) {
		if (!Operator.hasValue(type)) { return ""; }
		if (typeid < 1) { return ""; }
		if (notifyid < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		if (Operator.equalsIgnoreCase(type, "review")) {
			tableref = "COMBOREVIEW";
			idref = "REF_COMBOREVIEW_ACTION_ID";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_NOTIFY ( ");
		sb.append(" ").append(idref);
		sb.append(" , ");
		sb.append(" NOTIFY_ID ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(typeid);
		sb.append(" , ");
		sb.append(notifyid);
		sb.append(" , ");
		sb.append(creator);
		sb.append(" , ");
		sb.append(creator);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String addNotify(String recipient, int usersid, String subject, String content, int creator, String ip,String source) {
		if (!Operator.hasValue(recipient)) { return ""; }
		if (!Operator.hasValue(content)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO NOTIFY ( ");
		sb.append(" RECIPIENT ");
		sb.append(" , ");
		sb.append(" USERS_ID ");
		sb.append(" , ");
		sb.append(" SUBJECT ");
		sb.append(" , ");
		sb.append(" CONTENT ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" , ");
		sb.append(" SOURCE ");
		sb.append(" ) Output Inserted.ID VALUES ( ");
		sb.append(" '").append(Operator.sqlEscape(recipient)).append("' ");
		sb.append(" , ");
		sb.append(usersid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(subject)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(content)).append("' ");
		sb.append(" , ");
		sb.append(creator);
		sb.append(" , ");
		sb.append(creator);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(source)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getNotification(int notifyid) {
		if (notifyid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" N.ID ");
		sb.append(" , ");
		sb.append(" N.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" N.RECIPIENT AS TEXT ");
		sb.append(" , ");
		sb.append(" N.SUBJECT ");
		sb.append(" , ");
		sb.append(" N.CONTENT ");
		sb.append(" , ");
		sb.append(" CONVERT(VARCHAR, N.CREATED_DATE, 107) AS DATE ");
		sb.append(" , ");
		sb.append(" CAST(N.CREATED_DATE AS TIME) AS TIME ");
		sb.append(" FROM ");
		sb.append(" NOTIFY AS N ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(notifyid);
		return sb.toString();
	}

	public static String getNotifications(String type, int typeid) {
		return getNotifications(type, typeid, -1, "");
	}

	public static String getNotifications(String type, int typeid, int id) {
		return getNotifications(type, typeid, typeid, "");
	}

	public static String getNotifications(String type, int typeid, int id, String option) {
		if (!Operator.hasValue(type)) { return ""; }
		if (typeid < 1 && id < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		if (Operator.equalsIgnoreCase(type, "review")) {
			tableref = "COMBOREVIEW";
			idref = "REF_COMBOREVIEW_ACTION_ID";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		if (Operator.equalsIgnoreCase(option, "50")) {
			sb.append(" TOP 50 ");
		}
		else if (Operator.equalsIgnoreCase(option, "All")) {
		}
		else {
			sb.append(" TOP 10 ");
		}
		sb.append(" N.ID ");
		sb.append(" , ");
		sb.append(" N.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" N.ID AS VIEW_CONTENT ");
		sb.append(" , ");
		sb.append(" N.RECIPIENT AS TEXT ");
		sb.append(" , ");
		sb.append(" N.ID AS NOTIFY_ID ");
		sb.append(" , ");
		sb.append(" N.RECIPIENT ");
		sb.append(" , ");
		sb.append(" N.SUBJECT ");
		sb.append(" , ");
		sb.append(" N.CONTENT ");
		sb.append(" , ");
		sb.append(" U.USERNAME AS CREATED_BY ");
		sb.append(" , ");
		sb.append(" U.USERNAME AS SENT_BY ");
		sb.append(" , ");
		sb.append(" CONVERT(VARCHAR, N.CREATED_DATE, 107) AS DATE ");
		sb.append(" , ");
		sb.append(" CAST(N.CREATED_DATE AS TIME) AS TIME ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_NOTIFY AS RN ");
		sb.append(" JOIN NOTIFY AS N ON RN.NOTIFY_ID = N.ID AND RN.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON N.CREATED_BY = U.ID ");
		sb.append(" WHERE ");
		sb.append(" N.ACTIVE = 'Y' ");
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" N.ID = ").append(id);
		}
		else {
			sb.append(" AND ");
			sb.append(" RN.").append(idref).append(" = ").append(typeid);
		}
		sb.append(" ORDER BY ");
		sb.append(" N.CREATED_DATE DESC ");
		return sb.toString();
	}


}
























