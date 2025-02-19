package csapi.utils.objtools;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.common.Choices;
import csapi.common.FieldObjects;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class Details {

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
		return result;
	}

	/*public static ObjGroupVO holds(String type, int typeid, int id) {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("HOLDS");
		r.setGroupid(Operator.toString(id));
		ObjVO[] o = new ObjVO[2];
		o[0] =FieldObjects.toObject(1, 1, "HOLD", "String", "String", "HOLD", "", "", "", "", "", "", "", "Y");
		o[1] =FieldObjects.toObject(2, 1, "DESCRIPTION", "String", "String", "DESCRIPTION", "", "", "", "", "", "", "", "Y");
		r.setObj(o);
		return r;
	}

	public static ObjGroupVO notes(String type, int typeid, int id) {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("NOTES");
		r.setGroupid(Operator.toString(id));
		r.setType("NOTES");
		Sage db = new Sage();
		ObjVO[] o = new ObjVO[2];
		String command = ObjSQL.getNotes(type, typeid, id);
		db.query(command);
		if(db.next()){
			o[0] = FieldObjects.toObject(1, 3, "LKUP_NOTES_TYPE", "String", "text", "TYPE", db.getString("LKUP_NOTES_TYPE"), "", "", "", "", "", "", "Y",Choices.getNoteTypes("LKUP_NOTES_TYPE", "DESCRIPTION", ""));	
			o[1] =FieldObjects.toObject(2, 1, "NOTE", "String", "TEXTAREA", "NOTE",db.getString("NOTE"), "", "", "", "", "", "", "Y");
		}
		db.clear();
		r.setObj(o);
		return r;
	}
	
	public static ObjGroupVO attachments(String type, int typeid, int id) {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("ATTACHMENTS");
		r.setGroupid(Operator.toString(id));
		r.setType("ATTACHMENTS");
		Sage db = new Sage();
		ObjVO[] o = new ObjVO[12];
		String command = ObjSQL.getAttachment(type, typeid, id);
		db.query(command);
		if(db.next()){
			o[0] =FieldObjects.toObject(2, 1, "TITLE", "String", "String", "TITLE",db.getString("TITLE"), "", "", "", "", "", "", "Y");
			o[1] =FieldObjects.toObject(2, 1, "DESCRIPTION", "String", "Textarea", "DESCRIPTION",db.getString("DESCRIPTION"), "", "", "", "", "", "", "Y");
			o[2] =FieldObjects.toObject(2, 1, "PATH", "String", "File", "PATH",db.getString("PATH"), "", "", "", "", "", "", "N");
			o[3] =FieldObjects.toObject(2, 1, "ISPUBLIC", "Boolean", "Boolean", "ISPUBLIC",db.getString("ISPUBLIC"), "", "", "", "", "", "", "N");
			o[4] =FieldObjects.toObject(2, 1, "KEYWORD1", "String", "String", "KEYWORD1",db.getString("KEYWORD1"), "", "", "", "", "", "", "N");
			o[5] =FieldObjects.toObject(2, 1, "KEYWORD2", "String", "String", "KEYWORD1",db.getString("KEYWORD2"), "", "", "", "", "", "", "N");
			o[6] =FieldObjects.toObject(2, 1, "KEYWORD3", "String", "String", "KEYWORD1",db.getString("KEYWORD3"), "", "", "", "", "", "", "N");
			o[7] =FieldObjects.toObject(2, 1, "KEYWORD4", "String", "String", "KEYWORD1",db.getString("KEYWORD4"), "", "", "", "", "", "", "N");
			o[8] = FieldObjects.toObject(3, 3, "CREATED", "String", "uneditable", "CREATED", db.getString("CREATED"), "", "", "", "", "", "", "");
			o[9] = FieldObjects.toObject(3, 3, "CREATED_DATE", "DATETIME", "uneditable", "CREATED DATE", db.getString("CREATED_DATE"), "", "", "", "", "", "", "");
			
			o[10] = FieldObjects.toObject(3, 3, "UPDATED", "String", "uneditable", "UPDATED", db.getString("UPDATED"), "", "", "", "", "", "", "");
			o[11] = FieldObjects.toObject(3, 3, "UPDATED_DATE", "DATETIME", "uneditable", "UPDATED DATE", db.getString("UPDATED_DATE"), "", "", "", "", "", "", "");
		}
		db.clear();
		r.setObj(o);
		return r;
	}*/
	
	
	/*public static ObjGroupVO conditions(String type, int typeid, int id) {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("CONDITIONS");
		r.setGroupid(Operator.toString(id));
		r.setType("CONDITIONS");
		Sage db = new Sage();
		ObjVO[] o = new ObjVO[12];
		String command = ObjSQL.getAttachment(type, typeid, id);
		db.query(command);
		if(db.next()){
			o[0] =FieldObjects.toObject(2, 1, "TITLE", "String", "String", "TITLE",db.getString("TITLE"), "", "", "Y");
			o[1] =FieldObjects.toObject(2, 1, "DESCRIPTION", "String", "Textarea", "DESCRIPTION",db.getString("DESCRIPTION"), "", "", "Y");
			o[2] =FieldObjects.toObject(2, 1, "PATH", "String", "File", "PATH",db.getString("PATH"), "", "", "N");
			o[3] =FieldObjects.toObject(2, 1, "ISPUBLIC", "Boolean", "Boolean", "ISPUBLIC",db.getString("ISPUBLIC"), "", "", "N");
			o[4] =FieldObjects.toObject(2, 1, "KEYWORD1", "String", "String", "KEYWORD1",db.getString("KEYWORD1"), "", "", "N");
			o[5] =FieldObjects.toObject(2, 1, "KEYWORD2", "String", "String", "KEYWORD1",db.getString("KEYWORD2"), "", "", "N");
			o[6] =FieldObjects.toObject(2, 1, "KEYWORD3", "String", "String", "KEYWORD1",db.getString("KEYWORD3"), "", "", "N");
			o[7] =FieldObjects.toObject(2, 1, "KEYWORD4", "String", "String", "KEYWORD1",db.getString("KEYWORD4"), "", "", "N");
			o[8] = FieldObjects.toObject(3, 3, "CREATED", "String", "uneditable", "CREATED", db.getString("CREATED"), "", "", "");
			o[9] = FieldObjects.toObject(3, 3, "CREATED_DATE", "DATETIME", "uneditable", "CREATED DATE", db.getString("CREATED_DATE"), "", "", "");
			
			o[10] = FieldObjects.toObject(3, 3, "UPDATED", "String", "uneditable", "UPDATED", db.getString("UPDATED"), "", "", "");
			o[11] = FieldObjects.toObject(3, 3, "UPDATED_DATE", "DATETIME", "uneditable", "UPDATED DATE", db.getString("UPDATED_DATE"), "", "", "");
		}
		db.clear();
		r.setObj(o);
		return r;
	}*/
	
}
