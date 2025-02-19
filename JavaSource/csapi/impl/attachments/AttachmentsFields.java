package csapi.impl.attachments;

import csapi.common.Choices;
import csapi.impl.lkup.LkupAgent;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class AttachmentsFields {

	// title for detail group
	public static final String TYPE_DETAIL_TITLE = "ATTACHMENTS";

	// name of main type table (this is a requirement for tables such as activity where the main table is "activity" but connected tables use "act"
	public static final String MAIN_TABLE_REF = "ATTACHMENTS";

	// reference to other tables
	public static final String TABLE_REF = "ATTACHMENTS";

	// reference to type table
	public static final String TYPE_TABLE_REF = "LKUP_ATTACHMENTS_TYPE";

	// id field used to join other tables
	public static final String FIELD_ID_REF = "ATTACHMENT_ID";

	public static final String TABLE_TYPE = "horizontal";

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("attachments");
		r.setType("attachments");
		r.setGroupid("attachments");
		ObjVO[] o = new ObjVO[6];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TITLE");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("TITLE");
		vo.setLabel("TITLE");
		vo.setLinkfield("PATH");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(10);
		vo.setFieldid("DESCRIPTION");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("DESCRIPTION");
		vo.setLabel("DESCRIPTION");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("PATH");
		vo.setType("String");
		vo.setItype("file");
		vo.setField("PATH");
		vo.setLabel("PATH");
		vo.setLinkfield("FILEURL");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setUpdateIfValuePresent("N");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(30);
		vo.setFieldid("LKUP_ATTACHMENTS_TYPE_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("LKUP_ATTACHMENTS_TYPE_ID");
		vo.setTextfield("LKUP_ATTACHMENTS_TYPE_TEXT");
		vo.setLabel("TYPE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		//vo.setChoices(LkupAgent.types("attachments", "attachments", -1, -1));
		
		vo.setLkup("type");
		
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(30);
		vo.setFieldid("ISPUBLIC");
		vo.setType("Boolean");
		vo.setItype("Boolean");
		vo.setField("ISPUBLIC");
		vo.setLabel("PUBLIC");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(30);
		vo.setFieldid("SENSITIVE");
		vo.setType("Boolean");
		vo.setItype("Boolean");
		vo.setField("SENSITIVE");
		vo.setLabel("SENSITIVE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[5] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("attachments");
		r.setType("attachments");
		r.setGroupid("attachments");
		r.setFields(new String[] {"DATE","TITLE","DESCRIPTION", "TYPE", "PUBLIC", "SENSITIVE", "CREATED"});
		r.setIndex(new String[] {"DATE","TITLE", "TYPE", "PUBLIC", "CREATED"});
		ObjVO[] o = new ObjVO[7];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("CREATED_DATE");
		vo.setType("date");
		vo.setItype("date");
		vo.setField("CREATED_DATE");
		vo.setLabel("DATE");
		vo.setLinkfield("FILEURL");
		vo.setRequired("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TITLE");
		vo.setType("short");
		vo.setItype("text");
		vo.setField("TITLE");
		vo.setLinkfield("FILEURL");
		vo.setTarget("_blank");
		vo.setRequired("Y");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("DESCRIPTION");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("DESCRIPTION");
		vo.setLinkfield("FILEURL");
		vo.setTarget("_blank");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TYPE");
		vo.setType("type");
		vo.setItype("type");
		vo.setField("TYPE");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ISPUBLIC");
		vo.setField("ISPUBLIC");
		vo.setLabel("PUBLIC");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setShowpublic(false);
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("SENSITIVE");
		vo.setField("SENSITIVE");
		vo.setLabel("SENSITIVE");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setShowpublic(false);
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("CREATED");
		vo.setField("CREATED");
		vo.setLabel("CREATED");
		vo.setType("type");
		vo.setItype("type");
		vo.setShowpublic(false);
		o[6] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO ext() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("attachments");
		r.setType("attachments");
		r.setGroupid("attachments");
		r.setDeletable(false);
		r.setEditable(false);
		r.setFields(new String[] {"DATE","TITLE", "TYPE", "PUBLIC", "CREATED"});
		r.setIndex(new String[] {"DATE","TITLE", "TYPE", "PUBLIC", "CREATED"});
		ObjVO[] o = new ObjVO[5];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("CREATED_DATE");
		vo.setType("date");
		vo.setItype("date");
		vo.setField("CREATED_DATE");
		vo.setLabel("DATE");
		vo.setLinkfield("FILEURL");
		vo.setRequired("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TITLE");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("TITLE");
		vo.setLinkfield("FILEURL");
		vo.setTarget("_blank");
		vo.setRequired("Y");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TYPE");
		vo.setType("type");
		vo.setItype("type");
		vo.setField("TYPE");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ISPUBLIC");
		vo.setField("ISPUBLIC");
		vo.setLabel("PUBLIC");
		vo.setType("boolean");
		vo.setItype("boolean");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("CREATED");
		vo.setField("CREATED");
		vo.setLabel("CREATED");
		vo.setType("type");
		vo.setItype("type");
		o[4] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO id() {
		return summary();
	}
	
	public static ObjGroupVO info() {
		return summary();
	}
	
	public static ObjGroupVO list() {
		return summary();
	}

}
