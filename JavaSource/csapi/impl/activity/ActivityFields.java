package csapi.impl.activity;

import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import csapi.utils.CsTools;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class ActivityFields {

	// title for detail group
	public static final String TYPE_DETAIL_TITLE = "ACTIVITY";

	// name of main type table (this is a requirement for tables such as activity where the main table is "activity" but connected tables use "act"
	public static final String MAIN_TABLE_REF = "ACTIVITY";

	// reference to other tables
	public static final String TABLE_REF = "ACT";

	// id field used to join other tables
	public static final String FIELD_ID_REF = "ACTIVITY_ID";

	// field containing official number
	public static final String FIELD_REFERENCE_REF = "ACT_NBR";

	public static String[] summaryModules = {"history","activity","team","people","appointment","resolution","notes","library","attachments", "custom", "finance","deposit"};
	public static String[] infoModules = {"stopwork","holds","reviews"};

	public static ObjGroupVO summary() {
		ObjGroupVO g = new ObjGroupVO();
		g.setGroupid("activity");
		g.setGroup(TYPE_DETAIL_TITLE);
		g.setType("activity");
		g.setHistory(true);
		
		ObjVO[] o = new ObjVO[23];
		
		ObjVO vo = new ObjVO();

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("LKUP_ACT_TYPE_ID");
		vo.setType("String");
		vo.setItype("type");
		vo.setField("LKUP_ACT_TYPE_ID");
		vo.setLabel("TYPE");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setTextfield("LKUP_ACT_TYPE_ID_TEXT");
		vo.setText("");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setLkup("typedescriptions");
		vo.setUpdatevalues("Y");
//		vo.setChoices(LkupAgent.types("activity", -1));
//		vo.setJson(Choices.choiceUrl("LKUP_ACT_TYPE", "DESCRIPTION", "DESCRIPTION"));
		o[0] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("LKUP_ACT_STATUS_ID");
		vo.setType("String");
		vo.setItype("status");
		vo.setField("LKUP_ACT_STATUS_ID");
		vo.setLabel("STATUS");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setTextfield("STATUS");
		vo.setText("");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setLkup("status");
//		vo.setChoices(LkupAgent.status("activity", -1));
//		vo.setJson(Choices.choiceUrl("LKUP_ACT_STATUS", "STATUS", "STATUS"));
		o[1] = vo;
		//o[1] = FieldObjects.toObject(12, 3, "STATUS", "String", "text", "STATUS", "STATUS", "", "", "", "", "", "", "Y",Choices.getStatus("LKUP_ACT_STATUS", "DESCRIPTION", "","DESCRIPTION"),"Y","Y","Y","N","");				

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("DESCRIPTION");
		vo.setType("largetext");
		vo.setItype("textarea");
		vo.setField("DESCRIPTION");
		vo.setLabel("DESCRIPTION");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ACT_NBR");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("ACT_NBR");
		vo.setLabel("ACTIVITY NUMBER");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setSystemGenerated("Y");
		o[3] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("VALUATION_DECLARED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_DECLARED");
		vo.setLabel("VALUATION DECLARED");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[4] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("PLAN_CHK_REQ");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("PLAN_CHK_REQ");
		vo.setLabel("PLAN CHECK REQUIRED");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[5] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("VALUATION_CALCULATED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_CALCULATED");
		vo.setLabel("VALUATION CALCULATED");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setShowpublic(false);
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("START_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("START_DATE");
		vo.setLabel("START DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[7] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("APPLIED_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("APPLIED_DATE");
		vo.setLabel("APPLIED DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[8] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ISSUED_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("ISSUED_DATE");
		vo.setLabel("ISSUED DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[9] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("APPLICATION_EXP_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("APPLICATION_EXP_DATE");
		vo.setLabel("APPLICATION EXP DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[10] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("FINAL_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("FINAL_DATE");
		vo.setLabel("FINAL DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[11] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("EXP_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("EXP_DATE");
		vo.setLabel("PERMIT EXPIRATION DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[12] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("INHERIT");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("INHERIT");
		vo.setLabel("INHERIT");
		vo.setRequired("N");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setChoices(CsTools.yesNoNone(" "));
		vo.setShowpublic(false);
		o[13] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ONLINE");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("ONLINE");
		vo.setLabel("ONLINE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setShowpublic(false);
		o[14] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("SENSITIVE");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("SENSITIVE");
		vo.setLabel("SENSITIVE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setShowpublic(false);
		o[15] = vo;
		
		
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("PROJECT_ID");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("PROJECT_ID");
		vo.setLabel("PROJECT ID");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setDisplay("N");
		vo.setSystemGenerated("Y");
		o[16] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("CREATED");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("CREATED");
		vo.setLabel("CREATED BY");
		vo.setEditable("N");
		vo.setAddable("N");
		vo.setShowpublic(false);
		o[17] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATED");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("UPDATED");
		vo.setLabel("UPDATED BY");
		vo.setEditable("N");
		vo.setAddable("N");
		vo.setShowpublic(false);
		o[18] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("CREATED_DATE");
		vo.setType("DATETIME");
		vo.setItype("hidden");
		vo.setField("CREATED_DATE");
		vo.setLabel("CREATED DATE");
		vo.setEditable("N");
		vo.setAddable("N");
		vo.setShowpublic(false);
		o[19] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATED_DATE");
		vo.setType("DATETIME");
		vo.setItype("hidden");
		vo.setField("UPDATED_DATE");
		vo.setLabel("UPDATED DATE");
		vo.setEditable("N");
		vo.setAddable("N");
		vo.setShowpublic(false);
		o[20] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("QTY");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("QTY");
		vo.setLabel("QTY");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setDisplay("N");
		vo.setDefaultvalue("1");
		vo.setShowpublic(false);
		o[21] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(23);
		vo.setFieldid("PROJECT_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("PROJECT_ID");
		vo.setLabel("CS REF NO");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("N");
		vo.setDisplay("Y");
		o[22] = vo;
		
		g.setObj(o);
		
		return g;
	}

	public static ObjGroupVO list() {
		return details();
	}

	public static ObjGroupVO info() {
		return details();
	}

	public static ObjGroupVO history() {
		return id();
	}

	public static ObjGroupVO id() {
		ObjGroupVO g = new ObjGroupVO();
		g.setGroupid("activity");
		g.setType("activity");
		g.setHistory(true);
		
		ObjVO[] o = new ObjVO[19];
		
		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("TYPE");
		vo.setType("String");
		vo.setItype("type");
		vo.setField("TYPE");
		vo.setLabel("TYPE");
		o[0] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("STATUS");
		vo.setType("String");
		vo.setItype("status");
		vo.setField("STATUS");
		vo.setLabel("STATUS");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("DESCRIPTION");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("DESCRIPTION");
		vo.setLabel("DESCRIPTION");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("VALUATION_DECLARED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_DECLARED");
		vo.setLabel("VALUATION DECLARED");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[3] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("PLAN_CHK_REQ");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("PLAN_CHK_REQ");
		vo.setLabel("PLAN CHECK REQUIRED");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[4] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("VALUATION_CALCULATED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_CALCULATED");
		vo.setLabel("CITY VALUATION");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("START_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("START_DATE");
		vo.setLabel("START DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[6] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("APPLIED_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("APPLIED_DATE");
		vo.setLabel("APPLIED DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[7] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ISSUED_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("ISSUED_DATE");
		vo.setLabel("ISSUED DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("EXP_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("EXP_DATE");
		vo.setLabel("APPLICATION EXP DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[9] = vo;
		

		
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("EXP_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("EXP_DATE");
		vo.setLabel("EXPIRATION DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[10] = vo;
		
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("INHERIT");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("INHERIT");
		vo.setLabel("INHERIT");
		vo.setRequired("N");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setChoices(CsTools.yesNoNone(" "));
		o[11] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ONLINE");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("ONLINE");
		vo.setLabel("ONLINE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[12] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("SENSITIVE");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("SENSITIVE");
		vo.setLabel("SENSITIVE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[13] = vo;
		
		
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ACT_NBR");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("ACT_NBR");
		vo.setLabel("ACTIVITY NUMBER");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setSystemGenerated("Y");
		o[14] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATED");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("UPDATED");
		vo.setLabel("UPDATED BY");
		vo.setEditable("N");
		vo.setAddable("N");
		o[15] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATED_DATE");
		vo.setType("DATETIME");
		vo.setItype("hidden");
		vo.setField("UPDATED_DATE");
		vo.setLabel("UPDATED DATE");
		vo.setEditable("N");
		vo.setAddable("N");
		o[16] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("PROJECT_ID");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("PROJECT_ID");
		vo.setLabel("PROJECT ID");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setSystemGenerated("Y");
		o[17] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("CODE_ENFORCEMENT");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("CODE_ENFORCEMENT");
		vo.setLabel("CODE ENFORCEMENT");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[18] = vo;
		
		g.setObj(o);
		
		return g;
	}

	public static ObjGroupVO details() {
		ObjGroupVO g = new ObjGroupVO();
		g.setGroupid("activity");
		g.setGroup(TYPE_DETAIL_TITLE);
		g.setType("activity");
		g.setHistory(true);
		
		ObjVO[] o = new ObjVO[27];
		
		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("LKUP_ACT_TYPE_ID");
		vo.setType("String");
		vo.setItype("type");
		vo.setField("LKUP_ACT_TYPE_ID");
		vo.setLabel("TYPE");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setTextfield("LKUP_ACT_TYPE_ID_TEXT");
		vo.setText("");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setLkup("typedescriptions");
		vo.setUpdatevalues("Y");
		vo.setQty("Y");
//		vo.setChoices(LkupAgent.types("activity", -1));
//		vo.setJson(Choices.choiceUrl("LKUP_ACT_TYPE", "DESCRIPTION", "DESCRIPTION"));
		o[0] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("LKUP_ACT_STATUS_ID");
		vo.setType("String");
		vo.setItype("status");
		vo.setField("LKUP_ACT_STATUS_ID");
		vo.setLabel("STATUS");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setTextfield("STATUS");
		vo.setText("");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setLkup("status");
//		vo.setChoices(LkupAgent.status("activity", -1));
//		vo.setJson(Choices.choiceUrl("LKUP_ACT_STATUS", "STATUS", "STATUS"));
		o[1] = vo;
		//o[1] = FieldObjects.toObject(12, 3, "STATUS", "String", "text", "STATUS", "STATUS", "", "", "", "", "", "", "Y",Choices.getStatus("LKUP_ACT_STATUS", "DESCRIPTION", "","DESCRIPTION"),"Y","Y","Y","N","");				

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("DESCRIPTION");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("DESCRIPTION");
		vo.setLabel("DESCRIPTION");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("VALUATION_DECLARED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_DECLARED");
		vo.setLabel("VALUATION DECLARED");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[3] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("PLAN_CHK_REQ");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("PLAN_CHK_REQ");
		vo.setLabel("PLAN CHECK REQUIRED");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[4] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("VALUATION_CALCULATED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_CALCULATED");
		vo.setLabel("CITY VALUATION");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("START_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("START_DATE");
		vo.setLabel("START DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setMultivalueindex("LKUP_ACT_TYPE_ID");
		o[6] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("APPLIED_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("APPLIED_DATE");
		vo.setLabel("APPLIED DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setMultivalueindex("LKUP_ACT_TYPE_ID");
		o[7] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ISSUED_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("ISSUED_DATE");
		vo.setLabel("ISSUED DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setMultivalueindex("LKUP_ACT_TYPE_ID");
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("APPLICATION_EXP_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("APPLICATION_EXP_DATE");
		vo.setLabel("APPLICATION EXP DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setMultivalueindex("LKUP_ACT_TYPE_ID");
		o[9] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("INHERIT");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("INHERIT");
		vo.setLabel("INHERIT");
		vo.setRequired("N");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setChoices(CsTools.yesNoNone(" "));
		o[10] = vo;
		
		
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("EXP_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("EXP_DATE");
		vo.setLabel("PERMIT EXPIRATION DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setMultivalueindex("LKUP_ACT_TYPE_ID");
		o[11] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("FINAL_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("FINAL_DATE");
		vo.setLabel("FINAL DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setMultivalueindex("LKUP_ACT_TYPE_ID");
		o[12] = vo;
		
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ONLINE");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("ONLINE");
		vo.setLabel("ONLINE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[13] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("SENSITIVE");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("SENSITIVE");
		vo.setLabel("SENSITIVE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[14] = vo;
		
		
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ACT_NBR");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("ACT_NBR");
		vo.setLabel("ACTIVITY NUMBER");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setSystemGenerated("Y");
		o[15] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("PROJECT_ID");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("PROJECT_ID");
		vo.setLabel("PROJECT ID");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setSystemGenerated("Y");
		o[16] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("CREATED");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("CREATED");
		vo.setLabel("CREATED BY");
		vo.setEditable("N");
		vo.setAddable("N");
		o[17] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATED");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("UPDATED");
		vo.setLabel("UPDATED BY");
		vo.setEditable("N");
		vo.setAddable("N");
		o[18] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("CREATED_DATE");
		vo.setType("DATETIME");
		vo.setItype("hidden");
		vo.setField("CREATED_DATE");
		vo.setLabel("CREATED DATE");
		vo.setEditable("N");
		vo.setAddable("N");
		o[19] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATED_DATE");
		vo.setType("DATETIME");
		vo.setItype("hidden");
		vo.setField("UPDATED_DATE");
		vo.setLabel("UPDATED DATE");
		vo.setEditable("N");
		vo.setAddable("N");
		o[20] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("QTY");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("QTY");
		vo.setLabel("QTY");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setDefaultvalue("1");
		o[21] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("SEND_EMAIL");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("SEND_EMAIL");
		o[22] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("NOTIFY");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("NOTIFY");
		vo.setLabel("NOTIFY");
		o[23] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("COMMENT");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("COMMENT");
		vo.setLabel("COMMENT");
		o[24] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATE_FEES");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("UPDATE_FEES");
		vo.setLabel("UPDATE FEES");
		o[25] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("CODE_ENFORCEMENT");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("CODE_ENFORCEMENT");
		vo.setLabel("CODE ENFORCEMENT");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[26] = vo;
		
		g.setObj(o);
		
		return g;
	}

	public static ObjGroupVO my() {
		ObjGroupVO g = new ObjGroupVO();
		g.setGroup("activity");
		g.setType("activity");
		g.setGroupid("activity");
		g.setFields(new String[] {"ACT_NBR","TYPE"});
		g.setIndex(new String[] {"ACT_NBR","TYPE"});
		
		ObjVO[] o = new ObjVO[11];
		
		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ACT_NBR");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("ACT_NBR");
		vo.setLabel("Activity Number");
		o[0] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("TYPE");
		vo.setType("Type");
		vo.setItype("Type");
		vo.setField("TYPE");
		vo.setLabel("Type");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("STATUS");
		vo.setType("Text");
		vo.setItype("Text");
		vo.setField("STATUS");
		vo.setLabel("Status");
		o[2] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("DESCRIPTION");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("DESCRIPTION");
		vo.setLabel("DESCRIPTION");
		o[3] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("START_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("START_DATE");
		vo.setLabel("Start Date");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ISSUED_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("ISSUED_DATE");
		vo.setLabel("Issued Date");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("EXP_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("EXP_DATE");
		vo.setLabel("Exp Date");
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("BALANCE");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("BALANCE");
		vo.setLabel("BALANCE");
		o[7] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("ID");
		vo.setLabel("ID");
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ONLINE_PRINT");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("ONLINE_PRINT");
		vo.setLabel("ONLINE_PRINT");
		o[9] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("APPLIED_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("APPLIED_DATE");
		vo.setLabel("Applied Date");
		o[10] = vo;

		g.setObj(o);
		
		return g;
	}
	
	
	
	public static ObjGroupVO prefill(MapSet m) {
		ObjGroupVO g = new ObjGroupVO();
		g.setGroupid("activity");
		g.setGroup(TYPE_DETAIL_TITLE);
		g.setType("activity");
		g.setHistory(true);
		
		ObjVO[] o = new ObjVO[26];
		
		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("LKUP_ACT_TYPE_ID");
		vo.setType("String");
		vo.setItype("type");
		vo.setField("LKUP_ACT_TYPE_ID");
		vo.setLabel("TYPE");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setTextfield("LKUP_ACT_TYPE_ID_TEXT");
		vo.setText("");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setLkup("typedescriptions");
		vo.setUpdatevalues("Y");
		vo.setQty("Y");
		
		if(Operator.hasValue(m.getString("LKUP_ACT_TYPE_ID"))){
			vo.setValue(m.getString("LKUP_ACT_TYPE_ID"));
		}
		Logger.highlight(vo.getValue());
		
//		vo.setChoices(LkupAgent.types("activity", -1));
//		vo.setJson(Choices.choiceUrl("LKUP_ACT_TYPE", "DESCRIPTION", "DESCRIPTION"));
		o[0] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("LKUP_ACT_STATUS_ID");
		vo.setType("String");
		vo.setItype("status");
		vo.setField("LKUP_ACT_STATUS_ID");
		vo.setLabel("STATUS");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setTextfield("STATUS");
		vo.setText("");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setLkup("status");
//		vo.setChoices(LkupAgent.status("activity", -1));
//		vo.setJson(Choices.choiceUrl("LKUP_ACT_STATUS", "STATUS", "STATUS"));
		
		if(Operator.hasValue(m.getString("LKUP_ACT_STATUS_ID"))){
			vo.setValue(m.getString("LKUP_ACT_STATUS_ID"));
		}
		
		o[1] = vo;
		//o[1] = FieldObjects.toObject(12, 3, "STATUS", "String", "text", "STATUS", "STATUS", "", "", "", "", "", "", "Y",Choices.getStatus("LKUP_ACT_STATUS", "DESCRIPTION", "","DESCRIPTION"),"Y","Y","Y","N","");				

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("DESCRIPTION");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("DESCRIPTION");
		vo.setLabel("DESCRIPTION");
		if(Operator.hasValue(m.getString("DESCRIPTION"))){
			vo.setValue(m.getString("DESCRIPTION"));
		}
		
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("VALUATION_DECLARED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_DECLARED");
		vo.setLabel("VALUATION DECLARED");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[3] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("PLAN_CHK_REQ");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("PLAN_CHK_REQ");
		vo.setLabel("PLAN CHECK REQUIRED");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[4] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("VALUATION_CALCULATED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_CALCULATED");
		vo.setLabel("CITY VALUATION");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("START_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("START_DATE");
		vo.setLabel("START DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setMultivalueindex("LKUP_ACT_TYPE_ID");
		
		if(Operator.hasValue(m.getString("START_DATE"))){
			vo.setValue(m.getString("START_DATE"));
		}
		
		o[6] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("APPLIED_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("APPLIED_DATE");
		vo.setLabel("APPLIED DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setMultivalueindex("LKUP_ACT_TYPE_ID");
		
		if(Operator.hasValue(m.getString("APPLIED_DATE"))){
			vo.setValue(m.getString("APPLIED_DATE"));
		}
		o[7] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ISSUED_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("ISSUED_DATE");
		vo.setLabel("ISSUED DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setMultivalueindex("LKUP_ACT_TYPE_ID");
		
		if(Operator.hasValue(m.getString("ISSUED_DATE"))){
			vo.setValue(m.getString("ISSUED_DATE"));
		}
		
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("APPLICATION_EXP_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("APPLICATION_EXP_DATE");
		vo.setLabel("APPLICATION EXP DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setMultivalueindex("LKUP_ACT_TYPE_ID");
		
		if(Operator.hasValue(m.getString("APPLICATION_EXP_DATE"))){
			vo.setValue(m.getString("APPLICATION_EXP_DATE"));
		}
		o[9] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("INHERIT");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("INHERIT");
		vo.setLabel("INHERIT");
		vo.setRequired("N");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setChoices(CsTools.yesNoNone(" "));
		
		if(Operator.hasValue(m.getString("INHERIT"))){
			vo.setValue(m.getString("INHERIT"));
		}else {
			vo.setValue("N");
		}
		o[10] = vo;
		
		
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("EXP_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("EXP_DATE");
		vo.setLabel("PERMIT EXPIRATION DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setMultivalueindex("LKUP_ACT_TYPE_ID");
		
		if(Operator.hasValue(m.getString("EXP_DATE"))){
			vo.setValue(m.getString("EXP_DATE"));
		}
		
		o[11] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("FINAL_DATE");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("FINAL_DATE");
		vo.setLabel("FINAL DATE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setMultivalueindex("LKUP_ACT_TYPE_ID");
		o[12] = vo;
		
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ONLINE");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("ONLINE");
		vo.setLabel("ONLINE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[13] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("SENSITIVE");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("SENSITIVE");
		vo.setLabel("SENSITIVE");
		vo.setEditable("Y");
		vo.setAddable("Y");
		
		if(Operator.hasValue(m.getString("SENSITIVE"))){
			vo.setValue(m.getString("SENSITIVE"));
		}else {
			vo.setValue("N");
		}
		o[14] = vo;
		
		
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("ACT_NBR");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("ACT_NBR");
		vo.setLabel("ACTIVITY NUMBER");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setSystemGenerated("Y");
		o[15] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("PROJECT_ID");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("PROJECT_ID");
		vo.setLabel("PROJECT ID");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setSystemGenerated("Y");
		o[16] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("CREATED");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("CREATED");
		vo.setLabel("CREATED BY");
		vo.setEditable("N");
		vo.setAddable("N");
		o[17] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATED");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("UPDATED");
		vo.setLabel("UPDATED BY");
		vo.setEditable("N");
		vo.setAddable("N");
		o[18] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("CREATED_DATE");
		vo.setType("DATETIME");
		vo.setItype("hidden");
		vo.setField("CREATED_DATE");
		vo.setLabel("CREATED DATE");
		vo.setEditable("N");
		vo.setAddable("N");
		o[19] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATED_DATE");
		vo.setType("DATETIME");
		vo.setItype("hidden");
		vo.setField("UPDATED_DATE");
		vo.setLabel("UPDATED DATE");
		vo.setEditable("N");
		vo.setAddable("N");
		o[20] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("QTY");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("QTY");
		vo.setLabel("QTY");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setDefaultvalue("1");
		o[21] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("SEND_EMAIL");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("SEND_EMAIL");
		o[22] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("NOTIFY");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("NOTIFY");
		vo.setLabel("NOTIFY");
		o[23] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("COMMENT");
		vo.setType("hidden");
		vo.setItype("hidden");
		vo.setField("COMMENT");
		vo.setLabel("COMMENT");
		o[24] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATE_FEES");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("UPDATE_FEES");
		vo.setLabel("UPDATE FEES");
		vo.setDefaultvalue("N");
		o[25] = vo;
		
		g.setObj(o);
		
		return g;
	}
}
