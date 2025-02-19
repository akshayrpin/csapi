package csapi.impl.people;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class PeopleFields {

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
	
	public static ObjGroupVO id() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("people");
		r.setType("people");
		r.setGroupid("people");
		r.setEditable(false);
		r.setFields(new String[] {"TYPE","NAME","EMAIL","LICENSE","LICENSE EXPIRATION","PRIMARY","UPDATED BY"});
		r.setIndex(new String[] {"NAME","TYPE"});
		ObjVO[] o = new ObjVO[7];


		ObjVO s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("TYPE");
		s.setType("type");
		s.setItype("text");
		s.setField("TYPE");
		s.setLabel("TYPE");
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
		o[2] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LIC_NO");
		s.setType("String");
		s.setItype("text");
		s.setField("LIC_NO");
		s.setLabel("LICENSE");
		o[3] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PRIMARY_CONTACT");
		s.setType("primary");
		s.setItype("primary");
		s.setField("PRIMARY_CONTACT");
		s.setLabel("PRIMARY");
		o[4] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LIC_EXP_DT");
		s.setField("LIC_EXP_DT");
		s.setLabel("LICENSE EXPIRATION");
		s.setType("date");
		s.setItype("date");
		o[5] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("UPDATED");
		s.setField("UPDATED");
		s.setLabel("UPDATED BY");
		s.setType("user");
		s.setItype("user");
		o[6] = s;

		r.setObj(o);
		return r;
	}
	
	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("people");
		r.setType("people");
		r.setGroupid("people");
		r.setEditable(true);
		r.setFields(new String[] {"TYPE","NAME","EMAIL","PHONE","ADDRESS","LICENSE","LICENSE EXPIRATION","PRIMARY"});
		r.setIndex(new String[] {"NAME","TYPE"});
		ObjVO[] o = new ObjVO[10];


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
//		s.setLink("PATH");
//		s.setLinkfield("PATH");
//		s.setTarget("lightbox-iframe");
		o[1] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("EMAIL");
		s.setType("email");
		s.setItype("email");
		s.setField("EMAIL");
		s.setLabel("EMAIL");
		s.setTarget("lightbox-iframe");
		s.setRequired("Y");
		s.setShowpublic(false);
		o[2] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PHONE_WORK");
		s.setType("String");
		s.setItype("phone");
		s.setField("PHONE_WORK");
		s.setLabel("PHONE");
		s.setTarget("lightbox-iframe");
		s.setRequired("Y");
		s.setShowpublic(false);
		o[3] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ADDRESS");
		s.setType("String");
		s.setItype("text");
		s.setField("ADDRESS");
		s.setLabel("ADDRESS");
		s.setTarget("lightbox-iframe");
		s.setRequired("Y");
		o[4] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LIC_NO");
		s.setType("String");
		s.setItype("text");
		s.setField("LIC_NO");
		s.setLabel("LICENSE");
		s.setTarget("lightbox-iframe");
		
		s.setRequired("Y");
		o[5] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PRIMARY_CONTACT");
		s.setType("primary");
		s.setItype("primary");
		s.setField("PRIMARY_CONTACT");
		s.setLabel("PRIMARY");
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
		s.setTarget("lightbox-iframe");
		s.setRequired("Y");
		o[7] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("SET_PRIMARY_CONTACT");
		s.setType("Integer");
		s.setItype("Integer");
		s.setDisplay("N");
		s.setTarget("lightbox-iframe");
		o[8] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LIC_EXP_DT");
		s.setField("LIC_EXP_DT");
		s.setLabel("LICENSE EXPIRATION");
		s.setType("date");
		s.setItype("date");
		s.setTarget("lightbox-iframe");
		o[9] = s;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO ext() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("people");
		r.setType("people");
		r.setGroupid("people");
		r.setEditable(false);
		r.setDeletable(false);
		r.setFields(new String[] {"TYPE","NAME","EMAIL","PHONE","ADDRESS"});
		r.setIndex(new String[] {"NAME","TYPE"});
		ObjVO[] o = new ObjVO[10];


		ObjVO s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("TYPE");
		s.setType("type");
		s.setItype("text");
		s.setField("TYPE");
		s.setLabel("TYPE");
		s.setEditable("N");
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
		s.setEditable("N");
		s.setTarget("lightbox-iframe");
//		s.setLink("PATH");
//		s.setLinkfield("PATH");
//		s.setTarget("lightbox-iframe");
		o[1] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("EMAIL");
		s.setType("email");
		s.setItype("email");
		s.setField("EMAIL");
		s.setLabel("EMAIL");
		s.setTarget("lightbox-iframe");
		s.setEditable("N");
		o[2] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PHONE_WORK");
		s.setType("String");
		s.setItype("phone");
		s.setField("PHONE_WORK");
		s.setLabel("PHONE");
		s.setTarget("lightbox-iframe");
		s.setEditable("N");
		o[3] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ADDRESS");
		s.setType("String");
		s.setItype("text");
		s.setField("ADDRESS");
		s.setLabel("ADDRESS");
		s.setTarget("lightbox-iframe");
		s.setEditable("N");
		o[4] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LIC_NO");
		s.setType("String");
		s.setItype("text");
		s.setField("LIC_NO");
		s.setLabel("LICENSE");
		s.setTarget("lightbox-iframe");
		s.setEditable("N");
		o[5] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PRIMARY_CONTACT");
		s.setType("primary");
		s.setItype("primary");
		s.setField("PRIMARY_CONTACT");
		s.setLabel("PRIMARY");
		s.setTarget("lightbox-iframe");
		s.setEditable("N");
		o[6] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PEOPLE");
		s.setType("String");
		s.setItype("hidden");
		s.setField("PEOPLE");
		s.setLabel("PEOPLE");
		s.setTarget("lightbox-iframe");
		s.setEditable("N");
		o[7] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("SET_PRIMARY_CONTACT");
		s.setType("Integer");
		s.setItype("Integer");
		s.setDisplay("N");
		s.setTarget("lightbox-iframe");
		s.setEditable("N");
		o[8] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LIC_EXP_DT");
		s.setField("LIC_EXP_DT");
		s.setLabel("LICENSE EXPIRATION");
		s.setType("date");
		s.setItype("date");
		s.setTarget("lightbox-iframe");
		s.setEditable("N");
		o[9] = s;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO list() {
		return details();
	}

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("people");
		r.setType("people");
		r.setGroupid("people");
		r.setEditable(false);
		ObjVO[] o = new ObjVO[25];


		ObjVO s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("FIRST_NAME");
		s.setType("String");
		s.setItype("text");
		s.setField("FIRST_NAME");
		s.setLabel("FIRST NAME");
		s.setRequired("Y");
		o[0] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LKUP_USERS_TYPE_ID");
		s.setType("type");
		s.setItype("text");
		s.setField("LKUP_USERS_TYPE_ID");
		s.setLabel("TYPE");
		s.setTextfield("TYPE");
		s.setRequired("Y");
		s.setLkup("type");
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
		s.setFieldid("LIC_NO");
		s.setType("String");
		s.setItype("text");
		s.setField("LIC_NO");
		s.setLabel("LICENSE");
		o[3] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LAST_NAME");
		s.setType("String");
		s.setItype("text");
		s.setField("LAST_NAME");
		s.setLabel("LAST NAME");
		o[4] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("EMAIL");
		s.setType("email");
		s.setItype("email");
		s.setField("EMAIL");
		s.setLabel("EMAIL");
		o[5] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("USERNAME");
		s.setType("text");
		s.setItype("String");
		s.setField("USERNAME");
		s.setLabel("USERNAME");
		o[6] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ADDRESS");
		s.setType("String");
		s.setItype("text");
		s.setField("ADDRESS");
		s.setLabel("ADDRESS");
		o[7] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("CITY");
		s.setType("String");
		s.setItype("text");
		s.setField("CITY");
		s.setLabel("CITY");
		o[8] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("STATE");
		s.setType("String");
		s.setItype("text");
		s.setField("STATE");
		s.setLabel("STATE");
		o[9] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ZIP");
		s.setType("String");
		s.setItype("text");
		s.setField("ZIP");
		s.setLabel("ZIP");
		o[10] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PHONE_WORK");
		s.setType("String");
		s.setItype("phone");
		s.setField("PHONE_WORK");
		s.setLabel("PHONE (WORK)");
		o[11] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PHONE_CELL");
		s.setType("String");
		s.setItype("phone");
		s.setField("PHONE_CELL");
		s.setLabel("PHONE (CELL)");
		o[12] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PHONE_HOME");
		s.setType("String");
		s.setItype("phone");
		s.setField("PHONE_HOME");
		s.setLabel("PHONE (HOME)");
		o[13] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("FAX");
		s.setType("String");
		s.setItype("phone");
		s.setField("FAX");
		s.setLabel("FAX");
		o[14] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LIC_NO");
		s.setType("String");
		s.setItype("text");
		s.setField("LIC_NO");
		s.setLabel("LICENSE");
		o[15] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("LIC_EXP_DT");
		s.setType("date");
		s.setItype("date");
		s.setField("LIC_EXP_DT");
		s.setLabel("LICENSE EXPIRATION");
		o[16] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("GEN_LIABILITY_DT");
		s.setType("date");
		s.setItype("date");
		s.setField("GEN_LIABILITY_DT");
		s.setLabel("GENERAL LIABILITY DATE");
		o[17] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("AUTO_LIABILITY_DT");
		s.setType("date");
		s.setItype("date");
		s.setField("AUTO_LIABILITY_DT");
		s.setLabel("AUTO LIABILITY DATE");
		o[18] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("WORK_COMP_EXP_DT");
		s.setType("date");
		s.setItype("date");
		s.setField("WORK_COMP_EXP_DT");
		s.setLabel("WORKERS COMP EXPIRATION");
		o[19] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("REQUIRED_LICENSE");
		s.setType("text");
		s.setItype("hidden");
		s.setField("PRIMARY_CONTACT");
		s.setLabel("REQUIRED_LICENSE");
		o[20] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PRIMARY_CONTACT");
		s.setType("boolean");
		s.setItype("boolean");
		s.setField("PRIMARY_CONTACT");
		s.setLabel("PRIMARY");
		o[21] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PEOPLE");
		s.setType("String");
		s.setItype("hidden");
		s.setField("PEOPLE");
		s.setLabel("PEOPLE");
		o[22] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("SET_PRIMARY_CONTACT");
		s.setType("text");
		s.setItype("hidden");
		s.setField("SET_PRIMARY_CONTACT");
		o[23] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("COPYAPPLICANT");
		s.setType("Boolean");
		s.setItype("boolean");
		s.setField("COPYAPPLICANT");
		s.setDisplay("N");
		o[24] = s;

		r.setObj(o);
		return r;
	}


	public static ObjGroupVO search() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("people");
		r.setType("people");
		r.setGroupid("people");

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
