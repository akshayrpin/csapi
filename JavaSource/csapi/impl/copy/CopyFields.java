package csapi.impl.copy;

import alain.core.utils.FileUtil;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class CopyFields {

	public static final String TABLE_TYPE = "horizontal";
	public static final int CACHE_INTERVAL = FileUtil.INTERVAL_ONCE_PER_DAY;

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("copy");
		r.setType("copy");
		r.setGroupid("copy");
		ObjVO[] o = new ObjVO[15];

		ObjVO vo = new ObjVO();

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("PROJECT_ID");
		vo.setType("Integer");
		vo.setItype("Integer");
		vo.setField("PROJECT_ID");
		vo.setLabel("PROJECT ID");
		o[0] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("LKUP_MODULES_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("LKUP_MODULES_ID");
		vo.setLabel("LKUP_MODULES_ID");
		o[1] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("PROJECT_NBR");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("PROJECT_NBR");
		vo.setLabel("PROJECT_NUMBER");
		o[2] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ACT_NBR");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("ACT_NBR");
		vo.setLabel("ACT_NUMBER");
		o[3] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ACTIVITY_ID");
		vo.setType("Integer");
		vo.setItype("Integer");
		vo.setField("ACTIVITY_ID");
		vo.setLabel("ACTIVITY ID");
		o[4] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("START_DATE");
		vo.setType("text");
		vo.setItype("date");
		vo.setField("START_DATE");
		vo.setLabel("START DATE");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("APPLIED_DATE");
		vo.setType("text");
		vo.setItype("date");
		vo.setField("APPLIED_DATE");
		vo.setLabel("APPLIED DATE");
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ISSUED_DATE");
		vo.setType("text");
		vo.setItype("date");
		vo.setField("ISSUED_DATE");
		vo.setLabel("ISSUED DATE");
		o[7] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("EXP_DATE");
		vo.setType("text");
		vo.setItype("date");
		vo.setField("EXP_DATE");
		vo.setLabel("EXP DATE");
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("APPLICATION_EXP_DATE");
		vo.setType("text");
		vo.setItype("date");
		vo.setField("APPLICATION_EXP_DATE");
		vo.setLabel("APPLICATION EXP DATE");
		o[9] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("FINAL_DATE");
		vo.setType("text");
		vo.setItype("date");
		vo.setField("FINAL_DATE");
		vo.setLabel("FINAL DATE");
		o[10] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("PERMIT_FEE_DATE");
		vo.setType("text");
		vo.setItype("date");
		vo.setField("PERMIT_FEE_DATE");
		vo.setLabel("PERMIT FEE DATE");
		o[11] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("DAYS_TILL_EXPIRED");
		vo.setType("integer");
		vo.setItype("integer");
		vo.setField("DAYS_TILL_EXPIRED");
		vo.setLabel("DAYS TILL EXPIRED");
		o[12] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("DAYS_TILL_APPLICATION_EXPIRED");
		vo.setType("integer");
		vo.setItype("integer");
		vo.setField("DAYS_TILL_APPLICATION_EXPIRED");
		vo.setLabel("DAYS TILL APPLICATION EXPIRED");
		o[13] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("LKUP_ACT_STATUS_ID");
		vo.setType("integer");
		vo.setItype("integer");
		vo.setField("LKUP_ACT_STATUS_ID");
		vo.setLabel("STATUS");
		o[14] = vo;
		
		r.setObj(o);
		return r;
	}


}




