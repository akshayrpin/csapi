package csapi.impl.custom;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.common.Choices;
import csapi.utils.objtools.ObjSQL;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.SubObjVO;

public class CustomFields {

	public static final String TABLE_TYPE = "vertical";

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
					int fieldid = db.getInt("ID");
					ObjVO vo = new ObjVO();
					vo.setId(db.getInt("ID"));
					vo.setField(db.getString("NAME"));
					vo.setFieldid(db.getString("ID"));
					vo.setType(db.getString("FIELD_TYPE"));
					vo.setItype(db.getString("FIELD_ITYPE"));
					if (db.getInt("CHOICES") > 0) {
						command = CustomSQL.getChoices(fieldid);
						SubObjVO[] c = Choices.getChoices(command);
						vo.setChoices(c);
					}
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


}
