package csapi.impl.general;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import csapi.common.Table;
import csapi.impl.entity.EntityAgent;
import csapi.impl.project.ProjectAgent;
import csapi.utils.CsReflect;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.TypeInfo;

public class GeneralSQL {


	public static String getComments(String type,int id){
		return getComments(type, id, 0);
	}
	
	public static String getComments(String type,int id,int commentId){
		
		StringBuilder sb = new StringBuilder();
		
		if(type.equalsIgnoreCase("A")){
			sb.append("SELECT RAC.ID,N.NOTE,N.DATE  ");
			sb.append(" FROM  ").append(Table.REFACTCOMMENTSTABLE).append("  RAC ");
			sb.append(" LEFT OUTER JOIN  ").append(Table.NOTESTABLE).append("  N ON RAC.COMNT_ID=N.ID ");
			sb.append(" where ACTIVITY_ID= ").append(id).append(" and N.ACTIVE='Y' ");
			if(commentId>0){
				sb.append("  and RAC.ID= ").append(commentId);
			}
			sb.append("  order by N.DATE DESC ");
		} else if(type.equalsIgnoreCase("P")){
			sb.append("SELECT RAC.ID,N.NOTE,N.DATE  ");
			sb.append(" FROM  ").append(Table.REFPROJECTCOMMENTSTABLE).append("  RAC ");
			sb.append(" LEFT OUTER JOIN  ").append(Table.NOTESTABLE).append("  N ON RAC.COMNT_ID=N.ID ");
			sb.append(" where PROJ_ID= ").append(id).append(" and N.ACTIVE='Y' ");
			if(commentId>0){
				sb.append("  and RAC.ID= ").append(commentId);
			}
			sb.append("  order by N.DATE DESC ");
		}else {
			sb.append("SELECT RAC.ID,N.NOTE,N.DATE  ");
			sb.append(" FROM  ").append(Table.REFLSOCOMMENTSTABLE).append("  RAC ");
			sb.append(" LEFT OUTER JOIN  ").append(Table.NOTESTABLE).append("  N ON RAC.COMNT_ID=N.ID ");
			sb.append(" where LSO_ID= ").append(id).append(" and N.ACTIVE='Y' ");
			if(commentId>0){
				sb.append("  and RAC.ID= ").append(commentId);
			}
			sb.append("  order by N.DATE DESC ");
		}
		return sb.toString();
	}
	
	public static String getAttachments(String type,int id){
		
		StringBuilder sb = new StringBuilder();
		
		if(type.equalsIgnoreCase("A")){
			sb.append("SELECT RAC.ID,RAC.TITLE,RAC.CREATED_DATE  ");
			sb.append(" FROM  ").append(Table.REFACTATTACHMENTSTABLE).append("  RAC ");
			//sb.append(" LEFT OUTER JOIN  ").append(Table.ATTACHMENTSTABLE).append("  N ON RAC.ATTACHMENT_ID=N.ID ");
			sb.append(" where ACTIVITY_ID= ").append(id).append("  ");
			sb.append("  order by RAC.CREATED_DATE DESC ");
		} else if(type.equalsIgnoreCase("P")){
			sb.append("SELECT RAC.ID,RAC.TITLE,RAC.CREATED_DATE  ");
			sb.append(" FROM  ").append(Table.REFPROJECTATTACHMENTSTABLE).append("  RAC ");
			//sb.append(" LEFT OUTER JOIN  ").append(Table.ATTACHMENTSTABLE).append("  N ON RAC.ATTACHMENT_ID=N.ID ");
			sb.append(" where PROJ_ID= ").append(id).append("  ");
			sb.append("  order by RAC.CREATED_DATE DESC ");
		}else {
			sb.append("SELECT RAC.ID,RAC.TITLE,RAC.CREATED_DATE  ");
			sb.append(" FROM  ").append(Table.REFLSOATTACHMENTSTABLE).append("  RAC ");
			//sb.append(" LEFT OUTER JOIN  ").append(Table.ATTACHMENTSTABLE).append("  N ON RAC.ATTACHMENT_ID=N.ID ");
			sb.append(" where LSO_ID= ").append(id).append("  ");
			sb.append("  order by RAC.CREATED_DATE DESC ");
		}
		return sb.toString();
	}
	
	public static String getCustomFields(String type,int id){
		StringBuilder sb = new StringBuilder();
		
		if(type.equalsIgnoreCase("A")){
			sb.append(" select *   ");
			sb.append(" from  ").append(Table.REFACTFIELDGROUPSTABLE).append("   V   ");
			sb.append(" where ACT_TYPE_ID= ").append(id).append("  and V.ACTIVE='Y'  ");
			sb.append(" order by ORDR ");
		} else if(type.equalsIgnoreCase("P")){
			//TODO
			sb.append(" select *   ");
			sb.append(" from  ").append(Table.REFACTFIELDGROUPSTABLE).append("   V   ");
			sb.append(" where ACT_TYPE_ID= ").append(id).append("  and V.ACTIVE='Y'  ");
			sb.append(" order by ORDR ");
		}else {
			sb.append(" select *   ");
			sb.append(" from  ").append(Table.REFLSOTYPEFIELDGROUPSTABLE).append("   V   ");
			sb.append(" where LKUP_LSO_TYPE_ID= ").append(id).append("  and V.ACTIVE='Y'  ");
			sb.append(" order by ORDR ");
		}
		return sb.toString();
	}
	
	public static String getCustomFields(String type,int id,int fieldgroupId){
		
		StringBuilder sb = new StringBuilder();
		
		if(type.equalsIgnoreCase("A")){
			sb.append(" select V.ID,F.NAME,V.VALUE,FG.GROUP_NAME,FG.ID as GROUP_ID ");
			sb.append(" from  ").append(Table.ACTFIELDVALUETABLE).append("   V   ");
			sb.append(" join  ").append(Table.FIELDTABLE).append("  F on V.FIELD_ID=F.ID ");
			sb.append(" join   ").append(Table.FIELDGROUPSTABLE).append("  FG on F.FIELD_GROUPS_ID=FG.ID ");
			sb.append(" where ACTIVITY_ID= ").append(id).append(" ");
			sb.append(" AND ");
			sb.append(" FG.ID= ").append(fieldgroupId);
			sb.append(" and V.ACTIVE='Y' and F.ACTIVE='Y' and FG.ACTIVE='Y'  ");
			sb.append(" order by F.ORDR ");
		} else if(type.equalsIgnoreCase("P")){
			sb.append(" select V.ID,F.NAME,V.VALUE,FG.GROUP_NAME,FG.ID as GROUP_ID  ");
			sb.append(" from  ").append(Table.PROJECTFIELDVALUETABLE).append("   V   ");
			sb.append(" join  ").append(Table.FIELDTABLE).append("  F on V.FIELD_ID=F.ID ");
			sb.append(" join   ").append(Table.FIELDGROUPSTABLE).append("  FG on F.FIELD_GROUPS_ID=FG.ID ");
			sb.append(" where V.ID= ").append(id).append(" ");
			sb.append(" AND ");
			sb.append(" FG.ID= ").append(fieldgroupId);
			sb.append(" and V.ACTIVE='Y' and F.ACTIVE='Y' and FG.ACTIVE='Y'  ");
		
			sb.append(" order by F.ORDR ");
		} else if(type.equalsIgnoreCase("SITE")){
			sb.append(" select V.ID,F.NAME,V.VALUE,FG.GROUP_NAME,FG.ID as GROUP_ID  ");
			sb.append(" from  ").append(Table.LSOSITEDATAFIELDVALUETABLE).append("   V   ");
			sb.append(" join  ").append(Table.FIELDTABLE).append("  F on V.FIELD_ID=F.ID ");
			sb.append(" join   ").append(Table.FIELDGROUPSTABLE).append("  FG on F.FIELD_GROUPS_ID=FG.ID ");
			sb.append(" where V.LSO_SITEDATA_ID= ").append(id).append(" ");
			sb.append(" AND ");
			sb.append(" FG.ID= ").append(fieldgroupId);
			sb.append(" and V.ACTIVE='Y' and F.ACTIVE='Y' and FG.ACTIVE='Y'  ");
		
			sb.append(" order by F.ORDR ");
		}else {
			sb.append(" select V.ID,F.NAME,V.VALUE,FG.GROUP_NAME,FG.ID as GROUP_ID  ");
			sb.append(" from  ").append(Table.LSOFIELDVALUETABLE).append("   V   ");
			sb.append(" join  ").append(Table.FIELDTABLE).append("  F on V.FIELD_ID=F.ID ");
			sb.append(" join   ").append(Table.FIELDGROUPSTABLE).append("  FG on F.FIELD_GROUPS_ID=FG.ID ");
			
			sb.append(" where LSO_ID= ").append(id).append(" ");
			sb.append(" AND ");
			sb.append(" FG.ID= ").append(fieldgroupId);
			sb.append(" and V.ACTIVE='Y' and F.ACTIVE='Y' and FG.ACTIVE='Y'  ");
			
			sb.append(" order by F.ORDR ");
		}
		return sb.toString();
	}

	public static String getLibraryGroup(String type, int typeid) {
		String tableref = CsReflect.getTableRef(type);
		String typetableref = CsReflect.getTypeTable(type);
		String table = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		if (!Operator.hasValue(typetableref)) {
			sb = new StringBuilder();
			sb.append("LKUP_").append(tableref).append("_TYPE");
			typetableref = sb.toString();
		}
		sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" G.* ");
		sb.append(" FROM ");
		sb.append(" LIBRARY_GROUP AS G ");
		sb.append(" JOIN REF_").append(tableref).append("_LIBRARY_GROUP AS R ON G.ID = R.LIBRARY_GROUP_ID AND R.ACTIVE = 'Y' AND G.ACTIVE = 'Y' ");
		sb.append(" JOIN ").append(typetableref).append(" AS T ON R.").append(typetableref).append("_ID = T.ID AND T.ACTIVE = 'Y' ");
		sb.append(" JOIN ").append(table).append(" AS M ON M.").append(typetableref).append("_ID = T.ID AND M.ID = ").append(typeid);
		return sb.toString();
	}

	public static String getHolds(String type,int id){
		
		StringBuilder sb = new StringBuilder();
		/*
		if(type.equalsIgnoreCase("A")){
			sb.append(" SELECT RAH.ID,RAH.DESCRIPTION,LH.HOLD,LH.DESCRIPTION as HOLD_DESCRIPTION, CU.USERNAME AS CREATED, UP.USERNAME as UPDATED, RAH.CREATED_DATE, RAH.UPDATED_DATE ");
			sb.append(" FROM ").append(Table.REFACTHOLDTABLE).append(" RAH  ");
			sb.append(" LEFT OUTER JOIN ").append(Table.LKUPHOLDTABLE).append(" LH ON RAH.LKUP_HOLDS_ID=LH.ID ");
			sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on RAH.CREATED_BY=CU.ID ");
			sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on RAH.UPDATED_BY=UP.ID  ");
			sb.append(" WHERE ACTIVITY_ID=").append(id).append("  and RAH.ACTIVE='Y' and LH.ACTIVE='Y' ");
		} else if(type.equalsIgnoreCase("P")){
			sb.append(" SELECT RAH.ID,RAH.DESCRIPTION,LH.HOLD,LH.DESCRIPTION as HOLD_DESCRIPTION, CU.USERNAME AS CREATED, UP.USERNAME as UPDATED, RAH.CREATED_DATE, RAH.UPDATED_DATE ");
			sb.append(" FROM ").append(Table.REFPROJECTHOLDTABLE).append(" RAH  ");
			sb.append(" LEFT OUTER JOIN ").append(Table.LKUPHOLDTABLE).append(" LH ON RAH.LKUP_HOLDS_ID=LH.ID ");
			sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on RAH.CREATED_BY=CU.ID ");
			sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on RAH.UPDATED_BY=UP.ID  ");
			sb.append(" WHERE PROJ_ID=").append(id).append("  and RAH.ACTIVE='Y' and LH.ACTIVE='Y' ");
		}else {
			sb.append(" SELECT RAH.ID,RAH.DESCRIPTION,LH.HOLD,LH.DESCRIPTION as HOLD_DESCRIPTION, CU.USERNAME AS CREATED, UP.USERNAME as UPDATED, RAH.CREATED_DATE, RAH.UPDATED_DATE ");
			sb.append(" FROM ").append(Table.REFLSOHOLDTABLE).append(" RAH  ");
			sb.append(" LEFT OUTER JOIN ").append(Table.LKUPHOLDTABLE).append(" LH ON RAH.LKUP_HOLDS_ID=LH.ID ");
			sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on RAH.CREATED_BY=CU.ID ");
			sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on RAH.UPDATED_BY=UP.ID  ");
			sb.append(" WHERE LSO_ID=").append(id).append("  and RAH.ACTIVE='Y' and LH.ACTIVE='Y' ");
		}*/
		return sb.toString();
	}
	
	
	public static String getConditions(String type,int id){
		StringBuilder sb = new StringBuilder();
		
		if(type.equalsIgnoreCase("A")){
			sb.append(" SELECT  RALL.ID,SHORT_TEXT, RALL.CREATED_DATE ");
			sb.append(" FROM ").append(Table.LKUPLIBRARYTABLE).append(" LL  ");
			sb.append(" JOIN ").append(Table.REFACTLKUPLIBRARYTABLE).append(" RALL ON LL.ID=RALL.LKUP_LIBRARY_ID");
		/*	sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on RAH.CREATED_BY=CU.ID ");
			sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on RAH.UPDATED_BY=UP.ID  ");*/
			sb.append(" WHERE ACTIVITY_ID=").append(id).append("  and LL.ACTIVE='Y' and RALL.ACTIVE='Y' ");
		} else if(type.equalsIgnoreCase("P")){
			sb.append(" SELECT  RALL.ID,SHORT_TEXT, RALL.CREATED_DATE ") ;
			sb.append(" FROM ").append(Table.LKUPLIBRARYTABLE).append(" LL  ");
			sb.append(" JOIN ").append(Table.REFPROJECTLKUPLIBRARYTABLE).append(" RALL ON LL.ID=RALL.LKUP_LIBRARY_ID");
		/*	sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on RAH.CREATED_BY=CU.ID ");
			sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on RAH.UPDATED_BY=UP.ID  ");*/
			sb.append(" WHERE PROJ_ID=").append(id).append("  and LL.ACTIVE='Y' and RALL.ACTIVE='Y' ");
		}else {
			sb.append(" SELECT  RALL.ID,SHORT_TEXT, RALL.CREATED_DATE ");
			sb.append(" FROM ").append(Table.LKUPLIBRARYTABLE).append(" LL  ");
			sb.append(" JOIN ").append(Table.REFLSOLKUPLIBRARYTABLE).append(" RALL ON LL.ID=RALL.LKUP_LIBRARY_ID");
		/*	sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on RAH.CREATED_BY=CU.ID ");
			sb.append(" LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on RAH.UPDATED_BY=UP.ID  ");*/
			sb.append(" WHERE LSO_ID=").append(id).append("  and LL.ACTIVE='Y' and RALL.ACTIVE='Y' ");
		}
		return sb.toString();
	}
	
	public static String updateCommon(RequestVO r, Token u) {
		return updateCommon(r.getGrouptype(), r, u.getId());
	}
	
	public static String updateCommon(String table, RequestVO r, Token u) {
		return updateCommon(table, r, u.getId());
	}
	
	public static String updateCommon(String table, RequestVO r, int userid) {
		StringBuilder sb = new StringBuilder();
		try{
			int id = Operator.toInt(r.getId());
			ObjGroupVO o = r.getData()[0];
			ObjVO[] obj = o.getObj();
			if(id>0 && obj.length>0){
			
				sb.append(" UPDATE ").append(table).append(" SET ");
				for(int i=0;i<obj.length;i++){
					ObjVO vo = obj[i];
					String field = vo.getField();
					String value = vo.getValue();
					String itype = vo.getItype();
					boolean editable = vo.isEditable();
					boolean updateIfValuePresent = vo.updateIfValuePresent();
					boolean updateSameTable = vo.updateSameTable();
					if (editable && updateIfValuePresent && updateSameTable) {
						if (itype.equalsIgnoreCase("integer")) {
							sb.append(" ").append(field).append(" = '").append(value).append("' ");
						}
						else {
							sb.append(" ").append(field).append(" = '").append(Operator.sqlEscape(value)).append("' ");
						}
						
						sb.append(",");
					}
				}
				sb.append("  UPDATED_DATE = CURRENT_TIMESTAMP, UPDATED_BY= ").append(userid);
				sb.append(" WHERE ID =").append(id);
			}
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return sb.toString();
	}

	public static String insertCommon(RequestVO r, Token u) {
		return insertCommon(r.getGrouptype(), r, u.getId());
	}
	
	public static String insertCommon(String table, RequestVO r, Token u) {
		return insertCommon(table, r, u.getId());
	}
	
	/**
	 * sunil
	 * @param table
	 * @param r
	 * @param userid
	 * @return
	 */
	public static String insertCommon(String table, RequestVO r, int userid) {
		StringBuilder sb = new StringBuilder();
		try {
			ObjGroupVO o = r.getData()[0];
			ObjVO[] obj = o.getObj();
			if(obj.length>0){
				sb.append(" insert into  ").append(table).append(" ( ");
				StringBuilder t = new StringBuilder();
				StringBuilder v = new StringBuilder();
				for(int i=0;i<obj.length;i++){
					ObjVO vo = obj[i];
					String field = vo.getField();
					String value = vo.getValue();
					boolean addable = vo.isAddable();
					boolean isSystemGenerated = vo.isSystemGenerated();
					boolean updateSameTable = vo.updateSameTable();
					if (addable  && updateSameTable && !isSystemGenerated) {
						t.append(field).append(",");
						
						v.append("'").append(Operator.sqlEscape(value)).append("' ");
						v.append(",");
					}
					else if(isSystemGenerated){
						String number = "";
						boolean type = false;
						if(field.equals("_TYPE_ID")){
							String fieldcolumn = CsReflect.getFieldIdRef(r.getType());
							if(Operator.hasValue(fieldcolumn)){
								field = fieldcolumn;
								number = Operator.toString(r.getTypeid());
								type = true;
							}
						}
						if(!type){
							number = CsReflect.getSystemGenerated(r, field);
						}
						t.append(field).append(",");
						
						Logger.info(number);
						v.append("'").append(number).append("'");
						v.append(",");
					}
					
				}
				
				t.append(" CREATED_BY,UPDATED_BY,CREATED_DATE,UPDATED_DATE, ACTIVE) VALUES ( ");
				
				sb.append(t.toString());
				sb.append(v.toString());
				
				sb.append(userid);
				sb.append(",");
				sb.append(userid);
				sb.append(",");
				sb.append(" CURRENT_TIMESTAMP ");
				sb.append(",");
				sb.append(" CURRENT_TIMESTAMP ");
				sb.append(" , ");
				sb.append(" 'Y' ");
				sb.append(" ) "); 
			}
		}
		catch(Exception e){
			Logger.error(e.getMessage());
		}
		return sb.toString();
	}
	
	public static String insertRefCommon(RequestVO r, Token u,int id) {
		String type = r.getType();
		int typeId = r.getTypeid();
		int userId = u.getId();
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String idtype = CsReflect.getFieldIdRef(r.getGrouptype());
		String maintableref = CsReflect.getMainTableRef(r.getGrouptype());
		String table = "REF_"+tableref+"_"+maintableref;
		return insertRefCommon(typeId, userId, id, table, idref,idtype);
	}
	
	public static String insertRefCommon(int typeId, int userId, int id, String table, String idref, String idtype) {
		StringBuilder sb = new StringBuilder();
		if(id>0){
			sb.append(" INSERT INTO ").append(table).append(" (").append(idref).append(", ").append(idtype).append(", CREATED_BY,UPDATED_BY,CREATED_DATE,UPDATED_DATE,ACTIVE  ) ");
			sb.append(" VALUES ( ");
			sb.append(typeId);
			sb.append(",");
			sb.append(id);
			sb.append(",");
			sb.append(userId);
			sb.append(",");
			sb.append(userId);
			sb.append(",");
			sb.append(" CURRENT_TIMESTAMP ");
			sb.append(",");
			sb.append(" CURRENT_TIMESTAMP ");
			sb.append(" , ");
			sb.append(" 'Y' ");
			sb.append(" ) "); 
		}
		return sb.toString();
	}

	// By Alain - in use - do not remove
	public static String insertRef(String type, String subtype, int typeid, int subtypeid, int user) {
		StringBuilder sb = new StringBuilder();
		if (Operator.hasValue(type) && Operator.hasValue(subtype) && typeid > 0 && subtypeid > 0) {
			String typeref = CsReflect.getTableRef(type);
			String typeidref = CsReflect.getFieldIdRef(type);
			String styperef = CsReflect.getTableRef(subtype);
			String stypeidref = CsReflect.getFieldIdRef(subtype);
			sb.append(" INSERT INTO REF_").append(typeref).append("_").append(styperef).append(" ( ");
			sb.append(typeidref);
			sb.append(" , ");
			sb.append(stypeidref);
			sb.append(" , ");
			sb.append(" CREATED_BY ");
			sb.append(" , ");
			sb.append(" UPDATED_BY ");
			sb.append(" ) VALUES ( ");
			sb.append(typeid);
			sb.append(" , ");
			sb.append(subtypeid);
			sb.append(" , ");
			sb.append(user);
			sb.append(" , ");
			sb.append(user);
			sb.append(" ) ");
		}
		return sb.toString();
	}

	public static String getRef(String type, String subtype, int typeid) {
		StringBuilder sb = new StringBuilder();
		if (Operator.hasValue(type) && Operator.hasValue(subtype) && typeid > 0) {
			String typeref = CsReflect.getTableRef(type);
			String typeidref = CsReflect.getFieldIdRef(type);
			String styperef = CsReflect.getTableRef(subtype);
			sb.append(" SELECT ");
			sb.append(" * ");
			sb.append(" FROM ");
			sb.append(" REF_").append(typeref).append("_").append(styperef).append(" ");
			sb.append(" WHERE ");
			sb.append(typeidref).append(" = ").append(typeid);
			sb.append(" AND ");
			sb.append(" ACTIVE = 'Y' ");
		}
		return sb.toString();
	}

	public static String deleteRef(String type, int typeid, String group, int id, int userid) {
		if (!Operator.hasValue(type) || !Operator.hasValue(group) || typeid < 1 || id < 1) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);

		String gtableref = CsReflect.getTableRef(group);
		String gidref = CsReflect.getFieldIdRef(group);
		Logger.info("fffff"+gidref);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(" REF_").append(tableref).append("_").append(gtableref);
		sb.append(" SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(idref).append(" = ").append(typeid);
		sb.append(" AND ");
		sb.append(gidref).append(" = ").append(id);
		return sb.toString();
	}

	public static String delete(String type, int id, int userid) {
		String tableref = CsReflect.getTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ");
		sb.append(tableref);
		sb.append(" SET ");
		sb.append(" ACTIVE = 'N' ");
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		return sb.toString();
	}




	/*public static String getPatternId(RequestVO r, int type_typeid){
		return getPatternId(r.getGrouptype(), type_typeid);
	}*/

	public static String getPatternId(String type, int type_typeid){
		StringBuilder sb = new StringBuilder();
		String tableref = CsReflect.getTableRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		/*String tableref = CsReflect.getTableRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT T.TYPE AS TYPE, P.PATTERN, COUNT (A.ID) as G_PATTERN_ID   FROM  LKUP_").append(tableref).append("_TYPE AS T  JOIN LKUP_PATTERN P on T.PATTERN_ID=P.ID ");
		sb.append(" LEFT OUTER JOIN ").append(maintableref).append(" A on T.ID= A.LKUP_").append(tableref).append("_TYPE_ID  ");
		sb.append(" WHERE P.ACTIVE ='Y' AND T.ID =  ").append(type_typeid).append(" GROUP BY T.TYPE,P.PATTERN ");
		//sb.append(" UNION ");
		//sb.append(" SELECT  '' AS TYPE, MAX(ID) +1 AS ID,'' as PATTERN, 0 as G_PATTERN_ID FROM  ").append(maintableref).append("  ORDER BY TYPE");
*/		
		sb.append(" select P.*  from  LKUP_").append(tableref).append("_TYPE T LEFT OUTER JOIN LKUP_PATTERN P on T.LKUP_PATTERN_ID=P.ID where T.ID = ").append(type_typeid);

		return sb.toString();
	}
	
	
	public static String getPatternId(String type,int patternId, boolean year, boolean month, boolean day){
		StringBuilder sb = new StringBuilder();
		String tableref = CsReflect.getTableRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		
		sb.append(" select P.ID, COUNT (A.ID) as G_PATTERN_ID  ");
		sb.append(" from  ").append(maintableref).append("  A ");
		sb.append(" JOIN LKUP_").append(tableref).append("_TYPE LAT on A.LKUP_").append(tableref).append("_TYPE_ID= LAT.ID ");
		sb.append(" LEFT OUTER JOIN LKUP_PATTERN P on  LAT.LKUP_PATTERN_ID = P.ID ");
		sb.append(" WHERE LAT.LKUP_PATTERN_ID =").append(patternId);
		if(year){
			sb.append(" AND YEAR(A.CREATED_DATE) = YEAR(getdate()) ");
		}
		
		if(month){
			sb.append(" AND MONTH(A.CREATED_DATE) = MONTH(getdate()) ");
		}
		if(day){
			sb.append(" AND DAY(A.CREATED_DATE) = DAY(getdate()) ");
		}
		sb.append(" group by P.ID ");

		return sb.toString();
	}



	public static String getStreetList(){
		StringBuilder sb = new StringBuilder();
		sb.append(" select ID ,   ");
		sb.append(" CASE WHEN PRE_DIR is not null then PRE_DIR+' ' ELSE '' END + ");
		sb.append(" CASE WHEN STR_NAME is not null then STR_NAME+' ' ELSE '' END  + ");
		sb.append(" CASE WHEN STR_TYPE is not null then STR_TYPE+' ' ELSE '' END  as VALUE, ");
		sb.append(" CASE WHEN PRE_DIR is not null then PRE_DIR+' ' ELSE '' END + ");
		sb.append(" CASE WHEN STR_NAME is not null then STR_NAME+' ' ELSE '' END  + ");
		sb.append(" CASE WHEN STR_TYPE is not null then STR_TYPE+' ' ELSE '' END  as TEXT ");
		sb.append(" from LSO_STREET where ACTIVE ='Y' ");
		return sb.toString();
	}


	public static String getStreetFraction(){
		StringBuilder sb = new StringBuilder();
		sb.append(" select DISTINCT STR_MOD as ID,STR_MOD as VALUE, STR_MOD as TEXT  FROM LSO WHERE STR_MOD is not null  ");
		return sb.toString();
	}

	public static String getAllIds(String type, int typeid) {
		TypeInfo m = EntityAgent.getEntity(type, typeid);
		String e = m.getEntity();
		int eid = m.getEntityid();
		return getCurrentAndChildIds(e, eid);
	}

	public static String getCurrentAndChildIds(String type, int typeid) {
		StringBuilder sb = new StringBuilder();

		if (type.equalsIgnoreCase("lso")) {
			sb.append(" WITH E AS ( ");
			sb.append("   SELECT ");
			sb.append("     'lso' AS TYPE ");
			sb.append("     ,  ");
			sb.append("     ").append(typeid).append(" AS ID ");

			sb.append("   UNION ");

			sb.append("   SELECT ");
			sb.append("     'lso' AS TYPE ");
			sb.append("     ,  ");
			sb.append("     ID ");
			sb.append("   FROM ");
			sb.append("     LSO ");
			sb.append("   WHERE ");
			sb.append("     PARENT_ID = ").append(typeid);

			sb.append("   UNION ");

			sb.append("   SELECT ");
			sb.append("     'lso' AS TYPE ");
			sb.append("     ,  ");
			sb.append("     O.ID ");
			sb.append("   FROM ");
			sb.append("     LSO AS S ");
			sb.append("     JOIN LSO AS O ON S.ID = O.PARENT_ID ");
			sb.append("   WHERE ");
			sb.append("     S.PARENT_ID = ").append(typeid);
			sb.append(" ) ");

			sb.append(" , P AS ( ");
			sb.append("   SELECT ");
			sb.append("     'project' AS TYPE ");
			sb.append("     ,  ");
			sb.append("     PROJECT_ID AS ID ");
			sb.append("   FROM ");
			sb.append("     REF_LSO_PROJECT AS PR ");
			sb.append("     JOIN E ON PR.LSO_ID = E.ID ");
			sb.append(" ) ");

			sb.append(" , A AS ( ");
			sb.append("   SELECT ");
			sb.append("     'activity' AS TYPE ");
			sb.append("     ,  ");
			sb.append("     ACT.ID ");
			sb.append("   FROM ");
			sb.append("     ACTIVITY AS ACT ");
			sb.append("     JOIN P ON P.ID = ACT.PROJECT_ID ");
			sb.append(" ) ");

			sb.append(" SELECT * FROM E ");
			sb.append(" UNION ");
			sb.append(" SELECT * FROM P ");
			sb.append(" UNION ");
			sb.append(" SELECT * FROM A ");
		}
		else if (type.equalsIgnoreCase("project")) {
			sb.append("   SELECT ");
			sb.append("     'project' AS TYPE ");
			sb.append("     ,  ");
			sb.append("     ").append(typeid).append(" AS ID ");

			sb.append("   UNION ");

			sb.append("   SELECT ");
			sb.append("     'activity' AS TYPE ");
			sb.append("     ,  ");
			sb.append("     ACT.ID ");
			sb.append("   FROM ");
			sb.append("     ACTIVITY AS ACT ");
			sb.append("   WHERE ");
			sb.append("     ACT.PROJECT_ID = ").append(typeid);
		}
		else if (type.equalsIgnoreCase("activity")) {
			sb.append(" SELECT ");
			sb.append("     'activity' AS TYPE ");
			sb.append("     ,  ");
			sb.append("     ").append(typeid).append(" AS ID ");
		}
		else {
			String tableref = CsReflect.getTableRef(type);
			String idref = CsReflect.getFieldIdRef(type);
			sb.append(" WITH E AS ( ");
			sb.append("   SELECT ");
			sb.append("     '").append(type.toLowerCase()).append("' AS TYPE ");
			sb.append("     ,  ");
			sb.append("     ").append(typeid).append(" AS ID ");
			sb.append(" ) ");

			sb.append(" , P AS ( ");
			sb.append("   SELECT ");
			sb.append("     'project' AS TYPE ");
			sb.append("     ,  ");
			sb.append("     PROJECT_ID AS ID ");
			sb.append("   FROM ");
			sb.append("     REF_").append(tableref).append("_PROJECT AS PR ");
			sb.append("     JOIN E ON PR.").append(idref).append(" = E.ID ");
			sb.append(" ) ");

			sb.append(" , A AS ( ");
			sb.append("   SELECT ");
			sb.append("     'activity' AS TYPE ");
			sb.append("     ,  ");
			sb.append("     ACT.ID ");
			sb.append("   FROM ");
			sb.append("     ACTIVITY AS ACT ");
			sb.append("     JOIN P ON P.ID = ACT.PROJECT_ID ");
			sb.append(" ) ");

			sb.append(" SELECT * FROM E ");
			sb.append(" UNION ");
			sb.append(" SELECT * FROM P ");
			sb.append(" UNION ");
			sb.append(" SELECT * FROM A ");
		}



		return sb.toString();
	}


	public static String getUsersType(String isdot){
		StringBuilder sb = new StringBuilder();
		sb.append(" select DISTINCT ID,TYPE as VALUE, DESCRIPTION as TEXT from LKUP_USERS_TYPE WHERE active='Y' and isonline='Y' and isdot= '").append(isdot).append("'");
		return sb.toString();
	}


	public static String getAttachmentsType(String isdot){
		StringBuilder sb = new StringBuilder();
		sb.append(" select DISTINCT ID,TYPE as VALUE, DESCRIPTION as TEXT from LKUP_ATTACHMENTS_TYPE WHERE active='Y' and isdot= '").append(isdot).append("'");
		return sb.toString();
	}

	public static String getApplicationStatus(String def){
		StringBuilder sb = new StringBuilder();
		sb.append(" select DISTINCT ID,STATUS as VALUE, DESCRIPTION as TEXT from LKUP_ACCOUNT_APPLICATION_STATUS WHERE active = 'Y'");
		if(Operator.hasValue(def)){
			sb.append(" AND DEFLT = '").append(def).append("'");
		}else{
			sb.append(" AND DEFLT IS NULL");
		}
		return sb.toString();
	}
}












