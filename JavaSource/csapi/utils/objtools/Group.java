package csapi.utils.objtools;

import java.util.ArrayList;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.general.GeneralAgent;
import csapi.impl.info.InfoAgent;
import csapi.utils.CsApiConfig;
import csapi.utils.CsReflect;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjMap;
import csshared.vo.ObjVO;
import csshared.vo.SubObjVO;

public class Group {

//	public static ObjGroupVO group(String type, int typeid) {
//		String command = CsReflect.getQuery("getDetails", type, typeid);
//		ObjGroupVO g = CsReflect.getFields(type);
//		Sage db = new Sage();
//		db.query(command);
//		if (db.next()) {
//			int l = g.getObj().length;
//			for (int i = 0; i < l; i++) {
//				String field = g.getObj()[i].getFieldid();
//				if (Operator.hasValue(field)){
//					String value = db.getString(field);
//					g.getObj()[i].setValue(value);
//				}
//			}
//		}
//		db.clear();
//		return g;
//	}

	public static ObjGroupVO fields(String grouptype) {
		return CsReflect.getDetailFields(grouptype);
	}

	public static ObjGroupVO fields(String type, int typeid, String grouptype, int id) {
		return CsReflect.getDetailFields(type, grouptype);
	}

	public static ObjGroupVO searchfields(String type, int typeid, String grouptype, int id) {
		return CsReflect.getSearchFields(type, grouptype);
	}

	public static ObjGroupVO details(String type, int typeid, String grouptype, int id, String option) {
		ObjGroupVO fields = CsReflect.getDetailFields(type, grouptype);
		String command = CsReflect.getQuery("details", type, typeid, grouptype, id, option);
		return vertical(fields, command);
	}

	public static ObjGroupVO details(String type, int typeid, String grouptype, int id, String option, Token u) {
		ObjGroupVO fields = CsReflect.getDetailFields(type, grouptype);
		String command = CsReflect.getQuery("details", type, typeid, grouptype, id, option, u);
		return vertical(fields, command);
	}

	public static ObjGroupVO summary(String type, int typeid, String grouptype, String option, Token u) {
		return ui(type, typeid, grouptype, "summary", -1, option, u);
	}

	public static ObjGroupVO ext(String type, int typeid, String grouptype, String option, Token u) {
		return ui(type, typeid, grouptype, "ext", -1, option, u);
	}

	public static ObjGroupVO info(String type, int typeid, String grouptype, String option, Token u) {
		return ui(type, typeid, grouptype, "info", -1, option, u);
	}

	public static ObjGroupVO list(String type, int typeid, String grouptype, int id, String option, Token u) {
		return ui(type, typeid, grouptype, "list", id, option, u);
	}

	public static ObjGroupVO[] history(String type, int typeid, String grouptype, String option) {
		return uiHistory(type, typeid, grouptype, "history", -1, option);
	}

	public static ObjGroupVO id(String type, int typeid, String module, int id) {
		ObjGroupVO fields = CsReflect.idFields(module);
		String tabletype = CsReflect.getTableType(module, module);
		String command = CsReflect.getIdQuery(type, typeid, module, id);
		if (module.equalsIgnoreCase("appointment")) {
			Logger.info("Appointment Group", fields.getGroup());
			return vertical(fields, command);
		}
		else if (tabletype.equalsIgnoreCase("horizontal")) {
			Logger.info("Horizontal Group", fields.getGroup());
			return horizontal(fields, command);
		}
		else if (tabletype.equalsIgnoreCase("crosstab")) {
			Logger.info("Crosstab Group", fields.getGroup());
			return crosstab(fields, command);
		}
		else {
			Logger.info("Vertical Group", fields.getGroup());
			return vertical(fields, command);
		}
	}

	public static ObjGroupVO ui(String type, int typeid, String grouptype, String ui, int id, String option, Token u) {
		ObjGroupVO fields = CsReflect.getUiFields(type, grouptype, ui);
		String tabletype = CsReflect.getTableType(type, grouptype);
		String command = CsReflect.getQuery(ui, type, typeid, grouptype, id, option, u);
		if (Operator.equalsIgnoreCase(grouptype, "library")) {
			String title = GeneralAgent.getLibraryGroupName(type, typeid);
			if (Operator.hasValue(title)) {
				fields.setLabel(title);
				if (InfoAgent.hasContent(title)) {
					fields.setContenttype(title);
				}
			}
		}
		if (tabletype.equalsIgnoreCase("horizontal")) {
			Logger.info("Horizontal Group", fields.getGroup());
			return horizontal(fields, command);
		}
		else if (tabletype.equalsIgnoreCase("crosstab")) {
			Logger.info("Crosstab Group", fields.getGroup());
			return crosstab(fields, command);
		}
		else {
			Logger.info("Vertical Group", fields.getGroup());
			return vertical(fields, command);
		}
	}

	public static ObjGroupVO[] uiHistory(String type, int typeid, String grouptype, String ui, int id, String option) {
		ObjGroupVO[] r = new ObjGroupVO[0];
		String tabletype = CsReflect.getTableType(type, grouptype);
		if (tabletype.equalsIgnoreCase("horizontal")) {
			// TO DO
		}
		else if (tabletype.equalsIgnoreCase("crosstab")) {
			// TO DO
		}
		else {
			r = historyVertical(type, typeid, grouptype, ui, id, option);
		}
		return r;
	}

	public static ObjGroupVO my(String type, String username) {
		ObjGroupVO fields = CsReflect.getUiFields(type, type, "my");
		String command = CsReflect.getMyQuery(type, username);
		Logger.info("My Group", fields.getGroup());
		return horizontal(fields, command);
	}
	
	/*public static ObjGroupVO my(RequestVO vo,String username) {
		ObjGroupVO fields = CsReflect.getUiFields(type, type, "my");
		String command = CsReflect.getMyQuery(type, username);
		Logger.info("My Group", fields.getGroup());
		return horizontal(fields, command);
	}*/

	public static ObjGroupVO myActive(String type, String username, String option) {
		ObjGroupVO fields = CsReflect.myActiveFields(type, type);
		String command = CsReflect.getMyActiveQuery(type, username, option);
		Logger.info("My Group", fields.getGroup());
		return horizontal(fields, command);
	}

	public static ObjGroupVO full(String type, String id, String start, String end) {
		ObjGroupVO fields = CsReflect.getUiFields(type, type, "full");
		String command = CsReflect.getFullQuery(type, id, start, end);
		String tabletype = CsReflect.getTableType(type, type);
		Logger.info("Full Group", fields.getGroup());
		if (tabletype.equalsIgnoreCase("crosstab")) {
			Logger.info("Crosstab Group", fields.getGroup());
			return crosstab(fields, command);
		}
		else if (tabletype.equalsIgnoreCase("horizontal") || (!Operator.hasValue(tabletype) && (Operator.hasValue(fields.getFields()) || Operator.hasValue(fields.getIndex())))) {
			Logger.info("Horizontal Group", fields.getGroup());
			return horizontal(fields, command);
		}
		else {
			Logger.info("Vertical Group", fields.getGroup());
			return vertical(fields, command);
		}
	}

	public static ObjGroupVO horizontal(ObjGroupVO g, String command) {
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				ObjVO[] fields = g.getObj();
				int l = fields.length;
				int count = 0;
				ObjMap[] o = new ObjMap[db.size()];
				while (db.next()) {
					
					ObjMap v = new ObjMap();
					v.setId(db.getInt("ID"));
					if (db.equalsIgnoreCase("FINAL", "Y")) {
						v.setFinaled(true);
					}
					if (db.equalsIgnoreCase("ADDABLE", "N")) {
						g.setAddable(false);
					}
					if (db.equalsIgnoreCase("EDITABLE", "N")) {
						g.setEditable(false);
					}
					v.setRef(db.getString("REF"));
					v.setRefid(db.getInt("REF_ID"));
					v.setExpires(db.getString("EXPIRES"));
					if (db.hasValue("ISPUBLIC")) {
						v.setShowpublic(db.equalsIgnoreCase("ISPUBLIC", "Y"));
					}
					if (db.hasValue("CONTENT_TYPE")) {
						g.setContenttype(db.getString("CONTENT_TYPE"));
					}

					for (int i=0; i<l; i++) {
						ObjVO field = fields[i];
						ObjVO s = field.duplicate();
						String value = db.getString(field.getField());
						String link = db.getString(field.getLinkfield());
						String summarytype = db.getString("SUMMARYTYPE");
						int summaryid = db.getInt("SUMMARYID");
						String linktype = db.getString("LINKTYPE");
						int linkid = db.getInt("LINKID");
						s.setValue(value);
						String text = value;
						String textfield = field.getTextfield();
						if (Operator.hasValue(textfield)) {
							text = db.getString(textfield);
						}
						s.setText(text);
						if (Operator.hasValue(link)) {
							s.setLink(link);
//							s.setTarget("_blank");
						}

						if (Operator.hasValue(linktype)) {
							s.setLinktype(linktype);
						}
						if (linkid > 0) {
							s.setLinkid(linkid);
						}

						if (Operator.hasValue(summarytype)) {
							s.setSummarytype(summarytype);
						}
						if (summaryid > 0) {
							s.setSummaryid(summaryid);
						}
						if (db.hasValue("ISPUBLIC")) {
//							s.setShowpublic(db.equalsIgnoreCase("ISPUBLIC", "Y"));
						}

						s.setRef(db.getString("REF"));
						s.setRefid(db.getInt("REF_ID"));
						s.setAdminlink(db.getString("ADMINLINK"));

						String relfield = field.getRelfield();
						if (Operator.hasValue(relfield)) {
							s.setRel(db.getString(relfield));
						}
						String rel2field = field.getRel2field();
						if (Operator.hasValue(rel2field)) {
							s.setRel2(db.getString(rel2field));
						}
						v.getValues().put(field.getLabel(), s);
					}

					o[count] = v;
					count++;
					
				}
				g.setValues(o);
			}
			db.clear();
		}
		g.setObj(new ObjVO[0]);
		return g;
	}

	public static ObjGroupVO crosstab(ObjGroupVO g, String command) {
		if (Operator.hasValue(command)) {
			ObjVO[] o = new ObjVO[0];
			Sage db = new Sage();
			String crosstab = g.getCrosstab();
			if (Operator.hasValue(crosstab)) {
				if (db.query(command) && db.size() > 0) {
					o = new ObjVO[db.size()];
					int count = 0;
					ObjVO[] fields = g.getObj();
					int l = fields.length;
					while (db.next()) {
						if (db.hasValue("CONTENT_TYPE")) {
							g.setContenttype(db.getString("CONTENT_TYPE"));
						}
						String ct = db.getString(crosstab);
						ObjVO vo = new ObjVO();
						if (db.equalsIgnoreCase("FINAL", "Y")) {
							vo.setFinaled(true);
						}
						vo.setField(ct);
						for (int i=0; i<l; i++) {
							ObjVO f = fields[i];
							String field = f.getField();
							String type = f.getType();
							String itype = f.getItype();
							String value = db.getString(field);
							String text = value;
							String textfield = f.getTextfield();
							if (Operator.hasValue(textfield)) {
								text = db.getString(textfield);
							}
							SubObjVO s = new SubObjVO();
							s.setValue(value);
							s.setType(type);
							s.setItype(itype);
							s.setText(text);
							vo.getValues().put(field, s);
						}
						o[count] = vo;
						count++;
					}

				}
			}
			db.clear();
			g.setObj(o);
		}
		return g;
	}
	
	public static ObjGroupVO vertical(ObjGroupVO g, String command) {
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			db.query(command);
			if (db.next()) {
				if (db.equalsIgnoreCase("FINAL", "Y")) {
					g.setFinaled(true);
				}
				if (db.equalsIgnoreCase("ADDABLE", "N")) {
					g.setAddable(false);
				}
				if (db.equalsIgnoreCase("EDITABLE", "N")) {
					g.setEditable(false);
				}
				if (db.hasValue("CONTENT_TYPE")) {
					g.setContenttype(db.getString("CONTENT_TYPE"));
				}
				String groupid = "";
				if (!Operator.isNumber(g.getGroupid())) {
					groupid = g.getGroupid();
				}
				int l = g.getObj().length;
				if (Operator.hasValue(g.getTitlefield())) {
					g.setTitle(db.getString(g.getTitlefield()));
				}
				if (Operator.hasValue(g.getSubtitlefield())) {
					g.setSubtitle(db.getString(g.getSubtitlefield()));
				}
				for (int i = 0; i < l; i++) {
					String field = g.getObj()[i].getField();
					String textfield = g.getObj()[i].getTextfield();
					String linkfield = g.getObj()[i].getLinkfield();
					String relfield = g.getObj()[i].getRelfield();
					String rel2field = g.getObj()[i].getRel2field();
					String itype = g.getObj()[i].getItype();
					if (Operator.hasValue(groupid)) {
						int gid = db.getInt(groupid);
						if (gid > 0) {
							g.setGroupid(Operator.toString(gid));
						}
					}
					if (Operator.hasValue(field)){
						String value = db.getString(field);
						g.getObj()[i].setValue(value);
						
						if(Operator.hasValue(textfield)){
							String text = db.getString(textfield);
							g.getObj()[i].setText(text);
						}
						if(Operator.hasValue(linkfield)){
							String link = db.getString(linkfield);
							g.getObj()[i].setLink(link);
						}
						if (Operator.hasValue(relfield)) {
							String rel = db.getString(relfield);
							g.getObj()[i].setRel(rel);
						}
						if (Operator.hasValue(rel2field)) {
							String rel2 = db.getString(rel2field);
							g.getObj()[i].setRel2(rel2);
						}
						g.getObj()[i].setRefid(db.getInt("REF_ID"));
                        g.getObj()[i].setRef(db.getString("REF"));
					}
				}
			}
			db.clear();
		}
		return g;
	}

	public static ObjGroupVO[] verticals(ObjGroupVO fields, String command) {
		ArrayList<ObjGroupVO> r = new ArrayList<ObjGroupVO>();
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			db.query(command);
			while (db.next()) {
				ObjGroupVO g = fields.duplicate();
				int id = db.getInt("ID");
				if (db.equalsIgnoreCase("FINAL", "Y")) {
					g.setFinaled(true);
				}
				if (db.equalsIgnoreCase("ADDABLE", "N")) {
					g.setAddable(false);
				}
				if (db.equalsIgnoreCase("EDITABLE", "N")) {
					g.setEditable(false);
				}
				if (db.hasValue("CONTENT_TYPE")) {
					g.setContenttype(db.getString("CONTENT_TYPE"));
				}
				String groupid = "";
				if (!Operator.isNumber(g.getGroupid())) {
					groupid = g.getGroupid();
				}
				int l = g.getObj().length;
				if (Operator.hasValue(g.getTitlefield())) {
					g.setTitle(db.getString(g.getTitlefield()));
				}
				if (Operator.hasValue(g.getSubtitlefield())) {
					g.setSubtitle(db.getString(g.getSubtitlefield()));
				}
				for (int i = 0; i < l; i++) {
					String field = g.getObj()[i].getField();
					String textfield = g.getObj()[i].getTextfield();
					String linkfield = g.getObj()[i].getLinkfield();
					String relfield = g.getObj()[i].getRelfield();
					String rel2field = g.getObj()[i].getRel2field();
					if (Operator.hasValue(groupid)) {
						int gid = db.getInt(groupid);
						if (gid > 0) {
							g.setGroupid(Operator.toString(gid));
						}
					}
					if (Operator.hasValue(field)){
						String value = db.getString(field);
						g.getObj()[i].setValue(value);
						g.getObj()[i].setId(id);
						
						if(Operator.hasValue(textfield)){
							String text = db.getString(textfield);
							g.getObj()[i].setText(text);
						}
						if(Operator.hasValue(linkfield)){
							String link = db.getString(linkfield);
							g.getObj()[i].setLink(link);
						}
						if (Operator.hasValue(relfield)) {
							String rel = db.getString(relfield);
							g.getObj()[i].setRel(rel);
						}
						if (Operator.hasValue(rel2field)) {
							String rel2 = db.getString(rel2field);
							g.getObj()[i].setRel2(rel2);
						}
					}
				}
				r.add(g);
			}
			db.clear();
		}
		return r.toArray(new ObjGroupVO[r.size()]);
	}

	public static ObjGroupVO[] historyVertical(String type, int typeid, String grouptype, String ui, int id, String option) {
		String command = CsReflect.getQuery(ui, type, typeid, grouptype, id, option);
		ArrayList<ObjGroupVO> gs = new ArrayList<ObjGroupVO>();
		if (Operator.hasValue(command)) {
			Sage db = new Sage(CsApiConfig.getHistorySource());
			db.query(command);
			while (db.next()) {
				ObjGroupVO g = CsReflect.getUiFields(type, grouptype, ui);
				if (db.equalsIgnoreCase("FINAL", "Y")) {
					g.setFinaled(true);
				}
				if (db.equalsIgnoreCase("ADDABLE", "N")) {
					g.setAddable(false);
				}
				if (db.equalsIgnoreCase("EDITABLE", "N")) {
					g.setEditable(false);
				}
				if (db.hasValue("GROUP_LABEL")) {
					g.setLabel(db.getString("GROUP_LABEL"));
				}
				if (db.hasValue("GROUP_UPDATED")) {
					g.setUpdated(db.getString("GROUP_UPDATED"));
				}
				g.setModulechanged(db.getString("MODULE_CHANGED"));
				g.setModulechangedaction(db.getString("MODULE_CHANGED_ACTION"));
				g.setModulechangedid(db.getInt("MODULE_CHANGED_ID"));
				String groupid = "";
				if (!Operator.isNumber(g.getGroupid())) {
					groupid = g.getGroupid();
				}
				int l = g.getObj().length;
				if (Operator.hasValue(g.getTitlefield())) {
					g.setTitle(db.getString(g.getTitlefield()));
				}
				if (Operator.hasValue(g.getSubtitlefield())) {
					g.setSubtitle(db.getString(g.getSubtitlefield()));
				}
				for (int i = 0; i < l; i++) {
					String field = g.getObj()[i].getField();
					String textfield = g.getObj()[i].getTextfield();
					String relfield = g.getObj()[i].getRelfield();
					String rel2field = g.getObj()[i].getRel2field();
					if (Operator.hasValue(groupid)) {
						int gid = db.getInt(groupid);
						if (gid > 0) {
							g.setGroupid(Operator.toString(gid));
						}
					}
					if (Operator.hasValue(field)){
						String value = db.getString(field);
						g.getObj()[i].setValue(value);
						
						if(Operator.hasValue(textfield)){
							String text = db.getString(textfield);
							g.getObj()[i].setText(text);
						}
						if (Operator.hasValue(relfield)) {
							String rel = db.getString(relfield);
							g.getObj()[i].setRel(rel);
						}
						if (Operator.hasValue(rel2field)) {
							String rel2 = db.getString(rel2field);
							g.getObj()[i].setRel2(rel2);
						}
					}
				}
				gs.add(g);
			}
			db.clear();
		}
		return gs.toArray(new ObjGroupVO[gs.size()]);
	}

	public static ObjGroupVO details(ObjGroupVO g, String command) {
		g.fields = new String[0];
		g.index = new String[0];
		return vertical(g, command);
	}

	
}






