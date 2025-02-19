package csapi.impl.lockbox;

import java.util.ArrayList;
import java.util.HashMap;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.activity.ActivityValidate;
import csapi.impl.finance.FinanceAgent;
import csapi.impl.log.LogAgent;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;

public class LockboxAgent {

	
	
	
	public static ResponseVO loadlockbox(RequestVO vo,Token u){
		ResponseVO r = new ResponseVO();
		try{
			RequestVO process = vo;
			process.setType("project");
			int id = 0;
			if(Operator.toInt(vo.getId())>0){
				id = Operator.toInt(vo.getId());
			}
			ArrayList<MapSet> a = getlockboxdata(id);
			String batchNumber = "";
			Logger.info("SUNIL------------a size"+a.size());
			int l = a.size();
			for(int i=0;i<a.size();i++){ 
				MapSet m = a.get(i);
				 
				batchNumber = m.getString("BATCH_NUMBER");
				LogAgent.updateLog(vo.getProcessid(),40+i, "Processing  permits, finance, payments & validating for transaction #: "+m.getString("TRANSACTION_NUMBER"), "Processing permits for transaction #: "+m.getString("TRANSACTION_NUMBER"));
				ArrayList<Integer> n = new ArrayList<Integer>();
				if( !(m.getInt("DAYTIME_QTY")<=0 && m.getInt("OVERNIGHT_QTY")<=0)){
					if(m.getInt("DAYTIME_QTY")>0){
						for(int k=0;k<m.getInt("DAYTIME_QTY");k++){
							int LKUP_ACT_TYPE_ID = 252;
							 //LogAgent.updateLog(vo.getProcessid(), "Update Activity Addl : "+res, "Update Activity Addl: "+res);
							boolean rvalidate =  ActivityValidate.checks(vo.getProcessid(), m.getInt("PROJECT_ID"), LKUP_ACT_TYPE_ID, u,process,true);
							Logger.info(LKUP_ACT_TYPE_ID+"--->"+m.getInt("PROJECT_ID")+"---->"+rvalidate+"--->");
							if(rvalidate){
								int res = ActivityAgent.addActivity(m.getString("PROCESS_ID"), "lso", m.getInt("PROJECT_ID"), LKUP_ACT_TYPE_ID, m.getString("DESCRIPTION"), m.getInt("LKUP_ACT_STATUS_ID"), Operator.toDouble(m.getString("VALUATION_CALCULATED")), Operator.toDouble(m.getString("VALUATION_DECLARED")), m.getString("PLAN_CHK_REQ"), m.getString("START_DATE"), m.getString("APPLIED_DATE"), m.getString("ISSUED_DATE"), m.getString("EXP_DATE"), m.getString("APPLICATIONEXP_DATE"), m.getString("FINAL_DATE"), m.getString("ONLINE"), m.getString("SENSITIVE"), m.getString("INHERIT"), u.getId(), u.getIp(), u);
								n.add(res);
							}
							if(!rvalidate){
								LogAgent.updateLog(vo.getProcessid(), 50+i);
							}
							
						}
					}
					if(m.getInt("OVERNIGHT_QTY")>0){
						for(int k=0;k<m.getInt("OVERNIGHT_QTY");k++){
							int LKUP_ACT_TYPE_ID = 251;
							boolean rvalidate =  ActivityValidate.checks(vo.getProcessid(), m.getInt("PROJECT_ID"), LKUP_ACT_TYPE_ID, u,process,true);
							Logger.info(LKUP_ACT_TYPE_ID+"--->"+m.getInt("PROJECT_ID")+"---->"+rvalidate+"--->");
							if(rvalidate){
								int res = ActivityAgent.addActivity(m.getString("PROCESS_ID"), "lso", m.getInt("PROJECT_ID"), LKUP_ACT_TYPE_ID, m.getString("DESCRIPTION"), m.getInt("LKUP_ACT_STATUS_ID"), Operator.toDouble(m.getString("VALUATION_CALCULATED")), Operator.toDouble(m.getString("VALUATION_DECLARED")), m.getString("PLAN_CHK_REQ"), m.getString("START_DATE"), m.getString("APPLIED_DATE"), m.getString("ISSUED_DATE"), m.getString("EXP_DATE"), m.getString("APPLICATIONEXP_DATE"), m.getString("FINAL_DATE"), m.getString("ONLINE"), m.getString("SENSITIVE"), m.getString("INHERIT"), u.getId(), u.getIp(), u);
								n.add(res);
							}
							if(!rvalidate){
								LogAgent.updateLog(vo.getProcessid(), 50+i);
							}
							
						}
					}
					LogAgent.updateLog(m.getString("PROCESS_ID"),80);
					if(n.size()>0){
						FinanceAgent.saveLockBoxPayment(m, n, vo, u);
						
					}
					
				}
				Logger.info("nszzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"+n.size());
				r.setProcessid(m.getString("PROCESS_ID"));
				r.setPercentcomplete(0);
				
				Logger.info(i+"iszzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"+a.size());
				if(i==(l-1)){
					
					r.setPercentcomplete(100);
					LogAgent.updateLog(m.getString("PROCESS_ID"),100,"Complete8d","Complete8d");
				}
				
			}
			
			
			
			LogAgent.updateLog(vo.getProcessid(),100,"Completed","Completed for #"+batchNumber);
			
			
		}catch(Exception e){
			r.setMessagecode("cs500");
			r.setPercentcomplete(0);
			LogAgent.updateLog(vo.getProcessid(),100);
		}
		return r;
	}
	
	public static boolean updateLockbox(RequestVO vo,Token u){
		boolean r = false;
		Sage db = new Sage();
		MapSet m = new MapSet();
		ArrayList<HashMap<String, String>> mb = new ArrayList<HashMap<String, String>>();
		try{
		String DAYTIME_QTY = vo.getExtras().get("DAYTIME_QTY");
		String OVERNIGHT_QTY = vo.getExtras().get("OVERNIGHT_QTY");
		
		int PROJECT_ID = 0;
		int PAYEE_ID = 0;
		
		if(Operator.hasValue(vo.getExtras().get("PROJECT_NO"))){
			String command = "select ID FROM PROJECT WHERE PROJECT_NBR = "+vo.getExtras().get("PROJECT_NO");
			db.query(command);
			if(db.next()){
				PROJECT_ID = db.getInt("ID");
			}
		}
		
		if(Operator.hasValue(vo.getExtras().get("PAYEE_EMAIL"))){
			String command = "select ID FROM USERS WHERE EMAIL  = '"+Operator.sqlEscape(vo.getExtras().get("PAYEE_EMAIL"))+"'";
			db.query(command);
			if(db.next()){
				PAYEE_ID = db.getInt("ID");
			}
		}
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE LOCKBOX_UPLOADS SET UPDATED_DATE=CURRENT_TIMESTAMP ");
		
		if(Operator.toInt(DAYTIME_QTY)>-1){
			sb.append(" ,DAYTIME_QTY=").append(DAYTIME_QTY);
		}
		
		if(Operator.toInt(OVERNIGHT_QTY)>-1){
			sb.append(" ,OVERNIGHT_QTY=").append(OVERNIGHT_QTY);
		}
		
		
		/*if(PROJECT_ID>0){
			sb.append(" ,PROJECT_ID=").append(PROJECT_ID);
		}
		if(PAYEE_ID>0){
			sb.append(" ,PAYEE_ID=").append(PAYEE_ID);
		}*/
		
		if(Operator.hasValue(vo.getExtras().get("ACCOUNT_NUMBER"))){
			sb.append(" ,ACCOUNT_NUMBER=").append(vo.getExtras().get("ACCOUNT_NUMBER"));
		}
		
		sb.append(" WHERE ID =").append(vo.getId());
		
		r = db.update(sb.toString());
		if(r){
			String command = " UPDATE LOCKBOX_UPLOADS  SET PROJECT_ID = R.PROJECT_ID FROM LOCKBOX_UPLOADS as  L INNER JOIN REF_PROJECT_PARKING as  R on L.ACCOUNT_NUMBER = R.ID AND R.ACTIVE='Y' WHERE L.ACCOUNT_NUMBER >0 AND L.PROJECT_ID<=0 AND L.ID="+vo.getId();
			r = db.update(command);
			
			command = " UPDATE LOCKBOX_UPLOADS  SET PAYEE_ID = U.USERS_ID FROM LOCKBOX_UPLOADS as  L INNER JOIN REF_PROJECT_USERS as  R on L.PROJECT_ID = R.PROJECT_ID AND R.ACTIVE='Y'"
					+ "JOIN REF_USERS AS U on R.REF_USERS_ID = U.ID AND U.LKUP_USERS_TYPE_ID=11"
					+ "WHERE L.ID='"+vo.getId()+"'";
			r = db.update(command);
			
			
			command = "select ACCOUNT_NUMBER,BATCH_NUMBER,PROCESS_ID FROM LOCKBOX_UPLOADS WHERE ID = "+vo.getId();
			db.query(command);
			if(db.next()){
				m.add("ACCOUNT_NUMBER", db.getString("ACCOUNT_NUMBER")) ;
				m.add("BATCH_NUMBER", db.getString("BATCH_NUMBER")) ;
				m.add("PROCESS_ID", db.getString("PROCESS_ID")) ;
				HashMap<String, String> el = new HashMap<String, String>();
				el.put("ACCOUNT NUMBER", db.getString("ACCOUNT_NUMBER")) ;
				mb.add(el);
			}
			
		}
		
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		
		if(Operator.hasValue(m.getString("ACCOUNT_NUMBER"))){
			validateQty(mb, m.getString("BATCH_NUMBER"), m.getString("PROCESS_ID"));
		}
		
		return r;
	}
	
	
	public static boolean addLockbox(RequestVO vo,Token u){
		boolean r = false;
		Sage db = new Sage();
		MapSet m = new MapSet();
		ArrayList<HashMap<String, String>> mb = new ArrayList<HashMap<String, String>>();
		try{
		
			String PAYMENT_AMOUNT = vo.getExtras().get("PAYMENT_AMOUNT");
		
		
			if(Operator.toDouble(PAYMENT_AMOUNT)>0){
				
				String c = LockboxSQL.addLockbox(vo,u);
				
				db.query(c);
				
				int id =0;
				if(db.next()){
					id = db.getInt("ID");
				}
				if(id>0){
					String command = " UPDATE LOCKBOX_UPLOADS  SET PROJECT_ID = R.PROJECT_ID FROM LOCKBOX_UPLOADS as  L INNER JOIN REF_PROJECT_PARKING as  R on L.ACCOUNT_NUMBER = R.ID AND R.ACTIVE='Y' WHERE L.ACCOUNT_NUMBER >0 AND L.PROJECT_ID<=0 AND L.ID="+id;
					r = db.update(command);
					
					command = " UPDATE LOCKBOX_UPLOADS  SET PAYEE_ID = U.USERS_ID FROM LOCKBOX_UPLOADS as  L INNER JOIN REF_PROJECT_USERS as  R on L.PROJECT_ID = R.PROJECT_ID AND R.ACTIVE='Y'"
							+ "JOIN REF_USERS AS U on R.REF_USERS_ID = U.ID AND U.LKUP_USERS_TYPE_ID=11"
							+ "WHERE L.ID='"+id+"'";
					r = db.update(command);
					
					
					command = "select ACCOUNT_NUMBER,BATCH_NUMBER,PROCESS_ID FROM LOCKBOX_UPLOADS WHERE ID = "+vo.getId();
					db.query(command);
					if(db.next()){
						m.add("ACCOUNT_NUMBER", db.getString("ACCOUNT_NUMBER")) ;
						m.add("BATCH_NUMBER", db.getString("BATCH_NUMBER")) ;
						m.add("PROCESS_ID", db.getString("PROCESS_ID")) ;
						HashMap<String, String> el = new HashMap<String, String>();
						el.put("ACCOUNT NUMBER", db.getString("ACCOUNT_NUMBER")) ;
						mb.add(el);
					}
					
				}
			}
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		
		if(Operator.hasValue(m.getString("ACCOUNT_NUMBER"))){
			validateQty(mb, m.getString("BATCH_NUMBER"), m.getString("PROCESS_ID"));
		}
		
		return r;
	}
	
	
	
	public static ArrayList<MapSet> getlockboxdata(int id) {
		ArrayList<MapSet> ma = new ArrayList<MapSet>();

		Sage db = new Sage();
		
		try{
			String command = "select L.* from LOCKBOX_UPLOADS L LEFT OUTER JOIN PAYMENT P on L.TRANSACTION_NUMBER= P.LOCKBOX_TRANSACTION_NO WHERE L.ACTIVE='Y' AND P.ID is null AND L.PAYMENT_AMOUNT>0 AND L.PAYEE_ID >0 ";
			if(id>0){
				command += " AND L.ID="+id;
			}
			db.query(command);
			Timekeeper k = new Timekeeper();
			String styear = k.yyyy();
			k.addYear(1);
			String edyear = k.yyyy();
			k.addYear(-1);
			while(db.next()){
				MapSet m = new MapSet();
				m.add("ID", db.getInt("ID"));
				m.add("PROJECT_ID", db.getInt("PROJECT_ID"));
				m.add("DAYTIME_QTY", db.getInt("DAYTIME_QTY"));
				m.add("OVERNIGHT_QTY", db.getInt("OVERNIGHT_QTY"));
				m.add("TRANSACTION_NUMBER", db.getString("TRANSACTION_NUMBER"));
				m.add("BATCH_NUMBER", db.getString("BATCH_NUMBER"));
				m.add("CHECK_NO", db.getString("CHECK_ACCOUNT"));
				m.add("PAYMENT_AMOUNT", db.getDouble("PAYMENT_AMOUNT"));
				m.add("DESCRIPTION", "Loaded via lockbox #"+db.getString("TRANSACTION_NUMBER"));
				m.add("LKUP_ACT_STATUS_ID", 27);
				m.add("ACCOUNT_NUMBER", db.getString("ACCOUNT_NUMBER"));
				m.add("VALUATION_CALCULATED","0.00");
				m.add("VALUATION_DECLARED","0.00");
				m.add("PLAN_CHK_REQ","N");
				m.add("APPLIED_DATE",k.getString("YYYY/MM/DD"));
				m.add("PROCESS_ID",db.getString("PROCESS_ID"));
				m.add("START_DATE",styear+"/10/01");
				m.add("EXP_DATE",edyear+"/09/30");
				m.add("ISSUED_DATE","");
				m.add("APPLICATIONEXP_DATE","");
				m.add("FINAL_DATE","");
				m.add("ONLINE","N");
				m.add("SENSITIVE","N");
				m.add("INHERIT","N");
				
				m.add("PAYEE_ID",db.getInt("PAYEE_ID"));
				boolean p = true;
				int dqty = db.getInt("DAYTIME_QTY");
				int oqty = db.getInt("OVERNIGHT_QTY");
				
				
				int dcurrentqty = db.getInt("DAYTIME_CURR_QTY");
				int ocurrentqty = db.getInt("OVERNIGHT_CURR_QTY");
				int dprevqty = db.getInt("DAYTIME_PREV_QTY");
				int oprevqty = db.getInt("OVERNIGHT_PREV_QTY");
				
				if((dqty+dcurrentqty)>3){
					p = false;
				}
				
				if((oqty+ocurrentqty)>oprevqty){
					p = false;
				}
				
				/*if((dqty+dcurrentqty)> dprevqty){
					p= false;
				}*/
				//if(dqty<=0 && dprevqty>0){
					//p = false;
				//}
				
				/*if(oqty<=0 && oprevqty>0){
					p = false;
				}*/
				
				if(p){
					ma.add(m);
				}
			}
			
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		return ma;
	}
	
	
	public static HashMap<String,String> getlockboxedit(int id) {
		HashMap<String,String> m = new HashMap<String,String>();
		Sage db = new Sage();
		
		try{
			String command = "select L.* from LOCKBOX_UPLOADS L LEFT OUTER JOIN PAYMENT P on L.TRANSACTION_NUMBER= P.LOCKBOX_TRANSACTION_NO WHERE L.ACTIVE='Y' AND L.PAYMENT_AMOUNT>0  ";
			if(id>0){
				command += " AND L.ID="+id;
			}else {
				command += " AND L.PAYEE_ID >0 ";
			}
			db.query(command);
			Timekeeper k = new Timekeeper();
			String styear = k.yyyy();
			k.addYear(1);
			String edyear = k.yyyy();
			k.addYear(-1);
			while(db.next()){
				m.put("ID", db.getString("ID"));
				m.put("PROJECT_ID", db.getString("PROJECT_ID"));
				m.put("ACCOUNT_NUMBER", db.getString("ACCOUNT_NUMBER"));
				m.put("DAYTIME_QTY", db.getString("DAYTIME_QTY"));
				m.put("OVERNIGHT_QTY", db.getString("OVERNIGHT_QTY"));
				m.put("TRANSACTION_NUMBER", db.getString("TRANSACTION_NUMBER"));
				m.put("CHECK_NO", db.getString("CHECK_ACCOUNT"));
				m.put("PAYMENT_AMOUNT", db.getString("PAYMENT_AMOUNT"));
				m.put("DESCRIPTION", "Loaded via lockbox #"+db.getString("TRANSACTION_NUMBER"));
				m.put("LKUP_ACT_STATUS_ID", "27");
				
				m.put("VALUATION_CALCULATED","0.00");
				m.put("VALUATION_DECLARED","0.00");
				m.put("PLAN_CHK_REQ","N");
				m.put("APPLIED_DATE",k.getString("YYYY/MM/DD"));
				
				m.put("START_DATE",styear+"/10/01");
				m.put("EXP_DATE",edyear+"/09/30");
				m.put("ISSUED_DATE","");
				m.put("APPLICATIONEXP_DATE","");
				m.put("FINAL_DATE","");
				m.put("ONLINE","N");
				m.put("SENSITIVE","N");
				m.put("INHERIT","N");
				
				m.put("PAYEE_ID",db.getString("PAYEE_ID"));
				
				
			}
			
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		return m;
	}
	
	public static ResponseVO lockboxdataExceptions(ResponseVO r) {
		ArrayList<HashMap<String,String>> ma = new ArrayList<HashMap<String,String>>();
		Sage db = new Sage();
		
		try{
			String command = "select L.*,CONVERT(VARCHAR(10),L.UPDATED_DATE,101) as C_UPDATED_DATE from LOCKBOX_UPLOADS L  WHERE L.ACTIVE='Y' ";
			db.query(command);
			Timekeeper k = new Timekeeper();
			String styear = k.yyyy();
			k.addYear(1);
			String edyear = k.yyyy();
			k.addYear(-1);
			while(db.next()){
				HashMap<String,String> m = new HashMap<String,String>();
				m.put("PROJECT_ID", db.getString("PROJECT_ID"));
				m.put("ACCOUNT_NUMBER", db.getString("ACCOUNT_NUMBER"));
				m.put("DAYTIME_QTY", db.getString("DAYTIME_QTY"));
				m.put("OVERNIGHT_QTY", db.getString("OVERNIGHT_QTY"));
				m.put("TRANSACTION_NUMBER", db.getString("TRANSACTION_NUMBER"));
				m.put("BATCH_NUMBER", db.getString("BATCH_NUMBER"));
				m.put("CHECK_NO", db.getString("CHECK_ACCOUNT"));
				m.put("PAYMENT_AMOUNT", db.getString("PAYMENT_AMOUNT"));
				m.put("DESCRIPTION", "Loaded via lockbox #"+db.getString("TRANSACTION_NUMBER"));
				m.put("LKUP_ACT_STATUS_ID", "27");
				m.put("PROCESS_ID", db.getString("PROCESS_ID"));
				m.put("UPDATED_DATE", db.getString("UPDATED_DATE"));
				m.put("C_UPDATED_DATE", db.getString("C_UPDATED_DATE"));
				m.put("ID", db.getString("ID"));
				
				m.put("DAYTIME_PREV_QTY", db.getString("DAYTIME_PREV_QTY"));
				m.put("OVERNIGHT_PREV_QTY", db.getString("OVERNIGHT_PREV_QTY"));
				m.put("DAYTIME_CURR_QTY", db.getString("DAYTIME_CURR_QTY"));
				m.put("OVERNIGHT_CURR_QTY", db.getString("OVERNIGHT_CURR_QTY"));
				
				m.put("VALUATION_CALCULATED","0.00");
				m.put("VALUATION_DECLARED","0.00");
				m.put("PLAN_CHK_REQ","N");
				m.put("APPLIED_DATE",k.getString("YYYY/MM/DD"));
				
				m.put("START_DATE",styear+"/10/01");
				m.put("EXP_DATE",edyear+"/09/30");
				m.put("ISSUED_DATE","");
				m.put("APPLICATIONEXP_DATE","");
				m.put("FINAL_DATE","");
				m.put("ONLINE","N");
				m.put("SENSITIVE","N");
				m.put("INHERIT","N");
				
				m.put("PAYEE_ID",db.getString("PAYEE_ID"));
				
				ma.add(m);
			}
			r.setList(ma);
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		
		
		return r;
	}
	
	public static ResponseVO lockboxdataSearch(ResponseVO r,String ref) {
		ArrayList<HashMap<String,String>> ma = new ArrayList<HashMap<String,String>>();
		Sage db = new Sage();
		
		try{
			String command = "select L.*,CONVERT(VARCHAR(10),L.UPDATED_DATE,101) as C_UPDATED_DATE,P.ID AS PAYMENT_ID from LOCKBOX_UPLOADS L  LEFT OUTER JOIN PAYMENT P on L.TRANSACTION_NUMBER= P.LOCKBOX_TRANSACTION_NO WHERE  L.BATCH_NUMBER ='"+ref+"'";
			db.query(command);
			Timekeeper k = new Timekeeper();
			String styear = k.yyyy();
			k.addYear(1);
			String edyear = k.yyyy();
			k.addYear(-1);
			while(db.next()){
				HashMap<String,String> m = new HashMap<String,String>();
				m.put("PROJECT_ID", db.getString("PROJECT_ID"));
				m.put("ACCOUNT_NUMBER", db.getString("ACCOUNT_NUMBER"));
				m.put("DAYTIME_QTY", db.getString("DAYTIME_QTY"));
				m.put("OVERNIGHT_QTY", db.getString("OVERNIGHT_QTY"));
				m.put("TRANSACTION_NUMBER", db.getString("TRANSACTION_NUMBER"));
				m.put("BATCH_NUMBER", db.getString("BATCH_NUMBER"));
				m.put("CHECK_NO", db.getString("CHECK_ACCOUNT"));
				m.put("PAYMENT_AMOUNT", db.getString("PAYMENT_AMOUNT"));
				m.put("DESCRIPTION", "Loaded via lockbox #"+db.getString("TRANSACTION_NUMBER"));
				m.put("LKUP_ACT_STATUS_ID", "27");
				m.put("PROCESS_ID", db.getString("PROCESS_ID"));
				m.put("UPDATED_DATE", db.getString("UPDATED_DATE"));
				m.put("C_UPDATED_DATE", db.getString("C_UPDATED_DATE"));
				m.put("ID", db.getString("ID"));
				
				m.put("DAYTIME_PREV_QTY", db.getString("DAYTIME_PREV_QTY"));
				m.put("OVERNIGHT_PREV_QTY", db.getString("OVERNIGHT_PREV_QTY"));
				m.put("DAYTIME_CURR_QTY", db.getString("DAYTIME_CURR_QTY"));
				m.put("OVERNIGHT_CURR_QTY", db.getString("OVERNIGHT_CURR_QTY"));
				
				m.put("VALUATION_CALCULATED","0.00");
				m.put("VALUATION_DECLARED","0.00");
				m.put("PLAN_CHK_REQ","N");
				m.put("APPLIED_DATE",k.getString("YYYY/MM/DD"));
				
				m.put("START_DATE",styear+"/10/01");
				m.put("EXP_DATE",edyear+"/09/30");
				m.put("ISSUED_DATE","");
				m.put("APPLICATIONEXP_DATE","");
				m.put("FINAL_DATE","");
				m.put("ONLINE","N");
				m.put("SENSITIVE","N");
				m.put("INHERIT","N");
				m.put("ACTIVE",db.getString("ACTIVE"));
				m.put("STATUS",db.getString("STATUS"));
				
				m.put("PAYEE_ID",db.getString("PAYEE_ID"));
				m.put("PAYMENT_ID",db.getString("PAYMENT_ID"));
				ma.add(m);
			}
			r.setList(ma);
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		
		
		return r;
	}

	
	
	public static boolean validateQty(ArrayList<HashMap<String, String>> al,String ref,String processId){
		boolean r = false;
		
		
		Timekeeper k = new Timekeeper();
		String pyy = k.yyyy();
		k.addYear(1);
		String cyy = k.yyyy();
		int size = al.size();
		System.out.println("size is "+size);
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<size; i++) {
			HashMap<String, String> map = al.get(i);
			if(map.containsKey("ACCOUNT NUMBER")){
				String account_number = map.get("ACCOUNT NUMBER");
				account_number = account_number.trim();
				if(Operator.hasValue(account_number)){
					sb.append(Operator.toInt(account_number));
					sb.append(",");
				}
			}
		}
		String accounts = sb.toString();
		Logger.info(accounts.length()+"::::+accounts.length()-1::::"+accounts+"$$$$$");
		accounts = accounts.substring(0, accounts.length()-1);
		
		/*if(Operator.hasValue(accounts)){
			accounts= accounts.trim();
			if(accounts.endsWith(",")){
				accounts = accounts.substring(0, accounts.length()-1);
			}
		}*/
		
		
		Sage db = new Sage();
		
		sb = new StringBuilder();
		sb.append(" select RP.ID as ACCOUNT,count(*) as QTY,'PREVIOUS' AS TYPE_YEAR,251 as TYPE from activity A  ");
		sb.append(" join REF_PROJECT_PARKING RP on A.PROJECT_ID = RP.PROJECT_ID ");
		sb.append(" where lkup_act_status_id in (6,536)  ");
		sb.append(" AND RP.ID in (").append(accounts).append(") AND EXP_DATE= '").append(pyy).append("-09-30' and LKUP_ACT_TYPE_ID  in (251,278,279) group BY RP.ID ");
		sb.append(" union  ");
		sb.append(" select RP.ID as ACCOUNT, count(*) as QTY,'PREVIOUS' AS TYPE_YEAR,252 as TYPE  from activity A  ");
		sb.append(" join REF_PROJECT_PARKING RP on A.PROJECT_ID = RP.PROJECT_ID ");
		sb.append(" where lkup_act_status_id in (6,536)  ");
		sb.append(" AND RP.ID in (").append(accounts).append(") AND EXP_DATE= '").append(pyy).append("-09-30' and LKUP_ACT_TYPE_ID= 252 group BY RP.ID ");
		sb.append(" union ");
		sb.append(" select RP.ID as ACCOUNT, count(*) as QTY,'CURRENT' AS TYPE_YEAR,251 as TYPE  from activity A  ");
		sb.append(" join REF_PROJECT_PARKING RP on A.PROJECT_ID = RP.PROJECT_ID ");
		sb.append(" where lkup_act_status_id in (6,536)  ");
		sb.append(" AND RP.ID in (").append(accounts).append(")  AND EXP_DATE= '").append(cyy).append("-09-30' and LKUP_ACT_TYPE_ID in (251,278,279) group BY RP.ID ");
		sb.append(" union  ");
		sb.append(" select RP.ID as ACCOUNT, count(*) as QTY,'CURRENT' AS TYPE_YEAR,252 as TYPE  from activity A  ");
		sb.append(" join REF_PROJECT_PARKING RP on A.PROJECT_ID = RP.PROJECT_ID ");
		sb.append(" where lkup_act_status_id in (6,536)  ");
		sb.append(" AND RP.ID in (").append(accounts).append(")  AND EXP_DATE= '").append(cyy).append("-09-30' and LKUP_ACT_TYPE_ID= 252 group BY RP.ID ");
		
		db.query(sb.toString());
		ArrayList<String> a = new ArrayList<String>();
		while(db.next()){
			String typeyear = db.getString("TYPE_YEAR");
			int type = db.getInt("TYPE");
			int qty = db.getInt("QTY");
			int account = db.getInt("ACCOUNT");
			
			sb = new StringBuilder();
			sb.append(" UPDATE LOCKBOX_UPLOADS set UPDATED_DATE=CURRENT_TIMESTAMP ");
			if(Operator.equalsIgnoreCase(typeyear, "PREVIOUS") && type==251){
				  sb.append(" ,OVERNIGHT_PREV_QTY=").append(qty);
			}
			if(Operator.equalsIgnoreCase(typeyear, "PREVIOUS") && type==252){
				  sb.append(" ,DAYTIME_PREV_QTY=").append(qty);
			}
			
			if(Operator.equalsIgnoreCase(typeyear, "CURRENT") && type==251){
				  sb.append(" ,OVERNIGHT_CURR_QTY=").append(qty);
			}
			if(Operator.equalsIgnoreCase(typeyear, "CURRENT") && type==252){
				  sb.append(" ,DAYTIME_CURR_QTY=").append(qty);
			}
		
			sb.append(" WHERE ACCOUNT_NUMBER=").append(account);
			sb.append(" AND  BATCH_NUMBER= '").append(ref).append("'");
			sb.append(" AND  PROCESS_ID='").append(processId).append("'");
			a.add(sb.toString());
			
		
		}
		
		for (int i=0; i<a.size(); i++) {
			r = db.update(a.get(i));
		}
		
		
		db.clear();
		
		return r;
        	
		
	}
	

	public static boolean deleteLockbox(RequestVO vo,Token u){
		boolean r = false;
		Sage db = new Sage();
		try{
		
		
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE LOCKBOX_UPLOADS SET UPDATED_DATE=CURRENT_TIMESTAMP, ACTIVE='N', STATUS='N' ");
		sb.append(" WHERE ID =").append(vo.getId());
		
		r = db.update(sb.toString());
		
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		return r;
	}
	
	public static boolean manualprocess(RequestVO vo,Token u){
		boolean r = false;
		Sage db = new Sage();
		try{
		
		
			StringBuilder sb = new StringBuilder();
			sb.append(" select * from LOCKBOX_UPLOADS L JOIN PAYMENT P on L.TRANSACTION_NUMBER = P.LOCKBOX_TRANSACTION_NO where ID = ").append(vo.getId());
			db.query(sb.toString());
			
			boolean process = false;
			if(db.next()){
				process = true;
			}
			
			if(process){
				sb = new StringBuilder();
				sb.append(" UPDATE LOCKBOX_UPLOADS SET UPDATED_DATE=CURRENT_TIMESTAMP, ACTIVE='N', STATUS='N' ");
				sb.append(" WHERE ID =").append(vo.getId());
				
				r = db.update(sb.toString());
			}
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		return r;
	}

	


}




