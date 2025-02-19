package csapi.impl.holds;

import java.util.HashMap;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.entity.EntityAgent;
import csapi.impl.project.ProjectAgent;
import csapi.utils.CsReflect;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.TypeInfo;

public class HoldsSQL {

	public static String info(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( SELECT COUNT(DISTINCT ID) AS CONTENT_COUNT FROM CONTENT WHERE ACTIVE = 'Y' AND LOWER(TYPE) = 'holds') ");
		sb.append(" SELECT ");
		
		sb.append(" R.ID ");
		
		sb.append(" ,");
		sb.append(" R.DESCRIPTION ");
		sb.append(" ,");
		sb.append(" R.LKUP_HOLDS_TYPE_ID ");
		sb.append(" ,");
		sb.append(" R.LKUP_HOLDS_STATUS_ID ");
		sb.append(" , ");
		sb.append(" H.TYPE AS LKUP_HOLDS_TYPE_TEXT ");
		sb.append(" , ");
		sb.append(" H.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" S.DESCRIPTION AS LKUP_HOLDS_STATUS_TEXT ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" R.CREATED_DATE, ");
		sb.append(" R.UPDATED_DATE, ");
		sb.append("   CASE ");
		sb.append("     WHEN Q.CONTENT_COUNT IS NULL THEN '' ");
		sb.append("     WHEN Q.CONTENT_COUNT > 0 THEN 'holds' ");
		sb.append("   ELSE '' END AS CONTENT_TYPE, ");
		sb.append(" S.WATERMARK_PATH as hold_watermark_path, ");
		sb.append(" CASE WHEN S.WATERMARK_STATUS = 'Y' THEN S.DESCRIPTION ELSE '' END  as hold_watermark_text  ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_HOLDS R  ");
		sb.append(" LEFT OUTER JOIN LKUP_HOLDS_TYPE H ON R.LKUP_HOLDS_TYPE_ID = H.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_HOLDS_STATUS S ON R.LKUP_HOLDS_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN USERS CU ON R.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON R.UPDATED_BY = UP.ID  ");
		sb.append(" LEFT OUTER JOIN Q ON 1=1 ");
		sb.append(" WHERE ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" AND ");
		sb.append(" R.ACTIVE = 'Y' ");
		sb.append(" AND ");
		sb.append(" S.LIVE = 'Y' ");
//		sb.append(" AND ");
//		sb.append(" R.LKUP_HOLDS_STATUS_ID <> 3 ");
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" R.ID = ").append(id);
		}
		else {
			sb.append(" AND ");
			sb.append(" R.NEW_ID < 0 ");
		}
		return sb.toString();
	}

	public static String summary(String type, int typeid, int id) {
		return info(type, typeid, -1);
	}

	public static String id(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" R.ID ");
		sb.append(" ,");
		sb.append(" R.DESCRIPTION ");
		sb.append(" ,");
		sb.append(" R.LKUP_HOLDS_TYPE_ID ");
		sb.append(" ,");
		sb.append(" R.LKUP_HOLDS_STATUS_ID ");
		sb.append(" , ");
		sb.append(" H.TYPE AS LKUP_HOLDS_TYPE_TEXT");
		sb.append(" , ");
		sb.append(" S.DESCRIPTION AS LKUP_HOLDS_STATUS_TEXT ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" R.CREATED_DATE, ");
		sb.append(" R.UPDATED_DATE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_HOLDS R  ");
		sb.append(" LEFT OUTER JOIN LKUP_HOLDS_TYPE H ON R.LKUP_HOLDS_TYPE_ID = H.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_HOLDS_STATUS S ON R.LKUP_HOLDS_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN USERS CU ON R.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON R.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" R.ID = ").append(id);
		return sb.toString();
	}

	public static String list(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		if (id > 0) {
			sb.append(" WITH Q AS ( ");
			sb.append("   SELECT DISTINCT CASE WHEN ORIG_ID < 1 THEN ID ELSE ORIG_ID END AS ORIG_ID ");
			sb.append("   FROM ");
			sb.append("   REF_").append(tableref).append("_HOLDS  ");
			sb.append("   WHERE ");
			sb.append("   ID = ").append(id).append(" ");
			sb.append(" ) ");
		}
		sb.append(" SELECT ");
		
		sb.append(" R.ID ");
		sb.append(" ,");
		sb.append(" R.ID AS HOLDS ");
		
		sb.append(" ,");
		sb.append(" R.DESCRIPTION ");
		sb.append(" ,");
		sb.append(" R.LKUP_HOLDS_TYPE_ID ");
		sb.append(" ,");
		sb.append(" R.LKUP_HOLDS_STATUS_ID ");
		sb.append(" , ");
		sb.append(" H.DESCRIPTION AS LKUP_HOLDS_TYPE_TEXT");
		sb.append(" , ");
		sb.append(" S.DESCRIPTION AS LKUP_HOLDS_STATUS_TEXT ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" R.CREATED_DATE, ");
		sb.append(" R.UPDATED_DATE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_HOLDS R  ");
		if (id > 0) {
			sb.append(" JOIN Q ON R.ID = Q.ORIG_ID OR R.ORIG_ID = Q.ORIG_ID ");
		}
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPHOLDTYPETABLE).append(" H ON R.LKUP_HOLDS_TYPE_ID = H.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPHOLDSTATUSTABLE).append(" S ON R.LKUP_HOLDS_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU ON R.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP ON R.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" R.ACTIVE = 'Y' ");

		if (id < 1) {
			sb.append(" AND ");
			sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
			sb.append(" AND ");
			sb.append(" R.NEW_ID < 0 ");
		}
		sb.append(" ORDER BY R.CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String details(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		
		sb.append(" R.ID ");
		sb.append(" ,");
		sb.append(" R.ID AS HOLDS ");
		
		sb.append(" ,");
		sb.append(" R.DESCRIPTION ");
		sb.append(" ,");
		sb.append(" R.LKUP_HOLDS_TYPE_ID ");
		sb.append(" ,");
		sb.append(" R.LKUP_HOLDS_STATUS_ID ");
		sb.append(" , ");
		sb.append(" H.DESCRIPTION AS LKUP_HOLDS_TYPE_TEXT");
		sb.append(" , ");
		sb.append(" S.DESCRIPTION AS LKUP_HOLDS_STATUS_TEXT ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" R.CREATED_DATE, ");
		sb.append(" R.UPDATED_DATE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_HOLDS R  ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPHOLDTYPETABLE).append(" H ON R.LKUP_HOLDS_TYPE_ID = H.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPHOLDSTATUSTABLE).append(" S ON R.LKUP_HOLDS_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU ON R.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP ON R.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		sb.append(" AND ");
		sb.append(" R.ACTIVE = 'Y' ");

		if (id > 0) {
			sb.append(" AND ");
			sb.append(" R.ID = ").append(id);
		}
		else {
			sb.append(" AND ");
			sb.append(" R.NEW_ID < 0 ");
		}
		return sb.toString();
	}
	
	/**
	 * @deprecated use updateHold(String type, int holdid, int newid, int userid, String ip)
	 */
	public static String updateHolds(RequestVO r, Token u) {
		StringBuilder sb = new StringBuilder();
		try{
			String type = r.getType();
			int typeId = r.getTypeid();
			if (!Operator.hasValue(type)) { return ""; }
			String tableref = CsReflect.getTableRef(type);
			String idref = CsReflect.getFieldIdRef(type);
			String idtype = CsReflect.getFieldIdRef(r.getGrouptype());
			String table = "REF_"+tableref+"_HOLDS";
			
			sb.append(" update ").append(table).append(" SET ACTIVE ='N'  ");
			sb.append(", UPDATED_DATE  = CURRENT_TIMESTAMP ");
			sb.append(", UPDATED_BY  = ").append(u.getId());
			sb.append(" WHERE  ");
			sb.append(" ID = ").append(r.getId());
			sb.append(" AND ");
			sb.append(idref).append("=").append(typeId);
			
		}catch (Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	
	/**
	 * @deprecated use addHold(String type, int typeid, int holdtype, int holdstatus, String description, int userid, String ip, Timekeeper now)
	 */
	public static String insertHolds(RequestVO r, Token u) {
		StringBuilder sb = new StringBuilder();
		try{
			String type = r.getType();
			int id = Operator.toInt(r.getId());
			int typeId = r.getTypeid();
			if (!Operator.hasValue(type)) { return ""; }
			String tableref = CsReflect.getTableRef(type);
			String idref = CsReflect.getFieldIdRef(type);
			String idtype = CsReflect.getFieldIdRef(r.getGrouptype());
			String table = "REF_"+tableref+"_HOLDS";
			
			
			ObjGroupVO o = r.getData()[0];
			ObjVO[] obj = o.getObj();
			if(obj.length>0 && u.getId()>0){
				sb.append(" insert into  ").append(table).append(" ( ");
				StringBuilder t = new StringBuilder();
				StringBuilder v = new StringBuilder();
				for(int i=0;i<obj.length;i++){
					ObjVO vo = obj[i];
					String field = vo.getField();
					String value = vo.getValue();
					boolean addable = vo.isAddable();
					if (addable) {
						t.append(field).append(",");
						if(field.equalsIgnoreCase("LKUP_HOLDS_TYPE_ID") && id>0){
							v.append(" ( select LKUP_HOLDS_TYPE_ID from  ").append(table).append(" WHERE ID=").append(id).append(" )");
							v.append(",");
						}else {
							v.append("'").append(Operator.sqlEscape(value)).append("' ");
							v.append(",");
						}
					}
				}
				//types add
				
				t.append(idref).append(",");
				
				v.append("'").append(typeId).append("' ");
				v.append(",");
				
				t.append(" CREATED_BY,UPDATED_BY,CREATED_DATE,UPDATED_DATE) VALUES ( ");
				
				sb.append(t.toString());
				sb.append(v.toString());
				
				sb.append(u.getId());
				sb.append(",");
				sb.append(u.getId());
				sb.append(",");
				sb.append(" CURRENT_TIMESTAMP ");
				sb.append(",");
				sb.append(" CURRENT_TIMESTAMP ");
				sb.append(" ) "); 
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String addHold(String type, int typeid, int holdtype, int holdstatus, String description, int userid, String ip, Timekeeper now) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_HOLDS ( ");
		sb.append(idref).append(", LKUP_HOLDS_TYPE_ID, LKUP_HOLDS_STATUS_ID, DESCRIPTION, CREATED_BY, UPDATED_BY, CREATED_IP, UPDATED_IP, CREATED_DATE, UPDATED_DATE ");
		sb.append(" ) OUTPUT Inserted.ID VALUES ( ");
		sb.append(typeid);
		sb.append(" , ");
		sb.append(holdtype);
		sb.append(" , ");
		sb.append(holdstatus);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(now.sqlDatetime());
		sb.append(" , ");
		sb.append(now.sqlDatetime());
		sb.append(" ) ");
		return sb.toString();
	}

	public static String addHold(String type, int origid, int holdstatus, String description, int userid, String ip, Timekeeper now) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO REF_").append(tableref).append("_HOLDS ( ");
		sb.append(idref).append(", LKUP_HOLDS_TYPE_ID, LKUP_HOLDS_STATUS_ID, DESCRIPTION, ORIG_ID, CREATED_BY, UPDATED_BY, CREATED_IP, UPDATED_IP, CREATED_DATE, UPDATED_DATE ");
		sb.append(" ) SELECT ");
		sb.append(idref);
		sb.append(" , ");
		sb.append(" LKUP_HOLDS_TYPE_ID ");
		sb.append(" , ");
		sb.append(holdstatus);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(" CASE WHEN ORIG_ID = -1 THEN ID ELSE ORIG_ID END AS ORIG_ID ");
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(now.sqlDatetime());
		sb.append(" , ");
		sb.append(now.sqlDatetime());
		sb.append(" FROM REF_").append(tableref).append("_HOLDS ");
		sb.append(" WHERE ID =  ").append(origid);
		return sb.toString();
	}

	public static String getHold(int id, String type) {
		if (id < 1) { return ""; }
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(" , ");
		sb.append(idref).append(" AS REF_ID ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_HOLDS WHERE ID = ").append(id);
		return sb.toString();
	}

	public static String getHold(String type, int typeid, int holdstatus, int userid, Timekeeper now) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM REF_").append(tableref).append("_HOLDS ");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(" LKUP_HOLDS_STATUS_ID = ").append(holdstatus);
		sb.append(" AND ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" AND ");
		sb.append(" UPDATED_DATE = ").append(now.sqlDatetime());
		return sb.toString();
	}

	public static String updateHold(String type, int holdid, int newid, int userid, String ip) {
		String tableref = CsReflect.getTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE REF_").append(tableref).append("_HOLDS SET ");
		sb.append(" NEW_ID = ").append(newid);
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" NEW_ID < 1 ");
		sb.append(" AND ");
		sb.append(" ID <> ").append(newid);
		sb.append(" AND ");

		sb.append(" ( ");

		sb.append("   ( ");
		sb.append("      ID = ").append(holdid);
		sb.append("      AND ");
		sb.append("      ORIG_ID = -1 ");
		sb.append("      AND ");
		sb.append("      NEW_ID = -1 ");
		sb.append("   ) ");

		sb.append("   OR ");

		sb.append("   ORIG_ID IN ( ");
		sb.append("     SELECT ");
		sb.append("     CASE ");
		sb.append("       WHEN ORIG_ID IS NULL OR ORIG_ID < 1 THEN ID ");
		sb.append("       ELSE ORIG_ID END AS ORIG_ID ");
		sb.append("     FROM ");
		sb.append("       REF_").append(tableref).append("_HOLDS ");
		sb.append("     WHERE ID = ").append(holdid);
		sb.append("   ) ");

		sb.append(" ) ");
		return sb.toString();
	}
	
	/**
	 * @deprecated Use HoldsAgent.getCurrentHolds(String type, int typeid);
	 */
	public static String getAlertHolds(String type, int typeId) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);

		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");

		if (Operator.equalsIgnoreCase(type, "lso")) {
			sb.append(" SELECT ");
			sb.append("   H.LKUP_HOLDS_TYPE_ID, H.LKUP_HOLDS_STATUS_ID ");
			sb.append(" FROM ");
			sb.append(" LSO AS O ");
			sb.append(" LEFT OUTER JOIN LSO AS S ON O.PARENT_ID > 0 AND O.PARENT_ID = S.ID ");
			sb.append(" LEFT OUTER JOIN LSO AS L ON S.PARENT_ID > 0 AND S.PARENT_ID = L.ID ");
			sb.append(" JOIN REF_LSO_HOLDS AS H ON O.ID = H.LSO_ID OR S.ID = H.LSO_ID OR L.ID = H.LSO_ID ");
			sb.append(" JOIN LKUP_HOLDS_STATUS LHS ON H.LKUP_HOLDS_STATUS_ID = LHS.ID AND LHS.ACTIVE = 'Y' AND LHS.STATUS = 'A' ");
			sb.append(" WHERE ");
			sb.append("   O.ID = ").append(typeId).append(" ");
			sb.append("   AND ");
			sb.append("   H.ACTIVE = 'Y' ");
			sb.append("   AND ");
			sb.append("   H.NEW_ID < 1 ");
		}
		else {
			sb.append(" SELECT ");
			sb.append("   T.LKUP_HOLDS_TYPE_ID, T.LKUP_HOLDS_STATUS_ID ");
			sb.append(" FROM ");
			sb.append("   REF_").append(tableref).append("_HOLDS AS T  ");
			sb.append(" JOIN LKUP_HOLDS_STATUS S ON T.LKUP_HOLDS_STATUS_ID = S.ID AND S.ACTIVE = 'Y' AND S.STATUS = 'A' ");
			sb.append(" WHERE ");
			sb.append("   T.").append(idref).append(" = ").append(typeId).append(" ");
			sb.append("   AND ");
			sb.append("   T.ACTIVE = 'Y' ");
			sb.append("   AND ");
			sb.append("   T.NEW_ID < 1 ");

			if (Operator.equalsIgnoreCase(type, "project") || Operator.equalsIgnoreCase(type, "activity")) {
				TypeInfo project = EntityAgent.getEntity(type, typeId);
				String entity = project.getEntity();
				int entityid = project.getEntityid();
				int parentid = project.getParentid();
				int gparentid = project.getGrandparentid();
				int projectid = project.getProjectid();

				if (Operator.equalsIgnoreCase(type, "activity")) {
					sb.append(" UNION ");
					sb.append(" SELECT ");
					sb.append("   P.LKUP_HOLDS_TYPE_ID, P.LKUP_HOLDS_STATUS_ID ");
					sb.append(" FROM ");
					sb.append("   REF_PROJECT_HOLDS AS P  ");
					sb.append(" JOIN LKUP_HOLDS_STATUS S ON P.LKUP_HOLDS_STATUS_ID = S.ID AND S.ACTIVE = 'Y' AND S.STATUS = 'A' ");
					sb.append("   JOIN ACTIVITY AS A ON P.PROJECT_ID = A.PROJECT_ID AND P.ACTIVE = 'Y' AND P.NEW_ID < 1 AND A.ID = ").append(typeId).append(" ");
					sb.append("   AND ");
					sb.append("   P.ACTIVE = 'Y' ");
					sb.append("   AND ");
					sb.append("   P.NEW_ID < 1 ");
				}

				if (Operator.equalsIgnoreCase(entity, "lso") && entityid > 0) {
					sb.append(" UNION ");
					sb.append(" SELECT ");
					sb.append("   H.LKUP_HOLDS_TYPE_ID, H.LKUP_HOLDS_STATUS_ID ");
					sb.append(" FROM ");
					sb.append(" REF_LSO_HOLDS AS H ");
					sb.append(" JOIN LKUP_HOLDS_STATUS S ON H.LKUP_HOLDS_STATUS_ID = S.ID AND S.ACTIVE = 'Y' AND S.STATUS = 'A' ");
					sb.append(" WHERE ");
					sb.append("   H.LSO_ID IN (");
					sb.append(entityid);
					if (parentid > 0) {
						sb.append(" , ");
						sb.append(parentid);
					}
					if (gparentid > 0) {
						sb.append(" , ");
						sb.append(gparentid);
					}
					sb.append(" ) ");
					sb.append("   AND ");
					sb.append("   H.ACTIVE = 'Y' ");
					sb.append("   AND ");
					sb.append("   H.NEW_ID < 1 ");
				}

				if (Operator.hasValue(entity) && projectid > 0) {
					String etableref = CsReflect.getTableRef(entity);
					String eidref = CsReflect.getFieldIdRef(entity);
					sb.append(" UNION ");
					sb.append(" SELECT ");
					sb.append("   E.LKUP_HOLDS_TYPE_ID, E.LKUP_HOLDS_STATUS_ID ");
					sb.append(" FROM ");
					sb.append("   REF_").append(etableref).append("_HOLDS AS E  ");
					sb.append("   JOIN REF_").append(etableref).append("_PROJECT AS EP ON E.ACTIVE = 'Y' AND EP.ACTIVE = 'Y' AND E.NEW_ID < 1 AND E.").append(eidref).append(" = EP.").append(eidref).append(" AND EP.PROJECT_ID = ").append(projectid);
					sb.append("   AND ");
					sb.append("   E.ACTIVE = 'Y' ");
					sb.append("   AND ");
					sb.append("   E.NEW_ID < 1 ");
					sb.append(" JOIN LKUP_HOLDS_STATUS S ON E.LKUP_HOLDS_STATUS_ID = S.ID AND S.ACTIVE = 'Y' AND S.STATUS = 'A' ");
				}

			}
		}


		sb.append(" ), Q1 AS ( ");
		sb.append(" SELECT ");
		sb.append(" 'HOLD_' + H.TYPE AS ALERT, ");
		sb.append(" CASE ");
		sb.append("   WHEN H.TYPE = 'H' THEN 100 ");
		sb.append("   WHEN H.TYPE = 'S' THEN 50 ");
		sb.append(" ELSE 10 END AS ORDR ");
		sb.append(" FROM ");
		sb.append(" Q  ");
		sb.append(" JOIN LKUP_HOLDS_TYPE H ON Q.LKUP_HOLDS_TYPE_ID = H.ID AND H.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_HOLDS_STATUS S ON Q.LKUP_HOLDS_STATUS_ID = S.ID AND S.ACTIVE = 'Y' AND S.STATUS = 'A' ");
		sb.append(" ) ");

		sb.append(" SELECT TOP 1 * FROM Q1 ");
		sb.append(" ORDER BY Q1.ORDR DESC ");
		return sb.toString();
	}

	public static String getAllHolds(String type, int typeid) {
		TypeInfo t = EntityAgent.getEntity(type, typeid);
		String entity = t.getEntity();
		int entityid = t.getEntityid();
		int parentid = t.getParentid();
		int gparentid = t.getGrandparentid();
		int projectid = t.getProjectid();
		int activityid = t.getActivityid();

		String tableref = CsReflect.getTableRef(entity);
		String idref = CsReflect.getFieldIdRef(entity);

		boolean empty = true;
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH U AS ( ");
		if (Operator.hasValue(entity) && entityid > 0) {
			sb.append(" 	SELECT ");
			sb.append(" 		'").append(Operator.sqlEscape(entity)).append("' AS TYPE ");
			sb.append(" 		, ");
			sb.append(" 		R.").append(idref).append(" AS TYPE_ID ");
			sb.append(" 		, ");
			sb.append(" 		R.ID AS HOLDS_ID ");
			sb.append(" 		, ");
			sb.append(" 		R.TITLE ");
			sb.append(" 		, ");
			sb.append(" 		R.DESCRIPTION ");
			sb.append(" 		, ");
			sb.append(" 		T.ID AS HOLDS_TYPE_ID ");
			sb.append(" 		, ");
			sb.append(" 		T.TYPE AS HOLDS_TYPE ");
			sb.append(" 		, ");
			sb.append(" 		T.DESCRIPTION AS HOLDS_TYPE_DESCRIPTION ");
			sb.append(" 		, ");
			sb.append(" 		S.ID AS STATUS_ID ");
			sb.append(" 		, ");
			sb.append(" 		S.STATUS AS STATUS ");
			sb.append(" 		, ");
			sb.append(" 		S.DESCRIPTION AS STATUS_DESCRIPTION ");
			sb.append(" 		, ");
			sb.append(" 		S.LIVE ");
			sb.append(" 		, ");
			sb.append(" 		S.RELEASED ");
			sb.append(" 		, ");
			sb.append(" 		T.SIGNIFICANT ");
			sb.append(" 		, ");
			sb.append(" 		T.ISPUBLIC ");
			sb.append(" 		, ");
			sb.append(" 		T.ORDR ");
			sb.append(" 		, ");
			sb.append(" 		R.CREATED_BY ");
			sb.append(" 		, ");
			sb.append(" 		R.CREATED_DATE ");
			sb.append(" 		, ");
			sb.append(" 		CASE ");
			sb.append(" 			WHEN T.SIGNIFICANT = 'Y' THEN 10 ");
			sb.append(" 		ELSE 0 END AS SIGNIFICANT_ORDER ");
			sb.append(" 	FROM ");
			sb.append(" 		REF_").append(tableref).append("_HOLDS AS R ");

			sb.append(" 		JOIN LKUP_HOLDS_TYPE AS T ON R.LKUP_HOLDS_TYPE_ID = T.ID AND R.ACTIVE = 'Y' AND T.ACTIVE = 'Y' AND R.NEW_ID = -1 AND R.").append(idref).append(" IN ( ");
			sb.append(entityid);
			if (parentid > 0) {
				sb.append(" , ");
				sb.append(parentid);
			}
			if (gparentid > 0) {
				sb.append(" , ");
				sb.append(gparentid);
			}
			sb.append(" 		) ");

			sb.append(" 		JOIN LKUP_HOLDS_STATUS AS S ON R.LKUP_HOLDS_STATUS_ID = S.ID AND S.ACTIVE = 'Y' ");
			empty = false;
		}
		if (projectid > 0) {
			if (!empty) { sb.append(" UNION "); }
			sb.append(" 	SELECT ");
			sb.append(" 		'project' AS TYPE ");
			sb.append(" 		, ");
			sb.append(" 		R.PROJECT_ID AS TYPE_ID ");
			sb.append(" 		, ");
			sb.append(" 		R.ID AS HOLDS_ID ");
			sb.append(" 		, ");
			sb.append(" 		R.TITLE ");
			sb.append(" 		, ");
			sb.append(" 		R.DESCRIPTION ");
			sb.append(" 		, ");
			sb.append(" 		T.ID AS HOLDS_TYPE_ID ");
			sb.append(" 		, ");
			sb.append(" 		T.TYPE AS HOLDS_TYPE ");
			sb.append(" 		, ");
			sb.append(" 		T.DESCRIPTION AS HOLDS_TYPE_DESCRIPTION ");
			sb.append(" 		, ");
			sb.append(" 		S.ID AS STATUS_ID ");
			sb.append(" 		, ");
			sb.append(" 		S.STATUS AS STATUS ");
			sb.append(" 		, ");
			sb.append(" 		S.DESCRIPTION AS STATUS_DESCRIPTION ");
			sb.append(" 		, ");
			sb.append(" 		S.LIVE ");
			sb.append(" 		, ");
			sb.append(" 		S.RELEASED ");
			sb.append(" 		, ");
			sb.append(" 		T.SIGNIFICANT ");
			sb.append(" 		, ");
			sb.append(" 		T.ISPUBLIC ");
			sb.append(" 		, ");
			sb.append(" 		T.ORDR ");
			sb.append(" 		, ");
			sb.append(" 		R.CREATED_BY ");
			sb.append(" 		, ");
			sb.append(" 		R.CREATED_DATE ");
			sb.append(" 		, ");
			sb.append(" 		CASE ");
			sb.append(" 			WHEN T.SIGNIFICANT = 'Y' THEN 20 ");
			sb.append(" 		ELSE 0 END AS SIGNIFICANT_ORDER ");
			sb.append(" 	FROM ");
			sb.append(" 		REF_PROJECT_HOLDS AS R ");
			sb.append(" 		JOIN LKUP_HOLDS_TYPE AS T ON R.LKUP_HOLDS_TYPE_ID = T.ID AND R.ACTIVE = 'Y' AND T.ACTIVE = 'Y' AND R.NEW_ID = -1 AND R.PROJECT_ID = ").append(projectid);
			sb.append(" 		JOIN LKUP_HOLDS_STATUS AS S ON R.LKUP_HOLDS_STATUS_ID = S.ID AND S.ACTIVE = 'Y' ");
			empty = false;
		}
		if (activityid > 0) {
			if (!empty) { sb.append(" UNION "); }
			sb.append(" 	SELECT ");
			sb.append(" 		'activity' AS TYPE ");
			sb.append(" 		, ");
			sb.append(" 		R.ACTIVITY_ID AS TYPE_ID ");
			sb.append(" 		, ");
			sb.append(" 		R.ID AS HOLDS_ID ");
			sb.append(" 		, ");
			sb.append(" 		R.TITLE ");
			sb.append(" 		, ");
			sb.append(" 		R.DESCRIPTION ");
			sb.append(" 		, ");
			sb.append(" 		T.ID AS HOLDS_TYPE_ID ");
			sb.append(" 		, ");
			sb.append(" 		T.TYPE AS HOLDS_TYPE ");
			sb.append(" 		, ");
			sb.append(" 		T.DESCRIPTION AS HOLDS_TYPE_DESCRIPTION ");
			sb.append(" 		, ");
			sb.append(" 		S.ID AS STATUS_ID ");
			sb.append(" 		, ");
			sb.append(" 		S.STATUS AS STATUS ");
			sb.append(" 		, ");
			sb.append(" 		S.DESCRIPTION AS STATUS_DESCRIPTION ");
			sb.append(" 		, ");
			sb.append(" 		S.LIVE ");
			sb.append(" 		, ");
			sb.append(" 		S.RELEASED ");
			sb.append(" 		, ");
			sb.append(" 		T.SIGNIFICANT ");
			sb.append(" 		, ");
			sb.append(" 		T.ISPUBLIC ");
			sb.append(" 		, ");
			sb.append(" 		T.ORDR ");
			sb.append(" 		, ");
			sb.append(" 		R.CREATED_BY ");
			sb.append(" 		, ");
			sb.append(" 		R.CREATED_DATE ");
			sb.append(" 		, ");
			sb.append(" 		CASE ");
			sb.append(" 			WHEN T.SIGNIFICANT = 'Y' THEN 30 ");
			sb.append(" 		ELSE 0 END AS SIGNIFICANT_ORDER ");
			sb.append(" 	FROM ");
			sb.append(" 		REF_ACT_HOLDS AS R ");
			sb.append(" 		JOIN LKUP_HOLDS_TYPE AS T ON R.LKUP_HOLDS_TYPE_ID = T.ID AND R.ACTIVE = 'Y' AND T.ACTIVE = 'Y' AND R.NEW_ID = -1 AND R.ACTIVITY_ID = ").append(activityid);
			sb.append(" 		JOIN LKUP_HOLDS_STATUS AS S ON R.LKUP_HOLDS_STATUS_ID = S.ID AND S.ACTIVE = 'Y' ");
			empty = false;
		}

		if (empty) { return ""; }
		sb.append(" ) ");

		sb.append(" SELECT DISTINCT * ");
		sb.append(" FROM U ");
		sb.append(" ORDER BY ");
//		sb.append(" SIGNIFICANT_ORDER DESC, ");
		sb.append(" ORDR ");

		return sb.toString();
	}

	public static String getActivityTypes(String[] acttypeids) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	T.ID AS ACT_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	T.TYPE AS ACT_TYPE ");
		sb.append(" 	, ");
		sb.append(" 	T.DESCRIPTION AS ACT_TYPE_DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	H.ID AS HOLDS_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	H.TYPE AS HOLD ");
		sb.append(" 	, ");
		sb.append(" 	H.DESCRIPTION AS HOLDS_TYPE_DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" 	LKUP_ACT_TYPE AS T ");
		sb.append(" 	JOIN REF_ACT_TYPE_HOLDS AS RH ON T.ID = RH.LKUP_ACT_TYPE_ID AND RH.ACTIVE = 'Y' AND T.ACTIVE = 'Y' ");
		sb.append(" 	JOIN LKUP_HOLDS_TYPE AS H ON RH.LKUP_HOLDS_TYPE_ID = H.ID AND H.ACTIVE = 'Y' ");
		if (Operator.hasValue(acttypeids)) {
			sb.append(" AND T.ID IN (").append(Operator.join(acttypeids, ",")).append(") ");
		}
		return sb.toString();
	}

	public static String getActivity(int actid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	T.ID AS ACT_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	T.TYPE AS ACT_TYPE ");
		sb.append(" 	, ");
		sb.append(" 	T.DESCRIPTION AS ACT_TYPE_DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	H.ID AS HOLDS_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	H.TYPE AS HOLD ");
		sb.append(" 	, ");
		sb.append(" 	H.DESCRIPTION AS HOLDS_TYPE_DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" 	ACTIVITY AS A ");
		sb.append(" 	JOIN LKUP_ACT_TYPE AS T ON A.LKUP_ACT_TYPE_ID = T.ID AND A.ID = ").append(actid);
		sb.append(" 	JOIN REF_ACT_TYPE_HOLDS AS RH ON T.ID = RH.LKUP_ACT_TYPE_ID AND RH.ACTIVE = 'Y' AND T.ACTIVE = 'Y' ");
		sb.append(" 	JOIN LKUP_HOLDS_TYPE AS H ON RH.LKUP_HOLDS_TYPE_ID = H.ID AND H.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String getProject(int projectid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT ");
		sb.append(" 	T.ID AS ACT_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	T.TYPE AS ACT_TYPE ");
		sb.append(" 	, ");
		sb.append(" 	T.DESCRIPTION AS ACT_TYPE_DESCRIPTION ");
		sb.append(" 	, ");
		sb.append(" 	H.ID AS HOLDS_TYPE_ID ");
		sb.append(" 	, ");
		sb.append(" 	H.TYPE AS HOLD ");
		sb.append(" 	, ");
		sb.append(" 	H.DESCRIPTION AS HOLDS_TYPE_DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" 	PROJECT AS P ");
		sb.append(" 	JOIN REF_PROJECT_ACT_TYPE AS RPA ON P.LKUP_PROJECT_TYPE_ID = RPA.LKUP_PROJECT_TYPE_ID AND RPA.ACTIVE = 'Y' AND P.ID = ").append(projectid);
		sb.append(" 	JOIN LKUP_ACT_TYPE AS T ON RPA.LKUP_ACT_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" 	JOIN REF_ACT_TYPE_HOLDS AS RH ON T.ID = RH.LKUP_ACT_TYPE_ID AND RH.ACTIVE = 'Y' ");
		sb.append(" 	JOIN LKUP_HOLDS_TYPE AS H ON RH.LKUP_HOLDS_TYPE_ID = H.ID AND H.ACTIVE = 'Y' ");
		return sb.toString();
	}



	public static String getNotes(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" N.ID ");
		sb.append(" , ");
		sb.append(" R.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" N.NOTE AS TEXT ");
		sb.append(" , ");
		sb.append(" T.TYPE AS DESCRIPTION ");
		sb.append(" , ");
		sb.append(" T.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" N.CREATED_DATE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_HOLDS AS R ");
		sb.append(" JOIN NOTES AS N ON R.NOTES_ID = N.ID AND R.ACTIVE = 'Y' AND N.ACTIVE = 'Y' ");
		sb.append(" JOIN LKUP_NOTES_TYPE AS T ON N.LKUP_NOTES_TYPE_ID = T.ID ");
		sb.append(" WHERE ");
		sb.append(" R.").append(idref).append(" = ").append(typeid);
		sb.append(" ORDER BY ");
		sb.append(" N.CREATED_DATE DESC ");
		return sb.toString();
	}

	public static String userlist(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		if (id > 0) {
			sb.append(" WITH Q AS ( ");
			sb.append("   SELECT DISTINCT CASE WHEN ORIG_ID < 1 THEN ID ELSE ORIG_ID END AS ORIG_ID ");
			sb.append("   FROM ");
			sb.append("   REF_").append(tableref).append("_HOLDS  ");
			sb.append("   WHERE ");
			sb.append("   ID = ").append(id).append(" ");
			sb.append(" ) ");
		}
		sb.append(" SELECT ");
		
		sb.append(" R.ID ");
		sb.append(" ,");
		sb.append(" R."+tableref+"_ID AS HOLDS ");
		
		sb.append(" ,");
		sb.append(" R.DESCRIPTION ");
		sb.append(" ,");
		sb.append(" R.LKUP_HOLDS_TYPE_ID ");
		sb.append(" ,");
		sb.append(" R.LKUP_HOLDS_STATUS_ID ");
		sb.append(" , ");
		sb.append(" H.DESCRIPTION AS LKUP_HOLDS_TYPE_TEXT");
		sb.append(" , ");
		sb.append(" S.DESCRIPTION AS LKUP_HOLDS_STATUS_TEXT ");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" R.CREATED_DATE, ");
		sb.append(" R.UPDATED_DATE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_HOLDS R  ");
		if (id > 0) {
			sb.append(" JOIN Q ON R.ID = Q.ORIG_ID OR R.ORIG_ID = Q.ORIG_ID ");
		}
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPHOLDTYPETABLE).append(" H ON R.LKUP_HOLDS_TYPE_ID = H.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.LKUPHOLDSTATUSTABLE).append(" S ON R.LKUP_HOLDS_STATUS_ID = S.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU ON R.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP ON R.UPDATED_BY = UP.ID  ");
		sb.append(" WHERE ");
		sb.append(" R.ACTIVE = 'Y' ");

		if (id < 1) {
			sb.append(" AND ");
			sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
			sb.append(" AND ");
			sb.append(" R.NEW_ID < 0 ");
		}
		sb.append(" ORDER BY R.CREATED_DATE DESC ");
		return sb.toString();
	}
	public static String holdstypes(String usersTypeId,String type) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append("  LHT.ID,LHT.ID AS VALUE,LHT.TYPE AS DESCRIPTION,LHT.DESCRIPTION AS TEXT ");
		sb.append(" FROM ");
		sb.append(" LKUP_HOLDS_TYPE LHT ").append(" JOIN REF_"+tableref+"_TYPE_HOLDS R ").append(" ON LHT.ID=R.LKUP_HOLDS_TYPE_ID  ");
		sb.append(" WHERE ");
		sb.append(" LHT.ACTIVE= 'Y' ");
		sb.append(" AND R.LKUP_"+tableref+"_TYPE_ID= "+usersTypeId+" ");

		
		return sb.toString();
	}

}















