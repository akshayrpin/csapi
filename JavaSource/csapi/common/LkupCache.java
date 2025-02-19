package csapi.common;

import alain.core.db.Sage;
import alain.core.utils.Operator;
import csapi.utils.CsGetCache;
import csapi.utils.CsSaveCache;
import csshared.vo.lkup.RolesVO;


public class LkupCache {

	public static RolesVO getTabRoles(String tab) {
		RolesVO r = new RolesVO();
		if (Operator.hasValue(tab)) {
			try { r = CsGetCache.getTabRolesCache(tab); }
			catch (Exception e) {
				String command = LkupCacheSQL.getTabRoles(tab);
				r = getRoles(command);
				CsSaveCache.saveTabRolesCache(tab, r);
			}
		}
		return r;
	}

	public static RolesVO getAdminRoles(String admin) {
		RolesVO r = new RolesVO();
		if (Operator.hasValue(admin)) {
			try { r = CsGetCache.getAdminRolesCache(admin); }
			catch (Exception e) {
				String command = LkupCacheSQL.getAdminRoles(admin);
				r = getRoles(command);
				CsSaveCache.saveAdminRolesCache(admin, r);
			}
		}
		return r;
	}

	public static RolesVO getModuleRoles(String module) {
		RolesVO r = new RolesVO();
		if (Operator.hasValue(module)) {
			try {
				r = CsGetCache.getModuleRolesCache(module);
			}
			catch (Exception e) {
				String command = LkupCacheSQL.getModuleRoles(module);
				r = getRoles(command);
				CsSaveCache.saveModuleRolesCache(module, r);
			}
		}
		return r;
	}


	public static RolesVO getCustomRoles(int customid) {
		RolesVO r = new RolesVO();
		if (customid > 0) {
			try { r = CsGetCache.getCustomRolesCache(customid); }
			catch (Exception e) {
				String command = LkupCacheSQL.getCustomRoles(customid);
				r = getRoles(command);
				CsSaveCache.saveCustomRolesCache(customid, r);
			}
		}
		return r;
	}


	public static RolesVO getReviewRoles(int reviewgroupid) {
		RolesVO r = new RolesVO();
		if (reviewgroupid > 0) {
			try { r = CsGetCache.getReviewRolesCache(reviewgroupid); }
			catch (Exception e) {
				String command = LkupCacheSQL.getReviewRoles(reviewgroupid);
				r = getRoles(command);
				CsSaveCache.saveReviewRolesCache(reviewgroupid, r);
			}
		}
		return r;
	}


	public static RolesVO getActivityRoles(int acttypeid) {
		RolesVO r = new RolesVO();
		if (acttypeid > 0) {
			try { r = CsGetCache.getActivityRolesCache(acttypeid); }
			catch (Exception e) {
				String command = LkupCacheSQL.getActivityRoles(acttypeid);
				r = getRoles(command);
				CsSaveCache.saveActivityRolesCache(acttypeid, r);
			}
		}
		return r;
	}

	public static RolesVO getProjectRoles(int projtypeid) {
		RolesVO r = new RolesVO();
		if (projtypeid > 0) {
			try { r = CsGetCache.getProjectRolesCache(projtypeid); }
			catch (Exception e) {
				String command = LkupCacheSQL.getProjectRoles(projtypeid);
				r = getRoles(command);
				CsSaveCache.saveProjectRolesCache(projtypeid, r);
			}
		}
		return r;
	}

	public static RolesVO getRoles(String command) {
		RolesVO r = new RolesVO();
		if (Operator.hasValue(command)) {
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				db.query(command);
				while (db.next()) {
					if (db.equalsIgnoreCase("REQUIRE_PUBLIC", "Y")) {
						r.requirePublic(db.getString("ROLE"));
					}
					if (db.equalsIgnoreCase("C", "Y")) {
						r.addCreate(db.getString("ROLE"));
						if (db.equalsIgnoreCase("EVERYONE", "Y")) {
							r.setPubcreate(true);
							if (db.equalsIgnoreCase("REQUIRE_PUBLIC", "N")) {
								r.setPubcreatepublic(false);
							}
						}
						else {
							r.setCreateempty(false);
							r.setEmpty(false);
						}
					}
					if (db.equalsIgnoreCase("R", "Y")) {
						r.addRead(db.getString("ROLE"));
						if (db.equalsIgnoreCase("EVERYONE", "Y")) {
							r.setPubread(true);
							if (db.equalsIgnoreCase("REQUIRE_PUBLIC", "N")) {
								r.setPubreadpublic(false);
							}
						}
						else {
							r.setReadempty(false);
							r.setEmpty(false);
						}
					}
					if (db.equalsIgnoreCase("U", "Y")) {
						r.addUpdate(db.getString("ROLE"));
						if (db.equalsIgnoreCase("EVERYONE", "Y")) {
							r.setPubupdate(true);
							if (db.equalsIgnoreCase("REQUIRE_PUBLIC", "N")) {
								r.setPubupdatepublic(false);
							}
						}
						else {
							r.setUpdateempty(false);
							r.setEmpty(false);
						}
					}
					if (db.equalsIgnoreCase("D", "Y")) {
						r.addDelete(db.getString("ROLE"));
						if (db.equalsIgnoreCase("EVERYONE", "Y")) {
							r.setPubdelete(true);
							if (db.equalsIgnoreCase("REQUIRE_PUBLIC", "N")) {
								r.setPubdeletepublic(false);
							}
						}
						else {
							r.setDeleteempty(false);
							r.setEmpty(false);
						}
					}
				}
				db.clear();
			}
		}
		return r;
	}

















}





































