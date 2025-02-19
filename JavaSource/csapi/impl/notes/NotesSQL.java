package csapi.impl.notes;

import alain.core.security.Token;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.general.GeneralSQL;
import csapi.utils.CsReflect;
import csshared.vo.RequestVO;

public class NotesSQL {

	public static String info(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}

	public static String summary(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}

	public static String ext(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}

	public static String list(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}

	public static String details(String type, int typeid, int id, Token u) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH C AS ( SELECT COUNT(DISTINCT ID) AS CONTENT_COUNT FROM CONTENT WHERE ACTIVE = 'Y' AND LOWER(TYPE) = 'notes') ");
		sb.append(" SELECT ");
		sb.append(" N.ID ");
		sb.append(" , ");
		sb.append(" N.NOTE ");
		
		sb.append(" , ");
		sb.append(" N.CREATED_BY ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" T.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" N.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" N.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" N.UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" N.LKUP_NOTES_TYPE_ID ");
		sb.append(" , ");
		sb.append("   CASE ");
		sb.append("     WHEN C.CONTENT_COUNT IS NULL THEN '' ");
		sb.append("     WHEN C.CONTENT_COUNT > 0 THEN 'notes' ");
		sb.append("   ELSE '' END AS CONTENT_TYPE, ");
		sb.append(" T.DESCRIPTION as TYPE ");
		
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_NOTES AS R ");
		if (Operator.equalsIgnoreCase(type, "appointment")) {
			sb.append(" JOIN APPOINTMENT_SCHEDULE AS SCHED ON R.APPOINTMENT_ID = SCHED.APPOINTMENT_ID ");
		}
		sb.append(" JOIN NOTES AS N ON R.NOTES_ID = N.ID AND N.ACTIVE = 'Y' AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_NOTES_TYPE AS T ON N.LKUP_NOTES_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		if (!u.isStaff()) {
			sb.append(" AND T.ISPUBLIC = 'Y' ");
		}
		sb.append(" LEFT OUTER JOIN USERS AS CU ON N.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON N.UPDATED_BY = UP.ID  ");
		sb.append(" LEFT OUTER JOIN C ON 1 = 1 ");
		sb.append(" WHERE ");
		if (Operator.equalsIgnoreCase(type, "appointment")) {
			sb.append(" SCHED.ID = ").append(typeid).append(" ");
		}
		else {
			sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		}
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" N.ID = ").append(id);
		}
		sb.append(" ORDER BY N.UPDATED_DATE DESC ");
		return sb.toString();
	}
	
	public static String id(String type, int typeid, int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" N.ID ");
		sb.append(" , ");
		sb.append(" N.NOTE ");
		sb.append(" , ");
		sb.append(" N.CREATED_BY ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" N.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" N.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" N.UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" N.LKUP_NOTES_TYPE_ID ");
		sb.append(" , ");
		sb.append(" T.DESCRIPTION as TYPE ");
		
		sb.append(" FROM ");
		sb.append(" NOTES AS N ");
		sb.append(" JOIN LKUP_NOTES_TYPE AS T ON N.LKUP_NOTES_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS CU ON N.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON N.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" N.ID = ").append(id);
		return sb.toString();
	}
	
	public static String updateNotes(RequestVO r, Token u) {
		return GeneralSQL.updateCommon(r,u);
	}
	
	
	public static String insertNotes(RequestVO r, Token u) {
		return GeneralSQL.insertCommon(r, u);
	}

	public static String addNote(String note, int notetype, int userid, String ip, Timekeeper date) {
		if (!Operator.hasValue(note)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO NOTES ( ");
		sb.append(" NOTE, LKUP_NOTES_TYPE_ID, CREATED_BY, CREATED_DATE, CREATED_IP, UPDATED_BY, UPDATED_DATE, UPDATED_IP ");
		sb.append(" ) VALUES ( ");
		sb.append(" '").append(Operator.sqlEscape(note)).append("' ");
		sb.append(" , ");
		sb.append(notetype);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String addNote(String note, String notetype, int userid, String ip, Timekeeper date) {
		if (!Operator.hasValue(note)) { return ""; }
		if (!Operator.hasValue(notetype)) { notetype = "Note"; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO NOTES ( ");
		sb.append(" NOTE, LKUP_NOTES_TYPE_ID, CREATED_BY, CREATED_DATE, CREATED_IP, UPDATED_BY, UPDATED_DATE, UPDATED_IP ");
		sb.append(" ) Output Inserted.ID VALUES ( ");
		sb.append(" '").append(Operator.sqlEscape(note)).append("' ");
		sb.append(" , ");
		sb.append(" ( SELECT TOP 1 ID FROM LKUP_NOTES_TYPE WHERE LOWER(TYPE) = LOWER('").append(notetype).append("') AND ACTIVE = 'Y' ) ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}
	
	public static String getNote(String notetype, int userid, Timekeeper date) {
		if (!Operator.hasValue(notetype)) { notetype = "Note"; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM NOTES WHERE ");
		sb.append(" LKUP_NOTES_TYPE_ID = ( SELECT TOP 1 ID FROM LKUP_NOTES_TYPE WHERE LOWER(TYPE) = LOWER('").append(notetype).append("') AND ACTIVE = 'Y' ) ");
		sb.append(" AND ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" AND ");
		sb.append(" UPDATED_DATE = ").append(date.sqlDatetime());
		return sb.toString();
	}

	public static String getNotes(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" N.ID ");
		sb.append(" , ");
		sb.append(" R.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" N.NOTE AS TEXT ");
		sb.append(" , ");
		sb.append(" T.TYPE AS DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" N.CREATED_DATE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_NOTES AS R ");
		sb.append(" JOIN NOTES AS N ON R.NOTES_ID = N.ID AND R.ACTIVE = 'Y' AND N.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_NOTES_TYPE AS T ON N.LKUP_NOTES_TYPE_ID = T.ID ");
		sb.append(" WHERE ");
		sb.append(" R.").append(idref).append(" = ").append(typeid);
		sb.append(" ORDER BY ");
		sb.append(" N.CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String addNoteRef(String type, int typeid, int noteid, int userid, String ip, Timekeeper date) {
		if (!Operator.hasValue(type)) { return ""; }
		if (typeid < 1) { return ""; }
		if (noteid < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ");
		sb.append(" REF_").append(tableref).append("_NOTES ");
		sb.append(" ( ").append(idref).append(" , NOTES_ID, CREATED_BY, CREATED_DATE, CREATED_IP, UPDATED_BY, UPDATED_DATE, UPDATED_IP ) VALUES ( ");
		sb.append(typeid);
		sb.append(" , ");
		sb.append(noteid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getRefIdNotes(RequestVO r, Token u) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select TOP 1 ID FROM ").append(Table.NOTESTABLE).append(" ");
		sb.append(" WHERE ID >0  ");
		sb.append(" AND ");
		/*sb.append(" PATH = '").append(Operator.sqlEscape(obj[2].getValue())).append("' ");
		sb.append(" AND ");*/
		sb.append(" CREATED_BY = ").append(u.getId());
		sb.append(" order by CREATED_DATE DESC ");
		return sb.toString();
	}
	
	public static String insertRefNotes(RequestVO r, Token u, int id) {
		return GeneralSQL.insertRefCommon(r,u,id);
	}


	public static String types() {
		return types("", -1, -1);
	}

	public static String type(int notetypeid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		sb.append(" ISPUBLIC ");
		sb.append(" , ");
		sb.append(" NOTIFY ");
		sb.append(" FROM ");
		sb.append(" LKUP_NOTES_TYPE ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(notetypeid);
		sb.append(" ORDER BY TYPE ");
		return sb.toString();
	}

	public static String types(String type, int typeid, int selected) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ID AS VALUE ");
		sb.append(" , ");
		sb.append(" TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" DESCRIPTION ");
		sb.append(" , ");
		sb.append(" ISPUBLIC ");
		sb.append(" , ");
		sb.append(" NOTIFY ");
		sb.append(" , ");
		if (selected > 0) {
			sb.append(" CASE ");
			sb.append("   WHEN ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" ELSE 'N' END AS SELECTED ");
		}
		else {
			sb.append(" DEFLT AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" LKUP_NOTES_TYPE ");
		sb.append(" WHERE ");
		sb.append(" ACTIVE = 'Y' ");
		sb.append(" ORDER BY TYPE ");
		return sb.toString();
	}

	public static String addRef(String type, int typeid, int noteid, int userid, String ip) {
		if (!Operator.hasValue(type)) { return ""; }
		if (typeid < 1) { return ""; }
		if (noteid < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_NOTES ( ");
		sb.append(idref);
		sb.append(" , ");
		sb.append(" NOTES_ID ");
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
		sb.append(noteid);
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

	public static String add(int notetype, String note, int userid, String ip) {
		if (notetype < 1) { return ""; }
		if (!Operator.hasValue(note)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO NOTES ( ");
		sb.append(" LKUP_NOTES_TYPE_ID ");
		sb.append(" , ");
		sb.append(" NOTE ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) Output Inserted.* VALUES ( ");
		sb.append(notetype);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(note)).append("' ");
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
	public static String addUserNote(String note, int notetypeid, int userid, String ip, Timekeeper date) {
		if (!Operator.hasValue(note)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO NOTES ( ");
		sb.append(" NOTE, LKUP_NOTES_TYPE_ID, CREATED_BY, CREATED_DATE, CREATED_IP, UPDATED_BY, UPDATED_DATE, UPDATED_IP ");
		sb.append(" ) Output Inserted.ID VALUES ( ");
		sb.append(" '").append(Operator.sqlEscape(note)).append("' ");
		sb.append(" , ");
		sb.append(notetypeid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(date.sqlDatetime());
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}
//	public static String update(int id, int notetype, String note, int userid, String ip) {
//		if (id < 1) { return ""; }
//		if (notetype < 1) { return ""; }
//		if (!Operator.hasValue(note)) { return ""; }
//		StringBuilder sb = new StringBuilder();
//		sb.append(" UPDATE NOTES SET ");
//		sb.append(" WHERE ");
//		sb.append(" ID = ").append(id);
//		return sb.toString();
//	}


	public static String DeleteRefNote(int id, int userid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_USERS_NOTES SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append("UPDATED_BY = ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate()   ");
		sb.append(" WHERE NOTES_ID= ");
		sb.append(id);
		return sb.toString();
	}

	public static String DeleteNote(int id, int userid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE NOTES SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append("UPDATED_BY = ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate()   ");
		sb.append(" WHERE ID= ");
		sb.append(id);
		return sb.toString();
	}









}















