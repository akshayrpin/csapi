package csapi.utils.objtools;

import alain.core.utils.Operator;
import csapi.common.Table;
import csapi.utils.CsReflect;
import csshared.vo.ObjVO;

public class ObjSQL {

	public static String getCustomGroup(int groupid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM ").append(Table.FIELDGROUPSTABLE).append(" WHERE ID = ").append(groupid);
		return sb.toString();
	}

	public static String getCustomGroups(String type, int typeid, String option) {
		return CsReflect.getQuery("getCustomGroups", type, typeid, option);
	}

	public static String getCustomGroupValues(String type, int typeid) {
		return getCustomGroupValues(type, typeid, -1);
	}

	public static String getCustomGroupValues(String type, int typeid, int groupid) {
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
		sb.append(" , ");
		sb.append(" R.ORDR AS GROUP_ORDER ");
		sb.append(" , ");
		sb.append(" F.ORDR AS FIELD_ORDER ");
		sb.append(" ,");
		sb.append(" LFT.TYPE as FIELD_TYPE ");
		sb.append(" ,");
		sb.append(" LFIT.TYPE as FIELD_ITYPE ");
		sb.append(" FROM ");
		sb.append(" ").append(table).append(" AS P ");
		sb.append(" JOIN LKUP_").append(tableref).append("_TYPE AS T ON P.LKUP_ACT_TYPE_ID = T.ID ");
		sb.append(" JOIN REF_").append(tableref).append("_FIELD_GROUPS AS R ON R.LKUP_ACT_TYPE_ID = T.ID ");
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
		sb.append(" ORDER BY R.ORDR, G.GROUP_NAME, F.ORDR ");
		return sb.toString();
	}

	public static String getCustomFields(int groupid) {
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
		sb.append(" ,");
		sb.append(" COUNT (DISTINCT C.ID) AS CHOICES ");
		sb.append(" FROM ");
		sb.append(" FIELD_GROUPS AS G ");
		sb.append(" JOIN FIELD AS F ON F.FIELD_GROUPS_ID = G.ID AND F.ACTIVE = 'Y' ");
		
		sb.append(" LEFT OUTER JOIN LKUP_FIELD_TYPE AS LFT ON F.LKUP_FIELD_TYPE_ID = LFT.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_FIELD_ITYPE AS LFIT ON F.LKUP_FIELD_ITYPE_ID = LFIT.ID ");

		sb.append(" LEFT OUTER JOIN FIELD_CHOICES AS C ON F.ID = C.FIELD_ID AND C.ACTIVE = 'Y' ");

		sb.append(" WHERE ");
		sb.append(" G.ID = ").append(groupid);
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
		sb.append(" ,");
		sb.append(" F.ORDR ");
		sb.append(" ,");
		sb.append(" LFT.TYPE ");
		sb.append(" ,");
		sb.append(" LFIT.TYPE ");
		sb.append(" ORDER BY F.ORDR ");
		return sb.toString();
	}

	public static String getCustomFields(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append(" F.ID ");
		sb.append(" , ");
		sb.append(" F.NAME ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" IT.TYPE AS ITYPE ");
		sb.append(" , ");
		sb.append(" G.GROUP_NAME ");
		sb.append(" ,");
		sb.append(" G.ID as GROUP_ID ");
		sb.append(" ,");
		sb.append(" G.ISPUBLIC as GROUP_PUBLIC ");
		sb.append(" ,");
		sb.append(" R.ORDR as GROUP_ORDER ");
		sb.append(" ,");
		sb.append(" F.ORDR as FIELD_ORDER ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_FIELD_GROUPS R  ");
		sb.append(" JOIN ").append(Table.FIELDGROUPSTABLE).append(" AS G ON R.FIELD_GROUPS_ID = G.ID ");
		sb.append(" JOIN ").append(Table.FIELDTABLE).append(" F ON F.FIELD_GROUPS_ID = G.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDTYPETABLE).append(" T ON F.LKUP_FIELD_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPFIELDITYPETABLE).append(" IT ON F.LKUP_FIELD_ITYPE_ID = IT.ID ");
		sb.append(" WHERE ");
		sb.append(" F.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" G.ACTIVE = 'Y'  ");
		sb.append(" AND ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" ) ");
		sb.append(" , ");
		sb.append(" C AS ( ");
		sb.append("   SELECT COUNT(DISTINCT GROUP_NAME) AS GROUPS FROM Q ");
		sb.append(" ) ");
		sb.append(" SELECT Q.*, C.GROUPS ");
		sb.append(" FROM ");
		sb.append(" Q ");
		sb.append(" JOIN C ON 1=1 ");
		sb.append(" ORDER BY Q.GROUP_ORDER, Q.GROUP_NAME, Q.FIELD_ORDER ");
		return sb.toString();
	}

	public static String getModules(String loc, String type, int typeid) {
		String location = loc;
		if (!Operator.hasValue(location) || Operator.equalsIgnoreCase(location, "tool")) { location = "summary"; }
		String tableref = CsReflect.getTableRef(type);
		String mainref = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT DISTINCT L.ID, L.MODULE, L.TOOL_TITLE, L.DISABLE_ON_HOLD, L.DISABLE_TOOL_ON_HOLD, L.TOOL_ACTION, L.TOOL_ONLY, L.PROJECT_COPY, L.ACTIVITY_COPY, MIN(M.ORDR) AS ORDR ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_MODULE AS M ");
		sb.append(" JOIN ").append(mainref).append(" AS MT ON M.LKUP_").append(tableref).append("_TYPE_ID = MT.LKUP_").append(tableref).append("_TYPE_ID ");
		sb.append(" JOIN LKUP_MODULE AS L ON M.LKUP_MODULE_ID = L.ID ");
		sb.append(" WHERE ");
		sb.append(" MT.ID = ").append(typeid);
		sb.append(" AND ");
		sb.append(" L.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" M.ACTIVE = 'Y' ");
		if (!Operator.equalsIgnoreCase(loc, "tool")) {
			sb.append(" AND ");
			sb.append(" L.TOOL_ONLY <> 'Y' ");
		}
		if (Operator.hasValue(location)) {
			if (!Operator.equalsIgnoreCase(loc, "modules")) {
				sb.append(" AND ");
				sb.append(" LOWER(M.LOCATION) = LOWER('").append(Operator.sqlEscape(location)).append("') ");
			}
		}
		else {
			sb.append(" AND ");
			sb.append(" LOWER(L.MODULE) <> 'reviews' ");
			sb.append(" AND ");
			sb.append(" LOWER(L.MODULE) <> 'custom' ");
		}
		sb.append(" GROUP BY L.ID, L.MODULE, L.TOOL_TITLE, L.DISABLE_ON_HOLD, L.DISABLE_TOOL_ON_HOLD, L.TOOL_ACTION, L.TOOL_ONLY, L.PROJECT_COPY, L.ACTIVITY_COPY ");
		sb.append(" ) ");

		sb.append(" SELECT DISTINCT ");
		sb.append(" Q.ID, Q.MODULE, Q.TOOL_TITLE, Q.DISABLE_ON_HOLD, Q.DISABLE_TOOL_ON_HOLD, Q.TOOL_ACTION, Q.TOOL_ONLY, Q.PROJECT_COPY, Q.ACTIVITY_COPY, Q.ORDR ");
		sb.append(" FROM ");
		sb.append(" Q ");
		sb.append(" ORDER BY ");
		sb.append(" Q.ORDR ASC ");
		return sb.toString();
	}

	public static String getModules(String[] modules) {
		if (!Operator.hasValue(modules)) { return ""; }
		StringBuilder m = new StringBuilder();
		boolean empty = true;
		for (int i=0; i<modules.length; i++) {
			if (!empty) { m.append(","); }
			m.append(" '").append(Operator.sqlEscape(modules[i].toLowerCase())).append("' ");
			empty = false;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT L.ID, L.MODULE, L.TOOL_TITLE, L.DISABLE_ON_HOLD, L.DISABLE_TOOL_ON_HOLD, L.TOOL_ACTION, L.TOOL_ONLY, L.PROJECT_COPY, L.ACTIVITY_COPY ");
		sb.append(" FROM ");
		sb.append(" LKUP_MODULE AS L ");
		sb.append(" WHERE ");
		sb.append(" LOWER(L.MODULE) IN (").append(m.toString()).append(") ");
		sb.append(" AND ");
		sb.append(" L.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getModule(String module) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT L.ID, L.MODULE, L.TOOL_TITLE, L.DISABLE_ON_HOLD, L.DISABLE_TOOL_ON_HOLD, L.TOOL_ACTION, L.TOOL_ONLY, L.PROJECT_COPY, L.ACTIVITY_COPY ");
		sb.append(" FROM ");
		sb.append(" LKUP_MODULE AS L ");
		sb.append(" WHERE ");
		sb.append(" LOWER(L.MODULE) = LOWER('").append(Operator.sqlEscape(module)).append("') ");
		sb.append(" AND ");
		sb.append(" L.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getHolds(String type, int typeid) {
		return getHold(type, typeid, 0);
	}

	public static String getHold(String type, int typeid, int holdid) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.ID ");
		sb.append(" ,");
		sb.append(" R.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" H.HOLD ");
		sb.append(" , ");
		sb.append(" H.DESCRIPTION AS HOLD_DESCRIPTION ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" R.CREATED_DATE, ");
		sb.append(" R.UPDATED_DATE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_HOLD R  ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPHOLDTYPETABLE).append(" H ON R.LKUP_HOLDS_ID = H.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU ON R.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP ON R.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" AND ");
		sb.append(" R.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" H.ACTIVE = 'Y' ");
		if (holdid > 0) {
			sb.append(" AND ");
			sb.append(" R.ID = ").append(holdid);
		}
		return sb.toString();
	}

	public static String getAttachments(String type, int typeid) {
		return getAttachment(type, typeid, 0);
	}

	public static String getAttachment(String type, int typeid, int attachid) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.ID ");
		sb.append(" , ");
		sb.append(" R.TITLE ");
		sb.append(" , ");
		sb.append(" R.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" R.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" R.LOCATION ");
		sb.append(" , ");
		sb.append(" R.PATH ");
		sb.append(" , ");
		sb.append(" R.KEYWORD1 ");
		sb.append(" , ");
		sb.append(" R.KEYWORD2 ");
		sb.append(" , ");
		sb.append(" R.KEYWORD3 ");
		sb.append(" , ");
		sb.append(" R.KEYWORD4 ");
		sb.append(" , ");
		sb.append(" R.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" R.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" R.UPDATED_DATE ");
		
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_TYPE_ATTACHMENT_TYPE R ");
		//sb.append(" JOIN ").append(Table.ATTACHMENTSTABLE).append(" A ON R.ATTACHMENT_ID = A.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU ON R.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP ON R.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		if (attachid > 0) {
			sb.append(" AND ");
			sb.append(" R.ID = ").append(attachid);
		}
		sb.append(" ORDER BY R.CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String getLibraries(String type, int typeid) {
		return getLibrary(type, typeid, 0);
	}

	public static String getLibrary(String type, int typeid, int libid) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.ID ");
		sb.append(" , ");
		sb.append(" L.SHORT_TEXT ");
		sb.append(" , ");
		sb.append(" R.CREATED_DATE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_LKUP_LIBRARY R ");
		sb.append(" JOIN ").append(Table.LKUPLIBRARYTABLE).append(" L ON R.LKUP_LIBRARY_ID = L.ID ");
		sb.append(" WHERE ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" AND ");
		sb.append(" L.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" R.ACTIVE = 'Y' ");
		if (libid > 0) {
			sb.append(" AND ");
			sb.append(" R.ID = ").append(libid);
		}
		return sb.toString();
	}
	
	public static String getNotes(String type,int typeid){
		return getNotes(type, typeid, 0);
	}

	public static String getNotes(String type, int typeid, int noteid) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT N.ID ");
		sb.append(" , ");
		sb.append(" N.NOTE ");
		sb.append(" , ");
		sb.append(" N.DATE ");
		sb.append(" , ");
		sb.append(" N.LKUP_NOTES_TYPE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_NOTES AS R ");
		sb.append(" LEFT OUTER JOIN  ").append(Table.NOTESTABLE).append(" AS N ON R.COMNT_ID = N.ID ");
		sb.append(" WHERE ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" AND ");
		sb.append(" N.ACTIVE='Y' ");
		if(noteid > 0){
			sb.append(" AND ");
			sb.append(" N.ID = ").append(noteid);
		}
		sb.append(" ORDER BY N.DATE DESC ");
		return sb.toString();
	}
	
	public static String getConditions(String type,int typeid){
		return getConditions(type, typeid, 0);
	}

	public static String getConditions(String type, int typeid, int condid) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT R.ID ");
		sb.append(" , ");
		sb.append(" SHORT_TEXT ");
		sb.append(" , ");
		sb.append(" R.CREATED_DATE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_LKUP_LIBRARY AS R ");
		sb.append(" LEFT OUTER JOIN  ").append(Table.LKUPLIBRARYTABLE).append(" AS N ON R.LKUP_LIBRARY_ID = N.ID ");
		sb.append(" WHERE ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" AND ");
		sb.append(" N.ACTIVE='Y' ");
		if(condid > 0){
			sb.append(" AND ");
			sb.append(" R.ID = ").append(condid);
		}
		sb.append(" ORDER BY N.CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String getReviewsAndActions(String type, int typeid, String option) {
		return CsReflect.getQuery("getReviews", type, typeid, option);
	}
	
	public static String getReviews(String type, int typeid) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT RG.*   ");
		sb.append(" FROM ");
		sb.append(" ").append(maintableref).append(" AS A ");
		sb.append(" join REF_").append(tableref).append("_REVIEW_GROUP AS RARG on  A.ACT_TYPE_ID= RARG.ACT_TYPE_ID ");
		sb.append(" LEFT OUTER JOIN  ").append(Table.REVIEWGROUPTABLE).append(" RG  on RARG.REVIEW_GROUP_ID=RG.ID  ");
		sb.append(" WHERE ");
		sb.append(" A.ID = ").append(typeid).append(" ");
		sb.append(" AND ");
		sb.append(" A.ACTIVE='Y' ");
		/*if(condid > 0){
			sb.append(" AND ");
			sb.append(" R.ID = ").append(condid);
		}*/
		sb.append(" ORDER BY RG.ORDR ");
		return sb.toString();
	}
	
	public static String getReviews(int reviewGroupId) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select * ");
		sb.append(" FROM ");
		sb.append(" ").append(Table.REVIEWTABLE).append(" AS R ");
		sb.append(" WHERE ");
		sb.append(" R.REVIEW_GROUP_ID = ").append(reviewGroupId).append(" ");
		sb.append(" AND ");
		sb.append(" R.ACTIVE='Y' ");
		sb.append(" ORDER BY R.ORDR ");
		return sb.toString();
	}

	public static String getReviewAction(String type, int typeid, int reviewId){
		StringBuilder sb = new StringBuilder();
		sb.append(" select RA.DATE,LRS.DESCRIPTION from REF_ACT_REVIEW RAR  ");
		sb.append(" left outer join review_action RA  on RAR.REVIEW_ACTION_ID=RA.ID  ");
		sb.append(" left outer join review R on RA.REVIEW_ID=R.ID  ");
		sb.append(" left outer join LKUP_REVIEW_STATUS LRS on RA.LKUP_REVIEW_STATUS_ID = LRS.ID ");
		sb.append(" where RAR.activity_id=").append(typeid).append(" and RAR.ACTIVE='Y' and REVIEW_ID= ").append(reviewId);
		return sb.toString();
	}
	
	
	public static String reviewAll(String type, int typeid, int reviewGroupId){
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS (  ");
		sb.append("         select ID,NAME,DESCRIPTION  FROM  REVIEW AS R  WHERE  R.REVIEW_GROUP_ID = ").append(reviewGroupId).append("  AND  R.ACTIVE='Y'    ");
		sb.append(" )   ");
		sb.append(" select  Q.*,RA.ID,RA.DATE,LRS.DESCRIPTION as STATUS from REF_ACT_REVIEW RAR   ");
		sb.append(" left outer join review_action RA  on RAR.REVIEW_ACTION_ID=RA.ID   ");
		sb.append(" left outer join review R on RA.REVIEW_ID=R.ID   ");
		sb.append(" left outer join LKUP_REVIEW_STATUS LRS on RA.LKUP_REVIEW_STATUS_ID = LRS.ID  ");
		sb.append(" left outer join Q on Q.ID = R.ID  ");
		sb.append(" where RAR.activity_id=").append(typeid).append(" and RAR.ACTIVE='Y' ");
		//sb.append( " and Q.ID is not null  ");
		return sb.toString();
	}

	public static String getReview(int groupid, int reviewid) {
		if (reviewid < 1) { return getReviewGroup(groupid); }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		if (reviewid > 0) {
			sb.append(" R.ID AS REVIEW_ID ");
			sb.append(" , ");
			sb.append(" R.NAME AS REVIEW ");
			sb.append(" , ");
		}
		sb.append(" G.ID AS GROUP_ID ");
		sb.append(" , ");
		sb.append(" GROUP_NAME AS GROUP_NAME ");
		sb.append(" FROM ");
		sb.append(" REVIEW_GROUP AS G ");
		if (reviewid > 0) {
			sb.append(" LEFT OUTER JOIN REVIEW AS R ON G.ID = R.REVIEW_GROUP_ID AND R.ACTIVE = 'Y' AND R.ID = ").append(reviewid);
		}
		sb.append(" WHERE ");
		sb.append(" G.ID = ").append(groupid);
		return sb.toString();
	}

	public static String getReviewGroup(int groupid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID AS GROUP_ID ");
		sb.append(" , ");
		sb.append(" GROUP_NAME AS GROUP_NAME ");
		sb.append(" FROM ");
		sb.append(" REVIEW_GROUP ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(groupid);
		return sb.toString();
	}

	public static String reviews(String type, int typeid, int reviewGroupId) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append("    SELECT R.ID, R.NAME, R.DESCRIPTION, R.DISPLAY_TYPE, MAX(A.DATE) AS MAXDATE ");
		sb.append("    FROM  REVIEW AS R ");
		sb.append("    LEFT OUTER JOIN  ");
		sb.append("    (  ");
		sb.append("        REVIEW_ACTION AS A ");
		sb.append("        JOIN REF_ACT_REVIEW AS RE ON A.ID = RE.REVIEW_ACTION_ID AND RE.ACTIVITY_ID = ").append(typeid).append(" ");
		sb.append("    ) ON R.ID = A.REVIEW_ID ");
		sb.append("    WHERE ");
		sb.append("        R.REVIEW_GROUP_ID = ").append(reviewGroupId).append(" AND R.ACTIVE='Y'  "); 
		sb.append("        GROUP BY R.ID, R.NAME, R.DESCRIPTION, R.DISPLAY_TYPE ");
		sb.append("    ) ");
		sb.append(" SELECT ");
		sb.append(" DISTINCT RA.ID, Q.ID, RA.DATE,LRS.DESCRIPTION,Q.NAME,Q.DESCRIPTION as REVIEW ");
		sb.append(" FROM ");
		sb.append(" Q ");
		sb.append(" LEFT OUTER JOIN REVIEW_ACTION RA ON Q.ID = RA.REVIEW_ID AND Q.MAXDATE = RA.DATE ");
		sb.append(" LEFT OUTER JOIN LKUP_REVIEW_STATUS LRS on RA.LKUP_REVIEW_STATUS_ID = LRS.ID   ");
		//sb.append(" left outer JOIN REF_ACT_REVIEW AS RE ON RA.ID = RE.REVIEW_ACTION_ID "); 
		sb.append(" WHERE ");
		sb.append(" (");
		sb.append("     DISPLAY_TYPE = 'Y' ");
		sb.append("     OR ");
		sb.append("     (RA.ID IS NOT NULL AND DISPLAY_TYPE = 'AUTO')");
		sb.append(" ) "); 
		/*if(display.equalsIgnoreCase("AUTO")){
			sb.append(" AND RE.ACTIVITY_ID = ").append(typeid).append(" ");
		}*/
		sb.append(" ORDER BY DATE DESC ");
		return sb.toString();
	}
	
	
	public static String getPeople(String type, int typeid) {
		return getPeople(type, typeid, 0);
	}

	public static String getPeople(String type, int typeid, int attachid) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.ID as REF_ID, ");
		sb.append(" U.*, ");
		sb.append(" T.DESCRIPTION as TYPE, ");
		sb.append(" P.PHONE_WORK, ");
		sb.append(" P.ADDRESS,");
		sb.append(" LI.LIC_NUM ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_USERS R ");
		sb.append(" JOIN REF_USERS RU on R.REF_USERS_ID = RU.ID  ");
		sb.append(" JOIN USERS U on RU.USERS_ID = U.ID  ");
		sb.append(" JOIN LKUP_USERS_TYPE T on RU.LKUP_USERS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN PEOPLE P ON U.ID = P.USERS_ID ");
		sb.append(" LEFT OUTER JOIN PEOPLE_LICENSE LI on P.ID = LI.PEOPLE_ID ");
		sb.append(" WHERE ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" AND ");
		sb.append(" R.ACTIVE='Y' ");
		return sb.toString();
	}
	
	
	

	public static String updateNote(int noteid, ObjVO[] obj, int userId) {
		StringBuilder sb = new StringBuilder();
		if(noteid>0){
			sb.append(" UPDATE ").append(Table.NOTESTABLE).append(" SET ");
			sb.append(" NOTE= '").append(Operator.sqlEscape(obj[1].getValue())).append("' ");
			sb.append(",");
			sb.append(" LKUP_NOTES_TYPE= ").append(Operator.sqlEscape(obj[0].getValue())).append(" ");
			sb.append(" ,DATE = CURRENT_TIMESTAMP,UPDATED_DATE = CURRENT_TIMESTAMP, UPDATED_BY= ").append(userId);
			sb.append(" WHERE ID =").append(noteid);
		}
		return sb.toString();
	}
	
	
	public static String insertNote(ObjVO[] obj, int userId) {
		StringBuilder sb = new StringBuilder();
		sb.append(" insert into  ").append(Table.NOTESTABLE).append(" (NOTE,DATE,LKUP_NOTES_TYPE,CREATED_BY,UPDATED_BY,CREATED_DATE,UPDATED_DATE ) VALUES ( ");
		sb.append(" '").append(Operator.sqlEscape(obj[1].getValue())).append("' ");
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(",");
		sb.append(" ").append(Operator.sqlEscape(obj[0].getValue())).append(" ");
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(" ) "); 
		
		return sb.toString();
	}
	
	
	public static String getNoteId(ObjVO[] obj, int userId) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select TOP 1 ID FROM ").append(Table.NOTESTABLE).append(" ");
		sb.append(" WHERE NOTE= '").append(Operator.sqlEscape(obj[1].getValue())).append("' ");
		sb.append(" AND ");
		sb.append(" CREATED_BY = ").append(userId);
		sb.append(" order by CREATED_DATE DESC ");
		return sb.toString();
	}
	
	public static String insertNoteRef(String type, int typeId, int noteid, int userId) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		if(noteid>0){
			sb.append(" insert into REF_").append(tableref).append("_NOTES (").append(idref).append(", COMNT_ID, CREATED_BY,UPDATED_BY,CREATED_DATE,UPDATED_DATE  ) ");
			sb.append(" VALUES ( ");
			sb.append(typeId);
			sb.append(",");
			sb.append(noteid);
			sb.append(",");
			sb.append(userId);
			sb.append(",");
			sb.append(userId);
			sb.append(",");
			sb.append(" CURRENT_TIMESTAMP ");
			sb.append(",");
			sb.append(" CURRENT_TIMESTAMP ");
			sb.append(" ) "); 
		}
		return sb.toString();
	}
	
	
	public static String updateAttachment(int attachId, ObjVO[] obj, int userId) {
		StringBuilder sb = new StringBuilder();
		if(attachId>0){
			sb.append(" UPDATE ").append(Table.ATTACHMENTSTABLE).append(" SET ");
			sb.append(" TITLE= '").append(Operator.sqlEscape(obj[0].getValue())).append("' ");
			sb.append(",");
			sb.append(" DESCRIPTION= '").append(Operator.sqlEscape(obj[1].getValue())).append("' ");
			if(Operator.hasValue(obj[2].getValue())){
				sb.append(",");
				sb.append(" PATH= '").append(Operator.sqlEscape(obj[2].getValue())).append("' ");
			}
			sb.append(",");
			sb.append(" ISPUBLIC= '").append(Operator.sqlEscape(obj[3].getValue())).append("' ");
			sb.append(",");
			sb.append(" KEYWORD1= '").append(Operator.sqlEscape(obj[4].getValue())).append("' ");
			sb.append(",");
			sb.append(" KEYWORD2= '").append(Operator.sqlEscape(obj[5].getValue())).append("' ");
			sb.append(",");
			sb.append(" KEYWORD3= '").append(Operator.sqlEscape(obj[6].getValue())).append("' ");
			sb.append(",");
			sb.append(" KEYWORD4= '").append(Operator.sqlEscape(obj[7].getValue())).append("' ");
			sb.append(" , UPDATED_DATE = CURRENT_TIMESTAMP, UPDATED_BY= ").append(userId);
			sb.append(" WHERE ID =").append(attachId);
		}
		return sb.toString();
	}
	
	
	public static String insertAttachment(ObjVO[] obj, int userId) {
		StringBuilder sb = new StringBuilder();
		sb.append(" insert into  ").append(Table.ATTACHMENTSTABLE).append(" (TITLE,DESCRIPTION,PATH,ISPUBLIC,KEYWORD1,KEYWORD2,KEYWORD3,KEYWORD4,CREATED_BY,UPDATED_BY,CREATED_DATE,UPDATED_DATE,LKUP_ATTACHMENTS_TYPE_ID ) VALUES ( ");
		sb.append(" '").append(Operator.sqlEscape(obj[0].getValue())).append("' ");
		sb.append(",");
		sb.append(" '").append(Operator.sqlEscape(obj[1].getValue())).append("' ");
		sb.append(",");
		sb.append(" '").append(Operator.sqlEscape(obj[2].getValue())).append("' ");
		sb.append(",");
		sb.append(" '").append(Operator.sqlEscape(obj[3].getValue())).append("' ");
		sb.append(",");
		sb.append(" '").append(Operator.sqlEscape(obj[4].getValue())).append("' ");
		sb.append(",");
		sb.append(" '").append(Operator.sqlEscape(obj[5].getValue())).append("' ");
		sb.append(",");
		sb.append(" '").append(Operator.sqlEscape(obj[6].getValue())).append("' ");
		sb.append(",");
		sb.append(" '").append(Operator.sqlEscape(obj[7].getValue())).append("' ");
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(",");
		sb.append(" 0 ");
		sb.append(" ) "); 
		
		return sb.toString();
	}
	
	
	public static String getAttachmentId(ObjVO[] obj, int userId) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select TOP 1 ID FROM ").append(Table.ATTACHMENTSTABLE).append(" ");
		sb.append(" WHERE TITLE= '").append(Operator.sqlEscape(obj[0].getValue())).append("' ");
		sb.append(" AND ");
		sb.append(" PATH = '").append(Operator.sqlEscape(obj[2].getValue())).append("' ");
		sb.append(" AND ");
		sb.append(" CREATED_BY = ").append(userId);
		sb.append(" order by CREATED_DATE DESC ");
		return sb.toString();
	}
	
	public static String insertAttachRef(String type, int typeId, int attachid, int userId) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		if(attachid>0){
			sb.append(" insert into REF_").append(tableref).append("_ATTACHMENTS (").append(idref).append(", ATTACHMENT_ID, CREATED_BY,UPDATED_BY,CREATED_DATE,UPDATED_DATE  ) ");
			sb.append(" VALUES ( ");
			sb.append(typeId);
			sb.append(",");
			sb.append(attachid);
			sb.append(",");
			sb.append(userId);
			sb.append(",");
			sb.append(userId);
			sb.append(",");
			sb.append(" CURRENT_TIMESTAMP ");
			sb.append(",");
			sb.append(" CURRENT_TIMESTAMP ");
			sb.append(" ) "); 
		}
		return sb.toString();
	}
	
}















