package csapi.impl.library;

import alain.core.security.Token;
import alain.core.utils.Operator;
import csapi.common.Table;
import csapi.impl.entity.EntityAgent;
import csapi.impl.general.GeneralSQL;
import csapi.utils.CsReflect;
import csshared.vo.RequestVO;
import csshared.vo.TypeInfo;

public class LibrarySQL {

	public static String info(String type, int typeid, int id) {
		return details(type, typeid, -1);
	}

	public static String summary(String type, int typeid, int id) {
		return details(type, typeid, -1);
	}

	public static String details(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		
		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" A.TXT ");
		sb.append(" , ");
		sb.append(" A.LIBRARY_ID ");
		sb.append(" , ");
		sb.append(" A.LIBRARY_CODE ");
		sb.append(" , ");
		sb.append(" A.INSPECTABLE ");
		sb.append(" , ");
		sb.append(" A.WARNING ");
		sb.append(" , ");
		sb.append(" A.COMPLETE ");
		sb.append(" , ");
		sb.append(" A.REQUIRED ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_LIBRARY A ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU ON A.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP ON A.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" A.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" AND ");
		sb.append(" A.ACTIVE = 'Y' ");
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" A.ID = ").append(id);
		}
		sb.append(" ORDER BY A.UPDATED_DATE DESC ");
		return sb.toString();
	}
	
	
	
	public static String updateLibrary(RequestVO r, Token u) {
		String tableref = CsReflect.getTableRef(r.getType())+"_LIBRARY";
		return GeneralSQL.updateCommon(tableref,r,u);
	}
	
	
	public static String insertLibrary(RequestVO r, Token u) {
		String tableref = CsReflect.getTableRef(r.getType())+"_LIBRARY";;
		return GeneralSQL.insertCommon(tableref,r, u);
	}

	
	/*public static String getRefIdLibrary(RequestVO r, UserVO u) {
		StringBuilder sb = new StringBuilder();
		String tableref = CsReflect.getTableRef(r.getType())+"_LIBRARY";;
		sb.append(" select TOP 1 ID FROM ").append(tableref).append(" ");
		sb.append(" WHERE ID >0  ");
		sb.append(" AND ");
		sb.append(" PATH = '").append(Operator.sqlEscape(obj[2].getValue())).append("' ");
		sb.append(" AND ");
		sb.append(" CREATED_BY = ").append(u.getUserId());
		sb.append(" order by CREATED_DATE DESC ");
		return sb.toString();
	}
	
	public static String insertRefLibrary(RequestVO r, UserVO u, int id) {
		return GeneralSQL.insertRefCommon(r,u,id);
	}*/

	
	//LIBRARY
	/*select L.* from REF_ACT_LIBRARY_GROUP RG
	join REF_LIBRARY RL on RG.LIBRARY_GROUP_ID= RL.LIBRARY_GROUP_ID
	join LKUP_LIBRARY L on RL.LKUP_LIBRARY_ID= L.ID
	where LKUP_ACT_TYPE_ID =27*/

	public static String getGroup(int groupid, String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		TypeInfo t = EntityAgent.getEntity(type, typeid);
		int lsoid = t.getEntityid();
		int parentid = t.getParentid();
		int grandparentid = t.getGrandparentid();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" L.ID ");
		sb.append(" , ");
		sb.append(" L.NAME ");
		sb.append(" , ");
		sb.append(" L.LEVEL_TYPE ");
		sb.append(" , ");
		sb.append(" L.LIBRARY_TYPE_ID ");
		sb.append(" , ");
		sb.append(" L.TITLE ");
		sb.append(" , ");
		sb.append(" L.TXT ");
		sb.append(" , ");
		sb.append(" L.LIBRARY_CODE ");
		sb.append(" , ");
		sb.append(" L.LIBRARY_DESC ");
		sb.append(" , ");
		sb.append(" L.INSPECTABLE ");
		sb.append(" , ");
		sb.append(" L.WARNING ");
		sb.append(" , ");
		sb.append(" L.COMPLETE ");
		sb.append(" , ");
		sb.append(" L.REQUIRED ");
		sb.append(" , ");
		sb.append(" L.ORDR ");
		sb.append(" , ");
		sb.append(" L.ACTIVE ");
		sb.append(" , ");
		sb.append(" L.CREATED_BY ");
		sb.append(" , ");
		sb.append(" L.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" L.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" L.UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" L.CREATED_IP ");
		sb.append(" , ");
		sb.append(" L.UPDATED_IP ");
		sb.append(" , ");
		sb.append(" G.LIBRARY_GROUP_ID ");
		sb.append(" FROM ");
		sb.append(" LIBRARY AS L ");
		sb.append(" JOIN REF_LIBRARY_GROUP AS G ON L.ID = G.LIBRARY_ID AND G.ACTIVE = 'Y' ");
		if (Operator.hasValue(type) && typeid > 0) {
			sb.append(" LEFT OUTER JOIN REF_").append(tableref).append("_LIBRARY AS R ON L.ID = R.LIBRARY_ID AND R.").append(idref).append(" = ").append(typeid).append(" AND R.ACTIVE = 'Y' ");
		sb.append(" left outer JOIN REF_LIBRARY_IN_DIVISIONS RLID1 ON RLID1.LIBRARY_ID=L.ID AND RLID1.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REF_LIBRARY_OUT_DIVISIONS RLOD ON RLOD.LIBRARY_ID=L.ID AND RLOD.ACTIVE = 'Y' ");
		}
		sb.append(" WHERE ");
		sb.append(" G.LIBRARY_GROUP_ID = ").append(groupid);
		sb.append(" AND ");
		sb.append(" L.ACTIVE = 'Y' ");
		if (Operator.hasValue(type) && typeid > 0) {
			sb.append(" AND ");
			sb.append(" R.ID IS NULL ");
			sb.append(" AND ");
			sb.append(" RLID1.ID IS NULL AND RLOD.ID IS NULL ");
		}
		
		sb.append(" UNION ");
		
		sb.append(" SELECT ");
		sb.append(" DISTINCT L.ID ");
		sb.append(" , ");
		sb.append(" L.NAME ");
		sb.append(" , ");
		sb.append(" L.LEVEL_TYPE ");
		sb.append(" , ");
		sb.append(" L.LIBRARY_TYPE_ID ");
		sb.append(" , ");
		sb.append(" L.TITLE ");
		sb.append(" , ");
		sb.append(" L.TXT ");
		sb.append(" , ");
		sb.append(" L.LIBRARY_CODE ");
		sb.append(" , ");
		sb.append(" L.LIBRARY_DESC ");
		sb.append(" , ");
		sb.append(" L.INSPECTABLE ");
		sb.append(" , ");
		sb.append(" L.WARNING ");
		sb.append(" , ");
		sb.append(" L.COMPLETE ");
		sb.append(" , ");
		sb.append(" L.REQUIRED ");
		sb.append(" , ");
		sb.append(" L.ORDR ");
		sb.append(" , ");
		sb.append(" L.ACTIVE ");
		sb.append(" , ");
		sb.append(" L.CREATED_BY ");
		sb.append(" , ");
		sb.append(" L.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" L.UPDATED_BY ");
		sb.append(" , ");
		sb.append(" L.UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" L.CREATED_IP ");
		sb.append(" , ");
		sb.append(" L.UPDATED_IP ");
		sb.append(" , ");
		sb.append(" G.LIBRARY_GROUP_ID ");
		sb.append(" FROM ");
		sb.append(" LIBRARY AS L ");
		sb.append(" JOIN REF_LIBRARY_GROUP AS G ON L.ID = G.LIBRARY_ID AND G.ACTIVE = 'Y' ");
		if (Operator.hasValue(type) && typeid > 0) {
			sb.append(" LEFT OUTER JOIN REF_").append(tableref).append("_LIBRARY AS R ON L.ID = R.LIBRARY_ID AND R.").append(idref).append(" = ").append(typeid).append(" AND R.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REF_LIBRARY_OUT_DIVISIONS RLOD ON RLOD.LIBRARY_ID=L.ID AND RLOD.ACTIVE = 'Y'  AND  RLOD.LKUP_DIVISIONS_ID NOT IN (SELECT LKUP_DIVISIONS_ID FROM  REF_LSO_DIVISIONS WHERE ( ");
		sb.append(" LSO_ID IN ("+lsoid+")"); 
		if(parentid>0){
			sb.append(" OR LSO_ID IN ("+parentid+") ");	
			}
			if(grandparentid>0){
			sb.append(" OR LSO_ID IN ("+grandparentid+") ");	
			}
			sb.append(" ) ");
		sb.append(" AND ACTIVE='Y'");
		sb.append(" ) ");
		sb.append(" left outer JOIN REF_LIBRARY_IN_DIVISIONS RLID ON RLID.LIBRARY_ID=L.ID AND RLID.ACTIVE = 'Y' AND RLID.LKUP_DIVISIONS_ID  IN (SELECT LKUP_DIVISIONS_ID FROM  REF_LSO_DIVISIONS WHERE ( ");
		sb.append(" LSO_ID IN ("+lsoid+")"); 
		if(parentid>0){
			sb.append(" OR LSO_ID IN ("+parentid+") ");	
			}
			if(grandparentid>0){
			sb.append(" OR LSO_ID IN ("+grandparentid+") ");	
			}
			sb.append(" ) ");
		sb.append(" AND ACTIVE='Y'");
		sb.append(" ) ");
		}
		sb.append(" WHERE ");
		
		if (Operator.hasValue(type) && typeid > 0) {
			/*sb.append(" RLD.LSO_ID IN ("+lsoid+")");
			if(parentid>0){
			sb.append(" OR RLD.LSO_ID IN ("+parentid+") ");	
			}
			if(grandparentid>0){
			sb.append(" OR RLD.LSO_ID IN ("+grandparentid+") ");	
			}*/
			/*sb.append(" AND ");*/
			sb.append(" R.ID IS NULL ");
			sb.append("  AND (RLOD.ID IS NOT NULL OR RLID.ID IS NOT NULL) ");
			sb.append(" AND ");
		}
		sb.append(" G.LIBRARY_GROUP_ID = ").append(groupid);
		sb.append(" AND ");
		sb.append(" L.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String add(String type, int refid, int libid, int libgroupid, String title, String text, String libcode, String insp, String warning, String comp, String req, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);

		if (!Operator.hasValue(insp)) { insp = "N"; }
		if (!Operator.hasValue(warning)) { warning = "N"; }
		if (!Operator.hasValue(comp)) { comp = "N"; }
		if (!Operator.hasValue(req)) { req = "N"; }

		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ");
		sb.append(" REF_").append(tableref).append("_LIBRARY ");
		sb.append(" ( ");
		sb.append(idref);
		sb.append(" , ");
		sb.append(" LIBRARY_ID ");
		sb.append(" , ");
		sb.append(" LIBRARY_GROUP_ID ");
		sb.append(" , ");
		sb.append(" TITLE ");
		sb.append(" , ");
		sb.append(" TXT ");
		sb.append(" , ");
		sb.append(" LIBRARY_CODE ");
		sb.append(" , ");
		sb.append(" INSPECTABLE ");
		sb.append(" , ");
		sb.append(" WARNING ");
		sb.append(" , ");
		sb.append(" COMPLETE ");
		sb.append(" , ");
		sb.append(" REQUIRED ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");

		sb.append(" ) VALUES (  ");

		sb.append(refid);
		sb.append(" , ");
		sb.append(libid);
		sb.append(" , ");
		sb.append(libgroupid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(title)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(text)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(libcode)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(insp)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(warning)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(comp)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(req)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" ) ");
		return sb.toString();
	}

	public static String add(String type, int typeid, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String mainref = CsReflect.getMainTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		TypeInfo t = EntityAgent.getEntity(type, typeid);
		int lsoid = t.getEntityid();
		int parentid = t.getParentid();
		int grandparentid = t.getGrandparentid();
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_LIBRARY ( ");
		
		sb.append(" LIBRARY_ID ");
		sb.append(" , ");
		sb.append(idref);
		sb.append(" , ");
		sb.append(" LIBRARY_GROUP_ID ");
		sb.append(" , ");
		sb.append(" TITLE ");
		sb.append(" , ");
		sb.append(" TXT ");
		sb.append(" , ");
		sb.append(" LIBRARY_CODE ");
		sb.append(" , ");
		sb.append(" INSPECTABLE ");
		sb.append(" , ");
		sb.append(" WARNING ");
		sb.append(" , ");
		sb.append(" COMPLETE ");
		sb.append(" , ");
		sb.append(" REQUIRED ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_DATE ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) ");
		sb.append(" SELECT ");
		
		sb.append(" DISTINCT L.ID ");
		sb.append(" , ");
		sb.append(typeid);
		sb.append(" , ");
		sb.append(" RLG.LIBRARY_GROUP_ID ");
		sb.append(" , ");
		sb.append(" L.TITLE ");
		sb.append(" , ");
		sb.append(" L.TXT ");
		sb.append(" , ");
		sb.append(" L.LIBRARY_CODE ");
		sb.append(" , ");
		sb.append(" L.INSPECTABLE ");
		sb.append(" , ");
		sb.append(" L.WARNING ");
		sb.append(" , ");
		sb.append(" L.COMPLETE ");
		sb.append(" , ");
		sb.append(" L.REQUIRED ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" FROM ");
		sb.append(" LIBRARY AS L ");
		sb.append(" JOIN REF_LIBRARY_GROUP RLG on L.ID = RLG.LIBRARY_ID AND RLG.ACTIVE = 'Y'  ");
		sb.append(" JOIN REF_").append(tableref).append("_LIBRARY_GROUP AS R ON RLG.LIBRARY_GROUP_ID = R.LIBRARY_GROUP_ID AND L.ACTIVE = 'Y' AND L.REQUIRED= 'Y' AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN ").append(mainref).append(" AS M ON M.LKUP_ACT_TYPE_ID = R.LKUP_ACT_TYPE_ID ");
		sb.append(" LEFT OUTER JOIN REF_LIBRARY_IN_DIVISIONS RLID1 ON RLID1.LIBRARY_ID=L.ID AND RLID1.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REF_LIBRARY_OUT_DIVISIONS RLOD ON RLOD.LIBRARY_ID=L.ID AND RLOD.ACTIVE = 'Y' ");
		sb.append(" WHERE RLID1.ID IS NULL AND RLOD.ID IS NULL AND M.ID= ").append(typeid);

		sb.append(" UNION ");

		sb.append(" SELECT ");
		sb.append(" DISTINCT L.ID ");
		sb.append(" , ");
		sb.append(typeid);
		sb.append(" , ");
		sb.append(" RLG.LIBRARY_GROUP_ID ");
		sb.append(" , ");
		sb.append(" L.TITLE ");
		sb.append(" , ");
		sb.append(" L.TXT ");
		sb.append(" , ");
		sb.append(" L.LIBRARY_CODE ");
		sb.append(" , ");
		sb.append(" L.INSPECTABLE ");
		sb.append(" , ");
		sb.append(" L.WARNING ");
		sb.append(" , ");
		sb.append(" L.COMPLETE ");
		sb.append(" , ");
		sb.append(" L.REQUIRED ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" FROM ");
		sb.append(" LIBRARY AS L ");
		sb.append(" JOIN REF_LIBRARY_GROUP RLG on L.ID = RLG.LIBRARY_ID AND RLG.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_").append(tableref).append("_LIBRARY_GROUP AS R ON RLG.LIBRARY_GROUP_ID = R.LIBRARY_GROUP_ID AND L.ACTIVE = 'Y' AND L.REQUIRED= 'Y' AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN ").append(mainref).append(" AS M ON M.LKUP_ACT_TYPE_ID = R.LKUP_ACT_TYPE_ID ");
	
		sb.append(" LEFT OUTER JOIN REF_LIBRARY_OUT_DIVISIONS RLOD ON RLOD.LIBRARY_ID=L.ID AND RLOD.ACTIVE = 'Y'  AND  RLOD.LKUP_DIVISIONS_ID NOT IN (SELECT LKUP_DIVISIONS_ID FROM  REF_LSO_DIVISIONS WHERE ( ");
		sb.append(" LSO_ID IN ("+lsoid+")"); 
		if(parentid>0){
			sb.append(" OR LSO_ID IN ("+parentid+") ");	
			}
			if(grandparentid>0){
			sb.append(" OR LSO_ID IN ("+grandparentid+") ");	
			}
			sb.append(" ) ");
		sb.append(" AND ACTIVE='Y'");
		sb.append(" ) ");
		sb.append(" left outer JOIN REF_LIBRARY_IN_DIVISIONS RLID ON RLID.LIBRARY_ID=L.ID AND RLID.ACTIVE = 'Y' AND RLID.LKUP_DIVISIONS_ID  IN (SELECT LKUP_DIVISIONS_ID FROM  REF_LSO_DIVISIONS WHERE ( ");
		sb.append(" LSO_ID IN ("+lsoid+")"); 
		if(parentid>0){
			sb.append(" OR LSO_ID IN ("+parentid+") ");	
			}
			if(grandparentid>0){
			sb.append(" OR LSO_ID IN ("+grandparentid+") ");	
			}
			sb.append(" ) ");
		sb.append(" AND ACTIVE='Y'");
		sb.append(" ) ");
		sb.append(" WHERE  (RLOD.ID IS NOT NULL OR RLID.ID IS NOT NULL)  AND M.ID = ").append(typeid);
		/*sb.append(" JOIN REF_LIBRARY_IN_DIVISIONS RLID1 ON RLID1.LIBRARY_ID=L.ID AND RLID1.ACTIVE = 'Y' ");
		sb.append(" JOIN REF_LSO_DIVISIONS RLD ON RLD.LKUP_DIVISIONS_ID=RLID1.LKUP_DIVISIONS_ID AND RLD.ACTIVE = 'Y' ");
		sb.append(" WHERE RLD.LSO_ID IN ("+lsoid+")");
		if(parentid>0){
		sb.append(" OR RLD.LSO_ID IN ("+parentid+") ");	
		}
		if(grandparentid>0){
		sb.append(" OR RLD.LSO_ID IN ("+grandparentid+") ");	
		}*/
		//sb.append("  M.ID = ").append(typeid);
		return sb.toString();
	}

	public static String edit(int id, String type, int libid, int libgroupid, String title, String text, String libcode, String insp, String warning, String comp, String req, int userid, String ip) {
		if (!Operator.hasValue(insp)) { insp = "N"; }
		if (!Operator.hasValue(warning)) { warning = "N"; }
		if (!Operator.hasValue(comp)) { comp = "N"; }
		if (!Operator.hasValue(req)) { req = "N"; }

		String tableref = CsReflect.getTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" REF_").append(tableref).append("_LIBRARY ");
		sb.append(" SET ");
		sb.append(" LIBRARY_ID = ").append(libid);
		sb.append(" , ");
		sb.append(" LIBRARY_GROUP_ID = ").append(libgroupid);
		sb.append(" , ");
		sb.append(" TITLE = '").append(Operator.sqlEscape(title)).append("' ");
		sb.append(" , ");
		sb.append(" TXT = '").append(Operator.sqlEscape(text)).append("' ");
		sb.append(" , ");
		sb.append(" LIBRARY_CODE = '").append(Operator.sqlEscape(libcode)).append("' ");
		sb.append(" , ");
		sb.append(" INSPECTABLE = '").append(Operator.sqlEscape(insp)).append("' ");
		sb.append(" , ");
		sb.append(" WARNING = '").append(Operator.sqlEscape(warning)).append("' ");
		sb.append(" , ");
		sb.append(" COMPLETE = '").append(Operator.sqlEscape(comp)).append("' ");
		sb.append(" , ");
		sb.append(" REQUIRED = '").append(Operator.sqlEscape(req)).append("' ");
		sb.append(" , ");
		sb.append(" CREATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" CREATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		return sb.toString();
	}

	public static String delete(int id, String type, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" REF_").append(tableref).append("_LIBRARY ");
		sb.append(" SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		return sb.toString();
	}

	public static String complete(int id, String type, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" REF_").append(tableref).append("_LIBRARY ");
		sb.append(" SET ");
		sb.append(" COMPLETE = 'Y' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		return sb.toString();
	}

	public static String incomplete(int id, String type, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" REF_").append(tableref).append("_LIBRARY ");
		sb.append(" SET ");
		sb.append(" COMPLETE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		return sb.toString();
	}

	public static String groups(String type, int typeid) {
		String table = CsReflect.getMainTableRef(type);
		String tableref = CsReflect.getTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT");
		sb.append("   G.*, R.ORDR ");
		sb.append(" FROM ");
		sb.append("   ").append(table).append(" AS A ");
		sb.append(" JOIN REF_").append(tableref).append("_LIBRARY_GROUP AS R ON A.LKUP_ACT_TYPE_ID = R.LKUP_ACT_TYPE_ID AND A.ID = ").append(typeid).append(" AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN LIBRARY_GROUP AS G ON R.LIBRARY_GROUP_ID = G.ID AND G.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String copy(String moduleid, String type, int typeid, int newtypeid, int userid, String ip) {
		if (Operator.toInt(moduleid) < 1 || !Operator.hasValue(type) || typeid < 1 || newtypeid < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_LIBRARY ( ");
		sb.append(idref);
		sb.append(" , ");
		sb.append(" LIBRARY_ID ");
		sb.append(" , ");
		sb.append(" LIBRARY_GROUP_ID ");
		sb.append(" , ");
		sb.append(" TITLE ");
		sb.append(" , ");
		sb.append(" TXT ");
		sb.append(" , ");
		sb.append(" LIBRARY_CODE ");
		sb.append(" , ");
		sb.append(" LIBRARY_DESC ");
		sb.append(" , ");
		sb.append(" INSPECTABLE ");
		sb.append(" , ");
		sb.append(" WARNING ");
		sb.append(" , ");
		sb.append(" COMPLETE ");
		sb.append(" , ");
		sb.append(" REQUIRED ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_DATE ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append("		").append(newtypeid).append(" AS ").append(idref);
		sb.append(" 	, ");
		sb.append(" 	LIBRARY_ID ");
		sb.append(" 	, ");
		sb.append(" 	LIBRARY_GROUP_ID ");
		sb.append(" 	, ");
		sb.append(" 	TITLE ");
		sb.append(" 	, ");
		sb.append(" 	TXT ");
		sb.append(" 	, ");
		sb.append(" 	LIBRARY_CODE ");
		sb.append(" 	, ");
		sb.append(" 	LIBRARY_DESC ");
		sb.append(" 	, ");
		sb.append(" 	INSPECTABLE ");
		sb.append(" 	, ");
		sb.append(" 	WARNING ");
		sb.append(" 	, ");
		sb.append(" 	COMPLETE ");
		sb.append(" 	, ");
		sb.append(" 	REQUIRED ");
		sb.append(" 	, ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" 	, ");
		sb.append(" 	getDate() ");
		sb.append(" 	, ");
		sb.append(" 	getDate() ");
		sb.append(" 	, ");
		sb.append(" 	'").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" 	, ");
		sb.append(" 	'").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" FROM ");
		sb.append(" 	REF_").append(tableref).append("_LIBRARY AS L ");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		sb.append(" 	AND ");
		sb.append(" 	LIBRARY_GROUP_ID = ").append(moduleid);
		sb.append(" 	AND ");
		sb.append(" 	ACTIVE = 'Y' ");
		return sb.toString();
	}


	public static String addMulti(String type, int typeid, String libraryIds, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		String mainref = CsReflect.getMainTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		TypeInfo t = EntityAgent.getEntity(type, typeid);
		int lsoid = t.getEntityid();
		int parentid = t.getParentid();
		int grandparentid = t.getGrandparentid();
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_LIBRARY ( ");
		
		sb.append(" LIBRARY_ID ");
		sb.append(" , ");
		sb.append(idref);
		sb.append(" , ");
		sb.append(" LIBRARY_GROUP_ID ");
		sb.append(" , ");
		sb.append(" TITLE ");
		sb.append(" , ");
		sb.append(" TXT ");
		sb.append(" , ");
		sb.append(" LIBRARY_CODE ");
		sb.append(" , ");
		sb.append(" INSPECTABLE ");
		sb.append(" , ");
		sb.append(" WARNING ");
		sb.append(" , ");
		sb.append(" COMPLETE ");
		sb.append(" , ");
		sb.append(" REQUIRED ");
		sb.append(" , ");
		sb.append(" CREATED_BY ");
		sb.append(" , ");
		sb.append(" UPDATED_BY ");
		sb.append(" , ");
		sb.append(" CREATED_DATE ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE ");
		sb.append(" , ");
		sb.append(" CREATED_IP ");
		sb.append(" , ");
		sb.append(" UPDATED_IP ");
		sb.append(" ) ");
		sb.append(" SELECT ");
		
		sb.append(" DISTINCT L.ID ");
		sb.append(" , ");
		sb.append(typeid);
		sb.append(" , ");
		sb.append(" RLG.LIBRARY_GROUP_ID ");
		sb.append(" , ");
		sb.append(" L.TITLE ");
		sb.append(" , ");
		sb.append(" L.TXT ");
		sb.append(" , ");
		sb.append(" L.LIBRARY_CODE ");
		sb.append(" , ");
		sb.append(" L.INSPECTABLE ");
		sb.append(" , ");
		sb.append(" L.WARNING ");
		sb.append(" , ");
		sb.append(" L.COMPLETE ");
		sb.append(" , ");
		sb.append(" L.REQUIRED ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" getDate() ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" FROM ");
		sb.append(" LIBRARY AS L ");
		sb.append(" JOIN REF_LIBRARY_GROUP RLG on L.ID = RLG.LIBRARY_ID AND RLG.ACTIVE = 'Y'  ");
		sb.append(" JOIN REF_").append(tableref).append("_LIBRARY_GROUP AS R ON RLG.LIBRARY_GROUP_ID = R.LIBRARY_GROUP_ID AND L.ACTIVE = 'Y' AND R.ACTIVE = 'Y' ");
		sb.append(" JOIN ").append(mainref).append(" AS M ON M.LKUP_ACT_TYPE_ID = R.LKUP_ACT_TYPE_ID ");
		sb.append(" LEFT OUTER JOIN REF_LIBRARY_IN_DIVISIONS RLID1 ON RLID1.LIBRARY_ID=L.ID AND RLID1.ACTIVE = 'Y' ");
		sb.append(" LEFT OUTER JOIN REF_LIBRARY_OUT_DIVISIONS RLOD ON RLOD.LIBRARY_ID=L.ID AND RLOD.ACTIVE = 'Y' ");
		sb.append(" WHERE RLID1.ID IS NULL AND RLOD.ID IS NULL AND M.ID= ").append(typeid);
		sb.append(" AND L.ID in (").append(Operator.replace(libraryIds, "|", ",")).append(")");
		
		return sb.toString();
	}

}















