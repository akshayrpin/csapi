package csapi.impl.library;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class LibraryFields {

	public static final String TABLE_TYPE = "horizontal";
	// title for detail group
	public static final String TYPE_DETAIL_TITLE = "LIBRARY";

	// name of main type table (this is a requirement for tables such as activity where the main table is "activity" but connected tables use "act"
	public static final String MAIN_TABLE_REF = "LIBRARY";

	// reference to other tables
	public static final String TABLE_REF = "LIBRARY";

	// id field used to join other tables
	public static final String FIELD_ID_REF = "LIBRARY_ID";

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("library");
		r.setType("library");
		r.setGroupid("library");
		ObjVO[] o = new ObjVO[9];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("GROUP");
		vo.setType("String");
		vo.setItype("librarygroup");
		vo.setField("GROUP");
		vo.setLabel("GROUP");
		vo.setRequired("Y");
		vo.setAddable("Y");
		vo.setLkup("group");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("LIBRARY_CODE");
		vo.setType("String");
		vo.setItype("librarycode");
		vo.setField("LIBRARY_CODE");
		vo.setLabel("LIBRARY CODE");
		vo.setRequired("N");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TITLE");
		vo.setType("String");
		vo.setItype("librarytitle");
		vo.setField("TITLE");
		vo.setLabel("TITLE");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(10);
		vo.setFieldid("TXT");
		vo.setType("String");
		vo.setItype("librarydescription");
		vo.setField("TXT");
		vo.setLabel("DESCRIPTION");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(30);
		vo.setFieldid("INSPECTABLE");
		vo.setType("Boolean");
		vo.setItype("inspectable");
		vo.setField("INSPECTABLE");
		vo.setLabel("INSPECTABLE");
		o[4] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(30);
		vo.setFieldid("WARNING");
		vo.setType("Boolean");
		vo.setItype("warning");
		vo.setField("WARNING");
		vo.setLabel("WARNING");
		o[5] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(30);
		vo.setFieldid("COMPLETE");
		vo.setType("Boolean");
		vo.setItype("complete");
		vo.setField("COMPLETE");
		vo.setLabel("COMPLETE");
		o[6] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(30);
		vo.setFieldid("REQUIRED");
		vo.setType("Boolean");
		vo.setItype("required");
		vo.setField("REQUIRED");
		vo.setLabel("REQUIRED");
		o[7] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("LIBRARY_ID");
		vo.setType("hidden");
		vo.setItype("libraryid");
		vo.setField("LIBRARY_ID");
		vo.setLabel("LIBRARY_ID");
		o[8] = vo;
		
		r.setObj(o);
		return r;
	}

	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("library");
		r.setType("library");
		r.setGroupid("library");
		r.setFields(new String[] {"CODE","TITLE","COMPLETE","DATE"});
		r.setIndex(new String[] {"CODE","TITLE","COMPLETE","DATE"});
		ObjVO[] o = new ObjVO[4];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("CREATED_DATE");
		vo.setType("date");
		vo.setItype("date");
		vo.setField("CREATED_DATE");
		vo.setLabel("DATE");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("LIBRARY_CODE");
		vo.setType("type");
		vo.setItype("librarycode");
		vo.setField("LIBRARY_CODE");
		vo.setLabel("CODE");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TITLE");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("TITLE");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("COMPLETE");
		vo.setType("complete");
		vo.setItype("complete");
		vo.setField("COMPLETE");
		o[3] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO info() {
		return summary();
	}
	
}
