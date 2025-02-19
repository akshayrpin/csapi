package csapi.impl.finance;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;

public class AutoPenalty {

	public boolean automatePenalty(){
		boolean r = false;
		Sage db = new Sage();
		JSONArray a = new JSONArray ();
		ArrayList<Integer> l = new ArrayList<Integer>();
		try {
			
			String command = "select DISTINCT RFG.FEE_GROUP_ID, RF.FEE_ID ,F.NAME,LF.DEFINITION,RF.ID,INPUT1,INPUT2,INPUT3,INPUT4,INPUT5 FROM REF_FEE_FORMULA RF JOIN LKUP_FORMULA LF on RF.LKUP_FORMULA_ID = LF.ID JOIN FEE F on RF.FEE_ID = F.ID JOIN ref_fee_group RFG on RF.FEE_ID= RFG.FEE_ID WHERE LF.DEFINITION LIKE '%_noofdays%' AND RF.EXPIRATION_DATE is null";
			
			db.query(command);
			while(db.next()){
				JSONObject h = new JSONObject();
				h.put("REF_FEE_FORMULA_ID", db.getInt("ID"));
				h.put("FEE_ID", db.getInt("FEE_ID"));
				h.put("NAME", db.getString("NAME"));
				h.put("DEFINITION", db.getString("DEFINITION"));
				
				h.put("INPUT1", db.getString("INPUT1"));
				h.put("INPUT2", db.getString("INPUT2"));
				h.put("INPUT3", db.getString("INPUT3"));
				h.put("INPUT4", db.getString("INPUT4"));
				h.put("INPUT5", db.getString("INPUT5"));
				
				
				//h.put("INPUT5", db.getString("INPUT5"));
				h.put("FEE_GROUP_ID", db.getInt("FEE_GROUP_ID"));
				
				a.put(h);
			}
			Logger.info("a.length()"+a.length());
			/*for(int i=0;i<a.length();i++){
				JSONObject h = a.getJSONObject(i);
				db.query("select FEE_GROUP_ID,FEE_ID  from ref_fee_group where FEE_ID in ("+h.getInt("FEE_ID")+") ");
				if(db.next()){
					Logger.info("CCC"+db.getString("FEE_GROUP_ID"));
					a.getJSONObject(i).put("FEE_GROUP_ID", db.getInt("FEE_GROUP_ID"));
				}
			
			}
			Logger.info("a.length()"+a.length());*/
			StringBuilder sb2 = new StringBuilder();
			for(int i=0;i<a.length();i++){
				JSONObject h = a.getJSONObject(i);
				String feenames = comparegetPenaltyFeeNames(h.getString("DEFINITION"),"_noofdays");
				sb2.append(feenames);
			
			}
			String feeids ="";
			if(Operator.hasValue(sb2.toString())){
				
				db.query("select stuff((select  ','+convert(varchar(100),id) from FEE where NAME in ("+sb2.toString()+") and ACTIVE='Y' for xml path('')),1,1,'') as COMBINED");
				if(db.next()){
					Logger.info("CCC"+db.getString("COMBINED"));
					//a.getJSONObject(i).put("FEEIDS", db.getString("COMBINED"));
					feeids= db.getString("COMBINED");
				}
			}
			
			
			//for(int i=0;i<a.length();i++){
				//JSONObject h = a.getJSONObject(i);
				StringBuilder sb = new StringBuilder();
				
				sb.append(" select DISTINCT A.ID  ");
				sb.append(" from  ");
				sb.append("  ACTIVITY A  ");
				sb.append(" JOIN LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID = LAT.ID ");
				//sb.append(" JOIN REF_ACT_FEE_GROUP RAFG on LAT.ID = RAFG.LKUP_ACT_TYPE_ID AND  RAFG.FEE_GROUP_ID in (").append(h.getString("FEE_GROUP_ID")).append(") ");
				sb.append(" JOIN REF_ACT_STATEMENT RAS on A.ID = RAS.ACTIVITY_ID  ");
				//sb.append(" JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID= SD.STATEMENT_ID AND  SD.REF_FEE_FORMULA_ID in (").append(h.getString("REF_FEE_FORMULA_ID")).append(") ");
				sb.append(" JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID= SD.STATEMENT_ID  ");
				sb.append(" JOIN FEE F on SD.FEE_ID = F.ID  AND FEE_ID in (").append(feeids).append(") ");
				sb.append(" WHERE SD.CREATED_DATE > (getDate()  -365 )   AND SD.ACTIVE='Y' AND SD.BALANCE_DUE>0   ");
				db.query(sb.toString());
				while(db.next()){
					l.add(db.getInt("ID"));
				}
			
			//}
			Logger.info(l.size());
			
			
			
			
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		
		
		db.clear();
		
		if(l.size()>0){
			try{
				for(int i=0;i<a.length();i++){
					JSONObject h = a.getJSONObject(i);
					doProcess(l,h.getString("DEFINITION"), h.getInt("FEE_ID"),h.getInt("REF_FEE_FORMULA_ID"),h.getInt("FEE_GROUP_ID"), h.getString("INPUT1"), h.getString("INPUT2"), h.getString("INPUT3"), h.getString("INPUT4"), h.getString("INPUT5"));
				}
			}catch(Exception e){
				Logger.error(e.getMessage());
			}
		}
		
		return r;
	}
	
	
	public static boolean doProcess(ArrayList<Integer> l,String definition,int feeId,int reffeeid,int groupid,String input1,String input2, String input3,String input4,String input5){
		
		for(Integer actid: l){
		
		 String s = definition; 
		 s = Operator.replace(s, "input1", input1);
		 s = Operator.replace(s, "input2", input2);
		 s = Operator.replace(s, "input3", input3);
		 s = Operator.replace(s, "input4", input4);
		 s = Operator.replace(s, "input5", input5);
		
		 s = getForumulaNoofDays(actid, s,""); 
		 Logger.info("MID FINAL++++"+ s);
			
			
			 //Logger.info("FINAL++++"+ s);
			double value = Formula.calculate(s);
			if(value>0){
				 Logger.info(actid+" PERMIT NO AMOUNT FINAL++++"+ value+" feeid::"+feeId+" reffeeid::"+reffeeid+"groupid ::"+groupid+" ");
				 applyFee(actid,feeId,reffeeid,groupid,value);
			}
			
		
		
		}
		
		
		return true;
		
	}
	
	
	public static boolean applyFee(int actid, int feeId,int reffeeid,int groupId,double value){
		boolean result = false;
		
		Sage db = new Sage();
		
		try{
		
			StringBuilder sb = new StringBuilder();
			sb.append(" select SD.ID,SD.STATEMENT_ID , SD.FEE_ID,F.NAME,FEE_DATE, DATEDIFF(DAY, getdate(), FEE_DATE) AS DATE_DIFF,FEE_AMOUNT from STATEMENT_DETAIL SD  ");
			sb.append(" JOIN FEE F on SD.FEE_ID = F.ID  ");
			sb.append(" JOIN REF_ACT_STATEMENT RAS on SD.STATEMENT_ID = RAS.STATEMENT_ID  AND RAS.ACTIVITY_ID= ").append(actid);
			sb.append(" WHERE SD.ACTIVE='Y' AND SD.BALANCE_DUE>0 AND REF_FEE_FORMULA_ID =").append(reffeeid).append(" AND FEE_ID=").append(feeId).append(" AND GROUP_ID=").append(groupId);
			db.query(sb.toString());
			boolean update = true;
			while(db.next()){
				
				double amt = db.getDouble("FEE_AMOUNT");
				Logger.info(value +"--"+amt+" ????: "+(value!=amt));
				if(value!=amt){
					update = true;
					db.update("UPDATE STATEMENT_DETAIL SET ACTIVE='N', UPDATED_DATE= current_timestamp WHERE FEE_AMOUNT=BALANCE_DUE AND ID ="+db.getInt("ID"));
					
				}else {
					update =false;
				}
				
			}
			if(value>0 && update){
			
				Timekeeper k = new Timekeeper();
				String random = Operator.randomString(6);
				double total = value;
				int statementId =0;
				String command = FinanceSQL.insertStatement(random, total, "", k, "N", 890); 
			
				result = db.update(command);
				
				db.query(FinanceSQL.getStatement(random, 890));
				if(db.next()){
					statementId = db.getInt("ID");
				}
				
				if(statementId>0){
					result = db.update(FinanceSQL.insertRefStatement("activity", actid, statementId, 890));
				}
				
				
				sb = new StringBuilder();
				sb.append(" insert into statement_detail (STATEMENT_ID,FEE_DATE,GROUP_ID,FEE_ID,REF_FEE_FORMULA_ID,FEE_AMOUNT,FEE_PAID,BALANCE_DUE,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,CREATED_IP,UPDATED_IP,  ");
				sb.append(" INPUT1,INPUT2,INPUT3,INPUT4,INPUT5, INPUT_TYPE1,INPUT_TYPE2,INPUT_TYPE3,INPUT_TYPE4,INPUT_TYPE5) ");
		
				
				sb.append(" select ").append(statementId).append(",getDate(),").append(groupId).append(",FEE_ID,ID,").append(value).append(",0,").append(value).append(",890,getdate(),890,getdate(),'10.14.6.19','10.14.6.19', ");
				sb.append(" INPUT1,INPUT2,INPUT3,INPUT4,INPUT5, INPUT_TYPE1,INPUT_TYPE2,INPUT_TYPE3,INPUT_TYPE4,INPUT_TYPE5 ");
				sb.append(" from REF_FEE_FORMULA WHERE ID = ").append(reffeeid).append("  ");
				
				result = db.update(sb.toString());
				
				
				
				
			}
		}catch (Exception e){
			Logger.info(e.getMessage());
		}
		db.clear();
		
		return result;
	}
	
	
	
	
	public static String comparegetPenaltyFeeNames(String formula,String keyword,String diff){
		//String keyword = "_noofdays";
		Logger.info("came here ++"+formula);
		String s = formula;
		StringBuilder sb = new StringBuilder();
		try{
		Pattern p = Pattern.compile(keyword+"\\[(.*?)\\]");
		Matcher m = p.matcher(s);
		boolean t = false;
		while (m.find()) {
			String feename =  m.group(1);
			Logger.info("Masking: " +feename);
			sb.append("'").append(feename).append("'");
			sb.append(",");
			t = true;
		}	
		if(t){
			sb.append("'sdkjfbkjdsf'");
		}
		Logger.info("s: " +s);
		}catch(Exception e){
			e.printStackTrace();
			Logger.error("compareReplace ERROR  :" + e.getMessage());
			s ="";
		}
		return sb.toString();
	}
	
	public static String comparegetPenaltyFeeNames(String formula,String keyword){
		//String keyword = "_noofdays";
		Logger.info("came here ++"+formula);
		String s = formula;
		StringBuilder sb = new StringBuilder();
		try{
		Pattern p = Pattern.compile(keyword+"\\[(.*?)\\]");
		Matcher m = p.matcher(s);
		boolean t = false;
		while (m.find()) {
			String feename =  m.group(1);
			Logger.info("Masking: " +feename);
			sb.append("'").append(feename).append("'");
			sb.append(",");
			t = true;
		}	
		if(t){
			sb.append("'sdkjfbkjdsf'");
		}
		Logger.info("s: " +s);
		}catch(Exception e){
			e.printStackTrace();
			Logger.error("compareReplace ERROR  :" + e.getMessage());
			s ="";
		}
		return sb.toString();
	}

	public static String getForumulaNoofDays(int actid,String formula,String date){
		String s = formula;
		Sage db = new Sage();
		
		if(!Operator.hasValue(date)){
			date = "getdate()";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(" select SD.ID,SD.STATEMENT_ID , SD.FEE_ID,F.NAME,FEE_DATE, DATEDIFF(DAY, ").append(date).append(", FEE_DATE) AS DATE_DIFF from STATEMENT_DETAIL SD  ");
		sb.append(" JOIN FEE F on SD.FEE_ID = F.ID  ");
		sb.append(" JOIN REF_ACT_STATEMENT RAS on SD.STATEMENT_ID = RAS.STATEMENT_ID  AND RAS.ACTIVITY_ID= ").append(actid);
		sb.append(" WHERE SD.ACTIVE='Y' AND SD.BALANCE_DUE>0 ");
		
	
		db.query(sb.toString());
		
		while(db.next()){
			String noofdays = Math.abs(db.getInt("DATE_DIFF"))+""; 
			s = Operator.replace(s, "_noofdays["+db.getString("NAME")+"]", noofdays);
			
		}
		
		db.clear();
		return s;
	}

	
	public static void main(String args[]){
		double o =0;
		int noofdays =185;
		double input2 = 1000;
		if(noofdays <= 20){
			
		}
	 
		else if(noofdays > 20 && noofdays <=50){
			o =  (input2 * 0.50);
		}
		else if(noofdays > 50 && noofdays <=80){
			o =  (input2 * 0.50) + (input2 * 0.10);
		}
		else if(noofdays > 80 && noofdays <=110){
			o =  (input2 * 0.50) + (input2 * 0.20);
		}
		else if(noofdays > 110 && noofdays <=140){
			o =  (input2 * 0.50) + (input2 * 0.30);
		}
		else if(noofdays > 140 && noofdays <=170){
			o =  (input2 * 0.50) + (input2 * 0.40);
		}
	 	else if(noofdays > 170){
	 		o =  (input2 * 0.50) + (input2 * 0.40);
		}
		System.out.println("hhhhh"+o);
	}

}
