package csapi.impl.vehicle;

import alain.core.security.Token;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.general.GeneralSQL;
import csapi.utils.CsReflect;
import csshared.vo.RequestVO;

public class VehicleSQL {

	public static String info(String type, int typeid, int id) {
		return details(type, typeid, -1);
	}

	public static String list(String type, int typeid, int id) {
		return details(type, typeid, -1);
	}

	public static String details(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" V.* ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" V.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" V.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" V.UPDATED_DATE ");
		
		sb.append(" FROM ");
		sb.append(" VEHICLE AS V ");
		
		sb.append(" JOIN REF_PROJECT_PARKING R on V.REF_PROJECT_PARKING_ID = R.ID ");
		sb.append(" JOIN PROJECT P on R.PROJECT_ID= P.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS CU ON V.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON V.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" P.ID = ").append(typeid);
		
		sb.append(" ORDER BY V.UPDATED_DATE DESC ");
		return sb.toString();
	}
	
	public static String updateVehicle(RequestVO r, Token u) {
		return GeneralSQL.updateCommon(r,u);
	}

	public static String update(int vehicleid, String license, String state, String exp, String make, String model, String color, int year, String blocked, int user, String ip) {
		String b = "N";
		if (Operator.equalsIgnoreCase(blocked, "Y")) {
			b = "Y";
		}
		Timekeeper e = new Timekeeper();
		e.setDate(exp);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE VEHICLE SET ");
		sb.append("   LICENSE_PLATE = '").append(license).append("' ");
		sb.append("   , ");
		sb.append("   REG_STATE = '").append(state).append("' ");
		sb.append("   , ");
		sb.append("   VEHICLE_MAKE = '").append(make).append("' ");
		sb.append("   , ");
		sb.append("   VEHICLE_MODEL = '").append(model).append("' ");
		sb.append("   , ");
		sb.append("   VEHICLE_COLOR = '").append(color).append("' ");
		sb.append("   , ");
		sb.append("   VEHICLE_YEAR = '").append(year).append("' ");
		if (Operator.hasValue(exp)) {
			sb.append("   , ");
			sb.append("   REG_EXP_DT = ").append(e.sqlDatetime());
		}
		sb.append("   , ");
		sb.append("   UPDATED_BY = ").append(user);
		sb.append("   , ");
		sb.append("   UPDATED_IP = '").append(ip).append("' ");
		if (Operator.hasValue(blocked)) {
			sb.append("   , ");
			sb.append("   BLOCKED = '").append(b).append("' ");
		}
		sb.append(" WHERE ");
		sb.append(" ID = ").append(vehicleid);
		return sb.toString();
	}

	public static String insertVehicle(RequestVO r, Token u) {
		return GeneralSQL.insertCommon(r, u);
	}

	public static String addVehicle(int projectid, int actid, String license, String state, String make, String model, String color, String year, int userid, String ip) {
		if (projectid < 1 && actid < 1) { return ""; }
		if (!Operator.hasValue(license)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO VEHICLE ( ");
		sb.append(" REF_PROJECT_PARKING_ID, ACTIVITY_ID, LICENSE_PLATE, REG_STATE, VEHICLE_MAKE, VEHICLE_MODEL, VEHICLE_COLOR, VEHICLE_YEAR, CREATED_BY, CREATED_IP, CREATED_DATE, UPDATED_BY, UPDATED_IP, UPDATED_DATE ");
		sb.append(" ) VALUES ( ");
		if (projectid < 1 && actid > 0) {
			sb.append(" ( ");
			sb.append(" SELECT TOP 1 P.ID FROM ");
			sb.append(" REF_PROJECT_PARKING AS P ");
			sb.append(" JOIN ACTIVITY AS A ON P.PROJECT_ID = A.PROJECT_ID AND A.ID = ").append(actid).append(" AND P.ACTIVE = 'Y' ");
			sb.append(" ) ");
		}
		else {
			sb.append(" ( SELECT TOP 1 ID FROM REF_PROJECT_PARKING_ID WHERE PROJECT_ID = ").append(projectid).append(" AND ACTIVE = 'Y') ");
		}
		sb.append(" , ");
		sb.append(actid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(license)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(state)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(make)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(model)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(color)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(year)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String addNote(String note, String notetype, int userid, String ip, Timekeeper date) {
		if (!Operator.hasValue(note)) { return ""; }
		if (!Operator.hasValue(notetype)) { notetype = "Note"; }
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO NOTES ( ");
		sb.append(" NOTE, LKUP_NOTES_TYPE_ID, CREATED_BY, CREATED_DATE, CREATED_IP, UPDATED_BY, UPDATED_DATE, UPDATED_IP ");
		sb.append(" ) VALUES ( ");
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

	public static String summary(String type, int typeid, int id) {
		if (type.equalsIgnoreCase("project")) {
			return projectSummary(typeid);
		}
		else if (type.equalsIgnoreCase("activity")) {
			return activitySummary(typeid);
		}
		return "";
	}

	public static String projectSummary(int projectid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" V.* ");
		sb.append(" FROM ");
		sb.append(" VEHICLE AS V ");
		sb.append(" JOIN REF_PROJECT_PARKING AS P ON V.REF_PROJECT_PARKING_ID = P.ID AND V.ACTIVE = 'Y' AND P.ACTIVE = 'Y' AND ACTIVITY_ID < 1 AND P.PROJECT_ID = ").append(projectid);
		return sb.toString();
	}

	public static String activitySummary(int actid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" V.* ");
		sb.append(" FROM ");
		sb.append(" VEHICLE AS V ");
		sb.append(" WHERE ");
		sb.append(" V.ACTIVE = 'Y' AND V.ACTIVITY_ID = ").append(actid);
		return sb.toString();
	}
















}















