package csapi.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.project.ProjectAgent;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ToolsVO;

public class CsReflect {

	/**
	 * @author aromero
	 */
	public static String getQuery(String method, String type, int id, String option) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("SQL");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + "." + method + "(int id)");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod(method, int.class, String.class);
			result = (String) _method.invoke(null, id, option);
		}
		catch (Exception e) {
			try {
				Logger.info("REFLECT METHOD", classname + "." + method + "(int id)");
				Class<?> _class = Class.forName(classname);
				Method _method = _class.getDeclaredMethod(method, int.class);
				result = (String) _method.invoke(null, id);
			}
			catch (Exception e1) { }
		}
		return result;
	}

	public static String getQuery(String method, String type, int typeid, String grouptype, int groupid, String option) {
		String impl = grouptype;
		if (!Operator.hasValue(grouptype)) { impl = type; }
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(impl.toLowerCase()).append(".").append(Operator.toTitleCase(impl)).append("SQL");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + "." + method + "(String type, int typeid, int groupid)");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod(method, new Class[]{String.class, int.class, int.class, String.class});
			result = (String) _method.invoke(null, new Object[]{type, typeid, groupid, option});
		}
		catch (Exception e) {
			try {
				Logger.info("REFLECT METHOD", classname + "." + method + "(String type, int typeid, int groupid)");
				Class<?> _class = Class.forName(classname);
				Method _method = _class.getDeclaredMethod(method, new Class[]{String.class, int.class, int.class});
				result = (String) _method.invoke(null, new Object[]{type, typeid, groupid});
			}
			catch (Exception e1) {
				
			}
		}
		return result;
	}

	public static String getQuery(String method, String type, int typeid, String grouptype, int groupid, String option, Token u) {
		String impl = grouptype;
		if (!Operator.hasValue(grouptype)) { impl = type; }
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(impl.toLowerCase()).append(".").append(Operator.toTitleCase(impl)).append("SQL");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + "." + method + "(String type, int typeid, int groupid, Token u)");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod(method, new Class[]{String.class, int.class, int.class, String.class, Token.class});
			result = (String) _method.invoke(null, new Object[]{type, typeid, groupid, option, u});
		}
		catch (Exception e) {
			try {
				Logger.info("REFLECT METHOD", classname + "." + method + "(String type, int typeid, int groupid, Token u)");
				Class<?> _class = Class.forName(classname);
				Method _method = _class.getDeclaredMethod(method, new Class[]{String.class, int.class, int.class, Token.class});
				result = (String) _method.invoke(null, new Object[]{type, typeid, groupid, u});
			}
			catch (Exception e1) {
				result = getQuery(method, type, typeid, grouptype, groupid, option);
			}
		}
		return result;
	}

	public static String getIdQuery(String type, int typeid, String module, int id) {
		String impl = module;
		if (!Operator.hasValue(module)) { return ""; }
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(impl.toLowerCase()).append(".").append(Operator.toTitleCase(impl)).append("SQL");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".id(String type, int typeid, int id)");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod("id", new Class[]{String.class, int.class, int.class});
			result = (String) _method.invoke(null, new Object[]{type, typeid, id});
		}
		catch (Exception e) {
		}
		return result;
	}

	public static String getLkup(String method, String grouptype, String type, int typeid, int selected) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(grouptype.toLowerCase()).append(".").append(Operator.toTitleCase(grouptype)).append("SQL");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + "." + method + "(String type, int typeid, int selected)");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod(method, String.class, int.class, int.class);
			result = (String) _method.invoke(null, new Object[]{type, typeid, selected});
		}
		catch (Exception e) { }
		return result;
	}

	public static String getMyQuery(String type, String username) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("SQL");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".my(String username)");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod("my", new Class[]{String.class});
			result = (String) _method.invoke(null, new Object[]{username});
		}
		catch (Exception e) { }
		return result;
	}

	public static String getMyActiveQuery(String type, String username, String option) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("SQL");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".myActive(String username)");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod("myActive", new Class[]{String.class, String.class});
			result = (String) _method.invoke(null, new Object[]{username, option});
		}
		catch (Exception e) { }
		return result;
	}

	public static String getFullQuery(String type, String id, String start, String end) {
		Timekeeper s = new Timekeeper();
		if (Operator.hasValue(start)) {
			s.setDate(start);
		}
		Timekeeper e = new Timekeeper();
		if (Operator.hasValue(end)) {
			e.setDate(end);
		}
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("SQL");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".full(String id, Timekeeper start, Timekeeper end)");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod("full", new Class[]{String.class, Timekeeper.class, Timekeeper.class});
			result = (String) _method.invoke(null, new Object[]{id, s, e});
		}
		catch (Exception ex) { Logger.error(ex); }
		return result;
	}

	public static String getGeneralQuery(String method, String type, int typeid, int objid) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.general.GeneralSQL");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + "." + method + "(String type, int typeid, int objid)");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod(type.toLowerCase(), new Class[]{String.class, int.class, int.class});
			result = (String) _method.invoke(null, new Object[]{type, typeid, objid});
		}
		catch (Exception e) { }
		return result;
	}

	public static String getSearchQuery(String type, String query) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("SQL");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".search(String query)");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod("search", String.class);
			result = (String) _method.invoke(null, query);
		}
		catch (Exception e) { }
		return result;
	}

	/**
	 * @author aromero
	 */
	public static String getTypeDetailTitle(String type) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("Fields");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".TYPE_DETAIL_TITLE");
			Class<?> _class = Class.forName(classname);
			result = (String) _class.getField("TYPE_DETAIL_TITLE").get(null);
		}
		catch (Exception e) { }
		return result;
	}

	public static String getTypeTable(String type) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("Fields");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".TYPE_TABLE_REF");
			Class<?> _class = Class.forName(classname);
			result = (String) _class.getField("TYPE_TABLE_REF").get(null);
		}
		catch (Exception e) { }
		return result;
	}

	public static String getStatusTable(String type) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("Fields");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".STATUS_TABLE_REF");
			Class<?> _class = Class.forName(classname);
			result = (String) _class.getField("STATUS_TABLE_REF").get(null);
		}
		catch (Exception e) { }
		return result;
	}

	public static String[] getSummaryModules(String type) {
		String[] result = new String[0];
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("Fields");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".summaryModules");
			Class<?> _class = Class.forName(classname);
			result = (String[]) _class.getField("summaryModules").get(null);
		}
		catch (Exception e) { }
		return result;
	}

	public static String[] getInfoModules(String type) {
		String[] result = new String[0];
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("Fields");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".infoModules");
			Class<?> _class = Class.forName(classname);
			result = (String[]) _class.getField("infoModules").get(null);
		}
		catch (Exception e) { }
		return result;
	}

	public static String[] getModules(String type) {
		String[] s = getSummaryModules(type);
		String[] i = getInfoModules(type);
		if (!Operator.hasValue(s) && !Operator.hasValue(i)) { return new String[0]; }
		else if (!Operator.hasValue(i)) { return s; }
		else if (!Operator.hasValue(s)) { return i; }
		else { return (String[]) ArrayUtils.addAll(s, i); }
	}

	public static ToolsVO getTools(String entity, int entityid, String type, int typeid, String hold, Token u) {
		ToolsVO result = new ToolsVO();
		if (Operator.hasValue(type) && typeid > 0) {
			StringBuilder sb =  new StringBuilder();
			sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("Tools");
			String classname = sb.toString();
			try {
				Logger.info("REFLECT METHOD", classname + ".tools(String entity, int entityid, String type, int typeid, String alert)");
				Class<?> _class = Class.forName(classname);
				Method _method = _class.getDeclaredMethod("tools", new Class[]{String.class, int.class, String.class, int.class, String.class, Token.class});
				result = (ToolsVO) _method.invoke(null, new Object[]{entity, entityid, type, typeid, hold, u});
			}
			catch (Exception e) { }
		}
		return result;
	}

//	public static ObjGroupVO getModule(String module, String type, int typeid) {
//		ObjGroupVO result = new ObjGroupVO();
//		StringBuilder sb =  new StringBuilder();
//		sb.append("csapi.utils.objtools.Group");
//		String classname = sb.toString();
//		try {
//			Class<?> _class = Class.forName(classname);
//			Method _method = _class.getDeclaredMethod(module.toLowerCase(), new Class[]{String.class, int.class});
//			result = (ObjGroupVO) _method.invoke(null, new Object[]{type, typeid});
//		}
//		catch (Exception e) { }
//		return result;
//	}

	public static ObjGroupVO getGroup(String groupid, String grouptype, String type, int typeid) {
		ObjGroupVO result = new ObjGroupVO();
//		if (grouptype.equalsIgnoreCase("custom") && Operator.isNumber(groupid)) {
//			int grpid = Operator.toInt(groupid);
//			result = Group.custom(type, typeid, grpid);
//		}
//		else {
			try {
				String method = grouptype;
				if (!Operator.hasValue(grouptype) || grouptype.equalsIgnoreCase("details")) { method = "group"; }
				StringBuilder sb =  new StringBuilder();
				sb.append("csapi.utils.objtools.Group");
				String classname = sb.toString();
				Logger.info("REFLECT METHOD", classname + "." + method.toLowerCase() + "(String type, int typeid)");
				Class<?> _class = Class.forName(classname);
				Method _method = _class.getDeclaredMethod(method.toLowerCase(), new Class[]{String.class, int.class});
				result = (ObjGroupVO) _method.invoke(null, new Object[]{type, typeid});
			}
			catch (Exception e) { }
//		}
		return result;
	}

//	public static ObjGroupVO getFields(String type) {
//		return getFields("", type, "", -1);
//	}
//
//	public static ObjGroupVO getFields(String groupid, String grouptype, String type, int typeid) {
//		ObjGroupVO result = new ObjGroupVO();
//		if (grouptype.equalsIgnoreCase("custom") && Operator.isNumber(groupid)) {
//			int grpid = Operator.toInt(groupid);
//			result = Fields.custom(grpid);
//		}
//		else {
//			String impl = grouptype;
//			if (!Operator.hasValue(grouptype) || grouptype.equalsIgnoreCase("details")) { impl = type; }
//			StringBuilder sb =  new StringBuilder();
//			sb.append("csapi.impl.");
//			sb.append(impl).append(".").append(Operator.toTitleCase(impl));
//			sb.append("Fields");
//			String classname = sb.toString();
//			String method = "fields";
//			try {
//				Class<?> _class = Class.forName(classname);
//				Method _method = _class.getDeclaredMethod(method.toLowerCase());
//				result = (ObjGroupVO) _method.invoke(null);
//			}
//			catch (Exception e1) {
//				Logger.error(e1);
//				sb =  new StringBuilder();
//				sb.append("csapi.utils.objtools.Fields");
//				classname = sb.toString();
//				try {
//					Class<?> _class = Class.forName(classname);
//					Method _method = _class.getDeclaredMethod(grouptype.toLowerCase(), new Class[]{String.class, int.class});
//					result = (ObjGroupVO) _method.invoke(null, new Object[]{type, typeid});
//				}
//				catch (Exception e) { }
//			}
//
//		}
//		return result;
//	}

	public static boolean copy(String copymodule, String type, int typeid, int newid, int userid, String ip) {
		String processid = Operator.randomString(15);
		return copy(processid, copymodule, type, typeid, newid, userid, ip);
	}

	public static boolean copy(String processid, String copymodule, String type, int typeid, int newid, int userid, String ip) {
		boolean result = false;
		if (!Operator.hasValue(copymodule) || !Operator.hasValue(type) || typeid < 1 || newid < 1) { return result; }
		String module = "";
		String moduleid = "";
		String[] ma = Operator.split(copymodule, ":");
		if (ma.length > 0) {
			module = ma[0];
			if (ma.length > 1) {
				moduleid = ma[1];
			}
		}
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(module.toLowerCase()).append(".").append(Operator.toTitleCase(module)).append("Agent");
		String classname = sb.toString();
		try {
			Class<?> _class = Class.forName(classname);
			Logger.info("REFLECT METHOD", classname + "." + "copy(String processid, String type, int typeid, int newid, int userid, String ip)");
			Method _method = _class.getDeclaredMethod("copy", new Class[]{String.class, String.class, int.class, int.class, int.class, String.class});
			result = (boolean) _method.invoke(null, new Object[]{processid, type, typeid, newid, userid, ip});
		}
		catch (Exception e) {
			try {
				Class<?> _class = Class.forName(classname);
				Logger.info("REFLECT METHOD", classname + "." + "copy(String processid, String moduleid, String type, int typeid, int newid, int userid, String ip)");
				Method _method = _class.getDeclaredMethod("copy", new Class[]{String.class, String.class, String.class, int.class, int.class, int.class, String.class});
				result = (boolean) _method.invoke(null, new Object[]{processid, moduleid, type, typeid, newid, userid, ip});
			}
			catch (Exception e1) {
				try {
					Class<?> _class = Class.forName(classname);
					Logger.info("REFLECT METHOD", classname + "." + "copy(String type, int typeid, int newid, int userid, String ip)");
					Method _method = _class.getDeclaredMethod("copy", new Class[]{String.class, int.class, int.class, int.class, String.class});
					result = (boolean) _method.invoke(null, new Object[]{type, typeid, newid, userid, ip});
				}
				catch (Exception e3) {
					try {
						Class<?> _class = Class.forName(classname);
						Logger.info("REFLECT METHOD", classname + "." + "copy(String moduleid, String type, int typeid, int newid, int userid, String ip)");
						Method _method = _class.getDeclaredMethod("copy", new Class[]{String.class, String.class, int.class, int.class, int.class, String.class});
						result = (boolean) _method.invoke(null, new Object[]{moduleid, type, typeid, newid, userid, ip});
					}
					catch (Exception e4) {
						try {
							String command = copyQuery(module, moduleid, type, typeid, newid, userid, ip);
							if (Operator.hasValue(command)) {
								Sage db = new Sage();
								result = db.update(command);
								db.clear();
							}
						}
						catch (Exception e5) {
							result = false;
						}
					}
				}
			}
		}
		return result;
	}

	public static String copyQuery(String copymodule, String moduleid, String type, int typeid, int newid, int userid, String ip) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(copymodule.toLowerCase()).append(".").append(Operator.toTitleCase(copymodule)).append("SQL");
		String classname = sb.toString();
		try {
			Class<?> _class = Class.forName(classname);
			Logger.info("REFLECT METHOD", classname + "." + "copy(String type, int typeid, int newid, int userid, String ip)");
			Method _method = _class.getDeclaredMethod("copy", new Class[]{String.class, int.class, int.class, int.class, String.class});
			result = (String) _method.invoke(null, new Object[]{type, typeid, newid, userid, ip});
		}
		catch (Exception e) {
			try {
				Class<?> _class = Class.forName(classname);
				Logger.info("REFLECT METHOD", classname + "." + "copy(String moduleid, String type, int typeid, int newid, int userid, String ip)");
				Method _method = _class.getDeclaredMethod("copy", new Class[]{String.class, String.class, int.class, int.class, int.class, String.class});
				result = (String) _method.invoke(null, new Object[]{moduleid, type, typeid, newid, userid, ip});
			}
			catch (Exception e1) {
				result = "";
			}
		}
		return result;
	}

	public static String getTableRef(String type) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("Fields");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".TABLE_REF");
			Class<?> _class = Class.forName(classname);
			result = (String) _class.getField("TABLE_REF").get(null);
		}
		catch (Exception e) { result = type; }
		return result.toUpperCase();
	}

	public static String getMainTableRef(String type) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("Fields");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".MAIN_TABLE_REF");
			Class<?> _class = Class.forName(classname);
			result = (String) _class.getField("MAIN_TABLE_REF").get(null);
		}
		catch (Exception e) { result = type; }
		return result.toUpperCase();
	}

	public static String getReferenceRef(String type) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("Fields");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".MAIN_TABLE_REF");
			Class<?> _class = Class.forName(classname);
			result = (String) _class.getField("FIELD_REFERENCE_REF").get(null);
		}
		catch (Exception e) { result = type; }
		return result.toUpperCase();
	}

	public static String getTypeTableRef(String type) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("Fields");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".TYPE_TABLE_REF");
			Class<?> _class = Class.forName(classname);
			result = (String) _class.getField("TYPE_TABLE_REF").get(null);
		}
		catch (Exception e) {
			sb = new StringBuilder();
			sb.append("LKUP_").append(getTableRef(type)).append("_TYPE");
			result = sb.toString();
		}
		return result.toUpperCase();
	}

	public static String getFieldIdRef(String type) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("Fields");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".FIELD_ID_REF");
			Class<?> _class = Class.forName(classname);
			result = (String) _class.getField("FIELD_ID_REF").get(null);
		}
		catch (Exception e) { 
			sb = new StringBuilder();
			sb.append(type.toUpperCase()).append("_ID");
			result = sb.toString();
		}
		return result;
	}

	public static String getTableType(String type, String grouptype) {
		String module = grouptype;
		if (!Operator.hasValue(grouptype)) {
			module = type;
		}
		String result = "";
		try {
			StringBuilder sb =  new StringBuilder();
			sb.append("csapi.impl.").append(module.toLowerCase()).append(".").append(Operator.toTitleCase(module)).append("Fields");
			String classname = sb.toString();
			Logger.info("REFLECT METHOD", classname + ".TABLE_TYPE");
			Class<?> _class = Class.forName(classname);
			result = (String) _class.getField("TABLE_TYPE").get(null);
		}
		catch (Exception e) { result = ""; }
		return result.toLowerCase();
	}

	public static ObjGroupVO getSummaryFields(String type, String grouptype) {
		return uiFields(type, grouptype, "summary");
	}

	public static ObjGroupVO getHistoryFields(String type, String grouptype) {
		return uiFields(type, grouptype, "history");
	}

	public static ObjGroupVO getIdFields(String type, String grouptype) {
		return uiFields(type, grouptype, "id");
	}

	public static ObjGroupVO getExtFields(String type, String grouptype) {
		return uiFields(type, grouptype, "ext");
	}

	public static ObjGroupVO getInfoFields(String type, String grouptype) {
		return uiFields(type, grouptype, "info");
	}

	public static ObjGroupVO getListFields(String type, String grouptype) {
		return uiFields(type, grouptype, "list");
	}

	public static ObjGroupVO getMyFields(String type, String grouptype) {
		return uiFields(type, grouptype, "my");
	}

	public static ObjGroupVO getFullFields(String type, String grouptype) {
		return uiFields(type, grouptype, "full");
	}

	public static ObjGroupVO getUiFields(String type, String grouptype, String ui) {
		if (ui.equalsIgnoreCase("summary")) { return getSummaryFields(type, grouptype); }
		else if (ui.equalsIgnoreCase("info")) { return getInfoFields(type, grouptype); }
		else if (ui.equalsIgnoreCase("list")) { return getListFields(type, grouptype); }
		else if (ui.equalsIgnoreCase("my")) { return getMyFields(type, grouptype); }
		else if (ui.equalsIgnoreCase("full")) { return getFullFields(type, grouptype); }
		else if (ui.equalsIgnoreCase("history")) { return getHistoryFields(type, grouptype); }
		else if (ui.equalsIgnoreCase("id")) { return getIdFields(type, grouptype); }
		else if (ui.equalsIgnoreCase("ext")) { return getExtFields(type, grouptype); }
		else { return new ObjGroupVO(); }
	}
	
	public static int getCacheInterval() {
		return  CsApiConfig.getCacheInterval();
	}

	public static int getCacheInterval(String module) {
		int result = 0;
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(module.toLowerCase()).append(".").append(Operator.toTitleCase(module)).append("Fields");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".CACHE_INTERVAL");
			Class<?> _class = Class.forName(classname);
			result = (int) _class.getField("CACHE_INTERVAL").get(null);
		}
		catch (Exception e) { result = CsApiConfig.getCacheInterval(); }
		return result;
	}

	public static ObjGroupVO uiFields(String type, String grouptype, String field) {
		String module = grouptype;
		if (!Operator.hasValue(grouptype)) {
			module = type;
		}
		ObjGroupVO result = new ObjGroupVO();
		try {
			StringBuilder sb =  new StringBuilder();
			sb.append("csapi.impl.").append(module.toLowerCase()).append(".").append(Operator.toTitleCase(module)).append("Fields");
			String classname = sb.toString();
			Logger.info("REFLECT METHOD", classname + "." + field.toLowerCase() + "()");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod(field.toLowerCase());
			result = (ObjGroupVO) _method.invoke(null);
		}
		catch (Exception e) { }
		return result;
	}

	public static ObjGroupVO myActiveFields(String type, String grouptype) {
		String module = grouptype;
		if (!Operator.hasValue(grouptype)) {
			module = type;
		}
		ObjGroupVO result = new ObjGroupVO();
		try {
			StringBuilder sb =  new StringBuilder();
			sb.append("csapi.impl.").append(module.toLowerCase()).append(".").append(Operator.toTitleCase(module)).append("Fields");
			String classname = sb.toString();
			Logger.info("REFLECT METHOD", classname + ".myActive()");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod("myActive");
			result = (ObjGroupVO) _method.invoke(null);
		}
		catch (Exception e) {
			try {
				StringBuilder sb =  new StringBuilder();
				sb.append("csapi.impl.").append(module.toLowerCase()).append(".").append(Operator.toTitleCase(module)).append("Fields");
				String classname = sb.toString();
				Logger.info("REFLECT METHOD", classname + ".my()");
				Class<?> _class = Class.forName(classname);
				Method _method = _class.getDeclaredMethod("my");
				result = (ObjGroupVO) _method.invoke(null);
			}
			catch (Exception e1) {
				
			}
		}
		return result;
	}

	public static ObjGroupVO idFields(String module) {
		ObjGroupVO result = new ObjGroupVO();
		try {
			StringBuilder sb =  new StringBuilder();
			sb.append("csapi.impl.").append(module.toLowerCase()).append(".").append(Operator.toTitleCase(module)).append("Fields");
			String classname = sb.toString();
			Logger.info("REFLECT METHOD", classname + ".id()");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod("id");
			result = (ObjGroupVO) _method.invoke(null);
		}
		catch (Exception e) { }
		return result;
	}

	public static ObjGroupVO getDetailFields(String grouptype) {
		return getDetailFields(grouptype, grouptype);
	}

	public static ObjGroupVO getDetailFields(String type, String grouptype) {
		String module = grouptype;
		if (!Operator.hasValue(grouptype)) {
			module = type;
		}
		ObjGroupVO result = new ObjGroupVO();
		try {
			StringBuilder sb =  new StringBuilder();
			sb.append("csapi.impl.").append(module.toLowerCase()).append(".").append(Operator.toTitleCase(module)).append("Fields");
			String classname = sb.toString();
			Logger.info("REFLECT METHOD", classname + ".details()");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod("details".toLowerCase());
			result = (ObjGroupVO) _method.invoke(null);
		}
		catch (Exception e) { }
		return result;
	}

	public static ObjGroupVO getSearchFields(String type, String grouptype) {
		String module = grouptype;
		if (!Operator.hasValue(grouptype)) {
			module = type;
		}
		ObjGroupVO result = new ObjGroupVO();
		try {
			StringBuilder sb =  new StringBuilder();
			sb.append("csapi.impl.").append(module.toLowerCase()).append(".").append(Operator.toTitleCase(module)).append("Fields");
			String classname = sb.toString();
			Logger.info("REFLECT METHOD", classname + ".search()");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod("search".toLowerCase());
			result = (ObjGroupVO) _method.invoke(null);
		}
		catch (Exception e) { }
		return result;
	}

	public static String commonQuery(String method, RequestVO r, Token u) {
		return commonQuery(method, r, u, 0);
	}

	public static String commonQuery(String method, RequestVO r, Token u, int id) {
		String impl = r.getGrouptype();
		StringBuilder m = new StringBuilder();
		m.append(method).append(Operator.toTitleCase(impl));
		method = m.toString();
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(impl.toLowerCase()).append(".").append(Operator.toTitleCase(impl)).append("SQL");
		String classname = sb.toString();
		try {
			Class<?> _class = Class.forName(classname);
			if(id>0){
				Logger.info("REFLECT METHOD", classname + "." + method + "(RequestVO r, Token u, int id)");
				Method _method = _class.getDeclaredMethod(method, new Class[]{RequestVO.class, Token.class, int.class});
				result = (String) _method.invoke(null, new Object[]{r, u, id});
			}
			else {
				Logger.info("REFLECT METHOD", classname + "." + method + "(RequestVO r, Token u)");
				Method _method = _class.getDeclaredMethod(method, new Class[]{RequestVO.class, Token.class});
				result = (String) _method.invoke(null, new Object[]{r, u});
			}
			
		}
		catch (Exception e) { }
		return result;
	}

	public static String updateValQuery(String type, RequestVO r, Token u) {
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("SQL");
		String classname = sb.toString();
		try {
			Class<?> _class = Class.forName(classname);
			Logger.info("REFLECT METHOD", classname + ".updateVal(RequestVO vo, Token u)");
			Method _method = _class.getDeclaredMethod("updateVal", new Class[]{RequestVO.class, Token.class});
			result = (String) _method.invoke(null, new Object[]{r, u});
		}
		catch (Exception e) { }
		return result;
	}

	public static String deleteRefQuery(String type, int typeid, String grouptype, int id, int userid) {
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(grouptype.toLowerCase()).append(".").append(Operator.toTitleCase(grouptype)).append("SQL");
		String classname = sb.toString();
		String result = "";
		try {
			Logger.info("REFLECT METHOD", classname + ".deleteRef(String type, int typeid, int id, int userid)");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod("deleteRef", new Class[]{String.class, int.class, int.class, int.class});
			result = (String) _method.invoke(null, new Object[]{type,typeid,id,userid});
		}
		catch (Exception e) { }
		return result;
	}
	
	public static String deleteQuery(String type, int id, int userid) {
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(type.toLowerCase()).append(".").append(Operator.toTitleCase(type)).append("SQL");
		String classname = sb.toString();
		String result = "";
		try {
			Logger.info("REFLECT METHOD", classname + ".delete(int id, int userid)");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod("delete", new Class[]{int.class, int.class});
			result = (String) _method.invoke(null, new Object[]{id,userid});
		}
		catch (Exception e) { }
		return result;
	}
	
	public static String getSystemGenerated(RequestVO r,String field) {
		String impl = r.getGrouptype();
		StringBuilder m = new StringBuilder();
		m.append("getSystemGenerated").append(Operator.toTitleCase(field));
		String method = m.toString();
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(impl.toLowerCase()).append(".").append(Operator.toTitleCase(impl)).append("Agent");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + "." + method + "(RequestVO r)");
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod(method, new Class[]{RequestVO.class});
			result = (String) _method.invoke(null, new Object[]{r});
			
		}
		catch (Exception e) { }
		return result;
	}

	public static boolean addHistory(String type, int typeid, String module, int moduleid, String moduleaction) {
		if (Operator.equalsIgnoreCase(type, "activity")) {
			return ActivityAgent.addHistory(typeid, module, moduleid, moduleaction);
		}
		else if (Operator.equalsIgnoreCase(type, "project")) {
			return ProjectAgent.addHistory(typeid, module, moduleid, moduleaction);
		}
		return false;
	}
	
	
	/*public static String insertCommonQuery(ObjVO[] obj, int userId, String grouptype, String ip) {
		if (!Operator.hasValue(grouptype)) { return ""; }
		String impl = grouptype;
		StringBuilder m = new StringBuilder();
		m.append("insert").append(Operator.toTitleCase(impl));
		String method = m.toString();
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(impl).append(".").append(Operator.toTitleCase(impl)).append("SQL");
		String classname = sb.toString();
		try {
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod(method, new Class[]{ObjVO[].class, int.class});
			result = (String) _method.invoke(null, new Object[]{obj, userId});
		}
		catch (Exception e) { e.printStackTrace(); }
		return result;
	}
	
	public static String getRefIdCommonQuery(ObjVO[] obj, int userId, String grouptype) {
		if (!Operator.hasValue(grouptype)) { return ""; }
		String impl = grouptype;
		StringBuilder m = new StringBuilder();
		m.append("getRef").append(Operator.toTitleCase(impl)).append("Id");
		String method = m.toString();
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(impl).append(".").append(Operator.toTitleCase(impl)).append("SQL");
		String classname = sb.toString();
		try {
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod(method, new Class[]{ObjVO[].class, int.class});
			result = (String) _method.invoke(null, new Object[]{obj, userId});
		}
		catch (Exception e) { e.printStackTrace(); }
		return result;
	}
	
	public static String insertRefIdCommonQuery(String type, int typeId, int id, int userId,String grouptype) {
		if (!Operator.hasValue(grouptype)) { return ""; }
		String impl = grouptype;
		StringBuilder m = new StringBuilder();
		m.append("insertRefCommon").append(Operator.toTitleCase(impl)).append("");
		String method = m.toString();
		String result = "";
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.impl.").append(impl).append(".").append(Operator.toTitleCase(impl)).append("SQL");
		String classname = sb.toString();
		try {
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod(method, new Class[]{String.class, int.class, int.class,int.class, String.class});
			result = (String) _method.invoke(null, new Object[]{type, typeId, id, userId, grouptype});
		}
		catch (Exception e) { e.printStackTrace(); }
		return result;
	}*/

}
