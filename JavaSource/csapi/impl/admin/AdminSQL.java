package csapi.impl.admin;

import java.util.ArrayList;
import java.util.HashMap;

import com.itextpdf.text.log.SysoCounter;

import alain.core.security.Token;
import alain.core.utils.Cartographer;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import csapi.common.Table;
import csapi.utils.CsReflect;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;

public class AdminSQL {

	

	public static String details(String type, int typeid, int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ");
		sb.append(" ( "); 
		sb.append("   SELECT "); 
		sb.append("     TOP 1 "); 
		sb.append("     ATS.ACTIVITY_ID, "); 
		sb.append("     ATS.LKUP_ACT_STATUS_ID,"); 
		sb.append("     LAS.STATUS "); 
		sb.append("   FROM "); 
		sb.append("     ").append(Table.ACTSTATUSTABLE).append(" ATS "); 
		sb.append("     LEFT OUTER JOIN LKUP_ACT_STATUS LAS on ATS.LKUP_ACT_STATUS_ID = LAS.ID ");
		sb.append("   WHERE "); 
		sb.append("     ATS.ACTIVITY_ID = ").append(typeid).append(" "); 
		sb.append("     AND "); 
		sb.append("     ATS.ACTIVE = 'Y' "); 
		sb.append("   ORDER BY "); 
		sb.append("     DATE DESC");
		sb.append(" ) ");
					
		sb.append(" SELECT "); 
		sb.append("   A.*,"); 
		sb.append("   ATT.DESCRIPTION AS TITLE, "); 
		sb.append("   ATT.DESCRIPTION AS LKUP_ACT_TYPE_ID_TEXT, "); 
		
		sb.append("   A.ACT_NBR AS SUBTITLE, "); 
		sb.append("   LAS.STATUS, "); 
		sb.append("   CU.USERNAME AS CREATED, "); 
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" FROM "); 
		sb.append("   ").append(Table.ACTIVITYTABLE).append(" A ");
		sb.append("   JOIN  ").append(Table.ACTTYPETABLE).append(" ATT ON A.LKUP_ACT_TYPE_ID = ATT.ID ");
		sb.append("   JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID = LAS.ID ");
//		sb.append("   LEFT OUTER JOIN Q on A.ID = Q.ACTIVITY_ID ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 

		sb.append(" WHERE "); 
		sb.append("   A.ID = ").append(typeid).append(" "); 
		sb.append("   AND "); 
		sb.append("   A.ACTIVE = 'Y' ");
		
		return sb.toString();
	}

	public static String getRefTables(Cartographer map){
					
		StringBuilder s = new StringBuilder();
		s.append(" SELECT LAM.SOURCE_TABLE AS BASE_TABLE,LASM.REF_TABLE,LAMS.SOURCE_TABLE,LAMS.NAME AS MODULE_NAME FROM LKUP_ADMIN_MODULE LAM JOIN LKUP_ADMIN_SUB_MODULE LASM ON "
				+ "LAM.ID= LASM.LKUP_ADMIN_SUB_MODULE_ID JOIN LKUP_ADMIN_MODULE LAMS ON LASM.LKUP_ADMIN_MODULE_ID=LAMS.ID WHERE (LAM.SOURCE_TABLE IS NOT NULL AND LAM.SOURCE_TABLE NOT IN ('')) AND (LASM.REF_TABLE IS NOT NULL AND LASM.REF_TABLE NOT IN ('')) AND LAM.ID= ").append(map.getString("_id"));
		
		return s.toString();
	}
	
	
	public static String getList(String table,int start, int end,String sortfield,String sortorder,String query, String searchField){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM (");
		sb.append(" SELECT ROW_NUMBER() OVER (ORDER BY A.").append(sortfield).append(" ").append(sortorder).append(") AS RowNum  ");
		sb.append(",");
		sb.append(" A.* ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		
		sb.append(" from ").append(table).append(" A  ");
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" WHERE A.ACTIVE='Y' ");
		
		if(table.equalsIgnoreCase("LKUP_FORMULA")){
			sb.append(" AND  A.ORIGINAL_ID=0 ");
		}
		if(Operator.hasValue(searchField) && Operator.hasValue(query)){
			String s[] = Operator.split(searchField,",");
			int l = s.length-1;
			sb.append(" AND ( ");
			for(int i=0;i<s.length;i++){
				sb.append("  LOWER(A.").append(s[i]).append(") like '%").append(Operator.sqlEscape(query.toLowerCase())).append("%'  ");
				Logger.info(i+"#############"+l);
				if(i<l){
					sb.append(" OR ");
				}
			}
					
			sb.append(" ) ");
		}
		
		sb.append(" ) ");
		sb.append(" AS RowConstrainedResult WHERE RowNum >").append(start).append(" AND RowNum <=").append(end).append(" order by rownum ");
		return sb.toString();
	
	}
	
	
	public static String getListWithAdditional(String table,int start, int end,String sortfield,String sortorder,String query, String searchField,String additionalColumns,String additionaljoins,String additionaland){
		StringBuilder sb = new StringBuilder();
		String msortfield =  sortfield;
		if(msortfield.indexOf(".")>0){
			
		}else {
			msortfield = "A."+sortfield;
		}
		
		sb.append(" SELECT * FROM (");
		sb.append(" SELECT ROW_NUMBER() OVER (ORDER BY ").append(msortfield).append(" ").append(sortorder).append(") AS RowNum  ");
		sb.append(",");
		sb.append(" A.* ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(additionalColumns); 
		
		sb.append(" from ").append(table).append(" A  ");
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		
		sb.append(additionaljoins); 
		
		sb.append(" WHERE A.ACTIVE='Y' ");
		
		sb.append(additionaland);
		
		if(table.equalsIgnoreCase("LKUP_FORMULA")){
			sb.append(" AND  A.ORIGINAL_ID=0 ");
		}
		if(Operator.hasValue(searchField) && Operator.hasValue(query)){
			String s[] = Operator.split(searchField,",");
			int l = s.length-1;
			sb.append(" AND ( ");
			for(int i=0;i<s.length;i++){
				String ms =  s[i];
				if(ms.indexOf(".")>0){
					
				}else {
					ms = "A."+ms;
				}
				sb.append("  LOWER(").append(ms).append(") like '%").append(Operator.sqlEscape(query.toLowerCase())).append("%'  ");
				Logger.info(i+"#############"+l);
				if(i<l){
					sb.append(" OR ");
				}
			}
					
			sb.append(" ) ");
		}
		
		sb.append(" ) ");
		sb.append(" AS RowConstrainedResult WHERE RowNum >").append(start).append(" AND RowNum <=").append(end).append(" order by rownum ");
		return sb.toString();
	
	}
	
	public static MapSet getLsoLst(Cartographer map,String table,int start, int end,String sortfield,String sortorder,String query, String searchField){
		MapSet m = new MapSet();
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT * FROM (");
		sb.append(" SELECT ROW_NUMBER() OVER (ORDER BY A.").append(sortfield).append(" ").append(sortorder).append(") AS RowNum  ");
		sb.append(",");
		sb.append("   A.*, CONCAT(LS.PRE_DIR,' ',LS.STR_NAME,' ' ,");
		sb.append("LS.STR_TYPE,' ' ,LS.SUF_DIR) AS STREET_NAME,LLT.DESCRIPTION AS LSO_TYPE, ");
		sb.append("CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   C.FIRST_NAME AS CREATED ");
		sb.append(",");
		sb.append("   U.FIRST_NAME as UPDATED "); 
		sb.append(" FROM LSO A JOIN LSO_STREET  LS ON A.LSO_STREET_ID=LS.ID JOIN LKUP_LSO_TYPE LLT ON A.LKUP_LSO_TYPE_ID=LLT.ID");
		sb.append( " JOIN USERS C ON A.CREATED_BY=C.ID JOIN USERS U ON A.UPDATED_BY=U.ID ");
		sb.append(" WHERE A.ACTIVE='Y' ");
		
		if(Operator.hasValue(searchField) && Operator.hasValue(query)){
			String s[] = Operator.split(searchField,",");
			int l = s.length-1;
			sb.append(" AND ( ");
			for(int i=0;i<s.length;i++){
				sb.append("  LOWER(A.").append(s[i]).append(") like '%").append(Operator.sqlEscape(query.toLowerCase())).append("%'  ");
				Logger.info(i+"#############"+l);
				if(i<l){
					sb.append(" OR ");
				}
			}
					
			sb.append(" ) ");
		}
		
		sb.append(" ) ");
		sb.append(" AS RowConstrainedResult WHERE RowNum >").append(start).append(" AND RowNum <=").append(end).append(" order by rownum ");
		m.add("_getlsoquery",sb.toString());
		
		return m;
	}
	
	
	public static MapSet getLsoLstPerAddress(Cartographer map,String table,int start, int end,String sortfield,String sortorder,String query, String searchField){
		MapSet m = new MapSet();
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT * FROM (");
		sb.append(" SELECT ROW_NUMBER() OVER (ORDER BY A.").append(sortfield).append(" ").append(sortorder).append(") AS RowNum  ");
		sb.append(",");
		sb.append("   A.*, CONCAT(LS.PRE_DIR,' ',LS.STR_NAME,' ' ,");
		sb.append("LS.STR_TYPE,' ' ,LS.SUF_DIR) AS STREET_NAME,LLT.DESCRIPTION AS LSO_TYPE, ");
		sb.append("CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   C.FIRST_NAME AS CREATED ");
		sb.append(",");
		sb.append("   U.FIRST_NAME as UPDATED "); 
		sb.append(" FROM LSO A JOIN LSO_STREET  LS ON A.LSO_STREET_ID=LS.ID JOIN LKUP_LSO_TYPE LLT ON A.LKUP_LSO_TYPE_ID=LLT.ID");
		sb.append( " JOIN USERS C ON A.CREATED_BY=C.ID JOIN USERS U ON A.UPDATED_BY=U.ID ");
		sb.append(" WHERE A.ACTIVE='Y' ");
		
		if(Operator.hasValue(searchField) && Operator.hasValue(query)){
			String s[] = Operator.split(searchField,",");
			int l = s.length-1;
			sb.append(" AND ( ");
			for(int i=0;i<s.length;i++){
				sb.append("  LOWER(A.").append(s[i]).append(") like '%").append(Operator.sqlEscape(query.toLowerCase())).append("%'  ");
				Logger.info(i+"#############"+l);
				if(i<l){
					sb.append(" OR ");
				}
			}
					
			sb.append(" ) ");
		}
		
		sb.append(" ) ");
		sb.append(" AS RowConstrainedResult WHERE RowNum >").append(start).append(" AND RowNum <=").append(end).append(" order by rownum ");
		m.add("_getlsoquery",sb.toString());
		
		return m;
	}
	
	
	public static String getRefList(String baseTable , String refTable,String sourceTable,Cartographer map){
		StringBuilder sb = new StringBuilder();
		if(sourceTable.equalsIgnoreCase("LIBRARY"))
		{
		sb.append(" SELECT R.ID, B.GROUP_NAME, S.TITLE AS DESCRIPTION FROM "+baseTable+" B JOIN "+refTable+" R ON B.ID=R."+baseTable+"_ID JOIN "+sourceTable+" S ON R."+sourceTable+"_ID=S.ID WHERE  R.ACTIVE='Y' AND B.ID="+map.getString("ID"));
		}else {
		sb.append(" SELECT R.ID, B.GROUP_NAME, S.DESCRIPTION FROM "+baseTable+" B JOIN "+refTable+" R ON B.ID=R."+baseTable+"_ID JOIN "+sourceTable+" S ON R."+sourceTable+"_ID=S.ID WHERE  R.ACTIVE='Y' AND B.ID="+map.getString("ID"));
		}
		return sb.toString();
	
	}
	
	public static String getRefList(String baseTable , String refTable,String sourceTable,Cartographer map,int start, int end,String sortfield,String sortorder,String query, String searchField){
		StringBuilder sb = new StringBuilder();
		System.out.println(sourceTable+"****************************************");  
		if(sourceTable.equalsIgnoreCase("LIBRARY"))
		{ 
		sb.append(" SELECT R.ID ,B.GROUP_NAME,S.TITLE AS DESCRIPTION FROM "+baseTable+" B JOIN "+refTable+" R ON B.ID=R."+baseTable+"_ID JOIN "+sourceTable+" S ON R."+sourceTable+"_ID=S.ID WHERE B.ID="+map.getString("ID"));
		}else {
		sb.append(" SELECT R.ID ,B.GROUP_NAME,S.DESCRIPTION FROM "+baseTable+" B JOIN "+refTable+" R ON B.ID=R."+baseTable+"_ID JOIN "+sourceTable+" S ON R."+sourceTable+"_ID=S.ID WHERE B.ID="+map.getString("ID"));
		}
		if(Operator.hasValue(searchField) && Operator.hasValue(query)){
			String s[] = Operator.split(searchField,",");
			int l = s.length-1;
			sb.append(" AND  ");
			for(int i=0;i<s.length;i++){
				sb.append("  LOWER(S.").append(s[i]).append(") like '%").append(Operator.sqlEscape(query.toLowerCase())).append("%'  ");
				Logger.info(i+"#############"+l);
				if(i<l){
					sb.append(" OR ");
				}
			}
		}
		return sb.toString();
	
	}
	
	
	
	public static String getType(int id,String table){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.* ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		if(table.equalsIgnoreCase(Table.LSOTABLE)){
		sb.append(",");
		sb.append("   LTT.TYPE AS LSO_TYPE "); 
		sb.append(",");
		sb.append("   RLA.APN "); 
		sb.append(",");
		sb.append("   RLA.START_DATE AS RLA_START_DATE "); 
		sb.append(",");
		sb.append("   AO.FRST_OWNR_NAME AS OWNER_NAME "); 
		}
		sb.append(" from ").append(table).append(" A  ");
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		if(table.equalsIgnoreCase(Table.LSOTABLE)){
		sb.append("   LEFT OUTER JOIN ").append(Table.LKUPLSOTYPETABLE).append(" LTT on A.LKUP_LSO_TYPE_ID = LTT.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.REFLSOAPNTABLE).append(" RLA ON A.ID=RLA.LSO_ID AND RLA.ACTIVE='Y' ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.ASSESSOROWNERTABLE).append(" AO ON AO.APN=RLA.APN AND AO.ACTIVE='Y' "); 
		}
		sb.append(" WHERE A.ACTIVE='Y' ");
		sb.append(" AND A.ID =  ").append(id);
		
		return sb.toString();
	
	}
	
	
	
	
	
	/*public static String saveProjectType(Cartographer map){
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO LKUP_PROJECT_TYPE (ID, TYPE, DESCRIPTION, ISPUBLIC, ONLINE, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, CREATED_IP, UPDATED_IP, PATTERN, ISDOT, AVAILABILITY_ID, MAX_ACTIVE_APPOINTMENTS, MAX_CHILD_APPOINTMENTS, MAX_CHILD_APPOINTMENTS_DAY) VALUES (0, '', '', '', '', '', 0, '', 0, '', '', '', '', '', 0, 0, 0, 0);");
		sb.append(" A.* ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		
		sb.append(" from LKUP_PROJECT_TYPE A  ");
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" WHERE A.ACTIVE='Y' ");
		sb.append(" AND ID =  ").append(id);
		
		return sb.toString();
	
	}*/
	
	
	public static String getIndex(String id, boolean parent, Token u) {
		String nproles = Operator.join(u.getNonpublicroles(), ",");

		StringBuilder sb = new StringBuilder();
		sb.append(" 		SELECT ");
		sb.append(" 			A.ID ");
		sb.append(" 			, ");
		sb.append(" 			A.NAME ");
		sb.append(" 			, ");
		sb.append(" 			A.DESCRIPTION ");
		sb.append(" 			, ");
		sb.append(" 			A.LOCATION ");
		sb.append(" 			, ");
		sb.append(" 			A.PARENT_ID ");
		sb.append(" 			, ");
		sb.append(" 			A.ORDR ");
		sb.append(" 			, ");
		sb.append(" 			A.ACTIVE ");
		sb.append(" 			, ");
		sb.append(" 			A.SHOW ");
		sb.append(" 			, ");
		sb.append(" 			A.SOURCE_TABLE ");
		sb.append(" 		FROM ");
		sb.append(" 			LKUP_ADMIN_MODULE AS A ");

		if (!u.isAdmin()) {
			sb.append(" 			JOIN REF_ADMIN_ROLES AS R ON A.ID = R.LKUP_ADMIN_MODULE_ID AND R.ACTIVE = 'Y' AND R.LKUP_ROLES_ID IN (").append(nproles).append(") AND 'Y' IN (R.C, R.R, R.U, R.D) ");
		}

		sb.append(" 		WHERE ");
		sb.append(" 			A.ID > 0 "); 
		
		if(parent){
			sb.append(" 		AND ");
			sb.append(" 		").append(id).append(" IN (A.PARENT_ID, A.ID ) ");
		}
		else {
			sb.append(" 		AND ");
			sb.append(" 		A.ID = ").append(id).append("  ");
		}
		sb.append(" 			AND ");
		sb.append(" 			A.ACTIVE = 'Y' ");

		sb.append(" 		ORDER BY ");
		sb.append(" 			PARENT_ID, ORDR ");
		
		return sb.toString();
	
	}
	
	public static String getSubIndex(String type,String typeid, String id){
		StringBuilder sb = new StringBuilder();
		sb.append(" select A.* from LKUP_ADMIN_SUB_MODULE S JOIN LKUP_ADMIN_MODULE A on S.LKUP_ADMIN_SUB_MODULE_ID = A.ID ");
		sb.append(" WHERE S.ID>0 "); 
		sb.append(" AND ");
		sb.append("   LKUP_ADMIN_MODULE_ID = ").append(id).append("  ");
		sb.append(" ORDER BY S.ORDR ");
		
		return sb.toString();
	
	}
	
	public static String getSubModule(String type,String typeid, String id){
		StringBuilder sb = new StringBuilder();
		sb.append(" select A.* from LKUP_ADMIN_SUB_MODULE S JOIN LKUP_ADMIN_MODULE A on S.LKUP_ADMIN_MODULE_ID = A.ID ");
		sb.append(" WHERE S.ID>0 "); 
		sb.append(" AND ");
		sb.append("   LKUP_ADMIN_SUB_MODULE_ID = ").append(id).append("  ");
		sb.append(" ORDER BY S.ORDR ");
		
		return sb.toString();
	
	}
	

	
	public static String getOrder(String table,String column, String id){
		StringBuilder sb = new StringBuilder();
		sb.append(" select IsNull(MAX(ORDR),0) +1 as ORDR from  ");
		sb.append(table); 
		sb.append(" WHERE  ");
		sb.append("   ").append(column).append(" = ").append(id).append("  ");
		return sb.toString();
	
	}


	public static String browseDefault(Token u) {
		return browse(0, u);
	}

	public static String browseChildren(int typeid, Token u) {
		return browse(typeid, u);
	}

	public static String browse(int typeid, Token u) {
		String nproles = Operator.join(u.getNonpublicroles(), ",");
		StringBuilder sb = new StringBuilder();
		sb.append(" 		SELECT DISTINCT ");
		sb.append(" 			A.ID ");
		sb.append(" 			, ");
		sb.append(" 			A.NAME ");
		sb.append(" 			, ");
		sb.append(" 			A.DESCRIPTION ");
		sb.append(" 			, ");
		sb.append(" 			A.LOCATION ");
		sb.append(" 			, ");
		sb.append(" 			A.PARENT_ID ");
		sb.append(" 			, ");
		sb.append(" 			A.ORDR ");
		sb.append(" 			, ");
		sb.append(" 			A.ACTIVE ");
		sb.append(" 			, ");
		sb.append(" 			A.SHOW ");
		sb.append(" 			, ");
		sb.append(" 			A.SOURCE_TABLE ");
		sb.append(" 			, ");
		sb.append(" 			1 AS NUMROLES ");
		sb.append(" 		FROM ");
		sb.append(" 			LKUP_ADMIN_MODULE AS A ");

		if (!u.isAdmin()) {
			sb.append(" 		JOIN REF_ADMIN_ROLES AS R ON A.ID = R.LKUP_ADMIN_MODULE_ID AND R.ACTIVE = 'Y' AND R.LKUP_ROLES_ID IN (").append(nproles).append(") AND 'Y' IN (R.C, R.R, R.U, R.D) ");
		}

		sb.append(" 		WHERE ");
		sb.append(" 			A.PARENT_ID = ").append(typeid);
		sb.append(" 			AND ");
		sb.append(" 			A.ACTIVE = 'Y' ");
		sb.append(" 			AND ");
		sb.append(" 			A.SHOW = 'Y' ");
		sb.append(" 		ORDER BY ");
		sb.append(" 			ORDR ");
		return sb.toString();
	}

	public static String getFeeList(String table,int start, int end,String sortfield,String sortorder,String query, String searchField, String startDate,String expDate){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM (");
		sb.append(" SELECT ROW_NUMBER() OVER (ORDER BY ").append(sortfield).append(" ").append(sortorder).append(") AS RowNum  ");
		sb.append(" , ");
		sb.append(" RowConstrainedResult.* ");
		sb.append(" FROM ( SELECT ");
		sb.append(" DISTINCT F.ID,RF.ID AS REF_FEE_FORMULA_ID,R.FEE_ID,F.NAME,RF.INPUT1,RF.INPUT2,RF.INPUT3,RF.INPUT4,RF.INPUT5,RF.START_DATE,RF.EXPIRATION_DATE,RF.UPDATABLE,LF.NAME as FORMULA_NAME,MP.KEY_CODE, MP.ACCOUNT_NUMBER,MP.BUDGET_UNIT ");
		sb.append(" , CONVERT(VARCHAR(10),RF.START_DATE,101) as C_START_DATE,CONVERT(VARCHAR(10),RF.EXPIRATION_DATE,101) as C_EXPIRATION_DATE , ");
		sb.append(" CONVERT(VARCHAR(10),RF.CREATED_DATE,101) as C_CREATED_DATE ,CONVERT(VARCHAR(10),RF.UPDATED_DATE,101) as C_UPDATED_DATE , CU.USERNAME AS CREATED ,UP.USERNAME as UPDATED   ");
		
		sb.append(" from ").append(table).append(" F  ");
		
		sb.append(" JOIN REF_FEE_GROUP R  on R.FEE_ID=F.ID AND R.FEE_GROUP_ID >4 JOIN REF_FEE_FORMULA RF on R.FEE_ID=RF.FEE_ID ");
		
		sb.append("  LEFT OUTER JOIN LKUP_FORMULA LF on RF.LKUP_FORMULA_ID = LF.ID ");
		
		sb.append(" LEFT OUTER JOIN FINANCE_MAP MP on RF.FINANCE_MAP_ID = MP.ID    LEFT OUTER JOIN USERS CU on RF.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP on RF.UPDATED_BY = UP.ID ");
		sb.append(" WHERE  F.ACTIVE='Y' AND R.ACTIVE='Y' ");
		
		if(Operator.hasValue(searchField) && Operator.hasValue(query)){
			String s[] = Operator.split(searchField,",");
			int l = s.length-1;
			
			sb.append(" AND ( ");
			for(int i=0;i<s.length;i++){
				
				String cl ="";
				if(s[i].equalsIgnoreCase("KEY_CODE") || s[i].equalsIgnoreCase("BUDGET_UNIT") || s[i].equalsIgnoreCase("ACCOUNT_NUMBER") || s[i].equalsIgnoreCase("FUND") ){
					cl= "MP."+s[i];
				}else if(s[i].equalsIgnoreCase("FORMULA_NAME")){
					cl= "LF.NAME";
				}else {
					cl= "F."+s[i];
				}

				
				
				sb.append("  LOWER(").append(cl).append(") like '%").append(Operator.sqlEscape(query.toLowerCase())).append("%'  ");
				Logger.info(i+"#############"+l);
				if(i<l){
					sb.append(" OR ");
				}
			}
					
			sb.append(" ) ");
			
			if(Operator.hasValue(startDate) && Operator.hasValue(expDate)){
				sb.append(" AND RF.START_DATE >= "+Operator.checkString(startDate)+" AND RF.EXPIRATION_DATE <= "+Operator.checkString(expDate)+"  ");
			}else if (Operator.hasValue(startDate)){
				sb.append(" AND RF.START_DATE >= "+Operator.checkString(startDate)+" ");
			}else{
			    sb.append(" AND (RF.EXPIRATION_DATE >= GETDATE() OR  RF.EXPIRATION_DATE is null)  ");
			}
		}else if(Operator.hasValue(startDate) && Operator.hasValue(expDate)){
			sb.append(" AND RF.START_DATE >= "+Operator.checkString(startDate)+" AND RF.EXPIRATION_DATE <= "+Operator.checkString(expDate)+"  ");
		}else if (Operator.hasValue(startDate)){
			sb.append(" AND RF.START_DATE >= "+Operator.checkString(startDate)+" ");
		}else{
			sb.append(" AND (RF.EXPIRATION_DATE >= GETDATE() OR  RF.EXPIRATION_DATE is null)  ");
		}
		
		sb.append(" ) ");
		sb.append(" AS RowConstrainedResult) as RN WHERE RowNum >").append(start).append(" AND RowNum <=").append(end).append(" order by rownum ");		
		return sb.toString();
	}
	
	public static String getFeeGroupFields(int feeid){
		StringBuilder sb = new StringBuilder();
		
		sb.append("  SELECT * FROM ( SELECT ROW_NUMBER() OVER (ORDER BY A.GROUP_NAME ASC) AS RowNum  , A.* ,R.FEE_ID, CONVERT(VARCHAR(10),A.START_DATE,101) as C_START_DATE, CONVERT(VARCHAR(10),A.EXPIRATION_DATE,101) as C_EXPIRATION_DATE, CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE , CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ,   CU.USERNAME AS CREATED ,   UP.USERNAME as UPDATED ");
		sb.append(" from FEE_GROUP A  JOIN  REF_FEE_GROUP R  on R.FEE_GROUP_ID=A.ID  LEFT OUTER JOIN USERS CU on A.CREATED_BY = CU.ID LEFT OUTER JOIN USERS UP on A.UPDATED_BY = UP.ID  ");	
		sb.append("   WHERE A.ACTIVE='Y' AND R.FEE_ID="+feeid+" )  AS RowConstrainedResult WHERE RowNum >0 AND RowNum <=1000 order by rownum  ");
	
		
		return sb.toString();
	}
	public static String getFeeGroupDetails(String feeid){
		StringBuilder sb = new StringBuilder();
		
		sb.append("  SELECT * FROM ( SELECT ROW_NUMBER() OVER (ORDER BY FG.GROUP_NAME ASC) AS RowNum  , R.FEE_GROUP_ID,FG.GROUP_NAME ");
		sb.append(" from REF_FEE_GROUP R JOIN FEE_GROUP FG ON FG.ID=R.FEE_GROUP_ID AND FG.ACTIVE='Y' WHERE R.ACTIVE='Y'  ");	
		sb.append("   AND R.FEE_ID="+feeid+" )  AS RowConstrainedResult WHERE RowNum >0 AND RowNum <=1 order by rownum  ");
		System.out.println("sb : "+sb.toString());
		return sb.toString();
	}
	
	public static String getFeeListCount(String table,int start, int end,String sortfield,String sortorder,String query, String searchField, String startDate,String expDate){
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT  COUNT(DISTINCT F.ID) as COUNT  from ").append(table).append(" F  ");
		
		sb.append(" JOIN REF_FEE_GROUP R  on R.FEE_ID=F.ID AND R.FEE_GROUP_ID >4 JOIN REF_FEE_FORMULA RF on R.FEE_ID=RF.FEE_ID ");
		
		sb.append("  LEFT OUTER JOIN LKUP_FORMULA LF on RF.LKUP_FORMULA_ID = LF.ID ");
		
		sb.append(" LEFT OUTER JOIN FINANCE_MAP MP on RF.FINANCE_MAP_ID = MP.ID    LEFT OUTER JOIN USERS CU on RF.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP on RF.UPDATED_BY = UP.ID ");
		
		sb.append(" WHERE  F.ACTIVE='Y' AND  R.ACTIVE='Y' ");
		
		if(Operator.hasValue(searchField) && Operator.hasValue(query)){
			String s[] = Operator.split(searchField,",");
			int l = s.length-1;
			
			sb.append(" AND ( ");
			for(int i=0;i<s.length;i++){
				String cl ="";
				if(s[i].equalsIgnoreCase("KEY_CODE") || s[i].equalsIgnoreCase("BUDGET_UNIT") || s[i].equalsIgnoreCase("ACCOUNT_NUMBER") || s[i].equalsIgnoreCase("FUND") ){
					cl= "MP."+s[i];
				}else if(s[i].equalsIgnoreCase("FORMULA_NAME")){
					cl= "LF.NAME";
				}else {
					cl= "F."+s[i];
				}
				sb.append("  LOWER(").append(cl).append(") like '%").append(Operator.sqlEscape(query.toLowerCase())).append("%'  ");
				Logger.info(i+"#############"+l);
				if(i<l){
					sb.append(" OR ");
				}
			}
					
			sb.append(" ) ");
			
			if(Operator.hasValue(startDate) && Operator.hasValue(expDate)){
				sb.append(" AND RF.START_DATE >= "+Operator.checkString(startDate)+" AND RF.EXPIRATION_DATE <= "+Operator.checkString(expDate)+"  ");
			}else if (Operator.hasValue(startDate)){
				sb.append(" AND RF.START_DATE >= "+Operator.checkString(startDate)+" ");
			}else{
			    sb.append(" AND (RF.EXPIRATION_DATE >= GETDATE() OR  RF.EXPIRATION_DATE is null)  ");
			}
		}else if(Operator.hasValue(startDate) && Operator.hasValue(expDate)){
			sb.append(" AND RF.START_DATE >= "+Operator.checkString(startDate)+" AND RF.EXPIRATION_DATE <= "+Operator.checkString(expDate)+"  ");
		}else if (Operator.hasValue(startDate)){
			sb.append(" AND RF.START_DATE >= "+Operator.checkString(startDate)+" ");
		}else{
			sb.append(" AND (RF.EXPIRATION_DATE >= GETDATE() OR  RF.EXPIRATION_DATE is null)  ");
		}
		
		/*sb.append(" WHERE  (RF.EXPIRATION_DATE >= GETDATE() OR  RF.EXPIRATION_DATE is null)  AND R.ACTIVE='Y' ");
		
		if(Operator.hasValue(searchField) && Operator.hasValue(query)){
			String s[] = Operator.split(searchField,",");
			int l = s.length-1;
			sb.append(" AND ( ");
			for(int i=0;i<s.length;i++){
				sb.append("  LOWER(F.").append(s[i]).append(") like '%").append(Operator.sqlEscape(query.toLowerCase())).append("%'  ");
				Logger.info(i+"#############"+l);
				if(i<l){
					sb.append(" OR ");
				}
			}
			sb.append(" ) ");
		}*/
		
		return sb.toString();
	}
	public static String getFeeGroupDefaultDetails(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("  select ID AS FEE_GROUP_ID,GROUP_NAME from FEE_GROUP WHERE [DEFAULT]='Y' ");
		System.out.println("sb : "+sb.toString());
		return sb.toString();
	}
}