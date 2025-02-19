package csapi.impl.lso;

import java.util.HashMap;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class LsoFields {

	public static String[] summaryModules = {"lso", "divisions", "custom", "appointment", "people", "resolution", "notes", "attachments", "archive", "activities"};
	public static String[] infoModules = {"holds","reviews","apn","sitedata","addresses","stopwork","gis"};

	public static ObjGroupVO info() {
		return details();
	}
	
	public static ObjGroupVO summary() {
		return details();
	}
	
	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("lso");
		r.setType("lso");
		r.setGroupid("lso");
		ObjVO[] o = new ObjVO[11];

		ObjVO s = new ObjVO();

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("DESCRIPTION");
		s.setType("String");
		s.setItype("text");
		s.setField("DESCRIPTION");
		s.setLabel("DESCRIPTION");
		s.setEditable("Y");
		s.setAddable("Y");
		o[0] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ID");
		s.setType("String");
		s.setItype("text");
		s.setField("ID");
		s.setLabel("LSO ID");
		s.setRequired("Y");
		s.setEditable("N");
		s.setAddable("N");
		o[1] = s;
		
		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ADDRESS");
		s.setType("String");
		s.setItype("text");
		s.setField("ADDRESS");
		s.setLabel("ADDRESS");
		s.setRequired("Y");
		s.setEditable("N");
		s.setAddable("Y");
		o[2] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("CREATED_DATE");
		s.setType("datetime");
		s.setItype("datetime");
		s.setField("CREATED_DATE");
		s.setLabel("CREATED DATE");
		s.setRequired("Y");
		s.setEditable("N");
		s.setAddable("N");
		s.setShowpublic(false);
		o[3] = s;

		o[4] = unit();

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("UPDATED_DATE");
		s.setType("datetime");
		s.setItype("datetime");
		s.setField("UPDATED_DATE");
		s.setLabel("UPDATED DATE");
		s.setRequired("Y");
		s.setEditable("N");
		s.setAddable("N");
		s.setShowpublic(false);
		o[5] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("CITY");
		s.setType("String");
		s.setItype("text");
		s.setField("CITY");
		s.setLabel("CITY");
		s.setRequired("Y");
		s.setEditable("N");
		s.setAddable("Y");
		o[6] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("PRIMARY");
		s.setType("boolean");
		s.setItype("boolean");
		s.setField("PRMARY");
		s.setLabel("PRIMARY");
		s.setRequired("Y");
		s.setEditable("N");
		s.setAddable("N");
		o[7] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("STATE");
		s.setType("String");
		s.setItype("text");
		s.setField("STATE");
		s.setLabel("STATE");
		s.setRequired("Y");
		s.setEditable("N");
		s.setAddable("Y");
		o[8] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ACTIVE");
		s.setType("boolean");
		s.setItype("boolean");
		s.setField("ACTIVE");
		s.setLabel("ACTIVE");
		s.setRequired("Y");
		s.setEditable("N");
		s.setAddable("N");
		o[9] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ZIP");
		s.setType("String");
		s.setItype("text");
		s.setField("ZIP");
		s.setLabel("ZIP");
		s.setRequired("Y");
		s.setEditable("N");
		s.setAddable("Y");
		o[10] = s;


		r.setObj(o);
		return r;
	}
	
	public static ObjVO unit() {
		ObjVO s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("UNIT");
		s.setType("String");
		s.setItype("text");
		s.setField("UNIT");
		s.setLabel("UNIT");
		s.setEditable("Y");
		s.setAddable("Y");
		HashMap<String, String> c = new HashMap<String, String>();
		c.put("LKUP_LSO_TYPE_ID", "3");
		//s.setCondtions(c);
		return s;
		
	}

}
