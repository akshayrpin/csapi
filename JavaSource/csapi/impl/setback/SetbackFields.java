package csapi.impl.setback;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class SetbackFields {

	public static final String TABLE_TYPE = "crosstab";

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("setback");
		r.setType("setback");
		r.setGroupid("setback");
		r.setCrosstab("TYPE");
		r.setFields(new String[] {"FT","IN","COMMENTS","REQ_FT","REQ_IN","REQ_COMMENTS"});
		r.setIndex(new String[] {"FT","IN"});
		ObjVO[] o = new ObjVO[7];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TYPE");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("TYPE");
		vo.setLabel("TYPE");
		vo.setRequired("N");
		vo.setAddable("Y");
		vo.setEditable("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("FT");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("FT");
		vo.setLinkfield("FT");
		vo.setAddable("Y");
		vo.setEditable("Y");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("IN");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("IN");
		vo.setLinkfield("IN");
		vo.setAddable("Y");
		vo.setEditable("Y");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("COMMENTS");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("COMMENTS");
		vo.setLinkfield("COMMENTS");
		vo.setAddable("Y");
		vo.setEditable("Y");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REQ_FT");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("REQ_FT");
		vo.setLinkfield("REQ_FT");
		vo.setAddable("Y");
		vo.setEditable("Y");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REQ_IN");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("REQ_IN");
		vo.setLinkfield("REQ_IN");
		vo.setAddable("Y");
		vo.setEditable("Y");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REQ_COMMENT");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("REQ_COMMENT");
		vo.setLinkfield("REQ_COMMENT");
		vo.setAddable("Y");
		vo.setEditable("Y");
		o[6] = vo;

		r.setObj(o);
		return r;
	
	}

	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("setback");
		r.setType("setback");
		r.setGroupid("setback");
		r.setCrosstab("TYPE");
		r.setFields(new String[] {"FT","IN","COMMENTS","REQ_FT","REQ_IN","REQ_COMMENTS"});
		r.setIndex(new String[] {"FT","IN"});
		ObjVO[] o = new ObjVO[7];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TYPE");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("TYPE");
		vo.setLabel("TYPE");
		vo.setRequired("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("FT");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("FT");
		vo.setLinkfield("FT");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("IN");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("IN");
		vo.setLinkfield("IN");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("COMMENTS");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("COMMENTS");
		vo.setLinkfield("COMMENTS");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REQ_FT");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("REQ_FT");
		vo.setLinkfield("REQ_FT");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REQ_IN");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("REQ_IN");
		vo.setLinkfield("REQ_IN");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REQ_COMMENT");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("REQ_COMMENT");
		vo.setLinkfield("REQ_COMMENT");
		o[6] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO info() {
		return summary();
	}
	
}
