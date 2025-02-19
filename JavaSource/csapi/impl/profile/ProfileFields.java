package csapi.impl.profile;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class ProfileFields {

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("profile");
		r.setType("profile");
		r.setGroupid("profile");
		ObjVO[] o = new ObjVO[13];


		ObjVO s = new ObjVO();
		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("USERNAME");
		s.setType("text");
		s.setItype("String");
		s.setField("USERNAME");
		s.setLabel("USERNAME");
		s.setEditable("N");
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

		r.setObj(o);
		return r;

	}

	public static ObjGroupVO parkingaddl() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("profile");
		r.setType("profile");
		r.setGroupid("profile");
//		r.setEditable(false);
		ObjVO[] o = new ObjVO[9];


		ObjVO s = new ObjVO();
		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ACCOUNT");
		s.setType("text");
		s.setItype("String");
		s.setField("ACCOUNT");
		s.setLabel("ACCOUNT");
		o[0] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ADDRESS");
		s.setType("String");
		s.setItype("text");
		s.setField("ADDRESS");
		s.setLabel("ADDRESS");
		s.setAddresstype("full");
		o[1] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("STR_NO");
		s.setType("String");
		s.setItype("text");
		s.setField("STR_NO");
		s.setAddresstype("strno");
		s.setLabel("STREET NBR");
		o[2] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("STR_MOD");
		s.setType("String");
		s.setItype("text");
		s.setAddresstype("strmod");
		s.setField("STR_MOD");
		s.setLabel("FRACTION");
		o[3] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PRE_DIR");
		s.setType("String");
		s.setItype("text");
		s.setAddresstype("predir");
		s.setField("PRE_DIR");
		s.setLabel("DIR");
		o[4] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LSO_STREET_ID");
		s.setType("String");
		s.setItype("text");
		s.setAddresstype("strname");
		s.setTextfield("STREET");
		s.setField("LSO_STREET_ID");
		s.setLabel("STREET");
		o[5] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("UNIT");
		s.setType("String");
		s.setItype("text");
		s.setAddresstype("unit");
		s.setField("UNIT");
		s.setLabel("UNIT");
		o[6] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("STATUS");
		s.setType("String");
		s.setItype("text");
		s.setField("STATUS");
		s.setLabel("STATUS");
		s.setEditable("N");
		o[7] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("COMMENT");
		s.setType("String");
		s.setItype("text");
		s.setField("COMMENT");
		s.setLabel("COMMENT");
		s.setEditable("N");
		o[8] = s;


		r.setObj(o);
		return r;

	}



}
