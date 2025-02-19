package csapi.impl.move;

import alain.core.utils.FileUtil;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class MoveFields {

	public static final String TABLE_TYPE = "horizontal";
	public static final int CACHE_INTERVAL = FileUtil.INTERVAL_ONCE_PER_DAY;

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("move");
		r.setType("move");
		r.setGroupid("move");
		ObjVO[] o = new ObjVO[7];

		ObjVO vo = new ObjVO();

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("STR_NO");
		vo.setType("integer");
		vo.setItype("integer");
		vo.setField("STR_NO");
		vo.setLabel("STREET NUMBER");
		o[0] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("STR_MOD");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("STR_MOD");
		vo.setLabel("FRACTION");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("STREET");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("STREET");
		vo.setLabel("STREET");
		o[2] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("UNIT");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("UNIT");
		vo.setLabel("UNIT");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("LSO_ID");
		vo.setType("integer");
		vo.setItype("integer");
		vo.setField("LSO_ID");
		vo.setLabel("LSO_ID");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("PROJECT_ID");
		vo.setType("integer");
		vo.setItype("integer");
		vo.setField("PROJECT_ID");
		vo.setLabel("PROJECT_ID");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("PROJECT_NBR");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("PROJECT_NBR");
		vo.setLabel("PROJECT_NBR");
		o[6] = vo;

		r.setObj(o);
		return r;
	}


}




