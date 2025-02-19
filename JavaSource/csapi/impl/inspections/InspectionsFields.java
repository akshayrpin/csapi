package csapi.impl.inspections;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class InspectionsFields {

	// title for detail group
	public static final String TYPE_DETAIL_TITLE = "INSPECTIONS";

	// name of main type table (this is a requirement for tables such as activity where the main table is "activity" but connected tables use "act"
	public static final String MAIN_TABLE_REF = "REVIEW";

	// reference to other tables
	public static final String TABLE_REF = "REVIEW";

	// id field used to join other tables
	public static final String FIELD_ID_REF = "REVIEW_ID";

	public static final String TABLE_TYPE = "horizontal";

	public static ObjGroupVO my() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("inspections");
		r.setType("inspections");
		r.setGroupid("inspections");
		ObjVO[] o = new ObjVO[15];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		//vo.setFieldid("COMBOREVIEW_ID");
		vo.setFieldid("TYPE");
		vo.setType("string");
		vo.setItype("string");
		vo.setField("TYPE");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		//vo.setFieldid("REF_COMBOREVIEW_REVIEW_ID");
		vo.setFieldid("SUBJECT");
		vo.setType("string");
		vo.setItype("string");
		vo.setField("SUBJECT");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		//vo.setFieldid("REF_COMBOREVIEW_ACTION_ID");
		vo.setFieldid("START_DATE");
		vo.setType("string");
		vo.setItype("string");
		vo.setField("START_DATE");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		//vo.setFieldid("APPOINTMENT_ID");
		vo.setFieldid("END_DATE");
		vo.setType("string");
		vo.setItype("string");
		vo.setField("END_DATE");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		//vo.setFieldid("APPOINTMENT_ID");
		vo.setFieldid("TIME");
		vo.setType("string");
		vo.setItype("string");
		vo.setField("TIME");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		//vo.setFieldid("APPOINTMENT_SCHEDULE_ID");
		vo.setFieldid("STATUS");
		vo.setType("string");
		vo.setItype("string");
		vo.setField("STATUS");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		//vo.setFieldid("REVIEW_ID");
		vo.setFieldid("DESCRIPTION");
		vo.setType("string");
		vo.setItype("string");
		vo.setField("DESCRIPTION");
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ACT_NBR");
		vo.setType("string");
		vo.setItype("string");
		vo.setField("ACT_NBR");
		o[7] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REF_COMBOREVIEW_REVIEW_ID");
		vo.setType("integer");
		vo.setItype("integer");
		vo.setField("REF_COMBOREVIEW_REVIEW_ID");
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REFERENCE");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("REFERENCE");
		o[9] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REFERENCE_ID");
		vo.setType("Integer");
		vo.setItype("Integer");
		vo.setField("REFERENCE_ID");
		o[10] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REFERENCE_TYPE");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("REFERENCE_TYPE");
		o[11] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("EDITABLE");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("EDITABLE");
		o[12] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REVIEW_COMMENTS");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("REVIEW_COMMENTS");
		o[13] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ACTIVITY_ID");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("ACTIVITY_ID");
		o[14] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO my1() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("inspections");
		r.setType("inspections");
		r.setGroupid("inspections");
		r.setFields(new String[] {"TYPE","SUBJECT","STATUS","START_DATE","END_DATE"});
		r.setIndex(new String[] {"TYPE","STATUS","START_DATE"});
		ObjVO[] o = new ObjVO[13];

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

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("STR_NUM");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("STR_NUM");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("STR_MOD");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("STR_MOD");
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("PRE_DIR");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("PRE_DIR");
		o[7] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("STR_NAME");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("STR_NAME");
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("STR_TYPE");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("STR_TYPE");
		o[9] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("UNIT");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("UNIT");
		o[10] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ACT_TYPE");
		vo.setType("type");
		vo.setItype("text");
		vo.setField("ACT_TYPE");
		o[11] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ADDRESS");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("ADDRESS");
		o[12] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO full() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("inspections");
		r.setType("inspections");
		r.setGroupid("inspections");
		r.setAddable(false);
		r.setEditable(true);
		r.setDeletable(false);
		r.setFields(new String[] {"SELECT","TYPE","PROJECT","ACTIVITY","REVIEW","ADDRESS","INSPECTOR","INSPECTION DATE","STATUS"});
		r.setIndex(new String[] {"TYPE","PROJECT","REVIEW","ACTIVITY","ADDRESS","INSPECTOR","INSPECTION DATE","STATUS"});
		ObjVO[] o = new ObjVO[11];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ID");
		vo.setType("checkbox");
		vo.setItype("id");
		vo.setField("ID");
		vo.setLabel("SELECT");
		vo.setRelfield("AVAILABILITY_ID");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("GROUP_NAME");
		vo.setType("type");
		vo.setItype("text");
		vo.setTextfield("GROUP_NAME");
		vo.setField("GROUP_NAME");
		vo.setLabel("GROUP NAME");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REVIEW");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("REVIEW");
		vo.setLabel("REVIEW");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("PROJECT");
		vo.setType("short");
		vo.setItype("text");
		vo.setField("PROJECT");
		vo.setLabel("PROJECT");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ACTIVITY");
		vo.setType("short");
		vo.setItype("text");
		vo.setField("ACTIVITY");
		vo.setLabel("ACTIVITY");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ADDRESS");
		vo.setType("address");
		vo.setItype("text");
		vo.setField("ADDRESS");
		vo.setLabel("ADDRESS");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("START_DATE");
		vo.setType("datetime");
		vo.setItype("datetime");
		vo.setField("START_DATE");
		vo.setLabel("INSPECTION DATE");
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("END_DATE");
		vo.setType("datetime");
		vo.setItype("datetime");
		vo.setField("END_DATE");
		o[7] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("STATUS");
		vo.setType("short");
		vo.setItype("text");
		vo.setField("STATUS");
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("INSPECTOR");
		vo.setType("short");
		vo.setItype("text");
		vo.setField("INSPECTOR");
		o[9] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TYPE");
		vo.setType("type");
		vo.setItype("text");
		vo.setField("TYPE");
		o[10] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO type() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("inspections");
		r.setType("inspections");
		r.setGroupid("inspections");
		r.setEditable(false);
		r.setDeletable(false);
		ObjVO[] o = new ObjVO[11];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("VALUE");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("VALUE");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TEXT");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("TEXT");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("DESCRIPTION");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("DESCRIPTION");
		vo.setLabel("DESCRIPTION");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REQUIRE_ISSUED");
		vo.setType("text");
		vo.setItype("boolean");
		vo.setField("REQUIRE_ISSUED");
		vo.setLabel("REQUIRE_ISSUED");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ISSUED");
		vo.setType("text");
		vo.setItype("boolean");
		vo.setField("ISSUED");
		vo.setLabel("ISSUED");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("LKUP_VOIP_MENU_ID");
		vo.setType("text");
		vo.setItype("integer");
		vo.setField("LKUP_VOIP_MENU_ID");
		vo.setLabel("LKUP_VOIP_MENU_ID");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("PRESS");
		vo.setType("text");
		vo.setItype("integer");
		vo.setField("PRESS");
		vo.setLabel("PRESS");
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("VOIP_DESCRIPTION");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("VOIP_DESCRIPTION");
		vo.setLabel("VOIP_DESCRIPTION");
		o[7] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("LOGIC");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("LOGIC");
		vo.setLabel("LOGIC");
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("HINT");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("HINT");
		vo.setLabel("HINT");
		o[9] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("SAY_DESCRIPTION");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("SAY_DESCRIPTION");
		vo.setLabel("SAY_DESCRIPTION");
		o[10] = vo;

		r.setObj(o);
		return r;
	}




	public static ObjGroupVO activities() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("inspections");
		r.setType("inspections");
		r.setGroupid("inspections");
		r.setEditable(false);
		r.setDeletable(false);
		r.setFields(new String[] {"VALUE","TEXT","DESCRIPTION"});
		r.setIndex(new String[] {"VALUE","TEXT","DESCRIPTION"});
		ObjVO[] o = new ObjVO[3];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("VALUE");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("VALUE");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TEXT");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("TEXT");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("DESCRIPTION");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("DESCRIPTION");
		vo.setLabel("DESCRIPTION");
		o[2] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("inspections");
		r.setType("inspections");
		r.setGroupid("inspections");
		ObjVO[] o = new ObjVO[9];

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
		
		r.setObj(o);
		return r;
	}


}
