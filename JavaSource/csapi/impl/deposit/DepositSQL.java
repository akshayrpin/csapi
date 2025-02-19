package csapi.impl.deposit;

import alain.core.utils.Operator;
import csapi.utils.CsReflect;
import csshared.vo.RequestVO;
import csshared.vo.finance.DepositCreditVO;
import csshared.vo.finance.StatementVO;

public class DepositSQL {


	public static String summary(String type, int typeid, int id) {
		if (!Operator.hasValue(type) || typeid < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		if (type.equalsIgnoreCase("activity")) {
			sb.append("   SELECT ");
			sb.append("     D.ID ");
			sb.append("     , ");
			sb.append("     'activity' AS GROUPNAME ");
			sb.append("     , ");
			sb.append("     A.ACT_NBR AS NUMBER ");
			sb.append("     , ");
			sb.append("     D.PAYMENT_ID ");
			sb.append("     , ");
			sb.append("     D.AMOUNT ");
			sb.append("     , ");
			sb.append("     D.CURRENT_AMOUNT ");
			sb.append("     , ");
			sb.append("     D.COMMENT ");
			sb.append("     , ");
			sb.append("     P.PAYEE ");
			sb.append("     , ");
			sb.append("     P.PAYEE_ID ");
			sb.append("     , ");
			sb.append("     LTRIM(RTRIM( ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
			sb.append("         ELSE '' ");
			sb.append("       END + ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.EMAIL IS NOT NULL AND U.EMAIL <> '' THEN ' (' + U.EMAIL + ') ' ");
			sb.append("         ELSE '' ");
			sb.append("       END ");
			sb.append("     )) AS USER_PAYEE ");
			sb.append("   FROM ");
			sb.append("   REF_ACT_DEPOSIT AS AD ");
			sb.append("   JOIN ACTIVITY AS A ON AD.ACTIVITY_ID = A.ID AND AD.ACTIVE = 'Y' AND A.ACTIVE = 'Y' AND A.ID = ").append(typeid);
			sb.append("   JOIN DEPOSIT AS D ON AD.DEPOSIT_ID = D.ID AND AD.ACTIVE = 'Y' AND D.ACTIVE = 'Y' ");
			sb.append("   JOIN LKUP_DEPOSIT_TYPE AS T ON D.LKUP_DEPOSIT_TYPE_ID = T.ID ");
			sb.append("   JOIN PAYMENT AS P ON D.PAYMENT_ID = P.ID AND P.ACTIVE = 'Y' ");
			sb.append("   LEFT OUTER JOIN USERS AS U ON P.PAYEE_ID = U.ID ");
			
			
			/*sb.append("   UNION ");

			sb.append("   SELECT ");
			sb.append("     D.ID ");
			sb.append("     , ");
			sb.append("     'project' AS GROUPNAME ");
			sb.append("     , ");
			sb.append("     PJ.PROJECT_NBR AS NUMBER ");
			sb.append("     , ");
			sb.append("     D.PAYMENT_ID ");
			sb.append("     , ");
			sb.append("     D.AMOUNT ");
			sb.append("     , ");
			sb.append("     D.CURRENT_AMOUNT ");
			sb.append("     , ");
			sb.append("     D.COMMENT ");
			sb.append("     , ");
			sb.append("     P.PAYEE ");
			sb.append("     , ");
			sb.append("     P.PAYEE_ID ");
			sb.append("     , ");
			sb.append("     LTRIM(RTRIM( ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
			sb.append("         ELSE '' ");
			sb.append("       END + ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.EMAIL IS NOT NULL AND U.EMAIL <> '' THEN ' (' + U.EMAIL + ') ' ");
			sb.append("         ELSE '' ");
			sb.append("       END ");
			sb.append("     )) AS USER_PAYEE ");
			sb.append("   FROM ");
			sb.append("   REF_USERS_DEPOSIT AS PD ");
			sb.append("   JOIN REF_ACT_USERS AS PJ ON PD.USERS_ID = PJ.USERS_ID AND PD.ACTIVE = 'Y' AND PJ.ACTIVE = 'Y' AND PJ.ACTIVITY_ID = ").append(typeid);
			sb.append("   JOIN DEPOSIT AS D ON PD.DEPOSIT_ID = D.ID AND D.ACTIVE = 'Y' ");
			sb.append("   JOIN LKUP_DEPOSIT_TYPE AS T ON D.LKUP_DEPOSIT_TYPE_ID = T.ID ");
			sb.append("   JOIN PAYMENT AS P ON D.PAYMENT_ID = P.ID AND P.ACTIVE = 'Y' ");
			sb.append("   LEFT OUTER JOIN USERS AS U ON P.PAYEE_ID = U.ID ");*/
			
			
		}
		else if (type.equalsIgnoreCase("project")) {
			sb.append("   SELECT ");
			sb.append("     D.ID ");
			sb.append("     , ");
			sb.append("     'activity' AS GROUPNAME ");
			sb.append("     , ");
			sb.append("     A.ACT_NBR AS NUMBER ");
			sb.append("     , ");
			sb.append("     D.PAYMENT_ID ");
			sb.append("     , ");
			sb.append("     D.AMOUNT ");
			sb.append("     , ");
			sb.append("     D.CURRENT_AMOUNT ");
			sb.append("     , ");
			sb.append("     D.COMMENT ");
			sb.append("     , ");
			sb.append("     P.PAYEE ");
			sb.append("     , ");
			sb.append("     P.PAYEE_ID ");
			sb.append("     , ");
			sb.append("     LTRIM(RTRIM( ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
			sb.append("         ELSE '' ");
			sb.append("       END + ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.EMAIL IS NOT NULL AND U.EMAIL <> '' THEN ' (' + U.EMAIL + ') ' ");
			sb.append("         ELSE '' ");
			sb.append("       END ");
			sb.append("     )) AS USER_PAYEE ");
			sb.append("   FROM ");
			sb.append("   REF_ACT_DEPOSIT AS AD ");
			sb.append("   JOIN ACTIVITY AS A ON AD.ACTIVITY_ID = A.ID AND AD.ACTIVE = 'Y' AND A.ACTIVE = 'Y' AND A.PROJECT_ID = ").append(typeid);
			sb.append("   JOIN DEPOSIT AS D ON AD.DEPOSIT_ID = D.ID AND D.ACTIVE = 'Y' ");
			sb.append("   JOIN LKUP_DEPOSIT_TYPE AS T ON D.LKUP_DEPOSIT_TYPE_ID = T.ID ");
			sb.append("   JOIN PAYMENT AS P ON D.PAYMENT_ID = P.ID AND P.ACTIVE = 'Y' ");
			sb.append("   LEFT OUTER JOIN USERS AS U ON P.PAYEE_ID = U.ID ");

			sb.append("   UNION ");

			sb.append("   SELECT ");
			sb.append("     D.ID ");
			sb.append("     , ");
			sb.append("     'project' AS GROUPNAME ");
			sb.append("     , ");
			sb.append("     PJ.PROJECT_NBR AS NUMBER ");
			sb.append("     , ");
			sb.append("     D.PAYMENT_ID ");
			sb.append("     , ");
			sb.append("     D.AMOUNT ");
			sb.append("     , ");
			sb.append("     D.CURRENT_AMOUNT ");
			sb.append("     , ");
			sb.append("     D.COMMENT ");
			sb.append("     , ");
			sb.append("     P.PAYEE ");
			sb.append("     , ");
			sb.append("     P.PAYEE_ID ");
			sb.append("     , ");
			sb.append("     LTRIM(RTRIM( ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.FIRST_NAME IS NOT NULL AND U.FIRST_NAME <> '' THEN U.FIRST_NAME ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.MI IS NOT NULL AND U.MI <> '' THEN ' ' + U.MI ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.LAST_NAME IS NOT NULL AND U.LAST_NAME <> '' THEN ' ' + U.LAST_NAME ");
			sb.append("         ELSE '' ");
			sb.append("       END + ");
			sb.append("       CASE  ");
			sb.append("         WHEN U.EMAIL IS NOT NULL AND U.EMAIL <> '' THEN ' (' + U.EMAIL + ') ' ");
			sb.append("         ELSE '' ");
			sb.append("       END ");
			sb.append("     )) AS USER_PAYEE ");
			sb.append("   FROM ");
			sb.append("   REF_PROJECT_DEPOSIT AS PD ");
			sb.append("   JOIN PROJECT AS PJ ON PD.PROJECT_ID = PJ.ID AND PD.ACTIVE = 'Y' AND PJ.ACTIVE = 'Y' AND PJ.ID = ").append(typeid);
			sb.append("   JOIN DEPOSIT AS D ON PD.DEPOSIT_ID = D.ID AND D.ACTIVE = 'Y' ");
			sb.append("   JOIN LKUP_DEPOSIT_TYPE AS T ON D.LKUP_DEPOSIT_TYPE_ID = T.ID ");
			sb.append("   JOIN PAYMENT AS P ON D.PAYMENT_ID = P.ID AND P.ACTIVE = 'Y' ");
			sb.append("   LEFT OUTER JOIN USERS AS U ON P.PAYEE_ID = U.ID ");
			
			
		}
		sb.append(" ) ");
		sb.append(" SELECT ");
		sb.append("   ID ");
		sb.append("   , ");
		sb.append("   GROUPNAME ");
		sb.append("   , ");
		sb.append("   PAYMENT_ID ");
		sb.append("   , ");
		sb.append("   AMOUNT ");
		sb.append("   , ");
		sb.append("   CURRENT_AMOUNT ");
		sb.append("   , ");
		sb.append("   COMMENT ");
		sb.append("   , ");
		sb.append("   CASE WHEN PAYEE_ID > 0 THEN USER_PAYEE ELSE PAYEE END AS PAYEE ");
		sb.append(" FROM ");
		sb.append(" Q ");
		return sb.toString();
	}

	public static String summary1(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		if (type.equalsIgnoreCase("activity")) {
			sb.append(" WITH Q AS ( ");

			sb.append(" SELECT ");
			//sb.append("   DISTINCT U.ID AS USERS_ID ");
			sb.append("   DISTINCT RU.REF_USERS_ID  ");
			sb.append(" FROM ");
			sb.append(" ACTIVITY AS A ");
			sb.append(" JOIN REF_ACT_USERS AS RU ON A.ID = RU.ACTIVITY_ID AND RU.ACTIVE = 'Y' ");
			sb.append(" JOIN REF_USERS AS R ON RU.REF_USERS_ID = R.ID AND R.ACTIVE = 'Y' ");
			sb.append(" JOIN USERS AS U ON R.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
			sb.append(" WHERE ");
			sb.append(" A.ID = ").append(typeid);
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append("   DISTINCT U.ID AS USERS_ID ");
			sb.append(" FROM ");
			sb.append(" ACTIVITY AS A ");
			sb.append(" JOIN REF_PROJECT_USERS AS RU ON A.PROJECT_ID = RU.PROJECT_ID AND RU.ACTIVE = 'Y' ");
			sb.append(" JOIN REF_USERS AS R ON RU.REF_USERS_ID = R.ID AND R.ACTIVE = 'Y' ");
			sb.append(" JOIN USERS AS U ON R.USERS_ID = U.ID AND U.ACTIVE = 'Y' ");
			sb.append(" WHERE ");
			sb.append(" A.ID = ").append(typeid);
			sb.append(" ) "); 

			sb.append(" , ");
			sb.append(" M AS ( ");
			sb.append(" SELECT ");
			sb.append(" 'ACTIVITY' AS GROUPNAME ");
			sb.append(" , ");
			sb.append(" SUM(D.CURRENT_AMOUNT) as DEPOSIT ");
			sb.append(" FROM ");
			sb.append(" ACTIVITY AS A ");
			sb.append(" JOIN REF_ACT_DEPOSIT AS RD ON A.ID = RD.ACTIVITY_ID ");
			sb.append(" JOIN DEPOSIT AS D ON RD.DEPOSIT_ID = D.ID AND D.PARENT_ID = 0 ");
			sb.append(" WHERE ");
			sb.append(" A.ID = ").append(typeid);
			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" 'PROJECT' AS GROUPNAME ");
			sb.append(" , ");
			sb.append(" SUM(D.CURRENT_AMOUNT) as DEPOSIT ");
			sb.append(" FROM ");
			sb.append(" ACTIVITY AS A ");
			sb.append(" JOIN REF_PROJECT_DEPOSIT AS RD ON RD.PROJECT_ID = A.PROJECT_ID ");
			sb.append(" JOIN DEPOSIT AS D ON RD.DEPOSIT_ID = D.ID AND D.PARENT_ID = 0 ");
			sb.append(" WHERE ");
			sb.append(" A.ID = ").append(typeid);
			
			
			

			sb.append(" UNION ");
			sb.append(" SELECT ");
			sb.append(" 'USERS' AS GROUPNAME ");
			sb.append(" , ");
			sb.append(" SUM(D.CURRENT_AMOUNT) as DEPOSIT ");
			sb.append(" FROM ");
			sb.append(" Q ");
		//	sb.append(" JOIN REF_USERS_DEPOSIT AS RD ON RD.REF_USERS_ID = Q.USERS_ID ");
			sb.append(" JOIN REF_USERS_DEPOSIT AS RD ON RD.REF_USERS_ID = Q.REF_USERS_ID ");
			sb.append(" JOIN DEPOSIT AS D ON RD.DEPOSIT_ID = D.ID AND D.PARENT_ID = 0 ");
			sb.append(" ) ");
			sb.append(" SELECT ");
			sb.append(" GROUPNAME ");
			sb.append(" , ");
			sb.append(" CASE WHEN DEPOSIT IS NULL THEN 0 ELSE DEPOSIT END AS AMOUNT ");
			sb.append(" FROM M ");
//			sb.append(" SELECT ");
//			sb.append(" FROM ");
//			sb.append(" ACTIVITY AS A ");
//			sb.append(" JOIN REF_ACT_USER AS RU ON A.ID = RU.ACTIVITY_ID ");
//			sb.append(" JOIN USERS AS U ON RU.USERS_ID = U.ID ");
//			sb.append(" JOIN REF_USERS_DEPOSIT AS RD ON RD.USERS_ID = U.ID ");

		}
		
		if(type.equalsIgnoreCase("project")){
			sb.append(" SELECT ");
			sb.append(" 'PROJECT' AS GROUPNAME ");
			sb.append(" , ");
			sb.append(" SUM(D.CURRENT_AMOUNT) as AMOUNT ");
			sb.append(" FROM ");
			sb.append(" REF_PROJECT_DEPOSIT RD ");
			//sb.append(" JOIN REF_PROJECT_DEPOSIT AS RD ON RD.PROJECT_ID = A.PROJECT_ID ");
			sb.append(" JOIN DEPOSIT AS D ON RD.DEPOSIT_ID = D.ID AND D.PARENT_ID = 0 ");
			sb.append(" WHERE ");
			sb.append(" RD.PROJECT_ID = ").append(typeid);
		}


//		sb.append(" select    ");
//		sb.append("  '").append(type.toUpperCase()).append("' AS GROUPNAME ,  SUM(D.CURRENT_AMOUNT) as DEPOSIT  ");
//		sb.append(" from REF_").append(tableref).append("_DEPOSIT RD  ");
//		sb.append(" join DEPOSIT D on RD.DEPOSIT_ID=D.ID");
//	
//		sb.append(" where RD.").append(idref).append("= ").append(typeid).append(" and D.PARENT_ID=0 ");
		/*sb.append(" union ");
		sb.append(" select    ");
		sb.append(" 'TOTAL' as GROUPNAME,0 as GROUP_ID,  SUM(SD.FEE_AMOUNT) as AMOUNT , SUM(SD.FEE_PAID) as PAID,SUM(SD.BALANCE_DUE) as BALANCE ");
		sb.append(" from ").append(maintableref).append(" A  ");
		sb.append(" join REF_").append(tableref).append("_STATEMENT RAS on A.ID=RAS.").append(idref).append("   ");
		sb.append(" join STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y'   ");
		sb.append(" join STATEMENT_DETAIL SD on  S.ID=SD.STATEMENT_ID  ");
		sb.append(" join FEE_GROUP FG   on SD.GROUP_ID=FG.ID ");
		sb.append(" where A.ID= ").append(typeid).append("  ");*/
		return sb.toString();
	}
	
	
	public static String insertDeposit(DepositCreditVO p,int userId,String ip){
		StringBuilder sb = new StringBuilder();
		sb.append(" insert into DEPOSIT (" );
		/*sb.append("PAYMENT_NU ");
		sb.append(",");
		*/
		sb.append( " LKUP_DEPOSIT_TYPE_ID ");
		sb.append(",");
		sb.append("AMOUNT ");
		sb.append(",");
		sb.append("CURRENT_AMOUNT ");
		sb.append(",");
		sb.append( "PARENT_ID ");
		sb.append(",");
		sb.append("PAYMENT_ID ");
		sb.append(",");
		sb.append("COMMENT ");
		
		sb.append(",");
		sb.append("CREATED_BY ");
		sb.append(",");
		sb.append("UPDATED_BY ");
		sb.append(",");
		sb.append("CREATED_IP ");
		
		sb.append(") VALUES (");
		sb.append(p.getType());
		sb.append(",");
		sb.append(p.getAmount());
		sb.append(",");
		sb.append(p.getCurrentamount());
		sb.append(",");
		sb.append(p.getParentid());
		sb.append(",");
		sb.append(p.getPaymentid());
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(p.getComment())).append("'");
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(ip)).append("'");
		sb.append(" ) "); 
		
		return sb.toString();
	}
	
	public static String getDepositId(String ip,int userId, int paymentId){
		StringBuilder sb = new StringBuilder();
		sb.append( "select TOP 1 * from DEPOSIT WHERE ID>0 ");
		sb.append(" AND ");
		sb.append(" CREATED_IP= '").append(Operator.sqlEscape(ip)).append("'");
		sb.append(" AND ");
		sb.append(" CREATED_BY= ").append(userId);
		sb.append(" AND ");
		sb.append(" PAYMENT_ID= ").append(paymentId);
		sb.append(" ORDER BY ID DESC ");
		return sb.toString();
	}
	
	public static String insertRefDeposit(String type,int typeId,int depositId,int userId){
		String ref = CsReflect.getTableRef(type);
		String fieldId = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		
		if(Operator.equalsIgnoreCase(fieldId, "REF_USERS_ID")){
			fieldId = "USERS_ID";
		}
		
		sb.append( "insert into REF_").append(ref).append("_DEPOSIT (").append(fieldId).append(",DEPOSIT_ID,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)");
		sb.append(" VALUES (");
		sb.append(typeId);
		sb.append(",");
		sb.append(depositId);
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(",");
		sb.append(userId);
		sb.append(",");
		sb.append(" CURRENT_TIMESTAMP ");
		sb.append(" ) "); 
		return sb.toString();
	}
	
	public static String updateDeposit(double currentamount,int id){
		StringBuilder sb = new StringBuilder();
		sb.append( "UPDATE DEPOSIT SET ");
		sb.append(" CURRENT_AMOUNT= ").append(currentamount);
		sb.append(",");
		sb.append(" UPDATED_DATE= CURRENT_TIMESTAMP ");
		sb.append(" WHERE ");
		sb.append(" ID= ").append(id);
		
		return sb.toString();
	}
	
	public static String getDepositAmount(String activity,String project,StatementVO[] st){
		StringBuilder sb = new StringBuilder();
		
		
		sb.append(getDepositPeople(st));
		sb.append(" union ");
		
		sb.append(" select SUM(D.CURRENT_AMOUNT) as AMOUNT,RA.ACTIVITY_ID as ID, 'ACTIVITY' AS TYPE,D.LKUP_DEPOSIT_TYPE_ID,A.ACT_NBR as TYPENAME,RA.ACTIVITY_ID as CONNECTID  from REF_ACT_DEPOSIT RA ");
		sb.append(" join DEPOSIT D on RA.DEPOSIT_ID=D.ID ");
		sb.append(" join ACTIVITY A ON RA.ACTIVITY_ID=A.ID ");
		sb.append(" where RA.ACTIVITY_ID IN (").append(activity).append(") and PARENT_ID=0 and D.LKUP_DEPOSIT_TYPE_ID <>4 group by RA.ACTIVITY_ID,D.LKUP_DEPOSIT_TYPE_ID,A.ACT_NBR  ");
		sb.append(" union  ");
		sb.append(" select SUM(D.CURRENT_AMOUNT) as AMOUNT,RA.PROJECT_ID as ID ,'PROJECT' AS TYPE,D.LKUP_DEPOSIT_TYPE_ID,A.PROJECT_NBR as TYPENAME,RA.PROJECT_ID as CONNECTID  ");
		sb.append(" from REF_PROJECT_DEPOSIT RA ");
		sb.append(" join DEPOSIT D on RA.DEPOSIT_ID=D.ID ");
		sb.append(" join PROJECT A ON RA.PROJECT_ID=A.ID ");
		sb.append(" where RA.PROJECT_ID IN (").append(project).append(") and PARENT_ID=0 and D.LKUP_DEPOSIT_TYPE_ID <>4 group by RA.PROJECT_ID,D.LKUP_DEPOSIT_TYPE_ID,A.PROJECT_NBR  ");
		
	
		return sb.toString();
		
	}
	
	
	public static String getDepositAmount(int id,String level,int typeId){
		StringBuilder sb = new StringBuilder();
		if(level.equalsIgnoreCase("ACTIVITY")){
			sb.append(" select D.*  from REF_ACT_DEPOSIT RA ");
			sb.append(" join DEPOSIT D on RA.DEPOSIT_ID=D.ID ");
			sb.append(" where RA.ACTIVITY_ID IN (").append(id).append(") and D.LKUP_DEPOSIT_TYPE_ID <>4 and PARENT_ID=0  and CURRENT_AMOUNT>0 ");
		}else if(level.equalsIgnoreCase("PROJECT")){
			sb.append(" select D.*  ");
			sb.append(" from REF_PROJECT_DEPOSIT RA ");
			sb.append(" join DEPOSIT D on RA.DEPOSIT_ID=D.ID ");
			sb.append(" where RA.PROJECT_ID IN (").append(id).append(") and D.LKUP_DEPOSIT_TYPE_ID <>4 and PARENT_ID=0 and CURRENT_AMOUNT>0 ");
		}else if(level.equalsIgnoreCase("USER")){
			sb.append(" select D.*  ");
			sb.append(" from  REF_USERS_DEPOSIT RA  ");
			sb.append(" join DEPOSIT D on RA.DEPOSIT_ID=D.ID ");
			sb.append(" where RA.USERS_ID IN (").append(id).append(") and D.LKUP_DEPOSIT_TYPE_ID <>4 and PARENT_ID=0 and CURRENT_AMOUNT>0 ");
		}
		
		if(typeId>0){
			sb.append(" AND ");
			sb.append(" LKUP_DEPOSIT_TYPE_ID =").append(typeId);
		}
		sb.append(" order by D.ID DESC,LKUP_DEPOSIT_TYPE_ID ");
		return sb.toString();
	}
	
	
	public static String depositlist(RequestVO r){
		if (!Operator.hasValue(r.getType())) { return ""; }
		String tableref = CsReflect.getTableRef(r.getType());
		String idref = CsReflect.getFieldIdRef(r.getType());
		String maintableref = CsReflect.getMainTableRef(r.getType());
		
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select DISTINCT  D.*,");
		sb.append(" CONVERT(VARCHAR(10), D.CREATED_DATE, 101) + ' ' + RIGHT(CONVERT(CHAR(20), D.CREATED_DATE, 22), 11) as CREATEDDATE ");
		sb.append(" from REF_").append(tableref).append("_DEPOSIT R ");  
		sb.append(" join DEPOSIT D on R.DEPOSIT_ID=D.ID ");
		sb.append(" where D.PARENT_ID=0 AND R.").append(idref).append("=").append(r.getTypeid());
		sb.append(" order by D.CREATED_DATE DESC");
		
		return sb.toString();
	}
	
	public static String depositdetail(int paymentId){
		StringBuilder sb = new StringBuilder();
		sb.append(" select D.*");
		sb.append(" from DEPOSIT D  ");  
		//sb.append(" join DEPOSIT_CREDITS D on R.DEPOSIT_ID=D.ID ");
		sb.append(" where D.PARENT_ID=").append(paymentId).append(" ");// AND R.ACTIVITY_ID=").append(r.getTypeid());
		sb.append(" order by D.CREATED_DATE ASC ");
		return sb.toString();
	}
	
	
	//TODO PROJECT LEVEL AND ACT LEVEL CHECK AND TEST
	public static String getDepositPeople(StatementVO[] st) {
		int l = st.length;
		//Logger.highlight(l);
		StringBuilder sb = new StringBuilder();
		if (l > 0) {
			for (int i=0; i<l; i++) {
				if (i > 0) { sb.append(" , "); }
				sb.append(st[i].getActivityid());
			}
			String in = sb.toString();

			sb = new StringBuilder();
			sb.append(" WITH Q AS ( ");
			sb.append("   SELECT ");
			sb.append("     A.ID as CONNECTID, ");
			sb.append("     AU.ID AS ID, ");
			sb.append("     AU.ID AS VALUE, ");
			//sb.append("     '' AS USERGROUP, ");
			sb.append("     AU.ID AS USERS_ID, ");

			sb.append("     LTRIM(RTRIM( ");
			sb.append("       CASE  ");
			sb.append("         WHEN AU.FIRST_NAME IS NOT NULL AND AU.FIRST_NAME <> '' THEN AU.FIRST_NAME ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN AU.MI IS NOT NULL AND AU.MI <> '' THEN ' ' + AU.MI ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN AU.LAST_NAME IS NOT NULL AND AU.LAST_NAME <> '' THEN ' ' + AU.LAST_NAME ");
			sb.append("         ELSE '' ");
			sb.append("       END + ");
			sb.append("       CASE  ");
			sb.append("         WHEN AU.EMAIL IS NOT NULL AND AU.EMAIL <> '' THEN ' (' + AU.EMAIL + ') ' ");
			sb.append("         ELSE '' ");
			sb.append("       END ");
			sb.append("     )) AS TEXT ");

			sb.append("   FROM ");
			sb.append("   ACTIVITY AS A ");
			sb.append("    LEFT OUTER JOIN REF_ACT_USERS AS AR ON AR.ACTIVITY_ID = A.ID   ");
			sb.append("    			    LEFT OUTER JOIN REF_PROJECT_USERS AS ARP ON A.PROJECT_ID = ARP.PROJECT_ID   ");
			sb.append("   JOIN REF_USERS AS ARU ON (AR.REF_USERS_ID = ARU.ID  OR ARP.REF_USERS_ID = ARU.ID) ");
			sb.append("   LEFT OUTER JOIN USERS AS AU ON ARU.USERS_ID = AU.ID AND ARU.USERS_ID > 0 AND AU.ACTIVE = 'Y' ");
			sb.append("   LEFT OUTER JOIN USERS_GROUP AS AG ON ARU.USERS_GROUP_ID = AG.ID AND ARU.USERS_ID < 1 AND AG.ACTIVE = 'Y' ");
			sb.append("   WHERE ");
			sb.append("     A.ID IN ( ").append(in).append("     ) ");
			sb.append("   group by A.ID,ARU.ID,AG.GROUP_NAME,AU.ID,AU.FIRST_NAME,AU.MI,AU.LAST_NAME,AU.EMAIL ");
			
			sb.append("   UNION "); 
			
			sb.append("   SELECT ");
			sb.append("     A.ID as CONNECTID, ");
			sb.append("     AU.ID AS ID, ");
			sb.append("     AU.ID AS VALUE, ");
			//sb.append("     '' AS USERGROUP, ");
			sb.append("     AU.ID AS USERS_ID, ");

			sb.append("     LTRIM(RTRIM( ");
			sb.append("       CASE  ");
			sb.append("         WHEN AU.FIRST_NAME IS NOT NULL AND AU.FIRST_NAME <> '' THEN AU.FIRST_NAME ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN AU.MI IS NOT NULL AND AU.MI <> '' THEN ' ' + AU.MI ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN AU.LAST_NAME IS NOT NULL AND AU.LAST_NAME <> '' THEN ' ' + AU.LAST_NAME ");
			sb.append("         ELSE '' ");
			sb.append("       END + ");
			sb.append("       CASE  ");
			sb.append("         WHEN AU.EMAIL IS NOT NULL AND AU.EMAIL <> '' THEN ' (' + AU.EMAIL + ') ' ");
			sb.append("         ELSE '' ");
			sb.append("       END ");
			sb.append("     )) AS TEXT ");

			sb.append("   FROM ");
			sb.append("   ACTIVITY AS A ");
			sb.append("    JOIN REF_ACT_PAYMENT AS RAP ON  A.ID = RAP.ACTIVITY_ID         ");
			sb.append("    JOIN PAYMENT P on RAP.PAYMENT_ID = P.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS AU ON P.PAYEE_ID = AU.ID ");
			//sb.append("   LEFT OUTER JOIN USERS AS AU ON ARU.USERS_ID = AU.ID AND ARU.USERS_ID > 0 AND AU.ACTIVE = 'Y' ");
		//	sb.append("   LEFT OUTER JOIN USERS_GROUP AS AG ON ARU.USERS_GROUP_ID = AG.ID AND ARU.USERS_ID < 1 AND AG.ACTIVE = 'Y' ");
			sb.append("   WHERE ");
			sb.append("     A.ID IN ( ").append(in).append("     ) ");
			sb.append("   group by A.ID,AU.ID,AU.FIRST_NAME,AU.MI,AU.LAST_NAME,AU.EMAIL ");
			
			
			
			sb.append("   UNION ");

			sb.append("   SELECT ");
			sb.append("     PR.PROJECT_ID  as CONNECTID, ");
			sb.append("     PU.ID AS ID, ");
			sb.append("     PU.ID AS VALUE, ");
			//sb.append("     PG.GROUP_NAME AS USERGROUP, ");
			sb.append("     PU.ID AS USERS_ID, ");

			sb.append("     LTRIM(RTRIM( ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.FIRST_NAME IS NOT NULL AND PU.FIRST_NAME <> '' THEN PU.FIRST_NAME ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.MI IS NOT NULL AND PU.MI <> '' THEN ' ' + PU.MI ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.LAST_NAME IS NOT NULL AND PU.LAST_NAME <> '' THEN ' ' + PU.LAST_NAME ");
			sb.append("         ELSE '' ");
			sb.append("       END + ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.EMAIL IS NOT NULL AND PU.EMAIL <> '' THEN ' (' + PU.EMAIL + ') ' ");
			sb.append("         ELSE '' ");
			sb.append("       END ");
			sb.append("     )) AS TEXT ");
			sb.append("   FROM ");
			sb.append("   ACTIVITY AS A ");
			sb.append("   JOIN REF_PROJECT_USERS AS PR ON A.PROJECT_ID = PR.PROJECT_ID ");
			sb.append("   JOIN REF_USERS AS PRU ON PR.REF_USERS_ID = PRU.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS PU ON PRU.USERS_ID = PU.ID AND PRU.USERS_ID > 0 AND PU.ACTIVE = 'Y' ");
			sb.append("   LEFT OUTER JOIN USERS_GROUP AS PG ON PRU.USERS_GROUP_ID = PG.ID AND PRU.USERS_ID < 1 AND PG.ACTIVE = 'Y' ");
			sb.append("   WHERE ");
			sb.append("     A.ID IN ( ").append(in).append("     ) ");

			sb.append("  group by PR.PROJECT_ID,PRU.ID,PG.GROUP_NAME,PU.ID,PU.FIRST_NAME,PU.MI,PU.LAST_NAME,PU.EMAIL ");
			sb.append(" ) ");
			
			
			sb.append(" SELECT SUM(D.CURRENT_AMOUNT) as AMOUNT,Q.ID as ID, 'USER' AS TYPE,D.LKUP_DEPOSIT_TYPE_ID,  ");
			//sb.append(" CASE ");
		//	sb.append("   WHEN Q.USERS_ID IS NULL THEN Q.USERGROUP ");
		//	sb.append("   ELSE Q.TEXT END AS TYPENAME ");
			sb.append(" Q.TEXT as TYPENAME, ");
			sb.append(" Q.CONNECTID  ");
			sb.append(" FROM Q ");
			sb.append(" join REF_USERS_DEPOSIT R on Q.USERS_ID=R.USERS_ID ");
			sb.append(" join DEPOSIT D on R.DEPOSIT_ID = D.ID and D.PARENT_ID=0 and D.LKUP_DEPOSIT_TYPE_ID <>4  ");
			sb.append(" where Q.CONNECTID in (").append(in).append("     ) ");
			sb.append(" group by Q.ID,D.LKUP_DEPOSIT_TYPE_ID,Q.TEXT,Q.CONNECTID ");

		}
		return sb.toString();
	}
	
	
	public static String getDepositOptions(RequestVO v){
		StringBuilder sb = new StringBuilder();
		
		String in = v.getTypeid()+"";
		if(v.getType().equals("activity")){
		
		
			sb.append("   SELECT ");
			sb.append("     'USER' as LEVEL, ");
			sb.append("     A.ID as CONNECTID, ");
			sb.append("     AU.ID AS ID, ");
			sb.append("     AU.ID AS VALUE, ");
			sb.append("     AG.GROUP_NAME AS USERGROUP, ");
			sb.append("     A.ACT_NBR as LEVEL_REF, ");

			sb.append("     LTRIM(RTRIM( ");
			sb.append("       CASE  ");
			sb.append("         WHEN AU.FIRST_NAME IS NOT NULL AND AU.FIRST_NAME <> '' THEN AU.FIRST_NAME ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN AU.MI IS NOT NULL AND AU.MI <> '' THEN ' ' + AU.MI ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN AU.LAST_NAME IS NOT NULL AND AU.LAST_NAME <> '' THEN ' ' + AU.LAST_NAME ");
			sb.append("         ELSE '' ");
			sb.append("       END + ");
			sb.append("       CASE  ");
			sb.append("         WHEN AU.EMAIL IS NOT NULL AND AU.EMAIL <> '' THEN ' (' + AU.EMAIL + ') ' ");
			sb.append("         ELSE '' ");
			sb.append("       END ");
			sb.append("     )) AS TEXT ");

			sb.append("   FROM ");
			sb.append("   ACTIVITY AS A ");
			sb.append("   JOIN REF_ACT_USERS AS AR ON AR.ACTIVITY_ID = A.ID ");
			sb.append("   JOIN REF_USERS AS ARU ON AR.REF_USERS_ID = ARU.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS AU ON ARU.USERS_ID = AU.ID AND ARU.USERS_ID > 0 ");
			sb.append("   LEFT OUTER JOIN USERS_GROUP AS AG ON ARU.USERS_GROUP_ID = AG.ID AND ARU.USERS_ID < 1 ");
			sb.append("   WHERE ");
			sb.append("     A.ID IN ( ").append(in).append("     ) ");
		
			sb.append("   UNION ");

			sb.append("   SELECT ");
			sb.append("     'USER' as LEVEL, ");
			sb.append("     PR.PROJECT_ID  as CONNECTID, ");
			sb.append("     PU.ID AS ID, ");
			sb.append("     PU.ID AS VALUE, ");
			sb.append("     PG.GROUP_NAME AS USERGROUP, ");
			sb.append("     'PROJECT-'+ A.ACT_NBR as LEVEL_REF, ");

			sb.append("     LTRIM(RTRIM( ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.FIRST_NAME IS NOT NULL AND PU.FIRST_NAME <> '' THEN PU.FIRST_NAME ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.MI IS NOT NULL AND PU.MI <> '' THEN ' ' + PU.MI ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.LAST_NAME IS NOT NULL AND PU.LAST_NAME <> '' THEN ' ' + PU.LAST_NAME ");
			sb.append("         ELSE '' ");
			sb.append("       END + ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.EMAIL IS NOT NULL AND PU.EMAIL <> '' THEN ' (' + PU.EMAIL + ') ' ");
			sb.append("         ELSE '' ");
			sb.append("       END ");
			sb.append("     )) AS TEXT ");
			sb.append("   FROM ");
			sb.append("   ACTIVITY AS A ");
			sb.append("   JOIN REF_PROJECT_USERS AS PR ON A.PROJECT_ID = PR.PROJECT_ID ");
			sb.append("   JOIN REF_USERS AS PRU ON PR.REF_USERS_ID = PRU.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS PU ON PRU.USERS_ID = PU.ID AND PRU.USERS_ID > 0 ");
			sb.append("   LEFT OUTER JOIN USERS_GROUP AS PG ON PRU.USERS_GROUP_ID = PG.ID AND PRU.USERS_ID < 1  ");
			sb.append("   WHERE ");
			sb.append("     A.ID IN ( ").append(in).append("     ) ");

			sb.append("   UNION ");

			sb.append("   SELECT ");
			sb.append("     'USER' as LEVEL, ");
			sb.append("     A.ID as CONNECTID, ");
			sb.append("     PU.ID AS ID, ");
			sb.append("     PU.ID AS VALUE, ");
			sb.append("     '' AS USERGROUP, ");
			sb.append("     A.ACT_NBR as LEVEL_REF, ");

			sb.append("     LTRIM(RTRIM( ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.FIRST_NAME IS NOT NULL AND PU.FIRST_NAME <> '' THEN PU.FIRST_NAME ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.MI IS NOT NULL AND PU.MI <> '' THEN ' ' + PU.MI ");
			sb.append("         ELSE ''  ");
			sb.append("       END +  ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.LAST_NAME IS NOT NULL AND PU.LAST_NAME <> '' THEN ' ' + PU.LAST_NAME ");
			sb.append("         ELSE '' ");
			sb.append("       END + ");
			sb.append("       CASE  ");
			sb.append("         WHEN PU.EMAIL IS NOT NULL AND PU.EMAIL <> '' THEN ' (' + PU.EMAIL + ') ' ");
			sb.append("         ELSE '' ");
			sb.append("       END ");
			sb.append("     )) AS TEXT ");
			sb.append("   FROM ");
			sb.append("   ACTIVITY AS A ");
			sb.append("   JOIN REF_ACT_PAYMENT AS PR ON A.ID = PR.ACTIVITY_ID ");
			sb.append("   JOIN PAYMENT AS PRU ON PR.PAYMENT_ID = PRU.ID ");
			sb.append("   LEFT OUTER JOIN USERS AS PU ON PRU.PAYEE_ID = PU.ID  ");
			sb.append("   WHERE ");
			sb.append("     A.ID IN ( ").append(in).append("     ) ");
			
		}	
		
	
		return sb.toString();
		
	}
	
}
