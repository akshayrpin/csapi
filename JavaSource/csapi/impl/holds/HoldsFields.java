package csapi.impl.holds;

import csapi.common.Choices;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class HoldsFields {

	public static final String TABLE_TYPE = "horizontal";
	public static final String FIELD_ID_REF = "ID";
	
	public static ObjGroupVO info() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("holds");
		r.setType("holds");
		r.setGroupid("holds");
		r.setDeletable(false);
		r.setHistory(true);
		r.setFields(new String[] {"TYPE", "DESCRIPTION", "STATUS"});
		r.setIndex(new String[] {"TYPE", "DESCRIPTION", "STATUS"});
		ObjVO[] o = new ObjVO[4];


		ObjVO s = new ObjVO();
		s.setId(-1);
		s.setOrder(20);
		s.setFieldid("LKUP_HOLDS_TYPE_TEXT");
		s.setType("type");
		s.setItype("text");
		s.setField("LKUP_HOLDS_TYPE_TEXT");
		s.setLabel("TYPE");
	
		s.setTextfield("LKUP_HOLDS_TYPE_TEXT");
		
		o[0] = s;
		
		s = new ObjVO();
		s.setId(-1);
		s.setOrder(20);
		s.setFieldid("LKUP_HOLDS_STATUS_TEXT");
		s.setType("status");
		s.setItype("String");
		s.setField("LKUP_HOLDS_STATUS_TEXT");
		s.setLabel("STATUS");
		s.setTextfield("LKUP_HOLDS_STATUS_TEXT");
	
		o[1] = s;

	
		s = new ObjVO();
		s.setId(-1);
		s.setOrder(20);
		s.setFieldid("DESCRIPTION");
		s.setType("String");
		s.setItype("String");
		s.setField("DESCRIPTION");
		s.setLabel("DESCRIPTION");
		s.setTextfield("DESCRIPTION");
	
		o[2] = s;


		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("CREATED_DATE");
		s.setType("date");
		s.setItype("date");
		s.setField("CREATED_DATE");
		s.setLabel("DATE");
		s.setRequired("N");
		s.setEditable("N");
		s.setAddable("N");
		o[3] = s;
		
	
		r.setObj(o);
		return r;
	}
	
	public static ObjGroupVO id() {
		return details();
	}
	
	public static ObjGroupVO summary() {
		return details();
	}
	
	public static ObjGroupVO list() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("holds");
		r.setType("holds");
		r.setGroupid("holds");
		r.setDeletable(false);
		r.setHistory(true);
		r.setFields(new String[] {"TYPE","DESCRIPTION","STATUS","DATE","CREATED"});
		r.setIndex(new String[] {"TYPE","STATUS","DESCRIPTION","CREATED","DATE"});
		ObjVO[] o = new ObjVO[5];


		ObjVO s = new ObjVO();
		s.setId(-1);
		s.setOrder(20);
		s.setFieldid("LKUP_HOLDS_TYPE_ID");
		s.setType("type");
		s.setItype("String");
		s.setField("LKUP_HOLDS_TYPE_ID");
		s.setLabel("TYPE");
		s.setRequired("Y");
		s.setEditable("N");
		s.setAddable("Y");
		s.setTextfield("LKUP_HOLDS_TYPE_TEXT");
		
		s.setJson(Choices.choiceUrl("LKUP_HOLDS_TYPE", "DESCRIPTION", "DESCRIPTION"));
		o[0] = s;
		
		s = new ObjVO();
		s.setId(-1);
		s.setOrder(20);
		s.setFieldid("LKUP_HOLDS_STATUS_ID");
		s.setType("status");
		s.setItype("String");
		s.setField("LKUP_HOLDS_STATUS_ID");
		s.setLabel("STATUS");
		s.setRequired("Y");
		s.setEditable("Y");
		s.setAddable("Y");
		s.setTextfield("LKUP_HOLDS_STATUS_TEXT");
		s.setJson(Choices.choiceUrl("LKUP_HOLDS_STATUS", "DESCRIPTION", "DESCRIPTION"));
		o[1] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("DESCRIPTION");
		s.setType("String");
		s.setItype("largetextarea");
		s.setField("DESCRIPTION");
		s.setLabel("DESCRIPTION");
		s.setRequired("Y");
		o[2] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("CREATED");
		s.setType("user");
		s.setItype("user");
		s.setField("CREATED");
		s.setLabel("CREATED");
		s.setRequired("N");
		s.setEditable("N");
		s.setAddable("N");
		o[3] = s;
		
		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("CREATED_DATE");
		s.setType("date");
		s.setItype("date");
		s.setField("CREATED_DATE");
		s.setLabel("DATE");
		s.setRequired("N");
		s.setEditable("N");
		s.setAddable("N");
		o[4] = s;
		
		r.setObj(o);
		return r;
	}
	
	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("holds");
		r.setType("holds");
		r.setGroupid("holds");
		r.setDeletable(false);
		r.setHistory(true);
		r.setFields(new String[] {"TYPE","DESCRIPTION","STATUS","DATE","CREATED"});
		r.setIndex(new String[] {"TYPE","STATUS","DATE"});
		ObjVO[] o = new ObjVO[4];


		ObjVO s = new ObjVO();
		s.setId(-1);
		s.setOrder(20);
		s.setFieldid("LKUP_HOLDS_TYPE_ID");
		s.setType("String");
		s.setItype("String");
		s.setField("LKUP_HOLDS_TYPE_ID");
		s.setLabel("TYPE");
		s.setRequired("Y");
		s.setEditable("N");
		s.setAddable("Y");
		s.setTextfield("LKUP_HOLDS_TYPE_TEXT");
		s.setLkup("typedescriptions");
		
//		s.setJson(Choices.choiceUrl("LKUP_HOLDS_TYPE", "DESCRIPTION", "DESCRIPTION"));
		o[0] = s;
		
		s = new ObjVO();
		s.setId(-1);
		s.setOrder(20);
		s.setFieldid("LKUP_HOLDS_STATUS_ID");
		s.setType("String");
		s.setItype("String");
		s.setField("LKUP_HOLDS_STATUS_ID");
		s.setLabel("STATUS");
		s.setRequired("Y");
		s.setEditable("Y");
		s.setAddable("Y");
		s.setEmptyonedit("Y");
		s.setTextfield("LKUP_HOLDS_STATUS_TEXT");
		s.setLkup("statusdescriptions");
//		s.setJson(Choices.choiceUrl("LKUP_HOLDS_STATUS", "DESCRIPTION", "DESCRIPTION"));
		o[1] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("DESCRIPTION");
		s.setType("String");
		s.setItype("largetextarea");
		s.setField("DESCRIPTION");
		s.setLabel("DESCRIPTION");
		s.setRequired("Y");
		s.setEmptyonedit("Y");
		o[2] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("CREATED_DATE");
		s.setType("date");
		s.setItype("hidden");
		s.setField("CREATED_DATE");
		s.setLabel("DATE");
		s.setRequired("N");
		s.setEditable("N");
		s.setAddable("N");
		o[3] = s;
		
		
		
		
	
		r.setObj(o);

		return r;
	}

}
