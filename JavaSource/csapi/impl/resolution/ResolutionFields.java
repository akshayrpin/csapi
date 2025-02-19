package csapi.impl.resolution;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class ResolutionFields {

	//public static final String TABLE_TYPE = "horizontal";

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("resolution");
		r.setType("resolution");
		r.setGroupid("resolution");
		r.setHistory(true);
		r.setDeletable(false);
		r.setFields(new String[] {"NUMBER","TITLE","ADOPTED"});
		r.setIndex(new String[] {"NUMBER","PART","APPROVED","COMPLIANCE"});
		ObjVO[] o = new ObjVO[14];

		ObjVO vo = new ObjVO();

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(10);
		vo.setFieldid("TYPE");
		vo.setType("type");
		vo.setItype("type");
		vo.setField("TYPE");
		vo.setLabel("TYPE");
		vo.setLkup("type");
		vo.setEditable("N");
		vo.setRequired("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("DATE");
		vo.setType("date");
		vo.setItype("date");
		vo.setField("DATE");
		vo.setLabel("DATE");
		vo.setDefaultvalue("current");
		vo.setEmptyonedit("Y");
		vo.setMultiedit("Y");
		o[1] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("RESOLUTION_NUMBER");
		vo.setType("type");
		vo.setItype("Text");
		vo.setField("RESOLUTION_NUMBER");
		vo.setLabel("NUMBER");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("PART");
		vo.setType("type");
		vo.setItype("Text");
		vo.setField("PART");
		vo.setLabel("PART");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TITLE");
		vo.setType("String");
		vo.setItype("largetext");
		vo.setField("TITLE");
		vo.setLabel("TITLE");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("PART_TITLE");
		vo.setType("String");
		vo.setItype("largetext");
		vo.setField("PART_TITLE");
		vo.setLabel("PART TITLE");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("DESCRIPTION");
		vo.setType("String");
		vo.setItype("textarea");
		vo.setField("DESCRIPTION");
		vo.setLabel("DESCRIPTION");
		vo.setEmptyonedit("Y");
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(10);
		vo.setFieldid("STATUS_ID");
		vo.setType("status");
		vo.setItype("status");
		vo.setField("STATUS_ID");
		vo.setTextfield("STATUS");
		vo.setLabel("STATUS");
		vo.setLkup("status");
		vo.setMultiedit("Y");
		o[7] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ADOPTED_DATE");
		vo.setType("date");
		vo.setItype("hidden");
		vo.setField("ADOPTED_DATE");
		vo.setLabel("ADOPTED");
		vo.setEditable("N");
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("COMPLIANCE");
		vo.setType("complete");
		vo.setItype("complete");
		vo.setField("COMPLIANCE");
		vo.setLabel("COMPLIANCE");
//		vo.setEditable("N");
		o[9] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("EXP_DATE");
		vo.setType("date");
		vo.setItype("date");
		vo.setField("EXP_DATE");
		vo.setLabel("EXPIRATION");
		o[10] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("COMPLIANCE_DATE");
		vo.setType("date");
		vo.setItype("date");
		vo.setField("COMPLIANCE_DATE");
		vo.setLabel("COMPLIANCE DATE");
		o[11] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("APPLY_TO_ALL");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("APPLY_TO_ALL");
		vo.setLabel("APPLY_TO_ALL");
		vo.setMultiedit("Y");
		o[12] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("FILE");
		vo.setType("file");
		vo.setItype("hidden");
		vo.setField("FILE");
		o[13] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO summary() {
		return details();
	}

	public static ObjGroupVO info() {
		return details();
	}
	
	public static ObjGroupVO list() {
		return details();
	}

	public static ObjGroupVO history() {
		return details();
	}

	public static ObjGroupVO id() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("resolution");
		r.setType("resolution");
		r.setGroupid("resolution");
		r.setHistory(true);
		r.setDeletable(false);
		ObjVO[] o = new ObjVO[12];

		ObjVO vo = new ObjVO();

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("RESOLUTION_NUMBER");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("RESOLUTION_NUMBER");
		vo.setLabel("Resolution Number");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("STATUS");
		vo.setType("status");
		vo.setItype("status");
		vo.setField("STATUS");
		vo.setLabel("Status");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("TITLE");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("TITLE");
		vo.setLabel("title");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("REF");
		vo.setType("type");
		vo.setItype("type");
		vo.setField("REF");
		vo.setLabel("Type");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("PART");
		vo.setType("type");
		vo.setItype("Text");
		vo.setField("PART");
		vo.setLabel("Part");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("RESOLUTION_DATE");
		vo.setType("date");
		vo.setItype("date");
		vo.setField("RESOLUTION_DATE");
		vo.setLabel("Resolution Date");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("NAME");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("NAME");
		vo.setLabel("Part Title");
		o[6] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ADOPTED_DATE");
		vo.setType("date");
		vo.setItype("date");
		vo.setField("ADOPTED_DATE");
		vo.setLabel("Adopted Date");
		o[7] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("DESCRIPTION");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("DESCRIPTION");
		vo.setLabel("Description");
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("EXP_DATE");
		vo.setType("date");
		vo.setItype("date");
		vo.setField("EXP_DATE");
		vo.setLabel("EXPIRATION");
		o[9] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("REF_NUMBER");
		vo.setType("type");
		vo.setItype("type");
		vo.setField("REF_NUMBER");
		vo.setLabel("Reference");
		o[10] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATED");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("UPDATED");
		vo.setLabel("Updated By");
		o[11] = vo;

		r.setObj(o);
		return r;
	}



}
