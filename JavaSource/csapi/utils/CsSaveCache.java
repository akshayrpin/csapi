package csapi.utils;

import java.util.ArrayList;

import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import csapi.impl.entity.EntityAgent;
import csshared.vo.ObjGroupVO;
import csshared.vo.TypeInfo;
import csshared.vo.lkup.RolesVO;

public class CsSaveCache {

	public static void saveSummary(String type, int typeid, String module, String cache) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		saveCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid(), module, "summary", cache);
	}

	public static void saveInfo(String type, int typeid, String module, String cache) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		saveCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid(), module, "info", cache);
	}

	public static void saveCache(String type, int typeid, String module, String cache) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		saveCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid(), module, cache);
	}

	public static void saveCache(String type, int typeid, String module, String page, String cache) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		saveCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid(), module, page, cache);
	}

	public static void saveCache(String entity, int entityid, int projectid, int activityid, String module, String cache) {
		saveCache(entity, entityid, projectid, activityid, module, "summary", cache);
		saveCache(entity, entityid, projectid, activityid, module, "info", cache);
	}

	public static void saveCache(String entity, int entityid, int projectid, int activityid, String module, String page, String cache) {
		String path = CsApiConfig.getCachePath(entity, entityid, projectid, activityid, module, page);
		FileUtil.saveCache(path, cache);
		Logger.highlight("REFRESHED CACHE", "Refreshed cache of "+module+" - "+path);
	}

	public static void saveCache(String type, int typeid, String grouptype, String page, ObjGroupVO vo) {
		String c = CsTools.toJson(vo);
		saveCache(type, typeid, grouptype, page, c);
	}

	public static void saveCache(String type, int typeid, String grouptype, String page, ArrayList<ObjGroupVO> vo) {
		String c = CsTools.toJson(vo);
		saveCache(type, typeid, grouptype, page, c);
	}

	public static void saveTabRolesCache(String tab, RolesVO vo) {
		String path = CsApiConfig.getTabRoleCachePath(tab);
		String c = CsTools.toJson(vo);
		FileUtil.saveCache(path, c);
	}

	public static void saveAdminRolesCache(String admin, RolesVO vo) {
		String path = CsApiConfig.getAdminRoleCachePath(admin);
		String c = CsTools.toJson(vo);
		FileUtil.saveCache(path, c);
	}

	public static void saveModuleRolesCache(String module, RolesVO vo) {
		String path = CsApiConfig.getModuleRoleCachePath(module);
		String c = CsTools.toJson(vo);
		FileUtil.saveCache(path, c);
	}

	public static void saveCustomRolesCache(int customid, RolesVO vo) {
		String path = CsApiConfig.getCustomRoleCachePath(customid);
		String c = CsTools.toJson(vo);
		FileUtil.saveCache(path, c);
	}

	public static void saveReviewRolesCache(int reviewgroupid, RolesVO vo) {
		String path = CsApiConfig.getReviewRoleCachePath(reviewgroupid);
		String c = CsTools.toJson(vo);
		FileUtil.saveCache(path, c);
	}

	public static void saveProjectRolesCache(int projtypeid, RolesVO vo) {
		String path = CsApiConfig.getProjectRoleCachePath(projtypeid);
		String c = CsTools.toJson(vo);
		FileUtil.saveCache(path, c);
	}

	public static void saveActivityRolesCache(int acttypeid, RolesVO vo) {
		String path = CsApiConfig.getActivityRoleCachePath(acttypeid);
		String c = CsTools.toJson(vo);
		FileUtil.saveCache(path, c);
	}



}













