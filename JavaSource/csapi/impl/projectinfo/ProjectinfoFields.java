package csapi.impl.projectinfo;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class ProjectinfoFields {

	//public static final String TABLE_TYPE = "horizontal";
	
	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("projectinfo");
		r.setType("projectinfo");
		r.setGroupid("projectinfo");
		r.setLabel("Project Information");
		ObjVO[] o = new ObjVO[8];

		ObjVO vo = new ObjVO();
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("LKUP_PROJECTINFO_PROJECT_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("LKUP_PROJECTINFO_PROJECT_ID");
		vo.setTextfield("LKUP_PROJECTINFO_PROJECT_TEXT");
		vo.setLabel("PROJECT SCOPE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setLkup("status");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("LKUP_PROJECTINFO_BUILDINGTYPE_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("LKUP_PROJECTINFO_BUILDINGTYPE_ID");
		vo.setTextfield("LKUP_PROJECTINFO_BUILDINGTYPE_TEXT");
		vo.setLabel("BUILDING TYPE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setLkup("type");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("LKUP_PROJECTINFO_BUILDINGUSE_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("LKUP_PROJECTINFO_BUILDINGUSE_ID");
		vo.setTextfield("LKUP_PROJECTINFO_BUILDINGUSE_TEXT");
		vo.setLabel("BUILDING USE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setLkup("typedescriptions");
		o[2] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("OTHER_PROJECT_TYPE");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("OTHER_PROJECT_TYPE");
		vo.setLabel("OTHER_PROJECT_TYPE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[3] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("OTHER_BUILDING_TYPE");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("OTHER_BUILDING_TYPE");
		vo.setLabel("OTHER_BUILDING_TYPE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[4] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("OTHER_BUILDING_USE");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("OTHER_BUILDING_USE");
		vo.setLabel("OTHER_BUILDING_USE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[5] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("OTHER_USE_EXISTING");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("OTHER_USE_EXISTING");
		vo.setLabel("OTHER_USE_EXISTING");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("OTHER_USE_PROPOSED");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("OTHER_USE_PROPOSED");
		vo.setLabel("OTHER_USE_PROPOSED");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[7] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("projectinfo");
		r.setType("projectinfo");
		r.setGroupid("projectinfo");
		r.setLabel("Project Information");
		ObjVO[] o = new ObjVO[4];

		ObjVO vo = new ObjVO();
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("LKUP_PROJECTINFO_PROJECT_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("LKUP_PROJECTINFO_PROJECT_ID");
		vo.setTextfield("LKUP_PROJECTINFO_PROJECT_TEXT");
		vo.setLabel("PROJECT SCOPE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setLkup("status");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("LKUP_PROJECTINFO_BUILDINGTYPE_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("LKUP_PROJECTINFO_BUILDINGTYPE_ID");
		vo.setTextfield("LKUP_PROJECTINFO_BUILDINGTYPE_TEXT");
		vo.setLabel("BUILDING TYPE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setLkup("type");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("LKUP_PROJECTINFO_BUILDINGUSE_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("LKUP_PROJECTINFO_BUILDINGUSE_ID");
		vo.setTextfield("LKUP_PROJECTINFO_BUILDINGUSE_TEXT");
		vo.setLabel("BUILDING USE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setLkup("typedescriptions");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("");
		vo.setTextfield("");
		vo.setLabel("");
		o[3] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO info() {
		return summary();
	}
	
	public static ObjGroupVO list() {
		return summary();
	}
	
	public static ObjGroupVO id() {
		return summary();
	}
	
}
