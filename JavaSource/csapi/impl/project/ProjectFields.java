package csapi.impl.project;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class ProjectFields {

	// title for detail group
	public static final String TYPE_DETAIL_TITLE = "PROJECT";

	// name of main type table (this is a requirement for tables such as activity where the main table is "activity" but connected tables use "act"
	public static final String MAIN_TABLE_REF = "PROJECT";

	// reference to other tables
	public static final String TABLE_REF = "PROJECT";

	// field containing official number
	public static final String FIELD_REFERENCE_REF = "PROJECT_NBR";

	// id field used to join other tables
	public static final String FIELD_ID_REF = "PROJECT_ID";

	public static String[] summaryModules = {"project","appointment","resolution","peoplesummary","team","notes","attachments", "custom","finance","deposit","activities"};
	public static String[] infoModules = {"holds","stopwork","reviews"};
//	public static String[] summaryModules = {"finance"};
//	public static String[] infoModules = new String[0];

	public static ObjGroupVO summary() {
		return details();
	}

	public static ObjGroupVO list() {
		return details();
	}

	public static ObjGroupVO info() {
		return details();
	}

	public static ObjGroupVO history() {
		ObjGroupVO g = new ObjGroupVO();
		g.setGroupid("project");
		g.setGroup(TYPE_DETAIL_TITLE);
		g.setType("project");
		g.setHistory(true);

		ObjVO[] o = new ObjVO[16];

		ObjVO vo = new ObjVO();
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("NAME");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("NAME");
		vo.setLabel("NAME");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("TYPE");
		vo.setType("String");
		vo.setItype("type");
		vo.setField("TYPE");
		vo.setLabel("TYPE");
		o[1] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("DESCRIPTION");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("DESCRIPTION");
		vo.setLabel("DESCRIPTION");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("STATUS");
		vo.setType("String");
		vo.setItype("status");
		vo.setField("STATUS");
		vo.setLabel("STATUS");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("PROJECT_NBR");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("PROJECT_NBR");
		vo.setLabel("PROJECT NBR");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("APPLIED_DT");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("APPLIED_DT");
		vo.setLabel("APPLIED DATE");
		o[5] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("VALUATION_CALCULATED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_CALCULATED");
		vo.setLabel("VALUATION CALCULATED");
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("START_DT");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("START_DT");
		vo.setLabel("START DATE");
		o[7] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("VALUATION_DECLARED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_DECLARED");
		vo.setLabel("VALUATION DECLARED");
		o[8] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("COMPLETION_DT");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("COMPLETION_DT");
		vo.setLabel("COMPLETION DATE");
		o[9] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("CIP_ACCTNO");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("CIP_ACCTNO");
		vo.setLabel("CIP");
		o[10] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("EXPIRED_DT");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("EXPIRED_DT");
		vo.setLabel("EXPIRED DATE");
		o[11] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("PLAN_CHK_REQ");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("PLAN_CHK_REQ");
		vo.setLabel("PLAN CHK REQ");
		o[12] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATED");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("UPDATED");
		vo.setLabel("UPDATED BY");
		o[13] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("SENSITIVE");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("SENSITIVE");
		vo.setLabel("SENSITIVE");
		o[14] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATED_DATE");
		vo.setType("DATETIME");
		vo.setItype("hidden");
		vo.setField("UPDATED_DATE");
		vo.setLabel("UPDATED DATE");
		o[15] = vo;
		


		g.setObj(o);
		
		return g;
	}

	public static ObjGroupVO id() {
		return details();
	}

	public static ObjGroupVO details() {
		ObjGroupVO g = new ObjGroupVO();
		g.setGroupid("project");
		g.setGroup(TYPE_DETAIL_TITLE);
		g.setType("project");
		g.setHistory(true);

		ObjVO[] o = new ObjVO[24];
//		o[0] = FieldObjects.toObject(-1, 0, "NAME", "String", "text", "NAME", "", "", "", "", "", "", "", "Y","Y","Y");
//		o[1] = FieldObjects.toObject(-1, 0, "DESCRIPTION", "String", "text", "DESCRIPTION", "", "", "", "", "", "", "", "Y","Y","Y");

		ObjVO vo = new ObjVO();
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("NAME");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("NAME");
		vo.setLabel("NAME");
		vo.setRequired("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("LKUP_PROJECT_TYPE_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("LKUP_PROJECT_TYPE_ID");
		vo.setLabel("TYPE");
		vo.setRequired("Y");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setTextfield("LKUP_PROJECT_TYPE_TEXT");
		vo.setLkup("type");
//		vo.setJson(Choices.choiceUrl("LKUP_PROJECT_TYPE", "TYPE", "TYPE"));
		o[1] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("DESCRIPTION");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("DESCRIPTION");
		vo.setLabel("DESCRIPTION");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("LKUP_PROJECT_STATUS_ID");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("LKUP_PROJECT_STATUS_ID");
		vo.setLabel("STATUS");
		vo.setTextfield("LKUP_PROJECT_STATUS_TEXT");
		vo.setRequired("Y");
		vo.setEditable("Y");
		vo.setAddable("Y");
		vo.setText("");
		vo.setLkup("status");
//		vo.setJson(Choices.choiceUrl("LKUP_PROJECT_STATUS", "STATUS", "STATUS"));
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(20);
		vo.setFieldid("PROJECT_NBR");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("PROJECT_NBR");
		vo.setLabel("PROJECT NBR");
		vo.setRequired("N");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setSystemGenerated("Y");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("APPLIED_DT");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("APPLIED_DT");
		vo.setLabel("APPLIED DATE");
		o[5] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("VALUATION_CALCULATED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_CALCULATED");
		vo.setLabel("VALUATION CALCULATED");
		vo.setShowpublic(false);
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("START_DT");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("START_DT");
		vo.setLabel("START DATE");
		o[7] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("VALUATION_DECLARED");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("VALUATION_DECLARED");
		vo.setLabel("VALUATION DECLARED");
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[8] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("COMPLETION_DT");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("COMPLETION_DT");
		vo.setLabel("COMPLETION DATE");
		o[9] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("CIP_ACCTNO");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("CIP_ACCTNO");
		vo.setLabel("CIP");
		vo.setShowpublic(false);
		o[10] = vo;


		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("EXPIRED_DT");
		vo.setType("DATE");
		vo.setItype("DATE");
		vo.setField("EXPIRED_DT");
		vo.setLabel("EXPIRED DATE");
		o[11] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ENTITY");
		vo.setValue("lso");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("ENTITY");
		vo.setLabel("ENTITY");
		vo.setEditable("N");
		vo.setAddable("Y");
		vo.setDisplay("N");
		o[12] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("PLAN_CHK_REQ");
		vo.setType("boolean");
		vo.setItype("boolean");
		vo.setField("PLAN_CHK_REQ");
		vo.setLabel("PLAN CHK REQ");
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
		vo.setShowpublic(false);
		vo.setEditable("Y");
		vo.setAddable("Y");
		o[14] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ACTIVITY_ARRAY");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("ACTIVITY_ARRAY");
		vo.setAddable("Y");
		vo.setDisplay("N");
		o[15] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("ACTIVITY_DESCRIPTION");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("ACTIVITY_DESCRIPTION");
		vo.setAddable("Y");
		vo.setDisplay("N");
		o[16] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("PLAN_CHK_REQ_ARRAY");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("PLAN_CHK_REQ_ARRAY");
		vo.setAddable("Y");
		vo.setDisplay("N");
		vo.setShowpublic(false);
		o[17] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("INHERIT_ARRAY");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("INHERIT_ARRAY");
		vo.setAddable("Y");
		vo.setDisplay("N");
		o[18] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("CREATED");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("CREATED");
		vo.setLabel("CREATED BY");
		vo.setAddable("N");
		vo.setEditable("N");
		vo.setShowpublic(false);
		o[19] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("CREATED_DATE");
		vo.setType("DATETIME");
		vo.setItype("hidden");
		vo.setField("CREATED_DATE");
		vo.setLabel("CREATED DATE");
		vo.setAddable("N");
		vo.setEditable("N");
		vo.setShowpublic(false);
		o[20] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATED");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("UPDATED");
		vo.setLabel("UPDATED BY");
		vo.setAddable("N");
		vo.setEditable("N");
		vo.setShowpublic(false);
		o[21] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("UPDATED_DATE");
		vo.setType("DATETIME");
		vo.setItype("hidden");
		vo.setField("UPDATED_DATE");
		vo.setLabel("UPDATED DATE");
		vo.setAddable("N");
		vo.setEditable("N");
		vo.setShowpublic(false);
		o[22] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("RECREATE");
		vo.setType("String");
		vo.setItype("hidden");
		vo.setField("RECREATE");
		vo.setLabel("RECREATE");
		vo.setRequired("N");
		vo.setAddable("N");
		vo.setEditable("N");
		vo.setShowpublic(false);
		o[23] = vo;
		


		g.setObj(o);
		
		return g;
	}


}
