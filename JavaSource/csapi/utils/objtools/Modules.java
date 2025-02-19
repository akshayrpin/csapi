package csapi.utils.objtools;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.LkupCache;
import csapi.impl.custom.CustomSQL;
import csapi.impl.general.GeneralAgent;
import csapi.impl.library.LibrarySQL;
import csapi.impl.review.ReviewSQL;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsReflect;
import csshared.utils.ObjMapper;
import csshared.vo.RequestVO;
import csshared.vo.ToolVO;
import csshared.vo.ToolsVO;
import csshared.vo.TypeVO;
import csshared.vo.lkup.RolesVO;

public class Modules {

	public static String[] summaryModules = {"divisions","setback","appointment","people","notes","library","attachments","custom"};
	public static String[] infoModules = {"holds","reviews"};

	public static String[] summaryModules(String type) {
		return CsReflect.getSummaryModules(type);
	}

	public static String[] infoModules(String type) {
		return CsReflect.getInfoModules(type);
	}

	public static String[] summary(String type, int typeid, Token u) {
		boolean empty = true;
		ArrayList<String> l = new ArrayList<String>();
		l.add(type);
		String command = ObjSQL.getModules("summary", type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.size() > 0) {
				while (db.next()) {
					String module = db.getString("MODULE").toLowerCase();
					if (!module.equalsIgnoreCase("reviews")) {
						if (u.isAdmin()) {
							l.add(module);
							empty = false;
						}
						else {
							RolesVO r = LkupCache.getModuleRoles(module);
							if (r.readAccess(u.getRoles(), u.getNonpublicroles())) {
								l.add(module);
								empty = false;
							}
						}
					}
				}
			}
			db.clear();
		}
		if (empty) { return summaryModules(type); }
		return Operator.toArray(l);
	}
	
	public static String[] info(String type, int typeid, Token u) {
		boolean empty = true;
		ArrayList<String> l = new ArrayList<String>();
		String command = ObjSQL.getModules("info", type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.size() > 0) {
				while (db.next()) {
					String module = db.getString("MODULE").toLowerCase();
					empty = false;
					if (!module.equalsIgnoreCase("custom")) {
						if (u.isAdmin()) {
							l.add(module);
						}
						else {
							RolesVO r = LkupCache.getModuleRoles(module);
							if (r.readAccess(u.getRoles(), u.getNonpublicroles())) {
								l.add(module);
							}
						}
					}
				}
			}
			db.clear();
		}
		if (empty) { return infoModules(type); }
		return Operator.toArray(l);
	}

	public static ToolsVO tools(String entity, int entityid, String type, int typeid, String hold, Token u) {
		ToolsVO r = new ToolsVO();
		r.setType(type);
		r.setTypeid(typeid);
		r.setEntity(entity);
		r.setEntityid(entityid);
		boolean empty = true;
		String command = ObjSQL.getModules("tool", type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.size() > 0) {
				ArrayList<ToolVO> t = new ArrayList<ToolVO>();
				while (db.next()) {
					int moduleid = db.getInt("ID");
					String module = db.getString("MODULE").toLowerCase();
					String title = db.getString("TOOL_TITLE").toLowerCase();
					String doh = db.getString("DISABLE_ON_HOLD");
					String dtoh = db.getString("DISABLE_TOOL_ON_HOLD");
					String action = db.getString("TOOL_ACTION");
					String copyact = db.getString("ACTIVITY_COPY");
					String copyproj = db.getString("PROJECT_COPY");
					empty = false;

					if (Operator.equalsIgnoreCase(module, "library")) {
						title = GeneralAgent.getLibraryGroupName(type, typeid).toLowerCase();
					}
					if (Operator.hasValue(action)) {
						boolean valid = false;
						if (u.isAdmin()) {
							valid = true;
						}
						else {
							RolesVO rl = LkupCache.getModuleRoles(module);
							if (Operator.equalsIgnoreCase(action, "add")) {
								if (rl.createAccess(u.getRoles(), u.getNonpublicroles())) {
									valid = true;
								}
							}
							else if (Operator.equalsIgnoreCase(action, "edit")) {
								if (rl.updateAccess(u.getRoles(), u.getNonpublicroles())) {
									valid = true;
								}
							}
							else if (Operator.equalsIgnoreCase(action, "delete")) {
								if (rl.deleteAccess(u.getRoles(), u.getNonpublicroles())) {
									valid = true;
								}
							}
							else {
								if (rl.readAccess(u.getRoles(), u.getNonpublicroles())) {
									valid = true;
								}
							}
						}
						if (valid) {
							ToolVO vo = new ToolVO();
							vo.setTool(module);
							vo.setModuleid(moduleid);
							vo.setTitle(title);
							vo.setHolds(hold);
							vo.setAction(action);
							vo.setActivitycopy(copyact);
							vo.setProjectcopy(copyproj);
							if (type.equalsIgnoreCase("activity") && !u.isAdmin()) {
								vo.setDisabletoolonhold(Operator.equalsIgnoreCase(dtoh, "Y"));
								vo.setDisableonhold(Operator.equalsIgnoreCase(doh, "Y"));
							}
							t.add(vo);
						}
					}
				}
				r.setTools(t.toArray(new ToolVO[t.size()]));
			}
			db.clear();
		}
		if (empty) { return CsReflect.getTools(entity, entityid, type, typeid, hold, u); }
		return r;
	}

	public static ToolsVO modules(String entity, int entityid, String type, int typeid, Token u) {
		ToolsVO r = new ToolsVO();
		r.setType(type);
		r.setTypeid(typeid);
		r.setEntity(entity);
		r.setEntityid(entityid);
		boolean empty = true;
		String command = ObjSQL.getModules("modules", type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.size() > 0) {
				ArrayList<ToolVO> t = new ArrayList<ToolVO>();
				while (db.next()) {
					int mid = db.getInt("ID");
					String module = db.getString("MODULE").toLowerCase();
					String title = db.getString("TOOL_TITLE").toLowerCase();
					String doh = db.getString("DISABLE_ON_HOLD");
					String action = db.getString("TOOL_ACTION");
					String copyact = db.getString("ACTIVITY_COPY");
					String copyproj = db.getString("PROJECT_COPY");
					empty = false;

					if (Operator.equalsIgnoreCase(module, "library")) {
						title = GeneralAgent.getLibraryGroupName(type, typeid).toLowerCase();
					}
					boolean valid = true;
					if (u.isAdmin()) {
						valid = true;
					}
					else {
						RolesVO rl = LkupCache.getModuleRoles(module);
						if (rl.readAccess(u.getRoles(), u.getNonpublicroles())) {
							valid = true;
						}
					}
					if (valid) {
						ToolVO vo = new ToolVO();
						vo.setModuleid(mid);
						vo.setTool(module);
						vo.setTitle(title);
						vo.setAction(action);
						vo.setActivitycopy(copyact);
						vo.setProjectcopy(copyproj);
						vo.setDisableonhold(Operator.equalsIgnoreCase(doh, "Y"));
						t.add(vo);
					}
				}
				r.setTools(t.toArray(new ToolVO[t.size()]));
			}
			db.clear();
		}
		if (empty) { return defaultModules(type, typeid, u); }
		return r;
	}

	public static ToolsVO defaultModules(String type, int typeid, Token u) {
		ToolsVO r = new ToolsVO();
		String[] m = CsReflect.getModules(type);
		String command = ObjSQL.getModules(m);
		Sage db = new Sage();
		if (db.query(command) && db.size() > 0) {
			ArrayList<ToolVO> t = new ArrayList<ToolVO>();
			while (db.next()) {
				String module = db.getString("MODULE").toLowerCase();
				String title = db.getString("TOOL_TITLE").toLowerCase();
				String doh = db.getString("DISABLE_ON_HOLD");
				String action = db.getString("TOOL_ACTION");
				String copyact = db.getString("ACTIVITY_COPY");
				String copyproj = db.getString("PROJECT_COPY");

				if (Operator.equalsIgnoreCase(module, "library")) {
					title = GeneralAgent.getLibraryGroupName(type, typeid).toLowerCase();
				}
				if (Operator.hasValue(action)) {
					boolean valid = false;
					if (u.isAdmin()) {
						valid = true;
					}
					else {
						RolesVO rl = LkupCache.getModuleRoles(module);
						if (Operator.equalsIgnoreCase(action, "add")) {
							if (rl.createAccess(u.getRoles(), u.getNonpublicroles())) {
								valid = true;
							}
						}
						else if (Operator.equalsIgnoreCase(action, "edit")) {
							if (rl.updateAccess(u.getRoles(), u.getNonpublicroles())) {
								valid = true;
							}
						}
						else if (Operator.equalsIgnoreCase(action, "delete")) {
							if (rl.deleteAccess(u.getRoles(), u.getNonpublicroles())) {
								valid = true;
							}
						}
						else {
							if (rl.readAccess(u.getRoles(), u.getNonpublicroles())) {
								valid = true;
							}
						}
					}
					if (valid) {
						ToolVO vo = new ToolVO();
						vo.setTool(module);
						vo.setTitle(title);
						vo.setAction(action);
						vo.setActivitycopy(copyact);
						vo.setProjectcopy(copyproj);
						vo.setDisableonhold(Operator.equalsIgnoreCase(doh, "Y"));
						t.add(vo);
					}
				}
			}
			r.setTools(t.toArray(new ToolVO[t.size()]));
		}
		db.clear();
		return r;
	}

	public static ToolsVO copy(String entity, int entityid, String type, int typeid, Token u) {
		ToolsVO r = modules(entity, entityid, type, typeid, u);
		ArrayList<ToolVO> ta = new ArrayList<ToolVO>();
		ToolVO[] t = r.getTools();
		int l = t.length;
		for (int i=0; i<l; i++) {
			ToolVO tv = t[i];
			String tool = tv.getTool();
			if (Operator.equalsIgnoreCase(type, "activity") && tv.isActivitycopy()) {
				if (tv.isActivitycopy()) {
					if (Operator.equalsIgnoreCase(tool, "custom")) {
						int mid = tv.getModuleid();
						Sage db = new Sage();
						String command = "";
						command = CustomSQL.groups(type, typeid);
						db.query(command);
						while (db.next()) {
							ToolVO ct = new ToolVO();
							ct.setModuleid(mid);
							ct.setTool("custom");
							ct.setTitle(db.getString("GROUP_NAME"));
							ct.setSubmoduleid(db.getInt("ID"));
							ta.add(ct);
						}
						db.clear();
					}
					else if (Operator.equalsIgnoreCase(tool, "library")) {
						int mid = tv.getModuleid();
						Sage db = new Sage();
						String command = "";
						command = LibrarySQL.groups(type, typeid);
						db.query(command);
						while (db.next()) {
							ToolVO ct = new ToolVO();
							ct.setModuleid(mid);
							ct.setTool("library");
							ct.setTitle(db.getString("GROUP_NAME"));
							ct.setSubmoduleid(db.getInt("ID"));
							ta.add(ct);
						}
						db.clear();
					}
					else {
						ta.add(tv);
					}
				}
			}
			else if (Operator.equalsIgnoreCase(type, "project") && tv.isProjectcopy()) {
				if (tv.isProjectcopy()) {
					ta.add(tv);
				}
			}
		}
		t = ta.toArray(new ToolVO[ta.size()]);
		r.setTools(t);
		return r;
	}

	public static ToolsVO custom(String type, int typeid, Token u) {
		ToolsVO r = new ToolsVO();
		ArrayList<ToolVO> ta = new ArrayList<ToolVO>();
		Sage db = new Sage();
		String command = "";
		command = CustomSQL.groups(type, typeid);
		db.query(command);
		while (db.next()) {
			ToolVO ct = new ToolVO();
			ct.setTool(db.getString("GROUP_NAME"));
			ct.setTitle(db.getString("GROUP_NAME"));
			ta.add(ct);
		}
		db.clear();
		ToolVO[] t = ta.toArray(new ToolVO[ta.size()]);
		r.setTools(t);
		return r;
	}

	public static ToolsVO reviews(String type, int typeid, Token u) {
		ToolsVO r = new ToolsVO();
		ArrayList<ToolVO> ta = new ArrayList<ToolVO>();
		Sage db = new Sage();
		String command = "";
		command = ReviewSQL.groups(type, typeid);
		db.query(command);
		while (db.next()) {
			ToolVO ct = new ToolVO();
			ct.setTool(db.getString("GROUP_NAME"));
			ct.setTitle(db.getString("GROUP_NAME"));
			ta.add(ct);
		}
		db.clear();
		ToolVO[] t = ta.toArray(new ToolVO[ta.size()]);
		r.setTools(t);
		return r;
	}

	public static String modules(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getModules(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String refreshmodules(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			CsDeleteCache.deleteTypeCache(vo.getType(), vo.getTypeid());
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getModules(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static boolean disableOnHold(String module) {
		boolean r = true;
		if (Operator.hasValue(module)) {
			String command = ObjSQL.getModule(module);
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				if (db.query(command) && db.next()) {
					r = db.equalsIgnoreCase("DISABLE_ON_HOLD", "Y");
				}
				db.clear();
			}
		}
		return r;
	}



}











