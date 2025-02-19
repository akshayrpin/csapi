package csapi.impl.activities;

import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.common.Choices;
import csapi.utils.CsTools;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class ActivitiesFields {

	public static final String TABLE_TYPE = "horizontal";
	public static final int CACHE_INTERVAL = FileUtil.INTERVAL_ONCE_PER_DAY;

	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("activities");
		r.setType("activities");
		r.setGroupid("activities");
		ObjVO[] o = new ObjVO[16];

		ObjVO vo = new ObjVO();

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("APPLIED_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("APPLIED_DATE");
		vo.setLabel("APPLIED DATE");
		o[0] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("VALUATION_CALCULATED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_CALCULATED");
		vo.setLabel("VALUATION CALCULATED");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("START_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("START_DATE");
		vo.setLabel("START DATE");
		o[2] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("VALUATION_DECLARED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_DECLARED");
		vo.setLabel("VALUATION DECLARED");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ISSUED_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("ISSUED_DATE");
		vo.setLabel("ISSUED DATE");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("PLAN_CHK_REQ");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("PLAN_CHK_REQ");
		vo.setLabel("PLAN CHECK REQUIRED");
		vo.setChoices(CsTools.yesNoNone(" "));
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("APPLICATION_EXP_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("APPLICATION_EXP_DATE");
		vo.setLabel("APPLICATION EXP DATE");
		o[6] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("INHERIT");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("INHERIT");
		vo.setLabel("INHERIT");
		vo.setChoices(CsTools.yesNoNone(" "));
		o[7] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("EXP_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("EXP_DATE");
		vo.setLabel("PERMIT EXPIRATION DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("SENSITIVE");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("SENSITIVE");
		vo.setLabel("SENSITIVE");
		vo.setChoices(CsTools.yesNoNone(" "));
		o[9] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ACTIVITY_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("ACTIVITY_ID");
		vo.setLabel("ACTIVITY_ID");
		o[10] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("LKUP_ACT_STATUS_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("LKUP_ACT_STATUS_ID");
		vo.setLabel("STATUS");
		o[11] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("SEND_EMAIL");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("SEND_EMAIL");
		o[12] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("NOTIFY_TYPES");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("NOTIFY_TYPES");
		vo.setLabel("NOTIFY_TYPES");
		o[13] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("COMMENT");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("COMMENT");
		vo.setLabel("COMMENT");
		o[14] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("FINAL_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("FINAL_DATE");
		vo.setLabel("FINAL DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[15] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("activities");
		r.setType("activities");
		r.setGroupid("activities");
		r.setDisplay(TABLE_TYPE);
		r.setAddable(false);
		r.setMultieditable(true);
		r.setEditable(true);
		r.setUpdate(true);
		r.setDeletable(false);
		r.setDisplayempty(true);
		r.setFields(new String[] {"NUMBER","TYPE", "STATUS","FEE AMOUNT","FEE PAID","BALANCE DUE", "UPDATED"});
		r.setOptions(new String[] {"Active","Inactive","All"});
		//	r.setIndex(new String[] {"NOTE","DATE"});
		ObjVO[] o = new ObjVO[7];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ACT_NBR");
		vo.setType("type");
		vo.setItype("String");
		vo.setField("ACT_NBR");
		vo.setLabel("NUMBER");
		vo.setSummarytype("activity");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TYPE");
		vo.setType("type");
		vo.setItype("text");
		vo.setField("TYPE");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("N");
		vo.setSummarytype("activity");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("STATUS");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("STATUS");
		vo.setLabel("STATUS");
		vo.setEditable("N");
		vo.setAddable("N");
		vo.setSummarytype("activity");
		o[2] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("FEE_AMOUNT");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("FEE_AMOUNT");
		vo.setLabel("FEE AMOUNT");
		vo.setEditable("N");
		vo.setAddable("N");
		vo.setSummarytype("activity");
		o[3] = vo;
		
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("FEE_PAID");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("FEE_PAID");
		vo.setLabel("FEE PAID");
		vo.setEditable("N");
		vo.setAddable("N");
		vo.setSummarytype("activity");
		o[4] = vo;
		
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("BALANCE_DUE");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("BALANCE_DUE");
		vo.setLabel("BALANCE DUE");
		vo.setEditable("N");
		vo.setAddable("N");
		vo.setSummarytype("activity");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("UPDATED_BY");
		vo.setType("String");
		vo.setItype("user");
		vo.setField("UPDATED_BY");
		vo.setTextfield("UPDATED");
		vo.setLabel("UPDATED");
		
		vo.setEditable("N");
		vo.setAddable("N");
		vo.setSummarytype("activity");
		o[6] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO entitySummary() {
		ObjGroupVO vo = publicSummary();
		vo.setOptions(new String[] {"Active","Inactive","All"});
		return vo;
	}

	public static ObjGroupVO publicSummary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("activities");
		r.setType("activities");
		r.setGroupid("activities");
		r.setLabel("Activities/Permits");
		r.setDisplay(TABLE_TYPE);
		r.setAddable(false);
		r.setEditable(false);
		r.setDeletable(false);
		r.setDisplayempty(true);
		r.setFields(new String[] {"PROJECT","ACTIVITY","TYPE", "BUILDING","STATUS","ISSUED"});
//		r.setOptions(new String[] {"Active","Inactive","All"});
		//	r.setIndex(new String[] {"NOTE","DATE"});
		ObjVO[] o = new ObjVO[7];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("BUILDING");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("BUILDING");
		vo.setLabel("BUILDING");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("PROJECT_NBR");
		vo.setType("short");
		vo.setItype("String");
		vo.setField("PROJECT_NBR");
		vo.setLabel("PROJECT");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ACT_NBR");
		vo.setType("type");
		vo.setItype("String");
		vo.setField("ACT_NBR");
		vo.setLabel("ACTIVITY");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TYPE");
		vo.setType("type");
		vo.setItype("text");
		vo.setField("TYPE");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("N");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("STATUS");
		vo.setType("status");
		vo.setItype("status");
		vo.setField("STATUS");
		vo.setLabel("STATUS");
		vo.setEditable("N");
		vo.setAddable("N");
		o[4] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("DESCRIPTION");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("DESCRIPTION");
		vo.setLabel("DESCRIPTION");
		vo.setEditable("N");
		vo.setAddable("N");
		o[5] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ISSUED_DATE");
		vo.setType("date");
		vo.setItype("date");
		vo.setField("ISSUED_DATE");
		vo.setLabel("ISSUED");
		vo.setEditable("N");
		vo.setAddable("N");
		o[6] = vo;
		
		r.setObj(o);
		return r;
	}

	public static ObjGroupVO info() {
		return summary();
	}
	
	public static ObjGroupVO list() {
		return summary();
	}
	


}




