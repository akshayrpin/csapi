package csapi.impl.holds;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.utils.CsReflect;
import csshared.vo.DataVO;
import csshared.vo.HoldsList;
import csshared.vo.HoldsVO;
import csshared.vo.RequestVO;

public class HoldsAgent {

	public static String getSystemGeneratedActivity_id(RequestVO r){
		return Operator.toString(r.getTypeid());
	}
	
	public static String getSystemGeneratedProject_id(RequestVO r){
		return Operator.toString(r.getTypeid());
	}
	
	public static String getSystemGeneratedLso_id(RequestVO r){
		return Operator.toString(r.getTypeid());
	}
	
	
	public static boolean save(RequestVO r, Token u){
		boolean result= false;
		DataVO d = DataVO.toDataVO(r);

		String type = r.getType();
		int typeid = r.getTypeid();
		int id = Operator.toInt(r.getId());

		int holdtype = Operator.toInt(d.get("LKUP_HOLDS_TYPE_ID"));
		int holdstatus = Operator.toInt(d.get("LKUP_HOLDS_STATUS_ID"));
		String description = d.get("DESCRIPTION");

		Logger.highlight("ID", id);
		Logger.highlight("TYPE", type);
		Logger.highlight("TYPEID", typeid);
		Logger.highlight("HOLDTYPE", holdtype);
		Logger.highlight("HOLDSTATUS", holdstatus);
		Logger.highlight("DESCRIPTION", description);

		Timekeeper now = new Timekeeper();

		String command = "";
		Sage db = new Sage();
		if (id > 0) {
			command = HoldsSQL.addHold(type, id, holdstatus, description, u.getId(), u.getIp(), now);
			if (db.update(command)) {
				command = HoldsSQL.getHold(type, typeid, holdstatus, u.getId(), now);
				if (db.query(command) && db.next()) {
					int newid = db.getInt("ID");
					command = HoldsSQL.updateHold(type, id, newid, u.getId(), u.getIp());
					result = db.update(command);
					CsReflect.addHistory(type, typeid, "holds", newid, "update");
				}
			}
		}
		else {
			 command = HoldsSQL.addHold(type, typeid, holdtype, holdstatus, description, u.getId(), u.getIp(), now);
			 if (db.query(command) && db.next()) {
				 int hid = db.getInt("ID");
				 if (hid > 0) { result = true; }
					CsReflect.addHistory(type, typeid, "holds", hid, "add");
			 }
		}
		db.clear();
		
		return result;
	}

	public static boolean release(int holdid, String type, int holdstatus, String description, int userid) {
		boolean result = false;
		Timekeeper now = new Timekeeper();
		String command = "";
		command = HoldsSQL.getHold(holdid, type);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				int typeid = db.getInt("REF_ID");
				String desc = db.getString("DESCRIPTION");
				String d = description + " - " + desc;
				command = HoldsSQL.addHold(type, holdid, holdstatus, d, userid, "", now);
				if (Operator.hasValue(command)) {
					if (db.update(command)) {
						command = HoldsSQL.getHold(type, typeid, holdstatus, userid, now);
						if (db.query(command) && db.next()) {
							int newid = db.getInt("ID");
							command = HoldsSQL.updateHold(type, holdid, newid, userid, "");
							result = db.update(command);
							CsReflect.addHistory(type, typeid, "holds", newid, "update");
						}
					}
				}
			}
			db.clear();
		}
		return result;
	}


	public static String getAlert(String type, int typeId){

// COMMENTED BY ALAIN 6/20/2019 TO IMPLEMENT NEW HOLDS LOGIC
//		String alert= "";
//		try {
//			String command = HoldsSQL.getAlertHolds(type, typeId);
//			if(Operator.hasValue(command)){
//				Sage db = new Sage();
//				db.query(command);
//				if(db.next()){
//					alert = db.getString("ALERT");
//				}
//				db.clear();	
//			}
//		}catch(Exception e){
//			Logger.error(e.getMessage());
//		}


		StringBuilder sb = new StringBuilder();
		if (type.equalsIgnoreCase("activity")) {
			HoldsList hl = getActivityHolds(type, typeId);
			String[] ah = hl.significantActivityHolds();
			if (ah.length > 0) {
				sb.append("HOLD_").append(ah[0]);
			}
		}
		else {
			HoldsList hl = getHolds(type, typeId);
			String p = hl.getPriorityhold();
			if (Operator.hasValue(p)) {
				sb.append("HOLD_").append(p);
			}
		}
		return sb.toString();
	}

	// GET HOLDS AND SET ACTIVITY TYPES
	public static HoldsList getActivityHolds(String type, int typeid) {
		HoldsList l = new HoldsList();
		l = getHolds(type, typeid);
		if (type.equalsIgnoreCase("activity")) {
			l = setActivityType(l, typeid);
		}
		else if (type.equalsIgnoreCase("project")) {
			l = setProjectActivityTypes(l, typeid);
		}
		else {
			l = setActivityTypes(l);
		}
		return l;
	}

	public static HoldsList getHolds(String type, int typeid, String[] acttypes) {
		HoldsList l = getHolds(type, typeid);
		return setActivityTypes(l, acttypes);
	}

	public static HoldsList getHolds(String type, int typeid, int acttypes) {
		HoldsList l = getHolds(type, typeid);
		if (acttypes > 0) {
			String[] a = new String[1];
			a[0] = Operator.toString(acttypes);
			l = setActivityTypes(l, a);
		}
		return l;
	}

	public static HoldsList getHolds(String type, int typeid) {
		HoldsList l = new HoldsList();
		String command = HoldsSQL.getAllHolds(type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				while (db.next()) {
					HoldsVO vo = new HoldsVO();
					vo.setType(db.getString("TYPE"));
					vo.setTypeid(db.getInt("TYPE_ID"));
					vo.setHoldid(db.getInt("HOLDS_ID"));
					vo.setTitle(db.getString("TITLE"));
					vo.setDescription(db.getString("DESCRIPTION"));
					vo.setHoldtypeid(db.getInt("HOLDS_TYPE_ID"));
					vo.setHoldtype(db.getString("HOLDS_TYPE"));
					vo.setTypedescription(db.getString("HOLDS_TYPE_DESCRIPTION"));
					vo.setStatusid(db.getInt("STATUS_ID"));
					vo.setStatus(db.getString("STATUS"));
					vo.setStatusdescription(db.getString("STATUS_DESCRIPTION"));
					vo.setLive(db.getString("LIVE"));
					vo.setReleased(db.getString("RELEASED"));
					vo.setSignificant(db.getString("SIGNIFICANT"));
					vo.setIspublic(db.getString("ISPUBLIC"));
					vo.setCreatedby(db.getInt("CREATED_BY"));
					vo.setCreateddate(db.getString("CREATED_DATE"));
					l.set(vo);
				}
			}
			db.clear();
		}
		return l;
	}

	public static HoldsList setActivityTypes(HoldsList l) {
		return setActivityTypes(l, new String[0]);
	}

	public static HoldsList setActivityTypes(HoldsList l, String[] acttypeids) {
		String command = HoldsSQL.getActivityTypes(acttypeids);
		Sage db = new Sage();
		if (db.query(command)) {
			while (db.next()) {
				int acttypeid = db.getInt("ACT_TYPE_ID");
				String acttype = db.getString("ACT_TYPE");
				int holdid = db.getInt("HOLDS_TYPE_ID");
				if (Operator.hasValue(acttype) && holdid > 0) {
					l.setActType(acttypeid, acttype, holdid);
				}
			}
		}
		db.clear();
		return l;
	}

	public static HoldsList setActivityType(HoldsList l, int actid) {
		String command = HoldsSQL.getActivity(actid);
		Sage db = new Sage();
		if (db.query(command)) {
			while (db.next()) {
				int acttypeid = db.getInt("ACT_TYPE_ID");
				String acttype = db.getString("ACT_TYPE");
				int holdid = db.getInt("HOLDS_TYPE_ID");
				if (Operator.hasValue(acttype) && holdid > 0) {
					l.setActType(acttypeid, acttype, holdid);
				}
			}
		}
		db.clear();
		return l;
	}

	public static HoldsList setProjectActivityTypes(HoldsList l, int projectid) {
		String command = HoldsSQL.getProject(projectid);
		Sage db = new Sage();
		if (db.query(command)) {
			while (db.next()) {
				int acttypeid = db.getInt("ACT_TYPE_ID");
				String acttype = db.getString("ACT_TYPE");
				int holdid = db.getInt("HOLDS_TYPE_ID");
				if (Operator.hasValue(acttype) && holdid > 0) {
					l.setActType(acttypeid, acttype, holdid);
				}
			}
		}
		db.clear();
		return l;
	}


}



















