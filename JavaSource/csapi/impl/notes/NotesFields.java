package csapi.impl.notes;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class NotesFields {

	public static final String TABLE_TYPE = "horizontal";

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("notes");
		r.setType("notes");
		r.setGroupid("notes");
		r.setFields(new String[] {"NOTE","TYPE"});
		r.setIndex(new String[] {"NOTE","TYPE"});
		ObjVO[] o = new ObjVO[5];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("NOTE");
		vo.setType("text");
		vo.setItype("textarea");
		vo.setField("NOTE");
		vo.setLabel("NOTE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("LKUP_NOTES_TYPE_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("LKUP_NOTES_TYPE_ID");
		vo.setTextfield("TYPE");
		vo.setLabel("TYPE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setLkup("type");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("SUBJECT");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("SUBJECT");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("RECIPIENT");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("RECIPIENT");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("DATA");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("DATA");
		o[4] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("notes");
		r.setType("notes");
		r.setGroupid("notes");
		r.setEditable(false);
//		r.setAddable(true);
		r.setDisplay("hz");
		r.setFields(new String[] {"DATE","TYPE", "NOTE", "UPDATED"});
		r.setIndex(new String[] {"NOTE","DATE"});
		ObjVO[] o = new ObjVO[6];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("NOTE");
		vo.setType("text");
		vo.setItype("textarea");
		vo.setField("NOTE");
		vo.setLabel("NOTE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TYPE");
		vo.setType("type");
		vo.setItype("text");
		vo.setField("TYPE");
		vo.setRequired("Y");
		
		vo.setEditable("N");
		vo.setAddable("N");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("CREATED_DATE");
		vo.setType("datetime");
		vo.setItype("datetime");
		vo.setField("CREATED_DATE");
		vo.setLabel("CREATED DATE");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("N");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("CREATED_BY");
		vo.setType("String");
		vo.setItype("user");
		vo.setField("CREATED_BY");
		vo.setTextfield("CREATED");
		vo.setLabel("CREATED");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("N");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("UPDATED_DATE");
		vo.setType("datetime");
		vo.setItype("datetime");
		vo.setField("UPDATED_DATE");
		vo.setLabel("DATE");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("N");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("UPDATED_BY");
		vo.setType("String");
		vo.setItype("user");
		vo.setField("UPDATED_BY");
		vo.setTextfield("UPDATED");
		vo.setLabel("UPDATED");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("N");
		o[5] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO ext() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("notes");
		r.setType("notes");
		r.setGroupid("notes");
		r.setFields(new String[] {"DATE","TYPE", "NOTE", "UPDATED"});
		r.setIndex(new String[] {"NOTE","DATE"});
		r.setDeletable(false);
		r.setEditable(false);
		ObjVO[] o = new ObjVO[6];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("NOTE");
		vo.setType("text");
		vo.setItype("textarea");
		vo.setField("NOTE");
		vo.setLabel("NOTE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TYPE");
		vo.setType("type");
		vo.setItype("text");
		vo.setField("TYPE");
		vo.setRequired("Y");
		
		vo.setEditable("N");
		vo.setAddable("N");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("CREATED_DATE");
		vo.setType("datetime");
		vo.setItype("datetime");
		vo.setField("CREATED_DATE");
		vo.setLabel("CREATED DATE");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("N");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("CREATED_BY");
		vo.setType("String");
		vo.setItype("user");
		vo.setField("CREATED_BY");
		vo.setTextfield("CREATED");
		vo.setLabel("CREATED");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("N");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("UPDATED_DATE");
		vo.setType("datetime");
		vo.setItype("datetime");
		vo.setField("UPDATED_DATE");
		vo.setLabel("DATE");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("N");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("UPDATED_BY");
		vo.setType("String");
		vo.setItype("user");
		vo.setField("UPDATED_BY");
		vo.setTextfield("UPDATED");
		vo.setLabel("UPDATED");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("N");
		o[5] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO info() {
		return summary();
	}
	
	public static ObjGroupVO list() {
		return summary();
	}
	
	public static ObjGroupVO id() {
		return summary();
	}
	

}




