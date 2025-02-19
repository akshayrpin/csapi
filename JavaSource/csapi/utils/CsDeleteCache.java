package csapi.utils;

import java.io.File;

import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.entity.EntityAgent;
import csshared.vo.TypeInfo;

public class CsDeleteCache {

	public static void runDeleteCache() {
		String path = CsApiConfig.getCachePath();
		FileUtil.deleteDir(path);
	}

	public static void deleteCache(String type, int typeid, String module, String page) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		deleteCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid(), module, page);
	}

	public static void deleteCache(String entity, int entityid, int projectid, int activityid, String module, String page) {
		String path = CsApiConfig.getCachePath(entity, entityid, projectid, activityid, module, page);
		if (Operator.hasValue(path)) {
			alain.core.utils.FileUtil.deleteCache(path);
			Logger.highlight("DELETED CACHE", "Deleted cache of "+module+" - "+path);
		}
	}

	public static void deleteCache(String type, int typeid, String module) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		deleteCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid(), module);
	}

	public static void deleteCache(String entity, int entityid, int projectid, int activityid, String module) {
		deleteCache(entity, entityid, projectid, activityid, module, "summary");
		deleteCache(entity, entityid, projectid, activityid, module, "info");
	}

	public static void deleteTypeCache(String entity, int entityid, int projectid, int activityid) {
		String summarypath = CsApiConfig.getCachePath(entity, entityid, projectid, activityid, "", "summary");
		String infopath = CsApiConfig.getCachePath(entity, entityid, projectid, activityid, "", "info");
		if (Operator.hasValue(summarypath)) {
			alain.core.utils.FileUtil.deleteDir(summarypath);
		}
		if (Operator.hasValue(infopath)) {
			alain.core.utils.FileUtil.deleteDir(infopath);
		}
		Logger.highlight("DELETED CACHE", "Deleted cache "+summarypath+" and "+infopath);
	}

	public static void deleteTypeCache(String type, int typeid) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		deleteTypeCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid());
	}

	public static void deleteCacheDir(String type, int typeid) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		deleteCacheDir(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid());
	}

	public static void deleteCacheDir(String entity, int entityid, int projectid, int activityid) {
		String path = CsApiConfig.getCacheDir(entity, entityid, projectid, activityid);
		if (Operator.hasValue(path)) {
			alain.core.utils.FileUtil.deleteDir(path);
		}
	}

	public static void deleteRootCache(String type, int typeid) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		deleteEntityCache(entity.getEntity(), entity.getEntityid());
	}

	public static void deleteEntityCache(String entity, int entityid) {
		deleteCacheDir(entity, entityid, -1, -1);
	}

	public static void deleteProjectCache(String type, int typeid, String module) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		deleteProjectCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid(), module);
	}

	public static void deleteProjectChildCache(String type, int typeid, String module) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		deleteChildCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), -1, module);
	}

	public static void deleteProjectCache(String entity, int entityid, int projectid, int activityid, String module) {
		if (projectid > 0) {
			deleteCache(entity, entityid, projectid, -1, module);
		}
	}

	public static void deleteProjectAndActivityCache(String type, int typeid, String module) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		deleteProjectAndActivityCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid(), module);
	}

	public static void deleteProjectAndActivityCache(String entity, int entityid, int projectid, int activityid, String module) {
		if (projectid > 0) {
			deleteCache(entity, entityid, projectid, -1, module);
		}
		if (activityid > 0) {
			deleteCache(entity, entityid, projectid, activityid, module);
		}
	}

	public static void deleteProjectCache(String type, int typeid) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		deleteProjectCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid());
	}

	public static void deleteProjectCache(String entity, int entityid, int projectid) {
		deleteCacheDir(entity, entityid, projectid, -1);
	}

	public static void deleteChildCache(String type, int typeid) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		deleteChildCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid());
	}

	public static void deleteChildCache(String entity, int entityid, int projectid, int activityid) {
		deleteCacheDir(entity, entityid, projectid, activityid);
	}

	public static void deleteChildCache(String type, int typeid, String module) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		deleteChildCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid(), module);
	}

	public static void deleteChildCache(String entity, int entityid, int projectid, int activityid, String module) {
		String path = CsApiConfig.getCacheDir(entity, entityid, projectid, activityid);
		File f = new File(path);
		deleteChildCache(f, module);
	}

	public static void deleteChildCache(File dir, String module) {
		if (dir.isDirectory()) {
			StringBuilder sb = new StringBuilder();
			sb.append(module.toLowerCase()).append(".json");
			String txt = sb.toString();
			File[] files = dir.listFiles();
			for(File file: files){
				if (file.isDirectory()) {
					deleteChildCache(file, module);
				}
				else {
					String n = file.getName();
					if (Operator.equalsIgnoreCase(txt, n)) {
						file.delete();
					}
				}
			}
		}
	}

	public static void deleteParentCache(String type, int typeid, String module) {
		TypeInfo entity = EntityAgent.getEntity(type, typeid);
		deleteParentCache(entity.getEntity(), entity.getEntityid(), entity.getProjectid(), entity.getActivityid(), module);
	}

	public static void deleteParentCache(String entity, int entityid, int projectid, int activityid, String module) {
		if (activityid > 0) {
			deleteCache(entity, entityid, projectid, activityid, module);
		}
		if (projectid > 0) {
			deleteCache(entity, entityid, projectid, -1, module);
		}
		if (entityid > 0) {
			deleteCache(entity, entityid, -1, -1, module);
		}
	}




	public static void deleteModuleRolesCache(String module) {
		String path = CsApiConfig.getModuleRoleCachePath(module);
		FileUtil.deleteCache(path);
	}

	public static void deleteCustomRolesCache(int customid) {
		String path = CsApiConfig.getCustomRoleCachePath(customid);
		FileUtil.deleteCache(path);
	}

	public static void deleteReviewRolesCache(int reviewgroupid) {
		String path = CsApiConfig.getReviewRoleCachePath(reviewgroupid);
		FileUtil.deleteCache(path);
	}

	public static void deleteProjectRolesCache(int projtypeid) {
		String path = CsApiConfig.getProjectRoleCachePath(projtypeid);
		FileUtil.deleteCache(path);
	}

	public static void deleteActivityRolesCache(int acttypeid) {
		String path = CsApiConfig.getActivityRoleCachePath(acttypeid);
		FileUtil.deleteCache(path);
	}

	public static void runDeleteRolesCache() {
		String path = CsApiConfig.getRoleCachePath();
		FileUtil.deleteDir(path);
	}


	public static void deleteCache() {
		try {
			new Thread(new Runnable() {
				public void run() {
					try { runDeleteCache(); }
					catch (Exception e) { }
                }
            }).start();
        }
        catch(Exception e) { }
    }
	
	public static void deleteRolesCache() {
		try {
			new Thread(new Runnable() {
				public void run() {
					try { runDeleteRolesCache(); }
					catch (Exception e) { }
                }
            }).start();
        }
        catch(Exception e) { }
    }
	











}













