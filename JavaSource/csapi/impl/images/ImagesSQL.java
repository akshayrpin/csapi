package csapi.impl.images;

import alain.core.security.Token;
import alain.core.utils.Operator;
import csapi.impl.entity.EntityAgent;
import csapi.utils.CsApiConfig;
import csapi.utils.CsReflect;
import csshared.vo.TypeInfo;

public class ImagesSQL {

	public static String info(String type, int typeid, int id, Token u) {
		return summary(type, typeid, id, u);
	}

	public static String summary(String type, int typeid, int id, Token u) {
		String q = query(type, typeid, id, u);
		if (!Operator.hasValue(q)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH ");
		sb.append(" C AS ( SELECT COUNT(DISTINCT ID) AS CONTENT_COUNT FROM CONTENT WHERE ACTIVE = 'Y' AND LOWER(TYPE) = 'images') ");
		sb.append(" , Q AS ( ");
		sb.append(q);
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append(" Q.* ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append("   WHEN C.CONTENT_COUNT IS NULL THEN '' ");
		sb.append("   WHEN C.CONTENT_COUNT > 0 THEN 'images' ");
		sb.append(" ELSE '' END AS CONTENT_TYPE ");
		sb.append(" FROM ");
		sb.append(" Q ");
		sb.append(" LEFT OUTER JOIN C ON 1 = 1 ");
		sb.append(" WHERE ");
		sb.append(" Q.IMAGE = 'Y' ");
		sb.append(" ORDER BY CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String query(String type, int typeid, int id, Token u) {
		if (!Operator.hasValue(type)) { return ""; }
		else if (Operator.equalsIgnoreCase(type, "activity")) { return activitySummary(typeid, u); }
		else if (Operator.equalsIgnoreCase(type, "project")) { return projectSummary(typeid, u); }
		else if (Operator.equalsIgnoreCase(type, "lso")) { return lsoSummary(typeid, u); }
		String tableref = CsReflect.getTableRef(type);
		String maintable = CsReflect.getMainTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" M.ID AS REF_ID ");
		sb.append(" , ");
		sb.append(" '").append(type).append("' AS REF ");
		if (Operator.equalsIgnoreCase(type, "ACTIVITY")) {
			sb.append(" , ");
			sb.append(" ACT_NBR AS REF_NBR ");
		}
		else if (Operator.equalsIgnoreCase(type, "PROJECT")) {
			sb.append(" , ");
			sb.append(" PROJECT_NBR AS REF_NBR ");
		}
		else {
			sb.append(" , ");
			sb.append(" CAST(M.ID AS VARCHAR(15)) AS REF_NBR ");
		}
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		//sb.append("'").append(Config.getString("files.storage_url")).append("'+ A.PATH as FILEURL ");
		sb.append("'").append("/cs").append("'+'/viewfile.jsp?_id='+ CAST(A.ID as varchar(10)) as FILEURL ");

		sb.append(" , ");
		sb.append(" A.KEYWORD1 ");
		sb.append(" , ");
		sb.append(" A.KEYWORD2 ");
		sb.append(" , ");
		sb.append(" A.KEYWORD3 ");
		sb.append(" , ");
		sb.append(" A.KEYWORD4 ");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" R.ATTACHMENT_ID ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" T.TYPE AS LKUP_ATTACHMENTS_TYPE_TEXT");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" R.UPDATED_DATE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_ATTACHMENTS R ");
		sb.append(" JOIN ").append(maintable).append(" AS M ON R.").append(idref).append(" = M.ID ");
		sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND R.ACTIVE = 'Y' ");
		if (!u.isStaff()) {
			sb.append(" AND A.ISPUBLIC = 'Y' ");
		}
		sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");
		sb.append(" WHERE R.ACTIVE = 'Y'  ");
		sb.append(" AND  ");
		sb.append(" A.ACTIVE = 'Y' ");
		sb.append(" AND  ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" R.ATTACHMENT_ID = ").append(id);
		}
		return sb.toString();
	}

	public static String activitySummary(int actid, Token u) {
		if (actid < 1) { return ""; }
		TypeInfo entity = EntityAgent.getEntity("activity", actid);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");

		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" 'activity' AS REF ");
		sb.append(" , ");
		sb.append(" M.ID AS REF_ID ");
		sb.append(" , ");
		sb.append(" M.ACT_NBR AS REF_NBR ");
		sb.append(" , ");
		sb.append(" M.ACT_NBR ");
		sb.append(" , ");
		sb.append(" P.PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" ATY.TYPE AS ACTIVITY_TYPE ");
		sb.append(" , ");
		sb.append(" PTY.TYPE AS PROJECT_TYPE ");
		sb.append(" , ");
		sb.append(" '' AS REVIEW ");
		sb.append(" , ");
		sb.append(" '' AS REVIEW_GROUP ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
		sb.append(" ELSE 'N' END AS IMAGE ");

		sb.append(" FROM ");
		sb.append(" ACTIVITY AS M ");
		sb.append(" JOIN PROJECT AS P ON M.PROJECT_ID = P.ID ");
		sb.append(" JOIN REF_ACT_ATTACHMENTS AS R ON M.ID = R.ACTIVITY_ID AND R.ACTIVE = 'Y' AND M.ID = ").append(actid);
		sb.append(" JOIN LKUP_ACT_TYPE AS ATY ON M.LKUP_ACT_TYPE_ID = ATY.ID ");
		sb.append(" JOIN LKUP_PROJECT_TYPE AS PTY ON P.LKUP_PROJECT_TYPE_ID = PTY.ID ");
		sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' ");
		if (!u.isStaff()) {
			sb.append(" AND A.ISPUBLIC = 'Y' ");
		}
		sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");

		sb.append(" UNION ");

		sb.append(" SELECT ");

		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" 'review' AS REF ");
		sb.append(" , ");
		sb.append(" RCR.ID AS REF_ID ");
		sb.append(" , ");
		sb.append(" RV.NAME AS REF_NBR ");
		sb.append(" , ");
		sb.append(" AC.ACT_NBR ");
		sb.append(" , ");
		sb.append(" P.PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" ATY.TYPE AS ACTIVITY_TYPE ");
		sb.append(" , ");
		sb.append(" PTY.TYPE AS PROJECT_TYPE ");
		sb.append(" , ");
		sb.append(" RV.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME AS REVIEW_GROUP ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
		sb.append(" ELSE 'N' END AS IMAGE ");

		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_ACTION AS M ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RCR ON M.REF_COMBOREVIEW_REVIEW_ID = RCR.ID AND RCR.ACTIVE = 'Y' AND M.ACTIVE = 'Y' AND RCR.ACTIVE = 'Y' ");
		sb.append(" JOIN COMBOREVIEW AS C ON RCR.COMBOREVIEW_ID = C.ID AND C.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_ACT_COMBOREVIEW AS RAC ON C.ID = RAC.COMBOREVIEW_ID AND RAC.ACTIVE = 'Y' ");
		sb.append(" JOIN ACTIVITY AS AC ON RAC.ACTIVITY_ID = AC.ID AND AC.ACTIVE = 'Y' AND AC.ID = ").append(actid);
		sb.append(" JOIN LKUP_ACT_TYPE AS ATY ON AC.LKUP_ACT_TYPE_ID = ATY.ID ");
		sb.append(" JOIN PROJECT AS P ON AC.PROJECT_ID = P.ID ");
		sb.append(" JOIN LKUP_PROJECT_TYPE AS PTY ON P.LKUP_PROJECT_TYPE_ID = PTY.ID ");
		sb.append(" JOIN REVIEW AS RV ON RCR.REVIEW_ID = RV.ID ");
		sb.append(" JOIN REVIEW_GROUP AS G ON RV.REVIEW_GROUP_ID = G.ID ");
		sb.append(" JOIN REF_COMBOREVIEW_ATTACHMENTS AS R ON M.ID = R.REF_COMBOREVIEW_ACTION_ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' ");
		if (!u.isStaff()) {
			sb.append(" AND A.ISPUBLIC = 'Y' ");
		}
		sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");

// DISPLAY PROJECT LEVEL IMAGES - not useful
//		if (entity.getProjectid() > 0) {
//			sb.append(" UNION ");
//
//			sb.append(" SELECT ");
//
//			sb.append(" A.ID ");
//			sb.append(" , ");
//			sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
//			sb.append(" , ");
//			sb.append(" A.TITLE ");
//			sb.append(" , ");
//			sb.append(" 'project' AS REF ");
//			sb.append(" , ");
//			sb.append(" M.ID AS REF_ID ");
//			sb.append(" , ");
//			sb.append(" M.PROJECT_NBR AS REF_NBR ");
//			sb.append(" , ");
//			sb.append(" '' AS ACT_NBR ");
//			sb.append(" , ");
//			sb.append(" M.PROJECT_NBR ");
//			sb.append(" , ");
//			sb.append(" '' AS ACTIVITY_TYPE ");
//			sb.append(" , ");
//			sb.append(" PTY.TYPE AS PROJECT_TYPE ");
//			sb.append(" , ");
//			sb.append(" '' AS REVIEW ");
//			sb.append(" , ");
//			sb.append(" '' AS REVIEW_GROUP ");
//			sb.append(" , ");
//			sb.append(" A.DESCRIPTION ");
//			sb.append(" , ");
//			sb.append(" T.TYPE ");
//			sb.append(" , ");
//			sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
//			sb.append(" , ");
//			sb.append(" A.ISPUBLIC ");
//			sb.append(" , ");
//			sb.append(" A.LOCATION ");
//			sb.append(" , ");
//			sb.append(" A.PATH ");
//			sb.append(" , ");
//			sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
//			sb.append(" , ");
//			sb.append(" CU.USERNAME AS CREATED ");
//			sb.append(" , ");
//			sb.append(" UP.USERNAME AS UPDATED ");
//			sb.append(" , ");
//			sb.append(" A.CREATED_DATE ");
//			sb.append(" , ");
//			sb.append(" CASE ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
//			sb.append(" ELSE 'N' END AS IMAGE ");
//
//			sb.append(" FROM ");
//			sb.append(" PROJECT AS M ");
//			sb.append(" JOIN REF_PROJECT_ATTACHMENTS AS R ON M.ID = R.PROJECT_ID AND R.ACTIVE = 'Y' AND M.ID = ").append(entity.getProjectid());
//			sb.append(" JOIN LKUP_PROJECT_TYPE AS PTY ON M.LKUP_PROJECT_TYPE_ID = PTY.ID ");
//			sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' ");
//			sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' ");
//			sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
//			sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");
//
//			sb.append(" UNION ");
//
//			sb.append(" SELECT ");
//
//			sb.append(" A.ID ");
//			sb.append(" , ");
//			sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
//			sb.append(" , ");
//			sb.append(" A.TITLE ");
//			sb.append(" , ");
//			sb.append(" 'review' AS REF ");
//			sb.append(" , ");
//			sb.append(" RCR.ID AS REF_ID ");
//			sb.append(" , ");
//			sb.append(" RV.NAME AS REF_NBR ");
//			sb.append(" , ");
//			sb.append(" '' AS ACT_NBR ");
//			sb.append(" , ");
//			sb.append(" P.PROJECT_NBR ");
//			sb.append(" , ");
//			sb.append(" '' AS ACTIVITY_TYPE ");
//			sb.append(" , ");
//			sb.append(" PTY.TYPE AS PROJECT_TYPE ");
//			sb.append(" , ");
//			sb.append(" RV.NAME AS REVIEW ");
//			sb.append(" , ");
//			sb.append(" G.GROUP_NAME AS REVIEW_GROUP ");
//			sb.append(" , ");
//			sb.append(" A.DESCRIPTION ");
//			sb.append(" , ");
//			sb.append(" T.TYPE ");
//			sb.append(" , ");
//			sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
//			sb.append(" , ");
//			sb.append(" A.ISPUBLIC ");
//			sb.append(" , ");
//			sb.append(" A.LOCATION ");
//			sb.append(" , ");
//			sb.append(" A.PATH ");
//			sb.append(" , ");
//			sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
//			sb.append(" , ");
//			sb.append(" CU.USERNAME AS CREATED ");
//			sb.append(" , ");
//			sb.append(" UP.USERNAME AS UPDATED ");
//			sb.append(" , ");
//			sb.append(" A.CREATED_DATE ");
//			sb.append(" , ");
//			sb.append(" CASE ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
//			sb.append(" ELSE 'N' END AS IMAGE ");
//
//			sb.append(" FROM ");
//			sb.append(" REF_COMBOREVIEW_ACTION AS M ");
//			sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RCR ON M.REF_COMBOREVIEW_REVIEW_ID = RCR.ID AND RCR.ACTIVE = 'Y' AND M.ACTIVE = 'Y' AND RCR.ACTIVE = 'Y' ");
//			sb.append(" JOIN COMBOREVIEW AS C ON RCR.COMBOREVIEW_ID = C.ID AND C.ACTIVE = 'Y' ");
//			sb.append(" JOIN REF_PROJECT_COMBOREVIEW AS RPC ON C.ID = RPC.COMBOREVIEW_ID AND RPC.ACTIVE = 'Y' AND RPC.PROJECT_ID = ").append(entity.getProjectid());
//			sb.append(" JOIN PROJECT AS P ON RPC.PROJECT_ID = P.ID ");
//			sb.append(" JOIN LKUP_PROJECT_TYPE AS PTY ON P.LKUP_PROJECT_TYPE_ID = PTY.ID ");
//			sb.append(" JOIN REVIEW AS RV ON RCR.REVIEW_ID = RV.ID ");
//			sb.append(" JOIN REVIEW_GROUP AS G ON RV.REVIEW_GROUP_ID = G.ID ");
//			sb.append(" JOIN REF_COMBOREVIEW_ATTACHMENTS AS R ON M.ID = R.REF_COMBOREVIEW_ACTION_ID AND R.ACTIVE = 'Y' ");
//			sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' ");
//			sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' ");
//			sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
//			sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");
//		}
//
// DISPLAY ENTITY LEVEL IMAGES - not useful
//		if (Operator.hasValue(entity.getEntity()) && entity.getEntityid() > 0) {
//			String tableref = CsReflect.getTableRef(entity.getEntity());
//			String idref = CsReflect.getFieldIdRef(entity.getEntity());
//			sb.append(" UNION ");
//
//			sb.append(" SELECT ");
//
//			sb.append(" A.ID ");
//			sb.append(" , ");
//			sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
//			sb.append(" , ");
//			sb.append(" A.TITLE ");
//			sb.append(" , ");
//			sb.append(" '").append(entity.getEntity()).append("' AS REF ");
//			sb.append(" , ");
//			sb.append(" R.").append(idref).append(" AS REF_ID ");
//			sb.append(" , ");
//			sb.append(" CAST(R.").append(idref).append(" AS VARCHAR(15)) AS REF_NBR ");
//			sb.append(" , ");
//			sb.append(" '' AS ACT_NBR ");
//			sb.append(" , ");
//			sb.append(" '' AS PROJECT_NBR ");
//			sb.append(" , ");
//			sb.append(" '' AS ACTIVITY_TYPE ");
//			sb.append(" , ");
//			sb.append(" '' AS PROJECT_TYPE ");
//			sb.append(" , ");
//			sb.append(" '' AS REVIEW ");
//			sb.append(" , ");
//			sb.append(" '' AS REVIEW_GROUP ");
//			sb.append(" , ");
//			sb.append(" A.DESCRIPTION ");
//			sb.append(" , ");
//			sb.append(" T.TYPE ");
//			sb.append(" , ");
//			sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
//			sb.append(" , ");
//			sb.append(" A.ISPUBLIC ");
//			sb.append(" , ");
//			sb.append(" A.LOCATION ");
//			sb.append(" , ");
//			sb.append(" A.PATH ");
//			sb.append(" , ");
//			sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
//			sb.append(" , ");
//			sb.append(" CU.USERNAME AS CREATED ");
//			sb.append(" , ");
//			sb.append(" UP.USERNAME AS UPDATED ");
//			sb.append(" , ");
//			sb.append(" A.CREATED_DATE ");
//			sb.append(" , ");
//			sb.append(" CASE ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
//			sb.append(" ELSE 'N' END AS IMAGE ");
//
//			sb.append(" FROM ");
//			sb.append(" REF_").append(tableref).append("_ATTACHMENTS AS R ");
//			sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND R.").append(idref).append(" = ").append(entity.getEntityid());
//			sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' ");
//			sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
//			sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");
//
//		}
		return sb.toString();
	}

	public static String projectSummary(int projid, Token u) {
		if (projid < 1) { return ""; }
		TypeInfo entity = EntityAgent.getEntity("project", projid);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" 'activity' AS REF ");
		sb.append(" , ");
		sb.append(" M.ID AS REF_ID ");
		sb.append(" , ");
		sb.append(" M.ACT_NBR AS REF_NBR ");
		sb.append(" , ");
		sb.append(" M.ACT_NBR ");
		sb.append(" , ");
		sb.append(" P.PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" ATY.TYPE AS ACTIVITY_TYPE ");
		sb.append(" , ");
		sb.append(" PTY.TYPE AS PROJECT_TYPE ");
		sb.append(" , ");
		sb.append(" '' AS REVIEW ");
		sb.append(" , ");
		sb.append(" '' AS REVIEW_GROUP ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
		sb.append(" ELSE 'N' END AS IMAGE ");

		sb.append(" FROM ");
		sb.append(" ACTIVITY AS M ");
		sb.append(" JOIN PROJECT AS P ON M.PROJECT_ID = P.ID ");
		sb.append(" JOIN REF_ACT_ATTACHMENTS AS R ON M.ID = R.ACTIVITY_ID AND R.ACTIVE = 'Y' AND M.PROJECT_ID = ").append(projid);
		sb.append(" JOIN LKUP_ACT_TYPE AS ATY ON M.LKUP_ACT_TYPE_ID = ATY.ID ");
		sb.append(" JOIN LKUP_PROJECT_TYPE AS PTY ON P.LKUP_PROJECT_TYPE_ID = PTY.ID ");
		sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND A.SENSITIVE = 'N' ");
		if (!u.isStaff()) {
			sb.append(" AND A.ISPUBLIC = 'Y' ");
		}
		sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");

		// ACTIVITY REVIEW
		sb.append(" UNION ");

		sb.append(" SELECT ");

		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" 'review' AS REF ");
		sb.append(" , ");
		sb.append(" RCR.ID AS REF_ID ");
		sb.append(" , ");
		sb.append(" RV.NAME AS REF_NBR ");
		sb.append(" , ");
		sb.append(" AC.ACT_NBR ");
		sb.append(" , ");
		sb.append(" P.PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" ATY.TYPE AS ACTIVITY_TYPE ");
		sb.append(" , ");
		sb.append(" PTY.TYPE AS PROJECT_TYPE ");
		sb.append(" , ");
		sb.append(" RV.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME AS REVIEW_GROUP ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
		sb.append(" ELSE 'N' END AS IMAGE ");

		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_ACTION AS M ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RCR ON M.REF_COMBOREVIEW_REVIEW_ID = RCR.ID AND RCR.ACTIVE = 'Y' AND M.ACTIVE = 'Y' AND RCR.ACTIVE = 'Y' ");
		sb.append(" JOIN COMBOREVIEW AS C ON RCR.COMBOREVIEW_ID = C.ID AND C.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_ACT_COMBOREVIEW AS RAC ON C.ID = RAC.COMBOREVIEW_ID AND RAC.ACTIVE = 'Y' ");
		sb.append(" JOIN ACTIVITY AS AC ON RAC.ACTIVITY_ID = AC.ID AND AC.ACTIVE = 'Y' AND AC.PROJECT_ID = ").append(projid);
		sb.append(" JOIN LKUP_ACT_TYPE AS ATY ON AC.LKUP_ACT_TYPE_ID = ATY.ID ");
		sb.append(" JOIN PROJECT AS P ON AC.PROJECT_ID = P.ID ");
		sb.append(" JOIN LKUP_PROJECT_TYPE AS PTY ON P.LKUP_PROJECT_TYPE_ID = PTY.ID ");
		sb.append(" JOIN REVIEW AS RV ON RCR.REVIEW_ID = RV.ID ");
		sb.append(" JOIN REVIEW_GROUP AS G ON RV.REVIEW_GROUP_ID = G.ID ");
		sb.append(" JOIN REF_COMBOREVIEW_ATTACHMENTS AS R ON M.ID = R.REF_COMBOREVIEW_ACTION_ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND A.SENSITIVE = 'N' ");
		if (!u.isStaff()) {
			sb.append(" AND A.ISPUBLIC = 'Y' ");
		}
		sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");

		// PROJECT REVIEW
		sb.append(" UNION ");

		sb.append(" SELECT ");

		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" 'review' AS REF ");
		sb.append(" , ");
		sb.append(" RCR.ID AS REF_ID ");
		sb.append(" , ");
		sb.append(" RV.NAME AS REF_NBR ");
		sb.append(" , ");
		sb.append(" '' AS ACT_NBR ");
		sb.append(" , ");
		sb.append(" P.PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" '' AS ACTIVITY_TYPE ");
		sb.append(" , ");
		sb.append(" PTY.TYPE AS PROJECT_TYPE ");
		sb.append(" , ");
		sb.append(" RV.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME AS REVIEW_GROUP ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
		sb.append(" ELSE 'N' END AS IMAGE ");

		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_ACTION AS M ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RCR ON M.REF_COMBOREVIEW_REVIEW_ID = RCR.ID AND RCR.ACTIVE = 'Y' AND M.ACTIVE = 'Y' AND RCR.ACTIVE = 'Y' ");
		sb.append(" JOIN COMBOREVIEW AS C ON RCR.COMBOREVIEW_ID = C.ID AND C.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_PROJECT_COMBOREVIEW AS RPC ON C.ID = RPC.COMBOREVIEW_ID AND RPC.ACTIVE = 'Y' AND RPC.PROJECT_ID = ").append(projid);
		sb.append(" JOIN PROJECT AS P ON RPC.PROJECT_ID = P.ID ");
		sb.append(" JOIN LKUP_PROJECT_TYPE AS PTY ON P.LKUP_PROJECT_TYPE_ID = PTY.ID ");
		sb.append(" JOIN REVIEW AS RV ON RCR.REVIEW_ID = RV.ID ");
		sb.append(" JOIN REVIEW_GROUP AS G ON RV.REVIEW_GROUP_ID = G.ID ");
		sb.append(" JOIN REF_COMBOREVIEW_ATTACHMENTS AS R ON M.ID = R.REF_COMBOREVIEW_ACTION_ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' ");
		if (!u.isStaff()) {
			sb.append(" AND A.ISPUBLIC = 'Y' ");
		}
		sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");

		sb.append(" UNION ");

		sb.append(" SELECT ");

		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" 'project' AS REF ");
		sb.append(" , ");
		sb.append(" M.ID AS REF_ID ");
		sb.append(" , ");
		sb.append(" M.PROJECT_NBR AS REF_NBR ");
		sb.append(" , ");
		sb.append(" '' AS ACT_NBR ");
		sb.append(" , ");
		sb.append(" M.PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" '' AS ACTIVITY_TYPE ");
		sb.append(" , ");
		sb.append(" PTY.TYPE AS PROJECT_TYPE ");
		sb.append(" , ");
		sb.append(" '' AS REVIEW ");
		sb.append(" , ");
		sb.append(" '' AS REVIEW_GROUP ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
		sb.append(" ELSE 'N' END AS IMAGE ");

		sb.append(" FROM ");
		sb.append(" PROJECT AS M ");
		sb.append(" JOIN REF_PROJECT_ATTACHMENTS AS R ON M.ID = R.PROJECT_ID AND R.ACTIVE = 'Y' AND M.ID = ").append(projid);
		sb.append(" JOIN LKUP_PROJECT_TYPE AS PTY ON M.LKUP_PROJECT_TYPE_ID = PTY.ID ");
		sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' ");
		if (!u.isStaff()) {
			sb.append(" AND A.ISPUBLIC = 'Y' ");
		}
		sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");

// DISPLAY ENTITY LEVEL IMAGES - not useful
//		if (Operator.hasValue(entity.getEntity()) && entity.getEntityid() > 0) {
//			String tableref = CsReflect.getTableRef(entity.getEntity());
//			String idref = CsReflect.getFieldIdRef(entity.getEntity());
//			sb.append(" UNION ");
//
//			sb.append(" SELECT ");
//
//			sb.append(" A.ID ");
//			sb.append(" , ");
//			sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
//			sb.append(" , ");
//			sb.append(" A.TITLE ");
//			sb.append(" , ");
//			sb.append(" '").append(entity.getEntity()).append("' AS REF ");
//			sb.append(" , ");
//			sb.append(" R.").append(idref).append(" AS REF_ID ");
//			sb.append(" , ");
//			sb.append(" CAST(R.").append(idref).append(" AS VARCHAR(15)) AS REF_NBR ");
//			sb.append(" , ");
//			sb.append(" '' AS ACT_NBR ");
//			sb.append(" , ");
//			sb.append(" '' AS PROJECT_NBR ");
//			sb.append(" , ");
//			sb.append(" '' AS ACTIVITY_TYPE ");
//			sb.append(" , ");
//			sb.append(" '' AS PROJECT_TYPE ");
//			sb.append(" , ");
//			sb.append(" '' AS REVIEW ");
//			sb.append(" , ");
//			sb.append(" '' AS REVIEW_GROUP ");
//			sb.append(" , ");
//			sb.append(" A.DESCRIPTION ");
//			sb.append(" , ");
//			sb.append(" T.TYPE ");
//			sb.append(" , ");
//			sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
//			sb.append(" , ");
//			sb.append(" A.ISPUBLIC ");
//			sb.append(" , ");
//			sb.append(" A.LOCATION ");
//			sb.append(" , ");
//			sb.append(" A.PATH ");
//			sb.append(" , ");
//			sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
//			sb.append(" , ");
//			sb.append(" CU.USERNAME AS CREATED ");
//			sb.append(" , ");
//			sb.append(" UP.USERNAME AS UPDATED ");
//			sb.append(" , ");
//			sb.append(" A.CREATED_DATE ");
//			sb.append(" , ");
//			sb.append(" CASE ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
//			sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
//			sb.append(" ELSE 'N' END AS IMAGE ");
//
//			sb.append(" FROM ");
//			sb.append(" REF_").append(tableref).append("_ATTACHMENTS AS R ");
//			sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND R.").append(idref).append(" = ").append(entity.getEntityid());
//			sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' ");
//			sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
//			sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");
//		}

		return sb.toString();
	}


	// PROJECT AND ACTIVITY ATTACHMENTS MUST BE PUBLIC
	public static String lsoSummary(int lsoid, Token u) {
		TypeInfo entity = EntityAgent.getEntity("lso", lsoid);
		StringBuilder sb = new StringBuilder();
		sb.append(lsoid);
		if (entity.getChildid().size() > 0) {
			for (int i=0; i<entity.getChildid().size(); i++) {
				int id = entity.getChildid().get(i);
				sb.append(",").append(id);
			}
			if (entity.getGrandchildid().size() > 0) {
				for (int i=0; i<entity.getGrandchildid().size(); i++) {
					int id = entity.getGrandchildid().get(i);
					sb.append(",").append(id);
				}
			}
		}
		String ids = sb.toString();
		sb = new StringBuilder();
		sb.append(" SELECT ");

		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" 'activity' AS REF ");
		sb.append(" , ");
		sb.append(" M.ID AS REF_ID ");
		sb.append(" , ");
		sb.append(" M.ACT_NBR AS REF_NBR ");
		sb.append(" , ");
		sb.append(" M.ACT_NBR ");
		sb.append(" , ");
		sb.append(" P.PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" ATY.TYPE AS ACTIVITY_TYPE ");
		sb.append(" , ");
		sb.append(" PTY.TYPE AS PROJECT_TYPE ");
		sb.append(" , ");
		sb.append(" '' AS REVIEW ");
		sb.append(" , ");
		sb.append(" '' AS REVIEW_GROUP ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
		sb.append(" ELSE 'N' END AS IMAGE ");

		sb.append(" FROM ");
		sb.append(" ACTIVITY AS M ");
		sb.append(" JOIN PROJECT AS P ON M.PROJECT_ID = P.ID AND M.ACTIVE = 'Y' AND P.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_LSO_PROJECT AS L ON P.ID = L.PROJECT_ID AND L.ACTIVE = 'Y' AND L.LSO_ID  IN ( ").append(ids).append(" ) ");
		sb.append(" JOIN LKUP_ACT_TYPE AS ATY ON M.LKUP_ACT_TYPE_ID = ATY.ID ");
		sb.append(" JOIN LKUP_PROJECT_TYPE AS PTY ON P.LKUP_PROJECT_TYPE_ID = PTY.ID ");
		sb.append(" JOIN REF_ACT_ATTACHMENTS AS R ON M.ID = R.ACTIVITY_ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND A.ISPUBLIC = 'Y' AND A.SENSITIVE = 'N' ");
		if (!u.isStaff()) {
			sb.append(" AND A.ISPUBLIC = 'Y' ");
		}
		sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' AND T.IMGSHOWLSO = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");

		// ACTIVITY REVIEW
		sb.append(" UNION ");

		sb.append(" SELECT ");

		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" 'review' AS REF ");
		sb.append(" , ");
		sb.append(" RCR.ID AS REF_ID ");
		sb.append(" , ");
		sb.append(" RV.NAME AS REF_NBR ");
		sb.append(" , ");
		sb.append(" AC.ACT_NBR ");
		sb.append(" , ");
		sb.append(" P.PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" ATY.TYPE AS ACTIVITY_TYPE ");
		sb.append(" , ");
		sb.append(" PTY.TYPE AS PROJECT_TYPE ");
		sb.append(" , ");
		sb.append(" RV.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME AS REVIEW_GROUP ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
		sb.append(" ELSE 'N' END AS IMAGE ");

		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_ACTION AS M ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RCR ON M.REF_COMBOREVIEW_REVIEW_ID = RCR.ID AND RCR.ACTIVE = 'Y' AND M.ACTIVE = 'Y' AND RCR.ACTIVE = 'Y' ");
		sb.append(" JOIN COMBOREVIEW AS C ON RCR.COMBOREVIEW_ID = C.ID AND C.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_ACT_COMBOREVIEW AS RAC ON C.ID = RAC.COMBOREVIEW_ID AND RAC.ACTIVE = 'Y' ");
		sb.append(" JOIN ACTIVITY AS AC ON RAC.ACTIVITY_ID = AC.ID AND AC.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_ACT_TYPE AS ATY ON AC.LKUP_ACT_TYPE_ID = ATY.ID ");
		sb.append(" JOIN PROJECT AS P ON AC.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_PROJECT_TYPE AS PTY ON P.LKUP_PROJECT_TYPE_ID = PTY.ID ");
		sb.append(" JOIN REF_LSO_PROJECT AS L ON P.ID = L.PROJECT_ID AND L.ACTIVE = 'Y' AND L.LSO_ID  IN ( ").append(ids).append(" ) ");
		sb.append(" JOIN REVIEW AS RV ON RCR.REVIEW_ID = RV.ID ");
		sb.append(" JOIN REVIEW_GROUP AS G ON RV.REVIEW_GROUP_ID = G.ID ");
		sb.append(" JOIN REF_COMBOREVIEW_ATTACHMENTS AS R ON M.ID = R.REF_COMBOREVIEW_ACTION_ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND A.ISPUBLIC = 'Y' AND A.SENSITIVE = 'N' ");
		if (!u.isStaff()) {
			sb.append(" AND A.ISPUBLIC = 'Y' ");
		}
		sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' AND T.IMGSHOWLSO = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");

		// PROJECT REVIEW
		sb.append(" UNION ");

		sb.append(" SELECT ");

		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" 'review' AS REF ");
		sb.append(" , ");
		sb.append(" RCR.ID AS REF_ID ");
		sb.append(" , ");
		sb.append(" RV.NAME AS REF_NBR ");
		sb.append(" , ");
		sb.append(" '' AS ACT_NBR ");
		sb.append(" , ");
		sb.append(" P.PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" '' AS ACTIVITY_TYPE ");
		sb.append(" , ");
		sb.append(" PTY.TYPE AS PROJECT_TYPE ");
		sb.append(" , ");
		sb.append(" RV.NAME AS REVIEW ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME AS REVIEW_GROUP ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
		sb.append(" ELSE 'N' END AS IMAGE ");

		sb.append(" FROM ");
		sb.append(" REF_COMBOREVIEW_ACTION AS M ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RCR ON M.REF_COMBOREVIEW_REVIEW_ID = RCR.ID AND RCR.ACTIVE = 'Y' AND M.ACTIVE = 'Y' AND RCR.ACTIVE = 'Y' ");
		sb.append(" JOIN COMBOREVIEW AS C ON RCR.COMBOREVIEW_ID = C.ID AND C.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_PROJECT_COMBOREVIEW AS RPC ON C.ID = RPC.COMBOREVIEW_ID AND RPC.ACTIVE = 'Y' ");
		sb.append(" JOIN PROJECT AS P ON RPC.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_PROJECT_TYPE AS PTY ON P.LKUP_PROJECT_TYPE_ID = PTY.ID ");
		sb.append(" JOIN REF_LSO_PROJECT AS L ON P.ID = L.PROJECT_ID AND L.ACTIVE = 'Y' AND L.LSO_ID  IN ( ").append(ids).append(" ) ");
		sb.append(" JOIN REVIEW AS RV ON RCR.REVIEW_ID = RV.ID ");
		sb.append(" JOIN REVIEW_GROUP AS G ON RV.REVIEW_GROUP_ID = G.ID ");
		sb.append(" JOIN REF_COMBOREVIEW_ATTACHMENTS AS R ON M.ID = R.REF_COMBOREVIEW_ACTION_ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND A.ISPUBLIC = 'Y' AND A.SENSITIVE = 'N' ");
		if (!u.isStaff()) {
			sb.append(" AND A.ISPUBLIC = 'Y' ");
		}
		sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' AND T.IMGSHOWLSO = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");

		sb.append(" UNION ");

		sb.append(" SELECT ");

		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" 'project' AS REF ");
		sb.append(" , ");
		sb.append(" M.ID AS REF_ID ");
		sb.append(" , ");
		sb.append(" M.PROJECT_NBR AS REF_NBR ");
		sb.append(" , ");
		sb.append(" '' AS ACT_NBR ");
		sb.append(" , ");
		sb.append(" M.PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" '' AS ACTIVITY_TYPE ");
		sb.append(" , ");
		sb.append(" PTY.TYPE AS PROJECT_TYPE ");
		sb.append(" , ");
		sb.append(" '' AS REVIEW ");
		sb.append(" , ");
		sb.append(" '' AS REVIEW_GROUP ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
		sb.append(" ELSE 'N' END AS IMAGE ");

		sb.append(" FROM ");
		sb.append(" PROJECT AS M ");
		sb.append(" JOIN REF_LSO_PROJECT AS L ON M.ID = L.PROJECT_ID AND L.ACTIVE = 'Y' AND L.LSO_ID  IN ( ").append(ids).append(" ) ");
		sb.append(" JOIN LKUP_PROJECT_TYPE AS PTY ON M.LKUP_PROJECT_TYPE_ID = PTY.ID ");
		sb.append(" JOIN REF_PROJECT_ATTACHMENTS AS R ON M.ID = R.PROJECT_ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND A.ISPUBLIC = 'Y' AND A.SENSITIVE = 'N' ");
		if (!u.isStaff()) {
			sb.append(" AND A.ISPUBLIC = 'Y' ");
		}
		sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' AND T.IMGSHOWLSO = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");

		sb.append(" UNION ");

		sb.append(" SELECT ");

		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(entity.getEntitydescription())).append("' AS ENTITY ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" 'lso' AS REF ");
		sb.append(" , ");
		sb.append(" R.LSO_ID AS REF_ID ");
		sb.append(" , ");
		sb.append(" CAST(R.LSO_ID AS VARCHAR(15)) AS REF_NBR ");
		sb.append(" , ");
		sb.append(" '' AS ACT_NBR ");
		sb.append(" , ");
		sb.append(" '' AS PROJECT_NBR ");
		sb.append(" , ");
		sb.append(" '' AS ACTIVITY_TYPE ");
		sb.append(" , ");
		sb.append(" '' AS PROJECT_TYPE ");
		sb.append(" , ");
		sb.append(" '' AS REVIEW ");
		sb.append(" , ");
		sb.append(" '' AS REVIEW_GROUP ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		sb.append(" '").append("/cs").append("' + '/viewfile.jsp?_id='+ CAST(A.ID AS VARCHAR(10)) AS FILEURL ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" CASE ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%jpeg' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%png' THEN 'Y' ");
		sb.append(" 	WHEN LOWER(A.PATH) LIKE '%gif' THEN 'Y' ");
		sb.append(" ELSE 'N' END AS IMAGE ");

		sb.append(" FROM ");
		sb.append(" REF_LSO_ATTACHMENTS AS R ");
		sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND R.LSO_ID IN (").append(ids).append(" ) ");
		if (!u.isStaff()) {
			sb.append(" AND A.ISPUBLIC = 'Y' ");
		}
		sb.append(" JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID AND T.IMAGE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID AND CU.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID AND UP.ACTIVE = 'Y' ");
		return sb.toString();
	}

























}















