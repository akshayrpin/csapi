package csapi.impl.tasks;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class TasksFields {

	public static final String TABLE_TYPE = "horizontal";

	

	public static ObjGroupVO summary() {
		ObjGroupVO g = new ObjGroupVO();
		g.setGroupid("tasks");
		g.setGroup("tasks");
		g.setGroupid("tasks");
		g.setAddable(false);
		g.setEditable(false);
		g.setDeletable(false);
		g.setDisplayempty(true);
		g.setFields(new String[] {"TASK","DESCRIPTION","COMPLETE","RUN DATE"});
		g.setIndex(new String[] {"TASK","RUN DATE"});
		//g.setOptions(new String[] {"10","50","All"});
		//g.setHistory(true);
		
		ObjVO[] o = new ObjVO[4];
		
		ObjVO vo = new ObjVO();

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("TASK");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("TASK");
		vo.setLabel("TASK");
		vo.setType("short");
		o[0] = vo;
		
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("DESCRIPTION");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("DESCRIPTION");
		vo.setLabel("DESCRIPTION");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("COMPLETE");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("COMPLETE");
		vo.setLabel("COMPLETE");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("DATE");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("DATE");
		vo.setLabel("RUN DATE");
		o[3] = vo;

		

		g.setObj(o);
		
		return g;
	}
	
}
