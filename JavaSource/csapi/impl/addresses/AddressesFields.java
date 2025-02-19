package csapi.impl.addresses;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class AddressesFields {

	public static final String TABLE_TYPE = "horizontal";

	

	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("addresses");
		r.setType("addresses");
		r.setGroupid("addresses");
		r.setEditable(false);
		r.setAddable(false);
		r.setDisplay("hz");
	
		r.setUpdate(false);
		r.setDeletable(false);
		r.setFields(new String[] {"ADDRESS","TYPE", "APN", "LSO_ID"});
		r.setIndex(new String[] {"ADDRESS","TYPE"});
		ObjVO[] o = new ObjVO[4];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ADDRESS");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("ADDRESS");
		vo.setLabel("ADDRESS");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("N");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TYPE");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("TYPE");
		vo.setRequired("N");
		
		vo.setEditable("N");
		vo.setAddable("N");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("APN");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("APN");
		vo.setLabel("APN");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("N");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("LSO_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("LSO_ID");
		vo.setTextfield("LSO_ID");
		vo.setLabel("LSO_ID");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("N");
		o[3] = vo;

		

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO ext() {
		return summary();
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




