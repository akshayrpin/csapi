package csapi.common;

import alain.core.utils.Operator;




public class LkupCacheSQL {

	public static String getTabRoles(String tab) {
		if (!Operator.hasValue(tab)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append("   R.LKUP_ROLES_ID AS ROLE ");
		sb.append("   ,  ");
		sb.append("   LR.EVERYONE  ");
		sb.append("   ,  ");
		sb.append("   R.C  ");
		sb.append("   ,  ");
		sb.append("   R.R  ");
		sb.append("   ,  ");
		sb.append("   R.U  ");
		sb.append("   ,  ");
		sb.append("   R.D  ");
		sb.append(" FROM ");
		sb.append("   REF_TAB_ROLES AS R ");
		sb.append("   JOIN LKUP_TAB AS T ON R.LKUP_TAB_ID = T.ID AND R.ACTIVE = 'Y' AND T.ACTIVE = 'Y' ");
		sb.append("   JOIN LKUP_ROLES AS LR ON R.LKUP_ROLES_ID = LR.ID AND LR.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append("   LOWER(T.TAB) = LOWER('").append(tab).append("') ");
		return sb.toString();
	}

	public static String getAdminRoles(String admin) {
		if (!Operator.hasValue(admin)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append("   R.LKUP_ROLES_ID AS ROLE ");
		sb.append("   ,  ");
		sb.append("   LR.EVERYONE  ");
		sb.append("   ,  ");
		sb.append("   R.C  ");
		sb.append("   ,  ");
		sb.append("   R.R  ");
		sb.append("   ,  ");
		sb.append("   R.U  ");
		sb.append("   ,  ");
		sb.append("   R.D  ");
		sb.append(" FROM ");
		sb.append("   REF_ADMIN_ROLES AS R ");
		sb.append("   JOIN LKUP_ADMIN_MODULE AS A ON R.LKUP_ADMIN_MODULE_ID = A.ID AND R.ACTIVE = 'Y' AND A.ACTIVE = 'Y' ");
		sb.append("   JOIN LKUP_ROLES AS LR ON R.LKUP_ROLES_ID = LR.ID AND LR.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append("   LOWER(A.NAME) = LOWER('").append(admin).append("') ");
		return sb.toString();
	}

	public static String getModuleRoles(String module) {
		if (!Operator.hasValue(module)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append("   R.LKUP_ROLES_ID AS ROLE ");
		sb.append("   ,  ");
		sb.append("   LR.EVERYONE  ");
		sb.append("   ,  ");
		sb.append("   R.C  ");
		sb.append("   ,  ");
		sb.append("   R.R  ");
		sb.append("   ,  ");
		sb.append("   R.U  ");
		sb.append("   ,  ");
		sb.append("   R.D  ");
		sb.append(" FROM ");
		sb.append("   REF_MODULE_ROLES AS R ");
		sb.append("   JOIN LKUP_MODULE AS M ON R.LKUP_MODULE_ID = M.ID AND R.ACTIVE = 'Y' AND M.ACTIVE = 'Y' ");
		sb.append("   JOIN LKUP_ROLES AS LR ON R.LKUP_ROLES_ID = LR.ID AND LR.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append("   LOWER(M.MODULE) = LOWER('").append(module).append("') ");
		return sb.toString();
	}


	public static String getCustomRoles(int customid) {
		if (customid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append("   R.LKUP_ROLES_ID AS ROLE ");
		sb.append("   ,  ");
		sb.append("   LR.EVERYONE  ");
		sb.append("   ,  ");
		sb.append("   R.C  ");
		sb.append("   ,  ");
		sb.append("   R.R  ");
		sb.append("   ,  ");
		sb.append("   R.U  ");
		sb.append("   ,  ");
		sb.append("   R.D  ");
		sb.append(" FROM ");
		sb.append("   REF_FIELD_GROUPS_ROLES AS R ");
		sb.append("   JOIN LKUP_ROLES AS LR ON R.LKUP_ROLES_ID = LR.ID AND LR.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append("   R.FIELD_GROUPS_ID = ").append(customid);
		sb.append("   AND ");
		sb.append("   R.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getReviewRoles(int reviewgroupid) {
		if (reviewgroupid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append("   R.LKUP_ROLES_ID AS ROLE ");
		sb.append("   ,  ");
		sb.append("   LR.EVERYONE  ");
		sb.append("   ,  ");
		sb.append("   R.C  ");
		sb.append("   ,  ");
		sb.append("   R.R  ");
		sb.append("   ,  ");
		sb.append("   R.U  ");
		sb.append("   ,  ");
		sb.append("   R.D  ");
		sb.append(" FROM ");
		sb.append("   REF_REVIEW_GROUP_ROLES AS R ");
		sb.append("   JOIN LKUP_ROLES AS LR ON R.LKUP_ROLES_ID = LR.ID AND LR.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append("   R.REVIEW_GROUP_ID = ").append(reviewgroupid);
		sb.append("   AND ");
		sb.append("   R.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getActivityRoles(int acttypeid) {
		if (acttypeid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append("   R.LKUP_ROLES_ID AS ROLE ");
		sb.append("   ,  ");
		sb.append("   LR.EVERYONE  ");
		sb.append("   ,  ");
		sb.append("   R.REQUIRE_PUBLIC  ");
		sb.append("   ,  ");
		sb.append("   R.C  ");
		sb.append("   ,  ");
		sb.append("   R.R  ");
		sb.append("   ,  ");
		sb.append("   R.U  ");
		sb.append("   ,  ");
		sb.append("   R.D  ");
		sb.append(" FROM ");
		sb.append("   REF_ACT_TYPE_ROLES AS R ");
		sb.append("   JOIN LKUP_ROLES AS LR ON R.LKUP_ROLES_ID = LR.ID AND LR.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append("   R.ACTIVE = 'Y' ");
		sb.append("   AND ");
		sb.append("   R.LKUP_ACT_TYPE_ID = ").append(acttypeid);
		return sb.toString();
	}

	public static String getProjectRoles(int projtypeid) {
		if (projtypeid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append("   R.LKUP_ROLES_ID AS ROLE ");
		sb.append("   ,  ");
		sb.append("   LR.EVERYONE  ");
		sb.append("   ,  ");
		sb.append("   R.C  ");
		sb.append("   ,  ");
		sb.append("   R.R  ");
		sb.append("   ,  ");
		sb.append("   R.U  ");
		sb.append("   ,  ");
		sb.append("   R.D  ");
		sb.append(" FROM ");
		sb.append("   REF_PROJECT_TYPE_ROLES AS R ");
		sb.append("   JOIN LKUP_ROLES AS LR ON R.LKUP_ROLES_ID = LR.ID AND LR.ACTIVE = 'Y' ");
		sb.append(" WHERE ");
		sb.append("   R.ACTIVE = 'Y' ");
		sb.append("   AND ");
		sb.append("   R.LKUP_PROJECT_TYPE_ID = ").append(projtypeid);
		return sb.toString();
	}















}





































