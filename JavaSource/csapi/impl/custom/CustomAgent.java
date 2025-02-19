package csapi.impl.custom;

import alain.core.db.Sage;
import alain.core.utils.Operator;
import csapi.common.Choices;
import csapi.impl.team.TeamAgent;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.SubObjVO;


public class CustomAgent {

	public static ObjGroupVO details(String type, int typeid, int groupId, int setId) {
		ObjGroupVO result = new ObjGroupVO();
		String grp = "";
		String grpid = "";
		String grppublic = "";
		String multi = getGroupMulti(groupId);
		
		String command ="";
		if(multi.equalsIgnoreCase("Y")){
			command = CustomSQL.details(type, typeid, groupId, setId);
		}else {
			command = CustomSQL.details(type, typeid, groupId);
		}
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.size() > 0) {
				ObjVO[] os = new ObjVO[db.size()];
				int count = 0;
				while (db.next()) {
					if (db.hasValue("GROUP_NAME")) { grp = db.getString("GROUP_NAME"); }
					if (db.hasValue("GROUP_ID")) { grpid = db.getString("GROUP_ID"); }
					if (db.hasValue("GROUP_PUBLIC")) { grppublic = db.getString("GROUP_PUBLIC"); }
					ObjVO vo = new ObjVO();
					int fieldid = db.getInt("ID");
					String value = db.getString("VALUE");
					vo.setId(db.getInt("ID"));
					vo.setField(db.getString("NAME"));
					vo.setFieldid(db.getString("ID"));
					vo.setValue(value);
					/*vo.setType("text");
					vo.setItype("String");*/
					vo.setType(db.getString("FIELD_TYPE"));
					vo.setItype(db.getString("FIELD_ITYPE"));
					if (db.getInt("CHOICES") > 0) {
						command = CustomSQL.getChoices(fieldid);
						SubObjVO[] c = Choices.getChoices(command);
						vo.setChoices(c);
					}
					else if (db.equalsIgnoreCase("FIELD_ITYPE", "team") || db.equalsIgnoreCase("FIELD_ITYPE", "teammember")) {
						vo.setChoices(TeamAgent.getTeam(value));
					}
					os[count] = vo;
					count++;
				}
				result.setObj(os);
			}
			db.clear();
		}
		result.setType("custom");
		if (Operator.hasValue(grp)) { result.setGroup(grp); }
		if (Operator.hasValue(grppublic)) { result.setPub(grppublic); }
		//if (grpid > 0) { result.setId(grpid); }
		if (Operator.hasValue(grpid)) { result.setGroupid(grpid); }
		return result;
	}
	
	public static String getGroupMulti(int groupId){
		String multi = "N";
		String command = CustomSQL.getGroup(groupId);
		if(Operator.hasValue(command)){
			Sage db = new Sage();
			db.query(command);
			if(db.next()){
				multi = db.getString("MULTI");
			}
			db.clear();
		}
		return multi;
	}
	
	public static int getSetId(String type, int typeid, int groupId){
		int setId = 1;
		String command = CustomSQL.getSetId(type, typeid, groupId);
		if(Operator.hasValue(command)){
			Sage db = new Sage();
			db.query(command);
			if(db.next()){
				if(Operator.hasValue(db.getString("ID"))){
					setId = db.getInt("ID");
				}
				
			}
			db.clear();
		}
		return setId;
	}

	public static boolean delete(String type, int typeid, int fieldgroupid, int setid, int updatedby, String ip) {
		boolean r = false;
		String command = CustomSQL.delete(type, typeid, fieldgroupid, setid, updatedby, ip);
		Sage db = new Sage();
		r = db.update(command);
		db.clear();
		return r;
	}

}















