package csapi.impl.review;

import alain.core.db.Sage;
import csapi.common.Choices;
import csapi.common.FieldObjects;
import csapi.utils.objtools.ObjSQL;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class ReviewFields {

	// title for detail group
	public static final String TYPE_DETAIL_TITLE = "REVIEW";

	// name of main table (this is a requirement for tables such as activity where the main table is "activity" but connected tables use "act"
	public static final String MAIN_TABLE_REF = "REVIEW";

	// reference to other tables
	public static final String TABLE_REF = "REVIEW";

	// id field used to join other tables
	public static final String FIELD_ID_REF = "REVIEW_ID";

	// name of main type table
	public static final String TYPE_TABLE_REF = "REVIEW";

	/**
	 * @author aromero
	 */
	public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("review");
		r.setType("review");
		r.setGroupid("review");
		ObjVO[] o = new ObjVO[16];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("TITLE");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("TITLE");
		vo.setLabel("TITLE");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("START_DATE");
		vo.setType("select");
		vo.setItype("date");
		vo.setField("START_DATE");
		vo.setLabel("START DATE");
		vo.setRequired("Y");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("DUE_DATE");
		vo.setType("select");
		vo.setItype("date");
		vo.setField("DUE_DATE");
		vo.setLabel("DUE DATE");
		vo.setRequired("Y");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REVIEW_ID");
		vo.setType("String");
		vo.setItype("review");
		vo.setField("REVIEW_ID");
		vo.setLabel("REVIEW");
		vo.setRequired("Y");
		o[3] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("LKUP_REVIEW_STATUS_ID");
		vo.setType("select");
		vo.setItype("reviewstatus");
		vo.setField("LKUP_REVIEW_STATUS_ID");
		vo.setLabel("STATUS");
		vo.setRequired("Y");
		o[4] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("DATE");
		vo.setType("select");
		vo.setItype("date");
		vo.setField("DATE");
		vo.setLabel("DATE");
		vo.setRequired("Y");
		o[5] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setFieldid("TIME");
		vo.setType("datetime");
		vo.setItype("availability_start_time");
		vo.setField("TIME");
		vo.setTextfield("TIME_TEXT");
		vo.setLabel("START TIME");
		o[6] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("INSPECTOR_ID");
		vo.setType("select");
		vo.setItype("isnpectors");
		vo.setField("INSPECTOR_ID");
		vo.setLabel("INSPECTOR");
		vo.setRequired("Y");
		o[7] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REF_TEAM_ID");
		vo.setType("select");
		vo.setItype("reviewteam");
		vo.setField("REF_TEAM_ID");
		vo.setLabel("STATUS");
		vo.setRequired("Y");
		o[8] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REVIEW_COMMENTS");
		vo.setType("string");
		vo.setItype("reviewcomment");
		vo.setField("REVIEW_COMMENTS");
		vo.setLabel("COMMENTS");
		vo.setRequired("Y");
		o[9] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ATTACHMENT");
		vo.setType("file");
		vo.setItype("attachment");
		vo.setField("ATTACHMENT");
		vo.setLabel("ATTACHMENT");
		o[10] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ATTACHMENT_TITLE");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("ATTACHMENT_TITLE");
		vo.setLabel("ATTACHMENT TITLE");
		o[11] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ATTACHMENT_TYPE_ID");
		vo.setType("select");
		vo.setItype("attachtype");
		vo.setTextfield("ATTACHMENT_TYPE");
		vo.setField("ATTACHMENT_TYPE_ID");
		vo.setLabel("ATTACHMENT TYPE");
		o[12] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ATTACHMENT_DESCRIPTION");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("ATTACHMENT_DESCRIPTION");
		vo.setLabel("ATTACHMENT DESCRIPTION");
		o[13] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ATTACHMENT_PUBLIC");
		vo.setType("text");
		vo.setItype("boolean");
		vo.setField("ATTACHMENT_PUBLIC");
		vo.setLabel("PUBLIC ATTACHMENT");
		o[14] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("NOTIFY");
		vo.setType("text");
		vo.setItype("String");
		vo.setField("NOTIFY");
		vo.setLabel("NOTIFYT");
		o[15] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO combo() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("review");
		r.setType("review");
		r.setGroupid("review");
		ObjVO[] o = new ObjVO[2];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("START_DATE");
		vo.setType("select");
		vo.setItype("date");
		vo.setField("START_DATE");
		vo.setLabel("START DATE");
		vo.setRequired("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("DUE_DATE");
		vo.setType("select");
		vo.setItype("date");
		vo.setField("DUE_DATE");
		vo.setLabel("DUE DATE");
		vo.setRequired("Y");
		o[1] = vo;

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO list(int groupid, int reviewid) {
		ObjGroupVO r = new ObjGroupVO();
		String command = ObjSQL.getReview(groupid, reviewid);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			StringBuilder sb = new StringBuilder();
			sb.append(db.getString("GROUP_NAME"));
			if (db.hasValue("REVIEW")) {
				sb.append(": ");
				sb.append(db.getString("REVIEW"));
			}
			r.setGroupid(db.getString("GROUP_ID"));
			r.setGroup(sb.toString());
			r.setType("review");
		}
		db.clear();
		r.setFields(new String[] {"REVIEW","COMMENT","DATE","STATUS"});
		r.setIndex(new String[] {"REVIEW","COMMENT","DATE","STATUS"});

		ObjVO[] o = new ObjVO[5];

		ObjVO s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("GROUP_NAME");
		s.setType("type");
		s.setItype("text");
		s.setField("GROUP_NAME");
		s.setLabel("GROUP");
		o[0] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("REVIEW");
		s.setType("String");
		s.setItype("text");
		s.setField("REVIEW");
		s.setLabel("REVIEW");
		o[1] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("REVIEW_COMMENTS");
		s.setType("largetext");
		s.setItype("largetext");
		s.setField("REVIEW_COMMENTS");
		s.setLabel("COMMENT");
		o[2] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("DATE");
		s.setType("date");
		s.setItype("date");
		s.setField("DATE");
		s.setLabel("DATE");
		o[3] = s;

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("STATUS");
		s.setType("status");
		s.setItype("text");
		s.setField("STATUS");
		s.setLabel("STATUS");
		o[4] = s;

		r.setObj(o);
		return r;
	}

	
}
