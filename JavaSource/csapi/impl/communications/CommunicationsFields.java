package csapi.impl.communications;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class CommunicationsFields {

	public static final String TABLE_TYPE = "horizontal";

	// title for detail group
	public static final String TYPE_DETAIL_TITLE = "NOTIFY";

	// name of main type table (this is a requirement for tables such as activity where the main table is "activity" but connected tables use "act"
	public static final String MAIN_TABLE_REF = "NOTIFY";

	// reference to other tables
	public static final String TABLE_REF = "NOTIFY";

	// id field used to join other tables
	public static final String FIELD_ID_REF = "NOTIFY_ID";

	public static ObjGroupVO summary() {
		ObjGroupVO g = new ObjGroupVO();
		g.setGroupid("communications");
		g.setGroup("communications");
		g.setGroupid("communications");
		g.setAddable(false);
		g.setEditable(false);
		g.setDeletable(false);
		g.setDisplayempty(true);
		g.setFields(new String[] {"RECIPIENT","SUBJECT","SENT DATE", "SENT TIME", "SENT BY", "VIEW"});
		g.setIndex(new String[] {"RECIPIENT","SENT DATE"});
		g.setOptions(new String[] {"10","50","All"});
		g.setHistory(true);
		
		ObjVO[] o = new ObjVO[6];
		
		ObjVO vo = new ObjVO();

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("RECIPIENT");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("RECIPIENT");
		vo.setLabel("RECIPIENT");
		vo.setType("short");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("SUBJECT");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("SUBJECT");
		vo.setLabel("SUBJECT");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("DATE");
		vo.setType("date");
		vo.setItype("date");
		vo.setField("DATE");
		vo.setLabel("SENT DATE");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("TIME");
		vo.setType("time");
		vo.setItype("time");
		vo.setField("TIME");
		vo.setLabel("SENT TIME");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("SENT_BY");
		vo.setType("short");
		vo.setItype("String");
		vo.setField("SENT_BY");
		vo.setLabel("SENT BY");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("VIEW_CONTENT");
		vo.setType("notifylink");
		vo.setItype("email");
		vo.setField("VIEW_CONTENT");
		vo.setLabel("VIEW");
		o[5] = vo;

		g.setObj(o);
		
		return g;
	}
	
}
