package csapi.impl.images;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class ImagesFields {

	public static final String TABLE_TYPE = "horizontal";
	public static final String FIELD_ID_REF = "ID";
	
	public static ObjGroupVO info() {
		return summary();
	}

	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("images");
		r.setType("images");
		r.setGroupid("images");
		r.setFields(new String[] {"DATE","TITLE","DESCRIPTION", "TYPE", "PUBLIC", "PATH", "FILEURL", "CREATED"});
		r.setIndex(new String[] {"DATE","TITLE", "TYPE", "PUBLIC", "CREATED"});
		ObjVO[] o = new ObjVO[18];

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
		vo.setFieldid("DESCRIPTION");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("DESCRIPTION");
		vo.setLinkfield("FILEURL");
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
		vo.setFieldid("PATH");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("PATH");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("URL");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("FILEURL");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ISPUBLIC");
		vo.setField("ISPUBLIC");
		vo.setLabel("PUBLIC");
		vo.setType("boolean");
		vo.setItype("boolean");
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("CREATED");
		vo.setField("CREATED");
		vo.setLabel("CREATED");
		vo.setType("type");
		vo.setItype("type");
		o[7] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REF");
		vo.setField("REF");
		vo.setType("text");
		vo.setItype("String");
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REF_NBR");
		vo.setField("REF_NBR");
		vo.setType("text");
		vo.setItype("String");
		o[9] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ACT_NBR");
		vo.setField("ACT_NBR");
		vo.setType("text");
		vo.setItype("String");
		o[10] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("PROJECT_NBR");
		vo.setField("PROJECT_NBR");
		vo.setType("text");
		vo.setItype("String");
		o[11] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REVIEW");
		vo.setField("REVIEW");
		vo.setType("text");
		vo.setItype("String");
		o[12] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REVIEW_GROUP");
		vo.setField("REVIEW_GROUP");
		vo.setType("text");
		vo.setItype("String");
		o[13] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ENTITY");
		vo.setField("ENTITY");
		vo.setType("text");
		vo.setItype("String");
		o[14] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ACTIVITY_TYPE");
		vo.setField("ACTIVITY_TYPE");
		vo.setType("text");
		vo.setItype("String");
		o[15] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("PROJECT_TYPE");
		vo.setField("PROJECT_TYPE");
		vo.setType("text");
		vo.setItype("String");
		o[16] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ID");
		vo.setField("ID");
		vo.setType("text");
		vo.setItype("String");
		o[17] = vo;

		r.setObj(o);
		return r;
	}

}
