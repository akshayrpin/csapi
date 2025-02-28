package csapi.impl.print;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.project.ProjectSQL;
import csapi.utils.CsReflect;
import csshared.vo.finance.StatementVO;

public class PrintSQL {


	public static String getTemplate(String type, int typeid, int id, String online, String subreq) {
		return getTemplate(type, typeid, id, online, subreq, null);
	}

	public static String getTemplate(String type, int typeid, int id, String online, String subreq, String pub) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String tablemain = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		
		/*if(id>0){
			sb.append(" select *  from TEMPLATE where ACTIVE ='Y' AND ID= ").append(id);
		}else {*/
		if (type.equalsIgnoreCase("activity")) {
			sb.append(" WITH Q AS ( ");
			sb.append(" SELECT ");
			sb.append(" 	T.*  ");
			sb.append(" 	,  ");
			sb.append(" 	'activity' AS TTYPE  ");
			sb.append(" 	,  ");
			sb.append(" 	A.ID AS TID  ");
			sb.append(" FROM ");
			sb.append(" 	ACTIVITY AS A ");
			sb.append(" 	JOIN REF_ACT_TEMPLATE AS RT ON A.LKUP_ACT_TYPE_ID = RT.LKUP_ACT_TYPE_ID AND RT.ACTIVE='Y' ");
			sb.append(" 	JOIN TEMPLATE T on RT.TEMPLATE_ID = T.ID ");
			sb.append(" WHERE ");
			sb.append(" 	T.ACTIVE ='Y' ");
			sb.append(" 	AND ");
			sb.append(" 	A.ID IN (").append(typeid).append(")");
			if(Operator.hasValue(online)) {
				sb.append(" 	AND (RT.ONLINE='Y' OR T.ONLINE = 'Y' ) ");
			}
			if(Operator.equalsIgnoreCase(pub, "Y")) {
				sb.append("     AND ispublic = '");
				sb.append(pub);
				sb.append("'");
			}
			//else {
				sb.append(" UNION ");
				sb.append(" SELECT ");
				sb.append(" 	T.*  ");
				sb.append(" 	,  ");
				sb.append(" 	'project' AS TTYPE  ");
				sb.append(" 	,  ");
				sb.append(" 	P.ID AS TID  ");
				sb.append(" FROM ");
				sb.append(" 	ACTIVITY AS A ");
				sb.append(" 	JOIN PROJECT AS P ON A.PROJECT_ID = P.ID ");
				sb.append(" 	JOIN REF_PROJECT_TEMPLATE AS RT ON P.LKUP_PROJECT_TYPE_ID = RT.LKUP_PROJECT_TYPE_ID AND RT.ACTIVE='Y' ");
				sb.append(" 	JOIN TEMPLATE T on RT.TEMPLATE_ID = T.ID ");
				sb.append(" WHERE ");
				sb.append(" 	T.ACTIVE ='Y' ");
				sb.append(" 	AND ");
				sb.append(" 	A.ID IN (").append(typeid).append(")");
				if(Operator.hasValue(online)) {
					sb.append(" 	AND (RT.ONLINE='Y' OR T.ONLINE = 'Y' ) ");
				}
				if(Operator.equalsIgnoreCase(pub, "Y")) {
					sb.append("     AND ispublic = '");
					sb.append(pub);
					sb.append("'");
				}
			//}
			sb.append(" ) ");
			sb.append(" SELECT * FROM Q ORDER BY TTYPE, NAME ");
		}
		else if(type.equalsIgnoreCase("templatetype")) {
			sb = new StringBuilder();
			sb.append(" SELECT ");
			sb.append(" T.* ");
			sb.append(" FROM ");
			sb.append(" LKUP_TEMPLATE_TYPE A  ");
			sb.append(" JOIN REF_LKUP_TEMPLATE_TYPE_TEMPLATE AS RT ON A.ID =  RT.LKUP_TEMPLATE_TYPE_ID ");
			sb.append(" JOIN TEMPLATE AS T ON RT.TEMPLATE_ID = T.ID ");
			sb.append(" WHERE ");
			sb.append(" T.ACTIVE ='Y' ");
			sb.append(" AND ");
			sb.append(" A.ID=  ").append(typeid);
			
			if(Operator.hasValue(subreq) && subreq.equalsIgnoreCase("batch"))
				sb.append("  AND BATCH = 'Y' ");

			if(Operator.equalsIgnoreCase(pub, "Y")) {
				sb.append("     AND ispublic = '");
				sb.append(pub);
				sb.append("'");
			}
		}
		else {
			sb.append(" SELECT ");
			sb.append(" 	T.*  ");
			sb.append(" FROM ");
			sb.append(tablemain).append(" AS A ");
			sb.append(" 	JOIN REF_").append(tableref).append("_TEMPLATE AS RT ON A.LKUP_").append(tableref).append("_TYPE_ID = RT.LKUP_").append(tableref).append("_TYPE_ID AND RT.ACTIVE='Y' ");
			sb.append(" 	JOIN TEMPLATE T on RT.TEMPLATE_ID = T.ID ");
			sb.append(" WHERE ");
			sb.append(" 	T.ACTIVE ='Y' AND A.ID IN (").append(typeid).append(")");
			if(Operator.hasValue(online)) {
				sb.append(" 	AND T.ONLINE = 'Y' ");
			}

			if(Operator.equalsIgnoreCase(pub, "Y")) {
				sb.append("     AND ispublic = '");
				sb.append(pub);
				sb.append("'");
			}
		}
		//}
		
		return sb.toString();
	}

	
	public static String getTemplate(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select  T.*  from  TEMPLATE T ");
		sb.append(" where T.ACTIVE ='Y' AND T.ID=  ").append(id);
	
		
		return sb.toString();
	}
	
	
	
	
	public static String print(String type, int typeid) {
		if(Operator.equalsIgnoreCase(type, "activity")){
			return ActivitySQL.print(type, typeid);
		}
		else {
			return ProjectSQL.print(type, typeid);
		}
	}
	
	
	
	public static String insertBatchLog(String processId, int percent,String title,String description,String response){
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO LOG_THREAD_PROGRESS (PROCESS_ID,PERCENT_COMPLETE,TITLE,DESCRIPTION,RESPONSE) ");
		sb.append(" VALUES ( ");
		sb.append("'").append(Operator.sqlEscape(processId)).append("'");
		sb.append(",");
		sb.append(percent);
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(title)).append("'");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(description)).append("'");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(response)).append("'");
		sb.append(")");
		return sb.toString();
		
	}
	
	
	public static String deleteBatchLog(String processId){
		StringBuilder sb = new StringBuilder();
		sb.append(" DELETE FROM  LOG_THREAD_PROGRESS ");
		sb.append(" WHERE PROCESS_ID= '").append(Operator.sqlEscape(processId)).append("'");
		return sb.toString();
	}
	
	
	public static String insertBatch(String processId, String filename, int userid, String templateid, String type){
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO BATCH_PRINT (PROCESS_ID, LKUP_TEMPLATE_TYPE_ID, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, TYPE) OUTPUT Inserted.ID  VALUES (");
		sb.append("'").append(Operator.sqlEscape(processId)).append("'");
		/*sb.append(",");
		sb.append("'").append(Operator.sqlEscape(filename)).append("'");*/
		sb.append(",");
		sb.append(templateid);
		sb.append(",");
		sb.append(userid);
		sb.append(",");
		sb.append("CURRENT_TIMESTAMP");
		sb.append(",");
		sb.append(userid);
		sb.append(",");
		sb.append("CURRENT_TIMESTAMP");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(type)).append("'");
		sb.append(")");
		return sb.toString();
	}
	
	
	public static String updateBatch(String id, String filename){
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE BATCH_PRINT SET FILENAME = ");
		sb.append("'").append(Operator.sqlEscape(filename)).append("'");
		sb.append(",");
		sb.append("UPDATED_DATE = CURRENT_TIMESTAMP ");
		sb.append("WHERE ID = ").append(id);
		return sb.toString();
	}

	public static String updateBatchProcess(int id, String process){
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE BATCH_PRINT SET PROCESS_ID = ");
		sb.append("'").append(Operator.sqlEscape(process)).append("'");
		sb.append(",");
		sb.append("UPDATED_DATE = getDate() ");
		sb.append("WHERE ID = ").append(id);
		return sb.toString();
	}


	public static String getBatchDetails(String batchtype,int batchId) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT TOP 1 ID, LKUP_TEMPLATE_TYPE_ID as VALUE, FILENAME as TEXT FROM BATCH_PRINT");
		sb.append(" WHERE TYPE = '").append(batchtype).append("'");
		if(batchId>0){
			sb.append(" AND ID = ").append(batchId);
		}
		
		sb.append(" order by UPDATED_DATE desc");
		return sb.toString();
	}
	
	
	public static String getBatchStatus(String group, String batchid){
		StringBuilder sb = new StringBuilder();
		/*sb.append("SELECT SUM(CASE WHEN PRINTED='Y' THEN 1 ELSE 0 END)* 100 / COUNT(*) ");
		sb.append(" PERCENTAGE FROM ACTIVITY WHERE BATCH_PRINT_ID = ");
		sb.append(batchid);*/
		if(Operator.equalsIgnoreCase(group, "renewal")){
			sb.append("	SELECT PERCENT_COMPLETE AS PERCENTAGE ");
			sb.append(" FROM BATCH_PRINT BP ");
			sb.append("	JOIN LOG_THREAD_PROGRESS L ON BP.PROCESS_ID = L.PROCESS_ID ");
			sb.append("	WHERE BP.ID =   ");
			sb.append(batchid);
		} else{
			sb.append("	WITH Q AS( ");
			sb.append(" SELECT SUM(CASE WHEN PRINTED='Y' THEN 1 ELSE 0 END)* 100 / COUNT(*)  PERCENTAGE, COUNT(*) TOTAL, FILENAME  FROM ACTIVITY ");
			sb.append("	JOIN BATCH_PRINT B ON B.ID = BATCH_PRINT_ID WHERE BATCH_PRINT_ID = ");
			sb.append(batchid);
			sb.append("	GROUP BY FILENAME )  ");
			sb.append(" SELECT CASE WHEN PERCENTAGE = 100 AND FILENAME IS NOT NULL THEN 100 ELSE CASE WHEN PERCENTAGE = 100 THEN 99 ELSE PERCENTAGE END END PERCENTAGE FROM Q ");
		}
		return sb.toString();
	}

	public static String getBatch(String batchid){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM BATCH_PRINT WHERE ID = ").append(batchid);
		return sb.toString();
	}
	
	public static String getAccountsForRenewalLetter(String type){
		StringBuilder sb = new StringBuilder();
		Timekeeper cur = new Timekeeper();
		Timekeeper next = new Timekeeper();
		
		if(cur.MONTH()<10){
			cur.addYear(-1);
		}else {
			next.addYear(1);
		}
		
		next.addYear(1);
		
		sb.append("WITH ");
		sb.append(" OA AS ( ");
		sb.append("                SELECT DISTINCT ");
		sb.append("                                P.ID, ");
		sb.append("                                PP.ID as PARKING_ACCOUNT_NUMBER, ");
		sb.append("                                COUNT(A.ID) AS COUNT ");
		sb.append("                FROM ");
		sb.append("                                ACTIVITY AS A ");
		sb.append("                                JOIN LKUP_ACT_TYPE AS ATY ON A.LKUP_ACT_TYPE_ID = ATY.ID AND A.ACTIVE = 'Y' ");
		sb.append(" 							   AND A.START_DATE >= CAST('").append(cur.yyyy()).append("-10-01' AS DATE) ");
		sb.append(" 							   AND A.EXP_DATE = CAST('").append(next.yyyy()).append("-01-31' AS DATE) AND ATY.ID IN (251,278,279) ");
		sb.append("                                JOIN LKUP_ACT_STATUS AS AST ON A.LKUP_ACT_STATUS_ID = AST.ID AND AST.ID IN (6,532) ");
		sb.append("                                JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append("                                JOIN LKUP_PROJECT_STATUS AS PS ON P.LKUP_PROJECT_STATUS_ID = PS.ID AND PS.EXPIRED = 'N' ");
		sb.append("                                JOIN REF_PROJECT_PARKING AS PP ON P.ID = PP.PROJECT_ID AND PP.ACTIVE = 'Y' ");
		sb.append("                GROUP BY ");
		sb.append("                                P.ID,PP.ID  ");
		sb.append(") ");
		sb.append(", PA AS ( ");
		sb.append("                SELECT DISTINCT  ");
		sb.append("                                P.ID, ");
		sb.append("                                PP.ID as PARKING_ACCOUNT_NUMBER, ");
		sb.append("                                COUNT(A.ID) AS COUNT ");
		sb.append("                FROM ");
		sb.append("                                ACTIVITY AS A ");
		sb.append("                                JOIN LKUP_ACT_TYPE AS ATY ON A.LKUP_ACT_TYPE_ID = ATY.ID AND A.ACTIVE = 'Y' ");
		sb.append(" 							   AND A.START_DATE >= CAST('").append(cur.yyyy()).append("-10-01' AS DATE) ");
		sb.append("                                AND A.EXP_DATE = CAST('").append(next.yyyy()).append("-01-31' AS DATE) AND ATY.ID IN (252,280) ");
		sb.append("                                JOIN LKUP_ACT_STATUS AS AST ON A.LKUP_ACT_STATUS_ID = AST.ID AND AST.ID IN (6,532) ");
		sb.append("                                JOIN PROJECT AS P ON A.PROJECT_ID = P.ID AND P.ACTIVE = 'Y' ");
		sb.append("                                JOIN LKUP_PROJECT_STATUS AS PS ON P.LKUP_PROJECT_STATUS_ID = PS.ID AND PS.EXPIRED = 'N' ");
		sb.append("                                JOIN REF_PROJECT_PARKING AS PP ON P.ID = PP.PROJECT_ID AND PP.ACTIVE = 'Y' ");
		sb.append("                GROUP BY ");
		sb.append("                                P.ID,PP.ID  ");
		sb.append(") ");
		sb.append(", O AS ( ");
		sb.append("                SELECT ");
		sb.append("                OA.ID,OA.PARKING_ACCOUNT_NUMBER, OA.COUNT ");
		sb.append("                FROM ");
		sb.append("                OA ");
		sb.append("                LEFT OUTER JOIN PA ON OA.ID = PA.ID ");
		sb.append("                WHERE ");
		sb.append("                PA.ID IS NULL ");
		sb.append(") ");
		sb.append(" ");
		sb.append(" ");
		sb.append(" ");
		sb.append(", P AS ( ");
		sb.append("                SELECT ");
		sb.append("                PA.ID,PA.PARKING_ACCOUNT_NUMBER, PA.COUNT ");
		sb.append("                FROM ");
		sb.append("                PA ");
		sb.append("                LEFT OUTER JOIN OA ON OA.ID = PA.ID ");
		sb.append("                WHERE ");
		sb.append("                OA.ID IS NULL ");
		sb.append(") ");
		sb.append(", B AS ( ");
		sb.append("                SELECT ");
		sb.append("                OA.ID, OA.PARKING_ACCOUNT_NUMBER,OA.COUNT AS OVERNIGHT, PA.COUNT AS PREFERENTIAL ");
		sb.append("                FROM ");
		sb.append("                OA ");
		sb.append("                JOIN PA ON OA.ID = PA.ID ");
		sb.append(") ");
		sb.append(" ");
		sb.append(" ");
		sb.append("SELECT ");
		//sb.append(" TOP 5 ");
	
			sb.append("                ").append(type).append(".ID, ");
			sb.append("                ").append(type).append(".PARKING_ACCOUNT_NUMBER, ");
			
		
		if(type.equals("B")){
			sb.append("               ").append("B").append(".OVERNIGHT as ").append("O").append("COUNT, ");
			sb.append("               ").append("B").append(".PREFERENTIAL as ").append("P").append("COUNT, ");
		}
		else {
			sb.append("               ").append(type).append(".COUNT as ").append(type).append("COUNT, ");
		}
		sb.append("                UPPER(U.FIRST_NAME) AS FIRST_NAME, ");
		sb.append("                UPPER(U.LAST_NAME) AS LAST_NAME, ");
		sb.append("                U.USERNAME , ");
		sb.append("                U.EMAIL, ");
		sb.append("                L.STR_NO, ");
		sb.append("                L.STR_MOD, ");
		sb.append("                S.PRE_DIR, ");
		sb.append("                S.STR_NAME, ");
		sb.append("                S.STR_TYPE, ");
		sb.append("                L.UNIT, ");
		sb.append("                LTRIM(RTRIM( ");
		sb.append("                                CAST(L.STR_NO AS VARCHAR(10)) + ");
		sb.append("                                CASE  ");
		sb.append("                                                WHEN L.STR_MOD IS NOT NULL AND L.STR_MOD <> '' THEN ' ' + L.STR_MOD ");
		sb.append("                                                ELSE ''  ");
		sb.append("                                END +  ");
		sb.append("                                CASE ");
		sb.append("                                                WHEN S.PRE_DIR IS NOT NULL AND S.PRE_DIR <> '' THEN ' ' + S.PRE_DIR ");
		sb.append("                                                ELSE ''  ");
		sb.append("                                END +  ");
		sb.append("                                ' ' + S.STR_NAME + ");
		sb.append("                                CASE  ");
		sb.append("                                                WHEN S.STR_TYPE IS NOT NULL AND S.STR_TYPE <> '' THEN ' ' + S.STR_TYPE ");
		sb.append("                                                ELSE '' ");
		sb.append("                                END + ");
		sb.append("                                CASE  ");
		sb.append("                                                WHEN L.UNIT IS NOT NULL AND L.UNIT <> '' THEN ' #' + L.UNIT ");
		sb.append("                                                ELSE '' ");
		sb.append("                                END ");
		sb.append("                )) AS ADDRESS, ");
		sb.append("                L.CITY, ");
		sb.append("                L.STATE, ");
		sb.append("                L.ZIP ");
		sb.append("FROM ");
		sb.append("                 ").append(type).append(" ");
		sb.append("                JOIN REF_PROJECT_USERS AS PU ON  ").append(type).append(".ID = PU.PROJECT_ID AND PU.ACTIVE = 'Y' ");
		sb.append("                JOIN REF_USERS AS RU ON PU.REF_USERS_ID = RU.ID ");
		sb.append("                JOIN LKUP_USERS_TYPE AS TU ON RU.LKUP_USERS_TYPE_ID = TU.ID AND TU.ID = 11 ");
		sb.append("                JOIN USERS AS U ON RU.USERS_ID = U.ID ");
		sb.append("                JOIN REF_LSO_PROJECT AS LP ON LP.PROJECT_ID =  ").append(type).append(".ID AND LP.ACTIVE = 'Y' ");
		sb.append("                JOIN LSO AS L ON LP.LSO_ID = L.ID ");
		sb.append("                JOIN LSO_STREET AS S ON L.LSO_STREET_ID = S.ID ");
		sb.append("             ORDER BY    ").append(type).append(".PARKING_ACCOUNT_NUMBER ");
		sb.append(" ");
		sb.append(" ");
		sb.append(" ");
		
		return sb.toString();
	}

}















