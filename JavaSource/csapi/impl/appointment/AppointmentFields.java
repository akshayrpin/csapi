package csapi.impl.appointment;

import alain.core.utils.FileUtil;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class AppointmentFields {

	public static final int CACHE_INTERVAL = FileUtil.INTERVAL_ONCE_PER_DAY;

	// title for detail group
	public static final String TYPE_DETAIL_TITLE = "APPOINTMENT";

	// name of main type table (this is a requirement for tables such as activity where the main table is "activity" but connected tables use "act"
	public static final String MAIN_TABLE_REF = "APPOINTMENT";

	// reference to other tables
	public static final String TABLE_REF = "APPOINTMENT";

	// id field used to join other tables
	public static final String FIELD_ID_REF = "APPOINTMENT_ID";

	public static final String TABLE_TYPE = "horizontal";

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("appointment");
		r.setType("appointment");
		r.setGroupid("appointment");
		ObjVO[] o = new ObjVO[10];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("LKUP_APPOINTMENT_TYPE_ID");
		vo.setType("String");
		vo.setItype("appointment");
		vo.setField("LKUP_APPOINTMENT_TYPE_ID");
		vo.setTextfield("LKUP_APPOINTMENT_TYPE");
		vo.setLabel("APPOINTMENT TYPE");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("REVIEW_ID");
		vo.setType("String");
		vo.setItype("apptreview");
		vo.setField("REVIEW_ID");
		vo.setTextfield("REVIEW");
		vo.setLabel("APPOINTMENT SUB TYPE");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("Y");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("DATE");
		vo.setType("datetime");
		vo.setItype("availability_start_date");
		vo.setField("DATE");
		vo.setTextfield("DATE_START");
		vo.setLabel("START DATE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("TIME");
		vo.setType("datetime");
		vo.setItype("availability_start_time");
		vo.setField("TIME");
		vo.setTextfield("TIME_TEXT");
		vo.setLabel("START TIME");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("SUBJECT");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("SUBJECT");
		vo.setLabel("SUBJECT");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("NOTES");
		vo.setType("String");
		vo.setItype("textarea");
		vo.setLabel("NOTES");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("COLLABORATORS");
		vo.setType("people");
		vo.setItype("people");
		vo.setField("COLLABORATORS");
		vo.setLabel("COLLABORATORS");
		o[6] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TEAM");
		vo.setType("team");
		vo.setItype("team");
		vo.setField("TEAM");
		vo.setLabel("TEAM");
		o[7] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("STATUS_ID");
		vo.setType("status");
		vo.setItype("status");
		vo.setField("STATUS_ID");
		vo.setLabel("STATUS");
		vo.setTextfield("STATUS");
		o[8] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("NOTIFY");
		vo.setType("text");
		vo.setItype("hidden");
		vo.setField("NOTIFY");
		o[9] = vo;
		
		r.setObj(o);
		return r;
	}

	public static ObjGroupVO list() {
		return summary();
	}

	public static ObjGroupVO my() {
		return summary();
	}

	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("appointment");
		r.setType("appointment");
		r.setGroupid("appointment");
		r.setFields(new String[] {"TYPE","SUBJECT","STATUS","START_DATE","END_DATE"});
		r.setIndex(new String[] {"TYPE","STATUS","START_DATE"});
		r.setOptions(new String[] {"Active","Inactive","All"});
		ObjVO[] o = new ObjVO[5];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TYPE");
		vo.setType("type");
		vo.setItype("text");
		vo.setTextfield("TYPE_TEXT");
		vo.setField("TYPE");
		vo.setLabel("TYPE");
		vo.setRequired("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("SUBJECT");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("SUBJECT");
		vo.setLabel("SUBJECT");
		vo.setTextfield("SUBJECT_TEXT");
		vo.setRequired("Y");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("START_DATE");
		vo.setType("datetime");
		vo.setItype("datetime");
		vo.setField("START_DATE");
		vo.setRequired("Y");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("END_DATE");
		vo.setType("datetime");
		vo.setItype("datetime");
		vo.setField("END_DATE");
		vo.setRequired("Y");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("STATUS");
		vo.setType("status");
		vo.setItype("status");
		vo.setField("STATUS");
		vo.setRequired("Y");
		o[4] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO info() {
		return summary();
	}
	
	public static ObjGroupVO id() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("appointment");
		r.setType("appointment");
		r.setGroupid("appointment");
		ObjVO[] o = new ObjVO[7];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("TYPE");
		vo.setType("String");
		vo.setItype("appointment");
		vo.setField("TYPE");
		vo.setTextfield("TYPE");
		vo.setLabel("TYPE");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("SUBJECT");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("SUBJECT");
		vo.setTextfield("SUBJECT");
		vo.setLabel("SUBJECT");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("START_DATE");
		vo.setType("datetime");
		vo.setItype("datetime");
		vo.setField("START_DATE");
		vo.setTextfield("START_DATE");
		vo.setLabel("START DATE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("STATUS");
		vo.setType("status");
		vo.setItype("status");
		vo.setField("STATUS");
		vo.setTextfield("STATUS");
		vo.setLabel("STATUS");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("COLLABORATORS");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("COLLABORATORS");
		vo.setLabel("COLLABORATORS");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("TEAM");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("TEAM");
		vo.setLabel("TEAM");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("NOTES");
		vo.setType("textarea");
		vo.setItype("textarea");
		vo.setLabel("NOTES");
		o[6] = vo;

		r.setObj(o);
		return r;
	}















}
















