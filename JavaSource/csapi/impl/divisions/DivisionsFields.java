package csapi.impl.divisions;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class DivisionsFields {

	public static final String TABLE_TYPE = "horizontal";

	public static ObjGroupVO details(String type, int typeid) {
		return DivisionsAgent.details(type, typeid);
	}

	public static ObjGroupVO list(String type, int typeid) {
		return DivisionsAgent.details(type, typeid);
	}

	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("divisions");
		r.setType("divisions");
		r.setGroupid("divisions");
		r.setFields(new String[] {"CREATED","TITLE"});
		r.setIndex(new String[] {"CREATED","TITLE"});
		ObjVO[] o = new ObjVO[2];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("CREATED_DATE");
		vo.setType("date");
		vo.setItype("date");
		vo.setField("CREATED_DATE");
		vo.setLabel("CREATED");
		vo.setLinkfield("PATH");
		vo.setRequired("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TITLE");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("TITLE");
		vo.setLinkfield("PATH");
		vo.setRequired("Y");
		o[1] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO info() {
		return summary();
	}
	
}
