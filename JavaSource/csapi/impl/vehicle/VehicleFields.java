package csapi.impl.vehicle;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class VehicleFields {

	// name of main type table (this is a requirement for tables such as activity where the main table is "activity" but connected tables use "act"
	public static final String MAIN_TABLE_REF = "VEHICLE";

	public static final String TABLE_TYPE = "horizontal";

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("vehicle");
		r.setType("vehicle");
		r.setGroupid("vehicle");
		r.setFields(new String[] {"LICENSE PLATE","STATE","MAKE","MODEL","YEAR","COLOR"});
		r.setIndex(new String[] {"MAKE","MODEL","YEAR","COLOR"});
		ObjVO[] o = new ObjVO[8];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("LICENSE_PLATE");
		vo.setType("text");
		vo.setItype("text");
		vo.setField("LICENSE_PLATE");
		vo.setLabel("LICENSE PLATE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("BLOCKED");
		vo.setType("Boolean");
		vo.setItype("Boolean");
		vo.setField("BLOCKED");
		vo.setLabel("BLOCKED");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REG_STATE");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("REG_STATE");
		vo.setLabel("STATE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REG_EXP_DT");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("REG_EXP_DT");
		vo.setLabel("EXPIRATION DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("VEHICLE_MAKE");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("VEHICLE_MAKE");
		vo.setLabel("MAKE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("VEHICLE_MODEL");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("VEHICLE_MODEL");
		vo.setLabel("MODEL");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("VEHICLE_COLOR");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("VEHICLE_COLOR");
		vo.setLabel("COLOR");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("VEHICLE_YEAR");
		vo.setType("Integer");
		vo.setItype("Integer");
		vo.setField("VEHICLE_YEAR");
		vo.setLabel("YEAR");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[7] = vo;

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
	

}




