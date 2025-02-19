package csapi.impl.peoplesummary;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class PeoplesummaryFields {

	// title for detail group
	public static final String TYPE_DETAIL_TITLE = "PEOPLE";

	// name of main type table (this is a requirement for tables such as activity where the main table is "activity" but connected tables use "act"
	public static final String MAIN_TABLE_REF = "REF_USERS";

	// reference to other tables
	public static final String TABLE_REF = "USERS";

	// id field used to join other tables
	public static final String FIELD_ID_REF = "REF_USERS_ID";

	public static final String TABLE_TYPE = "horizontal";

	public static ObjGroupVO info() {
		return summary();
	}
	
	public static ObjGroupVO list() {
		return summary();
	}

	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("peoplesummary");
		r.setType("peoplesummary");
		r.setGroupid("peoplesummary");
		r.setLabel("PEOPLE SUMMARY");
		r.setEditable(false);
		r.setFields(new String[] {"ACTIVITY","NAME","EMAIL","PHONE","ADDRESS","LICENSE","TYPE"});
		ObjVO[] o = new ObjVO[9];


		ObjVO s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("TYPE");
		s.setType("type");
		s.setItype("text");
		s.setField("TYPE");
		s.setLabel("TYPE");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[0] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("NAME");
		s.setType("String");
		s.setItype("text");
		s.setField("NAME");
		s.setLabel("NAME");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[1] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("EMAIL");
		s.setType("email");
		s.setItype("email");
		s.setField("EMAIL");
		s.setLabel("EMAIL");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[2] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PHONE_WORK");
		s.setType("String");
		s.setItype("phone");
		s.setField("PHONE_WORK");
		s.setLabel("PHONE");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[3] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ADDRESS");
		s.setType("String");
		s.setItype("text");
		s.setField("ADDRESS");
		s.setLabel("ADDRESS");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[4] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LIC_NUM");
		s.setType("short");
		s.setItype("text");
		s.setField("LIC_NUM");
		s.setLabel("LICENSE");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[5] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ACT_NBR");
		s.setType("short");
		s.setItype("text");
		s.setField("ACT_NBR");
		s.setLabel("ACTIVITY");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[6] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PEOPLE");
		s.setType("String");
		s.setItype("hidden");
		s.setField("PEOPLE");
		s.setLabel("PEOPLE");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[7] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ACTIVITIES");
		s.setType("String");
		s.setItype("text");
		s.setField("ACTIVITIES");
		s.setLabel("ACTIVITIES");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[8] = s;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("peoplesummary");
		r.setType("peoplesummary");
		r.setGroupid("peoplesummary");
		r.setLabel("PEOPLE SUMMARY");
		r.setTitlefield("NAME");
		r.setSubtitlefield("EMAIL");
		r.setEditable(false);
		ObjVO[] o = new ObjVO[6];


		ObjVO s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("TYPE");
		s.setType("type");
		s.setItype("text");
		s.setField("TYPE");
		s.setLabel("TYPE");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[0] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("NAME");
		s.setType("String");
		s.setItype("text");
		s.setField("NAME");
		s.setLabel("NAME");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[1] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("EMAIL");
		s.setType("email");
		s.setItype("email");
		s.setField("EMAIL");
		s.setLabel("EMAIL");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[2] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PHONE_WORK");
		s.setType("String");
		s.setItype("phone");
		s.setField("PHONE_WORK");
		s.setLabel("PHONE");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[3] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ADDRESS");
		s.setType("String");
		s.setItype("text");
		s.setField("ADDRESS");
		s.setLabel("ADDRESS");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[4] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LIC_NUM");
		s.setType("short");
		s.setItype("text");
		s.setField("LIC_NUM");
		s.setLabel("LICENSE");
		s.setRequired("Y");
		s.setTarget("lightbox-iframe");
		o[5] = s;

		r.setObj(o);
		return r;
	}


}
