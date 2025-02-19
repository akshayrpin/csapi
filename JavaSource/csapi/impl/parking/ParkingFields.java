package csapi.impl.parking;

import csapi.common.FieldObjects;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class ParkingFields {

	// title for detail group
	public static final String TYPE_DETAIL_TITLE = "PARKING";
//	public static final String TABLE_TYPE_LIST = "vertical";

	public static ObjGroupVO summary() {
		return details();
	}

	public static ObjGroupVO list() {
		return details();
	}

	public static ObjGroupVO info() {
		return details();
	}

	public static ObjGroupVO details() {
		ObjGroupVO g = new ObjGroupVO();
		g.setGroupid("parking");
		g.setGroup("parking");
		g.setType("parking");
		
		ObjVO[] o = new ObjVO[5];
		
		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("NO_SPACES");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("NO_SPACES");
		vo.setLabel("Number of Spaces");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("ID");
		vo.setLabel("ACCOUNT");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("N");
		o[1] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("NO_CARS");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("NO_CARS");
		vo.setLabel("Number of Cars");
		o[2] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("SECRET");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("SECRET");
		vo.setLabel("Secret Code");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("APPROVED_SPACE");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("APPROVED_SPACE");
		vo.setLabel("Approved Spaces");
		o[4] = vo;

		g.setObj(o);
		
		return g;
	}

	public static ObjGroupVO id() {
		ObjGroupVO g = new ObjGroupVO();
		g.setGroupid("parking");
		g.setGroup("parking");
		g.setType("parking");
		
		ObjVO[] o = new ObjVO[5];
		
		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("NO_SPACES");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("NO_SPACES");
		vo.setLabel("Number of Spaces");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ACCOUNT");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("ACCOUNT");
		vo.setLabel("ACCOUNT");
		vo.setRequired("Y");
		o[1] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("NO_CARS");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("NO_CARS");
		vo.setLabel("Number of Cars");
		o[2] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("SECRET");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("SECRET");
		vo.setLabel("Secret Code");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("APPROVED_SPACE");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("APPROVED_SPACE");
		vo.setLabel("Approved Spaces");
		o[4] = vo;

		g.setObj(o);
		
		return g;
	}

	public static ObjGroupVO myActive() {
		ObjGroupVO g = new ObjGroupVO();
		g.setGroupid("parking");
		g.setGroup("parking");
		g.setType("parking");
		
		ObjVO[] o = new ObjVO[5];
		
		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ACCOUNT");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("ACCOUNT");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("START");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("START_DATE");
		vo.setLabel("Start Date");
		o[1] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("EXPIRE");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("EXP_DATE");
		vo.setLabel("Exp Date");
		o[2] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ACT_NBR");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("ACT_NBR");
		vo.setLabel("Activity Number");
		o[3] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ADDRESS");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("ADDRESS");
		vo.setLabel("Address");
		o[4] = vo;
		
		g.setObj(o);
		
		return g;
	}

	public static ObjGroupVO[] addlfields() {
		ObjGroupVO[] ga = new ObjGroupVO[1];
		ObjGroupVO g = new ObjGroupVO();
		g.setGroupid("vehicle");
		g.setGroup("vehicle");
		g.setType("parking");
		
		ObjVO[] o = new ObjVO[5];
		o[0] = FieldObjects.toObject(1, 3, "LICENSE_PLATE", "String", "text", "LICENSE #", "", "", "", "", "", "", "", "Y","Y","Y");
		o[1] = FieldObjects.toObject(2, 3, "REG_STATE", "String", "text", "REG STATE", "", "", "", "", "", "", "", "N","Y","Y");
		o[2] = FieldObjects.toObject(3, 3, "VEHICLE_MAKE", "String", "text", "MAKE", "", "", "", "", "", "", "", "N","Y","Y");
		o[3] = FieldObjects.toObject(4, 3, "VEHICLE_MODEL", "String", "text", "MODEL", "", "", "", "", "", "", "", "N","Y","Y");
		o[4] = FieldObjects.toObject(5, 3, "VEHICLE_COLOR", "String", "text", "COLOR", "", "", "", "", "", "", "", "N","Y","Y");
	
		
		g.setObj(o);
		ga[0] =g;
		
		return ga;
	}

	
	
}
