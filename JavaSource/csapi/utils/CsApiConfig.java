package csapi.utils;

import csapi.impl.entity.EntityAgent;
import csshared.vo.TypeInfo;
import alain.core.utils.Config;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;

public class CsApiConfig {

	public static String CONFIGFILE = "csapi.config.xml";
	public static final String SKIPVALUE = "--SKIP--";

	public static String getHistorySource() {
		return getString("history.datasource");
	}

	public static String getQrcodeDomain() {
		return getString("qrcode.domain");
	}

	public static String getApiPath() {
		return getString("api.path");
	}

	public static String getSummary() {
		return getString("jsp.summary");
	}

	public static String getForm() {
		return getString("jsp.form.default");
	}

	public static String getRepSalt() {
		return getString("representative.salt");
	}

	public static String getForm(String group) {
		if (!Operator.hasValue(group)) { return getForm(); }
		String f = getString("jsp.form."+group.toLowerCase());
		if (!Operator.hasValue(f)) { f = getForm(); }
		return f;
	}

	public static String getDetails() {
		return getString("jsp.details.default");
	}

	public static String getList() {
		return getString("jsp.list.default");
	}

	public static String getList(String group) {
		if (!Operator.hasValue(group)) { return getForm(); }
		String f = getString("jsp.list."+group.toLowerCase());
		if (!Operator.hasValue(f)) { f = getList(); }
		return f;
	}

	public static String getDomain(String entity) {
		StringBuilder sb = new StringBuilder();
		sb.append("entities.").append(entity).append(".domain");
		return getString(sb.toString());
	}

	public static String getImage(String img) {
		StringBuilder sb = new StringBuilder();
		sb.append("images.").append(img.toLowerCase());
		return getString(sb.toString());
	}

	public static String getImage(String color, String img) {
		StringBuilder sb = new StringBuilder();
		sb.append("images.");
		if (Operator.hasValue(color) && Operator.equalsIgnoreCase(color, "black")) {
			sb.append(color.toLowerCase()).append(".");
		}
		sb.append(img.toLowerCase());
		return getString(sb.toString());
	}

	public static String[] getEntities() {
		return getValues("entities.entity");
	}

	public static int getCacheInterval() {
		StringBuilder sb = new StringBuilder();
		sb.append("cache.interval");
		String s = getString(sb.toString());
		return Operator.toInt(s);
	}

	public static String getCachePath() {
		StringBuilder sb = new StringBuilder();
		sb.append("cache.path");
		return Operator.removeTrailingSlash(getString(sb.toString()));
	}

	public static String getCachePath(String type, int typeid, String module, String page) {
		if (!Operator.hasValue(type) || typeid < 1) { return ""; }
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		return getCachePath(entity, page, module);
	}

	public static String getCachePath(TypeInfo entity, String module, String page) {
		if (!Operator.hasValue(entity.getEntity()) || entity.getEntityid() < 1) { return ""; }
		return getCachePath(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid(), page, module);
	}

	public static String getCacheDir(String entity, int entityid, int projectid, int activityid) {
		return getCachePath(entity, entityid, projectid, activityid, "", "");
	}

	public static String getCachePath(String entity, int entityid, int projectid, int activityid, String module, String page) {
		if (!Operator.hasValue(entity) || entityid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(getCachePath());
		sb.append("/").append(FileUtil.simplifyFilename(entity.toLowerCase()));
		sb.append("/").append(entityid);
		if (projectid < 1) {
			if (Operator.hasValue(page)) {
				sb.append("/").append(FileUtil.simplifyFilename(page.toLowerCase()));
			}
			if (Operator.hasValue(module)) {
				sb.append("/").append(FileUtil.simplifyFilename(module.toLowerCase())).append(".json");
			}
		}
		else {
			sb.append("/").append(projectid);
			if (activityid < 1) {
				if (Operator.hasValue(page)) {
					sb.append("/").append(FileUtil.simplifyFilename(page.toLowerCase()));
				}
				if (Operator.hasValue(module)) {
					sb.append("/").append(FileUtil.simplifyFilename(module.toLowerCase())).append(".json");
				}
			}
			else {
				sb.append("/").append(activityid);
				if (Operator.hasValue(page)) {
					sb.append("/").append(FileUtil.simplifyFilename(page.toLowerCase()));
				}
				if (Operator.hasValue(module)) {
					sb.append("/").append(FileUtil.simplifyFilename(module.toLowerCase())).append(".json");
				}
			}
		}
		return sb.toString();
	}

	public static String getTabRoleCachePath(String tab) {
		StringBuilder sb = new StringBuilder();
		sb.append(getCachePath());
		sb.append("/roles");
		sb.append("/tab");
		sb.append("/").append(FileUtil.simplifyFilename(tab.toLowerCase())).append(".json");
		return sb.toString();
	}

	public static String getAdminRoleCachePath(String admin) {
		StringBuilder sb = new StringBuilder();
		sb.append(getCachePath());
		sb.append("/roles");
		sb.append("/admin");
		sb.append("/").append(FileUtil.simplifyFilename(admin.toLowerCase())).append(".json");
		return sb.toString();
	}

	public static String getModuleRoleCachePath(String module) {
		StringBuilder sb = new StringBuilder();
		sb.append(getCachePath());
		sb.append("/roles");
		sb.append("/module");
		sb.append("/").append(FileUtil.simplifyFilename(module.toLowerCase())).append(".json");
		return sb.toString();
	}

	public static String getCustomRoleCachePath(int customid) {
		StringBuilder sb = new StringBuilder();
		sb.append(getCachePath());
		sb.append("/roles");
		sb.append("/custom");
		sb.append("/").append(customid).append(".json");
		return sb.toString();
	}

	public static String getReviewRoleCachePath(int reviewgroupid) {
		StringBuilder sb = new StringBuilder();
		sb.append(getCachePath());
		sb.append("/roles");
		sb.append("/review");
		sb.append("/").append(reviewgroupid).append(".json");
		return sb.toString();
	}

	public static String getProjectRoleCachePath(int projtypeid) {
		StringBuilder sb = new StringBuilder();
		sb.append(getCachePath());
		sb.append("/roles");
		sb.append("/project");
		sb.append("/").append(projtypeid).append(".json");
		return sb.toString();
	}

	public static String getActivityRoleCachePath(int acttypeid) {
		StringBuilder sb = new StringBuilder();
		sb.append(getCachePath());
		sb.append("/roles");
		sb.append("/activity");
		sb.append("/").append(acttypeid).append(".json");
		return sb.toString();
	}

	public static String getRoleCachePath() {
		StringBuilder sb = new StringBuilder();
		sb.append(getCachePath());
		sb.append("/roles");
		return sb.toString();
	}




//	public static MapSet getEntity(String entity) {
//		MapSet map = new MapSet();
//		map.set("entity", entity);
//		map.set("type", entity);
//		StringBuilder sb = new StringBuilder();
//		sb.append("entities.").append(entity);
//		String n = sb.toString();
//
//		sb = new StringBuilder().append(n).append(".menuid");
//		System.out.println(sb.toString());
//		String menuid = getString(sb.toString());
//		map.set("menuid", menuid);
//
//		sb = new StringBuilder().append(n).append(".title");
//		String title = getString(sb.toString());
//		map.set("title", title);
//
//		sb = new StringBuilder().append(n).append(".domain");
//		String domain = getString(sb.toString());
//		map.set("domain", domain);
//
//		sb = new StringBuilder().append(n).append(".image");
//		String image = getString(sb.toString());
//		map.set("image", image);
//
//		sb = new StringBuilder().append(n).append(".main");
//		String main = getString(sb.toString());
//		if (Operator.hasValue(main)) {
//			map.set("main", main);
//		}
//
//		sb = new StringBuilder().append(n).append(".sub");
//		String sub = getString(sb.toString());
//		if (Operator.hasValue(sub)) {
//			map.set("sub", sub);
//		}
//
//		sb = new StringBuilder().append(n).append(".link");
//		String link = getString(sb.toString());
//		if (Operator.hasValue(link)) {
//			map.set("link", link);
//		}
//
//		sb = new StringBuilder().append(n).append(".admin");
//		String admin = getString(sb.toString());
//		if (Operator.hasValue(admin)) {
//			map.set("admin", admin);
//		}
//
//		return map;
//	}

	/**
	 * Get the value of specified node
	 * @param nodename - the name of the node to retrieve
	 * @return value of specified node
	 */
	public static String getString(String nodename) {
		return Config.getString(CONFIGFILE, nodename);
	}

	/**
	 * Get the number of instances of specified node
	 * @param nodename - the name of the node 
	 * @return number of instances of specified node
	 */
	public static int size(String nodename) {
		return Config.size(CONFIGFILE, nodename);
	}

	/**
	 * Get a MapSet object containing values of specified nodenames
	 * @param nodenames - a list of nodenames to retrieve
	 * @return MapSet object containing values of specified nodes
	 */
	public static MapSet getConfig(String[] nodenames) {
		return Config.getConfig(CONFIGFILE, nodenames);
	}

	public static String importTool(String tool) {
		return Config.importTool(CONFIGFILE, tool);
	}

	public static String[] getValues(String node) {
		return Config.getValues(CONFIGFILE, node);
	}
	


}