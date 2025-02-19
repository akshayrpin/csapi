package csapi.impl.gis;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class GisFields {

	public static final String TABLE_TYPE = "horizontal";
	public static final String FIELD_ID_REF = "ID";
	
	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("gis");
		r.setType("gis");
		r.setGroupid("gis");
		r.setLabel("map");
		r.setAddable(false);
		r.setDeletable(false);
		r.setFields(new String[] {"MAP"});
		r.setIndex(new String[] {"MAP"});
		r.setDisplay("hz");
		
		r.setEditable(false);
		
	
		ObjVO[] o = new ObjVO[1];


		ObjVO s = new ObjVO();
		s.setId(-1);
	
		s.setFieldid("TYPE");
		s.setType("String");
		s.setItype("String");
		s.setField("TYPE");
		s.setLabel("MAP");
		s.setEditable("N");
		s.setLink("gislink");
		o[0] = s;
		
		
		
		
		
		
	
		r.setObj(o);

		return r;
	}
	
	public static ObjGroupVO summary() {
		return details();
	}
	
	public static ObjGroupVO info() {
		return details();
	}
}
