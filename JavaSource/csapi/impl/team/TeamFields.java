package csapi.impl.team;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class TeamFields {

	// title for detail group
	public static final String TYPE_DETAIL_TITLE = "TEAM";

	// name of main type table (this is a requirement for tables such as activity where the main table is "activity" but connected tables use "act"
	public static final String MAIN_TABLE_REF = "REF_TEAM";

	// reference to other tables
	public static final String TABLE_REF = "TEAM";

	// id field used to join other tables
	public static final String FIELD_ID_REF = "REF_TEAM_ID";

	public static final String TABLE_TYPE = "horizontal";

	public static ObjGroupVO info() {
		return details();
	}
	
	public static ObjGroupVO summary() {
		return details();
	}

	public static ObjGroupVO list() {
		return details();
	}

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("team");
		r.setType("team");
		r.setGroupid("team");
		
		
		r.setEditable(true);
		r.setDisabledeletefield("DISABLEEDIT");
		r.setDisableeditfield("DISABLEEDIT");
		r.setFields(new String[] {"TYPE","NAME","DEPARTMENT","EMAIL"});
		
		r.setIndex(new String[] {"TYPE","NAME"});
		ObjVO[] o = new ObjVO[7];


		ObjVO s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("TYPE");
		s.setType("type");
		s.setItype("text");
		s.setField("TYPE");
		s.setLabel("TYPE");
		s.setRequired("Y");
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
		o[2] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("TITLE");
		s.setType("short");
		s.setItype("text");
		s.setField("TITLE");
		s.setLabel("TITLE");
		s.setRequired("Y");
		o[3] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("DEPARTMENT");
		s.setType("short");
		s.setItype("text");
		s.setField("DEPARTMENT");
		s.setLabel("DEPARTMENT");
		s.setRequired("Y");
		o[4] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PEOPLE");
		s.setType("hidden");
		s.setItype("hidden");
		s.setField("PEOPLE");
		s.setLabel("PEOPLE");
		o[5] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("DISABLEEDIT");
		s.setType("hidden");
		s.setItype("hidden");
		s.setField("DISABLEEDIT");
		s.setLabel("DISABLEEDIT");
		o[6] = s;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO search() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("team");
		r.setType("team");
		r.setGroupid("team");

		ObjVO[] o = new ObjVO[1];


		ObjVO s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("q");
		s.setType("String");
		s.setItype("minisearch");
		s.setField("search");
		s.setLabel("search");
//		s.setPlaceholder("Search");
		s.setRequired("Y");
		o[0] = s;

//		s = new ObjVO();
//		s.setId(-1);
//		s.setOrder(0);
//		s.setFieldid("TYPE");
//		s.setType("String");
//		s.setItype("text");
//		s.setField("type");
//		s.setLabel("type");
//		s.setRequired("Y");
//		s.setChoices(Choices.getChoices(PeopleSQL.peopleType()));
//		o[1] = s;

		r.setObj(o);
		return r;
	}



}
