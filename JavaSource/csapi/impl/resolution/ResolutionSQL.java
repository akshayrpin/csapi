package csapi.impl.resolution;

import java.util.ArrayList;

import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.impl.entity.EntityAgent;
import csapi.utils.CsReflect;
import csshared.vo.TypeInfo;

public class ResolutionSQL {

	public static String info(String type, int typeid, int id) {
		return details(type, typeid, -1);
	}

	public static String summary(String type, int typeid, int id) {
		return details(type, typeid, -1);
	}

	public static String list(String type, int typeid, int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" RD.REF AS RESOLUTION_TYPE ");
		sb.append(" , ");
		sb.append(" R.ID ");
		sb.append(" , ");
		sb.append(" R.RESOLUTION_NUMBER ");
		sb.append(" , ");
		sb.append(" RD.PART ");
		sb.append(" , ");
		sb.append(" RD.NAME ");
		sb.append(" , ");
		sb.append(" RD.RESOLUTION_DATE ");
		sb.append(" , ");
		sb.append(" RD.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" RD.LKUP_RESOLUTION_STATUS_ID ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" FROM ");
		sb.append(" RESOLUTION AS R ");
		sb.append(" JOIN RESOLUTION_DETAIL AS RD ON R.ID = RD.RESOLUTION_ID AND R.ACTIVE = 'Y' AND R.ID = ").append(id);
		sb.append(" LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON RD.LKUP_RESOLUTION_STATUS_ID = S.ID ");
		sb.append(" ORDER BY RD.CREATED_DATE DESC ");

		return sb.toString();
	}

	public static String details(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }

		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);

		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		String ent = entity.getEntity();
		String enttableref = CsReflect.getTableRef(ent);
		String entidref = CsReflect.getFieldIdRef(ent);

		int entityid = entity.getEntityid();
		int parentid = entity.getParentid();
		int grandparentid = entity.getGrandparentid();
		int projectid = entity.getProjectid();
		int activityid = entity.getActivityid();

		StringBuilder sb = new StringBuilder();

		sb.append(" WITH Q AS ( ");

		sb.append("   SELECT ");
		sb.append("     R.RESOLUTION_DETAIL_ID, ");
		sb.append("     'permanent' AS RESOLUTION_TYPE, ");
		sb.append("     R.REF, ");
		sb.append("     R.").append(entidref).append(" AS REF_ID, ");
		sb.append("     R.").append(entidref).append(" AS REF_NUMBER ");
		sb.append("   FROM ");
		sb.append("     REF_").append(enttableref).append("_RESOLUTION AS R ");
		sb.append("   WHERE ");
		sb.append("     R.ACTIVE = 'Y' ");
		sb.append("     AND ");
		sb.append("     R.").append(entidref).append(" IN ");

		sb.append(" ( ");
		sb.append(entityid);
		if (parentid > 0) {
			sb.append(" , ");
			sb.append(parentid);
		}
		if (grandparentid > 0) {
			sb.append(" , ");
			sb.append(grandparentid);
		}
		sb.append(" ) ");

		if (projectid > 0) {
			sb.append(" UNION ");
			sb.append("   SELECT ");
			sb.append("     P.RESOLUTION_DETAIL_ID, 'temporary' AS RESOLUTION_TYPE, ");
			sb.append("     P.REF, ");
			sb.append("     PR.ID AS REF_ID, ");
			sb.append("     PR.PROJECT_NBR AS REF_NUMBER ");
			sb.append("   FROM ");
			sb.append("     REF_PROJECT_RESOLUTION AS P ");
			sb.append("     JOIN PROJECT AS PR ON P.PROJECT_ID = PR.ID ");
			sb.append("   WHERE ");
			sb.append("     P.ACTIVE = 'Y' AND P.PROJECT_ID = ").append(projectid);
		}
		if (activityid > 0) {
			sb.append(" UNION ");
			sb.append("   SELECT ");
			sb.append("     A.RESOLUTION_DETAIL_ID, 'temporary' AS RESOLUTION_TYPE, ");
			sb.append("     A.REF, ");
			sb.append("     ACT.ID AS REF_ID, ");
			sb.append("     ACT.ACT_NBR AS REF_NUMBER ");
			sb.append("   FROM ");
			sb.append("     REF_ACT_RESOLUTION AS A ");
			sb.append("     JOIN ACTIVITY AS ACT ON A.ACTIVITY_ID = ACT.ID ");
			sb.append("   WHERE ");
			sb.append("     A.ACTIVE = 'Y' AND A.ACTIVITY_ID = ").append(activityid);
		}

		sb.append(" ) ");

		sb.append(" SELECT DISTINCT ");
		sb.append(" Q.RESOLUTION_TYPE ");
		sb.append(" , ");
		sb.append(" Q.REF ");
		sb.append(" , ");
		sb.append(" Q.REF_ID ");
		sb.append(" , ");
		sb.append(" Q.REF_NUMBER ");
		sb.append(" , ");
		sb.append(" R.ID AS RESOLUTION ");
		sb.append(" , ");
		sb.append(" R.ID ");
		sb.append(" , ");
		sb.append(" R.RESOLUTION_NUMBER ");
		sb.append(" , ");
		sb.append(" R.ADOPTED_DATE ");
		sb.append(" , ");
		sb.append(" R.TITLE ");
		
		sb.append(" FROM ");
		sb.append(" RESOLUTION AS R ");
		sb.append(" JOIN RESOLUTION_DETAIL AS RD ON R.ID = RD.RESOLUTION_ID AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN Q ON RD.ID = Q.RESOLUTION_DETAIL_ID ");

		if (id > 0) {
			sb.append(" WHERE ");
			sb.append(" RD.ID = ").append(id);
		}

		sb.append(" ORDER BY R.RESOLUTION_NUMBER ");
		return sb.toString();
	}

	public static String editResolution(int resid, String resnum, String title, int userid, String ip) {
		if (resid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE RESOLUTION SET ");
		sb.append(" RESOLUTION_NUMBER = '").append(Operator.sqlEscape(resnum)).append("' ");
		sb.append(" , ");
		sb.append(" TITLE = '").append(Operator.sqlEscape(title)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(resid);
		return sb.toString();
	}

	public static String addResolution(String resnum, String title, int userid, String ip) {
		if (!Operator.hasValue(resnum) && !Operator.hasValue(title)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO RESOLUTION ( ");
		sb.append("   RESOLUTION_NUMBER ");
		sb.append("   , ");
		sb.append("   TITLE ");
		sb.append("   , ");
		sb.append("   CREATED_BY ");
		sb.append("   , ");
		sb.append("   UPDATED_BY ");
		sb.append("   , ");
		sb.append("   CREATED_IP ");
		sb.append("   , ");
		sb.append("   UPDATED_IP ");
		sb.append("   , ");
		sb.append("   CREATED_DATE ");
		sb.append("   , ");
		sb.append("   UPDATED_DATE ");
		sb.append(" ) Output Inserted.* VALUES ( ");
		sb.append("   '").append(Operator.sqlEscape(resnum)).append("' ");
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(title)).append("' ");
		sb.append("   , ");
		sb.append(userid);
		sb.append("   , ");
		sb.append(userid);
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append("   , ");
		sb.append("   getDate() ");
		sb.append("   , ");
		sb.append("   getDate() ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String editDetail(int detailid, String part, String name, String description, Timekeeper resdate, String expdate, int statusid, String ref, int refid, String refnum, int userid, String ip) {
		if (detailid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE RESOLUTION_DETAIL SET ");
		sb.append(" PART = '").append(Operator.sqlEscape(part)).append("' ");
		sb.append(" , ");
		sb.append(" NAME = '").append(Operator.sqlEscape(name)).append("' ");
		sb.append(" , ");
		sb.append(" DESCRIPTION = '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(" RESOLUTION_DATE = ").append(resdate.sqlDatetime());
		sb.append(" , ");
		sb.append(" EXP_DATE = ");
		if (Operator.hasValue(expdate)) {
			Timekeeper ed = new Timekeeper();
			ed.setDate(expdate);
			sb.append(ed.sqlDatetime());
		}
		else {
			sb.append(" null ");
		}
		sb.append(" , ");
		sb.append(" LKUP_RESOLUTION_STATUS_ID = ").append(statusid);
		sb.append(" , ");
		sb.append(" REF = '").append(Operator.sqlEscape(ref)).append("' ");
		sb.append(" , ");
		sb.append(" REF_ID = ").append(refid);
		sb.append(" , ");
		sb.append(" REF_NUMBER = '").append(Operator.sqlEscape(refnum)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(detailid);
		return sb.toString();
	}

//	public static String archiveDetail(int detailid, String ref) {
//		if (detailid < 1) { return ""; }
//
//		String tableref = CsReflect.getTableRef(ref);
//		String idref = CsReflect.getFieldIdRef(ref);
//
//		StringBuilder sb = new StringBuilder();
//		sb.append(" INSERT INTO RESOLUTION_HISTORY ");
//		sb.append(" SELECT ");
//		sb.append(" D.RESOLUTION_ID, ");
//		sb.append(" D.ID AS RESOLUTION_DETAIL_ID, ");
//		sb.append(" R.RESOLUTION_NUMBER, ");
//		sb.append(" D.PART, ");
//		sb.append(" D.NAME, ");
//		sb.append(" D.DESCRIPTION, ");
//		sb.append(" D.RESOLUTION_DATE, ");
//		sb.append(" D.LKUP_RESOLUTION_STATUS_ID, ");
//		sb.append(" D.REF, ");
//		sb.append(" REF.").append(idref).append(" AS REF_ID, ");
//		if (ref.equalsIgnoreCase("project")) {
//			sb.append(" P.PROJECT_NBR AS REF_NUMBER, ");
//		}
//		else if (ref.equalsIgnoreCase("activity")) {
//			sb.append(" A.ACT_NBR AS REF_NUMBER, ");
//		}
//		else {
//			sb.append(" CONVERT(varchar(25), REF.").append(idref).append(") AS REF_NUMBER, ");
//		}
//		sb.append(" D.EXP_DATE, ");
//		sb.append(" getDate() AS HISTORY_DATE, ");
//		sb.append(" 'Y' AS ACTIVE, ");
//		sb.append(" D.CREATED_BY, ");
//		sb.append(" D.CREATED_DATE, ");
//		sb.append(" D.UPDATED_BY, ");
//		sb.append(" D.UPDATED_DATE, ");
//		sb.append(" D.CREATED_IP, ");
//		sb.append(" D.UPDATED_IP ");
//		sb.append(" FROM ");
//		sb.append(" RESOLUTION_DETAIL AS D ");
//		sb.append(" JOIN RESOLUTION AS R ON R.ID = D.RESOLUTION_ID ");
//		sb.append(" JOIN REF_").append(tableref).append("_RESOLUTION AS REF ON D.ID = REF.RESOLUTION_DETAIL_ID ");
//		if (ref.equalsIgnoreCase("project")) {
//			sb.append(" JOIN PROJECT AS P ON REF.PROJECT_ID = P.ID ");
//		}
//		else if (ref.equalsIgnoreCase("activity")) {
//			sb.append(" JOIN ACTIVITY AS A ON REF.ACTIVITY_ID = A.ID ");
//		}
//		sb.append(" WHERE ");
//		sb.append(" D.ID = ").append(detailid);
//		return sb.toString();
//		
//	}
//
	public static String addDetail(int resid, String part, String name, String description, Timekeeper resdate, String expdate, int statusid, String ref, int refid, String refnum, int userid, String ip) {
		if (resid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO RESOLUTION_DETAIL ( ");
		sb.append("   RESOLUTION_ID ");
		sb.append("   , ");
		sb.append("   PART ");
		sb.append("   , ");
		sb.append("   NAME ");
		sb.append("   , ");
		sb.append("   DESCRIPTION ");
		sb.append("   , ");
		sb.append("   RESOLUTION_DATE ");
		if (Operator.hasValue(expdate)) {
			sb.append("   , ");
			sb.append("   EXP_DATE ");
		}
		sb.append("   , ");
		sb.append("   LKUP_RESOLUTION_STATUS_ID ");
		sb.append("   , ");
		sb.append("   REF ");
		sb.append("   , ");
		sb.append("   REF_ID ");
		sb.append("   , ");
		sb.append("   REF_NUMBER ");
		sb.append("   , ");
		sb.append("   CREATED_BY ");
		sb.append("   , ");
		sb.append("   UPDATED_BY ");
		sb.append("   , ");
		sb.append("   CREATED_IP ");
		sb.append("   , ");
		sb.append("   UPDATED_IP ");
		sb.append("   , ");
		sb.append("   CREATED_DATE ");
		sb.append("   , ");
		sb.append("   UPDATED_DATE ");
		sb.append(" ) Output Inserted.* VALUES ( ");
		sb.append(resid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(part)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(name)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(resdate.sqlDatetime());
		if (Operator.hasValue(expdate)) {
			Timekeeper ed = new Timekeeper();
			ed.setDate(expdate);
			sb.append(" , ");
			sb.append(ed.sqlDatetime());
		}
		sb.append(" , ");
		sb.append(statusid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ref)).append("' ");
		sb.append(" , ");
		sb.append(refid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(refnum)).append("' ");
		sb.append("   , ");
		sb.append(userid);
		sb.append("   , ");
		sb.append(userid);
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append("   , ");
		sb.append("   getDate() ");
		sb.append("   , ");
		sb.append("   getDate() ");
		sb.append(" ) "); 
		return sb.toString();
	}

	public static String getDetail(int resid, int userid, Timekeeper date) {
		if (resid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM RESOLUTION_DETAIL WHERE ");
		sb.append(" RESOLUTION_ID = ").append(resid);
		sb.append(" AND ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" AND ");
		sb.append(" UPDATED_DATE = ").append(date.sqlDatetime());
		return sb.toString();
	}

	public static String deleteRef(String ref, int detailid, int userid, String ip) {
		if (detailid < 1 || !Operator.hasValue(ref)) { return ""; }
		String tableref = CsReflect.getTableRef(ref);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" REF_").append(tableref).append("_RESOLUTION ");
		sb.append(" SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" RESOLUTION_DETAIL_ID = ").append(detailid);
		return sb.toString();
	}

	public static String addRef(String ref, int refid, int detailid, int userid, String ip) {
		if (detailid < 1 || !Operator.hasValue(ref)) { return ""; }
		String tableref = CsReflect.getTableRef(ref);
		String idref = CsReflect.getFieldIdRef(ref);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_RESOLUTION ( ");
		sb.append("   ").append(idref).append(" ");
		sb.append("   , ");
		sb.append("   RESOLUTION_DETAIL_ID ");
		sb.append("   , ");
		sb.append("   CREATED_BY ");
		sb.append("   , ");
		sb.append("   UPDATED_BY ");
		sb.append("   , ");
		sb.append("   CREATED_IP ");
		sb.append("   , ");
		sb.append("   UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(refid);
		sb.append(" , ");
		sb.append(detailid);
		sb.append("   , ");
		sb.append(userid);
		sb.append("   , ");
		sb.append(userid);
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}







	public static String add(String resnum, String title, int userid, String ip, Timekeeper date) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO RESOLUTION ( ");
		sb.append("   RESOLUTION_NUMBER ");
		sb.append("   , ");
		sb.append("   TITLE ");
		sb.append("   , ");
		sb.append("   CREATED_BY ");
		sb.append("   , ");
		sb.append("   UPDATED_BY ");
		sb.append("   , ");
		sb.append("   CREATED_IP ");
		sb.append("   , ");
		sb.append("   UPDATED_IP ");
		sb.append("   , ");
		sb.append("   CREATED_DATE ");
		sb.append("   , ");
		sb.append("   UPDATED_DATE ");
		sb.append(" ) VALUES ( ");
		sb.append("   '").append(Operator.sqlEscape(resnum)).append("' ");
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(title)).append("' ");
		sb.append("   , ");
		sb.append(userid);
		sb.append("   , ");
		sb.append(userid);
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append("   , ");
		sb.append(date.sqlDatetime());
		sb.append("   , ");
		sb.append(date.sqlDatetime());
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getResolution(String resnum, int userid, Timekeeper date) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM RESOLUTION WHERE ");
		sb.append(" LOWER(RESOLUTION_NUMBER) = LOWER('").append(Operator.sqlEscape(resnum)).append("') ");
		sb.append(" AND ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" AND ");
		sb.append(" UPDATED_DATE = ").append(date.sqlDatetime());
		return sb.toString();
	}

	public static String addResolutionDetail(int resid, String type, String part, String resdate, String expdate, String comment, int statusid, int userid, String ip) {
		Timekeeper rdate = new Timekeeper();
		if (Operator.hasValue(resdate)) {
			rdate.setDate(resdate);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO RESOLUTION_DETAIL ( ");
		sb.append("   RESOLUTION_ID ");
		sb.append("   , ");
		sb.append("   REF ");
		sb.append("   , ");
		sb.append("   RESOLUTION_DATE ");
		if (Operator.hasValue(expdate)) {
			sb.append("   , ");
			sb.append("   EXP_DATE ");
		}
		sb.append("   , ");
		sb.append("   PART ");
		sb.append("   , ");
		sb.append("   COMMENT ");
		sb.append("   , ");
		sb.append("   LKUP_RESOLUTION_STATUS_ID ");
		sb.append("   , ");
		sb.append("   CREATED_BY ");
		sb.append("   , ");
		sb.append("   UPDATED_BY ");
		sb.append("   , ");
		sb.append("   CREATED_IP ");
		sb.append("   , ");
		sb.append("   UPDATED_IP ");
		sb.append("   , ");
		sb.append("   CREATED_DATE ");
		sb.append("   , ");
		sb.append("   UPDATED_DATE ");
		sb.append(" ) Output Inserted.* VALUES ( ");
		sb.append(resid);
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(type.toLowerCase())).append("' ");
		sb.append("   , ");
		sb.append(rdate.sqlDatetime());
		if (Operator.hasValue(expdate)) {
			Timekeeper ed = new Timekeeper();
			ed.setDate(expdate);
			sb.append("   , ");
			sb.append(ed.sqlDatetime());
		}
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(part)).append("' ");
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(comment)).append("' ");
		sb.append("   , ");
		sb.append(statusid);
		sb.append("   , ");
		sb.append(userid);
		sb.append("   , ");
		sb.append(userid);
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append("   , ");
		sb.append("   getDate() ");
		sb.append("   , ");
		sb.append("   getDate() ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String setHistory(int resid) {
		if (resid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO RESOLUTION_HISTORY ");
		sb.append(" SELECT ");
		sb.append(" D.RESOLUTION_ID, ");
		sb.append(" R.RESOLUTION_NUMBER, ");
		sb.append(" D.PART, ");
		sb.append(" D.COMMENT, ");
		sb.append(" D.RESOLUTION_DATE, ");
		sb.append(" D.LKUP_RESOLUTION_STATUS_ID, ");
		sb.append(" D.ACTIVE, ");
		sb.append(" getDate() AS HISTORY_DATE, ");
		sb.append(" D.CREATED_BY, ");
		sb.append(" D.CREATED_DATE, ");
		sb.append(" D.UPDATED_BY, ");
		sb.append(" D.UPDATED_DATE, ");
		sb.append(" D.CREATED_IP, ");
		sb.append(" D.UPDATED_IP ");
		sb.append(" FROM ");
		sb.append(" RESOLUTION AS R ");
		sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.ID = D.RESOLUTION_ID ");
		sb.append(" WHERE ");
		sb.append(" D.RESOLUTION_ID =  ").append(resid);
		return sb.toString();
	}

	public static String deleteDetail(int resid) {
		if (resid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" DELETE FROM RESOLUTION_DETAIL WHERE RESOLUTION_ID = ").append(resid);
		return sb.toString();
	}

	public static String getCurrentResolutionDetail(int resid) {
		if (resid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT TOP 1 ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(" RESOLUTION_DETAIL ");
		sb.append(" WHERE ");
		sb.append(" RESOLUTION_ID = ").append(resid);
		sb.append(" ORDER BY ");
		sb.append(" CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String insertRef(String type, int typeid, int resid, int userid, String ip) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_RESOLUTION ( ");
		sb.append("   RESOLUTION_ID ");
		sb.append("   , ");
		sb.append(idref);
		sb.append("   , ");
		sb.append("   CREATED_BY ");
		sb.append("   , ");
		sb.append("   UPDATED_BY ");
		sb.append("   , ");
		sb.append("   CREATED_IP ");
		sb.append("   , ");
		sb.append("   UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(resid);
		sb.append("   , ");
		sb.append(typeid);
		sb.append("   , ");
		sb.append(userid);
		sb.append("   , ");
		sb.append(userid);
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append("   , ");
		sb.append("   '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String update(int resid, String resnum, String title, int userid, String ip) {
		if (resid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE RESOLUTION SET ");
		sb.append(" RESOLUTION_NUMBER = '").append(Operator.sqlEscape(resnum)).append("' ");
		sb.append(" , ");
		sb.append(" TITLE = '").append(Operator.sqlEscape(title)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(resid);
		return sb.toString();
	}

	public static String updateNew(int resid, int newid) {
		if (resid < 1 || newid < 1) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE RESOLUTION_DETAIL SET NEW_ID = ").append(newid).append(" WHERE NEW_ID < 1 AND RESOLUTION_ID = ").append(resid).append(" AND ID <> ").append(newid);
		return sb.toString();
	}

	public static String addCompliance(int resdetailid, int projectid, int userid, String ip) {
		if (resdetailid < 1 ||  projectid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO RESOLUTION_COMPLIANCE ( ");
		sb.append(" PROJECT_ID ");
		sb.append(" , ");
		sb.append(" RESOLUTION_DETAIL_ID ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(projectid);
		sb.append(" , ");
		sb.append(resdetailid);
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

	public static String addApplicationCompliance(int resdetailid, int projectid, int userid, String ip) {
		if (resdetailid < 1 ||  projectid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO RESOLUTION_APPLICATION_COMPLIANCE ( ");
		sb.append(" PROJECT_ID ");
		sb.append(" , ");
		sb.append(" RESOLUTION_DETAIL_ID ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(projectid);
		sb.append(" , ");
		sb.append(resdetailid);
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

	public static String deleteCompliance(int resdetailid, int projectid) {
		if (resdetailid < 1 ||  projectid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE RESOLUTION_COMPLIANCE SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" WHERE ");
		sb.append(" PROJECT_ID = ").append(projectid);
		sb.append(" AND ");
		sb.append(" RESOLUTION_DETAIL_ID = ").append(resdetailid);
		return sb.toString();
	}

	public static String deleteApplicationCompliance(int resdetailid, int projectid) {
		if (resdetailid < 1 ||  projectid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE RESOLUTION_APPLICATION_COMPLIANCE SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" WHERE ");
		sb.append(" PROJECT_ID = ").append(projectid);
		sb.append(" AND ");
		sb.append(" RESOLUTION_DETAIL_ID = ").append(resdetailid);
		return sb.toString();
	}

	public static String types(String type, int typeid, int selected) {
		String ent = EntityAgent.getProjectEntity(type, typeid).getEntity();
		if (selected < 1) {
			if (type.equalsIgnoreCase(ent)) {
				selected = 1;
			}
			else {
				selected = 2;
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" 1 AS ID ");
		sb.append(" , ");
		sb.append(" 1 AS VALUE ");
		sb.append(" , ");
		sb.append(" 'permanent' AS TEXT ");

		if (selected == 1) {
			sb.append(" , ");
			sb.append(" 'Y' AS SELECTED ");
		}
		else {
			sb.append(" , ");
			sb.append(" 'N' AS SELECTED ");
		}

		if (type.equalsIgnoreCase("project") || type.equalsIgnoreCase("activity")) {
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" 2 AS ID ");
			sb.append(" , ");
			sb.append(" 2 AS VALUE ");
			sb.append(" , ");
			sb.append(" 'temporary' AS TEXT ");
			if (selected == 1) {
				sb.append(" , ");
				sb.append(" 'N' AS SELECTED ");
			}
			else {
				sb.append(" , ");
				sb.append(" 'Y' AS SELECTED ");
			}
		}

		return sb.toString();
	}

	public static String getStatus(String type, int typeid, String resnum) {
		if (!Operator.hasValue(type) || typeid < 1) { return ""; }
		TypeInfo m = EntityAgent.getEntity(type, typeid);
		String entity = m.getEntity();
		int entityid = m.getEntityid();
		int projectid = m.getProjectid();

		String tableref = CsReflect.getTableRef(entity);
		String idref = CsReflect.getFieldIdRef(entity);

		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append("   SELECT ");
		sb.append("     R.ID, ");
		sb.append("     D.RESOLUTION_DATE, ");
		sb.append("     D.LKUP_RESOLUTION_STATUS_ID, ");
		sb.append("     S.STATUS, ");
		sb.append("     COALESCE(S.APPROVED,'N') AS APPROVED, ");
		sb.append("     COALESCE(S.FINAL,'N') AS FINAL ");

		sb.append("   FROM ");
		sb.append("     RESOLUTION AS R ");
		sb.append("     JOIN REF_").append(tableref).append("_RESOLUTION AS E ON E.RESOLUTION_ID = R.ID AND E.ACTIVE = 'Y' AND E.").append(idref).append(" = ").append(entityid).append(" AND LOWER(R.RESOLUTION_NUMBER) = LOWER('").append(Operator.sqlEscape(resnum)).append("') ");
		sb.append("     JOIN RESOLUTION_DETAIL AS D ON R.ID = D.RESOLUTION_ID AND R.ACTIVE = 'Y' AND D.ACTIVE = 'Y' ");
		sb.append("     LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID ");

		sb.append("   UNION ");

		sb.append("   SELECT ");
		sb.append("     R.ID, ");
		sb.append("     D.RESOLUTION_DATE, ");
		sb.append("     D.LKUP_RESOLUTION_STATUS_ID, ");
		sb.append("     S.STATUS, ");
		sb.append("     COALESCE(S.APPROVED,'N') AS APPROVED, ");
		sb.append("     COALESCE(S.FINAL,'N') AS FINAL ");
		sb.append("   FROM ");
		sb.append("     RESOLUTION AS R ");
		sb.append("     JOIN REF_PROJECT_RESOLUTION AS P ON P.RESOLUTION_ID = R.ID AND P.ACTIVE = 'Y' AND LOWER(R.RESOLUTION_NUMBER) = LOWER('").append(Operator.sqlEscape(resnum)).append("') ");
		sb.append("     JOIN REF_").append(tableref).append("_PROJECT AS RE ON P.PROJECT_ID = RE.PROJECT_ID AND RE.ACTIVE = 'Y' AND RE.").append(idref).append(" = ").append(entityid);
		sb.append("     JOIN RESOLUTION_DETAIL AS D ON R.ID = D.RESOLUTION_ID AND R.ACTIVE = 'Y' AND D.ACTIVE = 'Y' ");
		sb.append("     LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID ");
		sb.append(" ) ");

		sb.append(" , ");

		sb.append(" C AS ( ");
		sb.append("   SELECT 'RESOLUTION' AS TYPE, COUNT(DISTINCT ID) AS UNAPPROVED_COUNT FROM Q WHERE APPROVED = 'N' ");
		sb.append(" ) ");

		sb.append(" , ");

		sb.append(" D AS ( ");
		sb.append("   SELECT 'RESOLUTION' AS TYPE, MAX(RESOLUTION_DATE) AS APPROVED_DATE FROM Q WHERE APPROVED = 'Y' ");
		sb.append(" ) ");

		sb.append(" SELECT C.UNAPPROVED_COUNT, D.APPROVED_DATE FROM C JOIN D ON C.TYPE = D.TYPE ");

		return sb.toString();
	}

	public static String getResolution(int resid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.ID AS RESOLUTION_ID ");
		sb.append(" , ");
		sb.append(" R.RESOLUTION_NUMBER ");
		sb.append(" , ");
		sb.append(" R.TITLE ");
		sb.append(" , ");
		sb.append(" D.REF ");
		sb.append(" , ");
		sb.append(" R.ADOPTED_DATE ");
		sb.append(" , ");
		sb.append(" RC.USERNAME AS RESOLUTION_CREATOR ");
		sb.append(" , ");
		sb.append(" RU.USERNAME AS RESOLUTION_UPDATER ");
		sb.append(" , ");
		sb.append(" R.CREATED_BY AS RESOLUTION_CREATED_BY ");
		sb.append(" , ");
		sb.append(" R.UPDATED_BY AS RESOLUTION_UPDATED_BY ");
		sb.append(" , ");
		sb.append(" R.CREATED_DATE AS RESOLUTION_CREATED_DATE ");
		sb.append(" , ");
		sb.append(" R.UPDATED_DATE AS RESOLUTION_UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" D.ID ");
		sb.append(" , ");
		sb.append(" D.PART ");
		sb.append(" , ");
		sb.append(" D.NAME ");
		sb.append(" , ");
		sb.append(" D.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" D.RESOLUTION_DATE ");
		sb.append(" , ");
		sb.append(" D.LKUP_RESOLUTION_STATUS_ID ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" , ");
		sb.append(" S.APPROVED ");
		sb.append(" , ");
		sb.append(" S.FINAL ");
		sb.append(" , ");
		sb.append(" D.CREATED_BY ");
		sb.append(" , ");
		sb.append(" D.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" D.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" D.UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" C.USERNAME AS CREATOR ");
		sb.append(" , ");
		sb.append(" U.USERNAME AS UPDATER ");
		sb.append(" FROM ");
		sb.append(" RESOLUTION AS R ");
		sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.ID = D.RESOLUTION_ID ");
		sb.append(" LEFT OUTER JOIN USERS AS RC ON R.CREATED_BY = RC.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS RU ON R.UPDATED_BY = RU.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS C ON D.CREATED_BY = C.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON D.UPDATED_BY = U.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID ");
		sb.append(" WHERE ");
		sb.append(" R.ID = ").append(resid);
		return sb.toString();
	}

	public static String getResolutions(String type, int typeid, int resid) {
		TypeInfo m = EntityAgent.getEntity(type, typeid);
		String entity = m.getEntity();
		int entityid = m.getEntityid();

		String enttableref = CsReflect.getTableRef(entity);
		String entidref = CsReflect.getFieldIdRef(entity);

		int parentid = m.getParentid();
		int grandparentid = m.getGrandparentid();
		ArrayList<Integer> childid = m.getChildid();
		ArrayList<Integer> grandchildid = m.getGrandchildid();
		int projectid = m.getProjectid();
		int activityid = m.getActivityid();

		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append("   R.ID AS RESOLUTION_ID ");
		sb.append("   , ");
		sb.append("   R.RESOLUTION_NUMBER ");
		sb.append("   , ");
		sb.append("   R.TITLE ");
		sb.append("   , ");
		sb.append("   '").append(entity).append("' AS REF ");
		sb.append("   , ");
		sb.append("   E.").append(entidref).append(" AS REF_ID ");
		sb.append("   , ");
		sb.append("   CONVERT(varchar(25), E.").append(entidref).append(") AS REF_NUMBER ");
		sb.append("   , ");
		sb.append("   R.ADOPTED_DATE ");
		sb.append("   , ");
		sb.append("   RC.USERNAME AS RESOLUTION_CREATOR ");
		sb.append("   , ");
		sb.append("   RU.USERNAME AS RESOLUTION_UPDATER ");
		sb.append("   , ");
		sb.append("   R.CREATED_BY AS RESOLUTION_CREATED_BY ");
		sb.append("   , ");
		sb.append("   R.UPDATED_BY AS RESOLUTION_UPDATED_BY ");
		sb.append("   , ");
		sb.append("   R.CREATED_DATE AS RESOLUTION_CREATED_DATE ");
		sb.append("   , ");
		sb.append("   R.UPDATED_DATE AS RESOLUTION_UPDATED_DATE ");
		sb.append("   , ");
		sb.append("   D.ID ");
		sb.append("   , ");
		sb.append("   D.PART ");
		sb.append("   , ");
		sb.append("   D.NAME ");
		sb.append("   , ");
		sb.append("   D.DESCRIPTION ");
		sb.append("   , ");
		sb.append("   D.RESOLUTION_DATE ");
		sb.append("   , ");
		sb.append("   D.EXP_DATE ");
		sb.append("   , ");
		sb.append("   D.LKUP_RESOLUTION_STATUS_ID ");
		sb.append("   , ");
		sb.append("   S.STATUS ");
		sb.append("   , ");
		sb.append("   S.APPROVED ");
		sb.append("   , ");
		sb.append("   S.FINAL ");
		sb.append("   , ");
		sb.append("   D.CREATED_BY ");
		sb.append("   , ");
		sb.append("   D.UPDATED_BY ");
		sb.append("   , ");
		sb.append("   D.CREATED_DATE ");
		sb.append("   , ");
		sb.append("   D.UPDATED_DATE ");
		sb.append("   , ");
		sb.append("   C.USERNAME AS CREATOR ");
		sb.append("   , ");
		sb.append("   U.USERNAME AS UPDATER ");
		sb.append("   , ");
		sb.append("   'permanent' AS TYPE ");
		sb.append(" FROM ");
		sb.append("   REF_").append(enttableref).append("_RESOLUTION AS E ");
		sb.append("   JOIN RESOLUTION_DETAIL AS D ON E.RESOLUTION_DETAIL_ID = D.ID AND E.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND COALESCE(D.EXP_DATE, getDate()+1) > getDate() ");
		sb.append("   JOIN RESOLUTION AS R ON D.RESOLUTION_ID = R.ID AND R.ACTIVE = 'Y' ");
		sb.append("   LEFT OUTER JOIN USERS AS RC ON R.CREATED_BY = RC.ID ");
		sb.append("   LEFT OUTER JOIN USERS AS RU ON R.UPDATED_BY = RU.ID ");
		sb.append("   LEFT OUTER JOIN USERS AS C ON D.CREATED_BY = C.ID ");
		sb.append("   LEFT OUTER JOIN USERS AS U ON D.UPDATED_BY = U.ID ");
		sb.append("   LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID ");
		sb.append(" WHERE ");
		if (resid > 0) {
			sb.append(" R.ID = ").append(resid);
			sb.append(" AND ");
		}
		sb.append("   E.").append(entidref).append(" IN ");

		sb.append("   ( ");
		sb.append(entityid);
		if (parentid > 0) { sb.append(" , ").append(parentid); }
		if (grandparentid > 0) { sb.append(" , ").append(grandparentid); }
		if (projectid < 1 && childid.size() > 0) {
			for (int i=0; i<childid.size(); i++) {
				int cid = childid.get(i);
				sb.append(" , ").append(cid);
			}
		}
		if (projectid < 1 && grandchildid.size() > 0) {
			for (int i=0; i<grandchildid.size(); i++) {
				int gid = grandchildid.get(i);
				sb.append(" , ").append(gid);
			}
		}
		sb.append("   ) ");

		if (projectid < 1) {
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append("   R.ID AS RESOLUTION_ID ");
			sb.append("   , ");
			sb.append("   R.RESOLUTION_NUMBER ");
			sb.append("   , ");
			sb.append("   R.TITLE ");
			sb.append("   , ");
			sb.append("   'project' AS REF ");
			sb.append("   , ");
			sb.append("   PR.ID AS REF_ID ");
			sb.append("   , ");
			sb.append("   PR.PROJECT_NBR AS REF_NUMBER ");
			sb.append("   , ");
			sb.append("   R.ADOPTED_DATE ");
			sb.append("   , ");
			sb.append("   RC.USERNAME AS RESOLUTION_CREATOR ");
			sb.append("   , ");
			sb.append("   RU.USERNAME AS RESOLUTION_UPDATER ");
			sb.append("   , ");
			sb.append("   R.CREATED_BY AS RESOLUTION_CREATED_BY ");
			sb.append("   , ");
			sb.append("   R.UPDATED_BY AS RESOLUTION_UPDATED_BY ");
			sb.append("   , ");
			sb.append("   R.CREATED_DATE AS RESOLUTION_CREATED_DATE ");
			sb.append("   , ");
			sb.append("   R.UPDATED_DATE AS RESOLUTION_UPDATED_DATE ");
			sb.append("   , ");
			sb.append("   D.ID ");
			sb.append("   , ");
			sb.append("   D.PART ");
			sb.append("   , ");
			sb.append("   D.NAME ");
			sb.append("   , ");
			sb.append("   D.DESCRIPTION ");
			sb.append("   , ");
			sb.append("   D.RESOLUTION_DATE ");
			sb.append("   , ");
			sb.append("   D.EXP_DATE ");
			sb.append("   , ");
			sb.append("   D.LKUP_RESOLUTION_STATUS_ID ");
			sb.append("   , ");
			sb.append("   S.STATUS ");
			sb.append("   , ");
			sb.append("   S.APPROVED ");
			sb.append("   , ");
			sb.append("   S.FINAL ");
			sb.append("   , ");
			sb.append("   D.CREATED_BY ");
			sb.append("   , ");
			sb.append("   D.UPDATED_BY ");
			sb.append("   , ");
			sb.append("   D.CREATED_DATE ");
			sb.append("   , ");
			sb.append("   D.UPDATED_DATE ");
			sb.append("   , ");
			sb.append("   C.USERNAME AS CREATOR ");
			sb.append("   , ");
			sb.append("   U.USERNAME AS UPDATER ");
			sb.append("   , ");
			sb.append("   'permanent' AS TYPE ");
			sb.append(" FROM ");
			sb.append("   RESOLUTION_DETAIL AS D ");
			sb.append("   JOIN REF_PROJECT_RESOLUTION AS P ON P.RESOLUTION_DETAIL_ID = D.ID AND P.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND COALESCE(D.EXP_DATE, getDate()+1) > getDate()  ");
			sb.append("   JOIN REF_").append(enttableref).append("_PROJECT AS RD ON P.PROJECT_ID = RD.PROJECT_ID ");
			sb.append("   JOIN RESOLUTION AS R ON D.RESOLUTION_ID = R.ID AND R.ACTIVE = 'Y' ");
			sb.append("   JOIN PROJECT AS PR ON RD.PROJECT_ID = PR.ID AND PR.ACTIVE = 'Y' ");
			sb.append("   LEFT OUTER JOIN USERS AS RC ON R.CREATED_BY = RC.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS RU ON R.UPDATED_BY = RU.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS C ON D.CREATED_BY = C.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS U ON D.UPDATED_BY = U.ID ");
			sb.append("   LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID ");
			sb.append(" WHERE ");
			if (resid > 0) {
				sb.append(" R.ID = ").append(resid);
				sb.append(" AND ");
			}
			sb.append("   RD.").append(entidref).append(" IN ");

			sb.append("   ( ");
			sb.append(entityid);
			if (childid.size() > 0) {
				for (int i=0; i<childid.size(); i++) {
					int cid = childid.get(i);
					sb.append(" , ").append(cid);
				}
			}
			if (grandchildid.size() > 0) {
				for (int i=0; i<grandchildid.size(); i++) {
					int gid = grandchildid.get(i);
					sb.append(" , ").append(gid);
				}
			}
			sb.append("   ) ");
		}
		else {
			if (projectid > 0) {
				sb.append(" UNION ");

				sb.append(" SELECT ");
				sb.append("   R.ID AS RESOLUTION_ID ");
				sb.append("   , ");
				sb.append("   R.RESOLUTION_NUMBER ");
				sb.append("   , ");
				sb.append("   R.TITLE ");
				sb.append("   , ");
				sb.append("   'project' AS REF ");
				sb.append("   , ");
				sb.append("   PR.ID AS REF_ID ");
				sb.append("   , ");
				sb.append("   PR.PROJECT_NBR AS REF_NUMBER ");
				sb.append("   , ");
				sb.append("   R.ADOPTED_DATE ");
				sb.append("   , ");
				sb.append("   RC.USERNAME AS RESOLUTION_CREATOR ");
				sb.append("   , ");
				sb.append("   RU.USERNAME AS RESOLUTION_UPDATER ");
				sb.append("   , ");
				sb.append("   R.CREATED_BY AS RESOLUTION_CREATED_BY ");
				sb.append("   , ");
				sb.append("   R.UPDATED_BY AS RESOLUTION_UPDATED_BY ");
				sb.append("   , ");
				sb.append("   R.CREATED_DATE AS RESOLUTION_CREATED_DATE ");
				sb.append("   , ");
				sb.append("   R.UPDATED_DATE AS RESOLUTION_UPDATED_DATE ");
				sb.append("   , ");
				sb.append("   D.ID ");
				sb.append("   , ");
				sb.append("   D.PART ");
				sb.append("   , ");
				sb.append("   D.NAME ");
				sb.append("   , ");
				sb.append("   D.DESCRIPTION ");
				sb.append("   , ");
				sb.append("   D.RESOLUTION_DATE ");
				sb.append("   , ");
				sb.append("   D.EXP_DATE ");
				sb.append("   , ");
				sb.append("   D.LKUP_RESOLUTION_STATUS_ID ");
				sb.append("   , ");
				sb.append("   S.STATUS ");
				sb.append("   , ");
				sb.append("   S.APPROVED ");
				sb.append("   , ");
				sb.append("   S.FINAL ");
				sb.append("   , ");
				sb.append("   D.CREATED_BY ");
				sb.append("   , ");
				sb.append("   D.UPDATED_BY ");
				sb.append("   , ");
				sb.append("   D.CREATED_DATE ");
				sb.append("   , ");
				sb.append("   D.UPDATED_DATE ");
				sb.append("   , ");
				sb.append("   C.USERNAME AS CREATOR ");
				sb.append("   , ");
				sb.append("   U.USERNAME AS UPDATER ");
				sb.append("   , ");
				sb.append("   'temporary' AS TYPE ");
				sb.append(" FROM ");
				sb.append("   REF_PROJECT_RESOLUTION AS P ");
				sb.append("   JOIN RESOLUTION_DETAIL AS D ON P.RESOLUTION_DETAIL_ID = D.ID AND P.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND COALESCE(D.EXP_DATE, getDate()+1) > getDate()  ");
				sb.append("   JOIN RESOLUTION AS R ON D.RESOLUTION_ID = R.ID AND R.ACTIVE = 'Y' ");
				sb.append("   JOIN PROJECT AS PR ON P.PROJECT_ID = PR.ID AND PR.ACTIVE = 'Y' ");
				sb.append("   LEFT OUTER JOIN USERS AS RC ON R.CREATED_BY = RC.ID ");
				sb.append("   LEFT OUTER JOIN USERS AS RU ON R.UPDATED_BY = RU.ID ");
				sb.append("   LEFT OUTER JOIN USERS AS C ON D.CREATED_BY = C.ID ");
				sb.append("   LEFT OUTER JOIN USERS AS U ON D.UPDATED_BY = U.ID ");
				sb.append("   LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID ");
				sb.append(" WHERE ");
				if (resid > 0) {
					sb.append(" R.ID = ").append(resid);
					sb.append(" AND ");
				}
				sb.append("   P.PROJECT_ID = ").append(projectid);

			}
			if (activityid > 0) {
				sb.append(" UNION ");

				sb.append(" SELECT ");
				sb.append("   R.ID AS RESOLUTION_ID ");
				sb.append("   , ");
				sb.append("   R.RESOLUTION_NUMBER ");
				sb.append("   , ");
				sb.append("   R.TITLE ");
				sb.append("   , ");
				sb.append("   'activity' AS REF ");
				sb.append("   , ");
				sb.append("   ACT.ID AS REF_ID ");
				sb.append("   , ");
				sb.append("   ACT.ACT_NBR AS REF_NUMBER ");
				sb.append("   , ");
				sb.append("   R.ADOPTED_DATE ");
				sb.append("   , ");
				sb.append("   RC.USERNAME AS RESOLUTION_CREATOR ");
				sb.append("   , ");
				sb.append("   RU.USERNAME AS RESOLUTION_UPDATER ");
				sb.append("   , ");
				sb.append("   R.CREATED_BY AS RESOLUTION_CREATED_BY ");
				sb.append("   , ");
				sb.append("   R.UPDATED_BY AS RESOLUTION_UPDATED_BY ");
				sb.append("   , ");
				sb.append("   R.CREATED_DATE AS RESOLUTION_CREATED_DATE ");
				sb.append("   , ");
				sb.append("   R.UPDATED_DATE AS RESOLUTION_UPDATED_DATE ");
				sb.append("   , ");
				sb.append("   D.ID ");
				sb.append("   , ");
				sb.append("   D.PART ");
				sb.append("   , ");
				sb.append("   D.NAME ");
				sb.append("   , ");
				sb.append("   D.DESCRIPTION ");
				sb.append("   , ");
				sb.append("   D.RESOLUTION_DATE ");
				sb.append("   , ");
				sb.append("   D.EXP_DATE ");
				sb.append("   , ");
				sb.append("   D.LKUP_RESOLUTION_STATUS_ID ");
				sb.append("   , ");
				sb.append("   S.STATUS ");
				sb.append("   , ");
				sb.append("   S.APPROVED ");
				sb.append("   , ");
				sb.append("   S.FINAL ");
				sb.append("   , ");
				sb.append("   D.CREATED_BY ");
				sb.append("   , ");
				sb.append("   D.UPDATED_BY ");
				sb.append("   , ");
				sb.append("   D.CREATED_DATE ");
				sb.append("   , ");
				sb.append("   D.UPDATED_DATE ");
				sb.append("   , ");
				sb.append("   C.USERNAME AS CREATOR ");
				sb.append("   , ");
				sb.append("   U.USERNAME AS UPDATER ");
				sb.append("   , ");
				sb.append("   'temporary' AS TYPE ");
				sb.append(" FROM ");
				sb.append("   REF_ACT_RESOLUTION AS A ");
				sb.append("   JOIN ACTIVITY AS ACT ON A.ACTIVITY_ID = ACT.ID AND ACT.ACTIVE = 'Y' ");
				sb.append("   JOIN RESOLUTION_DETAIL AS D ON A.RESOLUTION_DETAIL_ID = D.ID AND A.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND COALESCE(D.EXP_DATE, getDate()+1) > getDate()  ");
				sb.append("   JOIN RESOLUTION AS R ON D.RESOLUTION_ID = R.ID AND R.ACTIVE = 'Y' ");
				sb.append("   LEFT OUTER JOIN USERS AS RC ON R.CREATED_BY = RC.ID ");
				sb.append("   LEFT OUTER JOIN USERS AS RU ON R.UPDATED_BY = RU.ID ");
				sb.append("   LEFT OUTER JOIN USERS AS C ON D.CREATED_BY = C.ID ");
				sb.append("   LEFT OUTER JOIN USERS AS U ON D.UPDATED_BY = U.ID ");
				sb.append("   LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID ");
				sb.append(" WHERE ");
				if (resid > 0) {
					sb.append(" R.ID = ").append(resid);
					sb.append(" AND ");
				}
				sb.append("   A.ACTIVITY_ID = ").append(activityid);
			}

		}

		sb.append(" ) ");
		sb.append(" , M AS ( ");
		sb.append("    SELECT Q.RESOLUTION_ID, MAX(Q.RESOLUTION_DATE) AS MAXDATE FROM Q GROUP BY Q.RESOLUTION_ID ");
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append("   Q.* ");
		sb.append("   , ");
		sb.append("   C.COMPLIANCE_DATE ");
		sb.append("   , ");
		sb.append("   AC.COMPLIANCE_DATE AS APPLICATION_COMPLIANCE_DATE ");
		sb.append("   , ");
		sb.append("   M.MAXDATE ");
		sb.append(" FROM ");
		sb.append("   Q ");
		sb.append("   LEFT OUTER JOIN M ON Q.RESOLUTION_ID = M.RESOLUTION_ID ");
		sb.append("   LEFT OUTER JOIN RESOLUTION_COMPLIANCE AS C ON Q.ID = C.RESOLUTION_DETAIL_ID AND C.ACTIVE = 'Y' AND C.PROJECT_ID = ").append(projectid);
		sb.append("   LEFT OUTER JOIN RESOLUTION_APPLICATION_COMPLIANCE AS AC ON Q.ID = AC.RESOLUTION_DETAIL_ID AND AC.ACTIVE = 'Y' AND AC.PROJECT_ID = ").append(projectid);
		sb.append(" ORDER BY Q.RESOLUTION_CREATED_DATE DESC, Q.RESOLUTION_ID, Q.RESOLUTION_DATE ");
		return sb.toString();
	}

	public static String getResolution(String type, int typeid, int resid) {
		TypeInfo m = EntityAgent.getEntity(type, typeid);
		String entity = m.getEntity();
		int entityid = m.getEntityid();

		String enttableref = CsReflect.getTableRef(entity);
		String entidref = CsReflect.getFieldIdRef(entity);

		int parentid = m.getParentid();
		int grandparentid = m.getGrandparentid();
		ArrayList<Integer> childid = m.getChildid();
		ArrayList<Integer> grandchildid = m.getGrandchildid();
		int projectid = m.getProjectid();
		int activityid = m.getActivityid();

		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" SELECT ");
		sb.append("   R.ID AS RESOLUTION_ID ");
		sb.append("   , ");
		sb.append("   R.RESOLUTION_NUMBER ");
		sb.append("   , ");
		sb.append("   R.TITLE ");
		sb.append("   , ");
		sb.append("   D.REF ");
		sb.append("   , ");
		sb.append("   D.REF_ID ");
		sb.append("   , ");
		sb.append("   D.REF_NUMBER ");
		sb.append("   , ");
		sb.append("   R.ADOPTED_DATE ");
		sb.append("   , ");
		sb.append("   RC.USERNAME AS RESOLUTION_CREATOR ");
		sb.append("   , ");
		sb.append("   RU.USERNAME AS RESOLUTION_UPDATER ");
		sb.append("   , ");
		sb.append("   R.CREATED_BY AS RESOLUTION_CREATED_BY ");
		sb.append("   , ");
		sb.append("   R.UPDATED_BY AS RESOLUTION_UPDATED_BY ");
		sb.append("   , ");
		sb.append("   R.CREATED_DATE AS RESOLUTION_CREATED_DATE ");
		sb.append("   , ");
		sb.append("   R.UPDATED_DATE AS RESOLUTION_UPDATED_DATE ");
		sb.append("   , ");
		sb.append("   D.ID ");
		sb.append("   , ");
		sb.append("   D.PART ");
		sb.append("   , ");
		sb.append("   D.NAME ");
		sb.append("   , ");
		sb.append("   D.DESCRIPTION ");
		sb.append("   , ");
		sb.append("   D.RESOLUTION_DATE ");
		sb.append("   , ");
		sb.append("   D.EXP_DATE ");
		sb.append("   , ");
		sb.append("   D.LKUP_RESOLUTION_STATUS_ID ");
		sb.append("   , ");
		sb.append("   S.STATUS ");
		sb.append("   , ");
		sb.append("   S.APPROVED ");
		sb.append("   , ");
		sb.append("   S.FINAL ");
		sb.append("   , ");
		sb.append("   D.CREATED_BY ");
		sb.append("   , ");
		sb.append("   D.UPDATED_BY ");
		sb.append("   , ");
		sb.append("   D.CREATED_DATE ");
		sb.append("   , ");
		sb.append("   D.UPDATED_DATE ");
		sb.append("   , ");
		sb.append("   C.USERNAME AS CREATOR ");
		sb.append("   , ");
		sb.append("   U.USERNAME AS UPDATER ");
		sb.append("   , ");
		sb.append("   'permanent' AS TYPE ");
		sb.append(" FROM ");
		sb.append("   RESOLUTION AS R ");
		sb.append("   LEFT OUTER JOIN ( ");
		sb.append("     REF_").append(enttableref).append("_RESOLUTION AS E ");
		sb.append("     JOIN RESOLUTION_DETAIL AS D ON E.RESOLUTION_DETAIL_ID = D.ID AND E.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND E.ACTIVE = 'Y' AND E.").append(entidref).append(" IN ( ");
		sb.append(entityid);
		if (parentid > 0) { sb.append(" , ").append(parentid); }
		if (grandparentid > 0) { sb.append(" , ").append(grandparentid); }
		if (projectid < 1 && childid.size() > 0) {
			for (int i=0; i<childid.size(); i++) {
				int cid = childid.get(i);
				sb.append(" , ").append(cid);
			}
		}
		if (projectid < 1 && grandchildid.size() > 0) {
			for (int i=0; i<grandchildid.size(); i++) {
				int gid = grandchildid.get(i);
				sb.append(" , ").append(gid);
			}
		}
		sb.append("     ) ");
		sb.append("   ) ON D.RESOLUTION_ID = R.ID AND R.ACTIVE = 'Y' ");
		sb.append("   LEFT OUTER JOIN USERS AS RC ON R.CREATED_BY = RC.ID ");
		sb.append("   LEFT OUTER JOIN USERS AS RU ON R.UPDATED_BY = RU.ID ");
		sb.append("   LEFT OUTER JOIN USERS AS C ON D.CREATED_BY = C.ID ");
		sb.append("   LEFT OUTER JOIN USERS AS U ON D.UPDATED_BY = U.ID ");
		sb.append("   LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID ");
		sb.append(" WHERE ");
		sb.append(" R.ID = ").append(resid);

		if (projectid < 1) {
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append("   R.ID AS RESOLUTION_ID ");
			sb.append("   , ");
			sb.append("   R.RESOLUTION_NUMBER ");
			sb.append("   , ");
			sb.append("   R.TITLE ");
			sb.append("   , ");
			sb.append("   'project' AS REF ");
			sb.append("   , ");
			sb.append("   PR.ID AS REF_ID ");
			sb.append("   , ");
			sb.append("   PR.PROJECT_NBR AS REF_NUMBER ");
			sb.append("   , ");
			sb.append("   R.ADOPTED_DATE ");
			sb.append("   , ");
			sb.append("   RC.USERNAME AS RESOLUTION_CREATOR ");
			sb.append("   , ");
			sb.append("   RU.USERNAME AS RESOLUTION_UPDATER ");
			sb.append("   , ");
			sb.append("   R.CREATED_BY AS RESOLUTION_CREATED_BY ");
			sb.append("   , ");
			sb.append("   R.UPDATED_BY AS RESOLUTION_UPDATED_BY ");
			sb.append("   , ");
			sb.append("   R.CREATED_DATE AS RESOLUTION_CREATED_DATE ");
			sb.append("   , ");
			sb.append("   R.UPDATED_DATE AS RESOLUTION_UPDATED_DATE ");
			sb.append("   , ");
			sb.append("   D.ID ");
			sb.append("   , ");
			sb.append("   D.PART ");
			sb.append("   , ");
			sb.append("   D.NAME ");
			sb.append("   , ");
			sb.append("   D.DESCRIPTION ");
			sb.append("   , ");
			sb.append("   D.RESOLUTION_DATE ");
			sb.append("   , ");
			sb.append("   D.EXP_DATE ");
			sb.append("   , ");
			sb.append("   D.LKUP_RESOLUTION_STATUS_ID ");
			sb.append("   , ");
			sb.append("   S.STATUS ");
			sb.append("   , ");
			sb.append("   S.APPROVED ");
			sb.append("   , ");
			sb.append("   S.FINAL ");
			sb.append("   , ");
			sb.append("   D.CREATED_BY ");
			sb.append("   , ");
			sb.append("   D.UPDATED_BY ");
			sb.append("   , ");
			sb.append("   D.CREATED_DATE ");
			sb.append("   , ");
			sb.append("   D.UPDATED_DATE ");
			sb.append("   , ");
			sb.append("   C.USERNAME AS CREATOR ");
			sb.append("   , ");
			sb.append("   U.USERNAME AS UPDATER ");
			sb.append("   , ");
			sb.append("   'permanent' AS TYPE ");
			sb.append(" FROM ");
			sb.append("   RESOLUTION AS R ");
			sb.append("   LEFT OUTER JOIN ( ");
			sb.append("     RESOLUTION_DETAIL AS D ");
			sb.append("     JOIN REF_PROJECT_RESOLUTION AS P ON P.RESOLUTION_DETAIL_ID = D.ID AND P.ACTIVE = 'Y' AND D.ACTIVE = 'Y' ");
			sb.append("     JOIN REF_").append(enttableref).append("_PROJECT AS RD ON RD.ACTIVE = 'Y' AND P.PROJECT_ID = RD.PROJECT_ID AND RD.").append(entidref).append(" IN ( ");
			sb.append(entityid);
			if (childid.size() > 0) {
				for (int i=0; i<childid.size(); i++) {
					int cid = childid.get(i);
					sb.append(" , ").append(cid);
				}
			}
			if (grandchildid.size() > 0) {
				for (int i=0; i<grandchildid.size(); i++) {
					int gid = grandchildid.get(i);
					sb.append(" , ").append(gid);
				}
			}
			sb.append("     ) ");
			sb.append("   ) ON D.RESOLUTION_ID = R.ID AND R.ACTIVE = 'Y' ");
			sb.append("   JOIN PROJECT AS PR ON RD.PROJECT_ID = PR.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS RC ON R.CREATED_BY = RC.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS RU ON R.UPDATED_BY = RU.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS C ON D.CREATED_BY = C.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS U ON D.UPDATED_BY = U.ID ");
			sb.append("   LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID ");
			sb.append(" WHERE ");
			sb.append(" R.ID = ").append(resid);
		}
		else {
			if (projectid > 0) {
				sb.append(" UNION ");

				sb.append(" SELECT ");
				sb.append("   R.ID AS RESOLUTION_ID ");
				sb.append("   , ");
				sb.append("   R.RESOLUTION_NUMBER ");
				sb.append("   , ");
				sb.append("   R.TITLE ");
				sb.append("   , ");
				sb.append("   'project' AS REF ");
				sb.append("   , ");
				sb.append("   PR.ID AS REF_ID ");
				sb.append("   , ");
				sb.append("   PR.PROJECT_NBR AS REF_NUMBER ");
				sb.append("   , ");
				sb.append("   R.ADOPTED_DATE ");
				sb.append("   , ");
				sb.append("   RC.USERNAME AS RESOLUTION_CREATOR ");
				sb.append("   , ");
				sb.append("   RU.USERNAME AS RESOLUTION_UPDATER ");
				sb.append("   , ");
				sb.append("   R.CREATED_BY AS RESOLUTION_CREATED_BY ");
				sb.append("   , ");
				sb.append("   R.UPDATED_BY AS RESOLUTION_UPDATED_BY ");
				sb.append("   , ");
				sb.append("   R.CREATED_DATE AS RESOLUTION_CREATED_DATE ");
				sb.append("   , ");
				sb.append("   R.UPDATED_DATE AS RESOLUTION_UPDATED_DATE ");
				sb.append("   , ");
				sb.append("   D.ID ");
				sb.append("   , ");
				sb.append("   D.PART ");
				sb.append("   , ");
				sb.append("   D.NAME ");
				sb.append("   , ");
				sb.append("   D.DESCRIPTION ");
				sb.append("   , ");
				sb.append("   D.RESOLUTION_DATE ");
				sb.append("   , ");
				sb.append("   D.EXP_DATE ");
				sb.append("   , ");
				sb.append("   D.LKUP_RESOLUTION_STATUS_ID ");
				sb.append("   , ");
				sb.append("   S.STATUS ");
				sb.append("   , ");
				sb.append("   S.APPROVED ");
				sb.append("   , ");
				sb.append("   S.FINAL ");
				sb.append("   , ");
				sb.append("   D.CREATED_BY ");
				sb.append("   , ");
				sb.append("   D.UPDATED_BY ");
				sb.append("   , ");
				sb.append("   D.CREATED_DATE ");
				sb.append("   , ");
				sb.append("   D.UPDATED_DATE ");
				sb.append("   , ");
				sb.append("   C.USERNAME AS CREATOR ");
				sb.append("   , ");
				sb.append("   U.USERNAME AS UPDATER ");
				sb.append("   , ");
				sb.append("   'temporary' AS TYPE ");
				sb.append(" FROM ");
				sb.append("   RESOLUTION AS R ");
				sb.append("   LEFT OUTER JOIN ( ");
				sb.append("     REF_PROJECT_RESOLUTION AS P ");
				sb.append("     JOIN RESOLUTION_DETAIL AS D ON P.RESOLUTION_DETAIL_ID = D.ID AND P.ACTIVE = 'Y' AND D.ACTIVE = 'Y' ");
				sb.append("     JOIN PROJECT AS PR ON P.PROJECT_ID = PR.ID AND PR.ACTIVE = 'Y' AND P.PROJECT_ID = ").append(projectid);
				sb.append("   ) ON D.RESOLUTION_ID = R.ID AND R.ACTIVE = 'Y' ");
				sb.append("   LEFT OUTER JOIN USERS AS RC ON R.CREATED_BY = RC.ID ");
				sb.append("   LEFT OUTER JOIN USERS AS RU ON R.UPDATED_BY = RU.ID ");
				sb.append("   LEFT OUTER JOIN USERS AS C ON D.CREATED_BY = C.ID ");
				sb.append("   LEFT OUTER JOIN USERS AS U ON D.UPDATED_BY = U.ID ");
				sb.append("   LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID ");
				sb.append(" WHERE ");
				sb.append(" R.ID = ").append(resid);
			}
			if (activityid > 0) {
				sb.append(" UNION ");

				sb.append(" SELECT ");
				sb.append("   R.ID AS RESOLUTION_ID ");
				sb.append("   , ");
				sb.append("   R.RESOLUTION_NUMBER ");
				sb.append("   , ");
				sb.append("   R.TITLE ");
				sb.append("   , ");
				sb.append("   'activity' AS REF ");
				sb.append("   , ");
				sb.append("   ACT.ID AS REF_ID ");
				sb.append("   , ");
				sb.append("   ACT.ACT_NBR AS REF_NUMBER ");
				sb.append("   , ");
				sb.append("   R.ADOPTED_DATE ");
				sb.append("   , ");
				sb.append("   RC.USERNAME AS RESOLUTION_CREATOR ");
				sb.append("   , ");
				sb.append("   RU.USERNAME AS RESOLUTION_UPDATER ");
				sb.append("   , ");
				sb.append("   R.CREATED_BY AS RESOLUTION_CREATED_BY ");
				sb.append("   , ");
				sb.append("   R.UPDATED_BY AS RESOLUTION_UPDATED_BY ");
				sb.append("   , ");
				sb.append("   R.CREATED_DATE AS RESOLUTION_CREATED_DATE ");
				sb.append("   , ");
				sb.append("   R.UPDATED_DATE AS RESOLUTION_UPDATED_DATE ");
				sb.append("   , ");
				sb.append("   D.ID ");
				sb.append("   , ");
				sb.append("   D.PART ");
				sb.append("   , ");
				sb.append("   D.NAME ");
				sb.append("   , ");
				sb.append("   D.DESCRIPTION ");
				sb.append("   , ");
				sb.append("   D.RESOLUTION_DATE ");
				sb.append("   , ");
				sb.append("   D.EXP_DATE ");
				sb.append("   , ");
				sb.append("   D.LKUP_RESOLUTION_STATUS_ID ");
				sb.append("   , ");
				sb.append("   S.STATUS ");
				sb.append("   , ");
				sb.append("   S.APPROVED ");
				sb.append("   , ");
				sb.append("   S.FINAL ");
				sb.append("   , ");
				sb.append("   D.CREATED_BY ");
				sb.append("   , ");
				sb.append("   D.UPDATED_BY ");
				sb.append("   , ");
				sb.append("   D.CREATED_DATE ");
				sb.append("   , ");
				sb.append("   D.UPDATED_DATE ");
				sb.append("   , ");
				sb.append("   C.USERNAME AS CREATOR ");
				sb.append("   , ");
				sb.append("   U.USERNAME AS UPDATER ");
				sb.append("   , ");
				sb.append("   'temporary' AS TYPE ");
				sb.append(" FROM ");
				sb.append("   RESOLUTION AS R ");
				sb.append("   LEFT OUTER JOIN ( ");
				sb.append("     REF_ACT_RESOLUTION AS A ");
				sb.append("     JOIN ACTIVITY AS ACT ON A.ACTIVITY_ID = ACT.ID AND ACT.ACTIVE = 'Y' AND ACT.ID = ").append(activityid);
				sb.append("     JOIN RESOLUTION_DETAIL AS D ON A.RESOLUTION_DETAIL_ID = D.ID AND A.ACTIVE = 'Y' AND D.ACTIVE = 'Y' ");
				sb.append("   ) ON D.RESOLUTION_ID = R.ID AND R.ACTIVE = 'Y' ");
				sb.append("   LEFT OUTER JOIN USERS AS RC ON R.CREATED_BY = RC.ID ");
				sb.append("   LEFT OUTER JOIN USERS AS RU ON R.UPDATED_BY = RU.ID ");
				sb.append("   LEFT OUTER JOIN USERS AS C ON D.CREATED_BY = C.ID ");
				sb.append("   LEFT OUTER JOIN USERS AS U ON D.UPDATED_BY = U.ID ");
				sb.append("   LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID ");
				sb.append(" WHERE ");
				sb.append(" R.ID = ").append(resid);
			}

		}

		sb.append(" ) ");
		sb.append(" , M AS ( ");
		sb.append("    SELECT Q.RESOLUTION_ID, MAX(Q.RESOLUTION_DATE) AS MAXDATE FROM Q GROUP BY Q.RESOLUTION_ID ");
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append("   Q.* ");
		sb.append("   , ");
		sb.append("   C.COMPLIANCE_DATE ");
		sb.append("   , ");
		sb.append("   AC.COMPLIANCE_DATE AS APPLICATION_COMPLIANCE_DATE ");
		sb.append("   , ");
		sb.append("   M.MAXDATE ");
		sb.append(" FROM ");
		sb.append("   Q ");
		sb.append("   LEFT OUTER JOIN M ON Q.RESOLUTION_ID = M.RESOLUTION_ID ");
		sb.append("   LEFT OUTER JOIN RESOLUTION_COMPLIANCE AS C ON Q.ID = C.RESOLUTION_DETAIL_ID AND C.ACTIVE = 'Y' AND C.PROJECT_ID = ").append(projectid);
		sb.append("   LEFT OUTER JOIN RESOLUTION_APPLICATION_COMPLIANCE AS AC ON Q.ID = AC.RESOLUTION_DETAIL_ID AND AC.ACTIVE = 'Y' AND AC.PROJECT_ID = ").append(projectid);
		sb.append(" ORDER BY Q.PART ASC, Q.RESOLUTION_CREATED_DATE DESC, Q.RESOLUTION_ID, Q.RESOLUTION_DATE ");
		return sb.toString();
	}

	public static String getHistory(int detailid) {
		if (detailid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" H.* ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" FROM ");
		sb.append(" RESOLUTION_HISTORY AS H ");
		sb.append(" LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON H.LKUP_RESOLUTION_STATUS_ID = S.ID AND S.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append(" H.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" H.RESOLUTION_DETAIL_ID = ").append(detailid);
		sb.append(" ORDER BY ");
		sb.append(" H.HISTORY_DATE DESC ");
		return sb.toString();
	}

	public static String status() {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" STATUS AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		sb.append(" APPROVED ");
		sb.append(" , ");
		sb.append(" FINAL ");
		sb.append(" FROM ");
		sb.append(" LKUP_RESOLUTION_STATUS ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String updateAdopted(int resid) {
		if (resid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE RESOLUTION SET ADOPTED_DATE = ( ");
		sb.append(" SELECT ");
		sb.append(" MAX(D.RESOLUTION_DATE) AS RESDATE ");
		sb.append(" FROM ");
		sb.append(" RESOLUTION_DETAIL AS D ");
		sb.append(" JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID AND D.ACTIVE = 'Y' AND D.RESOLUTION_ID = ").append(resid);
		sb.append(" WHERE ");
		sb.append(" ( ");
		sb.append("   SELECT ");
		sb.append("   COUNT(DISTINCT SD.ID) AS CNT ");
		sb.append("   FROM ");
		sb.append("   RESOLUTION_DETAIL AS SD ");
		sb.append("   JOIN LKUP_RESOLUTION_STATUS AS SS ON SD.LKUP_RESOLUTION_STATUS_ID = SS.ID AND SD.ACTIVE = 'Y' AND SD.RESOLUTION_ID = D.RESOLUTION_ID AND (SS.UNAPPROVED = 'Y' OR (SS.APPROVED = 'N' AND SS.FINAL = 'N')) ");
		sb.append("   ) < 1 ");
		sb.append(" ) WHERE ID = ").append(resid);
		return sb.toString();
	}

	public static String deleteDetail(int detailid, int userid, String ip) {
		if (detailid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE RESOLUTION_DETAIL SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(detailid);
		return sb.toString();
	}

	public static String deleteRef(String type, int typeid, int detailid, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_").append(tableref).append("_RESOLUTION SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ").append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(" RESOLUTION_DETAIL_ID = ").append(detailid);
		return sb.toString();
	}

	public static String updateMultiLevel(String type, int typeid, int resid, int statusid, Timekeeper date, int userid, String ip) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		String ent = entity.getEntity();
		String enttableref = CsReflect.getTableRef(ent);
		String entidref = CsReflect.getFieldIdRef(ent);

		int entityid = entity.getEntityid();
		int parentid = entity.getParentid();
		int grandparentid = entity.getGrandparentid();
		int projectid = entity.getProjectid();
		int activityid = entity.getActivityid();
		
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE RESOLUTION_DETAIL SET ");
		sb.append(" LKUP_RESOLUTION_STATUS_ID = ").append(statusid);
		sb.append(" , ");
		sb.append(" RESOLUTION_DATE = ").append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append("  Output Inserted.ID ");
		sb.append(" WHERE ");
		sb.append(" ID IN ( ");
		sb.append(" SELECT ");
		sb.append(" D.ID ");
		sb.append(" FROM ");
		sb.append(" REF_").append(enttableref).append("_RESOLUTION AS R ");
		sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.RESOLUTION_DETAIL_ID = D.ID AND R.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND D.RESOLUTION_ID = ").append(resid).append(" AND R.").append(entidref).append(" IN ( ");
		sb.append(entityid);
		if (parentid > 0) {
			sb.append(" , ").append(parentid);
		}
		if (grandparentid > 0) {
			sb.append(" , ").append(grandparentid);
		}
		sb.append(" ) ");
		if (!type.equalsIgnoreCase("project") && !type.equalsIgnoreCase("activity")) {
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" D.ID ");
			sb.append(" FROM ");
			sb.append(" REF_PROJECT_RESOLUTION AS R ");
			sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.RESOLUTION_DETAIL_ID = D.ID AND D.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND D.RESOLUTION_ID = ").append(resid);
			sb.append(" JOIN REF_").append(enttableref).append("_PROJECT AS P ON R.PROJECT_ID = P.PROJECT_ID AND P.ACTIVE = 'Y'  AND P.").append(entidref).append(" = ").append(entityid);
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" D.ID ");
			sb.append(" FROM ");
			sb.append(" REF_ACT_RESOLUTION AS R ");
			sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.RESOLUTION_DETAIL_ID = D.ID AND D.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND D.RESOLUTION_ID = ").append(resid);
			sb.append(" JOIN REF_").append(enttableref).append("_PROJECT AS P ON P.ACTIVE = 'Y'  AND P.").append(entidref).append(" = ").append(entityid);
			sb.append(" JOIN ACTIVITY AS A ON P.PROJECT_ID = A.PROJECT_ID AND A.ACTIVE = 'Y' AND R.ACTIVITY_ID = A.ID ");
		}
		else {
			if (projectid > 0) {
				sb.append(" UNION ");
				sb.append(" SELECT ");
				sb.append(" D.ID ");
				sb.append(" FROM ");
				sb.append(" REF_PROJECT_RESOLUTION AS R ");
				sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.RESOLUTION_DETAIL_ID = D.ID AND R.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND D.RESOLUTION_ID = ").append(resid).append(" AND R.PROJECT_ID = ").append(projectid);
			}
			if (activityid > 0) {
				sb.append(" UNION ");
				sb.append(" SELECT ");
				sb.append(" D.ID ");
				sb.append(" FROM ");
				sb.append(" REF_ACT_RESOLUTION AS R ");
				sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.RESOLUTION_DETAIL_ID = D.ID AND R.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND D.RESOLUTION_ID = ").append(resid).append(" AND R.ACTIVITY_ID = ").append(activityid);
			}
		}
		sb.append(" ) ");
		return sb.toString();
	}

	public static String updateMultiAll(int resid, int statusid, Timekeeper date, int userid, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE RESOLUTION_DETAIL Output Inserted.ID SET ");
		sb.append(" LKUP_RESOLUTION_STATUS_ID = ").append(statusid);
		sb.append(" , ");
		sb.append(" RESOLUTION_DATE = ").append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append("  Output Inserted.ID ");
		sb.append(" WHERE ");
		sb.append(" RESOLUTION_ID = ").append(resid);
		return sb.toString();
	}

	public static String expireMultiLevel(String type, int typeid, int resid, String expdate, int userid, String ip) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		String ent = entity.getEntity();
		String enttableref = CsReflect.getTableRef(ent);
		String entidref = CsReflect.getFieldIdRef(ent);

		int entityid = entity.getEntityid();
		int parentid = entity.getParentid();
		int grandparentid = entity.getGrandparentid();
		int projectid = entity.getProjectid();
		int activityid = entity.getActivityid();
		
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE RESOLUTION_DETAIL SET ");
		sb.append(" EXP_DATE = ");
		if (Operator.hasValue(expdate)) {
			Timekeeper e = new Timekeeper();
			e.setDate(expdate);
			sb.append(e.sqlDatetime());
		}
		else {
			sb.append(" null ");
		}
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" Output Inserted.ID ");
		sb.append(" WHERE ");
		sb.append(" ID IN ( ");
		sb.append(" SELECT ");
		sb.append(" D.ID ");
		sb.append(" FROM ");
		sb.append(" REF_").append(enttableref).append("_RESOLUTION AS R ");
		sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.RESOLUTION_DETAIL_ID = D.ID AND R.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND D.RESOLUTION_ID = ").append(resid).append(" AND R.").append(entidref).append(" IN ( ");
		sb.append(entityid);
		if (parentid > 0) {
			sb.append(" , ").append(parentid);
		}
		if (grandparentid > 0) {
			sb.append(" , ").append(grandparentid);
		}
		sb.append(" ) ");
		if (!type.equalsIgnoreCase("project") && !type.equalsIgnoreCase("activity")) {
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" D.ID ");
			sb.append(" FROM ");
			sb.append(" REF_PROJECT_RESOLUTION AS R ");
			sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.RESOLUTION_DETAIL_ID = D.ID AND D.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND D.RESOLUTION_ID = ").append(resid);
			sb.append(" JOIN REF_").append(enttableref).append("_PROJECT AS P ON R.PROJECT_ID = P.PROJECT_ID AND P.ACTIVE = 'Y'  AND P.").append(entidref).append(" = ").append(entityid);
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" D.ID ");
			sb.append(" FROM ");
			sb.append(" REF_ACT_RESOLUTION AS R ");
			sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.RESOLUTION_DETAIL_ID = D.ID AND D.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND D.RESOLUTION_ID = ").append(resid);
			sb.append(" JOIN REF_").append(enttableref).append("_PROJECT AS P ON P.ACTIVE = 'Y'  AND P.").append(entidref).append(" = ").append(entityid);
			sb.append(" JOIN ACTIVITY AS A ON P.PROJECT_ID = A.PROJECT_ID AND A.ACTIVE = 'Y' AND R.ACTIVITY_ID = A.ID ");
		}
		else {
			if (projectid > 0) {
				sb.append(" UNION ");
				sb.append(" SELECT ");
				sb.append(" D.ID ");
				sb.append(" FROM ");
				sb.append(" REF_PROJECT_RESOLUTION AS R ");
				sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.RESOLUTION_DETAIL_ID = D.ID AND R.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND D.RESOLUTION_ID = ").append(resid).append(" AND R.PROJECT_ID = ").append(projectid);
			}
			if (activityid > 0) {
				sb.append(" UNION ");
				sb.append(" SELECT ");
				sb.append(" D.ID ");
				sb.append(" FROM ");
				sb.append(" REF_ACT_RESOLUTION AS R ");
				sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.RESOLUTION_DETAIL_ID = D.ID AND R.ACTIVE = 'Y' AND D.ACTIVE = 'Y' AND D.RESOLUTION_ID = ").append(resid).append(" AND R.ACTIVITY_ID = ").append(activityid);
			}
		}
		sb.append(" ) ");

		return sb.toString();
	}

	public static String expireMultiAll(int resid, String expdate, int userid, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE RESOLUTION_DETAIL SET ");
		sb.append(" EXP_DATE = ");
		if (Operator.hasValue(expdate)) {
			Timekeeper e = new Timekeeper();
			e.setDate(expdate);
			sb.append(e.sqlDatetime());
		}
		else {
			sb.append(" null ");
		}
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" Output Inserted.ID ");
		sb.append(" WHERE ");
		sb.append(" RESOLUTION_ID = ").append(resid);
		return sb.toString();
	}

	public static String getResolution(String type, int typeid, String resnum, TypeInfo entity) {
		if (!Operator.hasValue(type) || typeid < 1 || !Operator.hasValue(resnum)) { return ""; }
		String ent = entity.getEntity();
		String enttableref = CsReflect.getTableRef(ent);
		String entidref = CsReflect.getFieldIdRef(ent);

		int entityid = entity.getEntityid();
		int parentid = entity.getParentid();
		int grandparentid = entity.getGrandparentid();
		int projectid = entity.getProjectid();
		int activityid = entity.getActivityid();
		StringBuilder sb = new StringBuilder();
		boolean empty = true;

		sb.append(" WITH Q AS ( ");
		if (activityid > 0) {
			sb.append(" SELECT ");
			sb.append(" R.* ");
			sb.append(" FROM ");
			sb.append(" RESOLUTION AS R ");
			sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.ID = D.RESOLUTION_ID AND D.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND LOWER(R.RESOLUTION_NUMBER) = LOWER('").append(Operator.sqlEscape(resnum)).append("') ");
			sb.append(" JOIN REF_ACT_RESOLUTION AS RAR ON D.ID = RAR.RESOLUTION_DETAIL_ID AND RAR.ACTIVE = 'Y' AND RAR.ACTIVITY_ID = ").append(activityid);
			empty = false;
		}
		if (projectid > 0) {
			if (!empty) { sb.append(" UNION "); }
			sb.append(" SELECT ");
			sb.append(" R.* ");
			sb.append(" FROM ");
			sb.append(" RESOLUTION AS R ");
			sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.ID = D.RESOLUTION_ID AND D.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND LOWER(R.RESOLUTION_NUMBER) = LOWER('").append(Operator.sqlEscape(resnum)).append("') ");
			sb.append(" JOIN REF_PROJECT_RESOLUTION AS RPR ON D.ID = RPR.RESOLUTION_DETAIL_ID AND RPR.ACTIVE = 'Y' AND RPR.PROJECT_ID = ").append(projectid);
			empty = false;
		}
		if (entityid > 0) {
			if (!empty) { sb.append(" UNION "); }
			sb.append(" SELECT ");
			sb.append(" R.* ");
			sb.append(" FROM ");
			sb.append(" RESOLUTION AS R ");
			sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.ID = D.RESOLUTION_ID AND D.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND LOWER(R.RESOLUTION_NUMBER) = LOWER('").append(Operator.sqlEscape(resnum)).append("') ");
			sb.append(" JOIN REF_").append(enttableref).append("_RESOLUTION AS RER ON D.ID = RER.RESOLUTION_DETAIL_ID AND RER.ACTIVE = 'Y' AND RER.").append(entidref).append(" = ").append(entityid);
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" R.* ");
			sb.append(" FROM ");
			sb.append(" RESOLUTION AS R ");
			sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.ID = D.RESOLUTION_ID AND D.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND LOWER(R.RESOLUTION_NUMBER) = LOWER('").append(Operator.sqlEscape(resnum)).append("') ");
			sb.append(" JOIN REF_PROJECT_RESOLUTION AS RPR ON D.ID = RPR.RESOLUTION_DETAIL_ID AND RPR.ACTIVE = 'Y' ");
			sb.append(" JOIN REF_").append(enttableref).append("_PROJECT AS REP ON RPR.PROJECT_ID = REP.PROJECT_ID AND REP.ACTIVE = 'Y' AND REP.").append(entidref).append(" = ").append(entityid);
			empty = false;
		}
		if (parentid > 0) {
			if (!empty) { sb.append(" UNION "); }
			sb.append(" SELECT ");
			sb.append(" R.* ");
			sb.append(" FROM ");
			sb.append(" RESOLUTION AS R ");
			sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.ID = D.RESOLUTION_ID AND D.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND LOWER(R.RESOLUTION_NUMBER) = LOWER('").append(Operator.sqlEscape(resnum)).append("') ");
			sb.append(" JOIN REF_").append(enttableref).append("_RESOLUTION AS RER ON D.ID = RER.RESOLUTION_DETAIL_ID AND RER.ACTIVE = 'Y' AND RER.").append(entidref).append(" = ").append(parentid);
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" R.* ");
			sb.append(" FROM ");
			sb.append(" RESOLUTION AS R ");
			sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.ID = D.RESOLUTION_ID AND D.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND LOWER(R.RESOLUTION_NUMBER) = LOWER('").append(Operator.sqlEscape(resnum)).append("') ");
			sb.append(" JOIN REF_PROJECT_RESOLUTION AS RPR ON D.ID = RPR.RESOLUTION_DETAIL_ID AND RPR.ACTIVE = 'Y' ");
			sb.append(" JOIN REF_").append(enttableref).append("_PROJECT AS REP ON RPR.PROJECT_ID = REP.PROJECT_ID AND REP.ACTIVE = 'Y' AND REP.").append(entidref).append(" = ").append(entityid);
			empty = false;
		}
		if (grandparentid > 0) {
			if (!empty) { sb.append(" UNION "); }
			sb.append(" SELECT ");
			sb.append(" R.* ");
			sb.append(" FROM ");
			sb.append(" RESOLUTION AS R ");
			sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.ID = D.RESOLUTION_ID AND D.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND LOWER(R.RESOLUTION_NUMBER) = LOWER('").append(Operator.sqlEscape(resnum)).append("') ");
			sb.append(" JOIN REF_").append(enttableref).append("_RESOLUTION AS RER ON D.ID = RER.RESOLUTION_DETAIL_ID AND RER.ACTIVE = 'Y' AND RER.").append(entidref).append(" = ").append(grandparentid);
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" R.* ");
			sb.append(" FROM ");
			sb.append(" RESOLUTION AS R ");
			sb.append(" JOIN RESOLUTION_DETAIL AS D ON R.ID = D.RESOLUTION_ID AND D.ACTIVE = 'Y' AND R.ACTIVE = 'Y' AND LOWER(R.RESOLUTION_NUMBER) = LOWER('").append(Operator.sqlEscape(resnum)).append("') ");
			sb.append(" JOIN REF_PROJECT_RESOLUTION AS RPR ON D.ID = RPR.RESOLUTION_DETAIL_ID AND RPR.ACTIVE = 'Y' ");
			sb.append(" JOIN REF_").append(enttableref).append("_PROJECT AS REP ON RPR.PROJECT_ID = REP.PROJECT_ID AND REP.ACTIVE = 'Y' AND REP.").append(entidref).append(" = ").append(entityid);
			empty = false;
		}
		sb.append(" ) ");
		sb.append(" SELECT TOP 1 * FROM Q ORDER BY UPDATED_DATE DESC ");

		return sb.toString();
	}

	public static String getStatus() {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM LKUP_RESOLUTION_STATUS WHERE ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String id(String type, int typeid, int histid) {
		if (histid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * ");
		sb.append(" FROM ");
		sb.append(" RESOLUTION_HISTORY ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(histid);
		return sb.toString();
	}

	public static String getDetail(int detailid) {
		if (detailid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" D.ID ");
		sb.append(" , ");
		sb.append(" D.RESOLUTION_ID ");
		sb.append(" , ");
		sb.append(" D.ID AS RESOLUTION_DETAIL_ID ");
		sb.append(" , ");
		sb.append(" R.RESOLUTION_NUMBER ");
		sb.append(" , ");
		sb.append(" R.TITLE ");
		sb.append(" , ");
		sb.append(" R.ADOPTED_DATE ");
		sb.append(" , ");
		sb.append(" D.PART ");
		sb.append(" , ");
		sb.append(" D.NAME ");
		sb.append(" , ");
		sb.append(" D.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" D.RESOLUTION_DATE ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" , ");
		sb.append(" D.LKUP_RESOLUTION_STATUS_ID ");
		sb.append(" , ");
		sb.append(" D.REF ");
		sb.append(" , ");
		sb.append(" D.REF_ID ");
		sb.append(" , ");
		sb.append(" D.REF_NUMBER ");
		sb.append(" , ");
		sb.append(" D.EXP_DATE ");
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" D.ACTIVE ");
		sb.append(" , ");
		sb.append(" D.CREATED_BY ");
		sb.append(" , ");
		sb.append(" D.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" U.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" D.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" D.UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" D.CREATED_IP ");
		sb.append(" , ");
		sb.append(" D.UPDATED_IP ");
		sb.append(" FROM ");
		sb.append(" RESOLUTION_DETAIL AS D ");
		sb.append(" JOIN RESOLUTION AS R ON D.RESOLUTION_ID = R.ID AND D.ID = ").append(detailid);
		sb.append(" LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON D.UPDATED_BY = U.ID ");
		return sb.toString();
	}

	public static String getDetails(int[] detailid) {
		if (!Operator.hasValue(detailid)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" D.ID ");
		sb.append(" , ");
		sb.append(" D.RESOLUTION_ID ");
		sb.append(" , ");
		sb.append(" D.ID AS RESOLUTION_DETAIL_ID ");
		sb.append(" , ");
		sb.append(" R.RESOLUTION_NUMBER ");
		sb.append(" , ");
		sb.append(" R.TITLE ");
		sb.append(" , ");
		sb.append(" R.ADOPTED_DATE ");
		sb.append(" , ");
		sb.append(" D.PART ");
		sb.append(" , ");
		sb.append(" D.NAME ");
		sb.append(" , ");
		sb.append(" D.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" D.RESOLUTION_DATE ");
		sb.append(" , ");
		sb.append(" S.STATUS ");
		sb.append(" , ");
		sb.append(" D.LKUP_RESOLUTION_STATUS_ID ");
		sb.append(" , ");
		sb.append(" D.REF ");
		sb.append(" , ");
		sb.append(" D.REF_ID ");
		sb.append(" , ");
		sb.append(" D.REF_NUMBER ");
		sb.append(" , ");
		sb.append(" D.EXP_DATE ");
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" D.ACTIVE ");
		sb.append(" , ");
		sb.append(" D.CREATED_BY ");
		sb.append(" , ");
		sb.append(" D.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" U.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" D.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" D.UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" D.CREATED_IP ");
		sb.append(" , ");
		sb.append(" D.UPDATED_IP ");
		sb.append(" FROM ");
		sb.append(" RESOLUTION_DETAIL AS D ");
		sb.append(" JOIN RESOLUTION AS R ON D.RESOLUTION_ID = R.ID AND D.ID IN (").append(Operator.join(detailid, ",")).append(") ");
		sb.append(" LEFT OUTER JOIN LKUP_RESOLUTION_STATUS AS S ON D.LKUP_RESOLUTION_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS U ON D.UPDATED_BY = U.ID ");
		return sb.toString();
	}

	public static String addHistory(int resolutionid, int resolutiondetailid, String resolutionnumber, String title, String adopteddate, String part, String name, String description, String resolutiondate, String status, int lkupresolutionstatusid, String ref, int refid, String refnumber, String expdate, String active, int createdby, String createddate, String updated, int updatedby, String updateddate, String createdip, String updatedip) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO RESOLUTION_HISTORY ( ");
		sb.append(" RESOLUTION_ID ");
		sb.append(" , ");
		sb.append(" RESOLUTION_DETAIL_ID ");
		sb.append(" , ");
		sb.append(" RESOLUTION_NUMBER ");
		sb.append(" , ");
		sb.append(" TITLE ");
		sb.append(" , ");
		sb.append(" ADOPTED_DATE ");
		sb.append(" , ");
		sb.append(" PART ");
		sb.append(" , ");
		sb.append(" NAME ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		sb.append(" RESOLUTION_DATE ");
		sb.append(" , ");
		sb.append(" STATUS ");
		sb.append(" , ");
		sb.append(" LKUP_RESOLUTION_STATUS_ID ");
		sb.append(" , ");
		sb.append(" REF ");
		sb.append(" , ");
		sb.append(" REF_ID ");
		sb.append(" , ");
		sb.append(" REF_NUMBER ");
		sb.append(" , ");
		sb.append(" EXP_DATE ");
		sb.append(" , ");
		sb.append(" HISTORY_DATE ");
		sb.append(" , ");
		sb.append(" ACTIVE ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_DATE ");
		sb.append(" , ");
		sb.append(" UPDATED ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) Output Inserted.* VALUES ( ");
		sb.append(resolutionid);
		sb.append(" , ");
		sb.append(resolutiondetailid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(resolutionnumber)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(title)).append("' ");
		sb.append(" , ");

		if (Operator.hasValue(adopteddate)) {
			Timekeeper ad = new Timekeeper();
			ad.setDate(adopteddate);
			sb.append(ad.sqlDatetime());
		}
		else {
			sb.append(" null ");
		}

		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(part)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(name)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");

		if (Operator.hasValue(resolutiondate)) {
			Timekeeper rd = new Timekeeper();
			rd.setDate(resolutiondate);
			sb.append(rd.sqlDatetime());
		}
		else {
			sb.append(" null ");
		}

		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(status)).append("' ");
		sb.append(" , ");
		sb.append(lkupresolutionstatusid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ref)).append("' ");
		sb.append(" , ");
		sb.append(refid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(refnumber)).append("' ");
		sb.append(" , ");

		if (Operator.hasValue(expdate)) {
			Timekeeper ed = new Timekeeper();
			ed.setDate(expdate);
			sb.append(ed.sqlDatetime());
		}
		else {
			sb.append(" null ");
		}


		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(active)).append("' ");
		sb.append(" , ");
		sb.append(createdby);
		sb.append(" , ");

		if (Operator.hasValue(createddate)) {
			Timekeeper cd = new Timekeeper();
			cd.setDate(createddate);
			sb.append(cd.sqlDatetime());
		}
		else {
			sb.append(" null ");
		}

		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(updated)).append("' ");
		sb.append(" , ");
		sb.append(updatedby);
		sb.append(" , ");

		if (Operator.hasValue(updateddate)) {
			Timekeeper ud = new Timekeeper();
			ud.setDate(updateddate);
			sb.append(ud.sqlDatetime());
		}
		else {
			sb.append(" null ");
		}

		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(createdip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(updatedip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}








}















