package csapi.utils;

import java.util.ArrayList;

import csapi.impl.entity.EntityAgent;
import csshared.vo.ObjGroupVO;
import csshared.vo.TypeInfo;
import csshared.vo.lkup.RolesVO;
import alain.core.utils.FileUtil;
import alain.core.utils.Logger;

public class CsGetCache {

	public static String getSummary(String type, int typeid, String module) throws Exception {
		return getCache(type, typeid, module, "summary");
	}

	public static String getInfo(String type, int typeid, String module) throws Exception {
		return getCache(type, typeid, module, "info");
	}

	public static String getCache(String type, int typeid, String module) throws Exception {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		return getCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid(), module, "summary");
	}

	public static String getCache(String type, int typeid, String module, String page) throws Exception {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		return getCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid(), module, page);
	}

	public static String getCache(String entity, int entityid, int projectid, int activityid, String module, String page) throws Exception {
		String r = "";
		String path = CsApiConfig.getCachePath(entity, entityid, projectid, activityid, module, page);
		r = FileUtil.getCache(path, CsReflect.getCacheInterval(module));
		Logger.info("GET CACHE", "Retrieved cache of "+module+" - "+path);
		return r;
	}

	public static ObjGroupVO getObjCache(String type, int typeid, String grouptype, String page) throws Exception {
		String c = getCache(type, typeid, grouptype, page);
		return CsTools.toGroup(c);
	}

	public static ArrayList<ObjGroupVO> getObjArrayCache(String type, int typeid, String grouptype, String page) throws Exception {
		String c = getCache(type, typeid, grouptype, page);
		return CsTools.toGroupArray(c);
	}

	public static RolesVO getTabRolesCache(String tab) throws Exception {
		String path = CsApiConfig.getTabRoleCachePath(tab);
		Logger.highlight(path);
		String s = FileUtil.getCache(path, CsReflect.getCacheInterval());
		return CsTools.toRoles(s);
	}

	public static RolesVO getAdminRolesCache(String admin) throws Exception {
		String path = CsApiConfig.getAdminRoleCachePath(admin);
		Logger.highlight(path);
		String s = FileUtil.getCache(path, CsReflect.getCacheInterval());
		return CsTools.toRoles(s);
	}

	public static RolesVO getModuleRolesCache(String module) throws Exception {
		String path = CsApiConfig.getModuleRoleCachePath(module);
		String s = FileUtil.getCache(path, CsReflect.getCacheInterval());
		return CsTools.toRoles(s);
	}

	public static RolesVO getCustomRolesCache(int customid) throws Exception {
		String path = CsApiConfig.getCustomRoleCachePath(customid);
		String s = FileUtil.getCache(path, CsReflect.getCacheInterval());
		return CsTools.toRoles(s);
	}

	public static RolesVO getReviewRolesCache(int reviewgroupid) throws Exception {
		String path = CsApiConfig.getReviewRoleCachePath(reviewgroupid);
		String s = FileUtil.getCache(path, CsReflect.getCacheInterval());
		return CsTools.toRoles(s);
	}

	public static RolesVO getActivityRolesCache(int acttypeid) throws Exception {
		String path = CsApiConfig.getActivityRoleCachePath(acttypeid);
		String s = FileUtil.getCache(path, CsReflect.getCacheInterval());
		return CsTools.toRoles(s);
	}

	public static RolesVO getProjectRolesCache(int projtypeid) throws Exception {
		String path = CsApiConfig.getProjectRoleCachePath(projtypeid);
		String s = FileUtil.getCache(path, CsReflect.getCacheInterval());
		return CsTools.toRoles(s);
	}


}













