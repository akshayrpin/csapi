package csapi.utils.objtools;

import java.util.ArrayList;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.common.Choices;
import csapi.common.FieldObjects;
import csapi.utils.CsTools;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class Fields {

	public static ObjGroupVO custom(int groupid) {
		ObjGroupVO result = new ObjGroupVO();
		String grp = "";
		String grpid = "";
		String grppublic = "";
		String command = ObjSQL.getCustomFields(groupid);
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
					vo.setId(db.getInt("ID"));
					vo.setField(db.getString("NAME"));
					vo.setFieldid(db.getString("ID"));
					vo.setType(db.getString("FIELD_TYPE"));
					vo.setItype(db.getString("FIELD_ITYPE"));
					
					os[count] = vo;
					count++;
				}
				result.setObj(os);
			}
			db.clear();
		}
		if (Operator.hasValue(grp)) { result.setGroup(grp); }
		if (Operator.hasValue(grppublic)) { result.setPub(grppublic); }
		if (Operator.hasValue(grpid)) { result.setGroupid(grpid); }
		result.setType("custom");
		return result;
	}

	public static ObjGroupVO[] custom(String type, int typeid, ObjGroupVO details) {
		int size = 1;
		ObjGroupVO[] result = new ObjGroupVO[0];
		String grpname = "";
		int count = 0;
		String command = ObjSQL.getCustomFields(type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				size = size + db.getInt("GROUPS");
			}

			result = new ObjGroupVO[size];

			result[count] = details;
			count++;

			ObjGroupVO g = new ObjGroupVO();
			ArrayList<ObjVO> obj = new ArrayList<ObjVO>();

			while (db.next()) {
				String dbgrpname = db.getString("GROUP_NAME");
				if (!Operator.equalsIgnoreCase(grpname, dbgrpname)) {
					if (Operator.hasValue(grpname)) {
						ObjVO[] objs = CsTools.convert(obj);
						g.setObj(objs);
						result[count] = g;
						count++;
						g = new ObjGroupVO();
						obj = new ArrayList<ObjVO>();
					}
					g.setGroup(dbgrpname);
					g.setGroupid(db.getString("GROUP_ID"));
					g.setPub(db.getString("GROUP_PUBLIC"));
					g.setType("custom");
					grpname = dbgrpname;
				}
				ObjVO vo = new ObjVO();
				vo.setFieldid(db.getString("ID"));
				vo.setField(db.getString("NAME"));
				vo.setType(db.getString("TYPE", "text"));
				vo.setItype(db.getString("ITYPE", "text"));
				obj.add(vo);
			}
			if (Operator.hasValue(grpname)) {
				ObjVO[] objs = CsTools.convert(obj);
				g.setObj(objs);
				result[count] = g;
				count++;
			}

			db.clear();
		}
		return result;
	}

	/*public static ObjGroupVO holds(String type, int typeid) {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("HOLDS");
		ObjVO[] o = new ObjVO[2];
		o[0] =FieldObjects.toObject(1, 1, "HOLD", "String", "String", "HOLD", "", "", "", "", "", "", "", "Y");
		o[1] =FieldObjects.toObject(2, 1, "DESCRIPTION", "String", "String", "DESCRIPTION", "", "", "", "", "", "", "", "Y");
		r.setObj(o);
		return r;
	}

	public static ObjGroupVO notes(String type, int typeid) {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("NOTES");
		r.setType("NOTES");
		r.setGroupid("0");
		ObjVO[] o = new ObjVO[2];
		//o[0] =FieldObjects.toObject(1, 1, "LKUP_NOTES_TYPE", "String", "", "PUBLIC", "NO", "", "", "N");
		o[0] = FieldObjects.toObject(1, 3, "LKUP_NOTES_TYPE", "String", "text", "TYPE", "", "", "", "", "", "", "", "Y",Choices.getNoteTypes("LKUP_NOTES_TYPE", "DESCRIPTION", ""));	
		o[1] =FieldObjects.toObject(2, 1, "NOTE", "String", "TEXTAREA", "NOTE", "", "", "", "", "", "", "", "Y");
		r.setObj(o);
		return r;
	}*/
	
	
	public static ObjVO getField(ObjGroupVO r,String field){
		ObjVO op = new ObjVO();
		ObjVO[] o = r.getObj();
		for(int i=0;i<o.length;i++){
			if(o[i].getField().equalsIgnoreCase(field)){
				op = o[i];
				return op;
				
			}
		}
		return op;
	}
	
}
