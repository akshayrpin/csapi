package csapi.impl.print;

import org.json.JSONArray;
import org.json.JSONObject;

import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.finance.FinanceSQL;
import csapi.utils.CsApiConfig;
import csapi.utils.CsReflect;

public class MassTemplateSQL {



	public static String getAllInOneQueryActivity(String type, String ids,JSONObject doList,String additionalWith,String additionalSelect,String additionalJoins) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		
		
		try{
		
		sb.append("WITH Q as( ");
		sb.append("  ");
		//sb.append(" select TOP 10 ID as MAIN_ID from activity WHERE LKUP_ACT_TYPE_ID = 2 order by UPDATED_DATE DESC ");
		//sb.append(" select TOP 10 ID as MAIN_ID from activity WHERE ID = 2092942 ");
		sb.append(" select ID as MAIN_ID from activity WHERE ID in (").append(ids).append(") ");
		sb.append(" ) ");
		
		
		
		//divisions
		
		/*sb.append(", DIVISIONS AS ( ");
		sb.append(" select  A.ID as DIVISIONS_ACT_ID,");
		sb.append("  LDT.TYPE as division_type,");
		sb.append("  LDT.DESCRIPTION as division_type_description,");
		sb.append("  LD.DIVISION as division,");
		sb.append("  LDT.DESCRIPTION as division_description");
		sb.append(" from ").append(maintableref).append("  A    ");
		sb.append("  join Q on A.ID=Q.MAIN_ID ");
		sb.append(" 	join REF_LSO_PROJECT R on A.PROJECT_ID=R.PROJECT_ID ");
		sb.append(" 	left outer join REF_LSO_DIVISIONS RLD on R.LSO_ID= RLD.LSO_ID  ");
		sb.append(" 	left outer join LKUP_DIVISIONS LD on RLD.LKUP_DIVISIONS_ID= LD.ID and LD.ACTIVE='Y' ");
		sb.append(" 	left outer join LKUP_DIVISIONS_TYPE LDT on LD.LKUP_DIVISIONS_TYPE_ID=LDT.ID ");
		sb.append(" ) ");*/
		
		//if(doList.has("list_finance") || doList.has("list_finance_total")) {
			/*sb.append(", RY AS ( ");
			sb.append(" select   A.ID as FID,  FG.GROUP_NAME  , FG.ID AS GROUP_ID,  CASE WHEN SUM(SD.FEE_AMOUNT) IS NULL THEN 0 ELSE SUM(SD.FEE_AMOUNT) END as REVIEW_FEE ,");
			sb.append(" 0.00 as ACTIVITY_FEE, CASE WHEN SUM(SD.FEE_PAID) IS NULL THEN 0 ELSE  SUM(SD.FEE_PAID) END as PAID, ");
			sb.append(" CASE WHEN SUM(SD.BALANCE_DUE) IS NULL THEN 0 ELSE   SUM(SD.BALANCE_DUE) END as BALANCE ");  
			sb.append(" from ").append(maintableref).append("  A    ");
			sb.append("  join Q on A.ID=Q.MAIN_ID ");
			sb.append(" join REF_").append(tableref).append("_STATEMENT RAS on A.ID=RAS.").append(idref).append(" ");   
			sb.append(" join STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");   
			sb.append(" join STATEMENT_DETAIL SD on  S.ID=SD.STATEMENT_ID  and SD.ACTIVE ='Y' "); 
			sb.append(" join FEE_GROUP FG   on SD.GROUP_ID=FG.ID  ");
			sb.append(" left outer join REF_FEE_GROUP RFG on FG.ID =RFG.FEE_GROUP_ID AND SD.FEE_ID= RFG.FEE_ID AND REVIEW_FEE='Y' ");
			sb.append(" where REVIEW_FEE IS NOT NULL ");
			sb.append(" group by FG.ID,FG.GROUP_NAME ,A.ID ");
			sb.append(" ), RN AS ( ");
			sb.append(" ");
			sb.append(" select   A.ID as FID,  FG.GROUP_NAME  , FG.ID AS GROUP_ID, 0.00 as REVIEW_FEE,CASE WHEN SUM(SD.FEE_AMOUNT) IS NULL THEN 0 ELSE SUM(SD.FEE_AMOUNT) END as ACTIVITY_FEE , CASE WHEN SUM(SD.FEE_PAID) IS NULL THEN 0 ELSE  SUM(SD.FEE_PAID) END  as PAID, ");
			sb.append(" CASE WHEN SUM(SD.BALANCE_DUE) IS NULL THEN 0 ELSE   SUM(SD.BALANCE_DUE) END as BALANCE   ");
			sb.append(" from ").append(maintableref).append("  A    ");
			sb.append("  join Q on A.ID=Q.MAIN_ID ");
			sb.append(" join REF_").append(tableref).append("_STATEMENT RAS on A.ID=RAS.").append(idref).append(" ");   
			sb.append(" join STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");   
			sb.append(" join STATEMENT_DETAIL SD on  S.ID=SD.STATEMENT_ID  and SD.ACTIVE ='Y' "); 
			sb.append(" join FEE_GROUP FG   on SD.GROUP_ID=FG.ID "); 
			sb.append(" left outer join REF_FEE_GROUP RFG on FG.ID =RFG.FEE_GROUP_ID AND SD.FEE_ID= RFG.FEE_ID AND REVIEW_FEE='N' ");
			sb.append(" where  REVIEW_FEE IS NOT NULL ");
			sb.append(" group by FG.ID,FG.GROUP_NAME  ,A.ID ");
			sb.append(" ) ");
			
			sb.append(", RYN AS ( select RN.GROUP_ID as group_id , RN.GROUP_NAME as group_name , ");
			
			sb.append(" FORMAT(RN.ACTIVITY_FEE, 'C', 'en-us') as activity_fee , ");
			sb.append(" FORMAT(RN.REVIEW_FEE, 'C', 'en-us') as review_fee , ");
			sb.append(" FORMAT(RN.PAID, 'C', 'en-us')  as  paid,    ");
			sb.append(" FORMAT(RN.BALANCE, 'C', 'en-us')  as  balance  ");
			sb.append(" from RN  ");
			sb.append(" UNION  ");
			sb.append(" select RY.GROUP_ID as group_id , RY.GROUP_NAME as group_name , ");
			
			sb.append(" FORMAT(RY.ACTIVITY_FEE, 'C', 'en-us') as activity_fee , ");
			sb.append(" FORMAT(RY.REVIEW_FEE, 'C', 'en-us') as review_fee , ");
			sb.append(" FORMAT(RY.PAID, 'C', 'en-us')  as  paid,    ");
			sb.append(" FORMAT(RY.BALANCE, 'C', 'en-us')  as  balance  ");
			sb.append(" from RY  ");
			sb.append(" ) ");*/
			sb.append(" , ");
			sb.append(FinanceSQL.calcSubqueries(type, "select MAIN_ID FROM Q"));
			
		//}
		
		sb.append(" select (	 ");
		sb.append("  ");
		
		sb.append(" SELECT  DISTINCT ");
		sb.append(" A.ID as activity_id , ");
		sb.append(" A.ID as type_id , ");
		sb.append(" 'activity' as type , ");
		sb.append(" ACT_NBR as activity_act_nbr , ");
		sb.append(" '").append(CsApiConfig.getQrcodeDomain()).append("/cs/?entity=lso&type=activity&typeid='+CAST(A.ID AS varchar) as qr_code, ");
	//	sb.append(" '<a href=\"").append(Config.rooturl()).append("/cs/?entity=lso&type=activity&reference='+ACT_NBR+'\"><img src=\"\">'+ACT_NBR+'</img></a>' as activity_act_nbr, ");
		sb.append(" LAT.TYPE as activity_type , ");
		sb.append(" LAS.DESCRIPTION as activity_status , ");
		sb.append(" LAS.WATERMARK_PATH as watermark_path , ");
		sb.append(" CASE WHEN LAS.WATERMARK_STATUS = 'Y' THEN LAS.DESCRIPTION ELSE '' END  as watermark_text , ");
		
		sb.append(" A.DESCRIPTION as activity_description, ");
		//sb.append(" '$'+convert(varchar(50), CAST(A.VALUATION_CALCULATED as money), -1) as activity_valuation_calculated , ");
		//sb.append(" '$'+convert(varchar(50), CAST(A.VALUATION_DECLARED as money), -1) as activity_valuation_declared , ");
		
		sb.append(" FORMAT(A.VALUATION_CALCULATED, 'C', 'en-us') as activity_valuation_calculated , ");
		sb.append(" FORMAT(A.VALUATION_DECLARED, 'C', 'en-us') as activity_valuation_declared , ");
				
		sb.append(" CONVERT(VARCHAR(10),A.START_DATE,101) as activity_start_date , ");
		sb.append(" CONVERT(VARCHAR(10),A.APPLIED_DATE,101) as activity_applied_date, ");
		sb.append(" CONVERT(VARCHAR(10),A.ISSUED_DATE,101) as activity_issued_date , ");
		sb.append(" CONVERT(VARCHAR(10),A.EXP_DATE,101) as activity_exp_date , ");
//ALAIN
		sb.append(" CONVERT(VARCHAR(10),A.APPLICATION_EXP_DATE,101) as activity_application_exp_date , ");

		sb.append(" CONVERT(VARCHAR(10),A.FINAL_DATE,101) as activity_final_date , ");
		sb.append(" CU.USERNAME as activity_created_by , ");
		sb.append(" UP.USERNAME as activity_updated_by ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as activity_created_date ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as activity_updated_date ");
		sb.append(",");
		sb.append(" A.CREATED_DATE as activity_created_date_time ");
		sb.append(",");
		sb.append(" A.UPDATED_DATE as activity_updated_date_time ");
		sb.append(",");
		sb.append(" D.DEPT as activity_department ");
		
		//project
		 sb.append(" ,");
		 sb.append("  P.ID as project_id");
		 sb.append(" , ");
		 sb.append(" PROJECT_NBR as project_nbr");
		 sb.append(" , ");
		 sb.append(" P.NAME as project_name");
		 sb.append(" , ");
		 sb.append(" P.DESCRIPTION as project_description");
		 sb.append(" , ");
		 sb.append(" P.CIP_ACCTNO as project_cip_acctno");
		 sb.append(" , ");
		 sb.append(" P.ACCOUNT_NUMBER as project_account_number");
		 sb.append(" , ");
		 sb.append(" LPT.TYPE as project_type");
		 sb.append(" , ");
		 sb.append(" LPS.STATUS as project_status");
		 sb.append(" , ");
		 sb.append(" P.START_DT as project_start_dt");
		 sb.append(" , ");
		 sb.append(" P.COMPLETION_DT as project_completion_dt");
		 sb.append(" , ");
		 sb.append(" P.APPLIED_DT as project_applied_dt");
		 sb.append(" , ");
		 sb.append(" P.EXPIRED_DT as project_expired_dt");
		 sb.append(" , ");
		 sb.append(" PCU.USERNAME as project_created_by");
		 sb.append(" , ");
		 sb.append(" PUP.USERNAME as project_updated_by");
		 sb.append(" , ");
		 sb.append(" P.CREATED_DATE as project_created_date");
		 sb.append(" , ");
		 sb.append(" P.UPDATED_DATE as project_updated_date");


		 
		//divisions
		/* sb.append(" ,");
		 sb.append(" DIVISONS.* ");*/
		/* sb.append("  LDT.DESCRIPTION as division_type_description,");
		 sb.append("  LD.DIVISION as division,");
		 sb.append("  LDT.DESCRIPTION as division_description");*/
		
		 
		//address
		/*sb.append(" ,");
		sb.append(" 	CONVERT(varchar(100), L.STR_NO)+' '+ ");
		sb.append(" 	LS.STR_NAME+' '+ LS.STR_TYPE+' '+ CASE WHEN PRE_DIR is null THEN '' ELSE PRE_DIR END+''+CASE WHEN L.UNIT is null then '' ELSE L.UNIT END as lso_address ");
		
		//apn
		sb.append(" ,");
		sb.append(" APN as lso_apn ");*/
		
		
		if(Operator.hasValue(additionalWith) && Operator.hasValue(additionalSelect) && Operator.hasValue(additionalJoins)){
			
		}
		
		if(Operator.hasValue(additionalSelect)){
			sb.append(additionalSelect);
		}
		
		//listpeople
		
		if(doList.has("lists")){
			for(int i=0;i<doList.getJSONArray("lists").length();i++){
				JSONObject o = doList.getJSONArray("lists").getJSONObject(i);
				String listtype = o.getString("type");
				
				
				if(listtype.startsWith("list_people")){
					JSONArray filters = PrintAgent.getFilters("list_people",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append("         SELECT DISTINCT ");
					//sb.append("         A.ID as ACT_ID, ");
					sb.append("         U.ID as user_id, ");
					sb.append("         P.ID as people_id, ");
					sb.append("         FIRST_NAME + ' ' + LAST_NAME   as people_name , ");
					sb.append("         U.USERNAME   as people_username , ");
					sb.append("         U.EMAIL as people_email, ");
					
					sb.append("         P.CITY as people_city, ");
					sb.append("         P.STATE as people_state, ");
					sb.append("         P.ZIP as people_zip, ");
					sb.append("         P.ZIP4 as people_zip4, ");
					
					sb.append("         P.PHONE_HOME as people_phone_home, ");
					sb.append("         P.FAX as people_fax, ");
					sb.append("         P.COMMENTS as people_comments, ");
					
					sb.append("         P.PHONE_WORK as people_phone_work, ");
					sb.append("         P.PHONE_CELL as people_phone_cell, ");
					//sb.append(" RN.ACTIVITY_FEE+ CASE WHEN RY.ACTIVITY_FEE IS NULL THEN 0 ELSE RY.ACTIVITY_FEE END as finance_activity_fee , ");
					sb.append("        RAU.PRIMARY_CONTACT as people_primary_contact, ");
					
					sb.append("         FIRST_NAME as people_first_name , ");
					sb.append("         MI as people_mi, ");
					sb.append("         LAST_NAME people_last_name, ");
					sb.append("         LUT.TYPE as people_type , ");
					sb.append("         ADDRESS as people_address ");
				//	sb.append("         , ");
				//	sb.append("         PL.LIC_NUM as people_lic_num, ");
				//	sb.append("         CONVERT(VARCHAR(10), PL.LIC_EXPIRATION_DATE, 101) as people_lic_expiration_date, ");
				//	sb.append("         LPLT.TYPE as people_license_type ");
					sb.append("                         FROM ACTIVITY A ");
					
					sb.append("                         LEFT OUTER join REF_ACT_USERS RAU on A.ID=RAU.ACTIVITY_ID  AND RAU.ACTIVE='Y' ");
					sb.append("                         LEFT OUTER JOIN   REF_PROJECT_USERS RAUP on A.PROJECT_ID=RAUP.PROJECT_ID  AND RAUP.ACTIVE='Y'    ");
					sb.append("                          join REF_USERS RU on (RAU.REF_USERS_ID=RU.ID  OR      RAUP.REF_USERS_ID=RU.ID   )  ");
					
					
					sb.append("                         left outer join LKUP_USERS_TYPE as LUT on RU.LKUP_USERS_TYPE_ID=LUT.ID  ");
					sb.append("                         JOIN USERS AS U ON RU.USERS_ID = U.ID ");
					sb.append("                         left outer join PEOPLE P on U.ID = P.USERS_ID AND P.ACTIVE='Y' ");
					//sb.append("                         left outer join PEOPLE_LICENSE as PL on P.ID=PL.PEOPLE_ID ");
					//sb.append("                         left outer join LKUP_PEOPLE_LICENSE_TYPE as LPLT on PL.LKUP_PEOPLE_LICENSE_TYPE_ID=LPLT.ID ");
					sb.append("                         WHERE A.ID = MAIN_ID ");
					
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					sb.append("                       ");
					sb.append("                         FOR JSON PATH, INCLUDE_NULL_VALUES  )as '").append(listtype).append("' ");
				}
				
				
				
				if(listtype.startsWith("list_team")){
					JSONArray filters = PrintAgent.getFilters("list_team",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append("         SELECT DISTINCT ");
					//sb.append("         A.ID as ACT_ID, ");
					sb.append("         U.ID as user_id, ");
					sb.append("         FIRST_NAME + ' ' + LAST_NAME   as team_name , ");
					sb.append("         U.USERNAME   as team_username , ");
					sb.append("         U.EMAIL as team_email, ");
					
					sb.append("        type as team_type  , ");
					sb.append("         dept as team_department       ");
					sb.append("                         FROM ACTIVITY A ");
					
					sb.append("                         left outer join REF_ACT_TEAM RAU on A.ID=RAU.ACTIVITY_ID  AND RAU.ACTIVE='Y'     ");
					sb.append("                         LEFT OUTER JOIN   REF_PROJECT_TEAM RAUP on A.PROJECT_ID=RAUP.PROJECT_ID  AND RAUP.ACTIVE='Y'    ");
					sb.append("                         join REF_TEAM RU on (RAU.REF_TEAM_ID=RU.ID  OR      RAUP.REF_TEAM_ID=RU.ID                     ) ");
					
					
					sb.append("                         left outer join LKUP_TEAM_TYPE as LUT on RU.LKUP_TEAM_TYPE_ID=LUT.ID     ");
					sb.append("                         JOIN USERS AS U ON RU.USERS_ID = U.ID    ");
					sb.append("                        LEFT OUTER JOIN (      STAFF AS S      LEFT OUTER JOIN DEPARTMENT AS D ON S.DEPARTMENT_ID = D.ID    ) ON U.ID = S.USERS_ID AND S.ACTIVE = 'Y' ");
					sb.append("                         WHERE A.ID = MAIN_ID ");
					
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					sb.append("                       ");
					sb.append("                         FOR JSON PATH, INCLUDE_NULL_VALUES  )as '").append(listtype).append("' ");
				}
				
				
				//list fee
				
				if(listtype.startsWith("list_fee")){
					JSONArray filters = PrintAgent.getFilters("list_fee",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append(" select FG.GROUP_NAME as fee_group_name ,F.NAME as fee_name,  ");
					//sb.append(" FORMAT(SD.FEE_VALUATION, 'C', 'en-us') as fee_valuation , ");
					sb.append(" FORMAT(SD.FEE_AMOUNT, 'C', 'en-us') as fee_amount , ");
					sb.append(" FORMAT(SD.FEE_PAID, 'C', 'en-us') as fee_paid , ");
					sb.append(" FORMAT(SD.BALANCE_DUE, 'C', 'en-us') as fee_balance , ");
					sb.append(" CASE WHEN INPUT_EDITABLE1 = 'Y' THEN CAST(CAST (SD.input1 AS NUMERIC(18,2)) AS VARCHAR) ELSE '' END as fee_input1, ");
					sb.append(" CASE WHEN INPUT_EDITABLE2 = 'Y' THEN CAST(CAST (SD.input2 AS NUMERIC(18,2)) AS VARCHAR) ELSE '' END as fee_input2, ");
					sb.append(" CASE WHEN INPUT_EDITABLE3 = 'Y' THEN CAST(CAST (SD.input3 AS NUMERIC(18,2)) AS VARCHAR) ELSE '' END as fee_input3, ");
					sb.append(" CASE WHEN INPUT_EDITABLE4 = 'Y' THEN CAST(CAST (SD.input4 AS NUMERIC(18,2)) AS VARCHAR) ELSE '' END as fee_input4, ");
					sb.append(" CASE WHEN INPUT_EDITABLE5 = 'Y' THEN CAST(CAST (SD.input5 AS NUMERIC(18,2)) AS VARCHAR) ELSE '' END as fee_input5 ");
					
					sb.append(",");
					sb.append(" RFF.INPUT_LABEL1 as fee_input_label1,RFF.INPUT_LABEL2 as fee_input_label2 ,RFF.INPUT_LABEL3 as fee_input_label3,RFF.INPUT_LABEL4 as fee_input_label4,RFF.INPUT_LABEL5 as fee_input_label5 ");
					sb.append(",");
					sb.append(" RFF.INPUT_EDITABLE1 as fee_input_editable1,RFF.INPUT_EDITABLE2 as fee_input_editable2 ,RFF.INPUT_EDITABLE3 as fee_input_editable3,RFF.INPUT_EDITABLE4 as fee_input_editable4,RFF.INPUT_EDITABLE5 as fee_input_editable5 ");
					sb.append(",");
					sb.append(" CONVERT(VARCHAR(10),SD.FEE_DATE,101) as fee_date ");
					sb.append(",");
					sb.append(" FG.ID as fee_group_id ");
					sb.append(",");
					sb.append(" F.ID as fee_id ");
					sb.append(",");
					sb.append(" CONVERT(VARCHAR(10),SD.UPDATED_DATE,101) as fee_updated_date ");
					sb.append(",");
					sb.append(" CONVERT(VARCHAR(10),SD.CREATED_DATE,101) as fee_created_date ");
					sb.append(" , ");
					sb.append(" CU.USERNAME AS fee_created ");
					sb.append(" , ");
					sb.append(" UP.USERNAME AS fee_updated ");
					
					
					sb.append(" from ").append(maintableref).append(" A  ");
					sb.append(" JOIN REF_").append(tableref).append("_STATEMENT RAS on A.ID= RAS.").append(idref).append("  ");
					sb.append(" JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y'  ");
					sb.append(" JOIN FEE_GROUP FG on SD.GROUP_ID=FG.ID  ");
					sb.append(" JOIN FEE F on SD.FEE_ID= F.ID  ");
					sb.append(" JOIN REF_FEE_FORMULA RFF on SD.REF_FEE_FORMULA_ID= RFF.ID  ");
					sb.append(" LEFT OUTER JOIN USERS AS CU ON SD.CREATED_BY = CU.ID ");
					sb.append(" LEFT OUTER JOIN USERS UP ON SD.UPDATED_BY = UP.ID  ");
					sb.append("  WHERE  A.ID =Q.MAIN_ID ");
					sb.append("                       ");
					//sb.append(" FOR JSON PATH )as list_fee ");
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					sb.append(" ORDER BY FG.GROUP_NAME,F.NAME,SD.UPDATED_DATE  ");
					
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'            ");
					sb.append("                     ");
			
			}
				
				
				if(listtype.startsWith("list_resolution")){
					JSONArray filters = PrintAgent.getFilters("list_resolution",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append(" select FG.GROUP_NAME as fee_group_name ,F.NAME as fee_name,  ");
					//sb.append(" FORMAT(SD.FEE_VALUATION, 'C', 'en-us') as fee_valuation , ");
					sb.append(" FORMAT(SD.FEE_AMOUNT, 'C', 'en-us') as fee_amount , ");
					sb.append(" FORMAT(SD.FEE_PAID, 'C', 'en-us') as fee_paid , ");
					sb.append(" FORMAT(SD.BALANCE_DUE, 'C', 'en-us') as fee_balance , ");
					sb.append(" input1 as fee_input1,input2 as fee_input2 ,input3 as fee_input3,input4 as fee_input4,input5 as fee_input5 ");
					sb.append(",");
					sb.append(" CONVERT(VARCHAR(10),SD.FEE_DATE,101) as fee_date ");
					sb.append(",");
					sb.append(" FG.ID as fee_group_id ");
					sb.append(",");
					sb.append(" F.ID as fee_id ");
					sb.append(",");
					sb.append(" CONVERT(VARCHAR(10),SD.UPDATED_DATE,101) as fee_updated_date ");
					sb.append(",");
					sb.append(" CONVERT(VARCHAR(10),SD.CREATED_DATE,101) as fee_created_date ");
					sb.append(" , ");
					sb.append(" CU.USERNAME AS fee_created ");
					sb.append(" , ");
					sb.append(" UP.USERNAME AS fee_updated ");
					
					
					sb.append(" from ").append(maintableref).append(" A  ");
					sb.append(" JOIN REF_").append(tableref).append("_STATEMENT RAS on A.ID= RAS.").append(idref).append("  ");
					sb.append(" JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y'  ");
					sb.append(" JOIN FEE_GROUP FG on SD.GROUP_ID=FG.ID  ");
					sb.append(" JOIN FEE F on SD.FEE_ID= F.ID  ");
					sb.append(" LEFT OUTER JOIN USERS AS CU ON SD.CREATED_BY = CU.ID ");
					sb.append(" LEFT OUTER JOIN USERS UP ON SD.UPDATED_BY = UP.ID  ");
					sb.append("  WHERE  A.ID =Q.MAIN_ID ");
					sb.append("                       ");
					//sb.append(" FOR JSON PATH )as list_fee ");
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					sb.append(" ORDER BY SD.UPDATED_DATE DESC ");
					
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'            ");
					sb.append("                     ");
			
			}
				
				/*if(listtype.startsWith("list_fee")){
					sb.append(" , ");
					sb.append("  (      ");
					sb.append(" select FG.GROUP_NAME fee_group_name ,F.NAME as fee_name,  ");
					sb.append(" FORMAT(SD.FEE_VALUATION, 'C', 'en-us') as fee_valuation , ");
					sb.append(" FORMAT(SD.FEE_AMOUNT, 'C', 'en-us') as fee_amount , ");
					sb.append(" FORMAT(SD.FEE_PAID, 'C', 'en-us') as fee_paid , ");
					sb.append(" FORMAT(SD.BALANCE_DUE, 'C', 'en-us') as fee_balance , ");
					
					sb.append(" input1,input2,input3,input4,input5 ");
					
					sb.append(" ,SD.FEE_PAID as fee_paid , SD.BALANCE_DUE as fee_balance from ").append(maintableref).append(" A  ");
					sb.append(" JOIN REF_").append(tableref).append("_STATEMENT RAS on A.ID= RAS.").append(idref).append("  ");
					sb.append(" JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y'  ");
					sb.append(" JOIN FEE_GROUP FG on SD.GROUP_ID=FG.ID  ");
					sb.append(" JOIN FEE F on SD.FEE_ID= F.ID  ");
					sb.append("  join Q on A.ID=Q.MAIN_ID ");
					sb.append("                       ");
					sb.append(" FOR JSON PATH )as list_fee ");
				}*/
				
				
				//list finance 
				if(listtype.equalsIgnoreCase("list_finance")){
					sb.append(" , ");
					sb.append("  (      ");
					
					
					sb.append(" select GROUP_ID as group_id , GROUP_NAME as group_name , ");
					
					sb.append(" FORMAT(PERMIT_FEE, 'C', 'en-us') as activity_fee , ");
					sb.append(" FORMAT(REVIEW_FEE, 'C', 'en-us') as review_fee , ");
					sb.append(" FORMAT(FEE_PAID, 'C', 'en-us')  as  paid,    ");
					sb.append(" FORMAT(BALANCE_DUE, 'C', 'en-us')  as  balance  ");
					sb.append(" from CALC  WHERE FEE_AMOUNT >0  ");
				
					sb.append(" FOR JSON PATH, INCLUDE_NULL_VALUES )as list_finance ");
					
				}
				
				if(listtype.equalsIgnoreCase("list_finance_total")){
					sb.append(" , ");
					sb.append("  (      ");
					/*sb.append(" select 0 as list_finance_total_group_id,'TOTAL' as  list_finance_total_group_name , ");
					
					
					sb.append(" FORMAT(SUM(RN.ACTIVITY_FEE+ CASE WHEN RY.ACTIVITY_FEE IS NULL THEN 0 ELSE RY.ACTIVITY_FEE END ), 'C', 'en-us') as total_activity_fee, "); 
					sb.append(" FORMAT(SUM(RN.REVIEW_FEE+ CASE WHEN RY.REVIEW_FEE IS NULL THEN 0 ELSE RY.REVIEW_FEE END ), 'C', 'en-us') as  total_review_fee,  ");
					sb.append(" FORMAT(SUM(RN.PAID+ CASE WHEN RY.PAID IS NULL THEN 0 ELSE RY.PAID END ), 'C', 'en-us') as  total_paid,    ");
					sb.append(" FORMAT(SUM(RN.BALANCE+ CASE WHEN RY.BALANCE IS NULL THEN 0 ELSE RY.BALANCE END), 'C', 'en-us')  as  total_balance  ");
					
					
					sb.append(" from RN  ");
					sb.append(" left outer join RY on RN.FID= RY.FID ");*/
			
					
					sb.append(" select 0 as total_group_id , 'TOTAL'  as total_group_name , ");
					
					sb.append(" FORMAT(PERMIT_FEE, 'C', 'en-us') as total_activity_fee , ");
					sb.append(" FORMAT(REVIEW_FEE, 'C', 'en-us') as total_review_fee , ");
					sb.append(" FORMAT(FEE_PAID, 'C', 'en-us')  as  total_paid,    ");
					sb.append(" FORMAT(BALANCE_DUE, 'C', 'en-us')  as  total_balance  ");
					sb.append(" from TOTAL    ");
					sb.append(" FOR JSON PATH, INCLUDE_NULL_VALUES ) as list_finance_total ");
					
				}
				
				
				//list notes
				if(listtype.startsWith("list_notes")){
						JSONArray filters = PrintAgent.getFilters("list_notes",listtype);
						sb.append(" , ");
						sb.append("         (      ");
						sb.append(" SELECT ");
						sb.append(" N.ID note_id");
						sb.append(" , ");
						sb.append(" N.NOTE as note_note ");
						sb.append(" , ");
						sb.append(" UP.USERNAME AS note_updated ");
						sb.append(" , ");
						sb.append(" CONVERT(VARCHAR(10), N.CREATED_DATE, 101)  AS note_created_date");
						sb.append(" , ");
						sb.append(" CONVERT(VARCHAR(10), N.UPDATED_DATE, 101)  AS note_updated_date");
						sb.append(" , ");
						sb.append(" N.LKUP_NOTES_TYPE_ID note_lkup_notes_type_id");
						sb.append(" , ");
						sb.append(" T.DESCRIPTION as  note_type ");
						
						sb.append(" FROM ");
						sb.append(" REF_").append(tableref).append("_NOTES AS R ");
					
						sb.append(" JOIN NOTES AS N ON R.NOTES_ID = N.ID AND N.ACTIVE = 'Y' AND R.ACTIVE = 'Y' ");
						sb.append(" JOIN LKUP_NOTES_TYPE AS T ON N.LKUP_NOTES_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
						sb.append(" LEFT OUTER JOIN USERS AS CU ON N.CREATED_BY = CU.ID ");
						sb.append(" LEFT OUTER JOIN USERS UP ON N.UPDATED_BY = UP.ID  ");
						sb.append("  WHERE  R.ACTIVITY_ID =Q.MAIN_ID ");
						if(filters.length()>0){
							for(int f=0;f<filters.length();f++){
								JSONObject fl = filters.getJSONObject(f);
								if(fl.has("filter") && fl.has("filterValue")){
									String val = Operator.sqlEscape(fl.getString("filterValue"));
									String[] v = Operator.split(fl.getString("filterValue"),",");
									if(v.length>0){
										if(Operator.toInt(v[0])<=0){
											val = "'"+Operator.replace(val, ",", "','") +"'";
										}
									}
									sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
								}
							
							}
							
						}
						
						sb.append(" ORDER BY N.CREATED_DATE  ");
						
						sb.append("                       ");
						sb.append("                         FOR JSON PATH )as '").append(listtype).append("'            ");
						sb.append("                     ");
				
				}
				
				if(listtype.startsWith("list_library")){
					JSONArray filters = PrintAgent.getFilters("list_library",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append(" SELECT ");
					sb.append(" R.ID as library_id");
					sb.append(" , ");
					sb.append(" R.TITLE as library_title ");
					sb.append(" , ");
					sb.append(" R.TXT as library_txt ");
					sb.append(" , ");
					sb.append(" R.LIBRARY_CODE as library_code ");
					
					sb.append(" , ");
					sb.append(" R.INSPECTABLE as library_inspectable ");
					sb.append(" , ");
					sb.append(" R.WARNING as library_warning ");
					sb.append(" , ");
					sb.append(" R.COMPLETE as library_complete ");
					sb.append(" , ");
					sb.append(" UP.USERNAME AS library_updated ");
					sb.append(" , ");
					sb.append(" CONVERT(VARCHAR(10),R.UPDATED_DATE,101) as library_updated_date ");
			
					sb.append(" FROM ");
					sb.append(" REF_").append(tableref).append("_LIBRARY AS R ");
				
					sb.append(" LEFT OUTER JOIN USERS AS CU ON R.CREATED_BY = CU.ID ");
					sb.append(" LEFT OUTER JOIN USERS UP ON R.UPDATED_BY = UP.ID  ");
					sb.append("  WHERE  R.ACTIVITY_ID =Q.MAIN_ID  AND R.ACTIVE='Y' ");
					
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}

					
					sb.append(" ORDER BY R.UPDATED_DATE DESC ");
					
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'                ");
					sb.append("                     ");
			
				}
				
				
				
				if(listtype.startsWith("list_attachments")){
					JSONArray filters = PrintAgent.getFilters("list_attachments",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append(" SELECT ");
					sb.append(" ATT.ID attachment_id");
					sb.append(" , ");
					sb.append(" ATT.TITLE as attachment_title ");
					sb.append(" , ");
					sb.append(" ATT.DESCRIPTION as attachment_description ");
					sb.append(" , ");
					sb.append(" ATT.PATH as attachment_path ");
					sb.append(" , ");
					sb.append(" ATT.LOCATION as attachment_location ");
					sb.append(" , ");
					sb.append(" LAT.TYPE as attachment_type ");
					
					sb.append(" , ");
					sb.append(" UP.USERNAME AS attachment_updated ");
					sb.append(" , ");
					sb.append(" CONVERT(VARCHAR(10),R.UPDATED_DATE,101) as attachment_updated_date ");
			
					sb.append(" FROM ");
					sb.append(" ATTACHMENTS AS ATT ");
					sb.append(" JOIN REF_").append(tableref).append("_ATTACHMENTS AS R on ATT.ID= R.ATTACHMENT_ID ");
					sb.append(" LEFT OUTER JOIN LKUP_ATTACHMENTS_TYPE AS LAT ON ATT.LKUP_ATTACHMENTS_TYPE_ID = LAT.ID ");
					sb.append(" LEFT OUTER JOIN USERS AS CU ON R.CREATED_BY = CU.ID ");
					sb.append(" LEFT OUTER JOIN USERS UP ON R.UPDATED_BY = UP.ID  ");
					sb.append("  WHERE  R.ACTIVITY_ID =Q.MAIN_ID  AND R.ACTIVE='Y' ");
					
					
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}

					
					
					sb.append(" ORDER BY R.UPDATED_DATE DESC ");
					
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'                ");
					sb.append("                     ");
			
				}
				
				if(listtype.startsWith("list_holds")){
					JSONArray filters = PrintAgent.getFilters("list_holds",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append(" SELECT ");
					sb.append(" R.ID as hold_id");
					
					sb.append(" ,");
					sb.append(" R.DESCRIPTION as hold_description ");
					sb.append(" ,");
					sb.append(" R.LKUP_HOLDS_TYPE_ID as hold_lkup_holds_type_id ");
					sb.append(" ,");
					sb.append(" R.LKUP_HOLDS_STATUS_ID as hold_lkup_holds_status_id ");
					sb.append(" , ");
					sb.append(" H.TYPE AS hold_type ");
					sb.append(" , ");
					sb.append(" S.DESCRIPTION AS hold_status ");
					sb.append(" , ");
					sb.append(" CU.USERNAME AS hold_created ");
					sb.append(" , ");
					sb.append(" UP.USERNAME AS hold_updated ");
					sb.append(" , ");
					sb.append(" CONVERT(VARCHAR(10),R.UPDATED_DATE,101) as hold_updated_date ");
			
					sb.append(" FROM ");
					sb.append(" REF_").append(tableref).append("_HOLDS AS R ");
					sb.append(" LEFT OUTER JOIN LKUP_HOLDS_TYPE H ON R.LKUP_HOLDS_TYPE_ID = H.ID ");
					sb.append(" LEFT OUTER JOIN LKUP_HOLDS_STATUS S ON R.LKUP_HOLDS_STATUS_ID = S.ID ");
					sb.append(" LEFT OUTER JOIN USERS CU ON R.CREATED_BY = CU.ID ");
					sb.append(" LEFT OUTER JOIN USERS UP ON R.UPDATED_BY = UP.ID  ");
					sb.append("  WHERE  R.ACTIVITY_ID =Q.MAIN_ID  AND R.ACTIVE='Y' ");
					

					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					
					
					
					sb.append(" ORDER BY R.UPDATED_DATE DESC ");
					
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'                ");
					sb.append("                     ");
			
				}
				
				
				if(listtype.startsWith("list_custom55")){
					JSONArray filters = PrintAgent.getFilters("list_custom",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append(" SELECT ");
					sb.append(" R.FIELD_ID as custom_field_id, F.NAME as custom_field_name ");
					sb.append(" ,");
					sb.append(" FG.ID as custom_group_id, FG.GROUP_NAME as custom_group_name ");
					sb.append(" ,");
					sb.append(" value as custom_value, value_char as custom_value_char, value_int as custom_value_int,value_date as custom_value_date,value_decimal as custom_value_decimal ");
					sb.append(" ,");
					sb.append(" FC.TITLE AS custom_field_choice_title ");
					sb.append(" , ");
					sb.append(" FC.TITLE_VALUE AS custom_field_choice_title_value ");
					sb.append(" , ");
					sb.append(" CU.USERNAME AS custom_created ");
					sb.append(" , ");
					sb.append(" UP.USERNAME AS custom_updated ");
					sb.append(" , ");
					sb.append(" CONVERT(VARCHAR(10),R.UPDATED_DATE,101) as custom_updated_date ");
			
					sb.append(" FROM ");
					sb.append(" ").append(tableref).append("_FIELD_VALUE AS R ");
					sb.append("  JOIN FIELD F ON R.FIELD_ID = F.ID ");
					sb.append("  JOIN FIELD_GROUPS FG ON F.FIELD_GROUPS_ID = FG.ID ");
					sb.append(" LEFT OUTER JOIN FIELD_CHOICES FC ON F.ID = FC.FIELD_ID and R.VALUE_INT = FC.ID");
					sb.append(" LEFT OUTER JOIN USERS CU ON R.CREATED_BY = CU.ID ");
					sb.append(" LEFT OUTER JOIN USERS UP ON R.UPDATED_BY = UP.ID  ");
					sb.append("  WHERE  R.ACTIVITY_ID =Q.MAIN_ID  AND R.ACTIVE='Y' ");
					

					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					
					
					
					sb.append(" ORDER BY R.UPDATED_DATE ASC ");
					
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'                ");
					sb.append("                     ");
			
				}
				
				if(listtype.startsWith("list_reviews")){
					JSONArray filters = PrintAgent.getFilters("list_reviews",listtype);
			
					sb.append(" , ");
					sb.append(" (      ");
					sb.append(" select RG.GROUP_NAME AS review_group_name, ");
					sb.append(" C.TITLE AS review_sub_title, ");
					sb.append(" CONVERT(VARCHAR(10),C.START_DATE,101) as review_sub_date, ");
				
					sb.append(" R.NAME AS review_name, ");
					sb.append(" R.DESCRIPTION AS review_description, ");
					sb.append(" CONVERT(VARCHAR(10),RCA.DATE,101) as review_date, ");
					sb.append(" RCA.review_comments as review_comment, ");
					sb.append(" LRT.TYPE as review_type,  ");
					sb.append(" LRS.STATUS  as review_status, ");
					sb.append(" LTT.TYPE AS review_team_type, ");
					sb.append(" U.USERNAME AS review_user, ");
					sb.append(" CONVERT(VARCHAR(10),RCA.UPDATED_DATE,101) as review_updated_date, ");
					sb.append(" CONVERT(VARCHAR(50),RCA.UPDATED_DATE,109) as review_updated_date_time, ");
					
					sb.append(" ATT.TITLE as review_attachment_title ");
					sb.append(" , ");
					sb.append(" ATT.DESCRIPTION as review_attachment_description ");
					sb.append(" , ");
					sb.append(" ATT.PATH as review_attachment_path ");
					
					sb.append(" , ");
					sb.append("  CONVERT(VARCHAR(10),RR.DUE_DATE,101)  as review_due_date    ");
					
					
					sb.append(" , ");
					sb.append(" UA.FIRST_NAME + ' ' + UA.LAST_NAME   AS review_team_name ,     ");
					sb.append("  UA.EMAIL as review_team_email,     ");
					sb.append(" P.PHONE_WORK +' '+PHONE_CELL +' '+ PHONE_HOME AS review_team_phone   ");
					
					
					sb.append("  ");
					sb.append(" from  ");
					sb.append(" ACTIVITY A  ");
					//sb.append("  join Q on A.ID =Q.MAIN_ID ");
					sb.append(" join REF_ACT_COMBOREVIEW RAC on A.ID= RAC.ACTIVITY_ID  ");
					sb.append(" join COMBOREVIEW C on RAC.COMBOREVIEW_ID= C.ID ");
					sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RR ON C.ID = RR.COMBOREVIEW_ID AND RR.ACTIVE = 'Y'  ");
					sb.append(" JOIN REF_COMBOREVIEW_ACTION AS RCA ON RR.ID = RCA.REF_COMBOREVIEW_REVIEW_ID AND RCA.ACTIVE = 'Y'  ");
					sb.append(" JOIN REVIEW AS R ON RR.REVIEW_ID = R.ID ");
					sb.append(" JOIN LKUP_REVIEW_STATUS LRS on RCA.LKUP_REVIEW_STATUS_ID= LRS.ID  ");
					sb.append(" JOIN LKUP_REVIEW_TYPE LRT on R.LKUP_REVIEW_TYPE_ID = LRT.ID ");
					sb.append(" join REVIEW_GROUP RG on R.REVIEW_GROUP_ID= RG.ID ");
					sb.append(" left outer join REF_COMBOREVIEW_TEAM RCRT on  RR.ID =RCRT.REF_COMBOREVIEW_REVIEW_ID  AND RCRT.ACTIVE='Y' ");
					sb.append(" left outer join REF_TEAM RT on RCRT.REF_TEAM_ID= RT.ID ");
					sb.append(" left outer join LKUP_TEAM_TYPE LTT on RT.LKUP_TEAM_TYPE_ID= LTT.ID ");
					sb.append(" left outer join USERS U on RCA.UPDATED_BY= U.ID ");
					
					sb.append(" left outer join REF_COMBOREVIEW_ATTACHMENTS RCAT on RCA.ID= RCAT.ID AND RCAT.ACTIVE='Y'   ");
					sb.append(" left outer join ATTACHMENTS AS ATT on RCAT.ATTACHMENT_ID =ATT.ID  AND ATT.ACTIVE='Y' ");
					
					sb.append(" left outer JOIN USERS AS UA ON RT.USERS_ID = UA.ID         AND UA.ACTIVE= 'Y'   ");
					sb.append(" LEFT OUTER JOIN PEOPLE AS P ON UA.ID=P.USERS_ID    ");
					
					sb.append("  WHERE  A.ID =Q.MAIN_ID ");
					//sb.append(" where A.ID in (132759,132760,1101428)   ");
					//sb.append(" --AND RG.ID=8 ");
					//sb.append(" --group by RG.GROUP_NAME,R.NAME,R.DESCRIPTION,RCA.DATE,RCA.REVIEW_COMMENTS,LRT.TYPE, LRS.STATUS,LTT.TYPE,USERNAME ");

					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								if(fl.getString("filter").equalsIgnoreCase("LKUP_REVIEW_STATUS_ID")){
									sb.append("  AND   LOWER(RCA.").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");
								}else{
									sb.append("  AND   LOWER(R.").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");
								}
							}
						
						}
						
					}
					
					sb.append(" order by GROUP_NAME,C.START_DATE, RCA.DATE ");
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'     ");
					sb.append("   ");
				}
				
				
				
				if(listtype.startsWith("list_appointments")){
					JSONArray filters = PrintAgent.getFilters("list_appointments",listtype);
			
					sb.append(" , ");
					sb.append(" (      ");
					sb.append(" SELECT  A.ID as appointment_id  ,   ");
					sb.append(" CASE WHEN RMG.ID IS NOT NULL THEN RMG.ID ELSE T.ID END AS appointment_type  ,   ");
					sb.append(" CASE WHEN RMG.GROUP_NAME IS NOT NULL THEN RMG.GROUP_NAME ELSE T.TYPE END AS appointment_type_text  ,   ");
					sb.append(" CASE WHEN RM.NAME IS NOT NULL THEN RM.NAME ELSE A.SUBJECT END AS appointment_subject  ,   ");
					sb.append(" CASE WHEN RM.NAME IS NOT NULL THEN RM.NAME ELSE A.SUBJECT END AS appointment_subject_text  ,   ");
					sb.append(" S.START_DATE as appointment_start_date  ,  S.END_DATE as appointment_end_date ,  ");
					sb.append(" CASE WHEN NXTS.STATUS IS NOT NULL THEN NXTS.STATUS  ");
					sb.append(" WHEN CRS.STATUS IS NOT NULL THEN CRS.STATUS WHEN S.END_DATE < getDate() THEN 'EXPIRED' ELSE ST.STATUS END AS appointment_status  ,   ");
					sb.append(" CASE WHEN NXTS.STATUS IS NOT NULL THEN 'Y' WHEN ST.COMPLETE = 'Y' THEN 'Y' ELSE 'N' END AS appointment_final  ,   ");
					sb.append(" CASE WHEN S.REF_COMBOREVIEW_ACTION_ID > 0 AND NXTS.STATUS IS NULL THEN 'Y' WHEN ST.COMPLETE = 'N' THEN 'Y' ELSE 'N' END AS appointment_scheduled  ,  ");
					sb.append(" CASE WHEN S.REF_COMBOREVIEW_ACTION_ID > 0 AND NXTS.STATUS IS NULL THEN 'Y'  ");
					sb.append(" WHEN ST.COMPLETE = 'Y' THEN 'N' WHEN S.END_DATE < getDate() THEN 'N' ELSE 'Y' END AS appointment_isactive  ,  S.ID AS SCHEDULE_ID  ,  ");
					sb.append(" RMR.ID AS REF_COMBOREVIEW_REVIEW_ID  ,  CRA.ID AS REF_COMBOREVIEW_ACTION_ID  ,  RMR.COMBOREVIEW_ID  ,   ");
					sb.append(" RM.ID AS REVIEW_ID  ,  RM.NAME AS REVIEW  ,  RMG.ID AS REVIEW_GROUP_ID  ,  RMG.GROUP_NAME AS REVIEW_GROUP  ,  S.CREATED_DATE  ,  ");
					sb.append(" COUNT(DISTINCT U.ID) AS appointment_collaborators  ");
					sb.append(" FROM  REF_ACT_APPOINTMENT AS R   ");
					sb.append(" JOIN APPOINTMENT AS A ON R.APPOINTMENT_ID = A.ID   ");
					sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y'   ");
					sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID   ");
					sb.append(" LEFT OUTER JOIN LKUP_APPOINTMENT_TYPE AS T ON A.LKUP_APPOINTMENT_TYPE_ID > 0 AND A.LKUP_APPOINTMENT_TYPE_ID = T.ID   ");
					sb.append(" LEFT OUTER JOIN (    REF_COMBOREVIEW_REVIEW AS RMR    JOIN REVIEW AS RM ON RMR.REVIEW_ID = RM.ID AND RM.ACTIVE = 'Y'    ");
					sb.append(" JOIN REVIEW_GROUP AS RMG ON RM.REVIEW_GROUP_ID = RMG.ID AND RMG.ACTIVE = 'Y'  ) ON RMR.ID = A.REF_COMBOREVIEW_REVIEW_ID  ");
					sb.append(" AND RMR.ACTIVE = 'Y'  LEFT OUTER JOIN (    REF_COMBOREVIEW_ACTION AS CRA     ");
					sb.append(" JOIN LKUP_REVIEW_STATUS AS CRS ON CRA.LKUP_REVIEW_STATUS_ID = CRS.ID     ");
					sb.append(" LEFT OUTER JOIN (      REF_COMBOREVIEW_ACTION AS NXTA       ");
					sb.append(" JOIN LKUP_REVIEW_STATUS AS NXTS ON NXTA.LKUP_REVIEW_STATUS_ID = NXTS.ID AND NXTS.ACTIVE = 'Y'  ");
					sb.append(" AND NXTA.ACTIVE = 'Y'    ) ON NXTA.PREVIOUS_ID = CRA.ID  ) ON S.REF_COMBOREVIEW_ACTION_ID = CRA.ID   ");
					sb.append(" LEFT OUTER JOIN (    REF_APPOINTMENT_USERS AS RAU     ");
					sb.append(" JOIN USERS AS U ON RAU.USERS_ID = U.ID AND U.ACTIVE = 'Y' AND RAU.ACTIVE = 'Y'  )  ON A.ID = RAU.APPOINTMENT_ID  ");
					//sb.append(" WHERE  R.ACTIVITY_ID = 1101428  AND  ST.SCHEDULED = 'Y'  AND  A.ACTIVE = 'Y'  AND  R.ACTIVE = 'Y'   ");
					
					sb.append("  WHERE  R.ACTIVITY_ID =Q.MAIN_ID  AND  ST.SCHEDULED = 'Y'  AND  A.ACTIVE = 'Y'  AND  R.ACTIVE = 'Y'  ");
					//sb.append(" where A.ID in (132759,132760,1101428)   ");
					//sb.append(" --AND RG.ID=8 ");
				//	sb.append(" group by RG.GROUP_NAME,R.NAME,R.DESCRIPTION,RCA.DATE,RCA.REVIEW_COMMENTS,LRT.TYPE, LRS.STATUS,LTT.TYPE,USERNAME ");

					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					sb.append(" GROUP BY  S.ID  ,  A.ID  ,  RMG.ID  ,  T.ID  ,  RMG.GROUP_NAME  ,  T.TYPE  ,  RM.NAME  ,  A.SUBJECT  ,  S.START_DATE  ,  ");
					sb.append(" S.END_DATE  ,  NXTS.STATUS  ,  CRS.STATUS  ,  ST.STATUS  ,  ST.COMPLETE  ,  S.REF_COMBOREVIEW_ACTION_ID  ,  ");
					sb.append(" RMR.ID  ,  CRA.ID  ,  RMR.COMBOREVIEW_ID  ,  RM.ID  ,  S.CREATED_DATE   ");
					
					sb.append("  ORDER BY START_DATE DESC, END_DATE DESC, CREATED_DATE ASC ");
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'     ");
					sb.append("   ");
				}
				
				if(listtype.startsWith("list_payment")){
					JSONArray filters = PrintAgent.getFilters("list_payment",listtype);
					sb.append(" , ");
					sb.append(" (      ");
					sb.append(" select  P.ID as payment_id, M.METHOD_TYPE as payment_method, ");
					sb.append(" FORMAT(P.PAYMENT_AMOUNT, 'C', 'en-us') AS payment_amount, ");
				
					
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
					sb.append("     )) AS payment_paid_by ");
					
					sb.append(",");
					sb.append(" CONVERT(VARCHAR(10), P.PAYMENT_DATE, 101) + ' ' + RIGHT(CONVERT(CHAR(20), PAYMENT_DATE, 22), 11) as payment_date ");
					sb.append(",");
					sb.append(" T.TYPE as payment_transaction_type ");
					
					sb.append(" from payment p ");
					sb.append(" join LKUP_PAYMENT_METHOD M on P.LKUP_PAYMENT_METHOD_ID= M.ID  ");
					sb.append("   left outer join LKUP_PAYMENT_TRANSACTION_TYPE T on P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID  ");
				//	sb.append(" left outer join REF_USERS R ON P.PAYEE_ID = R.ID ");
					sb.append(" left outer join USERS AU ON P.PAYEE_ID = AU.ID ");
					
					sb.append(" left outer join REF_").append(tableref).append("_PAYMENT RAP ON P.ID = RAP.PAYMENT_ID ");
					sb.append(" where P.AUTO='N' AND RAP.").append(idref).append("=").append(" Q.MAIN_ID ");
					
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					
					
					sb.append(" order by P.PAYMENT_DATE");
				
				sb.append("                       ");
				sb.append("                         FOR JSON PATH )as '").append(listtype).append("'  ");
				sb.append("   ");
				}
				
			}
		}
		
		
		
		
		sb.append("  ");
		sb.append(" from  ACTIVITY A  ");
		sb.append(" join Q on A.ID=Q.MAIN_ID ");
		sb.append(" left outer join LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID=LAT.ID  ");
		sb.append(" left outer join DEPARTMENT D ON LAT.DEPARTMENT_ID=D.ID  ");
		sb.append(" LEFT OUTER JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID=LAS.ID ");
		sb.append(" LEFT OUTER JOIN USERS AS CU ON A.CREATED_BY = CU.ID   ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID  ");
		
		
		//PROJECT
		 sb.append(" LEFT OUTER join PROJECT P  ON A.PROJECT_ID=P.ID ");
		 sb.append(" 	LEFT OUTER join LKUP_PROJECT_TYPE LPT on P.LKUP_PROJECT_TYPE_ID=LPT.ID  ");
		 sb.append(" 	LEFT OUTER join LKUP_PROJECT_STATUS LPS on P.LKUP_PROJECT_STATUS_ID=LPS.ID  ");
		 sb.append(" LEFT OUTER JOIN USERS AS PCU ON P.CREATED_BY = PCU.ID ");
		 sb.append(" LEFT OUTER JOIN USERS PUP ON P.UPDATED_BY = PUP.ID  ");
		
		 
		 
		//divisions
		// sb.append(" LEFT OUTER JOIN DIVISIONS DIVISIONS ON A.ID = DIVISIONS.DIVISIONS_ACT_ID  ");
		/* 
		sb.append(" 	join REF_LSO_PROJECT R on A.PROJECT_ID=R.PROJECT_ID ");
		sb.append(" 	left outer join REF_LSO_DIVISIONS RLD on R.LSO_ID= RLD.LSO_ID  ");
		sb.append(" 	left outer join LKUP_DIVISIONS LD on RLD.LKUP_DIVISIONS_ID= LD.ID and LD.ACTIVE='Y' ");
		sb.append(" 	left outer join LKUP_DIVISIONS_TYPE LDT on LD.LKUP_DIVISIONS_TYPE_ID=LDT.ID ");
		 sb.append(" LEFT OUTER JOIN USERS PUP ON P.UPDATED_BY = PUP.ID  ");
		
		//address
		sb.append(" 	join LSO L on R.LSO_ID=L.ID ");
		sb.append(" 	join LSO_STREET LS on L.LSO_STREET_ID=LS.ID ");
		
		//apn
		sb.append(" 	left outer join REF_LSO_APN RLA on L.ID =RLA.LSO_ID AND RLA.ACTIVE='Y' ");*/
		 
		 
		sb.append("  ");
		sb.append("  ");
		sb.append(" where  A.ACTIVE='Y' ");
		sb.append("  ");
		sb.append(" FOR JSON PATH)  ");
		sb.append(" as RESULTS ");
		sb.append("  ");
		
		} catch(Exception e){
			Logger.error(e.getMessage());
		}
		return sb.toString();
	}
	
	
	
	
	public static String getAllInOneQueryProject(String type, String ids,JSONObject doList,String additionalWith,String additionalSelect,String additionalJoins) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		String maintableref = CsReflect.getMainTableRef(type);
		StringBuilder sb = new StringBuilder();
		
		
		try{
		
		sb.append("WITH Q as( ");
		sb.append("  ");
		//sb.append(" select TOP 10 ID as MAIN_ID from activity WHERE LKUP_ACT_TYPE_ID = 2 order by UPDATED_DATE DESC ");
		//sb.append(" select TOP 10 ID as MAIN_ID from activity WHERE ID = 2092942 ");
		sb.append(" select ID as MAIN_ID from project WHERE ID in (").append(ids).append(") ");
		sb.append(" ) ");
		
		
		
		
		//if(doList.has("list_finance") || doList.has("list_finance_total")) {
			/*sb.append(", RY AS ( ");
			sb.append(" select   A.ID as FID,  FG.GROUP_NAME  , FG.ID AS GROUP_ID,  CASE WHEN SUM(SD.FEE_AMOUNT) IS NULL THEN 0 ELSE SUM(SD.FEE_AMOUNT) END as REVIEW_FEE ,");
			sb.append(" 0.00 as ACTIVITY_FEE, CASE WHEN SUM(SD.FEE_PAID) IS NULL THEN 0 ELSE  SUM(SD.FEE_PAID) END as PAID, ");
			sb.append(" CASE WHEN SUM(SD.BALANCE_DUE) IS NULL THEN 0 ELSE   SUM(SD.BALANCE_DUE) END as BALANCE ");  
			sb.append(" from ").append(maintableref).append("  A    ");
			sb.append("  join Q on A.ID=Q.MAIN_ID ");
			sb.append(" join REF_").append(tableref).append("_STATEMENT RAS on A.ID=RAS.").append(idref).append(" ");   
			sb.append(" join STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");   
			sb.append(" join STATEMENT_DETAIL SD on  S.ID=SD.STATEMENT_ID  and SD.ACTIVE ='Y' "); 
			sb.append(" join FEE_GROUP FG   on SD.GROUP_ID=FG.ID  ");
			sb.append(" left outer join REF_FEE_GROUP RFG on FG.ID =RFG.FEE_GROUP_ID AND SD.FEE_ID= RFG.FEE_ID AND REVIEW_FEE='Y' ");
			sb.append(" where REVIEW_FEE IS NOT NULL ");
			sb.append(" group by FG.ID,FG.GROUP_NAME ,A.ID ");
			sb.append(" ), RN AS ( ");
			sb.append(" ");
			sb.append(" select   A.ID as FID,  FG.GROUP_NAME  , FG.ID AS GROUP_ID, 0.00 as REVIEW_FEE,CASE WHEN SUM(SD.FEE_AMOUNT) IS NULL THEN 0 ELSE SUM(SD.FEE_AMOUNT) END as ACTIVITY_FEE , CASE WHEN SUM(SD.FEE_PAID) IS NULL THEN 0 ELSE  SUM(SD.FEE_PAID) END  as PAID, ");
			sb.append(" CASE WHEN SUM(SD.BALANCE_DUE) IS NULL THEN 0 ELSE   SUM(SD.BALANCE_DUE) END as BALANCE   ");
			sb.append(" from ").append(maintableref).append("  A    ");
			sb.append("  join Q on A.ID=Q.MAIN_ID ");
			sb.append(" join REF_").append(tableref).append("_STATEMENT RAS on A.ID=RAS.").append(idref).append(" ");   
			sb.append(" join STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y' ");   
			sb.append(" join STATEMENT_DETAIL SD on  S.ID=SD.STATEMENT_ID  and SD.ACTIVE ='Y' "); 
			sb.append(" join FEE_GROUP FG   on SD.GROUP_ID=FG.ID "); 
			sb.append(" left outer join REF_FEE_GROUP RFG on FG.ID =RFG.FEE_GROUP_ID AND SD.FEE_ID= RFG.FEE_ID AND REVIEW_FEE='N' ");
			sb.append(" where  REVIEW_FEE IS NOT NULL ");
			sb.append(" group by FG.ID,FG.GROUP_NAME  ,A.ID ");
			sb.append(" ) ");*/
		
		sb.append(" , APPT AS (   ");
		sb.append(" SELECT  ACT.ACT_NBR as activity_act_nbr, ");
		sb.append(" LAT.TYPE as activity_type ,  ");
		sb.append(" LAS.DESCRIPTION as activity_status , ");
		sb.append(" ACT.DESCRIPTION as activity_description  ");
		sb.append(" , A.ID  ,  A.ID AS APPOINTMENT_ID  ,   ");
		sb.append(" CASE WHEN RMG.ID IS NOT NULL THEN RMG.ID ELSE T.ID END AS TYPE  ,   ");
		sb.append(" CASE WHEN RMG.GROUP_NAME IS NOT NULL THEN RMG.GROUP_NAME ELSE T.TYPE END AS appointment_type  ,   ");
		sb.append(" CASE WHEN RM.NAME IS NOT NULL THEN RM.NAME ELSE A.SUBJECT END AS appointment_subject,   ");
		sb.append(" CASE WHEN RM.NAME IS NOT NULL THEN RM.NAME ELSE A.SUBJECT END AS SUBJECT_TEXT  ,   ");
		sb.append(" S.START_DATE  as appointment_start_date,  S.END_DATE  as appointment_end_date,   ");
		sb.append(" CONVERT(VARCHAR(10),S.START_DATE , 101) + ' @ ' + CONVERT(VARCHAR(10),S.START_DATE , 108) + ' - ' + CONVERT(VARCHAR(10),S.END_DATE , 108) as appointment_date, ");
		sb.append(" CASE WHEN NXTS.STATUS IS NOT NULL THEN NXTS.STATUS WHEN CRS.STATUS IS NOT NULL THEN CRS.STATUS WHEN S.END_DATE < getDate() THEN 'EXPIRED' ELSE ST.STATUS END AS status  ,   ");
		sb.append(" CASE WHEN NXTS.STATUS IS NOT NULL THEN 'Y' WHEN ST.COMPLETE = 'Y' THEN 'Y' ELSE 'N' END AS FINAL  ,   ");
		sb.append(" CASE WHEN S.REF_COMBOREVIEW_ACTION_ID > 0 AND NXTS.STATUS IS NULL THEN 'Y' WHEN ST.COMPLETE = 'N' THEN 'Y' ELSE 'N' END AS SCHEDULED  ,   ");
		sb.append(" CASE WHEN S.REF_COMBOREVIEW_ACTION_ID > 0 AND NXTS.STATUS IS NULL THEN 'Y' WHEN ST.COMPLETE = 'Y' THEN 'N' WHEN S.END_DATE < getDate() THEN 'N' ELSE 'Y' END AS ISACTIVE  , ");
		sb.append(" S.ID AS SCHEDULE_ID  ,  RMR.ID AS REF_COMBOREVIEW_REVIEW_ID  ,   ");
		sb.append(" CRA.ID AS REF_COMBOREVIEW_ACTION_ID  ,  RMR.COMBOREVIEW_ID  ,   ");
		sb.append(" RM.ID AS REVIEW_ID  ,  RM.NAME AS REVIEW  ,  RMG.ID AS REVIEW_GROUP_ID  ,   ");
		sb.append(" RMG.GROUP_NAME AS REVIEW_GROUP  ,  S.CREATED_DATE  ,   ");
		sb.append(" COUNT(DISTINCT U.ID) AS COLLABORATORS   ");
		sb.append(" FROM   ");
		sb.append(" ACTIVITY ACT  ");
		sb.append(" left outer join LKUP_ACT_TYPE LAT ON ACT.LKUP_ACT_TYPE_ID=LAT.ID  ");
		sb.append(" LEFT OUTER JOIN LKUP_ACT_STATUS LAS on ACT.LKUP_ACT_STATUS_ID=LAS.ID ");
		sb.append("  ");
		sb.append(" JOIN REF_ACT_APPOINTMENT AS R on ACT.ID=R.ACTIVITY_ID   ");
		sb.append(" JOIN APPOINTMENT AS A ON R.APPOINTMENT_ID = A.ID   ");
		sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y'   ");
		sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID   ");
		sb.append(" LEFT OUTER JOIN LKUP_APPOINTMENT_TYPE AS T ON A.LKUP_APPOINTMENT_TYPE_ID > 0 AND A.LKUP_APPOINTMENT_TYPE_ID = T.ID   ");
		sb.append(" LEFT OUTER JOIN (     ");
		sb.append(" REF_COMBOREVIEW_REVIEW AS RMR    JOIN REVIEW AS RM ON RMR.REVIEW_ID = RM.ID AND RM.ACTIVE = 'Y'     ");
		sb.append(" JOIN REVIEW_GROUP AS RMG ON RM.REVIEW_GROUP_ID = RMG.ID AND RMG.ACTIVE = 'Y'   ");
		sb.append(" ) ON RMR.ID = A.REF_COMBOREVIEW_REVIEW_ID AND RMR.ACTIVE = 'Y'   ");
		sb.append(" LEFT OUTER JOIN (    REF_COMBOREVIEW_ACTION AS CRA    JOIN LKUP_REVIEW_STATUS AS CRS ON CRA.LKUP_REVIEW_STATUS_ID = CRS.ID     ");
		sb.append(" LEFT OUTER JOIN (      REF_COMBOREVIEW_ACTION AS NXTA       ");
		sb.append(" JOIN LKUP_REVIEW_STATUS AS NXTS ON NXTA.LKUP_REVIEW_STATUS_ID = NXTS.ID AND NXTS.ACTIVE = 'Y' AND NXTA.ACTIVE = 'Y'    ) ON NXTA.PREVIOUS_ID = CRA.ID  )  ");
		sb.append(" ON S.REF_COMBOREVIEW_ACTION_ID = CRA.ID   ");
		sb.append(" LEFT OUTER JOIN (    REF_APPOINTMENT_USERS AS RAU    JOIN USERS AS U ON RAU.USERS_ID = U.ID AND U.ACTIVE = 'Y' AND RAU.ACTIVE = 'Y'  )  ");
		sb.append(" ON A.ID = RAU.APPOINTMENT_ID   ");
		sb.append(" JOIN Q on  ACT.PROJECT_ID = Q.MAIN_ID  ");
		sb.append(" WHERE  ST.SCHEDULED = 'Y'  AND  A.ACTIVE = 'Y'  AND  R.ACTIVE = 'Y'   ");
		sb.append(" GROUP BY  ACT.ACT_NBR,LAT.TYPE, LAS.DESCRIPTION,ACT.DESCRIPTION, S.ID  ,  A.ID  ,  RMG.ID  ,  T.ID  ,  RMG.GROUP_NAME  ,  T.TYPE  ,  RM.NAME  ,  A.SUBJECT  ,  S.START_DATE  ,  S.END_DATE  ,  NXTS.STATUS  ,  CRS.STATUS  ,  ST.STATUS  ,  ST.COMPLETE  ,  S.REF_COMBOREVIEW_ACTION_ID  ,  RMR.ID  ,  CRA.ID  ,  RMR.COMBOREVIEW_ID  ,  RM.ID  ,  S.CREATED_DATE  ");
		sb.append(" )  ");
		sb.append(",");
		sb.append(FinanceSQL.calcSubqueries(type, "SELECT MAIN_ID FROM Q "));
		
		//}
		
		sb.append(" select (	 ");
		sb.append("  ");
		
		sb.append(" SELECT  DISTINCT ");
  	    sb.append("  P.ID as project_id");
		sb.append(" , ");
		 
		sb.append(" P.ID as type_id , ");
		sb.append(" 'project' as type , ");
		sb.append(" '").append(CsApiConfig.getQrcodeDomain()).append("/cs/?entity=lso&type=project&typeid='+CAST(P.ID AS varchar) as qr_code, ");

		 sb.append(" PROJECT_NBR as project_nbr");
		 sb.append(" , ");
		 sb.append(" P.NAME as project_name");
		 sb.append(" , ");
		 sb.append(" P.DESCRIPTION as project_description");
		 sb.append(" , ");
		 sb.append(" P.CIP_ACCTNO as project_cip_acctno");
		 sb.append(" , ");
		 sb.append(" DTA.ID as project_account_number");
		 sb.append(" , ");
		 sb.append(" LPT.TYPE as project_type");
		 sb.append(" , ");
		 sb.append(" LPS.STATUS as project_status");
		 sb.append(" , ");
		 sb.append(" PCU.USERNAME as project_created_by");
		 sb.append(" , ");
		 sb.append(" PUP.USERNAME as project_updated_by");
		 sb.append(" , ");
		 sb.append(" P.CREATED_DATE as project_created_date");
		 sb.append(" , ");
		 sb.append(" P.UPDATED_DATE as project_updated_date");
		 
		 
		 sb.append(" , ");
		
				
		 sb.append(" CONVERT(VARCHAR(10),P.START_DT,101) as project_start_date , ");
		 sb.append(" CONVERT(VARCHAR(10),P.APPLIED_DT,101) as project_applied_date, ");
		 sb.append(" CONVERT(VARCHAR(10),P.COMPLETION_DT,101) as project_completed_date , ");
		 sb.append(" CONVERT(VARCHAR(10),P.EXPIRED_DT,101) as project_exp_date  ");

		
		if(Operator.hasValue(additionalSelect)){
			sb.append(additionalSelect);
		}
		
		//listpeople
		
		if(doList.has("lists")){
			for(int i=0;i<doList.getJSONArray("lists").length();i++){
				JSONObject o = doList.getJSONArray("lists").getJSONObject(i);
				String listtype = o.getString("type");
				Logger.info(i+"i--------------------->"+listtype);
				
				if(listtype.startsWith("list_people")){
					JSONArray filters = PrintAgent.getFilters("list_people",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append("         SELECT DISTINCT ");
					//sb.append("         A.ID as ACT_ID, ");
					sb.append("         U.ID as user_id, ");
					sb.append("         P.ID as people_id, ");
					sb.append("         FIRST_NAME + ' ' + LAST_NAME   as people_name , ");
					sb.append("         U.USERNAME   as people_username , ");
					sb.append("         U.EMAIL as people_email, ");
					
					sb.append("         P.CITY as people_city, ");
					sb.append("         P.STATE as people_state, ");
					sb.append("         P.ZIP as people_zip, ");
					sb.append("         P.ZIP4 as people_zip4, ");
					
					sb.append("         P.PHONE_HOME as people_phone_home, ");
					sb.append("         P.FAX as people_fax, ");
					sb.append("         P.COMMENTS as people_comments, ");
					
					sb.append("         P.PHONE_WORK as people_phone_work, ");
					sb.append("         P.PHONE_CELL as people_phone_cell, ");
					//sb.append(" RN.ACTIVITY_FEE+ CASE WHEN RY.ACTIVITY_FEE IS NULL THEN 0 ELSE RY.ACTIVITY_FEE END as finance_activity_fee , ");
					sb.append("        PRIMARY_CONTACT as people_primary_contact, ");
					
					sb.append("         FIRST_NAME as people_first_name , ");
					sb.append("         MI as people_mi, ");
					sb.append("         LAST_NAME people_last_name, ");
					sb.append("         LUT.TYPE as people_type , ");
				
					sb.append("         ADDRESS as people_address ");
					//	sb.append("         , ");
					//	sb.append("         PL.LIC_NUM as people_lic_num, ");
					//	sb.append("         CONVERT(VARCHAR(10), PL.LIC_EXPIRATION_DATE, 101) as people_lic_expiration_date, ");
					//	sb.append("         LPLT.TYPE as people_license_type ");
					sb.append("                         FROM PROJECT A ");
					
					sb.append("                         join REF_PROJECT_USERS RAU on A.ID=RAU.PROJECT_ID  AND RAU.ACTIVE='Y'  ");
					sb.append("                         join REF_USERS RU on RAU.REF_USERS_ID=RU.ID  ");
					sb.append("                         left outer join LKUP_USERS_TYPE as LUT on RU.LKUP_USERS_TYPE_ID=LUT.ID    ");
					sb.append("                         JOIN USERS AS U ON RU.USERS_ID = U.ID  ");
					sb.append("                         left outer join PEOPLE P on U.ID = P.USERS_ID  AND P.ACTIVE='Y' ");
				//	sb.append("                         left outer join PEOPLE_LICENSE as PL on P.ID=PL.PEOPLE_ID ");
				//	sb.append("                         left outer join LKUP_PEOPLE_LICENSE_TYPE as LPLT on PL.LKUP_PEOPLE_LICENSE_TYPE_ID=LPLT.ID ");
					sb.append("                         WHERE A.ID = MAIN_ID ");
					
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					sb.append("                       ");
					sb.append("                         FOR JSON PATH, INCLUDE_NULL_VALUES  )as '").append(listtype).append("' ");
				}
				
				
				
				if(listtype.startsWith("list_team")){
					JSONArray filters = PrintAgent.getFilters("list_team",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append("         SELECT DISTINCT ");
					//sb.append("         A.ID as ACT_ID, ");
					sb.append("         U.ID as user_id, ");
					sb.append("         FIRST_NAME + ' ' + LAST_NAME   as team_name , ");
					sb.append("         U.USERNAME   as team_username , ");
					sb.append("         U.EMAIL as team_email, ");
					
					sb.append("        type as team_type  , ");
					sb.append("         dept as team_department       ");
					sb.append("                         FROM PROJECT A ");
					
					sb.append("                         LEFT OUTER JOIN   REF_PROJECT_TEAM RAUP on A.ID=RAUP.PROJECT_ID  AND RAUP.ACTIVE='Y'    ");
					sb.append("                         join REF_TEAM RU on RAUP.REF_TEAM_ID=RU.ID                     ");
					
					
					sb.append("                         left outer join LKUP_TEAM_TYPE as LUT on RU.LKUP_TEAM_TYPE_ID=LUT.ID     ");
					sb.append("                         JOIN USERS AS U ON RU.USERS_ID = U.ID    ");
					sb.append("                        LEFT OUTER JOIN (      STAFF AS S      LEFT OUTER JOIN DEPARTMENT AS D ON S.DEPARTMENT_ID = D.ID    ) ON U.ID = S.USERS_ID AND S.ACTIVE = 'Y' ");
					sb.append("                         WHERE A.ID = MAIN_ID ");
					
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					sb.append("                       ");
					sb.append("                         FOR JSON PATH, INCLUDE_NULL_VALUES  )as '").append(listtype).append("' ");
				}
				
				
				if(listtype.startsWith("list_summary_people")){
					JSONArray filters = PrintAgent.getFilters("list_summary_people",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append("         SELECT ");
					//sb.append("         A.ID as ACT_ID, ");
					sb.append("         U.ID as user_id, ");
					sb.append("         P.ID as people_id, ");
					sb.append("         FIRST_NAME + ' ' + LAST_NAME   as people_name , ");
					sb.append("         U.USERNAME   as people_username , ");
					sb.append("         U.EMAIL as people_email, ");
					
					sb.append("         P.CITY as people_city, ");
					sb.append("         P.STATE as people_state, ");
					sb.append("         P.ZIP as people_zip, ");
					sb.append("         P.ZIP4 as people_zip4, ");
					
					sb.append("         P.PHONE_HOME as people_phone_home, ");
					sb.append("         P.FAX as people_fax, ");
					sb.append("         P.COMMENTS as people_comments, ");
					
					sb.append("         P.PHONE_WORK as people_phone_work, ");
					sb.append("         P.PHONE_CELL as people_phone_cell, ");
					//sb.append(" RN.ACTIVITY_FEE+ CASE WHEN RY.ACTIVITY_FEE IS NULL THEN 0 ELSE RY.ACTIVITY_FEE END as finance_activity_fee , ");
					sb.append("        PRIMARY_CONTACT as people_primary_contact, ");
					
					sb.append("         FIRST_NAME as people_first_name , ");
					sb.append("         MI as people_mi, ");
					sb.append("         LAST_NAME people_last_name, ");
					sb.append("         LUT.TYPE as people_type , ");
				
					sb.append("         ADDRESS as people_address ,");
					
					sb.append(" ACT_NBR as activity_act_nbr , ");
					sb.append(" LAT.TYPE as activity_type , ");
					sb.append(" LAS.DESCRIPTION as activity_status , ");
					sb.append(" A.DESCRIPTION as activity_description ");
					
					
					//	sb.append("         , ");
					//	sb.append("         PL.LIC_NUM as people_lic_num, ");
					//	sb.append("         CONVERT(VARCHAR(10), PL.LIC_EXPIRATION_DATE, 101) as people_lic_expiration_date, ");
					//	sb.append("         LPLT.TYPE as people_license_type ");
					sb.append("                         FROM ACTIVITY A ");
					
					sb.append("                        join REF_ACT_USERS RAU on A.ID=RAU.ACTIVITY_ID    AND RAU.ACTIVE='Y'   ");
					sb.append("                         join REF_USERS RU on RAU.REF_USERS_ID=RU.ID  ");
					sb.append("                         left outer join LKUP_USERS_TYPE as LUT on RU.LKUP_USERS_TYPE_ID=LUT.ID   ");
					sb.append("                         JOIN USERS AS U ON RU.USERS_ID = U.ID  ");
					sb.append("                         left outer join PEOPLE P on U.ID = P.USERS_ID AND P.ACTIVE='Y' ");
					
					sb.append(" left outer join LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID=LAT.ID  ");
					sb.append(" LEFT OUTER JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID=LAS.ID ");
					
					
				//	sb.append("                         left outer join PEOPLE_LICENSE as PL on P.ID=PL.PEOPLE_ID ");
				//	sb.append("                         left outer join LKUP_PEOPLE_LICENSE_TYPE as LPLT on PL.LKUP_PEOPLE_LICENSE_TYPE_ID=LPLT.ID ");
					sb.append("                         WHERE A.PROJECT_ID = MAIN_ID ");
					
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					sb.append("                       ");
					sb.append("                         FOR JSON PATH, INCLUDE_NULL_VALUES  )as '").append(listtype).append("' ");
				}
				
				
				//list fee
				/*if(listtype.startsWith("list_fee")){
					sb.append(" , ");
					sb.append("  (      ");
					sb.append(" select FG.GROUP_NAME fee_group_name ,F.NAME as fee_name, SD.FEE_VALUATION as fee_FEE_VALUATION,SD.FEE_AMOUNT as fee_amount ");
					sb.append(" ,SD.FEE_PAID as fee_paid , SD.BALANCE_DUE as fee_balance from ").append(maintableref).append(" A  ");
					sb.append(" JOIN REF_").append(tableref).append("_STATEMENT RAS on A.ID= RAS.").append(idref).append("  ");
					sb.append(" JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y'  ");
					sb.append(" JOIN FEE_GROUP FG on SD.GROUP_ID=FG.ID  ");
					sb.append(" JOIN FEE F on SD.FEE_ID= F.ID  ");
					sb.append("  join Q on A.ID=Q.MAIN_ID ");
					sb.append("                       ");
					sb.append(" FOR JSON PATH )as list_fee ");
				}*/
				if(listtype.startsWith("list_fee")){
					JSONArray filters = PrintAgent.getFilters("list_fee",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append(" select FG.GROUP_NAME as fee_group_name ,F.NAME as fee_name,  ");
					//sb.append(" FORMAT(SD.FEE_VALUATION, 'C', 'en-us') as fee_valuation , ");
					sb.append(" FORMAT(SD.FEE_AMOUNT, 'C', 'en-us') as fee_amount , ");
					sb.append(" FORMAT(SD.FEE_PAID, 'C', 'en-us') as fee_paid , ");
					sb.append(" FORMAT(SD.BALANCE_DUE, 'C', 'en-us') as fee_balance , ");
					sb.append(" input1 as fee_input1,input2 as fee_input2 ,input3 as fee_input3,input4 as fee_input4,input5 as fee_input5 ");
					sb.append(",");
					sb.append(" CONVERT(VARCHAR(10),SD.FEE_DATE,101) as fee_date ");
					sb.append(",");
					sb.append(" FG.ID as fee_group_id ");
					sb.append(",");
					sb.append(" F.ID as fee_id ");
					sb.append(",");
					sb.append(" CONVERT(VARCHAR(10),SD.UPDATED_DATE,101) as fee_updated_date ");
					sb.append(",");
					sb.append(" CONVERT(VARCHAR(10),SD.CREATED_DATE,101) as fee_created_date ");
					sb.append(" , ");
					sb.append(" CU.USERNAME AS fee_created ");
					sb.append(" , ");
					sb.append(" UP.USERNAME AS fee_updated ");
					
					
					sb.append(" from ").append(maintableref).append(" A  ");
					sb.append(" JOIN REF_").append(tableref).append("_STATEMENT RAS on A.ID= RAS.").append(idref).append("  ");
					sb.append(" JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y'  ");
					sb.append(" JOIN FEE_GROUP FG on SD.GROUP_ID=FG.ID  ");
					sb.append(" JOIN FEE F on SD.FEE_ID= F.ID  ");
					sb.append(" LEFT OUTER JOIN USERS AS CU ON SD.CREATED_BY = CU.ID ");
					sb.append(" LEFT OUTER JOIN USERS UP ON SD.UPDATED_BY = UP.ID  ");
					sb.append("  WHERE  A.ID =Q.MAIN_ID ");
					sb.append("                       ");
					//sb.append(" FOR JSON PATH )as list_fee ");
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					sb.append(" ORDER BY FG.GROUP_NAME,SD.UPDATED_DATE  ");
					
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'            ");
					sb.append("                     ");
			
			}
				
				
				//list finance 
				if(listtype.equalsIgnoreCase("list_finance")){
					sb.append(" , ");
					sb.append("  (      ");
					
					
					sb.append(" select GROUP_ID as group_id , GROUP_NAME as group_name , ");
					
					sb.append(" FORMAT(PERMIT_FEE, 'C', 'en-us') as activity_fee , ");
					sb.append(" FORMAT(REVIEW_FEE, 'C', 'en-us') as review_fee , ");
					sb.append(" FORMAT(FEE_PAID, 'C', 'en-us')  as  paid,    ");
					sb.append(" FORMAT(BALANCE_DUE, 'C', 'en-us')  as  balance  ");
					sb.append(" from CALC  WHERE FEE_AMOUNT >0  ");
				
					sb.append(" FOR JSON PATH, INCLUDE_NULL_VALUES )as list_finance ");
					
				}
				
				if(listtype.equalsIgnoreCase("list_finance_total")){
					sb.append(" , ");
					sb.append("  (      ");
					
					sb.append(" select 0 as total_group_id , 'TOTAL'  as total_group_name , ");
					
					sb.append(" FORMAT(PERMIT_FEE, 'C', 'en-us') as total_activity_fee , ");
					sb.append(" FORMAT(REVIEW_FEE, 'C', 'en-us') as total_review_fee , ");
					sb.append(" FORMAT(FEE_PAID, 'C', 'en-us')  as  total_paid,    ");
					sb.append(" FORMAT(BALANCE_DUE, 'C', 'en-us')  as  total_balance  ");
					sb.append(" from TOTAL    ");
					sb.append(" FOR JSON PATH, INCLUDE_NULL_VALUES ) as list_finance_total ");
					
					
					
				}
				
				
				//list notes
				if(listtype.startsWith("list_notes")){
						JSONArray filters = PrintAgent.getFilters("list_notes",listtype);
						sb.append(" , ");
						sb.append("         (      ");
						sb.append(" SELECT ");
						sb.append(" N.ID note_id");
						sb.append(" , ");
						sb.append(" N.NOTE as note_note ");
						sb.append(" , ");
						sb.append(" UP.USERNAME AS note_updated ");
						sb.append(" , ");
						sb.append(" CONVERT(VARCHAR(10), N.CREATED_DATE, 101)  AS note_created_date");
						sb.append(" , ");
						sb.append(" CONVERT(VARCHAR(10), N.UPDATED_DATE, 101)  AS note_updated_date");
						sb.append(" , ");
						sb.append(" N.LKUP_NOTES_TYPE_ID note_lkup_notes_type_id");
						sb.append(" , ");
						sb.append(" T.DESCRIPTION as  note_type ");
						
						sb.append(" FROM ");
						sb.append(" REF_").append(tableref).append("_NOTES AS R ");
					
						sb.append(" JOIN NOTES AS N ON R.NOTES_ID = N.ID AND N.ACTIVE = 'Y' AND R.ACTIVE = 'Y' ");
						sb.append(" JOIN LKUP_NOTES_TYPE AS T ON N.LKUP_NOTES_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
						sb.append(" LEFT OUTER JOIN USERS AS CU ON N.CREATED_BY = CU.ID ");
						sb.append(" LEFT OUTER JOIN USERS UP ON N.UPDATED_BY = UP.ID  ");
						sb.append("  WHERE  R.PROJECT_ID =Q.MAIN_ID ");
						if(filters.length()>0){
							for(int f=0;f<filters.length();f++){
								JSONObject fl = filters.getJSONObject(f);
								if(fl.has("filter") && fl.has("filterValue")){
									String val = Operator.sqlEscape(fl.getString("filterValue"));
									String[] v = Operator.split(fl.getString("filterValue"),",");
									if(v.length>0){
										if(Operator.toInt(v[0])<=0){
											val = "'"+Operator.replace(val, ",", "','") +"'";
										}
									}
									sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
								}
							
							}
							
						}
						
						sb.append(" ORDER BY N.CREATED_DATE  ");
						
						sb.append("                       ");
						sb.append("                         FOR JSON PATH )as '").append(listtype).append("'            ");
						sb.append("                     ");
				
				}
				
				
				if(listtype.startsWith("list_summary_notes")){
					JSONArray filters = PrintAgent.getFilters("list_summary_notes",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append(" SELECT ");
					sb.append(" N.ID note_id");
					sb.append(" , ");
					sb.append(" N.NOTE as note_note ");
					sb.append(" , ");
					sb.append(" UP.USERNAME AS note_updated ");
					sb.append(" , ");
					sb.append(" CONVERT(VARCHAR(10), N.CREATED_DATE, 101)  AS note_created_date");
					sb.append(" , ");
					sb.append(" CONVERT(VARCHAR(10), N.UPDATED_DATE, 101)  AS note_updated_date");
					sb.append(" , ");
					sb.append(" N.LKUP_NOTES_TYPE_ID note_lkup_notes_type_id");
					sb.append(" , ");
					sb.append(" T.DESCRIPTION as  note_type ");
					
					sb.append(" , ");
					sb.append(" A.ACT_NBR as  activity_act_nbr, ");
					sb.append(" LAT.TYPE as activity_type , ");
					sb.append(" LAS.DESCRIPTION as activity_status , ");
					sb.append(" A.DESCRIPTION as activity_description ");
					
					
					sb.append(" FROM ");
					sb.append(" REF_ACT_NOTES AS R ");
					sb.append(" join ACTIVITY A on R.ACTIVITY_ID=A.ID ");
					sb.append(" left outer join LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID=LAT.ID  ");
					sb.append(" left outer join DEPARTMENT D ON LAT.DEPARTMENT_ID=D.ID  ");
					sb.append(" LEFT OUTER JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID=LAS.ID ");
					sb.append(" JOIN NOTES AS N ON R.NOTES_ID = N.ID AND N.ACTIVE = 'Y' AND R.ACTIVE = 'Y' ");
					sb.append(" JOIN LKUP_NOTES_TYPE AS T ON N.LKUP_NOTES_TYPE_ID = T.ID AND T.ACTIVE = 'Y' ");
					sb.append(" LEFT OUTER JOIN USERS AS CU ON N.CREATED_BY = CU.ID ");
					sb.append(" LEFT OUTER JOIN USERS UP ON N.UPDATED_BY = UP.ID  ");
					sb.append("  WHERE  A.PROJECT_ID =Q.MAIN_ID ");
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					sb.append(" ORDER BY N.CREATED_DATE  ");
					
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'            ");
					sb.append("                     ");
			
			}
				
				if(listtype.startsWith("list_library")){
					JSONArray filters = PrintAgent.getFilters("list_library",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append(" SELECT ");
					sb.append(" R.ID as library_id");
					sb.append(" , ");
					sb.append(" R.TITLE as library_title ");
					sb.append(" , ");
					sb.append(" R.TXT as library_txt ");
					sb.append(" , ");
					sb.append(" R.LIBRARY_CODE as library_code ");
					
					sb.append(" , ");
					sb.append(" R.INSPECTABLE as library_inspectable ");
					sb.append(" , ");
					sb.append(" R.WARNING as library_warning ");
					sb.append(" , ");
					sb.append(" R.COMPLETE as library_complete ");
					sb.append(" , ");
					sb.append(" UP.USERNAME AS library_updated ");
					sb.append(" , ");
					sb.append(" CONVERT(VARCHAR(10),R.UPDATED_DATE,101) as library_updated_date ");
			
					sb.append(" FROM ");
					sb.append(" REF_").append(tableref).append("_LIBRARY AS R ");
				
					sb.append(" LEFT OUTER JOIN USERS AS CU ON R.CREATED_BY = CU.ID ");
					sb.append(" LEFT OUTER JOIN USERS UP ON R.UPDATED_BY = UP.ID  ");
					sb.append("  WHERE  R.PROJECT_ID =Q.MAIN_ID  AND R.ACTIVE='Y' ");
					
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}

					
					sb.append(" ORDER BY R.UPDATED_DATE DESC ");
					
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'                ");
					sb.append("                     ");
			
				}
				
				
				
				if(listtype.startsWith("list_attachments")){
					JSONArray filters = PrintAgent.getFilters("list_attachments",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append(" SELECT ");
					sb.append(" ATT.ID attachment_id");
					sb.append(" , ");
					sb.append(" ATT.TITLE as attachment_title ");
					sb.append(" , ");
					sb.append(" ATT.DESCRIPTION as attachment_description ");
					sb.append(" , ");
					sb.append(" ATT.PATH as attachment_path ");
					sb.append(" , ");
					sb.append(" ATT.LOCATION as attachment_location ");
					sb.append(" , ");
					sb.append(" LAT.TYPE as attachment_type ");
					
					sb.append(" , ");
					sb.append(" UP.USERNAME AS attachment_updated ");
					sb.append(" , ");
					sb.append(" CONVERT(VARCHAR(10),R.UPDATED_DATE,101) as attachment_updated_date ");
			
					sb.append(" FROM ");
					sb.append(" ATTACHMENTS AS ATT ");
					sb.append(" JOIN REF_").append(tableref).append("_ATTACHMENTS AS R on ATT.ID= R.ATTACHMENT_ID ");
					sb.append(" LEFT OUTER JOIN LKUP_ATTACHMENTS_TYPE AS LAT ON ATT.LKUP_ATTACHMENTS_TYPE_ID = LAT.ID ");
					sb.append(" LEFT OUTER JOIN USERS AS CU ON R.CREATED_BY = CU.ID ");
					sb.append(" LEFT OUTER JOIN USERS UP ON R.UPDATED_BY = UP.ID  ");
					sb.append("  WHERE  R.PROJECT_ID =Q.MAIN_ID  AND R.ACTIVE='Y' ");
					
					
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}

					
					
					sb.append(" ORDER BY R.UPDATED_DATE DESC ");
					
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'                ");
					sb.append("                     ");
			
				}
				
				if(listtype.startsWith("list_holds")){
					JSONArray filters = PrintAgent.getFilters("list_holds",listtype);
					sb.append(" , ");
					sb.append("         (      ");
					sb.append(" SELECT ");
					sb.append(" R.ID as hold_id");
					
					sb.append(" ,");
					sb.append(" R.DESCRIPTION as hold_description ");
					sb.append(" ,");
					sb.append(" R.LKUP_HOLDS_TYPE_ID as hold_lkup_holds_type_id ");
					sb.append(" ,");
					sb.append(" R.LKUP_HOLDS_STATUS_ID as hold_lkup_holds_status_id ");
					sb.append(" , ");
					sb.append(" H.TYPE AS hold_type ");
					sb.append(" , ");
					sb.append(" S.DESCRIPTION AS hold_status ");
					sb.append(" , ");
					sb.append(" CU.USERNAME AS hold_created ");
					sb.append(" , ");
					sb.append(" UP.USERNAME AS hold_updated ");
					sb.append(" , ");
					sb.append(" CONVERT(VARCHAR(10),R.UPDATED_DATE,101) as hold_updated_date ");
			
					sb.append(" FROM ");
					sb.append(" REF_").append(tableref).append("_HOLDS AS R ");
					sb.append(" LEFT OUTER JOIN LKUP_HOLDS_TYPE H ON R.LKUP_HOLDS_TYPE_ID = H.ID ");
					sb.append(" LEFT OUTER JOIN LKUP_HOLDS_STATUS S ON R.LKUP_HOLDS_STATUS_ID = S.ID ");
					sb.append(" LEFT OUTER JOIN USERS CU ON R.CREATED_BY = CU.ID ");
					sb.append(" LEFT OUTER JOIN USERS UP ON R.UPDATED_BY = UP.ID  ");
					sb.append("  WHERE  R.PROJECT_ID =Q.MAIN_ID  AND R.ACTIVE='Y' ");
					

					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					
					
					
					sb.append(" ORDER BY R.UPDATED_DATE DESC ");
					
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'                ");
					sb.append("                     ");
			
				}
				
				
				
				
				if(listtype.startsWith("list_reviews")){
					JSONArray filters = PrintAgent.getFilters("list_reviews",listtype);
			
					sb.append(" , ");
					sb.append(" (      ");
					sb.append(" select RG.GROUP_NAME AS review_group_name, ");
					sb.append(" C.TITLE AS review_sub_title, ");
					sb.append(" CONVERT(VARCHAR(10),C.START_DATE,101) as review_sub_date, ");
				
					sb.append(" R.NAME AS review_name, ");
					sb.append(" R.DESCRIPTION AS review_description, ");
					sb.append(" CONVERT(VARCHAR(10),RCA.DATE,101) as review_date, ");
					sb.append(" RCA.review_comments as review_comment, ");
					sb.append(" LRT.TYPE as review_type,  ");
					sb.append(" LRS.STATUS  as review_status, ");
					sb.append(" LTT.TYPE AS review_team_type, ");
					sb.append(" U.USERNAME AS review_user, ");
					sb.append(" CONVERT(VARCHAR(10),RCA.UPDATED_DATE,101) as review_updated_date, ");
					sb.append(" CONVERT(VARCHAR(50),RCA.UPDATED_DATE,109) as review_updated_date_time, ");
					
					sb.append(" ATT.TITLE as review_attachment_title ");
					sb.append(" , ");
					sb.append(" ATT.DESCRIPTION as review_attachment_description ");
					sb.append(" , ");
					sb.append(" ATT.PATH as review_attachment_path ");
					
					sb.append("  ");
					sb.append(" from  ");
					sb.append(" PROJECT A  ");
					//sb.append("  join Q on A.ID =Q.MAIN_ID ");
					sb.append(" join REF_PROJECT_COMBOREVIEW RAC on A.ID= RAC.PROJECT_ID  ");
					sb.append(" join COMBOREVIEW C on RAC.COMBOREVIEW_ID= C.ID ");
					sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RR ON C.ID = RR.COMBOREVIEW_ID AND RR.ACTIVE = 'Y'  ");
					sb.append(" JOIN REF_COMBOREVIEW_ACTION AS RCA ON RR.ID = RCA.REF_COMBOREVIEW_REVIEW_ID AND RCA.ACTIVE = 'Y'  ");
					sb.append(" JOIN REVIEW AS R ON RR.REVIEW_ID = R.ID ");
					sb.append(" JOIN LKUP_REVIEW_STATUS LRS on RCA.LKUP_REVIEW_STATUS_ID= LRS.ID  ");
					sb.append(" JOIN LKUP_REVIEW_TYPE LRT on R.LKUP_REVIEW_TYPE_ID = LRT.ID ");
					sb.append(" join REVIEW_GROUP RG on R.REVIEW_GROUP_ID= RG.ID ");
					sb.append(" left outer join REF_COMBOREVIEW_TEAM RCRT on RCA.ID= REF_COMBOREVIEW_ACTION_ID AND RR.ID =RCRT.REF_COMBOREVIEW_REVIEW_ID   ");
					sb.append(" left outer join REF_TEAM RT on RCRT.REF_TEAM_ID= RT.ID ");
					sb.append(" left outer join LKUP_TEAM_TYPE LTT on RT.LKUP_TEAM_TYPE_ID= LTT.ID ");
					sb.append(" left outer join USERS U on RCA.UPDATED_BY= U.ID ");
					
					sb.append(" left outer join REF_COMBOREVIEW_ATTACHMENTS RCAT on RCA.ID= RCAT.ID AND RCAT.ACTIVE='Y'   ");
					sb.append(" left outer join ATTACHMENTS AS ATT on RCAT.ATTACHMENT_ID =ATT.ID  AND ATT.ACTIVE='Y' ");
					
					
					sb.append("  WHERE  A.ID =Q.MAIN_ID ");
					//sb.append(" where A.ID in (132759,132760,1101428)   ");
					//sb.append(" --AND RG.ID=8 ");
					//sb.append(" --group by RG.GROUP_NAME,R.NAME,R.DESCRIPTION,RCA.DATE,RCA.REVIEW_COMMENTS,LRT.TYPE, LRS.STATUS,LTT.TYPE,USERNAME ");

					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(R.").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					sb.append(" order by GROUP_NAME,C.START_DATE, RCA.DATE ");
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'     ");
					sb.append("   ");
				}
				
				
				if(listtype.startsWith("list_summary_reviews")){
					JSONArray filters = PrintAgent.getFilters("list_summary_reviews",listtype);
			
					sb.append(" , ");
					sb.append(" (      ");
					sb.append(" select RG.GROUP_NAME AS review_group_name, ");
					sb.append(" C.TITLE AS review_sub_title, ");
					sb.append(" CONVERT(VARCHAR(10),C.START_DATE,101) as review_sub_date, ");
				
					sb.append(" R.NAME AS review_name, ");
					sb.append(" R.DESCRIPTION AS review_description, ");
					sb.append(" CONVERT(VARCHAR(10),RCA.DATE,101) as review_date, ");
					sb.append(" RCA.review_comments as review_comment, ");
					sb.append(" LRT.TYPE as review_type,  ");
					sb.append(" LRS.STATUS  as review_status, ");
					sb.append(" LTT.TYPE AS review_team_type, ");
					
					sb.append(" U.USERNAME AS review_user, ");
					sb.append(" CONVERT(VARCHAR(10),RCA.UPDATED_DATE,101) as review_updated_date, ");
					sb.append(" CONVERT(VARCHAR(50),RCA.UPDATED_DATE,109) as review_updated_date_time, ");
					
					sb.append("  CONVERT(VARCHAR(10),APPTS.START_DATE,101) +' '+ CONVERT(VARCHAR(5),APPTS.START_DATE,108)  as review_appointment_start_date ");
					sb.append(" , ");
					
					sb.append(" CONVERT(VARCHAR(10),APPTS.END_DATE,101) +' '+ CONVERT(VARCHAR(5),APPTS.END_DATE,108)  as review_appointment_end_date ");
					sb.append(" , ");
					
					sb.append(" ATT.TITLE as review_attachment_title ");
					sb.append(" , ");
					sb.append(" ATT.DESCRIPTION as review_attachment_description ");
					sb.append(" , ");
					sb.append(" ATT.PATH as review_attachment_path ");
					sb.append(" , ");
					sb.append(" A.ACT_NBR as  activity_act_nbr, ");
					sb.append(" LAT.TYPE as activity_type , ");
					sb.append(" LAS.DESCRIPTION as activity_status , ");
					sb.append(" A.DESCRIPTION as activity_description ");
					
					
					sb.append("  ");
					sb.append(" from  ");
					sb.append(" ACTIVITY A  ");
					//sb.append("  join Q on A.ID =Q.MAIN_ID ");
					sb.append(" join REF_ACT_COMBOREVIEW RAC on A.ID= RAC.ACTIVITY_ID     ");
					sb.append(" join COMBOREVIEW C on RAC.COMBOREVIEW_ID= C.ID ");
					sb.append(" JOIN REF_COMBOREVIEW_REVIEW AS RR ON C.ID = RR.COMBOREVIEW_ID AND RR.ACTIVE = 'Y'  ");
					sb.append(" JOIN REF_COMBOREVIEW_ACTION AS RCA ON RR.ID = RCA.REF_COMBOREVIEW_REVIEW_ID AND RCA.ACTIVE = 'Y'  ");
					sb.append(" JOIN REVIEW AS R ON RR.REVIEW_ID = R.ID ");
					sb.append(" JOIN LKUP_REVIEW_STATUS LRS on RCA.LKUP_REVIEW_STATUS_ID= LRS.ID  ");
					sb.append(" JOIN LKUP_REVIEW_TYPE LRT on R.LKUP_REVIEW_TYPE_ID = LRT.ID ");
					sb.append(" join REVIEW_GROUP RG on R.REVIEW_GROUP_ID= RG.ID ");
					sb.append(" left outer join REF_COMBOREVIEW_TEAM RCRT on RCA.ID= REF_COMBOREVIEW_ACTION_ID AND RR.ID =RCRT.REF_COMBOREVIEW_REVIEW_ID   ");
					sb.append(" left outer join REF_TEAM RT on RCRT.REF_TEAM_ID= RT.ID ");
					sb.append(" left outer join LKUP_TEAM_TYPE LTT on RT.LKUP_TEAM_TYPE_ID= LTT.ID ");
					sb.append(" left outer join USERS U on RCA.UPDATED_BY= U.ID ");
					
					sb.append(" left outer join REF_COMBOREVIEW_ATTACHMENTS RCAT on RCA.ID= RCAT.ID AND RCAT.ACTIVE='Y'   ");
					sb.append(" left outer join ATTACHMENTS AS ATT on RCAT.ATTACHMENT_ID =ATT.ID  AND ATT.ACTIVE='Y' ");
					sb.append(" left outer join LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID=LAT.ID  ");
					sb.append(" left outer join DEPARTMENT D ON LAT.DEPARTMENT_ID=D.ID  ");
					sb.append(" LEFT OUTER JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID=LAS.ID ");
					sb.append(" LEFT OUTER JOIN APPOINTMENT_SCHEDULE APPTS on RCA.ID=APPTS. REF_COMBOREVIEW_ACTION_ID ");
					sb.append("  WHERE  A.PROJECT_ID =Q.MAIN_ID ");
					//sb.append(" where A.ID in (132759,132760,1101428)   ");
					//sb.append(" --AND RG.ID=8 ");
					//sb.append(" --group by RG.GROUP_NAME,R.NAME,R.DESCRIPTION,RCA.DATE,RCA.REVIEW_COMMENTS,LRT.TYPE, LRS.STATUS,LTT.TYPE,USERNAME ");

					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(R.").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					sb.append(" order by  RCA.DATE ");
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'     ");
					sb.append("   ");
				}
				
				
				if(listtype.startsWith("list_summary_appointments")){
					JSONArray filters = PrintAgent.getFilters("list_summary_appointments",listtype);
			
					sb.append(" , ");
					sb.append(" (      ");
					sb.append(" select * from APPT ");
					sb.append("   ");
					
					
					sb.append("  WHERE  APPT.ID>0 ");
					
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					sb.append(" ORDER BY  activity_act_nbr,SCHEDULED ASC, appointment_start_date ASC, appointment_end_date ASC, CREATED_DATE ASC  ");
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'     ");
					sb.append("   ");
				}
				
				
				/*if(listtype.startsWith("list_summary_activities")){
					JSONArray filters = PrintAgent.getFilters("list_summary_activities",listtype);
			
					sb.append(" , ");
					sb.append(" (      ");
				
					
					sb.append(" WITH F AS (  select     A.ID,'TOTAL' as GROUPNAME,0 as GROUP_ID,  SUM(SD.FEE_AMOUNT) as AMOUNT , SUM(SD.FEE_PAID) as PAID,SUM(SD.BALANCE_DUE) as BALANCE   ");
					sb.append("  from ACTIVITY A    ");
					sb.append("  join LKUP_ACT_STATUS AS ST ON A.LKUP_ACT_STATUS_ID = ST.ID  AND ST.EXPIRED = 'N'  ");
					sb.append("  AND COALESCE(CAST(A.EXP_DATE AS DATE), getDate()+1) > getDate()  join REF_ACT_STATEMENT RAS on A.ID=RAS.ACTIVITY_ID     ");
					sb.append("  join STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y'     ");
					sb.append("  join STATEMENT_DETAIL SD on  S.ID=SD.STATEMENT_ID   AND SD.ACTIVE='Y' ");
					sb.append("  join FEE_GROUP FG   on SD.GROUP_ID=FG.ID  where  A.PROJECT_ID = 216416  group by A.ID  )   ");
					sb.append("  SELECT   ");
					sb.append("  A.ID,   A.ID AS LINKID,   'activity' AS LINKTYPE,   ");
					sb.append("  A.ACT_NBR as 'activity_act_nbr',   ATT.DESCRIPTION AS activity_type,    LAS.STATUS as activity_status,     ");
					sb.append("  CU.USERNAME AS CREATED,    UP.USERNAME as UPDATED,    ");
					sb.append("  F.AMOUNT ,    F.PAID,    F.BALANCE   ");
					sb.append("  FROM    ACTIVITY A     ");
					sb.append("  JOIN  LKUP_ACT_TYPE ATT ON A.LKUP_ACT_TYPE_ID = ATT.ID    JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID = LAS.ID  AND LAS.EXPIRED = 'N'   ");
					sb.append("  AND COALESCE(CAST(A.EXP_DATE AS DATE), getDate()+1) > getDate()     ");
					sb.append("  LEFT OUTER JOIN USERS CU on A.CREATED_BY = CU.ID 	   ");
					sb.append("  LEFT OUTER JOIN USERS UP on A.UPDATED_BY = UP.ID 	  ");
					sb.append("  LEFT OUTER JOIN F on A.ID = F.ID  ");
						
					
					sb.append("  WHERE  A.PROJECT_ID =Q.MAIN_ID    AND    A.ACTIVE = 'Y'  ");
					//sb.append(" where A.ID in (132759,132760,1101428)   ");
					//sb.append(" --AND RG.ID=8 ");
					//sb.append(" --group by RG.GROUP_NAME,R.NAME,R.DESCRIPTION,RCA.DATE,RCA.REVIEW_COMMENTS,LRT.TYPE, LRS.STATUS,LTT.TYPE,USERNAME ");

					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					sb.append(" order by GROUP_NAME,C.START_DATE, RCA.DATE ");
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'     ");
					sb.append("   ");
				}*/
				
				
				
				if(listtype.startsWith("list_appointments")){
					JSONArray filters = PrintAgent.getFilters("list_appointments",listtype);
			
					sb.append(" , ");
					sb.append(" (      ");
					sb.append(" SELECT  A.ID as appointment_id  ,   ");
					sb.append(" CASE WHEN RMG.ID IS NOT NULL THEN RMG.ID ELSE T.ID END AS appointment_type  ,   ");
					sb.append(" CASE WHEN RMG.GROUP_NAME IS NOT NULL THEN RMG.GROUP_NAME ELSE T.TYPE END AS appointment_type_text  ,   ");
					sb.append(" CASE WHEN RM.NAME IS NOT NULL THEN RM.NAME ELSE A.SUBJECT END AS appointment_subject  ,   ");
					sb.append(" CASE WHEN RM.NAME IS NOT NULL THEN RM.NAME ELSE A.SUBJECT END AS appointment_subject_text  ,   ");
					sb.append(" S.START_DATE as appointment_start_date  ,  S.END_DATE as appointment_end_date ,  ");
					sb.append(" CASE WHEN NXTS.STATUS IS NOT NULL THEN NXTS.STATUS  ");
					sb.append(" WHEN CRS.STATUS IS NOT NULL THEN CRS.STATUS WHEN S.END_DATE < getDate() THEN 'EXPIRED' ELSE ST.STATUS END AS appointment_status  ,   ");
					sb.append(" CASE WHEN NXTS.STATUS IS NOT NULL THEN 'Y' WHEN ST.COMPLETE = 'Y' THEN 'Y' ELSE 'N' END AS appointment_final  ,   ");
					sb.append(" CASE WHEN S.REF_COMBOREVIEW_ACTION_ID > 0 AND NXTS.STATUS IS NULL THEN 'Y' WHEN ST.COMPLETE = 'N' THEN 'Y' ELSE 'N' END AS appointment_scheduled  ,  ");
					sb.append(" CASE WHEN S.REF_COMBOREVIEW_ACTION_ID > 0 AND NXTS.STATUS IS NULL THEN 'Y'  ");
					sb.append(" WHEN ST.COMPLETE = 'Y' THEN 'N' WHEN S.END_DATE < getDate() THEN 'N' ELSE 'Y' END AS appointment_isactive  ,  S.ID AS SCHEDULE_ID  ,  ");
					sb.append(" RMR.ID AS REF_COMBOREVIEW_REVIEW_ID  ,  CRA.ID AS REF_COMBOREVIEW_ACTION_ID  ,  RMR.COMBOREVIEW_ID  ,   ");
					sb.append(" RM.ID AS REVIEW_ID  ,  RM.NAME AS REVIEW  ,  RMG.ID AS REVIEW_GROUP_ID  ,  RMG.GROUP_NAME AS REVIEW_GROUP  ,  S.CREATED_DATE  ,  ");
					sb.append(" COUNT(DISTINCT U.ID) AS appointment_collaborators  ");
					sb.append(" FROM  REF_PROJECT_APPOINTMENT AS R   ");
					sb.append(" JOIN APPOINTMENT AS A ON R.APPOINTMENT_ID = A.ID   ");
					sb.append(" JOIN APPOINTMENT_SCHEDULE AS S ON A.ID = S.APPOINTMENT_ID AND S.ACTIVE = 'Y'   ");
					sb.append(" JOIN LKUP_APPOINTMENT_STATUS AS ST ON S.LKUP_APPOINTMENT_STATUS_ID = ST.ID   ");
					sb.append(" LEFT OUTER JOIN LKUP_APPOINTMENT_TYPE AS T ON A.LKUP_APPOINTMENT_TYPE_ID > 0 AND A.LKUP_APPOINTMENT_TYPE_ID = T.ID   ");
					sb.append(" LEFT OUTER JOIN (    REF_COMBOREVIEW_REVIEW AS RMR    JOIN REVIEW AS RM ON RMR.REVIEW_ID = RM.ID AND RM.ACTIVE = 'Y'    ");
					sb.append(" JOIN REVIEW_GROUP AS RMG ON RM.REVIEW_GROUP_ID = RMG.ID AND RMG.ACTIVE = 'Y'  ) ON RMR.ID = A.REF_COMBOREVIEW_REVIEW_ID  ");
					sb.append(" AND RMR.ACTIVE = 'Y'  LEFT OUTER JOIN (    REF_COMBOREVIEW_ACTION AS CRA     ");
					sb.append(" JOIN LKUP_REVIEW_STATUS AS CRS ON CRA.LKUP_REVIEW_STATUS_ID = CRS.ID     ");
					sb.append(" LEFT OUTER JOIN (      REF_COMBOREVIEW_ACTION AS NXTA       ");
					sb.append(" JOIN LKUP_REVIEW_STATUS AS NXTS ON NXTA.LKUP_REVIEW_STATUS_ID = NXTS.ID AND NXTS.ACTIVE = 'Y'  ");
					sb.append(" AND NXTA.ACTIVE = 'Y'    ) ON NXTA.PREVIOUS_ID = CRA.ID  ) ON S.REF_COMBOREVIEW_ACTION_ID = CRA.ID   ");
					sb.append(" LEFT OUTER JOIN (    REF_APPOINTMENT_USERS AS RAU     ");
					sb.append(" JOIN USERS AS U ON RAU.USERS_ID = U.ID AND U.ACTIVE = 'Y' AND RAU.ACTIVE = 'Y'  )  ON A.ID = RAU.APPOINTMENT_ID  ");
					sb.append("  WHERE  R.PROJECT_ID =Q.MAIN_ID  AND  ST.SCHEDULED = 'Y'  AND  A.ACTIVE = 'Y'  AND  R.ACTIVE = 'Y'  ");
			

					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					sb.append(" GROUP BY  S.ID  ,  A.ID  ,  RMG.ID  ,  T.ID  ,  RMG.GROUP_NAME  ,  T.TYPE  ,  RM.NAME  ,  A.SUBJECT  ,  S.START_DATE  ,  ");
					sb.append(" S.END_DATE  ,  NXTS.STATUS  ,  CRS.STATUS  ,  ST.STATUS  ,  ST.COMPLETE  ,  S.REF_COMBOREVIEW_ACTION_ID  ,  ");
					sb.append(" RMR.ID  ,  CRA.ID  ,  RMR.COMBOREVIEW_ID  ,  RM.ID  ,  S.CREATED_DATE   ");
					
					sb.append("  ORDER BY START_DATE DESC, END_DATE DESC, CREATED_DATE ASC ");
					sb.append("                       ");
					sb.append("                         FOR JSON PATH )as '").append(listtype).append("'     ");
					sb.append("   ");
				}
				
				if(listtype.startsWith("list_payment")){
					JSONArray filters = PrintAgent.getFilters("list_payment",listtype);
					sb.append(" , ");
					sb.append(" (      ");
					sb.append(" select  P.ID as payment_id, M.METHOD_TYPE as payment_method, ");
					sb.append(" FORMAT(P.PAYMENT_AMOUNT, 'C', 'en-us') AS payment_amount, ");
				
					
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
					sb.append("     )) AS payment_paid_by ");
					
					sb.append(",");
					sb.append(" CONVERT(VARCHAR(10), P.PAYMENT_DATE, 101) + ' ' + RIGHT(CONVERT(CHAR(20), PAYMENT_DATE, 22), 11) as payment_paymentdate ");
					sb.append(",");
					sb.append(" T.TYPE as payment_transaction_type ");
					
					sb.append(" from payment p ");
					sb.append(" join LKUP_PAYMENT_METHOD M on P.LKUP_PAYMENT_METHOD_ID= M.ID  ");
					sb.append("   left outer join LKUP_PAYMENT_TRANSACTION_TYPE T on P.LKUP_PAYMENT_TRANSACTION_TYPE_ID= T.ID  ");
				//	sb.append(" left outer join REF_USERS R ON P.PAYEE_ID = R.ID ");
					sb.append(" left outer join USERS AU ON P.PAYEE_ID = AU.ID ");
					
					sb.append(" left outer join REF_").append(tableref).append("_PAYMENT RAP ON P.ID = RAP.PAYMENT_ID ");
					sb.append(" where P.AUTO='N' AND RAP.").append(idref).append("=").append(" Q.MAIN_ID ");
					
					if(filters.length()>0){
						for(int f=0;f<filters.length();f++){
							JSONObject fl = filters.getJSONObject(f);
							if(fl.has("filter") && fl.has("filterValue")){
								String val = Operator.sqlEscape(fl.getString("filterValue"));
								String[] v = Operator.split(fl.getString("filterValue"),",");
								if(v.length>0){
									if(Operator.toInt(v[0])<=0){
										val = "'"+Operator.replace(val, ",", "','") +"'";
									}
								}
								sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
							}
						
						}
						
					}
					
					
					
					sb.append(" order by P.PAYMENT_DATE");
				
				sb.append("                       ");
				sb.append("                         FOR JSON PATH )as '").append(listtype).append("'  ");
				sb.append("   ");
				}
				
			}
		}
		
		
		
		
		sb.append("  ");
		sb.append(" from  PROJECT P  ");
		sb.append(" join Q on P.ID=Q.MAIN_ID ");
		sb.append(" 	join LKUP_PROJECT_TYPE LPT on P.LKUP_PROJECT_TYPE_ID=LPT.ID  ");
		sb.append(" 	join LKUP_PROJECT_STATUS LPS on P.LKUP_PROJECT_STATUS_ID=LPS.ID  ");
		sb.append(" LEFT OUTER JOIN REF_PROJECT_PARKING AS DTA ON P.ID = DTA.PROJECT_ID ");
		sb.append(" LEFT OUTER JOIN USERS AS PCU ON P.CREATED_BY = PCU.ID ");
		sb.append(" LEFT OUTER JOIN USERS PUP ON P.UPDATED_BY = PUP.ID  ");
		sb.append("  ");
		sb.append("  ");
		sb.append(" where  P.ACTIVE='Y' ");
		sb.append("  ");
		sb.append(" FOR JSON PATH)  ");
		sb.append(" as RESULTS ");
		sb.append("  ");
		
		} catch(Exception e){
			Logger.error(e.getMessage());
		}
		return sb.toString();
	}

	public static String printAddlActivity(String type, int typeid) {
		StringBuilder sb = new StringBuilder();
		
		//sb.append(" 	WITH Q AS ( ");
		
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 	LABEL = t.c.value('local-name(.)', 'VARCHAR(100)') ");
		sb.append(" 	,FIELDVALUE = t.c.value('.', 'VARCHAR(500)') ");
		sb.append("  ");
		sb.append(" 	FROM ( ");
		sb.append(" 		SELECT ");
		sb.append(" 			 [XML] = ( ");
		sb.append(" 				 ");
		sb.append(" 	select  ");
		sb.append(" 	CONVERT(varchar(100), L.STR_NO)+' '+ ");
		sb.append(" 	CASE WHEN STR_MOD is null THEN '' ELSE STR_MOD+' ' END+'' + CASE WHEN PRE_DIR is null THEN '' ELSE PRE_DIR+' ' END+'' + LS.STR_NAME+' '+ LS.STR_TYPE+' ' +CASE WHEN L.UNIT is null then '' ELSE L.UNIT END as  ");
		sb.append(" 	lso_address,APN as lso_APN ,CITY as lso_city,STATE as lso_state,ZIP as lso_zip,ZIP4 as lso_zip4 ");
		sb.append("  ");
		sb.append(" 	FOR XML RAW('f'), TYPE ");
		sb.append(" 	) ");
		sb.append(" 	from ACTIVITY A ");
		sb.append(" 	join REF_LSO_PROJECT R on A.PROJECT_ID=R.PROJECT_ID AND R.ACTIVE='Y' ");
		sb.append(" 	join LSO L on R.LSO_ID=L.ID ");
		sb.append(" 	join LSO_STREET LS on L.LSO_STREET_ID=LS.ID ");
		sb.append(" 	left outer join REF_LSO_APN LA on L.ID =LA.LSO_ID ");
		sb.append(" 	WHERE  A.ID = ").append(typeid).append("  ");
		sb.append("  ");
		sb.append(" 	) p ");
		sb.append(" 	CROSS APPLY p.[XML].nodes('f/@*') t(c) ");
		sb.append("  ");
		/*sb.append(" 	union  ");
		sb.append("  ");
		sb.append(" 	select distinct ");
		sb.append(" 	'division_'+''+cast(LDT.ID as varchar(5)) as LABEL ");
		sb.append(" 	,LD.DIVISION as FIELDVALUE ");
		sb.append(" 	from ACTIVITY A ");
		sb.append(" 	join REF_LSO_PROJECT R on A.PROJECT_ID=R.PROJECT_ID AND R.ACTIVE='Y' ");
		
		sb.append(" 	left outer join V_CENTRAL_DIVISION VCD on R.LSO_ID=VCD.LSO_ID ");
		sb.append(" 		left outer join REF_LSO_DIVISIONS RLD on VCD.LAND_ID= RLD.LSO_ID   "); 	
		//sb.append(" 	left outer join REF_LSO_DIVISIONS RLD on R.LSO_ID= RLD.LSO_ID  ");
		
		sb.append(" 	left outer join LKUP_DIVISIONS LD on RLD.LKUP_DIVISIONS_ID= LD.ID and LD.ACTIVE='Y' ");
		sb.append(" 	left outer join LKUP_DIVISIONS_TYPE LDT on LD.LKUP_DIVISIONS_TYPE_ID=LDT.ID ");
		
		sb.append(" WHERE  A.ID = ").append(typeid).append("  ");
		
		sb.append(" 	union  ");
		sb.append("  ");
		sb.append(" 	select distinct ");
		sb.append(" 	'division_'+''+cast(LDT.ID as varchar(5))+'_description' as LABEL ");
		sb.append(" 	,LD.DESCRIPTION as FIELDVALUE ");
		sb.append(" 	from ACTIVITY A ");
		sb.append(" 	join REF_LSO_PROJECT R on A.PROJECT_ID=R.PROJECT_ID  AND R.ACTIVE='Y'  ");
		
		sb.append(" 	left outer join V_CENTRAL_DIVISION VCD on R.LSO_ID=VCD.LSO_ID ");
		sb.append(" 		left outer join REF_LSO_DIVISIONS RLD on VCD.LAND_ID= RLD.LSO_ID   "); 	
		//sb.append(" 	left outer join REF_LSO_DIVISIONS RLD on R.LSO_ID= RLD.LSO_ID  ");
		
		sb.append(" 	left outer join LKUP_DIVISIONS LD on RLD.LKUP_DIVISIONS_ID= LD.ID and LD.ACTIVE='Y' ");
		sb.append(" 	left outer join LKUP_DIVISIONS_TYPE LDT on LD.LKUP_DIVISIONS_TYPE_ID=LDT.ID ");
		
		sb.append(" WHERE  A.ID = ").append(typeid).append("  ");*/
		
		/*sb.append("  ) ");
		sb.append(" select ( ");
				sb.append(" select * from Q	 ");
						sb.append(" FOR JSON PATH, INCLUDE_NULL_VALUES  ) as divisions_lso ");*/
		
		
		return sb.toString();
	}	
	
	public static String printAddlProject(String type, int typeid) {
		StringBuilder sb = new StringBuilder();
		
		//sb.append(" 	WITH Q AS ( ");
		
		sb.append(" 	SELECT DISTINCT ");
		sb.append(" 	LABEL = t.c.value('local-name(.)', 'VARCHAR(100)') ");
		sb.append(" 	,FIELDVALUE = t.c.value('.', 'VARCHAR(500)') ");
		sb.append("  ");
		sb.append(" 	FROM ( ");
		sb.append(" 		SELECT ");
		sb.append(" 			 [XML] = ( ");
		sb.append(" 				 ");
		sb.append(" 	select  ");
		sb.append(" 	CONVERT(varchar(100), L.STR_NO)+' '+ ");
		sb.append(" 	CASE WHEN STR_MOD is null THEN '' ELSE STR_MOD+' ' END+'' + CASE WHEN PRE_DIR is null THEN '' ELSE PRE_DIR+' ' END+'' + LS.STR_NAME+' '+ LS.STR_TYPE+' ' +CASE WHEN L.UNIT is null then '' ELSE L.UNIT END as  ");
		sb.append(" 	lso_address,APN as lso_APN ,CITY as lso_city,STATE as lso_state,ZIP as lso_zip,ZIP4 as lso_zip4 ");
		sb.append("  ");
		sb.append(" 	FOR XML RAW('f'), TYPE ");
		sb.append(" 	) ");
		sb.append(" 	from PROJECT A ");
		sb.append(" 	join REF_LSO_PROJECT R on A.ID=R.PROJECT_ID AND R.ACTIVE='Y' ");
		sb.append(" 	join LSO L on R.LSO_ID=L.ID ");
		sb.append(" 	join LSO_STREET LS on L.LSO_STREET_ID=LS.ID ");
		sb.append(" 	left outer join REF_LSO_APN LA on L.ID =LA.LSO_ID ");
		sb.append(" 	WHERE  A.ID = ").append(typeid).append("  ");
		sb.append("  ");
		sb.append(" 	) p ");
		sb.append(" 	CROSS APPLY p.[XML].nodes('f/@*') t(c) ");
		sb.append("  ");
	/*	sb.append(" 	union  ");
		sb.append("  ");
		sb.append(" 	select distinct ");
		sb.append(" 	'division_'+''+cast(LDT.ID as varchar(5)) as LABEL ");
		sb.append(" 	,LD.DIVISION as FIELDVALUE ");
		sb.append(" 	from PROJECT A ");
		sb.append(" 	join REF_LSO_PROJECT R on A.ID=R.PROJECT_ID  AND R.ACTIVE='Y' ");
		
		sb.append(" left outer join V_CENTRAL_DIVISION VCD on R.LSO_ID=VCD.LSO_ID ");
		sb.append("		left outer join REF_LSO_DIVISIONS RLD on VCD.LAND_ID= RLD.LSO_ID    ");
		sb.append(" 	left outer join LKUP_DIVISIONS LD on RLD.LKUP_DIVISIONS_ID= LD.ID and LD.ACTIVE='Y' ");
		sb.append(" 	left outer join LKUP_DIVISIONS_TYPE LDT on LD.LKUP_DIVISIONS_TYPE_ID=LDT.ID ");
		
		sb.append(" WHERE  A.ID = ").append(typeid).append("  ");
		
		sb.append(" 	union  ");
		sb.append("  ");
		sb.append(" 	select distinct ");
		sb.append(" 	'division_'+''+cast(LDT.ID as varchar(5))+'_description' as LABEL ");
		sb.append(" 	,LD.DESCRIPTION as FIELDVALUE ");
		sb.append(" 	from PROJECT A ");
		sb.append(" 	join REF_LSO_PROJECT R on A.ID=R.PROJECT_ID  AND R.ACTIVE='Y' ");
		
		sb.append(" left outer join V_CENTRAL_DIVISION VCD on R.LSO_ID=VCD.LSO_ID ");
		sb.append("		left outer join REF_LSO_DIVISIONS RLD on VCD.LAND_ID= RLD.LSO_ID    ");
		sb.append(" 	left outer join LKUP_DIVISIONS LD on RLD.LKUP_DIVISIONS_ID= LD.ID and LD.ACTIVE='Y' ");
		sb.append(" 	left outer join LKUP_DIVISIONS_TYPE LDT on LD.LKUP_DIVISIONS_TYPE_ID=LDT.ID ");
		
		sb.append(" WHERE  A.ID = ").append(typeid).append("  ");*/
		
		/*sb.append("  ) ");
		sb.append(" select ( ");
				sb.append(" select * from Q	 ");
						sb.append(" FOR JSON PATH, INCLUDE_NULL_VALUES  ) as divisions_lso ");*/
		
		
		return sb.toString();
	}	
	
	
	
	
	public static String activitySummary(JSONObject doList,int projectId){
		StringBuilder sb = new StringBuilder();
		
		try{
			if(doList.has("lists")){
				for(int i=0;i<doList.getJSONArray("lists").length();i++){
					JSONObject o = doList.getJSONArray("lists").getJSONObject(i);
					String listtype = o.getString("type");
					Logger.info(i+"i--------------------->"+listtype);
						if(listtype.startsWith("list_summary_activities")){
							JSONArray filters = PrintAgent.getFilters("list_summary_activities",listtype);
					
							
							sb.append("  WITH F AS (  select     A.ID,'TOTAL' as GROUPNAME,0 as GROUP_ID,  SUM(SD.FEE_AMOUNT) as AMOUNT , SUM(SD.FEE_PAID) as PAID,SUM(SD.BALANCE_DUE) as BALANCE   ");
							sb.append("  from ACTIVITY A    ");
							sb.append("  join LKUP_ACT_STATUS AS ST ON A.LKUP_ACT_STATUS_ID = ST.ID  AND ST.EXPIRED = 'N'  ");
							sb.append("  AND COALESCE(CAST(A.EXP_DATE AS DATE), getDate()+1) > getDate()  join REF_ACT_STATEMENT RAS on A.ID=RAS.ACTIVITY_ID     ");
						//	sb.append("  join STATEMENT S on  RAS.STATEMENT_ID =S.ID and S.ACTIVE='Y'     ");
							sb.append("  join STATEMENT_DETAIL SD on  RAS.STATEMENT_ID=SD.STATEMENT_ID   AND SD.ACTIVE='Y' ");
							sb.append("  join FEE_GROUP FG   on SD.GROUP_ID=FG.ID  where  A.PROJECT_ID = ").append(projectId).append("  group by A.ID  )  ");
							sb.append("   select (  ");
							sb.append("  SELECT   ");
							sb.append("  A.ID,   A.ID AS LINKID,   'activity' AS LINKTYPE,   ");
							sb.append(" '").append(CsApiConfig.getQrcodeDomain()).append("/cs/?entity=lso&type=activity&typeid='+CAST(A.ID AS varchar) as activity_qr_code, ");
							sb.append("  A.ACT_NBR as 'activity_act_nbr',   ATT.TYPE AS activity_type,    LAS.STATUS as activity_status,     ");
							sb.append("  CU.USERNAME AS CREATED,    UP.USERNAME as UPDATED,    ");
							sb.append(" A.DESCRIPTION as activity_description, ");
							
							sb.append(" FORMAT(CASE WHEN F.AMOUNT IS NULL THEN 0 ELSE F.AMOUNT END, 'C', 'en-us') as total_activity_fee , ");
							sb.append(" FORMAT(CASE WHEN F.PAID IS NULL THEN 0 ELSE F.PAID END, 'C', 'en-us') as total_paid , ");
							sb.append(" FORMAT(CASE WHEN F.BALANCE IS NULL THEN 0 ELSE F.BALANCE END, 'C', 'en-us') as total_balance, ");
							sb.append(" FORMAT(A.VALUATION_CALCULATED, 'C', 'en-us') as activity_valuation_calculated , ");
							sb.append(" FORMAT(A.VALUATION_DECLARED, 'C', 'en-us') as activity_valuation_declared , ");
							sb.append(" CONVERT(VARCHAR(10),A.START_DATE,101) as activity_start_date , ");
							sb.append(" CONVERT(VARCHAR(10),A.APPLIED_DATE,101) as activity_applied_date, ");
							sb.append(" CONVERT(VARCHAR(10),A.ISSUED_DATE,101) as activity_issued_date , ");
							sb.append(" CONVERT(VARCHAR(10),A.EXP_DATE,101) as activity_exp_date  ");		
							
							sb.append("  FROM    ACTIVITY A     ");
							sb.append("  JOIN  LKUP_ACT_TYPE ATT ON A.LKUP_ACT_TYPE_ID = ATT.ID    JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID = LAS.ID  AND LAS.EXPIRED = 'N'   ");
							//sb.append("  AND COALESCE(CAST(A.EXP_DATE AS DATE), getDate()+1) > getDate()     ");
							sb.append("  LEFT OUTER JOIN USERS CU on A.CREATED_BY = CU.ID 	   ");
							sb.append("  LEFT OUTER JOIN USERS UP on A.UPDATED_BY = UP.ID 	  ");
							sb.append("  LEFT OUTER JOIN F on A.ID = F.ID  ");
								
							
							sb.append("  WHERE  A.PROJECT_ID =").append(projectId).append("    AND    A.ACTIVE = 'Y'  ");
							//sb.append(" where A.ID in (132759,132760,1101428)   ");
							//sb.append(" --AND RG.ID=8 ");
							//sb.append(" --group by RG.GROUP_NAME,R.NAME,R.DESCRIPTION,RCA.DATE,RCA.REVIEW_COMMENTS,LRT.TYPE, LRS.STATUS,LTT.TYPE,USERNAME ");
				
							if(filters.length()>0){
								for(int f=0;f<filters.length();f++){
									JSONObject fl = filters.getJSONObject(f);
									if(fl.has("filter") && fl.has("filterValue")){
										String val = Operator.sqlEscape(fl.getString("filterValue"));
										String[] v = Operator.split(fl.getString("filterValue"),",");
										if(v.length>0){
											if(Operator.toInt(v[0])<=0){
												val = "'"+Operator.replace(val, ",", "','") +"'";
											}
										}
										sb.append("  AND   LOWER(").append(fl.getString("filter")).append(") IN (").append(val.toLowerCase()).append(") ");	
									}
								
								}
								
							}
							
						//	sb.append(" order by GROUP_NAME,C.START_DATE, RCA.DATE ");
							sb.append("                       ");
							sb.append("                         FOR JSON PATH )as summary_activities     ");
							sb.append("   ");
						}
				}
			}
		
		} catch(Exception e){ Logger.error(e.getMessage());}
	
		
		return sb.toString();
	}
	
	
	
}















