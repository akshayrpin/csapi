package csapi.impl.users;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class UsersFields {

	// title for detail group
	public static final String TYPE_DETAIL_TITLE = "USERS";

	// name of main type table (this is a requirement for tables such as activity where the main table is "activity" but connected tables use "act"
	public static final String MAIN_TABLE_REF = "USERS";

	// reference to other tables
	public static final String TABLE_REF = "USERS";

	// id field used to join other tables
	public static final String FIELD_ID_REF = "USERS_ID";

	public static final String TABLE_TYPE = "horizontal";

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("people");
		r.setType("people");
		r.setGroupid("people");
		r.setEditable(false);
		ObjVO[] o = new ObjVO[15];


		ObjVO s = new ObjVO();
		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("USERNAME");
		s.setType("text");
		s.setItype("String");
		s.setField("USERNAME");
		s.setLabel("USERNAME");
		o[0] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("FIRST_NAME");
		s.setType("String");
		s.setItype("text");
		s.setField("FIRST_NAME");
		s.setLabel("FIRST NAME");
		s.setRequired("Y");
		o[1] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("MIDDLE_NAME");
		s.setType("String");
		s.setItype("text");
		s.setField("MIDDLE_NAME");
		s.setLabel("MIDDLE NAME");
		o[2] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LAST_NAME");
		s.setType("String");
		s.setItype("text");
		s.setField("LAST_NAME");
		s.setLabel("LAST NAME");
		o[3] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("EMAIL");
		s.setType("email");
		s.setItype("email");
		s.setField("EMAIL");
		s.setLabel("EMAIL");
		o[4] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ADDRESS");
		s.setType("String");
		s.setItype("text");
		s.setField("ADDRESS");
		s.setLabel("ADDRESS");
		o[5] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("CITY");
		s.setType("String");
		s.setItype("text");
		s.setField("CITY");
		s.setLabel("CITY");
		o[6] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("STATE");
		s.setType("String");
		s.setItype("text");
		s.setField("STATE");
		s.setLabel("STATE");
		o[7] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ZIP");
		s.setType("String");
		s.setItype("text");
		s.setField("ZIP");
		s.setLabel("ZIP");
		o[8] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PHONE_WORK");
		s.setType("String");
		s.setItype("phone");
		s.setField("PHONE_WORK");
		s.setLabel("PHONE (WORK)");
		o[9] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PHONE_CELL");
		s.setType("String");
		s.setItype("phone");
		s.setField("PHONE_CELL");
		s.setLabel("PHONE (CELL)");
		o[10] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PHONE_HOME");
		s.setType("String");
		s.setItype("phone");
		s.setField("PHONE_HOME");
		s.setLabel("PHONE (HOME)");
		o[11] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("FAX");
		s.setType("String");
		s.setItype("phone");
		s.setField("FAX");
		s.setLabel("FAX");
		o[12] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PEOPLE");
		s.setType("String");
		s.setItype("hidden");
		s.setField("PEOPLE");
		s.setLabel("PEOPLE");
		o[13] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("CURRENT");
		s.setType("String");
		s.setItype("String");
		s.setField("CURRENT");
		s.setLabel("CURRENT");
		o[14] = s;

		r.setObj(o);
		return r;

	}



}
